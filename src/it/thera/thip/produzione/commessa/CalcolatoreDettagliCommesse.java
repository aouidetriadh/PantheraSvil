package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.formula.ExpressionTypes;
import com.thera.thermfw.formula.Formula;
import com.thera.thermfw.formula.FunctionVariable;
import com.thera.thermfw.formula.VariablesCollection;
import com.thera.thermfw.persist.CopyException;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.generale.AmbienteCosti;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
import it.thera.thip.datiTecnici.costi.ComponenteCostoTM;
import it.thera.thip.datiTecnici.costi.FormulaCosti;
import it.thera.thip.datiTecnici.costi.LinkCompSchema;
import it.thera.thip.datiTecnici.costi.VariabiliCosti;

public class CalcolatoreDettagliCommesse
{
   public final static BigDecimal ZERO = new BigDecimal(0);
   protected AmbienteCosti ambienteCosti = null;
   protected HashMap<String, ComponenteCosto> componentiCostoAzienda = null;

   public AmbienteCosti getAmbienteCosti()
   {
      if(ambienteCosti == null)
         ambienteCosti = AmbienteCosti.getCurrentAmbienteCosti();
      return ambienteCosti;
   }

   public ComponenteCosto getComponenteCostoAzienda(String idComponenteCosto)
   {
      if(componentiCostoAzienda == null)
      {
         componentiCostoAzienda = new HashMap<String, ComponenteCosto>();
         try 
         {
            String where = ComponenteCostoTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "' ";
            List componenti = ComponenteCosto.retrieveList(where, "", false);
            Iterator iterCmp = componenti.iterator();
            while(iterCmp.hasNext())
            {
               ComponenteCosto cmp = (ComponenteCosto)iterCmp.next();
               componentiCostoAzienda.put(cmp.getIdComponenteCosto(), cmp);
            }
         }
         catch (Exception ex) 
         {
            ex.printStackTrace(Trace.excStream); 
         }
      }
      return componentiCostoAzienda.get(idComponenteCosto);
   }

   public void applicaFormuleTotali(List listaDettagli, Articolo articolo)
   {
      List compDaValorizz = new ArrayList();
      List compValorizz = new ArrayList();

      Iterator iteDet = listaDettagli.iterator();
      while (iteDet.hasNext()) 
      {
         CostiCommessaDettaglio dettaglio = (CostiCommessaDettaglio)iteDet.next();
         char provenienza = dettaglio.getComponenteCosto().getProvenienza();
         if ((provenienza == ComponenteCosto.CALCOLATA_FORMULA || provenienza == ComponenteCosto.SOLO_TOTALE) && isUtilizzoFormula(articolo, dettaglio)) 
            compDaValorizz.add(dettaglio);
         else 
            compValorizz.add(dettaglio);
      }
      boolean found = false;

      while (compDaValorizz.size() != 0)
      {
         found = false;
         Iterator valorizIte = compDaValorizz.iterator();
         while (valorizIte.hasNext() && !found) 
         {
            CostiCommessaDettaglio consuntivoDet = (CostiCommessaDettaglio)valorizIte.next();           
            try
            {
               consuntivoDet.aggiornaGruppi(this, listaDettagli, compDaValorizz);
            }
            catch(Exception ex)
            {
               ex.printStackTrace(Trace.excStream);
            }

            compDaValorizz.remove(consuntivoDet);
            compValorizz.add(consuntivoDet);
            found = true;
         }
      }
   }

   protected boolean isUtilizzoFormula(Articolo articolo, CostiCommessaDettaglio dettaglio)   {
      Iterator iter = articolo.getArticoloDatiProduz().getClasseMerclg().getSchemaCosto().getComponenti().iterator();
      while (iter.hasNext()) {
         LinkCompSchema linkCompSchema = (LinkCompSchema)iter.next();
         if (linkCompSchema.getIdComponenteCosto().equals(dettaglio.getIdComponCosto()) && linkCompSchema.isUtilizzoFormula()) {
            return true;
         }
      }
      return false;
   }


   public void aggiorna(CostiCommessaDettaglio dettaglio, List listaDettglio, List compDaValorizz, CostiCommessaDetGruppo gruppo) throws SQLException {
      FormulaCosti formulaCosti = dettaglio.getComponenteCosto().getFormula();

      AmbienteCosti ac = getAmbienteCosti();

      Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(dettaglio.getComponenteCosto(), ac.getIdAmbiente()));
      formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, false, true, gruppo));
      if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), dettaglio)) {
         BigDecimal costoLivelloCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
         formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, false, false, gruppo));
         BigDecimal costoTotaleCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
         gruppo.setCostoTotale(costoTotaleCalcolato);
         gruppo.setCostoLivello(costoLivelloCalcolato);
         gruppo.setCostoLivelloInf(ZERO);
         if(dettaglio.getComponenteCosto().isGestioneATempo()) {
            formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, true, true, gruppo));
            BigDecimal tempoLivelloCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
            formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, true, false, gruppo));
            BigDecimal tempoTotaleCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
            gruppo.setTempoTotale(tempoLivelloCalcolato);
            gruppo.setTempoLivello(costoLivelloCalcolato);
            gruppo.setTempoLivelloInf(ZERO);
         }
      }
   }

   protected VariablesCollection buildVariablesDetCmm(List listaDettagli, boolean atempo, boolean livello, CostiCommessaDetGruppo gruppo) {
      VariablesCollection variableCollection = new VariablesCollection() {
         public Object getVariableValue(String name) {
            Object obj = values.get(name);
            if (obj != null && ((FunctionVariable)obj).getValue() != null) {
               return ((FunctionVariable)obj).getValue();
            }
            return new BigDecimal("0");
         }
      };
      try {
         Iterator iter = listaDettagli.iterator();
         FunctionVariable variables;
         ComponenteCosto compCosto;
         CostiCommessaDettaglio curConsDet;
         while (iter.hasNext()) {
            curConsDet = (CostiCommessaDettaglio)iter.next();
            compCosto = curConsDet.getComponenteCosto();
            variables = new FunctionVariable(compCosto.getIdComponenteCosto(), compCosto.getDescrizione().getDescrizioneRidotta(), null, ExpressionTypes.NUMBER);
            variables.setType(ExpressionTypes.NUMBER);
            CostiCommessaDetGruppo curConsDetGruppo = curConsDet.getGruppo(gruppo.tipoGruppo);
            if(livello) {
               if(atempo)
                  variables.setValue(curConsDetGruppo.getTempoLivello());
               else
                  variables.setValue(curConsDetGruppo.getCostoLivello());
            }
            else {
               if(atempo)
                  variables.setValue(curConsDetGruppo.getTempoTotale());
               else
                  variables.setValue(curConsDetGruppo.getCostoTotale());
            }
            variableCollection.addVariable(variables);
         }
      }
      catch (Exception ex) {
         ex.printStackTrace(Trace.excStream);
      }
      return variableCollection;
   }


   protected Formula cloneFormula(Formula formulaDaUtilizzOrig) {
      Formula formulaDaUtilizz = new Formula();
      try {
         formulaDaUtilizz.setEqual(formulaDaUtilizzOrig);
      }
      catch (CopyException e) {
         e.printStackTrace(Trace.excStream);
      }
      return formulaDaUtilizz;
   }  

   public boolean isFormulaCalcolabileDouble(FormulaCosti formulaCosti, Formula formulaDaUtilizz, List compDaValorizz, List usedComp, CostiCommessaDettaglio dettaglio) throws SQLException {
      boolean formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), dettaglio);
      if (!formCalc && !formulaCosti.equals(dettaglio.getComponenteCosto().getFormula())) {
         formulaDaUtilizz = dettaglio.getComponenteCosto().getFormula().getFormula();
         formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), dettaglio);
      }
      return formCalc;
   }       

   public static boolean isFormulaCalcolabile(List compDaValorizz, List usedComp, CostiCommessaDettaglio dettaglio) {
      if (usedComp == null || usedComp.size() == 0) {
         return true;
      } 
      else {
         Iterator itComponenteUsed = usedComp.iterator();
         ComponenteCosto compCosto = null;
         while (itComponenteUsed.hasNext()) {
            compCosto = (ComponenteCosto) itComponenteUsed.next();
            Iterator itDettaglio = compDaValorizz.iterator();
            CostiCommessaDettaglio dett = null;
            while (itDettaglio.hasNext()) {
               dett = (CostiCommessaDettaglio) itDettaglio.next();
               if (compCosto.getIdComponenteCosto().equalsIgnoreCase(dett.getComponenteCosto().getIdComponenteCosto())) {
                  if (dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE) {
                     return false;
                  }
                  if (!compCosto.getIdComponenteCosto().equals(dettaglio.getIdComponCosto())) {
                     return false;
                  }
               }
            }
         }
         return true;
      }
   }  

   public List compUsedInFormula(com.thera.thermfw.formula.Formula formula) {
      List compUsedList = new ArrayList();
      Set variabili = formula.getUsedVariables();
      Iterator variabiliIter = variabili.iterator();
      while (variabiliIter.hasNext()) 
      {
         String variabile = variabiliIter.next().toString();
         ComponenteCosto cmp = getComponenteCostoAzienda(componenteId(variabile));
         if(cmp != null)
            compUsedList.add(cmp);
      }
      return compUsedList;
   }

   private String componenteId(String idVariabile) {
      if (idVariabile.endsWith(VariabiliCosti.LIVELLO)) {
         return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO.length());
      }
      if (idVariabile.endsWith(VariabiliCosti.LIVELLO_INFERIORE)) {
         return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO_INFERIORE.length());
      }
      return idVariabile;
   }

}

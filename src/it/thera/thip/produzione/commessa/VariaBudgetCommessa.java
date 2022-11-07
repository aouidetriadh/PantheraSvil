package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.cbs.WfSpecNode;
import com.thera.thermfw.cbs.WfStatus;
import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.common.Numerator;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.ErrorCodes;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.PersistentObjectCursor;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.commessa.CommessaTM;


import it.thera.thip.datiTecnici.PersDatiTecnici;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
import it.thera.thip.datiTecnici.costi.LinkCompSchema;

/**
 * VariaBudgetCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/11/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34795   29/11/2021    RA		    Prima struttura
 */

public class VariaBudgetCommessa extends VariaBudgetCommessaPO {

   public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.VariaBudgetCommessa";

   public final static BigDecimal ZERO = new BigDecimal(0);

   //Stato avanzamento variazione budget
   public final static char PROVVISORIO = '1';
   public final static char DEFINITIVO = '2';

   protected char iOldStatoAvanzamento;
   protected WfStatus iOldWfStatus = null;

   protected boolean iAzioneDaSave = false;

   protected boolean iTotali = false;
   protected boolean iDettagliCommessa = true;
   protected boolean iDettagliSottoCommesse = true;
   protected boolean iComponentiPropri = true;
   protected boolean iSoloComponentiValorizzate = false;

   protected List<VariaBudgetCommessa> variaBudgetLivelliInferiori = null;

   protected CalcolatoreDettagliCommesse calcolatoreDettagli = null;

   public String getIdDescUMPrmMag() {
      if((getCommessa() != null) && (getCommessa().getUmPrmMag() != null)) {
         return getCommessa().getUmPrmMag().getIdDescrizione();
      }
      return null;
   }

   public boolean isTotali() {
      return iTotali;
   }

   public void setTotali(boolean totali) {
      iTotali = totali;
   }

   public boolean isDettagliCommessa() {
      return iDettagliCommessa;
   }

   public void setDettagliCommessa(boolean dettagliCommessa) {
      iDettagliCommessa = dettagliCommessa;
   }

   public boolean isDettagliSottoCommesse() {
      return iDettagliSottoCommesse;
   }

   public void setDettagliSottoCommesse(boolean dettagliSottoCommesse) {
      iDettagliSottoCommesse = dettagliSottoCommesse;
   }

   public boolean isComponentiPropri() {
      return iComponentiPropri;
   }

   public void setComponentiPropri(boolean componentiPropri) {
      iComponentiPropri = componentiPropri;
   }

   public boolean isSoloComponentiValorizzate() {
      return iSoloComponentiValorizzate;
   }

   public void setSoloComponentiValorizzate(boolean soloComponentiValorizzate) {
      iSoloComponentiValorizzate = soloComponentiValorizzate;
   }

   public List<VariaBudgetCommessa> getVariaBudgetLivelliInferiori()
   {
      return variaBudgetLivelliInferiori;
   }

   public CalcolatoreDettagliCommesse getCalcolatoreDettagli()
   {
      if(calcolatoreDettagli == null)
         calcolatoreDettagli = (CalcolatoreDettagliCommesse)Factory.createObject(CalcolatoreDettagliCommesse.class);
      return calcolatoreDettagli;
   }

   public void setCalcolatoreDettagli(CalcolatoreDettagliCommesse calcolatoreDettagli)
   {
      this.calcolatoreDettagli = calcolatoreDettagli;
   }

   public boolean initializeOwnedObjects(boolean result) {
      iOldStatoAvanzamento = getStatoAvanzamento();
      iOldWfStatus = getWfStatus();
      initializeAttDaScreenData();
      result = super.initializeOwnedObjects(result);
      return result;
   }

   public void initializeAttDaScreenData() {
      ScreenData sd = ScreenData.getDefaultScreenData("VariaBudgetCommessa");
      if (sd != null && sd.getAttValue("Totali") != null && sd.getAttValue("Totali").equals("Y"))
         iTotali = true;
      if (sd != null && sd.getAttValue("DettagliCommessa") != null && sd.getAttValue("DettagliCommessa").equals("N"))
         iDettagliCommessa = false;
      if (sd != null && sd.getAttValue("DettagliSottoCommesse") != null && sd.getAttValue("DettagliSottoCommesse").equals("N"))
         iDettagliSottoCommesse = false;
      if (sd != null && sd.getAttValue("ComponentiPropri") != null && sd.getAttValue("ComponentiPropri").equals("N"))
         iComponentiPropri = false;
      if (sd != null && sd.getAttValue("SoloComponentiValorizzate") != null && sd.getAttValue("SoloComponentiValorizzate").equals("Y"))
         iSoloComponentiValorizzate = true;
   }

   public ErrorMessage rendiVariazioneBudgetDefinitivo() {
      if(!rendiDefinitivo())
         return new ErrorMessage("THIP_TN809");//Problema nel passaggio allo stato Definitivo		
      return null;
   }

   public ErrorMessage rendiVariazioneBudgetProvvisorio() {
      if(!rendiProvvisorio()) {
         return new ErrorMessage("THIP_TN810");//Problema nel passaggio allo stato Provvisorio
      }		
      return null;
   }

   public boolean rendiDefinitivo() {
      Integer idNuovoBudget = getIdBudgetNuovo();
      if(getStatoAvanzamento() == DEFINITIVO && idNuovoBudget != null)
         return false;
      try {
         setStatoAvanzamento(DEFINITIVO);
         boolean ok = true;
         ok = creaNuovoBudgetCommessa();

         if(ok) {
            if(!iAzioneDaSave) {
               int rc = save();
               if(rc < ErrorCodes.NO_ROWS_UPDATED) {
                  return false;
               }
            }				
         }
      } 
      catch (Exception e) {
         e.printStackTrace(Trace.excStream);
      }
      return true;
   }

   public boolean creaNuovoBudgetCommessa()
   {
      try
      {
         caricaAlberoVariaBudget();

         BudgetCommessa oldBudget = getBudgetCommessa();
         oldBudget.calcolaAlberoBudget();
         oldBudget.rendiDefinitivo();

         BudgetCommessa nBudget = (BudgetCommessa)Factory.createObject(BudgetCommessa.class);
         nBudget.setIdAzienda(getIdAzienda());
         nBudget.setIdBudget(new Integer(Numerator.getNextInt(BudgetCommessa.ID_PROGR_NUM_ID)));
         nBudget.setIdCommessa(getIdCommessa());
         nBudget.setIdArticolo(getIdArticolo());
         nBudget.setIdVersione(getIdVersione());
         nBudget.setIdConfigurazione(getIdConfigurazione());
         nBudget.setIdStabilimento(getIdStabilimento());
         nBudget.setQuantitaPrm(getQuantitaPrm());
         nBudget.setIdUMPrmMag(getIdUMPrmMag());
         nBudget.setIdCommessaApp(getIdCommessaApp());
         nBudget.setIdCommessaPrm(getIdCommessaPrm());
         nBudget.setDataRiferimento(getDataRiferimento());
         nBudget.setStatoAvanzamento(PROVVISORIO);
         nBudget.setIdComponenteTotali(getBudgetCommessa().getIdComponenteTotali());
         nBudget.calcolaAlberoBudget();
         nBudget.setDescrizione(getDescrizione());
         nBudget.save();
         caricaNuovoBudgetCommessa(nBudget, getBudgetCommessa(), this);
         nBudget.save();

         setIdBudgetNuovo(nBudget.getIdBudget());
      }
      catch(Exception ex)
      {
         ex.printStackTrace(Trace.excStream);
         return false;
      }
      return true;   
   }

   public void caricaNuovoBudgetCommessa(BudgetCommessa nuovoBudget, BudgetCommessa oldBudget, VariaBudgetCommessa variaBudget) {
      Iterator iterDet = nuovoBudget.getBudgetCommessaDet().iterator();
      while(iterDet.hasNext())
      {
         BudgetCommessaDet nuovoBudgetDet = (BudgetCommessaDet)iterDet.next();
         if(nuovoBudgetDet.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI)
         {
            VariaBudgetCommessaDet variaDet = getDettaglio(variaBudget, nuovoBudgetDet.getIdComponCosto(), false);
            BudgetCommessaDet oldBudgetDet = oldBudget.getDettaglio(oldBudget, nuovoBudgetDet.getIdComponCosto(), false);
            BigDecimal costoLivello = ZERO;
            BigDecimal tempoLivello = ZERO;
            if(oldBudgetDet != null)
            {
               costoLivello = costoLivello.add(oldBudgetDet.getCostoLivello());
               tempoLivello = tempoLivello.add(oldBudgetDet.getTempoLivello());
            }
            if(variaDet != null)
            {
               costoLivello = costoLivello.add(variaDet.getCostoLivello());
               tempoLivello = tempoLivello.add(variaDet.getTempoLivello());
            }
            nuovoBudgetDet.setCostoLivello(costoLivello);
            nuovoBudgetDet.setTempoLivello(tempoLivello);
         }
      }

      for(VariaBudgetCommessa variaInf : variaBudget.variaBudgetLivelliInferiori)
      {
         BudgetCommessa oldBudInf = null;
         BudgetCommessa newBudInf = null;

         Iterator iterOldBud = oldBudget.budgetLivelliInferiori.iterator();
         while(iterOldBud.hasNext() && oldBudInf == null)
         {
            BudgetCommessa curBud = (BudgetCommessa)iterOldBud.next();
            if(curBud.getIdCommessa().equals(variaInf.getIdCommessa()))
               oldBudInf = curBud;
         }

         Iterator iterNewBud = nuovoBudget.budgetLivelliInferiori.iterator();
         while(iterNewBud.hasNext() && newBudInf == null)
         {
            BudgetCommessa curBud = (BudgetCommessa)iterNewBud.next();
            if(curBud.getIdCommessa().equals(variaInf.getIdCommessa()))
               newBudInf = curBud;
         }

         if(newBudInf != null && oldBudInf != null)
            caricaNuovoBudgetCommessa(newBudInf, oldBudInf, variaInf);
      }
   }

   public boolean rendiProvvisorio() {
      if(getStatoAvanzamento() == PROVVISORIO && getIdBudgetNuovo() == null)
         return false;

      try {	
         setStatoAvanzamento(PROVVISORIO);
         int rcDelete = ErrorCodes.OK; 
         BudgetCommessa nuovoBudget = getBudgetCommessaNuovo();
         if(nuovoBudget != null)
            rcDelete =  nuovoBudget.delete();
         BudgetCommessa oldBudget = getBudgetCommessa();
         if(oldBudget != null)
         {
            oldBudget.calcolaAlberoBudget();
            oldBudget.rendiProvvisorio();
         }

         if(rcDelete >= ErrorCodes.NO_ROWS_UPDATED) {
            setIdBudgetNuovo(null);
            setStatoAvanzamento(PROVVISORIO);
            if(!iAzioneDaSave) {
               int rc = save();
               if(rc < ErrorCodes.NO_ROWS_UPDATED) {
                  return false;
               }
            }
         }
      } 
      catch (SQLException e) {
         e.printStackTrace(Trace.excStream);
         return false;
      }
      return true;
   }	

   public Vector checkAll(BaseComponentsCollection components) {
      Vector errors = new Vector();
      components.runAllChecks(errors);
      return errors;
   }

   public ErrorMessage checkIdCommessa() {
      if(getCommessa() != null && getCommessa().getCommessaAppartenenza() != null )
         return new ErrorMessage("THIP_TN723");//Non è possibile inserire una sotto commessa
      if(getBudgetCommessa() != null && getBudgetCommessa().getBudgetCommessaDet() != null && !getBudgetCommessa().getBudgetCommessaDet().isEmpty()) {
         for(int i=0; i < getBudgetCommessa().getBudgetCommessaDet().size(); i++) {
            BudgetCommessaDet budgetDet = (BudgetCommessaDet) getBudgetCommessa().getBudgetCommessaDet().get(i);
            if(!isCompenenteValido(budgetDet.getIdComponCosto()))
               return new ErrorMessage("THIP_TN808");//“Non c’è corrispondenza tra le componenti di costo del budget e quelle attualmente contenute nello schema collegato all’articolo.”
         }
      }
      return null;
   }

   public ErrorMessage checkIdConsuntivo() {
      if(getConsuntivoCommessa() != null && getBudgetCommessa() != null && 
            getConsuntivoCommessa().getDataRiferimento().compareTo(getBudgetCommessa().getDataRiferimento()) < 0) {
         return new ErrorMessage("THIP_TN811");//Consuntivo commessa selezionata errata. Data riferimento del consuntivo selezionata è inferiore al data riferimento del budget.
      }
      return null;
   }

   public ErrorMessage checkIdBudgetCommessa() {
      if(!isOnDB() && getBudgetCommessa() != null) {
         List<BudgetCommessa> budgets = getBudgetCommessa().getBudgets();
         for (int i = 0; i < budgets.size(); i++) {
            BudgetCommessa budget = (BudgetCommessa)budgets.get(i);
            Iterator<BudgetCommessaDet> iteDet = budget.getBudgetCommessaDet().iterator();
            while (iteDet.hasNext()) {
               BudgetCommessaDet dettaglio = (BudgetCommessaDet)iteDet.next();
               if(!isCompenenteValido(dettaglio.getIdComponCosto())) {
                  return new ErrorMessage("THIP_TN808");//Non c’è corrispondenza tra le componenti di costo del budget e quelle attualmente contenute nello schema collegato all’articolo.
               }
            }
         }			
      }
      return null;
   }

   public ErrorMessage checkIdBudgetNuovo() {
      if(iOldStatoAvanzamento == PROVVISORIO && getStatoAvanzamento() == DEFINITIVO && getIdBudgetNuovo() == null) {
         return new ErrorMessage("THIP_TN806", true);//Sarà creato un nuovo budget definitivo con i dati di questa variazione. Si desidera procedere?
      }
      if(iOldStatoAvanzamento == DEFINITIVO && getStatoAvanzamento() == PROVVISORIO && !esisteBudgetSuccessiva()) {
         return new ErrorMessage("THIP_TN807", true);//Il budget creato da questa variazione sarà eliminato. Si desidera procedere?
      }
      return null;
   }

   public ErrorMessage checkDelete() {
	  boolean esisteVariazioneSuccessiva = esisteVariazioneSuccessiva();
	  if (esisteVariazioneSuccessiva)
			return new ErrorMessage("THIP_TN911");//"La variazione di budget non eliminabile perchè non è l'ultima"
		else if (getStatoAvanzamento() == DEFINITIVO)
			return new ErrorMessage("THIP_TN912");//"La variazione di budget non eliminabile perchè definitiva"
		return null;
   }
	
   public boolean esisteVariazioneSuccessiva() {
      try {
         String where = VariaBudgetCommessaTM.ID_AZIENDA + " ='" + getIdAzienda() + "' AND " + 
               VariaBudgetCommessaTM.ID_COMMESSA + " = '" + getIdCommessa() + "' AND " + 
               VariaBudgetCommessaTM.DATA_RIFERENTO + " > '" + getDataRiferimento() + "'";
         PersistentObjectCursor cursor = new PersistentObjectCursor(VariaBudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
         if(cursor.hasNext()) 
            return true;
      }
      catch(SQLException e) {
         e.printStackTrace(Trace.excStream);
      }
      return false;
   }

   public boolean esisteBudgetSuccessiva() {
      if(getIdBudgetNuovo() != null) {			
         try {
            String budgetNuovoKey = KeyHelper.buildObjectKey(new Object[] {getIdAzienda(), getIdBudgetNuovo(), getIdCommessa()});
            BudgetCommessa budgetNuovo = BudgetCommessa.elementWithKey(budgetNuovoKey, PersistentObject.NO_LOCK);
            if(budgetNuovo != null)
            {
               String where = BudgetCommessaTM.ID_AZIENDA + " ='" + getIdAzienda() + "' AND " + 
                     BudgetCommessaTM.ID_COMMESSA + " = '" + getIdCommessa() + "' AND " + 
                     BudgetCommessaTM.DATA_RIFERENTO + " > '" + budgetNuovo.getDataRiferimento() + "'";
               PersistentObjectCursor cursor = new PersistentObjectCursor(VariaBudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
               if (cursor.hasNext()) {
                  return true;
               }
            }
         }
         catch(SQLException e) {
            e.printStackTrace(Trace.excStream);
         }
      }
      return false;
   }

   public int delete() throws SQLException {
      int rc = 0;
      if(getCommessa() != null && getCommessa().getCommessaAppartenenza() == null) {
         if(getStatoAvanzamento() == DEFINITIVO && getBudgetCommessaNuovo() != null) {				
            int rc1 = getBudgetCommessaNuovo().delete();
            if(rc1 < ErrorCodes.NO_ROWS_UPDATED) {
               rc = rc1;
            }
         }
         if(rc >= 0) {
            String where = VariaBudgetCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' AND " + 
                  VariaBudgetCommessaTM.ID_COMMESSA + " <> '" + getIdCommessa() + "' AND " + 
                  VariaBudgetCommessaTM.ID_BUDGET + " = " + getIdBudget();
            PersistentObjectCursor cursor = new PersistentObjectCursor(VariaBudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
            while (cursor.hasNext()) {
               VariaBudgetCommessa variazione = (VariaBudgetCommessa)cursor.next();
               int rc1 = variazione.delete();
               if(rc1 < 0) {
                  rc = rc1;
                  break;
               }
               else
                  rc += rc1;							  
            }				
         }			
      }
      if(rc >= 0)
         rc += super.delete();

      return rc;
   }

   public int save() throws SQLException {
      beforeSave();
      calcolaAlberoVariaBudget();
      salvaAlberoVariaBudget();
      return 1;
   }

   public void caricaAlberoVariaBudget()
   {
      caricaLivelliInferiori();
      for(VariaBudgetCommessa variaBudLivelloInferiore : variaBudgetLivelliInferiori)
         variaBudLivelloInferiore.caricaAlberoVariaBudget();
   }

   public void calcolaAlberoVariaBudget()
   {
      calcolaLivello();
      caricaLivelliInferiori();
      calcolaLivelliInferiori();
      calcolaTotali();
   }

   public void calcolaLivello()
   {
      if(isOnDB() == false)
      {
         creaLivelloDaArticolo();
      }
      else
      {
         Iterator iter = getVariaBudgetCommessaDet().iterator();
         while(iter.hasNext())
         {
            VariaBudgetCommessaDet curDet = (VariaBudgetCommessaDet)iter.next();
            curDet.setCostoTotale(ZERO);
         }
      }
      applicaFormuleTotali();
   }

   public void creaLivelloDaArticolo()
   {
      Articolo articolo = this.getCommessaPrm().getArticolo();
      List<LinkCompSchema> listComponenti = new ArrayList<LinkCompSchema>();
      if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getSchemaCosto() != null)
         listComponenti.addAll(articolo.getClasseMerclg().getSchemaCosto().getComponenti());

      for(int idx = 0; idx < listComponenti.size(); idx++) 
      {
         LinkCompSchema linkCompSchema = listComponenti.get(idx);
         VariaBudgetCommessaDet dettaglioBudget = getDettaglio(this, linkCompSchema.getIdComponenteCosto());
         dettaglioBudget.setCostoTotale(ZERO);
      }
   }

   public void applicaFormuleTotali()
   {
      getCalcolatoreDettagli().applicaFormuleTotali(getVariaBudgetCommessaDet(), getArticolo());
   }

   public void caricaLivelliInferiori()
   {
      if(variaBudgetLivelliInferiori == null)
      {
         variaBudgetLivelliInferiori = new ArrayList<VariaBudgetCommessa>();
         String where = CommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + CommessaTM.R_COMMESSA_APP + "='" + getIdCommessa() + "'";
         List commesse;
         try
         {
            commesse = Commessa.retrieveList(Commessa.class, where, "", false);
            Iterator iter = commesse.iterator();
            while(iter.hasNext())
            {
               Commessa commessa = (Commessa)iter.next();
               VariaBudgetCommessa variaBudget = caricaVariaBudgetCommessa(commessa);
               if(variaBudget != null)
                  variaBudgetLivelliInferiori.add(variaBudget);
            }
         }
         catch(Exception e)
         {
            e.printStackTrace(Trace.excStream);
         }
      }
   }

   public void calcolaLivelliInferiori()
   {
      for(VariaBudgetCommessa budLivelloInferiore : variaBudgetLivelliInferiori)
      {
         budLivelloInferiore.calcolaAlberoVariaBudget();

         Iterator iter = getVariaBudgetCommessaDet().iterator();
         while(iter.hasNext())
         {
            VariaBudgetCommessaDet dettaglio = (VariaBudgetCommessaDet)iter.next();
            VariaBudgetCommessaDet detLivelloInferiore = getDettaglio(budLivelloInferiore, dettaglio.getIdComponCosto());
            if(detLivelloInferiore != null)
               dettaglio.sommaLivelloInferiore(detLivelloInferiore);
         }
      }
   }

   public void calcolaTotali()
   {
      Iterator iter = getVariaBudgetCommessaDet().iterator();
      while(iter.hasNext())
      {
         VariaBudgetCommessaDet dettaglio = (VariaBudgetCommessaDet)iter.next();
         dettaglio.calcolaTotali();
      }
      valorizzaCostiVariaBudget();
   }

   public VariaBudgetCommessaDet getDettaglio(VariaBudgetCommessa variabudget, String idCmpCosto)
   {
      return getDettaglio(variabudget, idCmpCosto, true);
   }

   public VariaBudgetCommessaDet getDettaglio(VariaBudgetCommessa variabudget, String idCmpCosto, boolean creaDettaglio)
   {
      VariaBudgetCommessaDet dettaglio = null;
      Iterator iter = variabudget.getVariaBudgetCommessaDet().iterator();
      while(iter.hasNext() && dettaglio == null)
      {
         VariaBudgetCommessaDet curDet = (VariaBudgetCommessaDet)iter.next();
         if(curDet.getIdComponCosto().equals(idCmpCosto))
            dettaglio = curDet;
      }
      if(dettaglio == null && creaDettaglio)
      {
         dettaglio = creaVariaBudgetCommessaDet(variabudget, variabudget.getCommessa(), idCmpCosto);
         variabudget.getVariaBudgetCommessaDet().add(dettaglio);
      }
      return dettaglio;
   }

   public VariaBudgetCommessaDet creaVariaBudgetCommessaDet(VariaBudgetCommessa variaBudget, Commessa commessa, String idComponCosto) {
      VariaBudgetCommessaDet variaBudgetCommessaDet = (VariaBudgetCommessaDet) Factory.createObject(VariaBudgetCommessaDet.class);
      variaBudgetCommessaDet.setIdAzienda(variaBudget.getIdAzienda());
      variaBudgetCommessaDet.setIdBudget(variaBudget.getIdBudget());
      variaBudgetCommessaDet.setIdCommessa(variaBudget.getIdCommessa());
      variaBudgetCommessaDet.setIdComponCosto(idComponCosto);
      return variaBudgetCommessaDet;
   }

   public void salvaAlberoVariaBudget()
   {
      for(VariaBudgetCommessa budLivelloInferiore : variaBudgetLivelliInferiori)
         budLivelloInferiore.salvaAlberoVariaBudget();
      try
      {
         super.save();
      }
      catch(SQLException e)
      {
         e.printStackTrace(Trace.excStream);
      }
   }

   public void beforeSave() throws SQLException {
      iAzioneDaSave = true;
      if(isOnDB() && iOldStatoAvanzamento == PROVVISORIO && getStatoAvanzamento() == DEFINITIVO) {
         rendiDefinitivo();
      }
      if(isOnDB() && iOldStatoAvanzamento == DEFINITIVO && getStatoAvanzamento() == PROVVISORIO) {
         rendiProvvisorio();
      }
      if(getWfStatus() != null && getWfStatus().getWfSpecific() != null) {
         WfSpecNode wfSpecNode = null;
         if(getStatoAvanzamento() == PROVVISORIO) {
            wfSpecNode = getWfStatus().getWfSpecific().getInitialNode();
         }
         else {
            wfSpecNode = getWfSpecNodeByKey("Definitivo");				
         }
         getWfStatus().setWfClassId(wfSpecNode.getWfClassId());
         getWfStatus().setWfId(wfSpecNode.getWfId());
         getWfStatus().setWfSpecificNodeId(wfSpecNode.getWfNodeId());
         getWfStatus().setCurrentNode(wfSpecNode);
      }
      iAzioneDaSave = false;
   }

   protected WfSpecNode getWfSpecNodeByKey(String nodeKey)
   {
      WfSpecNode node = null;
      if(nodeKey != null && getWfStatus() != null && getWfStatus().getWfSpecific() != null)
      {
         Iterator nodeIter = getWfStatus().getWfSpecific().getWfSpecNodeColl().iterator();
         while(node == null && nodeIter.hasNext())
         {
            WfSpecNode curNode = (WfSpecNode)nodeIter.next();
            if(curNode.getWfNodeId().equals(nodeKey))
               node = curNode;
         }
      }
      return node;
   }

   public boolean isCompenenteValido(String idCompenenteCosto) {
      Articolo articolo = getCommessaPrm().getArticolo();
      List<LinkCompSchema> listComponenti = new ArrayList<LinkCompSchema>();
      if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getSchemaCosto() != null)
         listComponenti.addAll(articolo.getClasseMerclg().getSchemaCosto().getComponenti());
      for(int idx = 0; idx < listComponenti.size(); idx++) {
         LinkCompSchema linkCompSchema = listComponenti.get(idx);
         if(linkCompSchema.getIdComponenteCosto().equals(idCompenenteCosto)) {
            return true;
         }
      }
      return false;
   }

   public VariaBudgetCommessa caricaVariaBudgetCommessa(Commessa commessa) {
      VariaBudgetCommessa currentVariaBudget = null;
      try {
         String currentVariaBudgetKey = KeyHelper.buildObjectKey(new Object[] {getIdAzienda(), getIdBudget(), commessa.getIdCommessa()});
         currentVariaBudget = VariaBudgetCommessa.elementWithKey(currentVariaBudgetKey, PersistentObject.NO_LOCK);
         if(currentVariaBudget == null) {
            currentVariaBudget = creaNuovoVariaBudgetCommessa(commessa);
         }

         currentVariaBudget.setTotali(isTotali());
         currentVariaBudget.setComponentiPropri(isComponentiPropri());
         currentVariaBudget.setDettagliCommessa(isDettagliCommessa());
         currentVariaBudget.setDettagliSottoCommesse(isDettagliSottoCommesse());
         currentVariaBudget.setSoloComponentiValorizzate(isSoloComponentiValorizzate());
         currentVariaBudget.setStatoAvanzamento(getStatoAvanzamento());
         currentVariaBudget.setDataRiferimento(getDataRiferimento());
         currentVariaBudget.setIdConsuntivo(getIdConsuntivo());
         currentVariaBudget.calcolatoreDettagli = calcolatoreDettagli;
      } 
      catch (SQLException e1) {
         e1.printStackTrace(Trace.excStream);
      }

      return currentVariaBudget;

   }

   public VariaBudgetCommessa creaNuovoVariaBudgetCommessa(Commessa commessa) {
      VariaBudgetCommessa nuovoVariaBudget = (VariaBudgetCommessa)Factory.createObject(VariaBudgetCommessa.class);		
      try {
         nuovoVariaBudget.setEqual(this);
         nuovoVariaBudget.setIdAzienda(getIdAzienda());
         if(getBudgetCommessa() != null)
            nuovoVariaBudget.setBudgetCommessa(getBudgetCommessa());
         if(getConsuntivoCommessa() != null)
            nuovoVariaBudget.setConsuntivoCommessa(getConsuntivoCommessa());
         nuovoVariaBudget.setIdCommessa(commessa.getIdCommessa());
         nuovoVariaBudget.setIdCommessaApp(commessa.getIdCommessaAppartenenza());
         nuovoVariaBudget.setIdCommessaPrm(commessa.getIdCommessaPrincipale());
         nuovoVariaBudget.setStatoAvanzamento(getStatoAvanzamento());
         nuovoVariaBudget.setDataRiferimento(getDataRiferimento());
         nuovoVariaBudget.setIdStabilimento(commessa.getIdStabilimento());
         nuovoVariaBudget.setIdArticolo(commessa.getIdArticolo());			
         nuovoVariaBudget.setIdVersione(commessa.getIdVersione());
         nuovoVariaBudget.setIdConfigurazione(commessa.getIdConfigurazione());
         nuovoVariaBudget.setIdUMPrmMag(commessa.getIdUmPrmMag());
         nuovoVariaBudget.setQuantitaPrm(commessa.getQtaUmPrm());
         nuovoVariaBudget.setDescrizione(getDescrizione());
         nuovoVariaBudget.setDataRiferimento(getDataRiferimento());
         nuovoVariaBudget.getVariaBudgetCommessaDet().clear();
      } 
      catch (Exception e) {
         e.printStackTrace(Trace.excStream);
      } 		

      return nuovoVariaBudget;
   }

   public JSONArray getColumnsDescriptors() throws JSONException {
      JSONArray columns = new JSONArray();
      columns.put(getColumnDescriptors());
      columns.put(getColumnDescriptorsBudget());
      if(getConsuntivoCommessa() != null)
         columns.put(getColumnDescriptorsConsuntivo());
      columns.put(getColumnDescriptorsVariazione());
      columns.put(getColumnDescriptorsNote());
      columns.put(getColumnDescriptorsNuovoBudget());
      return columns;
   }

   public JSONObject getColumnDescriptorsNuovoBudget() throws JSONException {
      JSONObject column = new JSONObject();
      column.put("title", ResourceLoader.getString(RES_FILE, "NuovoBudget")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false)).put("editable", false);
      column.put("colModel", getNuovoBudgetColumnModel());
      return column;		
   }

   public JSONArray getNuovoBudgetColumnModel() throws JSONException {	
      JSONArray columns = new JSONArray();
      String title = "NuovoBudget";
      if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
         columns =  getColumnModelATempo(title, false);
      }
      else {
         JSONObject column = new JSONObject();
         String dataIndx = title.substring(0, 3);
         column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", dataIndx+"Val").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold"));
         columns.put(column);
      }
      return columns;
   }

   public JSONObject getColumnDescriptorsTotale() throws JSONException {		
      JSONObject totaleColumnDescriptors = new JSONObject();
      String title = "NBTotale";
      if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
         totaleColumnDescriptors.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", true));
         totaleColumnDescriptors.put("colModel", getColumnModelATempo("NBTotale", false));
      }
      else {
         String dataIndx = title.substring(0, 3);
         totaleColumnDescriptors.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("collapsible", new JSONObject().put("last", true).put("on", true));
      }
      return totaleColumnDescriptors;	
   }

   public JSONObject getColumnDescriptorsNote() throws JSONException {
      JSONObject column = new JSONObject();
      column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold"));
      column.put("colModel", getNoteColumnModel());
      return column;		
   }

   public JSONArray getNoteColumnModel() throws JSONException {	
      JSONArray columns = new JSONArray();
      JSONObject column = new JSONObject();
      String title = "Note";
      String dataIndx = title.substring(0, 3);
      column.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", dataIndx).put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold"));

      if(getStatoAvanzamento() == PROVVISORIO )
         column.put("editable", "columnValoreEditable");
      else
         column.put("editable", false);
      columns.put(column);
      return columns;
   }

   public JSONObject getColumnDescriptors() throws JSONException {
      JSONObject column = new JSONObject();
      column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", "CompCosto").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold")).put("editable", false);
      return column;		
   }

   public JSONObject getColumnDescriptorsBudget() throws JSONException {
      JSONObject column = new JSONObject();
      String title = "Budget";
      if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
         column.put("title", ResourceLoader.getString(RES_FILE, "Budget")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false));
         column.put("colModel", getColumnModelATempo("Budget", false));				
      }
      else {
         String dataIndx = title.substring(0, 3);
         column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI).put("editable", false);			
      }	
      return column;
   }

   public JSONObject getColumnDescriptorsVariazione() throws JSONException {
      JSONObject column = new JSONObject();
      String title = "Variazione";
      if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
         column.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false)).put("editable", true);
         column.put("colModel", getColumnModelATempo("Variazione", true));				
      }
      else {			
         String dataIndx = title.substring(0, 3);
         column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI);
         if(getStatoAvanzamento() == PROVVISORIO)
            column.put("editable", "columnValoreEditable");
         else
            column.put("editable", false);
      }	
      return column;
   }

   public JSONObject getColumnDescriptorsConsuntivo() throws JSONException {
      JSONObject consuntivoColumnDescriptors = new JSONObject();
      consuntivoColumnDescriptors.put("title", ResourceLoader.getString(RES_FILE, "Consuntivo")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", true)).put("editable", false);
      consuntivoColumnDescriptors.put("colModel", getConsuntivoColumnModel());
      return consuntivoColumnDescriptors;		
   }

   public JSONArray getConsuntivoColumnModel() throws JSONException {		
      JSONArray columns = new JSONArray();
      ConsuntivoCommessa consuntivo = getConsuntivoCommessa();
      if(consuntivo == null)
         consuntivo = (ConsuntivoCommessa)Factory.createObject(ConsuntivoCommessa.class);
      if(consuntivo.isConsolidato())
         columns.put(consuntivo.getColumnDescriptors("Consolidato").put("editable", false));
      if(consuntivo.isEstrazioneRichieste())
         columns.put(consuntivo.getColumnDescriptors("Richiesto").put("editable", false));
      if(consuntivo.isEstrazioneOrdini())
         columns.put(consuntivo.getColumnDescriptors("Ordinato").put("editable", false));
      columns.put(consuntivo.getColumnDescriptors("Sostenuto").put("editable", false));
      columns.put(consuntivo.getColumnDescriptors("Totale").put("editable", false));
      return columns;
   }

   public JSONArray getColumnModelATempo(String title, boolean editable) throws JSONException {
      JSONArray columns = new JSONArray();
      String dataIndx = title.substring(0, 3);
      JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"HH").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_ORE);
      if(getStatoAvanzamento() == PROVVISORIO && editable)
         columnOre.put("editable", "columnOreEditable");
      else
         columnOre.put("editable", false);

      columns.put(columnOre);
      JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI);
      if(title.equals("Totale")) {
         columnValore.put("style", new JSONObject().put("font-weight", "bold")).put("editable", false);
      }
      else {
         if(getStatoAvanzamento() == PROVVISORIO && editable)
            columnValore.put("editable", "columnValoreEditable");
         else
            columnValore.put("editable", false);
      }
      columns.put(columnValore);
      return columns;
   }

   public BudgetCommessaDet cercaBudgetCommessaDet(Integer idBudget, String idCommessa, String idComp) {
      BudgetCommessaDet ret = getBudgetCommessa().getDettaglio(getBudgetCommessa(), idComp, false);
      return ret;
   }

   public ConsuntivoCommessaDet cercaConsuntivoCommessaDet(Integer idConsuntivo, String idCommessa, String idComp) {
      ConsuntivoCommessaDet ret = getConsuntivoCommessa().getDettaglio(getConsuntivoCommessa(), idComp, false);
      return ret;
   }

   public VariaBudgetCommessaDet cercaVariaBudgetCommessaDet(Integer idBudget, String idCommessa, String idComp) {
      return getDettaglio(this, idComp, false);
   }

   protected String getValoreString(BigDecimal valore, boolean vuoto)
   {
      boolean valorizzato = valore != null && valore.compareTo(ZERO) != 0;
      if(valorizzato)
         return "" + valore;
      else
      {
         if(!vuoto)
            return "" + ZERO;
         else
            return "";
      }
   }

   protected String getOreString(BigDecimal valore, boolean aTempo,  boolean vuoto)
   {
      if(aTempo)
         return getValoreString(valore, vuoto);
      else
         return "";
   }

   protected String getNuovoValoreString(BigDecimal valore, BigDecimal variaValore, boolean vuoto)
   {
      BigDecimal nuovoValore = sum(valore, variaValore);
      return getValoreString(nuovoValore, vuoto);
   }

   protected String getNuovoOreString(BigDecimal valore, BigDecimal variaValore, boolean aTempo, boolean vuoto)
   {
      BigDecimal nuovoValore = sum(valore, variaValore);
      return getOreString(nuovoValore, aTempo, vuoto);
   }

   public JSONArray getDataModelInMemoria() 
   {
      JSONArray data = new JSONArray();
      Commessa commessaPrm = getCommessa();
      if(commessaPrm != null) 
      {
         try 
         {
            writeJSONDatiInMemoria(data, "1", null, this, commessaPrm, null);
         } 
         catch (JSONException e) {
            e.printStackTrace(Trace.excStream);
         }
      }     
      return data;
   }

   @SuppressWarnings("unchecked")
   protected void writeJSONDatiInMemoria(JSONArray data, String id, String idParent, VariaBudgetCommessa variazione, Commessa commessaPrm, Commessa commessaApp) throws JSONException {
      String parent = "";
      Commessa commessa = commessaPrm;
      if(commessaApp != null) {
         commessa = commessaApp;
      }

      if(idParent != null) {        
         parent = idParent;
      }

      ConsuntivoCommessaDet consuntivoDetTotale = null;
      String idCompTotale = getBudgetCommessa().getIdComponenteTotali();
      BudgetCommessaDet budgetDetTotale = cercaBudgetCommessaDet(getIdBudget(), commessa.getIdCommessa(), idCompTotale);
      if(getConsuntivoCommessa() != null) {
         consuntivoDetTotale = cercaConsuntivoCommessaDet(getIdConsuntivo(), commessa.getIdCommessa(), idCompTotale);
      }

      VariaBudgetCommessaDet dettaglioTotali = cercaVariaBudgetCommessaDet(getIdBudget(), commessa.getIdCommessa(), idCompTotale);
      if(dettaglioTotali == null) {
         dettaglioTotali = (VariaBudgetCommessaDet)Factory.createObject(VariaBudgetCommessaDet.class);
         dettaglioTotali.setIdAzienda(variazione.getIdAzienda());
         dettaglioTotali.setIdCommessa(commessa.getIdCommessa());
         dettaglioTotali.setIdComponCosto(idCompTotale);
      }  

      int idxGrup = 1;
      writeCommessaGruppo(data, String.valueOf(id), parent, commessa.getDescrizione().getDescrizione(), commessa.getIdCommessa() + " - " + commessa.getDescrizione().getDescrizione(), commessaPrm.hasCompenenteATempo(), dettaglioTotali, budgetDetTotale, consuntivoDetTotale);

      if(isTotali()) {
         String idGruppTot = id + idxGrup;
         writeTotale(data, idGruppTot, String.valueOf(id), dettaglioTotali, commessaPrm.hasCompenenteATempo(), false, budgetDetTotale, consuntivoDetTotale);
         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
            writeTotaleDettagli(data, String.valueOf(id), idGruppTot, variazione.getVariaBudgetCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate());
      }

      if(isComponentiPropri()) {
         idxGrup++;
         String idGruppCmp = id + idxGrup;
         writeComponentePropri(data, idGruppCmp, String.valueOf(id), dettaglioTotali, commessaPrm.hasCompenenteATempo(), false, budgetDetTotale, consuntivoDetTotale);
         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
            writeComponentePropriDettagli(data, String.valueOf(id), idGruppCmp, variazione.getVariaBudgetCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate());
      }

      Iterator budIter = variaBudgetLivelliInferiori.iterator();
      while(budIter.hasNext())
      {
         VariaBudgetCommessa currentVariaBudget = (VariaBudgetCommessa)budIter.next();
         idxGrup++;
         String idSottoCmm = id + idxGrup;
         currentVariaBudget.writeJSONDatiInMemoria(data, idSottoCmm, id, currentVariaBudget, commessaPrm, currentVariaBudget.getCommessa());
      }
   }

   public void writeCommessaGruppo(JSONArray data, String id, String parentId, String descCommessa,
         String compCosto, boolean isATempo, VariaBudgetCommessaDet dettaglioTotali, BudgetCommessaDet budgetDet, ConsuntivoCommessaDet consDet) throws JSONException {

      String stileRiga = "RigaNormale";
      boolean cmpATempo = dettaglioTotali.getComponenteCosto().isGestioneATempo();

      JSONObject json = new JSONObject();
      json.put("id", id);
      json.put("parentId", parentId);
      json.put("Commessa", descCommessa);
      json.put("CompCosto", compCosto);
      json.put("IdCmpCosto", dettaglioTotali.getIdComponCosto());
      json.put("VariaBudgetDetKey", getKey());

      json.put("BudVal", getValoreString(budgetDet.getCostoTotale(), true));
      json.put("VarVal", getValoreString(dettaglioTotali.getCostoTotale(), true)); 
      json.put("NuoVal", getNuovoValoreString(budgetDet.getCostoTotale(), dettaglioTotali.getCostoTotale(), true));
      json.put("Not", dettaglioTotali.getNote());

      if(isATempo) {
         json.put("BudHH", getOreString(budgetDet.getTempoTotale(), cmpATempo, true));
         json.put("VarHH", getOreString(dettaglioTotali.getTempoTotale(), cmpATempo, true));
         json.put("NuoHH", getNuovoOreString(budgetDet.getTempoTotale(), dettaglioTotali.getTempoTotale(), cmpATempo, true));
      }

      if(consDet != null) {
         if (consDet.getConsuntivoCommessa().isConsolidato()) 
            json.put("ConVal",  getValoreString(consDet.getConsolidato().getCostoTotale(), false));
         json.put("RicVal", getValoreString(consDet.getRichiesto().getCostoTotale(), false));
         json.put("OrdVal", getValoreString(consDet.getOrdinato().getCostoTotale(), false));
         json.put("SosVal", getValoreString(consDet.getEffettuato().getCostoTotale(), false));
         json.put("TotVal", getValoreString(consDet.getTotale().getCostoTotale(), false));
         if (isATempo) {
            if (consDet.getConsuntivoCommessa().isConsolidato()) {
               json.put("ConHH", getOreString(consDet.getConsolidato().getTempoTotale(), cmpATempo, false));
            }
            json.put("RicHH", getOreString(consDet.getRichiesto().getTempoTotale(), cmpATempo, false)); 
            json.put("OrdHH", getOreString(consDet.getOrdinato().getTempoTotale(), cmpATempo, false)); 
            json.put("SosHH", getOreString(consDet.getEffettuato().getTempoTotale(), cmpATempo, false)); 
            json.put("TotHH", getOreString(consDet.getTotale().getTempoTotale(), cmpATempo, false));
         }
      }

      if(id.equals("1"))
         stileRiga = "RigaCommessa";
      else
         stileRiga = "RigaSottoCommessa";

      json.put("pq_rowcls", stileRiga);

      JSONObject stiliCelle = new JSONObject();
      if(dettaglioTotali.getCostoTotale() != null)
      {
         if(dettaglioTotali.getCostoTotale().compareTo(ZERO) < 0)
            stiliCelle.put("NuoVal", "CellaDeltaPos");
         else if(dettaglioTotali.getCostoTotale().compareTo(ZERO) > 0)
            stiliCelle.put("NuoVal", "CellaDeltaNeg");
      }
      if(dettaglioTotali.getTempoTotale() != null)
      {
         if(dettaglioTotali.getTempoTotale().compareTo(ZERO) < 0)
            stiliCelle.put("NuoHH", "CellaDeltaPos");
         else if(dettaglioTotali.getTempoTotale().compareTo(ZERO) > 0)
            stiliCelle.put("NuoHH", "CellaDeltaNeg");
      }
      json.put("pq_cellcls", stiliCelle);

      data.put(json);
   }

   public void writeTotale(JSONArray data, String id, String idParent, VariaBudgetCommessaDet dettaglio, boolean isATempo, boolean isDettaglio, BudgetCommessaDet budgetDet, ConsuntivoCommessaDet consDet) {
      try {
         String stileRiga = "RigaNormale";
         boolean cmpTotali = false;
         boolean totali = false;
         boolean cmpATempo = dettaglio.getComponenteCosto().isGestioneATempo();

         JSONObject json = new JSONObject();			
         json.put("id", id);
         json.put("parentId", idParent);
         if(isDettaglio) {
            json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
            if(dettaglio.getComponenteCosto() != null && dettaglio.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
               cmpTotali = true;
            totali = true;
         }
         else {
            stileRiga = "RigaTotali";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "Totali"));
         }
         json.put("IdCmpCosto", dettaglio.getIdComponCosto());
         json.put("VariaBudgetDetKey", dettaglio.getKey());		

         json.put("BudVal", getValoreString(budgetDet.getCostoTotale(), true));
         json.put("VarVal", getValoreString(dettaglio.getCostoTotale(), true)); 
         json.put("NuoVal", getNuovoValoreString(budgetDet.getCostoTotale(), dettaglio.getCostoTotale(), true));
         json.put("Not", getNote());

         if(isATempo) {
            json.put("BudHH", getOreString(budgetDet.getTempoTotale(), cmpATempo, true));
            json.put("VarHH", getOreString(dettaglio.getTempoTotale(), cmpATempo, true));
            json.put("NuoHH", getNuovoOreString(budgetDet.getTempoTotale(), dettaglio.getTempoTotale(), cmpATempo, true));
         }

         if(consDet != null) {
            if (consDet.getConsuntivoCommessa().isConsolidato()) 
               json.put("ConVal",  getValoreString(consDet.getConsolidato().getCostoTotale(), false));
            json.put("RicVal", getValoreString(consDet.getRichiesto().getCostoTotale(), false));
            json.put("OrdVal", getValoreString(consDet.getOrdinato().getCostoTotale(), false));
            json.put("SosVal", getValoreString(consDet.getEffettuato().getCostoTotale(), false));
            json.put("TotVal", getValoreString(consDet.getTotale().getCostoTotale(), false));
            if (isATempo) {
               if (consDet.getConsuntivoCommessa().isConsolidato()) {
                  json.put("ConHH", getOreString(consDet.getConsolidato().getTempoTotale(), cmpATempo, false));
               }
               json.put("RicHH", getOreString(consDet.getRichiesto().getTempoTotale(), cmpATempo, false)); 
               json.put("OrdHH", getOreString(consDet.getOrdinato().getTempoTotale(), cmpATempo, false)); 
               json.put("SosHH", getOreString(consDet.getEffettuato().getTempoTotale(), cmpATempo, false)); 
               json.put("TotHH", getOreString(consDet.getTotale().getTempoTotale(), cmpATempo, false));
            }
         }

         json.put("pq_rowcls", stileRiga);

         JSONObject stiliCelle = new JSONObject();
         if(totali)
         {
            stiliCelle.put("TotVal", "CellaTotali");
            stiliCelle.put("TotHH", "CellaTotali");
         }
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");

         if(dettaglio.getCostoTotale() != null)
         {
            if(dettaglio.getCostoTotale().compareTo(ZERO) < 0)
               stiliCelle.put("NuoVal", "CellaDeltaPos");
            else if(dettaglio.getCostoTotale().compareTo(ZERO) > 0)
               stiliCelle.put("NuoVal", "CellaDeltaNeg");
         }
         if(dettaglio.getTempoTotale() != null)
         {
            if(dettaglio.getTempoTotale().compareTo(ZERO) < 0)
               stiliCelle.put("NuoHH", "CellaDeltaPos");
            else if(dettaglio.getTempoTotale().compareTo(ZERO) > 0)
               stiliCelle.put("NuoHH", "CellaDeltaNeg");
         }
         json.put("pq_cellcls", stiliCelle);

         data.put(json);	      
      }
      catch (JSONException e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   public void writeTotaleDettagli(JSONArray data, String id, String idParent, List<VariaBudgetCommessaDet> varsCommessaDet, Commessa commessa, boolean isATempo, boolean soloComponentiValorizzate) {
      int idxDet = 1;
      for(int i = 0; i < varsCommessaDet.size(); i++) {
         VariaBudgetCommessaDet dettaglio = varsCommessaDet.get(i);
         ConsuntivoCommessaDet consuntivoDet = null;
         BudgetCommessaDet budgetDet = cercaBudgetCommessaDet(getIdBudget(), dettaglio.getIdCommessa(), dettaglio.getIdComponCosto());
         if(getConsuntivoCommessa() != null) {
            consuntivoDet = cercaConsuntivoCommessaDet(getIdConsuntivo(), dettaglio.getIdCommessa(), dettaglio.getIdComponCosto());
         }
         if((!soloComponentiValorizzate) || (soloComponentiValorizzate && budgetDet != null && budgetDet.getCostoTotale() != null && budgetDet.getCostoTotale().compareTo(new BigDecimal(0)) > 0)) {
            String idDet = idParent + idxDet;
            writeTotale(data, idDet, idParent, dettaglio, isATempo, true, budgetDet, consuntivoDet);
            idxDet ++;
         }
      }
   }

   public void writeComponentePropriDettagli(JSONArray data, String id, String idParent, List<VariaBudgetCommessaDet> varsCommessaDet, Commessa commessa, boolean isATempo, boolean soloComponentiValorizzate) {
      int idxDet = 1;
      for(int i = 0; i < varsCommessaDet.size(); i++) {
         VariaBudgetCommessaDet dettaglio = varsCommessaDet.get(i);
         ConsuntivoCommessaDet consuntivoDet = null;
         BudgetCommessaDet budgetDet = cercaBudgetCommessaDet(getIdBudget(), dettaglio.getIdCommessa(), dettaglio.getIdComponCosto());
         if(getConsuntivoCommessa() != null) {
            consuntivoDet = cercaConsuntivoCommessaDet(getIdConsuntivo(), dettaglio.getIdCommessa(), dettaglio.getIdComponCosto());
         }
         if(budgetDet != null) {
             boolean budgetValorizzato = budgetDet.getCostoLivello() != null && budgetDet.getCostoLivello().compareTo(new BigDecimal(0)) != 0;
             boolean variaValorizzato = dettaglio.getCostoLivello() != null && dettaglio.getCostoLivello().compareTo(new BigDecimal(0)) != 0;

             if((!soloComponentiValorizzate) || (soloComponentiValorizzate && (budgetValorizzato || variaValorizzato))) {
                String idDet = idParent + idxDet;
                writeComponentePropri(data, idDet, idParent, dettaglio, isATempo, true, budgetDet, consuntivoDet);
                idxDet ++;
             }
         }
      }
   }

   public void writeComponentePropri(JSONArray data, String id, String idParent, VariaBudgetCommessaDet dettaglio, boolean isATempo, boolean isDettaglio, BudgetCommessaDet budgetDet, ConsuntivoCommessaDet consDet) {
      try {
         boolean valoreEditabile = false;
         boolean oraEditabile = false;
         boolean cmpTotali = false;
         boolean totali = false;

         String stileRiga = "RigaNormale";
         boolean cmpATempo = dettaglio.getComponenteCosto().isGestioneATempo();

         JSONObject json = new JSONObject();			
         json.put("id", id);
         json.put("parentId", idParent);

         if(isDettaglio) {
            json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
            if (dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI	&& dettaglio.getVariaBudgetCommessa().getStatoAvanzamento() == BudgetCommessa.PROVVISORIO) {
               json.put("CmpElem", "Y");
               valoreEditabile = true;
               if (dettaglio.getComponenteCosto().isGestioneATempo())
               {
                  json.put("CmpATempo", "Y");
                  oraEditabile = true;
               }
               else 
                  json.put("CmpATempo", "N");					
            }

            if(dettaglio.getComponenteCosto() != null && dettaglio.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
               cmpTotali = true;
            totali = true;

            json.put("VariaBudgetDetKey", getKey());
         }
         else {
            stileRiga = "RigaTotali";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "ComponentiProprie"));
         }
         json.put("IdCmpCosto", dettaglio.getIdComponCosto());
         json.put("VariaBudgetDetKey", dettaglio.getKey());		

         json.put("BudVal", getValoreString(budgetDet.getCostoLivello(), true));
         json.put("VarVal", getValoreString(dettaglio.getCostoLivello(), true)); 
         json.put("NuoVal", getNuovoValoreString(budgetDet.getCostoLivello(), dettaglio.getCostoLivello(), true));
         json.put("Not", dettaglio.getNote());

         if(isATempo) {
            json.put("BudHH", getOreString(budgetDet.getTempoLivello(), cmpATempo, true));
            json.put("VarHH", getOreString(dettaglio.getTempoLivello(), cmpATempo, true));
            json.put("NuoHH", getNuovoOreString(budgetDet.getTempoLivello(), dettaglio.getTempoLivello(), cmpATempo, true));
         }

         if(consDet != null) {
            if (consDet.getConsuntivoCommessa().isConsolidato()) 
               json.put("ConVal",  getValoreString(consDet.getConsolidato().getCostoLivello(), false));
            json.put("RicVal", getValoreString(consDet.getRichiesto().getCostoLivello(), false));
            json.put("OrdVal", getValoreString(consDet.getOrdinato().getCostoLivello(), false));
            json.put("SosVal", getValoreString(consDet.getEffettuato().getCostoLivello(), false));
            json.put("TotVal", getValoreString(consDet.getTotale().getCostoLivello(), false));
            if (isATempo) {
               if (consDet.getConsuntivoCommessa().isConsolidato()) {
                  json.put("ConHH", getOreString(consDet.getConsolidato().getTempoLivello(), cmpATempo, false));
               }
               json.put("RicHH", getOreString(consDet.getRichiesto().getTempoLivello(), cmpATempo, false)); 
               json.put("OrdHH", getOreString(consDet.getOrdinato().getTempoLivello(), cmpATempo, false)); 
               json.put("SosHH", getOreString(consDet.getEffettuato().getTempoLivello(), cmpATempo, false)); 
               json.put("TotHH", getOreString(consDet.getTotale().getTempoLivello(), cmpATempo, false));
            }
         }

         json.put("pq_rowcls", stileRiga);

         JSONObject stiliCelle = new JSONObject();
         if(valoreEditabile || oraEditabile)
            stiliCelle.put("Not", "CellaEditabile");
         if(valoreEditabile)
            stiliCelle.put("VarVal", "CellaEditabile");
         if(oraEditabile)
            stiliCelle.put("VarHH", "CellaEditabile");
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");
         if(totali)
         {
            stiliCelle.put("TotVal", "CellaTotali");
            stiliCelle.put("TotHH", "CellaTotali");
         }

         if(dettaglio.getCostoLivello() != null)
         {
            if(dettaglio.getCostoLivello().compareTo(ZERO) < 0)
               stiliCelle.put("NuoVal", "CellaDeltaPos");
            else if(dettaglio.getCostoLivello().compareTo(ZERO) > 0)
               stiliCelle.put("NuoVal", "CellaDeltaNeg");
         }
         if(dettaglio.getTempoLivello() != null)
         {
            if(dettaglio.getTempoLivello().compareTo(ZERO) < 0)
               stiliCelle.put("NuoHH", "CellaDeltaPos");
            else if(dettaglio.getTempoLivello().compareTo(ZERO) > 0)
               stiliCelle.put("NuoHH", "CellaDeltaNeg");
         }
         json.put("pq_cellcls", stiliCelle);	         

         data.put(json);
      }
      catch (JSONException e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   public boolean aggiornaVariaBudgetDet(String variaBudgetDetKey, String oldValueStr, String newValueStr, boolean updatedOre, boolean updateNote) {
      caricaAlberoVariaBudget();
      Object[] keyParts = KeyHelper.unpackObjectKey(variaBudgetDetKey);
      String idComm = (String)keyParts[2];
      String idCompCosto = (String)keyParts[3];
      boolean ok = aggiornaDettaglio(idComm, idCompCosto, newValueStr, updatedOre, updateNote);

      if(ok)
      {
         try
         {
            int rc = save();
            if(rc > 0)
               ConnectionManager.commit();
            else
               ConnectionManager.rollback();   
         }
         catch(Exception ex)
         {
            ex.printStackTrace(Trace.excStream);
         }

      }
      return true;
   }

   protected boolean aggiornaDettaglio(String idCommessa, String idCompCosto, String newValue, boolean updateOre, boolean updateNote)
   {
      if(idCommessa.equals(getIdCommessa()))
      {
         VariaBudgetCommessaDet dettaglio = getDettaglio(this,  idCompCosto);
         if(!updateNote)
            dettaglio.aggiornaValore(newValue, updateOre);
         else
            dettaglio.setNote(newValue);
         return true;
      }
      else
      {
         for(VariaBudgetCommessa curVariaBudget: variaBudgetLivelliInferiori)
         {
            boolean ret = curVariaBudget.aggiornaDettaglio(idCommessa, idCompCosto, newValue, updateOre, updateNote);
            if(ret)
               return true;
         }
      }
      return false;
   }

   public boolean valorizzaCostiVariaBudget() {
      PersDatiTecnici psnDatiTecnici = PersDatiTecnici.getCurrentPersDatiTecnici();
      String idRiferimento = psnDatiTecnici.getIdRiferimento();
      String idPrimo = psnDatiTecnici.getIdPrimo();
      String idIndustriale = psnDatiTecnici.getIdIndustriale();
      String idGenerale = psnDatiTecnici.getIdGenerale();
      List<VariaBudgetCommessaDet> variaBudgetsDetList = getVariaBudgetCommessaDet();
      for (int i = 0; i < variaBudgetsDetList.size(); i++) {
         VariaBudgetCommessaDet variaBudgetDet = (VariaBudgetCommessaDet) variaBudgetsDetList.get(i);			
         if (variaBudgetDet.getIdComponCosto().equals(idRiferimento))
            setCostoRiferimento(variaBudgetDet.getCostoTotale());
         if (variaBudgetDet.getIdComponCosto().equals(idPrimo))
            setCostoPrimo(variaBudgetDet.getCostoTotale());
         if (variaBudgetDet.getIdComponCosto().equals(idIndustriale))
            setCostoIndustriale(variaBudgetDet.getCostoTotale());
         if (variaBudgetDet.getIdComponCosto().equals(idGenerale))
            setCostoGenerale(variaBudgetDet.getCostoTotale());
      }
      return true;
   }

   public BigDecimal sum(BigDecimal val1, BigDecimal val2) {
      if((val1 == null) && (val2 == null))
         return null;
      if(val1 == null)
         return val2;
      if(val2 == null)
         return val1;
      return val1.add(val2);
   }

}

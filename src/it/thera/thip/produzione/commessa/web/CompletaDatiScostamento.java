package it.thera.thip.produzione.commessa.web;

import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.PersistentObjectCursor;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.BudgetCommessaTM;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
import it.thera.thip.produzione.commessa.ConsuntivoCommessaTM;
/**
 * CompletaDatiScostamento
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/03/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 35837   29/03/2022    RA       Prima struttura
 */
public class CompletaDatiScostamento extends BaseServlet {

   protected void processAction(ServletEnvironment se) throws Exception {
      String action = getStringParameter(se.getRequest(), "Action");
      PrintWriter out = se.getResponse().getWriter();
      String idCommessa = getStringParameter(se.getRequest(), "IdCommessa");
      String key = KeyHelper.buildObjectKey(new String [] {Azienda.getAziendaCorrente(), idCommessa});
      Commessa commessa = Commessa.elementWithKey(key, PersistentObject.NO_LOCK);
      if(commessa != null) {
         out.println("<script language='javascript1.2'>");
         DateType dtt = new DateType();
         BudgetCommessa budgetCommessa = null;
         ConsuntivoCommessa consuntivoCommessa = null;
         if(action.equals("COMPLETA_DATI")) {				
            out.println("  parent.fillCampo('IdCommessaBudget', '" + idCommessa + "');");
            out.println("  parent.fillCampo('IdCommessaConsuntivo', '" + idCommessa + "');");				
            budgetCommessa = commessa.getUltimoBudgetCommessa();				
            consuntivoCommessa = commessa.getUltimoConsuntivoCommessa();				
         }
         if(action.equals("INDIVIDUA")) {
            String dataRiferimentoStr = se.getRequest().getParameter("DataRiferimento");
            DateType dateType = new DateType();
            Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dateType.format(dataRiferimentoStr));
            budgetCommessa = cercaBudgetCommessa(dataRiferimento, commessa);
            consuntivoCommessa = cercaConsuntivoCommessa(dataRiferimento, commessa);
         }

         if(budgetCommessa != null) {
            out.println("  parent.fillCampo('IdBudget', '" + budgetCommessa.getIdBudget() + "');");
            if(budgetCommessa.getDescrizione() != null)
               out.println("  parent.fillCampo('BudgetCommessa$Descrizione', '" + budgetCommessa.getDescrizione() + "');");
            else
               out.println("  parent.fillCampo('BudgetCommessa$Descrizione', '');");
            out.println("  parent.fillCampo('DataBudget', '" + dtt.objectToString(budgetCommessa.getDataRiferimento()) + "');");
         }
         else {
            out.println("  parent.fillCampo('IdBudget', '');");
            out.println("  parent.fillCampo('BudgetCommessa$Descrizione', '');");
            out.println("  parent.fillCampo('DataBudget', '');");
         }

         if(consuntivoCommessa != null) {
            out.println("  parent.fillCampo('IdConsuntivo', '" + consuntivoCommessa.getIdConsuntivo() + "');");
            if(consuntivoCommessa.getDescrizione() != null)
               out.println("  parent.fillCampo('ConsuntivoCommessa$Descrizione', '" + consuntivoCommessa.getDescrizione() + "');");
            else
               out.println("  parent.fillCampo('ConsuntivoCommessa$Descrizione', '');");
            out.println("  parent.fillCampo('DataConsuntivo', '" + dtt.objectToString(consuntivoCommessa.getDataRiferimento()) + "');");
         }
         else {
            out.println("  parent.fillCampo('IdConsuntivo', '');");
            out.println("  parent.fillCampo('ConsuntivoCommessa$Descrizione', '');");
            out.println("  parent.fillCampo('DataConsuntivo', '');");
         }
         out.println("</script>");
      }
   }

   public BudgetCommessa cercaBudgetCommessa(Date dataRiferimento, Commessa commessa) {
      BudgetCommessa retBudget = null;
      String where = BudgetCommessaTM.ID_AZIENDA + "='" + commessa.getIdAzienda() + "' AND " + 
            BudgetCommessaTM.ID_COMMESSA + "='" + commessa.getIdCommessa() + "' AND " + 
            BudgetCommessaTM.DATA_RIFERENTO + " <= " + ConnectionManager.getCurrentDatabase().getLiteral(dataRiferimento) ;

      PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, "",
            PersistentObject.NO_LOCK);
      try {
         while (cursor.hasNext()) {
            BudgetCommessa budget = (BudgetCommessa) cursor.next();
            if(retBudget == null) {
               retBudget = budget;	 
            }
            else if(budget.getDataRiferimento().compareTo(retBudget.getDataRiferimento()) > 0)
               retBudget = budget;	  
         }
      } catch (SQLException e) {
         e.printStackTrace(Trace.excStream);
      }
      return retBudget;
   }

   public ConsuntivoCommessa cercaConsuntivoCommessa(Date dataRiferimento, Commessa commessa) {
      ConsuntivoCommessa retConsuntivo = null;
      String where = ConsuntivoCommessaTM.ID_AZIENDA + " ='" + commessa.getIdAzienda() + "' AND " + 
            ConsuntivoCommessaTM.R_COMMESSA + " ='" + commessa.getIdCommessa() + "' AND " +
            ConsuntivoCommessaTM.DATA_RIFERENTO + " <= " + ConnectionManager.getCurrentDatabase().getLiteral(dataRiferimento);

      PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
      try {
         while (cursor.hasNext()) {
            ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)cursor.next();
            if(retConsuntivo == null) {
               retConsuntivo = consuntivo;	 
            }
            else if(consuntivo.getDataRiferimento().compareTo(retConsuntivo.getDataRiferimento()) > 0)
               retConsuntivo = consuntivo;	  
         }
      } 
      catch (SQLException e) {
         e.printStackTrace(Trace.excStream);
      }
      return retConsuntivo;
   }

}

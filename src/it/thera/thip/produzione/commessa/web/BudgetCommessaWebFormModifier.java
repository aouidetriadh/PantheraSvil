package it.thera.thip.produzione.commessa.web;

import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.cs.web.ThipWebFormModifier;
import it.thera.thip.produzione.commessa.BudgetCommessa;
/**
 * BudgetCommessaWebFormModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 02/11/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34585   02/11/2021    RA       Prima struttura
 * 35382   07/03/2022    RA		  Integrazione budget su commessa
 */
public class BudgetCommessaWebFormModifier extends ThipWebFormModifier {
   public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";

   public void writeFormEndElements(JspWriter out) throws java.io.IOException {
      super.writeFormEndElements(out);
      out.println("<script language=\"javascript1.2\">");
      String mode = (String)getRequest().getParameter("thAction");
      //35382 inizio
      String opener = BaseServlet.getStringParameter(getRequest(), "Opener");
      if(opener != null && opener.equals("COMMESSA")) {
         out.println(" var openerCommessa = true;");
      }
      else {
         out.println(" var openerCommessa = false;");
      }
      //35382 fine
      if(mode.equalsIgnoreCase("NEW")) {
         out.println("  eval(\"document.forms[0].\" + idFromName[\"IdBudget\"]).value = '0';");
      }
      else {
         BudgetCommessa budget = (BudgetCommessa)getBODataCollector().getBo();
         if(budget != null) {
            String errCarica1 = ResourceLoader.getString(RES_FILE, "ErrCarica1");
            out.println("errCarica1 = \"" + errCarica1 + "\";");
            if(budget.getStatoAvanzamento() == BudgetCommessa.PROVVISORIO) {
               if(budget.isSingoloProovisorio()) {
                  out.println(" var isSingoloProovisorio = true;");
                  String confirm1 = ResourceLoader.getString(RES_FILE, "Confirm1");
                  String confirm2 = ResourceLoader.getString(RES_FILE, "Confirm2");
                  out.println("confirm1 = \"" + confirm1 + "\";");
                  out.println("confirm2 = \"" + confirm2 + "\";");
               }
               else {
                  out.println(" var isSingoloProovisorio = false;");
                  out.println(" enableSearchComponent(\"Preventivo\", false, false);");
                  out.println("  eval(\"document.forms[0].\" + idFromName[\"IdAnnoPreventivo\"]).disabled = true;");
                  out.println("  eval(\"document.forms[0].\" + idFromName[\"IdNumeroPreventivo\"]).disabled = true;");
                  out.println(" document.getElementById(\"CaricaBUT\").disabled = true;");
               }
            }
            else {
               BudgetCommessa ultimoBudgetDefinitivo = budget.getCommessa().getUltimoBudgetCommessaDefinitivo();
               if(!budget.isCollegatoAVariazioneBudget() && ultimoBudgetDefinitivo != null && ultimoBudgetDefinitivo.getKey().equals(budget.getKey()))
                  out.println("  eval(\"document.forms[0].\" + idFromName[\"StatoAvanzamento\"]).disabled = false;");
               else
                  out.println("  eval(\"document.forms[0].\" + idFromName[\"StatoAvanzamento\"]).disabled = true;");
               out.println("  eval(\"document.forms[0].\" + idFromName[\"Descrizione\"]).disabled = true;");
               out.println("  enableSearchComponent(\"ComponenteTotali\", false, false);");
               out.println("  eval(\"document.forms[0].\" + idFromName[\"IdComponenteTotali\"]).disabled = true;");
               out.println("  eval(\"document.forms[0].\" + idFromName[\"DataRiferimento\"]).disabled = true;");
               out.println("  document.getElementById(\"thCalButtonDataRiferimento\").disabled = true;");
               out.println(" enableSearchComponent(\"Preventivo\", false, false);");
               out.println("  eval(\"document.forms[0].\" + idFromName[\"IdAnnoPreventivo\"]).disabled = true;");
               out.println("  eval(\"document.forms[0].\" + idFromName[\"IdNumeroPreventivo\"]).disabled = true;");
               out.println(" document.getElementById(\"CaricaBUT\").disabled = true;");
               out.println("  eval(\"document.forms[0].\" + idFromName[\"DatiComuniEstesi.Stato\"]).disabled = true;");//35382
            }
         }

      }
      out.println("</script>");
   }

   //35382 inizio
   public void writeHeadElements(JspWriter out) throws java.io.IOException	{
      super.writeHeadElements(out);
      out.println("<script language=\"javascript1.2\">");	    
      if (webForm.getMode() == webForm.NEW) {	        
         BODataCollector budgetDC = (BODataCollector)getBODataCollector();
         budgetDC.loadAttValue();
         BudgetCommessa budget = (BudgetCommessa)budgetDC.getBo();
         String opener = BaseServlet.getStringParameter(getRequest(), "Opener");
         String objectKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
         if(opener != null && !opener.equals("") && objectKey != null && !objectKey.equals("")) {
            try {
               Commessa commessa = null;
               if(opener.equals("COMMESSA")) {
                  commessa = Commessa.elementWithKey(objectKey, PersistentObject.NO_LOCK);
               }

               if(commessa != null) {
                  budget.setCommessa(commessa);
                  budget.setStabilimento(commessa.getStabilimento());
                  budget.setCommessaPrm(commessa.getCommessaPrincipale());
                  budget.setCommessaAppart(commessa.getCommessaAppartenenza());
                  budget.setArticolo(commessa.getArticolo());
                  budget.setVersione(commessa.getArticoloVersione());
                  budget.setConfigurazione(commessa.getConfigurazione());
                  budget.setQuantitaPrm(commessa.getQtaUmPrm());
                  budget.setUMPrmMag(commessa.getUmPrmMag());
                  budget.setDataRiferimento(commessa.getDataEstrazioneStorici());
               }
            }
            catch(SQLException e) {
               e.printStackTrace(Trace.excStream);
            }

         }

         budget.setTotali(true);
         budget.setDettagliCommessa(true);
         budget.setDettagliSottoCommesse(true);
         budget.setComponentiPropri(true);
         budget.setSoloComponentiValorizzate(false);

         budgetDC.setBo(budget);          
      }
      out.println("</script>");
   }
   //35382 fine
}

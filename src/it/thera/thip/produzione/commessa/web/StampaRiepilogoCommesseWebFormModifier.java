package it.thera.thip.produzione.commessa.web;

import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.web.ServletEnvironment;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.cs.web.ThipWebFormModifier;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
/**
 * StampaRiepilogoCommesseWebFormModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/07/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 36252   18/07/2022    RA       Prima struttura
 */
public class StampaRiepilogoCommesseWebFormModifier extends ThipWebFormModifier {

   public StampaRiepilogoCommesseWebFormModifier() {
      super();
   }

   public void writeFormEndElements(JspWriter out) throws java.io.IOException {
      super.writeFormEndElements(out);
      out.println("<script language=\"javascript1.2\">");

      String dataRiferimento = getRequest().getParameter("DataRiferimento");
      Commessa commessa = getCommessa();		
      if(commessa != null) {
         out.println("document.getElementById('DataRiferimento').value = '" + dataRiferimento + "';");
         out.println("document.getElementById('UsaConsuntiviStoricizzati').checked = 'true';");
         out.println("document.getElementById('IdCommessa').value = '" + commessa.getIdCommessa() + "';");
         out.println("document.getElementById('Commessa$Descrizione$Descrizione').value = '" + commessa.getDescrizione().getDescrizione() + "';");
         ConsuntivoCommessa consuntivo = getConsuntivoCommessa();			
         if(consuntivo != null) {				
            out.println("document.getElementById('IdConsuntivo').value = '" + consuntivo.getIdConsuntivo() + "';");
            out.println("document.getElementById('IdCommessaConsuntivo').value = '" + consuntivo.getIdCommessa() + "';");
            if(consuntivo.getDescrizione() !=  null && !consuntivo.getDescrizione().equals(""))
               out.println("document.getElementById('ConsuntivoCommessa$Descrizione').value = '" + consuntivo.getDescrizione() + "';");
         }
         BudgetCommessa budget = getBudgetCommessa();
         if(budget != null) {
            out.println("document.getElementById('IdBudget').value = '" + budget.getIdBudget() + "';");
            out.println("document.getElementById('IdCommessaBudget').value = '" + budget.getIdCommessa() + "';");
            if(budget.getDescrizione() !=  null && !budget.getDescrizione().equals(""))
               out.println("document.getElementById('BudgetCommessa$Descrizione').value = '" + budget.getDescrizione() + "';");
         }			
         out.println("onUsaConsuntiviAction();");
      }
      out.println("</script>");
   }

   public java.sql.Date getDataRiferimento(){
      String dataRiferimentoStr = getRequest().getParameter("DataRiferimento");
      if(dataRiferimentoStr != null && !dataRiferimentoStr.equals("")) {
         DateType dateType = new DateType();
         return (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);	
      }
      return null;
   }

   public Commessa getCommessa() {
      Commessa commessa = null;
      String idCommessa = getRequest().getParameter("IdCommessa");
      if(idCommessa != null && !idCommessa.equals("")) {
         String[] keyParts = new String[] {Azienda.getAziendaCorrente(), idCommessa};
         String keys = KeyHelper.buildObjectKey(keyParts);
         try {
            commessa = Commessa.elementWithKey(keys, PersistentObject.NO_LOCK);
         } 
         catch (SQLException e) {
            e.printStackTrace();
         }
      }
      return commessa;
   }

   public ConsuntivoCommessa getConsuntivoCommessa() {
      ConsuntivoCommessa consuntivo = null;
      String idCommessa = getRequest().getParameter("IdCommessa");
      Integer idConsuntivo = getIntegerPram(getServletEnvironment(), "IdConsuntivo");
      if(idCommessa != null && !idCommessa.equals("") && idConsuntivo != null) {
         Object[] keyParts = new Object[] {Azienda.getAziendaCorrente(), idConsuntivo, idCommessa};
         String keys = KeyHelper.buildObjectKey(keyParts);
         try {
            consuntivo = ConsuntivoCommessa.elementWithKey(keys, PersistentObject.NO_LOCK);
         } 
         catch (SQLException e) {
            e.printStackTrace();
         }
      }		
      return consuntivo;
   }

   public BudgetCommessa getBudgetCommessa() {
      BudgetCommessa budget = null;
      String idCommessa = getRequest().getParameter("IdCommessa");
      Integer idBudget = getIntegerPram(getServletEnvironment(), "IdBudget");
      if(idCommessa != null && !idCommessa.equals("") && idBudget != null) {
         Object[] keyParts = new Object[] {Azienda.getAziendaCorrente(), idBudget, idCommessa};
         String keys = KeyHelper.buildObjectKey(keyParts);
         try {
            budget = BudgetCommessa.elementWithKey(keys, PersistentObject.NO_LOCK);
         } 
         catch (SQLException e) {
            e.printStackTrace();
         }
      }			
      return budget;
   }

   public Integer getIntegerPram(ServletEnvironment se, String param) {
      String value = se.getRequest().getParameter(param);
      if(value != null && !value.equals(""))
         return new Integer(value);
      return null;
   }
}

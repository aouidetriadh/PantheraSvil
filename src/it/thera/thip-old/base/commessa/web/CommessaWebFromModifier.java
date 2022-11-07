package it.thera.thip.base.commessa.web;

import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.jsp.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.gui.cnr.*;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.security.Security;

import it.thera.thip.base.commessa.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.cs.web.*;
import it.thera.thip.produzione.commessa.BudgetCommessa;

/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2004</p>
 * <p>Société : </p>
 * @author non attribué
 * @version 1.0
 */
/*
 * Revisions:
 * Number  Date          Owner   Description
 * 04810   12/01/2006    LP      Aggiunto visualizzazione wf in modalità SHOW
 * 05543   12/06/2006    PJ      Aggiunta commessa di contabilità analitica
 * 12103   08/03/2010    RA      Compatibilità IE8 , FF & GC
 * 15938   15/03/2012    FM      Aggiunta controllo per disabilita bottone crezaione ordine vendita
 * 29025   20/03/2019    Jackal  Gestione piano fatturazione con acconti e saldo
 * 35382   07/03/2022    RA		 Gestione budget commessa
 */

public class CommessaWebFromModifier extends ExtensibleClassWebFormModifier {

  public String getExtensibleAttributeName() {
    return "AttributiEstendibili";
  }

  public void writeHeadElements(JspWriter out) throws java.io.IOException {
    super.writeHeadElements(out);

    //...FIX 4810 inizio
    if(getMode() == OpenType.SHOW) {
      Commessa commessa = (Commessa)getBODataCollector().getBo();
      out.println("<script language='Javascript1.2'>");
      TipoCommessa tipoCommessa = commessa.getTipoCommessa();
      if(tipoCommessa != null && tipoCommessa.getWfSpecifico() != null) {
        out.println("var dispWfInShow = true;");
      }
      else {
        out.println("var dispWfInShow = false;");
      }
      out.println("</script>");
    }
    //...FIX 4810 fine

    if(getMode() == OpenType.COPY) {
      Commessa commessa = (Commessa)getBODataCollector().getBo();
      commessa.setStatoAvanzamento(Commessa.STATO_AVANZAM__PROVVISORIA);

      commessa.setIdAnnoOrdine(null);
      commessa.setIdNumeroOrdine(null);
      commessa.setIdRigaOrdine(null);
      commessa.setIdDetRigaOrdine(null);

      commessa.setDataApertura(TimeUtils.getCurrentDate());

      commessa.setDataChiusOpe(null);
      commessa.setDataChiusTec(null);
      commessa.setDataChiusura(null);
      commessa.setDataConferma(null);
      commessa.setDataFinePrevista(null);
      commessa.setDataInizioPrevista(null);
      commessa.setDataPrimaAtt(null);
      commessa.setDataUltimAtt(null);

      commessa.setPianoFatturazione(Commessa.PIANO_FATT__ASSENTE);
      commessa.getRateCommesse().clear();
      commessa.resetWfStatus();

      getBODataCollector().setBo(commessa);

      //Fix 04599 Mz inizio
      /*
      getBODataCollector().getComponentManager("StatoAvanzamento").setReadOnly(true);
      getBODataCollector().getComponentManager("AnnoOrdine").setReadOnly(true);
      getBODataCollector().getComponentManager("IdNumeroOrdine").setReadOnly(true);
      getBODataCollector().getComponentManager("IdRigaOrdine").setReadOnly(true);
      getBODataCollector().getComponentManager("DataApertura").setReadOnly(true);
      getBODataCollector().getComponentManager("DataChiusuraOperativa").setReadOnly(true);
      getBODataCollector().getComponentManager("DataChiusuraTecnica").setReadOnly(true);
      getBODataCollector().getComponentManager("DataChiusura").setReadOnly(true);
      getBODataCollector().getComponentManager("DataConferma").setReadOnly(true);
      getBODataCollector().getComponentManager("DataFinePrevista").setReadOnly(true);
      getBODataCollector().getComponentManager("DataInizioPrevista").setReadOnly(true);
      getBODataCollector().getComponentManager("DataPrimaAttivita").setReadOnly(true);
      getBODataCollector().getComponentManager("DataUltimaAttivita").setReadOnly(true);
      getBODataCollector().getComponentManager("PianoFatturazione").setReadOnly(true);
      */
      //Fix 04599 Mz fine
    }
    // FIX 04361 FATTOUMA
    initMessage(out);
    gestioneBudgetFrame(out);//35382
  }

  public void initMessage(JspWriter out) throws java.io.IOException {
    ErrorMessage warningMessage = new ErrorMessage("THIP20T158");
    String confirmationText = "";
    if(warningMessage != null)
      confirmationText = warningMessage.getLongText();
    out.println("<script language='Javascript1.2'>");
    out.println("var pianoFatturazionemessage ='" + confirmationText + "';");
    out.println("</script>");
  }

  //5543 - inizio
  public void writeBodyEndElements(JspWriter out) throws java.io.IOException {
      super.writeBodyEndElements(out);

      // se il sistema non è interfacciato a contabilità analitica,
      // faccio sparire il link alla commessa CA
      PersDatiGen pdg = PersDatiGen.getCurrentPersDatiGen();
      if (pdg.getTipoInterfCA() == PersDatiGen.NON_INTERFACCITO) {
          out.println("<script>");
          //Fix12103 Inizio RA
          //out.println("document.getElementById('COMMESSA_C A').style.display='none';");
          out.println("document.getElementById('COMMESSA_CA').style.display=displayNone;"); //Fix 15582
          //Fix12103 Fine RA
          out.println("</script>");
      }
      initiButtoneCreaOrdVen(out); //15938
      
      //Fix 29025 - inizio
      if(getMode() == OpenType.UPDATE) {
    	  gestioneTipoPiano(out);
      }
      //Fix 29025 - fine
      //35382 inizio
	  out.println("<script language=\"javascript1.2\">");
	  if(getMode() == OpenType.NEW || getMode() == OpenType.COPY) {
		  out.println("mytabbed.disableTab('BudgetTab');");
	  }
	  else {
		  Commessa commessa = (Commessa)getBODataCollector().getBo();
		  if(commessa != null)  {
			  if(commessa.getCommessaAppartenenza() != null) {
				  out.println("mytabbed.disableTab('BudgetTab');");
			  }
			  else {
				  boolean autorizzato = true;
				  try {
					  if(!Security.validate("BudgetCMM", "UPDATE") && !Security.validate("BudgetCMM", "NEW") && !Security.validate("BudgetCMM", "SHOW")) {
						  autorizzato = false;
					  }
				  } catch (SQLException e) {
					  e.printStackTrace(Trace.excStream);
				  }
				  
				  if(autorizzato)
					  out.println("mytabbed.enableTab('BudgetTab');");
				  else {
					  out.println("mytabbed.disableTab('BudgetTab');");
				  }
			  }
		  }
	  }
	  out.println("</script>");
      //35382 fine
  }
  //5543 - fine


  //Fix 29025 - inizio
  /**
   * Disabilita il campo Tipo piano nel caso in cui esista almeno una rata con
   * documento di vendita emesso
   * @param out
   * @throws IOException
   */
  protected void gestioneTipoPiano(JspWriter out) throws IOException {
	  Commessa commessa = (Commessa)getBODataCollector().getBo();
	  
	  if (Commessa.hasRataConDocVenEmesso(commessa)) {
			out.println("<script type='text/javascript' language='JavaScript'>");
			out.println("eval('document.forms[0].' + idFromName['TipoPiano']).disabled = true;");
			out.println("</script>");
	  }
  }
  //Fix 29025 - fine

  
 /**
   * initiButtoneCreaOrdVen
   *15938
   * @param out JspWriter
   */
  public void initiButtoneCreaOrdVen(JspWriter out) throws java.io.IOException
  {
    Commessa commessa = (Commessa)getBODataCollector().getBo();
    String abilita ="true";
    if(!commessa.checkAbilitaCreazioneOrdVen())
      abilita = "false";
    out.println("<script language='Javascript1.2'>");
    out.println("var abilitaBottone ='" + abilita +"';");
    out.println("</script>");
  }
  
  //35382 inizio
  public void gestioneBudgetFrame(JspWriter out) throws java.io.IOException { 
	  out.println("<script language=\"javascript1.2\">");
	  if(getMode() == OpenType.UPDATE || getMode() == OpenType.SHOW) {
		  Commessa commessa = (Commessa)getBODataCollector().getBo();
		  if(commessa != null && commessa.getCommessaAppartenenza() == null)  {
			  BudgetCommessa budget = commessa.getUltimoBudgetCommessa();
			  if (budget != null) {
				  out.println("var budgetKey = '" + URLEncoder.encode(budget.getKey()) + "';");
				  try {
					  if(getMode() == OpenType.UPDATE && !Security.validate("BudgetCMM", "UPDATE")) {
						  out.println("var modeBudget = 'SHOW';");
					  }
					  else {
						  out.println("var modeBudget = '" + getMode() + "';");
					  }
				  }
				  catch (SQLException e) {
					  e.printStackTrace(Trace.excStream);
				  }
			  } 
			  else {
				  out.println("var budgetKey ='';");
			  }				  
		  }
	  }
	  out.println("</script>");
  }
  //35382 fine  
  
}

package it.thera.thip.produzione.commessa.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;

import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.collector.BaseBOComponent;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.type.DecimalType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebElement;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.produzione.commessa.BudgetCommessa;
/**
 * BudgetCommessaDetServlet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 02/11/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34585   02/11/2021    RA       Prima struttura
 * 35382   07/03/2022	 RA		  Gestione budget da commessa
 */
public class BudgetCommessaDetServlet extends BaseServlet {
	
	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";

	protected void processAction(ServletEnvironment se) throws Exception {
		String key = se.getRequest().getParameter("key");
		BudgetCommessa budget = BudgetCommessa.elementWithKey(key, PersistentObject.NO_LOCK);

		String action = se.getRequest().getParameter("myAction");
		if(action.equals("CARICA_PREVENTIVO"))
			caricaPreventivo(se, budget);
		else if(action.equals("AGGIORNA_BUDGET"))
			aggiornaDettaglio(se, budget);
		else if(action.equals("AGGIORNA"))
			aggiornaBudget(se, budget);
		else if(action.equals("CARICA"))
			caricaBudget(se, budget);
		else if(action.equals("SALVA_BUDGET"))
			salvaBudget(se, budget);
	}
	
	public String formatBooleanValue(boolean value) {
		return value ? "Y" : "N";
	}
	
   protected void aggiornaDettaglio(ServletEnvironment se, BudgetCommessa budget) throws Exception 
   {
	   ConsuntivoCommessaDetServlet.saveViewMDV(se, "BudgetCommessa");
	   String budgetDetKey = BaseServlet.getStringParameter(se.getRequest(), "BudgetDetKey");
	   String newValueStr = BaseServlet.getStringParameter(se.getRequest(), "NewValue");
	   String oldValueStr = BaseServlet.getStringParameter(se.getRequest(), "OldValue");
	   boolean updatedOreBudget = BaseServlet.getBooleanParameter(se.getRequest(), "UpdatedOre");
	   initializeBudgetCommessa(se, budget);
	   budget.aggiornaBudgetDet(budgetDetKey, oldValueStr, newValueStr, updatedOreBudget);
	   refreshGUI(se, budget);
   }
   
   protected void aggiornaBudget(ServletEnvironment se, BudgetCommessa budget) throws Exception 
   {
	   initializeBudgetCommessa(se, budget);
	   budget.caricaAlberoBudget();
	   refreshGUI(se, budget);
   }
	
   protected void salvaBudget(ServletEnvironment se, BudgetCommessa budget) throws Exception 
   {
	   ConsuntivoCommessaDetServlet.saveViewMDV(se, "BudgetCommessa");
	   initializeBudgetCommessa(se, budget);
	   BODataCollector boDC = (BODataCollector)Factory.createObject(BODataCollector.class);
	   boDC.initialize("BudgetCommessa");
	   boDC.setBo(budget);
	   int ret = boDC.check();
	   if(ret == BODataCollector.OK)
		   ret = boDC.save();
      
	   if(ret == BODataCollector.OK)
		   refreshGUI(se, budget); 
	   else {
		   List errors = new ArrayList();
		   errors.addAll(boDC.getErrorList().getErrors());
		   manageErrorMessages(se.getResponse().getWriter(), errors);
	   }      
   }

   protected void caricaBudget(ServletEnvironment se, BudgetCommessa budget) throws Exception 
   {
	   refreshGUI(se, budget);
   }
   
   protected void caricaPreventivo(ServletEnvironment se, BudgetCommessa budget) throws Exception 
   {
	   initializeBudgetCommessa(se, budget);
	   BODataCollector boDC = (BODataCollector)Factory.createObject(BODataCollector.class);
	   boDC.initialize("BudgetCommessa");
	   boDC.setBo(budget);
	   if(boDC.check() == BODataCollector.OK) {      
		   budget.caricaBudgetDaPreventivo();
		   refreshGUI(se, budget); 
	   }
	   else {
		   List errors = new ArrayList();
		   errors.addAll(boDC.getErrorList().getErrors());
		   manageErrorMessages(se.getResponse().getWriter(), errors);
	   }      
   }
   
   protected void refreshGUI(ServletEnvironment se, BudgetCommessa budget) throws IOException, JSONException
   {
	   PrintWriter out = se.getResponse().getWriter();
	   boolean perCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "PerCommessa");
	   String ret = "parent.aggiornaBudget(";
	   if(perCommessa)
		   ret = "parent.frames['BudgetCommessaFrame'].aggiornaBudget(";
	   ret += budget.getColumnsDescriptors().toString()  + "," +
			  budget.getDataModelInMemoria().toString()  + "," +
			  budget.getCommessa().hasCompenenteATempo() + "," +
			  formatValue(budget.getCostoGenerale())     + "," +
			  formatValue(budget.getCostoIndustriale())  + "," +
			  formatValue(budget.getCostoPrimo())        + "," +
			  formatValue(budget.getCostoRiferimento())  + "," +
			  "'" + budget.getDatiComuniEstesi().getTimestampAgg() + "' );";      
   	  out.println("<script>");
   	  out.println(ret);
   	  out.println("</script>");
   }
	
   public boolean isParameter(ServletEnvironment se, String param) {
	   return se.getRequest().getParameter(param) != null ? true : false;
   }

   public void initializeBudgetCommessa(ServletEnvironment se, BudgetCommessa budget) {
	   String idAnnoPreventivo = se.getRequest().getParameter("IdAnnoPreventivo");
	   String idNumeroPreventivo = se.getRequest().getParameter("IdNumeroPreventivo");
	   budget.setIdAnnoPreventivo(idAnnoPreventivo);
	   budget.setIdNumeroPreventivo(idNumeroPreventivo);

	   if(isParameter(se, "Totali")) {
		   boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
		   budget.setTotali(totali);
	   }
		
	   if(isParameter(se, "DettagliCommessa")) {
		   boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
		   budget.setDettagliCommessa(dettagliCommessa);
	   }
		
	   if(isParameter(se, "DettagliSottoCommesse")) {
		   boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
		   budget.setDettagliSottoCommesse(dettagliSottoCommesse);
	   }
		
	   if(isParameter(se, "ComponentiPropri")) {
		   boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
		   budget.setComponentiPropri(componentiPropri);
	   }
		
	   if(isParameter(se, "SoloComponentiValorizzate")) {
		   boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
		   budget.setSoloComponentiValorizzate(soloComponentiValorizzate);
	   }
		
	   if(isParameter(se, "IdComponenteTotali")) {
		   String idComponenteTotali = BaseServlet.getStringParameter(se.getRequest(), "IdComponenteTotali");
		   budget.setIdComponenteTotali(idComponenteTotali);
	   }
		
	   if(isParameter(se, "DataRiferimento")) {						
		   String dataRiferimentoStr = se.getRequest().getParameter("DataRiferimento");
		   DateType dateType = new DateType();
		   java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);
		   budget.setDataRiferimento(dataRiferimento);
	   }
		
	   if(isParameter(se, "StatoAvanzamento")) {
		   char statoAvanzamento = BaseServlet.getStringParameter(se.getRequest(), "StatoAvanzamento").charAt(0);
		   budget.setStatoAvanzamento(statoAvanzamento);
	   }
   }
	
   public String formatValue(BigDecimal val) {
	   String valueStr = "";
	   if(val != null) {
		   DecimalType dt = new DecimalType(16,6);
		   valueStr = "'" + dt.format(dt.objectToString(val))+ "'";
	   }
	   return valueStr;
   }
	
   public void manageErrorMessages(PrintWriter out, List  errorMessages)  throws java.io.IOException {
	   out.println("<script language='JavaScript1.2'>");
	   out.println("var wListForm =  parent.document.getElementById(\"WinListForm\");");
	   out.println("parent.showForm(wListForm, false);");	
	   if(errorMessages == null || errorMessages.isEmpty())
		   return;

	   out.println("var errorsArray = new Array();");
	   out.println("var errViewObj = parent.eval(parent.errorsViewName);");
	    
	   boolean foundErrors = false;
	   boolean foundErrorsForz = false;
	   String warningMessage = "";
	   int num = 1;
	   if (errorMessages.size() > 0) {
		   boolean first = true;
		   Iterator it = errorMessages.iterator();
		   Vector allCompInError = new Vector();
		   Vector collectionInError = new Vector();
		   Vector collectionInErrorPos = new Vector();
	  		
		   int i =0;
		   while (it.hasNext()) {
			   ErrorMessage em = (ErrorMessage)it.next();
			   i++;
			   String errId = em.getId();
			   String errShortText = WebElement.formatStringForHTML(em.getText());
			   String errLongText = WebElement.formatStringForHTML(em.getLongText());
			   String errLabel = WebElement.formatStringForHTML(em.getAttOrGroupLabel());
			   String errGrpName = WebElement.formatStringForHTML(em.getAttOrGroupName());
			   String errSeverity = String.valueOf(em.getSeverity());
			   boolean isForceable = em.getForceable();
			   if(errLabel != null && !errLabel.equals(""))
				   errShortText = errLabel + " - " + errShortText;
			   Vector idCompInError = new Vector();
			   Vector components = em.getComponents();
			   if(em.getSeverity()==ErrorMessage.ERROR) {
				   foundErrors=true;
			   }
	  			
			   if(components != null && components.size() > 0)	{
				   BODataCollector coll = ((BaseBOComponent)components.firstElement()).getComponentManager().getBODataCollector();
				   if(coll.getOwnerCollectionDC() != null) {
					   String collName = coll.getOwnerCollectionDC().getKeyForBaseComponentsCollection();
					   collectionInError.addElement(collName);
					   String pos = "" + coll.getPositionInOwnerCollectionDC();
					   collectionInErrorPos.addElement(pos);
				   }
				   else {
					   boolean f = true;
					   Iterator i1 = components.iterator();
					   while(i1.hasNext()) {
						   BaseBOComponent bc = (BaseBOComponent)i1.next();
						   String classADName = bc.getComponentManager().getKeyForBaseComponentsCollection();
						   if (f) {
							   f = false;
							   idCompInError.addElement(classADName);
						   }
						   allCompInError.addElement(classADName);
					   }
				   }
			   }			

			   out.println("var idCompInError = new Array();");
			   out.println("numErr = " + errorMessages.size()  + ";");  			
			   out.println("foundErrors = " + foundErrors + ";");  			
			   out.println("foundErrorsForzabili = " + foundErrorsForz + ";");  			

			   Iterator idCompIter = idCompInError.iterator();
			   while(idCompIter.hasNext())	{
				   String curIdCompInErr = (String)idCompIter.next();
				   out.println("idCompInError[idCompInError.length]='" + i + KeyHelper.KEY_SEPARATOR+curIdCompInErr + "';");  			
			   }
			   out.println("var singleError = new Array('" + errId + "', '" + errShortText + "', idCompInError, '" + errSeverity + "', '" + errLongText + "', " + isForceable + ", '" + errGrpName + "', '" + errLabel + "');");
			   out.println("errorsArray[errorsArray.length] = singleError;");
		   }
	   } 	
	  	
	   out.println("if (numErr > 0) {");
	   out.println("		errViewObj.addErrorsAsArray(errorsArray, parent.document.forms[0].elements);");		
	   out.println("}");		
	   out.println("else ");		
	   out.println("		errViewObj.setMessage(null); ");	
	   out.println("</script>");
   }

}

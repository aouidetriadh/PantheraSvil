package it.thera.thip.produzione.commessa.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.collector.BaseBOComponent;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.type.NumberType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebElement;
import com.thera.thermfw.web.servlet.BaseServlet;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.VariaBudgetCommessa;
/**
 * BudgetCommessaDetServlet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 02/11/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34585   02/11/2021    RA       Prima struttura
 */
public class VariaBudgetCommessaDetServlet extends BaseServlet {
	
	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.VariaBudgetCommessa";

	protected void processAction(ServletEnvironment se) throws Exception {
	     String key = se.getRequest().getParameter("key");
	     VariaBudgetCommessa variaBudget = VariaBudgetCommessa.elementWithKey(key, PersistentObject.NO_LOCK);

	      String action = se.getRequest().getParameter("myAction");
	      if(action.equals("AGGIORNA_VARIABUDGET"))
	         aggiornaDettaglio(se, variaBudget);
	      else if(action.equals("AGGIORNA"))
	         aggiornaVariaBudget(se, variaBudget);
	      else if(action.equals("CARICA"))
	         caricaVariaBudget(se, variaBudget);
	}
	
	protected void aggiornaDettaglio(ServletEnvironment se, VariaBudgetCommessa variaBudget) throws Exception {
		ConsuntivoCommessaDetServlet.saveViewMDV(se, "VariaBudgetCommessa");
		String variaBudgetDetKey = BaseServlet.getStringParameter(se.getRequest(), "VariaBudgetDetKey");
		String newValueStr = BaseServlet.getStringParameter(se.getRequest(), "NewValue");
		String oldValueStr = BaseServlet.getStringParameter(se.getRequest(), "OldValue");
		boolean updatedNote = BaseServlet.getBooleanParameter(se.getRequest(), "UpdatedNote");
		boolean updatedOre = BaseServlet.getBooleanParameter(se.getRequest(), "UpdatedOre");
		initializeVariaBudgetCommessa(se, variaBudget);
		variaBudget.aggiornaVariaBudgetDet(variaBudgetDetKey, oldValueStr, newValueStr, updatedOre, updatedNote);
		refreshGUI(se, variaBudget);
	}
	   
	protected void aggiornaVariaBudget(ServletEnvironment se, VariaBudgetCommessa variaBudget) throws Exception {
		initializeVariaBudgetCommessa(se, variaBudget);
		variaBudget.caricaAlberoVariaBudget();
		refreshGUI(se, variaBudget);
	}
	   
	protected void caricaVariaBudget(ServletEnvironment se, VariaBudgetCommessa variaBudget) throws Exception {
		refreshGUI(se, variaBudget);
	}
	
	protected void refreshGUI(ServletEnvironment se, VariaBudgetCommessa variaBudget) throws IOException, JSONException {
		PrintWriter out = se.getResponse().getWriter();	     	      
		out.println("<script>");
		out.println("parent.aggiornaVariaBudget(" +
				variaBudget.getColumnsDescriptors().toString()  + "," +
				variaBudget.getDataModelInMemoria().toString()           + "," +
				variaBudget.getCommessa().hasCompenenteATempo() + "," +
				"'" + variaBudget.getDatiComuniEstesi().getTimestampAgg() + "' );");
		int colsConsuntivoNascosti = 0;
		if(variaBudget.getConsuntivoCommessa() != null) {
			if(!variaBudget.getConsuntivoCommessa().isConsolidato())
				colsConsuntivoNascosti ++;
			if(!variaBudget.getConsuntivoCommessa().isEstrazioneOrdini())
				colsConsuntivoNascosti ++;
			if(!variaBudget.getConsuntivoCommessa().isEstrazioneRichieste())
				colsConsuntivoNascosti ++;
		}
	     
		int cols = 17;
		if(variaBudget.getCommessa().hasCompenenteATempo() && colsConsuntivoNascosti != 0) {
			colsConsuntivoNascosti = colsConsuntivoNascosti * 2;
	        cols = cols - colsConsuntivoNascosti;
		}
		else if(!variaBudget.getCommessa().hasCompenenteATempo() && colsConsuntivoNascosti != 0) {         
			cols = 9 - colsConsuntivoNascosti;           
		}
	     
		if(variaBudget.getCommessa().hasCompenenteATempo()) {
			int start = 7;
			if(variaBudget.getConsuntivoCommessa() != null) 
	           start = cols;
	
	        out.println("parent.aggiungiRenderer(" + start + ", parent.nuovoBudgetCellRender);");
	        out.println("parent.aggiungiRenderer(" + (start - 1) + ", parent.nuovoBudgetCellRender);");
	        out.println("parent.aggiungiRenderer(" + (start - 2) + ", parent.editableColumnValCellRender);");
	        out.println("parent.aggiungiRenderer(" + (start - 3) + ", parent.editableColumnValCellRender);");
	        out.println("parent.aggiungiRenderer(" + (start - 4) + ", parent.editableColumnOreCellRender);");
	
	        out.println("parent.aggiungiEditor(" + (start - 2) + ", parent.columnValoreEditable);");
	        out.println("parent.aggiungiEditor(" + (start - 3) + ", parent.columnValoreEditable);");
	        out.println("parent.aggiungiEditor(" + (start - 4) + ", parent.columnOreEditable);");
		}
		else {
			int start = 4;
	        if(variaBudget.getConsuntivoCommessa() != null) 
	           start = cols;
	
	        out.println("parent.aggiungiRenderer(" + start + ", parent.nuovoBudgetCellRender);");
	        out.println("parent.aggiungiRenderer(" + (start - 1) + ", parent.editableColumnValCellRender);");
	        out.println("parent.aggiungiRenderer(" + (start - 2) + ", parent.editableColumnValCellRender);");
	
	        out.println("parent.aggiungiEditor(" + (start - 1) + ", parent.columnValoreEditable);");
	        out.println("parent.aggiungiEditor(" + (start - 2) + ", parent.columnValoreEditable);");
		}  
	     
		out.println("parent.aggiungiOnChange();");
		out.println("parent.completeRefresh();");
		out.println("</script>");
	}	

	public void initializeVariaBudgetCommessa(ServletEnvironment se, VariaBudgetCommessa variaBudget) {
		if(isParameter(se, "Totali")) {
			boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
			variaBudget.setTotali(totali);
		}
		
		if(isParameter(se, "DettagliCommessa")) {
			boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
			variaBudget.setDettagliCommessa(dettagliCommessa);
		}
		
		if(isParameter(se, "DettagliSottoCommesse")) {
			boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
			variaBudget.setDettagliSottoCommesse(dettagliSottoCommesse);
		}
		
		if(isParameter(se, "ComponentiPropri")) {
			boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
			variaBudget.setComponentiPropri(componentiPropri);
		}
		
		if(isParameter(se, "SoloComponentiValorizzate")) {
			boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
			variaBudget.setSoloComponentiValorizzate(soloComponentiValorizzate);
		}
				
		if(isParameter(se, "DataRiferimento")) {						
			String dataRiferimentoStr = se.getRequest().getParameter("DataRiferimento");

		    DateType dateType = new DateType();
		    java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);
		    variaBudget.setDataRiferimento(dataRiferimento);
		}
		
		if(isParameter(se, "StatoAvanzamento")) {
			char statoAvanzamento = BaseServlet.getStringParameter(se.getRequest(), "StatoAvanzamento").charAt(0);
			variaBudget.setStatoAvanzamento(statoAvanzamento);
		}
	}
	
	public boolean isParameter(ServletEnvironment se, String param) {
		return se.getRequest().getParameter(param) != null ? true : false;
	}
	
	public String formatValue(BigDecimal val) {
		String valueStr = "";
		if(val != null) {
			valueStr = val.toString();					
			valueStr = valueStr.replace('.', NumberType.getDecimalSeparator());
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

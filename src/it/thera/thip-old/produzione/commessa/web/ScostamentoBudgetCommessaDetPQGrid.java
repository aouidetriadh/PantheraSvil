package it.thera.thip.produzione.commessa.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.collector.BaseBOComponent;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.type.IntegerType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebElement;
import com.thera.thermfw.web.WebParamQuery;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.produzione.commessa.ScostamentoBudgetCommessa;
/**
 * ScostamentoBudgetCommessaDetPQGrid
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/03/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 35837   29/03/2022    RA       Prima struttura
 */
public class ScostamentoBudgetCommessaDetPQGrid extends WebParamQuery{
	
	protected ScostamentoBudgetCommessa iScostamentoBudget = null;
	
	public ScostamentoBudgetCommessaDetPQGrid(ServletEnvironment se) {
		setServletEnvironment(se);
		setName("grid_ScostamentoBudgetCommessaDet");
		initScostamentoBudgetCommessa(se);
	}
	
	public ScostamentoBudgetCommessa getScostamentoBudgetCommessa() {
		return iScostamentoBudget; 
	}
	
	public void setScostamentoBudgetCommessa(ScostamentoBudgetCommessa scostamentoBudget) {
		iScostamentoBudget = scostamentoBudget;
	}
	
	public void initScostamentoBudgetCommessa(ServletEnvironment se){	
		ScostamentoBudgetCommessa scostamentoBudget = null;
		String idAzienda = getServletEnvironment().getRequest().getParameter("IdAzienda");
		String idCommessa = getServletEnvironment().getRequest().getParameter("IdCommessa");
		Integer idBudget = getIntegerPram(se, "IdBudget");
		Integer idConsuntivo = getIntegerPram(se,"IdConsuntivo");
	    String dataRiferimentoStr = getServletEnvironment().getRequest().getParameter("DataRiferimento");
	    DateType dateType = new DateType();
	    java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);
			    
	    boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
		boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
		boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
		boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
		boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
	    scostamentoBudget = (ScostamentoBudgetCommessa)Factory.createObject(ScostamentoBudgetCommessa.class);
		
		scostamentoBudget.setIdAzienda(idAzienda);
		scostamentoBudget.setIdCommessa(idCommessa);
		scostamentoBudget.setIdBudget(idBudget);
		scostamentoBudget.setIdConsuntivo(idConsuntivo);
		scostamentoBudget.setDataRiferimento(dataRiferimento);
		scostamentoBudget.setTotali(totali);
		scostamentoBudget.setDettagliCommessa(dettagliCommessa);
		scostamentoBudget.setDettagliSottoCommesse(dettagliSottoCommesse);
		scostamentoBudget.setComponentiPropri(componentiPropri);
		scostamentoBudget.setSoloComponentiValorizzate(soloComponentiValorizzate);
		setScostamentoBudgetCommessa(scostamentoBudget);	    	    
	}

   public void writeImport(PrintWriter out) throws IOException, JSONException {
      super.writeImport(out);
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForJSLibrary("it/thera/thip/produzione/commessa/ScostamentoBudgetCommessa.js",  getServletEnvironment().getRequest()));
   }

	public void write(PrintWriter out) throws IOException, JSONException {	
		initializeGridDescriptorAttributes();		
		super.write(out);

		if(getScostamentoBudgetCommessa() != null && 
		   getScostamentoBudgetCommessa().getCommessa() != null && 
		   getScostamentoBudgetCommessa().getBudgetCommessa() != null && 
		   getScostamentoBudgetCommessa().getConsuntivoCommessa() != null) {	
			out.println("<script>");
			int colsConsuntivoNascosti = 0;
			if(getScostamentoBudgetCommessa().getConsuntivoCommessa() != null) {
				if(!getScostamentoBudgetCommessa().getConsuntivoCommessa().isConsolidato())
					colsConsuntivoNascosti ++;
				if(!getScostamentoBudgetCommessa().getConsuntivoCommessa().isEstrazioneOrdini())
					colsConsuntivoNascosti ++;
				if(!getScostamentoBudgetCommessa().getConsuntivoCommessa().isEstrazioneRichieste())
					colsConsuntivoNascosti ++;
			}
			
			int cols = 16;
			if(getScostamentoBudgetCommessa().getCommessa().hasCompenenteATempo() && colsConsuntivoNascosti != 0) {
				colsConsuntivoNascosti = colsConsuntivoNascosti * 2;
				cols = cols - colsConsuntivoNascosti;
			}
			else if(!getScostamentoBudgetCommessa().getCommessa().hasCompenenteATempo() && colsConsuntivoNascosti != 0) {			
				cols = 8 - colsConsuntivoNascosti;				
			}
			
			if(getScostamentoBudgetCommessa().getCommessa().hasCompenenteATempo()) {
				out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + cols + "].render=percValCellRender;");
				out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (cols-1) + "].render=scosValCellRender;");
				out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (cols-2) + "].render=percOreCellRender;");
				out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (cols-3) + "].render=scosOreCellRender;");
			}
			else {
				out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + cols + "].render=percValCellRender;");
				out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (cols-1) + "].render=scosValCellRender;");
			}
			out.println("</script>");
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void initializeGridDescriptorAttributes() throws JSONException  {
		getGridDescriptorAttributes().put("height", "100%");
		getGridDescriptorAttributes().put("width", "auto");
		getGridDescriptorAttributes().put("menuIcon", false);
		getGridDescriptorAttributes().put("editable", false);
		getGridDescriptorAttributes().put("sortable", false);	
		getGridDescriptorAttributes().put("selectionModel", new JSONObject().put("type", "row").put("mode", "single"));
		getGridDescriptorAttributes().put("treeModel", new JSONObject().put("dataIndx", "CompCosto").put("cascade", true).put("icons", false));
		getGridDescriptorAttributes().put("editModel", new JSONObject().put("clicksToEdit", 2));
		getGridDescriptorAttributes().put("freezeCols", 1);
		getGridDescriptorAttributes().put("collapsible", false);
		getGridDescriptorAttributes().put("scrollModel", new JSONObject().put("autoFit", true));
		getGridDescriptorAttributes().put("numberCell", new JSONObject().put("show", false));
		getGridDescriptorAttributes().put("showTitle", false);
		getGridDescriptorAttributes().put("resizable", true);
		getGridDescriptorAttributes().put("hwrap", false);
		getGridDescriptorAttributes().put("wrap", false);
		getGridDescriptorAttributes().put("showToolbar", false);
		getGridDescriptorAttributes().put("showtTop", false);
		getGridDescriptorAttributes().put("showBottom", false);
		getGridDescriptorAttributes().put("stripeRows", false);
		getGridDescriptorAttributes().put("dragColumns", new JSONObject().put("enabled", false));
	}

	protected JSONObject getData() throws JSONException {
      JSONObject data = new JSONObject();    
      data.put("data", iScostamentoBudget.getDataModel());
      data.put("TotHH", "");
      data.put("TotVal", "");
      return data;
	}

	@Override
	protected JSONObject getDataModel() throws JSONException {
		JSONObject data = new JSONObject();
		String mode = getServletEnvironment().getRequest().getParameter("mode");
		String idCommessa = servletEnvironment.getRequest().getParameter("IdCommessa");
		if(idCommessa != null && !idCommessa.equals("")) {
			data = getData();
		}		
		return data;
	}

	@Override
	protected JSONArray getColumnModel() throws JSONException {
		return getScostamentoBudgetCommessa().getColumnsDescriptors();
	}
	
	public Integer getIntegerPram(ServletEnvironment se, String param) {
		String value = se.getRequest().getParameter(param);
		if(value != null && !value.equals(""))
		{
		   IntegerType intType = new IntegerType();
		   String unformatValue = intType.unFormat(value);
			return new Integer(unformatValue);
		}
		return null;
	}
	
	public List check() {
		List errors = new ArrayList();
		BODataCollector boDC = (BODataCollector)Factory.createObject(BODataCollector.class);
		boDC.initialize("ScostamentoBudgetCommessa");
		boDC.setBo(getScostamentoBudgetCommessa());
		boDC.check();
		if(boDC.getErrorList() != null && !boDC.getErrorList().getErrors().isEmpty()) {
			errors.addAll(boDC.getErrorList().getErrors());
		}
		return errors;
	}
	
	public void manageErrorMessages(PrintWriter out, List  errorMessages)  throws java.io.IOException {
		out.println("<script language='JavaScript1.2'>");
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

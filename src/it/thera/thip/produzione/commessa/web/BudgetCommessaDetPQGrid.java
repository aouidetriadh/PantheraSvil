package it.thera.thip.produzione.commessa.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebParamQuery;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.produzione.commessa.BudgetCommessa;
/**
 * BudgetCommessaDetPQGrid
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 02/11/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34585   05/10/2021    RA       Prima struttura
 */
public class BudgetCommessaDetPQGrid extends WebParamQuery{

   protected BudgetCommessa iBudget = null;

   public BudgetCommessaDetPQGrid(ServletEnvironment se) {
      setServletEnvironment(se);
      setName("grid_BudgetCommessaDet");
      initBudgetCommessa(se);
   }

   public BudgetCommessa getBudgetCommessa() {
      return iBudget;
   }

   public void setBudgetCommessa(BudgetCommessa budget) {
      iBudget = budget;
   }

   public void initBudgetCommessa(ServletEnvironment se){	
      BudgetCommessa budget = null;
      String idAzienda = getServletEnvironment().getRequest().getParameter("IdAzienda");
      String idCommessa = getServletEnvironment().getRequest().getParameter("IdCommessa");
      String idComponenteTotali = getServletEnvironment().getRequest().getParameter("IdComponenteTotali");
      char statoAvanzamento = BaseServlet.getStringParameter(getServletEnvironment().getRequest(), "StatoAvanzamento").charAt(0);	    
      boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
      boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
      boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
      boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
      boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
      String dataRiferimentoStr = getServletEnvironment().getRequest().getParameter("DataRiferimento");
      DateType dateType = new DateType();
      java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);
      String key = getServletEnvironment().getRequest().getParameter("key");
      if(key != null && !key.equals("")) {
         try {
            budget = BudgetCommessa.elementWithKey(key, PersistentObject.NO_LOCK);
         } 
         catch (SQLException e) {
            e.printStackTrace(Trace.excStream);
         }
      }
      if(budget == null) {
         budget = (BudgetCommessa)Factory.createObject(BudgetCommessa.class);
      }
      budget.setIdAzienda(idAzienda);
      budget.setIdCommessa(idCommessa);
      budget.setIdComponenteTotali(idComponenteTotali);
      budget.setDataRiferimento(dataRiferimento);
      budget.setStatoAvanzamento(statoAvanzamento);		
      budget.setTotali(totali);
      budget.setDettagliCommessa(dettagliCommessa);
      budget.setDettagliSottoCommesse(dettagliSottoCommesse);
      budget.setComponentiPropri(componentiPropri);
      budget.setSoloComponentiValorizzate(soloComponentiValorizzate);	
      budget.caricaAlberoBudget();
      setBudgetCommessa(budget);	    	    
   }

   public void writeImport(PrintWriter out) throws IOException, JSONException 
   {
      super.writeImport(out);
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForCSS("it/thera/thip/produzione/commessa/paramQueryCommessa.css", getServletEnvironment().getRequest()));
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForJSLibrary("it/thera/thip/produzione/commessa/BudgetCommessa.js",  getServletEnvironment().getRequest()));
   }

   public void write(PrintWriter out) throws IOException, JSONException {		
      initializeGridDescriptorAttributes();		
      super.write(out);	
      out.println("<script>");		

      if(getBudgetCommessa() != null && getBudgetCommessa().getCommessa() != null && getBudgetCommessa().getCommessa().hasCompenenteATempo()) {
         out.println("$" + getName() + ".pqGrid(\"instance\").colModel[1].editable=columnOreEditable;");
         out.println("$" + getName() + ".pqGrid(\"instance\").colModel[2].editable=columnValoreEditable;");
      }
      else {
         out.println("$" + getName() + ".pqGrid(\"instance\").colModel[1].editable=columnValoreEditable;");
      }		
      out.println("$" + getName() + ".pqGrid(\"instance\").on(\"change\", cellSaveBudget)");
      out.println("$" + getName() + ".pqGrid(\"instance\").refreshDataAndView();");
      out.println("</script>");
   }

   @SuppressWarnings("unchecked")
   public void initializeGridDescriptorAttributes() throws JSONException  {
      getGridDescriptorAttributes().put("height", "100%");
      getGridDescriptorAttributes().put("width", "auto");
      getGridDescriptorAttributes().put("menuIcon", false);
      getGridDescriptorAttributes().put("editable", "gridEditable");
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
      data.put("data", iBudget.getDataModelInMemoria());
      data.put("TotHH", "");
      data.put("TotVal", "");
      return data;
   }

   @Override
   protected JSONObject getDataModel() throws JSONException {
      JSONObject data = new JSONObject();
      String mode = getServletEnvironment().getRequest().getParameter("mode");
      if(mode != null && mode.equals("NEW")) {
         return data;
      }
      else
         data = getData();		

      return data;
   }

   @Override
   protected JSONArray getColumnModel() throws JSONException {
      return getBudgetCommessa().getColumnsDescriptors();
   }
}

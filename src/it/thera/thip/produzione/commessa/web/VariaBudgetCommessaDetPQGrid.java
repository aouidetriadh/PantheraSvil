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

import it.thera.thip.produzione.commessa.VariaBudgetCommessa;
/**
 * VariaBudgetCommessaDetPQGrid
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 20/12/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34795   20/12/2021    RA       Prima struttura
 */
public class VariaBudgetCommessaDetPQGrid extends WebParamQuery{

   protected VariaBudgetCommessa iVariaBudget = null;

   public VariaBudgetCommessaDetPQGrid(ServletEnvironment se) {
      setServletEnvironment(se);
      setName("grid_VariaBudgetCommessaDet");
      initVariaBudgetCommessa(se);
   }

   public VariaBudgetCommessa getVariaBudgetCommessa() {
      return iVariaBudget;
   }

   public void setVariaBudgetCommessa(VariaBudgetCommessa variaBudget) {
      iVariaBudget = variaBudget;
   }

   public void initVariaBudgetCommessa(ServletEnvironment se){	
      VariaBudgetCommessa variaBudget = null;
      String idAzienda = getServletEnvironment().getRequest().getParameter("IdAzienda");
      String idCommessa = getServletEnvironment().getRequest().getParameter("IdCommessa");
      String dataRiferimentoStr = getServletEnvironment().getRequest().getParameter("DataRiferimento");
      DateType dateType = new DateType();
      java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);		
      char statoAvanzamento = BaseServlet.getStringParameter(getServletEnvironment().getRequest(), "StatoAvanzamento").charAt(0);	    
      boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
      boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
      boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
      boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
      boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
      String key = getServletEnvironment().getRequest().getParameter("key");
      if(key != null && !key.equals("")) {
         try {
            variaBudget = VariaBudgetCommessa.elementWithKey(key, PersistentObject.NO_LOCK);
         } 
         catch (SQLException e) {
            e.printStackTrace(Trace.excStream);
         }
      }
      if(variaBudget == null) {
         variaBudget = (VariaBudgetCommessa)Factory.createObject(VariaBudgetCommessa.class);
      }
      variaBudget.setIdAzienda(idAzienda);
      variaBudget.setIdCommessa(idCommessa);
      variaBudget.setDataRiferimento(dataRiferimento);
      variaBudget.setStatoAvanzamento(statoAvanzamento);
      variaBudget.setTotali(totali);
      variaBudget.setDettagliCommessa(dettagliCommessa);
      variaBudget.setDettagliSottoCommesse(dettagliSottoCommesse);
      variaBudget.setComponentiPropri(componentiPropri);
      variaBudget.setSoloComponentiValorizzate(soloComponentiValorizzate);	
      variaBudget.caricaAlberoVariaBudget();
      setVariaBudgetCommessa(variaBudget);	    	    
   }

   public void writeImport(PrintWriter out) throws IOException, JSONException 
   {
      super.writeImport(out);
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForCSS("it/thera/thip/produzione/commessa/paramQueryCommessa.css", getServletEnvironment().getRequest()));
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForJSLibrary("it/thera/thip/produzione/commessa/VariaBudgetCommessa.js",  getServletEnvironment().getRequest()));
   }

   public void write(PrintWriter out) throws IOException, JSONException {		
      initializeGridDescriptorAttributes();		
      super.write(out);	

      out.println("<script>");		
      if(getVariaBudgetCommessa() != null && getVariaBudgetCommessa().getCommessa() != null) {
         int colsConsuntivoNascosti = 0;
         if(getVariaBudgetCommessa().getConsuntivoCommessa() != null) {
            if(!getVariaBudgetCommessa().getConsuntivoCommessa().isConsolidato())
               colsConsuntivoNascosti ++;
            if(!getVariaBudgetCommessa().getConsuntivoCommessa().isEstrazioneOrdini())
               colsConsuntivoNascosti ++;
            if(!getVariaBudgetCommessa().getConsuntivoCommessa().isEstrazioneRichieste())
               colsConsuntivoNascosti ++;
         }

         int cols = 17;
         if(getVariaBudgetCommessa().getCommessa().hasCompenenteATempo() && colsConsuntivoNascosti != 0) {
            colsConsuntivoNascosti = colsConsuntivoNascosti * 2;
            cols = cols - colsConsuntivoNascosti;
         }
         else if(!getVariaBudgetCommessa().getCommessa().hasCompenenteATempo() && colsConsuntivoNascosti != 0) {			
            cols = 9 - colsConsuntivoNascosti;				
         }


         if(getVariaBudgetCommessa().getCommessa().hasCompenenteATempo()) {
            int start = 7;
            if(getVariaBudgetCommessa().getConsuntivoCommessa() != null) 
               start = cols;

            out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (start - 2) + "].editable=columnValoreEditable;");
            out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (start - 3) + "].editable=columnValoreEditable;");
            out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (start - 4) + "].editable=columnOreEditable;");
         }
         else {
            int start = 4;
            if(getVariaBudgetCommessa().getConsuntivoCommessa() != null) 
               start = cols;

            out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (start - 1) + "].editable=columnValoreEditable;");
            out.println("$" + getName() + ".pqGrid(\"instance\").colModel[" + (start - 2) + "].editable=columnValoreEditable;");
         }	
         out.println("$" + getName() + ".pqGrid(\"instance\").on(\"cellSave\", cellSaveAction)");

      }		
      out.println("$" + getName() + ".pqGrid(\"instance\").refreshDataAndView()");
      out.println("</script>");
   }

   @SuppressWarnings("unchecked")
   public void initializeGridDescriptorAttributes() throws JSONException  {
      getGridDescriptorAttributes().put("height", "100%");
      getGridDescriptorAttributes().put("width", "auto");
      getGridDescriptorAttributes().put("menuIcon", false);
      getGridDescriptorAttributes().put("editable", "gridEditable");
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
      data.put("data", iVariaBudget.getDataModelInMemoria());
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
      return getVariaBudgetCommessa().getColumnsDescriptors();
   }
}

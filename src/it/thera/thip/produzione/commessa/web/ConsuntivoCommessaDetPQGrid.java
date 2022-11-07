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

import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
/**
 * ConsuntivoCommessaDetPQGrid
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 05/10/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   05/10/2021    RA       Prima struttura
 */
public class ConsuntivoCommessaDetPQGrid extends WebParamQuery{

   protected ConsuntivoCommessa consuntivo = null;

   public ConsuntivoCommessaDetPQGrid(ServletEnvironment se) {
      setServletEnvironment(se);
      setName("grid_ConsuntivoCommessaDet");
      initConsuntivoCommessa(se);
   }

   public ConsuntivoCommessa getConsuntivoCommessa() {
      return consuntivo;
   }

   public void setConsuntivoCommessa(ConsuntivoCommessa cons) {
      consuntivo = cons;
   }

   public void initConsuntivoCommessa(ServletEnvironment se){	
      ConsuntivoCommessa consuntivo = null;
      String idAzienda = getServletEnvironment().getRequest().getParameter("IdAzienda");
      String idCommessa = getServletEnvironment().getRequest().getParameter("IdCommessa");
      String idComponenteTotali = getServletEnvironment().getRequest().getParameter("IdComponenteTotali");
      boolean usaDataEstrazioneStorici = BaseServlet.getBooleanParameter(getServletEnvironment().getRequest(), "UsaDataEstrazioneStorici");
      boolean consolidato = BaseServlet.getBooleanParameter(getServletEnvironment().getRequest(), "Consolidato");
      boolean estrazioneOrdini = BaseServlet.getBooleanParameter(getServletEnvironment().getRequest(), "EstrazioneOrdini");
      boolean estrazioneRichieste = BaseServlet.getBooleanParameter(getServletEnvironment().getRequest(), "EstrazioneRichieste");

      String dataRiferimentoStr = getServletEnvironment().getRequest().getParameter("DataRiferimento");
      DateType dateType = new DateType();
      java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dateType.format(dataRiferimentoStr));

      char statoAvanzamento = BaseServlet.getStringParameter(getServletEnvironment().getRequest(), "StatoAvanzamento").charAt(0);	    
      boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
      boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
      boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
      boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
      boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");

      String key = getServletEnvironment().getRequest().getParameter("key");
      if(key != null && !key.equals("")) {
         try {
            consuntivo = ConsuntivoCommessa.elementWithKey(key, PersistentObject.NO_LOCK);
         } 
         catch (SQLException e) {
            e.printStackTrace(Trace.excStream);
         }
      }
      if(consuntivo == null) {
         consuntivo = (ConsuntivoCommessa)Factory.createObject(ConsuntivoCommessa.class);
      }
      consuntivo.setIdAzienda(idAzienda);
      consuntivo.setIdCommessa(idCommessa);
      consuntivo.setIdComponenteTotali(idComponenteTotali);
      consuntivo.setUsaDataEstrazioneStorici(usaDataEstrazioneStorici);
      consuntivo.setConsolidato(consolidato);
      consuntivo.setEstrazioneOrdini(estrazioneOrdini);
      consuntivo.setEstrazioneRichieste(estrazioneRichieste);
      consuntivo.setDataRiferimento(dataRiferimento);
      //consuntivo.setTipoVisualizzazione(tipoVisualizzazione);
      consuntivo.setStatoAvanzamento(statoAvanzamento);
      consuntivo.setTotali(totali);
      consuntivo.setDettagliCommessa(dettagliCommessa);
      consuntivo.setDettagliSottoCommesse(dettagliSottoCommesse);
      consuntivo.setComponentiPropri(componentiPropri);
      consuntivo.setSoloComponentiValorizzate(soloComponentiValorizzate);
      consuntivo.caricaAlberoConsuntivi();
      setConsuntivoCommessa(consuntivo);

   }

   public void writeImport(PrintWriter out) throws IOException, JSONException 
   {
      super.writeImport(out);
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForCSS("it/thera/thip/produzione/commessa/paramQueryCommessa.css", getServletEnvironment().getRequest()));
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForJSLibrary("it/thera/thip/produzione/commessa/ConsuntivoCommessa.js",  getServletEnvironment().getRequest()));
   }

   public void write(PrintWriter out) throws IOException, JSONException {		
      initializeGridDescriptorAttributes();		
      super.write(out);		

      out.println("<script>");
      out.println("$" + getName() + ".pqGrid(\"instance\").colModel[1].render=storiciCellRender;");
      out.println("$" + getName() + ".pqGrid(\"instance\").refreshView();");
      out.println("</script>");
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
      getGridDescriptorAttributes().put("editModel", new JSONObject().put("clicksToEdit", 1));
      getGridDescriptorAttributes().put("freezeCols", 1);
      getGridDescriptorAttributes().put("collapsible", false);
      getGridDescriptorAttributes().put("scrollModel", new JSONObject().put("autoFit", false));
      getGridDescriptorAttributes().put("numberCell", new JSONObject().put("show", false));
      getGridDescriptorAttributes().put("showTitle", false);
      getGridDescriptorAttributes().put("resizable", true);
      getGridDescriptorAttributes().put("hwrap", false);
      getGridDescriptorAttributes().put("wrap", false);
      getGridDescriptorAttributes().put("showToolbar", false);
      getGridDescriptorAttributes().put("showtTop", false);
      getGridDescriptorAttributes().put("stripeRows", false);
      getGridDescriptorAttributes().put("dragColumns", new JSONObject().put("enabled", false));
   }


   protected JSONObject getData() throws JSONException {

      JSONObject data = new JSONObject();    
      data.put("data", consuntivo.getDataModelInMemoria());
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
      return getConsuntivoCommessa().getColumnsDescriptors();
   }
}

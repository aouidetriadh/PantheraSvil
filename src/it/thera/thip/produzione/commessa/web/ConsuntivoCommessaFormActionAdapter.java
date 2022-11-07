package it.thera.thip.produzione.commessa.web;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.LicenceManager;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebMenu;
import com.thera.thermfw.web.WebMenuBar;
import com.thera.thermfw.web.WebMenuItem;
import com.thera.thermfw.web.WebToolBar;
import com.thera.thermfw.web.WebToolBarButton;
import com.thera.thermfw.web.WebToolBarException;
import com.thera.thermfw.web.servlet.BaseServlet;
import com.thera.thermfw.web.servlet.FormActionAdapter;
/**
 * ConsuntivoCommessaFormActionAdapter
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 07/07/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 36227   07/07/2022    RA       Prima struttura
 * 36252   18/07/2022	 RA		  Aggiunto gestione Stampa consuntivo commessa
 */
public class ConsuntivoCommessaFormActionAdapter extends FormActionAdapter {

   public void modifyToolBar(WebToolBar toolBar) {
      super.modifyToolBar(toolBar);
      try {
         toolBar.removeSeparatorAfter("Print");
         toolBar.removeButton("SaveScreenData");
         toolBar.removeButton("SaveScreenDataPullDown");
         toolBar.removeButton("Print");
         toolBar.addButton("CheckAll", new WebToolBarButton("Print", "action_submit", "new", "no", "com.thera.thermfw.web.resources.web", null, "thermweb/image/gui/Preview.gif", "PRINT", "single", false, false, "", "F_Print")); 
      } 
      catch (WebToolBarException e) {
         e.printStackTrace(Trace.excStream);
      }		
   }	

   public void modifyMenuBar(WebMenuBar menuBar) {
      super.modifyMenuBar(menuBar);
      menuBar.removeMenu("Print");
      WebMenu objectMenu = (WebMenu)menuBar.getMenu("ObjectMenu");
      objectMenu.addMenu(new WebMenuItem("Print", "action_submit", "new", "no", "com.thera.thermfw.web.resources.web", null, "PRINT", "single", false, "F_Print"));
   }

   protected void print(ClassADCollection cadc, ServletEnvironment se) throws ServletException, IOException {
      if (LicenceManager.checkClassDescriptor(se, this, cadc)) {
         String objectKey = se.getRequest().getParameter("thKey");
         String url = "/" + "it/thera/thip/produzione/commessa/StampaConsuntivoCommessa.jsp" ;
         String params = "?KEY=" + URLEncoder.encode(objectKey);
         se.sendRequest(getServletContext(), url + params, false);
      }
   }

   protected void save(ClassADCollection  cadc, ServletEnvironment se) throws ServletException, IOException
   {
      ConsuntivoCommessaDetServlet.saveViewMDV(se, "ConsuntivoCommessa");
      se.sendRequest(getServletContext(), se.getServletPath() + "it.thera.thip.produzione.commessa.web.ConsuntivoCommessaSave", true);
   }


   protected void saveMDV(ServletEnvironment se)
   {
      boolean pushed = false;
      try
      {
         ConnectionManager.pushConnection();
         pushed = true;
         boolean totaliB = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
         boolean dettagliCommB = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
         boolean dettagliSottoComB = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
         boolean compPropriB = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiProprie");
         boolean soloValB = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");

         String totali = totaliB ? "Y" : "N";
         String dettagliComm = dettagliCommB ? "Y" : "N";
         String dettagliSottoCom = dettagliSottoComB ? "Y" : "N";
         String compPropri = compPropriB ? "Y" : "N";
         String soloVal = soloValB ? "Y" : "N";

         String stream = 
               "Totali" + ScreenData.SD_SEPARATOR + totali + ScreenData.SD_SEPARATOR +
               "DettagliCommessa" + ScreenData.SD_SEPARATOR + dettagliComm + ScreenData.SD_SEPARATOR +
               "DettagliSottoCommesse" + ScreenData.SD_SEPARATOR + dettagliSottoCom + ScreenData.SD_SEPARATOR +
               "ComponentiPropri" + ScreenData.SD_SEPARATOR + compPropri + ScreenData.SD_SEPARATOR +
               "SoloComponentiValorizzate" + ScreenData.SD_SEPARATOR + soloVal;    

         ScreenData screenData = ScreenData.getDefaultScreenData(Factory.getName("ConsuntivoCommessa", Factory.CLASS_HDR));//Fix 8601 //Fix 10765
         if (screenData == null) {
            screenData = (ScreenData) Factory.createObject(ScreenData.class);
            screenData.setClassHdr(Factory.getName("ConsuntivoCommessa", Factory.CLASS_HDR));
            screenData.setUserId(Security.getCurrentUser().getId());
            screenData.setDescription("Default");
            screenData.setDefaultRow(true);
         }
         screenData.setStreamedDataMySep(stream);
         int rc = screenData.save();
         if(rc > 0)
            ConnectionManager.commit();
         else
         {
            ConnectionManager.rollback();
            System.out.println("Errore nel salvataggio della memorizzazione dati video del documento!");
         }
      }
      catch(Exception ex)
      {
         ex.printStackTrace(Trace.excStream);
      }
      finally
      {
         if(pushed)
            ConnectionManager.popConnection();
      }
   }
}

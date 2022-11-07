package it.thera.thip.produzione.commessa.web;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.web.LicenceManager;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebMenuBar;
import com.thera.thermfw.web.WebMenuItem;
import com.thera.thermfw.web.WebToolBar;
import com.thera.thermfw.web.WebToolBarButton;

import it.thera.thip.cs.web.AziendaGridActionAdapter;
/**
 * ConsuntivoCommessaGridActionAdapter
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 05/10/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   05/10/2021    RA       Prima struttura
 * 36252   18/07/2022    RA		  Redefine azione Print
 */
public class ConsuntivoCommessaGridActionAdapter extends AziendaGridActionAdapter
{
   public void modifyMenuBar(WebMenuBar menuBar) {
      menuBar.removeMenu("ListMenu.NewTemplate");
      menuBar.removeMenu("SelectedMenu.Copy");
      menuBar.removeMenu("SelectedMenu.Print");
      WebMenuItem print = new WebMenuItem("ConsuntivoCommessa", "action_submit", "new", "no", "com.thera.thermfw.web.resources.web", "Print", PRINT, "single", true);
      menuBar.addMenu("SelectedMenu.Delete", print);
   }

   public void modifyToolBar(WebToolBar toolBar) {
      super.modifyToolBar(toolBar);
      toolBar.removeButton("Copy");
      toolBar.removeButton("Print");
      WebToolBarButton printButton = new WebToolBarButton("ConsuntivoCommessa", "action_submit", "new", "no","com.thera.thermfw.web.resources.web", "Print", "thermweb/image/gui/Preview.gif", PRINT , "single", false);
      toolBar.addButton("PrintList",printButton);
   }

   protected void print(ClassADCollection cadc, ServletEnvironment se) throws ServletException, IOException {
      if (LicenceManager.checkClassDescriptor(se, this, cadc)) {
         String[] objectKeys = se.getRequest().getParameterValues("ObjectKey");
         int objectKeyNum = 0;
         String objectKeyNumStr = getStringParameter(se.getRequest(), "thObjectKeyNum");
         if(objectKeyNumStr != null && objectKeyNumStr.length() > 0)
            objectKeyNum = Integer.parseInt(objectKeyNumStr);

         String url = "/" + "it/thera/thip/produzione/commessa/StampaConsuntivoCommessa.jsp" ;
         String params = "?KEY=" + URLEncoder.encode(objectKeys[objectKeyNum]);
         se.sendRequest(getServletContext(), url + params, false);
      }
   }

}

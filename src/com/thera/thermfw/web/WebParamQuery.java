package com.thera.thermfw.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * WebParamQuery
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 25/10/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 36726   25/10/2021    RA		    Prima struttura
 */
public abstract class WebParamQuery {
   protected String iName;
   protected HashMap iGridDescriptorAttributes = new HashMap();
   protected ServletEnvironment servletEnvironment;

   public String getName() {
      return iName;
   }

   public void setName(String name) {
      this.iName = name;
   }

   public HashMap getGridDescriptorAttributes() {
      return iGridDescriptorAttributes;
   }

   public void setGridDescriptorAttributes(HashMap gridDescriptorAttributes) {
      this.iGridDescriptorAttributes = gridDescriptorAttributes;
   }

   public ServletEnvironment getServletEnvironment() {
      return servletEnvironment;
   }

   public void setServletEnvironment(ServletEnvironment se) {
      servletEnvironment = se;
   }

   public void write(PrintWriter out) throws IOException, JSONException {
      out.println("<div id=\"" + getName() + "\"></div>");
      out.println("<script>");
      out.println("var $" + getName() + " = $(\"#" + getName() + "\").pqGrid(" + getGridDescriptor().toString() + ");");
      out.println("</script>");
   }

   public void writeImport(PrintWriter out) throws IOException, JSONException 
   {
      out.println(WebExternalLibrary.importStylesheet(getServletEnvironment().getRequest(), "jquery-ui", "1.11.4"));  
      out.println(WebExternalLibrary.importStylesheet(getServletEnvironment().getRequest(), "paramquery", "pqgrid", "8.0.1", true));  
      out.println(WebExternalLibrary.importStylesheet(getServletEnvironment().getRequest(), "paramquery", "pqgrid.ui", "8.0.1", true));  
      out.println(WebExternalLibrary.importStylesheet(getServletEnvironment().getRequest(), "paramquery", "themes/gray/pqgrid", "8.0.1", false));  
      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForCSS("thermweb/css/paramQuery.css", getServletEnvironment().getRequest()));

      out.println(com.thera.thermfw.web.WebJSTypeList.getImportForJSLibrary("thermweb/factory/gui/therm.js", getServletEnvironment().getRequest()));
      out.println(WebExternalLibrary.importScript(getServletEnvironment().getRequest(), "jquery", "1.9.1"));
      out.println(WebExternalLibrary.importScript(getServletEnvironment().getRequest(), "jquery-ui", "1.11.4"));
      out.println(WebExternalLibrary.importScript(getServletEnvironment().getRequest(), "paramquery", "pqgrid", "8.0.1", true));  
      out.println(WebExternalLibrary.importScript(getServletEnvironment().getRequest(), "paramquery", "localize/pq-localize-it", "8.0.1", false));  
      out.println(WebExternalLibrary.importScript(getServletEnvironment().getRequest(), "paramquery", "javascript-detect-element-resize/jquery.resize", "8.0.1", false));  
   }


   protected JSONObject getGridDescriptor() throws JSONException {
      JSONObject descriptor = new JSONObject();
      for(Iterator iterator = getGridDescriptorAttributes().entrySet().iterator(); iterator.hasNext();) {
         Map.Entry attribute = (Map.Entry)iterator.next();
         descriptor.put(String.valueOf(attribute.getKey()), attribute.getValue());
      }
      descriptor.put("dataModel", getDataModel());
      descriptor.put("colModel", getColumnModel());
      return descriptor;
   }

   protected abstract JSONObject getDataModel() throws JSONException;
   protected abstract JSONArray getColumnModel() throws JSONException;

}

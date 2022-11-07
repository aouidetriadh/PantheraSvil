<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///D:/3rdParty/PantheraSvilL2Panth01/websrc/dtd/xhtml1-transitional.dtd">
<html>
<!-- WIZGEN Therm 2.0.0 as Form - multiBrowserGen = true -->
<%=WebGenerator.writeRuntimeInfo()%>

<head>
<%@ page contentType="text/html; charset=Cp1252"%>
<%@ page import= " 
  java.sql.*, 
  java.util.*, 
  java.lang.reflect.*, 
  javax.naming.*, 
  com.thera.thermfw.common.*, 
  com.thera.thermfw.type.*, 
  com.thera.thermfw.web.*, 
  com.thera.thermfw.security.*, 
  com.thera.thermfw.base.*, 
  com.thera.thermfw.ad.*, 
  com.thera.thermfw.persist.*, 
  com.thera.thermfw.gui.cnr.*, 
  com.thera.thermfw.setting.*, 
  com.thera.thermfw.collector.*, 
  com.thera.thermfw.batch.web.*, 
  com.thera.thermfw.batch.*, 
  com.thera.thermfw.pref.* 
"%> 
<%
  ServletEnvironment se = (ServletEnvironment)Factory.createObject("com.thera.thermfw.web.ServletEnvironment"); 
  BODataCollector SQLFileTesterWebBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm SQLFileTesterWebForm =  
     new com.thera.thermfw.web.WebForm(request, response, "SQLFileTesterWebForm", "SQLFileTesterWeb", "Arial,10", "com.thera.thermfw.setup.web.SQLFileTesterWebActionAdapter", false, false, true, true, true, true, null, 1, true, "com/thera/thermfw/setup/SQLFileTesterWeb.js"); 
  SQLFileTesterWebForm.setServletEnvironment(se); 
  SQLFileTesterWebForm.setJSTypeList(jsList); 
  SQLFileTesterWebForm.setHeader("it.thera.thip.cs.PantheraHeader.jsp"); 
  SQLFileTesterWebForm.setFooter("com.thera.thermfw.common.Footer.jsp"); 
  SQLFileTesterWebForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = SQLFileTesterWebForm.getMode(); 
  String key = SQLFileTesterWebForm.getKey(); 
  String errorMessage; 
  boolean requestIsValid = false; 
  boolean leftIsKey = false; 
  boolean conflitPresent = false; 
  String leftClass = ""; 
  try 
  {
     se.initialize(request, response); 
     if(se.begin()) 
     { 
        SQLFileTesterWebForm.outTraceInfo(getClass().getName()); 
        String collectorName = SQLFileTesterWebForm.findBODataCollectorName(); 
                SQLFileTesterWebBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (SQLFileTesterWebBODC instanceof WebDataCollector) 
            ((WebDataCollector)SQLFileTesterWebBODC).setServletEnvironment(se); 
        SQLFileTesterWebBODC.initialize("SQLFileTesterWeb", true, 1); 
        SQLFileTesterWebForm.setBODataCollector(SQLFileTesterWebBODC); 
        int rcBODC = SQLFileTesterWebForm.initSecurityServices(); 
        mode = SQLFileTesterWebForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           SQLFileTesterWebForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = SQLFileTesterWebBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              SQLFileTesterWebForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

</head>

<body bottommargin="0" leftmargin="0" onbeforeunload="<%=SQLFileTesterWebForm.getBodyOnBeforeUnload()%>" onload="<%=SQLFileTesterWebForm.getBodyOnLoad()%>" onunload="<%=SQLFileTesterWebForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   SQLFileTesterWebForm.writeBodyStartElements(out); 
%> 

  <table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = SQLFileTesterWebForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", SQLFileTesterWebBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=SQLFileTesterWebForm.getServlet()%>" enctype="multipart/form-data" method="post" name="SQLFileTesterWeb" style="height:100%"><%
  SQLFileTesterWebForm.writeFormStartElements(out); 
%>

    <table cellpadding="7" cellspacing="0" height="100%" id="emptyborder" width="100%">
      <tr>
        <td>
          <table cellpadding="0" cellspacing="7" width="100%">
            <tr>
              <td>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "SQLFileTesterWeb", "PlatformType", null); 
   label.setParent(SQLFileTesterWebForm); 
%><label class="<%=label.getClassType()%>" for="PlatformType"><%label.write(out);%></label><%}%>
              </td>
              <td>
                <% 
  WebComboBox SQLFileTesterWebPlatformType =  
     new com.thera.thermfw.web.WebComboBox("SQLFileTesterWeb", "PlatformType", null); 
  SQLFileTesterWebPlatformType.setParent(SQLFileTesterWebForm); 
  SQLFileTesterWebPlatformType.setOnChange("manageGenerateShortNames()"); 
%>
<select id="<%=SQLFileTesterWebPlatformType.getId()%>" name="<%=SQLFileTesterWebPlatformType.getName()%>"><% 
  SQLFileTesterWebPlatformType.write(out); 
%> 

                </select>                
              </td>
              <td>
              	<% 
  WebCheckBox SQLFileTesterWebGenerateShortNames =  
     new com.thera.thermfw.web.WebCheckBox("SQLFileTesterWeb", "GenerateShortNames"); 
  SQLFileTesterWebGenerateShortNames.setParent(SQLFileTesterWebForm); 
%>
<input id="<%=SQLFileTesterWebGenerateShortNames.getId()%>" name="<%=SQLFileTesterWebGenerateShortNames.getName()%>" type="checkbox" value="Y"><%
  SQLFileTesterWebGenerateShortNames.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td height="30" nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "SQLFileTesterWeb", "SrcFileName", null); 
   label.setParent(SQLFileTesterWebForm); 
%><label class="<%=label.getClassType()%>" for="SrcFileName"><%label.write(out);%></label><%}%></td>
              <td colspan="2" height="30" width="100%"><input id="SrcFileName" name="SrcFileName" size="75" type="file"></td>
            </tr>
            <tr>
              <td colspan="3"><% 
  WebCheckBox SQLFileTesterWebTryUpdate =  
     new com.thera.thermfw.web.WebCheckBox("SQLFileTesterWeb", "TryUpdate"); 
  SQLFileTesterWebTryUpdate.setParent(SQLFileTesterWebForm); 
%>
<input id="<%=SQLFileTesterWebTryUpdate.getId()%>" name="<%=SQLFileTesterWebTryUpdate.getName()%>" type="checkbox" value="Y"><%
  SQLFileTesterWebTryUpdate.write(out); 
%>
</td>
            </tr>
            <tr>
              <td colspan="3" height="30" width="100%"><button id="Importa" name="Importa" onclick="runSQLFileTester()" type="button"><%= ResourceLoader.getString("com.thera.thermfw.setup.resources.SQLFileTesterWeb", "Importa")%></button></td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td>
          <table cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td>
                <label class="thLabel" id="outputLabel" name="outputLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("com.thera.thermfw.setup.resources.SQLFileTesterWeb", "OutputLabel", null, null, null, null); 
 label.setParent(SQLFileTesterWebForm); 
label.write(out); }%> 
</label>
              </td>
            </tr>
            <tr>
              <td>
                <iframe height="280" id="outputArea" marginheight="0" marginwidth="0" name="outputArea" scrolling="auto" width="100%"></iframe>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <!--<tr>         <td width="100%" height="30"><button name="Clear" id="Clear"/></td>       </tr> -->
      <tr valign="bottom">
        <td style="height:0px">
          <% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(SQLFileTesterWebForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
        </td>
      </tr>
    </table>
  <%
  SQLFileTesterWebForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = SQLFileTesterWebForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", SQLFileTesterWebBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              SQLFileTesterWebForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, SQLFileTesterWebBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, SQLFileTesterWebBODC.getErrorList().getErrors()); 
           if(SQLFileTesterWebBODC.getConflict() != null) 
                conflitPresent = true; 
     } 
     else 
        errors.add(new ErrorMessage("BAS0000010")); 
  } 
  catch(NamingException e) { 
     errorMessage = e.getMessage(); 
     errors.add(new ErrorMessage("CBS000025", errorMessage));  } 
  catch(SQLException e) {
     errorMessage = e.getMessage(); 
     errors.add(new ErrorMessage("BAS0000071", errorMessage));  } 
  catch(Throwable e) {
     e.printStackTrace(Trace.excStream);
  }
  finally 
  {
     if(SQLFileTesterWebBODC != null && !SQLFileTesterWebBODC.close(false)) 
        errors.addAll(0, SQLFileTesterWebBODC.getErrorList().getErrors()); 
     try 
     { 
        se.end(); 
     }
     catch(IllegalArgumentException e) { 
        e.printStackTrace(Trace.excStream); 
     } 
     catch(SQLException e) { 
        e.printStackTrace(Trace.excStream); 
     } 
  } 
  if(!errors.isEmpty())
  { 
      if(!conflitPresent)
  { 
     request.setAttribute("ErrorMessages", errors); 
     String errorPage = SQLFileTesterWebForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", SQLFileTesterWebBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = SQLFileTesterWebForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>

</html>

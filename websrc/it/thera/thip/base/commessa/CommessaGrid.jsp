<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///D:/3rdParty/PantheraSvilL2Panth01/websrc/dtd/xhtml1-transitional.dtd">
<html>
<!-- WIZGEN Therm 2.0.0 as Griglia - multiBrowserGen = true -->
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
  List errors = new ArrayList(); 
  ErrorMessage dolError = null;//Mod.2777 
  WebJSTypeList jsList = new WebJSTypeList(); 
  String errorMessage; 
  WebFormForGrid gridForm =  
     new com.thera.thermfw.web.WebFormForGrid(request, response, "gridForm", "com.thera.thermfw.web.servlet.GridActionAdapter", true, true, true); 
  gridForm.setServletEnvironment(se); 
  gridForm.setJSTypeList(jsList); 
  gridForm.write(out); 
  try 
  {
     se.initialize(request, response); 
     if(se.begin()) 
     { 
        dolError = (ErrorMessage)request.getAttribute(com.thera.thermfw.web.servlet.ShowGrid.DOL_ERROR);//Mod.2777 
        gridForm.outTraceInfo(getClass().getName()); 
        gridForm.writeHeadElements(out); 
%> 

<title>Lista</title>
<% 
  WebSettingBar settBarTB = new com.thera.thermfw.web.WebSettingBar("settBar", "24", "24", "16", "16", "#f7fbfd","#C8D6E1"); 
  settBarTB.setParent(gridForm); 
   request.setAttribute("settingBar", settBarTB); 
%> 
<jsp:include page="/com/thera/thermfw/common/defGridMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="settingBar"/> 
</jsp:include> 
<% 
  settBarTB.write(out); 
  settBarTB.writeFunctions(out, false); 
%> 
<% 
  WebToolBar gridToolbarTB = new com.thera.thermfw.web.WebToolBar("gfToolbardiv", "24", "24", "16", "16", "#f7fbfd","#C8D6E1"); 
  gridToolbarTB.setParent(gridForm); 
   request.setAttribute("toolBar", gridToolbarTB); 
%> 
<jsp:include page="/com/thera/thermfw/common/defGridMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   gridToolbarTB.write(out); 
%> 
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(gridForm); 
   request.setAttribute("menuBar", menuBar); 
%> 
<jsp:include page="/com/thera/thermfw/common/defGridMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="menuBar"/> 
</jsp:include> 
<% 
  menuBar.write(out); 
  menuBar.writeChildren(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=gridForm.getBodyOnBeforeUnload()%>" onload="<%=gridForm.getBodyOnLoad()%>" onunload="<%=gridForm.getBodyOnUnload()%>" rightmargin="0" style="overflow: hidden;" topmargin="0"><%
   gridForm.writeBodyStartElements(out); 
%> 

<table width="100%" height="0" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0">
<% String hdr = gridForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td>
<form action="<%=gridForm.getServlet()%>" method="post" name="defGrid" style="height:100%"><%
  gridForm.writeFormStartElements(out); 
%>
<input name="thTarget" type="hidden" value><input name="thAction" type="hidden" value>
<table border="0" cellpadding="0" cellspacing="0" id="emptyborder" style="border:0px;margin:0px" width="100%">
<tr>
	<td>
		<span class="gridform" id="defGrid"><% menuBar.writeElements(out); %> 
<% gridToolbarTB.writeChildren(out); %> 
<% settBarTB.writeChildren(out); %> 
<% 
   WebGridForm grid = new com.thera.thermfw.web.WebGridForm("", true,true,true, 0, null, null); 
   grid.setParent(gridForm); 
   grid.completeData(); 
   grid.write(out); 
%> 
</span>
		
<%grid.writeImportLibraries(out);%>
<%grid.setHeaderProperties(new String[]{"", "", "", " class=\"header\"", "", "", "", " class=\"header\""});%>
<%grid.setBodyProperties(new String[]{"", "", " class=\"cell\""});%>
<div class="grid-container" id="divGridElement" name="divGridElement">
<div class="grid-header" id="grid-header" name="grid-header">
<div class="grid-header-frozen" id="grid-header-frozen" name="grid-header-frozen">
<%grid.writeFrozenHeaderPart(out);%>
</div>
<div class="grid-header-scrollable" id="grid-header-scrollable" name="grid-header-scrollable">
<%grid.writeScrollableHeaderPart(out);%>
</div>
</div>
<div class="grid-body" id="grid-body" name="grid-body">
<div class="grid-body-frozen" id="grid-body-frozen" name="grid-body-frozen" onscroll="fireFrozenBodyScrolled(event, this)">
<%grid.writeFrozenBodyPart(out);%>
</div>
<div class="grid-body-scrollable" id="grid-body-scrollable" name="grid-body-scrollable" onscroll="fireScrollableBodyScrolled(event, this)">
<%grid.writeScrollableBodyPart(out);%>
</div>
</div>
</div>
		<div align="left">
		<table cellspacing="0" class="footer_table" id="footer" width="100%">
			<tr>
 			    <td align="center" class="footer" id="first" width="26"><%grid.writeFirstButton(out);%></td>
				<td align="center" class="footer" id="prev" width="26"><%grid.writePrevButton(out);%></td>
				<td align="center" class="footer" id="status"><%grid.writePages(out);%></td>
				<td align="center" class="footer" id="page" width="200"><%grid.writeNumberPage(out);%></td>
				<td align="center" class="footer" id="next" width="26"><%grid.writeNextButton(out);%></td>
 			    <td align="center" class="footer" id="last" width="26"><%grid.writeLastButton(out);%></td>				
			</tr>
		</table>
		</div>
	</td>
</tr>
</table>
<%
  gridForm.writeFormEndElements(out); 
%>
<% 
  WebErrorListForGrid errorListForGrid = new WebErrorListForGrid(); 
  errorListForGrid.setParent(gridForm); 
  errorListForGrid.setRowNumber(1); 
  errorListForGrid.write(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = gridForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
        gridForm.writeBodyEndElements(out); 
     } 
     else 
        errors.add(new ErrorMessage("BATCH015")); 
  } 
  catch(NamingException e) { 
     errorMessage = e.getMessage(); 
     errors.add(new ErrorMessage("CBS000025", errorMessage));  } 
  catch(SQLException e) {
     errorMessage = e.getMessage(); 
     errors.add(new ErrorMessage("BAS0000071", errorMessage));  } 
  finally 
  {
     try 
     { 
        se.end(); 
     }
     catch(IllegalArgumentException e) { 
        e.printStackTrace(); 
     } 
     catch(SQLException e) { 
        e.printStackTrace(); 
     } 
  } 
  if(!errors.isEmpty())
  { 
     request.setAttribute("ErrorMessages", errors); 
     String errorPage = gridForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
   } 
  //Mod.2777 
  else if(dolError != null) 
  { 
    String longTextError = dolError.getLongText(); 
%> 
<script language="JavaScript1.2">alert("<%=longTextError%>"); 
</script> 
<% 
   } //fine mod.2777 
%> 
</body>
<% 
  WebScript script_0 =  
   new com.thera.thermfw.web.WebScript(); 
 script_0.setRequest(request); 
 script_0.setSrcAttribute("it/thera/thip/base/commessa/CommessaGrid.js"); 
 script_0.setLanguageAttribute("JavaScript1.2"); 
  script_0.write(out); 
%>
<!--<script language="JavaScript1.2" src="it/thera/thip/base/commessa/CommessaGrid.js"></script>-->
</html>

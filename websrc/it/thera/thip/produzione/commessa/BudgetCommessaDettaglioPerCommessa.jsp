<!DOCTYPE html>
<html>
<%@ page contentType="text/html; charset=Cp1252"%>
<%@ page import=" 
  java.io.*,
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
  com.thera.thermfw.pref.* ,
  org.json.JSONArray,
  org.json.JSONObject,
  it.thera.thip.produzione.commessa.web.*
"%>
<%
String baseURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
String webAppPath = IniFile.getValue("thermfw.ini", "Web", "WebApplicationPath");
String servletPath = IniFile.getValue("thermfw.ini", "Web", "ServletPath");
%>
<head>
<%
ServletEnvironment se = (ServletEnvironment) Factory.createObject("com.thera.thermfw.web.ServletEnvironment");
try {
	se.initialize(request, response);
	if (se.begin()) {
%>
<%=com.thera.thermfw.web.WebJSTypeList.getImportForCSS("thermweb/css/normalize.css", request)%>
<%=com.thera.thermfw.web.WebJSTypeList.getImportForCSS("thermweb/css/therm.css", request)%>

<style>
body{
	margin: 0px;
	background-color:  #E8E8E8;
	background-image:url('./bgForm.gif');
}
form {
	margin:0px;
}
.COM_STYLE{
	font-family: Arial, Helvetica, sans-serif;
	font-size: 14pt;
	font-type:bold;
}
</style>
<script>
webAppPath = "<%=webAppPath%>";
servletPath = "<%=servletPath%>";
</script>
</head>
<body bottommargin="0" leftmargin="0"  rightmargin="0" topmargin="0">
	<form name="mainform" method="post">
		<div id="refreshdiv">
<%
	PrintWriter writer = new PrintWriter(out);
	BudgetCommessaDetPQGrid paramQueryGrid = new BudgetCommessaDetPQGrid(se);
	paramQueryGrid.writeImport(writer);
%>	
			<div class="group-container" style="height: 100%;">
					<% paramQueryGrid.write(writer);%>
			</div>
 		</div>	
	</form>
<%
	}
}
catch (NamingException e) {
	e.printStackTrace(Trace.excStream);
}
catch (SQLException e) {
	e.printStackTrace(Trace.excStream);
}
catch (Throwable e) {
	e.printStackTrace(Trace.excStream);
}
finally {
	try {
		se.end();
	}
	catch (IllegalArgumentException e) {
		e.printStackTrace(Trace.excStream);
	}
	catch (SQLException e) {
		e.printStackTrace(Trace.excStream);
	}
}
%>
</body>
</html>

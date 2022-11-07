<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "..\..\..\..\DTD\tfml.dtd">

<html>
<!-- HANDGEN Therm 2.0.1  multiBrowserGen = true -->
<head>
<title>Error Handler </title>
<%=com.thera.thermfw.web.WebGenerator.getMetaTagEmulate()%>
<base href="http://<%=request.getServerName()%>:<%=request.getServerPort()%>/<%=IniFile.getValue("thermfw.ini","Web","WebApplicationPath")%>/">
</head>
<body onload="closeWindow()">

<FORM  NAME="newForm">
<%@ page contentType="text/html; charset=Cp1252"%>
<%@ page import= "javax.servlet.http.*,
javax.servlet.*,
java.util.*,
java.sql.*,
java.lang.reflect.*,
javax.naming.*,
com.thera.thermfw.ad.*,
com.thera.thermfw.collector.*,
com.thera.thermfw.base.*,
com.thera.thermfw.common.*,
com.thera.thermfw.persist.*,
com.thera.thermfw.web.*,
com.thera.thermfw.web.servlet.*"%>

<SCRIPT language=JavaScript type=text/javascript>
	var thAction = "<%=request.getParameter("thAction")%>";
	var thClassName = "<%=request.getParameter("thClassName")%>";
	var thKey = "<%=WebElement.formatStringForHTML((String)request.getAttribute("RealKey"))%>"; //28161		
	var errViewObj = window.parent.eval(window.parent.errorsViewName);
	var errorsArray = new Array();
	//mod. 780 SL - inizio
	var allComponentsInError = new Array();
	var collectionInError = new Array();
	var collectionInErrorPos = new Array();
	//mod. 780 SL - fine
	var foundErrors = false;
    var numErr = -1;
    var objSavedMessage = "";
    var warningMessage = "";
	var foundErrorsForzabili = false; // Fix 7165
    var codErrorsForced="";

</SCRIPT>
<!-- <script language='JavaScript1.2' type='text/javascript' src='com/thera/thermfw/common/ErrorListHandler.js'></script> Fix 10979 -->

<%=com.thera.thermfw.web.WebJSTypeList.getImportForJSLibrary("it/thera/thip/produzione/commessa/VariaBudgetErrorListHandler.js", request)%><!-- Fix 10979 -->
<%
ServletEnvironment se = (ServletEnvironment)Factory.createObject("com.thera.thermfw.web.ServletEnvironment");
try {
  se.initialize(request, response);
  if (se.begin()) {
    String RES = "com.thera.thermfw.web.resources.web";
	List  errorMessages = (ArrayList)request.getAttribute("ErrorMessages");
    String codErrorStream = null;
	String thTarget = request.getParameter("thTarget");
	int errorIndex = 0;

	ViewSelector viewSelector = (ViewSelector)request.getAttribute(BaseServlet.TH_VIEW_SELECTOR);
	if (viewSelector != null)
	  viewSelector.writeErrorListHandler(out, se);

	boolean foundErrors = false;
	boolean foundErrorsForz = false; // Inizio 7165
	String warningMessage = "";
	int num = 1;
	if (errorMessages.size() > 0) {
	      ClassADCollection cadc = ClassADCollectionManager.collectionWithName(request.getParameter("thClassName"));
	      boolean first = true;
		  Iterator it = errorMessages.iterator();
		  Vector allCompInError = new Vector();
		  Vector collectionInError = new Vector();
		  Vector collectionInErrorPos = new Vector();

		 //8253 - PJ - inizio
		 boolean warningsInList = false;
		 String wl = (String) request.getAttribute("warningsInList");
		 if (wl == null)
		 	wl = request.getParameter("warningsInList");
		 if (wl != null && wl.equalsIgnoreCase("Y"))
		 	warningsInList = true;
		 //8253 - PJ - fine

		 while (it.hasNext()) {
			ErrorMessage em = (ErrorMessage)it.next();
			String errId = em.getId();
			String errShortText = WebElement.formatStringForHTML(em.getText());
			String errLongText = WebElement.formatStringForHTML(em.getLongText());
			String errLabel = WebElement.formatStringForHTML(em.getAttOrGroupLabel());
			String errGrpName = WebElement.formatStringForHTML(em.getAttOrGroupName());// Fix 7165
			String errSeverity = String.valueOf(em.getSeverity());
			boolean isForceable = em.getForceable();
			if(errLabel != null && !errLabel.equals(""))
				errShortText = errLabel + " - " + errShortText;
			Vector idCompInError = new Vector();
			Vector components = em.getComponents();
			// Inizio 7165
			if(em.getSeverity()==ErrorMessage.ERROR){
				if (isForceable){
				  foundErrorsForz = true;
				  codErrorStream = BODataCollector.getCodStreamError(errId,errGrpName,codErrorStream);
				}
				else
				  foundErrors=true;
		    }
			// Fine 7165
			else
			{
				//8253 - PJ - inizio
				if (warningsInList)
					foundErrors = true;
				//8253 - PJ - fine

				warningMessage += "\n" + num + ") ";
				warningMessage += em.getLongText();
				num++;
			}
			if(components != null && components.size() > 0)
			{
				BODataCollector coll = ((BaseBOComponent)components.firstElement()).getComponentManager().getBODataCollector();
				if(coll.getOwnerCollectionDC() != null)
				{
					String collName = coll.getOwnerCollectionDC().getKeyForBaseComponentsCollection();
					collectionInError.addElement(collName);
					String pos = "" + coll.getPositionInOwnerCollectionDC();
					collectionInErrorPos.addElement(pos);
				}
				else
				{
					boolean f = true;
					Iterator i1 = components.iterator();
					while(i1.hasNext())
					{
						BaseBOComponent bc = (BaseBOComponent)i1.next();
						String classADName = bc.getComponentManager().getKeyForBaseComponentsCollection();
						if (f)
						{
							f = false;
							idCompInError.addElement(classADName);
						}
						allCompInError.addElement(classADName);
					}
				}
			}
%>

<SCRIPT language=JavaScript type=text/javascript>
	var idCompInError = new Array();
	numErr = <%=errorMessages.size()%>;
	foundErrors = <%=foundErrors%>;
	// Inizio 7165
	foundErrorsForzabili = <%=foundErrorsForz%>;
	codErrorsForced = "<%=codErrorStream%>";
	// Fine 7165
	objSavedMessage = "<%=ResourceLoader.getString(RES, "ObjSaved")%>";
<%
			Iterator idCompIter = idCompInError.iterator();
			while(idCompIter.hasNext())
			{
				String curIdCompInErr = (String)idCompIter.next();
%>
	idCompInError[idCompInError.length]="<%=curIdCompInErr%>";
<%
			}
%>
	var singleError = new Array("<%=errId%>", "<%=errShortText%>", idCompInError, "<%=errSeverity%>","<%=errLongText%>", <%=isForceable%>,"<%=errGrpName%>","<%=errLabel%>");
	errorsArray[errorsArray.length] = singleError;
</SCRIPT>

<%
			errorIndex++;
		}
%>

<SCRIPT language=JavaScript type=text/javascript>

<%
		Iterator allCompIter = allCompInError.iterator();
		while(allCompIter.hasNext())
		{
			String curCompInErr = (String)allCompIter.next();
%>

	allComponentsInError[allComponentsInError.length]="<%=curCompInErr%>";

<%
		}
%>


<%
			Iterator collectionIter = collectionInError.iterator();
			Iterator collectionPosIter = collectionInErrorPos.iterator();
			while(collectionIter.hasNext())
			{
				String curCollInErr = (String)collectionIter.next();
				String curCollInErrPos = (String)collectionPosIter.next();
%>

	collectionInError[collectionInError.length]="<%=curCollInErr%>";
	collectionInErrorPos[collectionInErrorPos.length]="<%=curCollInErrPos%>";

<%
			}
%>

</SCRIPT>

<%
			if (!foundErrors)
			{
				warningMessage = ResourceLoader.getString(RES, "WarningMsg") + warningMessage;
				warningMessage = WebElement.formatStringForHTML(warningMessage);
			}
			else
				warningMessage = "";
		}
		else
		{
%>

	OK<BR>

<%
	}
%>
<SCRIPT language=JavaScript type=text/javascript>
warningMessage = "<%=warningMessage%>";
</SCRIPT>
<%
  }
} catch(NamingException e) {
  e.printStackTrace();
} catch(SQLException e) {
  e.printStackTrace();
} finally {
  try {
    se.end();
  } catch(IllegalArgumentException e) {
    e.printStackTrace();
  } catch(SQLException e) {
    e.printStackTrace();
  }
}
%>
</FORM>
</body>
</html>

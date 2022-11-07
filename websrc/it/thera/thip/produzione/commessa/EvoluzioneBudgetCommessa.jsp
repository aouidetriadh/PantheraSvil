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
  BODataCollector EvoluzioneBudgetCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm EvoluzioneBudgetCommessaForm =  
     new com.thera.thermfw.web.WebForm(request, response, "EvoluzioneBudgetCommessaForm", "EvoluzioneBudgetCommessa", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, false, false, true, true, null, 1, true, "it/thera/thip/produzione/commessa/EvoluzioneBudgetCommessa.js"); 
  EvoluzioneBudgetCommessaForm.setServletEnvironment(se); 
  EvoluzioneBudgetCommessaForm.setJSTypeList(jsList); 
  EvoluzioneBudgetCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  EvoluzioneBudgetCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  EvoluzioneBudgetCommessaForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.EvoluzioneBudgetCMMWebFormModifier"); 
  EvoluzioneBudgetCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = EvoluzioneBudgetCommessaForm.getMode(); 
  String key = EvoluzioneBudgetCommessaForm.getKey(); 
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
        EvoluzioneBudgetCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = EvoluzioneBudgetCommessaForm.findBODataCollectorName(); 
                EvoluzioneBudgetCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (EvoluzioneBudgetCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)EvoluzioneBudgetCommessaBODC).setServletEnvironment(se); 
        EvoluzioneBudgetCommessaBODC.initialize("EvoluzioneBudgetCommessa", true, 1); 
        EvoluzioneBudgetCommessaForm.setBODataCollector(EvoluzioneBudgetCommessaBODC); 
        int rcBODC = EvoluzioneBudgetCommessaForm.initSecurityServices(); 
        mode = EvoluzioneBudgetCommessaForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           EvoluzioneBudgetCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = EvoluzioneBudgetCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              EvoluzioneBudgetCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Evoluzione budget commessa</title>
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=EvoluzioneBudgetCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=EvoluzioneBudgetCommessaForm.getBodyOnLoad()%>" onunload="<%=EvoluzioneBudgetCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   EvoluzioneBudgetCommessaForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = EvoluzioneBudgetCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", EvoluzioneBudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=EvoluzioneBudgetCommessaForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  EvoluzioneBudgetCommessaForm.writeFormStartElements(out); 
%>

		<table border="0" cellpadding="2" cellspacing="2" height="100%" width="100%">
			<tr>
				<td height="90px" valign="top">
					<table style="margin: 0 0 0 0 ;" width="90%">
						<tr>
							<td nowrap width="110px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EvoluzioneBudgetCommessa", "IdCommessa", null); 
   label.setParent(EvoluzioneBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
							<td colspan="3" nowrap><% 
  WebMultiSearchForm EvoluzioneBudgetCommessaCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("EvoluzioneBudgetCommessa", "Commessa", false, false, true, 1, null, null); 
  EvoluzioneBudgetCommessaCommessa.setParent(EvoluzioneBudgetCommessaForm); 
  EvoluzioneBudgetCommessaCommessa.setOnKeyChange("completaDati()"); 
  EvoluzioneBudgetCommessaCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  EvoluzioneBudgetCommessaCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
						</tr>
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EvoluzioneBudgetCommessa", "DataInizio", null); 
   label.setParent(EvoluzioneBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataInizio"><%label.write(out);%></label><%}%></td>
							<td colspan="3" nowrap>
								<% 
  WebTextInput EvoluzioneBudgetCommessaDataInizio =  
     new com.thera.thermfw.web.WebTextInput("EvoluzioneBudgetCommessa", "DataInizio"); 
  EvoluzioneBudgetCommessaDataInizio.setShowCalendarBtn(true); 
  EvoluzioneBudgetCommessaDataInizio.setParent(EvoluzioneBudgetCommessaForm); 
%>
<input class="<%=EvoluzioneBudgetCommessaDataInizio.getClassType()%>" id="<%=EvoluzioneBudgetCommessaDataInizio.getId()%>" maxlength="<%=EvoluzioneBudgetCommessaDataInizio.getMaxLength()%>" name="<%=EvoluzioneBudgetCommessaDataInizio.getName()%>" size="<%=EvoluzioneBudgetCommessaDataInizio.getSize()%>"><% 
  EvoluzioneBudgetCommessaDataInizio.write(out); 
%>

								<% 
  WebTextInput EvoluzioneBudgetCommessaDataFine =  
     new com.thera.thermfw.web.WebTextInput("EvoluzioneBudgetCommessa", "DataFine"); 
  EvoluzioneBudgetCommessaDataFine.setShowCalendarBtn(true); 
  EvoluzioneBudgetCommessaDataFine.setParent(EvoluzioneBudgetCommessaForm); 
%>
<input class="<%=EvoluzioneBudgetCommessaDataFine.getClassType()%>" id="<%=EvoluzioneBudgetCommessaDataFine.getId()%>" maxlength="<%=EvoluzioneBudgetCommessaDataFine.getMaxLength()%>" name="<%=EvoluzioneBudgetCommessaDataFine.getName()%>" size="<%=EvoluzioneBudgetCommessaDataFine.getSize()%>"><% 
  EvoluzioneBudgetCommessaDataFine.write(out); 
%>

							</td>				
						</tr>			
						<tr>			
							<td colspan="4" height="25px" valign="top">
								<table style="margin-left: 5px;">
									<tr>
										<td style="white-space:nowrap;" width="50px"><% 
  WebCheckBox EvoluzioneBudgetCommessaTotali =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneBudgetCommessa", "Totali"); 
  EvoluzioneBudgetCommessaTotali.setParent(EvoluzioneBudgetCommessaForm); 
%>
<input id="<%=EvoluzioneBudgetCommessaTotali.getId()%>" name="<%=EvoluzioneBudgetCommessaTotali.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneBudgetCommessaTotali.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneBudgetCommessaDettagliCommessa =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneBudgetCommessa", "DettagliCommessa"); 
  EvoluzioneBudgetCommessaDettagliCommessa.setParent(EvoluzioneBudgetCommessaForm); 
%>
<input id="<%=EvoluzioneBudgetCommessaDettagliCommessa.getId()%>" name="<%=EvoluzioneBudgetCommessaDettagliCommessa.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneBudgetCommessaDettagliCommessa.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneBudgetCommessaDettagliSottoCommesse =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneBudgetCommessa", "DettagliSottoCommesse"); 
  EvoluzioneBudgetCommessaDettagliSottoCommesse.setParent(EvoluzioneBudgetCommessaForm); 
%>
<input id="<%=EvoluzioneBudgetCommessaDettagliSottoCommesse.getId()%>" name="<%=EvoluzioneBudgetCommessaDettagliSottoCommesse.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneBudgetCommessaDettagliSottoCommesse.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneBudgetCommessaComponentiPropri =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneBudgetCommessa", "ComponentiPropri"); 
  EvoluzioneBudgetCommessaComponentiPropri.setParent(EvoluzioneBudgetCommessaForm); 
%>
<input id="<%=EvoluzioneBudgetCommessaComponentiPropri.getId()%>" name="<%=EvoluzioneBudgetCommessaComponentiPropri.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneBudgetCommessaComponentiPropri.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneBudgetCommessaSoloComponentiValorizzate =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneBudgetCommessa", "SoloComponentiValorizzate"); 
  EvoluzioneBudgetCommessaSoloComponentiValorizzate.setParent(EvoluzioneBudgetCommessaForm); 
%>
<input id="<%=EvoluzioneBudgetCommessaSoloComponentiValorizzate.getId()%>" name="<%=EvoluzioneBudgetCommessaSoloComponentiValorizzate.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneBudgetCommessaSoloComponentiValorizzate.write(out); 
%>
</td>
										<td><% 
  MDVButton newMDV =  
   new com.thera.thermfw.web.MDVButton("SaveScreenData", "com.thera.thermfw.web.resources.web", "SaveScreenData", "thermweb/image/gui/SaveScreenData.gif", null, null); 
  newMDV.setParent(EvoluzioneBudgetCommessaForm); 
  newMDV.setImageWidth("16"); 
  newMDV.setImageHeight("16"); 
  newMDV.write(out); 
%>
<!--<span class="mdvbutton" id="newMDV" name="newMDV"></span>--></td>
										<td>
											<button id="thApplicaScelte" name="thApplicaScelte" onclick="carica()" style="width:26px;height:26px;align:top" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.EvoluzioneBudgetCommessa", "ApplicaScelte")%>" type="button"><img border="0" height="16px" src="thermweb/image/gui/cnr/Run.gif" width="16px">
											<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.EvoluzioneBudgetCommessa", "ApplicaScelteID")%></button>
										</td>											
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<iframe height="100%" id="EvoluzioneBudgetCommessaDettaglio" name="EvoluzioneBudgetCommessaDettaglio" src style="border: 0px solid black;margin:0 5px 10px 5px;height: calc(100% - 15px);width: calc(100% - 10px);" width="100%"></iframe>
				</td>
			</tr>
			<tr><td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(EvoluzioneBudgetCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td></tr>			
		</table>
	<%
  EvoluzioneBudgetCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = EvoluzioneBudgetCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", EvoluzioneBudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              EvoluzioneBudgetCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, EvoluzioneBudgetCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, EvoluzioneBudgetCommessaBODC.getErrorList().getErrors()); 
           if(EvoluzioneBudgetCommessaBODC.getConflict() != null) 
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
     if(EvoluzioneBudgetCommessaBODC != null && !EvoluzioneBudgetCommessaBODC.close(false)) 
        errors.addAll(0, EvoluzioneBudgetCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = EvoluzioneBudgetCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", EvoluzioneBudgetCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = EvoluzioneBudgetCommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

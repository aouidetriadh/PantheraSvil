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
  BODataCollector EvoluzioneConsunCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm EvoluzioneConsunCommessaForm =  
     new com.thera.thermfw.web.WebForm(request, response, "EvoluzioneConsunCommessaForm", "EvoluzioneConsunCommessa", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, false, false, true, true, null, 1, true, "it/thera/thip/produzione/commessa/EvoluzioneConsuntivoCommessa.js"); 
  EvoluzioneConsunCommessaForm.setServletEnvironment(se); 
  EvoluzioneConsunCommessaForm.setJSTypeList(jsList); 
  EvoluzioneConsunCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  EvoluzioneConsunCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  EvoluzioneConsunCommessaForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.EvoluzioneConsuntivoCMMWebFormModifier"); 
  EvoluzioneConsunCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = EvoluzioneConsunCommessaForm.getMode(); 
  String key = EvoluzioneConsunCommessaForm.getKey(); 
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
        EvoluzioneConsunCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = EvoluzioneConsunCommessaForm.findBODataCollectorName(); 
                EvoluzioneConsunCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (EvoluzioneConsunCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)EvoluzioneConsunCommessaBODC).setServletEnvironment(se); 
        EvoluzioneConsunCommessaBODC.initialize("EvoluzioneConsunCommessa", true, 1); 
        EvoluzioneConsunCommessaForm.setBODataCollector(EvoluzioneConsunCommessaBODC); 
        int rcBODC = EvoluzioneConsunCommessaForm.initSecurityServices(); 
        mode = EvoluzioneConsunCommessaForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           EvoluzioneConsunCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = EvoluzioneConsunCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              EvoluzioneConsunCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Evoluzione consuntivo commessa</title>
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=EvoluzioneConsunCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=EvoluzioneConsunCommessaForm.getBodyOnLoad()%>" onunload="<%=EvoluzioneConsunCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   EvoluzioneConsunCommessaForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = EvoluzioneConsunCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", EvoluzioneConsunCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=EvoluzioneConsunCommessaForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  EvoluzioneConsunCommessaForm.writeFormStartElements(out); 
%>

		<table border="0" cellpadding="2" cellspacing="2" height="100%" width="100%">
			<tr>
				<td height="90px" valign="top">
					<table style="margin: 0 0 0 0 ;" width="90%">
						<tr>
							<td nowrap width="110px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EvoluzioneConsunCommessa", "IdCommessa", null); 
   label.setParent(EvoluzioneConsunCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
							<td colspan="3" nowrap><% 
  WebMultiSearchForm EvoluzioneConsunCommessaCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("EvoluzioneConsunCommessa", "Commessa", false, false, true, 1, null, null); 
  EvoluzioneConsunCommessaCommessa.setParent(EvoluzioneConsunCommessaForm); 
  EvoluzioneConsunCommessaCommessa.setOnKeyChange("completaDati()"); 
  EvoluzioneConsunCommessaCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  EvoluzioneConsunCommessaCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
						</tr>
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EvoluzioneConsunCommessa", "DataInizio", null); 
   label.setParent(EvoluzioneConsunCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataInizio"><%label.write(out);%></label><%}%></td>
							<td colspan="3" nowrap>
								<% 
  WebTextInput EvoluzioneConsunCommessaDataInizio =  
     new com.thera.thermfw.web.WebTextInput("EvoluzioneConsunCommessa", "DataInizio"); 
  EvoluzioneConsunCommessaDataInizio.setShowCalendarBtn(true); 
  EvoluzioneConsunCommessaDataInizio.setParent(EvoluzioneConsunCommessaForm); 
%>
<input class="<%=EvoluzioneConsunCommessaDataInizio.getClassType()%>" id="<%=EvoluzioneConsunCommessaDataInizio.getId()%>" maxlength="<%=EvoluzioneConsunCommessaDataInizio.getMaxLength()%>" name="<%=EvoluzioneConsunCommessaDataInizio.getName()%>" size="<%=EvoluzioneConsunCommessaDataInizio.getSize()%>"><% 
  EvoluzioneConsunCommessaDataInizio.write(out); 
%>

								<% 
  WebTextInput EvoluzioneConsunCommessaDataFine =  
     new com.thera.thermfw.web.WebTextInput("EvoluzioneConsunCommessa", "DataFine"); 
  EvoluzioneConsunCommessaDataFine.setShowCalendarBtn(true); 
  EvoluzioneConsunCommessaDataFine.setParent(EvoluzioneConsunCommessaForm); 
%>
<input class="<%=EvoluzioneConsunCommessaDataFine.getClassType()%>" id="<%=EvoluzioneConsunCommessaDataFine.getId()%>" maxlength="<%=EvoluzioneConsunCommessaDataFine.getMaxLength()%>" name="<%=EvoluzioneConsunCommessaDataFine.getName()%>" size="<%=EvoluzioneConsunCommessaDataFine.getSize()%>"><% 
  EvoluzioneConsunCommessaDataFine.write(out); 
%>

							</td>				
						</tr>			
						<tr>			
							<td colspan="4" height="25px" valign="top">
								<table style="margin-left: 5px;">
									<tr>
										<td style="white-space:nowrap;" width="50px"><% 
  WebCheckBox EvoluzioneConsunCommessaTotali =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneConsunCommessa", "Totali"); 
  EvoluzioneConsunCommessaTotali.setParent(EvoluzioneConsunCommessaForm); 
%>
<input id="<%=EvoluzioneConsunCommessaTotali.getId()%>" name="<%=EvoluzioneConsunCommessaTotali.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneConsunCommessaTotali.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneConsunCommessaDettagliCommessa =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneConsunCommessa", "DettagliCommessa"); 
  EvoluzioneConsunCommessaDettagliCommessa.setParent(EvoluzioneConsunCommessaForm); 
%>
<input id="<%=EvoluzioneConsunCommessaDettagliCommessa.getId()%>" name="<%=EvoluzioneConsunCommessaDettagliCommessa.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneConsunCommessaDettagliCommessa.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneConsunCommessaDettagliSottoCommesse =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneConsunCommessa", "DettagliSottoCommesse"); 
  EvoluzioneConsunCommessaDettagliSottoCommesse.setParent(EvoluzioneConsunCommessaForm); 
%>
<input id="<%=EvoluzioneConsunCommessaDettagliSottoCommesse.getId()%>" name="<%=EvoluzioneConsunCommessaDettagliSottoCommesse.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneConsunCommessaDettagliSottoCommesse.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneConsunCommessaComponentiPropri =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneConsunCommessa", "ComponentiPropri"); 
  EvoluzioneConsunCommessaComponentiPropri.setParent(EvoluzioneConsunCommessaForm); 
%>
<input id="<%=EvoluzioneConsunCommessaComponentiPropri.getId()%>" name="<%=EvoluzioneConsunCommessaComponentiPropri.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneConsunCommessaComponentiPropri.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox EvoluzioneConsunCommessaSoloComponentiValorizzate =  
     new com.thera.thermfw.web.WebCheckBox("EvoluzioneConsunCommessa", "SoloComponentiValorizzate"); 
  EvoluzioneConsunCommessaSoloComponentiValorizzate.setParent(EvoluzioneConsunCommessaForm); 
%>
<input id="<%=EvoluzioneConsunCommessaSoloComponentiValorizzate.getId()%>" name="<%=EvoluzioneConsunCommessaSoloComponentiValorizzate.getName()%>" type="checkbox" value="Y"><%
  EvoluzioneConsunCommessaSoloComponentiValorizzate.write(out); 
%>
</td>
										<td><% 
  MDVButton newMDV =  
   new com.thera.thermfw.web.MDVButton("SaveScreenData", "com.thera.thermfw.web.resources.web", "SaveScreenData", "thermweb/image/gui/SaveScreenData.gif", null, null); 
  newMDV.setParent(EvoluzioneConsunCommessaForm); 
  newMDV.setImageWidth("16"); 
  newMDV.setImageHeight("16"); 
  newMDV.write(out); 
%>
<!--<span class="mdvbutton" id="newMDV" name="newMDV"></span>--></td>										
										<td>
											<button id="thApplicaScelte" name="thApplicaScelte" onclick="carica()" style="width:26px;height:26px;align:top" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.EvoluzioneConsuntivoCommessa", "ApplicaScelte")%>" type="button"><img border="0" height="16px" src="thermweb/image/gui/cnr/Run.gif" width="16px">
											<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.EvoluzioneConsuntivoCommessa", "ApplicaScelteID")%></button>
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
					<iframe height="100%" id="EvoluzioneConsuntivoCommessaDettaglio" name="EvoluzioneConsuntivoCommessaDettaglio" src style="border: 0px solid black;margin:0 5px 10px 5px;height: calc(100% - 15px);width: calc(100% - 10px);" width="100%"></iframe>
				</td>
			</tr>
			<tr><td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(EvoluzioneConsunCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td></tr>			
		</table>
	<%
  EvoluzioneConsunCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = EvoluzioneConsunCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", EvoluzioneConsunCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              EvoluzioneConsunCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, EvoluzioneConsunCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, EvoluzioneConsunCommessaBODC.getErrorList().getErrors()); 
           if(EvoluzioneConsunCommessaBODC.getConflict() != null) 
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
     if(EvoluzioneConsunCommessaBODC != null && !EvoluzioneConsunCommessaBODC.close(false)) 
        errors.addAll(0, EvoluzioneConsunCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = EvoluzioneConsunCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", EvoluzioneConsunCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = EvoluzioneConsunCommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

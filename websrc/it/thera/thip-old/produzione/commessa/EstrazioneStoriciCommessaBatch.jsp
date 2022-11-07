<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///D:/3rdParty/PantheraSvil/websrc/dtd/xhtml1-transitional.dtd">
<html>
<!-- WIZGEN Therm 2.0.0 as Batch form - multiBrowserGen = true -->
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
  BODataCollector EstrazioneStoriciCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm EstrazioneStoriciCommessaForm =  
     new com.thera.thermfw.web.WebFormForBatchForm(request, response, "EstrazioneStoriciCommessaForm", "EstrazioneStoriciCommessa", null, "com.thera.thermfw.batch.web.BatchFormActionAdapter", false, false, true, true, true, true, null, 1, true, "it/thera/thip/produzione/commessa/EstrazioneStoriciCommessaBatch.js"); 
  EstrazioneStoriciCommessaForm.setServletEnvironment(se); 
  EstrazioneStoriciCommessaForm.setJSTypeList(jsList); 
  EstrazioneStoriciCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  EstrazioneStoriciCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ((WebFormForBatchForm)  EstrazioneStoriciCommessaForm).setGenerateSSDEnabled(true); 
  EstrazioneStoriciCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = EstrazioneStoriciCommessaForm.getMode(); 
  String key = EstrazioneStoriciCommessaForm.getKey(); 
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
        EstrazioneStoriciCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = EstrazioneStoriciCommessaForm.findBODataCollectorName(); 
				 EstrazioneStoriciCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (EstrazioneStoriciCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)EstrazioneStoriciCommessaBODC).setServletEnvironment(se); 
        EstrazioneStoriciCommessaBODC.initialize("EstrazioneStoriciCommessa", true, 1); 
        int rcBODC; 
        if (EstrazioneStoriciCommessaBODC.getBo() instanceof BatchRunnable) 
          rcBODC = EstrazioneStoriciCommessaBODC.initSecurityServices("RUN", mode, true, false, true); 
        else 
          rcBODC = EstrazioneStoriciCommessaBODC.initSecurityServices(mode, true, true, true); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           EstrazioneStoriciCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = EstrazioneStoriciCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              EstrazioneStoriciCommessaForm.setBODataCollector(EstrazioneStoriciCommessaBODC); 
              EstrazioneStoriciCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Estrazione storici commessa</title>
	<style>
	#myTabbedGeneraleTab_PAGE table{
		width: 90%;
	}
	#myTabbedGeneraleTab_PAGE td:FIRST-OF-TYPE{
		width: 160px;
	}
	</style>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(EstrazioneStoriciCommessaForm); 
   request.setAttribute("menuBar", menuBar); 
%> 
<jsp:include page="/com/thera/thermfw/batch/PrintRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="menuBar"/> 
</jsp:include> 
<% 
  menuBar.write(out); 
  menuBar.writeChildren(out); 
%> 
<% 
  WebToolBar myToolBarTB = new com.thera.thermfw.web.WebToolBar("myToolBar", "24", "24", "16", "16", "#f7fbfd","#C8D6E1"); 
  myToolBarTB.setParent(EstrazioneStoriciCommessaForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/com/thera/thermfw/batch/PrintRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=EstrazioneStoriciCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=EstrazioneStoriciCommessaForm.getBodyOnLoad()%>" onunload="<%=EstrazioneStoriciCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   EstrazioneStoriciCommessaForm.writeBodyStartElements(out); 
%> 


<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = EstrazioneStoriciCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", EstrazioneStoriciCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=EstrazioneStoriciCommessaForm.getServlet()%>" method="post" name="EstrazioneStoriciCommessaBatch" style="height:100%"><%
  EstrazioneStoriciCommessaForm.writeFormStartElements(out); 
%>

	<table cellpadding="2" cellspacing="0" height="100%" width="100%">
		<tr>
			<td style="height:0">
				<% menuBar.writeElements(out); %> 

			</td>
		</tr>
		<tr>
			<td style="height:0">
				<% myToolBarTB.writeChildren(out); %> 

			</td>
		</tr>
		<tr>
			<td height="100%">
				<!--<span class="tabbed" id="myTabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed myTabbed = new com.thera.thermfw.web.WebTabbed("myTabbed", "100%", "100%"); 
  myTabbed.setParent(EstrazioneStoriciCommessaForm); 
 myTabbed.addTab("GeneraleTab", "it.thera.thip.produzione.commessa.resources.EstrazioneStoriciCommessaBatch", "GeneraleTab", "EstrazioneStoriciCommessa", null, null, null, null); 
 myTabbed.addTab("FiltroTab", "it.thera.thip.produzione.commessa.resources.EstrazioneStoriciCommessaBatch", "FiltroTab", "EstrazioneStoriciCommessa", null, null, null, null); 
  myTabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
					<div class="tabbed_page" id="<%=myTabbed.getTabPageId("GeneraleTab")%>" style="width:100%;height:100%;overflow:auto;"><% myTabbed.startTab("GeneraleTab"); %>
						<table cellpadding="2" cellspacing="2" style="margin: 7 7 7 7;">
							<tr style="display: none">
								<td>
									<input id="thReportId" name="thReportId" type="hidden">
								</td>
								<td>
									<% 
  WebTextInput EstrazioneStoriciCommessaIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("EstrazioneStoriciCommessa", "IdAzienda"); 
  EstrazioneStoriciCommessaIdAzienda.setParent(EstrazioneStoriciCommessaForm); 
%>
<input class="<%=EstrazioneStoriciCommessaIdAzienda.getClassType()%>" id="<%=EstrazioneStoriciCommessaIdAzienda.getId()%>" maxlength="<%=EstrazioneStoriciCommessaIdAzienda.getMaxLength()%>" name="<%=EstrazioneStoriciCommessaIdAzienda.getName()%>" size="<%=EstrazioneStoriciCommessaIdAzienda.getSize()%>" type="hidden"><% 
  EstrazioneStoriciCommessaIdAzienda.write(out); 
%>

								</td>
							</tr>
							<tr>
								<td colspan="4" width="100%">
									<fieldset>
										<legend><label class="thLabel" id="SorgentiLbl">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.produzione.commessa.resources.EstrazioneStoriciCommessaBatch", "LblSorgenti", null, null, null, null); 
 label.setParent(EstrazioneStoriciCommessaForm); 
label.write(out); }%> 
</label></legend>
										<table width="100%">
											<tr>
												<td></td>
												<td>
													<% 
  WebCheckBox EstrazioneStoriciCommessaEstrarrePeriodiDefinitivi =  
     new com.thera.thermfw.web.WebCheckBox("EstrazioneStoriciCommessa", "EstrarrePeriodiDefinitivi"); 
  EstrazioneStoriciCommessaEstrarrePeriodiDefinitivi.setParent(EstrazioneStoriciCommessaForm); 
%>
<input id="<%=EstrazioneStoriciCommessaEstrarrePeriodiDefinitivi.getId()%>" name="<%=EstrazioneStoriciCommessaEstrarrePeriodiDefinitivi.getName()%>" type="checkbox" value="Y"><%
  EstrazioneStoriciCommessaEstrarrePeriodiDefinitivi.write(out); 
%>

												</td>
											</tr>
											<tr>
												<td></td>
												<td>
													<% 
  WebCheckBox EstrazioneStoriciCommessaCommesseProvvisorie =  
     new com.thera.thermfw.web.WebCheckBox("EstrazioneStoriciCommessa", "CommesseProvvisorie"); 
  EstrazioneStoriciCommessaCommesseProvvisorie.setParent(EstrazioneStoriciCommessaForm); 
%>
<input id="<%=EstrazioneStoriciCommessaCommesseProvvisorie.getId()%>" name="<%=EstrazioneStoriciCommessaCommesseProvvisorie.getName()%>" type="checkbox" value="Y"><%
  EstrazioneStoriciCommessaCommesseProvvisorie.write(out); 
%>

												</td>
											</tr>
											<tr>
												<td></td>
												<td>
													<% 
  WebCheckBox EstrazioneStoriciCommessaStoriciNonCommessa =  
     new com.thera.thermfw.web.WebCheckBox("EstrazioneStoriciCommessa", "StoriciNonCommessa"); 
  EstrazioneStoriciCommessaStoriciNonCommessa.setParent(EstrazioneStoriciCommessaForm); 
%>
<input id="<%=EstrazioneStoriciCommessaStoriciNonCommessa.getId()%>" name="<%=EstrazioneStoriciCommessaStoriciNonCommessa.getName()%>" type="checkbox" value="Y"><%
  EstrazioneStoriciCommessaStoriciNonCommessa.write(out); 
%>

												</td>
											</tr>																		
										</table>
									</fieldset>
								</td>
							</tr>
							<tr>
								<td colspan="2" width="100%">
									<fieldset>
										<legend><label class="thLabel" id="CostiMancantiLbl">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.produzione.commessa.resources.EstrazioneStoriciCommessaBatch", "LblCostiMancanti", null, null, null, null); 
 label.setParent(EstrazioneStoriciCommessaForm); 
label.write(out); }%> 
</label></legend>
										<table width="100%">
											<tr>
												<td><label class="thLabel" id="ArticoliLbl">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.produzione.commessa.resources.EstrazioneStoriciCommessaBatch", "LblArticoli", null, null, null, null); 
 label.setParent(EstrazioneStoriciCommessaForm); 
label.write(out); }%> 
</label></td>
												<td>
													<% 
  WebCheckBox EstrazioneStoriciCommessaCostiArticoloDaDocumento =  
     new com.thera.thermfw.web.WebCheckBox("EstrazioneStoriciCommessa", "CostiArticoloDaDocumento"); 
  EstrazioneStoriciCommessaCostiArticoloDaDocumento.setParent(EstrazioneStoriciCommessaForm); 
  EstrazioneStoriciCommessaCostiArticoloDaDocumento.setOnClick("checkCostiArticoloDaDocumento()"); 
%>
<input id="<%=EstrazioneStoriciCommessaCostiArticoloDaDocumento.getId()%>" name="<%=EstrazioneStoriciCommessaCostiArticoloDaDocumento.getName()%>" type="checkbox" value="Y"><%
  EstrazioneStoriciCommessaCostiArticoloDaDocumento.write(out); 
%>

												</td>
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EstrazioneStoriciCommessa", "OrdineRecArticolo", null); 
   label.setParent(EstrazioneStoriciCommessaForm); 
%><label class="<%=label.getClassType()%>" for="OrdineRecArticolo"><%label.write(out);%></label><%}%>
												</td>
												<td>
													<% 
  WebComboBox EstrazioneStoriciCommessaOrdineRecArticolo =  
     new com.thera.thermfw.web.WebComboBox("EstrazioneStoriciCommessa", "OrdineRecArticolo", null); 
  EstrazioneStoriciCommessaOrdineRecArticolo.setParent(EstrazioneStoriciCommessaForm); 
%>
<select id="<%=EstrazioneStoriciCommessaOrdineRecArticolo.getId()%>" name="<%=EstrazioneStoriciCommessaOrdineRecArticolo.getName()%>"><% 
  EstrazioneStoriciCommessaOrdineRecArticolo.write(out); 
%> 
</select>
												</td>
											</tr>
											<tr>
												<td><label class="thLabel" id="RisorseLbl">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.produzione.commessa.resources.EstrazioneStoriciCommessaBatch", "LblRisorse", null, null, null, null); 
 label.setParent(EstrazioneStoriciCommessaForm); 
label.write(out); }%> 
</label></td>
												<td>
													<% 
  WebCheckBox EstrazioneStoriciCommessaCostiRisorsaDaDocumento =  
     new com.thera.thermfw.web.WebCheckBox("EstrazioneStoriciCommessa", "CostiRisorsaDaDocumento"); 
  EstrazioneStoriciCommessaCostiRisorsaDaDocumento.setParent(EstrazioneStoriciCommessaForm); 
  EstrazioneStoriciCommessaCostiRisorsaDaDocumento.setOnClick("checkCostiRisorsaDaDocumento()"); 
%>
<input id="<%=EstrazioneStoriciCommessaCostiRisorsaDaDocumento.getId()%>" name="<%=EstrazioneStoriciCommessaCostiRisorsaDaDocumento.getName()%>" type="checkbox" value="Y"><%
  EstrazioneStoriciCommessaCostiRisorsaDaDocumento.write(out); 
%>

												</td>
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EstrazioneStoriciCommessa", "OrdineRecRisorsa", null); 
   label.setParent(EstrazioneStoriciCommessaForm); 
%><label class="<%=label.getClassType()%>" for="OrdineRecRisorsa"><%label.write(out);%></label><%}%>
												</td>
												<td>
													<% 
  WebComboBox EstrazioneStoriciCommessaOrdineRecRisorsa =  
     new com.thera.thermfw.web.WebComboBox("EstrazioneStoriciCommessa", "OrdineRecRisorsa", null); 
  EstrazioneStoriciCommessaOrdineRecRisorsa.setParent(EstrazioneStoriciCommessaForm); 
%>
<select id="<%=EstrazioneStoriciCommessaOrdineRecRisorsa.getId()%>" name="<%=EstrazioneStoriciCommessaOrdineRecRisorsa.getName()%>"><% 
  EstrazioneStoriciCommessaOrdineRecRisorsa.write(out); 
%> 
</select>
												</td>
											</tr>											
											<tr>
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EstrazioneStoriciCommessa", "ModalitaCostiMancanti", null); 
   label.setParent(EstrazioneStoriciCommessaForm); 
%><label class="<%=label.getClassType()%>" for="ModalitaCostiMancanti"><%label.write(out);%></label><%}%>
												</td>
												<td>
													<% 
  WebComboBox EstrazioneStoriciCommessaModalitaCostiMancanti =  
     new com.thera.thermfw.web.WebComboBox("EstrazioneStoriciCommessa", "ModalitaCostiMancanti", null); 
  EstrazioneStoriciCommessaModalitaCostiMancanti.setParent(EstrazioneStoriciCommessaForm); 
  EstrazioneStoriciCommessaModalitaCostiMancanti.setOnChange("onModalitaCostiMancantiChangeAction()"); 
%>
<select id="<%=EstrazioneStoriciCommessaModalitaCostiMancanti.getId()%>" name="<%=EstrazioneStoriciCommessaModalitaCostiMancanti.getName()%>"><% 
  EstrazioneStoriciCommessaModalitaCostiMancanti.write(out); 
%> 
</select>
												</td>
											</tr>
											<tr>
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EstrazioneStoriciCommessa", "IdAmbienteCostiMancanti", null); 
   label.setParent(EstrazioneStoriciCommessaForm); 
%><label class="<%=label.getClassType()%>" for="AmbienteCostiMancanti"><%label.write(out);%></label><%}%>
												</td>
												<td colspan="3">
													<% 
  WebMultiSearchForm EstrazioneStoriciCommessaAmbienteCostiMancanti =  
     new com.thera.thermfw.web.WebMultiSearchForm("EstrazioneStoriciCommessa", "AmbienteCostiMancanti", false, false, true, 1, null, null); 
  EstrazioneStoriciCommessaAmbienteCostiMancanti.setParent(EstrazioneStoriciCommessaForm); 
  EstrazioneStoriciCommessaAmbienteCostiMancanti.write(out); 
%>
<!--<span class="multisearchform" id="AmbienteCostiMancanti" name="AmbienteCostiMancanti"></span>-->
												</td>
											</tr>
											<tr>
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "EstrazioneStoriciCommessa", "IdTipoCostoMancanti", null); 
   label.setParent(EstrazioneStoriciCommessaForm); 
%><label class="<%=label.getClassType()%>" for="TipoCostoMancanti"><%label.write(out);%></label><%}%>
												</td>
												<td colspan="3">
													<% 
  WebMultiSearchForm EstrazioneStoriciCommessaTipoCostoMancanti =  
     new com.thera.thermfw.web.WebMultiSearchForm("EstrazioneStoriciCommessa", "TipoCostoMancanti", false, false, true, 1, null, null); 
  EstrazioneStoriciCommessaTipoCostoMancanti.setParent(EstrazioneStoriciCommessaForm); 
  EstrazioneStoriciCommessaTipoCostoMancanti.write(out); 
%>
<!--<span class="multisearchform" id="TipoCostoMancanti" name="TipoCostoMancanti"></span>-->
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					<% myTabbed.endTab(); %> 
</div>
					<div class="tabbed_page" id="<%=myTabbed.getTabPageId("FiltroTab")%>" style="width:100%;height:100%;overflow:auto;"><% myTabbed.startTab("FiltroTab"); %>
						<table style="margin: 7 7 7 7;" width="98%">
							<tr>
								<td>
									<% 
   request.setAttribute("parentForm", EstrazioneStoriciCommessaForm); 
   String CDForFiltroAmbienteCosti$it$thera$thip$cs$CondizioniFiltri$jsp = "FiltroAmbienteCosti"; 
%>
<jsp:include page="/it/thera/thip/cs/CondizioniFiltri.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForFiltroAmbienteCosti$it$thera$thip$cs$CondizioniFiltri$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="FiltroAmbienteCosti">
									</span>-->
								</td>
							</tr>
						</table>
					<% myTabbed.endTab(); %> 
</div>
				</div><% myTabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>-->
			</td>
		</tr>
		<tr>
			<td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(EstrazioneStoriciCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
		</tr>
	</table>
<%
  EstrazioneStoriciCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = EstrazioneStoriciCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", EstrazioneStoriciCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              EstrazioneStoriciCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, EstrazioneStoriciCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, EstrazioneStoriciCommessaBODC.getErrorList().getErrors()); 
           if(EstrazioneStoriciCommessaBODC.getConflict() != null) 
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
     if(EstrazioneStoriciCommessaBODC != null && !EstrazioneStoriciCommessaBODC.close(false)) 
        errors.addAll(0, EstrazioneStoriciCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = EstrazioneStoriciCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", EstrazioneStoriciCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = EstrazioneStoriciCommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
<% 
  WebScript script_0 =  
   new com.thera.thermfw.web.WebScript(); 
 script_0.setRequest(request); 
 script_0.setSrcAttribute("com/thera/thermfw/batch/PrintBatchRunnable.js"); 
 script_0.setLanguageAttribute("JavaScript1.2"); 
  script_0.write(out); 
%>
<!--<script language="JavaScript1.2" src="com/thera/thermfw/batch/PrintBatchRunnable.js" type="text/javascript"></script>-->
</html>

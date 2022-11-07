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
  BODataCollector VariaBudgetCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm VariaBudgetCommessaForm =  
     new com.thera.thermfw.web.WebForm(request, response, "VariaBudgetCommessaForm", "VariaBudgetCommessa", null, "it.thera.thip.produzione.commessa.web.VariaBudgetCommessaFormActionAdapter", false, false, false, true, true, true, null, 1, true, "it/thera/thip/produzione/commessa/VariaBudgetCommessa.js"); 
  VariaBudgetCommessaForm.setServletEnvironment(se); 
  VariaBudgetCommessaForm.setJSTypeList(jsList); 
  VariaBudgetCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  VariaBudgetCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  VariaBudgetCommessaForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.VariaBudgetCommessaWebFormModifier"); 
  VariaBudgetCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = VariaBudgetCommessaForm.getMode(); 
  String key = VariaBudgetCommessaForm.getKey(); 
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
        VariaBudgetCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = VariaBudgetCommessaForm.findBODataCollectorName(); 
                VariaBudgetCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (VariaBudgetCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)VariaBudgetCommessaBODC).setServletEnvironment(se); 
        VariaBudgetCommessaBODC.initialize("VariaBudgetCommessa", true, 1); 
        VariaBudgetCommessaForm.setBODataCollector(VariaBudgetCommessaBODC); 
        int rcBODC = VariaBudgetCommessaForm.initSecurityServices(); 
        mode = VariaBudgetCommessaForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           VariaBudgetCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = VariaBudgetCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              VariaBudgetCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Variazione budget commessa</title>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(VariaBudgetCommessaForm); 
   request.setAttribute("menuBar", menuBar); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="menuBar"/> 
</jsp:include> 
<% 
  menuBar.write(out); 
  menuBar.writeChildren(out); 
%> 
<% 
  WebToolBar myToolBarTB = new com.thera.thermfw.web.WebToolBar("myToolBar", "24", "24", "16", "16", "#f7fbfd","#C8D6E1"); 
  myToolBarTB.setParent(VariaBudgetCommessaForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=VariaBudgetCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=VariaBudgetCommessaForm.getBodyOnLoad()%>" onunload="<%=VariaBudgetCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   VariaBudgetCommessaForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = VariaBudgetCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", VariaBudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=VariaBudgetCommessaForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  VariaBudgetCommessaForm.writeFormStartElements(out); 
%>

		<table border="0" cellpadding="0" cellspacing="0" height="100%" width="100%">
			<tr><td style="height:10px" valign="top"><% menuBar.writeElements(out); %> 
</td></tr>
			<tr><td style="height:10px" valign="top"><% myToolBarTB.writeChildren(out); %> 
</td></tr>
			<tr><td style="height:10px" valign="top"><% 
  WebWorkflowPanel VariaBudgetCommessaWfStatus =  
     new com.thera.thermfw.web.WebWorkflowPanel("VariaBudgetCommessa", "WfStatus", true, false, null, null); 
  VariaBudgetCommessaWfStatus.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaWfStatus.write(out); 
%>
<!--<span class="wfpanel" id="WorkFlowf"></span>--></td></tr>
			<tr>
				<td height="193px" valign="top">
					<!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(VariaBudgetCommessaForm); 
 mytabbed.addTab("tab1", "it.thera.thip.produzione.commessa.resources.VariaBudgetCommessa", "Generale", "VariaBudgetCommessa", null, null, null, null); 
 mytabbed.addTab("tab2", "it.thera.thip.produzione.commessa.resources.VariaBudgetCommessa", "Riferimento", "VariaBudgetCommessa", null, null, null, null); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
						<div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab1")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab1"); %>
							<table cellpadding="0" cellspacinng="0" style="margin: 0 0 0 0 ;" width="95%">
								<tr>									
									<td valign="top">
										<table border="0" style="margin:0 0 0 0;">
											<tr>
												<td> 
													<table>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdCommessa", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebMultiSearchForm VariaBudgetCommessaCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "Commessa", false, false, true, 1, null, null); 
  VariaBudgetCommessaCommessa.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaCommessa.setOnKeyChange("completaDati()"); 
  VariaBudgetCommessaCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  VariaBudgetCommessaCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
														</tr>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdCommessaBudget", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="BudgetCommessa"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebMultiSearchForm VariaBudgetCommessaBudgetCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "BudgetCommessa", false, false, true, 2, null, null); 
  VariaBudgetCommessaBudgetCommessa.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaBudgetCommessa.write(out); 
%>
<!--<span class="multisearchform" id="BudgetCommessa"></span>--></td>
														</tr>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdCommessaConsuntivo", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="ConsuntivoCommessa"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebMultiSearchForm VariaBudgetCommessaConsuntivoCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "ConsuntivoCommessa", false, false, true, 2, null, null); 
  VariaBudgetCommessaConsuntivoCommessa.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaConsuntivoCommessa.setAdditionalRestrictConditions("IdCommessa, IdCommessa;IdBudget, IdConsuntivo"); 
  VariaBudgetCommessaConsuntivoCommessa.setSpecificDOList("it.thera.thip.produzione.commessa.web.VariaBudgetCMMRicConsuntivoCMMDOList"); 
  VariaBudgetCommessaConsuntivoCommessa.write(out); 
%>
<!--<span class="multisearchform" id="ConsuntivoCommessa"></span>--></td>
														</tr>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "DataRiferimento", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataRiferimento"><%label.write(out);%></label><%}%></td>
															<td nowrap>
																<% 
  WebTextInput VariaBudgetCommessaDataRiferimento =  
     new com.thera.thermfw.web.WebTextInput("VariaBudgetCommessa", "DataRiferimento"); 
  VariaBudgetCommessaDataRiferimento.setShowCalendarBtn(true); 
  VariaBudgetCommessaDataRiferimento.setParent(VariaBudgetCommessaForm); 
%>
<input class="<%=VariaBudgetCommessaDataRiferimento.getClassType()%>" id="<%=VariaBudgetCommessaDataRiferimento.getId()%>" maxlength="<%=VariaBudgetCommessaDataRiferimento.getMaxLength()%>" name="<%=VariaBudgetCommessaDataRiferimento.getName()%>" size="<%=VariaBudgetCommessaDataRiferimento.getSize()%>"><% 
  VariaBudgetCommessaDataRiferimento.write(out); 
%>

															</td>
														</tr>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "Descrizione", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Descrizione"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebTextInput VariaBudgetCommessaDescrizione =  
     new com.thera.thermfw.web.WebTextInput("VariaBudgetCommessa", "Descrizione"); 
  VariaBudgetCommessaDescrizione.setParent(VariaBudgetCommessaForm); 
%>
<input class="<%=VariaBudgetCommessaDescrizione.getClassType()%>" id="<%=VariaBudgetCommessaDescrizione.getId()%>" maxlength="<%=VariaBudgetCommessaDescrizione.getMaxLength()%>" name="<%=VariaBudgetCommessaDescrizione.getName()%>" size="<%=VariaBudgetCommessaDescrizione.getSize()%>"><% 
  VariaBudgetCommessaDescrizione.write(out); 
%>
</td>
														</tr>
														
													</table>
												</td>
												<td valign="top"> 
													<table height="100%" style="margin-left: 5px;" width="100%">
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "StatoAvanzamento", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="StatoAvanzamento"><%label.write(out);%></label><%}%></td>
															<td><% 
  WebComboBox VariaBudgetCommessaStatoAvanzamento =  
     new com.thera.thermfw.web.WebComboBox("VariaBudgetCommessa", "StatoAvanzamento", null); 
  VariaBudgetCommessaStatoAvanzamento.setParent(VariaBudgetCommessaForm); 
%>
<select id="<%=VariaBudgetCommessaStatoAvanzamento.getId()%>" name="<%=VariaBudgetCommessaStatoAvanzamento.getName()%>"><% 
  VariaBudgetCommessaStatoAvanzamento.write(out); 
%> 
</select></td>
														</tr>
														<tr>
															<td colspan="2"><% 
   request.setAttribute("parentForm", VariaBudgetCommessaForm); 
   String CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp = "DatiComuniEstesi"; 
%>
<jsp:include page="/it/thera/thip/cs/DatiComuniEstesi.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DatiComuniEstesi" name="DatiComuniEstesi"></span>--></td>
														</tr>														
													    <tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "Note", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Note"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebTextInput VariaBudgetCommessaNote =  
     new com.thera.thermfw.web.WebTextArea("VariaBudgetCommessa", "Note"); 
  VariaBudgetCommessaNote.setParent(VariaBudgetCommessaForm); 
%>
<textarea class="<%=VariaBudgetCommessaNote.getClassType()%>" cols="50" id="<%=VariaBudgetCommessaNote.getId()%>" maxlength="<%=VariaBudgetCommessaNote.getMaxLength()%>" name="<%=VariaBudgetCommessaNote.getName()%>" rows="2" size="<%=VariaBudgetCommessaNote.getSize()%>"></textarea><% 
  VariaBudgetCommessaNote.write(out); 
%>
</td>																
														</tr>
													</table>
												</td>												
											</tr>
											<tr>
												<td colspan="2" height="25px" valign="top">
													<table style="margin-left: 5px;">
														<tr>
															<td style="white-space:nowrap;" width="50px"><% 
  WebCheckBox VariaBudgetCommessaTotali =  
     new com.thera.thermfw.web.WebCheckBox("VariaBudgetCommessa", "Totali"); 
  VariaBudgetCommessaTotali.setParent(VariaBudgetCommessaForm); 
%>
<input id="<%=VariaBudgetCommessaTotali.getId()%>" name="<%=VariaBudgetCommessaTotali.getName()%>" type="checkbox" value="Y"><%
  VariaBudgetCommessaTotali.write(out); 
%>
</td>
															<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox VariaBudgetCommessaDettagliCommessa =  
     new com.thera.thermfw.web.WebCheckBox("VariaBudgetCommessa", "DettagliCommessa"); 
  VariaBudgetCommessaDettagliCommessa.setParent(VariaBudgetCommessaForm); 
%>
<input id="<%=VariaBudgetCommessaDettagliCommessa.getId()%>" name="<%=VariaBudgetCommessaDettagliCommessa.getName()%>" type="checkbox" value="Y"><%
  VariaBudgetCommessaDettagliCommessa.write(out); 
%>
</td>
															<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox VariaBudgetCommessaDettagliSottoCommesse =  
     new com.thera.thermfw.web.WebCheckBox("VariaBudgetCommessa", "DettagliSottoCommesse"); 
  VariaBudgetCommessaDettagliSottoCommesse.setParent(VariaBudgetCommessaForm); 
%>
<input id="<%=VariaBudgetCommessaDettagliSottoCommesse.getId()%>" name="<%=VariaBudgetCommessaDettagliSottoCommesse.getName()%>" type="checkbox" value="Y"><%
  VariaBudgetCommessaDettagliSottoCommesse.write(out); 
%>
</td>
															<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox VariaBudgetCommessaComponentiPropri =  
     new com.thera.thermfw.web.WebCheckBox("VariaBudgetCommessa", "ComponentiPropri"); 
  VariaBudgetCommessaComponentiPropri.setParent(VariaBudgetCommessaForm); 
%>
<input id="<%=VariaBudgetCommessaComponentiPropri.getId()%>" name="<%=VariaBudgetCommessaComponentiPropri.getName()%>" type="checkbox" value="Y"><%
  VariaBudgetCommessaComponentiPropri.write(out); 
%>
</td>
															<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox VariaBudgetCommessaSoloComponentiValorizzate =  
     new com.thera.thermfw.web.WebCheckBox("VariaBudgetCommessa", "SoloComponentiValorizzate"); 
  VariaBudgetCommessaSoloComponentiValorizzate.setParent(VariaBudgetCommessaForm); 
%>
<input id="<%=VariaBudgetCommessaSoloComponentiValorizzate.getId()%>" name="<%=VariaBudgetCommessaSoloComponentiValorizzate.getName()%>" type="checkbox" value="Y"><%
  VariaBudgetCommessaSoloComponentiValorizzate.write(out); 
%>
</td>
															<td><% 
  MDVButton newMDV =  
   new com.thera.thermfw.web.MDVButton("SaveScreenData", "com.thera.thermfw.web.resources.web", "SaveScreenData", "thermweb/image/gui/SaveScreenData.gif", null, null); 
  newMDV.setParent(VariaBudgetCommessaForm); 
  newMDV.setImageWidth("16"); 
  newMDV.setImageHeight("16"); 
  newMDV.write(out); 
%>
<!--<span class="mdvbutton" id="newMDV" name="newMDV"></span>--></td>
															<td>
																<button id="thAggiornaBUT" name="thAggiornaBUT" onclick="aggiornaVariaBudgetView()" style="width:26px;height:26px;align:top" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "Aggiorna")%>" type="button"><img border="0" height="16px" src="thermweb/image/gui/cnr/Run.gif" width="16px">
																<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "AggiornaID")%></button>
															</td>			
														</tr>
													</table>
												</td>
											</tr>	
											<tr>
												<td width="140"><% 
  WebTextInput VariaBudgetCommessaIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("VariaBudgetCommessa", "IdAzienda"); 
  VariaBudgetCommessaIdAzienda.setParent(VariaBudgetCommessaForm); 
%>
<input class="<%=VariaBudgetCommessaIdAzienda.getClassType()%>" id="<%=VariaBudgetCommessaIdAzienda.getId()%>" maxlength="<%=VariaBudgetCommessaIdAzienda.getMaxLength()%>" name="<%=VariaBudgetCommessaIdAzienda.getName()%>" size="<%=VariaBudgetCommessaIdAzienda.getSize()%>" type="hidden"><% 
  VariaBudgetCommessaIdAzienda.write(out); 
%>
</td>
											</tr>
										</table>
									</td>
								</tr>
						    </table>
						<% mytabbed.endTab(); %> 
</div>		
						<div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab2")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab2"); %>
							<table style="margin: 0 0 0 5;" width="97%">
								<tr>					
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdCommessaPrm", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CommessaPrm"><%label.write(out);%></label><%}%></td>
									<td nowrap style="width: 100%;"><% 
  WebMultiSearchForm VariaBudgetCommessaCommessaPrm =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "CommessaPrm", false, false, true, 1, null, null); 
  VariaBudgetCommessaCommessaPrm.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaCommessaPrm.write(out); 
%>
<!--<span class="multisearchform" id="CommessaPrm"></span>--></td>
								</tr>							
								<tr>					
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdArticolo", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Articolo"><%label.write(out);%></label><%}%></td>
									<td colspan="2"><% 
  WebMultiSearchForm VariaBudgetCommessaArticolo =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "Articolo", false, false, true, 1, null, null); 
  VariaBudgetCommessaArticolo.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaArticolo.write(out); 
%>
<!--<span class="multisearchform" id="Articolo"></span>--></td>
								</tr>								
								<tr>					
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdVersione", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Versione"><%label.write(out);%></label><%}%></td>
									<td colspan="2"><% 
  WebMultiSearchForm VariaBudgetCommessaVersione =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "Versione", false, false, true, 1, "10", "35"); 
  VariaBudgetCommessaVersione.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaVersione.write(out); 
%>
<!--<span class="multisearchform" id="Versione"></span>--></td>
								</tr>	
								<tr>					
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdEsternoConfig", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Configurazione"><%label.write(out);%></label><%}%></td>
									<td colspan="2"><% 
  WebMultiSearchForm VariaBudgetCommessaConfigurazione =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "Configurazione", false, false, true, 1, "10", "35"); 
  VariaBudgetCommessaConfigurazione.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaConfigurazione.write(out); 
%>
<!--<span class="multisearchform" id="Configurazione"></span>--></td>
								</tr>
								<tr>
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "QuantitaPrm", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="QuantitaPrm"><%label.write(out);%></label><%}%></td>
									<td>
										<% 
  WebTextInput VariaBudgetCommessaQuantitaPrm =  
     new com.thera.thermfw.web.WebTextInput("VariaBudgetCommessa", "QuantitaPrm"); 
  VariaBudgetCommessaQuantitaPrm.setParent(VariaBudgetCommessaForm); 
%>
<input class="<%=VariaBudgetCommessaQuantitaPrm.getClassType()%>" id="<%=VariaBudgetCommessaQuantitaPrm.getId()%>" maxlength="<%=VariaBudgetCommessaQuantitaPrm.getMaxLength()%>" name="<%=VariaBudgetCommessaQuantitaPrm.getName()%>" size="<%=VariaBudgetCommessaQuantitaPrm.getSize()%>"><% 
  VariaBudgetCommessaQuantitaPrm.write(out); 
%>

										<% 
  WebTextInput VariaBudgetCommessaIdDescUMPrmMag =  
     new com.thera.thermfw.web.WebTextInput("VariaBudgetCommessa", "IdDescUMPrmMag"); 
  VariaBudgetCommessaIdDescUMPrmMag.setParent(VariaBudgetCommessaForm); 
%>
<input class="<%=VariaBudgetCommessaIdDescUMPrmMag.getClassType()%>" id="<%=VariaBudgetCommessaIdDescUMPrmMag.getId()%>" maxlength="<%=VariaBudgetCommessaIdDescUMPrmMag.getMaxLength()%>" name="<%=VariaBudgetCommessaIdDescUMPrmMag.getName()%>" size="<%=VariaBudgetCommessaIdDescUMPrmMag.getSize()%>"><% 
  VariaBudgetCommessaIdDescUMPrmMag.write(out); 
%>

									</td>
								</tr>	
								<tr>					
									<td nowrap style="display:none"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdCommessaAppart", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CommessaAppart"><%label.write(out);%></label><%}%></td>
									<td colspan="3" nowrap style="display:none"><% 
  WebMultiSearchForm VariaBudgetCommessaCommessaAppart =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "CommessaAppart", false, false, true, 1, null, null); 
  VariaBudgetCommessaCommessaAppart.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaCommessaAppart.write(out); 
%>
<!--<span class="multisearchform" id="CommessaAppart"></span>--></td>
								</tr>	
								<tr>					
									<td nowrap style="display:none"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "VariaBudgetCommessa", "IdStabilimento", null); 
   label.setParent(VariaBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Stabilimento"><%label.write(out);%></label><%}%></td>
									<td colspan="2" style="display:none"><% 
  WebMultiSearchForm VariaBudgetCommessaStabilimento =  
     new com.thera.thermfw.web.WebMultiSearchForm("VariaBudgetCommessa", "Stabilimento", false, false, true, 1, "5", "35"); 
  VariaBudgetCommessaStabilimento.setParent(VariaBudgetCommessaForm); 
  VariaBudgetCommessaStabilimento.write(out); 
%>
<!--<span class="multisearchform" id="Stabilimento"></span>--></td>
								</tr>								
							</table>
						<% mytabbed.endTab(); %> 
</div>
					</div><% mytabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>-->								
				</td>
			</tr>			
			<tr>
				<td>
					<iframe height="100%" id="VariaBudgetCommessaDettaglio" name="VariaBudgetCommessaDettaglio" src style="border: 0px solid black;margin:0 5px 10px 5px;height: calc(100% - 15px);width: calc(100% - 10px);" width="100%"></iframe>
				</td>
			</tr>
			<tr><td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(VariaBudgetCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td></tr>
		</table>
	<%
  VariaBudgetCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = VariaBudgetCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", VariaBudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              VariaBudgetCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, VariaBudgetCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, VariaBudgetCommessaBODC.getErrorList().getErrors()); 
           if(VariaBudgetCommessaBODC.getConflict() != null) 
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
     if(VariaBudgetCommessaBODC != null && !VariaBudgetCommessaBODC.close(false)) 
        errors.addAll(0, VariaBudgetCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = VariaBudgetCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", VariaBudgetCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = VariaBudgetCommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

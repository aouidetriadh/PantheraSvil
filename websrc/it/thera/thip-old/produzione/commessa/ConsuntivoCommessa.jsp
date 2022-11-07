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
  BODataCollector ConsuntivoCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm ConsuntivoCommessaForm =  
     new com.thera.thermfw.web.WebForm(request, response, "ConsuntivoCommessaForm", "ConsuntivoCommessa", null, "it.thera.thip.produzione.commessa.web.ConsuntivoCommessaFormActionAdapter", false, false, false, true, true, true, null, 1, true, "it/thera/thip/produzione/commessa/ConsuntivoCommessa.js"); 
  ConsuntivoCommessaForm.setServletEnvironment(se); 
  ConsuntivoCommessaForm.setJSTypeList(jsList); 
  ConsuntivoCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  ConsuntivoCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ConsuntivoCommessaForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.ConsuntivoCommessaWebFormModifier"); 
  ConsuntivoCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = ConsuntivoCommessaForm.getMode(); 
  String key = ConsuntivoCommessaForm.getKey(); 
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
        ConsuntivoCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = ConsuntivoCommessaForm.findBODataCollectorName(); 
                ConsuntivoCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (ConsuntivoCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)ConsuntivoCommessaBODC).setServletEnvironment(se); 
        ConsuntivoCommessaBODC.initialize("ConsuntivoCommessa", true, 1); 
        ConsuntivoCommessaForm.setBODataCollector(ConsuntivoCommessaBODC); 
        int rcBODC = ConsuntivoCommessaForm.initSecurityServices(); 
        mode = ConsuntivoCommessaForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           ConsuntivoCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = ConsuntivoCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              ConsuntivoCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Consuntivo commessa</title>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(ConsuntivoCommessaForm); 
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
  myToolBarTB.setParent(ConsuntivoCommessaForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=ConsuntivoCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=ConsuntivoCommessaForm.getBodyOnLoad()%>" onunload="<%=ConsuntivoCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   ConsuntivoCommessaForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = ConsuntivoCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", ConsuntivoCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=ConsuntivoCommessaForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  ConsuntivoCommessaForm.writeFormStartElements(out); 
%>

		<table border="0" cellpadding="0" cellspacing="0" height="100%" width="100%">
			<tr><td style="height:0" valign="top"><% menuBar.writeElements(out); %> 
</td></tr>
			<tr><td style="height:30px" valign="top"><% myToolBarTB.writeChildren(out); %> 
</td></tr>
			<tr>
				<td height="165px" valign="top">
					<!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(ConsuntivoCommessaForm); 
 mytabbed.addTab("tab1", "it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa", "Generale", "ConsuntivoCommessa", null, null, null, null); 
 mytabbed.addTab("tab2", "it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa", "Riferimento", "ConsuntivoCommessa", null, null, null, null); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
						<div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab1")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab1"); %>
							<table style="margin: 0 0 0 0 ;" width="90%">
								<tr>									
									<td valign="top">
										<table border="0" style="margin:0 0 0 0;">
											<tr>
												<td> 
													<table>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdCommessa", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebMultiSearchForm ConsuntivoCommessaCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "Commessa", false, false, true, 1, null, null); 
  ConsuntivoCommessaCommessa.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaCommessa.setOnKeyChange("completaDatiConsuntivo()"); 
  ConsuntivoCommessaCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  ConsuntivoCommessaCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
														</tr>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "DataRiferimento", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataRiferimento"><%label.write(out);%></label><%}%></td>
															<td nowrap>
																<% 
  WebCheckBox ConsuntivoCommessaUsaDataEstrazioneStorici =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "UsaDataEstrazioneStorici"); 
  ConsuntivoCommessaUsaDataEstrazioneStorici.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaUsaDataEstrazioneStorici.setOnClick("gestioneUsaDataEstrazioneStorici()"); 
%>
<input id="<%=ConsuntivoCommessaUsaDataEstrazioneStorici.getId()%>" name="<%=ConsuntivoCommessaUsaDataEstrazioneStorici.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaUsaDataEstrazioneStorici.write(out); 
%>

																<% 
  WebTextInput ConsuntivoCommessaDataRiferimento =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "DataRiferimento"); 
  ConsuntivoCommessaDataRiferimento.setShowCalendarBtn(true); 
  ConsuntivoCommessaDataRiferimento.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaDataRiferimento.getClassType()%>" id="<%=ConsuntivoCommessaDataRiferimento.getId()%>" maxlength="<%=ConsuntivoCommessaDataRiferimento.getMaxLength()%>" name="<%=ConsuntivoCommessaDataRiferimento.getName()%>" size="<%=ConsuntivoCommessaDataRiferimento.getSize()%>"><% 
  ConsuntivoCommessaDataRiferimento.write(out); 
%>

															</td>
														</tr>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "Descrizione", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Descrizione"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebTextInput ConsuntivoCommessaDescrizione =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "Descrizione"); 
  ConsuntivoCommessaDescrizione.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaDescrizione.getClassType()%>" id="<%=ConsuntivoCommessaDescrizione.getId()%>" maxlength="<%=ConsuntivoCommessaDescrizione.getMaxLength()%>" name="<%=ConsuntivoCommessaDescrizione.getName()%>" size="<%=ConsuntivoCommessaDescrizione.getSize()%>"><% 
  ConsuntivoCommessaDescrizione.write(out); 
%>
</td>
														</tr>
														<tr>
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdComponenteTotali", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="ComponenteTotali"><%label.write(out);%></label><%}%></td>
															<td nowrap><% 
  WebMultiSearchForm ConsuntivoCommessaComponenteTotali =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "ComponenteTotali", false, false, true, 1, null, null); 
  ConsuntivoCommessaComponenteTotali.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaComponenteTotali.write(out); 
%>
<!--<span class="multisearchform" id="ComponenteTotali"></span>--></td>
														</tr>
													</table>
												</td>
												<td valign="top"> 
													<fieldset id="Costi" name="Costi" style="width:100%;"> 
														<legend align="top">
															<label class="thLabel" id="CostiLab" name="CostiLab">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa", "CostiLbl", null, null, null, null); 
 label.setParent(ConsuntivoCommessaForm); 
label.write(out); }%> 
</label>
														</legend>
														<table border="0" style="margin: 0 0 0 0;">
															<tr>
																<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "CostoRiferimento", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoRiferimento"><%label.write(out);%></label><%}%></td>
																<td><% 
  WebTextInput ConsuntivoCommessaCostoRiferimento =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "CostoRiferimento"); 
  ConsuntivoCommessaCostoRiferimento.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaCostoRiferimento.getClassType()%>" id="<%=ConsuntivoCommessaCostoRiferimento.getId()%>" maxlength="<%=ConsuntivoCommessaCostoRiferimento.getMaxLength()%>" name="<%=ConsuntivoCommessaCostoRiferimento.getName()%>" size="<%=ConsuntivoCommessaCostoRiferimento.getSize()%>"><% 
  ConsuntivoCommessaCostoRiferimento.write(out); 
%>
</td>
															</tr>
															<tr>
																<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "CostoPrimo", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoPrimo"><%label.write(out);%></label><%}%></td>
																<td><% 
  WebTextInput ConsuntivoCommessaCostoPrimo =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "CostoPrimo"); 
  ConsuntivoCommessaCostoPrimo.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaCostoPrimo.getClassType()%>" id="<%=ConsuntivoCommessaCostoPrimo.getId()%>" maxlength="<%=ConsuntivoCommessaCostoPrimo.getMaxLength()%>" name="<%=ConsuntivoCommessaCostoPrimo.getName()%>" size="<%=ConsuntivoCommessaCostoPrimo.getSize()%>"><% 
  ConsuntivoCommessaCostoPrimo.write(out); 
%>
</td>
															</tr>
															<tr>
																<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "CostoIndustriale", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoIndustriale"><%label.write(out);%></label><%}%></td>
																<td><% 
  WebTextInput ConsuntivoCommessaCostoIndustriale =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "CostoIndustriale"); 
  ConsuntivoCommessaCostoIndustriale.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaCostoIndustriale.getClassType()%>" id="<%=ConsuntivoCommessaCostoIndustriale.getId()%>" maxlength="<%=ConsuntivoCommessaCostoIndustriale.getMaxLength()%>" name="<%=ConsuntivoCommessaCostoIndustriale.getName()%>" size="<%=ConsuntivoCommessaCostoIndustriale.getSize()%>"><% 
  ConsuntivoCommessaCostoIndustriale.write(out); 
%>
</td>
															</tr>
															<tr>
																<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "CostoGenerale", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoGenerale"><%label.write(out);%></label><%}%></td>
																<td><% 
  WebTextInput ConsuntivoCommessaCostoGenerale =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "CostoGenerale"); 
  ConsuntivoCommessaCostoGenerale.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaCostoGenerale.getClassType()%>" id="<%=ConsuntivoCommessaCostoGenerale.getId()%>" maxlength="<%=ConsuntivoCommessaCostoGenerale.getMaxLength()%>" name="<%=ConsuntivoCommessaCostoGenerale.getName()%>" size="<%=ConsuntivoCommessaCostoGenerale.getSize()%>"><% 
  ConsuntivoCommessaCostoGenerale.write(out); 
%>
</td>
															</tr>
														</table>	
													</fieldset>
												</td>
												<td valign="top"> 
													<table height="100%" style="margin-left: 5px;" width="100%">
														<tr>
															<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "StatoAvanzamento", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="StatoAvanzamento"><%label.write(out);%></label><%}%></td>
															<td><% 
  WebComboBox ConsuntivoCommessaStatoAvanzamento =  
     new com.thera.thermfw.web.WebComboBox("ConsuntivoCommessa", "StatoAvanzamento", null); 
  ConsuntivoCommessaStatoAvanzamento.setParent(ConsuntivoCommessaForm); 
%>
<select id="<%=ConsuntivoCommessaStatoAvanzamento.getId()%>" name="<%=ConsuntivoCommessaStatoAvanzamento.getName()%>"><% 
  ConsuntivoCommessaStatoAvanzamento.write(out); 
%> 
</select></td>
														</tr>														
													    <tr>
															<td colspan="2">
																<fieldset id="Sorgenti" name="Sorgenti" style="width:100%;"> 
																	<legend align="top">
																		<label class="thLabel" id="SorgentiLab" name="SorgentiLab">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa", "SorgentiLbl", null, null, null, null); 
 label.setParent(ConsuntivoCommessaForm); 
label.write(out); }%> 
</label>
																	</legend>
																	<table border="0" style="margin: 0 0 0 0;">
																		<tr>
																			<td width="100px"></td>
																			<td style="white-space:nowrap;" width="100px"><% 
  WebCheckBox ConsuntivoCommessaConsolidato =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "Consolidato"); 
  ConsuntivoCommessaConsolidato.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaConsolidato.getId()%>" name="<%=ConsuntivoCommessaConsolidato.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaConsolidato.write(out); 
%>
</td>
																			<td style="white-space:nowrap;" width="70px"><% 
  WebCheckBox ConsuntivoCommessaEstrazioneOrdini =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "EstrazioneOrdini"); 
  ConsuntivoCommessaEstrazioneOrdini.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaEstrazioneOrdini.getId()%>" name="<%=ConsuntivoCommessaEstrazioneOrdini.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaEstrazioneOrdini.write(out); 
%>
</td>
																			<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ConsuntivoCommessaEstrazioneRichieste =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "EstrazioneRichieste"); 
  ConsuntivoCommessaEstrazioneRichieste.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaEstrazioneRichieste.getId()%>" name="<%=ConsuntivoCommessaEstrazioneRichieste.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaEstrazioneRichieste.write(out); 
%>
</td>
																		</tr>
																	</table>	
																</fieldset>
															</td>																
														</tr>
														<tr>
															<td colspan="2"><% 
   request.setAttribute("parentForm", ConsuntivoCommessaForm); 
   String CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp = "DatiComuniEstesi"; 
%>
<jsp:include page="/it/thera/thip/cs/DatiComuniEstesi.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DatiComuniEstesi" name="DatiComuniEstesi"></span>--></td>
														</tr>
													</table>
												</td>												
											</tr>
											<tr>
												<td width="140"><% 
  WebTextInput ConsuntivoCommessaIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "IdAzienda"); 
  ConsuntivoCommessaIdAzienda.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaIdAzienda.getClassType()%>" id="<%=ConsuntivoCommessaIdAzienda.getId()%>" maxlength="<%=ConsuntivoCommessaIdAzienda.getMaxLength()%>" name="<%=ConsuntivoCommessaIdAzienda.getName()%>" size="<%=ConsuntivoCommessaIdAzienda.getSize()%>" type="hidden"><% 
  ConsuntivoCommessaIdAzienda.write(out); 
%>
</td>
												<td><% 
  WebTextInput ConsuntivoCommessaIdConsuntivo =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "IdConsuntivo"); 
  ConsuntivoCommessaIdConsuntivo.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaIdConsuntivo.getClassType()%>" id="<%=ConsuntivoCommessaIdConsuntivo.getId()%>" maxlength="<%=ConsuntivoCommessaIdConsuntivo.getMaxLength()%>" name="<%=ConsuntivoCommessaIdConsuntivo.getName()%>" size="<%=ConsuntivoCommessaIdConsuntivo.getSize()%>" type="hidden"><% 
  ConsuntivoCommessaIdConsuntivo.write(out); 
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
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdCommessaPrm", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CommessaPrm"><%label.write(out);%></label><%}%></td>
									<td nowrap style="width: 100%;"><% 
  WebMultiSearchForm ConsuntivoCommessaCommessaPrm =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "CommessaPrm", false, false, true, 1, null, null); 
  ConsuntivoCommessaCommessaPrm.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaCommessaPrm.write(out); 
%>
<!--<span class="multisearchform" id="CommessaPrm"></span>--></td>
								</tr>							
								<tr>					
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdArticolo", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Articolo"><%label.write(out);%></label><%}%></td>
									<td colspan="2"><% 
  WebMultiSearchForm ConsuntivoCommessaArticolo =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "Articolo", false, false, true, 1, null, null); 
  ConsuntivoCommessaArticolo.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaArticolo.write(out); 
%>
<!--<span class="multisearchform" id="Articolo"></span>--></td>
								</tr>								
								<tr>					
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdVersione", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Versione"><%label.write(out);%></label><%}%></td>
									<td colspan="2"><% 
  WebMultiSearchForm ConsuntivoCommessaVersione =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "Versione", false, false, true, 1, "10", "35"); 
  ConsuntivoCommessaVersione.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaVersione.write(out); 
%>
<!--<span class="multisearchform" id="Versione"></span>--></td>
								</tr>	
								<tr>					
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdEsternoConfig", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Configurazione"><%label.write(out);%></label><%}%></td>
									<td colspan="2"><% 
  WebMultiSearchForm ConsuntivoCommessaConfigurazione =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "Configurazione", false, false, true, 1, "10", "35"); 
  ConsuntivoCommessaConfigurazione.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaConfigurazione.write(out); 
%>
<!--<span class="multisearchform" id="Configurazione"></span>--></td>
								</tr>
								<tr>
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "QuantitaPrm", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="QuantitaPrm"><%label.write(out);%></label><%}%></td>
									<td>
										<% 
  WebTextInput ConsuntivoCommessaQuantitaPrm =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "QuantitaPrm"); 
  ConsuntivoCommessaQuantitaPrm.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaQuantitaPrm.getClassType()%>" id="<%=ConsuntivoCommessaQuantitaPrm.getId()%>" maxlength="<%=ConsuntivoCommessaQuantitaPrm.getMaxLength()%>" name="<%=ConsuntivoCommessaQuantitaPrm.getName()%>" size="<%=ConsuntivoCommessaQuantitaPrm.getSize()%>"><% 
  ConsuntivoCommessaQuantitaPrm.write(out); 
%>

										<% 
  WebTextInput ConsuntivoCommessaIdDescUMPrmMag =  
     new com.thera.thermfw.web.WebTextInput("ConsuntivoCommessa", "IdDescUMPrmMag"); 
  ConsuntivoCommessaIdDescUMPrmMag.setParent(ConsuntivoCommessaForm); 
%>
<input class="<%=ConsuntivoCommessaIdDescUMPrmMag.getClassType()%>" id="<%=ConsuntivoCommessaIdDescUMPrmMag.getId()%>" maxlength="<%=ConsuntivoCommessaIdDescUMPrmMag.getMaxLength()%>" name="<%=ConsuntivoCommessaIdDescUMPrmMag.getName()%>" size="<%=ConsuntivoCommessaIdDescUMPrmMag.getSize()%>"><% 
  ConsuntivoCommessaIdDescUMPrmMag.write(out); 
%>

									</td>
								</tr>	
								<tr>					
									<td nowrap style="display:none"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdCommessaAppart", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CommessaAppart"><%label.write(out);%></label><%}%></td>
									<td colspan="3" nowrap style="display:none"><% 
  WebMultiSearchForm ConsuntivoCommessaCommessaAppart =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "CommessaAppart", false, false, true, 1, null, null); 
  ConsuntivoCommessaCommessaAppart.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaCommessaAppart.write(out); 
%>
<!--<span class="multisearchform" id="CommessaAppart"></span>--></td>
								</tr>	
								<tr>					
									<td nowrap style="display:none"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ConsuntivoCommessa", "IdStabilimento", null); 
   label.setParent(ConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Stabilimento"><%label.write(out);%></label><%}%></td>
									<td colspan="2" style="display:none"><% 
  WebMultiSearchForm ConsuntivoCommessaStabilimento =  
     new com.thera.thermfw.web.WebMultiSearchForm("ConsuntivoCommessa", "Stabilimento", false, false, true, 1, "5", "35"); 
  ConsuntivoCommessaStabilimento.setParent(ConsuntivoCommessaForm); 
  ConsuntivoCommessaStabilimento.write(out); 
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
				<td height="25px">
					<table style="margin-left: 5px;">
						<tr>			
							<td style="white-space:nowrap;" width="50px"><% 
  WebCheckBox ConsuntivoCommessaTotali =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "Totali"); 
  ConsuntivoCommessaTotali.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaTotali.getId()%>" name="<%=ConsuntivoCommessaTotali.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaTotali.write(out); 
%>
</td>
							<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ConsuntivoCommessaDettagliCommessa =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "DettagliCommessa"); 
  ConsuntivoCommessaDettagliCommessa.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaDettagliCommessa.getId()%>" name="<%=ConsuntivoCommessaDettagliCommessa.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaDettagliCommessa.write(out); 
%>
</td>
							<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ConsuntivoCommessaDettagliSottoCommesse =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "DettagliSottoCommesse"); 
  ConsuntivoCommessaDettagliSottoCommesse.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaDettagliSottoCommesse.getId()%>" name="<%=ConsuntivoCommessaDettagliSottoCommesse.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaDettagliSottoCommesse.write(out); 
%>
</td>
							<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ConsuntivoCommessaComponentiPropri =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "ComponentiPropri"); 
  ConsuntivoCommessaComponentiPropri.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaComponentiPropri.getId()%>" name="<%=ConsuntivoCommessaComponentiPropri.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaComponentiPropri.write(out); 
%>
</td>
							<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ConsuntivoCommessaSoloComponentiValorizzate =  
     new com.thera.thermfw.web.WebCheckBox("ConsuntivoCommessa", "SoloComponentiValorizzate"); 
  ConsuntivoCommessaSoloComponentiValorizzate.setParent(ConsuntivoCommessaForm); 
%>
<input id="<%=ConsuntivoCommessaSoloComponentiValorizzate.getId()%>" name="<%=ConsuntivoCommessaSoloComponentiValorizzate.getName()%>" type="checkbox" value="Y"><%
  ConsuntivoCommessaSoloComponentiValorizzate.write(out); 
%>
</td>
							<!--        <td>         <button type="button" style="width:26px;height:26px;align:top" name="thApplicaScelte" id="thApplicaScelte">          <img border="0" width="16px" height="16px" src="thermweb/image/gui/cnr/Run.gif" />         </button>        </td>-->
							<td><% 
  MDVButton newMDV =  
   new com.thera.thermfw.web.MDVButton("SaveScreenData", "com.thera.thermfw.web.resources.web", "SaveScreenData", "thermweb/image/gui/SaveScreenData.gif", null, null); 
  newMDV.setParent(ConsuntivoCommessaForm); 
  newMDV.setImageWidth("16"); 
  newMDV.setImageHeight("16"); 
  newMDV.write(out); 
%>
<!--<span class="mdvbutton" id="newMDV" name="newMDV"></span>--></td>
							<td>
								<button id="AggiornaBUT" name="AggiornaBUT" onclick="aggiornaConsuntivoView()" style="width:90" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa", "Aggiorna")%>" type="button"><%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa", "Aggiorna")%></button>
							</td>
						</tr>
					</table>
				</td>
			</tr>			
			<tr>
				<td>
					<iframe height="100%" id="ConsuntivoCommessaDettaglio" name="ConsuntivoCommessaDettaglio" src style="border: 0px solid black;margin:0 5px 10px 5px;height: calc(100% - 15px);width: calc(100% - 10px);" width="100%"></iframe>
				</td>
			</tr>
			<tr><td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(ConsuntivoCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td></tr>
		</table>
	<%
  ConsuntivoCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = ConsuntivoCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", ConsuntivoCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              ConsuntivoCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, ConsuntivoCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, ConsuntivoCommessaBODC.getErrorList().getErrors()); 
           if(ConsuntivoCommessaBODC.getConflict() != null) 
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
     if(ConsuntivoCommessaBODC != null && !ConsuntivoCommessaBODC.close(false)) 
        errors.addAll(0, ConsuntivoCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = ConsuntivoCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", ConsuntivoCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = ConsuntivoCommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

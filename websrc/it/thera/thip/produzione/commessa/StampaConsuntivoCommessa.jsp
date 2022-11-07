<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///D:/3rdParty/PantheraSvilL2Panth01/websrc/dtd/xhtml1-transitional.dtd">
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
  BODataCollector StampaConsuntivoCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm StampaConsuntivoCommessaForm =  
     new com.thera.thermfw.web.WebFormForBatchForm(request, response, "StampaConsuntivoCommessaForm", "StampaConsuntivoCommessa", null, "com.thera.thermfw.batch.web.BatchFormActionAdapter", false, false, true, true, true, true, null, 1, true, "it/thera/thip/produzione/commessa/StampaConsuntivoCommessa.js"); 
  StampaConsuntivoCommessaForm.setServletEnvironment(se); 
  StampaConsuntivoCommessaForm.setJSTypeList(jsList); 
  StampaConsuntivoCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  StampaConsuntivoCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ((WebFormForBatchForm)  StampaConsuntivoCommessaForm).setGenerateSSDEnabled(true); 
  StampaConsuntivoCommessaForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.StampaConsuntivoCommessaWebFormModifier"); 
  StampaConsuntivoCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = StampaConsuntivoCommessaForm.getMode(); 
  String key = StampaConsuntivoCommessaForm.getKey(); 
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
        StampaConsuntivoCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = StampaConsuntivoCommessaForm.findBODataCollectorName(); 
				 StampaConsuntivoCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (StampaConsuntivoCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)StampaConsuntivoCommessaBODC).setServletEnvironment(se); 
        StampaConsuntivoCommessaBODC.initialize("StampaConsuntivoCommessa", true, 1); 
        int rcBODC; 
        if (StampaConsuntivoCommessaBODC.getBo() instanceof BatchRunnable) 
          rcBODC = StampaConsuntivoCommessaBODC.initSecurityServices("RUN", mode, true, false, true); 
        else 
          rcBODC = StampaConsuntivoCommessaBODC.initSecurityServices(mode, true, true, true); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           StampaConsuntivoCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = StampaConsuntivoCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              StampaConsuntivoCommessaForm.setBODataCollector(StampaConsuntivoCommessaBODC); 
              StampaConsuntivoCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>StampaConsuntivoCommessa</title>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(StampaConsuntivoCommessaForm); 
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
  myToolBarTB.setParent(StampaConsuntivoCommessaForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/com/thera/thermfw/batch/PrintRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=StampaConsuntivoCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=StampaConsuntivoCommessaForm.getBodyOnLoad()%>" onunload="<%=StampaConsuntivoCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   StampaConsuntivoCommessaForm.writeBodyStartElements(out); 
%> 


<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = StampaConsuntivoCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", StampaConsuntivoCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=StampaConsuntivoCommessaForm.getServlet()%>" method="post" name="StampaConsuntivoCommessa" style="height:100%"><%
  StampaConsuntivoCommessaForm.writeFormStartElements(out); 
%>

	<table cellpadding="2" cellspacing="0" height="100%" width="100%">
		<tr><td style="height:0"><% menuBar.writeElements(out); %> 
</td></tr>
		<tr><td style="height:0"><% myToolBarTB.writeChildren(out); %> 
</td></tr>
		<tr>
			<td height="100%">
				<!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(StampaConsuntivoCommessaForm); 
 mytabbed.addTab("GeneraleTab", "it.thera.thip.produzione.commessa.resources.StampaConsuntivoCommessa", "GeneraleTab", "StampaConsuntivoCommessa", null, null, null, null); 
 mytabbed.addTab("FiltroTab", "it.thera.thip.produzione.commessa.resources.StampaConsuntivoCommessa", "FiltroTab", "StampaConsuntivoCommessa", null, null, null, null); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
					<div class="tabbed_page" id="<%=mytabbed.getTabPageId("GeneraleTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("GeneraleTab"); %>
						<table cellpadding="2" cellspacing="2" style="margin: 7 7 7 7;">
							<tr>
								<td><input id="thReportId" name="thReportId" type="hidden"></td>
								<td><% 
  WebTextInput StampaConsuntivoCommessaIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("StampaConsuntivoCommessa", "IdAzienda"); 
  StampaConsuntivoCommessaIdAzienda.setParent(StampaConsuntivoCommessaForm); 
%>
<input class="<%=StampaConsuntivoCommessaIdAzienda.getClassType()%>" id="<%=StampaConsuntivoCommessaIdAzienda.getId()%>" maxlength="<%=StampaConsuntivoCommessaIdAzienda.getMaxLength()%>" name="<%=StampaConsuntivoCommessaIdAzienda.getName()%>" size="<%=StampaConsuntivoCommessaIdAzienda.getSize()%>" type="hidden"><% 
  StampaConsuntivoCommessaIdAzienda.write(out); 
%>
</td>
							</tr>
							<tr>
								<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "StatoCommessa", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="StatoCommessa"><%label.write(out);%></label><%}%></td>
								<td><% 
  WebComboBox StampaConsuntivoCommessaStatoCommessa =  
     new com.thera.thermfw.web.WebComboBox("StampaConsuntivoCommessa", "StatoCommessa", null); 
  StampaConsuntivoCommessaStatoCommessa.setParent(StampaConsuntivoCommessaForm); 
%>
<select id="<%=StampaConsuntivoCommessaStatoCommessa.getId()%>" name="<%=StampaConsuntivoCommessaStatoCommessa.getName()%>"><% 
  StampaConsuntivoCommessaStatoCommessa.write(out); 
%> 
</select></td>
							</tr>							
							<tr>
								<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "TipoStampa", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="TipoStampa"><%label.write(out);%></label><%}%></td>
								<td><% 
  WebComboBox StampaConsuntivoCommessaTipoStampa =  
     new com.thera.thermfw.web.WebComboBox("StampaConsuntivoCommessa", "TipoStampa", null); 
  StampaConsuntivoCommessaTipoStampa.setParent(StampaConsuntivoCommessaForm); 
  StampaConsuntivoCommessaTipoStampa.setOnChange("onTipoStampaModifAction()"); 
%>
<select id="<%=StampaConsuntivoCommessaTipoStampa.getId()%>" name="<%=StampaConsuntivoCommessaTipoStampa.getName()%>"><% 
  StampaConsuntivoCommessaTipoStampa.write(out); 
%> 
</select>
								</td>
							</tr>
							<tr>
								<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "TipoDettaglio", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="TipoDettaglio"><%label.write(out);%></label><%}%></td>
								<td><% 
  WebComboBox StampaConsuntivoCommessaTipoDettaglio =  
     new com.thera.thermfw.web.WebComboBox("StampaConsuntivoCommessa", "TipoDettaglio", null); 
  StampaConsuntivoCommessaTipoDettaglio.setParent(StampaConsuntivoCommessaForm); 
%>
<select id="<%=StampaConsuntivoCommessaTipoDettaglio.getId()%>" name="<%=StampaConsuntivoCommessaTipoDettaglio.getName()%>"><% 
  StampaConsuntivoCommessaTipoDettaglio.write(out); 
%> 
</select></td>
							</tr>
							<!-- 36252 inizio -->
							<tr>
								<td></td>
								<td><% 
  WebCheckBox StampaConsuntivoCommessaUsaConsuntiviStoricizzati =  
     new com.thera.thermfw.web.WebCheckBox("StampaConsuntivoCommessa", "UsaConsuntiviStoricizzati"); 
  StampaConsuntivoCommessaUsaConsuntiviStoricizzati.setParent(StampaConsuntivoCommessaForm); 
  StampaConsuntivoCommessaUsaConsuntiviStoricizzati.setOnClick("onUsaConsuntiviAction()"); 
%>
<input id="<%=StampaConsuntivoCommessaUsaConsuntiviStoricizzati.getId()%>" name="<%=StampaConsuntivoCommessaUsaConsuntiviStoricizzati.getName()%>" type="checkbox" value="Y"><%
  StampaConsuntivoCommessaUsaConsuntiviStoricizzati.write(out); 
%>
</td>
							</tr>
							<tr>
								<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "DataRiferimento", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataRiferimento"><%label.write(out);%></label><%}%></td>
								<td nowrap>
									<% 
  WebTextInput StampaConsuntivoCommessaDataRiferimento =  
     new com.thera.thermfw.web.WebTextInput("StampaConsuntivoCommessa", "DataRiferimento"); 
  StampaConsuntivoCommessaDataRiferimento.setShowCalendarBtn(true); 
  StampaConsuntivoCommessaDataRiferimento.setParent(StampaConsuntivoCommessaForm); 
%>
<input class="<%=StampaConsuntivoCommessaDataRiferimento.getClassType()%>" id="<%=StampaConsuntivoCommessaDataRiferimento.getId()%>" maxlength="<%=StampaConsuntivoCommessaDataRiferimento.getMaxLength()%>" name="<%=StampaConsuntivoCommessaDataRiferimento.getName()%>" size="<%=StampaConsuntivoCommessaDataRiferimento.getSize()%>"><% 
  StampaConsuntivoCommessaDataRiferimento.write(out); 
%>

								</td>
							</tr>
							<tr>
								<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "IdCommessa", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
								<td nowrap><% 
  WebMultiSearchForm StampaConsuntivoCommessaCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("StampaConsuntivoCommessa", "Commessa", false, false, true, 1, null, null); 
  StampaConsuntivoCommessaCommessa.setParent(StampaConsuntivoCommessaForm); 
  StampaConsuntivoCommessaCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  StampaConsuntivoCommessaCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
							</tr>
							<tr>
								<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "IdCommessaConsuntivo", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="ConsuntivoCommessa"><%label.write(out);%></label><%}%></td>
								<td nowrap><% 
  WebMultiSearchForm StampaConsuntivoCommessaConsuntivoCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("StampaConsuntivoCommessa", "ConsuntivoCommessa", false, false, true, 2, null, null); 
  StampaConsuntivoCommessaConsuntivoCommessa.setParent(StampaConsuntivoCommessaForm); 
  StampaConsuntivoCommessaConsuntivoCommessa.setAdditionalRestrictConditions("IdCommessa, IdCommessa"); 
  StampaConsuntivoCommessaConsuntivoCommessa.write(out); 
%>
<!--<span class="multisearchform" id="ConsuntivoCommessa"></span>--></td>
							</tr>
							<!-- 36252 fine -->
							<tr>
							<td></td>
								<td>
									<% 
  WebCheckBox StampaConsuntivoCommessaExecutePrint =  
     new com.thera.thermfw.web.WebCheckBox("StampaConsuntivoCommessa", "ExecutePrint"); 
  StampaConsuntivoCommessaExecutePrint.setParent(StampaConsuntivoCommessaForm); 
%>
<input id="<%=StampaConsuntivoCommessaExecutePrint.getId()%>" name="<%=StampaConsuntivoCommessaExecutePrint.getName()%>" type="checkbox" value="Y"><%
  StampaConsuntivoCommessaExecutePrint.write(out); 
%>

								</td>
							</tr>	
						</table>
					<% mytabbed.endTab(); %> 
</div>					
					<div class="tabbed_page" id="<%=mytabbed.getTabPageId("FiltroTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("FiltroTab"); %>
						<table cellpadding="2" cellspacing="2" style="margin: 7 7 7 7;" width="98%">
							<tr id="TR_FILTRO_B">
								<td>
									<table>
										<tr>
											<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "ArticoliRisorse", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="ArticoliRisorse"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebComboBox StampaConsuntivoCommessaArticoliRisorse =  
     new com.thera.thermfw.web.WebComboBox("StampaConsuntivoCommessa", "ArticoliRisorse", null); 
  StampaConsuntivoCommessaArticoliRisorse.setParent(StampaConsuntivoCommessaForm); 
  StampaConsuntivoCommessaArticoliRisorse.setOnChange("onArticoliRisorseModifAction()"); 
%>
<select id="<%=StampaConsuntivoCommessaArticoliRisorse.getId()%>" name="<%=StampaConsuntivoCommessaArticoliRisorse.getName()%>"><% 
  StampaConsuntivoCommessaArticoliRisorse.write(out); 
%> 
</select>
											</td>
										</tr>
										<tr>
											<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "TipoRiga", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="TipoRiga"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebComboBox StampaConsuntivoCommessaTipoRiga =  
     new com.thera.thermfw.web.WebComboBox("StampaConsuntivoCommessa", "TipoRiga", null); 
  StampaConsuntivoCommessaTipoRiga.setParent(StampaConsuntivoCommessaForm); 
%>
<select id="<%=StampaConsuntivoCommessaTipoRiga.getId()%>" name="<%=StampaConsuntivoCommessaTipoRiga.getName()%>"><% 
  StampaConsuntivoCommessaTipoRiga.write(out); 
%> 
</select>
											</td>
										</tr>
										<tr>
											<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "DataSaldoIniziale", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataSaldoIniziale"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebTextInput StampaConsuntivoCommessaDataSaldoIniziale =  
     new com.thera.thermfw.web.WebTextInput("StampaConsuntivoCommessa", "DataSaldoIniziale"); 
  StampaConsuntivoCommessaDataSaldoIniziale.setShowCalendarBtn(true); 
  StampaConsuntivoCommessaDataSaldoIniziale.setParent(StampaConsuntivoCommessaForm); 
%>
<input class="<%=StampaConsuntivoCommessaDataSaldoIniziale.getClassType()%>" id="<%=StampaConsuntivoCommessaDataSaldoIniziale.getId()%>" maxlength="<%=StampaConsuntivoCommessaDataSaldoIniziale.getMaxLength()%>" name="<%=StampaConsuntivoCommessaDataSaldoIniziale.getName()%>" size="<%=StampaConsuntivoCommessaDataSaldoIniziale.getSize()%>" type="text"><% 
  StampaConsuntivoCommessaDataSaldoIniziale.write(out); 
%>
</td>
											<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "DataSaldoFinale", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataSaldoFinale"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebTextInput StampaConsuntivoCommessaDataSaldoFinale =  
     new com.thera.thermfw.web.WebTextInput("StampaConsuntivoCommessa", "DataSaldoFinale"); 
  StampaConsuntivoCommessaDataSaldoFinale.setShowCalendarBtn(true); 
  StampaConsuntivoCommessaDataSaldoFinale.setParent(StampaConsuntivoCommessaForm); 
%>
<input class="<%=StampaConsuntivoCommessaDataSaldoFinale.getClassType()%>" id="<%=StampaConsuntivoCommessaDataSaldoFinale.getId()%>" maxlength="<%=StampaConsuntivoCommessaDataSaldoFinale.getMaxLength()%>" name="<%=StampaConsuntivoCommessaDataSaldoFinale.getName()%>" size="<%=StampaConsuntivoCommessaDataSaldoFinale.getSize()%>" type="text"><% 
  StampaConsuntivoCommessaDataSaldoFinale.write(out); 
%>
</td>
										</tr>
										<tr>
											<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "TipoRisorsa", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="TipoRisorsa"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebComboBox StampaConsuntivoCommessaTipoRisorsa =  
     new com.thera.thermfw.web.WebComboBox("StampaConsuntivoCommessa", "TipoRisorsa", null); 
  StampaConsuntivoCommessaTipoRisorsa.setParent(StampaConsuntivoCommessaForm); 
%>
<select id="<%=StampaConsuntivoCommessaTipoRisorsa.getId()%>" name="<%=StampaConsuntivoCommessaTipoRisorsa.getName()%>"><% 
  StampaConsuntivoCommessaTipoRisorsa.write(out); 
%> 
</select></td>
										</tr>
										<tr>
											<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaConsuntivoCommessa", "LivelloRisorsa", null); 
   label.setParent(StampaConsuntivoCommessaForm); 
%><label class="<%=label.getClassType()%>" for="LivelloRisorsa"><%label.write(out);%></label><%}%></td>								
											<td><% 
  WebComboBox StampaConsuntivoCommessaLivelloRisorsa =  
     new com.thera.thermfw.web.WebComboBox("StampaConsuntivoCommessa", "LivelloRisorsa", null); 
  StampaConsuntivoCommessaLivelloRisorsa.setParent(StampaConsuntivoCommessaForm); 
%>
<select id="<%=StampaConsuntivoCommessaLivelloRisorsa.getId()%>" name="<%=StampaConsuntivoCommessaLivelloRisorsa.getName()%>"><% 
  StampaConsuntivoCommessaLivelloRisorsa.write(out); 
%> 
</select></td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="5">
									<table width="100%">									
										<% 
   request.setAttribute("parentForm", StampaConsuntivoCommessaForm); 
   String CDForCondizioniFiltri$it$thera$thip$cs$CondizioniFiltri$jsp = "CondizioniFiltri"; 
%>
<jsp:include page="/it/thera/thip/cs/CondizioniFiltri.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForCondizioniFiltri$it$thera$thip$cs$CondizioniFiltri$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="CondizioniFiltri"></span>-->								
									</table>
								</td>									
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
			<td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(StampaConsuntivoCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
		</tr>
	</table>
<%
  StampaConsuntivoCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = StampaConsuntivoCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", StampaConsuntivoCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              StampaConsuntivoCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, StampaConsuntivoCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, StampaConsuntivoCommessaBODC.getErrorList().getErrors()); 
           if(StampaConsuntivoCommessaBODC.getConflict() != null) 
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
     if(StampaConsuntivoCommessaBODC != null && !StampaConsuntivoCommessaBODC.close(false)) 
        errors.addAll(0, StampaConsuntivoCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = StampaConsuntivoCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", StampaConsuntivoCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = StampaConsuntivoCommessaForm.getConflictPage(); 
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

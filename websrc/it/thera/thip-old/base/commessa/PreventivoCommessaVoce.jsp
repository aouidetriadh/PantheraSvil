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
  BODataCollector PreventivoCommessaVoceBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm PreventivoCommessaVoceForm =  
     new com.thera.thermfw.web.WebForm(request, response, "PreventivoCommessaVoceForm", "PreventivoCommessaVoce", null, "it.thera.thip.base.commessa.web.PreventivoCommessaVoceRidottaFAA", false, false, true, true, true, true, null, 1, true, "it/thera/thip/base/commessa/PreventivoCommessaVoce.js"); 
  PreventivoCommessaVoceForm.setServletEnvironment(se); 
  PreventivoCommessaVoceForm.setJSTypeList(jsList); 
  PreventivoCommessaVoceForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  PreventivoCommessaVoceForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  PreventivoCommessaVoceForm.setWebFormModifierClass("it.thera.thip.base.commessa.web.PreventivoCommessaVoceFormModifier"); 
  PreventivoCommessaVoceForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = PreventivoCommessaVoceForm.getMode(); 
  String key = PreventivoCommessaVoceForm.getKey(); 
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
        PreventivoCommessaVoceForm.outTraceInfo(getClass().getName()); 
        String collectorName = PreventivoCommessaVoceForm.findBODataCollectorName(); 
                PreventivoCommessaVoceBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (PreventivoCommessaVoceBODC instanceof WebDataCollector) 
            ((WebDataCollector)PreventivoCommessaVoceBODC).setServletEnvironment(se); 
        PreventivoCommessaVoceBODC.initialize("PreventivoCommessaVoce", true, 1); 
        PreventivoCommessaVoceForm.setBODataCollector(PreventivoCommessaVoceBODC); 
        int rcBODC = PreventivoCommessaVoceForm.initSecurityServices(); 
        mode = PreventivoCommessaVoceForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           PreventivoCommessaVoceForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = PreventivoCommessaVoceBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              PreventivoCommessaVoceForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(PreventivoCommessaVoceForm); 
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
  myToolBarTB.setParent(PreventivoCommessaVoceForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=PreventivoCommessaVoceForm.getBodyOnBeforeUnload()%>" onload="<%=PreventivoCommessaVoceForm.getBodyOnLoad()%>" onunload="<%=PreventivoCommessaVoceForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   PreventivoCommessaVoceForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = PreventivoCommessaVoceForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", PreventivoCommessaVoceBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=PreventivoCommessaVoceForm.getServlet()%>" method="post" name="PreventivoCommessaVoceForm" style="height:100%"><%
  PreventivoCommessaVoceForm.writeFormStartElements(out); 
%>

		<table cellpadding="1" cellspacing="1" height="100%" width="100%">
			<!-- **************************************************************************************************** -->
			<!-- Menubar -->
			<tr>
				<td style="height:0">
					<% menuBar.writeElements(out); %> 

				</td>
			</tr>
			<!-- **************************************************************************************************** -->
			<!-- Toolbar -->
			<tr>
				<td style="height:0">
					<% myToolBarTB.writeChildren(out); %> 

				</td>
			</tr>
			<!-- **************************************************************************************************** -->
			<!-- Campi nascosti -->
			<tr>
				<td style="height:0px">
					<% 
  WebTextInput PreventivoCommessaVoceIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdAzienda"); 
  PreventivoCommessaVoceIdAzienda.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdAzienda.getClassType()%>" id="<%=PreventivoCommessaVoceIdAzienda.getId()%>" maxlength="<%=PreventivoCommessaVoceIdAzienda.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdAzienda.getName()%>" size="<%=PreventivoCommessaVoceIdAzienda.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceIdAzienda.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceIdAnnoPrevc =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdAnnoPrevc"); 
  PreventivoCommessaVoceIdAnnoPrevc.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdAnnoPrevc.getClassType()%>" id="<%=PreventivoCommessaVoceIdAnnoPrevc.getId()%>" maxlength="<%=PreventivoCommessaVoceIdAnnoPrevc.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdAnnoPrevc.getName()%>" size="<%=PreventivoCommessaVoceIdAnnoPrevc.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceIdAnnoPrevc.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceIdNumeroPrevc =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdNumeroPrevc"); 
  PreventivoCommessaVoceIdNumeroPrevc.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdNumeroPrevc.getClassType()%>" id="<%=PreventivoCommessaVoceIdNumeroPrevc.getId()%>" maxlength="<%=PreventivoCommessaVoceIdNumeroPrevc.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdNumeroPrevc.getName()%>" size="<%=PreventivoCommessaVoceIdNumeroPrevc.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceIdNumeroPrevc.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceIdRigacPrv =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdRigacPrv"); 
  PreventivoCommessaVoceIdRigacPrv.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdRigacPrv.getClassType()%>" id="<%=PreventivoCommessaVoceIdRigacPrv.getId()%>" maxlength="<%=PreventivoCommessaVoceIdRigacPrv.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdRigacPrv.getName()%>" size="<%=PreventivoCommessaVoceIdRigacPrv.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceIdRigacPrv.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceIdSubRigacPrv =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdSubRigacPrv"); 
  PreventivoCommessaVoceIdSubRigacPrv.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdSubRigacPrv.getClassType()%>" id="<%=PreventivoCommessaVoceIdSubRigacPrv.getId()%>" maxlength="<%=PreventivoCommessaVoceIdSubRigacPrv.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdSubRigacPrv.getName()%>" size="<%=PreventivoCommessaVoceIdSubRigacPrv.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceIdSubRigacPrv.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceIdRigavPrv =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdRigavPrv"); 
  PreventivoCommessaVoceIdRigavPrv.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdRigavPrv.getClassType()%>" id="<%=PreventivoCommessaVoceIdRigavPrv.getId()%>" maxlength="<%=PreventivoCommessaVoceIdRigavPrv.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdRigavPrv.getName()%>" size="<%=PreventivoCommessaVoceIdRigavPrv.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceIdRigavPrv.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceIdSubRigavPrv =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdSubRigavPrv"); 
  PreventivoCommessaVoceIdSubRigavPrv.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdSubRigavPrv.getClassType()%>" id="<%=PreventivoCommessaVoceIdSubRigavPrv.getId()%>" maxlength="<%=PreventivoCommessaVoceIdSubRigavPrv.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdSubRigavPrv.getName()%>" size="<%=PreventivoCommessaVoceIdSubRigavPrv.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceIdSubRigavPrv.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceSplRiga =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "SplRiga"); 
  PreventivoCommessaVoceSplRiga.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceSplRiga.getClassType()%>" id="<%=PreventivoCommessaVoceSplRiga.getId()%>" maxlength="<%=PreventivoCommessaVoceSplRiga.getMaxLength()%>" name="<%=PreventivoCommessaVoceSplRiga.getName()%>" size="<%=PreventivoCommessaVoceSplRiga.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceSplRiga.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceGeneraRigaDettaglio =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "GeneraRigaDettaglio"); 
  PreventivoCommessaVoceGeneraRigaDettaglio.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceGeneraRigaDettaglio.getClassType()%>" id="<%=PreventivoCommessaVoceGeneraRigaDettaglio.getId()%>" maxlength="<%=PreventivoCommessaVoceGeneraRigaDettaglio.getMaxLength()%>" name="<%=PreventivoCommessaVoceGeneraRigaDettaglio.getName()%>" size="<%=PreventivoCommessaVoceGeneraRigaDettaglio.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceGeneraRigaDettaglio.write(out); 
%>
<!-- 29166 -->
					<% 
  WebTextInput PreventivoCommessaVoceDescrizioneArticolo =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "DescrizioneArticolo"); 
  PreventivoCommessaVoceDescrizioneArticolo.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceDescrizioneArticolo.getClassType()%>" id="<%=PreventivoCommessaVoceDescrizioneArticolo.getId()%>" maxlength="<%=PreventivoCommessaVoceDescrizioneArticolo.getMaxLength()%>" name="<%=PreventivoCommessaVoceDescrizioneArticolo.getName()%>" size="<%=PreventivoCommessaVoceDescrizioneArticolo.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceDescrizioneArticolo.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceValuta =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "Valuta"); 
  PreventivoCommessaVoceValuta.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceValuta.getClassType()%>" id="<%=PreventivoCommessaVoceValuta.getId()%>" maxlength="<%=PreventivoCommessaVoceValuta.getMaxLength()%>" name="<%=PreventivoCommessaVoceValuta.getName()%>" size="<%=PreventivoCommessaVoceValuta.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceValuta.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceValutaAziendale =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "ValutaAziendale"); 
  PreventivoCommessaVoceValutaAziendale.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceValutaAziendale.getClassType()%>" id="<%=PreventivoCommessaVoceValutaAziendale.getId()%>" maxlength="<%=PreventivoCommessaVoceValutaAziendale.getMaxLength()%>" name="<%=PreventivoCommessaVoceValutaAziendale.getName()%>" size="<%=PreventivoCommessaVoceValutaAziendale.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceValutaAziendale.write(out); 
%>

					<% 
  WebTextInput PreventivoCommessaVoceCambio =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "Cambio"); 
  PreventivoCommessaVoceCambio.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceCambio.getClassType()%>" id="<%=PreventivoCommessaVoceCambio.getId()%>" maxlength="<%=PreventivoCommessaVoceCambio.getMaxLength()%>" name="<%=PreventivoCommessaVoceCambio.getName()%>" size="<%=PreventivoCommessaVoceCambio.getSize()%>" type="hidden"><% 
  PreventivoCommessaVoceCambio.write(out); 
%>

					<input id="ArticoloServizio" name="ArticoloServizio" type="hidden">
				</td>
			</tr>
			<!-- **************************************************************************************************** -->
			<!-- Pannello tabulare principale -->
			<tr>
				<td height="100%">
					<!--<span class="tabbed" id="MainTabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed MainTabbed = new com.thera.thermfw.web.WebTabbed("MainTabbed", "100%", "100%"); 
  MainTabbed.setParent(PreventivoCommessaVoceForm); 
 MainTabbed.addTab("GeneraleTab", "it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "GeneraleTab", "PreventivoCommessaVoce", null, null, null, null); 
 MainTabbed.addTab("PrezziCostoRisorsa", "it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "PrezziCostoRisorsa", "PreventivoCommessaVoce", null, null, null, null); 
 MainTabbed.addTab("CommentiMultimediaTab", "it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "CommentiMultimediaTab", "PreventivoCommessaVoce", null, null, null, null); 
 MainTabbed.addTab("RiepilogoTab", "it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "RiepilogoTab", "PreventivoCommessaVoce", null, null, null, null); 
 MainTabbed.addTab("TabDes", "it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "Descrizioni", "PreventivoCommessaVoce", null, null, null, null); 
  MainTabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
						<!-- **************************************************************************************** -->
						<!-- Cartella Generale -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("GeneraleTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("GeneraleTab"); %>
							<table>
								<tr>
									<td>
										<table cellpadding="1" cellspacing="1">
											<tr>
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "SequenzaRiga", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="SequenzaRiga"><%label.write(out);%></label><%}%>
												</td>
												<td>
													<% 
  WebTextInput PreventivoCommessaVoceSequenzaRiga =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "SequenzaRiga"); 
  PreventivoCommessaVoceSequenzaRiga.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceSequenzaRiga.getClassType()%>" id="<%=PreventivoCommessaVoceSequenzaRiga.getId()%>" maxlength="<%=PreventivoCommessaVoceSequenzaRiga.getMaxLength()%>" name="<%=PreventivoCommessaVoceSequenzaRiga.getName()%>" size="5" type="text"><% 
  PreventivoCommessaVoceSequenzaRiga.write(out); 
%>

												</td>
												<td>&nbsp;</td>
												<td>&nbsp;</td>
												<td>
													<% 
  WebCheckBox PreventivoCommessaVoceNoFatturare =  
     new com.thera.thermfw.web.WebCheckBox("PreventivoCommessaVoce", "NoFatturare"); 
  PreventivoCommessaVoceNoFatturare.setParent(PreventivoCommessaVoceForm); 
%>
<input id="<%=PreventivoCommessaVoceNoFatturare.getId()%>" name="<%=PreventivoCommessaVoceNoFatturare.getName()%>" type="checkbox" value="Y"><%
  PreventivoCommessaVoceNoFatturare.write(out); 
%>

												</td>
											</tr>
											<tr>
						                    	<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "TipoRigav", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="TipoRigav"><%label.write(out);%></label><%}%></td>
						                        <td><% 
  WebComboBox PreventivoCommessaVoceTipoRigav =  
     new com.thera.thermfw.web.WebComboBox("PreventivoCommessaVoce", "TipoRigav", null); 
  PreventivoCommessaVoceTipoRigav.setParent(PreventivoCommessaVoceForm); 
%>
<select id="<%=PreventivoCommessaVoceTipoRigav.getId()%>" name="<%=PreventivoCommessaVoceTipoRigav.getName()%>"><% 
  PreventivoCommessaVoceTipoRigav.write(out); 
%> 
</select></td>
						                        <td>&nbsp;</td>
						                        <td>&nbsp;</td>
						                        <td>&nbsp;</td>
						                    </tr>
						                    <tr>
						                        <td colspan="5"> <% 
   request.setAttribute("parentForm", PreventivoCommessaVoceForm); 
   String CDForDescrizione$it$thera$thip$cs$Descrizione$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/Descrizione.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$Descrizione$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="descrizioneComponent" name="descrizioneComponent"></span>--></td>
						                    </tr>
						                    <tr id="trRisorsa">
								                <td colspan="6">
								                  <table style="margin: -5 2 2 5;" width="97%">
								                    <tr>
								                      <td>
								                        <FIELDSET name="RisorsaFieldSet">
								                          <LEGEND align="top">
								                            <label class="thLabel" id="RisorsaLabel" name="RisorsaLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "Risorsa", null, null, null, null); 
 label.setParent(PreventivoCommessaVoceForm); 
label.write(out); }%> 
</label>
								                          </LEGEND>
								                          <table border="0">
								                            <tr>
								                              <td width="105"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "TipoRisorsa", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="TipoRisorsa"><%label.write(out);%></label><%}%></td>
								                              <td><% 
  WebComboBox PreventivoCommessaVoceTipoRisorsa =  
     new com.thera.thermfw.web.WebComboBox("PreventivoCommessaVoce", "TipoRisorsa", null); 
  PreventivoCommessaVoceTipoRisorsa.setParent(PreventivoCommessaVoceForm); 
%>
<select id="<%=PreventivoCommessaVoceTipoRisorsa.getId()%>" name="<%=PreventivoCommessaVoceTipoRisorsa.getName()%>"><% 
  PreventivoCommessaVoceTipoRisorsa.write(out); 
%> 
</select></td>
								                            </tr>
								                            <tr>
								                              <td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "LivelloRisorsa", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="LivelloRisorsa"><%label.write(out);%></label><%}%></td>
								                              <td><% 
  WebComboBox PreventivoCommessaVoceLivelloRisorsa =  
     new com.thera.thermfw.web.WebComboBox("PreventivoCommessaVoce", "LivelloRisorsa", null); 
  PreventivoCommessaVoceLivelloRisorsa.setParent(PreventivoCommessaVoceForm); 
%>
<select id="<%=PreventivoCommessaVoceLivelloRisorsa.getId()%>" name="<%=PreventivoCommessaVoceLivelloRisorsa.getName()%>"><% 
  PreventivoCommessaVoceLivelloRisorsa.write(out); 
%> 
</select></td>
								                            </tr>
								                            <tr>
								                              <td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "IdRisorsa", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="Risorsa"><%label.write(out);%></label><%}%></td>
								                              <td nowrap><% 
  WebMultiSearchForm PreventivoCommessaVoceRisorsa =  
     new com.thera.thermfw.web.WebMultiSearchForm("PreventivoCommessaVoce", "Risorsa", false, false, true, 1, null, "36"); 
  PreventivoCommessaVoceRisorsa.setExtraRelatedClassAD("ArticoloServizio"); 
  PreventivoCommessaVoceRisorsa.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceRisorsa.setOnKeyFocus("memorizzaCodiceRisorsa()"); 
  PreventivoCommessaVoceRisorsa.setOnKeyBlur("confrontaCodiceRisorsa()"); 
  PreventivoCommessaVoceRisorsa.setOnKeyChange("variazioneRisorsa()"); 
  PreventivoCommessaVoceRisorsa.setOnSearchBack("recuperaDatiRisorsa()"); 
  PreventivoCommessaVoceRisorsa.setAdditionalRestrictConditions("TipoRisorsa, TipoRisorsa; LivelloRisorsa, LivelloRisorsa"); 
  PreventivoCommessaVoceRisorsa.write(out); 
%>
<!--<span class="multisearchform" id="Risorsa"></span>--></td>
								                            </tr>
								                          </table>
								                        </FIELDSET>
								                       </td>
								                    </tr>
								                  </table>
								                  </td>						                    	
						                    </tr>
						                    <tr id="trArticolo">
											<!-- Proxy Articolo -->
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "IdArticolo", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="RArticolo"><%label.write(out);%></label><%}%>
												</td>
												<td colspan="3" nowrap>
													<% 
  WebMultiSearchForm PreventivoCommessaVoceRArticolo =  
     new it.thera.thip.base.articolo.web.ArticoloMultiSearchForm("PreventivoCommessaVoce", "RArticolo", false, false, true, 1, null, null); 
  PreventivoCommessaVoceRArticolo.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceRArticolo.setOnKeyFocus("memorizzaCodiceArticolo()"); 
  PreventivoCommessaVoceRArticolo.setOnKeyBlur("confrontaCodiceArticolo()"); 
  PreventivoCommessaVoceRArticolo.setOnKeyChange("variazioneArticolo()"); 
  PreventivoCommessaVoceRArticolo.setOnSearchBack("recuperaDatiArticolo()"); 
  PreventivoCommessaVoceRArticolo.setFixedRestrictConditions("IdSchemaCfg,NULL_VALUE;ArticoloDatiProduz.IdClasseMerclg,NOT_NULL_VALUE;DatiComuniEstesi.Stato,V"); 
  PreventivoCommessaVoceRArticolo.setAdditionalRestrictConditions("TipoRigav,TipoArticolo"); 
  PreventivoCommessaVoceRArticolo.setSpecificDOList("it.thera.thip.base.commessa.web.RicercaArticoliDOList"); 
  PreventivoCommessaVoceRArticolo.write(out); 
%>
<!--<span class="articolomultisearchform" id="RArticolo"></span>-->
												</td>
												<td>&nbsp;</td>
											</tr>
											<tr id="trArticoloVersione">
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "IdVersione", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="VersioneArticolo"><%label.write(out);%></label><%}%>
												</td>
												<td colspan="3" nowrap>
													<% 
  WebMultiSearchForm PreventivoCommessaVoceVersioneArticolo =  
     new com.thera.thermfw.web.WebMultiSearchForm("PreventivoCommessaVoce", "VersioneArticolo", false, false, true, 1, null, null); 
  PreventivoCommessaVoceVersioneArticolo.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceVersioneArticolo.write(out); 
%>
<!--<span class="multisearchform" id="VersioneArticolo"></span>-->
												</td>
												<td>&nbsp;</td>
												<td>&nbsp;</td>
											</tr>
											<tr id="trArticoloConfig">
												<td nowrap>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "IdEsternoConfig", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="Configurazione"><%label.write(out);%></label><%}%>
												</td>
												<td colspan="3" nowrap>
													<div id="confdiv">
														<% 
  WebMultiSearchForm PreventivoCommessaVoceConfigurazione =  
     new it.thera.thip.datiTecnici.configuratore.web.ConfigurazioneMultiSearchForm("PreventivoCommessaVoce", "Configurazione", false, false, true, 1, null, null); 
  PreventivoCommessaVoceConfigurazione.setExtraRelatedClassAD("IdAzienda,IdConfigurazione"); 
  PreventivoCommessaVoceConfigurazione.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceConfigurazione.setAdditionalRestrictConditions("IdArticolo,IdArticolo"); 
  PreventivoCommessaVoceConfigurazione.write(out); 
%>
<!--<span class="configurazionemultisearchform" id="Configurazione"></span>-->
													</div>
												</td>
												<td>&nbsp;</td>
												<td>&nbsp;</td>
											</tr>											
											<tr id="TR_COEF_IMP_RIC">
												<td>
													<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "CoeffImpiego", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="CoeffImpiego"><%label.write(out);%></label><%}%>
												</td>
												<td>
													<% 
  WebTextInput PreventivoCommessaVoceCoeffImpiego =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "CoeffImpiego"); 
  PreventivoCommessaVoceCoeffImpiego.setOnChange("ricalcoloQuantita()"); 
  PreventivoCommessaVoceCoeffImpiego.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceCoeffImpiego.getClassType()%>" id="<%=PreventivoCommessaVoceCoeffImpiego.getId()%>" maxlength="<%=PreventivoCommessaVoceCoeffImpiego.getMaxLength()%>" name="<%=PreventivoCommessaVoceCoeffImpiego.getName()%>" size="5" type="text"><% 
  PreventivoCommessaVoceCoeffImpiego.write(out); 
%>

												</td>
												<td>
													<% 
  WebCheckBox PreventivoCommessaVoceBloccoRicalcolo =  
     new com.thera.thermfw.web.WebCheckBox("PreventivoCommessaVoce", "BloccoRicalcolo"); 
  PreventivoCommessaVoceBloccoRicalcolo.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceBloccoRicalcolo.setOnClick("gestioneBloccoRicalcolo()"); 
%>
<input id="<%=PreventivoCommessaVoceBloccoRicalcolo.getId()%>" name="<%=PreventivoCommessaVoceBloccoRicalcolo.getName()%>" type="checkbox" value="Y"><%
  PreventivoCommessaVoceBloccoRicalcolo.write(out); 
%>

												</td>
											</tr>
										</table>
									</td>
								</tr>

								<tr>
									<td>
										<table cellpadding="1" cellspacing="1">
											<td align="left" valign="top" width="50%">
												<fieldset>
													<legend><label class="thLabel" id="LabelGroupBoxQuantita">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "QuantitaGroupBox", null, null, null, null); 
 label.setParent(PreventivoCommessaVoceForm); 
label.write(out); }%> 
</label></legend>
													<table cellpadding="1" cellspacing="1">
														<tr id="SezUMVen">
															<!-- Campo Qta Vendita -->
															<td nowrap>
																<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "QtaUmVenMag", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="QtaUmVenMag"><%label.write(out);%></label><%}%>
															</td>
															<td>
																<% 
  WebTextInput PreventivoCommessaVoceQtaUmVenMag =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "QtaUmVenMag"); 
  PreventivoCommessaVoceQtaUmVenMag.setOnChange("recuperaDatiCondVendita()"); 
  PreventivoCommessaVoceQtaUmVenMag.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceQtaUmVenMag.getClassType()%>" id="<%=PreventivoCommessaVoceQtaUmVenMag.getId()%>" maxlength="<%=PreventivoCommessaVoceQtaUmVenMag.getMaxLength()%>" name="<%=PreventivoCommessaVoceQtaUmVenMag.getName()%>" size="12" type="text"><% 
  PreventivoCommessaVoceQtaUmVenMag.write(out); 
%>

															</td>
															<!-- Combo UM Vendita -->
															<td>
																<% 
  WebSearchComboBox PreventivoCommessaVoceUmVenMag =  
     new com.thera.thermfw.web.WebSearchComboBox("PreventivoCommessaVoce", "UmVenMag", null, 2, "20", false, "getListaUMRiferimento"); 
  PreventivoCommessaVoceUmVenMag.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceUmVenMag.setOnChange("variazioneUnitaMisura()"); 
  PreventivoCommessaVoceUmVenMag.write(out); 
%>
<!--<span class="searchcombobox" id="UmVenMag" name="UmVenMag"></span>-->
															</td>
														</tr>
														<tr id="SezUMPrm">
															<!-- Campo Qta Primaria -->
															<td nowrap>
																<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "QtaUmPrmMag", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="QtaUmPrmMag"><%label.write(out);%></label><%}%>
															</td>
															<td>
																<% 
  WebTextInput PreventivoCommessaVoceQtaUmPrmMag =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "QtaUmPrmMag"); 
  PreventivoCommessaVoceQtaUmPrmMag.setOnChange("recuperaDatiArticoloServizio()"); 
  PreventivoCommessaVoceQtaUmPrmMag.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceQtaUmPrmMag.getClassType()%>" id="<%=PreventivoCommessaVoceQtaUmPrmMag.getId()%>" maxlength="<%=PreventivoCommessaVoceQtaUmPrmMag.getMaxLength()%>" name="<%=PreventivoCommessaVoceQtaUmPrmMag.getName()%>" size="12" type="text"><% 
  PreventivoCommessaVoceQtaUmPrmMag.write(out); 
%>

															</td>
															<!-- Campo Servizio UM Primaria -->
															<td colspan="2" nowrap>
																<% 
  WebSearchComboBox PreventivoCommessaVoceUmPrmMag =  
     new com.thera.thermfw.web.WebSearchComboBox("PreventivoCommessaVoce", "UmPrmMag", null, 2, "20", false, "getListaUMTempo"); 
  PreventivoCommessaVoceUmPrmMag.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceUmPrmMag.setOnChange("recuperaDatiArticoloServizio()"); 
  PreventivoCommessaVoceUmPrmMag.write(out); 
%>
<!--<span class="searchcombobox" id="UmPrmMag" name="UmPrmMag"></span>-->
																<!--input type="text" id="IdUmPrmMag" name="IdUmPrmMag" size="20"/-->
															</td>
															<td>&nbsp;</td>
														</tr>
														<tr id="SezUMSecondaria" name="SezUMSecondaria" style="display:none">
															<!-- Campo Qta Secondaria -->
															<td nowrap>
																<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "QtaUmSecMag", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="QtaUmSecMag"><%label.write(out);%></label><%}%>
															</td>
															<td>
																<% 
  WebTextInput PreventivoCommessaVoceQtaUmSecMag =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "QtaUmSecMag"); 
  PreventivoCommessaVoceQtaUmSecMag.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceQtaUmSecMag.getClassType()%>" id="<%=PreventivoCommessaVoceQtaUmSecMag.getId()%>" maxlength="<%=PreventivoCommessaVoceQtaUmSecMag.getMaxLength()%>" name="<%=PreventivoCommessaVoceQtaUmSecMag.getName()%>" size="12" type="text"><% 
  PreventivoCommessaVoceQtaUmSecMag.write(out); 
%>

															</td>
															<!-- Campo Servizio UM Secondaria -->
															<td colspan="2" nowrap>
																<% 
  WebTextInput PreventivoCommessaVoceIdUmSecMag =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "IdUmSecMag"); 
  PreventivoCommessaVoceIdUmSecMag.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceIdUmSecMag.getClassType()%>" id="<%=PreventivoCommessaVoceIdUmSecMag.getId()%>" maxlength="<%=PreventivoCommessaVoceIdUmSecMag.getMaxLength()%>" name="<%=PreventivoCommessaVoceIdUmSecMag.getName()%>" size="20" type="text"><% 
  PreventivoCommessaVoceIdUmSecMag.write(out); 
%>

															</td>
															<td>&nbsp;</td>
														</tr>
													</table>
												</fieldset>
											</td>
											<td width="50%">
												<fieldset>
													<legend><label class="thLabel" id="LabelGroupBoxDateConsegna">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "DateConsegnaGroupBox", null, null, null, null); 
 label.setParent(PreventivoCommessaVoceForm); 
label.write(out); }%> 
</label></legend>
													<table cellpadding="1" cellspacing="1">
														<tr>
															<!-- Campo Data Consegna Richiesta -->
															<td nowrap>
																<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "DataConsegRcs", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="DataConsegRcs"><%label.write(out);%></label><%}%>
															</td>
															<td nowrap>
																<% 
  WebTextInput PreventivoCommessaVoceDataConsegRcs =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "DataConsegRcs"); 
  PreventivoCommessaVoceDataConsegRcs.setOnChange("gestDataConsegnaRichiesta()"); 
  PreventivoCommessaVoceDataConsegRcs.setShowCalendarBtn(true); 
  PreventivoCommessaVoceDataConsegRcs.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceDataConsegRcs.getClassType()%>" id="<%=PreventivoCommessaVoceDataConsegRcs.getId()%>" maxlength="<%=PreventivoCommessaVoceDataConsegRcs.getMaxLength()%>" name="<%=PreventivoCommessaVoceDataConsegRcs.getName()%>" size="12" type="text"><% 
  PreventivoCommessaVoceDataConsegRcs.write(out); 
%>

															</td>
															<!-- Campo Settimana Consegna Richiesta -->
															<td nowrap>
																<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "SettConsegRcs", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="SettConsegRcs"><%label.write(out);%></label><%}%>
															</td>
															<td nowrap>
																<% 
  WebTextInput PreventivoCommessaVoceSettConsegRcs =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "SettConsegRcs"); 
  PreventivoCommessaVoceSettConsegRcs.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceSettConsegRcs.getClassType()%>" id="<%=PreventivoCommessaVoceSettConsegRcs.getId()%>" maxlength="<%=PreventivoCommessaVoceSettConsegRcs.getMaxLength()%>" name="<%=PreventivoCommessaVoceSettConsegRcs.getName()%>" size="8" type="text"><% 
  PreventivoCommessaVoceSettConsegRcs.write(out); 
%>

															</td>
														</tr>
														<tr>
															<!-- Campo Data Consegna Confermata -->
															<td nowrap>
																<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "DataConsegPrv", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="DataConsegPrv"><%label.write(out);%></label><%}%>
															</td>
															<td nowrap>
																<% 
  WebTextInput PreventivoCommessaVoceDataConsegPrv =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "DataConsegPrv"); 
  PreventivoCommessaVoceDataConsegPrv.setOnChange("gestDataConsegnaConfermata()"); 
  PreventivoCommessaVoceDataConsegPrv.setShowCalendarBtn(true); 
  PreventivoCommessaVoceDataConsegPrv.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceDataConsegPrv.getClassType()%>" id="<%=PreventivoCommessaVoceDataConsegPrv.getId()%>" maxlength="<%=PreventivoCommessaVoceDataConsegPrv.getMaxLength()%>" name="<%=PreventivoCommessaVoceDataConsegPrv.getName()%>" size="12" type="text"><% 
  PreventivoCommessaVoceDataConsegPrv.write(out); 
%>

															</td>
															<!-- Campo Settimana Consegna Confermata -->
															<td nowrap>
																<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "SettConsegPrv", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="SettConsegPrv"><%label.write(out);%></label><%}%>
															</td>
															<td nowrap>
																<% 
  WebTextInput PreventivoCommessaVoceSettConsegPrv =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "SettConsegPrv"); 
  PreventivoCommessaVoceSettConsegPrv.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceSettConsegPrv.getClassType()%>" id="<%=PreventivoCommessaVoceSettConsegPrv.getId()%>" maxlength="<%=PreventivoCommessaVoceSettConsegPrv.getMaxLength()%>" name="<%=PreventivoCommessaVoceSettConsegPrv.getName()%>" size="8" type="text"><% 
  PreventivoCommessaVoceSettConsegPrv.write(out); 
%>

															</td>
														</tr>
													</table>
												</fieldset>
											</td>
										</table>
									</td>
								</tr>
								<tr>
									<td>
										<table>
											<tr>
												<!-- Dati Comuni Estesi - Stato -->
												<td nowrap>
													<table>
														<tr>
															<td>
																<% 
   request.setAttribute("parentForm", PreventivoCommessaVoceForm); 
   String CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp = "DatiComuniEstesi"; 
%>
<jsp:include page="/it/thera/thip/cs/DatiComuniEstesi.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DatiComuniEstesiStato" name="DatiComuniEstesiStato"></span>-->
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
							<!-- FINE TABELLA PRINCIPALE -->
						<% MainTabbed.endTab(); %> 
</div>

						<!-- **************************************************************************************** -->
						<!-- Cartella Prezzi/Sconti -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("PrezziCostoRisorsa")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("PrezziCostoRisorsa"); %>
							<table cellpadding="1" cellspacing="1">
               				 	<tr>
			                    	<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "CostoRiferimento", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="CostoRiferimento"><%label.write(out);%></label><%}%></td>
			                    	<td colspan="3"><% 
  WebTextInput PreventivoCommessaVoceCostoRiferimento =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "CostoRiferimento"); 
  PreventivoCommessaVoceCostoRiferimento.setOnChange("calcolaPrezzo()"); 
  PreventivoCommessaVoceCostoRiferimento.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceCostoRiferimento.getClassType()%>" id="<%=PreventivoCommessaVoceCostoRiferimento.getId()%>" maxlength="<%=PreventivoCommessaVoceCostoRiferimento.getMaxLength()%>" name="<%=PreventivoCommessaVoceCostoRiferimento.getName()%>" size="<%=PreventivoCommessaVoceCostoRiferimento.getSize()%>" type="text"><% 
  PreventivoCommessaVoceCostoRiferimento.write(out); 
%>
</td>
			                	</tr>
                				<tr>
									 <td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "IdSchemaCosti", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="SchemaCosto"><%label.write(out);%></label><%}%></td>
                   					 <td colspan="3"><% 
  WebMultiSearchForm PreventivoCommessaVoceSchemaCosto =  
     new com.thera.thermfw.web.WebMultiSearchForm("PreventivoCommessaVoce", "SchemaCosto", false, false, true, 1, null, null); 
  PreventivoCommessaVoceSchemaCosto.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceSchemaCosto.write(out); 
%>
<!--<span class="multisearchform" id="SchemaCosto" name="SchemaCosto"></span>--></td>
								</tr>
								<tr>
									 <td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "IdCompoCosti", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="ComponenteCosto"><%label.write(out);%></label><%}%></td>
                   					 <td colspan="3"><% 
  WebMultiSearchForm PreventivoCommessaVoceComponenteCosto =  
     new com.thera.thermfw.web.WebMultiSearchForm("PreventivoCommessaVoce", "ComponenteCosto", false, false, true, 1, null, null); 
  PreventivoCommessaVoceComponenteCosto.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceComponenteCosto.setAdditionalRestrictConditions("IdSchemaCosti,IdSchemaCosti"); 
  PreventivoCommessaVoceComponenteCosto.setSpecificDOList("it.thera.thip.base.commessa.web.PrevCommVoceComponenteCostoDoList"); 
  PreventivoCommessaVoceComponenteCosto.write(out); 
%>
<!--<span class="multisearchform" id="ComponenteCosto" name="ComponenteCosto"></span>--></td>
								</tr>
								<!-- 29032 inizio -->
								<tr id="TR_MARKUP">
									 <td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "Markup", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="Markup"><%label.write(out);%></label><%}%></td>
                   					 <td colspan="3"><% 
  WebTextInput PreventivoCommessaVoceMarkup =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "Markup"); 
  PreventivoCommessaVoceMarkup.setOnChange("calcolaPrezzoDaMarkup()"); 
  PreventivoCommessaVoceMarkup.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceMarkup.getClassType()%>" id="<%=PreventivoCommessaVoceMarkup.getId()%>" maxlength="<%=PreventivoCommessaVoceMarkup.getMaxLength()%>" name="<%=PreventivoCommessaVoceMarkup.getName()%>" size="<%=PreventivoCommessaVoceMarkup.getSize()%>" type="text"><% 
  PreventivoCommessaVoceMarkup.write(out); 
%>
</td>
								</tr>
								<!-- 29032 fine -->
								<tr>
									<!-- Campo Prezzo -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "Prezzo", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="Prezzo"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebTextInput PreventivoCommessaVocePrezzo =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "Prezzo"); 
  PreventivoCommessaVocePrezzo.setOnChange("calcolaMargine()"); 
  PreventivoCommessaVocePrezzo.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVocePrezzo.getClassType()%>" id="<%=PreventivoCommessaVocePrezzo.getId()%>" maxlength="<%=PreventivoCommessaVocePrezzo.getMaxLength()%>" name="<%=PreventivoCommessaVocePrezzo.getName()%>" size="12" type="text"><% 
  PreventivoCommessaVocePrezzo.write(out); 
%>

									</td>
									<!-- Campo Prezzo Extra -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "PrezzoExtra", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="PrezzoExtra"><%label.write(out);%></label><%}%>
									</td>
                  					<td>&nbsp;&nbsp;</td>
									<td colspan="2">
										<% 
  WebTextInput PreventivoCommessaVocePrezzoExtra =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "PrezzoExtra"); 
  PreventivoCommessaVocePrezzoExtra.setOnChange("calcolaMargine()"); 
  PreventivoCommessaVocePrezzoExtra.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVocePrezzoExtra.getClassType()%>" id="<%=PreventivoCommessaVocePrezzoExtra.getId()%>" maxlength="<%=PreventivoCommessaVocePrezzoExtra.getMaxLength()%>" name="<%=PreventivoCommessaVocePrezzoExtra.getName()%>" size="12" type="text"><% 
  PreventivoCommessaVocePrezzoExtra.write(out); 
%>

									</td>
								</tr>
								<tr>
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "TipoPrezzo", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="TipoPrezzo"><%label.write(out);%></label><%}%></td>
                   					<td colspan="3"><% 
  WebComboBox PreventivoCommessaVoceTipoPrezzo =  
     new com.thera.thermfw.web.WebComboBox("PreventivoCommessaVoce", "TipoPrezzo", null); 
  PreventivoCommessaVoceTipoPrezzo.setParent(PreventivoCommessaVoceForm); 
%>
<select id="<%=PreventivoCommessaVoceTipoPrezzo.getId()%>" name="<%=PreventivoCommessaVoceTipoPrezzo.getName()%>"><% 
  PreventivoCommessaVoceTipoPrezzo.write(out); 
%> 
</select></td>
								</tr>
								<tr>
								   	<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "ProvenienzaPrezzo", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="ProvenienzaPrezzo"><%label.write(out);%></label><%}%></td>
                   					<td><% 
  WebComboBox PreventivoCommessaVoceProvenienzaPrezzo =  
     new com.thera.thermfw.web.WebComboBox("PreventivoCommessaVoce", "ProvenienzaPrezzo", null); 
  PreventivoCommessaVoceProvenienzaPrezzo.setParent(PreventivoCommessaVoceForm); 
%>
<select id="<%=PreventivoCommessaVoceProvenienzaPrezzo.getId()%>" name="<%=PreventivoCommessaVoceProvenienzaPrezzo.getName()%>"><% 
  PreventivoCommessaVoceProvenienzaPrezzo.write(out); 
%> 
</select></td>
                   					<!-- 31094 inizio -->
                   					<td colspan="2" id="RicalcolaPrezzoTD">
                   						<button id="RicalcolaPrezzoBut" name="RicalcolaPrezzoBut" onclick="ricalcolaPrezzo()" type="button"><%= ResourceLoader.getString("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "RicalcolaPrezzo")%></button>
                   					</td>
                   					<!-- 31094 fine -->
								</tr>
							</table>
						<% MainTabbed.endTab(); %> 
</div>

						<!-- **************************************************************************************** -->
						<!-- Cartella Commenti/Multimedia -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("CommentiMultimediaTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("CommentiMultimediaTab"); %>
							<table height="20%">
								<tr>
									<td nowrap>
										<table cellpadding="1" cellspacing="1">
											<tr>
												<td>
													<% 
   request.setAttribute("parentForm", PreventivoCommessaVoceForm); 
   String CDForCommenti$com$thera$thermfw$cbs$CommentHandler$jsp = "Commenti"; 
%>
<jsp:include page="/com/thera/thermfw/cbs/CommentHandler.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForCommenti$com$thera$thermfw$cbs$CommentHandler$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="Commenti"></span>-->
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
							<table>
								<tr>
									<td nowrap>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "IdDocumentoMm", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="DocumentoMM"><%label.write(out);%></label><%}%>
									</td>
									<td nowrap>
										<% 
  WebMultiSearchForm PreventivoCommessaVoceDocumentoMM =  
     new com.thera.thermfw.web.WebMultiSearchForm("PreventivoCommessaVoce", "DocumentoMM", false, false, true, 1, null, null); 
  PreventivoCommessaVoceDocumentoMM.setParent(PreventivoCommessaVoceForm); 
  PreventivoCommessaVoceDocumentoMM.write(out); 
%>
<!--<span class="multisearchform" id="DocumentoMM"></span>-->
									</td>
								</tr>
								<tr>
									<td nowrap>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "PreventivoCommessaVoce", "Nota", null); 
   label.setParent(PreventivoCommessaVoceForm); 
%><label class="<%=label.getClassType()%>" for="Nota"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebTextInput PreventivoCommessaVoceNota =  
     new com.thera.thermfw.web.WebTextArea("PreventivoCommessaVoce", "Nota"); 
  PreventivoCommessaVoceNota.setParent(PreventivoCommessaVoceForm); 
%>
<textarea class="<%=PreventivoCommessaVoceNota.getClassType()%>" cols="70" id="<%=PreventivoCommessaVoceNota.getId()%>" maxlength="<%=PreventivoCommessaVoceNota.getMaxLength()%>" name="<%=PreventivoCommessaVoceNota.getName()%>" rows="2" size="<%=PreventivoCommessaVoceNota.getSize()%>"></textarea><% 
  PreventivoCommessaVoceNota.write(out); 
%>

									</td>
								</tr>
							</table>
						<% MainTabbed.endTab(); %> 
</div>

						<!-- ************************************************************************************************ -->
						<!-- Cartella Riepilogo -->
						 <div class="tabbed_page" id="<%=MainTabbed.getTabPageId("RiepilogoTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("RiepilogoTab"); %>
						<table cellpadding="1" cellspacing="1">
							<tr>
								<td colspan="2" height="100%" width="100%">
									<!--<span class="tabbed" id="tabbedRiepilogo">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed tabbedRiepilogo = new com.thera.thermfw.web.WebTabbed("tabbedRiepilogo", "750", "300"); 
  tabbedRiepilogo.setParent(PreventivoCommessaVoceForm); 
 tabbedRiepilogo.addTab("tabRiepilogoGen", "it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "Tab_Riepilogo_Gen", "PreventivoCommessaVoce", null, null, null, null); 
 tabbedRiepilogo.addTab("tabRiepilogoVal", "it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "Tab_Riepilogo_Val", "PreventivoCommessaVoce", null, null, null, null); 
  tabbedRiepilogo.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
							  			<div class="tabbed_page" id="<%=tabbedRiepilogo.getTabPageId("tabRiepilogoGen")%>" style="width:750;height:300;overflow:auto;"><% tabbedRiepilogo.startTab("tabRiepilogoGen"); %>
											<table cellpadding="1" cellspacing="1">
                        <tr>
                          <td colspan="2"><% 
  WebCheckBox PreventivoCommessaVoceEscRigaOfferta =  
     new com.thera.thermfw.web.WebCheckBox("PreventivoCommessaVoce", "EscRigaOfferta"); 
  PreventivoCommessaVoceEscRigaOfferta.setParent(PreventivoCommessaVoceForm); 
%>
<input id="<%=PreventivoCommessaVoceEscRigaOfferta.getId()%>" name="<%=PreventivoCommessaVoceEscRigaOfferta.getName()%>" type="checkbox" value="Y"><%
  PreventivoCommessaVoceEscRigaOfferta.write(out); 
%>
</td>
                        </tr>
                        <!--<tr>                           <td nowrap="true" width="100"><label for="OffertaClienteRiga"></label></td>                           <td nowrap="true"><span class="multisearchform" id="OffertaClienteRiga"></span></td>                         </tr>-->
                      </table>
                    <% tabbedRiepilogo.endTab(); %> 
</div>
                    <div class="tabbed_page" id="<%=tabbedRiepilogo.getTabPageId("tabRiepilogoVal")%>" style="width:750;height:300;overflow:auto;"><% tabbedRiepilogo.startTab("tabRiepilogoVal"); %>
										<table style="margin: 7 7 7 7;">
												<tr>
                          <td nowrap><label class="thLabel" id="LabelValoreTotale">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "ValoreTotale", null, null, null, null); 
 label.setParent(PreventivoCommessaVoceForm); 
label.write(out); }%> 
</label></td>
                          <td><% 
  WebTextInput PreventivoCommessaVoceValoreTotale =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "ValoreTotale"); 
  PreventivoCommessaVoceValoreTotale.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceValoreTotale.getClassType()%>" id="<%=PreventivoCommessaVoceValoreTotale.getId()%>" maxlength="<%=PreventivoCommessaVoceValoreTotale.getMaxLength()%>" name="<%=PreventivoCommessaVoceValoreTotale.getName()%>" size="15" type="text"><% 
  PreventivoCommessaVoceValoreTotale.write(out); 
%>
</td>
                        </tr>
                       <tr>
                          <td nowrap><label class="thLabel" id="LabelCosTotale">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "CosTotale", null, null, null, null); 
 label.setParent(PreventivoCommessaVoceForm); 
label.write(out); }%> 
</label></td>
                          <td><% 
  WebTextInput PreventivoCommessaVoceCosTotale =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "CosTotale"); 
  PreventivoCommessaVoceCosTotale.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceCosTotale.getClassType()%>" id="<%=PreventivoCommessaVoceCosTotale.getId()%>" maxlength="<%=PreventivoCommessaVoceCosTotale.getMaxLength()%>" name="<%=PreventivoCommessaVoceCosTotale.getName()%>" size="15" type="text"><% 
  PreventivoCommessaVoceCosTotale.write(out); 
%>
</td>
                        </tr>
                        <tr>
                          <td nowrap><label class="thLabel" id="LabelMdcTotale">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "MdcTotale", null, null, null, null); 
 label.setParent(PreventivoCommessaVoceForm); 
label.write(out); }%> 
</label></td>
                          <td><% 
  WebTextInput PreventivoCommessaVoceMdcTotale =  
     new com.thera.thermfw.web.WebTextInput("PreventivoCommessaVoce", "MdcTotale"); 
  PreventivoCommessaVoceMdcTotale.setParent(PreventivoCommessaVoceForm); 
%>
<input class="<%=PreventivoCommessaVoceMdcTotale.getClassType()%>" id="<%=PreventivoCommessaVoceMdcTotale.getId()%>" maxlength="<%=PreventivoCommessaVoceMdcTotale.getMaxLength()%>" name="<%=PreventivoCommessaVoceMdcTotale.getName()%>" size="15" type="text"><% 
  PreventivoCommessaVoceMdcTotale.write(out); 
%>
</td>
                        </tr>
                    </table>
					 					<% tabbedRiepilogo.endTab(); %> 
</div>
                  </div><% tabbedRiepilogo.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>-->
								</td>
							</tr>
						</table>
 			  <% MainTabbed.endTab(); %> 
</div>
        <div class="tabbed_page" id="<%=MainTabbed.getTabPageId("TabDes")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("TabDes"); %>
          <table border="0" style="margin: 7 7 7 7;" width="98%">
            <tr>
              <td valign="top">
                <% 
   request.setAttribute("parentForm", PreventivoCommessaVoceForm); 
   String CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/DescrizioneInLingua.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="GrigliaNLS" name="GrigliaNLS"></span>-->
              </td>
            </tr>
          </table>
        <% MainTabbed.endTab(); %> 
</div>
        <!--<span class="tab" id="TabRigheSec">          <table width="98%" border="0" style="margin: 7 7 7 7;">             <tr>               <td colspan="4" width="100%"><span id="righe" name="righe" class="editgrid"></span></td>             </tr>          </table>         </span>-->
   </div><% MainTabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>-->
 </td>
  </tr>
  <tr>
    <td style="height:0">
      <% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(PreventivoCommessaVoceForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
    </td>
  </tr>
</table>
<%
  PreventivoCommessaVoceForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = PreventivoCommessaVoceForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", PreventivoCommessaVoceBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              PreventivoCommessaVoceForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, PreventivoCommessaVoceBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, PreventivoCommessaVoceBODC.getErrorList().getErrors()); 
           if(PreventivoCommessaVoceBODC.getConflict() != null) 
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
     if(PreventivoCommessaVoceBODC != null && !PreventivoCommessaVoceBODC.close(false)) 
        errors.addAll(0, PreventivoCommessaVoceBODC.getErrorList().getErrors()); 
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
     String errorPage = PreventivoCommessaVoceForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", PreventivoCommessaVoceBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = PreventivoCommessaVoceForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

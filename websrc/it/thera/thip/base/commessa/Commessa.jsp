<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///D:/3rdParty/PantheraSvilL2Panth01/websrc/dtd/xhtml1-transitional.dtd">
<!--
Paolo J Franzoni (24 jan 2003)
aggiunto il checkbox AggiornamentoSaldi
-->
<html>
<!-- WIZGEN Therm 2.0.0 as Form riga indipendente - multiBrowserGen = true -->
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
  BODataCollector CommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebFormForIndipendentRowForm CommessaForm =  
     new com.thera.thermfw.web.WebFormForIndipendentRowForm(request, response, "CommessaForm", "Commessa", null, "it.thera.thip.base.commessa.web.CommessaActionAdapter", false, false, true, true, true, true, "it.thera.thip.base.commessa.web.CommessaDataCollector", 1, true, "it/thera/thip/base/commessa/Commessa.js"); 
  CommessaForm.setServletEnvironment(se); 
  CommessaForm.setJSTypeList(jsList); 
  CommessaForm.setHeader(null); 
  CommessaForm.setFooter(null); 
  CommessaForm.setWebFormModifierClass("it.thera.thip.base.commessa.web.CommessaWebFromModifier"); 
  CommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = CommessaForm.getMode(); 
  String key = CommessaForm.getKey(); 
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
        CommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = CommessaForm.findBODataCollectorName(); 
	     CommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (CommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)CommessaBODC).setServletEnvironment(se); 
        CommessaBODC.initialize("Commessa", true, 1); 
        CommessaForm.setBODataCollector(CommessaBODC); 
        int rcBODC = CommessaForm.initSecurityServices(); 
        mode = CommessaForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           CommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = CommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              CommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(CommessaForm); 
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
  myToolBarTB.setParent(CommessaForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>

<body bottommargin="0" leftmargin="0" onbeforeunload="<%=CommessaForm.getBodyOnBeforeUnload()%>" onload="<%=CommessaForm.getBodyOnLoad()%>" onunload="<%=CommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   CommessaForm.writeBodyStartElements(out); 
%> 


<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = CommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", CommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=CommessaForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  CommessaForm.writeFormStartElements(out); 
%>


<!-- Inizio Fix  03463 A.Boulila -->
<table border="0" cellpadding="2" cellspacing="0" height="100%" id="emptyborder" width="100%">
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
  <tr id="WorkFlowTR" style="display:none">
    <td style="height:0"><% 
  WebWorkflowPanel CommessaWfStatus =  
     new com.thera.thermfw.web.WebWorkflowPanel("Commessa", "WfStatus", true, false, null, null); 
  CommessaWfStatus.setParent(CommessaForm); 
  CommessaWfStatus.write(out); 
%>
<!--<span class="wfpanel" id="WorkFlowf"></span>--></td>
  </tr>
  <tr>
    <td height="100%">
      <!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(CommessaForm); 
 mytabbed.addTab("GeneraleTab", "it.thera.thip.cs.resources.Cs", "DatiGenerali", "Commessa", null, null, null, null); 
 mytabbed.addTab("DatiGestionaliTab", "it.thera.thip.base.commessa.resources.Commessa", "DatiGestionali", "Commessa", null, null, null, null); 
 mytabbed.addTab("CIGCUP", "it.thera.thip.base.commessa.resources.Commessa", "CIGCUP", "Commessa", null, null, null, null); 
 mytabbed.addTab("BudgetTab", "it.thera.thip.base.commessa.resources.Commessa", "Budget", "Commessa", null, null, null, null); 
 mytabbed.addTab("PianoFatturazioneTab", "it.thera.thip.base.commessa.resources.Commessa", "PianoFatturazione", "Commessa", null, null, null, null); 
 mytabbed.addTab("CommentiTab", "it.thera.thip.base.commessa.resources.Commessa", "Commenti", "Commessa", null, null, null, null); 
 mytabbed.addTab("tabExtension", "it.thera.thip.base.articolo.resources.ClasseA", "Estensioni", "Commessa", null, null, null, null); 
 mytabbed.addTab("DescrizioneTab", "it.thera.thip.cs.resources.Cs", "DescrizioniNLS", "Commessa", null, null, null, null); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
        <div class="tabbed_page" id="<%=mytabbed.getTabPageId("GeneraleTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("GeneraleTab"); %>
          <table border="0" style="margin: 0 7 0 7;">
            <tr style="display: none;">
              <td>
                <% 
  WebTextInput CommessaIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "IdAzienda"); 
  CommessaIdAzienda.setParent(CommessaForm); 
%>
<input class="<%=CommessaIdAzienda.getClassType()%>" id="<%=CommessaIdAzienda.getId()%>" maxlength="<%=CommessaIdAzienda.getMaxLength()%>" name="<%=CommessaIdAzienda.getName()%>" size="<%=CommessaIdAzienda.getSize()%>" type="hidden"><% 
  CommessaIdAzienda.write(out); 
%>

              </td>
              <td>
                <% 
  WebTextInput CommessaNodoDescrizione =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "NodoDescrizione"); 
  CommessaNodoDescrizione.setParent(CommessaForm); 
%>
<input class="<%=CommessaNodoDescrizione.getClassType()%>" id="<%=CommessaNodoDescrizione.getId()%>" maxlength="<%=CommessaNodoDescrizione.getMaxLength()%>" name="<%=CommessaNodoDescrizione.getName()%>" size="<%=CommessaNodoDescrizione.getSize()%>" type="hidden"><% 
  CommessaNodoDescrizione.write(out); 
%>

              </td>
			    <td>
                <input id="flag" name="flag" type="hidden">
              </td>
            </tr>
            <tr>
              <td nowrap width="130">
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdCommessa", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="IdCommessa"><%label.write(out);%></label><%}%>
              </td>
              <td>
                <% 
  WebTextInput CommessaIdCommessa =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "IdCommessa"); 
  CommessaIdCommessa.setParent(CommessaForm); 
%>
<input class="<%=CommessaIdCommessa.getClassType()%>" id="<%=CommessaIdCommessa.getId()%>" maxlength="<%=CommessaIdCommessa.getMaxLength()%>" name="<%=CommessaIdCommessa.getName()%>" size="<%=CommessaIdCommessa.getSize()%>"><% 
  CommessaIdCommessa.write(out); 
%>

              </td>
            </tr>
            <% 
   request.setAttribute("parentForm", CommessaForm); 
   String CDForDescrizione$it$thera$thip$cs$Descrizione$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/Descrizione.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$Descrizione$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DescrizioneSubForm" name="DescrizioneSubForm"></span>-->
            <tr id="COMMESSA_CA">
              <td>
                <label for="IdCommessaCA">Commessa CA</label>
              </td>
              <td colspan="2">
                <% 
  WebMultiSearchForm CommessaCommessaCA =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "CommessaCA", false, false, false, 1, "10", "40"); 
  CommessaCommessaCA.setParent(CommessaForm); 
  CommessaCommessaCA.write(out); 
%>
<!--<span class="multisearchform" id="CommessaCA" name="CommessaCA"></span>-->
              </td>
              <td colspan="2">
                <% 
  WebCheckBox CommessaCodificaCommessaCA =  
     new com.thera.thermfw.web.WebCheckBox("Commessa", "CodificaCommessaCA"); 
  CommessaCodificaCommessaCA.setParent(CommessaForm); 
  CommessaCodificaCommessaCA.setOnClick("clickCodificaCommessaCA(this.checked)"); 
%>
<input id="<%=CommessaCodificaCommessaCA.getId()%>" name="<%=CommessaCodificaCommessaCA.getName()%>" type="checkbox" value="Y"><%
  CommessaCodificaCommessaCA.write(out); 
%>

              </td>
            </tr>
            <tr id="TR_CMM_APPARTENENZA" style="display: none;">
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdCommessaAppartenenza", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="CommessaAppartenenza"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaCommessaAppartenenza =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "CommessaAppartenenza", false, false, true, 1, null, null); 
  CommessaCommessaAppartenenza.setParent(CommessaForm); 
  CommessaCommessaAppartenenza.setOnKeyChange("onCommessaAppartenenzaChangeAction()"); 
  CommessaCommessaAppartenenza.write(out); 
%>
<!--<span class="multisearchform" id="CommessaAppartenenza" name="CommessaAppartenenza"></span>-->
              </td>
            </tr>
            <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdTipoCommessa", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="TipoCommessa"><%label.write(out);%></label><%}%>
              </td>
              <!-- Fix 04171 Begin -Jed -->
              <!--<td colspan="3">-->
                <td colspan="2" nowrap>
                  <!-- Fix 04171 End -Jed -->
                  <% 
  WebMultiSearchForm CommessaTipoCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "TipoCommessa", false, false, true, 1, null, null); 
  CommessaTipoCommessa.setParent(CommessaForm); 
  CommessaTipoCommessa.setOnKeyChange("onTipoCommessaChangeAction()"); 
  CommessaTipoCommessa.write(out); 
%>
<!--<span class="multisearchform" id="TipoCommessa" name="TipoCommessa"></span>-->
                </td>
                <td colspan="2" nowrap>
                  <% 
  WebCheckBox CommessaAggiornamentoSaldi =  
     new com.thera.thermfw.web.WebCheckBox("Commessa", "AggiornamentoSaldi"); 
  CommessaAggiornamentoSaldi.setParent(CommessaForm); 
%>
<input id="<%=CommessaAggiornamentoSaldi.getId()%>" name="<%=CommessaAggiornamentoSaldi.getName()%>" type="checkbox" value="Y"><%
  CommessaAggiornamentoSaldi.write(out); 
%>

                </td>
            </tr>
            <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdAmbienteCommessa", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="AmbienteCommessa"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaAmbienteCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "AmbienteCommessa", false, false, true, 1, null, null); 
  CommessaAmbienteCommessa.setParent(CommessaForm); 
  CommessaAmbienteCommessa.setOnKeyChange("onAmbienteCommessaChangeAction()"); 
  CommessaAmbienteCommessa.write(out); 
%>
<!--<span class="multisearchform" id="AmbienteCommessa" name="AmbienteCommessa"></span>-->
              </td>
            </tr>
            <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdStabilimento", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="Stabilimento"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaStabilimento =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "Stabilimento", false, false, true, 1, null, null); 
  CommessaStabilimento.setParent(CommessaForm); 
  CommessaStabilimento.write(out); 
%>
<!--<span class="multisearchform" id="Stabilimento" name="Stabilimento"></span>-->
              </td>
            </tr>
            <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdArticolo", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="Articolo"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaArticolo =  
     new it.thera.thip.base.articolo.web.ArticoloMultiSearchForm("Commessa", "Articolo", false, false, true, 1, null, null); 
  CommessaArticolo.setParent(CommessaForm); 
  CommessaArticolo.setOnKeyChange("onArticoloChangeAction()"); 
  CommessaArticolo.write(out); 
%>
<!--<span class="articolomultisearchform" id="Articolo" name="Articolo"></span>-->
              </td>
            </tr>
            <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdArticoloVersione", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="VersioneArticolo"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaVersioneArticolo =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "VersioneArticolo", false, false, true, 1, null, null); 
  CommessaVersioneArticolo.setParent(CommessaForm); 
  CommessaVersioneArticolo.write(out); 
%>
<!--<span class="multisearchform" id="VersioneArticolo" name="VersioneArticolo"></span>-->
              </td>
            </tr>
            <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdEsternoConfig", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="Configurazione"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaConfigurazione =  
     new it.thera.thip.datiTecnici.configuratore.web.ConfigurazioneMultiSearchForm("Commessa", "Configurazione", false, false, true, 1, null, "50"); 
  CommessaConfigurazione.setExtraRelatedClassAD("IdAzienda,IdConfigurazione"); 
  CommessaConfigurazione.setParent(CommessaForm); 
  CommessaConfigurazione.setEditGridActionAdapter("it.thera.thip.datiTecnici.configuratore.web.ConfigurazioneRicGridActionAdapter"); 
  CommessaConfigurazione.setFixedRestrictConditions("DatiComuniEstesi.Stato,V"); 
  CommessaConfigurazione.setAdditionalRestrictConditions("IdArticolo,IdArticolo"); 
  CommessaConfigurazione.write(out); 
%>
<!--<span class="configurazionemultisearchform" id="Configurazione" name="Configurazione"></span>-->
              </td>
            </tr>
            <tr>
            <!--Fix 13248 inizio-->
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "QtaUmPrmMag", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="QtaUmPrmMag"><%label.write(out);%></label><%}%>
              </td>
              <td>
                <% 
  WebTextInput CommessaQtaUmPrmMag =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "QtaUmPrmMag"); 
  CommessaQtaUmPrmMag.setParent(CommessaForm); 
%>
<input class="<%=CommessaQtaUmPrmMag.getClassType()%>" id="<%=CommessaQtaUmPrmMag.getId()%>" maxlength="<%=CommessaQtaUmPrmMag.getMaxLength()%>" name="<%=CommessaQtaUmPrmMag.getName()%>" size="<%=CommessaQtaUmPrmMag.getSize()%>"><% 
  CommessaQtaUmPrmMag.write(out); 
%>

              </td>
              <td colspan="3">
              <table>
              <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdUmPrmMag", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="UmPrmMag"><%label.write(out);%></label><%}%>
              </td>

              <!-- Fix 04171 Begin -Jed -->
              <!--<td>-->
                <td colspan="2" nowrap>
                  <!-- Fix 04171 End -Jed -->
                  <% 
  WebMultiSearchForm CommessaUmPrmMag =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "UmPrmMag", false, false, true, 1, null, null); 
  CommessaUmPrmMag.setParent(CommessaForm); 
  CommessaUmPrmMag.write(out); 
%>
<!--<span class="multisearchform" id="UmPrmMag" name="UmPrmMag"></span>-->
                </td>
                </tr>
              </table>
              </td>
              <!-- Fix 13248 Fine-->
            </tr>
            <tr>
              <td>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdCliente", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="Cliente" nowrap="true"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaCliente =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "Cliente", false, false, false, 1, null, null); 
  CommessaCliente.setParent(CommessaForm); 
  CommessaCliente.write(out); 
%>
<!--<span class="multisearchform" id="Cliente" name="Cliente"></span>-->
              </td>
            </tr>

            <!--GN Inizio-->
            <tr>
              <td nowrap>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdCommessaModello", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="CommessaModello"><%label.write(out);%></label><%}%>
              </td>
              <td colspan="4">
                <% 
  WebMultiSearchForm CommessaCommessaModello =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "CommessaModello", false, false, true, 1, null, null); 
  CommessaCommessaModello.setParent(CommessaForm); 
  CommessaCommessaModello.write(out); 
%>
<!--<span class="multisearchform" id="CommessaModello" name="CommessaModello"></span>-->
              </td>
            </tr>
            <!--GN fine-->

            <tr>
            <!--Fix 13248 Inizio-->
              <td>
                <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "StatoAvanzamento", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="StatoAvanzamento"><%label.write(out);%></label><%}%>
              </td>
              <td>
                 <% 
  WebComboBox CommessaStatoAvanzamento =  
     new com.thera.thermfw.web.WebComboBox("Commessa", "StatoAvanzamento", null); 
  CommessaStatoAvanzamento.setParent(CommessaForm); 
  CommessaStatoAvanzamento.setOnChange("onStatoAvanzChangeAction()"); 
%>
<select id="<%=CommessaStatoAvanzamento.getId()%>" name="<%=CommessaStatoAvanzamento.getName()%>"><% 
  CommessaStatoAvanzamento.write(out); 
%> 
</select>
              </td>
              <td>
                 <table>
                     <% 
   request.setAttribute("parentForm", CommessaForm); 
   String CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp = "DatiComuniEstesi"; 
%>
<jsp:include page="/it/thera/thip/cs/DatiComuniEstesi.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="Stato" name="Stato"></span>-->
                  </table>
              </td>
              <!--Fix 13248 Fine-->
             </tr>
          </table>
       <% mytabbed.endTab(); %> 
</div>
       <div class="tabbed_page" id="<%=mytabbed.getTabPageId("DatiGestionaliTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("DatiGestionaliTab"); %>
         <table border="0" style="margin: 0 7 0 7;">
           <!--Fix 04171 Begin -Jed -->
           <!--<tr>-->
           <tr style="display: none;">
             <!--Fix 04171 Begin -Jed -->
             <td nowrap width="140">
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdCommessaPrincipale", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="CommessaPrincipale" nowrap="true"><%label.write(out);%></label><%}%>
             </td>
             <td colspan="3">
               <% 
  WebMultiSearchForm CommessaCommessaPrincipale =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "CommessaPrincipale", false, false, true, 1, null, null); 
  CommessaCommessaPrincipale.setParent(CommessaForm); 
  CommessaCommessaPrincipale.write(out); 
%>
<!--<span class="multisearchform" id="CommessaPrincipale" name="CommessaPrincipale"></span>-->
             </td>
           </tr>
           <tr>
             <td>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdResponsabileCommessa", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="ResponsabileCommessa" nowrap="true"><%label.write(out);%></label><%}%>
             </td>
             <td colspan="3" nowrap>
               <% 
  WebMultiSearchForm CommessaResponsabileCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "ResponsabileCommessa", false, false, true, 1, null, null); 
  CommessaResponsabileCommessa.setParent(CommessaForm); 
  CommessaResponsabileCommessa.write(out); 
%>
<!--<span class="multisearchform" id="ResponsabileCommessa" name="ResponsabileCommessa"></span>-->
             </td>
           </tr>
           <tr>
             <td>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdResponsabilePreventivaz", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="ResponsabilePreventivaz" nowrap="true"><%label.write(out);%></label><%}%>
             </td>
             <td colspan="3" nowrap>
               <% 
  WebMultiSearchForm CommessaResponsabilePreventivaz =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "ResponsabilePreventivaz", false, false, true, 1, null, null); 
  CommessaResponsabilePreventivaz.setParent(CommessaForm); 
  CommessaResponsabilePreventivaz.write(out); 
%>
<!--<span class="multisearchform" id="ResponsabilePreventivaz" name="ResponsabilePreventivaz"></span>-->
             </td>
           </tr>
           <tr id="VisOrdEff">
             <td colspan="4">
               <FIELDSET name="OrdineVendita" width="100%">
                 <LEGEND align="top">
                   <label class="thLabel" id="OrdineVendita">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.commessa.resources.Commessa", "OrdineVendita", null, null, null, null); 
 label.setParent(CommessaForm); 
label.write(out); }%> 
</label>
                 </LEGEND>
                 <table border="0" cellpadding="0" cellspacing="0" style="margin: 0 0 0 7;">
                   <tr>
                     <td width="135">
                       <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdNumeroOrdine", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="Ordine" nowrap="true"><%label.write(out);%></label><%}%>
                     </td>
                     <td>
                       <% 
  WebMultiSearchForm CommessaOrdine =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "Ordine", false, false, true, 2, null, null); 
  CommessaOrdine.setParent(CommessaForm); 
  CommessaOrdine.setOnKeyChange("onOrdineChangeAction()"); 
  CommessaOrdine.setFixedRestrictConditions("StatoAvanzamento,2;DatiComuniEstesi.Stato,V"); 
  CommessaOrdine.write(out); 
%>
<!--<span class="multisearchform" id="Ordine" name="Ordine"></span>-->
                     </td>
					 <!-- 15938 begin -->
					 <td id="GenOrdVenTR" style="width:20px">
					 	<!-- 31460 inizio -->
						<button class="thShowBut" id="GenOrdVenBUT" name="GenOrdVenBUT" onclick="creaOrdineVendita()" style="width:16px;height:16px;align:top" title="<%= ResourceLoader.getString("it.thera.thip.base.commessa.resources.Commessa", "GenOrdVenButton")%>" type="button"><img border="0" height="16px" src="it/thera/thip/base/commessa/images/OrdVenOk.gif" width="16px">		   					
						<%= ResourceLoader.getString("it.thera.thip.base.commessa.resources.Commessa", "GenOrdVenButtonID")%></button>
						<!-- 31460 fine -->
					 </td>
					  <!-- 15938 end -->
                   </tr>
                   <tr>
                     <td>
                       <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdRigaOrdine", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="RigaOrdine" nowrap="true"><%label.write(out);%></label><%}%>
                     </td>
                     <td>
                       <% 
  WebMultiSearchForm CommessaRigaOrdine =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "RigaOrdine", false, false, true, 1, null, null); 
  CommessaRigaOrdine.setParent(CommessaForm); 
  CommessaRigaOrdine.write(out); 
%>
<!--<span class="multisearchform" id="RigaOrdine" name="RigaOrdine"></span>-->
                     </td>
                   </tr>
                 </table>
               </FIELDSET>
             </td>
           </tr>
           <tr id="VisOrdRio" style="display:none">
             <td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "RifOrdineRiorg", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="RifOrdineRiorg"><%label.write(out);%></label><%}%></td>
             <td><% 
  WebTextInput CommessaRifOrdineRiorg =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "RifOrdineRiorg"); 
  CommessaRifOrdineRiorg.setParent(CommessaForm); 
%>
<input class="<%=CommessaRifOrdineRiorg.getClassType()%>" id="<%=CommessaRifOrdineRiorg.getId()%>" maxlength="<%=CommessaRifOrdineRiorg.getMaxLength()%>" name="<%=CommessaRifOrdineRiorg.getName()%>" size="<%=CommessaRifOrdineRiorg.getSize()%>"><% 
  CommessaRifOrdineRiorg.write(out); 
%>
</td>
           </tr>
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataApertura", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataApertura"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataApertura =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataApertura"); 
  CommessaDataApertura.setShowCalendarBtn(true); 
  CommessaDataApertura.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataApertura.getClassType()%>" id="<%=CommessaDataApertura.getId()%>" maxlength="<%=CommessaDataApertura.getMaxLength()%>" name="<%=CommessaDataApertura.getName()%>" size="<%=CommessaDataApertura.getSize()%>"><% 
  CommessaDataApertura.write(out); 
%>

             </td>
           </tr>
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataConferma", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataConferma"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataConferma =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataConferma"); 
  CommessaDataConferma.setShowCalendarBtn(true); 
  CommessaDataConferma.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataConferma.getClassType()%>" id="<%=CommessaDataConferma.getId()%>" maxlength="<%=CommessaDataConferma.getMaxLength()%>" name="<%=CommessaDataConferma.getName()%>" size="<%=CommessaDataConferma.getSize()%>"><% 
  CommessaDataConferma.write(out); 
%>

             </td>
           </tr>
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataInizioPrevista", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataInizioPrevista"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataInizioPrevista =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataInizioPrevista"); 
  CommessaDataInizioPrevista.setShowCalendarBtn(true); 
  CommessaDataInizioPrevista.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataInizioPrevista.getClassType()%>" id="<%=CommessaDataInizioPrevista.getId()%>" maxlength="<%=CommessaDataInizioPrevista.getMaxLength()%>" name="<%=CommessaDataInizioPrevista.getName()%>" size="<%=CommessaDataInizioPrevista.getSize()%>"><% 
  CommessaDataInizioPrevista.write(out); 
%>

             </td>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataFinePrevista", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataFinePrevista"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataFinePrevista =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataFinePrevista"); 
  CommessaDataFinePrevista.setShowCalendarBtn(true); 
  CommessaDataFinePrevista.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataFinePrevista.getClassType()%>" id="<%=CommessaDataFinePrevista.getId()%>" maxlength="<%=CommessaDataFinePrevista.getMaxLength()%>" name="<%=CommessaDataFinePrevista.getName()%>" size="<%=CommessaDataFinePrevista.getSize()%>"><% 
  CommessaDataFinePrevista.write(out); 
%>

             </td>
           </tr>
           <!--span class="subform" name="DatePreviste" id="DatePreviste"></span-->
           <!--span class="subform" name="Validita" id="Validita"></span-->
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataPrimaAttivita", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataPrimaAttivita"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataPrimaAttivita =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataPrimaAttivita"); 
  CommessaDataPrimaAttivita.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataPrimaAttivita.getClassType()%>" id="<%=CommessaDataPrimaAttivita.getId()%>" maxlength="<%=CommessaDataPrimaAttivita.getMaxLength()%>" name="<%=CommessaDataPrimaAttivita.getName()%>" size="<%=CommessaDataPrimaAttivita.getSize()%>"><% 
  CommessaDataPrimaAttivita.write(out); 
%>

             </td>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataUltimaAttivita", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataUltimaAttivita"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataUltimaAttivita =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataUltimaAttivita"); 
  CommessaDataUltimaAttivita.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataUltimaAttivita.getClassType()%>" id="<%=CommessaDataUltimaAttivita.getId()%>" maxlength="<%=CommessaDataUltimaAttivita.getMaxLength()%>" name="<%=CommessaDataUltimaAttivita.getName()%>" size="<%=CommessaDataUltimaAttivita.getSize()%>"><% 
  CommessaDataUltimaAttivita.write(out); 
%>

             </td>
           </tr>
           <!-- 33950 inizio -->
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataEstrazioneStorici", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataEstrazioneStorici"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataEstrazioneStorici =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataEstrazioneStorici"); 
  CommessaDataEstrazioneStorici.setShowCalendarBtn(true); 
  CommessaDataEstrazioneStorici.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataEstrazioneStorici.getClassType()%>" id="<%=CommessaDataEstrazioneStorici.getId()%>" maxlength="<%=CommessaDataEstrazioneStorici.getMaxLength()%>" name="<%=CommessaDataEstrazioneStorici.getName()%>" size="<%=CommessaDataEstrazioneStorici.getSize()%>"><% 
  CommessaDataEstrazioneStorici.write(out); 
%>

             </td>
           </tr>
           <!-- 33950 fine -->
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataChiusuraTecnica", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataChiusuraTecnica"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataChiusuraTecnica =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataChiusuraTecnica"); 
  CommessaDataChiusuraTecnica.setShowCalendarBtn(true); 
  CommessaDataChiusuraTecnica.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataChiusuraTecnica.getClassType()%>" id="<%=CommessaDataChiusuraTecnica.getId()%>" maxlength="<%=CommessaDataChiusuraTecnica.getMaxLength()%>" name="<%=CommessaDataChiusuraTecnica.getName()%>" size="<%=CommessaDataChiusuraTecnica.getSize()%>"><% 
  CommessaDataChiusuraTecnica.write(out); 
%>

             </td>
           </tr>
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataChiusuraOperativa", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataChiusuraOperativa"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataChiusuraOperativa =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataChiusuraOperativa"); 
  CommessaDataChiusuraOperativa.setShowCalendarBtn(true); 
  CommessaDataChiusuraOperativa.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataChiusuraOperativa.getClassType()%>" id="<%=CommessaDataChiusuraOperativa.getId()%>" maxlength="<%=CommessaDataChiusuraOperativa.getMaxLength()%>" name="<%=CommessaDataChiusuraOperativa.getName()%>" size="<%=CommessaDataChiusuraOperativa.getSize()%>"><% 
  CommessaDataChiusuraOperativa.write(out); 
%>

             </td>
           </tr>
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "DataChiusura", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataChiusura"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaDataChiusura =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "DataChiusura"); 
  CommessaDataChiusura.setShowCalendarBtn(true); 
  CommessaDataChiusura.setParent(CommessaForm); 
%>
<input class="<%=CommessaDataChiusura.getClassType()%>" id="<%=CommessaDataChiusura.getId()%>" maxlength="<%=CommessaDataChiusura.getMaxLength()%>" name="<%=CommessaDataChiusura.getName()%>" size="<%=CommessaDataChiusura.getSize()%>"><% 
  CommessaDataChiusura.write(out); 
%>

             </td>
           </tr>
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "PianoFatturazione", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="PianoFatturazione"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebComboBox CommessaPianoFatturazione =  
     new com.thera.thermfw.web.WebComboBox("Commessa", "PianoFatturazione", null); 
  CommessaPianoFatturazione.setParent(CommessaForm); 
  CommessaPianoFatturazione.setOnChange("onPianoFattChangeAction()"); 
%>
<select id="<%=CommessaPianoFatturazione.getId()%>" name="<%=CommessaPianoFatturazione.getName()%>"><% 
  CommessaPianoFatturazione.write(out); 
%> 
</select>
             </td>
             <!--Fix 04171 Begin -Jed-->
             <!--</tr>               <tr>                <td></td>  -->
               <!--Fix 04171 End -Jed-->
               <td colspan="2">
                 <% 
  WebCheckBox CommessaChiudiOrdUltimaFat =  
     new com.thera.thermfw.web.WebCheckBox("Commessa", "ChiudiOrdUltimaFat"); 
  CommessaChiudiOrdUltimaFat.setParent(CommessaForm); 
%>
<input id="<%=CommessaChiudiOrdUltimaFat.getId()%>" name="<%=CommessaChiudiOrdUltimaFat.getName()%>" type="checkbox" value="Y"><%
  CommessaChiudiOrdUltimaFat.write(out); 
%>

               </td>
             </tr>
         </table>
         <% mytabbed.endTab(); %> 
</div>

         <!--Fix 19897 inizio-->
         <div class="tabbed_page" id="<%=mytabbed.getTabPageId("CIGCUP")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("CIGCUP"); %>
          <table border="0" style="margin: 7 7 7 7;" width="90%">
          <!--Fix 20785 inizio-->
          <tr>
              <td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "CodiceCUP", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="CodiceCUP"><%label.write(out);%></label><%}%></td>
              <td><% 
  WebTextInput CommessaCodiceCUP =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "CodiceCUP"); 
  CommessaCodiceCUP.setParent(CommessaForm); 
%>
<input class="<%=CommessaCodiceCUP.getClassType()%>" id="<%=CommessaCodiceCUP.getId()%>" maxlength="<%=CommessaCodiceCUP.getMaxLength()%>" name="<%=CommessaCodiceCUP.getName()%>" size="<%=CommessaCodiceCUP.getSize()%>"><% 
  CommessaCodiceCUP.write(out); 
%>
</td>
              <td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "CodiceCIG", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="CodiceCIG"><%label.write(out);%></label><%}%></td>
              <td><% 
  WebTextInput CommessaCodiceCIG =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "CodiceCIG"); 
  CommessaCodiceCIG.setParent(CommessaForm); 
%>
<input class="<%=CommessaCodiceCIG.getClassType()%>" id="<%=CommessaCodiceCIG.getId()%>" maxlength="<%=CommessaCodiceCIG.getMaxLength()%>" name="<%=CommessaCodiceCIG.getName()%>" size="<%=CommessaCodiceCIG.getSize()%>"><% 
  CommessaCodiceCIG.write(out); 
%>
</td>
            </tr>
            <!--tr>               <td colspan="4">                   <FIELDSET name="field1"><LEGEND align = "top"><label id="DatiOrdAcq" name="DatiOrdAcq"></label></LEGEND>                    <table>                     <tr>                        <td nowrap="true"><label for="NumeroDocOrdAcq"></label></td>                        <td><input id="NumeroDocOrdAcq" name="NumeroDocOrdAcq"/></td>                        <td nowrap="true"><label for="DataDocOrdAcq"></label></td>                        <td><input id="DataDocOrdAcq" name="DataDocOrdAcq"/></td>                        <td nowrap="true"><label for="IdCommConvOrdAcq"></label></td>                        <td><input id="IdCommConvOrdAcq" name="IdCommConvOrdAcq"/></td>                      </tr>                    </table>                  </FIELDSET>                  <FIELDSET name="field2"><LEGEND align = "top"><label id="DatiCont" name="DatiCont"></label></LEGEND>                    <table>                     <tr>                        <td nowrap="true"><label for="NumeroDocContratto"></label></td>                        <td><input id="NumeroDocContratto" name="NumeroDocContratto"/></td>                        <td nowrap="true"><label for="DataDocContratto"></label></td>                        <td><input id="DataDocContratto" name="DataDocContratto"/></td>                        <td nowrap="true"><label for="IdCommConvContratto"></label></td>                        <td><input id="IdCommConvContratto" name="IdCommConvContratto"/></td>                      </tr>                    </table>                  </FIELDSET>                  <FIELDSET name="field3"><LEGEND align = "top"><label id="DatiConv" name="DatiConv"></label></LEGEND>                    <table>                     <tr>                        <td nowrap="true"><label for="NumeroDocumento"></label></td>                        <td><input id="NumeroDocumento" name="NumeroDocumento"/></td>                        <td nowrap="true"><label for="DataDocumento"></label></td>                        <td><input id="DataDocumento" name="DataDocumento"/></td>                        <td nowrap="true"><label for="IdCommConven"></label></td>                        <td><input id="IdCommConven" name="IdCommConven"/></td>                      </tr>                    </table>                  </FIELDSET>                  <FIELDSET name="field4"><LEGEND align = "top"><label id="DatiRicez" name="DatiRicez"></label></LEGEND>                    <table>                     <tr>                        <td nowrap="true"><label for="NumeroDocRicezione"></label></td>                        <td><input id="NumeroDocRicezione" name="NumeroDocRicezione"/></td>                        <td nowrap="true"><label for="DataDocRicezione"></label></td>                        <td><input id="DataDocRicezione" name="DataDocRicezione"/></td>                        <td nowrap="true"><label for="IdCommConvRicezione"></label></td>                        <td><input id="IdCommConvRicezione" name="IdCommConvRicezione"/></td>                      </tr>                    </table>                  </FIELDSET>                  <FIELDSET name="field5"><LEGEND align = "top"><label id="DatiFattColl" name="DatiFattColl"></label></LEGEND>                    <table>                     <tr>                        <td nowrap="true"><label for="NumeroDocFatColl"></label></td>                        <td><input id="NumeroDocFatColl" name="NumeroDocFatColl"/></td>                        <td nowrap="true"><label for="DataDocFatColl"></label></td>                        <td><input id="DataDocFatColl" name="DataDocFatColl"/></td>                        <td nowrap="true"><label for="IdCommConvFatColl"></label></td>                        <td><input id="IdCommConvFatColl" name="IdCommConvFatColl"/></td>                      </tr>                    </table>                  </FIELDSET>               </td>             </tr-->
			<!--31437 inizio-->
			<tr>
				<td colspan="4">
					<!--<span class="editgrid" id="DocumentiCollegate">--><% 
  WebEditGrid CommessaDocumentiCollegate =  
     new com.thera.thermfw.web.WebEditGrid("Commessa", "DocumentiCollegate", 8, new String[]{"TipoDocumento", "DataDocumento", "CommConvenzione", "NumeroDocumento"}, 1, null, null,false,"com.thera.thermfw.web.servlet.GridActionAdapterForIndependentRow"); 
 CommessaDocumentiCollegate.setParent(CommessaForm); 
 CommessaDocumentiCollegate.setNoControlRowKeys(true); 
 CommessaDocumentiCollegate.write(out); 
%>
<BR><% 
   request.setAttribute("parentForm", CommessaForm); 
   String CDForDocumentiCollegate = "DocumentiCollegate"; 
%>
<jsp:include page="/it/thera/thip/base/commessa/CommessaDocCollegate.jsp" flush="true"> 
<jsp:param name="EditGridCDName" value="<%=CDForDocumentiCollegate%>"/> 
<jsp:param name="Mode" value="NEW"/> 
</jsp:include> 
<!--</span>-->
				</td>
			</tr>
			<!--31437 fine-->
            <!--Fix 20785 fine-->
            <!--<tr>               <td nowrap="true"><label for="TipoGestioneCigCup">Piano fatturazione</label></td>               <td><select name="TipoGestioneCigCup" id="TipoGestioneCigCup"><option></option></select></td>             </tr>             <tr>               <td nowrap="true"><label for="NumeroDocumento"></label></td>               <td><input id="NumeroDocumento" name="NumeroDocumento"/></td>               <td nowrap="true"><label for="DataDocumento"></label></td>               <td><input id="DataDocumento" name="DataDocumento"/></td>             </tr>             <tr>               <td nowrap="true"><label for="NumeroItem"></label></td>               <td><input id="NumeroItem" name="NumeroItem"/></td>             </tr>             <tr>               <td nowrap="true"><label for="IdCommConven"></label></td>               <td><input id="IdCommConven" name="IdCommConven"/></td>             </tr>             <tr>               <td nowrap="true"><label for="CodiceCUP"></label></td>               <td><input id="CodiceCUP" name="CodiceCUP"/></td>               <td nowrap="true"><label for="CodiceCIG"></label></td>               <td><input id="CodiceCIG" name="CodiceCIG"/></td>             </tr>Fix 20785-->
           </table>
         <% mytabbed.endTab(); %> 
</div>
         <!--Fix 19897 fine-->

		 <!-- 35382 inizio -->
		 <div class="tabbed_page" id="<%=mytabbed.getTabPageId("BudgetTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("BudgetTab"); %>
		 	<table border="0" style="margin: 0 0 0 0;width:calc(100% - 1px); height:calc(100% - 4px); " >
            	<tr>
					<td>
			 			<iframe height="100%" id="BudgetCommessaFrame" name="BudgetCommessaFrame" src width="100%" style="border:0px solid white;"></iframe>
			 		</td>
			 	</tr>
			 	<tr id="TR_CREA_BUDGET" style="display:none" valign="top">
			 		<td>
			 			<button id="CreaBudgetButton" name="CreaBudgetButton" onclick="apriCreaBudget()" style="width:140" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "CreaBudgetButton")%>" type="button"><%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "CreaBudgetButton")%></button> 		
			 		</td>
			 	</tr>
			 </table>
		 <% mytabbed.endTab(); %> 
</div>
		 <!-- 35382 fine -->
         <div class="tabbed_page" id="<%=mytabbed.getTabPageId("PianoFatturazioneTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("PianoFatturazioneTab"); %>
         <table border="0" style="margin: 7 7 7 7;">
           <tr>
				<!-- Fix 29025 inizio -->
				<td>
					<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "TipoPiano", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="TipoPiano"><%label.write(out);%></label><%}%>
				</td>
				<td>
					<% 
  WebComboBox CommessaTipoPiano =  
     new com.thera.thermfw.web.WebComboBox("Commessa", "TipoPiano", null); 
  CommessaTipoPiano.setParent(CommessaForm); 
%>
<select id="<%=CommessaTipoPiano.getId()%>" name="<%=CommessaTipoPiano.getName()%>"><% 
  CommessaTipoPiano.write(out); 
%> 
</select>
				</td>
				<!-- Fix 29025 fine -->
             <td id="VisValEffLabel" nowrap style="display:block">
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "ValoreTotaleOrdine", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="ValoreTotaleOrdine"><%label.write(out);%></label><%}%>
             </td>
             <td id="VisValEff" style="display:block">
               <% 
  WebTextInput CommessaValoreTotaleOrdine =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "ValoreTotaleOrdine"); 
  CommessaValoreTotaleOrdine.setParent(CommessaForm); 
%>
<input class="<%=CommessaValoreTotaleOrdine.getClassType()%>" id="<%=CommessaValoreTotaleOrdine.getId()%>" maxlength="<%=CommessaValoreTotaleOrdine.getMaxLength()%>" name="<%=CommessaValoreTotaleOrdine.getName()%>" size="<%=CommessaValoreTotaleOrdine.getSize()%>"><% 
  CommessaValoreTotaleOrdine.write(out); 
%>

             </td>
             <td id="VisValRioLabel" nowrap style="display:none">
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "ValoreOrdRiorg", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="ValoreOrdRiorg"><%label.write(out);%></label><%}%>
             </td>
             <td id="VisValRio" style="display:none">
               <% 
  WebTextInput CommessaValoreOrdRiorg =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "ValoreOrdRiorg"); 
  CommessaValoreOrdRiorg.setParent(CommessaForm); 
%>
<input class="<%=CommessaValoreOrdRiorg.getClassType()%>" id="<%=CommessaValoreOrdRiorg.getId()%>" maxlength="<%=CommessaValoreOrdRiorg.getMaxLength()%>" name="<%=CommessaValoreOrdRiorg.getName()%>" size="<%=CommessaValoreOrdRiorg.getSize()%>"><% 
  CommessaValoreOrdRiorg.write(out); 
%>

             </td>
       	   <!-- Fix 29025 inizio -->
           </tr>
           <tr>
				<td></td>
				<td>
					<% 
  WebCheckBox CommessaUtilizzaContoAnticipi =  
     new com.thera.thermfw.web.WebCheckBox("Commessa", "UtilizzaContoAnticipi"); 
  CommessaUtilizzaContoAnticipi.setParent(CommessaForm); 
%>
<input id="<%=CommessaUtilizzaContoAnticipi.getId()%>" name="<%=CommessaUtilizzaContoAnticipi.getName()%>" type="checkbox" value="Y"><%
  CommessaUtilizzaContoAnticipi.write(out); 
%>

				</td>
       	   <!-- Fix 29025 fine -->

             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "ValoreTotaleRate", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="ValoreTotaleRate"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebTextInput CommessaValoreTotaleRate =  
     new com.thera.thermfw.web.WebTextInput("Commessa", "ValoreTotaleRate"); 
  CommessaValoreTotaleRate.setParent(CommessaForm); 
%>
<input class="<%=CommessaValoreTotaleRate.getClassType()%>" id="<%=CommessaValoreTotaleRate.getId()%>" maxlength="<%=CommessaValoreTotaleRate.getMaxLength()%>" name="<%=CommessaValoreTotaleRate.getName()%>" size="<%=CommessaValoreTotaleRate.getSize()%>"><% 
  CommessaValoreTotaleRate.write(out); 
%>

             </td>
           </tr>
           <tr>
             <td colspan="5">
               <!--<span class="editgrid" id="RateCommesse" name="RateCommesse">--><% 
  WebEditGrid CommessaRateCommesse =  
     new com.thera.thermfw.web.WebEditGrid("Commessa", "RateCommesse", 8, new String[]{"NumeroRata", "TipoRata", "NumGiorniDaEventi", "IdArticolo", "QuantitaPrmMag", "ImportoRata", "PercSuOrdine", "NumeroDocumentoFormattato", "DataFatturazione", "CollegamentoOrdine", "AnnoNumeroFattura", "DataFattura", "DatiComuniEstesi.Stato"}, 3, null, null,false,"com.thera.thermfw.web.servlet.GridActionAdapterForIndependentRow"); 
 CommessaRateCommesse.setParent(CommessaForm); 
 CommessaRateCommesse.setNoControlRowKeys(false); 
 CommessaRateCommesse.write(out); 
%>
<!--</span>-->
             </td>
           </tr>
         </table>
         <% mytabbed.endTab(); %> 
</div>
         <div class="tabbed_page" id="<%=mytabbed.getTabPageId("CommentiTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("CommentiTab"); %>
         <table border="0" style="margin: 7 7 7 7;" width="97%">
           <% 
   request.setAttribute("parentForm", CommessaForm); 
   String CDForCommenti$com$thera$thermfw$cbs$CommentHandler$jsp = "Commenti"; 
%>
<jsp:include page="/com/thera/thermfw/cbs/CommentHandler.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForCommenti$com$thera$thermfw$cbs$CommentHandler$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="Commenti" name="Commenti"></span>-->
         </table>
         <table border="0" style="margin: 7 7 7 7;">
           <tr>
             <td nowrap>
               <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "IdDocumentoMM", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="DocumentoMM"><%label.write(out);%></label><%}%>
             </td>
             <td>
               <% 
  WebMultiSearchForm CommessaDocumentoMM =  
     new com.thera.thermfw.web.WebMultiSearchForm("Commessa", "DocumentoMM", false, false, true, 1, null, null); 
  CommessaDocumentoMM.setParent(CommessaForm); 
  CommessaDocumentoMM.write(out); 
%>
<!--<span class="multisearchform" id="DocumentoMM" name="DocumentoMM"></span>-->
             </td>
           </tr>
           <td nowrap>
             <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "Commessa", "Note", null); 
   label.setParent(CommessaForm); 
%><label class="<%=label.getClassType()%>" for="Note"><%label.write(out);%></label><%}%>
           </td>
           <td>
             <% 
  WebTextInput CommessaNote =  
     new com.thera.thermfw.web.WebTextArea("Commessa", "Note"); 
  CommessaNote.setParent(CommessaForm); 
%>
<textarea class="<%=CommessaNote.getClassType()%>" cols="80" id="<%=CommessaNote.getId()%>" maxlength="<%=CommessaNote.getMaxLength()%>" name="<%=CommessaNote.getName()%>" rows="2" size="<%=CommessaNote.getSize()%>"></textarea><% 
  CommessaNote.write(out); 
%>

           </td>
         </table>
         <% mytabbed.endTab(); %> 
</div>
         <div class="tabbed_page" id="<%=mytabbed.getTabPageId("tabExtension")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tabExtension"); %>
           <% 
  WebExtension CommessaAttributiEstendibili =  
     new com.thera.thermfw.web.WebExtension("Commessa", "AttributiEstendibili", null); 
  CommessaAttributiEstendibili.setParent(CommessaForm); 
  CommessaAttributiEstendibili.write(out); 
%>
<!--<span class="extension" id="ext"></span>-->
         <% mytabbed.endTab(); %> 
</div>
         <div class="tabbed_page" id="<%=mytabbed.getTabPageId("DescrizioneTab")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("DescrizioneTab"); %>
         <table border="0" style="margin: 7 7 7 7;" width="98%">
           <tr>
             <td valign="top">
               <% 
   request.setAttribute("parentForm", CommessaForm); 
   String CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/DescrizioneInLingua.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DescrizioneGriglia" name="DescrizioneGriglia"></span>-->
             </td>
           </tr>
         </table>
         <% mytabbed.endTab(); %> 
</div>
      </div><% mytabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>--><!-- Fine Fix 03463 A.Boulila -->
    </td>
  </tr>
  <tr>
    <td style="height:0">
      <% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(CommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
    </td>
  </tr>
  <!--Fix 04171 Begin -Jed -->
  <tr><td style="height:0"><iframe frameborder="0" height="0" id="displayTabPianif" name="displayTabPianif" style="visibility:hidden;" width="0"></iframe></td></tr>
  <!--Fix 04171 End -Jed -->
</table>

<%
  CommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = CommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", CommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              CommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, CommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, CommessaBODC.getErrorList().getErrors()); 
           if(CommessaBODC.getConflict() != null) 
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
     if(CommessaBODC != null && !CommessaBODC.close(false)) 
        errors.addAll(0, CommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = CommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", CommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = CommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

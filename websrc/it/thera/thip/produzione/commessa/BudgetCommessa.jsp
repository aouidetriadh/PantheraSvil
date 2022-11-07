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
  BODataCollector BudgetCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm BudgetCommessaForm =  
     new com.thera.thermfw.web.WebForm(request, response, "BudgetCommessaForm", "BudgetCommessa", null, "it.thera.thip.produzione.commessa.web.BudgetCommessaFormActionAdapter", false, false, false, true, true, true, null, 1, true, "it/thera/thip/produzione/commessa/BudgetCommessa.js"); 
  BudgetCommessaForm.setServletEnvironment(se); 
  BudgetCommessaForm.setJSTypeList(jsList); 
  BudgetCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  BudgetCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  BudgetCommessaForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.BudgetCommessaWebFormModifier"); 
  BudgetCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = BudgetCommessaForm.getMode(); 
  String key = BudgetCommessaForm.getKey(); 
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
        BudgetCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = BudgetCommessaForm.findBODataCollectorName(); 
                BudgetCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (BudgetCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)BudgetCommessaBODC).setServletEnvironment(se); 
        BudgetCommessaBODC.initialize("BudgetCommessa", true, 1); 
        BudgetCommessaForm.setBODataCollector(BudgetCommessaBODC); 
        int rcBODC = BudgetCommessaForm.initSecurityServices(); 
        mode = BudgetCommessaForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           BudgetCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = BudgetCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              BudgetCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Budget commessa</title>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(BudgetCommessaForm); 
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
  myToolBarTB.setParent(BudgetCommessaForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=BudgetCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=BudgetCommessaForm.getBodyOnLoad()%>" onunload="<%=BudgetCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   BudgetCommessaForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = BudgetCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", BudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=BudgetCommessaForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  BudgetCommessaForm.writeFormStartElements(out); 
%>

		<table border="0" cellpadding="0" cellspacing="0" height="100%" width="100%">
			<tr><td style="height:0" valign="top"><% menuBar.writeElements(out); %> 
</td></tr>
			<tr><td style="height:30px" valign="top"><% myToolBarTB.writeChildren(out); %> 
</td></tr>
			<tr>
				<td style="height:0" valign="top">
					<% 
  WebTextInput BudgetCommessaIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdAzienda"); 
  BudgetCommessaIdAzienda.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdAzienda.getClassType()%>" id="<%=BudgetCommessaIdAzienda.getId()%>" maxlength="<%=BudgetCommessaIdAzienda.getMaxLength()%>" name="<%=BudgetCommessaIdAzienda.getName()%>" size="<%=BudgetCommessaIdAzienda.getSize()%>" type="hidden"><% 
  BudgetCommessaIdAzienda.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdBudget =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdBudget"); 
  BudgetCommessaIdBudget.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdBudget.getClassType()%>" id="<%=BudgetCommessaIdBudget.getId()%>" maxlength="<%=BudgetCommessaIdBudget.getMaxLength()%>" name="<%=BudgetCommessaIdBudget.getName()%>" size="<%=BudgetCommessaIdBudget.getSize()%>" type="hidden"><% 
  BudgetCommessaIdBudget.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdArticolo =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdArticolo"); 
  BudgetCommessaIdArticolo.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdArticolo.getClassType()%>" id="<%=BudgetCommessaIdArticolo.getId()%>" maxlength="<%=BudgetCommessaIdArticolo.getMaxLength()%>" name="<%=BudgetCommessaIdArticolo.getName()%>" size="<%=BudgetCommessaIdArticolo.getSize()%>" type="hidden"><% 
  BudgetCommessaIdArticolo.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdVersione =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdVersione"); 
  BudgetCommessaIdVersione.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdVersione.getClassType()%>" id="<%=BudgetCommessaIdVersione.getId()%>" maxlength="<%=BudgetCommessaIdVersione.getMaxLength()%>" name="<%=BudgetCommessaIdVersione.getName()%>" size="<%=BudgetCommessaIdVersione.getSize()%>" type="hidden"><% 
  BudgetCommessaIdVersione.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdEsternoConfig =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdEsternoConfig"); 
  BudgetCommessaIdEsternoConfig.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdEsternoConfig.getClassType()%>" id="<%=BudgetCommessaIdEsternoConfig.getId()%>" maxlength="<%=BudgetCommessaIdEsternoConfig.getMaxLength()%>" name="<%=BudgetCommessaIdEsternoConfig.getName()%>" size="<%=BudgetCommessaIdEsternoConfig.getSize()%>" type="hidden"><% 
  BudgetCommessaIdEsternoConfig.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdStabilimento =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdStabilimento"); 
  BudgetCommessaIdStabilimento.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdStabilimento.getClassType()%>" id="<%=BudgetCommessaIdStabilimento.getId()%>" maxlength="<%=BudgetCommessaIdStabilimento.getMaxLength()%>" name="<%=BudgetCommessaIdStabilimento.getName()%>" size="<%=BudgetCommessaIdStabilimento.getSize()%>" type="hidden"><% 
  BudgetCommessaIdStabilimento.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdCommessaAppart =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdCommessaAppart"); 
  BudgetCommessaIdCommessaAppart.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdCommessaAppart.getClassType()%>" id="<%=BudgetCommessaIdCommessaAppart.getId()%>" maxlength="<%=BudgetCommessaIdCommessaAppart.getMaxLength()%>" name="<%=BudgetCommessaIdCommessaAppart.getName()%>" size="<%=BudgetCommessaIdCommessaAppart.getSize()%>" type="hidden"><% 
  BudgetCommessaIdCommessaAppart.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdCommessaPrm =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdCommessaPrm"); 
  BudgetCommessaIdCommessaPrm.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdCommessaPrm.getClassType()%>" id="<%=BudgetCommessaIdCommessaPrm.getId()%>" maxlength="<%=BudgetCommessaIdCommessaPrm.getMaxLength()%>" name="<%=BudgetCommessaIdCommessaPrm.getName()%>" size="<%=BudgetCommessaIdCommessaPrm.getSize()%>" type="hidden"><% 
  BudgetCommessaIdCommessaPrm.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaQuantitaPrm =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "QuantitaPrm"); 
  BudgetCommessaQuantitaPrm.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaQuantitaPrm.getClassType()%>" id="<%=BudgetCommessaQuantitaPrm.getId()%>" maxlength="<%=BudgetCommessaQuantitaPrm.getMaxLength()%>" name="<%=BudgetCommessaQuantitaPrm.getName()%>" size="<%=BudgetCommessaQuantitaPrm.getSize()%>" type="hidden"><% 
  BudgetCommessaQuantitaPrm.write(out); 
%>

					<% 
  WebTextInput BudgetCommessaIdUMPrmMag =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "IdUMPrmMag"); 
  BudgetCommessaIdUMPrmMag.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaIdUMPrmMag.getClassType()%>" id="<%=BudgetCommessaIdUMPrmMag.getId()%>" maxlength="<%=BudgetCommessaIdUMPrmMag.getMaxLength()%>" name="<%=BudgetCommessaIdUMPrmMag.getName()%>" size="<%=BudgetCommessaIdUMPrmMag.getSize()%>" type="hidden"><% 
  BudgetCommessaIdUMPrmMag.write(out); 
%>

				</td>
			</tr>
			<tr>
				<td height="140px" valign="top">
					<table border="0" style="margin: 0 0 0 2 ;" width="90%">
						<tr>
							<td nowrap width="120px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "IdCommessa", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
							<td nowrap width="500px"><% 
  WebMultiSearchForm BudgetCommessaCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("BudgetCommessa", "Commessa", false, false, true, 1, null, null); 
  BudgetCommessaCommessa.setParent(BudgetCommessaForm); 
  BudgetCommessaCommessa.setOnKeyChange("completaDatiBudget()"); 
  BudgetCommessaCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  BudgetCommessaCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
							<td nowrap rowspan="5" style="padding-right: 10px;" valign="top" width="230px">
								<fieldset id="Costi" name="Costi" style="width:100%;"> 
									<legend align="top">
										<label class="thLabel" id="CostiLab" name="CostiLab">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "CostiLbl", null, null, null, null); 
 label.setParent(BudgetCommessaForm); 
label.write(out); }%> 
</label>
									</legend>
									<table border="0" style="margin: 0 0 0 0;">
										<tr>
											<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "CostoRiferimento", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoRiferimento"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebTextInput BudgetCommessaCostoRiferimento =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "CostoRiferimento"); 
  BudgetCommessaCostoRiferimento.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaCostoRiferimento.getClassType()%>" id="<%=BudgetCommessaCostoRiferimento.getId()%>" maxlength="<%=BudgetCommessaCostoRiferimento.getMaxLength()%>" name="<%=BudgetCommessaCostoRiferimento.getName()%>" size="<%=BudgetCommessaCostoRiferimento.getSize()%>"><% 
  BudgetCommessaCostoRiferimento.write(out); 
%>
</td>
										</tr>
										<tr>
											<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "CostoPrimo", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoPrimo"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebTextInput BudgetCommessaCostoPrimo =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "CostoPrimo"); 
  BudgetCommessaCostoPrimo.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaCostoPrimo.getClassType()%>" id="<%=BudgetCommessaCostoPrimo.getId()%>" maxlength="<%=BudgetCommessaCostoPrimo.getMaxLength()%>" name="<%=BudgetCommessaCostoPrimo.getName()%>" size="<%=BudgetCommessaCostoPrimo.getSize()%>"><% 
  BudgetCommessaCostoPrimo.write(out); 
%>
</td>
										</tr>
										<tr>
											<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "CostoIndustriale", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoIndustriale"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebTextInput BudgetCommessaCostoIndustriale =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "CostoIndustriale"); 
  BudgetCommessaCostoIndustriale.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaCostoIndustriale.getClassType()%>" id="<%=BudgetCommessaCostoIndustriale.getId()%>" maxlength="<%=BudgetCommessaCostoIndustriale.getMaxLength()%>" name="<%=BudgetCommessaCostoIndustriale.getName()%>" size="<%=BudgetCommessaCostoIndustriale.getSize()%>"><% 
  BudgetCommessaCostoIndustriale.write(out); 
%>
</td>
										</tr>
										<tr>
											<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "CostoGenerale", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="CostoGenerale"><%label.write(out);%></label><%}%></td>
											<td><% 
  WebTextInput BudgetCommessaCostoGenerale =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "CostoGenerale"); 
  BudgetCommessaCostoGenerale.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaCostoGenerale.getClassType()%>" id="<%=BudgetCommessaCostoGenerale.getId()%>" maxlength="<%=BudgetCommessaCostoGenerale.getMaxLength()%>" name="<%=BudgetCommessaCostoGenerale.getName()%>" size="<%=BudgetCommessaCostoGenerale.getSize()%>"><% 
  BudgetCommessaCostoGenerale.write(out); 
%>
</td>
										</tr>
									</table>	
								</fieldset>
							</td>
							<td nowrap rowspan="5" valign="top">
								<table border="0" style="margin: 0 0 0 0;">
									<tr>
										<td align="right" nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "StatoAvanzamento", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="StatoAvanzamento"><%label.write(out);%></label><%}%></td>
										<td><% 
  WebComboBox BudgetCommessaStatoAvanzamento =  
     new com.thera.thermfw.web.WebComboBox("BudgetCommessa", "StatoAvanzamento", null); 
  BudgetCommessaStatoAvanzamento.setParent(BudgetCommessaForm); 
%>
<select id="<%=BudgetCommessaStatoAvanzamento.getId()%>" name="<%=BudgetCommessaStatoAvanzamento.getName()%>"><% 
  BudgetCommessaStatoAvanzamento.write(out); 
%> 
</select></td>
									</tr>
									<tr>
										<td colspan="2"><% 
   request.setAttribute("parentForm", BudgetCommessaForm); 
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
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "DataRiferimento", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataRiferimento"><%label.write(out);%></label><%}%></td>
							<td nowrap><% 
  WebTextInput BudgetCommessaDataRiferimento =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "DataRiferimento"); 
  BudgetCommessaDataRiferimento.setShowCalendarBtn(true); 
  BudgetCommessaDataRiferimento.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaDataRiferimento.getClassType()%>" id="<%=BudgetCommessaDataRiferimento.getId()%>" maxlength="<%=BudgetCommessaDataRiferimento.getMaxLength()%>" name="<%=BudgetCommessaDataRiferimento.getName()%>" size="<%=BudgetCommessaDataRiferimento.getSize()%>"><% 
  BudgetCommessaDataRiferimento.write(out); 
%>
</td>
						</tr>	
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "IdComponenteTotali", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="ComponenteTotali"><%label.write(out);%></label><%}%></td>
							<td colspan="4" nowrap><% 
  WebMultiSearchForm BudgetCommessaComponenteTotali =  
     new com.thera.thermfw.web.WebMultiSearchForm("BudgetCommessa", "ComponenteTotali", false, false, true, 1, null, null); 
  BudgetCommessaComponenteTotali.setParent(BudgetCommessaForm); 
  BudgetCommessaComponenteTotali.write(out); 
%>
<!--<span class="multisearchform" id="ComponenteTotali"></span>--></td>
						</tr>
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "Descrizione", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Descrizione"><%label.write(out);%></label><%}%></td>
							<td colspan="4" nowrap><% 
  WebTextInput BudgetCommessaDescrizione =  
     new com.thera.thermfw.web.WebTextInput("BudgetCommessa", "Descrizione"); 
  BudgetCommessaDescrizione.setParent(BudgetCommessaForm); 
%>
<input class="<%=BudgetCommessaDescrizione.getClassType()%>" id="<%=BudgetCommessaDescrizione.getId()%>" maxlength="<%=BudgetCommessaDescrizione.getMaxLength()%>" name="<%=BudgetCommessaDescrizione.getName()%>" size="<%=BudgetCommessaDescrizione.getSize()%>"><% 
  BudgetCommessaDescrizione.write(out); 
%>
</td>
						</tr>	
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "BudgetCommessa", "IdNumeroPreventivo", null); 
   label.setParent(BudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Preventivo"><%label.write(out);%></label><%}%></td>
							<td colspan="4" nowrap>
								<% 
  WebMultiSearchForm BudgetCommessaPreventivo =  
     new com.thera.thermfw.web.WebMultiSearchForm("BudgetCommessa", "Preventivo", false, false, true, 2, null, null); 
  BudgetCommessaPreventivo.setParent(BudgetCommessaForm); 
  BudgetCommessaPreventivo.setAdditionalRestrictConditions("IdCommessa,IdCommessa;"); 
  BudgetCommessaPreventivo.write(out); 
%>
<!--<span class="multisearchform" id="Preventivo"></span>-->
								<button id="CaricaBUT" name="CaricaBUT" onclick="caricaPreventivo()" style="width:70" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "Carica")%>" type="button"><%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "Carica")%></button>
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<table celpadding="10px">
									<tr>
										<td style="white-space:nowrap;" width="50px"><% 
  WebCheckBox BudgetCommessaTotali =  
     new com.thera.thermfw.web.WebCheckBox("BudgetCommessa", "Totali"); 
  BudgetCommessaTotali.setParent(BudgetCommessaForm); 
%>
<input id="<%=BudgetCommessaTotali.getId()%>" name="<%=BudgetCommessaTotali.getName()%>" type="checkbox" value="Y"><%
  BudgetCommessaTotali.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox BudgetCommessaDettagliCommessa =  
     new com.thera.thermfw.web.WebCheckBox("BudgetCommessa", "DettagliCommessa"); 
  BudgetCommessaDettagliCommessa.setParent(BudgetCommessaForm); 
%>
<input id="<%=BudgetCommessaDettagliCommessa.getId()%>" name="<%=BudgetCommessaDettagliCommessa.getName()%>" type="checkbox" value="Y"><%
  BudgetCommessaDettagliCommessa.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox BudgetCommessaDettagliSottoCommesse =  
     new com.thera.thermfw.web.WebCheckBox("BudgetCommessa", "DettagliSottoCommesse"); 
  BudgetCommessaDettagliSottoCommesse.setParent(BudgetCommessaForm); 
%>
<input id="<%=BudgetCommessaDettagliSottoCommesse.getId()%>" name="<%=BudgetCommessaDettagliSottoCommesse.getName()%>" type="checkbox" value="Y"><%
  BudgetCommessaDettagliSottoCommesse.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox BudgetCommessaComponentiPropri =  
     new com.thera.thermfw.web.WebCheckBox("BudgetCommessa", "ComponentiPropri"); 
  BudgetCommessaComponentiPropri.setParent(BudgetCommessaForm); 
%>
<input id="<%=BudgetCommessaComponentiPropri.getId()%>" name="<%=BudgetCommessaComponentiPropri.getName()%>" type="checkbox" value="Y"><%
  BudgetCommessaComponentiPropri.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox BudgetCommessaSoloComponentiValorizzate =  
     new com.thera.thermfw.web.WebCheckBox("BudgetCommessa", "SoloComponentiValorizzate"); 
  BudgetCommessaSoloComponentiValorizzate.setParent(BudgetCommessaForm); 
%>
<input id="<%=BudgetCommessaSoloComponentiValorizzate.getId()%>" name="<%=BudgetCommessaSoloComponentiValorizzate.getName()%>" type="checkbox" value="Y"><%
  BudgetCommessaSoloComponentiValorizzate.write(out); 
%>
</td>
										<td><% 
  MDVButton newMDV =  
   new com.thera.thermfw.web.MDVButton("SaveScreenData", "com.thera.thermfw.web.resources.web", "SaveScreenData", "thermweb/image/gui/SaveScreenData.gif", null, null); 
  newMDV.setParent(BudgetCommessaForm); 
  newMDV.setImageWidth("16"); 
  newMDV.setImageHeight("16"); 
  newMDV.write(out); 
%>
<!--<span class="mdvbutton" id="newMDV" name="newMDV"></span>--></td>	
										<td>
										<button id="thAggiornaBUT" name="thAggiornaBUT" onclick="aggiornaBudgetView()" style="width:26px;height:26px;align:top" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "Aggiorna")%>" type="button"><img border="0" height="16px" src="thermweb/image/gui/cnr/Run.gif" width="16px">
										<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "AggiornaID")%></button>
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
					<iframe height="100%" id="BudgetCommessaDettaglio" name="BudgetCommessaDettaglio" src style="border: 0px solid black;margin:0 5px 10px 5px;height: calc(100% - 15px);width: calc(100% - 10px);" width="100%"></iframe>
				</td>
			</tr>
			<tr><td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(BudgetCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td></tr>
		</table>
	<%
  BudgetCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = BudgetCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", BudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              BudgetCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, BudgetCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, BudgetCommessaBODC.getErrorList().getErrors()); 
           if(BudgetCommessaBODC.getConflict() != null) 
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
     if(BudgetCommessaBODC != null && !BudgetCommessaBODC.close(false)) 
        errors.addAll(0, BudgetCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = BudgetCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", BudgetCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = BudgetCommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

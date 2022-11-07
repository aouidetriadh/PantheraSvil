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
  BODataCollector StampaRiepilogoCommesseBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm StampaRiepilogoCommesseForm =  
     new com.thera.thermfw.web.WebFormForBatchForm(request, response, "StampaRiepilogoCommesseForm", "StampaRiepilogoCommesse", null, "com.thera.thermfw.batch.web.BatchFormActionAdapter", false, false, true, true, true, true, null, 1, true, "it/thera/thip/produzione/commessa/StampaRiepilogoCommesse.js"); 
  StampaRiepilogoCommesseForm.setServletEnvironment(se); 
  StampaRiepilogoCommesseForm.setJSTypeList(jsList); 
  StampaRiepilogoCommesseForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  StampaRiepilogoCommesseForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ((WebFormForBatchForm)  StampaRiepilogoCommesseForm).setGenerateSSDEnabled(true); 
  StampaRiepilogoCommesseForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.StampaRiepilogoCommesseWebFormModifier"); 
  StampaRiepilogoCommesseForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = StampaRiepilogoCommesseForm.getMode(); 
  String key = StampaRiepilogoCommesseForm.getKey(); 
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
        StampaRiepilogoCommesseForm.outTraceInfo(getClass().getName()); 
        String collectorName = StampaRiepilogoCommesseForm.findBODataCollectorName(); 
				 StampaRiepilogoCommesseBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (StampaRiepilogoCommesseBODC instanceof WebDataCollector) 
            ((WebDataCollector)StampaRiepilogoCommesseBODC).setServletEnvironment(se); 
        StampaRiepilogoCommesseBODC.initialize("StampaRiepilogoCommesse", true, 1); 
        int rcBODC; 
        if (StampaRiepilogoCommesseBODC.getBo() instanceof BatchRunnable) 
          rcBODC = StampaRiepilogoCommesseBODC.initSecurityServices("RUN", mode, true, false, true); 
        else 
          rcBODC = StampaRiepilogoCommesseBODC.initSecurityServices(mode, true, true, true); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           StampaRiepilogoCommesseForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = StampaRiepilogoCommesseBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              StampaRiepilogoCommesseForm.setBODataCollector(StampaRiepilogoCommesseBODC); 
              StampaRiepilogoCommesseForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>StampaRiepilogoCommesse</title>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(StampaRiepilogoCommesseForm); 
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
  myToolBarTB.setParent(StampaRiepilogoCommesseForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/com/thera/thermfw/batch/PrintRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=StampaRiepilogoCommesseForm.getBodyOnBeforeUnload()%>" onload="<%=StampaRiepilogoCommesseForm.getBodyOnLoad()%>" onunload="<%=StampaRiepilogoCommesseForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   StampaRiepilogoCommesseForm.writeBodyStartElements(out); 
%> 


<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = StampaRiepilogoCommesseForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", StampaRiepilogoCommesseBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=StampaRiepilogoCommesseForm.getServlet()%>" method="post" name="StampaRiepilogoCommesse" style="height:100%"><%
  StampaRiepilogoCommesseForm.writeFormStartElements(out); 
%>

<table cellpadding="2" cellspacing="0" height="100%" width="100%">
	<tr valign="top" width="100%">
		<td colspan="3" style="height:0"><% menuBar.writeElements(out); %> 
</td>
	</tr>
	<tr valign="top" width="100%">
		<td colspan="3" style="height:0"><% myToolBarTB.writeChildren(out); %> 
</td>
	</tr>
	<tr>
		<td><input id="thReportId" name="thReportId" type="hidden"></td>
		<td><% 
  WebTextInput StampaRiepilogoCommesseIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("StampaRiepilogoCommesse", "IdAzienda"); 
  StampaRiepilogoCommesseIdAzienda.setParent(StampaRiepilogoCommesseForm); 
%>
<input class="<%=StampaRiepilogoCommesseIdAzienda.getClassType()%>" id="<%=StampaRiepilogoCommesseIdAzienda.getId()%>" maxlength="<%=StampaRiepilogoCommesseIdAzienda.getMaxLength()%>" name="<%=StampaRiepilogoCommesseIdAzienda.getName()%>" size="<%=StampaRiepilogoCommesseIdAzienda.getSize()%>" type="hidden"><% 
  StampaRiepilogoCommesseIdAzienda.write(out); 
%>
</td>
	</tr>
	<tr>
		<td width="33%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "TipoStampa", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="TipoStampa"><%label.write(out);%></label><%}%></td>
		<td><% 
  WebComboBox StampaRiepilogoCommesseTipoStampa =  
     new com.thera.thermfw.web.WebComboBox("StampaRiepilogoCommesse", "TipoStampa", null); 
  StampaRiepilogoCommesseTipoStampa.setParent(StampaRiepilogoCommesseForm); 
%>
<select id="<%=StampaRiepilogoCommesseTipoStampa.getId()%>" name="<%=StampaRiepilogoCommesseTipoStampa.getName()%>"><% 
  StampaRiepilogoCommesseTipoStampa.write(out); 
%> 
</select></td>
		<td width="35%"></td>
	</tr>
	<tr>
		<td width="33%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "StatoCommessa", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="StatoCommessa"><%label.write(out);%></label><%}%></td>
		<td><% 
  WebComboBox StampaRiepilogoCommesseStatoCommessa =  
     new com.thera.thermfw.web.WebComboBox("StampaRiepilogoCommesse", "StatoCommessa", null); 
  StampaRiepilogoCommesseStatoCommessa.setParent(StampaRiepilogoCommesseForm); 
%>
<select id="<%=StampaRiepilogoCommesseStatoCommessa.getId()%>" name="<%=StampaRiepilogoCommesseStatoCommessa.getName()%>"><% 
  StampaRiepilogoCommesseStatoCommessa.write(out); 
%> 
</select></td>
		<td width="35%"></td>
	</tr>
	<!-- 36252 inizio -->
	<tr>
		<td></td>
		<td colspan="2"><% 
  WebCheckBox StampaRiepilogoCommesseUsaConsuntiviStoricizzati =  
     new com.thera.thermfw.web.WebCheckBox("StampaRiepilogoCommesse", "UsaConsuntiviStoricizzati"); 
  StampaRiepilogoCommesseUsaConsuntiviStoricizzati.setParent(StampaRiepilogoCommesseForm); 
  StampaRiepilogoCommesseUsaConsuntiviStoricizzati.setOnClick("onUsaConsuntiviAction()"); 
%>
<input id="<%=StampaRiepilogoCommesseUsaConsuntiviStoricizzati.getId()%>" name="<%=StampaRiepilogoCommesseUsaConsuntiviStoricizzati.getName()%>" type="checkbox" value="Y"><%
  StampaRiepilogoCommesseUsaConsuntiviStoricizzati.write(out); 
%>
</td>
	</tr>
	<tr>
		<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "DataRiferimento", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="DataRiferimento"><%label.write(out);%></label><%}%></td>
		<td colspan="2" nowrap>
			<% 
  WebTextInput StampaRiepilogoCommesseDataRiferimento =  
     new com.thera.thermfw.web.WebTextInput("StampaRiepilogoCommesse", "DataRiferimento"); 
  StampaRiepilogoCommesseDataRiferimento.setShowCalendarBtn(true); 
  StampaRiepilogoCommesseDataRiferimento.setParent(StampaRiepilogoCommesseForm); 
%>
<input class="<%=StampaRiepilogoCommesseDataRiferimento.getClassType()%>" id="<%=StampaRiepilogoCommesseDataRiferimento.getId()%>" maxlength="<%=StampaRiepilogoCommesseDataRiferimento.getMaxLength()%>" name="<%=StampaRiepilogoCommesseDataRiferimento.getName()%>" size="<%=StampaRiepilogoCommesseDataRiferimento.getSize()%>"><% 
  StampaRiepilogoCommesseDataRiferimento.write(out); 
%>

		</td>
	</tr>
	<tr>
		<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "IdCommessa", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
		<td colspan="2" nowrap><% 
  WebMultiSearchForm StampaRiepilogoCommesseCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("StampaRiepilogoCommesse", "Commessa", false, false, true, 1, null, null); 
  StampaRiepilogoCommesseCommessa.setParent(StampaRiepilogoCommesseForm); 
  StampaRiepilogoCommesseCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  StampaRiepilogoCommesseCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
	</tr>
	<tr>
		<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "IdCommessaConsuntivo", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="ConsuntivoCommessa"><%label.write(out);%></label><%}%></td>
		<td colspan="2" nowrap><% 
  WebMultiSearchForm StampaRiepilogoCommesseConsuntivoCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("StampaRiepilogoCommesse", "ConsuntivoCommessa", false, false, true, 2, null, null); 
  StampaRiepilogoCommesseConsuntivoCommessa.setParent(StampaRiepilogoCommesseForm); 
  StampaRiepilogoCommesseConsuntivoCommessa.setAdditionalRestrictConditions("IdCommessa, IdCommessa"); 
  StampaRiepilogoCommesseConsuntivoCommessa.write(out); 
%>
<!--<span class="multisearchform" id="ConsuntivoCommessa"></span>--></td>
	</tr>
	<tr>
		<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "IdCommessaBudget", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="BudgetCommessa"><%label.write(out);%></label><%}%></td>
		<td colspan="2" nowrap><% 
  WebMultiSearchForm StampaRiepilogoCommesseBudgetCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("StampaRiepilogoCommesse", "BudgetCommessa", false, false, true, 2, null, null); 
  StampaRiepilogoCommesseBudgetCommessa.setParent(StampaRiepilogoCommesseForm); 
  StampaRiepilogoCommesseBudgetCommessa.setAdditionalRestrictConditions("IdCommessa, IdCommessa"); 
  StampaRiepilogoCommesseBudgetCommessa.write(out); 
%>
<!--<span class="multisearchform" id="BudgetCommessa"></span>--></td>
	</tr>
	<!-- 36252 fine -->
	<tr>
		<td nowrap width="33%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "TipoChiusura", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="TipoChiusura"><%label.write(out);%></label><%}%></td>
		<td><% 
  WebComboBox StampaRiepilogoCommesseTipoChiusura =  
     new com.thera.thermfw.web.WebComboBox("StampaRiepilogoCommesse", "TipoChiusura", null); 
  StampaRiepilogoCommesseTipoChiusura.setParent(StampaRiepilogoCommesseForm); 
  StampaRiepilogoCommesseTipoChiusura.setOnChange("onTipoChiusuraChange()"); 
%>
<select id="<%=StampaRiepilogoCommesseTipoChiusura.getId()%>" name="<%=StampaRiepilogoCommesseTipoChiusura.getName()%>"><% 
  StampaRiepilogoCommesseTipoChiusura.write(out); 
%> 
</select></td>
		<td width="35%"></td>
	</tr>
        <!--Fix 22273 Inizio-->
        <tr id="TipoCostiCmmTR" style="display:none">
		<td nowrap width="33%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "TipologiaCostiCommessa", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="TipologiaCostiCommessa"><%label.write(out);%></label><%}%></td>
		<td><% 
  WebComboBox StampaRiepilogoCommesseTipologiaCostiCommessa =  
     new com.thera.thermfw.web.WebComboBox("StampaRiepilogoCommesse", "TipologiaCostiCommessa", null); 
  StampaRiepilogoCommesseTipologiaCostiCommessa.setParent(StampaRiepilogoCommesseForm); 
%>
<select id="<%=StampaRiepilogoCommesseTipologiaCostiCommessa.getId()%>" name="<%=StampaRiepilogoCommesseTipologiaCostiCommessa.getName()%>"><% 
  StampaRiepilogoCommesseTipologiaCostiCommessa.write(out); 
%> 
</select></td>
		<td width="35%"></td>
	</tr>
        <!--Fix 22273 Fine-->
	<tr>
		<td nowrap width="33%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "StampaRiepilogoCommesse", "TipologiaCostoRif", null); 
   label.setParent(StampaRiepilogoCommesseForm); 
%><label class="<%=label.getClassType()%>" for="TipologiaCostoRif"><%label.write(out);%></label><%}%></td>
		<td><% 
  WebComboBox StampaRiepilogoCommesseTipologiaCostoRif =  
     new com.thera.thermfw.web.WebComboBox("StampaRiepilogoCommesse", "TipologiaCostoRif", null); 
  StampaRiepilogoCommesseTipologiaCostoRif.setParent(StampaRiepilogoCommesseForm); 
%>
<select id="<%=StampaRiepilogoCommesseTipologiaCostoRif.getId()%>" name="<%=StampaRiepilogoCommesseTipologiaCostoRif.getName()%>"><% 
  StampaRiepilogoCommesseTipologiaCostoRif.write(out); 
%> 
</select></td>
		<td width="35%"></td>
	</tr>
	<tr>
		<td colspan="2">
			<fieldset>
				<legend align="top" name="CostiConsuntivatiProvLegend">
 <% { WebLegend legend = new com.thera.thermfw.web.WebLegend("it.thera.thip.produzione.commessa.resources.StampaRiepilogoCommesse", "CostiConsuntivatiProvLegend"); 
 legend.setParent(StampaRiepilogoCommesseForm); 
legend.write(out); }%> 
</legend>
				<table width="100%">
					<tr>
						<td width="50%"></td>
						<td><% 
  WebCheckBox StampaRiepilogoCommesseCompresoOrdinato =  
     new com.thera.thermfw.web.WebCheckBox("StampaRiepilogoCommesse", "CompresoOrdinato"); 
  StampaRiepilogoCommesseCompresoOrdinato.setParent(StampaRiepilogoCommesseForm); 
%>
<input id="<%=StampaRiepilogoCommesseCompresoOrdinato.getId()%>" name="<%=StampaRiepilogoCommesseCompresoOrdinato.getName()%>" type="checkbox" value="Y"><%
  StampaRiepilogoCommesseCompresoOrdinato.write(out); 
%>
</td>
						<td><% 
  WebCheckBox StampaRiepilogoCommesseCompresoRichiesto =  
     new com.thera.thermfw.web.WebCheckBox("StampaRiepilogoCommesse", "CompresoRichiesto"); 
  StampaRiepilogoCommesseCompresoRichiesto.setParent(StampaRiepilogoCommesseForm); 
%>
<input id="<%=StampaRiepilogoCommesseCompresoRichiesto.getId()%>" name="<%=StampaRiepilogoCommesseCompresoRichiesto.getName()%>" type="checkbox" value="Y"><%
  StampaRiepilogoCommesseCompresoRichiesto.write(out); 
%>
</td>
					</tr>
				</table>
			</fieldset>
		</td>
		<td width="35%"></td>
	</tr>
	<tr>
		<td width="33%"></td>
		<td><% 
  WebCheckBox StampaRiepilogoCommesseExecutePrint =  
     new com.thera.thermfw.web.WebCheckBox("StampaRiepilogoCommesse", "ExecutePrint"); 
  StampaRiepilogoCommesseExecutePrint.setParent(StampaRiepilogoCommesseForm); 
%>
<input id="<%=StampaRiepilogoCommesseExecutePrint.getId()%>" name="<%=StampaRiepilogoCommesseExecutePrint.getName()%>" type="checkbox" value="Y"><%
  StampaRiepilogoCommesseExecutePrint.write(out); 
%>
</td>
		<td width="35%"></td>
	</tr>
	<tr>
		<td colspan="3">
			<table cellpadding="2" cellspacing="0" height="100%" width="100%">
				<% 
   request.setAttribute("parentForm", StampaRiepilogoCommesseForm); 
   String CDForCondFiltro$it$thera$thip$cs$CondizioniFiltri$jsp = "CondFiltro"; 
%>
<jsp:include page="/it/thera/thip/cs/CondizioniFiltri.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForCondFiltro$it$thera$thip$cs$CondizioniFiltri$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="CondFiltro"></span>-->
			</table>
		</td>
	</tr>
	<tr valign="bottom">
		<td colspan="4" style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(StampaRiepilogoCommesseForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
	</tr>
</table>
<%
  StampaRiepilogoCommesseForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = StampaRiepilogoCommesseForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", StampaRiepilogoCommesseBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              StampaRiepilogoCommesseForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, StampaRiepilogoCommesseBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, StampaRiepilogoCommesseBODC.getErrorList().getErrors()); 
           if(StampaRiepilogoCommesseBODC.getConflict() != null) 
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
     if(StampaRiepilogoCommesseBODC != null && !StampaRiepilogoCommesseBODC.close(false)) 
        errors.addAll(0, StampaRiepilogoCommesseBODC.getErrorList().getErrors()); 
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
     String errorPage = StampaRiepilogoCommesseForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", StampaRiepilogoCommesseBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = StampaRiepilogoCommesseForm.getConflictPage(); 
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

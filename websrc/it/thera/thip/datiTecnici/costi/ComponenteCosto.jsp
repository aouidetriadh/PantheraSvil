<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///D:/3rdParty/PantheraSvil/websrc/dtd/xhtml1-transitional.dtd">
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
  BODataCollector ComponenteCostoBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm ComponenteCostoForm =  
     new com.thera.thermfw.web.WebForm(request, response, "ComponenteCostoForm", "ComponenteCosto", "Arial,10", "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, true, true, true, true, null, 1, true, "it/thera/thip/datiTecnici/costi/ComponenteCosto.js"); 
  ComponenteCostoForm.setServletEnvironment(se); 
  ComponenteCostoForm.setJSTypeList(jsList); 
  ComponenteCostoForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  ComponenteCostoForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ComponenteCostoForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = ComponenteCostoForm.getMode(); 
  String key = ComponenteCostoForm.getKey(); 
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
        ComponenteCostoForm.outTraceInfo(getClass().getName()); 
        String collectorName = ComponenteCostoForm.findBODataCollectorName(); 
                ComponenteCostoBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (ComponenteCostoBODC instanceof WebDataCollector) 
            ((WebDataCollector)ComponenteCostoBODC).setServletEnvironment(se); 
        ComponenteCostoBODC.initialize("ComponenteCosto", true, 1); 
        ComponenteCostoForm.setBODataCollector(ComponenteCostoBODC); 
        int rcBODC = ComponenteCostoForm.initSecurityServices(); 
        mode = ComponenteCostoForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           ComponenteCostoForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = ComponenteCostoBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              ComponenteCostoForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>ComponenteCosto</title>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(ComponenteCostoForm); 
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
  myToolBarTB.setParent(ComponenteCostoForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>

<body bottommargin="0" leftmargin="0" onbeforeunload="<%=ComponenteCostoForm.getBodyOnBeforeUnload()%>" onload="<%=ComponenteCostoForm.getBodyOnLoad()%>" onunload="<%=ComponenteCostoForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   ComponenteCostoForm.writeBodyStartElements(out); 
%> 


<% 
  WebScript script_0 =  
   new com.thera.thermfw.web.WebScript(); 
 script_0.setRequest(request); 
 script_0.setSrcAttribute("it/thera/thip/cs/util.js"); 
 script_0.setLanguageAttribute("JavaScript1.2"); 
  script_0.write(out); 
%>
<!--<script language="JavaScript1.2" src="it/thera/thip/cs/util.js" type="text/javascript"></script>-->

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = ComponenteCostoForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", ComponenteCostoBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=ComponenteCostoForm.getServlet()%>" method="post" name="form1" style="height:100%"><%
  ComponenteCostoForm.writeFormStartElements(out); 
%>

		<table cellpadding="2" cellspacing="0" height="100%" id="emptyborder" width="100%">
			<tr>
				<td style="height:0"><% menuBar.writeElements(out); %> 
</td>
			</tr>
			<tr>
				<td style="height:0"><% myToolBarTB.writeChildren(out); %> 
</td>
			</tr>
			<tr>
				<td height="100%">
					<!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(ComponenteCostoForm); 
 mytabbed.addTab("tab1", "it.thera.thip.datiTecnici.costi.resources.ComponenteCosto", "Componente", "ComponenteCosto", null, null, null, null); 
 mytabbed.addTab("tab3", "it.thera.thip.datiTecnici.costi.resources.ComponenteCosto", "TipiCosto", "ComponenteCosto", null, null, null, null); 
 mytabbed.addTab("tab2", "it.thera.thip.cs.resources.Cs", "DescrizioniNLS", "ComponenteCosto", null, null, null, null); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
						<div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab1")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab1"); %>
							<table border="0" style="margin:5 5 5 5">					
								<tr>
									<td width="120px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "IdComponenteCosto", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="T1"><%label.write(out);%></label><%}%></td>
									<td align="left"><% 
  WebTextInput ComponenteCostoIdComponenteCosto =  
     new com.thera.thermfw.web.WebTextInput("ComponenteCosto", "IdComponenteCosto"); 
  ComponenteCostoIdComponenteCosto.setParent(ComponenteCostoForm); 
%>
<input class="<%=ComponenteCostoIdComponenteCosto.getClassType()%>" id="<%=ComponenteCostoIdComponenteCosto.getId()%>" maxlength="<%=ComponenteCostoIdComponenteCosto.getMaxLength()%>" name="<%=ComponenteCostoIdComponenteCosto.getName()%>" size="<%=ComponenteCostoIdComponenteCosto.getSize()%>" type="text"><% 
  ComponenteCostoIdComponenteCosto.write(out); 
%>
</td>
								</tr>
								<tr>
									<td colspan="2" width="120px"><% 
   request.setAttribute("parentForm", ComponenteCostoForm); 
   String CDForDescrizione$it$thera$thip$cs$Descrizione$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/Descrizione.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$Descrizione$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="T2"></span>--></td>
								</tr>
								<tr>
									<td width="120px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "TipoComponente", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="T3"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebComboBox ComponenteCostoTipoComponente =  
     new com.thera.thermfw.web.WebComboBox("ComponenteCosto", "TipoComponente", null); 
  ComponenteCostoTipoComponente.setParent(ComponenteCostoForm); 
%>
<select id="<%=ComponenteCostoTipoComponente.getId()%>" name="<%=ComponenteCostoTipoComponente.getName()%>"><% 
  ComponenteCostoTipoComponente.write(out); 
%> 
</select></td>
								</tr>
								<tr>
									<td width="120px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "Provenienza", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="T4"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebComboBox ComponenteCostoProvenienza =  
     new com.thera.thermfw.web.WebComboBox("ComponenteCosto", "Provenienza", null); 
  ComponenteCostoProvenienza.setParent(ComponenteCostoForm); 
  ComponenteCostoProvenienza.setOnChange("onChangeProvenienza()"); 
%>
<select id="<%=ComponenteCostoProvenienza.getId()%>" name="<%=ComponenteCostoProvenienza.getName()%>"><% 
  ComponenteCostoProvenienza.write(out); 
%> 
</select></td>
								</tr>
								<tr>
									<td width="120px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "Criticita", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="T5"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebComboBox ComponenteCostoCriticita =  
     new com.thera.thermfw.web.WebComboBox("ComponenteCosto", "Criticita", null); 
  ComponenteCostoCriticita.setParent(ComponenteCostoForm); 
%>
<select id="<%=ComponenteCostoCriticita.getId()%>" name="<%=ComponenteCostoCriticita.getName()%>"><% 
  ComponenteCostoCriticita.write(out); 
%> 
</select></td>
								</tr>
								<tr>
									<td width="120px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "IdFormula", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="T6"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebMultiSearchForm ComponenteCostoFormula =  
     new com.thera.thermfw.web.WebMultiSearchForm("ComponenteCosto", "Formula", false, false, true, 1, null, null); 
  ComponenteCostoFormula.setParent(ComponenteCostoForm); 
  ComponenteCostoFormula.write(out); 
%>
<!--<span class="multisearchform" id="T6"></span>--></td>
								</tr>
								<tr>
									<td colspan="2" width="120px"><% 
   request.setAttribute("parentForm", ComponenteCostoForm); 
   String CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp = "DatiComuniEstesi"; 
%>
<jsp:include page="/it/thera/thip/cs/DatiComuniEstesi.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="T7"></span>--></td>
								</tr>
								<!-- 33950 inizio -->
								<tr>
									<td colspan="2">
										<table>
											<tr>
												<td>
													<fieldset name="CommessaFieldSet"><legend align="top"><label class="thLabel" id="CommessaLabel" name="CommessaLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.datiTecnici.costi.resources.ComponenteCosto", "Commessa", null, null, null, null); 
 label.setParent(ComponenteCostoForm); 
label.write(out); }%> 
</label></legend>
														<table border="0">
															<tr>
																<td width="110px"></td>
																<td nowrap><% 
  WebCheckBox ComponenteCostoGestioneATempo =  
     new com.thera.thermfw.web.WebCheckBox("ComponenteCosto", "GestioneATempo"); 
  ComponenteCostoGestioneATempo.setParent(ComponenteCostoForm); 
%>
<input id="<%=ComponenteCostoGestioneATempo.getId()%>" name="<%=ComponenteCostoGestioneATempo.getName()%>" onclick="gestioneATempo();" type="Checkbox" value="Y"><%
  ComponenteCostoGestioneATempo.write(out); 
%>
</td>
															</tr>
															<tr>
																<td nowrap width="110px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "IdTipoCosto", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="TipoCosto"><%label.write(out);%></label><%}%></td>
																<td nowrap><% 
  WebMultiSearchForm ComponenteCostoTipoCosto =  
     new com.thera.thermfw.web.WebMultiSearchForm("ComponenteCosto", "TipoCosto", false, false, true, 1, null, null); 
  ComponenteCostoTipoCosto.setParent(ComponenteCostoForm); 
  ComponenteCostoTipoCosto.write(out); 
%>
<!--<span class="multisearchform" id="TipoCosto"></span>--></td>
															</tr>
															<!--tr>                 <td nowrap="true"><label for="TipoRisorsa">TipoRisorsa</label></td>                 <td nowrap="true"><select name="TipoRisorsa" id="TipoRisorsa"/></td>                </tr>                <tr>                 <td nowrap="true"><label for="LivelloRisorsa">LivelloRisorsa</label></td>                 <td nowrap="true"><select name="LivelloRisorsa" id="LivelloRisorsa"/></td>                </tr-->
															<tr>
																<td nowrap width="110px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "IdRisorsa", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="Risorsa"><%label.write(out);%></label><%}%></td>
																<td nowrap>
																	<% 
  WebComboBox ComponenteCostoTipoRisorsa =  
     new com.thera.thermfw.web.WebComboBox("ComponenteCosto", "TipoRisorsa", null); 
  ComponenteCostoTipoRisorsa.setParent(ComponenteCostoForm); 
  ComponenteCostoTipoRisorsa.setOnChange("filedsClear()"); 
%>
<select id="<%=ComponenteCostoTipoRisorsa.getId()%>" name="<%=ComponenteCostoTipoRisorsa.getName()%>"><% 
  ComponenteCostoTipoRisorsa.write(out); 
%> 
</select>
																	<% 
  WebComboBox ComponenteCostoLivelloRisorsa =  
     new com.thera.thermfw.web.WebComboBox("ComponenteCosto", "LivelloRisorsa", null); 
  ComponenteCostoLivelloRisorsa.setParent(ComponenteCostoForm); 
  ComponenteCostoLivelloRisorsa.setOnChange("filedsClear()"); 
%>
<select id="<%=ComponenteCostoLivelloRisorsa.getId()%>" name="<%=ComponenteCostoLivelloRisorsa.getName()%>"><% 
  ComponenteCostoLivelloRisorsa.write(out); 
%> 
</select>
																	<% 
  WebMultiSearchForm ComponenteCostoRisorsa =  
     new com.thera.thermfw.web.WebMultiSearchForm("ComponenteCosto", "Risorsa", false, false, true, 1, null, null); 
  ComponenteCostoRisorsa.setParent(ComponenteCostoForm); 
  ComponenteCostoRisorsa.write(out); 
%>
<!--<span class="multisearchform" id="Risorsa"></span>-->
																</td>
															</tr>
															<tr>
																<td nowrap width="110px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ComponenteCosto", "CostoUnitario", null); 
   label.setParent(ComponenteCostoForm); 
%><label class="<%=label.getClassType()%>" for="CostoUnitario"><%label.write(out);%></label><%}%></td>
																<td nowrap><% 
  WebTextInput ComponenteCostoCostoUnitario =  
     new com.thera.thermfw.web.WebTextInput("ComponenteCosto", "CostoUnitario"); 
  ComponenteCostoCostoUnitario.setParent(ComponenteCostoForm); 
%>
<input class="<%=ComponenteCostoCostoUnitario.getClassType()%>" id="<%=ComponenteCostoCostoUnitario.getId()%>" maxlength="<%=ComponenteCostoCostoUnitario.getMaxLength()%>" name="<%=ComponenteCostoCostoUnitario.getName()%>" size="<%=ComponenteCostoCostoUnitario.getSize()%>" type="text"><% 
  ComponenteCostoCostoUnitario.write(out); 
%>
</td>
															</tr>															
														</table>
													</fieldset>
												</td>												
											</tr>
										</table>									
									</td>
								</tr>								
								<!-- 33950 fine -->
								<tr>
									<td><% 
  WebTextInput ComponenteCostoIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("ComponenteCosto", "IdAzienda"); 
  ComponenteCostoIdAzienda.setParent(ComponenteCostoForm); 
%>
<input class="<%=ComponenteCostoIdAzienda.getClassType()%>" id="<%=ComponenteCostoIdAzienda.getId()%>" maxlength="<%=ComponenteCostoIdAzienda.getMaxLength()%>" name="<%=ComponenteCostoIdAzienda.getName()%>" size="<%=ComponenteCostoIdAzienda.getSize()%>" style="display: none;" type="text"><% 
  ComponenteCostoIdAzienda.write(out); 
%>
</td>
								</tr>
							</table>
						<% mytabbed.endTab(); %> 
</div>
						<div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab3")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab3"); %>
							<table width="100%">
								<tr>
									<td><% 
  WebDoubleList ComponenteCostoTipiCosto =  
     new com.thera.thermfw.web.WebDoubleList("ComponenteCosto", "TipiCosto", "25", "15", null, null, null, "Description", true); 
 ComponenteCostoTipiCosto.setParent(ComponenteCostoForm); 
 ComponenteCostoTipiCosto.setGetAvailableElements("getTipiCostoAvailable"); 
  ComponenteCostoTipiCosto.setModifyPresent(true); 
  ComponenteCostoTipiCosto.write(out); 
%>
<!--<span class="doublelist" id="T9"></span>--></td>
								</tr>
							</table>
						<% mytabbed.endTab(); %> 
</div>
						<div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab2")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab2"); %>
							<table width="100%">
								<tr>
									<td valign="top"><% 
   request.setAttribute("parentForm", ComponenteCostoForm); 
   String CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/DescrizioneInLingua.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="T8" name="T8"></span>--></td>
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
  errorList.setParent(ComponenteCostoForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
			</tr>
		</table>
	<%
  ComponenteCostoForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = ComponenteCostoForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", ComponenteCostoBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              ComponenteCostoForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, ComponenteCostoBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, ComponenteCostoBODC.getErrorList().getErrors()); 
           if(ComponenteCostoBODC.getConflict() != null) 
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
     if(ComponenteCostoBODC != null && !ComponenteCostoBODC.close(false)) 
        errors.addAll(0, ComponenteCostoBODC.getErrorList().getErrors()); 
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
     String errorPage = ComponenteCostoForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", ComponenteCostoBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = ComponenteCostoForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>

</html>

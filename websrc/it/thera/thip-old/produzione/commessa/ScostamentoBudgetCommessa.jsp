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
  BODataCollector ScostamentoBudgetCommessaBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm ScostamentoBudgetCommessaForm =  
     new com.thera.thermfw.web.WebForm(request, response, "ScostamentoBudgetCommessaForm", "ScostamentoBudgetCommessa", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, false, false, true, true, null, 1, true, "it/thera/thip/produzione/commessa/ScostamentoBudgetCommessa.js"); 
  ScostamentoBudgetCommessaForm.setServletEnvironment(se); 
  ScostamentoBudgetCommessaForm.setJSTypeList(jsList); 
  ScostamentoBudgetCommessaForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  ScostamentoBudgetCommessaForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ScostamentoBudgetCommessaForm.setWebFormModifierClass("it.thera.thip.produzione.commessa.web.ScostamentoBudgetCMMWebFormModifier"); 
  ScostamentoBudgetCommessaForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = ScostamentoBudgetCommessaForm.getMode(); 
  String key = ScostamentoBudgetCommessaForm.getKey(); 
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
        ScostamentoBudgetCommessaForm.outTraceInfo(getClass().getName()); 
        String collectorName = ScostamentoBudgetCommessaForm.findBODataCollectorName(); 
                ScostamentoBudgetCommessaBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (ScostamentoBudgetCommessaBODC instanceof WebDataCollector) 
            ((WebDataCollector)ScostamentoBudgetCommessaBODC).setServletEnvironment(se); 
        ScostamentoBudgetCommessaBODC.initialize("ScostamentoBudgetCommessa", true, 1); 
        ScostamentoBudgetCommessaForm.setBODataCollector(ScostamentoBudgetCommessaBODC); 
        int rcBODC = ScostamentoBudgetCommessaForm.initSecurityServices(); 
        mode = ScostamentoBudgetCommessaForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           ScostamentoBudgetCommessaForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = ScostamentoBudgetCommessaBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              ScostamentoBudgetCommessaForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Scostamento budget commessa</title>
</head>
<body bottommargin="0" leftmargin="0" onbeforeunload="<%=ScostamentoBudgetCommessaForm.getBodyOnBeforeUnload()%>" onload="<%=ScostamentoBudgetCommessaForm.getBodyOnLoad()%>" onunload="<%=ScostamentoBudgetCommessaForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   ScostamentoBudgetCommessaForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = ScostamentoBudgetCommessaForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", ScostamentoBudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=ScostamentoBudgetCommessaForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  ScostamentoBudgetCommessaForm.writeFormStartElements(out); 
%>

		<table border="0" cellpadding="2" cellspacing="2" height="100%" width="100%">
			<tr>
				<td height="150px" valign="top">
					<table style="margin: 0 0 0 0 ;" width="90%">
						<tr>
							<td nowrap width="110px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ScostamentoBudgetCommessa", "IdCommessa", null); 
   label.setParent(ScostamentoBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="Commessa"><%label.write(out);%></label><%}%></td>
							<td colspan="3" nowrap><% 
  WebMultiSearchForm ScostamentoBudgetCommessaCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("ScostamentoBudgetCommessa", "Commessa", false, false, true, 1, null, null); 
  ScostamentoBudgetCommessaCommessa.setParent(ScostamentoBudgetCommessaForm); 
  ScostamentoBudgetCommessaCommessa.setOnKeyChange("completaDati()"); 
  ScostamentoBudgetCommessaCommessa.setFixedRestrictConditions("IdCommessaAppartenenza,NULL_VALUE"); 
  ScostamentoBudgetCommessaCommessa.write(out); 
%>
<!--<span class="multisearchform" id="Commessa"></span>--></td>
						</tr>
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ScostamentoBudgetCommessa", "DataRiferimento", null); 
   label.setParent(ScostamentoBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataRiferimento"><%label.write(out);%></label><%}%></td>
							<td colspan="3" nowrap>
								<% 
  WebTextInput ScostamentoBudgetCommessaDataRiferimento =  
     new com.thera.thermfw.web.WebTextInput("ScostamentoBudgetCommessa", "DataRiferimento"); 
  ScostamentoBudgetCommessaDataRiferimento.setShowCalendarBtn(true); 
  ScostamentoBudgetCommessaDataRiferimento.setParent(ScostamentoBudgetCommessaForm); 
%>
<input class="<%=ScostamentoBudgetCommessaDataRiferimento.getClassType()%>" id="<%=ScostamentoBudgetCommessaDataRiferimento.getId()%>" maxlength="<%=ScostamentoBudgetCommessaDataRiferimento.getMaxLength()%>" name="<%=ScostamentoBudgetCommessaDataRiferimento.getName()%>" size="<%=ScostamentoBudgetCommessaDataRiferimento.getSize()%>"><% 
  ScostamentoBudgetCommessaDataRiferimento.write(out); 
%>

								<button id="IndividuaBUT" name="IndividuaBUT" onclick="individua()" style="width:90" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa", "Individua")%>" type="button"><%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa", "Individua")%></button>
							</td>				
						</tr>			
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ScostamentoBudgetCommessa", "IdCommessaBudget", null); 
   label.setParent(ScostamentoBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="BudgetCommessa"><%label.write(out);%></label><%}%></td>
							<td nowrap width="600px"><% 
  WebMultiSearchForm ScostamentoBudgetCommessaBudgetCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("ScostamentoBudgetCommessa", "BudgetCommessa", false, false, true, 2, null, null); 
  ScostamentoBudgetCommessaBudgetCommessa.setParent(ScostamentoBudgetCommessaForm); 
  ScostamentoBudgetCommessaBudgetCommessa.setAdditionalRestrictConditions("IdCommessa, IdCommessa"); 
  ScostamentoBudgetCommessaBudgetCommessa.write(out); 
%>
<!--<span class="multisearchform" id="BudgetCommessa"></span>--></td>
							<td nowrap width="100px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ScostamentoBudgetCommessa", "DataBudget", null); 
   label.setParent(ScostamentoBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataBudget"><%label.write(out);%></label><%}%></td>
							<td nowrap><% 
  WebTextInput ScostamentoBudgetCommessaDataBudget =  
     new com.thera.thermfw.web.WebTextInput("ScostamentoBudgetCommessa", "DataBudget"); 
  ScostamentoBudgetCommessaDataBudget.setShowCalendarBtn(true); 
  ScostamentoBudgetCommessaDataBudget.setParent(ScostamentoBudgetCommessaForm); 
%>
<input class="<%=ScostamentoBudgetCommessaDataBudget.getClassType()%>" id="<%=ScostamentoBudgetCommessaDataBudget.getId()%>" maxlength="<%=ScostamentoBudgetCommessaDataBudget.getMaxLength()%>" name="<%=ScostamentoBudgetCommessaDataBudget.getName()%>" size="<%=ScostamentoBudgetCommessaDataBudget.getSize()%>"><% 
  ScostamentoBudgetCommessaDataBudget.write(out); 
%>
</td>
						</tr>
						<tr>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ScostamentoBudgetCommessa", "IdCommessaConsuntivo", null); 
   label.setParent(ScostamentoBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="ConsuntivoCommessa"><%label.write(out);%></label><%}%></td>
							<td nowrap><% 
  WebMultiSearchForm ScostamentoBudgetCommessaConsuntivoCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("ScostamentoBudgetCommessa", "ConsuntivoCommessa", false, false, true, 2, null, null); 
  ScostamentoBudgetCommessaConsuntivoCommessa.setParent(ScostamentoBudgetCommessaForm); 
  ScostamentoBudgetCommessaConsuntivoCommessa.setAdditionalRestrictConditions("IdCommessa, IdCommessa"); 
  ScostamentoBudgetCommessaConsuntivoCommessa.write(out); 
%>
<!--<span class="multisearchform" id="ConsuntivoCommessa"></span>--></td>
							<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "ScostamentoBudgetCommessa", "DataConsuntivo", null); 
   label.setParent(ScostamentoBudgetCommessaForm); 
%><label class="<%=label.getClassType()%>" for="DataConsuntivo"><%label.write(out);%></label><%}%></td>
							<td nowrap><% 
  WebTextInput ScostamentoBudgetCommessaDataConsuntivo =  
     new com.thera.thermfw.web.WebTextInput("ScostamentoBudgetCommessa", "DataConsuntivo"); 
  ScostamentoBudgetCommessaDataConsuntivo.setShowCalendarBtn(true); 
  ScostamentoBudgetCommessaDataConsuntivo.setParent(ScostamentoBudgetCommessaForm); 
%>
<input class="<%=ScostamentoBudgetCommessaDataConsuntivo.getClassType()%>" id="<%=ScostamentoBudgetCommessaDataConsuntivo.getId()%>" maxlength="<%=ScostamentoBudgetCommessaDataConsuntivo.getMaxLength()%>" name="<%=ScostamentoBudgetCommessaDataConsuntivo.getName()%>" size="<%=ScostamentoBudgetCommessaDataConsuntivo.getSize()%>"><% 
  ScostamentoBudgetCommessaDataConsuntivo.write(out); 
%>
</td>
						</tr>
						<tr>			
							<td colspan="4" height="25px" valign="top">
								<table style="margin-left: 5px;">
									<tr>
										<td style="white-space:nowrap;" width="50px"><% 
  WebCheckBox ScostamentoBudgetCommessaTotali =  
     new com.thera.thermfw.web.WebCheckBox("ScostamentoBudgetCommessa", "Totali"); 
  ScostamentoBudgetCommessaTotali.setParent(ScostamentoBudgetCommessaForm); 
%>
<input id="<%=ScostamentoBudgetCommessaTotali.getId()%>" name="<%=ScostamentoBudgetCommessaTotali.getName()%>" type="checkbox" value="Y"><%
  ScostamentoBudgetCommessaTotali.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ScostamentoBudgetCommessaDettagliCommessa =  
     new com.thera.thermfw.web.WebCheckBox("ScostamentoBudgetCommessa", "DettagliCommessa"); 
  ScostamentoBudgetCommessaDettagliCommessa.setParent(ScostamentoBudgetCommessaForm); 
%>
<input id="<%=ScostamentoBudgetCommessaDettagliCommessa.getId()%>" name="<%=ScostamentoBudgetCommessaDettagliCommessa.getName()%>" type="checkbox" value="Y"><%
  ScostamentoBudgetCommessaDettagliCommessa.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ScostamentoBudgetCommessaDettagliSottoCommesse =  
     new com.thera.thermfw.web.WebCheckBox("ScostamentoBudgetCommessa", "DettagliSottoCommesse"); 
  ScostamentoBudgetCommessaDettagliSottoCommesse.setParent(ScostamentoBudgetCommessaForm); 
%>
<input id="<%=ScostamentoBudgetCommessaDettagliSottoCommesse.getId()%>" name="<%=ScostamentoBudgetCommessaDettagliSottoCommesse.getName()%>" type="checkbox" value="Y"><%
  ScostamentoBudgetCommessaDettagliSottoCommesse.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ScostamentoBudgetCommessaComponentiPropri =  
     new com.thera.thermfw.web.WebCheckBox("ScostamentoBudgetCommessa", "ComponentiPropri"); 
  ScostamentoBudgetCommessaComponentiPropri.setParent(ScostamentoBudgetCommessaForm); 
%>
<input id="<%=ScostamentoBudgetCommessaComponentiPropri.getId()%>" name="<%=ScostamentoBudgetCommessaComponentiPropri.getName()%>" type="checkbox" value="Y"><%
  ScostamentoBudgetCommessaComponentiPropri.write(out); 
%>
</td>
										<td style="white-space:nowrap;" width="90px"><% 
  WebCheckBox ScostamentoBudgetCommessaSoloComponentiValorizzate =  
     new com.thera.thermfw.web.WebCheckBox("ScostamentoBudgetCommessa", "SoloComponentiValorizzate"); 
  ScostamentoBudgetCommessaSoloComponentiValorizzate.setParent(ScostamentoBudgetCommessaForm); 
%>
<input id="<%=ScostamentoBudgetCommessaSoloComponentiValorizzate.getId()%>" name="<%=ScostamentoBudgetCommessaSoloComponentiValorizzate.getName()%>" type="checkbox" value="Y"><%
  ScostamentoBudgetCommessaSoloComponentiValorizzate.write(out); 
%>
</td>
										<td>
											<button id="thApplicaScelte" name="thApplicaScelte" onclick="carica()" style="width:26px;height:26px;align:top" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa", "ApplicaScelte")%>" type="button"><img border="0" height="16px" src="thermweb/image/gui/cnr/Run.gif" width="16px">
											<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa", "ApplicaScelteID")%></button>
										</td>											
										<td><% 
  MDVButton newMDV =  
   new com.thera.thermfw.web.MDVButton("SaveScreenData", "com.thera.thermfw.web.resources.web", "SaveScreenData", "thermweb/image/gui/SaveScreenData.gif", null, null); 
  newMDV.setParent(ScostamentoBudgetCommessaForm); 
  newMDV.setImageWidth("16"); 
  newMDV.setImageHeight("16"); 
  newMDV.write(out); 
%>
<!--<span class="mdvbutton" id="newMDV" name="newMDV"></span>--></td>
										<!-- 36252 inizio -->
										<td>
											<button id="thStampaRiepCommessa" name="thStampaRiepCommessa" onclick="stampaRiepCommessa()" style="width:26px;height:26px;align:top" title="<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa", "StampaRiepCommessa")%>" type="button"><img border="0" height="16px" src="thermweb/image/gui/cnr/DocumentPreview.gif" width="16px">
											<%= ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa", "StampaRiepCommessaID")%></button>
										</td>	
										<!-- 36252 fine -->									
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<iframe height="100%" id="ScostamentoBudgetCommessaDettaglio" name="ScostamentoBudgetCommessaDettaglio" src style="border: 0px solid black;margin:0 5px 10px 5px;height: calc(100% - 15px);width: calc(100% - 10px);" width="100%"></iframe>
				</td>
			</tr>
			<tr><td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(ScostamentoBudgetCommessaForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td></tr>			
		</table>
	<%
  ScostamentoBudgetCommessaForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = ScostamentoBudgetCommessaForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", ScostamentoBudgetCommessaBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              ScostamentoBudgetCommessaForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, ScostamentoBudgetCommessaBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, ScostamentoBudgetCommessaBODC.getErrorList().getErrors()); 
           if(ScostamentoBudgetCommessaBODC.getConflict() != null) 
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
     if(ScostamentoBudgetCommessaBODC != null && !ScostamentoBudgetCommessaBODC.close(false)) 
        errors.addAll(0, ScostamentoBudgetCommessaBODC.getErrorList().getErrors()); 
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
     String errorPage = ScostamentoBudgetCommessaForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", ScostamentoBudgetCommessaBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = ScostamentoBudgetCommessaForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>

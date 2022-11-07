var gridEditable = true;

function BudgetCommessaOL(){
	redefineWriteTargetNew();
	var statoAvanzamento = document.getElementById("StatoAvanzamento").value;	
	if(statoAvanzamento == '2')//DEFENITIVO
		gridEditable = false;
    caricaDati();
}

function caricaDati(){
	var mode = document.getElementById("thMode").value;
	var key = URLEncode(document.getElementById("thKey").value);	
	var statoAvanzamento = document.getElementById("StatoAvanzamento").value;	
	var dataRiferimento =  URLEncode(document.getElementById("DataRiferimento").value);
	var idAzienda =  URLEncode(document.getElementById("IdAzienda").value);
	var idCommessa =  URLEncode(document.getElementById("IdCommessa").value);
	var idComponenteTotali =  URLEncode(document.getElementById("IdComponenteTotali").value);
	var totali =  document.getElementById("Totali").checked;
	var dettagliCommessa =  document.getElementById("DettagliCommessa").checked;
	var dettagliSottoCommesse =  document.getElementById("DettagliSottoCommesse").checked;
	var componentiPropri =  document.getElementById("ComponentiPropri").checked;
	var soloComponentiValorizzate =  document.getElementById("SoloComponentiValorizzate").checked;
	
	var url = "it/thera/thip/produzione/commessa/BudgetCommessaDettaglioPerCommessa.jsp?";
	var params = "mode=" + mode + "&key=" + key + "&myAction=CARICA"+ "&IdAzienda=" + idAzienda 
			  + "&IdCommessa=" + idCommessa  + "&IdComponenteTotali=" + idComponenteTotali 
			  + "&DataRiferimento=" + dataRiferimento + "&StatoAvanzamento=" + statoAvanzamento;
	params += "&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse+ 
			  "&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;

	url += params;
	document.getElementById("BudgetCommessaDettaglio").src=url;
	
	//var wListForm =  document.getElementById("WinListForm");
	//showForm(wListForm, false);
}

function completaDatiBudget(){
	var iIdCommessa = eval("document.forms[0]." + idFromName['IdCommessa']).value;
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.CompletaDatiBudget?";
	url = url + "Action=COMPLETA_DATI&";
	url = url + "IdCommessa=" + URLEncode(iIdCommessa);
	setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow,url);
}

function createBuilding(daAggiornaBudget){
	var wListFrame;
	var wListForm;
	if(daAggiornaBudget){
		 wListFrame =  parent.document.getElementById("winListFrame");		
		 wListForm =  parent.document.getElementById("WinListForm");	
	}
	else{
		 wListFrame =  document.getElementById("winListFrame");		
		 wListForm =  document.getElementById("WinListForm");	
	}

	winName = "newWind" + Math.round(Math.random() * 1000000);
    winFeature = "width=140, height=160, scrollbars=no, toolbar=no, status=no, menubar=no";
    var leftPosition = window.screen.width/2 - 70;
    var topPosition = window.screen.height/2 - 80;
    if(navigator.appName=="Microsoft Internet Explorer")
      winFeature += ", left="+leftPosition+", top="+topPosition;
    else
      winFeature += ", screenX="+leftPosition+", screenY="+topPosition;

	var htmlTxt = getHTMLWinText();
	
	wListFrame.contentWindow.document.write(htmlTxt);
	showForm(wListForm, true);
}

function showForm(el, show) {
	if(el == null)
		return;

	var isShow = (el.style.display != parent.displayNone);
	if(show) {
		if(!isShow)
			el.style.display = parent.displayBlock;
	}
	else {
		if(isShow)
			el.style.display = parent.displayNone;
	}
}

function getHTMLWinText() {
   var swt = "<html><head>";
   swt += "</head>";
   swt += "<body leftmargin=\"0\" rightmargin=\"0\" topmargin=\"0\" bottommargin=\"0\">";
   swt += "<table width=\"100%\" height=\"100%\" border=\"0\" align=\"center\" valign=\"middle\" style=\"background-color: #ffffff82;\">";
   swt += "<tr align=\"center\" valign=\"middle\" width=\"100%\" height=\"100%\">";
   swt += "<td align=\"center\" valign=\"middle\" width=\"100%\" height=\"100%\">";
   var pathImage = "/"+webAppPath+"/it/thera/thip/produzione/pianifica/images/Loading.gif";
   swt += "<img align=\"middle\" src=\" "+pathImage+"\">";
   swt +="</td>";
   swt += "</tr>";
   swt += "</table>";
   swt +="</body>";
   swt +="</html>";

   return swt;
}

function caricaPreventivo(){
	var mode = document.getElementById("thMode").value;
	if(mode == "NEW"){
		runActionDirect('SAVE', 'action_submit', document.forms[0].thClassName, null, 'errorsFrame', 'no');				
	}
	else{
		var prosegui = true;
		var idAnnoPreventivo =  URLEncode(document.getElementById("IdAnnoPreventivo").value);
		var idNumeroPreventivo =  URLEncode(document.getElementById("IdNumeroPreventivo").value);
		if(idAnnoPreventivo == "" || idNumeroPreventivo == ""){
			alert(errCarica1);
		}
		else if(isSingoloProovisorio && idAnnoPreventivo != "" && idNumeroPreventivo != ""){
			prosegui = confirm(confirm1 + "\n" + confirm2);
			if(prosegui)
			{
				var idAnnoPreventivo =  URLEncode(document.getElementById("IdAnnoPreventivo").value);
				var idNumeroPreventivo =  URLEncode(document.getElementById("IdNumeroPreventivo").value);
				var objectKey = URLEncode(document.getElementById("thKey").value);
				var statoAvanzamento = URLEncode(document.getElementById("StatoAvanzamento").value);	
				var dataRiferimento =  URLEncode(document.getElementById("DataRiferimento").value);
				var idAzienda =  URLEncode(document.getElementById("IdAzienda").value);
				var idCommessa = URLEncode( document.getElementById("IdCommessa").value);				
				var idComponenteTotali =  URLEncode(document.getElementById("IdComponenteTotali").value);
				var totali =  document.getElementById("Totali").checked;
				var dettagliCommessa =  document.getElementById("DettagliCommessa").checked;
				var dettagliSottoCommesse =  document.getElementById("DettagliSottoCommesse").checked;
				var componentiPropri =  document.getElementById("ComponentiPropri").checked;
				var soloComponentiValorizzate =  document.getElementById("SoloComponentiValorizzate").checked;
				if(idAnnoPreventivo != "" && idNumeroPreventivo != "")
				{
					var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?";
					var params = "key=" + objectKey + "&myAction=CARICA_PREVENTIVO" + "&IdAzienda=" + idAzienda + "&IdCommessa=" + idCommessa  
						+ "&IdAnnoPreventivo=" + idAnnoPreventivo + "&IdNumeroPreventivo=" + idNumeroPreventivo  
						+ "&IdComponenteTotali=" + idComponenteTotali + "&DataRiferimento=" + dataRiferimento 
						+ "&StatoAvanzamento=" + statoAvanzamento + "&PerCommessa=false";
					params += "&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse+ 
						"&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;
					url += params;
					createBuilding(false);
					setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow,url);					
				}
			}
		}	
	}
}


function columnOreEditable(ui){
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y" && rowData['CompDet'] == "Y"){
		if(rowData['CmpATempo'] == "Y")
			return true;
		
	}
	return false;
}

function columnValoreEditable(ui){	
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y" && rowData['CompDet'] == "Y"){
		return true;
		
	}
	return false;
}

function cellSaveBudget(event, ui){
	var objectKey = parent.document.getElementById("thKey").value;
	var budgetDetKey = ui.updateList[0].rowData['BudgetDetKey'];
	var newRowwHH = ui.updateList[0].newRow['BudHH'];
	var oldRowwHH = ui.updateList[0].oldRow['BudHH'];	
	var newRowwVal = ui.updateList[0].newRow['BudVal'];
	var oldRowwVal = ui.updateList[0].oldRow['BudVal'];
	var updatedOre = false;
	var oldValue=oldRowwVal;
	var newValue=newRowwVal;
	if(newRowwHH != undefined){
		updatedOre = true;
		oldValue = oldRowwHH;
		newValue = newRowwHH;
	}
	
	var params = "&PerCommessa=true" + "&key=" + objectKey + "&BudgetDetKey=" + budgetDetKey + "&OldValue=" + oldValue + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre;
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?myAction=AGGIORNA_BUDGET";
	url += params;
	if(parent.parent.document.getElementById('errorsFrameCommessa') != null && (objectKey.split(top.fsep).length == 3) ){
		createBuilding(true);
		parent.setLocationOnWindow(parent.parent.document.getElementById('errorsFrameCommessa').contentWindow,url);
	}
}

function aggiornaBudgetView(){
	var objectKey = document.getElementById("thKey").value;

	var totali = document.getElementById("Totali").checked;
	var dettagliCommessa = document.getElementById("DettagliCommessa").checked;
	var dettagliSottoCommesse = document.getElementById("DettagliSottoCommesse").checked;
	var componentiPropri = document.getElementById("ComponentiPropri").checked;
	var soloComponentiValorizzate = document.getElementById("SoloComponentiValorizzate").checked;
	
	var statoAvanzamento = document.getElementById("StatoAvanzamento").value;	
	var dataRiferimento =  document.getElementById("DataRiferimento").value;
	var idAzienda =  document.getElementById("IdAzienda").value;
	var idCommessa =  document.getElementById("IdCommessa").value;
	var idComponenteTotali =  document.getElementById("IdComponenteTotali").value;
	var idAnnoPreventivo =  URLEncode(document.getElementById("IdAnnoPreventivo").value);
	var idNumeroPreventivo =  URLEncode(document.getElementById("IdNumeroPreventivo").value);
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?";
	
	url = url + "key=" + objectKey + "&myAction=AGGIORNA" + "&IdAzienda=" + idAzienda + "&IdCommessa=" + idCommessa  + "&DataRiferimento=" + dataRiferimento 
	          + "&IdAnnoPreventivo=" + idAnnoPreventivo + "&IdNumeroPreventivo=" + idNumeroPreventivo  
			  + "&Totali=" + totali + "&DettagliCommessa=" + dettagliCommessa + "&DettagliSottoCommesse=" +  dettagliSottoCommesse
			  + "&ComponentiPropri=" + componentiPropri + "&SoloComponentiValorizzate=" +  soloComponentiValorizzate
			  + "&StatoAvanzamento=" + statoAvanzamento + "&PerCommessa=true";
	createBuilding();
	setLocationOnWindow(parent.document.getElementById(parent.errorsFrameName).contentWindow,url);
}

function clearErrors(){
	var errViewObj = parent.eval(parent.errorsViewName);
	errViewObj.clearDisplay();	
}

function fillCampo(nomeCampo, valore){
	var campo = document.getElementById(nomeCampo);
	if(campo != null)
		campo.value = valore;
}

//35382 inizio
function aggiornaBudget(columnDesc, data, isATempo, costoGenerale, costoIndustriale, costoPrimo, costoRiferimento, tsAgg){
	var gui = top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'];
	gui.clearErrors();	
	var wListForm =  gui.document.getElementById('WinListForm');
	showForm(wListForm, false);
	
	var grid = gui.frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid("instance");
    grid.option("colModel", columnDesc);
    grid.option("dataModel.data", data);
	if(isATempo) {
		grid.colModel[1].editable=parent.columnOreEditable;
		grid.colModel[2].editable=parent.columnValoreEditable;	
	}
	else {
		grid.colModel[1].editable=parent.columnValoreEditable;
	}

	grid.on('change', parent.cellSaveBudget);
    grid.refreshDataAndView();    
	

	parent.document.getElementById('CostoGenerale').value = costoGenerale;
	parent.document.getElementById('CostoIndustriale').value=costoIndustriale;
	parent.document.getElementById('CostoPrimo').value = costoPrimo;
	parent.document.getElementById('CostoRiferimento').value = costoRiferimento;
	parent.document.getElementById('thTimestamp').value = tsAgg;
}

function redefineWriteTargetNew(){
	oldWriteTargetNew = writeTargetNew;
    writeTargetNew = function (action, type, classhdr, key, toolbar) {
    	if(action == "VARIA_BUDGET_COMMESSA") {
        	document.forms[0].thTarget.value="new";
            createTargetWin("yes",toolbar,"1200","800");
        }
        else
	        oldWriteTargetNew(action, type, classhdr, key, toolbar);
    }
}

function salvaBudget(){
	var key = document.getElementById('thKey').value;
	runActionDirect('SAVE','action_submit','BudgetCommessa',key,'errorsFrameCommessa','no');
}

/*
function aggiornaBudget(columnDesc, data, isATempo, costoGenerale, costoIndustriale, costoPrimo, costoRiferimento, tsAgg){
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].clearErrors();	
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('instance').option('colModel', columnDesc);
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('instance').option('dataModel.data', data);//A verifier
	if(isATempo) {
		top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('instance').colModel[1].editable=top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].columnOreEditable;
		top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('instance').colModel[2].editable=top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].columnValoreEditable;	
	}
	else {
		top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('instance').colModel[1].editable=top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].columnValoreEditable;
	}
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('instance').on('change', top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].cellSaveBudget);
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('refresh');
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].frames['BudgetCommessaDettaglio'].$grid_BudgetCommessaDet.pqGrid('refreshDataAndView');
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].document.getElementById('CostoGenerale').value = costoGenerale;
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].document.getElementById('CostoIndustriale').value=costoIndustriale;
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].document.getElementById('CostoPrimo').value = costoPrimo;
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].document.getElementById('CostoRiferimento').value = costoRiferimento;
	top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].document.getElementById('thTimestamp').value = tsAgg;
}
*/
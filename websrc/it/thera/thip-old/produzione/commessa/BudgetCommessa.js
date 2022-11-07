var gridEditable = true;

var isPerCommessa = false;

function BudgetCommessaOL(){
	
	//redefineWriteTargetNew();
	redefineCallUpdateGridForSave();
	var statoAvanzamento = document.getElementById("StatoAvanzamento").value;	
	if(statoAvanzamento == '2')  //DEFENITIVO
		gridEditable = false;
    caricaDati();
}

/*
function redefineWriteTargetNew()
{
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
*/

function redefineCallUpdateGridForSave()
{
	if(!isPerCommessa)
	{
	    oldCallUpdateGridForSave = callUpdateGridForSave;
		callUpdateGridForSave = function (destination, objectKey) {
			if(openerCommessa != undefined && openerCommessa == true){
				key = destination.thKey.value;
				window.opener.top.frames['tree'].frames['detail'].runActionDirect('REFRESH','action_submit','Commessa',key,'same','no');
			}
			else{
				oldCallUpdateGridForSave(destination, objectKey);
			}
		}
	}
}

function getParams(action, fromTree)
{
	var gui = document;
	if(fromTree)
		gui = parent.document;
	
	var mode = gui.getElementById("thMode").value;
	var idAnnoPreventivo =  URLEncode(gui.getElementById("IdAnnoPreventivo").value);
	var idNumeroPreventivo =  URLEncode(gui.getElementById("IdNumeroPreventivo").value);
	var key = URLEncode(gui.getElementById("thKey").value);
	var statoAvanzamento = URLEncode(gui.getElementById("StatoAvanzamento").value);	
	var dataRiferimento =  URLEncode(gui.getElementById("DataRiferimento").value);
	var idAzienda =  URLEncode(gui.getElementById("IdAzienda").value);
	var idCommessa = URLEncode( gui.getElementById("IdCommessa").value);				
	var idComponenteTotali =  URLEncode(gui.getElementById("IdComponenteTotali").value);
	var totali =  gui.getElementById("Totali").checked;
	var dettagliCommessa =  gui.getElementById("DettagliCommessa").checked;
	var dettagliSottoCommesse =  gui.getElementById("DettagliSottoCommesse").checked;
	var componentiPropri =  gui.getElementById("ComponentiPropri").checked;
	var soloComponentiValorizzate =  gui.getElementById("SoloComponentiValorizzate").checked;
	
	var params = "mode=" + mode + "&key=" + key + "&myAction=" + action + "&IdAzienda=" + idAzienda 
				+ "&IdCommessa=" + idCommessa  + "&IdComponenteTotali=" + idComponenteTotali 
				+ "&IdAnnoPreventivo=" + idAnnoPreventivo + "&IdNumeroPreventivo=" + idNumeroPreventivo  
			  	+ "&DataRiferimento=" + dataRiferimento + "&StatoAvanzamento=" + statoAvanzamento 
	            +"&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse 
				+"&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;
	
    var testCommessa = isPerCommessa;
    if(fromTree)	
		testCommessa = parent.isPerCommessa;
	
	if(testCommessa)
		params += "&PerCommessa=true";
	else
		params += "&PerCommessa=false";
	
	return params;
}

function setLocationForAction(url, fromTree)
{
	var testCommessa = isPerCommessa;
    if(fromTree)	
		testCommessa = parent.isPerCommessa;
	
	if(!testCommessa)
	{
		if(fromTree)
			parent.setLocationOnWindow(parent.document.getElementById(parent.errorsFrameName).contentWindow, url);	
	    else
		    setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url);	
	}
	else
	{
		if(fromTree)
		{
			var objectKey = parent.document.getElementById("thKey").value;
			if(parent.parent.document.getElementById('errorsFrameCommessa') != null && (objectKey.split(top.fsep).length == 3) )
				parent.setLocationOnWindow(parent.parent.document.getElementById('errorsFrameCommessa').contentWindow,url);
		}
	   else
	    {
			var objectKey = document.getElementById("thKey").value;
			if(parent.document.getElementById('errorsFrameCommessa') != null && (objectKey.split(top.fsep).length == 3) )
				setLocationOnWindow(parent.document.getElementById('errorsFrameCommessa').contentWindow,url);
		}
	}
}



function caricaDati()
{
	var params = getParams("CARICA", false);	
	var url = "it/thera/thip/produzione/commessa/BudgetCommessaDettaglio.jsp?";
    if(isPerCommessa)	
		url = "it/thera/thip/produzione/commessa/BudgetCommessaDettaglioPerCommessa.jsp?";
	
	url += params;
	document.getElementById("BudgetCommessaDettaglio").src=url;
	
	var wListForm =  document.getElementById("WinListForm");
	showForm(wListForm, false);
}


function salvaBudget(){
	
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?";
    var params = getParams("SALVA_BUDGET");
	url += params;
	createBuilding(false);
	setLocationForAction(url, false);
}


function completaDatiBudget(){
	var iIdCommessa = eval("document.forms[0]." + idFromName['IdCommessa']).value;
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.CompletaDatiBudget?";
	url = url + "Action=COMPLETA_DATI&";
	url = url + "IdCommessa=" + URLEncode(iIdCommessa);
	setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow,url);
}


function createBuilding(daAggiornaBudget){
	/*
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
	
	*/
}

function showForm(el, show) {
	/*
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
	*/
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
	else
	{
		var prosegui = true;
		var idAnnoPreventivo =  URLEncode(document.getElementById("IdAnnoPreventivo").value);
		var idNumeroPreventivo =  URLEncode(document.getElementById("IdNumeroPreventivo").value);
		if(idAnnoPreventivo == "" || idNumeroPreventivo == ""){
			alert(errCarica1);
		}
		else if(isSingoloProovisorio && idAnnoPreventivo != "" && idNumeroPreventivo != "")
		{
			prosegui = confirm(confirm1 + "\n" + confirm2);
			if(prosegui)
			{
				if(idAnnoPreventivo != "" && idNumeroPreventivo != "")
				{
					var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?";
					var params = getParams("CARICA_PREVENTIVO");
					url += params;
					createBuilding(false);
					setLocationForAction(url, false);
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

function editableColumnOreCellRender(ui){
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y"){
		if(rowData['CmpATempo'] == "Y")
		{
			return {style: 'background-color: rgb(255, 255, 234);'}
		}
	}
	//return {style: 'background-color: #FFFFFF'};
}

function editableColumnValCellRender(ui){
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y"){
		return {style: 'background-color: rgb(255, 255, 234);'}
	}
	//return {style: 'background-color: #FFFFFF'};
}


var editEnding = false;

function cellSaveBudget(event, ui){
	
	if(!parent.editEnding)
	{
		parent.editEnding = true;
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
		
		var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?";
		var treeParams = "&BudgetDetKey=" + budgetDetKey + "&OldValue=" + oldValue + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre;	
		var params = getParams("AGGIORNA_BUDGET", true) + treeParams;
		url += params;
		createBuilding(true);
		setLocationForAction(url, true);
	}
	
}

function cellSave(event, ui){
	
	if(!parent.editEnding)
	{
		parent.editEnding = true;
		var budgetDetKey = ui.rowData['BudgetDetKey'];
		var newRowwHH = ui.newRow['BudHH'];
		var oldRowwHH = ui.oldRow['BudHH'];	
		var newRowwVal = ui.newRow['BudVal'];
		var oldRowwVal = ui.oldRow['BudVal'];
		var updatedOre = false;
		var oldValue=oldRowwVal;
		var newValue=newRowwVal;
		if(newRowwHH != undefined){
			updatedOre = true;
			oldValue = oldRowwHH;
			newValue = newRowwHH;
		}
		
		var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?";
		var treeParams = "&BudgetDetKey=" + budgetDetKey + "&OldValue=" + oldValue + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre;	
		var params = getParams("AGGIORNA_BUDGET", true) + treeParams;
		url += params;
		createBuilding(true);
		setLocationForAction(url, true);
	}
	
}

function aggiornaBudgetView(){
	var mode = document.getElementById("thMode").value;
	if(mode == "NEW"){
		runActionDirect('SAVE', 'action_submit', document.forms[0].thClassName, null, 'errorsFrame', 'no');				
	}
	else{
			var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.BudgetCommessaDetServlet?";
		var params = getParams("AGGIORNA");
		url += params;
		createBuilding(false);
		setLocationForAction(url, false);
	}
}

function clearErrors(){
	var errViewObj = eval(errorsViewName);
	errViewObj.clearDisplay();	
}

function fillCampo(nomeCampo, valore){
	var campo = document.getElementById(nomeCampo);
	if(campo != null)
		campo.value = valore;
}

function aggiornaBudget(columnDesc, data, isATempo, costoGenerale, costoIndustriale, costoPrimo, costoRiferimento, tsAgg){
	
	var gui = parent;
	if(isPerCommessa)
		gui = top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'];
	
	if(!isPerCommessa)
		gui.clearErrors();	
	var wListForm =  gui.document.getElementById('WinListForm');
	gui.showForm(wListForm, false);
	
	var grid = gui.frames["BudgetCommessaDettaglio"].$grid_BudgetCommessaDet.pqGrid("instance");
    grid.option("colModel", columnDesc);
    grid.option("dataModel.data", data);
	if(isATempo) {
		grid.colModel[1].editable=gui.columnOreEditable;
		grid.colModel[2].editable=gui.columnValoreEditable;	

		grid.colModel[1].render=gui.editableColumnOreCellRender;
		grid.colModel[2].render=gui.editableColumnValCellRender;	
	}
	else {
		grid.colModel[1].editable=gui.columnValoreEditable;
		grid.colModel[1].render=gui.editableColumnValCellRender;	
	}
	
 
    grid.refreshDataAndView();    

	gui.document.getElementById('CostoGenerale').value = costoGenerale;
	gui.document.getElementById('CostoIndustriale').value=costoIndustriale;
	gui.document.getElementById('CostoPrimo').value = costoPrimo;
	gui.document.getElementById('CostoRiferimento').value = costoRiferimento;
	gui.document.getElementById('thTimestamp').value = tsAgg;
	gui.editEnding = false;
}


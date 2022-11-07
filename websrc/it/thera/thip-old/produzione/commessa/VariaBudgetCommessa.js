var gridEditable = true;
function VariaBudgetCommessaOL(){
	//resizeTo(1200,800);
	redefineRunActionDirect();
	var statoAvanzamento = document.getElementById("StatoAvanzamento").value;	
	if(statoAvanzamento == '2')//DEFENITIVO
		gridEditable = false;
	document.getElementById("IdCommessaBudget").style.display=displayNone;
	document.getElementById("IdCommessaConsuntivo").style.display=displayNone;
    caricaDati();
}

function redefineRunActionDirect(){
	oldRunActionDirect = runActionDirect;
	runActionDirect = function (action, type, classhdr, key, target, toolbar) {
		document.getElementById("IdCommessa").disabled=false;
		oldRunActionDirect(action, type, classhdr, key, target, toolbar);
		document.getElementById("IdCommessa").disabled=true;		
	}
}

function getParams(action, fromTree)
{
	var gui = document;
	if(fromTree)
		gui = parent.document;
	
	var mode = gui.getElementById("thMode").value;
	var key = URLEncode(gui.getElementById("thKey").value);
	var statoAvanzamento = URLEncode(gui.getElementById("StatoAvanzamento").value);	
	var dataRiferimento =  URLEncode(gui.getElementById("DataRiferimento").value);
	var idAzienda =  URLEncode(gui.getElementById("IdAzienda").value);
	var idCommessa = URLEncode( gui.getElementById("IdCommessa").value);	
	var idBudget =  URLEncode(gui.getElementById("IdBudget").value);
	var idConsuntivo =  URLEncode(gui.getElementById("IdConsuntivo").value);	
	var totali =  gui.getElementById("Totali").checked;
	var dettagliCommessa =  gui.getElementById("DettagliCommessa").checked;
	var dettagliSottoCommesse =  gui.getElementById("DettagliSottoCommesse").checked;
	var componentiPropri =  gui.getElementById("ComponentiPropri").checked;
	var soloComponentiValorizzate =  gui.getElementById("SoloComponentiValorizzate").checked;
	
	var params = "mode=" + mode + "&key=" + key + "&myAction=" + action + "&IdAzienda=" + idAzienda 
				+ "&IdCommessa=" + idCommessa  + "&IdBudget=" + idBudget + "&IdConsuntivo=" + idConsuntivo + 
				+ "&DataRiferimento=" + dataRiferimento + "&StatoAvanzamento=" + statoAvanzamento 
	            +"&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse 
				+"&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;
	
	return params;
}

function setLocationForAction(url, fromTree)
{
	if(fromTree)
		parent.setLocationOnWindow(parent.document.getElementById(parent.errorsFrameName).contentWindow, url);	
    else
	    setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url);	
}

function caricaDati(){
	/*
	var mode = document.getElementById("thMode").value;
	var key = URLEncode(document.getElementById("thKey").value);
	var statoAvanzamento = document.getElementById("StatoAvanzamento").value;	
	var dataRiferimento =  URLEncode(document.getElementById("DataRiferimento").value);
	var idAzienda =  URLEncode(document.getElementById("IdAzienda").value);
	var idCommessa =  URLEncode(document.getElementById("IdCommessa").value);
	var idBudget =  URLEncode(document.getElementById("IdBudget").value);
	var idConsuntivo =  URLEncode(document.getElementById("IdConsuntivo").value);
	var totali =  document.getElementById("Totali").checked;
	var dettagliCommessa =  document.getElementById("DettagliCommessa").checked;
	var dettagliSottoCommesse =  document.getElementById("DettagliSottoCommesse").checked;
	var componentiPropri =  document.getElementById("ComponentiPropri").checked;
	var soloComponentiValorizzate =  document.getElementById("SoloComponentiValorizzate").checked;
	var url = "it/thera/thip/produzione/commessa/VariaBudgetCommessaDettaglio.jsp?";
	var params = "mode=" + mode + "&key=" + key + "&myAction=CARICA"+ "&IdAzienda=" + idAzienda + 
				 "&IdCommessa=" + idCommessa + "&IdBudget=" + idBudget + "&IdConsuntivo=" + idConsuntivo +
			     "&DataRiferimento=" + dataRiferimento + "&StatoAvanzamento=" + statoAvanzamento;
	params += "&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse+ 
			  "&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;
	url += params;
	*/
	var params = getParams("CARICA", false);
	var url = "it/thera/thip/produzione/commessa/VariaBudgetCommessaDettaglio.jsp?";
	url += params;
	document.getElementById("VariaBudgetCommessaDettaglio").src=url;
	
	var wListForm =  document.getElementById("WinListForm");
	showForm(wListForm, false);
}

function columnOreEditable(ui){
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y"){
		if(rowData['CmpATempo'] == "Y")
			return true;
	}
	return false;
}

function columnValoreEditable(ui){	
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y"){
		return true;
	}
	return false;
}

var editEnding = false;

function cellSaveVariaBudget(event, ui){
		
	if(!parent.editEnding)
	{
		parent.editEnding = true;
		var updatedOre = false;
		var updatedNote = false;
		var checkUpdate = true;
		var objectKey = parent.document.getElementById("thKey").value;
		var variaBudgetDetKey = ui.updateList[0].rowData['VariaBudgetDetKey'];
		var newRowNote = ui.updateList[0].newRow['Not'];
		var oldRowNote = ui.updateList[0].oldRow['Not'];
		if(newRowNote != undefined){
			var oldValue=oldRowNote;
			var newValue=newRowNote;
			updatedNote=true;
		}
		else{
			var newRowHH = ui.updateList[0].newRow['VarHH'];
			var oldRowHH = ui.updateList[0].oldRow['VarHH'];	
			var newRowVal = ui.updateList[0].newRow['VarVal'];
			var oldRowVal = ui.updateList[0].oldRow['VarVal'];
		
			var oldValue=oldRowVal;
			var newValue=newRowVal;
			if(newRowHH != undefined){
				updatedOre = true;
				oldValue = oldRowHH;
				newValue = newRowHH;
				var rd = ui.updateList[0].rowData;
				if(rd.BudHH != '' && newValue != ''){
					var variazione = parseFloat(rd.BudHH) + parseFloat(newValue);
					if(variazione < 0){
						checkUpdate = false;
						alert(parent.ErrVariazione);
					}
				}
			}
			else{
				var rd = ui.updateList[0].rowData;
				if(rd.BudVal != '' && newValue != ''){
					var variazione = parseFloat(rd.BudVal) + parseFloat(newValue);
					if(variazione < 0){
						checkUpdate = false;
						alert(parent.ErrVariazione);
					}
				}			
			}
		}
		
		if(checkUpdate)
		{
			var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.VariaBudgetCommessaDetServlet?";
			var treeParams = "&VariaBudgetDetKey=" + variaBudgetDetKey + "&OldValue=" + oldValue + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre + "&UpdatedNote=" + updatedNote;
			var params = getParams("AGGIORNA_VARIABUDGET", true) + treeParams;
			url += params;
			createBuilding(true);
			setLocationForAction(url, true);
			
			/*
			var params = "&key=" + objectKey + "&VariaBudgetDetKey=" + variaBudgetDetKey + "&OldValue=" + oldValue + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre + "&UpdatedNote=" + updatedNote;
			var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.VariaBudgetCommessaDetServlet?myAction=AGGIORNA_VARIABUDGET";
			url += params;
			parent.setLocationOnWindow(parent.document.getElementById(parent.errorsFrameName).contentWindow,url);
			*/
		}	
	}
	

}

function cellSaveAction(event, ui){
		
	if(!parent.editEnding)
	{
		parent.editEnding = true;
		var updatedOre = false;
		var updatedNote = false;
		var checkUpdate = true;
		var objectKey = parent.document.getElementById("thKey").value;
		var variaBudgetDetKey = ui.rowData['VariaBudgetDetKey'];
		
		var newValue = ui.newVal;
		var compareValue = undefined;
		if(ui.dataIndx == 'Not')
			updatedNote=true;
		else if(ui.dataIndx == 'VarHH')
		{
			updatedOre=true;
			compareValue = ui.rowData.BudHH;
		}
		else
		{
			compareValue = ui.rowData.BudVal;
		}
		
		if(compareValue != undefined && compareValue != '' && newValue !=  '')
		{
			var variazione = parseFloat(compareValue) + parseFloat(newValue);
			if(variazione < 0){
				checkUpdate = false;
				alert(parent.ErrVariazione);
			}
		}
		
	/*	
		var newRowNote = ui.rowData['Not'];
		var oldRowNote = "";//ui.oldRow['Not'];
		if(newRowNote != undefined){
			var oldValue=oldRowNote;
			var newValue=newRowNote;
			updatedNote=true;
		}
		else{
			var newRowHH = ui.rowData['VarHH'];
			var oldRowHH = "";//ui.oldRow['VarHH'];	
			var newRowVal = ui.rowData['VarVal'];
			var oldRowVal = "";//ui.oldRow['VarVal'];
		
			var oldValue=oldRowVal;
			var newValue=newRowVal;
			if(newRowHH != undefined){
				updatedOre = true;
				oldValue = oldRowHH;
				newValue = newRowHH;
				var rd = ui.rowData;
				if(rd.BudHH != '' && newValue != ''){
					var variazione = parseFloat(rd.BudHH) + parseFloat(newValue);
					if(variazione < 0){
						checkUpdate = false;
						alert(parent.ErrVariazione);
					}
				}
			}
			else{
				var rd = ui.rowData;
				if(rd.BudVal != '' && newValue != ''){
					var variazione = parseFloat(rd.BudVal) + parseFloat(newValue);
					if(variazione < 0){
						checkUpdate = false;
						alert(parent.ErrVariazione);
					}
				}			
			}
		}
		*/
		
		if(checkUpdate)
		{
			var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.VariaBudgetCommessaDetServlet?";
			var treeParams = "&VariaBudgetDetKey=" + variaBudgetDetKey + "&OldValue=" + ui.oldVal + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre + "&UpdatedNote=" + updatedNote;
			//var treeParams = "&VariaBudgetDetKey=" + variaBudgetDetKey + "&OldValue=" + oldValue + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre + "&UpdatedNote=" + updatedNote;
			var params = getParams("AGGIORNA_VARIABUDGET", true) + treeParams;
			url += params;
			createBuilding(true);
			setLocationForAction(url, true);
			
			/*
			var params = "&key=" + objectKey + "&VariaBudgetDetKey=" + variaBudgetDetKey + "&OldValue=" + oldValue + "&NewValue=" + newValue + "&UpdatedOre=" + updatedOre + "&UpdatedNote=" + updatedNote;
			var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.VariaBudgetCommessaDetServlet?myAction=AGGIORNA_VARIABUDGET";
			url += params;
			parent.setLocationOnWindow(parent.document.getElementById(parent.errorsFrameName).contentWindow,url);
			*/
		}	
	}
	

}

function completaDati(){
	var iIdCommessa = eval("document.forms[0]." + idFromName['IdCommessa']).value;
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.CompletaDatiVariaBudgetCommessa?";
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

function nuovoBudgetCellRender(ui){
	var rd = ui.rowData;
	var stile = {style: 'color:black;font-size:10pt'};
	if(rd.varHH != "")
	{
		if(rd.BudHH != rd.NuoHH)
		{
			if(parseFloat(rd.BudHH) < parseFloat(rd.NuoHH))
				stile = {style: 'color:blue;font-size:10pt'};
			else if(parseFloat(rd.BudHH) > parseFloat(rd.NuoHH))
				stile = {style: 'color:red;font-size:10pt'};
		}
	}

	if(rd.varVal != "")
	{
		if(rd.BudVal != rd.NuoVal)
		{
			if(parseFloat(rd.BudVal) < parseFloat(rd.NuoVal))
				stile = {style: 'color:blue;font-size:10pt'};
			else if(parseFloat(rd.BudVal) > parseFloat(rd.NuoVal))
				stile = {style: 'color:red;font-size:10pt'};
		}
	}

    return stile;
	
	/*
	if(rd.BudHH != rd.NuoHH) {
		if(parseFloat(rd.BudHH) < parseFloat(rd.NuoHH)){
	    	return {style: 'color:blue;font-size:10pt'};
		}
	    else{
		  	return {style: 'color:red;font-size:10pt'};
		}
    }
	else if(rd.BudVal != rd.NuoVal) {
		if(parseFloat(rd.BudVal) < parseFloat(rd.NuoVal)){
	    	return {style: 'color:blue;font-size:10pt'};
		}
	    else{
		  	return {style: 'color:red;font-size:10pt'};
		}
    }
   	else {
	  	return {style: 'color:black;font-size:8pt'};
  	}
	*/
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


function columnOreEditable(ui){
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y"){
		if(rowData['CmpATempo'] == "Y")
			return true;
	}
	return false;
}

function columnValoreEditable(ui){	
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y"){
		return true;
	}
	return false;
}


function fillCampo(nomeCampo, valore){
	var campo = document.getElementById(nomeCampo);
	if(campo != null)
		campo.value = valore;
}

function showForm(el, show) {
	if(el == null)
		return;

	var isShow = (el.style.display != displayNone);
	if(show) {
		if(!isShow)
			el.style.display = displayBlock;
	}
	else {
		if(isShow)
			el.style.display = displayNone;
	}
}

function getHTMLWinText()
{
   var swt = "<html><head>";
   swt += "</head>";
   swt += "<body leftmargin=\"0\" rightmargin=\"0\" topmargin=\"0\" bottommargin=\"0\">";
   swt += "<table width=\"100%\" height=\"100%\" border=\"0\" align=\"center\" valign=\"middle\">";
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

function clearErrors(){
	var errViewObj = eval(errorsViewName);
	errViewObj.clearDisplay();	
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

function aggiornaVariaBudgetView(){
	var mode = document.getElementById("thMode").value;
	if(mode == "NEW"){
		runActionDirect('SAVE', 'action_submit', document.forms[0].thClassName, null, 'errorsFrame', 'no');				
	}
	else{
			var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.VariaBudgetCommessaDetServlet?";
		var params = getParams("AGGIORNA");
		url += params;
		createBuilding(false);
		setLocationForAction(url, false);
	}
}

function aggiornaVariaBudget(columnDesc, data, isATempo, costoGenerale, costoIndustriale, costoPrimo, costoRiferimento, tsAgg){
	
	var gui = parent;
		
	gui.clearErrors();	
	var wListForm =  gui.document.getElementById('WinListForm');
	gui.showForm(wListForm, false);
	
	var grid = gui.frames["VariaBudgetCommessaDettaglio"].$grid_VariaBudgetCommessaDet.pqGrid("instance");
    grid.option("colModel", columnDesc);
    grid.option("dataModel.data", data);
						
	/*
	if(isATempo) {
		grid.colModel[1].editable=gui.columnOreEditable;
		grid.colModel[2].editable=gui.columnValoreEditable;	
	}
	else {
		grid.colModel[1].editable=gui.columnValoreEditable;
	}
    */
   //grid.refreshDataAndView();    

}

function aggiungiRenderer(numeroColonna, funzione){
	var gui = parent;
	var grid = gui.frames["VariaBudgetCommessaDettaglio"].$grid_VariaBudgetCommessaDet.pqGrid("instance");
	    grid.colModel[numeroColonna].render = funzione;
	//grid.refreshDataAndView(); 								
}

function aggiungiEditor(numeroColonna, funzione){
	
	var gui = parent;
    var grid = gui.frames["VariaBudgetCommessaDettaglio"].$grid_VariaBudgetCommessaDet.pqGrid("instance");
	grid.colModel[numeroColonna].editable = funzione;
    //grid.refreshDataAndView();    
}

function aggiungiOnChange(){

	var gui = parent;
	var grid = gui.frames["VariaBudgetCommessaDettaglio"].$grid_VariaBudgetCommessaDet.pqGrid("instance");
	//grid.option( "cellSave", parent.cellSaveAction);
	//grid.on("cellSave", cellSaveAction);
    //grid.refreshDataAndView();    
}

function completeRefresh()
{
	var gui = parent;
	var grid = gui.frames["VariaBudgetCommessaDettaglio"].$grid_VariaBudgetCommessaDet.pqGrid("instance");
	grid.refreshDataAndView(); 					
	parent.editEnding = false;
}

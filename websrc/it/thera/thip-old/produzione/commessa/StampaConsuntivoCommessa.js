var filterAContent;
var filterBContent;
var oldTipoStampa;
var init = true;

function StampaConsuntivoCommessaOL(){
	oldTipoStampa = eval("document.forms[0]." + idFromName["TipoStampa"]).value;
	overrideRunActionDirect();
	initFilerContent();
	onTipoStampaModifAction();
	oldTipoStampa = eval("document.forms[0]." + idFromName["TipoStampa"]).value;
	init = false;
	//36252 inizio
	document.getElementById("IdCommessaConsuntivo").style.display=displayNone;
	onUsaConsuntiviAction();
	//36252 fine
}


function redefineEditGridEditRow(){
	oldEditGridEditRow = editGridEditRow;
	editGridEditRow = function (gridClassCD, rowNum, colNum, cell){
		if(gridClassCD == "CondizioniFiltri$ColonneWhere"){
			if(canEditFilterRow())
				oldEditGridEditRow(gridClassCD, rowNum, colNum, cell);
		}
		else{
			oldEditGridEditRow(gridClassCD, rowNum, colNum, cell);
		}
	}
}

function canEditFilterRow(){
	var TipoStampa = eval("document.forms[0]." + idFromName["TipoStampa"]);
	var filtroGrid = eval(editGrid["CondizioniFiltri$ColonneWhere"]);
	var selectedRow = filtroGrid.selectedRow + filtroGrid.firstRow;
	
	if( (TipoStampa.value == '0' || TipoStampa.value == '1')
		&& (selectedRow == getRowIndex("CondizioniFiltri$ColonneWhere", "IdAmbienteCommessa") 
			|| selectedRow == getRowIndex("CondizioniFiltri$ColonneWhere", "IdCommessa"))
		){
		return true;
	}
	else if(TipoStampa.value != '0' 
			&& TipoStampa.value != '1'
			&& selectedRow != getRowIndex("CondizioniFiltri$ColonneWhere", "IdAmbienteCommessa") 
			&& selectedRow != getRowIndex("CondizioniFiltri$ColonneWhere", "IdCommessa")
		){
		return true;
	}
	return false;
}

function getRowIndex(classCD, classAD){
	var grid = editGrid[classCD];
	var rows = grid.rows;
	for(i = 0; i < rows.length; i++){
		if(rows[i].data[6] == classAD)
			return i;
	}
	return -1;
}

function onTipoStampaModifAction(){
	var TipoStampa = eval("document.forms[0]." + idFromName["TipoStampa"]);
	var filtroGrid = eval(editGrid["CondizioniFiltri$ColonneWhere"]);
	var TipoDettaglio = eval("document.forms[0]." + idFromName["TipoDettaglio"]);
	if(TipoStampa.value == '1'){
		TipoDettaglio.disabled = true;
		TipoDettaglio.value = "1";
	}
	else{
		TipoDettaglio.disabled = false;
	}

	if( (TipoStampa.value == '0' || TipoStampa.value == '1') && ( (oldTipoStampa != '0' && oldTipoStampa != '1') || init) ){
			//Fix 12099 --inizio
			//document.all["TR_FILTRO_B"].style.display = "none";
			document.getElementsByTagName('*')["TR_FILTRO_B"].style.display = displayNone;
			//Fix 12099 --fine
			clearFilterRow('B');
	}
	else if( (TipoStampa.value == '2' || TipoStampa.value == '3') && ( (oldTipoStampa != '2' && oldTipoStampa != '3') || init) ){
    	//Fix 12099 --inizio
			//document.all["TR_FILTRO_B"].style.display = "block";
			document.getElementsByTagName('*')["TR_FILTRO_B"].style.display = displayBlock;
			//Fix 12099 --fine
			clearFilterRow('A');
	}
	//Fix 04361 MK inizio
	if(TipoStampa.value == '3'){
		TipoDettaglio.disabled = true;
		TipoDettaglio.value = "0";
	}
	//Fix 04361 MK fine
	oldTipoStampa = TipoStampa.value;
}

function onArticoliRisorseModifAction(){
	var ArticoliRisorse = eval("document.forms[0]." + idFromName["ArticoliRisorse"]);
	var TipoRiga = eval("document.forms[0]." + idFromName["TipoRiga"]);
	var TipoRisorsa = eval("document.forms[0]." + idFromName["TipoRisorsa"]);
	var LivelloRisorsa = eval("document.forms[0]." + idFromName["LivelloRisorsa"]);

	if(ArticoliRisorse.value == '2'){
		TipoRiga.value = '2';
		TipoRiga.disabled = true;
	}
	else{
		TipoRiga.disabled = false;		
	}

	if(ArticoliRisorse.value == '1'){
		TipoRisorsa.value = '-';
		LivelloRisorsa.value = '-';
		TipoRisorsa.disabled = true;
		LivelloRisorsa.disabled = true;
	}
	else{
		TipoRisorsa.disabled = false;
		LivelloRisorsa.disabled = false;
	}
}

function overrideRunActionDirect(){
	var oldRunActionDirect = runActionDirect;

	var TipoDettaglio = eval('document.forms[0].' + idFromName['TipoDettaglio']);
	var LivelloRisorsa = eval('document.forms[0].' + idFromName['LivelloRisorsa']);
	var TipoRisorsa = eval('document.forms[0].' + idFromName['TipoRisorsa']);
	var TipoRiga = eval("document.forms[0]." + idFromName["TipoRiga"]);

	runActionDirect = function a(action, type, classhdr, key, target, toolbar) {
		var oldTipoDettaglioStatus = TipoDettaglio.disabled;
		var oldLivelloRisorsaStatus = LivelloRisorsa.disabled;
		var oldTipoRisorsaStatus = TipoRisorsa.disabled;
		var oldTipoRigaStatus = TipoRiga.disabled;

		TipoDettaglio.disabled = false;
		LivelloRisorsa.disabled = false;
		TipoRisorsa.disabled = false;
		TipoRiga.disabled = false;

		oldRunActionDirect(action, type, classhdr, key, target, toolbar);

		TipoDettaglio.disabled = oldTipoDettaglioStatus;
		LivelloRisorsa.disabled = oldLivelloRisorsaStatus;
		TipoRisorsa.disabled = oldTipoRisorsaStatus;
		TipoRiga.disabled = oldTipoRigaStatus;
	}
}

function clearFilterRow(filterType){
	clearFilter();
	if(filterType == 'A')
		editGrid["CondizioniFiltri$ColonneWhere"].rows = filterAContent;
	else
		editGrid["CondizioniFiltri$ColonneWhere"].rows = filterBContent;

	//...FIX 10082 - Resetto il valore di firstRow
	var curGrid = eval(editGrid["CondizioniFiltri$ColonneWhere"]);
	curGrid.firstRow = 0;

	clearFilter();

	editGridLoadTable("CondizioniFiltri$ColonneWhere");
}

function initFilerContent(){
	filterAContent = editGrid["CondizioniFiltri$ColonneWhere"].rows;
	filterBContent = new Array();
	filterBContent[0] = editGrid["CondizioniFiltri$ColonneWhere"].rows[0];
	filterBContent[1] = editGrid["CondizioniFiltri$ColonneWhere"].rows[1];
}

/***********************************/
function getColIndex(grid, classAd){
	for(i = 0; i < grid.columns.length; i++){
		if(grid.columns[i].classAD == classAd)
			return i;
	}
	return -1;
}

function clearFilter(){
	var grid = eval(editGrid["CondizioniFiltri$ColonneWhere"]);
	for(ridx = 0; ridx < grid.rows.length; ridx++){
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "From", "");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "FromRel", "");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "To.Descrizione.Descrizione", "");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "To", "");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "ToRel", "");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "From.Descrizione.Descrizione", "");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "EsisteLista", "N");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "Lista", "");
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "RangeEsclusione", "N"); //...FIX 7430
		setCellData(eval(editGrid["CondizioniFiltri$ColonneWhere"]), ridx, "ListaEsclusione", "N"); //...FIX 7430
		//editGridLoadTable("CondizioniFiltri$ColonneWhere");
	}
}

function setCellData(grid, rowIndex, classAd, value){
	colIndex = getColIndex(grid, classAd);
	if(colIndex != -1){
		grid.rows[rowIndex].data[colIndex] = value;
	}
}

//36252 inizio
function onUsaConsuntiviAction(){
	var usaConsuntiviStoricizzati = document.getElementById("UsaConsuntiviStoricizzati").checked;
	if(usaConsuntiviStoricizzati){
		document.getElementById("DataRiferimento").disabled=false;
		document.getElementById("thCalButtonDataRiferimento").disabled=false;
		document.getElementById("DataRiferimento").style.background='white';		
		enableSearchComponent("Commessa",true, false);
		enableSearchComponent("ConsuntivoCommessa",true, false);
	}
	else{
		document.getElementById("DataRiferimento").disabled=true;
		document.getElementById("thCalButtonDataRiferimento").disabled=true;
		document.getElementById("DataRiferimento").style.background=bCo;
		document.getElementById("DataRiferimento").value='';
		enableSearchComponent("Commessa",false, true);
		enableSearchComponent("ConsuntivoCommessa",false, true);
	}
}
//36252 fine
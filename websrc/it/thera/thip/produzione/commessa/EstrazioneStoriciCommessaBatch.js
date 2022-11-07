var TIPO_CONSUN_PRV = "0";
var TIPO_CONSUN_DEF = "1";

var MOD_AMBIENTE_COSTI = "A";
var MOD_ARTICOLO_COSTI = "C";

function EstrazioneStoriciCommessaOL(){
	onModalitaCostiMancantiChangeAction();
	checkCostiArticoloDaDocumento();
}

function displayTab(tabName, display){
	var displayStr = (display)? '' : 'none';
	document.getElementById("myTabbed" + tabName + "_TAB").style.display = displayStr;
}

function clearFilter(rowIndex){
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "From", "");
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "FromRel", "");
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "To", "");
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "ToRel", "");
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "EsisteLista", "N");
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "Lista", "");
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "RangeEsclusione", "N"); //...FIX 7430
	setCellData(eval(editGrid["FiltroAmbienteCosti$ColonneWhere"]), rowIndex, "ListaEsclusione", "N"); //...FIX 7430
	editGridLoadTable("FiltroAmbienteCosti$ColonneWhere");
}

function getRowIndex(classCD, classAD){
	var grid = editGrid[classCD];
	var rows = grid.rows;
	for(i = 0; i < rows.length; i++){
		if(rows[i].data[21] == classAD) //...FIX 7430
			return i;
	}
	return -1;
}

function setCellData(grid, rowIndex, classAd, value){
	colIndex = getColIndex(grid, classAd);
	if(colIndex != -1){
		grid.rows[rowIndex].data[colIndex] = value;
	}
}

function getColIndex(grid, classAd){
	for(i = 0; i < grid.columns.length; i++){
		if(grid.columns[i].classAD == classAd)
			return i;
	}
	return -1;
}

function onModalitaCostiMancantiChangeAction(){
	var modalitaRecuperoCostoMancanti = eval("document.forms[0]." + idFromName["ModalitaCostiMancanti"]);
	var ambienteCostiMancanti = document.getElementById("IdAmbienteCostiMancanti");
	var tipoCostoMancanti = document.getElementById("IdTipoCostoMancanti");
	if(modalitaRecuperoCostoMancanti.value == MOD_AMBIENTE_COSTI){
		enableSearchComponent("AmbienteCostiMancanti", true, true);
		enableSearchComponent("TipoCostoMancanti", false, true);
		ambienteCostiMancanti.typeNameJS.mandatory=true;
		tipoCostoMancanti.typeNameJS.mandatory=false;
	    clearError(ambienteCostiMancanti, ambienteCostiMancanti.typeNameJS.mandatory);
	    clearError(tipoCostoMancanti, tipoCostoMancanti.typeNameJS.mandatory);
	    ambienteCostiMancanti.style.background=mCo;
	}
	else{
		enableSearchComponent("AmbienteCostiMancanti", false, true);
		enableSearchComponent("TipoCostoMancanti", true, true);
		ambienteCostiMancanti.typeNameJS.mandatory=false;
		tipoCostoMancanti.typeNameJS.mandatory=true;
	    clearError(ambienteCostiMancanti, ambienteCostiMancanti.typeNameJS.mandatory);
	    clearError(tipoCostoMancanti, tipoCostoMancanti.typeNameJS.mandatory);
	    tipoCostoMancanti.style.background=mCo;
	}
}

function checkCostiArticoloDaDocumento(){
	var costiArticoloDaDocumento = eval("document.forms[0]." + idFromName["CostiArticoloDaDocumento"]);
	var ordineRecArticolo = eval("document.forms[0]." + idFromName["OrdineRecArticolo"]);
	if(costiArticoloDaDocumento.checked){
		ordineRecArticolo.disabled=true;
	}
	else{
		ordineRecArticolo.disabled=false;
	}
}

function checkCostiRisorsaDaDocumento(){
	var costiRisorsaDaDocumento = eval("document.forms[0]." + idFromName["CostiRisorsaDaDocumento"]);
	var ordineRecRisorsa = eval("document.forms[0]." + idFromName["OrdineRecRisorsa"]);
	if(costiRisorsaDaDocumento.checked){
		ordineRecRisorsa.disabled=true;
	}
	else{
		ordineRecRisorsa.disabled=false;
	}
}
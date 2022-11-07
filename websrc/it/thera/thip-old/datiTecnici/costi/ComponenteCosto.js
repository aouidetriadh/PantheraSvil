function onChangeProvenienza() {
	var provenienza = eval('document.forms[0].'+idFromName['Provenienza']).value;
	var formula = eval('document.forms[0].'+idFromName['IdFormula']);
	if (eval('document.forms[0].'+idFromName['Provenienza']).value == 'E') {
		eval('document.forms[0].'+idFromName['IdFormula']).value = '';
		eval('document.forms[0].'+idFromName[eval('document.forms[0].'+idFromName['IdFormula']).ADChildren[0]]).value = '';
		formula.typeNameJS.mandatory = false;
		clearError(formula, formula.typeNameJS.mandatory);
		setMultisearchFormDisabled('Formula', 'IdFormula', true);
	}
	else
		setMultisearchFormDisabled('Formula', 'IdFormula', false);
	checkIdFormula();
}
function ComponenteCostoOL() {
	onChangeProvenienza();
	gestioneATempo();//33950
	resizeTo(950,600);//33950
}

function checkIdFormula() {
	var provenienza = eval('document.forms[0].'+idFromName['Provenienza']).value;
	var formula = eval('document.forms[0].'+idFromName['IdFormula']);
	if (provenienza == 'T' || provenienza == 'F') {
		formula.typeNameJS.mandatory = true;
		formula.style.background = mCo;
	}
}

//33950 inizio
function filedsClear() {
	var iIdRisorsa   = eval("document.forms[0]." + idFromName['IdRisorsa']);
    var iRisorsaDesc = eval("document.forms[0]." + idFromName['Risorsa.Descrizione.Descrizione']);
   	iIdRisorsa.value = "";
	iRisorsaDesc.value = "";
}

function gestioneATempo(){
	var gestATempo = eval("document.forms[0]." + idFromName['GestioneATempo']);
	var iTipoRisorsa = eval("document.forms[0]." + idFromName['TipoRisorsa']);
	var iLivelloRisorsa = eval("document.forms[0]." + idFromName['LivelloRisorsa']);
	var iCostoUnitario = eval("document.forms[0]." + idFromName['CostoUnitario']);
	
	if(gestATempo.checked == false){
		enableSearchComponent("TipoCosto", false, true);
		iTipoRisorsa.disabled = true;
		iTipoRisorsa.style.background = bCo;		
		iLivelloRisorsa.disabled = true;
		iLivelloRisorsa.style.background = bCo;
		enableSearchComponent("Risorsa", false, true);
		iCostoUnitario.value = "";
		iCostoUnitario.readOnly = -1;
		iCostoUnitario.style.background = bCo;
	}
	else{
		enableSearchComponent("TipoCosto", true, false);
		iTipoRisorsa.disabled = false;
		iTipoRisorsa.style.background = sCo;		
		iLivelloRisorsa.disabled = false;
		iLivelloRisorsa.style.background = sCo;
		enableSearchComponent("Risorsa", true, false);
		iCostoUnitario.readOnly = 0;
		iCostoUnitario.style.background = sCo;
	}
}
//33950 fine
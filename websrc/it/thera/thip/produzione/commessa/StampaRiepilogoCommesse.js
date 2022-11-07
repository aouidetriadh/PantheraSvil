var TIPO_CHIUSURA_ULTIMA_PROV    = '0';
var TIPO_CHIUSURA_ULTIMA_DEFINIT = '1';

function StampaRiepilogoCommesseOL()
{
	onTipoChiusuraChange();
	//36252 inizio
	document.getElementById("IdCommessaConsuntivo").style.display=displayNone;
	document.getElementById("IdCommessaBudget").style.display=displayNone;
	onUsaConsuntiviAction();
	//36252 fine
}

function onTipoChiusuraChange()
{
	var chisuraDefintiva = eval('document.forms[0].' + idFromName['TipoChiusura']).value == TIPO_CHIUSURA_ULTIMA_DEFINIT;

	eval('document.forms[0].' + idFromName['CompresoOrdinato']).disabled = chisuraDefintiva;
	eval('document.forms[0].' + idFromName['CompresoRichiesto']).disabled = chisuraDefintiva;

	if (chisuraDefintiva)
	{
		eval('document.forms[0].' + idFromName['CompresoOrdinato']).checked = false;
		eval('document.forms[0].' + idFromName['CompresoRichiesto']).checked = false;
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
		enableSearchComponent("BudgetCommessa",true, false);
	}
	else{
		document.getElementById("DataRiferimento").disabled=true;
		document.getElementById("thCalButtonDataRiferimento").disabled=true;
		document.getElementById("DataRiferimento").style.background=bCo;
		document.getElementById("DataRiferimento").value='';
		enableSearchComponent("Commessa",false, true);
		enableSearchComponent("ConsuntivoCommessa",false, true);
		enableSearchComponent("BudgetCommessa",false, true);
	}
}
//36252 fine
var IdAzienda;
var IdCommessa;
var IdCommessaAppartenenza;
var IdCommessaPrincipale;
var IdAmbienteCommessa;
var IdResponsabileCommessa;
var IdResponsabilePreventivaz;
var IdTipoCommessa;
var StatoAvanzamento;
var IdArticolo;
var AnnoOrdine;
var IdNumeroOrdine;
var IdCliente;
var IdEsternoConfig;
var IdStabilimento;

var AggiornamentoSaldi;
var DataConferma;
var PianoFatturazione;
var oldPianoFatturazione;
var ChiudiOrdUltimaFat;
var mode;
var NaturaCommessa = "";
var hasWfStatus = false;

var DataChiusuraTecnica;
var DataChiusuraOperativa;
var DataChiusura;

var PROVVISORIO = "0";
var CONFERMATA = "1"
var CHIUSO_TEC = "2";
var CHIUSO_OPE = "3";
var CHIUSO_CON = "4";

var PIANO_FATT_ASS = '0';
var PIANO_FATT_DIS = '1';
var PIANO_FATT_ATT = '2';

var realAction = "";
var runRealAction = false;
var storedPianoFatturazione;
var storedStatoAvanzamento;
//Fix 04171 Begin -Jed 07/09/2005
var DataApertura;
//Fix 04171 End -Jed 07/09/2005

//Fix Fattouma 06/10/2005 04361
var initPianofatturazione;
//Fix 04637 Mz inizio
var storedIdTipoCommessa;
//Fix 04637 Mz fine
var abilitaBottone; // 15938
/*
 Paolo J Franzoni (24 jan 2003)
*/
// Inizio Fix 03463 A.Boulila on 01/04/2005
function CommessaOL(param) {

	initVars();
	gestioneBudgetCommessaFrame();//35382
	storedPianoFatturazione = PianoFatturazione.value;
	oldPianoFatturazione = PianoFatturazione.value;
	storedStatoAvanzamento = StatoAvanzamento.value;
	//Fix 04637 Mz inizio
	storedIdTipoCommessa = IdTipoCommessa.value;
	//Fix 04637 Mz fine

	overrideEditGridSelectRow();

	if (mode.value == 'UPDATE') {
		AggiornamentoSaldi.disabled = true;
		visualizzaOrdineRiorg(); // 9632
	}

	if(mode.value == 'NEW' && !isEm(IdCommessaAppartenenza.value)) {
		AggiornamentoSaldi.disabled = true;
		onCommessaAppartenenzaChangeAction();
	}

	if(mode.value != "SHOW") {
		managePianoFattTab();
		if(!isEm(IdCommessaAppartenenza.value))
			enableSottoCommessaFields(false);
		gestioneDateChisure();
		if(mode.value != 'NEW' && mode.value != "SHOW")
			onTipoCommessaChangeAction(true);
		onPianoFattChangeAction();
	}

	redifineEditGridActivateEditPanel();

	//Fix 04171 Begin - Jed 07/09/2005
	if(mode.value == 'NEW')
		DataApertura.value = getCurrentDate();
	if(mode.value == 'SHOW' &&
		(PianoFatturazione.value == PIANO_FATT_ASS || !isEm(IdCommessaAppartenenza.value))){
		//Fix12103 Inizio RA
		//eval("mytabbedPianoFatturazioneTab_TAB").style.display = 'none';
		eval("mytabbedPianoFatturazioneTab_TAB").style.display = displayNone;
		//Fix12103 Fine RA
	}
	if((mode.value == 'UPDATE') || (mode.value == "SHOW")){
		top.tree.tree.setSelectedNode(document.forms[0].thKey.value);
	}
	//Fix 04171 End - Jed 07/09/2005
	//Begin Fix 04361 - sayadi 27/09/2005
	if(!isEm(IdCommessaAppartenenza.value) && eval(StatoAvanzamento.value) < eval(CHIUSO_CON))
		removeStatoAvanzamentoLastElem();
	//End Fix 04361 - sayadi 27/09/2005
	if(mode.value == 'COPY')
		PianoFatturazione.disabled = true;

	if(mode.value == 'SHOW') {
		displayWfStatus(dispWfInShow);
	}

	redefineBackwardClick(mode.value);
	redefineForwardClick(mode.value);

	//...FIX 4810
	callMyAllROnly();
	// Fix 15938
	abilitaBottoneCreaOrdVen(abilitaBottone);
	redefineWriteTargetNew();
}

//...FIX 4810
function callMyAllROnly() {
	if(eval(StatoAvanzamento.value) == eval(CHIUSO_CON)) {
		myAllROnly();
	}
}

//...FIX 4810
function myAllROnly() {
	var allElem = document.forms[0].elements;
	var curEl;
	for (var i = 0; i < allElem.length; i++) {
		curEl = allElem[i];
		if(curEl.name) {
			if(curEl.name.substr(0,2) != "th" && curEl.name.substr(0,2) != "wf" &&
				 curEl.name.substr(0,2) != "Wf" && curEl.name.substr(0,2) != "WF") {
				if(curEl.type == "text" || curEl.type == "textarea" || curEl.tagName == "FIELDSET")
					curEl.readOnly = -1;
				else if(curEl.type == "select-one")
					curEl.disabled = -1;
				else
					curEl.disabled = true;
			}
			updateFieldColor(false, curEl, false, 'SHOW');
		}
		else {
			if(curEl.type == "button")
				curEl.disabled = true;
		}
	}
}

var oldRunActionDirect = runActionDirect;
runActionDirect = function a(action, type, classhdr, key, target, toolbar) {
	var oldAggSaldoStatus;
	var oldStatoAvanzStatus;
	var oldPianoFattStatus;

	if(AggiornamentoSaldi){
		oldAggSaldoStatus = AggiornamentoSaldi.disabled;
		AggiornamentoSaldi.disabled = false;
	}

	if(StatoAvanzamento){
		oldStatoAvanzStatus = StatoAvanzamento.disabled;
		StatoAvanzamento.disabled = false;
	}

	if(PianoFatturazione){
		oldPianoFattStatus = PianoFatturazione.disabled;
		PianoFatturazione.disabled = false;
	}

	if((action == "SAVE" || action == "SAVE_AND_NEW" || action == "SAVE_AND_CLOSE") && !runRealAction) {
		realAction = action;
		oldRunActionDirect("CHECK_BEFORE_SAVE", type, classhdr, key, target, toolbar);
	}
	else {
		if(action == "SAVE" || action == "SAVE_AND_NEW" || action == "SAVE_AND_CLOSE") {
			var desc = eval("document.forms[0]." + idFromName["Descrizione.Descrizione"]).value;
			eval("document.forms[0]." + idFromName["NodoDescrizione"]).value = IdCommessa.value + " - " + desc;
			if(checkPianoFatturazione()) {
				if(confirmChangePianofatturazione()) {
					oldRunActionDirect(action, type, classhdr, key, target, toolbar);
					runRealAction = false;
				}
				else
					eval(errorsViewName).clearDisplay();
			}
			else {
				oldRunActionDirect(action, type, classhdr, key, target, toolbar);
				runRealAction = false;
			}
		}
		else {
			oldRunActionDirect(action, type, classhdr, key, target, toolbar);
			runRealAction = false;
		}
	}

	if(AggiornamentoSaldi)
		AggiornamentoSaldi.disabled = oldAggSaldoStatus;
	if(StatoAvanzamento)
		StatoAvanzamento.disabled = oldStatoAvanzStatus;
	if(PianoFatturazione)
		PianoFatturazione.disabled = oldPianoFattStatus;
	enableFormActions();
}

/**
 *
 */
function onCommessaAppartenenzaChangeAction(){

	eval("document.forms[0]." + idFromName["AnnoOrdine"]).value = "";
	eval("document.forms[0]." + idFromName["IdNumeroOrdine"]).value = "";
	eval("document.forms[0]." + idFromName["Ordine.NumeroDocumentoFormattato"]).value = "";
	eval("document.forms[0]." + idFromName["IdRigaOrdine"]).value = "";
	eval("document.forms[0]." + idFromName["RigaOrdine.DescrizioneArticolo"]).value = "";

	if(!isEm(IdCommessaAppartenenza.value)) {
		PianoFatturazione.value = PIANO_FATT_ASS;
		enableSottoCommessaFields(false);
		var url = "/" + webAppPath + "/" + servletPath + "/it.thera.thip.base.commessa.web.CommessaActionsManager";
		//33276 inizio
		/*var params = "?IdAzienda=" + IdAzienda.value;
		params += "&IdCommessaApp=" + IdCommessaAppartenenza.value;*/
		var params = "?IdAzienda=" + URLEncode(IdAzienda.value);
		params += "&IdCommessaApp=" + URLEncode(IdCommessaAppartenenza.value);	 
		//33276 fine
		params += "&StatoAvanzamento=" + StatoAvanzamento.value;
		params +="&Action=IMPOSTA_CMM_APP_DATE";
		params += "&Mode=" + mode.value;
		//Fix12103 Inizio RA
		//document.frames[errorsFrameName].location = url + params;
		setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url + params);
		//Fix12103 Fine RA
	}
	else {
		enableSottoCommessaFields(true);
		updateTipoCmmRelatedData(NaturaCommessa);
	}

	managePianoFattTab();
	displayWfStatus(hasWfStatus);
}

/**
 *
 */
function onTipoCommessaChangeAction(init) {
	if(!isEm(IdTipoCommessa.value)) {
		var url = "/" + webAppPath + "/" + servletPath + "/it.thera.thip.base.commessa.web.CommessaActionsManager";
		//33276 inizio
		/*
		var params = "?IdAzienda=" + IdAzienda.value;
		params += "&IdTipoCommessa=" + IdTipoCommessa.value;
		*/
		var params = "?IdAzienda=" +  URLEncode(IdAzienda.value);
		params += "&IdTipoCommessa=" +  URLEncode(IdTipoCommessa.value);
		//Fix 04637 Mz inizio
		//params += "&OldIdTipoCommessa=" + storedIdTipoCommessa;
		params += "&OldIdTipoCommessa=" + URLEncode(storedIdTipoCommessa);
		//Fix 04637 Mz fine
		//33276 fine
		params +="&Action=TIPO_CMM_MODIF";
		params += "&Mode=" + mode.value;
		params += "&Init=" + init;
		if(init && !isEm(IdCommessaAppartenenza.value))
			//params += "&IdCommessaApp=" + IdCommessaAppartenenza.value;//33276
			params += "&IdCommessaApp=" + URLEncode(IdCommessaAppartenenza.value);//33276
		//Fix12103 Inizio RA
		//document.frames[errorsFrameName].location = url + params;
		setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url + params);
		//Fix12103 Fine RA
	}
	else {
		updateTipoCmmRelatedData("");
		if(init) {
			onArticoloChangeAction(init);
		}
	}
}

/**
 *
 */
function onStatoAvanzChangeAction() {
	if(!isEm(IdTipoCommessa.value) && !isEm(NaturaCommessa)) {
		updateTipoCmmRelatedData(NaturaCommessa);
	}
	gestioneDateChisure();
}

/**
 *
 */
function onOrdineChangeAction() {
	if(!isEm(IdNumeroOrdine.value) && mode.value != 'SHOW') {
		var url = "/" + webAppPath + "/" + servletPath + "/it.thera.thip.base.commessa.web.CommessaActionsManager";
		//33276 inizio
		/*
		var params = "?IdAzienda=" + IdAzienda.value;
		params += "&IdAnnoOrdine=" + AnnoOrdine.value;
		params += "&IdNumeroOrdine=" + IdNumeroOrdine.value;
		*/
		var params = "?IdAzienda=" +  URLEncode(IdAzienda.value);
		params += "&IdAnnoOrdine=" +  URLEncode(AnnoOrdine.value);
		params += "&IdNumeroOrdine=" +  URLEncode(IdNumeroOrdine.value);
		//33276 fine
		params += "&isCmmPrincipale=" + isEm(IdCommessaAppartenenza.value);
		params +="&Action=ORDINE_MODIF";
		params += "&Mode=" + mode.value;
		//Fix12103 Inizio RA
		//document.frames[errorsFrameName].location = url + params;
		setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url + params);
		//Fix12103 Fine RA
	}
	else {
		enableSearchComponent("RigaOrdine", false, true);
	}
}

/**
 *
 */
function onArticoloChangeAction(init) {
	if(isEm(IdArticolo.value)) {
		enableSearchComponent("VersioneArticolo", false, true);
		enableSearchComponent("Configurazione", false, true);
		valorizeMultisearchField("IdUmPrmMag", "", "UmPrmMag.Descrizione.Descrizione", "");
		if(init)
			onOrdineChangeAction();
	}
	else {
		enableSearchComponent("VersioneArticolo", true, false);
		var url = "/" + webAppPath + "/" + servletPath + "/it.thera.thip.base.commessa.web.CommessaActionsManager";
		//33276 inizio
		/*
		var params = "?IdAzienda=" + IdAzienda.value;
		params += "&IdArticolo=" + IdArticolo.value;
		*/
		var params = "?IdAzienda=" +  URLEncode(IdAzienda.value);
		params += "&IdArticolo=" +  URLEncode(IdArticolo.value);
		//33276 fine
		params +="&Action=ARTICOLO_MODIF";
		params += "&Init=" + init;
		//Fix12103 Inizio RA
		//document.frames[errorsFrameName].location = url + params;
		setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url + params);
		//Fix12103 Fine RA
	}
}

/**
 *
 */
function onPianoFattChangeAction() {
	managePianoFattTab();
	if(mode.value != "SHOW") {
		if(PianoFatturazione.value == PIANO_FATT_ASS) {
			ChiudiOrdUltimaFat.disabled = true;
			ChiudiOrdUltimaFat.checked = false;
		}
		else {
			ChiudiOrdUltimaFat.disabled = false;
			//if(mode.value == "NEW")
			if(oldPianoFatturazione == PIANO_FATT_ASS)
				ChiudiOrdUltimaFat.checked = true;
		}
	}
	oldPianoFatturazione = PianoFatturazione.value;
}

function checkPianoFatturazione() {
	if(mode.value != "SHOW") {
		if((PianoFatturazione.value == PIANO_FATT_ASS) ||
			 ((storedStatoAvanzamento != PROVVISORIO) && (StatoAvanzamento.value == PROVVISORIO))) {
			var rateEditGrid = editGrid['RateCommesse'];
			if(rateEditGrid.rows.length > 0) {
				return true;
			}
		}
		else {
			initPianofatturazione = PianoFatturazione.value;
		}
	}
	return false;
}

// Fix 04361 Fattouma 06/10/2005 begin
function confirmChangePianofatturazione() {
	return confirm(pianoFatturazionemessage);
	/*if(!ret) {
		//PianoFatturazione.value = initPianofatturazione;
		//displayTab("PianoFatturazioneTab", true);
		return true;
	}
	//initPianofatturazione = PianoFatturazione.value;
	return false;
	*/
}
// end

/**
 *
 */
function gestioneDateChisure() {

	if(eval(StatoAvanzamento.value) >= eval(CONFERMATA) && isEm(IdCommessaAppartenenza.value)) {
		enableSimpleField(DataConferma, true, false);
		setFieldMandatory(DataConferma, true);
		//Fix 04171 Begin - Jed 07/09/2005
		if(isEm(DataConferma.value))
			DataConferma.value = getCurrentDate();
		//Fix 04171 End - Jed 07/09/2005
	}
	else {
		enableSimpleField(DataConferma, false, isEm(IdCommessaAppartenenza.value));
		setFieldMandatory(DataConferma, false);
	}
	if(eval(StatoAvanzamento.value) >= eval(CHIUSO_TEC)) {
		enableSimpleField(DataChiusuraTecnica, true, false);
		setFieldMandatory(DataChiusuraTecnica, true);
		//Fix 04171 Begin - Jed 07/09/2005
		if(isEm(DataChiusuraTecnica.value))
			DataChiusuraTecnica.value = getCurrentDate();
		//Fix 04171 End - Jed 07/09/2005
	}
	else {
		enableSimpleField(DataChiusuraTecnica, false, true);
		setFieldMandatory(DataChiusuraTecnica, false);
	}
	if(eval(StatoAvanzamento.value) >= eval(CHIUSO_OPE)){
		enableSimpleField(DataChiusuraOperativa, true, false);
		setFieldMandatory(DataChiusuraOperativa, true);
		//Fix 04171 Begin - Jed 07/09/2005
		if(isEm(DataChiusuraOperativa.value))
			DataChiusuraOperativa.value = getCurrentDate();
		//Fix 04171 End	- Jed 07/09/2005
	}
	else{
		enableSimpleField(DataChiusuraOperativa, false, true);
		setFieldMandatory(DataChiusuraOperativa, false);
	}
	if(eval(StatoAvanzamento.value >= CHIUSO_CON)){
		enableSimpleField(DataChiusura, true, false);
		DataChiusura.disabled = false;
		setFieldMandatory(DataChiusura, true);
		//Fix 04171 Begin - Jed 07/09/2005
		if(isEm(DataChiusura.value))
			DataChiusura.value = getCurrentDate();
		//Fix 04171 End - Jed 07/09/2005
	}
	else{
		enableSimpleField(DataChiusura, false, true);
		setFieldMandatory(DataChiusura, false);
	}
}

/**
 *
 */
function enableSimpleField(field, enable, clearData) {
	var enableStr = (enable)? '' : '0';
	field.readOnly = enableStr;
	if(clearData)
		field.value = "";
	clearError(field);
}

/**
 *
 */
function enableSottoCommessaFields(enable) {
	if(enable){
		DataConferma.readOnly = '';
		DataConferma.style.background = sCo;
	}
	else{
		DataConferma.readOnly = '0';
		//Fix12103 Inizio RA
		//DataConferma.style.background = document.forms[0].style.background;
		DataConferma.style.background = bCo;
		//Fix12103 Fine RA
	}

	enableSearchComponent("TipoCommessa", enable, false);
	enableSearchComponent("AmbienteCommessa", enable, false);
	if(mode.value == 'UPDATE' || (mode.value == "NEW" && !isEm(IdCommessaAppartenenza.value)))
		AggiornamentoSaldi.disabled = true;
	else if(mode.value != 'SHOW')
		AggiornamentoSaldi.disabled = enable;

	enableSearchComponent("Cliente", enable, false);
	if(StatoAvanzamento.value == PROVVISORIO)
		StatoAvanzamento.disabled = true;
	else
		StatoAvanzamento.disabled = false;
	enableSearchComponent("Ordine", enable, false);
	if(NaturaCommessa != "1"){
		PianoFatturazione.disabled = true;
		PianoFatturazione.value = PIANO_FATT_ASS;
	}
	else {
		PianoFatturazione.disabled = !enable;
		if(mode.value == 'COPY')
			PianoFatturazione.disabled = true;
	}
}

/**
 *
 */
function valorizeMultisearchField(idField, id, descField, desc) {
	valorizeSimpleField(idField, id);
	valorizeSimpleField(descField, desc);
}

/**
 *
 */
function valorizeSimpleField(field, value) {
	eval('document.forms[0].' + idFromName[field]).value = value;
}

/**
 *
 */
function valorizeCheckBox(field, checked) {
	eval('document.forms[0].' + idFromName[field]).checked = eval(checked);
}

/**
 *
 */
function valorizeComboBox(field, value) {
	var combo = eval('document.forms[0].' + idFromName[field]);
	for(i = 0; i < combo.options.length; i++) {
		if(combo.options[i].value == value)
			combo.selectedIndex = i;
	}
}

/**
 *
 */
function impostaResponsabileCmm(idRespCmm, descRespCmm) {
	if(isEm(IdResponsabileCommessa.value))
		valorizeMultisearchField("IdResponsabileCommessa", idRespCmm, "ResponsabileCommessa.Denominazione", descRespCmm);
}

/**
 *
 */
function impostaResponsabilePrev(idRespPrev, descRespPrev) {
	if(isEm(IdResponsabilePreventivaz.value))
		valorizeMultisearchField("IdResponsabilePreventivaz", idRespPrev, "ResponsabilePreventivaz.Denominazione", descRespPrev);
}

/**
 *
 */
function updateTipoCmmRelatedData(naturaCmm) {
	NaturaCommessa = naturaCmm;
	if(naturaCmm == "1") {
		setFieldMandatory(IdAmbienteCommessa, true, false);

		if(StatoAvanzamento.value != PROVVISORIO) {
			setFieldMandatory(IdArticolo, true, false);
			setFieldMandatory(AnnoOrdine, true, false);
			setFieldMandatory(IdNumeroOrdine, true, false);
			setFieldMandatory(IdCliente, true, false);
			setFieldMandatory(IdResponsabileCommessa, true, false);
			//Fix 04361 Begin Jed 21/09/2205
			//setFieldMandatory(IdResponsabilePreventivaz, true, false);
			//Fix 04361 End Jed 21/09/2205
			setFieldMandatory(IdStabilimento, true, false)
		}

		if(isEm(IdCommessaAppartenenza.value)) {
			PianoFatturazione.disabled = false;
			if(mode.value == 'COPY')
				PianoFatturazione.disabled = true;
		}

		//if(mode.value == "NEW"){
		AggiornamentoSaldi.checked = true;
		AggiornamentoSaldi.disabled = true;
		//}
	}

	if(naturaCmm != "1") {
		setFieldMandatory(IdAmbienteCommessa, false, false);
		PianoFatturazione.disabled = true;
		PianoFatturazione.value = PIANO_FATT_ASS;
	}

	if(naturaCmm != "1" || StatoAvanzamento.value == PROVVISORIO) {
		setFieldMandatory(IdArticolo, false, false);
		setFieldMandatory(AnnoOrdine, false, false);
		setFieldMandatory(IdNumeroOrdine, false, false);
		setFieldMandatory(IdCliente, false, false);
		setFieldMandatory(IdResponsabileCommessa, false, false);
		//Fix 04361 Begin Jed 21/09/2205
		//setFieldMandatory(IdResponsabilePreventivaz, false, false);
		//Fix 04361 End Jed 21/09/2205
		if(mode.value == "NEW" && isEm(IdCommessaAppartenenza.value)) {
			AggiornamentoSaldi.disabled = false;
		}
		setFieldMandatory(IdStabilimento, false, false);
	}
	onPianoFattChangeAction();
}

/**
 *
 */
function setFieldMandatory(field, mand, forceMandColor) {
	field.typeNameJS.mandatory = mand;
	clearError(field, mand);
}

/**
 *
 */
function displayTab(tabName, display) {
	// Begin Fix 04171 - Jed 07/09/2005
	valoreTotaleOrdine = eval('document.forms[0].' + idFromName["ValoreTotaleOrdine"]).value;
	var rifOridneRiorg = eval('document.forms[0].' + idFromName["RifOrdineRiorg"]).value; // Fix 9632
	var url = "/" + webAppPath + "/" + servletPath + "/it.thera.thip.base.commessa.web.CommessaActionsManager";
	var params = "?display=" + display;
	params += "&annoOrdine=" + AnnoOrdine.value;
	params += "&idNumeroOrdine=" + IdNumeroOrdine.value;
	params += "&RifOrdineRiorg=" + rifOridneRiorg;
	params +="&Action=DISP_TAB";
	params += "&tabName=" + tabName;
	//Fix12103 Inizio RA
	//document.displayTabPianif.location = url + params;
	setLocationOnWindow(document.getElementById("displayTabPianif").contentWindow, url + params);
	//Fix12103 Fine RA
	/*var displayStr = (display)? '' : 'none';
		eval("mytabbed" + tabName + "_TAB").style.display = displayStr;*/
	// End Fix 04171 - Jed 07/09/2005
}

function managePianoFattTab() {
	var display = isEm(IdCommessaAppartenenza.value)
						 && PianoFatturazione.value != PIANO_FATT_ASS
						 && mode.value != "NEW"
	displayTab("PianoFatturazioneTab", display);
}

/**
 *
 */
function displayWfStatus(display) {
	var strDisplay;
  //Fix12103 Inizio RA
	if(isEm(IdCommessaAppartenenza.value))
		//strDisplay = (display)? 'block' : 'none';
    strDisplay = (display)? displayBlock : displayNone;
	else
		//strDisplay = 'none';
    strDisplay = displayNone;
	document.getElementById("WorkFlowTR").style.display = strDisplay;
  //Fix12103 Fine RA
}

/**
 *
 */
function confirmLivelloMin(message) {
	var resp = confirm(message);
	if(resp)
		runSave();
	else
		eval(errorsViewName).clearDisplay();
}

/**
 *
 */
function runSave() {
	runRealAction = true;
	var className = document.forms[0].thClassName.value;
	var key = document.forms[0].thKey.value;
	runActionDirect(realAction, "action_submit", className, key, 'errorsFrame', "no");
}

/**
 *
 */
function initVars() {
	IdAzienda = eval('document.forms[0].' + idFromName["IdAzienda"]);
	IdCommessa = eval('document.forms[0].' + idFromName["IdCommessa"]);
	IdCommessaAppartenenza = eval('document.forms[0].' + idFromName["IdCommessaAppartenenza"]);
	IdCommessaPrincipale = eval('document.forms[0].' + idFromName["IdCommessaPrincipale"]);
	IdResponsabileCommessa = eval('document.forms[0].' + idFromName["IdResponsabileCommessa"]);
	IdResponsabilePreventivaz = eval('document.forms[0].' + idFromName["IdResponsabilePreventivaz"]);
	IdAmbienteCommessa = eval('document.forms[0].' + idFromName["IdAmbienteCommessa"]);
	IdTipoCommessa = eval('document.forms[0].' + idFromName["IdTipoCommessa"]);
	StatoAvanzamento = eval('document.forms[0].' + idFromName["StatoAvanzamento"]);
	AggiornamentoSaldi = eval('document.forms[0].' + idFromName["AggiornamentoSaldi"]);
	PianoFatturazione = eval('document.forms[0].' + idFromName["PianoFatturazione"]);
	ChiudiOrdUltimaFat = eval('document.forms[0].' + idFromName["ChiudiOrdUltimaFat"]);
	IdArticolo = eval('document.forms[0].' + idFromName["IdArticolo"]);
	AnnoOrdine = eval('document.forms[0].' + idFromName["AnnoOrdine"]);
	IdNumeroOrdine = eval('document.forms[0].' + idFromName["IdNumeroOrdine"]);
	IdCliente = eval('document.forms[0].' + idFromName["IdCliente"]);
	DataConferma = eval('document.forms[0].' + idFromName["DataConferma"]);
	DataChiusuraTecnica = eval('document.forms[0].' + idFromName["DataChiusuraTecnica"]);
	DataChiusuraOperativa = eval('document.forms[0].' + idFromName["DataChiusuraOperativa"]);
	DataChiusura = eval('document.forms[0].' + idFromName["DataChiusura"]);
	IdEsternoConfig = eval('document.forms[0].' + idFromName["IdEsternoConfig"]);
	IdStabilimento = eval('document.forms[0].' + idFromName["IdStabilimento"]);
	//Fix 04171 Begin -Jed 07/09/2005
	DataApertura = eval('document.forms[0].' + idFromName["DataApertura"]);;
	//Fix 04171 End -Jed 07/09/2005
	mode = document.forms[0].thMode;
	//Fix 04361 Fattouma 06/10/2005 begin
	initPianofatturazione	= PianoFatturazione.value;
	//
}

function redifineEditGridActivateEditPanel() {
	oldEditGridActivateEditPanel = editGridActivateEditPanel;
	editGridActivateEditPanel = function (gridClassCD, mode) {
		var curGrid = eval(editGrid[gridClassCD]);
		curGrid.rowUrl += "&IdCommessa=" + eval('document.forms[0].' + idFromName['IdCommessa']).value;
		oldEditGridActivateEditPanel(gridClassCD, mode);
	}
}

/**
 *
 */
function overrideEditGridSelectRow() {
	var oldEditGridSelectRow = editGridSelectRow;
	editGridSelectRow = function(gridClassCD, rowNum, colNum) {
		oldEditGridSelectRow(gridClassCD, rowNum, colNum);
		//Fix 04171 Begin - Jed
		//if(gridClassCD == 'RateCommesse'){
		if(gridClassCD == 'RateCommesse' && mode.value != "SHOW") {
		//Fix 04171 End - Jed
			var rateEditGrid = editGrid[gridClassCD];
			if(rowNum >= 0 && colNum >= 0 && rateEditGrid.firstRow + rowNum < rateEditGrid.rows.length){
				var rataState = getCellData(rateEditGrid, rateEditGrid.firstRow + rowNum, "DatiComuniEstesi.Stato");
				if(rataState != "A" && storedPianoFatturazione == PIANO_FATT_ATT)
					eval("document.forms[0].DeleteRow_" + gridClassCD).disabled = true;
				else
					eval("document.forms[0].DeleteRow_" + gridClassCD).disabled = false;
			}
				//...FIX 4957
			if(eval(StatoAvanzamento.value) == eval(CHIUSO_CON)) {
				eval("document.forms[0].DeleteRow_" + gridClassCD).disabled = true;
				eval("document.forms[0].NewRow_" + gridClassCD).disabled = true;
				eval("document.forms[0].UpdateRow_" + gridClassCD).disabled = true;
				eval("document.forms[0].CopyRow_" + gridClassCD).disabled = true;
			}
		}
	}
}

/**
 *
 */
function getCellData(grid, rowIndex, classAd) {
	colIndex = getColIndex(grid, classAd);
	if(colIndex != -1){
		return grid.rows[rowIndex].data[colIndex];
	}
}

/**
 *
 */
function getColIndex(grid, classAd) {
	for(i = 0; i < grid.columns.length; i++) {
		if(grid.columns[i].classAD == classAd)
			return i;
	}
	return -1;
}

// Fine Fix 03463 A.Boulila on 01/04/2005

// Begin Fix 04361 Jed 22/09/2005

function onAmbienteCommessaChangeAction() {
	if(!isEm(IdAmbienteCommessa.value)) {
		var url = "/" + webAppPath + "/" + servletPath + "/it.thera.thip.base.commessa.web.CommessaActionsManager";
		//33276 inizio
		/*
		var params = "?IdAzienda=" + IdAzienda.value;
		params += "&IdCommessa=" + IdCommessa.value;
		params += "&IdAmbienteCommessa=" + IdAmbienteCommessa.value;
		*/
		var params = "?IdAzienda=" +  URLEncode(IdAzienda.value);
		params += "&IdCommessa=" +  URLEncode(IdCommessa.value);
		params += "&IdAmbienteCommessa=" +  URLEncode(IdAmbienteCommessa.value);
		//33276 fine
		params +="&Action=AMBIENTE_CMM_MODIF";
		//Fix12103 Inizio RA
		//document.frames[errorsFrameName].location = url + params;
		setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url + params);
		//Fix12103 Fine RA
	}
}

// End Fix 04361 Jed 22/09/2005
//Begin Fix 04361 Sayadi 27/09/2005
function removeStatoAvanzamentoLastElem() {
	StatoAvanzamento.options[StatoAvanzamento.length-1] = null;
}
//End Fix 04361 Sayadi 27/09/2005

// Fix 04361 Fattouma 07/10/2005 begin
function runSaveFromRata() {
	if(storedPianoFatturazione == PIANO_FATT_ASS &&
		 (PianoFatturazione.value == PIANO_FATT_ATT || PianoFatturazione.value == PIANO_FATT_DIS)) {
		var url = "/" + webAppPath + "/" + servletPath + "/it.thera.thip.base.commessa.web.CommessaActionsManager";
		//33276 inizio
		/*
		var params = "?IdAzienda=" + IdAzienda.value;
		params += "&IdCommessa=" + IdCommessa.value;
		params += "&PianoFatturazione=" + PianoFatturazione.value;
		*/
		var params = "?IdAzienda=" +  URLEncode(IdAzienda.value);
		params += "&IdCommessa=" +  URLEncode(IdCommessa.value);
		params += "&PianoFatturazione=" +  URLEncode(PianoFatturazione.value);
		//33276 fine
		params +="&Action=UPDATE_PIANO_FATTURAZIONE";
		//Fix12103 Inizio RA
		//document.frames[errorsFrameName].location = url + params;
		setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow, url + params);
		//Fix12103 Fine RA
	}
}
//end

//Fix 04637 Mz inizio
function impostaWfData(aWfId, aWfDesc, aWfNodeId, aWfNodeDesc, aWfSubNodeId, aWfSubNodeDesc) {
	var comboComp = eval("document.forms[0].WF_SPEC_COMBO");
	var idx = 0;
	if (comboComp) {
		for(; idx <comboComp.length;idx++) {
			if(comboComp.options[idx].text == aWfDesc) {
				comboComp.options[idx].selected = true;
				break;
			}
		}
		curWfNum = idx;
	}

	avaWf[curWfNum].wfId = aWfId;
	avaWf[curWfNum].wfDesc = aWfDesc;
	avaWf[curWfNum].wfNodeId = aWfNodeId;
	avaWf[curWfNum].wfNodeDesc = aWfNodeDesc;
	avaWf[curWfNum].wfSubNodeId = aWfSubNodeId;
	avaWf[curWfNum].wfSubNodeDesc = aWfSubNodeDesc;
	avaWf[curWfNum].backNum = 0;
	avaWf[curWfNum].forNum = 0;
	avaWf[curWfNum].notable = false;

	if (comboComp) {
		comboComp.selectedIndex = idx;
		wfPanelChangeSpec();
	}
	else {
		document.forms[0].WF_SPEC_INPUT.value = avaWf[0].wfDesc;
		wfPanelUpdateGUI();
	}
}

function redefineBackwardClick(mode) {
	var wfPanelBackwardClickStd = wfPanelBackwardClick;
	wfPanelBackwardClick = function ee() {
		if (mode == 'SHOW') {
			return
		}
		else {
			wfPanelBackwardClickStd();
		}
	}
}

function redefineForwardClick(mode) {
	var wfPanelForwardClickStd = wfPanelForwardClick;
	wfPanelForwardClick = function ss() {
		if (mode == 'SHOW') {
			return
		}
		else {
			wfPanelForwardClickStd();
		}
	}
}
//Fix 04637 Mz fine

// Inizio 9632
// Visualizza il riferimento all'ordine riorganizzato.
function visualizzaOrdineRiorg(){
	var annoOrdine = eval("document.forms[0]." + idFromName["AnnoOrdine"]).value;
	var numeroOrdine = eval("document.forms[0]." + idFromName["IdNumeroOrdine"]).value ;
	var rifOrdineRio = eval("document.forms[0]." + idFromName["RifOrdineRiorg"]).value ;
	//Fix12103 Inizio RA
	if (annoOrdine == "" && numeroOrdine == "" && rifOrdineRio != ""){
	   //VisOrdEff.style.display="none";
	   document.getElementById("VisOrdEff").style.display=displayNone;
	   //VisOrdRio.style.display="block";
	   document.getElementById("VisOrdRio").style.display=displayBlock;
	}
	else{
	   //VisOrdEff.style.display="block";
	   document.getElementById("VisOrdEff").style.display=displayBlock;
	   //VisOrdRio.style.display="none";
	   document.getElementById("VisOrdRio").style.display=displayNone;
	}
	//Fix12103 Fine RA
}

// Visualizza il valore dell'ordine riorganizzato.
function visualizzaValoreRiorg(){
  var annoOrdine = eval("document.forms[0]." + idFromName["AnnoOrdine"]).value;
  var numeroOrdine = eval("document.forms[0]." + idFromName["IdNumeroOrdine"]).value ;
  var rifOrdineRio = eval("document.forms[0]." + idFromName["RifOrdineRiorg"]).value ;
  //Fix12103 Inizio RA
  if (annoOrdine == "" && numeroOrdine == "" && rifOrdineRio != ""){
    //VisValEffLabel.style.display="none";
	document.getElementById("VisValEffLabel").style.display=displayNone;
	//VisValEff.style.display="none";
	document.getElementById("VisValEff").style.display=displayNone;
	//VisValRioLabel.style.display="block";
	document.getElementById("VisValRioLabel").style.display=displayBlock;
	//VisValRio.style.display="block";
	document.getElementById("VisValRio").style.display=displayBlock;
  }
  else{
    //VisValEffLabel.style.display="block";
	document.getElementById("VisValEffLabel").style.display=displayBlock;
	//VisValEff.style.display="block";
	document.getElementById("VisValEff").style.display=displayBlock;
	//VisValRioLabel.style.display="none";
	document.getElementById("VisValRioLabel").style.display=displayNone;
	//VisValRio.style.display="none";
	document.getElementById("VisValRio").style.display=displayNone;
  }
  //Fix12103 Fine RA
}
// Fine 9632


//5543 - PJ
function clickCodificaCommessaCA(checked) {
	enableSearchComponent("CommessaCA", !checked, true);
}

//15938 begin
function abilitaBottoneCreaOrdVen(flag)
{
  if(!isEm(IdCommessaAppartenenza.value))
  {
	document.getElementById("GenOrdVenTR").style.display = displayNone;
  }
  else
  {
 	document.getElementById("GenOrdVenTR").style.display = displayBlock;
	if (flag == "true")
 	{
		document.getElementById("GenOrdVenBUT").disabled = false;
 	}
 	else
    {
      document.getElementById("GenOrdVenBUT").disabled = true;
      //document.getElementById("GenOrdVenBUT").innerHTML= "<IMG border=0 src=\"it/thera/thip/base/commessa/images/OrdVenNotOk.gif\" width=25 height=25>";//31460
      document.getElementById("GenOrdVenBUT").innerHTML= "<IMG border=0 src=\"it/thera/thip/base/commessa/images/OrdVenNotOk.gif\" width=16 height=16>";//31460
    }
  }
}

function creaOrdineVendita()
{
   var commessakey = document.forms[0].thKey.value;
   var className = document.forms[0].thClassName.value;
   var key = document.forms[0].thKey.value;
   runActionDirect('NUOVO_ORDINE','action_submit',className,key,'errorsFrame','no');
}

function apriCreaOrdine(key)
{
   var commessakey = document.forms[0].thKey.value;
   var className = document.forms[0].thClassName.value;
   var url = "/" + webAppPath + "/it/thera/thip/base/commessa/CreaOrdineVendita.jsp?thMode=NEW";
   url +='&ObjectKey=' + URLEncode(key);
   var winName = "newWind" + Math.round(Math.random() * 1000000);
   var winFeature = "width=640, height=450, scrollbars=yes";
   winFeature += ", resizable=yes";
   winFeature += ", toolbar=no, status=yes, menubar=no, modal=yes";
   if(navigator.appName=="Microsoft Internet Explorer")
     winFeature += ", left=200, top=200";
   else
     winFeature += ", screenX=0, screenY=0";
   window.open(url, winName , winFeature);
}

//15938 end
//29960 inizio
function disableAmbienteCommessa(){
	if (!IdAmbienteCommessa.readOnly && IdAmbienteCommessa.value == "") //Fix 31596
	  enableSearchComponent("AmbienteCommessa", false, true);
}
//29960 fine
//Fix 31596 Inizio
function enableAmbienteCommessa(){
//if (IdAmbienteCommessa.readOnly)) //32908
  if (IdAmbienteCommessa.readOnly && isEm(IdCommessaAppartenenza.value)) //32908
    enableSearchComponent("AmbienteCommessa", true, true);
}
//Fix 31596 Fine
//Fix 35382 inizio
function gestioneBudgetCommessaFrame(){
	if(budgetKey != undefined){
		if(budgetKey != '') {
			document.getElementById('BudgetCommessaFrame').style.display = displayBlock;
			document.getElementById('BudgetCommessaFrame').style.overflow = "hidden";
			document.getElementById('BudgetCommessaFrame').scrolling = "no";
			var urlBudget = 'it/thera/thip/produzione/commessa/BudgetCommessaPerCommessa.jsp?thClassName=BudgetCommessa&InitialActionAdapter=it.thera.thip.produzione.commessa.web.BudgetCommessaGridActionAdapter';
			var params = "&Mode=" + modeBudget + "&Key=" + budgetKey;
			document.getElementById('BudgetCommessaFrame').src=urlBudget+params;	
			document.getElementById('TR_CREA_BUDGET').style.display = displayNone;					
		}
		else{
			document.getElementById('BudgetCommessaFrame').style.display = displayNone;
			document.getElementById('TR_CREA_BUDGET').style.display = displayBlock;
		}
	}	
}

function redefineWriteTargetNew(){
	oldWriteTargetNew = writeTargetNew;
    writeTargetNew = function (action, type, classhdr, key, toolbar) {
    	if(action == "NUOVO_BUDGET" || 
		   action == "CURRENT_BUDGET" || 
		   action == "SCOSTAMENTO_BUDGET" || //35837
		   action == "EVOLUZIONE_BUDGET" ||//36460
	   	   action == "EVOLUZIONE_CONSUNTIVI" ||//36460
		   action == "ULTIMA_VARIAZIONE" || 
		   action == "NUOVA_VARIAZIONE" || 
		   action == "ULTIMO_CONSUNTIVO" || 
		   action == "NUOVO_CONSUNTIVO") {
        	document.forms[0].thTarget.value="new";
            createTargetWin("yes",toolbar,"1200","800");
        }
        else
	        oldWriteTargetNew(action, type, classhdr, key, toolbar);
    }
}

function apriCreaBudget(){
	var key = document.getElementById('thKey').value;
	runActionDirect('NUOVO_BUDGET','action_submit','Commessa',key,'new','no');	
}
//Fix 35382 fine

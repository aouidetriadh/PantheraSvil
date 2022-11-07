function ScostamentoBudgetCommessaOL(){
	redefineWriteTargetNew();
	document.getElementById("IdCommessaBudget").style.display=displayNone;
	document.getElementById("IdCommessaConsuntivo").style.display=displayNone;
	carica();
}

function redefineRunActionDirect(){
	oldRunActionDirect = runActionDirect;
	runActionDirect = function (action, type, classhdr, key, target, toolbar) {
		document.getElementById("IdCommessa").disabled=false;
		oldRunActionDirect(action, type, classhdr, key, target, toolbar);
		document.getElementById("IdCommessa").disabled=true;		
	}
}

function carica(){
	parent.clearErrors();	
	var mode = document.getElementById("thMode").value;
	var key = URLEncode(document.getElementById("thKey").value);
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
	var url = "it/thera/thip/produzione/commessa/ScostamentoBudgetCommessaDettaglio.jsp?";
	var params = "mode=" + mode + "&key=" + key + "&myAction=CARICA"+ "&IdAzienda=" + idAzienda + 
				 "&IdCommessa=" + idCommessa + "&IdBudget=" + idBudget + "&IdConsuntivo=" + idConsuntivo + 
				 "&DataRiferimento=" + dataRiferimento;
	params += "&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse+ 
			  "&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;
	url += params;				 
	document.getElementById("ScostamentoBudgetCommessaDettaglio").src=url;
	
	var wListForm =  document.getElementById("WinListForm");
	showForm(wListForm, false);
}

function completaDati(){
	var iIdCommessa = eval("document.forms[0]." + idFromName['IdCommessa']).value;
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.CompletaDatiScostamento?";
	url = url + "Action=COMPLETA_DATI&";
	url = url + "IdCommessa=" + URLEncode(iIdCommessa);
	setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow,url);
}

function individua(){
	var iIdCommessa = eval("document.forms[0]." + idFromName['IdCommessa']).value;
	var iDataRiferimento = eval("document.forms[0]." + idFromName['DataRiferimento']).value;
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.CompletaDatiScostamento?";
	url = url + "Action=INDIVIDUA";
	url = url + "&IdCommessa=" + URLEncode(iIdCommessa);
	url = url + "&DataRiferimento=" + URLEncode(iDataRiferimento);
	setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow,url);
}

function fillCampo(nomeCampo, valore){
	var campo = document.getElementById(nomeCampo);
	if(campo != null)
		campo.value = valore;
}

function createBuilding(){
	var wListFrame =  document.getElementById("winListFrame");		
	var wListForm =  document.getElementById("WinListForm");	

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

function scosValCellRender(ui){
	var rd = ui.rowData;
	var attribute = rd.ScosVal;
	return scostamentoCellRender(attribute)
}

function scosOreCellRender(ui){
	var rd = ui.rowData;
	var attribute = rd.ScosOre;
	return scostamentoCellRender(attribute)
}

function percValCellRender(ui){
	var rd = ui.rowData;
	var attribute = rd.PercVal;
	return scostamentoCellRender(attribute)
}

function percOreCellRender(ui){
	var rd = ui.rowData;
	var attribute = rd.PercOre;
	return scostamentoCellRender(attribute)
}

function scostamentoCellRender(attribute){
	if(attribute != "" && parseFloat(attribute) < parseFloat(0)){
	   	return {style: 'color:blue;font-size:10pt'};
	}
	else if(attribute != "" && parseFloat(attribute) > parseFloat(0)){
		return {style: 'color:red;font-size:10pt'};
	}
	/*else {
	  	return {style: 'color:black;font-size:8pt'};
  	}*/
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

function stampaRiepCommessa(){
	runActionDirect()
}

function stampaRiepCommessa(){
	var dataRif = document.getElementById("DataRiferimento").value;
	var idCommessa = document.getElementById("IdCommessa").value;
	var idConsuntivo = document.getElementById("IdConsuntivo").value;
	var idBudget = document.getElementById("IdBudget").value; 
   	var url = "/" + webAppPath + "/it/thera/thip/produzione/commessa/StampaRiepilogoCommesse.jsp?thMode=NEW";
   	url +='&DataRiferimento=' + URLEncode(dataRif);
   	url +='&IdCommessa=' + URLEncode(idCommessa);
   	url +='&IdConsuntivo=' + URLEncode(idConsuntivo);
   	url +='&IdBudget=' + URLEncode(idBudget);
   	var winName = "newWind" + Math.round(Math.random() * 1000000);
   	var winFeature = "width=800, height=600, scrollbars=yes";
   	winFeature += ", resizable=yes";
   	winFeature += ", toolbar=no, status=yes, menubar=no, modal=yes";
   	if(navigator.appName=="Microsoft Internet Explorer")
    	winFeature += ", left=200, top=200";
   	else
    	winFeature += ", screenX=0, screenY=0";
   	window.open(url, winName , winFeature);
}
var DataEstrazioneStoriciCommessa;
function ConsuntivoCommessaOL(){
	redefineRunActionDirect();
    caricaDati();
}

function fillCampo(nomeCampo, valore){
	var campo = document.getElementById(nomeCampo);
	if(campo != null)
		campo.value = valore;
}

function aggiornaConsuntivoView(){
	var mode = document.getElementById("thMode").value;
	if(mode == "NEW"){
		runActionDirect('SAVE', 'action_submit', document.forms[0].thClassName, null, 'errorsFrame', 'no');				
	}
	else{
		var objectKey = document.getElementById("thKey").value;
		var usaDataEstrazioneStorici = document.getElementById("UsaDataEstrazioneStorici").checked;
		var consolidato = document.getElementById("Consolidato").checked;
		var estrazioneOrdini = document.getElementById("EstrazioneOrdini").checked;
		var estrazioneRichieste = document.getElementById("EstrazioneRichieste").checked;
		
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
		var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.ConsuntivoCommessaDetServlet?";
	
		url = url + "key=" + objectKey + "&myAction=AGGIORNA" + "&IdAzienda=" + idAzienda + "&IdCommessa=" + idCommessa  + "&IdComponenteTotali=" + idComponenteTotali 
				  + "&UsaDataEstrazioneStorici=" + usaDataEstrazioneStorici + "&DataRiferimento=" + dataRiferimento 
				  + "&Consolidato=" + consolidato + "&EstrazioneOrdini=" + estrazioneOrdini + "&EstrazioneRichieste=" + estrazioneRichieste
				  + "&Totali=" + totali + "&DettagliCommessa=" + dettagliCommessa + "&DettagliSottoCommesse=" +  dettagliSottoCommesse
				  + "&ComponentiPropri=" + componentiPropri + "&SoloComponentiValorizzate=" +  soloComponentiValorizzate
				  + "&StatoAvanzamento=" + statoAvanzamento;
		createBuilding();
		//caricaDati();
		setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow,url);
	}
}


function aggiornaConsuntivo(columnDesc, data, costoGenerale, costoIndustriale, costoPrimo, costoRiferimento, tsAgg){
	parent.clearErrors();	
	var wListForm =  parent.document.getElementById('WinListForm');
	parent.showForm(wListForm, false);	
	
	var grid = top.frames["ConsuntivoCommessaDettaglio"].$grid_ConsuntivoCommessaDet.pqGrid("instance");
    grid.option("colModel", columnDesc);
    grid.option("dataModel.data", data);
	grid.colModel[1].render = parent.storiciCellRender;
    grid.refreshDataAndView();              
 
	parent.document.getElementById('CostoGenerale').value = costoGenerale;
	parent.document.getElementById('CostoIndustriale').value=costoIndustriale;
	parent.document.getElementById('CostoPrimo').value = costoPrimo;
	parent.document.getElementById('CostoRiferimento').value = costoRiferimento;
	//parent.document.getElementById('thTimestamp').value = tsAgg;
}




function redefineRunActionDirect(){
	oldRunActionDirect12 = runActionDirect;
	runActionDirect = function (action, type, classhdr, key, target, toolbar)
	{
		var usaDataEstrazioneStorici = document.getElementById("UsaDataEstrazioneStorici").checked;
		document.getElementById("IdCommessa").disabled=false;
		if(usaDataEstrazioneStorici)
			document.getElementById("DataRiferimento").disabled=false;
			
		var usaDataEstrazioneStoriciDis = document.getElementById("UsaDataEstrazioneStorici").disabled;
		if(usaDataEstrazioneStoriciDis){
			document.getElementById("UsaDataEstrazioneStorici").disabled = false;
		}
		var consolidato = document.getElementById("Consolidato").disabled;
		if(consolidato){
			document.getElementById("Consolidato").disabled = false;
		}
		var estrazioneOrdini = document.getElementById("EstrazioneOrdini").disabled;
		if(estrazioneOrdini){
			document.getElementById("EstrazioneOrdini").disabled = false;
		}
		var estrazioneRichieste = document.getElementById("EstrazioneRichieste").disabled;
		if(estrazioneRichieste){
			document.getElementById("EstrazioneRichieste").disabled = false;
		}
		oldRunActionDirect12(action, type, classhdr, key, target, toolbar);
		document.getElementById("IdCommessa").disabled=true;
		if(usaDataEstrazioneStorici)
			document.getElementById("DataRiferimento").disabled=usaDataEstrazioneStorici;
		document.getElementById("Consolidato").disabled = consolidato;
		document.getElementById("EstrazioneOrdini").disabled = estrazioneOrdini;
		document.getElementById("EstrazioneRichieste").disabled = estrazioneRichieste;
		document.getElementById("UsaDataEstrazioneStorici").disabled = usaDataEstrazioneStoriciDis;
	}
}

function caricaDati(){
	//alert("caricadati")
	
	var mode = document.getElementById("thMode").value;
	var key = document.getElementById("thKey").value;
	var usaDataEstrazioneStorici = document.getElementById("UsaDataEstrazioneStorici").checked;
	var consolidato = document.getElementById("Consolidato").checked;
	var estrazioneOrdini = document.getElementById("EstrazioneOrdini").checked;
	var estrazioneRichieste = document.getElementById("EstrazioneRichieste").checked;
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
	var url = "it/thera/thip/produzione/commessa/ConsuntivoCommessaDettaglio.jsp?";
	var params = "mode=" + mode + "&key=" + key + "&myAction=CARICA"+ "&IdAzienda=" + idAzienda + "&IdCommessa=" + idCommessa  
				+ "&IdComponenteTotali=" + idComponenteTotali + "&UsaDataEstrazioneStorici=" + usaDataEstrazioneStorici 
				+ "&DataRiferimento=" + dataRiferimento + "&Consolidato=" + consolidato + "&EstrazioneOrdini=" + estrazioneOrdini 
				+ "&EstrazioneRichieste=" + estrazioneRichieste + "&StatoAvanzamento=" + statoAvanzamento;
			  
	params += "&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse+ 
				 "&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;
	url += params;
	
	setLocationOnWindow(document.getElementById("ConsuntivoCommessaDettaglio").contentWindow, url);
		
	var wListForm =  document.getElementById("WinListForm");
	showForm(wListForm, false);
	
}


function gestioneUsaDataEstrazioneStorici(){
	var usaDataEstrazioneStorici = document.getElementById("UsaDataEstrazioneStorici").checked;
	var statoAvanzamento = document.getElementById("StatoAvanzamento").value;
	if(!usaDataEstrazioneStorici){
		document.getElementById("DataRiferimento").disabled=false;
		document.getElementById("thCalButtonDataRiferimento").disabled=false;
		document.getElementById("DataRiferimento").typeNameJS.mandatory=true;
		document.getElementById("DataRiferimento").style.background=mCo;	
		if(statoAvanzamento == '2'){
			document.getElementById("DataRiferimento").disabled = true;
			document.getElementById("DataRiferimento").style.background=bCo;
		}
		
		document.getElementById("EstrazioneOrdini").disabled=true;
		document.getElementById("EstrazioneOrdini").checked=false;
		document.getElementById("EstrazioneRichieste").disabled=true;
		document.getElementById("EstrazioneRichieste").checked=false;
	}
	else{
		if(DataEstrazioneStoriciCommessa != undefined && statoAvanzamento == '1')
			document.getElementById("DataRiferimento").value=DataEstrazioneStoriciCommessa;
		document.getElementById("DataRiferimento").disabled=true;
		document.getElementById("thCalButtonDataRiferimento").disabled=true;		
		document.getElementById("DataRiferimento").style.background=bCo;
		if(statoAvanzamento != '2'){
			document.getElementById("EstrazioneOrdini").disabled=false;
			document.getElementById("EstrazioneRichieste").disabled=false;
		}
	}
}

function completaDatiConsuntivo(){
	var iIdCommessa = eval("document.forms[0]." + idFromName['IdCommessa']).value;
	var url = "/" + webAppPath  + "/" + servletPath +  "/it.thera.thip.produzione.commessa.web.CompletaDatiConsuntivo?";
	url = url + "Action=COMPLETA_DATI&";
	url = url + "IdCommessa=" + URLEncode(iIdCommessa);
	setLocationOnWindow(document.getElementById(errorsFrameName).contentWindow,url);
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

function storiciCellRender(ui){
	var rowData = ui.rowData;
	if(rowData['CmpElem'] == "Y"){
		return "<img src=\"/" + webAppPath + "/it/thera/thip/produzione/commessa/images/editRow.gif\" onclick=\"apriStoriciCommessa('" + rowData['IdCommessa'] + "','" + rowData['IdCmpCosto'] + "','" + rowData['ClassName'] + "')\" />";
	}
	return "";		
}

function apriStoriciCommessa(idCommessa, idCmpCosto, className){
	var addInfo ="&thAdditionalInfo=ValorizzaCosto=true;documenti=true;richieste=" + eval("parent.getCheckValue(\"EstrazioneRichieste\")") + ";ordini=" + eval("parent.getCheckValue(\"EstrazioneOrdini\")");
	var dataRif = parent.document.getElementById("DataRiferimento").value;
	var url = "/" + webAppPath +"/" + servletPath +"/" +
			  "com.thera.thermfw.web.servlet.Execute?";
	var params = "ClassName=" + className + "&thSpecificDOList=it.thera.thip.produzione.commessa.web.StoricoCommessaDOList&thGridType=list&thRestrictConditions=IdComponenteCosto=" + idCmpCosto + ";IdCommessa=" + idCommessa + ";DataOrigine=" + dataRif + ";"+ addInfo;
	runLink(url+params);
}

function runLink(url) {
	winName = "newWind" + Math.round(Math.random() * 1000000);
	var width = 800;
	var height = 600;
	winFeature = "width="+width+", height="+height+", scrollbars=yes";
    winFeature += ", resizable=yes";
	winFeature += ", toolbar=no, status=yes, menubar=no";
	if(navigator.appName=="Microsoft Internet Explorer")
		winFeature += ", left=0, top=0";
	else
		winFeature += ", screenX=0, screenY=0";
	var win = window.open(url, winName, winFeature);
}

function clearErrors(){
	var errViewObj = eval(errorsViewName);
	errViewObj.clearDisplay();	
}
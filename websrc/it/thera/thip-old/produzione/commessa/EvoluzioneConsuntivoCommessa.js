function EvoluzioneConsunCommessaOL(){
	//redefineWriteTargetNew();
	carica();
}

function carica(){
	var idCommessa =  URLEncode(document.getElementById("IdCommessa").value);
	if(idCommessa.length == 0)
		return;
	parent.clearErrors();	
	var mode = document.getElementById("thMode").value;
	var key = URLEncode(document.getElementById("thKey").value);
	var dataInizio =  URLEncode(document.getElementById("DataInizio").value);
	var dataFine =  URLEncode(document.getElementById("DataFine").value);
	var idAzienda =  URLEncode(document.getElementById("IdAzienda").value);
	var totali =  document.getElementById("Totali").checked;
	var dettagliCommessa =  document.getElementById("DettagliCommessa").checked;
	var dettagliSottoCommesse =  document.getElementById("DettagliSottoCommesse").checked;
	var componentiPropri =  document.getElementById("ComponentiPropri").checked;
	var soloComponentiValorizzate =  document.getElementById("SoloComponentiValorizzate").checked;
	var url = "it/thera/thip/produzione/commessa/EvoluzioneConsuntivoCommessaDettaglio.jsp?";
	var params = "mode=" + mode + "&key=" + key + "&myAction=CARICA"+ "&IdAzienda=" + idAzienda + 
				 "&IdCommessa=" + idCommessa +
				 "&DataInizio=" + dataInizio + "&DataFine=" + dataFine;
	params += "&Totali=" + totali+ "&DettagliCommessa=" + dettagliCommessa+ "&DettagliSottoCommesse=" + dettagliSottoCommesse+ 
			  "&ComponentiPropri=" + componentiPropri+ "&SoloComponentiValorizzate=" + soloComponentiValorizzate;
	url += params;				 
	document.getElementById("EvoluzioneConsuntivoCommessaDettaglio").src=url;
	
	var wListForm =  document.getElementById("WinListForm");
	showForm(wListForm, false);
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





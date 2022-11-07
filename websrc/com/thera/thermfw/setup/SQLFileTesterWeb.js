//35912 inizio
function SQLFileTesterWebOL(){
	manageGenerateShortNames();
}
//35912 fine

function runSQLFileTester(){
  var errViewObj = eval(window.errorsViewName);
  errViewObj.clearDisplay();
  //Fix 12101 inizio
  /*
  if (document.outputArea.internalArea!=undefined)
     document.outputArea.internalArea.value="attendere...";
  */
  var outputAreaWin = document.getElementById('outputArea').contentWindow;
  if (outputAreaWin.document.getElementById('internalArea') != null){
    outputAreaWin.document.getElementById('internalArea').value = "attendere...";
  }
  //Fix 12101 fine
  runActionDirect('SAVE','action_submit',document.forms[0].thClassName.value, document.forms[0].thKey.value,'outputArea','no')
}

function clearIframe(){
  //document.outputArea.innerText="";
  setInnerText(document.getElementById('outputArea'), "");
}
//35912 inizio
function manageGenerateShortNames(){
	var platformType = eval("document.forms[0]." + idFromName["PlatformType"]);
	if(platformType.value == "A"){
		document.getElementById("GenerateShortNames").disabled=false;
	}
	else{
		document.getElementById("GenerateShortNames").checked=false;
		document.getElementById("GenerateShortNames").disabled=true;
	}
}
//35912 fine
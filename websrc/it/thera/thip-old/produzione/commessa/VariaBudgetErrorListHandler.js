//CloseWindow
function closeWindow() {
  if (typeof parent.enableFormActions != "undefined") //Mod.2593
    parent.enableFormActions(); //Mod.2122

  if (numErr > 0 && (foundErrors || foundErrorsForzabili)){
    //Visualizza Errori
	runErrors();
    if (numErr > 0 && !foundErrors && foundErrorsForzabili){
      runErrorsForceable();
    }
  }
  else {
	if (numErr > 0 && !(foundErrors || foundErrorsForzabili))
	  //Visualizza Warnings
	  runWarnings();
	var indipendentForm = false;
	var destination;
	var isTargetNew = true;
	var origin = window.parent;
	if (thAction == "SAVE" || thAction == "SAVE_AND_CLOSE" || thAction == "SAVE_AND_NEW" || thAction == "WF_CHANGE_STATUS") {
      //Oggetto Salvato Correttamente
	  destination = origin.opener;
      if (destination == null) {
	    destination = origin.parent.frames[0];
	    isTargetNew = false;
      }
	  runObjSaved(origin, destination);
	} else if (thAction=="CHECK_ALL") {
	  //Risultato della Check
	  runCheckResult();
	}

	//Reload Form
	if (origin.indipendentRowForm)
	  indipendentForm = true;
	if (!indipendentForm)
	  //Normal Form
	  reloadForNormalForm();
	else
	  //IndipendentForm
	  reloadForIndipendentForm(destination, isTargetNew);
  }
}

function runErrorsForceable(){
  var undefined;
  var msgInt = "_____________________________________________________\n\n";
  var msgFin = "\n\n_____________________________________________________";
  var msgText = "\n\nSi desidera forzare gli errori segnalati ?";
  if(parent.msgForcedError){
	  msgText = "\n\n" + parent.msgForcedError;	  
  }	 	  
  var msgErrForz = "";
  for (var i=0;i<errorsArray.length;i++){
    var errId = errorsArray[i][0];
	var errShortText = errorsArray[i][1];
	var idComp = errorsArray[i][2];
	var errSeverity = errorsArray[i][3];
	var errLongText = errorsArray[i][4];
	var isForceable = errorsArray[i][5];
	var errGrpName = errorsArray[i][6];
	var errLabel = errorsArray[i][7];
	errGrpName =  formatErrGroupLabel(errGrpName);
	errLabel = formatErrGroupLabel(errLabel);
	if (errLabel != "")
	  msgErrForz += errLabel+'\n'+'\t'+errId+' - '+errLongText+"\n";
	else
	  msgErrForz += errId+' - '+errLongText+"\n";
  }
  var ok = confirm(msgErrForz+msgText);
  if (ok){
	eval(parent.document.forms[0].thCodErrorsForced).value = codErrorsForced;
	parent.runActionDirect(thAction,'action_submit',thClassName,thKey,'errorsFrame','no');
  }
  else{
	if(errId == "THIP_TN806"){
		eval(parent.document.forms[0].StatoAvanzamento).value = '1';
		parent.runActionDirect(thAction,'action_submit',thClassName,thKey,'errorsFrame','no');
	}
	else{
		eval(parent.document.forms[0].thCodErrorsForced).value ="";
		errViewObj.setMessage(null);
		return;
	}	
  }
}


function formatErrGroupLabel(textErr){
  if (textErr != "")
    textErr+=' - ';
  else
	textErr="";
	return textErr;
}

//Azione nel caso di Errors
function runErrors() {
  setAllValues();
}

//Azione nel caso di Warning
function runWarnings() {
  window.parent.alert(warningMessage);
}

//Azione nel caso di salvataggio corretto
function runObjSaved(origin, destination) {
  setSavedMessage();
  if (destination != null) {
	if (eval("origin.callUpdateGridForSave"))
	  runUpdateGridForSave(origin, destination, thKey);
  }

  if (destination && !destination.closed)
	runCallBack(origin, destination);
}

//Azione di Update della Griglia dopo Save
function runUpdateGridForSave(origin, destination, thKey) {
  origin.callUpdateGridForSave(destination, thKey);
}

//Azione di CallBack
function runCallBack(origin,destination) {
	//12411inizio
  try
  {
  	var callBack = eval("destination.saveCallBack");
  	if (callBack)
    	callBack(origin, thKey);
  }
  catch(e)
  {}
	//fine12411
}

//Azione nel caso di checkAll
function runCheckResult() {
  setAllValues();
}

//Azione di ReloadForNormalForm
function reloadForNormalForm() {
	window.parent.reloadForm(thAction, thClassName, thKey);
}

//Azione di ReloadForIndipendentForm
function reloadForIndipendentForm(destination, isTargetNew) {
	window.parent.conflictClosed = true;

	if(thAction == "SAVE_AND_CLOSE" && isTargetNew)
	{
		window.parent.close();
		return;
	}
	else if (thAction == "CHECK_ALL")
	{
		errViewObj.setMessage(window.parent.checkMsg);
		window.parent.conflictClosed = false;
		return;
	}

	var doReload = eval("destination.doReload");
	if(doReload)
	{
		var targetValue = "detail";
		if(isTargetNew)
			targetValue = window.parent.name;
		var actionRequest = "UPDATE";
		if(thAction == "SAVE_AND_NEW")
			actionRequest = "NEW";
		else if(thAction == "SAVE_AND_CLOSE")
			actionRequest = "CLOSE";
		doReload(window.parent, thKey, targetValue, actionRequest);
	}
}

// -------------------------------------------------------------------------------------

function setAllValues()
{
	errViewObj.addErrorsAsArray(errorsArray, parent.document.forms[0].elements)
	paintComponentsInError();
}

function setSavedMessage()
{
	errViewObj.setMessage(objSavedMessage);
}

function paintComponentsInError()
{
	window.parent.resetAllFieldsColor();
	var c = null;
	for(i = 0; i < allComponentsInError.length; i++)
	{
		if(allComponentsInError[i].indexOf(".") != -1)
		{
			var elem = window.parent.document.forms[0].elements;
			for(var j = 0; j < elem.length; j++)
			{
				var id;
				if(!window.parent.isEm(elem[j].name))
					id = elem[j].name;
				else
					id = elem[j].id;
				if(id == allComponentsInError[i])
				{
					c = elem[j];
					break;
				}
			}
		}
		else 
			c = eval("window.parent.document.forms[0]." + allComponentsInError[i]);

		if(c != null)
		{
			//c.style.background = window.parent.eCo;//Mod10640
			if(c.length == undefined || c.type=="select-one") //Fix 10522
				c.style.backgroundColor = window.parent.eCo;  
			else
			{
				for(var k = 0; k < c.length; k++)
				{
					c.item(k).style.backgroundColor = window.parent.eCo;
				}
			}
		}
	}
	
	for(i = 0; i < collectionInError.length; i++)
	{
		var elem = collectionInError[i].replace(".","$");
		var curGrid = window.parent.editGrid[elem];
		if (curGrid != null)
		{
			var j = collectionInErrorPos[i];
			curGrid.rows[j].isInError=true;
			if(j >= curGrid.firstRow && j < curGrid.firstRow + curGrid.visibleRows)
				window.parent.showRowStatus(elem, j - curGrid.firstRow, curGrid.rows[j].status);
		}
		// Fix 19116 inizio
		var classCD = collectionInError[i];		
		var ajaxGridCmp = eval("window.parent.thWebAjaxGrid_"+classCD);
		if(ajaxGridCmp != undefined) {
			parent.pageRequest(ajaxGridCmp.value, false);
		}
		// Fix 19116 fine		
	}
}


var oldRunActionWithTargetNew = runActionWithTargetNew;
runActionWithTargetNew = function a(action, sensibility, target, toolbar) {
	if(action == "CURRENT_BUDGET" || 
	   action == "SCOSTAMENTO_BUDGET" ||//35837
	   action == "EVOLUZIONE_BUDGET" ||//36460
	   action == "EVOLUZIONE_CONSUNTIVI" ||//36460
	   action == "ULTIMA_VARIAZIONE" || 
	   action == "NUOVA_VARIAZIONE" || 
       action == "ULTIMO_CONSUNTIVO" || 
	   action == "NUOVO_CONSUNTIVO") {
		objectFormWidth=1200;	
		objectFormHeight=800;
	}
	oldRunActionWithTargetNew(action, sensibility, target, toolbar);	
}

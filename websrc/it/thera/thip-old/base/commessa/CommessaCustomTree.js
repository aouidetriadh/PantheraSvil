/**
 * Override of addNewNode to modify the classHdr name in case of sottocommessa to load the right menu
 */
function addNewNode(parentNode, origin, objectKey) {
	var classHdr = eval("origin.document.forms[0].thClassName.value");
	var newNodeClassCD = eval("origin.document.forms[0].thGridCDName.value");
	if(newNodeClassCD == "Sottocommesse")
		classHdr = "Sottocommessa"
	if(newNodeClassCD == "")
		newNodeClassCD = null;
	var descValue = getDescValue(origin);
	var sAction = "displayMenu(" + nodeProperties[classHdr].menuIndex + ")";
	var newNode = new WebFXTreeItem(classHdr, objectKey, objectKey, descValue, sAction, null, true, newNodeClassCD);
	parentNode.add(newNode);
	newNode.select();
	return newNode;
}

/**
 * Load the commessa form in right panel according to the key parameter
 */
function loadCommessa(cmmKey) {
	var node = getNodeByKey(cmmKey);
	webFXTreeHandler.selected = node;
	doMenuAction("Sottocommessa", "UPDATE", "detail");
	expandNode(node);
	webFXTreeHandler.selected = getRooNode(node);
	node.select();
}

/**
 * returns the node according to the key parameter
 */
function getNodeByKey(key) {
	for(i = 0; i < webFXTreeHandler.idCounter; i++){
		var node = webFXTreeHandler.all[webFXTreeHandler.idPrefix + i];
		if(node.objectKey == key)
			return node;
	}
}

/**
 * Expand the node 
 */
function expandNode(node) {
	var parentNode = node.parentNode;
	while(parentNode != null){
		if(!parentNode.open)
			parentNode.expand();
		parentNode = parentNode.parentNode;
	}
}

/**
 * Returns the root node
 */
function getRooNode(node) {
	var parentNd = node;
	while(parentNd.parentNode != null)
		parentNd = parentNd.parentNode;
	return parentNd;
}

function setSelectedNode(nodeKey) {
	var node = getNodeByKey(nodeKey);
	if(node){
		node.select();
		webFXTreeHandler.selected = node;
	}
}

var oldSaveCallBack = saveCallBack;
saveCallBack = function mySaveCallBack(origin, objectKey) {
	//35382 inizio
	if(objectKey.split(top.fsep).length == 3){
		var urlBudget = 'it/thera/thip/produzione/commessa/BudgetCommessaPerCommessa.jsp?thClassName=BudgetCommessa&InitialActionAdapter=it.thera.thip.produzione.commessa.web.BudgetCommessaGridActionAdapter';
		var params = "&Mode=UPDATE&Key=" + objectKey;		
		top.frames['tree'].frames['detail'].frames['BudgetCommessaFrame'].location="/" + top.frames['tree'].frames['detail'].webAppPath + "/" +  urlBudget + params;
		objectKey=origin.parentKey;
	}
	else{
	//35382 fine
	oldSaveCallBack(origin, objectKey);
	var sessionTreeName = top.frames['tree'].frames['tree'].KeyCommessaTree;
	top.frames['tree'].frames['tree'].location = "/" + top.frames['tree'].frames['detail'].webAppPath + "/it/thera/thip/base/commessa/CommessaTreeLeft.jsp?KeyCommessaTree=" + sessionTreeName + "&ReloadTree=true&Key=" + getRooNode(webFXTreeHandler.selected).objectKey;
	//setSelectedNode(objectKey);
	}
}
//35382 inizio
var oldDoReload = doReload;
doReload = function myDoReload(origin, realObjectKey, target, actionRequest) {
	if(realObjectKey.split(top.fsep).length == 3){
		realObjectKey=origin.parentKey;
		top.frames['tree'].frames['detail'].eval(top.frames['tree'].frames['detail'].errorsViewName).clearDisplay();
	}
	else{
		oldDoReload(origin, realObjectKey, target, actionRequest);
	}	
}
//35382 fine
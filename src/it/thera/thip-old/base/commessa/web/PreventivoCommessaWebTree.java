package it.thera.thip.base.commessa.web;

import java.util.Iterator;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.ad.ClassAD;
import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.ad.ClassADCollectionManager;
import com.thera.thermfw.ad.ClassCD;
import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.clipboard.ClipboardManager;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.Factory;//30659
import com.thera.thermfw.type.DecimalType;
import com.thera.thermfw.web.WebForm;
import com.thera.thermfw.web.WebGenerator;
import com.thera.thermfw.web.WebJSTypeList;
import com.thera.thermfw.web.WebMenuItem;
import com.thera.thermfw.web.WebNodeProperties;
import com.thera.thermfw.web.WebTreeMenu;
import com.thera.thermfw.web.WebTreeMenuItem;

import it.thera.thip.base.commessa.PreventivoCommessaRiga;
import it.thera.thip.base.commessa.PreventivoCommessaTestata;
import it.thera.thip.base.commessa.PreventivoCommessaVoce;
import it.thera.thip.datiTecnici.distinta.web.BaseEsplosioneNodoWebTree;
/**
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh
 */
/**
 * Revisions:
 * Number   Date          Owner   Description
 * 29166    22/04/2019    RA      Prima Struttura
 * 29529    26/06/2019    RA	  Rivisione Preventivo commessa : celle editabile
 * 29642	17/07/2019	  RA	  Rivisione Preventivo commessa : aggiunto colonne Nota e Stato alla griglia
 * 29731	20/08/2019	  RA      Rivisione Preventivo commessa : varie modifiche
 * 29882 	25/09/2019	  RA	  Aggiunto l'azione di "expandAll" e "collapseAll" alle nodi di tipo commessa / sottocommessa.
 * 29959    10/10/2019    Mekki   Seleziona multipla e reorganizzazione menu
 * 30223    18/11/2019    DB      Ganci personalizzazioni
 * 30327    06/12/2019    DB      Ganci personalizzazioni
 * 30659	06/02/2020	  RA	  Modifica metodo boAttributeA affinchè resituisca il descrittore tramite Factory
 * 33594	21/05/2021	  RA	  Aggiunto gestion clipboard per preventivo commesse voce di tipo risorse 
 */
public class PreventivoCommessaWebTree extends BaseEsplosioneNodoWebTree {
	
	public PreventivoCommessaWebTree() {
	}

	public String classAdCollectionName() {
		return "PreventivoCommessaRiga";
	}
	
	public String jsVarPrefisso() {
		return "PreventivoCommessaTestata";
	}
	
    public String boAttributeA() {
        //return "PreventivoCommessaTestata";//30659
        return Factory.getName("PreventivoCommessaTestata", Factory.CLASS_HDR);//30659
    }
    
    public String boAttributeB() {
        return "RigheCommesse";
    }
	
    // fix 30223
	protected void creaMenuPersRighe(WebTreeMenu hdrMenu, ClassADCollection classHdr) throws Exception {
		
	}
	protected void createMenuPersMulti(WebTreeMenu multiSelectionMenu) throws Exception {
	}
    
	protected void creaMenuPersRigheDet(WebTreeMenu hdrMenu, ClassADCollection classHdr){
		
	}
    // fin fix 30223
	protected void createMenus(ClassADCollection classHdr, boolean isRoot) throws Exception {

		if (getMenu(classHdr.getOriginalClassAdCollectionName()) != null) 
			return;

		if (isRoot && headerInternal && parent != null) {
			ownerForm = getOwnerForm();
			if (ownerForm.getMode() == WebForm.NEW || ownerForm.getMode() == WebForm.COPY)
				return;
		}
		
		WebNodeProperties np = (WebNodeProperties) nodePropertyList.get(classHdr.getOriginalClassAdCollectionName()); 
		WebTreeMenu hdrMenu = new WebTreeMenu(classHdr.getOriginalClassAdCollectionName()); 
		addMenu(classHdr.getOriginalClassAdCollectionName(), hdrMenu); 	
		for (int i = 0;(np.getSonCDStrings() != null) && (i < np.getSonCDStrings().length); i++) {
			ClassCD sonCD = classHdr.getComponent(np.getSonCDStrings()[i]);
			String sonHdr = sonCD.getComponentHDR().getOriginalClassAdCollectionName();

			WebNodeProperties sonNP = getNodeProperty(sonHdr);
			if(!(np.getSonCDStrings()[i]).equals("Risorse")){
				WebTreeMenuItem newMenuItem = new WebTreeMenuItem("new" + np.getSonCDStrings()[i],
						"&nbsp;" + sonCD.getComponentNameNLS(),
						sonCD.getComponentHDR().getOriginalClassAdCollectionName(), 
						"NEW_" + np.getSonCDStrings()[i],
						WebTreeMenuItem.TARGET_NEW, 
						"thermweb/image/gui/cnr/New.gif");
				newMenuItem.setClassCD(np.getSonCDStrings()[i]);
								
				if (!np.isGroupSonCD()) {
					if (parent == null || ownerForm.getMode() != WebForm.SHOW) {
						hdrMenu.addMenuItem(newMenuItem);
					}
				}
				else {
					String groupName = getGroupName(classHdr.getOriginalClassAdCollectionName(), sonCD.getComponentName());
					WebTreeMenu groupMenu = new WebTreeMenu(groupName);
					groupMenu.addMenuItem(new WebTreeMenuItem(newMenuItem));
					if (parent == null || ownerForm.getMode() != WebForm.SHOW)
						addMenu(groupName, groupMenu);
				}
			}
			else{
				WebTreeMenuItem newMenuItemRM = new WebTreeMenuItem("new" + np.getSonCDStrings()[i], 
						//"&nbsp;" + sonCD.getComponentNameNLS() + " Macchina",
						"&nbsp;" + ResourceLoader.getString("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "Macchina"),//29731
						sonCD.getComponentHDR().getOriginalClassAdCollectionName(), 
						"NEW_" + (np.getSonCDStrings()[i]).toUpperCase() + "_MACCHINA",
						WebTreeMenuItem.TARGET_NEW, 
						"thermweb/image/gui/cnr/New.gif");
				newMenuItemRM.setClassCD(np.getSonCDStrings()[i]);
				
				WebTreeMenuItem newMenuItemRU = new WebTreeMenuItem("new" + np.getSonCDStrings()[i],
						//"&nbsp;" + sonCD.getComponentNameNLS() + " Uomo",
						"&nbsp;" + ResourceLoader.getString("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "Uomo"),//29731
						sonCD.getComponentHDR().getOriginalClassAdCollectionName(), 
						"NEW_" + (np.getSonCDStrings()[i]).toUpperCase() + "_UOMO",
						WebTreeMenuItem.TARGET_NEW, 
						"thermweb/image/gui/cnr/New.gif");
				newMenuItemRU.setClassCD(np.getSonCDStrings()[i]);
				if (!np.isGroupSonCD()) {
					if (parent == null || ownerForm.getMode() != WebForm.SHOW) {
						hdrMenu.addMenuItem(newMenuItemRM);
						hdrMenu.addMenuItem(newMenuItemRU);
					}
				}
				else {
					String groupName = getGroupName(classHdr.getOriginalClassAdCollectionName(), sonCD.getComponentName());
					WebTreeMenu groupMenu = new WebTreeMenu(groupName);
					groupMenu.addMenuItem(new WebTreeMenuItem(newMenuItemRM));
					groupMenu.addMenuItem(new WebTreeMenuItem(newMenuItemRU));
					if (parent == null || ownerForm.getMode() != WebForm.SHOW)
						addMenu(groupName, groupMenu);
				}
			}

			createMenus(sonCD.getComponentHDR(), false);
		}
		
		//Fix 29959 --inizio
		for (int i = 0;(np.getSonCDStrings() != null) && (i < np.getSonCDStrings().length); i++) {
			ClassCD sonCD = classHdr.getComponent(np.getSonCDStrings()[i]);
			String sonHdr = sonCD.getComponentHDR().getOriginalClassAdCollectionName();

			WebNodeProperties sonNP = getNodeProperty(sonHdr);

			if(!(np.getSonCDStrings()[i]).equals("Risorse")){//33594
				WebTreeMenuItem createFromClipboard = new WebTreeMenuItem("createFromClipboard" + np.getSonCDStrings()[i],	
																		  "&nbsp;" + sonCD.getComponentNameNLS(),
																		  sonCD.getComponentHDR().getOriginalClassAdCollectionName(), 
																		  ClipboardManager.ACTION_CREATE_FROM_CLIPBOARD,
																		  WebTreeMenuItem.TARGET_INFO_AREA, 
																		  "com/thera/thermfw/clipboard/css/images/clipboard-paste.png");
				createFromClipboard.setClassCD(np.getSonCDStrings()[i]);

				if (!np.isGroupSonCD()) {
					if (parent == null || ownerForm.getMode() != WebForm.SHOW) {
						//hdrMenu.addMenuItem(newMenuItem);
						if(ClipboardManager.existsClipboardCfgWithHdrDest(sonHdr)==true) {
							hdrMenu.addMenuItem(createFromClipboard);
						}
					}
				}
				else {
					String groupName = getGroupName(classHdr.getOriginalClassAdCollectionName(), sonCD.getComponentName());
					WebTreeMenu groupMenu = new WebTreeMenu(groupName);
					//groupMenu.addMenuItem(new WebTreeMenuItem(newMenuItem));
					if(ClipboardManager.existsClipboardCfgWithHdrDest(sonHdr)==true) {
						hdrMenu.addMenuItem(createFromClipboard);
					}
					if (parent == null || ownerForm.getMode() != WebForm.SHOW)
						addMenu(groupName, groupMenu);
				}
			//33594 inizio
			}
			else {
				WebTreeMenuItem createFromClipboard = new WebTreeMenuItem(
						"createFromClipboard" + np.getSonCDStrings()[i],
						"&nbsp;" + ResourceLoader.getString("it.thera.thip.base.commessa.resources.PreventivoCommessaVoce", "Uomo"),
						sonCD.getComponentHDR().getOriginalClassAdCollectionName(),
						ClipboardManager.ACTION_CREATE_FROM_CLIPBOARD, WebTreeMenuItem.TARGET_INFO_AREA,
						"com/thera/thermfw/clipboard/css/images/clipboard-paste.png");
				createFromClipboard.setClassCD(np.getSonCDStrings()[i]);

				if (!np.isGroupSonCD()) {
					if (parent == null || ownerForm.getMode() != WebForm.SHOW) {
						if (ClipboardManager.existsClipboardCfgWithHdrDest(sonHdr) == true) {
							hdrMenu.addMenuItem(createFromClipboard);
						}
					}
				} else {
					String groupName = getGroupName(classHdr.getOriginalClassAdCollectionName(),
							sonCD.getComponentName());
					WebTreeMenu groupMenu = new WebTreeMenu(groupName);
					if (ClipboardManager.existsClipboardCfgWithHdrDest(sonHdr) == true) {
						hdrMenu.addMenuItem(createFromClipboard);
					}
					if (parent == null || ownerForm.getMode() != WebForm.SHOW)
						addMenu(groupName, groupMenu);
				}
			}			
			//33594 fine	
			createMenus(sonCD.getComponentHDR(), false);
		}		
		
		if (isRoot && headerInternal) {
			if (parent == null || ownerForm.getMode() != WebForm.SHOW)
				hdrMenu.addMenuItem(new WebTreeMenuItem("update",
						"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "Open"),
						classHdr.getOriginalClassAdCollectionName(), 
						"UPDATE",
						WebTreeMenuItem.TARGET_NEW,
						"thermweb/image/gui/cnr/Open.gif"));

			hdrMenu.addMenuItem(new WebTreeMenuItem("view",
					"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "View"),
					classHdr.getOriginalClassAdCollectionName(), 
					"SHOW",
					WebTreeMenuItem.TARGET_NEW, 
					"thermweb/image/gui/cnr/View.gif"));
		}
		if (isRoot) {
			hdrMenu.addMenuItem(new WebTreeMenuItem("expandAll",
					"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "ExpandAll"),
					classHdr.getOriginalClassAdCollectionName(),
					"EXPANDALL",
					WebTreeMenuItem.TARGET_INFO_AREA,
					"thermweb/image/gui/cnr/tree-expand.png"));
			hdrMenu.addMenuItem(new WebTreeMenuItem("collapseAll",
					"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "CollapseAll"),
					classHdr.getOriginalClassAdCollectionName(), 
					"COLLAPSEALL",
					WebTreeMenuItem.TARGET_INFO_AREA,
					"thermweb/image/gui/cnr/tree-collapse.png"));
		}		
		//Fix 29959 --fine
			
		if (!isRoot)
			createModifyMenu(np, hdrMenu, classHdr);
		
		//Fix 29959 --inizio
		for (int i = 0;(np.getSonCDStrings() != null) && (i < np.getSonCDStrings().length); i++) {
			ClassCD sonCD = classHdr.getComponent(np.getSonCDStrings()[i]);
			String sonHdr = sonCD.getComponentHDR().getOriginalClassAdCollectionName();

			WebNodeProperties sonNP = getNodeProperty(sonHdr);
			if(!isRoot && (np.getSonCDStrings()[i]).equals("RigheCommesse") || (np.getSonCDStrings()[i]).equals("SottoCommesse") || (np.getSonCDStrings()[i]).equals("Righe")){ //Fix 29959
				hdrMenu.addMenuItem(new WebTreeMenuItem("expandAll",
						"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "ExpandAll"),
						classHdr.getOriginalClassAdCollectionName(),
						"EXPANDALL",
						WebTreeMenuItem.TARGET_INFO_AREA,
						"thermweb/image/gui/cnr/tree-expand.png"));
				hdrMenu.addMenuItem(new WebTreeMenuItem("collapseAll",
						"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "CollapseAll"),
						classHdr.getOriginalClassAdCollectionName(), 
						"COLLAPSEALL",
						WebTreeMenuItem.TARGET_INFO_AREA,
						"thermweb/image/gui/cnr/tree-collapse.png"));
				
				creaMenuPersRighe(hdrMenu, classHdr);  // fix 30223
				
				
			}
		}
		//Fix 29959 --fine
		//Fix 29959 --inizio //Seleziona Multipla
		WebTreeMenu multiSelectionMenu = new WebTreeMenu("MultiSelection");
		addMenu("MultiSelection", multiSelectionMenu); 			
		WebTreeMenuItem menuItemMultiSelection = menuItemMultiSelection = new WebTreeMenuItem("delete",
				"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "Delete"),
				"", 
				"DELETE_MULTI_SELECTION",
				WebTreeMenuItem.TARGET_INFO_AREA,
				"thermweb/image/gui/cnr/Delete.gif");
		multiSelectionMenu.addMenuItem(menuItemMultiSelection);
		
		WebTreeMenuItem addToClipboardMultiSelection = new WebTreeMenuItem("addToClipboard", 
																														"&nbsp;" + ResourceLoader.getString("com.thera.thermfw.clipboard.resources.Clipboard", "clipboard"),
																														"", 
																														ClipboardManagerMultiSelection.ADD_TO_CLIPBOARD_MUTLI_SELECTION, 
																														WebTreeMenuItem.TARGET_INFO_AREA, 
																														"com/thera/thermfw/clipboard/css/images/clipboard-copy.png");
		multiSelectionMenu.addMenuItem(addToClipboardMultiSelection);
		
		createMenuPersMulti(multiSelectionMenu);  // fix 30223
		
		multiSelectionMenu.addMenuItem(new WebTreeMenuItem("expandAll",
				"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "ExpandAll"),
				classHdr.getOriginalClassAdCollectionName(),
				"EXPANDALL",
				WebTreeMenuItem.TARGET_INFO_AREA,
				"thermweb/image/gui/cnr/tree-expand.png"));
		multiSelectionMenu.addMenuItem(new WebTreeMenuItem("collapseAll",
				"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "CollapseAll"),
				classHdr.getOriginalClassAdCollectionName(), 
				"COLLAPSEALL",
				WebTreeMenuItem.TARGET_INFO_AREA,
				"thermweb/image/gui/cnr/tree-collapse.png"));
		
		
		//Fix 29959 --fine		
	}
	
	protected void createModifyMenu(WebNodeProperties np, WebTreeMenu hdrMenu, ClassADCollection classHdr){
		WebTreeMenuItem menuItem;
		if (parent == null || ownerForm.getMode() != WebForm.SHOW) {
			menuItem = new WebTreeMenuItem("update",
					"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "Open"),
					classHdr.getOriginalClassAdCollectionName(), 
					"UPDATE",
					WebTreeMenuItem.TARGET_NEW, 
					"thermweb/image/gui/cnr/Open.gif");
			hdrMenu.addMenuItem(menuItem);
		}

		menuItem = new WebTreeMenuItem("view",
				"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "View"),
				classHdr.getOriginalClassAdCollectionName(), 
				"SHOW",
				WebTreeMenuItem.TARGET_NEW,
				"thermweb/image/gui/cnr/View.gif");
		hdrMenu.addMenuItem(menuItem);
		
		if (parent == null || ownerForm.getMode() != WebForm.SHOW) {
			menuItem = new WebTreeMenuItem("copy",
					"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "Copy"),
					classHdr.getOriginalClassAdCollectionName(), 
					"COPY",
					WebTreeMenuItem.TARGET_NEW, 
					"thermweb/image/gui/cnr/Copy.gif");
			hdrMenu.addMenuItem(menuItem);				
			
			menuItem = new WebTreeMenuItem("delete",
					"&nbsp;" + ResourceLoader.getString(WebGenerator.WEB_RESOURCE, "Delete"),
					classHdr.getOriginalClassAdCollectionName(), 
					"DELETE",
					WebTreeMenuItem.TARGET_INFO_AREA,
					"thermweb/image/gui/cnr/Delete.gif");
			hdrMenu.addMenuItem(menuItem);
			
			creaMenuPersRigheDet(hdrMenu, classHdr);  // fix 30223
			
			if(ClipboardManager.existsClipboardCfgWithHdrOrigin(classHdr.getOriginalClassAdCollectionName())) {
				WebTreeMenuItem addToClipboard = new WebTreeMenuItem("addToClipboard", 
																	"&nbsp;" + ResourceLoader.getString("com.thera.thermfw.clipboard.resources.Clipboard", "clipboard"),
																	classHdr.getOriginalClassAdCollectionName(), 
																	ClipboardManager.ACTION_ADD_TO_CLIPBOARD, 
																	WebTreeMenuItem.TARGET_INFO_AREA, 
																	"com/thera/thermfw/clipboard/css/images/clipboard-copy.png");
				hdrMenu.addMenuItem(addToClipboard);
			}
		} 
	}
	
	public boolean load() {
		// fix 30327
		//char tipoRepPrezzoArt = PreventivoCommessaTestata.REP_PREZZO_DA_LST_VEND;
		boolean isGestioneMarkUp = false;
		// fine fix 30327
		Object bo = getOwnerForm().getBODataCollector().getBo();
		if(bo instanceof PreventivoCommessaTestata){
			PreventivoCommessaTestata testata = (PreventivoCommessaTestata)bo;
			// fix 30327
			//tipoRepPrezzoArt = testata..getRepPrezzoArt();
			isGestioneMarkUp = testata.isGestioneMarkup();
			// fine fix 30327
		}
		
		columnClassAD.add("Livello");
		columnClassAD.add("TipoRigaForTree");		
		columnClassAD.add("DescrizioneForTree");
		columnClassAD.add("IdComponCostoForTree");
		columnClassAD.add("IdUmVenForTree");
		columnClassAD.add("QtaPrvUmPrmForTree");
		columnClassAD.add("CostoRiferForTree");
		// fix 30327
		//if(tipoRepPrezzoArt == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
		if(isGestioneMarkUp){
		// fine fix 30327	
			columnClassAD.add("MarkupForTree");
		}
		columnClassAD.add("PrezzoTotaleForTree");
		columnClassAD.add("CosLivelloForTree");
		columnClassAD.add("VlrLivelloForTree");
		columnClassAD.add("MdcLivelloForTree");
		columnClassAD.add("CosTotaleForTree");
		columnClassAD.add("VlrTotaleForTree");
		columnClassAD.add("MdcTotaleForTree");
		columnClassAD.add("PercentualeForTree");
		columnClassAD.add("Nota");//29642
		columnClassAD.add("DatiComuniEstesi.Stato");//29642
		columnClassAD.add("DescrizionArticoloForTree");//29731
		columnClassAD.add("DescrizionCommessaForTree");//29731
		adjustColumnClassAD();
		return super.load();
	}
	
	protected boolean isDefaultColumn(String adName) {
		if (adName.equals("Livello") || 
			adName.equals("VlrLivelloForTree") || 
			adName.equals("CosLivelloForTree") || 
			adName.equals("MdcLivelloForTree")
			|| adName.equals("Nota")//29642
			|| adName.equals("DatiComuniEstesi.Stato")//29642
			|| adName.equals("DescrizionArticoloForTree")//29731
			|| adName.equals("DescrizionCommessaForTree")//29731
		   )
		{
			return false;
		}
		return true;
	}
	
	public ClassADCollection getClassADCollection() {
		ClassADCollection cadc = null;
		try {
			cadc = ClassADCollectionManager.collectionWithName("PreventivoCommessaTestata");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return cadc;
	}
	
	public String getKeyForMenu(BODataCollector nodeBODC) {
		String key = "";
		Object bo = nodeBODC.getBo();
		if(bo instanceof PreventivoCommessaTestata){
			key = ((PreventivoCommessaTestata) bo).getKey();
		}
		else if(bo instanceof PreventivoCommessaRiga){
			key = ((PreventivoCommessaRiga) bo).getKey();
		}
		else if(bo instanceof PreventivoCommessaVoce){
			key = ((PreventivoCommessaVoce) bo).getKey();
		}
		return key;
	}
	
	public Object getNodo(BODataCollector nodeBODC) {
		return nodeBODC.getBo();
	}

	public String getErroriToString(Object nodo) {
		return "";
	}
	
	protected String getIcon(Object nodo) {
		if (nodo instanceof PreventivoCommessaTestata || nodo instanceof PreventivoCommessaRiga){
			return "it/thera/thip/base/commessa/images/Commessa.gif";
		}
		else if(nodo instanceof PreventivoCommessaVoce){
			PreventivoCommessaVoce voce = (PreventivoCommessaVoce) nodo;
			if(voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA){
				//29731 inizio
				if(voce.getIdSubRigavPrv() != 0){
					return "it/thera/thip/base/commessa//images/RisorsaA.gif";
				}
				else{
					return "it/thera/thip/datiTecnici/modpro/images/Risorsa.gif";
				}
				//29731 fine				
			}
			else if(voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO){
				return "it/thera/thip/cs/images/Articolo.gif";
			}				
			else{
				if(voce.getIdSubRigavPrv() != 0){
					return "it/thera/thip/datiTecnici/modpro/images/MaterialeA.gif";
				}
				else{
					return "it/thera/thip/datiTecnici/modpro/images/Materiale.gif";
				}								
			}
		}
		return "it/thera/thip/datiTecnici/modpro/images/Modpro.gif";	
	}
	
	public void writeBody(JspWriter out) throws java.io.IOException {
		out.println("<script language=\"JavaScript1.2\">");
		writeNodeProperties(out);
		writeNode(out, root, 0, null);
		if (type == MENU_TREE)
			out.println(treeName + ".setBehavior('menu');");
		else
			out.println(treeName + ".setBehavior('treeDet');");
		out.println("");
		out.println("  currentTree=" + treeName + ";");
		out.println("document.write(" + treeName + ");");
		out.println("</script>");
		out.println(hiddenClientCodeColumnSelectorParams());
	}

	public void writeSubmitForm(JspWriter out) throws java.io.IOException {
		super.writeSubmitForm(out);
	    out.println("<script language=\"JavaScript1.2\">");
	    out.println(treeName + ".toggle();");
	    //out.println(treeName + ".expandAll();"); //Fix 29959
	    out.println(treeName + ".expandAllTranneKit();"); //Fix 29959
	    out.println("</script>");
	}
	
	public void write(JspWriter out) throws java.io.IOException	{
		super.write(out);
		out.println(WebJSTypeList.getImportForJSLibrary("it/thera/thip/base/commessa/PreventivoCommessaTree.js", getServerName(), getServerPort()));
		//29529 inizio
		out.println(WebJSTypeList.getImportForJSLibrary("thermweb/factory/gui/ajax/XMLElement.js", getServerName(), getServerPort()));
		out.println(WebJSTypeList.getImportForJSLibrary("thermweb/factory/gui/ajax/ajaxUtil.js", getServerName(), getServerPort()));
		out.println(WebJSTypeList.getImportForJSLibrary("thermweb/factory/type/Type.js", getServerName(), getServerPort()));
		//29529 fine
	}
	
	//29529 inizio
	public void writeTableHeader(JspWriter out) throws java.io.IOException, NoSuchFieldException {
        ClassADCollection cac = getClassADCollection();
        StringBuffer rightLeftPos = new StringBuffer("");
        rightLeftPos = rightLeftPos.append("var rightLeftPos = new Array();\n");
        StringBuffer cellEditale = new StringBuffer("");
        cellEditale = cellEditale.append("var cellEditale = new Array();\n");
        StringBuffer attributeNames = new StringBuffer("");
        attributeNames = attributeNames.append("var attributeNames = new Array();\n");
        String outStr = "<td nowrap class=\"eg_header\">&nbsp;</td>";
        int i = 0;
        for (Iterator iter = columnClassAD.iterator(); iter.hasNext(); ) {
            ClassAD classAD = cac.getAttribute((String)iter.next());
            outStr += "<td nowrap class=\"eg_header\">&nbsp;" + classAD.getColumnTitleNLS() + "&nbsp;</td>\n";
            if (classAD.getType() instanceof DecimalType) {
                rightLeftPos.append("rightLeftPos['").append(i).append("']='").append("R';\n");
            } 
            else {
                rightLeftPos.append("rightLeftPos['").append(i).append("']='").append("L';\n");
            }
            
            if(classAD.getAttributeName().equals("QtaPrvUmPrmForTree") || 
               classAD.getAttributeName().equals("PrezzoTotaleForTree") || 
               classAD.getAttributeName().equals("MarkupForTree")) {
            	cellEditale.append("cellEditale['").append(i).append("']='").append("Y';\n");
            } 
            else {
            	cellEditale.append("cellEditale['").append(i).append("']='").append("N';\n");
            }    
            attributeNames.append("attributeNames['").append(i).append("']='").append(classAD.getAttributeName()).append("';\n");
            i++;           
        }
        out.println(outStr);
        out.println("<script language=\"JavaScript1.2\">");
        out.println(rightLeftPos);
        out.println(cellEditale); 
        out.println(attributeNames); 
        out.println("</script>");
    }
	//29529 fine
	
	//Fix 29959 --inizio
	protected void createMenuNumbers() {
	   super.createMenuNumbers();
	   menuNumbers.put("MultiSelection", new Integer(1000));
	}
	
	public void writeSubmitFormInternalFields(JspWriter out) throws java.io.IOException {
		super.writeSubmitFormInternalFields(out);
		out.println("<input id=\"thClassNames\" name=\"thClassNames\" type=\"hidden\">");
		out.println("<input id=\"ObjectKeys\" name=\"ObjectKeys\" type=\"hidden\">");
	}
	//Fix 29959 --fine
}

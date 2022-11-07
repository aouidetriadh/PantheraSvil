package it.thera.thip.produzione.commessa.web;

import java.io.IOException;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebMenuBar;
import com.thera.thermfw.web.WebMenuItem;
import com.thera.thermfw.web.WebToolBar;
import com.thera.thermfw.web.WebToolBarButton;

import it.thera.thip.cs.web.AziendaGridActionAdapter;
/**
 * BudgetCommessaGridActionAdapter
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 01/11/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34585   01/11/2021    RA       Prima struttura
 * 34795   14/02/2022    RA		  Aggiunto Azione VARIA_BUDGET_COMMESSA
 */
public class BudgetCommessaGridActionAdapter extends AziendaGridActionAdapter
{
	//34795 inizio
	protected static final String RES = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";
	public final static String VARIA_BUDGET_COMMESSA = "VARIA_BUDGET_COMMESSA";
	//34795 fine
	
	public void modifyMenuBar(WebMenuBar menuBar) {
		menuBar.removeMenu("ListMenu.NewTemplate");
		menuBar.removeMenu("SelectedMenu.Copy");
		
	    //34795 inizio
	    WebMenuItem variaBudgetCommessa = new WebMenuItem("VariaBudgetCommessa", "action_submit", "new", "no", RES, "VariaBudgetCommessa", VARIA_BUDGET_COMMESSA, "single", true);
	    menuBar.addMenu("SelectedMenu.View", variaBudgetCommessa);
	    //34795 fine
	}

	public void modifyToolBar(WebToolBar toolBar) {
		super.modifyToolBar(toolBar);
		toolBar.removeButton("Copy");
		
	    //34795 inizio
	    WebToolBarButton variaBudgetCommessa = new WebToolBarButton("VariaBudgetCommessa", "action_submit", "new", "no", RES, "VariaBudgetCommessa", "it/thera/thip/produzione/commessa/images/CommessaBudgetVaria.gif", VARIA_BUDGET_COMMESSA, "single", true);
	    toolBar.addButton("View", variaBudgetCommessa);
	    //34795 fine
	}
	
	//34795 inizio
	protected void otherActions(ClassADCollection cadc, ServletEnvironment se) throws ServletException, IOException {
		String action = getStringParameter(se.getRequest(), ACTION).toUpperCase();
		if (action.equals(VARIA_BUDGET_COMMESSA)) {
			variazioneBudgetCommessa(se);
		}
	}
	
	protected void variazioneBudgetCommessa(ServletEnvironment se) throws ServletException, IOException {
		ClassADCollection cadc = getClassADCollection("BudgetCommessa");
	    String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idBudget = KeyHelper.getTokenObjectKey(key,2);
		String idCommessa = KeyHelper.getTokenObjectKey(key,3);
		String budgetKey = KeyHelper.buildObjectKey(new Object[] {idAzienda, idBudget, idCommessa});
		String parameters = "?thAction=NEW&thClassName=VariaBudgetCommessa&ObjectKey=" + budgetKey ;
	    parameters += "&Opener=BUDGET_CMM";
	    String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
	                 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
	                 "/" + cadc.getActionAdapterNameWeb() + parameters;
	    se.getResponse().getOutputStream().print("<script>");
	    se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
	    se.getResponse().getOutputStream().print("</script>");
	}
	//34795 fine
	
}

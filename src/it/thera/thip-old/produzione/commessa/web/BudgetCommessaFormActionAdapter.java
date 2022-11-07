package it.thera.thip.produzione.commessa.web;

import java.io.IOException;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebMenu;
import com.thera.thermfw.web.WebMenuBar;
import com.thera.thermfw.web.WebMenuItem;
import com.thera.thermfw.web.WebToolBar;
import com.thera.thermfw.web.WebToolBarButton;
import com.thera.thermfw.web.WebToolBarException;
import com.thera.thermfw.web.servlet.BaseServlet;
import com.thera.thermfw.web.servlet.FormActionAdapter;

import it.thera.thip.produzione.commessa.BudgetCommessa;
/**
 * BudgetCommessaFormActionAdapter
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 16/02/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34795   16/02/2022    RA       Prima struttura
 */
public class BudgetCommessaFormActionAdapter extends FormActionAdapter {
	
	protected static final String RES = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";
	public final static String VARIA_BUDGET_COMMESSA = "VARIA_BUDGET_COMMESSA";
	
	public void modifyMenuBar(WebMenuBar menuBar) {
		WebMenu objectMenu = (WebMenu)menuBar.getMenu("ObjectMenu");
		BudgetCommessa budget = (BudgetCommessa)menuBar.getOwnerForm().getBODataCollector().getBo();
		if(budget.getCommessaAppart() == null) {
			WebMenuItem variaBudgetCommessa = new WebMenuItem("VariaBudgetCommessa", 
															  "action_submit", 
															  "new", 
															  "no", 
															  RES, 
															  "VariaBudgetCommessa",
															  VARIA_BUDGET_COMMESSA,
															  "single",
															  false,
															  null);
			objectMenu.addMenu(variaBudgetCommessa);
		}
	}

	public void modifyToolBar(WebToolBar toolBar) {
		super.modifyToolBar(toolBar);
		BudgetCommessa budget = (BudgetCommessa)toolBar.getOwnerForm().getBODataCollector().getBo();
		if(budget.getCommessaAppart() == null) {
			WebToolBarButton variaBudgetCommessa = new WebToolBarButton("VariaBudgetCommessa",
			    														"action_submit",
			    														"new",
			    														"no",
			    														RES,
			    														"VariaBudgetCommessa",
			    														"it/thera/thip/produzione/commessa/images/CommessaBudgetVaria.gif",
			    														VARIA_BUDGET_COMMESSA,
			    														"single",
			    														false);
			toolBar.addButton("SaveAndNew",variaBudgetCommessa);
		}
		try {
			toolBar.removeSeparatorAfter("Print");
		} 
		catch (WebToolBarException e) {
			e.printStackTrace(Trace.excStream);
		}
		toolBar.removeButton("SaveScreenData");
		toolBar.removeButton("SaveScreenDataPullDown");
	}	
	
	protected void otherActions(ClassADCollection cadc, ServletEnvironment se) throws ServletException, IOException {
		super.otherActions(cadc, se); 
	    String action = BaseServlet.getStringParameter(se.getRequest(), FormActionAdapter.ACTION).toUpperCase();
		if(action.equals(VARIA_BUDGET_COMMESSA))
	    	variazioneBudgetCommessa(se);
	}
	
	protected void variazioneBudgetCommessa(ServletEnvironment se) throws ServletException, IOException{
		ClassADCollection cadc = getClassADCollection("BudgetCommessa");
		String key = getStringParameter(se.getRequest(), "thKey");
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
	
   protected void save(ClassADCollection  cadc, ServletEnvironment se) throws ServletException, IOException {
      ConsuntivoCommessaDetServlet.saveViewMDV(se, "BudgetCommessa");
      super.save(cadc, se);
   }
 
}

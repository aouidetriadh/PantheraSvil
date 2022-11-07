package it.thera.thip.base.commessa.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebMenuBar;
import com.thera.thermfw.web.WebMenuItem;
import com.thera.thermfw.web.WebToolBar;
import com.thera.thermfw.web.WebToolBarPullDownAction;
import com.thera.thermfw.web.WebToolBarPullDownButton;

import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.documentoDgt.web.DocDgtClgAzGridActionAdapter;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
import it.thera.thip.produzione.commessa.VariaBudgetCommessa;

/**
 * Title: CommessaGridActionAdapter
 * Description: Gestisce le azioni della griglia per la commessa
 * Copyright: Copyright (c) 2005
 * Company: Thera spa
 * @author IT
 * @version 1.0
 */
/*
 * NrFix  Data        Owner  Description
 * 04475  18/11/2005  IT     Prima implementazione
 * 13196  21/09/2010  DZ     Aggiunto URLencoding.
 * 34094  14/02/2022  TJ     Se un sottocommessa e selezionata nel action 'WF_FIRST' quindi visualizzare un errore 
 * 33950  25/08/2021  RA	 Aggiunto azione ConsuntivoCommessa
 * 34795  16/02/2021  RA	 Aggiunto azione variazione budget commessa
 * 35382  07/03/2022  RA	 Integrazione budget dentro commenssa
 * 35837  04/04/2022  RA     Aggiunto azione  SCOSTAMENTO_BUDGET
 * 36460  13/09/2022  RA     Aggiunto azione  EVOLUZIONE_BUDGET , EVOLUZIONE_CONSUNTIVI
 */

public class CommessaGridActionAdapter extends DocDgtClgAzGridActionAdapter {
	
	//35382 inizio
	protected static final String RES_BUDGET = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";
	public static final String CURRENT_BUDGET ="CURRENT_BUDGET";
	public static final String LISTA_BUDGET ="LISTA_BUDGET";
	public static final String EVOLUZIONE_BUDGET ="EVOLUZIONE_BUDGET";//36460
	public static final String SCOSTAMENTO_BUDGET ="SCOSTAMENTO_BUDGET";  //35837
	public static final String NUOVA_VARIAZIONE ="NUOVA_VARIAZIONE";
	public static final String ULTIMA_VARIAZIONE ="ULTIMA_VARIAZIONE";
	public static final String LISTA_VARIAZIONI ="LISTA_VARIAZIONI";
	  
	public static final String NUOVO_CONSUNTIVO ="NUOVO_CONSUNTIVO";
	public static final String ULTIMO_CONSUNTIVO ="ULTIMO_CONSUNTIVO";
	public static final String LISTA_CONSUNTIVI ="LISTA_CONSUNTIVI";
	public static final String EVOLUZIONE_CONSUNTIVI ="EVOLUZIONE_CONSUNTIVI";//36460
	//35382 fine
	
	/* Costruisce il Filtro per l'apertura dei DocDgt Collegati
	 * @see it.thera.thip.base.documentoDgt.web.DocDgtClgGridActionAdapter#getFiltro(java.lang.String)
	 */
	protected String getFiltro(String key) {
		return "IdCommessa=" + URLEncoder.encode(KeyHelper.getTokenObjectKey(key, 2)); //...FIX13196 - DZ
	}

	//33950 inizio
	public void modifyMenuBar(WebMenuBar menuBar) {
		//35382 inizio
		WebMenuItem listaBudget = new WebMenuItem("ListaBudget", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "ListaBudget", LISTA_BUDGET, "single",
				false, null);
		menuBar.addMenu("SelectedMenu.Delete", listaBudget);

		//36460 inizio
		WebMenuItem evoluzioneBudget = new WebMenuItem("EvoluzioneBudget", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "EvoluzioneBudget", EVOLUZIONE_BUDGET, "single",
				false, null);
		menuBar.addMenu("SelectedMenu.ListaBudget", evoluzioneBudget);
		//36460 fine

		//35837 inizio
		WebMenuItem scostamentoBudget = new WebMenuItem("ScostamentoBudget", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "ScostamentoBudget", SCOSTAMENTO_BUDGET,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.ListaBudget", scostamentoBudget);
		//35837 fine

		WebMenuItem nuovaVariazione = new WebMenuItem("NuovaVariazione", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "NuovaVariazione", NUOVA_VARIAZIONE,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.ScostamentoBudget", nuovaVariazione);

		WebMenuItem ultimaVariazione = new WebMenuItem("UltimaVariazione", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "UltimaVariazione", ULTIMA_VARIAZIONE,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.NuovaVariazione", ultimaVariazione);

		WebMenuItem listaVariazioni = new WebMenuItem("ListaVariazioni", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "ListaVariazioni", LISTA_VARIAZIONI,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.UltimaVariazione", listaVariazioni);

		WebMenuItem nuovoConsuntivo = new WebMenuItem("NuovoConsuntivo", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "NuovoConsuntivo", NUOVO_CONSUNTIVO,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.ListaVariazioni", nuovoConsuntivo);

		WebMenuItem ultimoConsuntivo = new WebMenuItem("UltimoConsuntivo", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "UltimoConsuntivo", ULTIMO_CONSUNTIVO,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.NuovoConsuntivo", ultimoConsuntivo);

		WebMenuItem listaConsuntivi = new WebMenuItem("ListaConsuntivi", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "ListaConsuntivi", LISTA_CONSUNTIVI,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.UltimoConsuntivo", listaConsuntivi);

		//36460 inizio
		WebMenuItem evoluzioneConsuntivi = new WebMenuItem("EvoluzioneConsuntivi", "action_submit", "new", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "EvoluzioneConsuntivi", EVOLUZIONE_CONSUNTIVI,
				"single", false, null);
		menuBar.addMenu("SelectedMenu.ListaConsuntivi", evoluzioneConsuntivi);
		//36460 fine
		//35382 fine
	
	}
	
	public void modifyToolBar(WebToolBar toolBar) {
		super.modifyToolBar(toolBar);
		//35382 inizio
		toolBar.addButton("View", getBudgetButton());
		toolBar.addButton("AzioniBudget", getConsuntivoButton());
		//35382 fine
	}
	
	protected void otherActions(ClassADCollection cadc, ServletEnvironment se) throws ServletException, IOException  {
		String action = getStringParameter(se.getRequest(), ACTION).toUpperCase();
		//35382 inizio
		if(action.equals(CURRENT_BUDGET))
	    	apriCurrentBudget(se);
		else if(action.equals(LISTA_BUDGET))
	    	apriListaBudget(se);
		//36460 inizio
	    else if(action.equals(EVOLUZIONE_BUDGET))
	    	apriEvoluzioneBudget(se);
		//36460 fine
		//35837 inizio
	    else if(action.equals(SCOSTAMENTO_BUDGET))
	    	apriScostamentoBudget(se);
		//35837 fine
	    else if(action.equals(NUOVA_VARIAZIONE))
	    	apriNuovaVariazione(se);    
	    else if(action.equals(ULTIMA_VARIAZIONE))
	    	apriUltimaVariazione(se);
	    else if(action.equals(LISTA_VARIAZIONI))
	    	apriListaVariazioni(se);
	    else if(action.equals(NUOVO_CONSUNTIVO))
	    	apriNuovoConsuntivo(se);
	    else if(action.equals(ULTIMO_CONSUNTIVO))
	    	apriUltimoConsuntivo(se);
	    else if(action.equals(LISTA_CONSUNTIVI))
	    	apriListaConsuntivi(se); 
		//36460 inizio
	    else if(action.equals(EVOLUZIONE_CONSUNTIVI))
	    	apriEvoluzioneConsuntivo(se);
		//36460 fine
	}
	
	protected void apriCurrentBudget(ServletEnvironment se) throws ServletException, IOException{
		BudgetCommessa currentBudget = null;
		ClassADCollection cadc = getClassADCollection("BudgetCommessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		try {
			Commessa commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
			currentBudget = commessa.getUltimoBudgetCommessa();
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		if(currentBudget != null && currentBudget.getCommessaAppart() == null) {
			String parameters = "?thAction=UPDATE&thClassName=BudgetCommessa&ObjectKey=" + currentBudget.getKey() ;
			String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
						 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
						 "/" + cadc.getActionAdapterNameWeb() + parameters;
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
			se.getResponse().getOutputStream().print("</script>");
		}
		else {
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.close(); ");
			se.getResponse().getOutputStream().print("alert('" + ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "msgBudget1") + "'); ");
			se.getResponse().getOutputStream().print("</script>");
		}
	}
		
	protected void apriNuovoConsuntivo(ServletEnvironment se) throws ServletException, IOException {
		ClassADCollection cadc = getClassADCollection("Commessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		String parameters = "?thAction=NEW&thClassName=ConsuntivoCommessa&ObjectKey=" + commessaKey ;
		parameters += "&Opener=COMMESSA";
		String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
					 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
					 "/" + cadc.getActionAdapterNameWeb() + parameters;
		se.getResponse().getOutputStream().print("<script>");
		se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
		se.getResponse().getOutputStream().print("</script>");
	}

	protected void apriNuovaVariazione(ServletEnvironment se) throws ServletException, IOException{
		Commessa commessa = null;
		ClassADCollection cadc = getClassADCollection("Commessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		try {
			commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		
		if(commessa != null && !commessa.esisteVariazioneProvvisorio()) {
			String parameters = "?thAction=NEW&thClassName=VariaBudgetCommessa&ObjectKey=" + commessaKey ;
			parameters += "&Opener=COMMESSA";
			String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
						 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
						 "/" + cadc.getActionAdapterNameWeb() + parameters;
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
			se.getResponse().getOutputStream().print("</script>");
		}
		else {
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.close(); ");
			se.getResponse().getOutputStream().print("</script>");
		}

	}
	
	//35837 inizio
	protected void apriScostamentoBudget(ServletEnvironment se) throws ServletException, IOException{
		Commessa commessa = null;
		ClassADCollection cadc = getClassADCollection("Commessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		try {
			commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}

		String parameters = "?thAction=NEW&thClassName=ScostamentoBudgetCommessa&ObjectKey=" + commessaKey ;
		parameters += "&Opener=COMMESSA";
		String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
					 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
					 "/" + cadc.getActionAdapterNameWeb() + parameters;
		se.getResponse().getOutputStream().print("<script>");
		se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
		se.getResponse().getOutputStream().print("</script>");
	}
	//35837 fine
	
	protected void apriUltimaVariazione(ServletEnvironment se) throws ServletException, IOException{
		VariaBudgetCommessa ultimoVariazione = null;
		Commessa commessa = null;
		ClassADCollection cadc = getClassADCollection("VariaBudgetCommessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		try {
			commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
			ultimoVariazione = commessa.getUltimoVariaBudgetCommessa();
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		if(commessa != null && ultimoVariazione != null && commessa.esisteVariazioneProvvisorio()) {
			String parameters = "?thAction=UPDATE&thClassName=VariaBudgetCommessa&ObjectKey=" + ultimoVariazione.getKey() ;
			String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
						 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
						 "/" + cadc.getActionAdapterNameWeb() + parameters;
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
			se.getResponse().getOutputStream().print("</script>");
		}
		else {
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.close(); ");
			se.getResponse().getOutputStream().print("</script>");
		}
	}
	  
	protected void apriUltimoConsuntivo(ServletEnvironment se) throws ServletException, IOException{
		ConsuntivoCommessa ultimoConsuntivo = null;
		Commessa commessa = null;
		ClassADCollection cadc = getClassADCollection("ConsuntivoCommessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		try {
			commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
			ultimoConsuntivo = commessa.getUltimoConsuntivoCommessa();
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		if(commessa != null && ultimoConsuntivo != null && commessa.esisteConsuntivoCommessa()) {
			String parameters = "?thAction=UPDATE&thClassName=ConsuntivoCommessa&ObjectKey=" + ultimoConsuntivo.getKey() ;
			String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
						 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
						 "/" + cadc.getActionAdapterNameWeb() + parameters;
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
			se.getResponse().getOutputStream().print("</script>");
		}
		else {
			se.getResponse().getOutputStream().print("<script>");
			se.getResponse().getOutputStream().print("window.close(); ");
			se.getResponse().getOutputStream().print("alert('" + ResourceLoader.getString("it.thera.thip.produzione.commessa.resources.BudgetCommessa", "msgConsCmm1") + "'); ");
			se.getResponse().getOutputStream().print("</script>");
		}
	}
	  
	protected void apriListaBudget(ServletEnvironment se) throws ServletException, IOException{
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String parameters = "thGridType=list&ClassName=BudgetCommessa&thRestrictConditions=IdCommessa=" + idCommessa ;
		String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
					 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
					 "/com.thera.thermfw.web.servlet.Execute?"  + parameters;
		se.getResponse().getOutputStream().print("<script>");
		se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
		se.getResponse().getOutputStream().print("</script>");
	}
	  
	protected void apriListaConsuntivi(ServletEnvironment se) throws ServletException, IOException{
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String parameters = "thGridType=list&ClassName=ConsuntivoCommessa&thRestrictConditions=IdCommessa=" + idCommessa ;
		String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
					 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
					 "/com.thera.thermfw.web.servlet.Execute?"  + parameters;
		se.getResponse().getOutputStream().print("<script>");
		se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
		se.getResponse().getOutputStream().print("</script>");
	}
	  
	protected void apriListaVariazioni(ServletEnvironment se) throws ServletException, IOException{
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String parameters = "thGridType=list&ClassName=VariaBudgetCommessa&thRestrictConditions=IdCommessa=" + idCommessa ;
		String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
					 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
					 "/com.thera.thermfw.web.servlet.Execute?"  + parameters;
		se.getResponse().getOutputStream().print("<script>");
		se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
		se.getResponse().getOutputStream().print("</script>");
	}
	
	public WebToolBarPullDownButton getBudgetButton() {
		WebToolBarPullDownButton budgetButton = new WebToolBarPullDownButton("AzioniBudget", "", "", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "AzioniBudget",
				"it/thera/thip/produzione/commessa/images/CommessaBudget.gif", "", "single", false, false, null, null);

		WebToolBarPullDownAction currentBudgetAction = new WebToolBarPullDownAction("CurrentBudget", "action_submit", "new",
				"no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa", "CurrentBudget", null, CURRENT_BUDGET,
				"single", false, false, null, null);
		budgetButton.addAction(currentBudgetAction);
		
		WebToolBarPullDownAction listaBudgetAction = new WebToolBarPullDownAction("ListaBudget", "action_submit", "new",
				"no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa", "ListaBudget", null, LISTA_BUDGET,
				"single", false, false, null, null);
		budgetButton.addAction(listaBudgetAction);
		
		//36460 inizio
		WebToolBarPullDownAction evoluzioneBudgetAction = new WebToolBarPullDownAction("EvoluzioneBudget", "action_submit",
				"new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa", "EvoluzioneBudget", null,
				EVOLUZIONE_BUDGET, "single", false, false, null, null);
		budgetButton.addAction(evoluzioneBudgetAction);
		//36460 fine
		
		//35837 inizio
		WebToolBarPullDownAction scostamentoBudgetAction = new WebToolBarPullDownAction("ScostamentoBudget",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"ScostamentoBudget", null, SCOSTAMENTO_BUDGET, "single", false, false, null, null);
		budgetButton.addAction(scostamentoBudgetAction);
		//35837 fine
		
		WebToolBarPullDownAction nuovaVariazioneAction = new WebToolBarPullDownAction("NuovaVariazione",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"NuovaVariazione", null, NUOVA_VARIAZIONE, "single", false, false, null, null);
		budgetButton.addAction(nuovaVariazioneAction);
		
		WebToolBarPullDownAction ultimaVariazioneAction = new WebToolBarPullDownAction("UltimaVariazione",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"UltimaVariazione", null, ULTIMA_VARIAZIONE, "single", false, false, null, null);
		budgetButton.addAction(ultimaVariazioneAction);
		
		WebToolBarPullDownAction listaVariazioniAction = new WebToolBarPullDownAction("ListaVariazioni",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"ListaVariazioni", null, LISTA_VARIAZIONI, "single", false, false, null, null);
		budgetButton.addAction(listaVariazioniAction);
		return budgetButton;
	}

	public WebToolBarPullDownButton getConsuntivoButton() {
		WebToolBarPullDownButton consuntivoButton = new WebToolBarPullDownButton("AzioniConsuntivo", "", "", "no",
				"it.thera.thip.produzione.commessa.resources.BudgetCommessa", "AzioniConsuntivo",
				"it/thera/thip/produzione/commessa/images/CommessaConsuntivo.gif", "", "single", false, false, null,
				null);

		WebToolBarPullDownAction nuovoConsuntivoAction = new WebToolBarPullDownAction("NuovoConsuntivo",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"NuovoConsuntivo", null, NUOVO_CONSUNTIVO, "single", false, false, null, null);
		consuntivoButton.addAction(nuovoConsuntivoAction);
		WebToolBarPullDownAction ultimoConsuntivoAction = new WebToolBarPullDownAction("UltimoConsuntivo",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"UltimoConsuntivo", null, ULTIMO_CONSUNTIVO, "single", false, false, null, null);
		consuntivoButton.addAction(ultimoConsuntivoAction);
		WebToolBarPullDownAction listaConsuntiviAction = new WebToolBarPullDownAction("ListaConsuntivi",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"ListaConsuntivi", null, LISTA_CONSUNTIVI, "single", false, false, null, null);
		consuntivoButton.addAction(listaConsuntiviAction);
		//36460 inizio
		WebToolBarPullDownAction evoluzioneConsuntiviAction = new WebToolBarPullDownAction("EvoluzioneConsuntivi",
				"action_submit", "new", "no", "it.thera.thip.produzione.commessa.resources.BudgetCommessa",
				"EvoluzioneConsuntivi", null, EVOLUZIONE_CONSUNTIVI, "single", false, false, null, null);
		consuntivoButton.addAction(evoluzioneConsuntiviAction);
		//36460 fine
		return consuntivoButton;
	}

	public String getGridJSPName() {
		return "it/thera/thip/base/commessa/CommessaGrid.jsp";
	}
	//35382 fine
	//36460 inizio
	protected void apriEvoluzioneBudget(ServletEnvironment se) throws ServletException, IOException{
		Commessa commessa = null;
		ClassADCollection cadc = getClassADCollection("Commessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		try {
			commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}

		String parameters = "?thAction=NEW&thClassName=EvoluzioneBudgetCommessa&ObjectKey=" + commessaKey ;
		parameters += "&Opener=COMMESSA";
		String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
					 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
					 "/" + cadc.getActionAdapterNameWeb() + parameters;
		se.getResponse().getOutputStream().print("<script>");
		se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
		se.getResponse().getOutputStream().print("</script>");
	}
	
	protected void apriEvoluzioneConsuntivo(ServletEnvironment se) throws ServletException, IOException{
		Commessa commessa = null;
		ClassADCollection cadc = getClassADCollection("Commessa");
		String key = getStringParameter(se.getRequest(), "ObjectKey");
		String idAzienda = KeyHelper.getTokenObjectKey(key,1);
		String idCommessa = KeyHelper.getTokenObjectKey(key,2);
		String commessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda,idCommessa});
		try {
			commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}

		String parameters = "?thAction=NEW&thClassName=EvoluzioneConsunCommessa&ObjectKey=" + commessaKey ;
		parameters += "&Opener=COMMESSA";
		String url = "/" +com.thera.thermfw.base.IniFile.getValue("Web", "WebApplicationPath") +
					 "/" + com.thera.thermfw.base.IniFile.getValue("thermfw.ini","Web", "ServletPath") +
					 "/" + cadc.getActionAdapterNameWeb() + parameters;
		se.getResponse().getOutputStream().print("<script>");
		se.getResponse().getOutputStream().print("window.location = '" + url + "'; ");
		se.getResponse().getOutputStream().print("</script>");
	}
	//36460 fine
}

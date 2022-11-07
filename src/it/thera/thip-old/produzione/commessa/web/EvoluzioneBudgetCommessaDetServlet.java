package it.thera.thip.produzione.commessa.web;

import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.produzione.commessa.EvoluzioneBudgetCommessa;
/**
 * EvoluzioneBudgetCommessaDetServlet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 01/09/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 36460   01/09/2022    RA       Prima struttura
 */
public class EvoluzioneBudgetCommessaDetServlet extends BaseServlet {
	
	protected void processAction(ServletEnvironment se) throws Exception {
		caricaDati(se);
	}

	protected void caricaDati(ServletEnvironment se) throws Exception {
		String action = se.getRequest().getParameter("myAction");
		JSONObject json = new JSONObject();
		JSONArray data = new JSONArray();
		PrintWriter out = se.getResponse().getWriter();
		EvoluzioneBudgetCommessa evoluzione = getEvoluzioneBudgetCommessa(se);
		if(evoluzione != null && action != null && action.equals("CARICA")) {	
			data = evoluzione.getDataModel();
			json.put("data", data);
			out.print(json);			
		}
	}

	public EvoluzioneBudgetCommessa getEvoluzioneBudgetCommessa(ServletEnvironment se) {
		EvoluzioneBudgetCommessa evoluzione = (EvoluzioneBudgetCommessa)Factory.createObject(EvoluzioneBudgetCommessa.class);
		String idCommessa = se.getRequest().getParameter("IdCommessa");
		evoluzione.setIdAzienda(Azienda.getAziendaCorrente());
		evoluzione.setIdCommessa(idCommessa);
		if(isParameter(se, "Totali")) {
			boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
			evoluzione.setTotali(totali);
		}
		
		if(isParameter(se, "DettagliCommessa")) {
			boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
			evoluzione.setDettagliCommessa(dettagliCommessa);
		}
		
		if(isParameter(se, "DettagliSottoCommesse")) {
			boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
			evoluzione.setDettagliSottoCommesse(dettagliSottoCommesse);
		}
		
		if(isParameter(se, "ComponentiPropri")) {
			boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
			evoluzione.setComponentiPropri(componentiPropri);
		}
		
		if(isParameter(se, "SoloComponentiValorizzate")) {
			boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
			evoluzione.setSoloComponentiValorizzate(soloComponentiValorizzate);
		}
				
		if(isParameter(se, "DataInizio")) {						
			String dataInizioStr = se.getRequest().getParameter("DataInizio");

		    DateType dateType = new DateType();
		    java.sql.Date dataInizio = (java.sql.Date)dateType.stringToObject(dataInizioStr);
		    evoluzione.setDataInizio(dataInizio);
		}
		
		if(isParameter(se, "DataFine")) {						
			String dataFineStr = se.getRequest().getParameter("DataFine");

		    DateType dateType = new DateType();
		    java.sql.Date dataFine = (java.sql.Date)dateType.stringToObject(dataFineStr);
		    evoluzione.setDataFine(dataFine);
		}
		
		return evoluzione;
	}
	
	public boolean isParameter(ServletEnvironment se, String param) {
		return se.getRequest().getParameter(param) != null ? true : false;
	}
	
	public Integer getIntegerPram(ServletEnvironment se, String param) {
		String value = se.getRequest().getParameter(param);
		if(value != null && !value.equals(""))
			return new Integer(value);
		return null;
	}
}

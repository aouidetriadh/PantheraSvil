package it.thera.thip.produzione.commessa.web;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.collector.BaseBOComponent;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.type.IntegerType;
import com.thera.thermfw.type.NumberType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebElement;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.produzione.commessa.ScostamentoBudgetCommessa;
import it.thera.thip.produzione.commessa.VariaBudgetCommessa;
/**
 * ScostamentoBudgetCommessaDetServlet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/03/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 35837   29/03/2022    RA       Prima struttura
 */
public class ScostamentoBudgetCommessaDetServlet extends BaseServlet {
	
	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessaDetServlet";

	protected void processAction(ServletEnvironment se) throws Exception {
		caricaDati(se);
	}

	protected void caricaDati(ServletEnvironment se) throws Exception {
		String action = se.getRequest().getParameter("myAction");
		JSONObject json = new JSONObject();
		JSONArray data = new JSONArray();
		PrintWriter out = se.getResponse().getWriter();
		ScostamentoBudgetCommessa scostamento = getScostamentoBudgetCommessa(se);
		if(scostamento != null && action != null && action.equals("CARICA")) {	
			data = scostamento.getDataModel();
			json.put("data", data);
			json.put("TotHH", "");
			json.put("TotVal", "");
			out.print(json);			
		}
	}

	public ScostamentoBudgetCommessa getScostamentoBudgetCommessa(ServletEnvironment se) {
		ScostamentoBudgetCommessa scostamentoBudget = (ScostamentoBudgetCommessa)Factory.createObject(ScostamentoBudgetCommessa.class);
		String idCommessa = se.getRequest().getParameter("IdCommessa");
		Integer idBudget = getIntegerPram(se, "IdBudget");
		Integer idConsuntivo = getIntegerPram(se,"IdConsuntivo");
		scostamentoBudget.setIdAzienda(Azienda.getAziendaCorrente());
		scostamentoBudget.setIdCommessa(idCommessa);
		scostamentoBudget.setIdBudget(idBudget);
		scostamentoBudget.setIdConsuntivo(idConsuntivo);
		if(isParameter(se, "Totali")) {
			boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
			scostamentoBudget.setTotali(totali);
		}
		
		if(isParameter(se, "DettagliCommessa")) {
			boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
			scostamentoBudget.setDettagliCommessa(dettagliCommessa);
		}
		
		if(isParameter(se, "DettagliSottoCommesse")) {
			boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
			scostamentoBudget.setDettagliSottoCommesse(dettagliSottoCommesse);
		}
		
		if(isParameter(se, "ComponentiPropri")) {
			boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
			scostamentoBudget.setComponentiPropri(componentiPropri);
		}
		
		if(isParameter(se, "SoloComponentiValorizzate")) {
			boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
			scostamentoBudget.setSoloComponentiValorizzate(soloComponentiValorizzate);
		}
				
		if(isParameter(se, "DataRiferimento")) {						
			String dataRiferimentoStr = se.getRequest().getParameter("DataRiferimento");

		    DateType dateType = new DateType();
		    java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);
		    scostamentoBudget.setDataRiferimento(dataRiferimento);
		}
		
		return scostamentoBudget;
	}
	
	public boolean isParameter(ServletEnvironment se, String param) {
		return se.getRequest().getParameter(param) != null ? true : false;
	}
	
	public Integer getIntegerPram(ServletEnvironment se, String param) {
		String value = se.getRequest().getParameter(param);
		if(value != null && !value.equals(""))
		{
         IntegerType intType = new IntegerType();
         String unformatValue = intType.unFormat(value);
			return new Integer(unformatValue);
		}
		return null;
	}
}

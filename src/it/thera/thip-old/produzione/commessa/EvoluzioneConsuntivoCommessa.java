package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.BusinessObjectAdapter;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Database;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.Conflictable;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.commessa.CommessaTM;
import it.thera.thip.base.commessa.TipoCommessa;
import it.thera.thip.cs.DatiComuniEstesi;
/**
 * EvoluzioneConsuntivoCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 01/09/2022 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 36460   01/09/2022    RA		    Prima struttura
 */
public class EvoluzioneConsuntivoCommessa extends BusinessObjectAdapter implements Authorizable, Conflictable {

	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.EvoluzioneConsuntivoCommessa";
	
	protected java.sql.Date iDataInizio;
	protected java.sql.Date iDataFine;
	
	protected boolean iTotali = true;
	protected boolean iDettagliCommessa = true;
	protected boolean iDettagliSottoCommesse = true;
	protected boolean iComponentiPropri = false;
	protected boolean iSoloComponentiValorizzate = false;	

	protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);
	protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);
	
	protected List<ConsuntivoCommessa> iConsuntivi = null;

	public EvoluzioneConsuntivoCommessa() {
		setIdAzienda(Azienda.getAziendaCorrente());
	}

	public void setDataInizio(java.sql.Date dataInizio) {
		this.iDataInizio = dataInizio;
	}

	public java.sql.Date getDataInizio() {
		return iDataInizio;
	}

	public void setDataFine(java.sql.Date dataFine) {
		this.iDataFine = dataFine;
	}

	public java.sql.Date getDataFine() {
		return iDataFine;
	}
	
	public boolean isTotali() {
		return iTotali;
	}
	
	public void setTotali(boolean totali) {
		iTotali = totali;
	}
		
	public boolean isDettagliCommessa() {
		return iDettagliCommessa;
	}
		
	public void setDettagliCommessa(boolean dettagliCommessa) {
		iDettagliCommessa = dettagliCommessa;
	}
		
	public boolean isDettagliSottoCommesse() {
		return iDettagliSottoCommesse;
	}
		
	public void setDettagliSottoCommesse(boolean dettagliSottoCommesse) {
		iDettagliSottoCommesse = dettagliSottoCommesse;
	}
		
	public boolean isComponentiPropri() {
		return iComponentiPropri;
	}
		
	public void setComponentiPropri(boolean componentiPropri) {
		iComponentiPropri = componentiPropri;
	}

	public boolean isSoloComponentiValorizzate() {
		return iSoloComponentiValorizzate;
	}
		
	public void setSoloComponentiValorizzate(boolean soloComponentiValorizzate) {
		iSoloComponentiValorizzate = soloComponentiValorizzate;
	}
	
	public List<ConsuntivoCommessa> getConsuntivi(String idCommessa) {
		if(iConsuntivi == null)
			initializeConsuntivi(idCommessa);
		return iConsuntivi;
	}
	
	public void initializeConsuntivi(String idCommessa) {
		iConsuntivi = new ArrayList<ConsuntivoCommessa>();
	    Database db  = ConnectionManager.getCurrentDatabase();
	    String where  = ConsuntivoCommessaTM.ID_AZIENDA + "='" + Azienda.getAziendaCorrente() + "' AND " + 
						ConsuntivoCommessaTM.R_COMMESSA + "='" + idCommessa + "' ";
		if(getDataInizio() != null) {
			where +=  " AND " + ConsuntivoCommessaTM.DATA_RIFERENTO + " >= " + db.getLiteral(getDataInizio()) + "";
		}
		if(getDataFine() != null) {
			where += " AND " +	ConsuntivoCommessaTM.DATA_RIFERENTO + " <= " + db.getLiteral(getDataFine()) + "";
		}
		String orderBy = ConsuntivoCommessaTM.DATA_RIFERENTO + " ASC ";
		try {
			iConsuntivi = ConsuntivoCommessa.retrieveList(ConsuntivoCommessa.class, where, orderBy, false);
		} 
		catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		}
	}
		
	public void initializeAttDaScreenData() {
		ScreenData sd = ScreenData.getDefaultScreenData("EvoluzioneConsunCommessa");
		if (sd != null && sd.getAttValue("Totali") != null && sd.getAttValue("Totali").equals("N"))
			iTotali = false;
		if (sd != null && sd.getAttValue("DettagliCommessa") != null && sd.getAttValue("DettagliCommessa").equals("N"))
			iDettagliCommessa = false;
		if (sd != null && sd.getAttValue("DettagliSottoCommesse") != null && sd.getAttValue("DettagliSottoCommesse").equals("N"))
			iDettagliSottoCommesse = false;
		if (sd != null && sd.getAttValue("ComponentiPropri") != null && sd.getAttValue("ComponentiPropri").equals("Y"))
			iComponentiPropri = true;
		if (sd != null && sd.getAttValue("SoloComponentiValorizzate") != null && sd.getAttValue("SoloComponentiValorizzate").equals("Y"))
			iSoloComponentiValorizzate = true;
	}

	public void setAzienda(Azienda azienda) {
		setIdAziendaInternal(azienda.getKey());
		this.iAzienda.setObject(azienda);
	}

	public Azienda getAzienda() {
		return (Azienda) iAzienda.getObject();
	}

	public void setAziendaKey(String key) {
		iAzienda.setKey(key);
		setIdAziendaInternal(key);
	}

	public String getAziendaKey() {
		return iAzienda.getKey();
	}

	public void setIdAzienda(String idAzienda) {
		setIdAziendaInternal(idAzienda);
	}

	public String getIdAzienda() {
		String key = iAzienda.getKey();
		String objIdAzienda = KeyHelper.getTokenObjectKey(key, 1);
		return objIdAzienda;
	}

	public void setCommessa(Commessa commessa) {
		String idAzienda = null;
		String idCommessa = null;

		if (commessa != null) {
			idAzienda = KeyHelper.getTokenObjectKey(commessa.getKey(), 1);
			idCommessa = KeyHelper.getTokenObjectKey(commessa.getKey(), 2);
		}
		setIdAziendaInternal(idAzienda);
		setIdCommessaInternal(idCommessa);
		this.iCommessa.setObject(commessa);
	}

	public Commessa getCommessa() {
		return (Commessa) iCommessa.getObject();
	}

	public void setCommessaKey(String key) {
		iCommessa.setKey(key);
		String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
		setIdAziendaInternal(idAzienda);

		String idCommessa = KeyHelper.getTokenObjectKey(key, 2);
		setIdCommessaInternal(idCommessa);
	}

	public String getCommessaKey() {
		return iCommessa.getKey();
	}

	public void setIdCommessa(String idCommessa) {
		String key = iCommessa.getKey();
		iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessa));
		setIdCommessaInternal(idCommessa);
	}

	public String getIdCommessa() {
		String key = iCommessa.getKey();
		String objIdCommessa = KeyHelper.getTokenObjectKey(key, 2);
		return objIdCommessa;
	}

	protected void setIdAziendaInternal(String idAzienda) {
		iAzienda.setKey(idAzienda);
		String key1 = iCommessa.getKey();
		iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
	}

	protected void setIdCommessaInternal(String idCommessa) {
		String key1 = iCommessa.getKey();
		iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, idCommessa));
	}
	
	public ErrorMessage checkIdCommessa() {
		if(getCommessa() != null && getCommessa().getCommessaAppartenenza() != null )
			return new ErrorMessage("THIP_TN723");//Non è possibile inserire una sotto commessa
		else {
			if(getCommessa() != null && getCommessa().getArticolo() == null) {
				return new ErrorMessage("THIP_TN796");//Commessa con articolo non valorizzato.
			}
			
			if(getCommessa() != null && getCommessa().getArticoloVersione() == null) {
				return new ErrorMessage("THIP_TN797");//Commessa con versione articolo non valorizzata.
			}
			
			if(getCommessa() != null && getCommessa().getArticolo().isConfigurato() && getCommessa().getConfigurazione() == null){
				return new ErrorMessage("THIP_TN798");//Commessa con articolo configurato e configurazione vuota
			}
			  
			if(getCommessa() != null && getCommessa().getStabilimento() == null){
				return new ErrorMessage("THIP_TN799");//Commessa con stabilimento non valorizzato
			}
			
			if(getCommessa() != null && getCommessa().getDatiComuniEstesi() != null &&  getCommessa().getDatiComuniEstesi().getStato() == DatiComuniEstesi.ANNULLATO){
				return new ErrorMessage("THIP_TN800");//Commessa con stato annullato
			}
			
			if(getCommessa() != null && getCommessa().getTipoCommessa() != null && (getCommessa().getTipoCommessa().getNaturaCommessa() != TipoCommessa.NATURA_CMM__GEST_INTERNA && getCommessa().getTipoCommessa().getNaturaCommessa() != TipoCommessa.NATURA_CMM__GESTIONALE)) {
				return new ErrorMessage("THIP_TN801");//Commessa con natura commessa errata
			}
		}
		return null;
	}
	
	public ErrorMessage checkDataInizio() {
		if(getDataInizio() != null && getDataFine() != null && getDataInizio().compareTo(getDataFine()) > 0) {
			return new ErrorMessage("THIP_TN889");
		}
		return null;
	}
	
	public JSONArray getColumnsDescriptors() throws JSONException {
		JSONArray columns = new JSONArray();
		columns.put(getCmpColumnDescriptor());
		List<ConsuntivoCommessa> consuntivi = getConsuntivi(getIdCommessa());
		if(consuntivi != null && !consuntivi.isEmpty()) {
			Iterator<ConsuntivoCommessa> iterConsuntivo = consuntivi.iterator();
			while(iterConsuntivo.hasNext()) {
				ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)iterConsuntivo.next();
				columns.put(getColumnDescriptorsConsuntivo(consuntivo));
			}
		}
		return columns;
	}
	
	public JSONObject getColumnDescriptorsConsuntivo(ConsuntivoCommessa consuntivo) throws JSONException {
		JSONObject consuntivoColumnDescriptors = new JSONObject();
		consuntivoColumnDescriptors.put("title", consuntivo.getDataRiferimento()).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", true));
		consuntivoColumnDescriptors.put("colModel", getConsuntivoColumnModel(consuntivo));
		return consuntivoColumnDescriptors;		
	}
	
	public JSONObject getCmpColumnDescriptor() throws JSONException {
		JSONObject column = new JSONObject();
		column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", "CompCosto").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold")).put("editable", false);
		return column;		
	}
	
	public JSONArray getConsuntivoColumnModel(ConsuntivoCommessa consuntivo) throws JSONException {		
		JSONArray columns = new JSONArray();
		if(consuntivo == null)
			consuntivo = (ConsuntivoCommessa)Factory.createObject(ConsuntivoCommessa.class);
		if(consuntivo.isConsolidato())
			columns.put(getCompConsuntivoColumnDescriptors("Consolidato", consuntivo));
		if(consuntivo.isEstrazioneRichieste())
			columns.put(getCompConsuntivoColumnDescriptors("Richiesto", consuntivo));
		if(consuntivo.isEstrazioneOrdini())
			columns.put(getCompConsuntivoColumnDescriptors("Ordinato", consuntivo));
		columns.put(getCompConsuntivoColumnDescriptors("Sostenuto", consuntivo));
		columns.put(getCompConsuntivoColumnDescriptors("Totale", consuntivo));
		return columns;
	}
	
	public JSONObject getCompConsuntivoColumnDescriptors(String title, ConsuntivoCommessa consuntivo) throws JSONException {
		JSONObject column = new JSONObject();
		if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
			column.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", true));
			column.put("colModel", getColumnModelATempo(title, consuntivo));				
		}
		else {
			String dataIndx = title.substring(0, 3);
			column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx + "Val" + consuntivo.getIdConsuntivo()).put("dataType", "float").put("format", "##.###,00");
			if(title.equals("Totale"))
				column.put("style", new JSONObject().put("font-weight", "bold"));
		}
		return column;		
	}
	
	public JSONArray getColumnModelATempo(String title, ConsuntivoCommessa consuntivo) throws JSONException {
		JSONArray columns = new JSONArray();
		String dataIndx = title.substring(0, 3);
		JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx + "HH" + consuntivo.getIdConsuntivo()).put("dataType", "float").put("format", "##.###");
		if(title.equals("Totale"))
			columnOre.put("style", new JSONObject().put("font-weight", "bold"));
		columns.put(columnOre);
		JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx + "Val"  + consuntivo.getIdConsuntivo()).put("dataType", "float").put("format", "##.###,00");
		if(title.equals("Totale"))
			columnValore.put("style", new JSONObject().put("font-weight", "bold"));
		columns.put(columnValore);
		return columns;
	}

	public JSONArray getDataModel() {
		JSONArray data = new JSONArray();
		Commessa commessaPrm = getCommessa();
		List<ConsuntivoCommessa> consuntivi = getConsuntivi(getIdCommessa());
		if(commessaPrm != null && consuntivi != null && !consuntivi.isEmpty()) {
			try {
				writeJSONDati(data, "1", null, commessaPrm, null, consuntivi);
			} 
			catch (Exception e) {
				e.printStackTrace(Trace.excStream);
			}
		}		
		return data;
	}	

	@SuppressWarnings("unchecked")
	protected void writeJSONDati(JSONArray data, String id, String idParent, Commessa commessaPrm, Commessa commessaApp, List<ConsuntivoCommessa> consuntivi) throws Exception {
		String parent = "";
		Commessa commessa = commessaPrm;
		if(commessaApp != null) {
			commessa = commessaApp;
		}

		if(idParent != null) {			
			parent = idParent;
		}
		
		List<ConsuntivoCommessaDet> dettagliTotale = cercaConsuntivoCommessaDets(consuntivi, true, null);
		
		//write gruppamento Commessa
		int idxGrup = 1;	
		writeCommessaGruppo(data,id, parent, commessa, dettagliTotale);
		
		//write gruppamento Totali
		if(isTotali()) {
			String idGruppTot = id + idxGrup;
			writeTotale(data, idGruppTot, id, commessaPrm.hasCompenenteATempo(), false, dettagliTotale);
			//write dettagli totale
			if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
				writeTotaleDettagli(data, id, idGruppTot, commessaPrm.hasCompenenteATempo(), consuntivi);
		}
		
		//write gruppamento componenti propri
		if(isComponentiPropri()) {
			idxGrup++;
			String idGruppCmp = id + idxGrup;
			writeComponentePropri(data, idGruppCmp, id, commessaPrm.hasCompenenteATempo(), false,dettagliTotale);
			//write dettagli componenti propri
			if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
				writeComponentePropriDettagli(data, id, idGruppCmp, commessaPrm.hasCompenenteATempo(), consuntivi);
		}
		
		List<Commessa> sottoCommesse = getSottoCommesse(commessaPrm, commessa);
		if(sottoCommesse == null || sottoCommesse.isEmpty())
			return;
      
		for(int i = 0; i < sottoCommesse.size(); i++) {
			Commessa sottoCommessa = (Commessa)sottoCommesse.get(i);
			List<ConsuntivoCommessa> sottoConsuntivi = new ArrayList<ConsuntivoCommessa>();
			for(ConsuntivoCommessa curCons : consuntivi) {
				ConsuntivoCommessa curSottoCons = curCons.getConsuntivoLivelloInferiore(sottoCommessa);
				if(curSottoCons.isOnDB() == false)
					curSottoCons.calcolaAlberoConsuntivi();
				sottoConsuntivi.add(curCons);
			}
          
			if(sottoConsuntivi != null && !sottoConsuntivi.isEmpty()) {
				idxGrup++;
				String idSottoCmm = id + idxGrup;
				writeJSONDati(data, idSottoCmm, id, commessaPrm, sottoCommessa, sottoConsuntivi); 
			}        
		}
	}

	public void writeTotaleDettagli(JSONArray data, String id, String idParent, boolean isATempo, List<ConsuntivoCommessa> consuntivi) {
		int idxDet = 1;
		if(consuntivi != null && !consuntivi.isEmpty()) {
			ConsuntivoCommessa consuntivo = (ConsuntivoCommessa) consuntivi.get(0);
			List<ConsuntivoCommessaDet> consuntiviCommessaDet =  consuntivo.getConsuntivoCommessaDet();
			for(int i = 0; i < consuntiviCommessaDet.size(); i++) {
				ConsuntivoCommessaDet dettaglio = consuntiviCommessaDet.get(i);
				List<ConsuntivoCommessaDet> dettagli = cercaConsuntivoCommessaDets(consuntivi, false, dettaglio.getIdComponCosto());
				if((!isSoloComponentiValorizzate()) || (isSoloComponentiValorizzate() && hasDettagliValorizzate(dettagli, true))) {
					String idDet = idParent + idxDet;
					writeTotale(data, idDet, idParent, isATempo, true, dettagli);
					idxDet ++;
				}
			}
		}
	}
	
	public void writeTotale(JSONArray data, String id, String idParent, boolean isCmpAtempo, boolean isDettaglio, List<ConsuntivoCommessaDet> dettagli) {
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("parentId", idParent);
			if(!isDettaglio) {
				json.put("pq_rowstyle", "background-color: #F0F0F0;font-weight: bold;");
				json.put("CompCosto", ResourceLoader.getString(RES_FILE, "Totali"));
			}
			
			Iterator<ConsuntivoCommessaDet> iterDettagli = dettagli.iterator();
			while(iterDettagli.hasNext()) {
				ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)iterDettagli.next();
				if(isDettaglio) {
					json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
				}
				
				if(dettaglio.getConsuntivoCommessa().isConsolidato()) {
					json.put("ConVal" + dettaglio.getIdConsuntivo(), dettaglio.getConsolidato().getCostoTotale());
				}
				json.put("RicVal" + dettaglio.getIdConsuntivo(), dettaglio.getRichiesto().getCostoTotale());
				json.put("OrdVal" + dettaglio.getIdConsuntivo(), dettaglio.getOrdinato().getCostoTotale());
				json.put("SosVal" + dettaglio.getIdConsuntivo(), dettaglio.getEffettuato().getCostoTotale());
				json.put("TotVal" + dettaglio.getIdConsuntivo(), dettaglio.getTotale().getCostoTotale());
				
				if(isCmpAtempo) {
					if(dettaglio.getComponenteCosto().isGestioneATempo()) {						
						if(dettaglio.getConsuntivoCommessa().isConsolidato()) {
							json.put("ConHH" + dettaglio.getIdConsuntivo(), dettaglio.getConsolidato().getTempoTotale());
						}
						json.put("RicHH" + dettaglio.getIdConsuntivo(), dettaglio.getRichiesto().getTempoTotale());
						json.put("OrdHH" + dettaglio.getIdConsuntivo(), dettaglio.getOrdinato().getTempoTotale());
						json.put("SosHH" + dettaglio.getIdConsuntivo(), dettaglio.getEffettuato().getTempoTotale());
						json.put("TotHH" + dettaglio.getIdConsuntivo(), dettaglio.getTotale().getTempoTotale());
					}
					else {
						if(dettaglio.getConsuntivoCommessa().isConsolidato()) {
							json.put("ConHH" + dettaglio.getIdConsuntivo(), "");
						}
						json.put("RicHH" + dettaglio.getIdConsuntivo(), "");
						json.put("OrdHH" + dettaglio.getIdConsuntivo(), "");
						json.put("SosHH" + dettaglio.getIdConsuntivo(), "");
						json.put("TotHH" + dettaglio.getIdConsuntivo(), "");						
					}					
				}				
			}			
			data.put(json);
		}
		catch (JSONException e) {
			e.printStackTrace(Trace.excStream);
		}
	}
	
	public void writeComponentePropriDettagli(JSONArray data, String id, String idParent, boolean isATempo, List<ConsuntivoCommessa> consuntivi) {
		int idxDet = 1;
		if(consuntivi != null && !consuntivi.isEmpty()) {
			ConsuntivoCommessa consuntivo = (ConsuntivoCommessa) consuntivi.get(0);
			List<ConsuntivoCommessaDet> consuntiviCommessaDet =  consuntivo.getConsuntivoCommessaDet();
			for(int i = 0; i < consuntiviCommessaDet.size(); i++) {
				ConsuntivoCommessaDet dettaglio = consuntiviCommessaDet.get(i);
				List<ConsuntivoCommessaDet> dettagli = cercaConsuntivoCommessaDets(consuntivi, false, dettaglio.getIdComponCosto());
				if((!isSoloComponentiValorizzate()) || (isSoloComponentiValorizzate() && hasDettagliValorizzate(dettagli, false))) {
					String idDet = idParent + idxDet;
					writeComponentePropri(data, idDet, idParent, isATempo, true, dettagli);
					idxDet ++;
				}
			}
		}
	}
	
	public boolean hasDettagliValorizzate(List<ConsuntivoCommessaDet> dettagli, boolean isTotale) {
		Iterator<ConsuntivoCommessaDet> iterDettagli = dettagli.iterator();
		while(iterDettagli.hasNext()) {
			ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)iterDettagli.next();
			if(isTotale) {
				if(dettaglio.getTotale().getCostoTotale() != null && dettaglio.getTotale().getCostoLivello().compareTo(new BigDecimal(0)) > 0) {
					return true;
				}
			}
			else {
				if(dettaglio.getTotale().getCostoLivello() != null && dettaglio.getTotale().getCostoLivello().compareTo(new BigDecimal(0)) > 0) {
					return true;
				}
			}			
		}
		return false;
	}
	
	public void writeComponentePropri(JSONArray data, String id, String idParent, boolean isATempo, boolean isDettaglio, List<ConsuntivoCommessaDet> dettagli) {
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("parentId", idParent);
			if(!isDettaglio) {
				json.put("pq_rowstyle", "background-color: #F0F0F0;font-weight: bold;");
				json.put("CompCosto", ResourceLoader.getString(RES_FILE, "ComponentiProprie"));
			}
			
			Iterator<ConsuntivoCommessaDet> iterDettagli = dettagli.iterator();
			while(iterDettagli.hasNext()) {
				ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)iterDettagli.next();
				if(isDettaglio) {
					json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
				}

				if(dettaglio.getConsuntivoCommessa().isConsolidato()) {
					json.put("ConVal" + dettaglio.getIdConsuntivo(), dettaglio.getConsolidato().getCostoLivello());
				}
				json.put("RicVal" + dettaglio.getIdConsuntivo(), dettaglio.getRichiesto().getCostoLivello());
				json.put("OrdVal" + dettaglio.getIdConsuntivo(), dettaglio.getOrdinato().getCostoLivello());
				json.put("SosVal" + dettaglio.getIdConsuntivo(), dettaglio.getEffettuato().getCostoLivello());
				json.put("TotVal" + dettaglio.getIdConsuntivo(), dettaglio.getTotale().getCostoLivello());
				if(isATempo) {
					if(dettaglio.getComponenteCosto().isGestioneATempo()) {					
						if(dettaglio.getConsuntivoCommessa().isConsolidato()) {
							json.put("ConHH" + dettaglio.getIdConsuntivo(), dettaglio.getConsolidato().getTempoLivello());
						}
						json.put("RicHH" + dettaglio.getIdConsuntivo(), dettaglio.getRichiesto().getTempoLivello());
						json.put("OrdHH" + dettaglio.getIdConsuntivo(), dettaglio.getOrdinato().getTempoLivello());
						json.put("SosHH" + dettaglio.getIdConsuntivo(), dettaglio.getEffettuato().getTempoLivello());
						json.put("TotHH" + dettaglio.getIdConsuntivo(), dettaglio.getTotale().getTempoLivello());
					}
					else {
						if(dettaglio.getConsuntivoCommessa().isConsolidato()) {
							json.put("ConHH" + dettaglio.getIdConsuntivo(), "");
						}
						json.put("RicHH" + dettaglio.getIdConsuntivo(), "");
						json.put("OrdHH" + dettaglio.getIdConsuntivo(), "");
						json.put("SosHH" + dettaglio.getIdConsuntivo(), "");
						json.put("TotHH" + dettaglio.getIdConsuntivo(), "");						
					}
				}
			}			
			data.put(json);
		}
		catch (JSONException e) {
			e.printStackTrace(Trace.excStream);
		}
	}
	
	protected List<Commessa> getSottoCommesse(Commessa commessaPrm, Commessa commessaApp) {
		try {
			String whereSC = CommessaTM.ID_AZIENDA + "='" + commessaPrm.getIdAzienda() + "' AND " + CommessaTM.R_COMMESSA_PRM + "='" + commessaPrm.getIdCommessa() + "'";
			if(commessaApp != null) {
				whereSC += " AND " + CommessaTM.R_COMMESSA_APP + "='" + commessaApp.getIdCommessa() + "'";
			}
			else {
				whereSC += " AND " + CommessaTM.R_COMMESSA_APP + " IS NULL";
			}
			List<Commessa> sottoCommesse = Commessa.retrieveList(whereSC, CommessaTM.ID_COMMESSA, false);
			if(sottoCommesse != null && !sottoCommesse.isEmpty())
				return sottoCommesse;
		} 
		catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		} 
		
		return null;
	}
	
	public void writeCommessaGruppo(JSONArray data, String id, String parentId, Commessa commessa, List<ConsuntivoCommessaDet> dettagli) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("parentId", parentId);
		json.put("pq_rowstyle", "background-color: #E5F4FF;font-weight: bold;");		
		json.put("Commessa", commessa.getDescrizione().getDescrizione());
		json.put("Sottocommessa", "");
		json.put("CompCosto", commessa.getIdCommessa() + " - " + commessa.getDescrizione().getDescrizione());
	
		Iterator<ConsuntivoCommessaDet> iterDettagli = dettagli.iterator();
		while(iterDettagli.hasNext()) {
			ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)iterDettagli.next();
			
			if (dettaglio.getConsuntivoCommessa().isConsolidato()) {
				json.put("ConVal" + dettaglio.getIdConsuntivo(), "" + dettaglio.getConsolidato().getCostoTotale());
			}
			json.put("RicVal" + dettaglio.getIdConsuntivo(), "" + dettaglio.getRichiesto().getCostoTotale());
			json.put("OrdVal" + dettaglio.getIdConsuntivo(), "" + dettaglio.getOrdinato().getCostoTotale());
			json.put("SosVal" + dettaglio.getIdConsuntivo(), "" + dettaglio.getEffettuato().getCostoTotale());
			json.put("TotVal" + dettaglio.getIdConsuntivo(), "" + dettaglio.getTotale().getCostoTotale());
			if (commessa.getCommessaPrincipale().hasCompenenteATempo()) {
				if(dettaglio.getComponenteCosto().isGestioneATempo()) {
					if (dettaglio.getConsuntivoCommessa().isConsolidato()) {
						json.put("ConHH" + dettaglio.getIdConsuntivo(), "" + dettaglio.getConsolidato().getTempoTotale());
					}
					json.put("RicHH" + dettaglio.getIdConsuntivo(), "" + dettaglio.getRichiesto().getTempoTotale());
					json.put("OrdHH" + dettaglio.getIdConsuntivo(), "" + dettaglio.getOrdinato().getTempoTotale());
					json.put("SosHH" + dettaglio.getIdConsuntivo(), "" + dettaglio.getEffettuato().getTempoTotale());
					json.put("TotHH" + dettaglio.getIdConsuntivo(), "" + dettaglio.getTotale().getTempoTotale());
				}
				else {
					if (dettaglio.getConsuntivoCommessa().isConsolidato()) {
						json.put("ConHH" + dettaglio.getIdConsuntivo(), "");
					}
					json.put("RicHH" + dettaglio.getIdConsuntivo(), "");
					json.put("OrdHH" + dettaglio.getIdConsuntivo(), "");
					json.put("SosHH" + dettaglio.getIdConsuntivo(), "");
					json.put("TotHH" + dettaglio.getIdConsuntivo(), "");
				}
			}			
		}
		data.put(json);
	}
	
	public List<ConsuntivoCommessaDet> cercaConsuntivoCommessaDets(List<ConsuntivoCommessa> consuntivi, boolean totale, String idComp) {
		List<ConsuntivoCommessaDet> ret = new ArrayList<ConsuntivoCommessaDet>();
		if(consuntivi != null && !consuntivi.isEmpty()) {
			Iterator<ConsuntivoCommessa> iterConsuntivi = consuntivi.iterator();
			while(iterConsuntivi.hasNext()) {
				ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)iterConsuntivi.next();
				ConsuntivoCommessaDet dettaglio = null;
				if(totale) {
				   dettaglio = consuntivo.getDettaglio(consuntivo, consuntivo.getIdComponenteTotali(), false);
				}
				else {
					dettaglio = consuntivo.getDettaglio(consuntivo, idComp, false);
				}
				ret.add(dettaglio);
			}			
		}
		return ret;
	}
}

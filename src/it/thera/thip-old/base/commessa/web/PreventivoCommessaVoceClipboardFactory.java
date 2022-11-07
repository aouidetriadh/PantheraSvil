package it.thera.thip.base.commessa.web;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.clipboard.ClipboardFactory;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DecimalType;

import it.thera.thip.acquisti.generaleAC.CondizioniDiAcquisto;
import it.thera.thip.base.agentiProvv.Agente;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.articolo.ArticoloCosto;
import it.thera.thip.base.articolo.ArticoloVersione;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.cliente.ClienteVendita;
import it.thera.thip.base.cliente.ModalitaPagamento;
import it.thera.thip.base.commessa.PreventivoCommessaRiga;//30569
import it.thera.thip.base.commessa.PreventivoCommessaTestata;
import it.thera.thip.base.commessa.PreventivoCommessaVoce;
import it.thera.thip.base.comuniVenAcq.web.RecuperaDatiAcquisto;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.base.listini.ListinoVendita;
import it.thera.thip.base.partner.Valuta;
import it.thera.thip.base.risorse.Risorsa;//30569
import it.thera.thip.datiTecnici.configuratore.Configurazione;
import it.thera.thip.datiTecnici.configuratore.ConfigurazioneRicEnh;
import it.thera.thip.datiTecnici.costi.Costo;
import it.thera.thip.datiTecnici.costi.CostoArticoloTM;
import it.thera.thip.datiTecnici.costi.CostoTM;
import it.thera.thip.datiTecnici.modpro.ModproEsplosione;
import it.thera.thip.vendite.generaleVE.CondizioniDiVendita;
import it.thera.thip.vendite.generaleVE.RicercaCondizioniDiVendita;

/*
 * Revision:
 * Number     Date        Owner  Description
 * 29166      24/05/2019  RA     Prima versione
 * 29642	  17/07/2019  RA	 Revisione preventivi di commessa
 * 30569	  23/01/2019  RA     Corretto valorizzazione Markup è recupero secondo le logiche impostate nelle righe di livello superiore.
 * 31376      11/06/2020  DB     non carica correttamente la versione
 * 31451      25/06/2020  Mekki  Valorizza sequenza
 * 31854	  25/09/2020  RA	 Corretta problema di NullPointerException
 * 32048	  16/10/2020  RA	 Corretta problema di copia preventivo
 */

public class PreventivoCommessaVoceClipboardFactory extends ClipboardFactory {
	
	public void completeDestValues(BODataCollector dest, BODataCollector origin) {
		super.completeDestValues(dest, origin);
		completeDestValuesPreComune(dest, origin);
		completeDestValuesPostComune(dest, origin);
	}

	public void completeDestValuesPreComune(BODataCollector dest, BODataCollector origin) {
		PreventivoCommessaVoceDataCollector pcvDC = (PreventivoCommessaVoceDataCollector)dest;
		pcvDC.completaDocumento();
		
		PreventivoCommessaVoce voce = (PreventivoCommessaVoce)dest.getBo();
		String classname = dest.getClassADCollection().getOriginalClassAdCollectionName();
		
		if(classname.equals("PreventivoComArticolo")){
			voce.setTipoRigav(PreventivoCommessaVoce.TP_RIG_ARTICOLO);
			voce.initSequenza(false); //Fix 31451
		}
		else {
			voce.setTipoRigav(PreventivoCommessaVoce.TP_RIG_VOCE);
			if(classname.equals("PreventivoComVoceDet")){
				Hashtable fatherValues = getFatherValues();
				String idAzienda = (String) fatherValues.get("IdAzienda");
				String idAnnoPrevc = (String) fatherValues.get("IdAnnoPrevc");
				String idNumeroPrevc = (String) fatherValues.get("IdNumeroPrevc");
				String idRigavPrv = (String)fatherValues.get("IdRigavPrv");
				String idRigacPrv = (String)fatherValues.get("IdRigacPrv");
				String parentKey = KeyHelper.buildObjectKey(new Object[]{idAzienda,idAnnoPrevc,idNumeroPrevc,idRigacPrv, idRigavPrv});
				try {
					PreventivoCommessaVoce parentVoce =  PreventivoCommessaVoce.elementWithKey(parentKey, PersistentObject.NO_LOCK);
					int sequenza = 1;
					if(!parentVoce.getRighe().isEmpty()){
						sequenza = parentVoce.getRighe().size() + 1;
					}
					voce.setSequenzaRiga(sequenza); //Fix 31451
					voce.setGeneraRigaDettaglio(false);
					voce.setIdSubRigavPrv(sequenza);
					voce.setIdRigavPrv(parentVoce.getIdRigavPrv());
					voce.setIdSubRigacPrv(voce.getPrevComRiga().getIdRigacPrvApp());
					
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			else{ //Fix 31451 --inizio
			  voce.initSequenza(false);
			} //Fix 31451 --fine
		}
	}

	public void completeDestValuesPostComune(BODataCollector dest, BODataCollector origin) {
		dest.loadAttValue();
		PreventivoCommessaVoce voce = (PreventivoCommessaVoce)dest.getBo();
		if(voce.getArticolo() != null && voce.getArticolo().getClasseMerclg() != null){
			if(voce.getArticolo().getClasseMerclg().getComponenteCosto() != null)//31854
				voce.setComponenteCosto(voce.getArticolo().getClasseMerclg().getComponenteCosto());
			if(voce.getArticolo().getClasseMerclg().getSchemaCosto() != null)//31854
				voce.setSchemaCosto(voce.getArticolo().getClasseMerclg().getSchemaCosto());
		}
		voce.setIdVersione(new Integer("1"));
		//29642 inizio
		if(voce.getArticolo() != null && voce.getArticolo().getArticoloDatiVendita() != null && voce.getArticolo().getArticoloDatiVendita().getIdUMPrimaria() != null && !voce.getArticolo().getArticoloDatiVendita().getIdUMPrimaria().equals(""))
			voce.setIdUmVen(voce.getArticolo().getArticoloDatiVendita().getIdUMPrimaria());
		else
			voce.setIdUmVen(voce.getArticolo().getIdUMPrmMag());
		//29642 fine
		voce.setIdUmPrmMag(voce.getArticolo().getIdUMPrmMag());
		voce.setIdUmSecMag(voce.getArticolo().getIdUMSecMag());
		//30569 inizio
		String classname = dest.getClassADCollection().getOriginalClassAdCollectionName();
		if(classname.equals("PreventivoComVoceDet")){
			Hashtable fatherValues = getFatherValues();
			String idAzienda = (String) fatherValues.get("IdAzienda");
			String idAnnoPrevc = (String) fatherValues.get("IdAnnoPrevc");
			String idNumeroPrevc = (String) fatherValues.get("IdNumeroPrevc");
			String idRigavPrv = (String)fatherValues.get("IdRigavPrv");
			String idRigacPrv = (String)fatherValues.get("IdRigacPrv");
			String parentKey = KeyHelper.buildObjectKey(new Object[]{idAzienda,idAnnoPrevc,idNumeroPrevc,idRigacPrv, idRigavPrv});
			try{
				PreventivoCommessaVoce parentVoce =  PreventivoCommessaVoce.elementWithKey(parentKey, PersistentObject.NO_LOCK);
				if(parentVoce != null && parentVoce.getPrevComRiga() != null &&  parentVoce.getPrevComRiga().getTestata() != null && parentVoce.getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP ){
					voce.setMarkup(parentVoce.getMarkup());
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else{
			PreventivoCommessaRiga riga = voce.getPrevComRiga();
			if(riga != null && riga.getTestata() != null && riga.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP ){
				if(voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA){
					if(voce.getTipoRisorsa() == Risorsa.RISORSE_UMANE){
						voce.setMarkup(riga.getMarkupUomo());
					}
					if(voce.getTipoRisorsa() == Risorsa.MACCHINE){
						voce.setMarkup(riga.getMarkupMacchina());
					}
				}
				else{
					voce.setMarkup(riga.getMarkupArticolo());
				}
			}		
		}
		//30569 fine		
		calcoloPrezziCosto(voce);
	}
	
	//29642 inizio
	public void preSaveDest(BODataCollector dest, BODataCollector origin) {
		PreventivoCommessaVoce voce = (PreventivoCommessaVoce)dest.getBo();
		if(voce.getDescrizione()!= null && (voce.getDescrizione().getDescrizioneRidotta() == null || voce.getDescrizione().getDescrizioneRidotta().equals("") || voce.getDescrizione().getDescrizioneRidotta().length() > 15)){
			voce.getDescrizione().setDescrizioneRidotta(voce.getArticolo().getDescrizioneArticoloNLS().getDescrizioneRidotta());
		}
		voce.getPrevComRiga().getTestata().setSalvoDaClipboard(true);//32048
		dest.setBo(voce);
	}
	//29642 fine

	public void calcoloPrezziCosto(PreventivoCommessaVoce voce){
		CondizioniDiVendita condVen = null;
		CondizioniDiAcquisto condAcq = null;
	    if (condVen == null){
	        condVen = getCondizioniVendita(voce);
	    }

	    if (condAcq == null){
	    	condAcq = getCondizioniAcquisto(voce);
	    }
	    	      
	    PreventivoCommessaTestata testata = voce.getPrevComRiga().getTestata();
	    BigDecimal costoRif = null;
	    BigDecimal costoRifTot = null;
	    String idVersione  = voce.getIdVersione().toString();
	    ArticoloVersione articoloVersione = getArticoloVersione(voce.getIdAzienda(), voce.getIdArticolo(), idVersione);
	    if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_LISTINO_ACQ || 
	       testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTLST_RSRCOS) {
	    	if(condAcq!=null)
	    		costoRif = condAcq.getPrezzo();
	    }
	    else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTICOLO_COSTO){
	    	if(testata.getTipoCosto() != null){
	    		Integer idVersioneArt = null;
	    		if (articoloVersione == null || articoloVersione.getIdVersione() == null) {
	    			idVersioneArt = ModproEsplosione.determinaVersione(testata.getIdAzienda(), voce.getIdArticolo(), testata.getDataRiferimento());
	    			if (idVersioneArt == null)
	    				idVersioneArt = new Integer(1);
	    		}
	    		else {
	    			idVersioneArt = articoloVersione.getIdVersione();  //fix 31376
	    		}
	    		Integer idConfig = null;
	    		if(voce.getArticolo() != null && voce.getArticolo().isConfigurato()){
	    			idConfig = voce.getIdConfigurazione();//29642
	    		}
	    		ArticoloCosto articoloCosto = ArticoloCosto.elementWithKey(voce.getIdAzienda(), voce.getIdArticolo(), idVersioneArt, idConfig, testata.getIdTipoCosto(), testata.getDataRiferimento());
	    		if (articoloCosto != null)
	    			costoRif = articoloCosto.getCosto();
	    	}
	    }
	    else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_AMBIENTE_COSTO){
	    	if(testata.getIdAmbiente() != null){
	    		Integer idVersioneArt = null;
	    		if (articoloVersione == null || articoloVersione.getIdVersione() == null) {
	    			idVersioneArt = ModproEsplosione.determinaVersione(voce.getIdAzienda(), voce.getIdArticolo(), testata.getDataRiferimento());
	    			if (idVersioneArt == null)
	    				idVersioneArt = new Integer(1);
	    		}
	    		else {
	    			idVersioneArt = articoloVersione.getIdVersione();  //fix 31376
	    		}
	    		Integer idConfig = null;
	    		if(voce.getArticolo() != null && voce.getArticolo().isConfigurato()){
	    			idConfig = voce.getIdConfigurazione();//29642
	    		}
	    		Costo costo = getCostoArticoloDaAmbiente(testata.getIdStabilimento(), voce.getIdArticolo(), idVersioneArt, idConfig, testata.getIdCommessa(), testata.getIdAmbiente());
	    		if (costo != null)
	    			costoRif =  costo.getCostoRiferimento();
	    	}
	    }
	      
	    DecimalType decType = new DecimalType();
	    if(costoRif != null){
	    	decType.setScale(6);
	    	voce.setCostoRifer(costoRif);

	    	costoRifTot = costoRif;
	    	if(voce.getQtaPrvUmVen() != null)
	    		costoRifTot = costoRif.multiply(voce.getQtaPrvUmVen());
	    	
	    	voce.setCosTotale(costoRifTot);

	    	if(testata.getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
	    		if(costoRif != null){        		
	    			BigDecimal markup = voce.getMarkup();
	    			if(markup.compareTo(new BigDecimal("0")) >= 0){
	          			BigDecimal perc = markup.divide(new BigDecimal(100.0), 6, BigDecimal.ROUND_HALF_UP);
	          			BigDecimal prezzo = costoRif.add(costoRif.multiply(perc));
	          			decType.setScale(6);
	          			voce.setPrezzo(prezzo);
	          			voce.setProvenienzaPrz(PreventivoCommessaVoce.PRV_PREZZO_MANUALE);//29642
	          		}
	          	}
	          }
	      }
	      
	    
	      if (condVen != null) {
	    	  if(condVen.getPrezzo() != null ){
	    		  voce.setPrezzo(condVen.getPrezzo());
	    	  }
	    	  if(condVen.getPrezzoExtra() != null ){
	    		  voce.setPrezzoExtra(condVen.getPrezzoExtra());
	    	  }
	    	  
	      }
	      
	      if(condAcq != null) {	    	  
	    	  voce.setCostoRifer(condAcq.getPrezzo());
	      }

	}

	public CondizioniDiVendita getCondizioniVendita(PreventivoCommessaVoce voce){
		CondizioniDiVendita condVendita = null;

		BigDecimal qtaSecMag = voce.getQtaPrvUmSec();		
		String idConfigurazione = voce.getIdEsternoConfig();
        String idVersione = voce.getIdVersione().toString();
        String idUMSecMag = voce.getIdUmSecMag();		
        Articolo articolo = voce.getArticolo();
        PreventivoCommessaTestata testata = voce.getPrevComRiga().getTestata();
		
        if(testata.getRepPrezzoArt() != PreventivoCommessaTestata.REP_PREZZO_DA_LST_VEND){
      	  return null;
        }

        if (articolo != null) {        	
        	try{
                String key = KeyHelper.buildObjectKey(new String[] {testata.getIdAzienda(), testata.getIdListinoVen()});
                ListinoVendita listino = ListinoVendita.elementWithKey(key, PersistentObject.NO_LOCK);
        		Configurazione configurazione = ConfigurazioneRicEnh.recuperaConfigurazione(testata.getIdAzienda(), articolo.getIdArticolo(), idConfigurazione);
        		key = KeyHelper.buildObjectKey(new String[] {testata.getIdAzienda(), testata.getIdCliente()});
        		ClienteVendita cliente = (ClienteVendita)ClienteVendita.elementWithKey(ClienteVendita.class, key, PersistentObject.NO_LOCK);
        		UnitaMisura umVendita = UnitaMisura.getUM(voce.getArticolo().getIdUMPrmMag());
        		BigDecimal quantitaVendita = new BigDecimal(1);
        		BigDecimal quantitaMag = new BigDecimal(1);
        		UnitaMisura umMag = UnitaMisura.getUM(voce.getArticolo().getIdUMPrmMag());
        		key = KeyHelper.buildObjectKey(new String[] {testata.getIdValuta()});
        		Valuta valuta = (Valuta)PersistentObject.readOnlyElementWithKey(Valuta.class, key);
        		ArticoloVersione versione = ArticoloVersione.elementWithKey(KeyHelper.buildObjectKey(new String[] {articolo.getIdAzienda(), articolo.getIdArticolo(), idVersione}), PersistentObject.NO_LOCK);
        		UnitaMisura umSecMag = null;
        		BigDecimal quantSecMagazzino = null;
        		if (idUMSecMag != null){
        			umSecMag = UnitaMisura.getUM(idUMSecMag);
        		}
        		
        		if(umSecMag != null && qtaSecMag != null) {
        			quantSecMagazzino = new BigDecimal(((qtaSecMag)).doubleValue());
        		}
		
        		ModalitaPagamento modPagamento = null;
        		Agente agente = null;
        		Agente subAgente = null;
        		boolean visualizzaDettagli = false;
        		BigDecimal prcScontoIntestatarioDec = new BigDecimal(0);
        		BigDecimal prcScontoModalitaDec = new BigDecimal(0);
        		String idScontoModalita = null;
        		BigDecimal numImballo = null;
        		RicercaCondizioniDiVendita ricerca = (RicercaCondizioniDiVendita)Factory.createObject(RicercaCondizioniDiVendita.class);
        		condVendita = ricerca.ricercaCondizioniDiVendita(voce.getIdAzienda(),
        					  listino, cliente, articolo, configurazione, umVendita, quantitaVendita, new BigDecimal(0.0),
        					  modPagamento, testata.getDataRiferimento(), agente, subAgente,
        					  umMag, quantitaMag, valuta, visualizzaDettagli,
        					  prcScontoIntestatarioDec, prcScontoModalitaDec, idScontoModalita,
        					  versione, numImballo, umSecMag, quantSecMagazzino);
        		
        		if (condVendita != null && cliente != null) {
        			BigDecimal prezzo = condVendita.getPrezzo();
        			if (prezzo == null || prezzo.equals(new BigDecimal(0.0))) {	
        				ListinoVendita listinoCliente = cliente.getListino();
        				ListinoVendita listinoAlternativo = cliente.getListinoAlternativo();
        				if (listinoCliente != null && listinoAlternativo != null && listinoCliente.equals(listinoAlternativo)) {
        					condVendita = ricerca.ricercaCondizioniDiVendita(voce.getIdAzienda(), listinoAlternativo, cliente, 
        																	 articolo, configurazione, umVendita, quantitaVendita,
        																	 new BigDecimal(0.0), modPagamento, testata.getDataFineVal(), agente, subAgente,
        																	 umMag, quantitaMag, valuta,
        																	 prcScontoIntestatarioDec, prcScontoModalitaDec, idScontoModalita,
        																	 versione, numImballo, umSecMag, quantSecMagazzino);
        				}
        			}
        		}
        	}
        	catch (Exception ex) {
        		ex.printStackTrace();
        	}
        }
		return condVendita;
	}
	
	protected CondizioniDiAcquisto getCondizioniAcquisto(PreventivoCommessaVoce voce) {
		String idArticolo = voce.getIdArticolo();
	    String idUmVen = voce.getArticolo().getArticoloDatiVendita().getIdUMPrimaria();
	    String qtaVendita = voce.getQtaPrvUmVen().toString();
	    String idConfigurazione = voce.getIdEsternoConfig();
	    String idVersione = voce.getIdVersione().toString();
	    String idUMSecMag = voce.getArticolo().getIdUMSecMag();
	    String qtaSecMag = voce.getQtaPrvUmSec().toString();

	    PreventivoCommessaTestata testata = voce.getPrevComRiga().getTestata();

	    if (qtaSecMag == null)
	    	qtaSecMag = "0";
	    String dataRif = null;
	    String dataConsegna = null;
	    if (testata.getDataRiferimento() != null)
	    	dataRif = testata.getDataRiferimento().toString();
	    if (testata.getDataConsegRcs() != null)
	    	dataConsegna = testata.getDataConsegRcs().toString();
	    CondizioniDiAcquisto condAcq = null;
	    if (testata.getListinoAcq() != null) {
	    	try{
	    		condAcq = RecuperaDatiAcquisto.getCondizioniAcquisto("",testata.getIdListinoAcq(), "", idArticolo, idConfigurazione,
	    															 idUmVen, qtaVendita, qtaVendita, dataRif, dataConsegna, idUmVen,
	    															 testata.getListinoAcq().getIdValuta(), 'N', null, "", "", "",
	    															 idVersione, "", idUMSecMag, qtaSecMag);
	    	}
        	catch (Exception ex) {
        		ex.printStackTrace();
        	}
	    }
	    return condAcq;
	}
	
	public ArticoloVersione getArticoloVersione(String idAzienda, String idArticolo, String idVersione) {
		ArticoloVersione av = null;
		String key = KeyHelper.buildObjectKey(new String[] {idAzienda, idArticolo, idVersione});
		try {
			av = ArticoloVersione.elementWithKey(key, PersistentObject.NO_LOCK);
		}
		catch (Exception ex) {
			ex.printStackTrace(Trace.excStream);
		}
		return av;
	}

	public Costo getCostoArticoloDaAmbiente(String idStabilimento, String idArticolo, Integer idVersione, Integer idConfig, String idCommessa, String idAmbCosti) {
		String idConfigClause = "";
		String idCommessaClause = "";
		String idAmbienteCostiClause = "";
		
		if (idConfig != null)
			idConfigClause = CostoArticoloTM.R_CONFIGURAZIONE + " = " + idConfig;
		else
			idConfigClause = CostoArticoloTM.R_CONFIGURAZIONE + " is null ";

		if (idCommessa != null)
			idCommessaClause = CostoArticoloTM.R_COMMESSA + " = '" + idCommessa + "'";
		else
			idCommessaClause = CostoArticoloTM.R_COMMESSA + " is null ";

		if (idAmbCosti != null)
			idAmbienteCostiClause = CostoArticoloTM.ID_AMBIENTE + " = '" + idAmbCosti + "'";
		else
			idAmbienteCostiClause = CostoArticoloTM.ID_AMBIENTE + " is null ";

		String idAzienda = Azienda.getAziendaCorrente();
		String caluseWhere = CostoTM.ID_AZIENDA + " = '" + idAzienda + "' AND " +
							 CostoTM.R_STABILIMENTO + " = '" + idStabilimento + "' AND " +
							 CostoArticoloTM.R_ARTICOLO + " = '" + idArticolo + "' AND " +
							 CostoArticoloTM.R_VERSIONE + " = " + idVersione + " AND " +
							 idConfigClause + " AND " +
							 idCommessaClause + " AND " +
							 idAmbienteCostiClause + " AND " +
							 CostoTM.TIPOLOGIA + " = 'A'";
		try {
			Vector costiList = Costo.retrieveList(caluseWhere, "", true);
			if (costiList.size() > 0)
				return (Costo) costiList.get(0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

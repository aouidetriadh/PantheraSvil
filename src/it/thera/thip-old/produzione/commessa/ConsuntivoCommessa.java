package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.common.Numerator;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Database;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.PersistentObjectCursor;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.commessa.CommessaTM;
import it.thera.thip.base.commessa.TipoCommessa;
import it.thera.thip.cs.DatiComuniEstesi;
import it.thera.thip.datiTecnici.PersDatiTecnici;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
import it.thera.thip.datiTecnici.costi.LinkCompSchema;

/**
 * ConsuntivoCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/08/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 33950   18/08/2021    RA		    Prima struttura
 */

public class ConsuntivoCommessa extends ConsuntivoCommessaPO {
	
	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa";
	public static final String ID_PROGR_NUM_ID = "CONSUNTIVO_CMM";
	
	public final static BigDecimal ZERO = new BigDecimal(0);
	
	//Stato avanzamento consuntivazione
	public final static char PROVVISORIO = '1';
	public final static char DEFINITIVO = '2';

	public static final String FORMATO_VALORI = "##.###,00";
	public static final String FORMATO_ORE = "##.###,00";
	public static final String FORMATO_PERC = "##.###,00";


	protected boolean daCalcoloConsuntivo = false;
	HashMap<String, ConsuntivoCommessaDet> consuntivoCommessaDetTmpMap = new HashMap<String, ConsuntivoCommessaDet>();
		
	protected boolean iTotali = true;
	protected boolean iDettagliCommessa = true;
	protected boolean iDettagliSottoCommesse = true;
	protected boolean iComponentiPropri = false;
	protected boolean iSoloComponentiValorizzate = false;	

	protected List<ConsuntivoCommessa> consuntiviLivelliInferiori = null;

	protected CalcolatoreDettagliCommesse calcolatoreDettagli = null;
	  
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
	
	public List<ConsuntivoCommessa> getConsuntiviLivelliInferiori()
	{
	   return consuntiviLivelliInferiori;
	}
	
	public boolean initializeOwnedObjects(boolean result) {
		ScreenData sd = ScreenData.getDefaultScreenData("ConsuntivoCommessa");
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
		result = super.initializeOwnedObjects(result);
	    return result;
	}
	
	public ErrorMessage checkDelete() {
		if(isCollegatoAVariazioneBudget())
			return new ErrorMessage("THIP_TN815");//Il consuntivo non è eliminabile perché referenziato in una variazione di budget
		return null;
	}
	
	public boolean isCollegatoAVariazioneBudget() {
		String where = VariaBudgetCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' AND " + 
					   VariaBudgetCommessaTM.ID_COMMESSA + " = '" + getIdCommessa() + "' AND " + 
			   	       VariaBudgetCommessaTM.ID_CONSUNTIVO + " = " + getIdConsuntivo();
		PersistentObjectCursor cursor = new PersistentObjectCursor(VariaBudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
		try {
			if(cursor.hasNext()) {
				return true;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return false;
	}

	public Integer getNextIdConsuntivo() throws Exception {
		return  new Integer(Numerator.getNextInt(ID_PROGR_NUM_ID));
	}
	
	public boolean isDaCalcoloConsuntivo() {
		return daCalcoloConsuntivo;
	}
	
	public void setDaCalcoloConsuntivo(boolean da) {
		daCalcoloConsuntivo = da;
	}

	
	public CalcolatoreDettagliCommesse getCalcolatoreDettagli()	{
	   if(calcolatoreDettagli == null)
	      calcolatoreDettagli = (CalcolatoreDettagliCommesse)Factory.createObject(CalcolatoreDettagliCommesse.class);
	   return calcolatoreDettagli;
	}

	public void setCalcolatoreDettagli(CalcolatoreDettagliCommesse calcolatoreDettagli)	{
	   this.calcolatoreDettagli = calcolatoreDettagli;
	}
	
	public int delete() throws SQLException {
		int rc = 0;
		if(getCommessa() != null && getCommessa().getCommessaAppartenenza() == null) {
			String where = ConsuntivoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' AND " + 
						   ConsuntivoCommessaTM.R_COMMESSA + " <> '" + getIdCommessa() + "' AND " + 
						   ConsuntivoCommessaTM.ID_CONSUNTIVO + " = " + getIdConsuntivo();
			PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
			  try {
				  while (cursor.hasNext()) {
					  ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)cursor.next();
					  int rc1 = consuntivo.delete();
					  if(rc1 < 0) {
						  rc = rc1;
						  break;
					  }
					  else
						  rc += rc1;
						  
				  }
			  } 
			  catch (SQLException e) {
				  e.printStackTrace(Trace.excStream);
			  }
		}
		if(rc >= 0)
			rc += super.delete();

		return rc;
	}
	
	public int save() throws SQLException {
		beforeSave();
		calcolaAlberoConsuntivi();
		salvaAlberoConsuntivi();
		return 1;	  
	}
	
	public void beforeSave() {
		if(!isOnDB() && getIdConsuntivo().compareTo(new Integer(0)) == 0 ) {
			try {
				setIdConsuntivo(getNextIdConsuntivo());
				if(getCommessa() != null) {
					setLivelloCommessa(getCommessa().getLivelloCommessa());
				}
			}
			catch(Exception ex) {
				ex.printStackTrace(Trace.excStream); 
			}
		}
	}

	public void caricaAlberoConsuntivi() {
	   caricaLivelliInferiori();
	   for(ConsuntivoCommessa consLivelloInferiore : consuntiviLivelliInferiori)
	      consLivelloInferiore.caricaAlberoConsuntivi();
	}
	
	public void calcolaAlberoConsuntivi() {
	   calcolaLivello();
	   caricaLivelliInferiori();
	   calcolaLivelliInferiori();
	   calcolaTotali();
	}
	
	public void calcolaLivello() {
	   Articolo articolo = this.getCommessaPrm().getArticolo();
	   List<LinkCompSchema> listComponenti = new ArrayList<LinkCompSchema>();
	   if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getSchemaCosto() != null)
	      listComponenti.addAll(articolo.getClasseMerclg().getSchemaCosto().getComponenti());

	   //azzero tutte e non solo quelle contenute nelle componenti articolo
	   Iterator iter = getConsuntivoCommessaDet().iterator();
	   while(iter.hasNext()) {
	      ConsuntivoCommessaDet curDet = (ConsuntivoCommessaDet)iter.next();
	      curDet.azzera();
	   }

	   List<Integer> idProgrStorici = cercaIdProgrStorici(getIdCommessa(), getDataRiferimento());
	   if(idProgrStorici !=  null && !idProgrStorici.isEmpty()) {
	      for(int idxProgStor = 0; idxProgStor < idProgrStorici.size(); idxProgStor++) {
	         Integer idProgrStorico = (Integer)idProgrStorici.get(idxProgStor);
	         List<StoricoCommessaDet> dettagliStorico = recuperaStoricoCommessaDet(getIdAzienda(), idProgrStorico, getIdCommessa(), isEstrazioneOrdini(), isEstrazioneRichieste());
	         if(dettagliStorico == null || dettagliStorico.size() == 0)
	            continue;

	         List<StoricoCommessaDet> dettagliEffettuati = getStoriciCommessaDetPerTipoDettaglio(dettagliStorico, StoricoCommessaDet.EFFETTUATO);                                  
	         List<StoricoCommessaDet> dettagliRichiesto = isEstrazioneRichieste() ? getStoriciCommessaDetPerTipoDettaglio(dettagliStorico, StoricoCommessaDet.RICHIESTO) : new ArrayList<StoricoCommessaDet>();
	         List<StoricoCommessaDet> dettagliOrdinato = isEstrazioneOrdini() ? getStoriciCommessaDetPerTipoDettaglio(dettagliStorico, StoricoCommessaDet.ORDINATO) : new ArrayList<StoricoCommessaDet>();
	         List<StoricoCommessaDet> dettagliConsolidati = isConsolidato() ? cercaDettagliConsolidati(dettagliEffettuati) : new ArrayList<StoricoCommessaDet>();

	         if(dettagliConsolidati != null && !dettagliConsolidati.isEmpty()) {
	            dettagliEffettuati = pulireListaEffettuate(dettagliEffettuati, dettagliConsolidati);
	         }

	         for(int idx = 0; idx < listComponenti.size(); idx++) {
	            LinkCompSchema linkCompSchema = listComponenti.get(idx);
	            ConsuntivoCommessaDet dettaglioConsuntivo = getDettaglio(this, linkCompSchema.getIdComponenteCosto());
	            aggiornaGruppo(dettaglioConsuntivo.getEffettuato(), dettagliEffettuati, linkCompSchema.getIdComponenteCosto());
	            aggiornaGruppo(dettaglioConsuntivo.getRichiesto(), dettagliRichiesto, linkCompSchema.getIdComponenteCosto());
	            aggiornaGruppo(dettaglioConsuntivo.getOrdinato(), dettagliOrdinato, linkCompSchema.getIdComponenteCosto());
	            aggiornaGruppo(dettaglioConsuntivo.getConsolidato(), dettagliConsolidati, linkCompSchema.getIdComponenteCosto());
	            aggiornaTotaleLivello(dettaglioConsuntivo);
	         }
	      }
	      applicaFormuleTotali();
	   }
	}

	public void applicaFormuleTotali()	{
	   getCalcolatoreDettagli().applicaFormuleTotali(getConsuntivoCommessaDet(), getArticolo());
	}

	public void aggiornaGruppo(CostiCommessaDetGruppo gruppo, List<StoricoCommessaDet> dettagliStorico, String idComponenteCosto) 
	{
	   if(dettagliStorico != null && !dettagliStorico.isEmpty()) 
	   {
	      StoricoCommessaDet dettaglioStorico = cercaStoricoCommessaDet(dettagliStorico, idComponenteCosto);
	      if(dettaglioStorico != null) 
	      {
	         gruppo.setCostoLivello(gruppo.getCostoLivello().add(dettaglioStorico.getCostoLivello()));
	         gruppo.setTempoLivello(gruppo.getTempoLivello().add(dettaglioStorico.getTempoLivello()));
	      }
	   }
	}

	public void aggiornaTotaleLivello(ConsuntivoCommessaDet consuntivoCommessaDet) 
	{      
	   BigDecimal totaleCostoLivello =  getTotale(consuntivoCommessaDet.getConsolidato().getCostoLivello(), 
	         consuntivoCommessaDet.getRichiesto().getCostoLivello(),
	         consuntivoCommessaDet.getOrdinato().getCostoLivello(),
	         consuntivoCommessaDet.getEffettuato().getCostoLivello());
	   consuntivoCommessaDet.getTotale().setCostoLivello(totaleCostoLivello);

	   BigDecimal totaleTempoLivello = getTotale(consuntivoCommessaDet.getConsolidato().getTempoLivello(), 
	         consuntivoCommessaDet.getRichiesto().getTempoLivello(),
	         consuntivoCommessaDet.getOrdinato().getTempoLivello(),
	         consuntivoCommessaDet.getEffettuato().getTempoLivello());
	   consuntivoCommessaDet.getTotale().setTempoLivello(totaleTempoLivello);
	}

	public void caricaLivelliInferiori()
	{
	   if(consuntiviLivelliInferiori == null)
	   {
	      consuntiviLivelliInferiori = new ArrayList<ConsuntivoCommessa>();
	      String where = CommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + CommessaTM.R_COMMESSA_APP + "='" + getIdCommessa() + "'";
	      List commesse;
	      try
	      {
	         commesse = Commessa.retrieveList(Commessa.class, where, "", false);
	         Iterator iter = commesse.iterator();
	         while(iter.hasNext())
	         {
	            Commessa commessa = (Commessa)iter.next();
	            ConsuntivoCommessa consuntivo = caricaConsuntivoCommessa(commessa);
	            if(consuntivo != null)
	               consuntiviLivelliInferiori.add(consuntivo);
	         }
	      }
	      catch(Exception e)
	      {
	         e.printStackTrace(Trace.excStream);
	      }
	   }
	}

	public void calcolaLivelliInferiori()
	{
	   for(ConsuntivoCommessa consLivelloInferiore : consuntiviLivelliInferiori)
	   {
	      consLivelloInferiore.calcolaAlberoConsuntivi();

	      Iterator iter = getConsuntivoCommessaDet().iterator();
	      while(iter.hasNext())
	      {
	         ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)iter.next();
	         ConsuntivoCommessaDet detLivelloInferiore = getDettaglio(consLivelloInferiore, dettaglio.getIdComponCosto());
	         if(detLivelloInferiore != null)
	         {
	            dettaglio.getConsolidato().sommaLivelloInferiore(detLivelloInferiore.getConsolidato());
	            dettaglio.getRichiesto().sommaLivelloInferiore(detLivelloInferiore.getRichiesto());     
	            dettaglio.getOrdinato().sommaLivelloInferiore(detLivelloInferiore.getOrdinato());
	            dettaglio.getEffettuato().sommaLivelloInferiore(detLivelloInferiore.getEffettuato());
	            dettaglio.getTotale().sommaLivelloInferiore(detLivelloInferiore.getTotale());
	         }
	      }
	   }
	}



	public void calcolaTotali()
	{
	   Iterator iter = getConsuntivoCommessaDet().iterator();
	   while(iter.hasNext())
	   {
	      ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)iter.next();

	      dettaglio.getConsolidato().calcolaTotali();
	      dettaglio.getRichiesto().calcolaTotali();  
	      dettaglio.getOrdinato().calcolaTotali();
	      dettaglio.getEffettuato().calcolaTotali();
	      dettaglio.getTotale().calcolaTotali();
	   }
	   valorizzaCostiConsuntivo();
	}


	public ConsuntivoCommessa getConsuntivoLivelloInferiore(Commessa commessa)
	{
	   ConsuntivoCommessa cons = null;
	   if(consuntiviLivelliInferiori != null)
	   {
	      for(ConsuntivoCommessa curCons : consuntiviLivelliInferiori)
	      {
	         if(curCons.getIdCommessa().equals(commessa.getIdCommessa()))
	            cons = curCons;
	      }
	   }

	   if(cons == null)
	      cons = creaNuovoConsuntivoCommessa(commessa);
	   return cons;
	}


	public void salvaAlberoConsuntivi()
	{
	   for(ConsuntivoCommessa consLivelloInferiore : consuntiviLivelliInferiori)
	      consLivelloInferiore.salvaAlberoConsuntivi();
	   try
	   {
	      super.save();
	   }
	   catch(SQLException e)
	   {
	      e.printStackTrace(Trace.excStream);
	   }
	}

	public Vector checkAll(BaseComponentsCollection components) {
		Vector errors = new Vector();
		components.runAllChecks(errors);
		return errors;
	}
	
	public String getIdDescUMPrmMag() {
		if((getCommessa() != null) && (getCommessa().getUmPrmMag() != null)) {
			return getCommessa().getUmPrmMag().getIdDescrizione();
		}
		return null;
	}
	
	public ErrorMessage checkIdCommessa() {
		if(getCommessa() != null && getCommessa().getCommessaAppartenenza() != null )
			return new ErrorMessage("THIP_TN723");//Non è possibile inserire una sotto commessa
		else {
			if(getCommessa() != null && getCommessa().getDataEstrazioneStorici() == null) {
				return new ErrorMessage("THIP_TN795");//Commessa con data estrazione storici vuota
			}
			
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
	
	public ErrorMessage checkDataRiferimento() {
		if(getDataRiferimento() == null || getDataRiferimento().compareTo(TimeUtils.getCurrentDate()) > 0)
			return new ErrorMessage("THIP_TN724");
		return null;
	}
	
	/*
	public void calcoloConsuntivo() {
		boolean ok = true;		
		List<Commessa> commesse = getCommesse();
		Collections.sort(commesse, new AggregazioneCostiCommessaComparator());	    
		ok = calcoloConsuntivoStorici(commesse);
		if(ok)
			ok = aggregazioneConsuntivo(commesse);
		try {
			if(ok)
				ConnectionManager.commit();
			else
				ConnectionManager.rollback();
		}
		catch(SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
	}
	
	public boolean aggregazioneConsuntivo(List<Commessa> commesse) {
		boolean ok = prepareConsuntivoDetPerAggregazione();
		if(ok) {
			
			for(int i = 0; i < commesse.size(); i++) {
				Commessa commessa = commesse.get(i);
				ok = aggregaCurrentConsuntivoCommessa(commessa, commessa.getCommessaAppartenenza());
				if(!ok) {
					ok = false;
					break;
				}
			}
			if(ok) {
				completaCalcoloConsuntivo();
			}
		}
		
		return true;
	}
	
	public void completaCalcoloConsuntivo() {
		List<ConsuntivoCommessa> consuntivi = getConsuntivi();
		for (int i = 0; i < consuntivi.size(); i++) {
			ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)consuntivi.get(i);
			try {
				int rc = aggiornaDetCalculabile(consuntivo);
			} 
			catch (SQLException e) {
				e.printStackTrace(Trace.excStream);
			}
			consuntivo.valorizzaCostiConsuntivo();
		}	
	}
	
	private int aggiornaDetCalculabile(ConsuntivoCommessa consuntivo) throws SQLException {
      List listaDettglio = consuntivo.getConsuntivoCommessaDet();
      List compDaValorizz = new ArrayList();
      List compValorizz = new ArrayList();
      Iterator iteDet = listaDettglio.iterator();
      int rc=0;
      int result=0;
      while (iteDet.hasNext()) {
    	  ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)iteDet.next();
    	  if ((dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.CALCOLATA_FORMULA ||
    			  dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE)&&
    			  isUtilizzoFormula(consuntivo.getArticolo(), dettaglio)) {
    		  compDaValorizz.add(dettaglio);
    	  }
    	  else {
    		  compValorizz.add(dettaglio);
    	  }
      }
      boolean found = false;

      while (compDaValorizz.size() != 0) {
        found = false;
        Iterator valorizIte = compDaValorizz.iterator();
        while (valorizIte.hasNext() && !found) {
        	ConsuntivoCommessaDet consuntivoDet = (ConsuntivoCommessaDet)valorizIte.next();        	
        	aggiorna(consuntivoDet, listaDettglio, compDaValorizz, ConsuntivoCommessaDet.TIPO_CONSOLIDATO);
        	aggiorna(consuntivoDet, listaDettglio, compDaValorizz, ConsuntivoCommessaDet.TIPO_RICHIESTO);
        	aggiorna(consuntivoDet, listaDettglio, compDaValorizz, ConsuntivoCommessaDet.TIPO_ORDINATO);
        	aggiorna(consuntivoDet, listaDettglio, compDaValorizz, ConsuntivoCommessaDet.TIPO_EFFETUATO);
        	aggiorna(consuntivoDet, listaDettglio, compDaValorizz, ConsuntivoCommessaDet.TIPO_TOTALE);
			rc = consuntivoDet.save();
			if (rc < ErrorCodes.NO_ROWS_UPDATED) {
				return rc;
			}
			result += rc;
    		compDaValorizz.remove(consuntivoDet);
    		compValorizz.add(consuntivoDet);
    		found = true;
        }
      }
      return result;
    }
	 
	public void aggiorna(ConsuntivoCommessaDet consuntivoDet, List listaDettglio, List compDaValorizz, char tipoGruppo) throws SQLException {
		FormulaCosti formulaCosti = consuntivoDet.getComponenteCosto().getFormula();
		
		AmbienteCosti ac = getAmbienteCosti();
		//AmbienteCosti ac = AmbienteCosti.getCurrentAmbienteCosti();
    	
      Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(consuntivoDet.getComponenteCosto(), ac.getIdAmbiente()));
     formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, false, tipoGruppo, true));
     if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), consuntivoDet)) {
        BigDecimal costoLivelloCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
        formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, false, tipoGruppo, false));
        BigDecimal costoTotaleCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
        CostiCommessaDetGruppo gruppo = consuntivoDet.getGruppo(tipoGruppo);
        gruppo.setCostoTotale(costoTotaleCalcolato);
        gruppo.setCostoLivello(costoLivelloCalcolato);
        gruppo.setCostoLivelloInf(ZERO);
        if(consuntivoDet.getComponenteCosto().isGestioneATempo()) {
           formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, true, tipoGruppo, true));
           BigDecimal tempoLivelloCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
           formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio, true, tipoGruppo, false));
           BigDecimal tempoTotaleCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
           gruppo.setTempoTotale(tempoTotaleCalcolato);
           gruppo.setTempoLivello(tempoLivelloCalcolato);
           gruppo.setTempoLivelloInf(ZERO);
        }
     }
	}
	
	public boolean isFormulaCalcolabileDouble(FormulaCosti formulaCosti, Formula formulaDaUtilizz, List compDaValorizz, List usedComp, ConsuntivoCommessaDet componente) throws SQLException {
		boolean formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
		if (!formCalc && !formulaCosti.equals(componente.getComponenteCosto().getFormula())) {
			formulaDaUtilizz = componente.getComponenteCosto().getFormula().getFormula();
			formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
		}
		return formCalc;
	}		  
	
	public static boolean isFormulaCalcolabile(List compDaValorizz, List usedComp, ConsuntivoCommessaDet corrente) {
		if (usedComp == null || usedComp.size() == 0) {
			return true;
		} 
		else {
			Iterator itComponenteUsed = usedComp.iterator();
			ComponenteCosto compCosto = null;
			while (itComponenteUsed.hasNext()) {
				compCosto = (ComponenteCosto) itComponenteUsed.next();
				Iterator itDettaglio = compDaValorizz.iterator();
				ConsuntivoCommessaDet dett = null;
				while (itDettaglio.hasNext()) {
					dett = (ConsuntivoCommessaDet) itDettaglio.next();
					if (compCosto.getIdComponenteCosto().equalsIgnoreCase(dett.getComponenteCosto().getIdComponenteCosto())) {
						if (corrente.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE) {
							return false;
						}
						if (!compCosto.getIdComponenteCosto().equals(corrente.getIdComponCosto())) {
							return false;
						}
					}
				}
			}
			return true;
		}
	}	
	
	public List compUsedInFormula(com.thera.thermfw.formula.Formula formula) {
		List compUsedList = new ArrayList();
		Set variabili = formula.getUsedVariables();
		Iterator variabiliIter = variabili.iterator();
	   while (variabiliIter.hasNext()) 
      {
         String variabile = variabiliIter.next().toString();
         ComponenteCosto cmp = getComponentiCostoAzienda(componenteId(variabile));
         if(cmp != null)
            compUsedList.add(cmp);
      }
	   return compUsedList;
	}


	private String componenteId(String idVariabile) {
		if (idVariabile.endsWith(VariabiliCosti.LIVELLO)) {
			return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO.length());
		}
		if (idVariabile.endsWith(VariabiliCosti.LIVELLO_INFERIORE)) {
			return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO_INFERIORE.length());
		}
		return idVariabile;
	}

	public List<ConsuntivoCommessa> getConsuntivi() {
		List<ConsuntivoCommessa> ret = new ArrayList<ConsuntivoCommessa>();
		String where = ConsuntivoCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " +
				ConsuntivoCommessaTM.ID_CONSUNTIVO + "=" + getIdConsuntivo() ;
		PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
		try {
			while (cursor.hasNext()) {
				ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)cursor.next();
				ret.add(consuntivo);
			}
		}
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return ret;
	}
	*/   
	
	public boolean valorizzaCostiConsuntivo() {
		PersDatiTecnici psnDatiTecnici = PersDatiTecnici.getCurrentPersDatiTecnici();
		String idRiferimento = psnDatiTecnici.getIdRiferimento();
		String idPrimo = psnDatiTecnici.getIdPrimo();
		String idIndustriale = psnDatiTecnici.getIdIndustriale();
		String idGenerale = psnDatiTecnici.getIdGenerale();
		List consuntiviDetList = getConsuntivoCommessaDet();
	    List compDaValorizz = new ArrayList();
	    List compValorizz = new ArrayList();
		for (int i = 0; i < consuntiviDetList.size(); i++) {
			ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet) consuntiviDetList.get(i);	
			if (dettaglio.getIdComponCosto().equals(idRiferimento))
				setCostoRiferimento(dettaglio.getTotale().getCostoTotale());
			if (dettaglio.getIdComponCosto().equals(idPrimo))
				setCostoPrimo(dettaglio.getTotale().getCostoTotale());
			if (dettaglio.getIdComponCosto().equals(idIndustriale))
				setCostoIndustriale(dettaglio.getTotale().getCostoTotale());
			if (dettaglio.getIdComponCosto().equals(idGenerale))
				setCostoGenerale(dettaglio.getTotale().getCostoTotale());
		}
//		try {
//			setDaCalcoloConsuntivo(true);
//			int rc = save();
//			if (rc < 0)
//				return false;
//			else
//				ConnectionManager.commit();
//		} 
//		catch (Exception e) {
//			e.printStackTrace(Trace.excStream);
//		}
		return true;
	}
	
	/*
	  protected VariablesCollection buildVariablesDetCmm(List consuntivoDetList, boolean atempo, char tipoGruppo, boolean livello) {
	      VariablesCollection variableCollection = new VariablesCollection() {
	         public Object getVariableValue(String name) {
	            Object obj = values.get(name);
	            if (obj != null && ((FunctionVariable)obj).getValue() != null) {
	               return ((FunctionVariable)obj).getValue();
	            }
	            return new BigDecimal("0");
	         }
	      };
	      try {
	         Iterator iter = consuntivoDetList.iterator();
	         FunctionVariable variables;
	         ComponenteCosto compCosto;
	         ConsuntivoCommessaDet consuntivoDet;
	         while (iter.hasNext()) {
	            consuntivoDet = (ConsuntivoCommessaDet)iter.next();
	            compCosto = consuntivoDet.getComponenteCosto();
	            CostiCommessaDetGruppo gruppo = consuntivoDet.getGruppo(tipoGruppo);
	            variables = new FunctionVariable(compCosto.getIdComponenteCosto(), compCosto.getDescrizione().getDescrizioneRidotta(), null, ExpressionTypes.NUMBER);
	            variables.setType(ExpressionTypes.NUMBER);
	            if(livello) {
	               if(atempo)
	                  variables.setValue(gruppo.getTempoLivello());
	               else
	                  variables.setValue(gruppo.getCostoLivello());
	            }
	            else {
	               if(atempo)
	                  variables.setValue(gruppo.getTempoTotale());
	               else
	                  variables.setValue(gruppo.getCostoTotale());
	            }
	            variableCollection.addVariable(variables);
	         }
	      }
	      catch (Exception ex) {
	         ex.printStackTrace(Trace.excStream);
	       }
	       return variableCollection;
	   }
	   */
	/*
	protected VariablesCollection buildVariablesDetCmm(List consuntivoDetList, boolean atempo, char tipoGruppo) {
		VariablesCollection variableCollection = new VariablesCollection() {
			public Object getVariableValue(String name) {
				Object obj = values.get(name);
				if (obj != null && ((FunctionVariable)obj).getValue() != null) {
					return ((FunctionVariable)obj).getValue();
				}
				return new BigDecimal("0");
			}
		};
		try {
			Iterator iter = consuntivoDetList.iterator();
			FunctionVariable variables;
			ComponenteCosto compCosto;
			ConsuntivoCommessaDet consuntivoDet;
			while (iter.hasNext()) {
				consuntivoDet = (ConsuntivoCommessaDet)iter.next();
				compCosto = consuntivoDet.getComponenteCosto();
				CostiCommessaDetGruppo gruppo = consuntivoDet.getGruppo(tipoGruppo);
				variables = new FunctionVariable(compCosto.getIdComponenteCosto(), compCosto.getDescrizione().getDescrizioneRidotta(), null, ExpressionTypes.NUMBER);
				variables.setType(ExpressionTypes.NUMBER);
				//if(getTipoVisualizzazione() == COSTI_DI_LIVELLO) {
				if(isDettagliSottoCommesse() || isDettagliCommessa()) {
					if(atempo)
						variables.setValue(gruppo.getTempoLivello());
					else
						variables.setValue(gruppo.getCostoLivello());
				}
				else {
					if(atempo)
						variables.setValue(gruppo.getTempoTotale());
					else
						variables.setValue(gruppo.getCostoTotale());
				}
				variableCollection.addVariable(variables);
			}
		}
		catch (Exception ex) {
	      ex.printStackTrace(Trace.excStream);
	    }
	    return variableCollection;
	}
	
	protected Formula cloneFormula(Formula formulaDaUtilizzOrig) {
		Formula formulaDaUtilizz = new Formula();
	    try {
	    	formulaDaUtilizz.setEqual(formulaDaUtilizzOrig);
	    }
	    catch (CopyException e) {
	    	e.printStackTrace(Trace.excStream);
	    }
	    return formulaDaUtilizz;
	}	
		
	protected boolean isUtilizzoFormula(Articolo articolo, ConsuntivoCommessaDet dettaglio)	{
		Iterator iter = articolo.getArticoloDatiProduz().getClasseMerclg().getSchemaCosto().getComponenti().iterator();
		while (iter.hasNext()) {
			LinkCompSchema linkCompSchema = (LinkCompSchema)iter.next();
			if (linkCompSchema.getIdComponenteCosto().equals(dettaglio.getIdComponCosto()) && linkCompSchema.isUtilizzoFormula()) {
				return true;
			}
		}
		return false;
	}

	public boolean aggregaCurrentConsuntivoCommessa(Commessa commessa, Commessa commessaAppart) {
		boolean ok = true;
		List<ConsuntivoCommessaDet> consuntivoCommessaDetAppartList = new ArrayList<ConsuntivoCommessaDet>();
		List<ConsuntivoCommessaDet> consuntivoCommessaDetList = getConsuntivoCommessaDetPerCommessa(commessa.getIdCommessa());
		if(commessaAppart != null ) {
			consuntivoCommessaDetAppartList = getConsuntivoCommessaDetPerCommessa(commessaAppart.getIdCommessa());
			aggrega(consuntivoCommessaDetList, consuntivoCommessaDetAppartList);

			
			ok = saveConsuntivoCommessaDettaglioList(consuntivoCommessaDetAppartList);
			if(!ok)
				return false;
		}

		return ok;
	}

	public boolean saveConsuntivoCommessaDettaglioList(List<ConsuntivoCommessaDet> consuntivoCommessaDetAppartList) {
		ConsuntivoCommessaDet consuntivoCmmDet = null;
		for(int i = 0; i < consuntivoCommessaDetAppartList.size(); i++) {
			consuntivoCmmDet = (ConsuntivoCommessaDet)consuntivoCommessaDetAppartList.get(i);
			try {
				int rc = consuntivoCmmDet.save();
				if(rc >=  0)
					ConnectionManager.commit();
				else {
					return false;
				}
			}
			catch(Exception e) {
				e.printStackTrace(Trace.excStream);
			}
		}
		return true;
	}	
	
	public void aggrega(List<ConsuntivoCommessaDet> consuntivoCommessaDetList, List<ConsuntivoCommessaDet> consuntivoCommessaDetAppartList) {
		for(int i = 0; i < consuntivoCommessaDetList.size(); i++) {
			ConsuntivoCommessaDet consuntivoCmmDet = (ConsuntivoCommessaDet)consuntivoCommessaDetList.get(i);
			ConsuntivoCommessaDet consuntivoCommessaDetAppart = getConsuntivoCommessaDetAppartNuovo(consuntivoCmmDet, consuntivoCommessaDetAppartList);
			if(consuntivoCommessaDetAppart == null) {
				consuntivoCommessaDetAppart = copiaConsuntivoCommessaDet(consuntivoCmmDet);
				consuntivoCommessaDetAppartList.add(consuntivoCommessaDetAppart);
			}
			consuntivoCommessaDetAppart.aggrega(consuntivoCmmDet);	
			//valorizzaCostiCommessaDetGruppoTotale(consuntivoCommessaDetAppart);
		}
	}
	  
	public ConsuntivoCommessaDet copiaConsuntivoCommessaDet(ConsuntivoCommessaDet dettaglio) {
		ConsuntivoCommessaDet copia = (ConsuntivoCommessaDet)Factory.createObject(ConsuntivoCommessaDet.class);
		try {
			copia.setEqual(dettaglio);
			copia.azzera();
		}
		catch(CopyException ex) {
			ex.printStackTrace(Trace.excStream);
		}

		return copia;
	}
	
	public List<ConsuntivoCommessaDet> getConsuntivoCommessaDetPerCommessa(String idCommessa){
		List<ConsuntivoCommessaDet> ret = new ArrayList<ConsuntivoCommessaDet>();
		String where = ConsuntivoCommessaDetTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " +
					   ConsuntivoCommessaDetTM.ID_CONSUNTIVO + "=" + getIdConsuntivo() + " AND " + 
					   ConsuntivoCommessaDetTM.R_COMMESSA + "='" + idCommessa + "'";
		PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessaDet.class.getName(), where, "", PersistentObject.NO_LOCK);
		try {
			while (cursor.hasNext()) {
				ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)cursor.next();
				ret.add(dettaglio);				  
			}
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}

		return ret;
	}
	
	public ConsuntivoCommessaDet getConsuntivoCommessaDetAppartNuovo(ConsuntivoCommessaDet consuntivoCmmDet, List<ConsuntivoCommessaDet> consuntivoCommessaDetAppartList) {
		for(Iterator<ConsuntivoCommessaDet> i = consuntivoCommessaDetAppartList.iterator(); i.hasNext();) {
			ConsuntivoCommessaDet consuntivoCmmDetAppart = (ConsuntivoCommessaDet)i.next();
			if(areSimilar(consuntivoCmmDet, consuntivoCmmDetAppart))
				return consuntivoCmmDetAppart;
		}
		return null;
	}
	  
	public boolean areSimilar(ConsuntivoCommessaDet det1, ConsuntivoCommessaDet det2) {
		if(Utils.compare(det1.getIdComponCosto(), det2.getIdComponCosto()) != 0)
			return false;

		return true;
	}

	public boolean prepareConsuntivoDetPerAggregazione() {
		boolean ok = true;
		String where = ConsuntivoCommessaDetTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " +
				ConsuntivoCommessaDetTM.ID_CONSUNTIVO + "=" + getIdConsuntivo() ;
		PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessaDet.class.getName(), where, "", PersistentObject.NO_LOCK);
		try {
			while (cursor.hasNext()) {
				ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)cursor.next();
				dettaglio.getConsolidato().setCostoLivelloInf(ZERO);
				dettaglio.getConsolidato().setTempoLivelloInf(ZERO);
				dettaglio.getEffettuato().setCostoLivelloInf(ZERO);
				dettaglio.getEffettuato().setTempoLivelloInf(ZERO);
				dettaglio.getRichiesto().setCostoLivelloInf(ZERO);
				dettaglio.getRichiesto().setTempoLivelloInf(ZERO);
				dettaglio.getOrdinato().setCostoLivelloInf(ZERO);
				dettaglio.getOrdinato().setTempoLivelloInf(ZERO);
				dettaglio.getTotale().setCostoLivelloInf(ZERO);
				dettaglio.getTotale().setTempoLivelloInf(ZERO);
				int ret = dettaglio.save();
				if(ret < ErrorCodes.NO_ROWS_UPDATED) {
					ok = false;
					break;
				}					
			}
			if(ok)
				ConnectionManager.commit();
			else
				ConnectionManager.rollback();
		}
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return ok;
	}
	  */
	public ConsuntivoCommessa caricaConsuntivoCommessa(Commessa commessa) {
		ConsuntivoCommessa currentConsuntivo = null;
		try {
			String currentConsuntivoKey = KeyHelper.buildObjectKey(new Object[] {getIdAzienda(), getIdConsuntivo(), commessa.getIdCommessa()});
			currentConsuntivo = ConsuntivoCommessa.elementWithKey(currentConsuntivoKey, PersistentObject.NO_LOCK);
			if(currentConsuntivo == null) {
				currentConsuntivo = creaNuovoConsuntivoCommessa(commessa);
			}
			else {
				//currentConsuntivo.setDeepRetrieveEnabled(true);
				//currentConsuntivo.retrieve();
				currentConsuntivo.setDescrizione(getDescrizione());
				currentConsuntivo.setDataRiferimento(getDataRiferimento());
				currentConsuntivo.setUsaDataEstrazioneStorici(isUsaDataEstrazioneStorici());
				currentConsuntivo.setConsolidato(isConsolidato());
				currentConsuntivo.setEstrazioneOrdini(isEstrazioneOrdini());
				currentConsuntivo.setEstrazioneRichieste(isEstrazioneRichieste());
				//currentConsuntivo.setTipoVisualizzazione(getTipoVisualizzazione());
				currentConsuntivo.setCostoGenerale(ZERO);
				currentConsuntivo.setCostoIndustriale(ZERO);
				currentConsuntivo.setCostoPrimo(ZERO);
				currentConsuntivo.setCostoRiferimento(ZERO);
			}
			
		     currentConsuntivo.setTotali(isTotali());
		     currentConsuntivo.setComponentiPropri(isComponentiPropri());
		     currentConsuntivo.setDettagliCommessa(isDettagliCommessa());
		     currentConsuntivo.setDettagliSottoCommesse(isDettagliSottoCommesse());
		     currentConsuntivo.setSoloComponentiValorizzate(isSoloComponentiValorizzate());
           currentConsuntivo.setCalcolatoreDettagli(getCalcolatoreDettagli());

		        
		     
		} 
		catch (SQLException e1) {
			e1.printStackTrace(Trace.excStream);
		}
		return currentConsuntivo;
	}
		
	public List<StoricoCommessaDet> cercaDettagliConsolidati(List<StoricoCommessaDet> dettagli) {
		List<StoricoCommessaDet> dettagliConsolidati = new ArrayList<StoricoCommessaDet>();
		for(int idx = 0; idx < dettagli.size(); idx++) {
			StoricoCommessaDet storicoDettaglio = dettagli.get(idx);
			StoricoCommessa storico = storicoDettaglio.getStoricoCommessa();
			Date dataUltConsDef = (storico != null && storico.getCommessa() != null && 
								   storico.getCommessa().getUltimaConsuntivoCommessaDefinitivo() != null)?storico.getCommessa().getUltimaConsuntivoCommessaDefinitivo().getDataRiferimento():null;
			if(storico != null && storico.getDataOrigine() != null && dataUltConsDef != null &&
			   storico.getDataOrigine().compareTo(dataUltConsDef) <= 0) {		
				dettagliConsolidati.add(storicoDettaglio);
			}
		}
		return dettagliConsolidati;
	}
	
	/*
	public boolean salvaConsuntivoCommessaDet(HashMap consuntivoCommessaDetTmpMap) {
		for(Iterator iterator = consuntivoCommessaDetTmpMap.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry dettaglioMap = (Map.Entry)iterator.next();						
			ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)dettaglioMap.getValue();
			try {
				int rc = dettaglio.save();
				if(rc < 0)
					return false;
			} 
			catch (SQLException e) {
				e.printStackTrace(Trace.excStream);
			}
		}
		return true;
	}
	*/
	
	public List<StoricoCommessaDet> pulireListaEffettuate(List<StoricoCommessaDet> dettagliEff, List<StoricoCommessaDet> dettagliConsolidati) {
		List<StoricoCommessaDet> dettagliEffettuate = new ArrayList<StoricoCommessaDet>();
		for(int idx = 0; idx < dettagliEff.size(); idx++) {
			StoricoCommessaDet dettaglio = dettagliEff.get(idx);
			if(!dettagliConsolidati.contains(dettaglio)) {
				dettagliEffettuate.add(dettaglio);
			}
		}
		return dettagliEffettuate;
	}
	
	public StoricoCommessaDet cercaStoricoCommessaDet(List<StoricoCommessaDet> dettagli, String idComponCosto) {
		for(int idx = 0; idx < dettagli.size(); idx++) {
			StoricoCommessaDet dettaglio = dettagli.get(idx);
			if(dettaglio.getIdComponenteCosto().equals(idComponCosto)) {
				return dettaglio;
			}            
		}		
		return null;
	}
	
	public ConsuntivoCommessaDet creaConsuntivoCommessaDettaglio(ConsuntivoCommessa consuntivo, Commessa commessa, String idComponCosto) {
		ConsuntivoCommessaDet consuntivoCommessaDet = (ConsuntivoCommessaDet) Factory.createObject(ConsuntivoCommessaDet.class);
		consuntivoCommessaDet.setIdAzienda(consuntivo.getIdAzienda());
		consuntivoCommessaDet.setIdConsuntivo(consuntivo.getIdConsuntivo());
		consuntivoCommessaDet.setIdCommessa(commessa.getIdCommessa());
		consuntivoCommessaDet.setIdComponCosto(idComponCosto);
		return consuntivoCommessaDet;
	}
	
	/*
	public boolean calcoloConsuntivoStorici(List<Commessa> commesse) {
		boolean ok = true;
		for(int i = 0; i < commesse.size(); i++) {
			Commessa commessa = commesse.get(i);			
			if(ok){
				ConsuntivoCommessa currentConsuntivo = caricaConsuntivoCommessa(commessa);
				List<StoricoCommessaDet> strCommessaDettaglioList = new ArrayList<StoricoCommessaDet>();				
				List<Integer> idProgrStorici = cercaIdProgrStorici(commessa.getIdCommessa(), getDataRiferimento());
				if(idProgrStorici !=  null && !idProgrStorici.isEmpty()) {
					for(int idxProgStor = 0; idxProgStor < idProgrStorici.size(); idxProgStor++) {
						Integer idProgrStorico = (Integer)idProgrStorici.get(idxProgStor);
						strCommessaDettaglioList = recuperaStoricoCommessaDet(commessa.getIdAzienda(), idProgrStorico, commessa.getIdCommessa(), isEstrazioneOrdini(), isEstrazioneRichieste());
						gestioneConsuntivoCommessaDet(currentConsuntivo, strCommessaDettaglioList);
					}
//					if(!consuntivoCommessaDetTmpMap.isEmpty())
//						ok = salvaConsuntivoCommessaDet(consuntivoCommessaDetTmpMap);
//					consuntivoCommessaDetTmpMap.clear();
				}
				else {
//					try {
						Iterator iter1 = currentConsuntivo.getConsuntivoCommessaDet().iterator();
						while(iter1.hasNext()) {
							ConsuntivoCommessaDet det = (ConsuntivoCommessaDet)iter1.next();
							det.azzera();
//							det.save();
						}
//					}
//					catch(SQLException e) {
//						e.printStackTrace(Trace.excStream);
//					}
				}
			}
		}
		return ok;
	}
	*/
	
	protected static String ID_PROG_STORICO = "SELECT " + StoricoCommessaTM.ID_PROGRESSIVO + 
				" FROM " + StoricoCommessaTM.TABLE_NAME +
				" WHERE " + 
				StoricoCommessaTM.ID_AZIENDA + " = ? AND " +  
				StoricoCommessaTM.R_COMMESSA + " = ? AND " + 
				StoricoCommessaTM.DATA_ORG + "<= ?";

	protected static CachedStatement IdProgrStoricoStmt = new CachedStatement(ID_PROG_STORICO);	
	
	public synchronized List<Integer> cercaIdProgrStorici(String idCommessa, Date dataRiferimento) {
		List<Integer> idProgrStorici = new ArrayList<Integer>();
		try {
			ResultSet rs = null;
			PreparedStatement ps = IdProgrStoricoStmt.getStatement();
			Database db = ConnectionManager.getCurrentDatabase();
			db.setString(ps, 1, Azienda.getAziendaCorrente());
			db.setString(ps, 2, idCommessa);
			ps.setDate(3, dataRiferimento);

			rs = ps.executeQuery();
			while(rs.next()) {
				idProgrStorici.add(new Integer(rs.getInt(1))); 
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
			
		return idProgrStorici;
	}
	
	
	/*
	public boolean gestioneConsuntivoCommessaDet(ConsuntivoCommessa currentConsuntivo, List<StoricoCommessaDet> strCmmessaDettaglioList) {
		List<StoricoCommessaDet> dettagliEffettuati = getStoriciCommessaDetPerTipoDettaglio(strCmmessaDettaglioList, StoricoCommessaDet.EFFETTUATO);												
		List<StoricoCommessaDet> dettagliRichiesto = isEstrazioneRichieste() ? getStoriciCommessaDetPerTipoDettaglio(strCmmessaDettaglioList, StoricoCommessaDet.RICHIESTO) : new ArrayList<StoricoCommessaDet>();
		List<StoricoCommessaDet> dettagliOrdinato = isEstrazioneOrdini() ? getStoriciCommessaDetPerTipoDettaglio(strCmmessaDettaglioList, StoricoCommessaDet.ORDINATO) : new ArrayList<StoricoCommessaDet>();
		List<StoricoCommessaDet> dettagliConsolidati = isConsolidato() ? cercaDettagliConsolidati(dettagliEffettuati) : new ArrayList<StoricoCommessaDet>();

		if(dettagliConsolidati != null && !dettagliConsolidati.isEmpty()) {
			dettagliEffettuati = pulireListaEffettuate(dettagliEffettuati, dettagliConsolidati);
		}
		Commessa commessa = currentConsuntivo.getCommessa();
		Articolo articolo = currentConsuntivo.getCommessaPrm().getArticolo();
		List<LinkCompSchema> listComponenti = new ArrayList<LinkCompSchema>();
		if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getSchemaCosto() != null)
			listComponenti.addAll(articolo.getClasseMerclg().getSchemaCosto().getComponenti());
		for(int idx = 0; idx < listComponenti.size(); idx++) {
			LinkCompSchema linkCompSchema = listComponenti.get(idx);
			ConsuntivoCommessaDet consuntivoCommessaDet = null;
//			try {
	
			   consuntivoCommessaDet = getDettaglio(currentConsuntivo, linkCompSchema.getIdComponenteCosto());
				consuntivoCommessaDet.azzera();			

				if(dettagliEffettuati != null && !dettagliEffettuati.isEmpty()) {
					StoricoCommessaDet detEffetuati = cercaStoricoCommessaDet(dettagliEffettuati, linkCompSchema.getIdComponenteCosto());
					if(detEffetuati != null) {
						valorizzaCostiCommessaDetGruppo(consuntivoCommessaDet.getEffettuato(), detEffetuati);
					}
				}
				if(dettagliRichiesto != null && !dettagliRichiesto.isEmpty()) {
					StoricoCommessaDet detRichiesto = cercaStoricoCommessaDet(dettagliRichiesto, linkCompSchema.getIdComponenteCosto());
					if(detRichiesto != null) {
						valorizzaCostiCommessaDetGruppo(consuntivoCommessaDet.getRichiesto(), detRichiesto);
					}
				}
				if(dettagliOrdinato != null && !dettagliOrdinato.isEmpty()) {
					StoricoCommessaDet detOrdinato = cercaStoricoCommessaDet(dettagliOrdinato, linkCompSchema.getIdComponenteCosto());
					if(detOrdinato != null) {
						valorizzaCostiCommessaDetGruppo(consuntivoCommessaDet.getOrdinato(), detOrdinato);
					}
				}						
				if(dettagliConsolidati != null && !dettagliConsolidati.isEmpty()) {
					StoricoCommessaDet detConsolidato = cercaStoricoCommessaDet(dettagliConsolidati, linkCompSchema.getIdComponenteCosto());
					if(detConsolidato != null) {
						valorizzaCostiCommessaDetGruppo(consuntivoCommessaDet.getConsolidato(), detConsolidato);
					}
				}
				valorizzaCostiCommessaDetGruppoTotale(consuntivoCommessaDet);
				//consuntivoCommessaDetTmpMap.put(consuntivoCommessaDet.getKey(), consuntivoCommessaDet);
//			} 
//			catch (SQLException e1) {
//				e1.printStackTrace(Trace.excStream);
//			}
		}
		return true;
	}
	*/
	
	protected ConsuntivoCommessaDet getDettaglio(ConsuntivoCommessa consuntivo, String idCmpCosto)
	{
	   return getDettaglio(consuntivo, idCmpCosto, true);
	}

	protected ConsuntivoCommessaDet getDettaglio(ConsuntivoCommessa consuntivo, String idCmpCosto, boolean creaDettaglio)
	{
	   ConsuntivoCommessaDet dettaglio = null;
	   Iterator iter = consuntivo.getConsuntivoCommessaDet().iterator();
	   while(iter.hasNext() && dettaglio == null)
	   {
	      ConsuntivoCommessaDet curDet = (ConsuntivoCommessaDet)iter.next();
	      if(curDet.getIdComponCosto().equals(idCmpCosto))
	         dettaglio = curDet;
	   }
	   if(dettaglio == null && creaDettaglio)
	   {
	      dettaglio = creaConsuntivoCommessaDettaglio(consuntivo, consuntivo.getCommessa(), idCmpCosto);
	      consuntivo.getConsuntivoCommessaDet().add(dettaglio);
	   }
	   return dettaglio;
	}
	
	public List<StoricoCommessaDet> getStoriciCommessaDetPerTipoDettaglio(List<StoricoCommessaDet> strCmmessaDettaglioList, char tipoDettaglio){
		List<StoricoCommessaDet> ret = new ArrayList<StoricoCommessaDet>();
		Iterator<StoricoCommessaDet> dettagli = strCmmessaDettaglioList.iterator();
		while(dettagli.hasNext()) {
			StoricoCommessaDet dettaglio = (StoricoCommessaDet)dettagli.next();
			if(dettaglio.getTipoDettaglio() == tipoDettaglio)
				ret.add(dettaglio);
		}		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public List<StoricoCommessaDet> recuperaStoricoCommessaDet(String idAzienda, Integer idProgrStorico, String idCommessa, boolean estrazioneOrdini, boolean estrazioneRichieste) {
		List<StoricoCommessaDet> ret = new ArrayList<StoricoCommessaDet>();
		try {		  
			String where = StoricoCommessaDetTM.ID_AZIENDA + "='" + idAzienda + "' AND " +
						   StoricoCommessaDetTM.ID_PROGR_STORIC + "=" + idProgrStorico + " AND " +
						   StoricoCommessaDetTM.ID_COMMESSA + "='" + idCommessa + "'" ;
			if(!estrazioneOrdini)
				where += " AND " + StoricoCommessaDetTM.TIPO_DETTAGLIO + "<>'" + StoricoCommessaDet.ORDINATO + "'" ;
			
			if(!estrazioneRichieste)
				where += " AND " + StoricoCommessaDetTM.TIPO_DETTAGLIO + "<>'" + StoricoCommessaDet.RICHIESTO + "'" ;
			  
			ret = StoricoCommessaDet.retrieveList(where, "", false);	  
		}
		catch(Exception ex) {
			ex.printStackTrace(Trace.excStream);
		}
		return ret;
	}

	/*
	public void aggregaCostiNuovo(List<StoricoCommessaDet> storicoCommessaDet, List<StoricoCommessaDet> storicoCmmDettaglioAppartList) {
		for(int i = 0; i < storicoCommessaDet.size(); i++) {
			StoricoCommessaDet strCmmDet = (StoricoCommessaDet)storicoCommessaDet.get(i);
			StoricoCommessaDet strCmmDetAppart = getStoricoCommessaDetAppartNuovo(strCmmDet, storicoCmmDettaglioAppartList);
			if(strCmmDetAppart == null) {
				strCmmDetAppart = copiaStrCommessaDet(strCmmDet);
				storicoCmmDettaglioAppartList.add(strCmmDetAppart);
			}
			azzeraStoricoCommessaDet(strCmmDetAppart);
			strCmmDetAppart.setCostoLivelloInf(sum(strCmmDetAppart.getCostoLivelloInf(), strCmmDet.getCostoTotale()));
			strCmmDetAppart.setCostoTotale(sum(strCmmDetAppart.getCostoLivello(), strCmmDetAppart.getCostoLivelloInf()));
			strCmmDetAppart.setTempoLivelloInf(sum(strCmmDetAppart.getTempoLivelloInf(), strCmmDet.getTempoTotale()));
			strCmmDetAppart.setTempoTotale(sum(strCmmDetAppart.getTempoLivello(), strCmmDetAppart.getTempoLivelloInf()));
		}
	}

	public void azzeraStoricoCommessaDet(StoricoCommessaDet strCmmDetAppart) {
		strCmmDetAppart.setCostoLivelloInf(new BigDecimal(0));
		strCmmDetAppart.setCostoTotale(new BigDecimal(0));
		strCmmDetAppart.setTempoLivelloInf(new BigDecimal(0));
		strCmmDetAppart.setTempoTotale(new BigDecimal(0));
	}
	  
	public StoricoCommessaDet copiaStrCommessaDet(StoricoCommessaDet dettaglio) {
		StoricoCommessaDet copia = (StoricoCommessaDet)Factory.createObject(StoricoCommessaDet.class);
		try {
			copia.setEqual(dettaglio);
		}
		catch(CopyException ex) {
			ex.printStackTrace(Trace.excStream);
		}
		copia.setCostoLivelloInf(CostiCommessaDet.zero);
		copia.setCostoLivello(CostiCommessaDet.zero);
		copia.setCostoTotale(CostiCommessaDet.zero);
		copia.setTempoLivelloInf(CostiCommessaDet.zero);
		copia.setTempoLivello(CostiCommessaDet.zero);
		copia.setTempoTotale(CostiCommessaDet.zero);
		return copia;
	}
	  
	public StoricoCommessaDet getStoricoCommessaDetAppartNuovo(StoricoCommessaDet strCmmDet, List<StoricoCommessaDet> strCmmDettaglioAppartList) {
		for(Iterator<StoricoCommessaDet> i = strCmmDettaglioAppartList.iterator(); i.hasNext();) {
			StoricoCommessaDet strCmmDetAppart = (StoricoCommessaDet)i.next();
			if(areSimilar(strCmmDet, strCmmDetAppart))
				return strCmmDetAppart;
		}
		return null;
	}
	  
	public boolean areSimilar(StoricoCommessaDet det1, StoricoCommessaDet det2) {
		if(det1.getTipoDettaglio() != det2.getTipoDettaglio())
			return false;

		if(Utils.compare(det1.getIdComponenteCosto(), det2.getIdComponenteCosto()) != 0)
			return false;

		return true;
	}
	public BigDecimal sum(BigDecimal val1, BigDecimal val2) {
		if((val1 == null) && (val2 == null))
			return null;
		if(val1 == null)
			return val2;
		if(val2 == null)
			return val1;
		return val1.add(val2);
	}
   */   
	  
	public ConsuntivoCommessa creaNuovoConsuntivoCommessa(Commessa commessa) {
		ConsuntivoCommessa nuovoConsuntivo = (ConsuntivoCommessa)Factory.createObject(ConsuntivoCommessa.class);		
		try {
			nuovoConsuntivo.setEqual(this);
			nuovoConsuntivo.setIdAzienda(getIdAzienda());
			nuovoConsuntivo.setIdConsuntivo(getIdConsuntivo());			
			nuovoConsuntivo.setIdCommessa(commessa.getIdCommessa());
			nuovoConsuntivo.setIdCommessaApp(commessa.getIdCommessaAppartenenza());
			nuovoConsuntivo.setIdCommessaPrm(commessa.getIdCommessaPrincipale());
			nuovoConsuntivo.setLivelloCommessa(commessa.getLivelloCommessa());
			nuovoConsuntivo.setIdStabilimento(commessa.getIdStabilimento());
			nuovoConsuntivo.setIdArticolo(commessa.getIdArticolo());			
			nuovoConsuntivo.setIdVersione(commessa.getIdVersione());
			nuovoConsuntivo.setIdConfigurazione(commessa.getIdConfigurazione());
			nuovoConsuntivo.setIdUMPrmMag(commessa.getIdUmPrmMag());
			nuovoConsuntivo.setQuantitaPrm(commessa.getQtaUmPrm());
			nuovoConsuntivo.setDescrizione(getDescrizione());
			nuovoConsuntivo.setDataRiferimento(getDataRiferimento());
			nuovoConsuntivo.setUsaDataEstrazioneStorici(isUsaDataEstrazioneStorici());
			nuovoConsuntivo.setConsolidato(isConsolidato());
			nuovoConsuntivo.setEstrazioneOrdini(isEstrazioneOrdini());
			nuovoConsuntivo.setEstrazioneRichieste(isEstrazioneRichieste());
			//nuovoConsuntivo.setTipoVisualizzazione(getTipoVisualizzazione());
//			int rc = nuovoConsuntivo.save();
//			if (rc < 0)
//				return null;
		} 
		catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		} 		
		
		return nuovoConsuntivo;
	}
	
	/*
	public void valorizzaCostiCommessaDetGruppo(CostiCommessaDetGruppo gruppo, StoricoCommessaDet det) {
		gruppo.setCostoLivello(gruppo.getCostoLivello().add(det.getCostoLivello()));
		gruppo.setCostoLivelloInf(gruppo.getCostoLivelloInf().add(det.getCostoLivelloInf()));
		gruppo.setCostoTotale(gruppo.getCostoTotale().add(det.getCostoTotale()));

		gruppo.setTempoLivello(gruppo.getTempoLivello().add(det.getTempoLivello()));
		gruppo.setTempoLivelloInf(gruppo.getTempoLivelloInf().add(det.getTempoLivelloInf()));
		gruppo.setTempoTotale(gruppo.getTempoTotale().add(det.getTempoTotale()));
	}
	
	public void valorizzaCostiCommessaDetGruppoTotale(ConsuntivoCommessaDet consuntivoCommessaDet) {		
		BigDecimal totaleCostoLivello = getTotale(consuntivoCommessaDet.getConsolidato().getCostoLivello(), 
												  consuntivoCommessaDet.getRichiesto().getCostoLivello(),
												  consuntivoCommessaDet.getOrdinato().getCostoLivello(),
												  consuntivoCommessaDet.getEffettuato().getCostoLivello());
		consuntivoCommessaDet.getTotale().setCostoLivello(totaleCostoLivello);

		BigDecimal totaleCostoLivelloInf = getTotale(consuntivoCommessaDet.getConsolidato().getCostoLivelloInf(), 
												  consuntivoCommessaDet.getRichiesto().getCostoLivelloInf(),
												  consuntivoCommessaDet.getOrdinato().getCostoLivelloInf(),
												  consuntivoCommessaDet.getEffettuato().getCostoLivelloInf());
		consuntivoCommessaDet.getTotale().setCostoLivelloInf(totaleCostoLivelloInf);

		BigDecimal totaleCostoTotale = getTotale(consuntivoCommessaDet.getConsolidato().getCostoTotale(), 
												  consuntivoCommessaDet.getRichiesto().getCostoTotale(),
												  consuntivoCommessaDet.getOrdinato().getCostoTotale(),
												  consuntivoCommessaDet.getEffettuato().getCostoTotale());		
		consuntivoCommessaDet.getTotale().setCostoTotale(totaleCostoTotale);
		
		BigDecimal totaleTempoLivello = getTotale(consuntivoCommessaDet.getConsolidato().getTempoLivello(), 
												  consuntivoCommessaDet.getRichiesto().getTempoLivello(),
												  consuntivoCommessaDet.getOrdinato().getTempoLivello(),
												  consuntivoCommessaDet.getEffettuato().getTempoLivello());
		consuntivoCommessaDet.getTotale().setTempoLivello(totaleTempoLivello);

		BigDecimal totaleTempoLivelloInf = getTotale(consuntivoCommessaDet.getConsolidato().getTempoLivelloInf(), 
												  consuntivoCommessaDet.getRichiesto().getTempoLivelloInf(),
												  consuntivoCommessaDet.getOrdinato().getTempoLivelloInf(),
												  consuntivoCommessaDet.getEffettuato().getTempoLivelloInf());
		consuntivoCommessaDet.getTotale().setTempoLivelloInf(totaleTempoLivelloInf);

		BigDecimal totaleTempoTotale = getTotale(consuntivoCommessaDet.getConsolidato().getTempoTotale(), 
												  consuntivoCommessaDet.getRichiesto().getTempoTotale(),
												  consuntivoCommessaDet.getOrdinato().getTempoTotale(),
												  consuntivoCommessaDet.getEffettuato().getTempoTotale());
		consuntivoCommessaDet.getTotale().setTempoTotale(totaleTempoTotale);
	}
	*/
	public BigDecimal getTotale(BigDecimal costoCons, BigDecimal costoRic, BigDecimal costoOrd, BigDecimal costoEff) {
		BigDecimal costoTot = new BigDecimal(0);
		if(costoCons != null) {
			costoTot = costoTot.add(costoCons);
		}
		
		if(costoRic != null) {
			costoTot = costoTot.add(costoRic);
		}
		
		if(costoOrd != null) {
			costoTot = costoTot.add(costoOrd);
		}
		
		if(costoEff != null) {
			costoTot = costoTot.add(costoEff);
		}
		return costoTot;
	}
	
	/*
	@SuppressWarnings("unchecked")
	public List<Commessa> getCommesse()  {
		List<Commessa> commesse =  new ArrayList<Commessa>();
		Commessa commessa = getCommessa();
		if(commessa != null) {
			commesse.addAll(commessa.getRelatedCommesse());
		}
		return commesse;
	}
	*/
	
	public JSONArray getColumnsDescriptors() throws JSONException {
		JSONArray columns = new JSONArray();
		columns.put(getColumnDescriptors(""));
		columns.put(getColumnDescriptors("Storici"));
		if(isConsolidato())
			columns.put(getColumnDescriptors("Consolidato"));
		if(isEstrazioneRichieste())
			columns.put(getColumnDescriptors("Richiesto"));
		if(isEstrazioneOrdini())
			columns.put(getColumnDescriptors("Ordinato"));
		columns.put(getColumnDescriptors("Sostenuto"));
		columns.put(getColumnDescriptors("Totale"));
		return columns;
	}
	
	public JSONObject getColumnDescriptors(String title) throws JSONException {
		JSONObject column = new JSONObject();
		if(title == null || title.equals("")) {
			column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", "CompCosto").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold"));
		}
		else if(title.equals("Storici")) {
			column.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "100").put("halign", "center").put("align", "center").put("dataIndx", "Storici").put("collapsible", new JSONObject().put("last", true).put("on", true));
		}
		else {
			if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
				column.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", true));
				column.put("colModel", getColumnModelATempo(title));				
								
			}
			else {
				String dataIndx = title.substring(0, 3);
				column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", FORMATO_VALORI);
				if(title.equals("Totale"))
					column.put("style", new JSONObject().put("font-weight", "bold"));
			}			
		}
		return column;		
	}
	
	public JSONArray getColumnModelATempo(String title) throws JSONException {
		JSONArray columns = new JSONArray();
		String dataIndx = title.substring(0, 3);
		JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"HH").put("dataType", "float").put("format", FORMATO_ORE);
		if(title.equals("Totale"))
			columnOre.put("style", new JSONObject().put("font-weight", "bold"));
		columns.put(columnOre);
		JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", FORMATO_VALORI);
		if(title.equals("Totale"))
			columnValore.put("style", new JSONObject().put("font-weight", "bold"));
		columns.put(columnValore);
		return columns;
	}

	/*
	public JSONArray getDataModel() {
		JSONArray data = new JSONArray();
		Commessa commessaPrm = getCommessa();
		if(commessaPrm != null) {
			try {
				writeJSONDati(data, "1", null, this, commessaPrm, null);
			} catch (JSONException e) {
				e.printStackTrace(Trace.excStream);
			}
		}
		
		return data;
	}
	*/
	
	  public JSONArray getDataModelInMemoria() {
	      JSONArray data = new JSONArray();
	      Commessa commessaPrm = getCommessa();
	      if(commessaPrm != null) {
	         try {
	            writeJSONDatiInMemoria(data, "1", null, this, commessaPrm, null);
	         } catch (JSONException e) {
	            e.printStackTrace(Trace.excStream);
	         }
	      }
	      
	      return data;
	   }
	
	  @SuppressWarnings("unchecked")
	   protected void writeJSONDatiInMemoria(JSONArray data, String id, String idParent, ConsuntivoCommessa consuntivo, Commessa commessaPrm, Commessa commessaApp) throws JSONException {
	      String parent = "";
	      Commessa commessa = commessaPrm;
	      if(commessaApp != null) {
	         commessa = commessaApp;
	      }

	      if(idParent != null) {        
	         parent = idParent;
	      }
	      
	      ConsuntivoCommessaDet dettaglioTotali = cercaConsuntivoCommessaDetTotali(consuntivo, commessa.getIdCommessa(), getIdComponenteTotali());
	      if(dettaglioTotali == null) {
	         dettaglioTotali = (ConsuntivoCommessaDet)Factory.createObject(ConsuntivoCommessaDet.class);
	         dettaglioTotali.setIdAzienda(consuntivo.getIdAzienda());
	         dettaglioTotali.setIdCommessa(commessa.getIdCommessa());
	         dettaglioTotali.setIdComponCosto(getIdComponenteTotali());
	      }
	      int idxGrup = 1;
	      writeCommessaGruppo(data, id, parent, commessa.getDescrizione().getDescrizione(), commessa.getIdCommessa() + " - " + commessa.getDescrizione().getDescrizione() , isConsolidato(), commessaPrm.hasCompenenteATempo(), dettaglioTotali);
	      
	      if(isTotali()) {
	         String idGruppTot = id + idxGrup;
	         writeTotale(data, idGruppTot, id, commessaPrm.hasCompenenteATempo(), isConsolidato(), false, dettaglioTotali);
	         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
	            writeTotaleDettagli(data, id, idGruppTot, consuntivo.getConsuntivoCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate(), isConsolidato());
	      }
	            
	      if(isComponentiPropri()) {
	         idxGrup++;
	         String idGruppCmp = id + idxGrup;
	         writeComponentePropri(data, idGruppCmp, id, commessaPrm.hasCompenenteATempo(), isConsolidato(), false, dettaglioTotali);
	         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
	            writeComponentePropriDettagli(data, id, idGruppCmp, consuntivo.getConsuntivoCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate(), isConsolidato());
	      }
	      
	      if(consuntiviLivelliInferiori != null)
	      {
	      Iterator consIter = consuntiviLivelliInferiori.iterator();
	      while(consIter.hasNext())
	      {
	         ConsuntivoCommessa currentConsuntivo = (ConsuntivoCommessa)consIter.next();
	         idxGrup++;
            String idSottoCmm = id + idxGrup;
            currentConsuntivo.writeJSONDatiInMemoria(data, idSottoCmm, id, currentConsuntivo, commessaPrm, currentConsuntivo.getCommessa());
	      }
	      }
	      

	   }
	
	public void writeCommessaGruppo(JSONArray data, String id, String parentId, String descCommessa,
			String compCosto, boolean consolidato, boolean isATempo, ConsuntivoCommessaDet dettaglio) throws JSONException {

		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("parentId", parentId);
		json.put("pq_rowstyle", "background-color: #E5F4FF;font-weight: bold;"); //D8d8d8
		json.put("Commessa", descCommessa);
		json.put("CompCosto", compCosto);
		if (getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI) {
			json.put("CmpElem", "Y");
			json.put("IdCommessa", getIdCommessa());
			json.put("IdCmpCosto", dettaglio.getIdComponCosto());
			json.put("ClassName", "StoricoCommessa");
		}

		if (consolidato) {
			json.put("ConVal", "" + dettaglio.getConsolidato().getCostoTotale());
		}
		json.put("RicVal", "" + dettaglio.getRichiesto().getCostoTotale());
		json.put("OrdVal", "" + dettaglio.getOrdinato().getCostoTotale());
		json.put("SosVal", "" + dettaglio.getEffettuato().getCostoTotale());
		json.put("TotVal", "" + dettaglio.getTotale().getCostoTotale());
		if (isATempo) {
			if (getComponenteCosto().isGestioneATempo()) {
				if (consolidato) {
					json.put("ConHH", "" + dettaglio.getConsolidato().getTempoTotale());
				}
				json.put("RicHH", "" + dettaglio.getRichiesto().getTempoTotale());
				json.put("OrdHH", "" + dettaglio.getOrdinato().getTempoTotale());
				json.put("SosHH", "" + dettaglio.getEffettuato().getTempoTotale());
				json.put("TotHH", "" + dettaglio.getTotale().getTempoTotale());
			} else {
				if (consolidato) {
					json.put("ConHH", "");
				}
				json.put("RicHH", "");
				json.put("OrdHH", "");
				json.put("SosHH", "");
				json.put("TotHH", "");
			}
		}
		data.put(json);
	}
	
	public void writeComponentePropri(JSONArray data, String id, String idParent, boolean isATempo, boolean consolidato, boolean isDettaglio, ConsuntivoCommessaDet dettaglio) {
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("parentId", idParent);
			if(isDettaglio) {
				//json.put("pq_rowstyle", "background-color: #FFFFFF;");
				json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
				if (dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI
						&& dettaglio.getConsuntivoCommessa().getStatoAvanzamento() == BudgetCommessa.PROVVISORIO) {
					json.put("CmpElem", "Y");
					json.put("IdCommessa", dettaglio.getIdCommessa());
					json.put("IdCmpCosto", dettaglio.getIdComponCosto());
					json.put("ClassName", "StoricoCommessa");
					if (dettaglio.getComponenteCosto().isGestioneATempo()) 
						json.put("CmpATempo", "Y");
					else 
						json.put("CmpATempo", "N");					
				}
			}
			else {
				json.put("CompCosto", ResourceLoader.getString(RES_FILE, "ComponentiProprie"));
				json.put("pq_rowstyle", "background-color: #F0F0F0;font-weight: bold;");
			}
			if (consolidato) {
				json.put("ConVal", "" + dettaglio.getConsolidato().getCostoLivello());
			}
			json.put("RicVal", "" + dettaglio.getRichiesto().getCostoLivello());
			json.put("OrdVal", "" + dettaglio.getOrdinato().getCostoLivello());
			json.put("SosVal", "" + dettaglio.getEffettuato().getCostoLivello());
			json.put("TotVal", "" + dettaglio.getTotale().getCostoLivello());
			if (isATempo) {
				if(dettaglio.getComponenteCosto().isGestioneATempo()) {
					if (consolidato) {
						json.put("ConHH", "" + dettaglio.getConsolidato().getTempoLivello());
					}
					json.put("RicHH", "" + dettaglio.getRichiesto().getTempoLivello());
					json.put("OrdHH", "" + dettaglio.getOrdinato().getTempoLivello());
					json.put("SosHH", "" + dettaglio.getEffettuato().getTempoLivello());
					json.put("TotHH", "" + dettaglio.getTotale().getTempoLivello());
				}
				else {
					if (consolidato) {
						json.put("ConHH", "");
					}
					json.put("RicHH", "");
					json.put("OrdHH", "");
					json.put("SosHH", "");
					json.put("TotHH", "");
				}
			}
			data.put(json);
		}
		catch (JSONException e) {
			e.printStackTrace(Trace.excStream);
		}
	}
	
	public void writeTotale(JSONArray data, String id, String idParent, boolean isATempo, boolean consolidato, boolean isDettaglio, ConsuntivoCommessaDet dettaglio) {
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("parentId", idParent);
			if(isDettaglio) {
				json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
				//json.put("pq_rowstyle", "background-color: #FFFFFF;");
			}
			else {
				json.put("CompCosto", ResourceLoader.getString(RES_FILE, "Totali"));
				json.put("pq_rowstyle", "background-color: #F0F0F0;font-weight: bold;");
			}
			
			if (consolidato) {
				json.put("ConVal", "" + dettaglio.getConsolidato().getCostoTotale());
			}
			json.put("RicVal", "" + dettaglio.getRichiesto().getCostoTotale());
			json.put("OrdVal", "" + dettaglio.getOrdinato().getCostoTotale());
			json.put("SosVal", "" + dettaglio.getEffettuato().getCostoTotale());
			json.put("TotVal", "" + dettaglio.getTotale().getCostoTotale());
			if (isATempo) {
				if(dettaglio.getComponenteCosto().isGestioneATempo()) {
					if (consolidato) {
						json.put("ConHH", "" + dettaglio.getConsolidato().getTempoTotale());
					}
					json.put("RicHH", "" + dettaglio.getRichiesto().getTempoTotale());
					json.put("OrdHH", "" + dettaglio.getOrdinato().getTempoTotale());
					json.put("SosHH", "" + dettaglio.getEffettuato().getTempoTotale());
					json.put("TotHH", "" + dettaglio.getTotale().getTempoTotale());
				}
				else {
					if (consolidato) {
						json.put("ConHH", "");
					}
					json.put("RicHH", "");
					json.put("OrdHH", "");
					json.put("SosHH", "");
					json.put("TotHH", "");
				}
			}
			
			data.put(json);
		}
		catch (JSONException e) {
			e.printStackTrace(Trace.excStream);
		}
	}
	
	public void writeTotaleDettagli(JSONArray data, String id, String idParent, List<ConsuntivoCommessaDet> consuntiviCommessaDet, Commessa commessa, boolean isATempo, boolean  soloComponentiValorizzate, boolean consolidato) {
		int idxDet = 1;
		for(int i = 0; i < consuntiviCommessaDet.size(); i++) {
			ConsuntivoCommessaDet dettaglio = consuntiviCommessaDet.get(i);
			if((!soloComponentiValorizzate) || (soloComponentiValorizzate && dettaglio.getTotale().getCostoTotale() != null && dettaglio.getTotale().getCostoTotale().compareTo(new BigDecimal(0)) > 0)) {
				String idDet = idParent + idxDet;
				writeTotale(data, idDet, idParent, isATempo, consolidato, true, dettaglio);
				idxDet ++;
			}
		}
	}
	
	public void writeComponentePropriDettagli(JSONArray data, String id, String idParent, List<ConsuntivoCommessaDet> consuntiviCommessaDet, Commessa commessa, boolean isATempo, boolean soloComponentiValorizzate, boolean consolidato) {
		int idxDet = 1;
		for(int i = 0; i < consuntiviCommessaDet.size(); i++) {
			ConsuntivoCommessaDet dettaglio = consuntiviCommessaDet.get(i);			
			//if(soloComponentiValorizzate && dettaglio.getTotale().getCostoTotale() != null && dettaglio.getTotale().getCostoLivello().compareTo(new BigDecimal(0)) > 0) {
			if((!soloComponentiValorizzate) || (soloComponentiValorizzate && dettaglio.getTotale().getCostoTotale() != null && dettaglio.getTotale().getCostoLivello().compareTo(new BigDecimal(0)) > 0)) {
				String idDet = idParent + idxDet;
				writeComponentePropri(data, idDet, idParent, isATempo, consolidato, true, dettaglio);
				idxDet ++;
			}
		}
	}

	public ConsuntivoCommessaDet cercaConsuntivoCommessaDetTotali(ConsuntivoCommessa consuntivo, String idCommessa, String idComponenteTotali) {
		for(int i = 0; i < consuntivo.getConsuntivoCommessaDet().size(); i++) {
			ConsuntivoCommessaDet dettaglio = (ConsuntivoCommessaDet)consuntivo.getConsuntivoCommessaDet().get(i);
			if(dettaglio.getIdCommessa().equals(idCommessa) && dettaglio.getIdComponCosto().equals(idComponenteTotali))
				return dettaglio;
		}
		return null;
	}	
	
	protected void dump()
	{
	   String msg = "";
	   msg += "\nCOMM: " + getIdCommessa();
	   Iterator iter = getConsuntivoCommessaDet().iterator();
	   while(iter.hasNext())
	   {
	      ConsuntivoCommessaDet det = (ConsuntivoCommessaDet)iter.next();
	      if(true)//det.getIdComponCosto().equals("C10"))
	      {
	      msg += "\nCOMP: " + det.getIdComponCosto();
	      msg += "\n EFF LV: " + det.getEffettuato().getCostoLivello() + " LI: " +  det.getEffettuato().getCostoLivelloInf() +" TT: "+  det.getEffettuato().getCostoTotale();
	      msg += "\n TOT LV: " + det.getTotale().getCostoLivello() + " LI: " +  det.getTotale().getCostoLivelloInf() +" TT: "+  det.getTotale().getCostoTotale();
	      }
	   }
	   System.err.println(msg);
	}
}

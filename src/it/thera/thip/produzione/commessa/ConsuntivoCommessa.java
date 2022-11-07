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


   public CalcolatoreDettagliCommesse getCalcolatoreDettagli()
   {
      if(calcolatoreDettagli == null)
         calcolatoreDettagli = (CalcolatoreDettagliCommesse)Factory.createObject(CalcolatoreDettagliCommesse.class);
      return calcolatoreDettagli;
   }

   public void setCalcolatoreDettagli(CalcolatoreDettagliCommesse calcolatoreDettagli)
   {
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

   public void caricaAlberoConsuntivi()
   {
      caricaLivelliInferiori();
      for(ConsuntivoCommessa consLivelloInferiore : consuntiviLivelliInferiori)
         consLivelloInferiore.caricaAlberoConsuntivi();
   }

   public void calcolaAlberoConsuntivi()
   {
      calcolaLivello();
      caricaLivelliInferiori();
      calcolaLivelliInferiori();
      calcolaTotali();
   }


   public void calcolaLivello()
   {
      Articolo articolo = this.getCommessaPrm().getArticolo();
      List<LinkCompSchema> listComponenti = new ArrayList<LinkCompSchema>();
      if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getSchemaCosto() != null)
         listComponenti.addAll(articolo.getClasseMerclg().getSchemaCosto().getComponenti());

      //azzero tutte e non solo quelle contenute nelle componenti articolo
      Iterator iter = getConsuntivoCommessaDet().iterator();
      while(iter.hasNext())
      {
         ConsuntivoCommessaDet curDet = (ConsuntivoCommessaDet)iter.next();
         curDet.azzera();
      }

      List<Integer> idProgrStorici = cercaIdProgrStorici(getIdCommessa(), getDataRiferimento());
      if(idProgrStorici !=  null && !idProgrStorici.isEmpty()) 
      {
         for(int idxProgStor = 0; idxProgStor < idProgrStorici.size(); idxProgStor++) 
         {
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

            for(int idx = 0; idx < listComponenti.size(); idx++) 
            {
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


   public void applicaFormuleTotali()
   {
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
      return true;
   }

   public ConsuntivoCommessa caricaConsuntivoCommessa(Commessa commessa) {
      ConsuntivoCommessa currentConsuntivo = null;
      try {
         String currentConsuntivoKey = KeyHelper.buildObjectKey(new Object[] {getIdAzienda(), getIdConsuntivo(), commessa.getIdCommessa()});
         currentConsuntivo = ConsuntivoCommessa.elementWithKey(currentConsuntivoKey, PersistentObject.NO_LOCK);
         if(currentConsuntivo == null) {
            currentConsuntivo = creaNuovoConsuntivoCommessa(commessa);
         }
         else {
            currentConsuntivo.setDescrizione(getDescrizione());
            currentConsuntivo.setDataRiferimento(getDataRiferimento());
            currentConsuntivo.setUsaDataEstrazioneStorici(isUsaDataEstrazioneStorici());
            currentConsuntivo.setConsolidato(isConsolidato());
            currentConsuntivo.setEstrazioneOrdini(isEstrazioneOrdini());
            currentConsuntivo.setEstrazioneRichieste(isEstrazioneRichieste());
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
      } 
      catch (Exception e) {
         e.printStackTrace(Trace.excStream);
      } 		

      return nuovoConsuntivo;
   }

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
            boolean close = true;
            if(title.equals("Totale"))
               close = false;
            column.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", close));
            column.put("colModel", getColumnModelATempo(title));				

         }
         else {
            String dataIndx = title.substring(0, 3);
            column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", FORMATO_VALORI);
         }			
      }
      return column;		
   }

   public JSONArray getColumnModelATempo(String title) throws JSONException {
      JSONArray columns = new JSONArray();
      String dataIndx = title.substring(0, 3);
      JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"HH").put("dataType", "float").put("format", FORMATO_ORE);
      columns.put(columnOre);
      JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", FORMATO_VALORI);
      columns.put(columnValore);
      return columns;
   }

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

   protected String getValoreString(BigDecimal valore, boolean vuoto)
   {
      boolean valorizzato = valore != null && valore.compareTo(ZERO) != 0;
      if(valorizzato)
         return "" + valore;
      else
      {
         if(!vuoto)
            return "" + ZERO;
         else
            return "";
      }
   }

   protected String getOreString(BigDecimal valore, boolean aTempo,  boolean vuoto)
   {
      if(aTempo)
         return getValoreString(valore, vuoto);
      else
         return "";
   }


   public void writeCommessaGruppo(JSONArray data, String id, String parentId, String descCommessa,
         String compCosto, boolean consolidato, boolean isATempo, ConsuntivoCommessaDet dettaglio) throws JSONException {

      String stileRiga = "RigaNormale";
      JSONObject json = new JSONObject();
      json.put("id", id);
      json.put("parentId", parentId);
      json.put("Commessa", descCommessa);
      json.put("CompCosto", compCosto);
      if (getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI) {
         json.put("CmpElem", "Y");
         json.put("IdCommessa", getIdCommessa());
         json.put("IdCmpCosto", dettaglio.getIdComponCosto());
         json.put("ClassName", "StoricoCommessa");
      }

      if (consolidato) {
         json.put("ConVal", getValoreString(dettaglio.getConsolidato().getCostoTotale(), false));
      }
      json.put("RicVal", getValoreString(dettaglio.getRichiesto().getCostoTotale(), false));
      json.put("OrdVal", getValoreString(dettaglio.getOrdinato().getCostoTotale(), false));
      json.put("SosVal", getValoreString(dettaglio.getEffettuato().getCostoTotale(), false));
      json.put("TotVal", getValoreString( dettaglio.getTotale().getCostoTotale(), false));
      if (isATempo) {
         if (consolidato) 
            json.put("ConHH", getOreString(dettaglio.getConsolidato().getTempoTotale(), getComponenteCosto().isGestioneATempo(), false));
         json.put("RicHH", getOreString(dettaglio.getRichiesto().getTempoTotale(), getComponenteCosto().isGestioneATempo(), false));
         json.put("OrdHH", getOreString(dettaglio.getOrdinato().getTempoTotale(), getComponenteCosto().isGestioneATempo(), false));
         json.put("SosHH", getOreString(dettaglio.getEffettuato().getTempoTotale(), getComponenteCosto().isGestioneATempo(), false));
         json.put("TotHH", getOreString(dettaglio.getTotale().getTempoTotale(), getComponenteCosto().isGestioneATempo(), false));
      }

      if(id.equals("1"))
         stileRiga = "RigaCommessa";
      else
         stileRiga = "RigaSottoCommessa";

      json.put("pq_rowcls", stileRiga);

      data.put(json);
   }

   public void writeComponentePropri(JSONArray data, String id, String idParent, boolean isATempo, boolean consolidato, boolean isDettaglio, ConsuntivoCommessaDet dettaglio) {
      try {

         String stileRiga = "RigaNormale";
         boolean cmpTotali = false;
         boolean totali = false;

         JSONObject json = new JSONObject();
         json.put("id", id);
         json.put("parentId", idParent);
         if(isDettaglio) {
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
            if(dettaglio.getComponenteCosto() != null && dettaglio.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
               cmpTotali = true;
            totali = true;
         }
         else {
            stileRiga = "RigaCmpPropri";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "ComponentiProprie"));
         }

         if (consolidato) {
            json.put("ConVal", getValoreString(dettaglio.getConsolidato().getCostoLivello(), false));
         }
         json.put("RicVal", getValoreString(dettaglio.getRichiesto().getCostoLivello(), false));
         json.put("OrdVal", getValoreString(dettaglio.getOrdinato().getCostoLivello(), false));
         json.put("SosVal", getValoreString(dettaglio.getEffettuato().getCostoLivello(), false));
         json.put("TotVal", getValoreString( dettaglio.getTotale().getCostoLivello(), false));
         if (isATempo) {
            if (consolidato) 
               json.put("ConHH", getOreString(dettaglio.getConsolidato().getTempoLivello(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("RicHH", getOreString(dettaglio.getRichiesto().getTempoLivello(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("OrdHH", getOreString(dettaglio.getOrdinato().getTempoLivello(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("SosHH", getOreString(dettaglio.getEffettuato().getTempoLivello(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("TotHH", getOreString(dettaglio.getTotale().getTempoLivello(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
         }

         json.put("pq_rowcls", stileRiga);
         JSONObject stiliCelle = new JSONObject();
         if(totali)
         {
            stiliCelle.put("TotVal", "CellaTotali");
            stiliCelle.put("TotHH", "CellaTotali");
         }
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");

         json.put("pq_cellcls", stiliCelle);

         data.put(json);
      }
      catch (JSONException e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   public void writeTotale(JSONArray data, String id, String idParent, boolean isATempo, boolean consolidato, boolean isDettaglio, ConsuntivoCommessaDet dettaglio) {
      try {
         String stileRiga = "RigaNormale";
         boolean cmpTotali = false;
         boolean totali = false;

         JSONObject json = new JSONObject();
         json.put("id", id);
         json.put("parentId", idParent);
         if(isDettaglio) {
            json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
            if (dettaglio.getComponenteCosto() != null && dettaglio.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
               cmpTotali = true;
            totali = true;
         }
         else {
            stileRiga = "RigaTotali";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "Totali"));
         }

         if (consolidato) {
            json.put("ConVal", getValoreString(dettaglio.getConsolidato().getCostoTotale(), false));
         }
         json.put("RicVal", getValoreString(dettaglio.getRichiesto().getCostoTotale(), false));
         json.put("OrdVal", getValoreString(dettaglio.getOrdinato().getCostoTotale(), false));
         json.put("SosVal", getValoreString(dettaglio.getEffettuato().getCostoTotale(), false));
         json.put("TotVal", getValoreString( dettaglio.getTotale().getCostoTotale(), false));
         if (isATempo) {
            if (consolidato) 
               json.put("ConHH", getOreString(dettaglio.getConsolidato().getTempoTotale(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("RicHH", getOreString(dettaglio.getRichiesto().getTempoTotale(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("OrdHH", getOreString(dettaglio.getOrdinato().getTempoTotale(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("SosHH", getOreString(dettaglio.getEffettuato().getTempoTotale(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
            json.put("TotHH", getOreString(dettaglio.getTotale().getTempoTotale(), dettaglio.getComponenteCosto().isGestioneATempo(), false));
         }

         json.put("pq_rowcls", stileRiga);
         JSONObject stiliCelle = new JSONObject();
         if(totali)
         {
            stiliCelle.put("TotVal", "CellaTotali");
            stiliCelle.put("TotHH", "CellaTotali");
         }
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");

         json.put("pq_cellcls", stiliCelle);

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

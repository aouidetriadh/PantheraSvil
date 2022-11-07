package it.thera.thip.produzione.commessa;

import java.math.*;
import java.sql.SQLException;
import java.util.*;

import com.thera.thermfw.ad.*;
import com.thera.thermfw.base.*;
import com.thera.thermfw.batch.*;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.type.*;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;

/**
 * StampaConsuntivoCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa BOULILA 08/06/2005
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 *         08/06/2005    A.Boulila  Creation
 * 07430   09/07/2007    LP         Correzioni varie
 * 09786   17/09/2008    DBot       Aggiunta la gestione dei "null" nelle condizioni di filtro
 * 10082   17/11/2008    LP         Aggiunto hook per le personalizzazioni
 * 10222   10/12/2008    LC         Modificata la visibilità della inner class CompactStoricoCommessa
 *                                  da package a protected.
 * 10416   03/02/2009    DBot       Modifica per consuntivazione documenti servizio
 * 31513   21/12/2020    RA			Aggiunto gestione commesse provvisorie
 * 36252   11/07/2022	 RA			Aggiunto nuovo modalita di stampa consuntivo specifico
 */

public class StampaConsuntivoCommessa extends ElaboratePrintRunnable {

   public static final String CLASSAD_COLLECTION_NAME = "StampaConsuntivoCommessa";
   public static final String RESOURCE_FILE = "it.thera.thip.produzione.commessa.resources.StampaConsuntivoCommessa";
   public static final BigDecimal ZERO = new BigDecimal("0"); //...FIX 7430

   // StatoCommessa
   public static final char STATO_COMMESSA__IN_CORSO = '0';
   public static final char STATO_COMMESSA__ATTIVE = '1';
   public static final char STATO_COMMESSA__CHIUSE = '2';
   public static final char STATO_COMMESSA__TUTTE = '3';

   // TipoStampa
   public static final char TIPO_STAMPA__COMPLETA = '0';
   public static final char TIPO_STAMPA__SOLO_RIEPILOGO = '1';
   public static final char TIPO_STAMPA__SOLO_DET_PER_COSTI = '2';
   public static final char TIPO_STAMPA__SOLO_DETTAGLIO = '3';

   // TipoDettaglio
   public static final char TIPO_DETTAGLIO__CON_DET_MOV = '0';
   public static final char TIPO_DETTAGLIO__SOLO_SALDO_FINALE = '1';

   // ArticoliRisorse
   public static final char ARTICOLI_RISORSE__ENTRAMBI = '0';
   public static final char ARTICOLI_RISORSE__SOLO_ARTICOLI = '1';
   public static final char ARTICOLI_RISORSE__SOLO_RISORSE = '2';

   // TipoRiga
   public static final char TIPO_RIGA__TUTTE = '0';
   public static final char TIPO_RIGA__ACQ_LAV_ESN = '1';
   public static final char TIPO_RIGA__PRODUZIONE = '2';
   public static final char TIPO_RIGA__ALTRO = '3';
   public static final char TIPO_RIGA__SERVIZI = '4'; //Fix 10416

   // Batch variables
   protected AvailableReport iAvailableRep;
   protected int iBatchJobId = 0;
   protected int iReportNr = 0;
   protected int iRigaJobId = -1;
   protected int iDetRigaJob = 0;
   protected int iDetRigaJobRiep = 0;

   // Launch mode
   public static final char LAUNCH_MODE__STAMPA_CONSUNTIVO = '0';
   public static final char LAUNCH_MODE__CONSUNTIVAZIONE = '1';

   /**
    * Attributo iCallFromConsuntivazione
    */
   boolean iCallFromConsuntivazione = false;

   /**
    * Attributo iLaunchMode
    */
   protected char iLaunchMode = LAUNCH_MODE__STAMPA_CONSUNTIVO;

   /**
    * Attributo iIdAzienda
    */
   protected String iIdAzienda = Azienda.getAziendaCorrente();

   /**
    * Attributo iStatoCommessa
    */
   protected char iStatoCommessa = STATO_COMMESSA__IN_CORSO;

   /**
    * Attributo iTipoStampa
    */
   protected char iTipoStampa = TIPO_STAMPA__COMPLETA;

   /**
    * Attributo iTipoDettaglio
    */
   protected char iTipoDettaglio = TIPO_DETTAGLIO__CON_DET_MOV;

   /**
    * Attributo iArticoliRisorse
    */
   protected char iArticoliRisorse = ARTICOLI_RISORSE__ENTRAMBI;

   /**
    * Attributo iDataSaldoIniziale
    */
   protected java.sql.Date iDataSaldoIniziale;

   /**
    * Attributo iDataSaldoFinale
    */
   protected java.sql.Date iDataSaldoFinale;

   /**
    * Attributo iTipoRiga
    */
   protected char iTipoRiga = TIPO_RIGA__TUTTE;

   /**
    * Attributo iTipoRisorsa
    */
   protected char iTipoRisorsa = Risorsa.NON_SIGNIFICATIVO;

   /**
    * Attributo iLivelloRisorsa
    */
   protected char iLivelloRisorsa = Risorsa.NON_SIGNIFICATIVO;

   /**
    * Attributo iIdRisorsa
    */
   protected String iIdRisorsa;

   /**
    * CondizioniFiltri iCondizioneFiltri
    */
   protected CondizioniFiltri iCondizioneFiltri;

   /**
    * Attributo iCurrentDataInizio
    */
   protected java.sql.Date iCurrentDataInizio;

   /**
    * Attributo iCurrentDataFine
    */
   protected java.sql.Date iCurrentDataFine;

   /**
    * List iCommesseList
    */
   protected List iCommesseList = new ArrayList();

   protected boolean iCommesseProvvisorie = false;//31513
   //36252 inizio
   protected boolean iUsaConsuntiviStoricizzati = false;
   protected java.sql.Date iDataRiferimento;
   protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);
   protected Proxy iConsuntivoCommessa = new Proxy(it.thera.thip.produzione.commessa.ConsuntivoCommessa.class);
   protected ConsuntivoCommessa iCurrentConsuntivoCommessa;
   //36252 fine

   /**
    * StampaConsuntivoCommessa
    */
   public StampaConsuntivoCommessa() {
      setIdAzienda(Azienda.getAziendaCorrente());
      initCondizioneFiltri();
   }

   /**
    * getIdAzienda
    * @return String
    */
   public String getIdAzienda() {
      return iIdAzienda;
   }

   /**
    * setIdAzienda
    * @param idAzienda String
    */
   public void setIdAzienda(String idAzienda) {
      iIdAzienda = idAzienda;
      setIdAziendaInternal(idAzienda);//36252
   }

   /**
    * getStatoCommessa
    * @return char
    */
   public char getStatoCommessa() {
      return iStatoCommessa;
   }

   /**
    * setStatoCommessa
    * @param statoCommessa char
    */
   public void setStatoCommessa(char statoCommessa) {
      iStatoCommessa = statoCommessa;
   }

   /**
    * getTipoStampa
    * @return char
    */
   public char getTipoStampa() {
      return iTipoStampa;
   }

   /**
    * setTipoStampa
    * @param tipoStampa char
    */
   public void setTipoStampa(char tipoStampa) {
      iTipoStampa = tipoStampa;
   }

   /**
    * getTipoDettaglio
    * @return char
    */
   public char getTipoDettaglio() {
      return iTipoDettaglio;
   }

   /**
    * setTipoDettaglio
    * @param tipoDettaglio char
    */
   public void setTipoDettaglio(char tipoDettaglio) {
      iTipoDettaglio = tipoDettaglio;
   }

   /**
    * getArticoliRisorse
    * @return char
    */
   public char getArticoliRisorse() {
      return iArticoliRisorse;
   }

   /**
    * setArticoliRisorse
    * @param articoliRisorse char
    */
   public void setArticoliRisorse(char articoliRisorse) {
      iArticoliRisorse = articoliRisorse;
   }

   /**
    * getDataSaldoIniziale
    * @return Date
    */
   public java.sql.Date getDataSaldoIniziale() {
      return iDataSaldoIniziale;
   }

   /**
    * setDataSaldoIniziale
    * @param dataSaldoIniziale Date
    */
   public void setDataSaldoIniziale(java.sql.Date dataSaldoIniziale) {
      iDataSaldoIniziale = dataSaldoIniziale;
   }

   /**
    * getDataSaldoFinale
    * @return Date
    */
   public java.sql.Date getDataSaldoFinale() {
      return iDataSaldoFinale;
   }

   /**
    * setDataSaldoFinale
    * @param dataSaldoFinale Date
    */
   public void setDataSaldoFinale(java.sql.Date dataSaldoFinale) {
      iDataSaldoFinale = dataSaldoFinale;
   }

   /**
    * getTipoRiga
    * @return char
    */
   public char getTipoRiga() {
      return iTipoRiga;
   }

   /**
    * setTipoRiga
    * @param tipoRiga char
    */
   public void setTipoRiga(char tipoRiga) {
      iTipoRiga = tipoRiga;
   }

   /**
    * getTipoRisorsa
    * @return char
    */
   public char getTipoRisorsa() {
      return iTipoRisorsa;
   }

   /**
    * setTipoRisorsa
    * @param tipoRisorsa char
    */
   public void setTipoRisorsa(char tipoRisorsa) {
      iTipoRisorsa = tipoRisorsa;
   }

   /**
    * getLivelloRisorsa
    * @return char
    */
   public char getLivelloRisorsa() {
      return iLivelloRisorsa;
   }

   /**
    * setLivelloRisorsa
    * @param livelloRisorsa char
    */
   public void setLivelloRisorsa(char livelloRisorsa) {
      iLivelloRisorsa = livelloRisorsa;
   }

   /**
    * getIdRisorsa
    * @return String
    */
   public String getIdRisorsa() {
      return iIdRisorsa;
   }

   /**
    * setIdRisorsa
    * @param idRisorsa String
    */
   public void setIdRisorsa(String idRisorsa) {
      iIdRisorsa = idRisorsa;
   }

   /**
    * setLaunchMode
    * @param mode char
    */
   public void setLaunchMode(char mode) {
      iLaunchMode = mode;
   }

   /**
    * getLaunchMode
    * @return char
    */
   public char getLaunchMode() {
      return iLaunchMode;
   }

   //36252 inizio
   public void setUsaConsuntiviStoricizzati(boolean usaConsuntiviStoricizzati) {
      iUsaConsuntiviStoricizzati = usaConsuntiviStoricizzati;
   }

   public boolean isUsaConsuntiviStoricizzati() {
      return iUsaConsuntiviStoricizzati;
   }

   public void setDataRiferimento(java.sql.Date dataRiferimento) {
      this.iDataRiferimento = dataRiferimento;
   }

   public java.sql.Date getDataRiferimento() {
      return iDataRiferimento;
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
      return (Commessa)iCommessa.getObject();
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
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idCommessa));
      setIdCommessaInternal(idCommessa);
   }

   public String getIdCommessa() {
      String key = iCommessa.getKey();
      String objIdCommessa = KeyHelper.getTokenObjectKey(key,2);
      return objIdCommessa;
   }

   public void setConsuntivoCommessa(ConsuntivoCommessa consuntivo) {
      String oldObjectKey = getKey();
      String idAzienda = null;
      String idCommessa = null;
      if (consuntivo != null) {
         idAzienda = KeyHelper.getTokenObjectKey(consuntivo.getKey(), 1);
         idCommessa = KeyHelper.getTokenObjectKey(consuntivo.getKey(), 3);
      }
      setIdAziendaInternal(idAzienda);
      setIdCommessaInternal(idCommessa);
      this.iConsuntivoCommessa.setObject(consuntivo);
   }

   public ConsuntivoCommessa getConsuntivoCommessa() {
      return (ConsuntivoCommessa)iConsuntivoCommessa.getObject();
   }

   public void setConsuntivoCommessaKey(String key) {
      String oldObjectKey = getKey();
      iConsuntivoCommessa.setKey(key);
      String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
      setIdAziendaInternal(idAzienda);
      String idCommessa = KeyHelper.getTokenObjectKey(key, 3);
      setIdCommessaInternal(idCommessa);
   }

   public String getConsuntivoCommessaKey() {
      return iConsuntivoCommessa.getKey();
   }

   public void setIdConsuntivo(Integer idConsuntivo) {
      String oldObjectKey = getKey();
      String key = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idConsuntivo));
   }

   public Integer getIdConsuntivo() {
      String key = iConsuntivoCommessa.getKey();
      String objIdConsuntivo = KeyHelper.getTokenObjectKey(key, 2);
      return KeyHelper.stringToIntegerObj(objIdConsuntivo);
   }

   public String getIdCommessaConsuntivo() {
      String key = iConsuntivoCommessa.getKey();
      return KeyHelper.getTokenObjectKey(key, 3);
   }

   public void setIdCommessaConsuntivo(String idCommessaConsuntivo) {
      String oldObjectKey = getKey();
      String key = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idCommessaConsuntivo));
   }

   protected void setIdAziendaInternal(String idAzienda) {
      String key1 = iCommessa.getKey();
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
      String key2 = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
   }

   protected void setIdCommessaInternal(String idCommessa) {
      String key1 = iCommessa.getKey();
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, idCommessa));
      String key2 = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 3, idCommessa));
   }
   //36252 inizio

   /**
    * getCondizioneFiltri
    * @return CondizioniFiltri
    */
   public CondizioniFiltri getCondizioneFiltri() {
      return iCondizioneFiltri;
   }

   /**
    * setCondizioneFiltri
    * @param condizioneFiltri CondizioniFiltri
    */
   public void setCondizioneFiltri(CondizioniFiltri condizioneFiltri) {
      iCondizioneFiltri = condizioneFiltri;
   }

   /**
    * getClassAdCollectionName
    * @return String
    */
   protected String getClassAdCollectionName() {
      return CLASSAD_COLLECTION_NAME;
   }

   /**
    * createReport
    * @return boolean
    */
   public boolean createReport() {
      getBatchJob().setReportCounter((short)0);
      try {
         initializeReportModel();
         if(iCondizioneFiltri.getCondizioneWhere() == null || iCondizioneFiltri.getCondizioneWhere().trim().length() == 0)
            initCondizioniFiltroForBatch();
         return processAction();
      }
      catch(Exception ex) {
         ex.printStackTrace(Trace.excStream);
         return false;
      }
   }

   /**
    * initializeReportModel
    * @throws Exception
    */
   protected void initializeReportModel() throws Exception {
      if(iAvailableRep == null) {
         iBatchJobId = getBatchJob().getBatchJobId();
         iAvailableRep = createNewReport(getReportId());
         setPrintToolInterface((PrintingToolInterface)Factory.createObject(CrystalReportsInterface.class));
         iAvailableRep.setWhereCondition(printToolInterface.generateDefaultWhereCondition(iAvailableRep, ReportConsuntivoCommessaTestataTM.TABLE_NAME));
         iAvailableRep.save();
         ConnectionManager.commit();
         iReportNr = iAvailableRep.getReportNr();
      }
   }

   /**
    * processAction
    * @throws Exception
    * @return boolean
    */
   protected boolean processAction() throws Exception {
      //36252 inizio
      if(isUsaConsuntiviStoricizzati()) {
         initializeConsuntivoCommessa();
         if(getCommessa() != null) {
            iCommesseList.addAll(getCommessa().getRelatedCommesse());
         }				
      }
      else {
         iCommesseList = recuperoCommesse();
      }    
      //36252 fine
      Collections.sort(iCommesseList, createCommessaComparator()); // Fix 04171 Mz

      for(Iterator it = iCommesseList.iterator(); it.hasNext(); ) {
         checkPoint(); //...FIX 7430
         Commessa commessa = (Commessa)it.next();
         resetDataInizioFine(commessa);
         if(createReportConsTestata(commessa) < ErrorCodes.NO_ROWS_UPDATED) {
            ConnectionManager.rollback();
            return false;
         }
         if(getTipoStampa() != TIPO_STAMPA__SOLO_RIEPILOGO) {
            List storiciCommessa = recuperoStoriciCommesse(commessa);
            int ret = createReportConsuntivoCmmDet(storiciCommessa);
            if(ret < ErrorCodes.NO_ROWS_UPDATED) {
               ConnectionManager.rollback();
               return false;
            }
         }
         if(getTipoStampa() == TIPO_STAMPA__COMPLETA || getTipoStampa() == TIPO_STAMPA__SOLO_RIEPILOGO) {
            int ret1 = createRiepiloghi(commessa);
            if(ret1 < ErrorCodes.NO_ROWS_UPDATED)
               return false;
         }
         ConnectionManager.commit();
      }

      return true;
   }

   /**
    * initCondizioniFiltro
    */
   protected void initCondizioneFiltri() {
      iCondizioneFiltri = buildCondizioniFiltri();
   }

   /**
    * buildCondizioniFiltri
    * @return CondizioniFiltri
    */
   public CondizioniFiltri buildCondizioniFiltri() {
      Vector where = new Vector();
      ColonneFiltri cf1 = ColonneFiltri.creaColonnaFiltro("Commessa", "IdAmbienteCommessa", "AmbienteCommessa", true);
      ColonneFiltri cf2 = ColonneFiltri.creaColonnaFiltro("Commessa", "IdCommessaPrincipale", "CommessaPrm", true);
      ColonneFiltri cf3 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdArticolo", "Articolo", "DescrizioneArticolo.Descrizione", true);
      ColonneFiltri cf4 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdGruppoProdotto", "GruppoProdotto", true);
      ColonneFiltri cf5 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdClasseMerceologica", "ClasseMerceologica", true);
      ColonneFiltri cf6 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdClsMateriale", "ClasseMateriale", true);
      ColonneFiltri cf7 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdPianificatore", "Pianificatore", true);
      ColonneFiltri cf8 = CustomColonneFiltri.creaCustomColonnaFiltro("StoricoCommessa", "IdArticoloPrd", "Articolo", "DescrizioneArticolo.Descrizione", true);
      ColonneFiltri cf9 = CustomColonneFiltri.creaCustomColonnaFiltro("StampaConsuntivoCommessa", "IdRisorsa", "TipoLivelloRisorsa", "Descrizione", true);
      ColonneFiltri cf10 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdAttivita", "Attivita", true);
      ColonneFiltri cf11 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdReparto", "Reparto", true);
      ColonneFiltri cf12 = ColonneFiltri.creaColonnaFiltro("StoricoCommessa", "IdCentroLavoro", "CentroLavoro", true);

      where.add(cf1);
      where.add(cf2);
      where.add(cf3);
      where.add(cf4);
      where.add(cf5);
      where.add(cf6);
      where.add(cf7);
      where.add(cf8);
      where.add(cf9);
      where.add(cf10);
      where.add(cf11);
      where.add(cf12);

      return new CondizioniFiltri(where);
   }

   /**
    * createReportConsuntivoCmmDet
    * @param storiciCommesse List
    * @throws Exception
    * @return int
    */
   protected int createReportConsuntivoCmmDet(List storiciCommesse) throws Exception {
      int ret = 0;

      if(getTipoDettaglio() == TIPO_DETTAGLIO__CON_DET_MOV && getTipoStampa() != TIPO_STAMPA__SOLO_DETTAGLIO) {
         int ret1 = createDettaglioCostiPerArtRsr(storiciCommesse);
         if(ret1 < 0)
            return ret1;
         ret += ret1;
      }

      else if(getTipoDettaglio() == TIPO_DETTAGLIO__SOLO_SALDO_FINALE && getTipoStampa() != TIPO_STAMPA__SOLO_DETTAGLIO) {
         int ret1 = createDettaglioCostiSoloTotalePerArtRsr(storiciCommesse);
         if(ret1 < 0)
            return ret1;
         ret += ret1;
      }

      else if(getTipoStampa() == TIPO_STAMPA__SOLO_DETTAGLIO) {
         int ret1 = createSoloDettaglio(storiciCommesse);
         if(ret1 < 0)
            return ret1;
         ret += ret1;
      }

      return ret;
   }

   /**
    * createReportConsTestata
    * @param commessa Commessa
    * @throws Exception
    * @return int
    */
   protected int createReportConsTestata(Commessa commessa) throws Exception {
      ReportConsuntivoCommessaTestata reportConsuntivoCommessa = (ReportConsuntivoCommessaTestata)Factory.createObject(ReportConsuntivoCommessaTestata.class);
      reportConsuntivoCommessa.setBatchJobId(iBatchJobId);
      reportConsuntivoCommessa.setReportNr(iReportNr);
      reportConsuntivoCommessa.setRigaJobId(++iRigaJobId);
      reportConsuntivoCommessa.valorizzaAttributi(commessa);
      reportConsuntivoCommessa.setDataInizio(iCurrentDataInizio);
      reportConsuntivoCommessa.setDataFine(iCurrentDataFine);
      reportConsuntivoCommessa.setTipoDettaglio(getTipoDettaglio());
      reportConsuntivoCommessa.setTipoStampa(getTipoStampa());
      return reportConsuntivoCommessa.save();
   }

   /**
    * createDettaglioCostiPerArtRsr
    * @param storiciCommesse List
    * @throws Exception
    * @return int
    */
   protected int createDettaglioCostiPerArtRsr(List storiciCommesse) throws Exception {
      int ret = 0;

      BigDecimal qtaTempoTotale = ZERO;
      BigDecimal importoTotale = ZERO;

      CompactStoricoCommessa previousCompactStoricoCommessa = null;
      StoricoCommessa previousStoricoCommessa = null;
      StoricoCommessa currentStoricoCommessa = null;
      CompactStoricoCommessa currentCompactStoricoCommessa = null;
      int numDettaglioGenerati = 0;
      int numDettaglioGeneratiLastGroup = 0;

      boolean detSaldoCreated = false;

      for(Iterator it = storiciCommesse.iterator(); it.hasNext(); ) {

         currentStoricoCommessa = (StoricoCommessa)it.next();
         if(!it.hasNext())
            numDettaglioGeneratiLastGroup = numDettaglioGenerati;

         // if (currentStoricoCommessa.getValorizzaCosto() != StoricoCommessa.NO) {
         currentCompactStoricoCommessa = createCompactStoricoCommessa(currentStoricoCommessa);//10222
         boolean rottura = previousCompactStoricoCommessa != null ? previousCompactStoricoCommessa.rottura(currentCompactStoricoCommessa) : false;

         // New group and DataOrigine <= DataInizio
         // ==> we create the saldo iniziale for the previous storico
         if(rottura && compareDataInizio(previousStoricoCommessa.getDataOrigine()) <= 0) {
            int ret1 = createSaldoInizialeTotale(previousStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataInizio,
                  ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);
            if(ret1 < 0)
               return ret1;
            ret += ret1;
         }

         // DataOrigine became > DataInizio in the same group or processing first Storico for current commessa
         // ==> we create the saldo iniziale for the previous storico
         if(!rottura
               && previousStoricoCommessa != null
               && compareDataInizio(currentStoricoCommessa.getDataOrigine()) > 0
               && compareDataInizio(previousStoricoCommessa.getDataOrigine()) <= 0) {
            int ret1 = createSaldoInizialeTotale(previousStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataInizio,
                  ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);
            if(ret1 < 0)
               return ret1;
            ret += ret1;
         }

         // New group and the current storico has DataOrigine > DataInizio
         // ==> we create the saldo iniziale for the current storico
         if((rottura || previousStoricoCommessa == null)
               && (compareDataInizio(currentStoricoCommessa.getDataOrigine()) > 0)) {
            int ret1 = createSaldoInizialeTotale(currentStoricoCommessa, ZERO, ZERO, iCurrentDataInizio,
                  ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);
            if(ret1 < 0)
               return ret1;
            ret += ret1;
         }

         // DataOrigine of the current storico is > DataInizio ==> we create the dettaglio saldo for the current storico
         if(compareDataInizio(currentStoricoCommessa.getDataOrigine()) > 0) {
            //numDettaglioGenerati++;
            int ret1 = createDettaglioSaldo(currentStoricoCommessa);
            if(ret1 < 0)
               return ret1;
            ret += ret1;
            detSaldoCreated = true;
         }

         if(!it.hasNext())
            numDettaglioGeneratiLastGroup = numDettaglioGenerati;

         // New group ==> reinitialization of qtaTempoTotale and importoTotale, create saldo totale
         if(rottura) {
            if(numDettaglioGenerati > 0 && previousStoricoCommessa != null) {
               createSaldoInizialeTotale(previousStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataFine,
                     ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__TOTALE);
            }
            qtaTempoTotale = ZERO;
            importoTotale = ZERO;
            numDettaglioGenerati = 0;
         }

         qtaTempoTotale = addQuantitaTempo(qtaTempoTotale, currentStoricoCommessa);
         importoTotale = addImporto(importoTotale, currentStoricoCommessa);

         previousCompactStoricoCommessa = currentCompactStoricoCommessa;
         previousStoricoCommessa = currentStoricoCommessa;

         if(detSaldoCreated)
            numDettaglioGenerati++;
         //}

         detSaldoCreated = false;

         // Create the saldo finale of the last
         if(!it.hasNext()) {
            if(numDettaglioGenerati > 0) {
               //if(currentStoricoCommessa.getValorizzaCosto() != StoricoCommessa.NO)
               createSaldoInizialeTotale(currentStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataFine,
                     ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__TOTALE);
               /*else if(previousStoricoCommessa != null)
            createSaldoInizialeTotale(previousStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataFine, ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__TOTALE);*/
            }
         }
      }

      // DataOrigine of the last storico is <= DataInizio ==> we create its saldo iniziale
      //if (currentStoricoCommessa != null && currentStoricoCommessa.getValorizzaCosto() != StoricoCommessa.NO) {
      if(currentStoricoCommessa != null) {
         if(currentStoricoCommessa != null) {
            if(compareDataInizio(currentStoricoCommessa.getDataOrigine()) <= 0) {
               int ret1 = createSaldoInizialeTotale(currentStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataInizio,
                     ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);
               if(ret1 < 0)
                  return ret1;
               ret += ret1;
            }
         }
      }

      return ret;
   }

   /**
    * compareDataInizio
    * @param dataOrigine Date
    * @return int
    */
   protected int compareDataInizio(java.sql.Date dataOrigine) {
      if(iCurrentDataInizio == null || dataOrigine.compareTo(iCurrentDataInizio) > 0)
         return 1;
      if(iCurrentDataInizio != null && dataOrigine.compareTo(iCurrentDataInizio) < 0)
         return -1;
      else
         return 0;
   }

   /**
    * createSaldoInizialeTotale
    * @param storicoCommessa StoricoCommessa
    * @param qtaTempoTotale BigDecimal
    * @param importoTotale BigDecimal
    * @param dataSaldo Date
    * @param tipoRigaRpt char
    * @throws Exception
    * @return int
    */
   protected int createSaldoInizialeTotale(StoricoCommessa storicoCommessa, BigDecimal qtaTempoTotale, BigDecimal importoTotale,
         java.sql.Date dataSaldo, char tipoRigaRpt) throws Exception {
      ReportConsuntivoCommessaDettaglio reportConsuntivoCommessaDettaglio = (ReportConsuntivoCommessaDettaglio)Factory.createObject(ReportConsuntivoCommessaDettaglio.class);

      reportConsuntivoCommessaDettaglio.valorizzaAttributi(storicoCommessa);

      reportConsuntivoCommessaDettaglio.setBatchJobId(iBatchJobId);
      reportConsuntivoCommessaDettaglio.setReportNr(iReportNr);
      reportConsuntivoCommessaDettaglio.setRigaJobId(iRigaJobId);
      reportConsuntivoCommessaDettaglio.setDetRigaJob(iDetRigaJob++);
      reportConsuntivoCommessaDettaglio.setTipoRigaRpt(tipoRigaRpt);
      reportConsuntivoCommessaDettaglio.setDataSaldo(dataSaldo);
      reportConsuntivoCommessaDettaglio.setQtaTempo(qtaTempoTotale);
      reportConsuntivoCommessaDettaglio.setCostoTotaleRiferimento(importoTotale);
      reportConsuntivoCommessaDettaglio.setTipoCosto(ReportConsuntivoCommessaDettaglio.TIPO_COSTO__NON_SIGNIFICATIVO);

      return reportConsuntivoCommessaDettaglio.save();
   }

   /**
    * createDettaglioSaldo
    * @param storicoCommessa StoricoCommessa
    * @throws Exception
    * @return int
    */
   protected int createDettaglioSaldo(StoricoCommessa storicoCommessa) throws Exception {
      ReportConsuntivoCommessaDettaglio reportConsuntivoCommessaDettaglio = (ReportConsuntivoCommessaDettaglio)Factory.createObject(ReportConsuntivoCommessaDettaglio.class);

      reportConsuntivoCommessaDettaglio.valorizzaAttributi(storicoCommessa);

      reportConsuntivoCommessaDettaglio.setBatchJobId(iBatchJobId);
      reportConsuntivoCommessaDettaglio.setReportNr(iReportNr);
      reportConsuntivoCommessaDettaglio.setRigaJobId(iRigaJobId);
      reportConsuntivoCommessaDettaglio.setDetRigaJob(iDetRigaJob++);
      reportConsuntivoCommessaDettaglio.setTipoRigaRpt(ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__DETTAGLIO);
      //Fix 10416 inizio
      boolean isCauDaTestata = 
            storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_MATERIALE ||
            storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_PRODOTTO ||
            storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_MATERIALE ||
            storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_PRODOTTO ||
            storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_SPESA;
      if(isCauDaTestata)
         reportConsuntivoCommessaDettaglio.setIdCauOrgRig(storicoCommessa.getIdCauOrgTes());
      /*
    if(storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_MATERIALE
      || storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_PRODOTTO) {
      reportConsuntivoCommessaDettaglio.setIdCauOrgRig(storicoCommessa.getIdCauOrgTes());
    }
       */
      //Fix 10416 fine
      reportConsuntivoCommessaDettaglio.setTipoCosto(getTipoCostoPerRptDet(storicoCommessa));

      return reportConsuntivoCommessaDettaglio.save();
   }

   /**
    * createDettaglioCostiSoloTotalePerArtRsr
    * @param storiciCommesse List
    * @throws Exception
    * @return int
    */
   protected int createDettaglioCostiSoloTotalePerArtRsr(List storiciCommesse) throws Exception {
      int ret = 0;

      BigDecimal qtaTempoTotale = ZERO;
      BigDecimal importoTotale = ZERO;

      CompactStoricoCommessa previousCompactStoricoCommessa = null;
      StoricoCommessa previousStoricoCommessa = null;
      StoricoCommessa currentStoricoCommessa = null;
      CompactStoricoCommessa currentCompactStoricoCommessa = null;

      for(Iterator it = storiciCommesse.iterator(); it.hasNext(); ) {

         currentStoricoCommessa = (StoricoCommessa)it.next();
         // if (currentStoricoCommessa.getValorizzaCosto() != StoricoCommessa.NO) {
         currentCompactStoricoCommessa = createCompactStoricoCommessa(currentStoricoCommessa);//10222
         boolean rottura = previousCompactStoricoCommessa != null ? previousCompactStoricoCommessa.rottura(currentCompactStoricoCommessa) : false;

         // New group ==> reinitialization of qtaTempoTotale and importoTotale, create saldo totale
         if(rottura) {
            createSaldoInizialeTotale(previousStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataFine,
                  ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);
            qtaTempoTotale = ZERO;
            importoTotale = ZERO;
         }

         qtaTempoTotale = addQuantitaTempo(qtaTempoTotale, currentStoricoCommessa);
         importoTotale = addImporto(importoTotale, currentStoricoCommessa);

         previousCompactStoricoCommessa = currentCompactStoricoCommessa;
         previousStoricoCommessa = currentStoricoCommessa;
         //}

         // Create the saldo finale of the last strocico
         if(!it.hasNext()) {
            //if(currentStoricoCommessa.getValorizzaCosto() != StoricoCommessa.NO)
            createSaldoInizialeTotale(currentStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataFine,
                  ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);
            /*else if(previousStoricoCommessa != null)
          createSaldoInizialeTotale(previousStoricoCommessa, qtaTempoTotale, importoTotale, iCurrentDataFine, ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);*/
         }
      }

      return ret;
   }

   /**
    * createSoloDettaglio
    * @param storiciCommesse List
    * @throws Exception
    * @return int
    */
   protected int createSoloDettaglio(List storiciCommesse) throws Exception {
      int ret = 0;

      CompactStoricoCommessa previousCompactStoricoCommessa = null;
      StoricoCommessa currentStoricoCommessa = null;
      CompactStoricoCommessa currentCompactStoricoCommessa = null;

      for(Iterator it = storiciCommesse.iterator(); it.hasNext(); ) {

         currentStoricoCommessa = (StoricoCommessa)it.next();
         //if (currentStoricoCommessa.getValorizzaCosto() != StoricoCommessa.NO) {
         currentCompactStoricoCommessa = createCompactStoricoCommessa(currentStoricoCommessa);//10222
         boolean rottura = previousCompactStoricoCommessa != null ? previousCompactStoricoCommessa.rottura(currentCompactStoricoCommessa) : false;

         if(rottura || previousCompactStoricoCommessa == null) {
            int ret1 = createSaldoInizialeTotale(currentStoricoCommessa, null, null, null,
                  ReportConsuntivoCommessaDettaglio.TIPO_RIGA_RPT__SALDO_INIZIALE);
            if(ret1 < 0)
               return ret1;
            ret += ret1;
         }

         int ret1 = createDettaglioSaldo(currentStoricoCommessa);
         if(ret1 < 0)
            return ret1;
         ret += ret1;

         previousCompactStoricoCommessa = currentCompactStoricoCommessa;
         //}
      }

      return ret;
   }

   /**
    * createRiepiloghi
    * @param commessa Commessa
    * @throws Exception
    * @return int
    */
   protected int createRiepiloghi(Commessa commessa) throws Exception {
      int ret = 0;

      //36252 inizio
      if(isUsaConsuntiviStoricizzati())
         return createRiepiloghiCostiConsuntivi(commessa);
      //36252 fine

      int ret1 = createRiepiloghiCostiDef(commessa);
      if(ret1 < 0)
         return ret1;
      ret += ret1;

      ret1 = createRiepiloghiCostiPrv(commessa);
      if(ret1 < 0)
         return ret1;
      ret += ret1;

      return ret;
   }

   /**
    * createRiepiloghiCostiDef
    * @param commessa Commessa
    * @throws Exception
    * @return int
    */
   protected int createRiepiloghiCostiDef(Commessa commessa) throws Exception {
      int ret = 0;

      String where = CostiCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
            " AND " + CostiCommessaTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
            " AND " + CostiCommessaTM.TIPOLOGIA + " = '" + CostiCommessa.COSTO_CONSUNT_DEFIN + "'" +
            " AND " + CostiCommessaTM.UFFICIALE + " = '" + Column.TRUE_CHAR + "'";
      List costiCmmList = CostiCommessa.retrieveList(CostiCommessa.class, where, "", false);

      for(Iterator it = costiCmmList.iterator(); it.hasNext(); ) {
         CostiCommessa costiCmm = (CostiCommessa)it.next();
         String costiCmmDetWhere = CostiCommessaDetTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
               " AND " + CostiCommessaDetTM.ID_PROGR_STORIC + " = " + costiCmm.getIdProgrStoric() +
               " AND " + CostiCommessaDetTM.TIPO_DETTAGLIO + " = '" + CostiCommessaDet.NORMALE + "'";
         List costiCmmDetList = CostiCommessaDet.retrieveList(CostiCommessaDet.class, costiCmmDetWhere, "", false);

         for(Iterator it2 = costiCmmDetList.iterator(); it2.hasNext(); ) {
            CostiCommessaDet costiCmmDet = (CostiCommessaDet)it2.next();
            ReportConsuntivoCommessaRiepilogo reportConsuntivoCommessaRiepilogo = (ReportConsuntivoCommessaRiepilogo)Factory.createObject(ReportConsuntivoCommessaRiepilogo.class);
            reportConsuntivoCommessaRiepilogo.valorizzaAttributi(costiCmmDet, CostiCommessa.COSTO_CONSUNT_DEFIN);
            reportConsuntivoCommessaRiepilogo.setBatchJobId(iBatchJobId);
            reportConsuntivoCommessaRiepilogo.setReportNr(iReportNr);
            reportConsuntivoCommessaRiepilogo.setRigaJobId(iRigaJobId);
            reportConsuntivoCommessaRiepilogo.setDetRigaJob(iDetRigaJobRiep++);
            int ret1 = reportConsuntivoCommessaRiepilogo.save();
            if(ret1 < 0)
               return ret1;
            ret += ret1;
         }
      }
      return ret;
   }

   /**
    * createRiepiloghiCostiPrv
    * @param commessa Commessa
    * @throws Exception
    * @return int
    */
   protected int createRiepiloghiCostiPrv(Commessa commessa) throws Exception {
      int ret = 0;
      String where = CostiCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "'" +
            " AND " + CostiCommessaTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
            " AND " + CostiCommessaTM.TIPOLOGIA + " = '" + CostiCommessa.COSTO_CONSUNT_PROVV + "'" +
            " AND " + CostiCommessaTM.UFFICIALE + " = '" + Column.TRUE_CHAR + "'";
      List costiCmmList = CostiCommessa.retrieveList(CostiCommessa.class, where, "", false);

      for(Iterator it = costiCmmList.iterator(); it.hasNext(); ) {
         CostiCommessa costiCmm = (CostiCommessa)it.next();
         String costiCmmDetWhere = CostiCommessaDetTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
               " AND " + CostiCommessaDetTM.ID_PROGR_STORIC + " = " + costiCmm.getIdProgrStoric() +
               " AND " + CostiCommessaDetTM.TIPO_DETTAGLIO + " = '" + CostiCommessaDet.NORMALE + "'";
         List costiCmmDetList = CostiCommessaDet.retrieveList(CostiCommessaDet.class, costiCmmDetWhere, "", false);
         char tipoCostoConsuntivo = CostiCommessa.COSTO_CONSUNT_DEFIN;
         if(iCurrentConsuntivoCommessa.getStatoAvanzamento() == ConsuntivoCommessa.PROVVISORIO)
            tipoCostoConsuntivo = CostiCommessa.COSTO_CONSUNT_PROVV;
         for(Iterator it2 = costiCmmDetList.iterator(); it2.hasNext(); ) {
            CostiCommessaDet costiCmmDet = (CostiCommessaDet)it2.next();
            ReportConsuntivoCommessaRiepilogo reportConsuntivoCommessaRiepilogo = getSimilarRigaRpt(costiCmmDet.getIdCommessa(), costiCmmDet.getIdComponCosto());
            if(reportConsuntivoCommessaRiepilogo == null) {
               reportConsuntivoCommessaRiepilogo = (ReportConsuntivoCommessaRiepilogo)Factory.createObject(ReportConsuntivoCommessaRiepilogo.class);
               reportConsuntivoCommessaRiepilogo.valorizzaAttributi(costiCmmDet, CostiCommessa.COSTO_CONSUNT_PROVV);
               reportConsuntivoCommessaRiepilogo.setBatchJobId(iBatchJobId);
               reportConsuntivoCommessaRiepilogo.setReportNr(iReportNr);
               reportConsuntivoCommessaRiepilogo.setRigaJobId(iRigaJobId);
               reportConsuntivoCommessaRiepilogo.setDetRigaJob(iDetRigaJobRiep++);
               int ret1 = reportConsuntivoCommessaRiepilogo.save();
               if(ret1 < 0)
                  return ret1;
               ret += ret1;
            }
            else {
               reportConsuntivoCommessaRiepilogo.setCostoTotaleProvvisoria(costiCmmDet.getCostoTotale());
               BigDecimal costoPrv = reportConsuntivoCommessaRiepilogo.getCostoTotaleProvvisoria() != null ? reportConsuntivoCommessaRiepilogo.getCostoTotaleProvvisoria() : ZERO;
               BigDecimal costoDef = reportConsuntivoCommessaRiepilogo.getCostoTotaleDefinitivo() != null ? reportConsuntivoCommessaRiepilogo.getCostoTotaleDefinitivo() : ZERO;
               reportConsuntivoCommessaRiepilogo.setCostoTotalePeriodo(costoPrv.add(costoDef.negate()));

               reportConsuntivoCommessaRiepilogo.setCostoLivelloProvvisoria(costiCmmDet.getCostoLivello());
               costoPrv = reportConsuntivoCommessaRiepilogo.getCostoLivelloProvvisoria() != null ? reportConsuntivoCommessaRiepilogo.getCostoLivelloProvvisoria() : ZERO;
               costoDef = reportConsuntivoCommessaRiepilogo.getCostoLivelloDefinitivo() != null ? reportConsuntivoCommessaRiepilogo.getCostoLivelloDefinitivo() : ZERO;
               reportConsuntivoCommessaRiepilogo.setCostoLivelloPeriodo(costoPrv.add(costoDef.negate()));

               reportConsuntivoCommessaRiepilogo.setCostoLivelloInfProvvisoria(costiCmmDet.getCostoLivelliInf());
               costoPrv = reportConsuntivoCommessaRiepilogo.getCostoLivelloInfProvvisoria() != null ? reportConsuntivoCommessaRiepilogo.getCostoLivelloInfProvvisoria() : ZERO;
               costoDef = reportConsuntivoCommessaRiepilogo.getCostoLivelloInfDefinitivo() != null ? reportConsuntivoCommessaRiepilogo.getCostoLivelloInfDefinitivo() : ZERO;
               reportConsuntivoCommessaRiepilogo.setCostoLivelloInfPeriodo(costoPrv.add(costoDef.negate()));

               int ret1 = reportConsuntivoCommessaRiepilogo.save();
               if(ret1 < 0)
                  return ret1;
               ret += ret1;
            }
         }
      }
      return ret;
   }

   /**
    * getSimilarRigaRpt
    * @param idCommessa String
    * @param idCompCosto String
    * @throws Exception
    * @return ReportConsuntivoCommessaRiepilogo
    */
   protected ReportConsuntivoCommessaRiepilogo getSimilarRigaRpt(String idCommessa, String idCompCosto) throws Exception {
      String where = ReportConsuntivoCommessaRiepilogoTM.BATCH_JOB_ID + " = " + iBatchJobId +
            " AND " + ReportConsuntivoCommessaRiepilogoTM.REPORT_NR + " = " + iReportNr +
            " AND " + ReportConsuntivoCommessaRiepilogoTM.R_COMMESSA + " = '" + idCommessa + "'" +
            " AND " + ReportConsuntivoCommessaRiepilogoTM.R_COMPON_COST + " = '" + idCompCosto + "'";
      List rptRiepList = ReportConsuntivoCommessaRiepilogo.retrieveList(ReportConsuntivoCommessaRiepilogo.class, where, "", false);
      if(rptRiepList.size() > 0)
         return(ReportConsuntivoCommessaRiepilogo)rptRiepList.get(0);
      return null;
   }

   /**
    * getTipoCostoPerRptDet
    * @param storicoCommessa StoricoCommessa
    * @return char
    */
   protected char getTipoCostoPerRptDet(StoricoCommessa storicoCommessa) {
      if(!storicoCommessa.getGesSaldiCommessa()) {
         return ReportConsuntivoCommessaDettaglio.TIPO_COSTO__STANDARD;
      }
      else {
         if((storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.ACQUISTO
               || storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.LAVORAZIONE_ESTERNA_MATERIALE)
               && storicoCommessa.getIdNumeroFattura() == null) {
            return ReportConsuntivoCommessaDettaglio.TIPO_COSTO__PREVISTO;
         }
         else {
            return ReportConsuntivoCommessaDettaglio.TIPO_COSTO__EFFETTIVO;
         }
      }
   }

   /**
    * addQuantitaTempo
    * @param qtaTempo BigDecimal
    * @param storicoCommessa StoricoCommessa
    * @return BigDecimal
    */
   protected BigDecimal addQuantitaTempo(BigDecimal qtaTempo, StoricoCommessa storicoCommessa) {
      BigDecimal tempQtaTempo = ZERO;
      //Fix 10416 inizio
      boolean risorsa = 
            storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_RISORSA ||
            storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_RISORSA;
      //if(storicoCommessa.getTipoRigaOrigine() != StoricoCommessa.PRODUZIONE_RISORSA) {
      if(!risorsa) {
         //Fix 10416 fine
         if(storicoCommessa.getQuantitaUMPrm() != null)
            tempQtaTempo = tempQtaTempo.add(storicoCommessa.getQuantitaUMPrm());
         if(storicoCommessa.getQtaScarto() != null)
            tempQtaTempo = tempQtaTempo.add(storicoCommessa.getQtaScarto());
      }
      else {
         if(storicoCommessa.getTipoRilevazioneRsr() == Risorsa.TEMPO && storicoCommessa.getTempo() != null)
            tempQtaTempo = tempQtaTempo.add(storicoCommessa.getTempo());
         else {
            if(storicoCommessa.getQuantitaUMPrm() != null)
               tempQtaTempo = tempQtaTempo.add(storicoCommessa.getQuantitaUMPrm());
            if(storicoCommessa.getQtaScarto() != null)
               tempQtaTempo = tempQtaTempo.add(storicoCommessa.getQtaScarto());
         }
      }
      if(storicoCommessa.getValorizzaCosto() == StoricoCommessa.DECREMENTA_COSTO) {
         tempQtaTempo = tempQtaTempo.negate();
      }
      return qtaTempo.add(tempQtaTempo);
   }

   /**
    * addImporto
    * @param importo BigDecimal
    * @param storicoCommessa StoricoCommessa
    * @return BigDecimal
    */
   protected BigDecimal addImporto(BigDecimal importo, StoricoCommessa storicoCommessa) {
      BigDecimal tempImporto = ZERO;
      if(storicoCommessa.getCostoTotRifer() != null)
         tempImporto = tempImporto.add(storicoCommessa.getCostoTotRifer());
      if(storicoCommessa.getValorizzaCosto() == StoricoCommessa.DECREMENTA_COSTO) {
         tempImporto = tempImporto.negate();
      }
      return importo.add(tempImporto);
   }

   /**
    * recuperoCommesse
    * @throws Exception
    * @return List
    */
   protected List recuperoCommesse() throws Exception {
      String where = CommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
            " AND " + CommessaTM.STATO + " = '" + DatiComuniEstesi.VALIDO + "'";

      where += " AND " + getNaturaCommessaCondition();
      //31513 inizio
      where += " AND (" + getCmmConditionFromStato();
      if(isCommesseProvvisorie()) {
         where += " OR ("+ CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__PROVVISORIA + "'" +") ";
      }
      where += ")";
      //31513 fine

      Vector allColumn = new Vector();
      // FIX 27/09/05
      if(iCallFromConsuntivazione)
         allColumn = getCondizioneFiltri().getColonneFiltro();
      else
         allColumn = getAllColumn();
      //String filtroCondition = getFilterSQL(allColumn);
      String filtroCondition = getCmmConditionFromFiltro();
      if(filtroCondition.length() > 0) {
         where += " AND " + filtroCondition;
      }

      String orderBy = CommessaTM.R_AMBIENTE_CMM + ","
            + CommessaTM.R_COMMESSA_PRM + ","
            + CommessaTM.LIVELLO_CMM + ","
            + CommessaTM.ID_COMMESSA;
      return Commessa.retrieveList(Commessa.class, where, orderBy, false);
   }

   /**
    * getAllColumn
    * @return Vector
    */
   protected Vector getAllColumn() {
      //Fix 9786 inizio
      iCondizioneFiltri.impostaColonneConVettoreValoriFiltro(); 
      return iCondizioneFiltri.getColonneFiltro();
      /*
    Vector result = getCondizioneFiltri().getColonneFiltro();
    ArrayList source = getCondizioneFiltri().getVettoreValoriFiltro();
    for(int i = 0; i < result.size(); i++) {
      //...FIX 7430 inizio
      ColonneFiltri colFlt = (ColonneFiltri)result.get(i);
      ArrayList valori = (ArrayList)source.get(i);
      colFlt.setFrom((String)valori.get(2));
      colFlt.setTo((String)valori.get(3));
      colFlt.setListaString((String)valori.get(4));
      //...Faccio la try/catch di queste due righe di codice perchè
      //...potrebbero esserci dei lavori schedulati che non contengono
      //...i valori per le esclusioni, e quindi la get potrebbe andare in
      //...eccezione perchè l'Array contiene solo 5 valori (e non 7)
      try {
        colFlt.setRangeEsclusione(valori.get(5).equals("true") ? true : false);
        colFlt.setListaEsclusione(valori.get(6).equals("true") ? true : false);
      }
      catch(Exception e) {}
      //...FIX 7430 fine
    }
    return result;
       */
      //Fix 9786 fine
   }

   /**
    * getFilterSQL
    * @param allColumn Vector
    * @return String
    */
   protected String getFilterSQL(Vector allColumn) {
      String toTreat = "";
      for(int i = 0; i < allColumn.size(); i++) {
         ColonneFiltri cf = (ColonneFiltri)allColumn.get(i);
         String myWhereCondition = cf.getWhere();
         if(myWhereCondition != null && myWhereCondition.trim().length() > 0) {
            toTreat += " AND (" + myWhereCondition + ")";
         }
      }
      return toTreat;
   }

   /**
    * getNaturaCommessaCondition
    * @return String
    */
   public String getNaturaCommessaCondition() {
      return CommessaTM.R_TIPO_COMMESSA +
            " IN (SELECT " + TipoCommessaTM.ID_TIPO_CMM +
            " FROM " + TipoCommessaTM.TABLE_NAME +
            " WHERE " + TipoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
            " AND " + TipoCommessaTM.NATURA_CMM + " = '" + TipoCommessa.NATURA_CMM__GESTIONALE + "')";
   }

   /**
    * getCmmConditionFromStato
    * @return String
    */
   protected String getCmmConditionFromStato() {
      if(getStatoCommessa() == STATO_COMMESSA__IN_CORSO) {
         return
               "(" + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CONFERMATA + "' OR "
               + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_TECNICAMENTO + "')";
      }
      else if(getStatoCommessa() == STATO_COMMESSA__ATTIVE) {
         return
               "(" + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CONFERMATA + "' OR "
               + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_TECNICAMENTO + "' OR "
               + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_OPERATIVAMENTO + "')";
      }
      else if(getStatoCommessa() == STATO_COMMESSA__CHIUSE) {
         return CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_CONTABILAMENTO + "'";
      }
      //else if(getStatoCommessa() == STATO_COMMESSA__TUTTE) {//31513
      else if(getStatoCommessa() == STATO_COMMESSA__TUTTE && !isCommesseProvvisorie()) {//31513
         return CommessaTM.STATO_AVANZAMENTO + " <> '" + Commessa.STATO_AVANZAM__PROVVISORIA + "'";
      }
      return "";
   }

   /**
    * getCmmConditionFromFiltro
    * @return String
    */
   protected String getCmmConditionFromFiltro() {
      String ret = "";
      if(iCondizioneFiltri.getCondizioneWhere() != null && iCondizioneFiltri.getCondizioneWhere().trim().length() > 0) {
         String ambCondition = getConditionFromColonneFiltro(0);
         String cmmCondition = getConditionFromColonneFiltro(1);
         if(ambCondition.length() > 0) {
            ret = "(" + ambCondition;
         }
         if(cmmCondition.length() > 0) {
            if(ret.length() > 0)
               ret += " AND ";
            else
               ret = "(";
            ret += cmmCondition;
         }

         if(ret.length() > 0)
            ret += ")";
      }
      return ret;
   }

   /**
    * getConditionFromColonneFiltro
    * @param colIndex int
    * @return String
    */
   protected String getConditionFromColonneFiltro(int colIndex) {
      String condition = "";
      ColonneFiltri colFlt = (ColonneFiltri)iCondizioneFiltri.getColonneFiltro().get(colIndex);
      if(colFlt.getWhere() != null && !colFlt.getWhere().equals("")) {
         condition += "(" + colFlt.getWhere() + ")";
      }
      return condition;
   }

   //...FIX 10082 inizio

   /**
    * recuperoStoriciCommesse
    * @param commessa Commessa
    * @throws Exception
    * @return List
    */
   protected List recuperoStoriciCommesse(Commessa commessa) throws Exception {
      //36252 inizio
      List<StoricoCommessa> storici = new ArrayList<StoricoCommessa>();
      String where = "";
      if(isUsaConsuntiviStoricizzati() && iCurrentConsuntivoCommessa != null) {
         ConsuntivoCommessa consuntivo = null;
         String currentConsuntivoKey = KeyHelper.buildObjectKey(new Object[] {getIdAzienda(), iCurrentConsuntivoCommessa.getIdConsuntivo(), commessa.getIdCommessa()});
         consuntivo = ConsuntivoCommessa.elementWithKey(currentConsuntivoKey, PersistentObject.NO_LOCK);
         if(consuntivo != null) {
            where = buildWhereStoriciCommesse(consuntivo);
         }
      }
      else
         where = buildWhereStoriciCommesse(commessa);    

      String orderBy = buildOrderByStoriciCommesse(commessa);
      if(where != null && !where.equals(""))
         storici = StoricoCommessa.retrieveList(StoricoCommessa.class, where, orderBy, false);
      return storici;
      //36252 fine
   }

   /**
    * buildWhereStoriciCommesse
    * @param commessa Commessa
    * @return String
    */
   public String buildWhereStoriciCommesse(Commessa commessa) {
      String where = StoricoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
            " AND " + StoricoCommessaTM.R_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
            " AND " + StoricoCommessaTM.DOC_ORIGINE + " = '" + StoricoCommessa.DOCUMENTO + "'" +
            " AND " + StoricoCommessaTM.VALORIZZA_COSTO + " <> '" + StoricoCommessa.NO + "'";

      if(iCurrentDataFine != null)
         where += " AND " + StoricoCommessaTM.DATA_ORG + " <= " + ConnectionManager.getCurrentDatabase().getLiteral(iCurrentDataFine);

      if(iCurrentDataInizio != null && getTipoStampa() == TIPO_STAMPA__SOLO_DETTAGLIO)
         where += " AND " + StoricoCommessaTM.DATA_ORG + " >= " + ConnectionManager.getCurrentDatabase().getLiteral(iCurrentDataInizio);

      if(getTipoStampa() != TIPO_STAMPA__COMPLETA && getTipoStampa() != TIPO_STAMPA__SOLO_RIEPILOGO) {
         String tipoOrgCondition = getTipoRigaOrigineCondition();
         if(tipoOrgCondition.length() > 0)
            where += " AND " + tipoOrgCondition;

         where += getStoricoCmmConditionFromFiltro();

         String risorsaCondition = "";
         if(getTipoRisorsa() != Risorsa.NON_SIGNIFICATIVO)
            risorsaCondition = StoricoCommessaTM.R_TIPO_RISORSA + " = '" + getTipoRisorsa() + "'";

         if(getLivelloRisorsa() != Risorsa.NON_SIGNIFICATIVO) {
            if(risorsaCondition.length() > 0)
               risorsaCondition += " AND ";
            risorsaCondition += StoricoCommessaTM.R_LIVELLO_RISORSA + " = '" + getLivelloRisorsa() + "'";
         }
         if(risorsaCondition.length() > 0) {
            risorsaCondition += getRisorsaConditionFromFiltro();
         }
         else {
            String rsrConditionFromFiltro = getRisorsaConditionFromFiltro();
            if(rsrConditionFromFiltro.length() > 0)
               risorsaCondition += rsrConditionFromFiltro.substring(5, rsrConditionFromFiltro.length());
         }

         //Fix 04361 Mz inizio
         //if (risorsaCondition.length() > 0) {
         //  where += " AND ((" + risorsaCondition + ") OR " + StoricoCommessaTM.TIPO_RIGA_ORIGINE + "<>'" + StoricoCommessa.PRODUZIONE_RISORSA + "')";
         //}
         String articoloCondition = getArticoloConditionFromFiltro();
         //Fix 10416 inizio
         String righeRisorsa = 
               "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_RISORSA + "' OR " + 
                     StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_RISORSA + "')";

         String righeArticoli = 
               "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " <> '" + StoricoCommessa.PRODUZIONE_RISORSA + "' AND " + 
                     StoricoCommessaTM.TIPO_RIGA_ORIGINE + " <> '" + StoricoCommessa.SERVIZIO_RISORSA + "')";

         if((articoloCondition.length() > 0) && (risorsaCondition.length() > 0)) 
         {
            String risorsaConditionEnh = righeRisorsa + " AND (" + risorsaCondition + ")";
            String articoloConditionEnh = righeArticoli + " AND (" + articoloCondition + ")";
            where += " AND ((" + risorsaConditionEnh + ") OR (" + articoloConditionEnh + "))";
         }
         else if(articoloCondition.length() > 0)
            where += " AND ((" + articoloCondition + ") OR " + righeRisorsa + ")";
         else if(risorsaCondition.length() > 0)
            where += " AND ((" + risorsaCondition + ") OR (" + righeArticoli + ")";
/*
      if((articoloCondition.length() > 0) && (risorsaCondition.length() > 0)) {
        String risorsaConditionEnh = "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_RISORSA + "') AND (" + risorsaCondition + ")";
        String articoloConditionEnh = "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " <> '" + StoricoCommessa.PRODUZIONE_RISORSA + "') AND (" + articoloCondition + ")";
        where += " AND ((" + risorsaConditionEnh + ") OR (" + articoloConditionEnh + "))";
      }
      else if(articoloCondition.length() > 0)
        where += " AND ((" + articoloCondition + ") OR (" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_RISORSA + "'))";
      else if(risorsaCondition.length() > 0)
        where += " AND ((" + risorsaCondition + ") OR (" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + "<>'" + StoricoCommessa.PRODUZIONE_RISORSA + "'))";
        //Fix 04361 Mz fine
 */
         //Fix 10416 fine
      }

      return where;
   }

   /**
    * buildOrderByStoriciCommesse
    * @param commessa Commessa
    * @return String
    */
   public String buildOrderByStoriciCommesse(Commessa commessa) {
      String orderBy = StoricoCommessaTM.R_COMMESSA + ", "
            + StoricoCommessaTM.R_TIPO_RISORSA + ", "
            //Fix 04361 Mz inizio
            + StoricoCommessaTM.R_LIVELLO_RISORSA + ", "
            ////Fix 04361 Mz fine
            + StoricoCommessaTM.R_RISORSA + ", "
            + StoricoCommessaTM.R_ARTICOLO + ", "
            + StoricoCommessaTM.R_VERSIONE + ", "
            + StoricoCommessaTM.COD_CONFIG + ", "
            + StoricoCommessaTM.DATA_ORG;
      return orderBy;
   }

   //...FIX 10082 fine

   /**
    * resetDataInizioFine
    * @param commessa Commessa
    */
   protected void resetDataInizioFine(Commessa commessa) {
      iCurrentDataInizio = recuperoDataInizio(commessa);
      iCurrentDataFine = recuperoDataFine(commessa);
      iCurrentDataFine = correttaDataFine(iCurrentDataFine, iCurrentDataInizio);
   }

   /**
    * correttaDataFine
    * @param dataFine Date
    * @param dataInizio Date
    * @return Date
    */
   public static java.sql.Date correttaDataFine(java.sql.Date dataFine, java.sql.Date dataInizio) {
      if(dataInizio == null)
         return dataFine;
      if(dataFine == null)
         return dataInizio;
      if(TimeUtils.differenceInDays(dataFine, dataInizio) < 0)
         return dataInizio;
      return dataFine;
   }

   /**
    * recuperoDataInizio
    * @param commessa Commessa
    * @return Date
    */
   protected java.sql.Date recuperoDataInizio(Commessa commessa) {
      if(getDataSaldoIniziale() != null)
         return getDataSaldoIniziale();
      return(commessa.getAmbienteCommessa() == null) ? null : commessa.getAmbienteCommessa().getDataChiusDef();
   }

   /**
    * recuperoDataFine
    * @param commessa Commessa
    * @return Date
    */
   protected java.sql.Date recuperoDataFine(Commessa commessa) {
      if(getDataSaldoFinale() != null)
         return getDataSaldoFinale();
      return(commessa.getAmbienteCommessa() == null) ? null : commessa.getAmbienteCommessa().getDataChiusPrv();
   }

   /**
    * getTipoRigaOrigineCondition
    * @return String
    */
   protected String getTipoRigaOrigineCondition() {
      String ret = "";

      if(getArticoliRisorse() == ARTICOLI_RISORSE__ENTRAMBI) { // Articoli e risorse
         if(getTipoRiga() == TIPO_RIGA__TUTTE) {
            ret = "";
         }
         else if(getTipoRiga() == TIPO_RIGA__ACQ_LAV_ESN) {
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.ACQUISTO + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.LAVORAZIONE_ESTERNA_MATERIALE + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.LAVORAZIONE_ESTERNA_PRODOTTO + "')";
         }
         else if(getTipoRiga() == TIPO_RIGA__PRODUZIONE) {
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_MATERIALE + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_PRODOTTO + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_RISORSA + "')";
         }
         else if(getTipoRiga() == TIPO_RIGA__SERVIZI) { //Fix 10416 inizio
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_MATERIALE + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_PRODOTTO + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_RISORSA + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_SPESA + "')";
         }//Fix 10416 fine
         else if(getTipoRiga() == TIPO_RIGA__ALTRO) {
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.VENDITA + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.GENERICO + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.TRASFERIMENTO_MAGAZZINO + "')";
         }
      }
      else if(getArticoliRisorse() == ARTICOLI_RISORSE__SOLO_ARTICOLI) { // Solo articoli
         if(getTipoRiga() == TIPO_RIGA__TUTTE) {
            //Fix 10416 inizio
            ret = "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " <> '" + StoricoCommessa.PRODUZIONE_RISORSA + "' AND " + 
                  StoricoCommessaTM.TIPO_RIGA_ORIGINE + " <> '" + StoricoCommessa.SERVIZIO_RISORSA + "')";
            //ret = "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + "<>'" + StoricoCommessa.PRODUZIONE_RISORSA + "')";
            //Fix 10416 fine
         }
         else if(getTipoRiga() == TIPO_RIGA__ACQ_LAV_ESN) {
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.ACQUISTO + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.LAVORAZIONE_ESTERNA_MATERIALE + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.LAVORAZIONE_ESTERNA_PRODOTTO + "')";
         }
         else if(getTipoRiga() == TIPO_RIGA__PRODUZIONE) {
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_MATERIALE + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.PRODUZIONE_PRODOTTO + "')";
         }
         else if(getTipoRiga() == TIPO_RIGA__SERVIZI) { //Fix 10416 inizio
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_MATERIALE + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_PRODOTTO + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.SERVIZIO_SPESA + "')";
         } //Fix 10416 fine
         else if(getTipoRiga() == TIPO_RIGA__ALTRO) {
            ret =
                  "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.VENDITA + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.GENERICO + "' OR "
                        + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.TRASFERIMENTO_MAGAZZINO + "')";
         }
      }
      else if(getArticoliRisorse() == ARTICOLI_RISORSE__SOLO_RISORSE) { // Solo risorse
         //Fix 10416 inizio
         ret = 
               "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + "='" + StoricoCommessa.PRODUZIONE_RISORSA + "' OR " + 
                     StoricoCommessaTM.TIPO_RIGA_ORIGINE + "='" + StoricoCommessa.SERVIZIO_RISORSA + "')";
         //ret = "(" + StoricoCommessaTM.TIPO_RIGA_ORIGINE + "='" + StoricoCommessa.PRODUZIONE_RISORSA + "')";
         //Fix 10416 fine
      }

      return ret;
   }

   /**
    * getStoricoCmmConditionFromFiltro
    * @return String
    */
   protected String getStoricoCmmConditionFromFiltro() {
      String ret = "";
      if(iCondizioneFiltri.getCondizioneWhere() != null && iCondizioneFiltri.getCondizioneWhere().trim().length() > 0) {
         for(Iterator it = iCondizioneFiltri.getColonneFiltro().iterator(); it.hasNext(); ) {
            ColonneFiltri colFlt = (ColonneFiltri)it.next();
            if(!colFlt.getClassAdName().equals("IdAmbienteCommessa")
                  && !colFlt.getClassAdName().equals("IdCommessaPrincipale")
                  && !colFlt.getClassAdName().equals("IdRisorsa")
                  && !colFlt.getClassAdName().equals("IdAttivita")
                  && !colFlt.getClassAdName().equals("IdReparto")
                  && !colFlt.getClassAdName().equals("IdCentroLavoro")
                  //Fix 04361 Mz inizio
                  && !colFlt.getClassAdName().equals("IdArticolo")
                  && !colFlt.getClassAdName().equals("IdGruppoProdotto")
                  && !colFlt.getClassAdName().equals("IdClasseMerceologica")
                  && !colFlt.getClassAdName().equals("IdClsMateriale")
                  && !colFlt.getClassAdName().equals("IdPianificatore")
                  //Fix 04361 Mz fine
                  ) {
               if(colFlt.getWhere() != null && colFlt.getWhere().trim().length() > 0) {
                  ret += " AND (" + colFlt.getWhere() + ")";
               }
            }
         }
      }
      return ret;
   }

   /**
    * getRisorsaConditionFromFiltro
    * @return String
    */
   protected String getRisorsaConditionFromFiltro() {
      String ret = "";
      for(Iterator it = iCondizioneFiltri.getColonneFiltro().iterator(); it.hasNext(); ) {
         ColonneFiltri colFlt = (ColonneFiltri)it.next();
         if(colFlt.getClassAdName().equals("IdRisorsa")
               || colFlt.getClassAdName().equals("IdAttivita")
               || colFlt.getClassAdName().equals("IdReparto")
               || colFlt.getClassAdName().equals("IdCentroLavoro")) {
            if(colFlt.getWhere() != null && colFlt.getWhere().trim().length() > 0) {
               String where = colFlt.getWhere();
               if(colFlt.getClassAdName().equals("IdRisorsa"))
                  where = getWhereForRisorsa(colFlt);
               ret += " AND (" + where + ")";
            }
         }
      }
      return ret;
   }

   /**
    * getArticoloConditionFromFiltro
    * @return String
    */
   protected String getArticoloConditionFromFiltro() {
      String ret = null;
      for(Iterator it = iCondizioneFiltri.getColonneFiltro().iterator(); it.hasNext(); ) {
         ColonneFiltri colFlt = (ColonneFiltri)it.next();
         if(colFlt.getClassAdName().equals("IdArticolo")
               || colFlt.getClassAdName().equals("IdGruppoProdotto")
               || colFlt.getClassAdName().equals("IdClasseMerceologica")
               || colFlt.getClassAdName().equals("IdClsMateriale")
               || colFlt.getClassAdName().equals("IdPianificatore")) {
            if(colFlt.getWhere() != null && colFlt.getWhere().trim().length() > 0) {
               String where = colFlt.getWhere();
               if(ret == null)
                  ret = "(" + where + ")";
               else
                  ret += " AND (" + where + ")";
            }
         }
      }
      return(ret == null) ? "" : ret;
   }

   /**
    * replaceColName
    * @param where String
    * @param originalColName String
    * @param replaceColName String
    * @return String
    */
   protected String replaceColName(String where, String originalColName, String replaceColName) {
      while(where.indexOf(originalColName) != -1) {
         int idx = where.indexOf(originalColName);
         where = where.substring(0, idx) + replaceColName + where.substring(idx + replaceColName.length(), where.length());
      }
      return where;
   }

   /**
    * getWhereForRisorsa
    * @param colonneFiltri ColonneFiltri
    * @return String
    */
   protected String getWhereForRisorsa(ColonneFiltri colonneFiltri) {
      //return getWhere(colonneFiltri);

      try {
         ColonneFiltri tempColFlt = new CustomColonneFiltri();
         tempColFlt.setEqual(colonneFiltri);
         if(tempColFlt.getWhere() == null || tempColFlt.getWhere().trim().length() == 0)
            return null;

         /*
           if (tempColFlt.getFrom() != null && tempColFlt.getFrom().trim().length() > 0) {
        tempColFlt.setFrom(tempColFlt.getFrom().substring(4, tempColFlt.getFrom().length()));
             }
             if (tempColFlt.getTo() != null && tempColFlt.getTo().trim().length() > 0) {
        tempColFlt.setTo(tempColFlt.getTo().substring(4, tempColFlt.getTo().length()));
             }
          */
         if(tempColFlt.getLista().size() > 0) {
            Vector correctedList = new Vector();
            for(Iterator it = tempColFlt.getLista().iterator(); it.hasNext(); ) {
               String elem = (String)it.next();
               correctedList.add(elem.substring(4, elem.length()));
            }
            tempColFlt.setLista(correctedList);
            //tempColFlt.setListaString(constructListaString());
         }

         return tempColFlt.getWhere();
      }
      catch(Exception ex) {
         ex.printStackTrace(Trace.excStream); //...FIX 7430
      }
      return null;

   }

   /**
    * getWhere
    * @param colonneFiltri ColonneFiltri
    * @return String
    */
   protected String getWhere(ColonneFiltri colonneFiltri) {
      try {
         ColonneFiltri tempColFlt = new CustomColonneFiltri();
         tempColFlt.setEqual(colonneFiltri);
         if(tempColFlt.getWhere() == null || tempColFlt.getWhere().trim().length() == 0)
            return null;

         if(tempColFlt.getFrom() != null && tempColFlt.getFrom().trim().length() > 0) {
            tempColFlt.setFrom(tempColFlt.getFrom().substring(4, tempColFlt.getFrom().length()));
         }
         if(tempColFlt.getTo() != null && tempColFlt.getTo().trim().length() > 0) {
            tempColFlt.setTo(tempColFlt.getTo().substring(4, tempColFlt.getTo().length()));
         }
         if(tempColFlt.getLista().size() > 0) {
            Vector correctedList = new Vector();
            for(Iterator it = tempColFlt.getLista().iterator(); it.hasNext(); ) {
               String elem = (String)it.next();
               correctedList.add(elem.substring(4, elem.length()));
            }
            tempColFlt.setLista(correctedList);
            //tempColFlt.setListaString(constructListaString());
         }

         return tempColFlt.getWhere();
      }
      catch(Exception ex) {
         ex.printStackTrace(Trace.excStream); //...FIX 7430
      }
      return null;
   }

   /**
    * initializeUserParameters
    */
   protected void initializeUserParameters() {
      try {
         String parmStatoCommessa = getDescAttrRef("StatoCommessaRpt", iStatoCommessa);
         String parmTipoStampa = getDescAttrRef("TipoStampaConsCmm", iTipoStampa);
         String parmArticoliRisorse = getDescAttrRef("ArticoliRisorse", iArticoliRisorse);
         String parmDataSaldoInizio = iDataSaldoIniziale != null ? new DateType().objectToString(iDataSaldoIniziale) : "";
         String parmDataSaldoFine = iDataSaldoFinale != null ? new DateType().objectToString(iDataSaldoFinale) : "";
         String parmTipoDettaglio = getDescAttrRef("TipoDettaglio", iTipoDettaglio);
         String parmTipoRiga = getDescAttrRef("TipoRigaCmmRpt", iTipoRiga);
         String parmTipoRisorsa = getDescAttrRef("TipoRisorsaNonSignific", iTipoRisorsa);
         String parmLivelloRisorsa = getDescAttrRef("LivelloRisorsa", iLivelloRisorsa);

         addUserParameter("StatoCommessaDesc", parmStatoCommessa);
         addUserParameter("TipoStampaDesc", parmTipoStampa);
         addUserParameter("ArticoliRisorseDesc", parmArticoliRisorse);
         addUserParameter("DataSaldoInizio", parmDataSaldoInizio);
         addUserParameter("DataSaldoFine", parmDataSaldoFine);
         addUserParameter("TipoDettaglioDesc", parmTipoDettaglio);
         addUserParameter("TipoRigaDesc", parmTipoRiga);
         addUserParameter("TipoRisorsaDesc", parmTipoRisorsa);
         addUserParameter("LivelloRisorsaDesc", parmLivelloRisorsa);
         //...FIX 7430 inizio
         //addUserParameter("FiltriStp", constructParmFiltri(iCondizioneFiltri));
         addUserParameter("FiltriStp", iCondizioneFiltri.getValoriFiltroString());
         //...FIX 7430 fine
      }
      catch(Exception ex) {
         ex.printStackTrace(Trace.excStream); //...FIX 7430
      }
   }

   /**
    * initializeUserEnums
    */
   protected void initializeUserEnums() {
      String tableName = removeSchemaName(ReportConsuntivoCommessaDettaglioTM.TABLE_NAME);
      String testataTableName = removeSchemaName(ReportConsuntivoCommessaTestataTM.TABLE_NAME);
      addEnum("TipoRisorsaStp", tableName + "." + ReportConsuntivoCommessaDettaglioTM.R_TIPO_RISORSA, "TipoRisorsaNonSignific");
      addEnum("LivelloRisorsaStp", tableName + "." + ReportConsuntivoCommessaDettaglioTM.R_LIVELLO_RISORSA, "LivelloRisorsa");
      addEnum("TipoRilevazione", tableName + "." + ReportConsuntivoCommessaDettaglioTM.TIPO_RILEV_RSR, "TipoRilevazione");
      addEnum("TipoRigaOrigine", tableName + "." + ReportConsuntivoCommessaDettaglioTM.TIPO_RIGA_ORIGINE, "TipoRigaOrigine");
      addEnum("StatoAvanzamento", testataTableName + "." + ReportConsuntivoCommessaTestataTM.STATO_AVANZAMENTO, "StatoAvanzCmm");
   }

   /**
    * constructParmFiltri
    * @param condizioneFiltri CondizioniFiltri
    * @return String
    */
   /*protected static String constructParmFiltri(CondizioniFiltri condizioneFiltri) {
    String parmFiltri = "";
    ArrayList a = condizioneFiltri.getVettoreValoriFiltro();

    for(int i = 0; i < a.size(); i++) {
      ArrayList al = (ArrayList)a.get(i);
      String name = (String)al.get(1);
      String from = (String)al.get(2);
      String to = (String)al.get(3);
      String lista = (String)al.get(4);
      if(from != null || to != null || lista != null)
        parmFiltri += name + ":";
      if(from != null)
        parmFiltri += " " + ResourceLoader.getString(RESOURCE_FILE, "CP_DA") + " " + from;
      if(to != null)
        parmFiltri += " " + ResourceLoader.getString(RESOURCE_FILE, "CP_A") + " " + to;
      if(lista != null)
        parmFiltri += "\n [" + ResourceLoader.getString(RESOURCE_FILE, "CP_Lista") + ": " + convertLista(lista) + "]";
      if(from != null || to != null || lista != null)
        parmFiltri += "\n";
    }

    return parmFiltri;
     }*/

   /**
    *
    * @param lista String
    * @return String
    */
   /*protected static String convertLista(String lista) {
    String result = "";
    StringTokenizer st = new StringTokenizer(lista, ColonneFiltri.LISTA_SEP);
    while(st.hasMoreTokens()) {
      StringTokenizer stt = new StringTokenizer(st.nextToken(), ColonneFiltri.COD_DESC_SEP);
      if(result.length() == 0)
        result += stt.nextToken();
      else
        result += " - " + stt.nextToken();
    }
    return result;
     }*/

   /**
    * removeSchemaName
    * @param name String
    * @return String
    */
   public static String removeSchemaName(String name) {
      int pos = name.indexOf(".");
      if(pos > 0)
         name = name.substring(pos + 1);
      return name;
   }

   /**
    * getDescAttrRef
    * @param IdRef String
    * @param value char
    * @return String
    */
   public static String getDescAttrRef(String IdRef, char value) {
      EnumType eType = new EnumType(IdRef);
      return eType.descriptionFromValue(String.valueOf(value));
   }

   /**
    *
    * @return boolean
    */
   /*public boolean run() {
    return super.run();
     }*/


   /**
    * CustomColonneFiltri
    * <br></br><b>Copyright (C) : Thera s.p.a.</b>
    * @author Aissa BOULILA 08/06/2005
    */
   static class CustomColonneFiltri extends ColonneFiltri {

      /**
       * setEqual
       * @param obj Copyable
       * @throws CopyException
       */
      public void setEqual(Copyable obj) throws CopyException {
         CopyableHelper.copyObject(this, obj);
      }

      /**
       * getColumnName
       * @return String
       */
      public String getColumnName() {
         if(getClassHdrName().equals("StampaConsuntivoCommessa") && getClassAdName().equals("IdRisorsa"))
            return StoricoCommessaTM.TABLE_NAME + "." + StoricoCommessaTM.R_RISORSA;
         else
            return super.getColumnName();
      }

      /**
       * creaCustomColonnaFiltro
       * @param classHdrName String
       * @param classAdName String
       * @param abilitaLista boolean
       * @return CustomColonneFiltri
       */
      public static CustomColonneFiltri creaCustomColonnaFiltro(String classHdrName, String classAdName, boolean abilitaLista) {
         CustomColonneFiltri colonna = new CustomColonneFiltri();
         colonna.setAziendaCorrente(Azienda.getAziendaCorrente());
         colonna.setClassHdrName(classHdrName);
         colonna.setClassAdName(classAdName);
         colonna.setAbilitaLista(abilitaLista);
         return colonna;
      }

      /**
       * creaCustomColonnaFiltro
       * @param classHdrName String
       * @param classAdName String
       * @param relatedClassName String
       * @param abilitaLista boolean
       * @return CustomColonneFiltri
       */
      public static CustomColonneFiltri creaCustomColonnaFiltro(String classHdrName, String classAdName, String relatedClassName, boolean abilitaLista) {
         CustomColonneFiltri colonna = new CustomColonneFiltri();
         colonna.setAziendaCorrente(Azienda.getAziendaCorrente());
         colonna.setClassHdrName(classHdrName);
         colonna.setClassAdName(classAdName);
         colonna.setRelazione(true);
         colonna.setRelatedClassName(relatedClassName);
         colonna.setAbilitaLista(abilitaLista);
         return colonna;
      }

      /**
       * creaCustomColonnaFiltro
       * @param classHdrName String
       * @param classAdName String
       * @param relatedClassName String
       * @param metodo String
       * @param abilitaLista boolean
       * @return CustomColonneFiltri
       */
      public static CustomColonneFiltri creaCustomColonnaFiltro(String classHdrName, String classAdName, String relatedClassName, String metodo,
            boolean abilitaLista) {
         CustomColonneFiltri colonna = new CustomColonneFiltri();
         colonna.setAziendaCorrente(Azienda.getAziendaCorrente());
         colonna.setClassHdrName(classHdrName);
         colonna.setClassAdName(classAdName);
         colonna.setRelazione(true);
         colonna.setRelatedClassName(relatedClassName);
         colonna.setMetodoDescrizione(metodo);
         colonna.setAbilitaLista(abilitaLista);
         return colonna;
      }

      /**
       * getClassADNLSDesc
       * @return String
       */
      public String getClassADNLSDesc() {

         try {
            if(getClassHdrName() == null || getClassHdrName().equals("") ||
                  getClassAdName() == null || getClassAdName().equals(""))
               return "";
            ClassAD cad = getClassAd();

            if(getClassHdrName().equals("StoricoCommessa") && getClassAdName().equals("IdArticoloPrd")) {
               return ResourceLoader.getString(RESOURCE_FILE, "ArticoloPrd");
            }

            else
               return cad.getAttributeNameNLS();
         }
         catch(Exception e) {
            e.printStackTrace(Trace.excStream);
            return "";
         }
      }

      /**
       * scomponiListaString
       */
      public void scomponiListaString() {
         if(getClassHdrName().equals("StampaConsuntivoCommessa") && getClassAdName().equals("IdRisorsa")) {
            iLista.clear();
            if(listaString != null && !listaString.equals("")) {
               StringTokenizer strTok = new StringTokenizer(listaString, LISTA_SEP);
               while(strTok.hasMoreTokens()) {
                  String idDesc = strTok.nextToken();
                  StringTokenizer iddescTok = new StringTokenizer(idDesc, COD_DESC_SEP);
                  int i = 0;
                  while(iddescTok.hasMoreTokens()) {
                     if(i == 0) {
                        String id = iddescTok.nextToken();
                        iLista.add(id.substring(4, id.length()));
                     }
                     else
                        iddescTok.nextToken();
                     i++;
                  }
               }
            }
         }
         else
            super.scomponiListaString();
      }
   }

   // 10222 inizio
   /**
    * CompactStoricoCommessa
    * <br></br><b>Copyright (C) : Thera s.p.a.</b>
    * @author Aissa BOULILA 08/06/2005
    */
   protected class CompactStoricoCommessa {

      protected String iIdCommessa;
      protected char iTipoRisorsa;
      protected char iLivelloRisorsa;
      protected String iIdRisorsa;
      protected String iIdArticolo;
      protected Integer iIdVersione;
      protected String iIdEsternoConfig;
      protected java.sql.Date iDataOrigine;

      /**
       * CompactStoricoCommessa
       * @param idCommessa String
       * @param tipoRisorsa char
       * @param livelloRisorsa char
       * @param idRisorsa String
       * @param idArticolo String
       * @param idVersione Integer
       * @param idEsternoConfig String
       * @param dataOrigine Date
       */
      public CompactStoricoCommessa(String idCommessa, char tipoRisorsa, char livelloRisorsa, String idRisorsa, String idArticolo, Integer idVersione,
            String idEsternoConfig, java.sql.Date dataOrigine) {
         iIdCommessa = idCommessa;
         iTipoRisorsa = tipoRisorsa;
         iLivelloRisorsa = livelloRisorsa;
         iIdRisorsa = idRisorsa;
         iIdArticolo = idArticolo;
         iIdVersione = idVersione;
         iIdEsternoConfig = idEsternoConfig;
         iDataOrigine = dataOrigine;
      }

      /**
       * CompactStoricoCommessa
       * @param storicoCommessa StoricoCommessa
       */
      public CompactStoricoCommessa(StoricoCommessa storicoCommessa) {
         this(storicoCommessa.getIdCommessa(),
               storicoCommessa.getTipoRisorsa(),
               storicoCommessa.getLivelloRisorsa(),
               storicoCommessa.getIdRisorsa(),
               storicoCommessa.getIdArticolo(),
               storicoCommessa.getIdVersione(),
               storicoCommessa.getIdEsternoConfig(),
               storicoCommessa.getDataOrigine());
      }

      /**
       * rottura
       * @param compactStoricoCommessa CompactStoricoCommessa
       * @return boolean
       */
      public boolean rottura(CompactStoricoCommessa compactStoricoCommessa) {
         return iIdCommessa.equals(compactStoricoCommessa)
               || iTipoRisorsa != compactStoricoCommessa.iTipoRisorsa
               || iLivelloRisorsa != compactStoricoCommessa.iLivelloRisorsa
               || !areEqual(iIdRisorsa, compactStoricoCommessa.iIdRisorsa)
               || !areEqual(iIdArticolo, compactStoricoCommessa.iIdArticolo)
               || !areEqual(iIdVersione, compactStoricoCommessa.iIdVersione)
               || !areEqual(iIdEsternoConfig, compactStoricoCommessa.iIdEsternoConfig);
      }
   }

   protected CompactStoricoCommessa createCompactStoricoCommessa(StoricoCommessa sc) {
      return new CompactStoricoCommessa(sc);
   }
   // 10222 fine

   /**
    * areEqual
    * @param obj1 Object
    * @param obj2 Object
    * @return boolean
    */
   protected boolean areEqual(Object obj1, Object obj2) {
      if(obj1 == null && obj2 == null)
         return true;
      if(obj1 == null || obj2 == null)
         return false;
      return obj1.equals(obj2);
   }

   /**
    * initCondizioniFiltroForBatch
    */
   protected void initCondizioniFiltroForBatch() {
      //Fix 9786 inizio
      iCondizioneFiltri.impostaColonneConVettoreValoriFiltro(); 
      /*
    ArrayList filterValues = iCondizioneFiltri.getVettoreValoriFiltro();
    for(int i = 0; i < iCondizioneFiltri.getColonneFiltro().size(); i++) {
      ColonneFiltri colFlt = (ColonneFiltri)iCondizioneFiltri.getColonneFiltro().get(i);
      //...FIX 7430 inizio
      ArrayList valori = (ArrayList)filterValues.get(i);
      colFlt.setFrom((String)valori.get(2));
      colFlt.setTo((String)valori.get(3));
      colFlt.setListaString((String)valori.get(4));
      //...Faccio la try/catch di queste due righe di codice perchè
      //...potrebbero esserci dei lavori schedulati che non contengono
      //...i valori per le esclusioni, e quindi la get potrebbe andare in
      //...eccezione perchè l'Array contiene solo 5 valori (e non 7)
      try {
        colFlt.setRangeEsclusione(valori.get(5).equals("true") ? true : false);
        colFlt.setListaEsclusione(valori.get(6).equals("true") ? true : false);
      }
      catch(Exception e) {}
      //...FIX 7430 fine
    }
       */
      //Fix 9786 fine
   }

   /**
    * buildListString
    * @param compactCommesse List
    * @return String
    */
   public String buildListString(List compactCommesse) {
      StringBuffer listString = new StringBuffer();
      for(Iterator it = compactCommesse.iterator(); it.hasNext(); ) {
         CompactCommessa compactCommessa = (CompactCommessa)it.next();
         listString.append(compactCommessa.getIdCommessa())
         .append(ColonneFiltri.COD_DESC_SEP)
         .append("")
         .append(ColonneFiltri.LISTA_SEP);
      }
      return listString.toString();
   }

   // Fix 04171 Mz inizio

   /**
    * createCommessaComparator
    * @return Comparator
    */
   public Comparator createCommessaComparator() {
      return new CommessaComparator();
   }

   /**
    * CommessaComparator
    * <br></br><b>Copyright (C) : Thera s.p.a.</b>
    * @author Mz
    */
   public static class CommessaComparator implements Comparator {
      public int compare(Commessa commessa1, Commessa commessa2) {
         return Utils.compare(getIdElement(commessa1), getIdElement(commessa2));
      }

      /**
       * getIdElement
       * @param commessa Commessa
       * @return String
       */
      public String getIdElement(Commessa commessa) {
         //return getIdAmbienteCommessa(commessa) + "/" + getPath(commessa);
         return getIdAmbienteCommessa(commessa) + "/" + getIdCommessaPrm(commessa) + "/" + getLivello(commessa) + "/" + getIdCommessa(commessa);
      }

      /**
       * getIdAmbienteCommessa
       * @param commessa Commessa
       * @return String
       */
      public String getIdAmbienteCommessa(Commessa commessa) {
         return commessa == null ? "-" : commessa.getIdAmbienteCommessa();
      }

      /**
       * getIdCommessaPrm
       * @param commessa Commessa
       * @return String
       */
      public String getIdCommessaPrm(Commessa commessa) {
         String idCommessaPrm = commessa.getIdCommessaPrincipale();
         return(idCommessaPrm == null) ? "-" : idCommessaPrm;
      }

      /**
       * getIdCommessa
       * @param commessa Commessa
       * @return String
       */
      public String getIdCommessa(Commessa commessa) {
         String idCommessa = commessa.getIdCommessa();
         return(idCommessa == null) ? "-" : idCommessa;
      }

      /**
       * getLivello
       * @param commessa Commessa
       * @return String
       */
      public String getLivello(Commessa commessa) {
         Integer livello = commessa.getLivelloCommessa();
         return(livello == null) ? "0" : livello.toString();
      }

      /**
       * getPath
       * @param commessa Commessa
       * @return String
       */
      public String getPath(Commessa commessa) {
         return(commessa == null) ? "-" : getPath(commessa.getCommessaAppartenenza()) + "/" + commessa.getIdCommessa();
      }

      /**
       * compare
       * @param o1 Object
       * @param o2 Object
       * @return int
       */
      public int compare(Object o1, Object o2) {
         return compare((Commessa)o1, (Commessa)o2);
      }
   }

   // Fix 04171 Mz fine

   /**
    * setCallFromConsuntivazione
    * @param callFromConsuntivazione boolean
    */
   public void setCallFromConsuntivazione(boolean callFromConsuntivazione) {
      iCallFromConsuntivazione = callFromConsuntivazione;
   }

   //31513 inizio
   public boolean isCommesseProvvisorie() {
      return iCommesseProvvisorie;
   }

   public void setCommesseProvvisorie(boolean commesseProvvisorie) {
      this.iCommesseProvvisorie = commesseProvvisorie;
   }
   //31513 fine

   //36252 inizio
   public ErrorMessage checkIdConsuntivo() {
      if(isUsaConsuntiviStoricizzati()) {
         if(getIdConsuntivo() == null) {
            if( getIdCommessa() == null && getDataRiferimento() == null) {
               return new ErrorMessage("THIP_TN890");	//Deve valorizzare Commessa e Consuntivo o Data riferimento per usa consuntivi storicizzati.	
            }
            else if(getIdCommessa() != null && getDataRiferimento() == null) {
               return new ErrorMessage("THIP_TN890");
            }  
         }		  
      }	  
      return null;
   }

   public void initializeConsuntivoCommessa() {
      if(getConsuntivoCommessa() != null) {
         iCurrentConsuntivoCommessa = getConsuntivoCommessa();
      }
      else if(getDataRiferimento()!= null && getCommessa() != null) {
         ConsuntivoCommessa consuntivo = cercaConsuntivoCommessa();
         if(consuntivo != null)
            iCurrentConsuntivoCommessa = consuntivo;
      }
   }

   public ConsuntivoCommessa cercaConsuntivoCommessa() {
      ConsuntivoCommessa consuntivo = null;
      String where = ConsuntivoCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + 
            ConsuntivoCommessaTM.R_COMMESSA + "='" + getIdCommessa() + "' AND " +
            ConsuntivoCommessaTM.DATA_RIFERENTO + " <= " + ConnectionManager.getCurrentDatabase().getLiteral(getDataRiferimento());
      String orderBy = ConsuntivoCommessaTM.DATA_RIFERENTO  + " DESC ";
      PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, orderBy, PersistentObject.NO_LOCK);
      try {
         if(cursor.hasNext()) {
            consuntivo = (ConsuntivoCommessa)cursor.next();
         }
      } 
      catch (SQLException e) {
         e.printStackTrace(Trace.excStream);
      }
      return consuntivo;
   }

   public String buildWhereStoriciCommesse(ConsuntivoCommessa consuntivo) {
      String where = StoricoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
            " AND " + StoricoCommessaTM.R_COMMESSA + " = '" + consuntivo.getIdCommessa() + "'" ;
      where += " AND " + StoricoCommessaTM.DATA_ORG + " <= " + ConnectionManager.getCurrentDatabase().getLiteral(consuntivo.getDataRiferimento());
      boolean ordini = consuntivo.isEstrazioneOrdini();
      where +=  " AND " + StoricoCommessaTM.DOC_ORIGINE + " IN ( '" + StoricoCommessa.DOCUMENTO + "'";
      if(consuntivo.isEstrazioneRichieste())
         where += " , '" + StoricoCommessa.RICHIESTA + "'";
      if(consuntivo.isEstrazioneOrdini())
         where += " , '" + StoricoCommessa.ORDINE + "'";
      where += ") ";
      where += " AND " + StoricoCommessaTM.VALORIZZA_COSTO + " <> '" + StoricoCommessa.NO + "'";
      return where;
   }

   public int createRiepiloghiCostiConsuntivi(Commessa commessa) throws Exception {
      int ret = 0;
      char tipoCostoConsuntivo = CostiCommessa.COSTO_CONSUNT_DEFIN;
      if(iCurrentConsuntivoCommessa.getStatoAvanzamento() == ConsuntivoCommessa.PROVVISORIO)
         tipoCostoConsuntivo = CostiCommessa.COSTO_CONSUNT_PROVV;

      List<CostiCommessaDet> costiCmmDetList = buildCostiCommessaDetdaConsuntivo(commessa); 
      for(Iterator iter = costiCmmDetList.iterator(); iter.hasNext(); ) {
         CostiCommessaDet costiCmmDet = (CostiCommessaDet)iter.next();
         ReportConsuntivoCommessaRiepilogo reportConsuntivoCommessaRiepilogo = getSimilarRigaRpt(costiCmmDet.getIdCommessa(), costiCmmDet.getIdComponCosto());
         if(reportConsuntivoCommessaRiepilogo == null) {
            reportConsuntivoCommessaRiepilogo = (ReportConsuntivoCommessaRiepilogo)Factory.createObject(ReportConsuntivoCommessaRiepilogo.class);
            reportConsuntivoCommessaRiepilogo.valorizzaAttributi(costiCmmDet, tipoCostoConsuntivo);
            reportConsuntivoCommessaRiepilogo.setBatchJobId(iBatchJobId);
            reportConsuntivoCommessaRiepilogo.setReportNr(iReportNr);
            reportConsuntivoCommessaRiepilogo.setRigaJobId(iRigaJobId);
            reportConsuntivoCommessaRiepilogo.setDetRigaJob(iDetRigaJobRiep++);
            int ret1 = reportConsuntivoCommessaRiepilogo.save();
            if(ret1 < 0)
               return ret1;
            ret += ret1;
         }  
      }	  
      return ret;
   }

   public List<CostiCommessaDet> buildCostiCommessaDetdaConsuntivo(Commessa commessa) throws Exception{
      List<CostiCommessaDet> costiCmmDetList = new ArrayList<CostiCommessaDet>();
      String where =  ConsuntivoCommessaDetTM.ID_AZIENDA + "='" + iCurrentConsuntivoCommessa.getIdAzienda() + "' AND " + 
            ConsuntivoCommessaDetTM.ID_CONSUNTIVO + "=" + iCurrentConsuntivoCommessa.getIdConsuntivo() + " AND " + 
            ConsuntivoCommessaDetTM.R_COMMESSA + "='" + commessa.getIdCommessa() + "'" ;

      List consuntiviDet = ConsuntivoCommessaDet.retrieveList(where, "", false);
      if(consuntiviDet != null && !consuntiviDet.isEmpty()) {
         for(Iterator iter = consuntiviDet.iterator(); iter.hasNext(); ) {
            ConsuntivoCommessaDet consCmmDet = (ConsuntivoCommessaDet)iter.next();
            CostiCommessaDet costiCmmDet = (CostiCommessaDet) Factory.createObject(CostiCommessaDet.class);
            costiCmmDet.setIdAzienda(consCmmDet.getIdAzienda());
            costiCmmDet.setIdCommessa(consCmmDet.getIdCommessa());
            costiCmmDet.setIdComponCosto(consCmmDet.getIdComponCosto());
            costiCmmDet.setCostoTotale(consCmmDet.getTotale().getCostoTotale());
            costiCmmDet.setCostoLivello(consCmmDet.getTotale().getCostoLivello());
            costiCmmDet.setCostoLivelliInf(consCmmDet.getTotale().getCostoLivelloInf());


            costiCmmDetList.add(costiCmmDet);
         }
      }
      return costiCmmDetList;
   }
   //36252 fine
}

package it.thera.thip.produzione.commessa;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.thera.thermfw.ad.*;
import com.thera.thermfw.base.*;
import com.thera.thermfw.batch.*;
import com.thera.thermfw.collector.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.type.*;

import it.thera.thip.base.articolo.TipoCosto;//30049
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.costi.*;
import com.thera.thermfw.security.Security;
import it.thera.thip.base.profilo.UtenteAzienda;
import it.thera.thip.base.profilo.ThipUser;
import it.thera.thip.base.profilo.UtenteAmbienti;

/**
 * ConsuntivazioneCommesse
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 23/05/2005 at 11:20:32
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 04599   11/11/2005    Jed      add user param DataRif
 * 04810   10/01/2006    LP       Modificato default del booleano iSimulazione
 * 07430   09/07/2007    LP       Correzioni varie
 * 09786   17/09/2008    DBot     Aggiunta la gestione dei "null" nelle condizioni di filtro
 * 10151   26/11/2008    ME       Modificato metodo initializeUserEnums
 * 10411   28/01/2009    DBot     Modifica per ottimizzazione performance e per logging, e anomalie
 * 10537   30/03/2009    DBot     Riallineamento per fix WEB
 * 10913   26/05/2009    DB       Per permettere la personalizzazione
 * 13743   26/01/2011    RA       Controllo di associazione Utente/AmbienteCosti per tutte funzioni di commessa
 * 22268   01/10/2015    OCH      Recupera il valore MAX_ANOMALIE da file ConsuntivazioneCommesse.properties
 * 24035   09/09/2016    TF       Rimuovere alcune Filtri dell' attributo parameter del batch job
 * 27486   25/05/2018    DBot     Reperimento costi risorsa da documento
 * 29112   12/04/2019	 TJ		  Compilare il StampaRiepilogoCommesse e StampaConsuntivoCommessa con l'azienda vero
 * 29960   09/10/2019    RA		  Consuntivazione in modalità definitiva eseguibile per singola commessa/ambiente commessa
 * 30049   22/10/2019    RA		  Semplificazione del calcolo costi 
 * 31460   01/07/2020	 RA		  Aggiunto nuovo attributi: CommesseProvvisorie,EstrazioneOrdini,CostiArticoloDaDocumento, OrdineRecArticolo, OrdineRecRisorsa
 * 31504   24/07/2020    RA		  Aggiunto EstrazioneRDA
 * 31513   04/09/2020    RA		  Aggiunto StoriciNonCommessa
 * 33593   20/05/2021    Mekki    Correzione vari
 * 33950   11/08/2021    RA		  Agguiunto attributo EstrarrePeriodiDefinitivi e EstrazioneStoriciCommessa
 */

public class ConsuntivazioneCommesse extends ElaboratePrintRunnable {

  //public static final int MAX_ANOMALIE = 5000; //Fix 10411  // Fix 22268
  public static Boolean cvConsuntivazioneInCorso = new Boolean(false);

  public static final String CLASS_HDR_NAME = "ConsuntivazioneCommesse";
  public static final String RESOURCE_NAME = "it.thera.thip.produzione.commessa.resources.ConsuntivazioneCommesse";
  public static final String GESTORE_RES_NAME = "ConsuntivazioneCommessa";

  public static final int MAX_ANOMALIE = new Integer(ResourceBundle.getBundle(RESOURCE_NAME).getString("MaxAnomalie")).intValue();  // Fix 22268
  
  protected boolean iDataRifNull = false;

  // Tipo consuntivazione
  public static final char TIPO_CONSUN__PROVVISORIA = '0';
  public static final char TIPO_CONSUN__DEFINITIVA = '1';
  
  //Fix 24035 inizio
	public static final char DELIM = '=';
 	public static final String SEPARATOR = String.valueOf((char) 18);
  //Fix 24035 fine

  //For reports
  protected int iBatchJobId = 0;
  protected int iAnomalieReportNr = 0;
  protected int iDocReportNr = 0;
  protected int iStampaConsReportNr = 0;
  protected int iRiepilogoReportNr = 0;

  protected int iRigaJobId = 0;
  protected int iDetRigaJob = 0;
  protected AvailableReport iAnmAvailableRep;
  protected AvailableReport iDocAvailableRep;
  protected AvailableReport iConsuntivoAvailableRep;
  protected AvailableReport iRiepilogoAvailableRep;

  protected ConnectionDescriptor iAnomaliesConnectionDescriptor = null;

  protected StampaConsuntivoCommessa iStampaConsuntivoCommessa = (StampaConsuntivoCommessa)Factory.createObject(StampaConsuntivoCommessa.class);
  protected StampaRiepilogoCommesse iStampaRiepilogoCommesse = (StampaRiepilogoCommesse)Factory.createObject(StampaRiepilogoCommesse.class);

  /**
   * List of commesse to be processed
   */
  protected Vector iCompactCommesse = new Vector();

  /**
   * List of ambiente to be processed
   */
  protected List iAmbienti = new ArrayList();

  /**
   * Attributo iDataUltimaConsun
   */
  protected java.sql.Date iDataUltimaConsun;

  /**
   * Attributo iTipoConsuntivazione
   */
  protected char iTipoConsuntivazione = AmbienteCommessa.TIPO_ULTIMA_CONSUN__PROVVISORIA;

  /**
   * Attributo iDataRiferimento
   */
  protected java.sql.Date iDataRiferimento;

  /**
   * Attributo iGenFatture
   */
  protected boolean iGenFatture = true;

  /**
   * Attributo iSimulazione
   */
  protected boolean iSimulazione = false; //...FIX 4810

  /**
   * Attributo iStampaConsuntivo
   */
  protected boolean iStampaConsuntivo = false;

  /**
   * Attributo iStampaRiepilogo
   */
  protected boolean iStampaRiepilogo = false;

  /**
   * Attributo iStampaDiretta
   */
  protected boolean iStampaDiretta = false;

  /**
   * Attributo iFiltroAmbientiCommesse
   */
  protected CondizioniFiltri iFiltroAmbientiCommesse;

  /**
   * Attributo iAzienda
   */
  protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);

  /**
   * Attributo iAmbienteCostiMancanti
   */
  protected Proxy iAmbienteCostiMancanti = new Proxy(it.thera.thip.base.generale.AmbienteCosti.class);

  /**
   * Attributo iDataCorrente
   */
  protected java.sql.Date iDataCorrente;

  /**
   * Data ultima chiusura definitiva del ambiente
   */
  //protected java.sql.Date iDataUltChiusDefAmbiente;//29960
  protected HashMap iMapDataUltChiusDefAmbiente = new HashMap();

  /**
   * Precise if ordine must be processed or no
   */
  protected boolean iProcessOrdine = false;

  /**
   * iIdProgrStorico
   */
  protected Integer iIdProgrStorico;

  // Attributes for StampaConsuntivoCommessa batch

  /**
   *
   */
  protected char iStatoCommessa = StampaConsuntivoCommessa.STATO_COMMESSA__IN_CORSO;

  /**
   *
   */
  protected char iTipoStampa = StampaConsuntivoCommessa.TIPO_STAMPA__COMPLETA;

  /**
   *
   */
  protected char iTipoDettaglio = StampaConsuntivoCommessa.TIPO_DETTAGLIO__CON_DET_MOV;

  /**
   *
   */
  protected char iArticoliRisorse = StampaConsuntivoCommessa.ARTICOLI_RISORSE__ENTRAMBI;

  /**
   *
   */
  protected java.sql.Date iDataSaldoIniziale;

  /**
   *
   */
  protected java.sql.Date iDataSaldoFinale;

  /**
   *
   */
  protected char iTipoRiga = StampaConsuntivoCommessa.TIPO_RIGA__TUTTE;

  /**
   *
   */
  protected char iTipoRisorsa = Risorsa.NON_SIGNIFICATIVO;

  /**
   *
   */
  protected char iLivelloRisorsa = Risorsa.NON_SIGNIFICATIVO;

  /**
   *
   */
  protected String iIdRisorsa;

  protected boolean iCostiRisorsaDaDocumento = false; //Fix 27486
  
  //31460 inizio
  //OrdineRecupero
  public static final char ORDINE_RECUPERO_DOC_AMB_TIPO = '0';
  public static final char ORDINE_RECUPERO_AMB_TIPO_DOC = '1';
  
  protected boolean iCommesseProvvisorie = false;
  protected boolean iEstrazioneOrdini = false;
  protected boolean iCostiArticoloDaDocumento = false;
  protected char iOrdineRecArticolo = ORDINE_RECUPERO_DOC_AMB_TIPO;
  protected char iOrdineRecRisorsa = ORDINE_RECUPERO_AMB_TIPO_DOC;
  //31460 fine
  protected boolean iEstrazioneRDA = false;//31504
  protected boolean iStoriciNonCommessa = false;//31513
  
  //33950 inizio
  protected boolean iEstrarrePeriodiDefinitivi = false;
  protected boolean iEstrazioneStoriciCommessa = false;
  //33950 fine
  
/**
   *
   */
  protected CondizioniFiltri iFiltriStp;

  /**
   *
   */
  protected CondizioniFiltri iFiltriRiepilogo;

  /*******************************************************/

  /**
   * iGestoreCommit
   */
  protected GestoreCommit iGestoreCommit;

  protected ComLogger iLogger = null; //Fix 10411

  //Cache applicative delle componenti di costo
  protected List iListaComponentiCosto = null; //Fix 10411
  protected Map iMapComponentiCosto = new HashMap(); //Fix 10411

  //Cache applicativa delle VariabiliCosti
  protected VariabiliCosti iVariabiliCostiCollection = null; //Fix 10411

  protected int iNumeroAnomalie = 0; //Fix 10411
  
  //Fix 33593 --inizio
  protected int iNumeroWarning = 0;
  protected int iNumeroErrori = 0;
  //Fix 33593 --fine

  /**
   * cDeleteStoricoCmm
   */
  protected static CachedStatement cDeleteStoricoCmm = new CachedStatement(
    "DELETE FROM " + StoricoCommessaTM.TABLE_NAME +
    " WHERE " + StoricoCommessaTM.ID_AZIENDA + " = ?" +
    " AND " + StoricoCommessaTM.R_COMMESSA + " = ?" +
    " AND ((" + StoricoCommessaTM.DATA_ORG + " > ?) OR (" + StoricoCommessaTM.DOC_ORIGINE + " IN (?, ?)))"
    );

  /**
   * cDeleteCostiCmm
   */
  protected static CachedStatement cDeleteCostiCmm = new CachedStatement(
    "DELETE FROM " + CostiCommessaTM.TABLE_NAME
    + " WHERE " + CostiCommessaTM.ID_AZIENDA + "= ? AND "
    + CostiCommessaTM.ID_COMMESSA + " = ? AND "
    + CostiCommessaTM.TIPOLOGIA + " = ?"
    );
  
  //30049 inizio
  public static final char MOD_AMBIENTE_COSTI = 'A';
  public static final char MOD_ARTICOLO_COSTI = 'C';
  protected char iModalitaCostiMancanti = MOD_AMBIENTE_COSTI;
  protected Proxy iTipoCostoMancanti = new Proxy(it.thera.thip.base.articolo.TipoCosto.class);
  //30049 fine

  /**
   * Constructor
   */
  public ConsuntivazioneCommesse() {
    setIdAzienda(Azienda.getAziendaCorrente());
    try {
      iGestoreCommit = new GestoreCommit(GESTORE_RES_NAME);
      initFiltriStp();
      initFiltriRiepilogo();
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    initDataUtlimaConsunDef();
    initFiltroAmbientiCommesse();

  }

  /**
   * Override of createReport method
   * @return boolean
   */
  public boolean createReport() {
    synchronized(cvConsuntivazioneInCorso) {
       //Fix 10411 inizio
       boolean ret = createReportInternal();
       //Fix 33593 --inizio
       int iNumeroInfo = iNumeroAnomalie - iNumeroErrori - iNumeroWarning;
       if(iNumeroAnomalie > 0)
       {
          output.println(ResourceLoader.getString(RESOURCE_NAME, "MsgRisNoOK"));
          if(iNumeroErrori > 0)
          {
             output.println(iNumeroErrori + " " + ResourceLoader.getString(RESOURCE_NAME, "MsgRisERROR"));
             ret = false;
          }
          else
             ret = true;
          
          if(iNumeroWarning > 0)
          {
             output.println(iNumeroWarning + " " + ResourceLoader.getString(RESOURCE_NAME, "MsgRisWARNING"));
             getBatchJob().setApplStatus(BatchJob.WITH_WARNING);
          }
          if(iNumeroInfo > 0)
             output.println(iNumeroInfo + " " + ResourceLoader.getString(RESOURCE_NAME, "MsgRisINFO"));
          
          if(iNumeroErrori > 0 && getTipoConsuntivazione() == TIPO_CONSUN__DEFINITIVA)
          {
             output.println("");
             output.println(ResourceLoader.getString(RESOURCE_NAME, "MsgRisDefErrori1"));
             output.println(ResourceLoader.getString(RESOURCE_NAME, "MsgRisDefErrori2"));
          }
          return ret;
       }
       else
       {
          output.println(ResourceLoader.getString(RESOURCE_NAME, "MsgRisOK"));
          return true;
       }
       /*   

          
       if(iNumeroAnomalie > MAX_ANOMALIE)
       {
          output.println("Riscontrate " + iNumeroAnomalie + " anomalie (oltre massimo tracciabile di " + MAX_ANOMALIE + ").");
          job.setApplStatus(BatchJob.WITH_WARNING);
       }
       return ret;
       */
       //Fix 33593 --fine
       /*
       return createReportInternal();
       */
       //Fix 10411 fine
    }
  }

  /**
   * Override of createReportInternal method
   * @return boolean
   */
  public boolean createReportInternal() {
    try {
      iDataCorrente = TimeUtils.getCurrentDate();

      //Svuotamento cache VariabiliCommessaElem
      VariabiliCommessaElem.clearVariabiliCommessaHash(); //Fix 10411
      getLogger();//Fix 10411
      getLogger().printForcedMessage(">>>> INIZO CONSUNTIVAZIONE COMMESSE");

      if(iFiltroAmbientiCommesse.getCondizioneWhere() == null || iFiltroAmbientiCommesse.getCondizioneWhere().trim().length() == 0)
        initCondizioniFiltroForBatch();

      setExecutePrint(isStampaDiretta());

      if(getDataRiferimento() == null) {
        iDataRifNull = true;
        setDataRiferimento(iDataCorrente);
      }
      //31513 inizio
      if(isEstrazioneOrdini()) {
          setProcessOrdine(true);
      }
      //31513 fine 
      //31460 inizio     
      if(getDataRiferimento().compareTo(iDataCorrente) != 0){
        setProcessOrdine(false);
        setEstrazioneRDA(false);
      }
      //31460 fine
      recuperoAmbientiList();

      //Fix xxxxx Mz inizio
      //if (!checkAmbientiCongruence())
      //  return false;
      if(!isSimulazione() && !checkAmbientiCongruence())
        return false;
      //Fix xxxxx Mz fine

      if(!controlDataRiferimento())
        return false;
      job.setReportCounter((short)0);
      iAnmAvailableRep = createAnomaliaConsuntivoReport();
      iAnomalieReportNr = iAnmAvailableRep.getReportNr();

      if(isGenFatture()) {
        iDocAvailableRep = createDocumentoConsuntivoReport();
        iDocReportNr = iDocAvailableRep.getReportNr();
      }

      ConnectionManager.commit();

      getLogger().printForcedMessage(">>>> INIZO PROCESSO COMMESSE");

      if(iAmbienti.size() > 0) {
        //iDataUltChiusDefAmbiente = recupDataUltChiusDef();//29960
    	recupDataUltChiusDef();//29960
        if(!isSimulazione())
          updateAmbientiCmmToAvviato();

        getLogger().startTimeMsg("INIZIO recupero Commesse");
        recuperoCommesseList();
        getLogger().printTime("FINE recupero Commesse [" + iCompactCommesse.size() + "]");

        getLogger().startTimeMsg("INIZIO cancellazione storici");
        if(!isSimulazione())
          cancellaStroiciECosti();
        getLogger().printTime("FINE cancellazione storici");

        getLogger().startTimeMsg("INIZIO estrazione e attribuzione costi");
        boolean ret = estrazioneEAttribuzuioneCosti();
        getLogger().printTime("FINE estrazione e attribuzione costi");

        if(ret) {
          getLogger().startTimeMsg("INIZIO aggregazione costi commesse");
          aggregazioneCosti();
          getLogger().printTime("FINE aggregazione costi commesse");

          getLogger().startTimeMsg("INIZIO piano fatturazione");
          if(isGenFatture())
            valutazionePianoFatturazione();
          getLogger().printTime("FINE piano fatturazione");

          if(!isSimulazione())
            updateAmbientiStatoUltConsun(AmbienteCommessa.STATO_ULTIMA_CONSUN__COMPLETATA);

          if(isSimulazione()) {
            ConnectionManager.rollback();
          }
          else {
            commit();
          }
          if(isStampaConsuntivo() && getCompactCommesse().size() > 0) {
            getLogger().startTimeMsg("INIZIO stampa consuntivo");
            prepareStampaConsuntivoCommessa();
            eseguiStampaconsuntivoCommessa();
            getLogger().printTime("FINE stampa consuntivo");
          }
          if(!isSimulazione() && isStampaRiepilogo() && getCompactCommesse().size() > 0) {
            getLogger().startTimeMsg("INIZIO stampa riepilogo");
            prepareStampaRiepilogoCommessa();
            eseguiStampaRiepilogo();
            getLogger().printTime("FINE stampa riepilogo");
            ConnectionManager.commit();
          }
        }
        else
          return false;
      }
      getLogger().printForcedMessage(">>>> FINE CONSUNTIVAZIONE COMMESSE");
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
      return false;
    }
    finally //Fix 10411, 10437 inizio
    {
        getLogger().printMessage(">>>> FINALLY createReportInternal");
        getLogger().fineTipoDocumento();
    }//Fix 10411, 10437 fine
      return true;
  }

  /**
   *
   * @throws Exception
   */
  public void eseguiStampaconsuntivoCommessa() throws Exception {
    iConsuntivoAvailableRep = createStampaConsuntivoReport();

    iStampaConsReportNr = iConsuntivoAvailableRep.getReportNr();
    iStampaConsuntivoCommessa.iAvailableRep = iConsuntivoAvailableRep;
    iStampaConsuntivoCommessa.iBatchJobId = iBatchJobId;
    iStampaConsuntivoCommessa.iReportNr = iStampaConsReportNr;
    iStampaConsuntivoCommessa.setCallFromConsuntivazione(true); //27/09/05
    iStampaConsuntivoCommessa.setCondizioneFiltri(iFiltriStp);
    iStampaConsuntivoCommessa.setCommesseProvvisorie(iCommesseProvvisorie);//31513
    iStampaConsuntivoCommessa.processAction();
  }

  /**
   *
   * @throws Exception
   */
  public void eseguiStampaRiepilogo() throws Exception {
    iRiepilogoAvailableRep = createStampaRiepologoReport();

    iRiepilogoReportNr = iRiepilogoAvailableRep.getReportNr();
    iStampaRiepilogoCommesse.availableReport = iRiepilogoAvailableRep;
    //iStampaRiepilogoCommesse.iBatchJobId = iBatchJobId;
    iStampaRiepilogoCommesse.setBatchJob(getBatchJob());
    iStampaRiepilogoCommesse.iReportNr = iRiepilogoReportNr;
    iStampaRiepilogoCommesse.setCondFiltro(iFiltriRiepilogo);
    iStampaRiepilogoCommesse.setCallFromConsuntivazione(true); // 26/09/05
    iStampaRiepilogoCommesse.buildData();
  }

  /**
   *
   */
  protected void prepareStampaConsuntivoCommessa() {
	iStampaConsuntivoCommessa.setIdAzienda(getIdAzienda());  // Fix 29112
    iStampaConsuntivoCommessa.setTipoStampa(StampaConsuntivoCommessa.TIPO_STAMPA__COMPLETA);
    iStampaConsuntivoCommessa.setStatoCommessa(StampaConsuntivoCommessa.STATO_COMMESSA__ATTIVE);
    iStampaConsuntivoCommessa.setTipoDettaglio(StampaConsuntivoCommessa.TIPO_DETTAGLIO__SOLO_SALDO_FINALE);
    iStampaConsuntivoCommessa.setDataSaldoFinale(getDataRiferimento());

    addCommesseToFiltro(getCompactCommesse());
  }

  /**
   *
   */
  protected void prepareStampaRiepilogoCommessa() {
    iStampaRiepilogoCommesse.setIdAzienda(getIdAzienda());  // Fix 29112
	iStampaRiepilogoCommesse.setTipoStampa(StampaRiepilogoCommesse.TIPO_STAMPA_COSTI_RICAVI);
    iStampaRiepilogoCommesse.setStatoCommessa(StampaRiepilogoCommesse.STATO_COMMESSA_ATTIVE);
    iStampaRiepilogoCommesse.setTipoChiusura(getTipoConsuntivazione());
    iStampaRiepilogoCommesse.setTipologiaCostoRif(StampaRiepilogoCommesse.TIPOLOGIA_COSTO_PREVISTO);
    addCommesseToStampaRiepFiltro(getCompactCommesse());
  }

  /**
   * getColonneFiltro
   * @param filtro CondizioniFiltri
   * @param classADName String
   * @return ColonneFiltri
   */
  protected ColonneFiltri getColonneFiltro(CondizioniFiltri filtro, String classADName) {
    for(Iterator it = filtro.getColonneFiltro().iterator(); it.hasNext(); ) {
      ColonneFiltri colonneFiltri = (ColonneFiltri)it.next();
      if(colonneFiltri.getClassAdName().equals(classADName))
        return colonneFiltri;
    }
    return null;
  }

  /**
   * addCommesseToFiltro
   * @param compactCommesse List
   */
  public void addCommesseToFiltro(List compactCommesse) {
    ColonneFiltri commessaColFlt = getColonneFiltro(iFiltriStp, "IdCommessaPrincipale");
    if(commessaColFlt != null && getCompactCommesse().size() > 0) {
      commessaColFlt.setListaString(buildListString(compactCommesse));
    }
  }

  /**
   *
   * @param compactCommesse List
   */
  public void addCommesseToStampaRiepFiltro(List compactCommesse) {
    ColonneFiltri commessaColFlt = getColonneFiltro(iFiltriRiepilogo, "IdCommessa");
    if(commessaColFlt != null && getCompactCommesse().size() > 0) {
      commessaColFlt.setListaString(buildListString(compactCommesse));
    }
  }

  /**
   *
   * @param compactCommesse List
   * @return String
   */
  public String buildListString(List compactCommesse) {
    StringBuffer listString = new StringBuffer();
    for(Iterator it = compactCommesse.iterator(); it.hasNext(); ) {
      CompactCommessa compactCommessa = (CompactCommessa)it.next();
      //Fix 04171 Mz inizio
      if(compactCommessa.getIdCommessaApp() == null)
        listString.append(compactCommessa.getIdCommessa())
          .append(ColonneFiltri.COD_DESC_SEP)
          .append("")
          .append(ColonneFiltri.LISTA_SEP);
        //Fix 04171 Mz fine
    }

    return listString.toString();
  }

  /**
   *
   * @throws SQLException
   * @throws NumeratorException
   * @throws Exception
   * @return boolean
   */
  protected boolean estrazioneEAttribuzuioneCosti() throws SQLException, NumeratorException, Exception {
    //31460 inizio
	if(isEstrazioneOrdini() && getDataRiferimento().compareTo(TimeUtils.getCurrentDate()) != 0){
		String msg = ResourceLoader.getString(RESOURCE_NAME, "EstrazioneOrdineErr");
		output.print(msg);
	}
	else{
    	iIdProgrStorico = new Integer(Numerator.getNextInt(CostiCommessa.ID_PROGR_NUM_ID));
        ConsuntivazioneEstrazione consunsEtrazione = new ConsuntivazioneEstrazione(this);
        return consunsEtrazione.run();
    }
    return false;
    //31460 fine
  }

  /**
   * aggregazioneCosti
   * @throws Exception
   */
  protected void aggregazioneCosti() throws Exception {
    AggregazioneCostiCommessa aggregazioneCostiCommessa = new AggregazioneCostiCommessa(this);
    aggregazioneCostiCommessa.aggregaCostiCommessa();
    commit();
  }

  /**
   * valutazionePianoFatturazione
   * @throws Exception
   */
  protected void valutazionePianoFatturazione() throws Exception {
    for(Iterator it = iCompactCommesse.iterator(); it.hasNext(); ) {
      CompactCommessa compactCommessa = (CompactCommessa)it.next();
      //31460 inizio
      if(!isCommessaProovisoria(compactCommessa) && !isCommessaGestInterna(compactCommessa)){
    	  ValutaPianoFatturazioneCommessa valFatturazioneCmm = new ValutaPianoFatturazioneCommessa(compactCommessa, this);
    	  valFatturazioneCmm.valutaPianoFatturazione();
      }
      //31460 fine
    }

    commit();
  }

  /**
   * getClassAdCollectionName
   * @return String
   */
  protected String getClassAdCollectionName() {
    return CLASS_HDR_NAME;
  }

  /**
   * getAzienda
   * @return Azienda
   */
  public Azienda getAzienda() {
    return(Azienda)iAzienda.getObject();
  }

  /**
   * getAziendaKey
   * @return String
   */
  public String getAziendaKey() {
    return iAzienda.getKey();
  }

  /**
   * setAzienda
   * @param azienda Azienda
   */
  public void setAzienda(Azienda azienda) {
    iAzienda.setObject(azienda);
    setIdAziendaInternal(getIdAzienda());
  }

  /**
   * setAziendaKey
   * @param key String
   */
  public void setAziendaKey(String key) {
    iAzienda.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
  }

  /**
   * getIdAzienda
   * @return String
   */
  public String getIdAzienda() {
    String key = iAzienda.getKey();
    return key;
  }

  /**
   * setIdAzienda
   * @param idAzienda String
   */
  public void setIdAzienda(String idAzienda) {
    iAzienda.setKey(idAzienda);
    setIdAziendaInternal(idAzienda);
  }

  /**
   * getDataUltimaConsun
   * @return Date
   */
  public java.sql.Date getDataUltimaConsun() {
    return iDataUltimaConsun;
  }

  /**
   * setDataUltimaConsun
   * @param dataUltimaConsun Date
   */
  public void setDataUltimaConsun(java.sql.Date dataUltimaConsun) {
    iDataUltimaConsun = dataUltimaConsun;
  }

  /**
   * getDataRiferimento
   * @return Date
   */
  public java.sql.Date getDataRiferimento() {
    return iDataRiferimento;
  }

  /**
   * setDataRiferimento
   * @param dataRiferimento Date
   */
  public void setDataRiferimento(java.sql.Date dataRiferimento) {
    iDataRiferimento = dataRiferimento;
  }

  /**
   * getTipoConsuntivazione
   * @return char
   */
  public char getTipoConsuntivazione() {
    return iTipoConsuntivazione;
  }

  /**
   * setTipoConsuntivazione
   * @param tipoConsuntivazione char
   */
  public void setTipoConsuntivazione(char tipoConsuntivazione) {
    iTipoConsuntivazione = tipoConsuntivazione;
  }

  /**
   * isGenFatture
   * @return boolean
   */
  public boolean isGenFatture() {
    return iGenFatture;
  }

  /**
   * setGenFatture
   * @param genFatture boolean
   */
  public void setGenFatture(boolean genFatture) {
    iGenFatture = genFatture;
  }

  /**
   * isSimulazione
   * @return boolean
   */
  public boolean isSimulazione() {
    return iSimulazione;
  }

  /**
   * setSimulazione
   * @param simulazione boolean
   */
  public void setSimulazione(boolean simulazione) {
    iSimulazione = simulazione;
  }

  /**
   * isStampaConsuntivo
   * @return boolean
   */
  public boolean isStampaConsuntivo() {
    return iStampaConsuntivo;
  }

  /**
   * setStampaConsuntivo
   * @param stampaConsuntivo boolean
   */
  public void setStampaConsuntivo(boolean stampaConsuntivo) {
    iStampaConsuntivo = stampaConsuntivo;
  }

  /**
   * isStampaRiepilogo
   * @return boolean
   */
  public boolean isStampaRiepilogo() {
    return iStampaRiepilogo;
  }

  /**
   * setStampaRiepilogo
   * @param stampaRiepilogo boolean
   */
  public void setStampaRiepilogo(boolean stampaRiepilogo) {
    iStampaRiepilogo = stampaRiepilogo;
  }

  /**
   * isStampaDiretta
   * @return boolean
   */
  public boolean isStampaDiretta() {
    return iStampaDiretta;
  }

  /**
   * setStampaDiretta
   * @param stampaDiretta boolean
   */
  public void setStampaDiretta(boolean stampaDiretta) {
    iStampaDiretta = stampaDiretta;
  }

  /**
   * getAmbienteCostiMancanti
   * @return AmbienteCosti
   */
  public AmbienteCosti getAmbienteCostiMancanti() {
    return(AmbienteCosti)iAmbienteCostiMancanti.getObject();
  }

  /**
   * setAmbienteCostiMancanti
   * @param AmbienteCostiMancanti AmbienteCosti
   */
  public void setAmbienteCostiMancanti(AmbienteCosti AmbienteCostiMancanti) {
    iAmbienteCostiMancanti.setObject(AmbienteCostiMancanti);
  }

  /**
   * getAmbienteCostiMancantiKey
   * @return String
   */
  public String getAmbienteCostiMancantiKey() {
    return iAmbienteCostiMancanti.getKey();
  }

  /**
   * setAmbienteCostiMancantiKey
   * @param key String
   */
  public void setAmbienteCostiMancantiKey(String key) {
    iAmbienteCostiMancanti.setKey(key);
  }

  /**
   * getIdAmbienteCostiMancanti
   * @return String
   */
  public String getIdAmbienteCostiMancanti() {
    return KeyHelper.getTokenObjectKey(iAmbienteCostiMancanti.getKey(), 2);
  }

  /**
   * setIdAmbienteCostiMancanti
   * @param idAmbienteCosti String
   */
  public void setIdAmbienteCostiMancanti(String idAmbienteCosti) {
    String key = iAmbienteCostiMancanti.getKey();
    iAmbienteCostiMancanti.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idAmbienteCosti));
  }

  /**
   *
   * @return CondizioniFiltri
   */
  public CondizioniFiltri getFiltroAmbientiCommesse() {
    return iFiltroAmbientiCommesse;
  }

  /**
   * setFiltroAmbientiCommesse
   * @param filtroAmbientiCommesse CondizioniFiltri
   */
  public void setFiltroAmbientiCommesse(CondizioniFiltri filtroAmbientiCommesse) {
    iFiltroAmbientiCommesse = filtroAmbientiCommesse;
  }

  /**
   * getAmbienti
   * @return List
   */
  public List getAmbienti() {
    return iAmbienti;
  }

  /**
   * setAmbienti
   * @param ambienti List
   */
  public void setAmbienti(List ambienti) {
    iAmbienti = ambienti;
  }

  /**
   * getCommesse
   * @return List
   */
  public Vector getCompactCommesse() {
    return iCompactCommesse;
  }

  /**
   * setCommesse
   * @param commesse List
   */
  public void setCompactCommesse(Vector commesse) {
    iCompactCommesse = commesse;
  }

  /**
   * setIdAziendaInternal
   * @param idAzienda String
   */
  protected void setIdAziendaInternal(String idAzienda) {
    String key = iAmbienteCostiMancanti.getKey();
    iAmbienteCostiMancanti.setKey(KeyHelper.replaceTokenObjectKey(key, 1, idAzienda));
    //30049 inizio
    key = iTipoCostoMancanti.getKey();
    iTipoCostoMancanti.setKey(KeyHelper.replaceTokenObjectKey(key, 1, idAzienda));
    //30049 fine
  }

  /**************************************************************************/

  /**
   *
   * @return char
   */
  public char getStatoCommessa() {
    return iStatoCommessa;
  }

  /**
   *
   * @param statoCommessa char
   */
  public void setStatoCommessa(char statoCommessa) {
    iStatoCommessa = statoCommessa;
  }

  /**
   *
   * @return char
   */
  public char getTipoStampa() {
    return iTipoStampa;
  }

  /**
   *
   * @param tipoStampa char
   */
  public void setTipoStampa(char tipoStampa) {
    iTipoStampa = tipoStampa;
  }

  /**
   *
   * @return char
   */
  public char getTipoDettaglio() {
    return iTipoDettaglio;
  }

  /**
   *
   * @param tipoDettaglio char
   */
  public void setTipoDettaglio(char tipoDettaglio) {
    iTipoDettaglio = tipoDettaglio;
  }

  /**
   *
   * @return char
   */
  public char getArticoliRisorse() {
    return iArticoliRisorse;
  }

  /**
   *
   * @param articoliRisorse char
   */
  public void setArticoliRisorse(char articoliRisorse) {
    iArticoliRisorse = articoliRisorse;
  }

  /**
   *
   * @return Date
   */
  public java.sql.Date getDataSaldoIniziale() {
    return iDataSaldoIniziale;
  }

  /**
   *
   * @param dataSaldoIniziale Date
   */
  public void setDataSaldoIniziale(java.sql.Date dataSaldoIniziale) {
    iDataSaldoIniziale = dataSaldoIniziale;
  }

  /**
   *
   * @return Date
   */
  public java.sql.Date getDataSaldoFinale() {
    return iDataSaldoFinale;
  }

  /**
   *
   * @param dataSaldoFinale Date
   */
  public void setDataSaldoFinale(java.sql.Date dataSaldoFinale) {
    iDataSaldoFinale = dataSaldoFinale;
  }

  /**
   *
   * @return char
   */
  public char getTipoRiga() {
    return iTipoRiga;
  }

  /**
   *
   * @param tipoRiga char
   */
  public void setTipoRiga(char tipoRiga) {
    iTipoRiga = tipoRiga;
  }

  /**
   *
   * @return char
   */
  public char getTipoRisorsa() {
    return iTipoRisorsa;
  }

  /**
   *
   * @param tipoRisorsa char
   */
  public void setTipoRisorsa(char tipoRisorsa) {
    iTipoRisorsa = tipoRisorsa;
  }

  /**
   *
   * @return char
   */
  public char getLivelloRisorsa() {
    return iLivelloRisorsa;
  }

  /**
   *
   * @param livelloRisorsa char
   */
  public void setLivelloRisorsa(char livelloRisorsa) {
    iLivelloRisorsa = livelloRisorsa;
  }

  /**
   *
   * @return String
   */
  public String getIdRisorsa() {
    return iIdRisorsa;
  }

  /**
   *
   * @param idRisorsa String
   */
  public void setIdRisorsa(String idRisorsa) {
    iIdRisorsa = idRisorsa;
  }

  /**
   *
   * @return CondizioniFiltri
   */
  public CondizioniFiltri getFiltriStp() {
    return iFiltriStp;
  }

  /**
   *
   * @param condizioneFiltri CondizioniFiltri
   */
  public void setFiltriStp(CondizioniFiltri condizioneFiltri) {
    iFiltriStp = condizioneFiltri;
  }

  /**
   *
   * @return CondizioniFiltri
   */
  public CondizioniFiltri getFiltriRiepilogo() {
    return iFiltriRiepilogo;
  }

  /**
   *
   * @param condizioneFiltri CondizioniFiltri
   */
  public void setFiltriRiepilogo(CondizioniFiltri condizioneFiltri) {
    iFiltriRiepilogo = condizioneFiltri;
  }

  //Fix 27486 inizio
  public boolean isCostiRisorsaDaDocumento()
  {
     return iCostiRisorsaDaDocumento;
  }

  public void setCostiRisorsaDaDocumento(boolean iCostiRisorsaDaDocumento)
  {
     this.iCostiRisorsaDaDocumento = iCostiRisorsaDaDocumento;
  }
  //Fix 27486 fine
  
  
  /******************************************************************************/

  /**
   * initCondizioniFiltro
   */
  protected void initFiltroAmbientiCommesse() {
    java.util.Vector where = new java.util.Vector();
    ColonneFiltri cf1 = ColonneFiltri.creaColonnaFiltro("Commessa", "IdAmbienteCommessa", "AmbienteCommessa", true);
    where.add(cf1);
    iFiltroAmbientiCommesse = new CondizioniFiltri(where);
  }

  /**
   * initDataUtlimaConsunDef
   */
  //29960
  /*protected void initDataUtlimaConsunDef() {
	String where = AmbienteCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "'";
    try {
      List ambList = AmbienteCommessa.retrieveList(AmbienteCommessa.class, where, "", false);
      for(Iterator it = ambList.iterator(); it.hasNext(); ) {
        AmbienteCommessa amb = (AmbienteCommessa)it.next();
        if(amb.getDataChiusDef() != null) {
          setDataUltimaConsun(amb.getDataChiusDef());
          return;
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
  }*/
  
  protected synchronized void initDataUtlimaConsunDef() {
	  try{
		  CachedStatement cSelectCommesse = new CachedStatement("SELECT MAX(" + AmbienteCommessaTM.DATA_CHIUS_DEF + ") FROM " + AmbienteCommessaTM.TABLE_NAME + " WHERE " + AmbienteCommessaTM.ID_AZIENDA +"=?");
		  PreparedStatement stmt = cSelectCommesse.getStatement();
	      Database database = ConnectionManager.getCurrentDatabase();
	      database.setString(stmt, 1, getIdAzienda());
	      ResultSet rs = cSelectCommesse.executeQuery();
	      if(rs.next()) {
	    	  java.sql.Date dataUltimaConsun = rs.getDate(1);
	    	  if(dataUltimaConsun != null){
	    		  setDataUltimaConsun(dataUltimaConsun);
	    	  }
	    	  return;
	      }
	  }
	  catch (SQLException ex) {
		  ex.printStackTrace(Trace.excStream);
	}
  }
  //29960 fine

  /**
   * recuperoCommesseList
   * @throws SQLException
   */
  protected synchronized void recuperoCommesseList() throws SQLException {
    if(iAmbienti != null) {

      String inClause = "";
      for(Iterator it = iAmbienti.iterator(); it.hasNext(); ) {
        inClause += "'" + ((AmbienteCommessa)it.next()).getIdAmbienteCommessa() + "',";
      }
      inClause = inClause.substring(0, inClause.length() - 1);

      //31460 inizio
      String selectCommesseQuery =
        "SELECT CMM." + CommessaTM.ID_AZIENDA + ", "
        + "CMM." + CommessaTM.ID_COMMESSA + ", "
        + "CMM." + CommessaTM.R_COMMESSA_APP + ", "
        + "CMM." + CommessaTM.R_COMMESSA_PRM + ", "
        + "CMM." + CommessaTM.R_STABILIMENTO + ", "
        + "CMM." + CommessaTM.R_AMBIENTE_CMM
        + " FROM " + CommessaTM.TABLE_NAME + " CMM,"
        + AmbienteCommessaTM.TABLE_NAME + " AMB_CMM,"
        + TipoCommessaTM.TABLE_NAME + " TP_CMM"
        + " WHERE"
        + " CMM." + CommessaTM.ID_AZIENDA + " = ?"
        + " AND CMM." + CommessaTM.R_AMBIENTE_CMM + " IN (" + inClause + ")"
        + " AND CMM." + CommessaTM.STATO + " <> ?"
        + " AND CMM." + CommessaTM.ID_AZIENDA + " = TP_CMM." + TipoCommessaTM.ID_AZIENDA
        + " AND CMM." + CommessaTM.R_TIPO_COMMESSA + " = TP_CMM." + TipoCommessaTM.ID_TIPO_CMM
        + " AND CMM." + CommessaTM.ID_AZIENDA + " = AMB_CMM." + AmbienteCommessaTM.ID_AZIENDA
        + " AND CMM." + CommessaTM.R_AMBIENTE_CMM + " = AMB_CMM." + AmbienteCommessaTM.ID_AMBIENTE_CMM
        //+ " AND TP_CMM." + TipoCommessaTM.NATURA_CMM + " = ?";//31460
        + " AND TP_CMM." + TipoCommessaTM.NATURA_CMM + " IN (" + TipoCommessa.NATURA_CMM__GESTIONALE + "," + TipoCommessa.NATURA_CMM__GEST_INTERNA + ")";//31460
      	if(!isCommesseProvvisorie()){
      		selectCommesseQuery += " AND CMM." + CommessaTM.STATO_AVANZAMENTO + " <> " + Commessa.STATO_AVANZAM__PROVVISORIA;
      	}
//          + " AND ( AMB_CMM." + AmbienteCommessaTM.DATA_CHIUS_DEF + " IS NULL" //Data ultima chiusura is not valorized
//          + "     OR ( AMB_CMM." + AmbienteCommessaTM.DATA_CHIUS_DEF + " >= CMM." + CommessaTM.DATA_CONFERMA
//          + "         AND (CMM." + CommessaTM.STATO_AVANZAMENTO + " <> ?" //Commessa is not chiusa
//          + "              OR AMB_CMM." + AmbienteCommessaTM.DATA_CHIUS_DEF + "< CMM." + CommessaTM.DATA_FINE //Data ultima chiusura is previous of data chiusura
//          + ")"
//          + ")"
//          + ")"
        
        selectCommesseQuery += " AND (AMB_CMM." + AmbienteCommessaTM.DATA_CHIUS_DEF + " IS NULL"
        + " OR NOT (CMM." + CommessaTM.STATO_AVANZAMENTO + " = ?"
        + " AND CMM." + CommessaTM.DATA_FINE + " <= " + " AMB_CMM." + AmbienteCommessaTM.DATA_CHIUS_DEF //Data ultima chiusura is previous of data chiusura
        + ")"
        + ")"
        + " ORDER BY " + CommessaTM.ID_COMMESSA;
      //31460 fine
      CachedStatement cSelectCommesse = new CachedStatement(selectCommesseQuery);//31460
      PreparedStatement stmt = cSelectCommesse.getStatement();
      Database database = ConnectionManager.getCurrentDatabase();
      database.setString(stmt, 1, getIdAzienda());
      database.setString(stmt, 2, String.valueOf(DatiComuniEstesi.ANNULLATO));
      //database.setString(stmt, 3, String.valueOf(TipoCommessa.NATURA_CMM__GESTIONALE));//31460      
      database.setString(stmt, 3, String.valueOf(Commessa.STATO_AVANZAM__CHIUSA_CONTABILAMENTO));//31460

      ResultSet rs = cSelectCommesse.executeQuery();
      while(rs.next()) {
        CompactCommessa compactCmm = (CompactCommessa)Factory.createObject(CompactCommessa.class);
        compactCmm.setIdAzienda(rs.getString(1));
        compactCmm.setIdCommessa(rs.getString(2));
        compactCmm.setIdCommessaApp(rs.getString(3));
        compactCmm.setIdCommessaPrm(rs.getString(4));
        compactCmm.setIdStabilimento(rs.getString(5));
        compactCmm.setIdAmbiente(rs.getString(6));
        //31460 inizio
        if(isCommessaValidoPerEstrazione(compactCmm))
        	iCompactCommesse.add(compactCmm);
        //31460 fine
      }
      rs.close();
      cSelectCommesse.free();
    }
  }
  
  //31460 inizio
  public boolean isCommessaProovisoria(CompactCommessa compactCmm){
	return (compactCmm.getCommessa() != null &&  compactCmm.getCommessa().getStatoAvanzamento() == Commessa.STATO_AVANZAM__PROVVISORIA); 
  }
  
  public boolean isCommessaGestInterna(CompactCommessa compactCmm){
	return (compactCmm.getCommessa() != null && compactCmm.getCommessa().getTipoCommessa() != null && compactCmm.getCommessa().getTipoCommessa().getNaturaCommessa() == TipoCommessa.NATURA_CMM__GEST_INTERNA); 
  }
  
  public boolean isCommessaValidoPerEstrazione(CompactCommessa compactCmm){
	  boolean ret = true;
	  boolean articoloAssente = false;
	  boolean stabilimentoAssente = false;
	  boolean versioneAssente = false;
	  boolean configurazioneAssente = false;
	  if(isCommessaProovisoria(compactCmm) || isCommessaGestInterna(compactCmm)){		  
		  if(compactCmm.getCommessa().getArticolo() == null){
			  articoloAssente = true;
		  }
		  else if(compactCmm.getCommessa().getArticolo().isConfigurato() && compactCmm.getCommessa().getConfigurazione() == null){
			  configurazioneAssente = true;
		  }
		  
		  if(compactCmm.getCommessa().getStabilimento() == null){
			  stabilimentoAssente = true;
		  }
		  
		  if(compactCmm.getCommessa().getArticoloVersione() == null){
			  versioneAssente = true;
		  }
		  if(articoloAssente || stabilimentoAssente || versioneAssente || configurazioneAssente){
			  Vector msgParams = new Vector();
			  msgParams.add(KeyHelper.formatKeyString(compactCmm.getCommessa().getKey()));
			  String msg = Utils.buildTextWithParams(ResourceLoader.getString(RESOURCE_NAME, "CommessaNonConsuntivataErr") , msgParams);
			  boolean addSepMsg = false;
			  if(articoloAssente){
				  msg += ResourceLoader.getString(RESOURCE_NAME, "ArticoloAssenteErr");
				  addSepMsg = true;
			  }
			  
			  if(stabilimentoAssente){
				  if(addSepMsg){
					  msg += ", ";
				  }
				  msg += ResourceLoader.getString(RESOURCE_NAME, "StabilimentoAssenteErr");
				  addSepMsg = true;
			  }
			  
			  if(versioneAssente){
				  if(addSepMsg){
					  msg += ", ";
				  }
				  msg += ResourceLoader.getString(RESOURCE_NAME, "VersioneAssenteErr");
				  addSepMsg = true;
			  }
			  
			  if(configurazioneAssente){
				  if(addSepMsg){
					  msg += ", ";
				  }
				  msg += ResourceLoader.getString(RESOURCE_NAME, "ConfigurazioneAssenteErr");
				  addSepMsg = true;
			  }			  
			  output.print(msg);
			  ret = false;
		  }
	  }
	  return ret;
  }
  //31460 fine

  /**
   * recuperoAmbienteList
   * @throws Exception
   */
  protected void recuperoAmbientiList() throws Exception {
    String where = "";
    //29960 inizio
    /*if(getTipoConsuntivazione() == TIPO_CONSUN__DEFINITIVA)
      where = AmbienteCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "'";
    else {*/
    //29960 fine	
      where = iFiltroAmbientiCommesse.getCondizioneWhere();
      if(where == null || where.trim().length() == 0)
        where = AmbienteCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "'";
      else {
        where = replaceCmmName(where);
        where += " AND (" + AmbienteCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "')";
      }
    //}//29960
    iAmbienti = AmbienteCommessa.retrieveList(AmbienteCommessa.class, where, "", false);
  }

  /**
   * replaceCmmName
   * @param where String
   * @return String
   */
  protected String replaceCmmName(String where) {
    String ambCmmColName = CommessaTM.TABLE_NAME + "." + CommessaTM.R_AMBIENTE_CMM;
    while(where.indexOf(ambCmmColName) != -1) {
      int idx = where.indexOf(ambCmmColName);
      where = where.substring(0, idx) + AmbienteCommessaTM.TABLE_NAME + "." + AmbienteCommessaTM.ID_AMBIENTE_CMM
        + where.substring(idx + ambCmmColName.length(), where.length());
    }
    return where;
  }

  /**
   * checkAmbientiCongruence
   * @return boolean
   */
  protected boolean checkAmbientiCongruence() {
    boolean ret = true;
    for(Iterator it = iAmbienti.iterator(); it.hasNext(); ) {
      AmbienteCommessa ambCmm = (AmbienteCommessa)it.next();
      if(ambCmm.getStatoUltimaConsun() != AmbienteCommessa.STATO_ULTIMA_CONSUN__NON_SIGNIFICATIVO
        && ambCmm.getStatoUltimaConsun() != AmbienteCommessa.STATO_ULTIMA_CONSUN__COMPLETATA) {
        Vector msgParams = new Vector();
        msgParams.add(KeyHelper.formatKeyString(ambCmm.getKey()));
        String msg = Utils.buildTextWithParams(ResourceLoader.getString(RESOURCE_NAME, "AmbCmmNonCongruenteMsg") + "\n", msgParams);

        output.print(msg);
        ret = false;
      }
      //Fix 04171 Mz inizio
      //ret = controlDataRiferimentoCongruence(ambCmm);
      //if (!ret)
      //  return false;

      if(ret && !controlDataRiferimentoCongruence(ambCmm))
        ret = false;
        //Fix 04171 Mz fine
    }
    return ret;
  }

  /**
   * checkDataRiferimento
   * @return ErrorMessage
   */
  public ErrorMessage checkDataRiferimento() {
    if(getTipoConsuntivazione() == TIPO_CONSUN__DEFINITIVA && getDataRiferimento() == null)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  /**
   * controlDataRiferimentoCongruence
   * @param ambCmm AmbienteCommessa
   * @return boolean
   */
  protected boolean controlDataRiferimentoCongruence(AmbienteCommessa ambCmm) {
    if(ambCmm.getDataChiusDef() != null
      && getTipoConsuntivazione() == ConsuntivazioneCommesse.TIPO_CONSUN__DEFINITIVA
      && getDataRiferimento().compareTo(ambCmm.getDataChiusDef()) <= 0) {

      Vector msgParams = new Vector();
      msgParams.add(KeyHelper.formatKeyString(ambCmm.getKey()));
      String msg = Utils.buildTextWithParams(ResourceLoader.getString(RESOURCE_NAME, "AmbCmmDataRifNonCongruenteMsg") + "\n", msgParams);
      writer.write(msg);
      return false;
    }

    if(ambCmm.getDataChiusPrv() != null
      && getTipoConsuntivazione() == ConsuntivazioneCommesse.TIPO_CONSUN__PROVVISORIA
      && ambCmm.getDataChiusDef() != null
      && getDataRiferimento().compareTo(ambCmm.getDataChiusDef()) <= 0) {

      Vector msgParams = new Vector();
      msgParams.add(KeyHelper.formatKeyString(ambCmm.getKey()));
      String msg = Utils.buildTextWithParams(ResourceLoader.getString(RESOURCE_NAME, "AmbCmmDataRifNonCongruenteMsg") + "\n", msgParams);
      writer.write(msg);
      return false;
    }

    return true;
  }

  /**
   * updateAmbientiCmmToAvviato
   * @throws SQLException
   */
  protected void updateAmbientiCmmToAvviato() throws SQLException {
    for(Iterator it = iAmbienti.iterator(); it.hasNext(); ) {
      AmbienteCommessa ambCmm = (AmbienteCommessa)it.next();
      ambCmm.setDataElabUltConsun(iDataCorrente);
      ambCmm.setTipoUltimaConsun(getTipoConsuntivazione());
      ambCmm.setDataRiferUltConsun(getDataRiferimento());
      ambCmm.setIdAmbienteCosti(getIdAmbienteCostiMancanti());
      ambCmm.setStatoUltimaConsun(AmbienteCommessa.STATO_ULTIMA_CONSUN__AVVIATA);
      ambCmm.save();
    }
    commit();
  }

  /**
   * updateAmbientiCommessa
   * @param statoConsuntivazione char
   * @throws SQLException
   */
  protected void updateAmbientiCommessa(char statoConsuntivazione) throws SQLException {
    for(Iterator it = iAmbienti.iterator(); it.hasNext(); ) {
      AmbienteCommessa ambCmm = (AmbienteCommessa)it.next();
      ambCmm.setDataElabUltConsun(iDataCorrente);
      ambCmm.setTipoUltimaConsun(getTipoConsuntivazione());
      ambCmm.setDataRiferUltConsun(getDataRiferimento());
      ambCmm.setIdAmbienteCosti(getIdAmbienteCostiMancanti());
      ambCmm.setStatoUltimaConsun(statoConsuntivazione);
      ambCmm.save();
    }

    commit();
  }

  /**
   * cancellaStroiciECosti
   * @throws SQLException
   */
  protected synchronized void cancellaStroiciECosti() throws SQLException {
    for(Iterator it = iCompactCommesse.iterator(); it.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)it.next();
      Database database = ConnectionManager.getCurrentDatabase();

      //Cancella storici consuntivazione commesse
      PreparedStatement stmt = cDeleteStoricoCmm.getStatement();
      database.setString(stmt, 1, getIdAzienda());
      database.setString(stmt, 2, compactCmm.getIdCommessa());
      //Fix 04361 Mz inizio
      //cDeleteStoricoCmm.getStatement().setDate(3, iDataUltChiusDefAmbiente != null ? iDataUltChiusDefAmbiente : TimeUtils.getDate(1, 1, 1));
      //29960 inizio
      //stmt.setDate(3, (iDataUltChiusDefAmbiente != null) ? iDataUltChiusDefAmbiente : database.getMinimumDate());
      java.sql.Date dataUltChius = getDataUltChiusDefAmbiente(compactCmm.getIdAmbiente());
      stmt.setDate(3, (dataUltChius != null) ? dataUltChius : database.getMinimumDate());
      //29960 fine
      database.setString(stmt, 4, String.valueOf(StoricoCommessa.RICHIESTA));
      database.setString(stmt, 5, String.valueOf(StoricoCommessa.ORDINE));
      //Fix 04361 Mz fine
      cDeleteStoricoCmm.execute();

      //Cancella costi commesse e oggetti dipendenti
      stmt = cDeleteCostiCmm.getStatement();
      database.setString(stmt, 1, compactCmm.getIdAzienda());
      database.setString(stmt, 2, compactCmm.getIdCommessa());
      database.setString(stmt, 3, String.valueOf(CostiCommessa.COSTO_CONSUNT_PROVV));
      cDeleteCostiCmm.execute();
    }

    //Modifica stato ultima consuntivazione di ambienti processati
    updateAmbientiStatoUltConsun(AmbienteCommessa.STATO_ULTIMA_CONSUN__STORICO_COSTI_CANCELLATI);

    commit();
  }

  /**
   * updateAmbientiStatoUltConsun
   * @param statoUltimaConsun char
   * @throws SQLException
   */
  protected void updateAmbientiStatoUltConsun(char statoUltimaConsun) throws SQLException {
    for(Iterator it = iAmbienti.iterator(); it.hasNext(); ) {
      AmbienteCommessa ambCmm = (AmbienteCommessa)it.next();
      ambCmm.setStatoUltimaConsun(statoUltimaConsun);
      if(statoUltimaConsun == AmbienteCommessa.STATO_ULTIMA_CONSUN__STORICO_COSTI_CANCELLATI) {
        ambCmm.setDataChiusPrv(null);
      }
      ambCmm.save();
    }
  }

  /**
   * recupDataUltChiusDef
   * @return Date
   */
  //29960 inizio
  /*protected java.sql.Date recupDataUltChiusDef() {
    for(Iterator it = iAmbienti.iterator(); it.hasNext(); ) {
      AmbienteCommessa ambCmm = (AmbienteCommessa)it.next();
      if((ambCmm != null) && (ambCmm.getDataChiusDef() != null))
        return ambCmm.getDataChiusDef();
    }
    return null;
  }*/
  protected void recupDataUltChiusDef() {
	  for(Iterator it = iAmbienti.iterator(); it.hasNext(); ) {
		  AmbienteCommessa ambCmm = (AmbienteCommessa)it.next();
		  if((ambCmm != null) && (ambCmm.getDataChiusDef() != null))
			  setDataUltChiusDefAmbiente(ambCmm.getIdAmbienteCommessa(), ambCmm.getDataChiusDef());
	  }
  }
  //29960 fine

  /**
   * initializeReports
   * @throws Exception
   * @return AvailableReport
   */
  protected AvailableReport createAnomaliaConsuntivoReport() throws Exception {
    iBatchJobId = getBatchJob().getBatchJobId();
    AvailableReport anmAvailableRep = createNewReport(getReportId());
    setPrintToolInterface((PrintingToolInterface)Factory.createObject(CrystalReportsInterface.class));
    ReportModel rptModel = (ReportModel)Factory.createObject(ReportModel.class);
    rptModel.setReportModelId(getReportId().trim());
    rptModel.retrieve();
    anmAvailableRep.setWhereCondition(printToolInterface.generateDefaultWhereCondition(anmAvailableRep, ReportAnomalieConsCmmTM.TABLE_NAME));
    anmAvailableRep.save();
    return anmAvailableRep;
  }

  /**
   *
   * @throws Exception
   * @return AvailableReport
   */
  protected AvailableReport createDocumentoConsuntivoReport() throws Exception {
    String where = ReportModelTM.GROUP + " = '" + iAnmAvailableRep.getReportModel().getGroup() + "' AND " +
      ReportModelTM.REPORT_MODEL_ID + " != '" + iAnmAvailableRep.getReportModel().getReportModelId() + "' AND " +
      ReportModelTM.ENTITY_ID + "='" + iAnmAvailableRep.getReportModel().getEntityId() + "'";

    AvailableReport docAvailableRep = null;

    Vector reportModels = ReportModel.retrieveList(where, ReportModelTM.SEQUENCE, false);
    if(reportModels.size() > 0) {
      if(isGenFatture()) {
        docAvailableRep = createNewReport(((ReportModel)reportModels.get(0)).getReportModelId());
        docAvailableRep.setWhereCondition(printToolInterface.generateDefaultWhereCondition(docAvailableRep, ReportDocumentoConsCmmTM.TABLE_NAME));
        docAvailableRep.save();
      }
    }
    return docAvailableRep;
  }

  /**
   *
   * @throws Exception
   * @return AvailableReport
   */
  protected AvailableReport createStampaConsuntivoReport() throws Exception {
    String where = ReportModelTM.GROUP + " = '" + iAnmAvailableRep.getReportModel().getGroup() + "' AND " +
      ReportModelTM.REPORT_MODEL_ID + " != '" + iAnmAvailableRep.getReportModel().getReportModelId() + "' AND " +
      ReportModelTM.ENTITY_ID + "='" + iAnmAvailableRep.getReportModel().getEntityId() + "'";

    AvailableReport consuntivoAvailableRep = null;

    Vector reportModels = ReportModel.retrieveList(where, ReportModelTM.SEQUENCE, false);
    if(reportModels.size() > 1) {
      consuntivoAvailableRep = createNewReport(((ReportModel)reportModels.get(1)).getReportModelId());
      consuntivoAvailableRep.setWhereCondition(printToolInterface.generateDefaultWhereCondition(consuntivoAvailableRep,
        ReportConsuntivoCommessaTestataTM.TABLE_NAME));
      consuntivoAvailableRep.save();
    }
    return consuntivoAvailableRep;
  }

  /**
   *
   * @throws Exception
   * @return AvailableReport
   */
  protected AvailableReport createStampaRiepologoReport() throws Exception {
    String where = ReportModelTM.GROUP + " = '" + iAnmAvailableRep.getReportModel().getGroup() + "' AND " +
      ReportModelTM.REPORT_MODEL_ID + " != '" + iAnmAvailableRep.getReportModel().getReportModelId() + "' AND " +
      ReportModelTM.ENTITY_ID + "='" + iAnmAvailableRep.getReportModel().getEntityId() + "'";

    AvailableReport riepilogoAvailableRep = null;

    Vector reportModels = ReportModel.retrieveList(where, ReportModelTM.SEQUENCE, false);
    if(reportModels.size() > 2) {
      riepilogoAvailableRep = createNewReport(((ReportModel)reportModels.get(2)).getReportModelId());
      riepilogoAvailableRep.setWhereCondition(printToolInterface.generateDefaultWhereCondition(riepilogoAvailableRep,
        ReportRiepilogoCommessaTestataTM.TABLE_NAME));
      riepilogoAvailableRep.save();
      ConnectionManager.commit();
    }
    return riepilogoAvailableRep;
  }

  /**
   * addAnomalie
   * @param rptAnomalieConsCmm ReportAnomalieConsCmm
   * @throws Exception
   */
  public void addAnomalie(ReportAnomalieConsCmm rptAnomalieConsCmm) throws Exception {
     //Fix 10411 inizio limitato numero anomalie salvate
     iNumeroAnomalie++;
     //Fix 33593 --inizio
     /*
     if(iNumeroAnomalie > MAX_ANOMALIE)
        return;
        */
     //Fix 10411 fine
     
     if(rptAnomalieConsCmm.getTipoErrore() == ReportAnomalieConsCmm.TIPO_ERR_ERRORE)
        iNumeroErrori++;
     else if(rptAnomalieConsCmm.getTipoErrore() == ReportAnomalieConsCmm.TIPO_ERR_WARNING)
        iNumeroWarning++;
     //Fix 33593 --fine
     rptAnomalieConsCmm.setBatchJobId(iBatchJobId);
     rptAnomalieConsCmm.setReportNr(iAnomalieReportNr);
     rptAnomalieConsCmm.setIdProgressivo(iRigaJobId++);
     ConnectionManager.pushConnection(getAnomaliesConnectionDescriptor());
     rptAnomalieConsCmm.save();
     ConnectionManager.commit();
     ConnectionManager.popConnection(getAnomaliesConnectionDescriptor());
  }

  /**
   * addRptDocConsCmm
   * @param rptDocumentoConsCmm ReportDocumentoConsCmm
   * @throws Exception
   */
  public void addRptDocConsCmm(ReportDocumentoConsCmm rptDocumentoConsCmm) throws Exception {
    rptDocumentoConsCmm.setBatchJobId(iBatchJobId);
    rptDocumentoConsCmm.setReportNr(iDocReportNr);
    rptDocumentoConsCmm.setIdProgressivo(iRigaJobId++);
    ConnectionManager.pushConnection(getAnomaliesConnectionDescriptor());
    rptDocumentoConsCmm.save();
    ConnectionManager.commit();
    ConnectionManager.popConnection(getAnomaliesConnectionDescriptor());
  }

  //29960 inizio
  /**
   * getDataUltChiusDefAmbiente
   * @return Date
   */
  /**
   * @deprecated
   * Sostituito dal metodo getDataUltChiusDefAmbiente(String idAmbiente)
   */
  public java.sql.Date getDataUltChiusDefAmbiente() {
	//29960 inizio
	//return iDataUltChiusDefAmbiente;
	if (!iMapDataUltChiusDefAmbiente.isEmpty())
		return (java.sql.Date) iMapDataUltChiusDefAmbiente.values().toArray()[0];
	return null;
	//29960 fine  
  }
  //29960 inizio
  public java.sql.Date getDataUltChiusDefAmbiente(String idAmbiente) {
	  return (java.sql.Date) iMapDataUltChiusDefAmbiente.get(idAmbiente);
  }
  //29960 fine

  /**
   * setDataUltChiusDefAmbiente
   * @param DataUltChiusDefAmbiente Date
   */
  /**
   * @deprecated
   * Sostituito dal metodo setDataUltChiusDefAmbiente(String idAmbiente, java.sql.Date DataUltChiusDefAmbiente)
   */
  public void setDataUltChiusDefAmbiente(java.sql.Date DataUltChiusDefAmbiente) {
    //iDataUltChiusDefAmbiente = DataUltChiusDefAmbiente;//29960
	iMapDataUltChiusDefAmbiente.put("#####", DataUltChiusDefAmbiente);//29960
  }
  public void setDataUltChiusDefAmbiente(String idAmbiente, java.sql.Date DataUltChiusDefAmbiente) {
	  iMapDataUltChiusDefAmbiente.put(idAmbiente, DataUltChiusDefAmbiente);
  }
  //29960 fine

  /**
   * isProcessOrdine
   * @return boolean
   */
  public boolean isProcessOrdine() {
    return iProcessOrdine;
  }

  /**
   * setProcessOrdine
   * @param ProcessOrdine boolean
   */
  public void setProcessOrdine(boolean ProcessOrdine) {
    iProcessOrdine = ProcessOrdine;
  }

  /**
   * getIdProgrStorico
   * @return Integer
   */
  public Integer getIdProgrStorico() {
    return iIdProgrStorico;
  }

  /**
   * setIdProgrStorico
   * @param IdProgrStorico Integer
   */
  public void setIdProgrStorico(Integer IdProgrStorico) {
    iIdProgrStorico = IdProgrStorico;
  }

  /**
   * commitWithGestoreCommit
   * @param fine boolean
   * @throws SQLException
   */
  public void commitWithGestoreCommit(boolean fine) throws SQLException {
    if(!isSimulazione()) {
      if(fine)
        iGestoreCommit.fine();
      else
        iGestoreCommit.commit();
    }
  }

  /**
   *
   */
  public void commit() {
    try {
      if(!isSimulazione())
        ConnectionManager.commit();
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
  }

  /**
   * initializeUserEnums
   */
  protected void initializeUserEnums() {
    String tableName = removeSchema(ReportAnomalieConsCmmTM.TABLE_NAME);
    addEnum("TipoDocumento", tableName + "." + ReportAnomalieConsCmmTM.TIPO_RIGA_ORIGINE, "TipoRigaOrigine");
    addEnum("TipoRisorsa", tableName + "." + ReportAnomalieConsCmmTM.R_TIPO_RISORSA, "TipoRisorsaNonSignific");
    addEnum("LivelloRisorsa", tableName + "." + ReportAnomalieConsCmmTM.R_LIVELLO_RISORSA, "LivelloRisorsa");
    addEnum("DocumentoOrigine", tableName + "." + ReportAnomalieConsCmmTM.DOC_ORIGINE, "DocumentoOrigineStampa");
    //Fix 10151 - inizio
    addEnum("DesTipoErrore", tableName + "." + ReportAnomalieConsCmmTM.TIPO_ERRORE, "Severita");
    //Fix 10151 - fine

    if(isStampaConsuntivo()) {
      String detTableName = removeSchema(ReportConsuntivoCommessaDettaglioTM.TABLE_NAME);
      String testataTableName = removeSchema(ReportConsuntivoCommessaTestataTM.TABLE_NAME);
      addEnum("TipoRisorsaStp", detTableName + "." + ReportConsuntivoCommessaDettaglioTM.R_TIPO_RISORSA, "TipoRisorsaNonSignific");
      addEnum("LivelloRisorsaStp", detTableName + "." + ReportConsuntivoCommessaDettaglioTM.R_LIVELLO_RISORSA, "LivelloRisorsa");
      addEnum("TipoRilevazione", detTableName + "." + ReportConsuntivoCommessaDettaglioTM.TIPO_RILEV_RSR, "TipoRilevazione");
      addEnum("TipoRigaOrigine", detTableName + "." + ReportConsuntivoCommessaDettaglioTM.TIPO_RIGA_ORIGINE, "TipoRigaOrigine");
      addEnum("StatoAvanzamento", testataTableName + "." + ReportConsuntivoCommessaTestataTM.STATO_AVANZAMENTO, "StatoAvanzCmm");
    }

    if(isStampaRiepilogo()) {
      String tableNameTES = removeSchema(ReportRiepilogoCommessaTestataTM.TABLE_NAME);
      String tableNameDET = removeSchema(ReportRiepilogoCommessaDettaglioTM.TABLE_NAME);
      addEnum("StatoAvanzamentoRiep", tableNameTES + "." + ReportRiepilogoCommessaTestataTM.STATO_AVANZAMENTO, "StatoAvanzCmm");
      addEnum("CriterioFatturazione", tableNameDET + "." + ReportRiepilogoCommessaDettaglioTM.CRITERIO_FATT, "CriterioFatt");
    }

  }

  /**
   *
   */
  protected void initializeUserParameters() {

    String tipoConsuntivazione = getDescAttrRef("TipoConsunCmm", getTipoConsuntivazione());
    addUserParameter("TipoConsuntivazioneRic", tipoConsuntivazione);

    String data = getFormatedDate(getDataUltimaConsun());
    addUserParameter("DataUltimaConsuntivazioneRic", data);

    //...FIX 7430 inizio

    /*ArrayList a = iFiltroAmbientiCommesse.getVettoreValoriFiltro();
    String parmFiltri = "";

    for(int i = 0; i < a.size(); i++) {
      ArrayList al = (ArrayList)a.get(i);
      String name = (String)al.get(1);
      String from = (String)al.get(2);
      String to = (String)al.get(3);
      String lista = (String)al.get(4);
      if(from != null || to != null || lista != null)
        parmFiltri += name + ":";
      if(from != null)
        parmFiltri += " " + ResourceLoader.getString(RESOURCE_NAME, "CP_DA") + " " + from;
      if(to != null)
        parmFiltri += " " + ResourceLoader.getString(RESOURCE_NAME, "CP_A") + " " + to;
      if(lista != null)
        parmFiltri += "\n [" + ResourceLoader.getString(RESOURCE_NAME, "CP_Lista") + ": " + convertLista(lista) + "]";
      if(from != null || to != null || lista != null)
        parmFiltri += "\n";
    }

    addUserParameter("Filtri", parmFiltri);*/
    addUserParameter("Filtri", iFiltroAmbientiCommesse.getValoriFiltroString());

    //...FIX 7430 fine

    if(isStampaConsuntivo()) {
      initializeStampaConsUserParameters();
    }

    if(isStampaRiepilogo()) {
      initializeStampaRiepUserParameters();
    }
    //Fix 04599 Begin
    String dataRif = getDataRiferimento() != null ? new DateType().objectToString(getDataRiferimento()) : new DateType().objectToString(TimeUtils.getCurrentDate());
    addUserParameter("DataRif", dataRif);
    //Fix 04599 End
  }

  /**
   *
   * @param lista String
   * @return String
   */
  /*protected String convertLista(String lista) {
    String result = "";
    java.util.StringTokenizer st = new java.util.StringTokenizer(lista, ColonneFiltri.LISTA_SEP);
    while(st.hasMoreTokens()) {
      java.util.StringTokenizer stt = new java.util.StringTokenizer(st.nextToken(), ColonneFiltri.COD_DESC_SEP);
      if(result.length() == 0)
        result += stt.nextToken();
      else
        result += "/" + stt.nextToken();
    }
    return result;
  }*/

  /**
   *
   */
  protected void initCondizioniFiltroForBatch() {
   //Fix 9786 inizio
     iFiltroAmbientiCommesse.impostaColonneConVettoreValoriFiltro();
     /*
    java.util.ArrayList filterValues = iFiltroAmbientiCommesse.getVettoreValoriFiltro();
    for(int i = 0; i < iFiltroAmbientiCommesse.getColonneFiltro().size(); i++) {
      ColonneFiltri colFlt = (ColonneFiltri)iFiltroAmbientiCommesse.getColonneFiltro().get(i);
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
   *
   * @param IdRef String
   * @param value char
   * @return String
   */
  private static String getDescAttrRef(String IdRef, char value) {
    com.thera.thermfw.type.EnumType eType = new com.thera.thermfw.type.EnumType(IdRef);
    return eType.descriptionFromValue(String.valueOf(value));
  }

  /**
   *
   * @param date Date
   * @return String
   */
  protected String getFormatedDate(java.sql.Date date) {
    String dataString = "";
    if(date != null) {
      ClassAD cad = getClassAD("DataUltimaConsuntivazione");
      Type type = null;
      if(cad != null) {
        type = cad.getType();
        dataString = type.objectToString(date);
      }
    }
    return dataString;
  }

  protected ClassAD getClassAD(String attributeName) {
    try {
      ClassADCollection cad = ClassADCollectionManager.collectionWithName(CLASS_HDR_NAME);
      return cad.getAttribute(attributeName);
    }
    catch(Exception ex) {
      return null;
    }
  }

  /**
   * removeSchema
   * @param name String
   * @return String
   */
  public static String removeSchema(String name) {
    int pos = name.indexOf(".");
    if(pos > 0)
      name = name.substring(pos + 1);
    return name;
  }

  /**
   *
   * @return ErrorMessage
   */
  public boolean controlDataRiferimento() {
    if(getDataRiferimento() == null || iDataRifNull)
      return true;

    //if(getTipoConsuntivazione() == TIPO_CONSUN__DEFINITIVA && getDataRiferimento().compareTo(iDataCorrente) >= 0) {//31460
    if(getTipoConsuntivazione() == TIPO_CONSUN__DEFINITIVA && getDataRiferimento().compareTo(iDataCorrente) > 0) { //31460
      String msg = ResourceLoader.getString(RESOURCE_NAME, "DataRifAnomalie") + "\n";
      writer.write(msg);
      return false;
    }

    return true;
  }

  /**
   * getCompactCommessa
   * @param cmm Commessa
   * @return CompactCommessa
   */
  public CompactCommessa getCompactCommessa(Commessa cmm) {
    for(Iterator iter = iCompactCommesse.iterator(); iter.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)iter.next();
      if(compactCmm.equals(cmm))
        return compactCmm;
    }

    return null;
  }

  /**
   *
   * @return ConnectionDescriptor
   */
  public ConnectionDescriptor getAnomaliesConnectionDescriptor() {
    if(iAnomaliesConnectionDescriptor == null) {
      iAnomaliesConnectionDescriptor = ConnectionManager.getCurrentConnectionDescriptor().duplicate();
    }
    return iAnomaliesConnectionDescriptor;
  }

  /**
   *
   */
  protected void initializeStampaConsUserParameters() {
    try {
      String parmStatoCommessa = getDescAttrRef("StatoCommessaRpt", StampaConsuntivoCommessa.STATO_COMMESSA__ATTIVE);
      String parmTipoStampa = getDescAttrRef("TipoStampaConsCmm", StampaConsuntivoCommessa.TIPO_STAMPA__COMPLETA);
      String parmArticoliRisorse = getDescAttrRef("ArticoliRisorse", StampaConsuntivoCommessa.ARTICOLI_RISORSE__ENTRAMBI);
      String parmDataSaldoInizio = "";
      String parmDataSaldoFine = getDataRiferimento() != null ? new DateType().objectToString(getDataRiferimento()) : "";
      String parmTipoDettaglio = getDescAttrRef("TipoDettaglio", StampaConsuntivoCommessa.TIPO_DETTAGLIO__SOLO_SALDO_FINALE);
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
      //addUserParameter("FiltriStp", iStampaConsuntivoCommessa.constructParmFiltri(iFiltriStp));
      addUserParameter("FiltriStp", iFiltriStp.getValoriFiltroString());
      //...FIX 7430 fine
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
  }

  /**
   *
   */
  protected void initializeStampaRiepUserParameters() {
    try {
      String parmTipoStampa = getDescAttrRef("TipoStampaCmmRpt", StampaRiepilogoCommesse.TIPO_STAMPA_COSTI_RICAVI);
      String parmStatoCommessa = getDescAttrRef("StatoCommessaRpt", StampaRiepilogoCommesse.STATO_COMMESSA_ATTIVE);
      String parmTipoChiususra = getDescAttrRef("TipoConsCmmAmbRpt", getTipoConsuntivazione());
      String parmTipologiaCostoRif = getDescAttrRef("TipologiaCostoTraCmm", StampaRiepilogoCommesse.TIPOLOGIA_COSTO_PREVISTO);

      addUserParameter("TipoStampaRiepDesc", parmTipoStampa);
      addUserParameter("StatoCommessaRiepDesc", parmStatoCommessa);
      addUserParameter("TipoChiusuraRiepDesc", parmTipoChiususra);
      addUserParameter("TipologiaCostoRifRiepDesc", parmTipologiaCostoRif);
      //Fix 04837 Mz inizio
      addUserParameter("CompresaOrdinatoDesc", StampaRiepilogoCommesse.getBooleanDesc(false));
      addUserParameter("CompresaRichiestoDesc", StampaRiepilogoCommesse.getBooleanDesc(false));
      addUserParameter("Costo", StampaRiepilogoCommesse.getCostoParam(StampaRiepilogoCommesse.TIPOLOGIA_COSTO_PREVISTO));
      addUserParameter("CostoTotale", StampaRiepilogoCommesse.getCostoTotaleParam(StampaRiepilogoCommesse.TIPOLOGIA_COSTO_PREVISTO));
      //Fix 04837 Mz inizio

      //...FIX 7430 inizio
      //addUserParameter("FiltriRiep", iStampaRiepilogoCommesse.constructParmFiltri(iFiltriRiepilogo));
      addUserParameter("FiltriRiep", iFiltriRiepilogo.getValoriFiltroString());
      //...FIX 7430 fine
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
  }

  /**
   *
   * @return String
   */
  /*public String constructFiltriCmmStampa() {
    String filter = "";
    if(getCompactCommesse().size() > 0) {
      filter = "\n [ " + ResourceLoader.getString(RESOURCE_NAME, "CP_Lista") + ": ";
      Iterator it = getCompactCommesse().iterator();
      while(it.hasNext()) {
        CompactCommessa compactCmm = (CompactCommessa)it.next();
        filter += compactCmm.getIdCommessa() + (it.hasNext() ? " - " : "");
      }
      filter += " ]";
    }
    return filter;
  }*/

  /**
   *
   */
  protected void initFiltriStp() {
    iFiltriStp = iStampaConsuntivoCommessa.buildCondizioniFiltri();
  }

  /**
   *
   */
  protected void initFiltriRiepilogo() {
    iFiltriRiepilogo = iStampaRiepilogoCommesse.initializeConditioneFiltro();
  }


  //Fix 10411 inizio
  public ComLogger getLogger()
  {
     if(iLogger == null)
        iLogger = new ComLogger();
     return iLogger;
  }

  public List getComponentiDiCosto()
  {
     if(iListaComponentiCosto == null)
     {
        try
        {
           String where = ComponenteCostoTM.ID_AZIENDA + " ='" + getIdAzienda() + "'";
           iListaComponentiCosto = ComponenteCosto.retrieveList(where, "", false);
        }
        catch(Exception ex)
        {
          ex.printStackTrace(Trace.excStream);
        }
     }
     return iListaComponentiCosto;
  }

  public Map getMapComponentiDiCosto()
  {
     if(iMapComponentiCosto.size() == 0)
     {
        List compCosti = getComponentiDiCosto();
        if(compCosti != null)
        {
           Iterator iter = compCosti.iterator();
           while(iter.hasNext())
           {
              ComponenteCosto cmp = (ComponenteCosto)iter.next();
              String key = cmp.getIdComponenteCosto().toUpperCase();
              iMapComponentiCosto.put(key, cmp);
           }
        }
     }
     return iMapComponentiCosto;
  }

  protected VariabiliCosti getVariabiliCosti()
  {
     if(iVariabiliCostiCollection == null)
        iVariabiliCostiCollection = new VariabiliCosti();

     //Introdotta costruttore di copia per evitare di ricaricarle sempre da database
     return new VariabiliCosti(iVariabiliCostiCollection);
  }

 //Fix 10411 fine

 // fix 10913
 public int getBatchJobId(){
   return iBatchJobId;
 }

 public int getAnomalieReportNr(){
   return iAnomalieReportNr;
 }
 // fine fix 10913

 //Fix13743 Inizio RA
 public ErrorMessage checkAmbienteCostiMancanti(){
	 if(getModalitaCostiMancanti() == MOD_AMBIENTE_COSTI){ //30049
		 boolean res = false;
		 UtenteAmbienti ua = (UtenteAmbienti) Factory.createObject(UtenteAmbienti.class);
		 try {
			 res = ua.retrieve();
		 }
		 catch (SQLException ex) {
			 ex.printStackTrace();
		 }

		 if (res && ua.getAmbienteCosti() == null)
			 return new ErrorMessage("THIP40T058") ;
	 }//30049 
	 return null;
 }
 
 //30049 inizio
 public ErrorMessage checkModalitaCostiMancanti(){
	  if ((getModalitaCostiMancanti()== MOD_AMBIENTE_COSTI  && getIdAmbienteCostiMancanti()== null)||
			  (getModalitaCostiMancanti() == MOD_ARTICOLO_COSTI && getIdTipoCostoMancanti() == null)){		  
		  return new ErrorMessage("THIP_BS418");
	  }
	  return null;
 }
 //30049 fine

 public Vector checkAll(BaseComponentsCollection components) {
   Vector errors = super.checkAll(components);
   ErrorMessage em = checkAmbienteCostiMancanti();
   if (em != null)
     errors.add(em);

   return errors;
 }
 //Fix13743 Inizio RA
 
  //Fix 24035 inizio
	public void updateJobParameters() {
		getBatchJob().setParameter(objectToStream(this));
	}

	public String objectToStream(BusinessObject obj) {
		getDataCollector().loadAttValue();
		Enumeration e = getDataCollector().getComponents();
		String stream = "";
		while (e.hasMoreElements()) {
			BaseBOComponent current = (BaseBOComponent) e.nextElement();
			String classAdName = current.getComponentManager().getKeyForBaseComponentsCollection();
			if (!classAdName.equals("FiltriStp.WhereString") 
					&& !classAdName.equals("FiltriStp.WhereStringForCR")
					&& !classAdName.equals("FiltriRiepilogo.WhereString")
					&& !classAdName.equals("FiltriRiepilogo.WhereStringForCR")) {
				String value = (String) ((BaseBOComponentManager) current.getComponentManager()).getComponent().getValue();
				stream += classAdName + DELIM + value + SEPARATOR;
			}
		}
		return stream;
	}
  //Fix 24035 fine
	//30049 inizio
	public char getModalitaCostiMancanti() {
		return iModalitaCostiMancanti;
	}

	public void setModalitaCostiMancanti(char modalita) {
		iModalitaCostiMancanti = modalita;
		//setDirty();
	}
		  
	public TipoCosto getTipoCostoMancanti() {
		return (TipoCosto) iTipoCostoMancanti.getObject();
	}

	public void setTipoCostoMancanti(TipoCosto tipoCosto) {
		iTipoCostoMancanti.setObject(tipoCosto);
		//setDirty();
	}
	  
	public String getTipoCostoMancantiKey() {
		return iTipoCostoMancanti.getKey();
	}

	public void setTipoCostoMancantiKey(String key){
		iTipoCostoMancanti.setKey(key);
		//setDirty();
	}

	public String getIdTipoCostoMancanti(){
		return KeyHelper.getTokenObjectKey(iTipoCostoMancanti.getKey(), 2);
	}

	public void setIdTipoCostoMancanti(String idTipoCosto){
		String key = iTipoCostoMancanti.getKey();
		iTipoCostoMancanti.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idTipoCosto));
		//setDirty();
	} 
	//30049 fine

	//31460 inizio
	public boolean isCommesseProvvisorie() {
		return iCommesseProvvisorie;
	}

	public void setCommesseProvvisorie(boolean commesseProvvisorie) {
		this.iCommesseProvvisorie = commesseProvvisorie;
	}
	
	public boolean isEstrazioneOrdini() {
		return iEstrazioneOrdini;
	}

	public void setEstrazioneOrdini(boolean estrazioneOrdini) {
		this.iEstrazioneOrdini = estrazioneOrdini;
	}
	
	public boolean isCostiArticoloDaDocumento() {
		return iCostiArticoloDaDocumento;
	}

	public void setCostiArticoloDaDocumento(boolean costiArticoloDaDocumento) {
		this.iCostiArticoloDaDocumento = costiArticoloDaDocumento;
	}	
	
	public char getOrdineRecArticolo() {
		return iOrdineRecArticolo;
	}

	public void setOrdineRecArticolo(char ordineRecArticolo) {
		iOrdineRecArticolo = ordineRecArticolo;
	}	
	
	public char getOrdineRecRisorsa() {
		return iOrdineRecRisorsa;
	}

	public void setOrdineRecRisorsa(char ordineRecRisorsa) {
		iOrdineRecRisorsa = ordineRecRisorsa;
	}		
	//31460 fine
	
	//31504 inizio
	public boolean isEstrazioneRDA() {
		return iEstrazioneRDA;
	}

	public void setEstrazioneRDA(boolean estrazioneRDA) {
		this.iEstrazioneRDA = estrazioneRDA;
	}
	//31504 fine
	//31513 inizio
	public boolean isStoriciNonCommessa() {
		return iStoriciNonCommessa;
	}

	public void setStoriciNonCommessa(boolean storiciNonCommessa) {
		this.iStoriciNonCommessa = storiciNonCommessa;
	}
	//31513 fine
   //33950 inizio
   public boolean isEstrarrePeriodiDefinitivi() {
      return iEstrarrePeriodiDefinitivi;
   }

   public void setEstrarrePeriodiDefinitivi(boolean estrarrePeriodiDefinitivi) {
      iEstrarrePeriodiDefinitivi = estrarrePeriodiDefinitivi;
   }
   
   public boolean isEstrazioneStoriciCommessa() {
      return iEstrazioneStoriciCommessa;
   }

   public void setEstrazioneStoriciCommessa(boolean estrazioneStoriciCommessa) {
      iEstrazioneStoriciCommessa = estrazioneStoriciCommessa;
   }
   
   public boolean isDataValido(Date dataDocumento, CompactCommessa compactCmm) {
      return (getDataUltChiusDefAmbiente(compactCmm.getIdAmbiente()) == null || dataDocumento.compareTo(getDataUltChiusDefAmbiente(compactCmm.getIdAmbiente())) > 0);
   }
   //33950 fine
}

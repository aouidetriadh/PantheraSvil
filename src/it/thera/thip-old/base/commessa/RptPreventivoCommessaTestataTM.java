/*
 * @(#)RptPrevComTesTM.java
 */

/**
 * RptPrevComTesTM
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 16:39:23
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 16/12/2011    Wizard     Codice generato da Wizard
 * 19818     28/05/2014    AA    Aggiunto colonna R_LINGUA & DES_LINGUA
 * Number    Date          Owner    Descrizione
 * 33048     08/03/2021    RA       Aggiunto attributo Commenti e CommentiPiede
 */

package it.thera.thip.base.commessa;
import java.sql.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;

public class RptPreventivoCommessaTestataTM extends TableManager {

  /**
   * Attributo BATCH_JOB_ID
   */
  public static final String BATCH_JOB_ID = "BATCH_JOB_ID";

  /**
   * Attributo REPORT_NR
   */
  public static final String REPORT_NR = "REPORT_NR";

  /**
   * Attributo RIGA_JOB_ID
   */
  public static final String RIGA_JOB_ID = "RIGA_JOB_ID";

  /**
   * Attributo ID_AZIENDA
   */
  public static final String ID_AZIENDA = "ID_AZIENDA";

  /**
   * Attributo ID_ANNO_PREVC
   */
  public static final String ID_ANNO_PREVC = "ID_ANNO_PREVC";

  /**
   * Attributo ID_NUMERO_PREVC
   */
  public static final String ID_NUMERO_PREVC = "ID_NUMERO_PREVC";

  /**
   * Attributo DATA_PREVC
   */
  public static final String DATA_PREVC = "DATA_PREVC";

  /**
   * Attributo NUMERO_PREVC_FMT
   */
  public static final String NUMERO_PREVC_FMT = "NUMERO_PREVC_FMT";

  /**
   * Attributo R_STABILIMENTO
   */
  public static final String R_STABILIMENTO = "R_STABILIMENTO";

  /**
   * Attributo R_MAGAZZINO
   */
  public static final String R_MAGAZZINO = "R_MAGAZZINO";

  /**
   * Attributo R_CAU_PREVC
   */
  public static final String R_CAU_PREVC = "R_CAU_PREVC";

  /**
   * Attributo R_COMMESSA
   */
  public static final String R_COMMESSA = "R_COMMESSA";

  /**
   * Attributo R_COMMESSA_CA
   */
  public static final String R_COMMESSA_CA = "R_COMMESSA_CA";

  /**
   * Attributo TP_INTEST_PREVC
   */
  public static final String TP_INTEST_PREVC = "TP_INTEST_PREVC";

  /**
   * Attributo R_CLIENTE
   */
  public static final String R_CLIENTE = "R_CLIENTE";

  /**
   * Attributo R_DIVISIONE
   */
  public static final String R_DIVISIONE = "R_DIVISIONE";

  /**
   * Attributo R_ANAGRAFICO
   */
  public static final String R_ANAGRAFICO = "R_ANAGRAFICO";

  /**
   * Attributo R_RUB_CONTATTI
   */
  public static final String R_RUB_CONTATTI = "R_RUB_CONTATTI";

  /**
   * Attributo RAGIONE_SOC_PRVC
   */
  public static final String RAGIONE_SOC_PRVC = "RAGIONE_SOC_PRVC";

  /**
   * Attributo INDIRIZZO_PRVC
   */
  public static final String INDIRIZZO_PRVC = "INDIRIZZO_PRVC";

  /**
   * Attributo LOCALITA_PRVC
   */
  public static final String LOCALITA_PRVC = "LOCALITA_PRVC";

  /**
   * Attributo CAP_PRVC
   */
  public static final String CAP_PRVC = "CAP_PRVC";

  /**
   * Attributo R_NAZIONE_PRVC
   */
  public static final String R_NAZIONE_PRVC = "R_NAZIONE_PRVC";

  /**
   * Attributo R_PROVINCIA_PRVC
   */
  public static final String R_PROVINCIA_PRVC = "R_PROVINCIA_PRVC";

  /**
   * Attributo STATO_EVASIONE
   */
  public static final String STATO_EVASIONE = "STATO_EVASIONE";

  /**
   * Attributo STATO
   */
  public static final String STATO = "STATO";

  /**
   * Attributo RICHIEDENTE_CLI
   */
  public static final String RICHIEDENTE_CLI = "RICHIEDENTE_CLI";

  /**
   * Attributo MAIL_PRVC
   */
  public static final String MAIL_PRVC = "MAIL_PRVC";

  /**
   * Attributo FAX_PRVC
   */
  public static final String FAX_PRVC = "FAX_PRVC";

  /**
   * Attributo ATT_IMPORTO_1
   */
  public static final String ATT_IMPORTO_1 = "ATT_IMPORTO_1";

  /**
   * Attributo ATT_IMPORTO_2
   */
  public static final String ATT_IMPORTO_2 = "ATT_IMPORTO_2";

  /**
   * Attributo ATT_STRINGA_1
   */
  public static final String ATT_STRINGA_1 = "ATT_STRINGA_1";

  /**
   * Attributo ATT_STRINGA_2
   */
  public static final String ATT_STRINGA_2 = "ATT_STRINGA_2";

  /**
   * Attributo ATT_DATA_1
   */
  public static final String ATT_DATA_1 = "ATT_DATA_1";

  /**
   * Attributo ATT_DATA_2
   */
  public static final String ATT_DATA_2 = "ATT_DATA_2";

  /**
   * Attributo NOTA_RICH
   */
  public static final String NOTA_RICH = "NOTA_RICH";

  /**
   * Attributo R_AUT_PREVC
   */
  public static final String R_AUT_PREVC = "R_AUT_PREVC";

  /**
   * Attributo R_RES_PREVC
   */
  public static final String R_RES_PREVC = "R_RES_PREVC";

  /**
   * Attributo R_APP_PREVC
   */
  public static final String R_APP_PREVC = "R_APP_PREVC";

  /**
   * Attributo DATA_CONSEG_RCS
   */
  public static final String DATA_CONSEG_RCS = "DATA_CONSEG_RCS";

  /**
   * Attributo DATA_CONSEG_PRV
   */
  public static final String DATA_CONSEG_PRV = "DATA_CONSEG_PRV";

  /**
   * Attributo SET_CONSEG_RCS
   */
  public static final String SET_CONSEG_RCS = "SET_CONSEG_RCS";

  /**
   * Attributo SET_CONSEG_PRV
   */
  public static final String SET_CONSEG_PRV = "SET_CONSEG_PRV";

  /**
   * Attributo R_VALUTA
   */
  public static final String R_VALUTA = "R_VALUTA";
  public static final String R_VALUTA_AZ = "R_VALUTA_AZ";



  /**
   * Attributo FATTORE_CAMBIO
   */
  public static final String FATTORE_CAMBIO = "FATTORE_CAMBIO";

  /**
   * Attributo R_LISTINO_VEN
   */
  public static final String R_LISTINO_VEN = "R_LISTINO_VEN";

  /**
   * Attributo R_LISTINO_ACQ
   */
  public static final String R_LISTINO_ACQ = "R_LISTINO_ACQ";

  /**
   * Attributo R_AMBIENTE
   */
  public static final String R_AMBIENTE = "R_AMBIENTE";

  /**
   * Attributo REP_COS_ART
   */
  public static final String REP_COS_ART = "REP_COS_ART";

  /**
   * Attributo DATA_RIFERIMENTO
   */
  public static final String DATA_RIFERIMENTO = "DATA_RIFERIMENTO";

  /**
   * Attributo GG_VALIDITA
   */
  public static final String GG_VALIDITA = "GG_VALIDITA";

  /**
   * Attributo DATA_FINE_VALC
   */
  public static final String DATA_FINE_VALC = "DATA_FINE_VALC";

  /**
   * Attributo SETT_FINE_VALC
   */
  public static final String SETT_FINE_VALC = "SETT_FINE_VALC";

  /**
   * Attributo DATA_FINE_VAL
   */
  public static final String DATA_FINE_VAL = "DATA_FINE_VAL";

  /**
   * Attributo SETT_FINE_VAL
   */
  public static final String SETT_FINE_VAL = "SETT_FINE_VAL";

  /**
   * Attributo R_GES_COMMENTI
   */
  public static final String R_GES_COMMENTI = "R_GES_COMMENTI";

  /**
   * Attributo R_DOCUMENTO_MM
   */
  public static final String R_DOCUMENTO_MM = "R_DOCUMENTO_MM";

  /**
   * Attributo NOTE
   */
  public static final String NOTE = "NOTE";

  /**
   * Attributo STP_ALL_TEC
   */
  public static final String STP_ALL_TEC = "STP_ALL_TEC";

  /**
   * Attributo GEN_OFFC
   */
  public static final String GEN_OFFC = "GEN_OFFC";

  /**
   * Attributo GEN_RIG_OFFC
   */
  public static final String GEN_RIG_OFFC = "GEN_RIG_OFFC";

  /**
   * Attributo R_ANNO_OFFC
   */
  public static final String R_ANNO_OFFC = "R_ANNO_OFFC";

  /**
   * Attributo R_NUMERO_OFFC
   */
  public static final String R_NUMERO_OFFC = "R_NUMERO_OFFC";

  /**
   * Attributo VLR_VOCI_PREVC
   */
  public static final String VLR_VOCI_PREVC = "VLR_VOCI_PREVC";

  /**
   * Attributo VLR_ART_PREVC
   */
  public static final String VLR_ART_PREVC = "VLR_ART_PREVC";

  /**
   * Attributo VLR_RISU_PREVC
   */
  public static final String VLR_RISU_PREVC = "VLR_RISU_PREVC";

  /**
   * Attributo VLR_RISM_PREVC
   */
  public static final String VLR_RISM_PREVC = "VLR_RISM_PREVC";

  /**
   * Attributo VLR_TOT_PREVC
   */
  public static final String VLR_TOT_PREVC = "VLR_TOT_PREVC";

  /**
   * Attributo COS_VOCI_PREVC
   */
  public static final String COS_VOCI_PREVC = "COS_VOCI_PREVC";

  /**
   * Attributo COS_ART_PREVC
   */
  public static final String COS_ART_PREVC = "COS_ART_PREVC";

  /**
   * Attributo COS_RISU_PREVC
   */
  public static final String COS_RISU_PREVC = "COS_RISU_PREVC";

  /**
   * Attributo COS_RISM_PREVC
   */
  public static final String COS_RISM_PREVC = "COS_RISM_PREVC";

  /**
   * Attributo COS_TOT_PREVC
   */
  public static final String COS_TOT_PREVC = "COS_TOT_PREVC";

  /**
   * Attributo MDC_VOCI_PREVC
   */
  public static final String MDC_VOCI_PREVC = "MDC_VOCI_PREVC";

  /**
   * Attributo MDC_ART_PREVC
   */
  public static final String MDC_ART_PREVC = "MDC_ART_PREVC";

  /**
   * Attributo MDC_RISU_PREVC
   */
  public static final String MDC_RISU_PREVC = "MDC_RISU_PREVC";

  /**
   * Attributo MDC_RISM_PREVC
   */
  public static final String MDC_RISM_PREVC = "MDC_RISM_PREVC";

  /**
   * Attributo MDC_TOT_PREVC
   */
  public static final String MDC_TOT_PREVC = "MDC_TOT_PREVC";

  /**
   * Attributo FLAG_RIS_UTE_1
   */
  public static final String FLAG_RIS_UTE_1 = "FLAG_RIS_UTE_1";

  /**
   * Attributo FLAG_RIS_UTE_2
   */
  public static final String FLAG_RIS_UTE_2 = "FLAG_RIS_UTE_2";

  /**
   * Attributo FLAG_RIS_UTE_3
   */
  public static final String FLAG_RIS_UTE_3 = "FLAG_RIS_UTE_3";

  /**
   * Attributo FLAG_RIS_UTE_4
   */
  public static final String FLAG_RIS_UTE_4 = "FLAG_RIS_UTE_4";

  /**
   * Attributo FLAG_RIS_UTE_5
   */
  public static final String FLAG_RIS_UTE_5 = "FLAG_RIS_UTE_5";

  /**
   * Attributo STRINGA_RIS_UTE_1
   */
  public static final String STRINGA_RIS_UTE_1 = "STRINGA_RIS_UTE_1";

  /**
   * Attributo STRINGA_RIS_UTE_2
   */
  public static final String STRINGA_RIS_UTE_2 = "STRINGA_RIS_UTE_2";

  /**
   * Attributo NUM_RIS_UTE_1
   */
  public static final String NUM_RIS_UTE_1 = "NUM_RIS_UTE_1";

  /**
   * Attributo NUM_RIS_UTE_2
   */
  public static final String NUM_RIS_UTE_2 = "NUM_RIS_UTE_2";

  /**
   * Attributo WF_CLASS_ID
   */
  public static final String WF_CLASS_ID = "WF_CLASS_ID";

  /**
   * Attributo WF_ID
   */
  public static final String WF_ID = "WF_ID";

  /**
   * Attributo WF_NODE_ID
   */
  public static final String WF_NODE_ID = "WF_NODE_ID";

  /**
   * Attributo WF_SUB_NODE_ID
   */
  public static final String WF_SUB_NODE_ID = "WF_SUB_NODE_ID";
  /**
  * Attributo WF_SUB_NODE_ID
  */
 public static final String WF_DESCRIPTION = "WF_DESCRIPTION";



  /**
   * Attributo PERCENTUALE
   */
  public static final String PERCENTUALE = "PERCENTUALE";
  public static final String PERC_VOCE= "PERC_VOCE";
  public static final String PERC_ART= "PERC_ART";
  public static final String PERC_RISU= "PERC_RISU";
  public static final String PERC_RISM= "PERC_RISM";


  public static final String DESC_AUT_PREVC = "DESC_AUT_PREVC";
  public static final String DESC_RES_PREVC= "DESC_RES_PREVC";
 public static final String DESC_APP_PREVC= "DESC_APP_PREVC";
  public static final String TIPO_INTESTATARIO= "TIPO_INTESTATARIO";

  /**
   * Attributo LIVELLO
   */
  public static final String LIVELLO = "LIVELLO";

  public static final String VLR_VOCI_PREVC_AZ = "VLR_VOCI_PREVC_AZ";
  public static final String VLR_ART_PREVAZ = "VLR_ART_PREVAZ";
  public static final String VLR_RISU_PREVAZ = "VLR_RISU_PREVAZ";
  public static final String VLR_RISM_PREVAZ = "VLR_RISM_PREVAZ";
  public static final String VLR_TOT_PREVAZ = "VLR_TOT_PREVAZ";
  public static final String COS_VOCI_PREVAZ = "COS_VOCI_PREVAZ";
  public static final String COS_ART_PREVAZ = "COS_ART_PREVAZ";
  public static final String COS_RISU_PREVAZ = "COS_RISU_PREVAZ";
  public static final String COS_RISM_PREVAZ = "COS_RISM_PREVAZ";
  public static final String COS_TOT_PREVAZ = "COS_TOT_PREVAZ";
  public static final String MDC_VOCI_PREVAZ = "MDC_VOCI_PREVAZ";
  public static final String MDC_ART_PREVAZ = "MDC_ART_PREVAZ";
  public static final String MDC_RISU_PREVAZ = "MDC_RISU_PREVAZ";
  public static final String MDC_RISM_PREVAZ = "MDC_RISM_PREVAZ";
  public static final String MDC_TOT_PREVAZ = "MDC_TOT_PREVAZ";

    /**
   * Attributo R_UTENTE_CRZ
   */
  public static final String R_UTENTE_CRZ = "R_UTENTE_CRZ";

  /**
   * Attributo R_UTENTE_AGG
   */
  public static final String R_UTENTE_AGG = "R_UTENTE_AGG";

  /**
   * Attributo TIMESTAMP_CRZ
   */
  public static final String TIMESTAMP_CRZ = "TIMESTAMP_CRZ";

  /**
   * Attributo TIMESTAMP_AGG
   */
  public static final String TIMESTAMP_AGG = "TIMESTAMP_AGG";

  /**
   * Attributo R_LINGUA
   */
  public static final String R_LINGUA = "R_LINGUA"; //Fix 19818

  /**
   * Attributo DES_LINGUA
   */
  public static final String DES_LINGUA = "DES_LINGUA"; //Fix 19818
  
  //33048 inizio
  public static final String COMMENTI = "COMMENTI";
  public static final String COMMENTI_PIEDE = "COMMENTI_PIEDE";
  //33048 fine

  /**
   *  TABLE_NAME
   */
  public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "RPT_PREV_COM_TES";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.thera.thip.base.commessa.RptPreventivoCommessaTestata.class.getName();


  /**
   *  getInstance
   * @return TableManager
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public synchronized static TableManager getInstance() throws SQLException {
    if (cInstance == null) {
      cInstance = (TableManager)Factory.createObject(RptPreventivoCommessaTestataTM.class);
    }
    return cInstance;
  }

  /**
   *  RptPrevComTesTM
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public RptPreventivoCommessaTestataTM() throws SQLException {
    super();
  }

  /**
   *  initialize
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  protected void initialize() throws SQLException {
    setTableName(TABLE_NAME);
    setObjClassName(CLASS_NAME);
    init();
  }

  /**
   *  initializeRelation
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  protected void initializeRelation() throws SQLException {
    super.initializeRelation();
    addAttribute("RigaJobId" , RIGA_JOB_ID, "getIntegerObject");
    addAttribute("IdAzienda" , ID_AZIENDA);
    addAttribute("IdAnnoPrevc" , ID_ANNO_PREVC);
    addAttribute("IdNumeroPrevc" , ID_NUMERO_PREVC);
    addAttribute("DataPrevc" , DATA_PREVC);
    addAttribute("NumeroPrevcFmt" , NUMERO_PREVC_FMT);
    addAttribute("IdStabilimento" , R_STABILIMENTO);
    addAttribute("IdMagazzino" , R_MAGAZZINO);
    addAttribute("IdCauPrevc" , R_CAU_PREVC);
    addAttribute("IdCommessa" , R_COMMESSA);
    addAttribute("IdCommessaCa" , R_COMMESSA_CA);
    addAttribute("TpIntestPrevc" , TP_INTEST_PREVC);
    addAttribute("IdCliente" , R_CLIENTE);
    addAttribute("IdDivisione" , R_DIVISIONE);
    addAttribute("IdAnagrafico" , R_ANAGRAFICO);
    addAttribute("IdRubContatti" , R_RUB_CONTATTI);
    addAttribute("RagioneSocPrvc" , RAGIONE_SOC_PRVC);
    addAttribute("IndirizzoPrvc" , INDIRIZZO_PRVC);
    addAttribute("LocalitaPrvc" , LOCALITA_PRVC);
    addAttribute("CapPrvc" , CAP_PRVC);
    addAttribute("IdNazionePrvc" , R_NAZIONE_PRVC);
    addAttribute("IdProvinciaPrvc" , R_PROVINCIA_PRVC);
    addAttribute("StatoEvasione" , STATO_EVASIONE);
    addAttribute("Stato" , STATO);
    addAttribute("RichiedenteCli" , RICHIEDENTE_CLI);
    addAttribute("MailPrvc" , MAIL_PRVC);
    addAttribute("FaxPrvc" , FAX_PRVC);
    addAttribute("AttImporto1" , ATT_IMPORTO_1);
    addAttribute("AttImporto2" , ATT_IMPORTO_2);
    addAttribute("AttStringa1" , ATT_STRINGA_1);
    addAttribute("AttStringa2" , ATT_STRINGA_2);
    addAttribute("AttData1" , ATT_DATA_1);
    addAttribute("AttData2" , ATT_DATA_2);
    addAttribute("NotaRich" , NOTA_RICH);
    addAttribute("IdAutPrevc" , R_AUT_PREVC);
    addAttribute("IdResPrevc" , R_RES_PREVC);
    addAttribute("IdAppPrevc" , R_APP_PREVC);

    addAttribute("DescAutPrevc", DESC_AUT_PREVC);
    addAttribute("DescRevPrevc", DESC_RES_PREVC);
    addAttribute("DescAppPrevc", DESC_APP_PREVC);
    addAttribute("TipoIntestatario", TIPO_INTESTATARIO);


    addAttribute("DataConsegRcs" , DATA_CONSEG_RCS);
    addAttribute("DataConsegPrv" , DATA_CONSEG_PRV);
    addAttribute("SetConsegRcs" , SET_CONSEG_RCS);
    addAttribute("SetConsegPrv" , SET_CONSEG_PRV);
    addAttribute("IdValuta" , R_VALUTA);
    addAttribute("IdValutaAz" , R_VALUTA_AZ);
    addAttribute("FattoreCambio" , FATTORE_CAMBIO);
    addAttribute("IdListinoVen" , R_LISTINO_VEN);
    addAttribute("IdListinoAcq" , R_LISTINO_ACQ);
    addAttribute("Ambiente" , R_AMBIENTE);
    addAttribute("RepCosArt" , REP_COS_ART);
    addAttribute("DataRiferimento" , DATA_RIFERIMENTO);
    addAttribute("GgValidita" , GG_VALIDITA);
    addAttribute("DataFineValc" , DATA_FINE_VALC);
    addAttribute("SettFineValc" , SETT_FINE_VALC);
    addAttribute("DataFineVal" , DATA_FINE_VAL);
    addAttribute("SettFineVal" , SETT_FINE_VAL);
    addAttribute("RGesCommenti" , R_GES_COMMENTI);
    addAttribute("RDocumentoMm" , R_DOCUMENTO_MM);
    addAttribute("Note" , NOTE);
    addAttribute("StpAllTec" , STP_ALL_TEC);
    addAttribute("GenOffc" , GEN_OFFC);
    addAttribute("GenRigOffc" , GEN_RIG_OFFC);
    addAttribute("IdAnnoOffc" , R_ANNO_OFFC);
    addAttribute("NumeroOffc" , R_NUMERO_OFFC);
    addAttribute("VlrVociPrevc" , VLR_VOCI_PREVC);
    addAttribute("VlrArtPrevc" , VLR_ART_PREVC);
    addAttribute("VlrRisuPrevc" , VLR_RISU_PREVC);
    addAttribute("VlrRismPrevc" , VLR_RISM_PREVC);
    addAttribute("VlrTotPrevc" , VLR_TOT_PREVC);
    addAttribute("CosVociPrevc" , COS_VOCI_PREVC);
    addAttribute("CosArtPrevc" , COS_ART_PREVC);
    addAttribute("CosRisuPrevc" , COS_RISU_PREVC);
    addAttribute("CosRismPrevc" , COS_RISM_PREVC);
    addAttribute("CosTotPrevc" , COS_TOT_PREVC);
    addAttribute("MdcVociPrevc" , MDC_VOCI_PREVC);
    addAttribute("MdcArtPrevc" , MDC_ART_PREVC);
    addAttribute("MdcRisuPrevc" , MDC_RISU_PREVC);
    addAttribute("MdcRismPrevc" , MDC_RISM_PREVC);
    addAttribute("MdcTotPrevc" , MDC_TOT_PREVC);
    //
    addAttribute("VlrVociPrevAz", VLR_VOCI_PREVC_AZ);
    addAttribute("VlrArtPrevAz", VLR_ART_PREVAZ);
    addAttribute("VlrRisuPrevAz", VLR_RISU_PREVAZ);
    addAttribute("VlrRismPrevAz", VLR_RISM_PREVAZ);
    addAttribute("VlrTotPrevAz", VLR_TOT_PREVAZ);
    addAttribute("CosVociPrevAz", COS_VOCI_PREVAZ);
    addAttribute("CosArtPrevAz", COS_ART_PREVAZ);
    addAttribute("CosRisuPrevAz", COS_RISU_PREVAZ);
    addAttribute("CosRismPrevAz", COS_RISM_PREVAZ);
    addAttribute("CosTotPrevAz", COS_TOT_PREVAZ);
    addAttribute("MdcVociPrevAz", MDC_VOCI_PREVAZ);
    addAttribute("MdcArtPrevAz", MDC_ART_PREVAZ);
    addAttribute("MdcRisuPrevAz", MDC_RISU_PREVAZ);
    addAttribute("MdcRismPrevAz", MDC_RISM_PREVAZ);
    addAttribute("MdcTotPrevAz", MDC_TOT_PREVAZ);

    addAttribute("Percentuale", PERCENTUALE);

    addAttribute("PercentualeVoce", PERC_VOCE);
    addAttribute("PercentualeArtic", PERC_ART);
    addAttribute("PercentualeRisU", PERC_RISU);
    addAttribute("PercentualeRisM", PERC_RISM);


    addAttribute("FlagRisUte1" , FLAG_RIS_UTE_1);
    addAttribute("FlagRisUte2" , FLAG_RIS_UTE_2);
    addAttribute("FlagRisUte3" , FLAG_RIS_UTE_3);
    addAttribute("FlagRisUte4" , FLAG_RIS_UTE_4);
    addAttribute("FlagRisUte5" , FLAG_RIS_UTE_5);
    addAttribute("StringaRisUte1" , STRINGA_RIS_UTE_1);
    addAttribute("StringaRisUte2" , STRINGA_RIS_UTE_2);
    addAttribute("NumRisUte1" , NUM_RIS_UTE_1);
    addAttribute("NumRisUte2" , NUM_RIS_UTE_2);
    addAttribute("WfClassId" , WF_CLASS_ID);
    addAttribute("WfId" , WF_ID);
    addAttribute("WfNodeId" , WF_NODE_ID);
    addAttribute("WfSubNodeId" , WF_SUB_NODE_ID);
    addAttribute("WfDescription" , WF_DESCRIPTION);
    addAttribute("RUtenteCrz" , R_UTENTE_CRZ);
    addAttribute("RUtenteAgg" , R_UTENTE_AGG);
    addAttribute("TimestampAgg" , TIMESTAMP_AGG);
    //Fix 19818 Inizio
    addAttribute("IdLingua", R_LINGUA);
    addAttribute("DesLingua", DES_LINGUA);
    //Fix 19818 Fine
    //33048 inizio
    addAttribute("Commenti", COMMENTI);
    addAttribute("CommentiPiede", COMMENTI_PIEDE);
    //33048 fine
    addAttribute("BatchJobId" , BATCH_JOB_ID, "getIntegerObject");
    addAttribute("ReportNr" , REPORT_NR, "getIntegerObject");
    addTimestampAttribute("Timestamp" , TIMESTAMP_CRZ);
    setKeys(BATCH_JOB_ID + "," + REPORT_NR + "," + RIGA_JOB_ID);
  }

  /**
   *  init
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  private void init() throws SQLException {
    configure();
  }

}


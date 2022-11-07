/*
 * @(#)RptPreventivoCommessaRigaTM.java
 */

/**
 * RptPrevComRigTM
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 16:53:50
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 16/12/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 33048     04/03/2021    RA       Aggiunto attributo CommentiPiede
 */
package it.thera.thip.base.commessa;

import java.sql.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;

public class RptPreventivoCommessaRigaTM
  extends TableManager
{

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
   * Attributo DET_RIGA_JOB
   */
  public static final String DET_RIGA_JOB = "DET_RIGA_JOB";

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
   * Attributo ID_RIGAC_PRV
   */
  public static final String ID_RIGAC_PRV = "ID_RIGAC_PRV";

  /**
   * Attributo R_RIGAC_PRV
   */
  public static final String R_RIGAC_PRV = "R_RIGAC_PRV";

  /**
   * Attributo SEQUENZA_RIGA
   */
  public static final String SEQUENZA_RIGA = "SEQUENZA_RIGA";

  /**
   * Attributo SPL_RIGA
   */
  public static final String SPL_RIGA = "SPL_RIGA";

  /**
   * Attributo R_COMMESSA
   */
  public static final String R_COMMESSA = "R_COMMESSA";

  /**
   * Attributo R_COMMESSA_APP
   */
  public static final String R_COMMESSA_APP = "R_COMMESSA_APP";

  /**
   * Attributo R_COMMESSA_PRM
   */
  public static final String R_COMMESSA_PRM = "R_COMMESSA_PRM";

  /**
   * Attributo DESCRIZIONE
   */
  public static final String DESCRIZIONE = "DESCRIZIONE";

  /**
   * Attributo DESCR_RIDOTTA
   */
  public static final String DESCR_RIDOTTA = "DESCR_RIDOTTA";

  /**
   * Attributo R_ARTICOLO
   */
  public static final String R_ARTICOLO = "R_ARTICOLO";

  /**
   * Attributo R_VERSIONE
   */
  public static final String R_VERSIONE = "R_VERSIONE";

  /**
   * Attributo R_CONFIGURAZIONE
   */
  public static final String R_CONFIGURAZIONE = "R_CONFIGURAZIONE";

  /**
   * Attributo R_UM_PRM_MAG
   */
  public static final String R_UM_PRM_MAG = "R_UM_PRM_MAG";

  /**
   * Attributo QTA_UM_PRM
   */
  public static final String QTA_UM_PRM = "QTA_UM_PRM";

  /**
   * Attributo DATA_CONSEG_RCS
   */
  public static final String DATA_CONSEG_RCS = "DATA_CONSEG_RCS";

  /**
   * Attributo DATA_CONSEG_PRV
   */
  public static final String DATA_CONSEG_PRV = "DATA_CONSEG_PRV";

  /**
   * Attributo SETT_CONSEG_RCS
   */
  public static final String SETT_CONSEG_RCS = "SETT_CONSEG_RCS";

  /**
   * Attributo SETT_CONSEG_PRV
   */
  public static final String SETT_CONSEG_PRV = "SETT_CONSEG_PRV";

  /**
   * Attributo VLR_LIVELLO
   */
  public static final String VLR_LIVELLO = "VLR_LIVELLO";

  /**
   * Attributo COS_LIVELLO
   */
  public static final String COS_LIVELLO = "COS_LIVELLO";

  /**
   * Attributo MDC_LIVELLO
   */
  public static final String MDC_LIVELLO = "MDC_LIVELLO";

  /**
   * Attributo VLR_LIVELLO_INF
   */
  public static final String VLR_LIVELLO_INF = "VLR_LIVELLO_INF";

  /**
   * Attributo COS_LIVELLO_INF
   */
  public static final String COS_LIVELLO_INF = "COS_LIVELLO_INF";

  /**
   * Attributo MDC_LIVELLO_INF
   */
  public static final String MDC_LIVELLO_INF = "MDC_LIVELLO_INF";

  /**
   * Attributo VLR_TOTALE
   */
  public static final String VLR_TOTALE = "VLR_TOTALE";

  /**
   * Attributo COS_TOTALE
   */
  public static final String COS_TOTALE = "COS_TOTALE";

  /**
   * Attributo MDC_TOTALE
   */
  public static final String MDC_TOTALE = "MDC_TOTALE";

  public static final String VLR_LIVELLO_AZ = "VLR_LIVELLO_AZ";
  public static final String COS_LIVELLO_AZ = "COS_LIVELLO_AZ";
  public static final String MDC_LIVELLO_AZ = "MDC_LIVELLO_AZ";
  public static final String VLR_LIVELLO_INF_AZ = "VLR_LIVELLO_INF_AZ";
  public static final String COS_LIVELLO_INF_AZ = "COS_LIVELLO_INF_AZ";
  public static final String MDC_LIVELLO_INF_AZ = "MDC_LIVELLO_INF_AZ";
  public static final String VLR_TOTALE_AZ = "VLR_TOTALE_AZ";
  public static final String COS_TOTALE_AZ = "COS_TOTALE_AZ";
  public static final String MDC_TOTALE_AZ = "MDC_TOTALE_AZ";

  /**
     /**
    * Attributo STP_ALL_TES
    */
   public static final String STP_ALL_TES = "STP_ALL_TES";

  /**
   * Attributo GEN_RIG_OFFC
   */
  public static final String GEN_RIG_OFFC = "GEN_RIG_OFFC";

  /**
   * Attributo R_ANNO_OFF
   */
  public static final String R_ANNO_OFF = "R_ANNO_OFF";

  /**
   * Attributo R_NUM_OFF
   */
  public static final String R_NUM_OFF = "R_NUM_OFF";

  /**
   * Attributo R_RIGA_OFF
   */
  public static final String R_RIGA_OFF = "R_RIGA_OFF";

  /**
   * Attributo R_GES_COMMENTI
   */
  public static final String R_GES_COMMENTI = "R_GES_COMMENTI";

  /**
   * Attributo R_DOCUMENTO_MM
   */
  public static final String R_DOCUMENTO_MM = "R_DOCUMENTO_MM";

  /**
   * Attributo NOTA
   */
  public static final String NOTA = "NOTA";

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
   * Attributo STATO
   */
  public static final String STATO = "STATO";

  /**
   * Attributo PERCENTUALE
   */
  public static final String PERCENTUALE = "PERCENTUALE";

  /**
   * Attributo LIVELLO
   */
  public static final String LIVELLO = "LIVELLO";

  /**
   * Attributo COMMENTI
   */
  public static final String COMMENTI = "COMMENTI";
  
  public static final String COMMENTI_PIEDE = "COMMENTI_PIEDE";//33048

  /**
   *  TABLE_NAME
   */
  public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "RPT_PREV_COM_RIG";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.thera.thip.base.commessa.RptPreventivoCommessaRiga.class.getName();

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
  public synchronized static TableManager getInstance() throws SQLException
  {
    if (cInstance == null)
    {
      cInstance = (TableManager)Factory.createObject(RptPreventivoCommessaRigaTM.class);
    }
    return cInstance;
  }

  /**
   *  RptPreventivoCommessaRigaTM
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public RptPreventivoCommessaRigaTM() throws SQLException
  {
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
  protected void initialize() throws SQLException
  {
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
  protected void initializeRelation() throws SQLException
  {
    super.initializeRelation();
    addAttribute("DetRigaJob", DET_RIGA_JOB, "getIntegerObject");
    addAttribute("IdAzienda", ID_AZIENDA);
    addAttribute("IdAnnoPrevc", ID_ANNO_PREVC);
    addAttribute("IdNumeroPrevc", ID_NUMERO_PREVC);
    addAttribute("IdRigacPrv", ID_RIGAC_PRV);
    addAttribute("RigacPrv", R_RIGAC_PRV);
    addAttribute("SequenzaRiga", SEQUENZA_RIGA);
    addAttribute("SplRiga", SPL_RIGA);
    addAttribute("RCommessa", R_COMMESSA);
    addAttribute("RCommessaApp", R_COMMESSA_APP);
    addAttribute("RCommessaPrm", R_COMMESSA_PRM);
    addAttribute("Descrizione", DESCRIZIONE);
    addAttribute("DescrRidotta", DESCR_RIDOTTA);
    addAttribute("RArticolo", R_ARTICOLO);
    addAttribute("RVersione", R_VERSIONE);
    addAttribute("RConfigurazione", R_CONFIGURAZIONE);
    addAttribute("RUmPrmMag", R_UM_PRM_MAG);
    addAttribute("QtaUmPrm", QTA_UM_PRM);
    addAttribute("DataConsegRcs", DATA_CONSEG_RCS);
    addAttribute("DataConsegPrv", DATA_CONSEG_PRV);
    addAttribute("SettConsegRcs", SETT_CONSEG_RCS);
    addAttribute("SettConsegPrv", SETT_CONSEG_PRV);
    addAttribute("VlrLivello", VLR_LIVELLO);
    addAttribute("CosLivello", COS_LIVELLO);
    addAttribute("MdcLivello", MDC_LIVELLO);
    addAttribute("VlrLivelloInf", VLR_LIVELLO_INF);
    addAttribute("CosLivelloInf", COS_LIVELLO_INF);
    addAttribute("MdcLivelloInf", MDC_LIVELLO_INF);
    addAttribute("VlrTotale", VLR_TOTALE);
    addAttribute("CosTotale", COS_TOTALE);
    addAttribute("MdcTotale", MDC_TOTALE);
    addAttribute("VlrLivelloAz", "VLR_LIVELLO_AZ");
    addAttribute("CosLivelloAz", "COS_LIVELLO_AZ");
    addAttribute("MdcLivelloAz", "MDC_LIVELLO_AZ");
    addAttribute("VlrLivelloInfAz", "VLR_LIVELLO_INF_AZ");
    addAttribute("CosLivelloInfAz", "COS_LIVELLO_INF_AZ");
    addAttribute("MdcLivelloInfAz", "MDC_LIVELLO_INF_AZ");
    addAttribute("VlrTotaleAz", "VLR_TOTALE_AZ");
    addAttribute("CosTotaleAz", "COS_TOTALE_AZ");
    addAttribute("MdcTotaleAz", "MDC_TOTALE_AZ");
    addAttribute("StpAllTes", STP_ALL_TES);
    addAttribute("GenRigOffc", GEN_RIG_OFFC);
    addAttribute("RAnnoOff", R_ANNO_OFF);
    addAttribute("RNumOff", R_NUM_OFF);
    addAttribute("RRigaOff", R_RIGA_OFF);
    addAttribute("RGesCommenti", R_GES_COMMENTI);
    addAttribute("RDocumentoMm", R_DOCUMENTO_MM);
    addAttribute("Nota", NOTA);
    addAttribute("RUtenteCrz", R_UTENTE_CRZ);
    addAttribute("RUtenteAgg", R_UTENTE_AGG);
    addAttribute("TimestampAgg", TIMESTAMP_AGG);
    addAttribute("Stato", STATO);
    addAttribute("Percentuale", PERCENTUALE);
    addAttribute("Commenti", COMMENTI);
    addAttribute("CommentiPiede", COMMENTI_PIEDE);//33048
    addAttribute("Livello", LIVELLO);

    addAttribute("BatchJobId", BATCH_JOB_ID, "getIntegerObject");
    addAttribute("ReportNr", REPORT_NR, "getIntegerObject");
    addAttribute("RigaJobId", RIGA_JOB_ID, "getIntegerObject");
    addTimestampAttribute("Timestamp", TIMESTAMP_CRZ);
    setKeys(BATCH_JOB_ID + "," + REPORT_NR + "," + RIGA_JOB_ID + "," + DET_RIGA_JOB);
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
  private void init() throws SQLException
  {
    configure();
  }

}

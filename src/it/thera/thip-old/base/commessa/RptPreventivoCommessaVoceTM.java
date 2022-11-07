/*
 * @(#)RptPreventivoCommessaVoceTM.java
 */

/**
 * RptPrevComVoceTM
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 17:11:10
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

public class RptPreventivoCommessaVoceTM extends TableManager {


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
   * Attributo DET_RIGA_JOB2
   */
  public static final String DET_RIGA_JOB2 = "DET_RIGA_JOB2";

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
   * Attributo ID_RIGAV_PRV
   */
  public static final String ID_RIGAV_PRV = "ID_RIGAV_PRV";

  /**
   * Attributo ID_SUB_RIGAV_PRV
   */
  public static final String ID_SUB_RIGAV_PRV = "ID_SUB_RIGAV_PRV";

  /**
   * Attributo ID_SUB_RIGAC_PRV
   */
  public static final String ID_SUB_RIGAC_PRV = "ID_SUB_RIGAC_PRV";

  /**
   * Attributo SPL_RIGA
   */
  public static final String SPL_RIGA = "SPL_RIGA";

  /**
   * Attributo TIPO_RIGAV
   */
  public static final String TIPO_RIGAV = "TIPO_RIGAV";

  /**
   * Attributo SEQUENZA_RIGA
   */
  public static final String SEQUENZA_RIGA = "SEQUENZA_RIGA";

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
   * Attributo TIPO_RISORSA
   */
  public static final String TIPO_RISORSA = "TIPO_RISORSA";

  /**
   * Attributo LIVELLO_RISORSA
   */
  public static final String LIVELLO_RISORSA = "LIVELLO_RISORSA";

  /**
   * Attributo ID_RISORSA
   */
  public static final String ID_RISORSA = "ID_RISORSA";

  /**
   * Attributo DESCRIZIONE
   */
  public static final String DESCRIZIONE = "DESCRIZIONE";

  /**
   * Attributo DESCR_RIDOTTA
   */
  public static final String DESCR_RIDOTTA = "DESCR_RIDOTTA";

  /**
   * Attributo R_SCHEMA_COSTO
   */
  public static final String R_SCHEMA_COSTO = "R_SCHEMA_COSTO";

  /**
   * Attributo R_COMPON_COSTO
   */
  public static final String R_COMPON_COSTO = "R_COMPON_COSTO";

  /**
   * Attributo R_UM_PRM_MAG
   */
  public static final String R_UM_PRM_MAG = "R_UM_PRM_MAG";

  /**
   * Attributo R_UM_SEC_MAG
   */
  public static final String R_UM_SEC_MAG = "R_UM_SEC_MAG";

  /**
   * Attributo R_UM_VEN
   */
  public static final String R_UM_VEN = "R_UM_VEN";

  /**
   * Attributo QTA_PRV_UM_VEN
   */
  public static final String QTA_PRV_UM_VEN = "QTA_PRV_UM_VEN";

  /**
   * Attributo QTA_PRV_UM_PRM
   */
  public static final String QTA_PRV_UM_PRM = "QTA_PRV_UM_PRM";

  /**
   * Attributo QTA_PRV_UM_SEC
   */
  public static final String QTA_PRV_UM_SEC = "QTA_PRV_UM_SEC";

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
   * Attributo BLC_QTA_CMP
   */
  public static final String BLC_QTA_CMP = "BLC_QTA_CMP";

  /**
   * Attributo COEFF_IMP
   */
  public static final String COEFF_IMP = "COEFF_IMP";

  /**
   * Attributo PREZZO
   */
  public static final String PREZZO = "PREZZO";

  /**
   * Attributo PREZZO_EXTRA
   */
  public static final String PREZZO_EXTRA = "PREZZO_EXTRA";

  /**
   * Attributo NO_FATTURA
   */
  public static final String NO_FATTURA = "NO_FATTURA";

  /**
   * Attributo PROVENIENZA_PRZ
   */
  public static final String PROVENIENZA_PRZ = "PROVENIENZA_PRZ";

  /**
   * Attributo TP_PREZZO
   */
  public static final String TP_PREZZO = "TP_PREZZO";

  /**
   * Attributo COSTO_RIFER
   */
  public static final String COSTO_RIFER = "COSTO_RIFER";

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

  public static final String PREZZO_AZ = "PREZZO_AZ";
  public static final String COSTO_RIFER_AZ = "COSTO_RIFER_AZ";
  public static final String VLR_TOTALE_AZ = "VLR_TOTALE_AZ";
  public static final String COS_TOTALE_AZ = "COS_TOTALE_AZ";
  public static final String MDC_TOTALE_AZ = "MDC_TOTALE_AZ";

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
   * Attributo ESC_RIG_OFFC
   */
  public static final String ESC_RIG_OFFC = "ESC_RIG_OFFC";

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
   * Attributo R_DET_RIG_OFF
   */
  public static final String R_DET_RIG_OFF = "R_DET_RIG_OFF";

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
  public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "RPT_PREV_COM_VOCE";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.thera.thip.base.commessa.RptPreventivoCommessaVoce.class.getName();


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
      cInstance = (TableManager)Factory.createObject(RptPreventivoCommessaVoceTM.class);
    }
    return cInstance;
  }

  /**
   *  RptPreventivoCommessaVoceTM
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public RptPreventivoCommessaVoceTM() throws SQLException {
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
    addAttribute("BatchJobId" , BATCH_JOB_ID, "getIntegerObject");
    addAttribute("ReportNr" , REPORT_NR, "getIntegerObject");
    addAttribute("RigaJobId" , RIGA_JOB_ID, "getIntegerObject");
    addAttribute("DetRigaJob" , DET_RIGA_JOB, "getIntegerObject");
    addAttribute("DetRigaJob2" , DET_RIGA_JOB2, "getIntegerObject");
    addAttribute("IdAzienda" , ID_AZIENDA);
    addAttribute("IdAnnoPrevc" , ID_ANNO_PREVC);
    addAttribute("IdNumeroPrevc" , ID_NUMERO_PREVC);
    addAttribute("IdRigacPrv" , ID_RIGAC_PRV);
    addAttribute("IdRigavPrv" , ID_RIGAV_PRV);
    addAttribute("IdSubRigavPrv" , ID_SUB_RIGAV_PRV);
    addAttribute("IdSubRigacPrv" , ID_SUB_RIGAC_PRV);
    addAttribute("SplRiga" , SPL_RIGA);
    addAttribute("TipoRigav" , TIPO_RIGAV);
    addAttribute("SequenzaRiga" , SEQUENZA_RIGA);
    addAttribute("RArticolo" , R_ARTICOLO);
    addAttribute("RVersione" , R_VERSIONE);
    addAttribute("RConfigurazione" , R_CONFIGURAZIONE);
    addAttribute("TipoRisorsa" , TIPO_RISORSA);
    addAttribute("LivelloRisorsa" , LIVELLO_RISORSA);
    addAttribute("IdRisorsa" , ID_RISORSA);
    addAttribute("Descrizione" , DESCRIZIONE);
    addAttribute("DescrRidotta" , DESCR_RIDOTTA);
    addAttribute("RSchemaCosto" , R_SCHEMA_COSTO);
    addAttribute("RComponCosto" , R_COMPON_COSTO);
    addAttribute("RUmPrmMag" , R_UM_PRM_MAG);
    addAttribute("RUmSecMag" , R_UM_SEC_MAG);
    addAttribute("RUmVen" , R_UM_VEN);
    addAttribute("QtaPrvUmVen" , QTA_PRV_UM_VEN);
    addAttribute("QtaPrvUmPrm" , QTA_PRV_UM_PRM);
    addAttribute("QtaPrvUmSec" , QTA_PRV_UM_SEC);
    addAttribute("DataConsegRcs" , DATA_CONSEG_RCS);
    addAttribute("DataConsegPrv" , DATA_CONSEG_PRV);
    addAttribute("SettConsegRcs" , SETT_CONSEG_RCS);
    addAttribute("SettConsegPrv" , SETT_CONSEG_PRV);
    addAttribute("BlcQtaCmp" , BLC_QTA_CMP);
    addAttribute("CoeffImp" , COEFF_IMP);
    addAttribute("Prezzo" , PREZZO);
    addAttribute("PrezzoExtra" , PREZZO_EXTRA);
    addAttribute("NoFattura" , NO_FATTURA);
    addAttribute("ProvenienzaPrz" , PROVENIENZA_PRZ);
    addAttribute("TpPrezzo" , TP_PREZZO);
    addAttribute("CostoRifer" , COSTO_RIFER);
    addAttribute("VlrTotale" , VLR_TOTALE);
    addAttribute("CosTotale" , COS_TOTALE);
    addAttribute("MdcTotale" , MDC_TOTALE);
    addAttribute("PrezzoAz", PREZZO_AZ);
    addAttribute("CostoRiferAz", COSTO_RIFER_AZ);
    addAttribute("VlrTotaleAz", VLR_TOTALE_AZ);
    addAttribute("CosTotaleAz", COS_TOTALE_AZ);
    addAttribute("MdcTotaleAz", MDC_TOTALE_AZ);

    addAttribute("RGesCommenti" , R_GES_COMMENTI);
    addAttribute("RDocumentoMm" , R_DOCUMENTO_MM);
    addAttribute("Nota" , NOTA);
    addAttribute("EscRigOffc" , ESC_RIG_OFFC);
    addAttribute("RAnnoOff" , R_ANNO_OFF);
    addAttribute("RNumOff" , R_NUM_OFF);
    addAttribute("RRigaOff" , R_RIGA_OFF);
    addAttribute("RDetRigOff" , R_DET_RIG_OFF);
    addAttribute("RUtenteCrz" , R_UTENTE_CRZ);
    addAttribute("RUtenteAgg" , R_UTENTE_AGG);
    addAttribute("TimestampAgg" , TIMESTAMP_AGG);
    addAttribute("Stato" , STATO);
    addAttribute("Percentuale", PERCENTUALE);
    addAttribute("Commenti", COMMENTI);
    addAttribute("Livello", LIVELLO);
    addAttribute("CommentiPiede", COMMENTI_PIEDE);//33048
    addTimestampAttribute("Timestamp", TIMESTAMP_CRZ);
    setKeys(BATCH_JOB_ID + "," + REPORT_NR + "," + RIGA_JOB_ID + "," + DET_RIGA_JOB + "," + DET_RIGA_JOB2);
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


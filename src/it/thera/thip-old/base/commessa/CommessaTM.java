package it.thera.thip.base.commessa;

import java.sql.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.persist.*;
import it.thera.thip.cs.*;

/*
 * @(#)CommessaTM.java
 */

/**
 * CommessaTM
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Wizard 12/12/2001 at 13:53:10
 */
/*
 * Revisions:
 * Fix     Date          Owner      Description
 *         12/12/2001    Wizard     Codice generato da Wizard
 *         24/01/2003    PJ         Aggiunto attributo AggiornamentoSaldi (AGGIOR_SALDI)
 * 03463   30/03/2005    A.BOULILA  aggiunto di metodi e di attributi per il modulo di
 *                                  gestione di commesse
 * XXXXX   30/03/2005    A.BOULILA  aggiunto di metodi e di attributi LivelloCmm
 * 05543   12/06/2006    PJ         commessa di contabilità analitica
 * 09625   29/07/2008    MN         Aggiunti attributi ValoreOrdRiorg e RifRigaOrdRiorg per la
 *                                  gestione degli ordini riorganizzati.
 * 19897   30/05/2014    Linda      Aggiunti nuovi attributi.
 * 20785   09/12/2014    Linda      Aggiunti nuovi attributi.
 * 27669   02/07/2018    GN         Gestione commessa modello
 * 29025   20/03/2019    Jackal     Aggiunte colonne TP_PIANO e USA_CONTO_ANT
 * 32044   18/12/2020	 SZ			Aggiunte metodo addCommentHandlerManager() Per il CM da Griglia.	

 * 33950   23/07/2021    RA			Aggiunto colonna DATA_EST_STORICI
 */


public class CommessaTM extends TableManager
{


  /**
   * Attributo ID_AZIENDA
   */
  public static final String ID_AZIENDA = "ID_AZIENDA";

  /**
   * Attributo ID_COMMESSA
   */
  public static final String ID_COMMESSA = "ID_COMMESSA";

  /**
   * Attributo DESCRIZIONE
   */
  public static final String DESCRIZIONE = "DESCRIZIONE";

  /**
   * Attributo DESCR_RIDOTTA
   */
  public static final String DESCR_RIDOTTA = "DESCR_RIDOTTA";

  /**
   * Attributo R_CLIENTE
   */
  public static final String R_CLIENTE = "R_CLIENTE";

  /**
   * Attributo DATA_INIZIO
   */
  public static final String DATA_INIZIO = "DATA_INIZIO";

  /**
   * Attributo DATA_FINE
   */
  public static final String DATA_FINE = "DATA_FINE";

  ////PJ 24/01/2003 - BEGIN

  /**
   * Attributo AGGIOR_SALDI
   */
  public static final String AGGIOR_SALDI = "AGGIOR_SALDI";

  ////PJ 24/01/2003 - END

  /**
   * Attributo STATO
   */
  public static final String STATO = "STATO";

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
   * Attributo TP_PIANO
   */
  public static final String TP_PIANO = "TP_PIANO";

  /**
   * Attributo USA_CONTO_ANT
   */
  public static final String USA_CONTO_ANT = "USA_CONTO_ANT";

  /**
   *  TABLE_NAME
   */
  public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "COMMESSE";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.thera.thip.base.commessa.Commessa.class.getName();

  // Inizio 03463 A.BOULILA

  /**
   * Attributo R_COMMESSA_PRM
   */
  public static final String R_COMMESSA_PRM = "R_COMMESSA_PRM";

  /**
   * Attributo R_COMMESSA_APP
   */
  public static final String R_COMMESSA_APP = "R_COMMESSA_APP";
  
  //Fix 27669 inizio
  /**
   * Attributo R_COMMESSA_MOD
   */
  public static final String R_COMMESSA_MOD = "R_COMMESSA_MOD";
  //Fix 27669 fine
  
  /**
   * Attributo R_TIPO_COMMESSA
   */
  public static final String R_TIPO_COMMESSA = "R_TIPO_COMMESSA";

  /**
   * Attributo R_AMBIENTE_CMM
   */
  public static final String R_AMBIENTE_CMM = "R_AMBIENTE_CMM";

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
   * Attributo R_STABILIMENTO
   */
  public static final String R_STABILIMENTO = "R_STABILIMENTO";

  /**
   * Attributo STATO_AVANZAMENTO
   */
  public static final String STATO_AVANZAMENTO = "STATO_AVANZAMENTO";

  /**
   * Attributo QTA_UM_PRM
   */
  public static final String QTA_UM_PRM = "QTA_UM_PRM";

  /**
   * Attributo R_UM_PRM_MAG
   */
  public static final String R_UM_PRM_MAG = "R_UM_PRM_MAG";

  /**
   * Attributo R_RES_COMMES
   */
  public static final String R_RES_COMMES = "R_RES_COMMES";

  /**
   * Attributo R_RES_PREVEN
   */
  public static final String R_RES_PREVEN = "R_RES_PREVEN";

  /**
   * Attributo R_ANNO_ORDINE
   */
  public static final String R_ANNO_ORDINE = "R_ANNO_ORDINE";

  /**
   * Attributo R_NUMERO_ORD
   */
  public static final String R_NUMERO_ORD = "R_NUMERO_ORD";

  /**
   * Attributo R_RIGA_ORD
   */
  public static final String R_RIGA_ORD = "R_RIGA_ORD";

  /**
   * Attributo R_DET_RIGA_ORD
   */
  public static final String R_DET_RIGA_ORD = "R_DET_RIGA_ORD";

  /**
   * Attributo DATA_CONFERMA
   */
  public static final String DATA_CONFERMA = "DATA_CONFERMA";

  /**
   * Attributo DATA_INIZIO_PREV
   */
  public static final String DATA_INIZIO_PREV = "DATA_INIZIO_PREV";

  /**
   * Attributo DATA_FINE_PREV
   */
  public static final String DATA_FINE_PREV = "DATA_FINE_PREV";

  /**
   * Attributo DATA_PRIMA_ATT
   */
  public static final String DATA_PRIMA_ATT = "DATA_PRIMA_ATT";

  /**
   * Attributo DATA_ULTIM_ATT
   */
  public static final String DATA_ULTIM_ATT = "DATA_ULTIM_ATT";

  /**
   * Attributo DATA_CHIUS_TEC
   */
  public static final String DATA_CHIUS_TEC = "DATA_CHIUS_TEC";

  /**
   * Attributo DATA_CHIUS_OPE
   */
  public static final String DATA_CHIUS_OPE = "DATA_CHIUS_OPE";

  /**
   * Attributo PIANO_FATTUR
   */
  public static final String PIANO_FATTUR = "PIANO_FATTUR";

  /**
   * Attributo CHIUDI_ORDINE
   */
  public static final String CHIUDI_ORDINE = "CHIUDI_ORDINE";

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
   * Attributo NOTE
   */
  public static final String NOTE = "NOTE";

  /**
   * Attributo R_DOCUMENTO_MM
   */
  public static final String R_DOCUMENTO_MM = "R_DOCUMENTO_MM";

  /**
   * Attributo R_GES_COMMENTI
   */
  public static final String R_GES_COMMENTI = "R_GES_COMMENTI";

  /**
   * Attributo ATTRIBUTI_ESTEND
   */
  public static final String ATTRIBUTI_ESTEND = "ATTRIBUTI_ESTEND";

  // Fine 03463 A.BOULILA

  // Inizio XXXXX A.BOULILA
  /**
   * Attributo LIVELLO_CMM
   */
  public static final String LIVELLO_CMM = "LIVELLO_CMM";

  // Fine XXXXX A.BOULILA

  //5543 - inizio
  public static final String R_COMMESSA_CA = "R_COMMESSA_CA";
  //5543 - fine

  // Iniizo 9625
  public static final String VLR_ORDINATO_RIO = "VLR_ORDINATO_RIO";

  public static final String RIF_RIGORD_RIORG = "RIF_RIGORD_RIORG";

  // Fine 9625

  //Fix 19897 inizio
  public static final String GEST_CIG_CUP = "GEST_CIG_CUP";

  public static final String ID_DOCUMENTO = "ID_DOCUMENTO";

  public static final String DATA_DOC = "DATA_DOC";

  public static final String NUM_ITEM = "NUM_ITEM";

  public static final String ID_COMM_CONV = "ID_COMM_CONV";

  public static final String CODICE_CUP = "CODICE_CUP";

  public static final String CODICE_CIG = "CODICE_CIG";
 //Fix 19897 fine

 //Fix 20785 inizio
 public static final String ID_DOC_ORDACQ = "ID_DOC_ORDACQ";

 public static final String DATA_DOC_ORDACQ = "DATA_DOC_ORDACQ";

 public static final String ID_COMM_CONV_OA = "ID_COMM_CONV_OA";

 public static final String ID_DOC_CON = "ID_DOC_CON";

 public static final String DATA_DOC_CON = "DATA_DOC_CON";

 public static final String ID_COMM_CONV_CON = "ID_COMM_CONV_CON";

 public static final String ID_DOC_RIC = "ID_DOC_RIC";

 public static final String DATA_DOC_RIC = "DATA_DOC_RIC";

 public static final String ID_COMM_CONV_RIC = "ID_COMM_CONV_RIC";

 public static final String ID_DOC_FAT = "ID_DOC_FAT";

 public static final String DATA_DOC_FAT = "DATA_DOC_FAT";

 public static final String ID_COMM_CONV_FAT = "ID_COMM_CONV_FAT";
 //Fix 20785 fine

 public static final String DATA_EST_STORICI = "DATA_EST_STORICI";//33950

  /**
   *  getInstance
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    CodeGen     Codice generato da CodeGenerator
   *
   */
  public synchronized static TableManager getInstance() throws SQLException
  {
    if (cInstance == null) {
            cInstance = (TableManager) Factory.createObject(CommessaTM.class);
        }
        return cInstance;
  }

  /**
   *  CommessaTM
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    CodeGen     Codice generato da CodeGenerator
   *
   */
  public CommessaTM() throws SQLException
  {
    super();
  }

  /**
   *  initialize
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    CodeGen     Codice generato da CodeGenerator
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
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  protected void initializeRelation() throws SQLException
  {
    super.initializeRelation();
    addAttribute("IdCommessa" , ID_COMMESSA);
    addAttribute("IdAzienda" , ID_AZIENDA);
    addAttribute("IdCliente" , R_CLIENTE);

    ////PJ 24/01/2003 - BEGIN
    addAttribute("AggiornamentoSaldi" , AGGIOR_SALDI);
    ////PJ 24/01/2003 - END

    // Inizio 03463 A.BOULILA
    super.initializeRelation();
    addAttribute("StatoAvanzamento" , STATO_AVANZAMENTO);
    addAttribute("QtaUmPrm" , QTA_UM_PRM);
    addAttribute("IdAnnoOrdine" , R_ANNO_ORDINE);
    addAttribute("IdNumeroOrdine" , R_NUMERO_ORD);
    addAttribute("IdRigaOrdine" , R_RIGA_ORD);
    addAttribute("IdDetRigaOrdine" , R_DET_RIGA_ORD);
    addAttribute("DataApertura" , DATA_INIZIO);
    addAttribute("DataChiusura" , DATA_FINE);
    addAttribute("DataConferma" , DATA_CONFERMA);
    addAttribute("DataInizioPrevista" , DATA_INIZIO_PREV);
    addAttribute("DataFinePrevista" , DATA_FINE_PREV);
    addAttribute("DataPrimaAtt" , DATA_PRIMA_ATT);
    addAttribute("DataUltimAtt" , DATA_ULTIM_ATT);
    addAttribute("DataChiusTec" , DATA_CHIUS_TEC);
    addAttribute("DataChiusOpe" , DATA_CHIUS_OPE);
    addAttribute("PianoFatturazione" , PIANO_FATTUR);
    addAttribute("ChiudiOrdUltimaFat" , CHIUDI_ORDINE);
    addAttribute("Note" , NOTE);
    addAttribute("IdAmbienteCommessa" , R_AMBIENTE_CMM);
    addAttribute("IdArticolo" , R_ARTICOLO);
    addAttribute("IdVersione" , R_VERSIONE);
    addAttribute("IdCommessaAppartenenza" , R_COMMESSA_APP);
    addAttribute("IdCommessaPrincipale" , R_COMMESSA_PRM);
    addAttribute("IdCommessaModello" , R_COMMESSA_MOD); //Fix 27669
    addAttribute("IdConfigurazione" , R_CONFIGURAZIONE);
    addAttribute("IdResponsabileCommessa" , R_RES_COMMES);
    addAttribute("IdResponsabilePreventivaz" , R_RES_PREVEN);
    addAttribute("IdDocumentoMM" , R_DOCUMENTO_MM);
    addAttribute("IdStabilimento" , R_STABILIMENTO);
    addAttribute("IdTipoCommessa" , R_TIPO_COMMESSA);
    addAttribute("IdUmPrmMag" , R_UM_PRM_MAG);
    addAttribute("LivelloCommessa" , LIVELLO_CMM, "getIntegerObject");

    //5543 - inizio
    addAttribute("IdCommessaCA" , R_COMMESSA_CA);
    //5543 - fine
    // Inizui 9625
    addAttribute("ValoreOrdRiorg" , VLR_ORDINATO_RIO);
    addAttribute("RifRigaOrdRiorg" , RIF_RIGORD_RIORG);
    // Fine 9625

    //Fix 19897 inizio
    addAttribute("TipoGestioneCigCup" , GEST_CIG_CUP);
    addAttribute("NumeroDocumento" , ID_DOCUMENTO);
    addAttribute("DataDocumento" , DATA_DOC);
    addAttribute("NumeroItem" , NUM_ITEM);
    addAttribute("IdCommConven" , ID_COMM_CONV);
    addAttribute("CodiceCUP" , CODICE_CUP);
    addAttribute("CodiceCIG" , CODICE_CIG);
    //Fix 19897 fine
    //Fix 20785 inizio
    addAttribute("NumeroDocOrdAcq", ID_DOC_ORDACQ);
    addAttribute("DataDocOrdAcq", DATA_DOC_ORDACQ);
    addAttribute("IdCommConvOrdAcq", ID_COMM_CONV_OA);
    addAttribute("NumeroDocContratto", ID_DOC_CON);
    addAttribute("DataDocContratto", DATA_DOC_CON);
    addAttribute("IdCommConvContratto", ID_COMM_CONV_CON);
    addAttribute("NumeroDocRicezione", ID_DOC_RIC);
    addAttribute("DataDocRicezione", DATA_DOC_RIC);
    addAttribute("IdCommConvRicezione", ID_COMM_CONV_RIC);
    addAttribute("NumeroDocFatColl", ID_DOC_FAT);
    addAttribute("DataDocFatColl", DATA_DOC_FAT);
    addAttribute("IdCommConvFatColl", ID_COMM_CONV_FAT);
    //Fix 20785 fine

    addComponent("WfStatus", WfStatusExtendedTTM.class);

    //Fix 32044 Inizio
    addCommentHandlerManager();
    /*
    addComponent("CommentHandlerManager", CommentHandlerManagerTTM.class);
    changeAttributeColumn(new String[] {"CommentHandlerManager", "Key"}, R_GES_COMMENTI);
    */
    //Fix 32044 Fine

//    addComponent("DatePreviste", DateRangeTTM.class);
//    changeAttributeColumn(new String[] {"DatePreviste", "DBEndDate"}, DATA_FINE_PREV);
//    changeAttributeColumn(new String[] {"DatePreviste", "DBStartDate"}, DATA_INIZIO_PREV);

    addComponent("AttributiEstendibili", ExtensibleAttributeTTM.class);
    changeAttributeColumn(new String[]{"AttributiEstendibili","FormattedString"}, ATTRIBUTI_ESTEND);

   // addTimestampAttribute("Timestamp" , TIMESTAMP_AGG);
    addComponent("Descrizione", DescrizioneTTM.class);
    addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
    //addComponent("Validita", ValiditaCommessaTTM.class);
    //changeAttributeColumn(new String[] {"Validita", "DBEndDate"}, DATA_FINE);
    //changeAttributeColumn(new String[] {"Validita", "DBStartDate"}, DATA_INIZIO);
    // Fine 03463 A.BOULILA
    setTimestampColumn(TIMESTAMP_AGG);
    ((DatiComuniEstesiTTM)getTransientTableManager("DatiComuniEstesi")).setExcludedColums();
  
    addAttribute("TipoPiano", TP_PIANO);
    addAttribute("UtilizzaContoAnticipi", USA_CONTO_ANT);
    addAttribute("DataEstrazioneStorici", DATA_EST_STORICI);//33950
    
    setKeys(ID_AZIENDA + "," + ID_COMMESSA);
  }

  /**
   *  init
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  private void init() throws SQLException
  {
    configure();
  }
  
  
  //Fix 32044 Inizio
  public void addCommentHandlerManager() {
	    addComponent("CommentHandlerManager", CommentHandlerManagerTTM.class);
	    changeAttributeColumn(new String[] {"CommentHandlerManager", "Key"}, R_GES_COMMENTI);

  }
  //Fix 32044 Fine

}


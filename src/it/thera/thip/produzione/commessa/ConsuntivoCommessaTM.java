package it.thera.thip.produzione.commessa;
import java.sql.SQLException;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.TableManager;

import it.thera.thip.cs.DatiComuniEstesiTTM;

/*
 * @(#)ConsuntivoCommessaTM.java
 */

/**
 * ConsuntivoCommessaTM
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/08/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 33950   18/08/2021    RA		    Prima struttura
 */

public class ConsuntivoCommessaTM extends TableManager {

   /**
    * Attributo ID_AZIENDA
    */
   public static final String ID_AZIENDA = "ID_AZIENDA";

   /**
    * Attributo ID_CONSUNTIVO
    */
   public static final String ID_CONSUNTIVO = "ID_CONSUNTIVO";

   /**
    * Attributo R_COMMESSA
    */
   public static final String R_COMMESSA = "R_COMMESSA";

   /**
    * Attributo LIVELLO_CMM
    */
   public static final String LIVELLO_CMM = "LIVELLO_CMM";


   /**
    * Attributo USA_DATA_ESTSTOR
    */
   public static final String USA_DATA_ESTSTOR = "USA_DATA_ESTSTOR";

   /**
    * Attributo DATA_RIFER
    */
   public static final String DATA_RIFERENTO = "DATA_RIFERENTO";

   /**
    * Attributo STATO_AV
    */
   public static final String STATO_AV = "STATO_AV";

   /**
    * Attributo DESCRIZIONE
    */
   public static final String DESCRIZIONE = "DESCRIZIONE";

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
    * Attributo QTA_UM_PRM
    */
   public static final String QTA_UM_PRM = "QTA_UM_PRM";

   /**
    * Attributo R_UM_PRM_MAG
    */
   public static final String R_UM_PRM_MAG = "R_UM_PRM_MAG";

   /**
    * Attributo R_COMMESSA_APP
    */
   public static final String R_COMMESSA_APP = "R_COMMESSA_APP";

   /**
    * Attributo R_COMPCOS_TOT
    */
   public static final String R_COMPCOS_TOT = "R_COMPCOS_TOT";

   /**
    * Attributo R_COMMESSA_PRM
    */
   public static final String R_COMMESSA_PRM = "R_COMMESSA_PRM";

   /**
    * Attributo CONSOLIDATO
    */
   public static final String CONSOLIDATO = "CONSOLIDATO";

   /**
    * Attributo EST_ORDINI
    */
   public static final String EST_ORDINI = "EST_ORDINI";

   /**
    * Attributo EST_RICHIESTE
    */
   public static final String EST_RICHIESTE = "EST_RICHIESTE";

   /**
    * Attributo COSTO_RIFERIMENTO
    */
   public static final String COSTO_RIFERIMENTO = "COSTO_RIFERIMENTO";

   /**
    * Attributo COSTO_PRIMO
    */
   public static final String COSTO_PRIMO = "COSTO_PRIMO";

   /**
    * Attributo COSTO_INDUSTRIALE
    */
   public static final String COSTO_INDUSTRIALE = "COSTO_INDUSTRIALE";

   /**
    * Attributo COSTO_GENERALE
    */
   public static final String COSTO_GENERALE = "COSTO_GENERALE";

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
    *  TABLE_NAME
    */
   public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "CONSUNTIVO_CMM";

   /**
    *  instance
    */
   private static TableManager cInstance;

   /**
    *  CLASS_NAME
    */
   private static final String CLASS_NAME = it.thera.thip.produzione.commessa.ConsuntivoCommessa.class.getName();

   public synchronized static TableManager getInstance() throws SQLException {
      if (cInstance == null) {
         cInstance = (TableManager) Factory.createObject(ConsuntivoCommessaTM.class);
      }
      return cInstance;
   }

   public ConsuntivoCommessaTM() throws SQLException {
      super();
   }

   protected void initialize() throws SQLException {
      setTableName(TABLE_NAME);
      setObjClassName(CLASS_NAME);
      init();
   }

   protected void initializeRelation() throws SQLException {
      super.initializeRelation();
      addAttribute("IdAzienda", ID_AZIENDA);
      addAttribute("IdConsuntivo", ID_CONSUNTIVO, "getIntegerObject");
      addAttribute("IdCommessa", R_COMMESSA);
      addAttribute("LivelloCommessa", LIVELLO_CMM, "getIntegerObject");
      addAttribute("UsaDataEstrazioneStorici", USA_DATA_ESTSTOR);
      addAttribute("DataRiferimento", DATA_RIFERENTO);
      addAttribute("StatoAvanzamento", STATO_AV);
      addAttribute("Descrizione", DESCRIZIONE);
      addAttribute("IdArticolo", R_ARTICOLO);
      addAttribute("IdVersione", R_VERSIONE, "getIntegerObject");
      addAttribute("IdConfigurazione", R_CONFIGURAZIONE, "getIntegerObject");
      addAttribute("IdStabilimento", R_STABILIMENTO);
      addAttribute("QuantitaPrm", QTA_UM_PRM);
      addAttribute("IdUMPrmMag", R_UM_PRM_MAG);
      addAttribute("IdCommessaApp", R_COMMESSA_APP);
      addAttribute("IdComponenteTotali", R_COMPCOS_TOT);
      addAttribute("IdCommessaPrm", R_COMMESSA_PRM);
      addAttribute("Consolidato", CONSOLIDATO);
      addAttribute("EstrazioneOrdini", EST_ORDINI);
      addAttribute("EstrazioneRichieste", EST_RICHIESTE);
      addAttribute("CostoRiferimento", COSTO_RIFERIMENTO);
      addAttribute("CostoPrimo", COSTO_PRIMO);
      addAttribute("CostoIndustriale", COSTO_INDUSTRIALE);
      addAttribute("CostoGenerale", COSTO_GENERALE);

      addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
      setTimestampColumn(TIMESTAMP_AGG);
      ((DatiComuniEstesiTTM) getTransientTableManager("DatiComuniEstesi")).setExcludedColums();

      setKeys(ID_AZIENDA + "," + ID_CONSUNTIVO + "," + R_COMMESSA);
   }

   private void init() throws SQLException {
      configure();
   }

}
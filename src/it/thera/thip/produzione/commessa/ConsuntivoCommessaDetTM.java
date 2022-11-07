package it.thera.thip.produzione.commessa;
import java.sql.SQLException;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.AbstractTableManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.TableManager;

import it.thera.thip.cs.DatiComuniEstesiTTM;
/**
 * ConsuntivoCommessaDetTM
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/08/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 33950   18/08/2021    RA		    Prima struttura
 */
public class ConsuntivoCommessaDetTM extends TableManager {

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
    * Attributo ID_COMPON_COSTO
    */
   public static final String ID_COMPON_COSTO = "ID_COMPON_COSTO";

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
   public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "CONS_CMM_DET";

   /**
    *  instance
    */
   private static TableManager cInstance;

   /**
    *  CLASS_NAME
    */
   private static final String CLASS_NAME = it.thera.thip.produzione.commessa.ConsuntivoCommessaDet.class.getName();

   public synchronized static TableManager getInstance() throws SQLException {
      if (cInstance == null) {
         cInstance = (TableManager)Factory.createObject(ConsuntivoCommessaDetTM.class);
      }
      return cInstance;
   }

   public ConsuntivoCommessaDetTM() throws SQLException {
      super();
   }

   protected void initialize() throws SQLException {
      setTableName(TABLE_NAME);
      setObjClassName(CLASS_NAME);
      init();
   }

   protected void initializeRelation() throws SQLException {
      super.initializeRelation();
      addAttribute("IdAzienda" , ID_AZIENDA);
      addAttribute("IdConsuntivo" , ID_CONSUNTIVO);
      addAttribute("IdCommessa" , R_COMMESSA);
      addAttribute("IdComponCosto" , ID_COMPON_COSTO);

      addComponent("Consolidato", CostiCommessaDetGruppoTTM.class, AbstractTableManager.MOD_PREFIX, "CNS_");
      addComponent("Richiesto", CostiCommessaDetGruppoTTM.class, AbstractTableManager.MOD_PREFIX, "RCS_");
      addComponent("Ordinato", CostiCommessaDetGruppoTTM.class, AbstractTableManager.MOD_PREFIX, "ORD_");
      addComponent("Effettuato", CostiCommessaDetGruppoTTM.class, AbstractTableManager.MOD_PREFIX, "EFF_");
      addComponent("Totale", CostiCommessaDetGruppoTTM.class, AbstractTableManager.MOD_PREFIX, "TOT_");

      addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
      setTimestampColumn(TIMESTAMP_AGG);
      ((DatiComuniEstesiTTM)getTransientTableManager("DatiComuniEstesi")).setExcludedColums();

      setKeys(ID_AZIENDA + "," + ID_CONSUNTIVO + "," + R_COMMESSA + "," + ID_COMPON_COSTO);
   }

   private void init() throws SQLException {
      configure();
   }

}


package it.thera.thip.produzione.commessa;
import java.sql.SQLException;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.TableManager;

import it.thera.thip.cs.DatiComuniEstesiTTM;
/**
 * BudgetCommessaDetTM
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/10/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34585   29/10/2021    RA		    Prima struttura
 */
public class BudgetCommessaDetTM extends TableManager {


   /**
    * Attributo ID_AZIENDA
    */
   public static final String ID_AZIENDA = "ID_AZIENDA";

   /**
    * Attributo ID_BUDGET
    */
   public static final String ID_BUDGET = "ID_BUDGET";

   /**
    * Attributo ID_COMMESSA
    */
   public static final String ID_COMMESSA = "ID_COMMESSA";

   /**
    * Attributo ID_COMPON_COSTO
    */
   public static final String ID_COMPON_COSTO = "ID_COMPON_COSTO";

   /**
    * Attributo COS_LIV
    */
   public static final String COS_LIV = "COS_LIV";

   /**
    * Attributo COS_LINF
    */
   public static final String COS_LINF = "COS_LINF";

   /**
    * Attributo COS_TOT
    */
   public static final String COS_TOT = "COS_TOT";

   /**
    * Attributo TEMPO_LIV
    */
   public static final String TEMPO_LIV = "TEMPO_LIV";

   /**
    * Attributo TEMPO_LINF
    */
   public static final String TEMPO_LINF = "TEMPO_LINF";

   /**
    * Attributo TEMPO_TOT
    */
   public static final String TEMPO_TOT = "TEMPO_TOT";

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
   public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "BUDGET_CMM_DET";

   /**
    *  instance
    */
   private static TableManager cInstance;

   /**
    *  CLASS_NAME
    */
   private static final String CLASS_NAME = it.thera.thip.produzione.commessa.BudgetCommessaDet.class.getName();

   public synchronized static TableManager getInstance() throws SQLException {
      if (cInstance == null) {
         cInstance = (TableManager)Factory.createObject(BudgetCommessaDetTM.class);
      }
      return cInstance;
   }

   public BudgetCommessaDetTM() throws SQLException {
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
      addAttribute("IdBudget" , ID_BUDGET);
      addAttribute("IdCommessa" , ID_COMMESSA);
      addAttribute("IdComponCosto" , ID_COMPON_COSTO);
      addAttribute("CostoLivello", COS_LIV);
      addAttribute("CostoLivelloInf", COS_LINF);
      addAttribute("CostoTotale", COS_TOT); 
      addAttribute("TempoLivello", TEMPO_LIV);
      addAttribute("TempoLivelloInf", TEMPO_LINF);
      addAttribute("TempoTotale", TEMPO_TOT); 

      addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
      setTimestampColumn(TIMESTAMP_AGG);
      ((DatiComuniEstesiTTM)getTransientTableManager("DatiComuniEstesi")).setExcludedColums();

      setKeys(ID_AZIENDA + "," + ID_BUDGET + "," + ID_COMMESSA + "," + ID_COMPON_COSTO);
   }

   private void init() throws SQLException {
      configure();
   }

}


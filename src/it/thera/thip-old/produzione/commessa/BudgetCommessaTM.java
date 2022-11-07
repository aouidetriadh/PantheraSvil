package it.thera.thip.produzione.commessa;
import java.sql.SQLException;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.TableManager;

import it.thera.thip.cs.DatiComuniEstesiTTM;

/*
 * @(#)BudgetCommessaTM.java
 */

/**
 * BudgetCommessaTM
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/10/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34585   29/10/2021    RA		    Prima struttura
 */

public class BudgetCommessaTM extends TableManager {
	
  /**
   * Attributo ID_AZIENDA
   */
  public static final String ID_AZIENDA = "ID_AZIENDA";

  /**
   * Attributo ID_BUDGET
   */
  public static final String ID_BUDGET = "ID_BUDGET";

  /**
   * Attributo R_COMMESSA
   */
  public static final String ID_COMMESSA = "ID_COMMESSA";

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
   * Attributo R_ANNO_PRV
   */
  public static final String R_ANNO_PRV = "R_ANNO_PRV";

  /**
   * Attributo R_NUM_PRV
   */
  public static final String R_NUM_PRV = "R_NUM_PRV";
  
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
  public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "BUDGET_CMM";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.thera.thip.produzione.commessa.BudgetCommessa.class.getName();

  public synchronized static TableManager getInstance() throws SQLException {
	  if (cInstance == null) {
		  cInstance = (TableManager) Factory.createObject(BudgetCommessaTM.class);
	  }
	  return cInstance;
  }
	
  public BudgetCommessaTM() throws SQLException {
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
	  addAttribute("IdBudget", ID_BUDGET, "getIntegerObject");
	  addAttribute("IdCommessa", ID_COMMESSA);
	  addAttribute("DataRiferimento", DATA_RIFERENTO);
	  addAttribute("StatoAvanzamento", STATO_AV);
	  addAttribute("IdComponenteTotali", R_COMPCOS_TOT);
	  addAttribute("Descrizione", DESCRIZIONE);
	  addAttribute("IdArticolo", R_ARTICOLO);
	  addAttribute("IdVersione", R_VERSIONE, "getIntegerObject");
	  addAttribute("IdConfigurazione", R_CONFIGURAZIONE, "getIntegerObject");
	  addAttribute("IdStabilimento", R_STABILIMENTO);
	  addAttribute("QuantitaPrm", QTA_UM_PRM);
	  addAttribute("IdUMPrmMag", R_UM_PRM_MAG);
	  addAttribute("IdCommessaApp", R_COMMESSA_APP);	  
	  addAttribute("IdCommessaPrm", R_COMMESSA_PRM);
	  addAttribute("IdAnnoPreventivo", R_ANNO_PRV);
	  addAttribute("IdNumeroPreventivo", R_NUM_PRV);
	  addAttribute("CostoRiferimento", COSTO_RIFERIMENTO);
	  addAttribute("CostoPrimo", COSTO_PRIMO);
	  addAttribute("CostoIndustriale", COSTO_INDUSTRIALE);
	  addAttribute("CostoGenerale", COSTO_GENERALE);
	  
	  addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
	  setTimestampColumn(TIMESTAMP_AGG);
	  ((DatiComuniEstesiTTM) getTransientTableManager("DatiComuniEstesi")).setExcludedColums();
	  
	  setKeys(ID_AZIENDA + "," + ID_BUDGET + "," + ID_COMMESSA);
  }
	
  private void init() throws SQLException {
	  configure();
  }

}
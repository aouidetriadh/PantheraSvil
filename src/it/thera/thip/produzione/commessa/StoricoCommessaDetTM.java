package it.thera.thip.produzione.commessa;
import com.thera.thermfw.persist.*;
import java.sql.*;
import com.thera.thermfw.base.*;
import it.thera.thip.cs.*;

/*
 * @(#)StoricoCommessaDetTM.java
 */

/**
 * StoricoCommessaDetTM
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 26/07/2021
 */
/*
 * Revisions:
 * Number  Date          Owner           Description
 * 33950   26/07/2021    RA				 Prima struttura
 */

public class StoricoCommessaDetTM extends TableManager {


  /**
   * Attributo ID_AZIENDA
   */
  public static final String ID_AZIENDA = "ID_AZIENDA";

  /**
   * Attributo ID_PROGR_STORIC
   */
  public static final String ID_PROGR_STORIC = "ID_PROGR_STORIC";

  /**
   * Attributo ID_COMPON_COSTO
   */
  public static final String ID_COMPON_COSTO = "ID_COMPON_COSTO";
  
  /**
   * Attributo TIPO_DETTAGLIO
   */
  public static final String TIPO_DETTAGLIO = "TIPO_DETTAGLIO";
  
  /**
   * Attributo ID_COMMESSA
   */
  public static final String ID_COMMESSA = "ID_COMMESSA";

  /**
   * Attributo COSTO_LIVELLO
   */
  public static final String COSTO_LIVELLO = "COSTO_LIVELLO";

  /**
   * Attributo COSTO_LIVELLI_INF
   */
  public static final String COSTO_LIVELLI_INF = "COSTO_LIVELLI_INF";

  /**
   * Attributo COSTO_TOTALE
   */
  public static final String COSTO_TOTALE = "COSTO_TOTALE";

  /**
   * Attributo TEMPO_LIVELLO
   */
  public static final String TEMPO_LIVELLO = "TEMPO_LIVELLO";
  
  /**
   * Attributo TEMPO_LIVELLI_INF
   */
  public static final String TEMPO_LIVELLI_INF = "TEMPO_LIVELLI_INF";

  /**
   * Attributo TEMPO_TOTALE
   */
  public static final String TEMPO_TOTALE = "TEMPO_TOTALE";

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
  public static final String TABLE_NAME = SystemParam.getSchema("THIP") + "STORICO_CMM_DET";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.thera.thip.produzione.commessa.StoricoCommessaDet.class.getName();

  public synchronized static TableManager getInstance() throws SQLException {
    if (cInstance == null) {
      cInstance = (TableManager)Factory.createObject(StoricoCommessaDetTM.class);
    }
    return cInstance;
  }

  public StoricoCommessaDetTM() throws SQLException {
    super();
  }

  protected void initialize() throws SQLException {
    setTableName(TABLE_NAME);
    setObjClassName(CLASS_NAME);
    init();
  }

  protected void initializeRelation() throws SQLException {
    super.initializeRelation();
    addAttribute("IdProgrStorico" , ID_PROGR_STORIC, "getIntegerObject");
    addAttribute("IdComponenteCosto" , ID_COMPON_COSTO);
    addAttribute("TipoDettaglio" , TIPO_DETTAGLIO);
    addAttribute("IdCommessa" , ID_COMMESSA);
    addAttribute("CostoLivello" , COSTO_LIVELLO);
    addAttribute("CostoLivelloInf" , COSTO_LIVELLI_INF);
    addAttribute("CostoTotale" , COSTO_TOTALE);
    addAttribute("TempoLivello" , TEMPO_LIVELLO);
    addAttribute("TempoLivelloInf" , TEMPO_LIVELLI_INF);
    addAttribute("TempoTotale" , TEMPO_TOTALE);
    addAttribute("IdAzienda" , ID_AZIENDA);
    addComponent("DatiComuni", DatiComuniTTM.class);
    setTimestampColumn(TIMESTAMP_AGG);
    ((DatiComuniTTM)getTransientTableManager("DatiComuni")).setExcludedColums();
    setKeys(ID_AZIENDA + "," + ID_PROGR_STORIC + "," + ID_COMPON_COSTO + "," + TIPO_DETTAGLIO);
  }

  private void init() throws SQLException {
    configure();
  }

}


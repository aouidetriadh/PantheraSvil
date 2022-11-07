package it.thera.thip.datiTecnici.costi;

import java.sql.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.base.SystemParam;
import it.thera.thip.cs.*;
/*
 * Revision:
 * Number     Date        Owner      Description
 * 33950	  19/07/2021	RA		 Aggiunto nuovi attributi
 */
public class ComponenteCostoTM extends TableManager
{
// Columns
  public static final String ID_AZIENDA = "ID_AZIENDA";
  public static final String ID_COMPON_COSTO = "ID_COMPON_COSTO";
  public static final String DESCRIZIONE = "DESCRIZIONE";
  public static final String DESCR_RIDOTTA = "DESCR_RIDOTTA";
  public static final String STATO = "STATO";
  public static final String R_UTENTE_CRZ = "R_UTENTE_CRZ";
  public static final String R_UTENTE_AGG = "R_UTENTE_AGG";
  public static final String TIMESTAMP_CRZ = "TIMESTAMP_CRZ";
  public static final String TIMESTAMP_AGG = "TIMESTAMP_AGG";
  public static final String TIPO_COMP_COSTO = "TIPO_COMP_COSTO";
  public static final String PROVENIENZA = "PROVENIENZA";
  public static final String CRITICITA = "CRITICITA";
  public static final String R_FORMULA = "R_FORMULA";
  //33950 inizio
  public static final String GES_TEMPO = "GES_TEMPO";
  public static final String R_TIPO_COSTO = "R_TIPO_COSTO";
  public static final String R_TIPO_RIS = "R_TIPO_RIS";
  public static final String R_LIV_RIS = "R_LIV_RIS";
  public static final String R_RISORSA = "R_RISORSA"; 
  public static final String COSTO_UNITARIO = "COSTO_UNITARIO";
  //33950 fine

//  TABLE_NAME
  public static final String TABLE_NAME = SystemParam.getSchema("THIP11")+ "COMPON_COSTO";

//  instance
  private static TableManager cInstance;

//  CLASS_NAME
  private static final String CLASS_NAME = it.thera.thip.datiTecnici.costi.ComponenteCosto.class.getName();


  public synchronized static TableManager getInstance() throws SQLException
  {
    if (cInstance == null)
    {
      cInstance = (TableManager) Factory.createObject(ComponenteCostoTM.class);
    }
    return cInstance;
  }
  public ComponenteCostoTM() throws SQLException
  {
    super();
  }
  protected void initialize() throws SQLException
  {
    setTableName(TABLE_NAME);
        setObjClassName(CLASS_NAME);
        init();
  }
  protected void initializeRelation() throws SQLException
  {
    addAttribute("IdAzienda" , ID_AZIENDA);
    addAttribute("IdComponenteCosto" , ID_COMPON_COSTO);
    addAttribute("TipoComponente" , TIPO_COMP_COSTO);
    addAttribute("Provenienza" , PROVENIENZA);
    addAttribute("Criticita" , CRITICITA);
    addAttribute("IdFormula" , R_FORMULA);
    //33950 inizio
    addAttribute("GestioneATempo" , GES_TEMPO);
    addAttribute("IdTipoCosto" , R_TIPO_COSTO);
    addAttribute("TipoRisorsa" , R_TIPO_RIS);
    addAttribute("LivelloRisorsa" , R_LIV_RIS);
    addAttribute("IdRisorsa" , R_RISORSA);
    addAttribute("CostoUnitario" , COSTO_UNITARIO);
    //33950 fine
    addComponent("Descrizione", DescrizioneTTM.class);
    addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
    setTimestampColumn(TIMESTAMP_AGG);
   ((DatiComuniEstesiTTM)getTransientTableManager("DatiComuniEstesi")).setExcludedColums();
    setKeys(ID_AZIENDA + "," + ID_COMPON_COSTO);
  }
   private void init() throws SQLException
  {
    configure();
  }
}


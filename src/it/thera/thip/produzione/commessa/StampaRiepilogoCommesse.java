package it.thera.thip.produzione.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.thera.thermfw.base.*;
import com.thera.thermfw.batch.*;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.type.*;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.*;
import it.thera.thip.datiTecnici.costi.*;
import it.thera.thip.vendite.documentoVE.*;

/**
 * StampaRiepilogoCommesse.
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author FATTOUMA MARZOUK
 */
/*
 * Revisions:
 * Number  Date          Owner        Description
 * 04171   22/08/2005    F. MARZOUK
 * 04937   25/01/2006    MG           createReport: tolto setExecutePrint(true)
 * 04957   16/02/2006    LP           Modificato stampa
 * 07430   09/07/2007    LP           Aggiunto commit dopo ogni commessa e corretto metodo deprecato
 * 09786  17/09/2008     DBot         Aggiunta la gestione dei "null" nelle condizioni di filtro
 * 22273   12/10/2015    AA           Aggiunto l'attributo iTipoCostiCommessa
 * 36252   11/07/2022	 RA			  Aggiunto nuovo modalita di stampa riepilogo commesse
 */

public class StampaRiepilogoCommesse extends ElaboratePrintRunnable {

  // TipoStampa
  public static final char TIPO_STAMPA_COSTI = '0';
  public static final char TIPO_STAMPA_RICAVI = '1';
  public static final char TIPO_STAMPA_COSTI_RICAVI = '2';

  // StatoCommessa
  public static final char STATO_COMMESSA_CORSO = '0';
  public static final char STATO_COMMESSA_ATTIVE = '1';
  public static final char STATO_COMMESSA_CHIUSE = '2';
  public static final char STATO_COMMESSA_TUTTE = '3';

  // TipoChiusura
  public static final char TIPO_CHIUSURA_ULTIMA_PROV = '0';
  public static final char TIPO_CHIUSURA_ULTIMA_DEFINIT = '1';

  // TipologiaCostoRif
  public static final char TIPOLOGIA_COSTO_PREVENT = '0';
  public static final char TIPOLOGIA_COSTO_PREZZO_PREVEN = '1';
  public static final char TIPOLOGIA_COSTO_PREVISTO = '2';

  // labels
  public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.StampaRiepilogoCommesse";
  public static final String ID_COMMESSA = ResourceLoader.getString(RES_FILE, "ID_COMMESSA");
  public static final String RESP_COMMESSA = ResourceLoader.getString(RES_FILE, "RESP_COMMESSA");
  public static final String CLIENTE = ResourceLoader.getString(RES_FILE, "CLIENTE");
  public static final String ARTICOLO = ResourceLoader.getString(RES_FILE, "ARTICOLO");

  // Constants
  public static final BigDecimal MAX_PERC = new BigDecimal("999.99");
  public static final BigDecimal ZERO = new BigDecimal("0");
  public static final BigDecimal CENTO = new BigDecimal("100");

  //CLASS_HDR_NAME
  public static String CLASS_HDR_NAME = "StampaRiepilogoCommesse";

  // ATT
  public static String DESCRIZIONE_CMM = "DESCRIZIONE_CMM";
  public static String RAGIONE_SOCIALE = "RAGIONE_SOCIALE";
  public static String TIPO_ULT_CONSUN = "TIPO_ULT_CONSUN";
  public static String DATA_RIFERIMENTO = "DATA_RIFERIMENTO";
  public static String TIPO_COSTO_RIF = "TIPO_COSTO_RIF";
  public static String COD_CONFIG = "COD_CONFIG";
  public static String DESCRIZIONE_ART = "DESCRIZIONE_ART";

  //Fix 22273 Inizio
  /**
   * Costanti simboliche per l'attributo TipologiaCostoCmm.
   */
  public static final char COSTO_PREVENTIVO = '0';
  public static final char PREZZO_PREVENTIVO = '1';
  public static final char COSTO_PREVISTO = '2';
  public static final char COSTO_CONSUNTIVATO = '3';
  public static final char COSTO_CONSUNT_PROVV = '4';
  public static final char COSTO_CONSUNT_DEFIN = '5';
  public static final char PREVISIONE_A_FINIRE = '6';
  public static final char TUTTI = '7';
  //Fix 22273 Fine

  /**
   * Attributo iIdAzienda.
   */
  protected String iIdAzienda;

  /**
   * Attributo iTipoStampa.
   */
  protected char iTipoStampa;

  /**
   * Attributo iStatoCommessa.
   */
  protected char iStatoCommessa;

  /**
   * Attributo iTipoChiusura.
   */
  protected char iTipoChiusura;

  /**
   * Attributo iTipologiaCostoRif.
   */
  protected char iTipologiaCostoRif;

  // Fix 04837 Mz inizio

  /**
   * Attributo iCompresoOrdinato.
   */
  protected boolean iCompresoOrdinato = false;

  /**
   * Attributo iCompresoRichiesto.
   */
  protected boolean iCompresoRichiesto = false;

  // Fix 04837 Mz fine

  /**
   * Attributo iCompresoRichiesto.
   */
  protected int iBatchJobId = 0;

  /**
   * Attributo iReportNr.
   */
  protected int iReportNr = 0;

  /**
   * Attributo iRigaJobId.
   */
  protected int iRigaJobId = 1;

  /**
   * Attributo iDetRigaJobId.
   */
  protected int iDetRigaJobId = 0;

  /**
   * Attributo iRigaJobIdHand.
   */
  protected int iRigaJobIdHand = 0;

  /**
   * Attributo iCondFiltro.
   */
  protected CondizioniFiltri iCondFiltro;

  /**
   * Attributo iCondizioniOrdinamento.
   */
  protected CondizioniOrdinamento iCondizioniOrdinamento;

  /**
   * Attributo availableReport.
   */
  protected AvailableReport availableReport;

  /**
   * Attributo iCallFromConsuntivazione.
   */
  protected boolean iCallFromConsuntivazione = false;

  /**
   * Attributo iTipoCostiCommessa.
   */
  protected char iTipoCostiCommessa; //Fix 22273

  //36252 inizio
  protected boolean iUsaConsuntiviStoricizzati = false;
  protected java.sql.Date iDataRiferimento;
  protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);
  protected Proxy iConsuntivoCommessa = new Proxy(it.thera.thip.produzione.commessa.ConsuntivoCommessa.class);
  protected Proxy iBudgetCommessa = new Proxy(it.thera.thip.produzione.commessa.BudgetCommessa.class);
  
  protected ConsuntivoCommessa iCurrentConsuntivoCommessa;
  protected BudgetCommessa iCurrentBudgetCommessa;
  //36252 fine
  /**
   * Costruttore.
   */
  public StampaRiepilogoCommesse() {
    setIdAzienda(Azienda.getAziendaCorrente());
    setTipoStampa(TIPO_STAMPA_COSTI_RICAVI);
    setStatoCommessa(STATO_COMMESSA_CORSO);
    setTipoChiusura(TIPO_CHIUSURA_ULTIMA_PROV);
    setTipologiaCostoRif(TIPOLOGIA_COSTO_PREVISTO);
    setTipoCostiCommessa(TUTTI); //Fix 22273
    initializeConditioneFiltro();
    initializeCondizioniOrdinamento();
    getCondFiltro().setNumeroRighe((short)5); // Fix 04361
  }

  /**
   * getClassAdCollectionName
   * @return String
   */
  protected String getClassAdCollectionName() {
    return CLASS_HDR_NAME;
  }

  /**
   * setIdAzienda
   * @param idAzienda String
   */
  public void setIdAzienda(String idAzienda) {
    iIdAzienda = idAzienda;
    setIdAziendaInternal(idAzienda);//36252
  }

  /**
   * getIdAzienda
   * @return String
   */
  public String getIdAzienda() {
    return iIdAzienda;
  }

  /**
   * setTipoStampa
   * @param tipoStampa char
   */
  public void setTipoStampa(char tipoStampa) {
    iTipoStampa = tipoStampa;
  }

  /**
   * getTipoStampa
   * @return char
   */
  public char getTipoStampa() {
    return iTipoStampa;
  }

  /**
   * setStatoCommessa
   * @param statoCommessa char
   */
  public void setStatoCommessa(char statoCommessa) {
    iStatoCommessa = statoCommessa;
  }

  /**
   * getStatoCommessa
   * @return char
   */
  public char getStatoCommessa() {
    return iStatoCommessa;
  }

  /**
   * setTipoChiusura
   * @param tipoChiusura char
   */
  public void setTipoChiusura(char tipoChiusura) {
    iTipoChiusura = tipoChiusura;
  }

  /**
   * getTipoChiusura
   * @return char
   */
  public char getTipoChiusura() {
    return iTipoChiusura;
  }

  /**
   * setTipologiaCostoRif
   * @param tipologiaCostoRif char
   */
  public void setTipologiaCostoRif(char tipologiaCostoRif) {
    iTipologiaCostoRif = tipologiaCostoRif;
  }

  /**
   * getTipologiaCostoRif
   * @return char
   */
  public char getTipologiaCostoRif() {
    return iTipologiaCostoRif;
  }

  // Fix 04837 Mz inizio

  /**
   * setCompresoOrdinato
   * @param flag boolean
   */
  public void setCompresoOrdinato(boolean flag) {
    iCompresoOrdinato = flag;
  }

  /**
   * isCompresoOrdinato
   * @return boolean
   */
  public boolean isCompresoOrdinato() {
    return(getTipoChiusura() == TIPO_CHIUSURA_ULTIMA_DEFINIT) ? false : iCompresoOrdinato;
  }

  /**
   * setCompresoRichiesto
   * @param flag boolean
   */
  public void setCompresoRichiesto(boolean flag) {
    iCompresoRichiesto = flag;
  }

  /**
   * isCompresoRichiesto
   * @return boolean
   */
  public boolean isCompresoRichiesto() {
    return(getTipoChiusura() == TIPO_CHIUSURA_ULTIMA_DEFINIT) ? false : iCompresoRichiesto;
  }

  // Fix 04837 Mz fine
  //36252 inizio
  public void setUsaConsuntiviStoricizzati(boolean usaConsuntiviStoricizzati) {
	  iUsaConsuntiviStoricizzati = usaConsuntiviStoricizzati;
  }
  
  public boolean isUsaConsuntiviStoricizzati() {
	  return iUsaConsuntiviStoricizzati;
  }
  
  public void setDataRiferimento(java.sql.Date dataRiferimento) {
	  this.iDataRiferimento = dataRiferimento;
  }

  public java.sql.Date getDataRiferimento() {
	  return iDataRiferimento;
  }
  
  public void setCommessa(Commessa commessa) {
	  String idAzienda = null;
	  String idCommessa = null;	    
	  if (commessa != null) {
		  idAzienda = KeyHelper.getTokenObjectKey(commessa.getKey(), 1);
		  idCommessa = KeyHelper.getTokenObjectKey(commessa.getKey(), 2);
	  }
	  setIdAziendaInternal(idAzienda);
	  setIdCommessaInternal(idCommessa);
	  this.iCommessa.setObject(commessa);    
  }

  public Commessa getCommessa() {
	  return (Commessa)iCommessa.getObject();
  }

  public void setCommessaKey(String key) {
	  iCommessa.setKey(key);
	  String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
	  setIdAziendaInternal(idAzienda);	    
	  String idCommessa = KeyHelper.getTokenObjectKey(key, 2);
	  setIdCommessaInternal(idCommessa);
  }

  public String getCommessaKey() {
	  return iCommessa.getKey();
  }

  public void setIdCommessa(String idCommessa) {
	  String key = iCommessa.getKey();
	  iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idCommessa));
	  setIdCommessaInternal(idCommessa);
  }

  public String getIdCommessa() {
	  String key = iCommessa.getKey();
	  String objIdCommessa = KeyHelper.getTokenObjectKey(key,2);
	  return objIdCommessa;
  }
  
  public void setConsuntivoCommessa(ConsuntivoCommessa consuntivo) {
	  String oldObjectKey = getKey();
	  String idAzienda = null;
	  String idCommessa = null;
	  if (consuntivo != null) {
		  idAzienda = KeyHelper.getTokenObjectKey(consuntivo.getKey(), 1);
		  idCommessa = KeyHelper.getTokenObjectKey(consuntivo.getKey(), 3);
	  }
	  setIdAziendaInternal(idAzienda);
	  setIdCommessaInternal(idCommessa);
	  this.iConsuntivoCommessa.setObject(consuntivo);
  }
  
  public ConsuntivoCommessa getConsuntivoCommessa() {
	  return (ConsuntivoCommessa)iConsuntivoCommessa.getObject();
  }

  public void setConsuntivoCommessaKey(String key) {
	  String oldObjectKey = getKey();
	  iConsuntivoCommessa.setKey(key);
	  String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
	  setIdAziendaInternal(idAzienda);
	  String idCommessa = KeyHelper.getTokenObjectKey(key, 3);
	  setIdCommessaInternal(idCommessa);
  }

  public String getConsuntivoCommessaKey() {
	  return iConsuntivoCommessa.getKey();
  }
	  
  public void setIdConsuntivo(Integer idConsuntivo) {
	  String oldObjectKey = getKey();
	  String key = iConsuntivoCommessa.getKey();
	  iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idConsuntivo));
  }

  public Integer getIdConsuntivo() {
	  String key = iConsuntivoCommessa.getKey();
	  String objIdConsuntivo = KeyHelper.getTokenObjectKey(key, 2);
	  return KeyHelper.stringToIntegerObj(objIdConsuntivo);
  }
	  
  public String getIdCommessaConsuntivo() {
	  String key = iConsuntivoCommessa.getKey();
	  return KeyHelper.getTokenObjectKey(key, 3);
  }
  
  public void setIdCommessaConsuntivo(String idCommessaConsuntivo) {
	  String oldObjectKey = getKey();
	  String key = iConsuntivoCommessa.getKey();
	  iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idCommessaConsuntivo));
  }
  
  public void setBudgetCommessa(BudgetCommessa budgetCommessa) {
	  String idAzienda = null;
	  String idCommessa = null;
	  if (budgetCommessa != null) {
		  idAzienda = KeyHelper.getTokenObjectKey(budgetCommessa.getKey(), 1);
		  idCommessa = KeyHelper.getTokenObjectKey(budgetCommessa.getKey(), 3);
	  }
	  setIdAziendaInternal(idAzienda);
	  setIdCommessaInternal(idCommessa);
	  this.iBudgetCommessa.setObject(budgetCommessa);
  }

  public BudgetCommessa getBudgetCommessa() {
	  return (BudgetCommessa)iBudgetCommessa.getObject();
  }

  public void setBudgetCommessaKey(String key) {
	  iBudgetCommessa.setKey(key);
	  String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
	  setIdAziendaInternal(idAzienda);
	  String idCommessa = KeyHelper.getTokenObjectKey(key, 1);
	  setIdCommessaInternal(idCommessa);
  }

  public String getBudgetCommessaKey() {
	  return iBudgetCommessa.getKey();
  }
  
  public void setIdBudget(Integer idBudget) {
	  String key = iBudgetCommessa.getKey();
	  iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idBudget));
  }
  
  public Integer getIdBudget() {
	  String key = iBudgetCommessa.getKey();
	  String objIdBudget = KeyHelper.getTokenObjectKey(key, 2);
	  return KeyHelper.stringToIntegerObj(objIdBudget);
  }

  public String getIdCommessaBudget() {
	  String key = iBudgetCommessa.getKey();
	  return KeyHelper.getTokenObjectKey(key, 3);
  }
  
  public void setIdCommessaBudget(String idCommessaBudget) {
	  String oldObjectKey = getKey();
	  String key = iBudgetCommessa.getKey();
	  iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idCommessaBudget));
  }
  
  protected void setIdAziendaInternal(String idAzienda) {
	  String key1 = iCommessa.getKey();
	  iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
	  String key2 = iConsuntivoCommessa.getKey();
	  iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
	  String key3 = iBudgetCommessa.getKey();
	  iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key3, 1, idAzienda));
  }
  
  protected void setIdCommessaInternal(String idCommessa) {
	  String key1 = iCommessa.getKey();
	  iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, idCommessa));
	  String key2 = iConsuntivoCommessa.getKey();
	  iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 3, idCommessa));
	  String key3 = iBudgetCommessa.getKey();
	  iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key3, 3, idCommessa));
  }
  //36252 inizio

  /**
   * getCondFiltro
   * @return CondizioniFiltri
   */
  public CondizioniFiltri getCondFiltro() {
    return iCondFiltro;
  }

  /**
   * setCondFiltro
   * @param condFiltro CondizioniFiltri
   */
  public void setCondFiltro(CondizioniFiltri condFiltro) {
    iCondFiltro = condFiltro;
  }

  /**
   * initializeConditioneFiltro
   * @return CondizioniFiltri
   */
  protected CondizioniFiltri initializeConditioneFiltro() {
    Vector where = new Vector();
    ColonneFiltri cf1 = ColonneFiltri.creaColonnaFiltro("Commessa", "IdAmbienteCommessa", "AmbienteCommessa", true);
    where.add(cf1);
    ColonneFiltri cf2 = ColonneFiltriConNLS.creaColonnaFiltro("Commessa", "IdCommessa", true, ID_COMMESSA);
    cf2.setRelatedClassName("Commessa");
    where.add(cf2);
    ColonneFiltri cf3 = ColonneFiltriConNLS.creaColonnaFiltro("Commessa", "IdResponsabileCommessa", "Dipendente", "Descrizione", true, RESP_COMMESSA);
    where.add(cf3);
    ColonneFiltri cf4 = ColonneFiltriConNLS.creaColonnaFiltro("Commessa", "IdCliente", "ClienteVendita", "RagioneSociale", true, CLIENTE);
    where.add(cf4);
    ColonneFiltri cf5 = ColonneFiltriConNLS.creaColonnaFiltro("Commessa", "IdArticolo", "Articolo", "DescrizioneArticolo.Descrizione", true, ARTICOLO);
    where.add(cf5);
    iCondFiltro = new CondizioniFiltri(where);
    return iCondFiltro;
  }

  /**
   * initializeUserEnums
   */
  protected void initializeUserEnums() {
    String tableNameTES = removeSchema(ReportRiepilogoCommessaTestataTM.TABLE_NAME);
    String tableNameDET = removeSchema(ReportRiepilogoCommessaDettaglioTM.TABLE_NAME);
    addEnum("StatoAvanzamentoRiep", tableNameTES + "." + ReportRiepilogoCommessaTestataTM.STATO_AVANZAMENTO, "StatoAvanzCmm");
    addEnum("CriterioFatturazione", tableNameDET + "." + ReportRiepilogoCommessaDettaglioTM.CRITERIO_FATT, "CriterioFatt");
  }

  /**
   * removeSchema
   * @param name String
   * @return String
   */
  public String removeSchema(String name) {
    int pos = name.indexOf(".");
    if(pos > 0)
      name = name.substring(pos + 1);
    return name;
  }

  /**
   * createReport
   * @return boolean
   */
  public boolean createReport() {
    job.setReportCounter((short)0);
    availableReport = createNewReport(getReportId());
    boolean ret = true;
    setPrintToolInterface((PrintingToolInterface)Factory.createObject(CrystalReportsInterface.class));
    try {
      String s = printToolInterface.generateDefaultWhereCondition(availableReport, ReportRiepilogoCommessaTestataTM.getInstance());
      availableReport.setWhereCondition(s);
      availableReport.save();
      ConnectionManager.commit();
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream);
      return false;
    }
    try {
//    	if(isUsaConsuntiviStoricizzati()) {
//    		ret = buildDataConsuntiviStoricizzati();
//    	}
//    	else
    		ret = buildData();
    }
    catch(Exception sqle) {
      sqle.printStackTrace(Trace.excStream);
      return false;
    }
    return ret;
  }

  /**
   * initCondizioniFiltroForBatch
   */
  /*protected void initCondizioniFiltroForBatch() {
    ArrayList filterValues = iCondFiltro.getVettoreValoriFiltro();
    for(int i = 0; i < iCondFiltro.getColonneFiltro().size(); i++) {
      ColonneFiltri colFlt = (ColonneFiltri)iCondFiltro.getColonneFiltro().get(i);
      //...FIX 7430 inizio
       ArrayList valori = (ArrayList)filterValues.get(i);
      colFlt.setFrom((String)valori.get(2));
      colFlt.setTo((String)valori.get(3));
      colFlt.setListaString((String)valori.get(4));
     //...Faccio la try/catch di queste due righe di codice perchè
      //...potrebbero esserci dei lavori schedulati che non contengono
      //...i valori per le esclusioni, e quindi la get potrebbe andare in
      //...eccezione perchè l'Array contiene solo 5 valori (e non 7)
      try {
        colFlt.setRangeEsclusione(valori.get(5).equals("true") ? true : false);
        colFlt.setListaEsclusione(valori.get(6).equals("true") ? true : false);
      }
      catch(Exception e){}
      //...FIX 7430 fine
    }
     }*/

  /**
   *
   * @throws Exception
   * @return boolean
   */
  public boolean buildData() throws Exception {
    try {
    	//36252 inizio
    	Iterator it;
    	if(isUsaConsuntiviStoricizzati()) {
    		initializePerConsuntiviStoricizzati();	  
    		it = getCommessa().getRelatedCommesse().iterator();
    	}
    	else {
    		String SQLQuery = selectQuery();
    		it = richiestaParametri(SQLQuery);
    	}
    	//36252 fine

      ReportRiepilogoCommessaTestata rpttRiepCmmTes = null;
      while(it.hasNext()) {
        checkPoint(); //...FIX 7430
        Commessa commessa = (Commessa)it.next();
        if(commessa.getTipoCommessa() != null && commessa.getTipoCommessa().getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE) {
          iRigaJobIdHand = iRigaJobId;
          //36252 inizio
          CostiCommessa costiCommessa = null;
          if(!isUsaConsuntiviStoricizzati())
        	  costiCommessa = getCostiCommessaIdent(commessa, getTipoChiusura());
          //36252 fine
          //...FIX 4957
          rpttRiepCmmTes = createRiepCostiCommessaTes(commessa, costiCommessa);
          int saveTes = rpttRiepCmmTes.save();
          if(saveTes < ErrorCodes.NO_ROWS_UPDATED) {
            ConnectionManager.rollback();
            return false;
          }
          else
            if(saveTes > ErrorCodes.NO_ROWS_UPDATED /*&& costiCommessa != null*/) { // 04361 Fix Fattouma //Fix 22273 --Parte commentata
              if(getTipoStampa() == TIPO_STAMPA_COSTI || getTipoStampa() == TIPO_STAMPA_COSTI_RICAVI) {
                if(createRiepCostiCommessaDet(commessa, costiCommessa, rpttRiepCmmTes) < ErrorCodes.NO_ROWS_UPDATED) { //...FIX 4957
                  ConnectionManager.rollback();
                  return false;
                }
              }
              if(getTipoStampa() == TIPO_STAMPA_RICAVI || getTipoStampa() == TIPO_STAMPA_COSTI_RICAVI) {
                if(createRiepCostiCommessaDetRicavi(commessa) < ErrorCodes.NO_ROWS_UPDATED) {
                  ConnectionManager.rollback();
                  return false;
                }
              }
            }
          //...FIX 4957
          if(getTipoStampa() != TIPO_STAMPA_COSTI_RICAVI) {
            rpttRiepCmmTes.setCostoTotaleConsuntivato(null);
            rpttRiepCmmTes.save();
          }
          ConnectionManager.commit(); //...FIX 7430
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream);
      return false;
    }
    return true;
  }

  /**
   * richiestaParametri
   * @param where String
   * @throws Exception
   * @return Iterator
   */
  protected synchronized Iterator richiestaParametri(String where) throws Exception {
    Vector vec = Commessa.retrieveList(Commessa.class, where, ordinamentoInClausolaWhere(), false);
    if(vec != null)
      return vec.iterator();
    return null;
  }

  /**
   * selectQuery
   * @return String
   */
  protected String selectQuery() {
    String allQuery = null;
    Vector allColumn = new Vector();
    if(iCallFromConsuntivazione)
      allColumn = getCondFiltro().getColonneFiltro();
    else
      allColumn = getAllColumn();
    String iFilterSQL = getFilterSQL(allColumn);
    allQuery = getFilterParams() + iFilterSQL + getNaturaCommessaCondition();
    return allQuery;
  }

  /**
   * getNaturaCommessaCondition
   * @return String
   */
  public String getNaturaCommessaCondition() {
    return " AND " + CommessaTM.R_TIPO_COMMESSA +
      " IN (SELECT " + TipoCommessaTM.ID_TIPO_CMM +
      " FROM " + TipoCommessaTM.TABLE_NAME +
      " WHERE " + TipoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + TipoCommessaTM.NATURA_CMM + " = '" + TipoCommessa.NATURA_CMM__GESTIONALE + "')";
  }

  /**
   * getAllColumn
   * @return Vector
   */
  protected Vector getAllColumn() {
     //Fix 9786 inizio
     getCondFiltro().impostaColonneConVettoreValoriFiltro();
     return getCondFiltro().getColonneFiltro();
     /*
    Vector result = getCondFiltro().getColonneFiltro();
    ArrayList source = getCondFiltro().getVettoreValoriFiltro();
    for(int i = 0; i < result.size(); i++) {
      //...FIX 7430 inizio
      ColonneFiltri colFlt = (ColonneFiltri)result.get(i);
      ArrayList valori = (ArrayList)source.get(i);
      colFlt.setFrom((String)valori.get(2));
      colFlt.setTo((String)valori.get(3));
      colFlt.setListaString((String)valori.get(4));
      //...Faccio la try/catch di queste due righe di codice perchè
      //...potrebbero esserci dei lavori schedulati che non contengono
      //...i valori per le esclusioni, e quindi la get potrebbe andare in
      //...eccezione perchè l'Array contiene solo 5 valori (e non 7)
      try {
        colFlt.setRangeEsclusione(valori.get(5).equals("true") ? true : false);
        colFlt.setListaEsclusione(valori.get(6).equals("true") ? true : false);
      }
      catch(Exception e) {}
      //...FIX 7430 fine
    }
    return result;
     */
     //Fix 9786 fine
  }

  /**
   * getFilterSQL
   * @param allColumn Vector
   * @return String
   */
  protected String getFilterSQL(Vector allColumn) {
    String toTreat = "";
    for(int i = 0; i < allColumn.size(); i++) {
      ColonneFiltri cf = (ColonneFiltri)allColumn.get(i);
      String myWhereCondition = cf.getWhere();
      if(myWhereCondition != null && myWhereCondition.trim().length() > 0) {
        toTreat += " AND (" + myWhereCondition + ")";
      }
    }
    return toTreat;
  }

  /**
   * replace
   * @param data String
   * @param oldToken String
   * @param newToken String
   * @return String
   */
  public static final String replace(String data, String oldToken, String newToken) {
    while(data.indexOf(oldToken) != -1)
      data = data.substring(0, data.indexOf(oldToken)) + newToken + data.substring(data.indexOf(oldToken) + oldToken.length());
    return data;
  }

  /**
   * getFilterParams
   * @return String
   */
  protected String getFilterParams() {
    String filtriParamSQL = " " + CommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' ";
    filtriParamSQL += " AND " + CommessaTM.STATO + " = '" + DatiComuniEstesi.VALIDO + "' ";
    // stato commessa
    switch(getStatoCommessa()) {
      case STATO_COMMESSA_CORSO:
        filtriParamSQL += " AND (" + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CONFERMATA + "'" +
          " OR " + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_TECNICAMENTO + "') ";
        break;
      case STATO_COMMESSA_ATTIVE:
        filtriParamSQL += " AND (" + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CONFERMATA + "'" +
          " OR " + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_TECNICAMENTO + "'" +
          " OR " + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_OPERATIVAMENTO + "') ";
        break;
      case STATO_COMMESSA_CHIUSE:
        filtriParamSQL += " AND " + CommessaTM.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__CHIUSA_CONTABILAMENTO + "' ";
        break;
      case STATO_COMMESSA_TUTTE:
        filtriParamSQL += " AND " + CommessaTM.STATO_AVANZAMENTO + " <> '" + Commessa.STATO_AVANZAM__PROVVISORIA + "' ";
    }
    return filtriParamSQL;
  }

  /**
   * ordinamentoInClausolaWhere
   * @return String
   */
  public String ordinamentoInClausolaWhere() {
    String whereForOrdinamento = CommessaTM.R_AMBIENTE_CMM + ","
      + CommessaTM.R_COMMESSA_PRM + ","
      + CommessaTM.LIVELLO_CMM + ","
      + CommessaTM.ID_COMMESSA;
    return whereForOrdinamento;
  }

  /**
   * getCondizioniOrdinamento
   * @return CondizioniOrdinamento
   */
  public CondizioniOrdinamento getCondizioniOrdinamento() {
    return iCondizioniOrdinamento;
  }

  /**
   * initializeCondizioniOrdinamento
   */
  protected void initializeCondizioniOrdinamento() {
    Vector vector = new Vector();
    ColonneFiltri cf = ColonneFiltri.creaColonnaFiltro("Commessa", "IdAmbienteCommessa", "AmbienteCommessa", true);
    vector.add(cf);
    cf = ColonneFiltri.creaColonnaFiltro("Commessa", "IdCommessaPrincipale", "Commessa", true);
    vector.add(cf);
    cf = ColonneFiltri.creaColonnaFiltro("Commessa", "IdCommessa", "Commessa", true);
    vector.add(cf);
    iCondizioniOrdinamento = new CondizioniOrdinamento(vector);
  }

  // FIX 4361 FATTOUMA

  /**
   * createRiepCostiCommessaTes
   * @param commessa Commessa
   * @param costiCommessa CostiCommessa
   * @throws Exception
   * @return ReportRiepilogoCommessaTestata
   */
  //...FIX 4957
  public ReportRiepilogoCommessaTestata createRiepCostiCommessaTes(Commessa commessa, CostiCommessa costiCommessa) throws Exception {
    ReportRiepilogoCommessaTestata rptRiepCommessaTes = (ReportRiepilogoCommessaTestata)Factory.createObject(ReportRiepilogoCommessaTestata.class);
    rptRiepCommessaTes.setBatchJobId(getBatchJob().getBatchJobId());
    rptRiepCommessaTes.setReportNr(availableReport.getReportNr());
    rptRiepCommessaTes.setIdRigaJob(iRigaJobId++);
    rptRiepCommessaTes.valorize(commessa);
    rptRiepCommessaTes.setTipologiaCostoRif(getTipologiaCostoRif());
    rptRiepCommessaTes.setTipoUltimaConsun(getTipoChiusura());
    if(costiCommessa != null) {
      rptRiepCommessaTes.setDataRiferimento(costiCommessa.getDataRiferimento());
      rptRiepCommessaTes.setCostoTotaleConsuntivato(costiCommessa.getCostoRiferimento());
    }
    //36252 inizio
    else if(costiCommessa == null && getConsuntivoCommessa() != null) {
        rptRiepCommessaTes.setDataRiferimento(getConsuntivoCommessa().getDataRiferimento());
        rptRiepCommessaTes.setCostoTotaleConsuntivato(getConsuntivoCommessa().getCostoRiferimento());
    }
    //36252 fine
    return rptRiepCommessaTes;
  }

  /**
   * getCostiCommessaIdent
   * @param commessa Commessa
   * @param tipoChuisura char
   * @return CostiCommessa
   */
  public CostiCommessa getCostiCommessaIdent(Commessa commessa, char tipoChuisura) {
    CostiCommessa costiCommessa = null;
    if(getTipoChiusura() == TIPO_CHIUSURA_ULTIMA_PROV) {
      costiCommessa = getCostiCommessa(commessa, CostiCommessa.COSTO_CONSUNT_PROVV);
      if(costiCommessa == null)
        costiCommessa = getCostiCommessa(commessa, CostiCommessa.COSTO_CONSUNT_DEFIN);
    }
    else
      costiCommessa = getCostiCommessa(commessa, CostiCommessa.COSTO_CONSUNT_DEFIN);
    return costiCommessa;
  }

  /**
   * getCostiCommessa
   * @param commessa Commessa
   * @param tipologiaCostoCmm char
   * @return CostiCommessa
   */
  public CostiCommessa getCostiCommessa(Commessa commessa, char tipologiaCostoCmm) {
    String where = CostiCommessaTM.ID_AZIENDA + " = '" + commessa.getIdAzienda() + "'" +
      " AND " + CostiCommessaTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
      " AND " + CostiCommessaTM.TIPOLOGIA + " = '" + tipologiaCostoCmm + "'";
    try {
      List costiCommessaList = CostiCommessa.retrieveList(where, "", true);
      if(!costiCommessaList.isEmpty())
        return(CostiCommessa)costiCommessaList.get(0);
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream);
    }
    return null;
  }

  /**
   * createRiepCostiCommessaDet
   * @param commessa Commessa
   * @param costiCommessa CostiCommessa
   * @param testata ReportRiepilogoCommessaTestata
   * @throws Exception
   * @return int
   */
  public int createRiepCostiCommessaDet(Commessa commessa, CostiCommessa costiCommessa, ReportRiepilogoCommessaTestata testata) throws Exception {
    // recuperer la liste de componentiCosti
    List componentCostiList = getCostiComponent(commessa);
    Iterator it = componentCostiList.iterator();
    int ret = 0;
    // trait pour chaque component costi
    while(it.hasNext()) {
      ComponenteCosto componentCosto = ((LinkCompSchema)it.next()).getComponenteCosto();
      //...FIX 4957 inizio
      PersDatiTecnici pdt = PersDatiTecnici.getCurrentPersDatiTecnici();
      String cmpRif = pdt.getIdRiferimento();
      if(componentCosto != null) {
        ReportRiepilogoCommessaDettaglio dett = createRiepCostiCommDetail(commessa, costiCommessa, componentCosto);
        int ret1 = dett.save();
        if(cmpRif != null && componentCosto.getIdComponenteCosto().equals(cmpRif)) {
          testata.setCostoTotaleConsuntivato(dett.getCostoTotaleCons());
          testata.save();
        }
        //...FIX 4957 fine
        if(ret1 < 0)
          return ret1;
        ret += ret1;
      }
    }
    return ret;
  }

  /**
   * getCostiComponent
   * @param commessa Commessa
   * @return List
   */
  public List getCostiComponent(Commessa commessa) {
    if(commessa.getArticolo() != null)
      if(commessa.getArticolo().getClasseMerclg() != null)
        if(commessa.getArticolo().getClasseMerclg().getSchemaCosto() != null)
          return commessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti(); // order by id ...
    return new Vector();
  }

  /**
   * creation effective des details
   * @param commessa Commessa
   * @param costiCommessa CostiCommessa
   * @param componentCosto ComponenteCosto
   * @throws Exception
   * @return ReportRiepilogoCommessaDettaglio
   */
  //...FIX 4957
  public ReportRiepilogoCommessaDettaglio createRiepCostiCommDetail(Commessa commessa, CostiCommessa costiCommessa, ComponenteCosto componentCosto) throws Exception {
    ReportRiepilogoCommessaDettaglio rptRiepCommessaDet = (ReportRiepilogoCommessaDettaglio)Factory.createObject(ReportRiepilogoCommessaDettaglio.class);
    //
    rptRiepCommessaDet.setBatchJobId(getBatchJob().getBatchJobId());
    rptRiepCommessaDet.setReportNr(availableReport.getReportNr());
    rptRiepCommessaDet.setIdRigaJob(iRigaJobIdHand);
    rptRiepCommessaDet.setIdDetRigaJob(++iDetRigaJobId);

    rptRiepCommessaDet.initializeCosto();
    rptRiepCommessaDet.valorize(commessa, componentCosto);
    rptRiepCommessaDet.setProvenienza(componentCosto.getProvenienza());
    //36252 inizio
    if(isUsaConsuntiviStoricizzati() && iCurrentConsuntivoCommessa != null && iCurrentBudgetCommessa != null) {
    	ConsuntivoCommessaDet consuntivoDet = getConsuntivoCommessaDet(commessa.getIdCommessa(), componentCosto.getIdComponenteCosto());
        BudgetCommessaDet budgetDet = getBudgetCommessaDet(commessa.getIdCommessa(), componentCosto.getIdComponenteCosto());
        rptRiepCommessaDet.setCostoPrevisto(budgetDet.getCostoLivello());
        rptRiepCommessaDet.setCostoTotaleFinire(budgetDet.getCostoTotale());
        rptRiepCommessaDet.setCostoRichiesto(consuntivoDet.getRichiesto().getCostoLivello());
        rptRiepCommessaDet.setCostoOrdinato(consuntivoDet.getOrdinato().getCostoLivello());
        rptRiepCommessaDet.setCostoSostenuto(consuntivoDet.getEffettuato().getCostoLivello());        
        rptRiepCommessaDet.setCostoTotaleCons(consuntivoDet.getTotale().getCostoLivello());
    }
    else {
    //36252 fine
    // input costoPrevisto
    rptRiepCommessaDet.setCostoPrevisto(getCostiPrevisto(commessa, componentCosto.getIdComponenteCosto()));
    // input CostoPrevisione
    rptRiepCommessaDet.setCostoPrevisione(getCostiPrevisione(commessa, componentCosto.getIdComponenteCosto()));
    // input costoRichiesta ,costoOrdinato , CostoSostenuto
    rptRiepCommessaDet.setCostoRichiesto(getCostoValue(commessa, costiCommessa, componentCosto.getIdComponenteCosto(), CostiCommessaDet.COSTO_RICHIESTO));
    rptRiepCommessaDet.setCostoOrdinato(getCostoValue(commessa, costiCommessa, componentCosto.getIdComponenteCosto(), CostiCommessaDet.COSTO_ORDINATO));
    rptRiepCommessaDet.setCostoSostenuto(getCostoValue(commessa, costiCommessa, componentCosto.getIdComponenteCosto(), CostiCommessaDet.NORMALE));   
    // attributi calcolati
    rptRiepCommessaDet.setCostoTotaleFinire(rptRiepCommessaDet.getCostoPrevisto().add(rptRiepCommessaDet.getCostoPrevisione()));
  //Fix 04837 Mz inizio
    //rptRiepCommessaDet.setCostoTotaleCons(rptRiepCommessaDet.getCostoRichiesto().add(rptRiepCommessaDet.getCostoOrdinato()).add(rptRiepCommessaDet.getCostoSostenuto()));
    rptRiepCommessaDet.setCostoTotaleCons(rptRiepCommessaDet.getCostoSostenuto());
    if(isCompresoRichiesto())
      rptRiepCommessaDet.setCostoTotaleCons(rptRiepCommessaDet.getCostoTotaleCons().add(rptRiepCommessaDet.getCostoRichiesto()));
    if(isCompresoOrdinato())
      rptRiepCommessaDet.setCostoTotaleCons(rptRiepCommessaDet.getCostoTotaleCons().add(rptRiepCommessaDet.getCostoOrdinato()));
      //Fix 04837 Mz inizio
    rptRiepCommessaDet.setScostamento(rptRiepCommessaDet.getCostoTotaleFinire().subtract(rptRiepCommessaDet.getCostoTotaleCons()));
    }//36252
    
    BigDecimal percValue;
    //
    if(rptRiepCommessaDet.getCostoPrevisto().compareTo(rptRiepCommessaDet.NULL_BIG_DECIMAL) != 0) {
      percValue = rptRiepCommessaDet.getCostoTotaleCons().divide(rptRiepCommessaDet.getCostoPrevisto(), BigDecimal.ROUND_UP).multiply(CENTO);
      //Fix 04837 Mz inizio
      rptRiepCommessaDet.setPercConsPrevisto(getPercentuale(percValue));
      //if(percValue.compareTo(MAX_PERC)<0)
      //  rptRiepCommessaDet.setPercConsPrevisto(percValue);
      //else
      //  rptRiepCommessaDet.setPercConsPrevisto(MAX_PERC);
      //Fix 04837 Mz fine
    }
    else
      rptRiepCommessaDet.setPercConsPrevisto(rptRiepCommessaDet.NULL_BIG_DECIMAL);
      //
    if(rptRiepCommessaDet.getCostoTotaleFinire().compareTo(rptRiepCommessaDet.NULL_BIG_DECIMAL) != 0) {
      percValue = rptRiepCommessaDet.getCostoTotaleCons().divide(rptRiepCommessaDet.getCostoTotaleFinire(), BigDecimal.ROUND_UP).multiply(CENTO);
      //Fix 04837 Mz inizio
      rptRiepCommessaDet.setPercConsCostoFin(getPercentuale(percValue));
      //if(percValue.compareTo(MAX_PERC)<0)
      //  rptRiepCommessaDet.setPercConsCostoFin(percValue);
      //else
      //  rptRiepCommessaDet.setPercConsCostoFin(MAX_PERC);
      //Fix 04837 Mz fine
    }
    else
      rptRiepCommessaDet.setPercConsCostoFin(rptRiepCommessaDet.NULL_BIG_DECIMAL);
      //
    if(rptRiepCommessaDet.getCostoPrevisto().compareTo(rptRiepCommessaDet.NULL_BIG_DECIMAL) != 0) {
      percValue = rptRiepCommessaDet.getCostoPrevisione().divide(rptRiepCommessaDet.getCostoPrevisto(), BigDecimal.ROUND_UP).multiply(CENTO);
      //Fix 04837 Mz inizio
      rptRiepCommessaDet.setPercPrevisPrevisto(getPercentuale(percValue));
      //if(percValue.compareTo(MAX_PERC)<0)
      //  rptRiepCommessaDet.setPercPrevisPrevisto(percValue);
      //else
      //  rptRiepCommessaDet.setPercPrevisPrevisto(MAX_PERC);
      //Fix 04837 Mz fine
    }
    else
      rptRiepCommessaDet.setPercPrevisPrevisto(rptRiepCommessaDet.NULL_BIG_DECIMAL);
    return rptRiepCommessaDet /*.save()*/;
  }

  /**
   * getCostiPrevisto
   * @param commessa Commessa
   * @param idComponentCosto String
   * @throws Exception
   * @return BigDecimal
   */
  public BigDecimal getCostiPrevisto(Commessa commessa, String idComponentCosto) throws Exception {
    String where = CostiCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostiCommessaTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
      " AND " + CostiCommessaTM.TIPOLOGIA + " = '" + getTipologiaCostoRif() + "'" +
      " AND " + CostiCommessaTM.UFFICIALE + " = '" + Column.TRUE_CHAR + "'";
    List l = CostiCommessa.retrieveList(CostiCommessa.class, where, "", false);
    CostiCommessa costiCommessa = (CostiCommessa)Factory.createObject(CostiCommessa.class);
    BigDecimal costoPrevisto = ZERO;
    if(!l.isEmpty()) {
      costiCommessa = (CostiCommessa)l.get(0);
      String where1 = CostiCommessaElemTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
        " AND " + CostiCommessaElemTM.ID_COMMESSA + " = '" + costiCommessa.getIdCommessa() + "'" +
        " AND " + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + costiCommessa.getIdProgrStoric() +
        " AND " + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "'" +
        " AND " + CostiCommessaElemTM.R_ARTICOLO + " = '" + costiCommessa.getIdArticolo() + "'" +
        " AND " + CostiCommessaElemTM.R_VERSIONE + " = " + costiCommessa.getIdVersione() +
        " AND " + CostiCommessaElemTM.R_STABILIMENTO + " = '" + costiCommessa.getIdStabilimento() + "'";
      if(costiCommessa.getIdConfigurazione() != null && !costiCommessa.getIdConfigurazione().toString().equals(""))
        where1 += " AND " + CostiCommessaElemTM.R_CONFIGURAZIONE + " = " + costiCommessa.getIdConfigurazione() + " ";
      else
        where1 += " AND " + CostiCommessaElemTM.R_CONFIGURAZIONE + " IS NULL ";

      List costiCommessaElemList = CostiCommessaElem.retrieveList(CostiCommessaElem.class, where1, "", false);
      CostiCommessaElem costiCommessaElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
      if(!costiCommessaElemList.isEmpty()) {
        costiCommessaElem = (CostiCommessaElem)costiCommessaElemList.get(0);
        costoPrevisto = getCostiCommessaDetails(costiCommessaElem, idComponentCosto);
      }
    }
    return costoPrevisto;
  }

  /**
   * getCostiPrevisione
   * @param commessa Commessa
   * @param idComponentCosto String
   * @throws Exception
   * @return BigDecimal
   */
  public BigDecimal getCostiPrevisione(Commessa commessa, String idComponentCosto) throws Exception {
    BigDecimal costoPrevisione = ZERO;
    CostiCommessa costiCommessa = (CostiCommessa)Factory.createObject(CostiCommessa.class);
    String where = CostiCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostiCommessaTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
      " AND " + CostiCommessaTM.TIPOLOGIA + " = '" + CostiCommessa.PREVISIONE_A_FINIRE + "'";
    List l = CostiCommessa.retrieveList(CostiCommessa.class, where, "", false);
    if(!l.isEmpty()) {
      costiCommessa = (CostiCommessa)l.get(0);
      String where1 = CostiCommessaElemTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
        " AND " + CostiCommessaElemTM.ID_COMMESSA + " = '" + costiCommessa.getIdCommessa() + "'" +
        " AND " + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + costiCommessa.getIdProgrStoric() +
        " AND " + CostiCommessaElemTM.AGGREGA_COSTI + " = '" + Column.TRUE_CHAR + "' ";
      List costiCommessaElemList = CostiCommessaElem.retrieveList(CostiCommessaElem.class, where1, "", false);
      CostiCommessaElem costiCommessaElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
      if(!costiCommessaElemList.isEmpty()) {
        costiCommessaElem = (CostiCommessaElem)costiCommessaElemList.get(0);
        costoPrevisione = getCostiCommessaDetails(costiCommessaElem, idComponentCosto);
      }
    }
    return costoPrevisione;
  }

  /**
   * getCostiCommessaDetails
   * @param costiCommessaElem CostiCommessaElem
   * @param idComponentCosto String
   * @throws Exception
   * @return BigDecimal
   */
  public BigDecimal getCostiCommessaDetails(CostiCommessaElem costiCommessaElem, String idComponentCosto) throws Exception {
    String where = CostiCommessaDetTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostiCommessaDetTM.ID_COMMESSA + " = '" + costiCommessaElem.getIdCommessa() + "'" +
      " AND " + CostiCommessaDetTM.ID_PROGR_STORIC + " = " + costiCommessaElem.getIdProgrStoric() +
      " AND " + CostiCommessaDetTM.ID_COMPON_COSTO + " = '" + idComponentCosto + "'" +
      " AND " + CostiCommessaDetTM.ID_RIGA_ELEM + " = " + costiCommessaElem.getIdRigaElemento();
    List l = CostiCommessaDet.retrieveList(CostiCommessaDet.class, where, "", false);
    Iterator it = l.iterator();
    BigDecimal costo = ZERO;
    while(it.hasNext()) {
      CostiCommessaDet costiCommessaDet = (CostiCommessaDet)it.next();
      costo = costo.add(costiCommessaDet.getCostoTotale());
    }
    return costo;
  }

  /**
   * getCostoValue
   * @param commessa Commessa
   * @param costiCommessa CostiCommessa
   * @param idComponentCosto String
   * @param tipoDetail char
   * @throws Exception
   * @return BigDecimal
   */
  public BigDecimal getCostoValue(Commessa commessa, CostiCommessa costiCommessa, String idComponentCosto, char tipoDetail) throws Exception {
    if(costiCommessa != null) {
      String where1 = CostiCommessaElemTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
        " AND " + CostiCommessaElemTM.ID_COMMESSA + " = '" + costiCommessa.getIdCommessa() + "'" +
        " AND " + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + costiCommessa.getIdProgrStoric();
      List costiCommessaElemList = CostiCommessaElem.retrieveList(CostiCommessaElem.class, where1, "", false);
      if(!costiCommessaElemList.isEmpty()) {
        CostiCommessaElem costiCommessaElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
        costiCommessaElem = (CostiCommessaElem)costiCommessaElemList.get(0);
        return getCosto(costiCommessaElem, tipoDetail, idComponentCosto);
      }
    }
    return ZERO;
  }

  /**
   * getCosto
   * @param costiCommessaElem CostiCommessaElem
   * @param tipoDetail char
   * @param idComponentCosto String
   * @throws Exception
   * @return BigDecimal
   */
  public BigDecimal getCosto(CostiCommessaElem costiCommessaElem, char tipoDetail, String idComponentCosto) throws Exception {
    String where = CostiCommessaDetTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostiCommessaDetTM.ID_COMMESSA + " = '" + costiCommessaElem.getIdCommessa() + "'" +
      " AND " + CostiCommessaDetTM.ID_PROGR_STORIC + " = " + costiCommessaElem.getIdProgrStoric() +
      " AND " + CostiCommessaDetTM.ID_COMPON_COSTO + " = '" + idComponentCosto + "'" +
      " AND " + CostiCommessaDetTM.TIPO_DETTAGLIO + " = '" + tipoDetail + "'" +
      " AND " + CostiCommessaDetTM.ID_RIGA_ELEM + " = " + costiCommessaElem.getIdRigaElemento();
    List l = CostiCommessaDet.retrieveList(CostiCommessaDet.class, where, "", false);
    Iterator it = l.iterator();
    BigDecimal costo = ZERO;
    while(it.hasNext()) {
      CostiCommessaDet costiCommessaDet = (CostiCommessaDet)it.next();
      costo = costo.add(costiCommessaDet.getCostoTotale());
    }
    return costo;
  }

  /**
   * createRiepCostiCommessaDetRicavi
   * @param commessa Commessa
   * @throws Exception
   * @return int
   */
  public int createRiepCostiCommessaDetRicavi(Commessa commessa) throws Exception {
    int ret1 = 0;
    //con piano fatturazione commessa = 2
    if(commessa.getPianoFatturazione() == Commessa.PIANO_FATT__ATTIVO && commessa.getIdCommessaAppartenenza() == null) {
      OneToMany rateCommessaList = commessa.getRateCommesse();
      rateCommessaList.sort(new RateCommessaComparator());
      Iterator ite = rateCommessaList.iterator();
      while(ite.hasNext()) {
        // create a detail with rataEmesse = Y or rataEmesse = N
        RataCommessa rataCommessa = (RataCommessa)ite.next();
        // if(rataCommessa.getCriterioFatturazione() == Commessa.PIANO_FATT__ATTIVO)
        //  {
        int ret = createCommessaRiepDetailsRata(rataCommessa);
        if(ret < 0)
          return ret;
        ret1 += ret;
        // }
      }
    }
    // con piano fatturazione commessa != 2
    if(commessa.getPianoFatturazione() != Commessa.PIANO_FATT__ATTIVO) {
      List storicoCommessaList = getAllStoricoCommessa(commessa);
      Iterator ite = storicoCommessaList.iterator();
      while(ite.hasNext()) {
        int ret2 = createCommessaRiepDetailsFromStoricoComm((StoricoCommessa)ite.next());
        if(ret2 < 0)
          return ret2;
        ret1 += ret2;
      }
    }
    return ret1;
  }

  // Fix 04171 22/08/2005 ini

  /**
   * createCommessaRiepDetailsRata
   * @param rataCommessa RataCommessa
   * @throws Exception
   * @return int
   */
  public int createCommessaRiepDetailsRata(RataCommessa rataCommessa) throws Exception {
    ReportRiepilogoCommessaDettaglio rptRiepCommessaDet = (ReportRiepilogoCommessaDettaglio)Factory.createObject(ReportRiepilogoCommessaDettaglio.class);

    rptRiepCommessaDet.setBatchJobId(getBatchJob().getBatchJobId());
    rptRiepCommessaDet.setReportNr(availableReport.getReportNr());
    rptRiepCommessaDet.setIdRigaJob(iRigaJobIdHand);
    rptRiepCommessaDet.setIdDetRigaJob(++iDetRigaJobId);

    rptRiepCommessaDet.initializeCosto();
    rptRiepCommessaDet.initializeComponentCostoAttributes();
    rptRiepCommessaDet.setDescrizione(rataCommessa.getCommessa().getDescrizione().getDescrizione());
    rptRiepCommessaDet.setCostiRicavi(ReportRiepilogoCommessaDettaglio.RICAVI);
    rptRiepCommessaDet.setIdCommessa(rataCommessa.getIdCommessa());
    rptRiepCommessaDet.setNumeroRata(rataCommessa.getNumeroRata().shortValue());
    rptRiepCommessaDet.setCriterioFatturazione(rataCommessa.getCriterioFatturazione());
    rptRiepCommessaDet.setImportoRata(rataCommessa.getImporto());
    boolean docExist = false, docVenExist = false, storicoExist = false;
    if(rataCommessa.getIdNumeroDocumento() != null) {
      docExist = true;
      DocumentoVenditaRiga documentoVenditaRiga = getDocumentoVenditaRiga(rataCommessa); //04171

      if(documentoVenditaRiga != null) {
        docVenExist = true;
        rptRiepCommessaDet.setRataEmessa(true);
        rptRiepCommessaDet.setImportoEmesso(documentoVenditaRiga.getValoreRiga());
        rptRiepCommessaDet.setImportoFatturato(ReportRiepilogoCommessaDettaglio.NULL_BIG_DECIMAL);
        if(documentoVenditaRiga.getDataFattura() != null)
          rptRiepCommessaDet.setImportoFatturato(rptRiepCommessaDet.getImportoEmesso());
        rptRiepCommessaDet.setDataDocumento(documentoVenditaRiga.getTestata().getDataDocumento());
        rptRiepCommessaDet.setNumeroDocumento(documentoVenditaRiga.getNumeroDocumento());
        if(documentoVenditaRiga.getDettaglioRigaDocumento() != null)
          rptRiepCommessaDet.setDettaglioRigaDocumento(documentoVenditaRiga.getDettaglioRigaDocumento().intValue());
        if(documentoVenditaRiga.getNumeroRigaDocumento() != null)
          rptRiepCommessaDet.setRigaDocumento(documentoVenditaRiga.getNumeroRigaDocumento().intValue());
        rptRiepCommessaDet.setAnnoDocumento(documentoVenditaRiga.getAnnoDocumento());
      }
      else {
        StoricoCommessa storicoComm = getStoricoCommessa(rataCommessa);
        if(storicoComm != null) {
          storicoExist = true;
          rptRiepCommessaDet.setRataEmessa(true);
          rptRiepCommessaDet.setImportoEmesso(storicoComm.getValoreRiga());
          rptRiepCommessaDet.setImportoFatturato(ReportRiepilogoCommessaDettaglio.NULL_BIG_DECIMAL);
          if(storicoComm.getDataFattura() != null)
            rptRiepCommessaDet.setImportoFatturato(rptRiepCommessaDet.getImportoEmesso());
          rptRiepCommessaDet.setDataDocumento(storicoComm.getDataOrigine());
          rptRiepCommessaDet.setNumeroDocumento(rataCommessa.getIdNumeroDocumento());
          if(rataCommessa.getIdDetRigaDocumento() != null)
            rptRiepCommessaDet.setDettaglioRigaDocumento(rataCommessa.getIdDetRigaDocumento().intValue());
          if(rataCommessa.getIdRigaDocumento() != null)
            rptRiepCommessaDet.setRigaDocumento(rataCommessa.getIdRigaDocumento().intValue());
          rptRiepCommessaDet.setAnnoDocumento(rataCommessa.getIdAnnoDocumento());
        }
      }
    }
    if(!docExist || (!docVenExist && !storicoExist)) {
      rptRiepCommessaDet.setRataEmessa(false);
      rptRiepCommessaDet.setImportoEmesso(ZERO);
      rptRiepCommessaDet.setImportoFatturato(ZERO);
      rptRiepCommessaDet.setDataDocumento(rataCommessa.getDataFatturazione()); // 04361 Fix Fattouma
    }
    return rptRiepCommessaDet.save();
  }

  /**
   * getDocumentoVenditaRiga
   * @param rataCommessa RataCommessa
   * @throws Exception
   * @return DocumentoVenditaRiga
   */
  public DocumentoVenditaRiga getDocumentoVenditaRiga(RataCommessa rataCommessa) throws Exception {
    DocumentoVenditaRiga documentoVenditaRiga = (DocumentoVenditaRiga)Factory.createObject(DocumentoVenditaRiga.class);
    //Fix 04599
    String where = DocumentoVenditaRigaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + DocumentoVenditaRigaTM.R_COMMESSA + " = '" + rataCommessa.getIdCommessaDoc() + "'" +
      " AND " + DocumentoVenditaRigaTM.ID_ANNO_DOC + " = '" + rataCommessa.getIdAnnoDocumento() + "'" +
      " AND " + DocumentoVenditaRigaTM.ID_NUMERO_DOC + " = '" + rataCommessa.getIdNumeroDocumento() + "'" +
      " AND " + DocumentoVenditaRigaTM.ID_RIGA_DOC + " = " + rataCommessa.getIdRigaDocumento() +
      " AND " + DocumentoVenditaRigaTM.STATO + " = '" + DatiComuniEstesi.VALIDO + "'" +
      " AND " + DocumentoVenditaRigaTM.STATO_AVANZAMENTO + " = '" + DocumentoVenditaRiga.DEFINITIVO + "' ";

    if(rataCommessa.getIdDetRigaDocumento() != null)
      where += " AND " + DocumentoVenditaRigaTM.ID_DET_RIGA_DOC + " = " + rataCommessa.getIdDetRigaDocumento();
    else
      where += " AND " + DocumentoVenditaRigaTM.ID_DET_RIGA_DOC + " = 0 ";
    List documentoVenditaRigaList = DocumentoVenRigaPrm.retrieveList(DocumentoVenRigaPrm.class, where, "", true);
    //Fix 04599
    if(!documentoVenditaRigaList.isEmpty()) {
      documentoVenditaRiga = (DocumentoVenditaRiga)documentoVenditaRigaList.get(0);
      if(documentoVenditaRiga.getTestata() != null &&
        documentoVenditaRiga.getTestata().getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO &&
        documentoVenditaRiga.getTestata().getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO)
        return documentoVenditaRiga;
    }
    return null;
  }

  // Fix 04171 22/08/2005 fin

  /**
   * getStoricoCommessa
   * @param rataCommessa RataCommessa
   * @throws Exception
   * @return StoricoCommessa
   */
  public StoricoCommessa getStoricoCommessa(RataCommessa rataCommessa) throws Exception {
    StoricoCommessa storicoComm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
    String where = StoricoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + StoricoCommessaTM.R_COMMESSA + " = '" + rataCommessa.getIdCommessaDoc() + "'" +
      " AND " + StoricoCommessaTM.DOC_ORIGINE + " = '" + StoricoCommessa.DOCUMENTO + "'" +
      " AND " + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.VENDITA + "'" +
      " AND " + StoricoCommessaTM.NO_FATTURA + " = '" + Column.FALSE_CHAR + "'" +
      " AND " + StoricoCommessaTM.R_ANNO_ORG + " = '" + rataCommessa.getIdAnnoDocumento() + "'" +
      " AND " + StoricoCommessaTM.R_NUMERO_ORG + " = '" + rataCommessa.getIdNumeroDocumento() + "'";
    if(rataCommessa.getIdRigaDocumento() != null)
      where += " AND " + StoricoCommessaTM.R_RIGA_ORG + " = " + rataCommessa.getIdRigaDocumento();
    else
      where += " AND " + StoricoCommessaTM.R_RIGA_ORG + " = 0 ";

    List storicoCommList = StoricoCommessa.retrieveList(StoricoCommessa.class, where, "", true);
    if(!storicoCommList.isEmpty()) {
      storicoComm = (StoricoCommessa)storicoCommList.get(0);
      return storicoComm;
    }
    return null;
  }

  /**
   * getAllStoricoCommessa
   * @param commessa Commessa
   * @throws Exception
   * @return List
   */
  public List getAllStoricoCommessa(Commessa commessa) throws Exception {
    List retList = new ArrayList();
    StoricoCommessa storicoComm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
    String where = StoricoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + StoricoCommessaTM.R_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
      " AND " + StoricoCommessaTM.DOC_ORIGINE + " = '" + StoricoCommessa.DOCUMENTO + "'" +
      " AND " + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.VENDITA + "'" +
      " AND " + StoricoCommessaTM.NO_FATTURA + "='" + Column.FALSE_CHAR + "'";
    List storicoCommList = StoricoCommessa.retrieveList(StoricoCommessa.class, where, "", true);
    if(!storicoCommList.isEmpty()) {
      //storicoComm = (StoricoCommessa)storicoCommList.get(0);
      for(Iterator i = storicoCommList.iterator(); i.hasNext(); ) {
        storicoComm = (StoricoCommessa)i.next();
        retList.add(storicoComm);
      }
    }
    List sottoCommessa = commessa.getAllSottocommesseId();
    Iterator itSottoCmm = sottoCommessa.iterator();

    while(itSottoCmm.hasNext()) {
      storicoCommList.clear();
      where = StoricoCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
        " AND " + StoricoCommessaTM.R_COMMESSA + " = '" + (String)itSottoCmm.next() + "'" +
        " AND " + StoricoCommessaTM.DOC_ORIGINE + " = '" + StoricoCommessa.DOCUMENTO + "'" +
        " AND " + StoricoCommessaTM.TIPO_RIGA_ORIGINE + " = '" + StoricoCommessa.VENDITA + "'" +
        " AND " + StoricoCommessaTM.NO_FATTURA + " = '" + Column.FALSE_CHAR + "'";
      storicoCommList = StoricoCommessa.retrieveList(StoricoCommessa.class, where, "", true);
      if(!storicoCommList.isEmpty()) {
        // storicoComm = (StoricoCommessa)storicoCommList.get(0);
        for(Iterator i = storicoCommList.iterator(); i.hasNext(); ) {
          storicoComm = (StoricoCommessa)i.next();
          retList.add(storicoComm);
        }
      }
    }
    return retList;
  }

  /**
   * createCommessaRiepDetailsFromStoricoComm
   * @param storicoComm StoricoCommessa
   * @throws Exception
   * @return int
   */
  public int createCommessaRiepDetailsFromStoricoComm(StoricoCommessa storicoComm) throws Exception {
    ReportRiepilogoCommessaDettaglio rptRiepCommessaDet = (ReportRiepilogoCommessaDettaglio)Factory.createObject(ReportRiepilogoCommessaDettaglio.class);
    rptRiepCommessaDet.setBatchJobId(getBatchJob().getBatchJobId());
    rptRiepCommessaDet.setReportNr(availableReport.getReportNr());
    rptRiepCommessaDet.setIdRigaJob(iRigaJobIdHand);
    rptRiepCommessaDet.setIdDetRigaJob(++iDetRigaJobId);
    rptRiepCommessaDet.initializeCosto();
    rptRiepCommessaDet.initializeComponentCostoAttributes();
    rptRiepCommessaDet.setCostiRicavi(ReportRiepilogoCommessaDettaglio.RICAVI);
    rptRiepCommessaDet.setIdCommessa(storicoComm.getIdCommessa());
    rptRiepCommessaDet.setRataEmessa(true);
    rptRiepCommessaDet.setDataDocumento(storicoComm.getDataOrigine());
    rptRiepCommessaDet.setNumeroDocumento(storicoComm.getIdNumeroOrigine());
    rptRiepCommessaDet.setAnnoDocumento(storicoComm.getIdAnnoOrigine());
    if(storicoComm.getIdRigaOrigine() != null)
      rptRiepCommessaDet.setRigaDocumento(storicoComm.getIdRigaOrigine().intValue());
    if(storicoComm.getIdDetRigaOrigine() != null)
      rptRiepCommessaDet.setDettaglioRigaDocumento(storicoComm.getIdDetRigaOrigine().intValue());
    rptRiepCommessaDet.setImportoRata(storicoComm.getValoreRiga());
    rptRiepCommessaDet.setImportoEmesso(storicoComm.getValoreRiga());
    rptRiepCommessaDet.setCriterioFatturazione(RataCommessa.DATA);
    return rptRiepCommessaDet.save();
  }

  /**
   * initializeUserParameters
   */
  protected void initializeUserParameters() {
    String tipoStampa = getDescAttrRef("TipoStampaCmmRpt", getTipoStampa());
    addUserParameter("TipoStampaRiepDesc", tipoStampa);

    String statoCommessa = getDescAttrRef("StatoCommessaRpt", getStatoCommessa());
    addUserParameter("StatoCommessaRiepDesc", statoCommessa);

    String tipoChiusura = getDescAttrRef("TipoConsCmmAmbRpt", getTipoChiusura());
    addUserParameter("TipoChiusuraRiepDesc", tipoChiusura);

    String tipologiaCostoRif = getDescAttrRef("TipologiaCostoTraCmm", getTipologiaCostoRif());
    addUserParameter("TipologiaCostoRifRiepDesc", tipologiaCostoRif);

    //...FIX 7430 inizio
    //addUserParameter("FiltriRiep", constructParmFiltri(iCondFiltro));
    addUserParameter("FiltriRiep", iCondFiltro.getValoriFiltroString());
    //...FIX 7430 fine

    //Fix 04837 Mz inizio
    addUserParameter("CompresaOrdinatoDesc", getBooleanDesc(isCompresoOrdinato()));
    addUserParameter("CompresaRichiestoDesc", getBooleanDesc(isCompresoRichiesto()));
    addUserParameter("Costo", getCostoParam(getTipologiaCostoRif()));
    addUserParameter("CostoTotale", getCostoTotaleParam(getTipologiaCostoRif()));
    //Fix 04837 Mz fine
  }

  /**
   * constructParmFiltri
   * @param condizioneFiltri CondizioniFiltri
   * @return String
   */
  /*protected static String constructParmFiltri(CondizioniFiltri condizioneFiltri) {
    String parmFiltri = "";
    ArrayList a = condizioneFiltri.getVettoreValoriFiltro();
    for(int i = 0; i < a.size(); i++) {
      ArrayList al = (ArrayList)a.get(i);
      String name = (String)al.get(1);
      String from = (String)al.get(2);
      String to = (String)al.get(3);
      String lista = (String)al.get(4);
      if(from != null || to != null || lista != null)
        parmFiltri += name + ":";
      if(from != null)
        parmFiltri += " " + ResourceLoader.getString(RES_FILE, "CP_DA") + " " + from;
      if(to != null)
        parmFiltri += " " + ResourceLoader.getString(RES_FILE, "CP_A") + " " + to;
      if(lista != null)
        parmFiltri += "\n [" + ResourceLoader.getString(RES_FILE, "CP_Lista") + ": " + convertLista(lista) + "]";
      if(from != null || to != null || lista != null)
        parmFiltri += "\n";
    }
    return parmFiltri;
     }*/

  /**
   * convertLista
   * @param lista String
   * @return String
   */
  /*protected static String convertLista(String lista) {
    String result = "";
    StringTokenizer st = new StringTokenizer(lista, ColonneFiltri.LISTA_SEP);
    while(st.hasMoreTokens()) {
      StringTokenizer stt = new StringTokenizer(st.nextToken(), ColonneFiltri.COD_DESC_SEP);
      if(result.length() == 0)
        result += stt.nextToken();
      else
        result += " - " + stt.nextToken();
    }
    return result;
     }*/

  /**
   * getDescAttrRef
   * @param IdRef String
   * @param value char
   * @return String
   */
  private static String getDescAttrRef(String IdRef, char value) {
    EnumType eType = new EnumType(IdRef);
    return eType.descriptionFromValue(String.valueOf(value));
  }

  /**
   * setCallFromConsuntivazione
   * @param callFromConsuntivazione boolean
   */
  public void setCallFromConsuntivazione(boolean callFromConsuntivazione) {
    iCallFromConsuntivazione = callFromConsuntivazione;
  }

  // Fix 04837 Mz inizio

  /**
   * getPercentuale
   * @param value BigDecimal
   * @return BigDecimal
   */
  public BigDecimal getPercentuale(BigDecimal value) {
    if(value == null)
      return null;
    int signum = value.signum();
    BigDecimal percValue = value.abs().min(MAX_PERC);
    if(signum < 0)
      percValue.negate();
    return percValue;
  }

  /**
   * getCostoParam
   * @param tipologiaCostoRif char
   * @return String
   */
  public static String getCostoParam(char tipologiaCostoRif) {
    if(tipologiaCostoRif == TIPOLOGIA_COSTO_PREVENT)
      return ResourceLoader.getString(RES_FILE, "CostoPreventivo");
    if(tipologiaCostoRif == TIPOLOGIA_COSTO_PREZZO_PREVEN)
      return ResourceLoader.getString(RES_FILE, "PrezzoPreventivo");
    return ResourceLoader.getString(RES_FILE, "CostoPrevsito");
  }

  /**
   * getCostoTotaleParam
   * @param tipologiaCostoRif char
   * @return String
   */
  public static String getCostoTotaleParam(char tipologiaCostoRif) {
    if(tipologiaCostoRif == TIPOLOGIA_COSTO_PREZZO_PREVEN)
      return ResourceLoader.getString(RES_FILE, "PrezzoTotaleFinire");
    return ResourceLoader.getString(RES_FILE, "CostoTotaleFinire");
  }

  /**
   * getBooleanDesc
   * @param value boolean
   * @return String
   */
  public static String getBooleanDesc(boolean value) {
    EnumType type = new EnumType("Booleano");
    BooleanType booleanType = new BooleanType();
    return type.descriptionFromValue(booleanType.objectToString(new Boolean(value)));
  }

  //Fix 04837 Mz fine

  //Fix 22273 Inizio
  public void setTipoCostiCommessa(char tipo){
    iTipoCostiCommessa = tipo;
  }

  public char getTipoCostiCommessa(){
    return iTipoCostiCommessa;
  }
  //Fix 22273 Fine
  //36252 inizio
  public ErrorMessage checkUsaConsuntiviStoricizzati() {
	  if(isUsaConsuntiviStoricizzati()) {
		  if(getIdConsuntivo() == null || getIdBudget() == null) {
			  if( getIdCommessa() == null && getDataRiferimento() == null) {
				  return new ErrorMessage("THIP_TN890");	//Deve valorizzare Commessa , Consuntivo e Budget o Data riferimento per usa consuntivi storicizzati.	
			  }
			  else if(getIdCommessa() != null && getDataRiferimento() == null) {
				  return new ErrorMessage("THIP_TN890");	
			  }  
		  }		  
	  }	  
	  return null;
  }

  public void initializePerConsuntiviStoricizzati() {
	  initializeConsuntivoCommessa();
	  initializeBudgetCommessa();
  }
  
  public void initializeConsuntivoCommessa() {
	  if(getConsuntivoCommessa() != null) {
		  iCurrentConsuntivoCommessa = getConsuntivoCommessa();
	  }
	  else if(getDataRiferimento()!= null && getCommessa() != null) {
		  ConsuntivoCommessa consuntivo = cercaConsuntivoCommessa();
		  if(consuntivo != null)
			  iCurrentConsuntivoCommessa = consuntivo;
	  }
  }
  
  public void initializeBudgetCommessa() {
	  if(getBudgetCommessa() != null) {
		  iCurrentBudgetCommessa = getBudgetCommessa();
	  }
	  else if(getDataRiferimento()!= null && getCommessa() != null) {
		  BudgetCommessa budget = cercaBudgetCommessa();
		  if(budget != null)
			  iCurrentBudgetCommessa = budget;
	  }
  }
  
  public ConsuntivoCommessa cercaConsuntivoCommessa() {
	  ConsuntivoCommessa consuntivo = null;
	  String where = ConsuntivoCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + 
		  		     ConsuntivoCommessaTM.R_COMMESSA + "='" + getIdCommessa() + "' AND " +
		  		     ConsuntivoCommessaTM.DATA_RIFERENTO + " <= " + ConnectionManager.getCurrentDatabase().getLiteral(getDataRiferimento());
	  String orderBy = ConsuntivoCommessaTM.DATA_RIFERENTO  + " DESC ";
	  PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, orderBy, PersistentObject.NO_LOCK);
	  try {
		  if(cursor.hasNext()) {
			  consuntivo = (ConsuntivoCommessa)cursor.next();
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return consuntivo;
  }
  
  public BudgetCommessa cercaBudgetCommessa() {
	  BudgetCommessa budget = null;
	  java.sql.Date dataRiferimento = getDataRiferimento();
	  if(getConsuntivoCommessa() != null) {
		  dataRiferimento = getConsuntivoCommessa().getDataRiferimento();
	  }
	  String where = BudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + 
			  		 BudgetCommessaTM.ID_COMMESSA + "='" + getIdCommessa() + "' AND " +
			  		 BudgetCommessaTM.DATA_RIFERENTO + " <= " + ConnectionManager.getCurrentDatabase().getLiteral(dataRiferimento);
	  String orderBy = BudgetCommessaTM.DATA_RIFERENTO  + " DESC ";
	  PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, orderBy, PersistentObject.NO_LOCK);
	  try {
		  if(cursor.hasNext()) {
			  budget = (BudgetCommessa)cursor.next();
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return budget;
  }
  
  public ConsuntivoCommessaDet getConsuntivoCommessaDet(String idCommessa, String idCompCosto) {
	  ConsuntivoCommessaDet dettaglio = null;
	  if(iCurrentConsuntivoCommessa != null) {
		  String key = KeyHelper.buildObjectKey(new Object[] {Azienda.getAziendaCorrente(), iCurrentConsuntivoCommessa.getIdConsuntivo(), idCommessa, idCompCosto});
		  try {
			dettaglio = ConsuntivoCommessaDet.elementWithKey(key, PersistentObject.NO_LOCK);
		  } 
		  catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		  }
	  }
	  return dettaglio;
  }
  
  public BudgetCommessaDet getBudgetCommessaDet(String idCommessa, String idCompCosto) {
	  BudgetCommessaDet dettaglio = null;
	  if(iCurrentBudgetCommessa != null) {
		  String key = KeyHelper.buildObjectKey(new Object[] {Azienda.getAziendaCorrente(), iCurrentBudgetCommessa.getIdBudget(), idCommessa, idCompCosto});
		  try {
			dettaglio = BudgetCommessaDet.elementWithKey(key, PersistentObject.NO_LOCK);
		  } 
		  catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		  }
	  }
	  return dettaglio;
  }
  //36252 fine  
}

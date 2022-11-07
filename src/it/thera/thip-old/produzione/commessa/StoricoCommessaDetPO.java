package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Vector;

import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.persist.Child;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.ConflictableWithKey;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.cs.PersistentObjectDC;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;

/*
 * @(#)StoricoCommessaDetPO.java
 */

/**
 * StoricoCommessaDetPO
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 26/07/2021
 */
/*
 * Revisions:
 * Number  Date          Owner           Description
 * 33950   26/07/2021    RA				 Prima struttura
 */

public abstract class StoricoCommessaDetPO extends PersistentObjectDC implements BusinessObject, Authorizable, Deletable, Child, ConflictableWithKey {

  //Tipo dettaglio
  public static final char EFFETTUATO = '0';
  public static final char ORDINATO = '1';
  public static final char RICHIESTO = '2';

  /**
   *  instance
   */
  private static StoricoCommessaDet cInstance;

  /**
   * Attributo iTipoDettaglio
   */
  protected char iTipoDettaglio = EFFETTUATO;

  /**
   * Attributo iCostoLivello
   */
  protected BigDecimal iCostoLivello = new BigDecimal("0");

  /**
   * Attributo iCostoLivelloInf
   */
  protected BigDecimal iCostoLivelloInf = new BigDecimal("0");

  /**
   * Attributo iCostoTotale
   */
  protected BigDecimal iCostoTotale = new BigDecimal("0");
  
  /**
   * Attributo iTempoLivello
   */
  protected BigDecimal iTempoLivello = new BigDecimal("0");

  /**
   * Attributo iTempoLivelloInf
   */
  protected BigDecimal iTempoLivelloInf = new BigDecimal("0");

  /**
   * Attributo iTempoTotale
   */
  protected BigDecimal iTempoTotale = new BigDecimal("0");
  
  /**
   * Attributo iAzienda
   */
  protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);

  /**
   * Attributo iStoricoCommessa
   */
  protected Proxy iStoricoCommessa = new Proxy(it.thera.thip.produzione.commessa.StoricoCommessa.class);
  
  /**
   * Attributo iCommessa
   */
  protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iComponenteCosto
   */
  protected Proxy iComponenteCosto = new Proxy(it.thera.thip.datiTecnici.costi.ComponenteCosto.class);

  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    if (cInstance == null)
      cInstance = (StoricoCommessaDet) Factory.createObject(StoricoCommessaDet.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  public static StoricoCommessaDet elementWithKey(String key, int lockType) throws SQLException {
    return (StoricoCommessaDet) PersistentObject.elementWithKey(StoricoCommessaDet.class, key, lockType);
  }

  public StoricoCommessaDetPO() {
    super();
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  public String getAziendaKey() {
    return iAzienda.getKey();
  }

  public void setAzienda(Azienda aziende) {
    this.iAzienda.setObject(aziende);
    setIdAziendaInternal(aziende.getKey());
    setDirty();
    setOnDB(false);
  }

  public void setAziendaKey(String key) {
    iAzienda.setKey(key);
    setIdAziendaInternal(key);
    setDirty();
    setOnDB(false);
  }

  public String getIdAzienda() {
    String key = iAzienda.getKey();
    return key;
  }

  public void setIdAzienda(String idAzienda) {
    iAzienda.setKey(idAzienda);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  public void setTipoDettaglio(char tipoDettaglio) {
    this.iTipoDettaglio = tipoDettaglio;
    setDirty();
  }

  public char getTipoDettaglio() {
    return iTipoDettaglio;
  }

  public void setCostoLivello(BigDecimal costoLivello) {
    this.iCostoLivello = costoLivello;
    setDirty();
  }

  public BigDecimal getCostoLivello() {
    return iCostoLivello;
  }

  public void setCostoLivelloInf(BigDecimal costoLivelloInf) {
    this.iCostoLivelloInf = costoLivelloInf;
    setDirty();
  }

  public BigDecimal getCostoLivelloInf() {
    return iCostoLivelloInf;
  }

  public void setCostoTotale(BigDecimal costoTotale) {
    this.iCostoTotale = costoTotale;
    setDirty();
  }

  public BigDecimal getCostoTotale() {
    return iCostoTotale;
  }    

  public void setTempoLivello(BigDecimal tempo) {
    this.iTempoLivello = tempo;
    setDirty();
  }

  public BigDecimal getTempoLivello() {
    return iTempoLivello;
  }

  public void setTempoLivelloInf(BigDecimal tempo) {
    this.iTempoLivelloInf = tempo;
    setDirty();
  }

  public BigDecimal getTempoLivelloInf() {
    return iTempoLivelloInf;
  }
  
  public void setTempoTotale(BigDecimal tempo) {
    this.iTempoTotale = tempo;
    setDirty();
  }

  public BigDecimal getTempoTotale() {
    return iTempoTotale;
  }

  public void setStoricoCommessa(Commessa storicoCommessa) {
    this.iStoricoCommessa.setObject(storicoCommessa);
    setDirty();
  }

  public StoricoCommessa getStoricoCommessa() {
    return (StoricoCommessa) iStoricoCommessa.getObject();
  }

  public void setStoricoCommessaKey(String key) {
    iStoricoCommessa.setKey(key);
    setDirty();
  }

  public String getStoricoCommessaKey() {
    return iStoricoCommessa.getKey();
  }

  public void setIdProgrStorico(Integer idProgrStorico) {
	  String key = iStoricoCommessa.getKey();
	  iStoricoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idProgrStorico));
	  setDirty();
	  setOnDB(false);
  }

  public Integer getIdProgrStorico() {
	  String key = iStoricoCommessa.getKey();
	  String objIdProgrStorico = KeyHelper.getTokenObjectKey(key,2);
	  return KeyHelper.stringToIntegerObj(objIdProgrStorico);
  }

  public void setCommessa(Commessa commessa) {
    this.iCommessa.setObject(commessa);
    setDirty();
  }

  public Commessa getCommessa() {
    return (Commessa) iCommessa.getObject();
  }

  public void setCommessaKey(String key) {
    iCommessa.setKey(key);
    setDirty();
  }

  public String getCommessaKey() {
    return iCommessa.getKey();
  }

  public void setIdCommessa(String idCommessa) {
    String key = iCommessa.getKey();
    iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessa));
    setDirty();
  }

  public String getIdCommessa() {
    String key = iCommessa.getKey();
    String objIdCommessa = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessa;
  }

  public void setComponenteCosto(ComponenteCosto componenteCosto) {
    this.iComponenteCosto.setObject(componenteCosto);
    setDirty();
  }

  public ComponenteCosto getComponenteCosto() {
    return (ComponenteCosto) iComponenteCosto.getObject();
  }

  public void setComponenteCostoKey(String key) {
    iComponenteCosto.setKey(key);
    setDirty();
  }

  public String getComponenteCostoKey() {
    return iComponenteCosto.getKey();
  }

  public void setIdComponenteCosto(String idComponenteCosto) {
    String key = iComponenteCosto.getKey();
    iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idComponenteCosto));
    setDirty();
  }

  public String getIdComponenteCosto() {
    String key = iComponenteCosto.getKey();
    String objIdComponenteCosto = KeyHelper.getTokenObjectKey(key, 2);
    return objIdComponenteCosto;
  }

  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    StoricoCommessaDetPO storicoCommessaDetPO = (StoricoCommessaDetPO) obj;
    iAzienda.setEqual(storicoCommessaDetPO.iAzienda);
    iStoricoCommessa.setEqual(storicoCommessaDetPO.iStoricoCommessa);
    iCommessa.setEqual(storicoCommessaDetPO.iCommessa);
    iComponenteCosto.setEqual(storicoCommessaDetPO.iComponenteCosto);    
  }

  public Vector checkAll(BaseComponentsCollection components) {
    Vector errors = new Vector();
    components.runAllChecks(errors);
    return errors;
  }

  public void setKey(String key) {
    setIdAzienda(KeyHelper.getTokenObjectKey(key, 1));
    setIdProgrStorico(KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(key, 2)));
    setIdComponenteCosto(KeyHelper.getTokenObjectKey(key, 3));
    setTipoDettaglio(KeyHelper.stringToChar(KeyHelper.getTokenObjectKey(key, 4)));
  }

  public String getKey() {
    String idAzienda = getIdAzienda();
    Integer idProgStrorico = getIdProgrStorico();
    String idComponenteCosto = getIdComponenteCosto();
    char tipoDettaglio = getTipoDettaglio();
    Object[] keyParts = {
        idAzienda, idProgStrorico, idComponenteCosto, tipoDettaglio};
    return KeyHelper.buildObjectKey(keyParts);
  }

  public boolean isDeletable() {
    return checkDelete() == null;
  }

  public String toString() {
    return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
  }

  protected TableManager getTableManager() throws SQLException {
    return StoricoCommessaDetTM.getInstance();
  }

  protected void setIdAziendaInternal(String idAzienda) {
    String key1 = iCommessa.getKey();
    iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
    String key2 = iComponenteCosto.getKey();
    iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
    String key3 = iStoricoCommessa.getKey();
    iStoricoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key3, 1, idAzienda));    
  }

}


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
import it.thera.thip.cs.DatiComuniEstesi;
import it.thera.thip.cs.EntitaAzienda;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
/**
 * VariaBudgetCommessaDetPO
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/11/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34795   29/11/2021    RA		    Prima struttura
 */
public abstract class VariaBudgetCommessaDetPO extends EntitaAzienda implements BusinessObject, Authorizable, Deletable, Child, ConflictableWithKey {

  /**
   *  instance
   */
  private static VariaBudgetCommessaDet cInstance;
  
  /**
   * Attributo iIdBudget
   */
  protected Integer iIdBudget;
  
  /**
   * Attributo iComponenteCosto
   */
  protected Proxy iComponenteCosto = new Proxy(it.thera.thip.datiTecnici.costi.ComponenteCosto.class);

  /**
   * Attributo iCostiCommessaElem
   */
  protected Proxy iVariaBudgetCommessa = new Proxy(it.thera.thip.produzione.commessa.VariaBudgetCommessa.class);
  
  /**
   * Attributo iNote
   */
  protected String iNote;
  
  protected CostiCommessaDetGruppo gruppo;
  
  /**
   * Attributo iCostoLivello
   */
  protected BigDecimal iCostoLivello = BudgetCommessa.ZERO;
  
  /**
   * Attributo iCostoLivelloInf
   */
  protected BigDecimal iCostoLivelloInf = BudgetCommessa.ZERO;
  
  /**
   * Attributo iCostoTotale
   */
  protected BigDecimal iCostoTotale = BudgetCommessa.ZERO;
  
  /**
   * Attributo iTempoLivello
   */
  protected BigDecimal iTempoLivello = BudgetCommessa.ZERO;
  
  /**
   * Attributo iTempoLivelloInf
   */
  protected BigDecimal iTempoLivelloInf = BudgetCommessa.ZERO;
  
  /**
   * Attributo iTempoTotale
   */
  protected BigDecimal iTempoTotale = BudgetCommessa.ZERO;
  
  /**
   * Attributo iDatiComuniEstesi
   */
  protected DatiComuniEstesi iDatiComuniEstesi;

  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (cInstance == null)
      cInstance = (VariaBudgetCommessaDet)Factory.createObject(VariaBudgetCommessaDet.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  public static VariaBudgetCommessaDet elementWithKey(String key, int lockType) throws SQLException {
    return (VariaBudgetCommessaDet)PersistentObject.elementWithKey(VariaBudgetCommessaDet.class, key, lockType);
  }

  public VariaBudgetCommessaDetPO() {
    iDatiComuniEstesi = (DatiComuniEstesi) Factory.createObject(DatiComuniEstesi.class);
    iDatiComuniEstesi.setOwner(this);
    gruppo = (CostiCommessaDetGruppo) Factory.createObject(CostiCommessaDetGruppo.class);
    gruppo.setOwner(this);
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  public void setComponenteCosto(ComponenteCosto componenteCosto) {
    String idAzienda = null;
    if (componenteCosto != null) {
      idAzienda = KeyHelper.getTokenObjectKey(componenteCosto.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iComponenteCosto.setObject(componenteCosto);
    setDirty();
    setOnDB(false);
  }

  public ComponenteCosto getComponenteCosto() {
    return (ComponenteCosto)iComponenteCosto.getObject();
  }

  public void setComponenteCostoKey(String key) {
    iComponenteCosto.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  public String getComponenteCostoKey() {
    return iComponenteCosto.getKey();
  }

  public void setIdComponCosto(String idComponCosto) {
    String key = iComponenteCosto.getKey();
    iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idComponCosto));
    setDirty();
    setOnDB(false);
  }

  public String getIdComponCosto() {
    String key = iComponenteCosto.getKey();
    String objIdComponCosto = KeyHelper.getTokenObjectKey(key,2);
    return objIdComponCosto;
  }

  public void setVariaBudgetCommessa(VariaBudgetCommessa variaBudgetCommessa) {
    String idAzienda = null;
    if (variaBudgetCommessa != null) {
      idAzienda = KeyHelper.getTokenObjectKey(variaBudgetCommessa.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iVariaBudgetCommessa.setObject(variaBudgetCommessa);
    setDirty();
    setOnDB(false);
  }

  public VariaBudgetCommessa getVariaBudgetCommessa() {
    return (VariaBudgetCommessa)iVariaBudgetCommessa.getObject();
  }

  public void setVariaBudgetCommessaKey(String key) {
    iVariaBudgetCommessa.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  public String getVariaBudgetCommessaKey() {
    return iVariaBudgetCommessa.getKey();
  }

  public void setIdAzienda(String idAzienda) {
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  public String getIdAzienda() {
    String key = iComponenteCosto.getKey();
    String objIdAzienda = KeyHelper.getTokenObjectKey(key,1);
    return objIdAzienda;

  }

  public void setIdBudget(Integer idBudget) {
    String key = iVariaBudgetCommessa.getKey();
    iVariaBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idBudget));
    setDirty();
    setOnDB(false);
  }

  public Integer getIdBudget() {
    String key = iVariaBudgetCommessa.getKey();
    String objIdBudget = KeyHelper.getTokenObjectKey(key,2);
    return KeyHelper.stringToIntegerObj(objIdBudget);

  }

  public void setIdCommessa(String idCommessa) {
    String key = iVariaBudgetCommessa.getKey();
    iVariaBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 3, idCommessa));
    setDirty();
    setOnDB(false);
  }

  public String getIdCommessa() {
    String key = iVariaBudgetCommessa.getKey();
    String objIdCommessa = KeyHelper.getTokenObjectKey(key,3);
    return objIdCommessa;
  }

  public DatiComuniEstesi getDatiComuniEstesi() {
    return iDatiComuniEstesi;
  }
  
  public String getNote() {
	  return iNote;
  }

  public void setNote(String note) {
	  this.iNote = note;
	  setDirty();
  }
  
  public BigDecimal getCostoLivello() {
     return getGruppo().getCostoLivello();
     //return iCostoLivello;
  }

  public void setCostoLivello(BigDecimal costoLivello) {
     getGruppo().setCostoLivello(costoLivello);
     //this.iCostoLivello = costoLivello;
     setDirty();
  }
   
  public BigDecimal getCostoLivelloInf() {
     return getGruppo().getCostoLivelloInf();
     //return iCostoLivelloInf;
  }

  public void setCostoLivelloInf(BigDecimal costoLivelloInf) {
     getGruppo().setCostoLivelloInf(costoLivelloInf);
     //this.iCostoLivelloInf = costoLivelloInf;
     setDirty();
  }
   
  public BigDecimal getCostoTotale() {
     return getGruppo().getCostoTotale();
     //return iCostoTotale;
  }

  public void setCostoTotale(BigDecimal costoTotale) {
     getGruppo().setCostoTotale(costoTotale);
     //this.iCostoTotale = costoTotale;
     setDirty();
  }

  public BigDecimal getTempoLivello() {
     return getGruppo().getTempoLivello();
     //return iTempoLivello;
  }

  public void setTempoLivello(BigDecimal tempoLivello) {
     getGruppo().setTempoLivello(tempoLivello);
     //this.iTempoLivello = tempoLivello;
     setDirty();
  }
   
  public BigDecimal getTempoLivelloInf() {
     return getGruppo().getTempoLivelloInf();
     //return iTempoLivelloInf;
  }

  public void setTempoLivelloInf(BigDecimal tempoLivelloInf) {
     getGruppo().setTempoLivelloInf(tempoLivelloInf);
     //this.iTempoLivelloInf = tempoLivelloInf;
     setDirty();
  }
  
  public BigDecimal getTempoTotale() {
     return getGruppo().getTempoTotale();
     //return iTempoTotale;
  }

  public void setTempoTotale(BigDecimal tempoTotale) {
     getGruppo().setTempoTotale(tempoTotale);
     //this.iTempoTotale = tempoTotale;
     setDirty();
  }
  
  public CostiCommessaDetGruppo getGruppo()
  {
     return gruppo;
  }
  
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    VariaBudgetCommessaDetPO variabudgetCommessaDetPO = (VariaBudgetCommessaDetPO)obj;
    iComponenteCosto.setEqual(variabudgetCommessaDetPO.iComponenteCosto);
    iVariaBudgetCommessa.setEqual(variabudgetCommessaDetPO.iVariaBudgetCommessa);
    iDatiComuniEstesi.setEqual(variabudgetCommessaDetPO.iDatiComuniEstesi);
    gruppo.setEqual(variabudgetCommessaDetPO.gruppo);
  }

  public Vector checkAll(BaseComponentsCollection components) {
    Vector errors = new Vector();
    components.runAllChecks(errors);
    return errors;
  }

  public void setKey(String key) {
    String[] keys = KeyHelper.unpackObjectKey(key);
    setIdAzienda(keys[0]);
    setIdBudget(KeyHelper.stringToIntegerObj(keys[1]));
    setIdCommessa(keys[2]);
    setIdComponCosto(keys[3]);
  }

  public String getKey() {
    String idAzienda = getIdAzienda();
    Integer idBudget = getIdBudget();
    String idCommessa = getIdCommessa();
    String idComponCosto = getIdComponCosto();
    Object[] keyParts = {idAzienda, idBudget, idCommessa, idComponCosto};
    return KeyHelper.buildObjectKey(keyParts);
  }

  public boolean isDeletable() {
    return checkDelete() == null;
  }

  public String getFatherKey() {
    return getVariaBudgetCommessaKey();
  }

  public void setFatherKey(String key) {
    setVariaBudgetCommessaKey(key);
  }

  public void setFather(PersistentObject father) {
    iVariaBudgetCommessa.setObject(father);
  }

  public String getOrderByClause() {
    return VariaBudgetCommessaDetTM.ID_COMPON_COSTO;
  }

  public String toString() {
    return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
  }

  protected TableManager getTableManager() throws SQLException {
    return VariaBudgetCommessaDetTM.getInstance();
  }

  protected void setIdAziendaInternal(String idAzienda) {
    String key1 = iComponenteCosto.getKey();
    iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
    String key2 = iVariaBudgetCommessa.getKey();
    iVariaBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
  }

}


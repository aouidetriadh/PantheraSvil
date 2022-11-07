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
 * BudgetCommessaDetPO
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/10/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34585   29/10/2021    RA		    Prima struttura
 */
public abstract class BudgetCommessaDetPO extends EntitaAzienda implements BusinessObject, Authorizable, Deletable, Child, ConflictableWithKey {

  /**
   *  instance
   */
  private static BudgetCommessaDet cInstance;
  
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
  protected Proxy iBudgetCommessa = new Proxy(it.thera.thip.produzione.commessa.BudgetCommessa.class);
  
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
      cInstance = (BudgetCommessaDet)Factory.createObject(BudgetCommessaDet.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  public static BudgetCommessaDet elementWithKey(String key, int lockType) throws SQLException {
    return (BudgetCommessaDet)PersistentObject.elementWithKey(BudgetCommessaDet.class, key, lockType);
  }

  public BudgetCommessaDetPO() {
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

  public void setBudgetCommessa(BudgetCommessa budgetCommessa) {
    String idAzienda = null;
    if (budgetCommessa != null) {
      idAzienda = KeyHelper.getTokenObjectKey(budgetCommessa.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iBudgetCommessa.setObject(budgetCommessa);
    setDirty();
    setOnDB(false);
  }

  public BudgetCommessa getBudgetCommessa() {
    return (BudgetCommessa)iBudgetCommessa.getObject();
  }

  public void setBudgetCommessaKey(String key) {
    iBudgetCommessa.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  public String getBudgetCommessaKey() {
    return iBudgetCommessa.getKey();
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
    String key = iBudgetCommessa.getKey();
    iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idBudget));
    setDirty();
    setOnDB(false);
  }

  public Integer getIdBudget() {
    String key = iBudgetCommessa.getKey();
    String objIdBudget = KeyHelper.getTokenObjectKey(key,2);
    return KeyHelper.stringToIntegerObj(objIdBudget);
  }

  public void setIdCommessa(String idCommessa) {
    String key = iBudgetCommessa.getKey();
    iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 3, idCommessa));
    setDirty();
    setOnDB(false);
  }

  public String getIdCommessa() {
    String key = iBudgetCommessa.getKey();
    String objIdCommessa = KeyHelper.getTokenObjectKey(key,3);
    return objIdCommessa;
  }

  public DatiComuniEstesi getDatiComuniEstesi() {
    return iDatiComuniEstesi;
  }
  
  public BigDecimal getCostoLivello() {
     return getGruppo().getCostoLivello();
  }

  public void setCostoLivello(BigDecimal costoLivello) {
     getGruppo().setCostoLivello(costoLivello);
     setDirty();
  }
	
  public BigDecimal getCostoLivelloInf() {
     return getGruppo().getCostoLivelloInf();
  }

  public void setCostoLivelloInf(BigDecimal costoLivelloInf) {
     getGruppo().setCostoLivelloInf(costoLivelloInf);
     setDirty();
  }
	
  public BigDecimal getCostoTotale() {
     return getGruppo().getCostoTotale();
  }

  public void setCostoTotale(BigDecimal costoTotale) {
	  getGruppo().setCostoTotale(costoTotale);
	  setDirty();
  }

  public BigDecimal getTempoLivello() {
     return getGruppo().getTempoLivello();
  }

  public void setTempoLivello(BigDecimal tempoLivello) {
	  getGruppo().setTempoLivello(tempoLivello);
	  setDirty();
  }
	
  public BigDecimal getTempoLivelloInf() {
     return getGruppo().getTempoLivelloInf();
  }

  public void setTempoLivelloInf(BigDecimal tempoLivelloInf) {
	  getGruppo().setTempoLivelloInf(tempoLivelloInf);
	  setDirty();
  }
  
  public BigDecimal getTempoTotale() {
     return getGruppo().getTempoTotale();
  }

  public void setTempoTotale(BigDecimal tempoTotale) {
	  getGruppo().setTempoTotale(tempoTotale);
	  setDirty();
  }
  
  public CostiCommessaDetGruppo getGruppo() {
     return gruppo;
  }
  
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    BudgetCommessaDetPO budgetCommessaDetPO = (BudgetCommessaDetPO)obj;
    iComponenteCosto.setEqual(budgetCommessaDetPO.iComponenteCosto);
    iBudgetCommessa.setEqual(budgetCommessaDetPO.iBudgetCommessa);
    iDatiComuniEstesi.setEqual(budgetCommessaDetPO.iDatiComuniEstesi);
    gruppo.setEqual(budgetCommessaDetPO.gruppo);
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
    return getBudgetCommessaKey();
  }

  public void setFatherKey(String key) {
    setBudgetCommessaKey(key);
  }

  public void setFather(PersistentObject father) {
    iBudgetCommessa.setObject(father);
  }

  public String getOrderByClause() {
    return BudgetCommessaDetTM.ID_COMPON_COSTO;
  }

  public String toString() {
    return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
  }

  protected TableManager getTableManager() throws SQLException {
    return BudgetCommessaDetTM.getInstance();
  }

  protected void setIdAziendaInternal(String idAzienda) {
    String key1 = iComponenteCosto.getKey();
    iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
    String key2 = iBudgetCommessa.getKey();
    iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
  }

}


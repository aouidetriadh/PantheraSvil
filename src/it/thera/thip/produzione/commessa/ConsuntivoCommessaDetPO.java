package it.thera.thip.produzione.commessa;
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
 * ConsuntivoCommessaDetPO
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/08/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 33950   18/08/2021    RA		    Prima struttura
 */
public abstract class ConsuntivoCommessaDetPO extends EntitaAzienda implements BusinessObject, Authorizable, Deletable, Child, ConflictableWithKey {

   /**
    *  instance
    */
   private static ConsuntivoCommessaDet cInstance;

   /**
    * Attributo iIdConsuntivo
    */
   protected Integer iIdConsuntivo;

   /**
    * Attributo iComponenteCosto
    */
   protected Proxy iComponenteCosto = new Proxy(it.thera.thip.datiTecnici.costi.ComponenteCosto.class);

   /**
    * Attributo iCostiCommessaElem
    */
   protected Proxy iConsuntivoCommessa = new Proxy(it.thera.thip.produzione.commessa.ConsuntivoCommessa.class);

   /**
    * Attributo iConsolidato
    */
   protected CostiCommessaDetGruppo iConsolidato;

   /**
    * Attributo iRichiesto
    */
   protected CostiCommessaDetGruppo iRichiesto;

   /**
    * Attributo iOrdinato
    */
   protected CostiCommessaDetGruppo iOrdinato;

   /**
    * Attributo iEffettuato
    */
   protected CostiCommessaDetGruppo iEffettuato;

   /**
    * Attributo iTotale
    */
   protected CostiCommessaDetGruppo iTotale;

   /**
    * Attributo iDatiComuniEstesi
    */
   protected DatiComuniEstesi iDatiComuniEstesi;

   public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
      if (cInstance == null)
         cInstance = (ConsuntivoCommessaDet)Factory.createObject(ConsuntivoCommessaDet.class);
      return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
   }

   public static ConsuntivoCommessaDet elementWithKey(String key, int lockType) throws SQLException {
      return (ConsuntivoCommessaDet)PersistentObject.elementWithKey(ConsuntivoCommessaDet.class, key, lockType);
   }

   public ConsuntivoCommessaDetPO() {
      iDatiComuniEstesi = (DatiComuniEstesi) Factory.createObject(DatiComuniEstesi.class);
      iDatiComuniEstesi.setOwner(this);
      iConsolidato = (CostiCommessaDetGruppo) Factory.createObject(CostiCommessaDetGruppo.class);
      iConsolidato.setOwner(this);
      iConsolidato.tipoGruppo = ConsuntivoCommessaDet.TIPO_CONSOLIDATO;
      iRichiesto = (CostiCommessaDetGruppo) Factory.createObject(CostiCommessaDetGruppo.class);
      iRichiesto.setOwner(this);
      iRichiesto.tipoGruppo = ConsuntivoCommessaDet.TIPO_RICHIESTO;
      iOrdinato = (CostiCommessaDetGruppo) Factory.createObject(CostiCommessaDetGruppo.class);
      iOrdinato.setOwner(this);
      iOrdinato.tipoGruppo = ConsuntivoCommessaDet.TIPO_ORDINATO;
      iEffettuato = (CostiCommessaDetGruppo) Factory.createObject(CostiCommessaDetGruppo.class);
      iEffettuato.setOwner(this);
      iEffettuato.tipoGruppo = ConsuntivoCommessaDet.TIPO_EFFETUATO;
      iTotale = (CostiCommessaDetGruppo) Factory.createObject(CostiCommessaDetGruppo.class);
      iTotale.setOwner(this);
      iTotale.tipoGruppo = ConsuntivoCommessaDet.TIPO_TOTALE;
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

   public void setConsuntivoCommessa(ConsuntivoCommessa consuntivoCommessa) {
      String idAzienda = null;
      if (consuntivoCommessa != null) {
         idAzienda = KeyHelper.getTokenObjectKey(consuntivoCommessa.getKey(), 1);
      }
      setIdAziendaInternal(idAzienda);
      this.iConsuntivoCommessa.setObject(consuntivoCommessa);
      setDirty();
      setOnDB(false);
   }

   public ConsuntivoCommessa getConsuntivoCommessa() {
      return (ConsuntivoCommessa)iConsuntivoCommessa.getObject();
   }

   public void setConsuntivoCommessaKey(String key) {
      iConsuntivoCommessa.setKey(key);
      String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
      setIdAziendaInternal(idAzienda);
      setDirty();
      setOnDB(false);
   }

   public String getConsuntivoCommessaKey() {
      return iConsuntivoCommessa.getKey();
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

   public void setIdConsuntivo(Integer idConsuntivo) {
      String key = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idConsuntivo));
      setDirty();
      setOnDB(false);
   }

   public Integer getIdConsuntivo() {
      String key = iConsuntivoCommessa.getKey();
      String objIdConsuntivo = KeyHelper.getTokenObjectKey(key,2);
      return KeyHelper.stringToIntegerObj(objIdConsuntivo);

   }

   public void setIdCommessa(String idCommessa) {
      String key = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 3, idCommessa));
      setDirty();
      setOnDB(false);
   }

   public String getIdCommessa() {
      String key = iConsuntivoCommessa.getKey();
      String objIdCommessa = KeyHelper.getTokenObjectKey(key,3);
      return objIdCommessa;

   }

   public DatiComuniEstesi getDatiComuniEstesi() {
      return iDatiComuniEstesi;
   }

   public CostiCommessaDetGruppo getConsolidato() {
      return iConsolidato;
   }

   public CostiCommessaDetGruppo getRichiesto() {
      return iRichiesto;
   }

   public CostiCommessaDetGruppo getOrdinato() {
      return iOrdinato;
   }

   public CostiCommessaDetGruppo getEffettuato() {
      return iEffettuato;
   }

   public CostiCommessaDetGruppo getTotale() {
      return iTotale;
   }

   public void setEqual(Copyable obj) throws CopyException {
      super.setEqual(obj);
      ConsuntivoCommessaDetPO consuntivoCommessaDetPO = (ConsuntivoCommessaDetPO)obj;
      iComponenteCosto.setEqual(consuntivoCommessaDetPO.iComponenteCosto);
      iConsuntivoCommessa.setEqual(consuntivoCommessaDetPO.iConsuntivoCommessa);
      iDatiComuniEstesi.setEqual(consuntivoCommessaDetPO.iDatiComuniEstesi);
      iConsolidato.setEqual(consuntivoCommessaDetPO.iConsolidato);
      iRichiesto.setEqual(consuntivoCommessaDetPO.iRichiesto);
      iOrdinato.setEqual(consuntivoCommessaDetPO.iOrdinato);
      iEffettuato.setEqual(consuntivoCommessaDetPO.iEffettuato);
      iTotale.setEqual(consuntivoCommessaDetPO.iTotale);
   }

   public Vector checkAll(BaseComponentsCollection components) {
      Vector errors = new Vector();
      components.runAllChecks(errors);
      return errors;
   }

   public void setKey(String key) {
      String[] keys = KeyHelper.unpackObjectKey(key);
      setIdAzienda(keys[0]);
      setIdConsuntivo(KeyHelper.stringToIntegerObj(keys[1]));
      setIdCommessa(keys[2]);
      setIdComponCosto(keys[3]);
   }

   public String getKey() {
      String idAzienda = getIdAzienda();
      Integer idConsuntivo = getIdConsuntivo();
      String idCommessa = getIdCommessa();
      String idComponCosto = getIdComponCosto();
      Object[] keyParts = {idAzienda, idConsuntivo, idCommessa, idComponCosto};
      return KeyHelper.buildObjectKey(keyParts);
   }

   public boolean isDeletable() {
      return checkDelete() == null;
   }

   public String getFatherKey() {
      return getConsuntivoCommessaKey();
   }

   public void setFatherKey(String key) {
      setConsuntivoCommessaKey(key);
   }

   public void setFather(PersistentObject father) {
      iConsuntivoCommessa.setObject(father);
   }

   public String getOrderByClause() {
      return ConsuntivoCommessaDetTM.ID_COMPON_COSTO;
   }

   public String toString() {
      return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
   }

   protected TableManager getTableManager() throws SQLException {
      return ConsuntivoCommessaDetTM.getInstance();
   }

   protected void setIdAziendaInternal(String idAzienda) {
      String key1 = iComponenteCosto.getKey();
      iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
      String key2 = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
   }

}


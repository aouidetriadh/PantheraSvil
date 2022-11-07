package it.thera.thip.produzione.commessa;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.OneToMany;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.ConflictableWithKey;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.articolo.ArticoloVersione;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.azienda.Stabilimento;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.cs.DatiComuniEstesi;
import it.thera.thip.cs.EntitaAzienda;
import it.thera.thip.datiTecnici.configuratore.Configurazione;
import it.thera.thip.datiTecnici.configuratore.ConfigurazioneProxyEnh;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;


/*
 * @(#)ConsuntivoCommessaPO.java
 */

/**
 * ConsuntivoCommessaPO
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/08/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 33950   18/08/2021    RA		    Prima struttura
 */
public abstract class ConsuntivoCommessaPO extends EntitaAzienda implements BusinessObject, Authorizable, Deletable, ConflictableWithKey {

  /**
   *  instance
   */
  private static ConsuntivoCommessa cInstance;

  /**
   * Attributo iIdConsuntivo
   */
  protected Integer iIdConsuntivo;

  /**
   * Attributo iLivelloCommessa
   */
  protected Integer iLivelloCommessa = new Integer(0);

  /**
   * Attributo iUsaDataEstrazioneStorici
   */
  protected boolean iUsaDataEstrazioneStorici = true;

  /**
   * Attributo iDataRiferimento
   */
  protected java.sql.Date iDataRiferimento;

  /**
   * Attributo iStatoAvanzamento
   */
  protected char iStatoAvanzamento = ConsuntivoCommessa.PROVVISORIO;
  
  /**
   * Attributo iDescrizione
   */
  protected String iDescrizione;
  
  /**
   * Attributo iQuantitaPrm
   */
  protected BigDecimal iQuantitaPrm = ConsuntivoCommessa.ZERO;
  
  /**
   * Attributo iConsolidato
   */
  protected boolean iConsolidato = false;
  
  /**
   * Attributo iEstrazioneOrdini
   */
  protected boolean iEstrazioneOrdini = false;
  
  /**
   * Attributo iEstrazioneRichieste
   */
  protected boolean iEstrazioneRichieste = false;

  /**
   * Attributo iCostoRiferimento
   */
  protected BigDecimal iCostoRiferimento = ConsuntivoCommessa.ZERO;
  
  /**
   * Attributo iCostoPrimo
   */
  protected BigDecimal iCostoPrimo = ConsuntivoCommessa.ZERO;
  
  /**
   * Attributo iCostoIndustriale
   */
  protected BigDecimal iCostoIndustriale = ConsuntivoCommessa.ZERO;
  
  /**
   * Attributo iCostoGenerale
   */
  protected BigDecimal iCostoGenerale = ConsuntivoCommessa.ZERO;
  /**
   * Attributo iAzienda
   */
  protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);
  
  /**
  * Attributo iArticolo.
  */
  protected Proxy iArticolo = new Proxy(it.thera.thip.base.articolo.Articolo.class);
  
  /**
   * Attributo iVersione
   */
  protected Proxy iVersione = new Proxy(it.thera.thip.base.articolo.ArticoloVersione.class);

  /**
   * Attributo iConfigurazione
   */
  protected ConfigurazioneProxyEnh iConfigurazione = new ConfigurazioneProxyEnh(it.thera.thip.datiTecnici.configuratore.Configurazione.class);

  /**
  * Attributo iStabilimento
  */
  protected Proxy iStabilimento = new Proxy(it.thera.thip.base.azienda.Stabilimento.class);

  /**
   * Attributo iCommessa
   */
  protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iCommessaAppart
   */
  protected Proxy iCommessaAppart = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iCommessaPrm
   */
  protected Proxy iCommessaPrm = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iUMPrmMag
   */
  protected Proxy iUMPrmMag = new Proxy(it.thera.thip.base.generale.UnitaMisura.class);
  
  /**
   * Attributo iComponenteTotali
   */
  protected Proxy iComponenteTotali = new Proxy(it.thera.thip.datiTecnici.costi.ComponenteCosto.class);

  /**
   * Attributo iDatiComuniEstesi
   */
  protected DatiComuniEstesi iDatiComuniEstesi;

  /**
   * Attributo iConsuntivoCommessa
   */
  protected OneToMany iConsuntivoCommessaDet = new OneToMany(it.thera.thip.produzione.commessa.ConsuntivoCommessaDet.class, this, 7, false);

  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (cInstance == null)
      cInstance = (ConsuntivoCommessa)Factory.createObject(ConsuntivoCommessa.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  public static ConsuntivoCommessa elementWithKey(String key, int lockType) throws SQLException {
    return (ConsuntivoCommessa)PersistentObject.elementWithKey(ConsuntivoCommessa.class, key, lockType);
  }

  public ConsuntivoCommessaPO() {
    iDatiComuniEstesi = (DatiComuniEstesi) Factory.createObject(DatiComuniEstesi.class);
    iDatiComuniEstesi.setOwner(this);
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  public void setIdConsuntivo(Integer idConsuntivo) {
    this.iIdConsuntivo = idConsuntivo;
    setDirty();
    setOnDB(false);
    iConsuntivoCommessaDet.setFatherKeyChanged();
  }

  public Integer getIdConsuntivo() {
    return iIdConsuntivo;
  }

  public void setLivelloCommessa(Integer livello) {
    iLivelloCommessa = livello;
    setDirty();
  }

  public Integer getLivelloCommessa() {
    return iLivelloCommessa;
  }
  
  public void setUsaDataEstrazioneStorici(boolean usaDataEstrazioneStorici) {
	  this.iUsaDataEstrazioneStorici = usaDataEstrazioneStorici;
	  setDirty();
  }

  public boolean isUsaDataEstrazioneStorici() {
	  return iUsaDataEstrazioneStorici;
  }
  
  public void setDataRiferimento(java.sql.Date dataRiferimento) {
    this.iDataRiferimento = dataRiferimento;
    setDirty();
  }

  public java.sql.Date getDataRiferimento() {
    return iDataRiferimento;
  }

  public void setStatoAvanzamento(char stato) {
	  this.iStatoAvanzamento = stato;
	  setDirty();
  }

  public char getStatoAvanzamento() {
	  return iStatoAvanzamento;
  }

  public void setDescrizione(String descrizione) {
	  this.iDescrizione = descrizione;
	  setDirty();
  }

  public String getDescrizione() {
	  return iDescrizione;
  }
  
  public void setQuantitaPrm(BigDecimal quantita) {
	  this.iQuantitaPrm = quantita;
	  setDirty();
  }

  public BigDecimal getQuantitaPrm() {
	  return iQuantitaPrm;
  }
  
  public void setConsolidato(boolean consolidato) {
	  this.iConsolidato = consolidato;
	  setDirty();
  }

  public boolean isConsolidato() {
	  return iConsolidato;
  }
  
  public void setEstrazioneOrdini(boolean estrazioneOrdini) {
	  this.iEstrazioneOrdini = estrazioneOrdini;
	  setDirty();
  }

  public boolean isEstrazioneOrdini() {
	  return iEstrazioneOrdini;
  }
  
  public void setEstrazioneRichieste(boolean estrazioneRichieste) {
	  this.iEstrazioneRichieste = estrazioneRichieste;
	  setDirty();
  }

  public boolean isEstrazioneRichieste() {
	  return iEstrazioneRichieste;
  }
  
  public void setCostoRiferimento(BigDecimal costo) {
	  this.iCostoRiferimento = costo;
	  setDirty();
  }

  public BigDecimal getCostoRiferimento() {
	  return iCostoRiferimento;
  }
  
  public void setCostoPrimo(BigDecimal costo) {
	  this.iCostoPrimo = costo;
	  setDirty();
  }

  public BigDecimal getCostoPrimo() {
	  return iCostoPrimo;
  }
  
  public void setCostoIndustriale(BigDecimal costo) {
	  this.iCostoIndustriale = costo;
	  setDirty();
  }

  public BigDecimal getCostoIndustriale() {
	  return iCostoIndustriale;
  }
  
  public void setCostoGenerale(BigDecimal costo) {
	  this.iCostoGenerale = costo;
	  setDirty();
  }

  public BigDecimal getCostoGenerale() {
	  return iCostoGenerale;
  }
  
  public void setVersione(ArticoloVersione versione) {
    String oldObjectKey = getKey();
    String idAzienda = null;
    if (versione != null) {
      idAzienda = KeyHelper.getTokenObjectKey(versione.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iVersione.setObject(versione);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public ArticoloVersione getVersione() {
    return (ArticoloVersione)iVersione.getObject();
  }

  public void setVersioneKey(String key) {
    String oldObjectKey = getKey();
    iVersione.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public String getVersioneKey() {
    return iVersione.getKey();
  }

  public void setArticolo(Articolo articolo) {
    String oldObjectKey = getKey();
    String idAzienda = null;
    if (articolo != null) {
      idAzienda = KeyHelper.getTokenObjectKey(articolo.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    String idArticolo = null;
    if (articolo != null) {
      idArticolo = KeyHelper.getTokenObjectKey(articolo.getKey(), 2);
    }
    setIdArticoloInternal(idArticolo);
    this.iArticolo.setObject(articolo);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
    }
  }

  public Articolo getArticolo() {
    return (Articolo)iArticolo.getObject();
  }

  public void setArticoloKey(String key) {
    String oldObjectKey = getKey();
    iArticolo.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    String idArticolo = KeyHelper.getTokenObjectKey(key, 2);
    setIdArticoloInternal(idArticolo);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
    }
  }

  public String getArticoloKey() {
    return iArticolo.getKey();
  }

  public void setIdArticolo(String idArticolo) {
    setIdArticoloInternal(idArticolo);
    setDirty();
  }

  public String getIdArticolo() {
    String key = iVersione.getKey();
    String objIdArticolo = KeyHelper.getTokenObjectKey(key,2);
    return objIdArticolo;

  }

  protected void setIdArticoloInternal(String idArticolo) {
    String key1 = iVersione.getKey();
    iVersione.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, idArticolo));
    String key2 = iArticolo.getKey();
    iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key2, 2, idArticolo));
    iConfigurazione.setIdArticolo(idArticolo);
  }

  public void setIdVersione(Integer idVersione) {
    String key = iVersione.getKey();
    iVersione.setKey(KeyHelper.replaceTokenObjectKey(key , 3, idVersione));
    setDirty();
  }

  public Integer getIdVersione() {
    String key = iVersione.getKey();
    String objIdVersione = KeyHelper.getTokenObjectKey(key,3);
    return KeyHelper.stringToIntegerObj(objIdVersione);
  }

  public void setAzienda(Azienda azienda) {
    setIdAziendaInternal(azienda.getKey());
    this.iAzienda.setObject(azienda);
    setDirty();
    setOnDB(false);
    iConsuntivoCommessaDet.setFatherKeyChanged();
  }

  public Azienda getAzienda() {
    return (Azienda)iAzienda.getObject();
  }

  public void setAziendaKey(String key) {
    iAzienda.setKey(key);
    setIdAziendaInternal(key);
    setDirty();
    setOnDB(false);
    iConsuntivoCommessaDet.setFatherKeyChanged();
  }

  public String getAziendaKey() {
    return iAzienda.getKey();
  }

  public void setCommessa(Commessa commessa) {
    String idAzienda = null;
    if (commessa != null) {
      idAzienda = KeyHelper.getTokenObjectKey(commessa.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iCommessa.setObject(commessa);
    setDirty();
    setOnDB(false);
    iConsuntivoCommessaDet.setFatherKeyChanged();
  }

  public Commessa getCommessa() {
    return (Commessa)iCommessa.getObject();
  }

  public void setCommessaKey(String key) {
    iCommessa.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
    iConsuntivoCommessaDet.setFatherKeyChanged();
  }

  public String getCommessaKey() {
    return iCommessa.getKey();
  }

  public void setIdCommessa(String idCommessa) {
    String key = iCommessa.getKey();
    iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idCommessa));
    setDirty();
    setOnDB(false);
    iConsuntivoCommessaDet.setFatherKeyChanged();
  }

  public String getIdCommessa() {
    String key = iCommessa.getKey();
    String objIdCommessa = KeyHelper.getTokenObjectKey(key,2);
    return objIdCommessa;
  }

  public void setCommessaAppart(Commessa commessaAppart) {
    String oldObjectKey = getKey();
    String idAzienda = null;
    if (commessaAppart != null) {
      idAzienda = KeyHelper.getTokenObjectKey(commessaAppart.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iCommessaAppart.setObject(commessaAppart);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public Commessa getCommessaAppart() {
    return (Commessa)iCommessaAppart.getObject();
  }

  public void setCommessaAppartKey(String key) {
    String oldObjectKey = getKey();
    iCommessaAppart.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public String getCommessaAppartKey() {
    return iCommessaAppart.getKey();
  }

  public void setIdCommessaApp(String idCommessaApp) {
    String key = iCommessaAppart.getKey();
    iCommessaAppart.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idCommessaApp));
    setDirty();
  }

  public String getIdCommessaApp() {
    String key = iCommessaAppart.getKey();
    String objIdCommessaApp = KeyHelper.getTokenObjectKey(key,2);
    return objIdCommessaApp;
  }

  public void setCommessaPrm(Commessa commessaPrm) {
    String oldObjectKey = getKey();
    String idAzienda = null;
    if (commessaPrm != null) {
      idAzienda = KeyHelper.getTokenObjectKey(commessaPrm.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iCommessaPrm.setObject(commessaPrm);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public Commessa getCommessaPrm() {
    return (Commessa)iCommessaPrm.getObject();
  }

  public void setCommessaPrmKey(String key) {
    String oldObjectKey = getKey();
    iCommessaPrm.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public String getCommessaPrmKey() {
    return iCommessaPrm.getKey();
  }

  public void setIdCommessaPrm(String idCommessaPrm) {
    String key = iCommessaPrm.getKey();
    iCommessaPrm.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idCommessaPrm));
    setDirty();
  }

  public String getIdCommessaPrm() {
    String key = iCommessaPrm.getKey();
    String objIdCommessaPrm = KeyHelper.getTokenObjectKey(key,2);
    return objIdCommessaPrm;
  }

  public void setConfigurazione(Configurazione configurazione) {
    String oldObjectKey = getKey();
    String idAzienda = null;
    if (configurazione != null) {
      idAzienda = KeyHelper.getTokenObjectKey(configurazione.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iConfigurazione.setObject(configurazione);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public Configurazione getConfigurazione() {
    return (Configurazione)iConfigurazione.getObject();
  }

  public void setConfigurazioneKey(String key) {
    String oldObjectKey = getKey();
    iConfigurazione.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
      setOnDB(false);
      iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public String getConfigurazioneKey() {
    return iConfigurazione.getKey();
  }

  /**
   * used for configurazione, inorder to return the correct id (and to filter by IdArticolo)
   * @return String
   */
  public String getIdEsternoConfig() {
    return iConfigurazione.getIdEsternoConfig();
  }

  public void setIdEsternoConfig(String idEsternoConfig) {
    iConfigurazione.setIdEsternoConfig(idEsternoConfig);
  }

  public void setStabilimento(Stabilimento stabilimento) {
    String oldObjectKey = getKey();
     String idAzienda = null;
     if (stabilimento != null) {
       idAzienda = KeyHelper.getTokenObjectKey(stabilimento.getKey(), 1);
     }
     setIdAziendaInternal(idAzienda);
     this.iStabilimento.setObject(stabilimento);
     setDirty();
     if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
       setOnDB(false);
       iConsuntivoCommessaDet.setFatherKeyChanged();
    }
  }

  public Stabilimento getStabilimento() {
    return (Stabilimento)iStabilimento.getObject();
  }

  public void setStabilimentoKey(String key) {
	  String oldObjectKey = getKey();
	  iStabilimento.setKey(key);
	  String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
	  setIdAziendaInternal(idAzienda);
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
		  iConsuntivoCommessaDet.setFatherKeyChanged();
	  }
  }

  public String getStabilimentoKey() {
     return iStabilimento.getKey();
  }

  public void setIdStabilimento(String idStabilimento) {
    String key = iStabilimento.getKey();
    iStabilimento.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idStabilimento));
    setDirty();
  }

  public String getIdStabilimento() {
    String key = iStabilimento.getKey();
    String objIdStabilimento = KeyHelper.getTokenObjectKey(key,2);
    return objIdStabilimento;
  }

  public void setIdAzienda(String idAzienda) {
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
    iConsuntivoCommessaDet.setFatherKeyChanged();
  }

  public String getIdAzienda() {
    String key = iVersione.getKey();
    String objIdAzienda = KeyHelper.getTokenObjectKey(key,1);
    return objIdAzienda;

  }

  public void setIdConfigurazione(Integer idConfigurazione) {
    String key = iConfigurazione.getKey();
    iConfigurazione.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idConfigurazione));
    setDirty();
  }

  public Integer getIdConfigurazione() {
    String key = iConfigurazione.getKey();
    String objIdConfigurazione = KeyHelper.getTokenObjectKey(key,2);
    return KeyHelper.stringToIntegerObj(objIdConfigurazione);
  }
  
  public void setUMPrmMag(UnitaMisura uMPrmMag) {
	  String oldObjectKey = getKey();
	  String idAzienda = null;
	  if (uMPrmMag != null) {
		  idAzienda = KeyHelper.getTokenObjectKey(uMPrmMag.getKey(), 1);
	  }
	  setIdAziendaInternal(idAzienda);
	  this.iUMPrmMag.setObject(uMPrmMag);
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
		  iConsuntivoCommessaDet.setFatherKeyChanged();
	  }
  }

  public UnitaMisura getUMPrmMag() {
	  return (UnitaMisura)iUMPrmMag.getObject();
  }

  public void setUMPrmMagKey(String key) {
	  String oldObjectKey = getKey();
	  iUMPrmMag.setKey(key);
	  String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
	  setIdAziendaInternal(idAzienda);
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
		  iConsuntivoCommessaDet.setFatherKeyChanged();
	  }
  }

  public String getUMPrmMagKey() {
	  return iUMPrmMag.getKey();
  }
  
  public void setIdUMPrmMag(String idUmPrmMag) {
	  String key = iUMPrmMag.getKey();
	  iUMPrmMag.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idUmPrmMag));
	  setDirty();
  }

  public String getIdUMPrmMag() {
	  String key = iUMPrmMag.getKey();
	  String objIdUmPrmMag = KeyHelper.getTokenObjectKey(key,2);
	  return objIdUmPrmMag;
  }  

  public void setComponenteTotali(ComponenteCosto componenteCosto) {
	  String oldObjectKey = getKey();
	  String idAzienda = null;
	  if (componenteCosto != null) {
		  idAzienda = KeyHelper.getTokenObjectKey(componenteCosto.getKey(), 1);
	  }
	  setIdAziendaInternal(idAzienda);
	  this.iComponenteTotali.setObject(componenteCosto);
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
		  iConsuntivoCommessaDet.setFatherKeyChanged();
	  }
  }

  public ComponenteCosto getComponenteCosto() {
	  return (ComponenteCosto)iComponenteTotali.getObject();
  }

  public void setComponenteTotaliKey(String key) {
	  String oldObjectKey = getKey();
	  iComponenteTotali.setKey(key);
	  String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
	  setIdAziendaInternal(idAzienda);
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
		  iConsuntivoCommessaDet.setFatherKeyChanged();
	  }
  }

  public String getComponenteTotaliKey() {
	  return iComponenteTotali.getKey();
  }
  
  public void setIdComponenteTotali(String idComponCosto) {
	  String oldObjectKey = getKey();
	  String key = iComponenteTotali.getKey();
	  iComponenteTotali.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idComponCosto));
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
		  iConsuntivoCommessaDet.setFatherKeyChanged();
	  }
  }

  public String getIdComponenteTotali() {
	  String key = iComponenteTotali.getKey();
	  String objIdComponCosto = KeyHelper.getTokenObjectKey(key,2);
	  return objIdComponCosto;
  }
	  
  public DatiComuniEstesi getDatiComuniEstesi() {
    return iDatiComuniEstesi;
  }

  public List getConsuntivoCommessaDet() {
    return getConsuntivoCommessaDetInternal();
  }

  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    ConsuntivoCommessaPO consuntivoCommessaPO = (ConsuntivoCommessaPO)obj;
    if (consuntivoCommessaPO.iDataRiferimento != null)
        iDataRiferimento = (java.sql.Date)consuntivoCommessaPO.iDataRiferimento.clone();
    iVersione.setEqual(consuntivoCommessaPO.iVersione);
    iAzienda.setEqual(consuntivoCommessaPO.iAzienda);
    iCommessa.setEqual(consuntivoCommessaPO.iCommessa);
    iCommessaAppart.setEqual(consuntivoCommessaPO.iCommessaAppart);
    iCommessaPrm.setEqual(consuntivoCommessaPO.iCommessaPrm);
    iConfigurazione.setEqual(consuntivoCommessaPO.iConfigurazione);
    iStabilimento.setEqual(consuntivoCommessaPO.iStabilimento);
    iUMPrmMag.setEqual(consuntivoCommessaPO.iUMPrmMag);
    iComponenteTotali.setEqual(consuntivoCommessaPO.iComponenteTotali);
    iDatiComuniEstesi.setEqual(consuntivoCommessaPO.iDatiComuniEstesi);
    iConsuntivoCommessaDet.setEqual(consuntivoCommessaPO.iConsuntivoCommessaDet);
  }

  public void setKey(String key) {
    setIdAzienda(KeyHelper.getTokenObjectKey(key,1));
    Integer idConsuntivo = KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(key,2));
    setIdConsuntivo(idConsuntivo);
    setIdCommessa(KeyHelper.getTokenObjectKey(key,3));
  }

  public String getKey() {
    String idAzienda = getIdAzienda();
    Integer idProgrStoric = getIdConsuntivo();
    String idCommessa = getIdCommessa();
    Object[] keyParts = {idAzienda, idProgrStoric, idCommessa};
    return KeyHelper.buildObjectKey(keyParts);
  }

  public boolean isDeletable() {
    return checkDelete() == null;
  }

  public int saveOwnedObjects(int rc) throws SQLException {
    rc = iConsuntivoCommessaDet.save(rc);
    return rc;
  }

  public int deleteOwnedObjects() throws SQLException {
    return getConsuntivoCommessaDetInternal().delete();
  }

  public boolean initializeOwnedObjects(boolean result) {
    result = iConsuntivoCommessaDet.initialize(result);
    return result;
  }

  public String toString() {
    return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
  }

  protected TableManager getTableManager() throws SQLException {
    return ConsuntivoCommessaTM.getInstance();
  }

  protected OneToMany getConsuntivoCommessaDetInternal() {
    if (iConsuntivoCommessaDet.isNew())
        iConsuntivoCommessaDet.retrieve();
    return iConsuntivoCommessaDet;
  }
  
  protected void setIdAziendaInternal(String idAzienda) {
    String key1 = iVersione.getKey();
    iVersione.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
    iAzienda.setKey(idAzienda);
    String key3 = iCommessaAppart.getKey();
    iCommessaAppart.setKey(KeyHelper.replaceTokenObjectKey(key3, 1, idAzienda));
    String key4 = iCommessa.getKey();
    iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key4, 1, idAzienda));
    String key5 = iCommessaPrm.getKey();
    iCommessaPrm.setKey(KeyHelper.replaceTokenObjectKey(key5, 1, idAzienda));
    String key6 = iConfigurazione.getKey();
    iConfigurazione.setKey(KeyHelper.replaceTokenObjectKey(key6, 1, idAzienda));
    String key7 = iStabilimento.getKey();
    iStabilimento.setKey(KeyHelper.replaceTokenObjectKey(key7, 1, idAzienda));
    String key8 = iArticolo.getKey();
    iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key8, 1, idAzienda));
    String key9 = iUMPrmMag.getKey();
    iUMPrmMag.setKey(KeyHelper.replaceTokenObjectKey(key9, 1, idAzienda));
    String key10 = iComponenteTotali.getKey();
    iComponenteTotali.setKey(KeyHelper.replaceTokenObjectKey(key10, 1, idAzienda));
  }

}


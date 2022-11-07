/*
 * @(#)RptPreventivoCommessaVocePO.java
 */

/**
 * RptPrevComVoce
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 17:11:10
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 16/12/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 33048     04/03/2021    RA       Aggiunto attributo CommentiPiede
 */
package it.thera.thip.base.commessa;
import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;

public abstract class RptPreventivoCommessaVocePO extends PersistentObject implements BusinessObject, Authorizable, Deletable, ConflictableWithKey {


  /**
   *  instance
   */
  private static RptPreventivoCommessaVoce cInstance;
  //
  public static BigDecimal ZERO = new BigDecimal("0");
  /**
   * Attributo iDetRigaJob2
   */
  protected Integer iDetRigaJob2;

  /**
   * Attributo iIdAzienda
   */
  protected String iIdAzienda;

  /**
   * Attributo iIdAnnoPrevc
   */
  protected String iIdAnnoPrevc;

  /**
   * Attributo iIdNumeroPrevc
   */
  protected String iIdNumeroPrevc;

  /**
   * Attributo iIdRigacPrv
   */
  protected int iIdRigacPrv;

  /**
   * Attributo iIdRigavPrv
   */
  protected int iIdRigavPrv;

  /**
   * Attributo iIdSubRigavPrv
   */
  protected int iIdSubRigavPrv;

  /**
   * Attributo iIdSubRigacPrv
   */
  protected int iIdSubRigacPrv;

  /**
   * Attributo iSplRiga
   */
  protected char iSplRiga = RptPreventivoCommessaVoce.RIGA_PRIMARIA;

  /**
   * Attributo iTipoRigav
   */
  protected char iTipoRigav = RptPreventivoCommessaVoce.TP_RIG_VOCE;

  /**
   * Attributo iSequenzaRiga
   */
  protected int iSequenzaRiga;

  /**
   * Attributo iRArticolo
   */
  protected String iRArticolo;

  protected String iDescArticolo;

  /**
   * Attributo iRVersione
   */
  protected Integer iRVersione;

  /**
   * Attributo iRConfigurazione
   */
  protected Integer iRConfigurazione;

  protected String iDescConfigurazione;

  /**
   * Attributo iTipoRisorsa
   */
  protected char iTipoRisorsa = RptPreventivoCommessaVoce.TP_RIS_MACCHINA;

  /**
   * Attributo iLivelloRisorsa
   */
  protected char iLivelloRisorsa = RptPreventivoCommessaVoce.LIV_RIS_RISORSA;

  /**
   * Attributo iIdRisorsa
   */
  protected String iIdRisorsa;

  protected String iDescRisorsa;

  /**
   * Attributo iDescrizione
   */
  protected String iDescrizione;

  /**
   * Attributo iDescrRidotta
   */
  protected String iDescrRidotta;

  /**
   * Attributo iRSchemaCosto
   */
  protected String iRSchemaCosto;

  protected String iDescSchemaCosto;

  /**
   * Attributo iRComponCosto
   */
  protected String iRComponCosto;

  protected String iDescComponCosto;

  /**
   * Attributo iRUmPrmMag
   */
  protected String iRUmPrmMag;

  /**
   * Attributo iRUmSecMag
   */
  protected String iRUmSecMag;

  /**
   * Attributo iRUmVen
   */
  protected String iRUmVen;

  /**
   * Attributo iQtaPrvUmVen
   */
  protected BigDecimal iQtaPrvUmVen;

  /**
   * Attributo iQtaPrvUmPrm
   */
  protected BigDecimal iQtaPrvUmPrm;

  /**
   * Attributo iQtaPrvUmSec
   */
  protected BigDecimal iQtaPrvUmSec;

  /**
   * Attributo iDataConsegRcs
   */
  protected java.sql.Date iDataConsegRcs;

  /**
   * Attributo iDataConsegPrv
   */
  protected java.sql.Date iDataConsegPrv;

  /**
   * Attributo iSettConsegRcs
   */
  protected String iSettConsegRcs;

  /**
   * Attributo iSettConsegPrv
   */
  protected String iSettConsegPrv;

  /**
   * Attributo iBlcQtaCmp
   */
  protected boolean iBlcQtaCmp = false;

  /**
   * Attributo iCoeffImp
   */
  protected BigDecimal iCoeffImp;

  /**
   * Attributo iPrezzo
   */
  protected BigDecimal iPrezzo;

  /**
   * Attributo iPrezzoExtra
   */
  protected BigDecimal iPrezzoExtra;

  /**
   * Attributo iNoFattura
   */
  protected boolean iNoFattura = true;

  /**
   * Attributo iProvenienzaPrz
   */
  protected char iProvenienzaPrz = RptPreventivoCommessaVoce.PRV_PREZZO_MANUALE;

  /**
   * Attributo iTpPrezzo
   */
  protected char iTpPrezzo = RptPreventivoCommessaVoce.TP_PREZZO_NORMALE;

  /**
   * Attributo iCostoRifer
   */
  protected BigDecimal iCostoRifer;

  /**
   * Attributo iVlrTotale
   */
  protected BigDecimal iVlrTotale;

  /**
   * Attributo iCosTotale
   */
  protected BigDecimal iCosTotale;

  /**
   * Attributo iMdcTotale
   */
  protected BigDecimal iMdcTotale;
//

  protected BigDecimal iPrezzoAz = ZERO;
  protected BigDecimal iCostoRiferAz = ZERO;
  protected BigDecimal iVlrTotaleAz = ZERO;
  protected BigDecimal iCosTotaleAz = ZERO;
  protected BigDecimal iMdcTotaleAz = ZERO;

  /**
   * Attributo iRGesCommenti
   */
  protected Integer iRGesCommenti;

  /**
   * Attributo iRDocumentoMm
   */
  protected String iRDocumentoMm;

  /**
   * Attributo iNota
   */
  protected String iNota;

  /**
   * Attributo iEscRigOffc
   */
  protected boolean iEscRigOffc = false;

  /**
   * Attributo iRAnnoOff
   */
  protected String iRAnnoOff;

  /**
   * Attributo iRNumOff
   */
  protected String iRNumOff;

  /**
   * Attributo iRRigaOff
   */
  protected int iRRigaOff;

  /**
   * Attributo iRDetRigOff
   */
  protected int iRDetRigOff;

  protected BigDecimal iPercentuale= new BigDecimal(0);
  protected String iLivello;
  protected String iCommenti;
  protected String iCommentiPiede;//33048
  
  /**
   * Attributo iRUtenteCrz
   */
  protected String iRUtenteCrz;

  /**
   * Attributo iRUtenteAgg
   */
  protected String iRUtenteAgg;

  /**
   * Attributo iTimestamp
   */
  protected java.sql.Timestamp iTimestamp;

  /**
   * Attributo iTimestampAgg
   */
  protected java.sql.Timestamp iTimestampAgg;

  /**
   * Attributo iStato
   */
  protected char iStato = RptPreventivoCommessaVoce.STATO_VALIDO;

  /**
   * Attributo iRptPrevComRig
   */
  protected Proxy iRptPrevComRig = new Proxy(it.thera.thip.base.commessa.RptPreventivoCommessaRiga.class);



  /**
   *  retrieveList
   * @param where
   * @param orderBy
   * @param optimistic
   * @return Vector
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (cInstance == null)
      cInstance = (RptPreventivoCommessaVoce)Factory.createObject(RptPreventivoCommessaVoce.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  /**
   *  elementWithKey
   * @param key
   * @param lockType
   * @return RptPreventivoCommessaVoce
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static RptPreventivoCommessaVoce elementWithKey(String key, int lockType) throws SQLException {
    return (RptPreventivoCommessaVoce)PersistentObject.elementWithKey(RptPreventivoCommessaVoce.class, key, lockType);
  }

  /**
   * Valorizza l'attributo.
   * @param detRigaJob2
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDetRigaJob2(Integer detRigaJob2) {
    this.iDetRigaJob2 = detRigaJob2;
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public Integer getDetRigaJob2() {
    return iDetRigaJob2;
  }

  /**
   * Valorizza l'attributo.
   * @param idAzienda
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAzienda(String idAzienda) {
    this.iIdAzienda = idAzienda;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAzienda() {
    return iIdAzienda;
  }

  /**
   * Valorizza l'attributo.
   * @param idAnnoPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoPrevc(String idAnnoPrevc) {
    this.iIdAnnoPrevc = idAnnoPrevc;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAnnoPrevc() {
    return iIdAnnoPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param idNumeroPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNumeroPrevc(String idNumeroPrevc) {
    this.iIdNumeroPrevc = idNumeroPrevc;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getIdNumeroPrevc() {
    return iIdNumeroPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param idRigacPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRigacPrv(int idRigacPrv) {
    this.iIdRigacPrv = idRigacPrv;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public int getIdRigacPrv() {
    return iIdRigacPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param idRigavPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRigavPrv(int idRigavPrv) {
    this.iIdRigavPrv = idRigavPrv;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public int getIdRigavPrv() {
    return iIdRigavPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param idSubRigavPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdSubRigavPrv(int idSubRigavPrv) {
    this.iIdSubRigavPrv = idSubRigavPrv;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public int getIdSubRigavPrv() {
    return iIdSubRigavPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param idSubRigacPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdSubRigacPrv(int idSubRigacPrv) {
    this.iIdSubRigacPrv = idSubRigacPrv;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public int getIdSubRigacPrv() {
    return iIdSubRigacPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param splRiga
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSplRiga(char splRiga) {
    this.iSplRiga = splRiga;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public char getSplRiga() {
    return iSplRiga;
  }

  /**
   * Valorizza l'attributo.
   * @param tipoRigav
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoRigav(char tipoRigav) {
    this.iTipoRigav = tipoRigav;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public char getTipoRigav() {
    return iTipoRigav;
  }

  /**
   * Valorizza l'attributo.
   * @param sequenzaRiga
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSequenzaRiga(int sequenzaRiga) {
    this.iSequenzaRiga = sequenzaRiga;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public int getSequenzaRiga() {
    return iSequenzaRiga;
  }

  /**
   * Valorizza l'attributo.
   * @param rArticolo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRArticolo(String rArticolo) {
    this.iRArticolo = rArticolo;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRArticolo() {
    return iRArticolo;
  }

  public void setDescArticolo(String descArticolo) {
    this.iDescArticolo = descArticolo;
    setDirty();
  }

  public String getDescArticolo() {
    return iDescArticolo;
  }

  /**
   * Valorizza l'attributo.
   * @param rVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRVersione(Integer rVersione) {
    this.iRVersione = rVersione;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public Integer getRVersione() {
    return iRVersione;
  }

  /**
   * Valorizza l'attributo.
   * @param rConfigurazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRConfigurazione(Integer rConfigurazione) {
    this.iRConfigurazione = rConfigurazione;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public Integer getRConfigurazione() {
    return iRConfigurazione;
  }

  public void setDescConfigurazione(String descConfigurazione) {
    this.iDescConfigurazione = descConfigurazione;
    setDirty();
  }

  public String getDescConfigurazione() {
    return iDescConfigurazione;
  }

  /**
   * Valorizza l'attributo.
   * @param tipoRisorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoRisorsa(char tipoRisorsa) {
    this.iTipoRisorsa = tipoRisorsa;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public char getTipoRisorsa() {
    return iTipoRisorsa;
  }

  /**
   * Valorizza l'attributo.
   * @param livelloRisorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setLivelloRisorsa(char livelloRisorsa) {
    this.iLivelloRisorsa = livelloRisorsa;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public char getLivelloRisorsa() {
    return iLivelloRisorsa;
  }

  /**
   * Valorizza l'attributo.
   * @param idRisorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRisorsa(String idRisorsa) {
    this.iIdRisorsa = idRisorsa;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getIdRisorsa() {
    return iIdRisorsa;
  }

  public void setDescRisorsa(String descRisorsa) {
    this.iDescRisorsa = descRisorsa;
    setDirty();
  }

  public String getDescRisorsa() {
    return iDescRisorsa;
  }

  /**
   * Valorizza l'attributo.
   * @param descrizione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDescrizione(String descrizione) {
    this.iDescrizione = descrizione;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getDescrizione() {
    return iDescrizione;
  }

  /**
   * Valorizza l'attributo.
   * @param descrRidotta
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDescrRidotta(String descrRidotta) {
    this.iDescrRidotta = descrRidotta;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getDescrRidotta() {
    return iDescrRidotta;
  }

  /**
   * Valorizza l'attributo.
   * @param rSchemaCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRSchemaCosto(String rSchemaCosto) {
    this.iRSchemaCosto = rSchemaCosto;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRSchemaCosto() {
    return iRSchemaCosto;
  }

  public void setDescSchemaCosto(String descSchemaCosto) {
    this.iDescSchemaCosto = descSchemaCosto;
    setDirty();
  }

  public String getDescSchemaCosto() {
    return iDescSchemaCosto;
  }

  /**
   * Valorizza l'attributo.
   * @param rComponCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRComponCosto(String rComponCosto) {
    this.iRComponCosto = rComponCosto;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRComponCosto() {
    return iRComponCosto;
  }

  public void setDescComponCosto(String descComponCosto) {
    this.iDescComponCosto = descComponCosto;
    setDirty();
  }

  public String getDescComponCosto() {
    return iDescComponCosto;
  }

  /**
   * Valorizza l'attributo.
   * @param rUmPrmMag
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRUmPrmMag(String rUmPrmMag) {
    this.iRUmPrmMag = rUmPrmMag;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRUmPrmMag() {
    return iRUmPrmMag;
  }

  /**
   * Valorizza l'attributo.
   * @param rUmSecMag
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRUmSecMag(String rUmSecMag) {
    this.iRUmSecMag = rUmSecMag;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRUmSecMag() {
    return iRUmSecMag;
  }

  /**
   * Valorizza l'attributo.
   * @param rUmVen
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRUmVen(String rUmVen) {
    this.iRUmVen = rUmVen;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRUmVen() {
    return iRUmVen;
  }

  /**
   * Valorizza l'attributo.
   * @param qtaPrvUmVen
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setQtaPrvUmVen(BigDecimal qtaPrvUmVen) {
    this.iQtaPrvUmVen = qtaPrvUmVen;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQtaPrvUmVen() {
    return iQtaPrvUmVen;
  }

  /**
   * Valorizza l'attributo.
   * @param qtaPrvUmPrm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setQtaPrvUmPrm(BigDecimal qtaPrvUmPrm) {
    this.iQtaPrvUmPrm = qtaPrvUmPrm;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQtaPrvUmPrm() {
    return iQtaPrvUmPrm;
  }

  /**
   * Valorizza l'attributo.
   * @param qtaPrvUmSec
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setQtaPrvUmSec(BigDecimal qtaPrvUmSec) {
    this.iQtaPrvUmSec = qtaPrvUmSec;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQtaPrvUmSec() {
    return iQtaPrvUmSec;
  }

  /**
   * Valorizza l'attributo.
   * @param dataConsegRcs
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDataConsegRcs(java.sql.Date dataConsegRcs) {
    this.iDataConsegRcs = dataConsegRcs;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataConsegRcs() {
    return iDataConsegRcs;
  }

  /**
   * Valorizza l'attributo.
   * @param dataConsegPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDataConsegPrv(java.sql.Date dataConsegPrv) {
    this.iDataConsegPrv = dataConsegPrv;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataConsegPrv() {
    return iDataConsegPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param settConsegRcs
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSettConsegRcs(String settConsegRcs) {
    this.iSettConsegRcs = settConsegRcs;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getSettConsegRcs() {
    return iSettConsegRcs;
  }

  /**
   * Valorizza l'attributo.
   * @param settConsegPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSettConsegPrv(String settConsegPrv) {
    this.iSettConsegPrv = settConsegPrv;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getSettConsegPrv() {
    return iSettConsegPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param blcQtaCmp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setBlcQtaCmp(boolean blcQtaCmp) {
    this.iBlcQtaCmp = blcQtaCmp;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public boolean getBlcQtaCmp() {
    return iBlcQtaCmp;
  }

  /**
   * Valorizza l'attributo.
   * @param coeffImp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCoeffImp(BigDecimal coeffImp) {
    this.iCoeffImp = coeffImp;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getCoeffImp() {
    return iCoeffImp;
  }

  /**
   * Valorizza l'attributo.
   * @param prezzo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setPrezzo(BigDecimal prezzo) {
    this.iPrezzo = prezzo;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getPrezzo() {
    return iPrezzo;
  }

  /**
   * Valorizza l'attributo.
   * @param prezzoExtra
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setPrezzoExtra(BigDecimal prezzoExtra) {
    this.iPrezzoExtra = prezzoExtra;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getPrezzoExtra() {
    return iPrezzoExtra;
  }

  /**
   * Valorizza l'attributo.
   * @param noFattura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNoFattura(boolean noFattura) {
    this.iNoFattura = noFattura;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public boolean getNoFattura() {
    return iNoFattura;
  }

  /**
   * Valorizza l'attributo.
   * @param provenienzaPrz
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setProvenienzaPrz(char provenienzaPrz) {
    this.iProvenienzaPrz = provenienzaPrz;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public char getProvenienzaPrz() {
    return iProvenienzaPrz;
  }

  /**
   * Valorizza l'attributo.
   * @param tpPrezzo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setTpPrezzo(char tpPrezzo) {
    this.iTpPrezzo = tpPrezzo;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public char getTpPrezzo() {
    return iTpPrezzo;
  }

  /**
   * Valorizza l'attributo.
   * @param costoRifer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCostoRifer(BigDecimal costoRifer) {
    this.iCostoRifer = costoRifer;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getCostoRifer() {
    return iCostoRifer;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrTotale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrTotale(BigDecimal vlrTotale) {
    this.iVlrTotale = vlrTotale;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getVlrTotale() {
    return iVlrTotale;
  }

  /**
   * Valorizza l'attributo.
   * @param cosTotale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosTotale(BigDecimal cosTotale) {
    this.iCosTotale = cosTotale;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getCosTotale() {
    return iCosTotale;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcTotale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcTotale(BigDecimal mdcTotale) {
    this.iMdcTotale = mdcTotale;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getMdcTotale() {
    return iMdcTotale;
  }

  /**
   * Valorizza l'attributo.
   * @param rGesCommenti
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRGesCommenti(Integer rGesCommenti) {
    this.iRGesCommenti = rGesCommenti;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public Integer getRGesCommenti() {
    return iRGesCommenti;
  }

  /**
   * Valorizza l'attributo.
   * @param rDocumentoMm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRDocumentoMm(String rDocumentoMm) {
    this.iRDocumentoMm = rDocumentoMm;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRDocumentoMm() {
    return iRDocumentoMm;
  }

  /**
   * Valorizza l'attributo.
   * @param nota
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNota(String nota) {
    this.iNota = nota;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getNota() {
    return iNota;
  }

  /**
   * Valorizza l'attributo.
   * @param escRigOffc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setEscRigOffc(boolean escRigOffc) {
    this.iEscRigOffc = escRigOffc;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public boolean getEscRigOffc() {
    return iEscRigOffc;
  }

  /**
   * Valorizza l'attributo.
   * @param rAnnoOff
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRAnnoOff(String rAnnoOff) {
    this.iRAnnoOff = rAnnoOff;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRAnnoOff() {
    return iRAnnoOff;
  }

  /**
   * Valorizza l'attributo.
   * @param rNumOff
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRNumOff(String rNumOff) {
    this.iRNumOff = rNumOff;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRNumOff() {
    return iRNumOff;
  }

  /**
   * Valorizza l'attributo.
   * @param rRigaOff
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRRigaOff(int rRigaOff) {
    this.iRRigaOff = rRigaOff;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public int getRRigaOff() {
    return iRRigaOff;
  }

  /**
   * Valorizza l'attributo.
   * @param rDetRigOff
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRDetRigOff(int rDetRigOff) {
    this.iRDetRigOff = rDetRigOff;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public int getRDetRigOff() {
    return iRDetRigOff;
  }

  /**
   * Valorizza l'attributo.
   * @param rUtenteCrz
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRUtenteCrz(String rUtenteCrz) {
    this.iRUtenteCrz = rUtenteCrz;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRUtenteCrz() {
    return iRUtenteCrz;
  }

  /**
   * Valorizza l'attributo.
   * @param rUtenteAgg
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRUtenteAgg(String rUtenteAgg) {
    this.iRUtenteAgg = rUtenteAgg;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRUtenteAgg() {
    return iRUtenteAgg;
  }

  /**
   * Valorizza l'attributo.
   * @param timestamp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setTimestamp(java.sql.Timestamp timestamp) {
    this.iTimestamp = timestamp;

  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Timestamp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Timestamp getTimestamp() {
    return iTimestamp;
  }

  /**
   * Valorizza l'attributo.
   * @param timestampAgg
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setTimestampAgg(java.sql.Timestamp timestampAgg) {
    this.iTimestampAgg = timestampAgg;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Timestamp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Timestamp getTimestampAgg() {
    return iTimestampAgg;
  }

  /**
   * Valorizza l'attributo.
   * @param stato
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setStato(char stato) {
    this.iStato = stato;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public char getStato() {
    return iStato;
  }

  public void setRptPrevComRig(RptPreventivoCommessaRiga rptPrevComRig) {
    this.iRptPrevComRig.setObject(rptPrevComRig);
    setDirty();
    setOnDB(false);
  }

  public RptPreventivoCommessaRiga getRptPrevComRig() {
    return (RptPreventivoCommessaRiga) iRptPrevComRig.getObject();
  }

  public void setRptPrevComRigKey(String key) {
    iRptPrevComRig.setKey(key);
    setDirty();
    setOnDB(false);
  }

  public String getRptPrevComRigKey() {
    return iRptPrevComRig.getKey();
  }

  public void setBatchJobId(Integer batchJobId) {
    String key = iRptPrevComRig.getKey();
    iRptPrevComRig.setKey(KeyHelper.replaceTokenObjectKey(key, 1, batchJobId));
    setDirty();
    setOnDB(false);
  }

  public Integer getBatchJobId() {
    String key = iRptPrevComRig.getKey();
    String objBatchJobId = KeyHelper.getTokenObjectKey(key, 1);
    return KeyHelper.stringToIntegerObj(objBatchJobId);

  }

  public void setReportNr(Integer reportNr) {
    String key = iRptPrevComRig.getKey();
    iRptPrevComRig.setKey(KeyHelper.replaceTokenObjectKey(key, 2, reportNr));
    setDirty();
    setOnDB(false);
  }

  public Integer getReportNr() {
    String key = iRptPrevComRig.getKey();
    String objReportNr = KeyHelper.getTokenObjectKey(key, 2);
    return KeyHelper.stringToIntegerObj(objReportNr);

  }

  public void setRigaJobId(Integer rigaJobId) {
    String key = iRptPrevComRig.getKey();
    //Integer rigaJobIdTmp = new Integer(rigaJobId);
    iRptPrevComRig.setKey(KeyHelper.replaceTokenObjectKey(key, 3, rigaJobId));
    setDirty();
    setOnDB(false);
  }

  public Integer getRigaJobId() {
    String key = iRptPrevComRig.getKey();
    String objRigaJobId = KeyHelper.getTokenObjectKey(key, 3);
    return KeyHelper.stringToIntegerObj(objRigaJobId);
  }

  public void setDetRigaJob(Integer detRigaJob) {
    String key = iRptPrevComRig.getKey();
    iRptPrevComRig.setKey(KeyHelper.replaceTokenObjectKey(key, 4, detRigaJob));
    setDirty();
    setOnDB(false);
  }

  public Integer getDetRigaJob() {
    String key = iRptPrevComRig.getKey();
    String objDetRigaJob = KeyHelper.getTokenObjectKey(key, 4);
    return KeyHelper.stringToIntegerObj(objDetRigaJob);
  }

  /**
   * setEqual
   * @param obj
   * @throws CopyException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    RptPreventivoCommessaVocePO rptPreventivoCommessaVocePO = (RptPreventivoCommessaVocePO)obj;
    if (rptPreventivoCommessaVocePO.iDataConsegRcs != null)
        iDataConsegRcs = (java.sql.Date)rptPreventivoCommessaVocePO.iDataConsegRcs.clone();
    if (rptPreventivoCommessaVocePO.iDataConsegPrv != null)
        iDataConsegPrv = (java.sql.Date)rptPreventivoCommessaVocePO.iDataConsegPrv.clone();
    if (rptPreventivoCommessaVocePO.iTimestamp != null)
        iTimestamp = (java.sql.Timestamp)rptPreventivoCommessaVocePO.iTimestamp.clone();
    if (rptPreventivoCommessaVocePO.iTimestampAgg != null)
        iTimestampAgg = (java.sql.Timestamp)rptPreventivoCommessaVocePO.iTimestampAgg.clone();
  }

  /**
   * checkAll
   * @param components
   * @return Vector
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public Vector checkAll(BaseComponentsCollection components) {
    Vector errors = new Vector();
    components.runAllChecks(errors);
    return errors;
  }

  /**
   *  setKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setKey(String key) {
    String[] keys = KeyHelper.unpackObjectKey(key);
    setBatchJobId(KeyHelper.stringToIntegerObj(keys[0]));
    setReportNr(KeyHelper.stringToIntegerObj(keys[1]));
    setRigaJobId(KeyHelper.stringToIntegerObj(keys[2]));
    setDetRigaJob(KeyHelper.stringToIntegerObj(keys[3]));
    setDetRigaJob2(KeyHelper.stringToIntegerObj(keys[4]));
  }

  /**
   *  getKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getKey() {
    Integer batchJobId = getBatchJobId();
    Integer reportNr = getReportNr();
    Integer rigaJobId = getRigaJobId();
    Integer detRigaJob = getDetRigaJob();
    Integer detRigaJob2 = getDetRigaJob2();
    Object[] keyParts = {batchJobId, reportNr, rigaJobId, detRigaJob, detRigaJob2};
    return KeyHelper.buildObjectKey(keyParts);
  }

  /**
   * isDeletable
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public boolean isDeletable() {
    return checkDelete() == null;
  }

  /**
   * toString
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String toString() {
    return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
  }

  /**
   *  getTableManager
   * @return TableManager
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  protected TableManager getTableManager() throws SQLException {
    return RptPreventivoCommessaVoceTM.getInstance();
  }
  public BigDecimal getPercentuale()
  {
    return iPercentuale;
  }

  public void setPercentuale(BigDecimal perc)
  {
    this.iPercentuale = perc;
  }

  public String getLivello()
  {
    return iLivello;
  }

  public void setLivello(String perc)
  {
    this.iLivello = perc;
  }

  public String getCommenti()
  {
    return iCommenti;
  }

  public void setCommenti(String commenti)
  {
    this.iCommenti = commenti;
  }
  
  //33048 inizio  
  public String getCommentiPiede()
  {
    return iCommentiPiede;
  }

  public void setCommentiPiede(String commentiPiede)
  {
    this.iCommentiPiede = commentiPiede;
  }
  //33048 fine
  
  public void setMdcTotaleAz(BigDecimal bd)
    {
      iMdcTotaleAz = bd;
    }

    public BigDecimal getMdcTotaleAz()
    {
      return iMdcTotaleAz;
    }

    public void setCosTotaleAz(BigDecimal bd)
    {
      iCosTotaleAz = bd;
    }

    public BigDecimal getCosTotaleAz()
    {
      return iCosTotaleAz;
    }

    public void setVlrTotaleAz(BigDecimal bd)
    {
      iVlrTotaleAz = bd;
    }

    public BigDecimal getVlrTotaleAz()
    {
      return iVlrTotaleAz;
    }

    public void setCostoRiferAz(BigDecimal bd)
    {
      iCostoRiferAz = bd;
    }

    public BigDecimal getCostoRiferAz()
    {
      return iCostoRiferAz;
    }

    public void setPrezzoAz(BigDecimal bd)
    {
      iPrezzoAz = bd;
    }

    public BigDecimal getPrezzoAz()
    {
      return iPrezzoAz;
    }

}


/*
 * @(#)RptPreventivoCommessaRigaPO.java
 */

/**
 * RptPrevComRig
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 16:53:50
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

public abstract class RptPreventivoCommessaRigaPO
  extends PersistentObject
  implements BusinessObject, Authorizable, Deletable, ConflictableWithKey
{

  /**
   *  instance
   */
  private static RptPreventivoCommessaRiga cInstance;

  /**
   * Attributo iDetRigaJob
   */
  protected Integer iDetRigaJob;

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
   * Attributo iRRigacPrv
   */
  protected int iRigacPrv;

  /**
   * Attributo iSequenzaRiga
   */
  protected int iSequenzaRiga;

  /**
   * Attributo iSplRiga
   */
  protected char iSplRiga = RptPreventivoCommessaRiga.TIPO_RIGA_COMMESSA;

  /**
   * Attributo iRCommessa
   */
  protected String iRCommessa;

  protected String iDescCommessa;

  /**
   * Attributo iRCommessaApp
   */
  protected String iRCommessaApp;

  protected String iDescCommessaApp;

  /**
   * Attributo iRCommessaPrm
   */
  protected String iRCommessaPrm;

  protected String iDescCommessaPrm;

  /**
   * Attributo iDescrizione
   */
  protected String iDescrizione;

  /**
   * Attributo iDescrRidotta
   */
  protected String iDescrRidotta;

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
   * Attributo iRUmPrmMag
   */
  protected String iRUmPrmMag;

  /**
   * Attributo iQtaUmPrm
   */
  protected BigDecimal iQtaUmPrm;

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
   * Attributo iVlrLivello
   */
  protected BigDecimal iVlrLivello;

  /**
   * Attributo iCosLivello
   */
  protected BigDecimal iCosLivello;

  /**
   * Attributo iMdcLivello
   */
  protected BigDecimal iMdcLivello;

  /**
   * Attributo iVlrLivelloInf
   */
  protected BigDecimal iVlrLivelloInf;

  /**
   * Attributo iCosLivelloInf
   */
  protected BigDecimal iCosLivelloInf;

  /**
   * Attributo iMdcLivelloInf
   */
  protected BigDecimal iMdcLivelloInf;

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
  public static BigDecimal ZERO = new BigDecimal("0");

  //
  protected BigDecimal iVlrLivelloAz = ZERO;
  protected BigDecimal iCosLivelloAz = ZERO;
  protected BigDecimal iMdcLivelloAz = ZERO;
  protected BigDecimal iVlrLivelloInfAz = ZERO;
  protected BigDecimal iCosLivelloInfAz = ZERO;
  protected BigDecimal iMdcLivelloInfAz = ZERO;
  protected BigDecimal iVlrTotaleAz = ZERO;
  protected BigDecimal iCosTotaleAz = ZERO;
  protected BigDecimal iMdcTotaleAz = ZERO;

  /**
   * Attributo iStpAllTes
   */
  protected boolean iStpAllTes = false;

  /**
   * Attributo iGenRigOffc
   */
  protected char iGenRigOffc = RptPreventivoCommessaRiga.RIGA_COMMESSA;

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

  protected BigDecimal iPercentuale = new BigDecimal(0);
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
  protected char iStato = RptPreventivoCommessaRiga.STATO_VALIDO;

  /**
   * Attributo iRptPrevComTes
   */
  protected Proxy iRptPrevComTes = new Proxy(it.thera.thip.base.commessa.RptPreventivoCommessaTestata.class);

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
  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    if (cInstance == null)
      cInstance = (RptPreventivoCommessaRiga)Factory.createObject(RptPreventivoCommessaRiga.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  /**
   *  elementWithKey
   * @param key
   * @param lockType
   * @return RptPreventivoCommessaRiga
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static RptPreventivoCommessaRiga elementWithKey(String key, int lockType) throws SQLException
  {
    return (RptPreventivoCommessaRiga)PersistentObject.elementWithKey(RptPreventivoCommessaRiga.class, key, lockType);
  }

  /**
   * Valorizza l'attributo.
   * @param detRigaJob
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDetRigaJob(Integer detRigaJob)
  {
    this.iDetRigaJob = detRigaJob;
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
  public Integer getDetRigaJob()
  {
    return iDetRigaJob;
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
  public void setIdAzienda(String idAzienda)
  {
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
  public String getIdAzienda()
  {
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
  public void setIdAnnoPrevc(String idAnnoPrevc)
  {
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
  public String getIdAnnoPrevc()
  {
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
  public void setIdNumeroPrevc(String idNumeroPrevc)
  {
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
  public String getIdNumeroPrevc()
  {
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
  public void setIdRigacPrv(int idRigacPrv)
  {
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
  public int getIdRigacPrv()
  {
    return iIdRigacPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param rRigacPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRigacPrv(int rRigacPrv)
  {
    this.iRigacPrv = rRigacPrv;
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
  public int getRigacPrv()
  {
    return iRigacPrv;
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
  public void setSequenzaRiga(int sequenzaRiga)
  {
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
  public int getSequenzaRiga()
  {
    return iSequenzaRiga;
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
  public void setSplRiga(char splRiga)
  {
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
  public char getSplRiga()
  {
    return iSplRiga;
  }

  /**
   * Valorizza l'attributo.
   * @param rCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRCommessa(String rCommessa)
  {
    this.iRCommessa = rCommessa;
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
  public String getRCommessa()
  {
    return iRCommessa;
  }

  public void setDescCommessa(String descCommessa)
  {
    iDescCommessa = descCommessa;
    setDirty();
  }

  public String getDescCommessa()
  {
    return iDescCommessa;
  }

  /**
   * Valorizza l'attributo.
   * @param rCommessaApp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRCommessaApp(String rCommessaApp)
  {
    this.iRCommessaApp = rCommessaApp;
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
  public String getRCommessaApp()
  {
    return iRCommessaApp;
  }

  public void setDescCommessaApp(String descCommessaApp)
  {
    iDescCommessaApp = descCommessaApp;
    setDirty();
  }

  public String getDescCommessaApp()
  {
    return iDescCommessaApp;
  }

  /**
   * Valorizza l'attributo.
   * @param rCommessaPrm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRCommessaPrm(String rCommessaPrm)
  {
    this.iRCommessaPrm = rCommessaPrm;
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
  public String getRCommessaPrm()
  {
    return iRCommessaPrm;
  }

  public void setDescCommessaPrm(String descCommessaPrm)
  {
    iDescCommessaPrm = descCommessaPrm;
    setDirty();
  }

  public String getDescCommessaPrm()
  {
    return iDescCommessaPrm;
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
  public void setDescrizione(String descrizione)
  {
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
  public String getDescrizione()
  {
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
  public void setDescrRidotta(String descrRidotta)
  {
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
  public String getDescrRidotta()
  {
    return iDescrRidotta;
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
  public void setRArticolo(String rArticolo)
  {
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
  public String getRArticolo()
  {
    return iRArticolo;
  }

  public void setDescArticolo(String descArticolo)
  {
    this.iDescArticolo = descArticolo;
    setDirty();
  }

  public String getDescArticolo()
  {
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
  public void setRVersione(Integer rVersione)
  {
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
  public Integer getRVersione()
  {
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
  public void setRConfigurazione(Integer rConfigurazione)
  {
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
  public Integer getRConfigurazione()
  {
    return iRConfigurazione;
  }

  public void setDescConfigurazione(String descConfigurazione)
  {
    this.iDescConfigurazione = descConfigurazione;
    setDirty();
  }

  public String getDescConfigurazione()
  {
    return iDescConfigurazione;
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
  public void setRUmPrmMag(String rUmPrmMag)
  {
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
  public String getRUmPrmMag()
  {
    return iRUmPrmMag;
  }

  /**
   * Valorizza l'attributo.
   * @param qtaUmPrm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setQtaUmPrm(BigDecimal qtaUmPrm)
  {
    this.iQtaUmPrm = qtaUmPrm;
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
  public BigDecimal getQtaUmPrm()
  {
    return iQtaUmPrm;
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
  public void setDataConsegRcs(java.sql.Date dataConsegRcs)
  {
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
  public java.sql.Date getDataConsegRcs()
  {
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
  public void setDataConsegPrv(java.sql.Date dataConsegPrv)
  {
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
  public java.sql.Date getDataConsegPrv()
  {
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
  public void setSettConsegRcs(String settConsegRcs)
  {
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
  public String getSettConsegRcs()
  {
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
  public void setSettConsegPrv(String settConsegPrv)
  {
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
  public String getSettConsegPrv()
  {
    return iSettConsegPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrLivello
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrLivello(BigDecimal vlrLivello)
  {
    this.iVlrLivello = vlrLivello;
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
  public BigDecimal getVlrLivello()
  {
    return iVlrLivello;
  }

  /**
   * Valorizza l'attributo.
   * @param cosLivello
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosLivello(BigDecimal cosLivello)
  {
    this.iCosLivello = cosLivello;
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
  public BigDecimal getCosLivello()
  {
    return iCosLivello;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcLivello
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcLivello(BigDecimal mdcLivello)
  {
    this.iMdcLivello = mdcLivello;
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
  public BigDecimal getMdcLivello()
  {
    return iMdcLivello;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrLivelloInf
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrLivelloInf(BigDecimal vlrLivelloInf)
  {
    this.iVlrLivelloInf = vlrLivelloInf;
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
  public BigDecimal getVlrLivelloInf()
  {
    return iVlrLivelloInf;
  }

  /**
   * Valorizza l'attributo.
   * @param cosLivelloInf
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosLivelloInf(BigDecimal cosLivelloInf)
  {
    this.iCosLivelloInf = cosLivelloInf;
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
  public BigDecimal getCosLivelloInf()
  {
    return iCosLivelloInf;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcLivelloInf
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcLivelloInf(BigDecimal mdcLivelloInf)
  {
    this.iMdcLivelloInf = mdcLivelloInf;
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
  public BigDecimal getMdcLivelloInf()
  {
    return iMdcLivelloInf;
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
  public void setVlrTotale(BigDecimal vlrTotale)
  {
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
  public BigDecimal getVlrTotale()
  {
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
  public void setCosTotale(BigDecimal cosTotale)
  {
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
  public BigDecimal getCosTotale()
  {
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
  public void setMdcTotale(BigDecimal mdcTotale)
  {
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
  public BigDecimal getMdcTotale()
  {
    return iMdcTotale;
  }

  /**
   * Valorizza l'attributo.
   * @param stpAllTes
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setStpAllTes(boolean stpAllTes)
  {
    this.iStpAllTes = stpAllTes;
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
  public boolean getStpAllTes()
  {
    return iStpAllTes;
  }

  /**
   * Valorizza l'attributo.
   * @param genRigOffc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setGenRigOffc(char genRigOffc)
  {
    this.iGenRigOffc = genRigOffc;
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
  public char getGenRigOffc()
  {
    return iGenRigOffc;
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
  public void setRAnnoOff(String rAnnoOff)
  {
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
  public String getRAnnoOff()
  {
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
  public void setRNumOff(String rNumOff)
  {
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
  public String getRNumOff()
  {
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
  public void setRRigaOff(int rRigaOff)
  {
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
  public int getRRigaOff()
  {
    return iRRigaOff;
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
  public void setRGesCommenti(Integer rGesCommenti)
  {
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
  public Integer getRGesCommenti()
  {
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
  public void setRDocumentoMm(String rDocumentoMm)
  {
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
  public String getRDocumentoMm()
  {
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
  public void setNota(String nota)
  {
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
  public String getNota()
  {
    return iNota;
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
  public void setRUtenteCrz(String rUtenteCrz)
  {
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
  public String getRUtenteCrz()
  {
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
  public void setRUtenteAgg(String rUtenteAgg)
  {
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
  public String getRUtenteAgg()
  {
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
  public void setTimestamp(java.sql.Timestamp timestamp)
  {
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
  public java.sql.Timestamp getTimestamp()
  {
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
  public void setTimestampAgg(java.sql.Timestamp timestampAgg)
  {
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
  public java.sql.Timestamp getTimestampAgg()
  {
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
  public void setStato(char stato)
  {
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
  public char getStato()
  {
    return iStato;
  }

  /**
   * Valorizza l'attributo.
   * @param rptPrevComTes
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRptPrevComTes(RptPreventivoCommessaTestata rptPrevComTes)
  {
    this.iRptPrevComTes.setObject(rptPrevComTes);
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo.
   * @return RptPreventivoCommessaTestata
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public RptPreventivoCommessaTestata getRptPrevComTes()
  {
    return (RptPreventivoCommessaTestata)iRptPrevComTes.getObject();
  }

  /**
   * setRptPrevComTesKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRptPrevComTesKey(String key)
  {
    iRptPrevComTes.setKey(key);
    setDirty();
    setOnDB(false);
  }

  /**
   * getRptPrevComTesKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getRptPrevComTesKey()
  {
    return iRptPrevComTes.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param batchJobId
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setBatchJobId(Integer batchJobId)
  {
    String key = iRptPrevComTes.getKey();
    iRptPrevComTes.setKey(KeyHelper.replaceTokenObjectKey(key, 1, batchJobId));
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
  public Integer getBatchJobId()
  {
    String key = iRptPrevComTes.getKey();
    String objBatchJobId = KeyHelper.getTokenObjectKey(key, 1);
    return KeyHelper.stringToIntegerObj(objBatchJobId);

  }

  /**
   * Valorizza l'attributo.
   * @param reportNr
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setReportNr(Integer reportNr)
  {
    String key = iRptPrevComTes.getKey();
    iRptPrevComTes.setKey(KeyHelper.replaceTokenObjectKey(key, 2, reportNr));
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
  public Integer getReportNr()
  {
    String key = iRptPrevComTes.getKey();
    String objReportNr = KeyHelper.getTokenObjectKey(key, 2);
    return KeyHelper.stringToIntegerObj(objReportNr);

  }

  /**
   * Valorizza l'attributo.
   * @param rigaJobId
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRigaJobId(Integer rigaJobId)
  {
    String key = iRptPrevComTes.getKey();
    //  Integer rigaJobIdTmp = new Integer(rigaJobId);
    iRptPrevComTes.setKey(KeyHelper.replaceTokenObjectKey(key, 3, rigaJobId));
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
  public Integer getRigaJobId()
  {
    String key = iRptPrevComTes.getKey();
    String objRigaJobId = KeyHelper.getTokenObjectKey(key, 3);
    return KeyHelper.stringToIntegerObj(objRigaJobId);
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
  public void setEqual(Copyable obj) throws CopyException
  {
    super.setEqual(obj);
    RptPreventivoCommessaRigaPO rptPreventivoCommessaRigaPO = (RptPreventivoCommessaRigaPO)obj;
    if (rptPreventivoCommessaRigaPO.iDataConsegRcs != null)
      iDataConsegRcs = (java.sql.Date)rptPreventivoCommessaRigaPO.iDataConsegRcs.clone();
    if (rptPreventivoCommessaRigaPO.iDataConsegPrv != null)
      iDataConsegPrv = (java.sql.Date)rptPreventivoCommessaRigaPO.iDataConsegPrv.clone();
    if (rptPreventivoCommessaRigaPO.iTimestamp != null)
      iTimestamp = (java.sql.Timestamp)rptPreventivoCommessaRigaPO.iTimestamp.clone();
    if (rptPreventivoCommessaRigaPO.iTimestampAgg != null)
      iTimestampAgg = (java.sql.Timestamp)rptPreventivoCommessaRigaPO.iTimestampAgg.clone();
    iRptPrevComTes.setEqual(rptPreventivoCommessaRigaPO.iRptPrevComTes);
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
  public Vector checkAll(BaseComponentsCollection components)
  {
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
  public void setKey(String key)
  {
    String[] keys = KeyHelper.unpackObjectKey(key);
    setBatchJobId(KeyHelper.stringToIntegerObj(keys[0]));
    setReportNr(KeyHelper.stringToIntegerObj(keys[1]));
    setRigaJobId(KeyHelper.stringToIntegerObj(keys[2]));
    setDetRigaJob(KeyHelper.stringToIntegerObj(keys[3]));
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
  public String getKey()
  {
    Integer batchJobId = getBatchJobId();
    Integer reportNr = getReportNr();
    Integer rigaJobId = getRigaJobId();
    Integer detRigaJob = getDetRigaJob();
    Object[] keyParts =
      {
      batchJobId, reportNr, rigaJobId, detRigaJob};
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
  public boolean isDeletable()
  {
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
  public String toString()
  {
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
  protected TableManager getTableManager() throws SQLException
  {
    return RptPreventivoCommessaRigaTM.getInstance();
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

  public BigDecimal getVlrLivelloAz()
  {
    return iVlrLivelloAz;
  }

  public void setVlrLivelloAz(BigDecimal bd)
  {
    this.iVlrLivelloAz = bd;
  }

  public BigDecimal getCosLivelloAz()
  {
    return iCosLivelloAz;
  }

  public void setCosLivelloAz(BigDecimal bd)
  {
    this.iCosLivelloAz = bd;
  }

  public BigDecimal getMdcLivelloAz()
  {
    return iMdcLivelloAz;
  }

  public void setMdcLivelloAz(BigDecimal bd)
  {
    this.iMdcLivelloAz = bd;
  }

  public BigDecimal getVlrLivelloInfAz()
  {
    return iVlrLivelloInfAz;
  }

  public void setVlrLivelloInfAz(BigDecimal bd)
  {
    this.iVlrLivelloInfAz = bd;
  }

  public BigDecimal getCosLivelloInfAz()
  {
    return iCosLivelloInfAz;
  }

  public void setCosLivelloInfAz(BigDecimal bd)
  {
    this.iCosLivelloInfAz = bd;
  }

  public BigDecimal getMdcLivelloInfAz()
  {
    return iMdcLivelloInfAz;
  }

  public void setMdcLivelloInfAz(BigDecimal bd)
  {
    this.iMdcLivelloInfAz = bd;
  }

  public BigDecimal getVlrTotaleAz()
  {
    return iVlrTotaleAz;
  }

  public void setVlrTotaleAz(BigDecimal bd)
  {
    this.iVlrTotaleAz = bd;
  }

  public BigDecimal getCosTotaleAz()
  {
    return iCosTotaleAz;
  }

  public void setCosTotaleAz(BigDecimal bd)
  {
    this.iCosTotaleAz = bd;
  }

  public BigDecimal getMdcTotaleAz()
  {
    return iMdcTotaleAz;
  }

  public void setMdcTotaleAz(BigDecimal bd)
  {
    this.iMdcTotaleAz = bd;
  }

}

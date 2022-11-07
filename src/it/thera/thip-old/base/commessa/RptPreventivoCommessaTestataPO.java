/*
 * @(#)RptPrevComTesPO.java
 */

/**
 * RptPrevComTes
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 16:39:23
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 16/12/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 15848     05/03/2012    FM       Correzione
 * 19818     28/05/2014    AA       Aggiunto attributi iIdLingua & iDesLingua
 * 33048     08/03/2021    RA       Aggiunto attributo Commenti e CommentiPiede
 */
package it.thera.thip.base.commessa;

import com.thera.thermfw.persist.*;
import java.sql.*;
import java.util.*;
import com.thera.thermfw.batch.AvailableReport;
import java.math.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.security.*;

public abstract class RptPreventivoCommessaTestataPO
  extends PersistentObject
  implements BusinessObject, Authorizable, Deletable, ConflictableWithKey
{


  /**
   *  instance
   */
  private static RptPreventivoCommessaTestata cInstance;

  //
  public static BigDecimal ZERO = new BigDecimal("0");

  /**
   * Attributo iReportNumber
   */
  protected Integer iReportNumber = new Integer(1);

  /**
   * Attributo iRigaJobId
   */
  protected int iRigaJobId;

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

  protected String iWfDescription ;

  /**
   * Attributo iDataPrevc
   */
  protected java.sql.Date iDataPrevc;

  /**
   * Attributo iNumeroPrevcFmt
   */
  protected String iNumeroPrevcFmt;

  /**
   * Attributo iRStabilimento
   */
  protected String iIdStabilimento;

  protected String iDescStabilimento;

  /**
   * Attributo iRMagazzino
   */
  protected String iIdMagazzino;

  protected String iDescMagazzino;

  protected String iTipoIntestatario;

  /**
   * Attributo iRCauPrevc
   */
  protected String iIdCauPrevc;

  protected String iDescCauPrevc;

  /**
   * Attributo iRCommessa
   */
  protected String iIdCommessa;

  protected String iDescCommessa;

  /**
   * Attributo iRCommessaCa
   */
  protected String iIdCommessaCa;

  protected String iDescCommessaCa;

  /**
   * Attributo iTpIntestPrevc
   */
  protected char iTpIntestPrevc = RptPreventivoCommessaTestata.TP_INTES_CLIENTE;

  /**
   * Attributo iRCliente
   */
  protected String iIdCliente;

  /**
   * Attributo iRDivisione
   */
  protected String iIdDivisione;

  /**
   * Attributo iRAnagrafico
   */
  protected Integer iIdAnagrafico;

  /**
   * Attributo iRRubContatti
   */
  protected Integer iIdRubContatti;

  protected String iDescRubContatti;

  /**
   * Attributo iRagioneSocPrvc
   */
  protected String iRagioneSocPrvc;

  /**
   * Attributo iIndirizzoPrvc
   */
  protected String iIndirizzoPrvc;

  /**
   * Attributo iLocalitaPrvc
   */
  protected String iLocalitaPrvc;

  /**
   * Attributo iCapPrvc
   */
  protected String iCapPrvc;

  /**
   * Attributo iIdNazionePrvc
   */
  protected String iIdNazionePrvc;

  protected String iDescNazionePrvc;

  /**
   * Attributo iIdProvinciaPrvc
   */
  protected String iIdProvinciaPrvc;

  /**
   * Attributo iStatoEvasione
   */
  protected char iStatoEvasione = RptPreventivoCommessaTestata.ST_EVA_INEVASO;

  /**
   * Attributo iRichiedenteCli
   */
  protected String iRichiedenteCli;

  /**
   * Attributo iMailPrvc
   */
  protected String iMailPrvc;

  /**
   * Attributo iFaxPrvc
   */
  protected String iFaxPrvc;

  /**
   * Attributo iAttImporto1
   */
  protected BigDecimal iAttImporto1;

  /**
   * Attributo iAttImporto2
   */
  protected BigDecimal iAttImporto2;

  /**
   * Attributo iAttStringa1
   */
  protected String iAttStringa1;

  /**
   * Attributo iAttStringa2
   */
  protected String iAttStringa2;

  /**
   * Attributo iAttData1
   */
  protected java.sql.Date iAttData1;

  /**
   * Attributo iAttData2
   */
  protected java.sql.Date iAttData2;

  /**
   * Attributo iNotaRich
   */
  protected String iNotaRich;

  /**
   * Attributo iRAutPrevc
   */
  protected String iIdAutPrevc;

  protected String iDescAutPrevc;

  /**
   * Attributo iRResPrevc
   */
  protected String iIdRevPrevc;

  protected String iDescRevPrevc;

  /**
   * Attributo iRAppPrevc
   */
  protected String iIdAppPrevc;

  protected String iDescAppPrevc;

  /**
   * Attributo iDataConsegRcs
   */
  protected java.sql.Date iDataConsegRcs;

  /**
   * Attributo iDataConsegPrv
   */
  protected java.sql.Date iDataConsegPrv;

  /**
   * Attributo iSetConsegRcs
   */
  protected String iSetConsegRcs;

  /**
   * Attributo iSetConsegPrv
   */
  protected String iSetConsegPrv;

  /**
   * Attributo iRValuta
   */
  protected String iIdValuta;
  protected String iIdValutaAz;

  protected String iDescValuta;

  /**
   * Attributo iFattoreCambio
   */
  protected BigDecimal iFattoreCambio;

  /**
   * Attributo iRListinoVen
   */
  protected String iIdListinoVen;

  protected String iDescListinoVen;

  /**
   * Attributo iRListinoAcq
   */
  protected String iIdListinoAcq;

  protected String iDescListinoAcq;

  /**
   * Attributo iRAmbiente
   */
  protected String iAmbiente;

  protected String iDescAmbiente;

  /**
   * Attributo iRepCosArt
   */
  protected char iRepCosArt = RptPreventivoCommessaTestata.REP_COS_AMBIENTE_COSTO;

  /**
   * Attributo iDataRiferimento
   */
  protected java.sql.Date iDataRiferimento;

  /**
   * Attributo iGgValidita
   */
  protected int iGgValidita;

  /**
   * Attributo iDataFineValc
   */
  protected java.sql.Date iDataFineValc;

  /**
   * Attributo iSettFineValc
   */
  protected String iSettFineValc;

  /**
   * Attributo iDataFineVal
   */
  protected java.sql.Date iDataFineVal;

  /**
   * Attributo iSettFineVal
   */
  protected String iSettFineVal;

  /**
   * Attributo iRGesCommenti
   */
  protected Integer iRGesCommenti;

  /**
   * Attributo iRDocumentoMm
   */
  protected String iRDocumentoMm;

  /**
   * Attributo iNote
   */
  protected String iNote;

  protected BigDecimal iPercentuale = new BigDecimal(0);

  protected BigDecimal iPercentualeVoce = new BigDecimal(0);
  protected BigDecimal iPercentualeArtic = new BigDecimal(0);
  protected BigDecimal iPercentualeRisU = new BigDecimal(0);
  protected BigDecimal iPercentualeRisM = new BigDecimal(0);

  /**
   * Attributo iStpAllTec
   */
  protected char iStpAllTec = RptPreventivoCommessaTestata.STP_ALL_TEC_DA_EFFETTUARE;

  /**
   * Attributo iGenOffc
   */
  protected char iGenOffc = RptPreventivoCommessaTestata.STATO_OFF_DA_GENERARE;

  /**
   * Attributo iGenRigOffc
   */
  protected char iGenRigOffc = RptPreventivoCommessaTestata.MOD_GEN_OFF_RIG_COMM;

  /**
   * Attributo iRAnnoOffc
   */
  protected String iIdAnnoOffc;

  /**
   * Attributo iRNumeroOffc
   */
  protected String iNumeroOffc;

  /**
   * Attributo iVlrVociPrevc
   */
  protected BigDecimal iVlrVociPrevc;

  /**
   * Attributo iVlrArtPrevc
   */
  protected BigDecimal iVlrArtPrevc;

  /**
   * Attributo iVlrRisuPrevc
   */
  protected BigDecimal iVlrRisuPrevc;

  /**
   * Attributo iVlrRismPrevc
   */
  protected BigDecimal iVlrRismPrevc;

  /**
   * Attributo iVlrTotPrevc
   */
  protected BigDecimal iVlrTotPrevc;

  /**
   * Attributo iCosVociPrevc
   */
  protected BigDecimal iCosVociPrevc;

  /**
   * Attributo iCosArtPrevc
   */
  protected BigDecimal iCosArtPrevc;

  /**
   * Attributo iCosRisuPrevc
   */
  protected BigDecimal iCosRisuPrevc;

  /**
   * Attributo iCosRismPrevc
   */
  protected BigDecimal iCosRismPrevc;

  /**
   * Attributo iCosTotPrevc
   */
  protected BigDecimal iCosTotPrevc;

  /**
   * Attributo iMdcVociPrevc
   */
  protected BigDecimal iMdcVociPrevc;

  /**
   * Attributo iMdcArtPrevc
   */
  protected BigDecimal iMdcArtPrevc;

  /**
   * Attributo iMdcRisuPrevc
   */
  protected BigDecimal iMdcRisuPrevc;

  /**
   * Attributo iMdcRismPrevc
   */
  protected BigDecimal iMdcRismPrevc;

  public BigDecimal iVlrVociPrevAz = ZERO;
  public BigDecimal iVlrArtPrevAz = ZERO;
  public BigDecimal iVlrRisuPrevAz = ZERO;
  public BigDecimal iVlrRismPrevAz = ZERO;
  public BigDecimal iVlrTotPrevAz = ZERO;
  public BigDecimal iCosVociPrevAz = ZERO;
  public BigDecimal iCosArtPrevAz = ZERO;
  public BigDecimal iCosRisuPrevAz = ZERO;
  public BigDecimal iCosRismPrevAz = ZERO;
  public BigDecimal iCosTotPrevAz = ZERO;
  public BigDecimal iMdcVociPrevAz = ZERO;
  public BigDecimal iMdcArtPrevAz = ZERO;
  public BigDecimal iMdcRisuPrevAz = ZERO;
  public BigDecimal iMdcRismPrevAz = ZERO;
  public BigDecimal iMdcTotPrevAz = ZERO;

  /**
   * Attributo iMdcTotPrevc
   */
  protected BigDecimal iMdcTotPrevc;

  /**
   * Attributo iFlagRisUte1
   */
  protected char iFlagRisUte1;

  /**
   * Attributo iFlagRisUte2
   */
  protected char iFlagRisUte2;

  /**
   * Attributo iFlagRisUte3
   */
  protected char iFlagRisUte3;

  /**
   * Attributo iFlagRisUte4
   */
  protected char iFlagRisUte4;

  /**
   * Attributo iFlagRisUte5
   */
  protected char iFlagRisUte5;

  /**
   * Attributo iStringaRisUte1
   */
  protected String iStringaRisUte1;

  /**
   * Attributo iStringaRisUte2
   */
  protected String iStringaRisUte2;

  /**
   * Attributo iNumRisUte1
   */
  protected int iNumRisUte1;

  /**
   * Attributo iNumRisUte2
   */
  protected int iNumRisUte2;

  /**
   * Attributo iWfClassId
   */
  protected short iWfClassId;

  /**
   * Attributo iWfId
   */
  protected String iWfId;

  /**
   * Attributo iWfNodeId
   */
  protected String iWfNodeId;

  /**
   * Attributo iWfSubNodeId
   */
  protected String iWfSubNodeId;

  /**
   * Attributo iIdLingua
   */
  protected String iIdLingua; //Fix 19818

  /**
   * Attributo iDesLingua
   */
  protected String iDesLingua; //Fix 19818

  //33048 inizio
  protected String iCommenti;
  protected String iCommentiPiede;
  //33048 fine
  
  /**
   * Attributo iRBatchJobId
   */
  protected Proxy iBatchJobId = new Proxy(com.thera.thermfw.batch.AvailableReport.class);

  protected OneToMany iReportPreventivoCommessaRiga = new OneToMany(RptPreventivoCommessaRiga.class, this, 7, true);

  public String iUtenteCrz;
  public String iUtenteAgg;
  public Timestamp iTimestamp;
  public Timestamp iTimestampAgg;
  protected char iStato;

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
      cInstance = (RptPreventivoCommessaTestata)Factory.createObject(RptPreventivoCommessaTestata.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  /**
   *  elementWithKey
   * @param key
   * @param lockType
   * @return RptPrevComTes
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static RptPreventivoCommessaTestata elementWithKey(String key, int lockType) throws SQLException
  {
    return (RptPreventivoCommessaTestata)PersistentObject.elementWithKey(RptPreventivoCommessaTestata.class, key, lockType);
  }

  public void setReportNumber(Integer reportNumber)
  {
    this.iReportNumber = reportNumber;
    setDirty();
    setOnDB(false);
    iReportPreventivoCommessaRiga.setFatherKeyChanged();
  }

  public Integer getReportNumber()
  {
    return iReportNumber;
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
  public void setRigaJobId(int rigaJobId)
  {
    this.iRigaJobId = rigaJobId;
    setDirty();
    setOnDB(false);
    iReportPreventivoCommessaRiga.setFatherKeyChanged();
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
  public int getRigaJobId()
  {
    return iRigaJobId;
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
   * @param dataPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDataPrevc(java.sql.Date dataPrevc)
  {
    this.iDataPrevc = dataPrevc;
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
  public java.sql.Date getDataPrevc()
  {
    return iDataPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param numeroPrevcFmt
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNumeroPrevcFmt(String numeroPrevcFmt)
  {
    this.iNumeroPrevcFmt = numeroPrevcFmt;
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
  public String getNumeroPrevcFmt()
  {
    return iNumeroPrevcFmt;
  }

  /**
   * Valorizza l'attributo.
   * @param rStabilimento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdStabilimento(String stabilimento)
  {
    this.iIdStabilimento = stabilimento;
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
  public String getIdStabilimento()
  {
    return iIdStabilimento;
  }

  public void setDescStabilimento(String descStabilimento)
  {
    iDescStabilimento = descStabilimento;
    setDirty();
  }

  public String getDescStabilimento()
  {
    return iDescStabilimento;
  }

  /**
   * Valorizza l'attributo.
   * @param rMagazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdMagazzino(String magazzino)
  {
    this.iIdMagazzino = magazzino;
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
  public String getIdMagazzino()
  {
    return iIdMagazzino;
  }

  public void setDescMagazzino(String descMagazzino)
  {
    iDescMagazzino = descMagazzino;
    setDirty();
  }

  public String getDescMagazzino()
  {
    return iDescMagazzino;
  }

  /**
   * Valorizza l'attributo.
   * @param rCauPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCauPrevc(String cauPrevc)
  {
    this.iIdCauPrevc = cauPrevc;
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
  public String getIdCauPrevc()
  {
    return iIdCauPrevc;
  }

  public void setDescCauPrevc(String descCauPrevc)
  {
    iDescCauPrevc = descCauPrevc;
    setDirty();
  }

  public String getDescCauPrevc()
  {
    return iDescCauPrevc;
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
  public void setIdCommessa(String commessa)
  {
    this.iIdCommessa = commessa;
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
  public String getIdCommessa()
  {
    return iIdCommessa;
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
   * @param rCommessaCa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessaCa(String commessaCa)
  {
    this.iIdCommessaCa = commessaCa;
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
  public String getIdCommessaCa()
  {
    return iIdCommessaCa;
  }

  public void setDescCommessaCa(String descCommessaCa)
  {
    iDescCommessaCa = descCommessaCa;
    setDirty();
  }

  public String getDescCommessaCa()
  {
    return iDescCommessaCa;
  }

  /**
   * Valorizza l'attributo.
   * @param tpIntestPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setTpIntestPrevc(char tpIntestPrevc)
  {
    this.iTpIntestPrevc = tpIntestPrevc;
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
  public char getTpIntestPrevc()
  {
    return iTpIntestPrevc;
  }

  public void setTipoIntestatario(String tpIntestPrevc)
  {
    this.iTipoIntestatario = tpIntestPrevc;
    setDirty();
  }

  public String getTipoIntestatario()
  {
    return iTipoIntestatario;
  }

  /**
   * Valorizza l'attributo.
   * @param rCliente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCliente(String cliente)
  {
    this.iIdCliente = cliente;
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
  public String getIdCliente()
  {
    return iIdCliente;
  }

  /**
   * Valorizza l'attributo.
   * @param rDivisione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdDivisione(String divisione)
  {
    this.iIdDivisione = divisione;
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
  public String getIdDivisione()
  {
    return iIdDivisione;
  }

  /**
   * Valorizza l'attributo.
   * @param rAnagrafico
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnagrafico(Integer anagrafico)
  {
    this.iIdAnagrafico = anagrafico;
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
  public Integer getIdAnagrafico()
  {
    return iIdAnagrafico;
  }

  /**
   * Valorizza l'attributo.
   * @param rRubContatti
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRubContatti(Integer rubContatti)
  {
    this.iIdRubContatti = rubContatti;
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
  public Integer getIdRubContatti()
  {
    return iIdRubContatti;
  }

  public void setDescRubContatti(String descRubContatti)
  {
    iDescRubContatti = descRubContatti;
    setDirty();
  }

  public String getDescRubContatti()
  {
    return iDescRubContatti;
  }

  /**
   * Valorizza l'attributo.
   * @param ragioneSocPrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRagioneSocPrvc(String ragioneSocPrvc)
  {
    this.iRagioneSocPrvc = ragioneSocPrvc;
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
  public String getRagioneSocPrvc()
  {
    return iRagioneSocPrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param indirizzoPrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIndirizzoPrvc(String indirizzoPrvc)
  {
    this.iIndirizzoPrvc = indirizzoPrvc;
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
  public String getIndirizzoPrvc()
  {
    return iIndirizzoPrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param localitaPrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setLocalitaPrvc(String localitaPrvc)
  {
    this.iLocalitaPrvc = localitaPrvc;
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
  public String getLocalitaPrvc()
  {
    return iLocalitaPrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param capPrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCapPrvc(String capPrvc)
  {
    this.iCapPrvc = capPrvc;
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
  public String getCapPrvc()
  {
    return iCapPrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param rNazionePrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNazionePrvc(String nazionePrvc)
  {
    this.iIdNazionePrvc = nazionePrvc;
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
  public String getIdNazionePrvc()
  {
    return iIdNazionePrvc;
  }

  public void setDescNazionePrvc(String descNazionePrvc)
  {
    iDescNazionePrvc = descNazionePrvc;
    setDirty();
  }

  public String getDescNazionePrvc()
  {
    return iDescNazionePrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param rProvinciaPrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdProvinciaPrvc(String provinciaPrvc)
  {
    this.iIdProvinciaPrvc = provinciaPrvc;
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
  public String getIdProvinciaPrvc()
  {
    return iIdProvinciaPrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param statoEvasione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setStatoEvasione(char statoEvasione)
  {
    this.iStatoEvasione = statoEvasione;
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
  public char getStatoEvasione()
  {
    return iStatoEvasione;
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
   * @param richiedenteCli
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRichiedenteCli(String richiedenteCli)
  {
    this.iRichiedenteCli = richiedenteCli;
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
  public String getRichiedenteCli()
  {
    return iRichiedenteCli;
  }

  /**
   * Valorizza l'attributo.
   * @param mailPrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMailPrvc(String mailPrvc)
  {
    this.iMailPrvc = mailPrvc;
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
  public String getMailPrvc()
  {
    return iMailPrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param faxPrvc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setFaxPrvc(String faxPrvc)
  {
    this.iFaxPrvc = faxPrvc;
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
  public String getFaxPrvc()
  {
    return iFaxPrvc;
  }

  /**
   * Valorizza l'attributo.
   * @param attImporto1
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setAttImporto1(BigDecimal attImporto1)
  {
    this.iAttImporto1 = attImporto1;
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
  public BigDecimal getAttImporto1()
  {
    return iAttImporto1;
  }

  /**
   * Valorizza l'attributo.
   * @param attImporto2
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setAttImporto2(BigDecimal attImporto2)
  {
    this.iAttImporto2 = attImporto2;
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
  public BigDecimal getAttImporto2()
  {
    return iAttImporto2;
  }

  /**
   * Valorizza l'attributo.
   * @param attStringa1
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setAttStringa1(String attStringa1)
  {
    this.iAttStringa1 = attStringa1;
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
  public String getAttStringa1()
  {
    return iAttStringa1;
  }

  /**
   * Valorizza l'attributo.
   * @param attStringa2
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setAttStringa2(String attStringa2)
  {
    this.iAttStringa2 = attStringa2;
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
  public String getAttStringa2()
  {
    return iAttStringa2;
  }

  /**
   * Valorizza l'attributo.
   * @param attData1
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setAttData1(java.sql.Date attData1)
  {
    this.iAttData1 = attData1;
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
  public java.sql.Date getAttData1()
  {
    return iAttData1;
  }

  /**
   * Valorizza l'attributo.
   * @param attData2
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setAttData2(java.sql.Date attData2)
  {
    this.iAttData2 = attData2;
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
  public java.sql.Date getAttData2()
  {
    return iAttData2;
  }

  /**
   * Valorizza l'attributo.
   * @param notaRich
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNotaRich(String notaRich)
  {
    this.iNotaRich = notaRich;
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
  public String getNotaRich()
  {
    return iNotaRich;
  }

  /**
   * Valorizza l'attributo.
   * @param rAutPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAutPrevc(String autPrevc)
  {
    this.iIdAutPrevc = autPrevc;
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
  public String getIdAutPrevc()
  {
    return iIdAutPrevc;
  }

  public void setDescAutPrevc(String descAutPrevc)
  {
    iDescAutPrevc = descAutPrevc;
    setDirty();
  }

  public String getDescAutPrevc()
  {
    return iDescAutPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param rResPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdResPrevc(String revPrevc)
  {
    this.iIdRevPrevc = revPrevc;
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
  public String getIdResPrevc()
  {
    return iIdRevPrevc;
  }

  public void setDescRevPrevc(String descRevPrevc)
  {
    iDescRevPrevc = descRevPrevc;
    setDirty();
  }

  public String getDescRevPrevc()
  {
    return iDescRevPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param rAppPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAppPrevc(String appPrevc)
  {
    this.iIdAppPrevc = appPrevc;
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
  public String getIdAppPrevc()
  {
    return iIdAppPrevc;
  }

  public void setDescAppPrevc(String descAppPrevc)
  {
    iDescAppPrevc = descAppPrevc;
    setDirty();
  }

  public String getDescAppPrevc()
  {
    return iDescAppPrevc;
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
   * @param setConsegRcs
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSetConsegRcs(String setConsegRcs)
  {
    this.iSetConsegRcs = setConsegRcs;
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
  public String getSetConsegRcs()
  {
    return iSetConsegRcs;
  }

  /**
   * Valorizza l'attributo.
   * @param setConsegPrv
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSetConsegPrv(String setConsegPrv)
  {
    this.iSetConsegPrv = setConsegPrv;
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
  public String getSetConsegPrv()
  {
    return iSetConsegPrv;
  }

  /**
   * Valorizza l'attributo.
   * @param rValuta
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdValuta(String valuta)
  {
    this.iIdValuta = valuta;
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
  public String getIdValuta()
  {
    return iIdValuta;
  }

  public void setIdValutaAz(String valuta)
  {
    this.iIdValutaAz = valuta;
    setDirty();
  }

  public String getIdValutaAz()
  {
    return iIdValutaAz;
  }

  public void setDescValuta(String descValuta)
  {
    iDescValuta = descValuta;
    setDirty();
  }

  public String getDescValuta()
  {
    return iDescValuta;
  }

  /**
   * Valorizza l'attributo.
   * @param fattoreCambio
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setFattoreCambio(BigDecimal fattoreCambio)
  {
    this.iFattoreCambio = fattoreCambio;
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
  public BigDecimal getFattoreCambio()
  {
    return iFattoreCambio;
  }

  /**
   * Valorizza l'attributo.
   * @param rListinoVen
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdListinoVen(String listinoVen)
  {
    this.iIdListinoVen = listinoVen;
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
  public String getIdListinoVen()
  {
    return iIdListinoVen;
  }

  public void setDescListinoVen(String descListinoVen)
  {
    iDescListinoVen = descListinoVen;
    setDirty();
  }

  public String getDescListinoVen()
  {
    return iDescListinoVen;
  }

  /**
   * Valorizza l'attributo.
   * @param rListinoAcq
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdListinoAcq(String listinoAcq)
  {
    this.iIdListinoAcq = listinoAcq;
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
  public String getIdListinoAcq()
  {
    return iIdListinoAcq;
  }

  public void setDescListinoAcq(String descListinoAcq)
  {
    iDescListinoAcq = descListinoAcq;
    setDirty();
  }

  public String getDescListinoAcq()
  {
    return iDescListinoAcq;
  }

  /**
   * Valorizza l'attributo.
   * @param rAmbiente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setAmbiente(String ambiente)
  {
    this.iAmbiente = ambiente;
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
  public String getAmbiente()
  {
    return iAmbiente;
  }

  public void setDescAmbiente(String descAmbiente)
  {
    iDescAmbiente = descAmbiente;
    setDirty();
  }

  public String getDescAmbiente()
  {
    return iDescAmbiente;
  }

  /**
   * Valorizza l'attributo.
   * @param repCosArt
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setRepCosArt(char repCosArt)
  {
    this.iRepCosArt = repCosArt;
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
  public char getRepCosArt()
  {
    return iRepCosArt;
  }

  /**
   * Valorizza l'attributo.
   * @param dataRiferimento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDataRiferimento(java.sql.Date dataRiferimento)
  {
    this.iDataRiferimento = dataRiferimento;
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
  public java.sql.Date getDataRiferimento()
  {
    return iDataRiferimento;
  }

  /**
   * Valorizza l'attributo.
   * @param ggValidita
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setGgValidita(int ggValidita)
  {
    this.iGgValidita = ggValidita;
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
  public int getGgValidita()
  {
    return iGgValidita;
  }

  /**
   * Valorizza l'attributo.
   * @param dataFineValc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDataFineValc(java.sql.Date dataFineValc)
  {
    this.iDataFineValc = dataFineValc;
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
  public java.sql.Date getDataFineValc()
  {
    return iDataFineValc;
  }

  /**
   * Valorizza l'attributo.
   * @param settFineValc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSettFineValc(String settFineValc)
  {
    this.iSettFineValc = settFineValc;
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
  public String getSettFineValc()
  {
    return iSettFineValc;
  }

  /**
   * Valorizza l'attributo.
   * @param dataFineVal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setDataFineVal(java.sql.Date dataFineVal)
  {
    this.iDataFineVal = dataFineVal;
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
  public java.sql.Date getDataFineVal()
  {
    return iDataFineVal;
  }

  /**
   * Valorizza l'attributo.
   * @param settFineVal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setSettFineVal(String settFineVal)
  {
    this.iSettFineVal = settFineVal;
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
  public String getSettFineVal()
  {
    return iSettFineVal;
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
  public void setRGesCommenti(Integer gesCommenti)
  {
    this.iRGesCommenti = gesCommenti;
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
  public void setRDocumentoMm(String documentoMm)
  {
    this.iRDocumentoMm = documentoMm;
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
   * @param note
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNote(String note)
  {
    this.iNote = note;
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
  public String getNote()
  {
    return iNote;
  }

  /**
   * Valorizza l'attributo.
   * @param stpAllTec
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setStpAllTec(char stpAllTec)
  {
    this.iStpAllTec = stpAllTec;
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
  public char getStpAllTec()
  {
    return iStpAllTec;
  }

  /**
   * Valorizza l'attributo.
   * @param genOffc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setGenOffc(char genOffc)
  {
    this.iGenOffc = genOffc;
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
  public char getGenOffc()
  {
    return iGenOffc;
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
   * @param rAnnoOffc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoOffc(String annoOffc)
  {
    this.iIdAnnoOffc = annoOffc;
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
  public String getIdAnnoOffc()
  {
    return iIdAnnoOffc;
  }

  /**
   * Valorizza l'attributo.
   * @param rNumeroOffc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNumeroOffc(String numeroOffc)
  {
    this.iNumeroOffc = numeroOffc;
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
  public String getNumeroOffc()
  {
    return iNumeroOffc;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrVociPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrVociPrevc(BigDecimal vlrVociPrevc)
  {
    this.iVlrVociPrevc = vlrVociPrevc;
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
  public BigDecimal getVlrVociPrevc()
  {
    return iVlrVociPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrArtPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrArtPrevc(BigDecimal vlrArtPrevc)
  {
    this.iVlrArtPrevc = vlrArtPrevc;
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
  public BigDecimal getVlrArtPrevc()
  {
    return iVlrArtPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrRisuPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrRisuPrevc(BigDecimal vlrRisuPrevc)
  {
    this.iVlrRisuPrevc = vlrRisuPrevc;
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
  public BigDecimal getVlrRisuPrevc()
  {
    return iVlrRisuPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrRismPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrRismPrevc(BigDecimal vlrRismPrevc)
  {
    this.iVlrRismPrevc = vlrRismPrevc;
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
  public BigDecimal getVlrRismPrevc()
  {
    return iVlrRismPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param vlrTotPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setVlrTotPrevc(BigDecimal vlrTotPrevc)
  {
    this.iVlrTotPrevc = vlrTotPrevc;
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
  public BigDecimal getVlrTotPrevc()
  {
    return iVlrTotPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param cosVociPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosVociPrevc(BigDecimal cosVociPrevc)
  {
    this.iCosVociPrevc = cosVociPrevc;
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
  public BigDecimal getCosVociPrevc()
  {
    return iCosVociPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param cosArtPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosArtPrevc(BigDecimal cosArtPrevc)
  {
    this.iCosArtPrevc = cosArtPrevc;
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
  public BigDecimal getCosArtPrevc()
  {
    return iCosArtPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param cosRisuPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosRisuPrevc(BigDecimal cosRisuPrevc)
  {
    this.iCosRisuPrevc = cosRisuPrevc;
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
  public BigDecimal getCosRisuPrevc()
  {
    return iCosRisuPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param cosRismPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosRismPrevc(BigDecimal cosRismPrevc)
  {
    this.iCosRismPrevc = cosRismPrevc;
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
  public BigDecimal getCosRismPrevc()
  {
    return iCosRismPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param cosTotPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setCosTotPrevc(BigDecimal cosTotPrevc)
  {
    this.iCosTotPrevc = cosTotPrevc;
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
  public BigDecimal getCosTotPrevc()
  {
    return iCosTotPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcVociPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcVociPrevc(BigDecimal mdcVociPrevc)
  {
    this.iMdcVociPrevc = mdcVociPrevc;
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
  public BigDecimal getMdcVociPrevc()
  {
    return iMdcVociPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcArtPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcArtPrevc(BigDecimal mdcArtPrevc)
  {
    this.iMdcArtPrevc = mdcArtPrevc;
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
  public BigDecimal getMdcArtPrevc()
  {
    return iMdcArtPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcRisuPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcRisuPrevc(BigDecimal mdcRisuPrevc)
  {
    this.iMdcRisuPrevc = mdcRisuPrevc;
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
  public BigDecimal getMdcRisuPrevc()
  {
    return iMdcRisuPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcRismPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcRismPrevc(BigDecimal mdcRismPrevc)
  {
    this.iMdcRismPrevc = mdcRismPrevc;
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
  public BigDecimal getMdcRismPrevc()
  {
    return iMdcRismPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param mdcTotPrevc
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setMdcTotPrevc(BigDecimal mdcTotPrevc)
  {
    this.iMdcTotPrevc = mdcTotPrevc;
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
  public BigDecimal getMdcTotPrevc()
  {
    return iMdcTotPrevc;
  }

  /**
   * Valorizza l'attributo.
   * @param flagRisUte1
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setFlagRisUte1(char flagRisUte1)
  {
    this.iFlagRisUte1 = flagRisUte1;
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
  public char getFlagRisUte1()
  {
    return iFlagRisUte1;
  }

  /**
   * Valorizza l'attributo.
   * @param flagRisUte2
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setFlagRisUte2(char flagRisUte2)
  {
    this.iFlagRisUte2 = flagRisUte2;
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
  public char getFlagRisUte2()
  {
    return iFlagRisUte2;
  }

  /**
   * Valorizza l'attributo.
   * @param flagRisUte3
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setFlagRisUte3(char flagRisUte3)
  {
    this.iFlagRisUte3 = flagRisUte3;
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
  public char getFlagRisUte3()
  {
    return iFlagRisUte3;
  }

  /**
   * Valorizza l'attributo.
   * @param flagRisUte4
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setFlagRisUte4(char flagRisUte4)
  {
    this.iFlagRisUte4 = flagRisUte4;
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
  public char getFlagRisUte4()
  {
    return iFlagRisUte4;
  }

  /**
   * Valorizza l'attributo.
   * @param flagRisUte5
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setFlagRisUte5(char flagRisUte5)
  {
    this.iFlagRisUte5 = flagRisUte5;
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
  public char getFlagRisUte5()
  {
    return iFlagRisUte5;
  }

  /**
   * Valorizza l'attributo.
   * @param stringaRisUte1
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setStringaRisUte1(String stringaRisUte1)
  {
    this.iStringaRisUte1 = stringaRisUte1;
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
  public String getStringaRisUte1()
  {
    return iStringaRisUte1;
  }

  /**
   * Valorizza l'attributo.
   * @param stringaRisUte2
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setStringaRisUte2(String stringaRisUte2)
  {
    this.iStringaRisUte2 = stringaRisUte2;
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
  public String getStringaRisUte2()
  {
    return iStringaRisUte2;
  }

  /**
   * Valorizza l'attributo.
   * @param numRisUte1
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNumRisUte1(int numRisUte1)
  {
    this.iNumRisUte1 = numRisUte1;
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
  public int getNumRisUte1()
  {
    return iNumRisUte1;
  }

  /**
   * Valorizza l'attributo.
   * @param numRisUte2
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setNumRisUte2(int numRisUte2)
  {
    this.iNumRisUte2 = numRisUte2;
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
  public int getNumRisUte2()
  {
    return iNumRisUte2;
  }

  /**
   * Valorizza l'attributo.
   * @param wfClassId
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setWfClassId(short wfClassId)
  {
    this.iWfClassId = wfClassId;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return short
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public short getWfClassId()
  {
    return iWfClassId;
  }

  /**
   * Valorizza l'attributo.
   * @param wfId
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setWfId(String wfId)
  {
    this.iWfId = wfId;
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
  public String getWfId()
  {
    return iWfId;
  }

  /**
   * Valorizza l'attributo.
   * @param wfNodeId
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setWfNodeId(String wfNodeId)
  {
    this.iWfNodeId = wfNodeId;
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
  public String getWfNodeId()
  {
    return iWfNodeId;
  }

  /**
   * Valorizza l'attributo.
   * @param wfSubNodeId
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setWfSubNodeId(String wfSubNodeId)
  {
    this.iWfSubNodeId = wfSubNodeId;
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
  public String getWfSubNodeId()
  {
    return iWfSubNodeId;
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
    this.iUtenteCrz = rUtenteCrz;
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
    return iUtenteCrz;
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
    this.iUtenteAgg = rUtenteAgg;
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
    return iUtenteAgg;
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
   * @param rBatchJobId
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setBatchJobId(AvailableReport rBatchJobId)
  {
    this.iBatchJobId.setObject(rBatchJobId);
    setDirty();
    setOnDB(false);
    iReportPreventivoCommessaRiga.setFatherKeyChanged();
  }

  /**
   * setRBatchJobIdKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public void setBatchJobIdKey(String key)
  {
    iBatchJobId.setKey(key);
    setDirty();
    setOnDB(false);
    iReportPreventivoCommessaRiga.setFatherKeyChanged();
  }

  /**
   * getRBatchJobIdKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getBatchJobIdKey()
  {
    return iBatchJobId.getKey();
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
  public void setRBatchJobId(Integer batchJobId)
  {
    String key = iBatchJobId.getKey();
    iBatchJobId.setKey(KeyHelper.replaceTokenObjectKey(key, 1, batchJobId));
    setDirty();
    setOnDB(false);
    iReportPreventivoCommessaRiga.setFatherKeyChanged();
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
  public Integer getRBatchJobId()
  {
    String key = iBatchJobId.getKey();
    String objBatchJobId = KeyHelper.getTokenObjectKey(key, 1);
    return KeyHelper.stringToIntegerObj(objBatchJobId);

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
    RptPreventivoCommessaTestataPO rptPrevComTesPO = (RptPreventivoCommessaTestataPO)obj;
    if (rptPrevComTesPO.iDataPrevc != null)
      iDataPrevc = (java.sql.Date)rptPrevComTesPO.iDataPrevc.clone();
    if (rptPrevComTesPO.iAttData1 != null)
      iAttData1 = (java.sql.Date)rptPrevComTesPO.iAttData1.clone();
    if (rptPrevComTesPO.iAttData2 != null)
      iAttData2 = (java.sql.Date)rptPrevComTesPO.iAttData2.clone();
    if (rptPrevComTesPO.iDataConsegRcs != null)
      iDataConsegRcs = (java.sql.Date)rptPrevComTesPO.iDataConsegRcs.clone();
    if (rptPrevComTesPO.iDataConsegPrv != null)
      iDataConsegPrv = (java.sql.Date)rptPrevComTesPO.iDataConsegPrv.clone();
    if (rptPrevComTesPO.iDataRiferimento != null)
      iDataRiferimento = (java.sql.Date)rptPrevComTesPO.iDataRiferimento.clone();
    if (rptPrevComTesPO.iDataFineValc != null)
      iDataFineValc = (java.sql.Date)rptPrevComTesPO.iDataFineValc.clone();
    if (rptPrevComTesPO.iDataFineVal != null)
      iDataFineVal = (java.sql.Date)rptPrevComTesPO.iDataFineVal.clone();
    if (rptPrevComTesPO.iTimestamp != null)
      iTimestamp = (java.sql.Timestamp)rptPrevComTesPO.iTimestamp.clone();
    if (rptPrevComTesPO.iTimestampAgg != null)
      iTimestampAgg = (java.sql.Timestamp)rptPrevComTesPO.iTimestampAgg.clone();
    iBatchJobId.setEqual(rptPrevComTesPO.iBatchJobId);
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
    setRBatchJobId(KeyHelper.stringToIntegerObj(keys[0]));
    setReportNumber(KeyHelper.stringToIntegerObj(keys[1]));
    setRigaJobId(KeyHelper.stringToInt(keys[2]));
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
    Integer batchJobId = getRBatchJobId();
    Integer reportNr = getReportNumber();
    Integer rigaJobId = new Integer(getRigaJobId());
    Object[] keyParts =
      {
      batchJobId, reportNr, rigaJobId};
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
    return RptPreventivoCommessaTestataTM.getInstance();
  }

  public BigDecimal getPercentuale()
  {
    return iPercentuale;
  }

  public void setPercentuale(BigDecimal perc)
  {
    this.iPercentuale = perc;
  }

//
  public BigDecimal getPercentualeVoce()
  {
    return iPercentualeVoce;
  }

  public void setPercentualeVoce(BigDecimal perc)
  {
    this.iPercentualeVoce = perc;
  }

  public BigDecimal getPercentualeArtic()
  {
    return iPercentualeArtic;
  }

  public void setPercentualeArtic(BigDecimal perc)
  {
    this.iPercentualeArtic = perc;
  }

  public BigDecimal getPercentualeRisU()
  {
    return iPercentualeRisU;
  }

  public void setPercentualeRisU(BigDecimal perc)
  {
    this.iPercentualeRisU = perc;
  }

  public BigDecimal getPercentualeRisM()
  {
    return iPercentualeRisM;
  }

  public void setPercentualeRisM(BigDecimal perc)
  {
    this.iPercentualeRisM = perc;
  }

//
  public BigDecimal getVlrVociPrevAz()
  {
    return iVlrVociPrevAz;
  }

  public void setVlrVociPrevAz(BigDecimal bd)
  {
    iVlrVociPrevAz = bd;
  }

  public BigDecimal getVlrArtPrevAz()
  {
    return iVlrArtPrevAz;
  }

  public void setVlrArtPrevAz(BigDecimal bd)
  {
    iVlrArtPrevAz = bd;
  }

  public BigDecimal getVlrRisuPrevAz()
  {
    return iVlrRisuPrevAz;
  }

  public void setVlrRisuPrevAz(BigDecimal bd)
  {
    iVlrRisuPrevAz = bd;
  }

  public BigDecimal getVlrRismPrevAz()
  {
    return iVlrRismPrevAz;
  }

  public void setVlrRismPrevAz(BigDecimal bd)
  {
    iVlrRismPrevAz = bd;
  }

  public BigDecimal getVlrTotPrevAz()
  {
    return iVlrTotPrevAz;
  }

  public void setVlrTotPrevAz(BigDecimal bd)
  {
    iVlrTotPrevAz = bd;
  }

  public BigDecimal getCosVociPrevAz()
  {
    return iCosVociPrevAz;
  }

  public void setCosVociPrevAz(BigDecimal bd)
  {
    iCosVociPrevAz = bd;
  }

  public BigDecimal getCosArtPrevAz()
  {
    return iCosArtPrevAz;
  }

  public void setCosArtPrevAz(BigDecimal bd)
  {
    iCosArtPrevAz = bd;
  }

  public BigDecimal getCosRisuPrevAz()
  {
    return iCosRisuPrevAz;
  }

  public void setCosRisuPrevAz(BigDecimal bd)
  {
    iCosRisuPrevAz = bd;
  }

  public BigDecimal getCosRismPrevAz()
  {
    return iCosRismPrevAz;
  }

  public void setCosRismPrevAz(BigDecimal bd)
  {
    iCosRismPrevAz = bd;
  }

  public BigDecimal getCosTotPrevAz()
  {
    return iCosTotPrevAz;
  }

  public void setCosTotPrevAz(BigDecimal bd)
  {
    iCosTotPrevAz = bd;
  }

  public BigDecimal getMdcVociPrevAz()
  {
    return iMdcVociPrevAz;
  }

  public void setMdcVociPrevAz(BigDecimal bd)
  {
    iMdcVociPrevAz = bd;
  }

  public BigDecimal getMdcArtPrevAz()
  {
    return iMdcArtPrevAz;
  }

  public void setMdcArtPrevAz(BigDecimal bd)
  {
    iMdcArtPrevAz = bd;
  }

  public BigDecimal getMdcRisuPrevAz()
  {
    return iMdcRisuPrevAz;
  }

  public void setMdcRisuPrevAz(BigDecimal bd)
  {
    iMdcRisuPrevAz = bd;
  }

  public BigDecimal getMdcRismPrevAz()
  {
    return iMdcRismPrevAz;
  }

  public void setMdcRismPrevAz(BigDecimal bd)
  {
    iMdcRismPrevAz = bd;
  }

  public BigDecimal getMdcTotPrevAz()
  {
    return iMdcTotPrevAz;
  }

  public void setMdcTotPrevAz(BigDecimal bd)
  {
    iMdcTotPrevAz = bd;
  }
  //15848
   public void setWfDescription(String wfDescription)
    {
      this.iWfDescription = wfDescription;
      setDirty();
    }


    public String getWfDescription()
    {
      return iWfDescription;
    }

  //Fix 19818 Inizio
  public void setIdLingua(String lingua) {
    this.iIdLingua = lingua;
    setDirty();
  }

  public String getIdLingua() {
    return iIdLingua;
  }

  public void setDesLingua(String desLingua) {
    this.iDesLingua = desLingua;
    setDirty();
  }

  public String getDesLingua() {
    return iDesLingua;
  }
  //Fix 19818 Fine
  //33048 inizio 
  public String getCommenti() {
    return iCommenti;
  }

  public void setCommenti(String commenti) {
    this.iCommenti = commenti;
  }
   
  public String getCommentiPiede() {
    return iCommentiPiede;
  }

  public void setCommentiPiede(String commentiPiede) {
    this.iCommentiPiede = commentiPiede;
  }
  //33048 fine
}

package it.thera.thip.base.commessa;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.articolo.ArticoloVersione;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.azienda.Stabilimento;
import it.thera.thip.base.cliente.ClienteVendita;
import it.thera.thip.base.dipendente.Dipendente;
import it.thera.thip.base.documentoMM.DocumentoMM;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.base.interfca.CommessaCA;
import it.thera.thip.cs.EntitaSingola;
import it.thera.thip.datiTecnici.configuratore.Configurazione;
import it.thera.thip.datiTecnici.configuratore.ConfigurazioneProxyEnh;
import it.thera.thip.vendite.ordineVE.OrdineVendita;
import it.thera.thip.vendite.ordineVE.OrdineVenditaRiga;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.thera.thermfw.cbs.CommentHandler;
import com.thera.thermfw.cbs.CommentHandlerManager;
import com.thera.thermfw.cbs.Commentable;
import com.thera.thermfw.cbs.ExtensibleAttribute;
import com.thera.thermfw.cbs.WfSpecNode;
import com.thera.thermfw.cbs.WfStatus;
import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;
import com.thera.thermfw.persist.ErrorCodes;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.OneToMany;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.ConflictableWithKey;

/**
 * Commessa.
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Wizard 12/12/2001 at 13:53:10
 */
/*
 * Revisions:
 * Fix     Date          Owner      Description
 *         12/12/2001    Wizard     Codice generato da Wizard
 *         24/01/2003    PJ         Aggiunto attributo AggiornamentoSaldi
 * 03463   30/03/2005    A.BOULILA  aggiunto di metodi e di attributi per il modulo di gestione di commesse
 * 04810   04/01/2006    LP         Aggiunto default agli enumerati per i CM
 * 05543   12/06/2006    PJ         Aggiunta commessa di contabilità analitica
 * 09403   10/07/2008    IT         modificato metodo setIdAziendaInternal aggiungendo setKey su istanza di
 *                                  CommessaCA
 * 09625   29/07/2008    MN         Aggiunti attributi ValoreOrdRiorg e RifRigaOrdRiorg per la
 *                                  gestione degli ordini riorganizzati.
 * 10777   08/05/2009    ES         Aggiunta chiamata a ExtensibleAttribute.control() per vedere subito l'estensione
 * 11651   24/11/2009    ES         Aggiungo alla cancellazione la pulizia di WfTimer e WfLog
 * 19897   30/05/2014    Linda      Aggiunti nuovi attributi.
 * 20785   09/12/2014    Linda      Aggiunti nuovi attributi.
 * 27669   02/07/2018    GN         Gestione commessa modello
 * 29025   20/03/2019    Jackal     Aggiunti attributi iTipoPiano e iUtilizzaContoAnticipi
 * 31437   22/06/2020    LTB        Aggiungere la collezione di documenti collegate
 * 32044   18/12/2020	 SZ			Aggiunte metodo savePO() Per il CM da Griglia.
 * 33950   23/07/2021    RA			Aggiunto attributo iDataEstrazioneStorici
 */

public abstract class CommessaPO extends EntitaSingola implements BusinessObject, Authorizable, Deletable, ConflictableWithKey, Commentable {

  /**
   *  instance
   */
  private static Commessa cInstance;

  /**
   * Attributo iIdCommessa
   */
  protected String iIdCommessa;

  /**
   * Attributo iCliente
   */
  protected Proxy iCliente = new Proxy(it.thera.thip.base.cliente.ClienteVendita.class);

  ////PJ 24/01/2003 - BEGIN
  protected boolean iAggiornamentoSaldi;

  ////PJ 24/01/2003 - END

  // Inizio 03463 A.BOULILA
  /**
   * Attributo iValidita
   */
  protected ValiditaCommessa iValidita;

  /**
   * Attributo iDatePrevisti
   */
//  protected DateRange iDatePreviste;

  /**
   * Attributo iStatoAvanzamento
   */
  protected char iStatoAvanzamento = '0';

  /**
   * Attributo iQtaUmPrm
   */
  protected BigDecimal iQtaUmPrm;

  /**
   * Attributo iDataApertura
   */
  protected java.sql.Date iDataApertura;

  /**
   * Attributo iDataChiusura
   */
  protected java.sql.Date iDataChiusura;

  /**
   * Attributo iDataConferma
   */
  protected java.sql.Date iDataConferma;

  /**
   * Attributo iDataInizioPrevista
   */
  protected java.sql.Date iDataInizioPrevista;

  /**
   * Attributo iDataFinePrev
   */
  protected java.sql.Date iDataFinePrevista;

  /**
   * Attributo iDataPrimaAtt
   */
  protected java.sql.Date iDataPrimaAtt;

  /**
   * Attributo iDataUltimAtt
   */
  protected java.sql.Date iDataUltimAtt;

  /**
   * Attributo iDataChiusTec
   */
  protected java.sql.Date iDataChiusTec;

  /**
   * Attributo iDataChiusOpe
   */
  protected java.sql.Date iDataChiusOpe;

  /**
   * Attributo iPianoFatturazione
   */
  protected char iPianoFatturazione = '0';

  /**
   * Attributo iChiudiOrdUltimaFat
   */
  protected boolean iChiudiOrdUltimaFat;

  /**
   * Attributo iNote
   */
  protected String iNote;

  /**
   * Attributo iAttributiEstend
   */
  protected String iAttributiEstend;

  /**
   * Attributo iIdDetRigaOrdine
   */
  Integer iIdDetRigaOrdine = new Integer("0");

  // Inizio 9625
  protected BigDecimal iValoreOrdRiorg;

  protected String iRifRigaOrdRiorg;
  // Fine 9625


  /**
   * Attributo iAmbienteCommessa
   */
  protected Proxy iAmbienteCommessa = new Proxy(it.thera.thip.base.commessa.AmbienteCommessa.class);

  /**
   * Attributo iArticolo
   */
  protected Proxy iArticolo = new Proxy(it.thera.thip.base.articolo.Articolo.class);

  /**
   * Attributo iArticoloVersione
   */
  protected Proxy iArticoloVersione = new Proxy(it.thera.thip.base.articolo.ArticoloVersione.class);

  /**
   * Attributo iAzienda
   */
  protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);

  /**
   * Attributo iCommessaAppartenenza
   */
  protected Proxy iCommessaAppartenenza = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iCommessaPrincipale
   */
  protected Proxy iCommessaPrincipale = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  //Fix 27669 inizio
  /**
   * Attributo iCommessaModello
   */
  protected Proxy iCommessaModello = new Proxy(it.thera.thip.base.commessa.Commessa.class);
  //Fix 27669 fine 

  /**
   * Attributo iConfigurazione
   */
  protected ConfigurazioneProxyEnh iConfigurazione = new ConfigurazioneProxyEnh(it.thera.thip.datiTecnici.configuratore.Configurazione.class);

  /**
   * Attributo iResponsabileCommessa
   */
  protected Proxy iResponsabileCommessa = new Proxy(it.thera.thip.base.dipendente.Dipendente.class);

  /**
   * Attributo iResponsabilePreventivaz
   */
  protected Proxy iResponsabilePreventivaz = new Proxy(it.thera.thip.base.dipendente.Dipendente.class);

  /**
   * Attributo iDocumentoMM
   */
  protected Proxy iDocumentoMM = new Proxy(it.thera.thip.base.documentoMM.DocumentoMM.class);

  /**
   * Attributo iStabilimento
   */
  protected Proxy iStabilimento = new Proxy(it.thera.thip.base.azienda.Stabilimento.class);

  /**
   * Attributo iTipoCommessa
   */
  protected Proxy iTipoCommessa = new Proxy(it.thera.thip.base.commessa.TipoCommessa.class);

  /**
   * Attributo iUmPrmMag
   */
  protected Proxy iUmPrmMag = new Proxy(it.thera.thip.base.generale.UnitaMisura.class);

  /**
   * Attributo iSubnodoWorkflow
   */
  protected Proxy iSubnodoWorkflow = new Proxy(com.thera.thermfw.cbs.WfSpecNode.class);

  /**
   * Attributo iOrdineVendita
   */
  protected Proxy iOrdineVendita = new Proxy(it.thera.thip.vendite.ordineVE.OrdineVendita.class);

  /**
   * Attributo iOrdineVenditaRiga
   */
  protected Proxy iOrdineVenditaRiga = new Proxy(it.thera.thip.vendite.ordineVE.OrdineVenditaRigaPrm.class);
   
  /**
   * Attributo iRateCommesse
   */
  protected OneToMany iRateCommesse = new OneToMany(it.thera.thip.base.commessa.RataCommessa.class, this, 3, false);

  protected OneToMany iDocumentiCollegate = new OneToMany(it.thera.thip.base.commessa.CommessaDocCollegate.class, this, 3, false); //31437
  /**
   * Attributo iCommentiManager
   */
  protected CommentHandlerManager iCommentiManager = null;

  /**
   * Attributo iAttributiEstendibili
   */
  protected ExtensibleAttribute iAttributiEstendibili;

  /**
   * Attributo iWfStatus
   */
  protected WfStatus iWfStatus = null;

  // Fine 03463 A.BOULILA

  // Inizio XXXXX A.BOULILA

  protected Integer iLivelloCommessa;

  // Fine XXXX A.BOULILA

  //Fix 19897 inizio
  protected char iTipoGestioneCigCup = Commessa.DATI_CONVENZIONE;

  protected String iNumeroDocumento;

  protected java.sql.Date iDataDocumento;

  protected String iNumeroItem;

  protected String iIdCommConven;

  protected String iCodiceCUP;

  protected String iCodiceCIG;
  //Fix 19897 fine

  //Fix 20785 inizio
  protected String iNumeroDocOrdAcq;
  protected java.sql.Date iDataDocOrdAcq;
  protected String iIdCommConvOrdAcq;

  protected String iNumeroDocContratto;
  protected java.sql.Date iDataDocContratto;
  protected String iIdCommConvContratto;

  protected String iNumeroDocRicezione;
  protected java.sql.Date iDataDocRicezione;
  protected String iIdCommConvRicezione;

  protected String iNumeroDocFatColl;
  protected java.sql.Date iDataDocFatColl;
  protected String iIdCommConvFatColl;
  //Fix 20785 fine

  protected java.sql.Date iDataEstrazioneStorici;//33950
  protected char iTipoPiano = Commessa.TP_PIANO_NORMALE;
  protected boolean iUtilizzaContoAnticipi;
  
  /**
   *  retrieveList
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if(cInstance == null)
      cInstance = (Commessa)Factory.createObject(Commessa.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  /**
   *  elementWithKey
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static Commessa elementWithKey(String key, int lockType) throws SQLException {
    return(Commessa)PersistentObject.elementWithKey(Commessa.class, key, lockType);
  }

  /**
   * CommessaPO
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public CommessaPO() {
    super(3);
    iValidita = (ValiditaCommessa)Factory.createObject(ValiditaCommessa.class);
    iValidita.setOwner(this);

    // inizio 03463 A.BOULILA
    iCommentiManager = (CommentHandlerManager)Factory.createObject(CommentHandlerManager.class);
    iCommentiManager.setOwner(this);

//    iDatePreviste = (DateRange) Factory.createObject(DateRange.class);
//    iDatePreviste.setOwner(this);

    iAttributiEstendibili = (ExtensibleAttribute)Factory.createObject(ExtensibleAttribute.class);
    iAttributiEstendibili.setOwner(this);
    iAttributiEstendibili.control(); //Fix 10777

    iWfStatus = (WfStatus)Factory.createObject(WfStatus.class);
    iWfStatus.setOwner(this);
    iWfStatus.setType(WfStatus.EXTENDED);

    // Fine 03463 A.BOULILA

  }

  /**
   * Valorizza l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessa(String IdCommessa) {
    this.iIdCommessa = IdCommessa;
    iDescrizione.getHandler().setFatherKeyChanged();
    iCommentiManager.setOwnerKeyChanged();
    setDirty();
    setOnDB(false);
    iRateCommesse.setFatherKeyChanged();
    iDocumentiCollegate.setFatherKeyChanged(); //31437
  }

  /**
   * Restituisce l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessa() {
    return iIdCommessa;
  }

  /**
   * Valorizza l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public void setCliente(ClienteVendita iClienteVendita) {
    this.iCliente.setObject(iClienteVendita);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public ClienteVendita getCliente() {
    return(ClienteVendita)iCliente.getObject();
  }

  /**
   * setClienteKey
   * @param key
   * @return void
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public void setClienteKey(String key) {
    iCliente.setKey(key);
    setDirty();
  }

  /**
   * getClienteKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public String getClienteKey() {
    return iCliente.getKey();
  }

  /**
   * Valorizza l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCliente(String rCliente) {
    String key = iCliente.getKey();
    iCliente.setKey(KeyHelper.replaceTokenObjectKey(key, 2, rCliente));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCliente() {
    String key = iCliente.getKey();
    String objRCliente = KeyHelper.getTokenObjectKey(key, 2);
    return objRCliente;
  }

  ////PJ 24/01/2003 - BEGIN

  /**
   * Restituisce l'attributo.
   */
  public boolean getAggiornamentoSaldi() {
    return iAggiornamentoSaldi;
  }

  /**
   * Valorizza l'attributo.
   */
  public void setAggiornamentoSaldi(boolean aggiornamentoSaldi) {
    iAggiornamentoSaldi = aggiornamentoSaldi;
    setDirty();
  }

  ////PJ 24/01/2003 - END

  /**
   * Restituisce l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public ValiditaCommessa getValidita() {
    return iValidita;
  }

  /**
   * setEqual
   * @param obj
   * @return void
   * @throws CopyException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    CommessaPO commessaPO = (CommessaPO)obj;
    //iAzienda.setEqual(commessaPO.iAzienda);
    iCliente.setEqual(commessaPO.iCliente);

    // Inizio 03463 A.BOULILA

    //iValidita.setEqual(commessaPO.iValidita);

    // Inizio 03463 A.BOULILA
//    iDatePreviste.setEqual(commessaPO.iDatePreviste);
    if(commessaPO.iDataApertura != null)
      iDataApertura = (java.sql.Date)commessaPO.iDataApertura.clone();
    if(commessaPO.iDataChiusura != null)
      iDataChiusura = (java.sql.Date)commessaPO.iDataChiusura.clone();
    if(commessaPO.iDataConferma != null)
      iDataConferma = (java.sql.Date)commessaPO.iDataConferma.clone();
    if(commessaPO.iDataPrimaAtt != null)
      iDataPrimaAtt = (java.sql.Date)commessaPO.iDataPrimaAtt.clone();
    if(commessaPO.iDataUltimAtt != null)
      iDataUltimAtt = (java.sql.Date)commessaPO.iDataUltimAtt.clone();
    if(commessaPO.iDataChiusTec != null)
      iDataChiusTec = (java.sql.Date)commessaPO.iDataChiusTec.clone();
    if(commessaPO.iDataChiusOpe != null)
      iDataChiusOpe = (java.sql.Date)commessaPO.iDataChiusOpe.clone();

    iAmbienteCommessa.setEqual(commessaPO.iAmbienteCommessa);
    iArticolo.setEqual(commessaPO.iArticolo);
    iArticoloVersione.setEqual(commessaPO.iArticoloVersione);
    iCommessaAppartenenza.setEqual(commessaPO.iCommessaAppartenenza);
    iCommessaPrincipale.setEqual(commessaPO.iCommessaPrincipale);
    iCommessaModello.setEqual(commessaPO.iCommessaModello); //Fix 27669
    iConfigurazione.setEqual(commessaPO.iConfigurazione);
    iResponsabileCommessa.setEqual(commessaPO.iResponsabileCommessa);
    iResponsabilePreventivaz.setEqual(commessaPO.iResponsabilePreventivaz);
    iDocumentoMM.setEqual(commessaPO.iDocumentoMM);
    iStabilimento.setEqual(commessaPO.iStabilimento);
    iTipoCommessa.setEqual(commessaPO.iTipoCommessa);
    iUmPrmMag.setEqual(commessaPO.iUmPrmMag);
    iSubnodoWorkflow.setEqual(commessaPO.iSubnodoWorkflow);
    iCommentiManager.setEqual(commessaPO.iCommentiManager);
    iRateCommesse.setEqual(commessaPO.iRateCommesse);
	iDocumentiCollegate.setEqual(commessaPO.iDocumentiCollegate);; //31437
    iAttributiEstendibili.setEqual(commessaPO.iAttributiEstendibili);
    iWfStatus.setEqual(commessaPO.iWfStatus);
    // Fine 03463 A.BOULILA
  }

  /**
   * checkAll
   * @param components
   * @return Vector
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public Vector checkAll(BaseComponentsCollection components) {
    Vector errors = new Vector();
    components.runAllChecks(errors);
    return errors;
  }

  /**
   *  setKey
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public void setKey(String key) {
    String objIdAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAzienda(objIdAzienda);
    String objIdCommessa = KeyHelper.getTokenObjectKey(key, 2);
    setIdCommessa(objIdCommessa);
  }

  /**
   *  getKey
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public String getKey() {
    String idAzienda = getIdAzienda();
    String IdCommessa = getIdCommessa();
    Object[] keyParts = {idAzienda, IdCommessa};
    return KeyHelper.buildObjectKey(keyParts);
  }

  /**
   * isDeletable
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public boolean isDeletable() {
    return checkDelete() == null;
  }

  /**
   *  getTableManager
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    CodeGen     Codice generato da CodeGenerator
   *
   */
  protected TableManager getTableManager() throws SQLException {
    return CommessaTM.getInstance();
  }

  /**
   * setIdAziendaInternal
   * @param idAzienda
   * @return void
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  protected void setIdAziendaInternal(String idAzienda) {
    iAzienda.setKey(idAzienda);
    String key2 = iCliente.getKey();
    iCliente.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));

    // Inizio 03463 A.BOULILA
    String key1 = iAmbienteCommessa.getKey();
    iAmbienteCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
    String key3 = iArticoloVersione.getKey();
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(key3, 1, idAzienda));
    String key4 = iArticolo.getKey();
    iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key4, 1, idAzienda));
    String key5 = iCommessaAppartenenza.getKey();
    iCommessaAppartenenza.setKey(KeyHelper.replaceTokenObjectKey(key5, 1, idAzienda));
    String key6 = iCommessaPrincipale.getKey();
    iCommessaPrincipale.setKey(KeyHelper.replaceTokenObjectKey(key6, 1, idAzienda));
    String key6a = iCommessaModello.getKey(); //Fix 27669 
    iCommessaModello.setKey(KeyHelper.replaceTokenObjectKey(key6a, 1, idAzienda)); //Fix 27669
    String key7 = iConfigurazione.getKey();
    iConfigurazione.setKey(KeyHelper.replaceTokenObjectKey(key7, 1, idAzienda));
    String key8 = iResponsabileCommessa.getKey();
    iResponsabileCommessa.setKey(KeyHelper.replaceTokenObjectKey(key8, 1, idAzienda));
    String key9 = iResponsabilePreventivaz.getKey();
    iResponsabilePreventivaz.setKey(KeyHelper.replaceTokenObjectKey(key9, 1, idAzienda));
    String key10 = iDocumentoMM.getKey();
    iDocumentoMM.setKey(KeyHelper.replaceTokenObjectKey(key10, 1, idAzienda));
    String key11 = iStabilimento.getKey();
    iStabilimento.setKey(KeyHelper.replaceTokenObjectKey(key11, 1, idAzienda));
    String key12 = iTipoCommessa.getKey();
    iTipoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key12, 1, idAzienda));
    String key13 = iUmPrmMag.getKey();
    iUmPrmMag.setKey(KeyHelper.replaceTokenObjectKey(key13, 1, idAzienda));
    String key14 = iOrdineVendita.getKey();
    iOrdineVendita.setKey(KeyHelper.replaceTokenObjectKey(key14, 1, idAzienda));
    String key15 = iOrdineVenditaRiga.getKey();
    iOrdineVenditaRiga.setKey(KeyHelper.replaceTokenObjectKey(key15, 1, idAzienda));

    //Fine 03463 A.BOULILA


    // fix 09402 -inizio
    String keyCommessaCA = iCommessaCA.getKey();
    iCommessaCA.setKey(KeyHelper.replaceTokenObjectKey(keyCommessaCA, 1, idAzienda));
    // fix 09402 -fine

  }

  public void setIdAzienda(String idAzienda) {
    super.setIdAzienda(idAzienda);
    setIdAziendaInternal(idAzienda);

    // Inizio 03463 A.BOULILA
    iCommentiManager.setOwnerKeyChanged();
    iRateCommesse.setFatherKeyChanged();
	iDocumentiCollegate.setFatherKeyChanged(); //31437
  }

  /**
   * setAzienda
   * @param azienda Azienda
   */
  public void setAzienda(Azienda azienda) {
    super.setAzienda(azienda);
    setIdAziendaInternal(azienda.getKey());
    iCommentiManager.setOwnerKeyChanged();
    iRateCommesse.setFatherKeyChanged();
    iDocumentiCollegate.setFatherKeyChanged(); //31437
  }

  /**
   * setAziendaKey
   * @param key String
   */
  public void setAziendaKey(String key) {
    super.setAziendaKey(key);
    setIdAziendaInternal(key);
    iCommentiManager.setOwnerKeyChanged();
    iRateCommesse.setFatherKeyChanged();
    iDocumentiCollegate.setFatherKeyChanged(); //31437    
  }

  // Inizio 03463 A.BOULILA

  /**
   * getDatePreviste
   * @return DateRange
   */
//  public DateRange getDatePreviste(){
//    return iDatePreviste;
//  }

  /**
   * setDatePreviste
   * @return DateRange
   */
//  public void setDatePreviste(DateRange datePreviste){
//    iDatePreviste = datePreviste;
//  }

  /**
   * Valorizza l'attributo.
   * @param statoAvanzamento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setStatoAvanzamento(char statoAvanzamento) {
    this.iStatoAvanzamento = statoAvanzamento;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getStatoAvanzamento() {
    return iStatoAvanzamento;
  }

  /**
   * Valorizza l'attributo.
   * @param qtaUmPrm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setQtaUmPrm(BigDecimal qtaUmPrm) {
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
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQtaUmPrm() {
    return iQtaUmPrm;
  }

  /**
   * getOrdineVendita
   * @return OrdineVendita
   */
  public OrdineVendita getOrdineVendita() {
    return(OrdineVendita)iOrdineVendita.getObject();
  }

  /**
   * setOrdineVendita
   * @param ordineVendita OrdineVendita
   */
  public void setOrdineVendita(OrdineVendita ordineVendita) {
    iOrdineVendita.setObject(ordineVendita);

    String key = iOrdineVendita.getKey();
    String idAnnoOrdine = KeyHelper.getTokenObjectKey(key, 2);
    String idNumeroOrdine = KeyHelper.getTokenObjectKey(key, 3);

    String rigaKey = iOrdineVenditaRiga.getKey();
    rigaKey = KeyHelper.replaceTokenObjectKey(key, 2, idAnnoOrdine);
    rigaKey = KeyHelper.replaceTokenObjectKey(key, 3, idNumeroOrdine);

    iOrdineVenditaRiga.setKey(rigaKey);
    setDirty();
  }

  /**
   * getOrdineVenditaKey
   * @return String
   */
  public String getOrdineVenditaKey() {
    return iOrdineVendita.getKey();
  }

  /**
   * setOrdineVenditaKey
   * @param key String
   */
  public void setOrdineVenditaKey(String key) {
    iOrdineVendita.setKey(key);

    String idAnnoOrdine = KeyHelper.getTokenObjectKey(key, 2);
    String idNumeroOrdine = KeyHelper.getTokenObjectKey(key, 3);

    String rigaKey = iOrdineVenditaRiga.getKey();
    rigaKey = KeyHelper.replaceTokenObjectKey(key, 2, idAnnoOrdine);
    rigaKey = KeyHelper.replaceTokenObjectKey(key, 3, idNumeroOrdine);

    iOrdineVenditaRiga.setKey(rigaKey);

    setDirty();
  }

  /**
   * Valorizza l'attributo.
   * @param idAnnoOrdine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoOrdine(String idAnnoOrdine) {
    String key1 = iOrdineVendita.getKey();
    String key2 = iOrdineVenditaRiga.getKey();
    iOrdineVendita.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, idAnnoOrdine));
    iOrdineVenditaRiga.setKey(KeyHelper.replaceTokenObjectKey(key2, 2, idAnnoOrdine));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAnnoOrdine() {
    return KeyHelper.getTokenObjectKey(iOrdineVendita.getKey(), 2);
  }

  /**
   * Valorizza l'attributo.
   * @param idNumeroOrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNumeroOrdine(String idNumeroOrdine) {
    String key1 = iOrdineVendita.getKey();
    String key2 = iOrdineVenditaRiga.getKey();
    iOrdineVendita.setKey(KeyHelper.replaceTokenObjectKey(key1, 3, idNumeroOrdine));
    iOrdineVenditaRiga.setKey(KeyHelper.replaceTokenObjectKey(key2, 3, idNumeroOrdine));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdNumeroOrdine() {
    return KeyHelper.getTokenObjectKey(iOrdineVendita.getKey(), 3);
  }

  /**
   * OrdineVenditaRiga
   * @return OrdineVenditaRiga
   */
  public OrdineVenditaRiga getOrdineVenditaRiga() {
    return(OrdineVenditaRiga)iOrdineVenditaRiga.getObject();
  }

  /**
   * setOrdineVenditaRiga
   * @param ordineVenditaRiga OrdineVenditaRiga
   */
  public void setOrdineVenditaRiga(OrdineVenditaRiga ordineVenditaRiga) {
    iOrdineVenditaRiga.setObject(ordineVenditaRiga);
    setDirty();
  }

  /**
   * getOrdineVenditaRigaKey
   * @return String
   */
  public String getOrdineVenditaRigaKey() {
    return iOrdineVenditaRiga.getKey();
  }

  /**
   * setOrdineVenditaRigaKey
   * @param key String
   */
  public void setOrdineVenditaRigaKey(String key) {
    iOrdineVenditaRiga.setKey(key);
    setDirty();
  }

  /**
   * Valorizza l'attributo.
   * @param idRigaOrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRigaOrdine(Integer idRigaOrdine) {
    String key = iOrdineVenditaRiga.getKey();
    iOrdineVenditaRiga.setKey(KeyHelper.replaceTokenObjectKey(key, 4, idRigaOrdine));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdRigaOrdine() {
    return KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(iOrdineVenditaRiga.getKey(), 4));
  }

  /**
   * Valorizza l'attributo.
   * @param idDetRigaOrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdDetRigaOrdine(Integer idDetRigaOrdine) {
    //String key = iOrdineVenditaRiga.getKey();
    //iOrdineVenditaRiga.setKey(KeyHelper.replaceTokenObjectKey(key, 5, idDetRigaOrdine));
    iIdDetRigaOrdine = idDetRigaOrdine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdDetRigaOrdine() {
    //return KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(iOrdineVenditaRiga.getKey(), 5));
    return iIdDetRigaOrdine;
  }

  /**
   * Valorizza l'attributo.
   * @param dataConferma
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataConferma(java.sql.Date dataConferma) {
    this.iDataConferma = dataConferma;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataConferma() {
    return iDataConferma;
  }

  /**
   * Valorizza l'attributo.
   * @param dataInizioPrev
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 01/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataInizioPrevista(java.sql.Date dataInizioPrevista) {
    this.iDataInizioPrevista = dataInizioPrevista;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 01/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataInizioPrevista() {
    return iDataInizioPrevista;
  }

  /**
   * Valorizza l'attributo.
   * @param dataFinePrev
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 01/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataFinePrevista(java.sql.Date dataFinePrevista) {
    this.iDataFinePrevista = dataFinePrevista;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 01/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataFinePrevista() {
    return iDataFinePrevista;
  }

  /**
   * Valorizza l'attributo.
   * @param dataApertura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataApertura(java.sql.Date dataApertura) {
    this.iDataApertura = dataApertura;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataApertura() {
    return iDataApertura;
  }

  /**
   * Valorizza l'attributo.
   * @param dataChiusura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataChiusura(java.sql.Date dataChiusura) {
    this.iDataChiusura = dataChiusura;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataChiusura() {
    return iDataChiusura;
  }

  /**
   * Valorizza l'attributo.
   * @param dataPrimaAtt
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataPrimaAtt(java.sql.Date dataPrimaAtt) {
    this.iDataPrimaAtt = dataPrimaAtt;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataPrimaAtt() {
    return iDataPrimaAtt;
  }

  /**
   * Valorizza l'attributo.
   * @param dataUltimAtt
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataUltimAtt(java.sql.Date dataUltimAtt) {
    this.iDataUltimAtt = dataUltimAtt;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataUltimAtt() {
    return iDataUltimAtt;
  }

  /**
   * Valorizza l'attributo.
   * @param dataChiusTec
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataChiusTec(java.sql.Date dataChiusTec) {
    this.iDataChiusTec = dataChiusTec;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataChiusTec() {
    return iDataChiusTec;
  }

  /**
   * Valorizza l'attributo.
   * @param dataChiusOpe
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataChiusOpe(java.sql.Date dataChiusOpe) {
    this.iDataChiusOpe = dataChiusOpe;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataChiusOpe() {
    return iDataChiusOpe;
  }

  /**
   * Valorizza l'attributo.
   * @param pianoFatturazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setPianoFatturazione(char pianoFatturazione) {
    this.iPianoFatturazione = pianoFatturazione;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getPianoFatturazione() {
    return iPianoFatturazione;
  }

  /**
   * Valorizza l'attributo.
   * @param chiudiOrdUltimaFat
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setChiudiOrdUltimaFat(boolean chiudiOrdUltimaFat) {
    this.iChiudiOrdUltimaFat = chiudiOrdUltimaFat;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public boolean getChiudiOrdUltimaFat() {
    return iChiudiOrdUltimaFat;
  }

  /**
   * Valorizza l'attributo.
   * @param note
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setNote(String note) {
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
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getNote() {
    return iNote;
  }

  /**
   * Valorizza l'attributo.
   * @param ambienteCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setAmbienteCommessa(AmbienteCommessa ambienteCommessa) {
    this.iAmbienteCommessa.setObject(ambienteCommessa);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return AmbientiCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public AmbienteCommessa getAmbienteCommessa() {
    return(AmbienteCommessa)iAmbienteCommessa.getObject();
  }

  /**
   * setAmbienteCommessaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setAmbienteCommessaKey(String key) {
    iAmbienteCommessa.setKey(key);
    setDirty();
  }

  /**
   * getAmbienteCommessaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getAmbienteCommessaKey() {
    return iAmbienteCommessa.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param setIdAmbienteCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAmbienteCommessa(String idAmbienteCommessa) {
    String key = iAmbienteCommessa.getKey();
    iAmbienteCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idAmbienteCommessa));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAmbienteCommessa() {
    String key = iAmbienteCommessa.getKey();
    String objIdAmbienteCommessa = KeyHelper.getTokenObjectKey(key, 2);
    return objIdAmbienteCommessa;
  }

  /**
   * Valorizza l'attributo.
   * @param articolo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticolo(Articolo articolo) {
    this.iArticolo.setObject(articolo);
    String versioneKey = iArticoloVersione.getKey();
    String idArticolo = KeyHelper.getTokenObjectKey(articolo.getKey(), 2);
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(versioneKey, 2, idArticolo));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Articolo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Articolo getArticolo() {
    return(Articolo)iArticolo.getObject();
  }

  /**
   * setArticoloKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticoloKey(String key) {
    iArticolo.setKey(key);
    String versioneKey = iArticoloVersione.getKey();
    String idArticolo = KeyHelper.getTokenObjectKey(key, 2);
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(versioneKey, 2, idArticolo));
    setDirty();
  }

  /**
   * getArticoloKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getArticoloKey() {
    return iArticolo.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idArticolo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdArticolo(String idArticolo) {
    String key = iArticolo.getKey();
    iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idArticolo));
    String versioneKey = iArticoloVersione.getKey();
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(versioneKey, 2, idArticolo));
    iConfigurazione.setIdArticolo(idArticolo);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdArticolo() {
    String key = iArticoloVersione.getKey();
    String objIdArticolo = KeyHelper.getTokenObjectKey(key, 2);
    return objIdArticolo;

  }

  /**
   * Valorizza l'attributo.
   * @param articoloVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticoloVersione(ArticoloVersione articoloVersione) {
    this.iArticoloVersione.setObject(articoloVersione);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return ArticoloVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public ArticoloVersione getArticoloVersione() {
    return(ArticoloVersione)iArticoloVersione.getObject();
  }

  /**
   * setArticoloVersioneKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticoloVersioneKey(String key) {
    iArticoloVersione.setKey(key);
    setDirty();
  }

  /**
   * getArticoloVersioneKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getArticoloVersioneKey() {
    return iArticoloVersione.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdVersione(Integer idVersione) {
    String key = iArticoloVersione.getKey();
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idVersione));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdVersione() {
    String key = iArticoloVersione.getKey();
    String objIdVersione = KeyHelper.getTokenObjectKey(key, 3);
    return KeyHelper.stringToIntegerObj(objIdVersione);
  }

  /**
   * Valorizza l'attributo.
   * @param commessaAppartenenza
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaAppartenenza(Commessa commessaAppartenenza) {
    this.iCommessaAppartenenza.setObject(commessaAppartenenza);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Commessa getCommessaAppartenenza() {
    return(Commessa)iCommessaAppartenenza.getObject();
  }

  /**
   * setCommessaAppartenenzaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaAppartenenzaKey(String key) {
    iCommessaAppartenenza.setKey(key);
    setDirty();
  }

  /**
   * getCommessaAppartenenzaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCommessaAppartenenzaKey() {
    return iCommessaAppartenenza.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCommessaAppartenenza
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessaAppartenenza(String idCommessaAppartenenza) {
    String key = iCommessaAppartenenza.getKey();
    iCommessaAppartenenza.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessaAppartenenza));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessaAppartenenza() {
    String key = iCommessaAppartenenza.getKey();
    String objIdCommessaAppartenenza = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessaAppartenenza;
  }

  /**
   * Valorizza l'attributo.
   * @param commessaPrincipale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaPrincipale(Commessa commessaPrincipale) {
    this.iCommessaPrincipale.setObject(commessaPrincipale);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Commessa getCommessaPrincipale() {
    return(Commessa)iCommessaPrincipale.getObject();
  }

  /**
   * setCommessaPrincipaleKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaPrincipaleKey(String key) {
    iCommessaPrincipale.setKey(key);
    setDirty();
  }

  /**
   * getCommessaPrincipaleKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCommessaPrincipaleKey() {
    return iCommessaPrincipale.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCommessaPrincipale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessaPrincipale(String idCommessaPrincipale) {
    String key = iCommessaPrincipale.getKey();
    iCommessaPrincipale.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessaPrincipale));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessaPrincipale() {
    String key = iCommessaPrincipale.getKey();
    String objIdCommessaPrincipale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessaPrincipale;
  }

  //Fix 27669 inizio
  /**
   * Valorizza l'attributo.
   * @param commessaModello
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaModello(Commessa commessaModello) {
    this.iCommessaModello.setObject(commessaModello);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Commessa getCommessaModello() {
    return(Commessa)iCommessaModello.getObject();
  }

  /**
   * setCommessaModelloKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaModelloKey(String key) {
    iCommessaModello.setKey(key);
    setDirty();
  }

  /**
   * getCommessaModelloKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCommessaModelloKey() {
    return iCommessaModello.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCommessaModello
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessaModello(String idCommessaModello) {
    String key = iCommessaModello.getKey();
    iCommessaModello.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessaModello));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessaModello() {
    String key = iCommessaModello.getKey();
    String objIdCommessaModello = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessaModello;
  }
  //Fix 27669 fine
    
  /**
   * Valorizza l'attributo.
   * @param configurazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setConfigurazione(Configurazione configurazione) {
    this.iConfigurazione.setObject(configurazione);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Configurazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Configurazione getConfigurazione() {
    return(Configurazione)iConfigurazione.getObject();
  }

  /**
   * setConfigurazioneKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setConfigurazioneKey(String key) {
    iConfigurazione.setKey(key);
    setDirty();
  }

  /**
   * getConfigurazioneKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getConfigurazioneKey() {
    return iConfigurazione.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idConfigurazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdConfigurazione(Integer idConfigurazione) {
    String key = iConfigurazione.getKey();
    iConfigurazione.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idConfigurazione));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return int
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdConfigurazione() {
    String key = iConfigurazione.getKey();
    Integer objIdConfigurazione = KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(key, 2));
    return objIdConfigurazione;
  }

  /**
   * getIdEsternoConfig
   * @return String
   */
  public String getIdEsternoConfig() {
    return iConfigurazione.getIdEsternoConfig();
  }

  /**
   * setIdEsternoConfig
   * @param idEsternoConfig String
   */
  public void setIdEsternoConfig(String idEsternoConfig) {
    iConfigurazione.setIdEsternoConfig(idEsternoConfig);
    setDirty();
  }

  /**
   * Valorizza l'attributo.
   * @param responsabileCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setResponsabileCommessa(Dipendente responsabileCommessa) {
    this.iResponsabileCommessa.setObject(responsabileCommessa);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Dipendente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Dipendente getResponsabileCommessa() {
    return(Dipendente)iResponsabileCommessa.getObject();
  }

  /**
   * setResponsabileCommessaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setResponsabileCommessaKey(String key) {
    iResponsabileCommessa.setKey(key);
    setDirty();
  }

  /**
   * getResponsabileCommessaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getResponsabileCommessaKey() {
    return iResponsabileCommessa.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idResponsabileCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdResponsabileCommessa(String idResponsabileCommessa) {
    String key = iResponsabileCommessa.getKey();
    iResponsabileCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idResponsabileCommessa));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdResponsabileCommessa() {
    String key = iResponsabileCommessa.getKey();
    String objIdResponsabileCommessa = KeyHelper.getTokenObjectKey(key, 2);
    return objIdResponsabileCommessa;
  }

  /**
   * Valorizza l'attributo.
   * @param responsabilePreventivaz
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setResponsabilePreventivaz(Dipendente responsabilePreventivaz) {
    this.iResponsabilePreventivaz.setObject(responsabilePreventivaz);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Dipendente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Dipendente getResponsabilePreventivaz() {
    return(Dipendente)iResponsabilePreventivaz.getObject();
  }

  /**
   * setResponsabilePreventivazKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setResponsabilePreventivazKey(String key) {
    iResponsabilePreventivaz.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
  }

  /**
   * getResponsabilePreventivazKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getResponsabilePreventivazKey() {
    return iResponsabilePreventivaz.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idResponsabilePreventivaz
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdResponsabilePreventivaz(String idResponsabilePreventivaz) {
    String key = iResponsabilePreventivaz.getKey();
    iResponsabilePreventivaz.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idResponsabilePreventivaz));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdResponsabilePreventivaz() {
    String key = iResponsabilePreventivaz.getKey();
    String objIdResponsabilePreventivaz = KeyHelper.getTokenObjectKey(key, 2);
    return objIdResponsabilePreventivaz;
  }

  /**
   * Valorizza l'attributo.
   * @param documentoMM
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDocumentoMM(DocumentoMM documentoMM) {
    this.iDocumentoMM.setObject(documentoMM);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return DocumentoMM
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public DocumentoMM getDocumentoMM() {
    return(DocumentoMM)iDocumentoMM.getObject();
  }

  /**
   * setDocumentoMMKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDocumentoMMKey(String key) {
    iDocumentoMM.setKey(key);
    setDirty();
  }

  /**
   * getDocumentoMMKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getDocumentoMMKey() {
    return iDocumentoMM.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idDocumentoMM
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdDocumentoMM(String idDocumentoMM) {
    String key = iDocumentoMM.getKey();
    iDocumentoMM.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idDocumentoMM));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdDocumentoMM() {
    String key = iDocumentoMM.getKey();
    String objIdDocumentoMM = KeyHelper.getTokenObjectKey(key, 2);
    return objIdDocumentoMM;
  }

  /**
   * Valorizza l'attributo.
   * @param stabilimento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setStabilimento(Stabilimento stabilimento) {
    this.iStabilimento.setObject(stabilimento);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Stabilimento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public Stabilimento getStabilimento() {
    return(Stabilimento)iStabilimento.getObject();
  }

  /**
   * setStabilimentoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setStabilimentoKey(String key) {
    iStabilimento.setKey(key);
    setDirty();
  }

  /**
   * getStabilimentoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getStabilimentoKey() {
    return iStabilimento.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idStabilimento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdStabilimento(String idStabilimento) {
    String key = iStabilimento.getKey();
    iStabilimento.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idStabilimento));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdStabilimento() {
    String key = iStabilimento.getKey();
    String objIdStabilimento = KeyHelper.getTokenObjectKey(key, 2);
    return objIdStabilimento;
  }

  /**
   * Valorizza l'attributo.
   * @param tipoCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoCommessa(TipoCommessa tipoCommessa) {
    this.iTipoCommessa.setObject(tipoCommessa);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return TipoCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public TipoCommessa getTipoCommessa() {
    return(TipoCommessa)iTipoCommessa.getObject();
  }

  /**
   * setTipoCommessaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoCommessaKey(String key) {
    iTipoCommessa.setKey(key);
    setDirty();
  }

  /**
   * getTipoCommessaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getTipoCommessaKey() {
    return iTipoCommessa.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idTipoCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdTipoCommessa(String idTipoCommessa) {
    String key = iTipoCommessa.getKey();
    iTipoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idTipoCommessa));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdTipoCommessa() {
    String key = iTipoCommessa.getKey();
    String objIdTipoCommessa = KeyHelper.getTokenObjectKey(key, 2);
    return objIdTipoCommessa;
  }

  /**
   * Valorizza l'attributo.
   * @param umPrmMag
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmPrmMag(UnitaMisura umPrmMag) {
    this.iUmPrmMag.setObject(umPrmMag);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return UnitaMisura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public UnitaMisura getUmPrmMag() {
    return(UnitaMisura)iUmPrmMag.getObject();
  }

  /**
   * setUmPrmMagKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmPrmMagKey(String key) {
    iUmPrmMag.setKey(key);
    setDirty();
  }

  /**
   * getUmPrmMagKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getUmPrmMagKey() {
    return iUmPrmMag.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idUmPrmMag
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdUmPrmMag(String idUmPrmMag) {
    String key = iUmPrmMag.getKey();
    iUmPrmMag.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idUmPrmMag));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdUmPrmMag() {
    String key = iUmPrmMag.getKey();
    String objIdUmPrmMag = KeyHelper.getTokenObjectKey(key, 2);
    return objIdUmPrmMag;
  }

  /**
   * Valorizza l'attributo.
   * @param subnodoWorkflow
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setSubnodoWorkflow(WfSpecNode subnodoWorkflow) {
    this.iSubnodoWorkflow.setObject(subnodoWorkflow);
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return WfSpecNode
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public WfSpecNode getSubnodoWorkflow() {
    return(WfSpecNode)iSubnodoWorkflow.getObject();
  }

  /**
   * setSubnodoWorkflowKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setSubnodoWorkflowKey(String key) {
    iSubnodoWorkflow.setKey(key);
    setDirty();
  }

  /**
   * getSubnodoWorkflowKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getSubnodoWorkflowKey() {
    return iSubnodoWorkflow.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idClasseWorkflow
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdClasseWorkflow(short idClasseWorkflow) {
    String key = iSubnodoWorkflow.getKey();
    Short idClasseWorkflowTmp = new Short(idClasseWorkflow);
    iSubnodoWorkflow.setKey(KeyHelper.replaceTokenObjectKey(key, 1, idClasseWorkflowTmp));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return short
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public short getIdClasseWorkflow() {
    String key = iSubnodoWorkflow.getKey();
    String objIdClasseWorkflow = KeyHelper.getTokenObjectKey(key, 1);
    return KeyHelper.stringToShort(objIdClasseWorkflow);

  }

  /**
   * Valorizza l'attributo.
   * @param idWorkflowSpecifico
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdWorkflowSpecifico(String idWorkflowSpecifico) {
    String key = iSubnodoWorkflow.getKey();
    iSubnodoWorkflow.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idWorkflowSpecifico));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdWorkflowSpecifico() {
    String key = iSubnodoWorkflow.getKey();
    String objIdWorkflowSpecifico = KeyHelper.getTokenObjectKey(key, 2);
    return objIdWorkflowSpecifico;

  }

  /**
   * Valorizza l'attributo.
   * @param idNodoWorkflow
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNodoWorkflow(String idNodoWorkflow) {
    String key = iSubnodoWorkflow.getKey();
    iSubnodoWorkflow.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idNodoWorkflow));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdNodoWorkflow() {
    String key = iSubnodoWorkflow.getKey();
    String objIdNodoWorkflow = KeyHelper.getTokenObjectKey(key, 3);
    return objIdNodoWorkflow;

  }

  /**
   * Valorizza l'attributo.
   * @param idSubnodoWorkflow
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdSubnodoWorkflow(String idSubnodoWorkflow) {
    String key = iSubnodoWorkflow.getKey();
    iSubnodoWorkflow.setKey(KeyHelper.replaceTokenObjectKey(key, 4, idSubnodoWorkflow));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdSubnodoWorkflow() {
    String key = iSubnodoWorkflow.getKey();
    String objIdSubnodoWorkflow = KeyHelper.getTokenObjectKey(key, 4);
    return objIdSubnodoWorkflow;
  }

  public WfStatus getWfStatus() {
    return iWfStatus;
  }

  /**
   * toString
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/03/2005    Wizard     Codice generato da Wizard
   *
   */
  public String toString() {
    return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
  }

  /**
   * getCommentiManager
   * @return CommentHandlerManager
   */
  public CommentHandlerManager getCommentHandlerManager() {
    return iCommentiManager;
  }

  /**
   * getCommenti
   * @return CommentHandler
   */
  public CommentHandler getCommenti() {
    return getCommentHandlerManager().getObject();
  }

  /**
   * getCommentiLinks
   * @return List
   */
  public List getCommentiLinks() {
    return getCommenti().getCommentHandlerLinks();
  }

  /**
   * getCommentiId
   * @return String
   */
  public String getCommentiId() {
    return getCommenti().getKey();
  }

  /**
   * getAttributiEstendibili
   * @return ExtensibleAttribute
   */
  public ExtensibleAttribute getAttributiEstendibili() {
    return iAttributiEstendibili;
  }

  /**
   * Override of save method
   * @throws SQLException
   * @return int
   */
  public int save() throws SQLException {
    int rcCommentHandler = iCommentiManager.save();
    if(rcCommentHandler < ErrorCodes.NO_ROWS_UPDATED)
      return rcCommentHandler;

    int rc = super.save();
    if(rc < ErrorCodes.NO_ROWS_UPDATED)
      return rc;

    return rcCommentHandler + rc;
  }

  /**
   * Override of initializeOwnedObjects method
   * @param ret boolean
   * @return boolean
   */
  public boolean initializeOwnedObjects(boolean ret) {
    boolean result = super.initializeOwnedObjects(ret);
    result = iRateCommesse.initialize(result);
    result = iCommentiManager.initialize(result);
    result = iDocumentiCollegate.initialize(result); //31437
//    if (result)
//      iCommentiManager.getObject().getCommentHandlerLinks();

    return result;
  }

  /**
   * Override of saveOwnedObjects method
   * @param rc int
   * @throws SQLException
   * @return int
   */
  public int saveOwnedObjects(int rc) throws SQLException {
    if(rc < 0)
      return rc;

    int rcSuperOwnedObj = super.saveOwnedObjects(rc);
    if(rcSuperOwnedObj < 0)
      return rcSuperOwnedObj;

//    iCommentiManager.setOwnerKeyChanged();
//    int rcCommenti = iCommentiManager.save();
//    if (rcCommenti < 0)
//      return rcCommenti;

    int rcRateCommesse = iRateCommesse.save(rcSuperOwnedObj);
    if(rcRateCommesse < 0)
      return rcRateCommesse;

    //31437 inizio
    int rcDocCollegate = iDocumentiCollegate.save(rcRateCommesse);
    if(rcDocCollegate < 0)
      return rcDocCollegate;    
    //31437 fine
    
    return rc + rcSuperOwnedObj + rcRateCommesse + rcDocCollegate;
  }

  /**
   * getRateCommesseInternal
   * @return OneToMany
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 01/04/2005    Wizard     Codice generato da Wizard
   *
   */
  protected OneToMany getRateCommesseInternal() {
    if(iRateCommesse.isNew())
      iRateCommesse.retrieve();
    return iRateCommesse;
  }

  /**
   * getRateCommesse
   * @return OneToMany
   */
  public OneToMany getRateCommesse() {
    return getRateCommesseInternal();
  }
  //31437 inizio
  protected OneToMany getDocumentiCollegateInternal() {
	  if(iDocumentiCollegate.isNew())
		  iDocumentiCollegate.retrieve();
	  return iDocumentiCollegate;
  }
  
  /**
   * getDocumentiCollegate
   * @return OneToMany
   */
  public OneToMany getDocumentiCollegate() {
	  return getDocumentiCollegateInternal();
  }  
  //31437 fine

  /**
   * Override of deleteOwnedObjects method
   * @throws SQLException
   * @return int
   */
  public int deleteOwnedObjects() throws SQLException {
    int rcSuper = super.deleteOwnedObjects();
    if(rcSuper < 0)
      return rcSuper;

    int rcRateCommesse = iRateCommesse.delete();
    //Mod. 11651 inizio
    if (rcRateCommesse < 0)
      return rcRateCommesse;

    //31437 inizio
    int rcDocumentiCollegate = iDocumentiCollegate.delete();
    if (rcDocumentiCollegate < 0)
      return rcDocumentiCollegate;
    //31437 fine
    //Ripulisco eventuali WfTimer
    int retWS = getWfStatus().delete();
    if (retWS < 0)
      return retWS;

    //Ripulisco eventuali log del workflow
    int retLog = getWfStatus().deleteLogs();
    if (retLog < 0)
      return retLog;

   return retWS + retLog;
   //Mod.11651//return rcSuper + rcRateCommesse;
   //fine mod. 11651
  }

  // Fine 03463 A.BOULILA


  // Inizio XXXXX A.BOULILA

  /**
   *
   * @return Integer
   */
  public Integer getLivelloCommessa() {
    return iLivelloCommessa;
  }

  /**
   *
   * @param livelloCommessa Integer
   */
  public void setLivelloCommessa(Integer livelloCommessa) {
    iLivelloCommessa = livelloCommessa;
    setDirty();
  }

  // Fine 03463 A.BOULILA


  //5543 - inizio
  protected Proxy iCommessaCA = new Proxy(CommessaCA.class);

  public CommessaCA getCommessaCA() {
      return (CommessaCA) iCommessaCA.getObject();
  }

  public void setCommessaCA(CommessaCA commessaCA) {
      iCommessaCA.setObject(commessaCA);
      setDirty();
  }

  public String getCommessaCAKey() {
      return iCommessaCA.getKey();
  }

  public void setCommessaCAKey(String commessaCAKey) {
      iCommessaCA.setKey(commessaCAKey);
      setDirty();
  }

  public String getIdCommessaCA() {
      return KeyHelper.getTokenObjectKey(getCommessaCAKey(), 2);
  }

  public void setIdCommessaCA(String idCommessaCA) {
      setCommessaCAKey(KeyHelper.replaceTokenObjectKey(getCommessaCAKey(), 2, idCommessaCA));
  }

  //5543 - fine

  // Inizio 9625
  public void setValoreOrdRiorg(BigDecimal valoreOrdRiorg){
  	this.iValoreOrdRiorg = valoreOrdRiorg;
  	setDirty();
  }
  public BigDecimal getValoreOrdRiorg(){
  	return iValoreOrdRiorg;
  }
  public void setRifRigaOrdRiorg(String rifRigaOrdRiorg){
  	this.iRifRigaOrdRiorg = rifRigaOrdRiorg;
  	setDirty();
  }
  public String getRifRigaOrdRiorg(){
  	return iRifRigaOrdRiorg;
  }


  // Fine 9625

  //Fix 19897 inizio
  public void setTipoGestioneCigCup(char tipoGestioneCigCup) {
    iTipoGestioneCigCup = tipoGestioneCigCup;
    setDirty();
  }

  public char getTipoGestioneCigCup() {
    return iTipoGestioneCigCup;
  }

  public void setNumeroDocumento(String numeroDocumento) {
    iNumeroDocumento = numeroDocumento;
    setDirty();
  }

  public String getNumeroDocumento() {
    return iNumeroDocumento;
  }

  public void setDataDocumento(java.sql.Date dataDocumento) {
    iDataDocumento = dataDocumento;
    setDirty();
  }

  public java.sql.Date getDataDocumento() {
    return iDataDocumento;
  }

  public void setNumeroItem(String numeroItem) {
    iNumeroItem = numeroItem;
    setDirty();
  }

  public String getNumeroItem() {
    return iNumeroItem;
  }

  public void setIdCommConven(String idCommConven) {
    iIdCommConven = idCommConven;
    setDirty();
  }

  public String getIdCommConven() {
    return iIdCommConven;
  }

  public void setCodiceCUP(String codiceCUP) {
    iCodiceCUP = codiceCUP;
    setDirty();
  }

  public String getCodiceCUP() {
    return iCodiceCUP;
  }

  public void setCodiceCIG(String codiceCIG) {
    iCodiceCIG = codiceCIG;
    setDirty();
  }

  public String getCodiceCIG() {
    return iCodiceCIG;
  }
  //Fix 19897 fine

  //Fix 20785 inizio
  public void setNumeroDocOrdAcq(String numeroDocOrdAcq) {
    iNumeroDocOrdAcq = numeroDocOrdAcq;
    setDirty();
  }

  public String getNumeroDocOrdAcq() {
    return iNumeroDocOrdAcq;
  }

  public void setDataDocOrdAcq(java.sql.Date dataDocOrdAcq) {
    iDataDocOrdAcq = dataDocOrdAcq;
    setDirty();
  }

  public java.sql.Date getDataDocOrdAcq() {
    return iDataDocOrdAcq;
  }

  public void setIdCommConvOrdAcq(String idCommConvOrdAcq) {
    iIdCommConvOrdAcq = idCommConvOrdAcq;
    setDirty();
  }

  public String getIdCommConvOrdAcq() {
    return iIdCommConvOrdAcq;
  }

  public void setNumeroDocContratto(String numeroDocContratto) {
    iNumeroDocContratto = numeroDocContratto;
    setDirty();
  }

  public String getNumeroDocContratto() {
    return iNumeroDocContratto;
  }

  public void setDataDocContratto(java.sql.Date dataDocContratto) {
    iDataDocContratto = dataDocContratto;
    setDirty();
  }

  public java.sql.Date getDataDocContratto() {
    return iDataDocContratto;
  }

  public void setIdCommConvContratto(String idCommConvContratto) {
    iIdCommConvContratto = idCommConvContratto;
    setDirty();
  }

  public String getIdCommConvContratto() {
    return iIdCommConvContratto;
  }

  public void setNumeroDocRicezione(String numeroDocRicezione) {
    iNumeroDocRicezione = numeroDocRicezione;
    setDirty();
  }

  public String getNumeroDocRicezione() {
    return iNumeroDocRicezione;
  }

  public void setDataDocRicezione(java.sql.Date dataDocRicezione) {
    iDataDocRicezione = dataDocRicezione;
    setDirty();
  }

  public java.sql.Date getDataDocRicezione() {
    return iDataDocRicezione;
  }

  public void setIdCommConvRicezione(String idCommConvRicezione) {
    iIdCommConvRicezione = idCommConvRicezione;
    setDirty();
  }

  public String getIdCommConvRicezione() {
    return iIdCommConvRicezione;
  }

  public void setNumeroDocFatColl(String numeroDocFatColl) {
    iNumeroDocFatColl = numeroDocFatColl;
    setDirty();
  }

  public String getNumeroDocFatColl() {
    return iNumeroDocFatColl;
  }

  public void setDataDocFatColl(java.sql.Date dataDocFatColl) {
    iDataDocFatColl = dataDocFatColl;
    setDirty();
  }

  public java.sql.Date getDataDocFatColl() {
    return iDataDocFatColl;
  }

  public void setIdCommConvFatColl(String idCommConvFatColl) {
    iIdCommConvFatColl = idCommConvFatColl;
    setDirty();
  }

  public String getIdCommConvFatColl() {
    return iIdCommConvFatColl;
  }
  //Fix 20785 fine


  public void setTipoPiano(char tipoPiano) {
	  iTipoPiano = tipoPiano;
	  setDirty();
  }

  public char getTipoPiano() {
	  return iTipoPiano;
  }


  public void setUtilizzaContoAnticipi(boolean utilizzaContoAnticipi) {
	  iUtilizzaContoAnticipi = utilizzaContoAnticipi;
	  setDirty();
  }

  public boolean isUtilizzaContoAnticipi() {
	  return iUtilizzaContoAnticipi;
  }

  //Fix 32044 Inizio
	public int savePO() throws SQLException {
		return super.save();
	}
  //Fix 32044 Fine
  
  //33950 inizio
  public void setDataEstrazioneStorici(java.sql.Date dataEstrazioneStorici) {
	  iDataEstrazioneStorici = dataEstrazioneStorici;
	  setDirty();
  }

  public java.sql.Date getDataEstrazioneStorici() {
	  return iDataEstrazioneStorici;
  }
  //33950 fine

}

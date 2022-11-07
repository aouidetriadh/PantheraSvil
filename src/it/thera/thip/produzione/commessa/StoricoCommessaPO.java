/*
 * @(#)StoricoCommessaPO.java
 */

/**
 * StoricoCommessa
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Mekki 12/04/2005 at 15:21:11
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 12/04/2005    Wizard     Codice generato da Wizard
 * 10416   03/02/2009    DBot      Modifica per consuntivazione documenti servizio
 * 31460   01/07/2020    RA		   Aggiunto nuovo attributi ProvenienzaCosto, StoricoNonCommessa
 * 31513   21/12/2020	 RA		   Aggiunto default valore per l'attributo iValorizzaCosto
 * 33950   17/08/2021	 RA		   Aggiunto OneToMany StoricoCommessaDet
 */
package it.thera.thip.produzione.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;
import it.thera.thip.acquisti.generaleAC.*;
import it.thera.thip.base.articolo.*;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.cliente.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.dipendente.*;
import it.thera.thip.base.fornitore.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.base.interfca.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.configuratore.*;
import it.thera.thip.datiTecnici.costi.*;
import it.thera.thip.datiTecnici.modpro.*;
import it.thera.thip.magazzino.documenti.*;
import it.thera.thip.magazzino.movimenti.*;
import it.thera.thip.produzione.documento.*;
import it.thera.thip.servizi.documento.CausaleDocServizio;
import it.thera.thip.vendite.generaleVE.*;

public abstract class StoricoCommessaPO extends PersistentObjectDC implements BusinessObject, Authorizable, Deletable, ConflictableWithKey {

  //Documento Origine
  public static final char RICHIESTA = '0';
  public static final char ORDINE = '1';
  public static final char DOCUMENTO = '2';

  //Tipo Riga Origine
  public static final char ACQUISTO = '0';
  public static final char LAVORAZIONE_ESTERNA_MATERIALE = '1';
  public static final char LAVORAZIONE_ESTERNA_PRODOTTO = '2';
  public static final char PRODUZIONE_MATERIALE = '3';
  public static final char PRODUZIONE_PRODOTTO = '4';
  public static final char PRODUZIONE_RISORSA = '5';
  public static final char VENDITA = '6';
  public static final char GENERICO = '7';
  public static final char TRASFERIMENTO_MAGAZZINO = '8';
  public static final char SERVIZIO_MATERIALE = 'A';
  public static final char SERVIZIO_PRODOTTO = 'B';
  public static final char SERVIZIO_RISORSA = 'C';
  public static final char SERVIZIO_SPESA = 'D';

  //Azione magazzino
  public static final char NESSUNA = 'N';
  public static final char ENTRATA = 'E';
  public static final char USCITA = 'U';

  //Valorizza Costo
  public static final char NO = 'N';
  public static final char INCREMENTA_COSTO = '+';
  public static final char DECREMENTA_COSTO = '-';
  public static final char INCREMENTA_COSTI_INDIRETTI = 'I';

  //TipoRilevazioneRsr
  public static final char A_TEMPO = '0';
  public static final char A_QUANTITA = '1';
  public static final char A_COSTO = '2';
  
  //31460 inzio
  //ProvenienzaCosto
  public static final char PROV_COSTO_DEFAULT = '-';
  public static final char PROV_COSTO_DOCUMENTO = '0';
  public static final char PROV_COSTO_AMBIENTE_COMMESSA = '1';
  public static final char PROV_COSTO_AMBIENTE_MANCANTI = '2';
  public static final char PROV_COSTO_TIPO_COSTO = '3';
  //31460 fine

  /**
   *  instance
   */
  private static StoricoCommessa cInstance;

  /**
   * Attributo iIdProgressivo
   */
  protected Integer iIdProgressivo;

  /**
   * Attributo iDocumentoOrigine
   */
  protected char iDocumentoOrigine = RICHIESTA;

  /**
   * Attributo iTipoRigaOrigine
   */
  protected char iTipoRigaOrigine = ACQUISTO;

  /**
   * Attributo iIdAnnoOrigine
   */
  protected String iIdAnnoOrigine;

  /**
   * Attributo iIdNumeroOrigine
   */
  protected String iIdNumeroOrigine;

  /**
   * Attributo iIdRigaOrigine
   */
  protected Integer iIdRigaOrigine;

  /**
   * Attributo iIdDetRigaOrigine
   */
  protected Integer iIdDetRigaOrigine;

  /**
   * Attributo iNumeroOrgFormattato
   */
  protected String iNumeroOrgFormattato;

  /**
   * Attributo iDataOrigine
   */
  protected java.sql.Date iDataOrigine;

  /**
   * Attributo iIdCauOrgTes
   */
  protected String iIdCauOrgTes;

  /**
   * Attributo iIdCauOrgRig
   */
  protected String iIdCauOrgRig;

  /**
   * Attributo iAvanzamento
   */
  protected boolean iAvanzamento = false;

  /**
   * Attributo iAzioneMagazzino
   */
  protected char iAzioneMagazzino = NESSUNA;
  
  protected char iProvenienzaCosto = PROV_COSTO_DEFAULT;//31460
  protected boolean iStoricoNonCommessa = false;//31460
  /**
   * Attributo iDescrizioneArticolo
   */
  protected String iDescrizioneArticolo;

  /**
   * Attributo iQuantitaUMPrm
   */
  protected BigDecimal iQuantitaUMPrm;

  /**
   * Attributo iQuantitaUMSec
   */
  protected BigDecimal iQuantitaUMSec;

  /**
   * Attributo iQuantitaUMAcqVen
   */
  protected BigDecimal iQuantitaUMAcqVen;

  /**
   * Attributo iQtaScarto
   */
  protected BigDecimal iQtaScarto;

  /**
   * Attributo iTempo
   */
  protected BigDecimal iTempo;

  /**
   * Attributo iCostoUnitario
   */
  protected BigDecimal iCostoUnitario;

  /**
   * Attributo iCostoTotale
   */
  protected BigDecimal iCostoTotale;

  /**
   * Attributo iValorizzaCosto
   */
  //protected char iValorizzaCosto;//31513
  protected char iValorizzaCosto = StoricoCommessa.NO;//31513

  /**
   * Attributo iGesSaldiCommessa
   */
  protected boolean iGesSaldiCommessa = false;

  /**
   * Attributo iNoFatturare
   */
  protected boolean iNoFatturare = false;

  /**
   * Attributo iIdAnnoOrdine
   */
  protected String iIdAnnoOrdine;

  /**
   * Attributo iIdNumeroOrdine
   */
  protected String iIdNumeroOrdine;

  /**
   * Attributo iIdRigaOrdine
   */
  protected Integer iIdRigaOrdine;

  /**
   * Attributo iIdDetRigaOrdine
   */
  protected Integer iIdDetRigaOrdine;

  /**
   * Attributo iDataOrdine
   */
  protected java.sql.Date iDataOrdine;

  /**
   * Attributo iIdAnnoBolla
   */
  protected String iIdAnnoBolla;

  /**
   * Attributo iIdNumeroBolla
   */
  protected String iIdNumeroBolla;

  /**
   * Attributo iDataBolla
   */
  protected java.sql.Date iDataBolla;

  /**
   * Attributo iIdAnnoFattura
   */
  protected String iIdAnnoFattura;

  /**
   * Attributo iIdNumeroFattura
   */
  protected String iIdNumeroFattura;

  /**
   * Attributo iDataFattura
   */
  protected java.sql.Date iDataFattura;

  /**
   * Attributo iTipoArticolo
   */
  protected char iTipoArticolo = ArticoloBase.COD_NORMALE;

  /**
   * Attributo iTipoParte
   */
  protected char iTipoParte = ArticoloDatiIdent.NON_SIGNIFICATIVO;

  /**
   * Attributo iCodConfig
   */
  protected String iCodConfig;

  /**
   * Attributo iTipoRilevazioneRsr
   */
  protected char iTipoRilevazioneRsr = A_TEMPO;

  /**
   * Attributo iCostoUnitarioOrigine
   */
  protected BigDecimal iCostoUnitarioOrigine;

  /**
   * Attributo iValoreRiga
   */
  protected BigDecimal iValoreRiga;


  /**
  * Attributo iIdOperazione
  */
 protected String iIdOperazione; //Mod XXXXX

  /**
   * Attributo iLivelloCommessa
   */
  protected Integer iLivelloCommessa = new Integer(0);


  /**
   * Attributo iAzienda
   */
  protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);

  /**
   * Attributo iArticoloVersione
   */
  protected Proxy iArticoloVersione = new Proxy(it.thera.thip.base.articolo.ArticoloVersione.class);

  /**
   * Attributo iArticoloVersionePrd
   */
  protected Proxy iArticoloVersionePrd = new Proxy(it.thera.thip.base.articolo.ArticoloVersione.class);

  /**
   * Attributo iAttivita
   */
  protected Proxy iAttivita = new Proxy(it.thera.thip.datiTecnici.modpro.Attivita.class);

  /**
   * Attributo iCausaleMovMagazzino
   */
  protected Proxy iCausaleMovMagazzino = new Proxy(it.thera.thip.magazzino.movimenti.CausaleMovMagazzino.class);

  /**
   * Attributo iCentroCosto
   */
  protected Proxy iCentroCosto = new Proxy(it.thera.thip.base.interfca.CentroCostoCA.class);

  /**
   * Attributo iCentroLavoro
   */
  protected Proxy iCentroLavoro = new Proxy(it.thera.thip.base.azienda.CentroLavoro.class);

  /**
   * Attributo iClasseMerceologica
   */
  protected Proxy iClasseMerceologica = new Proxy(it.thera.thip.base.articolo.ClasseMerceologica.class);

  /**
   * Attributo iCliente
   */
  protected Proxy iCliente = new Proxy(it.thera.thip.base.cliente.ClienteVendita.class);

  /**
   * Attributo iClasseMateriale
   */
  protected Proxy iClasseMateriale = new Proxy(it.thera.thip.base.articolo.ClasseMateriale.class);

  /**
   * Attributo iCommessa
   */
  protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iCommessaApp
   */
  protected Proxy iCommessaApp = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iCommessaCol
   */
  protected Proxy iCommessaCol = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iCommessaPrm
   */
  protected Proxy iCommessaPrm = new Proxy(it.thera.thip.base.commessa.Commessa.class);

  /**
   * Attributo iComponenteCosto
   */
  protected Proxy iComponenteCosto = new Proxy(it.thera.thip.datiTecnici.costi.ComponenteCosto.class);

  /**
   * Attributo iConfigurazione
   */
  protected ConfigurazioneProxyEnh iConfigurazione = new ConfigurazioneProxyEnh(Configurazione.class);

  /**
   * Attributo iConfigurazionePrd
   */
  protected ConfigurazioneProxyEnh iConfigurazionePrd = new ConfigurazioneProxyEnh(Configurazione.class);

  /**
   * Attributo iDipendente
   */
  protected Proxy iDipendente = new Proxy(it.thera.thip.base.dipendente.Dipendente.class);

  /**
   * Attributo iFornitore
   */
  protected Proxy iFornitore = new Proxy(it.thera.thip.base.fornitore.Fornitore.class);

  /**
   * Attributo iGruppoProdotto
   */
  protected Proxy iGruppoProdotto = new Proxy(it.thera.thip.base.articolo.GruppoProdotto.class);

  /**
   * Attributo iMagazzino
   */
  protected Proxy iMagazzino = new Proxy(it.thera.thip.base.azienda.Magazzino.class);

  /**
   * Attributo iPianificatore
   */
  protected Proxy iPianificatore = new Proxy(it.thera.thip.base.azienda.Pianificatore.class);

  /**
   * Attributo iReparto
   */
  protected Proxy iReparto = new Proxy(it.thera.thip.base.azienda.Reparto.class);

  /**
   * Attributo iRisorsa
   */
  protected Proxy iRisorsa = new Proxy(it.thera.thip.base.risorse.Risorsa.class);

  /**
   * Attributo iSchemaCosto
   */
  protected Proxy iSchemaCosto = new Proxy(it.thera.thip.datiTecnici.costi.SchemaCosto.class);

  /**
   * Attributo iStabilimento
   */
  protected Proxy iStabilimento = new Proxy(it.thera.thip.base.azienda.Stabilimento.class);

  /**
   * Attributo iUmAcqVen
   */
  protected Proxy iUmAcqVen = new Proxy(it.thera.thip.base.generale.UnitaMisura.class);

  /**
   * Attributo iUmPrmMag
   */
  protected Proxy iUmPrmMag = new Proxy(it.thera.thip.base.generale.UnitaMisura.class);

  /**
   * Attributo iUmSecMag
   */
  protected Proxy iUmSecMag = new Proxy(it.thera.thip.base.generale.UnitaMisura.class);

  /**
   * Attributo iArticolo added by Rachida the 04/15/2005
   */
  protected Proxy iArticolo = new Proxy(it.thera.thip.base.articolo.Articolo.class);

  /**
   * Attributo iCausaleTestata added by Rachida the 04/18/2005
   */
  protected Proxy iCausaleTestataAcq = new Proxy(it.thera.thip.acquisti.generaleAC.CausaleDocumentoTestataAcq.class);
  protected Proxy iCausaleTestataVen = new Proxy(it.thera.thip.vendite.generaleVE.CausaleDocumentoVendita.class);
  protected Proxy iCausaleTestataPrd = new Proxy(it.thera.thip.produzione.documento.CausaleDocProduzione.class);
  protected Proxy iCausaleTestataGen = new Proxy(it.thera.thip.magazzino.documenti.CausaleDocumentoGen.class);
  protected Proxy iCausaleTestataTrasMag = new Proxy(it.thera.thip.magazzino.documenti.CausaleDocumentoTrasf.class);
  protected Proxy iCausaleTestataSrv = new Proxy(it.thera.thip.servizi.documento.CausaleDocServizio.class); //Fix 10416

  /**
   * Attributo iCausaleRiga added by Rachida the 04/18/2005
   */
  protected Proxy iCausaleRigaAcq = new Proxy(it.thera.thip.acquisti.generaleAC.CausaleDocumentoRigaAcq.class);
  protected Proxy iCausaleRigaVen = new Proxy(it.thera.thip.vendite.generaleVE.CausaleRigaDocVen.class);
  protected Proxy iCausaleRigaPrdMat = new Proxy(it.thera.thip.magazzino.movimenti.CausaleMovPrelPrd.class);
  protected Proxy iCausaleRigaPrdVrs = new Proxy(it.thera.thip.magazzino.movimenti.CausaleMovVersPrd.class);
  protected Proxy iCausaleRigaPrdRsr = new Proxy(it.thera.thip.produzione.documento.CausaleUtilizzoRisorse.class);
  protected Proxy iCausaleRigaGen = new Proxy(it.thera.thip.magazzino.documenti.CausaleRigaDocGen.class);
  protected Proxy iCausaleRigaTrasMag = new Proxy(it.thera.thip.magazzino.documenti.CausaleRigaDocTrasf.class);
  //Fix 10416 inizio
  protected Proxy iCausaleRigaSrvMat = new Proxy(it.thera.thip.magazzino.movimenti.CausaleMovPrelSrv.class);
  protected Proxy iCausaleRigaSrvVrs = new Proxy(it.thera.thip.magazzino.movimenti.CausaleMovVersSrv.class);
  protected Proxy iCausaleRigaSrvRsr = new Proxy(it.thera.thip.produzione.documento.CausaleUtilizzoRisorse.class);
  //Fix 10416 fine
  
  protected OneToMany iStoricoCommessaDet = new OneToMany(it.thera.thip.produzione.commessa.StoricoCommessaDet.class, this, 3, false);//33950

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
   * 12/04/2005    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    if (cInstance == null)
      cInstance = (StoricoCommessa) Factory.createObject(StoricoCommessa.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  /**
   *  elementWithKey
   * @param key
   * @param lockType
   * @return StoricoCommessa
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static StoricoCommessa elementWithKey(String key, int lockType) throws SQLException {
    return (StoricoCommessa) PersistentObject.elementWithKey(StoricoCommessa.class, key, lockType);
  }

  /**
   * StoricoCommessaPO
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public StoricoCommessaPO() {
    super();
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  /**
   * Restituisce l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   */
  public Azienda getAzienda() {
    return (Azienda) iAzienda.getObject();
  }

  /**
   * getAziendaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   */
  public String getAziendaKey() {
    return iAzienda.getKey();
  }

  /**
   * Valorizza l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   */
  public void setAzienda(Azienda aziende) {
    this.iAzienda.setObject(aziende);
    setIdAziendaInternal(aziende.getKey());
    setDirty();
    setOnDB(false);
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * setAziendaKey
   * @param key
   * @return void
   */
  /*
   * Revisions:
   * Date          Owner      Description
   */
  public void setAziendaKey(String key) {
    iAzienda.setKey(key);
    setIdAziendaInternal(key);
    setDirty();
    setOnDB(false);
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   */
  public String getIdAzienda() {
    String key = iAzienda.getKey();
    return key;
  }

  /**
   * Valorizza l'attributo.
   */
  /*
   * Revisions:
   * Date          Owner      Description
   */
  public void setIdAzienda(String idAzienda) {
    iAzienda.setKey(idAzienda);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Valorizza l'attributo.
   * @param idProgressivo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdProgressivo(Integer idProgressivo) {
    this.iIdProgressivo = idProgressivo;
    setDirty();
    setOnDB(false);
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdProgressivo() {
    return iIdProgressivo;
  }

  /**
   * Valorizza l'attributo.
   * @param documentoOrigine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDocumentoOrigine(char documentoOrigine) {
    this.iDocumentoOrigine = documentoOrigine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getDocumentoOrigine() {
    return iDocumentoOrigine;
  }

  /**
   * Valorizza l'attributo.
   * @param tipoRigaOrigine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoRigaOrigine(char tipoRigaOrigine) {
    this.iTipoRigaOrigine = tipoRigaOrigine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getTipoRigaOrigine() {
    return iTipoRigaOrigine;
  }

  /**
   * Valorizza l'attributo.
   * @param idAnnoOrigine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoOrigine(String idAnnoOrigine) {
    this.iIdAnnoOrigine = idAnnoOrigine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAnnoOrigine() {
    return iIdAnnoOrigine;
  }

  /**
   * Valorizza l'attributo.
   * @param idNumeroOrigine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNumeroOrigine(String idNumeroOrigine) {
    this.iIdNumeroOrigine = idNumeroOrigine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdNumeroOrigine() {
    return iIdNumeroOrigine;
  }

  /**
   * Valorizza l'attributo.
   * @param idRigaOrigine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRigaOrigine(Integer idRigaOrigine) {
    this.iIdRigaOrigine = idRigaOrigine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdRigaOrigine() {
    return iIdRigaOrigine;
  }

  /**
   * Valorizza l'attributo.
   * @param idDetRigaOrigine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdDetRigaOrigine(Integer idDetRigaOrigine) {
    this.iIdDetRigaOrigine = idDetRigaOrigine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdDetRigaOrigine() {
    return iIdDetRigaOrigine;
  }

  /**
   * Valorizza l'attributo.
   * @param numeroOrgFormattato
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setNumeroOrgFormattato(String numeroOrgFormattato) {
    this.iNumeroOrgFormattato = numeroOrgFormattato;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getNumeroOrgFormattato() {
    return iNumeroOrgFormattato;
  }

  /**
   * Valorizza l'attributo.
   * @param dataOrigine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataOrigine(java.sql.Date dataOrigine) {
    this.iDataOrigine = dataOrigine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataOrigine() {
    return iDataOrigine;
  }

  /**
   * Valorizza l'attributo.
   * @param idCauOrgTes
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCauOrgTes(String idCausale) {
    this.iIdCauOrgTes = idCausale;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCauOrgTes() {
    return iIdCauOrgTes;
  }

  /**
   * Valorizza l'attributo.
   * @param idCauOrgRig
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCauOrgRig(String idCausale) {
    this.iIdCauOrgRig = idCausale;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCauOrgRig() {
    return iIdCauOrgRig;
  }

  /**
   * Valorizza l'attributo.
   * @param avanzamento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setAvanzamento(boolean avanzamento) {
    this.iAvanzamento = avanzamento;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public boolean getAvanzamento() {
    return iAvanzamento;
  }

  /**
   * Valorizza l'attributo.
   * @param azioneMagazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setAzioneMagazzino(char azioneMagazzino) {
    this.iAzioneMagazzino = azioneMagazzino;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getAzioneMagazzino() {
    return iAzioneMagazzino;
  }
  
  //31460 inizio
  public void setProvenienzaCosto(char provenienzaCosto) {
	  this.iProvenienzaCosto = provenienzaCosto;
	  setDirty();
  }

  public char getProvenienzaCosto() {
	  return iProvenienzaCosto;
  }
  
  public void setStoricoNonCommessa(boolean storicoNonCommessa) {
	  this.iStoricoNonCommessa = storicoNonCommessa;
	  setDirty();
  }

  public boolean getStoricoNonCommessa() {
	  return iStoricoNonCommessa;
  }
  //31460 fine

  /**
   * Valorizza l'attributo.
   * @param descrizioneArticolo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDescrizioneArticolo(String descrizioneArticolo) {
    this.iDescrizioneArticolo = descrizioneArticolo;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getDescrizioneArticolo() {
    return iDescrizioneArticolo;
  }

  /**
   * Valorizza l'attributo.
   * @param quantitaUMPrm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setQuantitaUMPrm(BigDecimal quantitaUMPrm) {
    this.iQuantitaUMPrm = quantitaUMPrm;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQuantitaUMPrm() {
    return iQuantitaUMPrm;
  }

  /**
   * Valorizza l'attributo.
   * @param quantitaUMSec
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setQuantitaUMSec(BigDecimal quantitaUMSec) {
    this.iQuantitaUMSec = quantitaUMSec;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQuantitaUMSec() {
    return iQuantitaUMSec;
  }

  /**
   * Valorizza l'attributo.
   * @param quantitaUMAcqVen
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setQuantitaUMAcqVen(BigDecimal quantitaUMAcqVen) {
    this.iQuantitaUMAcqVen = quantitaUMAcqVen;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQuantitaUMAcqVen() {
    return iQuantitaUMAcqVen;
  }

  /**
   * Valorizza l'attributo.
   * @param qtaScarto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setQtaScarto(BigDecimal qtaScarto) {
    this.iQtaScarto = qtaScarto;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getQtaScarto() {
    return iQtaScarto;
  }

  /**
   * Valorizza l'attributo.
   * @param tempo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setTempo(BigDecimal tempo) {
    this.iTempo = tempo;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getTempo() {
    return iTempo;
  }

  /**
   * Valorizza l'attributo.
   * @param costoUnitario
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCostoUnitario(BigDecimal costoUnitario) {
    this.iCostoUnitario = costoUnitario;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getCostoUnitario() {
    return iCostoUnitario;
  }

  /**
   * Valorizza l'attributo.
   * @param costoTotale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCostoTotale(BigDecimal costoTotale) {
    this.iCostoTotale = costoTotale;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return BigDecimal
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public BigDecimal getCostoTotale() {
    return iCostoTotale;
  }

  /**
   * Valorizza l'attributo.
   * @param valorizzaCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setValorizzaCosto(char valorizzaCosto) {
    this.iValorizzaCosto = valorizzaCosto;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getValorizzaCosto() {
    return iValorizzaCosto;
  }

  /**
   * Valorizza l'attributo.
   * @param gesSaldiCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setGesSaldiCommessa(boolean gesSaldiCommessa) {
    this.iGesSaldiCommessa = gesSaldiCommessa;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public boolean getGesSaldiCommessa() {
    return iGesSaldiCommessa;
  }

  /**
   * Valorizza l'attributo.
   * @param noFatturare
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setNoFatturare(boolean noFatturare) {
    this.iNoFatturare = noFatturare;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public boolean getNoFatturare() {
    return iNoFatturare;
  }

  /**
   * Valorizza l'attributo.
   * @param idAnnoOrdine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoOrdine(String idAnnoOrdine) {
    this.iIdAnnoOrdine = idAnnoOrdine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAnnoOrdine() {
    return iIdAnnoOrdine;
  }

  /**
   * Valorizza l'attributo.
   * @param idNumeroOrdine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNumeroOrdine(String idNumeroOrdine) {
    this.iIdNumeroOrdine = idNumeroOrdine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdNumeroOrdine() {
    return iIdNumeroOrdine;
  }

  /**
   * Valorizza l'attributo.
   * @param idRigaOrdine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRigaOrdine(Integer idRigaOrdine) {
    this.iIdRigaOrdine = idRigaOrdine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdRigaOrdine() {
    return iIdRigaOrdine;
  }

  /**
   * Valorizza l'attributo.
   * @param idDetRigaOrdine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdDetRigaOrdine(Integer idDetRigaOrdine) {
    this.iIdDetRigaOrdine = idDetRigaOrdine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdDetRigaOrdine() {
    return iIdDetRigaOrdine;
  }

  /**
   * Valorizza l'attributo.
   * @param dataOrdine
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataOrdine(java.sql.Date dataOrdine) {
    this.iDataOrdine = dataOrdine;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataOrdine() {
    return iDataOrdine;
  }

  /**
   * Valorizza l'attributo.
   * @param idAnnoBolla
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoBolla(String idAnnoBolla) {
    this.iIdAnnoBolla = idAnnoBolla;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAnnoBolla() {
    return iIdAnnoBolla;
  }

  /**
   * Valorizza l'attributo.
   * @param idNumeroBolla
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNumeroBolla(String idNumeroBolla) {
    this.iIdNumeroBolla = idNumeroBolla;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdNumeroBolla() {
    return iIdNumeroBolla;
  }

  /**
   * Valorizza l'attributo.
   * @param dataBolla
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataBolla(java.sql.Date dataBolla) {
    this.iDataBolla = dataBolla;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataBolla() {
    return iDataBolla;
  }

  /**
   * Valorizza l'attributo.
   * @param idAnnoFattura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoFattura(String idAnnoFattura) {
    this.iIdAnnoFattura = idAnnoFattura;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAnnoFattura() {
    return iIdAnnoFattura;
  }

  /**
   * Valorizza l'attributo.
   * @param idNumeroFattura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdNumeroFattura(String idNumeroFattura) {
    this.iIdNumeroFattura = idNumeroFattura;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdNumeroFattura() {
    return iIdNumeroFattura;
  }

  /**
   * Valorizza l'attributo.
   * @param dataFattura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDataFattura(java.sql.Date dataFattura) {
    this.iDataFattura = dataFattura;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getDataFattura() {
    return iDataFattura;
  }

  /**
   * Valorizza l'attributo.
   * @param tipoArticolo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoArticolo(char tipoArticolo) {
    this.iTipoArticolo = tipoArticolo;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getTipoArticolo() {
    return iTipoArticolo;
  }

  /**
   * Valorizza l'attributo.
   * @param tipoParte
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoParte(char tipoParte) {
    this.iTipoParte = tipoParte;
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getTipoParte() {
    return iTipoParte;
  }

  /**
   * Valorizza l'attributo.
   * @param articoloVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticoloVersione(ArticoloVersione articoloVersione) {
    this.iArticoloVersione.setObject(articoloVersione);
    setOnDB(false);
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return ArticoloVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ArticoloVersione getArticoloVersione() {
    return (ArticoloVersione) iArticoloVersione.getObject();
  }

  /**
   * setArticoloVersioneKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticoloVersioneKey(String key) {
    iArticoloVersione.setKey(key);
    setOnDB(false);
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getArticoloVersioneKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getArticoloVersioneKey() {
    return iArticoloVersione.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idArticolo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  /*public void setIdArticolo(String idArticolo) {
    String key = iArticoloVersione.getKey();
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idArticolo));
    setDirty();
     }*/

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  /*public String getIdArticolo() {
    String key = iArticoloVersione.getKey();
    String objIdArticolo = KeyHelper.getTokenObjectKey(key,2);
    return objIdArticolo;

     }*/

  /**
   * Valorizza l'attributo.
   * @param idVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdVersione(Integer idVersione) {
    String key = iArticoloVersione.getKey();
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idVersione));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdVersione() {
    String key = iArticoloVersione.getKey();
    String objIdVersione = KeyHelper.getTokenObjectKey(key, 3);
    return KeyHelper.stringToIntegerObj(objIdVersione);
  }

  /**
   * Valorizza l'attributo.
   * @param articoloVersionePrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticoloVersionePrd(ArticoloVersione articoloVersionePrd) {
    this.iArticoloVersionePrd.setObject(articoloVersionePrd);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return ArticoloVersione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ArticoloVersione getArticoloVersionePrd() {
    return (ArticoloVersione) iArticoloVersionePrd.getObject();
  }

  /**
   * setArticoloVersionePrdKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setArticoloVersionePrdKey(String key) {
    iArticoloVersionePrd.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getArticoloVersionePrdKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getArticoloVersionePrdKey() {
    return iArticoloVersionePrd.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idArticoloPrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdArticoloPrd(String idArticoloPrd) {
    String key = iArticoloVersionePrd.getKey();
    iArticoloVersionePrd.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idArticoloPrd));
    iConfigurazionePrd.setIdArticolo(getIdArticoloPrd());
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdArticoloPrd() {
    String key = iArticoloVersionePrd.getKey();
    String objIdArticoloPrd = KeyHelper.getTokenObjectKey(key, 2);
    return objIdArticoloPrd;

  }

  /**
   * Valorizza l'attributo.
   * @param idVersionePrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdVersionePrd(Integer idVersionePrd) {
    String key = iArticoloVersionePrd.getKey();
    iArticoloVersionePrd.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idVersionePrd));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdVersionePrd() {
    String key = iArticoloVersionePrd.getKey();
    String objIdVersionePrd = KeyHelper.getTokenObjectKey(key, 3);
    return KeyHelper.stringToIntegerObj(objIdVersionePrd);
  }

  /**
   * Valorizza l'attributo.
   * @param attivita
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setAttivita(Attivita attivita) {
    this.iAttivita.setObject(attivita);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Attivita
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Attivita getAttivita() {
    return (Attivita) iAttivita.getObject();
  }

  /**
   * setAttivitaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setAttivitaKey(String key) {
    iAttivita.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getAttivitaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getAttivitaKey() {
    return iAttivita.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idAttivita
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAttivita(String idAttivita) {
    String key = iAttivita.getKey();
    iAttivita.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idAttivita));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAttivita() {
    String key = iAttivita.getKey();
    String objIdAttivita = KeyHelper.getTokenObjectKey(key, 2);
    return objIdAttivita;
  }

  /**
   * Valorizza l'attributo.
   * @param causaleMovMagazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCausaleMovMagazzino(CausaleMovMagazzino causaleMovMagazzino) {
    this.iCausaleMovMagazzino.setObject(causaleMovMagazzino);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return CausaleMovMagazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public CausaleMovMagazzino getCausaleMovMagazzino() {
    return (CausaleMovMagazzino) iCausaleMovMagazzino.getObject();
  }

  /**
   * setCausaleMovMagazzinoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCausaleMovMagazzinoKey(String key) {
    iCausaleMovMagazzino.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getCausaleMovMagazzinoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCausaleMovMagazzinoKey() {
    return iCausaleMovMagazzino.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCauMagazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCauMagazzino(String idCauMagazzino) {
    String key = iCausaleMovMagazzino.getKey();
    iCausaleMovMagazzino.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCauMagazzino));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCauMagazzino() {
    String key = iCausaleMovMagazzino.getKey();
    String objIdCauMagazzino = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCauMagazzino;
  }

  /**
   * Valorizza l'attributo.
   * @param centroCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCentroCosto(CentroCostoCA centroCosto) {
    this.iCentroCosto.setObject(centroCosto);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return CentroCostoCA
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public CentroCostoCA getCentroCosto() {
    return (CentroCostoCA) iCentroCosto.getObject();
  }

  /**
   * setCentroCostoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCentroCostoKey(String key) {
    iCentroCosto.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getCentroCostoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCentroCostoKey() {
    return iCentroCosto.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCentroCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCentroCosto(String idCentroCosto) {
    String key = iCentroCosto.getKey();
    iCentroCosto.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCentroCosto));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCentroCosto() {
    String key = iCentroCosto.getKey();
    String objIdCentroCosto = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCentroCosto;
  }

  /**
   * Valorizza l'attributo.
   * @param centroLavoro
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCentroLavoro(CentroLavoro centroLavoro) {
    this.iCentroLavoro.setObject(centroLavoro);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return CentroLavoro
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public CentroLavoro getCentroLavoro() {
    return (CentroLavoro) iCentroLavoro.getObject();
  }

  /**
   * setCentroLavoroKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCentroLavoroKey(String key) {
    iCentroLavoro.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getCentroLavoroKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCentroLavoroKey() {
    return iCentroLavoro.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCentroLavoro
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCentroLavoro(String idCentroLavoro) {
    String key = iCentroLavoro.getKey();
    iCentroLavoro.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCentroLavoro));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCentroLavoro() {
    String key = iCentroLavoro.getKey();
    String objIdCentroLavoro = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCentroLavoro;
  }

  /**
   * Valorizza l'attributo.
   * @param classeMerceologica
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setClasseMerceologica(ClasseMerceologica classeMerceologica) {
    this.iClasseMerceologica.setObject(classeMerceologica);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return ClasseMerceologica
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ClasseMerceologica getClasseMerceologica() {
    return (ClasseMerceologica) iClasseMerceologica.getObject();
  }

  /**
   * setClasseMerceologicaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setClasseMerceologicaKey(String key) {
    iClasseMerceologica.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getClasseMerceologicaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getClasseMerceologicaKey() {
    return iClasseMerceologica.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idClasseMerceologica
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdClasseMerceologica(String idClasseMerceologica) {
    String key = iClasseMerceologica.getKey();
    iClasseMerceologica.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idClasseMerceologica));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdClasseMerceologica() {
    String key = iClasseMerceologica.getKey();
    String objIdClasseMerceologica = KeyHelper.getTokenObjectKey(key, 2);
    return objIdClasseMerceologica;
  }

  /**
   * Valorizza l'attributo.
   * @param clienteVendita
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCliente(ClienteVendita clienteVendita) {
    this.iCliente.setObject(clienteVendita);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Cliente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ClienteVendita getCliente() {
    return (ClienteVendita) iCliente.getObject();
  }

  /**
   * setClienteVenditaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setClienteKey(String key) {
    iCliente.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getClienteKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getClienteKey() {
    return iCliente.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCliente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCliente(String idCliente) {
    String key = iCliente.getKey();
    iCliente.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCliente));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCliente() {
    String key = iCliente.getKey();
    String objIdCliente = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCliente;
  }

  /**
   * Valorizza l'attributo.
   * @param classeMateriale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setClasseMateriale(ClasseMateriale classeMateriale) {
    this.iClasseMateriale.setObject(classeMateriale);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return ClasseMateriale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ClasseMateriale getClasseMateriale() {
    return (ClasseMateriale) iClasseMateriale.getObject();
  }

  /**
   * setClasseMaterialeKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setClasseMaterialeKey(String key) {
    iClasseMateriale.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getClasseMaterialeKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getClasseMaterialeKey() {
    return iClasseMateriale.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idClsMateriale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdClsMateriale(String idClsMateriale) {
    String key = iClasseMateriale.getKey();
    iClasseMateriale.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idClsMateriale));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdClsMateriale() {
    String key = iClasseMateriale.getKey();
    String objIdClsMateriale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdClsMateriale;
  }

  /**
   * Valorizza l'attributo.
   * @param commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessa(Commessa commessa) {
    this.iCommessa.setObject(commessa);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Commessa getCommessa() {
    return (Commessa) iCommessa.getObject();
  }

  /**
   * setCommessaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaKey(String key) {
    iCommessa.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getCommessaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCommessaKey() {
    return iCommessa.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCommessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessa(String idCommessa) {
    String key = iCommessa.getKey();
    iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessa));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessa() {
    String key = iCommessa.getKey();
    String objIdCommessa = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessa;
  }

  /**
   * Valorizza l'attributo.
   * @param commessaApp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaApp(Commessa commessaApp) {
    this.iCommessaApp.setObject(commessaApp);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Commessa getCommessaApp() {
    return (Commessa) iCommessaApp.getObject();
  }

  /**
   * setCommessaAppKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaAppKey(String key) {
    iCommessaApp.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getCommessaAppKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCommessaAppKey() {
    return iCommessaApp.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCommessaApp
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessaApp(String idCommessaApp) {
    String key = iCommessaApp.getKey();
    iCommessaApp.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessaApp));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessaApp() {
    String key = iCommessaApp.getKey();
    String objIdCommessaApp = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessaApp;
  }

  /**
   * Valorizza l'attributo.
   * @param commessaCol
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaCol(Commessa commessaCol) {
    this.iCommessaCol.setObject(commessaCol);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Commessa getCommessaCol() {
    return (Commessa) iCommessaCol.getObject();
  }

  /**
   * setCommessaColKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaColKey(String key) {
    iCommessaCol.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getCommessaColKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCommessaColKey() {
    return iCommessaCol.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCommessaCol
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessaCol(String idCommessaCol) {
    String key = iCommessaCol.getKey();
    iCommessaCol.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessaCol));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessaCol() {
    String key = iCommessaCol.getKey();
    String objIdCommessaCol = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessaCol;
  }

  /**
   * Valorizza l'attributo.
   * @param commessaPrm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaPrm(Commessa commessaPrm) {
    this.iCommessaPrm.setObject(commessaPrm);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Commessa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Commessa getCommessaPrm() {
    return (Commessa) iCommessaPrm.getObject();
  }

  /**
   * setCommessaPrmKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setCommessaPrmKey(String key) {
    iCommessaPrm.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getCommessaPrmKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getCommessaPrmKey() {
    return iCommessaPrm.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idCommessaPrm
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdCommessaPrm(String idCommessaPrm) {
    String key = iCommessaPrm.getKey();
    iCommessaPrm.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessaPrm));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdCommessaPrm() {
    String key = iCommessaPrm.getKey();
    String objIdCommessaPrm = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCommessaPrm;
  }

  /**
   * Valorizza l'attributo.
   * @param componenteCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setComponenteCosto(ComponenteCosto componenteCosto) {
    this.iComponenteCosto.setObject(componenteCosto);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return ComponenteCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ComponenteCosto getComponenteCosto() {
    return (ComponenteCosto) iComponenteCosto.getObject();
  }

  /**
   * setComponenteCostoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setComponenteCostoKey(String key) {
    iComponenteCosto.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getComponenteCostoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getComponenteCostoKey() {
    return iComponenteCosto.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idComponenteCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdComponenteCosto(String idComponenteCosto) {
    String key = iComponenteCosto.getKey();
    iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idComponenteCosto));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdComponenteCosto() {
    String key = iComponenteCosto.getKey();
    String objIdComponenteCosto = KeyHelper.getTokenObjectKey(key, 2);
    return objIdComponenteCosto;
  }

  /**
   * Valorizza l'attributo.
   * @param configurazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setConfigurazione(Configurazione configurazione) {
    this.iConfigurazione.setObject(configurazione);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Configurazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Configurazione getConfigurazione() {
    return (Configurazione) iConfigurazione.getObject();
  }

  /**
   * setConfigurazioneKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setConfigurazioneKey(String key) {
    iConfigurazione.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getConfigurazioneKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdConfigurazione(Integer idConfigurazione) {
    String key = iConfigurazione.getKey();
    iConfigurazione.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idConfigurazione));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdConfigurazione() {
    String key = iConfigurazione.getKey();
    String objIdConfigurazione = KeyHelper.getTokenObjectKey(key, 2);
    return KeyHelper.stringToIntegerObj(objIdConfigurazione);
  }

  /**
   * Valorizza l'attributo.
   * @param configurazionePrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setConfigurazionePrd(Configurazione configurazionePrd) {
    this.iConfigurazionePrd.setObject(configurazionePrd);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Configurazione
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Configurazione getConfigurazionePrd() {
    return (Configurazione) iConfigurazionePrd.getObject();
  }

  /**
   * setConfigurazionePrdKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setConfigurazionePrdKey(String key) {
    iConfigurazionePrd.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getConfigurazionePrdKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getConfigurazionePrdKey() {
    return iConfigurazionePrd.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idConfigurazionePrd
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdConfigurazionePrd(Integer idConfigurazionePrd) {
    String key = iConfigurazionePrd.getKey();
    iConfigurazionePrd.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idConfigurazionePrd));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Integer getIdConfigurazionePrd() {
    String key = iConfigurazionePrd.getKey();
    String objIdConfigurazionePrd = KeyHelper.getTokenObjectKey(key, 2);
    return KeyHelper.stringToIntegerObj(objIdConfigurazionePrd);
  }

  /**
   * Valorizza l'attributo.
   * @param dipendente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDipendente(Dipendente dipendente) {
    this.iDipendente.setObject(dipendente);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Dipendente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Dipendente getDipendente() {
    return (Dipendente) iDipendente.getObject();
  }

  /**
   * setDipendenteKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setDipendenteKey(String key) {
    iDipendente.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getDipendenteKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getDipendenteKey() {
    return iDipendente.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idDipendente
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdDipendente(String idDipendente) {
    String key = iDipendente.getKey();
    iDipendente.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idDipendente));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdDipendente() {
    String key = iDipendente.getKey();
    String objIdDipendente = KeyHelper.getTokenObjectKey(key, 2);
    return objIdDipendente;
  }

  /**
   * Valorizza l'attributo.
   * @param fornitore
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setFornitore(Fornitore fornitore) {
    this.iFornitore.setObject(fornitore);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Fornitore
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Fornitore getFornitore() {
    return (Fornitore) iFornitore.getObject();
  }

  /**
   * setFornitoreKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setFornitoreKey(String key) {
    iFornitore.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getFornitoreKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getFornitoreKey() {
    return iFornitore.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idFornitore
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdFornitore(String idFornitore) {
    String key = iFornitore.getKey();
    iFornitore.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idFornitore));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdFornitore() {
    String key = iFornitore.getKey();
    String objIdFornitore = KeyHelper.getTokenObjectKey(key, 2);
    return objIdFornitore;
  }

  /**
   * Valorizza l'attributo.
   * @param gruppoProdotto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setGruppoProdotto(GruppoProdotto gruppoProdotto) {
    this.iGruppoProdotto.setObject(gruppoProdotto);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return GruppoProdotto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public GruppoProdotto getGruppoProdotto() {
    return (GruppoProdotto) iGruppoProdotto.getObject();
  }

  /**
   * setGruppoProdottoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setGruppoProdottoKey(String key) {
    iGruppoProdotto.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getGruppoProdottoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getGruppoProdottoKey() {
    return iGruppoProdotto.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idGruppoProdotto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdGruppoProdotto(String idGruppoProdotto) {
    String key = iGruppoProdotto.getKey();
    iGruppoProdotto.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idGruppoProdotto));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdGruppoProdotto() {
    String key = iGruppoProdotto.getKey();
    String objIdGruppoProdotto = KeyHelper.getTokenObjectKey(key, 2);
    return objIdGruppoProdotto;
  }

  /**
   * Valorizza l'attributo.
   * @param magazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setMagazzino(Magazzino magazzino) {
    this.iMagazzino.setObject(magazzino);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Magazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Magazzino getMagazzino() {
    return (Magazzino) iMagazzino.getObject();
  }

  /**
   * setMagazzinoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setMagazzinoKey(String key) {
    iMagazzino.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getMagazzinoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getMagazzinoKey() {
    return iMagazzino.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idMagazzino
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdMagazzino(String idMagazzino) {
    String key = iMagazzino.getKey();
    iMagazzino.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idMagazzino));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdMagazzino() {
    String key = iMagazzino.getKey();
    String objIdMagazzino = KeyHelper.getTokenObjectKey(key, 2);
    return objIdMagazzino;
  }

  /**
   * Valorizza l'attributo.
   * @param pianificatore
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setPianificatore(Pianificatore pianificatore) {
    this.iPianificatore.setObject(pianificatore);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Pianificatore
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Pianificatore getPianificatore() {
    return (Pianificatore) iPianificatore.getObject();
  }

  /**
   * setPianificatoreKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setPianificatoreKey(String key) {
    iPianificatore.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getPianificatoreKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getPianificatoreKey() {
    return iPianificatore.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idPianificatore
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdPianificatore(String idPianificatore) {
    String key = iPianificatore.getKey();
    iPianificatore.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idPianificatore));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdPianificatore() {
    String key = iPianificatore.getKey();
    String objIdPianificatore = KeyHelper.getTokenObjectKey(key, 2);
    return objIdPianificatore;
  }

  /**
   * Valorizza l'attributo.
   * @param reparto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setReparto(Reparto reparto) {
    this.iReparto.setObject(reparto);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Reparto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Reparto getReparto() {
    return (Reparto) iReparto.getObject();
  }

  /**
   * setRepartoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setRepartoKey(String key) {
    iReparto.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getRepartoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getRepartoKey() {
    return iReparto.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idReparto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdReparto(String idReparto) {
    String key = iReparto.getKey();
    iReparto.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idReparto));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdReparto() {
    String key = iReparto.getKey();
    String objIdReparto = KeyHelper.getTokenObjectKey(key, 2);
    return objIdReparto;
  }

  /**
   * Valorizza l'attributo.
   * @param risorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setRisorsa(Risorsa risorsa) {
    this.iRisorsa.setObject(risorsa);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Risorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Risorsa getRisorsa() {
    return (Risorsa) iRisorsa.getObject();
  }

  /**
   * setRisorsaKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setRisorsaKey(String key) {
    iRisorsa.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getRisorsaKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getRisorsaKey() {
    return iRisorsa.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param tipoRisorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setTipoRisorsa(char tipoRisorsa) {
    String key = iRisorsa.getKey();
    Character tipoRisorsaTmp = new Character(tipoRisorsa);
    iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, tipoRisorsaTmp));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getTipoRisorsa() {
    String key = iRisorsa.getKey();
    String objTipoRisorsa = KeyHelper.getTokenObjectKey(key, 2);
    return KeyHelper.stringToChar(objTipoRisorsa);

  }

  /**
   * Valorizza l'attributo.
   * @param livelloRisorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setLivelloRisorsa(char livelloRisorsa) {
    String key = iRisorsa.getKey();
    Character livelloRisorsaTmp = new Character(livelloRisorsa);
    iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, livelloRisorsaTmp));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return char
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public char getLivelloRisorsa() {
    String key = iRisorsa.getKey();
    String objLivelloRisorsa = KeyHelper.getTokenObjectKey(key, 3);
    return KeyHelper.stringToChar(objLivelloRisorsa);

  }

  /**
   * Valorizza l'attributo.
   * @param idRisorsa
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdRisorsa(String idRisorsa) {
    String key = iRisorsa.getKey();
    iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(key, 4, idRisorsa));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdRisorsa() {
    String key = iRisorsa.getKey();
    String objIdRisorsa = KeyHelper.getTokenObjectKey(key, 4);
    return objIdRisorsa;
  }

  /**
   * Valorizza l'attributo.
   * @param schemaCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setSchemaCosto(SchemaCosto schemaCosto) {
    this.iSchemaCosto.setObject(schemaCosto);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return SchemaCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public SchemaCosto getSchemaCosto() {
    return (SchemaCosto) iSchemaCosto.getObject();
  }

  /**
   * setSchemaCostoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setSchemaCostoKey(String key) {
    iSchemaCosto.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getSchemaCostoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getSchemaCostoKey() {
    return iSchemaCosto.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idSchemaCosto
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdSchemaCosto(String idSchemaCosto) {
    String key = iSchemaCosto.getKey();
    iSchemaCosto.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idSchemaCosto));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdSchemaCosto() {
    String key = iSchemaCosto.getKey();
    String objIdSchemaCosto = KeyHelper.getTokenObjectKey(key, 2);
    return objIdSchemaCosto;
  }

  /**
   * Valorizza l'attributo.
   * @param stabilimento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setStabilimento(Stabilimento stabilimento) {
    this.iStabilimento.setObject(stabilimento);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return Stabilimento
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public Stabilimento getStabilimento() {
    return (Stabilimento) iStabilimento.getObject();
  }

  /**
   * setStabilimentoKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setStabilimentoKey(String key) {
    iStabilimento.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getStabilimentoKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdStabilimento() {
    String key = iStabilimento.getKey();
    String objIdStabilimento = KeyHelper.getTokenObjectKey(key, 2);
    return objIdStabilimento;
  }

  /**
   * Valorizza l'attributo.
   * @param umAcqVen
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmAcqVen(UnitaMisura umAcqVen) {
    this.iUmAcqVen.setObject(umAcqVen);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return UnitaMisura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public UnitaMisura getUmAcqVen() {
    return (UnitaMisura) iUmAcqVen.getObject();
  }

  /**
   * setUmAcqVenKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmAcqVenKey(String key) {
    iUmAcqVen.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getUmAcqVenKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getUmAcqVenKey() {
    return iUmAcqVen.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idUMAcqVen
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdUMAcqVen(String idUMAcqVen) {
    String key = iUmAcqVen.getKey();
    iUmAcqVen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idUMAcqVen));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdUMAcqVen() {
    String key = iUmAcqVen.getKey();
    String objIdUMAcqVen = KeyHelper.getTokenObjectKey(key, 2);
    return objIdUMAcqVen;
  }

  /**
   * Valorizza l'attributo.
   * @param umPrmMag
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmPrmMag(UnitaMisura umPrmMag) {
    this.iUmPrmMag.setObject(umPrmMag);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return UnitaMisura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public UnitaMisura getUmPrmMag() {
    return (UnitaMisura) iUmPrmMag.getObject();
  }

  /**
   * setUmPrmMagKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmPrmMagKey(String key) {
    iUmPrmMag.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getUmPrmMagKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdUmPrmMag() {
    String key = iUmPrmMag.getKey();
    String objIdUmPrmMag = KeyHelper.getTokenObjectKey(key, 2);
    return objIdUmPrmMag;
  }

  /**
   * Valorizza l'attributo.
   * @param umSecMag
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmSecMag(UnitaMisura umSecMag) {
    this.iUmSecMag.setObject(umSecMag);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * Restituisce l'attributo.
   * @return UnitaMisura
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public UnitaMisura getUmSecMag() {
    return (UnitaMisura) iUmSecMag.getObject();
  }

  /**
   * setUmSecMagKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setUmSecMagKey(String key) {
    iUmSecMag.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  /**
   * getUmSecMagKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getUmSecMagKey() {
    return iUmSecMag.getKey();
  }

  /**
   * Valorizza l'attributo.
   * @param idUmSecMag
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setIdUmSecMag(String idUmSecMag) {
    String key = iUmSecMag.getKey();
    iUmSecMag.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idUmSecMag));
    setDirty();
  }

  /**
   * Restituisce l'attributo.
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getIdUmSecMag() {
    String key = iUmSecMag.getKey();
    String objIdUmSecMag = KeyHelper.getTokenObjectKey(key, 2);
    return objIdUmSecMag;
  }

  /**
   * setEqual
   * @param obj
   * @throws CopyException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    StoricoCommessaPO storicoCommessaPO = (StoricoCommessaPO) obj;
    if (storicoCommessaPO.iDataOrigine != null)
      iDataOrigine = (java.sql.Date) storicoCommessaPO.iDataOrigine.clone();
    if (storicoCommessaPO.iDataOrdine != null)
      iDataOrdine = (java.sql.Date) storicoCommessaPO.iDataOrdine.clone();
    if (storicoCommessaPO.iDataBolla != null)
      iDataBolla = (java.sql.Date) storicoCommessaPO.iDataBolla.clone();
    if (storicoCommessaPO.iDataFattura != null)
      iDataFattura = (java.sql.Date) storicoCommessaPO.iDataFattura.clone();
    iAzienda.setEqual(storicoCommessaPO.iAzienda);
    iArticolo.setEqual(storicoCommessaPO.iArticolo);
    iArticoloVersione.setEqual(storicoCommessaPO.iArticoloVersione);
    iArticoloVersionePrd.setEqual(storicoCommessaPO.iArticoloVersionePrd);
    iAttivita.setEqual(storicoCommessaPO.iAttivita);
    iCausaleMovMagazzino.setEqual(storicoCommessaPO.iCausaleMovMagazzino);
    iCentroCosto.setEqual(storicoCommessaPO.iCentroCosto);
    iCentroLavoro.setEqual(storicoCommessaPO.iCentroLavoro);
    iClasseMerceologica.setEqual(storicoCommessaPO.iClasseMerceologica);
    iCliente.setEqual(storicoCommessaPO.iCliente);
    iClasseMateriale.setEqual(storicoCommessaPO.iClasseMateriale);
    iCommessa.setEqual(storicoCommessaPO.iCommessa);
    iCommessaApp.setEqual(storicoCommessaPO.iCommessaApp);
    iCommessaCol.setEqual(storicoCommessaPO.iCommessaCol);
    iCommessaPrm.setEqual(storicoCommessaPO.iCommessaPrm);
    iComponenteCosto.setEqual(storicoCommessaPO.iComponenteCosto);
    iConfigurazione.setEqual(storicoCommessaPO.iConfigurazione);
    iConfigurazionePrd.setEqual(storicoCommessaPO.iConfigurazionePrd);
    iDipendente.setEqual(storicoCommessaPO.iDipendente);
    iFornitore.setEqual(storicoCommessaPO.iFornitore);
    iGruppoProdotto.setEqual(storicoCommessaPO.iGruppoProdotto);
    iMagazzino.setEqual(storicoCommessaPO.iMagazzino);
    iPianificatore.setEqual(storicoCommessaPO.iPianificatore);
    iReparto.setEqual(storicoCommessaPO.iReparto);
    iRisorsa.setEqual(storicoCommessaPO.iRisorsa);
    iSchemaCosto.setEqual(storicoCommessaPO.iSchemaCosto);
    iStabilimento.setEqual(storicoCommessaPO.iStabilimento);
    iUmAcqVen.setEqual(storicoCommessaPO.iUmAcqVen);
    iUmPrmMag.setEqual(storicoCommessaPO.iUmPrmMag);
    iUmSecMag.setEqual(storicoCommessaPO.iUmSecMag);

    iCausaleTestataAcq.setEqual(storicoCommessaPO.iCausaleTestataAcq);
    iCausaleRigaAcq.setEqual(storicoCommessaPO.iCausaleRigaAcq);
    iCausaleTestataVen.setEqual(storicoCommessaPO.iCausaleTestataVen);
    iCausaleRigaVen.setEqual(storicoCommessaPO.iCausaleRigaVen);
    iCausaleTestataPrd.setEqual(storicoCommessaPO.iCausaleTestataPrd);
    iCausaleRigaPrdMat.setEqual(storicoCommessaPO.iCausaleRigaPrdMat);
    iCausaleRigaPrdRsr.setEqual(storicoCommessaPO.iCausaleRigaPrdRsr);
    iCausaleRigaPrdVrs.setEqual(storicoCommessaPO.iCausaleRigaPrdVrs);
    iCausaleTestataGen.setEqual(storicoCommessaPO.iCausaleTestataGen);
    iCausaleRigaGen.setEqual(storicoCommessaPO.iCausaleRigaGen);
    iCausaleTestataTrasMag.setEqual(storicoCommessaPO.iCausaleTestataTrasMag);
    iCausaleRigaTrasMag.setEqual(storicoCommessaPO.iCausaleRigaTrasMag);
    //Fix 10416 inizio
    iCausaleTestataSrv.setEqual(storicoCommessaPO.iCausaleTestataSrv);
    iCausaleRigaSrvMat.setEqual(storicoCommessaPO.iCausaleRigaSrvMat);
    iCausaleRigaSrvRsr.setEqual(storicoCommessaPO.iCausaleRigaSrvRsr);
    iCausaleRigaSrvVrs.setEqual(storicoCommessaPO.iCausaleRigaSrvVrs);
    //Fix 10416 fine
    iStoricoCommessaDet.setEqual(storicoCommessaPO.iStoricoCommessaDet);//33590
    
  }

  /**
   * checkAll
   * @param components
   * @return Vector
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public void setKey(String key) {
    setIdAzienda(KeyHelper.getTokenObjectKey(key, 1));
    setIdProgressivo(KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(key, 2)));
  }

  /**
   *  getKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getKey() {
    String idAzienda = getIdAzienda();
    Integer idProgressivo = getIdProgressivo();
    Object[] keyParts = {
        idAzienda, idProgressivo};
    return KeyHelper.buildObjectKey(keyParts);
  }

  /**
   * isDeletable
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    Wizard     Codice generato da Wizard
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
   * 12/04/2005    CodeGen     Codice generato da CodeGenerator
   *
   */
  protected TableManager getTableManager() throws SQLException {
    return StoricoCommessaTM.getInstance();
  }

  /**
   * setIdAziendaInternal
   * @param idAzienda
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  protected void setIdAziendaInternal(String idAzienda) {

    String key1 = iArticoloVersione.getKey();
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
    String key30 = iArticolo.getKey();
    iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key30, 1, idAzienda));
    String key2 = iArticoloVersionePrd.getKey();
    iArticoloVersionePrd.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
    String key3 = iAttivita.getKey();
    iAttivita.setKey(KeyHelper.replaceTokenObjectKey(key3, 1, idAzienda));
    String key4 = iCausaleMovMagazzino.getKey();
    iCausaleMovMagazzino.setKey(KeyHelper.replaceTokenObjectKey(key4, 1, idAzienda));
    String key5 = iCentroLavoro.getKey();
    iCentroLavoro.setKey(KeyHelper.replaceTokenObjectKey(key5, 1, idAzienda));
    String key6 = iCentroCosto.getKey();
    iCentroCosto.setKey(KeyHelper.replaceTokenObjectKey(key6, 1, idAzienda));
    String key7 = iClasseMerceologica.getKey();
    iClasseMerceologica.setKey(KeyHelper.replaceTokenObjectKey(key7, 1, idAzienda));
    String key8 = iCliente.getKey();
    iCliente.setKey(KeyHelper.replaceTokenObjectKey(key8, 1, idAzienda));
    String key9 = iClasseMateriale.getKey();
    iClasseMateriale.setKey(KeyHelper.replaceTokenObjectKey(key9, 1, idAzienda));
    String key10 = iCommessa.getKey();
    iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key10, 1, idAzienda));
    String key11 = iCommessaApp.getKey();
    iCommessaApp.setKey(KeyHelper.replaceTokenObjectKey(key11, 1, idAzienda));
    String key12 = iCommessaCol.getKey();
    iCommessaCol.setKey(KeyHelper.replaceTokenObjectKey(key12, 1, idAzienda));
    String key13 = iCommessaPrm.getKey();
    iCommessaPrm.setKey(KeyHelper.replaceTokenObjectKey(key13, 1, idAzienda));
    String key14 = iComponenteCosto.getKey();
    iComponenteCosto.setKey(KeyHelper.replaceTokenObjectKey(key14, 1, idAzienda));
    String key15 = iConfigurazione.getKey();
    iConfigurazione.setKey(KeyHelper.replaceTokenObjectKey(key15, 1, idAzienda));
    String key16 = iConfigurazionePrd.getKey();
    iConfigurazionePrd.setKey(KeyHelper.replaceTokenObjectKey(key16, 1, idAzienda));
    String key17 = iDipendente.getKey();
    iDipendente.setKey(KeyHelper.replaceTokenObjectKey(key17, 1, idAzienda));
    String key18 = iFornitore.getKey();
    iFornitore.setKey(KeyHelper.replaceTokenObjectKey(key18, 1, idAzienda));
    String key19 = iGruppoProdotto.getKey();
    iGruppoProdotto.setKey(KeyHelper.replaceTokenObjectKey(key19, 1, idAzienda));
    String key20 = iMagazzino.getKey();
    iMagazzino.setKey(KeyHelper.replaceTokenObjectKey(key20, 1, idAzienda));
    String key21 = iPianificatore.getKey();
    iPianificatore.setKey(KeyHelper.replaceTokenObjectKey(key21, 1, idAzienda));
    String key22 = iReparto.getKey();
    iReparto.setKey(KeyHelper.replaceTokenObjectKey(key22, 1, idAzienda));
    String key23 = iRisorsa.getKey();
    iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(key23, 1, idAzienda));
    String key24 = iSchemaCosto.getKey();
    iSchemaCosto.setKey(KeyHelper.replaceTokenObjectKey(key24, 1, idAzienda));
    String key25 = iStabilimento.getKey();
    iStabilimento.setKey(KeyHelper.replaceTokenObjectKey(key25, 1, idAzienda));
    String key26 = iUmSecMag.getKey();
    iUmSecMag.setKey(KeyHelper.replaceTokenObjectKey(key26, 1, idAzienda));
    String key27 = iUmPrmMag.getKey();
    iUmPrmMag.setKey(KeyHelper.replaceTokenObjectKey(key27, 1, idAzienda));
    String key28 = iUmAcqVen.getKey();
    iUmAcqVen.setKey(KeyHelper.replaceTokenObjectKey(key28, 1, idAzienda));

    String key29 = iCausaleTestataAcq.getKey();
    iCausaleTestataAcq.setKey(KeyHelper.replaceTokenObjectKey(key29, 1, idAzienda));
    String key31 = iCausaleRigaAcq.getKey();
    iCausaleRigaAcq.setKey(KeyHelper.replaceTokenObjectKey(key31, 1, idAzienda));
    String key32 = iCausaleTestataVen.getKey();
    iCausaleTestataVen.setKey(KeyHelper.replaceTokenObjectKey(key32, 1, idAzienda));
    String key33 = iCausaleRigaVen.getKey();
    iCausaleRigaVen.setKey(KeyHelper.replaceTokenObjectKey(key33, 1, idAzienda));
    String key34 = iCausaleTestataPrd.getKey();
    iCausaleTestataPrd.setKey(KeyHelper.replaceTokenObjectKey(key34, 1, idAzienda));
    String key35 = iCausaleRigaPrdMat.getKey();
    iCausaleRigaPrdMat.setKey(KeyHelper.replaceTokenObjectKey(key35, 1, idAzienda));
    String key36 = iCausaleRigaPrdVrs.getKey();
    iCausaleRigaPrdVrs.setKey(KeyHelper.replaceTokenObjectKey(key36, 1, idAzienda));
    String key37 = iCausaleRigaPrdRsr.getKey();
    iCausaleRigaPrdRsr.setKey(KeyHelper.replaceTokenObjectKey(key37, 1, idAzienda));
    String key38 = iCausaleTestataGen.getKey();
    iCausaleTestataGen.setKey(KeyHelper.replaceTokenObjectKey(key38, 1, idAzienda));
    String key39 = iCausaleRigaGen.getKey();
    iCausaleRigaGen.setKey(KeyHelper.replaceTokenObjectKey(key39, 1, idAzienda));
    String key40 = iCausaleTestataTrasMag.getKey();
    iCausaleTestataTrasMag.setKey(KeyHelper.replaceTokenObjectKey(key40, 1, idAzienda));
    String key41 = iCausaleRigaTrasMag.getKey();
    iCausaleRigaTrasMag.setKey(KeyHelper.replaceTokenObjectKey(key41, 1, idAzienda));
    //Fix 10416 inizio
    String key42 = iCausaleTestataSrv.getKey();
    iCausaleTestataSrv.setKey(KeyHelper.replaceTokenObjectKey(key42, 1, idAzienda));
    String key43 = iCausaleRigaSrvMat.getKey();
    iCausaleRigaSrvMat.setKey(KeyHelper.replaceTokenObjectKey(key43, 1, idAzienda));
    String key44 = iCausaleRigaSrvRsr.getKey();
    iCausaleRigaSrvRsr.setKey(KeyHelper.replaceTokenObjectKey(key44, 1, idAzienda));
    String key45 = iCausaleRigaSrvVrs.getKey();
    iCausaleRigaSrvVrs.setKey(KeyHelper.replaceTokenObjectKey(key45, 1, idAzienda));
    //Fix 10416 fine
    
  }

  //----------------------------------------------------------------------------
  //  Articolo Proxy
  //----------------------------------------------------------------------------
  public void setIdArticolo(String idArticolo) {
    String key = iArticolo.getKey();
    iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idArticolo));
    iArticoloVersione.setKey(KeyHelper.replaceTokenObjectKey(iArticoloVersione.getKey(), 2, idArticolo));
    iConfigurazione.setIdArticolo(getIdArticolo());
    setDirty();
  }

  public String getIdArticolo() {
    String key = iArticolo.getKey();
    String objIdArticolo = KeyHelper.getTokenObjectKey(key, 2);
    return objIdArticolo;
  }

  public void setArticoloKey(String key) {
    iArticolo.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getArticoloKey() {
    return iArticolo.getKey();
  }

  public void setArticolo(Articolo articolo) {
    this.iArticolo.setObject(articolo);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public Articolo getArticolo() {
    return (Articolo) iArticolo.getObject();
  }

  //----------------------------------------------------------------------------
  // iCausaleTestataAcq proxy
  //----------------------------------------------------------------------------
  public void setCausaleTestataAcq(CausaleDocumentoTestataAcq causaleTestata) {
    this.iCausaleTestataAcq.setObject(causaleTestata);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleDocumentoTestataAcq getCausaleTestataAcq() {
    return (CausaleDocumentoTestataAcq) iCausaleTestataAcq.getObject();
  }

  public void setCausaleTestataAcqKey(String key) {
    iCausaleTestataAcq.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleTestataAcqKey() {
    return iCausaleTestataAcq.getKey();
  }

  public void setIdCauOrgTesAcq(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgTes = idCausale;
    String key = iCausaleTestataAcq.getKey();
    iCausaleTestataAcq.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgTesAcq() {
    String key = iCausaleTestataAcq.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaAcq proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaAcq(CausaleDocumentoRigaAcq causaleRiga) {
    this.iCausaleRigaAcq.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleDocumentoRigaAcq getCausaleRigaAcq() {
    return (CausaleDocumentoRigaAcq) iCausaleRigaAcq.getObject();
  }

  public void setCausaleRigaAcqKey(String key) {
    iCausaleRigaAcq.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaAcqKey() {
    return iCausaleRigaAcq.getKey();
  }

  public void setIdCauOrgRigAcq(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaAcq.getKey();
    iCausaleRigaAcq.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigAcq() {
    String key = iCausaleRigaAcq.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleTestataVen proxy
  //----------------------------------------------------------------------------
  public void setCausaleTestataVen(CausaleDocumentoVendita causaleTestata) {
    this.iCausaleTestataVen.setObject(causaleTestata);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleDocumentoVendita getCausaleTestataVen() {
    return (CausaleDocumentoVendita) iCausaleTestataVen.getObject();
  }

  public void setCausaleTestataVenKey(String key) {
    iCausaleTestataVen.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleTestataVenKey() {
    return iCausaleTestataVen.getKey();
  }

  public void setIdCauOrgTesVen(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgTes = idCausale;
    String key = iCausaleTestataVen.getKey();
    iCausaleTestataVen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgTesVen() {
    String key = iCausaleTestataVen.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaVen proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaVen(CausaleRigaDocVen causaleRiga) {
    this.iCausaleRigaVen.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleRigaDocVen getCausaleRigaVen() {
    return (CausaleRigaDocVen) iCausaleRigaVen.getObject();
  }

  public void setCausaleRigaVenKey(String key) {
    iCausaleRigaVen.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaVenKey() {
    return iCausaleRigaVen.getKey();
  }

  public void setIdCauOrgRigVen(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaVen.getKey();
    iCausaleRigaVen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigVen() {
    String key = iCausaleRigaVen.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleTestataPrd proxy
  //----------------------------------------------------------------------------
  public void setCausaleTestataPrd(CausaleDocProduzione causaleTestata) {
    this.iCausaleTestataPrd.setObject(causaleTestata);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleDocProduzione getCausaleTestataPrd() {
    return (CausaleDocProduzione) iCausaleTestataPrd.getObject();
  }

  public void setCausaleTestataPrdKey(String key) {
    iCausaleTestataPrd.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleTestataPrdKey() {
    return iCausaleTestataPrd.getKey();
  }

  public void setIdCauOrgTesPrd(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgTes = idCausale;
    String key = iCausaleTestataPrd.getKey();
    iCausaleTestataPrd.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgTesPrd() {
    String key = iCausaleTestataPrd.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaPrdMat proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaPrdMat(CausaleMovPrelPrd causaleRiga) {
    this.iCausaleRigaPrdMat.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleMovPrelPrd getCausaleRigaPrdMat() {
    return (CausaleMovPrelPrd) iCausaleRigaPrdMat.getObject();
  }

  public void setCausaleRigaPrdMatKey(String key) {
    iCausaleRigaPrdMat.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaPrdMatKey() {
    return iCausaleRigaPrdMat.getKey();
  }

  public void setIdCauOrgRigPrdMat(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaPrdMat.getKey();
    iCausaleRigaPrdMat.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigPrdMat() {
    String key = iCausaleRigaPrdMat.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaPrdVrs proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaPrdVrs(CausaleMovVersPrd causaleRiga) {
    this.iCausaleRigaPrdVrs.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleMovVersPrd getCausaleRigaPrdVrs() {
    return (CausaleMovVersPrd) iCausaleRigaPrdVrs.getObject();
  }

  public void setCausaleRigaPrdVrsKey(String key) {
    iCausaleRigaPrdVrs.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaPrdVrsKey() {
    return iCausaleRigaPrdVrs.getKey();
  }

  public void setIdCauOrgRigPrdVrs(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaPrdVrs.getKey();
    iCausaleRigaPrdVrs.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigPrdVrs() {
    String key = iCausaleRigaPrdVrs.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaPrdRsr proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaPrdRsr(CausaleUtilizzoRisorse causaleRiga) {
    this.iCausaleRigaPrdRsr.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleUtilizzoRisorse getCausaleRigaPrdRsr() {
    return (CausaleUtilizzoRisorse) iCausaleRigaPrdRsr.getObject();
  }

  public void setCausaleRigaPrdRsrKey(String key) {
    iCausaleRigaPrdRsr.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaPrdRsrKey() {
    return iCausaleRigaPrdRsr.getKey();
  }

  public void setIdCauOrgRigPrdRsr(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaPrdRsr.getKey();
    iCausaleRigaPrdRsr.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigPrdRsr() {
    String key = iCausaleRigaPrdRsr.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }
  

  //----------------------------------------------------------------------------
  // iCausaleTestataSrv proxy
  //----------------------------------------------------------------------------
  public void setCausaleTestataSrv(CausaleDocServizio causaleTestata) {
    this.iCausaleTestataSrv.setObject(causaleTestata);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleDocServizio getCausaleTestataSrv() {
    return (CausaleDocServizio) iCausaleTestataSrv.getObject();
  }

  public void setCausaleTestataSrvKey(String key) {
     iCausaleTestataSrv.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleTestataSrvKey() {
    return iCausaleTestataSrv.getKey();
  }

  public void setIdCauOrgTesSrv(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgTes = idCausale;
    String key = iCausaleTestataSrv.getKey();
    iCausaleTestataSrv.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgTesSrv() {
    String key = iCausaleTestataSrv.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaSrvMat proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaSrvMat(CausaleMovPrelSrv causaleRiga) {
    this.iCausaleRigaSrvMat.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleMovPrelSrv getCausaleRigaSrvMat() {
    return (CausaleMovPrelSrv) iCausaleRigaSrvMat.getObject();
  }

  public void setCausaleRigaPrdSrvKey(String key) {
     iCausaleRigaSrvMat.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaSrvMatKey() {
    return iCausaleRigaSrvMat.getKey();
  }

  public void setIdCauOrgRigSrvMat(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaSrvMat.getKey();
    iCausaleRigaSrvMat.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigSrvMat() {
    String key = iCausaleRigaSrvMat.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaSrvVrs proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaSrvVrs(CausaleMovVersSrv causaleRiga) {
    this.iCausaleRigaSrvVrs.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleMovVersSrv getCausaleRigaSrvVrs() {
    return (CausaleMovVersSrv) iCausaleRigaSrvVrs.getObject();
  }

  public void setCausaleRigaSrvVrsKey(String key) {
     iCausaleRigaSrvVrs.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaSrvVrsKey() {
    return iCausaleRigaSrvVrs.getKey();
  }

  public void setIdCauOrgRigSrvVrs(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaSrvVrs.getKey();
    iCausaleRigaSrvVrs.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigSrvVrs() {
    String key = iCausaleRigaSrvVrs.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaSrvRsr proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaSrvRsr(CausaleUtilizzoRisorse causaleRiga) {
    this.iCausaleRigaSrvRsr.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleUtilizzoRisorse getCausaleRigaSrvRsr() {
    return (CausaleUtilizzoRisorse) iCausaleRigaSrvRsr.getObject();
  }

  public void setCausaleRigaSrvRsrKey(String key) {
     iCausaleRigaSrvRsr.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaSrvRsrKey() {
    return iCausaleRigaSrvRsr.getKey();
  }

  public void setIdCauOrgRigSrvRsr(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaSrvRsr.getKey();
    iCausaleRigaSrvRsr.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigSrvRsr() {
    String key = iCausaleRigaSrvRsr.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleTestataGen proxy
  //----------------------------------------------------------------------------
  public void setCausaleTestataGen(CausaleDocumentoGen causaleTestata) {
    this.iCausaleTestataGen.setObject(causaleTestata);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleDocumentoGen getCausaleTestataGen() {
    return (CausaleDocumentoGen) iCausaleTestataGen.getObject();
  }

  public void setCausaleTestataGenKey(String key) {
    iCausaleTestataGen.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleTestataGenKey() {
    return iCausaleTestataGen.getKey();
  }

  public void setIdCauOrgTesGen(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgTes = idCausale;
    String key = iCausaleTestataGen.getKey();
    iCausaleTestataGen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgTesGen() {
    String key = iCausaleTestataGen.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaGen proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaGen(CausaleRigaDocGen causaleRiga) {
    this.iCausaleRigaGen.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleRigaDocGen getCausaleRigaGen() {
    return (CausaleRigaDocGen) iCausaleRigaGen.getObject();
  }

  public void setCausaleRigaGenKey(String key) {
    iCausaleRigaGen.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaGenKey() {
    return iCausaleRigaGen.getKey();
  }

  public void setIdCauOrgRigGen(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaGen.getKey();
    iCausaleRigaGen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigGen() {
    String key = iCausaleRigaGen.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleTestataTrasMag proxy
  //----------------------------------------------------------------------------
  public void setCausaleTestataTrasMag(CausaleDocumentoTrasf causaleTestata) {
    this.iCausaleTestataTrasMag.setObject(causaleTestata);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleDocumentoTrasf getCausaleTestataTrasMag() {
    return (CausaleDocumentoTrasf) iCausaleTestataTrasMag.getObject();
  }

  public void setCausaleTestataTrasMagKey(String key) {
    iCausaleTestataTrasMag.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleTestataTrasMagKey() {
    return iCausaleTestataTrasMag.getKey();
  }

  public void setIdCauOrgTesTrasMag(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgTes = idCausale;
    String key = iCausaleTestataTrasMag.getKey();
    iCausaleTestataTrasMag.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgTesTrasMag() {
    String key = iCausaleTestataTrasMag.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  //----------------------------------------------------------------------------
  // iCausaleRigaTrasMag proxy
  //----------------------------------------------------------------------------
  public void setCausaleRigaTrasMag(CausaleRigaDocTrasf causaleRiga) {
    this.iCausaleRigaTrasMag.setObject(causaleRiga);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public CausaleRigaDocTrasf getCausaleRigaTrasMag() {
    return (CausaleRigaDocTrasf) iCausaleRigaTrasMag.getObject();
  }

  public void setCausaleRigaTrasMagKey(String key) {
    iCausaleRigaTrasMag.setKey(key);
    setDirty();
    iStoricoCommessaDet.setFatherKeyChanged();//33950
  }

  public String getCausaleRigaTrasMagKey() {
    return iCausaleRigaTrasMag.getKey();
  }

  public void setIdCauOrgRigTrasMag(String idCausale) {
    if (idCausale != null)
      this.iIdCauOrgRig = idCausale;
    String key = iCausaleRigaTrasMag.getKey();
    iCausaleRigaTrasMag.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    setDirty();
  }

  public String getIdCauOrgRigTrasMag() {
    String key = iCausaleRigaTrasMag.getKey();
    String objIdCausale = KeyHelper.getTokenObjectKey(key, 2);
    return objIdCausale;
  }

  public void setCodConfig(String codConfig) {
    this.iCodConfig = codConfig;
    setDirty();
  }

  public String getCodConfig() {
    return iCodConfig;
  }

  public void setTipoRilevazioneRsr(char tipoRilevazioneRsr) {
    this.iTipoRilevazioneRsr = tipoRilevazioneRsr;
    setDirty();
  }

  public char getTipoRilevazioneRsr() {
    return iTipoRilevazioneRsr;
  }

  public void setCostoUnitarioOrigine(BigDecimal costoUnitarioOrigine) {
    this.iCostoUnitarioOrigine = costoUnitarioOrigine;
    setDirty();
  }

  public BigDecimal getCostoUnitarioOrigine() {
    return iCostoUnitarioOrigine;
  }

  public void setValoreRiga(BigDecimal valoreRiga) {
    this.iValoreRiga = valoreRiga;
    setDirty();
  }

  public BigDecimal getValoreRiga() {
    return iValoreRiga;
  }
  //Mod XXXXX start
  public void setIdOperazione(String idOperazione) {
     this.iIdOperazione = idOperazione;
     setDirty();
   }
   public String getIdOperazione() {
     return iIdOperazione;
   }
   //Mod XXXXX end

   public BigDecimal getCostoTotRifer() {
      //if (getTipoRigaOrigine() == PRODUZIONE_RISORSA && getTipoRilevazioneRsr() == A_TEMPO) { Fix 10416 
      if ((getTipoRigaOrigine() == PRODUZIONE_RISORSA || getTipoRigaOrigine() == SERVIZIO_RISORSA) && 
              getTipoRilevazioneRsr() == A_TEMPO) { //Fix 10416 
           if (getTempo() != null && getCostoUnitario() != null)
              return getTempo().multiply(getCostoUnitario());
     }//Fix 10416 inizio
     else if(getTipoRigaOrigine() == SERVIZIO_RISORSA && getTipoRilevazioneRsr() != A_TEMPO)
     {
        return getCostoUnitario();
     }//Fix 10416 fine
    else {
      if (getCostoUnitario() != null) {
        BigDecimal qtaUM = new BigDecimal("0");
        qtaUM = getQuantitaUMPrm() != null && getQuantitaUMPrm().compareTo(new BigDecimal("0")) != 0 ? getQuantitaUMPrm() : getQuantitaUMAcqVen();

        return sum(qtaUM, getQtaScarto()).multiply(getCostoUnitario());
      }
      //      if (getQuantitaUMPrm() != null && getQtaScarto() != null && getCostoUnitario() != null)
      //        return (getQuantitaUMPrm().add(getQtaScarto())).multiply(getCostoUnitario());
    }
    return null;
  }

  public BigDecimal sum(BigDecimal val1, BigDecimal val2)
  {
    BigDecimal v1 = (val1 == null) ? new BigDecimal("0") : val1;
    BigDecimal v2 = (val2 == null) ? new BigDecimal("0") : val2;

    return v1.add(v2);
  }

  public void setLivelloCommessa(Integer livello)
  {
    iLivelloCommessa = livello;
    setDirty();
  }

  public Integer getLivelloCommessa()
  {
    return iLivelloCommessa;
  }
  
  //33590 inizio
  public int saveOwnedObjects(int rc) throws SQLException {
	  rc = iStoricoCommessaDet.save(rc);
	  return rc;
  }

  public int deleteOwnedObjects() throws SQLException {
	  return getStoricoCommessaDetInternal().delete();
  }

  public boolean initializeOwnedObjects(boolean result) {
	  result = iStoricoCommessaDet.initialize(result);
	  return result;
  }
  
  public List getStoricoCommessaDet() {
	  return getStoricoCommessaDetInternal();
  }
  
  protected OneToMany getStoricoCommessaDetInternal() {
	  if (iStoricoCommessaDet.isNew())
		  iStoricoCommessaDet.retrieve();
	  return iStoricoCommessaDet;
  }
  //33590 fine
}


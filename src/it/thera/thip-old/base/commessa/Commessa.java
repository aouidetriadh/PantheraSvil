package it.thera.thip.base.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.ad.*;
import com.thera.thermfw.base.*;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.type.EnumType;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.base.interfca.*;
import it.thera.thip.base.profilo.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.costi.*;
import it.thera.thip.datiTecnici.modpro.*;
import it.thera.thip.magazzino.saldi.*;
import it.thera.thip.produzione.commessa.*;
import it.thera.thip.produzione.commessa.ws.*;
import it.thera.thip.vendite.ordineVE.*;

/**
 * Commessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Wizard 12/12/2001 at 13:53:10
 */
/*
 * Revisions:
 * Number  Date          Owner           Description
 *         12/12/2001    Wizard          Codice generato da Wizard
 *         14/02/2004    Imene Mbazaa    Fix 1438 : Aggiunta la costante COMMESSA_ECCEDENZA
 * 03463   30/03/2005    A.Boulila       aggiunto di metodi e di attributi per il modulo di
 *                                       gestione di commesse
 *         05/08/2005    F. marzouk      Add method ricercaCommesseWS;
 * 04171   06/09/2005    Jed             check on valortOrd of OrdineVendita
 * 04361   21/09/2005    Jed             Add a control between DataChiusura and Data ultima chiusura definitiva (assunta da AmbienteCommesse)
 * 04361   21/09/2005    SAYADI          solve the problem of save related to the Descrizioni in lingua
 * 04361   05/10/2005    SAYADI          Propagation of stato avenzamento when it equal to Provvisoria
 * 04599   09/11/2005    Jed             in new sottoCommessa if dataInzio > DataConfermaApp then dataCoferma <- dataInizio
 * 04599   10/11/2005    Jed             add the methods :   rendiProvvisoria, rendiConfermata, rendiChiusaTecnicamente, rendiChiusaOperativamente, rendiChiusa
 * 04677   22/11/2005    A. Bejaoui      Case is SQL result : NO_ROWS_FOUND will not be considered as error
 * 04709   07/12/2005    LP              Corretto controllo su esistenza costi commessa
 * 04882   12/01/2006    DM              Implementato ObjectWithPostSave per rendere possibile l'automatismo dello stato iniziale
 * 04810   05/01/2006    LP              Aggiunto metodo checkRateCollegateAdOrdine.
 * 05543   13/06/2006    PJ              Aggiunta commessa di contabilità analitica
 * 06958   21/03/2007    GN              Supporto ad Oracle.
 * 07430   09/07/2007    LP              Corretto gestione eccezioni
 * 08779   03/03/2008    OV              Corretto warning errato
 * 08890   19/03/2008    LP              Corretto controllo sull'esistenza di rate in caso il numero ordine sia null
 * 09121   28/04/2008    LP              Corretto impostazione della data conferma nelle sottocommesse
 * 09297   27/05/2008    Metelli         Corretto errore di nodo WF in CM
 * 09821   25/09/2008    FR              Commentato metodo retrieve(...) e spostato logica in initializeOwnedObjects(...)
 * 10061   13/11/2008    MN              Se l'ordine collegato alla commessa è stato riorganizzato (significativo il rif.
 *                                       all'ordine riorganizzato) non deve essere eseguito il controllo
 *                                       sull'ordine, indipendnetemente dalla tipologia di commessa.
 * 13297   15/10/2010    FM              Controllo su avansamento wf
 * 15938   15/03/2012    FM              Aggiunta checkAbilitaCreazioneOrdVen per controllare abilita creazione ordine vendita.
 * 19897   30/05/2014    Linda           Aggiunti nuovi attributi.
 * 20785   09/12/2014    Linda           aggiunto metodo checkCUPCIG().
 * 22268   01/10/2015    OCH             Modificato il metodo verifyWorkflow
 * 27669   02/07/2018    GN              Gestione commessa modello
 * 29025   20/03/2019    Jackal          Gestione piano fatturazione con acconti e saldo
 * 29960   08/10/2019    RA				 Gestione creazione ambiente commessa
 * 30948   18/03/2020    Bsondes         Modificare il principio di crea ambiente commessa nel metodo save.
 * 31209   08/05/2020    Mekki           Commentare nella save la chiamata al metodo setIdAmbienteCommessa
 * 31124   05/06/2020    HED             correzione gestione righe NLS nel caso di Multilingual
 * 31527   06/07/2020    PM              Correzione fix  31437 
 * 31596   15/07/2020    Bsondes         modefica il metodo creaAmbienteCommessa().
 * 35221   09/02/2022    YBA             Modifica il metodo checkIdEsternoConfig
 * 33950   10/09/2021    RA				 Aggiunto metodo hasCompenenteATempo
 * 34585   26/11/2021    RA				 Aggiunto metodo getUltimoBudgetCommessaDefinitivo()
 * 35382   12/03/2022    RA				 Aggiunto metodi di servizi per commessa
 */

// 4882 DM inizio
// public class Commessa extends CommessaPO {
public class Commessa extends CommessaPO implements ObjectWithPostSave, SaveConWarning {
  
// 4882 DM fine

  // Fix 1438 ini
  public static final String COMMESSA_ECCEDENZA = "ECCEDENZA";

  // Fix 1438 fin

  //Inizio 03463 A.Boulila
  public static final char STATO_AVANZAM__PROVVISORIA = '0';
  public static final char STATO_AVANZAM__CONFERMATA = '1';
  public static final char STATO_AVANZAM__CHIUSA_TECNICAMENTO = '2';
  public static final char STATO_AVANZAM__CHIUSA_OPERATIVAMENTO = '3';
  public static final char STATO_AVANZAM__CHIUSA_CONTABILAMENTO = '4';

  public static final char PIANO_FATT__ASSENTE = '0';
  public static final char PIANO_FATT__DISATTIVO = '1';
  public static final char PIANO_FATT__ATTIVO = '2';

  //Fix 19897 inizio
  //Gestione CIG e CUP
  public static final char DATI_CONVENZIONE = '1';
  public static final char DATI_ORD_ACQ     = '2';
  public static final char DATI_CONTRATTO   = '3';
  public static final char DATI_RiCEZIONE   = '4';
  public static final char DATI_FATT_COLEG  = '5';
  //Fix 19897 fine

  //Fix 29025 - inizio
  public static final char TP_PIANO_NORMALE = '0';
  public static final char TP_PIANO_ACCONTI_SALDO = '1';
  //Fix 29025 - fine
  
  protected boolean iSottocommesseLoaded = false;
  protected boolean iRelatedCommesseLoaded = false;

  protected String iOldIdCommessaAppartenenza = null;
  protected String iOldIdCommessaPrincipale = null;
  protected String iOldIdCliente = null;
  protected String iOldIdTipoCommessa = null;
  protected String iOldIdAmbienteCommessa = null;
  protected String iOldIdAnnoOrdine = null;
  protected String iOldIdNumeroOrdine = null;
  protected WfStatus iOldWfStatus = null;
  protected boolean iOldAggiorSaldo;
  protected char iOldStatoAvanzamento;
  protected char iOldTipoPiano;		//Fix 29025

  //5543 - inizio
  //salvo i dati di descrizione
  private String iOldDescrizione = null;
  private String iOldDescrizioneRidotta = null;

  //5543 - fine

  public boolean iSaveSottocommesse = false; //Mod 04361

  protected boolean iSaveOwnedObject = true; //Fixe 04361

  /**
   * Attributo iSottocommesse
   */
  protected List iSottocommesse = new ArrayList();

  //Fix 04637 Mz inizio
  protected boolean iDaDuplicazioneCommessa = false;

  //Fix 04637 Mz fine

  //Fine 03463 A.Boulila

  protected boolean iDaCMCommessa = false;  //Fix - 9297


//Fix AAAAB Begin

  public static final String DELETE_CMM_EVENTI = "DELETE FROM  " + EventoCommessaTM.TABLE_NAME +
    " WHERE " + EventoCommessaTM.ID_AZIENDA + " = ? " +
    " AND " + EventoCommessaTM.ID_COMMESSA + " = ? ";
  public static CachedStatement deleteCmmEventiStmt = new CachedStatement(DELETE_CMM_EVENTI);
  public static final String DELETE_CMM_RATE = "DELETE FROM  " + RataCommessaTM.TABLE_NAME +
    " WHERE " + RataCommessaTM.ID_AZIENDA + " = ? " +
    " AND " + RataCommessaTM.ID_COMMESSA + " = ? ";

  public static CachedStatement deleteCmmRateStmt = new CachedStatement(DELETE_CMM_RATE);

//Fix AAAAB End

  //...FIX 4709 inizio

  /**
   * String SELECT_AMB_NO_CMM.
   */
  public static final String SELECT_AMB_NO_CMM =
    "SELECT DISTINCT COSTI." + CostoTM.ID_AMBIENTE +
    " FROM " + CostoTM.TABLE_NAME + " COSTI" + //Fix 6958
    " JOIN " + AmbienteCostiTM.TABLE_NAME + " AMBIENTI" + //Fix 6958
    " ON COSTI." + CostoTM.ID_AZIENDA + " = AMBIENTI." + AmbienteCostiTM.ID_AZIENDA +
    " AND COSTI." + CostoTM.ID_AMBIENTE + " = AMBIENTI." + AmbienteCostiTM.ID_AMBIENTE +
    " WHERE COSTI." + CostoTM.ID_AZIENDA + " = ?" +
    " AND COSTI." + CostoArticoloTM.R_COMMESSA + " = ?" +
    " AND AMBIENTI." + AmbienteCostiTM.AMBIENTE_CMM + " = 'N'";

  /**
   * CachedStatement selectAmbientiNoCm
   */
  public static CachedStatement selectAmbientiNoCmm = new CachedStatement(SELECT_AMB_NO_CMM);

  //...FIX 4709 fine

  public Commessa() {
  }

  public String getTableNLSName() {
    return SystemParam.getSchema("THIP") + "COMMESSE_L";
  }

  /**
   * checkDelete
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/12/2001    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete() {
    if(getStatoAvanzamento() == STATO_AVANZAM__CHIUSA_CONTABILAMENTO)
      return new ErrorMessage("THIP20T064", KeyHelper.formatKeyString(getKey()));
    return null;
  }

  //----------------------------------------------------------------------------

  // Inizio 03463 A.Boulila

  /**
   *
   * @return ErrorMessage
   */
  public boolean isAppartenenzaModifiable() {
    if(getStatoAvanzamento() == STATO_AVANZAM__CHIUSA_CONTABILAMENTO)
      return false;
    return true;
  }

  /**
   * checkDateValidite
   * @return ErrorMessage
   */
  public ErrorMessage checkDateValidite() {
    if(getDataApertura() != null
      && getDataChiusura() != null
      && getDataApertura().compareTo(getDataChiusura()) > 0) {
      return new ErrorMessage("THIP20T001");
    }
    return null;
  }

  /**
   * checkDatePreviste
   * @return ErrorMessage
   */
  public ErrorMessage checkDatePreviste() {
    if(getDataInizioPrevista() != null
      && getDataFinePrevista() != null
      && getDataInizioPrevista().compareTo(getDataFinePrevista()) > 0) {
      return new ErrorMessage("THIP20T002");
    }
    return null;
  }

  /**
   * checkIdCommessaAppartenenza
   * @return ErrorMessage
   */
  public ErrorMessage checkIdCommessaAppartenenza() {
    if(getCommessaAppartenenza() != null) {
      Commessa currentCommessaApp = getCommessaAppartenenza();
      while(currentCommessaApp != null) {
        if(currentCommessaApp.equals(this))
          return new ErrorMessage("THIP20T003");
        else
          currentCommessaApp = currentCommessaApp.getCommessaAppartenenza();
      }
    }
    return null;
  }

  //Fix 27669 inizio
  /**
   * checkIdCommessaModello
   * @return ErrorMessage
   */
  public ErrorMessage checkIdCommessaModello() {
    Commessa commessaMod = getCommessaModello();
    if (commessaMod != null) {
      if (getIdCommessa().equals(getIdCommessaModello()))
        return new ErrorMessage("THIP400013");
      else if (!getAggiornamentoSaldi())
        return new ErrorMessage("THIP400014");
      if (!commessaMod.getAggiornamentoSaldi())
        return new ErrorMessage("THIP400015");
    }
    return null;
  }
  //Fix 27669 fine

  
  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkAggiornamentoSaldi() {
    if(!isOnDB()
      && getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
      && !getAggiornamentoSaldi()) {
      return new ErrorMessage("THIP20T004");
    }
    return null;
  }

  /**
   * checkIdAmbienteCommessa
   * @return ErrorMessage
   */
  public ErrorMessage checkIdAmbienteCommessa() {
	if(getTipoCommessa() != null && !getTipoCommessa().isCreaAmbienteCommessa()){//29960
		if(getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE && getIdAmbienteCommessa() == null)
			return new ErrorMessage("THIP20T024");
	}//29960
    return null;
  }

  /**
   * checkIdArticolo
   * @return ErrorMessage
   */
  public ErrorMessage checkIdArticolo() {
    if(getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
      && getStatoAvanzamento() != STATO_AVANZAM__PROVVISORIA
      && getIdArticolo() == null)
      return new ErrorMessage("THIP20T005");
    //Fix 04171 Begin
    /*if(getArticolo() != null){
      ErrorMessage err = checkLivelloMinimoModello();

      if(err != null)
        return err;
         }*/
    //Fix 04171 End
    return null;
  }

  /**
   * checkIdVersione
   * @return ErrorMessage
   */
  public ErrorMessage checkIdVersione() {
    if(getArticolo() != null && getArticolo().getGesVersioni() && getIdVersione() == null)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  /**
   * checkIdConfigurazione
   * @return ErrorMessage
   */
  public ErrorMessage checkIdEsternoConfig() {
    if(getArticolo() != null && getArticolo().getSchemaCfg() != null && getIdEsternoConfig() == null)
      return new ErrorMessage("BAS0000000");
    //Fix 35221 inizio
	  if (getArticolo() != null && getArticolo().isConfigurato()) {
		  //controllo che la configurazione caricata sia relativa all'articolo
		  if (getConfigurazione() != null && !getConfigurazione().getIdArticolo().equals(getArticolo().getIdArticolo())) 
			  return new ErrorMessage("THIP_TN805", new String[]{String.valueOf(getConfigurazione().getIdConfigurazione()), getArticolo().getIdArticolo()});
             }
	//Fix 35221 fine
    return null;
  }

  /**
   * checkLivelloMinimoModello
   * @return ErrorMessage
   */
  public ErrorMessage checkLivelloMinimoModello() {
    if(getArticolo() != null) {
      Integer livMinMod = getArticolo().getLivelloMinimoModello();
      if(getCommessaAppartenenza() != null && getCommessaAppartenenza().getArticolo() != null) {
        Integer livMinCmmApp = getCommessaAppartenenza().getArticolo().getLivelloMinimoModello();
        if(livMinMod.compareTo(livMinCmmApp) <= 0)
          return new ErrorMessage("THIP20T030");
      }

      for(Iterator it = getSottocommesse().iterator(); it.hasNext(); ) {
        Commessa cmm = (Commessa)it.next();
        if(cmm.getArticolo() != null) {
          Integer livMinSotCmm = cmm.getArticolo().getLivelloMinimoModello();
          if(livMinMod.compareTo(livMinSotCmm) >= 0)
            return new ErrorMessage("THIP20T029", cmm.getIdCommessa());
        }
      }
    }
    return null;
  }

  /**
   * checkIdCliente
   * @return ErrorMessage
   */
  public ErrorMessage checkIdCliente() {
    if(getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
      && getStatoAvanzamento() != STATO_AVANZAM__PROVVISORIA
      && getIdCliente() == null)
      return new ErrorMessage("THIP20T024");

    return null;
  }

  /**
   * checkIdResponsabileCommessa
   * @return ErrorMessage
   */
  public ErrorMessage checkIdResponsabileCommessa() {
    if(getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
      && getStatoAvanzamento() != STATO_AVANZAM__PROVVISORIA
      && getIdResponsabileCommessa() == null)
      return new ErrorMessage("THIP20T024");

    return null;
  }

  /**
   * checkIdResponsabilePreventivaz
   * @return ErrorMessage
   */
  //Fix 04361 Begin
  /*public ErrorMessage checkIdResponsabilePreventivaz() {
    if (getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
        && getStatoAvanzamento() != STATO_AVANZAM__PROVVISORIA
        && getIdResponsabilePreventivaz() == null)
      return new ErrorMessage("THIP20T024");

    return null;
     }*/
  //Fix 04361 End

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkPianoFatturazione() {
    if((getNaturaCommessa() != TipoCommessa.NATURA_CMM__GESTIONALE
      || getIdCommessaAppartenenza() != null)
      && compareStatoAvanzamento(getPianoFatturazione(), PIANO_FATT__ASSENTE) > 0)

      return new ErrorMessage("THIP20T025");

    return null;
  }

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkDataConferma() {
    if(compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__CONFERMATA) >= 0 && getDataConferma() == null)
      return new ErrorMessage("THIP20T010");

    if(getCommessaAppartenenza() == null) { //...FIX 9121
      if(getDataConferma() != null && getDataApertura() != null) {
        if(TimeUtils.differenceInDays(getDataConferma(), getDataApertura()) < 0)
          return new ErrorMessage("THIP20T015");
      }
    }
    return null;
  }

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkDataChiusTec() {
    if(compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__CHIUSA_TECNICAMENTO) >= 0 && getDataChiusTec() == null)
      return new ErrorMessage("THIP20T011");

    if(getDataChiusTec() != null && getDataConferma() != null) {
      if(TimeUtils.differenceInDays(getDataChiusTec(), getDataConferma()) < 0)
        return new ErrorMessage("THIP20T016");
    }
    return null;
  }

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkDataChiusOpe() {
    if(compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__CHIUSA_OPERATIVAMENTO) >= 0 && getDataChiusOpe() == null)
      return new ErrorMessage("THIP20T012");

    if(getDataChiusOpe() != null && getDataChiusTec() != null) {
      if(TimeUtils.differenceInDays(getDataChiusOpe(), getDataChiusTec()) < 0)
        return new ErrorMessage("THIP20T017");
    }

    return null;
  }

  /**
   * checkDataChiusura
   * @return ErrorMessage
   */
  public ErrorMessage checkDataChiusura() {
    if(compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__CHIUSA_CONTABILAMENTO) >= 0 && getDataChiusura() == null)
      return new ErrorMessage("THIP20T013");

    if(getDataChiusura() != null && getDataChiusOpe() != null) {
      if(TimeUtils.differenceInDays(getDataChiusura(), getDataChiusOpe()) < 0)
        return new ErrorMessage("THIP20T018");
    }
    //Fix 04361 Begin
    if(compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__CHIUSA_CONTABILAMENTO) >= 0 &&
      getAmbienteCommessa() != null && getAmbienteCommessa().getDataChiusDef() != null &&
      TimeUtils.differenceInDays(getDataChiusura(), getAmbienteCommessa().getDataChiusDef()) <= 0)
      return new ErrorMessage("THIP20T142");
    //Fix 04361 End
    return null;
  }

  /**
   * checkIdNumeroOrdine
   * @return ErrorMessage
   */
  public ErrorMessage checkIdNumeroOrdine() {
    // Inizio 10061
  	if (getRifRigaOrdRiorg() != null)
  		return null;
  	// Fine 10061
  	//Fix 04171 Begin
    if(getOrdineVendita() != null &&
      (getOrdineVendita().getValoreTotOrdinato() == null ||
      getOrdineVendita().getValoreTotOrdinato().compareTo(new BigDecimal("0")) == 0) &&
      getPianoFatturazione() != PIANO_FATT__ASSENTE)
      return new ErrorMessage("THIP20T120");
    //Fix 04171 End
    if((getIdAnnoOrdine() != null && getIdNumeroOrdine() == null)
      || (getIdAnnoOrdine() == null && getIdNumeroOrdine() != null))
      return new ErrorMessage("THIP20T009");

    if(getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
      && getStatoAvanzamento() != STATO_AVANZAM__PROVVISORIA
      && (getIdNumeroOrdine() == null || getIdAnnoOrdine() == null))
      return new ErrorMessage("THIP20T005");

    if(getOrdineVendita() != null) {
      if(getOrdineVendita().getStatoAvanzamento() != StatoAvanzamento.DEFINITIVO
        || getOrdineVendita().getDatiComuniEstesi().getStato() != DatiComuniEstesi.VALIDO)
        return new ErrorMessage("THIP20T006");

      if(getCliente() != null) {
        if(!getOrdineVendita().getClienteKey().equals(getClienteKey()))
          return new ErrorMessage("THIP20T007");
      }

      if(containsExtraRigheFromCmm())
        return new ErrorMessage("THIP20T008");
    }

    return null;
  }

  /**
   * checkStatoAvanzamento
   * @return ErrorMessage
   */
  public ErrorMessage checkStatoAvanzamento() {
    if(getCommessaAppartenenza() != null) {
      if(compareStatoAvanzamento(getStatoAvanzamento(), getCommessaAppartenenza().getStatoAvanzamento()) < 0)
        return new ErrorMessage("THIP20T014");
    }
    // 15938 begin
    /* non va bene da ricontrollare in 405
    if(getStatoAvanzamento() == STATO_AVANZAM__CONFERMATA && !isEsistenoModelloProd())
      return new ErrorMessage("THIP20T015");
      */
    // 15938 end
    return null;

  }

  /**
   * checkIdRigaOrdine
   * @return ErrorMessage
   */
  public ErrorMessage checkIdRigaOrdine() {
    if(getOrdineVendita() != null) {
      int nbRighe = getRigheOrdineValidoMerceServizio(getOrdineVendita()).size();

      if(nbRighe == 1) { //L'ordine contiene una sola riga 'merce'/'servizio'
        if(getIdCommessaAppartenenza() == null) {
          if(getIdRigaOrdine() == null) {
            return new ErrorMessage("THIP20T019");
          }
          else {
            if(!areObjectsEqual(getOrdineVenditaRiga().getIdArticolo(), getIdArticolo()))
              return new ErrorMessage("THIP20T020");

            if(!areObjectsEqual(getOrdineVenditaRiga().getIdVersioneRcs(), getIdVersione()))
              return new ErrorMessage("THIP20T021");

            if(!areObjectsEqual(getOrdineVenditaRiga().getIdConfigurazione(), getIdConfigurazione()))
              return new ErrorMessage("THIP20T022");

            if(!areObjectsEqual(getOrdineVenditaRiga().getIdCommessa(), getIdCommessa()))
              return new ErrorMessage("THIP20T023");
          }
        }
        else {
          if(getIdRigaOrdine() != null) {
            return new ErrorMessage("THIP20T026");
          }
        }
      }
      else if(nbRighe > 1) { //L'ordine contiene può righe 'merce'/'servizio'
        if(getIdCommessaAppartenenza() != null) {
          if(getIdRigaOrdine() != null) {
            if(!areObjectsEqual(getOrdineVenditaRiga().getIdArticolo(), getIdArticolo()))
              return new ErrorMessage("THIP20T020");

            if(!areObjectsEqual(getOrdineVenditaRiga().getIdVersioneRcs(), getIdVersione()))
              return new ErrorMessage("THIP20T021");

            if(!areObjectsEqual(getOrdineVenditaRiga().getIdConfigurazione(), getIdConfigurazione()))
              return new ErrorMessage("THIP20T022");

            if(!areObjectsEqual(getOrdineVenditaRiga().getIdCommessa(), getIdCommessa()))
              return new ErrorMessage("THIP20T023");
          }
        }

        if(getIdCommessaAppartenenza() == null) {
          if(getIdRigaOrdine() != null) {
            return new ErrorMessage("THIP20T027");
          }
        }
      }
    }
    return null;
  }

  /**
   * checkValoreTotaleOrdine
   * @param components BaseComponentsCollection
   * @return ErrorMessage
   */
  public ErrorMessage checkValoreTotaleOrdine(BaseComponentsCollection components) {
    try {
      if(getPianoFatturazione() == PIANO_FATT__ATTIVO
        && compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__PROVVISORIA) > 0) {
        if(getOrdineVendita() != null) {
          if(getValoreTotaleRate().compareTo(getValoreOrdine()) != 0) {
            ErrorMessage err = new ErrorMessage("THIP20T028");
            ClassADCollection cad = ClassADCollectionManager.collectionWithName("Commessa");
            String label = cad.getAttribute("ValoreTotaleOrdine").getAttributeNameNLS();
            err.addComponent("ValoreTotaleOrdine", label, components.getComponent("ValoreTotaleOrdine"));
            return err;
          }
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkChiudiOrdUltimaFat() {
    if(getPianoFatturazione() == PIANO_FATT__ASSENTE && getChiudiOrdUltimaFat()) {
      return new ErrorMessage("THIP20T031");
    }
    return null;
  }

  /**
   *
   * @return BigDecimal
   */
  public BigDecimal getValoreTotaleOrdine() {
    if(getOrdineVendita() != null)
      return getOrdineVendita().getValoreTotOrdinato();
    return new BigDecimal(0);
  }

  /**
   * getValoreOrdine
   * @return BigDecimal
   */
  public BigDecimal getValoreOrdine() {
    if(getOrdineVendita() != null)
      return getOrdineVendita().getValoreTotOrdinato().subtract(getOrdineVendita().getValoreImposta());
    return new BigDecimal(0);
  }

  /**
   * getSommaImportiRate
   * @return BigDecimal
   */
  public BigDecimal getValoreTotaleRate() {
    BigDecimal ret = new BigDecimal(0);
    if(getCommessaPrincipale() != null) {
      for(Iterator it = getCommessaPrincipale().getRateCommesse().iterator(); it.hasNext(); ) {
        RataCommessa rata = (RataCommessa)it.next();
        if(rata.getDatiComuniEstesi().getStato() != DatiComuniEstesi.ANNULLATO)
          ret = ret.add(rata.getImporto());
      }
    }
    return ret;
  }

  /**
   * containsExtraRigheFromCmm
   * @return boolean
   */
  protected boolean containsExtraRigheFromCmm() {
    List relatedCommesse = getRelatedCommesse();

    for(Iterator it = getOrdineVendita().getRigheValide().iterator(); it.hasNext(); ) {
      OrdineVenditaRigaPrm rigaOrd = (OrdineVenditaRigaPrm)it.next();

      if(rigaOrd.getCommessa() == null)
        return true;

      if(containsExtraRigheFromCmm(relatedCommesse, rigaOrd))
        return true;
    }
    return false;
  }

  /**
   * compareObjects
   * @param obj1 Object
   * @param obj2 Object
   * @return boolean
   */
  public static boolean areObjectsEqual(Object obj1, Object obj2) {

    if(obj1 == null && obj2 != null)
      return false;

    if(obj1 != null && obj2 == null)
      return false;

    if(obj1 == null && obj2 == null)
      return true;

    return(obj1.equals(obj2));
  }

  /**
   *
   * @param ordVen OrdineVendita
   * @return List
   */
  public static List getRigheOrdineValidoMerceServizio(OrdineVendita ordVen) {
    List ret = new ArrayList();
    for(Iterator it = ordVen.getRigheValide().iterator(); it.hasNext(); ) {
      OrdineVenditaRigaPrm riga = (OrdineVenditaRigaPrm)it.next();
      if(riga.getTipoRiga() == TipoRiga.MERCE || riga.getTipoRiga() == TipoRiga.SERVIZIO)
        ret.add(riga);
    }
    return ret;
  }

  /**
   * containsExtraRigheFromCmm
   * @param cmmList List
   * @param rigaOrd OrdineVenditaRigaPrm
   * @return boolean
   */
  protected boolean containsExtraRigheFromCmm(List cmmList, OrdineVenditaRigaPrm rigaOrd) {

    for(Iterator it = cmmList.iterator(); it.hasNext(); ) {
      Commessa cmm = (Commessa)it.next();

      if(rigaOrd.getCommessa().equals(cmm))
        return false;
    }
    return true;
  }

  /**
   * Override of save method
   * @throws SQLException
   * @return int
   */
  public int save() throws SQLException {
    int ret1 = 0;
    int ret2 = 0;

    //5543 - inizio
    gestioneCommessaAnalitica();
    //5543 - fine

    //Fix 30948 Inizio
    //creaAmbienteCommessa();//29960
    //if(getIdCommessaPrincipale() == null)//Fix 31596
    if(getIdCommessaPrincipale() == null || getIdCommessaPrincipale().equals(getIdCommessa())) //Fix 31596
    	creaAmbienteCommessa();
    //else //Fix 31209
    	//setIdAmbienteCommessa(getCommessaPrincipale().getIdAmbienteCommessa()); //Fix 31209
    //Fix 30948 Fine
    
    beforeSave();
    //Fix 04361 Begin
    if(getIdArticolo() != null && getIdVersione() == null)
      setIdVersione(new Integer("1"));
      //Fix 04361 End
    if(getCommessaAppartenenza() != null) {
      setIdCommessaPrincipale(getCommessaAppartenenza().getIdCommessaPrincipale());
      setLivelloCommessa(new Integer(getCommessaAppartenenza().getLivelloCommessa().intValue() + 1));
      ret1 = super.save();
      if(ret1 < 0)
        return ret1;
    }
    else {
      setLivelloCommessa(new Integer("0"));
      //Fix 04361 Begin
      //if (getIdArticolo() != null && getIdVersione() == null)
      //  setIdVersione(new Integer("1"));
      //Fix 04361 End
      ret1 = super.save();
      if(ret1 < 0)
        return ret1;

      setIdCommessaPrincipale(getIdCommessa());
      //31124 inizio
      if(getTextNLSHandler().isMultilingual() && !getTextNLSHandler().isDefaultLanguage()) {
    	  TextNLSRow userTextNLSRow = getDescrizione().getHandler().findTextNLSFor(Security.getCurrentUser().getLanguage());
    	  String[] textsForUserLanguage = (userTextNLSRow == null) ? null : userTextNLSRow.getTexts();
  		  getDescrizione().setDescrizione(textsForUserLanguage[0]);
  		  getDescrizione().setDescrizioneRidotta(textsForUserLanguage[1]);
	  }
      //31124 fine
      ret2 = super.save();
      if(ret2 < 0)
        return ret2;
    }
    int retPrg = 0;
    if(isOnDB()) {
      if(iSaveSottocommesse) {
        propagateModifications();
        retPrg = saveSottocommesse();
        if(retPrg < 0)
          return retPrg;
      }
    }
    int rcDel = 0;
    if(getPianoFatturazione() == PIANO_FATT__ASSENTE) {
      if(!getRateCommesseInternal().isEmpty()) {
        rcDel = getRateCommesseInternal().delete();
        if(rcDel < 0)
          return rcDel;
      }

//      rcDel = iRateCommesse.delete();
//      if(rcDel < 0)
//        return rcDel;
    }

    return ret1 + ret2 + retPrg + rcDel;
  }

  /**
   *
   * @return int
   */
  protected int beforeSave() {
    //Fix 04637 Mz inizio
    if(!isDaDuplicazioneCommessa()) {
      if(!isOnDB()) {
        if(!isDaCMCommessa()) {    //Fix 09297
          resetWfStatus();
        }
        getRateCommesse().clear();
        //Fix 04599 Begin
        impostaDataConfermaFromAppertenenza();
        //Fix 04599 End
      }
      else
        verifyWorkflow();

      impostaDataConfermaSottocommessa(); //...FIX 9121

    }
    //Fix 04637 Mz inizio

    if(getCommessaAppartenenza() != null || getStatoAvanzamento() == STATO_AVANZAM__PROVVISORIA)
      setPianoFatturazione(PIANO_FATT__ASSENTE);
    return 0;
  }

  public void resetWfStatus() {
//    getWfStatus().setWfId(null);
//    getWfStatus().setCurrentNode(null);
    initializeWorkflow();
  }

  /**
   * saveSottocommesse
   * @throws SQLException
   * @return int
   */
  protected int saveSottocommesse() throws SQLException {
    int ret = 0;
    for(Iterator it = getSottocommesse().iterator(); it.hasNext(); ) {
      Commessa cmm = (Commessa)it.next();
      cmm.iSaveSottocommesse = cmm.propagateModifications(); //Mod 04361
      int ret1 = cmm.save();
      if(ret1 < 0)
        return ret1;
      ret += ret1;
    }
    return ret;
  }

  /**
   * propagateModifications
   * @return int
   */
  protected boolean propagateModifications() {
    try {
      boolean updateCmmAppData = isAppartenenzaDataModified();
      boolean updateStatoAv = iOldStatoAvanzamento != getStatoAvanzamento();
      boolean updateNullDate = isSottocommesseNullDatePropagate();
      if(updateCmmAppData || updateStatoAv || updateNullDate) {
        for(Iterator it = getSottocommesse().iterator(); it.hasNext(); ) {
          Commessa cmm = (Commessa)it.next();
          cmm.getWfStatus().setEqual(getWfStatus());
          if(updateNullDate) {
            if(cmm.getIdResponsabileCommessa() == null)
              cmm.setIdResponsabileCommessa(getIdResponsabileCommessa());
            if(cmm.getIdResponsabilePreventivaz() == null)
              cmm.setIdResponsabilePreventivaz(getIdResponsabilePreventivaz());
            if(cmm.getIdStabilimento() == null)
              cmm.setIdStabilimento(getIdStabilimento());
          }
          if(updateCmmAppData) {
            cmm.setIdCommessaPrincipale(getIdCommessaPrincipale());
            cmm.setIdCliente(getIdCliente());
            cmm.setIdTipoCommessa(getIdTipoCommessa());
            cmm.setIdAmbienteCommessa(getIdAmbienteCommessa());
            cmm.setIdAnnoOrdine(getIdAnnoOrdine());
            cmm.setIdNumeroOrdine(getIdNumeroOrdine());

            if(!areObjectsEqual(iOldIdAnnoOrdine, getIdAnnoOrdine())
              || !areObjectsEqual(iOldIdNumeroOrdine, getIdNumeroOrdine())
              || !areObjectsEqual(iOldIdCommessaAppartenenza, getIdCommessaAppartenenza())) {

              cmm.setIdRigaOrdine(null);
              cmm.setIdDetRigaOrdine(null);

            }
            cmm.setAggiornamentoSaldi(getAggiornamentoSaldi());
          }
          //begin Mod 04361
          if(compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__PROVVISORIA) == 0) {
            cmm.setStatoAvanzamento(getStatoAvanzamento());
            cmm.setDataConferma(null);
            cmm.setDataChiusTec(null);
            cmm.setDataChiusOpe(null);
            cmm.setDataChiusura(null);
          }
          else
            if(compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__CHIUSA_OPERATIVAMENTO) == 0) {
              cmm.setStatoAvanzamento(getStatoAvanzamento());
              if(cmm.getDataConferma() == null)
                cmm.setDataConferma(getDataConferma());
              if(cmm.getDataChiusTec() == null)
                cmm.setDataChiusTec(getDataChiusTec());
              if(cmm.getDataChiusOpe() == null)
                cmm.setDataChiusOpe(getDataChiusOpe());
              cmm.setDataChiusura(null);
            }
            else {
              if(updateStatoAv && compareStatoAvanzamento(cmm.getStatoAvanzamento(), getStatoAvanzamento()) < 0) {
                //end Mod 04361
                cmm.setStatoAvanzamento(getStatoAvanzamento());
                //Fix 04567 Mz inizio
                //if (cmm.getDataConferma() == null)
                //  cmm.setDataConferma(getDataConferma());
                //Fix 04567 Mz fine
                if(cmm.getDataChiusTec() == null)
                  cmm.setDataChiusTec(getDataChiusTec());
                if(cmm.getDataChiusOpe() == null)
                  cmm.setDataChiusOpe(getDataChiusOpe());
                if(cmm.getDataChiusura() == null)
                  cmm.setDataChiusura(getDataChiusura());
              }
            }
          //Fix 04567 Mz inizio
          cmm.setDataConferma(getDataConferma());
          //Fix 04567 Mz fine
        }
      }
      return updateCmmAppData || updateStatoAv || updateNullDate;
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return false;
  }

  /**
   * Override delete method
   * @throws SQLException
   * @return int
   */
  public int delete() throws SQLException {
    //Fix 04599 Mz inizio
    //int rc1 = super.delete();
    int rcSottoCommesse = deleteSottoCommesse();
    if(rcSottoCommesse < 0)
      return rcSottoCommesse;
    //Fix 04599 Mz fine
    int rc1 = super.delete();
    if(rc1 < 0)
      return rc1;

    int rcc = iCommentiManager.delete();
    if(rcc < 0)
      return rcc;

    return rcc + rc1 + rcSottoCommesse;
  }

  //Fix 04599 Mz inizio
  public int deleteSottoCommesse() throws SQLException {
    int rc = ErrorCodes.NO_ROWS_UPDATED;
    Iterator sottoCommesse = getSottocommesse().iterator();
    while(sottoCommesse.hasNext()) {
      Commessa commessa = (Commessa)sottoCommesse.next();
      rc = commessa.delete(rc);
    }
    return rc;
  }

  //Fix 04599 Mz fine

  /**
   *
   * @param stato1 char
   * @param stato2 char
   * @return boolean: if stato1 is previous to stato2 returns -1 if equal returns 0 ele return 1
   */
  public static int compareStatoAvanzamento(char stato1, char stato2) {
    Integer intStato1 = new Integer(stato1);
    Integer intStato2 = new Integer(stato2);
    return intStato1.compareTo(intStato2);
  }

  /**
   * getRelatedCommesse
   * @return List
   */
  public List getRelatedCommesse() {
    String idCommessaPrincipale = null;
    if(getCommessaAppartenenza() != null) {
      idCommessaPrincipale = getCommessaAppartenenza().getIdCommessaPrincipale();
    }
    if(idCommessaPrincipale == null)
      idCommessaPrincipale = getIdCommessa();

    String where = CommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "
      + CommessaTM.R_COMMESSA_PRM + "='" + idCommessaPrincipale + "'";

    try {
      return Commessa.retrieveList(Commessa.class, where, "", false);
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;

  }

  /**
   * getSottocommesseDaPrincipale
   * @return List
   */
  public List loadSottocommesseDaPrincipale() {
    try {
      String where = CommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' AND " +
        CommessaTM.R_COMMESSA_PRM + " = '" + getIdCommessa() + "'";
      return Commessa.retrieveList(where, "", false);
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream);
      return null;
    }
  }

  /**
   * getSottocommesseDaPrincipale
   * @return List
   */
  public List loadSottocommesseDirette() {
    try {
      String where = CommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' AND " +
        CommessaTM.R_COMMESSA_APP + " = '" + getIdCommessa() + "'";
      return Commessa.retrieveList(where, "", false);
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream);
      return null;
    }
  }

  /**
   *
   * @return List
   */
  public List getSottocommesse() {
    loadSottocommesse();
    return iSottocommesse;
  }

  /**
   *
   * @param sottocommesse List
   */
  public void setSottocommesse(List sottocommesse) {
    iSottocommesse = sottocommesse;
  }

  /**
   * loadSottocommesse
   */
  public void loadSottocommesse() {
    if(!isOnDB())
      return;

    if(!iSottocommesseLoaded) {
      String where = CommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "
        + CommessaTM.R_COMMESSA_APP + "='" + getIdCommessa() + "'";
      try {
        iSottocommesse = Commessa.retrieveList(Commessa.class, where, CommessaTM.ID_COMMESSA, false);
        for(Iterator it = iSottocommesse.iterator(); it.hasNext(); ) {
          Commessa cmm = (Commessa)it.next();
          cmm.loadSottocommesse();
        }
        iSottocommesseLoaded = true;
      }
      catch(Exception ex) {
        ex.printStackTrace(Trace.excStream); //...FIX 7430
      }
    }
  }

  /**
   * @param lockType int
   * @throws SQLException
   * @return boolean
   */
  /* Fix 09821 FR : commentato metodo
  public boolean retrieve(int lockType) throws SQLException {
    boolean ret = super.retrieve(lockType);
    initializeWorkflow();
    saveOldInfo();

    return ret;
  }*/

  /**
   * Ridefinizione
   * @see it.thera.thip.base.commessa.CommessaPO#initializeOwnedObjects(boolean)
   * @author Fix 09821 FR
   */
  public boolean initializeOwnedObjects(boolean ret) {
	  ret = ret && super.initializeOwnedObjects(ret);
	  if (ret){
		  initializeWorkflow();
		  saveOldInfo();
	  }
	  return ret;
  }

  /**
   * Override of setIdTipoCommessa method
   * @param idTipoCommessa String
   */
  /*  public void setIdTipoCommessa(String idTipoCommessa) {
      super.setIdTipoCommessa(idTipoCommessa);
      initializeWorkflow();
    }*/

  /**
   * initializeWorkflow
   */
  protected void initializeWorkflow() {
    if(!isOnDB()) {
      initializeWorkflowInternal();
    }
  }

  public void initializeWorkflowInternal() {
    if((getTipoCommessa() == null) || (getTipoCommessa().getWfSpecifico() == null)) {
      iWfStatus.setWfClassId(WfClass.ID_DEF_VALUE);
      iWfStatus.setWfId(null);
      iWfStatus.setCurrentNodeId(null);
      iWfStatus.setCurrentSubNodeId(WfSpecNode.DEF_SUB_NODE_ID);
    }
    else {
      WfSpecific wfSpec = getTipoCommessa().getWfSpecifico();
      iWfStatus.setWfClassId(wfSpec.getWfClassId());
      iWfStatus.setWfId(wfSpec.getWfId());
      iWfStatus.setCurrentNode(iWfStatus.getWfSpecific().getInitialNode());
      if(iWfStatus.getWfSpecific().getInitialNode() != null)
        iWfStatus.setCurrentSubNodeId(iWfStatus.getWfSpecific().getInitialNode().getWfSubNodeId());
    }

  }

  public void verifyWorkflow() {
    Commessa dbImage = null;

    try {
      dbImage = Commessa.elementWithKey(getKey(), PersistentObject.NO_LOCK);
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    if(dbImage != null) {
      if(dbImage.getWfStatus().getWfId() == null)

        //Fix 04599 Mz inizio
        //initializeWorkflow();
        initializeWorkflowInternal();
        //Fix 04599 Mz fine

      else {
        if((getTipoCommessa() == null) || (getTipoCommessa().getWfSpecifico() == null)) {
          //do nothing
        }
        else {
          if(Utils.compare(dbImage.getWfStatus().getWfId(), getTipoCommessa().getIdWfSpec()) != 0) {
            //Fix 04599 Mz inizio
            //initializeWorkflow();
            if (iWfStatus.getCurrentNode() != null && iWfStatus.getCurrentNode().equals(iWfStatus.getWfSpecific().getInitialNode())) // Fix 22268
              initializeWorkflowInternal();
            //Fix 04599 Mz fine
          }
        }
      }
    }
  }

  /**
   * saveOldInfo
   */
  protected void saveOldInfo() {
    iOldIdCommessaPrincipale = getIdCommessaPrincipale();
    iOldIdCliente = getIdCliente();
    iOldIdTipoCommessa = getIdTipoCommessa();
    iOldIdAmbienteCommessa = getIdAmbienteCommessa();
    iOldIdAnnoOrdine = getIdAnnoOrdine();
    iOldIdNumeroOrdine = getIdNumeroOrdine();
    iOldAggiorSaldo = getAggiornamentoSaldi();
    iOldStatoAvanzamento = getStatoAvanzamento();
    iOldWfStatus = getWfStatus();

    iOldDescrizione = getDescrizione().getDescrizione();
    iOldDescrizioneRidotta = getDescrizione().getDescrizioneRidotta();
	iOldTipoPiano = getTipoPiano();
  }

  /**
   * isAppartenenzaDataModified
   * @return boolean
   */
  protected boolean isAppartenenzaDataModified() {
    if(!areObjectsEqual(iOldIdCommessaAppartenenza, getIdCommessaAppartenenza())
      || !areObjectsEqual(iOldIdCommessaPrincipale, getIdCommessaPrincipale())
      || !areObjectsEqual(iOldIdCliente, getIdCliente())
      || !areObjectsEqual(iOldIdTipoCommessa, getIdTipoCommessa())
      || !areObjectsEqual(iOldIdAmbienteCommessa, getIdAmbienteCommessa())
      || !areObjectsEqual(iOldIdAnnoOrdine, getIdAnnoOrdine())
      || !areObjectsEqual(iOldIdNumeroOrdine, getIdNumeroOrdine())
      || !(new Boolean(iOldAggiorSaldo).equals(new Boolean(getAggiornamentoSaldi()))))
      return true;

    return false;
  }

  /**
   *
   * @return boolean
   */
  protected boolean isSottocommesseNullDatePropagate() {
    if(getIdResponsabileCommessa() != null
      || getIdResponsabilePreventivaz() != null
      || getIdStabilimento() != null)
      return true;
    return false;
  }

  /**
   * Override of checkAll method
   * @param components BaseComponentsCollection
   * @return Vector
   */
  public Vector checkAll(BaseComponentsCollection components) {
    try {
      boolean isRecursive = checkIdCommessaAppartenenza() != null;
      Integer idVersione = getIdVersione();
      if(!isRecursive && isOnDB() && isDirty()) {
        iSaveOwnedObject = false; //Fix 04361
        // save(); // Fix 8779 - rimossa save()
        setIdVersione(idVersione);
      }

      //Porpagte data from appartenenza before check
      iSaveSottocommesse = propagateModifications();
      if(!isRecursive && iSaveSottocommesse && isOnDB())
        saveSottocommesse();

      Vector ret = super.checkAll(components);

      //...FIX 4810
      ErrorMessage error = checkRateCollegateAdOrdine(components);
      if(error != null)
        ret.add(error);
        //...FIX 4810

      ErrorMessage err = checkValoreTotaleOrdine(components);
      if(err != null)
        ret.add(err);

      //Fix 31437 inizio
      ErrorMessage er = checkDocCollegate();
      if(er != null)
        ret.add(er);
      //Fix 31437 fine
      iSaveOwnedObject = true; //Fix 04361
      return ret;
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
      iSaveOwnedObject = true; //Fix 04361
      return null;
    }

  }

  public int saveOwnedObjects(int rc) throws SQLException {
    if(iSaveOwnedObject)
      return super.saveOwnedObjects(rc);
    else
      return rc;
  }

  /**
   * setIdAzienda
   * @param idAzienda String
   */
  public void setIdAzienda(String idAzienda) {
    super.setIdAzienda(idAzienda);
    iSottocommesseLoaded = false;
  }

  /**
   * setIdCommessa
   * @param idCommessa String
   */
  public void setIdCommessa(String idCommessa) {
    super.setIdCommessa(idCommessa);
    iSottocommesseLoaded = false;
  }

  /**
   * getAllSottocommesse
   * @return List
   */
  public List getAllSottocommesse() {
    List ret = new ArrayList();

    //...Mi faccio dare le sottocommesse dirette della sottocommessa che sto considerando
    List lista = getSottocommesse();

    for(int i = 0; i < lista.size(); i++) {
      Commessa cmm = (Commessa)lista.get(i);
      ret.add(cmm);
      appendList(ret, cmm.getAllSottocommesse());
    }
    return ret;
  }

  /**
   * getAllSottocommesse
   * @return List
   */
  public List getAllSottocommesseId() {
    List ret = new ArrayList();

    //...Mi faccio dare le sottocommesse dirette della sottocommessa che sto considerando
    List lista = getSottocommesse();

    for(int i = 0; i < lista.size(); i++) {
      Commessa cmm = (Commessa)lista.get(i);
      ret.add(cmm.getIdCommessa());
      appendList(ret, cmm.getAllSottocommesseId());
    }
    return ret;
  }

  /**
   * appendList
   * @param list1 List
   * @param list2 List
   */
  public static void appendList(List list1, List list2) {
    Iterator it2 = list2.iterator();
    while(it2.hasNext()) {
      list1.add(it2.next());
    }
  }

  /**
   * getNaturaCommessa
   * @return char
   */
  public char getNaturaCommessa() {
    return getTipoCommessa() != null ? getTipoCommessa().getNaturaCommessa() : TipoCommessa.NATURA_CMM__TECNICA;
  }

  /**
   * isInShowOnly
   * @return boolean
   */
  public boolean isInShowOnly() {
    if(getStatoAvanzamento() == STATO_AVANZAM__CHIUSA_CONTABILAMENTO)
      return true;
    return false;
  }

  public String getNodeDescription() {
    return getIdCommessa() + " - " + getDescrizione().getDescrizione();
  }

  /**
   * impostaValoreDiAppartenenza
   */
  public void impostaValoreDiAppartenenza() {
    if(getCommessaAppartenenza() != null) {
      setCommessaPrincipale(getCommessaAppartenenza().getCommessaPrincipale());
      setCliente(getCommessaAppartenenza().getCliente());
      setTipoCommessa(getCommessaAppartenenza().getTipoCommessa());
      setAmbienteCommessa(getCommessaAppartenenza().getAmbienteCommessa());
      setAggiornamentoSaldi(getCommessaAppartenenza().getAggiornamentoSaldi());
      setOrdineVendita(getCommessaAppartenenza().getOrdineVendita());
      setIdRigaOrdine(null);
      setIdDetRigaOrdine(null);

      if(getResponsabileCommessa() == null)
        setResponsabileCommessa(getCommessaAppartenenza().getResponsabileCommessa());

      if(getResponsabilePreventivaz() == null)
        setResponsabilePreventivaz(getCommessaAppartenenza().getResponsabilePreventivaz());

      if(compareStatoAvanzamento(getStatoAvanzamento(), getCommessaAppartenenza().getStatoAvanzamento()) < 0) {
        setStatoAvanzamento(getCommessaAppartenenza().getStatoAvanzamento());
        if(getDataConferma() == null)
          setDataConferma(getCommessaAppartenenza().getDataConferma());
        if(getDataChiusTec() == null)
          setDataChiusTec(getCommessaAppartenenza().getDataChiusTec());
        if(getDataChiusOpe() == null)
          setDataChiusOpe(getCommessaAppartenenza().getDataChiusOpe());
        if(getDataChiusura() == null)
          setDataChiusura(getCommessaAppartenenza().getDataChiusura());
      }
    }
    else {
      setOrdineVendita(null);
      setIdRigaOrdine(null);
      setIdDetRigaOrdine(null);
    }
  }

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkIdStabilimento() {
    if(getTipoCommessa() != null
      && getTipoCommessa().getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
      && getStatoAvanzamento() != STATO_AVANZAM__PROVVISORIA
      && getIdStabilimento() == null)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  // Fine 03463 A.Boulila

  //Fix 04334 AB
  /**
   * cancella
   * @param commessa Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancella(Commessa commessa) {
    if(commessa == null)
      return null;
    ErrorMessage ret = null;
    int rc = 0;
    ret = isDeletebleCommessa(commessa);
    if(ret != null)
      return ret;
    ret = cancellaSaldiCommesse(commessa);
    if(ret != null)
      return ret;
    ret = cancellaAmbienteCostiAndCostiAndCostiDet(commessa);
    if(ret != null)
      return ret;
    ret = cancellaCostiCommessa(commessa);
    if(ret != null)
      return ret;
    ret = cancellaModelliProduttivo(commessa);
    if(ret != null)
      return ret;
    ret = cancellaStoriciCommesse(commessa);
    if(ret != null)
      return ret;
    ret = cancellaCmmPrmESottoCmm(commessa);
    if(ret != null)
      return ret;
    return null;
  }

  /**
   * areDeletebleSaldiCommesse
   * @param commessa commessa
   * @return int
   *
   */
  public static ErrorMessage areDeletebleSaldiCommesse(Commessa commessa) {
    ErrorMessage ret = null;
    try {
      if(commessa == null)
        return null;
      String where = SaldoMagLottoCommessaTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + SaldoMagLottoCommessaTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'";
      List saldi = SaldoMagLottoCommessa.retrieveList(where, "", false);
      if(saldi == null || saldi.isEmpty())
        return null;
      for(Iterator i = saldi.iterator(); i.hasNext(); ) {
        SaldoMagLottoCommessa saldo = (SaldoMagLottoCommessa)i.next();
        ErrorMessage err = saldo.checkSaldoMovimentato();
        if(err != null)
          return err;
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  /**
   * isDeletebleCommessa
   * @param commessa commessa
   * @return int
   **/
  public static ErrorMessage isDeletebleCommessa(Commessa commessa) {
    if(commessa == null)
      return null;
    if(!isCommessa_E_SottoCommessa_Provvisorie(commessa))
      return new ErrorMessage("THIP20T104");
    else {
      ErrorMessage err = areDeletebleSaldiCommesse(commessa);
      if(err != null)
        return err;
      //...fix 4709 inizio
      err = controllaCostiInAmbientiSenzaCommessa(commessa);
      if(err != null)
        return err;
      //...fix 4709 fine
      else {
        List list = commessa.getAllSottocommesse();
        if(list == null || list.isEmpty())
          return null;

        for(Iterator i = list.iterator(); i.hasNext(); ) {
          Commessa child = (Commessa)i.next();
          ErrorMessage ret = areDeletebleSaldiCommesse(child);
          if(ret != null)
            return ret;
          //...fix 4709 inizio
          ret = controllaCostiInAmbientiSenzaCommessa(child);
          if(ret != null)
            return ret;
          //...fix 4709 fine
        }
        return null;
      }
    }
  }

  /**
   * areDeletebleSaldiCommesse
   * @param commessa commessa
   * @return int
   *
   */
  public static ErrorMessage controllaCostiInAmbientiSenzaCommessa(Commessa commessa) {
    ErrorMessage ret = null;
    try {
      if(commessa == null)
        return null;
      PreparedStatement ps = selectAmbientiNoCmm.getStatement();
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(ps, 1, commessa.getIdAzienda());
      db.setString(ps, 2, commessa.getIdCommessa());
      ResultSet rs = ps.executeQuery();
      String listaAmbienti = "";
      while(rs.next()) {
        if(!listaAmbienti.equals(""))
          listaAmbienti += ",";
        listaAmbienti = rs.getString(CostoTM.ID_AMBIENTE).trim();
      }

      if(!listaAmbienti.equals(""))
        ret = new ErrorMessage("THIP200095", listaAmbienti);
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  public static boolean isCommessa_E_SottoCommessa_Provvisorie(Commessa commessaPrimaria) {
    boolean ret = commessaPrimaria.getStatoAvanzamento() == Commessa.STATO_AVANZAM__PROVVISORIA;
    for(Iterator iter = commessaPrimaria.getAllSottocommesse().iterator(); ret && iter.hasNext(); ) {
      Commessa sottoCommessa = (Commessa)iter.next();
      ret = sottoCommessa.getStatoAvanzamento() == Commessa.STATO_AVANZAM__PROVVISORIA;
    }
    return ret;
  }

  /**
   * cancellaSaldiCommessa
   * @param commessa commessa
   * @return int
   **/
  public static ErrorMessage cancellaSaldiCommessa(Commessa commessa) {
    ErrorMessage ret = null;
    int rc = 0;
    try {
      if(commessa == null)
        return null;
      String where = SaldoMagLottoCommessaTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + SaldoMagLottoCommessaTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'";
      List saldi = SaldoMagLottoCommessa.retrieveList(where, "", false);
      if(saldi == null || saldi.isEmpty())
        return null;
      for(Iterator i = saldi.iterator(); i.hasNext(); ) {
        SaldoMagLottoCommessa saldo = (SaldoMagLottoCommessa)i.next();
        rc = saldo.delete();
        //Fix AB 04677 Begin
//        if(rc < 0)
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND)
        //Fix AB 04677 End
        {
          ret = getErrorMessageFromSqlResult(rc, saldo.getException());
          return ret;
        }
      }
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  /**
   * cancellaSaldiCommesse
   * @param commessaPrm commessaPrm
   * @return int
   **/
  public static ErrorMessage cancellaSaldiCommesse(Commessa commessaPrm) {
    ErrorMessage ret = null;
    int rc = 0;
    try {
      if(commessaPrm == null)
        return null;
      String where = SaldoMagLottoCommessaTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + SaldoMagLottoCommessaTM.ID_COMMESSA + " = '" + commessaPrm.getIdCommessa() + "'";
      List saldi = SaldoMagLottoCommessa.retrieveList(where, "", false);
      if(saldi == null || saldi.isEmpty())
        return null;
      for(Iterator i = saldi.iterator(); i.hasNext(); ) {
        SaldoMagLottoCommessa saldo = (SaldoMagLottoCommessa)i.next();
        rc = saldo.delete();
//Fix AB 04677 Begin
//        if(rc < 0)
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND)
//Fix AB 04677 End
        {
          ret = getErrorMessageFromSqlResult(rc, saldo.getException());
          return ret;
        }
      }
      List sottoCommesse = commessaPrm.getAllSottocommesse();
      for(Iterator i = sottoCommesse.iterator(); i.hasNext(); ) {
        Commessa child = (Commessa)i.next();
        ret = cancellaSaldiCommessa(child);
        if(ret != null)
          return ret;
      }
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

//Fix 04567 AB begin
  /**
   * updateUtenteAmbiente
   * @param idAmbienteCosti String
   * @return int
   */
  public static int updateUtenteAmbiente(String idAmbienteCosti) {
    try {
      if(idAmbienteCosti == null)
        return 0;
      String user = ((ThipUser)Security.getCurrentUser()).getUtenteAzienda().getIdUtente();
      String sqlUpdateStm =
        " UPDATE " + UtenteAmbientiTM.TABLE_NAME + " SET " + UtenteAmbientiTM.R_AMBIENTE_COSTI + " = NULL " +
        " WHERE " + UtenteAmbientiTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "' " +
        " AND " + UtenteAmbientiTM.ID_UTENTE + " = '" + user + "'" +
        " AND " + UtenteAmbientiTM.R_AMBIENTE_COSTI + " = '" + idAmbienteCosti + "'";
      CachedStatement updateStm = new CachedStatement(sqlUpdateStm);
      return updateStm.executeUpdate();
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return 0;
  }

  //Fix 04567 AB end
  /**
   * cancellaAmbienteCostiAndCostiAndCostiDet
   * @param commessa Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancellaAmbienteCostiAndCostiAndCostiDet(Commessa commessa) {
    int rc = 0;
    ErrorMessage ret = null;
    try {
      if(commessa == null)
        return null;
      String where = AmbienteCostiTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + AmbienteCostiTM.R_COMMESSA + " = '" + commessa.getIdCommessa() + "'";
      List ambienteCosti = AmbienteCosti.retrieveList(where, "", false);
      if(ambienteCosti == null || ambienteCosti.isEmpty())
        return null;
//Fix AB 04677 Begin
      /*
             for (Iterator i = ambienteCosti.iterator(); rc >= 0 && i.hasNext(); )
             {
        AmbienteCosti ambCosto = (AmbienteCosti)i.next();
//Fix 04567 AB begin
        rc = updateUtenteAmbiente(ambCosto.getIdAmbiente());
//Fix 04567 AB end
        //cancella Costi et CostiDettaglio del ambiente corrente
        if(rc >= 0)
          rc =  ambCosto.deleteTuttiCosti(Azienda.getAziendaCorrente(),ambCosto.getIdAmbiente());
        else
          ret = getErrorMessageFromSqlResult(rc,ambCosto.getException());
        if(rc >= 0)
          rc = ambCosto.delete();
        if(rc < 0)
          ret = getErrorMessageFromSqlResult(rc,ambCosto.getException());
             }
       */
      boolean inError = false;
      for(Iterator i = ambienteCosti.iterator(); !inError && i.hasNext(); ) {
        AmbienteCosti ambCosto = (AmbienteCosti)i.next();
//Fix 04567 AB begin
        rc = updateUtenteAmbiente(ambCosto.getIdAmbiente());
//Fix 04567 AB end
        //cancella Costi et CostiDettaglio del ambiente corrente
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND) {
          ret = getErrorMessageFromSqlResult(rc, ambCosto.getException());
          inError = true;
        }
        else
          rc = ambCosto.deleteTuttiCosti(Azienda.getAziendaCorrente(), ambCosto.getIdAmbiente());

        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND) {
          ret = getErrorMessageFromSqlResult(rc, ambCosto.getException());
          inError = true;
        }
        else
          rc = ambCosto.delete();
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND) {
          ret = getErrorMessageFromSqlResult(rc, ambCosto.getException());
          inError = true;
        }
      }
      //Fix AB 04677 End
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  public static ErrorMessage cancellaCostiCommessa(Commessa commessa) {
    int rc = 0;
    ErrorMessage ret = null;
    try {
      if(commessa == null)
        return null;
      String where = CostiCommessaTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + CostiCommessaTM.R_COMMESSA_PRM + " = '" + commessa.getIdCommessa() + "'";
      List costiCmm = CostiCommessa.retrieveList(where, "", false);
      if(costiCmm == null || costiCmm.isEmpty())
        return null;
//Fix AB 04677 Begin
//      for (Iterator i = costiCmm.iterator(); rc >=0 && i.hasNext(); )
      boolean inError = false;
      for(Iterator i = costiCmm.iterator(); !inError && i.hasNext(); )
//Fix AB 04677 End
      {
        CostiCommessa costoCmm = (CostiCommessa)i.next();
        //cancella CostiCommessa e CostiCommessaElem é CostiCommessaDet
        rc = costoCmm.delete();
//Fix AB 04677 Begin
//        if(rc < 0)
//          ret = getErrorMessageFromSqlResult(rc,costoCmm.getException());
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND) {
          inError = true;
          ret = getErrorMessageFromSqlResult(rc, costoCmm.getException());
        }
//Fix AB 04677 End
      }
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  /**
   * cancellaModelliProduttivo
   * @param commessaPrm Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancellaModelliProduttivo(Commessa commessaPrm) {
    try {
      if(commessaPrm == null)
        return null;
      String where = it.thera.thip.datiTecnici.modpro.ModelloProduttivoTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + ModelloProduttivoTM.R_COMMESSA + " = '" + commessaPrm.getIdCommessa() + "'";
      List modelli = ModelloProduttivo.retrieveList(where, "", false);
      if(modelli == null || modelli.isEmpty())
        return null;
      for(Iterator i = modelli.iterator(); i.hasNext(); ) {
        ModelloProduttivo modello = (ModelloProduttivo)i.next();
        int rc = modello.delete();
//Fix AB 04677 Begin
//        if(rc < 0)
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND)

//Fix AB 04677 end
          return getErrorMessageFromSqlResult(rc, modello.getException());
      }
      List sottoCommesse = commessaPrm.getAllSottocommesse();
      for(Iterator i = sottoCommesse.iterator(); i.hasNext(); ) {
        Commessa child = (Commessa)i.next();
        ErrorMessage err = cancellaModelloPrdCommessa(child);
        if(err != null)
          return err;
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
      return getErrorMessageFromSqlResult(0, ex);
    }

    return null;
  }

  /**
   * cancellaModelloPrdCommessa
   * @param commessa Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancellaModelloPrdCommessa(Commessa commessa) {
    ErrorMessage ret = null;
    int rc = 0;
    try {
      if(commessa == null)
        return null;
      String where = ModelloProduttivoTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + ModelloProduttivoTM.R_COMMESSA + " = '" + commessa.getIdCommessa() + "'";
      List modelli = ModelloProduttivo.retrieveList(where, "", false);
      if(modelli == null || modelli.isEmpty())
        return null;
//Fix AB 04677 Begin
//      for (Iterator i = modelli.iterator(); rc >= 0 && i.hasNext(); )
      boolean inError = false;
      for(Iterator i = modelli.iterator(); !inError && i.hasNext(); )
//Fix AB 04677 End
      {
        ModelloProduttivo modello = (ModelloProduttivo)i.next();
        rc = modello.delete();
//Fix AB 04677 Begin
//        if(rc < 0)
//          ret = getErrorMessageFromSqlResult(rc,modello.getException());
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND) {
          ret = getErrorMessageFromSqlResult(rc, modello.getException());
          inError = true;
        }
//Fix AB 04677 End
      }
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  /**
   * cancellaStoriciCommesse
   * @param commessa Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancellaStoriciCommesse(Commessa commessa) {
    int rc = 0;
    ErrorMessage ret = null;
    try {
      if(commessa == null)
        return null;
      String where = StoricoCommessaTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
        + " AND " + StoricoCommessaTM.R_COMMESSA_PRM + " = '" + commessa.getIdCommessa() + "'";
      List storici = StoricoCommessa.retrieveList(where, "", false);
      if(storici == null || storici.isEmpty())
        return null;
//Fix AB 04677 Begin
      boolean inError = false;
//      for (Iterator i = storici.iterator(); rc >= 0 && i.hasNext(); )
      for(Iterator i = storici.iterator(); !inError && i.hasNext(); )
//Fix AB 04677 End
      {
        StoricoCommessa storico = (StoricoCommessa)i.next();
        rc = storico.delete();
//Fix AB 04677 Begin
//        if(rc < 0)
//          ret = getErrorMessageFromSqlResult(rc,storico.getException());
        if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND) {
          ret = getErrorMessageFromSqlResult(rc, storico.getException());
          inError = true;
        }
//Fix AB 04677 End
      }
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  /**
   * cancellaCmmPrmESottoCmm
   * @param commessaPrm Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancellaCmmPrmESottoCmm(Commessa commessaPrm) {
    int rc = 0;
    ErrorMessage ret = null;
    try {
      if(commessaPrm == null)
        return null;
      List children = commessaPrm.getAllSottocommesse();
      List list = new Vector();
      if(children == null || children.isEmpty())
        list.add(commessaPrm);
      else {
        list = children;
        list.add(commessaPrm);
      }
      java.util.Collections.sort(list, new Commessa.CommesseComparator());
      for(Iterator i = list.iterator(); ret == null && i.hasNext(); ) {
        Commessa item = (Commessa)i.next();
        if(item != null) {
          //ret = cancellaCommesseEventi(item);
          //if(ret == null)
          ret = cancellaCommesseRate(item);
          if(ret == null) {
            rc = item.delete();
            if(rc == ErrorCodes.CONSTRAINT_VIOLATION)
              ret = getErrorMessageFromSqlResult(rc, item.getException());
          }
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  /**
   * cancellaCommesseEventi
   * @param commessa Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancellaCommesseEventi(Commessa commessa) {
    ErrorMessage ret = null;
    try {
      if(commessa == null)
        return null;
      PreparedStatement ps = deleteCmmEventiStmt.getStatement();
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(ps, 1, commessa.getIdAzienda());
      db.setString(ps, 2, commessa.getIdCommessa());
      int rc = ps.executeUpdate();
//Fix AB 04677 Begin
//      if(rc < 0)
      if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND)

//Fix AB 04677 End
        ret = getErrorMessageFromSqlResult(rc, null);
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  /**
   * cancellaCommesseRate
   * @param commessa Commessa
   * @return ErrorMessage
   */
  public static ErrorMessage cancellaCommesseRate(Commessa commessa) {
    ErrorMessage ret = null;
    try {
      if(commessa == null)
        return null;
      PreparedStatement ps = deleteCmmRateStmt.getStatement();
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(ps, 1, commessa.getIdAzienda());
      db.setString(ps, 2, commessa.getIdCommessa());
      int rc = ps.executeUpdate();
//Fix AB 04677 Begin
//      if(rc < 0)
      if(rc < 0 && rc != ErrorCodes.NO_ROWS_FOUND)

//Fix AB 04677 End
        ret = getErrorMessageFromSqlResult(rc, null);
    }
    catch(Exception ex) {
      ret = getErrorMessageFromSqlResult(0, ex);
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return ret;
  }

  /**
   * getErrorMessageFromSqlResult
   * @param rc int
   * @param exception Exception
   * @return ErrorMessage
   */
  public static ErrorMessage getErrorMessageFromSqlResult(int rc, Exception exception) {
    String msg = "";
    if(exception != null)
      msg = exception.getMessage();
    if(rc == ErrorCodes.CONSTRAINT_VIOLATION)
      return new ErrorMessage("THIP20T105", msg);
    else
      return new ErrorMessage("THIP20T106", msg);
  }

  public static class CommesseComparator implements java.util.Comparator {
    /**
     * @param o1 Object
     * @param o2 Object
     * @return int
     */
    public int compare(Object o1, Object o2) {
      return compare((Commessa)o1, (Commessa)o2);
    }

    /**
     * @param obj1 Commessa
     * @param obj2 Commessa
     * @return int
     */
    public int compare(Commessa obj1, Commessa obj2) {
      int ret = 0;
      if(obj1 == null || obj2 == null)
        return 0;
      else {
        Integer livelloOne = obj1.getLivelloCommessa();
        Integer livelloTwo = obj2.getLivelloCommessa();
        if(Utils.compare(livelloOne, livelloTwo) > 0)
          return -1;
        else return +1;
      }
    }
  }

//  Fix 04334 AB end
  // method ricercaCommesseWS
  // begin
  //Fix 04239 Fattouma inizio
  public static ClienteCommessaWS[] ricercaCommesseWS(String ragioneSociale, char tipologiaCosto) throws ThipException {
    ArrayList clienteCommessaWSList = new ArrayList();
    ClienteCommessaWS clienteCommessaWS = (ClienteCommessaWS)Factory.createObject(ClienteCommessaWS.class);
    Commessa commessa = (Commessa)Factory.createObject(Commessa.class);
    if(ragioneSociale != null) {
      if(tipologiaCosto == ClienteCommessaWS.COSTO_PREVENTIVO || tipologiaCosto == ClienteCommessaWS.PREZZO_PREVENTIVO || tipologiaCosto == ClienteCommessaWS.COSTO_PREVISTO) {
        Iterator ite = commessa.caricaClienteCommessaWSList(ragioneSociale, tipologiaCosto);
        while(ite.hasNext()) {
          clienteCommessaWS = (ClienteCommessaWS)ite.next();
          clienteCommessaWSList.add(clienteCommessaWS);
        }
      }
    }
    return toClienteCommessaWSArray(clienteCommessaWSList);
  }

  public static ClienteCommessaWS[] toClienteCommessaWSArray(List clienteCommessaWSList) {
    ClienteCommessaWS[] clienteCommessaWSArray = new ClienteCommessaWS[clienteCommessaWSList.size()];
    for(int i = 0; i < clienteCommessaWSList.size(); i++) {
      clienteCommessaWSArray[i] = (ClienteCommessaWS)clienteCommessaWSList.get(i);
    }
    return clienteCommessaWSArray;
  }

  // method richiestaParametri
  public Iterator caricaClienteCommessaWSList(String ragioneSociale, char tipologiaCosto) {
    Database db = ConnectionManager.getCurrentDatabase();
    String where = ClienteCommessaWS.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "' AND "
      + ClienteCommessaWS.R_COMMESSA_APP + " IS NULL AND "
      + ClienteCommessaWS.STATO_AVANZAMENTO + " = '" + Commessa.STATO_AVANZAM__PROVVISORIA + "' AND "
      + db.getCallToUppercaseFn(ClienteCommessaWS.RAGIONE_SOCIALE) + " LIKE '%" + ragioneSociale.toUpperCase() + "%'";
    where += getCondition(tipologiaCosto);
    String orderBy = ClienteCommessaWS.ID_CLIENTE + " , "
      + ClienteCommessaWS.ID_COMMESSA;
    String SELECT_QUERY = "SELECT * FROM " + ClienteCommessaWS.VIEW_NAME + " WHERE " + where + " ORDER BY " + orderBy + " ;";
    try {
      ResultSet rs = ConnectionManager.getCurrentConnection().prepareStatement(SELECT_QUERY).executeQuery();
      return new ClienteCommessaWSIterator(rs);
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  public String getCondition(char tipologiaCosto) {
    String ret = "";
    if(tipologiaCosto == ClienteCommessaWS.COSTO_PREVENTIVO || tipologiaCosto == ClienteCommessaWS.PREZZO_PREVENTIVO)
      ret = " AND (" + ClienteCommessaWS.DOMINIO + "='" + ModelloProduttivo.PREVENTIVAZIONE + "' OR " +
        ClienteCommessaWS.DOMINIO + "='" + ModelloProduttivo.GENERICO + "') ";
    else if(tipologiaCosto == ClienteCommessaWS.COSTO_PREVISTO)
      ret = " AND (" + ClienteCommessaWS.DOMINIO + "='" + ModelloProduttivo.COSTO + "' OR " +
        ClienteCommessaWS.DOMINIO + "='" + ModelloProduttivo.GENERICO + "' )";
    return ret;
  }

// Classe Iterator
  protected static class ClienteCommessaWSIterator extends ResultSetIterator {
    protected ClienteCommessaWSIterator(ResultSet rs) {
      super(rs);
    }

    //  Riempire l'oggetto ClienteCommessaWS con i dati ottenuti del RS
    protected Object createObject() throws SQLException {
      ClienteCommessaWS clienteCommessaWSObj = (ClienteCommessaWS)Factory.createObject(ClienteCommessaWS.class);
      clienteCommessaWSObj.setIdAzienda(cursor.getString(ClienteCommessaWS.ID_AZIENDA));
      clienteCommessaWSObj.setIdCliente(cursor.getString(ClienteCommessaWS.ID_CLIENTE));
      clienteCommessaWSObj.setRagioneSociale(cursor.getString(ClienteCommessaWS.RAGIONE_SOCIALE));
      clienteCommessaWSObj.setIndirizzo(cursor.getString(ClienteCommessaWS.INDIRIZZO));
      clienteCommessaWSObj.setCap(cursor.getString(ClienteCommessaWS.CAP));
      clienteCommessaWSObj.setLocalita(cursor.getString(ClienteCommessaWS.LOCALITA));
      clienteCommessaWSObj.setIdProvincia(cursor.getString(ClienteCommessaWS.R_PROVINCIA));
      clienteCommessaWSObj.setIdNazione(cursor.getString(ClienteCommessaWS.R_NAZIONE));
      clienteCommessaWSObj.setCodiceFiscale(cursor.getString(ClienteCommessaWS.CODICE_FISCALE));
      clienteCommessaWSObj.setPartitaIVA(cursor.getString(ClienteCommessaWS.PARTITA_IVA));
      clienteCommessaWSObj.setIdCommessa(cursor.getString(ClienteCommessaWS.ID_COMMESSA));
      clienteCommessaWSObj.setDescrizione(cursor.getString(ClienteCommessaWS.DESCRIZIONE));
      return clienteCommessaWSObj;
    }
  }

  //Fix 04239 Fattouma fine
// End classe del'iteratore
  // end

//Fix 04361 start
  public ErrorMessage riaperturaWf() {
    riapertura();
    return null;
  }

  public void riapertura() {
    if(getStatoAvanzamento() == STATO_AVANZAM__CHIUSA_CONTABILAMENTO) {
      setDataChiusura(null);
      setStatoAvanzamento(STATO_AVANZAM__CHIUSA_OPERATIVAMENTO);
    }
  }

//Fix 04361 end

  //Fix 04567 inizio
  public ErrorMessage cancella() {
    return Commessa.cancella(this);
  }

  public ErrorMessage checkDeleteCommessa() {
    return Commessa.isDeletebleCommessa(this);
  }

  //Fix 04567 fine

  // Fix 04599 Begin

  protected void impostaDataConfermaFromAppertenenza() {
    if(getCommessaAppartenenza() == null)
      return;

    if(getCommessaAppartenenza().getDataConferma() != null && getDataApertura() != null)
      if(TimeUtils.differenceInDays(getCommessaAppartenenza().getDataConferma(), getDataApertura()) < 0)
        setDataConferma(getDataApertura());
      else
        setDataConferma(getCommessaAppartenenza().getDataConferma());
  }

  public ErrorMessage rendiProvvisoria() {
    //Fix 4677 begin
    if(getDataApertura() == null) {
      setDataApertura(TimeUtils.getCurrentDate());
    }
    //Fix 4677 End
    setStatoAvanzamento(STATO_AVANZAM__PROVVISORIA);
    if(getDataApertura() == null)
      setDataApertura(TimeUtils.getCurrentDate());
    Vector errors = checkChangeStatoAvanzamento();
    if(!errors.isEmpty())
      return(ErrorMessage)errors.get(0);
    try {
      save();
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  public ErrorMessage rendiConfermata() {
    //Fix 4677 begin
    if(getDataApertura() == null) {
      setDataApertura(TimeUtils.getCurrentDate());
    }
    if(getDataConferma() == null) {
      setDataConferma(TimeUtils.getCurrentDate());
    }
    //Fix 4677 End

    setStatoAvanzamento(STATO_AVANZAM__CONFERMATA);
    if(getDataConferma() == null)
      setDataConferma(TimeUtils.getCurrentDate());
    Vector errors = checkChangeStatoAvanzamento();
    if(!errors.isEmpty())
      return(ErrorMessage)errors.get(0);
    try {
      save();
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }

    return null;
  }

  public ErrorMessage rendiChiusaTecnicamente() {
    //Fix 4677 begin
    if(getDataApertura() == null) {
      setDataApertura(TimeUtils.getCurrentDate());
    }
    if(getDataConferma() == null) {
      setDataConferma(TimeUtils.getCurrentDate());
    }
    if(getDataChiusTec() == null) {
      setDataChiusTec(TimeUtils.getCurrentDate());
    }
    //Fix 4677 End

    setStatoAvanzamento(STATO_AVANZAM__CHIUSA_TECNICAMENTO);
    if(getDataChiusTec() == null)
      setDataChiusTec(TimeUtils.getCurrentDate());
    Vector errors = checkChangeStatoAvanzamento();
    if(!errors.isEmpty())
      return(ErrorMessage)errors.get(0);
    try {
      save();
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  public ErrorMessage rendiChiusaOperativamente() {
    //Fix 4677 begin
    if(getDataApertura() == null) {
      setDataApertura(TimeUtils.getCurrentDate());
    }
    if(getDataConferma() == null) {
      setDataConferma(TimeUtils.getCurrentDate());
    }
    if(getDataChiusTec() == null) {
      setDataChiusTec(TimeUtils.getCurrentDate());
    }
    if(getDataChiusOpe() == null) {
      setDataChiusOpe(TimeUtils.getCurrentDate());
    }
    //Fix 4677 End

    setStatoAvanzamento(STATO_AVANZAM__CHIUSA_OPERATIVAMENTO);
    if(getDataChiusOpe() == null)
      setDataChiusOpe(TimeUtils.getCurrentDate());
    Vector errors = checkChangeStatoAvanzamento();
    if(!errors.isEmpty())
      return(ErrorMessage)errors.get(0);
    try {
      save();
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  public ErrorMessage rendiChiusa() {
    //Fix 4677 begin
    if(getDataApertura() == null) {
      setDataApertura(TimeUtils.getCurrentDate());
    }
    if(getDataConferma() == null) {
      setDataConferma(TimeUtils.getCurrentDate());
    }
    if(getDataChiusTec() == null) {
      setDataChiusTec(TimeUtils.getCurrentDate());
    }
    if(getDataChiusOpe() == null) {
      setDataChiusOpe(TimeUtils.getCurrentDate());
    }
    if(getDataChiusura() == null) {
      setDataChiusura(TimeUtils.getCurrentDate());
    }
    //Fix 4677 End

    setStatoAvanzamento(STATO_AVANZAM__CHIUSA_CONTABILAMENTO);
    if(getDataChiusura() == null)
      setDataChiusura(TimeUtils.getCurrentDate());
    Vector errors = checkChangeStatoAvanzamento();
    if(!errors.isEmpty())
      return(ErrorMessage)errors.get(0);
    try {
      save();
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  public Vector checkChangeStatoAvanzamento() {
    Vector ret = new Vector();

    ErrorMessage errIdArticolo = checkIdArticolo();
    if(errIdArticolo != null)
      ret.add(errIdArticolo);

    ErrorMessage errIdCliente = checkIdCliente();
    if(errIdCliente != null)
      ret.add(errIdCliente);

    ErrorMessage errIdResponsabileCommessa = checkIdResponsabileCommessa();
    if(errIdResponsabileCommessa != null)
      ret.add(errIdResponsabileCommessa);

    ErrorMessage errIdNumeroOrdine = checkIdNumeroOrdineWF();
    if(errIdNumeroOrdine != null)
      ret.add(errIdNumeroOrdine);

    ErrorMessage errValoreTotaleOrdine = checkValoreTotaleOrdineWF();
    if(errValoreTotaleOrdine != null)
      ret.add(errValoreTotaleOrdine);

    ErrorMessage errIdStabilimento = checkIdStabilimento();
    if(errIdStabilimento != null)
      ret.add(errIdStabilimento);

    ErrorMessage errDataConferma = checkDataConferma();
    if(errDataConferma != null)
      ret.add(errDataConferma);

    ErrorMessage errDataChiusTec = checkDataChiusTec();
    if(errDataChiusTec != null)
      ret.add(errDataChiusTec);

    ErrorMessage errDataChiusOpe = checkDataChiusOpe();
    if(errDataChiusOpe != null)
      ret.add(errDataChiusOpe);

    ErrorMessage errDataChiusura = checkDataChiusura();
    if(errDataChiusura != null)
      ret.add(errDataChiusura);

    return ret;
  }

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkIdNumeroOrdineWF() {
    if(getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE
      && getStatoAvanzamento() != STATO_AVANZAM__PROVVISORIA
      && (getIdNumeroOrdine() == null || getIdAnnoOrdine() == null))
      return new ErrorMessage("THIP20T005");
    return null;
  }

  /**
   *
   * @return ErrorMessage
   */
  public ErrorMessage checkValoreTotaleOrdineWF() {
    if(getPianoFatturazione() == PIANO_FATT__ATTIVO
      && compareStatoAvanzamento(getStatoAvanzamento(), STATO_AVANZAM__PROVVISORIA) > 0) {
      if(getOrdineVendita() != null) {
        if(getValoreTotaleRate().compareTo(getValoreOrdine()) != 0) {
          return new ErrorMessage("THIP20T028");
        }
      }
    }
    return null;
  }

  //Fix 04599 End
  //Fix 04637 Mz inizio
  public boolean isDaDuplicazioneCommessa() {
    return iDaDuplicazioneCommessa;
  }

  public void setDaDuplicazioneCommessa(boolean flag) {
    iDaDuplicazioneCommessa = flag;
  }

  //Fix 04637 Mz fine
  // 4882 DM inizio

  public List postSave() {
    return getWfStatus().firstMove();
  }

  // 4882 DM fine

  /**
   * checkRateCollegateAdOrdine
   * @param components BaseComponentsCollection
   * @return ErrorMessage
   */
  //...FIX 4810
  public ErrorMessage checkRateCollegateAdOrdine(BaseComponentsCollection components) {
    try {
      //...FIX 8890 (aggiunto if)
      //...Se anno ordine e numero ordine sono valorizzati
      if(getIdAnnoOrdine() != null && getIdNumeroOrdine() != null) {
        //...Controllo se è cambiato il numero dell'ordine
        if(!areObjectsEqual(iOldIdAnnoOrdine, getIdAnnoOrdine()) ||
           !areObjectsEqual(iOldIdNumeroOrdine, getIdNumeroOrdine())) {
          //...Controllo se il piano di fatturazione è attivo
          //if(getPianoFatturazione() == PIANO_FATT__ATTIVO) {
          //...Scorro le rate, se almeno una è collegata ad ordine restituisco un errore
          List rate = getRateCommesse();
          boolean esisteRataCollegata = false;
          for(int i = 0; i < rate.size(); i++) {
            RataCommessa rata = (RataCommessa)rate.get(i);
            if(rata.isCollegamentoOrdine()) {
              esisteRataCollegata = true;
              break;
            }
          }
          if(esisteRataCollegata) {
            ErrorMessage err = new ErrorMessage("THIP200131");
            ClassADCollection cad = ClassADCollectionManager.collectionWithName("Commessa");
            String label = cad.getAttribute("IdNumeroOrdine").getAttributeNameNLS();
            err.addComponent("ValoreTotaleOrdine", label, components.getComponent("IdNumeroOrdine"));
            return err;
          }
          //}
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  //5543 - inizio
  private boolean iCodificaCommessaCA = false;

  public boolean getCodificaCommessaCA() {
    return iCodificaCommessaCA;
  }

  public void setCodificaCommessaCA(boolean codificaCommessaCA) {
    iCodificaCommessaCA = codificaCommessaCA;
    setDirty(); // pur non essendo un attributo persistente, modifica il dirty del BO
  }

  private boolean descrizioneModificata() {
    return(!getDescrizione().getDescrizione().equals(iOldDescrizione) ||
      !getDescrizione().getDescrizioneRidotta().equals(iOldDescrizioneRidotta));
  }

  private boolean iCommessaAnaliticaGiàGestita = false;

  private boolean gestioneContabilitàAnaliticaAttiva() {
    char tipoCA = PersDatiGen.getCurrentPersDatiGen().getTipoInterfCA();
    return iCommessaAnaliticaGiàGestita || tipoCA != PersDatiGen.NON_INTERFACCITO;
  }

  private void gestioneCommessaAnalitica() throws ThipException {
    if(!gestioneContabilitàAnaliticaAttiva())
      return;

    iCommessaAnaliticaGiàGestita = true;

    //controllo sulla configurazione della parte analitica
    //se non esiste il dataset, ritorno una warning e non faccio niente
    String dataset = PrimroseHelper.datasetDaAzienda(getIdAzienda());
    if(dataset == null) {
      warning(new ErrorMessage("THIP200177"));
      return;
    }

    //creazione automatica commessa analitica
    if(getCodificaCommessaCA()) {
      ErrorMessage err = codificaCommessaCA(dataset);
      if(err != null)
        throw new ThipException(err);
      else
        setIdCommessaCA(getIdCommessa());
    }

    //cambio della descrizione
    if(isOnDB() && descrizioneModificata()) {
      ErrorMessage err = modificaDescrizioneCommessaCA(dataset);
      if(err != null)
        throw new ThipException(err);
    }
  }

  private ErrorMessage salvaGPD0PT(GPD0PT c) {
    ErrorMessage errore = null;
    try {
      int rc = c.save();
      if(rc < 1) {
        errore = new ErrorMessage("THIP200176");
        Trace.excStream.println("Errore salvando GPD0PT return code: " + rc);
      }
    }
    catch(Exception e) {
      errore = new ErrorMessage("THIP200176");
      e.printStackTrace(Trace.excStream);
    }
    return errore;
  }

  private ErrorMessage codificaCommessaCA(String dataset) {
    try {
      GPD0PT c = GPD0PT.elementWithKey(KeyHelper.buildObjectKey(new String[] {dataset, getIdCommessa()}), PersistentObject.NO_LOCK);
      if(c != null) {
        //commessa già codificata in A&C
        warning(new ErrorMessage("THIP200178"));
        return null;
      }
    }
    catch(SQLException e) {
      return new ErrorMessage("THIP200176");
    }

    GPD0PT c = (GPD0PT)Factory.createObject(GPD0PT.class);
    c.riempiti(this);
    return salvaGPD0PT(c);
  }

  // modifica della descrizione della commessa
  private ErrorMessage modificaDescrizioneCommessaCA(String dataset) {
    GPD0PT c = null;

    try {
      c = (GPD0PT)GPD0PT.elementWithKey(KeyHelper.buildObjectKey(new String[] {dataset, getIdCommessa()}), PersistentObject.NO_LOCK);
    }
    catch(SQLException e) {
      e.printStackTrace(Trace.excStream);
    }

    if(c != null && c.isCollegataGestionale(getIdAzienda())) {
      c.setDescrizioni(getDescrizione().getDescrizione(), getDescrizione().getDescrizioneRidotta());
      return salvaGPD0PT(c);
    }

    return null;
  }

  // interface SaveConWarning

  private List iWarnings = null;

  private void warning(ErrorMessage e) {
    iWarnings = new ArrayList();
    iWarnings.add(e);
  }

  public List getWarningList() {
    return iWarnings;
  }

  //5543 - fine

  //...FIX 9121 inizio

  /**
   * impostaDataConfermaSottocommessa
   */
  protected void impostaDataConfermaSottocommessa() {
    if(getCommessaAppartenenza() == null)
      return;

    if(getDataConferma() != null && getDataApertura() != null) {
      if(TimeUtils.differenceInDays(getDataConferma(), getDataApertura()) < 0)
        setDataConferma(getDataApertura());
    }
  }
  //...FIX 9121 fine

  // Fix 9297 - Inizio
  /**
   * Restituisce l'attributo.
   */
  public boolean isDaCMCommessa() {
    return iDaCMCommessa;
  }

  /**
   * Valorizza l'attributo.
   */
  public void setDaCMCommessa(boolean daCMCommessa) {
    iDaCMCommessa = daCMCommessa;
    setDirty();
  }
  // Fix 9297 - Fine
 // FIX 13297 begin
 public ErrorMessage checkArcoAvanzamento()
 {
   Iterator iteRate = getRateCommesse().iterator();
   while (iteRate.hasNext())
   {
     RataCommessa rate = (RataCommessa)iteRate.next();
     if (rate.getCriterioFatturazione() == RataCommessa.DA_WORKFLOW &&
         rate.getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO &&
         rate.getIdNumeroDocumento() == null
         )
     {
       if(rate.getTipoAzione()==RataCommessa.EMISS_DOC_AVANZ_WF &&
          rate.getWfArcAV().getInitialNode().getKey()!= null &&
         rate.getWfArcAV().getInitialNode().getKey().equals(getWfStatus().getCurrentNode().getKey()))
        return new ErrorMessage("THIP40T015");
     }
   }
   return null;
 }
 // FIX 13297 end
  // 15938 begin
  public boolean checkAbilitaCreazioneOrdVen()
  {
    if((getIdCommessaAppartenenza() != null && !getIdCommessaAppartenenza().equals("")) ||
       !isCommessa_E_SottoCommessa_Provvisorie(this) ||
       getIdTipoCommessa() == null ||
       getTipoCommessa().getNaturaCommessa() == TipoCommessa.NATURA_CMM__TECNICA ||
       getIdCliente() == null ||
       isCommessaRealzionataAdOrdine())
      return false;
    return true;
  }

  /**
   * isCommessaRealzionataAdOrdine
   *
   * @return boolean
   */
  public boolean isCommessaRealzionataAdOrdine()
  {
    if(getOrdineVendita()!=null)
      return true ;
    else
    {
      Iterator ite =  getAllSottocommesse().iterator();
      while(ite.hasNext())
      {
        Commessa sottoCom = (Commessa)ite.next();
        if(sottoCom.getOrdineVendita()!=null)
          return true;
      }
    }
     return false;
  }

  /**
   * isEsistenoModelloProd
   *
   * @return boolean
   */
  public boolean isEsistenoModelloProd()
  {
    List modelli = null;
    String where = ModelloProduttivoTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'"
      + " AND " + ModelloProduttivoTM.R_COMMESSA + " = '" + getIdCommessa() + "'"
      + " AND " + ModelloProduttivoTM.R_ARTICOLO + " = '" + getIdArticolo() + "'"
      + " AND " + ModelloProduttivoTM.DATA_DISPON + "<= " +ConnectionManager.getCurrentDatabase().getLiteral(getDataApertura()) +" "
      + " AND " + ModelloProduttivoTM.STATO + " = '" + DatiComuniEstesi.VALIDO + "'";
    try
    {
      modelli = ModelloProduttivo.retrieveList(where, "", false);
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream);
    }
    if(modelli == null || modelli.isEmpty())
        return false;
    return true;
  }

  // 15938 end

  //Fix 20785 inizio
  public ErrorMessage checkCUPCIG() {
  if(getCodiceCUP() == null && getCodiceCIG() == null)
    return null;
  //Fix 31527 PM >
  //if (getNumeroDocumento() == null && getNumeroDocOrdAcq() == null && getNumeroDocContratto() == null && getNumeroDocFatColl() == null && getNumeroDocRicezione() == null) 
  String numeroDocumento = null;
  Iterator i = getDocumentiCollegate().iterator();
  while (i.hasNext())
  {
	  CommessaDocCollegate cdc = (CommessaDocCollegate)i.next();
	  if (cdc != null && cdc.getNumeroDocumento() != null && !cdc.getNumeroDocumento().equals(""))
	  {
		  numeroDocumento = cdc.getNumeroDocumento();
		  break;
	  }   
  }
  if (numeroDocumento == null || numeroDocumento.equals(""))
     return new ErrorMessage("THIP40T333");
  //Fix 31527 PM <
  return null;
  }
  //Fix 20785 fine
    
  //Fix 29025 - inizio
  /**
   * Verifica la coerenza tra tipologia piano e tipo rate
   * @return ErrorMessage o null
   */
  public ErrorMessage checkTipoPiano() {
	  ErrorMessage ret = null;
	  
	  if (getTipoPiano() == TP_PIANO_NORMALE) {
		  ret = checkRatePianoNormale();
	  }
	  else if (getTipoPiano() == TP_PIANO_ACCONTI_SALDO) {
		  ret = checkRatePianoAccontiSaldo();
	  }
	  
	  ret = ret == null ? checkVariazioneTipoPiano() : ret;
	  
	  return ret;
  }

  
  /**
   * Verifica la coerenza tra tipologia piano e tipo rate su piano normale
   * @return ErrorMessage o null
   */
  public ErrorMessage checkRatePianoNormale() {
	  ErrorMessage ret = null;
	  
	  Iterator iter = getRateCommesse().iterator();
	  while (ret == null && iter.hasNext()) {
		  RataCommessa rata = (RataCommessa)iter.next();
		  ret = rata.getTipoRata() == RataCommessa.TP_RATA_NORMALE ? null : new ErrorMessage("THIP400020");
	  }
	  
	  return ret;
  }

  
  /**
   * Verifica la coerenza tra tipologia piano e tipo rate su piano 
   * acconti/saldo
   * @return ErrorMessage o null
   */
  public ErrorMessage checkRatePianoAccontiSaldo() {
	  ErrorMessage ret = null;
	  
	  Iterator iter = getRateCommesse().iterator();
	  while (ret == null && iter.hasNext()) {
		  RataCommessa rata = (RataCommessa)iter.next();
		  char tipoRata = rata.getTipoRata();
		  if (tipoRata == RataCommessa.TP_RATA_NORMALE) {
			  ret = new ErrorMessage("THIP400021");
		  }
	  }
	  
	  return ret;
  }
  
  
  /**
   * Il tipo piano non può essere variato se esiste almeno una rata con 
   * documento emesso
   * @return ErrorMessage o null
   */
  protected ErrorMessage checkVariazioneTipoPiano() {
	  return getTipoPiano() != iOldTipoPiano && hasRataConDocVenEmesso(this) ? new ErrorMessage("THIP400023") : null;
  }
  
  
  /**
   * Verifica se per una commessa esiste una rata emessa
   * @param commessa Commessa da verificare
   * @return boolean
   * @see RataCommessa#isEmessa(RataCommessa)
   */
  public static boolean hasRataConDocVenEmesso(Commessa commessa) {
	  boolean ret = false;
	  
	  Iterator iter = commessa.getRateCommesse().iterator();
	  while (!ret && iter.hasNext()) {
		  RataCommessa rata = (RataCommessa)iter.next();
		  ret = RataCommessa.isEmessa(rata);
	  }
	  
	  return ret;
  }
  //Fix 29025 - fine

  //29960 inizio
  public void creaAmbienteCommessa() throws SQLException{
	  //if(!onDB && getTipoCommessa() != null && getTipoCommessa().isCreaAmbienteCommessa() && getIdAmbienteCommessa() == null){//Fix 31596
	  if (getTipoCommessa() != null && getTipoCommessa().isCreaAmbienteCommessa() && getIdAmbienteCommessa() == null) { //Fix 31596
	    //Fix 31596 Inizio
	  	AmbienteCommessa ambComm = AmbienteCommessa.elementWithKey(KeyHelper.buildObjectKey(new String[]{getIdAzienda(), getIdCommessa()}), PersistentObject.OPTIMISTIC_LOCK);
	  	if(ambComm != null){
	  		setIdAmbienteCommessa(ambComm.getIdAmbienteCommessa());
	  	}
	  	else{ //Fix 31596 Fine
		    AmbienteCommessa ambienteCommessa = (AmbienteCommessa) Factory.createObject(AmbienteCommessa.class);
		    ambienteCommessa.setIdAzienda(getIdAzienda());
		    ambienteCommessa.setIdAmbienteCommessa(getIdCommessa());
		    ambienteCommessa.getDescrizione().setDescrizione(getDescrizione().getDescrizione());
		    ambienteCommessa.getDescrizione().setDescrizioneRidotta(getDescrizione().getDescrizioneRidotta());
		    ambienteCommessa.setTipoCosto(getTipoCommessa().getTipoCosto());
		    int ret = ambienteCommessa.save(); 
		    if(ret < 0){
			    throw new ThipException(new ErrorMessage("THIP40T678", ambienteCommessa.getIdAmbienteCommessa()));
		    }
		    else{
			    setIdAmbienteCommessa(ambienteCommessa.getIdAmbienteCommessa());
		    }
	  	}	 //Fix 31596	
	  }
  }
  //29960 fine
//31437 inizio
  public ErrorMessage checkDocCollegate() {
	  List docCollegate = getDocumentiCollegate();
	  if(docCollegate.isEmpty())
		  return null;
	  
	  List datiDocs = new ArrayList();
	  DateType dt = new DateType();
	  Iterator docs = docCollegate.iterator();
	  while(docs.hasNext()) {
		 CommessaDocCollegate docCol = (CommessaDocCollegate)docs.next();
		 String dataDoc = "";
		 String numDoc = "";
		 String convDoc = "";
		 
		 if(docCol.getDataDocumento() != null)
			 dataDoc = dt.objectToString(docCol.getDataDocumento());
		
		 if(docCol.getNumeroDocumento() != null)
			 numDoc = docCol.getNumeroDocumento();
		
		 if(docCol.getCommConvenzione() != null)
			 convDoc = docCol.getCommConvenzione();
		 
		 String datiRiga = docCol.getTipoDocumento() + KeyHelper.KEY_SEPARATOR + dataDoc + KeyHelper.KEY_SEPARATOR + numDoc + KeyHelper.KEY_SEPARATOR + convDoc;
		 if(datiDocs.contains(datiRiga)) {
			 String param = getDescAttrRef("TipoDocumentoCommessa", docCol.getTipoDocumento());
			 if(!dataDoc.isEmpty())
				 param = param + ", " + dataDoc;
			 
			 if(!numDoc.isEmpty())
				 param = param + ", " + numDoc;
			 
			 if(!convDoc.isEmpty())
				 param = param + ", " + convDoc;
			 
			 return new ErrorMessage("THIP_TN90", param);
		 }
		 else 
			 datiDocs.add(datiRiga);
	  }
	  return null;
  }
  
  public static String getDescAttrRef(String IdRef, char value) {
	  EnumType eType = new EnumType(IdRef);
	  return eType.descriptionFromValue(String.valueOf(value));
  }
  //31437 fine
  //33950 inizio
  public boolean hasCompenenteATempo() {
	  if(getArticolo() != null && getArticolo().getClasseMerclg() != null && getArticolo().getClasseMerclg().getSchemaCosto() != null && 
		 getArticolo().getClasseMerclg().getSchemaCosto().getComponenti() != null && !getArticolo().getClasseMerclg().getSchemaCosto().getComponenti().isEmpty()) {
		  for (Iterator iterator = getArticolo().getClasseMerclg().getSchemaCosto().getComponenti().iterator(); iterator.hasNext();) {
			  LinkCompSchema cmp = (LinkCompSchema) iterator.next();
			  if(cmp.getComponenteCosto().isGestioneATempo())
				  return true;
		  }
	  }	  
	  return false;
  }
  
  public ConsuntivoCommessa getUltimaConsuntivoCommessaDefinitivo() {
	  ConsuntivoCommessa ultimoConsuntivoDefinitivo = null;
	  String where = ConsuntivoCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "+ 
			  		 ConsuntivoCommessaTM.R_COMMESSA + "='" + getIdCommessa() + "' AND " + 
			  		 ConsuntivoCommessaTM.STATO_AV + "='" + ConsuntivoCommessa.DEFINITIVO + "'";
	  
	  PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)cursor.next();
			  if(ultimoConsuntivoDefinitivo == null || (consuntivo.getDataRiferimento().compareTo(ultimoConsuntivoDefinitivo.getDataRiferimento()) > 0))
			  	ultimoConsuntivoDefinitivo = consuntivo;	  
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return ultimoConsuntivoDefinitivo;
  }
  //33950 fine
  //34585 inizio
  public BudgetCommessa getUltimoBudgetCommessaDefinitivo() {
	  BudgetCommessa ultimoBudgetDefinitivo = null;
	  String where = BudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "+ 
			  		 BudgetCommessaTM.ID_COMMESSA + "='" + getIdCommessa() + "' AND " + 
			  		 BudgetCommessaTM.STATO_AV + "='" + BudgetCommessa.DEFINITIVO + "'";
	  
	  PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  BudgetCommessa budget = (BudgetCommessa)cursor.next();
			  if(ultimoBudgetDefinitivo == null || (budget.getDataRiferimento().compareTo(ultimoBudgetDefinitivo.getDataRiferimento()) > 0))
				  ultimoBudgetDefinitivo = budget;	  
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return ultimoBudgetDefinitivo;
  }
  //34585 fine
  //35382 inizio
  public BudgetCommessa getUltimoBudgetCommessa() {
	  BudgetCommessa ultimoBudget = null;
	  String where = BudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "+ 
			  		 BudgetCommessaTM.ID_COMMESSA + "='" + getIdCommessa() + "' " ;
	  
	  PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  BudgetCommessa budget = (BudgetCommessa)cursor.next();
			  if(ultimoBudget == null || (budget.getDataRiferimento().compareTo(ultimoBudget.getDataRiferimento()) > 0))
				  ultimoBudget = budget;	  
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return ultimoBudget;
  }
  
  public VariaBudgetCommessa getUltimoVariaBudgetCommessa() {
	  VariaBudgetCommessa ultimoVariaBudget = null;
	  String where = VariaBudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "+ 
			  		 VariaBudgetCommessaTM.ID_COMMESSA + "='" + getIdCommessa() + "' " ;
	  
	  PersistentObjectCursor cursor = new PersistentObjectCursor(VariaBudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  VariaBudgetCommessa variazione = (VariaBudgetCommessa)cursor.next();
			  if(ultimoVariaBudget == null || (variazione.getDataRiferimento().compareTo(ultimoVariaBudget.getDataRiferimento()) > 0))
				  ultimoVariaBudget = variazione;	  
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return ultimoVariaBudget;
  }
  
  public ConsuntivoCommessa getUltimoConsuntivoCommessa() {
	  ConsuntivoCommessa ultimoConsuntivo = null;
	  String where = ConsuntivoCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "+ 
			  		 ConsuntivoCommessaTM.R_COMMESSA + "='" + getIdCommessa() + "' " ;
	  
	  PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)cursor.next();
			  if(ultimoConsuntivo == null || (consuntivo.getDataRiferimento().compareTo(ultimoConsuntivo.getDataRiferimento()) > 0))
				  ultimoConsuntivo = consuntivo;	  
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return ultimoConsuntivo;
  }
  
  public boolean esisteVariazioneProvvisorio() {
	  String where = VariaBudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + 
		  		 	 VariaBudgetCommessaTM.ID_COMMESSA + "='" + getIdCommessa() + "' AND " +
		  		 	 VariaBudgetCommessaTM.STATO_AV + "='" + VariaBudgetCommessa.PROVVISORIO + "'";;

	  PersistentObjectCursor cursor = new PersistentObjectCursor(VariaBudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  VariaBudgetCommessa variazione = (VariaBudgetCommessa)cursor.next();
			  if(variazione != null )
				  return true;	  
		  }

	  } catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }

	  return false;
  }
  
  public boolean esisteConsuntivoCommessa() {
	  String where = ConsuntivoCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "+ 
			  		 ConsuntivoCommessaTM.R_COMMESSA + "='" + getIdCommessa() + "'" ; 
	  
	  PersistentObjectCursor cursor = new PersistentObjectCursor(ConsuntivoCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)cursor.next();
			  if(consuntivo != null)
			  	return true;	  
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return false;
  }
  
  public boolean esisteBudgetCommessa() {
	  BudgetCommessa ultimoBudget = null;
	  String where = BudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND "+ 
			  		 BudgetCommessaTM.ID_COMMESSA + "='" + getIdCommessa() + "' " ;
	  
	  PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
	  try {
		  while (cursor.hasNext()) {
			  BudgetCommessa budget = (BudgetCommessa)cursor.next();
			  if(budget != null)
				  return true;	  
		  }
	  } 
	  catch (SQLException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  return false;
  }
  
  public ErrorMessage rendiBudgetDefinitivo() {
	  BudgetCommessa budget = getUltimoBudgetCommessa();
	  if(budget != null && !budget.rendiDefinitivo()) {
		  return new ErrorMessage("THIP_TN809");//Problema nel passaggio allo stato Definitivo
	  }
	  return null;
  }

  public ErrorMessage rendiBudgetProvvisorio() {
	  BudgetCommessa budget = getUltimoBudgetCommessa();
	  if(budget != null && !budget.rendiProvvisorio()) {
			return new ErrorMessage("THIP_TN810");//Problema nel passaggio allo stato Provvisorio
		}	
	  return null;
  }

  //35382 fine
}

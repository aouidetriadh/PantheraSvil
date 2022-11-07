package it.thera.thip.produzione.commessa;

import java.math.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.PersDatiTecnici;
import it.thera.thip.datiTecnici.PersDatiTecniciAmbiente;
import it.thera.thip.datiTecnici.costi.CostoRisorsa;
import it.thera.thip.datiTecnici.costi.CostoRisorsaTM;
import it.thera.thip.datiTecnici.modpro.*;
import it.thera.thip.produzione.ordese.*;
// FIX 9278 - inizio
import it.thera.thip.base.articolo.*;
// FIX 9278 - fine
import it.thera.thip.base.generale.AmbienteCosti;

/**
 * EstrazioneDocAcqRig
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 27/05/2005 at 14:58:32
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 04693   24/11/2005    LP         Correzioni varie
 * 04810   22/12/2005    LP         Aggiunto salvataggio storico solo se indicato nel file di risorse
 *                                  e aggiunto il riempimento del campo IdOperazione
 * 09278   magg 2008     LR         createStorici scarta materiali/prodotti WIP
 * 10411   28/01/2009    DBot       Logger per performance su consuntivazione
 * 10595   23/03/2009    DBot       Gestione di tempo nullo su risosrse a tempo
 * 10537   30/03/2009    DBot       Riallineamento per fix WEB
 * 10913   25/05/2009    DB         Per permettere la personalizzazione
 * 12837   08/09/2010    Mekki      Gestiti EsclusoDaCosti sul materiale
 * 27486   25/05/2018    DBot       Reperimento costi risorsa da documento
 * 31460   03/07/2020    RA		    Modifica valorozzazione CostoUnitario del storico per materiale/prodotti/risorse
 * 31513   08/09/2020	 RA			Aggiunto gestione articoli senza gestione saldi per commessa  
 * 33081   08/03/2021    DBot       Correzioni varie
 * 33143   17/03/2021    RA			Corretta valorizzazione costo 
 * 34308   21/09/2021    Mekki      GestoreCommit
 * 34138   16/10/2021    Mekki      Nuovo modalità di recupero di schema/Comp. costo
 * 33950   06/10/2021    RA         Corretta valorizzazione DocumentoOrigine
 */

public class EstrazioneOrdPrd extends EstrazioneDocumenti {
	
  boolean storicoNonCommessa = false; //31513
  
  protected GestoreCommit iGestoreCommit; //Fix 34308

  /**
   * Constructor
   * @param consunCmm ConsuntivazioneCommesse
   * @param compactCmm CompactCommessa
   */
  public EstrazioneOrdPrd(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm) {
    super(consunCmm, compactCmm);
  }

  /**
   * processRighe
   * @throws Exception
   * @return boolean
   */
  protected boolean processRighe() throws Exception {
     getLogger().iniziaTipoDocumento("ORD_PRD", iCompactCmm.getIdCommessa()); //Fix 10411
    String where =
      OrdineEsecutivoTM.ID_AZIENDA + " = '" + iCompactCmm.getIdAzienda() + "'" +
      " AND " + OrdineEsecutivoTM.R_COMMESSA + " = '" + iCompactCmm.getIdCommessa() + "'" +
      " AND " + OrdineEsecutivoTM.STATO + " = '" + DatiComuniEstesi.VALIDO + "'" +
      " AND (" + OrdineEsecutivoTM.STATO_ORDINE + " = '" + OrdineEsecutivo.IMMESSO + "'" +
      " OR " + OrdineEsecutivoTM.STATO_ORDINE + " = '" + OrdineEsecutivo.CONFERMATO + "'" +
      " OR " + OrdineEsecutivoTM.STATO_ORDINE + " = '" + OrdineEsecutivo.IN_CORSO + "')";

    getLogger().startTime(); // Fix 10411
    List docAcqList = OrdineEsecutivo.retrieveList(OrdineEsecutivo.class, where, OrdineEsecutivoTM.R_COMMESSA, false);
    getLogger().addTempoLettura(getLogger().stopTime()); // Fix 10411
    getLogger().incNumTestateLette(docAcqList.size()); // Fix 10411

    for(Iterator it = docAcqList.iterator(); it.hasNext(); ) {
      OrdineEsecutivo ordEsec = (OrdineEsecutivo)it.next();
      if(willBeProcessed(ordEsec)) {
      	//31513 inizio         
      	if(!ordEsec.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() && iConsunCmm.iStoriciNonCommessa)
      		storicoNonCommessa = true;
      	else
      		storicoNonCommessa = false;
      	//31513 fine 
        boolean ret = createStorici(ordEsec);
        if(!ret)
          return false;
        getGestoreCommit().commit(); //Fix 34308
      }
      it.remove(); // Fix 10411, 10437
    }
    iConsunCmm.commitWithGestoreCommit(true);// Fix 10411
    getLogger().fineTipoDocumento(); // Fix 10411
    return true;
  }

  /**
   * createStorici
   * @param OrdEsec OrdineEsecutivo
   * @throws Exception
   * @return boolean
   */
  protected boolean createStorici(OrdineEsecutivo OrdEsec) throws Exception {

    //Processing Righe Materiali
    for(Iterator it = recuperoAttiviteEsecMateriali(OrdEsec).iterator(); it.hasNext(); ) {
      AttivitaEsecMateriale attEsecMat = (AttivitaEsecMateriale)it.next();
      //StoricoCommessa storicoCmm = createStoricoAttEsecMateriale(OrdEsec, attEsecMat); Fix 10411
      getLogger().incNumRigheLette(1); // Fix 10411

      // FIX 9278 - inizio
      if (attEsecMat.getArticolo().getTipoParte() == ArticoloDatiIdent.ARTICOLO_WIP) {
          continue;
      }
      // FIX 9278 - fine
      getLogger().incNumRigheFatte(); // Fix 10411
      getLogger().startTime(); //Fix 10411
      StoricoCommessa storicoCmm = createStoricoAttEsecMateriale(OrdEsec, attEsecMat); //Fix 10411 spostata dopo test WIP
      getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

      if(storicoCmm == null) {
        iConsunCmm.commitWithGestoreCommit(false);
        return false;
      }

      getLogger().startTime(); //Fix 10411
      attribuzioneCosti(storicoCmm);
      getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411
      iConsunCmm.commitWithGestoreCommit(false);
    }

    //Processing Righe Prodotti
    for(Iterator it = recuperoAttiviteEsecProdotti(OrdEsec).iterator(); it.hasNext(); ) {
      AttivitaEsecProdotto attEsecPrd = (AttivitaEsecProdotto)it.next();
      getLogger().incNumRigheLette(1); // Fix 10411

      // FIX 9278 - inizio
      if (attEsecPrd.getTipoProdotto() == AttivitaEsecProdotto.ARTICOLO_WIP) {
          continue;
      }
      // FIX 9278 - fine

      getLogger().incNumRigheFatte(); // Fix 10411
      getLogger().startTime(); //Fix 10411
      StoricoCommessa storicoCmm = createStoricoOrdEsecProdotto(OrdEsec, attEsecPrd);
      getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

      if(storicoCmm == null) {
        iConsunCmm.commitWithGestoreCommit(false);
        return false;
      }

      getLogger().startTime(); //Fix 10411
      attribuzioneCosti(storicoCmm);
      getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411
      iConsunCmm.commitWithGestoreCommit(false);
    }

    //Processing Righe Risorse
    for(Iterator it = recuperoAttiviteEsecRisorse(OrdEsec).iterator(); it.hasNext(); ) {
      AttivitaEsecRisorsa attEsecRsr = (AttivitaEsecRisorsa)it.next();
      getLogger().incNumRigheLette(1); // Fix 10411
      getLogger().incNumRigheFatte(); // Fix 10411
      getLogger().startTime(); //Fix 10411
      StoricoCommessa storicoCmm = createStoricoOrdEsecRisorsa(OrdEsec, attEsecRsr);
      getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

      if(storicoCmm == null) {
        iConsunCmm.commitWithGestoreCommit(false);
        return false;
      }

      getLogger().startTime(); //Fix 10411
      attribuzioneCosti(storicoCmm);
      getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411
      iConsunCmm.commitWithGestoreCommit(false);
    }

    return true;
  }

  /**
   * createStoricoDocPrdMateriale
   * @param ordEsec OrdineEsecutivo
   * @param attEsecMat AttivitaEsecMateriale
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStoricoAttEsecMateriale(OrdineEsecutivo ordEsec, AttivitaEsecMateriale attEsecMat) throws Exception {

    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(ordEsec.getIdCommessa());
    storicoCmm.setLivelloCommessa(ordEsec.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(ordEsec.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(ordEsec.getCommessa().getIdCommessaPrincipale());

    if(Utils.compare(attEsecMat.getIdCommessa(), iCompactCmm.getIdCommessa().trim()) != 0)
      storicoCmm.setIdCommessaCol(attEsecMat.getIdCommessa());
    
    //33950 inizio
    //storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE); 
    if(ordEsec.getStatoOrdine() == OrdineEsecutivo.IMMESSO)
    	storicoCmm.setDocumentoOrigine(StoricoCommessa.RICHIESTA);
    else
    	storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);    
    //33950 fine
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.PRODUZIONE_MATERIALE);

    storicoCmm.setIdAnnoOrigine(ordEsec.getIdAnnoOrdine());
    storicoCmm.setIdNumeroOrigine(ordEsec.getIdNumeroOrdine());
    storicoCmm.setIdRigaOrigine(attEsecMat.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrigine(attEsecMat.getIdRigaMateriale());
    storicoCmm.setDataOrigine(ordEsec.getDataOrdine());
    //Fix 04361 Mz inizio
    storicoCmm.setNumeroOrgFormattato(ordEsec.getNumeroOrdFmt());
    //Fix 04361 Mz fine

    storicoCmm.setIdAnnoOrdine(ordEsec.getIdAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(ordEsec.getIdNumeroOrdine());
    storicoCmm.setIdRigaOrdine(attEsecMat.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrdine(attEsecMat.getIdRigaMateriale());
    storicoCmm.setDataOrdine(ordEsec.getDataOrdine());

    storicoCmm.setAvanzamento(false);

    storicoCmm.setIdArticolo(attEsecMat.getIdArticolo());
    storicoCmm.setIdVersione(attEsecMat.getIdVersione() != null ? attEsecMat.getIdVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazione(attEsecMat.getIdConfigurazione());

    storicoCmm.setDescrizioneArticolo(attEsecMat.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);

    String idCmm = null;
    if(attEsecMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
      idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

    //33143 inizio
    /*
    //Fix 12837 --inizio
    BigDecimal tmpCostoTotale = null;
    if(attEsecMat.isEsclusoDaCosti()){
      tmpCostoTotale = new BigDecimal("0");
    }
    else{ //Fix 12837 --fine
      tmpCostoTotale = recuperoOrdEsecCostoRif(idCmm, attEsecMat.getCostoRiferimento(),
        attEsecMat.getIdArticolo(),
        attEsecMat.getIdVersione(),
        //attEsecMat.getIdConfigurazione());//31460
        attEsecMat.getIdConfigurazione(), storicoCmm);//31460
    }
    //31460 inizio
    storicoCmm.setCostoUnitario(tmpCostoTotale);
    storicoCmm.setCostoUnitarioOrigine(attEsecMat.getCostoRiferimento());
    //31460 fine
    if(tmpCostoTotale != null && attEsecMat.getQtaResiduaUMPrm() != null)
      tmpCostoTotale = tmpCostoTotale.multiply(attEsecMat.getQtaResiduaUMPrm());

    storicoCmm.setCostoTotale(tmpCostoTotale != null && tmpCostoTotale.compareTo(new BigDecimal("0")) >= 0 ?
      tmpCostoTotale : new BigDecimal("0"));
    */
    //33143 fine
    //Fix 04161 Mz inizio
    //storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
    //Fix 04163 Mz fine
    storicoCmm.setTipoArticolo(attEsecMat.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(attEsecMat.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(attEsecMat.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(attEsecMat.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(attEsecMat.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(attEsecMat.getArticolo().getArticoloDatiPianif().getIdPianificatore());
    storicoCmm.setIdStabilimento(ordEsec.getIdStabilimento());
    //Fix 04361 Mz inizio
    if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine
    
    //33143 inizio
    /*
    storicoCmm.setQuantitaUMPrm(attEsecMat.getQtaResiduaUMPrm());
    storicoCmm.setQuantitaUMSec(attEsecMat.getQtaResiduaUMSec());
    */
    BigDecimal resPrm = attEsecMat.getQtaResiduaUMPrm();
    if(resPrm == null || resPrm.compareTo(new BigDecimal("0")) < 0) 
    	resPrm = new BigDecimal("0");
    storicoCmm.setQuantitaUMPrm(resPrm);

    BigDecimal resSec = attEsecMat.getQtaResiduaUMSec();
    if(resSec == null || resSec.compareTo(new BigDecimal("0")) < 0) 
    	resSec = new BigDecimal("0");
    storicoCmm.setQuantitaUMSec(resSec);
    //33143 fine

    storicoCmm.setIdUmPrmMag(attEsecMat.getIdUMPrmMag());
    storicoCmm.setIdUmSecMag(attEsecMat.getIdUMSecMag());

    storicoCmm.setGesSaldiCommessa(attEsecMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
    if(attEsecMat.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(attEsecMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(attEsecMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }
    //33143 inizio
    //Fix 12837 --inizio
    BigDecimal tmpCostoTotale = null;
    if(attEsecMat.isEsclusoDaCosti()){
      tmpCostoTotale = new BigDecimal("0");
    }
    else{ //Fix 12837 --fine
      tmpCostoTotale = recuperoOrdEsecCostoRif(idCmm, attEsecMat.getCostoRiferimento(),
        attEsecMat.getIdArticolo(),
        attEsecMat.getIdVersione(),
        //attEsecMat.getIdConfigurazione());//31460
        attEsecMat.getIdConfigurazione(), storicoCmm);//31460
    }
    //31460 inizio
    storicoCmm.setCostoUnitario(tmpCostoTotale);
    storicoCmm.setCostoUnitarioOrigine(attEsecMat.getCostoRiferimento());
    //31460 fine
    if(tmpCostoTotale != null && attEsecMat.getQtaResiduaUMPrm() != null)
      tmpCostoTotale = tmpCostoTotale.multiply(attEsecMat.getQtaResiduaUMPrm());

    storicoCmm.setCostoTotale(tmpCostoTotale != null && tmpCostoTotale.compareTo(new BigDecimal("0")) >= 0 ?
      tmpCostoTotale : new BigDecimal("0"));
    //33143 fine
    //Fix 04361 Mz inizio
    storicoCmm.setValorizzaCosto(recuperoOrdEsecMatValorizzaCosto(storicoCmm));
    //Fix 04361 Mz fine
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmmAttMat(storicoCmm, attEsecMat);
    // fine fix 10913


    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoTotale())) {
      String artKey = KeyHelper.formatKeyString(storicoCmm.getArticolo().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T089", artKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      /*if(isErroreBloccanteMode())
        return null;*/
    }

    if(!iConsunCmm.isSimulazione() && ConsuntivazioneEstrazione.isSalvaOrdine()) //...FIX 4810
      storicoCmm.save();

    return storicoCmm;
  }

  /**
   * createStoricoDocPrdProdotto
   * @param ordEsec OrdineEsecutivo
   * @param attEsecPrd AttivitaEsecProdotto
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStoricoOrdEsecProdotto(OrdineEsecutivo ordEsec, AttivitaEsecProdotto attEsecPrd) throws Exception {

    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(ordEsec.getIdCommessa());
    storicoCmm.setLivelloCommessa(ordEsec.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(ordEsec.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(ordEsec.getCommessa().getIdCommessaPrincipale());
   
    //33950 inizio
    //storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);
    if(ordEsec.getStatoOrdine() == OrdineEsecutivo.IMMESSO)
    	storicoCmm.setDocumentoOrigine(StoricoCommessa.RICHIESTA);
    else
    	storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);    
    //33950 fine
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.PRODUZIONE_PRODOTTO);

    storicoCmm.setIdAnnoOrigine(ordEsec.getIdAnnoOrdine());
    storicoCmm.setIdNumeroOrigine(ordEsec.getIdNumeroOrdine());
    storicoCmm.setIdRigaOrigine(attEsecPrd.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrigine(attEsecPrd.getIdRigaProdotto());
    storicoCmm.setDataOrigine(ordEsec.getDataOrdine());
    //Fix 04361 Mz inizio
    storicoCmm.setNumeroOrgFormattato(ordEsec.getNumeroOrdFmt());
    //Fix 04361 Mz fine

    storicoCmm.setIdAnnoOrdine(ordEsec.getIdAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(ordEsec.getIdNumeroOrdine());
    storicoCmm.setIdRigaOrdine(attEsecPrd.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrdine(attEsecPrd.getIdRigaProdotto());
    storicoCmm.setDataOrdine(ordEsec.getDataOrdine());

    storicoCmm.setAvanzamento(false);

    storicoCmm.setIdArticolo(attEsecPrd.getIdArticolo());
    storicoCmm.setIdVersione(attEsecPrd.getIdVersione() != null ? attEsecPrd.getIdVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazione(attEsecPrd.getIdConfigurazione());
    storicoCmm.setDescrizioneArticolo(attEsecPrd.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);

    String idCmm = attEsecPrd.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() ?
      storicoCmm.getIdCommessa() : null;

    //33143 inizio
    /*
    BigDecimal tmpCostoTotale = recuperoOrdEsecCostoRif(idCmm, attEsecPrd.getCostoRiferimento(),
      attEsecPrd.getIdArticolo(),
      attEsecPrd.getIdVersione(),
      //attEsecPrd.getIdConfigurazione());//31460
      attEsecPrd.getIdConfigurazione(), storicoCmm);//31460
    //31460 inizio
    storicoCmm.setCostoUnitario(tmpCostoTotale);
    storicoCmm.setCostoUnitarioOrigine(attEsecPrd.getCostoRiferimento());
    //31460 fine
    if(tmpCostoTotale != null && attEsecPrd.getQtaResiduaUMPrm() != null)
      tmpCostoTotale = tmpCostoTotale.multiply(attEsecPrd.getQtaResiduaUMPrm());

    storicoCmm.setCostoTotale(tmpCostoTotale != null && tmpCostoTotale.compareTo(new BigDecimal("0")) >= 0 ?
      tmpCostoTotale : new BigDecimal("0"));
    */
    //33143 fine
    storicoCmm.setTipoArticolo(attEsecPrd.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(attEsecPrd.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(attEsecPrd.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(attEsecPrd.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(attEsecPrd.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(attEsecPrd.getArticolo().getArticoloDatiPianif().getIdPianificatore());
    storicoCmm.setIdStabilimento(ordEsec.getIdStabilimento());
    //Fix 04361 Mz inizio
    if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine

    //33143 inizio
    /*
    storicoCmm.setQuantitaUMPrm(attEsecPrd.getQtaResiduaUMPrm());
    storicoCmm.setQuantitaUMSec(attEsecPrd.getQtaResiduaUMSec());
    */
    BigDecimal resPrm = attEsecPrd.getQtaResiduaUMPrm();
    if(resPrm == null || resPrm.compareTo(new BigDecimal("0")) < 0) 
    	resPrm=   new BigDecimal("0");
    storicoCmm.setQuantitaUMPrm(resPrm);

    BigDecimal resSec = attEsecPrd.getQtaResiduaUMSec();
    if(resSec == null || resSec.compareTo(new BigDecimal("0")) < 0) 
    	resSec = new BigDecimal("0");
    storicoCmm.setQuantitaUMSec(resSec);
    //33143 fine

    storicoCmm.setIdUmPrmMag(attEsecPrd.getIdUMPrmMag());
    storicoCmm.setIdUmSecMag(attEsecPrd.getIdUMSecMag());

    storicoCmm.setGesSaldiCommessa(attEsecPrd.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
    if(attEsecPrd.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(attEsecPrd.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(attEsecPrd.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }
    //33143 inizio
    BigDecimal tmpCostoTotale = recuperoOrdEsecCostoRif(idCmm, attEsecPrd.getCostoRiferimento(),
      attEsecPrd.getIdArticolo(),
      attEsecPrd.getIdVersione(),
      //attEsecPrd.getIdConfigurazione());//31460
      attEsecPrd.getIdConfigurazione(), storicoCmm);//31460
    //31460 inizio
    storicoCmm.setCostoUnitario(tmpCostoTotale);
    storicoCmm.setCostoUnitarioOrigine(attEsecPrd.getCostoRiferimento());
    //31460 fine
    if(tmpCostoTotale != null && attEsecPrd.getQtaResiduaUMPrm() != null)
      tmpCostoTotale = tmpCostoTotale.multiply(attEsecPrd.getQtaResiduaUMPrm());

    storicoCmm.setCostoTotale(tmpCostoTotale != null && tmpCostoTotale.compareTo(new BigDecimal("0")) >= 0 ?
      tmpCostoTotale : new BigDecimal("0"));
    //33143 fine
    storicoCmm.setValorizzaCosto(recuperoOrdEsecPrdValorizzaCosto(attEsecPrd.getTipoProdotto(), storicoCmm.getGesSaldiCommessa()));

    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmmOrdPrd(storicoCmm, attEsecPrd);
    // fine fix 10913

    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoTotale())) {
      String artKey = KeyHelper.formatKeyString(storicoCmm.getArticolo().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T089", artKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      /*if(isErroreBloccanteMode())
        return null;*/
    }

    if(!iConsunCmm.isSimulazione() && ConsuntivazioneEstrazione.isSalvaOrdine()) //...FIX 4810
      storicoCmm.save();

    return storicoCmm;
  }

  /**
   * createStoricoOrdEsecRisorsa
   * @param ordEsec OrdineEsecutivo
   * @param attEsecRsr AttivitaEsecRisorsa
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStoricoOrdEsecRisorsa(OrdineEsecutivo ordEsec, AttivitaEsecRisorsa attEsecRsr) throws Exception {

    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(ordEsec.getIdCommessa());
    storicoCmm.setLivelloCommessa(ordEsec.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(ordEsec.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(ordEsec.getCommessa().getIdCommessaPrincipale());

    //33950 inizio
    //storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE); 
    if(ordEsec.getStatoOrdine() == OrdineEsecutivo.IMMESSO)
    	storicoCmm.setDocumentoOrigine(StoricoCommessa.RICHIESTA);
    else
    	storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);    
    //33950 fine
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.PRODUZIONE_RISORSA);

    storicoCmm.setIdAnnoOrigine(ordEsec.getIdAnnoOrdine());
    storicoCmm.setIdNumeroOrigine(ordEsec.getIdNumeroOrdine());
    storicoCmm.setIdRigaOrigine(attEsecRsr.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrigine(attEsecRsr.getIdRigaRisorsa());
    storicoCmm.setDataOrigine(ordEsec.getDataOrdine());
    //Fix 04361 Mz inizio
    storicoCmm.setNumeroOrgFormattato(ordEsec.getNumeroOrdFmt());
    //Fix 04361 Mz fine

    storicoCmm.setIdAnnoOrdine(ordEsec.getIdAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(ordEsec.getIdNumeroOrdine());
    storicoCmm.setIdRigaOrdine(attEsecRsr.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrdine(attEsecRsr.getIdRigaRisorsa());
    storicoCmm.setDataOrdine(ordEsec.getDataOrdine());

    storicoCmm.setIdArticolo(ordEsec.getIdArticolo());
    storicoCmm.setIdVersione(ordEsec.getIdVersione() != null ? ordEsec.getIdVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazione(ordEsec.getIdConfigurazione());
    storicoCmm.setDescrizioneArticolo(ordEsec.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    //...FIX 4810 inizio
    if(attEsecRsr.getAttivitaEsecutiva() != null) {
      storicoCmm.setIdOperazione(attEsecRsr.getAttivitaEsecutiva().getIdOperazione());
    }
    //...FIX 4810 fine

    storicoCmm.setTipoRisorsa(attEsecRsr.getTipoRisorsa());
    storicoCmm.setLivelloRisorsa(attEsecRsr.getLivelloRisorsa());
    storicoCmm.setIdRisorsa(attEsecRsr.getIdRisorsa());
    storicoCmm.setTipoRilevazioneRsr(attEsecRsr.getRisorsa().getTipoRilevazione());

    //31460 inizio
    /*
    storicoCmm.setCostoUnitarioOrigine(attEsecRsr.getCostoRiferimento()); //...FIX 4693 (LP)
    storicoCmm.setCostoUnitario(recuperoOrdEsecRsrCostoRif(attEsecRsr)); //...FIX 4693 (LP)
    storicoCmm.setCostoTotale(recuperoOrdEsecRsrCostoTotale(attEsecRsr));
    */
    //33143 inizio
    /*
    valorizzaCostoOrdEsecRsr(attEsecRsr,storicoCmm);
    //31460 fine
    
    storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    */
    //31513 fine
    //33143 fine
    storicoCmm.setIdStabilimento(ordEsec.getIdStabilimento());
    //Fix 04361 Mz inizio
    if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine

    if(storicoCmm.getTipoRilevazioneRsr() == Risorsa.QUANTITA) {
      //33143 inizio
      /*
      storicoCmm.setQuantitaUMPrm(attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMPrm());
      storicoCmm.setQuantitaUMSec(attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMSec());
      */
      BigDecimal resPrm = attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMPrm();
      if(resPrm == null || resPrm.compareTo(new BigDecimal("0")) < 0) 
    	  resPrm=   new BigDecimal("0");
      storicoCmm.setQuantitaUMPrm(resPrm);

      BigDecimal resSec = attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMSec();
      if(resSec == null || resSec.compareTo(new BigDecimal("0")) < 0) 
    	  resSec= new BigDecimal("0");
      storicoCmm.setQuantitaUMSec(resSec);
      //33143 fine
    }
    if(storicoCmm.getTipoRilevazioneRsr() == Risorsa.TEMPO)
    {
      storicoCmm.setTempo(attEsecRsr.getOreResidue());
      //Fix 10595, 10437 inizio
      if(storicoCmm.getTempo() == null)
         storicoCmm.setTempo(new BigDecimal("0"));
      //Fix 10595, 10437 fine
    }

    storicoCmm.setIdUmPrmMag(ordEsec.getArticolo().getIdUMPrmMag());

    storicoCmm.setGesSaldiCommessa(true);
    //Fix 34138 --inizio 
    /*if(attEsecRsr.getRisorsa() != null) {
      storicoCmm.setIdSchemaCosto(attEsecRsr.getRisorsa().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(attEsecRsr.getRisorsa().getIdComponenteCosto());
      //Fix 33081 --inizio
      if(attEsecRsr.getRisorsa().getLivelloRisorsa() == Risorsa.MATRICOLA && storicoCmm.getIdSchemaCosto() == null)
      {
         Risorsa rapp = attEsecRsr.getRisorsa().getRisorsaAppart();
         if(rapp != null)
         {
            storicoCmm.setIdSchemaCosto(rapp.getIdSchemaCosto());
            storicoCmm.setIdComponenteCosto(rapp.getIdComponenteCosto());
         }
      }
      //Fix 33081 --fine   
    }*/
    trovaSchemaCostoRisorsa(storicoCmm, attEsecRsr.getAttivitaEsecutiva().getAttivita(), attEsecRsr.getRisorsa());
    //Fix 34138 --fine 
    //33143 inizio
    valorizzaCostoOrdEsecRsr(attEsecRsr,storicoCmm);
    //31460 fine
    
    storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    //33143 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmmOrdRis(storicoCmm, attEsecRsr);
    // fine fix 10913

    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoTotale())) {
      String rsrKey = KeyHelper.formatKeyString(storicoCmm.getRisorsa().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T090", rsrKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      /*if(isErroreBloccanteMode())
        return null;*/
    }

    if(!iConsunCmm.isSimulazione() && ConsuntivazioneEstrazione.isSalvaOrdine()) //...FIX 4810
      storicoCmm.save();

    return storicoCmm;
  }

  /**
   * willBeProcessed
   * @param ordEsec OrdineEsecutivo
   * @return boolean
   */
  protected boolean willBeProcessed(OrdineEsecutivo ordEsec) {
    if(ordEsec.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa()
       || (iConsunCmm.isStoriciNonCommessa() && !ordEsec.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa()))//31513
      return true;

    return false;
  }

  /**
   * recuperoOrdEsecCostoRif
   * @param idCommessa String
   * @param costoRif BigDecimal
   * @param idArticolo String
   * @param idVersione Integer
   * @param idConfigurazione Integer
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoOrdEsecCostoRif(String idCommessa, BigDecimal costoRif, String idArticolo, Integer idVersione, Integer idConfigurazione) throws Exception {
    if(costoRif != null && costoRif.compareTo(new BigDecimal("0")) != 0) {
      return costoRif;
    }
    else {
      return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), idArticolo, idVersione, idConfigurazione);
    }
  }
  
  //31460 inizio
  protected BigDecimal recuperoOrdEsecCostoRif(String idCommessa, BigDecimal costoRif, String idArticolo, Integer idVersione, Integer idConfigurazione, StoricoCommessa storicoCmm) throws Exception {
	  return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), idArticolo, idVersione, idConfigurazione, costoRif, storicoCmm);
  }
  
  protected void valorizzaCostoOrdEsecRsr(AttivitaEsecRisorsa attEsecRsr, StoricoCommessa storicoCmm) throws Exception {
	  BigDecimal tmpCosTotale = new BigDecimal("0");
	  BigDecimal tmpCosUnitario = new BigDecimal("0");
	  BigDecimal tmpCosUnitarioOrig = new BigDecimal("0");
	  if(attEsecRsr.getTipoRilevazione() == Risorsa.COSTO && attEsecRsr.isCostoFisso()) {
		  BigDecimal costoPrevisto = attEsecRsr.getCostoPrevisto() != null ? attEsecRsr.getCostoPrevisto() : new BigDecimal("0");
	      BigDecimal costoRilevato = attEsecRsr.getCostoRilevato() != null ? attEsecRsr.getCostoRilevato() : new BigDecimal("0");
	      tmpCosTotale = costoPrevisto.add(costoRilevato.negate());
	      tmpCosUnitario = tmpCosTotale;
	      tmpCosUnitarioOrig = tmpCosUnitario;
	      storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
	  }
	  else if(attEsecRsr.getTipoRilevazione() == Risorsa.COSTO && !attEsecRsr.isCostoFisso()) {
		  BigDecimal costoRif = recuperoOrdEsecRsrCostoRif(attEsecRsr, storicoCmm);
		  if(costoRif == null){
			  tmpCosTotale = null;
			  tmpCosUnitario = null; 
			  tmpCosUnitarioOrig = null;
			  storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
		  }
		  else{			  
			  if(attEsecRsr.getAttivitaEsecutiva().getQtaRichiestaUMPrm() != null){
				  tmpCosTotale = costoRif.multiply(attEsecRsr.getAttivitaEsecutiva().getQtaRichiestaUMPrm());
			  }
			  else{
				  tmpCosTotale = costoRif;
			  }
			  
			  if(attEsecRsr.getCostoRilevato() != null){
				  tmpCosTotale = tmpCosTotale.add(attEsecRsr.getCostoRilevato().negate());
			  }
			  
			  if(tmpCosTotale != null && attEsecRsr.getAttivitaEsecutiva().getQtaRichiestaUMPrm() != null
				 && attEsecRsr.getAttivitaEsecutiva().getQtaRichiestaUMPrm().compareTo(new BigDecimal("0")) != 0){
				  tmpCosUnitario = tmpCosTotale.divide(attEsecRsr.getAttivitaEsecutiva().getQtaRichiestaUMPrm(), BigDecimal.ROUND_HALF_UP);
				  tmpCosUnitarioOrig = tmpCosUnitario;
			  }
			  else{
				  tmpCosUnitario = costoRif;
				  tmpCosUnitarioOrig = attEsecRsr.getCostoRiferimento();  
			  }
		  }
	  }
	  else{
		  tmpCosUnitario = recuperoOrdEsecRsrCostoRif(attEsecRsr, storicoCmm);
		  tmpCosUnitarioOrig = attEsecRsr.getCostoRiferimento();
		  if(tmpCosUnitario != null){
			  if(attEsecRsr.getTipoRilevazione() == Risorsa.TEMPO) {
				  BigDecimal oreRichiesta = attEsecRsr.getOreRichieste() != null ? attEsecRsr.getOreRichieste() : new BigDecimal("0");
				  BigDecimal oreRilevate = attEsecRsr.getOreRilevate() != null ? attEsecRsr.getOreRilevate() : new BigDecimal("0");
				  tmpCosTotale = tmpCosUnitario.multiply(oreRichiesta.add(oreRilevate.negate()));
			  }
			  else if(attEsecRsr.getTipoRilevazione() == Risorsa.QUANTITA) {
				  if(attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMPrm() != null)
					  tmpCosTotale = tmpCosUnitario.multiply(attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMPrm());
			  }
		  }
	  }
	  //33143 inizio
	  //storicoCmm.setCostoTotale(tmpCosTotale);
	  storicoCmm.setCostoTotale(tmpCosTotale != null && tmpCosTotale.compareTo(new BigDecimal("0")) >= 0 ?
			  tmpCosTotale : new BigDecimal("0"));
	  //33143 fine
	  storicoCmm.setCostoUnitario(tmpCosUnitario);
	  storicoCmm.setCostoUnitarioOrigine(tmpCosUnitarioOrig);
  }
  
  protected BigDecimal recuperoOrdEsecRsrCostoRif(AttivitaEsecRisorsa attEsecRsr, StoricoCommessa storicoCmm) throws Exception {
	  if(attEsecRsr.getTipoRilevazione() == Risorsa.COSTO){
		  if(attEsecRsr.getCostoRiferimento() != null && attEsecRsr.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0) {
			  storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
			  return attEsecRsr.getCostoRiferimento();
		  }
		  else {
			  BigDecimal costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(), attEsecRsr.getTipoRisorsa(), attEsecRsr.getLivelloRisorsa(), attEsecRsr.getIdRisorsa());
			  if(costoRif == null || costoRif.compareTo(new BigDecimal("0")) == 0) {
				  if(attEsecRsr.getRisorsa().getRisorsaAppart() != null && attEsecRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa() == Risorsa.RISORSA)
					  costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(),
							  								  attEsecRsr.getRisorsa().getRisorsaAppart().getTipoRisorsa(),
							  								  attEsecRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa(),
							  								  attEsecRsr.getRisorsa().getRisorsaAppart().getIdRisorsa());
			  }	
			  storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_AMBIENTE_MANCANTI);
			  return costoRif;
		  }
	  }
	  else{
		  BigDecimal costoDocumento = attEsecRsr.getCostoRiferimento();
		  if(iConsunCmm.isCostiRisorsaDaDocumento()) {
			  storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
			  return attEsecRsr.getCostoRiferimento();
		  }
		  else {			  
			  if(iConsunCmm.getOrdineRecRisorsa() == ConsuntivazioneCommesse.ORDINE_RECUPERO_DOC_AMB_TIPO){
				  if(costoDocumento != null && costoDocumento.compareTo(new BigDecimal("0")) != 0){
					  storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
					  return costoDocumento;
				  }
				  else{
					  BigDecimal tmpCostoUnitario = recuperoRisorsaCostoUnitario(storicoCmm.getRisorsa(), storicoCmm);
					  if(tmpCostoUnitario != null && tmpCostoUnitario.compareTo(new BigDecimal("0")) != 0){
						  return tmpCostoUnitario;
					  }
	 				  else{
	 					  if(storicoCmm.getRisorsa().getLivelloRisorsa() == Risorsa.MATRICOLA) {
	 						  tmpCostoUnitario = recuperoRisorsaCostoUnitario(storicoCmm.getRisorsa().getRisorsaAppart(), storicoCmm);
	 						  if(tmpCostoUnitario != null && tmpCostoUnitario.compareTo(new BigDecimal("0")) != 0){
	 							  return tmpCostoUnitario;
	 						  }
	 					  }
	 				  }   
				  }
			  }
			  else{
				  BigDecimal tmpCostoUnitario = recuperoRisorsaCostoUnitario(storicoCmm.getRisorsa(), storicoCmm);
				  if(tmpCostoUnitario != null && tmpCostoUnitario.compareTo(new BigDecimal("0")) != 0){
					  return tmpCostoUnitario;
				  }
				  else{
					  if(storicoCmm.getRisorsa().getLivelloRisorsa() == Risorsa.MATRICOLA) {
						  tmpCostoUnitario = recuperoRisorsaCostoUnitario(storicoCmm.getRisorsa().getRisorsaAppart(), storicoCmm);
						  if(tmpCostoUnitario != null && tmpCostoUnitario.compareTo(new BigDecimal("0")) != 0){
							  return tmpCostoUnitario;
						  }
					  }
					  if(costoDocumento != null && costoDocumento.compareTo(new BigDecimal("0")) != 0){
						  storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
						  return costoDocumento;
					  }
				  }
			  }
		  }
	  }
	  return null;
  }
  //31460 fine

  /**
   * recuperoDocPrdRsrCostoRif
   * @param attEsecRsr AttivitaEsecRisorsa
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoOrdEsecRsrCostoRif(AttivitaEsecRisorsa attEsecRsr) throws Exception {
     //Fix 27486 inizio
     if(iConsunCmm.isCostiRisorsaDaDocumento())
     {
        return attEsecRsr.getCostoRiferimento();
     }
     else
     {
        if(attEsecRsr.getCostoRiferimento() != null && attEsecRsr.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0) {
           return attEsecRsr.getCostoRiferimento();
        }
        else {
           BigDecimal costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(), attEsecRsr.getTipoRisorsa(), attEsecRsr.getLivelloRisorsa(), attEsecRsr.getIdRisorsa());
           if(costoRif == null || costoRif.compareTo(new BigDecimal("0")) == 0) {
              if(attEsecRsr.getRisorsa().getRisorsaAppart() != null && attEsecRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa() == Risorsa.RISORSA)
                 costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(),
                       attEsecRsr.getRisorsa().getRisorsaAppart().getTipoRisorsa(),
                       attEsecRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa(),
                       attEsecRsr.getRisorsa().getRisorsaAppart().getIdRisorsa());
           }
           return costoRif;
        }
     }
     //Fix 27486 fine
  }

  /**
   * recuperoOrdEsecRsrCostoTotale
   * @param attEsecRsr AttivitaEsecRisorsa
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoOrdEsecRsrCostoTotale(AttivitaEsecRisorsa attEsecRsr) throws Exception {

    BigDecimal ret = new BigDecimal("0");

    if(attEsecRsr.getTipoRilevazione() == Risorsa.COSTO && attEsecRsr.isCostoFisso()) {
      BigDecimal costoPrevisto = attEsecRsr.getCostoPrevisto() != null ? attEsecRsr.getCostoPrevisto() : new BigDecimal("0");
      BigDecimal costoRilevato = attEsecRsr.getCostoRilevato() != null ? attEsecRsr.getCostoRilevato() : new BigDecimal("0");
      ret = costoPrevisto.add(costoRilevato.negate());
    }
    else {
      BigDecimal costoRif = recuperoOrdEsecRsrCostoRif(attEsecRsr);

      if(costoRif == null)
        return null;

      if(attEsecRsr.getTipoRilevazione() == Risorsa.TEMPO) {
        BigDecimal oreRichiesta = attEsecRsr.getOreRichieste() != null ? attEsecRsr.getOreRichieste() : new BigDecimal("0");
        BigDecimal oreRilevate = attEsecRsr.getOreRilevate() != null ? attEsecRsr.getOreRilevate() : new BigDecimal("0");
        ret = costoRif.multiply(oreRichiesta.add(oreRilevate.negate()));
      }
      else if(attEsecRsr.getTipoRilevazione() == Risorsa.QUANTITA) {
        if(attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMPrm() != null)
          ret = costoRif.multiply(attEsecRsr.getAttivitaEsecutiva().getQtaResiduaUMPrm());
      }
      else if(attEsecRsr.getTipoRilevazione() == Risorsa.COSTO && attEsecRsr.isCostoFisso()) {
        BigDecimal costoPrevisto = attEsecRsr.getCostoPrevisto() != null ? attEsecRsr.getCostoPrevisto() : new BigDecimal("0");
        BigDecimal costoRilevato = attEsecRsr.getCostoRilevato() != null ? attEsecRsr.getCostoRilevato() : new BigDecimal("0");
        ret = costoPrevisto.add(costoRilevato.negate());
      }
      else if(attEsecRsr.getTipoRilevazione() == Risorsa.COSTO && !attEsecRsr.isCostoFisso()) {
        if(attEsecRsr.getAttivitaEsecutiva().getQtaRichiestaUMPrm() != null)
          ret = costoRif.multiply(attEsecRsr.getAttivitaEsecutiva().getQtaRichiestaUMPrm());
        if(attEsecRsr.getCostoRilevato() != null)
          ret = ret.add(attEsecRsr.getCostoRilevato().negate());
      }
    }

    if(ret.compareTo(new BigDecimal("0")) < 0)
      return new BigDecimal("0");
    return ret;
  }

  /**
   * recuperoDocPrdVrsValorizzaCosto
   * @param tipoProdotto char
   * @param gesSaldiCmm boolean
   * @return char
   */
  protected char recuperoOrdEsecPrdValorizzaCosto(char tipoProdotto, boolean gesSaldiCmm) {
    if(tipoProdotto == AttivitaProdProdotto.PRODOTTO_PRIMARIO || tipoProdotto == AttivitaProdProdotto.PRODOTTO_PRIMARIO_ALTRE)
        return StoricoCommessa.INCREMENTA_COSTI_INDIRETTI;

    if((tipoProdotto == AttivitaProdProdotto.SOTTO_PRODOTTO || tipoProdotto == AttivitaProdProdotto.SFRIDO) && !gesSaldiCmm)
    	return StoricoCommessa.DECREMENTA_COSTO;

    if((tipoProdotto == AttivitaProdProdotto.SOTTO_PRODOTTO || tipoProdotto == AttivitaProdProdotto.SFRIDO) && gesSaldiCmm)
    	return StoricoCommessa.NO;

    return StoricoCommessa.NO;    
  }

  /**
   * recuperoAttiviteEsecMateriali
   * @param ordEsec OrdineEsecutivo
   * @return List
   */
  protected List recuperoAttiviteEsecMateriali(OrdineEsecutivo ordEsec) {
    List attEsecMatList = new ArrayList();
    for(Iterator it = ordEsec.getAttivitaEsecutive().iterator(); it.hasNext(); ) {
      AttivitaEsecutiva attEsec = (AttivitaEsecutiva)it.next();
      for(Iterator it2 = attEsec.getMateriali().iterator(); it2.hasNext(); ) {
        AttivitaEsecMateriale attEsecMat = (AttivitaEsecMateriale)it2.next();
        if(attEsecMat.getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO
          && compareStato(attEsecMat.getStatoPrlMateriale(), AttivitaEsecMateriale.COMPLETATO) < 0)
          attEsecMatList.add(attEsecMat);
      }
      attEsec.getMateriali();
    }
    return attEsecMatList;
  }

  /**
   * recuperoAttiviteEsecProdotti
   * @param ordEsec OrdineEsecutivo
   * @return List
   */
  protected List recuperoAttiviteEsecProdotti(OrdineEsecutivo ordEsec) {
    List attEsecPrdList = new ArrayList();
    for(Iterator it = ordEsec.getAttivitaEsecutive().iterator(); it.hasNext(); ) {
      AttivitaEsecutiva attEsec = (AttivitaEsecutiva)it.next();
      for(Iterator it2 = attEsec.getProdotti().iterator(); it2.hasNext(); ) {
        AttivitaEsecProdotto attEsecPrd = (AttivitaEsecProdotto)it2.next();
        if(attEsecPrd.getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO
          && compareStato(attEsecPrd.getStatoVersamento(), AttivitaEsecProdotto.COMPLETATO) < 0
          && compareStato(attEsecPrd.getTipoProdotto(), AttivitaEsecProdotto.SCARTO) < 0)
          attEsecPrdList.add(attEsecPrd);
      }
      attEsec.getProdotti();
    }
    return attEsecPrdList;
  }

  /**
   * recuperoAttiviteEsecRisorse
   * @param ordEsec OrdineEsecutivo
   * @return List
   */
  protected List recuperoAttiviteEsecRisorse(OrdineEsecutivo ordEsec) {
    List attEsecRsrList = new ArrayList();
    for(Iterator it = ordEsec.getAttivitaEsecutive().iterator(); it.hasNext(); ) {
      AttivitaEsecutiva attEsec = (AttivitaEsecutiva)it.next();
      for(Iterator it2 = attEsec.getRisorse().iterator(); it2.hasNext(); ) {
        AttivitaEsecRisorsa attEsecRsr = (AttivitaEsecRisorsa)it2.next();
        if(attEsecRsr.getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO
          && compareStato(attEsecRsr.getStatoUtilizzo(), AttivitaEsecRisorsa.COMPLETATO) < 0)
          attEsecRsrList.add(attEsecRsr);
      }
      attEsec.getRisorse();
    }
    return attEsecRsrList;
  }

  /**
   * compareStato
   * @param stato1 char
   * @param stato2 char
   * @return int
   */
  protected int compareStato(char stato1, char stato2) {
    return new Integer(stato1).compareTo(new Integer(stato2));
  }

  //Fix 04361 Mz inizio
  protected char recuperoOrdEsecMatValorizzaCosto(StoricoCommessa storicoCmm) {
	  return storicoCmm.getGesSaldiCommessa() ? StoricoCommessa.NO : StoricoCommessa.INCREMENTA_COSTO;
  }

  //Fix 04361 Mz fine

  // fix 10913
  public StoricoCommessa completaDatiStoricoCmmAttMat(StoricoCommessa storicoCmm, AttivitaEsecMateriale attEsecMat) throws Exception {
    return storicoCmm;
  }
  public StoricoCommessa completaDatiStoricoCmmOrdPrd(StoricoCommessa storicoCmm, AttivitaEsecProdotto attEsecPrd) throws Exception {
    return storicoCmm;
  }
  public StoricoCommessa completaDatiStoricoCmmOrdRis(StoricoCommessa storicoCmm, AttivitaEsecRisorsa attEsecRsr) throws Exception {
    return storicoCmm;
  }
  // fine fix 10913
  
  //Fix 34308 --inizio
  public GestoreCommit getGestoreCommit() {
	 return iGestoreCommit;
  }

  public void setGestoreCommit(GestoreCommit gestoreCommit) {
	  iGestoreCommit = gestoreCommit;
  }
  //Fix 34308 --fine
}

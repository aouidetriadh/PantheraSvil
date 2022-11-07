package it.thera.thip.produzione.commessa;

import java.math.*;
import java.util.*;

import com.thera.thermfw.persist.*;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.modpro.*;
import it.thera.thip.produzione.documento.*;
import it.thera.thip.produzione.ordese.*;
// FIX 9278 - inizio
import it.thera.thip.base.articolo.*;
// FIX 9278 - fine

/**
 * EstrazioneDocPrd
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 23/05/2005 at 11:20:32
 */
/*
 * Revisions:
 * Number  Date          Owner     Description
 * 04810   05/12/2005    LP        Aggiunto riempimento campo IdOperazione
 * 09278   magg 2008     LR        createStorici scarta materiali/prodotti WIP
 * 10411   28/01/2009    DBot      Logger per performance su consuntivazione
 * 10595   23/03/2009    DBot      Gestione di tempo nullo su risosrse a tempo
 * 10537   30/03/2009    DBot      Riallineamento per fix WEB
 * 10913   25/05/2009    DB      Per permettere la personalizzazione
 * 12837   08/09/2010    Mekki     Gestiti EsclusoDaCosti sul materiale
 * 18394   06/09/2013    AA        Aggiunto la condizioni sull'attività
 * 27486   25/05/2018    DBot      Reperimento costi risorsa da documento
 * 29960   09/10/2019    RA		   Consuntivazione in modalità definitiva eseguibile per singola commessa/ambiente commessa
 * 31460   03/07/2020    RA		   Modifica valorozzazione CostoUnitario del storico per materiale/prodotti/risorse
 * 31513   08/09/2020	 RA		   Aggiunto gestione articoli senza gestione saldi per commessa
 * 32985   24/02/2021    RA		   Corretta controllo nel metodo willBeProcessed
 * 33081   08/03/2021    DBot      Correzioni varie
 * 33143   17/03/2021    RA		   Corretta valorizzazione costo 
 * 34308   21/09/2021    Mekki     GestoreCommit
 * 34138   16/10/2021    Mekki     Nuovo modalità di recupero di schema/Comp. costo
 * 33950   06/10/2021    RA        modifica il metodo willBeProcessed 
 */

public class EstrazioneDocPrd extends EstrazioneDocumenti {

   boolean storicoNonCommessa = false; //31513
   
   protected GestoreCommit iGestoreCommit; //Fix 34308
   
  /**
   * Constructor
   * @param consunCmm ConsuntivazioneCommesse
   * @param compactCmm CompactCommessa
   */
  public EstrazioneDocPrd(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm) {
    super(consunCmm, compactCmm);
  }

  /**
   * processRighe
   * @throws Exception
   * @return boolean
   */
  protected boolean processRighe() throws Exception {
    getLogger().iniziaTipoDocumento("DOC_PRD", iCompactCmm.getIdCommessa()); //Fix 10411
    String where = DocumentoProduzioneTM.ID_AZIENDA + "='" + iCompactCmm.getIdAzienda() + "' AND "
      + DocumentoProduzioneTM.R_COMMESSA + "='" + iCompactCmm.getIdCommessa() + "' AND "
      + DocumentoProduzioneTM.STATO + "='" + DatiComuniEstesi.VALIDO + "'";

    getLogger().startTime(); //Fix 10411
    List docAcqList = DocumentoProduzione.retrieveList(DocumentoProduzione.class, where, DocumentoProduzioneTM.R_COMMESSA, false);
    getLogger().addTempoLettura(getLogger().stopTime()); // Fix 10411
    getLogger().incNumTestateLette(docAcqList.size()); // Fix 10411

    for(Iterator it = docAcqList.iterator(); it.hasNext(); ) {
      DocumentoProduzione docPrd = (DocumentoProduzione)it.next();
      if(willBeProcessed(docPrd)) {
    	//31513 inizio         
    	if(!docPrd.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() && iConsunCmm.iStoriciNonCommessa)
    		storicoNonCommessa = true;
    	else
    		storicoNonCommessa = false;
    	//31513 fine         
         
        getLogger().incNumTestateFatte(); // Fix 10411
        boolean ret = createStorici(docPrd);
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
   * @param docPrd DocumentoProduzione
   * @throws Exception
   * @return boolean
   */
  protected boolean createStorici(DocumentoProduzione docPrd) throws Exception {

    for(Iterator it = docPrd.getMaterialiColl().iterator(); it.hasNext(); ) {
      getLogger().incNumRigheLette(1); // Fix 10411
      DocumentoPrdRigaMateriale docPrdMat = (DocumentoPrdRigaMateriale)it.next();
      // FIX 9278 - inizio
      if (docPrdMat.getArticolo().getTipoParte() == ArticoloDatiIdent.ARTICOLO_WIP) {
          continue;
      }
      // FIX 9278 - fine
      getLogger().incNumRigheFatte(); // Fix 10411
      getLogger().startTime(); //Fix 10411
      StoricoCommessa storicoCmm = createStoricoDocPrdMateriale(docPrd, docPrdMat);
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

    for(Iterator it = docPrd.getVersamentiColl().iterator(); it.hasNext(); ) {
      getLogger().incNumRigheLette(1); // Fix 10411
      DocumentoPrdRigaVersamento docPrdVrs = (DocumentoPrdRigaVersamento)it.next();

      // FIX 9278 - inizio
      if (docPrdVrs.getTipoProdotto() == AttivitaProdProdotto.ARTICOLO_WIP) {
          continue;
      }
      // FIX 9278 - fine

      getLogger().incNumRigheFatte(); // Fix 10411
      getLogger().startTime();//Fix 10411
      StoricoCommessa storicoCmm = createStoricoDocPrdProdotto(docPrd, docPrdVrs);
      getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

      if(storicoCmm == null) {
        iConsunCmm.commitWithGestoreCommit(false);
        return false;
      }

      getLogger().startTime();//Fix 10411
      attribuzioneCosti(storicoCmm);
      getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411

      iConsunCmm.commitWithGestoreCommit(false);
    }

    List docPrdRsrList = docPrd.getRisorseColl();
    getLogger().incNumRigheLette(1); // Fix 10411
    if(docPrdRsrList.size() == 0 && docPrd.getCausale().getAvanzamento()) {

      getLogger().incNumRigheFatte(); // Fix 10411
      getLogger().startTime();//Fix 10411
      StoricoCommessa storicoCmm = createStoricoDocPrdRisorsa(docPrd);
      getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

      if(storicoCmm == null) {
        iConsunCmm.commitWithGestoreCommit(false);
        return false;
      }

      getLogger().startTime();//Fix 10411
      attribuzioneCosti(storicoCmm);
      getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411

      iConsunCmm.commitWithGestoreCommit(false);
    }
    else {
      Risorsa risorsaPrincipale = recuperoRisorsaPrincipale(docPrd);
      boolean rsrPrincipaleProcessed = false;
      for(int i = 0; i < docPrdRsrList.size(); i++) {
        getLogger().incNumRigheLette(1); // Fix 10411
        DocumentoPrdRigaRisorsa docPrdRsr = (DocumentoPrdRigaRisorsa)docPrdRsrList.get(i);
        if(docPrd.getCausale().getAvanzamento()) {
          if(docPrdRsr.getRisorsa().equals(risorsaPrincipale)) {
            rsrPrincipaleProcessed = true;
            getLogger().incNumRigheFatte(); // Fix 10411
            getLogger().startTime();//Fix 10411
            StoricoCommessa storicoCmm = createStoricoDocPrdRisorsa(docPrd, docPrdRsr, true);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

            if(storicoCmm == null) {
              iConsunCmm.commitWithGestoreCommit(false);
              return false;
            }

            getLogger().startTime();//Fix 10411
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411

            iConsunCmm.commitWithGestoreCommit(false);
          }
          else if(i == docPrdRsrList.size() - 1 && (risorsaPrincipale == null || !rsrPrincipaleProcessed)) {
            getLogger().incNumRigheFatte(); // Fix 10411
            getLogger().startTime();//Fix 10411
            StoricoCommessa storicoCmm = createStoricoDocPrdRisorsa(docPrd, docPrdRsr, true);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

            if(storicoCmm == null) {
              iConsunCmm.commitWithGestoreCommit(false);
              return false;
            }

            getLogger().startTime();//Fix 10411
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411

            iConsunCmm.commitWithGestoreCommit(false);
          }
          else {
            getLogger().incNumRigheFatte(); // Fix 10411
            getLogger().startTime();//Fix 10411
            StoricoCommessa storicoCmm = createStoricoDocPrdRisorsa(docPrd, docPrdRsr, false);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

            if(storicoCmm == null) {
              iConsunCmm.commitWithGestoreCommit(false);
              return false;
            }

            getLogger().startTime();//Fix 10411
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411

            iConsunCmm.commitWithGestoreCommit(false);
          }
        }
        else {
          getLogger().incNumRigheFatte(); // Fix 10411
          getLogger().startTime();//Fix 10411
          StoricoCommessa storicoCmm = createStoricoDocPrdRisorsa(docPrd, docPrdRsr, false);
          getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

          if(storicoCmm == null) {
            iConsunCmm.commitWithGestoreCommit(false);
            return false;
          }

          getLogger().startTime();//Fix 10411
          attribuzioneCosti(storicoCmm);
          getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());  // Fix 10411

          iConsunCmm.commitWithGestoreCommit(false);
        }
      }
    }
    return true;
  }

  /**
   * createStoricoDocPrdMateriale
   * @param docPrd DocumentoProduzione
   * @param docPrdRigMat DocumentoPrdRigaMateriale
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStoricoDocPrdMateriale(DocumentoProduzione docPrd, DocumentoPrdRigaMateriale docPrdRigMat) throws Exception {
    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(docPrd.getIdCommessa());
    storicoCmm.setLivelloCommessa(docPrd.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(docPrd.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(docPrd.getCommessa().getIdCommessaPrincipale());
    if(docPrdRigMat.getCommessa() != null && !docPrdRigMat.getCommessa().equals(docPrd.getCommessa()))
      storicoCmm.setIdCommessaCol(docPrdRigMat.getRCommessa());
    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.PRODUZIONE_MATERIALE);
    storicoCmm.setIdAnnoOrigine(docPrd.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docPrd.getNumeroDocumento());
    storicoCmm.setIdRigaOrigine(docPrdRigMat.getIdRigaDoc());
    storicoCmm.setNumeroOrgFormattato(docPrd.getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docPrd.getDataDichiarazione());
    storicoCmm.setIdCauOrgTes(docPrd.getRCauDocPrd());
    storicoCmm.setAvanzamento(false);
    storicoCmm.setIdCauMagazzino(docPrdRigMat.getRCauMagMat());

    if(docPrd.getCausale().getAbilitaMateriali() == CausaleDocProduzione.PRELIEVO_MANUALE
      || docPrd.getCausale().getAbilitaMateriali() == CausaleDocProduzione.PRELIEVO_AUTO
      || docPrd.getCausale().getAbilitaMateriali() == CausaleDocProduzione.SCARTI
      || docPrd.getCausale().getAbilitaMateriali() == CausaleDocProduzione.PRELIEVO_MAN_AUTO)
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.USCITA);
    else if(docPrd.getCausale().getAbilitaMateriali() == CausaleDocProduzione.RESO)
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.ENTRATA);

    storicoCmm.setIdMagazzino(docPrdRigMat.getRMagazzino());
    storicoCmm.setIdArticolo(docPrdRigMat.getRArticolo());
    storicoCmm.setIdVersione(docPrdRigMat.getRVersione() != null ? docPrdRigMat.getRVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docPrdRigMat.getRConfigurazione());

    storicoCmm.setDescrizioneArticolo(docPrdRigMat.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setIdAttivita(docPrd.getRAttivita());
    storicoCmm.setIdDipendente(docPrd.getRDipendente());
    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
    storicoCmm.setQuantitaUMPrm(docPrdRigMat.getQuantitaUMPrm());
    storicoCmm.setQuantitaUMSec(docPrdRigMat.getQuantitaUMSec());

    storicoCmm.setIdUmPrmMag(docPrdRigMat.getRUmPrmMag());
    storicoCmm.setIdUmSecMag(docPrdRigMat.getRUmSecMag());

    String idCmm = null;
    if(docPrdRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
      idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

    //33143 inizio
    /*  
    //Fix 12837 --inizio
    if(docPrdRigMat.isEsclusoDaCosti()){
      storicoCmm.setCostoUnitario(new BigDecimal("0"));
    }
    else{ //Fix 12837 --fine
      //storicoCmm.setCostoUnitario(recuperoDocPrdMatCostoUnitario(idCmm, docPrdRigMat));//31460
      storicoCmm.setCostoUnitario(recuperoDocPrdMatCostoUnitario(idCmm, docPrdRigMat, storicoCmm));//31460
    }

    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

    storicoCmm.setCostoUnitarioOrigine(docPrdRigMat.getCostoRiferimento());

    storicoCmm.setGesSaldiCommessa(docPrdRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());

    storicoCmm.setValorizzaCosto(recuperoDocPrdMatValorizzaCosto(storicoCmm));
    
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    */
    //33143 fine
    
    storicoCmm.setNoFatturare(false);

    if(docPrdRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(docPrdRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(docPrdRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }

    storicoCmm.setIdAnnoOrdine(docPrd.getAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(docPrd.getNumeroOrdine());
    storicoCmm.setIdRigaOrdine(docPrd.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrdine(docPrdRigMat.getIdRigaMateriale());
    if(docPrd.getOrdineEsecutivo() != null) {
      storicoCmm.setDataOrdine(docPrd.getOrdineEsecutivo().getDataOrdine());
      storicoCmm.setIdCliente(docPrd.getOrdineEsecutivo().getIdCliente());
    }

    storicoCmm.setTipoArticolo(docPrdRigMat.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(docPrdRigMat.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(docPrdRigMat.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(docPrdRigMat.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(docPrdRigMat.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(docPrdRigMat.getArticolo().getArticoloDatiPianif().getIdPianificatore());

    storicoCmm.setIdStabilimento(docPrd.getRStabilimento());
    //Fix 04361 Mz inizio
    if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine

    storicoCmm.setIdReparto(docPrd.getRReparto());
    storicoCmm.setIdCentroLavoro(docPrd.getRCentroLavoro());
    storicoCmm.setIdCentroCosto(docPrd.getRCentroCosto());
    storicoCmm.setIdArticoloPrd(docPrd.getRArticolo());
    storicoCmm.setIdVersionePrd(docPrd.getRVersione() != null ? docPrd.getRVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazionePrd(docPrd.getRConfigurazione());
    //33143 inizio
    //Fix 12837 --inizio
    if(docPrdRigMat.isEsclusoDaCosti()){
      storicoCmm.setCostoUnitario(new BigDecimal("0"));
    }
    else{ //Fix 12837 --fine
      //storicoCmm.setCostoUnitario(recuperoDocPrdMatCostoUnitario(idCmm, docPrdRigMat));//31460
      storicoCmm.setCostoUnitario(recuperoDocPrdMatCostoUnitario(idCmm, docPrdRigMat, storicoCmm));//31460
    }

    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

    storicoCmm.setCostoUnitarioOrigine(docPrdRigMat.getCostoRiferimento());

    storicoCmm.setGesSaldiCommessa(docPrdRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());

    storicoCmm.setValorizzaCosto(recuperoDocPrdMatValorizzaCosto(storicoCmm));
    
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    //33143 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmmPrdMat(storicoCmm, docPrdRigMat);
    // fine fix 10913


    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario())) {
      String artKey = KeyHelper.formatKeyString(storicoCmm.getArticolo().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T089", artKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      if(isErroreBloccanteMode())
        return null;
    }

    if(!iConsunCmm.isSimulazione())
      storicoCmm.save();

    return storicoCmm;
  }

  /**
   * createStoricoDocPrdProdotto
   * @param docPrd DocumentoProduzione
   * @param docPrdRigVrs DocumentoPrdRigaVersamento
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStoricoDocPrdProdotto(DocumentoProduzione docPrd, DocumentoPrdRigaVersamento docPrdRigVrs) throws Exception {
    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(docPrd.getIdCommessa());
    storicoCmm.setLivelloCommessa(docPrd.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(docPrd.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(docPrd.getCommessa().getIdCommessaPrincipale());
    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.PRODUZIONE_PRODOTTO);
    storicoCmm.setIdAnnoOrigine(docPrd.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docPrd.getNumeroDocumento());
    storicoCmm.setIdRigaOrigine(docPrdRigVrs.getIdRigaDoc());
    storicoCmm.setNumeroOrgFormattato(docPrd.getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docPrd.getDataDichiarazione());
    storicoCmm.setIdCauOrgTes(docPrd.getRCauDocPrd());
    storicoCmm.setAvanzamento(false);
    storicoCmm.setIdCauMagazzino(docPrdRigVrs.getRCauMagVrs());
    storicoCmm.setAzioneMagazzino(AzioneMagazzino.ENTRATA);
    storicoCmm.setIdMagazzino(docPrdRigVrs.getRMagazzino());
    storicoCmm.setIdArticolo(docPrdRigVrs.getRArticolo());
    storicoCmm.setIdVersione(docPrdRigVrs.getRVersione() != null ? docPrdRigVrs.getRVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docPrdRigVrs.getRConfigurazione());
    storicoCmm.setDescrizioneArticolo(docPrdRigVrs.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setIdAttivita(docPrd.getRAttivita());
    storicoCmm.setIdDipendente(docPrd.getRDipendente());
    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
    storicoCmm.setQuantitaUMPrm(docPrdRigVrs.getQuantitaUmPrm());
    storicoCmm.setQuantitaUMSec(docPrdRigVrs.getQuantitaUmSec());

    storicoCmm.setIdUmPrmMag(docPrdRigVrs.getRUmPrmMag());
    storicoCmm.setIdUmSecMag(docPrdRigVrs.getRUmSecMag());

    String idCmm = null;
    if(docPrdRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
      idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

    //33143 inizio
      /*
    //storicoCmm.setCostoUnitario(recuperoDocPrdVrsCostoUnitario(idCmm, docPrdRigVrs));//31460
    storicoCmm.setCostoUnitario(recuperoDocPrdVrsCostoUnitario(idCmm, docPrdRigVrs, storicoCmm));//31460

    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

    storicoCmm.setCostoUnitarioOrigine(docPrdRigVrs.getCostoRiferimento());
    storicoCmm.setGesSaldiCommessa(docPrdRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());

    storicoCmm.setValorizzaCosto(recuperoDocPrdVrsValorizzaCosto(docPrdRigVrs.getTipoProdotto(), storicoCmm.getGesSaldiCommessa()));
    
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    */
    //33143 fine
    storicoCmm.setNoFatturare(false);

    if(docPrdRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(docPrdRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(docPrdRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }

    storicoCmm.setIdAnnoOrdine(docPrd.getAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(docPrd.getNumeroOrdine());
    storicoCmm.setIdRigaOrdine(docPrd.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrdine(docPrdRigVrs.getIdRigaProdotto());
    if(docPrd.getOrdineEsecutivo() != null) {
      storicoCmm.setDataOrdine(docPrd.getOrdineEsecutivo().getDataOrdine());
      storicoCmm.setIdCliente(docPrd.getOrdineEsecutivo().getIdCliente());
    }

    storicoCmm.setTipoArticolo(docPrdRigVrs.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(docPrdRigVrs.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(docPrdRigVrs.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(docPrdRigVrs.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(docPrdRigVrs.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(docPrdRigVrs.getArticolo().getArticoloDatiPianif().getIdPianificatore());

    storicoCmm.setIdStabilimento(docPrd.getRStabilimento());
    //Fix 04361 Mz inizio
    if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine
    storicoCmm.setIdReparto(docPrd.getRReparto());
    storicoCmm.setIdCentroLavoro(docPrd.getRCentroLavoro());
    storicoCmm.setIdCentroCosto(docPrd.getRCentroCosto());

    if(!docPrdRigVrs.getArticolo().equals(docPrd.getArticolo())) {
      storicoCmm.setIdArticoloPrd(docPrdRigVrs.getRArticolo());
      storicoCmm.setIdVersionePrd(docPrdRigVrs.getRVersione() != null ? docPrdRigVrs.getRVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazionePrd(docPrdRigVrs.getRConfigurazione());
    }
    //33143 inizio
    //storicoCmm.setCostoUnitario(recuperoDocPrdVrsCostoUnitario(idCmm, docPrdRigVrs));//31460
    storicoCmm.setCostoUnitario(recuperoDocPrdVrsCostoUnitario(idCmm, docPrdRigVrs, storicoCmm));//31460

    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

    storicoCmm.setCostoUnitarioOrigine(docPrdRigVrs.getCostoRiferimento());
    storicoCmm.setGesSaldiCommessa(docPrdRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());

    storicoCmm.setValorizzaCosto(recuperoDocPrdVrsValorizzaCosto(docPrdRigVrs.getTipoProdotto(), storicoCmm.getGesSaldiCommessa()));
    
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    //33143 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmmPrdPrd(storicoCmm, docPrdRigVrs);
    // fine fix 10913

    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario())) {
      String artKey = KeyHelper.formatKeyString(storicoCmm.getArticolo().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T089", artKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      if(isErroreBloccanteMode())
        return null;
    }

    if(!iConsunCmm.isSimulazione())
      storicoCmm.save();

    return storicoCmm;
  }

  /**
   * createStoricoDocPrdRisorsa
   * @param docPrd DocumentoProduzione
   * @param docPrdRigRsr DocumentoPrdRigaRisorsa
   * @param setYesToAvanzame boolean
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStoricoDocPrdRisorsa(DocumentoProduzione docPrd, DocumentoPrdRigaRisorsa docPrdRigRsr, boolean setYesToAvanzame) throws Exception {
    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(docPrd.getIdCommessa());
    storicoCmm.setLivelloCommessa(docPrd.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(docPrd.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(docPrd.getCommessa().getIdCommessaPrincipale());
    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.PRODUZIONE_RISORSA);
    storicoCmm.setIdAnnoOrigine(docPrd.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docPrd.getNumeroDocumento());
    storicoCmm.setIdRigaOrigine(docPrdRigRsr.getIdRigaDoc());
    storicoCmm.setNumeroOrgFormattato(docPrd.getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docPrd.getDataDichiarazione());
    storicoCmm.setIdCauOrgTes(docPrd.getRCauDocPrd());
    storicoCmm.setIdCauOrgRig(docPrdRigRsr.getRCauUtiRsr());
    storicoCmm.setAvanzamento(setYesToAvanzame);
    storicoCmm.setAzioneMagazzino(AzioneMagazzino.NESSUNA_AZIONE);
    storicoCmm.setIdArticolo(docPrd.getRArticolo());
    storicoCmm.setIdVersione(docPrd.getRVersione() != null ? docPrd.getRVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docPrd.getRConfigurazione());
    storicoCmm.setDescrizioneArticolo(docPrd.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    //Fix 04361 Mz inizio
    storicoCmm.setIdArticoloPrd(docPrd.getRArticolo());
    storicoCmm.setIdVersionePrd(docPrd.getRVersione());
    storicoCmm.setIdConfigurazionePrd(docPrd.getRConfigurazione());
    //Fix 04361 Mz fine

    storicoCmm.setIdAttivita(docPrd.getRAttivita());
    //...FIX 4810 inizio
    if(docPrd.getAttivitaEsecutiva() != null) {
      storicoCmm.setIdOperazione(docPrd.getAttivitaEsecutiva().getIdOperazione());
    }
    //...FIX 4810 fine
    storicoCmm.setIdDipendente(docPrd.getRDipendente());
    storicoCmm.setTipoRisorsa(docPrdRigRsr.getRTipoRisorsa());
    storicoCmm.setLivelloRisorsa(docPrdRigRsr.getRLivelloRisorsa());
    storicoCmm.setIdRisorsa(docPrdRigRsr.getRRisorsa());
    storicoCmm.setTipoRilevazioneRsr(docPrdRigRsr.getRisorsa().getTipoRilevazione());
    storicoCmm.setQuantitaUMPrm(docPrdRigRsr.getQuantita());
    storicoCmm.setQtaScarto(docPrdRigRsr.getQtaScarto());
    storicoCmm.setTempo(docPrdRigRsr.getOreMillesimi());
    //Fix 10595, 10437 inizio
    if(storicoCmm.getTempo() == null)
       storicoCmm.setTempo(new BigDecimal("0"));
    //Fix 10595, 10437 fine
    storicoCmm.setIdUmPrmMag(docPrd.getArticolo().getIdUMPrmMag());

    //31460 inizio
    /*
    storicoCmm.setCostoUnitario(recuperoDocPrdRsrCostoUnitario(docPrdRigRsr, storicoCmm.getQuantitaUMPrm(), storicoCmm.getQtaScarto()));

    if(storicoCmm.getCostoUnitario() != null) {
      if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.TEMPO) {
         //Fix 10595, 10437 inizio
         if(storicoCmm.getTempo() != null)
            storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getTempo()));
        //Fix 10595, 10437 fine
      }

      else if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.QUANTITA) {
        BigDecimal qtaUmPrm = storicoCmm.getQuantitaUMPrm() != null ? storicoCmm.getQuantitaUMPrm() : new BigDecimal("0");
        BigDecimal qtaScarta = storicoCmm.getQtaScarto() != null ? storicoCmm.getQtaScarto() : new BigDecimal("0");
        BigDecimal tmpQta = qtaUmPrm.add(qtaScarta);
        storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(tmpQta));
      }
    }

    if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO)
      storicoCmm.setCostoTotale(docPrdRigRsr.getCostoRilevato());

    if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO) {
      BigDecimal qtaUmPrm = storicoCmm.getQuantitaUMPrm() != null ? storicoCmm.getQuantitaUMPrm() : new BigDecimal("0");
      BigDecimal qtaScarta = storicoCmm.getQtaScarto() != null ? storicoCmm.getQtaScarto() : new BigDecimal("0");
      BigDecimal tmpQta = qtaUmPrm.add(qtaScarta);
      if(docPrdRigRsr.getCostoRilevato() != null && tmpQta.compareTo(new BigDecimal("0")) != 0) {
        storicoCmm.setCostoUnitarioOrigine(docPrdRigRsr.getCostoRilevato().divide(tmpQta, BigDecimal.ROUND_HALF_UP));
      }
    }
    else
      storicoCmm.setCostoUnitarioOrigine(docPrdRigRsr.getCostoRiferimento());
    */
    //33143 inizio
    /*
    valorizzaCostoDocPrdRigRsr(docPrdRigRsr, storicoCmm);
    //31460 fine
    //31513 inizio
    if(storicoCmm.getGesSaldiCommessa())
    	storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
    else 
    	storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);

    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    */
    //31513 fine
    //33143 fine
    storicoCmm.setGesSaldiCommessa(false);
    storicoCmm.setNoFatturare(false);
    //Fix 34138 --inizio
    /*if(docPrdRigRsr.getRisorsa() != null) {
      storicoCmm.setIdSchemaCosto(docPrdRigRsr.getRisorsa().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(docPrdRigRsr.getRisorsa().getIdComponenteCosto());
      //Fix 33081 --inizio
      if(docPrdRigRsr.getRisorsa().getLivelloRisorsa() == Risorsa.MATRICOLA && storicoCmm.getIdSchemaCosto() == null)
      {
         Risorsa rapp = docPrdRigRsr.getRisorsa().getRisorsaAppart();
         if(rapp != null)
         {
            storicoCmm.setIdSchemaCosto(rapp.getIdSchemaCosto());
            storicoCmm.setIdComponenteCosto(rapp.getIdComponenteCosto());
         }
      }
      //Fix 33081 --fine 
    }*/
    trovaSchemaCostoRisorsa(storicoCmm, docPrd.getAttivita(), docPrdRigRsr.getRisorsa());
    //Fix 34138 --fine
    storicoCmm.setIdAnnoOrdine(docPrd.getAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(docPrd.getNumeroOrdine());
    storicoCmm.setIdRigaOrdine(docPrd.getIdRigaAttivita());
    storicoCmm.setIdDetRigaOrdine(docPrdRigRsr.getIdRigaRisorsa());
    if(docPrd.getOrdineEsecutivo() != null) {
      storicoCmm.setDataOrdine(docPrd.getOrdineEsecutivo().getDataOrdine());
      storicoCmm.setIdCliente(docPrd.getOrdineEsecutivo().getIdCliente());
    }

    storicoCmm.setIdStabilimento(docPrd.getRStabilimento());
    //Fix 04361 Mz inizio
    if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine
    storicoCmm.setIdReparto(docPrd.getRReparto());
    storicoCmm.setIdCentroLavoro(docPrd.getRCentroLavoro());
    storicoCmm.setIdCentroCosto(docPrd.getRCentroCosto());
    //33143 inizio
    valorizzaCostoDocPrdRigRsr(docPrdRigRsr, storicoCmm);
    if(storicoCmm.getGesSaldiCommessa())
    	storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
    else 
    	storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);

    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    //33143 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmmPrdRis(storicoCmm, docPrdRigRsr);
    // fine fix 10913

    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario())) {
      String rsrKey = KeyHelper.formatKeyString(storicoCmm.getRisorsa().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T090", rsrKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      if(isErroreBloccanteMode())
        return null;
    }

    if(!iConsunCmm.isSimulazione())
      storicoCmm.save();

    return storicoCmm;
  }

  /**
   * createStoricoDocPrdRisorsa
   * @param docPrd DocumentoProduzione
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStoricoDocPrdRisorsa(DocumentoProduzione docPrd) throws Exception {
    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(docPrd.getIdCommessa());
    storicoCmm.setLivelloCommessa(docPrd.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(docPrd.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(docPrd.getCommessa().getIdCommessaPrincipale());
    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.PRODUZIONE_RISORSA);
    storicoCmm.setIdAnnoOrigine(docPrd.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docPrd.getNumeroDocumento());
    storicoCmm.setNumeroOrgFormattato(docPrd.getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docPrd.getDataDichiarazione());
    storicoCmm.setIdCauOrgTes(docPrd.getRCauDocPrd());
    storicoCmm.setAvanzamento(true);
    storicoCmm.setAzioneMagazzino(AzioneMagazzino.NESSUNA_AZIONE);
    storicoCmm.setIdArticolo(docPrd.getRArticolo());
    storicoCmm.setIdVersione(docPrd.getRVersione() != null ? docPrd.getRVersione() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docPrd.getRConfigurazione());
    storicoCmm.setDescrizioneArticolo(docPrd.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    //Fix 04361 Mz inizio
    storicoCmm.setIdArticoloPrd(docPrd.getRArticolo());
    storicoCmm.setIdVersionePrd(docPrd.getRVersione());
    storicoCmm.setIdConfigurazionePrd(docPrd.getRConfigurazione());
    //Fix 04361 Mz fine

    storicoCmm.setIdAttivita(docPrd.getRAttivita());
    //...FIX 4810 inizio
    if(docPrd.getAttivitaEsecutiva() != null) {
      storicoCmm.setIdOperazione(docPrd.getAttivitaEsecutiva().getIdOperazione());
    }
    //...FIX 4810 fine
    storicoCmm.setIdDipendente(docPrd.getRDipendente());
    storicoCmm.setQuantitaUMPrm(docPrd.getQuantita());
    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setQtaScarto(docPrd.getQtaScarto());
    storicoCmm.setTempo(docPrd.getOreMillesimi());
    //Fix 10595, 10437 inizio
    if(storicoCmm.getTempo() == null)
       storicoCmm.setTempo(new BigDecimal("0"));
    //Fix 10595, 10437 fine
    storicoCmm.setIdUmPrmMag(docPrd.getArticolo().getIdUMPrmMag());

    //33143 inizio
    /*
    storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
    
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    */
    //33143 fine
    
    storicoCmm.setGesSaldiCommessa(false);
    storicoCmm.setNoFatturare(false);

    storicoCmm.setIdAnnoOrdine(docPrd.getAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(docPrd.getNumeroOrdine());
    storicoCmm.setIdRigaOrdine(docPrd.getIdRigaAttivita());
    if(docPrd.getOrdineEsecutivo() != null) {
      storicoCmm.setDataOrdine(docPrd.getOrdineEsecutivo().getDataOrdine());
      storicoCmm.setIdCliente(docPrd.getOrdineEsecutivo().getIdCliente());
    }

    storicoCmm.setIdStabilimento(docPrd.getRStabilimento());
    //Fix 04361 Mz inizio
    if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine
    storicoCmm.setIdReparto(docPrd.getRReparto());
    storicoCmm.setIdCentroLavoro(docPrd.getRCentroLavoro());
    storicoCmm.setIdCentroCosto(docPrd.getRCentroCosto());
    //33143 inizio
    storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
    
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    //33143 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmmPrdRis(storicoCmm, docPrd);
    // fine fix 10913

    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario())) {
      String rsrKey = KeyHelper.formatKeyString(storicoCmm.getRisorsa().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T090", rsrKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      if(isErroreBloccanteMode())
        return null;
    }

    if(!iConsunCmm.isSimulazione())
      storicoCmm.save();

    return storicoCmm;
  }

  /**
   * willBeProcessed
   * @param docPrd DocumentoProduzione
   * @return boolean
   */
  protected boolean willBeProcessed(DocumentoProduzione docPrd) {
	//32985 inizio
	if(docPrd.getArticolo() == null)
		return false;	
    //if((docPrd.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() || (docPrd.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() && iConsunCmm.iStoriciNonCommessa))//31513
	if((docPrd.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() || (!docPrd.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() && iConsunCmm.iStoriciNonCommessa))//31513
    //32985 fine 
      && docPrd.getAttivita() != null && docPrd.getAttivita().getTipoAttivita() != Attivita.PROD_ESTERNA //Fix 18394
      && docPrd.getDataDichiarazione().compareTo(iConsunCmm.getDataRiferimento()) <= 0
      //29960 inizio      
      //&& (iConsunCmm.getDataUltChiusDefAmbiente() == null
      //|| docPrd.getDataDichiarazione().compareTo(iConsunCmm.getDataUltChiusDefAmbiente()) > 0))
      //33950 inizio
      /*&& (iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente()) == null 
      || docPrd.getDataDichiarazione().compareTo(iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente())) > 0))*/
	  && iConsunCmm.isDataValido(docPrd.getDataDichiarazione(), iCompactCmm))
	  //33950 fine
      //29960 fine
      return true;

    return false;
  }

  /**
   * recuperoDocPrdMatCostoUnitario
   * @param idCommessa String
   * @param docPrdRigMat DocumentoPrdRigaMateriale
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoDocPrdMatCostoUnitario(String idCommessa, DocumentoPrdRigaMateriale docPrdRigMat) throws Exception {

    if(docPrdRigMat.getCostoRiferimento() != null && docPrdRigMat.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0) {
      return docPrdRigMat.getCostoRiferimento();
    }
    else {
      return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docPrdRigMat.getRArticolo(), docPrdRigMat.getRVersione(), docPrdRigMat.getRConfigurazione());
    }
  }  

  /**
   * recuperoDocPrdVrsCostoUnitario
   * @param idCommessa String
   * @param docPrdRigVrs DocumentoPrdRigaVersamento
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoDocPrdVrsCostoUnitario(String idCommessa, DocumentoPrdRigaVersamento docPrdRigVrs) throws Exception {
	  
    if(docPrdRigVrs.getCostoRiferimento() != null && docPrdRigVrs.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0) {
      return docPrdRigVrs.getCostoRiferimento();
    }
    else {
      return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docPrdRigVrs.getRArticolo(), docPrdRigVrs.getRVersione(), docPrdRigVrs.getRConfigurazione());
    }
  }
  
  //31460 inizio
  protected BigDecimal recuperoDocPrdMatCostoUnitario(String idCommessa, DocumentoPrdRigaMateriale docPrdRigMat, StoricoCommessa storicoCmm) throws Exception {
	return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docPrdRigMat.getRArticolo(), docPrdRigMat.getRVersione(), docPrdRigMat.getRConfigurazione(), docPrdRigMat.getCostoRiferimento(), storicoCmm);
  }
  
  protected BigDecimal recuperoDocPrdVrsCostoUnitario(String idCommessa, DocumentoPrdRigaVersamento docPrdRigVrs, StoricoCommessa storicoCmm) throws Exception {
	return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docPrdRigVrs.getRArticolo(), docPrdRigVrs.getRVersione(), docPrdRigVrs.getRConfigurazione(), docPrdRigVrs.getCostoRiferimento(), storicoCmm);
  }
  
  protected void valorizzaCostoDocPrdRigRsr(DocumentoPrdRigaRisorsa docPrdRigRsr, StoricoCommessa storicoCmm) throws Exception {
	  BigDecimal tmpCosTotale = new BigDecimal("0");
	  BigDecimal tmpCosUnitario = new BigDecimal("0");
	  BigDecimal tmpCosUnitarioOrig = new BigDecimal("0");
	  BigDecimal qtaPrmMag = storicoCmm.getQuantitaUMPrm();
	  BigDecimal qtaScarta = storicoCmm.getQtaScarto();
	  if(docPrdRigRsr.getRisorsa() != null){
		  if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO) {
			  BigDecimal tmpQta = new BigDecimal("0");
		      qtaPrmMag = qtaPrmMag != null ? qtaPrmMag : new BigDecimal("0");
		      qtaScarta = qtaScarta != null ? qtaScarta : new BigDecimal("0");
		      tmpQta = qtaPrmMag.add(qtaScarta);
		      if(docPrdRigRsr.getCostoRilevato() != null && docPrdRigRsr.getCostoRilevato().compareTo(new BigDecimal("0")) != 0 && tmpQta.compareTo(new BigDecimal("0")) != 0){
		    	  tmpCosUnitario = docPrdRigRsr.getCostoRilevato().divide(tmpQta, BigDecimal.ROUND_HALF_UP);
		    	  tmpCosUnitarioOrig = tmpCosUnitario;
		      }
		      else{
		    	  tmpCosUnitarioOrig = docPrdRigRsr.getCostoRilevato();
		      }
		      tmpCosTotale = docPrdRigRsr.getCostoRilevato();
		      storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
		  }
		  else{
			  tmpCosUnitario = recuperoDocPrdRigRsrCostoRif(docPrdRigRsr, storicoCmm);
			  tmpCosUnitarioOrig = docPrdRigRsr.getCostoRiferimento();
			  if(tmpCosUnitario != null){
				  if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.TEMPO) {
					  tmpCosTotale = tmpCosUnitario.multiply(storicoCmm.getTempo());
				  }
				  else if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.QUANTITA) {
					  BigDecimal qtaUmPrm = storicoCmm.getQuantitaUMPrm() != null ? storicoCmm.getQuantitaUMPrm() : new BigDecimal("0");
					  qtaScarta = storicoCmm.getQtaScarto() != null ? storicoCmm.getQtaScarto() : new BigDecimal("0");
					  BigDecimal tmpQta = qtaUmPrm.add(qtaScarta);
					  tmpCosTotale = tmpCosUnitario.multiply(tmpQta);
				  }
			  }
		  }
	  }	  
	  storicoCmm.setCostoTotale(tmpCosTotale);
	  storicoCmm.setCostoUnitario(tmpCosUnitario);
	  storicoCmm.setCostoUnitarioOrigine(tmpCosUnitarioOrig);
  }
  
  protected BigDecimal recuperoDocPrdRigRsrCostoRif(DocumentoPrdRigaRisorsa docPrdRigRsr, StoricoCommessa storicoCmm) throws Exception {
	  BigDecimal costoDocumento = docPrdRigRsr.getCostoRiferimento();
	  if(iConsunCmm.isCostiRisorsaDaDocumento()) {
		  storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
		  return docPrdRigRsr.getCostoRiferimento();
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
	  
	  return null;
  }
  //31460 fine

  /**
   * recuperoDocPrdRsrCostoUnitario
   * @param docPrdRigRsr DocumentoPrdRigaRisorsa
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoDocPrdRsrCostoRif(DocumentoPrdRigaRisorsa docPrdRigRsr) throws Exception {
     //Fix 27486 inizio
     if(iConsunCmm.isCostiRisorsaDaDocumento())
     {
        return docPrdRigRsr.getCostoRiferimento();
     }
     else
     {
    if(docPrdRigRsr.getCostoRiferimento() != null && docPrdRigRsr.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0) {
      return docPrdRigRsr.getCostoRiferimento();
    }
    else {
      BigDecimal costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(), docPrdRigRsr.getRTipoRisorsa(), docPrdRigRsr.getRLivelloRisorsa(), docPrdRigRsr.getRRisorsa());
      if(costoRif == null || costoRif.compareTo(new BigDecimal("0")) == 0) {
        if(docPrdRigRsr.getRisorsa().getRisorsaAppart() != null && docPrdRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa() == Risorsa.RISORSA)
          costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(),
            docPrdRigRsr.getRisorsa().getRisorsaAppart().getTipoRisorsa(),
            docPrdRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa(),
            docPrdRigRsr.getRisorsa().getRisorsaAppart().getIdRisorsa());
      }
      return costoRif;
    }
  }
    //Fix 27486 fine
  }
  

  /**
   * recuperoDocPrdMatValorizzaCosto
   * @param storicoCmm StoricoCommessa
   * @return char
   */
  protected char recuperoDocPrdMatValorizzaCosto(StoricoCommessa storicoCmm) {
    if(storicoCmm.getGesSaldiCommessa())
      return StoricoCommessa.NO;

    else if(storicoCmm.getAzioneMagazzino() == AzioneMagazzino.NESSUNA_AZIONE)
      return StoricoCommessa.NO;
    else if(storicoCmm.getAzioneMagazzino() == AzioneMagazzino.USCITA)
      return StoricoCommessa.INCREMENTA_COSTO;
    else if(storicoCmm.getAzioneMagazzino() == AzioneMagazzino.ENTRATA)
      return StoricoCommessa.DECREMENTA_COSTO;

    return StoricoCommessa.NO;
  }

  /**
   * recuperoDocPrdVrsValorizzaCosto
   * @param tipoProdotto char
   * @param gesSaldiCmm boolean
   * @return char
   */
  protected char recuperoDocPrdVrsValorizzaCosto(char tipoProdotto, boolean gesSaldiCmm) {

    if(tipoProdotto == AttivitaProdProdotto.PRODOTTO_PRIMARIO || tipoProdotto == AttivitaProdProdotto.PRODOTTO_PRIMARIO_ALTRE)
      return StoricoCommessa.INCREMENTA_COSTI_INDIRETTI;

    if((tipoProdotto == AttivitaProdProdotto.SOTTO_PRODOTTO || tipoProdotto == AttivitaProdProdotto.SFRIDO) && !gesSaldiCmm)
      return StoricoCommessa.DECREMENTA_COSTO;

    if((tipoProdotto == AttivitaProdProdotto.SOTTO_PRODOTTO || tipoProdotto == AttivitaProdProdotto.SFRIDO) && gesSaldiCmm)
      return StoricoCommessa.NESSUNA;

    if(tipoProdotto == AttivitaProdProdotto.SCARTO)
      return StoricoCommessa.NESSUNA;

    return StoricoCommessa.NESSUNA;
  }

  /**
   * recuperoDocPrdRsrCostoUnitario
   * @param docPrdRigRsr DocumentoPrdRigaRisorsa
   * @param qtaPrmMag BigDecimal
   * @param qtaScarta BigDecimal
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoDocPrdRsrCostoUnitario(DocumentoPrdRigaRisorsa docPrdRigRsr, BigDecimal qtaPrmMag, BigDecimal qtaScarta) throws Exception {
    if(docPrdRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO) {
      BigDecimal tmpQta = new BigDecimal("0");
      qtaPrmMag = qtaPrmMag != null ? qtaPrmMag : new BigDecimal("0");
      qtaScarta = qtaScarta != null ? qtaScarta : new BigDecimal("0");
      tmpQta = qtaPrmMag.add(qtaScarta);
      if(tmpQta.compareTo(new BigDecimal("0")) != 0)
        return docPrdRigRsr.getCostoRilevato().divide(tmpQta, BigDecimal.ROUND_HALF_UP);
      else
        return new BigDecimal("0");
    }
    else {
      return recuperoDocPrdRsrCostoRif(docPrdRigRsr);
    }
  }

  /**
   * recuperoRisorsaPrincipale
   * @param docPrd DocumentoProduzione
   * @return Risorsa
   */
  protected Risorsa recuperoRisorsaPrincipale(DocumentoProduzione docPrd) {
    if(docPrd.getOrdineEsecutivo() != null) {
      for(Iterator it = docPrd.getOrdineEsecutivo().getAttivitaEsecutive().iterator(); it.hasNext(); ) {
        AttivitaEsecutiva attEsec = (AttivitaEsecutiva)it.next();

        if(attEsec.getAtvEsecRsrPrincipale() != null)
          return attEsec.getAtvEsecRsrPrincipale().getRisorsa();

      }
    }
    return null;
  }

  // fix 10913
  public StoricoCommessa completaDatiStoricoCmmPrdMat(StoricoCommessa storicoCmm, DocumentoPrdRigaMateriale docPrdRigMat) throws Exception {
    return storicoCmm;
  }

  public StoricoCommessa completaDatiStoricoCmmPrdPrd(StoricoCommessa storicoCmm, DocumentoPrdRigaVersamento docPrdRigVrs) throws Exception {
    return storicoCmm;
  }

  public StoricoCommessa completaDatiStoricoCmmPrdRis(StoricoCommessa storicoCmm, DocumentoPrdRigaRisorsa docPrdRigVrs) throws Exception {
    return storicoCmm;
  }

  public StoricoCommessa completaDatiStoricoCmmPrdRis(StoricoCommessa storicoCmm, DocumentoProduzione docPrd) throws Exception {
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

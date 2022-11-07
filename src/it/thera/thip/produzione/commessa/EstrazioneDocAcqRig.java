package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import com.thera.thermfw.base.Utils;
import com.thera.thermfw.persist.Column;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;

import it.thera.thip.acquisti.documentoAC.DocumentoAcqRigaPrm;
import it.thera.thip.acquisti.documentoAC.DocumentoAcqRigaPrmTM;
import it.thera.thip.acquisti.documentoAC.DocumentoAcquisto;
import it.thera.thip.acquisti.generaleAC.TipoDocumentoAcq;
import it.thera.thip.acquisti.generaleAC.TipoLavEsterna;
import it.thera.thip.base.comuniVenAcq.AzioneMagazzino;
import it.thera.thip.base.comuniVenAcq.TipoRiga;
import it.thera.thip.base.documenti.StatoAttivita;
import it.thera.thip.base.documenti.StatoAvanzamento;
import it.thera.thip.base.risorse.Risorsa;
import it.thera.thip.cs.DatiComuniEstesi;
import it.thera.thip.cs.GestoreCommit;

/**
 * EstrazioneDocAcqRig
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 23/05/2005 at 11:20:32
 */
/*
 * Revisions:
 * Number  Date          Owner   Description
 * 09278   magg 2008     LR      createStorici scarta ordAcqRig con tipo lavorazione esterna per reparto
 * 10111   19/11/2008    GN      Corretto set del numero bolla sullo storico commesse
 * 10411   28/01/2009    DBot    Logger per performance su consuntivazione
 * 10537   30/03/2009    DBot    Riallineamento per fix WEB
 * 10913   25/05/2009    DB      Per permettere la personalizzazione
 * 29550   05/09/2019	 RA		 Corretto reperimento importi in valuta aziendale
 * 29960   09/10/2019    RA	     Consuntivazione in modalità definitiva eseguibile per singola commessa/ambiente commessa
 * 31460   09/07/2020    RA		 Valorizza nuovo attributo ProvenienzaCosto
 * 31513   07/09/2020	 RA		 Aggiunto gestione articoli senza gestione saldi per commessa
 * 34308   21/09/2021    Mekki   GestoreCommit 
 * 33950   06/10/2021    RA      modifica il metodo willBeProcessed
 */

public class EstrazioneDocAcqRig extends EstrazioneDocumenti{
	
  boolean storicoNonCommessa = false; //31513
  
  protected GestoreCommit iGestoreCommit; //Fix 34308

  public EstrazioneDocAcqRig(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm) {
    super(consunCmm, compactCmm);
  }

  /**
   * processRighe
   * @throws Exception
   * @return boolean
   */
  protected boolean processRighe() throws Exception{
    getLogger().iniziaTipoDocumento("DOC_ACQ", iCompactCmm.getIdCommessa()); //Fix 10411
    String where = DocumentoAcqRigaPrmTM.ID_AZIENDA + "='" + iCompactCmm.getIdAzienda() + "' AND "
        + DocumentoAcqRigaPrmTM.R_COMMESSA + "='" + iCompactCmm.getIdCommessa() + "' AND "
        + DocumentoAcqRigaPrmTM.ID_DET_RIGA_DOC + "= 0 AND "
        + DocumentoAcqRigaPrmTM.LAV_ESN + "='" + Column.FALSE_CHAR + "' AND "
        + DocumentoAcqRigaPrmTM.STATO + "='" + DatiComuniEstesi.VALIDO + "' AND "
        + DocumentoAcqRigaPrmTM.STATO_AVANZAMENTO + "='" + StatoAvanzamento.DEFINITIVO + "' AND "
        + DocumentoAcqRigaPrmTM.COL_MAGAZZINO + " IN ('" + StatoAttivita.ESEGUITO + "', '" + StatoAttivita.NON_RICHIESTO + "') AND "
        + DocumentoAcqRigaPrmTM.NO_FATTURA + "='" + Column.FALSE_CHAR + "' AND "
        + DocumentoAcqRigaPrmTM.TIPO_RIGA + "<>'" + TipoRiga.OMAGGIO + "'";

    getLogger().startTime(); // Fix 10411
    List docAcqList = DocumentoAcqRigaPrm.retrieveList(DocumentoAcqRigaPrm.class, where, "", false);
    getLogger().addTempoLettura(getLogger().stopTime()); // Fix 10411
    getLogger().incNumTestateLette(docAcqList.size()); // Fix 10411

    for (Iterator it = docAcqList.iterator(); it.hasNext(); ) {
      DocumentoAcqRigaPrm docAcqRig = (DocumentoAcqRigaPrm)it.next();
      if(willBeProcessed(docAcqRig)){

        getLogger().incNumTestateFatte(); // Fix 10411
        getLogger().startTime();  // Fix 10411
    	//31513 inizio         
    	if(!docAcqRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa() && iConsunCmm.iStoriciNonCommessa)
    		storicoNonCommessa = true;
    	else
    		storicoNonCommessa = false;
    	//31513 fine 
        StoricoCommessa storicoCmm = createStorico(docAcqRig);
        getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

        if(storicoCmm == null){
          //iConsunCmm.commitWithGestoreCommit(false); //Fix 34308
          return false;
        }

        getLogger().startTime();  // Fix 10411
        attribuzioneCosti(storicoCmm);
        getLogger().addTempoAttribuzioneCosti(getLogger().stopTime()); // Fix 10411

        //iConsunCmm.commitWithGestoreCommit(false); //Fix 34308
        getGestoreCommit().commit(); //Fix 34308
      }
      it.remove(); // Fix 10411, 10437
    }
    //iConsunCmm.commitWithGestoreCommit(true); //Fix 34308

    getLogger().fineTipoDocumento(); // Fix 10411

    return true;
  }

  /**
   * createStorico
   * @param docAcqRig DocumentoAcqRigaPrm
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStorico(DocumentoAcqRigaPrm docAcqRig) throws Exception{
    // FIX 9278 - inizio
    if (docAcqRig.getTipoLavEsterna() == TipoLavEsterna.LAV_PER_REPARTO) {
        return null;
    }
    // FIX 9278 - fine
    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(docAcqRig.getIdCommessa());
    storicoCmm.setLivelloCommessa(docAcqRig.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(docAcqRig.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(docAcqRig.getCommessa().getIdCommessaPrincipale());
    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.ACQUISTO);
    storicoCmm.setIdAnnoOrigine(docAcqRig.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docAcqRig.getNumeroDocumento());
    storicoCmm.setIdRigaOrigine(docAcqRig.getNumeroRigaDocumento());
    storicoCmm.setIdDetRigaOrigine(docAcqRig.getDettaglioRigaDocumento());
    storicoCmm.setNumeroOrgFormattato(docAcqRig.getTestata().getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docAcqRig.getTestata().getDataDocumento());
    storicoCmm.setIdCauOrgTes(docAcqRig.getTestata().getIdCau());
    storicoCmm.setIdCauOrgRig(docAcqRig.getIdCauRig());
    storicoCmm.setAvanzamento(false);

    if(docAcqRig.getCausaleRiga() != null)
      storicoCmm.setIdCauMagazzino(docAcqRig.getCausaleRiga().getIdCauMagazzino());

    storicoCmm.setAzioneMagazzino(recuperoDocAcqRigAzioneMagazzino(docAcqRig));

    if(docAcqRig.getTipoRiga() == TipoRiga.MERCE || docAcqRig.getTipoRiga() == TipoRiga.SERVIZIO)
       storicoCmm.setIdMagazzino(docAcqRig.getIdMagazzino());

    storicoCmm.setIdArticolo(docAcqRig.getIdArticolo());
    storicoCmm.setIdVersione(docAcqRig.getIdVersioneRcs() != null ? docAcqRig.getIdVersioneRcs() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docAcqRig.getIdConfigurazione());

    storicoCmm.setDescrizioneArticolo(docAcqRig.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);

    storicoCmm.setQuantitaUMPrm(docAcqRig.getQtaRicevuta().getQuantitaInUMPrm());
    storicoCmm.setQuantitaUMSec(docAcqRig.getQtaRicevuta().getQuantitaInUMSec());
    storicoCmm.setQuantitaUMAcqVen(docAcqRig.getQtaRicevuta().getQuantitaInUMRif());

    storicoCmm.setIdUmPrmMag(docAcqRig.getIdUMPrm());
    storicoCmm.setIdUmSecMag(docAcqRig.getIdUMSec());
    storicoCmm.setIdUMAcqVen(docAcqRig.getIdUMRif());
    //Fix 04361 Mz inzio
    if (docAcqRig.getTipoRiga() == TipoRiga.SPESE_MOV_VALORE)
    {
            if ((storicoCmm.getQuantitaUMPrm() == null) || (Utils.compare(storicoCmm.getQuantitaUMPrm(), new BigDecimal("0")) == 0))
        storicoCmm.setQuantitaUMPrm(storicoCmm.getQuantitaUMAcqVen());

      if (storicoCmm.getIdUmPrmMag() == null)
        storicoCmm.setIdUmPrmMag(storicoCmm.getIdUMAcqVen());
    }
    //Fix 04361 Mz fine

    if(docAcqRig.getImportoNettoFattura() != null && docAcqRig.getImportoNettoFattura().compareTo(new BigDecimal("0")) != 0)
      //storicoCmm.setCostoTotale(docAcqRig.getImportoNettoFattura());//29550
    	storicoCmm.setCostoTotale(docAcqRig.getImportoNettoFatturaVA());//29550
    else
      //storicoCmm.setCostoTotale(docAcqRig.getValoreRiga());//29550
    	storicoCmm.setCostoTotale(docAcqRig.getValoreRigaVA());//29550

    storicoCmm.setValoreRiga(docAcqRig.getValoreRiga());
    storicoCmm.setCostoUnitario(recuperoDocAcqRigCostoUnitario(docAcqRig.getTipoRiga(),
        storicoCmm.getCostoTotale(),
        storicoCmm.getQuantitaUMPrm(),
        storicoCmm.getQuantitaUMAcqVen()));
    storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);//31460

    storicoCmm.setCostoUnitarioOrigine(storicoCmm.getCostoUnitario());
    storicoCmm.setValorizzaCosto(recuperoDocAcqRigValorizzaCosto(((DocumentoAcquisto)docAcqRig.getTestata()).getTipoDocumento()));
    //31513 inizio
    if(storicoNonCommessa) {
       storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
       storicoCmm.setStoricoNonCommessa(true);
    }
    //31513 fine
    storicoCmm.setNoFatturare(docAcqRig.isNonFatturare());

    storicoCmm.setGesSaldiCommessa(docAcqRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
    if (docAcqRig.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(docAcqRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(docAcqRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }

    storicoCmm.setIdAnnoOrdine(docAcqRig.getAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(docAcqRig.getNumeroOrdine());
    storicoCmm.setIdRigaOrdine(docAcqRig.getNumeroRigaOrdine());
    storicoCmm.setIdDetRigaOrdine(docAcqRig.getDettaglioRigaOrdine());
    /**@todo verify From testata or riga */
    storicoCmm.setDataOrdine(docAcqRig.getDataOrdine());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdAnnoBolla(((DocumentoAcquisto)docAcqRig.getTestata()).getAnnoBolla());
    //storicoCmm.setDataBolla(docAcqRig.getDataBolla());
    //storicoCmm.setIdNumeroBolla(docAcqRig.getNumeroBolla());
    if (((DocumentoAcquisto)docAcqRig.getTestata()).getDataRifIntestatario() == null)
    {
      storicoCmm.setIdAnnoBolla(((DocumentoAcquisto)docAcqRig.getTestata()).getAnnoBolla());
      storicoCmm.setDataBolla(((DocumentoAcquisto)docAcqRig.getTestata()).getDataBolla());
      storicoCmm.setIdNumeroBolla(((DocumentoAcquisto)docAcqRig.getTestata()).getNumeroBolla());
    }
    else
    {
      storicoCmm.setIdAnnoBolla(null);
      storicoCmm.setDataBolla(((DocumentoAcquisto)docAcqRig.getTestata()).getDataRifIntestatario());
      //Fix 10111 inizio
      String numRifInt = ((DocumentoAcquisto)docAcqRig.getTestata()).getNumeroRifIntestatario();
      if (numRifInt != null && numRifInt.length() > 10)
      	numRifInt = numRifInt.substring(0, 10);
      storicoCmm.setIdNumeroBolla(numRifInt);
      //Fix 10111 fine
    }
    //Fix 04361 Mz fine
    storicoCmm.setIdAnnoFattura(((DocumentoAcquisto)docAcqRig.getTestata()).getAnnoFattura());
    storicoCmm.setDataFattura(docAcqRig.getDataFattura());
    storicoCmm.setIdNumeroFattura(docAcqRig.getNumeroFattura());
    storicoCmm.setIdFornitore(((DocumentoAcquisto)docAcqRig.getTestata()).getIdFornitore());

    storicoCmm.setTipoArticolo(docAcqRig.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(docAcqRig.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(docAcqRig.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(docAcqRig.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(docAcqRig.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(docAcqRig.getArticolo().getArticoloDatiPianif().getIdPianificatore());

    storicoCmm.setIdCentroCosto(docAcqRig.getIdCentroCosto());

    //Fix 04361 Mz inizio
    if ((storicoCmm.getMagazzino() != null) && (storicoCmm.getMagazzino().getIdStabilimento() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getMagazzino().getIdStabilimento());
    else if (storicoCmm.getCommessa() != null)
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
    //Fix 04361 Mz fine

    // fix 10913
    storicoCmm = completaDatiStoricoCmm(storicoCmm, docAcqRig);
    // fine fix 10913

    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario())){
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
   * willBeProcessed
   * @param docAcqRig DocumentoAcqRigaPrm
   * @return boolean
   */
  protected boolean willBeProcessed(DocumentoAcqRigaPrm docAcqRig){
    if(docAcqRig.getTestata().getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO
       && docAcqRig.getTestata().getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO
       && docAcqRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataRiferimento()) <= 0
       //29960 inizio
       //&& (iConsunCmm.getDataUltChiusDefAmbiente() == null
       //    || docAcqRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente()) > 0)
       //33950 inizio
       /*
       && (iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente()) == null 
       || docAcqRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente())) > 0)
       */
       && iConsunCmm.isDataValido(docAcqRig.getTestata().getDataDocumento(), iCompactCmm)
       //33950 fine
       //29960 fine
       && (docAcqRig.getTipoRiga() != TipoRiga.MERCE
           || docAcqRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa()
           || (iConsunCmm.isStoriciNonCommessa() && !docAcqRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())))//31513
      return true;

    return false;
  }

  /**
   * recuperoAzioneMagazzino
   * @param docAcqRig DocumentoAcqRigaPrm
   * @return char
   */
  protected char recuperoDocAcqRigAzioneMagazzino(DocumentoAcqRigaPrm docAcqRig){
    if(docAcqRig.getTipoRiga() == TipoRiga.MERCE || docAcqRig.getTipoRiga() == TipoRiga.SERVIZIO)
      return docAcqRig.getCausaleRiga() != null ? docAcqRig.getCausaleRiga().getAzioneMagazzino() : AzioneMagazzino.NESSUNA_AZIONE;
    else
      return AzioneMagazzino.NESSUNA_AZIONE;
  }

  /**
   * recuperoDocAcqRigCostoUnitario
   * @param tipoRiga char
   * @param costoTotale BigDecimal
   * @param qtaUmPrm BigDecimal
   * @param qtaUmAcq BigDecimal
   * @return BigDecimal
   */
  protected BigDecimal recuperoDocAcqRigCostoUnitario(char tipoRiga, BigDecimal costoTotale, BigDecimal qtaUmPrm, BigDecimal qtaUmAcq ){
    if(tipoRiga == TipoRiga.MERCE || tipoRiga == TipoRiga.SERVIZIO)
      return qtaUmPrm != null && costoTotale != null && qtaUmPrm.compareTo(new BigDecimal("0")) != 0 ?
          costoTotale.divide(qtaUmPrm, BigDecimal.ROUND_HALF_UP) : null;
    else
      return qtaUmAcq != null && costoTotale != null && qtaUmAcq.compareTo(new BigDecimal("0")) != 0 ?
          costoTotale.divide(qtaUmAcq, BigDecimal.ROUND_HALF_UP) : null;
  }

  /**
   * recuperoDocAcqRigValorizzaCosto
   * @param tipoDocumento char
   * @return BigDecimal
   */
  protected char recuperoDocAcqRigValorizzaCosto(char tipoDocumento){
   if(tipoDocumento == TipoDocumentoAcq.ACQUISTO || tipoDocumento == TipoDocumentoAcq.ENTRATA_GENERICA)
     return StoricoCommessa.INCREMENTA_COSTO;
   else if(tipoDocumento == TipoDocumentoAcq.RESO_FORNITORE || tipoDocumento == TipoDocumentoAcq.USCITA_GENERICA)
     return StoricoCommessa.DECREMENTA_COSTO;
   return StoricoCommessa.NO;
 }

 // fix 10913
 public StoricoCommessa completaDatiStoricoCmm(StoricoCommessa storicoCmm, DocumentoAcqRigaPrm docVenRig) throws Exception {
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

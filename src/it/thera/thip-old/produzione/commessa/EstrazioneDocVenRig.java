package it.thera.thip.produzione.commessa;

import java.math.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.vendite.documentoVE.*;
import it.thera.thip.vendite.generaleVE.CausaleRigaDocVen;

/**
 * EstrazioneDocVenRig
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 20/05/2005 at 15:39:10
 */
/*
 * Revisions:
 * Number  Date        Owner  Description
 * 09384   18/06/2008  LP     Modifiche per permettere le personalizzazioni
 * 10411   28/01/2009  DBot   Logger per performance su consuntivazione
 * 10537   30/03/2009  DBot   Riallineamento per fix WEB
 * 11293   10/09/2009  MN     Implementazione del metodo calcolaValoreCostoStoricoCommessa per intercettare i movimenti di vendita
 *                            su commessa di articoli standard, recuperando il prezzo o il costo di riferimento.
 * 29550   05/09/2019  RA	  Corretto reperimento importi in valuta aziendale          
 * 29960   09/10/2019  RA	  Consuntivazione in modalità definitiva eseguibile per singola commessa/ambiente commessa
 * 31460   03/07/2020  RA	  Aggiunto nuova definizione del metodo recuperoArticoloCostoUnitario                  
 * 33143   17/03/2021  RA	  Corretta valorizzazione costo 
 * 34308   21/09/2021  Mekki  GestoreCommit
 * 33950   06/10/2021  RA     modifica il metodo willBeProcessed 
 */

public class EstrazioneDocVenRig extends EstrazioneDocumenti {
	
  protected GestoreCommit iGestoreCommit; //Fix 34308	
  
  /**
   * Constructor
   * @param consunCmm ConsuntivazioneCommesse
   * @param compactCmm CompactCommessa
   */
  public EstrazioneDocVenRig(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm) {
    super(consunCmm, compactCmm);
  }

  /**
   * processRighe
   * @throws Exception
   * @return boolean
   */
  protected boolean processRighe() throws Exception {
    getLogger().iniziaTipoDocumento("DOC_VEN", iCompactCmm.getIdCommessa()); //Fix 10411
    String where = DocumentoVenRigaPrmTM.ID_AZIENDA + "='" + iCompactCmm.getIdAzienda() + "' AND "
        + DocumentoVenRigaPrmTM.R_COMMESSA + "='" + iCompactCmm.getIdCommessa() + "' AND "
        + DocumentoVenRigaPrmTM.ID_DET_RIGA_DOC + "= 0 AND "
        + DocumentoVenRigaPrmTM.STATO + "='" + DatiComuniEstesi.VALIDO + "' AND "
        + DocumentoVenRigaPrmTM.STATO_AVANZAMENTO + "='" + StatoAvanzamento.DEFINITIVO + "' AND "
        + DocumentoVenRigaPrmTM.COL_MAGAZZINO + " IN ('" + StatoAttivita.ESEGUITO + "', '" + StatoAttivita.NON_RICHIESTO + "') AND "
        + DocumentoVenRigaPrmTM.TIPO_RIGA + "<>'" + TipoRiga.OMAGGIO + "'";
    
    getLogger().startTime(); // Fix 10411
    List docVenList = DocumentoVenRigaPrm.retrieveList(DocumentoVenRigaPrm.class, where, "", false);
    getLogger().addTempoLettura(getLogger().stopTime()); // Fix 10411
    getLogger().incNumTestateLette(docVenList.size()); // Fix 10411

    for (Iterator it = docVenList.iterator(); it.hasNext(); ) {
      DocumentoVenRigaPrm docVenRig = (DocumentoVenRigaPrm) it.next();
      if (willBeProcessed(docVenRig)) {
        getLogger().incNumTestateFatte(); // Fix 10411
        StoricoCommessa storicoCmm = createStorico(docVenRig);
        getLogger().startTime();  // Fix 10411
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
   * @param docVenRig DocumentoVenRigaPrm
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStorico(DocumentoVenRigaPrm docVenRig) throws Exception {
    StoricoCommessa storicoCmm = (StoricoCommessa) Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(docVenRig.getIdCommessa());
    storicoCmm.setLivelloCommessa(docVenRig.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(docVenRig.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(docVenRig.getCommessa().getIdCommessaPrincipale());
    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.VENDITA);
    storicoCmm.setIdAnnoOrigine(docVenRig.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docVenRig.getNumeroDocumento());
    storicoCmm.setIdRigaOrigine(docVenRig.getNumeroRigaDocumento());
    storicoCmm.setIdDetRigaOrigine(docVenRig.getDettaglioRigaDocumento());
    storicoCmm.setNumeroOrgFormattato(docVenRig.getTestata().getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docVenRig.getTestata().getDataDocumento());
    storicoCmm.setIdCauOrgTes(docVenRig.getTestata().getIdCau());
    storicoCmm.setIdCauOrgRig(docVenRig.getIdCauRig());
    storicoCmm.setAvanzamento(false);

    if (docVenRig.getCausaleRiga() != null){
      storicoCmm.setIdCauMagazzino(docVenRig.getCausaleRiga().getIdCauMagazzino());
    }

    if(docVenRig.getTipoRiga() == TipoRiga.SPESE_MOV_VALORE){
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.NESSUNA_AZIONE);
    }
    else{
      storicoCmm.setAzioneMagazzino(docVenRig.getCausaleRiga().getAzioneMagazzino());
      storicoCmm.setIdMagazzino(docVenRig.getIdMagazzino());
    }

    storicoCmm.setIdArticolo(docVenRig.getIdArticolo());
    storicoCmm.setIdVersione(docVenRig.getIdVersioneRcs() != null ? docVenRig.getIdVersioneRcs() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docVenRig.getIdConfigurazione());

    storicoCmm.setDescrizioneArticolo(docVenRig.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);

    storicoCmm.setQuantitaUMPrm(docVenRig.getQtaSpedita().getQuantitaInUMPrm());
    storicoCmm.setQuantitaUMSec(docVenRig.getQtaSpedita().getQuantitaInUMSec());
    storicoCmm.setQuantitaUMAcqVen(docVenRig.getQtaSpedita().getQuantitaInUMRif());

    storicoCmm.setIdUmPrmMag(docVenRig.getIdUMPrm());
    storicoCmm.setIdUmSecMag(docVenRig.getIdUMSec());
    storicoCmm.setIdUMAcqVen(docVenRig.getIdUMRif());

    //Fix 04361 Mz inzio
    if(docVenRig.getTipoRiga() == TipoRiga.SPESE_MOV_VALORE){
      if ((storicoCmm.getQuantitaUMPrm() == null) || (Utils.compare(storicoCmm.getQuantitaUMPrm(), new BigDecimal("0")) == 0))
        storicoCmm.setQuantitaUMPrm(storicoCmm.getQuantitaUMAcqVen());

      if (storicoCmm.getIdUmPrmMag() == null)
        storicoCmm.setIdUmPrmMag(storicoCmm.getIdUMAcqVen());
    }
    //Fix 04361 Mz fine
    // Inizio fix 11293 
    //calcolaValoreCostoStoricoCommessa(storicoCmm, docVenRig);//33143
    /*
    storicoCmm.setCostoTotale(docVenRig.getValoreRiga());
    storicoCmm.setValoreRiga(docVenRig.getValoreRiga());

    if(storicoCmm.getCostoTotale() != null
       && storicoCmm.getQuantitaUMPrm() != null
       && storicoCmm.getQuantitaUMPrm().compareTo(new BigDecimal("0")) != 0)
      storicoCmm.setCostoUnitario(storicoCmm.getCostoTotale().divide(storicoCmm.getQuantitaUMPrm(), BigDecimal.ROUND_HALF_UP));

    storicoCmm.setCostoUnitarioOrigine(storicoCmm.getCostoUnitario());
    storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
    */
    // Fine 11293
    
    storicoCmm.setNoFatturare(docVenRig.isNonFatturare());

    storicoCmm.setGesSaldiCommessa(docVenRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
    if (docVenRig.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(docVenRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(docVenRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }

    storicoCmm.setIdAnnoOrdine(docVenRig.getAnnoOrdine());
    storicoCmm.setIdNumeroOrdine(docVenRig.getNumeroOrdine());
    storicoCmm.setIdRigaOrdine(docVenRig.getNumeroRigaOrdine());
    storicoCmm.setIdDetRigaOrdine(docVenRig.getDettaglioRigaOrdine());
    /**@todo verify From testata or riga */
    storicoCmm.setDataOrdine(docVenRig.getDataOrdine());
    storicoCmm.setIdAnnoBolla( ( (DocumentoVendita) docVenRig.getTestata()).getAnnoBolla());
    storicoCmm.setDataBolla(docVenRig.getDataBolla());
    storicoCmm.setIdNumeroBolla(docVenRig.getNumeroBolla());
    storicoCmm.setIdAnnoFattura( ( (DocumentoVendita) docVenRig.getTestata()).getAnnoFattura());
    storicoCmm.setDataFattura(docVenRig.getDataFattura());
    storicoCmm.setIdNumeroFattura(docVenRig.getNumeroFattura());
    storicoCmm.setIdCliente( ( (DocumentoVendita) docVenRig.getTestata()).getIdCliente());

    storicoCmm.setTipoArticolo(docVenRig.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(docVenRig.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(docVenRig.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(docVenRig.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(docVenRig.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(docVenRig.getArticolo().getArticoloDatiPianif().getIdPianificatore());
    storicoCmm.setIdCentroCosto(docVenRig.getIdCentroCosto());

    //Fix 04361 Mz inizio
    if ((storicoCmm.getMagazzino() != null) && (storicoCmm.getMagazzino().getIdStabilimento() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getMagazzino().getIdStabilimento());
    else if (storicoCmm.getCommessa() != null)
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
    //Fix 04361 Mz fine
    calcolaValoreCostoStoricoCommessa(storicoCmm, docVenRig);//33143
    storicoCmm = completaDatiStoricoCmm(storicoCmm, docVenRig); //...FIX 9384

    if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario())){
      String artKey = KeyHelper.formatKeyString(storicoCmm.getArticolo().getKey());
      ReportAnomalieConsCmm rptAnomalieConsCmm = addCostoUnitarioAnomalie(storicoCmm, "THIP20T089", artKey);
      iConsunCmm.addAnomalie(rptAnomalieConsCmm);
      if(isErroreBloccanteMode())
        return null;
    }

    if(!iConsunCmm.isSimulazione())
      storicoCmm.save();

    iConsunCmm.commitWithGestoreCommit(false);

    return storicoCmm;
  }
  
  // Inizio 11293
  /**
   * Calcolo dei seguenti attributi dello StoricoCommessa:
   * CostoTotale (=ValoreRiga)
   * CostoUnitario (=CostoUnitarioOrigine)
   * ValorizzaCosto
   */
  public void calcolaValoreCostoStoricoCommessa(StoricoCommessa storicoComm, DocumentoVenRigaPrm docVenRig) throws Exception{
    // Il calcolo deve essere eseguito solo se l'articolo non è gestito a SaldiCommessa
    if (!docVenRig.getArticolo().isGesSaldiCommessa()){
      BigDecimal costoTotaleStoricoComm = null;
      BigDecimal costoUnitarioStoricoComm = null;
      char azioneCostoCommessa = docVenRig.getCausaleRiga().getAzioneCostoCommessa();
      char valorizzaCostoStoricoComm = calcolaValorizzaCosto(azioneCostoCommessa);
      String idCommessa = null;
      // Calcolo
      // Valorizzazione CostoTotale (=ValoreRiga) e CostoUnitario (=CostoUnitarioOrigine)
      if (azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOMMA_COSTO_RIF || azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOTTRAE_COSTO_RIF){
        //costoUnitarioStoricoComm = recuperoDocVenRigCostoUnitario(idCommessa, docVenRig);//31460
        costoUnitarioStoricoComm = recuperoDocVenRigCostoUnitario(idCommessa, docVenRig, storicoComm);//31460
        if (costoUnitarioStoricoComm != null && storicoComm.getQuantitaUMPrm() != null)
          costoTotaleStoricoComm = costoUnitarioStoricoComm.multiply(storicoComm.getQuantitaUMPrm());
        storicoComm.setCostoUnitarioOrigine(docVenRig.getCostoUnitario());//31460
      }
      else if (azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOMMA_PREZZO_VEN || azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOTTRAE_PREZZO_VEN){
        //costoTotaleStoricoComm = docVenRig.getValoreRiga();//29550
    	  costoTotaleStoricoComm = docVenRig.getValoreRigaVA();//29550
        if (costoTotaleStoricoComm != null && storicoComm.getQuantitaUMPrm() != null && storicoComm.getQuantitaUMPrm().compareTo(new BigDecimal("0")) != 0)
          costoUnitarioStoricoComm = costoTotaleStoricoComm.divide(storicoComm.getQuantitaUMPrm(), BigDecimal.ROUND_HALF_UP);
        storicoComm.setCostoUnitarioOrigine(costoUnitarioStoricoComm);//31460
        storicoComm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);//31460
      }
      
      // Valori da settare dello storico commessa
      storicoComm.setCostoTotale(costoTotaleStoricoComm);
      storicoComm.setValoreRiga(costoTotaleStoricoComm);
      storicoComm.setCostoUnitario(costoUnitarioStoricoComm);
      //storicoComm.setCostoUnitarioOrigine(costoUnitarioStoricoComm);//31460      
      storicoComm.setValorizzaCosto(valorizzaCostoStoricoComm);
    }
    else{
      //29550 inizio
      //storicoComm.setCostoTotale(docVenRig.getValoreRiga());
      //storicoComm.setValoreRiga(docVenRig.getValoreRiga());
      storicoComm.setCostoTotale(docVenRig.getValoreRigaVA());
      storicoComm.setValoreRiga(docVenRig.getValoreRigaVA());
      //29550 fine
      if(storicoComm.getCostoTotale() != null
         && storicoComm.getQuantitaUMPrm() != null
         && storicoComm.getQuantitaUMPrm().compareTo(new BigDecimal("0")) != 0)
        storicoComm.setCostoUnitario(storicoComm.getCostoTotale().divide(storicoComm.getQuantitaUMPrm(), BigDecimal.ROUND_HALF_UP));
      storicoComm.setCostoUnitarioOrigine(storicoComm.getCostoUnitario());
      storicoComm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);//31460
      storicoComm.setValorizzaCosto(StoricoCommessa.NO);
    }
  }
  
  /**
   * Calcola ValorizzaCosto dello StoricoCommessa
   * @param azioneCostoCommessa
   * @return
   */
  public char calcolaValorizzaCosto(char azioneCostoCommessa){
    char valorizzaCostoStoricoComm = StoricoCommessa.NO;
    if (azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOMMA_COSTO_RIF || azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOMMA_PREZZO_VEN)
      valorizzaCostoStoricoComm = StoricoCommessa.INCREMENTA_COSTO;
    else if (azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOTTRAE_COSTO_RIF || azioneCostoCommessa == CausaleRigaDocVen.AZ_COS_COMM_SOTTRAE_PREZZO_VEN)
      valorizzaCostoStoricoComm = StoricoCommessa.DECREMENTA_COSTO;
    return valorizzaCostoStoricoComm;
  }
  
  
  /**
   * Recupero costo unitario per la riga del documento di vendita
   * @param idCommessa
   * @param docVenRig
   * @return
   * @throws Exception
   */

  protected BigDecimal recuperoDocVenRigCostoUnitario(String idCommessa, DocumentoVenRigaPrm docVenRig) throws Exception {
    if(docVenRig.getCostoUnitario() != null && docVenRig.getCostoUnitario().compareTo(new BigDecimal("0")) != 0) {

      return docVenRig.getCostoUnitario();
    }
    else {
      return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docVenRig.getIdArticolo(), docVenRig.getIdVersioneSal(), docVenRig.getIdConfigurazione());
    }
  }

  //31460 inizio
  protected BigDecimal recuperoDocVenRigCostoUnitario(String idCommessa, DocumentoVenRigaPrm docVenRig, StoricoCommessa storicoCmm) throws Exception {
	  return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docVenRig.getIdArticolo(), docVenRig.getIdVersioneSal(), docVenRig.getIdConfigurazione(), docVenRig.getCostoUnitario(), storicoCmm);
  }
  //31460 fine
  // Fine 11293
  
  

  /**
   * willBeProcessed
   * @param docVenRig DocumentoVenRigaPrm
   * @return boolean
   */
  protected boolean willBeProcessed(DocumentoVenRigaPrm docVenRig) {
    if (docVenRig.getTestata().getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO
        && docVenRig.getTestata().getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO
        && docVenRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataRiferimento()) <= 0
        //29960 inizio
        //&& (iConsunCmm.getDataUltChiusDefAmbiente() == null
        //    || docVenRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente()) > 0))
        //33950 inizio
        /*&& (iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente()) == null 
        || docVenRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente())) > 0))*/
    	&& iConsunCmm.isDataValido(docVenRig.getTestata().getDataDocumento(), iCompactCmm))
    	//33950 fine
    	//29960 fine
      return true;

    return false;
  }

  //...FIX 9384 inizio

  /**
   * completaDatiStoricoCmm
   * @param storicoCmm StoricoCommessa
   * @param docVenRig DocumentoVenRigaPrm
   * @return StoricoCommessa
   * @throws Exception
   */
  public StoricoCommessa completaDatiStoricoCmm(StoricoCommessa storicoCmm, DocumentoVenRigaPrm docVenRig) throws Exception {
    return storicoCmm;
  }

  //...FIX 9384 fine
  
  //Fix 34308 --inizio
  public GestoreCommit getGestoreCommit() {
  	 return iGestoreCommit;
  }

  public void setGestoreCommit(GestoreCommit gestoreCommit) {
  	 iGestoreCommit = gestoreCommit;
  }
  //Fix 34308 --fine

}

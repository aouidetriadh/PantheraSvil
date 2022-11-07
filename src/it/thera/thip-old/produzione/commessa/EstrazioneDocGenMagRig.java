package it.thera.thip.produzione.commessa;

import java.math.*;
import java.util.*;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.*;
import it.thera.thip.base.articolo.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.magazzino.documenti.*;
import it.thera.thip.magazzino.movimenti.*;

/**
 * EstrazioneDocGenMagRig
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 20/05/2005 at 16:05:30
 */
/*
 * Revisions:
 * Number  Date          Owner  Description
 * 07430   09/07/2007    LP     Tolto chiamata metodo set su proxy
 * 10411   28/01/2009    DBot   Logger per performance su consuntivazione
 * 10537   30/03/2009    DBot   Riallineamento per fix WEB
 * 10913   25/05/2009    DB     Per permettere la personalizzazione
 * 29960   09/10/2019    RA		Consuntivazione in modalità definitiva eseguibile per singola commessa/ambiente commessa
 * 31460   07/07/2020    RA		Modifica valorozzazione CostoUnitario del storico per materiale/prodotti/risorse
 * 33143   17/03/2021    RA		Corretta valorizzazione costo 
 * 34308   21/09/2021    Mekki  GestoreCommit
 * 33950   06/10/2021    RA      modifica il metodo willBeProcessed 
 */

public class EstrazioneDocGenMagRig extends EstrazioneDocumenti {

  protected GestoreCommit iGestoreCommit; //Fix 34308
  
  /**
   * Constructor
   * @param consunCmm ConsuntivazioneCommesse
   * @param compactCmm CompactCommessa
   */
  public EstrazioneDocGenMagRig(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm) {
    super(consunCmm, compactCmm);
  }

  /**
   * processRighe
   * @throws Exception
   * @return boolean
   */
  protected boolean processRighe() throws Exception {
     getLogger().iniziaTipoDocumento("DOC_GEN", iCompactCmm.getIdCommessa()); //Fix 10411

    String where = DocMagGenericoRigaTM.ID_AZIENDA + " = '" + iCompactCmm.getIdAzienda() + "'" +
         " AND " + DocMagGenericoRigaTM.R_COMMESSA + " = '" + iCompactCmm.getIdCommessa() + "'" +
         " AND " + DocMagGenericoRigaTM.ID_DET_RIGA_DOC + " = 0" +
         " AND " + DocMagGenericoRigaTM.STATO + " = '" + DatiComuniEstesi.VALIDO + "'" +
         " AND " + DocMagGenericoRigaTM.STATO_AVANZAMENTO + " = '" + StatoAvanzamento.DEFINITIVO + "'";

    getLogger().startTime(); //Fix 10411
    List docGenMagRigList = DocMagGenericoRiga.retrieveList(DocMagGenericoRiga.class, where, "", false);
    getLogger().addTempoLettura(getLogger().stopTime()); //Fix 10411
    getLogger().incNumTestateLette(docGenMagRigList.size()); //Fix 10411

    for(Iterator it = docGenMagRigList.iterator(); it.hasNext();) {
      DocMagGenericoRiga docGenMagRigRig = (DocMagGenericoRiga)it.next();
      if(willBeProcessed(docGenMagRigRig)) {
        getLogger().incNumTestateFatte(); //Fix 10411

        getLogger().startTime();  // Fix 10411
        StoricoCommessa storicoCmm = createStorico(docGenMagRigRig);
        getLogger().addTempoCreazioneStorico(getLogger().stopTime()); //Fix 10411

        if(storicoCmm == null) {
          //iConsunCmm.commitWithGestoreCommit(false); //Fix 34308
          return false;
        }

        getLogger().startTime();  // Fix 10411
        attribuzioneCosti(storicoCmm);
        getLogger().addTempoAttribuzioneCosti(getLogger().stopTime()); //Fix 10411

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
   * @param docGenMagRig DocMagGenericoRiga
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStorico(DocMagGenericoRiga docGenMagRig) throws Exception {
    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    storicoCmm.setIdCommessa(docGenMagRig.getIdCommessa());
    storicoCmm.setLivelloCommessa(docGenMagRig.getCommessa().getLivelloCommessa());

    storicoCmm.setIdCommessaApp(docGenMagRig.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(docGenMagRig.getCommessa().getIdCommessaPrincipale());
    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.GENERICO);
    storicoCmm.setIdAnnoOrigine(docGenMagRig.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docGenMagRig.getNumeroDocumento());
    storicoCmm.setIdRigaOrigine(docGenMagRig.getNumeroRigaDocumento());
    storicoCmm.setIdDetRigaOrigine(docGenMagRig.getDettaglioRigaDocumento());
    storicoCmm.setNumeroOrgFormattato(docGenMagRig.getTestata().getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docGenMagRig.getTestata().getDataDocumento());
    storicoCmm.setIdCauOrgTes(docGenMagRig.getTestata().getIdCau());
    storicoCmm.setIdCauOrgRig(docGenMagRig.getIdCauRig());
    storicoCmm.setAvanzamento(false);

    //...FIX 7430 inizio
    if(docGenMagRig.getCausaleRiga() != null) {
      CausaleRigaDocGen cauRiga = (CausaleRigaDocGen)docGenMagRig.getCausaleRiga();
      //String cauMagKey = KeyHelper.buildObjectKey(new Object[]{iCompactCmm.getIdAzienda(), cauRiga.getCodiceCausaleMovMagazzino()});
      //cauRiga.setCausaleMovMagazzinoKey(cauMagKey);
      //cauRiga.setCausaleMovMagazzinoKey(cauMagKey);
      CausaleMovGenerico cauRigMag = cauRiga.getCausaleMovMagazzino();
      storicoCmm.setIdCauMagazzino(cauRigMag.getCodiceCausale());
      storicoCmm.setAzioneMagazzino(cauRigMag.getQtaGiacenza());
      storicoCmm.setIdMagazzino(docGenMagRig.getIdMagazzino());
      storicoCmm.setValorizzaCosto(recuperoDocGenMagValorizzaCosto(docGenMagRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa(), cauRigMag.getQtaGiacenza()));
    }
    //...FIX 7430 fine

    storicoCmm.setIdArticolo(docGenMagRig.getIdArticolo());
    storicoCmm.setIdVersione(docGenMagRig.getIdVersioneRcs() != null ? docGenMagRig.getIdVersioneRcs() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docGenMagRig.getIdConfigurazione());

    storicoCmm.setDescrizioneArticolo(docGenMagRig.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);

    storicoCmm.setQuantitaUMPrm(docGenMagRig.getQuantita().getQuantitaInUMPrm());
    storicoCmm.setQuantitaUMSec(docGenMagRig.getQuantita().getQuantitaInUMSec());

    storicoCmm.setIdUmPrmMag(docGenMagRig.getIdUMPrm());
    storicoCmm.setIdUmSecMag(docGenMagRig.getIdUMSec());

    String idCmm = null;
    if(docGenMagRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
      idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

    //33143 inizio
    //storicoCmm.setCostoUnitario(recuperoDocGenMagCostoUnitario(idCmm, docGenMagRig));//31460
    /*
    storicoCmm.setCostoUnitario(recuperoDocGenMagCostoUnitario(idCmm, docGenMagRig, storicoCmm));//31460
    storicoCmm.setCostoUnitarioOrigine(docGenMagRig.getCostoRiferimento());
    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));
    */
    //33143 fine

    storicoCmm.setNoFatturare(false);

    storicoCmm.setGesSaldiCommessa(docGenMagRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
    if(docGenMagRig.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(docGenMagRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(docGenMagRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }

    storicoCmm.setIdCliente(docGenMagRig.getCodiceCliente());
    storicoCmm.setIdFornitore(docGenMagRig.getCodiceFornitore());

    storicoCmm.setTipoArticolo(docGenMagRig.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(docGenMagRig.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(docGenMagRig.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(docGenMagRig.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(docGenMagRig.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(docGenMagRig.getArticolo().getArticoloDatiPianif().getIdPianificatore());

    storicoCmm.setIdCentroCosto(docGenMagRig.getIdCentroCosto() != null ? docGenMagRig.getIdCentroCosto() : docGenMagRig.getCodiceCentroRicavo());

    //Fix 04361 Mz inizio
    if((storicoCmm.getMagazzino() != null) && (storicoCmm.getMagazzino().getIdStabilimento() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getMagazzino().getIdStabilimento());
    else if(storicoCmm.getCommessa() != null)
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine
    
    //33143 inizio
    storicoCmm.setCostoUnitario(recuperoDocGenMagCostoUnitario(idCmm, docGenMagRig, storicoCmm));//31460
    storicoCmm.setCostoUnitarioOrigine(docGenMagRig.getCostoRiferimento());
    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));
    //33143 fine

    // fix 10913
    storicoCmm = completaDatiStoricoCmm(storicoCmm, docGenMagRig);
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

    iConsunCmm.commitWithGestoreCommit(false);

    return storicoCmm;
  }

  /**
   * willBeProcessed
   * @param docGenMagRig DocMagGenericoRiga
   * @return boolean
   */
  protected boolean willBeProcessed(DocMagGenericoRiga docGenMagRig) {
    boolean condition = (docGenMagRig.getArticolo().getTipoArticolo() == ArticoloBase.ART_NORMALE
      && docGenMagRig.getTestata().getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO
      && docGenMagRig.getTestata().getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO
      && docGenMagRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataRiferimento()) <= 0
      //29960 inizio
      //&& (iConsunCmm.getDataUltChiusDefAmbiente() == null
      //|| docGenMagRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente()) > 0));
      //33950 inizio
      /*&& (iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente()) == null 
      || docGenMagRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente())) > 0));*/
      && iConsunCmm.isDataValido(docGenMagRig.getTestata().getDataDocumento(), iCompactCmm));
      //33950 fine
      //29960 fine

    if(!condition)
      return false;

    CausaleRigaDocGen cauRiga = (CausaleRigaDocGen)docGenMagRig.getCausaleRiga();
    //...FIX 7430 inizio
    //String cauMagKey = KeyHelper.buildObjectKey(new Object[]{iCompactCmm.getIdAzienda(), cauRiga.getCodiceCausaleMovMagazzino()});
    //cauRiga.setCausaleMovMagazzinoKey(cauMagKey);
    if(cauRiga != null && cauRiga.getCausaleMovMagazzino() != null) {
      if(cauRiga.getCausaleMovMagazzino().getQtaGiacenza() == CausaleMovimentoMagazzino.NESSUNA)
        return false;

      if(docGenMagRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa()) {
        if(cauRiga.getCausaleMovMagazzino().getQtaRettInvPos() == CausaleMovimentoMagazzino.NESSUNA
          && cauRiga.getCausaleMovMagazzino().getQtaRettInvNeg() == CausaleMovimentoMagazzino.NESSUNA)
          return false;
      }
    }
    //...FIX 7430 fine

    return true;
  }

  /**
   * recuperoDocGenMagCostoUnitario
   * @param idCommessa String
   * @param docGenMagRig DocMagGenericoRiga
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoDocGenMagCostoUnitario(String idCommessa, DocMagGenericoRiga docGenMagRig) throws Exception {
    if(docGenMagRig.getCostoRiferimento() != null && docGenMagRig.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0) {
      return docGenMagRig.getCostoRiferimento();
    }
    else {
      return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docGenMagRig.getIdArticolo(), docGenMagRig.getIdVersioneRcs(), docGenMagRig.getIdConfigurazione());
    }
  }
  //31460 inizio
  protected BigDecimal recuperoDocGenMagCostoUnitario(String idCommessa, DocMagGenericoRiga docGenMagRig, StoricoCommessa storicoCmm) throws Exception {
	  return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docGenMagRig.getIdArticolo(), docGenMagRig.getIdVersioneRcs(), docGenMagRig.getIdConfigurazione(), docGenMagRig.getCostoRiferimento(), storicoCmm);
  }
  //31460 fine

  /**
   * recuperoDocGenMagValorizzaCosto
   * @param gesSaldiCmm boolean
   * @param qtaGiac char
   * @return char
   */
  protected char recuperoDocGenMagValorizzaCosto(boolean gesSaldiCmm, char qtaGiac) {
    if(gesSaldiCmm)
      return StoricoCommessa.NO;
    else {
      if(qtaGiac == CausaleMovimentoMagazzino.SOTTRAE)
        return StoricoCommessa.INCREMENTA_COSTO;
      else if(qtaGiac == CausaleMovimentoMagazzino.SOMMA)
        return StoricoCommessa.DECREMENTA_COSTO;
    }
    return StoricoCommessa.NO;
  }
  // fix 10913
  public StoricoCommessa completaDatiStoricoCmm(StoricoCommessa storicoCmm, DocMagGenericoRiga docGenMagRig) throws Exception {
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

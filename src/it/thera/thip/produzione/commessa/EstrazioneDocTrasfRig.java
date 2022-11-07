package it.thera.thip.produzione.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;

import it.thera.thip.base.articolo.ArticoloCosto;//30049
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.costi.*;
import it.thera.thip.magazzino.documenti.*;

/**
 * EstrazioneDocTrasfRig
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 23/05/2005 at 11:20:32
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 04810   22/12/2005    LP       Modificato gestione storico per trasferimento tra commesse
 * 06773   27/02/2007    LP       Aggiunto recupero del costo unitario dalla riga del documento
 * 10411   28/01/2009    DBot     Logger per performance su consuntivazione
 * 10537   30/03/2009    DBot     Riallineamento per fix WEB
 * 10913   25/05/2009    DB       Per permettere la personalizzazione
 * 29928   01/10/2019	 RA		  Corretta CachedStatement cSelectCostoElemWithCfg
 * 30017   14/10/2019    Bsondes  Modificarle le query di estrazione dei costi senza usare ALIAS .
 * 29960   09/10/2019    RA		  Consuntivazione in modalità definitiva eseguibile per singola commessa/ambiente commessa
 * 30049   22/10/2019    RA		  Semplificazione del calcolo costi
 * 31460   03/07/2020    RA		  Modifica valorozzazione CostoUnitario del storico per materiale/prodotti/risorse
 * 33143   17/03/2021    RA		  Corretta valorizzazione costo 
 * 34308   21/09/2021    Mekki    GestoreCommit
 * 34139   22/09/2021    Mekki    Corrggere metodo recuperoArticoloCostoUnitario usando ModalitaCostiMancanti
 * 33950   06/10/2021    RA       modifica il metodo willBeProcessed 
 */

public class EstrazioneDocTrasfRig extends EstrazioneDocumenti {
	
  protected GestoreCommit iGestoreCommit; //Fix 34308

  /**
   * cSelectCostoElemWithCfg
   */
	//Fix 30017 Inizio
  /*public static final CachedStatement cSelectCostoElemWithCfg = new CachedStatement(
    "SELECT " + CostiCommessaElemTM.COSTO_RIFERIMENTO + ", " +
    CostiCommessaElemTM.QUANTITA +
    //" FROM " + CostiCommessaElemTM.TABLE_NAME + " ELEM " +//29928
    " FROM " + CostiCommessaElemTM.TABLE_NAME + " ELEM, " +//29928
    CostiCommessaTM.TABLE_NAME + " COST" +
    " WHERE ELEM." + CostiCommessaElemTM.ID_AZIENDA + " = ?" +
    " AND (ELEM." + CostiCommessaElemTM.ID_COMMESSA + " = ?" +
    " OR ELEM." + CostiCommessaElemTM.ID_COMMESSA + " = ?)" +
    " AND COST." + CostiCommessaElemTM.ID_AZIENDA + " = ELEM." + CostiCommessaTM.ID_AZIENDA +
    " AND COST." + CostiCommessaElemTM.ID_PROGR_STORIC + " = ELEM." + CostiCommessaTM.ID_PROGR_STORIC +
    " AND COST." + CostiCommessaElemTM.ID_COMMESSA + " = ELEM." + CostiCommessaTM.ID_COMMESSA +
    //...Aggiungo la seguente condizione per congruenza con il metodo getCostoPrevistoElemento di AttribuzioneCosti
    " AND ELEM." + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "'" + //...FIX 6773
    " AND ELEM." + CostiCommessaElemTM.R_ARTICOLO + " = ?" +
    " AND ELEM." + CostiCommessaElemTM.R_VERSIONE + " = ?" +
    " AND ELEM." + CostiCommessaElemTM.R_CONFIGURAZIONE + " = ?" +
    " AND COST." + CostiCommessaTM.UFFICIALE + " = ?" +
    " AND COST." + CostiCommessaTM.TIPOLOGIA + " = ?");*/
	public static final CachedStatement cSelectCostoElemWithCfg = new CachedStatement(
	    "SELECT " + CostiCommessaElemTM.COSTO_RIFERIMENTO + " , " +
	    CostiCommessaElemTM.QUANTITA +
	    " FROM " + CostiCommessaElemTM.TABLE_NAME + " , " +
	    CostiCommessaTM.TABLE_NAME + 
	    " WHERE " + CostiCommessaElemTM.TABLE_NAME +"." + CostiCommessaElemTM.ID_AZIENDA + " = ?" +
	    " AND (" + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaElemTM.ID_COMMESSA + " = ?" +
	    " OR " + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaElemTM.ID_COMMESSA + " = ?)" +
	    " AND " + CostiCommessaTM.TABLE_NAME +"." + CostiCommessaElemTM.ID_AZIENDA + " = " + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaTM.ID_AZIENDA +
	    " AND " + CostiCommessaTM.TABLE_NAME +"." + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaTM.ID_PROGR_STORIC +
	    " AND " + CostiCommessaTM.TABLE_NAME +"." + CostiCommessaElemTM.ID_COMMESSA + " = " + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaTM.ID_COMMESSA +
	    " AND " + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "'" +
	    " AND " + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaElemTM.R_ARTICOLO + " = ?" +
	    " AND " + CostiCommessaElemTM.TABLE_NAME +"." + CostiCommessaElemTM.R_VERSIONE + " = ?" +
	    " AND " + CostiCommessaElemTM.TABLE_NAME +"."  + CostiCommessaElemTM.R_CONFIGURAZIONE + " = ?" +
	    " AND " + CostiCommessaTM.TABLE_NAME +"." + CostiCommessaTM.UFFICIALE + " = ?" +
	    " AND " + CostiCommessaTM.TABLE_NAME +"." + CostiCommessaTM.TIPOLOGIA + " = ?");

  /**
   * cSelectCostoElemWithoutCfg
   */
  /*public static final CachedStatement cSelectCostoElemWithoutCfg = new CachedStatement(
    "SELECT " + CostiCommessaElemTM.COSTO_RIFERIMENTO + ", " +
    CostiCommessaElemTM.QUANTITA +
    " FROM " + CostiCommessaElemTM.TABLE_NAME + " ELEM, " +
    CostiCommessaTM.TABLE_NAME + " COST" +
    " WHERE ELEM." + CostiCommessaElemTM.ID_AZIENDA + " = ?" +
    " AND (ELEM." + CostiCommessaElemTM.ID_COMMESSA + " = ?" +
    " OR ELEM." + CostiCommessaElemTM.ID_COMMESSA + " = ?)" +
    " AND COST." + CostiCommessaElemTM.ID_AZIENDA + " = ELEM." + CostiCommessaTM.ID_AZIENDA +
    " AND COST." + CostiCommessaElemTM.ID_PROGR_STORIC + " = ELEM." + CostiCommessaTM.ID_PROGR_STORIC +
    " AND COST." + CostiCommessaElemTM.ID_COMMESSA + " = ELEM." + CostiCommessaTM.ID_COMMESSA +
    //...Aggiungo la seguente condizione per congruenza con il metodo getCostoPrevistoElemento di AttribuzioneCosti
    " AND ELEM." + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "'" + //...FIX 6773
    " AND ELEM." + CostiCommessaElemTM.R_ARTICOLO + " = ?" +
    " AND ELEM." + CostiCommessaElemTM.R_VERSIONE + " = ?" +
    " AND ELEM." + CostiCommessaElemTM.R_CONFIGURAZIONE + " IS NULL " +
    " AND COST." + CostiCommessaTM.UFFICIALE + " = ?" +
    " AND COST." + CostiCommessaTM.TIPOLOGIA + " = ?");*/
	public static final CachedStatement cSelectCostoElemWithoutCfg = new CachedStatement(
	    "SELECT " + CostiCommessaElemTM.COSTO_RIFERIMENTO + " , " +
	    CostiCommessaElemTM.QUANTITA +
	    " FROM " + CostiCommessaElemTM.TABLE_NAME + " , " +
	    CostiCommessaTM.TABLE_NAME + 
	    " WHERE " + CostiCommessaElemTM.TABLE_NAME + "." + CostiCommessaElemTM.ID_AZIENDA + " = ?" +
	    " AND (" + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaElemTM.ID_COMMESSA + " = ?" +
	    " OR " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaElemTM.ID_COMMESSA + " = ?)" +
	    " AND " + CostiCommessaTM.TABLE_NAME + "." + CostiCommessaElemTM.ID_AZIENDA + " = " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaTM.ID_AZIENDA +
	    " AND " + CostiCommessaTM.TABLE_NAME + "." + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaTM.ID_PROGR_STORIC +
	    " AND " + CostiCommessaTM.TABLE_NAME + "." + CostiCommessaElemTM.ID_COMMESSA + " = " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaTM.ID_COMMESSA +
	    " AND " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "'" +
	    " AND " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaElemTM.R_ARTICOLO + " = ?" +
	    " AND " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaElemTM.R_VERSIONE + " = ?" +
	    " AND " + CostiCommessaElemTM.TABLE_NAME + "."  + CostiCommessaElemTM.R_CONFIGURAZIONE + " IS NULL " +
	    " AND " + CostiCommessaTM.TABLE_NAME + "." + CostiCommessaTM.UFFICIALE + " = ?" +
	    " AND " + CostiCommessaTM.TABLE_NAME + "." + CostiCommessaTM.TIPOLOGIA + " = ?");
	//Fix 30017 Fine

  /**
   * Constructor
   * @param consunCmm ConsuntivazioneCommesse
   * @param compactCmm CompactCommessa
   */
  public EstrazioneDocTrasfRig(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm) {
    super(consunCmm, compactCmm);
  }

  /**
   * processRighe
   * @throws Exception
   * @return boolean
   */
  protected boolean processRighe() throws Exception {
    //...Leggo le righe con commessa o commessaArrivo uguale alla commessa corrente
    getLogger().iniziaTipoDocumento("DOC_TRASF", iCompactCmm.getIdCommessa()); //Fix 10411
    String where = DocMagTrasferimentoRigaTM.ID_AZIENDA + " = '" + iCompactCmm.getIdAzienda() + "'" +
      //" AND " + DocMagTrasferimentoRigaTM.R_COMMESSA + " = '" + iCompactCmm.getIdCommessa() + "'" +
      //...FIX 4810
      " AND (" + DocMagTrasferimentoRigaTM.R_COMMESSA + " = '" + iCompactCmm.getIdCommessa() + "'" +
      " OR " + DocMagTrasferimentoRigaTM.R_COMMESSA_ARR + " = '" + iCompactCmm.getIdCommessa() + "')" +
      " AND " + DocMagTrasferimentoRigaTM.ID_DET_RIGA_DOC + " = 0" +
      " AND " + DocMagTrasferimentoRigaTM.STATO + " = '" + DatiComuniEstesi.VALIDO + "' " +
      " AND " + DocMagTrasferimentoRigaTM.STATO_AVANZAMENTO + " = '" + StatoAvanzamento.DEFINITIVO + "'";
    getLogger().startTime(); // Fix 10411
    List docAcqList = DocMagTrasferimentoRiga.retrieveList(DocMagTrasferimentoRiga.class, where, "", false);
    getLogger().addTempoLettura(getLogger().stopTime()); // Fix 10411
    getLogger().incNumTestateLette(docAcqList.size()); // Fix 10411

    for(Iterator it = docAcqList.iterator(); it.hasNext(); ) {
      DocMagTrasferimentoRiga docTraRig = (DocMagTrasferimentoRiga)it.next();
      if(willBeProcessed(docTraRig)) {
        //...Se commessa origine uguale a quella corrente creo una riga di storico
        if(Utils.compare(docTraRig.getIdCommessa(), iCompactCmm.getIdCommessa()) == 0) { //...FIX 4810
          //...La riga di storico la creo purchè non si riferisca ad un trasferimento
          //...relativo alla stessa commessa fra magazzini dello stesso stabilimento
          boolean creaStorico = constrollaStabilimenti(docTraRig);
          if(creaStorico) {

            getLogger().incNumTestateFatte(); // Fix 10411
            getLogger().startTime();  // Fix 10411
            StoricoCommessa storicoCmm = createStorico(docTraRig, false);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

            if(storicoCmm == null) {
              //iConsunCmm.commitWithGestoreCommit(false); //Fix 34308
              return false;
            }

            getLogger().startTime();  // Fix 10411
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime()); // Fix 10411
            //iConsunCmm.commitWithGestoreCommit(false); //Fix 34308
          }
        }

        //...Se commessa destinazione uguale a quella corrente creo una riga di storico
        if(Utils.compare(docTraRig.getIdCommessaArrivo(), iCompactCmm.getIdCommessa()) == 0) { //...FIX 4810
          //...La riga di storico la creo purchè non si riferisca ad un trasferimento
          //...relativo alla stessa commessa fra magazzini dello stesso stabilimento
          boolean creaStorico = constrollaStabilimenti(docTraRig);
          if(creaStorico) {
            getLogger().incNumTestateFatte(); // Fix 10411
            getLogger().startTime();  // Fix 10411
            StoricoCommessa storicoCmm = createStorico(docTraRig, true);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());  // Fix 10411

            if(storicoCmm == null) {
              //iConsunCmm.commitWithGestoreCommit(false); //Fix 34308
              return false;
            }

            getLogger().startTime();  // Fix 10411
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime()); // Fix 10411
            //iConsunCmm.commitWithGestoreCommit(false); //Fix 34308
          }
        }
        getGestoreCommit().commit(); //Fix 34308
      }
      it.remove(); // Fix 10411, 10437
    }
    //iConsunCmm.commitWithGestoreCommit(true);// Fix 10411 //Fix 34308
    return true;
  }

  /**
   * constrollaStabilimenti
   * @param docTraRig DocMagTrasferimentoRiga
   * @return boolean
   */
  public boolean constrollaStabilimenti(DocMagTrasferimentoRiga docTraRig) {
    boolean crea = true;
    //...Quando IdCommessa (DOC_TRA_RIG) = IdCommessaDestin (DOC_TRA_RIG)
    if(Utils.compare(docTraRig.getIdCommessa(), docTraRig.getIdCommessaArrivo()) == 0) {
      //...la riga viene trattata solo se IdStabilimento assunto da IdMagazzinoOrig (DOC_TRA_RIG)
      //...e IdStabilimento assunto da IdMagazzinoDest (DOC_TRA_RIG) sono significativi e diversi
      String idStabOrig = docTraRig.getMagazzino().getIdStabilimento();
      String idStabDest = docTraRig.getMagazzinoArrivo().getIdStabilimento();
      if(idStabOrig != null && idStabDest != null && !idStabOrig.equals(idStabDest))
        crea = true;
      else
        crea = false;
    }
    return crea;
  }

  /**
   * createSotrico
   * @param docTraRig DocMagTrasferimentoRiga
   * @param commessaDestinazione boolean
   * @throws Exception
   * @return StoricoCommessa
   */
  protected StoricoCommessa createStorico(DocMagTrasferimentoRiga docTraRig, boolean commessaDestinazione) throws Exception {

    StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

    storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
    //Fix 04361 Mz inizio
    //storicoCmm.setIdProgressivo(new Integer(Numerator.getNextInt(StoricoCommessa.ID_PROGR_NUM_ID)));
    storicoCmm.setIdProgressivo(getNextIdProgressivo());
    //Fix 04361 Mz fine
    /* Take in account Commessa destinazione */
    //storicoCmm.setIdCommessa(docTraRig.getIdCommessa());
    //storicoCmm.setLivelloCommessa(docTraRig.getCommessa().getLivelloCommessa());
    storicoCmm.setIdCommessa(commessaDestinazione ? docTraRig.getIdCommessaArrivo() : docTraRig.getIdCommessa());
    storicoCmm.setLivelloCommessa(commessaDestinazione ? docTraRig.getCommessaArrivo().getLivelloCommessa() : docTraRig.getCommessa().getLivelloCommessa());
    storicoCmm.setIdCommessaApp(storicoCmm.getCommessa().getIdCommessaAppartenenza());
    storicoCmm.setIdCommessaPrm(storicoCmm.getCommessa().getIdCommessaPrincipale());
    /* Take in account Commessa destinazione */
    //storicoCmm.setIdCommessaCol(docTraRig.getIdCommessa());
    storicoCmm.setIdCommessaCol(commessaDestinazione ? docTraRig.getIdCommessa() : docTraRig.getIdCommessaArrivo());

    storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
    storicoCmm.setTipoRigaOrigine(StoricoCommessa.TRASFERIMENTO_MAGAZZINO);
    storicoCmm.setIdAnnoOrigine(docTraRig.getAnnoDocumento());
    storicoCmm.setIdNumeroOrigine(docTraRig.getNumeroDocumento());
    storicoCmm.setIdRigaOrigine(docTraRig.getNumeroRigaDocumento());
    storicoCmm.setIdDetRigaOrigine(docTraRig.getDettaglioRigaDocumento());
    storicoCmm.setNumeroOrgFormattato(docTraRig.getTestata().getNumeroDocumentoFormattato());
    storicoCmm.setDataOrigine(docTraRig.getTestata().getDataDocumento());
    storicoCmm.setIdCauOrgTes(docTraRig.getTestata().getIdCau());
    storicoCmm.setIdCauOrgRig(docTraRig.getIdCauRig());
    storicoCmm.setAvanzamento(false);

    CausaleRigaDocTrasf cauRiga = (CausaleRigaDocTrasf)docTraRig.getCausaleRiga();

    storicoCmm.setIdCauMagazzino(commessaDestinazione ? cauRiga.getCodiceCausaleMovMagArrivo() : cauRiga.getCodiceCausaleMovMagazzino());
    storicoCmm.setAzioneMagazzino(commessaDestinazione ? AzioneMagazzino.ENTRATA : AzioneMagazzino.USCITA);
    storicoCmm.setIdMagazzino(commessaDestinazione ? docTraRig.getCodiceMagazzinoArrivo() : docTraRig.getIdMagazzino());

    storicoCmm.setIdArticolo(docTraRig.getIdArticolo());
    storicoCmm.setIdVersione(docTraRig.getIdVersioneRcs() != null ? docTraRig.getIdVersioneRcs() : new Integer("1"));
    storicoCmm.setIdConfigurazione(docTraRig.getIdConfigurazione());

    storicoCmm.setDescrizioneArticolo(docTraRig.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

    storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);

    storicoCmm.setQuantitaUMPrm(docTraRig.getQuantita().getQuantitaInUMPrm());
    storicoCmm.setQuantitaUMSec(docTraRig.getQuantita().getQuantitaInUMSec());

    storicoCmm.setIdUmPrmMag(docTraRig.getIdUMPrm());
    storicoCmm.setIdUmSecMag(docTraRig.getIdUMSec());

    String idCmm = null;
    if(docTraRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
      idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

    //31460 inizio
    //storicoCmm.setCostoUnitario(recuperoCostoUnitario(idCmm, docTraRig));
    //storicoCmm.setCostoUnitarioOrigine(storicoCmm.getCostoUnitario());
    //33143 inizio  
    /*
    storicoCmm.setCostoUnitario(recuperoCostoUnitario(idCmm, docTraRig, storicoCmm));
    storicoCmm.setCostoUnitarioOrigine(docTraRig.getCosto());
    //31460 fine
    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

      //...FIX 4810 inizio
      //...Se IdCommessaCol è uguale alla commessa corrente allora ValorizzaCosto=NO
    if(Utils.compare(storicoCmm.getIdCommessaCol(), iCompactCmm.getIdCommessa()) == 0) {
      storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
    }
    //...Se IdCommessaCol è diversa dalla commessa corrente
    else
      storicoCmm.setValorizzaCosto(commessaDestinazione ? StoricoCommessa.INCREMENTA_COSTO : StoricoCommessa.DECREMENTA_COSTO);
      //...FIX 4810 fine
    */
    //33143 fine
    storicoCmm.setGesSaldiCommessa(docTraRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
    if(docTraRig.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null) {
      storicoCmm.setIdSchemaCosto(docTraRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(docTraRig.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
    }

    storicoCmm.setTipoArticolo(docTraRig.getArticolo().getTipoArticolo());
    storicoCmm.setTipoParte(docTraRig.getArticolo().getTipoParte());
    storicoCmm.setIdGruppoProdotto(docTraRig.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
    storicoCmm.setIdClasseMerceologica(docTraRig.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
    storicoCmm.setIdClsMateriale(docTraRig.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
    storicoCmm.setIdPianificatore(docTraRig.getArticolo().getArticoloDatiPianif().getIdPianificatore());

    storicoCmm.setIdCentroCosto(docTraRig.getIdCentroCosto() != null ? docTraRig.getIdCentroCosto() : docTraRig.getCodiceCentroRicavo());

    //Fix 04361 Mz inizio
    if((storicoCmm.getMagazzino() != null) && (storicoCmm.getMagazzino().getIdStabilimento() != null))
      storicoCmm.setIdStabilimento(storicoCmm.getMagazzino().getIdStabilimento());
    else if(storicoCmm.getCommessa() != null)
      storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //Fix 04361 Mz fine
    //33143 inizio  
    storicoCmm.setCostoUnitario(recuperoCostoUnitario(idCmm, docTraRig, storicoCmm));
    storicoCmm.setCostoUnitarioOrigine(docTraRig.getCosto());
    //31460 fine
    if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

      //...FIX 4810 inizio
      //...Se IdCommessaCol è uguale alla commessa corrente allora ValorizzaCosto=NO
    if(Utils.compare(storicoCmm.getIdCommessaCol(), iCompactCmm.getIdCommessa()) == 0) {
      storicoCmm.setValorizzaCosto(StoricoCommessa.NO);
    }
    //...Se IdCommessaCol è diversa dalla commessa corrente
    else
      storicoCmm.setValorizzaCosto(commessaDestinazione ? StoricoCommessa.INCREMENTA_COSTO : StoricoCommessa.DECREMENTA_COSTO);
      //...FIX 4810 fine
    //33143 fine
    // fix 10913
    storicoCmm = completaDatiStoricoCmm(storicoCmm, docTraRig);
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
   * willBeProcessed
   * @param docTraRig DocMagTrasferimentoRiga
   * @throws Exception
   * @return boolean
   */
  protected boolean willBeProcessed(DocMagTrasferimentoRiga docTraRig) throws Exception {
    //...Ricontrollo che tutti i requisiti siano soddisfatti
    if( //(Utils.compare(docTraRig.getIdCommessa(), docTraRig.getIdCommessaArrivo()) != 0) && //...FIX 4810
      docTraRig.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa()
      && (docTraRig.getTestata().getDatiComuniEstesi().getStato() == DatiComuniEstesi.VALIDO)
      && docTraRig.getTestata().getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO
      && (docTraRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataRiferimento()) <= 0)
      //29960 inizio
      //&& (iConsunCmm.getDataUltChiusDefAmbiente() == null
      //|| (docTraRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente()) > 0)))
      //33950 inizio
      /*&& (iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente()) == null 
      || docTraRig.getTestata().getDataDocumento().compareTo(iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente())) > 0))*/
      && iConsunCmm.isDataValido(docTraRig.getTestata().getDataDocumento(), iCompactCmm))
      //33950 fine
      //29960 fine
      return true;

    return false;
  }

  /**
   * recuperoCostoUnitario
   * @param idCommessa String
   * @param docTraRig DocMagTrasferimentoRiga
   * @throws Exception
   * @return BigDecimal
   */
  protected BigDecimal recuperoCostoUnitario(String idCommessa, DocMagTrasferimentoRiga docTraRig) throws Exception {
	    BigDecimal costoUnitario = recuperoCostoUnitarioFromCostoCmmElem(docTraRig.getIdCommessa(),
	      docTraRig.getIdCommessa(),
	      //...Replace with getIdCommessaDestin
	      docTraRig.getIdArticolo(),
	      docTraRig.getIdVersioneRcs() != null ? docTraRig.getIdVersioneRcs() : new Integer("1"),
	      docTraRig.getIdConfigurazione());
	    if(costoUnitario != null) {
	      return costoUnitario;
	    }
	    //...FIX 6773 inizio
	    //...Prima di cercare nell'ambiente costi di input controllo se è stato indicato un costo sulla riga
	    else if(docTraRig.getCosto() != null && docTraRig.getCosto().compareTo(new BigDecimal("0")) != 0) {
	      return docTraRig.getCosto();
	    }
	    //...FIX 6773 fine
	    else {
	      return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docTraRig.getIdArticolo(), docTraRig.getIdVersioneRcs(), docTraRig.getIdConfigurazione());
	    }
  }
  
  //31460 inizio
  protected BigDecimal recuperoCostoUnitario(String idCommessa, DocMagTrasferimentoRiga docTraRig, StoricoCommessa storicoCmm) throws Exception {//31460
    BigDecimal costoElem = recuperoCostoUnitarioFromCostoCmmElem(docTraRig.getIdCommessa(),
      docTraRig.getIdCommessa(),
      //...Replace with getIdCommessaDestin
      docTraRig.getIdArticolo(),
      docTraRig.getIdVersioneRcs() != null ? docTraRig.getIdVersioneRcs() : new Integer("1"),
      docTraRig.getIdConfigurazione());
    BigDecimal costoRif = docTraRig.getCosto();
    if(iConsunCmm.isCostiArticoloDaDocumento()){
    	if(costoElem != null) {
    		storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_AMBIENTE_COMMESSA);
    		return costoElem;
    	}
    	else{
    		storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
    		return costoRif;
    	}
    }
    else{
    	if(iConsunCmm.getOrdineRecArticolo() == ConsuntivazioneCommesse.ORDINE_RECUPERO_DOC_AMB_TIPO){
    		if(costoElem != null) {
        		storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_AMBIENTE_COMMESSA);
        		return costoElem;
        	}
    		else{
    			if(costoRif != null && costoRif.compareTo(new BigDecimal("0")) != 0){
    				storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
    	    		return costoRif;
    			}
    			else{
    				return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docTraRig.getIdArticolo(), docTraRig.getIdVersioneRcs(), docTraRig.getIdConfigurazione(), storicoCmm);
    			}
    		}
    	}
    	else{
    		BigDecimal tmpCostoUnit = recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docTraRig.getIdArticolo(), docTraRig.getIdVersioneRcs(), docTraRig.getIdConfigurazione(), storicoCmm);
    		if(tmpCostoUnit != null){
    			return tmpCostoUnit;
    		}
    		else{
    			return costoRif;
    		}    		
    	}
    }
  }
  //31460 fine

  /**
   * recuperoCostoUnitarioFromCostoCmmElem
   * @param idCommessa String
   * @param idCommessaDestin String
   * @param idArticolo String
   * @param idVersione Integer
   * @param idConfig Integer
   * @throws Exception
   * @return BigDecimal
   */
  protected synchronized BigDecimal recuperoCostoUnitarioFromCostoCmmElem(String idCommessa, String idCommessaDestin, String idArticolo, Integer idVersione, Integer idConfig) throws Exception {
    PreparedStatement prStat;
    if(idConfig != null) {
      Database database = ConnectionManager.getCurrentDatabase();
      prStat = cSelectCostoElemWithCfg.getStatement();
      database.setString(prStat, 1, iCompactCmm.getIdAzienda());
      database.setString(prStat, 2, idCommessa);
      database.setString(prStat, 3, idCommessaDestin);
      database.setString(prStat, 4, idArticolo);
      database.setString(prStat, 5, String.valueOf(idVersione));
      database.setString(prStat, 6, String.valueOf(idConfig));
      database.setString(prStat, 7, String.valueOf(Column.TRUE_CHAR));
      database.setString(prStat, 8, String.valueOf(CostiCommessa.COSTO_PREVISTO));
    }
    else {
      Database database = ConnectionManager.getCurrentDatabase();
      prStat = cSelectCostoElemWithoutCfg.getStatement();
      database.setString(prStat, 1, iCompactCmm.getIdAzienda());
      database.setString(prStat, 2, idCommessa);
      database.setString(prStat, 3, idCommessaDestin);
      database.setString(prStat, 4, idArticolo);
      database.setString(prStat, 5, String.valueOf(idVersione));
      database.setString(prStat, 6, String.valueOf(Column.TRUE_CHAR));
      database.setString(prStat, 7, String.valueOf(CostiCommessa.COSTO_PREVISTO));
    }

    ResultSet rs = prStat.executeQuery();
    if(rs.next()) {
      BigDecimal costoRif = rs.getBigDecimal(1);
      BigDecimal qta = rs.getBigDecimal(2);
      if(costoRif != null && qta != null && qta.compareTo(new BigDecimal("0")) != 0)
        return costoRif.divide(qta, BigDecimal.ROUND_HALF_UP);
    }

    return null;
  }

  /**
   * recuperoArticoloCostoUnitario
   * @param idCommessa String
   * @param idStabilimento String
   * @param idArticolo String
   * @param idVersione Integer
   * @param idConfigurazione Integer
   * @throws Exception
   * @return BigDecimal
   */
  //...FIX 6773
  protected BigDecimal recuperoArticoloCostoUnitario(String idCommessa, String idStabilimento, String idArticolo, Integer idVersione, Integer idConfigurazione) throws Exception {
	    BigDecimal ret = super.recuperoArticoloCostoUnitario(idCommessa, idStabilimento, idArticolo, idVersione, idConfigurazione);
	    //...Se la commessa era valorizzata e non ho trovato un CostoArticolo nell'ambiente
	    //...di riferimento allora ritento la ricerca con commessa = null
	    //if(ret == null && iConsunCmm.getAmbienteCostiMancanti() != null && idCommessa != null) {//30049
	    if(ret == null && idCommessa != null) { //30049
	    	//if (iConsunCmm.getAmbienteCostiMancanti() != null) { //30049 //Fix 34139
	    	if (iConsunCmm.getModalitaCostiMancanti() == ConsuntivazioneCommesse.MOD_AMBIENTE_COSTI && iConsunCmm.getAmbienteCostiMancanti() != null) { //Fix 34139

	    		String cfgCondition = idConfigurazione != null ? CostoArticoloTM.R_CONFIGURAZIONE + " = " + idConfigurazione : CostoArticoloTM.R_CONFIGURAZIONE + " IS NULL";

	    		String stbCondition = idStabilimento != null ? CostoArticoloTM.R_STABILIMENTO + " = '" + idStabilimento + "'" : CostoArticoloTM.R_STABILIMENTO + " IS NULL";

	    		String where = CostoArticoloTM.ID_AZIENDA + " = '" + iCompactCmm.getIdAzienda() + "'" +
	    					  " AND " + CostoArticoloTM.ID_AMBIENTE + " = '" + iConsunCmm.getIdAmbienteCostiMancanti() + "'" +
	    					  " AND " + stbCondition +
	    					  " AND " + CostoArticoloTM.R_ARTICOLO + " = '" + idArticolo + "'" +
	    					  " AND " + CostoArticoloTM.R_VERSIONE + " = " + idVersione +
	    					  " AND " + cfgCondition +
	    					  " AND " + CostoArticoloTM.R_COMMESSA + " IS NULL";

	    		List costiArtList = CostoArticolo.retrieveList(CostoArticolo.class, where, "", false);

	    		if(costiArtList.size() != 0) {
	    			return((CostoArticolo)costiArtList.get(0)).getCostoRiferimento();
	    		}
	    		//30049 inizio
	    	}
	    	//else if (iConsunCmm.getIdTipoCostoMancanti() != null) { //Fix 34139
	    	else if (iConsunCmm.getModalitaCostiMancanti() == ConsuntivazioneCommesse.MOD_ARTICOLO_COSTI && iConsunCmm.getIdTipoCostoMancanti() != null) { //Fix 34139
	    		ArticoloCosto articoloCosto = ArticoloCosto.elementWithKey( iCompactCmm.getIdAzienda(),
	      																	idArticolo,
	      																	idVersione,
	      																	idConfigurazione,
	      																	iConsunCmm.getIdTipoCostoMancanti(),
	      																	iConsunCmm.getDataRiferimento());
	    		if (articoloCosto != null)
	    			return articoloCosto.getCosto();
	    	}
	      //30049 fine
	    }
	    //return null;//30049
	    return ret;//30049
  }
  
  //31460 inizio
  protected BigDecimal recuperoArticoloCostoUnitario(String idCommessa, String idStabilimento, String idArticolo, Integer idVersione, Integer idConfigurazione, StoricoCommessa storicoCmm) throws Exception {//31460 
    BigDecimal ret = super.recuperoArticoloCostoUnitario(idCommessa, idStabilimento, idArticolo, idVersione, idConfigurazione); 
    if(ret != null && ret.compareTo(new BigDecimal("0")) != 0){
    	if(iConsunCmm.getModalitaCostiMancanti() == ConsuntivazioneCommesse.MOD_AMBIENTE_COSTI){
    		storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_AMBIENTE_MANCANTI);
    	}
    	else{
    		storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_TIPO_COSTO);
    	}
    }
    //...Se la commessa era valorizzata e non ho trovato un CostoArticolo nell'ambiente
    //...di riferimento allora ritento la ricerca con commessa = null
    if(ret == null && idCommessa != null) { 
    	//if (iConsunCmm.getAmbienteCostiMancanti() != null) { //Fix 34139
    	if (iConsunCmm.getModalitaCostiMancanti() == ConsuntivazioneCommesse.MOD_AMBIENTE_COSTI && iConsunCmm.getAmbienteCostiMancanti() != null) { //Fix 34139
    		String cfgCondition = idConfigurazione != null ? CostoArticoloTM.R_CONFIGURAZIONE + " = " + idConfigurazione : CostoArticoloTM.R_CONFIGURAZIONE + " IS NULL";

    		String stbCondition = idStabilimento != null ? CostoArticoloTM.R_STABILIMENTO + " = '" + idStabilimento + "'" : CostoArticoloTM.R_STABILIMENTO + " IS NULL";

    		String where = CostoArticoloTM.ID_AZIENDA + " = '" + iCompactCmm.getIdAzienda() + "'" +
    					  " AND " + CostoArticoloTM.ID_AMBIENTE + " = '" + iConsunCmm.getIdAmbienteCostiMancanti() + "'" +
    					  " AND " + stbCondition +
    					  " AND " + CostoArticoloTM.R_ARTICOLO + " = '" + idArticolo + "'" +
    					  " AND " + CostoArticoloTM.R_VERSIONE + " = " + idVersione +
    					  " AND " + cfgCondition +
    					  " AND " + CostoArticoloTM.R_COMMESSA + " IS NULL";

    		List costiArtList = CostoArticolo.retrieveList(CostoArticolo.class, where, "", false);

    		if(costiArtList.size() != 0) {
    			storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_AMBIENTE_MANCANTI);//31460
    			return((CostoArticolo)costiArtList.get(0)).getCostoRiferimento();
    		}
    	}
    	//else if (iConsunCmm.getIdTipoCostoMancanti() != null) { //Fix 34139
    	else if (iConsunCmm.getModalitaCostiMancanti() == ConsuntivazioneCommesse.MOD_ARTICOLO_COSTI && iConsunCmm.getIdTipoCostoMancanti() != null) { //Fix 34139
    		ArticoloCosto articoloCosto = ArticoloCosto.elementWithKey( iCompactCmm.getIdAzienda(),
      																	idArticolo,
      																	idVersione,
      																	idConfigurazione,
      																	iConsunCmm.getIdTipoCostoMancanti(),
      																	iConsunCmm.getDataRiferimento());
    		if (articoloCosto != null){
    			storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_TIPO_COSTO);
    			return articoloCosto.getCosto();
    		}
    	}
    }
    return ret;
  }
  //31460 fine

  // fix 10913
  public StoricoCommessa completaDatiStoricoCmm(StoricoCommessa storicoCmm, DocMagTrasferimentoRiga docTraRig) throws Exception {
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

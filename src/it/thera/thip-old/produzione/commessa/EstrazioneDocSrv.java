package it.thera.thip.produzione.commessa;

import java.math.*;
import java.util.*;

import com.thera.thermfw.base.Utils;
import com.thera.thermfw.persist.*;
import it.thera.thip.base.cliente.Spesa;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.articolo.ArticoloVersione;
import it.thera.thip.base.articolo.ClasseMerceologica;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.produzione.documento.*;
import it.thera.thip.servizi.documento.*;
import it.thera.thip.servizi.ordsrv.AttivitaSrvProdotto;
import it.thera.thip.servizi.ordsrv.AttivitaSrvSpesa;

/**
 * EstrazioneDocSrv
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Davide Bottarelli 03/02/2009
 */
/*
 * Revisions:
 * Number  Date          Owner     Description
 * 10416   03/02/2009    DBot      Versione iniziale
 * 10537   06/03/2009    DBot      Modifica per remove da iterator, e gestione righe spesa, nullo tempo risorse
 * 10913   25/05/2009    DB        Per permettere la personalizzazione
 * 27486   25/05/2018  DBot    Reperimento costi risorsa da documento
 * 29960   09/10/2019    RA		   Consuntivazione in modalità definitiva eseguibile per singola commessa/ambiente commessa
 * 31093   15/04/2020    Mekki     Corrggere il recupero del l'importo unitario
 * 31460   03/07/2020    RA		   Modifica valorozzazione CostoUnitario del storico per materiale/prodotti/risorse
 * 33081   08/03/2021    DBot      Correzioni varie
 * 33143   17/03/2021    RA		   Corretta valorizzazione costo 
 * 34308   21/09/2021    Mekki     GestoreCommit
 * 34138   16/10/2021    Mekki     Nuovo modalità di recupero di schema/Comp. costo
 * 33950   06/10/2021    RA        modifica il metodo willBeProcessed 
 */
public class EstrazioneDocSrv extends EstrazioneDocumenti
{
   protected GestoreCommit iGestoreCommit; //Fix 34308
   
   public EstrazioneDocSrv(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm)
   {
     super(consunCmm, compactCmm);
   }

   protected boolean processRighe() throws Exception
   {
      getLogger().iniziaTipoDocumento("DOC_SRV", iCompactCmm.getIdCommessa());
      String where =
         DocumentoServizioTM.ID_AZIENDA + "='" + iCompactCmm.getIdAzienda() + "' AND " +
         DocumentoServizioTM.R_COMMESSA + "='" + iCompactCmm.getIdCommessa() + "' AND " +
         DocumentoServizioTM.STATO + "='" + DatiComuniEstesi.VALIDO + "'";

      getLogger().startTime();
      List docSrvList = DocumentoServizio.retrieveList(DocumentoServizio.class, where, DocumentoServizioTM.R_COMMESSA, false);
      getLogger().addTempoLettura(getLogger().stopTime());
      getLogger().incNumTestateLette(docSrvList.size());

      Iterator docSrvIter = docSrvList.iterator();
      while(docSrvIter.hasNext())
      {
        DocumentoServizio docSrv = (DocumentoServizio)docSrvIter.next();
        if(willBeProcessed(docSrv))
        {
          getLogger().incNumTestateFatte();
          boolean ret = createStorici(docSrv);
          if(!ret)
            return false;
          getGestoreCommit().commit(); //Fix 34308
        }
        docSrvIter.remove();//Fix 10537
      }
      //iConsunCmm.commitWithGestoreCommit(true); //Fix 34308
      getLogger().fineTipoDocumento();
      return true;
    }


   protected boolean willBeProcessed(DocumentoServizio docSrv)
   {
      java.sql.Date dataInt = docSrv.getDataIntervento();
      //29960 inizio
      //java.sql.Date dataAmb = iConsunCmm.getDataUltChiusDefAmbiente();
      java.sql.Date dataAmb = iConsunCmm.getDataUltChiusDefAmbiente(iCompactCmm.getIdAmbiente());
      //29960 fine
      boolean goodData =
         dataInt.compareTo(iConsunCmm.getDataRiferimento()) <= 0 &&
         //33950 inizio
         //(dataAmb == null || dataInt.compareTo(dataAmb) > 0);
      	 iConsunCmm.isDataValido(dataInt, iCompactCmm);
      	 //33950 fine

      CausaleDocServizio cau = docSrv.getCausale();
      boolean goodCausale =
         cau != null &&
         (cau.getTipoCausale() == CausaleDocServizio.ATTIVITA_LAVORATIVA || cau.getTipoCausale() == CausaleDocServizio.CORSI_FORMAZIONE);

      if(goodData && goodCausale)
        return true;
      return false;
    }

   protected boolean createStorici(DocumentoServizio docSrv) throws Exception
   {
      boolean ret = true;
      ret = creaStoriciMateriali(docSrv);
      if(ret)
         ret = creaStoriciRisorse(docSrv);

      if(ret)
         ret = creaStoriciSpese(docSrv);

      if(ret)
         ret = creaStoriciVersamenti(docSrv);

      return ret;
   }

   //----------------------------------------------------
   //  Elaborazione MATERIALI
   //----------------------------------------------------
   protected boolean creaStoriciMateriali(DocumentoServizio docSrv) throws Exception
   {
      List materiali = docSrv.getMateriali();
      Iterator iterMat = materiali.iterator();
      while(iterMat.hasNext())
      {
         getLogger().incNumRigheLette(1);
         DocumentoSrvRigaMateriale docSrvMat = (DocumentoSrvRigaMateriale)iterMat.next();
         if(isMaterialeDaConsuntivare(docSrvMat))
         {
            getLogger().incNumRigheFatte();
            getLogger().startTime();
            StoricoCommessa storicoCmm = creaStoricoDocSrvMateriale(docSrv, docSrvMat);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());

            if(storicoCmm == null)
            {
               iConsunCmm.commitWithGestoreCommit(false);
               return false;
            }

            getLogger().startTime();
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());
            iConsunCmm.commitWithGestoreCommit(false);
         }
      }
      return true;
   }

   protected boolean isMaterialeDaConsuntivare(DocumentoSrvRigaMateriale docSrvMat)
   {
      //Considerati solo i materiali non coperti da garanzia
      boolean daConsiderare = true;
      char tipoGaranzia = docSrvMat.getInGaranzia();
      if(tipoGaranzia == DocumentoSrvRigaMateriale.GARANZIA_PRODUTTORE ||
         tipoGaranzia == DocumentoSrvRigaMateriale.GARANZIA_TERZI)
         daConsiderare = false;
      return daConsiderare;
    }


   protected StoricoCommessa creaStoricoDocSrvMateriale(DocumentoServizio docSrv, DocumentoSrvRigaMateriale docSrvRigMat) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());
      storicoCmm.setIdCommessa(docSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(docSrv.getCommessa().getLivelloCommessa());
      storicoCmm.setIdCommessaApp(docSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(docSrv.getCommessa().getIdCommessaPrincipale());
      if(docSrvRigMat.getCommessa() != null && !docSrvRigMat.getCommessa().equals(docSrv.getCommessa()))
        storicoCmm.setIdCommessaCol(docSrvRigMat.getIdCommessa());
      storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_MATERIALE);
      storicoCmm.setIdAnnoOrigine(docSrv.getIdAnnoDocServ());
      storicoCmm.setIdNumeroOrigine(docSrv.getIdNumeroDocServ());
      storicoCmm.setIdRigaOrigine(docSrvRigMat.getIdRigaDocumento());
      storicoCmm.setNumeroOrgFormattato(docSrv.getNumeroDocFormattato());
      storicoCmm.setDataOrigine(docSrv.getDataIntervento());
      storicoCmm.setIdCauOrgTes(docSrv.getIdCausale());
      storicoCmm.setAvanzamento(false);
      storicoCmm.setIdCauMagazzino(docSrvRigMat.getIdCausaleMagazzinoMat());

      if(docSrv.getCausale().getAbilitaMateriali() == CausaleDocProduzione.PRELIEVO_MANUALE ||
         docSrv.getCausale().getAbilitaMateriali() == CausaleDocProduzione.PRELIEVO_AUTO ||
         docSrv.getCausale().getAbilitaMateriali() == CausaleDocProduzione.SCARTI ||
         docSrv.getCausale().getAbilitaMateriali() == CausaleDocProduzione.PRELIEVO_MAN_AUTO)
         storicoCmm.setAzioneMagazzino(AzioneMagazzino.USCITA);
      else if(docSrv.getCausale().getAbilitaMateriali() == CausaleDocProduzione.RESO)
         storicoCmm.setAzioneMagazzino(AzioneMagazzino.ENTRATA);

      storicoCmm.setIdMagazzino(docSrvRigMat.getIdMagazzino());
      storicoCmm.setIdArticolo(docSrvRigMat.getIdArticolo());
      storicoCmm.setIdVersione(docSrvRigMat.getIdVersione() != null ? docSrvRigMat.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazione(docSrvRigMat.getIdConfigurazione());
      storicoCmm.setDescrizioneArticolo(docSrvRigMat.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

      storicoCmm.setIdAttivita(docSrv.getIdAttivita());
      storicoCmm.setIdDipendente(docSrv.getIdDipendente());
      storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
      storicoCmm.setQuantitaUMPrm(docSrvRigMat.getQuantitaUMPrm());
      storicoCmm.setQuantitaUMSec(docSrvRigMat.getQuantitaUMSec());
      storicoCmm.setQuantitaUMAcqVen(docSrvRigMat.getQuantitaUMVen());

      storicoCmm.setIdUmPrmMag(docSrvRigMat.getIdUnitaMisuraPrmMag());
      storicoCmm.setIdUmSecMag(docSrvRigMat.getIdUnitaMisuraSec());
      storicoCmm.setIdUMAcqVen(docSrvRigMat.getIdUnitaMisuraVendita());

      String idCmm = null;
      if(docSrvRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
         idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

      //storicoCmm.setCostoUnitario(recuperaDocSrvMatCostoUnitario(idCmm, docSrvRigMat));//31460
      //33143 inizio
       /*
      storicoCmm.setCostoUnitario(recuperaDocSrvMatCostoUnitario(idCmm, docSrvRigMat, storicoCmm));//31460
      if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
         storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));
      storicoCmm.setCostoUnitarioOrigine(docSrvRigMat.getCostoRiferimento());
      */
      //33143 fine
      storicoCmm.setGesSaldiCommessa(docSrvRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
      storicoCmm.setValorizzaCosto(recuperaDocSrvMatValorizzaCosto(storicoCmm));
      storicoCmm.setNoFatturare(false);
      if(docSrvRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null)
      {
         storicoCmm.setIdSchemaCosto(docSrvRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
         storicoCmm.setIdComponenteCosto(docSrvRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
      }

      storicoCmm.setIdAnnoOrdine(docSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(docSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(docSrv.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(docSrvRigMat.getIdRigaMateriale());
      if(docSrv.getOrdineServizio() != null)
      {
         storicoCmm.setDataOrdine(docSrv.getOrdineServizio().getDataOrdine());
         storicoCmm.setIdCliente(docSrv.getOrdineServizio().getIdCliente());
      }

      storicoCmm.setTipoArticolo(docSrvRigMat.getArticolo().getTipoArticolo());
      storicoCmm.setTipoParte(docSrvRigMat.getArticolo().getTipoParte());
      storicoCmm.setIdGruppoProdotto(docSrvRigMat.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
      storicoCmm.setIdClasseMerceologica(docSrvRigMat.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
      storicoCmm.setIdClsMateriale(docSrvRigMat.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
      storicoCmm.setIdPianificatore(docSrvRigMat.getArticolo().getArticoloDatiPianif().getIdPianificatore());

      storicoCmm.setIdStabilimento(docSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());

      storicoCmm.setIdReparto(docSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(docSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(docSrv.getIdCentroCosto());
      storicoCmm.setIdArticoloPrd(docSrv.getIdArticolo());
      storicoCmm.setIdVersionePrd(docSrv.getIdVersione() != null ? docSrv.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazionePrd(docSrv.getIdConfigurazione());
      //33143 inizio
      storicoCmm.setCostoUnitario(recuperaDocSrvMatCostoUnitario(idCmm, docSrvRigMat, storicoCmm));//31460
      if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
         storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));
      storicoCmm.setCostoUnitarioOrigine(docSrvRigMat.getCostoRiferimento());
      //33143 fine
      // fix 10913
      storicoCmm = completaDatiStoricoCmmMat(storicoCmm, docSrvRigMat);
      // fine fix 10913


      if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario()))
      {
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
   
   protected BigDecimal recuperaDocSrvMatCostoUnitario(String idCommessa, DocumentoSrvRigaMateriale docSrvRigMat) throws Exception
   {
      if(docSrvRigMat.getCostoRiferimento() != null && docSrvRigMat.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0)
         return docSrvRigMat.getCostoRiferimento();
      else
         return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docSrvRigMat.getIdArticolo(), docSrvRigMat.getIdVersione(), docSrvRigMat.getIdConfigurazione());
   }

   //31460 inizio
   protected BigDecimal recuperaDocSrvMatCostoUnitario(String idCommessa, DocumentoSrvRigaMateriale docSrvRigMat, StoricoCommessa storicoCmm) throws Exception
   {
	   return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docSrvRigMat.getIdArticolo(), docSrvRigMat.getIdVersione(), docSrvRigMat.getIdConfigurazione(), docSrvRigMat.getCostoRiferimento(), storicoCmm);
   }
   //31460 fine
   protected char recuperaDocSrvMatValorizzaCosto(StoricoCommessa storicoCmm)
   {
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


   //----------------------------------------------------
   //  FINE Elaborazione MATERIALI
   //----------------------------------------------------

   //----------------------------------------------------
   //  Elaborazione RISORSE
   //----------------------------------------------------
   protected boolean creaStoriciRisorse(DocumentoServizio docSrv) throws Exception
   {
      List risorseList = docSrv.getRisorse();
      Iterator iterRsr = risorseList.iterator();
      while(iterRsr.hasNext())
      {
         getLogger().incNumRigheLette(1);
         DocumentoSrvRigaRisorsa docSrvRsr = (DocumentoSrvRigaRisorsa)iterRsr.next();
         getLogger().incNumRigheFatte();
         getLogger().startTime();
         StoricoCommessa storicoCmm = creaStoricoDocSrvRisorsa(docSrv, docSrvRsr);
         getLogger().addTempoCreazioneStorico(getLogger().stopTime());

         if(storicoCmm == null)
         {
            iConsunCmm.commitWithGestoreCommit(false);
            return false;
         }

         getLogger().startTime();
         attribuzioneCosti(storicoCmm);
         getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());

         iConsunCmm.commitWithGestoreCommit(false);
      }
      return true;
   }

   protected StoricoCommessa creaStoricoDocSrvRisorsa(DocumentoServizio docSrv, DocumentoSrvRigaRisorsa docSrvRigRsr) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());
      storicoCmm.setIdCommessa(docSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(docSrv.getCommessa().getLivelloCommessa());
      storicoCmm.setIdCommessaApp(docSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(docSrv.getCommessa().getIdCommessaPrincipale());
      storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_RISORSA);
      storicoCmm.setIdAnnoOrigine(docSrv.getIdAnnoDoc());
      storicoCmm.setIdNumeroOrigine(docSrv.getIdNumeroDoc());
      storicoCmm.setIdRigaOrigine(docSrvRigRsr.getIdRigaDocumento());
      storicoCmm.setNumeroOrgFormattato(docSrv.getNumeroDocFormattato());
      storicoCmm.setDataOrigine(docSrv.getDataIntervento());
      storicoCmm.setIdCauOrgTes(docSrv.getIdCausale());
      storicoCmm.setIdCauOrgRig(docSrvRigRsr.getIdCausaleUtilizzaRisorsa());
      storicoCmm.setAvanzamento(true);
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.NESSUNA_AZIONE);
      storicoCmm.setIdArticolo(docSrv.getIdArticolo());
      storicoCmm.setIdVersione(docSrv.getIdVersione() != null ? docSrv.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazione(docSrv.getIdConfigurazione());
      storicoCmm.setDescrizioneArticolo(docSrv.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

      storicoCmm.setIdAttivita(docSrv.getIdAttivita());
      if(docSrv.getAttivitaServizio() != null) {
         storicoCmm.setIdOperazione(docSrv.getAttivitaServizio().getIdOperazione());
      }
      storicoCmm.setIdDipendente(docSrv.getIdDipendente());

      storicoCmm.setTipoRisorsa(docSrvRigRsr.getTipoRisorsa());
      storicoCmm.setLivelloRisorsa(docSrvRigRsr.getLivelloRisorsa());
      storicoCmm.setIdRisorsa(docSrvRigRsr.getIdRisorsa());
      storicoCmm.setTipoRilevazioneRsr(docSrvRigRsr.getRisorsa().getTipoRilevazione());
      storicoCmm.setQuantitaUMPrm(docSrvRigRsr.getQuantitaUMPRM());
      storicoCmm.setQuantitaUMAcqVen(docSrvRigRsr.getQuantitaUMVendita());
      storicoCmm.setQtaScarto(docSrvRigRsr.getQuantitaScarto());
      storicoCmm.setTempo(docSrvRigRsr.getOreMillesimi());
      //Fix 10537 inizio
      if(storicoCmm.getTempo() == null)
         storicoCmm.setTempo(new BigDecimal("0"));
      //Fix 10537 fine
      storicoCmm.setIdUmPrmMag(docSrvRigRsr.getIdUnitaMisuraPrmMag());
      storicoCmm.setIdUMAcqVen(docSrvRigRsr.getIdUnitaMisuraVendita());

      //31460 inizio
      /*
      storicoCmm.setCostoUnitario(recuperaDocSrvRsrCostoUnitario(docSrvRigRsr, storicoCmm.getQuantitaUMPrm(), storicoCmm.getQtaScarto()));

      if(docSrvRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.TEMPO)
      {
         //Fix 10537 inizio
         if(storicoCmm.getCostoUnitario() != null && storicoCmm.getTempo() != null)
            storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getTempo()));
         //Fix 10537 fine
         storicoCmm.setCostoUnitarioOrigine(docSrvRigRsr.getCostoRiferimento());
      }
      else if(docSrvRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO)
      {
         storicoCmm.setCostoTotale(docSrvRigRsr.getCostoRilevato());
         storicoCmm.setCostoUnitarioOrigine(docSrvRigRsr.getCostoRilevato());
      }
      */
      //valorizzaCostoDocSrvRigRsr(docSrvRigRsr, storicoCmm);//33143
      //31460 fine

      //storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);//33143

      storicoCmm.setGesSaldiCommessa(false);
      storicoCmm.setNoFatturare(false);
      //Fix 34138 --inizio
      /*if(docSrvRigRsr.getRisorsa() != null)
      {
         storicoCmm.setIdSchemaCosto(docSrvRigRsr.getRisorsa().getIdSchemaCosto());
         storicoCmm.setIdComponenteCosto(docSrvRigRsr.getRisorsa().getIdComponenteCosto());
         //Fix 33081 --inizio
         if(docSrvRigRsr.getRisorsa().getLivelloRisorsa() == Risorsa.MATRICOLA && storicoCmm.getIdSchemaCosto() == null)
         {
            Risorsa rapp = docSrvRigRsr.getRisorsa().getRisorsaAppart();
            if(rapp != null)
            {
               storicoCmm.setIdSchemaCosto(rapp.getIdSchemaCosto());
               storicoCmm.setIdComponenteCosto(rapp.getIdComponenteCosto());
            }
         }
         //Fix 33081 --fine
      }*/
      trovaSchemaCostoRisorsa(storicoCmm, docSrv.getAttivita(), docSrvRigRsr.getRisorsa());
      //Fix 34138 --fine
      storicoCmm.setIdAnnoOrdine(docSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(docSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(docSrv.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(docSrvRigRsr.getIdRigaRisorsa());
      if(docSrv.getOrdineServizio() != null) {
         storicoCmm.setDataOrdine(docSrv.getOrdineServizio().getDataOrdine());
         storicoCmm.setIdCliente(docSrv.getOrdineServizio().getIdCliente());
      }

      storicoCmm.setIdStabilimento(docSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      storicoCmm.setIdReparto(docSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(docSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(docSrv.getIdCentroCosto());

      storicoCmm.setIdArticoloPrd(docSrv.getIdArticolo());
      storicoCmm.setIdVersionePrd(docSrv.getIdVersione());
      storicoCmm.setIdConfigurazionePrd(docSrv.getIdConfigurazione());

      valorizzaCostoDocSrvRigRsr(docSrvRigRsr, storicoCmm);//33143
      storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);//33143
      // fix 10913
      storicoCmm = completaDatiStoricoCmmRis(storicoCmm, docSrvRigRsr);
      // fine fix 10913


      if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario()))
      {
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


   protected BigDecimal recuperaDocSrvRsrCostoUnitario(DocumentoSrvRigaRisorsa docSrvRigRsr, BigDecimal qtaPrmMag, BigDecimal qtaScarta) throws Exception
   {
      if(docSrvRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO)
         return docSrvRigRsr.getCostoRilevato();
      else
         return recuperaDocSrvRsrCostoRif(docSrvRigRsr);
    }

   protected BigDecimal recuperaDocSrvRsrCostoRif(DocumentoSrvRigaRisorsa docPrdRigRsr) throws Exception
   {
      //Fix 27486 inizio
      if(iConsunCmm.isCostiRisorsaDaDocumento())
      {
         return docPrdRigRsr.getCostoRiferimento();
      }
      else
      {
      if(docPrdRigRsr.getCostoRiferimento() != null && docPrdRigRsr.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0)
      {
         return docPrdRigRsr.getCostoRiferimento();
      }
      else
      {
         BigDecimal costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(), docPrdRigRsr.getTipoRisorsa(), docPrdRigRsr.getLivelloRisorsa(), docPrdRigRsr.getIdRisorsa());
         if(costoRif == null || costoRif.compareTo(new BigDecimal("0")) == 0)
         {
            if(docPrdRigRsr.getRisorsa().getRisorsaAppart() != null && docPrdRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa() == Risorsa.RISORSA)
               costoRif =
                  recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(),
                                               docPrdRigRsr.getRisorsa().getRisorsaAppart().getTipoRisorsa(),
                                               docPrdRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa(),
                                               docPrdRigRsr.getRisorsa().getRisorsaAppart().getIdRisorsa());
         }
         return costoRif;
      }
      }
      //Fix 27486 fine
   }


   //----------------------------------------------------
   //  FINE Elaborazione RISORSE
   //----------------------------------------------------

   //----------------------------------------------------
   //  Elaborazione SPESE
   //----------------------------------------------------
   protected boolean creaStoriciSpese(DocumentoServizio docSrv) throws Exception
   {
      List spese = docSrv.getSpese();
      Iterator iterSpese = spese.iterator();
      while(iterSpese.hasNext())
      {
         //Considerate solo le spese con classe merciologica per servizi non abbiamo errore bloccante ma solo warning???
         getLogger().incNumRigheLette(1);
         DocumentoSrvRigaSpesa docSrvSpesa = (DocumentoSrvRigaSpesa)iterSpese.next();
         //Fix 10537 solo quelle con classe merciol. valorizzata e non percentuali
         //Tolte anche le spese percentuali
         if(isRigaSpesaDaProcessare(docSrvSpesa))
         {
            getLogger().incNumRigheFatte();
            getLogger().startTime();
            StoricoCommessa storicoCmm = creaStoricoDocSrvSpesa(docSrv, docSrvSpesa);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());

            if(storicoCmm == null)
            {
               iConsunCmm.commitWithGestoreCommit(false);
               return false;
            }

            getLogger().startTime();
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());

            iConsunCmm.commitWithGestoreCommit(false);
         }
      }
      return true;
   }


   protected StoricoCommessa creaStoricoDocSrvSpesa(DocumentoServizio docSrv, DocumentoSrvRigaSpesa docSrvRigSps) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());

      storicoCmm.setIdCommessa(docSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(docSrv.getCommessa().getLivelloCommessa());

      storicoCmm.setIdCommessaApp(docSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(docSrv.getCommessa().getIdCommessaPrincipale());
      storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_SPESA);
      storicoCmm.setIdAnnoOrigine(docSrv.getIdAnnoDoc());
      storicoCmm.setIdNumeroOrigine(docSrv.getIdNumeroDoc());
      storicoCmm.setIdRigaOrigine(docSrvRigSps.getIdRigaAttivita());
      storicoCmm.setNumeroOrgFormattato(docSrv.getNumeroDocFormattato());
      storicoCmm.setDataOrigine(docSrv.getDataIntervento());
      storicoCmm.setIdCauOrgTes(docSrv.getIdCausale());
      storicoCmm.setAvanzamento(false);
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.NESSUNA_AZIONE);

      Spesa spesa = docSrvRigSps.getSpesa();
      //Fix 10537 inizio
      Articolo art = spesa.getArticolo();
      storicoCmm.setIdArticolo(art.getIdArticolo());
      storicoCmm.setIdVersione(new Integer("1"));
      storicoCmm.setIdConfigurazione(null);
      storicoCmm.setDescrizioneArticolo(art.getDescrizioneArticoloNLS().getDescrizione());
      ClasseMerceologica cmArt = art.getArticoloDatiProduz().getClasseMerclg();
      storicoCmm.setIdSchemaCosto(cmArt.getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(cmArt.getIdComponenteCosto());
      /*
      if(spesa != null && spesa.getArticolo() != null)
      {
         Articolo art = spesa.getArticolo();
         storicoCmm.setIdArticolo(art.getIdArticolo());
         storicoCmm.setIdVersione(new Integer("1"));
         storicoCmm.setIdConfigurazione(null);
         storicoCmm.setDescrizioneArticolo(art.getDescrizioneArticoloNLS().getDescrizione());
         ClasseMerceologica cmArt = art.getArticoloDatiProduz().getClasseMerclg();
         if(cmArt != null)
         {
            storicoCmm.setIdSchemaCosto(cmArt.getIdSchemaCosto());
            storicoCmm.setIdComponenteCosto(cmArt.getIdComponenteCosto());
         }
      }
      else
         return null;
      */
      //Fix 10537 fine

      storicoCmm.setIdAttivita(docSrv.getIdAttivita());
      if(docSrv.getAttivitaServizio() != null) {
         storicoCmm.setIdOperazione(docSrv.getAttivitaServizio().getIdOperazione());
      }
      storicoCmm.setIdDipendente(docSrv.getIdDipendente());
      storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
      storicoCmm.setQuantitaUMPrm(docSrvRigSps.getQuantitaUMPrm());
      storicoCmm.setQuantitaUMAcqVen(docSrvRigSps.getQuantitaUMVen());
      if((storicoCmm.getQuantitaUMPrm() == null) || (Utils.compare(storicoCmm.getQuantitaUMPrm(), new BigDecimal("0")) == 0))
         storicoCmm.setQuantitaUMPrm(storicoCmm.getQuantitaUMAcqVen());

      storicoCmm.setIdUmPrmMag(docSrvRigSps.getIdUnitaMisuraPrmMag());
      storicoCmm.setIdUMAcqVen(docSrvRigSps.getIdUnitaMisuraVendita());
      if(storicoCmm.getIdUmPrmMag() == null)
         storicoCmm.setIdUmPrmMag(storicoCmm.getIdUMAcqVen());

      //33143 inizio
      /*
      storicoCmm.setCostoTotale(docSrvRigSps.getImpSpesa());
      storicoCmm.setValoreRiga(docSrvRigSps.getImpSpesa());
      storicoCmm.setCostoUnitario(recuperaDocSrvSpsCostoUnitario(docSrvRigSps, storicoCmm.getQuantitaUMPrm(), storicoCmm.getQtaScarto()));
      storicoCmm.setCostoUnitarioOrigine(storicoCmm.getCostoUnitario());
      storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
      */
      //33143 fine
      storicoCmm.setGesSaldiCommessa(false);
      storicoCmm.setNoFatturare(false);
      storicoCmm.setIdAnnoOrdine(docSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(docSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(docSrv.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(docSrvRigSps.getIdRigaSpesa());
      if(docSrv.getOrdineServizio() != null) {
         storicoCmm.setDataOrdine(docSrv.getOrdineServizio().getDataOrdine());
         storicoCmm.setIdCliente(docSrv.getOrdineServizio().getIdCliente());
      }

      storicoCmm.setIdStabilimento(docSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      storicoCmm.setIdReparto(docSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(docSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(docSrv.getIdCentroCosto());

      storicoCmm.setIdArticoloPrd(docSrv.getIdArticolo());
      storicoCmm.setIdVersionePrd(docSrv.getIdVersione());
      storicoCmm.setIdConfigurazionePrd(docSrv.getIdConfigurazione());
      //33143 inizio
      storicoCmm.setCostoTotale(docSrvRigSps.getImpSpesa());
      storicoCmm.setValoreRiga(docSrvRigSps.getImpSpesa());
      storicoCmm.setCostoUnitario(recuperaDocSrvSpsCostoUnitario(docSrvRigSps, storicoCmm.getQuantitaUMPrm(), storicoCmm.getQtaScarto()));
      storicoCmm.setCostoUnitarioOrigine(storicoCmm.getCostoUnitario());
      storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
      //33143 fine
      // fix 10913
      storicoCmm = completaDatiStoricoCmmSpe(storicoCmm, docSrvRigSps);
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

   protected BigDecimal recuperaDocSrvSpsCostoUnitario(DocumentoSrvRigaSpesa docSrvRigSps, BigDecimal qtaUmPrm, BigDecimal qtaScarto)
   {
	  //return docSrvRigSps.getImpSpesa(); //Fix 31093
	  return docSrvRigSps.getImpPrcSpesa(); //Fix 31093
   }

   //----------------------------------------------------
   //  FINE Elaborazione SPESE
   //----------------------------------------------------

   //----------------------------------------------------
   //  Elaborazione VERSAMENTI
   //----------------------------------------------------
   protected boolean creaStoriciVersamenti(DocumentoServizio docSrv) throws Exception
   {
      List versamenti = docSrv.getVersamenti();
      Iterator iterVer = versamenti.iterator();
      getLogger().incNumRigheLette(versamenti.size());
      while(iterVer.hasNext())
      {
         DocumentoSrvRigaVersamento docSrvVrs = (DocumentoSrvRigaVersamento)iterVer.next();
         if(isRigaVersamentoDaProcessare(docSrvVrs))
         {
            getLogger().incNumRigheFatte();
            getLogger().startTime();
            StoricoCommessa storicoCmm = creaStoricoDocSrvVersamento(docSrv, docSrvVrs);
            getLogger().addTempoCreazioneStorico(getLogger().stopTime());

            if(storicoCmm == null)
            {
               iConsunCmm.commitWithGestoreCommit(false);
               return false;
            }

            getLogger().startTime();
            attribuzioneCosti(storicoCmm);
            getLogger().addTempoAttribuzioneCosti(getLogger().stopTime());

            iConsunCmm.commitWithGestoreCommit(false);
         }
      }
      return true;
   }

   protected boolean isRigaVersamentoDaProcessare(DocumentoSrvRigaVersamento docSrvVrs)
   {
      boolean daProcessare = true;
      //Eseguo test anche su Prodotto primario (altre scelte) anche se per servizi non è previsto
      if(docSrvVrs.getTipoProdotto() == AttivitaSrvProdotto.PRODOTTO_PRIMARIO ||
         docSrvVrs.getTipoProdotto() == '1')
         daProcessare = false;
      return daProcessare;
   }

   //Fix 10537 inizio
   protected boolean isRigaSpesaDaProcessare(DocumentoSrvRigaSpesa docSrvSpesa)
   {
      //Eliminate le spese percentuali perchè la consuntiazione sarebbe fatta a costi teorici e mai effettivi e
      //quindi non serv a niente
      if(!docSrvSpesa.getSpesaPrc())
      {
         Spesa spesa = docSrvSpesa.getSpesa();
         if(spesa != null && spesa.getArticolo() != null)
         {
            Articolo art = spesa.getArticolo();
            ClasseMerceologica cmArt = art.getArticoloDatiProduz().getClasseMerclg();
            if(cmArt != null)
               return true;
         }
      }
      return false;
   }
   //Fix 10537 fine


   protected StoricoCommessa creaStoricoDocSrvVersamento(DocumentoServizio docSrv, DocumentoSrvRigaVersamento docSrvRigVrs) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());
      storicoCmm.setIdCommessa(docSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(docSrv.getCommessa().getLivelloCommessa());

      storicoCmm.setIdCommessaApp(docSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(docSrv.getCommessa().getIdCommessaPrincipale());
      if(docSrvRigVrs.getCommessa() != null && !docSrvRigVrs.getCommessa().equals(docSrv.getCommessa()))
         storicoCmm.setIdCommessaCol(docSrvRigVrs.getIdCommessa());
      storicoCmm.setDocumentoOrigine(StoricoCommessa.DOCUMENTO);
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_PRODOTTO);
      storicoCmm.setIdAnnoOrigine(docSrv.getIdAnnoDoc());
      storicoCmm.setIdNumeroOrigine(docSrv.getIdNumeroDoc());
      storicoCmm.setIdRigaOrigine(docSrvRigVrs.getIdRigaDocumento());
      storicoCmm.setNumeroOrgFormattato(docSrv.getNumeroDocFormattato());
      storicoCmm.setDataOrigine(docSrv.getDataIntervento());
      storicoCmm.setIdCauOrgTes(docSrv.getIdCausale());
      storicoCmm.setAvanzamento(false);
      storicoCmm.setIdCauMagazzino(docSrvRigVrs.getIdCausaleMagazzinoVrs());
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.ENTRATA);
      storicoCmm.setIdMagazzino(docSrvRigVrs.getIdMagazzino());
      storicoCmm.setIdArticolo(docSrvRigVrs.getIdArticolo());
      storicoCmm.setIdVersione(docSrvRigVrs.getIdVersione() != null ? docSrvRigVrs.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazione(docSrvRigVrs.getIdConfigurazione());
      storicoCmm.setDescrizioneArticolo(docSrvRigVrs.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

      storicoCmm.setIdAttivita(docSrv.getIdAttivita());
      storicoCmm.setIdDipendente(docSrv.getIdDipendente());
      storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
      storicoCmm.setQuantitaUMPrm(docSrvRigVrs.getQuantitaUMPrm());
      storicoCmm.setQuantitaUMSec(docSrvRigVrs.getQuantitaUMSec());
      storicoCmm.setQuantitaUMAcqVen(docSrvRigVrs.getQuantitaUMVen());

      storicoCmm.setIdUmPrmMag(docSrvRigVrs.getIdUnitaMisuraPrmMag());
      storicoCmm.setIdUmSecMag(docSrvRigVrs.getIdUnitaMisuraSecMag());
      storicoCmm.setIdUMAcqVen(docSrvRigVrs.getIdUnitaMisuraVendita());

      String idCmm = null;
      if(docSrvRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
         idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

      //storicoCmm.setCostoUnitario(recuperaDocSrvVrsCostoUnitario(idCmm, docSrvRigVrs));//31460
      //33143 inizio
      /*
      storicoCmm.setCostoUnitario(recuperaDocSrvVrsCostoUnitario(idCmm, docSrvRigVrs, storicoCmm));//31460

      if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
         storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

      storicoCmm.setCostoUnitarioOrigine(docSrvRigVrs.getCostoRiferimento());
      storicoCmm.setGesSaldiCommessa(docSrvRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
      storicoCmm.setValorizzaCosto(recuperaDocSrvVrsValorizzaCosto(docSrvRigVrs.getTipoProdotto(), storicoCmm.getGesSaldiCommessa()));
      */
      //33143 fine

      storicoCmm.setNoFatturare(false);

      if(docSrvRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null)
      {
         storicoCmm.setIdSchemaCosto(docSrvRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
         storicoCmm.setIdComponenteCosto(docSrvRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
      }

      storicoCmm.setIdAnnoOrdine(docSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(docSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(docSrv.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(docSrvRigVrs.getIdRigaProdotto());
      if(docSrv.getOrdineServizio() != null)
      {
         storicoCmm.setDataOrdine(docSrv.getOrdineServizio().getDataOrdine());
         storicoCmm.setIdCliente(docSrv.getOrdineServizio().getIdCliente());
      }

      storicoCmm.setTipoArticolo(docSrvRigVrs.getArticolo().getTipoArticolo());
      storicoCmm.setTipoParte(docSrvRigVrs.getArticolo().getTipoParte());
      storicoCmm.setIdGruppoProdotto(docSrvRigVrs.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
      storicoCmm.setIdClasseMerceologica(docSrvRigVrs.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
      storicoCmm.setIdClsMateriale(docSrvRigVrs.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
      storicoCmm.setIdPianificatore(docSrvRigVrs.getArticolo().getArticoloDatiPianif().getIdPianificatore());

      storicoCmm.setIdStabilimento(docSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      storicoCmm.setIdReparto(docSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(docSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(docSrv.getIdCentroCosto());

      if(!docSrvRigVrs.getArticolo().equals(docSrv.getArticolo()))
      {
         storicoCmm.setIdArticoloPrd(docSrvRigVrs.getIdArticolo());
         storicoCmm.setIdVersionePrd(docSrvRigVrs.getIdVersione() != null ? docSrvRigVrs.getIdVersione() : new Integer("1"));
         storicoCmm.setIdConfigurazionePrd(docSrvRigVrs.getIdConfigurazione());
      }
      //33143 inizio
      storicoCmm.setCostoUnitario(recuperaDocSrvVrsCostoUnitario(idCmm, docSrvRigVrs, storicoCmm));//31460

      if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null)
         storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

      storicoCmm.setCostoUnitarioOrigine(docSrvRigVrs.getCostoRiferimento());
      storicoCmm.setGesSaldiCommessa(docSrvRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
      storicoCmm.setValorizzaCosto(recuperaDocSrvVrsValorizzaCosto(docSrvRigVrs.getTipoProdotto(), storicoCmm.getGesSaldiCommessa()));
      //33143 fine
      // fix 10913
      storicoCmm = completaDatiStoricoCmmVrs(storicoCmm, docSrvRigVrs);
      // fine fix 10913


      if(hasCostoErrore(storicoCmm, storicoCmm.getCostoUnitario()))
      {
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

   protected BigDecimal recuperaDocSrvVrsCostoUnitario(String idCommessa, DocumentoSrvRigaVersamento docPrdRigVrs) throws Exception
   {
      if(docPrdRigVrs.getCostoRiferimento() != null && docPrdRigVrs.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0)
         return docPrdRigVrs.getCostoRiferimento();
      else
         return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docPrdRigVrs.getIdArticolo(), docPrdRigVrs.getIdVersione(), docPrdRigVrs.getIdConfigurazione());
   }
   
   //31460 inizio
   protected BigDecimal recuperaDocSrvVrsCostoUnitario(String idCommessa, DocumentoSrvRigaVersamento docPrdRigVrs, StoricoCommessa storicoCmm) throws Exception
   {
	   return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), docPrdRigVrs.getIdArticolo(), docPrdRigVrs.getIdVersione(), docPrdRigVrs.getIdConfigurazione(), docPrdRigVrs.getCostoRiferimento(), storicoCmm);
   }
   
   protected void valorizzaCostoDocSrvRigRsr(DocumentoSrvRigaRisorsa docSrvRigRsr, StoricoCommessa storicoCmm) throws Exception {
	   BigDecimal tmpCosTotale = new BigDecimal("0");
	   BigDecimal tmpCosUnitario = new BigDecimal("0");
	   BigDecimal tmpCosUnitarioOrig = new BigDecimal("0");
	   BigDecimal qtaPrmMag = storicoCmm.getQuantitaUMPrm();
	   BigDecimal qtaScarta = storicoCmm.getQtaScarto();
	   if(docSrvRigRsr.getRisorsa() != null){
		   if(docSrvRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO) {
			   tmpCosUnitario = docSrvRigRsr.getCostoRilevato();
			   tmpCosUnitarioOrig = docSrvRigRsr.getCostoRilevato();
			   tmpCosTotale = docSrvRigRsr.getCostoRilevato();
			   storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
		   }
		   else{
			   tmpCosUnitario = recuperoDocSrvRigRsrCostoRif(docSrvRigRsr, storicoCmm);
			   tmpCosUnitarioOrig = docSrvRigRsr.getCostoRiferimento();
			   if(tmpCosUnitario != null){
				   if(docSrvRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.TEMPO) {
					   tmpCosTotale = tmpCosUnitario.multiply(storicoCmm.getTempo());
				   }
				   else if(docSrvRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.QUANTITA) {
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
	  
   protected BigDecimal recuperoDocSrvRigRsrCostoRif(DocumentoSrvRigaRisorsa docSrvRigRsr, StoricoCommessa storicoCmm) throws Exception {
	   BigDecimal costoDocumento = docSrvRigRsr.getCostoRiferimento();
	   if(iConsunCmm.isCostiRisorsaDaDocumento()) {
		   storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
		   return docSrvRigRsr.getCostoRiferimento();
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

   protected char recuperaDocSrvVrsValorizzaCosto(char tipoProdotto, boolean gesSaldiCmm)
   {
      //Inserita anche se non gestita
      if(tipoProdotto == AttivitaSrvProdotto.PRODOTTO_PRIMARIO/* || tipoProdotto == AttivitaSrvProdotto.PRODOTTO_PRIMARIO_ALTRE*/)
         return StoricoCommessa.INCREMENTA_COSTI_INDIRETTI;

      if((tipoProdotto == AttivitaSrvProdotto.SOTTOPRODOTTO || tipoProdotto == AttivitaSrvProdotto.SFRIDO_ROTTAME) && !gesSaldiCmm)
         return StoricoCommessa.DECREMENTA_COSTO;

      if((tipoProdotto == AttivitaSrvProdotto.SOTTOPRODOTTO || tipoProdotto == AttivitaSrvProdotto.SFRIDO_ROTTAME) && gesSaldiCmm)
         return StoricoCommessa.NO; //Fix 10537

      if(tipoProdotto == AttivitaSrvProdotto.SCARTO)
         return StoricoCommessa.NO; //Fix 10537

      return StoricoCommessa.NO; //Fix 10537
    }

    // fix 10913
    public StoricoCommessa completaDatiStoricoCmmMat(StoricoCommessa storicoCmm, DocumentoSrvRigaMateriale docSrvRigMat) throws Exception {
      return storicoCmm;
    }

    public StoricoCommessa completaDatiStoricoCmmRis(StoricoCommessa storicoCmm, DocumentoSrvRigaRisorsa docSrvRigRsr) throws Exception {
      return storicoCmm;
    }

    public StoricoCommessa completaDatiStoricoCmmSpe(StoricoCommessa storicoCmm, DocumentoSrvRigaSpesa docSrvRigSps) throws Exception {
      return storicoCmm;
    }

    public StoricoCommessa completaDatiStoricoCmmVrs(StoricoCommessa storicoCmm, DocumentoSrvRigaVersamento docSrvRigVrs) throws Exception {
      return storicoCmm;
    }

    // fine fix 10913

   //----------------------------------------------------
   //  FINE Elaborazione VERSAMENTI
   //----------------------------------------------------
   
    //Fix 34308 --inizio
    public GestoreCommit getGestoreCommit() {
    	 return iGestoreCommit;
    }

    public void setGestoreCommit(GestoreCommit gestoreCommit) {
    	 iGestoreCommit = gestoreCommit;
    }
    //Fix 34308 --fine

}

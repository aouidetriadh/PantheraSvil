package it.thera.thip.produzione.commessa;

import java.math.*;
import java.util.*;

import com.thera.thermfw.base.Utils;
import com.thera.thermfw.persist.*;
import it.thera.thip.base.cliente.Spesa;
import it.thera.thip.base.articolo.*;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.modpro.Attivita;
import it.thera.thip.produzione.ordese.OrdineEsecutivo;
import it.thera.thip.servizi.assistenzaManut.CausaleOrdineServizio;
import it.thera.thip.servizi.ordsrv.*;

/**
 * EstrazioneOrdSrv
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Davide Bottarelli 03/02/2009
 */
/*
 * Revisions:
 * Number  Date          Owner     Description
 * 10537   04/03/2009    DBot      Versione iniziale
 * 10913   25/05/2009    DB        Per permettere la personalizzazione
 * 27486   25/05/2018    DBot      Reperimento costi risorsa da documento
 * 31460   03/07/2020    RA		   Modifica valorozzazione CostoUnitario del storico per materiale/prodotti/risorse
 * 33081   08/03/2021    DBot      Correzioni varie
 * 33143   17/03/2021    RA		   Corretta valorizzazione costo
 * 34308   21/09/2021    Mekki     GestoreCommit 
 * 34138   16/10/2021    Mekki     Nuovo modalità di recupero di schema/Comp. costo
 * 33950   06/10/2021    RA        Corretta valorizzazione DocumentoOrigine 
 */
public class EstrazioneOrdSrv extends EstrazioneDocumenti
{
   protected GestoreCommit iGestoreCommit; //Fix 34308
	
   //Lascio commentatala estione delle spese percentuali perchè per ora non viene utilizzata //XX
   //in quanto solo una consuntivazione teorica e non effettiva
   //boolean ordineConSpesePercentuali = false;

   public EstrazioneOrdSrv(ConsuntivazioneCommesse consunCmm, CompactCommessa compactCmm)
   {
      super(consunCmm, compactCmm);
   }

   //ricordarsi le modifiche agli estrattori dovuti alla 10411 modificata
   protected boolean processRighe() throws Exception
   {
      getLogger().iniziaTipoDocumento("ORD_SRV", iCompactCmm.getIdCommessa());
      String where =
         OrdineServizioTM.ID_AZIENDA + "='" + iCompactCmm.getIdAzienda() + "' AND " +
         OrdineServizioTM.R_COMMESSA + "='" + iCompactCmm.getIdCommessa() + "' AND " +
         OrdineServizioTM.STATO + "='" + DatiComuniEstesi.VALIDO + "' AND (" +
         OrdineServizioTM.STATO_ORDINE + " = '" + OrdineServizio.ST_DEFINITIVO + "' OR " +
         OrdineServizioTM.STATO_ORDINE + " = '" + OrdineServizio.ST_IN_CORSO + "')";

      getLogger().startTime();
      List ordSrvList = OrdineServizio.retrieveList(OrdineServizio.class, where, OrdineServizioTM.R_COMMESSA, false);
      getLogger().addTempoLettura(getLogger().stopTime());
      getLogger().incNumTestateLette(ordSrvList.size());

      Iterator ordSrvIter = ordSrvList.iterator();
      while(ordSrvIter.hasNext())
      {
        OrdineServizio ordSrv = (OrdineServizio)ordSrvIter.next();
        if(willBeProcessed(ordSrv))
        {
          getLogger().incNumTestateFatte();
          boolean ret = createStorici(ordSrv);
          if(!ret)
            return false;
          getGestoreCommit().commit(); //Fix 34308
        }
        //ordineConSpesePercentuali = false; //Azzero presenza spese percentuali
        ordSrvIter.remove();
      }
      iConsunCmm.commitWithGestoreCommit(true);
      getLogger().fineTipoDocumento();
      return true;
    }


   protected boolean willBeProcessed(OrdineServizio ordSrv)
   {
      CausaleOrdineServizio cau = ordSrv.getCausaleOrdineServizio();
      boolean goodCausale =
         cau != null &&
         (cau.getTipoOrdine() !=  CausaleOrdineServizio.NOLLEGGIO);

      return goodCausale;
/*
      public static final char ASSISTENZA = '0';
      public static final char GUASTI_REPA = '1';
      public static final char MANUTENZIONE_PROG = '2';
      public static final char SVILUPPO = '3';
      public static final char NOLLEGGIO = '4';
  */
    }

   protected boolean createStorici(OrdineServizio ordSrv) throws Exception
   {
      //XX ordineConSpesePercentuali = isOrdineConSpesePercentuali(ordSrv);

      boolean ret = true;
      ret = creaStoriciMateriali(ordSrv);
      if(ret)
         ret = creaStoriciRisorse(ordSrv);

      if(ret)
         ret = creaStoriciVersamenti(ordSrv);

      if(ret)
         ret = creaStoriciSpese(ordSrv);

      return ret;
   }

   /* XX
   protected boolean isOrdineConSpesePercentuali()
   {
      return ordineConSpesePercentuali;
   }

   public boolean isOrdineConSpesePercentuali(OrdineServizio ordSrv) throws Exception
   {
      boolean spePerc = false;
      Iterator iterAtt = ordSrv.getAttivitaServizi().iterator();
      while(iterAtt.hasNext() && !spePerc)
      {
         AttivitaServizio atv = (AttivitaServizio) iterAtt.next();
         List spese = atv.getSpese();
         Iterator iterSpe = spese.iterator();
         while(iterSpe.hasNext())
         {
            AttivitaSrvSpesa ordSrvSpesa = (AttivitaSrvSpesa)iterSpe.next();
            if(ordSrvSpesa.isSpesaPercentuale() && isRigaSpesaDaProcessare(ordSrvSpesa))
               spePerc = true;
         }
      }
      return spePerc;
   }
   */

   //----------------------------------------------------
   //  Elaborazione MATERIALI
   //----------------------------------------------------
   protected boolean creaStoriciMateriali(OrdineServizio ordSrv) throws Exception
   {
      List materiali = ordSrv.getMaterialiEffettivi();
      Iterator iterMat = materiali.iterator();
      while(iterMat.hasNext())
      {
         getLogger().incNumRigheLette(1);
         AttivitaSrvMateriale ordSrvMat = (AttivitaSrvMateriale)iterMat.next();
         if(isMaterialeDaConsuntivare(ordSrvMat))
         {
            getLogger().incNumRigheFatte();
            getLogger().startTime();
            StoricoCommessa storicoCmm = creaStoricoOrdSrvMateriale(ordSrv, ordSrvMat);
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

   protected boolean isMaterialeDaConsuntivare(AttivitaSrvMateriale ordSrvMat)
   {
      //Considerati solo i materiali non coperti da garanzia
      boolean daConsiderare = true;
      char tipoGaranzia = ordSrvMat.getInGaranzia();
      if(tipoGaranzia == AttivitaSrvMateriale.GARANZIA_PRODUTTORE ||
         tipoGaranzia == AttivitaSrvMateriale.GARANZIA_TERZI)
         daConsiderare = false;
      return daConsiderare;
    }


   protected StoricoCommessa creaStoricoOrdSrvMateriale(OrdineServizio ordSrv, AttivitaSrvMateriale ordSrvRigMat) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());
      storicoCmm.setIdCommessa(ordSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(ordSrv.getCommessa().getLivelloCommessa());
      storicoCmm.setIdCommessaApp(ordSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(ordSrv.getCommessa().getIdCommessaPrincipale());
      if(ordSrvRigMat.getCommessa() != null && !ordSrvRigMat.getCommessa().equals(ordSrv.getCommessa()))
        storicoCmm.setIdCommessaCol(ordSrvRigMat.getIdCommessa());
      //33950 inizio
      //storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE); 
      if(ordSrv.getStatoOrdine() == OrdineEsecutivo.IMMESSO)
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.RICHIESTA);
      else
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);    
      //33950 fine
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_MATERIALE);

      storicoCmm.setIdAnnoOrigine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrigine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrigine(ordSrvRigMat.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrigine(ordSrvRigMat.getIdRigaMateriale());
      storicoCmm.setNumeroOrgFormattato(ordSrv.getNumeroOrdFmt());
      storicoCmm.setDataOrigine(ordSrv.getDataOrdine());

      storicoCmm.setIdAnnoOrdine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(ordSrvRigMat.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(ordSrvRigMat.getIdRigaMateriale());
      storicoCmm.setDataOrdine(ordSrv.getDataOrdine());
      storicoCmm.setIdCliente(ordSrv.getIdCliente());

      storicoCmm.setIdCauOrgTes(ordSrv.getIdCausaleOrdineServizio());
      storicoCmm.setAvanzamento(false);

      //Non specificato il tipo
      //storicoCmm.setAzioneMagazzino(AzioneMagazzino.USCITA);

      storicoCmm.setIdMagazzino(ordSrvRigMat.getIdMagazzinoPrl());
      storicoCmm.setIdArticolo(ordSrvRigMat.getIdArticolo());
      storicoCmm.setIdVersione(ordSrvRigMat.getIdVersione() != null ? ordSrvRigMat.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazione(ordSrvRigMat.getIdConfigurazione());
      storicoCmm.setDescrizioneArticolo(ordSrvRigMat.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

      //storicoCmm.setIdAttivita(ordSrv.getIdAttivita());
      storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
      //33143 inizio
      /*
      storicoCmm.setQuantitaUMPrm(ordSrvRigMat.getQtaResiduaUMPrm());
      storicoCmm.setQuantitaUMSec(ordSrvRigMat.getQtaResiduaUMSec());
      storicoCmm.setQuantitaUMAcqVen(ordSrvRigMat.getQtaResiduaUMVen());
      */
      BigDecimal resPrm = ordSrvRigMat.getQtaResiduaUMPrm();
      if(resPrm == null || resPrm.compareTo(new BigDecimal("0")) < 0) 
    	  resPrm=   new BigDecimal("0");
      storicoCmm.setQuantitaUMPrm(resPrm);

      BigDecimal resSec = ordSrvRigMat.getQtaResiduaUMSec();
      if(resSec == null || resSec.compareTo(new BigDecimal("0")) < 0) 
    	  resSec= new BigDecimal("0");
      storicoCmm.setQuantitaUMSec(resSec);
      
      BigDecimal resAcqVen = ordSrvRigMat.getQtaResiduaUMVen();
      if(resAcqVen == null || resAcqVen.compareTo(new BigDecimal("0")) < 0) 
    	  resAcqVen= new BigDecimal("0");
      storicoCmm.setQuantitaUMAcqVen(resAcqVen);
      //33143 fine

      storicoCmm.setIdUmPrmMag(ordSrvRigMat.getIdUMPrmMag());
      storicoCmm.setIdUmSecMag(ordSrvRigMat.getIdUMSecMag());
      storicoCmm.setIdUMAcqVen(ordSrvRigMat.getIdUMVen());
      
      //33143 inizio
      /*
      String idCmm = null;
      if(ordSrvRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
         idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

      //storicoCmm.setCostoUnitario(recuperaOrdSrvMatCostoUnitario(idCmm, ordSrvRigMat));//31460
      storicoCmm.setCostoUnitario(recuperaOrdSrvMatCostoUnitario(idCmm, ordSrvRigMat, storicoCmm));//31460

      if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null) 
    	  storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

      storicoCmm.setCostoUnitarioOrigine(ordSrvRigMat.getCostoRiferimento());
      storicoCmm.setGesSaldiCommessa(ordSrvRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
      storicoCmm.setValorizzaCosto(recuperaOrdSrvMatValorizzaCosto(storicoCmm));
       */
      //33143 fine
      storicoCmm.setNoFatturare(false);
      if(ordSrvRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null)
      {
         storicoCmm.setIdSchemaCosto(ordSrvRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
         storicoCmm.setIdComponenteCosto(ordSrvRigMat.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
      }

      storicoCmm.setTipoArticolo(ordSrvRigMat.getArticolo().getTipoArticolo());
      storicoCmm.setTipoParte(ordSrvRigMat.getArticolo().getTipoParte());
      storicoCmm.setIdGruppoProdotto(ordSrvRigMat.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
      storicoCmm.setIdClasseMerceologica(ordSrvRigMat.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
      storicoCmm.setIdClsMateriale(ordSrvRigMat.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
      storicoCmm.setIdPianificatore(ordSrvRigMat.getArticolo().getArticoloDatiPianif().getIdPianificatore());

      storicoCmm.setIdStabilimento(ordSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());

      //storicoCmm.setIdReparto(ordSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(ordSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(ordSrv.getIdCentroCosto());
      storicoCmm.setIdArticoloPrd(ordSrv.getIdArticolo());
      storicoCmm.setIdVersionePrd(ordSrv.getIdVersione() != null ? ordSrv.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazionePrd(ordSrv.getIdConfigurazione());
      //33143 inizio  
      String idCmm = null;
      if(ordSrvRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
         idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();

      //storicoCmm.setCostoUnitario(recuperaOrdSrvMatCostoUnitario(idCmm, ordSrvRigMat));//31460
      storicoCmm.setCostoUnitario(recuperaOrdSrvMatCostoUnitario(idCmm, ordSrvRigMat, storicoCmm));//31460

      if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null) {
    	  //storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));
    	  BigDecimal tmpCosTotale = storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm());
    	  storicoCmm.setCostoTotale(tmpCosTotale != null && tmpCosTotale.compareTo(new BigDecimal("0")) >= 0 ?
    			  tmpCosTotale : new BigDecimal("0"));
    	  
      }

      storicoCmm.setCostoUnitarioOrigine(ordSrvRigMat.getCostoRiferimento());
      storicoCmm.setGesSaldiCommessa(ordSrvRigMat.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
      storicoCmm.setValorizzaCosto(recuperaOrdSrvMatValorizzaCosto(storicoCmm));
      //33143 fine
      // fix 10913
      storicoCmm = completaDatiStoricoCmmMat(storicoCmm, ordSrvRigMat);
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

      /* //XX
      //imposto valori di costo e qta su ordin per calcoli di residuo su costi a percentuale
      if(isOrdineConSpesePercentuali())
      {
         ordSrvRigMat.setQtaRichiestaUMPrm(storicoCmm.getQuantitaUMPrm());
         ordSrvRigMat.setQtaRichiestaUMSec(storicoCmm.getQuantitaUMSec());
         ordSrvRigMat.setQtaRichiestaUMVen(storicoCmm.getQuantitaUMAcqVen());
         ordSrvRigMat.setCostoRiferimento(storicoCmm.getCostoUnitario());
      }
      */

      return storicoCmm;
   }

   protected BigDecimal recuperaOrdSrvMatCostoUnitario(String idCommessa, AttivitaSrvMateriale ordSrvRigMat) throws Exception
   {
      if(ordSrvRigMat.getCostoRiferimento() != null && ordSrvRigMat.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0)
         return ordSrvRigMat.getCostoRiferimento();
      else
         return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), ordSrvRigMat.getIdArticolo(), ordSrvRigMat.getIdVersione(), ordSrvRigMat.getIdConfigurazione());
   }
   
   //31460 inizio   
   protected BigDecimal recuperaOrdSrvMatCostoUnitario(String idCommessa, AttivitaSrvMateriale ordSrvRigMat, StoricoCommessa storicoCmm) throws Exception
   {
	   return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), ordSrvRigMat.getIdArticolo(), ordSrvRigMat.getIdVersione(), ordSrvRigMat.getIdConfigurazione(), ordSrvRigMat.getCostoRiferimento(), storicoCmm);
   }
   //31460 fine

   protected char recuperaOrdSrvMatValorizzaCosto(StoricoCommessa storicoCmm)
   {
      if(storicoCmm.getGesSaldiCommessa())
         return StoricoCommessa.NO;
      else
         return StoricoCommessa.INCREMENTA_COSTO;
    }


   //----------------------------------------------------
   //  FINE Elaborazione MATERIALI
   //----------------------------------------------------

   //----------------------------------------------------
   //  Elaborazione RISORSE
   //----------------------------------------------------
   protected boolean creaStoriciRisorse(OrdineServizio ordSrv) throws Exception
   {
      List risorseList = ordSrv.getRisorse();
      Iterator iterRsr = risorseList.iterator();
      while(iterRsr.hasNext())
      {
         getLogger().incNumRigheLette(1);
         AttivitaSrvRisorsa ordSrvRsr = (AttivitaSrvRisorsa)iterRsr.next();
         getLogger().incNumRigheFatte();
         getLogger().startTime();
         StoricoCommessa storicoCmm = creaStoricoOrdSrvRisorsa(ordSrv, ordSrvRsr);
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

   protected StoricoCommessa creaStoricoOrdSrvRisorsa(OrdineServizio ordSrv, AttivitaSrvRisorsa ordSrvRigRsr) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());
      storicoCmm.setIdCommessa(ordSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(ordSrv.getCommessa().getLivelloCommessa());
      storicoCmm.setIdCommessaApp(ordSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(ordSrv.getCommessa().getIdCommessaPrincipale());
      //33950 inizio
      //storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE); 
      if(ordSrv.getStatoOrdine() == OrdineEsecutivo.IMMESSO)
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.RICHIESTA);
      else
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);    
      //33950 fine
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_RISORSA);

      storicoCmm.setIdAnnoOrigine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrigine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrigine(ordSrvRigRsr.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrigine(ordSrvRigRsr.getIdRigaRisorsa());
      storicoCmm.setNumeroOrgFormattato(ordSrv.getNumeroOrdFmt());
      storicoCmm.setDataOrigine(ordSrv.getDataOrdine());

      storicoCmm.setIdAnnoOrdine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(ordSrvRigRsr.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(ordSrvRigRsr.getIdRigaRisorsa());
      storicoCmm.setDataOrdine(ordSrv.getDataOrdine());
      storicoCmm.setIdCliente(ordSrv.getIdCliente());

      //storicoCmm.setAvanzamento(true);
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.NESSUNA_AZIONE);
      storicoCmm.setIdArticolo(ordSrv.getIdArticolo());
      storicoCmm.setIdVersione(ordSrv.getIdVersione() != null ? ordSrv.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazione(ordSrv.getIdConfigurazione());
      storicoCmm.setDescrizioneArticolo(ordSrv.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

      if(ordSrvRigRsr.getAttivitaServizio() != null)
         storicoCmm.setIdOperazione(ordSrvRigRsr.getAttivitaServizio().getIdOperazione());

      //storicoCmm.setIdDipendente(ordSrv.getIdDipendente());

      storicoCmm.setTipoRisorsa(ordSrvRigRsr.getTipoRisorsa());
      storicoCmm.setLivelloRisorsa(ordSrvRigRsr.getLivelloRisorsa());
      storicoCmm.setIdRisorsa(ordSrvRigRsr.getIdRisorsa());
      storicoCmm.setTipoRilevazioneRsr(ordSrvRigRsr.getRisorsa().getTipoRilevazione());
      //33143 inizio
      /*
      storicoCmm.setQuantitaUMPrm(ordSrvRigRsr.getQtaResiduaUMPrm());
      storicoCmm.setQuantitaUMAcqVen(ordSrvRigRsr.getQtaResiduaUMVen());
      */
      BigDecimal resPrm = ordSrvRigRsr.getQtaResiduaUMPrm();
      if(resPrm == null || resPrm.compareTo(new BigDecimal("0")) < 0) 
    	  resPrm=   new BigDecimal("0");
      storicoCmm.setQuantitaUMPrm(resPrm);
      
      BigDecimal resAcqVen = ordSrvRigRsr.getQtaResiduaUMVen();
      if(resAcqVen == null || resAcqVen.compareTo(new BigDecimal("0")) < 0) 
    	  resAcqVen= new BigDecimal("0");
      storicoCmm.setQuantitaUMAcqVen(resAcqVen);
      //33143 fine
      //storicoCmm.setQtaScarto(ordSrvRigRsr.getQuantitaScarto());
      storicoCmm.setTempo(ordSrvRigRsr.getOreResidue());
      if(storicoCmm.getTempo() == null)
         storicoCmm.setTempo(new BigDecimal("0"));
      storicoCmm.setIdUmPrmMag(ordSrvRigRsr.getIdUMPrmMag());
      storicoCmm.setIdUMAcqVen(ordSrvRigRsr.getIdUMVen());
      //31460 inizio
      /*
      storicoCmm.setCostoUnitario(recuperaOrdSrvRsrCostoUnitario(ordSrvRigRsr, storicoCmm.getQuantitaUMPrm(), storicoCmm.getQtaScarto()));
      storicoCmm.setCostoUnitarioOrigine(ordSrvRigRsr.getCostoRiferimento());
      storicoCmm.setCostoTotale(recuperaOrdSrvRsrCostoTotale(ordSrvRigRsr));
      */
      //33143 inizio
      /*
      valorizzaCostoOrdSrvRsr(ordSrvRigRsr,storicoCmm);
      //31460 fine
      storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
      */
      //33143 fine
      storicoCmm.setGesSaldiCommessa(false);
      storicoCmm.setNoFatturare(false);
      //Fix 34138 --inizio      
      /*if(ordSrvRigRsr.getRisorsa() != null)
      {
         storicoCmm.setIdSchemaCosto(ordSrvRigRsr.getRisorsa().getIdSchemaCosto());
         storicoCmm.setIdComponenteCosto(ordSrvRigRsr.getRisorsa().getIdComponenteCosto());
         //Fix 33081 --inizio
         if(ordSrvRigRsr.getRisorsa().getLivelloRisorsa() == Risorsa.MATRICOLA && storicoCmm.getIdSchemaCosto() == null)
         {
            Risorsa rapp = ordSrvRigRsr.getRisorsa().getRisorsaAppart();
            if(rapp != null)
            {
               storicoCmm.setIdSchemaCosto(rapp.getIdSchemaCosto());
               storicoCmm.setIdComponenteCosto(rapp.getIdComponenteCosto());
            }
         }
         //Fix 33081 --fine 
      }*/
      trovaSchemaCostoRisorsa(storicoCmm, ordSrvRigRsr.getAttivitaServizio().getAttivita(), ordSrvRigRsr.getRisorsa());
      //Fix 34138 --fine
      
      storicoCmm.setIdStabilimento(ordSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());

      //storicoCmm.setIdReparto(ordSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(ordSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(ordSrv.getIdCentroCosto());

      storicoCmm.setIdArticoloPrd(ordSrv.getIdArticolo());
      storicoCmm.setIdVersionePrd(ordSrv.getIdVersione());
      storicoCmm.setIdConfigurazionePrd(ordSrv.getIdConfigurazione());
      //33143 inizio
      valorizzaCostoOrdSrvRsr(ordSrvRigRsr,storicoCmm);
      storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
      //33143 fine
      // fix 10913
      storicoCmm = completaDatiStoricoCmmRsr(storicoCmm, ordSrvRigRsr);
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

      //imposto valori di costo e qta su ordin per calcoli di residuo su costi a percentuale
      /*//XX
      if(isOrdineConSpesePercentuali())
      {
         ordSrvRigRsr.setQtaRichiestaUMPrm(storicoCmm.getQuantitaUMPrm());
         ordSrvRigRsr.setQtaRichiestaUMVen(storicoCmm.getQuantitaUMAcqVen());
         ordSrvRigRsr.setOreRichieste(storicoCmm.getTempo());
         ordSrvRigRsr.setCostoRiferimento(storicoCmm.getCostoUnitario());
      }
      */

      return storicoCmm;
    }


   protected BigDecimal recuperaOrdSrvRsrCostoUnitario(AttivitaSrvRisorsa ordSrvRigRsr, BigDecimal qtaPrmMag, BigDecimal qtaScarta) throws Exception
   {
      /*
      if(ordSrvRigRsr.getRisorsa().getTipoRilevazione() == Risorsa.COSTO)
         return ordSrvRigRsr.getCostoRilevato();
      else
      */
         return recuperaOrdSrvRsrCostoRif(ordSrvRigRsr);
    }

   protected BigDecimal recuperaOrdSrvRsrCostoRif(AttivitaSrvRisorsa ordSrvRigRsr) throws Exception
   {
      //Fix 27486 inizio
      if(iConsunCmm.isCostiRisorsaDaDocumento())
      {
         return ordSrvRigRsr.getCostoRiferimento();
      }
      else
      {
         if(ordSrvRigRsr.getCostoRiferimento() != null && ordSrvRigRsr.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0)
         {
            return ordSrvRigRsr.getCostoRiferimento();
         }
         else
         {
            BigDecimal costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(), ordSrvRigRsr.getTipoRisorsa(), ordSrvRigRsr.getLivelloRisorsa(), ordSrvRigRsr.getIdRisorsa());
            if(costoRif == null || costoRif.compareTo(new BigDecimal("0")) == 0)
            {
               if(ordSrvRigRsr.getRisorsa().getRisorsaAppart() != null && ordSrvRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa() == Risorsa.RISORSA)
                  costoRif =
                  recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(),
                        ordSrvRigRsr.getRisorsa().getRisorsaAppart().getTipoRisorsa(),
                        ordSrvRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa(),
                        ordSrvRigRsr.getRisorsa().getRisorsaAppart().getIdRisorsa());
            }
            return costoRif;

         }
      }
      //Fix 27486 fine
   }

   protected BigDecimal recuperaOrdSrvRsrCostoTotale(AttivitaSrvRisorsa ordSrvRigRsr) throws Exception {

      BigDecimal ret = new BigDecimal("0");

      if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.COSTO && ordSrvRigRsr.isCostoFisso()) {
        BigDecimal costoPrevisto = ordSrvRigRsr.getCostoPrevisto() != null ? ordSrvRigRsr.getCostoPrevisto() : new BigDecimal("0");
        BigDecimal costoRilevato = ordSrvRigRsr.getCostoRilevato() != null ? ordSrvRigRsr.getCostoRilevato() : new BigDecimal("0");
        ret = costoPrevisto.add(costoRilevato.negate());
      }
      else {
        BigDecimal costoRif = recuperaOrdSrvRsrCostoRif(ordSrvRigRsr);

        if(costoRif == null)
          return null;

        if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.TEMPO) {
          BigDecimal oreRichiesta = ordSrvRigRsr.getOreRichieste() != null ? ordSrvRigRsr.getOreRichieste() : new BigDecimal("0");
          BigDecimal oreRilevate = ordSrvRigRsr.getOreRilevate() != null ? ordSrvRigRsr.getOreRilevate() : new BigDecimal("0");
          ret = costoRif.multiply(oreRichiesta.add(oreRilevate.negate()));
        }
        else if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.COSTO && ordSrvRigRsr.isCostoFisso()) {
          BigDecimal costoPrevisto = ordSrvRigRsr.getCostoPrevisto() != null ? ordSrvRigRsr.getCostoPrevisto() : new BigDecimal("0");
          BigDecimal costoRilevato = ordSrvRigRsr.getCostoRilevato() != null ? ordSrvRigRsr.getCostoRilevato() : new BigDecimal("0");
          ret = costoPrevisto.add(costoRilevato.negate());
        }
        else if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.COSTO && !ordSrvRigRsr.isCostoFisso()) {
          if(ordSrvRigRsr.getAttivitaServizio().getQtaRichiestaUMPrm() != null)
            ret = costoRif.multiply(ordSrvRigRsr.getAttivitaServizio().getQtaRichiestaUMPrm());
          if(ordSrvRigRsr.getCostoRilevato() != null)
            ret = ret.add(ordSrvRigRsr.getCostoRilevato().negate());
        }
      }

      if(ret.compareTo(new BigDecimal("0")) < 0)
        return new BigDecimal("0");
      return ret;
    }



   //----------------------------------------------------
   //  FINE Elaborazione RISORSE
   //----------------------------------------------------

   //----------------------------------------------------
   //  Elaborazione SPESE
   //----------------------------------------------------
   protected boolean creaStoriciSpese(OrdineServizio ordSrv) throws Exception
   {
      List spese = new ArrayList();
      //XX List spesePercentuali = new ArrayList();

      Iterator iterAtt = ordSrv.getAttivitaServizi().iterator();
      while(iterAtt.hasNext())
      {
         AttivitaServizio atv = (AttivitaServizio) iterAtt.next();
         spese.addAll(atv.getSpese());
      }

      //Processo righe spesa non percentuali
      Iterator iterSpese = spese.iterator();
      while(iterSpese.hasNext())
      {
         //Considerate solo le spese con classe merciologica per servizi non abbiamo errore bloccante ma solo warning???
         getLogger().incNumRigheLette(1);
         AttivitaSrvSpesa ordSrvSpesa = (AttivitaSrvSpesa)iterSpese.next();
         if(isRigaSpesaDaProcessare(ordSrvSpesa))
         {
            if(!ordSrvSpesa.isSpesaPercentuale())
            {
               boolean good = processaSingolaSpesa(ordSrv, ordSrvSpesa);
               if(!good)
                  return false;
            }
            /* XX
            else
               spesePercentuali.add(ordSrvSpesa);
            */
         }
      }
      return true;

      //Processo righe spesa percentuali
      /*//XX
      if(isOrdineConSpesePercentuali() && !spesePercentuali.isEmpty())
      {
         ValorizzatoreImportiOrdineServizio valorizzatore = (ValorizzatoreImportiOrdineServizio)Factory.createObject(ValorizzatoreImportiOrdineServizio.class);
         valorizzatore.calcolaTotaliImporti(ordSrv);
         Hashtable importiSingoleEntita = valorizzatore.getImportiEntita();

         Iterator percIter = spesePercentuali.iterator();
         while(percIter.hasNext())
         {
            AttivitaSrvSpesa ordSrvSpesa = (AttivitaSrvSpesa)percIter.next();
            ImportiEntitaOrdSrv importiSps = (ImportiEntitaOrdSrv)importiSingoleEntita.get(ImportiEntitaOrdSrv.getKey(ordSrvSpesa));
            if(importiSps != null)
            {
               ordSrvSpesa.setImportoSpesa(importiSps.getCostoPrevisto());
               boolean good = processaSingolaSpesa(ordSrv, ordSrvSpesa);
               if(!good)
                  return false;
            }
         }
      }
      return true;
      */
   }

   protected boolean processaSingolaSpesa(OrdineServizio ordSrv, AttivitaSrvSpesa ordSrvSpesa)  throws Exception
   {
      getLogger().incNumRigheFatte();
      getLogger().startTime();
      StoricoCommessa storicoCmm = creaStoricoOrdSrvSpesa(ordSrv, ordSrvSpesa);
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
      return true;
   }

   protected boolean isRigaSpesaDaProcessare(AttivitaSrvSpesa ordSrvSpesa)
   {
      //Eliminate le spese percentuali perchè la consuntiazione sarebbe fatta a costi teorici e mai effettivi e
      //quindi non serv a niente
      if(!ordSrvSpesa.isSpesaPercentuale())
      {
         Spesa spesa = ordSrvSpesa.getSpesa();
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


   protected StoricoCommessa creaStoricoOrdSrvSpesa(OrdineServizio ordSrv, AttivitaSrvSpesa ordSrvRigSps) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);
      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());

      storicoCmm.setIdCommessa(ordSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(ordSrv.getCommessa().getLivelloCommessa());

      storicoCmm.setIdCommessaApp(ordSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(ordSrv.getCommessa().getIdCommessaPrincipale());
      //33950 inizio
      //storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE); 
      if(ordSrv.getStatoOrdine() == OrdineEsecutivo.IMMESSO)
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.RICHIESTA);
      else
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);    
      //33950 fine
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_SPESA);

      storicoCmm.setIdAnnoOrigine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrigine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrigine(ordSrvRigSps.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrigine(ordSrvRigSps.getIdRigaSpesa());
      storicoCmm.setNumeroOrgFormattato(ordSrv.getNumeroOrdFmt());
      storicoCmm.setDataOrigine(ordSrv.getDataOrdine());

      storicoCmm.setIdAnnoOrdine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(ordSrvRigSps.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(ordSrvRigSps.getIdRigaSpesa());
      storicoCmm.setDataOrdine(ordSrv.getDataOrdine());
      storicoCmm.setIdCliente(ordSrv.getIdCliente());

      storicoCmm.setAvanzamento(false);
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.NESSUNA_AZIONE);

      Spesa spesa = ordSrvRigSps.getSpesa();
      Articolo art = spesa.getArticolo();
      storicoCmm.setIdArticolo(art.getIdArticolo());
      storicoCmm.setIdVersione(new Integer("1"));
      storicoCmm.setIdConfigurazione(null);
      storicoCmm.setDescrizioneArticolo(art.getDescrizioneArticoloNLS().getDescrizione());
      ClasseMerceologica cmArt = art.getArticoloDatiProduz().getClasseMerclg();
      storicoCmm.setIdSchemaCosto(cmArt.getIdSchemaCosto());
      storicoCmm.setIdComponenteCosto(cmArt.getIdComponenteCosto());

      //storicoCmm.setIdAttivita(ordSrv.getIdAttivita());
      if(ordSrvRigSps.getAttivitaServizio() != null) {
         storicoCmm.setIdOperazione(ordSrvRigSps.getAttivitaServizio().getIdOperazione());
      }

      //storicoCmm.setIdDipendente(ordSrv.getIdDipendente());
      storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
      storicoCmm.setQuantitaUMPrm(ordSrvRigSps.getQtaUMPrm());
      storicoCmm.setQuantitaUMAcqVen(ordSrvRigSps.getQtaUMVen());
      if((storicoCmm.getQuantitaUMPrm() == null) || (Utils.compare(storicoCmm.getQuantitaUMPrm(), new BigDecimal("0")) == 0))
         storicoCmm.setQuantitaUMPrm(storicoCmm.getQuantitaUMAcqVen());

      storicoCmm.setIdUmPrmMag(ordSrvRigSps.getIdUMPrmMag());
      storicoCmm.setIdUMAcqVen(ordSrvRigSps.getIdUMVen());
      if(storicoCmm.getIdUmPrmMag() == null)
         storicoCmm.setIdUmPrmMag(storicoCmm.getIdUMAcqVen());
      //33143 inizio
      /*
      storicoCmm.setCostoUnitario(recuperaOrdSrvSpsCostoUnitario(ordSrvRigSps, storicoCmm.getQuantitaUMPrm(), storicoCmm.getQtaScarto()));
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario());
      storicoCmm.setValoreRiga(storicoCmm.getCostoUnitario());
      storicoCmm.setCostoUnitarioOrigine(storicoCmm.getCostoUnitario());
      storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
      */
      //33143 fine
      storicoCmm.setGesSaldiCommessa(false);
      storicoCmm.setNoFatturare(false);

      storicoCmm.setIdStabilimento(ordSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
      //storicoCmm.setIdReparto(ordSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(ordSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(ordSrv.getIdCentroCosto());

      storicoCmm.setIdArticoloPrd(ordSrv.getIdArticolo());
      storicoCmm.setIdVersionePrd(ordSrv.getIdVersione());
      storicoCmm.setIdConfigurazionePrd(ordSrv.getIdConfigurazione());
      //33143 inizio
      storicoCmm.setCostoUnitario(recuperaOrdSrvSpsCostoUnitario(ordSrvRigSps, storicoCmm.getQuantitaUMPrm(), storicoCmm.getQtaScarto()));
      storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario());
      storicoCmm.setValoreRiga(storicoCmm.getCostoUnitario());
      storicoCmm.setCostoUnitarioOrigine(storicoCmm.getCostoUnitario());
      storicoCmm.setValorizzaCosto(StoricoCommessa.INCREMENTA_COSTO);
      //33143 fine
      // fix 10913
      storicoCmm = completaDatiStoricoCmmSpe(storicoCmm, ordSrvRigSps);
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

      //imposto valori di costo e qta su ordin per calcoli di residuo su costi a percentuale
      /*//XX
      if(isOrdineConSpesePercentuali())
      {
         ordSrvRigSps.setImportoSpesa(storicoCmm.getCostoUnitario());
      }
      */
      return storicoCmm;

   }

   protected BigDecimal recuperaOrdSrvSpsCostoUnitario(AttivitaSrvSpesa ordSrvRigSps, BigDecimal qtaUmPrm, BigDecimal qtaScarto)
   {
      BigDecimal impSpesa = new BigDecimal("0");
      if(!ordSrvRigSps.isSpesaPercentuale())
      {
         BigDecimal previsto = ordSrvRigSps.getImportoSpesa();
         if(previsto == null )
            previsto = new BigDecimal("0");
         BigDecimal rilevato = ordSrvRigSps.getImportoSpesaRilevata();
         if(rilevato == null )
            rilevato = new BigDecimal("0");

         if(previsto.compareTo(rilevato) > 0)
            impSpesa = previsto.add(rilevato.negate());
      }
      else
         impSpesa = ordSrvRigSps.getImportoSpesa(); //Assegnato dopo calcolo
      return impSpesa;
   }

   //----------------------------------------------------
   //  FINE Elaborazione SPESE
   //----------------------------------------------------

   //----------------------------------------------------
   //  Elaborazione VERSAMENTI
   //----------------------------------------------------
   protected boolean creaStoriciVersamenti(OrdineServizio ordSrv) throws Exception
   {
      List versamenti = new ArrayList();
      Iterator iterAtt = ordSrv.getAttivitaServizi().iterator();
      while(iterAtt.hasNext())
      {
         AttivitaServizio atv = (AttivitaServizio) iterAtt.next();
         versamenti.addAll(atv.getProdotti());
      }

      Iterator iterVer = versamenti.iterator();
      getLogger().incNumRigheLette(versamenti.size());
      while(iterVer.hasNext())
      {
         AttivitaSrvProdotto ordSrvVrs = (AttivitaSrvProdotto)iterVer.next();
         if(isRigaVersamentoDaProcessare(ordSrvVrs))
         {
            getLogger().incNumRigheFatte();
            getLogger().startTime();
            StoricoCommessa storicoCmm = creaStoricoOrdSrvVersamento(ordSrv, ordSrvVrs);
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

   protected boolean isRigaVersamentoDaProcessare(AttivitaSrvProdotto ordSrvVrs)
   {
      boolean daProcessare = true;
      //Eseguo test anche su Prodotto primario (altre scelte) anche se per servizi non è previsto
      if(ordSrvVrs.getTipoProdotto() == AttivitaSrvProdotto.PRODOTTO_PRIMARIO ||
         ordSrvVrs.getTipoProdotto() == '1')
         daProcessare = false;
      return daProcessare;
   }

   protected StoricoCommessa creaStoricoOrdSrvVersamento(OrdineServizio ordSrv, AttivitaSrvProdotto ordSrvRigVrs) throws Exception
   {
      StoricoCommessa storicoCmm = (StoricoCommessa)Factory.createObject(StoricoCommessa.class);

      storicoCmm.setIdAzienda(iConsunCmm.getIdAzienda());
      storicoCmm.setIdProgressivo(getNextIdProgressivo());
      storicoCmm.setIdCommessa(ordSrv.getIdCommessa());
      storicoCmm.setLivelloCommessa(ordSrv.getCommessa().getLivelloCommessa());

      storicoCmm.setIdCommessaApp(ordSrv.getCommessa().getIdCommessaAppartenenza());
      storicoCmm.setIdCommessaPrm(ordSrv.getCommessa().getIdCommessaPrincipale());
      if(ordSrvRigVrs.getCommessa() != null && !ordSrvRigVrs.getCommessa().equals(ordSrv.getCommessa()))
         storicoCmm.setIdCommessaCol(ordSrvRigVrs.getIdCommessa());
      //33950 inizio
      //storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE); 
      if(ordSrv.getStatoOrdine() == OrdineEsecutivo.IMMESSO)
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.RICHIESTA);
      else
      	storicoCmm.setDocumentoOrigine(StoricoCommessa.ORDINE);    
      //33950 fine
      storicoCmm.setTipoRigaOrigine(StoricoCommessa.SERVIZIO_PRODOTTO);

      storicoCmm.setIdAnnoOrigine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrigine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrigine(ordSrvRigVrs.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrigine(ordSrvRigVrs.getIdRigaProdotto());
      storicoCmm.setNumeroOrgFormattato(ordSrv.getNumeroOrdFmt());
      storicoCmm.setDataOrigine(ordSrv.getDataOrdine());

      storicoCmm.setIdAnnoOrdine(ordSrv.getIdAnnoOrdine());
      storicoCmm.setIdNumeroOrdine(ordSrv.getIdNumeroOrdine());
      storicoCmm.setIdRigaOrdine(ordSrvRigVrs.getIdRigaAttivita());
      storicoCmm.setIdDetRigaOrdine(ordSrvRigVrs.getIdRigaProdotto());
      storicoCmm.setDataOrdine(ordSrv.getDataOrdine());
      storicoCmm.setIdCliente(ordSrv.getIdCliente());

      storicoCmm.setAvanzamento(false);
//      storicoCmm.setIdCauMagazzino(ordSrvRigVrs.getIdCausaleMagazzinoVrs());
      storicoCmm.setAzioneMagazzino(AzioneMagazzino.ENTRATA);
      storicoCmm.setIdMagazzino(ordSrvRigVrs.getIdMagazzinoVrs());
      storicoCmm.setIdArticolo(ordSrvRigVrs.getIdArticolo());
      storicoCmm.setIdVersione(ordSrvRigVrs.getIdVersione() != null ? ordSrvRigVrs.getIdVersione() : new Integer("1"));
      storicoCmm.setIdConfigurazione(ordSrvRigVrs.getIdConfigurazione());
      storicoCmm.setDescrizioneArticolo(ordSrvRigVrs.getArticolo().getDescrizioneArticoloNLS().getDescrizione());

      //storicoCmm.setIdAttivita(ordSrv.getIdAttivita());
      //storicoCmm.setIdDipendente(ordSrv.getIdDipendente());
      storicoCmm.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      storicoCmm.setTipoRilevazioneRsr(Risorsa.TEMPO);
      //33143 inizio
      /*
      storicoCmm.setQuantitaUMPrm(ordSrvRigVrs.getQtaResiduaUMPrm());
      storicoCmm.setQuantitaUMSec(ordSrvRigVrs.getQtaResiduaUMSec());
      storicoCmm.setQuantitaUMAcqVen(ordSrvRigVrs.getQtaResiduaUMVen());
      */
      BigDecimal resPrm = ordSrvRigVrs.getQtaResiduaUMPrm();
      if(resPrm == null || resPrm.compareTo(new BigDecimal("0")) < 0) 
    	  resPrm=   new BigDecimal("0");
      storicoCmm.setQuantitaUMPrm(resPrm);

      BigDecimal resSec = ordSrvRigVrs.getQtaResiduaUMSec();
      if(resSec == null || resSec.compareTo(new BigDecimal("0")) < 0) 
    	  resSec= new BigDecimal("0");
      storicoCmm.setQuantitaUMSec(resSec);
      
      BigDecimal resAcqVen = ordSrvRigVrs.getQtaResiduaUMVen();
      if(resAcqVen == null || resAcqVen.compareTo(new BigDecimal("0")) < 0) 
    	  resAcqVen= new BigDecimal("0");
      storicoCmm.setQuantitaUMAcqVen(resAcqVen);
      //33143 fine

      storicoCmm.setIdUmPrmMag(ordSrvRigVrs.getIdUMPrmMag());
      storicoCmm.setIdUmSecMag(ordSrvRigVrs.getIdUMSecMag());
      storicoCmm.setIdUMAcqVen(ordSrvRigVrs.getIdUMVen());

      String idCmm = null;
      if(ordSrvRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa())
         idCmm = storicoCmm.getIdCommessaCol() != null ? storicoCmm.getIdCommessaCol() : storicoCmm.getIdCommessa();
       //33143 inizio
       /*
      //storicoCmm.setCostoUnitario(recuperaOrdSrvVrsCostoUnitario(idCmm, ordSrvRigVrs));//31460
      storicoCmm.setCostoUnitario(recuperaOrdSrvVrsCostoUnitario(idCmm, ordSrvRigVrs, storicoCmm));//31460
      
      if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null) 
    	  storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));

      storicoCmm.setCostoUnitarioOrigine(ordSrvRigVrs.getCostoRiferimento());
      storicoCmm.setGesSaldiCommessa(ordSrvRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
      storicoCmm.setValorizzaCosto(recuperaOrdSrvVrsValorizzaCosto(ordSrvRigVrs.getTipoProdotto(), storicoCmm.getGesSaldiCommessa()));
      */
      //33143 fine
      storicoCmm.setNoFatturare(false);

      if(ordSrvRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg() != null)
      {
         storicoCmm.setIdSchemaCosto(ordSrvRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdSchemaCosto());
         storicoCmm.setIdComponenteCosto(ordSrvRigVrs.getArticolo().getArticoloDatiProduz().getClasseMerclg().getIdComponenteCosto());
      }

      storicoCmm.setTipoArticolo(ordSrvRigVrs.getArticolo().getTipoArticolo());
      storicoCmm.setTipoParte(ordSrvRigVrs.getArticolo().getTipoParte());
      storicoCmm.setIdGruppoProdotto(ordSrvRigVrs.getArticolo().getArticoloDatiIdent().getIdGruppoProdotto());
      storicoCmm.setIdClasseMerceologica(ordSrvRigVrs.getArticolo().getArticoloDatiProduz().getIdClasseMerclg());
      storicoCmm.setIdClsMateriale(ordSrvRigVrs.getArticolo().getArticoloDatiIdent().getIdClasseMateriale());
      storicoCmm.setIdPianificatore(ordSrvRigVrs.getArticolo().getArticoloDatiPianif().getIdPianificatore());

      storicoCmm.setIdStabilimento(ordSrv.getIdStabilimento());
      if((storicoCmm.getIdStabilimento() == null) && (storicoCmm.getCommessa() != null))
         storicoCmm.setIdStabilimento(storicoCmm.getCommessa().getIdStabilimento());
//      storicoCmm.setIdReparto(ordSrv.getIdReparto());
      storicoCmm.setIdCentroLavoro(ordSrv.getIdCentroLavoro());
      storicoCmm.setIdCentroCosto(ordSrv.getIdCentroCosto());

      if(!ordSrvRigVrs.getArticolo().equals(ordSrv.getArticolo()))
      {
         storicoCmm.setIdArticoloPrd(ordSrvRigVrs.getIdArticolo());
         storicoCmm.setIdVersionePrd(ordSrvRigVrs.getIdVersione() != null ? ordSrvRigVrs.getIdVersione() : new Integer("1"));
         storicoCmm.setIdConfigurazionePrd(ordSrvRigVrs.getIdConfigurazione());
      }
      //33143 inizio
     //storicoCmm.setCostoUnitario(recuperaOrdSrvVrsCostoUnitario(idCmm, ordSrvRigVrs));//31460
     storicoCmm.setCostoUnitario(recuperaOrdSrvVrsCostoUnitario(idCmm, ordSrvRigVrs, storicoCmm));//31460
     
     if(storicoCmm.getCostoUnitario() != null && storicoCmm.getQuantitaUMPrm() != null) {
   	  //storicoCmm.setCostoTotale(storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm()));
   	  BigDecimal tmpCosTotale = storicoCmm.getCostoUnitario().multiply(storicoCmm.getQuantitaUMPrm());
   	  storicoCmm.setCostoTotale(tmpCosTotale != null && tmpCosTotale.compareTo(new BigDecimal("0")) >= 0 ?
   			  tmpCosTotale : new BigDecimal("0"));
     }
     
     storicoCmm.setCostoUnitarioOrigine(ordSrvRigVrs.getCostoRiferimento());
     storicoCmm.setGesSaldiCommessa(ordSrvRigVrs.getArticolo().getArticoloDatiMagaz().isGesSaldiCommessa());
     storicoCmm.setValorizzaCosto(recuperaOrdSrvVrsValorizzaCosto(ordSrvRigVrs.getTipoProdotto(), storicoCmm.getGesSaldiCommessa()));
     //33143 fine
      // fix 10913
      storicoCmm = completaDatiStoricoCmmVrs(storicoCmm, ordSrvRigVrs);
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

      //imposto valori di costo e qta su ordin per calcoli di residuo su costi a percentuale
      /*//XX
      if(isOrdineConSpesePercentuali())
      {
         ordSrvRigVrs.setQtaRichiestaUMPrm(storicoCmm.getQuantitaUMPrm());
         ordSrvRigVrs.setQtaRichiestaUMSec(storicoCmm.getQuantitaUMSec());
         ordSrvRigVrs.setQtaRichiestaUMVen(storicoCmm.getQuantitaUMAcqVen());
         ordSrvRigVrs.setCostoRiferimento(storicoCmm.getCostoUnitario());
      }
      */
      return storicoCmm;
   }

   protected BigDecimal recuperaOrdSrvVrsCostoUnitario(String idCommessa, AttivitaSrvProdotto ordSrvRigVrs) throws Exception
   {
      if(ordSrvRigVrs.getCostoRiferimento() != null && ordSrvRigVrs.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0)
         return ordSrvRigVrs.getCostoRiferimento();
      else
         return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), ordSrvRigVrs.getIdArticolo(), ordSrvRigVrs.getIdVersione(), ordSrvRigVrs.getIdConfigurazione());
   }

   //31460 inzio   
   protected BigDecimal recuperaOrdSrvVrsCostoUnitario(String idCommessa, AttivitaSrvProdotto ordSrvRigVrs, StoricoCommessa storicoCmm) throws Exception
   {
	   return recuperoArticoloCostoUnitario(idCommessa, iCompactCmm.getIdStabilimento(), ordSrvRigVrs.getIdArticolo(), ordSrvRigVrs.getIdVersione(), ordSrvRigVrs.getIdConfigurazione(), ordSrvRigVrs.getCostoRiferimento(), storicoCmm);
   }
   
   protected void valorizzaCostoOrdSrvRsr(AttivitaSrvRisorsa ordSrvRigRsr, StoricoCommessa storicoCmm) throws Exception {
	   BigDecimal tmpCosTotale = new BigDecimal("0");
	   BigDecimal tmpCosUnitario = new BigDecimal("0");
	   BigDecimal tmpCosUnitarioOrig = new BigDecimal("0");
	   if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.COSTO && ordSrvRigRsr.isCostoFisso()) {
		   BigDecimal costoPrevisto = ordSrvRigRsr.getCostoPrevisto() != null ? ordSrvRigRsr.getCostoPrevisto() : new BigDecimal("0");
		   BigDecimal costoRilevato = ordSrvRigRsr.getCostoRilevato() != null ? ordSrvRigRsr.getCostoRilevato() : new BigDecimal("0");
		   tmpCosTotale = costoPrevisto.add(costoRilevato.negate());
		   tmpCosUnitario = tmpCosTotale;
		   tmpCosUnitarioOrig = tmpCosUnitario;
		   storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
	   }
	   else if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.COSTO && !ordSrvRigRsr.isCostoFisso()) {
		   BigDecimal costoRif = recuperoOrdSrvRsrCostoRif(ordSrvRigRsr, storicoCmm);
		   if(costoRif == null){
			   tmpCosTotale = null;
			   tmpCosUnitario = null; 
			   tmpCosUnitarioOrig = null;
			   storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
		   }
		   else{			  
			   if(ordSrvRigRsr.getAttivitaServizio().getQtaRichiestaUMPrm() != null){
				   tmpCosTotale = costoRif.multiply(ordSrvRigRsr.getAttivitaServizio().getQtaRichiestaUMPrm());
			   }
			   else{
				   tmpCosTotale = costoRif;
			   }
				  
			   if(ordSrvRigRsr.getCostoRilevato() != null){
				   tmpCosTotale = tmpCosTotale.add(ordSrvRigRsr.getCostoRilevato().negate());
			   }
				  
			   if(tmpCosTotale != null && ordSrvRigRsr.getAttivitaServizio().getQtaRichiestaUMPrm() != null
					   && ordSrvRigRsr.getAttivitaServizio().getQtaRichiestaUMPrm().compareTo(new BigDecimal("0")) != 0){
				   tmpCosUnitario = tmpCosTotale.divide(ordSrvRigRsr.getAttivitaServizio().getQtaRichiestaUMPrm(), BigDecimal.ROUND_HALF_UP);
				   tmpCosUnitarioOrig = tmpCosUnitario;
			   }
			   else{
				   tmpCosUnitario = costoRif;
				   tmpCosUnitarioOrig = ordSrvRigRsr.getCostoRiferimento();  
			   }	
		   }
	   }
	   else{
		   tmpCosUnitario = recuperoOrdSrvRsrCostoRif(ordSrvRigRsr, storicoCmm);
		   tmpCosUnitarioOrig = ordSrvRigRsr.getCostoRiferimento();
		   if(tmpCosUnitario != null){
			   if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.TEMPO) {
				   BigDecimal oreRichiesta = ordSrvRigRsr.getOreRichieste() != null ? ordSrvRigRsr.getOreRichieste() : new BigDecimal("0");
				   BigDecimal oreRilevate = ordSrvRigRsr.getOreRilevate() != null ? ordSrvRigRsr.getOreRilevate() : new BigDecimal("0");
				   tmpCosTotale = tmpCosUnitario.multiply(oreRichiesta.add(oreRilevate.negate()));
			   }
			   else if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.QUANTITA) {
				   if(ordSrvRigRsr.getAttivitaServizio().getQtaResiduaUMPrm() != null)
					   tmpCosTotale = tmpCosUnitario.multiply(ordSrvRigRsr.getAttivitaServizio().getQtaResiduaUMPrm());
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
	  
   protected BigDecimal recuperoOrdSrvRsrCostoRif(AttivitaSrvRisorsa ordSrvRigRsr, StoricoCommessa storicoCmm) throws Exception {
	   if(ordSrvRigRsr.getTipoRilevazione() == Risorsa.COSTO){
		   if(ordSrvRigRsr.getCostoRiferimento() != null && ordSrvRigRsr.getCostoRiferimento().compareTo(new BigDecimal("0")) != 0) {
			   storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
			   return ordSrvRigRsr.getCostoRiferimento();
		   }
		   else {
			   BigDecimal costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(), ordSrvRigRsr.getTipoRisorsa(), ordSrvRigRsr.getLivelloRisorsa(), ordSrvRigRsr.getIdRisorsa());
			   if(costoRif == null || costoRif.compareTo(new BigDecimal("0")) == 0) {
				   if(ordSrvRigRsr.getRisorsa().getRisorsaAppart() != null && ordSrvRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa() == Risorsa.RISORSA)
					   costoRif = recuperoRisorsaCostoUnitario(iCompactCmm.getIdStabilimento(),
							   								   ordSrvRigRsr.getRisorsa().getRisorsaAppart().getTipoRisorsa(),
							   								   ordSrvRigRsr.getRisorsa().getRisorsaAppart().getLivelloRisorsa(),
							   								   ordSrvRigRsr.getRisorsa().getRisorsaAppart().getIdRisorsa());
			   }	
			   storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_AMBIENTE_MANCANTI);
			   return costoRif;
		   }
	   }
	   else{
		   BigDecimal costoDocumento = ordSrvRigRsr.getCostoRiferimento();
		   if(iConsunCmm.isCostiRisorsaDaDocumento()) {
			   storicoCmm.setProvenienzaCosto(StoricoCommessa.PROV_COSTO_DOCUMENTO);
			   return ordSrvRigRsr.getCostoRiferimento();
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

   protected char recuperaOrdSrvVrsValorizzaCosto(char tipoProdotto, boolean gesSaldiCmm)
   {
      //Inserita anche se non gestita
      if(tipoProdotto == AttivitaSrvProdotto.PRODOTTO_PRIMARIO/* || tipoProdotto == AttivitaSrvProdotto.PRODOTTO_PRIMARIO_ALTRE*/)
         return StoricoCommessa.INCREMENTA_COSTI_INDIRETTI;

      if((tipoProdotto == AttivitaSrvProdotto.SOTTOPRODOTTO || tipoProdotto == AttivitaSrvProdotto.SFRIDO_ROTTAME) && !gesSaldiCmm)
         return StoricoCommessa.DECREMENTA_COSTO;

      if((tipoProdotto == AttivitaSrvProdotto.SOTTOPRODOTTO || tipoProdotto == AttivitaSrvProdotto.SFRIDO_ROTTAME) && gesSaldiCmm)
         return StoricoCommessa.NO;

      if(tipoProdotto == AttivitaSrvProdotto.SCARTO)
         return StoricoCommessa.NO;

      return StoricoCommessa.NO;
    }

    // fix 10913
    public StoricoCommessa completaDatiStoricoCmmMat(StoricoCommessa storicoCmm, AttivitaSrvMateriale ordSrvRigaMat) throws Exception {
      return storicoCmm;
    }
    public StoricoCommessa completaDatiStoricoCmmRsr(StoricoCommessa storicoCmm, AttivitaSrvRisorsa ordSrvRigaRsr) throws Exception {
      return storicoCmm;
    }
    public StoricoCommessa completaDatiStoricoCmmSpe(StoricoCommessa storicoCmm, AttivitaSrvSpesa ordSrvRigaSps) throws Exception {
      return storicoCmm;
    }
    public StoricoCommessa completaDatiStoricoCmmVrs(StoricoCommessa storicoCmm, AttivitaSrvProdotto ordSrvRigaVrs) throws Exception {
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

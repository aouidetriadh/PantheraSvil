package it.thera.thip.produzione.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.formula.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;
import it.thera.thip.base.articolo.*;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.generale.AmbienteCosti;
import it.thera.thip.base.risorse.*;
import it.thera.thip.datiTecnici.*;
import it.thera.thip.datiTecnici.costi.*;

/**
 * AttribuzioneCosti
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Mekki
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 04693   24/11/2005    LP         Correzioni varie
 * 04810   22/12/2005    LP         Aggiunto salvataggio storico solo se indicato nel file di risorse
 * 06773   27/02/2007    LP         Aggiunto metodo attribuzioneCostiInternal e chiamato anche per le
 *                                  righe di documento di trasferimento
 * 06994   26/03/2007    GN         Corretto reperimento dell'ambiente
 * 07430   09/07/2007    LP         Corretto gestione eccezioni
 * 10155   26/11/2008    DBot       Corretto reperimento costo risorsa su ambienti costi.
 *                                  Arrotondati valore costo dettaglio per non sfondare scale DB
 * 10160   28/11/2008    ME         Impostato tipo errore per alcune anomalie
 * 10411   28/01/2009    DBot       Ottimizzazione performance in consuntivazione
 * 10416   03/02/2009    DBot       Consuntivazione area servizi
 * 10913   25/03/2009    DB         Per permettere la personalizzazione
 * 12271   25/02/2010    DBot       Modificata attribuzione costi per le risorse. In caso non rovato valore su Ambiente o se trovato
 *                                  non valorizzato si usa quello del documento assegnandolo a opportuna cmp costo
 * 27486   25/05/2018    DBot       Reperimento costi risorsa da documento
 * 31460   13/07/2020    RA			Nuova gestione di calcolo costo
 * 31513   11/01/2021	 RA			Corretto problema di loop
 * 33081   08/03/2021    DBot       Correzioni varie
 * 33950   14/10/2021    RA			Vari modifiche: nuovo gestione di estrazione consuntivi commessa
 */

public class AttribuzioneCosti {

  protected static final String LIVELLO_RES = "it.thera.thip.datiTecnici.costi.resources.FormulaCosti";

  ConsuntivazioneCommesse iConsuntivazioneCommesse = new ConsuntivazioneCommesse();
  // Fix 10913
  //private int messageNumber = 0;
  protected int messageNumber = 0;
  // fine fix 10913

  public static final BigDecimal ZERO = new BigDecimal("0");

  protected static final int SCALA_COSTI_DET = 6; //Fix 10155

  public AttribuzioneCosti(ConsuntivazioneCommesse consComm) {
    iConsuntivazioneCommesse = consComm;
  }

  public List attribuzioneCosti(StoricoCommessa storicoCommessa, Integer idProgrStoric, char tipologiaCostoCmm, java.sql.Date dataRiferimento, String idAmbienteCosti, boolean previsto) {

    CompactCommessa compactCmm = iConsuntivazioneCommesse.getCompactCommessa(storicoCommessa.getCommessa() != null ?
      storicoCommessa.getCommessa() : storicoCommessa.getCommessaCol());
    if(compactCmm != null)
      compactCmm.setCostoAggregato(true);

    Vector costiCommessaList = new Vector();
    Vector costiCommessaElemList = new Vector();
    CostiCommessaElem costiCommessaElem = null;
    CostiCommessa costiCommessa = null;
    List costoCommessaDetTempList = new ArrayList();
    List costoCommessaDetNouvoCreatedList = new ArrayList();
    String message = null;
    ReportAnomalieConsCmm anomalie = null;
    List anomalieList = new ArrayList();

    /*if(storicoCommessa.getIdCommessaApp() != null){
      CostiCommessa costiCommParent = getCostiCommessa(storicoCommessa.getIdAzienda(),storicoCommessa.getIdCommessaApp(),storicoCommessa.getIdCommessaPrm(),tipologiaCostoCmm);
      if(costiCommParent != null)
        idProgrStoric = costiCommParent.getIdProgrStoric();
         }
     */
    //Fix 04599 Mz inizio
    String clauseWhere = CostiCommessaTM.ID_AZIENDA + " = '" + storicoCommessa.getIdAzienda() + "' AND " +
      CostiCommessaTM.ID_PROGR_STORIC + " =" + idProgrStoric + " AND " +
      CostiCommessaTM.ID_COMMESSA + " = '" + storicoCommessa.getIdCommessa() + "' AND " +
      CostiCommessaTM.UFFICIALE + " = '" + Column.TRUE_CHAR + "' AND " +
      CostiCommessaTM.TIPOLOGIA + " = '" + tipologiaCostoCmm + "'";
    //Fix 04599 Mz fine
    try {
      costiCommessaList = CostiCommessa.retrieveList(clauseWhere, "", true);
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }

    if(costiCommessaList.size() == 1) {
      costiCommessa = (CostiCommessa)costiCommessaList.get(0);
      if(costiCommessa.getIdConfigurazione() != null)
        clauseWhere = CostiCommessaElemTM.ID_AZIENDA + " = '" + costiCommessa.getIdAzienda() + "' AND " +
          CostiCommessaElemTM.ID_PROGR_STORIC + " =" + costiCommessa.getIdProgrStoric() + " AND " +
          CostiCommessaElemTM.ID_COMMESSA + " = '" + costiCommessa.getIdCommessa() + "' AND " +
          CostiCommessaElemTM.R_ARTICOLO + " = '" + costiCommessa.getIdArticolo() + "' AND " +
          CostiCommessaElemTM.R_VERSIONE + " =" + costiCommessa.getIdVersione() + " AND " +
          CostiCommessaElemTM.R_CONFIGURAZIONE + " =" + costiCommessa.getIdConfigurazione() + " AND " +
          CostiCommessaElemTM.R_STABILIMENTO + " = '" + costiCommessa.getIdStabilimento() + "'";
      else
        clauseWhere = CostiCommessaElemTM.ID_AZIENDA + " = '" + costiCommessa.getIdAzienda() + "' AND " +
          CostiCommessaElemTM.ID_PROGR_STORIC + " =" + costiCommessa.getIdProgrStoric() + " AND " +
          CostiCommessaElemTM.ID_COMMESSA + " = '" + costiCommessa.getIdCommessa() + "' AND " +
          CostiCommessaElemTM.R_ARTICOLO + " = '" + costiCommessa.getIdArticolo() + "' AND " +
          CostiCommessaElemTM.R_VERSIONE + " =" + costiCommessa.getIdVersione() + " AND " +
          CostiCommessaElemTM.R_STABILIMENTO + " = '" + costiCommessa.getIdStabilimento() + "'";

      try {
        costiCommessaElemList = CostiCommessaElem.retrieveList(clauseWhere, "", true);
      }
      catch(Exception e) {
        e.printStackTrace(Trace.excStream); //...FIX 7430
      }
      if(costiCommessaElemList.size() == 1) {
        costiCommessaElem = (CostiCommessaElem)costiCommessaElemList.get(0);
      }
    }
    if(costiCommessa == null) {
      costiCommessa = creaCostiCommessa(storicoCommessa, idProgrStoric, tipologiaCostoCmm, dataRiferimento);
      if(costiCommessa != null) {
        costiCommessaElem = creaCostiCommessaElem(storicoCommessa, costiCommessa, idProgrStoric);
        if(costiCommessaElem != null) {
          Articolo articolo = costiCommessa.getArticolo();
          List listComponenti = new ArrayList();
          //List costoCommessaDetList = new ArrayList();
          if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getIdSchemaCosto() != null)
            listComponenti = costiCommessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti();
          for(int i = 0; i < listComponenti.size(); i++) {
            LinkCompSchema linkCompSchema = (LinkCompSchema)listComponenti.get(i);
            CostiCommessaDet costiCommDet = creaCostiCommessaDet(storicoCommessa, costiCommessaElem, linkCompSchema.getComponenteCosto(), idProgrStoric, false);
            if(costiCommDet == null) {
              //return new ArrayList();
              message = new ErrorMessage("THIP20T073").getLongText();

              anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
              try {
                iConsuntivazioneCommesse.addAnomalie(anomalie);
              }
              catch(Exception e) {
                e.printStackTrace(Trace.excStream); //...FIX 7430
              }
              anomalieList.add(anomalie);

              return anomalieList;
            }
            else
              costoCommessaDetNouvoCreatedList.add(costiCommDet);
          }
          //28/06/05
          if(!iConsuntivazioneCommesse.isSimulazione()) {
            try {
              ConnectionManager.commit();
            }
            catch(Exception e) {
              e.printStackTrace(Trace.excStream); //...FIX 7430
            }
          }
        }
        else {
          //return new ArrayList();
          message = new ErrorMessage("THIP20T074").getLongText();

          anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
          //Fix 10160 - inizio
          anomalie.setTipoErrore(new ErrorMessage("THIP20T074"));
          //Fix 10160 - fine
          try {
            iConsuntivazioneCommesse.addAnomalie(anomalie);
          }
          catch(Exception e) {
            e.printStackTrace(Trace.excStream); //...FIX 7430
          }
          anomalieList.add(anomalie);

          return anomalieList;
        }
      }
      else {
        //return new ArrayList();
        message = new ErrorMessage("THIP20T075").getLongText();

        anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
        //Fix 10160 - inizio
        anomalie.setTipoErrore(new ErrorMessage("THIP20T075"));
        //Fix 10160 - fine
        try {
          iConsuntivazioneCommesse.addAnomalie(anomalie);
        }
        catch(Exception e) {
          e.printStackTrace(Trace.excStream); //...FIX 7430
        }
        anomalieList.add(anomalie);

        return anomalieList;
      }
    }

    //punto B
    List listLinkCompSchema = new ArrayList();
    //Fix 10416 inizio
    //if(storicoCommessa.getTipoRigaOrigine() != StoricoCommessa.PRODUZIONE_RISORSA) {
    if(storicoCommessa.getTipoRigaOrigine() != StoricoCommessa.PRODUZIONE_RISORSA &&
       storicoCommessa.getTipoRigaOrigine() != StoricoCommessa.SERVIZIO_RISORSA) { //Fix 10416 fine
      Articolo articolo = storicoCommessa.getArticolo();
      if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getIdSchemaCosto() != null){
        // fix 10913
        listLinkCompSchema = dammiComponenti(storicoCommessa, articolo);
        // fine fix 10913
      }
    }
    else {
      Risorsa risorsa = storicoCommessa.getRisorsa();
      if(risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA)
        risorsa = risorsa.getRisorsaAppart();
      if(risorsa.getIdSchemaCosto() != null)
        listLinkCompSchema = risorsa.getSchemaCosto().getComponenti();
    }
    for(int i = 0; i < listLinkCompSchema.size(); i++) {
      LinkCompSchema linkCompSchema = (LinkCompSchema)listLinkCompSchema.get(i);
      CostiCommessaDet costiCommDet = creaCostiCommessaDet(storicoCommessa, costiCommessaElem, linkCompSchema.getComponenteCosto(), idProgrStoric, true);
      costoCommessaDetTempList.add(costiCommDet);
    }
    //punto C
    //Fix 10416 inizio
    //if(storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_RISORSA
     if((storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_RISORSA ||
         storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_RISORSA)     //Fix 10416 fine
      && (storicoCommessa.getTipoRilevazioneRsr() == Risorsa.TEMPO || storicoCommessa.getTipoRilevazioneRsr() == Risorsa.QUANTITA)) {

      //Fix 12271 inizio
      CostoRisorsa costoRsr = getCostoRisorsaPerCalcolo(storicoCommessa, idAmbienteCosti);
      /*
      Vector risorse = new Vector();
      CostoRisorsa costoRsr = null;
      Risorsa risorsa = storicoCommessa.getRisorsa();

      String idAmbiente = getIdAmbienteValid(storicoCommessa);
      //Fix 10155 inizio
      if(idAmbiente != null)
      {
         costoRsr = getCostoRisorsa(risorsa, " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbiente + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
         if(costoRsr == null && risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA)
            costoRsr = getCostoRisorsa(risorsa.getRisorsaAppart(), " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbiente + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
      }

      if(costoRsr == null)
      {
         costoRsr = getCostoRisorsa(risorsa, " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbienteCosti + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
         if(costoRsr == null && risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA)
            costoRsr = getCostoRisorsa(risorsa.getRisorsaAppart(), " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbienteCosti + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
      }
      */
      //Fix 12271 fine
      /*
      if(idAmbiente != null) {
         costoRsr = getCostoRisorsa(risorsa, " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbiente + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
         if(costoRsr == null) {
           if(risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA) {
             costoRsr = getCostoRisorsa(risorsa.getRisorsaAppart(), " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbiente + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
             if(costoRsr == null) {
               costoRsr = getCostoRisorsa(risorsa, " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbienteCosti + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
               if(costoRsr == null) {
                 if(risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA) {
                   costoRsr = getCostoRisorsa(risorsa.getRisorsaAppart(), " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbienteCosti + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
                 }
               }
             }
           }
         }
       }
       else {
        costoRsr = getCostoRisorsa(risorsa, " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbienteCosti + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
        if(costoRsr == null) {
          if(risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA) {
            costoRsr = getCostoRisorsa(risorsa.getRisorsaAppart(), " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbienteCosti + "'", storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
          }
        }
      }
      */
      //Fix 10155 fine
      if(costoRsr != null) {
        List costoDettagli = costoRsr.getDettagli();
        //Fix 04361 Mz inizio
        //if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO && costoRsr.getCostoRiferimento().compareTo(storicoCommessa.getCostoUnitario()) != 0){
        //if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO && Utils.compare(costoRsr.getCostoRiferimento(), storicoCommessa.getCostoUnitario()) != 0){
        //...FIX 4693 (LP)
        
        //31460 inizio
        /*
        if(!iConsuntivazioneCommesse.isCostiRisorsaDaDocumento())//Fix 27486 condizionata modifica al flag
        {   
        */	
        //31460	
        
           if(Utils.compare(costoRsr.getCostoRiferimento(), storicoCommessa.getCostoUnitario()) != 0) {
              //Fix 04361 Mz fine
              //add error + anomalie
              message = new ErrorMessage("THIP20T070").getLongText();

              anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
              try {
                 iConsuntivazioneCommesse.addAnomalie(anomalie);
              }
              catch(Exception e) {
                 e.printStackTrace(Trace.excStream); //...FIX 7430
              }
              anomalieList.add(anomalie);

              storicoCommessa.setCostoUnitario(costoRsr.getCostoRiferimento());
              if(storicoCommessa.getTipoRilevazioneRsr() == Risorsa.TEMPO) {
                 if(costoRsr.getCostoRiferimento() != null && storicoCommessa.getTempo() != null)
                    storicoCommessa.setCostoTotale(costoRsr.getCostoRiferimento().multiply(storicoCommessa.getTempo()));
              }
              else if(storicoCommessa.getTipoRilevazioneRsr() == Risorsa.QUANTITA) {
                 if(costoRsr.getCostoRiferimento() != null && storicoCommessa.getQuantita() != null && storicoCommessa.getQtaScarto() != null)
                    storicoCommessa.setCostoTotale(costoRsr.getCostoRiferimento().multiply(storicoCommessa.getQuantita().add(storicoCommessa.getQtaScarto())));
                 else {
                    if(costoRsr.getCostoRiferimento() != null && storicoCommessa.getQuantita() != null)
                       storicoCommessa.setCostoTotale(costoRsr.getCostoRiferimento().multiply(storicoCommessa.getQuantita()));
                    else {
                       if(costoRsr.getCostoRiferimento() != null && storicoCommessa.getQtaScarto() != null)
                          storicoCommessa.setCostoTotale(costoRsr.getCostoRiferimento().multiply(storicoCommessa.getQtaScarto()));
                    }
                 }
              }
              try {
                 //...FIX 4693 (LP)
                 if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO || ConsuntivazioneEstrazione.isSalvaOrdine()) //...FIX 4810
                    storicoCommessa.save();
              }
              catch(Exception e) {
                 e.printStackTrace(Trace.excStream); //...FIX 7430
              }

           }
        //}//31460
        sommaCostoCommessaDettTemp(costoCommessaDetTempList, costoDettagli, storicoCommessa);
      }
      else {
        // add error anomalie
        message = new ErrorMessage("THIP20T071").getLongText();

        anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
        //Fix 10160 - inizio
        anomalie.setTipoErrore(new ErrorMessage("THIP20T071"));
        //Fix 10160 - fine
        try {
           iConsuntivazioneCommesse.addAnomalie(anomalie);
        }
        catch(Exception e) {
          e.printStackTrace(Trace.excStream); //...FIX 7430
        }
        anomalieList.add(anomalie);
        
      }
    }

    //punto D
    //Fix 04653 Mz inizio
    else if(isRigaTrasferimentoMagazzino(storicoCommessa)) {
      if(storicoCommessa.getValorizzaCosto() == StoricoCommessa.DECREMENTA_COSTO) {
    	//33950 inizio
    	//CostiCommessaElem costoPrevistoElem = getCostoPrevistoElemento(storicoCommessa);
    	CostiCommessaElem costoPrevistoElem = null;
    	if(!iConsuntivazioneCommesse.isEstrazioneStoriciCommessa())
    		costoPrevistoElem = getCostoPrevistoElemento(storicoCommessa);
    	//33950 fine
        if(costoPrevistoElem == null) {
          //...FIX 6773 inizio
          //addCostoPrevistoNonTrovatoAnomlia(storicoCommessa, anomalieList);
          //33950 inizio
          //CostiCommessaDet apportunoCostiCommessaDet = getApportunoCostoCommessaDet(storicoCommessa, costoCommessaDetTempList);
          CostiCommessaDet apportunoCostiCommessaDet = null;
          if(!iConsuntivazioneCommesse.isEstrazioneStoriciCommessa())
        	  apportunoCostiCommessaDet = getApportunoCostoCommessaDet(storicoCommessa, costoCommessaDetTempList);
          //33950 fine
          if(apportunoCostiCommessaDet != null) {
            attribuzioneCostiInternal(apportunoCostiCommessaDet, storicoCommessa, costoCommessaDetTempList, listLinkCompSchema, idAmbienteCosti, message, anomalie, anomalieList);
          }
          //...FIX 6773 fine
        }
        else {
          decrementaDettagliTemporanei(storicoCommessa, costoCommessaDetTempList, costoPrevistoElem);
        }
      }
      else { // force storicoCommessa.getValorizzaCosto() == StoricoCommessa.INCREMENTA_COSTO
    	//33950 inizio
    	//CostiCommessaElem costoPrevistoElem = getCostoPrevistoElemento(storicoCommessa, true);
    	CostiCommessaElem costoPrevistoElem = null;
    	if(!iConsuntivazioneCommesse.isEstrazioneStoriciCommessa())
    		costoPrevistoElem = getCostoPrevistoElemento(storicoCommessa, true);
    	//33950 fine
        if(costoPrevistoElem == null) {
          //...FIX 6773 inizio
          //addCostoPrevistoNonTrovatoAnomlia(storicoCommessa, anomalieList);
          CostiCommessaDet apportunoCostiCommessaDet = getApportunoCostoCommessaDet(storicoCommessa, costoCommessaDetTempList);
          if(apportunoCostiCommessaDet != null) {
            attribuzioneCostiInternal(apportunoCostiCommessaDet, storicoCommessa, costoCommessaDetTempList, listLinkCompSchema, idAmbienteCosti, message, anomalie, anomalieList);
          }
          //...FIX 6773 fine
        }
        else {
          incrementaDettagliTemporanei(storicoCommessa, costoCommessaDetTempList, costoPrevistoElem);
        }
      }
    }
    //Fix 04653 Mz fine
    //punto E
    else {
      CostiCommessaDet apportunoCostiCommessaDet = null;
      //Fix 10416 inizio
      //if(storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_RISORSA && storicoCommessa.getTipoRilevazioneRsr() == Risorsa.COSTO)
      if((storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_RISORSA ||
          storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_RISORSA) && //Fix 10416 fine
          storicoCommessa.getTipoRilevazioneRsr() == Risorsa.COSTO)
        apportunoCostiCommessaDet = getApportunoCostoCommessaDetRisorsa(storicoCommessa, costoCommessaDetTempList);
      else
        apportunoCostiCommessaDet = getApportunoCostoCommessaDet(storicoCommessa, costoCommessaDetTempList);
      if(apportunoCostiCommessaDet != null) {
        attribuzioneCostiInternal(apportunoCostiCommessaDet, storicoCommessa, costoCommessaDetTempList, listLinkCompSchema, idAmbienteCosti, message, anomalie, anomalieList);
      }
      else {
        // return new ArrayList();
        //Fix 04361 Mz inizio
        //message = new ErrorMessage("THIP20T076").getLongText();
        ErrorMessage err = checkClasseMerceologica(storicoCommessa);
        if(err == null)
          message = new ErrorMessage("THIP20T076").getLongText();
        else
          message = checkClasseMerceologica(storicoCommessa).getLongText();
          //Fix 04361 Mz fine
        anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
        //Fix 10160 - inizio
        anomalie.setTipoErrore(new ErrorMessage("THIP20T076"));
        //Fix 10160 - fine
        try {
          iConsuntivazioneCommesse.addAnomalie(anomalie);
        }
        catch(Exception e) {
          e.printStackTrace(Trace.excStream); //...FIX 7430
        }
        anomalieList.add(anomalie);

        return anomalieList;
      }
    }
    //33950 inizio : Valorizzazione dettaglio di storici
    if(iConsuntivazioneCommesse.isEstrazioneStoriciCommessa() && !costoCommessaDetTempList.isEmpty()) {
    	for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
    		CostiCommessaDet costoCommessaDet = (CostiCommessaDet)costoCommessaDetTempList.get(i);
    		if((costoCommessaDet.getCostoLivello() != null && costoCommessaDet.getCostoLivello().compareTo(ZERO) != 0)
    			||
    			(costoCommessaDet.getCostoLivelliInf() != null && costoCommessaDet.getCostoLivelliInf().compareTo(ZERO) != 0)
    			||
    			(costoCommessaDet.getCostoTotale() != null && costoCommessaDet.getCostoTotale().compareTo(ZERO) != 0)
    			||
    			(storicoCommessa.getTempo() != null && storicoCommessa.getTempo().compareTo(ZERO) != 0)) {
    			StoricoCommessaDet storicoCommessaDet = creaStoricoCommessaDet(storicoCommessa, costoCommessaDet);
    		}
    	}
    }
    //33950 fine
    //punto F
    boolean ok = sommaCostiToPersistentDet(costoCommessaDetTempList, costiCommessaElem, costoCommessaDetNouvoCreatedList);
    if(ok) {
      try {
        if(!previsto) {
          int rc = storicoCommessa.save();
          if(rc < 0)
            ok = false;
        }
        if(ok) {
          costiCommessaElem.iCalcoloImporto = false;
          ok = valorizzaCostiElem(costiCommessaElem, costoCommessaDetTempList);
        }
        if(ok) {
          //28/06/05
          if(!iConsuntivazioneCommesse.isSimulazione())
            ConnectionManager.commit();
            //else
            //ConnectionManager.rollback();
        }
      }
      catch(Exception e) {
        e.printStackTrace(Trace.excStream); //...FIX 7430
      }
    }
    else {
      //addError
      message = new ErrorMessage("THIP20T077").getLongText();

      anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
      //Fix 10160 - inizio
      anomalie.setTipoErrore(new ErrorMessage("THIP20T077"));
      //Fix 10160 - fine
      try {
        iConsuntivazioneCommesse.addAnomalie(anomalie);
      }
      catch(Exception e) {
        e.printStackTrace(Trace.excStream); //...FIX 7430
      }
      anomalieList.add(anomalie);

      return anomalieList;
    }
    return anomalieList;
  }

  //Fix 12271 inizio
  protected CostoRisorsa getCostoRisorsaPerCalcolo(StoricoCommessa storicoCommessa, String idAmbienteCosti)
  {
     CostoRisorsa costoRsr = null;
     //CostoRisorsa ultimoCRNotNull = null;//31460
     Risorsa risorsa = storicoCommessa.getRisorsa();
     AmbienteCosti ambiente = getAmbienteCostiValid(storicoCommessa);
     //Fix 33081 inizio
     if(ambiente == null)
     {
        try
        {
           
           String keyAmb = KeyHelper.buildObjectKey(new String[] {Azienda.getAziendaCorrente(), idAmbienteCosti});
           ambiente = AmbienteCosti.elementWithKey(keyAmb, PersistentObject.NO_LOCK);
        }
        catch(Exception ex)
        {
           ex.printStackTrace(Trace.excStream);
        }
     }
     
     TipoCosto tipoCosto = getConsuntivazioneCommesse().getTipoCostoMancanti();
     //Fix 33081 fine
     
     //31460 inzio
     /*     
     //Fix 27486 inizio
     if(iConsuntivazioneCommesse.isCostiRisorsaDaDocumento())
     {
        if(ambiente != null)
           costoRsr = AmbienteCosti.creaCostoRisorsa(ambiente, storicoCommessa.getIdStabilimento(), risorsa, true, null);

        if(costoRsr != null)
        {
           costoRsr.setCostoRiferimento(storicoCommessa.getCostoUnitario());
           //Azzero tutte le componenti di costo e imposto quella per com costo su risorsa o su 1° elementare
           boolean costoImpostato = false;
           String idCompCostoRis = risorsa.getIdComponenteCosto();
           List costoDettagli = costoRsr.getDettagli();
           Iterator iterComp = costoDettagli.iterator();
           while(iterComp.hasNext())
           {
              DettaglioCosto detCosto = (DettaglioCosto)iterComp.next();
              detCosto.setCostoLivelliInf(null);
              detCosto.setCostoLivello(null);
              if(!costoImpostato)
              {
                 if((idCompCostoRis != null && idCompCostoRis.equals(detCosto.getIdComponenteCosto())) ||
                    (idCompCostoRis == null && detCosto.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI))
                 {
                    detCosto.setCostoLivello(storicoCommessa.getCostoUnitario());
                    costoImpostato = true;
                 }
              }
           }
        }        
        return costoRsr;
     }
     //Fix 27486 fine
        

     //Ricerca costo risorsa su ambiente standard
     if(ambiente != null)
     {
        String whereAmb = " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + ambiente.getIdAmbiente() + "'";
        costoRsr = getCostoRisorsa(risorsa, whereAmb, storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
        if(costoRsr != null)
           ultimoCRNotNull = costoRsr;
        
        if(!isCostoRisorsaValido(costoRsr) && risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA)
        {
           costoRsr = getCostoRisorsa(risorsa.getRisorsaAppart(), whereAmb, storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
           if(costoRsr != null)
              ultimoCRNotNull = costoRsr;
        }
     }
     
     //Ricerca costo risorsa su ambiente costi mancanti
     if(!isCostoRisorsaValido(costoRsr))
     {
        String whereAmb = " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbienteCosti + "'";
        costoRsr = getCostoRisorsa(risorsa, whereAmb, storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
        if(costoRsr != null)
           ultimoCRNotNull = costoRsr;
        
        if(!isCostoRisorsaValido(costoRsr) && risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA)
        {
           costoRsr = getCostoRisorsa(risorsa.getRisorsaAppart(), whereAmb, storicoCommessa.getIdAzienda(), storicoCommessa.getIdStabilimento());
           if(costoRsr != null)
              ultimoCRNotNull = costoRsr;
        }
     }

     //Impostazione di singola componente di costo con costo da documento
     if(!isCostoRisorsaValido(costoRsr))
     {
        BigDecimal costoUnitario = ZERO;
        if(storicoCommessa.getCostoUnitario() != null && storicoCommessa.getCostoUnitario().compareTo(ZERO) > 0)
           costoUnitario = storicoCommessa.getCostoUnitario();
        
        costoRsr = ultimoCRNotNull;
        if(costoRsr == null)
        {
           if(ambiente != null)
              costoRsr = AmbienteCosti.creaCostoRisorsa(ambiente, storicoCommessa.getIdStabilimento(), risorsa, true, null);
        }
        if(costoRsr != null)
        {
           costoRsr.setCostoRiferimento(costoUnitario);
           //Azzero tutte le componenti di costo e imposto quella per com costo su risorsa o su 1° elementare
           boolean costoImpostato = false;
           String idCompCostoRis = risorsa.getIdComponenteCosto();
           List costoDettagli = costoRsr.getDettagli();
           Iterator iterComp = costoDettagli.iterator();
           while(iterComp.hasNext())
           {
              DettaglioCosto detCosto = (DettaglioCosto)iterComp.next();
              detCosto.setCostoLivelliInf(null);
              detCosto.setCostoLivello(null);
              if(!costoImpostato)
              {
                 if((idCompCostoRis != null && idCompCostoRis.equals(detCosto.getIdComponenteCosto())) ||
                    (idCompCostoRis == null && detCosto.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI))
                 {
                    detCosto.setCostoLivello(costoUnitario);
                    costoImpostato = true;
                 }
              }
           }
        }
     }
     */
     BigDecimal costoUnitario = ZERO;
     if(storicoCommessa.getCostoUnitario() != null && storicoCommessa.getCostoUnitario().compareTo(ZERO) > 0)
        costoUnitario = storicoCommessa.getCostoUnitario();
     //Fix 33081 inizio
     //if(ambiente != null)
     // costoRsr = AmbienteCosti.creaCostoRisorsa(ambiente, storicoCommessa.getIdStabilimento(), risorsa, true, null); 
     if(ambiente != null || tipoCosto != null)
        costoRsr = creaCostoRisorsa(ambiente, tipoCosto, storicoCommessa.getIdStabilimento(), risorsa, true, null); 
     //Fix 33081 fine
     
     if(costoRsr != null) {
        costoRsr.setCostoRiferimento(costoUnitario);
        //Azzero tutte le componenti di costo e imposto quella per com costo su risorsa o su 1° elementare
        boolean costoImpostato = false;
        String idCompCostoRis = risorsa.getIdComponenteCosto();
        List costoDettagli = costoRsr.getDettagli();
        Iterator iterComp = costoDettagli.iterator();
        while(iterComp.hasNext())
        {
           DettaglioCosto detCosto = (DettaglioCosto)iterComp.next();
           detCosto.setCostoLivelliInf(null);
           detCosto.setCostoLivello(null);
           if(!costoImpostato)
           {
              if((idCompCostoRis != null && idCompCostoRis.equals(detCosto.getIdComponenteCosto())) ||
                 (idCompCostoRis == null && detCosto.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI))
              {
                 detCosto.setCostoLivello(costoUnitario);
                 costoImpostato = true;
              }
           }
        }
     }
     //31460 fine
     return costoRsr;
  }

  //Fix 33081 inizio
  public CostoRisorsa creaCostoRisorsa(AmbienteCosti ambienteCosti, TipoCosto tipoCosto, String idStabilimento, Risorsa risorsa, boolean creaDettagli, HashMap componentiCosto) { 
     CostoRisorsa costoRisorsa = (CostoRisorsa) Factory.createObject(CostoRisorsa.class);

     try {
       costoRisorsa.setIdAzienda(Azienda.getAziendaCorrente());
       if(ambienteCosti != null)
          costoRisorsa.setIdAmbiente(ambienteCosti.getIdAmbiente());
       costoRisorsa.setIdStabilimento(idStabilimento);
       costoRisorsa.setRisorsa(risorsa);
       costoRisorsa.setIdCentroLavoro(risorsa.getIdCentroLavoro());
       costoRisorsa.setTipologia(CostoRisorsa.RISORSA);
       costoRisorsa.setComponentiCosto(componentiCosto); //Fix 3764

       if (creaDettagli) {
         SchemaCosto schemaCosto = AperturaUtil.identificaSchemaCostoForRisorsa(risorsa);
         if (schemaCosto != null) {
           List ComponentiFromRisorsa = schemaCosto.getComponenti();
           ComponenteCosto componenteCosto;
           DettaglioCosto dettaglioCosto;
           for (int i = 0; i < ComponentiFromRisorsa.size(); i++) {
             if (componentiCosto == null) //Fix 3764
               componenteCosto = ( (LinkCompSchema) ComponentiFromRisorsa.get(i)).getComponenteCosto();
             //Fix 3764 inizio
             else {
               String compKey = ( (LinkCompSchema) ComponentiFromRisorsa.get(i)).getComponenteCostoKey();
               componenteCosto = (ComponenteCosto) componentiCosto.get(compKey);
             }
             
             TipoCosto tc = tipoCosto;
             if(ambienteCosti != null)
                tc = ambienteCosti.getTipoCosto();
             
             if (componenteCosto.getTipiCosto().contains(tc)) {
               //Fix 3764 fine
               dettaglioCosto = (DettaglioCosto) Factory.createObject(DettaglioCosto.class);
               dettaglioCosto.setFatherKey(costoRisorsa.getKey());
               dettaglioCosto.setComponenteCosto(componenteCosto);
               dettaglioCosto.setCostoLivello(null);
               dettaglioCosto.setCostoLivelliInf(null);

               costoRisorsa.getDettagli().add(dettaglioCosto);
             }
           }
         }
       }
     }
     catch (Exception ex) {
       ex.printStackTrace(Trace.excStream);
     }
     return costoRisorsa;
   }
  //Fix 33081 fine  

  protected boolean isCostoRisorsaValido(CostoRisorsa costoRsr)
  {
     boolean valido = false;
     if(costoRsr != null && 
        costoRsr.getCostoRiferimento() != null && 
        costoRsr.getCostoRiferimento().compareTo(ZERO) > 0)
        valido = true;
     return valido;
  }
//Fix 12271 fine


  /**
   * puntoE
   * @param apportunoCostiCommessaDet CostiCommessaDet
   * @param storicoCommessa StoricoCommessa
   * @param costoCommessaDetTempList List
   * @param listLinkCompSchema List
   * @param idAmbienteCosti String
   * @param message String
   * @param anomalie ReportAnomalieConsCmm
   * @param anomalieList List
   */
  public void attribuzioneCostiInternal(CostiCommessaDet apportunoCostiCommessaDet, StoricoCommessa storicoCommessa, List costoCommessaDetTempList, List listLinkCompSchema, String idAmbienteCosti, String message, ReportAnomalieConsCmm anomalie, List anomalieList) {
    if(storicoCommessa.getValorizzaCosto() == StoricoCommessa.INCREMENTA_COSTO || storicoCommessa.getValorizzaCosto() == StoricoCommessa.DECREMENTA_COSTO) {
      if(storicoCommessa.getValorizzaCosto() == StoricoCommessa.INCREMENTA_COSTO) {
        if(storicoCommessa.getCostoTotale() != null) {
          costoCommessaDetTempList.remove(apportunoCostiCommessaDet);
          apportunoCostiCommessaDet.setCostoLivello(apportunoCostiCommessaDet.getCostoLivello().add(storicoCommessa.getCostoTotale()));
          apportunoCostiCommessaDet.setCostoTotale(apportunoCostiCommessaDet.getCostoLivello());
          costoCommessaDetTempList.add(apportunoCostiCommessaDet);
        }
      }
      else if(storicoCommessa.getValorizzaCosto() == StoricoCommessa.DECREMENTA_COSTO) {
        if(storicoCommessa.getCostoTotale() != null) {
          costoCommessaDetTempList.remove(apportunoCostiCommessaDet);
          apportunoCostiCommessaDet.setCostoLivello(apportunoCostiCommessaDet.getCostoLivello().subtract(storicoCommessa.getCostoTotale()));
          apportunoCostiCommessaDet.setCostoTotale(apportunoCostiCommessaDet.getCostoLivello());
          costoCommessaDetTempList.add(apportunoCostiCommessaDet);
        }
      }
      try {
        //Fix 04361 Mz inizio
        java.util.Hashtable result = calcoloCosti(costoCommessaDetTempList, storicoCommessa, listLinkCompSchema, idAmbienteCosti);
        //java.util.Hashtable result= calcoloCostiInternal(costoCommessaDetTempList,storicoCommessa,listLinkCompSchema,idAmbienteCosti);
        //Fix 04361 Mz fine
      }
      catch(Exception e) {
        e.printStackTrace(Trace.excStream); //...FIX 7430
      }

      //... FIX 4709 LP inizio
      //...Se TipoRigaOrigine = '2' (Lavorazione esterna – prodotto), in aggiunta al trattamento
      //...dei costi 'diretti', bisogna attribuire anche gli eventuali contributi ai costi
      //...'indiretti' assunti dal 'Costo previsto' in modo analogo a quanto descritto al punto E.2
      if(storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.LAVORAZIONE_ESTERNA_PRODOTTO) {

        //...Trovo tutte le componenti di costo aventi attributo TipoComponente != 'D' (componenti di tipo indiretto),
        //...Provenienza = 'F' (Calcolato da formula) e UtilizzoFormula (nell’oggetto SchemaCostoComponente) = 'Si'
        //...e me le salvo in una lista
        List costiCommDetIndirect = getCostoCommessaDetIndirect(costoCommessaDetTempList, listLinkCompSchema);

        //...Identifico l'oggetto CostoCommessaElemento che rappresenta il 'Costo previsto'
        CostiCommessaElem costiCmmElemPrevisto = getCostiCmmElemPrevisto(storicoCommessa);
        //...Se non ho trovato il CostoCommessaElemento per il 'Costo previsto' restituisco un'anomalia
        if(costiCmmElemPrevisto == null) {
          message = new ErrorMessage("THIP20T072").getLongText();
          anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
          try {
            iConsuntivazioneCommesse.addAnomalie(anomalie);
          }
          catch(Exception e) {
            e.printStackTrace(Trace.excStream);
          }
          anomalieList.add(anomalie);
        }
        //...Altrimenti proseguo
        else {
          List costoCmmDetMiaListaTemporanea = new ArrayList();
          for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
            CostiCommessaDet costCmm = (CostiCommessaDet)costoCommessaDetTempList.get(i);
            CostiCommessaDet costiCommessaDet = new CostiCommessaDet();
            try {
              costiCommessaDet.setEqual(costCmm);
            }
            catch(CopyException ex) {
            }
            costoCmmDetMiaListaTemporanea.add(costiCommessaDet);
          }

          for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
            CostiCommessaDet costCmm = (CostiCommessaDet)costoCommessaDetTempList.get(i);
            costCmm.setCostoLivello(ZERO);
            costCmm.setCostoLivelliInf(ZERO);
            costCmm.setCostoTotale(ZERO);
          }

          //...Cerco le componenti indirette che mi sono salvata prima e metto via una lista con
          //...queste componenti valorizzate come nel CostoPrevistoCommessaElem
          List compValorizz = getCompValorizz(costiCommDetIndirect, costiCmmElemPrevisto);

          //...Aggiorno la mia lista temporanea di dettagli con i valori letti dal CostoPrevistoCommessaElem
          aggiornaCostoCommessaDetTempListConPrevisti(costoCommessaDetTempList, costiCmmElemPrevisto);

          //...Calcolo le formule degli indiretti interessati con i valori che mi sono appena salvata
          calcolaFormula(costiCommDetIndirect, compValorizz, costoCommessaDetTempList, storicoCommessa, idAmbienteCosti);

          //...Valorizzo il CostoLivello dell’oggetto temporaneo CostoCommessaDettaglio
          valorizzaCostoLivello(costiCommDetIndirect, costiCmmElemPrevisto, storicoCommessa, costoCommessaDetTempList);

          //...Trova le componenti di costo di Totale
          List costiCommDetTotaleList = getCostiCommDetTotaleList(costoCommessaDetTempList, listLinkCompSchema);

          //...Valorizza le componenti di costo di totale
          valorizzaCostoCommDetTotale(costiCommDetTotaleList, costoCommessaDetTempList, costiCommDetIndirect, storicoCommessa, idAmbienteCosti, listLinkCompSchema);

          aggiornaCostoCommessaDetTempList(costiCommDetIndirect, costoCmmDetMiaListaTemporanea, storicoCommessa.getValorizzaCosto());

          costoCommessaDetTempList.clear();
          costoCommessaDetTempList.addAll(costoCmmDetMiaListaTemporanea);
          try {
            calcoloCostiTotali(costoCommessaDetTempList, storicoCommessa, listLinkCompSchema, idAmbienteCosti);
          }
          catch(SQLException ex1) {
            ex1.printStackTrace(Trace.excStream);
          }

        }
      }
      //... FIX 4709 LP fine

      if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO &&
        (storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.ACQUISTO ||
        storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.LAVORAZIONE_ESTERNA_PRODOTTO ||
        storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_RISORSA || //Fix 10416
        storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_RISORSA))
        aggiornaStoricoCommessa(storicoCommessa, costoCommessaDetTempList);
    }
    else if(storicoCommessa.getValorizzaCosto() == StoricoCommessa.INCREMENTA_COSTI_INDIRETTI) {
      List costiCommDetIndirect = getCostoCommessaDetIndirect(costoCommessaDetTempList, listLinkCompSchema);
      CostiCommessaElem costiCmmElemPrevisto = getCostiCmmElemPrevisto(storicoCommessa);
      if(costiCmmElemPrevisto == null) {
        // add anomalie
        message = new ErrorMessage("THIP20T072").getLongText();

        anomalie = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), message);
        try {
          iConsuntivazioneCommesse.addAnomalie(anomalie);
        }
        catch(Exception e) {
          e.printStackTrace(Trace.excStream); //...FIX 7430
        }
        anomalieList.add(anomalie);
        if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO) {
          storicoCommessa.setCostoUnitario(ZERO);
          storicoCommessa.setCostoTotale(ZERO);
          try {
            storicoCommessa.save();
          }
          catch(Exception e) {
            e.printStackTrace(Trace.excStream); //...FIX 7430
          }
        }
      }
      else {
        List compValorizz = getCompValorizz(costiCommDetIndirect, costiCmmElemPrevisto);
        aggiornaCostoCommessaDetTempListConPrevisti(costoCommessaDetTempList, costiCmmElemPrevisto);
        calcolaFormula(costiCommDetIndirect, compValorizz, costoCommessaDetTempList, storicoCommessa, idAmbienteCosti);
        valorizzaCostoLivello(costiCommDetIndirect, costiCmmElemPrevisto, storicoCommessa, costoCommessaDetTempList);
        List costiCommDetTotaleList = getCostiCommDetTotaleList(costoCommessaDetTempList, listLinkCompSchema);
        valorizzaCostoCommDetTotale(costiCommDetTotaleList, costoCommessaDetTempList, costiCommDetIndirect, storicoCommessa, idAmbienteCosti, listLinkCompSchema); //...FIX 4693 (LP)
        //recuperaCostoCommessaDetTempList(costiCommDetIndirect,costiCommDetTotaleList,costoCommessaDetTempList,listLinkCompSchema);
        if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO)
          aggiornaStoricoCommessa(storicoCommessa, costoCommessaDetTempList);
      }
    }
  }

  public CostiCommessa creaCostiCommessa(StoricoCommessa storicoCommessa, Integer idProgrStoric, char tipologiaCostoCmm, java.sql.Date dataRiferimento) {
    CostiCommessa costiComm = new CostiCommessa();
    Commessa commessa = storicoCommessa.getCommessa();
    costiComm.setIdAzienda(storicoCommessa.getIdAzienda());
    costiComm.setIdProgrStoric(idProgrStoric);
    costiComm.setIdCommessa(storicoCommessa.getIdCommessa());
    costiComm.setTipologiaCostoCmm(tipologiaCostoCmm);
    costiComm.setDataRiferimento(dataRiferimento);
    costiComm.setIdArticolo(commessa.getIdArticolo());
    if(commessa.getIdVersione() != null)
      costiComm.setIdVersione(commessa.getIdVersione());
    else
      costiComm.setIdVersione(new Integer("1"));
    costiComm.setIdConfigurazione(commessa.getIdConfigurazione());
    costiComm.setIdStabilimento(commessa.getIdStabilimento());
    costiComm.setUfficiale(true);
    costiComm.setIdCommessaApp(commessa.getIdCommessaAppartenenza());
    costiComm.setIdCommessaPrm(commessa.getIdCommessaPrincipale());
    costiComm.getDatiComuniEstesi().setStato(commessa.getDatiComuniEstesi().getStato());
    costiComm.setLivelloCommessa(commessa.getLivelloCommessa());

    try {
      //Fix 04599 Mz inizio
      //int rc = costiComm.save();
      //33950 inizio
      //int rc = costiComm.superSave();
      int rc = 0;
      if(!getConsuntivazioneCommesse().isEstrazioneStoriciCommessa())
    	  rc = costiComm.superSave();
      //33950 fine
      //Fix 04599 Mz fine
      if(rc >= 0) {
        return costiComm;
      }
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  public CostiCommessaElem creaCostiCommessaElem(StoricoCommessa storicoCommessa, CostiCommessa costiComm, Integer idProgrStoric) {
    CostiCommessaElem costiCommElem = new CostiCommessaElem();
    Commessa commessa = costiComm.getCommessa();
    Articolo articolo = costiComm.getArticolo();

    costiCommElem.setIdAzienda(costiComm.getIdAzienda());
    costiCommElem.setIdProgrStoric(idProgrStoric);
    costiCommElem.setIdCommessa(costiComm.getIdCommessa());
    //costiCommElem.setIdRigaElemento(new Integer(1)); //!!!
    costiCommElem.setTipologiaElem(CostiCommessaElem.ARTICOLO);
    costiCommElem.setIdArticolo(costiComm.getIdArticolo());
    costiCommElem.setIdVersione(costiComm.getIdVersione());
    costiCommElem.setIdConfigurazione(costiComm.getIdConfigurazione());
    costiCommElem.setIdStabilimento(costiComm.getIdStabilimento());
    if(commessa.getQtaUmPrm() != null)
      costiCommElem.setQuantita(commessa.getQtaUmPrm());
    else
      costiCommElem.setQuantita(new BigDecimal("1")); //!!
    costiCommElem.setIdUmPrmMag(commessa.getIdUmPrmMag()); //!!!
    costiCommElem.setCostoElementare(false); //!!!
    costiCommElem.setCostoGenerale(ZERO);
    costiCommElem.setCostoIndustriale(ZERO);
    costiCommElem.setCostoPrimo(ZERO);
    costiCommElem.setCostoRiferimento(ZERO);
    costiCommElem.setTipoParte(articolo.getTipoParte());
    costiCommElem.setIdPianificatore(articolo.getIdPianificatore());
    costiCommElem.setIdGruppoProdotto(articolo.getArticoloDatiIdent().getIdGruppoProdotto());
    costiCommElem.setIdClasseMerceologica(articolo.getIdClasseMerclg());
    costiCommElem.setIdClasseMateriale(articolo.getIdClasseMateriale());
    costiCommElem.getDatiComuniEstesi().setStato(commessa.getDatiComuniEstesi().getStato());

    costiCommElem.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
    costiCommElem.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
    costiCommElem.setTipoRilevazioneRsr(Risorsa.TEMPO);

    try {
      //33950 inizio
      //int rc = costiCommElem.save();
      int rc = 0;
      if(!getConsuntivazioneCommesse().isEstrazioneStoriciCommessa())
    	  rc = costiCommElem.save();
      //33950 fine
      if(rc >= 0) {
        return costiCommElem;
      }
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return null;
  }

  public CostiCommessaDet creaCostiCommessaDet(StoricoCommessa storicoCommessa, CostiCommessaElem costiCommElem, ComponenteCosto compCost, Integer idProgrStoric, boolean temp) {
    CostiCommessaDet costiCommessaDet = new CostiCommessaDet();

    costiCommessaDet.setIdAzienda(costiCommElem.getIdAzienda());
    costiCommessaDet.setIdProgrStoric(idProgrStoric);
    costiCommessaDet.setIdCommessa(costiCommElem.getIdCommessa());
    costiCommessaDet.setIdRigaElem(costiCommElem.getIdRigaElemento());

    if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO)
      costiCommessaDet.setTipoDettaglioCosto(CostiCommessaDet.NORMALE);
    else if(storicoCommessa.getDocumentoOrigine() == StoricoCommessa.ORDINE)
      costiCommessaDet.setTipoDettaglioCosto(CostiCommessaDet.COSTO_ORDINATO);
    else
      costiCommessaDet.setTipoDettaglioCosto(CostiCommessaDet.COSTO_RICHIESTO);

    costiCommessaDet.setIdComponCosto(compCost.getIdComponenteCosto());
    //Fix 04598 Mz inizio
    //costiCommessaDet.setDescrizioneCompCost(compCost.getDescrizione().getDescrizione());
    //Fix 04598 Mz fine
    costiCommessaDet.setCostoLivello(ZERO);
    costiCommessaDet.setCostoLivelliInf(ZERO);
    costiCommessaDet.setCostoTotale(ZERO);
    costiCommessaDet.getDatiComuniEstesi().setStato(costiCommElem.getDatiComuniEstesi().getStato());
    if(!temp) {
      try {
    	//33950 inizio
    	//int rc = costiCommessaDet.save();
    	int rc = 0;
    	if(!getConsuntivazioneCommesse().isEstrazioneStoriciCommessa())
    		rc = costiCommessaDet.save();
    	//33950 fine
        if(rc >= 0) {
          return costiCommessaDet;
        }
      }
      catch(Exception e) {
        e.printStackTrace(Trace.excStream); //...FIX 7430
      }
    }
    else
      return costiCommessaDet;

    return null;
  }

  public void sommaCostoCommessaDettTemp(List costoCommessaDetTempList, List costoDettagli, StoricoCommessa storicoCommessa) {
    for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
      CostiCommessaDet costoCommDetTemp = (CostiCommessaDet)costoCommessaDetTempList.get(i);
      for(int j = 0; j < costoDettagli.size(); j++) {
        DettaglioCosto dettaglioCosto = (DettaglioCosto)costoDettagli.get(j);
        if(costoCommDetTemp.getIdComponCosto().equals(dettaglioCosto.getIdComponenteCosto())) {
          if(storicoCommessa.getTipoRilevazioneRsr() == Risorsa.TEMPO) {
            if(dettaglioCosto.getCostoLivello() != null && dettaglioCosto.getCostoLivelliInf() != null && storicoCommessa.getTempo() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivello().add(dettaglioCosto.getCostoLivelliInf()).multiply(storicoCommessa.getTempo())));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivello() != null && storicoCommessa.getTempo() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivello().multiply(storicoCommessa.getTempo())));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivelliInf() != null && storicoCommessa.getTempo() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivelliInf().multiply(storicoCommessa.getTempo())));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
          }
          else if(storicoCommessa.getTipoRilevazioneRsr() == Risorsa.QUANTITA) {
            if(dettaglioCosto.getCostoLivello() != null && dettaglioCosto.getCostoLivelliInf() != null && storicoCommessa.getQuantita() != null && storicoCommessa.getQtaScarto() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivello().add(dettaglioCosto.getCostoLivelliInf()).multiply(storicoCommessa.getQuantita().add(storicoCommessa.getQtaScarto()))));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivello() != null && storicoCommessa.getQuantita() != null && storicoCommessa.getQtaScarto() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivello().multiply(storicoCommessa.getQuantita().add(storicoCommessa.getQtaScarto()))));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivello() != null && storicoCommessa.getQuantita() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivello().multiply(storicoCommessa.getQuantita())));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivello() != null && storicoCommessa.getQtaScarto() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivello().multiply(storicoCommessa.getQtaScarto())));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivelliInf() != null && storicoCommessa.getQuantita() != null && storicoCommessa.getQtaScarto() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivelliInf().multiply(storicoCommessa.getQuantita().add(storicoCommessa.getQtaScarto()))));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivelliInf() != null && storicoCommessa.getQuantita() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivelliInf().multiply(storicoCommessa.getQuantita())));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
            else if(dettaglioCosto.getCostoLivelliInf() != null && storicoCommessa.getQtaScarto() != null) {
              costoCommDetTemp.setCostoLivello(costoCommDetTemp.getCostoLivello().add(dettaglioCosto.getCostoLivelliInf().multiply(storicoCommessa.getQtaScarto())));
              costoCommDetTemp.setCostoTotale(costoCommDetTemp.getCostoLivello());
            }
          }
        }
      }
    }
  }

  public CostiCommessaDet getApportunoCostoCommessaDet(StoricoCommessa storicoCommessa, List costoCommessaDetTempList) {
    String idComponenteCosto = storicoCommessa.getIdComponenteCosto();
    if(idComponenteCosto != null) {
      for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
        CostiCommessaDet apportunoCostoCommessaDet = (CostiCommessaDet)costoCommessaDetTempList.get(i);
        if(apportunoCostoCommessaDet.getIdComponCosto().equals(idComponenteCosto))
          return apportunoCostoCommessaDet;
      }
    }
    return null;
  }

  public java.util.Hashtable calcoloCosti(List costoCommessaDetTempList, StoricoCommessa storicoCmm, List listLinkCompSchema, String idAmbienteCosti) throws java.sql.SQLException {
    java.util.Hashtable result = new java.util.Hashtable();
    if(costoCommessaDetTempList == null || costoCommessaDetTempList.size() == 0)
      return result;

    BigDecimal costoCalcolato = null;
    BigDecimal costoCalcolatoTot = null;

    List compDaValorizz = new ArrayList();
    List compValorizz = new ArrayList();
    dividereDettagli(compDaValorizz, compValorizz, costoCommessaDetTempList, storicoCmm, listLinkCompSchema);
    boolean found = false;
    //Scorrere gli comp da valorizzare
    while(compDaValorizz.size() != 0) {
      found = false;
      Iterator valorizIte = compDaValorizz.iterator();
      String idComponente = null;
      while(valorizIte.hasNext() && !found) {
        CostiCommessaDet componente = (CostiCommessaDet)valorizIte.next();
        idComponente = componente.getIdComponCosto();
        FormulaCosti formulaCosti = getFormulaToBeUsed(componente.getComponenteCosto(), storicoCmm);
        //determinare la formula per il calcolo del importo
        Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(componente.getComponenteCosto(), idAmbienteCosti)); //Fix 8631
        formulaDaUtilizz.setVariables(buildVariables(costoCommessaDetTempList));
        //Calcolare la formula
        //if(isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente)) {//31513
        if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente)) { //31513
          synchronized(this) {
            if(componente.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE) {
              costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
              if(costoCalcolato == null)
                result.put(idComponente, "THIP11T026");
              else {
                componente.setCostoLivello(costoCalcolato);
                componente.setCostoTotale(costoCalcolato);
              }
            }
            else {
              costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
              if(costoCalcolato == null)
                result.put(idComponente, "THIP11T026");
              componente.setCostoLivello(costoCalcolato);
              componente.setCostoTotale(costoCalcolato);
            }

          }
          //trasferire nel gruppo di Comp valorizz
          compDaValorizz.remove(componente);
          compValorizz.add(componente);
          found = true;

        }
      }
      if(!found) {
        result.put(idComponente, "THIP11T017");
      }
    }
    return result;
  }

  public java.util.Hashtable calcoloCostiTotali(List costoCommessaDetTempList, StoricoCommessa storicoCmm, List listLinkCompSchema, String idAmbienteCosti) throws java.sql.SQLException {
    java.util.Hashtable result = new java.util.Hashtable();
    if(costoCommessaDetTempList == null || costoCommessaDetTempList.size() == 0)
      return result;

    BigDecimal costoCalcolato = null;
    BigDecimal costoCalcolatoTot = null;

    List compDaValorizz = new ArrayList();
    List compValorizz = new ArrayList();
    dividereDettagli2(compDaValorizz, compValorizz, costoCommessaDetTempList, storicoCmm, listLinkCompSchema);
    boolean found = false;
    //Scorrere gli comp da valorizzare
    while(compDaValorizz.size() != 0) {
      found = false;
      Iterator valorizIte = compDaValorizz.iterator();
      String idComponente = null;
      while(valorizIte.hasNext() && !found) {
        CostiCommessaDet componente = (CostiCommessaDet)valorizIte.next();
        idComponente = componente.getIdComponCosto();
        FormulaCosti formulaCosti = getFormulaToBeUsed(componente.getComponenteCosto(), storicoCmm);
        //determinare la formula per il calcolo del importo
        Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(componente.getComponenteCosto(), idAmbienteCosti)); //Fix 8631
        formulaDaUtilizz.setVariables(buildVariables(costoCommessaDetTempList));
        //Calcolare la formula
        //if(isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente)) {//31513
        if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente)) { //31513
          synchronized(this) {
            if(componente.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE) {
              costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
              if(costoCalcolato == null)
                result.put(idComponente, "THIP11T026");
              else {
                componente.setCostoLivello(costoCalcolato);
                componente.setCostoTotale(costoCalcolato);
              }
            }
            else {
              costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
              if(costoCalcolato == null)
                result.put(idComponente, "THIP11T026");
              componente.setCostoLivello(costoCalcolato);
              componente.setCostoTotale(costoCalcolato);
            }

          }
          //trasferire nel gruppo di Comp valorizz
          compDaValorizz.remove(componente);
          compValorizz.add(componente);
          found = true;

        }
      }
      if(!found) {
        result.put(idComponente, "THIP11T017");
      }
    }
    return result;
  }

  public java.util.Hashtable calcoloCostiInternal(List costoCommessaDetTempList, StoricoCommessa storicoCmm, List listLinkCompSchema, String idAmbienteCosti) throws java.sql.SQLException {
    java.util.Hashtable result = new java.util.Hashtable();
    if(costoCommessaDetTempList == null || costoCommessaDetTempList.size() == 0)
      return result;

    BigDecimal costoCalcolato = null;
    BigDecimal costoCalcolatoTot = null;

    List compDaValorizz = new ArrayList();
    List compValorizz = new ArrayList();
    dividereDettagli(compDaValorizz, compValorizz, costoCommessaDetTempList, storicoCmm, listLinkCompSchema);
    boolean found = false;
    //Scorrere gli comp da valorizzare

    while(!compDaValorizz.isEmpty()) {
      found = false;
      Iterator valorizIte = compDaValorizz.iterator();
      String idComponente = null;
      while(valorizIte.hasNext() && !found) {
        CostiCommessaDet componente = (CostiCommessaDet)valorizIte.next();
        idComponente = componente.getIdComponCosto();
        FormulaCosti formulaCosti = getFormulaToBeUsed(componente.getComponenteCosto(), storicoCmm);
        //determinare la formula per il calcolo del importo
        Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(componente.getComponenteCosto(), idAmbienteCosti)); //Fix 8631
        formulaDaUtilizz.setVariables(buildVariables(costoCommessaDetTempList));
        //Calcolare la formula
        boolean recalcolaCompomenente = false;
        //if(isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente)) {//31513
        if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente)) { //31513
          synchronized(this) {
            if(componente.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE) {
              costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
              if(costoCalcolato == null)
                result.put(idComponente, "THIP11T026");
              else {
                recalcolaCompomenente = (Utils.compare(costoCalcolato, componente.getCostoLivello()) != 0);
                componente.setCostoLivello(costoCalcolato);
                componente.setCostoTotale(costoCalcolato);
              }
            }
            else {
              costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
              if(costoCalcolato == null)
                result.put(idComponente, "THIP11T026");
              else
                recalcolaCompomenente = (Utils.compare(costoCalcolato, componente.getCostoLivello()) != 0);

              componente.setCostoLivello(costoCalcolato);
              componente.setCostoTotale(costoCalcolato);
            }

          }
          //trasferire nel gruppo di Comp valorizz
          if(!recalcolaCompomenente) {
            compDaValorizz.remove(componente);
            compValorizz.add(componente);
          }
          found = true;
        }
      }
      if(!found) {
        result.put(idComponente, "THIP11T017");
      }
    }
    return result;
  }

  /**
   * dividere la lista delgi dettagli in due liste : Componente Valorizzati e Componente Da Valorizzare
   * @param compDaValorizz List
   * @param compValorizz List
   * @param costoCommessaDetTempList List
   * @param storicoCmm StoricoCommessa
   * @param listLinkCompSchema List
   */
  protected void dividereDettagli(List compDaValorizz, List compValorizz, List costoCommessaDetTempList, StoricoCommessa storicoCmm, List listLinkCompSchema) {
    if(compDaValorizz == null)
      compDaValorizz = new ArrayList();
    if(compValorizz == null)
      compValorizz = new ArrayList();

    List compList = costoCommessaDetTempList;
    CostiCommessaDet dettCompCosto = null;
    //Iterator compIte = compList.iterator();
    // sono da valorizzare i componente 'F' o 'T' con la formula non disabilita da schema costo
    for(int i = 0; i < compList.size(); i++) {
      dettCompCosto = (CostiCommessaDet)compList.get(i);
      for(int j = 0; j < listLinkCompSchema.size(); j++) {
        LinkCompSchema linkCompCosto = (LinkCompSchema)listLinkCompSchema.get(j);
        //if ( dettCompCosto.getComponenteCosto().getFormula() != null && utilizzoFormula(dettCompCosto.getIdComponCosto(), true))
        if(dettCompCosto.getIdComponCosto().equals(linkCompCosto.getIdComponenteCosto())) {
          if(linkCompCosto.isUtilizzoFormula() && ((dettCompCosto.getComponenteCosto().getProvenienza() == 'T') || (dettCompCosto.getComponenteCosto().getProvenienza() == 'F'
            && (storicoCmm.getTipoRigaOrigine() == StoricoCommessa.ACQUISTO
            || storicoCmm.getTipoRigaOrigine() == StoricoCommessa.LAVORAZIONE_ESTERNA_PRODOTTO
            || storicoCmm.getTipoRigaOrigine() == StoricoCommessa.SERVIZIO_RISORSA //Fix 10416
            || storicoCmm.getTipoRigaOrigine() == StoricoCommessa.PRODUZIONE_RISORSA))))
            compDaValorizz.add(dettCompCosto);
          else
            compValorizz.add(dettCompCosto);
        }
      }
    }
  }

  /**
   * dividere la lista delgi dettagli in due liste : Componente Valorizzati e Componente Da Valorizzare
   * @param compDaValorizz List
   * @param compValorizz List
   * @param costoCommessaDetTempList List
   * @param storicoCmm StoricoCommessa
   * @param listLinkCompSchema List
   */
  protected void dividereDettagli2(List compDaValorizz, List compValorizz, List costoCommessaDetTempList, StoricoCommessa storicoCmm, List listLinkCompSchema) {
    if(compDaValorizz == null)
      compDaValorizz = new ArrayList();
    if(compValorizz == null)
      compValorizz = new ArrayList();

    List compList = costoCommessaDetTempList;
    CostiCommessaDet dettCompCosto = null;
    //Iterator compIte = compList.iterator();
    // sono da valorizzare i componente 'F' o 'T' con la formula non disabilita da schema costo
    for(int i = 0; i < compList.size(); i++) {
      dettCompCosto = (CostiCommessaDet)compList.get(i);
      for(int j = 0; j < listLinkCompSchema.size(); j++) {
        LinkCompSchema linkCompCosto = (LinkCompSchema)listLinkCompSchema.get(j);
        //if ( dettCompCosto.getComponenteCosto().getFormula() != null && utilizzoFormula(dettCompCosto.getIdComponCosto(), true))
        if(dettCompCosto.getIdComponCosto().equals(linkCompCosto.getIdComponenteCosto())) {
          if(linkCompCosto.isUtilizzoFormula() && dettCompCosto.getComponenteCosto().getProvenienza() == 'T')
            compDaValorizz.add(dettCompCosto);
          else
            compValorizz.add(dettCompCosto);
        }
      }
    }
  }

  public FormulaCosti getFormulaToBeUsed(ComponenteCosto componente, StoricoCommessa storicoCmm) {
    FormulaCosti formulaToUse = null;
    if(componente == null)
      return formulaToUse;
    else {
      PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
      if(storicoCmm.getArticolo().getClasseMerclg() != null && storicoCmm.getArticolo().getClasseMerclg().getSchemaCosto() != null) {
        if(storicoCmm.getArticolo().getClasseMerclg().getSchemaCosto().getIdFormulaCostoRif() != null && componente.equals(p.getRiferimento())) {
          formulaToUse = storicoCmm.getArticolo().getClasseMerclg().getSchemaCosto().getFormulaCostoRif();
        }
        else {
          formulaToUse = componente.getFormula();
        }
      }
      else {
        formulaToUse = componente.getFormula();
      }
    }
    return formulaToUse;
  }

  public void updateTotales(List costoCommessaDetTempList) {
    CostiCommessaDet dettCosto;
    List dettagli = costoCommessaDetTempList;
    Iterator dettIter = dettagli.iterator();
    BigDecimal totalCosti = ZERO;
    while(dettIter.hasNext()) {
      dettCosto = (CostiCommessaDet)dettIter.next();
      //updateTotales
      if(dettCosto.getCostoLivello() != null && dettCosto.getCostoLivelliInf() != null)
        totalCosti = dettCosto.getCostoLivello().add(dettCosto.getCostoLivelliInf());
      else if(dettCosto.getCostoLivello() != null)
        totalCosti = dettCosto.getCostoLivello();
      else if(dettCosto.getCostoLivelliInf() != null)
        totalCosti = dettCosto.getCostoLivelliInf();
      dettCosto.setCostoTotale(totalCosti);
    }
  }

  //Fix 04171 Mz inizio
  public void reAggiustoDettagli(List costoCommessaDetTempList) {
    CostiCommessaElem costiCommessaElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
    costiCommessaElem.reAggiustoDettagli(costoCommessaDetTempList);
  }

  //Fix 04171 Mz fine

  public void aggiornaStoricoCommessa(StoricoCommessa storicoCmm, List costoCommessaDetTempList) {
    //Fix 04171 Mz inizio
    reAggiustoDettagli(costoCommessaDetTempList);
    //Fix 04171 Mz fine
    CostiCommessaDet costiCmmDetTempRif = getCostiCommessaDetTempRif(costoCommessaDetTempList);
    BigDecimal qtaToUse = storicoCmm.getQuantitaUMPrm();
    if(qtaToUse == null || (qtaToUse != null && qtaToUse.compareTo(ZERO) == 0))
      qtaToUse = storicoCmm.getQuantitaUMAcqVen();

    if(qtaToUse != null && storicoCmm.getQtaScarto() != null) {
      BigDecimal qta = qtaToUse.add(storicoCmm.getQtaScarto());
      if(qta.compareTo(ZERO) != 0)
        storicoCmm.setCostoUnitario(abs(costiCmmDetTempRif.getCostoLivello().divide(qta, 12, BigDecimal.ROUND_HALF_DOWN)));
    }
    else {
      if(qtaToUse != null && qtaToUse.compareTo(ZERO) != 0)
        storicoCmm.setCostoUnitario(abs(costiCmmDetTempRif.getCostoLivello().divide(qtaToUse, 12, BigDecimal.ROUND_HALF_DOWN)));
      else {
        if(storicoCmm.getQtaScarto() != null && storicoCmm.getQtaScarto().compareTo(ZERO) != 0)
          storicoCmm.setCostoUnitario(abs(costiCmmDetTempRif.getCostoLivello().divide(storicoCmm.getQtaScarto(), 12, BigDecimal.ROUND_HALF_DOWN)));
      }
    }

    if(storicoCmm.getValorizzaCosto() == StoricoCommessa.INCREMENTA_COSTI_INDIRETTI)
      storicoCmm.setCostoTotale(costiCmmDetTempRif.getCostoLivello());

    try {
      storicoCmm.save();
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }
  }

  public CostiCommessaDet getCostiCommessaDetTempRif(List costoCommessaDetTempList) {
    PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
    ComponenteCosto ComponenteRiferimento = p.getRiferimento();
    CostiCommessaDet costiCmmDetTemp = null;
    for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
      costiCmmDetTemp = (CostiCommessaDet)costoCommessaDetTempList.get(i);
      if(costiCmmDetTemp.getIdComponCosto().equals(ComponenteRiferimento.getIdComponenteCosto()))
        return costiCmmDetTemp;
    }
    return costiCmmDetTemp;
  }

  public List getCostoCommessaDetIndirect(List costoCommessaDetTempList, List listLinkCompSchema) {
    List CostoCommDetIndirect = new ArrayList();
    CostiCommessaDet costiCmmDetTemp = null;
    for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
      costiCmmDetTemp = (CostiCommessaDet)costoCommessaDetTempList.get(i);
      ComponenteCosto compCosto = costiCmmDetTemp.getComponenteCosto();
      LinkCompSchema linkCompCosto = (LinkCompSchema)listLinkCompSchema.get(i);
      if(compCosto.getTipoComponente() != ComponenteCosto.DIRETTA
        && compCosto.getProvenienza() == ComponenteCosto.CALCOLATA_FORMULA
        && linkCompCosto.isUtilizzoFormula())
        CostoCommDetIndirect.add(costiCmmDetTemp);
    }
    return CostoCommDetIndirect;
  }

  public CostiCommessaElem getCostiCmmElemPrevisto(StoricoCommessa storicoCommessa) {
    CostiCommessaElem CostiCmmElemPrev = null;
    Vector CostiCmmPrevList = new Vector();
    String clauseWhere = CostiCommessaTM.ID_AZIENDA + " = '" + storicoCommessa.getIdAzienda() + "' AND " +
      CostiCommessaTM.ID_COMMESSA + " = '" + storicoCommessa.getIdCommessa() + "' AND " +
      CostiCommessaTM.TIPOLOGIA + " = '" + CostiCommessa.COSTO_PREVISTO + "' AND " +
      CostiCommessaTM.UFFICIALE + " = 'Y'";

    try {
      CostiCmmPrevList = CostiCommessa.retrieveList(clauseWhere, "", true);
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }

    if(CostiCmmPrevList.size() == 1) {
      Vector CostiCmmElemPrevList = new Vector();
      CostiCommessa CostiCmmPrev = (CostiCommessa)CostiCmmPrevList.get(0);
      clauseWhere = CostiCommessaElemTM.ID_AZIENDA + " = '" + storicoCommessa.getIdAzienda() + "' AND " +
        CostiCommessaElemTM.ID_PROGR_STORIC + " =" + CostiCmmPrev.getIdProgrStoric() + " AND " +
        CostiCommessaElemTM.ID_COMMESSA + " = '" + CostiCmmPrev.getIdCommessa() + "' AND " +
        CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "' AND " +
        CostiCommessaElemTM.R_ARTICOLO + " = '" + storicoCommessa.getIdArticolo() + "' AND " +
        CostiCommessaElemTM.R_VERSIONE + " =" + storicoCommessa.getIdVersione();
      if(storicoCommessa.getIdConfigurazione() != null && storicoCommessa.getIdStabilimento() != null) {
        clauseWhere = clauseWhere + " AND " +
          CostiCommessaElemTM.R_CONFIGURAZIONE + " =" + storicoCommessa.getIdConfigurazione() + " AND " +
          CostiCommessaElemTM.R_STABILIMENTO + " = '" + storicoCommessa.getIdStabilimento() + "'";
      }
      else {
        if(storicoCommessa.getIdStabilimento() != null)
          clauseWhere = clauseWhere + " AND " +
            CostiCommessaElemTM.R_STABILIMENTO + " = '" + storicoCommessa.getIdStabilimento() + "'";
        else
          if(storicoCommessa.getIdConfigurazione() != null)
            clauseWhere = clauseWhere + " AND " +
              CostiCommessaElemTM.R_CONFIGURAZIONE + " =" + storicoCommessa.getIdConfigurazione();
      }
      try {
        CostiCmmElemPrevList = CostiCommessaElem.retrieveList(clauseWhere, "", true);
      }
      catch(Exception e) {
        e.printStackTrace(Trace.excStream); //...FIX 7430
      }

      if(CostiCmmElemPrevList.size() < 1) {
        CostiCmmElemPrev = null;
      }
      else {
        CostiCmmElemPrev = (CostiCommessaElem)CostiCmmElemPrevList.get(0);
      }
    }
    return CostiCmmElemPrev;
  }

  public void aggiornaCostoCommessaDetTempListConPrevisti(List costoCommessaDetTempList, CostiCommessaElem costiCmmElemPrevisto) {
    List costiCmmDetPrvstoList = costiCmmElemPrevisto.getCostiCommessaDet();
    CostiCommessaDet costiCommessaTmp = null;
    CostiCommessaDet costiCommessaDetPrevisto = null;
    for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
      costiCommessaTmp = (CostiCommessaDet)costoCommessaDetTempList.get(i);
      for(int j = 0; j < costiCmmDetPrvstoList.size(); j++) {
        costiCommessaDetPrevisto = (CostiCommessaDet)costiCmmDetPrvstoList.get(j);
        if(costiCommessaTmp.getComponenteCosto().getIdComponenteCosto().equals(costiCommessaDetPrevisto.getComponenteCosto().getIdComponenteCosto())) {
          costiCommessaTmp.setCostoLivello(costiCommessaDetPrevisto.getCostoLivello());
          costiCommessaTmp.setCostoTotale(costiCommessaDetPrevisto.getCostoTotale());
        }
      }
    }
  }

  public void calcolaFormula(List costiCommDetIndirectList, List compValorizz, List costoCommessaDetList, StoricoCommessa storicoCmm, String idAmbienteCosti) {
    String idComponente = null;
    BigDecimal costoCalcolato = ZERO;
    for(int i = 0; i < costiCommDetIndirectList.size(); i++) {
      CostiCommessaDet componente = (CostiCommessaDet)costiCommDetIndirectList.get(i);
      idComponente = componente.getIdComponCosto();
      FormulaCosti formulaCosti = getFormulaToBeUsed(componente.getComponenteCosto(), storicoCmm);
      //determinare la formula per il calcolo del importo
      Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(componente.getComponenteCosto(), idAmbienteCosti)); //Fix 8631
      formulaDaUtilizz.setVariables(buildVariables(costoCommessaDetList));
      //Calcolare la formula
      synchronized(this) {
        costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
        componente.setCostoLivello(costoCalcolato);
        componente.setCostoTotale(costoCalcolato);
      }
      //costiCommDetIndirectList.remove(componente);
      compValorizz.add(componente);
    }
  }

  /* protected VariablesCollection buildVariablesInternal(List costoCommessaDetTempList)
   {
     VariabiliCosti variableCollection = new VariabiliCosti();
     List componentiCosto = getTuttiComponentiCosto();

     Iterator iter = componentiCosto.iterator();
     while (iter.hasNext()) {
       ComponenteCosto componenteCosto = (ComponenteCosto)iter.next();
       FunctionVariable variable = new FunctionVariable(componenteCosto.getIdComponenteCosto(),
           componenteCosto.getDescrizione().getDescrizioneRidotta(),
           getValore(costoCommessaDetTempList, componenteCosto.getIdComponenteCosto()),
           ExpressionTypes.NUMBER);
       variableCollection.addVariable(variable);
     }

     return variableCollection;
   }*/

  public List getTuttiComponentiCosto() {
    String where = ComponenteCostoTM.ID_AZIENDA + " = '" + iConsuntivazioneCommesse.getIdAzienda() + "'";
    try {
      return ComponenteCosto.retrieveList(where, "", false);
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }

    return new Vector();
  }

  public BigDecimal getValore(List dettagliCosto, String idComponCosto) {
    BigDecimal valore = null;
    CostiCommessaDet dettaglio = getCostoCommessaDettaglio(dettagliCosto, idComponCosto);
    if(dettaglio != null) {
      valore = dettaglio.getCostoTotale();
    }

    return(valore == null) ? ZERO : valore;
  }

  public static CostiCommessaDet getCostoCommessaDettaglio(List dettagliCosto, String idComponCosto) {
    for(Iterator i = dettagliCosto.iterator(); i.hasNext(); ) {
      CostiCommessaDet dettaglio = (CostiCommessaDet)i.next();
      if(Utils.compare(dettaglio.getIdComponCosto(), idComponCosto) == 0)
        return dettaglio;
    }

    return null;
  }

  protected VariablesCollection buildVariables(List costoCommessaDetTempList) {
    //return buildVariablesInternal(costoCommessaDetTempList);

    //Fix 10411 inizio
    // non ricaricate da database ogni volta le componenti di costo
    VariabiliCosti variableCollection = null;
    if(iConsuntivazioneCommesse != null)
       variableCollection = iConsuntivazioneCommesse.getVariabiliCosti();
    else
       variableCollection = new VariabiliCosti();
    //VariabiliCosti variableCollection = new VariabiliCosti();
    //Fix 10411 fine

    for(Iterator i = variableCollection.getVariablesList().iterator(); i.hasNext(); ) {
      FunctionVariable var = (FunctionVariable)i.next();
      if(var.getValue() == null)
        var.setValue(ZERO);
    }

    try {
      Iterator iter = costoCommessaDetTempList.iterator();
      FunctionVariable variables;
      FunctionVariable variablesL;
      FunctionVariable variablesI;
      ComponenteCosto compCosto;
      CostiCommessaDet costiCmmDet;
      while(iter.hasNext()) {
        costiCmmDet = (CostiCommessaDet)iter.next();
        compCosto = costiCmmDet.getComponenteCosto();
        variables = new FunctionVariable(compCosto.getIdComponenteCosto(), compCosto.getDescrizione().getDescrizioneRidotta(), null, ExpressionTypes.NUMBER);
        variablesL = new FunctionVariable(compCosto.getIdComponenteCosto() + VariabiliCommessaElem.LIVELLO, compCosto.getDescrizione().getDescrizioneRidotta() + " " + ResourceLoader.getString(LIVELLO_RES, "Livello"), null, ExpressionTypes.NUMBER);
        variablesI = new FunctionVariable(compCosto.getIdComponenteCosto() + VariabiliCommessaElem.LIVELLO_INFERIORE, compCosto.getDescrizione().getDescrizioneRidotta() + " " + ResourceLoader.getString(LIVELLO_RES, "LivelloInferiore"), null, ExpressionTypes.NUMBER);
        variables.setType(ExpressionTypes.NUMBER);
        variables.setValue(costiCmmDet.getCostoTotale());
        variablesL.setType(ExpressionTypes.NUMBER);
        variablesL.setValue(costiCmmDet.getCostoLivello());
        variablesI.setType(ExpressionTypes.NUMBER);
        variablesI.setValue(costiCmmDet.getCostoLivelliInf());
        variableCollection.addVariable(variables);
        variableCollection.addVariable(variablesL);
        variableCollection.addVariable(variablesI);
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    return variableCollection;

  }

  public List getCompValorizz(List costiCommDetIndirect, CostiCommessaElem costiCmmElemPrevisto) {
    List CompValorizz = new ArrayList();
    List costiCommessaDetPrevistoList = costiCmmElemPrevisto.getCostiCommessaDet();
    CostiCommessaDet costiCommDetIndirectObj = null;
    CostiCommessaDet costiCommessaDetPrevisto = null;

    for(int i = 0; i < costiCommessaDetPrevistoList.size(); i++) {
      boolean exist = false;
      costiCommessaDetPrevisto = (CostiCommessaDet)costiCommessaDetPrevistoList.get(i);
      for(int j = 0; j < costiCommDetIndirect.size(); j++) {
        costiCommDetIndirectObj = (CostiCommessaDet)costiCommDetIndirect.get(j);
        if(costiCommessaDetPrevisto.getComponenteCosto().getIdComponenteCosto().equals(costiCommDetIndirectObj.getComponenteCosto().getIdComponenteCosto()))
          exist = true;
      }
      if(!exist)
        CompValorizz.add(costiCommessaDetPrevisto);
    }
    return CompValorizz;
  }

  /**
   * valorizzaCostoLivello
   * @param costiCommDetIndirect List
   * @param costiCmmElemPrevisto CostiCommessaElem
   * @param storicoCommessa StoricoCommessa
   * @param costoCommessaDetTempList List
   */
  //...FIX 4709 modificato per la gestione della Lavorazione esterna
  public void valorizzaCostoLivello(List costiCommDetIndirect, CostiCommessaElem costiCmmElemPrevisto, StoricoCommessa storicoCommessa, List costoCommessaDetTempList) {
    for(int i = 0; i < costiCommDetIndirect.size(); i++) {
      CostiCommessaDet costoCommDetIndct = (CostiCommessaDet)costiCommDetIndirect.get(i);
      BigDecimal resultaFormula = costoCommDetIndct.getCostoLivello() != null ? costoCommDetIndct.getCostoLivello() : ZERO;

      if(storicoCommessa.getQuantitaUMPrm() != null && storicoCommessa.getQtaScarto() != null && costiCmmElemPrevisto.getQuantita().compareTo(ZERO) != 0) {
        if(storicoCommessa.getQtaScarto().compareTo(ZERO) != 0 && storicoCommessa.getQuantitaUMPrm().compareTo(ZERO) != 0) {
          BigDecimal costoLivello = (storicoCommessa.getQtaScarto().add(storicoCommessa.getQuantitaUMPrm())).divide(costiCmmElemPrevisto.getQuantita(), 12, BigDecimal.ROUND_HALF_DOWN);
          costoCommDetIndct.setCostoLivello(costoLivello.multiply(resultaFormula));
          costoCommDetIndct.setCostoTotale(costoLivello.multiply(resultaFormula));
        }
        else {
          costoCommDetIndct.setCostoLivello(ZERO);
          costoCommDetIndct.setCostoLivello(ZERO);
        }
      }
      else if(storicoCommessa.getQuantitaUMPrm() != null && costiCmmElemPrevisto.getQuantita().compareTo(ZERO) != 0) {
        if(storicoCommessa.getQuantitaUMPrm().compareTo(ZERO) != 0) {
          BigDecimal costoLivello = (storicoCommessa.getQuantitaUMPrm()).divide(costiCmmElemPrevisto.getQuantita(), 12, BigDecimal.ROUND_HALF_DOWN);
          costoCommDetIndct.setCostoLivello(costoLivello.multiply(resultaFormula));
          costoCommDetIndct.setCostoTotale(costoLivello.multiply(resultaFormula));
        }
        else {
          costoCommDetIndct.setCostoLivello(ZERO);
          costoCommDetIndct.setCostoLivello(ZERO);
        }
      }
      else if(storicoCommessa.getQtaScarto() != null && costiCmmElemPrevisto.getQuantita().compareTo(ZERO) != 0) {
        if(storicoCommessa.getQtaScarto().compareTo(ZERO) != 0) {
          BigDecimal costoLivello = (storicoCommessa.getQtaScarto().add(storicoCommessa.getQuantitaUMPrm())).divide(costiCmmElemPrevisto.getQuantita(), 12, BigDecimal.ROUND_HALF_DOWN);
          costoCommDetIndct.setCostoLivello(costoLivello.multiply(resultaFormula));
          costoCommDetIndct.setCostoTotale(costoLivello.multiply(resultaFormula));
        }
        else {
          costoCommDetIndct.setCostoLivello(ZERO);
          costoCommDetIndct.setCostoLivello(ZERO);
        }
      }
      else {
        costoCommDetIndct.setCostoLivello(ZERO);
        costoCommDetIndct.setCostoLivello(ZERO);
      }
    }
  }

  public void valorizzaCostoCommDetTotale(List costiCommDetTotaleList, List costoCommessaDetTempList, List compValorizz, StoricoCommessa storicoCmm, String idAmbienteCosti, List listLinkCompSchema) { //...FIX 4693 (LP)
    for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
      CostiCommessaDet costiCommessaDet = (CostiCommessaDet)costoCommessaDetTempList.get(i);
      LinkCompSchema linkCompCosto = (LinkCompSchema)listLinkCompSchema.get(i); //...FIX 4693 (LP)
      if(costiCommessaDet.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI ||
        costiCommessaDet.getComponenteCosto().getTipoComponente() == ComponenteCosto.DIRETTA ||
        !linkCompCosto.isUtilizzoFormula()) { //...FIX 4693 (LP)
        costiCommessaDet.setCostoLivello(ZERO);
        costiCommessaDet.setCostoTotale(ZERO);
      }
    }

    Iterator valorizIte = costiCommDetTotaleList.iterator();
    String idComponente = null;
    BigDecimal costoCalcolato = ZERO;
    while(valorizIte.hasNext()) {
      CostiCommessaDet componente = (CostiCommessaDet)valorizIte.next();
      idComponente = componente.getIdComponCosto();
      FormulaCosti formulaCosti = getFormulaToBeUsed(componente.getComponenteCosto(), storicoCmm);
      //determinare la formula per il calcolo del importo
      Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(componente.getComponenteCosto(), idAmbienteCosti)); //Fix 8631
      formulaDaUtilizz.setVariables(buildVariables(costoCommessaDetTempList));
      //Calcolare la formula
      synchronized(this) {
        costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
        componente.setCostoLivello(costoCalcolato);
        componente.setCostoTotale(costoCalcolato);
      }
      //costiCommDetTotaleList.remove(componente);
      compValorizz.add(componente);
    }
  }

  public List getCostiCommDetTotaleList(List costoCommessaDetTempList, List listLinkCompSchema) {
    List CostoCommDetTotaleList = new ArrayList();
    CostiCommessaDet costiCmmDetTemp = null;
    for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
      costiCmmDetTemp = (CostiCommessaDet)costoCommessaDetTempList.get(i);
      ComponenteCosto compCosto = costiCmmDetTemp.getComponenteCosto();
      LinkCompSchema linkCompCosto = (LinkCompSchema)listLinkCompSchema.get(i);
      if(compCosto.getProvenienza() == ComponenteCosto.SOLO_TOTALE
        && linkCompCosto.isUtilizzoFormula())
        CostoCommDetTotaleList.add(costiCmmDetTemp);
    }
    return CostoCommDetTotaleList;
  }

  public void recuperaCostoCommessaDetTempList(List costiCommDetIndirect, List costiCommDetTotaleList, List costoCommessaDetTempList, List listLinkCompSchema) {
    CostiCommessaDet costiCmmDetTemp = null;
    for(int i = 0; i < costiCommDetTotaleList.size(); i++) {
      costiCmmDetTemp = (CostiCommessaDet)costoCommessaDetTempList.get(i);
      ComponenteCosto compCosto = costiCmmDetTemp.getComponenteCosto();
      LinkCompSchema linkCompCosto = (LinkCompSchema)listLinkCompSchema.get(i);
      if((compCosto.getProvenienza() == ComponenteCosto.SOLO_TOTALE && linkCompCosto.isUtilizzoFormula()) || (compCosto.getTipoComponente() != ComponenteCosto.DIRETTA
        && compCosto.getProvenienza() == ComponenteCosto.CALCOLATA_FORMULA
        && linkCompCosto.isUtilizzoFormula())) {
        costiCommDetTotaleList.remove(costiCmmDetTemp);
      }
    }
    for(int i = 0; i < costiCommDetIndirect.size(); i++) {
      costiCmmDetTemp = (CostiCommessaDet)costiCommDetIndirect.get(i);
      costiCommDetTotaleList.add(costiCmmDetTemp);
    }
    for(int i = 0; i < costiCommDetTotaleList.size(); i++) {
      costiCmmDetTemp = (CostiCommessaDet)costiCommDetTotaleList.get(i);
      costiCommDetTotaleList.add(costiCmmDetTemp);
    }

  }

  public boolean sommaCostiToPersistentDet(List costoCommessaDetTempList, CostiCommessaElem costiCommessaElem, List costoCommessaDetNouvoCreatedList) {
    List costoCommessaDetPerstList = new ArrayList();
    if(costoCommessaDetNouvoCreatedList.isEmpty())
      costoCommessaDetPerstList = costiCommessaElem.getCostiCommessaDet();
    else
      costoCommessaDetPerstList = costoCommessaDetNouvoCreatedList;

    CostiCommessaDet costiCmmDetPerst = null;
    CostiCommessaDet costiCmmDetTemp = null;

    if(costoCommessaDetPerstList.size() > 0 && costoCommessaDetTempList.size() > 0) {
       //Fix 10411 inizio
       //evitata doppia iterazione per rintracciare elementi (in getPersistFromTemp)
       //creo mappa per chiaveper avere accesso immediato valori
       Map persMap = new HashMap();
       Iterator persIter = costoCommessaDetPerstList.iterator();
       while(persIter.hasNext())
       {
         CostiCommessaDet cur = (CostiCommessaDet)persIter.next();
         String curKey = cur.getKey();
         persMap.put(curKey, cur);
       }
       //Fix 10411 fine

       Iterator iter = costoCommessaDetTempList.iterator();
       while(iter.hasNext()) {
         CostiCommessaDet temp = (CostiCommessaDet)iter.next();
         String tempKey = temp.getKey();//Fix 10411
         CostiCommessaDet persist = (CostiCommessaDet)persMap.get(tempKey); //Fix 10411
         //CostiCommessaDet persist = getPersistFromTemp(costoCommessaDetPerstList, temp);//Fix 10411
         saveCostiCommessaDet(persist, temp);
       }
    }
//    if(costoCommessaDetPerstList.size() >0 && costoCommessaDetTempList.size() >0){
    /*
//      CostiCommessaDet temp = (CostiCommessaDet)costoCommessaDetTempList.get(0);
//      int start = -1;
//      for (int i = 0; i < costoCommessaDetPerstList.size(); i++)
//      {
//        CostiCommessaDet perst = (CostiCommessaDet)costoCommessaDetPerstList.get(i);
//        if (perst.getTipoDettaglioCosto() == temp.getTipoDettaglioCosto())
//          start = i;
//      }
//
//     if (start != -1){
         if(((CostiCommessaDet)costoCommessaDetPerstList.get(0)).getTipoDettaglioCosto() == ((CostiCommessaDet)costoCommessaDetTempList.get(0)).getTipoDettaglioCosto()){
          for(int i=0;i<costoCommessaDetPerstList.size();i++){
          //for(int i=start;i<costoCommessaDetPerstList.size();i++){
           costiCmmDetPerst = (CostiCommessaDet)costoCommessaDetPerstList.get(i);
           for(int j=0;j<costoCommessaDetTempList.size();j++){
            costiCmmDetTemp = (CostiCommessaDet)costoCommessaDetTempList.get(j);
            if(costiCmmDetPerst.getIdComponCosto().equals(costiCmmDetTemp.getIdComponCosto())){
              if(costiCmmDetTemp.getCostoTotale().compareTo(ZERO)!=0){
                if(costiCmmDetPerst.getCostoTotale() != null && costiCmmDetPerst.getCostoLivello() != null){
                 costiCmmDetPerst.setCostoLivello(costiCmmDetPerst.getCostoLivello().add(costiCmmDetTemp.getCostoLivello()));
                 costiCmmDetPerst.setCostoTotale(costiCmmDetPerst.getCostoTotale().add(costiCmmDetTemp.getCostoTotale()));
                }else{
                 costiCmmDetPerst.setCostoLivello(costiCmmDetTemp.getCostoLivello());
                 costiCmmDetPerst.setCostoTotale(costiCmmDetTemp.getCostoTotale());
                }
                try{
                  int rc = costiCmmDetPerst.save();
                  if(rc<0)
                    return false;
                }
                catch(Exception e){
                  e.printStackTrace(Trace.excStream); //...FIX 7430
                }
              }
            }
           }
          }
         }
         else{
           for(int j=0;j<costoCommessaDetTempList.size();j++){
             costiCmmDetTemp = (CostiCommessaDet)costoCommessaDetTempList.get(j);
             try{
               costiCmmDetTemp.save();
             }
             catch(Exception e){
              e.printStackTrace(Trace.excStream); //...FIX 7430
             }
           }
         }
        }
     */
    return true;
  }

  public int saveCostiCommessaDet(CostiCommessaDet persist, CostiCommessaDet temp) {
    if(persist != null) {
      if(temp.getCostoTotale().compareTo(ZERO) != 0) {
        if(persist.getCostoTotale() != null && persist.getCostoLivello() != null) {
          //Fix 04171 Mz inizio
          //
          //persist.setCostoLivello(persist.getCostoLivello().add(temp.getCostoLivello()));
          //persist.setCostoTotale(persist.getCostoTotale().add(temp.getCostoTotale()));
          persist.setCostoLivello(sum(persist.getCostoLivello(), temp.getCostoLivello()));
          persist.setCostoTotale(sum(persist.getCostoTotale(), temp.getCostoTotale()));
          //Fix 04171 Mz fine
        }
        else {
          persist.setCostoLivello(temp.getCostoLivello());
          persist.setCostoTotale(temp.getCostoTotale());
        }
      }
    }

    try {
      if(persist == null) {
         correggiScaleCosti(temp); //Fix 10155
         //33950 inizio
         if(iConsuntivazioneCommesse.isEstrazioneStoriciCommessa())
        	 return ErrorCodes.NO_ROWS_FOUND;
         else 
         //33950 fine
        	 return temp.save();
      }
      else {
         correggiScaleCosti(persist);  //Fix 10155
         //33950 inizio
         if(iConsuntivazioneCommesse.isEstrazioneStoriciCommessa())
        	 return ErrorCodes.NO_ROWS_FOUND;
         else 
         //33950 fine
        	 return persist.save();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }

    return ErrorCodes.NO_ROWS_FOUND;
  }

  //Fix 10155 inizio
  protected void correggiScaleCosti(CostiCommessaDet det)
  {
     BigDecimal costoLiv = det.getCostoLivello();
     if(costoLiv != null)
     {
        costoLiv = costoLiv.setScale(SCALA_COSTI_DET, BigDecimal.ROUND_HALF_UP);
        det.setCostoLivello(costoLiv);
     }
     BigDecimal costoLivInf = det.getCostoLivelliInf();
     if(costoLivInf != null)
     {
        costoLivInf = costoLivInf.setScale(SCALA_COSTI_DET, BigDecimal.ROUND_HALF_UP);
        det.setCostoLivelliInf(costoLivInf);
     }
     BigDecimal costoTot = det.getCostoTotale();
     if(costoTot != null)
     {
        costoTot = costoTot.setScale(SCALA_COSTI_DET, BigDecimal.ROUND_HALF_UP);
        det.setCostoTotale(costoTot);
     }
  }
  //Fix 10155 fine

  public CostiCommessaDet getPersistFromTemp(List persistList, CostiCommessaDet temp) {
    for(Iterator iter = persistList.iterator(); iter.hasNext(); ) {
      CostiCommessaDet persist = (CostiCommessaDet)iter.next();
      if(areSimilar(temp, persist))
        return persist;
    }

    return null;
  }

  public boolean areSimilar(CostiCommessaDet temp, CostiCommessaDet persist) {
    if(!persist.getIdAzienda().equals(temp.getIdAzienda()))
      return false;

    if(!persist.getIdProgrStoric().equals(temp.getIdProgrStoric()))
      return false;

    if(!persist.getIdCommessa().equals(temp.getIdCommessa()))
      return false;

    if(!persist.getIdRigaElem().equals(temp.getIdRigaElem()))
      return false;

    if(persist.getTipoDettaglioCosto() != temp.getTipoDettaglioCosto())
      return false;

    if(!persist.getIdComponCosto().equals(temp.getIdComponCosto()))
      return false;

    return true;
  }

  public ConsuntivazioneCommesse getConsuntivazioneCommesse() {
    return iConsuntivazioneCommesse;
  }

  public void setConsuntivazioneCommesse(ConsuntivazioneCommesse consuntivazioneCommesse) {
    iConsuntivazioneCommesse = consuntivazioneCommesse;
  }

  public String getIdAmbienteValid(StoricoCommessa storicoCmm) {
    PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
    List persDatiTAmbienteCostiList = p.getAmbienteCosti();
    PersDatiTecniciAmbiente persDatiTecAmbiente = null;
    for(int i = 0; i < persDatiTAmbienteCostiList.size(); i++) {
      persDatiTecAmbiente = (PersDatiTecniciAmbiente)persDatiTAmbienteCostiList.get(i);
      if(storicoCmm.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO) {
        //Fix 6994 inizio
        java.sql.Date dataInizioAmb = persDatiTecAmbiente.getDataInizioVal();
        java.sql.Date dataFineAmb = persDatiTecAmbiente.getDataFineVal();
        java.sql.Date dataStorico = storicoCmm.getDataOrigine();
        if (TimeUtils.differenceInDays(dataInizioAmb, dataStorico) <= 0 &&
            TimeUtils.differenceInDays(dataFineAmb, dataStorico) >= 0)
          return persDatiTecAmbiente.getIdAmbiente();
        //Fix 6994 fine
      }
      else {
        if(storicoCmm.getDocumentoOrigine() == StoricoCommessa.ORDINE)
          if(persDatiTecAmbiente.getDataInizioVal().before(storicoCmm.getDataOrdine()) && persDatiTecAmbiente.getDataFineVal().after(storicoCmm.getDataOrdine()))
            return persDatiTecAmbiente.getIdAmbiente();
      }
    }
    return null;
  }

  //Fix 12271 inizio
  public AmbienteCosti getAmbienteCostiValid(StoricoCommessa storicoCmm) 
  {
     PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
     List persDatiTAmbienteCostiList = p.getAmbienteCosti();
     PersDatiTecniciAmbiente persDatiTecAmbiente = null;
     for(int i = 0; i < persDatiTAmbienteCostiList.size(); i++) {
       persDatiTecAmbiente = (PersDatiTecniciAmbiente)persDatiTAmbienteCostiList.get(i);
       if(storicoCmm.getDocumentoOrigine() == StoricoCommessa.DOCUMENTO) {
         //Fix 6994 inizio
         java.sql.Date dataInizioAmb = persDatiTecAmbiente.getDataInizioVal();
         java.sql.Date dataFineAmb = persDatiTecAmbiente.getDataFineVal();
         java.sql.Date dataStorico = storicoCmm.getDataOrigine();
         if (TimeUtils.differenceInDays(dataInizioAmb, dataStorico) <= 0 &&
             TimeUtils.differenceInDays(dataFineAmb, dataStorico) >= 0)
           return persDatiTecAmbiente.getAmbienteCosti();
         //Fix 6994 fine
       }
       else {
         if(storicoCmm.getDocumentoOrigine() == StoricoCommessa.ORDINE)
           if(persDatiTecAmbiente.getDataInizioVal().before(storicoCmm.getDataOrdine()) && persDatiTecAmbiente.getDataFineVal().after(storicoCmm.getDataOrdine()))
             return persDatiTecAmbiente.getAmbienteCosti();
       }
     }
     return null;
   }
  //Fix 12271 fine
  

  public boolean valorizzaCostiElem(CostiCommessaElem costiCommessaElem, List costiCmmessaDettaglioList) {
    PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
    String idRiferimento = p.getIdRiferimento();
    String idPrimo = p.getIdPrimo();
    String idIndustriale = p.getIdIndustriale();
    String idGenerale = p.getIdGenerale();
    CostiCommessaDet costiCmmDet = null;
    for(int i = 0; i < costiCmmessaDettaglioList.size(); i++) {
      costiCmmDet = (CostiCommessaDet)costiCmmessaDettaglioList.get(i);
      if(costiCmmDet.getTipoDettaglioCosto() == CostiCommessaDet.NORMALE) {
        if(costiCmmDet.getIdComponCosto().equals(idRiferimento)) {
          if(costiCommessaElem.getCostoRiferimento() != null)
            costiCommessaElem.setCostoRiferimento(costiCommessaElem.getCostoRiferimento().add(costiCmmDet.getCostoTotale()));
          else
            costiCommessaElem.setCostoRiferimento(costiCmmDet.getCostoTotale());
        }
        if(costiCmmDet.getIdComponCosto().equals(idPrimo)) {
          if(costiCommessaElem.getCostoPrimo() != null)
            costiCommessaElem.setCostoPrimo(costiCommessaElem.getCostoPrimo().add(costiCmmDet.getCostoTotale()));
          else
            costiCommessaElem.setCostoPrimo(costiCmmDet.getCostoTotale());
        }
        if(costiCmmDet.getIdComponCosto().equals(idIndustriale)) {
          if(costiCommessaElem.getCostoIndustriale() != null)
            costiCommessaElem.setCostoIndustriale(costiCommessaElem.getCostoIndustriale().add(costiCmmDet.getCostoTotale()));
          else
            costiCommessaElem.setCostoIndustriale(costiCmmDet.getCostoTotale());
        }
        if(costiCmmDet.getIdComponCosto().equals(idGenerale)) {
          if(costiCommessaElem.getCostoGenerale() != null)
            costiCommessaElem.setCostoGenerale(costiCommessaElem.getCostoGenerale().add(costiCmmDet.getCostoTotale()));
          else
            costiCommessaElem.setCostoGenerale(costiCmmDet.getCostoTotale());
        }
      }
    }
    try {
      //33950 inizio
      //int rc = costiCommessaElem.save();
      int rc = 0; 
      if(!iConsuntivazioneCommesse.isEstrazioneStoriciCommessa())
    	  rc = costiCommessaElem.save();
      //33950 fine
      if(rc < 0)
        return false;
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }

    return true;
  }

  public CostiCommessaDet getApportunoCostoCommessaDetRisorsa(StoricoCommessa storicoCommessa, List costoCommessaDetTempList) {
    Risorsa risorsa = storicoCommessa.getRisorsa();
    if(risorsa.getLivelloRisorsa() == Risorsa.MATRICOLA)
      risorsa = risorsa.getRisorsaAppart();
    ComponenteCosto compCosto = risorsa.getComponenteCosto();
    if(compCosto != null) {
      CostiCommessaDet apportunoCostoCommessaDet = null;
      for(int i = 0; i < costoCommessaDetTempList.size(); i++) {
        apportunoCostoCommessaDet = (CostiCommessaDet)costoCommessaDetTempList.get(i);
        if(apportunoCostoCommessaDet.getIdComponCosto().equals(compCosto.getIdComponenteCosto()))
          return apportunoCostoCommessaDet;
      }
      return apportunoCostoCommessaDet;
    }
    else {
      return null;
    }
  }

  public CostoRisorsa getCostoRisorsa(Risorsa risorsa, String clauseWhereAmbiente, String idAzienda, String idStabilimento) {
    Vector risorseCosti = new Vector();
    CostoRisorsa costoRsr = null;
    String clauseWhere = CostoRisorsaTM.ID_AZIENDA + " = '" + idAzienda + "' AND " +
      CostoRisorsaTM.R_TIPO_RISORSA + " = '" + risorsa.getTipoRisorsa() + "' AND " +
      CostoRisorsaTM.R_LIVELLO_RISORSA + " = '" + risorsa.getLivelloRisorsa() + "' AND " +
      CostoRisorsaTM.R_RISORSA + " = '" + risorsa.getIdRisorsa() + "' AND " +
      CostoRisorsaTM.R_STABILIMENTO + " = '" + idStabilimento + "'";
    clauseWhere = clauseWhere + clauseWhereAmbiente;
    try {
      risorseCosti = CostoRisorsa.retrieveList(clauseWhere, "", true);
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }

    if(risorseCosti.size() == 1)
      costoRsr = (CostoRisorsa)risorseCosti.get(0);

    return costoRsr;
  }
  /*
  public static void main(String[] args) {
    try {
      Security.setCurrentDatabase("THIP205D", new DB2Database());
      Security.openMainSession("ZIED_ZB", "ZIED0001");
      String idAzienda = "ZB";
      String idCommessa = "PANTHERA";
      String idArticolo = "PANTHERA";
      Integer idVersione = new Integer(2);
      Integer idConfigurazione = new Integer(253);
      CostiCommessaElem elem = getCostoPrevistoElemento(idAzienda, idCommessa, idArticolo, null, idConfigurazione);
      System.out.println(elem);

      elem = getCostoPrevistoElemento(idAzienda, idCommessa, idArticolo, null, null);
      System.out.println(elem);

      elem = getCostoPrevistoElemento(idAzienda, idCommessa, idArticolo, idVersione, null);
      System.out.println(elem);

      elem = getCostoPrevistoElemento(idAzienda, idCommessa, idArticolo, new Integer(1), idConfigurazione);
      System.out.println(elem);

      elem = getCostoPrevistoElemento(idAzienda, idCommessa, idArticolo, idVersione, idConfigurazione);
      System.out.println(elem);

      ConnectionManager.closeMainConnection();
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }
  }
  */

  public static Vector getStoricoCommessaList(String idAzienda) {
    String clauseWhere = StoricoCommessaTM.ID_AZIENDA + " = '" + idAzienda + "'";
    Vector storicoVect = new Vector();
    try {
      storicoVect = StoricoCommessa.retrieveList(clauseWhere, StoricoCommessaTM.ID_PROGRESSIVO, true);
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }

    return storicoVect;
  }

  /**
   * verificare che i componenti referenziati dalla formula sono tutti valorizzati
   * @param compDaValorizz List
   * @param usedComp List
   * @param corrente DettaglioCosto
   * @return boolean
   */
  public static boolean isFormulaCalcolabile(List compDaValorizz, List usedComp, CostiCommessaDet corrente) {
    if(usedComp == null || usedComp.size() == 0)
      return true;
    else {
      Iterator itComponenteUsed = usedComp.iterator();
      ComponenteCosto compCosto = null;
      while(itComponenteUsed.hasNext()) {
        compCosto = (ComponenteCosto)itComponenteUsed.next();
        Iterator itDettaglio = compDaValorizz.iterator();
        CostiCommessaDet dettCosto = null;
        while(itDettaglio.hasNext()) {
          dettCosto = (CostiCommessaDet)itDettaglio.next();
          if(compCosto.getIdComponenteCosto().equalsIgnoreCase(dettCosto.getComponenteCosto().getIdComponenteCosto())) {
            if(corrente.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE)
              return false;
            if(!compCosto.getIdComponenteCosto().equals(corrente.getIdComponCosto()))
              return false;
          }
        }
      }
      return true;
    }
  }

  /**
   * Definire i differenti componente utilizzati nella formula
   * @param formula Formula
   * @throws SQLException
   * @return List
   */
  public List compUsedInFormula(Formula formula) {
    List compUsedList = new ArrayList();
    Set variabili = formula.getUsedVariables();
    Iterator variabiliIter = variabili.iterator();
    String variabile;
    //Fix 10411 inizio
    // sfrutto cache di componenti costo
    Map compMap = iConsuntivazioneCommesse.getMapComponentiDiCosto();
    /*
    List Componenti;
    try {
       String where = ComponenteCostoTM.ID_AZIENDA + " = '" + iConsuntivazioneCommesse.getIdAzienda() + "' ";
       Componenti = ComponenteCosto.retrieveList(where, "", false);
    }
    catch(Exception ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
        return null;
    }
    */
    //Fix 10411 fine

    //Fix 10411 inizio
    Iterator vIter = variabili.iterator();
    while(vIter.hasNext())
    {
       variabile = vIter.next().toString();
       String idCmp = componenteId(variabile);
       ComponenteCosto cmp = (ComponenteCosto)compMap.get(idCmp.toUpperCase());
       if(cmp != null)
          compUsedList.add(cmp);
    }
    /*
    ComponenteCosto cmp = (ComponenteCosto)Factory.createObject(ComponenteCosto.class);
    cmp.setIdAzienda(iConsuntivazioneCommesse.getIdAzienda());
    int index = 0;
    while(variabiliIter.hasNext()) {
      variabile = variabiliIter.next().toString();
      cmp.setIdComponenteCosto(componenteId(variabile));
      index = Componenti.indexOf(cmp);
      if(index >= 0 && Componenti != null)
        compUsedList.add(Componenti.get(index));
    }
    */
    //Fix 10411 fine
    return compUsedList;
  }

  /**
   * componenteId
   * @param idVariabile String
   * @return String
   */
  private String componenteId(String idVariabile) {
    if(idVariabile.endsWith(VariabiliCosti.LIVELLO)) {
      return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO.length());
    }
    if(idVariabile.endsWith(VariabiliCosti.LIVELLO_INFERIORE)) {
      return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO_INFERIORE.length());
    }
    return idVariabile;
  }

  public ErrorMessage checkClasseMerceologica(StoricoCommessa storicoCommessa) {
    if((storicoCommessa.getRisorsa() == null) && (storicoCommessa.getArticolo() != null)) {
      if(storicoCommessa.getArticolo().getClasseMerclg() == null)
        return new ErrorMessage("THIP20T148");
      else if(getIdComponeteCosto(storicoCommessa.getArticolo().getClasseMerclg(), storicoCommessa) == null)
        return new ErrorMessage("THIP20T149");
    }
    return null;
  }

  public String getIdComponeteCosto(ClasseMerceologica classeMerceologica, StoricoCommessa storicoCommessa) {
    if((storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.LAVORAZIONE_ESTERNA_MATERIALE) || (storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.LAVORAZIONE_ESTERNA_PRODOTTO)) {
      return classeMerceologica.getIdCompCostoLavEsterna();
    }
    else
      return classeMerceologica.getIdComponenteCosto();
  }

  //Fix 04653 Mz inizio
  public boolean isRigaTrasferimentoMagazzino(StoricoCommessa storicoCommessa) {
    return(storicoCommessa.getTipoRigaOrigine() == StoricoCommessa.TRASFERIMENTO_MAGAZZINO) &&
      ((storicoCommessa.getValorizzaCosto() == StoricoCommessa.DECREMENTA_COSTO) ||
      ((storicoCommessa.getValorizzaCosto() == StoricoCommessa.INCREMENTA_COSTO) && (storicoCommessa.getIdCommessaCol() != null))
      );
  }

  public void addCostoPrevistoNonTrovatoAnomlia(StoricoCommessa storicoCommessa, List anomalieList) {
    ReportAnomalieConsCmm anomalia = new ReportAnomalieConsCmm(storicoCommessa, iConsuntivazioneCommesse.iBatchJobId, iConsuntivazioneCommesse.iAnomalieReportNr, messageNumber++, storicoCommessa.getCommessa().getIdAmbienteCommessa(), new ErrorMessage("THIP20T072").getLongText());
    try {
      iConsuntivazioneCommesse.addAnomalie(anomalia);
    }
    catch(Exception e) {
      e.printStackTrace(Trace.excStream); //...FIX 7430
    }
    anomalieList.add(anomalia);
  }

  public static CostiCommessaElem getCostoPrevistoElemento(StoricoCommessa storicoCommessa) {
    return getCostoPrevistoElemento(storicoCommessa, false);
  }

  public static CostiCommessaElem getCostoPrevistoElemento(StoricoCommessa storicoCommessa, boolean usaCommessaCollegata) {
    return getCostoPrevistoElemento(storicoCommessa.getIdAzienda(),
      usaCommessaCollegata ? storicoCommessa.getIdCommessaCol() : storicoCommessa.getIdCommessa(),
      storicoCommessa.getIdArticolo(),
      storicoCommessa.getIdVersione(),
      storicoCommessa.getIdConfigurazione());
  }

  public static CostiCommessaElem getCostoPrevistoElemento(String idAzienda, String idCommessa, String idArticolo, Integer idVersione, Integer idConfigurazione) {
    String where = CostiCommessaElemTM.ID_AZIENDA + " = '" + idAzienda + "'" +
      " AND " + CostiCommessaElemTM.ID_COMMESSA + " = '" + idCommessa + "'" +
      " AND " + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "'" +
      " AND " + CostiCommessaElemTM.ID_PROGR_STORIC + " IN (SELECT " + CostiCommessaTM.ID_PROGR_STORIC +
      " FROM " + CostiCommessaTM.TABLE_NAME +
      " WHERE " + CostiCommessaTM.ID_AZIENDA + " ='" + idAzienda + "'" +
      " AND " + CostiCommessaTM.ID_COMMESSA + " ='" + idCommessa + "'" +
      " AND " + CostiCommessaTM.TIPOLOGIA + " ='" + CostiCommessa.COSTO_PREVISTO + "'" +
      " AND " + CostiCommessaTM.UFFICIALE + " ='" + Column.TRUE_CHAR + "')" +
      " AND " + CostiCommessaElemTM.R_ARTICOLO + " = '" + idArticolo + "'";

    if(idVersione == null) {
      where += " AND " + CostiCommessaElemTM.R_VERSIONE + " = 1";
    }
    else {
      where += " AND " + CostiCommessaElemTM.R_VERSIONE + " = " +
        idVersione.intValue();
    }

    if(idConfigurazione == null) {
      where += " AND " + CostiCommessaElemTM.R_CONFIGURAZIONE + " IS NULL";
    }
    else {
      where += " AND " + CostiCommessaElemTM.R_CONFIGURAZIONE + " = " +
        idConfigurazione.intValue();
    }

    try {
      Vector elementi = CostiCommessaElem.retrieveList(where, "", false);
      return elementi.isEmpty() ? null : (CostiCommessaElem)elementi.firstElement();
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }

    return null;
  }

  public static void decrementaDettagliTemporanei(StoricoCommessa storicoCommessa, List dettagliTemporanei, CostiCommessaElem costoPrevistoElem) {
    for(Iterator iter = costoPrevistoElem.getCostiCommessaDet().iterator(); iter.hasNext(); ) {
      CostiCommessaDet costoPrevistoDettaglio = (CostiCommessaDet)iter.next();
      CostiCommessaDet costoDettaglioTemporaneo = getCostoCommessaDettaglio(dettagliTemporanei, costoPrevistoDettaglio.getIdComponCosto());
      decrementaDettaglioTemporaneo(storicoCommessa, costoPrevistoElem, costoDettaglioTemporaneo, costoPrevistoDettaglio);
    }
  }

  public static void decrementaDettaglioTemporaneo(StoricoCommessa storicoCommessa, CostiCommessaElem costoPrevistoElem, CostiCommessaDet costoDettaglioTemporaneo, CostiCommessaDet costoPrevistoDettaglio) {
    if((costoDettaglioTemporaneo != null) && (costoPrevistoDettaglio != null)) {
      BigDecimal costoCalcolato = divide(multiply(storicoCommessa.getQuantitaUMPrm(), costoPrevistoDettaglio.getCostoTotale()), costoPrevistoElem.getQuantita());
      costoDettaglioTemporaneo.setCostoLivello(substract(costoDettaglioTemporaneo.getCostoLivello(), costoCalcolato));
      costoDettaglioTemporaneo.setCostoTotale(sum(costoDettaglioTemporaneo.getCostoLivello(), costoDettaglioTemporaneo.getCostoLivelliInf()));
    }
  }

  public static void incrementaDettagliTemporanei(StoricoCommessa storicoCommessa, List dettagliTemporanei, CostiCommessaElem costoPrevistoElem) {
    for(Iterator iter = costoPrevistoElem.getCostiCommessaDet().iterator(); iter.hasNext(); ) {
      CostiCommessaDet costoPrevistoDettaglio = (CostiCommessaDet)iter.next();
      CostiCommessaDet costoDettaglioTemporaneo = getCostoCommessaDettaglio(dettagliTemporanei, costoPrevistoDettaglio.getIdComponCosto());
      incrementaDettaglioTemporaneo(storicoCommessa, costoPrevistoElem, costoDettaglioTemporaneo, costoPrevistoDettaglio);
    }
  }

  public static void incrementaDettaglioTemporaneo(StoricoCommessa storicoCommessa, CostiCommessaElem costoPrevistoElem, CostiCommessaDet costoDettaglioTemporaneo, CostiCommessaDet costoPrevistoDettaglio) {
    if((costoDettaglioTemporaneo != null) && (costoPrevistoDettaglio != null)) {
      BigDecimal costoCalcolato = divide(multiply(storicoCommessa.getQuantitaUMPrm(), costoPrevistoDettaglio.getCostoTotale()), costoPrevistoElem.getQuantita());
      costoDettaglioTemporaneo.setCostoLivello(sum(costoDettaglioTemporaneo.getCostoLivello(), costoCalcolato));
      costoDettaglioTemporaneo.setCostoTotale(sum(costoDettaglioTemporaneo.getCostoLivello(), costoDettaglioTemporaneo.getCostoLivelliInf()));
    }
  }

  public BigDecimal abs(BigDecimal decimal) {
    return(decimal == null) ? null : decimal.abs();
  }

  public static BigDecimal sum(BigDecimal val1, BigDecimal val2) {
    if(val1 == null) {
      if(val2 == null)
        return null;
      else
        return val2;
    }
    if(val2 == null) {
      if(val1 == null)
        return null;
      else
        return val1;
    }
    return val1.add(val2);
  }

  public static BigDecimal substract(BigDecimal val1, BigDecimal val2) {
    if((val1 == null) && (val2 == null))
      return null;
    else if(val1 == null) {
      return val2.negate();
    }
    else if(val2 == null) {
      return val1;
    }

    return val1.subtract(val2);
  }

  public static BigDecimal multiply(BigDecimal val1, BigDecimal val2) {
    if((val1 == null) || (val2 == null))
      return null;

    return val1.multiply(val2);
  }

  public static BigDecimal divide(BigDecimal val1, BigDecimal val2) {
    if((val1 == null) || (val2 == null))
      return null;
    return(Utils.compare(val2, ZERO) == 0) ? val1 : val1.divide(val2, BigDecimal.ROUND_HALF_UP);
  }

  //Fix 04653 Mz fine

  //...FIX 4709 (LP) inizio

  /**
   * sommaOrSottraiCosto
   * @param costoPartenza BigDecimal
   * @param costo BigDecimal
   * @param operazione char
   * @return BigDecimal
   */
  public BigDecimal sommaOrSottraiCosto(BigDecimal costoPartenza, BigDecimal costo, char operazione) {
    if(operazione == StoricoCommessa.INCREMENTA_COSTI_INDIRETTI || operazione == StoricoCommessa.INCREMENTA_COSTO) {
      return sum(costoPartenza, costo);
    }
    else {
      return substract(costoPartenza, costo);
    }
  }

  public void aggiornaCostoCommessaDetTempList(List costoCommessaDetTempList, List costiCommDetOldList, char operazione) {
    for(Iterator iter = costoCommessaDetTempList.iterator(); iter.hasNext(); ) {
      //...Dettaglio che contieme il ricalcolo dell'indiretto
      CostiCommessaDet costoDettaglioInd = (CostiCommessaDet)iter.next();
      BigDecimal costoLivelloInd = costoDettaglioInd.getCostoLivello();

      //...Costo dettaglio corrispondente nella lista vecchia salvata
      CostiCommessaDet costoDettaglioTemp = getCostoCommessaDettaglio(costiCommDetOldList, costoDettaglioInd.getIdComponCosto());
      BigDecimal costoLivelloOld = costoDettaglioTemp.getCostoLivello();

      BigDecimal result = ZERO;
      if(operazione == StoricoCommessa.INCREMENTA_COSTO) {
        result = sum(costoLivelloOld, costoLivelloInd);
      }
      else if(operazione == StoricoCommessa.DECREMENTA_COSTO) {
        result = substract(costoLivelloOld, costoLivelloInd);
      }
      costoDettaglioTemp.setCostoLivello(result);
      costoDettaglioTemp.setCostoTotale(result);
    }
  }

  //...FIX 4709 (LP) fine

  //Fix 8631 inizio
  protected Formula cloneFormula(Formula formulaDaUtilizzOrig) {
    Formula formulaDaUtilizz = new Formula();
    try {
      formulaDaUtilizz.setEqual(formulaDaUtilizzOrig);
    }
    catch (CopyException e) {
    }
    return formulaDaUtilizz;
  }
  //Fix 8631 fine

  // fix 10913
  public List dammiComponenti(StoricoCommessa storicoCommessa, Articolo articolo){
    return articolo.getClasseMerclg().getSchemaCosto().
            getComponenti();
  }
  // fine fix 10913
  //31513 inizio
  public boolean isFormulaCalcolabileDouble(FormulaCosti formulaCosti, Formula formulaDaUtilizz, List compDaValorizz, List usedComp, CostiCommessaDet componente) throws SQLException {
    boolean formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
    if (!formCalc && !formulaCosti.equals(componente.getComponenteCosto().getFormula())) {
      formulaDaUtilizz = componente.getComponenteCosto().getFormula().getFormula();
      formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
    }
    return formCalc;
  }
  //31513 fine
  //33950 inizio
  public StoricoCommessaDet creaStoricoCommessaDet (StoricoCommessa storicoCommessa, CostiCommessaDet costiCommDet) {
	  StoricoCommessaDet storicoCommessaDet = (StoricoCommessaDet)Factory.createObject(StoricoCommessaDet.class);
	  storicoCommessaDet.setIdAzienda(storicoCommessa.getIdAzienda());
	  storicoCommessaDet.setIdProgrStorico(storicoCommessa.getIdProgressivo());
	  storicoCommessaDet.setIdComponenteCosto(costiCommDet.getIdComponCosto());	  
	  storicoCommessaDet.setTipoDettaglio(costiCommDet.getTipoDettaglioCosto());
	  storicoCommessaDet.setIdCommessa(costiCommDet.getIdCommessa());
	  storicoCommessaDet.setCostoLivello(costiCommDet.getCostoLivello());
	  storicoCommessaDet.setCostoLivelloInf(costiCommDet.getCostoLivelliInf());
	  storicoCommessaDet.setCostoTotale(costiCommDet.getCostoTotale());
	  storicoCommessaDet.setTempoLivello(ZERO);
	  storicoCommessaDet.setTempoLivelloInf(ZERO);
	  storicoCommessaDet.setTempoTotale(ZERO);
	  if(costiCommDet.getIdComponCosto() != null && 
	     storicoCommessa.getIdComponenteCosto() != null && 
	     costiCommDet.getIdComponCosto().equals(storicoCommessa.getIdComponenteCosto()) && 
	     costiCommDet.getComponenteCosto().isGestioneATempo() && storicoCommessa.getTempo() != null) {
		  storicoCommessaDet.setTempoLivello(storicoCommessa.getTempo());
		  storicoCommessaDet.setTempoTotale(storicoCommessa.getTempo());
	  }
      try {
    	  int rc = storicoCommessaDet.save();
    	  if(rc >= 0) {
    		  return storicoCommessaDet;
    	  }
      }
      catch(Exception e) {
    	  e.printStackTrace(Trace.excStream);
      }
	  return null;
  }
  //33950 fine
}

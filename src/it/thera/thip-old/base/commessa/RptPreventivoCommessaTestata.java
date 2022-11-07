/*
 * @(#)RptPrevComTes.java
 */

/**
 * RptPrevComTes
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 16:39:23
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 16/12/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 15848     05/03/2012    FM       Correzione
 * 33048     08/03/2021    RA       Aggiunto metodo caricaCommenti
 */
package it.thera.thip.base.commessa;
import com.thera.thermfw.batch.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.Entity;
import com.thera.thermfw.security.Task;

import it.thera.thip.base.comuniVenAcq.CommentiDocumento;//33048

public class RptPreventivoCommessaTestata extends RptPreventivoCommessaTestataPO {

  //Modalità di generazione offerta
  public static final char MOD_GEN_OFF_RIG_COMM = '0';
  public static final char MOD_GEN_OFF_RIG_COMM_VOCI_NON_FATT = '1';
  public static final char MOD_GEN_OFF_RIG_COMM_NON_FATT_VOCI = '2';
  public static final char MOD_GEN_OFF_ESCL_GEN_OFF = '3';

  //Logica reperimento costo delle voci
  public static final char REP_COS_ARTICOLO_COSTO = '1';
  public static final char REP_COS_AMBIENTE_COSTO = '2';
  public static final char REP_COS_LISTINO_ACQ = '3';

  //Stato evasione
  public static final char ST_EVA_INEVASO = '0';
  public static final char ST_EVA_EVASO_PARZIALMENTE = '1';
  public static final char ST_EVA_SALDATO = '2';

  //Stato offerta cliente
  public static final char STATO_OFF_DA_GENERARE = '0';
  public static final char STATO_OFF_GENERATA = '1';
  public static final char STATO_OFF_NON_RICHIESTA = '2';

  //Stampa allegato tecnico
  public static final char STP_ALL_TEC_DA_EFFETTUARE = '0';
  public static final char STP_ALL_TEC_DA_RIEMETTERE = '1';
  public static final char STP_ALL_TEC_EMESSA = '2';
  public static final char STP_ALL_TEC_NON_RICHIESTA = '3';

  //Tipo intestatario preventivo
  public static final char TP_INTES_CLIENTE = '1';
  public static final char TP_INTES_ANAGRAFICO = '2';
  public static final char TP_INTES_CONTATTO = '3';

  //Stato
  public static final char STATO_INCOMPLETO = 'I';
  public static final char STATO_VALIDO = 'V';
  public static final char STATO_ANNULATO = 'A';
  public static final char STATO_SOSPESO = 'S';


  /**
   * Proxy verso la classe AvailableReport(Report disponibili).
   */
  protected Proxy availableReport = new Proxy(com.thera.thermfw.batch.AvailableReport.class);

  /**
   * Imposta l'oggetto AvailableReport.
   * @param L'oggetto AvailableReport.
   */
  public void setAvailableReport(AvailableReport availReport) {
    this.availableReport.setObject(availReport);
    setDirty();
  }

  /**
   * Recupera l'oggetto AvailableReport.
   * @return L'oggetto AvailableReport.
   */
  public AvailableReport getAvailableReport() {
    return (AvailableReport) availableReport.getObject();
  }

  /**
   * Imposta la chiave dell'oggetto AvailableReport.
   * @param key La chiave dell'oggetto AvailableReport.
   */
  public void setAvailableReportKey(String key) {
    availableReport.setKey(key);
    setDirty();
  }

  /**
   * Recupera la chiave dell'oggetto AvailableReport.
   * @return La chiave dell'oggeto AvailableReport.
   */
  public String getAvailableReportKey() {
    return availableReport.getKey();
  }

  /**
   * Imposta la prima parte della chiave dell'oggetto AvailableReport.
   * @param key La prima parte della chiave dell'oggetto AvailableReport.
   */
  public void setBatchJobId(int batchJobId) {
    String key = availableReport.getKey();
    Integer batchJobIdTmp = new Integer(batchJobId);
    availableReport.setKey(KeyHelper.replaceTokenObjectKey(key, 1, batchJobIdTmp));
    setDirty();
  }

  /**
   * Recupera la prima parte della chiave dell'oggetto AvailableReport.
   * @return La prima parte della chiave dell'oggeto AvailableReport.
   */
  public int getBatchJobId() {
    String key = availableReport.getKey();
    String objBatchJobId = KeyHelper.getTokenObjectKey(key, 1);
    return KeyHelper.stringToInt(objBatchJobId);
  }

  /**
   * Imposta la seconda parte della chiave dell'oggetto AvailableReport.
   * @param key La seconda parte della chiave dell'oggetto AvailableReport.
   */
  public void setReportNr(int reportNr) {
    String key = availableReport.getKey();
    Integer reportNrTmp = new Integer(reportNr);
    availableReport.setKey(KeyHelper.replaceTokenObjectKey(key, 2, reportNrTmp));
    setDirty();
  }

  /**
   * Recupera la seconda parte della chiave dell'oggetto AvailableReport.
   * @return La seconda parte della chiave dell'oggeto AvailableReport.
   */
  public int getReportNr() {
    String key = availableReport.getKey();
    String objReportNr = KeyHelper.getTokenObjectKey(key, 2);
    return KeyHelper.stringToInt(objReportNr);
  }


  /**
   * checkDelete
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete() {
    /**@todo*/
    return null;
  }
  
  //33048 inizio
  public void caricaCommenti(PreventivoCommessaTestata tes, Entity entity, Task task, String idLanguage) throws Exception {
    if (tes.getCommenti() != null) {
      this.setRGesCommenti(new Integer(tes.getCommenti().getId()));
      CommentiDocumento commPos = (CommentiDocumento)Factory.createObject(it.thera.thip.base.comuniVenAcq.CommentiDocumento.class);
      setCommenti(commPos.formattaListaCommenti(commPos.getCommentiPerPosizione(tes.getCommenti(), entity, task, "" + CommentiDocumento.NORMALE + CommentiDocumento.PRIMA_DETTAGLI, idLanguage)));
      setCommentiPiede(commPos.formattaListaCommenti(commPos.getCommentiPerPosizione(tes.getCommenti(), entity, task, "" + CommentiDocumento.DOPO_DETTAGLI, idLanguage)));
    }
  }
  //33048 fine

}


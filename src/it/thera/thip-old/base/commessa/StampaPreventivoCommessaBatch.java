package it.thera.thip.base.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.batch.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;

import com.thera.thermfw.type.*;
import com.thera.thermfw.util.*;
import it.thera.thip.base.articolo.*;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.documentoDgt.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.cs.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: softher</p>
 * @author Linda Fix14963 18/11/2011
 *
 */
/* Revision:
 * Fix #    Date          Owner      Description
 * 14965    11/01/2012     FM        doc digitale
 * 15848    05/03/2012     FM         Correzione
 * 19818    28/05/2014     AA        Correzione relativa alla stampa in lingua
 * 23171    10/03/2016     RA        Rendi attributi entity e task non static per la gestione di commenti
 * 29731	10/09/2019	   RA		 Corretto gestione campo IdConfigurazione
 * 33048    02/03/2021	   RA		 Corretta il carico di righe e gestione di descrizione in lingua
 * 33229    30/03/2021     RA		 Vari modifiche
 */
public class StampaPreventivoCommessaBatch
  extends ThipElaboratePrintRunnable
  implements Authorizable
{

  // Tipo stampa
  public static final char TIPO_STP_INTERNA = '1';
  public static final char TIPO_STP_ALL_TEC = '2';

  // Stato stampa allegato tecnico
  public static final char STT_STP_DA_EMETTERE = '1';
  public static final char STT_STP_EMESSA = '2';
  public static final char STT_STP_ENTRAMBE = '3';

  // livello
  public static final String LIVELLO1 = ".";
  public static final String LIVELLO2 = "..";
  public static final String LIVELLO3 = "...";
  public static final String LIVELLO4 = "-";

  protected static final String TESTATA_TABLE = PreventivoCommessaTestataTM.TABLE_NAME;

  protected static final String linguaDefault = SystemParam.getCompanyDefaultLocale();

  public static String RES_FILE = "it.thera.thip.base.commessa.resources.StampaPreventivoCommessa";
  public static final String RESPONSABILE_PREV = ResourceLoader.getString(RES_FILE, "ResponsabilePreventivo");

	//Fix 23171 inizio
	/*
	public static Task task = null;
	public static Entity entity = null;
	*/
	public Task task = null;
	public Entity entity = null;	
	//Fix 23171 fine

  protected AvailableReport availableReport = null;

  protected int reportNr = 0;

  protected int contatoreTestate = 0;
  protected int contatoreRigheCommessa = 0;
  protected int contatoreRigheVoce = 0;
  protected int contatoreRigheGlobaliCommessa = 0;
  protected int contatoreRigheGlobaliVoce = 0;

  protected char iTipoStampa = TIPO_STP_INTERNA;

  protected char iStatoStampaAllTec = STT_STP_DA_EMETTERE;

  protected boolean iStampaCommenti = false;

  protected boolean iStampaInLingua = false;

  protected String lingua;

  private String where = "";

  public boolean iStpInValutaAz = false;

  public CondizioniOrdinamento condOrdinamento;
  public CondizioniFiltri condFiltro;

  public ColonneFiltri cf;

  protected static final Class TESTATA_REPORT = it.thera.thip.base.commessa.RptPreventivoCommessaTestata.class;
  protected static final Class RIGA_REPORT = it.thera.thip.base.commessa.RptPreventivoCommessaRiga.class;
  protected static final Class VOCE_REPORT = it.thera.thip.base.commessa.RptPreventivoCommessaVoce.class;

  protected PreventivoCommessaTestata preventivoCom = null;

  protected PreventivoCommessaRiga preventivoComRiga = null;

  protected PreventivoCommessaVoce preventivoComVoce = null;

  protected RptPreventivoCommessaTestata reportPreventivoCommessaTestata = (RptPreventivoCommessaTestata)Factory.createObject(TESTATA_REPORT);

  protected RptPreventivoCommessaRiga reportPreventivoCommessaRiga = (RptPreventivoCommessaRiga)Factory.createObject(RIGA_REPORT);

  protected RptPreventivoCommessaVoce reportPreventivoCommessaVoce = (RptPreventivoCommessaVoce)Factory.createObject(VOCE_REPORT);

  /**
   * initializeCondizioniOrdinamento().
   */

  protected void initializeCondizioniOrdinamento()
  {
    Vector vector = new Vector();
    cf = ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "NumeroPrevCom", false);
    vector.add(ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "NumeroPrevCom", false));

    vector.add(ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "IdCommessa", false));
    vector.add(ColonneFiltriConNLS.creaColonnaFiltro("PreventivoCommessaTestata", "IdApprovatorePrev", false, RESPONSABILE_PREV));
    condOrdinamento = new CondizioniOrdinamento(vector);
  }

  /**
   * initializeCondizioniFiltro().
   */

  protected void initializeCondizioniFiltro()
  {
    Vector vector = new Vector();
    ColonneFiltri cfr = ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "NumeroPrevCom", "PreventivoCommessaTestata", "NumeroPrevcFormattato", true);
    cfr.setAdditionalClassAdName("AnnoPrevCom");
    cfr.setUsaDescrizioneClasseRelazionata(true);
    vector.add(cfr);
    vector.add(ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "IdCommessa", "Commessa", "Descrizione.Descrizione", true));
    vector.add(ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "IdCliente", "ClienteVendita", "RagioneSociale", true));
    vector.add(ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "IdAnagrafico", "AnagraficoDiBaseAzienda", "RagioneSociale", true));
    vector.add(ColonneFiltri.creaColonnaFiltro("PreventivoCommessaTestata", "IdRubricaContatti", "WPURubricaContattiAzienda", "RagioneSociale", true));
    vector.add(ColonneFiltriConNLS.creaColonnaFiltro("PreventivoCommessaTestata", "IdApprovatorePrev", "Dipendente", "Matricola", true, RESPONSABILE_PREV));
    condFiltro = new CondizioniFiltri(vector);
  }

  /**
   * Costruttore.
   */

  public StampaPreventivoCommessaBatch()
  {
    initializeCondizioniOrdinamento();
    initializeCondizioniFiltro();
  }

  protected String getClassAdCollectionName()
  {
    return "StampaPreventivoCom";
  }

  /**
   * setStampaInLingua.
   * @param booleano boolean
   */
  public void setStampaInLingua(boolean booleano)
  {
    this.iStampaInLingua = booleano;
  }

  /**
   * getStampaInLingua().
   * @return boolean
   */
  public boolean getStampaInLingua()
  {
    return iStampaInLingua;
  }

  /**
   * setStampaCommenti.
   * @param booleano boolean
   */
  public void setStampaCommenti(boolean booleano)
  {
    this.iStampaCommenti = booleano;
  }

  /**
   * getStampaCommenti().
   * @return boolean
   */
  public boolean getStampaCommenti()
  {
    return iStampaCommenti;
  }

  /**
   * getColonneOrderBy().
   * @return Vector
   */
  public Vector getColonneOrderBy()
  {
    return condOrdinamento.getColonneOrderBy();
  }

  /**
   * getFiltri().
   * @return CondizioniFiltri
   */
  public CondizioniFiltri getFiltri()
  {
    return condFiltro;
  }

  /**
   * getOrdinamento().
   * @return CondizioniOrdinamento
   */
  public CondizioniOrdinamento getOrdinamento()
  {
    return condOrdinamento;
  }

  public void setWhere(String where)
  {
    this.where = where;
  }

  public String getWhere()
  {
    String innerWhere = PreventivoCommessaTestataTM.ID_AZIENDA + "='" + Azienda.getAziendaCorrente() + "'";
    if (getTipoStampa() == TIPO_STP_ALL_TEC)
    {
      if (getStatoStampaAllTec() != STT_STP_ENTRAMBE)
        innerWhere += " AND " + PreventivoCommessaTestataTM.STP_ALL_TEC + "='" + getStatoStampaAllTec() + "'";
      else
        innerWhere += " AND " + PreventivoCommessaTestataTM.STP_ALL_TEC + "<> '" + PreventivoCommessaTestata.STP_ALL_TEC_NON_RICHIESTA + "' ";
    }
    String stringa = getFiltri().getCondizioneWhere();
    if (stringa != null && !stringa.equals(""))
    {
      innerWhere += " AND " + stringa;
    }
    return innerWhere;
  }

  private String getOrderBy()
  {
    String orderBy = getOrdinamento().getCondizioneOrderBy();
    return orderBy;
  }

  /**
   * @param iTipoStampa char
   */
  public void setTipoStampa(char tipoStampa)
  {
    iTipoStampa = tipoStampa;
  }

  /**
   * @return char
   */
  public char getTipoStampa()
  {
    return iTipoStampa;
  }

  /**
   * @param iStatoStampaAllTec char
   */
  public void setStatoStampaAllTec(char statoStampaAllTec)
  {
    iStatoStampaAllTec = statoStampaAllTec;
  }

  /**
   * @return char
   */
  public char getStatoStampaAllTec()
  {
    return iStatoStampaAllTec;
  }

  public boolean createReport()
  {
    where = getWhere();
    Iterator elencoLingueIter = getElencoLingue(where).iterator();
    while (elencoLingueIter.hasNext())
    {
      lingua = (String)elencoLingueIter.next();
      String w = where;
      if (getStampaInLingua())
      {
        if (lingua.equals(linguaDefault))
          w = w + " AND (R_LINGUA = '" + linguaDefault + "' OR R_LINGUA IS NULL)";
        else
          w = w + " AND R_LINGUA = '" + lingua + "'";
      }
      contatoreTestate = 0;
      contatoreRigheCommessa = 0;
      contatoreRigheVoce = 0;
      contatoreRigheGlobaliCommessa = 0;
      contatoreRigheGlobaliVoce = 0;

      try
      {
        int ret = initAvailableReport();
      }
      catch (SQLException ex)
      {
        ex.printStackTrace(Trace.excStream);
      }
      try
      {
        if (entity == null)
        {
          entity = Entity.elementWithKey("StpPrevCom", Entity.NO_LOCK);
          String taskKey = KeyHelper.buildObjectKey(new String[]
            {"StpPrevCom", getTaskId()});
          task = Task.elementWithKey(taskKey, Entity.NO_LOCK);
        }
      }
      catch (SQLException ex)
      {
        ex.printStackTrace();
      }

      boolean ret = createReportInternal(w);
      if (!ret)
      {
        return false;
      }
    }
    return true;
  }

  public int initAvailableReport() throws java.sql.SQLException
  {
    char tipoStampa = this.getTipoStampa();
    boolean stampainValutaAz = this.getStpInValutaAz();
    availableReport = createNewReport(getReportId());
    String entity = availableReport.getReportModel().getEntityId();
    String gruppo = availableReport.getReportModel().getGroup();
    if (tipoStampa == TIPO_STP_INTERNA)
    {
      if (!stampainValutaAz)
        availableReport = getModelloScelto(1, entity, gruppo);
      else
        availableReport = getModelloScelto(3, entity, gruppo);
    }

    else
      availableReport = getModelloScelto(2, entity, gruppo);
    if (availableReport.getReportModel() != null)
    {
      availableReport.setLanguage(lingua); //Fix 19818
      setPrintToolInterface((com.thera.thermfw.batch.PrintingToolInterface)com.thera.thermfw.persist.Factory.createObject(com.thera.thermfw.batch.CrystalReportsInterface.class));
      String s = printToolInterface.generateDefaultWhereCondition(availableReport, RptPreventivoCommessaTestataTM.getInstance());
      availableReport.setWhereCondition(s);
      try
      {
        availableReport.save();
      }
      catch (Exception ex)
      {
        ex.printStackTrace(Trace.excStream);
      }
    }
    com.thera.thermfw.persist.ConnectionManager.commit();
    return availableReport.getReportNr();
  }

  public boolean createReportInternal(String where)
  {
    boolean newConnection = false;
    try
    {
      PersistentObjectCursor poCursor = new PersistentObjectCursor(PreventivoCommessaTestata.class.getName(), where, getOrderBy(), PersistentObject.NO_LOCK);
      poCursor.setPreLoadKeys(true);
      ConnectionManager.pushConnection();
      newConnection = true;
      if (poCursor.hasNext())
      {
         while (poCursor.hasNext())
        {
          preventivoCom = (PreventivoCommessaTestata)poCursor.next();
          checkPoint();
          if (preventivoCom == null)
          {
            output.println("Fallita retrieve dell'preventivo commessa");
          }
          else
          {
            int rc1 = fillTestata(true);
            if (rc1 >= ErrorCodes.OK)
            {
              creaDocDgt();//14965
              ConnectionManager.commit();
            }
            else
            {
              ConnectionManager.rollback();
            }
          }
        }

      }
      poCursor.close();
      return true;
    }
    catch (SQLException e)
    {
      e.printStackTrace(Trace.excStream);
    }
    catch (Exception e)
    {
      e.printStackTrace(Trace.excStream);
    }
    finally
    {
      try
      {
        if (contatoreRigheGlobaliCommessa == 0 && availableReport != null)
        {
          int rc2 = availableReport.delete();
          if (rc2 < ErrorCodes.OK)
          {
            output.println("Nessun preventivo da stampare, fallita la cancellazione del report.");
          }
          else
          {
            ConnectionManager.commit();
          }
        }
        ConnectionManager.rollback();
        if (newConnection)
        {
          ConnectionManager.popConnection();
        }
      }
      catch (SQLException e)
      {
        output.println("Fallita anche la rollback: forse è meglio spegnere il server");
        e.printStackTrace(Trace.excStream);
      }
    }
    return false;
  }

  public int fillTestata(boolean check) throws SQLException
  {
    RptPreventivoCommessaTestata report = caricaDatiTestata(check);
    return salvaDatiTestata(report);
  }

  public RptPreventivoCommessaTestata caricaDatiTestata(boolean check)
  {
    contatoreRigheCommessa = 0;
    lingua = linguaDefault;
    //33048 inizio
    /*
    if (getStampaInLingua() && getLingua() != null)
    {
      lingua = preventivoCom.getCliente().getCliente().getLingua().getId();
    }
    */
    if(getStampaInLingua() && preventivoCom.getLingua() != null)
    	lingua = preventivoCom.getLingua().getId();
    //33048 fine
    setPropertiesLblLanguage(lingua);
    updateJobParameters();
    reportPreventivoCommessaTestata = (RptPreventivoCommessaTestata)Factory.createObject(TESTATA_REPORT);
    reportPreventivoCommessaTestata.setAvailableReport(availableReport);
    reportPreventivoCommessaTestata.setIdAzienda(preventivoCom.getIdAzienda());
    reportPreventivoCommessaTestata.setIdAnnoPrevc(preventivoCom.getIdAnnoPrevc());
    reportPreventivoCommessaTestata.setIdNumeroPrevc(preventivoCom.getIdNumeroPrevc());
    reportPreventivoCommessaTestata.setDataPrevc(preventivoCom.getDataPrevc());
    reportPreventivoCommessaTestata.setNumeroPrevcFmt(preventivoCom.getNumeroPrevcFormattato());
    reportPreventivoCommessaTestata.setPercentuale(preventivoCom.getPercTotPrevCom());
    fillDatiCliente(preventivoCom);
    fillCausalePreventivoCommessa(preventivoCom.getCausalePreventivoCommessa());
    fillDatiComuniEstesi(preventivoCom);
    fillDatiRif(preventivoCom);
    fillDatiCommenti(preventivoCom);
    fillDatiGestioneOffertaCliente(preventivoCom);
    fillValori(preventivoCom);
    fillDatiWorkFlow(preventivoCom);
    reportPreventivoCommessaTestata.setIdStabilimento(preventivoCom.getIdStabilimento());
    //if (preventivoCom.getIdStabilimento() != null)//33229
    if (preventivoCom.getStabilimento() != null)//33229
      reportPreventivoCommessaTestata.setDescStabilimento(getDescrizioneInLingua(preventivoCom.getStabilimento().getDescrizione(), lingua));
    reportPreventivoCommessaTestata.setIdMagazzino(preventivoCom.getIdMagazzino());
    //if (preventivoCom.getIdMagazzino() != null)//33229
    if (preventivoCom.getMagazzino() != null)//33229
      reportPreventivoCommessaTestata.setDescMagazzino(getDescrizioneInLingua(preventivoCom.getMagazzino().getDescrizione(), lingua));
    reportPreventivoCommessaTestata.setIdCommessa(preventivoCom.getIdCommessa());
    //if (preventivoCom.getIdCommessa() != null)//33229
    if (preventivoCom.getCommessa() != null)//33229
      reportPreventivoCommessaTestata.setDescCommessa(getDescrizioneInLingua(preventivoCom.getCommessa().getDescrizione(), lingua));
    reportPreventivoCommessaTestata.setIdCommessaCa(preventivoCom.getIdCommessaCa());
    //if (preventivoCom.getIdCommessaCa() != null)//33229
    if (preventivoCom.getCommessaCa() != null)//33229
      reportPreventivoCommessaTestata.setDescCommessaCa(getDescrizioneInLingua(preventivoCom.getCommessaCa().getDescrizione(), lingua));
    reportPreventivoCommessaTestata.setTpIntestPrevc(preventivoCom.getTpIntestPrevc());
    reportPreventivoCommessaTestata.setTipoIntestatario(getDescrizioneEnum("TipoIntestatarioPreventiv", preventivoCom.getTpIntestPrevc()));
    reportPreventivoCommessaTestata.setIdDivisione(preventivoCom.getIdDivisione());
    reportPreventivoCommessaTestata.setIdAnagrafico(preventivoCom.getIdAnagrafico());
    reportPreventivoCommessaTestata.setIdRubContatti(preventivoCom.getIdRubContatti());
    //if (preventivoCom.getIdRubContatti() != null)//33229
    if (preventivoCom.getRubricaContatti() != null)//33229
    {
      reportPreventivoCommessaTestata.setDescRubContatti(preventivoCom.getRubricaContatti().getContatto());
    }
    reportPreventivoCommessaTestata.setStatoEvasione(preventivoCom.getStatoEvasione());
    reportPreventivoCommessaTestata.setIdValuta(preventivoCom.getIdValuta());
    reportPreventivoCommessaTestata.setIdValutaAz(getIdValutaAziendale());
    //if (preventivoCom.getIdValuta() != null)//33229
    if (preventivoCom.getValuta() != null)//33229
    {
      reportPreventivoCommessaTestata.setDescValuta(getDescrizioneInLingua(preventivoCom.getValuta().getDescrizione(), lingua));
    }
    reportPreventivoCommessaTestata.setFattoreCambio(preventivoCom.getFattoreCambio());
    reportPreventivoCommessaTestata.setIdListinoVen(preventivoCom.getIdListinoVen());
    //if (preventivoCom.getIdListinoVen() != null)//33229
    if (preventivoCom.getListinoVen() != null)//33229
    {
      reportPreventivoCommessaTestata.setDescListinoVen(getDescrizioneInLingua(preventivoCom.getListinoVen().getDescrizione(), lingua));
    }
    reportPreventivoCommessaTestata.setIdListinoAcq(preventivoCom.getIdListinoAcq());
    //if (preventivoCom.getIdListinoAcq() != null)//33229
    if (preventivoCom.getListinoAcq() != null)//33229
    {
      reportPreventivoCommessaTestata.setDescListinoAcq(getDescrizioneInLingua(preventivoCom.getListinoAcq().getDescrizione(), lingua));
    }
    reportPreventivoCommessaTestata.setAmbiente(preventivoCom.getIdAmbiente());
    //if (preventivoCom.getIdAmbiente() != null)//33229
    if (preventivoCom.getAmbienteCosti() != null)//33229
    {
      reportPreventivoCommessaTestata.setDescAmbiente(getDescrizioneInLingua(preventivoCom.getAmbienteCosti().getDescrizione(), lingua));
    }
    reportPreventivoCommessaTestata.setRepCosArt(preventivoCom.getRepCosArt());
    reportPreventivoCommessaTestata.setDataRiferimento(preventivoCom.getDataRiferimento());
    reportPreventivoCommessaTestata.setGgValidita(preventivoCom.getGgValidita());
    reportPreventivoCommessaTestata.setDataFineValc(preventivoCom.getDataFineValc());
    reportPreventivoCommessaTestata.setSettFineValc(preventivoCom.getSettFineValc());
    reportPreventivoCommessaTestata.setDataFineVal(preventivoCom.getDataFineVal());
    reportPreventivoCommessaTestata.setSettFineVal(preventivoCom.getSettFineVal());
    reportPreventivoCommessaTestata.setFlagRisUte1(preventivoCom.getFlagRisUte1());
    reportPreventivoCommessaTestata.setFlagRisUte2(preventivoCom.getFlagRisUte2());
    reportPreventivoCommessaTestata.setFlagRisUte3(preventivoCom.getFlagRisUte3());
    reportPreventivoCommessaTestata.setFlagRisUte4(preventivoCom.getFlagRisUte4());
    reportPreventivoCommessaTestata.setFlagRisUte5(preventivoCom.getFlagRisUte5());
    reportPreventivoCommessaTestata.setStringaRisUte1(preventivoCom.getStringaRisUte1());
    reportPreventivoCommessaTestata.setStringaRisUte2(preventivoCom.getStringaRisUte2());
    reportPreventivoCommessaTestata.setNumRisUte1(preventivoCom.getNumRisUte1());
    reportPreventivoCommessaTestata.setNumRisUte2(preventivoCom.getNumRisUte2());
    reportPreventivoCommessaTestata.setRBatchJobId(new Integer(getBatchJob().getBatchJobId()));
    reportPreventivoCommessaTestata.setReportNumber(new Integer(availableReport.getReportNr()));
    //perc
    reportPreventivoCommessaTestata.setPercentualeVoce(preventivoCom.calcolaPercentualeSu(preventivoCom.getMdcVociPrevc(), preventivoCom.getVlrVociPrevc()));
    reportPreventivoCommessaTestata.setPercentualeArtic(preventivoCom.calcolaPercentualeSu(preventivoCom.getMdcArtPrevc(), preventivoCom.getVlrArtPrevc()));
    reportPreventivoCommessaTestata.setPercentualeRisU(preventivoCom.calcolaPercentualeSu(preventivoCom.getMdcRisuPrevc(), preventivoCom.getVlrRisuPrevc()));
    reportPreventivoCommessaTestata.setPercentualeRisM(preventivoCom.calcolaPercentualeSu(preventivoCom.getMdcRismPrevc(), preventivoCom.getVlrRismPrevc()));
    //
    reportPreventivoCommessaTestata.setRigaJobId(++contatoreTestate);

    //Fix 19818 Inizio
    reportPreventivoCommessaTestata.setIdLingua(preventivoCom.getIdLingua());
    if(preventivoCom.getLingua() != null){
      reportPreventivoCommessaTestata.setDesLingua(preventivoCom.getLingua().getDescription());
    }
    //Fix 19818 Fine
    //33048 inizio
    if (this.getStampaCommenti()) {
      try {
    	  reportPreventivoCommessaTestata.caricaCommenti(preventivoCom, entity, task, lingua);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    //33048 fine
    return reportPreventivoCommessaTestata;
  }

  public String getDescrizioneEnum(String enumRef, char value)
  {
    return new com.thera.thermfw.type.EnumType(enumRef).descriptionFromValue(String.valueOf(value));
  }

  public void fillDatiWorkFlow(PreventivoCommessaTestata preventivoCom)
  {
    reportPreventivoCommessaTestata.setWfClassId(preventivoCom.getWfStatus().getWfClassId());
    reportPreventivoCommessaTestata.setWfId(preventivoCom.getWfStatus().getWfId());
    reportPreventivoCommessaTestata.setWfNodeId(preventivoCom.getWfStatus().getCurrentNodeId());
    reportPreventivoCommessaTestata.setWfSubNodeId(preventivoCom.getWfStatus().getCurrentSubNodeId());
    reportPreventivoCommessaTestata.setWfDescription(preventivoCom.getWfStatus().getDescription());
  }

  public void fillValori(PreventivoCommessaTestata preventivoCom)
  {
    reportPreventivoCommessaTestata.setVlrVociPrevc(preventivoCom.getVlrVociPrevc());
    reportPreventivoCommessaTestata.setVlrArtPrevc(preventivoCom.getVlrArtPrevc());
    reportPreventivoCommessaTestata.setVlrRisuPrevc(preventivoCom.getVlrRisuPrevc());
    reportPreventivoCommessaTestata.setVlrRismPrevc(preventivoCom.getVlrRismPrevc());
    reportPreventivoCommessaTestata.setVlrTotPrevc(preventivoCom.getVlrTotPrevc());

    reportPreventivoCommessaTestata.setCosVociPrevc(preventivoCom.getCosVociPrevc());
    reportPreventivoCommessaTestata.setCosArtPrevc(preventivoCom.getCosArtPrevc());
    reportPreventivoCommessaTestata.setCosRisuPrevc(preventivoCom.getCosRisuPrevc());
    reportPreventivoCommessaTestata.setCosRismPrevc(preventivoCom.getCosRismPrevc());
    reportPreventivoCommessaTestata.setCosTotPrevc(preventivoCom.getCosTotPrevc());

    reportPreventivoCommessaTestata.setMdcVociPrevc(preventivoCom.getMdcVociPrevc());
    reportPreventivoCommessaTestata.setMdcArtPrevc(preventivoCom.getMdcArtPrevc());
    reportPreventivoCommessaTestata.setMdcRisuPrevc(preventivoCom.getMdcRisuPrevc());
    reportPreventivoCommessaTestata.setMdcRismPrevc(preventivoCom.getMdcRismPrevc());
    reportPreventivoCommessaTestata.setMdcTotPrevc(preventivoCom.getMdcTotPrevc());

    fillValoreAziendale();
  }

  /**
   * fillValoreAziendale
   */
  public void fillValoreAziendale()
  {
    String valuta = preventivoCom.getIdValuta();
    BigDecimal cambio = preventivoCom.getFattoreCambio();
    reportPreventivoCommessaTestata.setVlrVociPrevAz(convertiInValutaAziendale(preventivoCom.getVlrVociPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setVlrArtPrevAz(convertiInValutaAziendale(preventivoCom.getVlrArtPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setVlrRisuPrevAz(convertiInValutaAziendale(preventivoCom.getVlrRisuPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setVlrRismPrevAz(convertiInValutaAziendale(preventivoCom.getVlrRismPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setVlrTotPrevAz(convertiInValutaAziendale(preventivoCom.getVlrTotPrevc(), valuta, cambio));

    reportPreventivoCommessaTestata.setCosVociPrevAz(convertiInValutaAziendale(preventivoCom.getCosVociPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setCosArtPrevAz(convertiInValutaAziendale(preventivoCom.getCosArtPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setCosRisuPrevAz(convertiInValutaAziendale(preventivoCom.getCosRisuPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setCosRismPrevAz(convertiInValutaAziendale(preventivoCom.getCosRismPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setCosTotPrevAz(convertiInValutaAziendale(preventivoCom.getCosTotPrevc(), valuta, cambio));

    reportPreventivoCommessaTestata.setMdcVociPrevAz(convertiInValutaAziendale(preventivoCom.getMdcVociPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setMdcArtPrevAz(convertiInValutaAziendale(preventivoCom.getMdcArtPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setMdcRisuPrevAz(convertiInValutaAziendale(preventivoCom.getMdcRisuPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setMdcRismPrevAz(convertiInValutaAziendale(preventivoCom.getMdcRismPrevc(), valuta, cambio));
    reportPreventivoCommessaTestata.setMdcTotPrevAz(convertiInValutaAziendale(preventivoCom.getMdcTotPrevc(), valuta, cambio));
  }

  protected BigDecimal convertiInValutaAziendale(BigDecimal valore, String valuta, BigDecimal cambio)
  {
    ImportoInValutaEstera ive = (ImportoInValutaEstera)Factory.createObject(ImportoInValutaEstera.class);
    BigDecimal ret = valore;
    if (valore != null && valore.compareTo(new BigDecimal(0)) != 0 &&
        valuta != null && !valuta.equals("") && !valuta.equals(getIdValutaAziendale()) &&
        cambio != null && cambio.compareTo(new BigDecimal(0)) != 0)
    {
      ive.setFattCambioOper(cambio);
      ive.convertiEstPrim(valuta, valore, cambio);
      ret = ive.getImportaValPrim() != null ? ive.getImportaValPrim() : new BigDecimal("0");
      ret = ret.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    return ret;
  }

  public String getIdValutaAziendale()
  {
    PersDatiGen pdg = PersDatiGen.getCurrentPersDatiGen();
    return pdg.getIdValutaPrimaria();
  }

  public void fillDatiGestioneOffertaCliente(PreventivoCommessaTestata preventivoCom)
  {
    reportPreventivoCommessaTestata.setStpAllTec(preventivoCom.getStpAllTec());
    reportPreventivoCommessaTestata.setGenOffc(preventivoCom.getGenOffc());
    reportPreventivoCommessaTestata.setGenRigOffc(preventivoCom.getGenRigOffc());
    reportPreventivoCommessaTestata.setIdAnnoOffc(preventivoCom.getIdAnnoOffc());
    reportPreventivoCommessaTestata.setNumeroOffc(preventivoCom.getIdNumeroOffc());
  }

  public void fillDatiCommenti(PreventivoCommessaTestata preventivoCom)
  {
    if (preventivoCom.getCommenti() != null)
      reportPreventivoCommessaTestata.setRGesCommenti(new Integer(preventivoCom.getCommenti().getId()));
    reportPreventivoCommessaTestata.setRDocumentoMm(preventivoCom.getIdDocumentoMm());
    reportPreventivoCommessaTestata.setNote(preventivoCom.getNote());
  }

  public void fillDatiRif(PreventivoCommessaTestata preventivoCom)
  {
    reportPreventivoCommessaTestata.setRichiedenteCli(preventivoCom.getRichiedenteCli());
    reportPreventivoCommessaTestata.setMailPrvc(preventivoCom.getMailPrvc());
    reportPreventivoCommessaTestata.setFaxPrvc(preventivoCom.getFaxPrvc());
    reportPreventivoCommessaTestata.setAttImporto1(preventivoCom.getAttImporto1());
    reportPreventivoCommessaTestata.setAttImporto2(preventivoCom.getAttImporto2());
    reportPreventivoCommessaTestata.setAttData1(preventivoCom.getAttData1());
    reportPreventivoCommessaTestata.setAttData2(preventivoCom.getAttData2());
    reportPreventivoCommessaTestata.setAttStringa1(preventivoCom.getAttStringa1());
    reportPreventivoCommessaTestata.setAttStringa2(preventivoCom.getAttStringa2());
    reportPreventivoCommessaTestata.setNotaRich(preventivoCom.getNotaRich());
    reportPreventivoCommessaTestata.setIdAutPrevc(preventivoCom.getIdAutPrevc());
    //if (preventivoCom.getIdAutPrevc() != null)//33229
    if (preventivoCom.getAutorePreventivo() != null)//33229
      reportPreventivoCommessaTestata.setDescAutPrevc(preventivoCom.getAutorePreventivo().getDenominazione());
    reportPreventivoCommessaTestata.setIdResPrevc(preventivoCom.getIdRevPrevc());
    //if (preventivoCom.getIdRevPrevc() != null)//33229
    if (preventivoCom.getRevisorePreventivo() != null)//33229
      reportPreventivoCommessaTestata.setDescRevPrevc(preventivoCom.getRevisorePreventivo().getDenominazione());
    reportPreventivoCommessaTestata.setIdAppPrevc(preventivoCom.getIdAppPrevc());
    //if (preventivoCom.getIdAppPrevc() != null)//33229
    if (preventivoCom.getApprovatorePreventivo() != null)//33229
      reportPreventivoCommessaTestata.setDescAppPrevc(preventivoCom.getApprovatorePreventivo().getDenominazione());
    reportPreventivoCommessaTestata.setDataConsegRcs(preventivoCom.getDataConsegRcs());
    reportPreventivoCommessaTestata.setDataConsegPrv(preventivoCom.getDataConsegPrv());
    reportPreventivoCommessaTestata.setSetConsegRcs(preventivoCom.getSetConsegRcs());
    reportPreventivoCommessaTestata.setSetConsegPrv(preventivoCom.getSetConsegRcs());
  }

  public void fillDatiComuniEstesi(PreventivoCommessaTestata preventivoCom)
  {
    reportPreventivoCommessaTestata.setTimestampAgg(preventivoCom.getDatiComuniEstesi().getTimestampAgg());
    reportPreventivoCommessaTestata.setTimestamp(preventivoCom.getDatiComuniEstesi().getTimestampCrz());
    reportPreventivoCommessaTestata.setRUtenteAgg(preventivoCom.getDatiComuniEstesi().getIdUtenteAgg());
    reportPreventivoCommessaTestata.setRUtenteCrz(preventivoCom.getDatiComuniEstesi().getIdUtenteCrz());
    reportPreventivoCommessaTestata.setStato(preventivoCom.getDatiComuniEstesi().getStato());
  }

  public void fillCausalePreventivoCommessa(CausalePreventivoCommessa causalePreventivoCommessa)
  {
    reportPreventivoCommessaTestata.setIdCauPrevc(causalePreventivoCommessa.getIdCauPrevCom());
    String desCauPrevCom = causalePreventivoCommessa.getDescrizione().getHandler().getText("Descrizione", linguaDefault);
    if (getStampaInLingua() && causalePreventivoCommessa.getDescrizione().getHandler().getText("Descrizione", lingua) != null)
    {
      desCauPrevCom = causalePreventivoCommessa.getDescrizione().getHandler().getText("Descrizione", lingua);
    }
    reportPreventivoCommessaTestata.setDescCauPrevc(desCauPrevCom);
  }

  public void fillDatiCliente(PreventivoCommessaTestata preventivoCom)
  {
    if (preventivoCom.getTpIntestPrevc() == PreventivoCommessaTestata.TP_INTES_CLIENTE)
    {
      reportPreventivoCommessaTestata.setIdCliente(preventivoCom.getIdCliente());
    }
    reportPreventivoCommessaTestata.setRagioneSocPrvc(preventivoCom.getRagioneSocPrvc());
    reportPreventivoCommessaTestata.setIndirizzoPrvc(preventivoCom.getIndirizzoPrvc());
    reportPreventivoCommessaTestata.setLocalitaPrvc(preventivoCom.getLocalitaPrvc());
    reportPreventivoCommessaTestata.setCapPrvc(preventivoCom.getCapPrvc());
    reportPreventivoCommessaTestata.setIdNazionePrvc(preventivoCom.getIdNazionePrvc());
    //if (preventivoCom.getIdNazionePrvc() != null)//33229
    if (preventivoCom.getNazione() != null)//33229
    {
      reportPreventivoCommessaTestata.setDescNazionePrvc(getDescrizioneInLingua(preventivoCom.getNazione().getDescrizione(), lingua));
    }
    reportPreventivoCommessaTestata.setIdProvinciaPrvc(preventivoCom.getIdProvinciaPrvc());
  }

  public static String getDescrizioneInLingua(DescrizioneInLingua descrInLingua, String linguaCorrente)
  {
    String linguaDefault = SystemParam.getCompanyDefaultLocale();
    String descr = descrInLingua.getHandler().getText("Descrizione", linguaDefault);
    if (linguaCorrente != null && !linguaCorrente.equals(linguaDefault))
    {
      if (descrInLingua.getHandler().getText("Descrizione", linguaCorrente) != null)
      {
        descr = descrInLingua.getHandler().getText("Descrizione", linguaCorrente);
      }
    }
    return descr;
  }

  public int salvaDatiTestata(RptPreventivoCommessaTestata reportPreventivoCommessaTestata) throws SQLException
  {
    int rc = reportPreventivoCommessaTestata.save();
    if (rc >= ErrorCodes.OK)
    {
      //33048 inizio
      //List righe = preventivoCom.getRighe();
      NodoTestata root = preventivoCom.costruisciAlbero();
      List righe = root.getAllRigheCommessa();
      //33048 fine
      if (!righe.isEmpty())
      {
        int rc1 = fillRigheCommesseSottoCommesse(righe);
        if (rc1 >= ErrorCodes.OK)
        {
          rc = rc1;
        }
        else
        {
          return rc1;
        }
      }
    }
    contatoreRigheGlobaliCommessa += contatoreRigheCommessa;
    return rc;
  }

  public int fillRigheCommesseSottoCommesse(List righeCommesseSottocommesse) throws SQLException
  {
    Iterator iterator = righeCommesseSottocommesse.iterator();

    int rc = 0;
    while (iterator.hasNext())
    {
      preventivoComRiga = (PreventivoCommessaRiga)iterator.next();
      rc = fillRiga(preventivoComRiga);
    }
    return rc;
  }

  public int fillRiga(PreventivoCommessaRiga preventivoComRiga) throws SQLException
  {
    int rc = 0;
    if (preventivoComRiga.getArticolo() != null)
    {
      RptPreventivoCommessaRiga report = caricaDatiRiga(preventivoComRiga);
      if (report != null)
      {
        rc = salvaDatiRiga(report);
      }
    }
    return rc;
  }

  public RptPreventivoCommessaRiga caricaDatiRiga(PreventivoCommessaRiga preventivoComRiga)
  {
    reportPreventivoCommessaRiga = (RptPreventivoCommessaRiga)Factory.createObject(RIGA_REPORT);
    Articolo articolo = preventivoComRiga.getArticolo();
    if (articolo != null)
    {
      contatoreRigheCommessa++;
      reportPreventivoCommessaRiga.setBatchJobId(reportPreventivoCommessaTestata.getRBatchJobId());
      reportPreventivoCommessaRiga.setReportNr(reportPreventivoCommessaTestata.getReportNumber());
      reportPreventivoCommessaRiga.setRigaJobId(new Integer(reportPreventivoCommessaTestata.getRigaJobId()));
      reportPreventivoCommessaRiga.setDetRigaJob(new Integer(contatoreRigheCommessa));

      reportPreventivoCommessaRiga.setIdAzienda(preventivoComRiga.getIdAzienda());
      reportPreventivoCommessaRiga.setIdAnnoPrevc(preventivoComRiga.getIdAnnoPrevc());
      reportPreventivoCommessaRiga.setIdNumeroPrevc(preventivoComRiga.getIdNumeroPrevc());
      reportPreventivoCommessaRiga.setIdRigacPrv(preventivoComRiga.getIdRigacPrv());
      reportPreventivoCommessaRiga.setRigacPrv(preventivoComRiga.getIdRigacPrvApp());
      reportPreventivoCommessaRiga.setSequenzaRiga(preventivoComRiga.getSequenzaRiga());
      reportPreventivoCommessaRiga.setSplRiga(preventivoComRiga.getSplRiga());
      reportPreventivoCommessaRiga.setRCommessa(preventivoComRiga.getIdCommessa());
      //if (preventivoComRiga.getIdCommessa() != null)//33229
      if (preventivoComRiga.getCommessa() != null)//33229
      {
        reportPreventivoCommessaRiga.setDescCommessa(getDescrizioneInLingua(preventivoComRiga.getCommessa().getDescrizione(), lingua));
      }
      reportPreventivoCommessaRiga.setRCommessaApp(preventivoComRiga.getIdCommessaAppartenenza());
      //if (preventivoComRiga.getIdCommessaAppartenenza() != null)//33229
      if (preventivoComRiga.getCommessaAppartenenza() != null)//33229
      {
        reportPreventivoCommessaRiga.setDescCommessaApp(getDescrizioneInLingua(preventivoComRiga.getCommessaAppartenenza().getDescrizione(), lingua));
      }
      reportPreventivoCommessaRiga.setRCommessaPrm(preventivoComRiga.getIdCommessaPrincipale());
      //if (preventivoComRiga.getIdCommessaPrincipale() != null)//33229
      if (preventivoComRiga.getCommessaPrincipale() != null)//33229
      {
        reportPreventivoCommessaRiga.setDescCommessaPrm(getDescrizioneInLingua(preventivoComRiga.getCommessaPrincipale().getDescrizione(), lingua));
      }
      reportPreventivoCommessaRiga.setRArticolo(preventivoComRiga.getIdArticolo());
      reportPreventivoCommessaRiga.setRVersione(preventivoComRiga.getIdVersione());
      reportPreventivoCommessaRiga.setRConfigurazione(preventivoComRiga.getIdConfigurazione());
      //if (preventivoComRiga.getIdConfigurazione() != null)//33229
      if (preventivoComRiga.getConfigurazione() != null)//33229
      {
        String desConfigurazione = preventivoComRiga.getConfigurazione().getDescrizione().getHandler().getText("Descrizione", linguaDefault);
        if (getStampaInLingua() && preventivoComRiga.getConfigurazione().getDescrizione().getHandler().getText("Descrizione", lingua) != null)
        {
          desConfigurazione = preventivoComRiga.getConfigurazione().getDescrizione().getHandler().getText("Descrizione", lingua);
        }
        reportPreventivoCommessaRiga.setDescConfigurazione(desConfigurazione);
      }
      reportPreventivoCommessaRiga.setRUmPrmMag(preventivoComRiga.getIdUmPrmMag());
      reportPreventivoCommessaRiga.setQtaUmPrm(preventivoComRiga.getQtaUmPrm());
      reportPreventivoCommessaRiga.setDataConsegRcs(preventivoComRiga.getDataConsegRcs());
      reportPreventivoCommessaRiga.setDataConsegPrv(preventivoComRiga.getDataConsegPrv());
      reportPreventivoCommessaRiga.setSettConsegRcs(preventivoComRiga.getSettConsegRcs());
      reportPreventivoCommessaRiga.setSettConsegPrv(preventivoComRiga.getSettConsegPrv());
      reportPreventivoCommessaRiga.setVlrLivello(preventivoComRiga.getVlrLivello());
      reportPreventivoCommessaRiga.setCosLivello(preventivoComRiga.getCosLivello());
      reportPreventivoCommessaRiga.setMdcLivello(preventivoComRiga.getMdcLivello());
      reportPreventivoCommessaRiga.setVlrLivelloInf(preventivoComRiga.getVlrLivelloInf());
      reportPreventivoCommessaRiga.setCosLivelloInf(preventivoComRiga.getCosLivelloInf());
      reportPreventivoCommessaRiga.setMdcLivelloInf(preventivoComRiga.getMdcLivelloInf());
      reportPreventivoCommessaRiga.setVlrTotale(preventivoComRiga.getVlrTotale());
      reportPreventivoCommessaRiga.setCosTotale(preventivoComRiga.getCosTotale());
      reportPreventivoCommessaRiga.setMdcTotale(preventivoComRiga.getMdcTotale());
      //
      String valuta = preventivoCom.getIdValuta();
      BigDecimal cambio = preventivoCom.getFattoreCambio();
      reportPreventivoCommessaRiga.setVlrLivelloAz(convertiInValutaAziendale(preventivoComRiga.getVlrLivello(), valuta, cambio));
      reportPreventivoCommessaRiga.setCosLivelloAz(convertiInValutaAziendale(preventivoComRiga.getCosLivello(), valuta, cambio));
      reportPreventivoCommessaRiga.setMdcLivelloAz(convertiInValutaAziendale(preventivoComRiga.getMdcLivello(), valuta, cambio));
      reportPreventivoCommessaRiga.setVlrLivelloInfAz(convertiInValutaAziendale(preventivoComRiga.getVlrLivelloInf(), valuta, cambio));
      reportPreventivoCommessaRiga.setCosLivelloInfAz(convertiInValutaAziendale(preventivoComRiga.getCosLivelloInf(), valuta, cambio));
      reportPreventivoCommessaRiga.setMdcLivelloInfAz(convertiInValutaAziendale(preventivoComRiga.getMdcLivelloInf(), valuta, cambio));
      reportPreventivoCommessaRiga.setVlrTotaleAz(convertiInValutaAziendale(preventivoComRiga.getVlrTotale(), valuta, cambio));
      reportPreventivoCommessaRiga.setCosTotaleAz(convertiInValutaAziendale(preventivoComRiga.getCosTotale(), valuta, cambio));
      reportPreventivoCommessaRiga.setMdcTotaleAz(convertiInValutaAziendale(preventivoComRiga.getMdcTotale(), valuta, cambio));
      //

      reportPreventivoCommessaRiga.setStpAllTes(preventivoComRiga.getStpAllTes());
      reportPreventivoCommessaRiga.setGenRigOffc(preventivoComRiga.getGenRigOffc());
      reportPreventivoCommessaRiga.setRAnnoOff(preventivoComRiga.getIdAnnoOff());
      reportPreventivoCommessaRiga.setRNumOff(preventivoComRiga.getNumeroOff());
      reportPreventivoCommessaRiga.setRRigaOff(preventivoComRiga.getRigaOff());
      reportPreventivoCommessaRiga.setRGesCommenti(new Integer(preventivoComRiga.getCommenti().getId()));
      reportPreventivoCommessaRiga.setRDocumentoMm(preventivoComRiga.getIdDocumentoMm());
      reportPreventivoCommessaRiga.setNota(preventivoComRiga.getNota());
      reportPreventivoCommessaRiga.setRUtenteCrz(preventivoComRiga.getDatiComuniEstesi().getIdUtenteCrz());
      reportPreventivoCommessaRiga.setRUtenteAgg(preventivoComRiga.getDatiComuniEstesi().getIdUtenteAgg());
      reportPreventivoCommessaRiga.setTimestamp(preventivoComRiga.getDatiComuniEstesi().getTimestampCrz());
      reportPreventivoCommessaRiga.setTimestampAgg(preventivoComRiga.getDatiComuniEstesi().getTimestampAgg());
      reportPreventivoCommessaRiga.setStato(preventivoComRiga.getDatiComuniEstesi().getStato());
      //33048 inizio
      //reportPreventivoCommessaRiga.setDescrizione(preventivoComRiga.getDescrizione().getDescrizione()); 
      //reportPreventivoCommessaRiga.setDescrRidotta(preventivoComRiga.getDescrizione().getDescrizioneRidotta());
      reportPreventivoCommessaRiga.setDescrizione(getDescrizioneInLingua(preventivoComRiga.getDescrizione(), lingua)); 
      reportPreventivoCommessaRiga.setDescrRidotta(getDescrizioneRidottaInLingua(preventivoComRiga.getDescrizione(), lingua)); 
      //33048 fine
      reportPreventivoCommessaRiga.setLivello(getLivelloRiga(preventivoComRiga));
      reportPreventivoCommessaRiga.setPercentuale(preventivoComRiga.getPercentuale());
      if (this.getStampaCommenti())
      {
        try
        {
          reportPreventivoCommessaRiga.caricaCommenti(preventivoComRiga, entity, task, lingua);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

      }
    }
    else
    {
      contatoreRigheCommessa++;
      return null;
    }

    return reportPreventivoCommessaRiga;
  }

  public AvailableReport getModelloScelto(int sequenza, String entity, String gruppo)
  {
    ReportModel reportObj;
    try
    {
      String where = ReportModelTM.ENTITY_ID + "= '" + entity + "' AND " + ReportModelTM.GROUP + "= '" + gruppo + "'";
      List report = ReportModel.retrieveList(where, "", false);
      Iterator it = report.iterator();
      boolean trouve = false;
      while (it.hasNext() && !trouve)
      {
        reportObj = (ReportModel)it.next();
        if (reportObj.getSequence() == sequenza)
        {
          availableReport = createNewReport(reportObj.getReportModelId());
          trouve = true;
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream);
    }
    return availableReport;
  }

  public int salvaDatiRiga(RptPreventivoCommessaRiga reportPreventivoCommessaRiga) throws SQLException
  {
    int rc = reportPreventivoCommessaRiga.save();
    if (rc >= ErrorCodes.OK)
    {
      //33048 inizio
      //List voce = preventivoComRiga.getRighe();
      List voce = preventivoComRiga.getVoci();
      List risorse = preventivoComRiga.getRisorse();
      List articoli = preventivoComRiga.getArticoli();
      List righe = new ArrayList();
      if(voce != null && !voce.isEmpty()) {
    	  righe.addAll(voce);
      }
      if(articoli != null && !articoli.isEmpty()) {
    	  righe.addAll(articoli);
      }
      if(risorse != null && !risorse.isEmpty()) {
    	  righe.addAll(risorse);
      }

      //if (!voce.isEmpty())
      if (!righe.isEmpty())
      {
        //int rc1 = fillRigheVoce(voce);
    	int rc1 = fillRigheVoce(righe);
    	//33048 fine
        rc = rc1;
      }
    }
    contatoreRigheGlobaliVoce += contatoreRigheVoce;
    return rc;
  }

  public int fillRigheVoce(List righeVoce) throws SQLException
  {
    Iterator iterator = righeVoce.iterator();
    int rc = 0;
    while (iterator.hasNext())
    {
      preventivoComVoce = (PreventivoCommessaVoce)iterator.next();
      int rc1 = fillVoce(preventivoComVoce);
      if (rc1 >= ErrorCodes.OK)
      {
        rc = rc1;
      }
      else
      {
        return rc1;

      }
    }
    return rc;
  }

  public int fillRigheSecondarie(PreventivoCommessaVoce preventivoComVoce) throws SQLException
  {
    int rc = 0;
    if (preventivoComVoce.getTipoRigav() == RptPreventivoCommessaVoce.TP_RIG_VOCE)
    {
      Iterator righeSec = preventivoComVoce.getRighe().iterator();
      while (righeSec.hasNext())
      {
    	//33048 inizio
    	PreventivoCommessaVoce voceDet = (PreventivoCommessaVoce)righeSec.next();
        //int rc1 = fillVoce((PreventivoCommessaVoce)righeSec.next());
    	this.preventivoComVoce = voceDet;
        int rc1 = fillVoce(voceDet);
        //33048 fine
        if (rc1 >= ErrorCodes.OK)
        {
          rc = rc + rc1;
        }
        else
        {
          rc = rc1;
          break;
        }
      }
    }
    return rc;
  }

  public int fillVoce(PreventivoCommessaVoce preventivoComVoce) throws SQLException
  {
    int rc = 0;
    RptPreventivoCommessaVoce report = caricaDatiVoce(preventivoComVoce);
    if (report != null)
    {
      rc = salvaDatiVoce(report);
    }
    return rc;
  }

  public RptPreventivoCommessaVoce caricaDatiVoce(PreventivoCommessaVoce preventivoComVoce)
  {
    reportPreventivoCommessaVoce = (RptPreventivoCommessaVoce)Factory.createObject(VOCE_REPORT);
    Articolo articolo = preventivoComVoce.getArticolo();
    // if (articolo != null) {
    contatoreRigheVoce++;
    reportPreventivoCommessaVoce.setBatchJobId(reportPreventivoCommessaTestata.getRBatchJobId());
    reportPreventivoCommessaVoce.setReportNr(reportPreventivoCommessaTestata.getReportNumber());
    reportPreventivoCommessaVoce.setRigaJobId(reportPreventivoCommessaRiga.getRigaJobId());
    reportPreventivoCommessaVoce.setDetRigaJob(reportPreventivoCommessaRiga.getDetRigaJob());
    reportPreventivoCommessaVoce.setDetRigaJob2(new Integer(contatoreRigheVoce));
    reportPreventivoCommessaVoce.setIdAzienda(preventivoComVoce.getIdAzienda());
    reportPreventivoCommessaVoce.setIdAnnoPrevc(preventivoComVoce.getIdAnnoPrevc());
    reportPreventivoCommessaVoce.setIdNumeroPrevc(preventivoComVoce.getIdNumeroPrevc());
    reportPreventivoCommessaVoce.setIdRigacPrv(preventivoComVoce.getIdRigacPrv());
    reportPreventivoCommessaVoce.setIdSubRigacPrv(preventivoComVoce.getIdSubRigacPrv());
    reportPreventivoCommessaVoce.setIdRigavPrv(preventivoComVoce.getIdRigavPrv());
    reportPreventivoCommessaVoce.setIdSubRigavPrv(preventivoComVoce.getIdSubRigavPrv());
    reportPreventivoCommessaVoce.setLivello(getLivelloVoce(preventivoComVoce));
    reportPreventivoCommessaVoce.setPercentuale(preventivoComVoce.getPercentuale());
    if (this.getStampaCommenti())
    {
      try
      {
        reportPreventivoCommessaVoce.caricaCommenti(preventivoComVoce, entity, task, lingua);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    reportPreventivoCommessaVoce.setSequenzaRiga(preventivoComVoce.getSequenzaRiga());
    reportPreventivoCommessaVoce.setSplRiga(preventivoComVoce.getSplRiga());
    reportPreventivoCommessaVoce.setTipoRigav(preventivoComVoce.getTipoRigav());

    reportPreventivoCommessaVoce.setRArticolo(preventivoComVoce.getIdArticolo());
    reportPreventivoCommessaVoce.setRVersione(preventivoComVoce.getIdVersione());
    //reportPreventivoCommessaVoce.setRConfigurazione(new Integer(preventivoComVoce.getIdConfigurazione()));//29731
    reportPreventivoCommessaVoce.setRConfigurazione(preventivoComVoce.getIdConfigurazione());//29731
    if (preventivoComVoce.getConfigurazione() != null)
    {
      String desConfigurazione = preventivoComVoce.getConfigurazione().getDescrizione().getHandler().getText("Descrizione", linguaDefault);
      if (getStampaInLingua() && preventivoComVoce.getConfigurazione().getDescrizione().getHandler().getText("Descrizione", lingua) != null)
      {
        desConfigurazione = preventivoComVoce.getConfigurazione().getDescrizione().getHandler().getText("Descrizione", lingua);
      }
      reportPreventivoCommessaVoce.setDescConfigurazione(desConfigurazione);
    }
    reportPreventivoCommessaVoce.setTipoRisorsa(preventivoComVoce.getTipoRisorsa());
    reportPreventivoCommessaVoce.setLivelloRisorsa(preventivoComVoce.getLivelloRisorsa());
    reportPreventivoCommessaVoce.setIdRisorsa(preventivoComVoce.getIdRisorsa());
    //if (preventivoComVoce.getIdRisorsa() != null)//33229
    if (preventivoComVoce.getRisorsa() != null)//33229
    {
      reportPreventivoCommessaVoce.setDescRisorsa(getDescrizioneInLingua(preventivoComVoce.getRisorsa().getDescrizione(), lingua));
    }
    reportPreventivoCommessaVoce.setRSchemaCosto(preventivoComVoce.getRSchemaCosto());
    //if (preventivoComVoce.getRSchemaCosto() != null)//33229
    if (preventivoComVoce.getSchemaCosto() != null)//33229
    {
      reportPreventivoCommessaVoce.setDescSchemaCosto(getDescrizioneInLingua(preventivoComVoce.getSchemaCosto().getDescrizione(), lingua));
    }
    reportPreventivoCommessaVoce.setRComponCosto(preventivoComVoce.getRComponCosto());
    if (preventivoComVoce.getComponenteCosto() != null)
    {
      reportPreventivoCommessaVoce.setDescComponCosto(getDescrizioneInLingua(preventivoComVoce.getComponenteCosto().getDescrizione(), lingua));
    }
    reportPreventivoCommessaVoce.setRUmPrmMag(preventivoComVoce.getIdUmPrmMag());
    reportPreventivoCommessaVoce.setRUmSecMag(preventivoComVoce.getIdUmSecMag());
    reportPreventivoCommessaVoce.setRUmVen(preventivoComVoce.getIdUmVen());

    reportPreventivoCommessaVoce.setQtaPrvUmPrm(preventivoComVoce.getQtaPrvUmPrm());
    reportPreventivoCommessaVoce.setQtaPrvUmSec(preventivoComVoce.getQtaPrvUmSec());
    reportPreventivoCommessaVoce.setQtaPrvUmVen(preventivoComVoce.getQtaPrvUmVen());

    reportPreventivoCommessaVoce.setDataConsegRcs(preventivoComVoce.getDataConsegRcs());
    reportPreventivoCommessaVoce.setDataConsegPrv(preventivoComVoce.getDataConsegPrv());
    reportPreventivoCommessaVoce.setSettConsegRcs(preventivoComVoce.getSettConsegRcs());
    reportPreventivoCommessaVoce.setSettConsegPrv(preventivoComVoce.getSettConsegPrv());

    reportPreventivoCommessaVoce.setBlcQtaCmp(preventivoComVoce.isBlcQtaCmp());
    reportPreventivoCommessaVoce.setCoeffImp(preventivoComVoce.getCoeffImp());
    reportPreventivoCommessaVoce.setPrezzo(preventivoComVoce.getPrezzo());
    String valuta = preventivoCom.getIdValuta();
    BigDecimal cambio = preventivoCom.getFattoreCambio();
    reportPreventivoCommessaVoce.setPrezzoAz(convertiInValutaAziendale(preventivoComVoce.getPrezzo(), valuta, cambio));

    reportPreventivoCommessaVoce.setPrezzoExtra(preventivoComVoce.getPrezzoExtra());
    reportPreventivoCommessaVoce.setNoFattura(preventivoComVoce.isNoFattura());
    reportPreventivoCommessaVoce.setProvenienzaPrz(preventivoComVoce.getProvenienzaPrz());
    reportPreventivoCommessaVoce.setTpPrezzo(preventivoComVoce.getTpPrezzo());
    reportPreventivoCommessaVoce.setCostoRifer(preventivoComVoce.getCostoRifer());
    reportPreventivoCommessaVoce.setVlrTotale(preventivoComVoce.getVlrTotale());
    reportPreventivoCommessaVoce.setCosTotale(preventivoComVoce.getCosTotale());
    reportPreventivoCommessaVoce.setMdcTotale(preventivoComVoce.getMdcTotale());
    //
    reportPreventivoCommessaVoce.setCostoRiferAz(convertiInValutaAziendale(preventivoComVoce.getCostoRifer(), valuta, cambio));
    reportPreventivoCommessaVoce.setVlrTotaleAz(convertiInValutaAziendale(preventivoComVoce.getVlrTotale(), valuta, cambio));
    reportPreventivoCommessaVoce.setCosTotaleAz(convertiInValutaAziendale(preventivoComVoce.getCosTotale(), valuta, cambio));
    reportPreventivoCommessaVoce.setMdcTotaleAz(convertiInValutaAziendale(preventivoComVoce.getMdcTotale(), valuta, cambio));
    //
    reportPreventivoCommessaVoce.setEscRigOffc(preventivoComVoce.isEscRigOffc());

    reportPreventivoCommessaVoce.setRAnnoOff(preventivoComVoce.getRAnnoOff());
    reportPreventivoCommessaVoce.setRNumOff(preventivoComVoce.getRNumOff());
    reportPreventivoCommessaVoce.setRRigaOff(preventivoComVoce.getRRigaOff());
    reportPreventivoCommessaVoce.setRDetRigOff(preventivoComVoce.getRDetRigOff());

    reportPreventivoCommessaVoce.setRGesCommenti(new Integer(preventivoComVoce.getCommenti().getId()));
    reportPreventivoCommessaVoce.setRDocumentoMm(preventivoComVoce.getRDocumentoMm());
    reportPreventivoCommessaVoce.setNota(preventivoComVoce.getNota());
    reportPreventivoCommessaVoce.setRUtenteCrz(preventivoComVoce.getDatiComuniEstesi().getIdUtenteCrz());
    reportPreventivoCommessaVoce.setRUtenteAgg(preventivoComVoce.getDatiComuniEstesi().getIdUtenteAgg());
    reportPreventivoCommessaVoce.setTimestamp(preventivoComVoce.getDatiComuniEstesi().getTimestampCrz());
    reportPreventivoCommessaVoce.setTimestampAgg(preventivoComVoce.getDatiComuniEstesi().getTimestampAgg());
    reportPreventivoCommessaVoce.setStato(preventivoComVoce.getDatiComuniEstesi().getStato());
    //33048 inizio
    //reportPreventivoCommessaVoce.setDescrizione(preventivoComVoce.getDescrizione().getDescrizione()); 
    //reportPreventivoCommessaVoce.setDescrRidotta(preventivoComVoce.getDescrizione().getDescrizioneRidotta()); 
    reportPreventivoCommessaVoce.setDescrizione(getDescrizioneInLingua(preventivoComVoce.getDescrizione(), lingua)); 
    reportPreventivoCommessaVoce.setDescrRidotta(getDescrizioneRidottaInLingua(preventivoComVoce.getDescrizione(), lingua)); 
    //33048 fine
    return reportPreventivoCommessaVoce;
  }

  protected String getLivelloVoce(PreventivoCommessaVoce voce)
  {
    if (voce.getIdSubRigavPrv() != 0)
      return "";
    if (voce.getSplRiga() == PreventivoCommessaVoce.RIGA_PRIMARIA)
      return getLivelloRiga(voce.getPrevComRiga());
    return getLivelloRiga(voce.getPrevComRiga()) + LIVELLO1;
  }

  protected String getLivelloRiga(PreventivoCommessaRiga riga)
  {
    int livello = riga.calcolaLivello(riga, 1);
    /*int i = 0;
    String retLiv = "";
    while (i < livello)
    {
      retLiv += LIVELLO1;
      i++;
    }
    return retLiv;*///15848
    return formatLivello(livello);
  }

  /**
   * formatLivello
   *
   * @param string String
   */
  protected String formatLivello(int livello)
  {
    if(livello<10)
      return "0"+String.valueOf(livello);
    return String.valueOf(livello);
  }

  public int salvaDatiVoce(RptPreventivoCommessaVoce report) throws SQLException
  {
    int rc = report.save();
    //33048 inizio
    if (preventivoComVoce.getTipoRigav() == RptPreventivoCommessaVoce.TP_RIG_VOCE && !preventivoComVoce.getRighe().isEmpty()){
    	rc = fillRigheSecondarie(preventivoComVoce);
    }
    //33048 fine
    return rc;
  }

  public PersistentLanguage getLingua()
  {
    if (preventivoCom.getTpIntestPrevc() == PreventivoCommessaTestata.TP_INTES_CLIENTE)
      return preventivoCom.getCliente().getCliente().getLingua();
    else
      return null;
  }

  protected Set getElencoLingue(String where)
  {
    String sql = "SELECT DISTINCT R_LINGUA FROM " + PreventivoCommessaTestataTM.TABLE_NAME + " WHERE " + where;
    CachedStatement stmt = new CachedStatement(sql);
    Set elencoLingue = new TreeSet();
    if (getStampaInLingua())
    {
      ResultSet rs = null;
      try
      {
        rs = stmt.executeQuery();
        while (rs.next())
        {
          String idLingua = rs.getString(1);
          if (idLingua != null)
            elencoLingue.add(idLingua.trim());
          else
            elencoLingue.add(linguaDefault);

        }
      }
      catch (SQLException e)
      {
        elencoLingue.add(linguaDefault);
        e.printStackTrace(Trace.excStream);
      }
      finally
      {
        try
        {
          if (rs != null)
            rs.close();
          stmt.free();
        }
        catch (SQLException e)
        {
          e.printStackTrace(Trace.excStream);
        }
      }
    }
    else
      elencoLingue.add(linguaDefault);
    return elencoLingue;

  }

  // fix 14965 begin
  //Creazione Documento Digitale
  protected void creaDocDgt()
  {
    if (isDocDgtEnabled())
    {
      String key = KeyHelper.buildObjectKey(new String[]
                                            {Azienda.getAziendaCorrente(), availableReport.getReportModelId()});
      try
      {
        DescrittoreStampaDgt descrStpDgt = DescrittoreStampaDgt.elementWithKey(key, PersistentObject.NO_LOCK);
        if (descrStpDgt != null)
        {
          CreaDocumentoDigitale cDocDgt = (CreaDocumentoDigitale)Factory.createObject("it.thera.thip.base.documentoDgt.CreaDocumentoDigitale");
          cDocDgt.initialize(descrStpDgt);
          cDocDgt.setAnnoDoc(preventivoCom.getIdAnnoPrevc());
          cDocDgt.setNumeroDoc(preventivoCom.getIdNumeroPrevc());
          cDocDgt.setDataDoc(preventivoCom.getDataPrevc());
          if (preventivoCom.getIdCliente() != null)
            cDocDgt.setIdCliente(preventivoCom.getIdCliente());
          if (preventivoCom.getIdAnagrafico() != null)
            cDocDgt.setIdProspect(preventivoCom.getIdAnagrafico());
          if (preventivoCom.getIdRubContatti() != null)
            cDocDgt.setIdContattoWPU(preventivoCom.getIdRubContatti());
          cDocDgt.creaDocumentoDigitale();
        }
      }
      catch (SQLException e)
      {
        e.printStackTrace(output);
      }
    }
  }

  // fix 14965 end
  public void impostaFiltroNumeroDataOrd(String numeroDoc, String annoDoc, java.sql.Date data)
  {
    Vector colonne = condFiltro.getColonneFiltro();
    for (int i = 0; i < colonne.size(); i++)
    {
      ColonneFiltri colFiltro = (ColonneFiltri)colonne.elementAt(i);
      if (colFiltro.getClassAdName().equals("NumeroPrevCom"))
      {
        try
        {
          int size = colFiltro.getAdditionalClassAd().getType().getSize();
          while (annoDoc.length() < size)
          {
            annoDoc += " ";
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace(Trace.excStream);
        }
        String filtro = annoDoc + ColonneFiltri.ANNO_SEP + numeroDoc;
        CondizioniFiltri.svuotaColonnaFiltro(colFiltro);
        CondizioniFiltri.impostaFiltroFrom(colFiltro, filtro);
        CondizioniFiltri.impostaFiltroTo(colFiltro, filtro);
      }
      else if (colFiltro.getClassAdName().equals("DataPrevCom"))
      {
        DateType dt = new DateType();
        String dataStr = dt.objectToString(data);
        CondizioniFiltri.svuotaColonnaFiltro(colFiltro);
        CondizioniFiltri.impostaFiltroFrom(colFiltro, dataStr);
        CondizioniFiltri.impostaFiltroTo(colFiltro, dataStr);
      }
    }
  }

  public void impostaOrdinamentoNumeroDoc()
  {
    Vector colonneSel = new Vector();
    colonneSel.add(cf);
    getOrdinamento().setColonneSelezionate(colonneSel);
  }

  public void setStpInValutaAz(boolean flag)
  {
    this.iStpInValutaAz = flag;
  }

  public boolean getStpInValutaAz()
  {
    return this.iStpInValutaAz;
  }
  
  //33048 inizio 
  public static String getDescrizioneRidottaInLingua(DescrizioneInLingua descrInLingua, String linguaCorrente) {
    String linguaDefault = SystemParam.getCompanyDefaultLocale();
    String descr = descrInLingua.getHandler().getText("DescrizioneRidotta", linguaDefault);
    if (linguaCorrente != null && !linguaCorrente.equals(linguaDefault)) {
      if (descrInLingua.getHandler().getText("DescrizioneRidotta", linguaCorrente) != null) {
        descr = descrInLingua.getHandler().getText("DescrizioneRidotta", linguaCorrente);
      }
    }
    return descr;
  }
  //33048 fine

}

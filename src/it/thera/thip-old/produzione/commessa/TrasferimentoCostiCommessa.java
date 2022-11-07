package it.thera.thip.produzione.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;
import com.thera.thermfw.ad.*;
import com.thera.thermfw.base.*;
import com.thera.thermfw.batch.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.formula.*;
import com.thera.thermfw.formula.Formula;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;
import com.thera.thermfw.type.*;
import it.thera.thip.base.articolo.*;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.datiTecnici.*;
import it.thera.thip.datiTecnici.configuratore.*;
import it.thera.thip.datiTecnici.costi.*;
import it.thera.thip.datiTecnici.modpro.*;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.cs.ThipException;

/**
 * TrasferimentoCostiCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Jed
 */
/*
 * Revisions:
 * Number Date        Owner  Description
 * 04171  01/08/2005  Jed    Aggunto Methodo updateAmbienteCosti e getIdCommessa
 *        17/10/2005  Jed    Calcolo costo in the case of risorsa a costo
 * 04567  01/11/2005  Jed    Impostare CostoElementare='Si' in the case of risorsa
 * 06773  27/02/2007  LP     Riallineamento a seguito della fix 5626
 * 07430  09/07/2007  LP     Correzioni per gestione trace sui log
 * 08631  31/01/2008  GN     Corretto problemi con cache di formule
 * 08938  28/03/2008  LP     Corretto aggregazione delle oreRichieste nei costi di commessa per risorsa
 * 09213  13/05/2008  LP     Corretto aggregazione della quantità nei costi di commessa per risorsa
 * 09598  24/07/2008  DBot   Tolta dalla esplosione del modello l'esplosione delle parti a standard
 * 09966  24/10/2008  GN     Modifiche in modo da scrivere sul costo 2 risorse anche quando hanno attività uguale,
 *                           ma idOperazione diversa
 * 11204  28/07/2009  MN     Modificato metodo checkIdAmbienteCosti(), restituire errore se lo stato ambiente costi
 *                           è diverso da "Costi calcolati" e "calcolo parziale"
 * 14964  12/01/2012  FM     Trasferimento costi da preventivo commessa
 * 15848  05/03/2012  FM     Correzione varie
 * 17265  19/12/2012  FM     Correzione trasferimento costi da preventivo commessa
 * 20569  08/04/2014  AA     Indrodutto qualche controlli per evitare problemi in trasf. dei costi da preventivo commessa
 * 21964  01/09/2015  AA     Aggiunto metodo checkRighePreventivoCommessa
 * 22273  07/10/2015  AA     Aggiunto il controllo sulla valuta del preventivo commessa & Altre correzioni
 * 22355  02/12/2015  AA     Aggiunto metodo checkRighePreventivoVoce
 * 27458  21/05/2018  DBot   Modificato trasferimento comp costo per art no commessa
 * 27472  31/05/2018  DBot   Modificato trasferimento per anomalia in assenza modello
 * 31513  11/01/2021  RA	 Corretto problema di loop
 * 32902  10/02/2021  RA	 Corretto calcolo costi per righe voce con dettaglio
 * 33008  25/02/2021  RA	 Modifica calcolo costi 
 * 34943  27/12/2021  Mekki  Correggere getCommessa()
 * 34585  26/11/2021  RA	 Aggiunto poosibilita di trasferCostiCommessaDaPreventivo per Budget
 */

public class TrasferimentoCostiCommessa
  extends ElaboratePrintRunnable
{

  //...NON_SIGN_CHAR
  public final static char NON_SIGN_CHAR = '-';

  //...TipologiaCosto
  public final static char TIPOLOGIA_COSTO__COSTO_PREVENTIVATO = '0';
  public final static char TIPOLOGIA_COSTO__PREZZO_PREVENTIVATO = '1';
  public final static char TIPOLOGIA_COSTO__COSTO_PREVISTO = '2';

  //...Tipo elaborazione
  public final static char TIPO_ELAB_DA_AMB_COS = '1';
  public final static char TIPO_ELAB_DA_PREVENTIVO = '2';

  //...Costante simbolica
  public final static String RESSOURCE_NAME = "it.thera.thip.produzione.commessa.resources.TrasferimentoCostiCommessa";

  /**
   * Attributo iIdAzienda.
   */
  protected String iIdAzienda;

  //Fix 14964 inizio
  /**
   * Attributo iTipoElaborazione.
   */
  protected char iTipoElaborazione = TIPO_ELAB_DA_AMB_COS;

  /**
   * Attributo iPreventivoCommessa.
   */
  protected Proxy iPreventivoCommessa = new Proxy(it.thera.thip.base.commessa.PreventivoCommessaTestata.class);

  //Fix 14964 fine

  /**
   * Attributo iAmbienteCosti.
   */
  protected Proxy iAmbienteCosti = new Proxy(AmbienteCosti.class);

  /**
   * Attributo iNonTrasferConAnm.
   */
  protected boolean iNonTrasferConAnm = true;

  /**
   * Attributo iTipologiaCosto.
   */
  protected char iTipologiaCosto = TIPOLOGIA_COSTO__COSTO_PREVENTIVATO;

  /**
   * Attributo iCalcoloUfficiale.
   */
  protected boolean iCalcoloUfficiale = true;

  /**
   * Attributo iExecutePrintAnm.
   */
  protected boolean iExecutePrintAnm = false;

  /**
   * Attributo iAvailableReport.
   */
  protected AvailableReport iAvailableReport;

  /**
   * Attributo iCostoCmm.
   */
  protected CostiCommessa iCostoCmm;

  /**
   * Attributo iProgressivoStorico.
   */
  protected Integer iProgressivoStorico = new Integer("0");

  /**
   * Attributo iEsisteAnomalie.
   */
  protected boolean iEsisteAnomalie = false;
  
  protected boolean iTrasferimentoPerBudget = false;//34585

  /**
   * Attributo costiDescriptor.
   */
  protected ConnectionDescriptor costiDescriptor = ConnectionManager.getMainConnectionDescriptor().duplicate();

  /**
   * Attributo reportDescriptor.
   */
  protected ConnectionDescriptor reportDescriptor = ConnectionManager.getMainConnectionDescriptor().duplicate();

  /**
   * Attributo iStampaPreventivoCommessa.
   */
  protected boolean iStampaPreventivoCommessa = false;

  /**
   * Attributo iStampaPreventivoCommessaBatch.
   */
  protected StampaPreventivoCommessa iStampaPreventivoCommessaBatch = (StampaPreventivoCommessa)Factory.createObject(StampaPreventivoCommessa.class);
 // 17265 begin
 protected static final String UPDATE_UFF_COSTI_CMM =
   "UPDATE " + CostiCommessaTM.TABLE_NAME +
   " SET " + CostiCommessaTM.UFFICIALE + " = ?, "
   + CostiCommessaTM.TIMESTAMP_AGG + " = " + ConnectionManager.getCurrentDatabase().getCurrTimestampKeywords() +
   " WHERE " + CostiCommessaTM.ID_AZIENDA + " = ?" +
   " AND " + CostiCommessaTM.ID_PROGR_STORIC + " = ?" +
   " AND " + CostiCommessaTM.TIPOLOGIA + " = ?";

 protected static CachedStatement cUpdateUffCostiCmm = new CachedStatement(UPDATE_UFF_COSTI_CMM);
//17265 end
  /**
   * Attributo iPreventivoAvailableRep.
   */
  protected AvailableReport iPreventivoAvailableRep;

  protected boolean iModelloMancante = false; //Fizx 27472
  
  /**
   * Costruttore
   */
  public TrasferimentoCostiCommessa()
  {
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  /**
   * setIdAzienda
   * @param idAzienda String
   */
  public void setIdAzienda(String idAzienda)
  {
    iIdAzienda = idAzienda;
    String key1 = iAmbienteCosti.getKey();
    iAmbienteCosti.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
    //Fix 14964 inizio
    String key2 = iPreventivoCommessa.getKey();
    iPreventivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
    //Fix 14964 fine
  }

  /**
   * getIdAzienda
   * @return String
   */
  public String getIdAzienda()
  {
    return iIdAzienda;
  }

  //Fix 14964 inizio
  /**
   * setTipoElaborazione
   * @param iTipoElaborazione char
   */

  public void setTipoElaborazione(char tipoElaborazione)
  {
    iTipoElaborazione = tipoElaborazione;
  }

  /**
   * getTipoElaborazione
   * @return char
   */

  public char getTipoElaborazione()
  {
    return iTipoElaborazione;
  }

  //Fix 14964 fine

  /**
   * setNonTrasferConAnm
   * @param nonTrasferConAnm boolean
   */
  public void setNonTrasferConAnm(boolean nonTrasferConAnm)
  {
    iNonTrasferConAnm = nonTrasferConAnm;
  }

  /**
   * isNonTrasferConAnm
   * @return boolean
   */
  public boolean isNonTrasferConAnm()
  {
    return iNonTrasferConAnm;
  }

  /**
   * setTipologiaCosto
   * @param tipologiaCosto char
   */
  public void setTipologiaCosto(char tipologiaCosto)
  {
    iTipologiaCosto = tipologiaCosto;
  }

  /**
   * getTipologiaCosto
   * @return char
   */
  public char getTipologiaCosto()
  {
    return iTipologiaCosto;
  }

  /**
   * setCalcoloUfficiale
   * @param calcoloUfficiale boolean
   */
  public void setCalcoloUfficiale(boolean calcoloUfficiale)
  {
    iCalcoloUfficiale = calcoloUfficiale;
  }

  /**
   * isCalcoloUfficiale
   * @return boolean
   */
  public boolean isCalcoloUfficiale()
  {
    return iCalcoloUfficiale;
  }

  /**
   * setExecutePrint
   * @param executePrint boolean
   */
  public void setExecutePrintAnm(boolean executePrint)
  {
    iExecutePrintAnm = executePrint;
  }

  /**
   * isExecutePrint
   * @return boolean
   */
  public boolean isExecutePrintAnm()
  {
    return iExecutePrintAnm;
  }

  /**
   * getAmbienteCosti
   * @return AmbienteCosti
   */
  public AmbienteCosti getAmbienteCosti()
  {
    return (AmbienteCosti)iAmbienteCosti.getObject();
  }

  /**
   * setAmbienteCosti
   * @param ambienteCosti AmbienteCosti
   */
  public void setAmbienteCosti(AmbienteCosti ambienteCosti)
  {
    iAmbienteCosti.setObject(ambienteCosti);
  }

  /**
   * getAmbienteCostiKey
   * @return String
   */
  public String getAmbienteCostiKey()
  {
    return iAmbienteCosti.getKey();
  }

  /**
   * setAmbienteCostiKey
   * @param key String
   */
  public void setAmbienteCostiKey(String key)
  {
    iAmbienteCosti.setKey(key);
  }

  /**
   * getIdAmbienteCosti
   * @return String
   */
  public String getIdAmbienteCosti()
  {
    return KeyHelper.getTokenObjectKey(iAmbienteCosti.getKey(), 2);
  }

  /**
   * setIdAmbienteCosti
   * @param idAmbienteCosti String
   */
  public void setIdAmbienteCosti(String idAmbienteCosti)
  {
    String key = iAmbienteCosti.getKey();
    iAmbienteCosti.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idAmbienteCosti));
  }

  //Fix 14964 inizio
  /**
   * getPreventivoCommessa
   * @return PreventivoCommessaTestata
   */
  public PreventivoCommessaTestata getPreventivoCommessa()
  {
    return (PreventivoCommessaTestata)iPreventivoCommessa.getObject();
  }

  /**
   * setPreventivoCommessa
   * @param preventivoCommessa PreventivoCommessaTestata
   */
  public void setPreventivoCommessa(PreventivoCommessaTestata preventivoCommessa)
  {
    iPreventivoCommessa.setObject(preventivoCommessa);
  }

  /**
   * getPreventivoCommessaKey
   * @return String
   */
  public String getPreventivoCommessaKey()
  {
    return iPreventivoCommessa.getKey();
  }

  /**
   * setPreventivoCommessaKey
   * @param key String
   */
  public void setPreventivoCommessaKey(String key)
  {
    iPreventivoCommessa.setKey(key);
  }

  /**
   * getAnnoPreventivoCommessa
   * @return String
   */
  public String getAnnoPreventivoCommessa()
  {
    return KeyHelper.getTokenObjectKey(iPreventivoCommessa.getKey(), 2);
  }

  /**
   * setAnnoPreventivoCommessa
   * @param idPreventivoCommessa String
   */
  public void setAnnoPreventivoCommessa(String idPreventivoCommessa)
  {
    String key = iPreventivoCommessa.getKey();
    iPreventivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idPreventivoCommessa));
  }

  /**
   * getIdPreventivoCommessa
   * @return String
   */
  public String getIdPreventivoCommessa()
  {
    return KeyHelper.getTokenObjectKey(iPreventivoCommessa.getKey(), 3);
  }

  /**
   * setIdPreventivoCommessa
   * @param idPreventivoCommessa String
   */
  public void setIdPreventivoCommessa(String idPreventivoCommessa)
  {
    String key = iPreventivoCommessa.getKey();
    iPreventivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idPreventivoCommessa));
  }

  //Fix 14964 fine

  /**
   * setStampaPreventivoCommessa
   * @param stampaPreventivoCommessa boolean
   */
  public void setStampaPreventivoCommessa(boolean stampaPreventivoCommessa)
  {
    iStampaPreventivoCommessa = stampaPreventivoCommessa;
  }

  /**
   * isStampaPreventivoCommessa
   * @return boolean
   */
  public boolean isStampaPreventivoCommessa()
  {
    return iStampaPreventivoCommessa;
  }

  /**
   * getClassAdCollectionName
   * @return String
   */
  protected String getClassAdCollectionName()
  {
    return "TrasferimentoCostiCmm";
  }

  //Fix 14964 inizio

  /**
   * checkIdPreventivoCommessa
   * @return ErrorMessage
   */

  public ErrorMessage checkIdPreventivoCommessa()
  {
    if (getTipoElaborazione() == TIPO_ELAB_DA_PREVENTIVO && getPreventivoCommessa() == null)
    {
      return new ErrorMessage("BAS0000000");
    }
    //Fix 22273 Inizio
    if(getTipoElaborazione() == TIPO_ELAB_DA_PREVENTIVO && getPreventivoCommessa() != null){
      String idValutaAziendale = PersDatiGen.getCurrentPersDatiGen().getIdValutaPrimaria();
      if(getPreventivoCommessa().getIdValuta()!= null && !idValutaAziendale.equals(getPreventivoCommessa().getIdValuta()))
        return new ErrorMessage("THIP40T411");
    }
    //Fix 22273 Fine
    return null;
  }

  //Fix 14964 fine

  /**
   * checkIdAmbienteCosti
   * @return ErrorMessage fix 14964 update ambiente costi reference
   *  15848 ambiente non e obligatoio in caso di preventivo
   */
  public ErrorMessage checkIdAmbienteCosti() {
   AmbienteCosti ambienteCosti = getAmbienteCosti();
   if(ambienteCosti == null  && getTipoElaborazione() == this.TIPO_ELAB_DA_AMB_COS)
      return new ErrorMessage("BAS0000000");
  /* if(getTipoElaborazione() == this.TIPO_ELAB_DA_PREVENTIVO && this.getPreventivoCommessa() != null)
     ambienteCosti = this.getPreventivoCommessa().getAmbienteCosti();*/
   if(getTipoElaborazione() != this.TIPO_ELAB_DA_PREVENTIVO)
   {
     if (ambienteCosti != null && !ambienteCosti.isAmbienteCommessa())
       return new ErrorMessage("THIP20T066");

     if (ambienteCosti != null &&
         ambienteCosti.getStatoAmbiente() != AmbienteCosti.AMB_COSTI_CALCOLATI &&
         ambienteCosti.getStatoAmbiente() != AmbienteCosti.AMB_COSTI_CALCOLO_PARZIALE) // Fix 11204
       return new ErrorMessage("THIP20T067");
     if (ambienteCosti != null)
     {
       Commessa cmm = ambienteCosti.getCommessa();
       return checkCommessa(cmm);
     }
   }
   /* Fix22273 --Blocco commentato
   //Fix 20569 Inizio
   else{
     if(getPreventivoCommessa() != null && getPreventivoCommessa().getIdAmbiente() == null){
        return new ErrorMessage("THIP40T376");
      }
   }
   //Fix 20569 Fine*/
   return null;
 }

  /**
   * checkCommessa
   * @param commessa Commessa
   * @return ErrorMessage update 14964
   */
  protected ErrorMessage checkCommessa(Commessa commessa)
  {
    if (commessa!=null && !commessa.getIdCommessaPrincipale().equals(commessa.getIdCommessa()))
    {
      return new ErrorMessage("THIP20T084");
    }
    if (commessa.getIdArticolo() == null ||
        commessa.getIdVersione() == null ||
        (commessa.getArticolo().getSchemaCfg() != null && commessa.getIdConfigurazione() == null) ||
        commessa.getIdStabilimento() == null ||
        commessa.getQtaUmPrm() == null ||
        commessa.getQtaUmPrm().compareTo(new BigDecimal("0")) <= 0)
    {
      return new ErrorMessage("THIP20T068");
    }
    return null;
  }

  /**
   * checkTipologiaCosto
   * @return ErrorMessage UPDATED ON FIX 14964
   */
  public ErrorMessage checkTipologiaCosto()
  {
    AmbienteCosti ambienteCosti = getAmbienteCosti();
    if (getTipoElaborazione() == this.TIPO_ELAB_DA_PREVENTIVO &&
        this.getPreventivoCommessa() != null &&
        this.getPreventivoCommessa().getAmbienteCosti()!=null)
      ambienteCosti = this.getPreventivoCommessa().getAmbienteCosti();
    if (ambienteCosti != null &&
        ambienteCosti.getTipologiaCostoCmm() == AmbienteCosti.PREVENTIVATO &&
        getTipologiaCosto() == TIPOLOGIA_COSTO__COSTO_PREVISTO)
    {
      return new ErrorMessage("THIP20T069");
    }
    if (ambienteCosti != null &&
        ambienteCosti.getTipologiaCostoCmm() == AmbienteCosti.PREVISTO)
    {
      setTipologiaCosto(TIPOLOGIA_COSTO__COSTO_PREVISTO);
    }
    return null;
  }

  /**
   * checkTipoCosto
   * @return ErrorMessage  UPDATED ON FIX 14964
   */
  public ErrorMessage checkTipoCosto()
  {
    AmbienteCosti ambienteCosti = getAmbienteCosti();
    if (getTipoElaborazione() == this.TIPO_ELAB_DA_PREVENTIVO &&
        this.getPreventivoCommessa() != null &&
        this.getPreventivoCommessa().getAmbienteCosti()!=null)
      ambienteCosti = this.getPreventivoCommessa().getAmbienteCosti();

    if (ambienteCosti != null &&
        ambienteCosti.getCommessa() != null &&
        ambienteCosti.getCommessa().getAmbienteCommessa() != null &&
        !ambienteCosti.getIdTipoCosto().equals(ambienteCosti.getCommessa().getAmbienteCommessa().getIdTipoCosto()))
    {
      return new ErrorMessage("THIP20T078");
    }
    return null;
  }

  /**
   * checkAll
   * @param components BaseComponentsCollection
   * @return Vector
   */
  public Vector checkAll(BaseComponentsCollection components)
  {
    Vector errors = super.checkAll(components);
    ErrorMessage err = checkTipoCosto();
    if (err != null)
    {
      errors.add(err);
    }
    //Fix 21964 Inizio
    ErrorMessage err1 = checkRighePreventivoCommessa();
    if (err1 != null){
      errors.add(err1);
    }
    //Fix 21964 Fine
    //Fix 22355 Inizio
    ErrorMessage err2 = checkRighePreventivoVoce();
    if (err2 != null){
      errors.add(err2);
    }
    //Fix 22355 Fine
    return errors;
  }

  /**
   * createReport
   * @return boolean
   */
  public boolean createReport()
  {
    job.setReportCounter((short)0);
    iAvailableReport = createNewReport(getReportId());
    setPrintToolInterface((PrintingToolInterface)Factory.createObject(CrystalReportsInterface.class));
    String s = printToolInterface.generateDefaultWhereCondition(iAvailableReport, ReportAnomalieTrasfCostiTM.TABLE_NAME);
    iAvailableReport.setWhereCondition(s);
    try
    {
      iAvailableReport.save();
      ConnectionManager.commit();

      int rc = ErrorCodes.NO_ROWS_UPDATED;
      try
      {
        if (getTipoElaborazione() == TIPO_ELAB_DA_AMB_COS) //Fix 14964
        {
          try
          {
            rc = trasferCostiCommessa();
          }
          catch (SQLException ex1)
          {
            ex1.printStackTrace(Trace.excStream);
          }
          //Fix 14964 inizio
        }
        else
        {
          try
          {
            rc = trasferCostiCommessaDaPreventivo(); // 17265
          }
          catch (SQLException ex2)
          {
            ex2.printStackTrace(Trace.excStream);
          }
          //Fix 14964 fine
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace(Trace.excStream);
        output.println(ResourceLoader.getString(RESSOURCE_NAME, "ModelloNonTrovato"));
        return false;
      }

      if (rc < ErrorCodes.NO_ROWS_UPDATED)
      {
        writeErrors(rc);
        setExecutePrint(false);
        doCostiRollback();
        doRptRollback();
        return false;
      }
      else
      {
         //Fix 27472 inizio
         if(iModelloMancante)
         {
            iEsisteAnomalie = true;

            Integer config = (getCommessa().getIdConfigurazione() == null) ? null : getCommessa().getIdConfigurazione();
            String codConfig = (getCommessa().getIdConfigurazione() == null) ? null : getCommessa().getIdEsternoConfig();
            ReportAnomalieTrasfCosti reportAnm = (ReportAnomalieTrasfCosti)Factory.createObject(ReportAnomalieTrasfCosti.class);
            reportAnm.setBatchJobId(getBatchJob().getBatchJobId());
            reportAnm.setReportNr(iAvailableReport.getReportNr());
            reportAnm.setIdAzienda(getIdAzienda());
            reportAnm.setIdAmbiente(getIdAmbienteCosti());
            reportAnm.setIdStabilimento(getCommessa().getIdStabilimento());
            reportAnm.setIdArticolo(getCommessa().getIdArticolo());
            reportAnm.setIdVersione(getCommessa().getIdVersione());
            reportAnm.setIdConfigurazione(config);
            reportAnm.setCodConfig(codConfig);
            reportAnm.setIdCommessa(getCommessa().getIdCommessa());
            ErrorMessage err = new ErrorMessage("THIP110038", new String[] {getCommessa().getIdArticolo()});
            reportAnm.setMessaggioErrore(err.getText());
            reportAnm.setSeverita(ErrorMessage.ERROR);
            reportAnm.setIdArticoloPrd(getCommessa().getIdArticolo());
            reportAnm.setIdVersionePrd(getCommessa().getIdVersione());
            reportAnm.setIdConfigurazionePrd(getCommessa().getIdConfigurazione());
            reportAnm.setIdCommessaPrd(getCommessa().getIdCommessa());
            saveReportObject(reportAnm);
            setExecutePrintAnm(true);
         }
         //Fix 27472 fine
         
        setExecutePrint(isExecutePrintAnm());
        if (iEsisteAnomalie && isNonTrasferConAnm())
        {
          doCostiRollback();
        }
        else
        {
          doCostiCommit();
        }
        if (isStampaPreventivoCommessa())
        {
          prepareStampaPreventivoCommessa();
          eseguiStampaPreventivoCommessa();
        }
        doRptCommit();
      }

    }
    catch (SQLException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
      return false;
    }
    //Fix 27472 inizio
    if(iModelloMancante)
       return false;
    //Fix 27472 fine
    return true;
  }

  //Fix 14964 inizio //17265
  public int trasferCostiCommessaDaPreventivo() throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    List righe = getPreventivoCommessa().getRighe();
    Iterator iter = righe.iterator();
    assegnaProgStoricizzazione();
    while (iter.hasNext())
    {
      PreventivoCommessaRiga prevComRiga = (PreventivoCommessaRiga)iter.next();
      Commessa commessa = prevComRiga.getCommessa();
      // Creazione costi com
      //34585 inizio
      //if ( commessa != null && commessa.getStatoAvanzamento() == Commessa.STATO_AVANZAM__CONFERMATA )
      if ( commessa != null && (commessa.getStatoAvanzamento() == Commessa.STATO_AVANZAM__CONFERMATA || isTrasferimentoPerBudget() ))
      //34585 fine
        calcolaCostiCommessaPreventivo(prevComRiga);
      //Fix 20569 Inizio
      else{
        output.println(new ErrorMessage("THIP40T377").getLongText());
      }
      //Fix 20569 Fine
    }
    //34585 inizio
    if(isTrasferimentoPerBudget())
    	doCostiCommit();
    else {
    //34585 fine
	    ConnectionManager.pushConnection(costiDescriptor);
	    impostaUfficalePreventivo();
	    ConnectionManager.popConnection(costiDescriptor);
    }//34585
    return result;
  }

  public  synchronized void impostaUfficalePreventivo() {
    try {
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(cUpdateUffCostiCmm.getStatement(), 1, isCalcoloUfficiale()?"Y":"N");
      db.setString(cUpdateUffCostiCmm.getStatement(), 2, Azienda.getAziendaCorrente());
      db.setString(cUpdateUffCostiCmm.getStatement(), 3, iProgressivoStorico.toString());
      //db.setString(cUpdateUffCostiCmm.getStatement(), 4, new Character(getAmbienteCosti().getTipologiaCostoCmm()).toString()); //Fix 22273 --Riga Commentata
      //Fix 22273 Inizio
      char tc = getAmbienteCosti() != null ? getAmbienteCosti().getTipologiaCostoCmm() : AmbienteCosti.PREVENTIVATO;
      db.setString(cUpdateUffCostiCmm.getStatement(), 4, new Character(tc).toString());
      //Fix 22273 Fine
      int j = cUpdateUffCostiCmm.executeUpdate();
    }
    catch(Exception ex) {
      ex.printStackTrace(Trace.excStream);
    }
  }

  public int aggregaDetCostiCommessa(CostiCommessaElem costiCmmF, CostiCommessaElem costiCmmD) throws SQLException
     {
       ConnectionManager.pushConnection(costiDescriptor);
       List dettagliF = costiCmmF.getCostiCommessaDet();
       List dettagliD = costiCmmD.getCostiCommessaDet();
       Iterator iter = dettagliF.iterator();
       int rc = ErrorCodes.NO_ROWS_UPDATED;
       while (iter.hasNext())
       {
         CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
         CostiCommessaDet dettaglioD = costiCmmF.getCostoCommessaDettaglio(dettagliD, costoCmmDet.getIdComponCosto());
           if (dettaglioD != null)
           {
             updateCostoCmmDet(costoCmmDet, dettaglioD);
             rc = saveCostiObject(dettaglioD);
           }
       }
        ConnectionManager.popConnection(costiDescriptor);
       return rc;
     }
     protected void updateCostoCmmDet(CostiCommessaDet costoCmmDet1, CostiCommessaDet costoCmmDet2)
     {

       if (costoCmmDet2.getCostoLivelliInf() != null && costoCmmDet1.getCostoTotale() != null)
       {
         costoCmmDet2.setCostoLivelliInf(costoCmmDet2.getCostoLivelliInf().add(costoCmmDet1.getCostoTotale()));
       }
       if (costoCmmDet2.getCostoTotale() != null && costoCmmDet1.getCostoTotale() != null)
      {
          costoCmmDet2.setCostoTotale(costoCmmDet2.getCostoTotale().add(costoCmmDet1.getCostoTotale()));
      }
     }

  private CostiCommessaElem calcolaCostiCommessaPreventivo(PreventivoCommessaRiga prevComRiga) throws SQLException
  {
    Commessa commessa = prevComRiga.getCommessa();
    Integer idVersione = getNotNullInteger(prevComRiga.getIdVersione());
    List prevComRighe = prevComRiga.getSottoCommesse();
    Iterator ite = prevComRighe.iterator();
    int rc = ErrorCodes.NO_ROWS_UPDATED;
    rc = creaCostoCmm(commessa,
                      prevComRiga.getIdArticolo(),
                      idVersione,
                      prevComRiga.getIdConfigurazione(),
                      getPreventivoCommessa().getIdStabilimento());
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return null;
    }
    CostiCommessaElem costoCmmElemP = creaCostiCmmPrev(prevComRiga, commessa,
                          prevComRiga.getArticolo(),
                          idVersione,
                          prevComRiga.getConfigurazione(),
                          commessa.getIdStabilimento(),
                          null,
                          prevComRiga.getQtaUmPrm(),
                          prevComRiga.getIdUmPrmMag(),
                          null);
    if (costoCmmElemP ==null)
    {
      return null;
    }

    while (ite.hasNext())
    {
      PreventivoCommessaRiga prevRiga = (PreventivoCommessaRiga)ite.next();
      CostiCommessaElem costoCmmElemFigli = calcolaCostiCommessaPreventivo(prevRiga);
      aggiornaDetCalculabile(costoCmmElemFigli);
      cacoloCosti(costoCmmElemFigli);
      rc = saveCostiObject(costoCmmElemFigli);
      if(costoCmmElemFigli!=null && costoCmmElemP!=null)
      {
        aggregaDetCostiCommessa(costoCmmElemFigli, costoCmmElemP);
      }
      if (rc < ErrorCodes.NO_ROWS_UPDATED)
      {
        return null;
      }
    }
    //Fix 20569 Inizio
    aggiornaDetCalculabile(costoCmmElemP);
    cacoloCosti(costoCmmElemP);
    rc = saveCostiObject(costoCmmElemP);
    if (costoCmmElemP ==null){
      return null;
    }
    //Fix 20569 Fine
    return costoCmmElemP;
  }


  /**
   * getNotNullInteger
   *
   * @param integer Integer
   * @return String 14964
   */
  private Integer getNotNullInteger(Integer integer)
  {
    if (integer == null)
    {
      return new Integer("1");
    }
    return integer;
  }

  public int trasferCostiVoce(PreventivoCommessaRiga prevComRiga, CostiCommessaElem  costoCmmElem) throws SQLException
 {
   int rc = 0;
   List voci = prevComRiga.getRighe();
   Iterator iter = voci.iterator();
   Commessa commessa = prevComRiga.getCommessa();
   while (iter.hasNext())
   {
     PreventivoCommessaVoce prevComVoce = (PreventivoCommessaVoce)iter.next();
     if (prevComVoce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE)
     {
    	 //32902 inizio
         boolean ok = true;
         Articolo articolo = prevComVoce.getArticolo();
         if (articolo != null) {
           char tipoParte = articolo.getTipoParte();
           char tipoCalcoloPrezzo = articolo.getTipoCalcPrzKit();
           if ((tipoParte == ArticoloDatiIdent.KIT_NON_GEST || tipoParte == ArticoloDatiIdent.KIT_GEST) &&
               tipoCalcoloPrezzo == ArticoloDatiVendita.DA_COMPONENTI)
             ok = false;
         }
         if (prevComVoce.getSplRiga() == PreventivoCommessaVoce.RIGA_SECONDARIA &&
             prevComVoce.getVoceFather() != null && prevComVoce.getVoceFather().getArticolo() != null &&
             prevComVoce.getVoceFather().getArticolo().getTipoCalcPrzKit() == ArticoloDatiVendita.SUL_PRODOTTO_FINITO)
           ok = false;
         //32902 fine

         //if (ok) { //32902//33008    	 
	       rc = creaCostiCmmPrevSenzaDet(prevComVoce, commessa,
	                                     prevComVoce.getArticolo(),
	                                     prevComVoce.getIdVersione(),
	                                     prevComVoce.getConfigurazione(),
	                                     prevComVoce.getPrevComRiga().getTestata().getIdStabilimento(),
	                                     null,
	                                     prevComVoce.getQtaPrvUmVen(),
	                                     prevComVoce.getIdUmVen(),
	                                     null);
	     if (ok) { //33008   
	       rc = elaborateVoce(prevComVoce, costoCmmElem);
         } //32902
     }
     else if (prevComVoce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO)
     {
       rc = creaCostiCmmPrevSenzaDet(prevComVoce, commessa,
                                     prevComVoce.getArticolo(),
                                     prevComVoce.getIdVersione(),
                                     prevComVoce.getConfigurazione(),
                                     prevComVoce.getPrevComRiga().getTestata().getIdStabilimento(),
                                     null,
                                     prevComVoce.getQtaPrvUmVen(),
                                     prevComVoce.getIdUmVen(),
                                     null);

       rc = elaborateArticolo(prevComVoce, costoCmmElem);
      }
     else
     {
       rc = creaCostiCmmRisorsaPRev(prevComVoce);
       if (rc < ErrorCodes.NO_ROWS_UPDATED)
       {
         return rc;
       }
       rc = elaborateRisorsa(prevComVoce, costoCmmElem);
     }

   }
    return rc;
  }

   private int aggiornaDetCalculabile(CostiCommessaElem costoCmm) throws SQLException
    {
      ConnectionManager.pushConnection(costiDescriptor);
      List listaDettglio = costoCmm.getCostiCommessaDetSort();
      List compDaValorizz = new ArrayList();
      List compValorizz = new ArrayList();
      Iterator iteDet = listaDettglio.iterator();
      int rc=0;
      int result=0;
      while (iteDet.hasNext())
      {
        CostiCommessaDet dettaglio = (CostiCommessaDet)iteDet.next();
        if ((dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.CALCOLATA_FORMULA ||
            dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE)&&
            isUtilizzoFormula(costoCmm.getArticolo(), dettaglio))
        {
          compDaValorizz.add(dettaglio);
        }
        else
        {
           compValorizz.add(dettaglio);
        }
      }
      boolean found = false;

      while (compDaValorizz.size() != 0)
      {
        found = false;
        Iterator valorizIte = compDaValorizz.iterator();
        while (valorizIte.hasNext() && !found)
        {
          CostiCommessaDet detCosto = (CostiCommessaDet)valorizIte.next();
          FormulaCosti formulaCosti = detCosto.getComponenteCosto().getFormula();
          //Fix 22273 Inizio
          AmbienteCosti ac = getAmbienteCosti();
          if (ac == null)
            ac = AmbienteCosti.getCurrentAmbienteCosti();
          //Fix 22273 Fine
          Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(detCosto.getComponenteCosto(), ac.getIdAmbiente())); //Fix 8631 //Fix 22273
          formulaDaUtilizz.setVariables(buildVariablesDetCmm(listaDettglio));
          //if (isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), detCosto))//31513
          if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), detCosto)) //31513
          {
           synchronized (this)
            {
              BigDecimal costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
              //Fix 22273 Inizio
              //Fix 33008 --inizio
              /*if (costoCalcolato != null && detCosto.getCostoTotale() != null)
                costoCalcolato = costoCalcolato.add(detCosto.getCostoTotale());*/
              //Fix 33008 --fine
              //Fix 22273 Fine
              detCosto.setCostoTotale(costoCalcolato);
              detCosto.setCostoLivello(costoCalcolato);
              detCosto.setCostoLivelliInf(new BigDecimal("0"));
              rc = this.saveCostiObject(detCosto);
               if (rc < ErrorCodes.NO_ROWS_UPDATED)
              {
               return rc;
              }
             result += rc;
            }
            compDaValorizz.remove(detCosto);
            compValorizz.add(detCosto);
            found = true;
          }
        }
      }
      ConnectionManager.popConnection(costiDescriptor);
      return result;
    }

  protected boolean isUtilizzoFormula(Articolo articolo, CostiCommessaDet detCosto)
 {
   Iterator iter = articolo.getArticoloDatiProduz().getClasseMerclg().getSchemaCosto().getComponenti().iterator();
   while (iter.hasNext())
   {
     LinkCompSchema linkCompSchema = (LinkCompSchema)iter.next();
     if (linkCompSchema.getIdComponenteCosto().equals(detCosto.getIdComponCosto()) && linkCompSchema.isUtilizzoFormula())
     {
       return true;
     }
   }
   return false;
 }


  public int creaCostiCmmRisorsaPRev( PreventivoCommessaVoce rigaRisorsa)throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    boolean risorsaPrp = false;
    Commessa commessa = rigaRisorsa.getPrevComRiga().getCommessa();
    Risorsa risorsa= rigaRisorsa.getRisorsa();
    String idAttivita = null;
    String idOperazione = null;
    char tipoRisorsa = risorsa.getTipoRisorsa();
    char livelloRisorsa = risorsa.getLivelloRisorsa();
    String stb = commessa.getIdStabilimento();
    Articolo artSrv = rigaRisorsa.getRisorsa().getArticoloServizio(); //Fix 22355
    CostiCommessaElem costoCmmElemRsr = creaCostoCmmElemPrev(commessa,
      //rigaRisorsa.getRisorsa().getArticoloServizio(), //Fix 22355 --Riga Commentata
      artSrv, //Fix 22355
      null,
      //rigaRisorsa.getRisorsa().getArticoloServizio().getIdConfigurazioneStd(), //Fix 22355 --Riga Commentata
      artSrv != null ? artSrv.getIdConfigurazioneStd() : null, //Fix 22355
      stb,
      idAttivita,
      rigaRisorsa.getQtaPrvUmPrm(),
      rigaRisorsa.getIdUmVen(),
      rigaRisorsa,
      risorsa.getCentroLavoro().getIdReparto(),
      risorsa.getIdCentroLavoro(),
      tipoRisorsa,
      livelloRisorsa,
      risorsa.getIdRisorsa(),
      risorsaPrp,
      risorsa.getTipoRilevazione(),
      rigaRisorsa.getQtaPrvUmPrm(),
      rigaRisorsa.getQtaPrvUmPrm(),
      idOperazione);

    costoCmmElemRsr.iCalcoloImporto = false;
    ConnectionManager.pushConnection(costiDescriptor);
    costoCmmElemRsr.impostaIdRigaElem();
    ConnectionManager.popConnection(costiDescriptor);
    int rc = saveCostiObject(costoCmmElemRsr);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;
    return result;
  }

   public int elaborateVoce(PreventivoCommessaVoce rigaVoce,CostiCommessaElem  costoCmmElem) throws SQLException
  {
    int rc = 0;
    Commessa commessa = rigaVoce.getPrevComRiga().getCommessa();
    ConnectionManager.pushConnection(costiDescriptor);

    Iterator iter = costoCmmElem.getCostiCommessaDet().iterator();

    ConnectionManager.popConnection(costiDescriptor);
    PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
    while (iter.hasNext())
    {
       CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
       if (costoCmmDet.getIdComponCosto().equals(rigaVoce.getRComponCosto()))
       {
        updateCostoCmmDetPrev(costoCmmDet, rigaVoce);
        rc = saveCostiObject(costoCmmDet);
        costoCmmElem.iCalcoloImporto = false;
        cacoloCosti(costoCmmElem);
        rc = saveCostiObject(costoCmmElem);
        return rc;
      }
    }
    rc = creaCostiDetPrev(rigaVoce.getArticolo(), commessa, rigaVoce.getQtaPrvUmVen(), rigaVoce, costoCmmElem);
    costoCmmElem.iCalcoloImporto = false;
    cacoloCosti(costoCmmElem);
    rc = saveCostiObject(costoCmmElem);
    rc += elaborateRigheSecondaria(rigaVoce, costoCmmElem);
    return rc;
  }


  public int elaborateRigheSecondaria(PreventivoCommessaVoce rigaVoce,CostiCommessaElem  costoCmmElem) throws SQLException
   {
     int rc = 0;
     List righeSec = rigaVoce.getRighe();
     Iterator iter = righeSec.iterator();
     while (iter.hasNext())
     {
       PreventivoCommessaVoce rigaSec = (PreventivoCommessaVoce)iter.next();
       rc = elaborateRigaConSingoloArticolo(rigaSec,costoCmmElem);
     }
     return rc;
   }

  public int elaborateArticolo(PreventivoCommessaVoce rigaArticolo,CostiCommessaElem  costoCmmElem) throws SQLException
 {
   int rc = 0;
   rc = elaborateRigaConSingoloArticolo(rigaArticolo,costoCmmElem);
   return rc;
 }


 public int elaborateRigaConSingoloArticolo(PreventivoCommessaVoce riga, CostiCommessaElem costoCmmElem) throws SQLException
 {
   int rc = 0;
   Commessa commessa = riga.getPrevComRiga().getCommessa();
   ConnectionManager.pushConnection(costiDescriptor);
   Iterator iter = costoCmmElem.getCostiCommessaDet().iterator();
   ConnectionManager.popConnection(costiDescriptor);
   PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
   while (iter.hasNext())
   {
     CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
     if (costoCmmDet.getIdComponCosto().equals(riga.getRComponCosto()))
     {
       updateCostoCmmDetPrev(costoCmmDet, riga);
       rc = saveCostiObject(costoCmmDet);
       costoCmmElem.iCalcoloImporto = false;
       cacoloCosti(costoCmmElem);
       rc = saveCostiObject(costoCmmElem);
       return rc;
     }
   }
   rc = creaCostiDetPrev(riga.getArticolo(), commessa, riga.getQtaPrvUmVen(), riga, costoCmmElem);
   costoCmmElem.iCalcoloImporto = false;
   cacoloCosti(costoCmmElem);
   rc = saveCostiObject(costoCmmElem);
   rc += elaborateRigheSecondaria(riga, costoCmmElem);
   return rc;
 }

public int elaborateRisorsa(PreventivoCommessaVoce riga,CostiCommessaElem  costoCmmElem) throws SQLException
{
  int rc = 0;
  Commessa commessa = riga.getPrevComRiga().getCommessa();
  ConnectionManager.pushConnection(costiDescriptor);
  Iterator iter = costoCmmElem.getCostiCommessaDet().iterator();
  ConnectionManager.popConnection(costiDescriptor);
  PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
  while (iter.hasNext())
  {
    CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
    if (costoCmmDet.getIdComponCosto().equals(riga.getRComponCosto()))
    {
      updateCostoCmmDetPrev(costoCmmDet, riga);
      rc = saveCostiObject(costoCmmDet);
      costoCmmElem.iCalcoloImporto = false;
      cacoloCosti(costoCmmElem);

      rc = saveCostiObject(costoCmmElem);
      return rc;
    }
  }
  rc = creaCostiDetPrev(riga.getArticolo(), commessa, riga.getQtaPrvUmVen(), riga, costoCmmElem);
  costoCmmElem.iCalcoloImporto = false;
  cacoloCosti(costoCmmElem);
  rc = saveCostiObject(costoCmmElem);
  rc += elaborateRigheSecondaria(riga, costoCmmElem);
  return rc;
}

protected void updateCostoCmmDetPrev(CostiCommessaDet costoCmmDet, PreventivoCommessaVoce rigaVoce)
{
  if (costoCmmDet.getCostoTotale() != null && rigaVoce.getCosTotale() != null)
  {
    //Fix 22273 Inizio
    if(isValutaPrevAziendale())
      costoCmmDet.setCostoTotale(costoCmmDet.getCostoTotale().add(rigaVoce.getCosTotale()));
    else
      costoCmmDet.setCostoTotale(costoCmmDet.getCostoTotale().add(rigaVoce.getCosTotaleAZ()));
    //Fix 22273 Fine
  }
  if (costoCmmDet.getCostoLivello() != null && rigaVoce.getCosTotale() != null)
 {
   //Fix 22273 Inizio
    if(isValutaPrevAziendale())
      costoCmmDet.setCostoLivello(costoCmmDet.getCostoLivello().add(rigaVoce.getCosTotale()));
    else
      costoCmmDet.setCostoLivello(costoCmmDet.getCostoLivello().add(rigaVoce.getCosTotaleAZ()));
    //Fix 22273 Fine
 }


}
protected void updateCostoCmmDetPrevSup(CostiCommessaDet costoCmmDet, PreventivoCommessaVoce rigaVoce)
{
  if (costoCmmDet.getCostoLivelliInf() != null && rigaVoce.getCosTotale() != null)
  {
    costoCmmDet.setCostoLivelliInf(costoCmmDet.getCostoLivelliInf().add(rigaVoce.getCosTotale()));
  }
  if (costoCmmDet.getCostoTotale() != null && rigaVoce.getCosTotale() != null)
 {
     costoCmmDet.setCostoTotale(costoCmmDet.getCostoTotale().add(rigaVoce.getCosTotale()));
 }

}

  public int aggiornaCostoElemECostiDet(PreventivoCommessaVoce rigaRisorsa, CostiCommessaElem costoCmmElm) throws SQLException
  {
    Risorsa risorsa = rigaRisorsa.getRisorsa();
    char tipoRisorsa = risorsa.getTipoRisorsa();
    char livelloRisorsa = risorsa.getLivelloRisorsa();
    String idRisorsa = risorsa.getIdRisorsa();
    Commessa commessa = rigaRisorsa.getPrevComRiga().getCommessa();
    BigDecimal qtaMolt = (rigaRisorsa.getTipoRisorsa() == Risorsa.RISORSE_UMANE) ? rigaRisorsa.getQtaPrvUmPrm() : rigaRisorsa.getPrevComRiga().getQtaUmPrm();
    BigDecimal oreRichieste = rigaRisorsa.getQtaPrvUmPrm();
    BigDecimal qta = rigaRisorsa.getPrevComRiga().getQtaUmPrm();

    Costo costo = getCostoPrev(rigaRisorsa, tipoRisorsa, livelloRisorsa, idRisorsa, commessa.getIdStabilimento());
    if (costo == null)
    {
      return addAnomaliePrev(rigaRisorsa ,rigaRisorsa.getIdArticolo(), rigaRisorsa.getIdVersione(), rigaRisorsa.getConfigurazione(), commessa.getIdCommessa(), tipoRisorsa, livelloRisorsa, idRisorsa, costoCmmElm.getIdAttivita(), commessa.getIdStabilimento());
    }

    costoCmmElm.setQuantita(costoCmmElm.getQuantita().add(qta));

    costoCmmElm.setOreRichieste(costoCmmElm.getOreRichieste().add(oreRichieste));
    if (costoCmmElm.getCostoGenerale() != null && costo.getCostoGenerale() != null)
    {
      costoCmmElm.setCostoGenerale(costoCmmElm.getCostoGenerale().add(costo.getCostoGenerale().multiply(qtaMolt)));
    }
    if (costoCmmElm.getCostoIndustriale() != null && costo.getCostoIndustriale() != null)
    {
      costoCmmElm.setCostoIndustriale(costoCmmElm.getCostoIndustriale().add(costo.getCostoIndustriale().multiply(qtaMolt)));
    }
    if (costoCmmElm.getCostoPrimo() != null && costo.getCostoPrimo() != null)
    {
      costoCmmElm.setCostoPrimo(costoCmmElm.getCostoPrimo().add(costo.getCostoPrimo().multiply(qtaMolt)));
    }
    if (costoCmmElm.getCostoRiferimento() != null && costo.getCostoRiferimento() != null)
    {
      costoCmmElm.setCostoRiferimento(costoCmmElm.getCostoRiferimento().add(costo.getCostoRiferimento().multiply(qtaMolt)));
    }
    ConnectionManager.pushConnection(costiDescriptor);
    Iterator iter = costoCmmElm.getCostiCommessaDet().iterator();
    ConnectionManager.popConnection(costiDescriptor);
    while (iter.hasNext())
    {
      CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
      aggiornaCostiCmmDet(true, qtaMolt, costo, costoCmmDet, rigaRisorsa.getRComponCosto());
    }
    costoCmmElm.iCalcoloImporto = false;
    cacoloCosti(costoCmmElm);
    return saveCostiObject(costoCmmElm);

  }

  protected void aggiornaCostiCmmDet(boolean gesCommessa, BigDecimal qta, Costo costo, CostiCommessaDet costoCmmDet, String compCosto) throws SQLException
  {
    /* Iterator iter = costo.getDettagli().iterator();
     if (gesCommessa) {
       while (iter.hasNext()) {
         DettaglioCosto detCosto = (DettaglioCosto) iter.next();
         if (detCosto.getComponenteCosto().equals(costoCmmDet.getComponenteCosto())) {
           updateCostoCmmDet(costoCmmDet, detCosto, qta);
           return;
         }
       }
     }
     else {
       valorizzaCostoDet(costo);
       while (iter.hasNext()) {
         DettaglioCosto detCosto = (DettaglioCosto) iter.next();
         updateCostoCmmDet(costoCmmDet, detCosto, qta);
       }
     }*/
  }

  public int creaCostiCmm(BigDecimal qtaMult, PreventivoCommessaVoce rigaRisorsa, Risorsa risorsa, BigDecimal tempo, BigDecimal qta) throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    boolean risorsaPrp = false;
    Commessa commessa = rigaRisorsa.getPrevComRiga().getCommessa();
    String idAttivita = null;
    String idOperazione = null;
    char tipoRisorsa = risorsa.getTipoRisorsa();
    char livelloRisorsa = risorsa.getLivelloRisorsa();
    String stb = commessa.getIdStabilimento();
    Costo costo = getCostoPrev(rigaRisorsa ,tipoRisorsa, livelloRisorsa, risorsa.getIdRisorsa(), stb);
    if (costo == null)
    {
      String idArticolo = rigaRisorsa.getIdArticolo();
      Integer idVersione = rigaRisorsa.getIdVersione();
      Configurazione configurazione = rigaRisorsa.getConfigurazione();
      String idCommessa = commessa.getIdCommessa();
      return addAnomaliePrev( rigaRisorsa,idArticolo, idVersione, configurazione, idCommessa, tipoRisorsa, livelloRisorsa, risorsa.getIdRisorsa(), idAttivita, stb);
    }
    CostiCommessaElem costoCmmElem = creaCostoCmmElem(commessa,
      rigaRisorsa.getArticolo(),
      rigaRisorsa.getIdVersione(),
      rigaRisorsa.getConfigurazione().getIdConfigurazione(),
      stb,
      idAttivita,
      qtaMult,
      rigaRisorsa.getIdUmVen(),
      costo,
      risorsa.getCentroLavoro().getIdReparto(),
      risorsa.getIdCentroLavoro(),
      tipoRisorsa,
      livelloRisorsa,
      risorsa.getIdRisorsa(),
      risorsaPrp,
      risorsa.getTipoRilevazione(),
      tempo,
      qta,
      idOperazione);

    costoCmmElem.iCalcoloImporto = false;
    ConnectionManager.pushConnection(costiDescriptor);
    costoCmmElem.impostaIdRigaElem();
    ConnectionManager.popConnection(costiDescriptor);
    int rc = saveCostiObject(costoCmmElem);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;

    rc = creaCostiDet(rigaRisorsa.getArticolo(), commessa, qtaMult, costo, costoCmmElem);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;
    return result;
  }
//14964
  /*
  public int creaCostiCmmPrev(PreventivoCommessaVoce voce,BigDecimal qtaMult, PreventivoCommessaVoce rigaRisorsa, Risorsa risorsa, BigDecimal tempo, BigDecimal qta) throws SQLException
 {
   int result = ErrorCodes.NO_ROWS_UPDATED;
   boolean risorsaPrp = false;
   Commessa commessa = rigaRisorsa.getPrevComRiga().getCommessa();
   String idAttivita = null;
   String idOperazione = null;
   char tipoRisorsa = risorsa.getTipoRisorsa();
   char livelloRisorsa = risorsa.getLivelloRisorsa();
   String stb = commessa.getIdStabilimento();
   CostiCommessaElem costoCmmElem = creaCostoCmmElemPrev(commessa,
     rigaRisorsa.getRisorsa().getArticoloServizio(),
     null,
     rigaRisorsa.getRisorsa().getArticoloServizio().getIdConfigurazioneStd(),
     stb,
     idAttivita,
     qtaMult,
     rigaRisorsa.getIdUmVen(),
     voce,
     risorsa.getCentroLavoro().getIdReparto(),
     risorsa.getIdCentroLavoro(),
     tipoRisorsa,
     livelloRisorsa,
     risorsa.getIdRisorsa(),
     risorsaPrp,
     risorsa.getTipoRilevazione(),
     tempo,
     qta,
     idOperazione);

   costoCmmElem.iCalcoloImporto = false;
   ConnectionManager.pushConnection(costiDescriptor);
   costoCmmElem.impostaIdRigaElem();
   ConnectionManager.popConnection(costiDescriptor);
   int rc = saveCostiObject(costoCmmElem);
   if (rc < ErrorCodes.NO_ROWS_UPDATED)
   {
     return rc;
   }
   result += rc;

   rc = creaCostiDetPrev(rigaRisorsa.getArticolo(), commessa, qtaMult, voce, costoCmmElem);
   if (rc < ErrorCodes.NO_ROWS_UPDATED)
   {
     return rc;
   }
   result += rc;
   return result;
 }
*/
  /*
  public CostiCommessaElem getCostoCmmElem(PreventivoCommessaVoce rigaRisorsa) throws SQLException
  {
    Risorsa risorsa = rigaRisorsa.getRisorsa();
    Commessa commessa = rigaRisorsa.getPrevComRiga().getCommessa();
    String where = CostiCommessaElemTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + iProgressivoStorico.toString() +
      " AND " + CostiCommessaElemTM.ID_COMMESSA + " = '" + commessa.getIdCommessa() + "'" +
      " AND " + CostiCommessaElemTM.R_ARTICOLO + " = '" + commessa.getArticolo() + "'" +
      " AND " + CostiCommessaElemTM.R_VERSIONE + " = " + commessa.getIdVersione().intValue() +
      " AND " + CostiCommessaElemTM.R_CONFIGURAZIONE + ((commessa.getIdConfigurazione() == null) ? " IS NULL " : " = " + commessa.getIdConfigurazione().intValue()) +
      " AND " + CostiCommessaElemTM.R_STABILIMENTO + " = '" + commessa.getIdStabilimento() + "'" +
      " AND " + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.RISORSA + "'" +
      " AND " + CostiCommessaElemTM.TIPO_RISORSA + " = '" + risorsa.getTipoRisorsa() + "'" +
      " AND " + CostiCommessaElemTM.LIVELLO_RISORSA + " = '" + risorsa.getLivelloRisorsa() + "'" +
      " AND " + CostiCommessaElemTM.R_RISORSA + " = '" + risorsa.getIdRisorsa() + "'";
    List costiElem = null;
    try
    {
      ConnectionManager.pushConnection(costiDescriptor);
      costiElem = CostiCommessaElem.retrieveList(where, "", false);
      if (!costiElem.isEmpty())
      {
        ((CostiCommessaElem)costiElem.get(0)).getCostiCommessaDet();
      }
      ConnectionManager.popConnection(costiDescriptor);
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace(Trace.excStream);
    }
    catch (InstantiationException ex)
    {
      ex.printStackTrace(Trace.excStream);
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace(Trace.excStream);
    }

    if (costiElem.isEmpty())
    {
      return null;
    }

    return (CostiCommessaElem)costiElem.get(0);

  }
*/
  //Fix 14964 fine

  /**
   * prepareStampaPreventivoCommessa
   */
  protected void prepareStampaPreventivoCommessa()
  {
    if (getTipologiaCosto() == TIPOLOGIA_COSTO__PREZZO_PREVENTIVATO)
    {
      iStampaPreventivoCommessaBatch.setStampaEsplCompleta(false);
    }
    else
    {
      iStampaPreventivoCommessaBatch.setStampaEsplCompleta(true);
    }
    iStampaPreventivoCommessaBatch.setArticoliRisorse(StampaPreventivoCommessa.ENTRAMBI);
    iStampaPreventivoCommessaBatch.setSoloCommesseProv(false);
    iStampaPreventivoCommessaBatch.setTipoCosto(getTipologiaCosto());
    iStampaPreventivoCommessaBatch.getCondizioneFiltri().iWhereString = "(" + CostiCommessaElemTM.ID_COMMESSA + " = '" + getCommessa().getIdCommessa() + "')";
  }

  /**
   * eseguiStampaPreventivoCommessa
   */
  protected void eseguiStampaPreventivoCommessa()
  {
    try
    {
      iPreventivoAvailableRep = createStampaPreventivoReport();
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    iStampaPreventivoCommessaBatch.iAvailableRep = iPreventivoAvailableRep;
    iStampaPreventivoCommessaBatch.iBatchJobId = getBatchJob().getBatchJobId(); ;
    iStampaPreventivoCommessaBatch.iReportNr = iPreventivoAvailableRep.getReportNr();
    iStampaPreventivoCommessaBatch.caricaPreventivoCommessaTestata();
    try
    {
      ConnectionManager.commit();
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }

  }

  /**
   * createStampaPreventivoReport
   * @throws Exception
   * @return AvailableReport
   */
  protected AvailableReport createStampaPreventivoReport() throws Exception
  {
    String where = ReportModelTM.GROUP + " = '" + iAvailableReport.getReportModel().getGroup() + "'" +
      " AND " + ReportModelTM.REPORT_MODEL_ID + " != '" + iAvailableReport.getReportModel().getReportModelId() + "'" +
      " AND " + ReportModelTM.ENTITY_ID + " = '" + iAvailableReport.getReportModel().getEntityId() + "'";

    AvailableReport preventivoAvailableRep = null;

    Vector reportModels = ReportModel.retrieveList(where, ReportModelTM.SEQUENCE, false);
    if (reportModels.size() >= 1)
    {
      preventivoAvailableRep = createNewReport(((ReportModel)reportModels.get(0)).getReportModelId());
      preventivoAvailableRep.setWhereCondition(printToolInterface.generateDefaultWhereCondition(preventivoAvailableRep, ReportPreventivoCommessaTestataTM.TABLE_NAME));
      preventivoAvailableRep.save();
      ConnectionManager.commit();
    }
    return preventivoAvailableRep;
  }

  /**
   * writeErrors
   * @param rc int
   */
  protected void writeErrors(int rc)
  {
    if (rc == ErrorCodes.OPTIMISTIC_LOCK_FAILED)
    {
      output.println(new ErrorMessage("BAS0000035").getLongText());
    }
    else if (rc == ErrorCodes.DUPLICATED_ROW)
    {
      output.println(new ErrorMessage("BAS0000034").getLongText());
    }
    else if (rc == ErrorCodes.CONSTRAINT_VIOLATION)
    {
      output.println(new ErrorMessage("BAS0000033").getLongText());
    }
    else if (rc == ErrorCodes.LOCKED_ROW || rc == ErrorCodes.OBJ_TIMEOUT)
    {
      output.println(new ErrorMessage("BAS0000019").getLongText());
    }
    else if (rc == ErrorCodes.NO_ROWS_FOUND)
    {
      output.println(new ErrorMessage("BAS0000045").getLongText());
    }
    else if (rc == ErrorCodes.BLOB_TOO_BIG)
    {
      output.println(new ErrorMessage("BAS0000084").getLongText());
    }
    else
    {
      output.println(new ErrorMessage("BAS0000036", String.valueOf(rc)).getLongText());
    }
  }

  /**
   * trasferCostiCommessa
   * @throws SQLException
   * @return int
   */
  public int trasferCostiCommessa() throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    ModproEsplosione modproEsplosione = esplosoModello();
    assegnaProgStoricizzazione();

    int rc = creaCostoCmm(getCommessa(),
                          getCommessa().getArticolo().getIdArticolo(),
                          getCommessa().getIdVersione(),
                          getCommessa().getIdConfigurazione(),
                          getCommessa().getIdStabilimento());

    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }

    result += rc;

    rc = trasferCostiArticoliRisorse(modproEsplosione);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;
    //Fix 04171 Begin
    rc = updateAmbienteCosti();
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;
    //Fix 04171 End
    return result;
  }

  /**
   * esplosoModello
   * @throws SQLException
   * @return ModproEsplosione
   */
  protected ModproEsplosione esplosoModello() throws SQLException
  {
    ModproEsplosione modproEsplosione = (ModproEsplosione)Factory.createObject(ModproEsplosione.class);
    PersDatiTecnici currPersDatiTec = PersDatiTecnici.getCurrentPersDatiTecnici();

    modproEsplosione.setIdStabilimento(getCommessa().getIdStabilimento());
    modproEsplosione.setIdArticolo(getCommessa().getIdArticolo());
    modproEsplosione.setIdConfigurazione(getCommessa().getIdConfigurazione());
    modproEsplosione.setIdCommessa(getCommessa().getIdCommessa());
    modproEsplosione.setTipoCosto(getAmbienteCosti().getTipoCosto());
    modproEsplosione.setTipoEsplosione(ModproEsplosione.PRODUZIONE);
    setDominio(modproEsplosione);
    modproEsplosione.setLivelloMassimo(Integer.MAX_VALUE);
    modproEsplosione.setQuantita(getCommessa().getQtaUmPrm());
    modproEsplosione.setData(getAmbienteCosti().getDataRiferimento());
    modproEsplosione.setGesConfigTmp(ModproEsplosione.CREATE_MEM_MA_NON_UTILIZ);
    modproEsplosione.setApplicaEfficienzaRsr(currPersDatiTec.isGesEfficienzaCosti());
    modproEsplosione.setApplicaFattoreScarto(currPersDatiTec.isGesFattoreScartoCosti());
    modproEsplosione.setEsplodiComponentiStd(false); //Fix 9598
    modproEsplosione.setRilascioOrdineEsecutivo(false);

    //Fix 27472 inizio
    try
    {
       modproEsplosione.run();
    }
    catch(ThipException ex)
    {
       if(ex.getErrorMessage() != null && ex.getErrorMessage().getId().equals("THIP110038"))
          iModelloMancante = true;
       
       throw ex;
    }
    //modproEsplosione.run();
    //Fix 27472 fine
    //Fix 04171 Begin
    // if(modproEsplosione.getModello().getDominio() == ModelloProduttivo.GENERICO)
    // throw new ThipException(new ErrorMessage("THIP110038", new String[]{getCommessa().getIdArticolo()}));
    //Fix 04171 End
    return modproEsplosione;
  }

  /**
   * getCommessa
   * @return Commessa
   */
  protected Commessa getCommessa()
  {
    if (getAmbienteCosti() != null)
    {
      return getAmbienteCosti().getCommessa();
    }
    else if(getPreventivoCommessa() != null){ //Fix 34943 inizio
    	return getPreventivoCommessa().getCommessa();
    } //Fix 34943 fine
    return null;
  }

  /**
   * setDominio
   * @param modproEsplosione ModproEsplosione
   */
  protected void setDominio(ModproEsplosione modproEsplosione)
  {
    if (getTipologiaCosto() == TIPOLOGIA_COSTO__COSTO_PREVISTO)
    {
      modproEsplosione.setDominio(ModelloProduttivo.COSTO);
    }
    else
    {
      modproEsplosione.setDominio(ModelloProduttivo.PREVENTIVAZIONE);
    }
  }

  /**
   * assegnaProgStoricizzazione
   */
  protected void assegnaProgStoricizzazione()
  {
    try
    {
      iProgressivoStorico = new Integer(Numerator.getNextInt(CostiCommessa.ID_PROGR_NUM_ID));
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
  }

  /**
   * trasferCostiArticoliRisorse
   * @param modproEsplosione ModproEsplosione
   * @throws SQLException
   * @return int
   */
  protected int trasferCostiArticoliRisorse(ModproEsplosione modproEsplosione) throws
    SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    EspNodoArticolo nodoArticolo = modproEsplosione.getNodoRadice();

    String stb = (nodoArticolo.getIdStabilimento() == null || nodoArticolo.getIdStabilimento().equals("")) ? getCommessa().getIdStabilimento() : nodoArticolo.getIdStabilimento();
    int rc = creaCostiCmm(getCommessa(),
                          getCommessa().getArticolo(),
                          getCommessa().getIdVersione(),
                          getCommessa().getConfigurazione(),
                          stb,
                          null,
                          nodoArticolo.getQuantitaCalcolata(),
                          nodoArticolo.getIdUnitaMisura(),
                          null);

    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }

    result += rc;

    rc = esplodeMateriali(nodoArticolo);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }

    result += rc;

    rc = elaborateProdotti(nodoArticolo);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }

    result += rc;

    rc = elaborateRisorse(nodoArticolo);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }

    result += rc;
    return result;
  }

  /**
   * elaborateProdotti
   * @param materiale EspNodoArticolo
   * @throws SQLException
   * @return int
   */
  protected int elaborateProdotti(EspNodoArticolo materiale) throws
    SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    Iterator iter = materiale.getNodiProdottiNonPrimari().iterator();
    while (iter.hasNext())
    {
      EspNodoProdotto prd = (EspNodoProdotto)iter.next();
      if (prd.getAttivitaProdProdotto().getTipoProdotto() == AttivitaProdProdotto.SOTTO_PRODOTTO ||
          prd.getAttivitaProdProdotto().getTipoProdotto() == AttivitaProdProdotto.SFRIDO)
      {
        int rc = elaborateProdotto(materiale, prd);
        if (rc < ErrorCodes.NO_ROWS_UPDATED)
        {
          return rc;
        }
        result += rc;
      }
    }
    return result;
  }

  /**
   * elaborateProdotto
   * @param nodoPadre EspNodoArticolo
   * @param prodotto EspNodoProdotto
   * @throws SQLException
   * @return int
   */
  protected int elaborateProdotto(EspNodoArticolo nodoPadre, EspNodoProdotto prodotto) throws SQLException
  {
    boolean gesSaldiCmm = prodotto.getAttivitaProdProdotto().getArticolo().isGesSaldiCommessa();
    boolean cmmEquals = false;
    Commessa commessa = (nodoPadre.getAttivitaProdMateriale() == null) ? getCommessa() : nodoPadre.getAttivitaProdMateriale().getCommessa();
    if (prodotto.getAttivitaProdProdotto().getIdCommessa() != null && commessa.getIdCommessa() != null)
    {
      cmmEquals = prodotto.getAttivitaProdProdotto().getIdCommessa().equals(commessa.getIdCommessa());

    }
    if ((!gesSaldiCmm) || (gesSaldiCmm && cmmEquals))
    {

      CostiCommessaElem costoCmmElm = getCostoCmmElem(commessa.getIdCommessa(),
        prodotto.getAttivitaProdProdotto().getArticolo(),
        prodotto.getAttivitaProdProdotto().getIdVersione(),
        prodotto.getAttivitaProdProdotto().getIdConfigurazione(),
        //getCommessa().getIdStabilimento()
        nodoPadre.getIdStabilimento());

      if (costoCmmElm != null)
      {
        return aggiornaCostoElemECostiDet(commessa,
                                          prodotto.getAttivitaProdProdotto().
                                          getArticolo(),
                                          prodotto.getAttivitaProdProdotto().
                                          getIdVersione(),
                                          prodotto.getAttivitaProdProdotto().
                                          getConfigurazione(),
                                          //getCommessa().getIdStabilimento()
                                          nodoPadre.getIdStabilimento(),
                                          prodotto.getQuantitaCalcolata(),
                                          costoCmmElm);
      }
      else
      {
        String idOperazione = null;
        if (prodotto.getAttivitaProdProdotto() != null && prodotto.getAttivitaProdProdotto().getAttivita() != null)
        {
          idOperazione = prodotto.getAttivitaProdProdotto().getAttivita().getIdOperazione();

        }
        String stb = (nodoPadre.getIdStabilimento() == null || nodoPadre.getIdStabilimento().equals("")) ? getCommessa().getIdStabilimento() : nodoPadre.getIdStabilimento();

        return creaCostiCmm(commessa,
                            prodotto.getAttivitaProdProdotto().getArticolo(),
                            prodotto.getAttivitaProdProdotto().getIdVersione(),
                            prodotto.getAttivitaProdProdotto().
                            getConfigurazione(),
                            stb,
                            prodotto.getIdAttivita(),
                            prodotto.getQuantitaCalcolata(),
                            prodotto.getIdUnitaMisura(),
                            idOperazione);
      }
    }
    return ErrorCodes.NO_ROWS_UPDATED;
  }

  /**
   * elaborateRisorse
   * @param nodoArticolo EspNodoArticolo
   * @throws SQLException
   * @return int
   */
  protected int elaborateRisorse(EspNodoArticolo nodoArticolo) throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    Iterator iter = nodoArticolo.getNodiRisorse().iterator();
    while (iter.hasNext())
    {
      EspNodoRisorsa risorsa = (EspNodoRisorsa)iter.next();
      int rc = elaborateRisorsa(nodoArticolo, risorsa);
      if (rc < ErrorCodes.NO_ROWS_UPDATED)
      {
        return rc;
      }
      result += rc;
    }
    return result;
  }

  /**
   * elaborateRisorsa
   * @param nodoArticolo EspNodoArticolo
   * @param risorsa EspNodoRisorsa
   * @throws SQLException
   * @return int
   */
  protected int elaborateRisorsa(EspNodoArticolo nodoArticolo, EspNodoRisorsa risorsa) throws SQLException
  {
    CostiCommessaElem costoCmmElm = getCostoCmmElem(nodoArticolo,
      risorsa.getAttivitaProdRisorsa().getTipoRisorsa(),
      risorsa.getAttivitaProdRisorsa().getLivelloRisorsa(),
      risorsa.getAttivitaProdRisorsa().getRisorsa().getIdRisorsa(),
      risorsa.getNodoPadre().getAttivitaProduttiva().getIdOperazione(), //Fix 9966
      risorsa.getNodoPadre().getAttivitaProduttiva().getIdAttivita());
    BigDecimal qtaMult = null;
    if (risorsa.getAttivitaProdRisorsa().getRisorsa().getTipoRilevazione() == Risorsa.TEMPO)
    {
      qtaMult = risorsa.getTempoCalc();
    }
    //...LP
    else if (risorsa.getAttivitaProdRisorsa().getRisorsa().getTipoRilevazione() == Risorsa.QUANTITA)
    {
      qtaMult = risorsa.getNodoPadre().getQtaMagazzinoCalc();
    }
    else
    {
      //Fix 04171 Begin
      //qtaMult = risorsa.getNodoPadre().getQtaMagazzinoCalc();
      return elaborateRisorsaACosto(nodoArticolo, risorsa, costoCmmElm);
      //Fix 04171 End
    }
    if (costoCmmElm != null)
    {
      return aggiornaCostoElemECostiDet(risorsa.getAttivitaProdRisorsa().getTipoRisorsa(),
                                        risorsa.getAttivitaProdRisorsa().getLivelloRisorsa(),
                                        risorsa.getAttivitaProdRisorsa().getRisorsa().getIdRisorsa(),
                                        qtaMult,
                                        risorsa.getTempoCalc(),
                                        costoCmmElm,
                                        risorsa.getNodoPadre().getQtaMagazzinoCalc()); //...FIX 9213
    }
    else
    {
      boolean rsrPrcp = false;
      if (risorsa.getAttivitaProdRisorsa().getPolConsRisorse() == AttivitaRisorsa.PRINCIPALE)
      {
        rsrPrcp = true;
      }
      return creaCostiCmm(nodoArticolo,
                          qtaMult,
                          getCommessa().getIdUmPrmMag(),
                          risorsa.getAttivitaProdRisorsa().getAttivitaProduttiva().getCentroLavoro().getIdReparto(),
                          risorsa.getAttivitaProdRisorsa().getAttivitaProduttiva().getIdCentroLavoro(),
                          risorsa.getAttivitaProdRisorsa().getTipoRisorsa(),
                          risorsa.getAttivitaProdRisorsa().getLivelloRisorsa(),
                          risorsa.getAttivitaProdRisorsa().getRisorsa(),
                          rsrPrcp,
                          risorsa.getAttivitaProdRisorsa().getTipoRilevazione(),
                          risorsa.getTempoCalc(),
                          risorsa.getNodoPadre().getQtaMagazzinoCalc(),
                          risorsa.getNodoPadre().getAttivitaProduttiva().getIdAttivita(),
                          risorsa.getAttivitaProdRisorsa().getAttivitaProduttiva().getIdOperazione());
    }
  }

  /**
   * esplodeMateriali
   * @param nodoArticolo EspNodoArticolo
   * @throws SQLException
   * @return int
   */
  protected int esplodeMateriali(EspNodoArticolo nodoArticolo) throws
    SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    Iterator iter = nodoArticolo.getNodiMateriali().iterator();
    while (iter.hasNext())
    {
      EspNodoArticolo espNodoArt = (EspNodoArticolo)iter.next();
      int rc = esplodeMateriale(nodoArticolo, espNodoArt);
      if (rc < ErrorCodes.NO_ROWS_UPDATED)
      {
        return rc;
      }
      result += rc;
    }
    return result;
  }

  /**
   * esplodeMateriale
   * @param nodoPadre EspNodoArticolo
   * @param materiale EspNodoArticolo
   * @throws SQLException
   * @return int
   */
  protected int esplodeMateriale(EspNodoArticolo nodoPadre, EspNodoArticolo materiale) throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;

    int rc = creaOAggiornaCostiCmm(nodoPadre, materiale);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;

    rc = elaborateProdotti(materiale);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;

    rc = elaborateRisorse(materiale);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;

    return result;
  }

  /**
   * creaOAggiornaCostiCmm
   * @param nodoPadre EspNodoArticolo
   * @param materiale EspNodoArticolo
   * @throws SQLException
   * @return int
   */
  protected int creaOAggiornaCostiCmm(EspNodoArticolo nodoPadre, EspNodoArticolo materiale) throws SQLException
  {
    boolean gesSaldiCmm = materiale.getAttivitaProdMateriale().getArticolo().isGesSaldiCommessa();
    boolean cmmEquals = false;
    String idCommessa = (nodoPadre.getAttivitaProdMateriale() == null) ? getCommessa().getIdCommessa() : nodoPadre.getAttivitaProdMateriale().getIdCommessa();
    if (materiale.getAttivitaProdMateriale().getIdCommessa() != null && idCommessa != null)
    {
      cmmEquals = materiale.getAttivitaProdMateriale().getIdCommessa().equals(idCommessa);

    }
    ConnectionManager.pushConnection(costiDescriptor);
    CostiCommessa costoCmm = CostiCommessa.elementWithKey(KeyHelper.buildObjectKey(new Object[]
      {getIdAzienda(), iProgressivoStorico, materiale.getAttivitaProdMateriale().getIdCommessa()}), PersistentObject.NO_LOCK);
    ConnectionManager.popConnection(costiDescriptor);

    if (gesSaldiCmm && !cmmEquals)
    {
      int rc1 = ErrorCodes.NO_ROWS_UPDATED;
      if (costoCmm == null)
      {
        String stb = (materiale.getIdStabilimento() == null || materiale.getIdStabilimento().equals("")) ? materiale.getIdStabilimentoEsp() : materiale.getIdStabilimento();
        rc1 = creaCostoCmm(materiale.getAttivitaProdMateriale().getCommessa(),
                           materiale.getAttivitaProdMateriale().getIdArticolo(),
                           materiale.getAttivitaProdMateriale().getIdVersione(),
                           materiale.getAttivitaProdMateriale().getIdConfigurazione(),
                           stb);
        if (rc1 < ErrorCodes.NO_ROWS_UPDATED)
        {
          return rc1;
        }
      }
      String stb = (materiale.getIdStabilimento() == null || materiale.getIdStabilimento().equals("")) ? materiale.getIdStabilimentoEsp() : materiale.getIdStabilimento();
      int rc2 = creaCostiCmm(materiale.getAttivitaProdMateriale().getCommessa(),
                             materiale.getAttivitaProdMateriale().getArticolo(),
                             materiale.getAttivitaProdMateriale().getIdVersione(),
                             materiale.getAttivitaProdMateriale().getConfigurazione(),
                             stb,
                             materiale.getIdAttivita(),
                             materiale.getQuantitaCalcolata(),
                             materiale.getIdUnitaMisura(),
                             materiale.getAttivitaProdMateriale().getAttivita().getIdOperazione());
      if (rc2 < ErrorCodes.NO_ROWS_UPDATED)
      {
        return rc2;
      }

      int rc3 = esplodeMateriali(materiale);
      if (rc3 < ErrorCodes.NO_ROWS_UPDATED)
      {
        return rc3;
      }

      return rc1 + rc2 + rc3;
    }

    if ((!gesSaldiCmm) || (gesSaldiCmm && cmmEquals))
    {
      String stb = (materiale.getIdStabilimento() == null || materiale.getIdStabilimento().equals("")) ? materiale.getIdStabilimentoEsp() : materiale.getIdStabilimento();
      CostiCommessaElem costoCmmElm = getCostoCmmElem(idCommessa,
        materiale.getAttivitaProdMateriale().getArticolo(),
        materiale.getAttivitaProdMateriale().getIdVersione(),
        materiale.getAttivitaProdMateriale().getIdConfigurazione(),
        stb);

      if (costoCmmElm != null)
      {
        int rc1 = aggiornaCostoElemECostiDet(materiale.getAttivitaProdMateriale().getCommessa(),
                                             materiale.getAttivitaProdMateriale().getArticolo(),
                                             materiale.getAttivitaProdMateriale().getIdVersione(),
                                             materiale.getAttivitaProdMateriale().getConfigurazione(),
                                             stb,
                                             materiale.getQuantitaCalcolata(),
                                             costoCmmElm);
        if (rc1 < ErrorCodes.NO_ROWS_UPDATED)
        {
          return rc1;
        }
        int rc2 = ErrorCodes.NO_ROWS_UPDATED;
        if (gesSaldiCmm)
        {
          rc2 = esplodeMateriali(materiale);
          if (rc2 < ErrorCodes.NO_ROWS_UPDATED)
          {
            return rc2;
          }
        }
        return rc1 + rc2;
      }
      else
      {
        Commessa commessa = (nodoPadre.getAttivitaProdMateriale() == null) ? getCommessa() : nodoPadre.getAttivitaProdMateriale().getCommessa();

        int rc1 = creaCostiCmm(commessa,
                               materiale.getAttivitaProdMateriale().getArticolo(),
                               materiale.getAttivitaProdMateriale().getIdVersione(),
                               materiale.getAttivitaProdMateriale().getConfigurazione(),
                               stb,
                               materiale.getIdAttivita(),
                               materiale.getQuantitaCalcolata(),
                               materiale.getIdUnitaMisura(),
                               materiale.getAttivitaProdMateriale().getAttivita().getIdOperazione());
        if (rc1 < ErrorCodes.NO_ROWS_UPDATED)
        {
          return rc1;
        }
        int rc2 = ErrorCodes.NO_ROWS_UPDATED;
        if (gesSaldiCmm)
        {
          rc2 = esplodeMateriali(materiale);
          if (rc2 < ErrorCodes.NO_ROWS_UPDATED)
          {
            return rc2;
          }
        }
        return rc1 + rc2;
      }
    }
    return ErrorCodes.NO_ROWS_UPDATED;
  }

  /**
   * getCostoCmmElem
   * @param idCommessa String
   * @param articolo Articolo
   * @param idVersione Integer
   * @param idConfigurazione Integer
   * @param idStabilimento String
   * @throws SQLException
   * @return CostiCommessaElem
   */
  protected CostiCommessaElem getCostoCmmElem(String idCommessa, Articolo articolo, Integer idVersione, Integer idConfigurazione, String idStabilimento) throws SQLException
  {
    String whereCmm = (articolo.isGesSaldiCommessa()) ? idCommessa : getCommessa().getIdCommessa();
    Integer versione = getNotNullInteger(idVersione);
    String where = CostiCommessaElemTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + iProgressivoStorico.toString() +
      " AND " + CostiCommessaElemTM.ID_COMMESSA + " = '" + whereCmm + "'" +
      " AND " + CostiCommessaElemTM.R_ARTICOLO + " = '" + articolo.getIdArticolo() + "'" +
      " AND " + CostiCommessaElemTM.R_VERSIONE + " ="  +versione + " "+
      " AND " + CostiCommessaElemTM.R_CONFIGURAZIONE + ((idConfigurazione == null) ? " IS NULL " : " = " + idConfigurazione.toString()) +
      " AND " + CostiCommessaElemTM.R_STABILIMENTO + " = '" + idStabilimento + "'" +
      " AND " + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.ARTICOLO + "'";

    List costiElem = null;
    try
    {
      ConnectionManager.pushConnection(costiDescriptor);
      costiElem = CostiCommessaElem.retrieveList(where, "", false);
      ConnectionManager.popConnection(costiDescriptor);
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    catch (InstantiationException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }

    if (costiElem.isEmpty())
    {
      return null;
    }
    return (CostiCommessaElem)costiElem.get(0);
  }

  /**
   * getCostoCmmElem
   * @param nodoArticolo EspNodoArticolo
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param idAttivita String
   * @throws SQLException
   * @return CostiCommessaElem
   */
  protected CostiCommessaElem getCostoCmmElem(EspNodoArticolo nodoArticolo, char tipoRisorsa, char livelloRisorsa, String idRisorsa,
                                              String idOperazione, String idAttivita) throws SQLException
  { //Fix 9966
    String whereOperazione = (idOperazione == null) ? " IS NULL " : " = '" + idOperazione + "' "; //Fix 9966
    String whereAttivita = (idAttivita == null) ? " IS NULL " : " = '" + idAttivita + "' ";
    Integer config = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdConfigurazione() : nodoArticolo.getAttivitaProdMateriale().getIdConfigurazione();
    String where = CostiCommessaElemTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostiCommessaElemTM.ID_PROGR_STORIC + " = " + iProgressivoStorico.toString() +
      " AND " + CostiCommessaElemTM.ID_COMMESSA + " = '" + nodoArticolo.getIdCommessa() + "'" +
      " AND " + CostiCommessaElemTM.R_ARTICOLO + " = '" + ((nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdArticolo() : nodoArticolo.getAttivitaProdMateriale().getIdArticolo()) + "'" +
      " AND " + CostiCommessaElemTM.R_VERSIONE + " = " + ((nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdVersione().intValue() : nodoArticolo.getAttivitaProdMateriale().getIdVersione().intValue()) +
      " AND " + CostiCommessaElemTM.R_CONFIGURAZIONE + ((config == null) ? " IS NULL " : " = " + config.intValue()) +
      " AND " + CostiCommessaElemTM.R_STABILIMENTO + " = '" + ((nodoArticolo.getIdStabilimento() == null || nodoArticolo.getIdStabilimento().equals("")) ? getCommessa().getIdStabilimento() : nodoArticolo.getIdStabilimento()) + "'" +
      " AND " + CostiCommessaElemTM.TIPOLOGIA_ELEM + " = '" + CostiCommessaElem.RISORSA + "'" +
      " AND " + CostiCommessaElemTM.R_ATTIVITA + whereAttivita +
      " AND " + CostiCommessaElemTM.R_OPERAZIONE + whereOperazione + //Fix 9966
      " AND " + CostiCommessaElemTM.TIPO_RISORSA + " = '" + tipoRisorsa + "'" +
      " AND " + CostiCommessaElemTM.LIVELLO_RISORSA + " = '" + livelloRisorsa + "'" +
      " AND " + CostiCommessaElemTM.R_RISORSA + " = '" + idRisorsa + "'";
    List costiElem = null;
    try
    {
      ConnectionManager.pushConnection(costiDescriptor);
      costiElem = CostiCommessaElem.retrieveList(where, "", false);
      if (!costiElem.isEmpty()) //Fix 9966
      {
        ((CostiCommessaElem)costiElem.get(0)).getCostiCommessaDet(); //Fix 9966
      }
      ConnectionManager.popConnection(costiDescriptor);
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    catch (InstantiationException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }

    if (costiElem.isEmpty())
    {
      return null;
    }
    return (CostiCommessaElem)costiElem.get(0);
  }

  /**
   * aggiornaCostoElemECostiDet
   * @param commessa Commessa
   * @param articolo Articolo
   * @param versione Integer
   * @param configurazione Configurazione
   * @param stabilimento String
   * @param qta BigDecimal
   * @param costoCmmElm CostiCommessaElem
   * @throws SQLException
   * @return int
   */
  protected int aggiornaCostoElemECostiDet(Commessa commessa, Articolo articolo, Integer versione, Configurazione configurazione, String stabilimento, BigDecimal qta, CostiCommessaElem costoCmmElm) throws SQLException
  {
    Integer idConfig = (configurazione == null) ? null : configurazione.getIdConfigurazione();
    String stb = (stabilimento == null || stabilimento.equals("")) ? getCommessa().getIdStabilimento() : stabilimento;
    Costo costo = getCosto(commessa, articolo, versione, idConfig, stb);
    //...FIX LP inizio
    if (((CostoArticolo)costo).getClasseMerceologica() == null)
    {
      ClasseMerceologica clasMerc = ((CostoArticolo)costo).getArticolo().getClasseMerclg();
      ((CostoArticolo)costo).setClasseMerceologica(clasMerc);
    }
    //...FIX LP fine

    if (costo == null)
    {
      String idCommessa = (commessa == null) ? null : commessa.getIdCommessa();
      return addAnomalie(articolo.getIdArticolo(), versione, configurazione, idCommessa, NON_SIGN_CHAR, NON_SIGN_CHAR, null, costoCmmElm.getIdAttivita(), stb);
    }
    costoCmmElm.setQuantita(costoCmmElm.getQuantita().add(qta));
    if (costoCmmElm.getCostoGenerale() != null && costo.getCostoGenerale() != null)
    {
      costoCmmElm.setCostoGenerale(costoCmmElm.getCostoGenerale().add(costo.getCostoGenerale().multiply(qta)));
    }
    if (costoCmmElm.getCostoIndustriale() != null && costo.getCostoIndustriale() != null)
    {
      costoCmmElm.setCostoIndustriale(costoCmmElm.getCostoIndustriale().add(costo.getCostoIndustriale().multiply(qta)));
    }
    if (costoCmmElm.getCostoPrimo() != null && costo.getCostoPrimo() != null)
    {
      costoCmmElm.setCostoPrimo(costoCmmElm.getCostoPrimo().add(costo.getCostoPrimo().multiply(qta)));
    }
    if (costoCmmElm.getCostoRiferimento() != null && costo.getCostoRiferimento() != null)
    {
      costoCmmElm.setCostoRiferimento(costoCmmElm.getCostoRiferimento().add(costo.getCostoRiferimento().multiply(qta)));
    }
    ConnectionManager.pushConnection(costiDescriptor);
    Iterator iter = costoCmmElm.getCostiCommessaDet().iterator();
    ConnectionManager.popConnection(costiDescriptor);
    while (iter.hasNext())
    {
      CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
      aggiornaCostiCmmDet(articolo.isGesSaldiCommessa(), qta, costo, costoCmmDet);
    }
    costoCmmElm.iCalcoloImporto = false;
    cacoloCosti(costoCmmElm);
    return saveCostiObject(costoCmmElm);
  }

  /**
   * aggiornaCostoElemECostiDet
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param qtaMolt BigDecimal
   * @param oreRichieste BigDecimal
   * @param costoCmmElm CostiCommessaElem
   * @return int
   * @throws SQLException
   * @deprecated
   */
  //...FIX 9213 (Aggiunto per mantenere il metodo vecchio)
  protected int aggiornaCostoElemECostiDet(char tipoRisorsa, char livelloRisorsa, String idRisorsa, BigDecimal qtaMolt, BigDecimal oreRichieste, CostiCommessaElem costoCmmElm) throws SQLException
  {
    return aggiornaCostoElemECostiDet(tipoRisorsa, livelloRisorsa, idRisorsa, qtaMolt, oreRichieste, costoCmmElm, qtaMolt);
  }

  /**
   * aggiornaCostoElemECostiDet
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param qtaMolt BigDecimal
   * @param oreRichieste BigDecimal
   * @param costoCmmElm CostiCommessaElem
   * @param qta BigDecimal
   * @return int
   * @throws SQLException
   */
  //...FIX 9213 (Aggiunto parametro qta)
  protected int aggiornaCostoElemECostiDet(char tipoRisorsa, char livelloRisorsa, String idRisorsa, BigDecimal qtaMolt, BigDecimal oreRichieste, CostiCommessaElem costoCmmElm, BigDecimal qta) throws SQLException
  {
    Costo costo = getCosto(tipoRisorsa, livelloRisorsa, idRisorsa, getCommessa().getIdStabilimento());
    if (costo == null)
    {
      return addAnomalie(costoCmmElm.getIdArticolo(), costoCmmElm.getIdVersione(), costoCmmElm.getConfigurazione(), costoCmmElm.getIdCommessa(), tipoRisorsa, livelloRisorsa, idRisorsa, costoCmmElm.getIdAttivita(), getCommessa().getIdStabilimento());
    }

    //...FIX 9213 inizio
    //costoCmmElm.setQuantita(costoCmmElm.getQuantita().add(qtaMolt));
    costoCmmElm.setQuantita(costoCmmElm.getQuantita().add(qta));
    //...FIX 9213 fine

    costoCmmElm.setOreRichieste(costoCmmElm.getOreRichieste().add(oreRichieste)); //...FIX 8938
    if (costoCmmElm.getCostoGenerale() != null && costo.getCostoGenerale() != null)
    {
      costoCmmElm.setCostoGenerale(costoCmmElm.getCostoGenerale().add(costo.getCostoGenerale().multiply(qtaMolt)));
    }
    if (costoCmmElm.getCostoIndustriale() != null && costo.getCostoIndustriale() != null)
    {
      costoCmmElm.setCostoIndustriale(costoCmmElm.getCostoIndustriale().add(costo.getCostoIndustriale().multiply(qtaMolt)));
    }
    if (costoCmmElm.getCostoPrimo() != null && costo.getCostoPrimo() != null)
    {
      costoCmmElm.setCostoPrimo(costoCmmElm.getCostoPrimo().add(costo.getCostoPrimo().multiply(qtaMolt)));
    }
    if (costoCmmElm.getCostoRiferimento() != null && costo.getCostoRiferimento() != null)
    {
      costoCmmElm.setCostoRiferimento(costoCmmElm.getCostoRiferimento().add(costo.getCostoRiferimento().multiply(qtaMolt)));
      //...FIX 8938 inizio
    }
    ConnectionManager.pushConnection(costiDescriptor);
    Iterator iter = costoCmmElm.getCostiCommessaDet().iterator();
    ConnectionManager.popConnection(costiDescriptor);
    //...FIX 8938 fine
    while (iter.hasNext())
    {
      CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
      aggiornaCostiCmmDet(true, qtaMolt, costo, costoCmmDet);
    }
    costoCmmElm.iCalcoloImporto = false;
    cacoloCosti(costoCmmElm);
    return saveCostiObject(costoCmmElm);
  }

  /**
   * aggiornaCostiCmmDet
   * @param gesCommessa boolean
   * @param qta BigDecimal
   * @param costo Costo
   * @param costoCmmDet CostiCommessaDet
   * @throws SQLException
   */
  protected void aggiornaCostiCmmDet(boolean gesCommessa, BigDecimal qta, Costo costo, CostiCommessaDet costoCmmDet) throws SQLException
  {
    Iterator iter = costo.getDettagli().iterator();
    if (gesCommessa)
    {
      while (iter.hasNext())
      {
        DettaglioCosto detCosto = (DettaglioCosto)iter.next();
        if (detCosto.getComponenteCosto().equals(costoCmmDet.getComponenteCosto()))
        {
          updateCostoCmmDet(costoCmmDet, detCosto, qta);
          return;
        }
      }
    }
    else
    {
      valorizzaCostoDet(costo);
      while (iter.hasNext())
      {
        DettaglioCosto detCosto = (DettaglioCosto)iter.next();
        if (detCosto.getComponenteCosto().equals(costoCmmDet.getComponenteCosto())) //Fix 27458
        {
           updateCostoCmmDet(costoCmmDet, detCosto, qta);
        }
      }
    }
  }

  /**
   * updateCostoCmmDet
   * @param costoCmmDet CostiCommessaDet
   * @param detCosto DettaglioCosto
   * @param qta BigDecimal
   */
  protected void updateCostoCmmDet(CostiCommessaDet costoCmmDet, DettaglioCosto detCosto, BigDecimal qta)
  {
    if (costoCmmDet.getCostoLivello() != null && detCosto.getCostoLivello() != null)
    {
      costoCmmDet.setCostoLivello(costoCmmDet.getCostoLivello().add(detCosto.getCostoLivello().multiply(qta)));
    }
    if (costoCmmDet.getCostoLivelliInf() != null && detCosto.getCostoLivelliInf() != null)
    {
      costoCmmDet.setCostoLivelliInf(costoCmmDet.getCostoLivelliInf().add(detCosto.getCostoLivelliInf().multiply(qta)));
    }
    if (costoCmmDet.getCostoTotale() != null && detCosto.getCostoTotale() != null)
    {
      costoCmmDet.setCostoTotale(costoCmmDet.getCostoTotale().add(detCosto.getCostoTotale().multiply(qta)));
    }
  }

  /**
   * creaCostiCmm
   * @param nodoArticolo EspNodoArticolo
   * @param qtaMult BigDecimal
   * @param idUM String
   * @param idReparto String
   * @param idCentroLavoro String
   * @param tipoRisorsa char
   * @param livelliRisorsa char
   * @param risorsa Risorsa
   * @param risorsaPrp boolean
   * @param tipoLavRsr char
   * @param oreRich BigDecimal
   * @param qtaEff BigDecimal
   * @param idAttivita String
   * @param idOperazione String
   * @throws SQLException
   * @return int
   */
  protected int creaCostiCmm(EspNodoArticolo nodoArticolo, BigDecimal qtaMult, String idUM, String idReparto, String idCentroLavoro, char tipoRisorsa, char livelliRisorsa, Risorsa risorsa, boolean risorsaPrp, char tipoLavRsr, BigDecimal oreRich, BigDecimal qtaEff, String idAttivita, String idOperazione) throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    String stb = (nodoArticolo.getIdStabilimento() == null || nodoArticolo.getIdStabilimento().equals("")) ? getCommessa().getIdStabilimento() : nodoArticolo.getIdStabilimento();
    Costo costo = getCosto(tipoRisorsa, livelliRisorsa, risorsa.getIdRisorsa(), stb);
    if (costo == null)
    {
      String idArticolo = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdArticolo() : nodoArticolo.getAttivitaProdMateriale().getIdArticolo();
      Integer idVersione = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdVersione() : nodoArticolo.getAttivitaProdMateriale().getIdVersione();
      Configurazione configurazione = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getConfigurazione() : nodoArticolo.getAttivitaProdMateriale().getConfigurazione();
      String idCommessa = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdCommessa() : nodoArticolo.getAttivitaProdMateriale().getIdCommessa();
      return addAnomalie(idArticolo, idVersione, configurazione, idCommessa, tipoRisorsa, livelliRisorsa, risorsa.getIdRisorsa(), idAttivita, stb);
    }

    CostiCommessaElem costoCmmElem = creaCostoCmmElem(nodoArticolo,
      qtaMult,
      idUM,
      costo,
      idReparto,
      idCentroLavoro,
      tipoRisorsa,
      livelliRisorsa,
      risorsa.getIdRisorsa(),
      risorsaPrp,
      tipoLavRsr,
      oreRich,
      qtaEff,
      idAttivita,
      idOperazione);
    costoCmmElem.iCalcoloImporto = false;
    ConnectionManager.pushConnection(costiDescriptor);
    costoCmmElem.impostaIdRigaElem();
    ConnectionManager.popConnection(costiDescriptor);
    int rc = saveCostiObject(costoCmmElem);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;

    if (nodoArticolo.getAttivitaProdMateriale() == null)
    {
      rc = creaCostiDet(getCommessa().getArticolo(), getCommessa(), qtaMult, costo, costoCmmElem);
    }
    else
    {
      rc = creaCostiDet(nodoArticolo.getAttivitaProdMateriale().getArticolo(), nodoArticolo.getAttivitaProdMateriale().getCommessa(), qtaMult, costo, costoCmmElem);
    }
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;
    return result;
  }

  /**
   * creaCostiCmm
   * @param commessa Commessa
   * @param articolo Articolo
   * @param versione Integer
   * @param configurazione Configurazione
   * @param stabilimento String
   * @param attivita String
   * @param qtaEff BigDecimal
   * @param idUm String
   * @param idOperazione String
   * @throws SQLException
   * @return int
   */
  protected int creaCostiCmm(Commessa commessa, Articolo articolo, Integer versione, Configurazione configurazione, String stabilimento, String attivita, BigDecimal qtaEff, String idUm, String idOperazione) throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
    //String codConfig = (configurazione == null) ? null : configurazione.getIdEsternoConfig(); //Fix 9966
    String stb = (stabilimento == null || stabilimento.equals("")) ? getCommessa().getIdStabilimento() : stabilimento;
    Costo costo = getCosto(commessa, articolo, versione, config, stb);
     if (costo == null)
       return addAnomalie(articolo.getIdArticolo(), versione, configurazione, commessa.getIdCommessa(), NON_SIGN_CHAR, NON_SIGN_CHAR, null, attivita, stb);
     //...FIX LP inizio
    else if (((CostoArticolo)costo).getClasseMerceologica() == null)
    {
      ClasseMerceologica clasMerc = ((CostoArticolo)costo).getArticolo().getClasseMerclg();
      ((CostoArticolo)costo).setClasseMerceologica(clasMerc);
    }
    //...FIX LP fine

    CostiCommessaElem costoCmmElem = creaCostoCmmElem(commessa,
      articolo,
      versione,
      config,
      stb,
      attivita,
      qtaEff,
      idUm,
      costo,
      idOperazione);
    costoCmmElem.iCalcoloImporto = false;
    ConnectionManager.pushConnection(costiDescriptor);
    costoCmmElem.impostaIdRigaElem();
    ConnectionManager.popConnection(costiDescriptor);
    int rc = saveCostiObject(costoCmmElem);

    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;

    rc = creaCostiDet(articolo, commessa, qtaEff, costo, costoCmmElem);
    if (rc < ErrorCodes.NO_ROWS_UPDATED)
    {
      return rc;
    }
    result += rc;

    return result;
  }
//14964
  protected int creaCostiCmmPrev(PreventivoCommessaVoce voce,Commessa commessa, Articolo articolo, Integer versione, Configurazione configurazione,  String stabilimento, String attivita, BigDecimal qtaEff, String idUm, String idOperazione) throws SQLException
 {
   int result = ErrorCodes.NO_ROWS_UPDATED;
   Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
   String stb = (stabilimento == null || stabilimento.equals("")) ? getCommessa().getIdStabilimento() : stabilimento;
   CostiCommessaElem costoCmmElem = creaCostoCmmElemPrev(commessa,
     articolo,
     versione,
     config,
     stb,
     attivita,
     qtaEff,
     idUm,
     voce,
     idOperazione);
   costoCmmElem.iCalcoloImporto = false;
   ConnectionManager.pushConnection(costiDescriptor);
   costoCmmElem.impostaIdRigaElem();
   ConnectionManager.popConnection(costiDescriptor);
   int rc = saveCostiObject(costoCmmElem);

   if (rc < ErrorCodes.NO_ROWS_UPDATED)
   {
     return rc;
   }
   result += rc;

   rc = creaCostiDetPrev(articolo, commessa, qtaEff, voce, costoCmmElem);
   if (rc < ErrorCodes.NO_ROWS_UPDATED)
   {
     return rc;
   }
   result += rc;

   return result;
 }
 protected int creaCostiCmmPrevSenzaDet(PreventivoCommessaVoce voce,Commessa commessa, Articolo articolo, Integer versione, Configurazione configurazione,  String stabilimento, String attivita, BigDecimal qtaEff, String idUm, String idOperazione) throws SQLException
 {
   int result = ErrorCodes.NO_ROWS_UPDATED;
   Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
   String stb = (stabilimento == null || stabilimento.equals("")) ? getCommessa().getIdStabilimento() : stabilimento;
   CostiCommessaElem costoCmmElem = creaCostoCmmElemPrev(commessa,
     articolo,
     versione,
     config,
     stb,
     attivita,
     qtaEff,
     idUm,
     voce,
     idOperazione);
   costoCmmElem.iCalcoloImporto = false;
   ConnectionManager.pushConnection(costiDescriptor);
   costoCmmElem.impostaIdRigaElem();
   ConnectionManager.popConnection(costiDescriptor);
   int rc = saveCostiObject(costoCmmElem);
   if (rc < ErrorCodes.NO_ROWS_UPDATED)
   {
     return rc;
   }
   result += rc;

  /* rc = creaCostiDetPrev(articolo, commessa, qtaEff, voce, costoCmmElem);
   if (rc < ErrorCodes.NO_ROWS_UPDATED)
   {
     return rc;
   }
   result += rc;*/

   return result;
 }

  /**
   * getCosto
   * @param commessa Commessa
   * @param articolo Articolo
   * @param versione Integer
   * @param configurazione Integer
   * @param stabilimento String
   * @throws SQLException
   * @return Costo
   */
  protected Costo getCosto(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento) throws SQLException
  {
    String whereCommessa = " IS NULL ";
    if (articolo.isGesSaldiCommessa())
    {
      whereCommessa = " = '" + commessa.getIdCommessa() + "' ";

    }
    String whereConfig = (configurazione == null) ? " IS NULL " : " = " + configurazione.intValue();
    String where = CostoArticoloTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostoArticoloTM.ID_AMBIENTE + " = '" + getIdAmbienteCosti() + "'" +
      " AND " + CostoArticoloTM.R_STABILIMENTO + " = '" + stabilimento + "'" +
      " AND " + CostoArticoloTM.TIPOLOGIA + " = '" + Costo.ARTICOLO + "'" +
      " AND " + CostoArticoloTM.R_ARTICOLO + " = '" + articolo.getIdArticolo() + "'" +
      " AND " + CostoArticoloTM.R_VERSIONE + " = " + versione +
      " AND " + CostoArticoloTM.R_CONFIGURAZIONE + whereConfig +
      " AND " + CostoArticoloTM.R_COMMESSA + whereCommessa;
    List costi = null;
    try
    {
      ConnectionManager.pushConnection(costiDescriptor);
      costi = CostoArticolo.retrieveList(where, "", false);
      ConnectionManager.popConnection(costiDescriptor);
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    catch (InstantiationException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }

    if (costi.isEmpty())
    {
      return null;
    }
    return (CostoArticolo)costi.get(0);
  }

  /**
   * getCosto
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param idStabilimento String
   * @throws SQLException
   * @return Costo
   */
  protected Costo getCosto(char tipoRisorsa, char livelloRisorsa, String idRisorsa, String idStabilimento) throws SQLException
  {
    String where = CostoRisorsaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
      " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + getIdAmbienteCosti() + "'" +
      " AND " + CostoRisorsaTM.R_STABILIMENTO + " = '" + idStabilimento + "'" +
      " AND " + CostoRisorsaTM.TIPOLOGIA + " = '" + Costo.RISORSA + "'" +
      " AND " + CostoRisorsaTM.R_TIPO_RISORSA + " = '" + tipoRisorsa + "'" +
      " AND " + CostoRisorsaTM.R_LIVELLO_RISORSA + " = '" + livelloRisorsa + "'" +
      " AND " + CostoRisorsaTM.R_RISORSA + " = '" + idRisorsa + "'";
    List costi = null;
    try
    {
      ConnectionManager.pushConnection(costiDescriptor);
      costi = CostoRisorsa.retrieveList(where, "", false);
      ConnectionManager.popConnection(costiDescriptor);
    }
    catch (ClassNotFoundException ex)
    {
      ex.printStackTrace(Trace.excStream);
    }
    catch (InstantiationException ex)
    {
      ex.printStackTrace(Trace.excStream);
    }
    catch (IllegalAccessException ex)
    {
      ex.printStackTrace(Trace.excStream);
    }

    if (costi.isEmpty())
    {
      return null;
    }
    return (CostoRisorsa)costi.get(0);
  }

  /**
   * creaCostoCmmElem
   * @param commessa Commessa
   * @param articolo Articolo
   * @param versione Integer
   * @param configurazione Integer
   * @param stabilimento String
   * @param attivita String
   * @param qtaEff BigDecimal
   * @param idUm String
   * @param costo Costo
   * @param idOperazione String
   * @throws SQLException
   * @return CostiCommessaElem
   */
  protected CostiCommessaElem creaCostoCmmElem(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento, String attivita, BigDecimal qtaEff, String idUm, Costo costo, String idOperazione) throws SQLException
  {
    return creaCostoCmmElem(commessa,
                            articolo,
                            versione,
                            configurazione,
                            stabilimento,
                            attivita,
                            qtaEff,
                            idUm,
                            costo,
                            null,
                            null,
                            NON_SIGN_CHAR,
                            NON_SIGN_CHAR,
                            null,
                            false,
                            NON_SIGN_CHAR,
                            null,
                            qtaEff,
                            idOperazione);
  }
  //14964
  protected CostiCommessaElem creaCostoCmmElemPrev(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento,  String attivita, BigDecimal qtaEff, String idUm, PreventivoCommessaVoce voce, String idOperazione) throws SQLException
  {
    return creaCostoCmmElemPrev(commessa,
                            articolo,
                            versione,
                            configurazione,
                            stabilimento,
                            attivita,
                            qtaEff,
                            idUm,
                            voce,
                            null,
                            null,
                            NON_SIGN_CHAR,
                            NON_SIGN_CHAR,
                            null,
                            false,
                            NON_SIGN_CHAR,
                            null,
                            qtaEff,
                            idOperazione);
  }

  protected CostiCommessaElem creaCostoCmmElemPrev(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento, String attivita, BigDecimal qtaEff, String idUm,PreventivoCommessaRiga riga,String idOperazione) throws SQLException
   {
     return creaCostoCmmElemPrev(commessa,
                             articolo,
                             versione,
                             configurazione,
                             stabilimento,
                             attivita,
                             qtaEff,
                             idUm,
                             riga,
                             null,
                             null,
                             NON_SIGN_CHAR,
                             NON_SIGN_CHAR,
                             null,
                             false,
                             NON_SIGN_CHAR,
                             null,
                             qtaEff,
                             idOperazione);
   }



  /**
   * creaCostoCmmElem
   * @param nodoArticolo EspNodoArticolo
   * @param qtaMult BigDecimal
   * @param idUM String
   * @param costo Costo
   * @param idReparto String
   * @param idCentroLavoro String
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param risorsaPrp boolean
   * @param tipoLavRsr char
   * @param oreRich BigDecimal
   * @param qtaEff BigDecimal
   * @param idAttivita String
   * @param idOperazione String
   * @throws SQLException
   * @return CostiCommessaElem
   */
  protected CostiCommessaElem creaCostoCmmElem(EspNodoArticolo nodoArticolo, BigDecimal qtaMult, String idUM, Costo costo, String idReparto, String idCentroLavoro, char tipoRisorsa, char livelloRisorsa, String idRisorsa, boolean risorsaPrp, char tipoLavRsr, BigDecimal oreRich, BigDecimal qtaEff, String idAttivita, String idOperazione) throws SQLException
  {
    Articolo articolo = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getArticolo() : nodoArticolo.getAttivitaProdMateriale().getArticolo();
    Integer idVersione = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdVersione() : nodoArticolo.getAttivitaProdMateriale().getIdVersione();
    Integer idConfigurazione = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdConfigurazione() : nodoArticolo.getAttivitaProdMateriale().getIdConfigurazione();
    Commessa commessa = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa() : nodoArticolo.getAttivitaProdMateriale().getCommessa();
    String stb = (nodoArticolo.getIdStabilimento() == null || nodoArticolo.getIdStabilimento().equals("")) ? getCommessa().getIdStabilimento() : nodoArticolo.getIdStabilimento();
    return creaCostoCmmElem(commessa,
                            articolo,
                            idVersione,
                            idConfigurazione,
                            stb,
                            idAttivita,
                            qtaMult,
                            idUM,
                            costo,
                            idReparto,
                            idCentroLavoro,
                            tipoRisorsa,
                            livelloRisorsa,
                            idRisorsa,
                            risorsaPrp,
                            tipoLavRsr,
                            oreRich,
                            qtaEff,
                            idOperazione);
  }

  /**
   * creaCostoCmmElem
   * @param commessa Commessa
   * @param articolo Articolo
   * @param versione Integer
   * @param configurazione Integer
   * @param stabilimento String
   * @param attivita String
   * @param qtaMult BigDecimal
   * @param idUm String
   * @param costo Costo
   * @param idReparto String
   * @param idCentroLavoro String
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param risorsaPrp boolean
   * @param tipoLavRsr char
   * @param oreRich BigDecimal
   * @param qtaEff BigDecimal
   * @param idOperazione String
   * @throws SQLException
   * @return CostiCommessaElem
   */
  protected CostiCommessaElem creaCostoCmmElem(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento, String attivita, BigDecimal qtaMult, String idUm, Costo costo, String idReparto, String idCentroLavoro, char tipoRisorsa, char livelloRisorsa, String idRisorsa, boolean risorsaPrp, char tipoLavRsr, BigDecimal oreRich, BigDecimal qtaEff, String idOperazione) throws SQLException
  {
    CostiCommessaElem costoCmmElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
    costoCmmElem.setIdAzienda(getIdAzienda());
    costoCmmElem.setIdProgrStoric(iProgressivoStorico);
    costoCmmElem.setIdCommessa(commessa.getIdCommessa());
    costoCmmElem.setTipologiaElem(costo.getTipologia());
    costoCmmElem.setIdArticolo(articolo.getIdArticolo());
    costoCmmElem.setIdVersione(versione);
    costoCmmElem.setIdConfigurazione(configurazione);
    costoCmmElem.setIdStabilimento(stabilimento);
    costoCmmElem.setIdAttivita(attivita);
    costoCmmElem.setIdOperazione(idOperazione);
    costoCmmElem.setQuantita(qtaEff);
    costoCmmElem.setIdUmPrmMag(idUm);

    if (costo instanceof CostoArticolo)
    {
      costoCmmElem.setCostoElementare(((CostoArticolo)costo).getCostoElementare());
      costoCmmElem.setTipoParte(((CostoArticolo)costo).getTipoParte());
      costoCmmElem.setIdPianificatore(((CostoArticolo)costo).getIdPianificatore());
      costoCmmElem.setIdGruppoProdotto(((CostoArticolo)costo).getIdGruppoProdotto());
      costoCmmElem.setIdClasseMerceologica(((CostoArticolo)costo).getIdClasseMerceologica());
      costoCmmElem.setIdClasseMateriale(((CostoArticolo)costo).getIdClasseMateriale());
      costoCmmElem.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      costoCmmElem.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      costoCmmElem.setTipoRilevazioneRsr(Risorsa.TEMPO);
    }
    else if (costo instanceof CostoRisorsa)
    {
      costoCmmElem.setTipoParte(articolo.getTipoParte());
      costoCmmElem.setIdReparto(idReparto);
      costoCmmElem.setIdCentroLavoro(idCentroLavoro);
      costoCmmElem.setTipoRisorsa(tipoRisorsa);
      costoCmmElem.setLivelloRisorsa(livelloRisorsa);
      costoCmmElem.setIdRisorsa(idRisorsa);
      costoCmmElem.setRisorsaPrincipale(risorsaPrp);
      costoCmmElem.setTipoRilevazioneRsr(tipoLavRsr);
      costoCmmElem.setOreRichieste(oreRich);
      //Fix 04567 Begin
      costoCmmElem.setCostoElementare(true);
      //Fix 04567 End
    }
    if (costo.getCostoGenerale() != null)
    {
      costoCmmElem.setCostoGenerale(costo.getCostoGenerale().multiply(qtaMult));
    }
    if (costo.getCostoIndustriale() != null)
    {
      costoCmmElem.setCostoIndustriale(costo.getCostoIndustriale().multiply(qtaMult));
    }
    if (costo.getCostoPrimo() != null)
    {
      costoCmmElem.setCostoPrimo(costo.getCostoPrimo().multiply(qtaMult));
    }
    if (costo.getCostoRiferimento() != null)
    {
      costoCmmElem.setCostoRiferimento(costo.getCostoRiferimento().multiply(qtaMult));
    }
    return costoCmmElem;
  }

//14964
  protected CostiCommessaElem creaCostoCmmElemPrev(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento,
    String attivita, BigDecimal qtaMult, String idUm, PreventivoCommessaVoce voce, String idReparto, String idCentroLavoro, char tipoRisorsa, char livelloRisorsa, String idRisorsa, boolean risorsaPrp, char tipoLavRsr, BigDecimal oreRich, BigDecimal qtaEff, String idOperazione) throws SQLException
  {
    CostiCommessaElem costoCmmElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
    costoCmmElem.setIdAzienda(getIdAzienda());
    costoCmmElem.setIdProgrStoric(iProgressivoStorico);
    costoCmmElem.setIdCommessa(commessa.getIdCommessa());
    costoCmmElem.setIdVersione(versione);
    if(configurazione !=null && configurazione.equals(new Integer("0")))
      costoCmmElem.setIdConfigurazione(null);
    else
     costoCmmElem.setIdConfigurazione(configurazione);
    costoCmmElem.setIdStabilimento(stabilimento);
    costoCmmElem.setIdAttivita(attivita);
    costoCmmElem.setIdOperazione(idOperazione);
    costoCmmElem.setQuantita(qtaEff);
    costoCmmElem.setIdUmPrmMag(idUm);

    if (voce.getTipoRigav() ==  PreventivoCommessaVoce.TP_RIG_VOCE || voce.getTipoRigav() ==  PreventivoCommessaVoce.TP_RIG_ARTICOLO)
    {
      costoCmmElem.setTipologiaElem(Costo.ARTICOLO);
      costoCmmElem.setIdArticolo(articolo.getIdArticolo());
      costoCmmElem.setCostoElementare(false);
      costoCmmElem.setTipoParte(articolo.getTipoParte());
      costoCmmElem.setIdPianificatore(articolo.getIdPianificatore());
      costoCmmElem.setIdGruppoProdotto(articolo.getIdGruppoProdotto());
      costoCmmElem.setIdClasseMerceologica(articolo.getIdClasseMerclg());
      costoCmmElem.setIdClasseMateriale(articolo.getIdClasseMateriale());
      costoCmmElem.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
      costoCmmElem.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
      costoCmmElem.setTipoRilevazioneRsr(Risorsa.TEMPO);
    }
    else if (voce.getTipoRigav() ==  PreventivoCommessaVoce.TP_RIG_RISORSA )
    {
      costoCmmElem.setTipologiaElem(Costo.RISORSA);
      costoCmmElem.setIdArticolo(voce.getRisorsa().getIdArticoloServizio());
      if (voce.getRisorsa().getArticoloServizio() != null)
        costoCmmElem.setTipoParte(voce.getRisorsa().getArticoloServizio().getTipoParte());
      else
        costoCmmElem.setTipoParte(Articolo.NON_SIGNIFICATIVO);
      costoCmmElem.setIdReparto(idReparto);
      costoCmmElem.setIdCentroLavoro(idCentroLavoro);
      costoCmmElem.setTipoRisorsa(tipoRisorsa);
      costoCmmElem.setLivelloRisorsa(livelloRisorsa);
      costoCmmElem.setIdRisorsa(idRisorsa);
      costoCmmElem.setRisorsaPrincipale(risorsaPrp);
      costoCmmElem.setTipoRilevazioneRsr(tipoLavRsr);
      costoCmmElem.setOreRichieste(oreRich);
      costoCmmElem.setCostoElementare(true);
    }

    //Fix 22273 Inizio
    if(isValutaPrevAziendale()){
      costoCmmElem.setCostoGenerale(voce.getCosTotale().multiply(qtaMult));
      costoCmmElem.setCostoIndustriale(voce.getCosTotale().multiply(qtaMult));
      costoCmmElem.setCostoPrimo(voce.getCosTotale().multiply(qtaMult));
      costoCmmElem.setCostoRiferimento(voce.getCostoRifer().multiply(qtaMult));
    }
    else{
      costoCmmElem.setCostoGenerale(voce.getCosTotaleAZ().multiply(qtaMult));
      costoCmmElem.setCostoIndustriale(voce.getCosTotaleAZ().multiply(qtaMult));
      costoCmmElem.setCostoPrimo(voce.getCosTotaleAZ().multiply(qtaMult));
      costoCmmElem.setCostoRiferimento(voce.getCosUnitarioAZ().multiply(qtaMult));
    }
    //Fix 22237 Fine
    return costoCmmElem;
  }

  protected CostiCommessaElem creaCostoCmmElemPrev(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento,
    String attivita, BigDecimal qtaMult, String idUm, PreventivoCommessaRiga riga,String idReparto, String idCentroLavoro, char tipoRisorsa, char livelloRisorsa, String idRisorsa, boolean risorsaPrp, char tipoLavRsr, BigDecimal oreRich, BigDecimal qtaEff, String idOperazione) throws SQLException
 {
   CostiCommessaElem costoCmmElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
   costoCmmElem.setIdAzienda(getIdAzienda());
   costoCmmElem.setIdProgrStoric(iProgressivoStorico);
   costoCmmElem.setIdCommessa(commessa.getIdCommessa());
   costoCmmElem.setTipologiaElem(Costo.ARTICOLO);
   costoCmmElem.setIdArticolo(articolo.getIdArticolo());
   costoCmmElem.setIdVersione(versione);
   if(configurazione != null && configurazione.equals(new Integer("0")))
     costoCmmElem.setIdConfigurazione(null);
    else
      costoCmmElem.setIdConfigurazione(configurazione);
   costoCmmElem.setIdStabilimento(stabilimento);
   costoCmmElem.setIdAttivita(attivita);
   costoCmmElem.setIdOperazione(idOperazione);
   costoCmmElem.setQuantita(qtaEff);
   costoCmmElem.setIdUmPrmMag(idUm);
   costoCmmElem.setCostoElementare(true);
   costoCmmElem.setTipoParte(articolo.getTipoParte());
   costoCmmElem.setIdPianificatore(articolo.getIdPianificatore());
   costoCmmElem.setIdGruppoProdotto(articolo.getIdGruppoProdotto());
   costoCmmElem.setIdClasseMerceologica(articolo.getIdClasseMerclg());
   costoCmmElem.setIdClasseMateriale(articolo.getIdClasseMateriale());
   costoCmmElem.setTipoRisorsa(Risorsa.NON_SIGNIFICATIVO);
   costoCmmElem.setLivelloRisorsa(Risorsa.NON_SIGNIFICATIVO);
   costoCmmElem.setTipoRilevazioneRsr(Risorsa.TEMPO);
   //Fix 22273 Inizio
    String idValutaPreventivo = riga.getTestata().getIdValuta();
    if(isValutaPrevAziendale()){
      costoCmmElem.setCostoGenerale(riga.getCosTotale());
      costoCmmElem.setCostoIndustriale(riga.getCosLivello());
      costoCmmElem.setCostoPrimo(riga.getCosLivelloInf());
      costoCmmElem.setCostoRiferimento(riga.getCosTotale());
    }
   else{
     costoCmmElem.setCostoGenerale(convertiInValutaAziendale(riga.getCosTotPrevCom(), idValutaPreventivo, riga.getTestata().getDataPrevc()));
     costoCmmElem.setCostoIndustriale(convertiInValutaAziendale(riga.getCosLivello(), idValutaPreventivo, riga.getTestata().getDataPrevc()));
     costoCmmElem.setCostoPrimo(convertiInValutaAziendale(riga.getCosLivelloInf(), idValutaPreventivo, riga.getTestata().getDataPrevc()));
     costoCmmElem.setCostoRiferimento(convertiInValutaAziendale(riga.getCosTotale(), idValutaPreventivo, riga.getTestata().getDataPrevc()));
    }
    //Fix 22237 Fine
   return costoCmmElem;
 }

  /**
   * addAnomalie
   * @param idArticolo String
   * @param versione Integer
   * @param configurazione Configurazione
   * @param idCommessa String
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param idAttivita String
   * @param idStabilimento String
   * @throws SQLException
   * @return int
   */
  protected int addAnomalie(String idArticolo, Integer versione, Configurazione configurazione, String idCommessa, char tipoRisorsa, char livelloRisorsa, String idRisorsa, String idAttivita, String idStabilimento) throws SQLException
  {
    iEsisteAnomalie = true;
    Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
    String codConfig = (configurazione == null) ? null : configurazione.getIdEsternoConfig();
    ReportAnomalieTrasfCosti reportAnm = (ReportAnomalieTrasfCosti)Factory.createObject(ReportAnomalieTrasfCosti.class);
    reportAnm.setBatchJobId(getBatchJob().getBatchJobId());
    reportAnm.setReportNr(iAvailableReport.getReportNr());
    reportAnm.setIdProgrStoric(iProgressivoStorico);
    reportAnm.setIdAzienda(getIdAzienda());
    reportAnm.setIdAmbiente(getIdAmbienteCosti());
    reportAnm.setIdStabilimento(idStabilimento /*getCommessa().getIdStabilimento()*/);
    reportAnm.setIdArticolo(idArticolo);
    reportAnm.setIdVersione(versione);
    reportAnm.setIdConfigurazione(config);
    reportAnm.setCodConfig(codConfig);
    reportAnm.setIdCommessa(idCommessa);
    reportAnm.setTipoRisorsa(tipoRisorsa);
    reportAnm.setLivelloRisorsa(livelloRisorsa);
    reportAnm.setIdRisorsa(idRisorsa);
    UserErrorMessage err = new UserErrorMessage("THIP20T083", new String[]
                                             {KeyHelper.formatKeyString(getAmbienteCostiKey()), getFormatedDate(getAmbienteCosti().getDataRiferimento())});
    reportAnm.setMessaggioErrore(err.getLongText());
    reportAnm.setSeverita(err.getSeverity());
    reportAnm.setIdArticoloPrd(getCommessa().getIdArticolo());
    reportAnm.setIdVersionePrd(getCommessa().getIdVersione());
    reportAnm.setIdConfigurazionePrd(getCommessa().getIdConfigurazione());
    reportAnm.setIdCommessaPrd(getCommessa().getIdCommessa());
    reportAnm.setIdAttivita(idAttivita);
    return saveReportObject(reportAnm);
  }

  /**
   * creaCostoCmm
   * @param commessa Commessa
   * @param idArticolo String
   * @param idVersione Integer
   * @param idconfigurazione Integer
   * @param idStabilimento String
   * @throws SQLException
   * @return int
   */
  protected int creaCostoCmm(Commessa commessa, String idArticolo, Integer idVersione, Integer idconfigurazione, String idStabilimento) throws SQLException
  {
    CostiCommessa costoCmm = (CostiCommessa)Factory.createObject(CostiCommessa.class);
    costoCmm.setImpostaInformazioniComm(false);
    costoCmm.setIdAzienda(getIdAzienda());
    costoCmm.setIdProgrStoric(iProgressivoStorico);
    costoCmm.setIdCommessa(commessa.getIdCommessa());
    costoCmm.setTipologiaCostoCmm(getTipologiaCosto());
    if (getTipoElaborazione() == TIPO_ELAB_DA_AMB_COS) //Fix 14964
    {
      costoCmm.setDataRiferimento(getAmbienteCosti().getDataRiferimento());
    }
    else //Fix 14964
    {
   //   if(getPreventivoCommessa().getAmbienteCosti()!=null)//15848
       costoCmm.setDataRiferimento(getPreventivoCommessa().getDataRiferimento()); //Fix 14964
    }
    costoCmm.setIdArticolo(idArticolo);
    costoCmm.setIdVersione(idVersione);
    costoCmm.setIdConfigurazione(idconfigurazione);
    costoCmm.setIdStabilimento(idStabilimento);
    //34585 inizio
    if(isTrasferimentoPerBudget())
    	costoCmm.setUfficiale(false);
    else 
    //34585 fine
    	costoCmm.setUfficiale(isCalcoloUfficiale());
    ConnectionManager.pushConnection(costiDescriptor);
    if(!isTrasferimentoPerBudget())//34585
    	costoCmm.impostaUfficiale(); //manage ufficiale

    ConnectionManager.popConnection(costiDescriptor);
    costoCmm.setIdCommessaApp(commessa.getIdCommessaAppartenenza());
    costoCmm.setIdCommessaPrm(commessa.getIdCommessaPrincipale());
    costoCmm.setLivelloCommessa(commessa.getLivelloCommessa());
    return saveCostiObject(costoCmm);
  }



  /**
   * creaCostiDet
   * @param articolo Articolo
   * @param commessa Commessa
   * @param qta BigDecimal
   * @param costo Costo
   * @param costiCommessaElem CostiCommessaElem
   * @throws SQLException
   * @return int
   */
  protected int creaCostiDet(Articolo articolo, Commessa commessa, BigDecimal qta, Costo costo, CostiCommessaElem costiCommessaElem) throws SQLException
  {
    int result = ErrorCodes.NO_ROWS_UPDATED;
    Iterator iter = costo.getDettagli().iterator();
    if (costo instanceof CostoRisorsa || articolo.isGesSaldiCommessa())
    {
      while (iter.hasNext())
      {
        DettaglioCosto detCosto = (DettaglioCosto)iter.next();
        int rc = saveCostoCmmDet(detCosto, commessa.getIdCommessa(), qta, costiCommessaElem);
        if (rc < ErrorCodes.NO_ROWS_UPDATED)
        {
          return rc;
        }
        result += rc;
      }
    }
    else
    {
      valorizzaCostoDet(costo);
      while (iter.hasNext())
      {
        DettaglioCosto detCosto = (DettaglioCosto)iter.next();
        int rc = saveCostoCmmDet(detCosto, commessa.getIdCommessa(), qta, costiCommessaElem);
        if (rc < ErrorCodes.NO_ROWS_UPDATED)
        {
          return rc;
        }
        result += rc;
      }
    }
    costiCommessaElem.setDeepRetrieveEnabled(true);
    ConnectionManager.pushConnection(costiDescriptor);
    costiCommessaElem.retrieve();
    ConnectionManager.popConnection(costiDescriptor);
    costiCommessaElem.iCalcoloImporto = false;
    cacoloCosti(costiCommessaElem);
    saveCostiObject(costiCommessaElem);
    return result;
  }
 //14964
 protected int creaCostiDetPrev(Articolo articolo, Commessa commessa, BigDecimal qta, PreventivoCommessaVoce voce, CostiCommessaElem costiCommessaElem) throws SQLException
 {
   int result = ErrorCodes.NO_ROWS_UPDATED;
   int rc = saveCostoCmmDetPrev(voce,commessa.getIdCommessa(), qta, costiCommessaElem);
       if (rc < ErrorCodes.NO_ROWS_UPDATED)
       {
         return rc;
       }
      result += rc;

   costiCommessaElem.setDeepRetrieveEnabled(true);
   ConnectionManager.pushConnection(costiDescriptor);
   costiCommessaElem.retrieve();
   ConnectionManager.popConnection(costiDescriptor);
   costiCommessaElem.iCalcoloImporto = false;
   cacoloCosti(costiCommessaElem);
   saveCostiObject(costiCommessaElem);
   return result;
 }
 protected int creaCostiDetPrev(Articolo articolo, Commessa commessa, BigDecimal qta, PreventivoCommessaRiga riga, CostiCommessaElem costiCommessaElem) throws SQLException
 {
   int result = ErrorCodes.NO_ROWS_UPDATED;
   int rc = saveCostoCmmDetPrev(riga,commessa.getIdCommessa(), qta, costiCommessaElem);
       if (rc < ErrorCodes.NO_ROWS_UPDATED)
       {
         return rc;
       }
      result += rc;
   return result;
 }

  /**
   * valorizzaCostoDet
   * @param costo Costo
   */
  protected void valorizzaCostoDet(Costo costo) throws java.sql.SQLException//31513
  {
    List compDaValorizz = new ArrayList();
    List compValorizz = new ArrayList();
    Iterator iterator = costo.getDettagli().iterator();
    while (iterator.hasNext())
    {
      DettaglioCosto detCosto = (DettaglioCosto)iterator.next();
      if (((CostoArticolo)costo).getClasseMerceologica().getIdComponenteCosto() != null && detCosto.getIdComponenteCosto().equals(((CostoArticolo)costo).getClasseMerceologica().getIdComponenteCosto()))
      {
        Iterator iter = costo.getDettagli().iterator();
        while (iter.hasNext())
        {
          DettaglioCosto detCst = (DettaglioCosto)iter.next();
          if (detCst.getIdComponenteCosto().equals(PersDatiTecnici.getCurrentPersDatiTecnici().getIdRiferimento()))
          {
            detCosto.setCostoLivello(detCst.getCostoLivello());
            detCosto.setCostoLivelliInf(detCst.getCostoLivelliInf());
            compValorizz.add(detCosto);
          }
        }
      }
      else if (detCosto.getProvenienzaOfCompCosto() == ComponenteCosto.SOLO_TOTALE && isUtilizzoFormula(costo, detCosto))
      {
        compDaValorizz.add(detCosto);
      }
      else
      {
        detCosto.setCostoLivello(new BigDecimal("0"));
        detCosto.setCostoLivelliInf(new BigDecimal("0"));
        compValorizz.add(detCosto);
      }
    }

    boolean found = false;

    while (compDaValorizz.size() != 0)
    {
      found = false;
      Iterator valorizIte = compDaValorizz.iterator();
      //String idComponente = null;
      while (valorizIte.hasNext() && !found)
      {
        DettaglioCosto detCosto = (DettaglioCosto)valorizIte.next();
        //idComponente = detCosto.getIdComponenteCosto();

        FormulaCosti formulaCosti = detCosto.getComponenteCosto().getFormula();
        Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(detCosto.getComponenteCosto(), getAmbienteCosti().getIdAmbiente())); //Fix 8631
        formulaDaUtilizz.setVariables(buildVariables(costo.getDettagli()));
        //31513 inizio
        /*if (isFormulaCalcolabile(compDaValorizz,
                                 compUsedInFormula(formulaDaUtilizz), detCosto))*/
        if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), detCosto)) //31513
        //31513 fine
        {
          synchronized (this)
          {
            BigDecimal costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
            detCosto.setCostoLivello(costoCalcolato);
            detCosto.setCostoLivelliInf(new BigDecimal("0"));
          }
          compDaValorizz.remove(detCosto);
          compValorizz.add(detCosto);
          found = true;
        }
      }
    }
  }

  /**
   * isFormulaCalcolabile
   * @param compDaValorizz List
   * @param usedComp List
   * @param corrente DettaglioCosto
   * @return boolean
   */
  public static boolean isFormulaCalcolabile(List compDaValorizz, List usedComp,
                                             DettaglioCosto corrente)
  {
    if (usedComp == null || usedComp.size() == 0)
    {
      return true;
    }
    else
    {
      Iterator itComponenteUsed = usedComp.iterator();
      ComponenteCosto compCosto = null;
      while (itComponenteUsed.hasNext())
      {
        compCosto = (ComponenteCosto)itComponenteUsed.next();
        Iterator itDettaglio = compDaValorizz.iterator();
        DettaglioCosto dettCosto = null;
        while (itDettaglio.hasNext())
        {
          dettCosto = (DettaglioCosto)itDettaglio.next();
          if (compCosto.getIdComponenteCosto().equalsIgnoreCase(dettCosto.getComponenteCosto().getIdComponenteCosto()))
          {
            if (corrente.getProvenienzaOfCompCosto() == ComponenteCosto.SOLO_TOTALE)
            {
              return false;
            }
            if (!compCosto.getIdComponenteCosto().equals(corrente.getIdComponenteCosto()))
            {
              return false;
            }
          }
        }
      }
      return true;
    }
  }
  //31513 inizio
  public boolean isFormulaCalcolabileDouble(FormulaCosti formulaCosti, Formula formulaDaUtilizz, List compDaValorizz, List usedComp, CostiCommessaDet componente) throws SQLException {
    boolean formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
    if (!formCalc && !formulaCosti.equals(componente.getComponenteCosto().getFormula())) {
      formulaDaUtilizz = componente.getComponenteCosto().getFormula().getFormula();
      formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
    }
    return formCalc;
  }
  
  public boolean isFormulaCalcolabileDouble(FormulaCosti formulaCosti, Formula formulaDaUtilizz, List compDaValorizz, List usedComp, DettaglioCosto componente) throws SQLException {
	    boolean formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
	    if (!formCalc && !formulaCosti.equals(componente.getComponenteCosto().getFormula())) {
	      formulaDaUtilizz = componente.getComponenteCosto().getFormula().getFormula();
	      formCalc = isFormulaCalcolabile(compDaValorizz, compUsedInFormula(formulaDaUtilizz), componente);
	    }
	    return formCalc;
  }
  //31513 fine
  /**
   * compUsedInFormula
   * @param formula Formula
   * @return List
   */
  public List compUsedInFormula(com.thera.thermfw.formula.Formula formula)
  {
    List compUsedList = new ArrayList();
    Set variabili = formula.getUsedVariables();
    Iterator variabiliIter = variabili.iterator();
    String variabile;
    List Componenti;
    try
    {
      String where = ComponenteCostoTM.ID_AZIENDA + " = '" + getIdAzienda() + "' ";
      Componenti = ComponenteCosto.retrieveList(where, "", false);
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
      return null;
    }

    ComponenteCosto cmp = (ComponenteCosto)Factory.createObject(ComponenteCosto.class);
    cmp.setIdAzienda(getIdAzienda());
    int index = 0;
    while (variabiliIter.hasNext())
    {
      variabile = variabiliIter.next().toString();
      cmp.setIdComponenteCosto(componenteId(variabile));
      index = Componenti.indexOf(cmp);
      if (index >= 0 && Componenti != null)
      {
        compUsedList.add(Componenti.get(index));
      }
    }
    return compUsedList;
  }

  /**
   * componenteId
   * @param idVariabile String
   * @return String
   */
  private String componenteId(String idVariabile)
  {
    if (idVariabile.endsWith(VariabiliCosti.LIVELLO))
    {
      return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO.length());
    }
    if (idVariabile.endsWith(VariabiliCosti.LIVELLO_INFERIORE))
    {
      return idVariabile.substring(0, idVariabile.length() - VariabiliCosti.LIVELLO_INFERIORE.length());
    }
    return idVariabile;
  }

  /*
     protected void valorizzaCostoDet(Costo costo, DettaglioCosto detCosto){
    if(((CostoArticolo)costo).getClasseMerceologica().getIdComponenteCosto() != null && detCosto.getIdComponenteCosto().equals(((CostoArticolo)costo).getClasseMerceologica().getIdComponenteCosto())){
      Iterator iter = costo.getDettagli().iterator();
      while (iter.hasNext()) {
        DettaglioCosto detCst = (DettaglioCosto)iter.next();
        if(detCst.getIdComponenteCosto().equals(PersDatiTecnici.getCurrentPersDatiTecnici().getIdRiferimento())){
          detCosto.setCostoLivello(detCst.getCostoLivello());
          detCosto.setCostoLivelliInf(detCst.getCostoLivelliInf());
        }
      }
    }
    else if(detCosto.getProvenienzaOfCompCosto() == ComponenteCosto.SOLO_TOTALE && isUtilizzoFormula(costo, detCosto)){
      FormulaCosti formulaCosti = detCosto.getComponenteCosto().getFormula();
      com.thera.thermfw.formula.Formula formulaDaUtilizz = formulaCosti.getComponenteFormula(detCosto.getComponenteCosto(), getAmbienteCosti().getIdAmbiente());
      formulaDaUtilizz.setVariables(buildVariables(costo.getDettagli()));
      synchronized(this){
        BigDecimal costoCalcolato = (BigDecimal) formulaDaUtilizz.evaluate();
        detCosto.setCostoLivello(costoCalcolato);
        detCosto.setCostoLivelliInf(new BigDecimal("0"));
      }
    }
    else{
      detCosto.setCostoLivello(new BigDecimal("0"));
      detCosto.setCostoLivelliInf(new BigDecimal("0"));
    }
     }
   */

  /**
   * isUtilizzoFormula
   * @param costo Costo
   * @param detCosto DettaglioCosto
   * @return boolean
   */
  protected boolean isUtilizzoFormula(Costo costo, DettaglioCosto detCosto)
  {
    Iterator iter = ((CostoArticolo)costo).getClasseMerceologica().getSchemaCosto().getComponenti().iterator();
    while (iter.hasNext())
    {
      LinkCompSchema linkCompSchema = (LinkCompSchema)iter.next();
      if (linkCompSchema.getIdComponenteCosto().equals(detCosto.getIdComponenteCosto()) && linkCompSchema.isUtilizzoFormula())
      {
        return true;
      }
    }
    return false;
  }

  /**
   * saveCostoCmmDet
   * @param detCosto DettaglioCosto
   * @param idCommessa String
   * @param qta BigDecimal
   * @param costiCommessaElem CostiCommessaElem
   * @throws SQLException
   * @return int
   */
  protected int saveCostoCmmDet(DettaglioCosto detCosto, String idCommessa, BigDecimal qta, CostiCommessaElem costiCommessaElem) throws SQLException
  {
    CostiCommessaDet costoCmmDet = (CostiCommessaDet)Factory.createObject(CostiCommessaDet.class);
    costoCmmDet.setIdAzienda(getIdAzienda());
    costoCmmDet.setIdProgrStoric(iProgressivoStorico);
    costoCmmDet.setIdCommessa(idCommessa);
    costoCmmDet.setIdRigaElem(costiCommessaElem.getIdRigaElemento());
    costoCmmDet.setTipoDettaglioCosto(CostiCommessaDet.NORMALE);
    costoCmmDet.setIdComponCosto(detCosto.getIdComponenteCosto());
    //Fix 04598 Mz inizio
    //costoCmmDet.setDescrizioneCompCost(detCosto.getDescCompCosto());
    //Fix 04598 Mz inizio
    if (detCosto.getCostoLivello() != null)
    {
      costoCmmDet.setCostoLivello(detCosto.getCostoLivello().multiply(qta));
    }
    if (detCosto.getCostoLivelliInf() != null && detCosto.getCostoLivelliInf() != null)
    {
      costoCmmDet.setCostoLivelliInf(detCosto.getCostoLivelliInf().multiply(qta));
    }
    if (detCosto.getCostoTotale() != null)
    {
      costoCmmDet.setCostoTotale(detCosto.getCostoTotale().multiply(qta));
    }
    return saveCostiObject(costoCmmDet);
  }

  protected int saveCostoCmmDetPrev(PreventivoCommessaVoce voce , String idCommessa, BigDecimal qta, CostiCommessaElem costiCommessaElem) throws SQLException
   {
     CostiCommessaDet costoCmmDet = (CostiCommessaDet)Factory.createObject(CostiCommessaDet.class);
     costoCmmDet.setIdAzienda(getIdAzienda());
     costoCmmDet.setIdProgrStoric(iProgressivoStorico);
     costoCmmDet.setIdCommessa(idCommessa);
     costoCmmDet.setIdRigaElem(costiCommessaElem.getIdRigaElemento());
     costoCmmDet.setTipoDettaglioCosto(CostiCommessaDet.NORMALE);
     costoCmmDet.setIdComponCosto(voce.getRComponCosto());
     //Fix 22273 Inizio
     if(isValutaPrevAziendale()){
       costoCmmDet.setCostoLivello(voce.getCostoRifer());
       costoCmmDet.setCostoLivelliInf(voce.getCosTotale());
       costoCmmDet.setCostoTotale(voce.getCosTotale());
       costoCmmDet.setCostoUnitario(voce.getCostoRifer());
     }
     else{
       costoCmmDet.setCostoLivello(voce.getCosUnitarioAZ());
       costoCmmDet.setCostoLivelliInf(voce.getCosTotaleAZ());
       costoCmmDet.setCostoTotale(voce.getCosTotaleAZ());
       costoCmmDet.setCostoUnitario(voce.getCosUnitarioAZ());
     }
     //Fix 22273 Fine
     return saveCostiObject(costoCmmDet);
   }
   protected int saveCostoCmmDetPrev(PreventivoCommessaRiga riga , String idCommessa, BigDecimal qta, CostiCommessaElem costiCommessaElem) throws SQLException
   {
     int rc = ErrorCodes.NO_ROWS_UPDATED;
     List costi = riga.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti();
     Iterator ite = costi.iterator();
     int result = ErrorCodes.NO_ROWS_UPDATED;
     while(ite.hasNext())
     {
       LinkCompSchema costo  = (LinkCompSchema)ite.next();
       if(costo.getIdComponenteCosto() != riga.getArticolo().getClasseMerclg().getIdComponenteCosto())
       {
         rc = saveCostoCmmDetEmpty(riga , idCommessa, qta, costiCommessaElem,costo.getIdComponenteCosto());
         if (rc < ErrorCodes.NO_ROWS_UPDATED)
        {
          return rc;
        }
        result += rc;
       }
     }
    return result;
   }
   protected int saveCostoCmmDetEmpty(PreventivoCommessaRiga riga , String idCommessa, BigDecimal qta, CostiCommessaElem costiCommessaElem, String idCompoenentoCosti) throws SQLException
   {
     CostiCommessaDet costoCmmDet = (CostiCommessaDet)Factory.createObject(CostiCommessaDet.class);
     costoCmmDet.setIdAzienda(getIdAzienda());
     costoCmmDet.setIdProgrStoric(iProgressivoStorico);
     costoCmmDet.setIdCommessa(idCommessa);
     costoCmmDet.setIdRigaElem(costiCommessaElem.getIdRigaElemento());
     costoCmmDet.setTipoDettaglioCosto(CostiCommessaDet.NORMALE);
     costoCmmDet.setIdComponCosto(idCompoenentoCosti);
     costoCmmDet.setCostoLivello(new BigDecimal("0"));
     costoCmmDet.setCostoLivelliInf(new BigDecimal("0"));
     costoCmmDet.setCostoTotale(new BigDecimal("0"));
     PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
     return saveCostiObject(costoCmmDet);

   }



  /**
   * buildVariables
   * @param costoDetList List
   * @return VariablesCollection
   */
  protected VariablesCollection buildVariables(List costoDetList)
  {
    VariablesCollection variableCollection = new VariablesCollection()
    {
      public Object getVariableValue(String name)
      {
        Object obj = values.get(name);
        if (obj != null)
        {
          return ((FunctionVariable)obj).getValue();
        }
        return new BigDecimal("0");
      }
    };
    try
    {
      Iterator iter = costoDetList.iterator();
      FunctionVariable variables;
      //FunctionVariable variablesL;
      //FunctionVariable variablesI;
      ComponenteCosto compCosto;
      DettaglioCosto costoDet;
      while (iter.hasNext())
      {
        costoDet = (DettaglioCosto)iter.next();
        compCosto = costoDet.getComponenteCosto();
        variables = new FunctionVariable(compCosto.getIdComponenteCosto(), compCosto.getDescrizione().getDescrizioneRidotta(), null, ExpressionTypes.NUMBER);
        variables.setType(ExpressionTypes.NUMBER);
        variables.setValue(costoDet.getCostoTotale());
        variableCollection.addVariable(variables);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    return variableCollection;
  }

  /**
   * saveObject
   * @param obj PersistentObject
   * @param descriptor ConnectionDescriptor
   * @throws SQLException
   * @return int
   */
  public int saveObject(PersistentObject obj, ConnectionDescriptor descriptor) throws SQLException
  {
    ConnectionManager.pushConnection(descriptor);
    int rc = obj.save();
    ConnectionManager.popConnection(descriptor);
    return rc;
  }

  /**
   * saveReportObject
   * @param obj PersistentObject
   * @throws SQLException
   * @return int
   */
  public int saveReportObject(PersistentObject obj) throws SQLException
  {
    return saveObject(obj, reportDescriptor);
  }

  /**
   * saveCostiObject
   * @param obj PersistentObject
   * @throws SQLException
   * @return int
   */
  public int saveCostiObject(PersistentObject obj) throws SQLException
  {
    return saveObject(obj, costiDescriptor);
  }

  /**
   * doCommit
   * @param descriptor ConnectionDescriptor
   * @throws SQLException
   */
  public void doCommit(ConnectionDescriptor descriptor) throws SQLException
  {
    ConnectionManager.pushConnection(descriptor);
    ConnectionManager.commit();
    ConnectionManager.popConnection(descriptor);
  }

  /**
   * doRptCommit
   * @throws SQLException
   */
  public void doRptCommit() throws SQLException
  {
    doCommit(reportDescriptor);
  }

  /**
   * doCostiCommit
   * @throws SQLException
   */
  public void doCostiCommit() throws SQLException
  {
    doCommit(costiDescriptor);
  }

  /**
   * doRollback
   * @param descriptor ConnectionDescriptor
   * @throws SQLException
   */
  public void doRollback(ConnectionDescriptor descriptor) throws SQLException
  {
    ConnectionManager.pushConnection(descriptor);
    ConnectionManager.rollback();
    ConnectionManager.popConnection(descriptor);
  }

  /**
   * doRptRollback
   * @throws SQLException
   */
  public void doRptRollback() throws SQLException
  {
    doRollback(reportDescriptor);
  }

  /**
   * doCostiRollback
   * @throws SQLException
   */
  public void doCostiRollback() throws SQLException
  {
    doRollback(costiDescriptor);
  }

  /**
   * initializeUserParameters
   */
  protected void initializeUserParameters()
  {
    String tipologia = getDescAttrRef("TipologiaCostoTraCmm", getTipologiaCosto());
    addUserParameter("TipologiaCostoNLS", tipologia);
    if (isStampaPreventivoCommessa())
    {

      String parmTipoCosto = getDescAttrRef("TipologiaCostoTraCmm", getTipologiaCosto());
      String parmStampaEsplCompl = "";
      if (getTipologiaCosto() == TIPOLOGIA_COSTO__PREZZO_PREVENTIVATO)
      {
        parmStampaEsplCompl = getDescAttrRef("Booleano", iStampaPreventivoCommessaBatch.getDBValue(false));
      }
      else
      {
        parmStampaEsplCompl = getDescAttrRef("Booleano", iStampaPreventivoCommessaBatch.getDBValue(true));
      }
      String parmArticoliRisorse = getDescAttrRef("ArticoliRisorse", StampaPreventivoCommessa.ENTRAMBI);
      String parmSoloCommessaProv = getDescAttrRef("Booleano", iStampaPreventivoCommessaBatch.getDBValue(false));
      addUserParameter("TipoCostoDesc", parmTipoCosto);
      addUserParameter("StampaEsplCompDesc", parmStampaEsplCompl);
      addUserParameter("ArticoliRisorseDesc", parmArticoliRisorse);
      addUserParameter("SoloCommProvDesc", parmSoloCommessaProv);
      // 14964

      //String paramFiltri = " :  " + ((String)((ArrayList)iStampaPreventivoCommessaBatch.getCondizioneFiltri().getVettoreValoriFiltro().get(1)).get(1)) + "   " + getCommessa().getIdCommessa();
     Commessa commessa = getCommessa();
     if(this.getTipoElaborazione() == this.TIPO_ELAB_DA_PREVENTIVO)
    commessa = this.getPreventivoCommessa().getCommessa();
      String paramFiltri = " :  " + ((String)((ArrayList)iStampaPreventivoCommessaBatch.getCondizioneFiltri().getVettoreValoriFiltro().get(1)).get(1)) + "   " + commessa.getIdCommessa();


      addUserParameter("Filtri", paramFiltri);
    }
  }

  /**
   * initializeUserEnums
   */
  protected void initializeUserEnums()
  {
    String tableName = removeSchema(ReportAnomalieTrasfCostiTM.TABLE_NAME);
    addEnum("tipoRisorsa", tableName + "." + ReportAnomalieTrasfCostiTM.R_TIPO_RISORSA, "TipoRisorsa");
    addEnum("livelloRisorsa", tableName + "." + ReportAnomalieTrasfCostiTM.R_LIVELLO_RISORSA, "LivelloRisorsa");

    if (isStampaPreventivoCommessa())
    {
      String tableNamePrvCmm = removeSchema(ReportPreventivoCommessaDettaglioTM.TABLE_NAME);
      String tableNameTes = removeSchema(ReportPreventivoCommessaTestataTM.TABLE_NAME);
      addEnum("TipoArtRisPrd", tableNamePrvCmm + "." + ReportPreventivoCommessaDettaglioTM.ART_RIS, "ArtRisPrd");
      addEnum("TipoParte", tableNamePrvCmm + "." + ReportPreventivoCommessaDettaglioTM.TIPO_PARTE, "TipoParte");
      addEnum("TipoRisorse", tableNamePrvCmm + "." + ReportPreventivoCommessaDettaglioTM.R_TIPO_RISORSA, "TipoRisorsa");
      addEnum("StatoAvanzamento", tableNameTes + "." + ReportPreventivoCommessaTestataTM.STATO_AVANZAMENTO, "StatoAvanzCmm");
    }
  }

  /**
   * removeSchema
   * @param name String
   * @return String
   */
  public static String removeSchema(String name)
  {
    int pos = name.indexOf(".");
    if (pos > 0)
    {
      name = name.substring(pos + 1);
    }
    return name;
  }

  /**
   * getDescAttrRef
   * @param IdRef String
   * @param value char
   * @return String
   */
  public static String getDescAttrRef(String IdRef, char value)
  {
    EnumType eType = new EnumType(IdRef);
    return eType.descriptionFromValue(String.valueOf(value));
  }

  /**
   * UserErrorMessage
   * <br></br><b>Copyright (C) : Thera s.p.a.</b>
   * @author Jed
   */
  public class UserErrorMessage
    extends ErrorMessage
  {

    /**
     * UserErrorMessage
     * @param id String
     * @param parameters String[]
     */
    public UserErrorMessage(String id, String parameters[])
    {
      super(id, parameters);
    }

    /**
     * readFromDataBase
     * @return boolean
     */
    protected boolean readFromDataBase()
    {
      boolean found = false;
      if (stmt == null)
      {
        stmt = new CachedStatement(SQL_QUERY);
      }
      try
      {
        try
        {
          if (stmt.getStatement() == null)
          {
            return false;
          }
        }
        catch (NullPointerException e)
        {
          Trace.println("Connessione a DB non trovata.");
          return false;
        }
        Database db = ConnectionManager.getCurrentDatabase();
        //...FIX 6773 (Riallineamento a seguito della fix 5626)
        String loc = getLanguage();
        //db.setString(stmt.getStatement(),1,id);
        //db.setString(stmt.getStatement(),2,loc);//Mod. 1356
        db.setString(stmt.getStatement(), 1, loc);
        db.setString(stmt.getStatement(), 2, id);
        ResultSet resSet = stmt.executeQuery();
        found = resSet.next();
        if (found)
        {
          severity = resSet.getString("SEVERITY").trim().charAt(0);
          text = resSet.getString("TEXT").trim();
          longText = resSet.getString("LONG_TEXT");
        }
        resSet.close();
      }
      catch (SQLException e)
      {
        Trace.println("Errore con codice " + id + " non trovato su DB");
        return false;
      }
      return found;
    }

    /**
     * getLanguage
     * @return String
     */
    public String getLanguage()
    {
      String loc = Security.getCurrentUser().getLanguage();
      if (loc == null)
      {
        loc = SystemParam.getDefaultLocaleString();
      }
      return loc;
    }
  }

  /**
   * getFormatedDate
   * @param date Date
   * @return String
   */
  protected String getFormatedDate(java.sql.Date date)
  {
    String dataString = "";
    if (date != null)
    {
      ClassAD cad = getClassAD("DataRiferimento");
      Type type = null;
      if (cad != null)
      {
        type = cad.getType();
        dataString = type.objectToString(date);
      }
    }
    return dataString;
  }

  /**
   * getClassAD
   * @param attributeName String
   * @return ClassAD
   */
  protected ClassAD getClassAD(String attributeName)
  {
    try
    {
      ClassADCollection cad = ClassADCollectionManager.collectionWithName("GestioneAmbienteCosti");
      return cad.getAttribute(attributeName);
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream);
      return null;
    }
  }

  /**
   * cacoloCosti
   * @param costiCommessaElem CostiCommessaElem
   */
  public void cacoloCosti(CostiCommessaElem costiCommessaElem)
  {
    PersDatiTecnici p = PersDatiTecnici.getCurrentPersDatiTecnici();
    if (p.getRiferimento() == null)
    {
      costiCommessaElem.setCostoRiferimento(null);
    }
    if (p.getPrimo() == null)
    {
      costiCommessaElem.setCostoPrimo(null);
    }
    if (p.getIndustriale() == null)
    {
      costiCommessaElem.setCostoIndustriale(null);
    }
    if (p.getGenerale() == null)
    {
      costiCommessaElem.setCostoGenerale(null);
    }
    CostiCommessaDet dettCosto;
    List dettagli = costiCommessaElem.getCostiCommessaDet();
    Iterator dettIter = dettagli.iterator();
    //BigDecimal totalCosti = new BigDecimal("0");
    while (dettIter.hasNext())
    {
      dettCosto = (CostiCommessaDet)dettIter.next();
      if (dettCosto.getComponenteCosto().equals(p.getRiferimento()))
      {
        costiCommessaElem.setCostoRiferimento(dettCosto.getCostoTotale());
      }
      if (dettCosto.getComponenteCosto().equals(p.getPrimo()))
      {
        costiCommessaElem.setCostoPrimo(dettCosto.getCostoTotale());
      }
      if (dettCosto.getComponenteCosto().equals(p.getIndustriale()))
      {
        costiCommessaElem.setCostoIndustriale(dettCosto.getCostoTotale());
      }
      if (dettCosto.getComponenteCosto().equals(p.getGenerale()))
      {
        costiCommessaElem.setCostoGenerale(dettCosto.getCostoTotale());
      }
    }
  }

  // Fix 04171 Begin

  /**
   * updateAmbienteCosti
   * @return int
   * @throws SQLException
   */
  protected int updateAmbienteCosti() throws SQLException
  {
    getAmbienteCosti().setCostiTrasferiti(true);
    return saveCostiObject(getAmbienteCosti());
  }

  /**
   * getIdCommessa
   * @return String
   */
  public String getIdCommessa()
  {
    if (getCommessa() != null)
    {
      return getCommessa().getIdCommessa();
    }
    return null;
  }

  /**
   *
   * @param nodoArticolo EspNodoArticolo
   * @param risorsa EspNodoRisorsa
   * @param costoCmmElm CostiCommessaElem
   * @throws SQLException
   * @return int
   */
  protected int elaborateRisorsaACosto(EspNodoArticolo nodoArticolo, EspNodoRisorsa risorsa, CostiCommessaElem costoCmmElm) throws SQLException
  {

    int result = ErrorCodes.NO_ROWS_UPDATED;
    Articolo articolo = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getArticolo() : nodoArticolo.getAttivitaProdMateriale().getArticolo();
    Integer idVersione = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdVersione() : nodoArticolo.getAttivitaProdMateriale().getIdVersione();
    Integer idConfigurazione = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getIdConfigurazione() : nodoArticolo.getAttivitaProdMateriale().getIdConfigurazione();
    Configurazione configurazione = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa().getConfigurazione() : nodoArticolo.getAttivitaProdMateriale().getConfigurazione();
    Commessa commessa = (nodoArticolo.getAttivitaProdMateriale() == null) ? getCommessa() : nodoArticolo.getAttivitaProdMateriale().getCommessa();
    String stb = (nodoArticolo.getIdStabilimento() == null || nodoArticolo.getIdStabilimento().equals("")) ? getCommessa().getIdStabilimento() : nodoArticolo.getIdStabilimento();

    if (risorsa.getAttivitaProdRisorsa().getRisorsa().getComponenteCosto() == null)
    {
      int rc = addAnomalie(articolo.getIdArticolo(), idVersione, configurazione, commessa.getIdCommessa(), risorsa.getAttivitaProdRisorsa().getTipoRisorsa(), risorsa.getAttivitaProdRisorsa().getLivelloRisorsa(), risorsa.getAttivitaProdRisorsa().getRisorsa().getIdRisorsa(), risorsa.getNodoPadre().getAttivitaProduttiva().getIdAttivita(), stb, "THIP20T160");
      if (rc < ErrorCodes.NO_ROWS_UPDATED)
      {
        return rc;
      }
      result += rc;
    }

    if (risorsa.getAttivitaProdRisorsa().getRisorsa().getSchemaCosto() == null)
    {
      int rc = addAnomalie(articolo.getIdArticolo(), idVersione, configurazione, commessa.getIdCommessa(), risorsa.getAttivitaProdRisorsa().getTipoRisorsa(), risorsa.getAttivitaProdRisorsa().getLivelloRisorsa(), risorsa.getAttivitaProdRisorsa().getRisorsa().getIdRisorsa(), risorsa.getNodoPadre().getAttivitaProduttiva().getIdAttivita(), stb, "THIP20T159");
      if (rc < ErrorCodes.NO_ROWS_UPDATED)
      {
        return rc;
      }
      result += rc;
    }

    if (result > ErrorCodes.NO_ROWS_UPDATED)
    {
      return result;
    }

    BigDecimal cstCalc = risorsa.getCostoCalc();
    BigDecimal qtaMult = risorsa.getNodoPadre().getQtaMagazzinoCalc();
    boolean rsrPrcp = false;
    if (risorsa.getAttivitaProdRisorsa().getPolConsRisorse() == AttivitaRisorsa.PRINCIPALE)
    {
      rsrPrcp = true;
    }
    if (costoCmmElm == null)
    {
      costoCmmElm = creaCostoCmmElemRisorsaACosto(commessa,
                                                  articolo,
                                                  idVersione,
                                                  idConfigurazione, stb,
                                                  risorsa.getNodoPadre().getAttivitaProduttiva().getIdAttivita(),
                                                  getCommessa().getIdUmPrmMag(),
                                                  risorsa.getAttivitaProdRisorsa().getAttivitaProduttiva().getCentroLavoro().getIdReparto(),
                                                  risorsa.getAttivitaProdRisorsa().getAttivitaProduttiva().getIdCentroLavoro(),
                                                  risorsa.getAttivitaProdRisorsa().getTipoRisorsa(),
                                                  risorsa.getAttivitaProdRisorsa().getLivelloRisorsa(),
                                                  risorsa.getAttivitaProdRisorsa().getRisorsa().getIdRisorsa(),
                                                  rsrPrcp,
                                                  risorsa.getAttivitaProdRisorsa().getTipoRilevazione(),
                                                  risorsa.getTempoCalc(),
                                                  risorsa.getNodoPadre().getQtaMagazzinoCalc(),
                                                  risorsa.getAttivitaProdRisorsa().getAttivitaProduttiva().getIdOperazione());
      creaCostiCmmDetRisorsaACosto(risorsa.getAttivitaProdRisorsa().getRisorsa(),
                                   commessa.getIdCommessa(),
                                   costoCmmElm);
    }
    else
    {
      costoCmmElm.setOreRichieste(costoCmmElm.getOreRichieste().add(risorsa.getTempoCalc())); //Fix 9966

    }
    List costiComessaDet = getCostiComessaDet(costoCmmElm);
    initCostoCmmDetFromCompRisorsa(risorsa.getAttivitaProdRisorsa().getRisorsa(), cstCalc, costiComessaDet);
    valorizzaCostoDet(risorsa.getAttivitaProdRisorsa().getRisorsa(), costiComessaDet);
    aggiornaListaCalculata(costiComessaDet, qtaMult);
    aggiornaLisataCommessaDet(costoCmmElm.getCostiCommessaDet(), costiComessaDet);
    costoCmmElm.iCalcoloImporto = false;
    cacoloCosti(costoCmmElm);
    return saveCostiObject(costoCmmElm);
  }

  /**
   *
   * @param commessa Commessa
   * @param articolo Articolo
   * @param versione Integer
   * @param configurazione Integer
   * @param stabilimento String
   * @param attivita String
   * @param idUm String
   * @param idReparto String
   * @param idCentroLavoro String
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param risorsaPrp boolean
   * @param tipoLavRsr char
   * @param oreRich BigDecimal
   * @param qtaEff BigDecimal
   * @param idOperazione String
   * @throws SQLException
   * @return CostiCommessaElem
   */
  protected CostiCommessaElem creaCostoCmmElemRisorsaACosto(Commessa commessa, Articolo articolo, Integer versione, Integer configurazione, String stabilimento, String attivita, String idUm, String idReparto, String idCentroLavoro, char tipoRisorsa, char livelloRisorsa, String idRisorsa, boolean risorsaPrp, char tipoLavRsr, BigDecimal oreRich, BigDecimal qtaEff, String idOperazione) throws SQLException
  {
    CostiCommessaElem costoCmmElem = (CostiCommessaElem)Factory.createObject(CostiCommessaElem.class);
    costoCmmElem.setIdAzienda(getIdAzienda());
    costoCmmElem.setIdProgrStoric(iProgressivoStorico);
    costoCmmElem.setIdCommessa(commessa.getIdCommessa());
    costoCmmElem.setIdArticolo(articolo.getIdArticolo());
    costoCmmElem.setIdVersione(versione);
    costoCmmElem.setIdConfigurazione(configurazione);
    costoCmmElem.setIdStabilimento(stabilimento);
    costoCmmElem.setIdAttivita(attivita);
    costoCmmElem.setIdOperazione(idOperazione);
    costoCmmElem.setQuantita(qtaEff);
    costoCmmElem.setIdUmPrmMag(idUm);
    costoCmmElem.setTipoParte(articolo.getTipoParte());
    costoCmmElem.setIdReparto(idReparto);
    costoCmmElem.setIdCentroLavoro(idCentroLavoro);
    costoCmmElem.setTipoRisorsa(tipoRisorsa);
    costoCmmElem.setLivelloRisorsa(livelloRisorsa);
    costoCmmElem.setIdRisorsa(idRisorsa);
    costoCmmElem.setRisorsaPrincipale(risorsaPrp);
    costoCmmElem.setTipoRilevazioneRsr(tipoLavRsr);
    costoCmmElem.setOreRichieste(oreRich);
    //Fix 04567 Begin
    costoCmmElem.setCostoElementare(true);
    //Fix 04567 End
    return costoCmmElem;
  }

  /**
   * initCostoCmmDetFromCompRisorsa
   * @param risorsa Risorsa
   * @param cstCalc BigDecimal
   * @param costiCommessaDet List
   * @throws SQLException
   */
  protected void initCostoCmmDetFromCompRisorsa(Risorsa risorsa, BigDecimal cstCalc, List costiCommessaDet) throws SQLException
  {
    Iterator iter = costiCommessaDet.iterator();
    while (iter.hasNext())
    {
      CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
      if (costoCmmDet.getIdComponCosto().equals(risorsa.getIdComponenteCosto()))
      {
        costoCmmDet.setCostoLivello(cstCalc);
        costoCmmDet.setCostoTotale(cstCalc);
        return;
      }
    }
  }

  /**
   * creaCostiCmmDetRisorsaACosto
   * @param risorsa Risorsa
   * @param idCommessa String
   * @param costiCommessaElem CostiCommessaElem
   * @throws SQLException
   */
  protected void creaCostiCmmDetRisorsaACosto(Risorsa risorsa, String idCommessa, CostiCommessaElem costiCommessaElem) throws SQLException
  {
    Iterator iter = risorsa.getSchemaCosto().getComponenti().iterator();
    while (iter.hasNext())
    {
      LinkCompSchema linkCompSchema = (LinkCompSchema)iter.next();
      ComponenteCosto compCosto = linkCompSchema.getComponenteCosto();
      CostiCommessaDet costoCmmDet = (CostiCommessaDet)Factory.createObject(CostiCommessaDet.class);
      costoCmmDet.setIdAzienda(getIdAzienda());
      costoCmmDet.setIdProgrStoric(iProgressivoStorico);
      costoCmmDet.setIdCommessa(idCommessa);
      costoCmmDet.setIdRigaElem(costiCommessaElem.getIdRigaElemento());
      costoCmmDet.setTipoDettaglioCosto(CostiCommessaDet.NORMALE);
      costoCmmDet.setIdComponCosto(compCosto.getIdComponenteCosto());
      //Fix 04598 Mz inizio
      //costoCmmDet.setDescrizioneCompCost(compCosto.getDescription());
      //Fix 04598 Mz fine
      costoCmmDet.setCostoLivello(new BigDecimal("0"));
      costoCmmDet.setCostoTotale(new BigDecimal("0"));
      costoCmmDet.setCostoLivelliInf(new BigDecimal("0"));
      costiCommessaElem.getCostiCommessaDet().add(costoCmmDet);
    }
  }

  /**
   * valorizzaCostoDet
   * @param risorsa Risorsa
   * @param costiComessaDet List
   */
  protected void valorizzaCostoDet(Risorsa risorsa, List costiComessaDet) throws java.sql.SQLException//31513
  {
    List compDaValorizz = new ArrayList();
    List compValorizz = new ArrayList();
    Iterator iterator = costiComessaDet.iterator();
    while (iterator.hasNext())
    {
      CostiCommessaDet detCosto = (CostiCommessaDet)iterator.next();
      if ((detCosto.getComponenteCosto().getProvenienza() ==
           ComponenteCosto.SOLO_TOTALE ||
           detCosto.getComponenteCosto().getProvenienza() ==
           ComponenteCosto.CALCOLATA_FORMULA) &&
          isUtilizzoFormula(risorsa, detCosto))
      {
        compDaValorizz.add(detCosto);
      }
      else
      {
        compValorizz.add(detCosto);
      }
    }

    boolean found = false;

    while (compDaValorizz.size() != 0)
    {
      found = false;
      Iterator valorizIte = compDaValorizz.iterator();
      //String idComponente = null;
      while (valorizIte.hasNext() && !found)
      {
        CostiCommessaDet detCosto = (CostiCommessaDet)valorizIte.next();
        //idComponente = detCosto.getIdComponCosto();
        FormulaCosti formulaCosti = detCosto.getComponenteCosto().getFormula();
        Formula formulaDaUtilizz = cloneFormula(formulaCosti.getComponenteFormula(detCosto.getComponenteCosto(), getAmbienteCosti().getIdAmbiente())); //Fix 8631
        formulaDaUtilizz.setVariables(buildVariablesDetCmm(costiComessaDet));
        //31513 inizio
        /*if (isFormulaCalcolabile(compDaValorizz,
                                 compUsedInFormula(formulaDaUtilizz), detCosto))*/
        if(isFormulaCalcolabileDouble(formulaCosti, formulaDaUtilizz, compDaValorizz, compUsedInFormula(formulaDaUtilizz), detCosto)) 
        //31513 fine
        {
          synchronized (this)
          {
            BigDecimal costoCalcolato = (BigDecimal)formulaDaUtilizz.evaluate();
            //Fix 22273 Inizio
            if (costoCalcolato != null && detCosto.getCostoTotale() != null)
              costoCalcolato.add(detCosto.getCostoTotale());
            //Fix 22273 Fine
            detCosto.setCostoLivello(costoCalcolato);
            detCosto.setCostoTotale(costoCalcolato);
            detCosto.setCostoLivelliInf(new BigDecimal("0"));
          }
          compDaValorizz.remove(detCosto);
          compValorizz.add(detCosto);
          found = true;
        }
      }
    }
  }

  /**
   *
   * @param risorsa Risorsa
   * @param detCosto CostiCommessaDet
   * @return boolean
   */
  protected boolean isUtilizzoFormula(Risorsa risorsa,
                                      CostiCommessaDet detCosto)
  {
    Iterator iter = risorsa.getSchemaCosto().getComponenti().iterator();
    while (iter.hasNext())
    {
      LinkCompSchema linkCompSchema = (LinkCompSchema)iter.next();
      if (linkCompSchema.getIdComponenteCosto().equals(detCosto.getIdComponCosto()) && linkCompSchema.isUtilizzoFormula())
      {
        return true;
      }
    }
    return false;
  }

  /**
   * buildVariablesDetCmm
   * @param costoDetList List
   * @return VariablesCollection
   */
  protected VariablesCollection buildVariablesDetCmm(List costoDetList)
  {
    VariablesCollection variableCollection = new VariablesCollection()
    {
      public Object getVariableValue(String name)
      {
        Object obj = values.get(name);
        if (obj != null && ((FunctionVariable)obj).getValue() != null)
        {
          return ((FunctionVariable)obj).getValue();
        }
        return new BigDecimal("0");
      }
    };
    try
    {
      Iterator iter = costoDetList.iterator();
      FunctionVariable variables;
      ComponenteCosto compCosto;
      CostiCommessaDet costoDet;
      while (iter.hasNext())
      {
        costoDet = (CostiCommessaDet)iter.next();
        compCosto = costoDet.getComponenteCosto();
        variables = new FunctionVariable(compCosto.getIdComponenteCosto(), compCosto.getDescrizione().getDescrizioneRidotta(), null, ExpressionTypes.NUMBER);
        variables.setType(ExpressionTypes.NUMBER);
        variables.setValue(costoDet.getCostoTotale());
        variableCollection.addVariable(variables);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace(Trace.excStream); //...FIX 07430
    }
    return variableCollection;
  }

  /**
   *
   * @param compDaValorizz List
   * @param usedComp List
   * @param corrente CostiCommessaDet
   * @return boolean
   */
  public static boolean isFormulaCalcolabile(List compDaValorizz, List usedComp,
                                             CostiCommessaDet corrente)
  {
    if (usedComp == null || usedComp.size() == 0)
    {
      return true;
    }
    else
    {
      Iterator itComponenteUsed = usedComp.iterator();
      ComponenteCosto compCosto = null;
      while (itComponenteUsed.hasNext())
      {
        compCosto = (ComponenteCosto)itComponenteUsed.next();
        Iterator itDettaglio = compDaValorizz.iterator();
        CostiCommessaDet dettCosto = null;
        while (itDettaglio.hasNext())
        {
          dettCosto = (CostiCommessaDet)itDettaglio.next();
          if (compCosto.getIdComponenteCosto().equalsIgnoreCase(dettCosto.getComponenteCosto().getIdComponenteCosto()))
          {
            if (corrente.getComponenteCosto().getProvenienza() == ComponenteCosto.SOLO_TOTALE)
            {
              return false;
            }
            if (!compCosto.getIdComponenteCosto().equals(corrente.getIdComponCosto()))
            {
              return false;
            }
          }
        }
      }
      return true;
    }
  }

  /**
   *
   * @param costoCmmElm CostiCommessaElem
   * @return List
   */
  protected List getCostiComessaDet(CostiCommessaElem costoCmmElm)
  {
    Vector result = new Vector();
    Iterator iter = costoCmmElm.getCostiCommessaDet().iterator();
    while (iter.hasNext())
    {
      CostiCommessaDet cstCmmDet = (CostiCommessaDet)iter.next();
      CostiCommessaDet newCstCmmDet = (CostiCommessaDet)Factory.createObject(CostiCommessaDet.class);
      try
      {
        newCstCmmDet.setEqual(cstCmmDet);
      }
      catch (CopyException ex)
      {
        ex.printStackTrace(Trace.excStream); //...FIX 07430
      }
      result.add(newCstCmmDet);
    }
    return result;
  }

  /**
   *
   * @param costiComessaDet List
   * @param qta BigDecimal
   */
  protected void aggiornaListaCalculata(List costiComessaDet, BigDecimal qta)
  {
    if (qta == null)
    {
      return;
    }
    Iterator iter = costiComessaDet.iterator();
    while (iter.hasNext())
    {
      CostiCommessaDet costoCmmDet = (CostiCommessaDet)iter.next();
      if (costoCmmDet.getCostoLivello() != null)
      {
        costoCmmDet.setCostoLivello(costoCmmDet.getCostoLivello() /*.multiply(qta)*/);
      }
      if (costoCmmDet.getCostoLivelliInf() != null)
      {
        costoCmmDet.setCostoLivelliInf(costoCmmDet.getCostoLivelliInf() /*.multiply(qta)*/);
      }
      if (costoCmmDet.getCostoTotale() != null)
      {
        costoCmmDet.setCostoTotale(costoCmmDet.getCostoTotale() /*.multiply(qta)*/);
      }
    }
  }

  /**
   *
   * @param cstCmmDetToUpdate List
   * @param cstCmmDetCalculated List
   */
  protected void aggiornaLisataCommessaDet(List cstCmmDetToUpdate, List cstCmmDetCalculated)
  {
    Iterator iterator = cstCmmDetToUpdate.iterator();
    while (iterator.hasNext())
    {
      CostiCommessaDet costoCmmDet = (CostiCommessaDet)iterator.next();
      Iterator iter = cstCmmDetCalculated.iterator();
      while (iter.hasNext())
      {
        CostiCommessaDet costoCmmDetCalculated = (CostiCommessaDet)iter.next();
        if (costoCmmDet.getIdComponCosto().equals(costoCmmDetCalculated.getIdComponCosto()))
        {
          aggiornaCostoCmmDet(costoCmmDet, costoCmmDetCalculated);
        }
      }
    }
  }

  /**
   *
   * @param costoCmmDetToUpdate CostiCommessaDet
   * @param costoCmmDetCalculated CostiCommessaDet
   */
  protected void aggiornaCostoCmmDet(CostiCommessaDet costoCmmDetToUpdate, CostiCommessaDet costoCmmDetCalculated)
  {
    if (costoCmmDetToUpdate.getCostoLivello() != null && costoCmmDetCalculated.getCostoLivello() != null)
    {
      costoCmmDetToUpdate.setCostoLivello(costoCmmDetToUpdate.getCostoLivello().add(costoCmmDetCalculated.getCostoLivello()));
    }
    if (costoCmmDetToUpdate.getCostoLivelliInf() != null && costoCmmDetCalculated.getCostoLivelliInf() != null)
    {
      costoCmmDetToUpdate.setCostoLivelliInf(costoCmmDetToUpdate.getCostoLivelliInf().add(costoCmmDetCalculated.getCostoLivelliInf()));
    }
    if (costoCmmDetToUpdate.getCostoTotale() != null && costoCmmDetCalculated.getCostoTotale() != null)
    {
      costoCmmDetToUpdate.setCostoTotale(costoCmmDetToUpdate.getCostoTotale().add(costoCmmDetCalculated.getCostoTotale()));
    }
  }

  /**
   *
   * @param idArticolo String
   * @param versione Integer
   * @param configurazione Configurazione
   * @param idCommessa String
   * @param tipoRisorsa char
   * @param livelloRisorsa char
   * @param idRisorsa String
   * @param idAttivita String
   * @param idStabilimento String
   * @param errorCode String
   * @throws SQLException
   * @return int
   */
  protected int addAnomalie(String idArticolo, Integer versione, Configurazione configurazione, String idCommessa, char tipoRisorsa, char livelloRisorsa, String idRisorsa, String idAttivita, String idStabilimento, String errorCode) throws SQLException
  {
    iEsisteAnomalie = true;
    Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
    String codConfig = (configurazione == null) ? null : configurazione.getIdEsternoConfig();
    ReportAnomalieTrasfCosti reportAnm = (ReportAnomalieTrasfCosti)Factory.createObject(ReportAnomalieTrasfCosti.class);
    reportAnm.setBatchJobId(getBatchJob().getBatchJobId());
    reportAnm.setReportNr(iAvailableReport.getReportNr());
    reportAnm.setIdProgrStoric(iProgressivoStorico);
    reportAnm.setIdAzienda(getIdAzienda());
    reportAnm.setIdAmbiente(getIdAmbienteCosti());
    reportAnm.setIdStabilimento(idStabilimento /*getCommessa().getIdStabilimento()*/);
    reportAnm.setIdArticolo(idArticolo);
    reportAnm.setIdVersione(versione);
    reportAnm.setIdConfigurazione(config);
    reportAnm.setCodConfig(codConfig);
    reportAnm.setIdCommessa(idCommessa);
    reportAnm.setTipoRisorsa(tipoRisorsa);
    reportAnm.setLivelloRisorsa(livelloRisorsa);
    reportAnm.setIdRisorsa(idRisorsa);
    UserErrorMessage err = new UserErrorMessage(errorCode, new String[]
                                                {KeyHelper.formatKeyString(getAmbienteCostiKey()), getFormatedDate(getAmbienteCosti().getDataRiferimento())});
    reportAnm.setMessaggioErrore(err.getLongText());
    reportAnm.setSeverita(err.getSeverity());
    reportAnm.setIdArticoloPrd(getCommessa().getIdArticolo());
    reportAnm.setIdVersionePrd(getCommessa().getIdVersione());
    reportAnm.setIdConfigurazionePrd(getCommessa().getIdConfigurazione());
    reportAnm.setIdCommessaPrd(getCommessa().getIdCommessa());
    reportAnm.setIdAttivita(idAttivita);
    return saveReportObject(reportAnm);
  }

  //Fix 04171 End

  //Fix 8631 inizio

  /**
   * cloneFormula
   * @param formulaDaUtilizzOrig Formula
   * @return Formula
   */
  protected Formula cloneFormula(Formula formulaDaUtilizzOrig)
  {
    Formula formulaDaUtilizz = new Formula();
    try
    {
      formulaDaUtilizz.setEqual(formulaDaUtilizzOrig);
    }
    catch (CopyException e)
    {
    }
    return formulaDaUtilizz;
  }

  //Fix 8631 fine

  public String getAmbienteFromPrev(PreventivoCommessaVoce voce )
  {
    if(voce.getPrevComRiga().getCommessaPrincipale()!=null)
      return voce.getPrevComRiga().getTestata().getIdAmbiente();
    return null;
  }
  protected Costo getCostoPrev(PreventivoCommessaVoce voce ,char tipoRisorsa, char livelloRisorsa, String idRisorsa, String idStabilimento) throws SQLException
 {
   String idAmbiente =  getAmbienteFromPrev(voce );
   String where = CostoRisorsaTM.ID_AZIENDA + " = '" + getIdAzienda() + "'" +
     " AND " + CostoRisorsaTM.ID_AMBIENTE + " = '" + idAmbiente + "'" +
     " AND " + CostoRisorsaTM.R_STABILIMENTO + " = '" + idStabilimento + "'" +
     " AND " + CostoRisorsaTM.TIPOLOGIA + " = '" + Costo.RISORSA + "'" +
     " AND " + CostoRisorsaTM.R_TIPO_RISORSA + " = '" + tipoRisorsa + "'" +
     " AND " + CostoRisorsaTM.R_LIVELLO_RISORSA + " = '" + livelloRisorsa + "'" +
     " AND " + CostoRisorsaTM.R_RISORSA + " = '" + idRisorsa + "'";
   List costi = null;
   try
   {
     ConnectionManager.pushConnection(costiDescriptor);
     costi = CostoRisorsa.retrieveList(where, "", false);
     ConnectionManager.popConnection(costiDescriptor);
   }
   catch (ClassNotFoundException ex)
   {
     ex.printStackTrace(Trace.excStream); //...FIX 07430
   }
   catch (InstantiationException ex)
   {
     ex.printStackTrace(Trace.excStream); //...FIX 07430
   }
   catch (IllegalAccessException ex)
   {
     ex.printStackTrace(Trace.excStream); //...FIX 07430
   }

   if (costi.isEmpty())
   {
     return null;
   }
   return (CostoRisorsa)costi.get(0);
 }
// Fix 14964
 protected int addAnomaliePrev(PreventivoCommessaVoce voce,String idArticolo, Integer versione, Configurazione configurazione, String idCommessa, char tipoRisorsa, char livelloRisorsa, String idRisorsa, String idAttivita, String idStabilimento) throws SQLException
  {
    iEsisteAnomalie = true;
    Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
    String codConfig = (configurazione == null) ? null : configurazione.getIdEsternoConfig();
    ReportAnomalieTrasfCosti reportAnm = (ReportAnomalieTrasfCosti)Factory.createObject(ReportAnomalieTrasfCosti.class);
    reportAnm.setBatchJobId(getBatchJob().getBatchJobId());
    reportAnm.setReportNr(iAvailableReport.getReportNr());
    reportAnm.setIdProgrStoric(iProgressivoStorico);
    reportAnm.setIdAzienda(getIdAzienda());
    reportAnm.setIdAmbiente(getIdAmbienteCosti());
    reportAnm.setIdStabilimento(idStabilimento );
    reportAnm.setIdArticolo(idArticolo);
    reportAnm.setIdVersione(versione);
    reportAnm.setIdConfigurazione(config);
    reportAnm.setCodConfig(codConfig);
    reportAnm.setIdCommessa(idCommessa);
    reportAnm.setTipoRisorsa(tipoRisorsa);
    reportAnm.setLivelloRisorsa(livelloRisorsa);
    reportAnm.setIdRisorsa(idRisorsa);
    UserErrorMessage   err = new UserErrorMessage("THIP20T083", new String[]{this.getPreventivoCommessa().getCommessa().getAmbienteCommessa().getAmbienteCosti().getKey(),
                                                getFormatedDate(this.getPreventivoCommessa().getDataRiferimento())});

    reportAnm.setMessaggioErrore(err.getLongText());
    reportAnm.setSeverita(err.getSeverity());
    reportAnm.setIdArticoloPrd(voce.getPrevComRiga().getCommessa().getIdArticolo());
    reportAnm.setIdVersionePrd(voce.getPrevComRiga().getCommessa().getIdVersione());
    reportAnm.setIdConfigurazionePrd(voce.getPrevComRiga().getCommessa().getIdConfigurazione());
    reportAnm.setIdCommessaPrd(voce.getPrevComRiga().getIdCommessa());
    reportAnm.setIdAttivita(idAttivita);
    return saveReportObject(reportAnm);
  }

  protected CostiCommessaElem creaCostiCmmPrev(PreventivoCommessaRiga riga,Commessa commessa, Articolo articolo, Integer versione, Configurazione configurazione, String stabilimento, String attivita, BigDecimal qtaEff, String idUm, String idOperazione) throws SQLException
   {
     int result = ErrorCodes.NO_ROWS_UPDATED;
     Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
     String stb = (stabilimento == null || stabilimento.equals("")) ? commessa.getIdStabilimento() : stabilimento;

     CostiCommessaElem costoCmmElem = creaCostoCmmElemPrev(commessa,
       articolo,
       versione,
       config,
       stb,
       attivita,
       qtaEff,
       idUm,
       riga,
       idOperazione);
     costoCmmElem.iCalcoloImporto = false;
     ConnectionManager.pushConnection(costiDescriptor);
     costoCmmElem.impostaIdRigaElem();
     ConnectionManager.popConnection(costiDescriptor);
     int rc = saveCostiObject(costoCmmElem);
     rc = creaCostiDetPrev(articolo, commessa, qtaEff, riga, costoCmmElem);
     rc = trasferCostiVoce(riga,costoCmmElem);
     return costoCmmElem;
   }


   protected int addAnomaliePrev(PreventivoCommessaRiga riga,String idArticolo, Integer versione, Configurazione configurazione, String idCommessa, char tipoRisorsa, char livelloRisorsa, String idRisorsa, String idAttivita, String idStabilimento) throws SQLException
     {
       iEsisteAnomalie = true;
       Integer config = (configurazione == null) ? null : configurazione.getIdConfigurazione();
       String codConfig = (configurazione == null) ? null : configurazione.getIdEsternoConfig();
       ReportAnomalieTrasfCosti reportAnm = (ReportAnomalieTrasfCosti)Factory.createObject(ReportAnomalieTrasfCosti.class);
       reportAnm.setBatchJobId(getBatchJob().getBatchJobId());
       reportAnm.setReportNr(iAvailableReport.getReportNr());
       reportAnm.setIdProgrStoric(iProgressivoStorico);
       reportAnm.setIdAzienda(getIdAzienda());
       reportAnm.setIdAmbiente(getIdAmbienteCosti());
       reportAnm.setIdStabilimento(idStabilimento /*getCommessa().getIdStabilimento()*/);
       reportAnm.setIdArticolo(idArticolo);
       reportAnm.setIdVersione(versione);
       reportAnm.setIdConfigurazione(config);
       reportAnm.setCodConfig(codConfig);
       reportAnm.setIdCommessa(idCommessa);
       reportAnm.setTipoRisorsa(tipoRisorsa);
       reportAnm.setLivelloRisorsa(livelloRisorsa);
       reportAnm.setIdRisorsa(idRisorsa);
       UserErrorMessage   err = new UserErrorMessage("THIP20T083", new String[]{this.getPreventivoCommessa().getAmbienteCosti().getKey(),
                                                   getFormatedDate(this.getPreventivoCommessa().getDataRiferimento())});

       reportAnm.setMessaggioErrore(err.getLongText());
       reportAnm.setSeverita(err.getSeverity());
       reportAnm.setIdArticoloPrd(riga.getCommessa().getIdArticolo());
       reportAnm.setIdVersionePrd(riga.getCommessa().getIdVersione());
       reportAnm.setIdConfigurazionePrd(riga.getCommessa().getIdConfigurazione());
       reportAnm.setIdCommessaPrd(riga.getIdCommessa());
       reportAnm.setIdAttivita(idAttivita);
       return saveReportObject(reportAnm);
     }

     //Fix 21964 Inizio
     public ErrorMessage checkRighePreventivoCommessa(){
       if(getPreventivoCommessa() != null){
         if(getPreventivoCommessa().getRighe() != null && !getPreventivoCommessa().getRighe().isEmpty()){
           Iterator iteRighe = getPreventivoCommessa().getRighe().iterator();
           while (iteRighe.hasNext()){
             PreventivoCommessaRiga riga=(PreventivoCommessaRiga)iteRighe.next();
             if(riga.getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_COMMESSA || riga.getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_SOTTO_COMMESSA){
               if(riga.getCommessa() == null || riga.getCommessa().getStatoAvanzamento() != Commessa.STATO_AVANZAM__CONFERMATA ){
                 return new ErrorMessage("THIP40T377");
               }
             }
           }
         }
       }
       return null;
     }
     //Fix 21964 Fine

     //Fix  22273 Inizio
     protected BigDecimal convertiInValutaAziendale(BigDecimal valore, String valuta, java.sql.Date dataPrev)
     {
       ImportoInValutaEstera ive = (ImportoInValutaEstera)Factory.createObject(ImportoInValutaEstera.class);
       BigDecimal ret = valore;
       BigDecimal cambio = getFattorecambio(valuta, dataPrev);
       String idValutaAziendale = PersDatiGen.getCurrentPersDatiGen().getIdValutaPrimaria();
       if (valore != null && valore.compareTo(new BigDecimal(0)) != 0 &&
           valuta != null && !valuta.equals("") && !valuta.equals(idValutaAziendale) &&
           cambio != null && cambio.compareTo(new BigDecimal(0)) != 0)
       {
         ive.setFattCambioOper(cambio);
         ive.convertiEstPrim(valuta, valore, cambio);
         ret = ive.getImportaValPrim() != null ? ive.getImportaValPrim() : new BigDecimal("0");
         ret = ret.setScale(2, BigDecimal.ROUND_HALF_UP);
       }
       return ret;
     }

     public BigDecimal getFattorecambio(String idValuta, java.sql.Date data)
     {
       BigDecimal cambio = DocumentoOrdineTestata.recuperaCambio(idValuta, data);
       return cambio;
     }

     public boolean isValutaPrevAziendale(){
       boolean isValutaAziendale = true;
       String idValutaAziendale = PersDatiGen.getCurrentPersDatiGen().getIdValutaPrimaria();
       if(getPreventivoCommessa() != null && getPreventivoCommessa().getIdValuta() != null){
         if(idValutaAziendale!= null && !idValutaAziendale.equals(getPreventivoCommessa().getIdValuta())){
           isValutaAziendale = false;
         }
       }
       return isValutaAziendale;
     }
     //Fix 22273 Fine

     //Fix 22355 Inizio
     public ErrorMessage checkRighePreventivoVoce(){
       if(getPreventivoCommessa() != null){
         if(getPreventivoCommessa().getRighe() != null && !getPreventivoCommessa().getRighe().isEmpty()){
           Iterator iteRighe = getPreventivoCommessa().getRighe().iterator();
           while (iteRighe.hasNext()){
             PreventivoCommessaRiga riga=(PreventivoCommessaRiga)iteRighe.next();
             if(riga.getRighe() != null && !riga.getRighe().isEmpty()){
               Iterator itRigheVoce = riga.getRighe().iterator();
               while(itRigheVoce.hasNext()){
                 PreventivoCommessaVoce prevComVoce=(PreventivoCommessaVoce)itRigheVoce.next();
                 if(prevComVoce.getTipoRigav()==PreventivoCommessaVoce.TP_RIG_RISORSA){
                   if(prevComVoce.getRisorsa() != null && prevComVoce.getRisorsa().getIdArticoloServizio() == null){
                     return new ErrorMessage("THIP40T420");
                   }
                 }
               }
             }
           }
         }
       }
       return null;
     }
     //Fix 22355 Fine
     //34585 inizio
     public Integer getProgressivoStorico() {
    	 return iProgressivoStorico;
     }
     
     public void setTrasferimentoPerBudget(boolean trasferimentoPerBudget) {
    	 iTrasferimentoPerBudget = trasferimentoPerBudget;
     }
     
     public boolean isTrasferimentoPerBudget() {
    	 return iTrasferimentoPerBudget;
     }
     //34585 fine

 }



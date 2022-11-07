/*
 * @(#)PreventivoCommessaRiga.java
 */

/**
 * PreventivoCommessaRiga
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author MARZOUK FATTOUMA 21/10/2011 at 10:10:07
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 21/10/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 15848     05/03/2012    FM       Correzione
 * 20406     29/09/2014    TF       Modifico nel metodo copiaVoce
 * 22355     03/12/2015    AA       Aggiunto l'attributo iDelete
 * 27506     30/05/2018    DBot     Correzioni a preventivo
 * 29032	 25/03/2019	   RA		Revisione preventivi di commessa
 * 29166	 01/04/2019	   RA       Revisione preventivi di commessa
 * 29529     08/07/2019    RA		Revisione preventivi di commessa
 * 29642	 18/07/2019    RA		Revisione preventivi di commessa: gestione ProvenienzaPrz
 * 29672     23/07/2019    RA		Revisione preventivi di commessa
 * 29731	 20/08/2019	   RA       Revisione Preventivo commessa : varie modifiche
 * 29732	 29/08/2019    RA		Aggiunto metodo checkIdCapitoliPreventivo
 * 29882	 02/10/2019	   RA		Corretto metodo gestioneMarkupRighe()
 * 29959	 14/10/2019	   RA		Correto valorizzazione DataConsegnaRcs e DataConsegnaPrv
 * 30762     20/02/2020    DB       Riga duplicata in copia
 * 31091     15/04/2020    Mekki    Modificare il metodo saveOwnedObjects affinchè nel caso di isSalvaRighe uguale a false chiami direttamente il metodo save delle descrizioni in lingua
 * 31162     29/04/2020    Mekki    In copia non vengono ricalcolati i valori delle righe dettaglio
 * 31227     15/05/2020    Mekki    Velocizzare la cancellazione delle righe all'interno del preventivo
 * 31239	 20/05/2020	   RA		Modifica gestione DataConsegnaRcs e DataConsegnaPrv
 * 31310	 02/06/2020	   RA		Corretto cancellazione PreventivoCommessaRiga
 * 31451     25/06/2020    Mekki    Nel calcolo tenere in compto anche le eventuali righe commesse figlie e non solo i padri
 * 33022	 01/03/2021	   RA		Corretto copia preventivo
 * 33972     09/07/2021    RA		Valorizza IdCommessaAppartenenza e corretta problema di copia riga
 */
package it.thera.thip.base.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.collector.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;

import it.thera.thip.base.articolo.*;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.risorse.Risorsa;
import it.thera.thip.cs.DatiComuniEstesi;
import it.thera.thip.vendite.generaleVE.*;
import com.thera.thermfw.persist.ErrorCodes;

public class PreventivoCommessaRiga
  extends PreventivoCommessaRigaPO
{

  // TIPO RIGA
  public static final char TIPO_RIGA_COMMESSA = '1';
  public static final char TIPO_RIGA_SOTTO_COMMESSA = '2';

  // MODALITA GENERAZIONE OFFERTA
  public static final char RIGA_COMMESSA = '0';
  public static final char RIGA_COMMESSA_VOCE_1 = '1';
  public static final char RIGA_COMMESSA_VOCE_2 = '2';
  public static final char ESCLUSO_GEN_OFFERTA = '3';

  //attribute di serviizo
  public int iLivello = 0;
  private boolean iSalvaRighe = false;
  public BigDecimal iPercentuale = ZERO;

  //
  protected static final String SELECT_MAX_SEQUENZA_RIGHE_COM = "SELECT MAX(" + PreventivoCommessaRigaTM.SEQUENZA_RIGA + ")" +
    " FROM " + PreventivoCommessaRigaTM.TABLE_NAME +
    " WHERE " + PreventivoCommessaRigaTM.ID_AZIENDA + " = ?" +
    " AND " + PreventivoCommessaRigaTM.ID_ANNO_PREVC + " = ?" +
    " AND " + PreventivoCommessaRigaTM.ID_NUMERO_PREVC + " = ?";
  protected static CachedStatement cSelectMaxSequenzaRigheCom = new CachedStatement(SELECT_MAX_SEQUENZA_RIGHE_COM);

  protected static final String SELECT_MAX_ID_RIGHE_COM = "SELECT MAX(" + PreventivoCommessaRigaTM.ID_RIGAC_PRV + ")" +
    " FROM " + PreventivoCommessaRigaTM.TABLE_NAME +
    " WHERE " + PreventivoCommessaRigaTM.ID_AZIENDA + " = ?" +
    " AND " + PreventivoCommessaRigaTM.ID_ANNO_PREVC + " = ?" +
    " AND " + PreventivoCommessaRigaTM.ID_NUMERO_PREVC + " = ?";
  protected static CachedStatement cSelectMaxIdRigheCom = new CachedStatement(SELECT_MAX_ID_RIGHE_COM);

  protected static final String SELECT_MAX_SEQUENZA_RIGHE_SOTT_COM = "SELECT MAX(" + PreventivoCommessaRigaTM.SEQUENZA_RIGA + ")" +
    " FROM " + PreventivoCommessaRigaTM.TABLE_NAME +
    " WHERE " + PreventivoCommessaRigaTM.ID_AZIENDA + " = ?" +
    " AND " + PreventivoCommessaRigaTM.ID_ANNO_PREVC + " = ?" +
    " AND " + PreventivoCommessaRigaTM.ID_NUMERO_PREVC + " = ?" +
    " AND " + PreventivoCommessaRigaTM.R_RIGAC_PRV + " = ?";
  protected static CachedStatement cSelectMaxSequenzaRigheSottoCom = new CachedStatement(SELECT_MAX_SEQUENZA_RIGHE_SOTT_COM);

  protected String iDescrizioneArticolo;
  protected BigDecimal iVlrTotPrevCom = new BigDecimal(0);
  protected BigDecimal iCosTotPrevCom = new BigDecimal(0);
  protected BigDecimal iMdcTotPrevCom = new BigDecimal(0);
  protected String iIdCliente;
  public boolean iDelete = false;//Fix 22355
  //29529 inizio
  protected BigDecimal iOldMarkupArticolo = null;
  protected BigDecimal iOldMarkupUomo = null;
  protected BigDecimal iOldMarkupMacchina = null;
  protected boolean iDaSaveTestata = false;
  //29529 fine
  public boolean iDeleteFather = true;//Fix 31310
  public PreventivoCommessaRiga()
  {
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  /**
   * checkTestata
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/10/2011    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkTestata()
  {
    /**@todo*/
    return null;
  }

  /**
   * checkDelete
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/10/2011    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete()
  {
    /**@todo*/
    return null;
  }

  public String getTableNLSName()
  {
    return SystemParam.getSchema("THIP") + "PREV_COM_RIG_L";
  }

  public int save() throws SQLException
  {
     //System.out.println("SAV£:" + KeyHelper.formatKeyString(getKey()));	
    if (!onDB)
    {
      setIdRigacPrv(PreventivoCommessaTestata.getNumeroNuovaRiga(getTestata(), 1, PreventivoCommessaRiga.cSelectMaxIdRigheCom));
      if (getSequenzaRiga() == 0)
      {
        if (getSplRiga() == TIPO_RIGA_COMMESSA)
          setSequenzaRiga(PreventivoCommessaTestata.getNumeroNuovaRiga(getTestata(), 10, PreventivoCommessaRiga.cSelectMaxSequenzaRigheCom));
        else
          setSequenzaRiga(PreventivoCommessaTestata.getNumeroNuovaRigaSottoCom(getRigaAppartenenza(), 10, PreventivoCommessaRiga.cSelectMaxSequenzaRigheSottoCom));
      }
      completaBO();
    }
    //29529 inizio
  	if(getTestata() != null && getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
  		gestioneMarkupRighe();
  	}	
  	//29529 fine
    beforSave();
    int rc = (iGestoreCommenti == null) ? 0 : salvaCommenti();
    if (rc < 0)
      return rc;
    else
    {
      int rc1 = super.save();
      if (rc1 >= 0)
        rc = rc + rc1;
      else
        rc = rc1;
    }

    if (rc >= 0)
    {
       //Fix 27506 inizio
    	if (!this.getTestata().isSalvoDaSola()) { // fix 30762
    		 //Fix 31227 inizio
            //getTestata().ricalcolaValori();
            List righeDaRicalcolare = new ArrayList();
            righeDaRicalcolare.add(this);
            PreventivoCommessaRiga tmp = this;
            while (tmp.getRigaAppartenenza() != null) {
              righeDaRicalcolare.add(tmp.getRigaAppartenenza());
              tmp = tmp.getRigaAppartenenza();
            }
            cercaSottocomesseDaRic(this, righeDaRicalcolare); //Fix 31451
            getTestata().ricalcolaValori(righeDaRicalcolare, null);
            //Fix 31227 fine
    	}
       //  getTestata().setSalvaRighe(false);
       //  calcolaTotaleTestata(getTestata());
       //Fix 27506 fine
    }
    /*  if(getSplRiga() == TIPO_RIGA_SOTTO_COMMESSA )
     {
       try
       {
         if (getRigaAppartenenza() != null)
         {
           PreventivoCommessaRiga rigaApp = (PreventivoCommessaRiga) getRigaAppartenenza();
           rigaApp.setSalvaRighe(false);
           rigaApp.save();
         }
       }
       catch (SQLException ex)
       {
       }
     }*/

    return rc;
  }

  private void beforSave()
  {
    // fix 30762
	// vuol dire che sono in copia  
	//33022 inizio
	/*
	if (this.getOldRigaPrev()!=null) {
		// vuol dire che questa riga appartiene ad una delle righe cerco a quale
		if (this.getIdRigacPrvApp()>1) {
			Integer inte = new Integer(this.getIdRigacPrvApp());
			Iterator iter = this.getTestata().getRighe().iterator();
			while (iter.hasNext()) {
				PreventivoCommessaRiga rigaOld = (PreventivoCommessaRiga)iter.next();
				if (rigaOld.getOldRigaPrev().compareTo(inte)==0) {
					this.setIdRigacPrvApp(rigaOld.getIdRigacPrv());
					break;
				}	
				
			}
			
		}
	}
	*/
	//33022 fine
	// fine fix 30762  
	  
	  //calcolaImportiECCosti();//Fix 27506
    //updateDescrizione();//29529
	  //33972 inizio
	  if (getRigaAppartenenza() != null)
	      setIdCommessaAppartenenza(getRigaAppartenenza().getIdCommessa());
	  //33972 fine
  }
  
  //29529 inizio
  public void gestioneMarkupRighe(){
	  
	  if(!isDaSaveTestata() && getSottoCommesse() != null && !getSottoCommesse().isEmpty()){
		  Iterator iterRighe = getSottoCommesse().iterator();
		  while(iterRighe.hasNext()){
			  PreventivoCommessaRiga riga = (PreventivoCommessaRiga) iterRighe.next();
			  if((getMarkupArticolo().compareTo(iOldMarkupArticolo) != 0) || 
						 (getMarkupUomo().compareTo(iOldMarkupUomo) != 0) || 
						 (getMarkupMacchina().compareTo(iOldMarkupMacchina) != 0)){
				  riga.setMarkupArticolo(getMarkupArticolo());
				  riga.setMarkupUomo(getMarkupUomo());
				  riga.setMarkupMacchina(getMarkupMacchina());
				  try {
					  riga.getTestata().setPropagazioneRicalcoloValori(false);
					  riga.save();
				  } catch (SQLException e) {
					  e.printStackTrace(Trace.excStream);
				  }
			  }
		  }
	  }
	  
	  if(getRighe() != null && !getRighe().isEmpty()){
		  Iterator iterVoce = getRighe().iterator();
		  while(iterVoce.hasNext()){
			  PreventivoCommessaVoce voce = (PreventivoCommessaVoce) iterVoce.next();
			  //29882 inizio
			  voce.setPropagazioneRicalcoloValori(false);
			  setSalvaRighe(true);
			  //29882 fine
			  if(voce.getTipoRigav() != PreventivoCommessaVoce.TP_RIG_RISORSA){
				  if(getMarkupArticolo() != null && getMarkupArticolo().compareTo(iOldMarkupArticolo) != 0){
					  voce.setMarkup(getMarkupArticolo());
					  voce.setPrezzo(voce.calcoloPrezziDaMarkup(voce));
					  voce.setProvenienzaPrz(PreventivoCommessaVoce.PRV_PREZZO_MANUALE);//29642
					  //setSalvaRighe(true);//29882
					  //voce.setPropagazioneRicalcoloValori(false);//29882
				  }
			  }
			  else{
				  if(voce.getTipoRisorsa() == Risorsa.RISORSE_UMANE && getMarkupUomo() != null && getMarkupUomo().compareTo(iOldMarkupUomo) != 0){
					  voce.setMarkup(getMarkupUomo());
					  voce.setPrezzo(voce.calcoloPrezziDaMarkup(voce));
					  //setSalvaRighe(true);//29882
					  //voce.setPropagazioneRicalcoloValori(false);//29882
				  }
				  else if(voce.getTipoRisorsa() == Risorsa.MACCHINE && getMarkupMacchina() != null && getMarkupMacchina().compareTo(iOldMarkupMacchina) != 0){
					  voce.setMarkup(getMarkupMacchina());
					  voce.setPrezzo(voce.calcoloPrezziDaMarkup(voce));
					  //setSalvaRighe(true);//29882
					  //voce.setPropagazioneRicalcoloValori(false);//29882
				  } 
			  }
		  }
	  }
  }
  
  public boolean isDaSaveTestata(){
    return iDaSaveTestata;
  }
  
  public void setDaSaveTestata(boolean daSaveTestata){
	  iDaSaveTestata = daSaveTestata;
  }
  //29529 fine

  protected void calcolaImportiECCosti()
  {
    calcolaValoreLivello();
    calcolaValoreLivelloInferiore();
    calcolaValoreTotale();
  }

  /**
   * calcolaValoreLivelloTotale
   */
  protected void calcolaValoreTotale()
  {
    BigDecimal margineSottoCommesse = new BigDecimal(0);
    setVlrTotale(somma(getVlrLivello(), getVlrLivelloInf()));
    setCosTotale(somma(getCosLivello(), getCosLivelloInf()));
    margineSottoCommesse = sottrai(getVlrTotale(), getCosTotale());
    setMdcTotale(margineSottoCommesse);
  }

  /**
   * calcolaValoreLivelloInferiore
   */
  protected void calcolaValoreLivelloInferiore()
  {
     //Fix 27506 inizio
     calcolaValoreLivelloInferiore(getSottoCommesse());
     /*
    Iterator iteRighe = getSottoCommesse().iterator();
    BigDecimal valoreSottoCommesse = new BigDecimal(0);
    BigDecimal costoSottoCommesse = new BigDecimal(0);
    BigDecimal margineSottoCommesse = new BigDecimal(0);
    while (iteRighe.hasNext())
    {
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iteRighe.next();
      valoreSottoCommesse = somma(valoreSottoCommesse, riga.getVlrTotale());
      costoSottoCommesse = somma(costoSottoCommesse, riga.getCosTotale());
    }
    setVlrLivelloInf(valoreSottoCommesse);
    setCosLivelloInf(costoSottoCommesse);
    margineSottoCommesse = sottrai(valoreSottoCommesse, costoSottoCommesse);
    setMdcLivelloInf(margineSottoCommesse);
    */
     //Fix 27506 fine
  }

  /**
   * calcolaValoreLivello
   */
  protected void calcolaValoreLivello()
  {
     //Fix 27506 inizio
     calcolaValoreLivello(getRighe());
     /*
    Iterator iteVoce = getRighe().iterator();
    BigDecimal valoreLivello = new BigDecimal(0);
    BigDecimal costoLivello = new BigDecimal(0);
    BigDecimal margineLivello = new BigDecimal(0);
    while (iteVoce.hasNext())
    {
      PreventivoCommessaVoce voce = (PreventivoCommessaVoce)iteVoce.next();
      valoreLivello = somma(valoreLivello, voce.getVlrTotale());
      costoLivello = somma(costoLivello, voce.getCosTotale());
    }

    margineLivello = sottrai(valoreLivello, costoLivello);
    setVlrLivello(valoreLivello);
    setCosLivello(costoLivello);
    setMdcLivello(margineLivello);
    */
     //Fix 27506 fine
  }

  //Fix 27506 inizio
  protected void calcolaValoreLivelloInferiore(List righe)
  {
    Iterator iteRighe = righe.iterator();
    BigDecimal valoreSottoCommesse = new BigDecimal(0);
    BigDecimal costoSottoCommesse = new BigDecimal(0);
    BigDecimal margineSottoCommesse = new BigDecimal(0);
    while (iteRighe.hasNext())
    {
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iteRighe.next();
      valoreSottoCommesse = somma(valoreSottoCommesse, riga.getVlrTotale());
      costoSottoCommesse = somma(costoSottoCommesse, riga.getCosTotale());
    }
    setVlrLivelloInf(valoreSottoCommesse);
    setCosLivelloInf(costoSottoCommesse);
    margineSottoCommesse = sottrai(valoreSottoCommesse, costoSottoCommesse);
    setMdcLivelloInf(margineSottoCommesse);
  }

  protected void calcolaValoreLivello(List voci)
  {
    Iterator iteVoce = voci.iterator();
    BigDecimal valoreLivello = new BigDecimal(0);
    BigDecimal costoLivello = new BigDecimal(0);
    BigDecimal margineLivello = new BigDecimal(0);
    while (iteVoce.hasNext())
    {
      PreventivoCommessaVoce voce = (PreventivoCommessaVoce)iteVoce.next();
      valoreLivello = somma(valoreLivello, voce.getVlrTotale());
      costoLivello = somma(costoLivello, voce.getCosTotale());
    }

    margineLivello = sottrai(valoreLivello, costoLivello);
    setVlrLivello(valoreLivello);
    setCosLivello(costoLivello);
    setMdcLivello(margineLivello);
  }
  //Fix 27506 fine
  
  protected BigDecimal sottrai(BigDecimal valore1, BigDecimal valore2)
  {
    if (valore1 == null)
      valore1 = new BigDecimal(0);
    if (valore2 == null)
      valore2 = new BigDecimal(0);
    return valore1.subtract(valore2);

  }

  /**
   * calcolaTotaleRigaTestata
   */


  /**
   * calcolaTotaleTestata
   *
   * @param preventivoCommessaTestata PreventivoCommessaTestata
   */
  public void calcolaTotaleTestata(PreventivoCommessaTestata preventivoCommessaTestata)
  {
     System.err.println("Voce.salvaTestata() Metodo non più operativo");

     /*
    Iterator ite = preventivoCommessaTestata.getRighe().iterator();
    BigDecimal valoreTotale = new BigDecimal(0);
    BigDecimal costoTotale = new BigDecimal(0);
    BigDecimal margineTotale = new BigDecimal(0);

    while (ite.hasNext())
    {
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)ite.next();
      valoreTotale = somma(valoreTotale, riga.getVlrLivello());
      costoTotale = somma(costoTotale, riga.getCosLivello());
      margineTotale = sottrai(valoreTotale, costoTotale);
    }
    preventivoCommessaTestata.setVlrTotPrevc(valoreTotale);
    preventivoCommessaTestata.setCosTotPrevc(costoTotale);
//    if (!preventivoCommessaTestata.getRighe().isEmpty())
//      margineTotale = margineTotale.divide(new BigDecimal(preventivoCommessaTestata.getRighe().size()), 6, BigDecimal.ROUND_HALF_UP);
  
    preventivoCommessaTestata.setMdcTotPrevc(margineTotale);
    salvaTestata(preventivoCommessaTestata);
    */
  }

  /**
   * salvaTestata
   *
   * @param preventivoCommessaTestata PreventivoCommessaTestata
   */
  public void salvaTestata(PreventivoCommessaTestata preventivoCommessaTestata)
  {
     System.err.println("Riga.salvaTestata() Metodo non più operativo");
/*     
    try
    {
      preventivoCommessaTestata.setSalvaRighe(false);
      preventivoCommessaTestata.save();
    }
    catch (SQLException ex)
    {

    }
    */
  }

  /**
   * somma
   *
   * @param valoreTotale BigDecimal
   * @param bigDecimal BigDecimal
   */
  private BigDecimal somma(BigDecimal valore1, BigDecimal valore2)
  {
    if (valore1 == null)
      return valore2 == null ? new BigDecimal(0) : valore2;
    else
    if (valore2 == null)
      return valore1;
    return valore1.add(valore2);
  }

  public java.util.Vector checkAll(BaseComponentsCollection components)
  {
    components.getGroup(BaseValidationGroup.KEY_VALIDATION_GROUP).setCheckMode(BaseBOComponentManager.CHECK_NEVER);
    components.getComponent("Descrizione.Descrizione").setValue(("Descrizione"));
    components.getComponent("Descrizione.DescrizioneRidotta").setValue(("Descrizione"));
    //updateDescrizione();29529
    Vector errors = super.checkAll(components);
    return errors;
  }

  protected void updateDescrizione()
  {
    if (getDescrizioneArticolo() != null)
    {
      getDescrizione().setDescrizione(getDescrizioneArticolo());
      if (getDescrizioneArticolo().length() > 15)
        getDescrizione().setDescrizioneRidotta(getDescrizioneArticolo().substring(0, 14));
      else
        getDescrizione().setDescrizioneRidotta(getDescrizioneArticolo());
    }

  }

  public void setDescrizioneArticolo(String iDescrizioneArticolo)
  {
    this.iDescrizioneArticolo = iDescrizioneArticolo;
    setDirty();
  }

  public String getDescrizioneArticolo()
  {
    return iDescrizioneArticolo;
  }

  public ErrorMessage checkIdCommessa()
  {
    if (getSplRiga() == TIPO_RIGA_SOTTO_COMMESSA && this.getIdCommessa() != null)
    {
      if (this.getCommessa().getIdCommessaAppartenenza() == null ||
          (!this.getCommessa().getIdCommessaAppartenenza().equals(getIdCommessaAppartenenza())))
        return new ErrorMessage("THIP40T173");
    }
    return null;
  }
  
  //29732 inizio
  public ErrorMessage checkIdCapitoliPreventivo(){
	  if((getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_SOTTO_COMMESSA) && (getCapitoliPreventivo() != null) && isCapitoliPrevUtilizzata()){
		  return new ErrorMessage("THIP40T659");
	  }
	  return null;
  }
  
  public boolean isCapitoliPrevUtilizzata(){
	  boolean ret = false;
	  PreventivoCommessaRiga rigaCommessaPadre = getRigaCommessaPadre(this);
	  if(rigaCommessaPadre != null){
		  List sottoCommesseList = rigaCommessaPadre.getSottoCommesse();
		  if(sottoCommesseList != null && !sottoCommesseList.isEmpty()){
			  Iterator iterSottoCommesse = sottoCommesseList.iterator();
			  while(iterSottoCommesse.hasNext() && !ret){
				  PreventivoCommessaRiga rigaSC = (PreventivoCommessaRiga)iterSottoCommesse.next();
				  if(!rigaSC.getKey().equals(getKey()) && rigaSC.getIdCapitoliPreventivo() != null && !rigaSC.getIdCapitoliPreventivo().equals("") && rigaSC.getIdCapitoliPreventivo().equals(getIdCapitoliPreventivo())){
					  ret = true;  
				  }
				  else{
					  ret = isCapitoliPrevUtilizzata(rigaSC);
				  }
			  }
		  }
	  }
	  return ret;
  }
  
  public boolean isCapitoliPrevUtilizzata(PreventivoCommessaRiga rigaSC){
	  boolean ret = false;
	  List sottoCommesseList = rigaSC.getSottoCommesse();
	  if(sottoCommesseList != null && !sottoCommesseList.isEmpty()){
		  Iterator iterSottoCommesse = sottoCommesseList.iterator();
		  while(iterSottoCommesse.hasNext() && !ret){
			  PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iterSottoCommesse.next();
			  if(!riga.getKey().equals(getKey()) && riga.getIdCapitoliPreventivo() != null && !riga.getIdCapitoliPreventivo().equals("") && riga.getIdCapitoliPreventivo().equals(getIdCapitoliPreventivo())){
				  ret = true;  
			  }
			  else{
				  ret = isCapitoliPrevUtilizzata(riga);
			  }
		  }
	  }
	  return ret;
  }
  
  public PreventivoCommessaRiga getRigaCommessaPadre(PreventivoCommessaRiga riga){
	  if(riga.getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_COMMESSA){
		  return riga;
	  }
	  else{
		  PreventivoCommessaRiga rigaApp = riga.getRigaAppartenenza();
		  if(rigaApp != null){
			  if(rigaApp.getSplRiga() != PreventivoCommessaRiga.TIPO_RIGA_COMMESSA){
				  return getRigaCommessaPadre(rigaApp);
			  }
			  else{
				  return rigaApp;
			  }  
		  }		  
	  }
	  return null;
  }
  //29732 fine

  public void completaBO()
  {
    if (getTestata() != null && getTestata().getIdCommessa() != null)
      setIdCommessaPrincipale(getTestata().getIdCommessa());
  }

  public boolean initializeOwnedObjects(boolean result)
  {
    if (!isOnDB())
      completaBO();
    result = super.initializeOwnedObjects(result);
    if (isOnDB() && getDescrizione() != null)
      setDescrizioneArticolo(getDescrizione().getDescrizione());
    //29529 inizio
    iOldMarkupArticolo = getMarkupArticolo();
    iOldMarkupUomo = getMarkupUomo();
    iOldMarkupMacchina = getMarkupMacchina();    
    //29529 fine
    return result;
  }

  public static synchronized int getNumeroNuovaVoce(PreventivoCommessaRiga riga, int passo, CachedStatement cSelectMaxNumeroRiga)
  {
    try
    {
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(cSelectMaxNumeroRiga.getStatement(), 1, riga.getIdAzienda());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 2, riga.getIdAnnoPrevc());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 3, riga.getIdNumeroPrevc());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 4, String.valueOf(riga.getIdRigacPrv()));
      ResultSet rs = cSelectMaxNumeroRiga.executeQuery();
      int ret = (rs.next()) ? rs.getInt(1) + passo : passo;
      rs.close();

      return ret;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return 0;
    }
  }

  protected CommentHandler getCommentiParteIntestatario()
  {
    CommentHandler ret = null;
    ArticoloIntestatario articoloIntestatario = getArticoloIntestatario();
    if (articoloIntestatario != null)
      ret = ((ArticoloCliente)articoloIntestatario).getCommentHandler();
    return ret;
  }

  public ArticoloIntestatario getArticoloIntestatario()
  {
    if (iArticoloIntestatario == null || isArticoloIntestatarioCambiato())
    {
      iArticoloIntestatario = recuperaArticoloIntestatario();
    }
    return iArticoloIntestatario;
  }

  protected boolean isArticoloIntestatarioCambiato()
  {
    boolean ret = false;

    if (iArticoloIntestatario != null)
    {
      if (iArticoloIntestatario.getIdConfigurazione() != null)
      {
        ret = !(iArticoloIntestatario.getArticolo().equals(getArticolo()) && iArticoloIntestatario.getIdConfigurazione().equals(getIdConfigurazione()));
      }
      else
      {
        ret = !(iArticoloIntestatario.getArticolo().equals(getArticolo()) && (getIdConfigurazione() == null));
      }
    }
    return ret;
  }

  public ArticoloIntestatario recuperaArticoloIntestatario()
  {
    return recuperaArticoloIntestatario(false);
  }

  public ArticoloIntestatario recuperaArticoloIntestatario(boolean creaOggetto)
  {
    if (getTestata().getIdCliente() != null)
    {
      String idAzienda = Azienda.getAziendaCorrente();
      String idCliente = getIdIntestatario();
      Articolo articolo = getArticolo();
      Integer idConfigurazione = getIdConfigurazione();
      return recuperaArticoloCliente(idAzienda, idCliente, articolo, idConfigurazione, creaOggetto);
    }
    return null;
  }

  protected static ArticoloCliente recuperaArticoloCliente(String idAzienda, String idCliente, Articolo articolo, Integer idConfigurazione, boolean creaOggetto)
  {

    ArticoloCliente ret = null;
    try
    {
      if (articolo != null)
      {

        ret = ArticoloCliente.getArticoloCliente(idAzienda, idCliente, articolo.getIdArticolo(), idConfigurazione);
        if (ret == null)
        {
          if (creaOggetto)
          {
            PersDatiVen pdv = PersDatiVen.getCurrentPersDatiVen();

            if (pdv.getAprAutArtCli())
            {
              ret = (ArticoloCliente)Factory.createObject(ArticoloCliente.class);
              ret.setArticolo(articolo);
              ret.setIdCliente(idCliente);
              ret.setIdConfigurazione(idConfigurazione);
              ret.getDescrizioneEst().setDescrizione(articolo.getDescrizioneArticoloNLS().getDescrizione());
              ret.getDescrizioneEst().setDescrizioneRidotta(articolo.getDescrizioneArticoloNLS().getDescrizioneRidotta());
              ret.getDescrizioneEst().setDescrizioneEstesa(articolo.getDescrizioneArticoloNLS().getDescrizioneEstesa());

              ret.setUMPrmVendita(articolo.getUMDefaultVendita());

            }
          }
        }

      }
    }
    catch (SQLException e)
    {
      Trace.println("Eccezione nel metodo recuperaArticoloCliente() della classe OrdineVenditaRiga : " + e.getMessage());
      e.printStackTrace(Trace.excStream);
    }
    return ret;
  }

  protected List getCommentiDaCopiare()
  {
    List ret = new ArrayList();

    CommentHandler ch = getCommentiIntestatario();
    if (ch != null)
    {
      ret.add(ch);
    }
    ch = getCommentiParteIntestatario();
    if (ch != null)
    {
      ret.add(ch);
    }
    return ret;
  }

  protected CommentHandler getCommentiIntestatario()
  {
    if (getArticolo() != null && getArticolo().getCommentHandlerManager() != null)
      return getArticolo().getCommentHandlerManager().getObject();
    return null;
  }

  protected void copiaCommenti() throws SQLException
  {
    Task t = new Task();
    Entity e = getEntity();
    Task task = null;

    Iterator i = e.getTasks().iterator();
    while (i.hasNext())
    {
      task = (Task)i.next();
      if (task.getId().equals("NEW"))
        break;
    }

    CommentService commSrv = new CommentService();
    try
    {
      List listaCH = getCommentiDaCopiare();
      Iterator iter = listaCH.iterator();
      while (iter.hasNext())
      {
        CommentHandler ch = (CommentHandler)iter.next();
        CommentService.commentUseManagementGeneral(
          ch, getCommenti(), e, task
          );
      }
    }
    catch (Exception ex)
    {
      Trace.println("Eccezione nel metodo copiaCommenti() della classe " + getClass().getName() + ": " + ex.getMessage());
      ex.printStackTrace(Trace.excStream);
    }
  }

  protected Entity getEntity()
  {
    Entity ret = null;
    try
    {
      PreventivoCommessaTestata prev = (PreventivoCommessaTestata)this.getTestata();
      ret = prev.getEntity();
    }
    catch (Exception ex)
    {
      Trace.println("Eccezione nel metodo getEntity() della classe " + getClass().getName() + ": " + ex.getMessage());
      ex.printStackTrace(Trace.excStream);
    }
    return ret;
  }

  protected int salvaCommenti() throws SQLException
  {
    int rcCommentHandler = iGestoreCommenti.save();
    int rc = 0;
    if (rcCommentHandler >= ErrorCodes.NO_ROWS_UPDATED)
    {
      if (!isOnDB())
      {
        copiaCommenti();
        rc = iGestoreCommenti.save();
      }

      if (rc >= ErrorCodes.NO_ROWS_UPDATED)
        rcCommentHandler += rc;
      else
        rcCommentHandler = rc;
    }
    return rcCommentHandler;
  }

  public void completaDateConsegna()
  {
	//if (getTestata() != null)//29959
	if (getTestata() != null && getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_COMMESSA)//29959
    {
	  //31239 inizio
	  /*
      if (getTestata().getDataPrevc() != null)
        completaDateConsegna(getTestata().getDataPrevc());
      */
	  if(getTestata().getDataConsegRcs() != null)
		  setDataConsegRcs(getTestata().getDataConsegRcs());
	  if(getTestata().getDataConsegPrv() != null)
		  setDataConsegPrv(getTestata().getDataConsegPrv());	  
      //31239 fine
    }
    
	//29959 inizio
    if(getRigaAppartenenza() != null && getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_SOTTO_COMMESSA)
    {
    	if (getRigaAppartenenza().getDataConsegRcs() != null){
    		//completaDateConsegnaRcs(getRigaAppartenenza().getDataConsegRcs());//31239
    		setDataConsegRcs(getRigaAppartenenza().getDataConsegRcs());//31239
    	}
    	if (getRigaAppartenenza().getDataConsegPrv() != null){
    		//completaDateConsegnaPrv(getRigaAppartenenza().getDataConsegPrv());//31239
    		setDataConsegPrv(getRigaAppartenenza().getDataConsegPrv());//31239
    	}
    }
    //29959 fine
    calcolaSettimane();//31239
  }

  //29959 inizio
  public void completaDateConsegnaRcs(java.sql.Date data)
  {
    int minGgEvas = getTempoMinimoEvasione();
    java.sql.Date nuovaData = TimeUtils.addDays(data, minGgEvas);
    setDataConsegRcs(nuovaData);
    calcolaSettimane();
  }
  
  public void completaDateConsegnaPrv(java.sql.Date data)
  {
    int minGgEvas = getTempoMinimoEvasione();
    java.sql.Date nuovaData = TimeUtils.addDays(data, minGgEvas);
    setDataConsegPrv(nuovaData);
    calcolaSettimane();
  }
  //29959 fine
  
  public void completaDateConsegna(java.sql.Date dataDocumento)
  {
    int minGgEvas = getTempoMinimoEvasione();
    java.sql.Date nuovaData = TimeUtils.addDays(dataDocumento, minGgEvas);
    setDataConsegRcs(nuovaData);
    setDataConsegPrv(nuovaData);
    calcolaSettimane();
  }

  public int getTempoMinimoEvasione()
  {
    return PersDatiVen.getCurrentPersDatiVen().getTempoMinEvasione();
  }

  protected void calcolaSettimane()
  {
    java.sql.Date dataRichiesta = this.getDataConsegRcs();
    if (dataRichiesta != null)
    {
      int[] datiSettConsegnaRichiesta = TimeUtils.getISOWeek(dataRichiesta);
      String sett = getSettimanaFormattata(datiSettConsegnaRichiesta[0], datiSettConsegnaRichiesta[1]);
      this.setSettConsegRcs(sett);
    }

    java.sql.Date dataPrevista = this.getDataConsegPrv();
    if (dataPrevista != null)
    {
      int[] datiSettConsegnaPrevista = TimeUtils.getISOWeek(dataPrevista);
      String sett = getSettimanaFormattata(datiSettConsegnaPrevista[0], datiSettConsegnaPrevista[1]);
      this.setSettConsegPrv(sett);
    }

  }

  public static String getSettimanaFormattata(int anno, int settimana)
  {
    if (settimana < 10)
    {
      return anno + "-0" + settimana;
    }
    else
    {
      return anno + "-" + settimana;
    }
  }

  public List getListaUMRiferimento()
  {
    Articolo articolo = getArticolo();
    if (articolo != null)
    {
      List list = new ArrayList(articolo.getArticoloDatiVendita().getForcedUMSecondarie());
      if (!DocumentoBaseRiga.isUMNellaLista(this.getUmPrmMag(), list))
      {
        list.add(this.getUmPrmMag());
      }
      return list;
    }
    else
    {
      return new ArrayList();
    }
  }

  public ErrorMessage checkQtaUmPrm()
  {
    BigDecimal qta = getQtaUmPrm();
    if (qta == null || qta.compareTo(new BigDecimal(0)) == 0)
      return new ErrorMessage("THIP40T170");
    return null;
  }

  public ErrorMessage checkIdUmPrmMag()
  {
    String idUmPrmMag = getIdUmPrmMag();
    if (idUmPrmMag == null)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  public void initSequenza(PreventivoCommessaRiga rigaApp)
  {
    setSequenzaRiga(PreventivoCommessaTestata.getNumeroNuovaRigaSottoCom(rigaApp, 10, PreventivoCommessaRiga.cSelectMaxSequenzaRigheSottoCom));
  }

  public void initSequenza(String keyTestata)
  {
    setSequenzaRiga(PreventivoCommessaTestata.getNumeroNuovaRiga(keyTestata, 10, PreventivoCommessaRiga.cSelectMaxSequenzaRigheCom));
  }

  // attributo di servizo
  public void setLivello(int livello)
  {
    this.iLivello = livello;
  }

  public int getLivello()
  {
	//29166 inizio
	// return this.iLivello;  
	return calcolaLivello(this, 1);
	//29166 fine   
  }

  public List getSottoCommesse()
  {
	  //31310 inizio
	  /*
	  String where = PreventivoCommessaRigaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " +
      PreventivoCommessaRigaTM.ID_NUMERO_PREVC + "='" + getIdNumeroPrevc() + "' AND " +
      PreventivoCommessaRigaTM.ID_ANNO_PREVC + "='" + getIdAnnoPrevc() + "' AND " +
      PreventivoCommessaRigaTM.R_RIGAC_PRV + "=" + getIdRigacPrv() ;//+ " AND " +//29529 
      //PreventivoCommessaRigaTM.R_COMMESSA_APP + "='" + getIdCommessa() + "' ";//29166//29529
    try
    {
      return PreventivoCommessaRiga.retrieveList(where, PreventivoCommessaRigaTM.SEQUENZA_RIGA, true);
    }
    catch (Exception ex)
    {
      return null;
    }*/
	  return super.getSottoCommesse();
    //31310 fine
  }
  
  //29166 inizio
  public List getArticoli(){
	  String where = PreventivoCommessaVoceTM.ID_AZIENDA + "='"  + getIdAzienda() + "' AND "+
			  PreventivoCommessaVoceTM.ID_NUMERO_PREVC + "='" + getIdNumeroPrevc() + "' AND " +
			  PreventivoCommessaVoceTM.ID_ANNO_PREVC + "='" + getIdAnnoPrevc() + "' AND " +
			  PreventivoCommessaVoceTM.ID_RIGAC_PRV + "=" + getIdRigacPrv() + " AND " + 
			  PreventivoCommessaVoceTM.TIPO_RIGAV + "='" + PreventivoCommessaVoce.TP_RIG_ARTICOLO + "'";
	    try
	    {
	      return PreventivoCommessaVoce.retrieveList(where, PreventivoCommessaRigaTM.SEQUENZA_RIGA, true);
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace(Trace.excStream);;
	    }	  
	  return null;
  }
  
  public List getRisorse(){
	  String where = PreventivoCommessaVoceTM.ID_AZIENDA + "='"  + getIdAzienda() + "' AND "+
			  PreventivoCommessaVoceTM.ID_NUMERO_PREVC + "='" + getIdNumeroPrevc() + "' AND " +
			  PreventivoCommessaVoceTM.ID_ANNO_PREVC + "='" + getIdAnnoPrevc() + "' AND " +
			  PreventivoCommessaVoceTM.ID_RIGAC_PRV + "=" + getIdRigacPrv() + " AND " + 
			  PreventivoCommessaVoceTM.TIPO_RIGAV + "='" + PreventivoCommessaVoce.TP_RIG_RISORSA + "'"
			  + " AND " + PreventivoCommessaVoceTM.ID_SUB_RIGAV_PRV + " = 0";//29731
	    try
	    {
	      return PreventivoCommessaVoce.retrieveList(where, PreventivoCommessaRigaTM.SEQUENZA_RIGA, true);
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace(Trace.excStream);;
	    }	  
	  return null;
  }

  public List getVoci(){
	  String where = PreventivoCommessaVoceTM.ID_AZIENDA + "='"  + getIdAzienda() + "' AND "+
			  PreventivoCommessaVoceTM.ID_NUMERO_PREVC + "='" + getIdNumeroPrevc() + "' AND " +
			  PreventivoCommessaVoceTM.ID_ANNO_PREVC + "='" + getIdAnnoPrevc() + "' AND " +
			  PreventivoCommessaVoceTM.ID_RIGAC_PRV + "=" + getIdRigacPrv() + " AND " + 
			  PreventivoCommessaVoceTM.TIPO_RIGAV + "='" + PreventivoCommessaVoce.TP_RIG_VOCE + "' AND " + 
			  PreventivoCommessaVoceTM.ID_SUB_RIGAV_PRV + " = 0 ";
	    try
	    {
	      return PreventivoCommessaVoce.retrieveList(where, PreventivoCommessaRigaTM.SEQUENZA_RIGA, true);
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace(Trace.excStream);;
	    }	  
	  return null;
  }
  
  public String getKeyForTree() {
	  if(getCommessa() != null)
		  return getIdCommessa();
	  else if(getArticolo() != null)
		  return getIdArticolo();
	  else
		  return getDescrizione().getDescrizione();
  }
  
  public String getDescrizioneForTree(){
	  //Fix 31091 blocco commentato
	  /*String descrizione = "";
	    if(getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_COMMESSA){
	      if(getCommessa() != null)
	        descrizione = getNotNullDescrizione(getCommessa());
	      else
	        descrizione = getDescrizioneArticolo();
	    }
	    else if(getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_SOTTO_COMMESSA){
	      if(getCommessa() != null)
	        descrizione = getNotNullDescrizione(getCommessa());
	      else
	        descrizione = getDescrizioneArticolo();
	    }
	    return descrizione;*/
	   return getDescrizione().getDescrizione(); //Fix 31091
  }
  
  public String getNotNullDescrizione(Commessa commessa) {
    if (commessa == null)
      return "";
    return commessa.getDescrizione().getDescrizione();
  }
  
  public static final String RISORSE = "it.thera.thip.base.commessa.resources.PreventivoCommessaRiga";
  public static String TIPO_COMMESSA = ResourceLoader.getString(RISORSE, "TipoCommessa");
  public static String TIPO_SOTTO_COMMESSA = ResourceLoader.getString(RISORSE, "TipoSottoCommessa");
  
  public String getTipoRigaForTree()  {
    if (getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_COMMESSA)
      return TIPO_COMMESSA;

    return TIPO_SOTTO_COMMESSA;
  }
  
  public String getIdComponCostoForTree(){
	  return "";
  }
  
  public String getIdUmVenForTree() {
	  return "";
  }
  
  public BigDecimal getQtaPrvUmPrmForTree() {
	  return null;
  }
  
  public BigDecimal getPrezzoTotaleForTree() {
	  return null;
  }
  
  public BigDecimal getCostoRiferForTree() {
	  return null;
  }
  
  public BigDecimal getMarckupForTree(){
	  return null;
  }
  
  public BigDecimal getVlrLivelloForTree() {
	  return getVlrLivello();
  }
	  
  public BigDecimal getCosLivelloForTree() {
	  return getCosLivello();
  }
	  
  public BigDecimal getMdcLivelloForTree() {
	  return getMdcLivello();
  }
  
  public BigDecimal getVlrTotaleForTree() {
	  return getVlrTotale();	  
  }
  
  public BigDecimal getCosTotaleForTree() {
	  return getCosTotale();
  }
	  
  public BigDecimal getMdcTotaleForTree() {
	  return getMdcTotale();
  }
  
  public BigDecimal getPercentualeForTree(){
	  //29672 inizio
	  if(getDatiComuniEstesi().getStato() != DatiComuniEstesi.VALIDO && getPercentuale().compareTo(ZERO) == 0){
		  return null;
	  }
	  //29672 fine
	  return getPercentuale();
  }
  //29166 fine
  
  //29731 inizio
  public String getDescrArticoloForTree(){
	  if(getArticolo() != null){
		  return getArticolo().getDescrizioneArticoloNLS().getDescrizione();
	  }
	  return "";
  }
  
  public String getDescrCommessaForTree(){
	  if(getCommessa() != null){
		  return getCommessa().getDescrizione().getDescrizione();
	  }
	  return "";
  }
  //29731 fine

  /**
   * getRigaPrincipaleKey
   *
   * @return String
   */
  public int getRigaPrincipaleKey(PreventivoCommessaRiga riga)
  {
    int idRigacPrv = 0;
    if (getSplRiga() == TIPO_RIGA_SOTTO_COMMESSA)
    {
      if (riga.getRigaAppartenenza() != null)
        idRigacPrv = getRigaPrincipaleKey((PreventivoCommessaRiga)riga.getRigaAppartenenza());
      else
        idRigacPrv = riga.getIdRigacPrv();
    }
    return idRigacPrv;
  }

  /**
   * calcolaLivello
   *
   * @return int
   */
  public int calcolaLivello(PreventivoCommessaRiga riga, int ret)
  {
    if (riga.getRigaAppartenenza() != null)
    {
      ret = ret + calcolaLivello(riga.getRigaAppartenenza(), ret);
    }
    return ret;
  }

  protected boolean verificaStrutturaCommessa(Commessa commessa)
  {
    if (commessa != null)
    {
      if (commessa.getIdArticolo() == null || commessa.getIdArticolo().equals(""))
        return false;
      else
      {
        Iterator ite = commessa.getSottocommesse().iterator();
        while (ite.hasNext())
        {
          if (!verificaStrutturaCommessa((Commessa)ite.next()))
            return false;
        }
        return true;
      }
    }
    return false;

  }

  /**
   * caricaStrutturaCommessa
   *
   * @return boolean
   */
  public boolean caricaStrutturaCommessa()
  {
    if (verificaStrutturaCommessa(getCommessa()))
    {
      if (getIdCommessa() != null && !getIdCommessa().equals(""))
      {
        if (!getCommessa().getSottocommesse().isEmpty())
          if (getSottoCommesse().isEmpty())
            caricaStrutturaCommessa(true);
          else
          {
            Map sottoCommessaCorrente = getSottoCommesseFromPreventivo(getSottoCommesse());
            verificaStrutturaCommessa(getCommessa(), this, sottoCommessaCorrente);
          }
        return true;
      }
      else
        return false;
    }
    return false;
  }

  /**
   * verificaStrutturaCommessa
   */
  public void caricaStrutturaCommessa(boolean flag)
  {
    Iterator ite = getCommessa().getSottocommesse().iterator();
    while (ite.hasNext())
    {
      Commessa sottoCom = (Commessa)ite.next();
      if (!esistenoRigaCommessa(sottoCom, getSottoCommesse()))
        generaRigheCommesse(sottoCom);
    }
  }

  /**
   * esistenoRigaCommessa
   *
   * @param sottoCom Commessa
   * @param list List
   * @return boolean
   */
  private boolean esistenoRigaCommessa(Commessa sottoCom, List list)
  {
    return false;
  }

  public int generaRigheCommesse(Commessa commessa)
  {
    if (getIdCommessa() != null && !getIdCommessa().equals(""))
    {
      return generaRighe(commessa, this);
    }
    return 0;

  }

  public int generaRighe(Commessa commessa, PreventivoCommessaRiga rigaApp)
  {
    if (!commessa.getSottocommesse().isEmpty())
    {
      Iterator iteCommesse = commessa.getSottocommesse().iterator();
      PreventivoCommessaRiga rigaAppObj = generaRigaCommessa(commessa, rigaApp);
      while (iteCommesse.hasNext())
      {
        generaRighe((Commessa)iteCommesse.next(), rigaAppObj);
      }
    }
    else
    {
      generaRigaCommessa(commessa, rigaApp);
    }
    return 0;
  }

  public PreventivoCommessaRiga generaRigaCommessa(Commessa commessa, PreventivoCommessaRiga rigaApp)
  {
    PreventivoCommessaRiga riga = (PreventivoCommessaRiga)Factory.createObject(PreventivoCommessaRiga.class);
    riga.setTestata(getTestata());
    riga.setCommessa(commessa);
    riga.setSequenzaRiga(PreventivoCommessaTestata.getNumeroNuovaRigaSottoCom(rigaApp, 10, PreventivoCommessaRiga.cSelectMaxSequenzaRigheSottoCom));
    riga.setCommessaPrincipale(commessa.getCommessaPrincipale());
    riga.setCommessaAppartenenza(commessa.getCommessaAppartenenza());
    if (rigaApp != null)
    {
      riga.setSplRiga(PreventivoCommessaRiga.TIPO_RIGA_SOTTO_COMMESSA);
      riga.setIdRigacPrvApp(rigaApp.getIdRigacPrv());
    }
    riga.setCommessa(commessa);
    riga.getDescrizione().setDescrizione(commessa.getDescrizione().getDescrizione());
    riga.getDescrizione().setDescrizioneRidotta(commessa.getDescrizione().getDescrizioneRidotta());
    if (commessa.getIdArticolo() != null && !commessa.getIdArticolo().equals(""))
      riga.setIdArticolo(commessa.getIdArticolo());
    else
      riga.setIdArticolo(getIdArticolo());
    riga.setIdVersione(commessa.getIdVersione());
    riga.setIdConfigurazione(commessa.getIdConfigurazione());
    riga.setIdUmPrmMag(commessa.getIdUmPrmMag());
    riga.setQtaUmPrm(new BigDecimal("1"));
    riga.setVlrLivello(commessa.getValoreOrdine());
    riga.setVlrTotale(commessa.getValoreTotaleOrdine());
    riga.setNota(commessa.getNote());
    try
    {
      riga.save();
      ConnectionManager.commit();
    }
    catch (SQLException ex)
    {
    }
    return riga;
  }

  /**
   * getCommessMap
   *
   * @param ite Iterator
   * @return Map
   */
  protected void verificaStrutturaCommessa(Commessa commessa, PreventivoCommessaRiga riga, Map sottoCommessaCorrente)
  {
    if (commessa != null)
    {
      Map sottoCommessaSelezionate = getSottoCommesse(commessa.getSottocommesse());
      Iterator ite = sottoCommessaSelezionate.keySet().iterator();
      while (ite.hasNext())
      {
        String key = (String)ite.next();
        Commessa sottoCommessa = (Commessa)sottoCommessaSelezionate.get(key);
        if (!sottoCommessaCorrente.containsKey(key))
        {
          PreventivoCommessaRiga rigaAPP = generaRigaCommessa(sottoCommessa, riga);
          verificaStrutturaCommessa(sottoCommessa, rigaAPP, sottoCommessaCorrente);
        }
      }
    }
  }

  /**
   * GetSottoCommesseCorrente
   *
   * @return Map
   */
  protected Map getSottoCommesse(List sottoCommesse)
  {
    Iterator ite = sottoCommesse.iterator();
    Map ret = new HashMap();
    while (ite.hasNext())
    {
      Commessa sottoCom = (Commessa)ite.next();
      ret.put(sottoCom.getKey(), sottoCom);
    }
    return ret;
  }

  protected Map getSottoCommesseFromPreventivo(List sottoCommesse)
  {
    Iterator ite = sottoCommesse.iterator();
    Map ret = new HashMap();
    while (ite.hasNext())
    {
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)ite.next();
      if (riga.getIdCommessa() != null && !riga.getIdCommessa().equals(""))
        ret.put(riga.getCommessa().getKey(), riga.getCommessa());
    }
    return ret;
  }

  public int deleteRigaPrev() throws SQLException
  {
    int rc = super.delete();
    rc = deleteSottoCommesse();
    return rc;
  }

  /**
   * deleteSottoCommesse
   *
   * @return int
   */
  protected int deleteSottoCommesse() throws SQLException
  {
    int rc = 1;
    List sottoCommesse = getSottoCommesse();
    if (!sottoCommesse.isEmpty())
    {
      Iterator ite = sottoCommesse.iterator();
      while (ite.hasNext())
      {
        PreventivoCommessaRiga riga = (PreventivoCommessaRiga)ite.next();
        rc = riga.deleteRigaPrev();
      }
    }
    return rc;
  }

  public void setSalvaRighe(boolean b)
  {
    this.iSalvaRighe = b;
  }

  /**
   * setSalvaRighe
   *
   * @param b boolean
   */
  public BigDecimal getPercentuale()
  {
    return calcolaPercentualeSu(getMdcLivello(), getVlrLivello());
  }

  public static BigDecimal calcolaPercentualeSu(BigDecimal importo, BigDecimal perc)
  {
    BigDecimal valPerc = new BigDecimal(0);
    if (importo != null && importo.compareTo(new BigDecimal(0)) != 0 && perc != null && perc.compareTo(new BigDecimal(0)) != 0)
    {
      BigDecimal val = importo.divide(perc, 6, BigDecimal.ROUND_HALF_UP);
      valPerc = val.multiply(new BigDecimal(100));
    }
    return valPerc;
  }

  public void setPercentuale(BigDecimal b)
  {
    this.iPercentuale = b;
  }

  public boolean isSalvaRighe()
  {
    return this.iSalvaRighe;
  }

  public int saveOwnedObjects(int rc) throws SQLException
  {
     if (isSalvaRighe())
     {
       return super.saveOwnedObjects(rc);
     }
     else //Fix 31091
       return iDescrizione.getHandler().save(rc); //Fix 31091
     //return ErrorCodes.OK; //Fix 31091
  }

  public BigDecimal getVlrTotPrevCom()
  {

    return iVlrTotPrevCom;
  }

  public BigDecimal getCosTotPrevCom()
  {

    return iCosTotPrevCom;
  }

  public BigDecimal getMdcTotPrevCom()
  {

    return iMdcTotPrevCom;
  }

  public BigDecimal getPercTotPrevCom()
  {

    return iMdcTotPrevCom;
  }

  public String getIdCliente()
  {
    return iIdCliente;
  }

  //15848
  public PreventivoCommessaRiga copiaRiga(PreventivoCommessaTestata docDest, SpecificCopiaPreventivoCommessa spec) throws CopyException
  {
    PreventivoCommessaRiga riga = (PreventivoCommessaRiga)Factory.createObject(PreventivoCommessaRiga.class);
    //33972 inizio
    this.setDeepRetrieveEnabled(true);
    this. getCommenti();
    //33972 fine
    this.setDeepRetrieveEnabled(false);
    riga.setEqual(this);
    riga.setOnDB(false);
    riga.setKey(null);
    riga.setIdAzienda(docDest.getIdAzienda());
    riga.setTestata(docDest);
    riga.setOldRigaPrev(this.getIdRigacPrv());  // fix 30762
    //29032 inizio
    if(spec.getProfonditaCopia() == SpecificCopiaPreventivoCommessa.PF_COPIA_TUTTO){
    	copiaVoce(riga, spec);
    }
    //29032 fine
    
    //Fix 31162 inzio
    List righe = riga.getRighe();
    Iterator iter = righe.iterator();
    while (iter.hasNext()) {
      PreventivoCommessaVoce rigaPadre = (PreventivoCommessaVoce)iter.next();
      if (rigaPadre.getIdSubRigavPrv() == 0) {
        int idRigaPadre = rigaPadre.getIdRigavPrv();
        Iterator iterRighe = righe.iterator();
        while (iterRighe.hasNext()) {
         PreventivoCommessaVoce rigaDett = (PreventivoCommessaVoce)iterRighe.next();
         if(rigaDett.getIdSubRigavPrv() != 0 && rigaDett.getIdRigavPrv() == idRigaPadre)
           rigaPadre.getRighe().add(rigaDett);
        }
      }
    }
    //Fix 31162 fine
    
    return riga;
  }

  public void copiaVoce(PreventivoCommessaRiga rigaDest, SpecificCopiaPreventivoCommessa spec) throws CopyException
  {
    if (rigaDest != null)
    {

      List righe = getRighe();
      //Fix 20406 inizio
      List righeDaSort = new ArrayList();
      for (Iterator iter = righe.iterator(); iter.hasNext(); )
      {
    	  PreventivoCommessaVoce voce = (PreventivoCommessaVoce)iter.next();
         righeDaSort.add(voce);
      }
      Collections.sort(righeDaSort, new PrevCommVoceComparator());
      //Fix 20406 fine
      if (!righeDaSort.isEmpty()) //Fix 20406
        rigaDest.setSalvaRighe(true);
      for (Iterator iter = righeDaSort.iterator(); iter.hasNext(); ) //Fix 20406
      {
        PreventivoCommessaVoce voce = (PreventivoCommessaVoce)iter.next();
        PreventivoCommessaVoce rigaCopiata = voce.copiaRiga(rigaDest, spec);
        if (rigaCopiata != null)
        {
          rigaDest.getRighe().add(rigaCopiata);
        }
      }
    }

  }

  //Fix 20406 Inizio
  protected class PrevCommVoceComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			PreventivoCommessaVoce prevCommVoce1 = (PreventivoCommessaVoce) o1;
			PreventivoCommessaVoce prevCommVoce2 = (PreventivoCommessaVoce) o2;
            int res = Utils.compare(new Integer(prevCommVoce1.getIdRigavPrv()), new Integer(prevCommVoce2.getIdRigavPrv()));
            if(res ==0)
            {
            	res = Utils.compare(new Integer(prevCommVoce1.getIdSubRigavPrv()), new Integer(prevCommVoce2.getIdSubRigavPrv()));
            }
            return res;
		}
	}
  //Fix 20406 Fine

  //Fix 22355 Inizio
  public boolean isDelete() {
    return iDelete;
  }

  public void setDelete(boolean delete) {
    iDelete = delete;
  }

  public int delete() throws SQLException{
    setDelete(true);
    int rc = super.delete();
    
    //Fix 27506 inizio
    //if (rc >= 0){//31310
    if (rc >= 0 && isDeleteFather()){//31310
    	//Fix 31227 inizio
        //getTestata().ricalcolaValori();
        List righeDaRicalcolare = new ArrayList();
        PreventivoCommessaRiga tmp = this;
        while (tmp.getRigaAppartenenza() != null) {
          righeDaRicalcolare.add(tmp.getRigaAppartenenza());
          tmp = tmp.getRigaAppartenenza();
        }
        getTestata().ricalcolaValori(righeDaRicalcolare, null);
        //Fix 31227 fine
    }
    //Fix 27506 fine
    
    return rc;
  }
  //Fix 22355 Fine
  
  // fix 30762
  // riferimento riga vecchia per copia
  protected Integer iOldRigaPrev;
  public void setOldRigaPrev(Integer oldRiga) {
	  iOldRigaPrev = oldRiga;
  }
  public Integer getOldRigaPrev() {
	  return iOldRigaPrev;
  }
  
  // fine fix 30762
  //31310 inizio
  public boolean isDeleteFather() {
	  return iDeleteFather;
  }
  public void setDeleteFather(boolean deleteFather) {
	  iDeleteFather = deleteFather;
  } 
  //31310 fine
  //Fix 31451 inizio
  protected void cercaSottocomesseDaRic(PreventivoCommessaRiga riga, List righeDaRicalcolare) {
    List stc = riga.getSottoCommesse();
    Iterator iter = stc.iterator();
    while (iter.hasNext()) {
      PreventivoCommessaRiga tmp = (PreventivoCommessaRiga) iter.next();
      righeDaRicalcolare.add(tmp);
      cercaSottocomesseDaRic(tmp, righeDaRicalcolare);
    }
  }
  //Fix 31451 fine
}

/*
 * @(#)PreventivoCommessaVoce.java
 */

/**
 * PrevComVoce
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author MARZOUK FATTOUMA 24/10/2011 at 13:56:43
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 24/10/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 15848     05/03/2012    FM       Correzione
 * 19817     19/05/2014    AA       Correzione del caso d'articolo Kit con esplosione da modello
 * 18537     07/10/2013    TF       Istanziare la classe ModproEsplosione utilizzando la Factory
 * 20406     26/09/2014    TF       Correzione preventivo commessa
 * 20569     22/07/2015    AA       Corretto il recupero del prezzo nel caso d'articolo Kit con esplosione da modello
 * 22355     12/10/2015    AA       Varie correzioni
 * 27506     30/05/2018    DBot     Correzioni a preventivo
 * 29032	 25/03/2019	   RA		Revisione preventivi di commessa
 * 29166	 01/04/2019	   RA       Revisione preventivi di commessa
 * 29529     04/07/2019	   RA		Varie correzioni
 * 29642	 17/07/2019	   RA		Revisione preventivi di commessa
 * 29731	 20/08/2019	   RA       Rivisione Preventivo commessa : varie modifiche
 * 29870	 23/08/2019	   RA       Rivisione Preventivo commessa : varie modifiche
 * 29882	 25/09/2019    RA		Corrette gestione righe secondari
 * 30327     09/12/2019    DB       Ganci personalizzazioni
 * 30358     18/12/2019    RA		Corretto errore in copia preventivo commessa
 * 30687	 10/02/2020	   RA		Corretto valorizzazione prezzo
 * 30702     12/02/2020    DB       Gancio personalizzazioni
 * 30934     17/03/2020   Mekki     Markup : portare la scale da 2 a 6
 * 30871     09/03/2020    SZ		6 Decimali.
 * 31162     28/04/2020   Mekki     Correggere problemi di performance (decine di secondi) quando si modifica la quantità o si eliminano delle voci.
 * 31227     15/05/2020   Mekki     Velocizzare la cancellazione delle righe all'interno del preventivo
 * 31218	 20/05/2020	   RA		Corretto valorizzazione costo da listino vendita per articolo con configurazione
 * 31451     25/06/2020   Mekki     Corrggere metodo hasRigheSec()
 * 31385	 29/06/2020    RA		Rivisione Preventivo commessa : varie modifiche
 * 32902     10/02/2021    RA		Rendi il metodo getVoceFather() public
 * 33626     07/07/2021    Jackal	Aggiunti ganci per personalizzazioni
 * 34002     14/07/2021    RA	    Correto problema di arrotondamento di qtà calcolata in esplosione dei componenti
 * 36730     12/10/2022    RA       Aggiunto controllo su SchemaCosto e ComponenteCosto
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
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.base.listini.ListinoVendita;//29032
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.costi.*;
import it.thera.thip.datiTecnici.distinta.*;
import it.thera.thip.datiTecnici.modpro.*;
import it.thera.thip.vendite.generaleVE.*;
import com.thera.thermfw.type.DecimalType;
import com.thera.thermfw.type.DateType;
import it.thera.thip.base.cliente.ClienteVendita;
import it.thera.thip.acquisti.generaleAC.CondizioniDiAcquisto;
import it.thera.thip.base.comuniVenAcq.web.*;
import it.thera.thip.base.documenti.DocumentoBaseRiga;

public class PreventivoCommessaVoce
  extends PreventivoCommessaVocePO
  implements SaveConWarning//29529
{

  protected static final String SELECT_MAX_ID_RIGHE_VOCE = "SELECT MAX(" + PreventivoCommessaVoceTM.ID_RIGAV_PRV + ")" +
    " FROM " + PreventivoCommessaVoceTM.TABLE_NAME +
    " WHERE " + PreventivoCommessaVoceTM.ID_AZIENDA + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_ANNO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_NUMERO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_RIGAC_PRV + " = ?";

  protected static CachedStatement cSelectMaxIdRigheVoce = new CachedStatement(SELECT_MAX_ID_RIGHE_VOCE);

  protected static final String SELECT_MAX_SEQUENZA_RIGHE_VOCE = "SELECT MAX(" + PreventivoCommessaVoceTM.SEQUENZA_RIGA + ")" +
    " FROM " + PreventivoCommessaVoceTM.TABLE_NAME +
    " WHERE " + PreventivoCommessaVoceTM.ID_AZIENDA + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_ANNO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_NUMERO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_RIGAC_PRV + " = ?";

  protected static CachedStatement cSelectMaxSequenzaRigheVoce = new CachedStatement(SELECT_MAX_SEQUENZA_RIGHE_VOCE);

  protected static final String SELECT_MAX_SUB_RIGHE_VOCE = "SELECT MAX(" + PreventivoCommessaVoceTM.ID_SUB_RIGAV_PRV + ")" +
    " FROM " + PreventivoCommessaVoceTM.TABLE_NAME +
    " WHERE " + PreventivoCommessaVoceTM.ID_AZIENDA + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_ANNO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_NUMERO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_RIGAC_PRV + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_RIGAV_PRV + " = ?";

  protected static CachedStatement cSelectMaxSubRigheVoce = new CachedStatement(SELECT_MAX_SUB_RIGHE_VOCE);

  //Fix 19817 Inizio
  protected static final String DELETE_RIGHE_VOCE_SEC = "DELETE " +
    " FROM " + PreventivoCommessaVoceTM.TABLE_NAME +
    " WHERE " + PreventivoCommessaVoceTM.ID_AZIENDA + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_ANNO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_NUMERO_PREVC + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_RIGAC_PRV + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_RIGAV_PRV + " = ?" +
    " AND " + PreventivoCommessaVoceTM.ID_SUB_RIGAV_PRV + " <> 0 " ;

  protected static CachedStatement cDeleteRigheVoceSec = new CachedStatement(DELETE_RIGHE_VOCE_SEC);
  //Fix 19817 Fine

  protected static final String WHERE_UM_TEMPO = " WHERE ID_AZIENDA = ?";
  public static final String UM_TEMPO_VIEW_NAME = SystemParam.getSchema("THIP") + "UM_TEMPI_V01";

  // SPECIALIZZAZIONE RIGA
  public static final char RIGA_PRIMARIA = '1';
  public static final char RIGA_SECONDARIA = '2';

  // COSTO RIFERIMENTO
  public static final char COS_RIF_NESSUN_COSTO = '0';
  public static final char COS_RIF_COSTO_STD = '1';
  public static final char COS_RIF_COSTO_MEDIO = '2';
  public static final char COS_RIF_COSTO_ULTIMO = '3';
  public static final char COS_RIF_COSTO_ART = '4';

  // TIPO PREZZO
  public static final char TP_PREZZO_NORMALE = '0';
  public static final char TP_PREZZO_NETTO = '1';

  // PROVENIENZA PREZZO
  public static final char PRV_PREZZO_MANUALE = '0';
  public static final char PRV_PREZZO_LISTINO_GEN = '1';
  public static final char PRV_PREZZO_LISTINO_CLT = '2';
  public static final char PRV_PREZZO_LISTINO_ZONA = '3';
  public static final char PRV_PREZZO_LISTINO_CATEG_VEN = '4';
  public static final char PRV_PREZZO_CONTRATTO = '5';

  //TIPO RIGA
  public static final char TP_RIG_VOCE = '1';
  public static final char TP_RIG_ARTICOLO = '2';
  public static final char TP_RIG_RISORSA = '3';

  public boolean generaRigaDettaglio = true;
  public BigDecimal iPercentuale = ZERO;
  protected CondizioniDiVendita condVen = null;
  protected CondizioniDiAcquisto condAcq = null;

  protected boolean propagazioneRicalcoloValori = true;//Fix 27506
  protected boolean propagazioneMarkup = true;//Fix 29032

  private List iWarningList;//29529

  public PreventivoCommessaVoce()
  {
    setIdAzienda(Azienda.getAziendaCorrente());
    setWarningList(new ArrayList());
  }

  /**
   * checkDeletev
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 24/10/2011    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete()
  {
    /**@todo*/
    return null;
  }

  /**
   * getTableNLSName
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 24/10/2011    Wizard     Codice generato da Wizard
   *
   */
  public String getTableNLSName()
  {
    return SystemParam.getSchema("THIP") + "PREV_COM_VOCE_L";
  }

  public int save() throws SQLException
  {
    // System.out.println("SAV£:" + KeyHelper.formatKeyString(getKey()));

    boolean oggettoNuovo = isOnDB();
    beforeSave();
    if (!onDB && isGeneraRigaDettaglio())
    {
      setIdRigavPrv(PreventivoCommessaRiga.getNumeroNuovaVoce(getPrevComRiga(), 1, PreventivoCommessaVoce.cSelectMaxIdRigheVoce));
      //setSequenzaRiga(PreventivoCommessaRiga.getNumeroNuovaVoce(getPrevComRiga(), 10, PreventivoCommessaVoce.cSelectMaxSequenzaRigheVoce));
      setIdSubRigacPrv(getPrevComRiga().getIdRigacPrvApp());
    }
    int rc = (iGestoreCommenti == null) ? 0 : salvaCommenti();
    if (rc < 0)
      return rc;
    else
    {
      int rc1 = super.save();
      if (rc1 >= 0)
      {
        rc = rc + rc1;
        //calcolaPrezzoCostiDettaglio(); //15848 //Fix 27506

      }
      else
        rc = rc1;
    }
    if (rc > 0 && isGeneraRigaDettaglio())
    {
      char tipoRigav = getTipoRigav();
      if (getArticolo() != null)
      {
        char tipoParteArt = getArticolo().getTipoParte();
        //29731 inizio
        //if (!oggettoNuovo && tipoRigav == TP_RIG_VOCE && (tipoParteArt == ArticoloDatiIdent.KIT_NON_GEST || tipoParteArt == ArticoloDatiIdent.KIT_GEST))//29731
        if (tipoRigav == TP_RIG_VOCE && (tipoParteArt == ArticoloDatiIdent.KIT_NON_GEST || tipoParteArt == ArticoloDatiIdent.KIT_GEST)){
        	if(!oggettoNuovo){
        		generaRigheSecondaria();
        	}
        	else{
        		//aggiorna qtà sec
        		//Fix 33626 - inizio
//        		if(getOldQtaPrvUmVen() != null && getQtaPrvUmVen() != null && getQtaPrvUmVen().compareTo(getOldQtaPrvUmVen()) != 0){
           		if(areCondizioniAggiornaRigheSec()){
           		//Fix 33626 - fine
        			aggiornaRigheSecondaria();
        		}
        	}
        }
        //29731 fine
      }
    }
    if (rc > 0)
    {
       //Fix 27506 inizio
       if(isPropagazioneRicalcoloValori()){
    	   //Fix 31227 inizio
           //getPrevComRiga().getTestata().ricalcolaValori();
           List vociDaRicalcolare = new ArrayList();
           vociDaRicalcolare.add(this);
           if (getIdSubRigavPrv() != 0 && getVoceFather() != null)
             vociDaRicalcolare.add(getVoceFather());

           List righeDaRicalcolare = new ArrayList();
           PreventivoCommessaRiga tmp = this.getPrevComRiga();
           righeDaRicalcolare.add(tmp);
           while (tmp.getRigaAppartenenza() != null) {
             righeDaRicalcolare.add(tmp.getRigaAppartenenza());
             tmp = tmp.getRigaAppartenenza();
           }

           getPrevComRiga().getTestata().ricalcolaValori(righeDaRicalcolare, vociDaRicalcolare);
           //Fix 31227 fine
       }
       //calcolaTotalePrevCommessa(getPrevComRiga());
       //Fix 27506 fine
    }
    return rc;
  }


  /**
   * calcolaCostiDettaglio
   */
  protected void calcolaPrezzoCostiDettaglio()
  {
     //Fix 27506 inizio
     System.err.println("Voce.calcolaPrezzoCostiDettaglio() Metodo non più operativo");
     /*
    if (getIdSubRigavPrv() != 0)
    {
      PreventivoCommessaVoce father = getVoceFather();
      if(father != null) { //Fix 20406
      	father.ricalcolaPrezzoFromRigaSec();
      	try {
      		father.save();
      	}
      	catch (SQLException ex) {
      	}
      }//Fix 20406
    }
    */
     //Fix 27506 fine
  }

  public void beforeSave()
  {

    if (getTipoRigav() == TP_RIG_VOCE)
      setQtaPrvUmPrm(getQtaPrvUmVen());
    if (getTipoRigav() == TP_RIG_RISORSA)
    {
      setQtaPrvUmVen(getQtaPrvUmPrm());
      setIdUmVen(getIdUmPrmMag());
    }

    if(!isInCopia()){//29032
    	BigDecimal costoRifDV = getCostoRifer();//29529
    	//29870 inizio
        char costoMod = getPrevComRiga().getTestata().getCausalePreventivoCommessa().getCostoModificabile();
        if (costoMod == CausalePreventivoCommessa.COS_MODIF_SOLO_NON_REPERITO ||
        	((costoRifDV == null || costoRifDV.compareTo(ZERO) == 0) &&  costoMod == CausalePreventivoCommessa.COS_MODIF_SEMPRE)){
        	recuperaCostoRif(); //Fix 20569
        	//30687 inizio
        	if(getMarkup() != null
        			//&& getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP) {   // fix 30702
        			&& isGestioneConMarkup()) {  // fine fix 30702
        		BigDecimal prezzo = calcoloPrezziDaMarkup(this);
        		setPrezzo(prezzo);
        	}
        	//30687 fine
        }
    	//29870 fine
    	//29529 inizio
    	if(costoRifDV != null && costoRifDV.compareTo(ZERO) != 0 && costoRifDV.compareTo(getCostoRifer()) != 0){
    		addWarning(new ErrorMessage("THIP40T657"));
    	}
    	//29529 fine
    	calcolaImportiECCosti();
    }//29032
    //30358 inizio
    else{
        setIdAnnoPrevc(getIdAnnoPrevc());
    }
    //30358 fine
    //updateDescrizione();//29529
    //29032 inizio
    /*
    if(isPropagazioneMarkup()){
        char tipoRigav = getTipoRigav();
        if(getArticolo() != null){
	        char tipoParteArt = getArticolo().getTipoParte();
	        if (onDB && getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP  &&
	        	tipoRigav == TP_RIG_VOCE &&
	        	(tipoParteArt == ArticoloDatiIdent.KIT_NON_GEST || tipoParteArt == ArticoloDatiIdent.KIT_GEST))
	        {
	        	if(getMarkup().compareTo(getOldMarkup()) != 0){
	                Iterator righeSecondarie = getRighe().iterator();
	                while (righeSecondarie.hasNext()) {
	                  PreventivoCommessaVoce voceSec = (PreventivoCommessaVoce)righeSecondarie.next();
	                  if(voceSec.getMarkup().compareTo(getOldMarkup()) == 0){
	                	  voceSec.setMarkup(getMarkup());
	                	  BigDecimal prezzo = calcoloPrezziDaMarkup(voceSec);
	                	  voceSec.setPrezzo(prezzo);
	                	  voceSec.setProvenienzaPrz(PreventivoCommessaVoce.PRV_PREZZO_MANUALE);//29642
	                	  voceSec.setPropagazioneMarkup(false);
	                	  voceSec.setPropagazioneRicalcoloValori(false);
	                	  try {
	                		  voceSec.save();
	                	  } catch (SQLException e) {
	                		  e.printStackTrace(Trace.excStream);
	                	  }
	                  }
	                }
	        	}
	        }
	        //29731 inizio
	        else if(tipoRigav == TP_RIG_RISORSA && getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
	        	 BigDecimal prezzo = calcoloPrezziDaMarkup(this);
	        	 setPrezzo(prezzo);
	        	 setProvenienzaPrz(PreventivoCommessaVoce.PRV_PREZZO_MANUALE);
	        }
	        //29731 fine
        }
    }
    */
    //29032 fine
  }

  protected void calcolaImportiECCosti()
  {
    BigDecimal valoreRiga = somma(getPrezzo(), getPrezzoExtra());
    setVlrTotale(valoreRiga.multiply(getQtaPrvUmVen()));
    setCosTotale(getCostoRifer().multiply(getQtaPrvUmVen()));
    //29529 inizio
    if(getTipoRigav() == TP_RIG_RISORSA){
    	if(getCoeffImp() != null)//29731
    		setCosTotale(getCosTotale().multiply(getCoeffImp()));
    }
    //29529 fine
    setMdcTotale(sottrai(getVlrTotale(), getCosTotale()));
  }

  /**
   * getVoceFather
   *
   * @return PreventivoCommessaVoce
   */
  //protected PreventivoCommessaVoce getVoceFather()//32902
  public PreventivoCommessaVoce getVoceFather()//32902
  {
    PreventivoCommessaVoce voce = null;
    try
    {
      String key = KeyHelper.replaceTokenObjectKey(getKey(), 6, "0");
      voce = PreventivoCommessaVoce.elementWithKey(key, PersistentObject.NO_LOCK);
    }
    catch (SQLException ex)
    {
    }
    return voce;
  }

  public ErrorMessage checkQtaPrvUmVen()
  {
    BigDecimal qta = getQtaPrvUmVen();
    if (qta == null || qta.compareTo(new BigDecimal(0)) == 0 && getTipoRigav() != PreventivoCommessaVoce.TP_RIG_RISORSA)
      return new ErrorMessage("THIP40T170");
    return null;
  }

  public ErrorMessage checkQtaPrvUmPrm()
  {
    BigDecimal qta = getQtaPrvUmPrm();
    if (qta == null || qta.compareTo(new BigDecimal(0)) == 0 && getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA)
      return new ErrorMessage("THIP40T170");
    return null;
  }

  public ErrorMessage checkIdArticolo()
  {
    String idArticolo = getIdArticolo();
    if (idArticolo == null && getTipoRigav() != TP_RIG_RISORSA)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  public ErrorMessage checkIdRisorsa()
  {
    String idRisorsa = getIdRisorsa();
    if (idRisorsa == null && getTipoRigav() == TP_RIG_RISORSA)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  public ErrorMessage checkIdUmVen()
  {
    String idUMVendita = getIdUmVen();
    if (idUMVendita == null && getTipoRigav() != TP_RIG_RISORSA)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  public ErrorMessage checkIdUmPrmMag()
  {
    String idUmPrmMag = getIdUmPrmMag();
    if (idUmPrmMag == null && getTipoRigav() == TP_RIG_RISORSA)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  public ErrorMessage checkCoeffImp()
  {
    BigDecimal coefImp = this.getCoeffImp();
    if (coefImp == null && getTipoRigav() == TP_RIG_RISORSA)
      return new ErrorMessage("BAS0000000");
    return null;
  }

  public void calcolaTotalePrevCommessa(PreventivoCommessaRiga preventivoCommessaRiga)
  {
     System.err.println("Voce.calcolaTotalePrevCommessa() Metodo non più operativo");

     /*
    salvaRiga(preventivoCommessaRiga);
    calcolaValoreTestata(preventivoCommessaRiga.getTestata());
    */
  }

  public void calcolaValoreTestata(PreventivoCommessaTestata preventivoCommessaTestata)
  {
     System.err.println("Voce.calcolaTotalePrevCommessa() Metodo non più operativo");
     /*
    Iterator ite = preventivoCommessaTestata.getRighe().iterator();
    BigDecimal valoreVoce = new BigDecimal(0);
    BigDecimal valoreArticolo = new BigDecimal(0);
    BigDecimal valoreRisorsaU = new BigDecimal(0);
    BigDecimal valoreRisorsaM = new BigDecimal(0);
    BigDecimal costoVoce = new BigDecimal(0);
    BigDecimal costoArticolo = new BigDecimal(0);
    BigDecimal costoRisorsaU = new BigDecimal(0);
    BigDecimal costoRisorsaM = new BigDecimal(0);
    BigDecimal margineVoce = new BigDecimal(0);
    BigDecimal margineArticolo = new BigDecimal(0);
    BigDecimal margineRisorsaU = new BigDecimal(0);
    BigDecimal margineRisorsaM = new BigDecimal(0);

    while (ite.hasNext())
    {
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)ite.next();
      Iterator iteRiga = riga.getRighe().iterator();
      while (iteRiga.hasNext())
      {
        PreventivoCommessaVoce rigaVoce = (PreventivoCommessaVoce)iteRiga.next();
        if (rigaVoce.getTipoRigav() == TP_RIG_VOCE)
        {
          valoreVoce = somma(valoreVoce, rigaVoce.getVlrTotale());
          costoVoce = somma(costoVoce, rigaVoce.getCosTotale());
          margineVoce = somma(margineVoce, rigaVoce.getMdcTotale());
        }
        if (rigaVoce.getTipoRigav() == TP_RIG_ARTICOLO)
        {
          valoreArticolo = somma(valoreArticolo, rigaVoce.getVlrTotale());
          costoArticolo = somma(costoArticolo, rigaVoce.getCosTotale());
          margineArticolo = somma(margineArticolo, rigaVoce.getMdcTotale());
        }
        if (rigaVoce.getTipoRigav() == TP_RIG_RISORSA && rigaVoce.getTipoRisorsa() == Risorsa.RISORSE_UMANE)
        {
          valoreRisorsaU = somma(valoreRisorsaU, rigaVoce.getVlrTotale());
          costoRisorsaU = somma(costoRisorsaU, rigaVoce.getCosTotale());
          margineRisorsaU = somma(margineRisorsaU, rigaVoce.getMdcTotale());
        }
        if (rigaVoce.getTipoRigav() == TP_RIG_RISORSA && rigaVoce.getTipoRisorsa() == Risorsa.MACCHINE)
        {
          valoreRisorsaM = somma(valoreRisorsaM, rigaVoce.getVlrTotale());
          costoRisorsaM = somma(costoRisorsaM, rigaVoce.getCosTotale());
          margineRisorsaM = somma(margineRisorsaM, rigaVoce.getMdcTotale());
        }

      }
    }
    preventivoCommessaTestata.setVlrVociPrevc(valoreVoce);
    preventivoCommessaTestata.setVlrArtPrevc(valoreArticolo);
    preventivoCommessaTestata.setVlrRisuPrevc(valoreRisorsaU);
    preventivoCommessaTestata.setVlrRismPrevc(valoreRisorsaM);

    preventivoCommessaTestata.setCosVociPrevc(costoVoce);
    preventivoCommessaTestata.setCosArtPrevc(costoArticolo);
    preventivoCommessaTestata.setCosRisuPrevc(costoRisorsaU);
    preventivoCommessaTestata.setCosRismPrevc(costoRisorsaM);

    preventivoCommessaTestata.setMdcVociPrevc(margineVoce);
    preventivoCommessaTestata.setMdcArtPrevc(margineArticolo);
    preventivoCommessaTestata.setMdcRisuPrevc(margineRisorsaU);
    preventivoCommessaTestata.setMdcRismPrevc(margineRisorsaM);

    salvaTestata(preventivoCommessaTestata);
    */

  }

  private void salvaRiga(PreventivoCommessaRiga preventivoCommessaRiga)
  {
    try
    {
      preventivoCommessaRiga.setSalvaRighe(false);
      preventivoCommessaRiga.save();
    }
    catch (SQLException ex)
    {

    }
  }

  private BigDecimal somma(BigDecimal valore1, BigDecimal valore2)
  {
    if (valore1 == null)
      return valore2 == null ? new BigDecimal(0) : valore2;
    else
    if (valore2 == null)
      return valore1;
    return valore1.add(valore2);
  }

  protected BigDecimal sottrai(BigDecimal valore1, BigDecimal valore2)
  {
    if (valore1 == null)
      valore1 = new BigDecimal(0);
    if (valore2 == null)
      valore2 = new BigDecimal(0);
    return valore1.subtract(valore2);

  }

  private void salvaTestata(PreventivoCommessaTestata preventivoCommessa)
  {
     System.err.println("Voce.salvaTestata() Metodo non più operativo");
/*
    try
    {
      preventivoCommessa.save();
      preventivoCommessa.ricalcolaImportiCosti();
    }
    catch (SQLException ex)
    {

    }
    */
  }

  protected void generaRigheSecondaria() throws SQLException
  {
    Articolo articolo = getArticolo();
    EspNodoArticolo esplosione = esplosioneModelloDocumento(articolo);
    if (esplosione != null)
      generaRigheSecondarieEsplosioneModello(false, esplosione);
    else
      generaRigheKit(getEsplosioneNodo(articolo));
  }

  protected EsplosioneNodo getEsplosioneNodo(Articolo articolo) throws
    SQLException
  {
    Esplosione esplosione = new Esplosione();
    esplosione.setTipoEsplosione(Esplosione.PRODUZIONE);
    esplosione.setTrovaTestataEsatta(false);
    esplosione.setIdArticolo(articolo.getIdArticolo());
    esplosione.getProprietario().setTipoProprietario(ProprietarioDistinta.
      CLIENTE);
    esplosione.getProprietario().setCliente(((PreventivoCommessaRiga)getPrevComRiga()).getTestata().
                                            getCliente());
    esplosione.setTipoDistinta(DistintaTestata.NORMALE);
    esplosione.setLivelloMassimo(new Integer(1));
    esplosione.setData(getDataConsegPrv());
    esplosione.setQuantita(getQtaPrvUmPrm());
    Integer idConfigurazione = getIdConfigurazione();//29642
    if (idConfigurazione != null)
      esplosione.setIdConfigurazione(idConfigurazione);

    esplosione.run();
    Trace.println(esplosione.getKey());
    Trace.println(esplosione.getNodoRadice());
    return esplosione.getNodoRadice();
  }

  public EspNodoArticolo esplosioneModelloDocumento(Articolo articolo) throws SQLException
  {
    EspNodoArticolo esplosione = null;
    boolean okModello = false;
    try
    {
      esplosione = getEsplosioneNodoModello(false, articolo, ModelloProduttivo.KIT);
      okModello = true;
    }
    catch (ThipException ex)
    {
      okModello = false;
      esplosione = null;
    }

    if (!okModello)
    {
      try
      {
        esplosione = getEsplosioneNodoModello(false, articolo, ModelloProduttivo.PRODUZIONE);
        okModello = true;
      }
      catch (ThipException ex)
      {
        okModello = false;
        esplosione = null;
      }
    }
    return esplosione;
  }

  protected EspNodoArticolo getEsplosioneNodoModello(boolean ricercaGerarchica, Articolo articolo, char tipoModello) throws SQLException
  {

  	//Fix 18537 inizio
 		//ModproEsplosione esplosione = new ModproEsplosione();
 		ModproEsplosione esplosione = (ModproEsplosione)Factory.createObject(ModproEsplosione.class);
 		//Fix 18537 fine

    esplosione.setIdArticolo(articolo.getIdArticolo());
    if (articolo.isConfigurato())
    {
      esplosione.setConfigurazione(getConfigurazione());
    }
    esplosione.setIdVersione(getIdVersione());
    esplosione.setTipoEsplosione(ModelloProduttivo.PRODUZIONE);
    esplosione.setLivelloMassimo(1);
    esplosione.setData(getDataConsegPrv());
    esplosione.setQuantita(this.getQtaPrvUmPrm());
    esplosione.setApplicaFattoreScarto(true);
    esplosione.setDominio(ModelloProduttivo.GENERICO);
    if (tipoModello != '-')
      esplosione.setTipiModello(new char[]
                                {tipoModello});
    //esplosione.setEsplodeRisorse(false);//29731
    //esplosione.setEsplodeRisorse(true);//29731//29870
    esplosione.setEsplodeRisorse(getPrevComRiga().getTestata().getCausalePreventivoCommessa().isEsplodiRisorseInKit());//29731 //29870
    esplosione.setAttivaGestioneQtaIntera(true);

    Stabilimento stab = getPrevComRiga().getTestata().getStabilimento();
    esplosione.setStabilimento(stab);
    esplosione.setGesConfigTmp(ModproEsplosione.CREATE_MEMORIZZATE);
    esplosione.run();

    return esplosione.getNodoRadice();
  }

  protected void generaRigheSecondarieEsplosioneModello(boolean ricercaGerarchica, EspNodoArticolo nodo) throws SQLException
  {
    List datiRigheSec = nodo.getNodiMateriali();
    datiRigheSec.addAll(nodo.getNodiProdottiNonPrimari());
    datiRigheSec.addAll(nodo.getNodiRisorse());//29731
    if (datiRigheSec.isEmpty())
    {
      return;
    }
    else
    {
      //int sequenza = 0; //Fix 19817
      int sequenza = 1; //Fix 19817
      Iterator iter = datiRigheSec.iterator();
      while (iter.hasNext())
      {
        //EspNodoArticoloBase datiRigaSec = (EspNodoArticoloBase)iter.next();//29731
        //Fix 19817 Inizio
        //PreventivoCommessaVoce rigaSec = generaRigaSecondariaModello(datiRigaSec, sequenza++);
        //PreventivoCommessaVoce rigaSec = generaRigaSecondariaModello(datiRigaSec, sequenza);//29731
    	//29731 inizio
        PreventivoCommessaVoce rigaSec = null;
        Object obj =  iter.next();
        if (obj instanceof EspNodoArticoloBase)  {
        	EspNodoArticoloBase datiRigaSec = (EspNodoArticoloBase) obj;
        	rigaSec = generaRigaSecondariaModello(datiRigaSec, sequenza++);
        }
        else if (obj instanceof EspNodoRisorsa)  {
        	EspNodoRisorsa datiRigaSec = (EspNodoRisorsa) obj;
        	rigaSec = generaRigaSecondariaModello(datiRigaSec, sequenza++);
        }
        //29731 fine
        if (rigaSec != null) { //29731
        	sequenza++;
        	//Fix 19817 Fine
        	rigaSec.setIdAzienda(getIdAzienda());
        	rigaSec.setPropagazioneRicalcoloValori(false); //Fix 27506
        	rigaSec.save();
        	rigaSec.setPropagazioneRicalcoloValori(true);//Fix 27506
        }//29731
      }
    }
  }

  protected PreventivoCommessaVoce generaRigaSecondariaModello(EspNodoArticoloBase datiRigaSec, int sequenza) throws SQLException
  {
    PreventivoCommessaVoce rigaSec = creaRigaSecondaria();
    if (rigaSec != null)
    {
      rigaSec.setIdAzienda(this.getIdAzienda());
    }
    rigaSec.setGeneraRigaDettaglio(false);
    rigaSec.setSequenzaRiga(sequenza);
    rigaSec.setIdAnnoPrevc(getIdAnnoPrevc());
    rigaSec.setIdNumeroPrevc(getIdNumeroPrevc());
    rigaSec.setIdRigacPrv(getIdRigacPrv());
    rigaSec.setIdRigavPrv(getIdRigavPrv());
    //rigaSec.setIdSubRigavPrv(sequenza++); //Fix 19817
    rigaSec.setIdSubRigavPrv(sequenza); //Fix 19817
    rigaSec.setIdSubRigacPrv(getPrevComRiga().getIdRigacPrvApp());
    rigaSec.setTipoRigav(getTipoRigav());
    rigaSec.setSplRiga(PreventivoCommessaVoce.RIGA_SECONDARIA);
    Articolo articoloSec = datiRigaSec.getArticoloUsato().getArticolo();
    //Fix 27506 inizio
    rigaSec.iDescrizione.setDescrizione(articoloSec.getDescrizioneArticoloNLS().getDescrizione());
    rigaSec.iDescrizione.setDescrizioneRidotta(articoloSec.getDescrizioneArticoloNLS().getDescrizioneRidotta());
    //rigaSec.iDescrizione.setDescrizione(iDescrizione.getDescrizione());
    //rigaSec.iDescrizione.setDescrizioneRidotta(iDescrizione.getDescrizioneRidotta());
    //Fix 27506 fine
    rigaSec.setArticoloKey(articoloSec.getKey());
    rigaSec.setIdUmPrmMag(articoloSec.getIdUMPrmMag());
    if (articoloSec.getClasseMerclg() != null)
    {
      ComponenteCosto compCosto = articoloSec.getClasseMerclg().getComponenteCosto();
      rigaSec.setComponenteCosto(compCosto);
    }
    //rigaSec.setIdUmVen(articoloSec.getUMPrimariaVendita().getIdUnitaMisura());//29870
    rigaSec.setIdUmVen(articoloSec.getUMDefaultVendita().getIdUnitaMisura());//29870
    Integer idVersioneSec = datiRigaSec.getIdVersioneUsata();
    if (idVersioneSec != null)
    {
      rigaSec.setIdVersione(idVersioneSec);
    }
    //Fix 19817 Inizio
    if(datiRigaSec.getConfigurazioneUsata() != null)
      rigaSec.setConfigurazione(datiRigaSec.getConfigurazioneUsata());
    //Fix 19817 Fine
    rigaSec.setNota(((EspNodoArticolo)datiRigaSec).getAttivitaProdMateriale().getNote());
    //recuperaDatiVendita(rigaSec);   // fix 30327
    //recuperaDatiAcquisto(rigaSec); //Fix 20569 Riga Commentata
    recuperaCostoRifRigaSec(rigaSec); //Fix 20569
    // Sposto in basso il metodo recuperaDatiVendita() 30327
    impostaDatiRigaSecPers(rigaSec);		//Fix 33626
    recuperaDatiVendita(rigaSec);
    //29032 inizio
    if(rigaSec.getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
    	//BigDecimal markup = getMarkup();//29882
    	BigDecimal markup = rigaSec.getPrevComRiga().getMarkupArticolo();//29882
		if(markup.compareTo(new BigDecimal("0")) >= 0){
			rigaSec.setMarkup(markup);
			BigDecimal prezzo = calcoloPrezziDaMarkup(rigaSec);
			rigaSec.setPrezzo(prezzo);
			rigaSec.setProvenienzaPrz(PreventivoCommessaVoce.PRV_PREZZO_MANUALE);//29642
		}
    }
    //29032 fine
    //Fix 20569 Inizio
    //Articolo articoloKit = datiRigaSec.getModproEsplosione().getArticolo();//34002 : riga commentata
    Articolo articoloKit = datiRigaSec.getArticoloUsato().getArticolo(); //34002
    UnitaMisura umVen = articoloKit.getUMDefaultVendita();
    UnitaMisura umPrm = articoloKit.getUMPrmMag();
    UnitaMisura umSec = articoloKit.getUMSecMag();
    BigDecimal qc = datiRigaSec.getQuantitaCalcolata();
    //BigDecimal qtaCalcolata = qc.setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
	BigDecimal qtaCalcolata = Q6Calc.get().setScale(qc,2, BigDecimal.ROUND_HALF_UP);//Fix 30871

    BigDecimal qtaVendita = articoloKit.convertiUM(qtaCalcolata, umPrm, umVen, rigaSec.getArticoloVersione());
    BigDecimal qtaSecondaria = (umSec == null) ? new BigDecimal(0.0) : articoloKit.convertiUM(qtaVendita, umVen, umSec, rigaSec.getArticoloVersione());
    if (UnitaMisura.isPresentUMQtaIntera(umVen, umPrm, umSec, articoloKit))
    {
      QuantitaInUMRif qta = articoloKit.calcolaQuantitaArrotondate(qtaCalcolata, umVen, umPrm, umSec, rigaSec.getArticoloVersione(), Articolo.UM_PRM);
      qtaVendita = qta.getQuantitaInUMRif();
      qtaCalcolata = qta.getQuantitaInUMPrm();
      qtaSecondaria = qta.getQuantitaInUMSec();
    }
    rigaSec.setCoeffImp(datiRigaSec.getCoeffImpiego());
    if (datiRigaSec.getCoeffTotale())
    {
      rigaSec.setBlcQtaCmp(true);
      rigaSec.setCoeffImp(new BigDecimal("0"));
    }
    rigaSec.setQtaPrvUmVen(qtaVendita);
    rigaSec.setQtaPrvUmPrm(qtaCalcolata);
    rigaSec.setQtaPrvUmSec(qtaSecondaria);
    //Fix 20569 Fine
    return rigaSec;
  }

  //29731 inizio
  protected PreventivoCommessaVoce generaRigaSecondariaModello(EspNodoRisorsa datiRigaSec, int sequenza) throws SQLException  {
	  // qui creare un PreventivoCommessaVoce ed impostarne i dati usando l'EspNodoRisorsa sulla falsariga di quanto fatto con l'EspNodoArticoloBase
	  PreventivoCommessaVoce rigaSec = creaRigaSecondaria();
	  if (rigaSec != null) {
		  rigaSec.setIdAzienda(this.getIdAzienda());
	  }
	  rigaSec.setGeneraRigaDettaglio(false);
	  rigaSec.setSequenzaRiga(sequenza);
	  rigaSec.setIdAnnoPrevc(getIdAnnoPrevc());
	  rigaSec.setIdNumeroPrevc(getIdNumeroPrevc());
	  rigaSec.setIdRigacPrv(getIdRigacPrv());
	  rigaSec.setIdRigavPrv(getIdRigavPrv());
	  rigaSec.setIdSubRigavPrv(sequenza);
	  rigaSec.setIdSubRigacPrv(getPrevComRiga().getIdRigacPrvApp());
	  rigaSec.setTipoRigav(PreventivoCommessaVoce.TP_RIG_RISORSA);
	  rigaSec.setSplRiga(PreventivoCommessaVoce.RIGA_SECONDARIA);

	  Risorsa risorsa = datiRigaSec.getDatiRisorsa().getRisorsa();
	  rigaSec.setRisorsa(risorsa);
	  rigaSec.setLivelloRisorsa(datiRigaSec.getDatiRisorsa().getLivelloRisorsa());
	  rigaSec.setTipoRisorsa(datiRigaSec.getDatiRisorsa().getTipoRisorsa());
	  rigaSec.iDescrizione.setDescrizione(risorsa.getDescrizione().getDescrizione());
	  rigaSec.iDescrizione.setDescrizioneRidotta(risorsa.getDescrizione().getDescrizioneRidotta());
	  rigaSec.setComponenteCosto(datiRigaSec.getDatiRisorsa().getComponenteCosto());
	  rigaSec.setSchemaCosto(datiRigaSec.getDatiRisorsa().getSchemaCosto());
	  Articolo articoloRsr = risorsa.getArticoloServizio();
	  if(articoloRsr != null){
		  rigaSec.setArticoloKey(articoloRsr.getKey());
		  rigaSec.setIdUmPrmMag( PersDatiGen.getCurrentPersDatiGen().getCategoriaUMTempi().getIdUmPrm());
	  }
	  rigaSec.setCoeffImp(datiRigaSec.getAttivitaProdRisorsa().getCoeffUtilizzo());

	  UnitaMisura umVen = articoloRsr.getUMDefaultVendita();
	  UnitaMisura umPrm = articoloRsr.getUMPrmMag();
	  UnitaMisura umSec = articoloRsr.getUMSecMag();
	  BigDecimal qc = datiRigaSec.getTempoCalc();
	  //BigDecimal qtaCalcolata = qc.setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
	  BigDecimal qtaCalcolata = Q6Calc.get().setScale(qc,2, BigDecimal.ROUND_HALF_UP);//Fix 30871

	  BigDecimal qtaVendita = articoloRsr.convertiUM(qtaCalcolata, umPrm, umVen, rigaSec.getArticoloVersione());
	  BigDecimal qtaSecondaria = (umSec == null) ? new BigDecimal(0.0) : articoloRsr.convertiUM(qtaVendita, umVen, umSec, rigaSec.getArticoloVersione());
	  if (UnitaMisura.isPresentUMQtaIntera(umVen, umPrm, umSec, articoloRsr)) {
		  QuantitaInUMRif qta = articoloRsr.calcolaQuantitaArrotondate(qtaCalcolata, umVen, umPrm, umSec, rigaSec.getArticoloVersione(), Articolo.UM_PRM);
		  qtaVendita = qta.getQuantitaInUMRif();
	      qtaCalcolata = qta.getQuantitaInUMPrm();
	      qtaSecondaria = qta.getQuantitaInUMSec();
	  }

	  rigaSec.setQtaPrvUmVen(qtaVendita);
	  rigaSec.setQtaPrvUmPrm(qtaCalcolata);
	  rigaSec.setQtaPrvUmSec(qtaSecondaria);

	  if(getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){//31385
		  if(rigaSec.getTipoRisorsa() == Risorsa.RISORSE_UMANE){
			  rigaSec.setMarkup(rigaSec.getPrevComRiga().getMarkupUomo());
		  }
		  else if(rigaSec.getTipoRisorsa() == Risorsa.MACCHINE){
			  rigaSec.setMarkup(rigaSec.getPrevComRiga().getMarkupMacchina());
		  }
	  }

	  //recuperaDatiVendita(rigaSec);  // fix 30327
	  recuperaCostoRifRigaSec(rigaSec);
	  impostaDatiRigaSecPers(rigaSec);		//Fix 33626
	  recuperaDatiVendita(rigaSec);  // fix 30327
	  if(getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){//31385
		  //BigDecimal markup = rigaSec.getMarkup();//29882
		  //BigDecimal markup = rigaSec.getPrevComRiga().getMarkupArticolo();//29882//31385
		  BigDecimal markup = getPrevComRiga().getMarkupArticolo();//29882//31385
		  if(markup.compareTo(new BigDecimal("0")) > 0){
			  BigDecimal prezzo = calcoloPrezziDaMarkup(rigaSec);
			  rigaSec.setPrezzo(prezzo);
		  }
		  else{
			  rigaSec.setPrezzo(rigaSec.getCostoRifer());
		  }
		  rigaSec.setProvenienzaPrz(PreventivoCommessaVoce.PRV_PREZZO_MANUALE);
	  }
	  return rigaSec;
  }

  protected void aggiornaRigheSecondaria() throws SQLException{
	  Articolo articolo = getArticolo();
	  EspNodoArticolo esplosione = esplosioneModelloDocumento(articolo);
	  if (esplosione != null){
		    List datiRigheSec = esplosione.getNodiMateriali();
		    datiRigheSec.addAll(esplosione.getNodiProdottiNonPrimari());
		    datiRigheSec.addAll(esplosione.getNodiRisorse());
		    if (datiRigheSec.isEmpty()) {
		      return;
		    }
		    else {
		    	Iterator iter = datiRigheSec.iterator();
		    	while (iter.hasNext()) {
		    		Object obj =  iter.next();
		    		if (obj instanceof EspNodoArticoloBase)  {
		    			EspNodoArticoloBase datiRigaSec = (EspNodoArticoloBase) obj;
		    			aggiornaRigaSecondariaModello(datiRigaSec);
		    		}
		    		else if (obj instanceof EspNodoRisorsa) {
		    			EspNodoRisorsa datiRigaSec = (EspNodoRisorsa) obj;
		    			aggiornaRigaSecondariaModello(datiRigaSec);
		    		}
		    	}
		    }
	  }
	  else{
		  EsplosioneNodo esp = getEsplosioneNodo(articolo);
		  if(esp != null && esp.getNodiFigli() != null && !esp.getNodiFigli().isEmpty()){
			  aggiornaRigheKit(esp);
		  }
		  else{
			  addWarning(new ErrorMessage("THIP40T658", new String[] {getIdArticolo()}));
		  }
	  }
  }

  protected void aggiornaRigaSecondariaModello(EspNodoArticoloBase datiRigaSec) throws SQLException  {
    PreventivoCommessaVoce rigaSec = cercaRigaSecondaria(datiRigaSec);
    if (rigaSec != null) {
    	//Articolo articoloKit = datiRigaSec.getModproEsplosione().getArticolo(); //34002 : riga commentata
    	Articolo articoloKit = datiRigaSec.getArticoloUsato().getArticolo(); //34002
    	UnitaMisura umVen = articoloKit.getUMDefaultVendita();
    	UnitaMisura umPrm = articoloKit.getUMPrmMag();
    	UnitaMisura umSec = articoloKit.getUMSecMag();
    	BigDecimal qc = datiRigaSec.getQuantitaCalcolata();
    	//BigDecimal qtaCalcolata = qc.setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
		BigDecimal qtaCalcolata = Q6Calc.get().setScale(qc,2, BigDecimal.ROUND_HALF_UP);//Fix 30871

    	BigDecimal qtaVendita = articoloKit.convertiUM(qtaCalcolata, umPrm, umVen, rigaSec.getArticoloVersione());
    	BigDecimal qtaSecondaria = (umSec == null) ? new BigDecimal(0.0) : articoloKit.convertiUM(qtaVendita, umVen, umSec, rigaSec.getArticoloVersione());
    	if (UnitaMisura.isPresentUMQtaIntera(umVen, umPrm, umSec, articoloKit))
    	{
    		QuantitaInUMRif qta = articoloKit.calcolaQuantitaArrotondate(qtaCalcolata, umVen, umPrm, umSec, rigaSec.getArticoloVersione(), Articolo.UM_PRM);
    		qtaVendita = qta.getQuantitaInUMRif();
    		qtaCalcolata = qta.getQuantitaInUMPrm();
    		qtaSecondaria = qta.getQuantitaInUMSec();
    	}
    	rigaSec.setCoeffImp(datiRigaSec.getCoeffImpiego());
    	if (datiRigaSec.getCoeffTotale())
    	{
    		rigaSec.setBlcQtaCmp(true);
    		rigaSec.setCoeffImp(new BigDecimal("0"));
    	}
    	rigaSec.setQtaPrvUmVen(qtaVendita);
    	rigaSec.setQtaPrvUmPrm(qtaCalcolata);
    	rigaSec.setQtaPrvUmSec(qtaSecondaria);
    	rigaSec.setPropagazioneRicalcoloValori(false); //Fix 31162
    	aggiornaDatiRigaSecPers(rigaSec);	//Fix 33626
    	rigaSec.save();
    }
  }

  protected void aggiornaRigaSecondariaModello(EspNodoRisorsa datiRigaSec) throws SQLException  {
	  PreventivoCommessaVoce rigaSec = cercaRigaSecondaria(datiRigaSec);
	  if (rigaSec != null) {
		  Risorsa risorsa = datiRigaSec.getDatiRisorsa().getRisorsa();
		  Articolo articoloRsr = risorsa.getArticoloServizio();
		  if(articoloRsr != null){
			  rigaSec.setArticoloKey(articoloRsr.getKey());
			  rigaSec.setIdUmPrmMag( PersDatiGen.getCurrentPersDatiGen().getCategoriaUMTempi().getIdUmPrm());
		  }
		  rigaSec.setCoeffImp(datiRigaSec.getAttivitaProdRisorsa().getCoeffUtilizzo());

		  UnitaMisura umVen = articoloRsr.getUMDefaultVendita();
		  UnitaMisura umPrm = articoloRsr.getUMPrmMag();
		  UnitaMisura umSec = articoloRsr.getUMSecMag();
		  BigDecimal qc = datiRigaSec.getTempoCalc();
		  //BigDecimal qtaCalcolata = qc.setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
		  BigDecimal qtaCalcolata = Q6Calc.get().setScale(qc,2, BigDecimal.ROUND_HALF_UP);//Fix 30871

		  BigDecimal qtaVendita = articoloRsr.convertiUM(qtaCalcolata, umPrm, umVen, rigaSec.getArticoloVersione());
		  BigDecimal qtaSecondaria = (umSec == null) ? new BigDecimal(0.0) : articoloRsr.convertiUM(qtaVendita, umVen, umSec, rigaSec.getArticoloVersione());
		  if (UnitaMisura.isPresentUMQtaIntera(umVen, umPrm, umSec, articoloRsr)) {
			  QuantitaInUMRif qta = articoloRsr.calcolaQuantitaArrotondate(qtaCalcolata, umVen, umPrm, umSec, rigaSec.getArticoloVersione(), Articolo.UM_PRM);
			  qtaVendita = qta.getQuantitaInUMRif();
		      qtaCalcolata = qta.getQuantitaInUMPrm();
		      qtaSecondaria = qta.getQuantitaInUMSec();
		  }

		  rigaSec.setQtaPrvUmVen(qtaVendita);
		  rigaSec.setQtaPrvUmPrm(qtaCalcolata);
		  rigaSec.setQtaPrvUmSec(qtaSecondaria);
		  rigaSec.setPropagazioneRicalcoloValori(false); //Fix 31162
		  rigaSec.save();
	  }
  }

  protected void aggiornaRigheKit(EsplosioneNodo esp) throws SQLException{
	  List datiRigheKit = esp.getNodiFigli();
	  if (datiRigheKit.isEmpty()){
		  return;
	  }
	  else {
		  Iterator iter = datiRigheKit.iterator();
		  while (iter.hasNext()) {
			  EsplosioneNodo datiRigaKit = (EsplosioneNodo)iter.next();
			  PreventivoCommessaVoce rigaKit = cercaRigaSecondaria(datiRigaKit);
			  if(rigaKit != null){
				  Articolo articoloKit = datiRigaKit.getArticolo();
				  UnitaMisura umVen = articoloKit.getUMDefaultVendita();
				  UnitaMisura umPrm = articoloKit.getUMPrmMag();
				  UnitaMisura umSec = articoloKit.getUMSecMag();
				  BigDecimal qc = datiRigaKit.getQuantitaCalcolata();
				  //BigDecimal qtaCalcolata = qc.setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
				  BigDecimal qtaCalcolata = Q6Calc.get().setScale(qc,2, BigDecimal.ROUND_HALF_UP);//Fix 30871
				  BigDecimal qtaVendita = articoloKit.convertiUM(qtaCalcolata, umPrm, umVen, rigaKit.getArticoloVersione());
				  BigDecimal qtaSecondaria = (umSec == null) ? new BigDecimal(0.0) : articoloKit.convertiUM(qtaVendita, umVen, umSec, rigaKit.getArticoloVersione());

				  if (UnitaMisura.isPresentUMQtaIntera(umVen, umPrm, umSec, articoloKit)) {
					  QuantitaInUMRif qta = articoloKit.calcolaQuantitaArrotondate(qtaCalcolata, umVen, umPrm, umSec, rigaKit.getArticoloVersione(), Articolo.UM_PRM);
					  qtaVendita = qta.getQuantitaInUMRif();
					  qtaCalcolata = qta.getQuantitaInUMPrm();
					  qtaSecondaria = qta.getQuantitaInUMSec();
				  }

				  rigaKit.setCoeffImp(datiRigaKit.getCoeffImpiego());
				  if (datiRigaKit.getCoeffTotale()) {
					  rigaKit.setBlcQtaCmp(true);
					  rigaKit.setCoeffImp(new BigDecimal("0"));
				  }

				  rigaKit.setQtaPrvUmVen(qtaVendita);
				  if (umVen != null){
					  rigaKit.setUMVen(umVen);
				  }
				  rigaKit.setQtaPrvUmPrm(qtaCalcolata);
				  rigaKit.setUmPrmMag(umPrm);
				  rigaKit.setQtaPrvUmSec(qtaSecondaria);
				  rigaKit.setUmSecMag(umSec);
				  rigaKit.save();
			  }
		  }
	  }
  }

  protected PreventivoCommessaVoce cercaRigaSecondaria(EspNodoArticoloBase datiRigaSec){
	  PreventivoCommessaVoce riga = null;
	  List righe = getRighe();
	  if(!righe.isEmpty()) {
		  Iterator iter = righe.iterator();
	      while (iter.hasNext() && riga == null){
	    	  PreventivoCommessaVoce rigaDet = (PreventivoCommessaVoce)iter.next();
	    	  if(rigaDet.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE && rigaDet.getArticolo().equals(datiRigaSec.getArticoloUsato().getArticolo())){
	    		  riga = rigaDet;
	    	  }
	      }
	  }
	  return riga;
  }

  protected PreventivoCommessaVoce cercaRigaSecondaria(EspNodoRisorsa datiRigaSec){
	  PreventivoCommessaVoce riga = null;
	  List righe = getRighe();
	  if(!righe.isEmpty()) {
		  Iterator iter = righe.iterator();
	      while (iter.hasNext() && riga == null){
	    	  PreventivoCommessaVoce rigaDet = (PreventivoCommessaVoce)iter.next();
	    	  if(rigaDet.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA && rigaDet.getRisorsa().equals(datiRigaSec.getDatiRisorsa().getRisorsa())){
	    		  riga = rigaDet;
	    	  }
	      }
	  }
	  return riga;
  }

  protected PreventivoCommessaVoce cercaRigaSecondaria(EsplosioneNodo datiRigaSec){
	  PreventivoCommessaVoce riga = null;
	  List righe = getRighe();
	  if(!righe.isEmpty()) {
		  Iterator iter = righe.iterator();
	      while (iter.hasNext() && riga == null){
	    	  PreventivoCommessaVoce rigaDet = (PreventivoCommessaVoce)iter.next();
	    	  if(rigaDet.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE && rigaDet.getArticolo().equals(datiRigaSec.getArticolo())){
	    		  riga = rigaDet;
	    	  }
	      }
	  }
	  return riga;
  }
  //29731 fine

  /**
   * recuperaDatiVendita
   */
  protected void recuperaDatiVendita(PreventivoCommessaVoce rigaSec) throws SQLException
  {
    if(getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_DA_LST_VEND){//29032//31385
		if (getPrevComRiga().getTestata().getListinoVen() != null)//31385
	    {
	      recuperaCondizioniVendita(rigaSec);

	      if (condVen != null)
	      {

	        rigaSec.setPrezzo(condVen.getPrezzo());
	        rigaSec.setPrezzoExtra(condVen.getPrezzoExtra());
	        rigaSec.setProvenienzaPrz(condVen.getTipoTestata());
	        impostaDatiRigaSecDaCondVenditaPers(rigaSec);	//Fix 33626
	      }
	    }
    }//29032

  }

  protected void recuperaDatiAcquisto(PreventivoCommessaVoce rigaSec) throws SQLException
  {
    if (getPrevComRiga().getTestata().getListinoAcq() != null)//31385
    {
      recuperaCondizioniAcquisto(rigaSec);

      if (condAcq != null)
        rigaSec.setCostoRifer(condAcq.getPrezzo());
    }

  }

  protected void recuperaCondizioniAcquisto(PreventivoCommessaVoce riga) throws SQLException
  {
    PreventivoCommessaTestata testata= riga.getPrevComRiga().getTestata();
    String dataRif = null;
    String dataConsegna = null;
    String versione = null;
    String    qtaPrvUmPrm= null;
    DecimalType decType = new DecimalType();//31218
    if (testata.getDataRiferimento() != null)
    dataRif = testata.getDataRiferimento().toString();
   if (testata.getDataConsegRcs() != null)
    dataConsegna = testata.getDataConsegRcs().toString();

  if (riga.getIdVersione() != null)
    versione = riga.getIdVersione().toString();

  if (riga.getQtaPrvUmPrm() != null)
    qtaPrvUmPrm = riga.getQtaPrvUmPrm().toString();

    if (testata.getListinoAcq() != null)
    {
      condAcq =
        RecuperaDatiAcquisto.getCondizioniAcquisto(
        "",
        testata.getIdListinoAcq(),
        "",
         riga.getIdArticolo(),
        //String.valueOf(riga.getIdConfigurazione()), //31218
        riga.getIdEsternoConfig(), //31218
        String.valueOf(riga.getIdUmVen()),
        //31218 inizio
        decType.objectToString(riga.getQtaPrvUmVen()),
        decType.objectToString(riga.getQtaPrvUmVen()),
        /*
        qtaPrvUmPrm,
        qtaPrvUmPrm,
        */
        //31218 fine
        dataRif,
        dataConsegna,
        String.valueOf(riga.getIdUmVen()),
        testata.getListinoAcq().getIdValuta(),
        'N',
        null,
        "",
        "",
        "",
        versione, "",
        String.valueOf(riga.getIdUmSecMag()), String.valueOf(riga.getQtaPrvUmSec())
        );
    }
  }

  protected void recuperaCondizioniVendita(PreventivoCommessaVoce riga) throws SQLException
  {
    DecimalType decType = new DecimalType();
    DateType dateType = new DateType();

    condVen =
      RicercaCondizioniDiVendita.getCondizioniVendita(
      riga.getPrevComRiga().getTestata().getIdListinoVen(),
      riga.getPrevComRiga().getTestata().getIdCliente(),
      riga.getIdArticolo(),
      riga.getIdEsternoConfig(),
      // Fix 33626 - inizio
//      riga.getIdUmPrmMag(),
//      decType.objectToString(riga.getQtaPrvUmPrm()),
//      decType.objectToString(riga.getQtaPrvUmVen()),
      riga.getIdUmVen(),
      decType.objectToString(riga.getQtaPrvUmVen()),
      decType.objectToString(riga.getQtaPrvUmPrm()),
      // Fix 33626 - fine
      null,
      dateType.objectToString(riga.getPrevComRiga().getTestata().getDataPrevc()),
      dateType.objectToString(riga.getPrevComRiga().getTestata().getDataConsegPrv()),
      null,
      null,
      riga.getIdUmPrmMag(),
      riga.getPrevComRiga().getTestata().getIdValuta(),
      null,
      riga.getIdUmSecMag(), decType.objectToString(riga.getQtaPrvUmSec()) // fix 13211
      );

    BigDecimal prezzo = null;
    if (condVen != null)
    {
      prezzo = condVen.getPrezzo();
    }

    if (prezzo == null || prezzo.equals(new BigDecimal(0.0)))
    {

      String chiaveCliente =
        KeyHelper.buildObjectKey(
        new String[]
        {Azienda.getAziendaCorrente(), riga.getPrevComRiga().getTestata().getIdCliente()}
        );
      ClienteVendita cliente = (ClienteVendita)
        ClienteVendita.elementWithKey(
        ClienteVendita.class, chiaveCliente, PersistentObject.NO_LOCK
        );

      String idListinoCliente = null;
      String idListinoAlternativo = null;

      if (cliente != null)
      {
        idListinoCliente = cliente.getIdListino();
        idListinoAlternativo = cliente.getIdListinoAlternativo();
      }

      if (idListinoCliente != null &&
          idListinoAlternativo != null &&
          idListinoCliente.equals(riga.getPrevComRiga().getTestata().getIdListinoVen()))
      {
        condVen =
          RicercaCondizioniDiVendita.getCondizioniVendita(
          idListinoAlternativo,
          riga.getPrevComRiga().getTestata().getIdCliente(),
          getIdArticolo(),
          getIdEsternoConfig(),
          riga.getIdUmPrmMag(),
          decType.objectToString(riga.getQtaPrvUmVen()),
          decType.objectToString(riga.getQtaPrvUmPrm()),
          null,
          dateType.objectToString(riga.getPrevComRiga().getTestata().getDataPrevc()),
          dateType.objectToString(getDataConsegPrv()),
          null,
          null,
          riga.getIdUmPrmMag(),
          riga.getPrevComRiga().getTestata().getIdValuta(),
          null,
          riga.getIdUmSecMag(), decType.objectToString(riga.getQtaPrvUmSec()) // fix 13211
          );
      }
    }

  }

  public PreventivoCommessaVoce creaRigaSecondaria()
  {
    PreventivoCommessaVoce rigaSec =
      (PreventivoCommessaVoce)Factory.createObject(PreventivoCommessaVoce.class);
    return rigaSec;
  }

  protected void generaRigheKit(EsplosioneNodo nodo) throws SQLException
  {
    boolean calcoloDatiVendita = false;
    List datiRigheKit = nodo.getNodiFigli();
    if (datiRigheKit.isEmpty())
    {
      return;
    }
    else
    {
      int sequenza = 1;
      Iterator iter = datiRigheKit.iterator();
      while (iter.hasNext())
      {
        EsplosioneNodo datiRigaKit = (EsplosioneNodo)iter.next();
        PreventivoCommessaVoce rigaKit =
          (PreventivoCommessaVoce)Factory.createObject(PreventivoCommessaVoce.class);

        Articolo articoloKit = datiRigaKit.getArticolo();

        UnitaMisura umVen = articoloKit.getUMDefaultVendita();
        UnitaMisura umPrm = articoloKit.getUMPrmMag();
        UnitaMisura umSec = articoloKit.getUMSecMag();
        BigDecimal qc = datiRigaKit.getQuantitaCalcolata();
        //BigDecimal qtaCalcolata = qc.setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
		BigDecimal qtaCalcolata = Q6Calc.get().setScale(qc,2, BigDecimal.ROUND_HALF_UP);//Fix 30871

        BigDecimal qtaVendita =
          articoloKit.convertiUM(qtaCalcolata, umPrm, umVen, rigaKit.getArticoloVersione());
        BigDecimal qtaSecondaria =
          (umSec == null) ?
          new BigDecimal(0.0) :
          articoloKit.convertiUM(qtaVendita, umVen, umSec, rigaKit.getArticoloVersione());

        if (UnitaMisura.isPresentUMQtaIntera(umVen, umPrm, umSec, articoloKit))
        {
          QuantitaInUMRif qta = articoloKit.calcolaQuantitaArrotondate(qtaCalcolata, umVen, umPrm, umSec, rigaKit.getArticoloVersione(), Articolo.UM_PRM);
          qtaVendita = qta.getQuantitaInUMRif();
          qtaCalcolata = qta.getQuantitaInUMPrm();
          qtaSecondaria = qta.getQuantitaInUMSec();
        }
        rigaKit.setGeneraRigaDettaglio(false);
        rigaKit.setSequenzaRiga(sequenza);
        rigaKit.setIdAnnoPrevc(getIdAnnoPrevc());
        rigaKit.setIdNumeroPrevc(getIdNumeroPrevc());
        rigaKit.setIdRigacPrv(getIdRigacPrv());
        rigaKit.setIdRigavPrv(getIdRigavPrv());
        rigaKit.setIdSubRigavPrv(sequenza);
        rigaKit.setTipoRigav(getTipoRigav());
        //Fix 27506 inizio
        rigaKit.iDescrizione.setDescrizione(articoloKit.getDescrizioneArticoloNLS().getDescrizione());
        rigaKit.iDescrizione.setDescrizioneRidotta(articoloKit.getDescrizioneArticoloNLS().getDescrizioneRidotta());
        //rigaKit.iDescrizione.setDescrizione(iDescrizione.getDescrizione());
        //rigaKit.iDescrizione.setDescrizioneRidotta(iDescrizione.getDescrizioneRidotta());
        //Fix 27506 fine
        rigaKit.setCoeffImp(datiRigaKit.getCoeffImpiego());
        if (datiRigaKit.getCoeffTotale())
        {
          rigaKit.setBlcQtaCmp(true);
          rigaKit.setCoeffImp(new BigDecimal("0"));
        }

        rigaKit.getDatiComuniEstesi().setStato(getDatiComuniEstesi().getStato());

        rigaKit.setArticolo(articoloKit);
        //Fix 22355 Inizio
        if (articoloKit.getClasseMerclg() != null){
          String compCosto = articoloKit.getClasseMerclg().getIdComponenteCosto();
          rigaKit.setRComponCosto(compCosto);
        }
        //Fix 22355 Fine
        Integer idVersioneKit = datiRigaKit.getIdVersione();
        if (idVersioneKit != null)
        {
          rigaKit.setIdVersione(idVersioneKit);
          ArticoloVersione versioneKit =
            (ArticoloVersione)Factory.createObject(ArticoloVersione.class);
          String versioneKitKey =
            KeyHelper.buildObjectKey(
            new Object[]
            {
            getIdAzienda(),
            articoloKit.getIdArticolo(),
            idVersioneKit
          }
            );
          versioneKit.setKey(versioneKitKey);
        }
        rigaKit.setRDocumentoMm(getRDocumentoMm());

        if (datiRigaKit.getDistintaLegame().getIdDocumentoMM() != null)
          rigaKit.setRDocumentoMm(datiRigaKit.getDistintaLegame().getIdDocumentoMM());
        rigaKit.setNota(datiRigaKit.getDistintaLegame().getNote());

        rigaKit.setConfigurazione(datiRigaKit.getConfigurazione());
        rigaKit.setQtaPrvUmVen(qtaVendita);
        if (umVen != null)
          rigaKit.setUMVen(umVen);
        rigaKit.setQtaPrvUmPrm(qtaCalcolata);
        rigaKit.setUmPrmMag(umPrm);
        rigaKit.setQtaPrvUmSec(qtaSecondaria);
        rigaKit.setUmSecMag(umSec);
        rigaKit.setDataConsegRcs(getDataConsegRcs());
        rigaKit.setSettConsegRcs(getSettConsegRcs());
        rigaKit.setDataConsegPrv(getDataConsegPrv());
        rigaKit.setSettConsegPrv(getSettConsegPrv());
        rigaKit.setIdAzienda(getIdAzienda());
        impostaDatiRigaSecPers(rigaKit);	//Fix 33626
        recuperaDatiVendita(rigaKit);
        //recuperaDatiAcquisto(rigaKit); //Fix 20569 --Riga commentata
        recuperaCostoRifRigaSec(rigaKit); //Fix 20569
        //29032 inizio
        if(rigaKit.getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
        	BigDecimal markup = getMarkup();
    		if(markup.compareTo(new BigDecimal("0")) >= 0){
    			rigaKit.setMarkup(markup);
    			BigDecimal prezzo = calcoloPrezziDaMarkup(rigaKit);
    			rigaKit.setPrezzo(prezzo);
    			rigaKit.setProvenienzaPrz(PreventivoCommessaVoce.PRV_PREZZO_MANUALE);//29642
    		}
        }
        //29032 fine
        rigaKit.setPropagazioneRicalcoloValori(false); //Fix 27506
        rigaKit.save();
        rigaKit.setPropagazioneRicalcoloValori(true); //Fix 27506
        sequenza++;
      }
    }
  }

  /**
   * calcolaTotaleprevCommessa
   *
   * @param preventivoCommessaRiga PreventivoCommessaRiga
   */
  /* public void calcolaTotalePrevCommessa(PreventivoCommessaRiga preventivoCommessaRiga)
   {
     Iterator ite = preventivoCommessaRiga.getRighe().iterator();
     BigDecimal valoreTotale = new BigDecimal(0);
     BigDecimal costoTotale = new BigDecimal(0);
     BigDecimal margineTotale = new BigDecimal(0);

     while (ite.hasNext())
     {
       PreventivoCommessaRiga riga = (PreventivoCommessaRiga)ite.next();
      valoreTotale = somma(valoreTotale, riga.getVlrTotale());
      costoTotale = somma(costoTotale,riga.getCosTotale());
      margineTotale=somma(margineTotale, riga.getMdcTotale());
     }
     preventivoCommessaTestata.setVlrTotPrevc(valoreTotale);
     preventivoCommessaTestata.setCosTotPrevc(costoTotale);
     if(!preventivoCommessaTestata.getRighe().isEmpty())
       margineTotale = margineTotale.divide(new BigDecimal(preventivoCommessaTestata.getRighe().size()), 6, BigDecimal.ROUND_HALF_UP);
     preventivoCommessaTestata.setMdcTotPrevc(margineTotale);
     salvaTestata(preventivoCommessaTestata);


   }*/
  public java.util.Vector checkAll(BaseComponentsCollection components)
  {
    components.getGroup(BaseValidationGroup.KEY_VALIDATION_GROUP).setCheckMode(BaseBOComponentManager.CHECK_NEVER);
    //components.getComponent("Descrizione.Descrizione").setValue(("Descrizione"));
    //components.getComponent("Descrizione.DescrizioneRidotta").setValue(("Descrizione"));

    //updateDescrizione();
    Vector errors = super.checkAll(components);
    return errors;
  }

  /**
   * updateDescrizione
   */
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

  public void setGeneraRigaDettaglio(boolean flag)
  {
    this.generaRigaDettaglio = flag;
  }

  public boolean isGeneraRigaDettaglio()
  {
    return this.generaRigaDettaglio;
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
      PreventivoCommessaTestata prev = (PreventivoCommessaTestata)this.getPrevComRiga().getTestata();
      ret = prev.getEntity();
    }
    catch (Exception ex)
    {
      Trace.println("Eccezione nel metodo getEntity() della classe " + getClass().getName() + ": " + ex.getMessage());
      ex.printStackTrace(Trace.excStream);
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
    if (getArticolo() != null)
      return getArticolo().getCommentHandlerManager().getObject();
    return null;
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
        ret = !(iArticoloIntestatario.getArticolo().equals(getArticolo()) && iArticoloIntestatario.getIdConfigurazione().equals(getIdConfigurazione()));//29642
      }
      else
      {
        ret = !(iArticoloIntestatario.getArticolo().equals(getArticolo()) && (getIdConfigurazione() == null));//29642
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
    if (getPrevComRiga().getTestata().getIdCliente() != null)
    {
      String idAzienda = Azienda.getAziendaCorrente();
      String idCliente = getIdIntestatario();
      Articolo articolo = getArticolo();
      Integer idConfigurazione = getIdConfigurazione();//29642
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
      Trace.println("Eccezione nel metodo recuperaArticoloCliente() della classe  : " + e.getMessage());
      e.printStackTrace(Trace.excStream);
    }
    return ret;
  }

  public void completaDateConsegna()
  {
    if (getPrevComRiga().getTestata().getDataPrevc() != null)
      completaDateConsegna(getPrevComRiga().getTestata().getDataPrevc());
  }

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

  public String getIdValuta()
  {
    if (getPrevComRiga() != null && getPrevComRiga().getTestata() != null)
    {
      return getPrevComRiga().getTestata().getIdValuta();
    }
    return "";
  }

  public String getIdValutaAziendale()
  {
    PersDatiGen pdg = PersDatiGen.getCurrentPersDatiGen();
    return pdg.getIdValutaPrimaria();
  }

  public BigDecimal getFattorecambio()
  {
     //Fix 27506 inizio
     if(getPrevComRiga() != null && getPrevComRiga().getTestata() != null)
     {
        BigDecimal cambio = DocumentoOrdineTestata.recuperaCambio(getIdValuta(), getPrevComRiga().getTestata().getDataPrevc());
        return cambio;
     }
     return new BigDecimal(1);
//
//    BigDecimal cambio = DocumentoOrdineTestata.recuperaCambio(getIdValuta(), getPrevComRiga().getTestata().getDataPrevc());
//    return cambio;
     //Fix 27506 fine
  }

  public BigDecimal getCambio()
  {
    return convertiInValutaAziendale(getVlrTotale(), getIdValuta(), getFattorecambio());
  }

  public BigDecimal getCosUnitarioAZ()
  {
    return convertiInValutaAziendale(getCostoRifer(), getIdValuta(), getFattorecambio());
  }

  public BigDecimal getCosTotaleAZ()
  {
    return convertiInValutaAziendale(getCosTotale(), getIdValuta(), getFattorecambio());
  }

  public BigDecimal getMdcTotaleAZ()
  {
    return convertiInValutaAziendale(getMdcTotale(), getIdValuta(), getFattorecambio());
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

  public String getServizioUMRif()
  {
    UnitaMisura um = getUMVen();
    if (um != null)
      return um.getIdUnitaMisura() + " - " + um.getDescrizione().getDescrizione();
    else
      return null;
  }

  public String getServizioUMPrmMag()
  {
    UnitaMisura um = getUmPrmMag();
    if (um != null)
      return um.getIdUnitaMisura() + " - " + um.getDescrizione().getDescrizione();
    else
      return null;
  }

  public String getServizioUMSecMag()
  {
    UnitaMisura um = getUmSecMag();
    if (um != null)
      return um.getIdUnitaMisura() + " - " + um.getDescrizione().getDescrizione();
    else
      return null;
  }

  /**
   * initSequenza
   *
   * @param fatherKey String
   */
  public void initSequenza(boolean isDettaglio)
  {
    if (!isDettaglio)
      setSequenzaRiga(PreventivoCommessaRiga.getNumeroNuovaVoce(getPrevComRiga(), 10, PreventivoCommessaVoce.cSelectMaxSequenzaRigheVoce));
  }

  public int delete() throws SQLException
  {
    int rc = super.delete();
    rc = deleteRigheSecondarie(rc); //Fix 19817 //Fix 20569 Riga commentata //Fix 22355
    if (rc > 0 && !getPrevComRiga().isDelete())//29731
      salvaRiga(getPrevComRiga());
    return rc;
  }

  /**
   * getMaxSequenza
   *
   * @return int
   */
  public static int getMaxSequenza(String key)
  {
    return getNumeroSubVoce(key, 1, cSelectMaxSubRigheVoce);
  }

  public static synchronized int getNumeroSubVoce(String key, int passo, CachedStatement cSelectMaxNumeroRiga)
  {
    try
    {
      Database db = ConnectionManager.getCurrentDatabase();

      db.setString(cSelectMaxNumeroRiga.getStatement(), 1, KeyHelper.getTokenObjectKey(key, 1));
      db.setString(cSelectMaxNumeroRiga.getStatement(), 2, KeyHelper.getTokenObjectKey(key, 2));
      db.setString(cSelectMaxNumeroRiga.getStatement(), 3, KeyHelper.getTokenObjectKey(key, 3));
      db.setString(cSelectMaxNumeroRiga.getStatement(), 4, KeyHelper.getTokenObjectKey(key, 4));
      db.setString(cSelectMaxNumeroRiga.getStatement(), 5, KeyHelper.getTokenObjectKey(key, 5));
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

  public boolean isKitGestito()
  {
    if (getArticolo() != null &&
        getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE &&
        (getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_NON_GEST || getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_GEST))
      return true;
    return false;
  }

  /**
   * ricalcolaPrezzoFromRigaSec
   */
  public void ricalcolaPrezzoFromRigaSec()
  {
     //Fix 27506 inizio
     if(getArticolo() == null)
        return;
     //Fix 27506 fine
    try
    {
      char tipoParte = getArticolo().getTipoParte();
      char tipoCalcoloPrezzo = getArticolo().getTipoCalcPrzKit();
      if ((tipoParte == ArticoloDatiIdent.KIT_NON_GEST ||
           tipoParte == ArticoloDatiIdent.KIT_GEST)
          &&
          tipoCalcoloPrezzo == ArticoloDatiVendita.DA_COMPONENTI)
      {
        BigDecimal zero = new BigDecimal(0.0);
        BigDecimal prezzoRigaPrimaria = zero;
        //15848
        BigDecimal costoRigaPrimaria = zero;

        Iterator righeSecondarie = getRighe().iterator();
        while (righeSecondarie.hasNext())
        {
          PreventivoCommessaVoce voceSec =
            (PreventivoCommessaVoce)righeSecondarie.next();
          prezzoRigaPrimaria = prezzoRigaPrimaria.add(voceSec.getVlrTotale());
          costoRigaPrimaria = costoRigaPrimaria.add(voceSec.getCosTotale());
        }

        prezzoRigaPrimaria = prezzoRigaPrimaria.divide(getQtaPrvUmVen(), BigDecimal.ROUND_HALF_UP);
        costoRigaPrimaria = costoRigaPrimaria.divide(getQtaPrvUmVen(), BigDecimal.ROUND_HALF_UP);
        BigDecimal markup = getArticolo().getMarkupKit();
        if (markup != null && markup != zero)
        {
          BigDecimal perc = markup.divide(new BigDecimal(100.0), 6, BigDecimal.ROUND_HALF_UP);
          prezzoRigaPrimaria = prezzoRigaPrimaria.add(prezzoRigaPrimaria.multiply(perc));
        }
    	setPrezzo(prezzoRigaPrimaria);
        setCostoRifer(costoRigaPrimaria);
    	setMarkup(calcoloMarkup()); //29870
        setTpPrezzo(TipoPrezzo.LORDO);
        setProvenienzaPrz(TipoRigaRicerca.MANUALE);
        calcolaImportiECCosti();//29032
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

  }

  public List getListaUMTempo()
  {
    ResultSet rs = null;
    ArrayList list = new ArrayList();
    try
    {
      CachedStatement cRetrieveUmTempo = new CachedStatement("SELECT * FROM " + UM_TEMPO_VIEW_NAME + WHERE_UM_TEMPO);
      PreparedStatement ps = cRetrieveUmTempo.getStatement();
      Database db = ConnectionManager.getCurrentDatabase();

      db.setString(ps, 1, Azienda.getAziendaCorrente());
      rs = cRetrieveUmTempo.executeQuery();
      while (rs.next())
      {
        String UMTempoKey = KeyHelper.buildObjectKey(new Object[]
          {rs.getString(1), rs.getString(2)});
        UnitaMisura umTempo = UnitaMisura.elementWithKey(UMTempoKey, PersistentObject.NO_LOCK);
        list.add(umTempo);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace(System.err);
    }
    finally
    {
      try
      {
        rs.close();
      }
      catch (SQLException e)
      {
        e.printStackTrace(System.err);
      }
    }
    return list;
  }

  public BigDecimal getPercentuale()
  {
    return calcolaPercentualeSu(getMdcTotale(), getVlrTotale());
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

  /**
   * copiaRiga
   *
   * @param rigaDest PreventivoCommessaRiga
   * @param spec SpecificCopiaPreventivoCommessa
   * @return PreventivoCommessaVoce
   */
  public PreventivoCommessaVoce copiaRiga(PreventivoCommessaRiga rigaDest, SpecificCopiaPreventivoCommessa spec) throws CopyException
  {
    PreventivoCommessaVoce voce = (PreventivoCommessaVoce)Factory.createObject(PreventivoCommessaVoce.class);
    this.setDeepRetrieveEnabled(false);
    voce.setEqual(this);
    voce.setOnDB(false);
    voce.setKey(null);
    voce.setIdAzienda(rigaDest.getIdAzienda());
    voce.setPrevComRiga(rigaDest);
    //Fix 20406 inizio
    voce.setIdRigavPrv(getIdRigavPrv());
    voce.setIdSubRigavPrv(getIdSubRigavPrv());
    voce.setGeneraRigaDettaglio(false);
    //Fix 20406 fine
    //29032 inizio
    voce.setInCopia(true);
    if(!spec.isCopiaMarkup()){
    	voce.setMarkup(ZERO);
    }

    boolean costoRicalcolato = false;
    if(spec.isRicalcolaPrezzi()){
    	if(rigaDest.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_DA_LST_VEND){
    		Articolo articolo = null;
    		if (getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE || getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO){
    			articolo = getArticolo();
    		}
    		else{
    			articolo = getRisorsa().getArticoloServizio();
    		}
    		if(articolo != null){
        		char tipoParte = articolo.getTipoParte();
        		char tipoCalcoloPrezzo = articolo.getTipoCalcPrzKit();
        		if ((tipoParte != ArticoloDatiIdent.KIT_NON_GEST && tipoParte != ArticoloDatiIdent.KIT_GEST)
        	          || tipoCalcoloPrezzo != ArticoloDatiVendita.DA_COMPONENTI)
        		{
            		CondizioniDiVendita cv = getCondizioniVendita(voce);
            		if (cv != null) {
            			voce.setPrezzo(cv.getPrezzo());
            			voce.setPrezzoExtra(cv.getPrezzoExtra());
            			voce.setProvenienzaPrz(cv.getTipoTestata());
            		}
        		}
    		}

    	}
    	else if(rigaDest.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
    		if(spec.isRicalcolaCosti()){
    			voce.recuperaCostoRif();
    			costoRicalcolato = true;
    		}

    		if(voce.getCostoRifer() != null){
    			if(voce.getMarkup() != null &&  voce.getMarkup().compareTo(new BigDecimal("0")) >= 0){
    				BigDecimal prezzo = calcoloPrezziDaMarkup(voce);
    				voce.setPrezzo(prezzo);
    			}
    		}
    	}
    }

    if(!costoRicalcolato && spec.isRicalcolaCosti()){
    	voce.recuperaCostoRif();
    }
    //29032 fine
    return voce;
  }

  /**
   * getPrezzoTotale
   *
   * @return Object
   */
  public BigDecimal getPrezzoTotale()
  {
    return somma(getPrezzo(), getPrezzoExtra());
  }

  //Fix 19817 Inizio
  public int deleteRigheSecondarie(int rc){
    //Fix 22355 Inizio
    boolean isDeleteTestata = false;
    if(getPrevComRiga() != null){
      isDeleteTestata = getPrevComRiga().isDelete();
    }
    //Fix 22355 Fine
    if(rc > 0 && hasRigheSec() && getKey() != null && !isDeleteTestata){ //Fix 22355
      String key = getKey();
      try{
        Database db = ConnectionManager.getCurrentDatabase();
        PreparedStatement ps = cDeleteRigheVoceSec.getStatement();
        db.setString(ps, 1, KeyHelper.getTokenObjectKey(key, 1));
        db.setString(ps, 2, KeyHelper.getTokenObjectKey(key, 2));
        db.setString(ps, 3, KeyHelper.getTokenObjectKey(key, 3));
        db.setString(ps, 4, KeyHelper.getTokenObjectKey(key, 4));
        db.setString(ps, 5, KeyHelper.getTokenObjectKey(key, 5));

        int ret = cDeleteRigheVoceSec.executeUpdate();
        if (ret > 0){
          return rc;
        }
      }
      catch (SQLException ex){ex.printStackTrace(Trace.excStream);}
    }
    return rc;
  }

  public boolean hasRigheSec(){
	//Fix 31451 inizio
    /*if (isKitGestito() && isGeneraRigaDettaglio()){
      return true;
    }*/
	if (!getRighe().isEmpty())
	   return true;
	//Fix 31451 fine
    return false;
  }
  //Fix 19817 Fine

  //Fix 20569 Inizio
  public void recuperaCostoRif(){
    if(getPrevComRiga() == null || getPrevComRiga().getTestata() == null)
      return;
    if(!isRigaValidaPerRecuperaCosto())
      return;
    try{
      PreventivoCommessaTestata testata = getPrevComRiga().getTestata();
      if(getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE || getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO){
        if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_LISTINO_ACQ ||
           testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTLST_RSRCOS)
        {
          recuperaCondizioniAcquisto(this);
          if(condAcq!=null)
            setCostoRifer(condAcq.getPrezzo());
        }
        else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTICOLO_COSTO){
          if(testata.getTipoCosto() != null){
            Integer idVersione = getIdVersione();
            if (idVersione == null) {
              idVersione = ModproEsplosione.determinaVersione(getIdAzienda(), getIdArticolo(), testata.getDataRiferimento());
              if (idVersione == null)
                idVersione = new Integer(1);
            }
            Integer idConfig = null;
            //29642 inizio
            /*if(getIdConfigurazione() != 0)
              idConfig = new Integer(getIdConfigurazione());*/
            if(getIdConfigurazione() != null)
                idConfig = getIdConfigurazione();
            //29642 fine
            ArticoloCosto articoloCosto = ArticoloCosto.elementWithKey(getIdAzienda(), getIdArticolo(), idVersione, idConfig, testata.getIdTipoCosto(), testata.getDataRiferimento());
            if (articoloCosto != null)
              setCostoRifer(articoloCosto.getCosto());
          }
        }
        else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_AMBIENTE_COSTO){
          if(testata.getIdAmbiente() != null){
            Integer idVersione = getIdVersione();
            if (idVersione == null) {
              idVersione = ModproEsplosione.determinaVersione(getIdAzienda(), getIdArticolo(), testata.getDataRiferimento());
              if (idVersione == null)
                idVersione = new Integer(1);
            }
            Integer idConfig = null;
            //29642 inizio
            /*if(getIdConfigurazione() != 0)
              idConfig = new Integer(getIdConfigurazione());*/
            if(getIdConfigurazione() != null)
                idConfig = getIdConfigurazione();
            //29642 fine
            Costo costo = getCostoArticoloDaAmbiente(testata.getIdStabilimento(), getIdArticolo(), idVersione, idConfig, testata.getIdCommessa(), testata.getIdAmbiente());
            if (costo != null)
              setCostoRifer(costo.getCostoRiferimento());
          }
        }
      }
      else if(getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA && getRisorsa() != null){
        if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_LISTINO_ACQ){
          BigDecimal costoRis = getCostoRisorsaDaListino(this, getRisorsa().getIdArticoloServizio());
          if(costoRis != null)
            setCostoRifer(costoRis);
        }
        else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTICOLO_COSTO ||
                testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTLST_RSRCOS)
        {
          if(testata.getTipoCosto() != null){
            RisorsaCosto risorsaCosto = RisorsaCosto.getRisorsaCosto(getRisorsa(), testata.getIdTipoCosto(), testata.getDataRiferimento());
            if (risorsaCosto != null)
              setCostoRifer(risorsaCosto.getCosto());
          }
        }
        else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_AMBIENTE_COSTO){
          if(testata.getIdAmbiente() != null){
            Costo costo = getCostoRisorsaDaAmbiente(testata, getRisorsa());
            if (costo != null)
              setCostoRifer(costo.getCostoRiferimento());
          }
        }
      }
    }
    catch(SQLException ex){ex.printStackTrace(Trace.excStream);}
  }

  public void recuperaCostoRifRigaSec(PreventivoCommessaVoce riga){
    if(riga == null || riga.getPrevComRiga() == null || riga.getPrevComRiga().getTestata() == null)
      return;
    try{
      PreventivoCommessaTestata testata = riga.getPrevComRiga().getTestata();
      if (riga.getIdRisorsa() == null) { //29882
    	  if(testata.getRepCosArt()==PreventivoCommessaTestata.REP_COS_LISTINO_ACQ ||
    		 testata.getRepCosArt()==PreventivoCommessaTestata.REP_COS_ARTLST_RSRCOS)
    	  {
    		  recuperaDatiAcquisto(riga);
    	  }
    	  else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTICOLO_COSTO){
    		  Integer idVersione = riga.getIdVersione();
    		  if (idVersione == null) {
    			  idVersione = ModproEsplosione.determinaVersione(getIdAzienda(), riga.getIdArticolo(), testata.getDataRiferimento());
    			  if (idVersione == null)
    				  idVersione = new Integer(1);
    		  }
    		  Integer idConfig = riga.getIdConfigurazione();//29642
    		  ArticoloCosto articoloCosto = ArticoloCosto.elementWithKey(getIdAzienda(), riga.getIdArticolo(), idVersione, idConfig, testata.getIdTipoCosto(), testata.getDataRiferimento());
    		  if (articoloCosto != null)
    			  riga.setCostoRifer(articoloCosto.getCosto());
    	  }
    	  else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_AMBIENTE_COSTO){
    		  Integer idVersione = riga.getIdVersione();
    		  if (idVersione == null) {
    			  idVersione = ModproEsplosione.determinaVersione(getIdAzienda(), riga.getIdArticolo(), testata.getDataRiferimento());
    			  if (idVersione == null)
    				  idVersione = new Integer(1);
    		  }
    		  Integer idConfig = riga.getIdConfigurazione();//29642
    		  Costo costo = getCostoArticoloDaAmbiente(testata.getIdStabilimento(), riga.getIdArticolo(), idVersione, idConfig, testata.getIdCommessa(), testata.getIdAmbiente());
    		  if (costo != null)
    			  riga.setCostoRifer(costo.getCostoRiferimento());
    	  }
      }//29882
      //29882 inizio
      else if(riga.getRisorsa() != null){
    	  if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_LISTINO_ACQ){
    		  BigDecimal costoRis = getCostoRisorsaDaListino(this, riga.getRisorsa().getIdArticoloServizio());
    		  if(costoRis != null)
    			  riga.setCostoRifer(costoRis);
    	  }
    	  else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTICOLO_COSTO ||
    			  testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_ARTLST_RSRCOS) {
    		  if(testata.getTipoCosto() != null){
    			  RisorsaCosto risorsaCosto = RisorsaCosto.getRisorsaCosto(riga.getRisorsa(), testata.getIdTipoCosto(), testata.getDataRiferimento());
    			  if (risorsaCosto != null)
    				  riga.setCostoRifer(risorsaCosto.getCosto());
    		  }
    	  }
    	  else if(testata.getRepCosArt() == PreventivoCommessaTestata.REP_COS_AMBIENTE_COSTO){
    		  if(testata.getIdAmbiente() != null){
    			  Costo costo = getCostoRisorsaDaAmbiente(testata, riga.getRisorsa());
    			  if (costo != null)
    				  riga.setCostoRifer(costo.getCostoRiferimento());
    		  }
    	  }
      }
      //29882 fine
    }
    catch(SQLException ex){ex.printStackTrace(Trace.excStream);}
  }

  public boolean isRigaValidaPerRecuperaCosto(){
    boolean isRigaValida = false;
    if(!isKitGestito() ||
       getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO ||
       getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA)
    {
      isRigaValida = true;
    }
    return isRigaValida;
  }

  public Costo getCostoArticoloDaAmbiente(String idStabilimento, String idArticolo, Integer idVersione, Integer idConfig, String idCommessa, String idAmbCosti) {
    String idConfigClause = "";
    String idCommessaClause = "";
    String idAmbienteCostiClause = "";

    if (idConfig != null)
      idConfigClause = CostoArticoloTM.R_CONFIGURAZIONE + " = " + idConfig;
    else
      idConfigClause = CostoArticoloTM.R_CONFIGURAZIONE + " is null ";

    if (idCommessa != null)
      idCommessaClause = CostoArticoloTM.R_COMMESSA + " = '" + idCommessa + "'";
    else
      idCommessaClause = CostoArticoloTM.R_COMMESSA + " is null ";

    if (idAmbCosti != null)
      idAmbienteCostiClause = CostoArticoloTM.ID_AMBIENTE + " = '" + idAmbCosti + "'";
    else
      idAmbienteCostiClause = CostoArticoloTM.ID_AMBIENTE + " is null ";

    String idAzienda = Azienda.getAziendaCorrente();
    String caluseWhere = CostoTM.ID_AZIENDA + " = '" + idAzienda + "' AND " +
                         CostoTM.R_STABILIMENTO + " = '" + idStabilimento + "' AND " +
                         CostoArticoloTM.R_ARTICOLO + " = '" + idArticolo + "' AND " +
                         CostoArticoloTM.R_VERSIONE + " = " + idVersione + " AND " +
                         idConfigClause + " AND " +
                         idCommessaClause + " AND " +
                         idAmbienteCostiClause + " AND " +
                         CostoTM.TIPOLOGIA + " = 'A'";
    try {
      Vector costiList = Costo.retrieveList(caluseWhere, "", true);
      if (costiList.size() > 0)
        return (Costo) costiList.get(0);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Costo getCostoRisorsaDaAmbiente(PreventivoCommessaTestata testata, Risorsa risorsa) {
    String idAmbienteCostiClause = "";
    String idStabilimento = testata.getIdStabilimento();
    String idAmbCosti = testata.getIdAmbiente();
    String idAzienda = getIdAzienda();
    char tipoRisorsa = risorsa.getTipoRisorsa();
    char livelloRsr = risorsa.getLivelloRisorsa();
    String idRisorsa = risorsa.getIdRisorsa();

    if (idAmbCosti != null) {
      idAmbienteCostiClause = CostoArticoloTM.ID_AMBIENTE + " = '" + idAmbCosti + "'";
    }
    else {
      idAmbienteCostiClause = CostoArticoloTM.ID_AMBIENTE + " is null ";
    }

    String caluseWhere = CostoTM.ID_AZIENDA + " = '" + idAzienda + "' AND " +
                         CostoRisorsaTM.R_TIPO_RISORSA + " = '" + tipoRisorsa + "' AND " +
                         CostoRisorsaTM.R_LIVELLO_RISORSA + " = '" + livelloRsr + "' AND " +
                         CostoRisorsaTM.R_RISORSA + " = '" + idRisorsa + "' AND " +
                         CostoTM.R_STABILIMENTO + " = '" + idStabilimento + "' AND " +
                         idAmbienteCostiClause + " AND " + CostoTM.TIPOLOGIA + " = 'R'";
    try {
      Vector costiList = Costo.retrieveList(caluseWhere, "", true);
      if (costiList.size() > 0) {
        return (Costo)costiList.get(0);
      }
      else {
        Risorsa risorsaP = getRisorsa().getRisorsaAppart();
        if (livelloRsr == Risorsa.MATRICOLA && risorsaP != null) {
          return getCostoRisorsaDaAmbiente(testata, risorsaP);
        }
      }
    }
    catch (Exception e) {e.printStackTrace();}
    return null;
  }

  public BigDecimal getCostoRisorsaDaListino(PreventivoCommessaVoce riga, String idArticoloRis) {
    BigDecimal ret = null;
    if(idArticoloRis == null) return ret;
    PreventivoCommessaTestata testata= riga.getPrevComRiga().getTestata();
    String dataRif = null;
    String dataConsegna = null;
    String versione = null;
    String qtaPrvUmPrm = null;
    DecimalType decType = new DecimalType();//31218
    if (testata.getDataRiferimento() != null)
    dataRif = testata.getDataRiferimento().toString();
   if (testata.getDataConsegRcs() != null)
    dataConsegna = testata.getDataConsegRcs().toString();

    Integer idVersione = ModproEsplosione.determinaVersione(getIdAzienda(), idArticoloRis, testata.getDataRiferimento());
    if (idVersione != null)
      versione = idVersione.toString();
    else
      versione = "1";


  if (riga.getQtaPrvUmPrm() != null)
    qtaPrvUmPrm = riga.getQtaPrvUmPrm().toString();

    if (testata.getListinoAcq() != null)
    {
      CondizioniDiAcquisto condAcqRis =
        RecuperaDatiAcquisto.getCondizioniAcquisto(
        "",
        testata.getIdListinoAcq(),
        "",
        idArticoloRis,
        String.valueOf(riga.getIdConfigurazione()),
        String.valueOf(riga.getIdUmVen()),
        //31218 inizio
        decType.objectToString(riga.getQtaPrvUmVen()),
        decType.objectToString(riga.getQtaPrvUmVen()),
        //qtaPrvUmPrm,
        //qtaPrvUmPrm,
        //31218 fine
        dataRif,
        dataConsegna,
        String.valueOf(riga.getIdUmVen()),
        testata.getListinoAcq().getIdValuta(),
        'N',
        null,
        "",
        "",
        "",
        versione, "",
        String.valueOf(riga.getIdUmSecMag()), String.valueOf(riga.getQtaPrvUmSec())
        );
        if(condAcqRis != null)
          ret = condAcqRis.getPrezzo();
    }

    return ret;
  }
  //Fix 20569 Fine
  //36730 inizio
  public ErrorMessage checkRSchemaCosto() {
	  if(getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA) {
		  if(getSchemaCosto() != null && getRisorsa().getSchemaCosto() != null && !getRisorsa().getIdSchemaCosto().equals(getRSchemaCosto())) {
			  return new ErrorMessage("THIP_TN908", getRSchemaCosto());
		  }		  
	  }
	  else {
		  if(getSchemaCosto() != null && getArticolo() != null && getArticolo().getClasseMerclg() != null && getArticolo().getClasseMerclg().getSchemaCosto() != null && !getArticolo().getClasseMerclg().getIdSchemaCosto().equals(getRSchemaCosto())) {
			  return new ErrorMessage("THIP_TN907", new String[] {getRSchemaCosto(), getArticolo().getIdClasseMerclg(), getIdArticolo()});
		  }	
	  }
	  return null;
  }
  //36730 fine
  //Fix 22355 Inizio
  public ErrorMessage checkRComponCosto(){
     //Fix 27506 inizio
    if(getRComponCosto() == null)
    {
       boolean tested = false;
       if(getPrevComRiga() != null && getPrevComRiga().getCommessa() != null)
       {
          if(getPrevComRiga().getCommessa().getTipoCommessa() != null)
          {
             TipoCommessa tc = getPrevComRiga().getCommessa().getTipoCommessa();
             if(tc.getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE)
                tested = true;
          }
       }

       if(!tested)
       {
          if(getPrevComRiga() != null && getPrevComRiga().getTestata() != null && getPrevComRiga().getTestata().getCommessa() != null)
          {
             if(getPrevComRiga().getTestata().getCommessa().getTipoCommessa() != null)
             {
                TipoCommessa tc = getPrevComRiga().getTestata().getCommessa().getTipoCommessa();
                if(tc.getNaturaCommessa() == TipoCommessa.NATURA_CMM__GESTIONALE)
                   tested = true;
             }
          }
       }
       //Fix 27506 fine

       if(tested)
          return new ErrorMessage("BAS0000000");
    }
    //36730 inizio
    else {
  	  if(getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA) {
		  if(getRComponCosto() != null && getRisorsa().getComponenteCosto() != null && !getRisorsa().getIdComponenteCosto().equals(getRComponCosto())) {
			  return new ErrorMessage("THIP_TN906", getRComponCosto());
		  }		  
	  }
	  else {
		  if(getRComponCosto() != null && getArticolo() != null && getArticolo().getClasseMerclg() != null && getArticolo().getClasseMerclg().getComponenteCosto() != null && !getArticolo().getClasseMerclg().getIdComponenteCosto().equals(getRComponCosto())) {
			  return new ErrorMessage("THIP_TN905", new String[] {getRComponCosto(), getArticolo().getIdClasseMerclg(), getIdArticolo()});
		  }	
	  }
    }
    //36730 fine
    return null;
  }
  //Fix 22355 Fine

  //Fix 27506 inizio
  public boolean isPropagazioneRicalcoloValori()
  {
     return propagazioneRicalcoloValori;
  }


  public void setPropagazioneRicalcoloValori(boolean propagazioneRicalcoloValori)
  {
     this.propagazioneRicalcoloValori = propagazioneRicalcoloValori;
  }
  //Fix 27506 fine

  //29032 inizio
  public CondizioniDiVendita getCondizioniVendita(PreventivoCommessaVoce voce) {
	  PreventivoCommessaTestata testata = voce.getPrevComRiga().getTestata();
	  if(testata.getRepPrezzoArt() != PreventivoCommessaTestata.REP_PREZZO_DA_LST_VEND){
		  return null;
	  }
	  BigDecimal qtaSecMag = voce.getQtaPrvUmSec();
	  CondizioniDiVendita condVendita = null;
	  String idUMSecMag = null;
	  try {
		  String idUmPrmMag = voce.getIdUmPrmMag();
		  if (voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO) {
			  idUMSecMag = voce.getIdUmSecMag();
		  }

		  Articolo articolo = voce.getArticolo();
		  if(voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA) {
			  articolo = voce.getRisorsa().getArticoloServizio();
		  }

		  if (articolo != null) {
			  ClienteVendita cliente = testata.getCliente();

			  BigDecimal quantitaVendita = voce.getQtaPrvUmVen();
			  BigDecimal quantitaMag = voce.getQtaPrvUmPrm();
			  UnitaMisura umMag = UnitaMisura.getUM(idUmPrmMag);
			  UnitaMisura umSecMag = null;
			  BigDecimal quantSecMagazzino = null;
			  if (idUMSecMag != null)
				  umSecMag = UnitaMisura.getUM(idUMSecMag);
			  if (umSecMag != null && qtaSecMag != null) {
				  quantSecMagazzino = new BigDecimal(((qtaSecMag)).doubleValue());
			  }

			  RicercaCondizioniDiVendita ricerca = (RicercaCondizioniDiVendita)Factory.createObject(RicercaCondizioniDiVendita.class);
			  condVendita = ricerca.ricercaCondizioniDiVendita(voce.getIdAzienda(), testata.getListinoVen(), cliente, articolo, voce.getConfigurazione(), voce.getUMVen(), quantitaVendita, new BigDecimal(0.0),
					  										   null, testata.getDataRiferimento(), null, null,
					  										   umMag, quantitaMag, testata.getValuta(), false,
					  										   new BigDecimal(0), new BigDecimal(0), null,
					  										   voce.getArticoloVersione(), null,
					  										   umSecMag, quantSecMagazzino);

			  if (condVendita != null && cliente != null) {
				  BigDecimal prezzo = condVendita.getPrezzo();
				  if (prezzo == null || prezzo.equals(new BigDecimal(0.0))) {
					  ListinoVendita listinoCliente = cliente.getListino();
					  ListinoVendita listinoAlternativo = cliente.getListinoAlternativo();
					  if (listinoCliente != null && listinoAlternativo != null && listinoCliente.equals(listinoAlternativo)) {
						  condVendita = ricerca.ricercaCondizioniDiVendita(voce.getIdAzienda(), listinoAlternativo, cliente, articolo, voce.getConfigurazione(), voce.getUMVen(), quantitaVendita,
								  										   new BigDecimal(0.0), null, testata.getDataFineVal(), null, null,
								  										   umMag, quantitaMag, testata.getValuta(),
								  										   new BigDecimal(0), new BigDecimal(0), null,
								  										   voce.getArticoloVersione(), null, umSecMag, quantSecMagazzino);
					  }
				  }
			  }
		  }
	  }
	  catch (Exception ex) {
		  ex.printStackTrace();
	  }
	  return condVendita;
  }

  public BigDecimal calcoloPrezziDaMarkup(PreventivoCommessaVoce voce){
	  //Fix 30934 --inizio
	  /*BigDecimal perc = voce.getMarkup().divide(new BigDecimal(100.0), 6, BigDecimal.ROUND_HALF_UP);
	  BigDecimal prezzo = voce.getCostoRifer().add(voce.getCostoRifer().multiply(perc));*/
	  BigDecimal prezzo = voce.getCostoRifer().multiply(voce.getMarkup());
	  prezzo = prezzo.divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_UP);
	  prezzo = voce.getCostoRifer().add(prezzo);
	  //Fix 30934 --fine
	  return prezzo;
  }

  public boolean isPropagazioneMarkup()
  {
     return propagazioneMarkup;
  }


  public void setPropagazioneMarkup(boolean propagazioneMarkup)
  {
     this.propagazioneMarkup = propagazioneMarkup;
  }
  //29032 fine
  //29166 inizio
  public String getKeyForTree() {
	    String keyForTree = "";
	    if(getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA){
	    	if(getRisorsa() != null){
	    		keyForTree = getTipoRisorsa() + "/" + getLivelloRisorsa() + "/" + getIdRisorsa();
	    	}
	    }
	    else{
	    	if(getArticolo() != null){
	    		keyForTree = getIdArticolo();
	    		if (PersDatiGen.getCurrentPersDatiGen().isGestioneTessile()){
	    			keyForTree = getArticolo().getIdProdottoFormattato();
	    		}
	    		if (getIdVersione() != null && PersDatiGen.getCurrentPersDatiGen().getGesVersioni() && getArticolo().getGesVersioni()){
	    			keyForTree += " / " + getIdVersione();
	    		}
	    	}
	    }
	    return keyForTree;
  }

  public int getLivello() {
    return getPrevComRiga().getLivello() + 1;
  }

  public String getDescrizioneForTree(){
	  return getDescrizioneArticolo();
  }

  public static final String RISORSE = "it.thera.thip.base.commessa.resources.PreventivoCommessaRiga";

  public static String TIPO_ARTICOLO = ResourceLoader.getString(RISORSE, "TipoArticolo");
  public static String TIPO_RISORSA_U = ResourceLoader.getString(RISORSE, "TipoRisU");
  public static String TIPO_RISORSA_M = ResourceLoader.getString(RISORSE, "TipoRisM");
  public static String TIPO_DETTAGLIO = ResourceLoader.getString(RISORSE, "TipoDettaglio");

  public String getTipoRigaForTree()  {
    if (getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE){
    	if(getIdSubRigavPrv() == 0)
    		return getDescrizioneEnum("TipoRigaPrev", PreventivoCommessaVoce.TP_RIG_VOCE);
    	else
    		return TIPO_DETTAGLIO;
    }
    else if (getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO)
      return TIPO_ARTICOLO;
    //29731 inizo
    /*else if (getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA && getTipoRisorsa() == Risorsa.RISORSE_UMANE)
      return TIPO_RISORSA_U;
    else if (getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA && getTipoRisorsa() == Risorsa.MACCHINE)
      return TIPO_RISORSA_M;
    */
    else if (getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA){
    	if(getIdSubRigavPrv() != 0){
    		return TIPO_DETTAGLIO;
    	}
    	else{
    		if (getTipoRisorsa() == Risorsa.RISORSE_UMANE){
    			return TIPO_RISORSA_U;
    		}
    		else if (getTipoRisorsa() == Risorsa.MACCHINE){
    			return TIPO_RISORSA_M;
    		}
    	}
    }
    //29731 fine
    return "";
  }

  public String getDescrizioneEnum(String enumRef, char value) {
    return new com.thera.thermfw.type.EnumType(enumRef).descriptionFromValue(String.valueOf(value));
  }

  public String getIdComponCostoForTree(){
	  return getRComponCosto();
  }

  public String getIdUmVenForTree() {
	  String idUm = getIdUmVen();
	  if (getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA)
		  idUm = getIdUmPrmMag();
	  return idUm;
  }

  public BigDecimal getQtaPrvUmPrmForTree() {
	  return getQtaPrvUmVen();
  }

  public BigDecimal getPrezzoTotaleForTree() {
	  return getPrezzoTotale();
  }

  public BigDecimal getCostoRiferForTree() {
	  return getCostoRifer();
  }

  public BigDecimal getMarckupForTree(){
	  return getMarkup();
  }

  public BigDecimal getVlrLivelloForTree() {
	  return null;
  }

  public BigDecimal getCosLivelloForTree() {
	  return null;
  }

  public BigDecimal getMdcLivelloForTree() {
	  return null;
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
	  return getPercentuale();
  }

  public List getListaUMRiferimento() {
	  List list = new ArrayList();
	  Articolo articolo = getArticolo();
	  Risorsa risorsa = getRisorsa();
	  if (articolo != null) {
		  list = new ArrayList(articolo.getArticoloDatiVendita().getForcedUMSecondarie());
		  if(!DocumentoBaseRiga.isUMNellaLista(this.getUMVen(), list)){
			  list.add(this.getUMVen());
		  }
	  }
	  else{
		  try {
			list = UnitaMisura.retrieveList("", "", false);
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		}
	  }

	  return list;
  }
  //29166 fine

  // 29529 inizio
  public void setWarningList(List warnings) {
      iWarningList = warnings;
  }

  public List getWarningList() {
      return iWarningList;
  }

  public void addWarning(ErrorMessage errormessage) {
      if (errormessage != null) {
          if (getWarningList() == null) {
              setWarningList(new ArrayList());
          }
          getWarningList().add(errormessage);
      }
  }

  //29529 fine

  //29642 inizio
  public BigDecimal calcoloMarkup(){
	  BigDecimal markup = ZERO;
	  if(getPrevComRiga() != null && getPrevComRiga().getTestata() != null && getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
		  if(getPrezzo() != null && getPrezzo().compareTo(ZERO) > 0){
			  if(getCostoRifer() != null && getCostoRifer().compareTo(ZERO) > 0){
				  markup = getPrezzo().subtract(getCostoRifer());
				  markup = markup.multiply(new BigDecimal(100));//29882
				  //markup = markup.divide(getCostoRifer(), 2, BigDecimal.ROUND_HALF_UP); //Fix 30934
				  markup = markup.divide(getCostoRifer(), 6, BigDecimal.ROUND_HALF_UP); //Fix 30934
				  //markup = markup.multiply(new BigDecimal(100));//29882
			  }
		  }
	  }
	  return markup;
  }
  //29642 fine

  //29731 inizio
  public String getDescrArticoloForTree(){
	  if(getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE || getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO){
		  if(getArticolo() != null){
			  return getArticolo().getDescrizioneArticoloNLS().getDescrizione();
		  }
	  }
	  return "";
  }

  public String getDescrCommessaForTree(){
	  return "";
  }
  //29731 fine

  // fix 30702
  public boolean isGestioneConMarkup() {
	  return getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP;
  }
  // fine fix 30702


  //Fix 33626 - inizio
  protected void impostaDatiRigaSecPers(PreventivoCommessaVoce rigaSec) {
  }


  protected void impostaDatiRigaSecDaCondVenditaPers(PreventivoCommessaVoce rigaSec) {
  }


  protected boolean areCondizioniAggiornaRigheSec() {
	  return getOldQtaPrvUmVen() != null && getQtaPrvUmVen() != null && getQtaPrvUmVen().compareTo(getOldQtaPrvUmVen()) != 0;
  }


  protected void aggiornaDatiRigaSecPers(PreventivoCommessaVoce rigaSec) {

  }
  //Fix 33626 - fine

}

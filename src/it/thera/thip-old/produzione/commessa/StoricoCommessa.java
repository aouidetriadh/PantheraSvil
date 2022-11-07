/*
 * @(#)StoricoCommessa.java
 */

/**
 * StoricoCommessa
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Mekki 12/04/2005 at 15:21:12
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 12/04/2005    Wizard     Codice generato da Wizard
 * 10416   03/02/2009    DBot      Modifica per consuntivazione documenti servizio
 * 31513   21/01/2021    RA        Prima di salva oggetto sistemare il scale di iCostoUnitario, iCostoTotale ,iCostoUnitarioOrigine e iValoreRiga
 * 33950   06/10/2021    RA		   Aggiunto metodo getDettagli
 */
package it.thera.thip.produzione.commessa;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thera.thermfw.base.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;

public class StoricoCommessa extends StoricoCommessaPO {

  public static final String ID_PROGR_NUM_ID = "STORICO_COMMESSE";

  public void setTipoRigaOrigine(char tipoRigaOrigine) {
    this.iTipoRigaOrigine = tipoRigaOrigine;
    setIdCauOrgTes(getIdCauOrgTes());
    setIdCauOrgRig(getIdCauOrgRig());
    setDirty();
  }

  public void setIdCauOrgTes(String idCausale) {
    this.iIdCauOrgTes = idCausale;
    String key = null;
    if (isRigaAcquisto(getTipoRigaOrigine())) {
      key = iCausaleTestataAcq.getKey();
      iCausaleTestataAcq.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (isRigaProduzione(getTipoRigaOrigine())) {
      key = iCausaleTestataPrd.getKey();
      iCausaleTestataPrd.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == VENDITA) {
      key = iCausaleTestataVen.getKey();
      iCausaleTestataVen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == GENERICO) {
      key = iCausaleTestataGen.getKey();
      iCausaleTestataGen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == TRASFERIMENTO_MAGAZZINO) {
      key = iCausaleTestataTrasMag.getKey();
      iCausaleTestataTrasMag.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    //Fix 10416 inizio
    if(isRigaServizio(getTipoRigaOrigine())) {
       key = iCausaleTestataSrv.getKey();
       iCausaleTestataSrv.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
     }
    //Fix 10416 fine
    setDirty();
  }

  public void setIdCauOrgRig(String idCausale) {
    this.iIdCauOrgRig = idCausale;
    String key = null;
    if (isRigaAcquisto(getTipoRigaOrigine())) {
      key = iCausaleRigaAcq.getKey();
      iCausaleRigaAcq.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == PRODUZIONE_MATERIALE) {
      key = iCausaleRigaPrdMat.getKey();
      iCausaleRigaPrdMat.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == PRODUZIONE_PRODOTTO) {
      key = iCausaleRigaPrdVrs.getKey();
      iCausaleRigaPrdVrs.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == PRODUZIONE_RISORSA) {
      key = iCausaleRigaPrdRsr.getKey();
      iCausaleRigaPrdRsr.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }

    if (getTipoRigaOrigine() == VENDITA) {
      key = iCausaleRigaVen.getKey();
      iCausaleRigaVen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == GENERICO) {
      key = iCausaleRigaGen.getKey();
      iCausaleRigaGen.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    if (getTipoRigaOrigine() == TRASFERIMENTO_MAGAZZINO) {
      key = iCausaleRigaTrasMag.getKey();
      iCausaleRigaTrasMag.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
    }
    //Fix 10416 inizio
    if (getTipoRigaOrigine() == SERVIZIO_MATERIALE) {
       key = iCausaleRigaSrvMat.getKey();
       iCausaleRigaSrvMat.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
     }
     if (getTipoRigaOrigine() == SERVIZIO_PRODOTTO) {
       key = iCausaleRigaSrvVrs.getKey();
       iCausaleRigaSrvVrs.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
     }
     if (getTipoRigaOrigine() == SERVIZIO_RISORSA) {
       key = iCausaleRigaSrvRsr.getKey();
       iCausaleRigaSrvRsr.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCausale));
     }
     //Fix 10416 fine

    setDirty();
  }


  public String getIdEsternoConfig() {
    return iConfigurazione.getIdEsternoConfig();
  }

  public void setIdEsternoConfig(String idEsternoConfig) {
    iConfigurazione.setIdEsternoConfig(idEsternoConfig);
  }

  public String getIdEsternoConfigPrd() {
    return iConfigurazionePrd.getIdEsternoConfig();
  }

  public void setIdEsternoConfigPrd(String idEsternoConfig) {
    iConfigurazionePrd.setIdEsternoConfig(idEsternoConfig);
  }

  /**
   * checkDelete
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete() {
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
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public String getTableNLSName() {
    return SystemParam.getSchema("THIP20") + "STORICO_CMM_L";
  }

  public String getUmPrmMagDescrizione() {
    String uMDescr = "";
    if (getUmPrmMag() != null)
      uMDescr = getIdUmPrmMag() + " - " + getUmPrmMag().getDescrizione().getDescrizione();
    return uMDescr;
  }

  public String getUmSecMagDescrizione() {
    String uMDescr = "";
    if (getUmSecMag() != null)
      uMDescr = getIdUmSecMag() + " - " + getUmSecMag().getDescrizione().getDescrizione();
    return uMDescr;
  }

  public String getUmAcqVenDescrizione() {
    String uMDescr = "";
    if (getUmAcqVen() != null)
      uMDescr = getIdUMAcqVen() + " - " + getUmAcqVen().getDescrizione().getDescrizione();
    return uMDescr;
  }

  public java.math.BigDecimal getQuantita() {
    return getQuantitaUMPrm();
  }

  public java.math.BigDecimal getCosto() {
    return getCostoTotale();
  }

  private static boolean isRigaAcquisto(char tiporigaOrigine)
  {
    return ((tiporigaOrigine == ACQUISTO) ||
            (tiporigaOrigine == LAVORAZIONE_ESTERNA_MATERIALE) ||
            (tiporigaOrigine == LAVORAZIONE_ESTERNA_PRODOTTO));
  }

  private static boolean isRigaProduzione(char tiporigaOrigine)
  {
    return ((tiporigaOrigine == PRODUZIONE_MATERIALE) ||
            (tiporigaOrigine == PRODUZIONE_PRODOTTO) ||
            (tiporigaOrigine == PRODUZIONE_RISORSA));
  }

  //Fix 10416 inizio
  private static boolean isRigaServizio(char tiporigaOrigine)
  {
    return (tiporigaOrigine == SERVIZIO_MATERIALE ||
            tiporigaOrigine == SERVIZIO_PRODOTTO ||
            tiporigaOrigine == SERVIZIO_SPESA ||
            tiporigaOrigine == SERVIZIO_RISORSA);
  }
  //Fix 10416 fine

  public String getCodConfig()
  {
    if (super.getCodConfig() == null)
    {
      setCodConfig(getIdEsternoConfig());
    }
    return super.getCodConfig();
  }
  
  //31513 inizio
  public int save() throws SQLException
  {
	  beforeSave();
	  return super.save();	  
  }
  
  public void beforeSave() {
	  sistemaScale();
  }
  
  public void sistemaScale() {
	  BigDecimal val = null;
	  if(getCostoUnitario() != null) {
		  val = getCostoUnitario();
		  val = val.setScale(6, BigDecimal.ROUND_HALF_UP);
		  setCostoUnitario(val);
	  }
	  
	  if(getCostoUnitarioOrigine() != null) {
		  val = getCostoUnitarioOrigine();
		  val = val.setScale(6, BigDecimal.ROUND_HALF_UP);
		  setCostoUnitarioOrigine(val);
	  }
	  
	  if(getCostoTotale() != null) {
		  val = getCostoTotale();
		  val = val.setScale(6, BigDecimal.ROUND_HALF_UP);
		  setCostoTotale(val);
	  }
	  
	  if(getValoreRiga() != null) {
		  val = getValoreRiga();
		  val = val.setScale(2, BigDecimal.ROUND_HALF_UP);
		  setValoreRiga(val);
	  }
  }
  //31513 fine
  //33950 inizio
  public List getDettagli(char tipoDettaglio) {
	  return getDettagli(tipoDettaglio, getStoricoCommessaDet());
  }

  public List getDettagli(char tipoDettaglio, List storicoCommessaDet) {
	  List ret = new ArrayList();
	  Iterator dettagli = storicoCommessaDet.iterator();
	  while(dettagli.hasNext()) {
		  StoricoCommessaDet dettaglio = (StoricoCommessaDet)dettagli.next();
		  if(dettaglio.getTipoDettaglio() == tipoDettaglio)
			  ret.add(dettaglio);
	  }

	  return ret;
  }
  //33950 fine
}


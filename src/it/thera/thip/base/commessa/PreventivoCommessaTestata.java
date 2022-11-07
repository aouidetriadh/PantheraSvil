/*
 * @(#)PreventivoCommessaTestata.java
 */

/**
 * PreventivoCommessaTestata
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 14/09/2011 at 16:39:51
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 14/09/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 15848     05/03/2012    FM       Correzione
 * 19818     23/05/2014    AA       Valorizzazione della lingua
 * 20406     26/09/2014    TF       Correzione preventivo commessa
 * 20569     15/07/2015    AA       Correzione preventivo commessa
 * 22273     07/10/2015    AA       Varie correzioni
 * 27506     30/05/2018    DBot     Correzioni a preventivo
 * 29032	 25/03/2019	   RA		Revisione preventivi di commessa
 * 29166	 01/04/2019	   RA       Revisione preventivi di commessa
 * 29529 	 09/07/2019	   RA	    Aggiunto gestione Markup articolo, uomo e macchina
 * 29642     17/07/2019	   RA		Revisione preventivi di commessa
 * 29672     23/07/2019	   RA		Revisione preventivi di commessa
 * 29731	 20/08/2019	   RA       Rivisione Preventivo commessa : varie modifiche
 * 30327     06/12/2019    DB       Gancio personalizzazioni
 * 30762     20/02/2020    DB       Evitare quando salvo direttamente la testata di andare a fare tutti i ricaloli sulle righe
 * 31162     28/04/2020   Mekki     Corrggere problemi di performance (decine di secondi) quando si modifica la quantità o si eliminano delle voci.
 * 31227     15/05/2020   Mekki     Velocizzare la cancellazione delle righe all'interno del preventivo
 * 31374     10/06/2020    DB       Favorire personalizzazioni
 * 31606     14/07/2020    DB       Gancio personalizzazioni in copia
 * 31639     21/07/2020   Mekki     Togliere la chiamata di completaBO in copia
 * 31854	 25/09/2020	   RA		Correto problema di carica righe
 * 32048	 16/10/2020	   RA		Corretto copia preventivo
 * 33022	 01/03/2021	   RA		Corretto copia preventivo
 * 33048     02/03/2021    RA   	Aggiunto metodo getAllRigheCommessa
 * 33229     01/04/2021    RA		Corretto valorizzazione ListinoVendita
 * 33626     07/07/2021    Jackal	Cambiata visibilità metodo getClienteTemp
 * 34426     12/10/2021    Mekki    Copia commenti
 * 34819     07/12/2021    Mekki    Copia documento
 * 36924     04/11/2022    RA		Corretta copia Documento con Data consegna specifica
 */
package it.thera.thip.base.commessa;

import java.math.*;
import java.sql.*;
import java.util.*;
import com.thera.thermfw.base.*;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;

import it.thera.thip.base.articolo.ArticoloDatiIdent;
import it.thera.thip.base.articolo.ArticoloDatiVendita;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.cliente.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.base.listini.*;
import it.thera.thip.base.partner.*;
import it.thera.thip.base.risorse.Risorsa;
import it.thera.thip.base.wpu.admin.*;
import it.thera.thip.cs.DatiComuniEstesi;//29642
import it.thera.thip.vendite.generaleVE.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class PreventivoCommessaTestata
  extends PreventivoCommessaTestataPO
{

  //Numeratore
  public static final String NUMERATORE = "PREV_COM";
  private boolean iSalvaRighe = true;
  public boolean iCopia = false;//Fix 20406

  protected boolean propagazioneRicalcoloValori = true;//Fix 27506
  //29529 inizio
  protected BigDecimal iOldMarkupArticolo = null;
  protected BigDecimal iOldMarkupUomo = null;
  protected BigDecimal iOldMarkupMacchina = null;
  //29529 fine
  protected boolean iSalvoDaClipboard = false;//32048
  // fix 30762
  protected boolean iSalvoDaSola = false;
  
  protected boolean iAbilitaCopiaCommenti = true; //Fix 34426

  public boolean isSalvoDaSola() {
	  return iSalvoDaSola;
  }

  public void setSalvoDaSola(boolean salvo) {
	  iSalvoDaSola = salvo;
  }
  // fine fix 30762

  public PreventivoCommessaTestata()
  {
    ivNumeratoreHandler = (NumeratoreHandler)Factory.createObject(NumeratoreHandler.class);
    ivNumeratoreHandler.setIdTipoDocumento(NUMERATORE);
    ivNumeratoreHandler.setOwner(this);
  }

  public int save() throws SQLException
  {
    // System.out.println("SAV£:" + KeyHelper.formatKeyString(getKey()) + " PA:" + isPropagazioneRicalcoloValori());
    beforeSaveForNumeratore();
    this.setSalvoDaSola(true);  // fix 30762
    //29529 inizio
    if (this.isGestioneMarkup()) {     // fix 31374
    //if( getRepPrezzoArt() == REP_PREZZO_COSTO_MARKUP){  // fine fix 31374
    	gestioneMarkupRighe();
    	//setSalvaRighe(true); //Fix 31162
    }
    //29529 fine

    boolean oggettoNuovo = !isOnDB();
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
    //Fix 20406 inizio
    //if (oggettoNuovo && rc > 0)

    //Fix 33626 - inizio
    if (rc > ErrorCodes.NO_ROWS_UPDATED) {
	    int rcPers = gestioneRigheOnSavePers();
	    rc = rcPers >= ErrorCodes.NO_ROWS_UPDATED ? rc + rcPers : rcPers;
    }
    //Fix 33626 - fine

    if (oggettoNuovo && rc > 0 && !isCopia())
    //Fix 20406 fine
    {
      try
      {
        generaRigheCommesse();
      }
      catch (SQLException ex)
      {
        ex.printStackTrace(Trace.excStream);
      }
    }
    //Fix 30762
    //33022 inizio
    /*
    // Nel caso di copia devo ripulire i riferimenti delle righe
    if(this.isCopia() && this.getRighe()!=null && !this.getRighe().isEmpty()) {
    	PreventivoCommessaRiga riga = (PreventivoCommessaRiga)this.getRighe().get(0);
    	if(riga.getOldRigaPrev()!=null) {
    		Iterator iter = this.getRighe().iterator();
    		while(iter.hasNext()) {
    			riga = (PreventivoCommessaRiga)iter.next();
    			riga.setOldRigaPrev(null);
    		}
    	}
    }
    */
    //33022 fine
    // fine fix 30762
    if (!isCopia()) //Fix 31227
     ricalcolaValori();//Fix 27506

    return rc;
  }

  /**
   * generaRigheCommesse
   */
  public int generaRigheCommesse() throws SQLException
  {
    if (verificaStrutturaCommessa(getCommessa()))
    {
      if (getIdCommessa() != null && !getIdCommessa().equals(""))
        if (!getCommessa().getSottocommesse().isEmpty())
          caricaStrutturaCommessa(true);
    }
    return 0;

  }

  /**
   * verificaStrutturaCommessa
   *
   * @return boolean
   */
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

  public void caricaStrutturaCommessa(boolean flag) throws SQLException
  {
    Iterator ite = getCommessa().getSottocommesse().iterator();
    PreventivoCommessaRiga rigaPrincipale = generaRigaCommessa(getCommessa(), null);
    while (ite.hasNext())
    {
      Commessa sottoCom = (Commessa)ite.next();
      generaRigheCommesse(sottoCom, rigaPrincipale);
    }
  }

  public int generaRigheCommesse(Commessa commessa, PreventivoCommessaRiga rigaAppObj) throws SQLException
  {
    if (getIdCommessa() != null && !getIdCommessa().equals(""))
    {
      return generaRighe(commessa, rigaAppObj);
    }
    return 0;

  }

  public int generaRighe(Commessa commessa, PreventivoCommessaRiga rigaApp) throws SQLException
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

  public static BigDecimal recuperaCambio(String idValuta, java.sql.Date data)
  {
    BigDecimal cambio;
    if (idValuta == null || idValuta.equals(""))
    {
      return new BigDecimal("0.0");
    }

    PersDatiGen pdg = PersDatiGen.getCurrentPersDatiGen();
    if (pdg == null)
    {
      Trace.println("ATTENZIONE: PersDatiGen non è valorizzato");

      Trace.println("Restituito cambio di default pari a 1");
      return new BigDecimal("1.0");
    }

    String idValutaPrm = pdg.getIdValutaPrimaria();

    if (idValutaPrm.equals(idValuta))
    {
      cambio = new BigDecimal("1.0");
    }
    else
    {

      cambio = Cambio.getCambioValute(data, idValuta, idValutaPrm);
      if (cambio == null || cambio.compareTo(new BigDecimal("0")) == 0)
        cambio = new BigDecimal("0");
    }
    return cambio;
  }

  public static synchronized int getNumeroNuovaRiga(PreventivoCommessaTestata testata, int passo, CachedStatement cSelectMaxNumeroRiga)
  {
    try
    {
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(cSelectMaxNumeroRiga.getStatement(), 1, testata.getIdAzienda());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 2, testata.getIdAnnoPrevc());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 3, testata.getIdNumeroPrevc());
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

  public static synchronized int getNumeroNuovaRiga(String keyTestata, int passo, CachedStatement cSelectMaxNumeroRiga)
  {
    try
    {
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(cSelectMaxNumeroRiga.getStatement(), 1, KeyHelper.getTokenObjectKey(keyTestata, 1));
      db.setString(cSelectMaxNumeroRiga.getStatement(), 2, KeyHelper.getTokenObjectKey(keyTestata, 2));
      db.setString(cSelectMaxNumeroRiga.getStatement(), 3, KeyHelper.getTokenObjectKey(keyTestata, 3));
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

  public static synchronized int getNumeroNuovaRigaSottoCom(PreventivoCommessaRiga rigaAppa, int passo, CachedStatement cSelectMaxNumeroRiga)
  {
    try
    {
      Database db = ConnectionManager.getCurrentDatabase();
      db.setString(cSelectMaxNumeroRiga.getStatement(), 1, rigaAppa.getIdAzienda());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 2, rigaAppa.getIdAnnoPrevc());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 3, rigaAppa.getIdNumeroPrevc());
      db.setString(cSelectMaxNumeroRiga.getStatement(), 4, String.valueOf(rigaAppa.getIdRigacPrv()));
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

  /**
   * generaRiga
   *
   * @param commessa Commessa
   */
  public PreventivoCommessaRiga generaRigaCommessa(Commessa commessa, PreventivoCommessaRiga rigaApp) throws SQLException
  {
    PreventivoCommessaRiga riga = (PreventivoCommessaRiga)Factory.createObject(PreventivoCommessaRiga.class);
    riga.setTestata(this);
    riga.setCommessa(commessa);
    if (rigaApp != null)
      riga.initSequenza(rigaApp);
    else
      riga.setSequenzaRiga(getNumeroNuovaRiga(this, 10, PreventivoCommessaRiga.cSelectMaxSequenzaRigheCom));
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
    riga.setIdArticolo(commessa.getIdArticolo());
    riga.setIdVersione(commessa.getIdVersione());
    riga.setIdConfigurazione(commessa.getIdConfigurazione());
    riga.setIdUmPrmMag(commessa.getIdUmPrmMag());
    if (commessa.getIdUmPrmMag() == null && commessa.getArticolo() != null && commessa.getIdArticolo() != null && commessa.getArticolo().getIdUMPrmMag() != null)
      riga.setIdUmPrmMag(commessa.getArticolo().getIdUMPrmMag());

    riga.setQtaUmPrm(new BigDecimal("1"));
    riga.setVlrLivello(commessa.getValoreOrdine());
    riga.setVlrTotale(commessa.getValoreTotaleOrdine());
    riga.setNota(commessa.getNote());
    riga.save();
    return riga;
  }

  public boolean beforeSaveForNumeratore()
  {
    if (!isOnDB())
    {
      getCommenti().setKey(null);
    }
    if (!isOnDB() && getNumeratoreHandler() != null)
    {
      try
      {
        setIdNumeroPrevc(getNumeratoreHandler().getIdProgressivo());
        setIdAnnoPrevc(getNumeratoreHandler().getAnno());
        setNumeroPrevcFormattato(getNumeratoreHandler().getIdProgressivoFormattato());
      }
      catch (it.thera.thip.base.generale.NumeratoreException ne)
      {
        this.exception = ne;
        return false;
      }
    }
    return true;
  }

  protected CommentHandler getCommentiIntestatario()
  {
    if (getCliente() != null)
      return getCliente().getCommenti();
    return new CommentHandler();
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
      {
        break;
      }
    }
    CommentService commSrv = new CommentService();
    try
    {
      commSrv.commentUseManagementGeneral(getCommentiIntestatario(), this.getCommenti(), e, task);
    }
    catch (Exception ex)
    {
      Trace.println("Eccezione nel metodo copiaCommenti() della classe " + getClass().getName() + ": " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  protected Entity getEntity()
  {
    try
    {
      return Entity.elementWithKey("PrevComTestata", Entity.NO_LOCK);
    }
    catch (Exception ex)
    {
      return null;
    }
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

  public void completaBO()
  {
    completaDatiCausale();
    completaDati();
    if (getDataPrevc() != null)
    {
      completaDataRiferimento(getDataPrevc());//15848
      completaDateConsegna(getDataPrevc());
    }
  }

  /**
   * completaDataRiferimento
   *
   * @param date Date 15848
   */
  public void completaDataRiferimento(java.sql.Date date)
  {
    setDataRiferimento(getDataPrevc());
    setDataFineVal(getDataPrevc());
    setSettFineVal(String.valueOf(TimeUtils.getISOWeek(getDataPrevc())[1]));
    setDataFineValc(getDataPrevc());
    setSettFineValc(String.valueOf(TimeUtils.getISOWeek(getDataPrevc())[1]));
  }

  public void completaDatiCausale()
  {
    PersDatiVen persDatiVen = PersDatiVen.getCurrentPersDatiVen();
    CausalePreventivoCommessa causale = getCausaleTemp(getIdCauPrevc());
    if (causale != null)
    {
      CompletaDatiWorkFlow(causale.getWorkflow());
      if (causale.getIdMagazzino() != null && !causale.getIdMagazzino().equals(""))
      {
        setIdMagazzino(causale.getIdMagazzino());
      }
      else
      {
        if (persDatiVen != null)
          setIdMagazzino(persDatiVen.getRMagazzino());
      }
      if (causale.getIdAmbienteCosti() != null && !causale.getIdAmbienteCosti().equals(""))
      {
        setIdAmbiente(causale.getIdAmbienteCosti());
      }
      setRepCosArt(causale.getRepCosArt());
      if (causale.getIdListinoAcq() != null && !causale.getIdListinoAcq().equals(""))
        setIdListinoAcq(causale.getIdListinoAcq());
      //Fix 22273 Inizio
      if(getRepCosArt() == REP_COS_ARTICOLO_COSTO || getRepCosArt() == REP_COS_ARTLST_RSRCOS) //fIX 27506
        setIdTipoCosto(causale.getIdTipoCosto());
      //Fix 22273 Fine
      //29642 inizio
      setRepPrezzoArt(causale.getRepPrezzoArt());
      if(causale.getIdListinoVen() != null && !causale.getIdListinoVen().equals(""))
    	  setIdListinoVen(causale.getIdListinoVen());
      //29642 fine
    }
  }

  public CausalePreventivoCommessa getCausaleTemp(String idCausale)
  {
    CausalePreventivoCommessa cau = null;
    try
    {
      String key = KeyHelper.buildObjectKey(new String[]
                                            {Azienda.getAziendaCorrente(), idCausale});
      cau = (CausalePreventivoCommessa)CausalePreventivoCommessa.elementWithKey(CausalePreventivoCommessa.class, key, PersistentObject.NO_LOCK);
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    return cau;
  }

  public void CompletaDatiWorkFlow(WfSpecific wfSpec)
  {
    if (wfSpec != null)
    {
      WfSpecNode wfSpecNode = wfSpec.getInitialNode();
      wfStatus.setWfClassId(wfSpec.getWfClassId());
      wfStatus.setWfId(wfSpec.getWfId());
      if (wfSpecNode != null)
        wfStatus.setWfSpecificNodeId(wfSpecNode.getWfNodeId());
    }
  }

  protected void completaDateConsegna(java.sql.Date dataDocumento)
  {
    int minGgEvas = getTempoMinimoEvasione();
    java.sql.Date nuovaData = TimeUtils.addDays(dataDocumento, minGgEvas);
    setDataConsegRcs(nuovaData);
    setDataConsegPrv(nuovaData);
    calcolaSettimane(dataDocumento);
  }

  public int getTempoMinimoEvasione()
  {
    return PersDatiVen.getCurrentPersDatiVen().getTempoMinEvasione();
  }

  protected void calcolaSettimane(java.sql.Date dataDocumento)
  {
    if (dataDocumento != null)
    {
      int[] datiSettConsegnaRichiesta = TimeUtils.getISOWeek(dataDocumento);
      String sett = getSettimanaFormattata(datiSettConsegnaRichiesta[0], datiSettConsegnaRichiesta[1]);
      this.setSettFineVal(sett);
      this.setSettFineValc(sett);
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

  public void completaDati()
  {
    ClienteVendita cliente = getClienteTemp(getIdCliente());
    WpuRubricaContatti contatto = getRubricaContattiTemp(getIdRubContatti());
    PersDatiVen persDatiVen = PersDatiVen.getCurrentPersDatiVen();
    if (persDatiVen == null)
    {
      Trace.println("Impossibile creare l'ordine perchè PersDatiVen non è valorizzato");
      Trace.println("Eccezione nel recupero di PersDatiVen in completaBO() di " + this.getClass().getName());
      return;
    }
    String clienteDivisioneKey = KeyHelper.buildObjectKey(new String[]
      {Azienda.getAziendaCorrente(), getIdCliente(), getIdDivisione()});

    PersDatiGen persDatiGen = PersDatiGen.getCurrentPersDatiGen();
    if (persDatiGen == null)
    {
      Trace.println("Impossibile creare l'ordine perchè PersDatiGen non è valorizzato");
      Trace.println("Eccezione nel recupero di PersDatiGen in completaBO() di " + this.getClass().getName());
      return;
    }
    else if (persDatiGen != null)
      setIdValuta(persDatiGen.getIdValutaPrimaria());
    Cliente cli = null;
    if (getTipoIntestatarioGUI() == TP_INTES_CLIENTE && cliente != null)
    {
      cli = cliente.getCliente();
      setIdAnagrafico(cliente.getIdAnagrafico());
      AnagraficoDiBase anagBase = cli.getAnagrafico();

      String ragSoc = anagBase.getRagioneSociale();
      if (anagBase.getRagioneSociale2() != null)
        ragSoc = ragSoc + " " + anagBase.getRagioneSociale2();
      setRagioneSocPrvc(ragSoc);
      setIndirizzoPrvc(cliente.getIndirizzo());
      setLocalitaPrvc(cliente.getLocalita());
      setCapPrvc(cliente.getCAP());
      setIdNazionePrvc(anagBase.getIndirizzoBase().getIdNazione());
      setIdProvinciaPrvc(anagBase.getIndirizzoBase().getIdProvincia());
      ListinoVendita listino = cliente.getListino(clienteDivisioneKey);
      //33229 inizio : blocco commentato
      /*
      if ((listino != null && !RicercaCondizioniDiVendita.isListinoAuthorized(listino)) || listino == null)
      {
        listino = persDatiVen.getListinoVendita();
        if (!RicercaCondizioniDiVendita.isListinoAuthorized(listino))
          listino = null;
      }
      if(listino != null) //Fix 22273
        setListinoVen(listino);
      */
      if(listino != null && RicercaCondizioniDiVendita.isListinoAuthorized(listino)) {
    	  setListinoVen(listino);
      }
      //33229 fine

      Valuta valuta = cli.getValuta(clienteDivisioneKey);
      if (valuta != null)
        setValuta(valuta);

    }
    if (getListinoVen() == null && persDatiVen != null)
    {
      ListinoVendita listino = persDatiVen.getListinoVendita();
      if (!RicercaCondizioniDiVendita.isListinoAuthorized(listino))
        listino = null;
      if(listino != null) //Fix 22273
        setListinoVen(listino);
    }

    if (getTipoIntestatarioGUI() == TP_INTES_CONTATTO && contatto != null)
    {
      setRagioneSocPrvc(contatto.getAzRagioneSociale());
      setIndirizzoPrvc(contatto.getAzIndirizzo());
      setIdNazionePrvc(contatto.getAzNazione());
      setLocalitaPrvc(contatto.getAzLocalita());
      setIdProvinciaPrvc(contatto.getAzProvincia()); //Fix 27506
      setCapPrvc(contatto.getAzCap());
      setFaxPrvc(contatto.getFax());
      setMailPrvc(contatto.getEmail());
    }

    // anagrafica
    if (getTipoIntestatarioGUI() == TP_INTES_ANAGRAFICO)
    {
      AnagraficoDiBase anaBas = getAnagrafico();
      if (anaBas != null)
      {

        String ragSoc = anaBas.getRagioneSociale();
        if (anaBas.getRagioneSociale2() != null)
          ragSoc = ragSoc + " " + anaBas.getRagioneSociale2();
        setRagioneSocPrvc(ragSoc);

        setIndirizzoPrvc(anaBas.getIndirizzoBase().getIndirizzo());
        setLocalitaPrvc(anaBas.getIndirizzoBase().getLocalita());
        setCapPrvc(anaBas.getIndirizzoBase().getCAP());
        setIdNazionePrvc(anaBas.getIndirizzoBase().getIdNazione());
        setIdProvinciaPrvc(anaBas.getIndirizzoBase().getIdProvincia());
      }
    }
    setFattoreCambio(PreventivoCommessaTestata.recuperaCambio(getIdValuta(), getDataPrevc()));
    valorizzaLingua(); //Fix 19818

  }

  /**
   * getRubricaContattiTemp
   *
   * @param integer Integer
   * @return WpuRubricaContatti
   */
  private WpuRubricaContatti getRubricaContattiTemp(Integer integer)
  {
    WpuRubricaContatti cli = null;
    try
    {
      cli = (WpuRubricaContatti)WpuRubricaContatti.elementWithKey(WpuRubricaContatti.class, KeyHelper.objectToString(integer), PersistentObject.NO_LOCK);
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    return cli;

  }

  /**
   * getClienteTemp
   *
   * @param string String
   * @return ClienteVendita
   */
  //Fix 33626 da private a protected
  protected ClienteVendita getClienteTemp(String string)
  {
    ClienteVendita cli = null;
    try
    {
      String key = KeyHelper.buildObjectKey(new String[]
                                            {Azienda.getAziendaCorrente(), string});
      cli = (ClienteVendita)ClienteVendita.elementWithKey(ClienteVendita.class, key, PersistentObject.NO_LOCK);
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    return cli;

  }

  public AnagraficoDiBase getAnagrafico()
  {
    AnagraficoDiBase anaBas = null;
    try
    {
      anaBas = AnagraficoDiBase.elementWithKey(KeyHelper.objectToString(getIdAnagrafico()), PersistentObject.OPTIMISTIC_LOCK);
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    return anaBas;
  }

  /**
   * checkDelete
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 14/09/2011    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete()
  {
    /**@todo*/
    return null;
  }

  /**
   * setSalvaRighe
   *
   * @param b boolean
   */
  public void setSalvaRighe(boolean b)
  {
    this.iSalvaRighe = b;
  }

  /**
   * setSalvaRighe
   *
   * @param b boolean
   */
  public boolean isSalvaRighe()
  {
    return this.iSalvaRighe;
  }

  public int saveOwnedObjects(int rc) throws SQLException
  {
     //33022 inizio
	 if (isSalvaRighe()) {
		 if (!iCopia)
			 return super.saveOwnedObjects(rc);
		 else
			 return copiaAlbero();
	 }
	 //33022 fine
     return 0;
  }

  //33022 inizio
  protected int copiaAlbero() throws SQLException {
	  NodoTestata root = costruisciAlbero();
	  return root.save();
  }
  //33022 fine

  public BigDecimal getPercTotPrevCom()
  {

    return calcolaPercentualeSu(getMdcTotPrevc(), getVlrTotPrevc());
  }

  public static BigDecimal calcolaPercentualeSu(BigDecimal importo, BigDecimal perc)
  {
    BigDecimal valPerc = new BigDecimal(0);
    valPerc.setScale(2);
    if (importo != null && importo.compareTo(new BigDecimal(0)) != 0 && perc != null && perc.compareTo(new BigDecimal(0)) != 0)
    {
      BigDecimal val = importo.divide(perc, 6, BigDecimal.ROUND_HALF_UP);
      valPerc = val.multiply(new BigDecimal(100));
    }
    return valPerc;
  }

  public ErrorMessage checkIdListinoVen()
  {
    if (getIdListinoVen() != null && getIdValuta() != null)
    {
      if (getListinoVen().getIdValuta() != null && !getListinoVen().getIdValuta().equals(getIdValuta()))
        return new ErrorMessage("THIP40T182");
    }

    //29642 inizio
	 if(getIdListinoVen() == null && getRepPrezzoArt() == CausalePreventivoCommessa.REP_PREZZO_DA_LST_VEND )
 		 return new ErrorMessage("BAS0000000");
    //29642 fine
    return null;
  }

  public ErrorMessage checkIdListinoAcq()
  {
    PersDatiGen pdg = PersDatiGen.getCurrentPersDatiGen();
    if (getIdListinoAcq() != null && getIdValuta() != null)
    {
      if (getListinoAcq().getIdValuta() != null && !getListinoAcq().getIdValuta().equals(getIdValuta()) && !getListinoAcq().getIdValuta().equals(pdg.getIdValutaPrimaria()))
        return new ErrorMessage("THIP40T183");
    }
    //29642 inizio
	 if(getIdListinoAcq() == null && (getRepCosArt() == CausalePreventivoCommessa.REP_COS_LISTINO_ACQ || getRepCosArt() == CausalePreventivoCommessa.REP_COS_ARTLST_RSRCOS))
 		 return new ErrorMessage("BAS0000000");
    //29642 fine
    return null;
  }

  //29642 inizio
  public ErrorMessage checkIdAmbiente(){
 	 if(getIdAmbiente() == null && getRepCosArt() == CausalePreventivoCommessa.REP_COS_AMBIENTE)
 		 return new ErrorMessage("BAS0000000");
 	 return null;
  }
  //29642 fine

  public void ricalcolaImportiCosti()
  {
     System.err.println("Testata.ricalcolaImportiCosti() Metodo non più operativo");
     /*
    List righe = getRighe();
    Iterator iteRighe = righe.iterator();
    while (iteRighe.hasNext())
    {
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iteRighe.next();
      riga.calcolaImportiECCosti();
      if(riga.isOnDB()) //Fix 20406
        riga.setSalvaRighe(false);
      try
      {
        riga.save();
      }
      catch (SQLException ex)
      {
      }
    }
    */
  }

  protected PreventivoCommessaTestata getDocDestinazionePerCopia()
  {
    return (PreventivoCommessaTestata)Factory.createObject(PreventivoCommessaTestata.class);
  }

  public PreventivoCommessaTestata copiaDocumento(SpecificCopiaPreventivoCommessa spec) throws CopyException, NumeratoreException
  {
    PreventivoCommessaTestata doc = getDocDestinazionePerCopia();
    doc.setEqual(this);
    doc.setOnDB(false);
    doc.setKey(null);

    //Numeratore
    NumeratoreHandler nDoc = doc.getNumeratoreHandler();
    NumeratoreHandler nSpec = spec.getNumeratore();
    nDoc.setAnno(nSpec.getAnno());
    nDoc.setIdAzienda(nSpec.getIdAzienda());
    nDoc.setIdNumeratore(nSpec.getIdNumeratore());
    nDoc.setIdSerie(nSpec.getIdSerie());
    nDoc.setIdSottoserie(nSpec.getIdSottoserie());
    nDoc.setDataDocumento(nSpec.getDataDocumento());    
    if (nSpec.getNumero() != null)
    {
      nDoc.setNumero(nSpec.getNumero());
    }
    impostaIntestatarioInCopiaDocumento(spec, doc);
    if (spec.getCondizTestataDocumento() == SpecificCopiaPreventivoCommessa.CTD_DA_INTESTATARIO) //Fix 31639
        doc.completaBO();

    if (!spec.getCausaleKey().equals(spec.getCausaleDocOrigKey()))
    {
      doc.completaDatiCausale();
      doc.setMagazzino(getMagazzino());
    }

    switch (spec.getCondizCommenti())
    {
      case SpecificCopiaPreventivoCommessa.COM_DA_DOCUMENTO:
    	doc.setAbilitaCopiaCommenti(false); //Fix 34426
        setCommentToCopy(doc.getCommenti(), true);
        break;
      case SpecificCopiaPreventivoCommessa.COM_DA_CONDIZ_ANAGR:
        doc.getCommenti().getCommentHandlerLinks().clear();
        break;
    }
    //Fix 20569 Inizio
    if (spec.getTipoOperazione() == SpecificCopiaPreventivoCommessa.TOP_VERSIONE) {
      PreventivoCommessaTestata docOrig = spec.getDocOrig();
      String nd = docOrig.getIdNumeroPrevc();
      nd = nd.substring(nd.length() - docOrig.getNumeratoreHandler().getNumeroCifreRese().intValue(), nd.length());
      int numDocOrig = new Integer(nd).intValue();
      nDoc.setAnno(docOrig.getIdAnnoPrevc());
      nDoc.setIdAzienda(nSpec.getIdAzienda());
      nDoc.setIdNumeratore(nSpec.getIdNumeratore());
      nDoc.setIdSerie(nSpec.getIdSerie());
      nDoc.setIdSottoserie(nSpec.getIdSottoserie());
      nDoc.setDataDocumento(nSpec.getDataDocumento());
      nDoc.setNumero(new Integer(numDocOrig + 1));
    }
    //Fix 20569 Fine
    //36924 inizio
    if(spec.getDateConsegna() == SpecificCopiaPreventivoCommessa.DC_SPECIFICA) {
    	doc.setDataConsegRcs(spec.getDataSpecifica());
    	doc.setDataConsegPrv(spec.getDataSpecifica());
    }
    //36924 fine
    //29032 inizio
    if(spec.isRicalcolaPrezzi() || spec.isRicalcolaCosti()){
    	doc.setDataRiferimento(spec.getDataRiferimento());
    }
    copiaDocumentoPers(spec, doc);   // fix 31606
    //29032 fine
    List righe = getRighe();
    //Fix 20406 inizio
    List righeDaSort = new ArrayList();
    for (Iterator iter = righe.iterator(); iter.hasNext(); )
    {
       PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iter.next();
       righeDaSort.add(riga);
    }
    Collections.sort(righeDaSort, new PrevCommRigheComparator());
    //Fix 20406 fine
    for (Iterator iter = righeDaSort.iterator(); iter.hasNext(); ) //Fix 20406
    {
      PreventivoCommessaRiga rigaCopiata = null;
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iter.next();
      rigaCopiata = riga.copiaRiga(doc, spec);
      if(rigaCopiata!=null)
        doc.getRighe().add(rigaCopiata);
    }

    doc.resetWorkflow(getWfStatus());
    doc.getDatiComuniEstesi().setCaricaAllStati(true); //Fix 34819
    return doc;
  }

  // fix 31606
  public void copiaDocumentoPers(SpecificCopiaPreventivoCommessa spec, PreventivoCommessaTestata doc) {

  }
  // fine fix 31606

  protected static void setCommentToCopy(CommentHandler commentHandler, boolean commentToCopy)
  {
    if (commentHandler != null && commentHandler.getCommentHandlerLinks() != null)
    {
      for (Iterator iter = commentHandler.getCommentHandlerLinks().iterator(); iter.hasNext(); )
      {
        CommentHandlerLink chl = (CommentHandlerLink)iter.next();
        if (chl != null)
        {
          chl.setCommentToCopy(commentToCopy);
          chl.getChoiceCommentUse();
        }
      }
    }
  }

  public void resetWorkflow(WfStatus wfs)
  {
    WfSpecific wfSpec = null;
    if (wfs != null)
      wfSpec = wfs.getWfSpecific();
    if (wfSpec == null)
    {
      wfStatus.setWfClassId(WfClass.ID_DEF_VALUE);
      wfStatus.setWfId(null);
      wfStatus.setCurrentNodeId(null);
      wfStatus.setCurrentSubNodeId(WfSpecNode.DEF_SUB_NODE_ID);
    }
    else
    {
      wfStatus.setWfClassId(wfSpec.getWfClassId());
      wfStatus.setWfId(wfSpec.getWfId());
      if (wfSpec.getInitialNode() != null)
      {
        wfStatus.setCurrentNodeId(wfSpec.getInitialNode().getWfNodeId());
        wfStatus.setCurrentSubNodeId(wfSpec.getInitialNode().getWfSubNodeId());
      }
    }
  }

  protected void impostaIntestatarioInCopiaDocumento(SpecificCopiaPreventivoCommessa spec, PreventivoCommessaTestata doc)
  {
    PreventivoCommessaTestata prev = (PreventivoCommessaTestata)doc;
    prev.setIdAzienda(Azienda.getAziendaCorrente());
    prev.setTipoIntestatarioGUI(((SpecificCopiaPreventivoCommessa)spec).getTipoIntestatario());
    prev.setIdCliente(((SpecificCopiaPreventivoCommessa)spec).getIdCliente());
    prev.setIdRubContatti(((SpecificCopiaPreventivoCommessa)spec).getIdContatto());
    prev.setIdAnagrafico(((SpecificCopiaPreventivoCommessa)spec).getIdAnagrafico());
    prev.setDivisione(spec.getDivisione());
    prev.setIdCauPrevc(spec.getIdCausale());

    SpecificCopiaPreventivoCommessa sco = (SpecificCopiaPreventivoCommessa)spec;
    if (sco.getTipoIntestatario() == PreventivoCommessaTestata.TP_INTES_CLIENTE)
      prev.setIdAnagrafico(prev.getCliente().getIdAnagrafico());

    if (sco.getTipoIntestatario() != getTipoIntestatarioGUI() ||
        (sco.getTipoIntestatario() == PreventivoCommessaTestata.TP_INTES_CLIENTE && !prev.getIdCliente().equals(getIdCliente())) ||
        (sco.getTipoIntestatario() == PreventivoCommessaTestata.TP_INTES_ANAGRAFICO && !prev.getIdAnagrafico().equals(getIdAnagrafico())) ||
        (sco.getTipoIntestatario() == PreventivoCommessaTestata.TP_INTES_CONTATTO && !prev.getIdRubContatti().equals(getIdRubContatti())))
      prev.completaBO();

  }

  public List getComponentiCosti()
  {
    List ret = new ArrayList();
     if(getAmbienteCosti()!=null )
       ret.addAll(getAmbienteCosti().getTipoCosto().getComponentiCosto());
      return ret;
  }

  //Fix 19818 Inizio
  public void valorizzaLingua(){
    char tipoIntertatario = getTipoIntestatarioGUI();
    if (tipoIntertatario == TP_INTES_CLIENTE){
      ClienteVendita cliente = getClienteTemp(getIdCliente());
      if(cliente!=null && cliente.getCliente() != null){
        setIdLingua(cliente.getCliente().getIdLingua());
      }
    }
  }
  //Fix 19818 Fine

  //Fix 20406 inizio
  public boolean isCopia() {
  	return iCopia;
  }

  public void setCopia(boolean copia) {
  	iCopia = copia;
  }

	protected class PrevCommRigheComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			PreventivoCommessaRiga prevCommRiga1 = (PreventivoCommessaRiga) o1;
			PreventivoCommessaRiga prevCommRiga2 = (PreventivoCommessaRiga) o2;
			if(prevCommRiga1 != null && prevCommRiga2 != null)
			  return Utils.compare(new Integer(prevCommRiga1.getIdRigacPrv()), new Integer(prevCommRiga2.getIdRigacPrv()));
			return 0;
		}
	}
  //Fix 20406 Fine

  //Fix 22273 Inizio
  public ErrorMessage checkIdTipoCosto(){
    String idTipoCosto = getIdTipoCosto();
    //if (idTipoCosto == null && getRepCosArt() == REP_COS_ARTICOLO_COSTO)//29642
    if (idTipoCosto == null && (getRepCosArt() == CausalePreventivoCommessa.REP_COS_ARTICOLO || getRepCosArt() == CausalePreventivoCommessa.REP_COS_ARTLST_RSRCOS))//29642
      return new ErrorMessage("BAS0000000");
    return null;

  }
  //Fix 22273 Fine


  //Fix 27506 inizio
  public void calcolaValoriSpecifici(List righeVoce)
  {
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

    setVlrVociPrevc(valoreVoce);
    setVlrArtPrevc(valoreArticolo);
    setVlrRisuPrevc(valoreRisorsaU);
    setVlrRismPrevc(valoreRisorsaM);

    setCosVociPrevc(costoVoce);
    setCosArtPrevc(costoArticolo);
    setCosRisuPrevc(costoRisorsaU);
    setCosRismPrevc(costoRisorsaM);

    setMdcVociPrevc(margineVoce);
    setMdcArtPrevc(margineArticolo);
    setMdcRisuPrevc(margineRisorsaU);
    setMdcRismPrevc(margineRisorsaM);

    Iterator iteRiga = righeVoce.iterator();
    while (iteRiga.hasNext())
    {
       PreventivoCommessaVoce rigaVoce = (PreventivoCommessaVoce)iteRiga.next();
       if (rigaVoce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE)
       {
          valoreVoce = somma(valoreVoce, rigaVoce.getVlrTotale());
          costoVoce = somma(costoVoce, rigaVoce.getCosTotale());
          margineVoce = somma(margineVoce, rigaVoce.getMdcTotale());
       }
       if (rigaVoce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO)
       {
          valoreArticolo = somma(valoreArticolo, rigaVoce.getVlrTotale());
          costoArticolo = somma(costoArticolo, rigaVoce.getCosTotale());
          margineArticolo = somma(margineArticolo, rigaVoce.getMdcTotale());
       }
       if (rigaVoce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA && rigaVoce.getTipoRisorsa() == Risorsa.RISORSE_UMANE)
       {
          valoreRisorsaU = somma(valoreRisorsaU, rigaVoce.getVlrTotale());
          costoRisorsaU = somma(costoRisorsaU, rigaVoce.getCosTotale());
          margineRisorsaU = somma(margineRisorsaU, rigaVoce.getMdcTotale());
       }
       if (rigaVoce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA && rigaVoce.getTipoRisorsa() == Risorsa.MACCHINE)
       {
          valoreRisorsaM = somma(valoreRisorsaM, rigaVoce.getVlrTotale());
          costoRisorsaM = somma(costoRisorsaM, rigaVoce.getCosTotale());
          margineRisorsaM = somma(margineRisorsaM, rigaVoce.getMdcTotale());
       }

    }

    setVlrVociPrevc(valoreVoce);
    setVlrArtPrevc(valoreArticolo);
    setVlrRisuPrevc(valoreRisorsaU);
    setVlrRismPrevc(valoreRisorsaM);

    setCosVociPrevc(costoVoce);
    setCosArtPrevc(costoArticolo);
    setCosRisuPrevc(costoRisorsaU);
    setCosRismPrevc(costoRisorsaM);

    setMdcVociPrevc(margineVoce);
    setMdcArtPrevc(margineArticolo);
    setMdcRisuPrevc(margineRisorsaU);
    setMdcRismPrevc(margineRisorsaM);


  }

  public void calcolaValoreTotale(List righe)
  {
    Iterator ite = righe.iterator();
    BigDecimal valoreTotale = new BigDecimal(0);
    BigDecimal costoTotale = new BigDecimal(0);
    BigDecimal margineTotale = new BigDecimal(0);

    setVlrTotPrevc(valoreTotale);
    setCosTotPrevc(costoTotale);
    setMdcTotPrevc(margineTotale);

    while (ite.hasNext())
    {
      PreventivoCommessaRiga riga = (PreventivoCommessaRiga)ite.next();
      if(riga.getStato() == DatiComuniEstesi.VALIDO){//29642
	      valoreTotale = somma(valoreTotale, riga.getVlrTotale());
	      costoTotale = somma(costoTotale, riga.getCosTotale());
	      margineTotale = sottrai(valoreTotale, costoTotale);
      }//29642
    }
    setVlrTotPrevc(valoreTotale);
    setCosTotPrevc(costoTotale);
    setMdcTotPrevc(margineTotale);
  }

  protected BigDecimal somma(BigDecimal valore1, BigDecimal valore2)
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
  public boolean isPropagazioneRicalcoloValori()
  {
     return propagazioneRicalcoloValori;
  }


  public void setPropagazioneRicalcoloValori(boolean propagazioneRicalcoloValori)
  {
     this.propagazioneRicalcoloValori = propagazioneRicalcoloValori;
  }


  //protected void ricalcolaValori() //Fix 31227
  protected void ricalcolaValori(List righeDaRicalcolare, List vociDaRicalcolare) //Fix 31227
  {
     if(!propagazioneRicalcoloValori)
        return;

     NodoTestata root = costruisciAlbero();
     //printNode(root,"");
     //root.ricalcolaValori(); //Fix 31227
     root.ricalcolaValori(righeDaRicalcolare, vociDaRicalcolare); //Fix 31227
     propagazioneRicalcoloValori = false;
     try
     {
        //save(); //Fix 31162
      if (righeDaRicalcolare == null) //Fix 31227
    	super.save(); //Fix 31162
      //Fix 31227 inizio
      else {
        setSalvaRighe(false);
        setSalvoDaSola(true);
        super.save(); //Fix 31162
        Iterator iterRighe = getRighe().iterator();
        while (iterRighe.hasNext()) {
          PreventivoCommessaRiga riga = (PreventivoCommessaRiga) iterRighe.next();
          if (righeDaRicalcolare.contains(riga)) {
            riga.setSalvaRighe(false);
            riga.save();
            if (vociDaRicalcolare != null) {
              Iterator iterVoci = riga.getRighe().iterator();
              while (iterVoci.hasNext()) {
                PreventivoCommessaVoce voce = (PreventivoCommessaVoce) iterVoci.next();
                if (vociDaRicalcolare.contains(voce)) {
                  voce.setPropagazioneRicalcoloValori(false);
                  voce.save();
                }
              }
            }
          }
        }
      }
      //Fix 31227 fine
     }
     catch(SQLException e)
     {
        e.printStackTrace(Trace.excStream);
     }
     propagazioneRicalcoloValori = true;

  }

  //Fix 31227 inizio
  protected void ricalcolaValori() {
    ricalcolaValori(null, null); //Fix 31227
  }
  //Fix 31227 fine

  protected void printNode(DefaultMutableTreeNode node , String ident)
  {
     System.out.println(ident + "" + ((BusinessObject)node.getUserObject()).getKey());
     if(node instanceof NodoRiga)
     {
        List voci = ((NodoRiga)node).getVoci();
        Iterator iter = voci.iterator();
        while(iter.hasNext())
        {
           NodoVoce nv = (NodoVoce)iter.next();
           printNode(nv,"  ");
        }
     }
     if(node instanceof NodoVoce)
     {
        List voci = ((NodoVoce)node).getVoci();
        Iterator iter = voci.iterator();
        while(iter.hasNext())
        {
           NodoVoce nv = (NodoVoce)iter.next();
           printNode(nv,"  " +  ident);
        }
     }
     Enumeration iterNodi = node.children();
     while(iterNodi.hasMoreElements())
     {
        DefaultMutableTreeNode nodoRiga = (DefaultMutableTreeNode)iterNodi.nextElement();
        printNode(nodoRiga, "");
     }
  }


  protected NodoTestata costruisciAlbero()
  {
     NodoTestata root = new NodoTestata(this);
     if(isSalvoDaClipboard())//32048
    	 ((OneToMany) getRighe()).setNew(true);//31854
     List righeCommessa = new ArrayList(getRighe());
     Iterator iterRighe = righeCommessa.iterator();
     while(iterRighe.hasNext())
     {
        PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iterRighe.next();
        if(riga.getIdRigacPrvApp() == 0)
        {
           NodoRiga nodo = new NodoRiga(riga);
           root.add(nodo);
           iterRighe.remove();
           estraiRigheVoce(nodo);
        }
     }

     Enumeration iterNodi = root.children();
     while(iterNodi.hasMoreElements() && !righeCommessa.isEmpty())
     {
        NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
        estraiRigheSottoCommessa(nodoRiga, righeCommessa);
     }

     return root;
  }

  protected void estraiRigheSottoCommessa(NodoRiga nodoPadre, List righeCommessa)
  {
     PreventivoCommessaRiga rigaPadre = (PreventivoCommessaRiga)nodoPadre.getUserObject();
     int idRigaPadre = rigaPadre.getIdRigacPrv();
     Iterator iterRighe = righeCommessa.iterator();
     while(iterRighe.hasNext())
     {
        PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iterRighe.next();
        if(riga.getIdRigacPrvApp() != 0 && riga.getIdRigacPrvApp() == idRigaPadre)
        {
           NodoRiga nodo = new NodoRiga(riga);
           nodoPadre.add(nodo);
           iterRighe.remove();
           estraiRigheVoce(nodo);
        }
     }

     Enumeration iterNodi = nodoPadre.children();
     while(iterNodi.hasMoreElements() && !righeCommessa.isEmpty())
     {
        NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
        estraiRigheSottoCommessa(nodoRiga, righeCommessa);
     }
  }


  protected void estraiRigheVoce(NodoRiga nodoPadre)
  {
     PreventivoCommessaRiga rigaPadre = nodoPadre.getRiga();

     List righeVoce = new ArrayList(rigaPadre.getRighe());
     Iterator iterRighe = righeVoce.iterator();
     while(iterRighe.hasNext())
     {
        PreventivoCommessaVoce voce = (PreventivoCommessaVoce)iterRighe.next();
        if(voce.getIdSubRigavPrv() == 0)
        {
           NodoVoce nodo = new NodoVoce(voce);
           nodoPadre.getVoci().add(nodo);
           iterRighe.remove();
        }
     }

     Iterator iterVoci = nodoPadre.getVoci().iterator();
     while(iterVoci.hasNext() && !righeVoce.isEmpty())
     {
        NodoVoce nodoVoce = (NodoVoce)iterVoci.next();
        estraiRigheSottoVoce(nodoVoce, righeVoce);
     }

  }


  protected void estraiRigheSottoVoce(NodoVoce nodoPadre, List righeVoce)
  {
     PreventivoCommessaVoce rigaPadre = nodoPadre.getVoce();

     //Fix 31162 inizio
     if (rigaPadre.getArticolo() != null) {
       char tipoParte = rigaPadre.getArticolo().getTipoParte();
       char tipoCalcoloPrezzo = rigaPadre.getArticolo().getTipoCalcPrzKit();
       if ((tipoParte == ArticoloDatiIdent.KIT_NON_GEST || tipoParte == ArticoloDatiIdent.KIT_GEST) &&
            tipoCalcoloPrezzo == ArticoloDatiVendita.DA_COMPONENTI)
         return;
     }
     //Fix 31162 fine

     int idRigaPadre = rigaPadre.getIdRigavPrv();
     Iterator iterRighe = righeVoce.iterator();
     while(iterRighe.hasNext())
     {
        PreventivoCommessaVoce riga = (PreventivoCommessaVoce)iterRighe.next();
        if(riga.getIdRigavPrv() == idRigaPadre)
        {
           NodoVoce nodo = new NodoVoce(riga);
           nodoPadre.getVoci().add(nodo);
           iterRighe.remove();
        }
     }

  }
  //Fix 27506 fine
  //29166 inizio
  public int getLivello() {
	  return 0;
  }

  public String getDescrzioneForTree(){
	  return "";
  }

  public String getTipoRigaForTree(){
	  return "";
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
    return getVlrTotPrevc();
  }

  public BigDecimal getCosTotaleForTree() {
    return getCosTotPrevc();
  }

  public BigDecimal getMarckupForTree(){
	  return null;
  }

  public BigDecimal getMdcTotaleForTree() {
    return getMdcTotPrevc();
  }

  public BigDecimal getPercentualeForTree(){
	  return getPercTotPrevCom();
  }

  public List getRigheCommesse(){
	  String where = PreventivoCommessaRigaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " +
			         PreventivoCommessaRigaTM.ID_NUMERO_PREVC + "='" + getIdNumeroPrevc() + "' AND " +
			         PreventivoCommessaRigaTM.ID_ANNO_PREVC + "='" + getIdAnnoPrevc() + "' AND " +
			         //PreventivoCommessaRigaTM.R_COMMESSA_APP + " IS NULL" ;//29529
			         PreventivoCommessaRigaTM.R_RIGAC_PRV +" = 0"; //29529
	  try
	  {
		  return PreventivoCommessaRiga.retrieveList(where, PreventivoCommessaRigaTM.SEQUENZA_RIGA, true);
	  }
	  catch (Exception ex)
	  {
		  return null;
	  }
  }

  public String getKeyForTree() {
	  return getIdAnnoPrevc() + "/" + getIdNumeroPrevc();
  }
  //29166 fine
  //29529 inizio
  public boolean initializeOwnedObjects(boolean result) {
    result = super.initializeOwnedObjects(result);
    iOldMarkupArticolo = getMarkupArticolo();
    iOldMarkupUomo = getMarkupUomo();
    iOldMarkupMacchina = getMarkupMacchina();
    return result;
  }

  public void gestioneMarkupRighe() throws SQLException{
	  if((iOldMarkupArticolo != null && getMarkupArticolo().compareTo(iOldMarkupArticolo) != 0) ||//29672
		 (iOldMarkupUomo != null && getMarkupUomo().compareTo(iOldMarkupUomo) != 0) || //29672
		 (iOldMarkupMacchina != null && getMarkupMacchina().compareTo(iOldMarkupMacchina) != 0)){//29672
		  if(getRighe() != null && !getRighe().isEmpty()){
			  Iterator iterRighe = getRighe().iterator();
			  while(iterRighe.hasNext()){
				  PreventivoCommessaRiga riga = (PreventivoCommessaRiga)iterRighe.next();
				  riga.setMarkupArticolo(getMarkupArticolo());
				  riga.setMarkupUomo(getMarkupUomo());
				  riga.setMarkupMacchina(getMarkupMacchina());
				  riga.setDaSaveTestata(true);
			  }
			  setSalvaRighe(true); //Fix 31162
		  }
	  }
  }
  //29529 fine
  //29642 inizio
  public String getNota(){
	  return this.getNotaRich();
  }
  //29642 fine
  //29731 inizio
  public String getDescrArticoloForTree(){
	  return "";
  }

  public String getDescrCommessaForTree(){
	  if(getCommessa() != null){
		  return getCommessa().getDescrizione().getDescrizione();
	  }
	  return "";
  }
  //29731 fine
  // fix 30327
	public boolean isGestioneMarkup(){
		return (getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP);
	}

  // fine fix 30327
  //32048 inizio
  public boolean isSalvoDaClipboard() {
	  return iSalvoDaClipboard;
  }

  public void setSalvoDaClipboard(boolean salvo) {
	  iSalvoDaClipboard = salvo;
  }
  //32048 fine

  //Fix 33626 - inizio
  protected int gestioneRigheOnSavePers() throws SQLException {
	  return ErrorCodes.NO_ROWS_UPDATED;
  }
  //Fix 33626 - fine

  //Fix 34426 --inizio
  public void setAbilitaCopiaCommenti(boolean b) {
	  this.iAbilitaCopiaCommenti = b;
  }

  public boolean isAbilitaCopiaCommenti() {
	  return iAbilitaCopiaCommenti;
  }
  //Fix 34426 --fine

}


//Fix 27506 inizio
class NodoTestata extends DefaultMutableTreeNode
{
   public NodoTestata(PreventivoCommessaTestata testata)
   {
      super();
      setUserObject(testata);
   }

   public PreventivoCommessaTestata getTestata()
   {
      return (PreventivoCommessaTestata)getUserObject();
   }

   public List getRigheRiga()
   {
      List righeRiga = new ArrayList();

      Enumeration iterNodi = children();
      while(iterNodi.hasMoreElements())
      {
         NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
         if(nodoRiga.getRiga().getStato() == DatiComuniEstesi.VALIDO)//29642
        	 righeRiga.add(nodoRiga.getRiga());
      }

      return righeRiga;
   }

   public List getAllRigheVoce()
   {
      List righeVoce = new ArrayList();
      Enumeration iterNodi = preorderEnumeration();
      while(iterNodi.hasMoreElements())
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)iterNodi.nextElement();
         if(node instanceof NodoRiga)
         {
            NodoRiga riga = (NodoRiga)node;
            if(riga.getRiga().getStato() == DatiComuniEstesi.VALIDO){//29642
	            Iterator iter = riga.getVoci().iterator();
	            while(iter.hasNext())
	            {
	               NodoVoce voce = (NodoVoce)iter.next();
	               righeVoce.add(voce.getVoce());

	               Iterator iterSotto = voce.getVoci().iterator();
	               while(iterSotto.hasNext())
	               {
	                  NodoVoce voceSotto = (NodoVoce)iterSotto.next();
	                  righeVoce.add(voceSotto.getVoce());
	               }
	            }
            }//29642
         }
      }
      return righeVoce;
   }

   //public void ricalcolaValori() //Fix 31227
   public void ricalcolaValori(List righeDaRicalcolare, List vociDaRicalcolare) //Fix 31227
   {
      Enumeration iterNodi = children();
      while(iterNodi.hasMoreElements())
      {
         NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
         if (righeDaRicalcolare == null || righeDaRicalcolare.contains(nodoRiga.getRiga())) { //Fix 31227
           if(nodoRiga.getRiga().getStato() == DatiComuniEstesi.VALIDO)//29642
        	// nodoRiga.ricalcolaValori();
        	 nodoRiga.ricalcolaValori(righeDaRicalcolare, vociDaRicalcolare); //Fix 31227
           //29672 inizio
           else{
        	 nodoRiga.getRiga().setCosTotale(null);
        	 nodoRiga.getRiga().setVlrTotale(null);
        	 nodoRiga.getRiga().setMdcTotale(null);
        	 nodoRiga.getRiga().setCosLivello(null);
        	 nodoRiga.getRiga().setVlrLivello(null);
        	 nodoRiga.getRiga().setMdcLivello(null);
        	 nodoRiga.getRiga().setSalvaRighe(true);
          }
           //29672 fine
         } //Fix 31227
      }

      getTestata().calcolaValoreTotale(getRigheRiga());
      getTestata().calcolaValoriSpecifici(getAllRigheVoce());
   }

   //33022 inizio
   protected int save() throws SQLException {
     int ret = 0;
     Enumeration iterNodi = children();
     while(iterNodi.hasMoreElements()) {
         NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
         int rc1 = nodoRiga.save();
         if (rc1 < 0)
           return rc1;
         ret += rc1;
      }
      return ret;
   }
   //33022 fine
   //33048 inizio
   public List getAllRigheCommessa()
   {
      List righeCommessa = new ArrayList();
      Enumeration iterNodi = preorderEnumeration();
      while(iterNodi.hasMoreElements())
      {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)iterNodi.nextElement();
         if(node instanceof NodoRiga) {
            NodoRiga riga = (NodoRiga)node;
            righeCommessa.add(riga.getRiga());
         }
      }
      return righeCommessa;
   }
   //33048 fine
}


class NodoRiga extends DefaultMutableTreeNode
{
   List voci = new ArrayList();

   public NodoRiga(PreventivoCommessaRiga riga)
   {
      super();
      setUserObject(riga);
   }

   public PreventivoCommessaRiga getRiga()
   {
      return (PreventivoCommessaRiga)getUserObject();
   }

   public List getVoci()
   {
      return voci;
   }

   public List getRigheRiga()
   {
      List righeRiga = new ArrayList();

      Enumeration iterNodi = children();
      while(iterNodi.hasMoreElements())
      {
         NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
         if(nodoRiga.getRiga().getStato() == DatiComuniEstesi.VALIDO)//29642
        	 righeRiga.add(nodoRiga.getRiga());
      }

      return righeRiga;
   }

   public List getRigheVoce()
   {
      List righeVoce = new ArrayList();
      Iterator iterVoci = getVoci().iterator();
      while(iterVoci.hasNext())
      {
         NodoVoce nodoVoce = (NodoVoce)iterVoci.next();
         if(nodoVoce.getVoce().getPrevComRiga().getStato() == DatiComuniEstesi.VALIDO)//29642
        	 righeVoce.add(nodoVoce.getVoce());
      }
      return righeVoce;
   }


   //public void ricalcolaValori() //Fix 31227
   public void ricalcolaValori(List righeDaRicalcolare, List vociDaRicalcolare) //Fix 31227
   {
	  if(getRiga().getStato() == DatiComuniEstesi.VALIDO){//29642
	      Enumeration iterNodi = children();
	      while(iterNodi.hasMoreElements()) {
	         NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
	         //nodoRiga.ricalcolaValori(); //Fix 31227
	         if (righeDaRicalcolare == null || righeDaRicalcolare.contains(nodoRiga.getRiga()))  //Fix 31227
	             nodoRiga.ricalcolaValori(righeDaRicalcolare, vociDaRicalcolare); //Fix 31227
	      }

	      Iterator iterVoci = voci.iterator();
	      while(iterVoci.hasNext())	{
	         NodoVoce nodoVoce = (NodoVoce)iterVoci.next();
	         if (vociDaRicalcolare == null || vociDaRicalcolare.contains(nodoVoce.getVoce())) //Fix 31227
	           nodoVoce.ricalcolaValori();
	      }

	      getRiga().calcolaValoreLivello(getRigheVoce());
	      getRiga().calcolaValoreLivelloInferiore(getRigheRiga());
	      getRiga().calcolaValoreTotale();
	      getRiga().setSalvaRighe(true);//29032
	  }
	  //29672 inizio
	  else{
		  getRiga().setCosTotale(null);
		  getRiga().setVlrTotale(null);
		  getRiga().setMdcTotale(null);
		  getRiga().setCosLivello(null);
		  getRiga().setVlrLivello(null);
		  getRiga().setMdcLivello(null);
		  getRiga().setSalvaRighe(true);
	  }
	  //29672 fine
   }
   //33022 inizio
   protected int save() throws SQLException  {
	   int ret = 0;
	   try {
		   int rc2 = getRiga().save();
		   if (rc2 < 0)
			   return rc2;
		   ret += rc2;
	   }
	   catch (SQLException e) {
		   e.printStackTrace();
		   throw e;
	   }

	   Enumeration iterNodi = children();
	   while(iterNodi.hasMoreElements()) {
		   NodoRiga nodoRiga = (NodoRiga)iterNodi.nextElement();
		   nodoRiga.getRiga().setIdRigacPrvApp(getRiga().getIdRigacPrv());
		   int rc1 = nodoRiga.save();
		   if (rc1 < 0)
			   return rc1;
		   ret += rc1;
	   }
	   return ret;
   }
   //33022 fine
}

class NodoVoce extends DefaultMutableTreeNode
{
   List voci = new ArrayList();

   public NodoVoce(PreventivoCommessaVoce voce)
   {
      setUserObject(voce);
   }

   public PreventivoCommessaVoce getVoce()
   {
      return (PreventivoCommessaVoce)getUserObject();
   }

   public List getVoci()
   {
      return voci;
   }

   public void ricalcolaValori()
   {
      Iterator iterVoci = voci.iterator();
      while(iterVoci.hasNext())
      {
         NodoVoce nodoVoce = (NodoVoce)iterVoci.next();
         nodoVoce.ricalcolaValori();
      }

      getVoce().calcolaImportiECCosti();
      getVoce().ricalcolaPrezzoFromRigaSec();
   }
}
//Fix 27506 fine

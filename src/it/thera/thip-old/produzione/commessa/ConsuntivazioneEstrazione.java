package it.thera.thip.produzione.commessa;

import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.components.*;
import com.thera.thermfw.persist.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.cs.GestoreCommit;

/**
 * ConsuntivazioneEstrazione
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aissa Boulila 16/05/2005 at 10:30:28
 */
/*
 * Revisions:
 * Number  Date        Owner  Description
 * 04810   22/12/2005  LP     Aggiunto impostazione booleano per il salvataggio dello storico ordine
 * 07430   09/07/2007  LP     Correzioni per gestione trace sui log
 * 09384   18/06/2008  LP     Modifiche per permettere le personalizzazioni
 * 10416   03/02/2009  DBot   Consuntivazione area servizi (documenti)
 * 10537   04/03/2009  DBot   Consuntivazione area servizi (ordini)
 * 31460   08/07/2020  RA	  Modifica valorizzazione attributo SalvaOrdine
 * 31504   24/07/2020  RA	  Aggiunto l’estrazione delle RDA
 * 33593   20/05/2021  Mekki  Correzione vari
 * 34308   21/09/2021  Mekki  GestoreCommit
 * 33950   12/08/2021  RA	  Gestione nuovo batch di estrazione storici commessa
 */

public class ConsuntivazioneEstrazione {

  /**
   * cDataDocumentoPiuRecenteStmtStr
   */
  public static String cDataDocumentoPiuRecenteStmtStr =
    "SELECT MIN(" + StoricoCommessaTM.DATA_ORG + ")," +
    " MAX(" + StoricoCommessaTM.DATA_ORG + ")" +
    " FROM " + StoricoCommessaTM.TABLE_NAME +
    " WHERE " + StoricoCommessaTM.ID_AZIENDA + " = ?" +
    " AND " + StoricoCommessaTM.R_COMMESSA + " = ?" +
    " AND " + StoricoCommessaTM.DOC_ORIGINE + " = ?";
  public static CachedStatement cDataDocumentoPiuRecenteStmt = new CachedStatement(cDataDocumentoPiuRecenteStmtStr);

  /**
   * File di risorse.
   */
  public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.ConsuntivazioneCommesse"; //...FIX 4810

  /**
   * Chiave per risorsa.
   */
  public static final String RES_FILE_KEY = "SalvaStoricoOrdine"; //...FIX 4810

  /**
   * Attributo iSalvaOrdine.
   */
  public static boolean iSalvaOrdine; //...FIX 4810

  /**
   * iConsunCmm
   */
  protected ConsuntivazioneCommesse iConsunCmm;

  /**
   * Constructor
   * @param consunCmm ConsuntivazioneCommesse
   */
  public ConsuntivazioneEstrazione(ConsuntivazioneCommesse consunCmm) {
    //iSalvaOrdine = ResourceLoader.getString(RES_FILE, RES_FILE_KEY).equalsIgnoreCase("Y") ? true : false; //...FIX 4810//31460
	iSalvaOrdine = consunCmm.isEstrazioneOrdini();//31460 
    iConsunCmm = consunCmm;
  }

  /**
   * isSalvaOrdine
   * @return boolean
   */
  //...FIX 4810
  public static boolean isSalvaOrdine() {
    return iSalvaOrdine;
  }

  /**
   * run: launch the extraction of documents and the creation of storici
   * @throws Exception
   * @return boolean: if there is a blockant error the method returns fals
   */
  public boolean run() throws Exception {

     for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
        CompactCommessa compactCmm = (CompactCommessa)it.next();
        boolean ret = processRigheDocAcq(compactCmm);
        if(!ret) {
          iConsunCmm.commitWithGestoreCommit(true);
          return false;
        }
      }

    for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)it.next();
      boolean ret = processRigheDocLavEsn(compactCmm);
      if(!ret) {
        iConsunCmm.commitWithGestoreCommit(true);
        return false;
      }
    }

    for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)it.next();
      boolean ret = processDocPrd(compactCmm);
      if(!ret) {
        iConsunCmm.commitWithGestoreCommit(true);
        return false;
      }
    }

    for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)it.next();
      boolean ret = processRigheDocVen(compactCmm);
      if(!ret) {
        iConsunCmm.commitWithGestoreCommit(true);
        return false;
      }
    }

    for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)it.next();
      boolean ret = processDocGenMag(compactCmm);
      if(!ret) {
        iConsunCmm.commitWithGestoreCommit(true);
        return false;
      }
    }

    for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)it.next();
      boolean ret = processRigheDocTrasf(compactCmm);
      if(!ret) {
        iConsunCmm.commitWithGestoreCommit(true);
        return false;
      }
    }
    
    //Fix 10416 inizio
    for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) 
    {
       CompactCommessa compactCmm = (CompactCommessa)it.next();
       boolean ret = processDocSrv(compactCmm);
       if(!ret) {
          iConsunCmm.commitWithGestoreCommit(true);
          return false;
        }
    }
    //Fix 10416 fine

    //Fix 04567 Mz inizio
    updateDataPrimaUltimaAttivitaCommesse();
    //Fix 04567 Mz fine

    if(iConsunCmm.isProcessOrdine()) {
      for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
        CompactCommessa compactCmm = (CompactCommessa)it.next();
        boolean ret = processRigheOrdAcq(compactCmm);
        //Fix 33593 --inizio
        /*
        if(!ret) {
          iConsunCmm.commitWithGestoreCommit(true);
          return false;
        }
        */
        //Fix 33593 --fine
       }

      for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
        CompactCommessa compactCmm = (CompactCommessa)it.next();
        boolean ret = processRigheOrdLavEsn(compactCmm);
        //Fix 33593 --inizio
        /*
        if(!ret) {
          iConsunCmm.commitWithGestoreCommit(true);
          return false;
        }
        */
        //Fix 33593 --fine
      }

      for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
        CompactCommessa compactCmm = (CompactCommessa)it.next();
        boolean ret = processOrdPrd(compactCmm);
        //Fix 33593 --inizio
        /*
        if(!ret) {
          iConsunCmm.commitWithGestoreCommit(true);
          return false;
        }
        */
        //Fix 33593 --fine
     }
      
      //Fix 10537 inizio
      for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
         CompactCommessa compactCmm = (CompactCommessa)it.next();
         boolean ret = processOrdSrv(compactCmm);
         //Fix 33593 --inizio
         /*
         if(!ret) {
           iConsunCmm.commitWithGestoreCommit(true);
           return false;
         }
         */
         //Fix 33593 --fine
       }
      //Fix 10537 fine
      //31504 inizio
      if(iConsunCmm.isEstrazioneRDA()){
  	      for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
  	          CompactCommessa compactCmm = (CompactCommessa)it.next();
  	          boolean ret = processRigheRDA(compactCmm);
  	         //Fix 33593 --inizio
  	         /*
  	          if(!ret) {
  	            iConsunCmm.commitWithGestoreCommit(true);
  	            return false;
  	          }
  	          */
  	          //Fix 33593 --fine
  	      }
      }
      //31504 fine
    }
    //33950 inizio
    if(iConsunCmm.isEstrazioneStoriciCommessa()) {
       updateDataEstrazioneStoriciCommesse();
    }
    //33950 fine

    iConsunCmm.commitWithGestoreCommit(true);
    return true;
  }

  /**
   * processRigheDocAcq
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processRigheDocAcq(CompactCommessa compactCmm) throws Exception {
     //...FIX 9384 inizio
     //EstrazioneDocAcqRig estrazioneDocAcqRig = new EstrazioneDocAcqRig(iConsunCmm, compactCmm);
    
    EstrazioneDocAcqRig estrazioneDocAcqRig = (EstrazioneDocAcqRig)Factory.createObject(EstrazioneDocAcqRig.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    //Fix 33593 --inizio
     boolean ret = false;
     GestoreCommit gc = null;
     try 
     {
        gc = new GestoreCommit(100, true);
        estrazioneDocAcqRig.setGestoreCommit(gc); //Fix 34308
        ret = estrazioneDocAcqRig.processRighe();
     }
     catch(SQLException ex) {
        ex.printStackTrace(Trace.excStream); //...FIX 7430
     }
     finally{
        if(gc != null)
           gc.fine();
     }
     //  return estrazioneDocAcqRig.processRighe();
     return ret;
     //Fix 33593 --fine
  }

  /**
   * processRigheDocLavEsn
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processRigheDocLavEsn(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneDocLavEsnRig estrazioneDocLavEsnRig = new EstrazioneDocLavEsnRig(iConsunCmm, compactCmm);
    EstrazioneDocLavEsnRig estrazioneDocLavEsnRig = (EstrazioneDocLavEsnRig)Factory.createObject(EstrazioneDocLavEsnRig.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneDocLavEsnRig.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneDocLavEsnRig.processRighe();
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
    //  return estrazioneDocLavEsnRig.processRighe();
    return ret;
    //Fix 33593 --fine
  }

  /**
   * processDocPrd
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processDocPrd(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneDocPrd estrazioneDocPrd = new EstrazioneDocPrd(iConsunCmm, compactCmm);
    EstrazioneDocPrd estrazioneDocPrd = (EstrazioneDocPrd)Factory.createObject(EstrazioneDocPrd.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneDocPrd.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneDocPrd.processRighe();
       
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
 //  return estrazioneDocPrd.processRighe();
    return ret;
    //Fix 33593 --fine
   }

  /**
   * processRigheDocVen
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processRigheDocVen(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneDocVenRig estrazioneDocVenRig = new EstrazioneDocVenRig(iConsunCmm, compactCmm);
    EstrazioneDocVenRig estrazioneDocVenRig = (EstrazioneDocVenRig)Factory.createObject(EstrazioneDocVenRig.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneDocVenRig.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneDocVenRig.processRighe();
       
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
    //return estrazioneDocVenRig.processRighe();
    return ret;
    //Fix 33593 --fine
  }

  /**
   * processDocGenMag
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processDocGenMag(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneDocGenMagRig estrazioneDocGenMagRig = new EstrazioneDocGenMagRig(iConsunCmm, compactCmm);
    EstrazioneDocGenMagRig estrazioneDocGenMagRig = (EstrazioneDocGenMagRig)Factory.createObject(EstrazioneDocGenMagRig.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneDocGenMagRig.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneDocGenMagRig.processRighe();
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
    //return estrazioneDocGenMagRig.processRighe();
    return ret;
    //Fix 33593 --fine
  }

  /**
   * processRigheDocTrasf
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processRigheDocTrasf(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneDocTrasfRig estrazioneDocTrasfRig = new EstrazioneDocTrasfRig(iConsunCmm, compactCmm);
    EstrazioneDocTrasfRig estrazioneDocTrasfRig = (EstrazioneDocTrasfRig)Factory.createObject(EstrazioneDocTrasfRig.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneDocTrasfRig.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneDocTrasfRig.processRighe();
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
    //return estrazioneDocTrasfRig.processRighe();
    return ret;
    //Fix 33593 --fine
  }

  //Fix 10416 inizio
  protected boolean processDocSrv(CompactCommessa compactCmm) throws Exception 
  {
     EstrazioneDocSrv estrazioneDocSrv = (EstrazioneDocSrv)Factory.createObject(EstrazioneDocSrv.class, new Object[]{iConsunCmm, compactCmm});
     //Fix 33593 --inizio
     boolean ret = false;
     GestoreCommit gc = null;
     try 
     {
        gc = new GestoreCommit(100, true);
        estrazioneDocSrv.setGestoreCommit(gc); //Fix 34308
        ret = estrazioneDocSrv.processRighe();
     }
     catch(SQLException ex) {
        ex.printStackTrace(Trace.excStream); //...FIX 7430
     }
     finally{
        if(gc != null)
           gc.fine();
     }
//   return estrazioneDocSrv.processRighe();
     return ret;
     //Fix 33593 --fine
  }
  //Fix 10416 fine
  
  /**
   * processRigheOrdAcq
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processRigheOrdAcq(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneOrdAcqRig estrazioneOrdAcqRig = new EstrazioneOrdAcqRig(iConsunCmm, compactCmm);
    EstrazioneOrdAcqRig estrazioneOrdAcqRig = (EstrazioneOrdAcqRig)Factory.createObject(EstrazioneOrdAcqRig.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneOrdAcqRig.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneOrdAcqRig.processRighe();
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
//  return estrazioneOrdAcqRig.processRighe();
    return ret;
    //Fix 33593 --fine
  }

  /**
   * processRigheOrdLavEsn
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processRigheOrdLavEsn(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneOrdLavEsnRig estrazioneOrdLavEsnRig = new EstrazioneOrdLavEsnRig(iConsunCmm, compactCmm);
    EstrazioneOrdLavEsnRig estrazioneOrdLavEsnRig = (EstrazioneOrdLavEsnRig)Factory.createObject(EstrazioneOrdLavEsnRig.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneOrdLavEsnRig.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneOrdLavEsnRig.processRighe();
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
//  return estrazioneOrdLavEsnRig.processRighe();
    return ret;
    //Fix 33593 --fine
  }

  /**
   * processOrdPrd
   * @param compactCmm CompactCommessa
   * @throws Exception
   * @return boolean
   */
  protected boolean processOrdPrd(CompactCommessa compactCmm) throws Exception {
    //...FIX 9384 inizio
    //EstrazioneOrdPrd estrazioneOrdPrd = new EstrazioneOrdPrd(iConsunCmm, compactCmm);
    EstrazioneOrdPrd estrazioneOrdPrd = (EstrazioneOrdPrd)Factory.createObject(EstrazioneOrdPrd.class, new Object[]{iConsunCmm, compactCmm});
    //...FIX 9384 fine
    //Fix 33593 --inizio
    boolean ret = false;
    GestoreCommit gc = null;
    try 
    {
       gc = new GestoreCommit(100, true);
       estrazioneOrdPrd.setGestoreCommit(gc); //Fix 34308
       ret = estrazioneOrdPrd.processRighe();
    }
    catch(SQLException ex) {
       ex.printStackTrace(Trace.excStream); //...FIX 7430
    }
    finally{
       if(gc != null)
          gc.fine();
    }
//  return estrazioneOrdPrd.processRighe();
    return ret;
    //Fix 33593 --fine
  }

  //Fix 10537 inizio
  protected boolean processOrdSrv(CompactCommessa compactCmm) throws Exception 
  {
     EstrazioneOrdSrv estrazioneOrdSrv = (EstrazioneOrdSrv)Factory.createObject(EstrazioneOrdSrv.class, new Object[]{iConsunCmm, compactCmm});
     //Fix 33593 --inizio
     boolean ret = false;
     GestoreCommit gc = null;
     try 
     {
        gc = new GestoreCommit(100, true);
        estrazioneOrdSrv.setGestoreCommit(gc); //Fix 34308
        ret = estrazioneOrdSrv.processRighe();
     }
     catch(SQLException ex) {
        ex.printStackTrace(Trace.excStream); //...FIX 7430
     }
     finally{
        if(gc != null)
           gc.fine();
     }
//   return estrazioneOrdSrv.processRighe();
     return ret;
     //Fix 33593 --fine
  }
  //Fix 10537 fine
  
  
  /**
   * updateDataPrimaUltimaAttivitaCommesse
   * @throws Exception
   */
  //Fix 04567 Mz inizio
  public void updateDataPrimaUltimaAttivitaCommesse() throws Exception {
    for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
      CompactCommessa compactCmm = (CompactCommessa)it.next();
      updateDataPrimaUltimaAttivitaCommessa(compactCmm.getCommessa());
    }
    iConsunCmm.commitWithGestoreCommit(true);
  }

  /**
   * updateDataPrimaUltimaAttivitaCommessa
   * @param commessa Commessa
   * @throws Exception
   */
  public void updateDataPrimaUltimaAttivitaCommessa(Commessa commessa) throws Exception {
    if(commessa != null) {
      DateRange dateRangeDocumento = getDateRangeDocumento(commessa);
      ensureDataPrimaAttivita(commessa, dateRangeDocumento.getStartDate());
      ensureDataUltimaAttivita(commessa, dateRangeDocumento.getEndDate());
      commessa.save();
    }
  }

  /**
   * ensureDataPrimaAttivita
   * @param commessa Commessa
   * @param dataDocumentoPiuRecente Date
   */
  protected void ensureDataPrimaAttivita(Commessa commessa, java.sql.Date dataDocumentoPiuRecente) {
    if(commessa != null) {
      if((commessa.getDataPrimaAtt() == null) || (getDataUltimaChiusuraDefinitiva(commessa) == null))
        commessa.setDataPrimaAtt(dataDocumentoPiuRecente);
    }
  }

  /**
   * ensureDataUltimaAttivita
   * @param commessa Commessa
   * @param dataDocumentoPiuRecente Date
   */
  protected void ensureDataUltimaAttivita(Commessa commessa, java.sql.Date dataDocumentoPiuRecente) {
    if(commessa != null) {
      commessa.setDataUltimAtt(dataDocumentoPiuRecente);
    }
  }

  /**
   * getDataUltimaChiusuraDefinitiva
   * @param commessa Commessa
   * @return Date
   */
  public java.sql.Date getDataUltimaChiusuraDefinitiva(Commessa commessa) {
    return(commessa.getAmbienteCommessa() == null) ? null : commessa.getAmbienteCommessa().getDataChiusDef();
  }

  /**
   * getDateRangeDocumento
   * @param commessa Commessa
   * @return DateRange
   */
  public static DateRange getDateRangeDocumento(Commessa commessa) {
    DateRange dateRangeDocumento = (DateRange)Factory.createObject(DateRange.class);
    try {
      Database db = ConnectionManager.getCurrentDatabase();
      PreparedStatement ps = cDataDocumentoPiuRecenteStmt.getStatement();
      db.setString(ps, 1, commessa.getIdAzienda());
      db.setString(ps, 2, commessa.getIdCommessa());
      db.setString(ps, 3, String.valueOf(StoricoCommessa.DOCUMENTO));
      ResultSet rs = cDataDocumentoPiuRecenteStmt.executeQuery();
      if(rs.next()) {
        dateRangeDocumento.setStartDate(rs.getDate(1));
        dateRangeDocumento.setEndDate(rs.getDate(2));
      }
    }
    catch(SQLException ex) {
      ex.printStackTrace(Trace.excStream); //...FIX 7430
    }

    return dateRangeDocumento;
  }

  //Fix 04567 Mz fine
  //31504 inizio
  protected boolean processRigheRDA(CompactCommessa compactCmm) throws Exception {
	  EstrazioneRDARig estrazioneRDARig = (EstrazioneRDARig)Factory.createObject(EstrazioneRDARig.class, new Object[]{iConsunCmm, compactCmm});
	    //Fix 33593 --inizio
	    boolean ret = false;
	    GestoreCommit gc = null;
	    try 
	    {
	       gc = new GestoreCommit(100, true);
	       estrazioneRDARig.setGestoreCommit(gc); //Fix 34308
	       ret = estrazioneRDARig.processRighe();
	    }
	    catch(SQLException ex) {
	       ex.printStackTrace(Trace.excStream); //...FIX 7430
	    }
	    finally{
	       if(gc != null)
	          gc.fine();
	    }
//	    return estrazioneRDARig.processRighe();
	     return ret;
	     //Fix 33593 --fine
  }
  //31504 fine
  //33950 inizio
  public void updateDataEstrazioneStoriciCommesse() throws Exception {
     for(Iterator it = iConsunCmm.getCompactCommesse().iterator(); it.hasNext(); ) {
        CompactCommessa compactCmm = (CompactCommessa)it.next();
        updateDataEstrazioneStoriciCommesse(compactCmm.getCommessa());
     }
     iConsunCmm.commitWithGestoreCommit(true);
  }
  
  public void updateDataEstrazioneStoriciCommesse(Commessa commessa) throws Exception {
     if(commessa != null) {
        commessa.setDataEstrazioneStorici(TimeUtils.getCurrentDate());
        commessa.save();
     }
  }
  //33950 fine
}

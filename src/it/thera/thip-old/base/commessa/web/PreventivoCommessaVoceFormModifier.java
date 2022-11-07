package it.thera.thip.base.commessa.web;

import java.sql.SQLException;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2011
 * Company: Softher 2011
 * @author: Linda Fix 14962 08/11/2011
 */
/**
 * Revisions:
 * Number  Date        Owner  Description
 * 29032   25/03/2019  RA	  Revisione preventivi di commessa
 * 29166   04/04/2019  RA	  Revisione preventivi di commessa
 * 29529   04/07/2019  RA	  Gestione TR_COEF_IMP_RIC
 * 29642   17/07/2019  RA	  Revisione preventivi di commessa
 * 30441   07/01/2020  Mekki  Corretta l'anomalia di SAVE_AND_NEW
 * 31094   15/05/2020  RA	  Aggiunto gestione buttone RicalcolaPrezzo
 * 33471   27/04/2021  RA	  Corretto valorizzazione Markup
*/
import javax.servlet.jsp.*;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.gui.cnr.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.web.*;
import com.thera.thermfw.web.servlet.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.risorse.*;
import it.thera.thip.cs.web.*;

public class PreventivoCommessaVoceFormModifier
  extends ThipWebFormModifier
{

  public void writeHeadElements(JspWriter out) throws java.io.IOException
  {
    super.writeHeadElements(out);
    PreventivoCommessaVoceDataCollector rigaBODC = (PreventivoCommessaVoceDataCollector)getBODataCollector();

    PreventivoCommessaVoce bo = (PreventivoCommessaVoce)rigaBODC.getBo();
    if (getMode() == WebForm.COPY)
    {
      bo.setIdRigavPrv(0);
      rigaBODC.setBo(bo);
    }

    out.println(WebJSTypeList.getImportForJSLibrary("it/thera/thip/base/commessa/PreventivoCommessaVoce.js", getServletEnvironment().getRequest()));

  }

  public void writeFormStartElements(JspWriter out) throws java.io.IOException
  {

    PreventivoCommessaVoceDataCollector rigaBODC = (PreventivoCommessaVoceDataCollector)getBODataCollector();
    super.writeFormStartElements(out);
    if (rigaBODC.getMode() == OpenType.NEW || rigaBODC.getMode() == OpenType.COPY)
    {
      //29166 inizio	
      //String tipoRiga = BaseServlet.getStringParameter(getRequest(), "tipoRig");
      //String isDettaglio = BaseServlet.getStringParameter(getRequest(), "Det");
      //String fatherKey = BaseServlet.getStringParameter(getRequest(), "Key");
      String tipoRiga = (String)getServletEnvironment().getRequest().getAttribute("tipoRig");
      String isDettaglio = (String)getServletEnvironment().getRequest().getAttribute("Det");
      String fatherKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
      //29166 fine
      PreventivoCommessaVoce obj = (PreventivoCommessaVoce)rigaBODC.getBo();
      if(tipoRiga != null)//29642
    	  updateTipoRiga(obj, tipoRiga);
      //29529 inizio
      //33471 inizio
      /*
      try{
    	  if(obj.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE && isDettaglio != null && isDettaglio.equals("Y")){
    		  PreventivoCommessaVoce father = (PreventivoCommessaVoce)PersistentObject.elementWithKey(PreventivoCommessaVoce.class, fatherKey, PersistentObject.NO_LOCK);
    		  if(father != null && father.getPrevComRiga() != null &&  father.getPrevComRiga().getTestata() != null && father.getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP ){
    			  obj.setMarkup(father.getMarkup());
    		  }
    	  }
    	  else{
    		  PreventivoCommessaRiga father = (PreventivoCommessaRiga)PersistentObject.elementWithKey(PreventivoCommessaRiga.class, fatherKey, PersistentObject.NO_LOCK);
        	  if(father != null && father.getTestata() != null && father.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP ){
        		  if(obj.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA){
        			  if(obj.getTipoRisorsa() == Risorsa.RISORSE_UMANE){
        				  obj.setMarkup(father.getMarkupUomo());
        			  }
        			  if(obj.getTipoRisorsa() == Risorsa.MACCHINE){
        				  obj.setMarkup(father.getMarkupMacchina());
        			  }
        		  }
        		  else{
        			  obj.setMarkup(father.getMarkupArticolo());
        		  }
        	  }
    	  }    	 
      }
      catch (SQLException ex) {
    	  ex.printStackTrace(Trace.excStream);
      }
      */
      impostaMarkup(isDettaglio, fatherKey, obj);
      //33471 fine
      //29529 fine
      boolean isVoceDettaglio = false;
      if (isDettaglio != null && isDettaglio.equals("Y"))
      {
        String objIdAnnoPrev = KeyHelper.getTokenObjectKey(fatherKey, 2);
        obj.setIdAnnoPrevc(objIdAnnoPrev);
        String objNumeroPrv = KeyHelper.getTokenObjectKey(fatherKey, 3);
        obj.setIdNumeroPrevc(objNumeroPrv);
        String objIdRigavPrv = KeyHelper.getTokenObjectKey(fatherKey, 5);
        obj.setIdRigavPrv(KeyHelper.stringToInt(objIdRigavPrv));
        obj.setIdSubRigavPrv(PreventivoCommessaVoce.getMaxSequenza(fatherKey));
        obj.setSequenzaRiga(obj.getIdSubRigavPrv());
        obj.setGeneraRigaDettaglio(false);
        obj.setSplRiga(PreventivoCommessaVoce.RIGA_SECONDARIA);
        obj.getGestoreCommenti().getObject().setOnDB(true);
        isVoceDettaglio = true;
      }
      //Fix 30441 --inizio
      if(fatherKey == null || fatherKey.equals("")){
    	  String key = BaseServlet.getStringParameter(getRequest(), "Key");
    	  try{
    	    PreventivoCommessaVoce voce = PreventivoCommessaVoce.elementWithKey(key, PersistentObject.OPTIMISTIC_LOCK);
    	    fatherKey = voce.getFatherKey();
    	    if(fatherKey != null && !fatherKey.equals("")) {
    	      obj.setTipoRigav(voce.getTipoRigav());
    	      obj.setTipoRisorsa(voce.getTipoRisorsa());
    	      obj.setLivelloRisorsa(voce.getLivelloRisorsa());
    	    }
    	    impostaMarkup(isDettaglio, fatherKey, obj);//33471
    	  }catch(Exception e){
    		e.printStackTrace(Trace.excStream);
    	  }
      }
      //Fix 30441 --fine
      obj.setFatherKey(fatherKey);
      obj.initSequenza(isVoceDettaglio);
      getBODataCollector().setBo(obj);
      rigaBODC.completaDocumento();
    }
  }

  /**
   * updateTipoRiga
   *
   * @param obj PreventivoCommessaVoce
   * @param tipoRiga String
   *
   */
  protected void updateTipoRiga(PreventivoCommessaVoce obj, String tipoRiga)
  {
    if (tipoRiga != null && tipoRiga.equals("1"))
    {
      obj.setTipoRigav(PreventivoCommessaVoce.TP_RIG_VOCE);
    }
    else if (tipoRiga != null && tipoRiga.equals("2"))
    {
      obj.setTipoRigav(PreventivoCommessaVoce.TP_RIG_ARTICOLO);
    }
    //29166 inizio
    else{
    	obj.setTipoRigav(PreventivoCommessaVoce.TP_RIG_RISORSA);
    	obj.setLivelloRisorsa(Risorsa.RISORSA);
        if (tipoRiga != null && tipoRiga.equals("M"))
        {
          obj.setTipoRigav(PreventivoCommessaVoce.TP_RIG_RISORSA);
          obj.setTipoRisorsa(Risorsa.MACCHINE);
        }
        else if (tipoRiga != null && tipoRiga.equals("U"))
        {
          obj.setTipoRigav(PreventivoCommessaVoce.TP_RIG_RISORSA);
          obj.setTipoRisorsa(Risorsa.RISORSE_UMANE);
        }
    }
    //29166 fine
  }

  public void writeFormEndElements(JspWriter out) throws java.io.IOException
  {
    super.writeFormEndElements(out);
    PreventivoCommessaVoceDataCollector rigaBODC = (PreventivoCommessaVoceDataCollector)getBODataCollector();
    PreventivoCommessaVoce bo = (PreventivoCommessaVoce)rigaBODC.getBo();
    //29032 inizio
    out.println("<script language='JavaScript1.2'>");
    out.println("   document.getElementById('JSPName').value='it/thera/thip/base/commessa/PreventivoCommessaVoce.jsp';"); //Fix 30441
    if(bo.getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
        out.println("   document.getElementById('TR_MARKUP').style.display=displayBlock;");
    }
    else{
    	out.println("   document.getElementById('TR_MARKUP').style.display=displayNone;");
    	out.println("   document.getElementById('Markup').disabled=true;");//29642
    }    
    //29529 inizio
    if(bo.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA){
        out.println("   document.getElementById('TR_COEF_IMP_RIC').style.display=displayBlock;");
    }
    else{
    	out.println("   document.getElementById('TR_COEF_IMP_RIC').style.display=displayNone;");
    }    
    //29529 fine
    //31094 inizio
    if(bo.getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_DA_LST_VEND){
    	out.println("   document.getElementById('RicalcolaPrezzoTD').style.display=displayBlock;");
    }
    else{
    	out.println("   document.getElementById('RicalcolaPrezzoTD').style.display=displayNone;");
    }
    //31094 fine
    out.println("</script>");
    //29032 fine
    if (getMode() == WebForm.NEW)
    {
      out.println("<script language='JavaScript1.2'>");
      out.println("   document.getElementById('thKey').value='';");     
//      out.println("   document.getElementById('thParentKey').value='" + bo.getPrevComRigaKey() + "';");//29166
//      out.println("   document.getElementById('IdRigacPrv').value='" + bo.getIdRigacPrv() + "';");//29166
      out.println("</script>");
    }
//    if ((getMode() == WebForm.NEW || !bo.isKitGestito()) && bo.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE)
//    {
//      out.println("<script language='JavaScript1.2'>");
//      out.println("   document.getElementById('idVoceConRigaSec').style.display=displayNone;");
//      out.println("</script>");
//    }
  }
  
  //33471 inizio
  protected void impostaMarkup(String isDettaglio, String fatherKey, PreventivoCommessaVoce obj) {
	  try {
		  if (obj.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE && isDettaglio != null && isDettaglio.equals("Y")) {
			  PreventivoCommessaVoce father = (PreventivoCommessaVoce) PersistentObject.elementWithKey(PreventivoCommessaVoce.class, fatherKey, PersistentObject.NO_LOCK);
			  if (father != null && father.getPrevComRiga() != null && father.getPrevComRiga().getTestata() != null && 
				  father.getPrevComRiga().getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP) {
				  obj.setMarkup(father.getMarkup());
			  }
		  }
		  else {
			  PreventivoCommessaRiga father = (PreventivoCommessaRiga) PersistentObject.elementWithKey(PreventivoCommessaRiga.class, fatherKey, PersistentObject.NO_LOCK);
			  if (father != null && father.getTestata() != null && father.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP) {
				  if (obj.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA) {
					  if (obj.getTipoRisorsa() == Risorsa.RISORSE_UMANE) {
						  obj.setMarkup(father.getMarkupUomo());
					  }
					  if (obj.getTipoRisorsa() == Risorsa.MACCHINE) {
						  obj.setMarkup(father.getMarkupMacchina());
					  }
				  }
				  else {
					  obj.setMarkup(father.getMarkupArticolo());
				  }
			  }
		  }
	  }
	  catch (SQLException ex) {
		  ex.printStackTrace(Trace.excStream);
	  }
  }
  //33471 fine

}

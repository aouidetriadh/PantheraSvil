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
 * 29166   04/04/2019  RA	  Revisione preventivi di commessa 
 * 29529   05/07/2019  RA	  Gestione campi Markup
 * 29642   17/07/2019  RA	  Revisione preventivi di commessa
 * 29732   28/08/2019  RA	  Gestione Capitoli preventivo e CreaCommessa
 * 29959   16/10/2019  RA	  Corretto valorizzazione rigaAppartenenza
 * 30441   07/01/2020  Mekki  Corretta l'anomalia di SAVE_AND_NEW
 * 31187   07/05/2020  RA	  Gestione MDV per l'attributo QtaUmPrmMag
 * 31265   20/05/2020  RA	  Uso Nome classe origine per gestione MDV
 * 31385   25/06/2020  RA	  Corretto gestione MDV
 * 33357   14/04/2021  RA	  Correto valorizzazione di markup
*/
import javax.servlet.jsp.*;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.gui.ScreenData;//31187
import com.thera.thermfw.gui.cnr.*;
import com.thera.thermfw.persist.Factory;//31187
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.WebForm;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.commessa.PreventivoCommessaRiga;
import it.thera.thip.base.commessa.PreventivoCommessaRigaAppartenenza;
import it.thera.thip.base.commessa.PreventivoCommessaTestata;
import it.thera.thip.cs.web.*;

public class PreventivoCommessaRigaFormModifier
  extends ThipWebFormModifier
{

  public void writeFormStartElements(JspWriter out) throws java.io.IOException
  {
    PreventivoCommessaRigaDataCollector rigaBODC = (PreventivoCommessaRigaDataCollector)getBODataCollector();
    super.writeFormStartElements(out);
    //String tipoRiga = BaseServlet.getStringParameter(getRequest(), "tipoRig");//29166
    String tipoRiga = (String)getServletEnvironment().getRequest().getAttribute("tipoRig");//29166
    if (rigaBODC.getMode() == OpenType.NEW)
    {
      String rigaAppartenenzaKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
      String key = BaseServlet.getStringParameter(getRequest(), "Key"); //Fix 30441
      String comAppar = BaseServlet.getStringParameter(getRequest(), "comAppar");
      PreventivoCommessaRiga obj = (PreventivoCommessaRiga)rigaBODC.getBo();
      if (tipoRiga != null && tipoRiga.equals("S"))
      {
        obj.setSplRiga(PreventivoCommessaRiga.TIPO_RIGA_SOTTO_COMMESSA);
        PreventivoCommessaRigaAppartenenza father = null;
        try
        {
          father = (PreventivoCommessaRigaAppartenenza)PersistentObject.elementWithKey(PreventivoCommessaRigaAppartenenza.class, rigaAppartenenzaKey, PersistentObject.NO_LOCK);
          comAppar = father.getIdCommessa();
          //29529 inizio
          if(father != null && father.getTestata() != null && father.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
			  obj.setMarkupArticolo(father.getMarkupArticolo());
			  obj.setMarkupUomo(father.getMarkupUomo());
			  obj.setMarkupMacchina(father.getMarkupMacchina());
          }
          //29529 fine
          obj.setRigaAppartenenza(father);//29959
          obj.setTestata(father.getTestata());
          obj.setIdRigacPrvApp(father.getIdRigacPrv());
        }
        catch (SQLException ex)
        {
        }
        obj.initSequenza(father);
      }
      //29529 inizio
      else if (tipoRiga != null && tipoRiga.equals("R")) {
          try
          {
        	  PreventivoCommessaTestata testata = (PreventivoCommessaTestata)PersistentObject.elementWithKey(PreventivoCommessaTestata.class, rigaAppartenenzaKey, PersistentObject.NO_LOCK);
        	  if(testata != null && testata.getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
        		  obj.setMarkupArticolo(testata.getMarkupArticolo());
        		  obj.setMarkupUomo(testata.getMarkupUomo());
        		  obj.setMarkupMacchina(testata.getMarkupMacchina());
        	  }
          }
          catch (SQLException ex) {
        	  ex.printStackTrace(Trace.excStream);
          }
          obj.setFatherKey(rigaAppartenenzaKey);
          obj.initSequenza(rigaAppartenenzaKey);
      }
      //29529 fine
      //Fix 30441 --inizio
      else if(key != null && !key.equals("")){
    	  try{
    	    PreventivoCommessaRiga riga = PreventivoCommessaRiga.elementWithKey(key, PersistentObject.OPTIMISTIC_LOCK);
    	    obj.setSplRiga(riga.getSplRiga());
    	    obj.setRigaAppartenenza(riga.getRigaAppartenenza());
            obj.setTestata(riga.getTestata());
            if (riga.getRigaAppartenenza() != null) { //33357
            	obj.setIdRigacPrvApp(riga.getRigaAppartenenza().getIdRigacPrv());
            	obj.initSequenza(riga.getRigaAppartenenza());
            }//33357
            //33357 inizio
            PreventivoCommessaRigaAppartenenza father = riga.getRigaAppartenenza();
            PreventivoCommessaTestata testata = riga.getTestata();
            if(father != null && father.getTestata() != null && father.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
            	obj.setMarkupArticolo(father.getMarkupArticolo());
            	obj.setMarkupUomo(father.getMarkupUomo());
            	obj.setMarkupMacchina(father.getMarkupMacchina());
            }
            else if(testata != null && testata.getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
            	obj.setMarkupArticolo(testata.getMarkupArticolo());
            	obj.setMarkupUomo(testata.getMarkupUomo());
            	obj.setMarkupMacchina(testata.getMarkupMacchina());
            }
            //33357 fine
    	  }catch(Exception e){
    		e.printStackTrace(Trace.excStream);
    	  }
      } 
      //Fix 30441 --fine
      else
      {
        obj.setFatherKey(rigaAppartenenzaKey);
        obj.initSequenza(rigaAppartenenzaKey);
      }
      if (comAppar != null && isCommessaValida(comAppar)) 
        obj.setIdCommessaAppartenenza(comAppar);      
      getBODataCollector().setBo(obj);
      rigaBODC.completaDocumento();
    }
  }

  public void writeFormEndElements(JspWriter out) throws java.io.IOException
  {
    super.writeFormEndElements(out);
    if (getMode() == WebForm.NEW)
    {
      out.println("<script language='JavaScript1.2'>");
      out.println("   document.getElementById('thKey').value='';");
      out.println("</script>");
    }
    PreventivoCommessaRigaDataCollector rigaBODC = (PreventivoCommessaRigaDataCollector)getBODataCollector();
    PreventivoCommessaRiga obj = (PreventivoCommessaRiga)rigaBODC.getBo();
    if (obj.getTestata() != null)
    {
      if(obj.getTestata().getIdCliente() != null && !obj.getTestata().getIdCliente().equals("")) //Fix 25780
      {
        out.println("<script language='JavaScript1.2'>");
        out.println("document.forms[0].IdCliente.value='" + obj.getTestata().getIdCliente() + "';");
        out.println("</script>");
      }
      //29529 inizio
      out.println("<script language='JavaScript1.2'>");
      if(obj.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
    	  out.println("document.getElementById('TR_MARKUP').style.display=displayBlock;");
      }
      else{
    	  out.println("document.getElementById('TR_MARKUP').style.display=displayNone;");    	  
      }
      //29732 inizio
      if(obj.getSplRiga() == PreventivoCommessaRiga.TIPO_RIGA_COMMESSA){
    	  out.println("document.getElementById('TR_CAP_PREV').style.display=displayNone;");
    	  out.println("document.getElementById('TD_BTN_CREA_COMM').style.display=displayBlock;");
    	  if(obj.getCommessa() != null){
    		  out.println("document.getElementById('bottoneCreaCommessa').disabled=true;");
    	  }
    	  else{
    		  out.println("document.getElementById('bottoneCreaCommessa').disabled=false;");
    	  }
      }
      else{
    	  out.println("document.getElementById('TR_CAP_PREV').style.display=displayBlock;");
    	  out.println("document.getElementById('TD_BTN_CREA_COMM').style.display=displayNone;");
      }
      //29732 fine
      out.println("</script>");
      //29529 fine
    }

  }

  public boolean isCommessaValida(String idCommessa){
    boolean isValida = false;
    try{
      if(idCommessa!=null){
        String key=KeyHelper.buildObjectKey(new String[]{Azienda.
        getAziendaCorrente(),idCommessa});
        Commessa commessa=Commessa.elementWithKey(key,PersistentObject.NO_LOCK);
        if(commessa!=null){
          isValida=true;
        }
      }
    }
    catch(SQLException ex){ex.printStackTrace(Trace.excStream);}
    return isValida;
  }

  //29642 inizio
  public void writeHeadElements(JspWriter out) throws java.io.IOException {
    super.writeHeadElements(out);
    PreventivoCommessaRigaDataCollector rigaBODC = (PreventivoCommessaRigaDataCollector)getBODataCollector();

    PreventivoCommessaRiga bo = (PreventivoCommessaRiga)rigaBODC.getBo();
    if (getMode() == WebForm.COPY)
    {
      bo.setIdRigacPrv(0);
      rigaBODC.setBo(bo);
    }
  }
  //29642 fine  
  //31187 inizio
  public void writeBodyEndElements(JspWriter out) throws java.io.IOException  {
    super.writeBodyEndElements(out);
    if(getMode() == WebForm.NEW) {
    	//ScreenData sd = ScreenData.getDefaultScreenData(Factory.getName("PreventivoCommessaRiga", Factory.CLASS_HDR));//31265
		ScreenData sd = ScreenData.getDefaultScreenData("PreventivoCommessaRiga");//31265
		if(sd != null){//31385 inizio
	    	String qtaUmPrmMagMDV = sd.getAttValue("QtaUmPrmMag");
	    	if(qtaUmPrmMagMDV != null && !qtaUmPrmMagMDV.equals("")){
	    		out.println("<script language='JavaScript1.2'>");
	    		out.println("eval('document.forms[0].' + idFromName['QtaUmPrmMag']).value= '" + qtaUmPrmMagMDV + "';");
	    		out.println("</script>"); 
	    	}
		}//31385 fine
    }
  }
  //31187 fine
}

package it.thera.thip.base.commessa.web;

/**
 * <p>Titre : </p>
 * <p>Description : PANTHERA</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Softher</p>
 * @author Linda BHN Fix 14962
 * @version 1.0
 */
/**
 * Revisions:
 * Number  Date        Owner  Description
 * 29529   10/07/2019  RA	  Revisione preventivi di commessa
 * 29870   23/09/2019  RA	  Gestione errori di controlli
 * 33229   01/04/2021  RA     Aggiunto valorizzazione di IdAzienda nel metodo writeHeadElements
*/
import java.io.*;
import javax.servlet.jsp.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.gui.cnr.*;
import com.thera.thermfw.type.*;
import com.thera.thermfw.web.*;
import com.thera.thermfw.web.servlet.*;
import it.thera.thip.base.commessa.*;
import it.thera.thip.base.generale.*;
import it.thera.thip.cs.web.*;

public class PreventivoCommessaCompletaFormModifier
  extends ThipWebFormModifier
{

  public void writeHeadElements(JspWriter out) throws java.io.IOException
  {
    super.writeHeadElements(out);

    if (webForm.getMode() == webForm.NEW || webForm.getMode() == webForm.COPY)
    {
      out.println("<script language='JavaScript1.2' type='text/javascript'> ");
      PreventivoCommessaTestataDataCollector PrevcDC = (PreventivoCommessaTestataDataCollector)getBODataCollector();
      PrevcDC.setDisabilitaControlliRelazione(false);
      PrevcDC.loadAttValue();
      PreventivoCommessaTestata preventivoCommessa = (PreventivoCommessaTestata)PrevcDC.getBo();
      String tpIntes = BaseServlet.getStringParameter(getRequest(), "TipoIntestatarioPrev");
      String idAzienda = BaseServlet.getStringParameter(getRequest(), "IdAzienda");//33229
      String idCliente = BaseServlet.getStringParameter(getRequest(), "idCliente");
      String idDivisione = BaseServlet.getStringParameter(getRequest(), "idDivisione");
      String anagraficoDiBase = BaseServlet.getStringParameter(getRequest(), "IdAnagrafico");
      String contatto = BaseServlet.getStringParameter(getRequest(), "IdRubricaContatti");
      String idCausale = BaseServlet.getStringParameter(getRequest(), "idCausale");
      preventivoCommessa.setIdAzienda(idAzienda);//33229
      preventivoCommessa.setIdCauPrevc(idCausale);
      preventivoCommessa.setTpIntestPrevc(tpIntes.charAt(0));
      if (tpIntes.charAt(0) == PreventivoCommessaTestata.TP_INTES_CLIENTE)
      {
        preventivoCommessa.setIdCliente(idCliente);
        preventivoCommessa.setIdDivisione(idDivisione);
      }
      if (tpIntes.charAt(0) == PreventivoCommessaTestata.TP_INTES_ANAGRAFICO)
      {
        preventivoCommessa.setIdAnagrafico(new Integer(anagraficoDiBase));
      }
      if (tpIntes.charAt(0) == PreventivoCommessaTestata.TP_INTES_CONTATTO)
      {
        preventivoCommessa.setIdRubContatti(Integer.valueOf(contatto));
      }
      preventivoCommessa.completaBO();
      scriviDatiForNumeratore(preventivoCommessa, out, PrevcDC);
      PrevcDC.setBo(preventivoCommessa);
      out.println("</script>");
    }
  }

  public void writeBodyEndElements(JspWriter out) throws java.io.IOException
  {
    super.writeBodyEndElements(out);
    String thErrorPresent = BaseServlet.getStringParameter(getRequest(), "thErrorPresent");//29870
    out.println("<script language='JavaScript1.2'>");
    out.println("disabiltaTipoIntestatario('true');");
    out.println("document.getElementById('thErrorPresent').value = '" + thErrorPresent + "';");//29870
    out.println("</script>");
    out.println(WebJSTypeList.getImportForJSLibrary("it/thera/thip/base/commessa/PreventivoCommessaTestataRedifinizione.js", getServletEnvironment().getRequest())); //Fix  11357

  }

  protected void scriviDatiForNumeratore(PreventivoCommessaTestata prevComTes, JspWriter out, PreventivoCommessaTestataDataCollector prevcDC) throws java.io.IOException
  {
    String idSerie = BaseServlet.getStringParameter(getRequest(), "idSerie");
    String idSottoserie = BaseServlet.getStringParameter(getRequest(), "idSottoSerie");
    String dataDocumento = BaseServlet.getStringParameter(getRequest(), "dataDocumento");
    String numero = BaseServlet.getStringParameter(getRequest(), "numero");
    NumeratoreHandler numHand = prevComTes.getNumeratoreHandler();
    String idNumeratore = numHand.getIdNumeratore();
    String numCifreRese = "" + numHand.getNumeroCifreRese();
    if (dataDocumento != null)
    {
      prevComTes.setDataPrevc(formatDate(dataDocumento));
      if (idSerie != null)
        prevComTes.getNumeratoreHandler().setIdSerie(idSerie);
      if (idSottoserie != null)
        prevComTes.getNumeratoreHandler().setIdSottoserie(idSottoserie);
      prevComTes.getNumeratoreHandler().setIdNumeratore(idNumeratore);
      prevComTes.getNumeratoreHandler().setDataDocumento(formatDate(dataDocumento));
      prevComTes.getNumeratoreHandler().getNumeratore().setNumeroCifreRese(new Integer(numCifreRese));
      if (numero != null && !numero.equals(""))
        prevComTes.getNumeratoreHandler().setNumero(Integer.valueOf(numero));
    }

  }

  public java.sql.Date formatDate(String date)
  {
    if (date != null)
    {
      DateType dt = new DateType();
      return (java.sql.Date)dt.stringToObject(date);
    }
    return null;
  }
  
  //29870 inizio
  public void writeFormStartElements(JspWriter out) throws IOException
  {
    super.writeFormStartElements(out);
    out.println("<input type=\"hidden\" id=\"thErrorPresent\" name=\"thErrorPresent\" value=>");
  }
  //29870 fine

  public void writeFormEndElements(JspWriter out) throws IOException
  {
    if (webForm.getMode() == webForm.NEW || webForm.getMode() == webForm.COPY)
    {
      PreventivoCommessaTestataDataCollector prevcDC = (PreventivoCommessaTestataDataCollector)getBODataCollector();
      PreventivoCommessaTestata prevCom = (PreventivoCommessaTestata)prevcDC.getBo();
      String idCliente = BaseServlet.getStringParameter(getRequest(), "idCliente");
      String idCausale = BaseServlet.getStringParameter(getRequest(), "idCausale");
      String idDivisione = BaseServlet.getStringParameter(getRequest(), "idDivisione");
      prevCom.setIdCliente(idCliente);
      prevCom.setIdCauPrevc(idCausale);
      prevCom.setIdDivisione(idDivisione);
      //prevCom.completaContrattoNuovo();
      scriviDatiForNumeratore(prevCom, out, prevcDC);
      prevcDC.setBo(prevCom);
    }
    //verificaWorkflow(out);
    scriviDatiNumeratore(out);
    //scriviDatiCliente(out);
    out.println("<input type=\"hidden\" id=\"thModalitaAperturaTestata\" name=\"thModalitaAperturaTestata\" value=\"" + BaseServlet.getStringParameter(getRequest(), "Mode") + "\">");
    gestioneCampiMarkup(out);//29529
  }
  
  //29529 inizio
  public void gestioneCampiMarkup(JspWriter out) throws IOException{
      PreventivoCommessaTestataDataCollector prevcDC = (PreventivoCommessaTestataDataCollector)getBODataCollector();
      PreventivoCommessaTestata prevCom = (PreventivoCommessaTestata)prevcDC.getBo();
      out.println("<script language='JavaScript1.2'>");
      if(prevCom.getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP){
    	  out.println("document.getElementById('TR_MARKUP').style.display=displayBlock;");
      }
      else{
    	  out.println("document.getElementById('TR_MARKUP').style.display=displayNone;");    	  
      }
      out.println("</script>");
  }
  //29529 fine

  protected void scriviDatiNumeratore(JspWriter out) throws java.io.IOException
  {
    PreventivoCommessaTestataDataCollector prevcDC = (PreventivoCommessaTestataDataCollector)getBODataCollector();
    PreventivoCommessaTestata prevCom = (PreventivoCommessaTestata)prevcDC.getBo();
    NumeratoreHandler numHand = prevCom.getNumeratoreHandler();

    String idAzienda = "";
    String anno = "";
    String numero = "";
    String idNumeratore = numHand.getIdNumeratore();
    String numCifreRese = "" + numHand.getNumeroCifreRese();
    String idSerie = getValore(numHand.getIdSerie());
    String idSottoserie = getValore(numHand.getIdSottoserie());
    String attrDataDocumento = "NumeratoreHandler.DataDocumento";
    String dataDocumento = (String)prevcDC.get(attrDataDocumento);

    if (prevcDC.getMode() == OpenType.NEW || prevcDC.getMode() == OpenType.COPY)
    {
      try
      {
        idAzienda = numHand.getIdAzienda();
        switch (prevcDC.getMode())
        {
          case OpenType.NEW:
            anno = "" + numHand.getAnno();
            numero = getValore(numHand.getNumero());
            break;
          case OpenType.COPY:
            anno = "" + Integer.toString(TimeUtils.getYear(numHand.getDataDocumento()));
            numero = "";
            break;
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace(Trace.excStream);
      }
    }
    out.println(getField("IdAzienda", idAzienda));
    out.println(getField("Anno", anno));
    out.println(getField("Numero", numero));
    out.println(getField("IdNumeratore", idNumeratore));
    out.println(getField("NumeroCifreRese", numCifreRese));
    out.println(getField("IdSerie", idSerie));
    out.println(getField("IdSottoserie", idSottoserie));
    out.println(getField("DataDocumento", dataDocumento));

    out.println("<script language='JavaScript1.2'>");
    getWebForm().getJSTypeList().startNewType(Type.newType("UpperStringType", 2, 0, null));
    out.println(" " + getWebForm().getJSTypeList().getConstructorForCurrentType(WebForm.FORM_PREF + "NumeratoreHandler", false));
    out.println("setupNF(document.forms[0].NumeratoreHandler$$IdSerie," +
                getWebForm().getJSTypeList().getCurrentTypeName() + ",'" +
                idSerie + "','',true,false,false,null,null);");
    out.println("addIdFromName('NumeratoreHandler.IdSerie', 'NumeratoreHandler$$IdSerie');");
    String nomeAdSerie = "NumeratoreHandler.IdSerie";
    String nlsAdSerie = prevcDC.getClassADCollection().getAttribute(nomeAdSerie).getAttributeNameNLS();
    out.println("NLSFld['" + nomeAdSerie + "'] = '" + nlsAdSerie + "';");

    out.println("</script>");
  }

  protected String getValore(Object o)
  {
    String ret = "";
    if (o != null)
      ret = o.toString();
    return ret;
  }

  protected String getField(String adName, String valore)
  {
    String field = "<input id=\"NumeratoreHandler$$" + adName + "\" " +
      " name=\"NumeratoreHandler." + adName + "\" " +
      "type=\"hidden\" value=\"" + valore + "\">";
    return field;
  }

}

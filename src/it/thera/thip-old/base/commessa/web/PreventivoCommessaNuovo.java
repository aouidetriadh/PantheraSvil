package it.thera.thip.base.commessa.web;

import java.io.*;
import javax.servlet.*;

import com.thera.thermfw.collector.*;
import com.thera.thermfw.web.*;
import com.thera.thermfw.web.servlet.*;
import it.thera.thip.base.commessa.*;

/*
 * @(#)PreventivoCommessaNuovo.java
 */

/**
 * PreventivoCommessaNuovo
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 17/10/2011
 */
/**
 * Revision:
 * Number     Date        Owner  Description
 * 29870      19/09/2019  RA     Corretto azione okRighe in modo di effetuare le controlli
 * 33229      01/04/2021  RA	 Corretto caricamento del BODataCollector
 */
public class PreventivoCommessaNuovo
  extends Save
{

  public void actionOnObject(BODataCollector boDC, ServletEnvironment se)
  {
    String action = BaseServlet.getStringParameter(se.getRequest(), "thAction");
    PreventivoCommessaTestataDataCollector prevComDC = (PreventivoCommessaTestataDataCollector)boDC;
    prevComDC.setNuovoDocumento(true);
    prevComDC.setOnBORecursive();//33229
    if (action != null)
    {
      if (action.equals("OK_TESTATA"))
        okTestata(boDC, se);
      else if (action.equals("OK_RIGHE"))
        okRighe(boDC, se);

    }
  }

  public void okRighe(BODataCollector boDC, ServletEnvironment se)
  {
    PreventivoCommessaTestataDataCollector prevComDC = (PreventivoCommessaTestataDataCollector)boDC;
    setSecurityValues(boDC, se);
    if (prevComDC.check() != BODataCollector.OK)
    {
      se.addErrorMessages(prevComDC.getErrorList().getErrors());
      se.getRequest().setAttribute("ERRORE_OK_RIGHE", "ERRORE_CHECK");
    }

    if (prevComDC.getErrorList().getErrors().isEmpty())
    {
      PreventivoCommessaTestata preventivoCommessaTestata = (PreventivoCommessaTestata)prevComDC.getBo();
      preventivoCommessaTestata.completaBO();
      prevComDC.loadAttValue();
      prevComDC.setNuovoDocumento(false); //29870
      if (prevComDC.save() != BODataCollector.OK)
      {
        se.addErrorMessages(prevComDC.getErrorList().getErrors());
        se.getRequest().setAttribute("ERRORE_OK_RIGHE", "ERRORE_SAVE");
      }
      else
        se.getRequest().setAttribute(REAL_KEY, prevComDC.getBo().getKey());

    }
  }

  public void okTestata(BODataCollector boDC, ServletEnvironment se)
  {
    PreventivoCommessaTestataDataCollector prevComDC = (PreventivoCommessaTestataDataCollector)boDC;
    PreventivoCommessaTestata preventivoCommessaTestata = (PreventivoCommessaTestata)prevComDC.getBo();
    prevComDC.setDisabilitaControlliRelazione(true);
    preventivoCommessaTestata.completaBO();
    setSecurityValues(boDC, se);
    if (prevComDC.check() != BODataCollector.OK)
      se.addErrorMessages(prevComDC.getErrorList().getErrors());
  }

  public void afterProcessAction(BODataCollector boDC, ServletEnvironment se) throws ServletException, IOException
  {
    String action = BaseServlet.getStringParameter(se.getRequest(), "thAction");
    if (boDC.getErrorList().getErrors() != null && !boDC.getErrorList().getErrors().isEmpty())
    {
      if (action != null)
      {
        if (action.equals("OK_TESTATA"))
          super.afterProcessAction(boDC, se);
        else if (action.equals("OK_RIGHE"))
        {
          String erroreOkRighe = (String)se.getRequest().getAttribute("ERRORE_OK_RIGHE");
          if (erroreOkRighe.equals("ERRORE_CHECK"))
            super.afterProcessAction(boDC, se);
          else
          {
            PrintWriter out = se.getResponse().getWriter();
            out.println("<script language='JavaScript1.2'>");
            out.println("window.parent.document.getElementById('thErrorPresent').value = 'Y';"); //29870
            out.println("window.parent.passaATestata();");
            out.println("</script>");

          }
        }
      }
    }
    else
    {
      if (action.equals("OK_TESTATA"))
      {
        PrintWriter out = se.getResponse().getWriter();
        out.println("<script language='JavaScript1.2'>");
        out.println("window.parent.passaATestata();");
        out.println("</script>");
      }
      else
      {
        PrintWriter out = se.getResponse().getWriter();
        out.println("<script language='JavaScript1.2'>");
        String webApplicationPath = se.getWebApplicationPath();
        if (webApplicationPath.endsWith("/"))
          webApplicationPath = webApplicationPath.substring(0, webApplicationPath.length() - 1);
        String servletPath = webApplicationPath + se.getServletPath();
        out.println("window.parent.passaARighe(\"" + servletPath + "\", \"" + boDC.getBo().getKey() + "\");");
        out.println("</script>");

      }
    }
  }
}

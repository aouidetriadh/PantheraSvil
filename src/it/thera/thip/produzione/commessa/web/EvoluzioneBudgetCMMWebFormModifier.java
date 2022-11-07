package it.thera.thip.produzione.commessa.web;

import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.cs.web.ThipWebFormModifier;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
import it.thera.thip.produzione.commessa.EvoluzioneBudgetCommessa;
import it.thera.thip.produzione.commessa.ScostamentoBudgetCommessa;
import it.thera.thip.produzione.commessa.VariaBudgetCommessa;
/**
 * EvoluzioneBudgetCMMWebFormModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/03/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 36460   06/09/2022    RA       Prima struttura
 */
public class EvoluzioneBudgetCMMWebFormModifier extends ThipWebFormModifier {

   public EvoluzioneBudgetCMMWebFormModifier() {
      super();
   }

   public void writeHeadElements(JspWriter out) throws java.io.IOException	{
      super.writeHeadElements(out);
      out.println("<script language=\"javascript1.2\">");
      BODataCollector evoluzioneBudgetDC = (BODataCollector) getBODataCollector();
      evoluzioneBudgetDC.loadAttValue();
      EvoluzioneBudgetCommessa evoluzione = (EvoluzioneBudgetCommessa) evoluzioneBudgetDC.getBo();
      String opener = BaseServlet.getStringParameter(getRequest(), "Opener");
      String objectKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
      if (opener != null && !opener.equals("") && objectKey != null && !objectKey.equals("")) {
         try {
            Commessa commessa = null;
            if (opener.equals("COMMESSA")) {
               commessa = Commessa.elementWithKey(objectKey, PersistentObject.NO_LOCK);
            }
            if (commessa != null) {
               evoluzione.setCommessa(commessa);					
            }
         } 
         catch (SQLException e) {
            e.printStackTrace(Trace.excStream);
         }
      }
      evoluzione.initializeAttDaScreenData();
      evoluzioneBudgetDC.setBo(evoluzione);
      out.println("</script>");
   }
}

package it.thera.thip.produzione.commessa.web;

import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DateType;

import it.thera.thip.cs.web.ThipWebFormModifier;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
/**
 * StampaConsuntivoCommessaWebFormModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/07/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 36252   18/07/2022    RA       Prima struttura
 */
public class StampaConsuntivoCommessaWebFormModifier extends ThipWebFormModifier {

   public StampaConsuntivoCommessaWebFormModifier() {
      super();
   }

   public void writeFormEndElements(JspWriter out) throws java.io.IOException {
      super.writeFormEndElements(out);
      String key = (String)getRequest().getParameter("KEY");
      if(key != null && !key.equals("")) {
         try {
            ConsuntivoCommessa consuntivo = ConsuntivoCommessa.elementWithKey(key, PersistentObject.NO_LOCK);
            if(consuntivo != null) {
               DateType dtt = new DateType();
               out.println("<script language=\"javascript1.2\">");					
               out.println("document.getElementById('DataRiferimento').value = '" + dtt.objectToString(consuntivo.getDataRiferimento()) + "';");
               out.println("document.getElementById('UsaConsuntiviStoricizzati').checked = 'true';");
               out.println("document.getElementById('IdCommessa').value = '" + consuntivo.getIdCommessa() + "';");
               out.println("document.getElementById('Commessa$Descrizione$Descrizione').value = '" + consuntivo.getCommessa().getDescrizione().getDescrizione() + "';");
               out.println("document.getElementById('IdConsuntivo').value = '" + consuntivo.getIdConsuntivo() + "';");
               out.println("document.getElementById('IdCommessaConsuntivo').value = '" + consuntivo.getIdCommessa() + "';");
               if(consuntivo.getDescrizione() !=  null && !consuntivo.getDescrizione().equals(""))
                  out.println("document.getElementById('ConsuntivoCommessa$Descrizione').value = '" + consuntivo.getDescrizione() + "';");
               out.println("onUsaConsuntiviAction();");					
               out.println("</script>");
            }
         } 
         catch (SQLException e) {
            e.printStackTrace();
         }
      }
   }
}

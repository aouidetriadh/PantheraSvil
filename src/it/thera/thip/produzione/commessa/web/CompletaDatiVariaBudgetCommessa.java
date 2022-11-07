package it.thera.thip.produzione.commessa.web;

import java.io.PrintWriter;

import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
/**
 * CompletaDatiVariaBudgetCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 20/12/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34795   20/102/2021    RA       Prima struttura
 */
public class CompletaDatiVariaBudgetCommessa extends BaseServlet {

   protected void processAction(ServletEnvironment se) throws Exception {
      String action = getStringParameter(se.getRequest(), "Action");
      PrintWriter out = se.getResponse().getWriter();
      String idCommessa = getStringParameter(se.getRequest(), "IdCommessa");
      String key = KeyHelper.buildObjectKey(new String [] {Azienda.getAziendaCorrente(), idCommessa});
      Commessa commessa = Commessa.elementWithKey(key, PersistentObject.NO_LOCK);
      if(commessa != null) {
         out.println("<script language='javascript1.2'>");
         if(action.equals("COMPLETA_DATI")) {
            out.println("  parent.fillCampo('IdCommessaBudget', '" + idCommessa + "');");
            out.println("  parent.fillCampo('IdCommessaConsuntivo', '" + idCommessa + "');");
            if(commessa.getStabilimento() != null) {
               out.println("  parent.fillCampo('IdStabilimento', '" + commessa.getIdStabilimento() + "');");
               out.println("  parent.fillCampo('Stabilimento$Descrizione$Descrizione', '" + commessa.getStabilimento().getDescrizione().getDescrizione() + "');");
            }
            else {
               out.println("  parent.fillCampo('IdStabilimento', '');");
               out.println("  parent.fillCampo('Stabilimento$Descrizione$Descrizione', '');");		        
            }
            if(commessa.getCommessaPrincipale() != null) {
               out.println("  parent.fillCampo('IdCommessaPrm', '" + commessa.getIdCommessaPrincipale() + "');");
               out.println("  parent.fillCampo('CommessaPrm$Descrizione$Descrizione', '" + commessa.getCommessaPrincipale().getDescrizione().getDescrizione() + "');");
            }
            else {
               out.println("  parent.fillCampo('IdCommessaPrm', '');");
               out.println("  parent.fillCampo('CommessaPrm$Descrizione$Descrizione', '');");
            }
            if(commessa.getCommessaAppartenenza() != null) {
               out.println("  parent.fillCampo('IdCommessaAppart', '" + commessa.getIdCommessaAppartenenza() + "');");
               out.println("  parent.fillCampo('CommessaAppart$Descrizione$Descrizione', '" + commessa.getCommessaAppartenenza().getDescrizione().getDescrizione() + "');");
            }
            else {
               out.println("  parent.fillCampo('IdCommessaAppart', '');");
               out.println("  parent.fillCampo('CommessaAppart$Descrizione$Descrizione', '');");
            }

            if(commessa.getArticolo() != null) {
               out.println("  parent.fillCampo('IdArticolo', '" + commessa.getIdArticolo() + "');");
               out.println("  parent.fillCampo('Articolo$DescrizioneArticolo$Descrizione', '" + commessa.getArticolo().getDescrizioneArticoloNLS().getDescrizione() + "');");
               out.println("  parent.fillCampo('IdVersione', '" + commessa.getIdVersione() + "');");
               out.println("  parent.fillCampo('Versione$DescrizioneNLS$Descrizione', '" + commessa.getArticoloVersione().getDescrizione().getDescrizione() + "');");
               if(commessa.getConfigurazione() != null) {
                  out.println("  parent.fillCampo('IdEsternoConfig', '" + commessa.getIdEsternoConfig() + "');");
                  out.println("  parent.fillCampo('Configurazione$Descrizione$Descrizione', '" + commessa.getConfigurazione().getDescrizione().getDescrizione() + "');");
               }
               if(commessa.getQtaUmPrm() != null)
                  out.println("  parent.fillCampo('QuantitaPrm', '" + commessa.getQtaUmPrm() + "');");
               out.println("  parent.fillCampo('IdDescUMPrmMag', '" + commessa.getIdUmPrmMag() + " - " + commessa.getUmPrmMag().getDescrizione().getDescrizione() + "');");

            }
            else {
               out.println("  parent.fillCampo('IdArticolo', '');");
               out.println("  parent.fillCampo('Articolo$DescrizioneArticolo$Descrizione', '');");
               out.println("  parent.fillCampo('IdVersione', '');");
               out.println("  parent.fillCampo('Versione$DescrizioneNLS$Descrizione', '');");
               out.println("  parent.fillCampo('IdEsternoConfig', '');");
               out.println("  parent.fillCampo('Configurazione$Descrizione$Descrizione', '');");
               out.println("  parent.fillCampo('QuantitaPrm', '');");
               out.println("  parent.fillCampo('IdDescUMPrmMag', '');");
            }
            if(commessa.getDataEstrazioneStorici() != null) {
               DateType dtt = new DateType();
               out.println("  parent.fillCampo('DataRiferimento', '" + dtt.objectToString(commessa.getDataEstrazioneStorici()) + "');");
            }	
            else {
               out.println("  parent.fillCampo('DataRiferimento', '');");
            }
         }
         out.println("</script>");
      }
   }

}

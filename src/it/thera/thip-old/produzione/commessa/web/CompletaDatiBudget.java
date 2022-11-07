package it.thera.thip.produzione.commessa.web;

import java.io.PrintWriter;
import java.util.Iterator;

import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.datiTecnici.PersDatiTecnici;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
import it.thera.thip.datiTecnici.costi.LinkCompSchema;
/**
 * CompletaDatiConsuntivo
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 02/11/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34585   05/10/2021    RA       Prima struttura
 */
public class CompletaDatiBudget extends BaseServlet {

	protected void processAction(ServletEnvironment se) throws Exception {
		String action = getStringParameter(se.getRequest(), "Action");
		PrintWriter out = se.getResponse().getWriter();
		String idCommessa = getStringParameter(se.getRequest(), "IdCommessa");
		String key = KeyHelper.buildObjectKey(new String [] {Azienda.getAziendaCorrente(), idCommessa});
		Commessa commessa = Commessa.elementWithKey(key, PersistentObject.NO_LOCK);
		if(commessa != null) {
			out.println("<script language='javascript1.2'>");
			if(action.equals("COMPLETA_DATI")) {
				if(commessa.getStabilimento() != null) {
					out.println("  parent.fillCampo('IdStabilimento', '" + commessa.getIdStabilimento() + "');");
		        }
				else {
					out.println("  parent.fillCampo('IdStabilimento', '');");        
				}
				
		        if(commessa.getCommessaPrincipale() != null) {
		        	out.println("  parent.fillCampo('IdCommessaPrm', '" + commessa.getIdCommessaPrincipale() + "');");
		        }
		        else {
		        	out.println("  parent.fillCampo('IdCommessaPrm', '');");
		        }
		        
		        if(commessa.getCommessaAppartenenza() != null) {
		        	out.println("  parent.fillCampo('IdCommessaAppart', '" + commessa.getIdCommessaAppartenenza() + "');");
		        }
		        else {
		        	out.println("  parent.fillCampo('IdCommessaAppart', '');");
		        }
		        
		        if(commessa.getArticolo() != null) {
		        	out.println("  parent.fillCampo('IdArticolo', '" + commessa.getIdArticolo() + "');");
			        out.println("  parent.fillCampo('IdVersione', '" + commessa.getIdVersione() + "');");
			        if(commessa.getConfigurazione() != null) {
				        out.println("  parent.fillCampo('IdEsternoConfig', '" + commessa.getIdEsternoConfig() + "');");
			        }
			        if(commessa.getQtaUmPrm() != null)
			        	out.println("  parent.fillCampo('QuantitaPrm', '" + commessa.getQtaUmPrm() + "');");
			        out.println("  parent.fillCampo('IdUMPrmMag', '" + commessa.getIdUmPrmMag() + "');");
		        }
		        else {
		        	out.println("  parent.fillCampo('IdArticolo', '');");
		        	out.println("  parent.fillCampo('IdVersione', '');");
		        	out.println("  parent.fillCampo('IdEsternoConfig', '');");
		        	out.println("  parent.fillCampo('QuantitaPrm', '');");
			        out.println("  parent.fillCampo('IdUMPrmMag', '');");
		        }
		        		        
		        PersDatiTecnici persDT = PersDatiTecnici.getCurrentPersDatiTecnici();
		        ComponenteCosto cmpRif = persDT.getRiferimento();
		        if(cmpRif != null && commessa.getArticolo() != null && 
		           commessa.getArticolo().getClasseMerclg() != null && 
		           commessa.getArticolo().getClasseMerclg().getSchemaCosto() != null && 
		           commessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti() != null && 
		           !commessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti().isEmpty()) {
		        	boolean containCmpRif = false;
		        	for (Iterator iterator = commessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti().iterator(); iterator.hasNext();) {
						LinkCompSchema cmp = (LinkCompSchema) iterator.next();
						if(cmp.getIdComponenteCosto() != null && cmpRif.getIdComponenteCosto() != null && cmp.getIdComponenteCosto().equals(cmpRif.getIdComponenteCosto()))
							containCmpRif = true;
					}
		        	if(containCmpRif) {
		        		out.println("  parent.fillCampo('IdComponenteTotali', '" + cmpRif.getIdComponenteCosto() + "');");
		        		out.println("  parent.fillCampo('ComponenteTotali$Descrizione$Descrizione', '" + cmpRif.getDescrizione().getDescrizione() + "');");		        		
		        	}
		        	else {
		        		out.println("  parent.fillCampo('IdComponenteTotali', '');");
			        	out.println("  parent.fillCampo('ComponenteTotali$Descrizione$Descrizione', '');");
		        	}
		        }		        
			}
			out.println("</script>");
		}
	}

}

package it.thera.thip.produzione.commessa.web;

import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.cs.web.ThipWebFormModifier;
import it.thera.thip.datiTecnici.PersDatiTecnici;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
import it.thera.thip.datiTecnici.costi.LinkCompSchema;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
/**
 * ConsuntivoCommessaWebFormModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 05/10/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   05/10/2021    RA       Prima struttura
 */
public class ConsuntivoCommessaWebFormModifier extends ThipWebFormModifier {

	public ConsuntivoCommessaWebFormModifier() {
		super();
	}
	
	public void writeHeadElements(JspWriter out) throws java.io.IOException	{
	    super.writeHeadElements(out);
	    if (webForm.getMode() == webForm.NEW) {
	        out.println("<script language='JavaScript1.2' type='text/javascript'> ");
	        BODataCollector consuntivoDC = (BODataCollector)getBODataCollector();
	        consuntivoDC.loadAttValue();
	        ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)consuntivoDC.getBo();
	        String commessaKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
			if(commessaKey != null && !commessaKey.equals("")) {
				try {
					Commessa commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
					if(commessa != null) {
						consuntivo.setCommessa(commessa);
						consuntivo.setStabilimento(commessa.getStabilimento());
						consuntivo.setCommessaPrm(commessa.getCommessaPrincipale());
						consuntivo.setCommessaAppart(commessa.getCommessaAppartenenza());
						consuntivo.setLivelloCommessa(commessa.getLivelloCommessa());
						consuntivo.setArticolo(commessa.getArticolo());
						consuntivo.setVersione(commessa.getArticoloVersione());
						consuntivo.setConfigurazione(commessa.getConfigurazione());
						consuntivo.setQuantitaPrm(commessa.getQtaUmPrm());
						consuntivo.setUMPrmMag(commessa.getUmPrmMag());
				        PersDatiTecnici persDT = PersDatiTecnici.getCurrentPersDatiTecnici();
				        ComponenteCosto cmpRif = persDT.getRiferimento();
				        if(cmpRif != null && 
				           commessa.getArticolo().getClasseMerclg() != null && 
				           commessa.getArticolo().getClasseMerclg().getSchemaCosto() != null && 
				           commessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti() != null && 
				           !commessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti().isEmpty()) {
				        	for (Iterator iterator = commessa.getArticolo().getClasseMerclg().getSchemaCosto().getComponenti().iterator(); iterator.hasNext();) {
								LinkCompSchema cmp = (LinkCompSchema) iterator.next();
								if(cmp.getIdComponenteCosto().equals(cmpRif.getIdComponenteCosto()))
									consuntivo.setComponenteTotali(cmpRif);	
							}
				        }
				        
				        if(commessa.getDataEstrazioneStorici() != null) {
				        	if(consuntivo.isUsaDataEstrazioneStorici()) {
				        		consuntivo.setDataRiferimento(commessa.getDataEstrazioneStorici());				
				        	}
				        }
					}
				}
				catch(SQLException e) {
				      e.printStackTrace(Trace.excStream);
				    }
			}
	        consuntivoDC.setBo(consuntivo);
	        out.println("</script>");
	    }
	}
	
	public void writeFormEndElements(JspWriter out) throws java.io.IOException {
		super.writeFormEndElements(out);
		out.println("<script language=\"javascript1.2\">");
		ConsuntivoCommessa consuntivo = (ConsuntivoCommessa)getBODataCollector().getBo();
		String mode = (String)getRequest().getParameter("thAction");
		if(mode.equalsIgnoreCase("NEW")) {
			try {				
				out.println("  eval(\"document.forms[0].\" + idFromName[\"IdConsuntivo\"]).value = '0';");
				String commessaKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
				if(commessaKey != null && !commessaKey.equals("")) {
					Commessa commessa = Commessa.elementWithKey(commessaKey, PersistentObject.NO_LOCK);
					if(commessa != null) {
				        out.println("  enableSearchComponent(\"Commessa\", false, false);");
				        out.println("  eval(\"document.forms[0].\" + idFromName[\"IdCommessa\"]).disabled = true;");
				        if(commessa.getDataEstrazioneStorici() != null) {
				        	DateType dtt = new DateType();
				        	if(consuntivo.isUsaDataEstrazioneStorici()) {
				        		out.println("  document.getElementById(\"DataRiferimento\").value = \"" + dtt.objectToString(commessa.getDataEstrazioneStorici()) + "\";");
				        		out.println("  document.getElementById(\"DataRiferimento\").disabled = true;");
				        		out.println("  document.getElementById(\"thCalButtonDataRiferimento\").disabled = true;");
				        	}
				        	out.println("  DataEstrazioneStoriciCommessa = \"" + dtt.objectToString(commessa.getDataEstrazioneStorici()) + "\";");
				        }
					}
				}
			}
		    catch(SQLException e) {
		      e.printStackTrace(Trace.excStream);
		    }
		}
		else if(mode.equalsIgnoreCase("UPDATE")) {
			if(consuntivo.getCommessa().getDataEstrazioneStorici() != null /*&& consuntivo.isUsaDataEstrazioneStorici()*/ && consuntivo.getStatoAvanzamento() == ConsuntivoCommessa.PROVVISORIO) {
				DateType dtt = new DateType();
				if(consuntivo.isUsaDataEstrazioneStorici()) {
					out.println("  eval(\"document.forms[0].\" + idFromName[\"DataRiferimento\"]).disabled = true;");
					out.println("  document.getElementById(\"thCalButtonDataRiferimento\").disabled = true;");
				}
	        	out.println("  DataEstrazioneStoriciCommessa = \"" + dtt.objectToString(consuntivo.getCommessa().getDataEstrazioneStorici()) + "\";");
	        }
			
			if(consuntivo.getStatoAvanzamento() == ConsuntivoCommessa.DEFINITIVO) {
				ConsuntivoCommessa ultimoConsuntivoDefinitivo = consuntivo.getCommessa().getUltimaConsuntivoCommessaDefinitivo();
				if(!consuntivo.isCollegatoAVariazioneBudget() && ultimoConsuntivoDefinitivo != null && ultimoConsuntivoDefinitivo.getKey().equals(consuntivo.getKey()))
					out.println("  eval(\"document.forms[0].\" + idFromName[\"StatoAvanzamento\"]).disabled = false;");
				else
					out.println("  eval(\"document.forms[0].\" + idFromName[\"StatoAvanzamento\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"UsaDataEstrazioneStorici\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"Descrizione\"]).disabled = true;");
				out.println("  enableSearchComponent(\"ComponenteTotali\", false, false);");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"IdComponenteTotali\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].DatiComuniEstesi$$Stato\").disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"Consolidato\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"EstrazioneOrdini\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"EstrazioneRichieste\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"TipoVisualizzazione\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"DataRiferimento\"]).disabled = true;");
				out.println("  document.getElementById(\"thCalButtonDataRiferimento\").disabled = true;");
				out.println("  document.getElementById(\"AggiornaBUT\").disabled = true;");				
			}
		}
		out.println("</script>");
	}
}

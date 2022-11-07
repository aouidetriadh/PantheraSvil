package it.thera.thip.produzione.commessa.web;

import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.cs.web.ThipWebFormModifier;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
import it.thera.thip.produzione.commessa.VariaBudgetCommessa;
/**
 * VariaBudgetCommessaWebFormModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 20/12/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34795   20/12/2021    RA       Prima struttura
 */
public class VariaBudgetCommessaWebFormModifier extends ThipWebFormModifier {

	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.VariaBudgetCommessa";
	public VariaBudgetCommessaWebFormModifier() {
		super();
	}
	
	public void writeHeadElements(JspWriter out) throws java.io.IOException	{
	    super.writeHeadElements(out);
	    out.println("<script language=\"javascript1.2\">");	    
	    if (webForm.getMode() == webForm.NEW) {	        
	        BODataCollector variaBudgetDC = (BODataCollector)getBODataCollector();
	        variaBudgetDC.loadAttValue();
	        VariaBudgetCommessa variazione = (VariaBudgetCommessa)variaBudgetDC.getBo();
	        String opener = BaseServlet.getStringParameter(getRequest(), "Opener");
	        String objectKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
	        if(opener != null && !opener.equals("") && objectKey != null && !objectKey.equals("")) {
	        	try {
	        		Commessa commessa = null;
		        	if(opener.equals("COMMESSA")) {
		        		commessa = Commessa.elementWithKey(objectKey, PersistentObject.NO_LOCK);
		        	}
		        	else if(opener.equals("BUDGET_CMM")) {
		        		BudgetCommessa budget = BudgetCommessa.elementWithKey(objectKey, PersistentObject.NO_LOCK);
		        		if(budget != null) {
		        			commessa = budget.getCommessa();
		        			variazione.setIdBudget(budget.getIdBudget());
		        		}
		        	}
		        	
					if(commessa != null) {
						variazione.setCommessa(commessa);
						variazione.setStabilimento(commessa.getStabilimento());
						variazione.setCommessaPrm(commessa.getCommessaPrincipale());
						variazione.setCommessaAppart(commessa.getCommessaAppartenenza());
						variazione.setArticolo(commessa.getArticolo());
						variazione.setVersione(commessa.getArticoloVersione());
						variazione.setConfigurazione(commessa.getConfigurazione());
						variazione.setQuantitaPrm(commessa.getQtaUmPrm());
						variazione.setUMPrmMag(commessa.getUmPrmMag());
						variazione.setDataRiferimento(commessa.getDataEstrazioneStorici());
					}
	        	}
				catch(SQLException e) {
				      e.printStackTrace(Trace.excStream);
				}
	        	
	        }
	        variaBudgetDC.setBo(variazione);	        
	    }
	    out.println("</script>");
	}
	
	public void writeFormEndElements(JspWriter out) throws java.io.IOException {
		super.writeFormEndElements(out);
		out.println("<script language=\"javascript1.2\">");
		out.println("ErrVariazione = '" + ResourceLoader.getString(RES_FILE, "ErrVariazione") + "';");
		VariaBudgetCommessa variazione = (VariaBudgetCommessa)getBODataCollector().getBo();
		String mode = (String)getRequest().getParameter("thAction");
		if(mode.equalsIgnoreCase("UPDATE")) {			
			if(variazione.getStatoAvanzamento() == ConsuntivoCommessa.DEFINITIVO) {
				out.println("  eval(\"document.forms[0].\" + idFromName[\"Descrizione\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].DatiComuniEstesi$$Stato\").disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"DataRiferimento\"]).disabled = true;");
				out.println("  document.getElementById(\"thCalButtonDataRiferimento\").disabled = true;");	
				out.println("  enableSearchComponent(\"ConsuntivoCommessa\", false, false);");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"IdConsuntivo\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"Note\"]).disabled = true;");
			}
		}
		out.println("</script>");
	}
}

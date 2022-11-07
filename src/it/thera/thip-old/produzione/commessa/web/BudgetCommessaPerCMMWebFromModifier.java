package it.thera.thip.produzione.commessa.web;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.web.WebFormModifier;

import it.thera.thip.produzione.commessa.BudgetCommessa;
/**
 * BudgetCommessaPerCMMWebFromModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 07/03/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 35382   07/03/2022    RA       Prima struttura
 */
public class BudgetCommessaPerCMMWebFromModifier extends WebFormModifier {
	
	protected static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";
	
	public void writeHeadElements(JspWriter out) throws IOException {}

	public void writeBodyStartElements(JspWriter out) throws IOException 
	{
	   out.println("<script language=\"javascript1.2\">");
	   out.println("isPerCommessa = true;");
	   out.println("</script>");    
	}

	public void writeFormStartElements(JspWriter out) throws IOException {}

	public void writeFormEndElements(JspWriter out) throws IOException {
		gestioneBudgetCommessa(out);
		completaBudgetPerCommessaForm(out);	
	}

	public void writeBodyEndElements(JspWriter out) throws IOException {}

	public void gestioneBudgetCommessa(JspWriter out) throws IOException {
		out.println("<script language=\"javascript1.2\">");
		BudgetCommessa budget = (BudgetCommessa)getBODataCollector().getBo();
		if(budget != null) {
			String errCarica1 = ResourceLoader.getString(RES_FILE, "ErrCarica1");
			out.println("errCarica1 = \"" + errCarica1 + "\";");
			if(budget.getStatoAvanzamento() == BudgetCommessa.PROVVISORIO) {
				if(budget.isSingoloProovisorio()) {
					out.println(" var isSingoloProovisorio = true;");
					String confirm1 = ResourceLoader.getString(RES_FILE, "Confirm1");
					String confirm2 = ResourceLoader.getString(RES_FILE, "Confirm2");
					out.println("confirm1 = \"" + confirm1 + "\";");
					out.println("confirm2 = \"" + confirm2 + "\";");
				}
				else {
					out.println(" var isSingoloProovisorio = false;");
					out.println(" enableSearchComponent(\"Preventivo\", false, false);");
					out.println("  eval(\"document.forms[0].\" + idFromName[\"IdAnnoPreventivo\"]).disabled = true;");
					out.println("  eval(\"document.forms[0].\" + idFromName[\"IdNumeroPreventivo\"]).disabled = true;");
					out.println(" document.getElementById(\"CaricaBUT\").disabled = true;");
				}
			}
			else {
				BudgetCommessa ultimoBudgetDefinitivo = budget.getCommessa().getUltimoBudgetCommessaDefinitivo();
				if(!budget.isCollegatoAVariazioneBudget() && ultimoBudgetDefinitivo != null && ultimoBudgetDefinitivo.getKey().equals(budget.getKey()))
					out.println("  eval(\"document.forms[0].\" + idFromName[\"StatoAvanzamento\"]).disabled = false;");
				else
					out.println("  eval(\"document.forms[0].\" + idFromName[\"StatoAvanzamento\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"Descrizione\"]).disabled = true;");
				out.println("  enableSearchComponent(\"ComponenteTotali\", false, false);");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"IdComponenteTotali\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"DataRiferimento\"]).disabled = true;");
				out.println("  document.getElementById(\"thCalButtonDataRiferimento\").disabled = true;");
				out.println(" enableSearchComponent(\"Preventivo\", false, false);");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"IdAnnoPreventivo\"]).disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"IdNumeroPreventivo\"]).disabled = true;");
				out.println(" document.getElementById(\"CaricaBUT\").disabled = true;");
				out.println("  eval(\"document.forms[0].\" + idFromName[\"DatiComuniEstesi.Stato\"]).disabled = true;");
			}
		}
		out.println("</script>");		
	}
	
	public void completaBudgetPerCommessaForm(JspWriter out) throws java.io.IOException{
		//writeFrameDettaglioBudget(out);
	}
	
//	public void writeFrameDettaglioBudget(JspWriter out) throws java.io.IOException{
//		out.println("<div id=\"DettaglioBudgetDiv\" style=\"margin:0px 0px 0px 0px;\">"); 
//		out.println("<table cellpadding=\"0\" cellspacing=\"0\" style=\"height: calc(100% - 160px);width: calc(100% - 10px);\"  >");	
//		out.println("<tr>");
//		out.println("<td  nowrap=\"true\">");
//		out.println("<iframe height=\"100%\" width=\"100%\" id=\"BudgetCommessaDettaglio\" name=\"BudgetCommessaDettaglio\" src=\"\"  style=\"border:0px;\"></iframe>");
//		out.println("</td>");
//		out.println("</tr>");
//		out.println("</table>");
//		out.println("</div>");
//	}
}

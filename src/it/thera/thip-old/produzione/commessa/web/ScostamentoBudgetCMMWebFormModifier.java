package it.thera.thip.produzione.commessa.web;

import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.cs.web.ThipWebFormModifier;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
import it.thera.thip.produzione.commessa.ScostamentoBudgetCommessa;
/**
 * ScostamentoBudgetCommessaWebFormModifier
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/03/2022
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 35837   29/03/2022    RA       Prima struttura
 */
public class ScostamentoBudgetCMMWebFormModifier extends ThipWebFormModifier {

	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa";
	public ScostamentoBudgetCMMWebFormModifier() {
		super();
	}
	
	public void writeHeadElements(JspWriter out) throws java.io.IOException	{
	    super.writeHeadElements(out);
		out.println("<script language=\"javascript1.2\">");
		BODataCollector scostamentoBudgetDC = (BODataCollector) getBODataCollector();
		scostamentoBudgetDC.loadAttValue();
		ScostamentoBudgetCommessa scostamento = (ScostamentoBudgetCommessa) scostamentoBudgetDC.getBo();
		scostamento.setDataRiferimento(TimeUtils.getCurrentDate());
		String opener = BaseServlet.getStringParameter(getRequest(), "Opener");
		String objectKey = BaseServlet.getStringParameter(getRequest(), "ObjectKey");
		if (opener != null && !opener.equals("") && objectKey != null && !objectKey.equals("")) {
			try {
				Commessa commessa = null;
				if (opener.equals("COMMESSA")) {
					commessa = Commessa.elementWithKey(objectKey, PersistentObject.NO_LOCK);
				}
				if (commessa != null) {
					scostamento.setCommessa(commessa);					
					BudgetCommessa budgetCommessa = commessa.getUltimoBudgetCommessa();
					if(budgetCommessa != null) {
						scostamento.setBudgetCommessa(budgetCommessa);
						scostamento.setDataBudget(budgetCommessa.getDataRiferimento());
					}
					
					ConsuntivoCommessa consuntivoCommessa = commessa.getUltimoConsuntivoCommessa();
					if(consuntivoCommessa != null) {
						scostamento.setConsuntivoCommessa(consuntivoCommessa);
						scostamento.setDataConsuntivo(consuntivoCommessa.getDataRiferimento());
					}
				}
			} 
			catch (SQLException e) {
				e.printStackTrace(Trace.excStream);
			}
		}
		scostamento.initializeAttDaScreenData();
		scostamentoBudgetDC.setBo(scostamento);
		out.println("</script>");
	}
}

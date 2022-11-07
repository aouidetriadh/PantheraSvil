package it.thera.thip.produzione.commessa.web;

import java.sql.Date;
import java.sql.SQLException;

import com.thera.thermfw.ad.ClassAD;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;

import it.thera.thip.cs.web.AziendaDOList;
import it.thera.thip.produzione.commessa.BudgetCommessa;
import it.thera.thip.produzione.commessa.ConsuntivoCommessaTM;
/**
 * VariaBudgetCMMRicConsuntivoCMMDOList
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 20/12/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34795   20/12/2021    RA       Prima struttura
 */
public class VariaBudgetCMMRicConsuntivoCMMDOList extends AziendaDOList {
	protected String idAzienda = null;
	protected String idCommessa = null;
	protected Integer idBudget = null;
	
	public void setRestrictCondition(ClassAD[] attributes, String[] values) {
		if (values[0] != NULL_VALUE) {
			idAzienda = values[0];
		}
		if (values[1] != NULL_VALUE) {
			idCommessa = values[1];
		}
		if (values[2] != NULL_VALUE) {
			idBudget = new Integer(values[2]);
		}
		ClassAD[] xAttributes = new ClassAD[] {attributes[0] , attributes[1]};
		String[] xValues = new String[] {values[0] , values[1]};
		super.setRestrictCondition(xAttributes, xValues);
	}
	
	public Date getDataRiferimentoBudget() {
		if(idAzienda != null && idCommessa != null && idBudget != null) {
			String budgetCommessaKey = KeyHelper.buildObjectKey(new Object[] {idAzienda, idBudget, idCommessa});
			try {
				BudgetCommessa budget = BudgetCommessa.elementWithKey(budgetCommessaKey, PersistentObject.NO_LOCK);
				if(budget != null)
					return budget.getDataRiferimento();
			}
			catch(SQLException ex) {
				ex.printStackTrace(Trace.excStream);
			} 
		}
		return null;
	}
	
	public String getSpecificWhereClause(){
		String where = null;
		String specWhereClause = super.getSpecificWhereClause();
		Date dataRiferimentoBudget = getDataRiferimentoBudget();
		if(dataRiferimentoBudget != null) {
			where = "(" + ConsuntivoCommessaTM.TABLE_NAME + "." + ConsuntivoCommessaTM.DATA_RIFERENTO + " >= '" + dataRiferimentoBudget + "' )";
		}
		if(where != null) {
			if(specWhereClause == null)
				specWhereClause = where;
			else
				specWhereClause += " AND " + where;
		}
		specificWhereClause = specWhereClause;
		return specWhereClause;
	}
}

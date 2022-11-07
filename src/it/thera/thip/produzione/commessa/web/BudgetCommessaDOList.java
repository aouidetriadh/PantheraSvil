package it.thera.thip.produzione.commessa.web;

import it.thera.thip.cs.web.AziendaDOList;
import it.thera.thip.produzione.commessa.BudgetCommessaTM;
/**
 * BudgetCommessaDOList
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 01/11/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34585   01/11/2021    RA       Prima struttura
 */
public class BudgetCommessaDOList extends AziendaDOList {
	
	public String getSpecificWhereClause(){
		String specWhereClause = super.getSpecificWhereClause();
		String where = "(" + BudgetCommessaTM.TABLE_NAME + "." + BudgetCommessaTM.R_COMMESSA_APP + " IS NULL )";
		if(specWhereClause == null)
			specWhereClause = where;
		else
			specWhereClause += " AND " + where;
		specificWhereClause = specWhereClause;
		return specWhereClause;
	}
}

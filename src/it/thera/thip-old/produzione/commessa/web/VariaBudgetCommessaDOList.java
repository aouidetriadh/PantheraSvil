package it.thera.thip.produzione.commessa.web;

import it.thera.thip.cs.web.AziendaDOList;
import it.thera.thip.produzione.commessa.VariaBudgetCommessaTM;
/**
 * VariaBudgetCommessaDOList
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 14/12/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34795   14/12/2021    RA       Prima struttura
 */
public class VariaBudgetCommessaDOList extends AziendaDOList {
	
	public String getSpecificWhereClause(){
		String specWhereClause = super.getSpecificWhereClause();
		String where = "(" + VariaBudgetCommessaTM.TABLE_NAME + "." + VariaBudgetCommessaTM.R_COMMESSA_APP + " IS NULL )";
		if(specWhereClause == null)
			specWhereClause = where;
		else
			specWhereClause += " AND " + where;
		specificWhereClause = specWhereClause;
		return specWhereClause;
	}
}

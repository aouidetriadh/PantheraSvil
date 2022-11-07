package it.thera.thip.produzione.commessa.web;

import it.thera.thip.cs.web.AziendaDOList;
import it.thera.thip.produzione.commessa.ConsuntivoCommessaTM;
/**
 * ConsuntivoCommessaDOList
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 05/10/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   05/10/2021    RA       Prima struttura
 */
public class ConsuntivoCommessaDOList extends AziendaDOList {
	
	public String getSpecificWhereClause(){
		String specWhereClause = super.getSpecificWhereClause();
		String where = "(" + ConsuntivoCommessaTM.TABLE_NAME + "." + ConsuntivoCommessaTM.R_COMMESSA_APP + " IS NULL )";
		if(specWhereClause == null)
			specWhereClause = where;
		else
			specWhereClause += " AND " + where;
		specificWhereClause = specWhereClause;
		return specWhereClause;
	}
}

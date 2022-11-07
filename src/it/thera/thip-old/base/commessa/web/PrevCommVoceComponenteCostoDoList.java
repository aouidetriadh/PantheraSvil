package it.thera.thip.base.commessa.web;

import com.thera.thermfw.ad.ClassAD;
import com.thera.thermfw.gui.cnr.DOList;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.datiTecnici.costi.ComponenteCostoTM;
import it.thera.thip.datiTecnici.costi.LinkCompSchemaTM;
/**
 * Revisions:
 * Number   Date          Owner       Description
 * 36730    11/10/2022    RA         Prima versione
 */
public class PrevCommVoceComponenteCostoDoList extends DOList {
	public void setRestrictCondition(ClassAD[] attributes, String[] values) {
		specificWhereClause = "PRIM." + ComponenteCostoTM.ID_AZIENDA +" = '"+Azienda.getAziendaCorrente()+"'";
		if (values[1] != NULL_VALUE)
			specificWhereClause = specificWhereClause + " AND " + getIdCompCostoWhereClause(values[1]);
	}
	
	public String getIdCompCostoWhereClause(String idSchemaCosto) {
		return	ComponenteCostoTM.ID_COMPON_COSTO + " IN (SELECT " + LinkCompSchemaTM.ID_COMPON_COSTO +
				" FROM " + LinkCompSchemaTM.TABLE_NAME +
		        " WHERE " + LinkCompSchemaTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "'" +
		        " AND " + LinkCompSchemaTM.ID_SCHEMA_COSTO + " = '" + idSchemaCosto + "')";
	}
}	

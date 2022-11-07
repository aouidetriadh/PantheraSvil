package it.thera.thip.produzione.commessa;

import java.sql.SQLException;

import com.thera.thermfw.persist.TransientTableManager;
/**
 * CostiCommessaDetGruppoTTM
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 17/08/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   17/08/2021    RA       Prima struttura
 */
public class CostiCommessaDetGruppoTTM extends TransientTableManager {
	public static final String CLASS_NAME = CostiCommessaDetGruppo.class.getName();
	
	public static final String COS_LIV = "COS_LIV";
	public static final String COS_LINF = "COS_LINF";
	public static final String COS_TOT = "COS_TOT";
	public static final String TEMPO_LIV = "TEMPO_LIV";
	public static final String TEMPO_LINF = "TEMPO_LINF";
	public static final String TEMPO_TOT = "TEMPO_TOT";
	
	public CostiCommessaDetGruppoTTM() throws SQLException {
		 super();
	}
	
	protected void initialize() throws SQLException{
		setObjClassName(CLASS_NAME);
	}

	protected void initializeRelation() throws SQLException{
		addAttribute("CostoLivello", COS_LIV);
		addAttribute("CostoLivelloInf", COS_LINF);
		addAttribute("CostoTotale", COS_TOT);
		addAttribute("TempoLivello", TEMPO_LIV);
		addAttribute("TempoLivelloInf", TEMPO_LINF);
		addAttribute("TempoTotale", TEMPO_TOT);
	}
}

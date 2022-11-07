package it.thera.thip.produzione.commessa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Database;

import it.thera.thip.base.commessa.AmbienteCommessa;

/**
 * EstrazioneStoriciCommessaBatch
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/07/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   29/07/2021    RA       Prima struttura
 */
public class EstrazioneStoriciCommessaBatch extends ConsuntivazioneCommesse {

	public EstrazioneStoriciCommessaBatch() {
		super();
		setEstrazioneStoriciCommessa(true);
	}
	
	public boolean createReportInternal() {
		try {
			iDataCorrente = TimeUtils.getCurrentDate();
			//Svuotamento cache VariabiliCommessaElem
			VariabiliCommessaElem.clearVariabiliCommessaHash();
			getLogger();
			getLogger().printForcedMessage(">>>> INIZO CONSUNTIVAZIONE COMMESSE");

			if(iFiltroAmbientiCommesse.getCondizioneWhere() == null || iFiltroAmbientiCommesse.getCondizioneWhere().trim().length() == 0)
				initCondizioniFiltroForBatch();

			setExecutePrint(isStampaDiretta());

			if(getDataRiferimento() == null) {
				iDataRifNull = true;
				setDataRiferimento(iDataCorrente);
			}

			setProcessOrdine(true);
			setEstrazioneOrdini(true);
			setEstrazioneRDA(true);

			recuperoAmbientiList();

			job.setReportCounter((short)0);
			iAnmAvailableRep = createAnomaliaConsuntivoReport();
			iAnomalieReportNr = iAnmAvailableRep.getReportNr();

			ConnectionManager.commit();

			getLogger().printForcedMessage(">>>> INIZO PROCESSO ESTRAZIONE COMMESSE");

			if(iAmbienti.size() > 0) {
				recupDataUltChiusDef();
				updateAmbientiCmmToAvviato();

				getLogger().startTimeMsg("INIZIO recupero Commesse");
				recuperoCommesseList();
				getLogger().printTime("FINE recupero Commesse [" + iCompactCommesse.size() + "]");

				getLogger().startTimeMsg("INIZIO cancellazione storici");
				
				cancellaStroiciECosti();
				
				getLogger().printTime("FINE cancellazione storici");

				getLogger().startTimeMsg("INIZIO estrazione e attribuzione costi");
				boolean ret = estrazioneEAttribuzuioneCosti();
				getLogger().printTime("FINE estrazione e attribuzione costi");
				
				if(!ret) {
					return false;
				}
				updateAmbientiStatoUltConsun(AmbienteCommessa.STATO_ULTIMA_CONSUN__ORD_ESTRATTI);
				commit();
			}
			getLogger().printForcedMessage(">>>> FINE ESTRAZIONE COMMESSE");
		}
		catch(Exception ex) {
			ex.printStackTrace(Trace.excStream);
			return false;
		}
		finally {
	        getLogger().printMessage(">>>> FINALLY createReportInternal");
	        getLogger().fineTipoDocumento();
	    }
		return true;
	}

	protected static CachedStatement cDeleteTuttiStoricoCmm = new CachedStatement(
			    "DELETE FROM " + StoricoCommessaTM.TABLE_NAME +
			    " WHERE " + StoricoCommessaTM.ID_AZIENDA + " = ?" +
			    " AND " + StoricoCommessaTM.R_COMMESSA + " = ?" 
			    );
	protected static CachedStatement cDeleteSelStoricoCmm = new CachedStatement(
		    "DELETE FROM " + StoricoCommessaTM.TABLE_NAME +
		    " WHERE " + StoricoCommessaTM.ID_AZIENDA + " = ?" +
		    " AND " + StoricoCommessaTM.R_COMMESSA + " = ?" +
		    " AND ((" + StoricoCommessaTM.DOC_ORIGINE + " IN (?, ?)) OR (" + StoricoCommessaTM.DOC_ORIGINE + "= ? AND " + StoricoCommessaTM.DATA_ORG + "> ?))"
		    );
	protected synchronized void cancellaStroiciECosti() throws SQLException {
		for (Iterator it = iCompactCommesse.iterator(); it.hasNext();) {
			CompactCommessa compactCmm = (CompactCommessa) it.next();
			Database database = ConnectionManager.getCurrentDatabase();

			if(isEstrarrePeriodiDefinitivi()) {
				PreparedStatement stmt = cDeleteTuttiStoricoCmm.getStatement();
				database.setString(stmt, 1, getIdAzienda());
				database.setString(stmt, 2, compactCmm.getIdCommessa());
				cDeleteTuttiStoricoCmm.execute();
			}
			else {
				ConsuntivoCommessa ultimoConsuntivoCmmDef = compactCmm.getCommessa().getCommessaPrincipale().getUltimaConsuntivoCommessaDefinitivo();
				java.sql.Date dataUltChiusDef = (ultimoConsuntivoCmmDef != null)? ultimoConsuntivoCmmDef.getDataRiferimento():null;
				PreparedStatement stmt = cDeleteSelStoricoCmm.getStatement();
				database.setString(stmt, 1, getIdAzienda());
				database.setString(stmt, 2, compactCmm.getIdCommessa());
				database.setString(stmt, 3, String.valueOf(StoricoCommessa.RICHIESTA));
				database.setString(stmt, 4, String.valueOf(StoricoCommessa.ORDINE));
				database.setString(stmt, 5, String.valueOf(StoricoCommessa.DOCUMENTO));
				stmt.setDate(6, (dataUltChiusDef != null) ? dataUltChiusDef : database.getMinimumDate());
				cDeleteSelStoricoCmm.execute();
			}
		}
		updateAmbientiStatoUltConsun(AmbienteCommessa.STATO_ULTIMA_CONSUN__STORICO_COSTI_CANCELLATI);
		commit();
	}

	public boolean isDataValido(Date dataDocumento, CompactCommessa compactCommessa) {
		ConsuntivoCommessa ultimoConsuntivoCmmDef = compactCommessa.getCommessa().getCommessaPrincipale().getUltimaConsuntivoCommessaDefinitivo();
		return (ultimoConsuntivoCmmDef == null || ultimoConsuntivoCmmDef.getDataRiferimento() == null || dataDocumento.compareTo(ultimoConsuntivoCmmDef.getDataRiferimento()) > 0);
	}
}

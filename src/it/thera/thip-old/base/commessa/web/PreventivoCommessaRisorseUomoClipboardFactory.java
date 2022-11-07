package it.thera.thip.base.commessa.web;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.clipboard.ClipboardElement;
import com.thera.thermfw.clipboard.ClipboardFactory;
import com.thera.thermfw.clipboard.ClipboardFactoryResult;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;

import it.thera.thip.base.commessa.PreventivoCommessaRiga;
import it.thera.thip.base.commessa.PreventivoCommessaTestata;
import it.thera.thip.base.commessa.PreventivoCommessaVoce;
import it.thera.thip.base.risorse.Risorsa;

/*
 * Revision:
 * Number     Date        Owner  Description
 * 33594      21/05/2021  RA     Prima versione
 */

public class PreventivoCommessaRisorseUomoClipboardFactory extends ClipboardFactory {
	
	public ClipboardFactoryResult createElement(ClipboardElement clipboardElement) {
		ClipboardFactoryResult result = (ClipboardFactoryResult)Factory.createObject(ClipboardFactoryResult.class);
		try {
			BODataCollector origin = loadOrigin(clipboardElement);
			if(origin != null) {
				PreventivoCommessaVoce risorsaOrig = (PreventivoCommessaVoce)origin.getBo(); 
				if(risorsaOrig.getTipoRisorsa() == Risorsa.MACCHINE) {
					ErrorMessage em = new ErrorMessage("THIP_TN679");
					result.getErrors().add(em);
				}
				else {
					return super.createElement(clipboardElement);
				}
			}
			else {
				ErrorMessage em = new ErrorMessage("BAS0000004", KeyHelper.formatKeyString(clipboardElement.getKey()));
				result.getErrors().add(em);
			}
		}
		catch (Exception exception) {
			result.setReturnCode(BODataCollector.ERROR);
			result.setException(exception);
			exception.printStackTrace(Trace.excStream);
		}
		return result;
	}
	
	public void completeDestValues(BODataCollector dest, BODataCollector origin) {
		super.completeDestValues(dest, origin);
		completeDestValuesPreComune(dest, origin);
		completeDestValuesPostComune(dest, origin);
	}

	public void completeDestValuesPreComune(BODataCollector dest, BODataCollector origin) {
		PreventivoCommessaVoceDataCollector pcvDC = (PreventivoCommessaVoceDataCollector)dest;
		pcvDC.completaDocumento();		
		PreventivoCommessaVoce voceRisorsa = (PreventivoCommessaVoce)dest.getBo();
		voceRisorsa.setTipoRigav(PreventivoCommessaVoce.TP_RIG_RISORSA);
		voceRisorsa.initSequenza(false);
	}

	public void completeDestValuesPostComune(BODataCollector dest, BODataCollector origin) {
		dest.loadAttValue();
		PreventivoCommessaVoce voceRisorsa = (PreventivoCommessaVoce)dest.getBo();		
		PreventivoCommessaRiga riga = voceRisorsa.getPrevComRiga();
		if(riga != null && riga.getTestata() != null && riga.getTestata().getRepPrezzoArt() == PreventivoCommessaTestata.REP_PREZZO_COSTO_MARKUP ){
			voceRisorsa.setMarkup(riga.getMarkupUomo());
		}
	}
	
	public void preSaveDest(BODataCollector dest, BODataCollector origin) {
		PreventivoCommessaVoce voceRisorsa = (PreventivoCommessaVoce)dest.getBo();
		if(voceRisorsa.getDescrizione()!= null && (voceRisorsa.getDescrizione().getDescrizioneRidotta() == null || voceRisorsa.getDescrizione().getDescrizioneRidotta().equals("") || voceRisorsa.getDescrizione().getDescrizioneRidotta().length() > 15)){
			voceRisorsa.getDescrizione().setDescrizioneRidotta(voceRisorsa.getArticolo().getDescrizioneArticoloNLS().getDescrizioneRidotta());
		}
		voceRisorsa.getPrevComRiga().getTestata().setSalvoDaClipboard(true);
		dest.setBo(voceRisorsa);
	}

}

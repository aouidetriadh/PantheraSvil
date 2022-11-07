package it.thera.thip.base.commessa.web;

import java.util.Iterator;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.clipboard.ClipboardElement;
import com.thera.thermfw.clipboard.ClipboardFactory;
import com.thera.thermfw.clipboard.ClipboardFactoryResult;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;

import it.thera.thip.base.commessa.PreventivoCommessaRiga;
import it.thera.thip.base.commessa.PreventivoCommessaVoce;
/*
 * Revision:
 * Number     Date        Owner  Description
 * 29672      22/07/2019  RA     Prima versione
 * 32048	  16/10/2020  RA	 Corretta problema di copia preventivo
 * 32790	  01/02/2021  RA	 Aggiunto un controllo
 */
public class PreventivoCommessaRigaClipboardFactory extends ClipboardFactory {

	public void completeDestValues(BODataCollector dest, BODataCollector origin) {
		super.completeDestValues(dest, origin);
		completeDestValuesPreComune(dest, origin);
	}
		
	public ClipboardFactoryResult createElement(ClipboardElement clipboardElement) {
		ClipboardFactoryResult result = (ClipboardFactoryResult)Factory.createObject(ClipboardFactoryResult.class);
		try {
			BODataCollector origin = loadOrigin(clipboardElement);
			if(origin != null) {
				if(areAllWarnings(origin.getErrorList())) {
					BODataCollector dest = loadDest();
					if(dest != null) {
						setDestFatherValues(dest, origin);
						setDestAutomaticValues(dest, origin);
						completeDestValues(dest, origin);
						//32790 inizio
						PreventivoCommessaRiga rigaOrig = (PreventivoCommessaRiga)origin.getBo();
						PreventivoCommessaRiga rigaDest = (PreventivoCommessaRiga)dest.getBo();
						if(isCommessaPadre(rigaOrig, rigaDest)) {
							ErrorMessage em = new ErrorMessage("THIP_TN630");
							result.getErrors().add(em);
						}
						else {
						//32790 fine
							preSaveDest(dest, origin);
							result = saveDestPers(dest, origin);
						}//32790
					}
					else {
						ErrorMessage em = new ErrorMessage("BAS0000055");
						result.getErrors().add(em);
					}
				}
				else {
					result.getErrors().addAll(origin.getErrorList().getErrors());
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
	
	public void completeDestValuesPreComune(BODataCollector dest, BODataCollector origin) {
		PreventivoCommessaRiga rigaOrig = (PreventivoCommessaRiga)origin.getBo();
		PreventivoCommessaRiga rigaDest = (PreventivoCommessaRiga)dest.getBo();
		try {
			rigaDest.setEqual(rigaOrig);
			rigaDest.setOnDB(false);
			
			String parentKey = getServletEnvironment().getRequest().getParameter("ObjectKey");
			String[] parentKeyValues = KeyHelper.unpackObjectKey(parentKey);
			
			if(parentKeyValues.length == 3){
				rigaDest.setSplRiga(PreventivoCommessaRiga.TIPO_RIGA_COMMESSA);
				rigaDest.setIdAzienda(parentKeyValues[0]);
				rigaDest.setIdAnnoPrevc(parentKeyValues[1]);
				rigaDest.setIdNumeroPrevc(parentKeyValues[2]);
				rigaDest.setIdRigacPrvApp(0);
			}
			else if(parentKeyValues.length == 4){
				rigaDest.setSplRiga(PreventivoCommessaRiga.TIPO_RIGA_SOTTO_COMMESSA);
				rigaDest.setIdAzienda(parentKeyValues[0]);
				rigaDest.setIdAnnoPrevc(parentKeyValues[1]);
				rigaDest.setIdNumeroPrevc(parentKeyValues[2]);
				rigaDest.setIdRigacPrv(KeyHelper.stringToIntegerObj(parentKeyValues[3]).intValue());
				rigaDest.setIdRigacPrvApp(KeyHelper.stringToIntegerObj(parentKeyValues[3]).intValue());
			}
		} 
		catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		}
		rigaDest.getTestata().setSalvoDaClipboard(true);//32048
		dest.setBo(rigaDest);
	}
	
	
	public ClipboardFactoryResult saveDestPers(BODataCollector dest, BODataCollector origin) {
		ClipboardFactoryResult result = saveDest(dest);
		try {
			PreventivoCommessaRiga rigaOrig = (PreventivoCommessaRiga)origin.getBo();
			PreventivoCommessaRiga rigaDest = (PreventivoCommessaRiga)dest.getBo();

			Iterator iterVoce = rigaOrig.getRighe().iterator();
			while(iterVoce.hasNext()){
				BODataCollector voceDC = createDataCollector("PreventivoCommessaVoce"); 
				PreventivoCommessaVoce rigaVoceOrig = (PreventivoCommessaVoce)iterVoce.next();
				PreventivoCommessaVoce rigav = (PreventivoCommessaVoce)Factory.createObject(PreventivoCommessaVoce.class);
				rigav.setEqual(rigaVoceOrig);
				rigav.setOnDB(false);
				rigav.setIdAzienda(rigaDest.getIdAzienda());
				rigav.setIdAnnoPrevc(rigaDest.getIdAnnoPrevc());
				rigav.setIdNumeroPrevc(rigaDest.getIdNumeroPrevc());
				rigav.setIdRigacPrv(rigaDest.getIdRigacPrv());
				rigav.setPrevComRiga(rigaDest);
				rigav.setGeneraRigaDettaglio(false);
				rigav.getPrevComRiga().getTestata().setSalvoDaClipboard(true);//32048
				voceDC.setBo(rigav);
				int ret = voceDC.save();
				if(ret == BODataCollector.ERROR) {
					result.setErrors(voceDC.getErrorList().getErrors());
				}
			}
			
			Iterator iterSC = rigaOrig.getSottoCommesse().iterator();
			while(iterSC.hasNext()){
				PreventivoCommessaRiga scRiga = (PreventivoCommessaRiga)iterSC.next();
				caricaStruttura(rigaDest, scRiga, result);
			}

		}
		catch (Exception exception) {
			result.setReturnCode(BODataCollector.ERROR);
			result.setException(exception);
			exception.printStackTrace(Trace.excStream);
		}
		return result;
	}
	
	public void caricaStruttura(PreventivoCommessaRiga rigaDest, PreventivoCommessaRiga rigaOrig, ClipboardFactoryResult result){
		try{
			BODataCollector rigaDC = createDataCollector("PreventivoCommessaRiga"); 
			PreventivoCommessaRiga riga = (PreventivoCommessaRiga)Factory.createObject(PreventivoCommessaRiga.class);
			riga.setEqual(rigaOrig);
			riga.setOnDB(false);
			riga.setIdAzienda(rigaDest.getIdAzienda());
			riga.setIdAnnoPrevc(rigaDest.getIdAnnoPrevc());
			riga.setIdNumeroPrevc(rigaDest.getIdNumeroPrevc());
			riga.setIdRigacPrv(0);
			riga.setIdRigacPrvApp(rigaDest.getIdRigacPrv());
			riga.getTestata().setSalvoDaClipboard(true);//32048
			rigaDC.setBo(riga);
			int ret = rigaDC.save();
			if(ret == BODataCollector.ERROR) {
				result.setErrors(rigaDC.getErrorList().getErrors());
			}
			Iterator iterSC = rigaOrig.getSottoCommesse().iterator();
			while(iterSC.hasNext()){
				PreventivoCommessaRiga scRiga = (PreventivoCommessaRiga)iterSC.next();
				caricaStruttura(riga, scRiga, result);
			}
			
			Iterator iterVoce = rigaOrig.getRighe().iterator();
			while(iterVoce.hasNext()){
				BODataCollector voceDC = createDataCollector("PreventivoCommessaVoce"); 
				PreventivoCommessaVoce rigaVoceOrig = (PreventivoCommessaVoce)iterVoce.next();
				PreventivoCommessaVoce rigav = (PreventivoCommessaVoce)Factory.createObject(PreventivoCommessaVoce.class);
				rigav.setEqual(rigaVoceOrig);
				rigav.setOnDB(false);
				rigav.setIdAzienda(rigaDest.getIdAzienda());
				rigav.setIdAnnoPrevc(rigaDest.getIdAnnoPrevc());
				rigav.setIdNumeroPrevc(rigaDest.getIdNumeroPrevc());
				rigav.setIdRigacPrv(riga.getIdRigacPrv());
				rigav.setPrevComRiga(riga);
				rigav.setGeneraRigaDettaglio(false);
				rigav.getPrevComRiga().getTestata().setSalvoDaClipboard(true);//32048
				voceDC.setBo(rigav);
				int retv = voceDC.save();
				if(retv == BODataCollector.ERROR) {
					result.setErrors(voceDC.getErrorList().getErrors());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		}
	}
	
	//32790 inizio
	public boolean isCommessaPadre(PreventivoCommessaRiga rigaOrig, PreventivoCommessaRiga rigaDest) {
		PreventivoCommessaRiga rigaApp = rigaDest.getRigaAppartenenza();
		if(rigaApp != null){
			if(rigaApp.equals(rigaOrig)) {
				return true;
			}
			else {
				return isCommessaPadre(rigaOrig, rigaApp);
			}
		}	
		return false;
	}	
	//32790 fine
}

package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.ErrorCodes;

import it.thera.thip.base.risorse.RisorsaCosto;
/**
 * VariaBudgetCommessaDet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/11/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34795   29/11/2021    RA		    Prima struttura
 */
public class VariaBudgetCommessaDet  extends VariaBudgetCommessaDetPO  implements CostiCommessaDettaglio{

	public ErrorMessage checkDelete() {
		return null;
	}
	
	public BigDecimal sum(BigDecimal val1, BigDecimal val2) {
		if((val1 == null) && (val2 == null))
			return null;
		if(val1 == null)
			return val2;
		if(val2 == null)
			return val1;
		return val1.add(val2);
	}
	
   public boolean aggiornaValore(String newValueStr, boolean updatedOre) {
       BigDecimal newValue = new BigDecimal("0");
       if(newValueStr != null && !newValueStr.equals("") && !newValueStr.equals("null")) 
          newValue = new BigDecimal(newValueStr);
                
       if(!getComponenteCosto().isGestioneATempo()) {
          setCostoLivello(newValue);
       }
       else {
          BigDecimal costoUnitario = getCostoUnitario();
          BigDecimal costoLivello = new BigDecimal("0");
          BigDecimal tempoLivello = new BigDecimal("0");
          if(updatedOre) {
             costoLivello = costoUnitario.multiply(newValue);
             tempoLivello = newValue;
          }
          else {
             costoLivello = newValue;
             tempoLivello = newValue.divide(costoUnitario, 3, BigDecimal.ROUND_HALF_UP);
          }
          setCostoLivello(costoLivello);
          setTempoLivello(tempoLivello);
       }
       return true;
    }
	
	public boolean aggiorna(String oldValueStr, String newValueStr, boolean updatedOre) {
		BigDecimal newValue = new BigDecimal("0");
		BigDecimal oldValue = new BigDecimal("0");
		if(newValueStr != null && !newValueStr.equals("")) {
			newValue = new BigDecimal(newValueStr);
		}
		if(oldValueStr != null && !oldValueStr.equals("")) {
			oldValue = new BigDecimal(oldValueStr);
		}		
		if(!getComponenteCosto().isGestioneATempo()) {
			setCostoLivello(newValue);
			BigDecimal totale = getCostoTotale().subtract(oldValue).add(newValue);
			setCostoTotale(totale);
		}
		else {
			BigDecimal costoUnitario = getCostoUnitario();
			BigDecimal costoLivello = new BigDecimal("0");
			BigDecimal tempoLivello = new BigDecimal("0");
			BigDecimal costoTotale = new BigDecimal("0");
			BigDecimal tempoTotale = new BigDecimal("0");
			if(updatedOre){
				costoLivello = costoUnitario.multiply(newValue);
				tempoLivello = newValue;
				tempoTotale = getTempoTotale().subtract(oldValue).add(newValue);
				costoTotale = getCostoTotale().subtract(getCostoLivello()).add(costoLivello);
			}
			else {
				newValue = newValue.setScale(6);
				costoLivello = newValue;
				tempoLivello = newValue.divide(costoUnitario, 6, BigDecimal.ROUND_HALF_UP);
				tempoTotale = getTempoTotale().subtract(getTempoLivello()).add(tempoLivello);
				costoTotale = getCostoTotale().subtract(oldValue).add(newValue);
			}
			setCostoLivello(costoLivello);
			setTempoLivello(tempoLivello);
			setCostoTotale(costoTotale);
			setTempoTotale(tempoTotale);
		}
		try {
			int ret = save();
			if(ret >= ErrorCodes.NO_ROWS_UPDATED) {			
				ConnectionManager.commit();
				return true;
			}
			else {
				ConnectionManager.rollback();
				return false;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return true;
	}
	
	public BigDecimal getCostoUnitario() {
		if(getComponenteCosto() != null && getComponenteCosto().isGestioneATempo()) {
			if(getComponenteCosto().getCostoUnitario() != null)
				return getComponenteCosto().getCostoUnitario();
			else if(getComponenteCosto().getTipoCosto() != null && getComponenteCosto().getRisorsa() != null) {
				RisorsaCosto risorsaCosto = RisorsaCosto.getRisorsaCosto(getComponenteCosto().getRisorsa(), getComponenteCosto().getIdTipoCosto(), getVariaBudgetCommessa().getDataRiferimento());
				if(risorsaCosto != null)
					return risorsaCosto.getCosto();
			}
		}
		 
		return null;
	}
	
   protected void sommaLivelloInferiore(VariaBudgetCommessaDet dettaglio) {
      setCostoLivelloInf(dettaglio.getCostoTotale());
      setTempoLivelloInf(dettaglio.getTempoTotale());
   }
   
   protected void calcolaTotali() {
      setCostoTotale(sum(getCostoLivello(), getCostoLivelloInf()));
      setTempoTotale(sum(getTempoLivello(), getTempoLivelloInf()));        
   }

   @Override
   public void aggiornaGruppi(CalcolatoreDettagliCommesse calcolatore, List listaDettagli, List compDaValorizz) throws SQLException {
      calcolatore.aggiorna(this, listaDettagli, compDaValorizz, getGruppo());
   }

   @Override
   public CostiCommessaDetGruppo getGruppo(char tipoGruppo) {
      return getGruppo();
   }   
}

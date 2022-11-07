package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;


import com.thera.thermfw.common.ErrorMessage;
import it.thera.thip.base.risorse.RisorsaCosto;

/**
 * BudgetCommessaDet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/10/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34585   29/10/2021    RA		    Prima struttura
 * 35382   29/03/2022    RA			corretta problema di NullPointerException
 */
public class BudgetCommessaDet  extends BudgetCommessaDetPO implements CostiCommessaDettaglio{
   public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";

   public ErrorMessage checkDelete() {
      return null;
   }

   public boolean aggiornaValore(String newValueStr, boolean updatedOre) 
   {
      BigDecimal newValue = new BigDecimal("0");
      if(newValueStr != null && !newValueStr.equals("") && !newValueStr.equals("null")) 
         newValue = new BigDecimal(newValueStr);

      if(!getComponenteCosto().isGestioneATempo()) 
      {
         setCostoLivello(newValue);
      }
      else 
      {
         BigDecimal costoUnitario = getCostoUnitario();
         BigDecimal costoLivello = new BigDecimal("0");
         BigDecimal tempoLivello = new BigDecimal("0");
         if(updatedOre)
         {
            costoLivello = costoUnitario.multiply(newValue);
            tempoLivello = newValue;
         }
         else 
         {
            costoLivello = newValue;
            tempoLivello = newValue.divide(costoUnitario, 3, BigDecimal.ROUND_HALF_UP);

         }
         setCostoLivello(costoLivello);
         setTempoLivello(tempoLivello);
      }
      return true;
   }

   public BigDecimal getCostoUnitario() {
      if(getComponenteCosto() != null && getComponenteCosto().isGestioneATempo()) {
         if(getComponenteCosto().getCostoUnitario() != null)
            return getComponenteCosto().getCostoUnitario();
         else if(getComponenteCosto().getTipoCosto() != null && getComponenteCosto().getRisorsa() != null) {
            RisorsaCosto risorsaCosto = RisorsaCosto.getRisorsaCosto(getComponenteCosto().getRisorsa(), getComponenteCosto().getIdTipoCosto(), getBudgetCommessa().getDataRiferimento());
            if(risorsaCosto != null)
               return risorsaCosto.getCosto();
         }
      }

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


   protected void sommaLivelloInferiore(BudgetCommessaDet dettaglio)
   {
      setCostoLivelloInf(dettaglio.getCostoTotale());
      setTempoLivelloInf(dettaglio.getTempoTotale());
   }

   protected void calcolaTotali()
   {
      setCostoTotale(sum(getCostoLivello(), getCostoLivelloInf()));
      setTempoTotale(sum(getTempoLivello(), getTempoLivelloInf()));        
   }

   public void azzera()
   {
      getGruppo().azzera();
   }


   @Override
   public void aggiornaGruppi(CalcolatoreDettagliCommesse calcolatore, List listaDettagli, List compDaValorizz) throws SQLException
   {
      calcolatore.aggiorna(this, listaDettagli, compDaValorizz, getGruppo());
   }

   @Override
   public CostiCommessaDetGruppo getGruppo(char tipoGruppo)
   {
      return getGruppo();
   }



}

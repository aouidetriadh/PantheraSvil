package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;

import com.thera.thermfw.persist.TransientObject;
/**
 * CostiCommessaDetGruppo
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 17/08/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   17/08/2021    RA       Prima struttura
 */
public class CostiCommessaDetGruppo extends TransientObject {

   public static BigDecimal ZERO = new BigDecimal("0");

   private BigDecimal iCostoLivello = ZERO;
   private BigDecimal iCostoLivelloInf = ZERO;
   private BigDecimal iCostoTotale = ZERO;
   private BigDecimal iTempoLivello = ZERO;
   private BigDecimal iTempoLivelloInf = ZERO;
   private BigDecimal iTempoTotale = ZERO;

   protected char tipoGruppo = '-';

   public CostiCommessaDetGruppo() {
   }

   public BigDecimal getCostoLivello() {
      return iCostoLivello;
   }

   public void setCostoLivello(BigDecimal costoLivello) {
      this.iCostoLivello = costoLivello;
      setDirty();
   }

   public BigDecimal getCostoLivelloInf() {
      return iCostoLivelloInf;
   }

   public void setCostoLivelloInf(BigDecimal costoLivelloInf) {
      this.iCostoLivelloInf = costoLivelloInf;
      setDirty();
   }

   public BigDecimal getCostoTotale() {
      return iCostoTotale;
   }

   public void setCostoTotale(BigDecimal costoTotale) {
      this.iCostoTotale = costoTotale;
      setDirty();
   }

   public BigDecimal getTempoLivello() {
      return iTempoLivello;
   }

   public void setTempoLivello(BigDecimal tempoLivello) {
      this.iTempoLivello = tempoLivello;
      setDirty();
   }

   public BigDecimal getTempoLivelloInf() {
      return iTempoLivelloInf;
   }

   public void setTempoLivelloInf(BigDecimal tempoLivelloInf) {
      this.iTempoLivelloInf = tempoLivelloInf;
      setDirty();
   }

   public BigDecimal getTempoTotale() {
      return iTempoTotale;
   }

   public void setTempoTotale(BigDecimal tempoTotale) {
      this.iTempoTotale = tempoTotale;
      setDirty();
   }

   public void azzera() {
      setCostoLivello(ZERO);
      setCostoLivelloInf(ZERO);
      setCostoTotale(ZERO);
      setTempoLivello(ZERO);
      setTempoLivelloInf(ZERO);
      setTempoTotale(ZERO);
   }

   public void aggrega(CostiCommessaDetGruppo gruppo) {
      setCostoLivelloInf(sum(getCostoLivelloInf(), gruppo.getCostoTotale()));
      setCostoTotale(sum(getCostoLivello(), getCostoLivelloInf()));
      setTempoLivelloInf(sum(getTempoLivelloInf(), gruppo.getTempoTotale()));
      setTempoTotale(sum(getTempoLivello(), getTempoLivelloInf()));			
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

   protected void sommaLivelloInferiore(CostiCommessaDetGruppo gruppo)
   {
      setCostoLivelloInf(sum(getCostoLivelloInf(), gruppo.getCostoTotale()));
      setTempoLivelloInf(sum(getTempoLivelloInf(), gruppo.getTempoTotale()));
   }

   protected void calcolaTotali()
   {
      setCostoTotale(sum(getCostoLivello(), getCostoLivelloInf()));
      setTempoTotale(sum(getTempoLivello(), getTempoLivelloInf()));        
   }

}

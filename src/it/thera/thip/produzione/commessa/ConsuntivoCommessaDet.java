package it.thera.thip.produzione.commessa;

import java.sql.SQLException;
import java.util.List;
import com.thera.thermfw.common.ErrorMessage;

/**
 * ConsuntivoCommessaDet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 18/08/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 33950   18/08/2021    RA		    Prima struttura
 */
public class ConsuntivoCommessaDet  extends ConsuntivoCommessaDetPO  implements CostiCommessaDettaglio{

   public final static char TIPO_CONSOLIDATO = 'C';
   public final static char TIPO_RICHIESTO = 'R';
   public final static char TIPO_ORDINATO = 'O';
   public final static char TIPO_EFFETUATO = 'E';
   public final static char TIPO_TOTALE = 'T';

   public ErrorMessage checkDelete() {
      return null;
   }

   public void azzera() {
      getConsolidato().azzera();
      getRichiesto().azzera();		
      getOrdinato().azzera();
      getEffettuato().azzera();
      getTotale().azzera();
   }

   public void aggrega(ConsuntivoCommessaDet dettaglio) {
      getConsolidato().aggrega(dettaglio.getConsolidato());
      getRichiesto().aggrega(dettaglio.getRichiesto());		
      getOrdinato().aggrega(dettaglio.getOrdinato());
      getEffettuato().aggrega(dettaglio.getEffettuato());
      getTotale().aggrega(dettaglio.getTotale());
      if(getIdComponCosto().equals("C10"))
      {
         String msg = "\nXXX: " + getIdCommessa();
         msg += "\nCOMP: " + getIdComponCosto();
         msg += "\n EFF LV: " + getEffettuato().getCostoLivello() + " LI: " +  getEffettuato().getCostoLivelloInf() +" TT: "+  getEffettuato().getCostoTotale();
         msg += "\n TOT LV: " + getTotale().getCostoLivello() + " LI: " +  getTotale().getCostoLivelloInf() +" TT: "+  getTotale().getCostoTotale();

         System.err.println(msg);
      }
   }

   public CostiCommessaDetGruppo getGruppo(char tipoGruppo) {
      switch (tipoGruppo) {
         case TIPO_CONSOLIDATO:
            return getConsolidato();
         case TIPO_RICHIESTO:
            return getRichiesto();
         case TIPO_ORDINATO:
            return getOrdinato();
         case TIPO_EFFETUATO:
            return getEffettuato();
         case TIPO_TOTALE:
            return getTotale();
      }
      return null;
   }

   @Override
   public void aggiornaGruppi(CalcolatoreDettagliCommesse calcolatore, List listaDettagli, List compDaValorizz) throws SQLException
   {
      calcolatore.aggiorna(this, listaDettagli, compDaValorizz, getConsolidato());
      calcolatore.aggiorna(this, listaDettagli, compDaValorizz, getRichiesto());
      calcolatore.aggiorna(this, listaDettagli, compDaValorizz, getOrdinato());
      calcolatore.aggiorna(this, listaDettagli, compDaValorizz, getEffettuato());
      calcolatore.aggiorna(this, listaDettagli, compDaValorizz, getTotale());
   }
}

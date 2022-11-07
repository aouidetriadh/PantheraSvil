package it.thera.thip.produzione.commessa;

import java.sql.SQLException;
import java.util.List;

import it.thera.thip.datiTecnici.costi.ComponenteCosto;

public interface CostiCommessaDettaglio
{
   public ComponenteCosto getComponenteCosto();
   public String getIdComponCosto();   
   public void aggiornaGruppi(CalcolatoreDettagliCommesse calcolatore, List listaDettagli, List compDaValorizz) throws SQLException;
   public CostiCommessaDetGruppo getGruppo(char tipoGruppo);
}

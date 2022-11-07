package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.common.Numerator;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.ErrorCodes;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.PersistentObjectCursor;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.commessa.CommessaTM;
import it.thera.thip.base.commessa.PreventivoCommessaRiga;
import it.thera.thip.base.commessa.PreventivoCommessaTestata;
import it.thera.thip.base.commessa.PreventivoCommessaVoce;
import it.thera.thip.base.commessa.TipoCommessa;
import it.thera.thip.base.generale.CategoriaUM;
import it.thera.thip.base.generale.PersDatiGen;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.cs.DatiComuniEstesi;
import it.thera.thip.datiTecnici.PersDatiTecnici;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
import it.thera.thip.datiTecnici.costi.LinkCompSchema;

/**
 * BudgetCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/10/2021 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34585   29/10/2021    RA		    Prima struttura
 * 35382   11/04/2022    RA			Aggiunto metodi rendiDefinitivo e rendiProvvisorio
 */

public class BudgetCommessa extends BudgetCommessaPO {
	
	public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.BudgetCommessa";
	public static final String ID_PROGR_NUM_ID = "BUDGET_CMM";
	
	public final static BigDecimal ZERO = new BigDecimal(0);
	
	//Stato avanzamento budget
	public final static char PROVVISORIO = '1';
	public final static char DEFINITIVO = '2';

	protected boolean iTotali = false;
	protected boolean iDettagliCommessa = true;
	protected boolean iDettagliSottoCommesse = true;
	protected boolean iComponentiPropri = true;
	protected boolean iSoloComponentiValorizzate = false;
	protected boolean forzaRicalcolo = false;

	protected List<BudgetCommessa> budgetLivelliInferiori = null;   
	protected CalcolatoreDettagliCommesse calcolatoreDettagli = null;
	   	
	public boolean isTotali() {
	   return iTotali;
	}

	public void setTotali(boolean totali) {
	   iTotali = totali;
	}

	public boolean isDettagliCommessa() {
	   return iDettagliCommessa;
	}

	public void setDettagliCommessa(boolean dettagliCommessa) {
	   iDettagliCommessa = dettagliCommessa;
	}

	public boolean isDettagliSottoCommesse() {
	   return iDettagliSottoCommesse;
	}

	public void setDettagliSottoCommesse(boolean dettagliSottoCommesse) {
	   iDettagliSottoCommesse = dettagliSottoCommesse;
	}

	public boolean isComponentiPropri() {
	   return iComponentiPropri;
	}

	public void setComponentiPropri(boolean componentiPropri) {
	   iComponentiPropri = componentiPropri;
	}

	public boolean isSoloComponentiValorizzate() {
	   return iSoloComponentiValorizzate;
	}

	public void setSoloComponentiValorizzate(boolean soloComponentiValorizzate) {
	   iSoloComponentiValorizzate = soloComponentiValorizzate;
	}

	public boolean isForzaRicalcolo()
	{
	   return forzaRicalcolo;
	}

	public void setForzaRicalcolo(boolean forzaRicalcolo)
	{
	   this.forzaRicalcolo = forzaRicalcolo;
	}

   public List<BudgetCommessa> getBudgetLivelliInferiori()
   {
      return budgetLivelliInferiori;
   }

   
   public CalcolatoreDettagliCommesse getCalcolatoreDettagli()
   {
      if(calcolatoreDettagli == null)
         calcolatoreDettagli = (CalcolatoreDettagliCommesse)Factory.createObject(CalcolatoreDettagliCommesse.class);
      return calcolatoreDettagli;
   }

   public void setCalcolatoreDettagli(CalcolatoreDettagliCommesse calcolatoreDettagli)
   {
      this.calcolatoreDettagli = calcolatoreDettagli;
   }
   
	public boolean initializeOwnedObjects(boolean result) {
		ScreenData sd = ScreenData.getDefaultScreenData("BudgetCommessa");
		if (sd != null && sd.getAttValue("Totali") != null && sd.getAttValue("Totali").equals("Y"))
			iTotali = true;
		if (sd != null && sd.getAttValue("DettagliCommessa") != null && sd.getAttValue("DettagliCommessa").equals("N"))
			iDettagliCommessa = false;
		if (sd != null && sd.getAttValue("DettagliSottoCommesse") != null && sd.getAttValue("DettagliSottoCommesse").equals("N"))
			iDettagliSottoCommesse = false;
		if (sd != null && sd.getAttValue("ComponentiPropri") != null && sd.getAttValue("ComponentiPropri").equals("N"))
			iComponentiPropri = false;
		if (sd != null && sd.getAttValue("SoloComponentiValorizzate") != null && sd.getAttValue("SoloComponentiValorizzate").equals("Y"))
			iSoloComponentiValorizzate = true;
		result = super.initializeOwnedObjects(result);
	    return result;
	}
	
	public ErrorMessage checkDelete() {
		if(isCollegatoAVariazioneBudget())
			return new ErrorMessage("THIP_TN814");//Il budget non è eliminabile perché referenziato in una variazione di budget
		return null;
	}
	
	public boolean isCollegatoAVariazioneBudget() {
		String where = VariaBudgetCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' AND " + 
					   VariaBudgetCommessaTM.ID_COMMESSA + " = '" + getIdCommessa() + "' AND " + 
			   	       VariaBudgetCommessaTM.ID_BUDGET + " = " + getIdBudget();
		PersistentObjectCursor cursor = new PersistentObjectCursor(VariaBudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
		try {
			if(cursor.hasNext()) {
				return true;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return false;
	}

	public Integer getNextIdBudget() throws Exception {
		return  new Integer(Numerator.getNextInt(ID_PROGR_NUM_ID));
	}
	
	public int delete() throws SQLException {
		int rc = 0;
		if(getCommessa() != null && getCommessa().getCommessaAppartenenza() == null) {
			String where = BudgetCommessaTM.ID_AZIENDA + " = '" + getIdAzienda() + "' AND " + 
						   BudgetCommessaTM.ID_COMMESSA + " <> '" + getIdCommessa() + "' AND " + 
						   BudgetCommessaTM.ID_BUDGET + " = " + getIdBudget();
			PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
			  try {
				  while (cursor.hasNext()) {
					  BudgetCommessa budget = (BudgetCommessa)cursor.next();
					  int rc1 = budget.delete();
					  if(rc1 < 0) {
						  rc = rc1;
						  break;
					  }
					  else
						  rc += rc1;
				  }
			  } 
			  catch (SQLException e) {
				  e.printStackTrace(Trace.excStream);
			  }
		}
		if(rc >= 0)
			rc += super.delete();

		return rc;
	}
		
	
	public void caricaBudgetDaPreventivo()
	{
	   svuotaDettagli();
	   setForzaRicalcolo(true);
	   try
      {
          int rc = save();
          if(rc >= 0)
             ConnectionManager.commit();
          else
             ConnectionManager.rollback();
      }
      catch(SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace(Trace.excStream);
      }
	   
	}
	
	protected void svuotaDettagli()
	{
	   getBudgetCommessaDet().clear();
	   if(budgetLivelliInferiori != null)
	   {
	      for(BudgetCommessa budLivelloInferiore : budgetLivelliInferiori)
	         budLivelloInferiore.svuotaDettagli();
	   }
	}

	public int save() throws SQLException {
	   beforeSave();
      calcolaAlberoBudget();
      salvaAlberoBudget();
      return 1;
	}
	
	public void beforeSave() {
	   
		if(!isOnDB() && getIdBudget().compareTo(new Integer(0)) == 0) {
			try {
				setIdBudget(getNextIdBudget());
			}
			catch(Exception ex) {
				ex.printStackTrace(Trace.excStream); 
			}
		}
	}
	
   public void caricaAlberoBudget() {
      caricaLivelliInferiori();
      for(BudgetCommessa budLivelloInferiore : budgetLivelliInferiori)
         budLivelloInferiore.caricaAlberoBudget();
   }
   	
   public void calcolaAlberoBudget() {
      calcolaLivello();
      caricaLivelliInferiori();
      calcolaLivelliInferiori();
      calcolaTotali();
   }   
   
   public void calcolaLivello() {
      if(isOnDB() == false || isForzaRicalcolo()) {
         if(isForzaRicalcolo()) {
            Iterator iter = getBudgetCommessaDet().iterator();
            while(iter.hasNext()) {
               BudgetCommessaDet curDet = (BudgetCommessaDet)iter.next();
               curDet.setCostoLivello(ZERO);
               curDet.setCostoLivelloInf(ZERO);
               curDet.setCostoTotale(ZERO);
            }
         }
         creaLivelloDaArticolo();
         if(getPreventivo() != null)
            caricaLivelloDaPreventivo();
      }
      else {
         Iterator iter = getBudgetCommessaDet().iterator();
         while(iter.hasNext()) {
            BudgetCommessaDet curDet = (BudgetCommessaDet)iter.next();
            curDet.setCostoTotale(ZERO);
         }
      }
      applicaFormuleTotali();
   }

   public void creaLivelloDaArticolo() {
      Articolo articolo = this.getCommessaPrm().getArticolo();
      List<LinkCompSchema> listComponenti = new ArrayList<LinkCompSchema>();
      if(articolo.getClasseMerclg() != null && articolo.getClasseMerclg().getSchemaCosto() != null)
         listComponenti.addAll(articolo.getClasseMerclg().getSchemaCosto().getComponenti());

      for(int idx = 0; idx < listComponenti.size(); idx++) {
         LinkCompSchema linkCompSchema = listComponenti.get(idx);
         BudgetCommessaDet dettaglioBudget = getDettaglio(this, linkCompSchema.getIdComponenteCosto());
         dettaglioBudget.setCostoTotale(ZERO);
      }
   }
   
   public void caricaLivelloDaPreventivo() {
      Commessa commessaLivello = getCommessa();
      PreventivoCommessaTestata preventivo = getPreventivo();
      if(preventivo != null) {
         PreventivoCommessaRiga rigaCommessa = null;
         Iterator iterRighe = preventivo.getRighe().iterator();
         while(iterRighe.hasNext() && rigaCommessa == null) {
            PreventivoCommessaRiga curRiga = (PreventivoCommessaRiga)iterRighe.next();
            if(curRiga.getIdCommessa() != null && curRiga.getIdCommessa().equals(commessaLivello.getIdCommessa()))
               rigaCommessa = curRiga;
         }
         
         UnitaMisura umOre = null;
         PersDatiGen pdg = PersDatiGen.getCurrentPersDatiGen();
         CategoriaUM catUmTempi = pdg.getCategoriaUMTempi();
         if(catUmTempi != null)
             umOre = catUmTempi.getUMPrm();        
         
         if(rigaCommessa != null) {
            Iterator iterVoci = rigaCommessa.getRighe().iterator();
            while(iterVoci.hasNext()) {
               PreventivoCommessaVoce voce = (PreventivoCommessaVoce)iterVoci.next();
               ComponenteCosto compVoce = voce.getComponenteCosto();
               if(compVoce != null) {
                  BudgetCommessaDet dettaglioBudget = getDettaglio(this, compVoce.getIdComponenteCosto(), false);
                  if(dettaglioBudget != null) {
                     dettaglioBudget.setCostoLivello(dettaglioBudget.getCostoLivello().add(voce.getCosTotale()));
                     if(compVoce.isGestioneATempo() && voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA && voce.getQtaPrvUmPrm() != null) {
                        BigDecimal valore = voce.getQtaPrvUmPrm();
                        UnitaMisura umRiga = voce.getUmPrmMag();                        
                        BigDecimal valoreOre = catUmTempi.convertiUM(valore, umRiga, umOre);
                        if(valoreOre != null)
                           dettaglioBudget.setTempoLivello(dettaglioBudget.getTempoLivello().add(valoreOre));
                     }
                  }
               }
             }
          }
      }
   }
   
   public void applicaFormuleTotali() {
      getCalcolatoreDettagli().applicaFormuleTotali(getBudgetCommessaDet(), getArticolo());
   }
      
   public void caricaLivelliInferiori() {
      if(budgetLivelliInferiori == null) {
         budgetLivelliInferiori = new ArrayList<BudgetCommessa>();
         String where = CommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + CommessaTM.R_COMMESSA_APP + "='" + getIdCommessa() + "'";
         List commesse;
         try {
            commesse = Commessa.retrieveList(Commessa.class, where, "", false);
            Iterator iter = commesse.iterator();
            while(iter.hasNext()) {
               Commessa commessa = (Commessa)iter.next();
               BudgetCommessa budget = caricaBudgetCommessa(commessa);
               if(budget != null)
                  budgetLivelliInferiori.add(budget);
            }
         }
         catch(Exception e) {
            e.printStackTrace(Trace.excStream);
         }
      }
   }
   
   public void calcolaLivelliInferiori() {
      for(BudgetCommessa budLivelloInferiore : budgetLivelliInferiori)
      {
         budLivelloInferiore.calcolaAlberoBudget();
         
         Iterator iter = getBudgetCommessaDet().iterator();
         while(iter.hasNext())
         {
            BudgetCommessaDet dettaglio = (BudgetCommessaDet)iter.next();
            BudgetCommessaDet detLivelloInferiore = getDettaglio(budLivelloInferiore, dettaglio.getIdComponCosto());
            if(detLivelloInferiore != null)
               dettaglio.sommaLivelloInferiore(detLivelloInferiore);
         }
      }
   }

   public void calcolaTotali() {
      Iterator iter = getBudgetCommessaDet().iterator();
      while(iter.hasNext())
      {
         BudgetCommessaDet dettaglio = (BudgetCommessaDet)iter.next();
         dettaglio.calcolaTotali();
      }
      valorizzaCostiBudget();
   }
   
   public BudgetCommessaDet getDettaglio(BudgetCommessa budget, String idCmpCosto) {
      return getDettaglio(budget, idCmpCosto, true);
   }

   public BudgetCommessaDet getDettaglio(BudgetCommessa budget, String idCmpCosto, boolean creaDettaglio) {
      BudgetCommessaDet dettaglio = null;
      Iterator iter = budget.getBudgetCommessaDet().iterator();
      while(iter.hasNext() && dettaglio == null)
      {
         BudgetCommessaDet curDet = (BudgetCommessaDet)iter.next();
         if(curDet.getIdComponCosto().equals(idCmpCosto))
            dettaglio = curDet;
      }
      if(dettaglio == null && creaDettaglio)
      {
         dettaglio = creaBudgetCommessaDet(budget, budget.getCommessa(), idCmpCosto);
         budget.getBudgetCommessaDet().add(dettaglio);
      }
      return dettaglio;
   }
   
   public BudgetCommessaDet creaBudgetCommessaDet(BudgetCommessa budget, Commessa commessa, String idComponCosto) {
      BudgetCommessaDet budgetCommessaDet = (BudgetCommessaDet) Factory.createObject(BudgetCommessaDet.class);
      budgetCommessaDet.setIdAzienda(budget.getIdAzienda());
      budgetCommessaDet.setIdBudget(budget.getIdBudget());
      budgetCommessaDet.setIdCommessa(commessa.getIdCommessa());
      budgetCommessaDet.setIdComponCosto(idComponCosto);
      return budgetCommessaDet;
   }
   
   public BudgetCommessa getBudgetLivelloInferiore(Commessa commessa) {
      BudgetCommessa bud = null;
      if(budgetLivelliInferiori != null)
      {
         for(BudgetCommessa curBud : budgetLivelliInferiori)
         {
            if(curBud.getIdCommessa().equals(commessa.getIdCommessa()))
               bud = curBud;
         }
      }
      
      if(bud == null)
         bud = creaNuovoBudgetCommessa(commessa);
      return bud;
   }
	
   public void salvaAlberoBudget() {
      for(BudgetCommessa budLivelloInferiore : budgetLivelliInferiori)
         budLivelloInferiore.salvaAlberoBudget();
      try
      {
         super.save();
      }
      catch(SQLException e)
      {
         e.printStackTrace(Trace.excStream);
      }
   }
	
   public Vector checkAll(BaseComponentsCollection components) {
	   Vector errors = new Vector();
	   components.runAllChecks(errors);
	   return errors;
   }
	
   public String getIdDescUMPrmMag() {
		if((getCommessa() != null) && (getCommessa().getUmPrmMag() != null)) {
			return getCommessa().getUmPrmMag().getIdDescrizione();
		}
		return null;
	}
	
	public ErrorMessage checkIdCommessa() {
		if(getCommessa() != null && getCommessa().getCommessaAppartenenza() != null) {
			return new ErrorMessage("THIP_TN723");
		}
		
		if(getCommessa() != null && !isOnDB() && getBudgetsCommessa(BudgetCommessa.PROVVISORIO).size() >= 1 ) {
			return new ErrorMessage("THIP_TN756");//“La commessa selezionata ha già un budget iniziale”
		}
		
		if(getCommessa() != null && (getCommessa().getArticolo() == null || getCommessa().getArticolo().getStato() != DatiComuniEstesi.VALIDO) ) {
			return new ErrorMessage("THIP_TN755");//“La commessa deve riferire un articolo valido”
		}
		
		if(getCommessa() != null && getCommessa().getArticolo() != null && (getCommessa().getArticolo().getClasseMerclg() == null || getCommessa().getArticolo().getClasseMerclg().getSchemaCosto() == null)) {
			return new ErrorMessage("THIP_TN754");//“Articolo senza schema di costo”
		}
		
		if(getCommessa() != null && getCommessa().getArticolo() == null) {
			return new ErrorMessage("THIP_TN796");//Commessa con articolo non valorizzato.
		}
		
		if(getCommessa() != null && getCommessa().getArticoloVersione() == null) {
			return new ErrorMessage("THIP_TN797");//Commessa con versione articolo non valorizzata.
		}
		
		if(getCommessa() != null && getCommessa().getArticolo().isConfigurato() && getCommessa().getConfigurazione() == null){
			return new ErrorMessage("THIP_TN798");//Commessa con articolo configurato e configurazione vuota
		}
		  
		if(getCommessa() != null && getCommessa().getStabilimento() == null){
			return new ErrorMessage("THIP_TN799");//Commessa con stabilimento non valorizzato
		}
		
		if(getCommessa() != null && getCommessa().getDatiComuniEstesi() != null &&  getCommessa().getDatiComuniEstesi().getStato() == DatiComuniEstesi.ANNULLATO){
			return new ErrorMessage("THIP_TN800");//Commessa con stato annullato
		}
		
		if(getCommessa() != null && getCommessa().getTipoCommessa() != null && (getCommessa().getTipoCommessa().getNaturaCommessa() != TipoCommessa.NATURA_CMM__GEST_INTERNA && getCommessa().getTipoCommessa().getNaturaCommessa() != TipoCommessa.NATURA_CMM__GESTIONALE)) {
			return new ErrorMessage("THIP_TN801");//Commessa con natura commessa errata
		}
		return null;
	}
	
	public ErrorMessage checkDataRiferimento() {
		if(isOnDB() && !isDataRiferimentoValide()) {
			return new ErrorMessage("THIP_TN753");//"Data riferimento è antecedente a un budget già presenti"
		}
		return null;
	}
	
	public boolean isPreventivoValide(PreventivoCommessaRiga prevRiga, Commessa commessa) {	
		List<Commessa> sottoCommesse = commessa.loadSottocommesseDirette();
		List<PreventivoCommessaRiga> prevRighe = prevRiga.getSottoCommesse();
		for(int i=0; i<sottoCommesse.size(); i++) {
			Commessa sc = (Commessa)sottoCommesse.get(i);
			List<PreventivoCommessaRiga> prevRigheSC = new ArrayList<PreventivoCommessaRiga>();
			for(int j=0; j<prevRighe.size(); j++) {
				PreventivoCommessaRiga riga = (PreventivoCommessaRiga)prevRighe.get(j);
				if(riga.getIdCommessa().equals(sc.getIdCommessa()))
					prevRigheSC.add(riga);
			}
			if(prevRigheSC == null || prevRigheSC.isEmpty() || prevRigheSC.size() > 1) {
				return false;
			}
			else if(prevRigheSC.size() == 1) {
				boolean ret = isPreventivoValide((PreventivoCommessaRiga)prevRigheSC.get(0), sc);
				if(!ret)
					return false;
			}			
		}
		
		return true;
	}
	
	public ErrorMessage checkIdNumeroPreventivo() {
		boolean isPreventivoValide = true;
		if(getPreventivo() != null && getCommessa() != null) {
			if(!getPreventivo().getIdCommessa().equals(getIdCommessa())) {
				isPreventivoValide = false;
			}
			else {
				List<PreventivoCommessaRiga> listRigheCommessa = getPreventivo().getRigheCommesse();
				if(listRigheCommessa != null && !listRigheCommessa.isEmpty()) {
					if(listRigheCommessa.size() > 1) {
						isPreventivoValide = false;
					}
					else {
						PreventivoCommessaRiga prevRiga = (PreventivoCommessaRiga)listRigheCommessa.get(0);
						if(!prevRiga.getIdCommessa().equals(getIdCommessa())) {
							isPreventivoValide = false;
						}
						else {
							isPreventivoValide = isPreventivoValide(prevRiga, getCommessa());
						}
					}					
				}
				else {
					isPreventivoValide = false;
				}		
			}
		}
		if(!isPreventivoValide)
			return new ErrorMessage("THIP_TN760");//Preventivo con struttura non compatibile con quella della commessa.
		
		return null;
	}
	
	public boolean isDataRiferimentoValide() {
		List<BudgetCommessa> budgetProvv = getBudgetsCommessa(PROVVISORIO);
		for (int i = 0; i < budgetProvv.size(); i++) {
			BudgetCommessa budget = (BudgetCommessa) budgetProvv.get(i);
			if(!budget.getKey().equals(getKey()) && budget.getDataRiferimento().compareTo(getDataRiferimento()) > 0) {
				return false;
			}
		}
		
		List<BudgetCommessa> budgetDef = getBudgetsCommessa(DEFINITIVO);
		for (int i = 0; i < budgetDef.size(); i++) {
			BudgetCommessa budget = (BudgetCommessa) budgetDef.get(i);
			if(!budget.getKey().equals(getKey()) && budget.getDataRiferimento().compareTo(getDataRiferimento()) > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isSingoloProovisorio() {
		if(getStatoAvanzamento() == BudgetCommessa.DEFINITIVO)
			return false;
		List<BudgetCommessa> budgets = getBudgetsCommessa(BudgetCommessa.PROVVISORIO);
		if(budgets != null && !budgets.isEmpty() && budgets.size() > 1)
			return false;
		
		return true;
	}
	
	public List<BudgetCommessa> getBudgetsCommessa(char statoAvanzamento) {
		List<BudgetCommessa> ret = new ArrayList<BudgetCommessa>();
		String where = BudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + 
					   BudgetCommessaTM.ID_COMMESSA	+ "='" + getIdCommessa() + "' AND " + 
					   BudgetCommessaTM.STATO_AV + "='" + statoAvanzamento + "'";
		PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
		try {
			while (cursor.hasNext()) {
				BudgetCommessa budget = (BudgetCommessa) cursor.next();
				ret.add(budget);
			}
		} catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return ret;
	}
	
	public BudgetCommessa caricaBudgetCommessa(Commessa commessa) {
	   BudgetCommessa currentBudget = null;
	   try {
	      String currentBudgetKey = KeyHelper.buildObjectKey(new Object[] {getIdAzienda(), getIdBudget(), commessa.getIdCommessa()});
	      currentBudget = BudgetCommessa.elementWithKey(currentBudgetKey, PersistentObject.NO_LOCK);
	      if(currentBudget == null) {
	         currentBudget = creaNuovoBudgetCommessa(commessa);
	      }

	      currentBudget.setTotali(isTotali());
	      currentBudget.setComponentiPropri(isComponentiPropri());
	      currentBudget.setDettagliCommessa(isDettagliCommessa());
	      currentBudget.setDettagliSottoCommesse(isDettagliSottoCommesse());
	      currentBudget.setSoloComponentiValorizzate(isSoloComponentiValorizzate());
	      currentBudget.setIdAnnoPreventivo(getIdAnnoPreventivo());
	      currentBudget.setIdNumeroPreventivo(getIdNumeroPreventivo());
	      currentBudget.setForzaRicalcolo(isForzaRicalcolo());
	      currentBudget.setStatoAvanzamento(getStatoAvanzamento());
	      currentBudget.setCalcolatoreDettagli(getCalcolatoreDettagli());
	   } 
	   catch (SQLException e1) {
	      e1.printStackTrace(Trace.excStream);
	   }

	   return currentBudget;
	}
	
	public BudgetCommessa creaNuovoBudgetCommessa(Commessa commessa) {
		BudgetCommessa nuovoBudget = (BudgetCommessa)Factory.createObject(BudgetCommessa.class);		
		try {
			nuovoBudget.setEqual(this);
			nuovoBudget.setIdAzienda(getIdAzienda());
			nuovoBudget.setIdBudget(getIdBudget());
			nuovoBudget.setIdAnnoPreventivo(getIdAnnoPreventivo());
			nuovoBudget.setIdNumeroPreventivo(getIdNumeroPreventivo());
			nuovoBudget.setIdCommessa(commessa.getIdCommessa());
			nuovoBudget.setIdCommessaApp(commessa.getIdCommessaAppartenenza());
			nuovoBudget.setIdCommessaPrm(commessa.getIdCommessaPrincipale());
			nuovoBudget.setIdStabilimento(commessa.getIdStabilimento());
			nuovoBudget.setIdArticolo(commessa.getIdArticolo());			
			nuovoBudget.setIdVersione(commessa.getIdVersione());
			nuovoBudget.setIdConfigurazione(commessa.getIdConfigurazione());
			nuovoBudget.setIdUMPrmMag(commessa.getIdUmPrmMag());
			nuovoBudget.setQuantitaPrm(commessa.getQtaUmPrm());
			nuovoBudget.setDescrizione(getDescrizione());
			nuovoBudget.setDataRiferimento(getDataRiferimento());
			nuovoBudget.getBudgetCommessaDet().clear();
		} 
		catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		} 		
		
		return nuovoBudget;
	}
	
	public JSONArray getColumnsDescriptors() throws JSONException {
		JSONArray columns = new JSONArray();
		columns.put(getColumnDescriptors(""));
		columns.put(getColumnDescriptors("Budget"));
		return columns;
	}
	
	public JSONObject getColumnDescriptors(String title) throws JSONException {
		JSONObject column = new JSONObject();
		if(title == null || title.equals("")) {
			column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", "CompCosto").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold")).put("editable", false);
		}
		else {
			if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
				column.put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false)).put("editable", false);
				column.put("colModel", getColumnModelATempo(title));								
			}
			else {
				String dataIndx = title.substring(0, 3);
				column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI);
				if(getStatoAvanzamento() == PROVVISORIO)
					column.put("editable", "columnValoreEditable");
				else
					column.put("editable", false);
			}			
		}
		return column;		
	}
	
	public JSONArray getColumnModelATempo(String title) throws JSONException {
		JSONArray columns = new JSONArray();
		String dataIndx = title.substring(0, 3);
		JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"HH").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_ORE);

		if(getStatoAvanzamento() == PROVVISORIO)
			columnOre.put("editable", "columnOreEditable");
		else
			columnOre.put("editable", false);			

		columns.put(columnOre);
		JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI);

		if(getStatoAvanzamento() == PROVVISORIO)
			columnValore.put("editable", "columnValoreEditable");
		else
			columnValore.put("editable", false);

		columns.put(columnValore);
		return columns;
	}
	
	public JSONArray getDataModelInMemoria() {
	   JSONArray data = new JSONArray();
	   Commessa commessaPrm = getCommessa();
	   if(commessaPrm != null) 
	   {
	      try 
	      {
	         writeJSONDatiInMemoria(data, "1", null, this, commessaPrm, null);
	      } 
	      catch (JSONException e) {
	         e.printStackTrace(Trace.excStream);
	      }
	   }     
	   return data;
	}

	@SuppressWarnings("unchecked")
	protected void writeJSONDatiInMemoria(JSONArray data, String id, String idParent, BudgetCommessa budget, Commessa commessaPrm, Commessa commessaApp) throws JSONException {
	   String parent = "";
	   Commessa commessa = commessaPrm;
	   if(commessaApp != null) {
	      commessa = commessaApp;
	   }

	   if(idParent != null) {        
	      parent = idParent;
	   }

	   BudgetCommessaDet dettaglioTotali = cercaBudgetCommessaDetTotali(budget, commessa.getIdCommessa(), getIdComponenteTotali());
	   if(dettaglioTotali == null) {
	      dettaglioTotali = (BudgetCommessaDet)Factory.createObject(BudgetCommessaDet.class);
	      dettaglioTotali.setIdAzienda(budget.getIdAzienda());
	      dettaglioTotali.setIdCommessa(commessa.getIdCommessa());
	      dettaglioTotali.setIdComponCosto(getIdComponenteTotali());
	   }
	   int idxGrup = 1;
	   writeCommessaGruppo(data, String.valueOf(id), parent, commessa.getDescrizione().getDescrizione(), commessa.getIdCommessa() + " - " + commessa.getDescrizione().getDescrizione(), commessaPrm.hasCompenenteATempo(), dettaglioTotali);

	   if(isTotali()) {
	      String idGruppTot = id + idxGrup;
	      writeTotale(data, idGruppTot, String.valueOf(id), dettaglioTotali, commessaPrm.hasCompenenteATempo(), false);
	      if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
	         writeTotaleDettagli(data, id, idGruppTot, budget.getBudgetCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate());
	   }

	   if(isComponentiPropri()) {
	      idxGrup++;
	      String idGruppCmp = id + idxGrup;
	      writeComponentePropri(data, idGruppCmp, id, commessaPrm.hasCompenenteATempo(), false, dettaglioTotali);
	      if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
	         writeComponentePropriDettagli(data, id, idGruppCmp, budget.getBudgetCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate());
	   }

	   Iterator budIter = budgetLivelliInferiori.iterator();
	   while(budIter.hasNext())
	   {
	      BudgetCommessa currentBudget = (BudgetCommessa)budIter.next();
	      idxGrup++;
	      String idSottoCmm = id + idxGrup;
	      currentBudget.writeJSONDatiInMemoria(data, idSottoCmm, id, currentBudget, commessaPrm, currentBudget.getCommessa());
	   }
	}

	public void writeCommessaGruppo(JSONArray data, String id, String parentId, String descCommessa,
			String compCosto, boolean isATempo, BudgetCommessaDet dettaglio) throws JSONException {

		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("parentId", parentId);
		json.put("pq_rowstyle", "background-color: #E5F4FF;font-weight: bold;"); //D8d8d8
		json.put("Commessa", descCommessa);
		json.put("CompCosto", compCosto);
		if (getComponenteCosto() != null && getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI
				&& dettaglio.getBudgetCommessa().getStatoAvanzamento() == BudgetCommessa.PROVVISORIO) {// 35382
			json.put("CmpElem", "Y");
			if (getComponenteCosto().isGestioneATempo()) {
				json.put("CmpATempo", "Y");
			} else {
				json.put("CmpATempo", "N");
			}
		}

		json.put("IdCmpCosto", dettaglio.getIdComponCosto());

		json.put("BudgetDetKey", getKey());
		boolean valValorizzato = dettaglio.getCostoTotale().compareTo(ZERO) != 0;
		json.put("BudVal", "" + (valValorizzato ? dettaglio.getCostoTotale() : ""));
		if (isATempo) {
			if (dettaglio.getComponenteCosto().isGestioneATempo()) {
            boolean oreValorizzate = dettaglio.getTempoTotale().compareTo(ZERO) != 0;
            json.put("BudHH", "" + (oreValorizzate ? dettaglio.getTempoTotale() : ""));
			} else {
				json.put("BudHH", "");
			}
		}
		data.put(json);
	}

	public void writeComponentePropri(JSONArray data, String id, String parentId, boolean isATempo, boolean isDettaglio, BudgetCommessaDet dettaglio){
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("parentId", parentId);
			if(isDettaglio) {
				json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
				json.put("TotaleDet", "N");
				json.put("CompDet", "Y");
				if (dettaglio.getComponenteCosto() != null && dettaglio.getComponenteCosto().getProvenienza() == ComponenteCosto.ELEMENTARI
						&& dettaglio.getBudgetCommessa().getStatoAvanzamento() == BudgetCommessa.PROVVISORIO) {// 35382
					json.put("CmpElem", "Y");
					if (dettaglio.getComponenteCosto().isGestioneATempo()) {
						json.put("CmpATempo", "Y");
					} else {
						json.put("CmpATempo", "N");
					}
				}
			}
			else {
				json.put("pq_rowstyle", "background-color: #F0F0F0;font-weight: bold;");
				json.put("CompCosto", ResourceLoader.getString(RES_FILE, "ComponentiProprie"));	
			}
			
			json.put("BudgetDetKey", dettaglio.getKey());
			boolean valValorizzato = dettaglio.getCostoLivello().compareTo(ZERO) != 0;
			json.put("BudVal", "" + (valValorizzato ? dettaglio.getCostoLivello() : ""));
			if (isATempo) {
				if (dettaglio.getComponenteCosto().isGestioneATempo()) {
				   boolean oreValorizzate = dettaglio.getTempoLivello().compareTo(ZERO) != 0;
				   json.put("BudHH", "" + (oreValorizzate ? dettaglio.getTempoLivello() : ""));
				} else {
					json.put("BudHH", "");
				}
			}
	
			data.put(json);
		}
		catch (JSONException e) {
			e.printStackTrace(Trace.excStream);
		}
	}
	
	public void writeTotale(JSONArray data, String id, String idParent, BudgetCommessaDet dettaglio, boolean isATempo, boolean isDettaglio) {
		try {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("parentId", idParent);
			if(isDettaglio) {
				json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
			}
			else {
				json.put("pq_rowstyle", "background-color: #F0F0F0;font-weight: bold;");
				json.put("CompCosto", ResourceLoader.getString(RES_FILE, "Totali"));	
			}	

			json.put("BudgetDetKey", dettaglio.getKey());	
			
			boolean valValorizzato = dettaglio.getCostoTotale().compareTo(ZERO) != 0;
			json.put("BudVal", (valValorizzato? dettaglio.getCostoTotale(): ""));
			if(isATempo) {
				if (dettaglio.getComponenteCosto().isGestioneATempo()) {
               boolean oreValorizzate = dettaglio.getTempoTotale().compareTo(ZERO) != 0;
				   json.put("BudHH", "" + (oreValorizzate ? dettaglio.getTempoTotale() : ""));
				} else {
					json.put("BudHH", "");
				}
			}
			data.put(json);
		}
		catch (JSONException e) {
			e.printStackTrace(Trace.excStream);
		}
	}
	
	public void writeTotaleDettagli(JSONArray data, String id, String idParent, List<BudgetCommessaDet> budgetsCommessaDet, Commessa commessa, boolean isATempo, boolean  soloComponentiValorizzate) {
		int idxDet = 1;
		for(int i = 0; i < budgetsCommessaDet.size(); i++) {
			BudgetCommessaDet dettaglio = budgetsCommessaDet.get(i);
			if((!soloComponentiValorizzate) || (soloComponentiValorizzate && dettaglio.getCostoTotale() != null && dettaglio.getCostoTotale().compareTo(new BigDecimal(0)) > 0)) {
				String idDet = idParent + idxDet;
				writeTotale(data, idDet, idParent, dettaglio, isATempo, true);
				idxDet ++;
			}
		}
	}
	
	public void writeComponentePropriDettagli(JSONArray data, String id, String idParent, List<BudgetCommessaDet> budgetsCommessaDet, Commessa commessa, boolean isATempo, boolean soloComponentiValorizzate) {
		int idxDet = 1;
		for(int i = 0; i < budgetsCommessaDet.size(); i++) {
			BudgetCommessaDet dettaglio = budgetsCommessaDet.get(i);
			if((!soloComponentiValorizzate) || (soloComponentiValorizzate && dettaglio.getCostoTotale() != null && dettaglio.getCostoLivello().compareTo(new BigDecimal(0)) > 0)) {
				String idDet = idParent + idxDet;				
				writeComponentePropri(data, idDet, idParent, isATempo, true, dettaglio);
				idxDet ++;
			}
		}
	}
			
	public BudgetCommessaDet cercaBudgetCommessaDetTotali(BudgetCommessa budget, String idCommessa, String idComponenteTotali) {
		for(int i = 0; i < budget.getBudgetCommessaDet().size(); i++) {
			BudgetCommessaDet dettaglio = (BudgetCommessaDet)budget.getBudgetCommessaDet().get(i);
			if(dettaglio.getIdCommessa().equals(idCommessa) && dettaglio.getIdComponCosto().equals(idComponenteTotali))
				return dettaglio;
		}
		return null;
	}	
		
	public boolean aggiornaBudgetDet(String budgetDetKey, String oldValueStr, String newValueStr, boolean updatedOre) {
		caricaAlberoBudget();
		Object[] keyParts = KeyHelper.unpackObjectKey(budgetDetKey);
		String idComm = (String)keyParts[2];
		String idCompCosto = (String)keyParts[3];
		boolean ok = aggiornaDettaglio(idComm, idCompCosto, newValueStr, updatedOre);

		if(ok) {
			try	{
				int rc = save();
				if(rc > 0)
					ConnectionManager.commit();
				else
					ConnectionManager.rollback();   
			}
			catch(Exception ex)	{
				ex.printStackTrace(Trace.excStream);
			}
		}
		return true;
   }
	
	protected boolean aggiornaDettaglio(String idCommessa, String idCompCosto, String newValue, boolean updateOre){
	   if(idCommessa.equals(getIdCommessa()))	   {
	      BudgetCommessaDet dettaglio = getDettaglio(this,  idCompCosto);
	      dettaglio.aggiornaValore(newValue, updateOre);
	      return true;
	   }
	   else {
         for(BudgetCommessa curBudget: budgetLivelliInferiori) {
	         boolean ret = curBudget.aggiornaDettaglio(idCommessa, idCompCosto, newValue, updateOre);
	         if(ret)
	            return true;
	      }
	   }
	   return false;
 	}
	
	public List<BudgetCommessa> getBudgets() {
		List<BudgetCommessa> ret = new ArrayList<BudgetCommessa>();
		String where = BudgetCommessaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " +
				   	   BudgetCommessaTM.ID_BUDGET + "=" + getIdBudget() ;
		PersistentObjectCursor cursor = new PersistentObjectCursor(BudgetCommessa.class.getName(), where, "", PersistentObject.NO_LOCK);
		try {
			while (cursor.hasNext()) {
				BudgetCommessa budget = (BudgetCommessa)cursor.next();
				ret.add(budget);
			}
		}
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return ret;
	}
	
	public boolean valorizzaCostiBudget() {
		PersDatiTecnici psnDatiTecnici = PersDatiTecnici.getCurrentPersDatiTecnici();
		String idRiferimento = psnDatiTecnici.getIdRiferimento();
		String idPrimo = psnDatiTecnici.getIdPrimo();
		String idIndustriale = psnDatiTecnici.getIdIndustriale();
		String idGenerale = psnDatiTecnici.getIdGenerale();
		List budgetsDetList = getBudgetCommessaDet();
	    List compDaValorizz = new ArrayList();
	    List compValorizz = new ArrayList();
		for (int i = 0; i < budgetsDetList.size(); i++) {
			BudgetCommessaDet budgetDet = (BudgetCommessaDet) budgetsDetList.get(i);			
			if (budgetDet.getIdComponCosto().equals(idRiferimento))
				setCostoRiferimento(budgetDet.getCostoTotale());
			if (budgetDet.getIdComponCosto().equals(idPrimo))
				setCostoPrimo(budgetDet.getCostoTotale());
			if (budgetDet.getIdComponCosto().equals(idIndustriale))
				setCostoIndustriale(budgetDet.getCostoTotale());
			if (budgetDet.getIdComponCosto().equals(idGenerale))
				setCostoGenerale(budgetDet.getCostoTotale());
		}
		return true;
	}
	//35382 inizio
	public boolean rendiDefinitivo() {
		if(getStatoAvanzamento() == DEFINITIVO )
			return false;
		try {
			setStatoAvanzamento(DEFINITIVO);
			int rc = save();
			if(rc < ErrorCodes.NO_ROWS_UPDATED) {
				return false;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return true;
	}
	
	public boolean rendiProvvisorio() {
		if(getStatoAvanzamento() == PROVVISORIO )
			return false;
		try {
			setStatoAvanzamento(PROVVISORIO);
			int rc = save();
			if(rc < ErrorCodes.NO_ROWS_UPDATED) {
				return false;
			}
		} 
		catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
		return true;
	}
	//35382 fine	
}


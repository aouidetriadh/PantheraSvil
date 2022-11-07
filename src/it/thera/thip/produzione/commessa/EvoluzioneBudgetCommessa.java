package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.BusinessObjectAdapter;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Database;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.Conflictable;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.commessa.Commessa;
import it.thera.thip.base.commessa.CommessaTM;
import it.thera.thip.base.commessa.TipoCommessa;
import it.thera.thip.cs.DatiComuniEstesi;
import it.thera.thip.datiTecnici.costi.ComponenteCosto;
/**
 * EvoluzioneBudgetCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 01/09/2022 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 36460   01/09/2022    RA		    Prima struttura
 */
public class EvoluzioneBudgetCommessa extends BusinessObjectAdapter implements Authorizable, Conflictable {

   public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.EvoluzioneBudgetCommessa";

   public final static BigDecimal ZERO = new BigDecimal(0);

   protected java.sql.Date iDataInizio;
   protected java.sql.Date iDataFine;

   protected boolean iTotali = true;
   protected boolean iDettagliCommessa = true;
   protected boolean iDettagliSottoCommesse = true;
   protected boolean iComponentiPropri = false;
   protected boolean iSoloComponentiValorizzate = false;	

   protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);
   protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);

   protected List<BudgetCommessa> iBudgets = null;

   public EvoluzioneBudgetCommessa() {
      setIdAzienda(Azienda.getAziendaCorrente());
   }

   public void setDataInizio(java.sql.Date dataInizio) {
      this.iDataInizio = dataInizio;
   }

   public java.sql.Date getDataInizio() {
      return iDataInizio;
   }

   public void setDataFine(java.sql.Date dataFine) {
      this.iDataFine = dataFine;
   }

   public java.sql.Date getDataFine() {
      return iDataFine;
   }

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

   public List<BudgetCommessa> getBudgets(String idCommessa) {
      if(iBudgets == null)
         initializeBudgets(idCommessa);
      return iBudgets;
   }

   public void initializeBudgets(String idCommessa) {
      iBudgets = new ArrayList<BudgetCommessa>();
      Database db  = ConnectionManager.getCurrentDatabase();
      String where  = BudgetCommessaTM.ID_AZIENDA + "='" + Azienda.getAziendaCorrente() + "' AND " + 
            BudgetCommessaTM.ID_COMMESSA + "='" + idCommessa + "' " ;
      if(getDataInizio() != null) {
         where +=  " AND " + BudgetCommessaTM.DATA_RIFERENTO + " >= " + db.getLiteral(getDataInizio()) + "";
      }
      if(getDataFine() != null) {
         where += " AND " +	BudgetCommessaTM.DATA_RIFERENTO + " <= " + db.getLiteral(getDataFine()) + "";
      }

      String orderBy = BudgetCommessaTM.DATA_RIFERENTO + " ASC ";
      try {
         iBudgets = BudgetCommessa.retrieveList(BudgetCommessa.class, where, orderBy, false);
         Iterator budIter = iBudgets.iterator();
         while(budIter.hasNext())
         {
            BudgetCommessa budget = (BudgetCommessa)budIter.next();
            budget.caricaAlberoBudget();
         }
      } 
      catch (Exception e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   public void initializeAttDaScreenData() {
      ScreenData sd = ScreenData.getDefaultScreenData("EvoluzioneBudgetCommessa");
      if (sd != null && sd.getAttValue("Totali") != null && sd.getAttValue("Totali").equals("N"))
         iTotali = false;
      if (sd != null && sd.getAttValue("DettagliCommessa") != null && sd.getAttValue("DettagliCommessa").equals("N"))
         iDettagliCommessa = false;
      if (sd != null && sd.getAttValue("DettagliSottoCommesse") != null && sd.getAttValue("DettagliSottoCommesse").equals("N"))
         iDettagliSottoCommesse = false;
      if (sd != null && sd.getAttValue("ComponentiPropri") != null && sd.getAttValue("ComponentiPropri").equals("Y"))
         iComponentiPropri = true;
      if (sd != null && sd.getAttValue("SoloComponentiValorizzate") != null && sd.getAttValue("SoloComponentiValorizzate").equals("Y"))
         iSoloComponentiValorizzate = true;
   }

   public void setAzienda(Azienda azienda) {
      setIdAziendaInternal(azienda.getKey());
      this.iAzienda.setObject(azienda);
   }

   public Azienda getAzienda() {
      return (Azienda) iAzienda.getObject();
   }

   public void setAziendaKey(String key) {
      iAzienda.setKey(key);
      setIdAziendaInternal(key);
   }

   public String getAziendaKey() {
      return iAzienda.getKey();
   }

   public void setIdAzienda(String idAzienda) {
      setIdAziendaInternal(idAzienda);
   }

   public String getIdAzienda() {
      String key = iAzienda.getKey();
      String objIdAzienda = KeyHelper.getTokenObjectKey(key, 1);
      return objIdAzienda;
   }

   public void setCommessa(Commessa commessa) {
      String idAzienda = null;
      String idCommessa = null;

      if (commessa != null) {
         idAzienda = KeyHelper.getTokenObjectKey(commessa.getKey(), 1);
         idCommessa = KeyHelper.getTokenObjectKey(commessa.getKey(), 2);
      }
      setIdAziendaInternal(idAzienda);
      setIdCommessaInternal(idCommessa);
      this.iCommessa.setObject(commessa);
   }

   public Commessa getCommessa() {
      return (Commessa) iCommessa.getObject();
   }

   public void setCommessaKey(String key) {
      iCommessa.setKey(key);
      String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
      setIdAziendaInternal(idAzienda);

      String idCommessa = KeyHelper.getTokenObjectKey(key, 2);
      setIdCommessaInternal(idCommessa);
   }

   public String getCommessaKey() {
      return iCommessa.getKey();
   }

   public void setIdCommessa(String idCommessa) {
      String key = iCommessa.getKey();
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCommessa));
      setIdCommessaInternal(idCommessa);
   }

   public String getIdCommessa() {
      String key = iCommessa.getKey();
      String objIdCommessa = KeyHelper.getTokenObjectKey(key, 2);
      return objIdCommessa;
   }

   protected void setIdAziendaInternal(String idAzienda) {
      iAzienda.setKey(idAzienda);
      String key1 = iCommessa.getKey();
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
   }

   protected void setIdCommessaInternal(String idCommessa) {
      String key1 = iCommessa.getKey();
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, idCommessa));
   }

   public ErrorMessage checkIdCommessa() {
      if(getCommessa() != null && getCommessa().getCommessaAppartenenza() != null )
         return new ErrorMessage("THIP_TN723");//Non è possibile inserire una sotto commessa
      else {
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
      }
      return null;
   }

   public ErrorMessage checkDataInizio() {
      if(getDataInizio() != null && getDataFine() != null && getDataInizio().compareTo(getDataFine()) > 0) {
         return new ErrorMessage("THIP_TN889");
      }
      return null;
   }

   public JSONArray getColumnsDescriptors() throws JSONException {
      JSONArray columns = new JSONArray();
      columns.put(getColumnDescriptors());
      List<BudgetCommessa> budgets = getBudgets(getIdCommessa());
      if(budgets != null && !budgets.isEmpty()) {
         Iterator<BudgetCommessa> iterBudget = budgets.iterator();
         while(iterBudget.hasNext()) {
            BudgetCommessa budget = (BudgetCommessa)iterBudget.next();
            columns.put(getColumnDescriptorsBudget(budget));
         }
      }		
      return columns;
   }

   public JSONObject getColumnDescriptors() throws JSONException {
      JSONObject column = new JSONObject();
      column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", "CompCosto").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold")).put("editable", false);
      return column;		
   }

   public JSONObject getColumnDescriptorsBudget(BudgetCommessa budget) throws JSONException {
      JSONObject column = new JSONObject();
      if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
         column.put("title", budget.getDataRiferimento()).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", true));
         column.put("colModel", getColumnModelATempo(budget));							
      }
      else {
         column = new JSONObject().put("title", budget.getDataRiferimento()).put("width", "260").put("halign", "center").put("align", "right").put("dataIndx", "BudVal" + budget.getIdBudget()).put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI).put("editable", false);			
      }	
      return column;
   }

   public JSONArray getColumnModelATempo(BudgetCommessa budget) throws JSONException {
      JSONArray columns = new JSONArray();		
      JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", "BudHH" + budget.getIdBudget()).put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_ORE);
      columns.put(columnOre);		
      JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", "BudVal" + budget.getIdBudget()).put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI);
      columns.put(columnValore);
      return columns;
   }

   protected String getValoreString(BigDecimal valore, boolean vuoto)
   {
      boolean valorizzato = valore != null && valore.compareTo(ZERO) != 0;
      if(valorizzato)
         return "" + valore;
      else
      {
         if(!vuoto)
            return "" + ZERO;
         else
            return "";
      }
   }

   protected String getOreString(BigDecimal valore, boolean aTempo,  boolean vuoto)
   {
      if(aTempo)
         return getValoreString(valore, vuoto);
      else
         return "";
   }

   public JSONArray getDataModel() {
      JSONArray data = new JSONArray();
      Commessa commessaPrm = getCommessa();
      List<BudgetCommessa> budgets = getBudgets(getIdCommessa());
      if(commessaPrm != null && budgets != null && !budgets.isEmpty()) {
         try {
            writeJSONDati(data, "1", null, commessaPrm, null, budgets);
         } 
         catch (Exception e) {
            e.printStackTrace(Trace.excStream);
         }
      }		
      return data;
   }

   @SuppressWarnings("unchecked")
   protected void writeJSONDati(JSONArray data, String id, String idParent, Commessa commessaPrm, Commessa commessaApp, List<BudgetCommessa> budgets) throws Exception {
      String parent = "";
      Commessa commessa = commessaPrm;
      if(commessaApp != null) {
         commessa = commessaApp;
      }

      if(idParent != null) {			
         parent = String.valueOf(idParent);
      }

      List<BudgetCommessaDet> dettagliTotale = cercaBudgetCommessaDets(budgets, true, null);

      //write gruppamento Commessa
      int idxGrup = 1;	
      writeCommessaGruppo(data,String.valueOf(id), parent, commessa, dettagliTotale);

      //write gruppamento Totali
      if(isTotali()) {
         String idGruppTot = String.valueOf(id) + idxGrup;
         writeTotale(data, idGruppTot, id, commessaPrm.hasCompenenteATempo(), false, dettagliTotale);
         //write dettagli totale
         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
            writeTotaleDettagli(data, id, idGruppTot, commessaPrm.hasCompenenteATempo(), budgets);
      }

      //write gruppamento componenti propri
      if(isComponentiPropri()) {
         idxGrup++;
         String idGruppCmp = id + idxGrup;
         writeComponentePropri(data, idGruppCmp, id, commessaPrm.hasCompenenteATempo(), false,dettagliTotale);
         //write dettagli componenti propri
         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
            writeComponentePropriDettagli(data, id, idGruppCmp, commessaPrm.hasCompenenteATempo(), budgets);
      }

      List<Commessa> sottoCommesse = getSottoCommesse(commessaPrm, commessa);
      if(sottoCommesse == null || sottoCommesse.isEmpty())
         return;

      for(int i = 0; i < sottoCommesse.size(); i++) 
      {
         Commessa sottoCommessa = (Commessa)sottoCommesse.get(i);
         List<BudgetCommessa> sottoBudgests = new ArrayList<BudgetCommessa>();
         for(BudgetCommessa curBud : budgets)
         {
            BudgetCommessa curSottoBud = curBud.getBudgetLivelloInferiore(sottoCommessa);
            if(curSottoBud.isOnDB() == false)
               curSottoBud.calcolaAlberoBudget();
            sottoBudgests.add(curSottoBud);
         }

         if(sottoBudgests != null && !sottoBudgests.isEmpty()) {
            idxGrup++;
            String idSottoCmm = id + idxGrup;
            writeJSONDati(data, idSottoCmm, id, commessaPrm, sottoCommessa, sottoBudgests); 
         }        
      }
   }

   public void writeTotaleDettagli(JSONArray data, String id, String idParent, boolean isATempo, List<BudgetCommessa> budgets) {
      int idxDet = 1;
      if(budgets != null && !budgets.isEmpty()) {
         BudgetCommessa budget = (BudgetCommessa) budgets.get(0);
         List<BudgetCommessaDet> budgetsCommessaDet =  budget.getBudgetCommessaDet();
         for(int i = 0; i < budgetsCommessaDet.size(); i++) {
            BudgetCommessaDet dettaglio = budgetsCommessaDet.get(i);
            List<BudgetCommessaDet> dettagli = cercaBudgetCommessaDets(budgets, false, dettaglio.getIdComponCosto());
            if((!isSoloComponentiValorizzate()) || (isSoloComponentiValorizzate() && hasDettagliValorizzate(dettagli, true))) {
               String idDet = idParent + idxDet;
               writeTotale(data, idDet, idParent, isATempo, true, dettagli);
               idxDet ++;
            }
         }
      }
   }

   public void writeTotale(JSONArray data, String id, String idParent, boolean isCmpAtempo, boolean isDettaglio, List<BudgetCommessaDet> dettagli) {
      try {
         String stileRiga = "RigaNormale";
         boolean cmpTotali = false;
         JSONObject json = new JSONObject();
         json.put("id", id);
         json.put("parentId", idParent);
         if(isDettaglio) {
            BudgetCommessaDet dettaglio = dettagli.get(0);
            if(dettaglio != null)
            {
               json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
               if(dettaglio.getComponenteCosto() != null && dettaglio.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
                  cmpTotali = true;
            }
         }
         else {
            stileRiga = "RigaTotali";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "Totali")); 
         }

         Iterator<BudgetCommessaDet> iterDettagli = dettagli.iterator();
         while(iterDettagli.hasNext()) {
            BudgetCommessaDet dettaglio = (BudgetCommessaDet)iterDettagli.next();
            json.put("BudVal" + dettaglio.getIdBudget(), getValoreString(dettaglio.getCostoTotale(), true));			
            if(dettaglio.getComponenteCosto().isGestioneATempo()) 
               json.put("BudHH" + dettaglio.getIdBudget(),  getOreString(dettaglio.getTempoTotale(), dettaglio.getComponenteCosto().isGestioneATempo(), true));
         }		

         json.put("pq_rowcls", stileRiga);

         JSONObject stiliCelle = new JSONObject();
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");
         json.put("pq_cellcls", stiliCelle);
         data.put(json);
      }
      catch (JSONException e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   public void writeComponentePropriDettagli(JSONArray data, String id, String idParent, boolean isATempo, List<BudgetCommessa> budgets) {
      int idxDet = 1;
      if(budgets != null && !budgets.isEmpty()) {
         BudgetCommessa budget = (BudgetCommessa) budgets.get(0);
         List<BudgetCommessaDet> budgetsCommessaDet =  budget.getBudgetCommessaDet();
         for(int i = 0; i < budgetsCommessaDet.size(); i++) {
            BudgetCommessaDet dettaglio = budgetsCommessaDet.get(i);
            List<BudgetCommessaDet> dettagli = cercaBudgetCommessaDets(budgets, false, dettaglio.getIdComponCosto());
            if((!isSoloComponentiValorizzate()) || (isSoloComponentiValorizzate() && hasDettagliValorizzate(dettagli, false))) {
               String idDet = idParent + idxDet;
               writeComponentePropri(data, idDet, idParent, isATempo, true, dettagli);
               idxDet ++;
            }
         }
      }
   }

   public boolean hasDettagliValorizzate(List<BudgetCommessaDet> dettagli, boolean isTotale) {
      Iterator<BudgetCommessaDet> iterDettagli = dettagli.iterator();
      while(iterDettagli.hasNext()) {
         BudgetCommessaDet dettaglio = (BudgetCommessaDet)iterDettagli.next();
         if(isTotale) {
            if(dettaglio.getCostoTotale() != null && dettaglio.getCostoLivello().compareTo(new BigDecimal(0)) > 0) {
               return true;
            }
         }
         else {
            if(dettaglio.getCostoLivello() != null && dettaglio.getCostoLivello().compareTo(new BigDecimal(0)) > 0) {
               return true;
            }
         }			
      }
      return false;
   }

   public void writeComponentePropri(JSONArray data, String id, String idParent, boolean isATempo, boolean isDettaglio, List<BudgetCommessaDet> dettagli) {
      try {
         boolean cmpTotali = false;
         String stileRiga = "RigaNormale";
         JSONObject json = new JSONObject();
         json.put("id", id);
         json.put("parentId", idParent);

         if(isDettaglio) 
         {
            BudgetCommessaDet dettaglio = dettagli.get(0);
            if(dettaglio != null)
            {
               json.put("CompCosto", dettaglio.getIdComponCosto() + " - " + dettaglio.getComponenteCosto().getDescrizione().getDescrizione());
               if(dettaglio.getComponenteCosto() != null && dettaglio.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
                  cmpTotali = true;
            }
         }
         else{
            stileRiga = "RigaCmpPropri";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "ComponentiProprie"));
         }

         Iterator<BudgetCommessaDet> iterDettagli = dettagli.iterator();
         while(iterDettagli.hasNext()) {
            BudgetCommessaDet dettaglio = (BudgetCommessaDet)iterDettagli.next();

            json.put("BudVal" + dettaglio.getIdBudget(), getValoreString(dettaglio.getCostoLivello(), true));			
            if(dettaglio.getComponenteCosto().isGestioneATempo()) 
               json.put("BudHH" + dettaglio.getIdBudget(), getOreString(dettaglio.getTempoLivello(), dettaglio.getComponenteCosto().isGestioneATempo(), true));
         }

         json.put("pq_rowcls", stileRiga);

         JSONObject stiliCelle = new JSONObject();
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");
         json.put("pq_cellcls", stiliCelle);

         data.put(json);
      }
      catch (JSONException e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   protected List<Commessa> getSottoCommesse(Commessa commessaPrm, Commessa commessaApp) {
      try {
         String whereSC = CommessaTM.ID_AZIENDA + "='" + commessaPrm.getIdAzienda() + "' AND " + CommessaTM.R_COMMESSA_PRM + "='" + commessaPrm.getIdCommessa() + "'";
         if(commessaApp != null) {
            whereSC += " AND " + CommessaTM.R_COMMESSA_APP + "='" + commessaApp.getIdCommessa() + "'";
         }
         else {
            whereSC += " AND " + CommessaTM.R_COMMESSA_APP + " IS NULL";
         }
         List<Commessa> sottoCommesse = Commessa.retrieveList(whereSC, CommessaTM.ID_COMMESSA, false);
         if(sottoCommesse != null && !sottoCommesse.isEmpty())
            return sottoCommesse;
      } 
      catch (Exception e) {
         e.printStackTrace(Trace.excStream);
      } 
      return null;
   }

   public void writeCommessaGruppo(JSONArray data, String id, String parentId, Commessa commessa, List<BudgetCommessaDet> dettagli) throws JSONException {
      String stileRiga = "RigaNormale";
      JSONObject json = new JSONObject();
      json.put("id", id);
      json.put("parentId", parentId);

      json.put("Commessa", commessa.getDescrizione().getDescrizione());
      json.put("Sottocommessa", "");
      json.put("CompCosto", commessa.getIdCommessa() + " - " + commessa.getDescrizione().getDescrizione());

      Iterator<BudgetCommessaDet> iterDettagli = dettagli.iterator();
      while(iterDettagli.hasNext()) {
         BudgetCommessaDet dettaglio = (BudgetCommessaDet)iterDettagli.next();
         json.put("BudVal" + dettaglio.getIdBudget(),  getValoreString(dettaglio.getCostoTotale(), true));			
         if(commessa.getCommessaPrincipale().hasCompenenteATempo()) 
            json.put("BudHH" + dettaglio.getIdBudget(), getOreString(dettaglio.getTempoTotale(), dettaglio.getComponenteCosto().isGestioneATempo(), true));
      }

      if(id.equals("1"))
         stileRiga = "RigaCommessa";
      else
         stileRiga = "RigaSottoCommessa";

      json.put("pq_rowcls", stileRiga);

      data.put(json);
   }

   public List<BudgetCommessaDet> cercaBudgetCommessaDets(List<BudgetCommessa> budgets, boolean totale, String idComp) {
      List<BudgetCommessaDet> ret = new ArrayList<BudgetCommessaDet>();
      if(budgets != null && !budgets.isEmpty()) {
         Iterator<BudgetCommessa> iterBudget = budgets.iterator();
         while(iterBudget.hasNext()) {
            BudgetCommessa budget = (BudgetCommessa)iterBudget.next();
            BudgetCommessaDet dettaglio = null;
            if(totale) 
               dettaglio = budget.getDettaglio(budget, budget.getIdComponenteTotali(), false);
            else 
               dettaglio = budget.getDettaglio(budget, idComp, false);
            ret.add(dettaglio);
         }			
      }
      return ret;
   }
}

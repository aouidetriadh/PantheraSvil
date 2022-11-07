package it.thera.thip.produzione.commessa;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.BusinessObjectAdapter;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.Factory;
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
 * ScostamentoBudgetCommessa
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 29/03/2022 at 15:20:38
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 35837   29/03/2022    RA		    Prima struttura
 */
public class ScostamentoBudgetCommessa extends BusinessObjectAdapter implements Authorizable, Conflictable {

   public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.ScostamentoBudgetCommessa";

   public final static BigDecimal ZERO = new BigDecimal(0);

   protected java.sql.Date iDataRiferimento;
   protected java.sql.Date iDataBudget;
   protected java.sql.Date iDataConsuntivo;

   protected boolean iTotali = true;
   protected boolean iDettagliCommessa = true;
   protected boolean iDettagliSottoCommesse = true;
   protected boolean iComponentiPropri = false;
   protected boolean iSoloComponentiValorizzate = false;	

   protected Proxy iAzienda = new Proxy(it.thera.thip.base.azienda.Azienda.class);
   protected Proxy iCommessa = new Proxy(it.thera.thip.base.commessa.Commessa.class);
   protected Proxy iBudgetCommessa = new Proxy(it.thera.thip.produzione.commessa.BudgetCommessa.class);
   protected Proxy iConsuntivoCommessa = new Proxy(it.thera.thip.produzione.commessa.ConsuntivoCommessa.class);

   public ScostamentoBudgetCommessa() {
      setIdAzienda(Azienda.getAziendaCorrente());
   }

   public void setDataRiferimento(java.sql.Date dataRiferimento) {
      this.iDataRiferimento = dataRiferimento;
   }

   public java.sql.Date getDataRiferimento() {
      return iDataRiferimento;
   }

   public void setDataBudget(java.sql.Date dataBudget) {
      this.iDataBudget = dataBudget;
   }

   public java.sql.Date getDataBudget() {
      return iDataBudget;
   }

   public void setDataConsuntivo(java.sql.Date dataConsuntivo) {
      this.iDataConsuntivo = dataConsuntivo;
   }

   public java.sql.Date getDataConsuntivo() {
      return iDataConsuntivo;
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

   public void initializeAttDaScreenData() {
      ScreenData sd = ScreenData.getDefaultScreenData("ScostamentoBudgetCommessa");
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

   public void setBudgetCommessa(BudgetCommessa budgetCommessa) {
      String idAzienda = null;
      String idCommessa = null;
      if (budgetCommessa != null) {
         idAzienda = KeyHelper.getTokenObjectKey(budgetCommessa.getKey(), 1);
         idCommessa = KeyHelper.getTokenObjectKey(budgetCommessa.getKey(), 3);
      }
      setIdAziendaInternal(idAzienda);
      setIdCommessaInternal(idCommessa);
      this.iBudgetCommessa.setObject(budgetCommessa);
   }

   public BudgetCommessa getBudgetCommessa() {
      return (BudgetCommessa) iBudgetCommessa.getObject();
   }

   public void setBudgetCommessaKey(String key) {
      iBudgetCommessa.setKey(key);
      String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
      setIdAziendaInternal(idAzienda);
      String idCommessa = KeyHelper.getTokenObjectKey(key, 1);
      setIdCommessaInternal(idCommessa);
   }

   public String getBudgetCommessaKey() {
      return iBudgetCommessa.getKey();
   }

   public void setIdBudget(Integer idBudget) {
      String key = iBudgetCommessa.getKey();
      iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idBudget));
   }

   public Integer getIdBudget() {
      String key = iBudgetCommessa.getKey();
      String objIdBudget = KeyHelper.getTokenObjectKey(key, 2);
      return KeyHelper.stringToIntegerObj(objIdBudget);
   }

   public String getIdCommessaBudget() {
      String key = iBudgetCommessa.getKey();
      return KeyHelper.getTokenObjectKey(key, 3);
   }

   public void setIdCommessaBudget(String idCommessaBudget) {
      String key = iBudgetCommessa.getKey();
      iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idCommessaBudget));
   }

   public void setConsuntivoCommessa(ConsuntivoCommessa consuntivo) {
      String idAzienda = null;
      String idCommessa = null;
      if (consuntivo != null) {
         idAzienda = KeyHelper.getTokenObjectKey(consuntivo.getKey(), 1);
         idCommessa = KeyHelper.getTokenObjectKey(consuntivo.getKey(), 3);
      }
      setIdAziendaInternal(idAzienda);
      setIdCommessaInternal(idCommessa);
      this.iConsuntivoCommessa.setObject(consuntivo);
   }

   public ConsuntivoCommessa getConsuntivoCommessa() {
      return (ConsuntivoCommessa) iConsuntivoCommessa.getObject();
   }

   public void setConsuntivoCommessaKey(String key) {
      iConsuntivoCommessa.setKey(key);
      String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
      setIdAziendaInternal(idAzienda);
      String idCommessa = KeyHelper.getTokenObjectKey(key, 3);
      setIdCommessaInternal(idCommessa);
   }

   public String getConsuntivoCommessaKey() {
      return iConsuntivoCommessa.getKey();
   }

   public void setIdConsuntivo(Integer idConsuntivo) {
      String key = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idConsuntivo));
   }

   public Integer getIdConsuntivo() {
      String key = iConsuntivoCommessa.getKey();
      String objIdConsuntivo = KeyHelper.getTokenObjectKey(key, 2);
      return KeyHelper.stringToIntegerObj(objIdConsuntivo);
   }

   public String getIdCommessaConsuntivo() {
      String key = iConsuntivoCommessa.getKey();
      return KeyHelper.getTokenObjectKey(key, 3);
   }

   public void setIdCommessaConsuntivo(String idCommessaConsuntivo) {
      String key = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, idCommessaConsuntivo));
   }

   protected void setIdAziendaInternal(String idAzienda) {
      iAzienda.setKey(idAzienda);
      String key1 = iCommessa.getKey();
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
      String key2 = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
      String key3 = iBudgetCommessa.getKey();
      iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key3, 1, idAzienda));
   }

   protected void setIdCommessaInternal(String idCommessa) {
      String key1 = iCommessa.getKey();
      iCommessa.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, idCommessa));
      String key2 = iConsuntivoCommessa.getKey();
      iConsuntivoCommessa.setKey(KeyHelper.replaceTokenObjectKey(key2, 3, idCommessa));
      String key3 = iBudgetCommessa.getKey();
      iBudgetCommessa.setKey(KeyHelper.replaceTokenObjectKey(key3, 3, idCommessa));
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

   public ErrorMessage checkDataRiferimento() {
      if(getDataRiferimento() == null || getDataRiferimento().compareTo(TimeUtils.getCurrentDate()) > 0)
         return new ErrorMessage("THIP_TN724");
      return null;
   }

   public JSONArray getColumnsDescriptors() throws JSONException {
      JSONArray columns = new JSONArray();
      columns.put(getColumnDescriptors());
      columns.put(getColumnDescriptorsBudget());
      columns.put(getColumnDescriptorsConsuntivo());
      columns.put(getColumnDescriptorsScostamento());
      return columns;
   }

   public JSONObject getColumnDescriptors() throws JSONException {
      JSONObject column = new JSONObject();
      column.put("title", "").put("width", "320").put("halign", "center").put("align", "left").put("dataIndx", "CompCosto").put("nodrag", true).put("styleHead", new JSONObject().put("font-style", "bold")).put("editable", false);
      return column;		
   }

   public JSONObject getColumnDescriptorsBudget() throws JSONException {
      JSONObject column = new JSONObject();
      String title = "Budget";
      if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
         column.put("title", ResourceLoader.getString(RES_FILE, "Budget")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false));
         column.put("colModel", getColumnModelATempo("Budget"));				

      }
      else {
         String dataIndx = title.substring(0, 3);
         column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "260").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI).put("editable", false);			
      }	
      return column;
   }

   public JSONArray getColumnModelATempo(String title) throws JSONException {
      JSONArray columns = new JSONArray();
      String dataIndx = title.substring(0, 3);
      JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"HH").put("dataType", "float").put("format",ConsuntivoCommessa.FORMATO_ORE);
      columns.put(columnOre);
      JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", dataIndx+"Val").put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_VALORI);
      columns.put(columnValore);
      return columns;
   }

   public JSONObject getColumnDescriptorsConsuntivo() throws JSONException {
      JSONObject consuntivoColumnDescriptors = new JSONObject();
      consuntivoColumnDescriptors.put("title", ResourceLoader.getString(RES_FILE, "Consuntivo")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", true));
      consuntivoColumnDescriptors.put("colModel", getConsuntivoColumnModel());
      return consuntivoColumnDescriptors;		
   }

   public JSONArray getConsuntivoColumnModel() throws JSONException {		
      JSONArray columns = new JSONArray();
      ConsuntivoCommessa consuntivo = getConsuntivoCommessa();
      if(consuntivo == null)
         consuntivo = (ConsuntivoCommessa)Factory.createObject(ConsuntivoCommessa.class);
      if(consuntivo.isConsolidato())
         columns.put(consuntivo.getColumnDescriptors("Consolidato"));
      if(consuntivo.isEstrazioneRichieste())
         columns.put(consuntivo.getColumnDescriptors("Richiesto"));
      if(consuntivo.isEstrazioneOrdini())
         columns.put(consuntivo.getColumnDescriptors("Ordinato"));
      columns.put(consuntivo.getColumnDescriptors("Sostenuto"));
      columns.put(consuntivo.getColumnDescriptors("Totale"));
      return columns;
   }

   public JSONArray getColumnModelScostamento(String title) throws JSONException {
      JSONArray columns = new JSONArray();
      String dataIndx = title.substring(0, 3);

      JSONObject column = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, title)).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", "Scos"+dataIndx).put("dataType", "float");
      if(title.equals("Ore")) {
         column.put("format", ConsuntivoCommessa.FORMATO_ORE);
      }
      else {
         column.put("format", ConsuntivoCommessa.FORMATO_VALORI);
      }
      columns.put(column);

      JSONObject columnPerc = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Percentuale")).put("width", "130").put("halign", "center").put("align", "right").put("dataIndx", "Perc" + dataIndx).put("dataType", "float").put("format", ConsuntivoCommessa.FORMATO_PERC);
      columns.put(columnPerc);
      return columns;
   }

   public JSONArray getColumnModelScostamento() throws JSONException {	
      JSONArray columns = new JSONArray();
      if(getCommessa() != null && getCommessa().hasCompenenteATempo()) {
         JSONObject columnOre = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Ore")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false));
         columnOre.put("colModel", getColumnModelScostamento("Ore"));
         columns.put(columnOre);
      }
      JSONObject columnValore = new JSONObject().put("title", ResourceLoader.getString(RES_FILE, "Valore")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false));
      columnValore.put("colModel", getColumnModelScostamento("Valore"));
      columns.put(columnValore);		
      return columns;
   }

   public JSONObject getColumnDescriptorsScostamento() throws JSONException {
      JSONObject column = new JSONObject();
      column.put("title", ResourceLoader.getString(RES_FILE, "Scostamento")).put("width", "260").put("halign", "center").put("align", "right").put("collapsible", new JSONObject().put("last", true).put("on", false));
      column.put("colModel", getColumnModelScostamento());
      return column;
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

   protected String getScostValoreString(BigDecimal budgetVal, BigDecimal consVal,  boolean perc, boolean vuoto)
   {
      BigDecimal scostValore = sub(budgetVal, consVal);
      if(perc)
         scostValore = getScostamentoPercentuale(budgetVal, scostValore);
      return getValoreString(scostValore, vuoto);
   }

   protected String getScostOreString(BigDecimal budgetOre, BigDecimal consOre, boolean aTempo, boolean perc, boolean vuoto)
   {
      BigDecimal scostOre = sub(budgetOre, consOre);
      if(perc)
         scostOre = getScostamentoPercentuale(budgetOre, scostOre);
      return getOreString(scostOre, aTempo, vuoto);
   }

   public BigDecimal sub(BigDecimal val1, BigDecimal val2) 
   {
      if((val1 == null) && (val2 == null))
         return null;
      if(val1 == null)
         return ZERO.subtract(val2);
      if(val2 == null)
         return val1;
      return val1.subtract(val2);
   }

   public BigDecimal getScostamentoPercentuale(BigDecimal valBudget, BigDecimal valScos) {
      if(valBudget == null || valBudget.compareTo(ZERO) == 0) {
         return ZERO;
      }
      else if(valScos == null || valScos.compareTo(ZERO) == 0) {
         return ZERO;// new BigDecimal(100);
      }

      return valScos.divide(valBudget, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
   }

   public JSONArray getDataModel() {
      JSONArray data = new JSONArray();
      Commessa commessaPrm = getCommessa();
      if(commessaPrm != null) {
         try {

            BudgetCommessa budgetCom = getBudgetCommessa();
            if(budgetCom != null)
               budgetCom.caricaAlberoBudget();

            ConsuntivoCommessa conuntivoCom = getConsuntivoCommessa();
            if(conuntivoCom != null)
               conuntivoCom.caricaAlberoConsuntivi();

            if(budgetCom != null)
               writeJSONDati(data, "1", null, budgetCom, conuntivoCom, commessaPrm, null);
         } catch (JSONException e) {
            e.printStackTrace(Trace.excStream);
         }
      }		
      return data;
   }

   @SuppressWarnings("unchecked")
   protected void writeJSONDati(JSONArray data, String id, String idParent, BudgetCommessa currentBudget, ConsuntivoCommessa currentConsuntivo, Commessa commessaPrm, Commessa commessaApp) throws JSONException {
      String parent = "";
      Commessa commessa = commessaPrm;
      if(commessaApp != null) {
         commessa = commessaApp;
      }

      if(idParent != null) {			
         parent = String.valueOf(idParent);
      }

      ConsuntivoCommessaDet consuntivoDetTotale = null;
      String idCompTotale = currentBudget.getIdComponenteTotali();
      BudgetCommessaDet budgetDetTotale = currentBudget.cercaBudgetCommessaDetTotali(currentBudget, commessa.getIdCommessa(), idCompTotale);
      if(currentConsuntivo != null) {
         consuntivoDetTotale = currentConsuntivo.cercaConsuntivoCommessaDetTotali(currentConsuntivo, commessa.getIdCommessa(), idCompTotale);
      }	
      int idxGrup = 1;
      writeCommessaGruppo(data, id, parent, commessa.getDescrizione().getDescrizione(), "", commessa.getIdCommessa() + " - " + commessa.getDescrizione().getDescrizione(), commessaPrm.hasCompenenteATempo(), budgetDetTotale.getComponenteCosto(), budgetDetTotale, consuntivoDetTotale);

      if(isTotali()) {
         String idGruppTot = id + idxGrup;
         writeTotale(data, idGruppTot, id, commessaPrm.hasCompenenteATempo(),false, budgetDetTotale, consuntivoDetTotale);
         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
            writeTotaleDettagli(data, id, idGruppTot, currentBudget.getBudgetCommessaDet(), currentConsuntivo.getConsuntivoCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate());
      }

      if(isComponentiPropri()) {
         idxGrup++;
         String idGruppCmp = id + idxGrup;
         writeComponentePropri(data, idGruppCmp, id, commessaPrm.hasCompenenteATempo(), false,budgetDetTotale, consuntivoDetTotale);
         if((idParent == null && isDettagliCommessa()) || (idParent != null && isDettagliSottoCommesse()))
            writeComponentePropriDettagli(data, id, idGruppCmp, currentBudget.getBudgetCommessaDet(), currentConsuntivo.getConsuntivoCommessaDet(), commessa, commessaPrm.hasCompenenteATempo(), isSoloComponentiValorizzate());
      }

      Iterator budIter = currentBudget.getBudgetLivelliInferiori().iterator();
      while(budIter.hasNext())
      {
         BudgetCommessa sottoBudget = (BudgetCommessa)budIter.next();

         ConsuntivoCommessa sottoCons = currentConsuntivo.getConsuntivoLivelloInferiore(sottoBudget.getCommessa());

         idxGrup++;
         String idSottoCmm = id + idxGrup;
         writeJSONDati(data, idSottoCmm, id, sottoBudget, sottoCons, commessaPrm, sottoBudget.getCommessa());
      }
   }

   public void writeTotaleDettagli(JSONArray data, String id, String idParent, List<BudgetCommessaDet> budgetsCommessaDet, List<ConsuntivoCommessaDet> consCommessaDet, Commessa commessa, boolean isATempo, boolean soloComponentiValorizzate) {
      int idxDet = 1;
      for(int i = 0; i < budgetsCommessaDet.size(); i++) {
         BudgetCommessaDet dettaglio = budgetsCommessaDet.get(i);

         ConsuntivoCommessaDet consuntivoDet = null;
         for(int j = 0; j < consCommessaDet.size() && consuntivoDet == null; j++) {
            ConsuntivoCommessaDet consDet = consCommessaDet.get(j);
            if(consDet.getIdComponCosto().equals(dettaglio.getIdComponCosto()))
               consuntivoDet = consDet;
         }


         if((!soloComponentiValorizzate) || (soloComponentiValorizzate && dettaglio.getCostoTotale() != null && dettaglio.getCostoTotale().compareTo(new BigDecimal(0)) > 0)) {
            String idDet = idParent + idxDet;
            writeTotale(data, idDet, idParent, isATempo, true, dettaglio, consuntivoDet);
            idxDet ++;
         }
      }
   }

   public void writeTotale(JSONArray data, String id, String idParent, boolean isCmpAtempo, boolean isDettaglio, BudgetCommessaDet budgetDet, ConsuntivoCommessaDet consDet) {
      try {

         String stileRiga = "RigaNormale";
         boolean cmpTotali = false;
         boolean totali = false;
         boolean cmpATempo = budgetDet.getComponenteCosto().isGestioneATempo();

         JSONObject json = new JSONObject();
         json.put("id", id);
         json.put("parentId", idParent);
         if(isDettaglio) {
            json.put("CompCosto", budgetDet.getIdComponCosto() + " - " + budgetDet.getComponenteCosto().getDescrizione().getDescrizione());
            if(budgetDet.getComponenteCosto() != null && budgetDet.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
               cmpTotali = true;
            totali = true;
         }
         else {
            stileRiga = "RigaTotali";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "Totali"));
         }

         BigDecimal costCons = null;
         if(consDet != null)
            costCons = consDet.getTotale().getCostoTotale();
         BigDecimal scostValore = sub(budgetDet.getCostoTotale(), costCons);

         json.put("BudVal", getValoreString(budgetDet.getCostoTotale(), true));	
         json.put("ScosVal", getScostValoreString(budgetDet.getCostoTotale(), costCons, false, true));
         json.put("PercVal", getScostValoreString(budgetDet.getCostoTotale(), costCons, true, true));


         BigDecimal oreCons = null;
         if(consDet != null)
            oreCons = consDet.getTotale().getTempoTotale();
         BigDecimal scostOre = sub(budgetDet.getTempoTotale(), oreCons);

         if(isCmpAtempo) 
         {
            json.put("BudHH", getOreString(budgetDet.getTempoTotale(), cmpATempo, true));
            json.put("ScosOre", getScostOreString(budgetDet.getTempoTotale(), oreCons, cmpATempo, false, true));
            json.put("PercOre", getScostOreString(budgetDet.getTempoTotale(), oreCons, cmpATempo, true, true));

         }


         if(consDet != null) {
            if (consDet.getConsuntivoCommessa().isConsolidato()) 
               json.put("ConVal",  getValoreString(consDet.getConsolidato().getCostoTotale(), false));
            json.put("RicVal", getValoreString(consDet.getRichiesto().getCostoTotale(), false));
            json.put("OrdVal", getValoreString(consDet.getOrdinato().getCostoTotale(), false));
            json.put("SosVal", getValoreString(consDet.getEffettuato().getCostoTotale(), false));
            json.put("TotVal", getValoreString(consDet.getTotale().getCostoTotale(), false));
            if (isCmpAtempo) {
               if (consDet.getConsuntivoCommessa().isConsolidato()) {
                  json.put("ConHH", getOreString(consDet.getConsolidato().getTempoTotale(), cmpATempo, false));
               }
               json.put("RicHH", getOreString(consDet.getRichiesto().getTempoTotale(), cmpATempo, false)); 
               json.put("OrdHH", getOreString(consDet.getOrdinato().getTempoTotale(), cmpATempo, false)); 
               json.put("SosHH", getOreString(consDet.getEffettuato().getTempoTotale(), cmpATempo, false)); 
               json.put("TotHH", getOreString(consDet.getTotale().getTempoTotale(), cmpATempo, false));
            }
         }

         json.put("pq_rowcls", stileRiga);

         JSONObject stiliCelle = new JSONObject();
         if(totali)
         {
            stiliCelle.put("TotVal", "CellaTotali");
            stiliCelle.put("TotHH", "CellaTotali");
         }
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");

         if(scostValore != null)
         {
            if(scostValore.compareTo(ZERO) < 0)
            {
               stiliCelle.put("ScosVal", "CellaDeltaNeg");
               stiliCelle.put("PercVal", "CellaDeltaNeg");
            }
            else if(scostValore.compareTo(ZERO) > 0)
            {
               stiliCelle.put("ScosVal", "CellaDeltaPos");
               stiliCelle.put("PercVal", "CellaDeltaPos");
            }
         }
         if(scostOre != null)
         {
            if(scostOre.compareTo(ZERO) < 0)
            {
               stiliCelle.put("ScosOre", "CellaDeltaNeg");
               stiliCelle.put("PercOre", "CellaDeltaNeg");
            }
            else if(scostOre.compareTo(ZERO) > 0)
            {
               stiliCelle.put("ScosOre", "CellaDeltaPos");
               stiliCelle.put("PercOre", "CellaDeltaPos");
            }
         }
         json.put("pq_cellcls", stiliCelle);
         data.put(json);
      }
      catch (JSONException e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   public void writeComponentePropriDettagli(JSONArray data, String id, String idParent, List<BudgetCommessaDet> budgetsCommessaDet, List<ConsuntivoCommessaDet> consCommessaDet, Commessa commessa, boolean isATempo, boolean soloComponentiValorizzate) {

      int idxDet = 1;
      for(int i = 0; i < budgetsCommessaDet.size(); i++) {
         BudgetCommessaDet dettaglio = budgetsCommessaDet.get(i);

         ConsuntivoCommessaDet consuntivoDet = null;
         for(int j = 0; j < consCommessaDet.size() && consuntivoDet == null; j++) {
            ConsuntivoCommessaDet consDet = consCommessaDet.get(j);
            if(consDet.getIdComponCosto().equals(dettaglio.getIdComponCosto()))
               consuntivoDet = consDet;
         }


         if((!soloComponentiValorizzate) || (soloComponentiValorizzate && dettaglio.getCostoTotale() != null && dettaglio.getCostoTotale().compareTo(new BigDecimal(0)) > 0)) {
            String idDet = idParent + idxDet;
            writeComponentePropri(data, idDet, idParent, isATempo, true, dettaglio, consuntivoDet);
            idxDet ++;
         }
      }
   }

   public void writeComponentePropri(JSONArray data, String id, String idParent, boolean isATempo, boolean isDettaglio, BudgetCommessaDet budgetDet, ConsuntivoCommessaDet consDet) {
      try {

         String stileRiga = "RigaNormale";
         boolean cmpTotali = false;
         boolean totali = false;
         boolean cmpATempo = budgetDet.getComponenteCosto().isGestioneATempo();

         JSONObject json = new JSONObject();
         json.put("id", id);
         json.put("parentId", idParent);
         if(isDettaglio) {

            json.put("CompCosto", budgetDet.getIdComponCosto() + " - " + budgetDet.getComponenteCosto().getDescrizione().getDescrizione());
            if(budgetDet.getComponenteCosto() != null && budgetDet.getComponenteCosto().getProvenienza() != ComponenteCosto.ELEMENTARI)
               cmpTotali = true;
            totali = true;
         }
         else {
            stileRiga = "RigaTotali";
            json.put("CompCosto", ResourceLoader.getString(RES_FILE, "ComponentiProprie"));
         }

         BigDecimal costCons = null;
         if(consDet != null)
            costCons = consDet.getTotale().getCostoLivello();
         BigDecimal scostValore = sub(budgetDet.getCostoLivello(), costCons);

         json.put("BudVal", getValoreString(budgetDet.getCostoLivello(), true));  
         json.put("ScosVal", getScostValoreString(budgetDet.getCostoLivello(), costCons, false, true));
         json.put("PercVal", getScostValoreString(budgetDet.getCostoLivello(), costCons, true, true));


         BigDecimal oreCons = null;
         if(consDet != null)
            oreCons = consDet.getTotale().getTempoLivello();
         BigDecimal scostOre = sub(budgetDet.getTempoLivello(), oreCons);

         if(isATempo) 
         {
            json.put("BudHH", getOreString(budgetDet.getTempoLivello(), cmpATempo, true));
            json.put("ScosOre", getScostOreString(budgetDet.getTempoLivello(), oreCons, cmpATempo, false, true));
            json.put("PercOre", getScostOreString(budgetDet.getTempoLivello(), oreCons, cmpATempo, true, true));
         }


         if(consDet != null) {
            if (consDet.getConsuntivoCommessa().isConsolidato()) 
               json.put("ConVal",  getValoreString(consDet.getConsolidato().getCostoLivello(), false));
            json.put("RicVal", getValoreString(consDet.getRichiesto().getCostoLivello(), false));
            json.put("OrdVal", getValoreString(consDet.getOrdinato().getCostoLivello(), false));
            json.put("SosVal", getValoreString(consDet.getEffettuato().getCostoLivello(), false));
            json.put("TotVal", getValoreString(consDet.getTotale().getCostoLivello(), false));
            if (isATempo) {
               if (consDet.getConsuntivoCommessa().isConsolidato()) {
                  json.put("ConHH", getOreString(consDet.getConsolidato().getTempoLivello(), cmpATempo, false));
               }
               json.put("RicHH", getOreString(consDet.getRichiesto().getTempoLivello(), cmpATempo, false)); 
               json.put("OrdHH", getOreString(consDet.getOrdinato().getTempoLivello(), cmpATempo, false)); 
               json.put("SosHH", getOreString(consDet.getEffettuato().getTempoLivello(), cmpATempo, false)); 
               json.put("TotHH", getOreString(consDet.getTotale().getTempoLivello(), cmpATempo, false));
            }
         }

         json.put("pq_rowcls", stileRiga);

         JSONObject stiliCelle = new JSONObject();
         if(totali)
         {
            stiliCelle.put("TotVal", "CellaTotali");
            stiliCelle.put("TotHH", "CellaTotali");
         }
         if(cmpTotali)
            stiliCelle.put("CompCosto", "CellaCompTotali");

         if(scostValore != null)
         {
            if(scostValore.compareTo(ZERO) < 0)
            {
               stiliCelle.put("ScosVal", "CellaDeltaNeg");
               stiliCelle.put("PercVal", "CellaDeltaNeg");
            }
            else if(scostValore.compareTo(ZERO) > 0)
            {
               stiliCelle.put("ScosVal", "CellaDeltaPos");
               stiliCelle.put("PercVal", "CellaDeltaPos");
            }
         }
         if(scostOre != null)
         {
            if(scostOre.compareTo(ZERO) < 0)
            {
               stiliCelle.put("ScosOre", "CellaDeltaNeg");
               stiliCelle.put("PercOre", "CellaDeltaNeg");
            }
            else if(scostOre.compareTo(ZERO) > 0)
            {
               stiliCelle.put("ScosOre", "CellaDeltaPos");
               stiliCelle.put("PercOre", "CellaDeltaPos");
            }
         }
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

   public void writeCommessaGruppo(JSONArray data, String id, String parentId, String descCommessa, String descSottoCommessa,
         String compCosto, boolean isATempo, ComponenteCosto cmpCosto, BudgetCommessaDet budgetDet, ConsuntivoCommessaDet consDet) throws JSONException {

      String stileRiga = "RigaNormale";
      boolean cmpATempo = budgetDet.getComponenteCosto().isGestioneATempo();

      JSONObject json = new JSONObject();
      json.put("id", id);
      json.put("parentId", parentId);

      json.put("Commessa", descCommessa);
      json.put("Sottocommessa", descSottoCommessa);
      json.put("CompCosto", compCosto);
      json.put("IdCmpCosto", cmpCosto.getIdComponenteCosto());

      BigDecimal costCons = null;
      if(consDet != null)
         costCons = consDet.getTotale().getCostoTotale();
      BigDecimal scostValore = sub(budgetDet.getCostoTotale(), costCons);

      json.put("BudVal", getValoreString(budgetDet.getCostoTotale(), true));  
      json.put("ScosVal", getScostValoreString(budgetDet.getCostoTotale(), costCons, false, true));
      json.put("PercVal", getScostValoreString(budgetDet.getCostoTotale(), costCons, true, true));


      BigDecimal oreCons = null;
      if(consDet != null)
         oreCons = consDet.getTotale().getTempoTotale();
      BigDecimal scostOre = sub(budgetDet.getTempoTotale(), oreCons);

      if(isATempo) 
      {
         json.put("BudHH", getOreString(budgetDet.getTempoTotale(), cmpATempo, true));
         json.put("ScosOre", getScostOreString(budgetDet.getTempoTotale(), oreCons, cmpATempo, false, true));
         json.put("PercOre", getScostOreString(budgetDet.getTempoTotale(), oreCons, cmpATempo, true, true));

      }


      if(consDet != null) {
         if (consDet.getConsuntivoCommessa().isConsolidato()) 
            json.put("ConVal",  getValoreString(consDet.getConsolidato().getCostoTotale(), false));
         json.put("RicVal", getValoreString(consDet.getRichiesto().getCostoTotale(), false));
         json.put("OrdVal", getValoreString(consDet.getOrdinato().getCostoTotale(), false));
         json.put("SosVal", getValoreString(consDet.getEffettuato().getCostoTotale(), false));
         json.put("TotVal", getValoreString(consDet.getTotale().getCostoTotale(), false));
         if (isATempo) {
            if (consDet.getConsuntivoCommessa().isConsolidato()) {
               json.put("ConHH", getOreString(consDet.getConsolidato().getTempoTotale(), cmpATempo, false));
            }
            json.put("RicHH", getOreString(consDet.getRichiesto().getTempoTotale(), cmpATempo, false)); 
            json.put("OrdHH", getOreString(consDet.getOrdinato().getTempoTotale(), cmpATempo, false)); 
            json.put("SosHH", getOreString(consDet.getEffettuato().getTempoTotale(), cmpATempo, false)); 
            json.put("TotHH", getOreString(consDet.getTotale().getTempoTotale(), cmpATempo, false));
         }


      }

      if(id.equals("1"))
         stileRiga = "RigaCommessa";
      else
         stileRiga = "RigaSottoCommessa";
      json.put("pq_rowcls", stileRiga);

      JSONObject stiliCelle = new JSONObject();

      if(scostValore != null)
      {
         if(scostValore.compareTo(ZERO) < 0)
         {
            stiliCelle.put("ScosVal", "CellaDeltaNeg");
            stiliCelle.put("PercVal", "CellaDeltaNeg");
         }
         else if(scostValore.compareTo(ZERO) > 0)
         {
            stiliCelle.put("ScosVal", "CellaDeltaPos");
            stiliCelle.put("PercVal", "CellaDeltaPos");
         }
      }
      if(scostOre != null)
      {
         if(scostOre.compareTo(ZERO) < 0)
         {
            stiliCelle.put("ScosOre", "CellaDeltaNeg");
            stiliCelle.put("PercOre", "CellaDeltaNeg");
         }
         else if(scostOre.compareTo(ZERO) > 0)
         {
            stiliCelle.put("ScosOre", "CellaDeltaPos");
            stiliCelle.put("PercOre", "CellaDeltaPos");
         }
      }
      json.put("pq_cellcls", stiliCelle);

      data.put(json);
   }

   public BudgetCommessaDet cercaBudgetCommessaDet(Integer idBudget, Commessa sottoCom, String idComp) {
      BudgetCommessa budgetRicerca = getBudgetCommessa(); 
      if(!getIdCommessa().equals(sottoCom.getIdCommessa()))
         budgetRicerca = getBudgetCommessa().getBudgetLivelloInferiore(sottoCom);
      BudgetCommessaDet ret = getBudgetCommessa().getDettaglio(budgetRicerca, idComp, false);
      return ret;
   }

   public ConsuntivoCommessaDet cercaConsuntivoCommessaDet(Integer idConsuntivo, String idCommessa, String idComp) {
      ConsuntivoCommessaDet ret = getConsuntivoCommessa().getDettaglio(getConsuntivoCommessa(), idComp, false);
      return ret;
   }


}

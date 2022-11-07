package it.thera.thip.datiTecnici.costi;

import com.thera.thermfw.persist.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.security.*;

import java.math.BigDecimal;

import com.thera.thermfw.base.SystemParam;

import it.thera.thip.base.articolo.TipoCosto;
import it.thera.thip.base.risorse.Risorsa;
import it.thera.thip.cs.*;

/*
 * Revision:
 * Number     Date        Owner      Description
 * 33950	  19/07/2021	RA		 Aggiunto nuovi attributi
 */
public abstract class ComponenteCostoPO extends EntitaSingola implements BusinessObject, Authorizable, Deletable, ConflictableWithKey
{
//  instance
  private static ComponenteCosto cInstance;

// Constant TipoComponente
  public static final char DIRETTA='D';
  public static final char INDIRETTA_PRODUZIONE='P';
  public static final char INDIRETTA_GENERALE='G';
// Constant Provenienza
  public static final char ELEMENTARI='E';
  public static final char CALCOLATA_FORMULA='F';
  public static final char SOLO_TOTALE='T';
// Constant Criticita
  public static final char TRASCURABILE='T';
  public static final char ANOMALIA_NON_BLOCCANTE='N';
  public static final char ANOMALIA_BLOCCANTE='B';

// simple Attribut
  protected String iIdComponenteCosto;
  protected char iTipoComponente=DIRETTA;
  protected char iProvenienza= ELEMENTARI;
  protected char iCriticita  = TRASCURABILE;
  
  //33950 inizio
  protected boolean iGestioneATempo = false;
  protected BigDecimal iCostoUnitario;
  protected Proxy iTipoCosto = new Proxy(it.thera.thip.base.articolo.TipoCosto.class);
  protected Proxy iRisorsa = new Proxy(Risorsa.class);
  //33950 fine
  
// Proxies
  protected Proxy iFormula = new Proxy(FormulaCosti.class);

// Collections
  protected ManyToMany iTipiCosto = new ManyToMany(SystemParam.getSchema("THIP11")+"TPCOS_CMPCOS","",it.thera.thip.base.articolo.TipoCosto.class,this,3,5,3);

// constructor
  public ComponenteCostoPO()
  {
    super(3);
  }

// redifined methods
  public static java.util.Vector retrieveList(String where, String orderBy, boolean optimistic) throws java.sql.SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    if (cInstance == null)
          cInstance = (ComponenteCosto)Factory.createObject(ComponenteCosto.class);
      return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  public static ComponenteCosto elementWithKey(String key, int lockType) throws java.sql.SQLException
  {
    return (ComponenteCosto) PersistentObject.elementWithKey(ComponenteCosto.class, key, lockType);
  }

  public java.util.Vector checkAll(BaseComponentsCollection components)
  {
    java.util.Vector errors = new java.util.Vector();
    components.runAllChecks(errors);
    return errors;
  }

  public void setKey(String key)
  {
    String objIdAzienda = KeyHelper.getTokenObjectKey(key,1);
    setIdAzienda(objIdAzienda);
    String objIdComponenteCosto = KeyHelper.getTokenObjectKey(key,2);
    setIdComponenteCosto(objIdComponenteCosto);
  }

  public String getKey()
  {
    String idAzienda = getIdAzienda();
    String idComponenteCosto = getIdComponenteCosto();
    Object[] keyParts = {idAzienda, idComponenteCosto};
    return KeyHelper.buildObjectKey(keyParts);
  }

  public boolean isDeletable()
  {
    return checkDelete() == null;
  }

  protected TableManager getTableManager() throws java.sql.SQLException
  {
    return ComponenteCostoTM.getInstance();
  }

  public void setEqual(Copyable obj) throws CopyException
  {
    super.setEqual(obj);
    ComponenteCostoPO componenteCostoPO = (ComponenteCostoPO)obj;
    iFormula.setEqual(componenteCostoPO.iFormula);
    iTipiCosto.setEqual(componenteCostoPO.iTipiCosto);
    iTipoCosto.setEqual(componenteCostoPO.iTipoCosto);//33950
    iRisorsa.setEqual(componenteCostoPO.iRisorsa);//33950
  }

  public void setIdAzienda(String idAzienda)
  {
    super.setIdAzienda(idAzienda);
    iFormula.setKey(KeyHelper.replaceTokenObjectKey(iFormula.getKey(),1,idAzienda));
    iTipoCosto.setKey(KeyHelper.replaceTokenObjectKey(iTipoCosto.getKey(),1,idAzienda)); //33950  
    iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(iRisorsa.getKey(), 1, idAzienda)); //33950
  }

// set/get methods
  public void setIdComponenteCosto(String idComponenteCosto)
  {
    this.iIdComponenteCosto = idComponenteCosto;
    iDescrizione.getHandler().setFatherKeyChanged();
    setDirty();
    setOnDB(false);
  }
  public String getIdComponenteCosto()
  {
    return iIdComponenteCosto;
  }
  public void setTipoComponente(char tipoComponente)
  {
    iTipoComponente=tipoComponente;
    setDirty();
  }
  public char getTipoComponente()
  {
    return iTipoComponente;
  }
  public void setProvenienza(char provenienza)
  {
    iProvenienza=provenienza;
    setDirty();
  }
  public char getProvenienza()
  {
    return iProvenienza;
  }
  public void setCriticita(char criticita)
  {
    iCriticita=criticita;
    setDirty();
  }
  public char getCriticita()
  {
    return iCriticita;
  }

// Proxies methods
  public FormulaCosti getFormula()
  {
    return (FormulaCosti)iFormula.getObject();
  }
  public void setFormula(FormulaCosti formula)
  {
    iFormula.setObject(formula);
  }
  public String getFormulaKey()
  {
    return iFormula.getKey();
  }
  public void setFormulaKey(String key)
  {
    iFormula.setKey(key);
  }
  public String getIdFormula()
  {
    if (iFormula.getKey()!=null)
      return KeyHelper.getTokenObjectKey(iFormula.getKey(),2);
    return null;
  }
  public void setIdFormula(String idFormula)
  {
    if (iFormula.getKey()!=null)
      iFormula.setKey(KeyHelper.replaceTokenObjectKey(iFormula.getKey(),2,idFormula));
    else
      iFormula.setKey(KeyHelper.buildObjectKey(new String[]{getIdAzienda(),idFormula}));
  }
  
  //33950 inizio
  public void setGestioneATempo(boolean gestioneATempo) {
    iGestioneATempo = gestioneATempo;
    setDirty();
  }
  
  public boolean isGestioneATempo() {
    return iGestioneATempo;
  } 
  
  public void setCostoUnitario(BigDecimal costoUnitario) {
	  iCostoUnitario = costoUnitario;
	  setDirty();
  }
	  
  public BigDecimal getCostoUnitario() {
	  return iCostoUnitario;
  } 
  
  public TipoCosto getTipoCosto() {
	  return (TipoCosto)iTipoCosto.getObject();
  }
  
  public void setTipoCosto(TipoCosto tipoCosto) {
	  iTipoCosto.setObject(tipoCosto);
  }
  
  public String getTipoCostoKey() {
	  return iTipoCosto.getKey();
  }
  
  public void setTipoCostoKey(String key) {
	  iTipoCosto.setKey(key);
  }
  
  public String getIdTipoCosto() {
	  if (iTipoCosto.getKey()!=null)
		  return KeyHelper.getTokenObjectKey(iTipoCosto.getKey(),2);
	  return null;
  }
  
  public void setIdTipoCosto(String idTipoCosto) {
	  if (iTipoCosto.getKey()!=null)
		  iTipoCosto.setKey(KeyHelper.replaceTokenObjectKey(iTipoCosto.getKey(),2,idTipoCosto));
	  else
		  iTipoCosto.setKey(KeyHelper.buildObjectKey(new String[]{getIdAzienda(),idTipoCosto}));
  }

  public void setRisorsa(Risorsa risorsa) {
	  String oldObjectKey = getKey();
	  String idAzienda = null;
	  if (risorsa != null) {
		  idAzienda = KeyHelper.getTokenObjectKey(risorsa.getKey(), 1);
	  }
	  setIdAzienda(idAzienda);
	  String tipoRisorsa = null;
	  if (risorsa != null) {
		  tipoRisorsa = KeyHelper.getTokenObjectKey(risorsa.getKey(), 2);
	  }
	  setTipoRisorsaInternal(tipoRisorsa);
	  String idRisorsa = null;
	  if (risorsa != null) {
		  idRisorsa = KeyHelper.getTokenObjectKey(risorsa.getKey(), 4);
	  }
	  setIdRisorsaInternal(idRisorsa);
	  this.iRisorsa.setObject(risorsa);
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
	  }
  }

  public Risorsa getRisorsa() {
	  return (Risorsa) iRisorsa.getObject();
  }

  public void setRisorsaKey(String key) {
	  String oldObjectKey = getKey();
	  iRisorsa.setKey(key);
	  String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
	  setIdAzienda(idAzienda);
	  String tipoRisorsa = KeyHelper.getTokenObjectKey(key, 2);
	  setTipoRisorsaInternal(tipoRisorsa);
	  String idRisorsa = KeyHelper.getTokenObjectKey(key, 4);
	  setIdRisorsaInternal(idRisorsa);
	  setDirty();
	  if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
		  setOnDB(false);
	  }
  }

  public String getRisorsaKey() {
	  return iRisorsa.getKey();
  }

  public void setLivelloRisorsa(char livelloRisorsa) {
	  String key = iRisorsa.getKey();
	  Character livelloRisorsTmp = new Character(livelloRisorsa);
	  iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(key, 3, livelloRisorsTmp));
	  setDirty();
  }

  public char getLivelloRisorsa() {
	  String key = iRisorsa.getKey();
	  String objLivelloRisorsa = KeyHelper.getTokenObjectKey(key, 3);
	  return KeyHelper.stringToChar(objLivelloRisorsa);
  }

  public void setTipoRisorsa(char tipoRisorsa) {
	  setTipoRisorsaInternal("" + tipoRisorsa);
	  setDirty();
  }

  public char getTipoRisorsa() {
	  String key = iRisorsa.getKey();
	  String objTipoRisorsa = KeyHelper.getTokenObjectKey(key, 2);
	  return KeyHelper.stringToChar(objTipoRisorsa);
  }

  public void setIdRisorsa(String idRisorsa) {
	  setIdRisorsaInternal(idRisorsa);
	  setDirty();
  }

  public String getIdRisorsa() {
	  String key = iRisorsa.getKey();
	  String objRRisorsa = KeyHelper.getTokenObjectKey(key, 4);
	  return objRRisorsa;
  }

  protected void setTipoRisorsaInternal(String tipoRisorsa) {
	  String key1 = iRisorsa.getKey();
	  iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(key1, 2, tipoRisorsa));
  }

  protected void setIdRisorsaInternal(String IdRisorsa) {
	  String key1 = iRisorsa.getKey();
	  iRisorsa.setKey(KeyHelper.replaceTokenObjectKey(key1, 4, IdRisorsa));
  }
  //33950 fine

// Collections Methods
  public java.util.List getTipiCosto()
  {
    return getTipiCostoInternal();
  }
  public java.util.List getTipiCostoAvailable()
  {
    String where=it.thera.thip.base.articolo.TipoCostoTM.ID_AZIENDA+"='"+getIdAzienda()+"'";
    java.util.List result=null;
    try
    {
      result=new java.util.ArrayList(it.thera.thip.base.articolo.TipoCosto.retrieveList(where,"",false));
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return new java.util.ArrayList();
    }
//    System.out.println("result = "+result);
    return result;
  }

  protected ManyToMany getTipiCostoInternal()
  {
    if (iTipiCosto.isNew())
        iTipiCosto.retrieve();
    return iTipiCosto;
  }

  public java.util.Vector getTipiCostoKeys()
  {
    return getTipiCostoInternal().getKeys();
  }
  public void setTipiCostoKeys(java.util.Vector keys)
  {
   getTipiCostoInternal().setKeys(keys);
   getTipiCostoInternal().syncOnKeys();
  }
  public int saveOwnedObjects(int rc) throws java.sql.SQLException
  {
    int rc1=super.saveOwnedObjects(rc);
    if (rc1<0)
      return rc1;
    int rc2=iTipiCosto.save(rc1);
    return rc2;
  }
  public int deleteOwnedObjects() throws java.sql.SQLException
  {
    int rc=iTipiCosto.delete();
    if (rc<0)
      return rc;
    return super.deleteOwnedObjects();
  }
  public boolean initializeOwnedObjects(boolean ret)
  {
    return super.initializeOwnedObjects(iTipiCosto.initialize(ret));
  }
}
package it.thera.thip.datiTecnici.costi;

import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.formula.*;
import com.thera.thermfw.persist.*;

/*
 * Revision:
 * Number     Date        Owner      Description
 * 02234      09/07/2004  MBH        Fix of AddComponenteToVariabiliCosti method,
 *                                   assign  the type of the new variabile
 *
 * 04080      16/12/2005   AB        Added method getIdAndDescr to return description
 *                                   composed by idComponenteCosto + " - " + Descrizione.descrizione
 * 05298      10/04/2006   DBot      Implementata interfaccia Cacheable
 * 06381      07/12/2006   GN        Corretto loop nel metodo checkComponenteRicorsiva e moificato il ritorno del
 *                                   metodo getIdAndDescr perchè più lungo dell'attributeRef associato
 * 10565      12/03/2009   LP        Aggiunto pulizia cache elenco VariabiliCosti
 * 33950      19/07/2021   RA		 Aggiunt checkCommessa
 */

public class ComponenteCosto extends ComponenteCostoPO implements Cacheable {

  //La formula contiene un variabile non utilizzabile
  public static final String ERROR_CONTIENE_VAR_NON_UTILIZZABILE = "THIP11T016";
  //The error message : la formula deve contenere solamente dei costi totali.
  public static final String ERROR_SOLA_COSTO_TOTALE = "THIP11T064";

  //Fix 04080 AB BEGIN
  public String getIdAndDescr() {
    String ret = getIdComponenteCosto();
    if (ret != null && getDescrizione() != null)
      ret += "-" + getDescrizione().getDescrizione(); //Fix 6381
    return ret;
  }
  //Fix 04080 AB end

  public ComponenteCosto() {
    setIdAzienda(it.thera.thip.base.azienda.Azienda.getAziendaCorrente());
  }

  public String getTableNLSName() {
    return SystemParam.getSchema("THIP11") + "COMPON_COSTO_L";
  }

  public ErrorMessage checkDelete() {
    String where = FormulaCostiTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + FormulaCostiTM.FORMULA + " LIKE '%" + this.getIdComponenteCosto() + "%'";
    java.util.Vector allFormula = null;
    boolean found = false;
    try {
      allFormula = FormulaCosti.retrieveList(where, "", false);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    String idF = "";
    for (int i = 0; i < allFormula.size(); i++) {
      FormulaCosti currentFormula = (FormulaCosti) allFormula.get(i);
      com.thera.thermfw.formula.Formula f = currentFormula.getFormula();
      java.util.Set variabiles = f.getUsedVariables();
      found = variabiles.contains(this.getIdComponenteCosto()) || variabiles.contains(this.getIdComponenteCosto() + ".L") || variabiles.contains(this.getIdComponenteCosto() + ".I");
      if (found) {
        idF = currentFormula.getIdFormulaCosti();
        break;
      }
    }
    if (!found) {
      where = SpecFormulaTM.ID_AZIENDA + "='" + getIdAzienda() + "' AND " + SpecFormulaTM.FORMULA + " LIKE '%" + this.getIdComponenteCosto() + "%'";
      allFormula = new java.util.Vector();
      try {
        allFormula = SpecFormula.retrieveList(where, "", false);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      for (int i = 0; i < allFormula.size(); i++) {
        SpecFormula currentFormula = (SpecFormula) allFormula.get(i);
        com.thera.thermfw.formula.Formula f = currentFormula.getFormula();
        java.util.Set variabiles = f.getUsedVariables();
        found = variabiles.contains(this.getIdComponenteCosto()) || variabiles.contains(this.getIdComponenteCosto() + ".L") || variabiles.contains(this.getIdComponenteCosto() + ".I");
        if (found) {
          idF = currentFormula.getFormulaCosti().getIdFormulaCosti();
          break;
        }
      }
    }
    if (found)
      return new ErrorMessage("THIP11T032", new String[] {idF});
    return null;
  }

  public Class getOwnerClass() {
    return ComponenteCosto.class;
  }

  public String getDescription() {
    return getDescrizione().getDescrizione();
  }

  public ErrorMessage checkIdFormula() {
    ErrorMessage resultCheck = checkProvenienzaFormula();
    if (resultCheck != null)
      return resultCheck;

    if (getProvenienza() == SOLO_TOTALE || getProvenienza() == CALCOLATA_FORMULA) {
      resultCheck = checkComponenteRicorsiva(this, this, getProvenienza() == SOLO_TOTALE);
      if (resultCheck != null)
        return resultCheck;
    }

    return null;
  }

  public boolean equals(Object comp) {
    if (comp == null)
      return false;
    else {
      ComponenteCosto cmp = ((ComponenteCosto) comp);
      return cmp.getIdAzienda().equalsIgnoreCase(getIdAzienda()) && cmp.getIdComponenteCosto().equalsIgnoreCase(getIdComponenteCosto());
    }
  }

  //------------------------------------------------------------------------------
  public int save() throws SQLException {
    //FIX 10565 inizio
    if (isDirty()) {
      if(PersistentObjectCache.isCacheable(FormulaCosti.class))
        PersistentObjectCache.getCache(FormulaCosti.class).invalidate();
      if(PersistentObjectCache.isCacheable(ComponenteCosto.class))
        PersistentObjectCache.getCache(ComponenteCosto.class).invalidate();
      VariabiliCosti.clearVariabiliCostiHash();
    }
    //FIX 10565 fine
    VariabiliCosti.getCurrentVariabili();
    int rc = super.save();
    if (rc > 0)
      AddComponenteToVariabiliCosti();
    return rc;
  }

  public int delete() throws SQLException {
    VariabiliCosti.getCurrentVariabili();
    int rc = super.delete();
    if (rc > 0)
      RemoveComponenteFromVariabiliCosti();
    return rc;
  }

  //------------------------------------------------------------------------------

  protected synchronized void AddComponenteToVariabiliCosti() {
    FunctionVariable variables = new FunctionVariable();
    FunctionVariable variablesL = new FunctionVariable();
    FunctionVariable variablesI = new FunctionVariable();
    variables.setId(getIdComponenteCosto());
    variables.setDescription(getDescrizione().getDescrizioneRidotta());

    //Fix02234
    variables.setType(ExpressionTypes.NUMBER);

    variablesL.setId(getIdComponenteCosto() + VariabiliCosti.LIVELLO);
    variablesL.setDescription(getDescrizione().getDescrizioneRidotta() + " " + ResourceLoader.getString(VariabiliCosti.LIVELLO_RES, "Livello"));

    //Fix02234
    variablesL.setType(ExpressionTypes.NUMBER);

    variablesI.setId(getIdComponenteCosto() + VariabiliCosti.LIVELLO_INFERIORE);
    variablesI.setDescription(getDescrizione().getDescrizioneRidotta() + " " + ResourceLoader.getString(VariabiliCosti.LIVELLO_RES, "LivelloInferiore"));

    //Fix02234
    variablesI.setType(ExpressionTypes.NUMBER);

    VariabiliCosti.getCurrentVariabili().addVariable(variables);
    VariabiliCosti.getCurrentVariabili().addVariable(variablesL);
    VariabiliCosti.getCurrentVariabili().addVariable(variablesI);
  }

  protected synchronized void RemoveComponenteFromVariabiliCosti() {
    FunctionVariable variables = new FunctionVariable();
    variables.setId(getIdComponenteCosto());
    VariabiliCosti.getCurrentVariabili().removeVariable(variables);
    variables.setId(getIdComponenteCosto() + VariabiliCosti.LIVELLO);
    VariabiliCosti.getCurrentVariabili().removeVariable(variables);
    variables.setId(getIdComponenteCosto() + VariabiliCosti.LIVELLO_INFERIORE);
    VariabiliCosti.getCurrentVariabili().removeVariable(variables);
  }

  public ErrorMessage checkProvenienzaFormula() {
    if (getProvenienza() == ELEMENTARI && getIdFormula() != null) {
      setIdFormula(null);
      return null;
    }

    if (getProvenienza() == SOLO_TOTALE || getProvenienza() == CALCOLATA_FORMULA) {
      if (getFormula() == null)
        return new ErrorMessage("THIP11T020");
    }
    ErrorMessage rs = checkComponenteCostoFormula();
    if (rs != null)
      return rs;
    return null;
  }

  public ErrorMessage checkComponenteCostoFormula() {
    if (getProvenienza() == SOLO_TOTALE && getFormula() != null) {
      if (getFormula().getFormula() != null) {
        Set usedVariablesList = getFormula().getFormula().getUsedVariables();
        if (usedVariablesList != null) {
          if (existComponanteDettaglio(usedVariablesList))
            return new ErrorMessage(ERROR_SOLA_COSTO_TOTALE);

          if (usedVariablesList.contains(VariabiliCosti.ORE_ATTREZZAGGIO) || usedVariablesList.contains(VariabiliCosti.ORE_LAVORO)
            || usedVariablesList.contains(VariabiliCosti.ORE_MACCHINA) || usedVariablesList.contains(getIdComponenteCosto()))
            //|| usedVariablesList.contains(getIdComponenteCosto() + VariabiliCosti.LIVELLO_INFERIORE ) || usedVariablesList.contains(getIdComponenteCosto() + VariabiliCosti.LIVELLO))
            return new ErrorMessage(ERROR_CONTIENE_VAR_NON_UTILIZZABILE);
        }
      }
      List Specializzazione = getFormula().getSpecializzazione();
      Iterator iter = Specializzazione.iterator();
      SpecFormula specFormula;
      Set usedVariablesListSpecFormula;
      while (iter.hasNext()) {
        specFormula = (SpecFormula) iter.next();
        if (specFormula.getFormula() != null) {
          usedVariablesListSpecFormula = specFormula.getFormula().getUsedVariables();
          if (usedVariablesListSpecFormula != null) {
            if (existComponanteDettaglio(usedVariablesListSpecFormula))
              return new ErrorMessage(ERROR_SOLA_COSTO_TOTALE);

            if (usedVariablesListSpecFormula.contains(VariabiliCosti.ORE_ATTREZZAGGIO) || usedVariablesListSpecFormula.contains(VariabiliCosti.ORE_LAVORO)
              || usedVariablesListSpecFormula.contains(VariabiliCosti.ORE_MACCHINA) || usedVariablesListSpecFormula.contains(getIdComponenteCosto()))
              //|| usedVariablesListSpecFormula.contains(getIdComponenteCosto() + VariabiliCosti.LIVELLO_INFERIORE) || usedVariablesListSpecFormula.contains(getIdComponenteCosto() + VariabiliCosti.LIVELLO))
              return new ErrorMessage(ERROR_CONTIENE_VAR_NON_UTILIZZABILE);
          }
        }
      }
    }
    return null;
  }

  protected boolean existComponanteDettaglio(Set list) {
    if (list == null)
      return false;

    boolean found = false;
    Iterator iter = list.iterator();
    String s;
    while (iter.hasNext() && !found) {
      s = (String) iter.next();
      if (s.endsWith(VariabiliCosti.LIVELLO) ||
        s.endsWith(VariabiliCosti.LIVELLO_INFERIORE)) {
        found = true;
      }
    }
    return found;
  }

  public List getComponentiFromFormula(Formula formula) {
    ArrayList result = new ArrayList();
    Set variabili = formula.getUsedVariables();
    Iterator iter = variabili.iterator();
    ComponenteCosto c;
    while (iter.hasNext()) {
      c = VariabiliCosti.getCmpCosti((String) iter.next());
      if (c != null)
        result.add(c);
    }
    return result;
  }

  public ErrorMessage checkComponenteRicorsiva(ComponenteCosto originale, ComponenteCosto corrente, boolean checkQuestoLivello) {
    SpecFormula formulaSpec;
    Formula formula = corrente.getFormula().getFormula();
    ErrorMessage errore = checkFormulaRicorsiva(originale, corrente, formula, checkQuestoLivello);
    if (errore != null)
      return errore;
    List frmSpec = corrente.getFormula().getSpecializzazione(); //Fix 6381
    Iterator iter = frmSpec.iterator();
    while (iter.hasNext()) {
      formulaSpec = (SpecFormula) iter.next();
      errore = checkFormulaRicorsiva(originale, corrente, formulaSpec.getFormula(), checkQuestoLivello);
      if (errore != null)
        return errore;
    }
    return null;
  }

  public ErrorMessage checkFormulaRicorsiva(ComponenteCosto originale, ComponenteCosto corrente, Formula formula, boolean checkQuestoLivello) {
    List cmpList = getComponentiFromFormula(formula);
    Iterator iter = cmpList.iterator();
    ComponenteCosto compCost;
    ErrorMessage errore;
    while (iter.hasNext()) {
      compCost = (ComponenteCosto) iter.next();
      if (originale.equals(compCost)) {
        if (checkQuestoLivello)
          return new ErrorMessage("THIP11T024");
      }
      else if (corrente.equals(compCost)) {
        if (corrente.getProvenienza() != CALCOLATA_FORMULA) {
          return new ErrorMessage("THIP11T024");
        }
      }
      else if (compCost.getFormula() != null) {
        errore = checkComponenteRicorsiva(originale, compCost, true);
        if (errore != null)
          return errore;
      }
    }
    return null;
  }

  public ErrorMessage checkIdComponenteCosto() {
    if (!onDB) {
      VariabiliCosti vc = VariabiliCosti.getCurrentVariabili();
      if (vc.containsVariable(getIdComponenteCosto()))
        return new ErrorMessage("THIP11T031");
    }
    return null;
  }
  
  //33950 inizio
  public ErrorMessage checkCommessa() {
	  if(isGestioneATempo()) {
		  if(getIdTipoCosto() == null && getIdRisorsa() == null && getCostoUnitario() == null) {
			  //THIP_TN719 : Almeno uno tra Risorsa e Costo unitario deve essere valorizzato
			  return new ErrorMessage("THIP_TN719");
		  }
		  else if(getIdTipoCosto() != null && getIdRisorsa() != null && getCostoUnitario() != null) {
			  //THIP_TN720 : Solo uno tra Risorsa e Costo unitario deve essere valorizzato
			  return new ErrorMessage("THIP_TN720");
		  }
		  else if(getIdTipoCosto() == null && getIdRisorsa() != null && getCostoUnitario() == null) {
			  //THIP_TN721 : Valorizzare tipo costo
			  return new ErrorMessage("THIP_TN721");
		  }
		  else if(getIdTipoCosto() != null && getIdRisorsa() == null && getCostoUnitario() == null) {
			  //THIP_TN722 : Valorizzare risorsa
			  return new ErrorMessage("THIP_TN722");
		  }
	  }	  
	  return null;
  }
  //33950 fine
}

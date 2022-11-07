package it.thera.thip.produzione.commessa;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.PersistentObject;

/*
 * @(#)StoricoCommessaDet.java
 */

/**
 * StoricoCommessaDet
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 26/07/2021
 */
/*
 * Revisions:
 * Number  Date          Owner           Description
 * 33950   26/07/2021    RA				 Prima struttura
 */

public class StoricoCommessaDet extends StoricoCommessaDetPO {

  /**
   * checkDelete
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 12/04/2005    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete() {
    return null;
  }

  public String getFatherKey() {
	  return getStoricoCommessaKey();
  }

  public void setFatherKey(String key) {
	  setStoricoCommessaKey(key);
  }

  public void setFather(PersistentObject father) {
	  iStoricoCommessa.setObject(father);
  }

  public String getOrderByClause() {
	  return "";
  }

}


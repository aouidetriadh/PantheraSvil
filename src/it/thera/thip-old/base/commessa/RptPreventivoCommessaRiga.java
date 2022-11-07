/*
 * @(#)RptPreventivoCommessaRiga.java
 */

/**
 * RptPrevComRig
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 16:53:50
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 16/12/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 33048     04/03/2021    RA       Corretta gestione commenti
 */
package it.thera.thip.base.commessa;

import com.thera.thermfw.cbs.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.Factory;//33048
import com.thera.thermfw.security.*;

import it.thera.thip.base.comuniVenAcq.CommentiDocumento;//33048

public class RptPreventivoCommessaRiga
  extends RptPreventivoCommessaRigaPO
{

  // TIPO RIGA
  public static final char TIPO_RIGA_COMMESSA = '1';
  public static final char TIPO_RIGA_SOTTO_COMMESSA = '2';

  // MODALITA GENERAZIONE OFFERTA
  public static final char RIGA_COMMESSA = '0';
  public static final char RIGA_COMMESSA_VOCE_1 = '1';
  public static final char RIGA_COMMESSA_VOCE_2 = '2';
  public static final char ESCLUSO_GEN_OFFERTA = '3';

  //Stato
  public static final char STATO_INCOMPLETO = 'I';
  public static final char STATO_VALIDO = 'V';
  public static final char STATO_ANNULATO = 'A';
  public static final char STATO_SOSPESO = 'S';

  /**
   * checkDelete
   * @return ErrorMessage
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 16/12/2011    Wizard     Codice generato da Wizard
   *
   */
  public ErrorMessage checkDelete()
  {
    /**@todo*/
    return null;
  }

  public void caricaCommenti(PreventivoCommessaRiga tes, Entity entity, Task task, String idLanguage) throws Exception
  {
    if (tes.getCommenti() != null)
    {
      //33048 inizio
      /*
      String commenti = "";
      try
      {
        commenti += CommentService.getTrasformerCommentUses(tes.getCommenti(),
          entity, task, "-");
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      if (commenti == null || commenti.compareTo("") == 0)
        setRGesCommenti(new Integer(0));
      else
      {
        if (commenti.trim().length() > 300)
          commenti.substring(1, 300);
        setCommenti(commenti);
      }
      */
      this.setRGesCommenti(new Integer(tes.getCommenti().getId()));
      CommentiDocumento commPos = (CommentiDocumento)Factory.createObject(it.thera.thip.base.comuniVenAcq.CommentiDocumento.class);
      setCommenti(commPos.formattaListaCommenti(commPos.getCommentiPerPosizione(tes.getCommenti(), entity, task, "" + CommentiDocumento.NORMALE + CommentiDocumento.PRIMA_DETTAGLI, idLanguage)));
      setCommentiPiede(commPos.formattaListaCommenti(commPos.getCommentiPerPosizione(tes.getCommenti(), entity, task, "" + CommentiDocumento.DOPO_DETTAGLI, idLanguage)));
      //33048 fine
    }
  }
}

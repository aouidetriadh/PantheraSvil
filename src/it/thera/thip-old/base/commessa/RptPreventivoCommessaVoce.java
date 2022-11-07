/*
 * @(#)RptPreventivoCommessaVoce.java
 */

/**
 * RptPrevComVoce
 *
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Linda 16/12/2011 at 17:11:10
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 16/12/2011    Wizard     Codice generato da Wizard
 * Number    Date          Owner    Descrizione
 * 33048     08/03/2021    RA       Corretta gestione commenti
 */
package it.thera.thip.base.commessa;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.Factory;//33048
import com.thera.thermfw.security.*;

import it.thera.thip.base.comuniVenAcq.CommentiDocumento;//33048

public class RptPreventivoCommessaVoce extends RptPreventivoCommessaVocePO {

  // SPECIALIZZAZIONE RIGA
  public static final char RIGA_PRIMARIA = '1';
  public static final char RIGA_SECONDARIA = '2';

  // COSTO RIFERIMENTO
  public static final char COS_RIF_NESSUN_COSTO = '0';
  public static final char COS_RIF_COSTO_STD = '1';
  public static final char COS_RIF_COSTO_MEDIO = '2';
  public static final char COS_RIF_COSTO_ULTIMO = '3';
  public static final char COS_RIF_COSTO_ART = '4';

  // TIPO PREZZO
  public static final char TP_PREZZO_NORMALE = '0';
  public static final char TP_PREZZO_NETTO = '1';

  // PROVENIENZA PREZZO
  public static final char PRV_PREZZO_MANUALE = '0';
  public static final char PRV_PREZZO_LISTINO_GEN = '1';
  public static final char PRV_PREZZO_LISTINO_CLT = '2';
  public static final char PRV_PREZZO_LISTINO_ZONA = '3';
  public static final char PRV_PREZZO_LISTINO_CATEG_VEN = '4';
  public static final char PRV_PREZZO_CONTRATTO = '5';

  //TIPO RIGA
  public static final char TP_RIG_VOCE = '1';
  public static final char TP_RIG_ARTICOLO = '2';
  public static final char TP_RIG_RISORSA = '3';

  //Stato
  public static final char STATO_INCOMPLETO = 'I';
  public static final char STATO_VALIDO = 'V';
  public static final char STATO_ANNULATO = 'A';
  public static final char STATO_SOSPESO = 'S';

  //Tipo risorsa
  public static final char TP_RIS_MACCHINA = 'M';
  public static final char TP_RIS_UOMO = 'U';

  //Livello risorsa
  public static final char LIV_RIS_ = '-';
  public static final char LIV_RIS_GRUPPO = '1';
  public static final char LIV_RIS_FAMIGLIA = '2';
  public static final char LIV_RIS_RISORSA = '3';
  public static final char LIV_RIS_MATRICOLA = '4';

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
  public ErrorMessage checkDelete() {
    /**@todo*/
    return null;
  }
  public void caricaCommenti(PreventivoCommessaVoce tes, Entity entity, Task task, String idLanguage) throws Exception {
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


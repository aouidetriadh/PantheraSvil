package it.thera.thip.produzione.commessa.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.collector.BaseBOComponent;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.gui.ScreenData;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.type.DateType;
import com.thera.thermfw.type.DecimalType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebElement;
import com.thera.thermfw.web.servlet.BaseServlet;
import it.thera.thip.produzione.commessa.ConsuntivoCommessa;
/**
 * ConsuntivoCommessaDetServlet
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 05/10/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   05/10/2021    RA       Prima struttura
 */
public class ConsuntivoCommessaDetServlet extends BaseServlet {

   public static final String RES_FILE = "it.thera.thip.produzione.commessa.resources.ConsuntivoCommessa";

   public static void saveViewMDV(ServletEnvironment se, String soggetto)
   {
      boolean pushed = false;
      try
      {
         ConnectionManager.pushConnection();
         pushed = true;
         boolean totaliB = BaseServlet.getBooleanParameter(se.getRequest(), "Totali") ||
               getStringParameter(se.getRequest(), "Totali", "N").equals("Y");
         boolean dettagliCommB = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa") ||
               getStringParameter(se.getRequest(), "DettagliCommessa", "N").equals("Y");
         boolean dettagliSottoComB = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse") ||
               getStringParameter(se.getRequest(), "DettagliSottoCommesse", "N").equals("Y");
         boolean compPropriB = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri") ||
               getStringParameter(se.getRequest(), "ComponentiPropri", "N").equals("Y");
         boolean soloValB = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate") ||
               getStringParameter(se.getRequest(), "SoloComponentiValorizzate", "N").equals("Y");

         String totali = totaliB ? "Y" : "N";
         String dettagliComm = dettagliCommB ? "Y" : "N";
         String dettagliSottoCom = dettagliSottoComB ? "Y" : "N";
         String compPropri = compPropriB ? "Y" : "N";
         String soloVal = soloValB ? "Y" : "N";

         String stream = 
               "Totali" + ScreenData.SD_SEPARATOR + totali + ScreenData.SD_SEPARATOR +
               "DettagliCommessa" + ScreenData.SD_SEPARATOR + dettagliComm + ScreenData.SD_SEPARATOR +
               "DettagliSottoCommesse" + ScreenData.SD_SEPARATOR + dettagliSottoCom + ScreenData.SD_SEPARATOR +
               "ComponentiPropri" + ScreenData.SD_SEPARATOR + compPropri + ScreenData.SD_SEPARATOR +
               "SoloComponentiValorizzate" + ScreenData.SD_SEPARATOR + soloVal;    

         ScreenData screenData = ScreenData.getDefaultScreenData(Factory.getName(soggetto, Factory.CLASS_HDR));//Fix 8601 //Fix 10765
         if (screenData == null) {
            screenData = (ScreenData) Factory.createObject(ScreenData.class);
            screenData.setClassHdr(Factory.getName(soggetto, Factory.CLASS_HDR));
            screenData.setUserId(Security.getCurrentUser().getId());
            screenData.setDescription("Default");
            screenData.setDefaultRow(true);
         }
         screenData.setStreamedDataMySep(stream);
         int rc = screenData.save();
         if(rc > 0)
            ConnectionManager.commit();
         else
         {
            ConnectionManager.rollback();
            System.out.println("Errore nel salvataggio della memorizzazione dati video del documento!");
         }
      }
      catch(Exception ex)
      {
         ex.printStackTrace(Trace.excStream);
      }
      finally
      {
         if(pushed)
            ConnectionManager.popConnection();
      }
   }



   protected void processAction(ServletEnvironment se) throws Exception {
      String key = se.getRequest().getParameter("key");
      ConsuntivoCommessa consuntivo = ConsuntivoCommessa.elementWithKey(key, PersistentObject.NO_LOCK);

      String action = se.getRequest().getParameter("myAction");
      if(action.equals("AGGIORNA"))
         aggiornaConsuntivo(se, consuntivo);
      else if(action.equals("CARICA"))
         caricaConsuntivo(se, consuntivo);
   }


   protected void aggiornaConsuntivo(ServletEnvironment se, ConsuntivoCommessa consuntivo)throws Exception
   {
      boolean forzaRicalcolo = initializeConsuntivoCommessa(se, consuntivo);
      if(forzaRicalcolo)
         consuntivo.calcolaAlberoConsuntivi();
      else
         consuntivo.caricaAlberoConsuntivi();
      refreshGUI(se, consuntivo);
   }

   protected void caricaConsuntivo(ServletEnvironment se, ConsuntivoCommessa consuntivo)throws Exception
   {
      refreshGUI(se, consuntivo);
   }

   protected void refreshGUI(ServletEnvironment se, ConsuntivoCommessa consuntivo) throws IOException, JSONException
   {
      PrintWriter out = se.getResponse().getWriter();
      String ret = "parent.aggiornaConsuntivo(";
      ret += consuntivo.getColumnsDescriptors().toString()  + "," +
            consuntivo.getDataModelInMemoria().toString()           + "," +
            formatValue(consuntivo.getCostoGenerale())     + "," +
            formatValue(consuntivo.getCostoIndustriale())  + "," +
            formatValue(consuntivo.getCostoPrimo())        + "," +
            formatValue(consuntivo.getCostoRiferimento())  + "," +
            "'" + consuntivo.getDatiComuniEstesi().getTimestampAgg() + "' );";      
      out.println("<script>");
      out.println(ret);
      out.println("</script>");
   }

   public boolean isParameter(ServletEnvironment se, String param) {
      return se.getRequest().getParameter(param) != null ? true : false;
   }

   public boolean initializeConsuntivoCommessa(ServletEnvironment se, ConsuntivoCommessa consuntivo) {

      boolean forzaRicalcolo = false;
      if(isParameter(se, "Totali")) {
         boolean totali = BaseServlet.getBooleanParameter(se.getRequest(), "Totali");
         consuntivo.setTotali(totali);
      }

      if(isParameter(se, "DettagliCommessa")) {
         boolean dettagliCommessa = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliCommessa");
         consuntivo.setDettagliCommessa(dettagliCommessa);
      }

      if(isParameter(se, "DettagliSottoCommesse")) {
         boolean dettagliSottoCommesse = BaseServlet.getBooleanParameter(se.getRequest(), "DettagliSottoCommesse");
         consuntivo.setDettagliSottoCommesse(dettagliSottoCommesse);
      }

      if(isParameter(se, "ComponentiPropri")) {
         boolean componentiPropri = BaseServlet.getBooleanParameter(se.getRequest(), "ComponentiPropri");
         consuntivo.setComponentiPropri(componentiPropri);
      }

      if(isParameter(se, "SoloComponentiValorizzate")) {
         boolean soloComponentiValorizzate = BaseServlet.getBooleanParameter(se.getRequest(), "SoloComponentiValorizzate");
         consuntivo.setSoloComponentiValorizzate(soloComponentiValorizzate);
      }

      if(isParameter(se, "IdComponenteTotali")) {
         String idComponenteTotali = BaseServlet.getStringParameter(se.getRequest(), "IdComponenteTotali");
         if(!idComponenteTotali.equals(consuntivo.getIdComponenteTotali()))
            forzaRicalcolo = true;
         consuntivo.setIdComponenteTotali(idComponenteTotali);

      }

      if(isParameter(se, "UsaDataEstrazioneStorici")) {
         boolean usaDataEstrazioneStorici = BaseServlet.getBooleanParameter(se.getRequest(), "UsaDataEstrazioneStorici");
         if(usaDataEstrazioneStorici != consuntivo.isUsaDataEstrazioneStorici())
            forzaRicalcolo = true;
         consuntivo.setUsaDataEstrazioneStorici(usaDataEstrazioneStorici);
      }

      if(isParameter(se, "DataRiferimento")) {                 
         String dataRiferimentoStr = se.getRequest().getParameter("DataRiferimento");

         DateType dateType = new DateType();
         java.sql.Date dataRiferimento = (java.sql.Date)dateType.stringToObject(dataRiferimentoStr);
         if(!dataRiferimento.equals(consuntivo.getDataRiferimento()))
            forzaRicalcolo = true;
         consuntivo.setDataRiferimento(dataRiferimento);
      }

      if(isParameter(se, "StatoAvanzamento")) {
         char statoAvanzamento = BaseServlet.getStringParameter(se.getRequest(), "StatoAvanzamento").charAt(0);
         consuntivo.setStatoAvanzamento(statoAvanzamento);
      }

      if(isParameter(se, "Consolidato")) {
         boolean consolidato = BaseServlet.getBooleanParameter(se.getRequest(), "Consolidato");
         if(consolidato != consuntivo.isConsolidato())
            forzaRicalcolo = true;
         consuntivo.setConsolidato(consolidato);
      }

      if(isParameter(se, "EstrazioneOrdini")) {
         boolean estrazioneOrdini = BaseServlet.getBooleanParameter(se.getRequest(), "EstrazioneOrdini");
         if(estrazioneOrdini != consuntivo.isEstrazioneOrdini())
            forzaRicalcolo = true;
         consuntivo.setEstrazioneOrdini(estrazioneOrdini);
      }

      if(isParameter(se, "EstrazioneRichieste")) {
         boolean estrazioneRichieste = BaseServlet.getBooleanParameter(se.getRequest(), "EstrazioneRichieste");
         if(estrazioneRichieste != consuntivo.isEstrazioneRichieste())
            forzaRicalcolo = true;
         consuntivo.setEstrazioneRichieste(estrazioneRichieste);
      }

      return forzaRicalcolo;
   }

   public void salvaConsuntivoCommessa(ConsuntivoCommessa consuntivo) {
      try {
         int rc = consuntivo.save();
         if(rc >= 0)
            ConnectionManager.commit();
         else
            ConnectionManager.rollback();

      } 
      catch (SQLException e) {
         e.printStackTrace(Trace.excStream);
      }
   }

   public String formatValue(BigDecimal val) {
      String valueStr = "";
      if(val != null) {
         DecimalType dt = new DecimalType(16,6);
         valueStr = "'" + dt.format(dt.objectToString(val))+ "'";
      }
      return valueStr;
   }

   public void manageErrorMessages(PrintWriter out, List  errorMessages)  throws java.io.IOException {
      out.println("<script language='JavaScript1.2'>");
      out.println("var wListForm =  parent.document.getElementById(\"WinListForm\");");
      out.println("parent.showForm(wListForm, false);");	
      if(errorMessages == null || errorMessages.isEmpty())
         return;

      out.println("var errorsArray = new Array();");
      out.println("var errViewObj = parent.eval(parent.errorsViewName);");

      boolean foundErrors = false;
      boolean foundErrorsForz = false;
      int num = 1;
      if (errorMessages.size() > 0) {
         Iterator it = errorMessages.iterator();
         Vector allCompInError = new Vector();
         Vector collectionInError = new Vector();
         Vector collectionInErrorPos = new Vector();

         int i =0;
         while (it.hasNext()) {
            ErrorMessage em = (ErrorMessage)it.next();
            i++;
            String errId = em.getId();
            String errShortText = WebElement.formatStringForHTML(em.getText());
            String errLongText = WebElement.formatStringForHTML(em.getLongText());
            String errLabel = WebElement.formatStringForHTML(em.getAttOrGroupLabel());
            String errGrpName = WebElement.formatStringForHTML(em.getAttOrGroupName());
            String errSeverity = String.valueOf(em.getSeverity());
            boolean isForceable = em.getForceable();
            if(errLabel != null && !errLabel.equals(""))
               errShortText = errLabel + " - " + errShortText;
            Vector idCompInError = new Vector();
            Vector components = em.getComponents();
            if(em.getSeverity()==ErrorMessage.ERROR) {
               foundErrors=true;
            }

            if(components != null && components.size() > 0)	{
               BODataCollector coll = ((BaseBOComponent)components.firstElement()).getComponentManager().getBODataCollector();
               if(coll.getOwnerCollectionDC() != null) {
                  String collName = coll.getOwnerCollectionDC().getKeyForBaseComponentsCollection();
                  collectionInError.addElement(collName);
                  String pos = "" + coll.getPositionInOwnerCollectionDC();
                  collectionInErrorPos.addElement(pos);
               }
               else {
                  boolean f = true;
                  Iterator i1 = components.iterator();
                  while(i1.hasNext()) {
                     BaseBOComponent bc = (BaseBOComponent)i1.next();
                     String classADName = bc.getComponentManager().getKeyForBaseComponentsCollection();
                     if (f) {
                        f = false;
                        idCompInError.addElement(classADName);
                     }
                     allCompInError.addElement(classADName);
                  }
               }
            }			

            out.println("var idCompInError = new Array();");
            out.println("numErr = " + errorMessages.size()  + ";");  			
            out.println("foundErrors = " + foundErrors + ";");  			
            out.println("foundErrorsForzabili = " + foundErrorsForz + ";");  			

            Iterator idCompIter = idCompInError.iterator();
            while(idCompIter.hasNext())	{
               String curIdCompInErr = (String)idCompIter.next();
               out.println("idCompInError[idCompInError.length]='" + i + KeyHelper.KEY_SEPARATOR+curIdCompInErr + "';");  			

            }
            out.println("var singleError = new Array('" + errId + "', '" + errShortText + "', idCompInError, '" + errSeverity + "', '" + errLongText + "', " + isForceable + ", '" + errGrpName + "', '" + errLabel + "');");
            out.println("errorsArray[errorsArray.length] = singleError;");
         }
      } 	

      out.println("if (numErr > 0) {");
      out.println("		errViewObj.addErrorsAsArray(errorsArray, parent.document.forms[0].elements);");		
      out.println("}");		
      out.println("else ");		
      out.println("		errViewObj.setMessage(null); ");	
      out.println("</script>");
   }
}

package com.thera.thermfw.setup.web;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import com.thera.thermfw.dict.*;
import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.setup.*;
import com.thera.thermfw.web.*;
import com.thera.thermfw.web.servlet.*;

/**
 * Action adapter per SQLFileTesterWeb
 * <br><br><b>Copyright (C): Thera SpA</b>
 * @author Cristina Borboni 31/05/2006
 * <br><br>
 */

/*
 * Revisions:
 * Number   Date        Owner   Description
 * 05488    31/05/2006  CB      Prima versione
 * 05504    09/06/2006  CB      Aggiunta gestione file TDDML
 * 12101    06/04/2010  Mz      rende compatibile con Firefox e Chrome
 * 13604    29/11/2010  ES      Controlla se TDDML.dtd non è nel classpath x evitare eccezioni
 * 19061    11/03/2014  Mz      modifica posto dei file temporane
 * 35912    03/11/2022  RA		Aggiunta gestione GenerateShortNames
 */
public class SQLFileTesterWebActionAdapter extends FormActionAdapter {

   public static final String IMPORT = "IMPORT";

   public static final String PARAM_ID = "Id";
   public static final String PARAM_NUM_PAGE = "NumPage";
   public static final String PARAM_REMOVE = "RemoveFromSession";
   public static final String PARAM_RPTDESC_ID = "DescriptorId";

   public static final String ATTR_RPT_NAME = "RptFileName";
   public static final String ATTR_DESCR = "Descr";

   public static final String SETUP_RES = "com.thera.thermfw.setup.resources.Setup"; //Mod. 13604

   /**
    * Permette di gestire richieste in cui il content type è multipart.
    * @param se ServletEnvironment
    * @throws ServletException
    * @throws IOException
    */
   public void processAction(ServletEnvironment se) throws ServletException, IOException {
     // Cerco il content type
     Enumeration e = se.getRequest().getHeaders("content-type");

     se.getResponse().setContentType("text/plain");

     PrintWriter output = se.getResponse().getWriter();
     String value = "";
     if (e.hasMoreElements())
       value = (String) e.nextElement();

     // Il content type deve essere multipart
     if (!value.startsWith("multipart/form-data") ||
         !runFileTesterWeb(se,output)){
       se.getResponse().setContentType("text/html");//Fix 12101
       se.sendRequest(getServletContext(), "com/thera/thermfw/common/ErrorListHandler.jsp?thClassName=SQLFileTesterWeb", true); //!!!! VIEWSELECTOR?????
     }

     output.close();
   }

   /**
    * Esegue l'elaborazione mandando l'output sul PrintWriter passato
    * @param se ServletEnvironment
    * @param output PrintWriter
    * @return boolean
    */
   private boolean runFileTesterWeb(ServletEnvironment se,PrintWriter output){
     try{
         // Istanzio un MultipartHandler
         MultipartHandler mh = new MultipartHandler(se);
         mh.initialize();

         if (internalRunFileTesterWeb(se, mh)){
           // creo un file temporaneo che poi sparirà
           MultipartFile f = (MultipartFile) mh.getMultipartFiles().get(0);
           //Fix 19061 inizio
            boolean tryUpdate = (mh.getMultipartRequest().getParameter("TryUpdate") != null);
            boolean generateShortNames = (mh.getMultipartRequest().getParameter("GenerateShortNames") != null);//35912
            File tddml = null;
            File script = null;
            try {
              //cerco la caretlla temporanea
              File tempDir = getTempDir(se);
              //copio il tddml nella cartella temporanea
              tddml = copyTDDML(tempDir);
              //salvo lo script e lo modfico
              script = saveScript(f, tempDir);
              // lancio l'SQLFileTester
              SQLFileTesterWeb sqlFileTesterWeb = (SQLFileTesterWeb) Factory.createObject(SQLFileTesterWeb.class);
              sqlFileTesterWeb.setSQLFile(script);
              sqlFileTesterWeb.setOutputStream(output);
              sqlFileTesterWeb.setTryUpdate(tryUpdate);
              sqlFileTesterWeb.setGenerateShortNames(generateShortNames);//35912
              sqlFileTesterWeb.processFile();
              sqlFileTesterWeb.getSQLFile();
    
            }
            catch (Exception e) {
              se.addErrorMessage("BAS0000078", e.getMessage());
              return false;
            }
            finally {
              if(script != null) {
                script.delete();
              }
              if (tddml != null) {
                tddml.delete();
              }
            }
          /*
           int punto = f.getFileName().lastIndexOf(".");
           String nomeFile = f.getFileName().substring(0, punto);
           //5504 CB inizio
           String extFile = f.getFileName().substring(punto);
          File tempFileRead = File.createTempFile(nomeFile, extFile);
           File tempFileWrite = File.createTempFile(nomeFile, extFile);
           tempFileRead.deleteOnExit();
           f.save(tempFileRead);
           tempFileWrite = createTempTDDMLFile(tempFileRead, tempFileWrite, nomeFile, extFile);
           tempFileWrite.deleteOnExit();
           tempFileRead.delete();
           //Mod. 13606 inizo//Catturo eventuali eccezioni per segnalare se non ho trovato
           //nel classpath il file TDDML.doc che devo copiare nella C:/temp
           try {
             copyTDDMLFile(tempFileWrite);
           }
           catch (IOException e) {
             se.addErrorMessage("BAS0000078",e.getMessage());
             return false;
           }
           //fine mod. 13604
           //5504 CB fine

           // lancio l'SQLFileTester
           SQLFileTesterWeb sqlFileTesterWeb = (SQLFileTesterWeb)Factory.createObject(SQLFileTesterWeb.class);
           sqlFileTesterWeb.setSQLFile(tempFileWrite);
           sqlFileTesterWeb.setOutputStream(output);
           sqlFileTesterWeb.setTryUpdate(mh.getMultipartRequest().getParameter("TryUpdate")==null?false:true);
           sqlFileTesterWeb.processFile();
           sqlFileTesterWeb.getSQLFile();
           tempFileWrite.delete();
           //sqlFileTesterWeb.notify();
            */
          //Fix 19061 fine

         }
         else
           return false;
       }
       catch (IOException e) {
         e.printStackTrace(Trace.excStream);
         return false;
       }
       catch (ServletException e) {
       e.printStackTrace(Trace.excStream);
       return false;
     }


     return true;
   }

   //5504 CB inizio
   protected File createTempTDDMLFile(File tempRead, File tempWrite, String name, String ext) throws IOException{
     FileWriter w = new FileWriter(tempWrite);
     PrintWriter writer = new PrintWriter(w);
     LineNumberReader reader = new LineNumberReader(new FileReader(tempRead));
     String line = reader.readLine();
     while (line != null) {
       if (line.startsWith(TDDMLWriter.DOCTYPE_ST)) {
         //String docType = TDDMLWriter.DOCTYPE.substring(0,TDDMLWriter.DOCTYPE.indexOf(TDDMLWriter.DTD_STAMP));
         int index = TDDMLWriter.DOCTYPE.indexOf(TDDMLWriter.DTD_STAMP);
         String docType = TDDMLWriter.DOCTYPE.substring(0,index);
         docType += TDDMLWriter.DTD_FILE;
         docType += TDDMLWriter.DOCTYPE.substring(index+TDDMLWriter.DTD_STAMP.length());
         writer.println(docType);
       }
       else
         writer.println(line);
       line = reader.readLine();
     }
     reader.close();
     writer.close();
     w.close();
     return tempWrite;
   }

   /**
    * Copia il file TDDML nella temp in modo che si possa eseguire il file xml senza errori
    */
   protected void copyTDDMLFile(File file) throws IOException{
     String path = file.getPath().substring(0,file.getPath().indexOf(file.getName()));
     ClassLoader cl = this.getClass().getClassLoader();
     URL u = cl.getSystemResource(TDDMLWriter.DTD_FILE);
     FileWriter writer = new FileWriter(path+TDDMLWriter.DTD_FILE);

     //Mod. 13604//Se nel classpath non c'è il file TDDMLWriter.DTD_FILE (TDDML.dtd) non posso procedere
     if (u == null)
       throw new IOException(ResourceLoader.getString(SETUP_RES, "TDDMLdtdNotInClasspath",  new String[] {TDDMLWriter.DTD_FILE}));
     //fine mod. 13604
     LineNumberReader reader = new LineNumberReader(new FileReader(u.getPath()));
     String line;
     while ((line=reader.readLine())!=null)
       writer.write(line);
     reader.close();
     writer.close();
   }
   //5504 CB fine


   /**
    * verifiche pre elaborazione
    * @param se ServletEnvironment
    * @param mh MultipartHandler
    * @return boolean
    */
   private boolean internalRunFileTesterWeb(ServletEnvironment se,MultipartHandler mh){
     // Controlla che sia stato selezionato un file
     if (mh.getMultipartFiles().size() == 0) {
       se.addErrorMessage("SET00001");
       return false;
     }

     // Controlla che il file non sia vuoto
     MultipartFile mf = (MultipartFile) mh.getMultipartFiles().get(0);
     if (mf.getSize() == 0) {
       se.addErrorMessage("SET00002");
       return false;
     }

     return true;
   }
   //Fix 19061 inizio
   protected File getTempDir(ServletEnvironment se) {
     String rootPath = se.getSession().getServletContext().getRealPath("");
     File tmp = new File(rootPath, "tmp");
     if(!tmp.exists()) {
       tmp.mkdir();
     }
     return tmp;
   }
   
   protected File saveScript(MultipartFile f, File tempDir) throws IOException{
     //preparo posto per lo script nella cartella temporanea
     File originalScript = new File(tempDir, "tmp_" + f.getFileName());
     //preparo posto per lo script modificato  nella cartella temporanea
     File changedScript = new File(tempDir, f.getFileName());
     //salvo lo script
     f.save(originalScript);
     //modifico lo script
     PrintWriter writer = new PrintWriter(new FileWriter(changedScript));
     BufferedReader reader = new BufferedReader(new FileReader(originalScript));
     String line = reader.readLine();
     while (line != null) {
       if (line.startsWith(TDDMLWriter.DOCTYPE_ST)) {
         int index = TDDMLWriter.DOCTYPE.indexOf(TDDMLWriter.DTD_STAMP);
         String docType = TDDMLWriter.DOCTYPE.substring(0, index);
         docType += TDDMLWriter.DTD_FILE;
         docType += TDDMLWriter.DOCTYPE.substring(index + TDDMLWriter.DTD_STAMP.length());
         line = docType;
       }
       writer.println(line);
       line = reader.readLine();
     }
     reader.close();
     writer.close();
     
     //toglio lo script originale
     originalScript.delete();
     
     return changedScript;
   }
   
   protected File copyTDDML(File tempDir) throws IOException{
     URL u = this.getClass().getClassLoader().getResource(TDDMLWriter.DTD_FILE);
     if (u == null) {
       throw new IOException(ResourceLoader.getString(SETUP_RES, "TDDMLdtdNotInClasspath",  new String[] {TDDMLWriter.DTD_FILE}));
     }
     
     File tddml = new File(tempDir, TDDMLWriter.DTD_FILE);
     if(tddml.exists()) {
       tddml.delete();
     }
     
     PrintWriter writer = new PrintWriter(new FileWriter(tddml));

     LineNumberReader reader = new LineNumberReader(new FileReader(u.getPath()));
     String line = reader.readLine();
     while (line != null) {
       writer.println(line);
       line = reader.readLine();
     }
       
     reader.close();
     writer.close();
     
     return tddml;
   }
   //Fix 19061 fine
}

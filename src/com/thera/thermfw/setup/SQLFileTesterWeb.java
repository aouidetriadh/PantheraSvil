package com.thera.thermfw.setup;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import com.thera.thermfw.base.*;
import com.thera.thermfw.batch.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;
import com.thera.thermfw.web.*;

/**
 * Classe di business del SQLFileTesterWeb.
 *
 * <br><br><b>Copyright (C): Thera SpA</b>
 * @author Cristina Borboni 31/05/2006
 * <br><br>
 */

/*
 * Revisions:
 * Number   Date        Owner   Description
 * 05488    31/05/2006  CB      Prima versione
 * 09634      31/07/2008  ES         Sostituisco le stringhe cablate con stringhe lette da file risorse
 * 35912    02/11/2022   RA		Aggiunto attributo iGenerateShortNames
 */
public class SQLFileTesterWeb extends BusinessObjectAdapter implements Authorizable {

    //Mod. 9634: definizione del file di risorse
    /**
     * File di risorse.
     */
    public static final String SETUP_RES = "com.thera.thermfw.setup.resources.Setup";

   // Flag per decidere se fare un update in caso di riga duplicata
   protected boolean tryUpdate = false;
   // Nome del file da cui importare
   protected String srcFileName;

   // File da cui fare l'importazione dati
   protected File sqlFile;

   protected PrintWriter output;

   protected static final char NT='N';
   protected static final char AS='A';
   protected static final char LINUX='L';

   protected char platformType = NT;

   // Lista complessiva dei parametri
   protected List parameters = new ArrayList();
   // Lista dei parametri che l'utente ha selezionato per l'aggiornamento
   protected List selected = new ArrayList();
   
   protected boolean iGenerateShortNames=false;//35912


   public String getSrcFileName() {
     return srcFileName;
   }

   public void setSrcFileName(String srcFileName) {
     this.srcFileName = srcFileName;
   }

   public boolean isTryUpdate() {
     return tryUpdate;
   }

   public void setTryUpdate(boolean tryUp) {
     tryUpdate = tryUp;
   }

   public File getSQLFile() {
      return sqlFile;
   }

   public void setSQLFile(File file) {
      sqlFile = file;
   }

   public char getPlatformType() {
      return platformType;
   }

   public void setPlatformType(char pt) {
      platformType = pt;
   }

   public void setOutputStream(PrintWriter o) {
      output = o;
   }
   
   //35912 inizio
   public boolean isGenerateShortNames() {
	   return iGenerateShortNames;
   }
   
   public void setGenerateShortNames(boolean generateShortNames) {
	   iGenerateShortNames = generateShortNames;
   }
   //35912 fine

   /**
    * @param obj Copyable
    * @throws CopyException
    */
   public void setEqual(Copyable obj) throws CopyException {
      SQLFileTesterWeb src = (SQLFileTesterWeb) obj;
      CopyableHelper.copyObject(this, obj);
      parameters.clear();
      parameters.addAll(src.parameters);
      selected.clear();
      selected.addAll(src.selected);
   }

   /**
    * Processa il file sql passato usando la classe SQLFileTester
    * @return boolean
    */
   public boolean processFile(){
     try{
       String ptfType = Setup.NT;
       if (getPlatformType() == NT)
         ptfType = Setup.NT;
       else if (getPlatformType() == AS)
         ptfType = Setup.AS;
       else if (getPlatformType() == LINUX)
         ptfType = Setup.LINUX;

       Setup setup = new Setup(ptfType, output);
       Setup.DB_DUPLICATED_ROW_ERROR=ConnectionManager.getCurrentDatabase().getDuplicatedRowCode();
       setup.setUser(Security.getCurrentUser().getId());
       setup.setTryUpdate(isTryUpdate());
       setup.setGenerateShortNames(isGenerateShortNames());//35912
       setup.initDbDir(ConnectionManager.getCurrentDatabase());

       StringWriter writer = new StringWriter();
       PrintWriter outputTemp = new PrintWriter(writer);
       outputTemp.println("<html><body><textarea id=\"internalArea\" rows=17 cols=78>");
       outputTemp.println(ResourceLoader.getString(SETUP_RES, "FTW_FileCopyOnServer",  new String[] {getSQLFile().getName()}));//Mod. 9634//"Il file viene copiato su server con nome " + getSQLFile().getName()+"\n");

       SQLFileTester sqlFT = new SQLFileTester(setup, getSQLFile().getName());
       sqlFT.setOutputFile(outputTemp);
       sqlFT.process(getSQLFile().getPath(), false);
       outputTemp.println("</textarea>");

       output.println(writer.toString());

       String message="";
       Vector results = sqlFT.getResultList();
       for (int i=0;i<results.size();i++)
         message+=((String[]) results.elementAt(i))[1]+"\n";

       output.println("<script>alert(\"" + WebElement.formatStringForHTML(message) +"\")</script>");
       if (sqlFT.getErrorCount() == 0) {
         ConnectionManager.commit();
//         outputTemp.println("Commit");
       }
       else {
         ConnectionManager.rollback();
//         outputTemp.println("Rollback");
       }

       output.println("</body></html>");
     }catch(SQLException e){
       return false;
     }

     return true;
   }

}

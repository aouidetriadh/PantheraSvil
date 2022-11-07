package com.thera.thermfw.dict;

import org.w3c.dom.*;

/**
 * Generator con specifiche per DB2
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

/*
 * Revisions:
 * Fix      Date          Owner      Description
 * 05504    06/06/2006    CB         Prima implementazione
 * 09884    08/10/2008    DM         Inserito "#ERRORE#" nel messaggio della GeneratorException
 * 10293    23/12/2008    CB         Corretto malfunzionamento quando usato il tag platform
 * 32400    03/12/2020    Mz         non inserirre i nomi corti su colonne e indici 
 * 35912    03/11/2022	  RA         Corretta gestione nomi corti 
*/
public class DDLGeneratorDB2ASShort extends DDLGeneratorDB2AS {

  protected boolean iGenerateShortNames = false;//35912
  
  protected DDLGeneratorDB2ASShort() throws GeneratorException {
  }

  protected String getPlatformName() {
    return PLATF_DB2_400_SHORT;
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
   * In AS-400 i nomi brevi per le colonne sono scritti come
   * <NOME_COLONNA> FOR COLUMN <NOME_BREVE_COLONNA>
   * @param element Element
   * @return String
   */
  //32400 inizio
  @Override
  protected String getColumnName(Element element) throws TDDMLException  {
   String colName = element.getAttribute(ATTR_NAME);
   if(isGenerateShortNames()) {//35912
	   String shortName = element.getAttribute(ATTR_SHORTNAME);
	   if (colName.length() > 10){
	     if (shortName == null || shortName.length() == 0)
	       // 9884 DM inizio
	       // throw new GeneratorException("Missing short name for column "+ colName);
	       throw new GeneratorException("#ERRORE# Missing short name for column "+ colName);
	       // 9884 DM fine
	     else
	       colName = colName + " FOR COLUMN " + shortName;
	   }
   }//35912
   return colName;
 }
 //32400 fine

 /**
  * In AS-400 i nomi brevi per le tabelle vengono impostati con il comando
  * RENAME TABLE <NOME_TABELLA> TO SYSTEM NAME <NOME_BREVE_TABELLA>
  * @param element Element
  * @param executor DDLExecutor
  * @throws TDDMLException
  */
  @Override
  public void navigateCreateTable(Element element,DDLExecutor executor) throws TDDMLException {
   super.navigateCreateTable(element, executor);
   if (validatePlatform(element)) { //10293 
     String tabName = element.getAttribute(ATTR_NAME);
     String shortName = element.getAttribute(ATTR_SHORTNAME);
     if (tabName.length() > 10) {
       if (shortName == null || shortName.length() == 0)

         // 9884 DM inizio
         // throw new GeneratorException("Missing short name for table "+ tabName);
         throw new GeneratorException("#ERRORE# Missing short name for table " +
                                      tabName);
       // 9884 DM fine
       else {
         executor.addStatementLine("RENAME TABLE " + getQualifiedName(element) +
                                   " TO SYSTEM NAME " + shortName);
         executor.executeStatement();
       }
     }
   }
 }

 /**
  * In AS-400 i nomi brevi per gli indici vengono impostati con il comando
  * RENAME INDEX <NOME_INDICE> TO SYSTEM NAME <NOME_BREVE_INDICE>
  * @param element Element
  * @param executor DDLExecutor
  * @throws TDDMLException
  */
 //32400 inizio
 @Override
 public void navigateCreateIndex(Element element,DDLExecutor executor) throws TDDMLException {
   super.navigateCreateIndex(element, executor);
   //if (validatePlatform(element)) { //10293//35912
   if (validatePlatform(element) && isGenerateShortNames()) { //10293//35912
     String indexName = element.getAttribute(ATTR_NAME);
     String shortName = element.getAttribute(ATTR_SHORTNAME);
     if (indexName.length() > 10) {
       if (shortName == null || shortName.length() == 0)

         // 9884 DM inizio
         // throw new GeneratorException("Missing short name for index "+ indexName);
         throw new GeneratorException("#ERRORE# Missing short name for index " +
                                      indexName);
       // 9884 DM fine
       else {
         executor.addStatementLine("RENAME INDEX " + getQualifiedName(element) +
                                   " TO SYSTEM NAME " + shortName);
         executor.executeStatement();
       }
     }
   }
 }
 //32400 fine

}

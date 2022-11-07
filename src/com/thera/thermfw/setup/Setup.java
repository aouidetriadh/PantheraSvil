package com.thera.thermfw.setup;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.*;

/**
 * Classe di gestione dei setup.
 * <br><br><b>Copyright (C): Thera SpA</b>
 * @author Davide Bottarelli 00/00/2000
 */
/*
 * Revisions:
 * Number     Date        Owner      Description
 * 00002      31/08/2001  DM         Aggiusta la costante ORACLE
 * 00014      20/07/2001  LP         Aggiuti metodi setupCD, setupRD e setupVG
 * 00068      16/10/2001  DM         Aggiunta la costante SQLSERVER
 * 00261      23/05/2002  ES         Se insert fallisce tento l'update
 *                                   Aggiunto parametro FC = frequenza commit
 *                                   Aggiungo parametro MI = modalità insert
 * 00273      04/06/2002  ES         Leggo da file i nomi dei file .ddl o .sql da non processare
 * 02558      04/10/2004  LP         Corretto assegnazione della password all'utente da creare.
 * 03402      16/03/2005  DF         Modificata implementazione metodo getTableKeys per ovviare a errore AS400
 * 03775      16/05/2005  DM         Cambiato l'indice di partenza in getTableKeys()
 * 03679      27/05/2005  MM         Diretto l'output da System.err a System.out
 * 05488      05/06/2006  CB         Aggiunto metodo per inizializzare la db directory
 * 09805      22/09/2008  DM         Uso del DB2v813MetaDataAdapter
 * 09990      28/10/2008  DM         Accesso al DB2v813MetaDataAdapter dall'interface Database
 * 09634      30/07/2008  ES         Sostituisco le stringhe cablate con stringhe lette da file risorse
 * 11397      28/09/2009  ES         Gestione dei file _NAV per la navigazione
 * 35912      03/11/2022  RA		 Gestione GenerateShortNames
 */

public class Setup {

  /**
   * Costanti simboliche.
   */

  //Mod. 9634: definizione del file di risorse
  /**
   * File di risorse.
   */
  public static final String SETUP_RES =
    "com.thera.thermfw.setup.resources.Setup";

  protected static final int DDL       = 0;
  protected static final int FW_SQL    = 1;
  protected static final int ADMIN_SQL = 2;
  protected static final int DATA_SQL  = 3;

  protected static final String DDL_NAME       = "DDLOrder.txt";
  protected static final String FW_NAME        = "fwSQLOrder.txt";
  protected static final String ADMIN_NAME     = "adminSQLOrder.txt";
  protected static final String DATA_NAME      = "dataSQLOrder.txt";
  protected static final String EXTENSION_NAME = "extensionOrder.txt";
  //Mod. 273 nome file con i file .sql o ddl da ignorare durante i setup
  protected static final String IGNORE_NAME    = "ignoreOrder.txt";

  protected static final String FW_PATH    = "fw" + File.separator;
  protected static final String ADMIN_PATH = "admin" + File.separator;
  protected static final String DATA_PATH  = "data" + File.separator;

  public static final String NT        = "nt" + File.separator;
  public static final String AS        = "as" + File.separator;
  public static final String DB2       = "db2" + File.separator;
  public static final String INFORMIX  = "informix" + File.separator;
  public static final String LINUX     = "linux" + File.separator;
  public static final String ORACLE    = "oracle" + File.separator;
  public static final String SQLSERVER = "sqlserver" + File.separator;

  // Usato solo su AS400
  // Funziona solo se tutte le tabelle stanno nello stesso schema !!!
  // Fix 3402
  protected static final CachedStatement stmt = new CachedStatement
                                              ("select "+
                                               "col.table_schema, col.table_name, col.column_name "+
                                               "from "+SystemParam.getFrameworkSchema()+"syscstcol as col "+
                                               "join "+SystemParam.getFrameworkSchema()+"syscst as cst "+
                                               "on cst.constraint_schema=col.constraint_schema and "+
                                               "cst.constraint_name=col.constraint_name "+
                                               "where cst.constraint_type like 'PRIM%' and "+
                                               "col.table_schema=? and col.table_name=?");



  //________________________AGGIUNTO DA LP__________________________________\\

 /**
  * Costanti simboliche che servono per la compilazione dei file con i componenti,
  * con le relazioni e con i gruppi di validazione.
  */
  public static final String CD = "_CD";
  public static final String RD = "_RD";
  public static final String VG = "_VG";
  public static final String NV = "_NAV"; //11397

  //________________________________________________________________________\\

  public static int DB_DUPLICATED_ROW_ERROR;

  public static BufferedReader input = new java.io.BufferedReader(new InputStreamReader(System.in));

  protected String database;
  protected String absoluteFullPath;
  protected String localFullPath;
  protected boolean localPathSetted = false;
  protected String serverFullPath;
  protected boolean serverPathSetted = false;
  protected String user;
  protected String password; //...FIX 2558
  protected String moduleName;
  //Mod. 261
  protected int commitFrequency;
  protected boolean executeInternalCommit;
  protected String insertMode;
  protected boolean tryUpdate;
  // Hash table con i nomi delle colonne chiave primaria delle varie tabelle.
  private static Hashtable tableKeys = new Hashtable();
  DatabaseMetaData metaData;
  //fine mod. 261
  //Mod. 273
  // Collezione con gli eventuali nomi dei file da ignorare durante il setup in
  //  modalità IU=insert/update per il modulo in esame.
  public static Collection filesToBeIgnored = new HashSet();
  //fine mod. 273

  protected String databaseType;// = NT;
  protected static String dataBaseDir;// = DB2;
  protected PrintWriter output;
  
  protected boolean iGenerateShortNames;//35912

 /**
  * Costruttore.
  * @param databaseType String
  * @param output PrintWriter
  */
 public Setup(String databaseType, PrintWriter output) {
 this.databaseType = databaseType;
    this.output = output;
  }

 /**
  * getDBDir().
  * @return String
  */
  public String getDBDir() {
    return dataBaseDir;
  }

 /**
  * setDBDir(String value).
  * @param value String
  */
  public static void setDBDir(String value) {
    dataBaseDir = value;
  }

 /**
  * getPlatformType().
  * @return String
  */
  public String getPlatformType() {
    return databaseType;
  }

 /**
  * setUser(String user).
  * @param user String
  */
  public void setUser(String user) {
    this.user = user;
  }

 /**
  * getUser().
  * @return String
  */
  public String getUser() {
    return user;
  }

 /**
  * setPassword(String password).
  * @param password String
  */
  //...FIX 2558
  public void setPassword(String password) {
    this.password = password;
  }

 /**
  * getPassword().
  * @return String
  */
  //...FIX 2558
  public String getPassword() {
    return password;
  }

 /**
  * setModuleName(String moduleName).
  * @param moduleName String
  */
  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

 /**
  * getModuleName().
  * @return String
  */
  public String getModuleName() {
    return moduleName;
  }

 /**
  * setDatabase(String database).
  * @param database String
  */
  public void setDatabase(String database) {
    this.database = database;
  }

 /**
  * setAbsolutePath(String absolutePath).
  * @param absolutePath String
  */
  public void setAbsolutePath(String absolutePath) {
    absoluteFullPath = absolutePath;
    if(!absoluteFullPath.endsWith(File.separator))
      absoluteFullPath += File.separator;
  }

 /**
  * getAbsoluteFullPath().
  * @return String
  */
  public String getAbsoluteFullPath() {
    return absoluteFullPath;
  }

 /**
  * setLocalPath(String localPath).
  * @param localPath String
  */
  public void setLocalPath(String localPath) {
    localPathSetted = true;
    localFullPath = localPath;
    if(!localFullPath.endsWith(File.separator))
      localFullPath += File.separator;
  }

 /**
  * getLocalFullPath().
  * @return String
  */
  public String getLocalFullPath() {
    return localFullPath;
  }

 /**
  * isLocalPathSetted().
  * @return boolean
  */
  public boolean isLocalPathSetted() {
    return localPathSetted;
  }

 /**
  * setServerPath(String serverPath).
  * @param serverPath String
  */
  public void setServerPath(String serverPath) {
    serverPathSetted = true;
    serverFullPath = serverPath;
    if(!serverFullPath.endsWith(File.separator))
      serverFullPath += File.separator;
  }

 /**
  * getServerFullPath().
  * @return String
  */
  public String getServerFullPath() {
    return serverFullPath;
  }

 /**
  * isServerPathSetted().
  * @return boolean
  */
  public boolean isServerPathSetted() {
    return serverPathSetted;
  }
  //...Mod. 261

 /**
  * setCommitFrequency(int commitFrequency).
  * @param commitFrequency int
  */
  public void setCommitFrequency(int commitFrequency) {
    this.commitFrequency = commitFrequency;
  }

 /**
  * getCommitFrequency().
  * @return int
  */
  public int getCommitFrequency() {
    return commitFrequency;
  }

 /**
  * setExecuteInternalCommit(boolean executeInternalCommit).
  * @param executeInternalCommit boolean
  */
  public void setExecuteInternalCommit(boolean executeInternalCommit) {
    this.executeInternalCommit = executeInternalCommit;
  }

 /**
  * isExecuteInternalCommit().
  * @return boolean
  */
  public boolean isExecuteInternalCommit() {
    return executeInternalCommit;
  }

 /**
  * setTryUpdate(boolean tryUpdate).
  * @param tryUpdate String
  */
  public void setTryUpdate(boolean tryUpdate) {
    this.tryUpdate = tryUpdate;
  }

 /**
  * isTryUpdate().
  * @return boolean
  */
  public boolean isTryUpdate() {
   return tryUpdate;
  }

  //...fine Mod. 261
  
  //35912 inizio
  public boolean isGenerateShortNames() {
	   return iGenerateShortNames;
  }
  
  public void setGenerateShortNames(boolean generateShortNames) {
	   iGenerateShortNames = generateShortNames;
  }
  //35912 fine

 /**
  * getAnswer(String question).
  * @param question String
  * @return boolean
  */
  boolean getAnswer(String question) {
    boolean end = false;
    boolean result = false;
    while(!end) {
    	//03679
      System.out.print(question);
      String line = "";
      try {
        line = input.readLine();
      }
      catch(IOException e1) {
        e1.printStackTrace();
      }
      if(line.toLowerCase().equals("s")) {
        result = true;
        end = true;
      }
      else if(line.toLowerCase().equals("n")) {
        result = false;
        end = true;
      }
    }
    return result;
  }

 /**
  * openConnection().
  * @return boolean
  */
  protected boolean openConnection() {
    try {
      String userid = Crypto.decrypt(ConnectionManager.getDefaultUserId(database));
      String password = Crypto.decrypt(ConnectionManager.getDefaultPassword(database));
      Database driver = ConnectionManager.getDefaultDriver(database);
      ConnectionManager.openMainConnection(database, userid, password, driver);
      return true;
    }
    catch(SQLException e1) {
    	//03679
      System.out.println(ResourceLoader.getString(SETUP_RES, "NoDBConnection",  new String[] {database}));//Mod. 9634//"Impossibile connettersi al database " + database);
      System.out.println(e1.getMessage());
      return false;
    }
  }

 /**
  * closeConnection().
  */
  protected void closeConnection() {
    try {
      ConnectionManager.closeAllConnections();
    }
    catch(SQLException e1) {
    	//03679
      System.out.println(ResourceLoader.getString(SETUP_RES, "NoDBCloseConnect",  new String[] {database}));//Mod. 9634//"Impossibile chiudere le connessioni al database " + database);
      System.out.println(e1.getMessage());
    }
  }

 /**
  * setupDDL().
  * @return boolean
  */
  public boolean setupDDL() {
    String fileName = moduleName + DDL_NAME;
    /////////////// SOLO TEST
    //XX//System.out.println("XXXXX Setup.setupDDL moduleName ---- > " + moduleName );
    //XX//System.out.println("XXXXX Setup.setupDDL DDL_NAME  ---- > " + DDL_NAME );
    /////////////// FINE PER TEST
    //Mod. 273
    // Se sono in modalità InsertOnly oppure
    //  sono in modalità Insert/Update e tra i file da ignorare non ci sono *.DDL
    //Processo il file DDLOrder.txt
    boolean res = true;
    /////////////// SOLO TEST
    //System.out.println("XXXXX Setup.setupDDL isTryUpdate ---- > " + isTryUpdate() );
    //System.out.println("XXXXX Setup.setupDDL contains  ---- > " + filesToBeIgnored.contains("*.DDL") );
    //boolean perTest = isTryUpdate() && !filesToBeIgnored.contains("*.DDL");
    //System.out.println("XXXXX Setup.setupDDL && - > " + perTest);
    /////////////// FINE PER TEST
    if (!isTryUpdate() || (isTryUpdate() && !filesToBeIgnored.contains("*.DDL"))) {
      //XX//System.out.println("XXXXX Setup.setupDDL XXXXX " + fileName + " processato");
      DDLProcessor fp = new DDLProcessor(this, fileName);
      fp.setOutputFile(output);
      res = fp.process();
    }
    //Altrimenti ignoro il file DDLOrder.txt
    else {
      System.out.println(ResourceLoader.getString(SETUP_RES, "IgnoreDDLFile"));//Mod. 9634//"Ignoro file order DDL");
    }
    return res;
  }

 /**
  * setupFW().
  * Metodo che chiama sull'oggetto FWProcessor appena creato il metodo
  * <code>process()</code> per processare i file sql con Hdr e Ad.
  * Successivamennte, se non si sono verificati errori (cioè good è true)
  * viene chiamato il metodo <code>processExtFile(String extension)</code>
  * a cui vengono passate le stringhe con le estensioni.
  * Questo metodo rilegge i comandi del file fwSQLOrder.txt e al nome di
  * ogni file, prima dell'estensione -.sql- aggiunge la stringa "_CD", ""_RD" o "_VG.
  * @return boolean - true se la compilazione dei file non ha dato errori <br>
  *        - false se ci sono stati errori
  */
  public boolean setupFW() {

    boolean good = true;
    String fileName = moduleName + FW_NAME;
    FWProcessor fp = new FWProcessor(this, fileName);

    fp.setOutputFile(output);
    good = fp.process();

    //___ se tutto è andato bene processo i file con i componenti
    if (good)
      good = fp.processExtFile(CD);

    //___ se tutto è andato bene processo i file con le relazioni
    if (good)
      good = fp.processExtFile(RD);

    //___ se tutto è andato bene processo i file con i gruppi di validazione
    if (good)
      good = fp.processExtFile(VG);

    //Mod. 11397 inizio
    //___ se tutto è andato bene processo i file con la navigazione
    if (good)
      good = fp.processExtFile(NV);
    //fine 11397

     return good;

  }

//_____________________________________________________\\

//  public boolean setupCD() {
//   String fileName = moduleName + FW_NAME;
//   FWProcessor fp = new FWProcessor(this, fileName);
//   fp.setOutputFile(output);
//   return fp.processExtFile(CD);
//  }

//  public boolean setupRD() {
//   String fileName = moduleName + FW_NAME;
//   FWProcessor fp = new FWProcessor(this, fileName);
//   fp.setOutputFile(output);
//   return fp.processExtFile(RD);
//  }

//  public boolean setupVG() {
//   String fileName = moduleName + FW_NAME;
//   FWProcessor fp = new FWProcessor(this, fileName);
//   fp.setOutputFile(output);
//   return fp.processExtFile(VG);
//  }
//_____________________________________________________\\

 /**
  * setupAdmin(String[] params).
  * @param  params String[]
  * @return boolean
  */
  public boolean setupAdmin(String[] params) {
    String fileName = moduleName + ADMIN_NAME;
    AdminProcessor fp = new AdminProcessor(this, fileName);
    fp.setParams(params);
    fp.setOutputFile(output);
    return fp.process();
  }

 /**
  * setupData().
  * @return boolean
  */
  public boolean setupData() {
    String fileName = moduleName + DATA_NAME;
    DataProcessor fp = new DataProcessor(this, fileName);
    fp.setOutputFile(output);
    return fp.process();
  }

 /**
  * setupExtension().
  * @return boolean
  */
  public boolean setupExtension() {
    String fileName = moduleName + EXTENSION_NAME;
    ExtensionProcessor fp = new ExtensionProcessor(this, fileName);
    fp.setOutputFile(output);
    return fp.process();
  }

 /**
  * createUser().
  */
  protected void createUser() {
    try {
      User u = User.elementWithKey(user, PersistentObject.NO_LOCK);
      if(u == null) {
        UserDB udb = new UserDB();
        System.out.println(ResourceLoader.getString(SETUP_RES, "CreateDBUser",  new String[] {ConnectionManager.getCurrentUser(), ConnectionManager.getCurrentPassword()}));//Mod. 9634//"Creo utente di database "+ ConnectionManager.getCurrentUser() + " con password "+ ConnectionManager.getCurrentPassword());
        udb.setId(ConnectionManager.getCurrentUser());
        udb.setPwdDB(ConnectionManager.getCurrentPassword());
        udb.setDescription("Default user");
        udb.save();

        u = new User();
        u.setId(user);
        //u.setPwd(user);
        u.setPwd(password); //...FIX 2558
        u.setUserName("Amministratore");
        u.setUserDB(udb);
        u.setLanguage("it");
        u.save();

        ConnectionManager.commit();
      }
    }
    catch(SQLException e) {
      e.printStackTrace();
    }
  }

 /**
  * Mod.261
  * Restituisce il vettore con i nomi delle colonne che sono chiave primaria per
  * la tabella ricevuta.
  * @param completeTableName String
  * @return Vector
  */
 public Vector getTableKeys(String completeTableName) {
 //Cerco nella hashTable tableKeys il nome della tabella, se non c'è lo inserisco
    // 3775 DM inizio
    // String schemaName = completeTableName.substring(1,completeTableName.indexOf("."));
    // String tableName = completeTableName.substring(completeTableName.indexOf(".")+1);
    int dotPos = completeTableName.indexOf(".");
    String schemaName = completeTableName.substring(0, dotPos);
    String tableName = completeTableName.substring(dotPos + 1);
    // 3775 DM fine
    Vector keyColumnNames = (Vector) tableKeys.get(tableName);
    if (keyColumnNames == null) {
      keyColumnNames = new Vector();
      try {
        // 9805 DM inizio
        /*
        //Leggo i metadata per le chiavi delle tabelle
        if (metaData == null) {
          metaData = ConnectionManager.getCurrentConnection().getMetaData();
        }
        ResultSet res;
        // Fix 3402
        if (ConnectionManager.getCurrentDatabase() instanceof DB2AS400Database)
          res = readPkForAS400(schemaName,tableName);
        else
          res = metaData.getPrimaryKeys(null, schemaName, tableName);
        // Fine fix 3402
        */
        ResultSet res = null;
        Database db = ConnectionManager.getCurrentDatabase();
        if (db instanceof DB2AS400Database || db instanceof DB2ToolboxDatabase)
          res = readPkForAS400(schemaName, tableName);
        else
        {
          if (metaData == null)
          {
            Connection conn = ConnectionManager.getCurrentConnection();
            // 9990 DM inizio
            /*
            if (db instanceof DB2Database)
              metaData = new DB2v813MetaDataAdapter(conn);
            else
              metaData = conn.getMetaData();
            */
            metaData = db.getDatabaseMetaData(conn);
            // 9990 DM fine
          }
          res = metaData.getPrimaryKeys(null, schemaName, tableName);
        }
        // 9805 DM fine
        while (res.next()) {
          String keyCol = res.getString("COLUMN_NAME").toUpperCase();
          keyColumnNames.add(keyCol);
        }
        res.close();
        //Inserisco la nuova tabella nella hashtable
        tableKeys.put(tableName, keyColumnNames);
      }
      catch(SQLException ex) {
        ex.printStackTrace(Trace.excStream);
      }
    }
    return keyColumnNames;
  }

  /**
   * Lettura primary key dal catalogo dell'AS400. Funziona solo se tutte le
   * tabelle stanno nello schema di THERM !
   * @param schema
   * @param table
   * @return
   * @throws SQLException
   */
  protected ResultSet readPkForAS400(String schema,String table) throws SQLException {
    stmt.getStatement().setString(1,schema);
    stmt.getStatement().setString(2,table);
    return stmt.executeQuery();
  }

 /**
  * Mod.273
  * Restituisce la collezione con i nomi dei file da ignorare durante il setup in
  * modalità IU=Insert/Update, contenuti nel IGNORE_NAME per il module ricevuto.
  * In modalità IO=InsertOnly non legge neppure il file
  * @param module String
  */
  public void setFilesToBeIgnored(String module) {
   //Eseguo solo in modalità InsertUpdate
    if (this.isTryUpdate()) {
      //Costruisco il nome, completo di path, prefissi, ecc., del file da leggere
      LineNumberReader orderFile = null;
      String orderFileName;
      System.out.println("");
      String modulePrefix = "";
      if(!module.equals(SetupFW.BASE_NAME))
        modulePrefix = module + "_";
      if(isLocalPathSetted()) {
        orderFileName = getLocalFullPath() + modulePrefix + IGNORE_NAME;
        if(isServerPathSetted()) {
          orderFile = getFile(orderFileName, null);
        }
        else
          orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "SU_LocaleFileOrder"));//Mod. 9634//"File order LOCALE ");
        if(orderFile == null && isServerPathSetted()) {
          orderFileName = getServerFullPath() + modulePrefix + IGNORE_NAME;
          orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "SU_ServerFileOrder"));//Mod. 9634//"File order SERVER ");
        }
      }
      else if(isServerPathSetted()) {
        orderFileName = getServerFullPath() + modulePrefix + IGNORE_NAME;
        orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "SU_AbsoluteFileOrder"));//Mod. 9634//"File order SERVER ");
      }
      else {
        orderFileName = getAbsoluteFullPath() + modulePrefix + IGNORE_NAME;
        orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "SU_ElaborateOrderFile"));//Mod. 9634//"File order ASSOLUTO ");
      }

      if(orderFile != null) {
        System.out.println(ResourceLoader.getString(SETUP_RES, "IgnoreListFile",  new String[] {orderFileName}));//Mod. 9634//"Trovato file con i nomi file da ignorare " + orderFileName);
        // Leggo il file
        try {
          String line = orderFile.readLine();
          String lineSpc = "";
          String lineGen = "";
          while(line != null) {
            line = line.trim();
            if(!line.startsWith(FileProcessor.COMMENT_TAG) && (!line.equals(""))) {
              //Aggiungo il nome di file da ignorare
              filesToBeIgnored.add(line);
            }
            line = orderFile.readLine();
          }
          orderFile.close();
        }
        catch(IOException e2) {
          output.println(ResourceLoader.getString(SETUP_RES, "NoReadFromOrderFile"));//Mod. 9634//"Impossibile leggere dal file order");
          output.println(ResourceLoader.getString(SETUP_RES, "ErrorInRow",  new String[] { Integer.toString(orderFile.getLineNumber())}));//Mod. 9634//"Errore alla riga " + orderFile.getLineNumber());
          output.println(e2.getMessage());
        }
      }
    }
  }

  /**
   * getFile
   * @param fileName String
   * @param message String
   * @return LineNumberReader
   */
  //Mod. 273
  public LineNumberReader getFile(String fileName, String message) {
    try {
      LineNumberReader file = new LineNumberReader(new FileReader(fileName));
      return file;
    }
    catch(FileNotFoundException e1) {
      if(message != null)
        System.out.println(message + fileName + ResourceLoader.getString(SETUP_RES, "SU_NotPresent"));//Mod. 9634//" non presente");//03679
      return null;
    }
  }

 /**
  * Main della classe.
  * @param argv String[]
  */
  public static void main(String argv[]) {
    if(argv.length < 1 || argv.length > 4) {
      System.out.println(ResourceLoader.getString(SETUP_RES, "SU_Use=Usage"));//Mod. 9634//"Use: JAVA com.thera.thermfw.util.SetupFramework <database> [<application>] [<data file path>]");
      System.out.println(ResourceLoader.getString(SETUP_RES, "SU_db"));//Mod. 9634//" <database>    = database name");
      System.out.println(ResourceLoader.getString(SETUP_RES, "SU_appl"));//Mod. 9634//" <application>  = application name");
      System.out.println(ResourceLoader.getString(SETUP_RES, "SU_dataFP"));//Mod. 9634//" <data file path> = full path for data files (BuildOrder.txt - FillBaseOrder.txt - FillApplOrder.txt)");
      System.exit(0);
    }

    String database = argv[0];
    String applName = null;
    if(argv.length == 2)
      applName = argv[1];
    String path = "." + File.separator;
    if (argv.length == 3)
      path = argv[2];

    //SetupFramework s = new SetupFramework(database,applName,path);
    //s.run();
    System.exit(0);
  }

//05488 CB inizio
  public void initDbDir(Database db){
    if (db instanceof DB2AS400Database || db instanceof DB2ToolboxDatabase) {
      setDBDir(Setup.AS);
    }
    else if (db instanceof DB2Database) {
      setDBDir(Setup.DB2);
    }
    else if (db instanceof InformixDatabase) {
      setDBDir(Setup.INFORMIX);
    }
    else if (db instanceof OracleDatabase) {
      setDBDir(Setup.ORACLE);
    }
    else if (db instanceof SQLServerDatabase) {
      setDBDir(Setup.SQLSERVER);
    }
  }
//05488 CB fine

}

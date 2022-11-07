package com.thera.thermfw.setup;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import Zql.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.dict.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.setup.gui.*;

/**
 * Processa i file per eseguire i setup.
 * <br><br><b>Copyright: (C) Thera SpA</b>
 * @author Davide Bottarelli 00/00/2000
 */
/* Revisions:
 * Number     Date         Owner   Description
 * 00014      12/07/2001   LP      Aggiunto metodo per processare i file con i componenti,
 *                                 con le relazioni e con i gruppi di validazione.
 * 00058      02/10/2001   LP      Aggiunto riga per evitare che, se i file non trovati sono
 *                                 quelli con estensione VG/CD/RD, venga inserita nel file setupReport
 *                                 la scritta: "Impossibile trovare il file di comandi ..." (vedi //.....#)
 * 00261      23/05/2002   ES      Se insert fallisce tento l'update
 *                                 Aggiunto parametro FC = frequenza commit
 *                                 Aggiunto parametro MI = modalità di inserimento (IO=inputOnly/IU=insert o update)
 * 00332      22/07/2002   EM      Inserita la stampa del codice SQL degli errori
 * 00476      02/02/2003   Ryo     Nei file DDL vengono considerate come righe di commento anche quelle che iniziano con il tag "--"
 * 00905      22/10/2003   IT      Aggiunto possibilità di decidere per quale applicazione eseguire le fix.
 * 01107      25/11/2003   IT      Aggiunto Continue per istruzioni contenenti DROP
 * 01315      29/01/2004   IT      Ora il CONTINUE può essere messo a qualsiasi istruzione
 * 02226      08/07/2004   IT      Intercettato errore Update che non aggiorna record (in DB2 non generava exception)
 * 03775      16/05/2005   DM      Sistemati problemi di blank in fromInserToUpdateCommand()
 * 03679      27/05/2005   MM      Diretto l'output su System.out
 * 03907      13/05/2005   Ryo     Introdotta la struttura per intercettare istruzioni SQL per eventuali manipolazioni
 *                                 Implementata l'intercettazione dell'istruzione DROP TABLE per prima eliminare i constraint verso la tabella da droppare
 * 03913      14/06/2005   DM      Riparazione danni causati da rollback automatico su errore di SQL Server
 * 04648      23/11/2005   Ryo     Nel comando di CREATE TABLE deve essere presente il tag <TBS>
 * 05504      08/06/2006   CB      Introdotto meccanismo per l'interpretazione dei file TDDML
 * 05782      02/08/2006   Ryo     Aggiunto metodo initalize() in modo che la classe sia istanziabile via factory
 * 06029      10/10/2006   CB      Corretta generazione per as-400
 * 05629      27/09/2006   DM      Uso della OracleCommandHistory
 * 06057      13/10/2006   CB      Impostato ErrorHandler per il parser TDDML
 * 06274      21/11/2006   DM      Aggiunto metodi ridefinibili declassErrorToWarning() e ignoreError() per gestire gli errori.
 *                                 Eliminato problema su "values" in minuscolo.
 * 06400      13/12/2006   Ryo     Corretto il procedimento di inserimento del tablespace di default in internalSQLParseWithTags
 * 06504      11/01/2007   Ryo     Eliminato un punto e virgola prima dell'inserimento del tablespace di default
 * 06932      21/03/2007   ES      Il non riuscire a trasformare la insert duplicata in update è un error che deve far fermare il setup
 *                                 Ma se c'è il tag CONTINUE si deve trasformare in warning e proseguire
 * 07281      10/05/2007   DM      Miglioramento segnalazione di record duplicato
 * 07935      06/09/2007   DF      Aggiunto metodo getOutput
 * 08299      21/11/2007   DM      Introduzione del parser ZQL per passare da INSERT a UPDATE
 * 08596      23/01/2008   DM      Gestito caso di sole colonne chiave nel nuovo passaggio da INSERT a UPDATE
 * 09394      18/06/2008   CB      Gestito caso presenza di funzione DATE quando passa da ZQL parser
 * 09990      06/11/2008   DM      Uso di Database.getMetaData()
 * 09634      31/07/2008   ES      Sostituisco le stringhe cablate con stringhe lette da file risorse
 * 11397      28/09/2009   ES      Modificato solo i commenti, per inserire la gestione dei file _NAV per la navigazione
 * 16550      12/06/2012   ES      Evitare eccezione eseguendo UPDATE con <continue> che genera Riga duplicata
 * 28128      26/10/2018   WL      Ignora l'errore quando il file non contiene il tag <CONTINUOUS>
 * 35912      02/11/2022   RA	   Gestione attributo GenerateShortNames
 */
/*********************************************************************
 * ATTENZIONE!
 * DALLA FIX 3913
 * CHIUNQUE ABBIA BISOGNO DI CHIAMARE COMMIT() O ROLLBACK() DEVE USARE
 * I METODI DI QUESTA CLASSE E NON QUELLI DEL CONNECTION MANAGER
 *********************************************************************/

public abstract class FileProcessor implements ErrorHandler {  //#### CB
	/**
	 * Mod. 9634:File di risorse.
	 */
	public static final String SETUP_RES = "com.thera.thermfw.setup.resources.Setup";

	public static final String ERR_STR = ResourceLoader.getString(SETUP_RES, "ErrorString");//Mod. 9634
	public static final String WARN_STR = ResourceLoader.getString(SETUP_RES, "WarningString");//Mod. 9634

	/**
	 * Costanti simboliche.
	 */
	protected static final String COMMENT_TAG = "#";
	protected static final String SQL_COMMENT_TAG = "--"; // Fix 476 Ryo
	protected static final String EOL_TAG = ";";

	protected static final String SQL_DROP = "DROP"; // Fix 3907 Ryo
	protected static final String SQL_CREATE = "CREATE"; // Fix 4648 Ryo
	protected static final String SQL_TABLE = "TABLE"; // Fix 3907 Ryo

	protected Setup setup;
	protected TagProcessor tagProcessor;
	protected LineNumberReader orderFile;
	protected LineNumberReader commandFile;
	protected PrintWriter output;
	protected CachedStatement statement = null;

	protected Vector duplicatedCommands = new Vector();

	protected String[] params;
	protected String endCommandPath = "";
	protected String genCommandPath = "";
	protected String stdCommandPath = "";
	protected String fullCommandPath;
	protected String fileName;

	protected int errorCount = 0;
	protected int warningCount = 0; //Mod.905
	protected int duplicatedRows = 0;

	protected boolean commitEachFile = false;
	protected boolean insertComment = true; //.....#

	//protected boolean isDeleteCommand = false;Mod.1315

	//Mod. 261
	//Ogni quanti statement viene fatta una commit. MAX_VALUE= solo a fine setup
	protected int stmtBeforeCommit = Integer.MAX_VALUE;

	//Eseguire commit ogni n=stmtBeforeCommit statement. False = non eseguire commit interni
	protected boolean executeInternalCommit = false;
	protected boolean iGenerateShortNames;//35912
	protected static final String TDDML_START_LINE = "<?xml";  //05504

	//Nel caso l'INSERT fallisca provare un UPDATE. Flase = non provare.
	//protected boolean tryUpdate = false;
	//fine Mod. 261

	/**
	 * Costruttore.
	 * FileProcessor(Setup setup, String fileName).
	 * @param Setup setup
	 * @param String fileName
	 */
	/*
	 * Revisions:
	 * Number     Date         Owner   Description
	 * 00261      23/05/2002   ES      Aggiunto parametro FC = frequenza commit
	 */
	public FileProcessor(Setup setup, String fileName)
	{
		initialize(setup, fileName);
	}

	/* Revisions:
	 * Number     Date         Owner   Description
	 * 05782      02/08/2006   Ryo     Costruttore vuoto
	 */
	public FileProcessor()
	{
	}

	/* Revisions:
	 * Number     Date         Owner   Description
	 * 05782      02/08/2006   Ryo     Spostato qui il contenuto del costruttore parametrico
	 */
	public void initialize(Setup setup, String fileName)
	{
		this.fileName = fileName;
		this.setup = setup;
		this.iGenerateShortNames = setup.iGenerateShortNames;//35912
		tagProcessor = new TagProcessor(setup);
		//Mod. 261
		stmtBeforeCommit = setup.getCommitFrequency();
		if (stmtBeforeCommit != Integer.MAX_VALUE) {
			System.out.println(ResourceLoader.getString(SETUP_RES, "StmntNrBeforeCommit",  new String[] {String.valueOf(executeInternalCommit)}));//Mod. 9634//"Numero di statement prima di commit: " +
					//Mod. 9634//stmtBeforeCommit);
		}
		else {
			System.out.println(ResourceLoader.getString(SETUP_RES, "CommitAtFileEnd"));//Mod. 9634//"Commit a fine file");
		}
		executeInternalCommit = setup.isExecuteInternalCommit();
		System.out.println(ResourceLoader.getString(SETUP_RES, "ExecuteInternalCommit",  new String[] {String.valueOf(executeInternalCommit)}));//Mod. 9634//"Eseguire commit interni: " + executeInternalCommit);
		//fine Mod. 261
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
	 * Metodo astratto decisionOnError().
	 */
	public abstract boolean decisionOnError();

	/**
	 * Metodo astratto postProcessingOrderFile().
	 */
	public abstract void postProcessingOrderFile();

	/**
	 * Metodo astratto goodResult().
	 */
	public abstract void goodResult();

	/**
	 * setParams(String[] params).
	 * @param String[] params
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

	/**
	 * setOutputFile(PrintWriter output).
	 * @param PrintWriter output
	 */
	public void setOutputFile(PrintWriter output) {
		this.output = output;
	}

	/**
	 * process().
	 * @return boolean
	 */
	public boolean process() {
		boolean good = true;
		insertComment = true;
		if (checkOrderFilePresence()) {
			good = processOrderFile();
			if (!good) {
				good = decisionOnError();
			}
			else {
				goodResult();
			}
			if (good) {
				postProcessingOrderFile();
			}
		}
		return good;
	}

//	_______________________________AGGIUNTO DA LP______________________________________\\
	/**
	 * Aggiunto metodo a cui passo un parametro.
	 * Lo chiamo se devo processare file a cui deve essere aggiunta un'estensione.
	 * La stringa che passo corrisponde all'estensione.
	 * Esempio: (se passo la stringa "_CD") <br>
	 * Il metodo <code>process()</code> processerebbe il file language.sql,
	 * questo invece processa il file language_CD.sql.
	 * @param String extension estensione da aggiungere al file.
	 * @return boolean true se la compilazione del file non ha generato errori.
	 */
	public boolean processExtFile(String extension) {
		boolean good = true;
		insertComment = false;
		output.println("");
		output.println(
		"------------------------------------------------------------");
		output.println(ResourceLoader.getString(SETUP_RES, "StartFileElab",  new String[] {extension}));//Mod. 9634//"Inizio elaborazione dei file di comandi con estensione " +
				//Mod. 9634//extension);
		if (checkOrderFilePresence()) {
			good = processFileWithExtension(extension);
			if (!good) {
				good = decisionOnError();
			}
			else {
				goodResult();
			}
			if (good) {
				postProcessingOrderFile();
			}
		}
		output.println("");
		output.println(ResourceLoader.getString(SETUP_RES, "EndFileElab",  new String[] {extension}));//Mod. 9634//"Fine elaborazione dei file di comandi con estensione " +
				//Mod. 9634//extension);
		output.println(
		"------------------------------------------------------------");
		return good;
	}

//	___________________________________________________________________________________\\

	/**
	 * checkOrderFilePresence().
	 * @return boolean
	 */
	public boolean checkOrderFilePresence() {
		orderFile = null;
		String orderFileName;
		System.out.println("");
		if (setup.isLocalPathSetted()) {
			orderFileName = setup.getLocalFullPath() + fileName;
			if (setup.isServerPathSetted()) {
				orderFile = getFile(orderFileName, null);
			}
			else {
				orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "LocaleFileOrder"));//Mod. 9634//"File order LOCALE ");
			}
			if (orderFile == null && setup.isServerPathSetted()) {
				orderFileName = setup.getServerFullPath() + fileName;
				orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "ServerFileOrder"));//Mod. 9634//"File order SERVER ");
			}
		}
		else if (setup.isServerPathSetted()) {
			orderFileName = setup.getServerFullPath() + fileName;
			orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "ServerFileOrder"));//Mod. 9634//"File order SERVER ");
		}
		else {
			orderFileName = setup.getAbsoluteFullPath() + fileName;
			orderFile = getFile(orderFileName, ResourceLoader.getString(SETUP_RES, "AbsoluteFileOrder"));//Mod. 9634//"File order ASSOLUTO ");
		}

		if (orderFile != null) {
			System.out.println(ResourceLoader.getString(SETUP_RES, "ElaborateOrderFile",  new String[] {orderFileName}));//Mod. 9634//"Elaborazione file order " + orderFileName);
		}
		return orderFile != null;
	}

	/**
	 * getFile(String fileName, String message).
	 * @param String fileName
	 * @param String message
	 * @return LineNumberReader
	 */
	public LineNumberReader getFile(String fileName, String message) {
		try {
			LineNumberReader file = new LineNumberReader(new FileReader(fileName));
			return file;
		}
		catch (FileNotFoundException e1) {
			if (message != null) {
				System.out.println(message + fileName + ResourceLoader.getString(SETUP_RES, "NotPresent"));//Mod. 9634//" non presente"); //03679
			}
			return null;
		}
	}

	/**
	 * getSpecificFileName(String line).
	 * @param String line
	 * @return String
	 */
	protected String getSpecificFileName(String line) {
		String db = setup.getDBDir().substring(0, setup.getDBDir().length() - 1);
		String platform = setup.getPlatformType().substring(0,
				setup.getPlatformType().length() - 1);
		String fileName = line.substring(0, line.lastIndexOf("."));
		String fileExtension = line.substring(line.lastIndexOf("."));
		return (fileName + "_" + db + "_" + platform + fileExtension);
	}

	/**
	 * getGenericFileName(String line).
	 * @param String line
	 * @return String
	 */
	protected String getGenericFileName(String line) {
		String db = setup.getDBDir().substring(0, setup.getDBDir().length() - 1);
		String fileName = line.substring(0, line.lastIndexOf("."));
		String fileExtension = line.substring(line.lastIndexOf("."));
		return (fileName + "_" + db + fileExtension);
	}

	/**
	 * processOrderFile().
	 * @return boolean
	 */
	public boolean processOrderFile() {
		errorCount = 0;
		warningCount = 0; //Mod.905
		try {
			String line = orderFile.readLine();
			String lineSpc = "";
			String lineGen = "";
			while (line != null) {
				line = line.trim();
				//Mod. 273
				if (!line.startsWith(COMMENT_TAG) && !line.startsWith(SQL_COMMENT_TAG) && // Fix 476 Ryo
						(!line.equals("")) &&
						(!setup.isTryUpdate() ||
								(setup.isTryUpdate() && !Setup.filesToBeIgnored.contains(line))))
					//(setup.isTryUpdate() && !setup.filesToBeIgnored.contains(line)))
				{

					//Mod.905 - ini
					// Elimino i TAGS dalla riga
					String newLine = tagProcessor.replaceTags(line);
					// Controllo se la riga è da eseguire o meno
					if (tagProcessor.isRunnable()) {
						if (!endCommandPath.equals("") &&
								endCommandPath.substring(0, 3).equals("ddl")) {
							lineGen = getGenericFileName(newLine.trim());
							lineSpc = getSpecificFileName(newLine.trim());
						}
						processOrderFileLine(newLine, lineGen, lineSpc);
					}
					//Mod.905 - ini

					if (commitEachFile && errorCount == 0) {

						// 3913 DM inizio
						// ConnectionManager.commit();
						commit();
						// 3913 DM fine
					}
				}
				line = orderFile.readLine();
			}
			if (errorCount == 0) {

				// 3913 DM inizio
				// ConnectionManager.commit();
				commit();
				// 3913 DM fine
			}
			else {

				// 3913 DM inizio
				// ConnectionManager.rollback();
				rollback();
				// 3913 DM fine
			}
			orderFile.close();
			showResult();
		}
		catch (IOException e2) {
			output.println(ResourceLoader.getString(SETUP_RES, "NoReadFromOrderFile"));//Mod. 9634//"Impossibile leggere dal file order");
			output.println(ResourceLoader.getString(SETUP_RES, "ErrorInRow",  new String[] { String.valueOf(orderFile.getLineNumber())}));//Mod. 9634//"Errore alla riga " + orderFile.getLineNumber());
			output.println(e2.getMessage());
			errorCount++;
		}
		catch (SQLException e3) {
			output.println(ResourceLoader.getString(SETUP_RES, "NoCommit"));//Mod. 9634//"Impossibile eseguire la commit ");
			output.println(e3.getMessage());
			errorCount++;
		}
		return (errorCount == 0);
	}

//	_______________________________AGGIUNTO DA LP______________________________________\\
	/**
	 * Metodo che processa i file con i componenti, con le relazioni e con i gruppi
	 * di validazione.
	 * I nomi dei file da processare sono quelli con estesione <code>.sql</code> a cui
	 * vengono aggiunte le stringhe:<br>
	 * - "_CD" = per i file che contengono i componenti;<br>
	 * - "_RD" = per i file che contengono le ralazioni;<br>
	 * - "_VG" = per i file che contengono i gruppi di validazione.
	 * - "_NAV" = per i file che contengono la navigazione (Mod. 11397).
	 * @param String extension E' l'estensione del file ( "_CD", "_RD", "_VG", "_NAV")
	 * @return boolean true se è andato tutto bene
	 *                 false se ci sono stati errori
	 */
	public boolean processFileWithExtension(String extension) {
		errorCount = 0;
		try {
			String line = orderFile.readLine();
			String lineSpc = "";
			String lineGen = "";

			while (line != null) {
				line = line.trim();
				//Mod. 273
				if (!line.startsWith(COMMENT_TAG) && !line.startsWith(SQL_COMMENT_TAG) &&
						(!line.equals("")) && // Fix 476 Ryo
						(!setup.isTryUpdate() ||
								(setup.isTryUpdate() && !Setup.filesToBeIgnored.contains(line)))) {
					StringTokenizer st = new StringTokenizer(line, ".");
					String lineStart = st.nextToken();
					String lineEnd = st.nextToken();

					String lineGenStart = "";
					String lineGenEnd = "";
					if (lineGen != null && !lineGen.equals("")) {
						StringTokenizer gn = new StringTokenizer(lineGen, ".");
						lineGenStart = gn.nextToken();
						lineGenEnd = gn.nextToken();
					}

					String lineSpcStart = "";
					String lineSpcEnd = "";
					if (lineSpc != null && !lineSpc.equals("")) {
						StringTokenizer sp = new StringTokenizer(lineSpc, ".");
						lineSpcStart = sp.nextToken();
						lineSpcEnd = sp.nextToken();
					}

					processOrderFileLine(lineStart + extension + "." + lineEnd,
							lineGenStart + extension + "." + lineGenEnd,
							lineSpcStart + extension + "." + lineSpcEnd);

					if (commitEachFile && errorCount == 0) {

						// 3913 DM inizio
						// ConnectionManager.commit();
						commit();
						// 3913 DM fine
					}
				}
				line = orderFile.readLine();
			}
			if (errorCount == 0) {

				// 3913 DM inizio
				// ConnectionManager.commit();
				commit();
				// 3913 DM fine
			}
			else {

				// 3913 DM inizio
				// ConnectionManager.rollback();
				rollback();
				// 3913 DM fine
			}
			orderFile.close();
			showResult();
		}
		catch (IOException e2) {
			output.println(ResourceLoader.getString(SETUP_RES, "NoReadFromOrderFile"));//Mod. 9634//"Impossibile leggere dal file order");
			output.println(ResourceLoader.getString(SETUP_RES, "ErrorInRow",  new String[] { String.valueOf(orderFile.getLineNumber())}));//Mod. 9634//"Errore alla riga " + orderFile.getLineNumber());
			output.println(e2.getMessage());
			errorCount++;
		}
		catch (SQLException e3) {
			output.println(ResourceLoader.getString(SETUP_RES, "NoCommit"));//Mod. 9634//"Impossibile eseguire la commit ");
			output.println(e3.getMessage());
			errorCount++;
		}
		return (errorCount == 0);
	}

//	___________________________________________________________________________________\\

	/**
	 * processOrderFileLine(String lineStandard, String lineGeneric, String lineSpecific).
	 * @param String lineStandard
	 * @param String lineGeneric
	 * @param String lineSpecific
	 */
	public void processOrderFileLine(String lineStandard, String lineGeneric,
			String lineSpecific) {
		String fullCommandFileName = null;
		String commandFileName = null;
		if (lineSpecific != null && !lineSpecific.equals("")) {
			commandFileName = lineSpecific;
			fullCommandFileName = checkCommandFilePresence(commandFileName, false);
		}
		if (fullCommandFileName == null && lineGeneric != null &&
				!lineSpecific.equals("")) {
			commandFileName = lineGeneric;
			fullCommandFileName = checkCommandFilePresence(commandFileName, false);
		}
		if (fullCommandFileName == null) {
			commandFileName = lineStandard;
			fullCommandFileName = checkCommandFilePresence(commandFileName, true);

			if (fullCommandFileName == null) {
				if (insertComment) {
					output.println(ResourceLoader.getString(SETUP_RES, "NoCommandFileFound",  new String[] {commandFileName}));//Mod. 9634//"Impossibile trovare il file di comandi " +
							//Mod. 9634//commandFileName);
				}
				return;
			}
		}
		processCommandFile(fullCommandFileName);

	}

//	5504 CB inizio
	/**
	 * Processa il file di tipo TDDML
	 * @param fileName String
	 * @return boolean
	 */
	/* Revisions:
	 * Number     Date         Owner   Description
	 * 06400      14/12/2006   Ryo     Revocata l'impostazione della non parametricità degli statement generati
	 */
	public boolean processTDDMLFile(String fileName){
		try {
			String platform = DDLGenerator.PLATF_DB2; //default
			if (ConnectionManager.getCurrentDatabase() instanceof DB2AS400Database
					|| ConnectionManager.getCurrentDatabase() instanceof DB2ToolboxDatabase) //06029 CB
				platform = DDLGenerator.PLATF_DB2_400_SHORT;   //06029 CB genero short
			else if (ConnectionManager.getCurrentDatabase() instanceof DB2Database)
				platform = DDLGenerator.PLATF_DB2;
			else if (ConnectionManager.getCurrentDatabase() instanceof SQLServerDatabase)
				platform = DDLGenerator.PLATF_SQL_SERVER;
			else if (ConnectionManager.getCurrentDatabase() instanceof Oracle8Database)
				platform = DDLGenerator.PLATF_ORACLE;

			DDLGenerator generator = DDLGenerator.getInstance(platform);
			//35912 inizio
			if(generator instanceof DDLGeneratorDB2ASShort) {
				((DDLGeneratorDB2ASShort) generator).setGenerateShortNames(setup.isGenerateShortNames());
			}
			//35912 fine
			DDLExecutor executor = new DDLExecutorFileProcessor(this);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder parser = factory.newDocumentBuilder();
			parser.setErrorHandler(this); //6057 CB
			Document doc = parser.parse(fileName);
//			generator.setParametric(false);	// Fix 6400 Ryo
			generator.generate(doc, executor);
		}
		// 5629 DM inizio
		catch (IllegalArgumentException e) {
			throw e;
		}
		// 5629 DM fine
		catch (Exception e) {
			output.println(ResourceLoader.getString(SETUP_RES, "ErrorFoundReadingTDDML",  new String[] {fileName}));//Mod. 9634//"Errore durante la lettura del file TDDML " + fileName);
			output.println(e.getMessage());
			e.printStackTrace();
			errorCount++;
			return false;
		}

		return true;
	}
//	5504 CB fine

	/**
	 * checkCommandFilePresence(String fileName, boolean findAlsoInStd).
	 * @param String fileName
	 * @param boolean findAlsoInStd
	 * @return String
	 */
	public String checkCommandFilePresence(String fileName, boolean findAlsoInStd) {
		if (fileName == null || fileName.equals("")) {
			return null;
		}
		commandFile = null;
		String commandFileName;
		if (setup.isLocalPathSetted()) {
			commandFileName = setup.getLocalFullPath() + endCommandPath + fileName;
			commandFile = getFile(commandFileName, null);
			if (commandFile == null) {
				commandFileName = setup.getLocalFullPath() + genCommandPath + fileName;
				commandFile = getFile(commandFileName, null);
			}
			if (commandFile == null && findAlsoInStd) {
				commandFileName = setup.getLocalFullPath() + stdCommandPath + fileName;
				commandFile = getFile(commandFileName, null);
			}
			if (commandFile == null && setup.isServerPathSetted()) {
				commandFileName = setup.getServerFullPath() + endCommandPath + fileName;
				commandFile = getFile(commandFileName, null);
				if (commandFile == null) {
					commandFileName = setup.getServerFullPath() + genCommandPath +
					fileName;
					commandFile = getFile(commandFileName, null);
				}
				if (commandFile == null && findAlsoInStd) {
					commandFileName = setup.getServerFullPath() + stdCommandPath +
					fileName;
					commandFile = getFile(commandFileName, null);
				}
			}
		}
		else if (setup.isServerPathSetted()) {
			commandFileName = setup.getServerFullPath() + endCommandPath + fileName;
			commandFile = getFile(commandFileName, null);
			if (commandFile == null) {
				commandFileName = setup.getServerFullPath() + genCommandPath + fileName;
				commandFile = getFile(commandFileName, null);
			}
			if (commandFile == null && findAlsoInStd) {
				commandFileName = setup.getServerFullPath() + stdCommandPath + fileName;
				commandFile = getFile(commandFileName, null);
			}
		}
		else {
			commandFileName = setup.getAbsoluteFullPath() + endCommandPath + fileName;
			commandFile = getFile(commandFileName, null);
			if (commandFile == null) {
				commandFileName = setup.getAbsoluteFullPath() + genCommandPath +
				fileName;
				commandFile = getFile(commandFileName, null);
			}
			if (commandFile == null && findAlsoInStd) {
				commandFileName = setup.getAbsoluteFullPath() + stdCommandPath +
				fileName;
				commandFile = getFile(commandFileName, null);
			}
		}

		if (commandFile != null) {
			output.println("");
			output.println(ResourceLoader.getString(SETUP_RES, "CmdFileElaboration",  new String[] {commandFileName}));//Mod. 9634//"Elaborazione file comandi " + commandFileName);
			return commandFileName;
		}
		else {
			return null;
		}
	}

	/**
	 * processCommandFile(String fullCommandFileName).
	 * @param String fullCommandFileName
	 */
	/*	public void processCommandFile(String fullCommandFileName)
	 {
		try
		{
		 String line = commandFile.readLine();
		 StringBuffer command = new StringBuffer();
		 while(line != null)
		 {
			line = line.trim();
			if(!line.startsWith(COMMENT_TAG))
			{
			 line = tagProcessor.replaceTags(line);
			 command.append(line);
			 if(line.endsWith(EOL_TAG))
			 {
				executeCommand(command.toString().trim());
				command = new StringBuffer();
			 }
			 else
				command.append(" ");
			}
			line = commandFile.readLine();
		 }
		 commandFile.close();
		}
		catch(IOException e)
		{
		 output.println("Errore durante la lettura del file dei comandi " + fullCommandFileName);
		 output.println(e.getMessage());
		 errorCount++;
		}
	 }
	 int executedCommand = 0;
	 protected void executeCommand(String command)
	 {
		command = command.substring(0, command.length() - 1);
		output.println(command);
		try
		{
		 CachedStatement s = getStatement();
		 s.setStmtString(command);

		 if(params != null)
			for(int i = 0;i< params.length;i++)
			 try
			 {
				s.getStatement().setObject(i+1,params[i]);
			 }
		 catch(SQLException e)
		 {
			break;
		 }
		 s.execute();
		 s.free();
		 executedCommand++;
		 if((executedCommand % 50) == 0)
			System.out.print(".");
		}
		catch(SQLWarning w)
		{
		 String s = w.getMessage();
		 System.out.println("WARNING ");
		 output.println(s);
		 if(!s.endsWith("\n"))
			output.println("");
		}
		catch(SQLException e)
		{
		 String s = e.getMessage();
		 output.println("********** #ERRORE# **********");
		 output.println(s);
		 if(!s.endsWith("\n"))
			output.println("");
		 int sqlCode = e.getErrorCode();
		 boolean onDup = false;
		 if(ConnectionManager.getCurrentDatabase() instanceof DB2Database)
		 {
			if(sqlCode == Setup.DB_DUPLICATED_ROW_ERROR)
			 onDup = true;
		 }
		 if(onDup)
		 {
			//              output.println("********** #ERRORE# **********");
			duplicatedRows++;
			duplicatedCommands.addElement(command);
		 }
		 else
		 {
			errorCount++;
			//                  output.println("********** #ERRORE# **********");
		 }
		}
	 }*/

	/**
	 * processCommandFile(String fullCommandFileName).
	 * @param String fullCommandFileName
	 */
	/* Revisions:
	 * Fix #    Date          Owner      Description
	 * 06627    01/02/2007    Ryo        Prese in considerazione le righe senza tag di fine riga (';')
	 */
	public void processCommandFile(String fullCommandFileName) {
		try {
			String line = commandFile.readLine();
			//05504 CB inizio
			if ((line != null)&&(line.startsWith(TDDML_START_LINE)))
				processTDDMLFile(fullCommandFileName);
			else {
				//05504 CB fine
				StringBuffer command = new StringBuffer();
				while (line != null) {
					line = line.trim();
					if(!line.startsWith(COMMENT_TAG) && !line.startsWith(SQL_COMMENT_TAG)) {	// Fix 476 Ryo
						command.append(line);
						if (line.endsWith(EOL_TAG)) {
							executeCommand(command.toString().trim());
							command = new StringBuffer();
						}
						else {
							command.append(" ");
						}
					}
					line = commandFile.readLine();
				}
			}
			commandFile.close();
		}
		catch (IOException e) {
			output.println(ResourceLoader.getString(SETUP_RES, "CmdFileReadingErr",  new String[] {fullCommandFileName}));//Mod. 9634//"Errore durante la lettura del file dei comandi " +
					//Mod. 9634//fullCommandFileName);
			output.println(e.getMessage());
			errorCount++;
		}
	}

	int executedCommand = 0;

	/**
	 * executeCommand(String command).
	 * @param String command
	 */
	/*
	 * Revisions:
	 * Number     Date         Owner   Description
	 * 00261      23/05/2002   ES      Se insert fallisce tento l'update
	 *                                 Aggiunto parametro FC = frequenza commit
	 *                                 Aggiunto parametro MI = modalità di inserimento (IO=inputOnly/IU=insert o update)
	 * 05504      09/06/2006   CB      Reso public
	 */
	
	//inzio 28128
	public boolean isEligibleToContinue(String command) {
		String partialCommand = command.trim();
		if(!partialCommand.endsWith("<CONTINUE>") && (partialCommand.startsWith("INSERT"))) {
			partialCommand = partialCommand.substring(6).trim();
			if(partialCommand.startsWith("INTO")) {
				partialCommand = partialCommand.substring(4).trim();
				if(partialCommand.startsWith("<SCH>USER_MENU_ITEM") || partialCommand.startsWith("<SCH>USER_MENU_FOLDER")){
					return true;
				}
			}
		}
		return false;
	}
	//fine 28128
	
	//protected void executeCommand(String command) { //05504
	public void executeCommand(String command) { //05504
		//Mod. 261
		String insertCommand = "";
		
		try {
			CachedStatement s = getStatement();
			/* Mod.1315
			 Commentato controllo perchè ora il CONTINUE funziona per tutte le istruzioni
			 Non è più necessario sapere se è un'istruzione di DELETE o DROP
			 // Mod.905 - ini
				if (command.trim().substring(0,6).equalsIgnoreCase("DELETE") ||
				command.trim().substring(0,4).equalsIgnoreCase("DROP") ||
				command.trim().indexOf(" DROP ") != -1) //Mod.1107
				isDeleteCommand = true;
				else
				isDeleteCommand = false;
			 */
			
//			Fix 6400 Ryo inizio
//			if(internalSQLParserWithTags(command)) {
			command = internalSQLParseWithTags(command);
			
			if(command != null)
			{
//				Fix 6400 Ryo fine
				
				//28128 inizio
				if(isEligibleToContinue(command)) {
					command = command.concat("<CONTINUE>"); 
				}
			  //28128
				
				tagProcessor.buildStmt(s, command, params);
				if (tagProcessor.isRunnable()) {
					output.println(s.getStmtString());
					//Mod. 261
					insertCommand = s.getStmtString();
					String memoryHog = s.getStmtString(); // Fix 3907 Ryo
					boolean continueAnyway = internalSQLParser(insertCommand); // Fix 3907 Ryo
					s.setStmtString(memoryHog); // Fix 3907 Ryo
					
					if (continueAnyway) { // Fix 3907 Ryo
						//Mod.2226 - ini
						//s.execute();
						// 5629 DM inizio
						if (OracleCommandHistory.isActive()) {
							int oracleStatus = OracleCommandHistory.getCommandStatus(insertCommand);
							if (oracleStatus == OracleCommandHistory.IN_SEQUENCE) {
								// il comando era già stato eseguito e confermato, me ne vado
								s.free();
								return;
							}
							else if (oracleStatus == OracleCommandHistory.OUT_OF_SEQUENCE) {
								OracleCommandHistory.throwException(fileName, insertCommand);
							}
							// Se siamo qui il comando è da eseguire
							OracleCommandHistory.storeCommand(insertCommand);
							// Il comando è stato salvato.
							// Se il comando è DDL -> viene mandata una commit, e tutti i comandi vengono
							//                        confermati, anche se è sbagliato!
							// Se il comando è SQL
							//     Se riesce -> restiamo in attesa di una successiva commit o rollback
							//     Se non riesce -> come sopra, ma passiamo al tentativo di update
						}
						// 5629 DM fine
						int ret = s.executeUpdate();
						String iniCommand = insertCommand.substring(0, 6);
						if (iniCommand.equalsIgnoreCase("UPDATE") && ret <= 0) {
							throw new SQLException(
									// 6274 DM inizio
									// "SQL EXCEPTION: No rows found; the update is failed.", null,
									"No rows found, the update is failed.", "MenoSei",
									// 6274 DM Fine
									ErrorCodes.NO_ROWS_FOUND);
						}
						//Mod.2226 - fin
					}
				}

				// 3903 DM inizio
				// 5629 DM inizio
				// if (ConnectionManager.getCurrentDatabase() instanceof SQLServerDatabase) {
				if (CommandHistory.isActive()) {
					// 5629 DM fine
					if (isCommit(insertCommand)) {
						CommandHistory.clear();
					}
					else {
						CommandHistory.add(insertCommand);
					}
				}
				// 3903 DM fine
				s.free();
				executedCommand++;
			}
			// Mod.905 - fin
			if ( (executedCommand % 50) == 0) {
				System.out.print(".");
				//Mod. 261
			}
			executeInternalCommit = setup.isExecuteInternalCommit();
			if (executeInternalCommit && (executedCommand % stmtBeforeCommit) == 0) {

				// 3913 DM inizio
				// ConnectionManager.commit();
				commit();
				// 3913 DM fine
				//fine Mod. 261
			}
		}
		catch (SQLWarning w) {
			String s = w.getMessage();
			System.out.println(ResourceLoader.getString(SETUP_RES, "JustWarningStr"));//Mod. 9634//"WARNING ");
			output.println(s);
			if (!s.endsWith("\n")) {
				output.println("");
			}
		}
		catch (SQLException e) {
			String s = e.getMessage();
			/* sposto per non stamparlo se è riga dupl, tento update e update è andato bene
			 output.println("********** #ERRORE# **********");
			 output.println(s);
			 if(!s.endsWith("\n"))
			 output.println("");
			 */
			int sqlCode = e.getErrorCode();
			
			boolean onDup = false;
			//if(ConnectionManager.getCurrentDatabase() instanceof DB2Database) {
			if (sqlCode == Setup.DB_DUPLICATED_ROW_ERROR) {
				onDup = true;
				//In caso di riga duplicata se il parametro insertMode = isert/update
				//provo a trasformare la insert in update, altrimenti segnalo la riga duplicata
				if (setup.isTryUpdate()) {
					//Mod. 261
										// 8299 DM inizio
					// String updateCommand = fromInserToUpdateCommand(insertCommand);
										String updateCommand = fromInsertToUpdateCommand(insertCommand);
										// 8299 DM fine
					if (!updateCommand.equals("")) { //Riesco a passare da insert a update
						try {
							CachedStatement st = new CachedStatement();
							st.setStmtString(updateCommand);
							output.println("---->" + updateCommand);
							//Mod. 261
							st.execute();
							// 3903 DM inizio
							// 5629 DM inizio
							// if (ConnectionManager.getCurrentDatabase() instanceof
							//		SQLServerDatabase) {
							if (CommandHistory.isActive()) {
								// 5629 DM fine
								CommandHistory.add(updateCommand);
							}
							// 3903 DM fine
							st.free();
							executedCommand++;
							if ( (executedCommand % 50) == 0) {
								System.out.print(".");
							}
							executeInternalCommit = setup.isExecuteInternalCommit();
							if (executeInternalCommit &&
									(executedCommand % stmtBeforeCommit) == 0) {

								// 3913 DM inizio
								// ConnectionManager.commit();
								commit();
								// 3913 DM fine
							}
						}
						catch (SQLWarning w) {
							warningCount++;
							String wMsg = w.getMessage();
							System.out.println(ResourceLoader.getString(SETUP_RES, "JustWarningStr"));//Mod. 9634//"WARNING ");
							output.println(wMsg);
							if (!wMsg.endsWith("\n")) {
								output.println("");
							}
						}
						catch (SQLException sqle) {
							//Per ignorare eventuali errori quando si fa UPDATE di record inesistente
							//Capita ad esempio con le voci del menu utente che hanno come chiave primaria
							//un numeratore, quindi l'insert non darebbe mai "Riga duplicata" inserendo ad
							//ogni setup in modalità update una voce di menu nuova, diversa solo per il numeratore
							//Se aggiungo un indice univoco su descrizione, classe ecc. faccio in
							//modo che l'INSERT dia riga duplicata, quindi tento l'update: anche se è sbagliato
							//perché ha come chiave primaria un numeratore non ancora inserito, voglio che
							//non generi un errore che provocherebbe il rollback
							//Alcune versioni di DB2 non genera errore, per gli altri lo gestisco
							try {
								//Rielaboro il codice di errore ricevuto tenendo conto dei diversi database
								//e li riconduco ad un codice unico per ogni tipologia di errore.
								//Se l'errore  non è noto viene rilanciata l'eccezione che verrà
								//gestita come al solito nella catch. Se l'errore noto è diverso da "Duplicate row"
								//lo segnalo come al solito, se è proprio "Duplicate row" lo ignoro.
								int excNr = ConnectionManager.getCurrentDatabase().
								handleException(sqle);

								//Se il codice di errore noto non corrisponde al "DUPLICATE ROW"
								if (excNr != ErrorCodes.NO_ROWS_FOUND) {
									ErrorCodesEnum excECE = ErrorCodesEnum.getEnumFor(excNr);
									output.println(ERR_STR + ResourceLoader.getString(SETUP_RES, "UpdUnknownErr"));//Mod. 9634//"********** #ERRORE# **********" +
									//Mod. 9634//" non gestito su update");
									output.println(ResourceLoader.getString(SETUP_RES, "ErrorCode",  new String[] { Integer.toString(e.getErrorCode())}));//Mod. 9634//"********** CODICE ERRORE = " +
											//Mod. 9634//Integer.toString(e.getErrorCode()) +
									//Mod. 9634//" **********");
									output.println(excECE);
									if (!s.endsWith("\n")) {
										output.println("");
									}
									onDup = false;
								}
							}
							catch (SQLException ee) {
								String sqlMsg = sqle.getMessage();
								output.println(ERR_STR + ResourceLoader.getString(SETUP_RES, "OnUpdate"));//Mod. 9634//"********** #ERRORE# **********" + " su update");
								output.println(ResourceLoader.getString(SETUP_RES, "ErrorCode",  new String[] { Integer.toString(e.getErrorCode())}));//Mod. 9634//"********** CODICE ERRORE = " +
										//Mod. 9634//Integer.toString(e.getErrorCode()) +  ///////////??????????????????????? e o ee.getErrorCode???????????????????
								//Mod. 9634//" **********");
								output.println(sqlMsg);
								if (!s.endsWith("\n")) {
									output.println("");
								}
								onDup = false; //Mod.2226
							}
						}
					}
					
					
					else { //Non riesco a passare da insert a update
					//Mod. 6932 inizio
					//C'è il tag CONTINUE segnalo WARNING e vado avanti
				   
					if (TagProcessor.NoControlForDelete) {
							try {
								System.out.println(WARN_STR + ResourceLoader.getString(SETUP_RES, "UpdNotBuildable"));//Mod. 9634//"********** #WARNING# **********"+" update non ricostruibile");
								System.out.println(e.getMessage());
								if (CommandHistory.isActive() && isAutoRollback(insertCommand)) {
										CommandHistory.reissueAll();
									}
									String eMsg = e.getMessage();
									output.println(WARN_STR + ResourceLoader.getString(SETUP_RES, "UpdNotBuildable"));//Mod. 9634//"********** #WARNING# **********"+" update non ricostruibile");
									output.println(eMsg);
									if (!eMsg.endsWith("\n")) {
										output.println("");
									}
									executedCommand++;
									if ( (executedCommand % 50) == 0) {
										System.out.print(".");
									}
									executeInternalCommit = setup.isExecuteInternalCommit();
									if (executeInternalCommit &&
											(executedCommand % stmtBeforeCommit) == 0) {
											commit();
										}
										output.println("");
										onDup = false;
										warningCount++;
										errorCount--;
									}
									catch (SQLException ee) {
										output.println(ERR_STR);
										output.println(ResourceLoader.getString(SETUP_RES, "ErrorCode",  new String[] { Integer.toString(ee.getErrorCode())}));//Mod. 9634//"********** CODICE ERRORE = " + Integer.toString(ee.getErrorCode()) + " **********");
										output.println(s);
										if (!s.endsWith("\n")) {
											output.println("");
										}
										onDup = false;
									}
								}
							else { //Non c'è il tag CONTINUE
								//fine 6932
								output.println(ERR_STR + ResourceLoader.getString(SETUP_RES, "UpdNotBuildable"));//Mod. 9634//"********** #ERRORE# **********" +
															 //Mod. 9634//" update non ricostruibile");
								output.println(ResourceLoader.getString(SETUP_RES, "ErrorCode",  new String[] { Integer.toString(e.getErrorCode())}));//Mod. 9634//"********** CODICE ERRORE = " +
															 //Mod. 9634//Integer.toString(e.getErrorCode()) + " **********");
								onDup = false; //Mod.6932
							} //Mod. 6932
					}
				}
				else {
					output.println(ERR_STR);//Mod. 9634//"********** #ERRORE# **********"); //riga duplicata, in modalità IO
					output.println(ResourceLoader.getString(SETUP_RES, "ErrorCode",  new String[] { Integer.toString(e.getErrorCode())}));//Mod. 9634//"********** CODICE ERRORE = " +
							//Mod. 9634//Integer.toString(e.getErrorCode()) + " **********");
					output.println(s);
					if (!s.endsWith("\n")) {
						output.println("");
					}
				}
			}
			//Mod. 905 - ini: se sull'istruzione c'è il tag <CONTINUE> continua ignorando l'errore
			// 6274 DM inizio
			// else if ( /*Mod.1315 isDeleteCommand &&*/TagProcessor.NoControlForDelete) {
			else if (TagProcessor.NoControlForDelete || declassErrorToWarning(e, insertCommand)) {
				// 6274 DM fine
				
				
				try {
					// 3913 DM inizio
					System.out.println(WARN_STR);//Mod. 9634//"********** #WARNING# **********");
					System.out.println(e.getMessage());
					// 5629 DM inizio
					// if (ConnectionManager.getCurrentDatabase() instanceof
					//		SQLServerDatabase && isAutoRollback(insertCommand)) {
					if (CommandHistory.isActive() && isAutoRollback(insertCommand)) {
						// 5629 DM fine
						CommandHistory.reissueAll();
						// 3913 DM fine
					}
					String eMsg = e.getMessage();
					output.println(WARN_STR);//Mod. 9634//"********** #WARNING# **********");
					output.println(eMsg);
					if (!eMsg.endsWith("\n")) {
						output.println("");
					}
					executedCommand++;
					if ( (executedCommand % 50) == 0) {
						System.out.print(".");
					}
					executeInternalCommit = setup.isExecuteInternalCommit();
					if (executeInternalCommit &&
							(executedCommand % stmtBeforeCommit) == 0) {

						// 3913 DM inizio
						// ConnectionManager.commit();
						commit();
						// 3913 DM fine
					}
					output.println("");
					onDup = false;
					warningCount++;
					errorCount--;
				}
				catch (SQLException ee) {
					output.println(ERR_STR);//Mod. 9634//"********** #ERRORE# **********");
					output.println(ResourceLoader.getString(SETUP_RES, "ErrorCode",  new String[] { Integer.toString(ee.getErrorCode())}));//Mod. 9634//"********** CODICE ERRORE = " +
							//Mod. 9634//Integer.toString(ee.getErrorCode()) + " **********");
					output.println(s);
					if (!s.endsWith("\n")) {
						output.println("");
					}
					onDup = false;
				}
				
			}
			//Mod. 905 - fin
			else {
				// 6274 DM inizio
				
				
	if (ignoreError(e, insertCommand))
					return;
				// 6274 DM fine
				output.println(ERR_STR);//Mod. 9634//"********** #ERRORE# **********"); //errore diverso da: riga duplicata
			
			   output.println(ResourceLoader.getString(SETUP_RES, "ErrorCode",  new String[] { Integer.toString(e.getErrorCode())}));//Mod. 9634//"********** CODICE ERRORE = " +
						//Mod. 9634//Integer.toString(e.getErrorCode()) + " **********");
				output.println(s);
				if (!s.endsWith("\n")) {
					output.println("");
				}
				onDup = false;
				// 5629 DM inizio
				// il revoke potrebbe semplicemente marcare come annullato
				// se non mi piace fare commit in questo punto!
				if (OracleCommandHistory.isActive() && isDDL(insertCommand))
					OracleCommandHistory.revokeLastCommand();
				// 5629 DM fine
			
		
			
			}
			if (onDup) {
				duplicatedRows++;
				//Se provo a fare update non salvo né stampo l'elenco delle righe duplicate!
				if (!setup.isTryUpdate()) {
					duplicatedCommands.addElement(command);
				}
			}
			else {
				errorCount++;
			}
		}

	}

		// 8299 DM inizio
		protected String fromInsertToUpdateCommand(String insertCommand)
		{
				// 5596 DM inizio
				boolean db2KeywordsReplaced = false;
				if (ConnectionManager.getCurrentDatabase() instanceof DB2Database)
				{
						String insertCommand2 = replaceDB2Keywords(insertCommand);
						if (!insertCommand2.equals(insertCommand))
						{
								insertCommand = insertCommand2;
								db2KeywordsReplaced = true;
						}
				}
				// 5596 DM fine
				boolean tokenError = false;
				ZInsert insertStmt = null;
				while (insertStmt == null)
				try
				{
						InputStream is = new ByteArrayInputStream((insertCommand + ";").getBytes());
						ZqlParser p = new ZqlParser(is);
						insertStmt = (ZInsert)p.readStatement();
				}
				//Mod. 16550 inizio
				catch (ClassCastException cce)
				{
					return "";
				}
				//fine mod. 16550
				catch (ParseException e)
				{
						String msg = e.getMessage();
						if (msg.indexOf("Undefined function:") < 0)
								return "";
						else
						{
								int blankPos = msg.lastIndexOf(' ');
								String fun = msg.substring(blankPos + 1);
								ZUtils.addCustomFunction(fun, 10000);
						}
				}
				catch (TokenMgrError e)
				{
						if (tokenError)
								return "";
						insertCommand = Utils.replace(insertCommand, "\u2018", "''");
						insertCommand = Utils.replace(insertCommand, "\u2019", "''");
						insertCommand = Utils.replace(insertCommand, "\u201C", "\"");
						insertCommand = Utils.replace(insertCommand, "\u201D", "\"");
						insertCommand = Utils.replace(insertCommand, "\u2026", "...");
						insertCommand = Utils.replace(insertCommand, "\u2013", "-");
						insertCommand = Utils.replace(insertCommand, "\u2014", "-");
						tokenError = true;
				}
				String tabName = insertStmt.getTable();
				List cols = insertStmt.getColumns();
				List vals = insertStmt.getValues();
				Set keys = new HashSet(setup.getTableKeys(tabName));

				ZUpdate upd = new ZUpdate(tabName);
				ZExp where = null;
				Iterator ci = cols.iterator();
				Iterator vi = vals.iterator();
				while (ci.hasNext() && vi.hasNext())
				{
						String col = (String)ci.next();
						ZExp val = (ZExp)vi.next();
						if (keys.contains(col))
						{
								ZExp wherePart = new ZExpression("=", new ZConstant(col, ZConstant.COLUMNNAME), val);
								if (where != null)
										where = new ZExpression("AND", where, wherePart);
								else
										where = wherePart;
						}
						// 8596 DM inizio
						// else
						// 8596 DM fine
								upd.addColumnUpdate(col, val);
				}
				upd.addWhere(where);

				// 5596 DM inizio
				// return upd.toString();
				String updStr = upd.toString();
				if (db2KeywordsReplaced)
						updStr = restoreDB2Keywords(updStr);
				return updStr;
				// 5596 DM fine
		}
		// 8299 DM fine

		// 5596 DM inizio
		protected String replaceDB2Keywords(String sql)
		{
				sql = Utils.replace(sql, "CURRENT DATE", "CURRENT_DATE");
				sql = Utils.replace(sql, "CURRENT TIME", "CURRENT_TIME");
				sql = Utils.replace(sql, "CURRENT TIMESTAMP", "CURRENT_TIMESTAMP");
				sql = Utils.replace(sql, "DATE('", "DATE_TMP('"); //9394 CB
				return sql;
		}

		protected String restoreDB2Keywords(String sql)
		{
				sql = Utils.replace(sql, "CURRENT_DATE", "CURRENT DATE");
				sql = Utils.replace(sql, "CURRENT_TIME", "CURRENT TIME");
				sql = Utils.replace(sql, "CURRENT_TIMESTAMP", "CURRENT TIMESTAMP");
				sql = Utils.replace(sql, "DATE_TMP('", "DATE('");//9394 CB
				return sql;
		}
		// 5596 DM fine

	/**
	 * Mod. 261
	 * Trasforma il comando di insert (con l'elenco delle colonne) in uno di update
	 * Se ci sono errori nella costruzione dell'update restituisce una stringa vuota
	 */
	protected String fromInserToUpdateCommand(String insertCommand) {
		//SE RIGA DUPLICATA PROVO A FARE UPDATE
		//Inizializzo il comando di update
		String updateCommand = "UPDATE ";
		//... aggiungo il nome della tabella, estraendolo dal comando di insert
		// 6274 DM inizio
		/*
				int indexI = insertCommand.indexOf("INTO");
				int indexF = insertCommand.indexOf("(");
				int indexValue = insertCommand.indexOf(") VALUES (");
				if (indexValue < 1) {
						indexValue = insertCommand.indexOf(")VALUES ");
						if (indexValue < 1) {
								indexValue = insertCommand.indexOf(" VALUES(");
								if (indexValue < 1) {
										indexValue = insertCommand.indexOf(")VALUES(");
								}
						}
				}
		 */
		String uppecaseInsertCommand = insertCommand.toUpperCase();
		int indexI = uppecaseInsertCommand.indexOf("INTO");
		int indexF = uppecaseInsertCommand.indexOf("(");
		int indexValue = uppecaseInsertCommand.indexOf(") VALUES (");
		if (indexValue < 1) {
			indexValue = uppecaseInsertCommand.indexOf(")VALUES ");
			if (indexValue < 1) {
				indexValue = uppecaseInsertCommand.indexOf(" VALUES(");
				if (indexValue < 1) {
					indexValue = uppecaseInsertCommand.indexOf(")VALUES(");
				}
			}
		}
		// 6274 DM fine
		//String completeTableName = insertCommand.substring(indexI+4, indexF-1);
		// 3775 DM inizio
		// String completeTableName = insertCommand.substring(indexI+4, indexF);//Mod.2226
		// updateCommand = updateCommand + completeTableName.trim() + " SET ";
		String completeTableName = insertCommand.substring(indexI + 4, indexF).trim();
		updateCommand = updateCommand + completeTableName + " SET ";

		// 3775 DM fine

		//... estraggo l'elenco colonne e l'elenco valori
		int indexCloseParCol = insertCommand.indexOf(")");
		//... uso la lunghezza della stringa e non cerco ')' come negli altri casi
		//    perché i valori dei campi stringa possono contenere parentesi e generano errori
		int indexCloseParVal = insertCommand.lastIndexOf(")");
		int indexOpenParVal = insertCommand.indexOf("(", indexValue);
		String columnNames = insertCommand.substring(indexF + 1, indexCloseParCol);
		String columnValues = insertCommand.substring(indexOpenParVal + 1,
				indexCloseParVal);

		//... isolo i pezzi con i nomi delle colonne: columnNames e con i valori: columnValues
		columnNames = columnNames + ",";
		columnValues = columnValues + ",";
		//...Spezzo la stringa delle colonne nei singoli pezzi
		Vector colNames = brokeAString(columnNames, ",");
		Vector colValues = this.brokeComplexString(columnValues, ",");
		if (colNames != null && colValues != null &&
				colNames.size() == colValues.size()) {
			//... mi faccio dare le chiavi primarie della tabella
			Vector tabKeys = setup.getTableKeys(completeTableName);
			//... costruisco le condizione di where con le colonne chiavi e i rispetivi valori
			//    e l'assegnazione dei valori alle colonne utilizzando la sintassi
			//    colonna = valore, col1 = val1, ... accettata da tutti i database
			String whereCond = " WHERE ";
			String updCond = "";
			boolean firstKey = true;
			boolean firstField = true;
			for (int ii = 0; ii < colNames.size(); ii++) {
				String col = (String) colNames.elementAt(ii);
				//La colonna è chiave: la metto nella condizione di where
				if (tabKeys.contains(col.trim())) {
					if (!firstKey) {
						whereCond = whereCond + " AND ";
					}
					whereCond = whereCond + "(" + col + " = " + colValues.elementAt(ii) +
					") ";
					firstKey = false;
				}
				//la colonna non è chiave: aggiungo l'assegnazione
				//////////// Tolto per non avere errore nel'esecuzione con tabelle di sole chiavi else
				//{
				if (!firstField) {
					updCond = updCond + ", ";
				}
				updCond = updCond + colNames.elementAt(ii) + " =" +
				colValues.elementAt(ii);
				firstField = false;
				//}
			}
			updateCommand = updateCommand + updCond + whereCond;
		}
		else {
			updateCommand = "";
		}
		return updateCommand;
	}

	/**
	 * Mod. 261
	 * Spezza una stringa del tipo: "xxxx, yyyyy, zzzzz," mettendo i pezzi in un vettore:
	 *  primo elemento= "xxxx" secondo= "yyyyy"  ecc.
	 * brokeAString().
	 * @return Vector
	 */
	protected Vector brokeAString(String str, String separator) {
		Vector bp = new Vector();
		int indexStart = 0;
		int indexEnd = str.indexOf(separator);
		while (indexEnd > 0) {
			String col = str.substring(indexStart, indexEnd);
			bp.add(col);
			indexStart = indexEnd + 1;
			int oldindexEnd = indexEnd;
			indexEnd = str.indexOf(",", oldindexEnd + 1);
		}
		return bp;
	}

	/**
	 * getStatement().
	 * @return CachedStatement
	 */
	protected CachedStatement getStatement() {
		if (statement == null) {
			statement = new CachedStatement();
		}
		return statement;
	}

	/**
	 * showResult().
	 */
	protected void showResult() {
		System.out.println("");
		if (errorCount == 0) {
			if (warningCount == 0) {
				System.out.println(ResourceLoader.getString(SETUP_RES, "EndWithoutErr"));//Mod. 9634//"Elaborazione terminata senza errori."); //03679
			}
			else { //Mod.905

				//03679
				System.out.println(ResourceLoader.getString(SETUP_RES, "EndWithWarning",  new String[] { Integer.toString(warningCount)}));//Mod. 9634//"Elaborazione terminata con " + warningCount +
				//Mod. 9634//" warning."); //Mod.905
			}
		}
		else {

			//03679
			System.out.println(ResourceLoader.getString(SETUP_RES, "EndWithErr",  new String[] { Integer.toString(errorCount)}));//Mod. 9634//"Elaborazione terminata con " + errorCount +
			//Mod. 9634//" errori.");
		}
		if (duplicatedRows != 0) {
			//03679
			System.out.println(ResourceLoader.getString(SETUP_RES, "EndWithDupl",  new String[] { Integer.toString(duplicatedRows)}));//Mod. 9634//"Trovati gia' inseriti " + duplicatedRows + " record.");
			Iterator dupIter = duplicatedCommands.iterator();
			while (dupIter.hasNext()) {
								// 7281 DM inizio
				// output.println("DUPLICATO: " + dupIter.next());
								output.println(ResourceLoader.getString(SETUP_RES, "Duplicate",  new String[] { dupIter.next().toString()}));//Mod. 9634//"#DUPLICATO#: " + dupIter.next());
								// 7281 DM fine
			}
						// 7281 DM inizio
						duplicatedCommands.clear();
						// 7281 DM fine
		}
		System.out.println("");
	}

	int countOccurencesInAString(String strToBeSearched, String searchStr) {
		int counter = 0;
		int index = strToBeSearched.indexOf(searchStr);
		if (index < 0) {
			return counter; //Nessuna occorenza
		}
		else {
			counter++; //Almeno una occorrenza
		}
		while (index >= 0) {
			int startIndex = index + 1;
			index = strToBeSearched.indexOf(searchStr, startIndex);
			if (index > 0) {
				counter++;
			}
		}
		return counter;
	}

	Vector brokeComplexString(String str, String separator) {
		boolean correct = true;
		Vector bp = new Vector();
		int indexStart = 0;
		int indexEnd = str.indexOf(separator);
		while (indexEnd > 0) {
			indexEnd = brokeAPiece(str, ",", indexStart);
			String col = str.substring(indexStart, indexEnd);
			//Controllo se il pezzo staccato è completo
			//La stringa è corretta, la aggiungo al vettore
			bp.add(col);
			indexStart = indexEnd + 1;
			int oldindexEnd = indexEnd;
			indexEnd = str.indexOf(",", oldindexEnd + 1);
		}
		if (correct) {
			return bp;
		}
		else {
			return null;
		}
	}

	int brokeAPiece(String strToBeBroken, String separator, int startIndex) {
		boolean correct = true;
		String piece = "";
		boolean endFound = false;

		int endIndex = 0;
		int startSearchIndex = startIndex;
		while (endIndex <= strToBeBroken.length() && correct && !endFound) {
			endIndex = strToBeBroken.indexOf(separator, startSearchIndex);
			if (endIndex < 0) {
				endIndex = strToBeBroken.length();
				endFound = true;
			}
			piece = strToBeBroken.substring(startIndex, endIndex);

			//Controllo se il pezzo staccato è completo
			//può essere privo di apici iniziale e finale (è un numero)
			//oppure deve avere apice iniziale e finale e contenere un numero pari di apici
			int error = checkAPiece(piece);
			//Stringa ok
			if (error == 0) {
				endFound = true;
				// non è servito startIndex = endIndex+1;
			}
			//Stringa non chiusa: cerco il separatore successivo
			else if (error == -2) {
				startSearchIndex = endIndex + 1;
			}
			//Stringa non chiusa: cerco il separatore successivo
			else { //altrimenti segnalo errore
				correct = false;
			}
		}
		return endIndex;
	}

	//Controllo la correttezza della substring staccata
	//può essere privo di apici iniziale e finale (è un numero)
	//oppure deve avere apice iniziale e finale e contenere un numero pari di apici
	//Restituisce:
	// 0 = numero o stringa corretta (nessun apice o apice iniziale e finale e numero di apici pari
	// -1 = manca apice iniziale, c'è quello finale
	// -2 = manca quello finale: STRINGA NON COMPLETA
	int checkAPiece(String str) {
		str = str.trim();
		int nQuotes = countOccurencesInAString(str, "'");
//		int mod = nQuotes % 2;
		//Nessun apice
		if (nQuotes == 0) {
			return 0;
		}
		//Numero pari di apice, e apice iniziale e finale
		if (nQuotes % 2 == 0 && str.startsWith("'") && str.endsWith("'")) {
			return 0;
		}
		//Stringa aperta, ma non chiusa
		if (str.startsWith("'") && !str.endsWith("'")) {
			return -2;
		}
		//Stringa errata
		return -1;
	}

	// 3913 DM inizio
	protected void commit() throws SQLException {
		ConnectionManager.commit();
		// 5629 DM inizio
		//if (ConnectionManager.getCurrentDatabase() instanceof SQLServerDatabase) {
		if (CommandHistory.isActive()) {
			// 5629 DM fine
			CommandHistory.clear();
		}
	}

	protected void rollback() throws SQLException {
		ConnectionManager.rollback();
		// 5629 DM inizio
		// if (ConnectionManager.getCurrentDatabase() instanceof SQLServerDatabase) {
		if (CommandHistory.isActive()) {
			// 5629 DM fine
			CommandHistory.clear();
		}
	}

	public static boolean isCommit(String cmd) {
		return cmd.equals("COMMIT");
	}

	public static boolean isAutoRollback(String cmd) {
		// 5629 DM inizio
		// return cmd.startsWith("CREATE") ||
		// cmd.startsWith("ALTER TABLE") &&
		// (cmd.indexOf("CONSTRAINT") >= 0 || cmd.indexOf("COLUMN") >= 0);
		Database db = ConnectionManager.getCurrentDatabase();
		if (db instanceof SQLServerDatabase)
			return isSQLServerAutoRollbackDDL(cmd);
		else
			return false;
		// 5629 DM fine
	}
	// 3913 DM fine

	// 5629 DM inizio
	public static boolean isSQLServerAutoRollbackDDL(String cmd) {
		return cmd.startsWith("CREATE") || cmd.startsWith("ALTER TABLE") && (cmd.indexOf("CONSTRAINT") >= 0 || cmd.indexOf("COLUMN") >= 0);
	}

	public static boolean isDDL(String cmd) {
		return !cmd.startsWith("INSERT") && !cmd.startsWith("UPDATE") && !cmd.startsWith("DELETE");
	}
	// 5629 DM fine

	/**
	 * getTableToDrop(String command).
	 * @param String command
	 * @return String che rappresenta il nome della tabella da eliminare
	 */
	/* Revisions:
	 * Number     Date         Owner   Description
	 * 03907      13/06/2005   Ryo     Prima stesura: Restituisce la tabella da eliminare del formato SCHEMA.TABELLA
	 */
	public String getTableToDrop(String command) {
		command = command.trim();
		int leftoMargin = command.lastIndexOf(' ') + 1;
		int rightoMargin = command.length();
		String table = command.substring(leftoMargin, rightoMargin);
		return table;
	}

	/**
	 * isDropTable(String command).
	 * @param String command (deve essere tutto in maiuscolo)
	 * @return boolean che indica se lo statement è una DROP TABLE
	 */
	/* Revisions:
	 * Number     Date         Owner   Description
	 * 03907      13/06/2005   Ryo     Prima stesura: Controlla che command sia uno statement di eliminazione tabella
	 */
	public boolean isDropTable(String command) {
		boolean isDropTable = false;
		int drop = command.indexOf(SQL_DROP);
		if (drop > -1) {
			drop = drop + SQL_DROP.length();
			int table = command.indexOf(SQL_TABLE, drop);
			if (table > -1) {
				if (command.substring(drop, table).trim().equals("")) {
					isDropTable = true;
				}
			}
		}
		else {
			isDropTable = false;
		}
		return isDropTable;
	}

	/**
	 * Controlla che command sia uno statement di creazione tabella
	 * @param String command (deve essere tutto in maiuscolo)
	 * @return boolean che indica se lo statement è una CREATE TABLE
	 */
	/* Revisions:
	 * Number     Date         Owner   Description
	 * 04648      23/11/2005   Ryo     Prima stesura
	 */
	public boolean isCreateTable(String command) {
		boolean isCreateTable = false;
		int create = command.indexOf(SQL_CREATE);
		if (create > -1) {
			create = create + SQL_CREATE.length();
			int table = command.indexOf(SQL_TABLE, create);
			if (table > -1) {
				if (command.substring(create, table).trim().equals("")) {
					isCreateTable = true;
				}
			}
		}
		return isCreateTable;
	}

	/**
	 * Intercetto gli statement prima che vengano eseguiti per eventuali manipolazioni
	 * @param String command
	 * @return boolean che indica se lo statement deve comunque venir eseguito
	 */
	/* Revisions:
	 * Number     Date         Owner   Description
	 * 03907      13/06/2005   Ryo     Prima stesura
	 */
	public boolean internalSQLParser(String command)
	{
		boolean interceptor = true; // non modificandolo consente l'esecuzione dello statement corrente

		// Intercetta lo statement "DROP TABLE": su SQL Server, prima che venga eseguito, elimina i constraint che puntano alla tabella da cancellare
		if(isDropTable(command.toUpperCase()))
		{
//			try
//			{
			Database db = ConnectionManager.getCurrentDatabase();
			if(db instanceof SQLServerDatabase)
			{
				String table = getTableToDrop(command);
				String schemaName = table.substring(0, table.indexOf('.'));
				String tableName = table.substring(table.indexOf('.') + 1, table.length());
//				Table tab = new Table(ConnectionManager.getCurrentDBName(),
//				schemaName, tableName);
				dropConstraint(schemaName, tableName);
			}
//			}
//			catch (SQLException se) {
//			interceptor = false;
//			}
		}

		// Altre eventuali intercettazioni
		// ...

		return interceptor;
	}

	/**
	 * Intercetto gli statement prima che vengano eseguiti e che vengano loro sostituiti i tag per eventuali manipolazioni
	 * @param String command
	 * @return boolean che indica se lo statement deve comunque venir eseguito
	 */
	/* Revisions:
	 * Number     Date         Owner   Description
	 * 04648      23/11/2005   Ryo     Prima stesura
	 * 06400      13/12/2006   Ryo     Cambiato tipo di return
	 * 06504      11/01/2007   Ryo     Eliminato un punto e virgola prima dell'inserimento del tablespace di default
	 */
//	public boolean internalSQLParserWithTags(String command) throws SQLException
	public String internalSQLParseWithTags(String command) throws SQLException
	{

//		Fix 6400 Ryo inizio
//		boolean interceptor = true;
		// impostando ret a valore null non consente l'esecuzione dello statement corrente
		String ret = command;
//		Fix 6400 Ryo fine

		// Intercetta lo statement "CREATE TABLE": in mancanza del tag <TBS> lo statement non va eseguito. Ovviamente per AS400 non si comporta così ma lo esegue ugualmente
		Database currDBì = ConnectionManager.getCurrentDatabase();
		if(isCreateTable(command.toUpperCase()) && ! (currDBì instanceof DB2AS400Database || currDBì instanceof DB2ToolboxDatabase))
		{
			int tagTbs = command.indexOf("<" + TagProcessor.TABLESPACE + ">");
			if(tagTbs == -1)
			{
//				CachedStatement s = getStatement();
//				tagProcessor.buildStmt(s, command, params);
//				output.println(s.getStmtString() + "\n********** #ERRORE# **********\nTablespace non specificato per la tabella");
//				interceptor = false;
				CachedStatement s = getStatement();
				tagProcessor.buildStmt(s, command, params);
				output.println(ResourceLoader.getString(SETUP_RES, "DefaultTablespace",  new String[] {s.getStmtString()}));//Mod. 9634//s.getStmtString() + "\n********** #WARNING# **********\nTablespace non specificato per la tabella, usato quello di default.");
//				statement.setStmtString(statement.getStmtString() + " <" + TagProcessor.TABLESPACE + "><DEFAULT>");	// Fix 6400 Ryo

//				Fix 6504 Ryo inizio
				if(command.endsWith(";"))
				{
					command = command.substring(0, command.length() - 1);
				}
//				Fix 6504 Ryo fine

				ret = command +  " <" + TagProcessor.TABLESPACE + "><DEFAULT>";	// Fix 6400 Ryo
			}
		}

		// Altre eventuali intercettazioni
		// ...

		return ret;
	}

	/* Revisions:
	 * Fix nr   Date          Owner      Description
	 * 03907    13/06/2005    Ryo        Prima stesura: Elimina le foreign key che puntano a una tabella SQL Server
	 */
	public boolean dropConstraint(String schemaName, String tableName) {
		boolean okei = true;
		try {
						// 9990 DM inizio
			// DatabaseMetaData meta = ConnectionManager.getCurrentConnection().getMetaData();
						DatabaseMetaData meta = ConnectionManager.getCurrentDatabase().getDatabaseMetaData(ConnectionManager.getCurrentConnection());
						// 9990 DM fine
			ColumnHealer healer = new ColumnHealer();
			Vector cts2t = healer.getConstraintToTable(meta, schemaName, tableName, false);
//			StringBuffer c2t = new StringBuffer();
			ListIterator clearCts2tIterator = cts2t.listIterator();
			while (clearCts2tIterator.hasNext()) {
				String command = ( (ColumnHealer.ConstraintToTable) clearCts2tIterator.next()).
				getDropStatement();
				executeCommand(command);
			}
		}
		catch (SQLException se) {
			okei = false;
		}
		return okei;
	}

	//6057 CB inizio
	//implementati i metodi per l'interfaccia ErrorHandler
	public void warning(SAXParseException exception)
	throws SAXException{
		output.println("TDDML WARNING: " + getSaxExceptionMessage(exception));
	}

	public void error(SAXParseException exception)
	throws SAXException{
		output.println("TDDML ERROR: " + getSaxExceptionMessage(exception));
	}

	public void fatalError(SAXParseException exception)
	throws SAXException{
		output.println("TDDML FATAL ERROR: " + getSaxExceptionMessage(exception));
	}

	protected String getSaxExceptionMessage(SAXParseException e){
		String msg  =e.getMessage() + " Line: " + e.getLineNumber();
		if (e.getColumnNumber()>=0)
			msg += " Column: " + e.getColumnNumber();
		return msg;
	}
	//6057 CB fine

	// 6274 DM inizio
	protected boolean declassErrorToWarning(SQLException e, String command) {
		return e.getErrorCode() == ErrorCodes.NO_ROWS_FOUND && e.getSQLState().equals("MenoSei");
	}

	protected boolean ignoreError(SQLException e, String command) {
		return false;
	}
	// 6274 DM fine

	//07935 - DF
	public PrintWriter getOutput() {
		return output;
	}
	//07935 - Fine

	public static void main(String[] args) throws SQLException {
				//Trace.setLogEnabled(Trace.PERS_CONN, true);
		ConnectionManager.openMainConnection("panth01", "db2admin", "db2admin",	new com.thera.thermfw.persist.DB2Database());
		String insert =  "";
		//insert = "INSERT INTO FINANCE .SVAPSPT (APSCATEG, T01CD, T96CD) VALUES(CURRENT DATE, CURRENT TIME, CURRENT TIMESTAMP)";
		Setup s = new Setup("db2", null);
		FileProcessor p = new SQLFileTester(s, "ciao");
				System.out.println(insert);
		//String update1 = p.fromInserToUpdateCommand(insert);
		//System.out.println("[" + update1 + "]");
				String update2 = p.fromInsertToUpdateCommand(insert);
				System.out.println("[" + update2 + "]");
		ConnectionManager.closeAllConnections();
	}


}

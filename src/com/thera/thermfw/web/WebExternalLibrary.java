package com.thera.thermfw.web;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * 
 * @author Mehrez Ben Salem
 * @version 1.0
 */
/*
 * Revisions:
 * Number   Date         Owner   Description
 * 28387    20/12/2018   Mz      Versione iniziale
 * 30206    05/12/2019   Mz      Aggiungendo le librerie esterne di Clipboard negli Estensione entità
 * 36726    25/10/2021   RA      Metodi per paramquery
 * 
 */
public class WebExternalLibrary {
	
	static public String importStylesheet(HttpServletRequest request, String library) {
		return importStylesheet(request, library, null);
	}
	
	static public String importStylesheet(HttpServletRequest request, String library, String version) {
      //fix 36726 inizio
	   return importStylesheet(request, library, "css/" + library, version, true);
	   /*
		ServletContext requestContext = request.getSession().getServletContext();
		File extlibs = new File(requestContext.getRealPath("/extlibs"));
		File libraryDir = new File(extlibs, library);
		if(libraryDir.exists() && libraryDir.isDirectory()) {
			if(version != null) {
				File versionDir = new File(libraryDir, version);
				if(versionDir.exists() && versionDir.isDirectory()) {
					return importStylesheet(getBaseURL(request), library, version);
				}
			}
			String[] versionDirs = libraryDir.list();
			if(versionDirs != null) {
				List<String> versions = Arrays.asList(versionDirs);
				Collections.sort(versions);
				String lastVersion = (String)versions.get(versions.size() - 1);
				return importStylesheet(getBaseURL(request), library, lastVersion);
			}
		}
		
		return "";
		*/
      //fix 36726 fine
	}
	
	
   //fix 36726 inizio
   static public String importStylesheet(HttpServletRequest request, String library, String sublibrary, String version, boolean minVersion) {
      ServletContext requestContext = request.getSession().getServletContext();
      File extlibs = new File(requestContext.getRealPath("/extlibs"));
      File libraryDir = new File(extlibs, library);
      if(libraryDir.exists() && libraryDir.isDirectory()) {
         if(version != null) {
            File versionDir = new File(libraryDir, version);
            if(versionDir.exists() && versionDir.isDirectory()) {
               return importStylesheet(getBaseURL(request), library, sublibrary, version, minVersion);
            }
         }
         String[] versionDirs = libraryDir.list();
         if(versionDirs != null) {
            List<String> versions = Arrays.asList(versionDirs);
            Collections.sort(versions);
            String lastVersion = (String)versions.get(versions.size() - 1);
            return importStylesheet(getBaseURL(request), library, sublibrary, lastVersion, minVersion);
         }
      }
      
      return "";
   }
   //fix 36726 fine
	
	
	static protected String importStylesheet(String baseUrl, String library, String version) {
	   return importStylesheet(baseUrl, library, null, version, true);
	   //fix 36726 inizio
	   /*    
		String href = baseUrl + "/extlibs/" + library + "/" + version + "/css/" + library + getStylesheetSuffix();
		return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\"/>";
		*/
	   //fix 36726 fine
	}

   //fix 36726 inizio
   static protected String importStylesheet(String baseUrl, String library, String sublibrary, String version, boolean minVersion) {
      String href = baseUrl + "/extlibs/" + library + "/" + version + "/";
      if(sublibrary != null)
         href += sublibrary;
      else
         href += library;
      if(minVersion)
         href += getStylesheetMinSuffix();
      else
         href += getStylesheetSuffix();
         
      return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\"/>";
   }
   //fix 36726 fine
	
	static public String importScript(HttpServletRequest request, String library) {
		return importScript(request, library, null);
	}
	
	static public String importScript(HttpServletRequest request, String library, String version) {
	   //fix  36726 inizio
	   return importScript(request, library, library, version, true);
	   /*
		ServletContext requestContext = request.getSession().getServletContext();
		File extlibs = new File(requestContext.getRealPath("/extlibs"));
		File libraryDir = new File(extlibs, library);
		if(libraryDir.exists() && libraryDir.isDirectory()) {
			if(version != null) {
				File versionDir = new File(libraryDir, version);
				if(versionDir.exists() && versionDir.isDirectory()) {
					return importScript(getBaseURL(request), library, version);
				}
			}
			String[] versionDirs = libraryDir.list();
			if(versionDirs != null) {
				List<String> versions = Arrays.asList(versionDirs);
				Collections.sort(versions);
				String lastVersion = (String)versions.get(versions.size() - 1);
				return importScript(getBaseURL(request), library, lastVersion);
			}
		}
		
		return "";
		*/
     //fix  36726 fine
	}
	
   //fix  36726 inizio
   static public String importScript(HttpServletRequest request, String library, String sublibrary, String version, boolean minVersion) {
      ServletContext requestContext = request.getSession().getServletContext();
      File extlibs = new File(requestContext.getRealPath("/extlibs"));
      File libraryDir = new File(extlibs, library);
      if(libraryDir.exists() && libraryDir.isDirectory()) {
         if(version != null) {
            File versionDir = new File(libraryDir, version);
            if(versionDir.exists() && versionDir.isDirectory()) {
               return importScript(getBaseURL(request), library, sublibrary, version, minVersion);
            }
         }
         String[] versionDirs = libraryDir.list();
         if(versionDirs != null) {
            List<String> versions = Arrays.asList(versionDirs);
            Collections.sort(versions);
            String lastVersion = (String)versions.get(versions.size() - 1);
            return importScript(getBaseURL(request), library, sublibrary, lastVersion, minVersion);
         }
      }
      
      return ""; 
   }
   //fix  36726 fine
   

	static protected String importScript(String baseUrl, String library, String version) {
	   //fix  36726 inizio
	   return importScript(baseUrl, library, null, version, true);
	   /*
		String src = baseUrl + "/extlibs/" + library + "/" + version + "/" + library + getScriptSuffix();
		return "<script src=\"" + src + "\"></script>";
		*/
	   //fix  36726 fine
	}

   //fix  36726 inizio
	static protected String importScript(String baseUrl, String library, String sublibrary, String version, boolean minVersion) {
      String src = baseUrl + "/extlibs/" + library + "/" + version + "/";
      if(sublibrary != null)
         src += sublibrary;
      else
         src += library;
      if(minVersion)
         src += getScriptMinSuffix();
      else
         src += getScriptSuffix();
      return "<script src=\"" + src + "\"></script>";
   }

	static protected String getScriptMinSuffix() {
		return ".min.js";
		//return ".js";
	}
	
	static protected String getStylesheetMinSuffix() {
		return ".min.css";
		//return ".css";
	}

   static protected String getScriptSuffix() {
      return ".js";
   }
   
   static protected String getStylesheetSuffix() {
      return ".css";
   }
   //fix  36726 fine
	
	static protected String getBaseURL(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}
}

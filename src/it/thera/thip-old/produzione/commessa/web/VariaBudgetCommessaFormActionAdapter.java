package it.thera.thip.produzione.commessa.web;

import java.io.IOException;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.cbs.web.WfFormActionAdapter;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebToolBar;
import com.thera.thermfw.web.WebToolBarException;

/**
 * VariaBudgetCommessaFormActionAdapter.
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34795   09/02/2022     RA        Prima versione
 */

public class VariaBudgetCommessaFormActionAdapter extends WfFormActionAdapter {

  protected void save(ClassADCollection	cadc, ServletEnvironment se) throws ServletException, IOException {
     ConsuntivoCommessaDetServlet.saveViewMDV(se, "VariaBudgetCommessa");
     se.sendRequest(getServletContext(), se.getServletPath() + "it.thera.thip.produzione.commessa.web.VariaBudgetCommessaSave", true);
  }
  
  public void modifyToolBar(WebToolBar toolBar) {
	  super.modifyToolBar(toolBar);
	  try {
		  toolBar.removeSeparatorAfter("Print");
	  } 
	  catch (WebToolBarException e) {
		  e.printStackTrace(Trace.excStream);
	  }
	  toolBar.removeButton("SaveScreenData");
	  toolBar.removeButton("SaveScreenDataPullDown");
  }	

}

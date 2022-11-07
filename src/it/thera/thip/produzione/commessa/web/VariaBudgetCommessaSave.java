package it.thera.thip.produzione.commessa.web;

import java.io.IOException;

import javax.servlet.ServletException;

import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.Save;

/**
 * VariaBudgetCommessaSave.
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh
 */
/*
 * Revisions:
 * Number  Date          Owner      Description
 * 34795   09/02/2022     RA        Prima versione
 */

public class VariaBudgetCommessaSave extends Save {
	
	public void afterProcessAction(BODataCollector boDC, ServletEnvironment se) throws ServletException, IOException {
		se.sendRequest(getServletContext(), "it/thera/thip/produzione/commessa/VariaBudgetErrorListHandler.jsp", true);
	}
}

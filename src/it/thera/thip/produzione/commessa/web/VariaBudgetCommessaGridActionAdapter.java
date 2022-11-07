package it.thera.thip.produzione.commessa.web;

import com.thera.thermfw.web.WebMenuBar;
import com.thera.thermfw.web.WebToolBar;

import it.thera.thip.cs.web.AziendaGridActionAdapter;
/**
 * VariaBudgetCommessaGridActionAdapter
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh 14/12/2021
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 34795   14/12/2021    RA       Prima struttura
 */
public class VariaBudgetCommessaGridActionAdapter extends AziendaGridActionAdapter
{
	public void modifyMenuBar(WebMenuBar menuBar) {
		menuBar.removeMenu("ListMenu.NewTemplate");
		menuBar.removeMenu("SelectedMenu.Copy");
		menuBar.removeMenu("SelectedMenu.New");
	}

	public void modifyToolBar(WebToolBar toolBar) {
		super.modifyToolBar(toolBar);
		toolBar.removeButton("Copy");
		toolBar.removeButton("New");
	}
	
}

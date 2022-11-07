package it.thera.thip.base.commessa.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.thera.thermfw.base.Q6Calc;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.base.Utils;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.type.NumberType;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.ajax.XMLElement;
import com.thera.thermfw.web.ajax.XMLElementBuilder;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.commessa.PreventivoCommessaRiga;
import it.thera.thip.base.commessa.PreventivoCommessaVoce;
/**
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Aouidet Riadh
 */
/**
 * Revisions:
 * Number   Date          Owner   Description
 * 31385    16/06/2020    RA      Prima Struttura
 * 31854	25/09/2020	  RA	  Corretto caricamento preventivo commessa riga  
*/
public class PreventivoCommessaLoadNewRowAjaxServlet extends BaseServlet{
	   
	public static final String SELECT_ROW = "selectRow";
	public static final String ACTION_LOAD_NEW_ROW = "loadNewRow";
	public static final String CLASS_HDR_ROW = "classHdrRow";
	
	protected void processAction(ServletEnvironment se) throws Exception {
		XMLElementBuilder builder = new XMLElementBuilder();
		XMLElement xmlElement = builder.parseStream(se.getRequest().getInputStream());
		XMLElement response = null;
		String chiave = getAttributeFromRequest(xmlElement, SELECT_ROW);
		String actionLoadNewRow = getAttributeFromRequest(xmlElement, ACTION_LOAD_NEW_ROW);
		String classHdrRow = getAttributeFromRequest(xmlElement, CLASS_HDR_ROW);
  		 
		response = new XMLElement("response");
		response.setAttribute("Key", chiave);
		response.setAttribute(ACTION_LOAD_NEW_ROW, actionLoadNewRow);
		response.setAttribute(CLASS_HDR_ROW, classHdrRow);
		
		if(classHdrRow != null && classHdrRow.equals("PreventivoCommessaRiga")){
			caricaPreventivoCommessaRiga(response, chiave, actionLoadNewRow);
		}
		else{
			caricaPreventivoCommessaVoce(response, chiave, actionLoadNewRow);
		}
		
		
		replyXML(response, se.getResponse());
	}
	
	public void caricaPreventivoCommessaRiga(XMLElement response, String chiaveRiga, String actionLoadNewRow) {
		String rigaKey = URLDecoder.decode(chiaveRiga);
		try{
			PreventivoCommessaRiga riga = (PreventivoCommessaRiga)PreventivoCommessaRiga.elementWithKey(rigaKey, PersistentObject.NO_LOCK);
			if(riga != null){
				//31854 inizio
				XMLElement xmlRiga = new XMLElement("riga");
				xmlRiga.setAttribute("Key", chiaveRiga);
				xmlRiga.setAttribute(ACTION_LOAD_NEW_ROW, actionLoadNewRow);
				xmlRiga.setAttribute(CLASS_HDR_ROW, "PreventivoCommessaRiga");
				caricaXMLElementsRiga(xmlRiga, chiaveRiga, riga);				
				if(riga.getSottoCommesse() != null && !riga.getSottoCommesse().isEmpty()){
					for (Iterator iterator = riga.getSottoCommesse().iterator(); iterator.hasNext();) {
						PreventivoCommessaRiga sottoRiga = (PreventivoCommessaRiga) iterator.next();
						caricaPreventivoCommessaRiga(xmlRiga, URLEncoder.encode(sottoRiga.getKey()), actionLoadNewRow);
					}
				}
				
				if(riga.getArticoli() != null && !riga.getArticoli().isEmpty()){
					for (Iterator iterator = riga.getArticoli().iterator(); iterator.hasNext();) {
						PreventivoCommessaVoce articolo = (PreventivoCommessaVoce) iterator.next();						
						caricaPreventivoCommessaVoce(xmlRiga, URLEncoder.encode(articolo.getKey()), actionLoadNewRow);
					}
				}
				if(riga.getRisorse() != null && !riga.getRisorse().isEmpty()){
					for (Iterator iterator = riga.getRisorse().iterator(); iterator.hasNext();) {
						PreventivoCommessaVoce risorsa = (PreventivoCommessaVoce) iterator.next();						
						caricaPreventivoCommessaVoce(xmlRiga, URLEncoder.encode(risorsa.getKey()), actionLoadNewRow);
					}
				}
								
				if(riga.getVoci() != null && !riga.getVoci().isEmpty()){
					for (Iterator iterator = riga.getVoci().iterator(); iterator.hasNext();) {
						PreventivoCommessaVoce voce = (PreventivoCommessaVoce) iterator.next();						
						caricaPreventivoCommessaVoce(xmlRiga, URLEncoder.encode(voce.getKey()), actionLoadNewRow);
					}
				}
				response.appendChild(xmlRiga);
				//31854 fine
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
	}
	
	public void caricaPreventivoCommessaVoce(XMLElement response, String chiaveVoce, String actionLoadNewRow){
		String voceKey = URLDecoder.decode(chiaveVoce);
		try{
		PreventivoCommessaVoce voce = (PreventivoCommessaVoce)PreventivoCommessaVoce.elementWithKey(voceKey, PersistentObject.NO_LOCK);
			if(voce != null){
				//31854 inizio
				XMLElement xmlVoce = new XMLElement("voce");
				xmlVoce.setAttribute("Key", chiaveVoce);
				xmlVoce.setAttribute(ACTION_LOAD_NEW_ROW, actionLoadNewRow);
				xmlVoce.setAttribute(CLASS_HDR_ROW, "PreventivoCommessaVoce");
				caricaXMLElementsVoce(xmlVoce, chiaveVoce, voce);
				if(voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_VOCE){
					List righeDet = voce.getRighe();
					if(righeDet != null && !righeDet.isEmpty()) {
						for (Iterator iter = righeDet.iterator(); iter.hasNext(); ) {
							PreventivoCommessaVoce voceDet = (PreventivoCommessaVoce)iter.next();
							String chiaveVoceDet = URLEncoder.encode(voceDet.getKey());
							XMLElement dettaglio = new XMLElement("dettaglio");
							dettaglio.setAttribute("Key", chiaveVoceDet);
							dettaglio.setAttribute(ACTION_LOAD_NEW_ROW, actionLoadNewRow);
							dettaglio.setAttribute(CLASS_HDR_ROW, "PreventivoCommessaVoce");
							caricaXMLElementsVoce(dettaglio, chiaveVoceDet, voceDet);
							xmlVoce.appendChild(dettaglio);
						}					
					}
				}
				response.appendChild(xmlVoce);
				//31854 fine
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
	}
	
	public void caricaXMLElementsRiga(XMLElement response, String chiaveRiga, PreventivoCommessaRiga riga){
		caricaXMLElements(response, chiaveRiga, getIcon(riga), "icon", "icon_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getKeyForTree()), "label", "label_");			
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getTipoRigaForTree()), "TipoRiga", "TipoRigaForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getNota()), "Nota", "Nota_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getIdComponCostoForTree()), "IdComponCostoForTree", "IdComponCostoForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getIdUmVenForTree()), "IdUmVenForTree", "IdUmVenForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getQtaPrvUmPrmForTree(),"QtaPrvUmPrm"), "QtaPrvUmPrm", "QtaPrvUmPrmForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getCostoRiferForTree(), "Costo"), "Costo", "CostoRiferForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getPrezzoTotaleForTree(), "Prezzo"), "Prezzo", "PrezzoTotaleForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getMarckupForTree(), "Markup"), "Markup", "MarkupForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getVlrLivelloForTree(), "VlrLivelloForTree"), "VlrLivelloForTree", "VlrLivelloForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getCosLivelloForTree(), "CosLivelloForTree"), "CosLivelloForTree", "CosLivelloForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getMdcLivelloForTree(), "MdcLivelloForTree"), "MdcLivelloForTree", "MdcLivelloForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getVlrTotaleForTree(), "ValoreTotale"), "ValoreTotale", "VlrTotaleForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getCosTotaleForTree(), "CostoTotale"), "CostoTotale", "CosTotaleForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getMdcTotaleForTree(), "MargineTotale"), "MargineTotale", "MdcTotaleForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getPercentualeForTree(), "PercentualeTotale"), "PercentualeTotale", "PercentualeForTree_");
		caricaXMLElements(response, chiaveRiga, getDescrizioneEnum("Stato",riga.getStato()), "Stato", "DatiComuniEstesi.Stato_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getDescrArticoloForTree()), "DescrizionArticoloForTree", "DescrizionArticoloForTree_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getDescrCommessaForTree()), "DescrizionCommessaForTree", "DescrizionCommessaForTree_");
		caricaXMLElements(response, chiaveRiga, String.valueOf(riga.getLivello()), "Livello", "Livello_");
		caricaXMLElements(response, chiaveRiga, getStringValue(riga.getDescrizione().getDescrizione()), "DescrizioneForTree", "DescrizioneForTree_");
	}
	
	public void caricaXMLElementsVoce(XMLElement response, String chiaveVoce, PreventivoCommessaVoce voce){
		caricaXMLElements(response, chiaveVoce, getIcon(voce), "icon", "icon_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getKeyForTree()), "label", "label_");			
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getTipoRigaForTree()), "TipoRigav", "TipoRigaForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getNota()), "Nota", "Nota_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getIdComponCostoForTree()), "IdComponCostoForTree", "IdComponCostoForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getIdUmVenForTree()), "IdUmVenForTree", "IdUmVenForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getQtaPrvUmPrmForTree(),"QtaPrvUmPrm"), "QtaPrvUmPrm", "QtaPrvUmPrmForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getCostoRiferForTree(), "Costo"), "Costo", "CostoRiferForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getPrezzoTotaleForTree(), "Prezzo"), "Prezzo", "PrezzoTotaleForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getMarckupForTree(), "Markup"), "Markup", "MarkupForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getVlrLivelloForTree(), "VlrLivelloForTree"), "VlrLivelloForTree", "VlrLivelloForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getCosLivelloForTree(), "CosLivelloForTree"), "CosLivelloForTree", "CosLivelloForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getMdcLivelloForTree(), "MdcLivelloForTree"), "MdcLivelloForTree", "MdcLivelloForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getVlrTotaleForTree(), "ValoreTotale"), "ValoreTotale", "VlrTotaleForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getCosTotaleForTree(), "CostoTotale"), "CostoTotale", "CosTotaleForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getMdcTotaleForTree(), "MargineTotale"), "MargineTotale", "MdcTotaleForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getPercentualeForTree(), "PercentualeTotale"), "PercentualeTotale", "PercentualeForTree_");
		caricaXMLElements(response, chiaveVoce, getDescrizioneEnum("Stato",voce.getStato()), "Stato", "DatiComuniEstesi.Stato_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getDescrArticoloForTree()), "DescrizionArticoloForTree", "DescrizionArticoloForTree_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getDescrCommessaForTree()), "DescrizionCommessaForTree", "DescrizionCommessaForTree_");
		caricaXMLElements(response, chiaveVoce, String.valueOf(voce.getLivello()), "Livello", "Livello_");
		caricaXMLElements(response, chiaveVoce, getStringValue(voce.getDescrizione().getDescrizione()), "DescrizioneForTree", "DescrizioneForTree_");
	}
	
	public String getDescrizioneEnum(String enumRef, char value) {
		return new com.thera.thermfw.type.EnumType(enumRef).descriptionFromValue(String.valueOf(value));
	}
	
	public String getStringValue(String value){
		if(value == null)
			return "";
		return value;
	}
	
	protected String getIcon(PreventivoCommessaRiga riga) {
		return "it/thera/thip/base/commessa/images/Commessa.gif";
	}
	
	protected String getIcon(PreventivoCommessaVoce voce) {
		if(voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_RISORSA){
			if(voce.getIdSubRigavPrv() != 0){
				return "it/thera/thip/base/commessa//images/RisorsaA.gif";
			}
			else{
				return "it/thera/thip/datiTecnici/modpro/images/Risorsa.gif";
			}			
		}
		else if(voce.getTipoRigav() == PreventivoCommessaVoce.TP_RIG_ARTICOLO){
			return "it/thera/thip/cs/images/Articolo.gif";
		}				
		else{
			if(voce.getIdSubRigavPrv() != 0){
				return "it/thera/thip/datiTecnici/modpro/images/MaterialeA.gif";
			}
			else{
				return "it/thera/thip/datiTecnici/modpro/images/Materiale.gif";
			}								
		}
	}
	
	public String getStringValue(BigDecimal value,String xmlElementName){
		String valueStr = "";
		if(value != null){
			if(xmlElementName.equals("Prezzo") || xmlElementName.equals("Costo") ||  xmlElementName.equals("Markup")){
				value = value.setScale(6, BigDecimal.ROUND_HALF_UP);
			}
			else if (xmlElementName.equals("QtaPrvUmPrm")){
				value = Q6Calc.get().setScale(value, 2, BigDecimal.ROUND_HALF_UP);
			}
			else{
				value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			valueStr = Utils.bigDecimalToString(value);
		}

		valueStr = valueStr.replace('.', NumberType.getDecimalSeparator());
		return valueStr;
	}
	
	public void caricaXMLElements(XMLElement response,String keyElement, String valore, String xmlElementName, String columnName) {
		XMLElement element = new XMLElement(xmlElementName);
		element.setAttribute("Key", keyElement);
		element.setAttribute("ColumnName", columnName);
		element.setText(valore);
		response.appendChild(element);
	}
	
	public String getAttributeFromRequest(XMLElement xmlRequest, String attName) {
	   XMLElement contents = xmlRequest.getFirstChild("Contents");
	   return contents.getAttribute(attName);
	}
	 
	protected void replyXML(XMLElement xmlRes, HttpServletResponse res) throws IOException {
		if(xmlRes != null) {
			res.setContentType("text/xml; charset=windows-1252");
			PrintWriter pw = res.getWriter();
			pw.println(xmlRes.toXMLString("windows-1252"));	        
	        pw.flush();
		}
	}	
}

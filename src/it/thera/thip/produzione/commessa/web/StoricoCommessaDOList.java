package it.thera.thip.produzione.commessa.web;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import com.thera.thermfw.ad.ClassAD;
import com.thera.thermfw.ad.DescriptorAuthorizable;
import com.thera.thermfw.ad.SimpleClassAD;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.gui.cnr.CalculatedAttrDescription;
import com.thera.thermfw.gui.cnr.DisplayObject;
import com.thera.thermfw.persist.Column;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.setting.Criterion;
import com.thera.thermfw.setting.LiteralCriterion;
import com.thera.thermfw.type.EnumType;
import com.thera.thermfw.type.EqualOperator;
import com.thera.thermfw.type.NotEqualOperator;
import com.thera.thermfw.type.Type;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.BaseServlet;
import it.thera.thip.cs.web.AziendaDOList;
import it.thera.thip.cs.web.AziendaWebDOList;
import it.thera.thip.produzione.commessa.StoricoCommessaTM;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
/*
 * Revisions:
 * Number  Date          Owner    Description
 * 33950   27/10/2021    RA       Prima struttura
 */
public class StoricoCommessaDOList extends AziendaWebDOList {

   /**
    * Override of getDisplayObject method
    * @param resSet ResultSet
    * @param SQLTypes int[]
    * @return DisplayObject
    */
   protected DisplayObject getDisplayObject(ResultSet resSet, int[] SQLTypes) {

      DisplayObject currentDO = new DisplayObject(numberOfBuffer);
      currentDO.setNumberOfHidden(numberOfHiddenSelectedAttributes);
      int j = 1;
      Object value = null;
      String keysText = null;
      try {
         for (int i = 1; i <= numberOfBuffer; i++) {
            ClassAD curAD = selectClassADs[i - 1];
            boolean isDenied = curAD.getAuthorizationLevel() == DescriptorAuthorizable.DENIED;
            Type type = curAD.getType();
            //Added to change the type of TipoRisorsa by TipoRisorsaNonSignific
            //if the AttributeName equal "TipoRisorsa"
            if (curAD.getAttributeName().equals("TipoRisorsa")) {
               type = (EnumType) Type.newType("EnumType", type.getSize(), type.getScale(), "TipoRisorsaNonSignific");
            }
            //End
            if (selectClassADs[i - 1].isMapped()) {
               boolean isDec = SQLTypes[j - 1] == Types.DECIMAL;

               value = ConnectionManager.getCurrentDatabase().getObjectFromColumn(resSet, SQLTypes[j - 1], j);
               // modificato per ENUM con blank
               if (value instanceof String && !type.hasValues())
                  value = Column.rightTrim( (String) value);
               if (!isDenied)
                  currentDO.setDBValue(i, value, type, isDec);
               else
                  currentDO.setDBValue(i, value, null, isDec);
               j++;
            }
            else if (selectClassADs[i - 1].isCompound()) {
               value = ( (CalculatedAttrDescription) calculatedAttrDescriptionSet.get(new Integer(i))).getValue();
               if (!isDenied)
                  currentDO.setDBValue(i, value, type);
               else
                  currentDO.setDBValue(i, value, null);
            }
            else if (type.isIcon()) {
               currentDO.setDBValue(i, defaultIcon, type);
            }
         }

         for (int i = startOfHiddenCols; i < (startOfHiddenCols + numberOfHiddenSelectedAttributes); i++) {
            ClassAD curAD = hiddenSelectClassADs[i - startOfHiddenCols];
            Type type = curAD.getType();
            boolean isDenied = curAD.getAuthorizationLevel() == DescriptorAuthorizable.DENIED;
            boolean isDec = SQLTypes[i - 1] == Types.DECIMAL;
            value = ConnectionManager.getCurrentDatabase().getObjectFromColumn(resSet, SQLTypes[i - 1], i);
            if (value instanceof String && !type.hasValues())
               value = Column.rightTrim( (String) value);
            int hh = i - startOfHiddenCols;
            if (!isDenied)
               currentDO.setDBHiddenValue(i - startOfHiddenCols + 1, value, type, isDec);
            else
               currentDO.setDBHiddenValue(i - startOfHiddenCols + 1, value, null, isDec);
         }

         for (int i = startOfKeyCols; i < (startOfKeyCols + numberOfKeys); i++) {
            Type type = keyClassADs[i - startOfKeyCols].getType();
            String valueStr = null;

            {
               value = ConnectionManager.getCurrentDatabase().getObjectFromColumn(resSet, SQLTypes[i - 1], i);
               valueStr = type.objectToString(value);
            }

            String k = value == null ? "" : value.toString(); //GN sostituita String k = value.toString();
            if (!type.hasValues())
               k = Column.rightTrim(k);
            currentDO.addObjectKey(k);

            String curKey = type.format(valueStr);
            if (keysText != null)
               keysText += PersistentObject.KEY_SEPARATOR + curKey;
            else
               keysText = curKey;
         }
      }
      catch (Exception e) {
         e.printStackTrace(Trace.excStream);
      }
      currentDO.setKeysText(KeyHelper.formatKeyString(keysText));
      currentDO.setTreeText("(" + currentDO.getKeysText() + ")");
      return currentDO;
   }

   //33950 inizio
   public String getSpecificWhereClause() {	  
      int idx_DATA_ORG = specificWhereClause.indexOf(StoricoCommessaTM.DATA_ORG);
      if(idx_DATA_ORG > 0) {
         specificWhereClause = specificWhereClause.substring(0, idx_DATA_ORG + 9) +  "<=" + specificWhereClause.substring(idx_DATA_ORG + 10, specificWhereClause.length());
      }
      return specificWhereClause;
   }

   protected void initSpecificDOList() {
      super.initSpecificDOList();
      ServletEnvironment se = getServletEnvironment();
      if(se != null)
      {
         String addInfo = BaseServlet.getStringParameter(se.getRequest(), "thAdditionalInfo");
         if(addInfo != null && addInfo.length() > 0)
         {
            boolean ordini = true;
            boolean richieste  = true;
            boolean documenti  = true;
            boolean valorizzaCosto  = false;
            StringTokenizer st = new StringTokenizer(addInfo, ";");
            while(st.hasMoreTokens())
            {
               String token = st.nextToken();
               int equalIndex = token.indexOf("=");
               String param = token.substring(0, equalIndex).trim();
               String value = token.substring(equalIndex + 1);
               if(param.equals("ordini"))
               {
                  if(value.trim().equals("false"))
                     ordini = false;
               }
               else if(param.equals("richieste"))
               {
                  if(value.trim().equals("false"))
                     richieste = false;
               }
               else if(param.equals("documenti"))
               {
                  if(value.trim().equals("false"))
                     documenti = false;
               }
               else if(param.equals("ValorizzaCosto"))
               {
                  if(value.trim().equals("true"))
                     valorizzaCosto = true;
               }
            }

            if(documenti || ordini || richieste)
            {
               ClassAD classAD = getClassADCollection().getAttribute("DocumentoOrigine");
               LiteralCriterion lc = new LiteralCriterion();
               lc.setAttribute(classAD);
               lc.setOperator(EqualOperator.getInstance());
               int idx = 0;
               if(documenti)
                  lc.setValue(idx++, 0, "2");
               if(ordini)
                  lc.setValue(idx++, 0, "1");
               if(richieste)
                  lc.setValue(idx++, 0, "0");
               if(idx > 0)
                  addRestrictCondition(lc);
            }

            if(valorizzaCosto)
            {   
               ClassAD classAD1 = getClassADCollection().getAttribute("ValorizzaCosto");
               LiteralCriterion lc1 = new LiteralCriterion();
               lc1.setAttribute(classAD1);
               lc1.setOperator(NotEqualOperator.getInstance());
               lc1.setValue(0, 0, "N");
               addRestrictCondition(lc1);
            }
         }
      }

   }
   //33950 fine


}

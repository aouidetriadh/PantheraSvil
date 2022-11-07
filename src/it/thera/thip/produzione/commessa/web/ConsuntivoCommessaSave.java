package it.thera.thip.produzione.commessa.web;

import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.Save;

public class ConsuntivoCommessaSave extends Save
{
   public void actionOnObject( BODataCollector boDC, ServletEnvironment se)
   {
      boDC.setCheckKey(false);
      super.actionOnObject(boDC, se);
   }
}

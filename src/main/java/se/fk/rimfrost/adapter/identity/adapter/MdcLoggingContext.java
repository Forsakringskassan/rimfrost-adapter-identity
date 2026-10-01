package se.fk.rimfrost.adapter.identity.adapter;

import org.slf4j.MDC;
import se.fk.github.logging.callerinfo.model.MDCKeys;

import java.util.UUID;

/**
 * MDC Logging context that works around FK-logging potentially
 * putting null values in header map if process id or breadcrumb
 * id MDC values are not defined, thus breaking quarkus rest
 * client handling. The context falls back to similar handling
 * as FK-logging LoggingContextHttpRequestFilter when keys are
 * not defined and conditionally removes inserted keys on closure.
 */
public final class MdcLoggingContext implements AutoCloseable
{
   String processId;
   String breadcrumbId;

   public MdcLoggingContext()
   {
      processId = MDC.get(MDCKeys.PROCESSID.name());
      breadcrumbId = MDC.get(MDCKeys.BREADCRUMBID.name());

      if (processId == null)
      {
         MDC.put(MDCKeys.PROCESSID.name(), "");
      }

      if (breadcrumbId == null)
      {
         MDC.put(MDCKeys.BREADCRUMBID.name(), UUID.randomUUID().toString());
      }
   }

   @Override
   public void close() throws Exception
   {
      if (processId == null)
      {
         MDC.remove(MDCKeys.PROCESSID.name());
      }

      if (breadcrumbId == null)
      {
         MDC.remove(MDCKeys.BREADCRUMBID.name());
      }
   }
}

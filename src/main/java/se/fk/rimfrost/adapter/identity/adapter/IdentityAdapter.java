package se.fk.rimfrost.adapter.identity.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.rimfrost.adapter.identity.exception.IdentityException;
import se.fk.rimfrost.adapter.identity.model.Idtyp;
import se.fk.rimfrost.adapter.identity.model.ImmutableIdtyp;

/**
 * Adapter for querying the identity service.
 *
 * <p>Manages a quarkus rest client lifecycle and translates domain model types
 * from identity API responses, mapping HTTP error responses to typed {@link IdentityException}s.
 */
@SuppressWarnings("unused")
@ApplicationScoped
public class IdentityAdapter
{
   private static final Logger LOGGER = LoggerFactory.getLogger(IdentityAdapter.class);

   @RestClient
   IdentityClient identityClient;

   public Idtyp getIdentity(String authorizationHeaderValue) throws IdentityException
   {
      try (MdcLoggingContext ignore = new MdcLoggingContext())
      {
         var response = identityClient.getIdentity(authorizationHeaderValue);

         if (response == null || response.getIdentity() == null)
         {
            throw new IdentityException(IdentityException.ErrorType.UNEXPECTED_ERROR,
                  "Received unexpected response from identity service");
         }

         return ImmutableIdtyp.builder().typId(response.getIdentity().getTypId()).varde(response.getIdentity().getVarde())
               .build();
      }
      catch (IdentityException e)
      {
         throw e;
      }
      catch (ClientWebApplicationException e)
      {
         var errorType = IdentityException.ErrorType.UNEXPECTED_ERROR;
         var message = "An unexpected error occurred when checking identity";

         if (e.getCause() != null && e.getCause() instanceof WebApplicationException ex)
         {
            if (ex.getResponse() != null)
            {
               if (ex.getResponse().getStatus() == 400)
               {
                  errorType = IdentityException.ErrorType.BAD_REQUEST;
                  message = "Request rejected as invalid by identity service";
               }
               else if (ex.getResponse().getStatus() == 404)
               {
                  errorType = IdentityException.ErrorType.NOT_FOUND;
                  message = "Service path not found while attempting to check identity";
               }
               else if (ex.getResponse().getStatus() == 401)
               {
                  errorType = IdentityException.ErrorType.UNAUTHORIZED;
                  message = "Authorization credentials rejected by identity service";
               }
               else if (ex.getResponse().getStatus() == 503)
               {
                  errorType = IdentityException.ErrorType.SERVICE_UNAVAILABLE;
                  message = "Identity service unavailable";
               }
            }
         }

         LOGGER.error(message, e);
         throw new IdentityException(errorType, message, e);
      }
      catch (Exception e)
      {
         var message = "An unexpected error occurred when checking identity";
         LOGGER.error(message, e);
         throw new IdentityException(IdentityException.ErrorType.UNEXPECTED_ERROR, e.getMessage(), e);
      }
   }
}

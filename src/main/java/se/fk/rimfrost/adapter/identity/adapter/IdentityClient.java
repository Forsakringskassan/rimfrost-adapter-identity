package se.fk.rimfrost.adapter.identity.adapter;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import se.fk.rimfrost.identity.jaxrsspec.controllers.generatedsource.model.GetIdentityResponse;

@RegisterRestClient(configKey = "identity-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IdentityClient
{
   @GET
   @Path("/identity")
   @ClientHeaderParam(name = HttpHeaders.AUTHORIZATION, value = "{authorizationHeaderValue}")
   GetIdentityResponse getIdentity(String authorizationHeaderValue);
}

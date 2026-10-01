package se.fk.rimfrost.adapter.identity;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.fk.rimfrost.adapter.identity.adapter.IdentityAdapter;
import se.fk.rimfrost.adapter.identity.exception.IdentityException;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class IdentityAdapterTest
{
   private static WireMockServer server;

   @Inject
   IdentityAdapter identityAdapter;

   @BeforeAll
   public static void setup()
   {
      server = new WireMockServer(
            options()
                  .dynamicPort());
      server.start();

      System.setProperty("quarkus.rest-client.identity-api.url", server.baseUrl());
   }

   @AfterAll
   public static void tearDown()
   {
      if (server != null)
      {
         server.stop();
         server = null;
      }
   }

   @BeforeEach
   void resetStubs()
   {
      server.resetToDefaultMappings();
   }

   @Test
   @DisplayName("IDENT-FR-01.1: Identity should be returned on successful request")
   void should_return_expected_identity_on_success() throws IdentityException
   {
      String expectedTypId = UUID.randomUUID().toString();
      String expectedValue = UUID.randomUUID().toString();
      String expectedHeaderValue = "Bearer " + expectedTypId + ":" + expectedValue;

      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .withHeader(HttpHeaders.AUTHORIZATION, WireMock.equalTo(expectedHeaderValue))
            .willReturn(WireMock.aResponse().withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                  .withBody("{\"identity\": {\"typId\": \"" + expectedTypId + "\", \"value\": \"" + expectedValue
                        + "\", \"varde\": \"" + expectedValue + "\"}}")));

      var identity = identityAdapter.getIdentity(expectedHeaderValue);

      assertNotNull(identity);
      assertEquals(expectedTypId, identity.typId());
      assertEquals(expectedValue, identity.varde());
   }

   @Test
   @DisplayName("IDENT-FR-01.2: IdentityException with ErrorType.BAD_REQUEST should be thrown for status 400")
   void should_throw_with_error_type_bad_request_on_status_400()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .willReturn(WireMock.aResponse().withStatus(400)));

      var exception = assertThrows(IdentityException.class, () -> identityAdapter.getIdentity("header value"));
      assertEquals(IdentityException.ErrorType.BAD_REQUEST, exception.getErrorType());
   }

   @Test
   @DisplayName("IDENT-FR-01.3: IdentityException with ErrorType.UNAUTHORIZED should be thrown for status 401")
   void should_throw_with_error_type_unauthorized_on_status_401()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .willReturn(WireMock.aResponse().withStatus(401)));

      var exception = assertThrows(IdentityException.class, () -> identityAdapter.getIdentity("header value"));
      assertEquals(IdentityException.ErrorType.UNAUTHORIZED, exception.getErrorType());
   }

   @Test
   @DisplayName("IDENT-FR-01.4: IdentityException with ErrorType.NOT_FOUND should be thrown for status 404")
   void should_throw_with_error_type_not_found_on_status_404()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      var exception = assertThrows(IdentityException.class, () -> identityAdapter.getIdentity("header value"));
      assertEquals(IdentityException.ErrorType.NOT_FOUND, exception.getErrorType());
   }

   @Test
   @DisplayName("IDENT-FR-01.5: IdentityException with ErrorType.SERVICE_UNAVAILABLE should be thrown for status 503")
   void should_throw_with_error_type_service_unavailable_on_status_503()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .willReturn(WireMock.aResponse().withStatus(503)));

      var exception = assertThrows(IdentityException.class, () -> identityAdapter.getIdentity("header value"));
      assertEquals(IdentityException.ErrorType.SERVICE_UNAVAILABLE, exception.getErrorType());
   }

   @Test
   @DisplayName("IDENT-FR-01.6: IdentityException with ErrorType.UNEXPECTED_ERROR should be thrown for status 500")
   void should_throw_with_error_type_unexpected_error_on_status_500()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .willReturn(WireMock.aResponse().withStatus(500)));

      var exception = assertThrows(IdentityException.class, () -> identityAdapter.getIdentity("header value"));
      assertEquals(IdentityException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @Test
   @DisplayName("IDENT-FR-01.6: IdentityException with ErrorType.UNEXPECTED_ERROR should be thrown for missing response body")
   void should_throw_with_error_type_unexpected_error_on_null_response()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .willReturn(WireMock.aResponse().withBody((String) null)));

      var exception = assertThrows(IdentityException.class, () -> identityAdapter.getIdentity("header value"));
      assertEquals(IdentityException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @Test
   @DisplayName("IDENT-FR-01.7: Authorized header should not be sent for null header value")
   void should_not_send_authorization_header_on_null_value() throws IdentityException
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/identity"))
            .withHeader(HttpHeaders.AUTHORIZATION, WireMock.notMatching(".*"))
            .willReturn(WireMock.aResponse().withStatus(401)));

      var exception = assertThrows(IdentityException.class, () -> identityAdapter.getIdentity(null));
      assertEquals(IdentityException.ErrorType.UNAUTHORIZED, exception.getErrorType());
   }
}

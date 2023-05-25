package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.agent.lib.WebAgent;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialAlreadyStoredException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialNotFoundException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

/** Non-Internet facing API for other connector to request verifiable credentials */
@Produces({MediaType.APPLICATION_JSON})
@Path("/agent")
@RequiredArgsConstructor
public class EmbeddedAgentController {

  private final WebAgent agent;
  private final Monitor monitor;

  // maybe https://w3c-ccg.github.io/vp-request-spec/ ?
  @GET
  @Path("/verifiable-credential/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response requestCredential(@PathParam("id") String id) {

    try {

      final VerifiableCredential credential = agent.getCredentialById(new URI(id));
      monitor.info(String.format("Credential found (id=%s)", credential.getId()));
      return Response.ok(credential).build();

    } catch (CredentialNotFoundException e) {
      monitor.info("Credential not found", e);
      return Response.status(Response.Status.NOT_FOUND).build();
    } catch (URISyntaxException e) {
      monitor.info("Invalid Credential ID", e);
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
  }

  @GET
  @Path("/verifiable-credential")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response requestAllCredentials() {

    final List<VerifiableCredential> credentials = agent.getAllCredentials();

    monitor.info(String.format("Credential found (count=%s)", credentials.size()));
    return Response.ok(credentials).build();
  }

  @POST
  @Path("/verifiable-credential")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createCredential(Map<String, Object> credentialJson) {

    try {
      final VerifiableCredential verifiableCredential = new VerifiableCredential(credentialJson);
      agent.storeCredential(verifiableCredential);

      final URI location = URI.create("/verifiable-credential/" + verifiableCredential.getId());
      return Response.created(location).build();

    } catch (CredentialAlreadyStoredException e) {
      return Response.status(Response.Status.CONFLICT).build();
    }
  }
}

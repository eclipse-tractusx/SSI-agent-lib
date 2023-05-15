package org.eclipse.tractusx.ssi.lab.system.test.client.agent;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.agent.client.ApiClient;
import org.eclipse.tractusx.ssi.agent.client.ApiException;
import org.eclipse.tractusx.ssi.agent.client.api.VerifiableCredentialsApi;
import org.eclipse.tractusx.ssi.agent.client.model.VerifiableCredential;
import org.eclipse.tractusx.ssi.lab.system.test.client.Connector;
import org.eclipse.tractusx.ssi.lab.system.test.client.SsiAgent;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.model.Proof;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;

@RequiredArgsConstructor
public class AgentClient {

  private final SsiAgent agent;

  public org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
      issueMembershipCredential(Connector connector) {

    final Did connectorDid =
        DidWebFactory.fromHostname(
            connector.getHostName().getInternal() + ":" + connector.getDidDocumentApi().getPort());

    return issueCredential(
        connector,
        VerifiableCredentialType.MEMBERSHIP_CREDENTIAL,
        Map.of("id", connectorDid.toString(), "bpn", connector.getBpn()));
  }

  public org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
      issueDismantlerCredential(Connector connector, String activityType) {

    final Did connectorDid =
        DidWebFactory.fromHostname(
            connector.getHostName().getInternal() + ":" + connector.getDidDocumentApi().getPort());
    return issueCredential(
        connector,
        "DismantlerCredential",
        Map.of("id", connectorDid.toString(), "activity", Map.of("activityType", activityType)));
  }

  @SneakyThrows(ApiException.class)
  public org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
      issueCredential(
          Connector connector, String credentialType, Map<String, Object> credentialSubject) {
    final String agentUrl =
        String.format("http://%s:%s", agent.getHostName(), agent.getAgentApi().getPort());
    final ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(agentUrl);
    final VerifiableCredentialsApi credentialsApi = new VerifiableCredentialsApi(apiClient);

    final Did connectorDid =
        DidWebFactory.fromHostname(
            connector.getHostName().getInternal() + ":" + connector.getDidDocumentApi().getPort());

    final VerifiableCredential membershipCredential = new VerifiableCredential();
    membershipCredential.setAtContext(List.of("https://www.w3.org/2018/credentials/v1"));
    membershipCredential.setId(URI.create(UUID.randomUUID().toString()));
    membershipCredential.setType(
        List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL, credentialType));
    membershipCredential.credentialSubject(List.of(credentialSubject));
    membershipCredential.setExpirationDate(OffsetDateTime.now().plusDays(1));
    membershipCredential.setIssuer(connectorDid.toUri());
    membershipCredential.setIssuanceDate(OffsetDateTime.now());

    final VerifiableCredential signedCredential =
        credentialsApi.signCredential(membershipCredential);
    return map(signedCredential);
  }

  private org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential map(
      VerifiableCredential credential) {

    final VerifiableCredentialBuilder builder = new VerifiableCredentialBuilder();
    builder.context(credential.getAtContext());
    builder.id(credential.getId());
    builder.type(credential.getType());
    builder.issuer(credential.getIssuer());
    builder.issuanceDate(credential.getIssuanceDate().toInstant());

    if (credential.getExpirationDate() != null) {
      builder.expirationDate(credential.getExpirationDate().toInstant());
    }

    if (credential.getProof() != null) {
      Proof proof = new Proof(credential.getProof());
      builder.proof(proof);
    }

    final List<VerifiableCredentialSubject> subject =
        credential.getCredentialSubject().stream()
            .map(VerifiableCredentialSubject::new)
            .collect(Collectors.toList());

    builder.credentialSubject(subject);

    return builder.build();
  }
}

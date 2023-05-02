package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import com.danubetech.verifiablecredentials.CredentialSubject;
import info.weboftrust.ldsignatures.LdProof;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.PackagePrivate;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.model.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

@PackagePrivate
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DanubTechMapper {
  @NonNull
  public static com.danubetech.verifiablecredentials.VerifiablePresentation map(
      VerifiablePresentation presentation) {

    final List<com.danubetech.verifiablecredentials.VerifiableCredential> dtCredentials =
        presentation.getVerifiableCredentials().stream()
            .map(DanubTechMapper::map)
            .collect(Collectors.toList());

    // TODO Throw Exception if more or less than one

    if (!presentation.getTypes().get(0).equals("VerifiablePresentation")) {
      throw new SsiException("Type: VerifiablePresentation missing");
    }
    // VerifiablePresentation Type is automatically added in Builder
    List<String> types = new ArrayList<>(presentation.getTypes());
    types.remove(0); // TODO remove default type differently

    com.danubetech.verifiablecredentials.VerifiablePresentation.Builder<
            ? extends com.danubetech.verifiablecredentials.VerifiablePresentation.Builder<?>>
        builder = com.danubetech.verifiablecredentials.VerifiablePresentation.builder();

    builder
        .defaultContexts(true)
        .forceContextsArray(true)
        .forceTypesArray(true)
        .id(presentation.getId())
        .types(types)
        .holder(presentation.getHolder())
        .ldProof(null); // set to null, as presentation will be used within JWT

    // TODO handle more than one verifiable credential per presentation
    if (dtCredentials.size() > 1) {
      throw new RuntimeException(
          "More than one verifiable credential per presentation is not supported"); // TODO
    }
    if (dtCredentials.size() == 1) {
      builder.verifiableCredential(dtCredentials.get(0));
    }

    return builder.build();
  }

  @NonNull
  @SneakyThrows
  public static VerifiablePresentation map(
      com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation) {

    Objects.requireNonNull(dtPresentation);
    Objects.requireNonNull(dtPresentation.getVerifiableCredential());

    List<VerifiableCredential> credentials = List.of(map(dtPresentation.getVerifiableCredential()));

    return VerifiablePresentation.builder()
        .id(dtPresentation.getId())
        .types(dtPresentation.getTypes())
        .verifiableCredentials(credentials)
        .holder(dtPresentation.getHolder())
        .build();
  }

  @NonNull
  @SneakyThrows
  public static com.danubetech.verifiablecredentials.VerifiableCredential map(
      VerifiableCredential credential) {

    if (credential.getTypes().stream()
        .noneMatch(x -> x.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))) {
      throw new SsiException("Type: VerifiableCredential missing");
    }
    // Verifiable Credential Type is automatically added in Builder
    final List<String> types =
        credential.getTypes().stream()
            .filter(t -> !t.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .collect(Collectors.toList());

    final CredentialSubject subject =
        CredentialSubject.builder().properties(credential.getCredentialSubject()).build();

    return com.danubetech.verifiablecredentials.VerifiableCredential.builder()
        .defaultContexts(true)
        .forceContextsArray(true)
        .forceTypesArray(true)
        .id(credential.getId())
        .types(types)
        .issuer(credential.getIssuer())
        .issuanceDate(Date.from(credential.getIssuanceDate()))
        .expirationDate(Date.from(credential.getExpirationDate()))
        .credentialSubject(subject)
        .ldProof(map(credential.getProof()))
        .build();
    // .credentialStatus(credential.getStatus())
  }

  @NonNull
  @SneakyThrows
  public static VerifiableCredential map(
      com.danubetech.verifiablecredentials.VerifiableCredential dtCredential) throws SsiException {
    return new VerifiableCredential(dtCredential.toMap());
  }

  private static VerifiableCredentialSubject map(CredentialSubject dtSubject) {
    if (dtSubject == null) return null;

    var subject = new VerifiableCredentialSubject();
    subject.putAll(dtSubject.getClaims());
    return subject;
  }

  private static Proof map(LdProof dtProof) {
    if (dtProof == null) return null;

    return new Proof(dtProof.toMap());
  }

  private static LdProof map(Proof proof) {
    if (proof == null) return null;

    return LdProof.fromMap(proof);
  }
}

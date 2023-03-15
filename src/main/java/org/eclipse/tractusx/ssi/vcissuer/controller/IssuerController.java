package org.eclipse.tractusx.ssi.vcissuer.controller;

import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.vcissuer.exception.DidParseException;
import org.eclipse.tractusx.ssi.vcissuer.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.vcissuer.proof.LinkedDataProofGenerator;
import org.eclipse.tractusx.ssi.vcissuer.spi.Ed25519Proof;
import org.eclipse.tractusx.ssi.vcissuer.spi.did.Did;
import org.eclipse.tractusx.ssi.vcissuer.spi.did.DidParser;
import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.vcissuer.web.DidWebFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringReader;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vc")
public class IssuerController {

    private final Logger logger = LoggerFactory.getLogger(IssuerController.class);

    @Value("${ed25519.pem}")
    private String privateKey;

    @Value("${host.name}")
    private String hostName;

    @GetMapping(value = "/membership/{did}/{bpn}", produces = "application/ld+json;vc=ldp-v1.0")
    public ResponseEntity didConfiguration(@PathVariable String did, @PathVariable String bpn) {

        try {
            DidParser.parse(did);
        } catch (DidParseException e) {
            logger.error("Invalid DID: {}", did);
            return new ResponseEntity<>("Invalid DID", HttpStatus.BAD_REQUEST);
        }

        final Did issuer = DidWebFactory.fromHostname(hostName);
        final VerifiableCredentialSubject subject = new VerifiableCredentialSubject();
        subject.put("id", did);
        subject.put("businessPartnerNumber", bpn);
        final VerifiableCredential.VerifiableCredentialBuilder vcBuilder = VerifiableCredential.builder()
                .id(URI.create(issuer.toUri() + "#" + UUID.randomUUID()))
                .issuer(issuer.toUri())
                .issuanceDate(Instant.now())
                .credentialSubject(subject)
                .expirationDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .types(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL, VerifiableCredentialType.MEMBERSHIP_CREDENTIAL));

        LinkedDataProofGenerator validation = LinkedDataProofGenerator.create();
        final Ed25519Proof proof = validation.createProof(vcBuilder.build(), URI.create(issuer + "#key-1"), readPrivateKey());
        final VerifiableCredential.VerifiableCredentialBuilder vcWithProf = vcBuilder.proof(proof);
        final JsonLdSerializerImpl serializer = new JsonLdSerializerImpl();
        final String json = serializer.serializeVerifiableCredential(vcWithProf.build());

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @SneakyThrows
    public byte[] readPrivateKey() {
        final PemReader reader = new PemReader(new StringReader(privateKey));
        return reader.readPemObject().getContent();
    }
}

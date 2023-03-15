package org.eclipse.tractusx.ssi.vcissuer.controller;

import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.vcissuer.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.vcissuer.spi.MultibaseString;
import org.eclipse.tractusx.ssi.vcissuer.spi.did.Did;
import org.eclipse.tractusx.ssi.vcissuer.web.DidWebFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/.well-known")
public class DocumentController {

    public static final String DOCUMENT = """
            {
              "@context": [
                "https://www.w3.org/ns/did/v1",
              ],
              "id": "<did>",
              "verificationMethod": [{
                "id": "<did>#key-1",
                "type": "Ed25519VerificationKey2020",
                "controller": "<did>",
                "publicKeyMultibase": "<key>"
              }],
            }
            """;

    private final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private static final String REGEX_SSH = "ssh-ed25519 (?<base64>[a-zA-Z0-9+/=]*) .*";
    private static final String REGEX_GROUP_BASE64 = "base64";

    @Value("${ed25519.pub}")
    private String publicKey;

    @Value("${host.name}")
    private String hostName;

    @SneakyThrows
    @GetMapping(value = "/did.json", produces = "application/json")
    public String getDocument() {

        final Did did = DidWebFactory.fromHostname(hostName);

        final byte[] publicKey64 = readPublicKey();
        final MultibaseString publicKeyBase = MultibaseFactory.create(publicKey64);

        return DOCUMENT.replace("<did>", did.toString()).replace("<key>", publicKeyBase.getEncoded());
    }

    public byte[] readPublicKey() {

        logger.debug("Configured public key: {}", publicKey);
        final Pattern pattern = Pattern.compile(REGEX_SSH);
        final Matcher matcher = pattern.matcher(publicKey);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Public key is not in the correct format");
        }

        final String base64 = matcher.group(REGEX_GROUP_BASE64);
        logger.debug("Configured public key: {}", base64);

        return Base64.getDecoder().decode(base64);
    }
}

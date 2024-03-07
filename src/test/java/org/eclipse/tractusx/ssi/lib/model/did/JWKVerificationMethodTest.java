package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class JWKVerificationMethodTest {

  static final String EC_SECP256k1_JWK_STRING =
      "{\n"
          + "      \"id\": \"did:example:123#4SZ-StXrp5Yd4_4rxHVTCYTHyt4zyPfN1fIuYsm6k3A\",\n"
          + "      \"type\": \"JsonWebKey2020\",\n"
          + "      \"controller\": \"did:example:123\",\n"
          + "      \"publicKeyJwk\": {\n"
          + "        \"kty\": \"EC\",\n"
          + "        \"crv\": \"secp256k1\",\n"
          + "        \"x\": \"Z4Y3NNOxv0J6tCgqOBFnHnaZhJF6LdulT7z8A-2D5_8\",\n"
          + "        \"y\": \"i5a2NtJoUKXkLm6q8nOEu9WOkso1Ag6FTUT6k_LMnGk\"\n"
          + "      }\n"
          + "    }";

  static final String RSA_JWK_STRING =
      "{\n"
          + "      \"id\": \"did:example:123#n4cQ-I_WkHMcwXBJa7IHkYu8CMfdNcZKnKsOrnHLpFs\",\n"
          + "      \"type\": \"JsonWebKey2020\",\n"
          + "      \"controller\": \"did:example:123\",\n"
          + "      \"publicKeyJwk\": {\n"
          + "        \"kty\": \"RSA\",\n"
          + "        \"e\": \"AQAB\",\n"
          + "        \"n\": \"omwsC1AqEk6whvxyOltCFWheSQvv1MExu5RLCMT4jVk9khJKv8JeMXWe3bWHatjPskdf2dlaGkW5QjtOnUKL742mvr4tCldKS3ULIaT1hJInMHHxj2gcubO6eEegACQ4QSu9LO0H-LM_L3DsRABB7Qja8HecpyuspW1Tu_DbqxcSnwendamwL52V17eKhlO4uXwv2HFlxufFHM0KmCJujIKyAxjD_m3q__IiHUVHD1tDIEvLPhG9Azsn3j95d-saIgZzPLhQFiKluGvsjrSkYU5pXVWIsV-B2jtLeeLC14XcYxWDUJ0qVopxkBvdlERcNtgF4dvW4X00EHj4vCljFw\"\n"
          + "      }\n"
          + "    }";

  static final String EC_P256_JWK_STRING =
      "{\n"
          + "      \"id\": \"did:example:123#_TKzHv2jFIyvdTGF1Dsgwngfdg3SH6TpDv0Ta1aOEkw\",\n"
          + "      \"type\": \"JsonWebKey2020\",\n"
          + "      \"controller\": \"did:example:123\",\n"
          + "      \"publicKeyJwk\": {\n"
          + "        \"kty\": \"EC\",\n"
          + "        \"crv\": \"P-256\",\n"
          + "        \"x\": \"38M1FDts7Oea7urmseiugGW7tWc3mLpJh6rKe7xINZ8\",\n"
          + "        \"y\": \"nDQW6XZ7b_u2Sy9slofYLlG03sOEoug3I0aAPQ0exs4\"\n"
          + "      }\n"
          + "    }";

  static final String EC_P384_JWK_STRING =
      "{\n"
          + "      \"id\": \"did:example:123#8wgRfY3sWmzoeAL-78-oALNvNj67ZlQxd1ss_NX1hZY\",\n"
          + "      \"type\": \"JsonWebKey2020\",\n"
          + "      \"controller\": \"did:example:123\",\n"
          + "      \"publicKeyJwk\": {\n"
          + "        \"kty\": \"EC\",\n"
          + "        \"crv\": \"P-384\",\n"
          + "        \"x\": \"GnLl6mDti7a2VUIZP5w6pcRX8q5nvEIgB3Q_5RI2p9F_QVsaAlDN7IG68Jn0dS_F\",\n"
          + "        \"y\": \"jq4QoAHKiIzezDp88s_cxSPXtuXYFliuCGndgU4Qp8l91xzD1spCmFIzQgVjqvcP\"\n"
          + "      }\n"
          + "    }";

  static final String EC_P521_JWK_STRING =
      "{\n"
          + "      \"id\": \"did:example:123#NjQ6Y_ZMj6IUK_XkgCDwtKHlNTUTVjEYOWZtxhp1n-E\",\n"
          + "      \"type\": \"JsonWebKey2020\",\n"
          + "      \"controller\": \"did:example:123\",\n"
          + "      \"publicKeyJwk\": {\n"
          + "        \"kty\": \"EC\",\n"
          + "        \"crv\": \"P-521\",\n"
          + "        \"x\": \"AVlZG23LyXYwlbjbGPMxZbHmJpDSu-IvpuKigEN2pzgWtSo--Rwd-n78nrWnZzeDc187Ln3qHlw5LRGrX4qgLQ-y\",\n"
          + "        \"y\": \"ANIbFeRdPHf1WYMCUjcPz-ZhecZFybOqLIJjVOlLETH7uPlyG0gEoMWnIZXhQVypPy_HtUiUzdnSEPAylYhHBTX2\"\n"
          + "\n"
          + "      }\n"
          + "    }";

  static final String ED_JWK_STRING =
      "{\n"
          + "      \"id\": \"did:example:123#_Qq0UL2Fq651Q0Fjd6TvnYE-faHiOpRlPVQcY_-tA4A\",\n"
          + "      \"type\": \"JsonWebKey2020\",\n"
          + "      \"controller\": \"did:example:123\",\n"
          + "      \"publicKeyJwk\": {\n"
          + "        \"kty\": \"OKP\",\n"
          + "        \"crv\": \"Ed25519\",\n"
          + "        \"x\": \"VCpo2LMLhn6iWku8MKvSLg2ZAoC-nlOyPVQaO3FxVeQ\"\n"
          + "      }\n"
          + "    }";

  @Test
  void testSecp256k1() throws ParseException {
    Map<String, Object> parsed = JSONObjectUtils.parse(EC_SECP256k1_JWK_STRING);
    JWKVerificationMethod abstractJWKVerificationMethod = new JWKVerificationMethod(parsed);
    JWK jwk = abstractJWKVerificationMethod.getJwk();
    assertEquals("EC", jwk.getKeyType().getValue());
    assertEquals("secp256k1", ((ECKey) jwk).getCurve().getName());
    assertEquals("Z4Y3NNOxv0J6tCgqOBFnHnaZhJF6LdulT7z8A-2D5_8", ((ECKey) jwk).getX().toString());
    assertEquals("i5a2NtJoUKXkLm6q8nOEu9WOkso1Ag6FTUT6k_LMnGk", ((ECKey) jwk).getY().toString());
  }

  @Test
  void testRSA() throws ParseException {
    Map<String, Object> parsed = JSONObjectUtils.parse(RSA_JWK_STRING);
    JWKVerificationMethod abstractJWKVerificationMethod = new JWKVerificationMethod(parsed);
    JWK jwk = abstractJWKVerificationMethod.getJwk();
    assertEquals("RSA", jwk.getKeyType().getValue());
    assertEquals("AQAB", ((RSAKey) jwk).getPublicExponent().toString());
    assertEquals(
        "omwsC1AqEk6whvxyOltCFWheSQvv1MExu5RLCMT4jVk9khJKv8JeMXWe3bWHatjPskdf2dlaGkW5QjtOnUKL742mvr4tCldKS3ULIaT1hJInMHHxj2gcubO6eEegACQ4QSu9LO0H-LM_L3DsRABB7Qja8HecpyuspW1Tu_DbqxcSnwendamwL52V17eKhlO4uXwv2HFlxufFHM0KmCJujIKyAxjD_m3q__IiHUVHD1tDIEvLPhG9Azsn3j95d-saIgZzPLhQFiKluGvsjrSkYU5pXVWIsV-B2jtLeeLC14XcYxWDUJ0qVopxkBvdlERcNtgF4dvW4X00EHj4vCljFw",
        ((RSAKey) jwk).getModulus().toString());
  }

  @Test
  void testP256() throws ParseException {
    Map<String, Object> parsed = JSONObjectUtils.parse(EC_P256_JWK_STRING);
    JWKVerificationMethod abstractJWKVerificationMethod = new JWKVerificationMethod(parsed);
    JWK jwk = abstractJWKVerificationMethod.getJwk();
    assertEquals("EC", jwk.getKeyType().getValue());
    assertEquals("P-256", ((ECKey) jwk).getCurve().getName());
    assertEquals("38M1FDts7Oea7urmseiugGW7tWc3mLpJh6rKe7xINZ8", ((ECKey) jwk).getX().toString());
    assertEquals("nDQW6XZ7b_u2Sy9slofYLlG03sOEoug3I0aAPQ0exs4", ((ECKey) jwk).getY().toString());
  }

  @Test
  void testP384() throws ParseException {
    Map<String, Object> parsed = JSONObjectUtils.parse(EC_P384_JWK_STRING);
    JWKVerificationMethod abstractJWKVerificationMethod = new JWKVerificationMethod(parsed);
    JWK jwk = abstractJWKVerificationMethod.getJwk();
    assertEquals("EC", jwk.getKeyType().getValue());
    assertEquals("P-384", ((ECKey) jwk).getCurve().getName());
    assertEquals(
        "GnLl6mDti7a2VUIZP5w6pcRX8q5nvEIgB3Q_5RI2p9F_QVsaAlDN7IG68Jn0dS_F",
        ((ECKey) jwk).getX().toString());
    assertEquals(
        "jq4QoAHKiIzezDp88s_cxSPXtuXYFliuCGndgU4Qp8l91xzD1spCmFIzQgVjqvcP",
        ((ECKey) jwk).getY().toString());
  }

  @Test
  void testP521() throws ParseException {
    Map<String, Object> parsed = JSONObjectUtils.parse(EC_P521_JWK_STRING);
    JWKVerificationMethod abstractJWKVerificationMethod = new JWKVerificationMethod(parsed);
    JWK jwk = abstractJWKVerificationMethod.getJwk();
    assertEquals("EC", jwk.getKeyType().getValue());
    assertEquals("P-521", ((ECKey) jwk).getCurve().getName());
    assertEquals(
        "AVlZG23LyXYwlbjbGPMxZbHmJpDSu-IvpuKigEN2pzgWtSo--Rwd-n78nrWnZzeDc187Ln3qHlw5LRGrX4qgLQ-y",
        ((ECKey) jwk).getX().toString());
    assertEquals(
        "ANIbFeRdPHf1WYMCUjcPz-ZhecZFybOqLIJjVOlLETH7uPlyG0gEoMWnIZXhQVypPy_HtUiUzdnSEPAylYhHBTX2",
        ((ECKey) jwk).getY().toString());
  }

  @Test
  void testED() throws ParseException {
    Map<String, Object> parsed = JSONObjectUtils.parse(ED_JWK_STRING);
    JWKVerificationMethod abstractJWKVerificationMethod = new JWKVerificationMethod(parsed);
    JWK jwk = abstractJWKVerificationMethod.getJwk();
    assertEquals("OKP", jwk.getKeyType().getValue());
    assertEquals("Ed25519", ((OctetKeyPair) jwk).getCurve().getName());
    assertEquals(
        "VCpo2LMLhn6iWku8MKvSLg2ZAoC-nlOyPVQaO3FxVeQ", ((OctetKeyPair) jwk).getX().toString());
  }

  @Test
  void shouldThrowWhenIllegalType() {
    Map<String, Object> map =
        Map.of(
            "type",
            "Ed25519VerificationKey2069",
            "id",
            UUID.randomUUID().toString(),
            "controller",
            UUID.randomUUID().toString());
    assertThrows(IllegalArgumentException.class, () -> new JWKVerificationMethod(map));
  }

  @Test
  void shouldThrowWhenInvalidPublicKeyJWK() {
    Map<String, Object> map =
        Map.of(
            "type",
            JWKVerificationMethod.DEFAULT_TYPE,
            "id",
            UUID.randomUUID().toString(),
            "controller",
            UUID.randomUUID().toString(),
            JWKVerificationMethod.PUBLIC_KEY_JWK,
            42);
    assertThrows(IllegalArgumentException.class, () -> new JWKVerificationMethod(map));
  }
}

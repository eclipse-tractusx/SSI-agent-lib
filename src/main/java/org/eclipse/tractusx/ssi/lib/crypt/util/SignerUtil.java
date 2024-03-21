/*
 * ******************************************************************************
 * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.crypt.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jwk.OctetKeyPair;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureGenerateFailedException;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;

public class SignerUtil {

  private SignerUtil() {
    // all static
  }

  public static JWSSigner getSigner(SignatureType type, IPrivateKey privateKey)
      throws JOSEException, SignatureGenerateFailedException {
    return switch (type) {
      case JWS -> SignerUtil.getEDSigner(privateKey);
      case JWS_P256, JWS_P384, JWS_SEC_P_256K1 -> SignerUtil.getECSigner(privateKey);
      case JWS_RSA -> SignerUtil.getRSASigner(privateKey);
      default -> throw new IllegalArgumentException(
          String.format("algorithm %s is not supported", type.algorithm));
    };
  }

  public static JWSSigner getECSigner(IPrivateKey privateKey) throws JOSEException {
    ECDSASigner ecdsaSigner = new ECDSASigner(getECPrivateKey(privateKey.asByte()));
    // this is necessary because of issue:
    // https://bitbucket.org/connect2id/nimbus-jose-jwt/issues/458/comnimbusdsjosejoseexception-curve-not
    ecdsaSigner.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());
    return ecdsaSigner;
  }

  public static JWSSigner getRSASigner(IPrivateKey privateKey) {
    return new RSASSASigner(getRSAPrivateKey(privateKey.asByte()));
  }

  // input must be that of privateKey.getEncoded()
  public static ECPrivateKey getECPrivateKey(byte[] keyBytes) {
    try {
      KeyFactory kf = KeyFactory.getInstance("EC"); // or "EC" or whatever
      return (ECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException(e);
    }
  }

  // input must be that of privateKey.getEncoded()
  public static RSAPrivateKey getRSAPrivateKey(byte[] keyBytes) {
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
      return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException(e);
    }
  }

  public static JWSSigner getEDSigner(IPrivateKey privateKey)
      throws SignatureGenerateFailedException {
    OctetKeyPairFactory octetKeyPairFactory = new OctetKeyPairFactory();
    OctetKeyPair keyPair = octetKeyPairFactory.fromPrivateKey(privateKey);

    JWSSigner signer;
    try {
      signer = new Ed25519Signer(keyPair);
    } catch (JOSEException e) {
      throw new SignatureGenerateFailedException(e.getMessage());
    }

    return signer;
  }
}

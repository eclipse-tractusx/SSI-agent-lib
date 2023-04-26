package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.webagent;

import java.io.IOException;
import java.io.StringReader;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.agent.lib.WebAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.webagent.setting.WebAgentSettings;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.webagent.setting.WebAgentSettingsFactory;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.InvalidSigningKeyException;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.MandatorySettingMissingException;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.SigningKeyNotFoundException;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519KeySet;

@RequiredArgsConstructor
public class WebAgentFactory {

  private final ServiceExtensionContext context;
  private final Vault vault;

  public WebAgent createAgent()
      throws MandatorySettingMissingException, InvalidSigningKeyException,
          SigningKeyNotFoundException {

    final WebAgentSettingsFactory settingsFactory = new WebAgentSettingsFactory(context);
    final WebAgentSettings settings = settingsFactory.createSettings();

    final String privateKey = vault.resolveSecret(settings.getPrivateKeyAlias());
    final String publicKey = vault.resolveSecret(settings.getPublicKeyAlias());

    if (privateKey == null) {
      throw new SigningKeyNotFoundException(settings.getPrivateKeyAlias());
    }
    if (publicKey == null) {
      throw new SigningKeyNotFoundException(settings.getPublicKeyAlias());
    }

    try {
      final PemReader privateKeyReader = new PemReader(new StringReader(privateKey));
      final PemReader publicKeyReader = new PemReader(new StringReader(publicKey));
      final byte[] privateKeyBytes = privateKeyReader.readPemObject().getContent();
      final byte[] publicKeyBytes = publicKeyReader.readPemObject().getContent();
      final Ed25519KeySet keySet = new Ed25519KeySet(publicKeyBytes, privateKeyBytes);
      return new WebAgent(settings.getHostName(), keySet, settings.isEnforceHttpsToResolveDidWeb());
    } catch (IOException e) {
      throw new InvalidSigningKeyException(e);
    }
  }
}

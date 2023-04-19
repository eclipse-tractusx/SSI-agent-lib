package org.eclipse.tractusx.ssi.agent.lib.wallet;

import java.util.function.Supplier;

public interface VerifiableCredentialWalletRegistry {

  VerifiableCredentialWallet get(String walletIdentifier);

  void register(String identifier, Supplier<VerifiableCredentialWallet> wallet);
}

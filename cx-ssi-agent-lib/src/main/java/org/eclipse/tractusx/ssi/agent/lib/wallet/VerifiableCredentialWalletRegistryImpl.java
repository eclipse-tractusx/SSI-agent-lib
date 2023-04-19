package org.eclipse.tractusx.ssi.agent.lib.wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.SsiWalletAlreadyExistsException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.SsiWalletNotFoundException;

/**
 * Initialize Wallet instances only if necessary, as some of them might have dependencies to
 * specific settings or external components, which would make them fail to initialize.
 */
public class VerifiableCredentialWalletRegistryImpl implements VerifiableCredentialWalletRegistry {

  private final Map<String, Supplier<VerifiableCredentialWallet>> walletSuppliers = new HashMap<>();
  private final Map<String, VerifiableCredentialWallet> wallets = new HashMap<>();

  @Override
  public VerifiableCredentialWallet get(String walletIdentifier) {

    if (wallets.containsKey(walletIdentifier)) {
      return wallets.get(walletIdentifier);
    }

    synchronized (this) {
      if (!wallets.containsKey(walletIdentifier)
          && !walletSuppliers.containsKey(walletIdentifier)) {
        throw new SsiWalletNotFoundException(walletIdentifier, new ArrayList<>(wallets.keySet()));
      }

      if (wallets.containsKey(walletIdentifier)) {
        return wallets.get(walletIdentifier);
      }

      wallets.put(walletIdentifier, walletSuppliers.get(walletIdentifier).get());
      return wallets.get(walletIdentifier);
    }
  }

  @Override
  public void register(String identifier, Supplier<VerifiableCredentialWallet> wallet) {
    if (walletSuppliers.containsKey(identifier)) {
      throw new SsiWalletAlreadyExistsException(identifier);
    }

    walletSuppliers.put(identifier, wallet);
  }
}

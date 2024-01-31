package org.eclipse.tractusx.ssi.lib.model;

public enum ProofPurpose {
  ASSERTION_METHOD("assertionMethod"),
  AUTHENTICATION("authentication"),
  CAPABILITY_INVOCATION("capabilityInvocation"),
  CAPABILITY_DELEGATION("capabilityDelegation");

  public final String purpose;

  private ProofPurpose(String purpose) {
    this.purpose = purpose;
  }
}

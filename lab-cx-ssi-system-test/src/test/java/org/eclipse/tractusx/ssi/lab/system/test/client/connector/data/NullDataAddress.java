package org.eclipse.tractusx.ssi.lab.system.test.client.connector.data;

public class NullDataAddress implements DataAddress {

  private static final NullDataAddress _instance = new NullDataAddress();

  public static final DataAddress INSTANCE = new NullDataAddress();

  private NullDataAddress() {}
}

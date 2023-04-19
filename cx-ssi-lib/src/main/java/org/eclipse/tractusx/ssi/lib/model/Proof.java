package org.eclipse.tractusx.ssi.lib.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Proof extends HashMap<String, Object> {

  public static final String TYPE = "type";

  public Proof(Map<String, Object> json) {
    super(json);

    try {
      // verify getters
      Objects.requireNonNull(this.getType());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid Proof", e);
    }
  }

  public String getType() {
    return (String) this.get(TYPE);
  }
}

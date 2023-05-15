package org.eclipse.tractusx.ssi.lib.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.tractusx.ssi.lib.util.SerializeUtil;

public class JsonLdObject extends HashMap<String, Object> {

  public static final String CONTEXT = "@context";

  public JsonLdObject(Map<String, Object> json) {
    super(json);

    try {
      // validate getters
      Objects.requireNonNull(this.getContext());
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid JsonLdObject: %s", SerializeUtil.toJson(json)), e);
    }
  }

  public List<String> getContext() {
    final Object context = this.get(CONTEXT);
    if (context instanceof String) {
      return List.of((String) context);
    }
    if (context instanceof List) {
      return (List<String>) context;
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Context must be of type string or list. Context Type: %s",
              context.getClass().getName()));
    }
  }
}

package org.eclipse.tractusx.ssi.lib.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;

public final class SerializeUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @SneakyThrows
  public static String toJson(Map<String, Object> map) {
    return OBJECT_MAPPER.writeValueAsString(map);
  }

  /**
   * Sometimes SSI uri is serialized as string, sometimes as URI. If it starts with 'http://' it is
   * handled as URI, if it starts with 'did:<method>' it is handled as string.
   *
   * @param object string or URI
   * @return URI
   */
  public static URI asURI(Object object) {
    if (object instanceof URI) {
      return (URI) object;
    }
    if (object instanceof String) {
      return URI.create((String) object);
    }
    throw new IllegalArgumentException("Unsupported type: " + object.getClass());
  }

  public static List<String> asStringList(Object object) {
    if (object instanceof List) {
      return (List<String>) object;
    }
    if (object instanceof String) {
      return List.of((String) object);
    }
    throw new IllegalArgumentException("Unsupported type: " + object.getClass());
  }
}

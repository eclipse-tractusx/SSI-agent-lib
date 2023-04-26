package org.eclipse.tractusx.ssi.lab.system.test.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.InputStream;
import java.net.URL;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lab.system.test.client.Connector;
import org.eclipse.tractusx.ssi.lab.system.test.client.SsiAgent;

public class TestConfiguration {

  private static final String ALICE_YAML = "/alice.yaml";
  private static final String BOB_YAML = "/bob.yaml";
  private static final String OPERATOR_YAML = "/operator.yaml";

  public Connector readAliceConfig() {
    return readConnectorConfig(ALICE_YAML);
  }

  public Connector readBobConfig() {
    return readConnectorConfig(BOB_YAML);
  }

  @SneakyThrows
  public SsiAgent readOperatorConfig() {
    final InputStream is = this.getClass().getResourceAsStream(OPERATOR_YAML);
    final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    return objectMapper.readValue(is, SsiAgent.class);
  }

  @SneakyThrows
  private Connector readConnectorConfig(String yamlFile) {

    final URL yaml = this.getClass().getResource(yamlFile);
    final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    return objectMapper.readValue(yaml, Connector.class);
  }
}

package org.eclipse.tractusx.ssi.agent.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "org.eclipse.tractusx.ssi.agent.api",
      "org.eclipse.tractusx.ssi.agent.app.delegates",
      "org.eclipse.tractusx.ssi.agent.app.services"
    })
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}

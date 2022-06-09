package org.dcsa.uisupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
This test application class is needed to test a separate "layer" like the controller layer.
Since the main Application performs a component scan more beans are loaded then what is required in the "layered" tests.
In order to avoid this a test Application class with only the default component scan via @SpringBootApplication is provided.
 */
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}

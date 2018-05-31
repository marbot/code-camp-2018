package ch.sic.codecamp.tkmbn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeavyLoadApplication {

  public static void main(String[] args) {
    SpringApplication.run(HeavyLoadApplicationConfiguration.class, args);
  }


}

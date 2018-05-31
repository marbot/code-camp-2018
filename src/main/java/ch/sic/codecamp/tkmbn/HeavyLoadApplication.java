package ch.sic.codecamp.tkmbn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ch.sic.codecamp.tkmbn.service.HeavyLoadRunner;

@SpringBootApplication
public class HeavyLoadApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext applicationContext = SpringApplication.run(HeavyLoadApplicationConfiguration.class, args);

    HeavyLoadRunner heavyLoadRunner = applicationContext.getBean(HeavyLoadRunner.class);
    heavyLoadRunner.run();
  }


}

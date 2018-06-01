package ch.sic.codecamp.tkmbn.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ch.sic.codecamp.tkmbn.HeavyLoadApplicationConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HeavyLoadApplicationConfiguration.class)
public class HeavyLoadRunnerTest {

  @Autowired
  private HeavyLoadRunner heavyLoadRunner;

  @Test
  public void run() {

    // act
    this.heavyLoadRunner.run();

  }
}

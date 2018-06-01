package ch.sic.codecamp.tkmbn.producer;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.time.StopWatch;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ch.sic.codecamp.tkmbn.HeavyLoadApplicationConfiguration;
import ch.sic.codecamp.tkmbn.domain.Payment;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HeavyLoadApplicationConfiguration.class)
public class PaymentProducerTest {

  public static final long TEN_SECONDS_IN_MILLIS = 10_000L;
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  private PaymentProducer paymentProducer;

  @Test
  public void produceSimplePayment() {

    // act
    Payment payment = this.paymentProducer.produce();

    // assert
    assertThat(payment, is(notNullValue()));
  }

  @Test
  public void producePaymentsAreDifferent() {

    // act
    Payment payment1 = this.paymentProducer.produce();
    Payment payment2 = this.paymentProducer.produce();

    // assert
    assertThat(payment1.getId(), is(not(payment2.getId())));
    assertThat(payment1.getUuid(), is(not(payment2.getUuid())));
    assertThat(payment1.getIid(), is(not(payment2.getIid())));
    assertThat(payment1.getBic(), is(not(payment2.getBic())));
    assertThat(payment1.getMessage(), is(not(payment2.getMessage())));
    assertThat(payment1.getEncryptedMessage(), is(not(payment2.getEncryptedMessage())));
  }

  @Test
  public void produceAndMeasurePerformance() {
    // arrange
    StopWatch stopWatch = new StopWatch();
    List<Payment> paymentList = new ArrayList<>();
    int numberOfPayments = 100000;

    // act
    stopWatch.start();
    for (int i = 0; i < numberOfPayments; i++) {
      paymentList.add(this.paymentProducer.produce());
    }
    stopWatch.stop();

    // assert
    assertThat(paymentList, Matchers.hasSize(numberOfPayments));
    logger.info("creating 100000 payments took {}", stopWatch.toString());
    assertThat(stopWatch.getTime(), is(Matchers.lessThan(TEN_SECONDS_IN_MILLIS)));
  }
}

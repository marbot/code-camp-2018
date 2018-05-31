package ch.sic.codecamp.tkmbn.producer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.concurrent.ThreadSafe;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ch.sic.codecamp.tkmbn.domain.Payment;
import ch.sic.codecamp.tkmbn.domain.PaymentState;

@Service
@ThreadSafe
public class PaymentProducer {

  private final AtomicLong id = new AtomicLong();

  public Payment produce() {
    String message = RandomStringUtils.randomAlphanumeric(1000, 2500);

    return new Payment(
        this.id.incrementAndGet(),
        UUID.randomUUID().toString(),
        RandomStringUtils.randomNumeric(5),
        RandomStringUtils.randomAlphanumeric(11),
        new BigDecimal(RandomStringUtils.randomNumeric(2, 10)),
        ZonedDateTime.now(),
        PaymentState.VALIDATED,
        message,
        Base64.getEncoder().encodeToString(message.getBytes())
    );
  }

}

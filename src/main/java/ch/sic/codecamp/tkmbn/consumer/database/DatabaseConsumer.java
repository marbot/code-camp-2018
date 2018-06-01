package ch.sic.codecamp.tkmbn.consumer.database;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.sic.codecamp.tkmbn.Consumer;
import ch.sic.codecamp.tkmbn.domain.Payment;
import ch.sic.codecamp.tkmbn.writer.database.PaymentDao;

@Service
public class DatabaseConsumer implements Consumer {

  @Autowired
  private PaymentDao paymentDao;

  @Override
  public void consume(Collection<Payment> payments) {
    this.paymentDao.insert(payments);
  }
}

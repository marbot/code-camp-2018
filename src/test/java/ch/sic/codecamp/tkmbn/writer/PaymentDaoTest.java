package ch.sic.codecamp.tkmbn.writer;

import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ch.sic.codecamp.tkmbn.HeavyLoadApplicationConfiguration;
import ch.sic.codecamp.tkmbn.domain.Payment;
import ch.sic.codecamp.tkmbn.producer.PaymentProducer;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HeavyLoadApplicationConfiguration.class)
public class PaymentDaoTest {

  @Autowired
  private PaymentProducer paymentProducer;

  @Autowired
  private PaymentDao paymentDao;

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Test
  public void insertPayment() {
    // arrange
    Payment payment = this.paymentProducer.produce();

    // act
    this.paymentDao.insert(Collections.singleton(payment));

    // arrange
    Long count = this.namedParameterJdbcTemplate.queryForObject("select count(*) from t_payment", new MapSqlParameterSource(), Long.class);
    assertThat(count, is(1L));
  }
}

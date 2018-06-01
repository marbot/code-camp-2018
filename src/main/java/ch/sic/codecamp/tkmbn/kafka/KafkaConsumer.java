package ch.sic.codecamp.tkmbn.kafka;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ch.sic.codecamp.tkmbn.Consumer;
import ch.sic.codecamp.tkmbn.domain.Payment;

@Service
public class KafkaConsumer implements Consumer {

  @Autowired
  private KafkaTemplate<Long, String> kafkaTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void consume(Collection<Payment> payments) {
    for (Payment payment : payments) {
      String serializedPayment;
      try {
        serializedPayment = this.objectMapper.writeValueAsString(payment);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      this.kafkaTemplate.sendDefault(payment.getId(), serializedPayment);
    }
    this.kafkaTemplate.flush();
  }
}

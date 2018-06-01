package ch.sic.codecamp.tkmbn.service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ch.sic.codecamp.tkmbn.domain.Payment;
import ch.sic.codecamp.tkmbn.producer.PaymentProducer;

@Service
public class HeavyLoadRunner {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  private ExecutorService producerExecutorService;

  @Autowired
  private PaymentProducer paymentProducer;

  @Value("${payment.producer.thread.pool.size}")
  private int paymeentProducerThreadPoolSize;

  @Value("${payment.count.total}")
  private int paymmentCountTotal;

  @Value("${payment.deque.buffer.size}")
  private int paymmentDequeBufferSize;

  private final AtomicLong generatedPaymentCount = new AtomicLong();

  private final Deque<Payment> paymentDeque = new ConcurrentLinkedDeque<>();

  public void run() {

    Collection<Runnable> producerTasks = createProducerTasks();

    logger.info("starting producers...");
    for (Runnable producerTask : producerTasks) {
      this.producerExecutorService.execute(producerTask);
    }

    waitForPaymentBufferToFill();


  }

  private void waitForPaymentBufferToFill() {
    logger.info("waiting for a payment buffer of {}...", this.paymmentDequeBufferSize);
    int paymentDequeSize = this.paymentDeque.size();
    while (paymentDequeSize < this.paymmentDequeBufferSize) {
      try {
        logger.debug("buffering {} / {}...", paymentDequeSize, this.paymmentDequeBufferSize);
        Thread.sleep(300L);
        paymentDequeSize = this.paymentDeque.size();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
  }

  private Collection<Runnable> createProducerTasks() {
    Collection<Runnable> producerTasks = new ArrayList<>();
    for (int i = 0; i < this.paymeentProducerThreadPoolSize; i++) {
      producerTasks.add(this::addGeneratedPaymentsToDeque);
    }
    return producerTasks;
  }

  private void addGeneratedPaymentsToDeque() {
    do {
      this.paymentDeque.addLast(this.paymentProducer.produce());
    }
    while (this.generatedPaymentCount.incrementAndGet() < this.paymmentCountTotal);
  }
}


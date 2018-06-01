package ch.sic.codecamp.tkmbn.service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import ch.sic.codecamp.tkmbn.Consumer;
import ch.sic.codecamp.tkmbn.domain.Payment;
import ch.sic.codecamp.tkmbn.producer.PaymentProducer;

@Service
public class HeavyLoadRunner {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  private ExecutorService producerExecutorService;

  @Autowired
  private ExecutorService consumerExecutorService;

  @Autowired
  private PaymentProducer paymentProducer;

  @Autowired
  private ConfigurableApplicationContext applicationContext;

  @Value("${payment.producer.thread.pool.size}")
  private int paymeentProducerThreadPoolSize;

  @Value("${payment.generated.count.total}")
  private int paymmentGeneratedCountTotal;

  @Value("${payment.deque.buffer.size}")
  private int paymmentDequeBufferSize;

  @Value("${payment.consumer.thread.pool.size}")
  private int paymeentConsumerThreadPoolSize;

  @Value("${payment.consumer.class}")
  private String paymentConsumerClass;

  @Value("${payment.consumer.batch.size}")
  private int paymentConsumerBstchSize;

  private final AtomicLong generatedPaymentCount = new AtomicLong();

  private final Deque<Payment> paymentDeque = new ConcurrentLinkedDeque<>();

  public void run() {

    Collection<Runnable> producerTasks = createProducerTasks();

    logger.info("starting producers...");
    StopWatch totalStopWatch = new StopWatch();
    totalStopWatch.start();
    for (Runnable producerTask : producerTasks) {
      this.producerExecutorService.execute(producerTask);
    }

    waitForPaymentBufferToFill();
    logger.info("buffered {} payments, took {}", this.paymmentDequeBufferSize, totalStopWatch);

    Consumer consumer = (Consumer) this.applicationContext.getBean(this.paymentConsumerClass);

    Collection<Runnable> consumerTasks = createConsumerTasks(consumer);

    logger.info("starting consumers...");
    StopWatch consumerStopWatch = new StopWatch();
    consumerStopWatch.start();
    for (Runnable consumerTask : consumerTasks) {
      this.consumerExecutorService.execute(consumerTask);
    }

    waitForPaymentDequeToEmpty();

    // make sure the producers have been able to keep up and the buffer was not emptied too early
    if (this.generatedPaymentCount.get() != this.paymmentGeneratedCountTotal) {
      throw new IllegalStateException("The producers have not been able to keep up! They've stopped early after " + this.generatedPaymentCount.get() + " payments.");
    }

    consumerStopWatch.stop();
    totalStopWatch.stop();
    logger.info("Done writing, took {} to write {} payments to {}. Speed: {} payments/sec", consumerStopWatch, this.paymmentGeneratedCountTotal, this.paymentConsumerClass, (float) this.paymmentGeneratedCountTotal / consumerStopWatch.getTime() * 1000);
    logger.info("Done, took {}", totalStopWatch);

  }

  private void waitForPaymentBufferToFill() {
    logger.info("waiting for a payment buffer of {}...", this.paymmentDequeBufferSize);
    int paymentDequeSize = this.paymentDeque.size();
    while (paymentDequeSize < this.paymmentDequeBufferSize) {
      try {
        logger.info("buffering {} / {}...", paymentDequeSize, this.paymmentDequeBufferSize);
        Thread.sleep(300L);
        paymentDequeSize = this.paymentDeque.size();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
  }

  private void waitForPaymentDequeToEmpty() {
    logger.info("waiting for a payment dequeuq to empty...");
    while (!this.paymentDeque.isEmpty()) {
      try {
        logger.info("still waiting for deque to empty, {} left...", this.paymentDeque.size());
        Thread.sleep(300L);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    }
  }

  private Collection<Runnable> createProducerTasks() {
    Collection<Runnable> tasks = new ArrayList<>();
    for (int i = 0; i < this.paymeentProducerThreadPoolSize; i++) {
      tasks.add(this::addGeneratedPaymentsToDeque);
    }
    return tasks;
  }

  private Collection<Runnable> createConsumerTasks(Consumer consumer) {
    Collection<Runnable> tasks = new ArrayList<>();
    for (int i = 0; i < this.paymeentConsumerThreadPoolSize; i++) {
      tasks.add(() -> consumeGeneratedPaymentsFromDeque(consumer));
    }
    return tasks;
  }

  private void addGeneratedPaymentsToDeque() {
    do {
      this.paymentDeque.addLast(this.paymentProducer.produce());
    }
    while (this.generatedPaymentCount.incrementAndGet() < this.paymmentGeneratedCountTotal);
  }

  private void consumeGeneratedPaymentsFromDeque(Consumer consumer) {
    while (!this.paymentDeque.isEmpty()) {
      Collection<Payment> paymentsInBatch = new ArrayList<>();
      for (int i = 0; i < this.paymentConsumerBstchSize; i++) {
        Payment payment = this.paymentDeque.pollFirst();
        if (payment != null) {
          paymentsInBatch.add(payment);
        }
      }
      consumer.consume(paymentsInBatch);
    }

  }
}


package ch.sic.codecamp.tkmbn;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootConfiguration
@ComponentScan
@PropertySource("classpath:application.properties")
public class HeavyLoadApplicationConfiguration {


  @Bean
  public ExecutorService producerExecutorService(
      @Value("${payment.producer.thread.pool.size}") int paymeentProducerThreadPoolSize
  ) {
    BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
        .daemon(true)
        .namingPattern("producer-%d")
        .build();

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        paymeentProducerThreadPoolSize,
        paymeentProducerThreadPoolSize,
        1L, TimeUnit.MINUTES,
        new LinkedBlockingQueue<>(),
        threadFactory);
    threadPoolExecutor.allowCoreThreadTimeOut(true);

    return threadPoolExecutor;
  }

  @Bean
  public ExecutorService consumerExecutorService(
      @Value("${payment.consumer.thread.pool.size}") int paymeentConsumerThreadPoolSize
  ) {
    BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
        .daemon(true)
        .namingPattern("consumer-%d")
        .build();

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        paymeentConsumerThreadPoolSize,
        paymeentConsumerThreadPoolSize,
        1L, TimeUnit.MINUTES,
        new LinkedBlockingQueue<>(),
        threadFactory);
    threadPoolExecutor.allowCoreThreadTimeOut(true);

    return threadPoolExecutor;
  }

  @Bean
  public KafkaTemplate<Long, String> kafkaTemplate(
      @Value("${kafka.server.configs}") String kafkaServerConfigs,
      @Value("${kafka.default.topic}") String kafkaDefaultTopic,
      @Value("${kafka.batch.size}") int kafkaBatchSize,
      @Value("${kafka.linger.ms}") int kafkaLingerSize,
      @Value("${kafka.buffer.size}") int kafkaBufferSize
  ) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerConfigs);
    props.put(ProducerConfig.RETRIES_CONFIG, 0);
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaBatchSize);
    props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaLingerSize);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaBufferSize);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    KafkaTemplate<Long, String> kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    kafkaTemplate.setDefaultTopic(kafkaDefaultTopic);
    return kafkaTemplate;
  }

}

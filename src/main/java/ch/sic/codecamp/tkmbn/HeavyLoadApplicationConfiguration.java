package ch.sic.codecamp.tkmbn;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

@SpringBootConfiguration
@ComponentScan
@PropertySource("classpath:application.properties")
public class HeavyLoadApplicationConfiguration {

  @Bean
  public DataSource dataSource(
      @Value("${datasource.url}") String dataSourceUrl,
      @Value("${datasource.username}") String dataSourceUsername,
      @Value("${datasource.password}") String dataSourcePassword,
      @Value("${datasource.pool.min.size}") int dataSourcePoolMinSize,
      @Value("${datasource.pool.max.size}") int dataSourcePoolMaxSize,
      @Value("${datasource.pool.initial.size}") int dataSourcePoolInitialSize,
      @Value("${datasource.timeout.inactive-connection.seconds}") int dataSourceTimeoutInactiveConnectionInSeconds,
      @Value("${datasource.timeout.check-interval.seconds}") int dataSourceTimeoutCheckIntervalInSeconds) {
    try {
      Properties connectionProperties = new Properties();
      connectionProperties.setProperty("defaultAutoCommit", "false");
      connectionProperties.setProperty("autoCommit", "false");

      PoolDataSource poolDataSource = PoolDataSourceFactory.getPoolDataSource();
      poolDataSource.setURL(dataSourceUrl);
      poolDataSource.setUser(dataSourceUsername);
      poolDataSource.setPassword(dataSourcePassword);
      poolDataSource.setInitialPoolSize(dataSourcePoolInitialSize);
      poolDataSource.setMinPoolSize(dataSourcePoolMinSize);
      poolDataSource.setMaxPoolSize(dataSourcePoolMaxSize);
      poolDataSource.setInactiveConnectionTimeout(dataSourceTimeoutInactiveConnectionInSeconds);
      poolDataSource.setTimeoutCheckInterval(dataSourceTimeoutCheckIntervalInSeconds);
      poolDataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
      poolDataSource.setConnectionProperties(connectionProperties);

      return poolDataSource;
    } catch (SQLException e) {
      throw new RuntimeException("unable to create oracle pool datasource", e);
    }
  }

  /**
   * Override the flyway bean in order to always clean the database on startup.
   */
  @Bean
  public Flyway flyway(DataSource theDataSource) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(theDataSource);
    flyway.setLocations("classpath:db/migration");
    flyway.clean();
    flyway.migrate();

    return flyway;
  }

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

}

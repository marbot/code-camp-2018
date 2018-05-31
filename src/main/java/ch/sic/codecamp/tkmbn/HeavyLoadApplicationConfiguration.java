package ch.sic.codecamp.tkmbn;

import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

@SpringBootConfiguration
@ComponentScan
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
}

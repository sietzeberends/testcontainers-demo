package nl.sietzeberends.demo.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration public class CassandraConfig {

  private final String configLocation;

  public @Bean CqlSession session() {
    return CqlSession.builder().withConfigLoader(DriverConfigLoader.fromPath(Paths.get(String.format("%scassandra.conf", configLocation)))).withKeyspace("ut").build();
  }

  public CassandraConfig(@Value("${testcontainers.cassandra.config.location}") String configLocation) {
    this.configLocation = configLocation;

  }
}

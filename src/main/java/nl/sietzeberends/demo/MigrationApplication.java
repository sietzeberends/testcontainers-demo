package nl.sietzeberends.demo;

import nl.sietzeberends.demo.config.CassandraConfig;
import nl.sietzeberends.demo.repository.CassandraRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = {MigrationApplication.class, CassandraConfig.class, CassandraRepository.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MigrationApplication {
  public static void main(String[] args) {
    SpringApplication.run(MigrationApplication.class, args);
  }
}

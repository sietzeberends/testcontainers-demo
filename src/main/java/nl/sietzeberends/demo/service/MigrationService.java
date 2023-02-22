package nl.sietzeberends.demo.service;

import nl.sietzeberends.demo.model.EmployeeRecordCassandra;
import nl.sietzeberends.demo.model.EmployeeRecordElastic;
import nl.sietzeberends.demo.repository.CassandraRepository;
import nl.sietzeberends.demo.repository.ElasticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MigrationService {
  private final CassandraRepository cassandraRepository;
  private final ElasticRepository elasticRepository;
  private final Logger log = LoggerFactory.getLogger(MigrationService.class);

  public MigrationService(CassandraRepository cassandraRepository, ElasticRepository elasticRepository) {
    this.cassandraRepository = cassandraRepository;
    this.elasticRepository = elasticRepository;
  }

  public boolean migrateElasticToCassandra() {
    elasticRepository.getAllEmployees()
        .stream()
        .map(this::mapElasticToCassandraDto)
        .forEach(this::insertIntoCassandra);
    return true;
  }

  private EmployeeRecordCassandra mapElasticToCassandraDto(EmployeeRecordElastic employeeRecordElastic) {
    log.info("Mapping elastic record to Cassandra: {}", employeeRecordElastic.toString());
    return new EmployeeRecordCassandra(UUID.randomUUID().toString(),
        employeeRecordElastic.getFirstName(),
        employeeRecordElastic.getLastName(),
        employeeRecordElastic.alwaysBooksHoursOnTime(),
        employeeRecordElastic.getCerts()
    );
  }

  private void insertIntoCassandra(EmployeeRecordCassandra employeeRecordCassandra) {
    log.info("Inserting record into Cassandra: {}", employeeRecordCassandra.toString());
    cassandraRepository.insertEmployee(employeeRecordCassandra);
  }
}

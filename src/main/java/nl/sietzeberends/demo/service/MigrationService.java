package nl.sietzeberends.demo.service;

import nl.sietzeberends.demo.model.EmployeeRecordElastic;
import nl.sietzeberends.demo.model.EmployeeRecordCassandra;
import nl.sietzeberends.demo.repository.CassandraRepository;
import nl.sietzeberends.demo.repository.ElasticRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MigrationService {
  private final CassandraRepository cassandraRepository;
  private final ElasticRepository elasticRepository;

  public MigrationService(CassandraRepository cassandraRepository, ElasticRepository elasticRepository) {
    this.cassandraRepository = cassandraRepository;
    this.elasticRepository = elasticRepository;
  }

  public boolean migrateElasticToCassandra() {
    elasticRepository.getAllEmployees().stream().map(this::mapElasticToCassandraDto).forEach(this::insertIntoCassandra);
    return true;
  }

  private EmployeeRecordCassandra mapElasticToCassandraDto(EmployeeRecordElastic employeeRecordElastic) {
    return new EmployeeRecordCassandra(UUID.randomUUID().toString(),
        employeeRecordElastic.getFirstName(),
        employeeRecordElastic.getLastName(),
        employeeRecordElastic.alwaysBooksHoursOnTime(),
        employeeRecordElastic.getCerts()
    );
  }

  private void insertIntoCassandra(EmployeeRecordCassandra employeeRecordCassandra) {
    cassandraRepository.insertEmployee(employeeRecordCassandra);
  }
}

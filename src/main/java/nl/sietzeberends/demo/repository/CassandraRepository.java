package nl.sietzeberends.demo.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import nl.sietzeberends.demo.model.EmployeeRecordCassandra;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

@Component
public class CassandraRepository {

  private final CassandraTemplate cassandraTemplate;

  public CassandraRepository(CqlSession cqlSession) {
    cassandraTemplate = new CassandraTemplate(cqlSession);
  }

  public boolean insertEmployee(EmployeeRecordCassandra employeeRecordCassandra) {
    cassandraTemplate.insert(employeeRecordCassandra);
    return true;
  }
}

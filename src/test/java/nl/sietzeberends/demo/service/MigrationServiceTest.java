package nl.sietzeberends.demo.service;

import nl.sietzeberends.demo.BaseTest;
import nl.sietzeberends.demo.model.Cert;
import nl.sietzeberends.demo.model.EmployeeRecordCassandra;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.core.CassandraTemplate;

import java.util.Comparator;
import java.util.List;

@SpringBootTest public class MigrationServiceTest extends BaseTest {

  @Autowired MigrationService migrationService;
  @Autowired CassandraTemplate cassandraTemplate;

  @Test void testMigrationCorrectly() {
    migrationService.migrateElasticToCassandra();
    List<EmployeeRecordCassandra> employees = cassandraTemplate.select("SELECT * FROM employee", EmployeeRecordCassandra.class)
        .stream()
        .sorted(Comparator.comparing(EmployeeRecordCassandra::getLastName))
        .toList();
    Assertions.assertEquals("Sietze", employees.get(0).getFirstName());
    Assertions.assertEquals("Berends", employees.get(0).getLastName());
    Assertions.assertTrue(employees.get(0).getCerts().contains(Cert.OCA));
    Assertions.assertTrue(employees.get(0).getCerts().contains(Cert.OCP));
    Assertions.assertTrue(employees.get(0).getCerts().contains(Cert.CKAD));
    Assertions.assertFalse(employees.get(0).alwaysBooksHoursOnTime());
    Assertions.assertEquals("Tim", employees.get(1).getFirstName());
    Assertions.assertTrue(employees.get(1).alwaysBooksHoursOnTime());
    Assertions.assertEquals("Johnny", employees.get(2).getFirstName());
    Assertions.assertTrue(employees.get(2).alwaysBooksHoursOnTime());
  }
}

package nl.sietzeberends.demo.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table(value = "employee")
public class EmployeeRecordCassandra {
  @PrimaryKey private final String id;
  private final String firstName;
  private final String lastName;
  private final boolean alwaysBooksHoursOnTime;
  private final List<Cert> certs;

  public EmployeeRecordCassandra(String id, String firstName, String lastName, boolean alwaysBooksHoursOnTime, List<Cert> certs) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.alwaysBooksHoursOnTime = alwaysBooksHoursOnTime;
    this.certs = certs;
  }

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public boolean alwaysBooksHoursOnTime() {
    return alwaysBooksHoursOnTime;
  }

  public List<Cert> getCerts() {
    return certs;
  }
}

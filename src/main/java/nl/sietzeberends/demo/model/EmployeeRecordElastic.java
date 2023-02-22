package nl.sietzeberends.demo.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Document(indexName = "employees")
public class EmployeeRecordElastic {
  @Field(name = "first-name") private String firstName;
  @Field(name = "last-name") private String lastName;
  @Field(name = "always-books-hours-on-time") private boolean alwaysBooksHoursOnTime;
  @Field(name = "certs") private List<Cert> certs;

  public EmployeeRecordElastic(String firstName, String lastName, boolean alwaysBooksHoursOnTime, List<Cert> certs) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.alwaysBooksHoursOnTime = alwaysBooksHoursOnTime;
    this.certs = certs;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public boolean isAlwaysBooksHoursOnTime() {
    return alwaysBooksHoursOnTime;
  }

  public void setAlwaysBooksHoursOnTime(boolean alwaysBooksHoursOnTime) {
    this.alwaysBooksHoursOnTime = alwaysBooksHoursOnTime;
  }

  public void setCerts(List<Cert> certs) {
    this.certs = certs;
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

  @Override public String toString() {
    return "Employee in elastic: {" + "firstName='" + firstName + '\'' + ", lastName='" + lastName
        + '\'' + ", alwaysBooksHoursOnTime=" + alwaysBooksHoursOnTime + ", certs=" + certs + '}';
  }
}

package nl.sietzeberends.demo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import nl.sietzeberends.demo.model.Cert;
import nl.sietzeberends.demo.model.EmployeeRecordElastic;
import nl.sietzeberends.demo.repository.CassandraRepository;
import nl.sietzeberends.demo.repository.ElasticRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InternetProtocol;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(classes = {MigrationApplication.class, CassandraTemplate.class}) public class BaseTest {

  private static final FixedHostPortGenericContainer cassandraContainer =
      new FixedHostPortGenericContainer("cassandra:4.0.6").withFixedExposedPort(19042, 9042, InternetProtocol.TCP);
  private static CqlSession cassandraSession = null;
  private static final ElasticsearchContainer elasticContainer =
      new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.2")).withPassword("elastic")
          .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx1024m");
  private static final AtomicBoolean elasticInitialized = new AtomicBoolean();
  private static final AtomicBoolean cassandraInitialized = new AtomicBoolean();


  @BeforeAll public static void initialize() {
    if (elasticInitialized.compareAndSet(false, true)) {
      elasticContainer.start();
    }
    if (cassandraInitialized.compareAndSet(false, true)) {
      cassandraContainer.start();
    }
    await().atMost(5, TimeUnit.MINUTES).pollInterval(1, TimeUnit.SECONDS).until(() -> {
      try {
        cassandraSession = CqlSession.builder().addContactPoint(getCassandraEndpoint()).withLocalDatacenter("datacenter1").build();
        cassandraSession.execute(SimpleStatement.newInstance(
                "CREATE KEYSPACE IF NOT EXISTS ut WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'} AND durable_writes = true;")
            .setTimeout(Duration.ofMillis(2000L)));
        cassandraSession.execute(SimpleStatement.newInstance("USE ut;"));
        cassandraSession.execute(SimpleStatement.newInstance(
            "CREATE TABLE employee(id text, firstName text, lastName text, alwaysBooksHoursOnTime boolean, certs list<text>, PRIMARY KEY (id));"));
        return true;
      } catch (Exception e) {
        return false;
      }
    });
  }

  @NotNull private static InetSocketAddress getCassandraEndpoint() {
    return InetSocketAddress.createUnresolved("localhost", 19042);
  }

  @TestConfiguration static class BaseTestConfiguration {

    private void initializeElastic(ElasticsearchOperations elasticsearchOperations) {
      EmployeeRecordElastic sietze = new EmployeeRecordElastic("Sietze", "Berends", false, List.of(Cert.CKAD, Cert.OCA, Cert.OCP));
      EmployeeRecordElastic jade = new EmployeeRecordElastic("Jade", "Eloff", true, List.of(Cert.values()));
      EmployeeRecordElastic wietse = new EmployeeRecordElastic("Wietse", "Smit",true, List.of(Cert.OCA, Cert.OCP, Cert.SPRING));
      IndexQueryBuilder builder = new IndexQueryBuilder();
      List<IndexQuery> employeeIndexQueries = new ArrayList<>();
      employeeIndexQueries.add(builder.withIndex("employees").withId(UUID.randomUUID().toString()).withObject(sietze).build());
      employeeIndexQueries.add(builder.withIndex("employees").withId(UUID.randomUUID().toString()).withObject(jade).build());
      employeeIndexQueries.add(builder.withIndex("employees").withId(UUID.randomUUID().toString()).withObject(wietse).build());
      elasticsearchOperations.bulkIndex(employeeIndexQueries, IndexCoordinates.of("employees"));
    }


    @EventListener public void onApplicationEvent(ApplicationReadyEvent event) {
      ElasticsearchOperations elasticsearchOperations = event.getApplicationContext().getBean(ElasticsearchOperations.class);
      initializeElastic(elasticsearchOperations);
    }
  }

  @DynamicPropertySource static void setProperties(DynamicPropertyRegistry registry) {
    System.setProperty("spring.config.location", String.format("%s/", Paths.get("src", "test", "resources").normalize()));
    registry.add("elasticsearch.uris", () -> new String[] {"http://" + elasticContainer.getHttpHostAddress()});
    registry.add("elasticsearch.username", () -> "elastic");
    registry.add("elasticsearch.password", () -> "elastic");
  }
}

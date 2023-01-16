package nl.sietzeberends.demo.repository;

import nl.sietzeberends.demo.model.EmployeeRecordElastic;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticRepository {

  private final ElasticsearchOperations elasticsearchOperations;

  public ElasticRepository(ElasticsearchOperations elasticsearchOperations) {
    this.elasticsearchOperations = elasticsearchOperations;
  }

  public List<EmployeeRecordElastic> getAllEmployees() {
    return elasticsearchOperations
        .search(elasticsearchOperations.matchAllQuery(), EmployeeRecordElastic.class, IndexCoordinates.of("employees"))
        .getSearchHits()
        .stream()
        .map(SearchHit::getContent)
        .collect(Collectors.toList());
  }
}

package nl.sietzeberends.demo.controller;

import nl.sietzeberends.demo.service.MigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MigrationController {

  private final MigrationService migrationService;

  public MigrationController(MigrationService migrationService) {
    this.migrationService = migrationService;
  }

  @GetMapping
  public ResponseEntity<Boolean> getGoing() {
    return ResponseEntity.ok(migrationService.migrateElasticToCassandra());
  }
}

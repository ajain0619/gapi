package com.nexage.app.job;

import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
abstract class AbstractPlacementFormulaUpdateService implements PlacementFormulaUpdateService {

  private final String entityType;

  AbstractPlacementFormulaUpdateService(String entityType) {
    this.entityType = entityType;
  }

  @Override
  public String getEntityType() {
    return entityType;
  }

  protected abstract void compareAndUpdate(Long pid, PlacementFormulaAutoUpdateMetrics metrics);

  @Override
  @Transactional
  public void tryUpdate(Long pid, PlacementFormulaAutoUpdateMetrics metrics) {
    try {
      compareAndUpdate(pid, metrics);
    } catch (Exception e) {
      metrics.incrementErrors();
      log.error("Unable to update {} because {}", pid, e.getMessage(), e);
    }
  }
}

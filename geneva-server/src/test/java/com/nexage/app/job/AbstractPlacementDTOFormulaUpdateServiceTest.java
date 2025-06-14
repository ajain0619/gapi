package com.nexage.app.job;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class AbstractPlacementDTOFormulaUpdateServiceTest {

  @Test
  void whenUpdateFailsWithOtherException_thenItShouldBeCountedAsError() {
    PlacementFormulaAutoUpdateMetrics jobMetricsData =
        new PlacementFormulaAutoUpdateMetrics(100L, 1);
    createAbstractServiceWithExceptionWhileUpdatingEntity(new RuntimeException())
        .tryUpdate(8L, jobMetricsData);

    assertEquals(0, jobMetricsData.getWarnings());
    assertEquals(1, jobMetricsData.getErrors());
  }

  private AbstractPlacementFormulaUpdateService
      createAbstractServiceWithExceptionWhileUpdatingEntity(RuntimeException e) {
    return new AbstractPlacementFormulaUpdateService("Test") {
      @Override
      protected void compareAndUpdate(Long pid, PlacementFormulaAutoUpdateMetrics metrics) {
        throw e;
      }

      @Override
      public List<Long> findAllToUpdate() {
        return null;
      }
    };
  }
}

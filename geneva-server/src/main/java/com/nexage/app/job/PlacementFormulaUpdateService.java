package com.nexage.app.job;

import java.util.List;

public interface PlacementFormulaUpdateService {
  String getEntityType();

  List<Long> findAllToUpdate();

  void tryUpdate(Long pid, PlacementFormulaAutoUpdateMetrics metrics);
}

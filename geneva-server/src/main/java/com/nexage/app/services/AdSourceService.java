package com.nexage.app.services;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.model.AdSource;
import java.util.List;
import java.util.Map;

public interface AdSourceService {

  /**
   * @param tagPidBuyerPidMap map between tagPid and BuyerPid.
   * @return {@link Map} of indexes and {@link AdSource} associated.
   */
  Map<Long, AdSource> getAdSourcesUsedForTierTags(Map<Long, Long> tagPidBuyerPidMap);

  void softDelete(Long adSourcePid);

  List<AdSourceSummaryDTO> getAdSourceSummariesByCompanyPid(Long companyPid);

  List<AdSourceSummaryDTO> getAdSourceSummariesForGeneva();
}

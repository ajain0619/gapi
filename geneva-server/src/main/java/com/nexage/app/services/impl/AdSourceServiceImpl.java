package com.nexage.app.services.impl;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.repository.AdSourceRepository;
import com.nexage.app.mapper.AdSourceSummaryDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.AdSourceService;
import com.nexage.app.util.assemblers.PublisherPositionAssembler;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class was extracted from {@link PublisherPositionAssembler} to avoid circular dependencies,
 * please, do not consider this class as a good example for new code.
 */
@Service
@Transactional
public class AdSourceServiceImpl implements AdSourceService {

  private final UserContext userContext;
  private final AdSourceRepository adSourceRepository;

  @Autowired
  public AdSourceServiceImpl(UserContext userContext, AdSourceRepository adSourceRepository) {
    this.userContext = userContext;
    this.adSourceRepository = adSourceRepository;
  }

  /** {@inheritDoc} */
  public Map<Long, AdSource> getAdSourcesUsedForTierTags(Map<Long, Long> tagPidBuyerPidMap) {
    if (!(userContext.isOcAdminNexage()
        || userContext.isOcManagerNexage()
        || userContext.isOcAdminSeller()
        || userContext.isOcManagerSeller())) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
    // from the buyerPids, get the adsource  for each PublisherTag pid in the PublisherTier
    Map<Long, AdSource> adSourcesMap = new HashMap<>();
    if (tagPidBuyerPidMap != null && !tagPidBuyerPidMap.isEmpty()) {
      List<AdSource> adSources =
          adSourceRepository.findNonDeletedByPidIn(new ArrayList<>(tagPidBuyerPidMap.values()));
      tagPidBuyerPidMap.forEach(
          (tagPid, buyerPid) ->
              adSources.stream()
                  .filter(adSource -> adSource.getPid().equals(buyerPid))
                  .findFirst()
                  .ifPresent(as -> adSourcesMap.put(tagPid, as)));
    }
    return adSourcesMap;
  }

  @Override
  public void softDelete(Long adSourcePid) {
    Optional<AdSource> result = adSourceRepository.findById(adSourcePid);
    if (result.isEmpty()) {
      return;
    }
    AdSource adSource = result.get();
    adSource.setStatus(Status.DELETED);
    adSourceRepository.save(adSource);
  }

  @Override
  public List<AdSourceSummaryDTO> getAdSourceSummariesByCompanyPid(Long companyPid) {
    return adSourceRepository.findNonDeletedByCompanyPid(companyPid).stream()
        .map(AdSourceSummaryDTOMapper.MAPPER::map)
        .collect(Collectors.toList());
  }

  @Override
  public List<AdSourceSummaryDTO> getAdSourceSummariesForGeneva() {
    return adSourceRepository.findAllActiveOrderedByName().stream()
        .map(AdSourceSummaryDTOMapper.MAPPER::mapForGeneva)
        .collect(Collectors.toList());
  }
}

package com.nexage.app.services.impl;

import com.nexage.admin.core.model.PositionMetrics_;
import com.nexage.admin.core.model.Position_;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.PositionMetricsAggregation;
import com.nexage.app.dto.seller.PlacementSummaryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.site.PlacementSummaryDTOMapper;
import com.nexage.app.services.PlacementSummaryDTOService;
import com.nexage.app.util.JpaPolyfills;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserNexage() or "
        + "@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller()")
public class PlacementSummaryDTOServiceImpl implements PlacementSummaryDTOService {
  private final PositionRepository positionRepository;
  private final SiteRepository siteRepository;

  private static final Map<String, String> aliasMap =
      Stream.of(
              PositionMetrics_.AD_CLICKED,
              PositionMetrics_.AD_DELIVERED,
              PositionMetrics_.AD_REQUESTED,
              PositionMetrics_.AD_SERVED,
              PositionMetrics_.SELLER_REVENUE,
              PositionMetrics_.FILL_RATE,
              PositionMetrics_.CTR,
              PositionMetrics_.RPM,
              PositionMetrics_.ECPM,
              PositionMetrics_.TOTAL_ECPM,
              PositionMetrics_.TOTAL_RPM)
          .collect(Collectors.toMap(key -> key, value -> PositionRepository.siteMetricsTableAlias));

  /** {@inheritDoc} */
  @Override
  public Page<PlacementSummaryDTO> getPlacementsWithMetrics(
      Date startDate,
      Date stopDate,
      Long siteId,
      Long sellerId,
      Optional<String> name,
      Optional<Long> pid,
      Pageable pageable) {
    var site = siteRepository.findByPid(siteId);
    if (site.isPresent()) {
      var sellerSite = site.get();
      if (!sellerId.equals(sellerSite.getCompanyPid())) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_SITE_SELLER_INVALID_COMBINATION);
      }
    } else {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
    }
    Page<PositionMetricsAggregation> placements;
    pageable = JpaPolyfills.createPageRequestWithTableAliases(pageable, aliasMap);
    pageable = JpaPolyfills.addSortBy(pageable, Position_.PID);
    if (name.isPresent()) {
      placements =
          positionRepository.findPositionsByNameWithMetrics(
              startDate, stopDate, name.get(), siteId, pageable);
    } else if (pid.isPresent()) {
      placements =
          positionRepository.findPositionsByPidWithMetrics(
              startDate, stopDate, pid.get(), siteId, pageable);
    } else {
      placements =
          positionRepository.findPositionsWithMetrics(startDate, stopDate, siteId, pageable);
    }
    return getMetricsDataOnPlacements(placements, startDate, stopDate);
  }

  /** {@inheritDoc} */
  @Override
  public Page<PlacementSummaryDTO> getPlacementsWithMetricsWithoutSitePid(
      Date startDate, Date stopDate, Long sellerId, String name, Pageable pageable) {
    Page<PositionMetricsAggregation> placements;
    pageable = JpaPolyfills.createPageRequestWithTableAliases(pageable, aliasMap);
    pageable = JpaPolyfills.addSortBy(pageable, Position_.PID);
    placements =
        positionRepository.findPositionsByNameWithMetricsNoSitePid(
            startDate, stopDate, sellerId, name, pageable);

    return getMetricsDataOnPlacements(placements, startDate, stopDate);
  }

  private Page<PlacementSummaryDTO> getMetricsDataOnPlacements(
      Page<PositionMetricsAggregation> placements, Date prevStart, Date startDate) {
    List<Long> placementPids =
        placements.getContent().stream()
            .map(PositionMetricsAggregation::getPid)
            .collect(Collectors.toList());
    if (!placementPids.isEmpty()) {
      List<PositionMetricsAggregation> prevMetrics =
          positionRepository.findPositionsByPidsWithMetrics(prevStart, startDate, placementPids);
      Map<Long, PositionMetricsAggregation> prevPlacementsMap =
          prevMetrics.stream()
              .collect(
                  Collectors.toMap(
                      PositionMetricsAggregation::getPid, placementDTO -> placementDTO));
      for (int i = 0; i < placements.getContent().size(); i++) {
        PositionMetricsAggregation currentMetrics = placements.getContent().get(i);
        PositionMetricsAggregation prevMetric = prevPlacementsMap.get(currentMetrics.getPid());
        if (prevMetric != null) {
          double revenueTrendPercent =
              prevMetric.getSellerRevenue() > 0
                  ? (currentMetrics.getSellerRevenue() - prevMetric.getSellerRevenue())
                      / currentMetrics.getSellerRevenue()
                      * 100.0
                  : 0.0;
          currentMetrics.setRevenueTrendPercent(revenueTrendPercent);
        } else {
          currentMetrics.setRevenueTrendPercent(0.0);
        }
      }
    }
    return placements.map(PlacementSummaryDTOMapper.MAPPER::map);
  }
}

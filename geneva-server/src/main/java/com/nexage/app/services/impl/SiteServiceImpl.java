package com.nexage.app.services.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.SiteMetrics_;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.SiteViewRepository;
import com.nexage.admin.core.specification.SiteQueryFieldSpecification;
import com.nexage.admin.core.specification.SiteSpecification;
import com.nexage.admin.core.specification.SpecificationUtils;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.site.SiteDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SiteService;
import com.nexage.app.services.site.SiteQueryField;
import com.nexage.app.services.site.SiteQueryFieldParameter;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.nexage.app.util.validator.site.SiteQueryParams;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
@PreAuthorize(
    "@loginUserContext.isOcAdminNexage() "
        + "or @loginUserContext.isOcManagerNexage() "
        + "or @loginUserContext.isOcUserNexage() "
        + "or @loginUserContext.isOcAdminSeller() "
        + "or @loginUserContext.isOcManagerSeller() "
        + "or @loginUserContext.isOcUserSeller() "
        + "or @loginUserContext.isOcManagerYieldNexage()")
@Transactional(readOnly = true)
public class SiteServiceImpl implements SiteService {
  private final SiteRepository siteRepository;
  private final UserContext userContext;
  private final SiteViewRepository siteViewRepository;

  // There is a bug with JPQL. When the SELECT uses a CASE or a COALESCE
  // JPA doesn't recognize the alias whe doing the sorting
  // and it forces the sort to be prefixed by the table alias...
  private static Map<String, String> aliasMap = new HashMap<>();

  static {
    for (Field field : SiteMetrics_.class.getFields()) {
      aliasMap.put(field.getName(), SiteRepository.SITE_METRICS_TABLE_ALIAS);
    }
  }

  /** {@inheritDoc} */
  public Page<SiteDTO> getSites(
      Long sellerId,
      Pageable pageable,
      Optional<String> qt,
      Optional<List<String>> siteTypesOpt,
      Optional<List<String>> statusOpt,
      Optional<String> fetch) {
    checkSellerId(sellerId);
    if (fetch.isPresent() && fetch.get().equalsIgnoreCase("limited")) {
      return siteRepository
          .findLimitedSiteByCompanyPid(sellerId, pageable)
          .map(SiteDTOMapper.MAPPER::map);
    } else {
      Optional<Specification<Site>> siteTypeSpec =
          siteTypesOpt.map(
              st ->
                  SiteSpecification.withSiteTypes(
                      SiteSpecification.typeAndPlatformFromSiteTypes(st)));
      Optional<Specification<Site>> qtSpec = qt.map(SiteSpecification::withNameLike);
      Optional<Specification<Site>> activeSpec = statusOpt.map(SiteSpecification::withStatus);
      Optional<Specification<Site>> sellerIdSpec =
          Optional.of(sellerId).map(SiteSpecification::withSellerId);
      return SpecificationUtils.conjunction(siteTypeSpec, qtSpec, activeSpec, sellerIdSpec)
          .map(specs -> siteRepository.findAll(specs, pageable))
          .orElseThrow(
              () -> new GenevaValidationException(ServerErrorCodes.SERVER_ERROR_FETCHING_SITES))
          .map(SiteDTOMapper.MAPPER::map);
    }
  }

  /** {@inheritDoc} */
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  public Page<SiteDTO> getSitesForSellerSeat(
      Long sellerSeatPid,
      Pageable pageable,
      Optional<String> qt,
      Optional<List<String>> siteTypesOpt,
      Optional<List<String>> statusOpt,
      Optional<String> fetch) {

    if (fetch.orElse("").equalsIgnoreCase("limited")) {
      return siteRepository
          .findLimitedSiteBySellerSeatPid(sellerSeatPid, pageable)
          .map(SiteDTOMapper.MAPPER::map);
    } else {
      Optional<Specification<Site>> siteTypeSpec =
          siteTypesOpt.map(
              st ->
                  SiteSpecification.withSiteTypes(
                      SiteSpecification.typeAndPlatformFromSiteTypes(st)));
      Optional<Specification<Site>> qtSpec = qt.map(SiteSpecification::withNameLike);
      Optional<Specification<Site>> activeSpec = statusOpt.map(SiteSpecification::withStatus);
      Optional<Specification<Site>> sellerSeatIdSpec =
          Optional.of(sellerSeatPid).map(SiteSpecification::withSellerSeatPid);
      return SpecificationUtils.conjunction(siteTypeSpec, qtSpec, activeSpec, sellerSeatIdSpec)
          .map(specs -> siteRepository.findAll(specs, pageable))
          .orElseThrow(
              () -> new GenevaValidationException(ServerErrorCodes.SERVER_ERROR_FETCHING_SITES))
          .map(SiteDTOMapper.MAPPER::map);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Page<SiteDTO> searchSitesAndPositionsForSeller(
      Long sellerId, Optional<String> qtOpt, Pageable pageable) {
    Optional<Specification<Site>> sellerSpec;
    Optional<Specification<Site>> qtSpec;
    sellerSpec = Optional.of(sellerId).map(SiteSpecification::withSellerId);
    qtSpec = qtOpt.map(SiteSpecification::searchSitesAndPositions);
    return SpecificationUtils.conjunction(sellerSpec, qtSpec)
        .map(specs -> siteRepository.findAll(specs, pageable))
        .orElseThrow(
            () -> new GenevaValidationException(ServerErrorCodes.SERVER_ERROR_FETCHING_SITES))
        .map(SiteDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.canAccessSellersResource(#qf, #qt)")
  public Page<SiteDTO> getSites(String qf, Set<Long> qt, Pageable pageable) {
    validateSearchParamRequest(qf, qt, Site.class);
    return siteRepository
        .findAll(SiteSpecification.withIds(qf, qt), pageable)
        .map(SiteDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()")
  public Page<SiteDTO> getSites(SiteQueryParams queryParams, Pageable pageable) {
    var queryFieldParameter =
        SiteQueryFieldParameter.createFrom(queryParams, SiteQueryField.values());
    boolean isAndOperator = SearchQueryOperator.AND.equals(queryFieldParameter.getOperator());
    SiteQueryFieldSpecification.SiteQueryFieldSpecificationBuilder builder =
        SiteQueryFieldSpecification.builder();
    builder
        .isAndOperator(isAndOperator)
        .name(queryFieldParameter.getName())
        .globalAliasName(queryFieldParameter.getGlobalAliasName())
        .pids(queryFieldParameter.getPids())
        .companyPids(queryFieldParameter.getCompanyPids())
        .companyName(queryFieldParameter.getCompanyName());

    if (userContext.isOcUserBuyer()) {
      builder.userCompanyPids(Optional.of(userContext.getCompanyPids()));
    }

    queryFieldParameter
        .getStatus()
        .ifPresent(specsPids -> builder.statuses(queryFieldParameter.getStatus()));

    return siteRepository.findAll(builder.build(), pageable).map(SiteDTOMapper.MAPPER::map);
  }

  @Override
  public Page<SiteDTO> getSiteMinimalData(Long sellerPid, String qt, Pageable pageable) {
    if (isNotBlank(qt)) {
      return siteViewRepository
          .searchSellerSitesByName(sellerPid, qt, pageable)
          .map(SiteDTOMapper.MAPPER::map);
    }
    return siteViewRepository
        .findAllSellerSites(sellerPid, pageable)
        .map(SiteDTOMapper.MAPPER::map);
  }

  @Override
  public Page<SiteDTO> getSiteMinimalDataForSellerSeat(
      Long sellerSeatPid, String qt, Pageable pageable) {
    if (isNotBlank(qt)) {
      return siteViewRepository
          .searchSellerSitesByNameForSellerSeat(sellerSeatPid, qt, pageable)
          .map(SiteDTOMapper.MAPPER::map);
    }
    return siteViewRepository
        .findAllSellerSitesForSellerSeat(sellerSeatPid, pageable)
        .map(SiteDTOMapper.MAPPER::map);
  }

  private void validateSearchParamRequest(String qf, Set<Long> qt, Class classType) {
    boolean isValidQf = SearchRequestParamValidator.isValid(Sets.newHashSet(qf), classType);
    if (!isValidQf || CollectionUtils.isEmpty(qt))
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
  }

  private void checkSellerId(Long sellerId) {
    if (userContext.getType() == CompanyType.SELLER
        && !userContext.doSameOrNexageAffiliation(sellerId)) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }
}

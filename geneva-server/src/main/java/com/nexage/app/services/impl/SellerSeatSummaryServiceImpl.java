package com.nexage.app.services.impl;

import com.google.common.base.Strings;
import com.nexage.admin.core.model.BaseModel_;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import com.nexage.app.mapper.SellerSummaryDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSeatSummaryService;
import com.nexage.app.util.JpaPolyfills;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional(readOnly = true)
public class SellerSeatSummaryServiceImpl implements SellerSeatSummaryService {

  private final CompanyRepository companyRepository;
  private final UserContext userContext;

  /** {@inheritDoc} */
  @PreAuthorize("@loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#sellerSeatPid)")
  public Page<SellerSummaryDTO> findSummary(
      final Long sellerSeatPid,
      final Date startDate,
      final Date stopDate,
      final Set<String> qf,
      final String qt,
      Pageable pageable) {
    validateSearchParamRequest(qf, Company.class);
    pageable = JpaPolyfills.addSortBy(pageable, BaseModel_.PID);
    if (!CollectionUtils.isEmpty(qf) && !Strings.isNullOrEmpty(qt)) {
      if (qf.contains(Company_.NAME)) {
        return aggregateMetricsByName(sellerSeatPid, startDate, stopDate, qt, pageable);
      } else {
        log.info("Unable to perform operation for qf={}", qf);
      }
    }
    return aggregateMetrics(sellerSeatPid, startDate, stopDate, pageable);
  }

  private Page<SellerSummaryDTO> aggregateMetrics(
      Long sellerSeatPid, Date startDate, Date stopDate, Pageable pageable) {
    if (userContext.isNexageUser()) {
      return companyRepository
          .aggregateMetricsBySellerSeatPid(startDate, stopDate, sellerSeatPid, pageable)
          .map(SellerSummaryDTOMapper.MAPPER::map);
    } else {
      return companyRepository
          .aggregateNonNexageMetricsByCompanies(
              startDate, stopDate, userContext.getCompanyPids(), pageable)
          .map(SellerSummaryDTOMapper.MAPPER::map);
    }
  }

  private Page<SellerSummaryDTO> aggregateMetricsByName(
      Long sellerSeatPid, Date startDate, Date stopDate, String qt, Pageable pageable) {
    if (userContext.isNexageUser()) {
      return companyRepository
          .aggregateMetricsByNameAndSellerSeat(startDate, stopDate, qt, sellerSeatPid, pageable)
          .map(SellerSummaryDTOMapper.MAPPER::map);
    } else {
      return companyRepository
          .aggregateNonNexageMetricsByNameAndCompanies(
              startDate, stopDate, qt, userContext.getCompanyPids(), pageable)
          .map(SellerSummaryDTOMapper.MAPPER::map);
    }
  }

  private void validateSearchParamRequest(Set<String> qf, Class classType) {
    if (!SearchRequestParamValidator.isValid(qf, classType)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }
}

package com.nexage.app.services.impl;

import com.google.common.base.Strings;
import com.nexage.admin.core.model.BaseModel_;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Company_;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.seller.SellerSummaryDTO;
import com.nexage.app.mapper.SellerSummaryDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSummaryDTOService;
import com.nexage.app.util.JpaPolyfills;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
public class SellerSummaryDTOServiceImpl implements SellerSummaryDTOService {

  private final CompanyRepository companyRepository;
  private final UserContext userContext;

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcUserBuyer() or @loginUserContext.isOcAdminSeatHolder() or "
          + "@loginUserContext.isOcUserSellerSeat()")
  public Page<SellerSummaryDTO> findSummary(
      final Date startDate,
      final Date stopDate,
      final Set<String> qf,
      final String qt,
      Pageable pageable) {
    validateSearchParamRequest(qf, Company.class);
    pageable = JpaPolyfills.addSortBy(pageable, BaseModel_.PID);
    if (!CollectionUtils.isEmpty(qf) && !Strings.isNullOrEmpty(qt)) {
      if (qf.contains(Company_.NAME)) {
        return aggregateMetricsByName(startDate, fixStopDate(stopDate), qt, pageable);
      } else {
        log.info("Unable to perform operation for qf={}", qf);
      }
    }
    return aggregateMetrics(startDate, fixStopDate(stopDate), pageable);
  }

  private Page<SellerSummaryDTO> aggregateMetrics(
      Date startDate, Date stopDate, Pageable pageable) {
    if (userContext.isNexageUser()) {
      return companyRepository
          .aggregateMetrics(startDate, stopDate, pageable)
          .map(SellerSummaryDTOMapper.MAPPER::map);
    } else {
      return companyRepository
          .aggregateNonNexageMetricsByCompanies(
              startDate, stopDate, userContext.getCompanyPids(), pageable)
          .map(SellerSummaryDTOMapper.MAPPER::map);
    }
  }

  private Page<SellerSummaryDTO> aggregateMetricsByName(
      Date startDate, Date stopDate, String qt, Pageable pageable) {
    if (userContext.isNexageUser()) {
      return companyRepository
          .aggregateMetricsByName(startDate, stopDate, qt, pageable)
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

  // Need to make sure the time on the stop date includes the whole day
  private Date fixStopDate(Date stopDate) {
    if (stopDate == null) {
      return stopDate;
    }
    Calendar cal = DateUtils.toCalendar(stopDate);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    return cal.getTime();
  }
}

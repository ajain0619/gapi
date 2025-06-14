package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.ReportDefinition;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.ReportDefinitionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.dw.xstream.ReportDefinitionAdapter;
import com.nexage.admin.dw.xstream.XmlReportDefinition;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.ReportingService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.model.report.ReportType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service("reportingService")
public class ReportingServiceImpl implements ReportingService {

  private static final long EXCLUDE_OLD_REPORT = 27L;

  private final UserContext userContext;
  private final ReportDefinitionRepository reportDefinitionRepository;
  private final SiteRepository siteRepository;
  private final CompanyRepository companyRepository;
  private final ReportDefinitionAdapter reportDefAdapter;

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserSeller() or "
          + "@loginUserContext.isOcUserSeatHolder() or "
          + "@loginUserContext.isOcUserNexage() or "
          + "@loginUserContext.isOcUserBuyer()")
  public List<XmlReportDefinition> getAllReportDefinitions() {
    Long companyPid = getCompanyPid(userContext.getCompanyPids());

    if (companyPid == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }

    Company company =
        companyRepository
            .findById(companyPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    CompanyType companyType = userContext.getType();

    List<ReportDefinition> reportDefinitions =
        reportDefinitionRepository.findByCompanyTypesContainingOrderByDisplayOrder(companyType);
    List<XmlReportDefinition> xmlReportDefinitions = new ArrayList<>();

    for (ReportDefinition reportDefinition : reportDefinitions) {
      ReportType reportType = reportDefinition.getReportType();
      // don't include old Sparta rtb revenue performance report for
      // Nexage users, the right fix should be
      if (reportType == ReportType.RTB_REVENUE_PERFORMANCE
          && reportDefinition.getPid() == EXCLUDE_OLD_REPORT
          && userContext.isNexageUser()) {
        continue;
      }
      String reportDefinitionXml;
      if (companyType == CompanyType.SELLER) {
        reportDefinitionXml = getSellerReportDefinition(reportDefinition, company);
      } else {
        if (ReportType.CPI_CONVERSION == reportType
            && CompanyType.NEXAGE != company.getType()
            && !company.isCpiTrackingEnabled()) {
          // if it is not NEXAGE company type do not show report because CPI Tracking is not enabled
          reportDefinitionXml = null;
        } else {
          reportDefinitionXml = reportDefinition.getReportDefAsXml();
        }
      }
      if (!StringUtils.isBlank(reportDefinitionXml)) {
        xmlReportDefinitions.add(reportDefAdapter.getReportDefObject(reportDefinitionXml));
      }
    }
    return xmlReportDefinitions;
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserSeller() or "
          + "@loginUserContext.isOcUserSeatHolder() or "
          + "@loginUserContext.isOcUserNexage() or "
          + "@loginUserContext.isOcUserBuyer('Buyer', 'User')")
  public XmlReportDefinition getReportDefinition(String id) {
    ReportDefinition reportDefinition =
        reportDefinitionRepository
            .findById(id)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_REPORTDEF_NOT_FOUND));
    if (!reportDefinition.getCompanyTypes().contains(userContext.getType())) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
    return reportDefAdapter.getReportDefObject(reportDefinition.getReportDefAsXml());
  }

  /*
  at this point only seller seat user will have >1 company. As this
  reporting is going away, this is just a minimal effort to make a
  seller seat user see revenue report and render it in sellers
  dashboard in UI
  */
  private Long getCompanyPid(Set<Long> companyPids) {
    return Optional.of(companyPids.iterator())
        .filter(Iterator::hasNext)
        .map(Iterator::next)
        .orElse(null);
  }

  private String getSellerReportDefinition(ReportDefinition reportDefinition, Company company) {
    ReportType reportType = reportDefinition.getReportType();

    if ((reportType == ReportType.AD_SERVER && !company.isAdServingEnabled())
        || (!reportType.isAvailableToRestrictedSellers() && company.isRestrictDrillDown())
        || (!reportType.isAvailableToNonRestrictedSellers() && !company.isRestrictDrillDown())
        || (reportType == ReportType.RTB_NET_REVENUE_PERFORMANCE
            && !company.isRtbRevenueReportEnabled())
        // if the company does not have sites with impression groups enabled
        || (reportType == ReportType.IMPRESSION_GROUP_NET
            && !siteRepository.existsByCompanyPidAndGroupsEnabledTrueAndStatusGreaterThanEqual(
                company.getPid(), Status.INACTIVE))) {
      return null;
    } else {
      return reportDefinition.getReportDefAsXml();
    }
  }
}

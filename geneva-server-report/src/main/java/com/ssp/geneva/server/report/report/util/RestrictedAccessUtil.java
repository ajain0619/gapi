package com.ssp.geneva.server.report.report.util;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import com.nexage.admin.core.repository.SiteRepository;
import com.ssp.geneva.server.report.report.ReportUser;
import java.util.Set;
import org.springframework.stereotype.Service;

/** Helps to deal with restrictions. */
@Service
public class RestrictedAccessUtil {

  private final SiteRepository siteRepository;

  public RestrictedAccessUtil(SiteRepository siteRepository) {
    this.siteRepository = siteRepository;
  }

  /**
   * Returns restriction by site id for specified companyPid and reportUser.
   *
   * <p>If companyPid null - no site restrictions for nexageUser, and restriction by reportUser for
   * non nexageUser. If companyPid presented - site restriction by companyPid for nexageUser, and
   * restriction by companyPid and reportUser for non nexageUser
   *
   * @param companyPid company pid for which report was requested, may be null
   * @param reportUser user that requested report, must not be null
   * @return request to database should be restricted by this site ids
   */
  public Set<Long> getSiteIdsRestrictionForCompany(Long companyPid, ReportUser reportUser) {
    final Set<Long> companyPids = companyPid != null ? singleton(companyPid) : emptySet();
    return getSiteIdsRestrictionForCompanies(companyPids, reportUser);
  }

  /**
   * Performs same operation as {@link #getSiteIdsRestrictionForCompany(Long, ReportUser)} for
   * multiple companies
   *
   * @param companyPids set of companies pids for which report was requested, non null
   * @param reportUser user that requested report, must not be null
   * @return request to database should be restricted by this site ids
   */
  public Set<Long> getSiteIdsRestrictionForCompanies(Set<Long> companyPids, ReportUser reportUser) {
    if (reportUser == null) {
      throw new IllegalArgumentException("reportUser cannot be null");
    }

    // if no user pid provided, this request is coming from the reporting API
    if (!reportUser.getCompanies().isEmpty() && reportUser.getUserPid() == null) {
      return siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(reportUser.getCompanies());
    }

    if (companyPids.isEmpty()) {
      if (reportUser.isNexageUser()) {
        return null;
      } else {
        return siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
            reportUser.getUserPid(), reportUser.getCompanies());
      }
    } else {
      if (reportUser.isNexageUser()) {
        return siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(companyPids);
      } else {
        return siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
            reportUser.getUserPid(), companyPids);
      }
    }
  }
}

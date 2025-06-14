package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.HbPartner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HbPartnerRepository
    extends JpaRepository<HbPartner, Long>, JpaSpecificationExecutor<HbPartner> {

  /**
   * Finds active Hb Partners.
   *
   * @return A list of active Hb Partners.
   */
  @Query("SELECT p FROM HbPartner p WHERE p.status > 0")
  List<HbPartner> findActiveHbPartners();

  @Query(
      value = "SELECT hb_partner_pid FROM hb_partner_company WHERE company_pid = :companyPid",
      nativeQuery = true)
  List<Long> findPidsByCompanyPid(@Param("companyPid") Long companyPid);

  /**
   * Finds pids of Hb Partners which support formatted defaults.
   *
   * @return A list of pids of Hb Partners which support formatted defaults.
   */
  @Query(
      value = "SELECT pid FROM hb_partner WHERE formatted_default_type_enabled = 1",
      nativeQuery = true)
  List<Long> findPidsWhichSupportFormattedDefaults();

  @Query(
      "SELECT CASE WHEN COUNT(hb) > 0 THEN true ELSE false END FROM HbPartner hb WHERE hb.pid = :pid AND hb.multiImpressionBid = 1")
  boolean isHbPartnerEnabledForMultiBidding(@Param("pid") Long pid);

  @Query(
      "SELECT CASE WHEN COUNT(hb) > 0 THEN true ELSE false END FROM HbPartner hb WHERE hb.pid = :pid AND hb.fillMaxDuration = 1")
  boolean isHbPartnerEnabledForFillMaxDuration(@Param("pid") Long pid);
}

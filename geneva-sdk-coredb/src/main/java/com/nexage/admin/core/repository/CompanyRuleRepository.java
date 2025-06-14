package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRuleRepository
    extends JpaRepository<CompanyRule, Long>, JpaSpecificationExecutor<CompanyRule> {
  /**
   * Get all {@link CompanyRule} from given seller id
   *
   * @param sellerId The seller id.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyRule} instances based on parameters.
   */
  @Query(
      "SELECT r FROM CompanyRule r JOIN r.deployedCompanies c WHERE c.pid = :sellerId AND r.ruleType = 0")
  Page<CompanyRule> findBidManagementRulesBySellerId(
      @Param("sellerId") Long sellerId, Pageable pageable);

  /**
   * Get all {@link CompanyRule} from given site id
   *
   * @param siteId The site id.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyRule} instances based on parameters.
   */
  @Query(
      "SELECT r FROM CompanyRule r JOIN r.deployedSites s WHERE s.pid = :siteId AND r.ruleType = 0")
  Page<CompanyRule> findBidManagementRulesBySiteId(@Param("siteId") Long siteId, Pageable pageable);

  /**
   * Get all {@link CompanyRule} from given placement id
   *
   * @param placementPid The placement Pid.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyRule} instances based on parameters.
   */
  @Query(
      "SELECT r FROM CompanyRule r JOIN r.deployedPositions p WHERE p.pid = :placementPid AND r.ruleType = 0")
  Page<CompanyRule> findBidManagementRulesByPlacementId(
      @Param("placementPid") Long placementPid, Pageable pageable);

  /**
   * Get all {@link CompanyRule} from given rule ids
   *
   * @param rulesPids The rule pids.
   * @param pageable Pagination based on {@link Pageable}
   * @return {@link Page} of {@link CompanyRule} instances based on parameters.
   */
  @Query("SELECT r FROM CompanyRule r WHERE r.pid IN (:rulesPids) AND r.ruleType = 0")
  Page<CompanyRule> findBidManagementRulesByPids(
      @Param("rulesPids") List<Long> rulesPids, Pageable pageable);

  /**
   * Get all {@link CompanyRule} for the given seller id and name
   *
   * @param sellerId The seller id
   * @param name The name
   * @return {@link List} of {@link CompanyRule} instances based on parameters.
   */
  @Query(
      "SELECT DISTINCT r FROM CompanyRule r LEFT JOIN FETCH r.ruleTargets t LEFT JOIN FETCH r.deployedCompanies c "
          + "  LEFT JOIN FETCH r.deployedSites s LEFT JOIN FETCH r.deployedPositions p "
          + "WHERE r.name=:name AND r.ownerCompanyPid=:sellerId")
  List<CompanyRule> findByNameAndSellerId(
      @Param("sellerId") Long sellerId, @Param("name") String name);

  /**
   * Get all {@link CompanyRule} from given seller id
   *
   * @param rulePid The rule pid.
   * @param statuses The statuses.
   * @return {@link List} of {@link CompanyRule} instances based on parameters.
   */
  @Query(
      "SELECT DISTINCT r FROM Rule r LEFT JOIN FETCH r.ruleTargets t LEFT JOIN FETCH r.deployedCompanies c "
          + "  LEFT JOIN FETCH r.deployedSites s LEFT JOIN FETCH r.deployedPositions p "
          + "WHERE r.pid=:rulePid AND r.status IN (:statuses)")
  CompanyRule findByPidAndStatuses(
      @Param("rulePid") Long rulePid, @Param("statuses") List<Status> statuses);

  /**
   * Get all {@link CompanyRule} for given rules pids and owner companies pids
   *
   * @param rulesPids The rules pids.
   * @param companyPids The company pids
   * @return {@link Page} of {@link CompanyRule} instances based on parameters.
   */
  @Query(
      "SELECT r FROM CompanyRule r WHERE r.pid IN (:rulesPids) AND r.ownerCompanyPid IN (:companyPids)")
  List<CompanyRule> findRulesByPidsAndOwnerCompanyPids(
      @Param("rulesPids") Set<Long> rulesPids, @Param("companyPids") Set<Long> companyPids);

  /**
   * Get {@link CompanyRule} with the given name and owner company
   *
   * @param name The name
   * @param companyPid The company pid
   * @return {@link CompanyRule} instance based on parameters or null if not found
   */
  CompanyRule findByNameAndOwnerCompanyPid(String name, Long companyPid);

  /**
   * Get {@link CompanyRule} by its pid and company pid that it belongs to
   *
   * @param rulePid rule pid
   * @param companyPid The company pid
   * @return single {@link CompanyRule} or <code>null</code> if rule was not found
   */
  CompanyRule findByPidAndOwnerCompanyPid(Long rulePid, Long companyPid);

  /**
   * Get {@link CompanyRule} by its pid, the company pid that it belongs to and its rule type
   *
   * @param rulePid rule pid
   * @param companyPid The company pid
   * @param ruleTypes The rule types
   * @return single {@link CompanyRule} or <code>null</code> if rule was not found
   */
  CompanyRule findByPidAndOwnerCompanyPidAndRuleTypeIn(
      Long rulePid, Long companyPid, Collection<RuleType> ruleTypes);

  /**
   * Soft-deletes a requested rule by setting {@link CompanyRule#status} to {@link
   * com.nexage.admin.core.enums.Status#DELETED}
   */
  @Query("UPDATE CompanyRule SET status = -1 WHERE pid=?1")
  @Modifying
  @Transactional
  void delete(Long rulePid);

  /**
   * Get all {@link CompanyRule} from given deal id list
   *
   * @param rulePids The rules pids.
   * @return {@link List} of {@link CompanyRule} instances based on parameters.
   */
  @Query("SELECT r FROM CompanyRule r WHERE r.pid IN (:rulesPids) AND r.ruleType = 2")
  List<CompanyRule> findDealRulesByRulePidIn(@Param("rulesPids") Set<Long> rulePids);
}

package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.model.CompanyRule;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository
    extends JpaRepository<CompanyRule, Long>, JpaSpecificationExecutor<CompanyRule> {

  @Query(
      "SELECT DISTINCT r FROM CompanyRule r LEFT JOIN FETCH r.ruleTargets t LEFT JOIN FETCH r.deployedCompanies c "
          + "  LEFT JOIN FETCH r.deployedSites s LEFT JOIN FETCH r.deployedPositions p "
          + "WHERE r.pid=:pid AND r.status >= 0")
  Optional<CompanyRule> findActualByPid(@Param("pid") Long pid);

  @Query(
      "SELECT r FROM CompanyRule r, DealRule dr WHERE r.pid = dr.rulePid AND dr.deal.pid = :dealPid "
          + "AND r.ruleType = 2 AND r.status >= 0")
  List<CompanyRule> findAllActiveDealRulesAssosiatedWithDeal(@Param("dealPid") Long dealPid);

  @Query(
      "SELECT r FROM CompanyRule r WHERE r.pid =:rulePid AND r.status >= 0 AND r.ruleType = :type")
  Optional<CompanyRule> findByPidAndRuleType(
      @Param("rulePid") Long rulePid, @Param("type") RuleType type);

  @Query("SELECT r.pid FROM CompanyRule r WHERE r.status > 0 AND r.ruleFormula.autoUpdate = true")
  List<Long> findRulesUpdateableWithNewlyApplicablePlacements();
}

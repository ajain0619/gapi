package com.nexage.admin.core.repository;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.RuleTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleTargetRepository
    extends JpaRepository<RuleTarget, Long>, JpaSpecificationExecutor<RuleTarget> {

  @Query(
      "SELECT CASE WHEN LENGTH(rt.data) - LENGTH(REPLACE(rt.data, ':', '')) = 1 THEN true"
          + " ELSE false END FROM RuleTarget rt WHERE rt.rule.pid=:rulePid"
          + " AND rt.ruleTargetType=:ruleTargetType")
  Boolean hasOneDsp(
      @Param("rulePid") Long rulePid, @Param("ruleTargetType") RuleTargetType ruleTargetType);

  @Query(
      "SELECT CASE WHEN LENGTH(rt.data) > 0 THEN true"
          + " ELSE false END FROM RuleTarget rt WHERE rt.rule.pid=:rulePid")
  Boolean hasRuleTarget(@Param("rulePid") Long rulePid);

  @Query(
      "SELECT CASE WHEN (COUNT(rt)>0) THEN true"
          + " ELSE false END FROM RuleTarget rt WHERE rt.rule.pid=:rulePid"
          + " AND NOT rt.ruleTargetType=:ruleTargetType"
          + " AND LENGTH(rt.data)>0")
  Boolean hasRuleTargetOtherThanProvided(
      @Param("rulePid") Long rulePid, @Param("ruleTargetType") RuleTargetType ruleTargetType);
}

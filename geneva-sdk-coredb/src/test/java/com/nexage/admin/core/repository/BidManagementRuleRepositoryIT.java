package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.google.common.collect.Sets;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.CompanyRule;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/bid-management-rule-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BidManagementRuleRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired CompanyRuleRepository companyRuleRepository;

  @Test
  void shouldGetAllRulesBySellerId() {
    Page<CompanyRule> rules =
        companyRuleRepository.findBidManagementRulesBySellerId(1L, PageRequest.of(0, 10));
    assertEquals(1, rules.getTotalPages());
    assertEquals(1L, (long) rules.getContent().get(0).getPid());
  }

  @Test
  void shouldGetAllRulesBySiteId() {
    Page<CompanyRule> rules =
        companyRuleRepository.findBidManagementRulesBySiteId(1L, PageRequest.of(0, 10));
    assertEquals(1, rules.getTotalPages());
    assertEquals(1L, (long) rules.getContent().get(0).getPid());
  }

  @Test
  void shouldGetAllRulesByPlacementId() {
    List<CompanyRule> list = companyRuleRepository.findAll();
    Page<CompanyRule> rules =
        companyRuleRepository.findBidManagementRulesByPlacementId(1L, PageRequest.of(0, 10));
    assertEquals(1, rules.getTotalPages());
    assertEquals(1L, (long) rules.getContent().get(0).getPid());
  }

  @Test
  void shouldGetAllRulesByRuleIds() {
    Page<CompanyRule> rules =
        companyRuleRepository.findBidManagementRulesByPids(
            Arrays.asList(1L), PageRequest.of(0, 10));
    assertEquals(1, rules.getTotalPages());
    assertEquals(1L, (long) rules.getContent().get(0).getPid());
  }

  @Test
  void shouldGetRulesByRulePidsAndCompanyPids() {
    List<CompanyRule> rules =
        companyRuleRepository.findRulesByPidsAndOwnerCompanyPids(
            Sets.newHashSet(1L), Sets.newHashSet(1L));
    assertFalse(rules.isEmpty());
    assertEquals(1L, (long) rules.get(0).getPid());
    assertEquals(1L, (long) rules.get(0).getOwnerCompanyPid());
  }

  @Test
  void shouldGetRulesByNameAndPublisherId() {
    List<CompanyRule> rules = companyRuleRepository.findByNameAndSellerId(1L, "rule name 1");
    assertFalse(rules.isEmpty());
    assertEquals(1, rules.size());
    assertEquals(1L, (long) rules.get(0).getPid());
    assertEquals("rule name 1", rules.get(0).getName());
  }
}

package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.DirectDeal_;
import com.nexage.admin.core.specification.DirectDealSpecification;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = {"/data/repository/deals-common.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class DirectDealRepositoryIT extends CoreDbSdkIntegrationTestBase {
  private static final Long DEAL_PID = 1L;

  @Autowired private DirectDealRepository directDealRepository;

  @Test
  void shouldFindByPid() {
    // when
    var dealView =
        directDealRepository
            .findByPid(DEAL_PID)
            .orElseThrow(() -> new EntityNotFoundException("DirectDeal not found"));

    // then
    assertEquals(DEAL_PID, dealView.getPid());
  }

  @Test
  void shouldFindAllWithRules() {
    // when
    var out =
        directDealRepository.findAll(DirectDealSpecification.of(null, null), Pageable.unpaged());

    // then
    Iterator<DirectDeal> it = out.iterator();
    assertEquals(2, out.getTotalElements());
    assertEquals(1, it.next().getPid());
    assertEquals(2, it.next().getPid());
  }

  @Test
  void shouldFindAllWithoutRules() {
    // given
    var qf = new HashSet<String>();
    qf.add("hasRules");

    // when
    var out =
        directDealRepository.findAll(DirectDealSpecification.of(qf, "false"), Pageable.unpaged());

    // then
    assertEquals(3, out.getTotalElements());
    assertEquals(110, out.iterator().next().getPid());
  }

  @Test
  void shouldFindAll() {
    // given
    var qf = new HashSet<String>();
    qf.add("all");

    // when
    var out =
        directDealRepository.findAll(DirectDealSpecification.of(qf, "true"), PageRequest.of(1, 10));

    // then
    assertEquals(5, out.getTotalElements());
  }

  @Test
  void shouldFindAllByDealIdAndDealCategory() {
    // given
    var map = new HashMap<String, List<String>>();
    map.put("dealCategory", Collections.singletonList("SSP"));
    map.put("dealId", Collections.singletonList("5"));

    // when
    var out = directDealRepository.findAll(DirectDealSpecification.of(map), PageRequest.of(1, 10));

    // then
    assertEquals(3, out.getTotalElements());
  }

  @Test
  void shouldFindAllByDealDescAndDealCategory() {
    // given
    var map = new HashMap<String, List<String>>();
    map.put("dealCategory", Collections.singletonList("SSP"));
    map.put("description", Collections.singletonList("deal"));

    // when
    var out = directDealRepository.findAll(DirectDealSpecification.of(map), PageRequest.of(1, 10));

    // then
    assertEquals(3, out.getTotalElements());
  }

  @Test
  void shouldReturnDealBasedOnDealCategoryAndFewCharsFromDesc() {
    // given
    var map = new HashMap<String, List<String>>();
    map.put("dealCategory", Collections.singletonList("SSP"));
    map.put("description", Collections.singletonList("de"));

    // when
    var out = directDealRepository.findAll(DirectDealSpecification.of(map), PageRequest.of(1, 10));

    // then
    assertEquals(3, out.getTotalElements());
  }

  @Test
  void shouldFindAllByDealCategory() {
    // given
    var map = new HashMap<String, List<String>>();
    map.put("dealCategory", Collections.singletonList("SSP"));

    // when
    var out = directDealRepository.findAll(DirectDealSpecification.of(map), PageRequest.of(1, 10));

    // then
    assertEquals(1, out.getTotalElements());
  }

  @Test
  void shouldFindAllByDealId() {
    // given
    var map = new HashMap<String, List<String>>();
    map.put("all", Collections.singletonList("true"));
    map.put("dealId", Collections.singletonList("5"));

    // when
    var out = directDealRepository.findAll(DirectDealSpecification.of(map), PageRequest.of(1, 10));

    // then
    assertEquals(3, out.getTotalElements());
  }

  @Test
  void shouldFindAllByPriorityType() {
    // given
    var map = new HashMap<String, List<String>>();
    map.put("all", Collections.singletonList("true"));
    map.put(DirectDeal_.PRIORITY_TYPE, Collections.singletonList("OPEN"));

    // when
    var out = directDealRepository.findAll(DirectDealSpecification.of(map), PageRequest.of(1, 10));

    // then
    assertEquals(0, out.getTotalElements());
  }

  @Test
  void shouldFindDealsForSuppliedDealIds() {
    // when
    List<DirectDeal> deals = directDealRepository.findByDealIdIn(List.of("5550", "5551"));

    // then
    assertEquals(2, deals.size());
  }

  @Test
  void shouldFindDirectDealByDealId() {
    // given
    String dealId = "5550";

    // when
    DirectDeal directDeal =
        directDealRepository
            .findByDealId(dealId)
            .orElseThrow(() -> new EntityNotFoundException("DirectDeal not found in DB"));

    // then
    assertEquals(dealId, directDeal.getDealId());
    assertNotNull(directDeal.getPid());
  }

  @Test
  void shouldCountDirectDealsByBealId() {
    // given
    String dealId1 = "5550";
    String dealId2 = "55500";

    // when
    long result1 = directDealRepository.countByDealId(dealId1);
    long result2 = directDealRepository.countByDealId(dealId2);

    // then
    assertEquals(1, result1);
    assertEquals(0, result2);
  }

  @Test
  void shouldFindAllDealsWithRules() {
    // when
    List<DirectDeal> withRules = directDealRepository.findByRulesNotNull();

    // then
    assertEquals(3, withRules.size());
  }

  @Test
  void shouldFindActiveDealPidsWithAutoUpdateFormula() {
    // when
    List<Long> dealPids = directDealRepository.findActiveDealsWithAutoUpdateFormula();

    // then
    assertEquals(2, dealPids.size());
    assertEquals(2L, dealPids.get(0));
    assertEquals(110L, dealPids.get(1));
  }

  @Test
  void shouldSaveDeal() {
    // given
    DirectDeal directDeal = new DirectDeal();
    directDeal.setPid(3L);
    directDeal.setStatus(DirectDeal.DealStatus.Active);
    directDeal.setPriorityType(DealPriorityType.OPEN);
    directDeal.setCurrency("ISK");
    directDeal.setDealCategory(1);
    directDeal.setDealId("af");

    // when
    directDealRepository.save(directDeal);

    // then
    DirectDeal resultDeal =
        directDealRepository
            .findById(directDeal.getPid())
            .orElseThrow(() -> new EntityNotFoundException("DirectDeal not found in DB"));
    assertNotNull(resultDeal);
    assertEquals(directDeal.getPid(), resultDeal.getPid());
    assertNotNull(resultDeal.getUpdatedOn());
  }
}

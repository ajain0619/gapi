package com.nexage.admin.core.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.BuyerGroup_;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
@Sql(
    scripts = "/data/repository/buyer-group-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BuyerGroupSpecificationIT extends CoreDbSdkIntegrationTestBase {

  private static final long PID = 1L;
  @Autowired private BuyerGroupRepository buyerGroupRepository;

  @Test
  void shouldFindSpecificationWithGivenCompanyPid() {
    // given
    Specification<BuyerGroup> spec = BuyerGroupSpecification.withCompanyPid(PID);

    // when
    List<BuyerGroup> result = buyerGroupRepository.findAll(spec);

    // then
    assertEquals(3, result.size());
    assertEquals(
        Set.of(1L, 2L, 3L), result.stream().map(BuyerGroup::getPid).collect(Collectors.toSet()));
  }

  @Test
  void shouldFindSpecificationWithGivenCompanyPidAndSearchCriteria() {
    // given
    Set<String> qf = Collections.singleton(BuyerGroup_.NAME);
    final String qt = "buyer_group_1";
    Specification<BuyerGroup> spec =
        BuyerGroupSpecification.withCompanyPidAndSearchCriteria(PID, qf, qt);

    // when
    List<BuyerGroup> result = buyerGroupRepository.findAll(spec);

    // then
    assertEquals(1, result.size());
    assertEquals(Set.of(1L), result.stream().map(BuyerGroup::getPid).collect(Collectors.toSet()));
  }
}

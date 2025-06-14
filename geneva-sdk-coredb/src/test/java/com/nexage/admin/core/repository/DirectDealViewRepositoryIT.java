package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.DirectDealView;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/direct-deal-view-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class DirectDealViewRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private DirectDealViewRepository repository;

  @Test
  void shouldFindByPids() {
    List<Long> pids = List.of(1L, 3L);

    List<DirectDealView> actual = repository.findByPidIn(pids);

    assertEquals(2, actual.size());
    assertEquals(
        Set.of(1L, 3L), actual.stream().map(DirectDealView::getPid).collect(Collectors.toSet()));
  }
}

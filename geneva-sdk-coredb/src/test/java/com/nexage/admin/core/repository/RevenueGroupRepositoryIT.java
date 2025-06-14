package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.RevenueGroup;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(
    scripts = "/data/repository/revenue-group-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class RevenueGroupRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private RevenueGroupRepository revenueGroupRepository;

  @Test
  void shouldFetchAllActiveRevenueGroups() {
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "pid");
    Page<RevenueGroup> result = revenueGroupRepository.findAllByStatus(Status.ACTIVE, pageable);

    assertEquals(4, result.getTotalElements());
    assertEquals(4, result.getContent().size());
    assertEquals(1, result.getTotalPages());
    assertEquals(0, result.getPageable().getPageNumber());
  }

  @Test
  void shouldFetchPagedRevenueGroups() {
    Pageable pageable = PageRequest.of(1, 2, Sort.Direction.ASC, "pid");
    Page<RevenueGroup> result = revenueGroupRepository.findAllByStatus(Status.ACTIVE, pageable);

    assertEquals(4, result.getTotalElements());
    assertEquals(2, result.getContent().size());
    assertEquals(3L, result.getContent().get(0).getPid());
    assertEquals(4L, result.getContent().get(1).getPid());
    assertEquals(2, result.getTotalPages());
    assertEquals(1, result.getPageable().getPageNumber());
  }

  @Test
  void shouldCountByPidIn() {
    assertEquals(2L, revenueGroupRepository.countByPidIn(List.of(1L, 2L, 6L, 7L)));
  }
}

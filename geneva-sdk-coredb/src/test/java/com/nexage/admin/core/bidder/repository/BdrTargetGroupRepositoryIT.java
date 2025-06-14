package com.nexage.admin.core.bidder.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.repository.BdrTargetGroupRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Sql(
    scripts = {
      "/data/repository/bdr-insertion-order-repository.sql",
      "/data/repository/bdr-line-item-repository.sql",
      "/data/repository/bdr-target-group-repository.sql"
    },
    config = @SqlConfig(encoding = "utf-8"))
class BdrTargetGroupRepositoryIT extends CoreDbSdkIntegrationTestBase {
  @Autowired private BdrTargetGroupRepository bdrTargetGroupRepository;

  @Test
  void shouldUpdateAllVersions() {
    // given
    BdrTargetGroup targetGroup =
        bdrTargetGroupRepository.findById(1L).orElseThrow(UnsupportedOperationException::new);
    Date now = Calendar.getInstance().getTime();
    targetGroup.setName(RandomStringUtils.randomAlphanumeric(10));
    targetGroup.getLineItem().setUpdatedOn(now);
    targetGroup.getLineItem().getInsertionOrder().setUpdatedOn(now);

    // when
    bdrTargetGroupRepository.saveAndFlush(targetGroup);

    // then
    assertEquals(1, targetGroup.getVersion());
    assertEquals(1, targetGroup.getLineItem().getVersion());
    assertEquals(1, targetGroup.getLineItem().getInsertionOrder().getVersion());
  }

  @Test
  void shouldFindAll() {
    // when
    List<BdrTargetGroup> bdrTargetGroups = bdrTargetGroupRepository.findAll();

    // then
    assertEquals(
        Set.of(1L),
        bdrTargetGroups.stream().map(BdrTargetGroup::getPid).collect(Collectors.toSet()));
  }
}

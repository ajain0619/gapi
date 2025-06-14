package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.DeviceOs;
import com.nexage.admin.core.specification.GeneralSpecification;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "/data/repository/device-os-repository.sql", config = @SqlConfig(encoding = "utf-8"))
@Transactional(propagation = Propagation.REQUIRES_NEW)
class DeviceOsRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private DeviceOsRepository deviceOsRepository;

  @Test
  void shouldFindRepositoriesWithMatchingName() {
    Page<DeviceOs> page =
        deviceOsRepository.findAll(
            GeneralSpecification.withSearchCriteria(ImmutableSet.of("name"), "ios"),
            PageRequest.of(0, 10));
    assertEquals(1, page.getTotalElements());
    assertEquals("ios", page.getContent().get(0).getName());
    assertEquals(3, page.getContent().get(0).getPid());
  }

  @Test
  void shouldFindAllOsWithMatchingNames() {
    List<DeviceOs> data = deviceOsRepository.findByNameIn(Set.of("ios", "android"));
    assertEquals(2, data.size());
    assertEquals("android", data.get(0).getName());
    assertEquals("ios", data.get(1).getName());
  }
}

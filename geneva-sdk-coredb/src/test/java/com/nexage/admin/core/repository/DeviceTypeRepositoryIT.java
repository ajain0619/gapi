package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.DeviceType;
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

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/device-type-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class DeviceTypeRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private DeviceTypeRepository deviceTypeRepository;

  @Test
  void testFindAllDevices() {
    Page<DeviceType> page =
        deviceTypeRepository.findAll(
            GeneralSpecification.withSearchCriteria(ImmutableSet.of("name"), "Phone"),
            PageRequest.of(0, 10));
    assertEquals(1, page.getTotalElements());
    assertEquals("Phone", page.getContent().get(0).getName());
    assertEquals(3, page.getContent().get(0).getPid());
    assertEquals(4, page.getContent().get(0).getId());
  }

  @Test
  void shouldFindAllDevicesWithMatchingNames() {
    List<DeviceType> data = deviceTypeRepository.findByNameIn(Set.of("Phone", "Tablet"));
    assertEquals(2, data.size());
    assertEquals("Phone", data.get(0).getName());
    assertEquals("Tablet", data.get(1).getName());
  }
}

package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DeviceType;
import com.nexage.admin.core.repository.DeviceTypeRepository;
import com.nexage.app.dto.DeviceTypeDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class DeviceTypeServiceImplTest {
  @Mock private DeviceTypeRepository deviceTypeRepository;

  @Mock private Pageable pageable;

  @InjectMocks private DeviceTypeServiceImpl deviceTypeService;

  Page<DeviceType> pagedEntity;

  @BeforeEach
  void setup() {
    pagedEntity =
        new PageImpl<DeviceType>(TestObjectsFactory.gimme(1, DeviceType.class))
            .map(
                deviceType -> {
                  deviceType.setPid(1L);
                  deviceType.setName("type1");
                  deviceType.setId(2);
                  return deviceType;
                });
  }

  @Test
  void testFindAll() {
    when(deviceTypeRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<DeviceTypeDTO> returnedPage = deviceTypeService.findAll("", null, pageable);

    assertEquals(1, returnedPage.getTotalElements());
    assertEquals("type1", returnedPage.stream().findFirst().get().getName());
    assertEquals(2, returnedPage.stream().findFirst().get().getId());
  }
}

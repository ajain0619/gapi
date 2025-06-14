package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.DeviceOs;
import com.nexage.admin.core.repository.DeviceOsRepository;
import com.nexage.app.dto.DeviceOsDTO;
import java.util.List;
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
class DeviceOsServiceImplTest {
  @Mock private DeviceOsRepository deviceOsRepository;

  @Mock private Pageable pageable;

  @InjectMocks private DeviceOsServiceImpl deviceOsService;

  Page<DeviceOs> pagedEntity;

  @BeforeEach
  void setup() {
    pagedEntity = new PageImpl<DeviceOs>(List.of(new DeviceOs(1L, "linux")));
  }

  @Test
  void shouldFindAllDeviceOsWithNoCriteria() {
    when(deviceOsRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);
    Page<DeviceOsDTO> returnedPage = deviceOsService.findAllByName("", null, pageable);

    assertEquals(1, returnedPage.getTotalElements());
    assertEquals("linux", returnedPage.stream().findFirst().get().getName());
  }
}

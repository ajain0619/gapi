package com.nexage.app.web;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.nexage.app.dto.DeviceOsDTO;
import com.nexage.app.services.DeviceOsService;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class DeviceOsControllerIT {

  private MockMvc mockMvc;
  @Mock private DeviceOsService deviceOsService;
  @InjectMocks private DeviceOsController deviceOsController;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(deviceOsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getDeviceOsData() throws Exception {
    Pageable pageRequest = PageRequest.of(0, 1);
    DeviceOsDTO deviceOsDTO = new DeviceOsDTO(1L, "android");

    Page<DeviceOsDTO> dtoPage = new PageImpl(List.of(deviceOsDTO), pageRequest, 1);
    when(deviceOsService.findAllByName(
            nullable(String.class), nullable(Set.class), nullable(Pageable.class)))
        .thenReturn(dtoPage);
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(URI.create("/v1/device-os")))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("content[0].pid").value(1))
        .andExpect(jsonPath("content[0].name").value("android"));
  }
}

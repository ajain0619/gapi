package com.nexage.app.web;

import static com.nexage.app.web.support.TestObjectsFactory.createRevenueGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.dto.RevenueGroupDTO;
import com.nexage.app.mapper.RevenueGroupDTOMapper;
import com.nexage.app.services.RevenueGroupService;
import com.nexage.app.web.support.BaseControllerItTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RevenueGroupControllerIT extends BaseControllerItTest {

  @Mock private RevenueGroupService revenueGroupService;
  @InjectMocks private RevenueGroupController revenueGroupController;

  @BeforeEach
  void setUp() throws Exception {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(revenueGroupController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  void shouldFetchAllRevenueGroups() throws Exception {
    // given
    Page<RevenueGroupDTO> serviceOutput =
        new PageImpl<>(List.of(createRevenueGroup(), createRevenueGroup(), createRevenueGroup()))
            .map(RevenueGroupDTOMapper.MAPPER::map);
    given(revenueGroupService.getRevenueGroups(any())).willReturn(serviceOutput);

    // when & then
    MvcResult result =
        mockMvc.perform(get("/v1/revenue-groups")).andExpect(status().isOk()).andReturn();
    assertEquals(
        result.getResponse().getContentAsString(),
        new ObjectMapper().writeValueAsString(serviceOutput));
  }
}

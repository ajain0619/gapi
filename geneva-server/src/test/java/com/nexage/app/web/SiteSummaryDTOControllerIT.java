package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.nexage.app.dto.seller.SiteSummaryDTO;
import com.nexage.app.dto.seller.SitesSummaryDTO;
import com.nexage.app.services.SitesSummaryDTOService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SiteSummaryDTOControllerIT {

  private MockMvc mockMvc;

  @Mock private SitesSummaryDTOService sitesSummaryDTOService;

  @InjectMocks private SiteSummaryDTOController siteSummaryDTOController;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(siteSummaryDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getSitesByPids() throws Throwable {
    SitesSummaryDTO sitesSummaryDTO = new SitesSummaryDTO();
    List<SiteSummaryDTO> siteSummaryDTOList = new ArrayList<>();
    SiteSummaryDTO first = new SiteSummaryDTO();
    first.setPId(Math.abs(new Random().nextLong()));
    first.setName(RandomStringUtils.randomAlphanumeric(12));
    siteSummaryDTOList.add(first);
    Page<SiteSummaryDTO> siteSummaryDTOPage = new PageImpl<>(siteSummaryDTOList);
    sitesSummaryDTO.setSites(siteSummaryDTOPage);
    when(sitesSummaryDTOService.getSitesSummaryDTO(
            anyLong(), any(Date.class), any(Date.class), any(), any(), any(Pageable.class)))
        .thenReturn(sitesSummaryDTO);
    mockMvc
        .perform(
            get(
                "/v1/sellers/1/sites/summaries?page=0&size=10&startDate=2019-05-21&stopDate=2019-05-27&pids=123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("sites.content.[0].pid", is(first.getPId())))
        .andExpect(jsonPath("sites.content.[0].name", is(first.getName())));
  }

  @Test
  void findSummaryParseException() throws Exception {
    mockMvc
        .perform(
            get(
                "/v1/sellers/123/sites/summaries?page=0&size=10&startDate=2019-05-21&stopDate=2019-0527"))
        .andExpect(status().isBadRequest());
  }
}

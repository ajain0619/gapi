package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.SiteService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class SiteDTOControllerIT {

  private static final String BASE_URL = "/v1/sites";

  private MockMvc mockMvc;
  private final List<SiteDTO> defaultSites = TestObjectsFactory.createSite(3);

  @Mock private SiteService siteService;
  @Mock private BeanValidationService beanValidationService;

  @InjectMocks private SiteDTOController siteDTOController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(siteDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  void shouldFetchSitesForSellers() throws Exception {
    Page<SiteDTO> sites = new PageImpl<>(defaultSites);
    given(siteService.getSites(any(), any(), any())).willReturn(sites);

    mockMvc
        .perform(get(BASE_URL + "?qf=companyPid&qt=1,2,3"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pId", is(defaultSites.get(0).getPid())))
        .andExpect(jsonPath("content.[1].pId", is(defaultSites.get(1).getPid())))
        .andExpect(jsonPath("content.[2].pId", is(defaultSites.get(2).getPid())));
  }

  @Test
  void shouldReturnGenevaValidationException() throws Exception {
    Page<SiteDTO> sites = new PageImpl<>(defaultSites);
    given(siteService.getSites(any(), any(), any())).willReturn(sites);

    mockMvc
        .perform(get(BASE_URL + "?qf=companyPid&qt=1,2,3Bogus"))
        .andExpect(status().isBadRequest());
    mockMvc.perform(get(BASE_URL)).andExpect(status().isBadRequest());
  }

  @Test
  void shouldFetchSitesForMultiSearch() throws Exception {
    Page<SiteDTO> sites = new PageImpl<>(defaultSites);
    given(siteService.getSites(any(), any())).willReturn(sites);

    mockMvc
        .perform(get(BASE_URL + "?multiSearch&qf=name=test,globalAliasName=test"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pId", is(defaultSites.get(0).getPid())))
        .andExpect(jsonPath("content.[1].pId", is(defaultSites.get(1).getPid())))
        .andExpect(jsonPath("content.[2].pId", is(defaultSites.get(2).getPid())));
  }
}

package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.SiteView;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.mapper.site.SiteDTOMapper;
import com.nexage.app.services.SiteService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class SellerSiteDTOControllerIT {

  private MockMvc mockMvc;

  @InjectMocks private SellerSiteDTOController siteDTOController;

  @Mock private SiteService siteService;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(siteDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getSites() throws Throwable {
    List<SiteDTO> siteDTOList = TestObjectsFactory.gimme(10, SiteDTO.class);
    Page<SiteDTO> sitePage = new PageImpl<>(siteDTOList);
    SiteDTO first = siteDTOList.get(0);
    when(siteService.getSites(
            anyLong(),
            any(Pageable.class),
            any(Optional.class),
            any(Optional.class),
            any(Optional.class),
            any(Optional.class)))
        .thenReturn(sitePage);
    mockMvc
        .perform(get("/v1/sellers/1/sites"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pId", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }

  @Test
  void getSitesWithLimitedFetch() throws Throwable {
    List<SiteDTO> siteDTOList = TestObjectsFactory.gimme(10, SiteDTO.class);
    Page<SiteDTO> sitePage = new PageImpl<>(siteDTOList);
    SiteDTO first = siteDTOList.get(0);
    when(siteService.getSites(
            anyLong(),
            any(Pageable.class),
            any(Optional.class),
            any(Optional.class),
            any(Optional.class),
            any(Optional.class)))
        .thenReturn(sitePage);
    mockMvc
        .perform(get("/v1/sellers/1/sites?fetch=limited"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("content.[0].pId", is(first.getPid())))
        .andExpect(jsonPath("content.[0].name", is(first.getName())));
  }

  @ParameterizedTest
  @CsvSource({
    "/v1/sellers/123/sites?minimal=true,''",
    "/v1/sellers/123/sites?minimal=true&qt=name,name"
  })
  void shouldGetSellerSitesMinimalData(String url, String qt) throws Exception {
    // given
    var sellerPid = 123L;
    SiteDTO returnedSite =
        SiteDTOMapper.MAPPER.map(
            new SiteView(1L, "site name", Status.ACTIVE, "example.com", "Example"));
    var pagedSites = new PageImpl<>(List.of(returnedSite));
    when(siteService.getSiteMinimalData(eq(sellerPid), eq(qt), any(Pageable.class)))
        .thenReturn(pagedSites);

    // when & then
    mockMvc
        .perform((get(url)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].pid", is(1)))
        .andExpect(jsonPath("$.content[0].url", is("example.com")))
        .andExpect(jsonPath("$.content[0].companyName", is("Example")))
        .andExpect(jsonPath("$.content[0].name", is("site name")));
  }
}

package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.repository.CompanyViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PublisherServiceImplTest {

  @InjectMocks private PublisherServiceImpl publisherService;
  @Mock private CompanyViewRepository companyViewRepository;
  @Mock private SiteRepository siteRepository;

  @Test
  void shouldReturnExchangePublishers() {
    // given
    String expectedName = "publisher1";
    String expectedGlobalAliasName = "globalAliasName1";
    CompanyView publisher =
        new CompanyView(1L, expectedName, null, false, null, expectedGlobalAliasName, null);
    given(companyViewRepository.findCompaniesByType(CompanyType.SELLER))
        .willReturn(List.of(publisher));
    // when
    List<com.nexage.app.dto.ExchangePublisherDTO> exchangePublishers =
        publisherService.getExchangePublishers();
    // then
    assertEquals(1, exchangePublishers.size());
    assertEquals(1L, exchangePublishers.get(0).getPid());
    assertEquals(expectedName, exchangePublishers.get(0).getName());
    assertEquals(expectedGlobalAliasName, exchangePublishers.get(0).getGlobalAliasName());
  }

  @Test
  void shouldReturnAllSitesSummary() {
    // given
    SiteSummaryDTO sites =
        SiteSummaryDTO.builder()
            .pid(1L)
            .name("siteSummaryName1")
            .globalAliasName("globalAliasName")
            .sellerPid(2L)
            .build();
    given(siteRepository.findSummaryDtosWithStatusNotDeleted()).willReturn(List.of(sites));

    // when
    List<com.nexage.app.dto.ExchangeSiteDTO> exchangeSites = publisherService.getExchangeSites();

    // then
    assertEquals(1, exchangeSites.size());
    assertEquals(1L, exchangeSites.get(0).getPid());
    assertEquals("siteSummaryName1", exchangeSites.get(0).getName());
    assertEquals("globalAliasName", exchangeSites.get(0).getGlobalAliasName());
    assertEquals(2L, exchangeSites.get(0).getPublisherPid());
  }
}

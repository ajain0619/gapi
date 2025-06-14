package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.dto.CompanySearchSummaryDTO;
import com.nexage.admin.core.dto.SearchSummaryDTO;
import com.nexage.admin.core.dto.SiteSearchSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.projections.CompanySearchSummaryProjection;
import com.nexage.admin.core.projections.SearchSummaryProjection;
import com.nexage.admin.core.repository.CompanyRepository;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

  @Mock CompanyRepository companyRepository;

  @InjectMocks private SearchServiceImpl<SearchSummaryDTO> searchService;

  @Test
  void shouldFindCompanySearchDtosByTypeAndNamePrefix() {
    // given
    Company company = createCompany(CompanyType.BUYER);
    company.setName("Test");

    List<CompanySearchSummaryProjection> companySearchSummaryProjections = new ArrayList<>();
    CompanySearchSummaryProjection companySearchSummaryProjection =
        mock(CompanySearchSummaryProjection.class);
    given(companySearchSummaryProjection.getPid()).willReturn(company.getPid());
    given(companySearchSummaryProjection.getName()).willReturn("Test");
    given(companySearchSummaryProjection.getType()).willReturn(CompanyType.BUYER);
    companySearchSummaryProjections.add(companySearchSummaryProjection);

    when(companyRepository.findCompanySearchProjectionsByTypeAndNameStartingWith(
            CompanyType.BUYER, "T"))
        .thenReturn(companySearchSummaryProjections);

    // when
    List<SearchSummaryDTO> companySearchDtos =
        searchService.findCompanySearchDtosByTypeAndNamePrefix("T", CompanyType.BUYER);

    // then
    assertEquals(companySearchDtos.get(0).getName(), company.getName());
  }

  @Test
  void shouldFindSiteSearchSummaryDtosContaining() {
    // given
    SiteSearchSummaryDTO siteSearchSummaryDTO =
        new SiteSearchSummaryDTO(1L, "A", SearchSummaryDTO.Type.SITE, Status.ACTIVE, "A");

    List<SearchSummaryProjection> searchSummaryProjections = new ArrayList<>();
    SearchSummaryProjection searchSummaryProjection = mock(SearchSummaryProjection.class);
    given(searchSummaryProjection.getSitePid()).willReturn(1L);
    given(searchSummaryProjection.getSiteName()).willReturn("A");
    searchSummaryProjections.add(searchSummaryProjection);

    when(companyRepository.findSearchSummaryProjectionsContaining("A"))
        .thenReturn(searchSummaryProjections);

    // when
    List<SearchSummaryDTO> result = searchService.findSearchSummaryDtosContaining("A");

    // then
    assertEquals(result.get(0).getPid(), siteSearchSummaryDTO.getPid());
    assertEquals(result.get(0).getName(), siteSearchSummaryDTO.getName());
    assertEquals(result.get(0).getType(), siteSearchSummaryDTO.getType());
  }

  @Test
  void shouldFindCompanySearchSummaryDtosContaining() {
    // given
    CompanySearchSummaryDTO companySearchSummaryDTO =
        CompanySearchSummaryDTO.builder()
            .pid(1L)
            .name("B")
            .type(SearchSummaryDTO.Type.SELLER)
            .build();

    List<SearchSummaryProjection> searchSummaryProjections = new ArrayList<>();
    SearchSummaryProjection searchSummaryProjection = mock(SearchSummaryProjection.class);
    given(searchSummaryProjection.getSitePid()).willReturn(-1L);
    given(searchSummaryProjection.getCompanyPid()).willReturn(1);
    given(searchSummaryProjection.getCompanyName()).willReturn("B");
    searchSummaryProjections.add(searchSummaryProjection);

    when(companyRepository.findSearchSummaryProjectionsContaining("B"))
        .thenReturn(searchSummaryProjections);

    // when
    List<SearchSummaryDTO> result = searchService.findSearchSummaryDtosContaining("B");

    // then
    assertEquals(result.get(0).getPid(), companySearchSummaryDTO.getPid());
    assertEquals(result.get(0).getName(), companySearchSummaryDTO.getName());
    assertEquals(result.get(0).getType(), companySearchSummaryDTO.getType());
  }
}

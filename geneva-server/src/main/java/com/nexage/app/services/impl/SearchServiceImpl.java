package com.nexage.app.services.impl;

import com.nexage.admin.core.dto.CompanySearchSummaryDTO;
import com.nexage.admin.core.dto.SearchSummaryDTO;
import com.nexage.admin.core.dto.SiteSearchSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.projections.CompanySearchSummaryProjection;
import com.nexage.admin.core.projections.SearchSummaryProjection;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.services.SearchService;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
@Transactional
@Service("searchService")
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class SearchServiceImpl<T extends SearchSummaryDTO> implements SearchService<T> {

  private final CompanyRepository companyRepository;

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<T> findCompanySearchDtosByTypeAndNamePrefix(String prefix, CompanyType type) {
    List<CompanySearchSummaryProjection> projections =
        companyRepository.findCompanySearchProjectionsByTypeAndNameStartingWith(type, prefix);

    return (List<T>)
        projections.stream()
            .map(
                (projection ->
                    CompanySearchSummaryDTO.builder()
                        .pid(projection.getPid())
                        .name(projection.getName())
                        .type(SearchSummaryDTO.Type.valueOf(projection.getType().toString()))
                        .currency(projection.getCurrency())
                        .defaultRtbProfilesEnabled(projection.getDefaultRtbProfilesEnabled())
                        .sellerSeatPid(projection.getSellerSeat_Pid())
                        .build()))
            .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<T> findSearchSummaryDtosContaining(String text) {
    List<SearchSummaryDTO> summaries = new ArrayList<>();
    List<SearchSummaryProjection> searchSummaries =
        companyRepository.findSearchSummaryProjectionsContaining(text);
    for (SearchSummaryProjection searchSummary : searchSummaries) {
      if (searchSummary.getSitePid() != -1) { // it is a site
        summaries.add(
            new SiteSearchSummaryDTO(
                searchSummary.getSitePid(),
                searchSummary.getSiteName(),
                SearchSummaryDTO.Type.SITE,
                Status.fromInt(searchSummary.getSiteStatus()),
                searchSummary.getCompanyName()));
      } else { // it is a seller
        CompanySearchSummaryDTO summary =
            CompanySearchSummaryDTO.builder()
                .pid(searchSummary.getCompanyPid())
                .name(searchSummary.getCompanyName())
                .type(SearchSummaryDTO.Type.SELLER)
                .build();
        summaries.add(summary);
      }
    }
    return (List<T>) summaries;
  }
}

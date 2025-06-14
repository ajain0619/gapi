package com.nexage.app.services.impl;

import com.google.common.collect.Lists;
import com.nexage.admin.core.repository.CompanyViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.dto.ExchangePublisherDTO;
import com.nexage.app.dto.ExchangeSiteDTO;
import com.nexage.app.services.PublisherService;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service("publisherService")
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class PublisherServiceImpl implements PublisherService {

  private final SiteRepository siteRepository;
  private final CompanyViewRepository companyViewRepository;

  public PublisherServiceImpl(
      SiteRepository siteRepository, CompanyViewRepository companyViewRepository) {
    this.siteRepository = siteRepository;
    this.companyViewRepository = companyViewRepository;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()")
  public List<ExchangePublisherDTO> getExchangePublishers() {
    return Lists.transform(
        companyViewRepository.findCompaniesByType(CompanyType.SELLER),
        input ->
            new ExchangePublisherDTO(input.getPid(), input.getName(), input.getGlobalAliasName()));
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or " + "@loginUserContext.isOcUserBuyer()")
  public List<ExchangeSiteDTO> getExchangeSites() {
    return siteRepository.findSummaryDtosWithStatusNotDeleted().stream()
        .map(
            input ->
                new ExchangeSiteDTO(
                    input.getPid(),
                    input.getName(),
                    input.getGlobalAliasName(),
                    input.getSellerPid()))
        .collect(Collectors.toList());
  }
}

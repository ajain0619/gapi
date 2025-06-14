package com.nexage.app.services.impl;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.specification.CompanySpecification;
import com.nexage.app.dto.seller.SellerDTO;
import com.nexage.app.mapper.PublisherEligibleBiddersDTOMapper;
import com.nexage.app.mapper.SellerDTOMapper;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.SellerDTOService;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class SellerDTOServiceImpl implements SellerDTOService {

  private final CompanyRepository companyRepository;
  private final CompanyService companyService;

  public SellerDTOServiceImpl(CompanyRepository companyRepository, CompanyService companyService) {
    this.companyRepository = companyRepository;
    this.companyService = companyService;
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or "
          + "@loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcUserBuyer() or "
          + "@loginUserContext.isOcAdminSeatHolder()")
  public Page<SellerDTO> findAll(
      final Set<String> qf, final String qt, final boolean isRtbEnabled, final Pageable pageable) {
    validateSearchParamRequest(qf, Company.class);
    return companyRepository
        .findAll(CompanySpecification.ofSellerTypeWith(qf, qt, isRtbEnabled), pageable)
        .map(SellerDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() "
          + "or @loginUserContext.isOcUserBuyer() or @loginUserContext.isOcAdminSeatHolder()")
  public SellerDTO findOne(Long sellerPid) {
    Company company = companyService.getCompany(sellerPid);
    SellerDTO sellerDTO = SellerDTOMapper.MAPPER.map(company);

    var eligibleBidders =
        company.getEligibleBidders().stream()
            .map(PublisherEligibleBiddersDTOMapper.MAPPER::map)
            .collect(Collectors.toSet());
    sellerDTO.setEligibleBidderGroups(eligibleBidders);
    return sellerDTO;
  }

  private void validateSearchParamRequest(Set<String> qf, Class classType) {
    if (!SearchRequestParamValidator.isValid(qf, classType)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }
}

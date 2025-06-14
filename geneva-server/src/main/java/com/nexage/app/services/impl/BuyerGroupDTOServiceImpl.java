package com.nexage.app.services.impl;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.specification.BuyerGroupSpecification;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.BuyerGroupDTOMapper;
import com.nexage.app.services.BuyerGroupDTOService;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.StaleStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
public class BuyerGroupDTOServiceImpl implements BuyerGroupDTOService {

  private final BuyerGroupRepository buyerGroupRepository;

  private final CompanyRepository companyRepository;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public Page<BuyerGroupDTO> findAll(
      final Long companyPid, final Set<String> qf, final String qt, final Pageable pageable) {
    validateSearchParamRequest(qf, BuyerGroupDTO.class);
    return buyerGroupRepository
        .findAll(
            BuyerGroupSpecification.withCompanyPidAndSearchCriteria(companyPid, qf, qt), pageable)
        .map(BuyerGroupDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public BuyerGroupDTO findOne(Long buyerGroupPid) {
    return buyerGroupRepository
        .findById(buyerGroupPid)
        .map(BuyerGroupDTOMapper.MAPPER::map)
        .orElseThrow(() -> new GenevaValidationException(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() OR @loginUserContext.isOcManagerBuyer()")
  public BuyerGroupDTO create(Long dspPid, BuyerGroupDTO buyerGroupDTO) {
    BuyerGroup buyerGroup = BuyerGroupDTOMapper.MAPPER.map(buyerGroupDTO);
    buyerGroup.setCompany(findCompany(dspPid));
    BuyerGroup created = buyerGroupRepository.save(buyerGroup);
    return BuyerGroupDTOMapper.MAPPER.map(created);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() OR @loginUserContext.isOcManagerBuyer()")
  public BuyerGroupDTO update(Long dspPid, Long buyerGroupPid, BuyerGroupDTO buyerGroupDTO) {
    if (dspPid == null || buyerGroupPid == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    if (!buyerGroupPid.equals(buyerGroupDTO.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    BuyerGroup entity =
        buyerGroupRepository
            .findById(buyerGroupPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST));
    if (!entity.getVersion().equals(buyerGroupDTO.getVersion())) {
      throw new StaleStateException("Buyer group has a different version of data");
    }
    if (!entity.getCompany().getPid().equals(buyerGroupDTO.getCompanyPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_MUST_NOT_BE_CHANGED);
    }
    final BuyerGroup buyerGroup = BuyerGroupDTOMapper.MAPPER.map(entity, buyerGroupDTO);
    return BuyerGroupDTOMapper.MAPPER.map(buyerGroupRepository.saveAndFlush(buyerGroup));
  }

  private void validateSearchParamRequest(Set<String> qf, Class<?> classType) {
    if (!SearchRequestParamValidator.isValid(qf, classType)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  private Company findCompany(Long companyPid) {
    return companyRepository
        .findById(companyPid)
        .orElseThrow(() -> new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND));
  }
}

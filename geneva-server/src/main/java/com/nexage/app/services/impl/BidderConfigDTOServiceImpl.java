package com.nexage.app.services.impl;

import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.specification.BidderConfigSpecification;
import com.nexage.app.dto.BidderConfigDTO;
import com.nexage.app.dto.BidderConfigDTOView;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.BidderConfigDTOMapper;
import com.nexage.app.mapper.BidderConfigDTOViewMapper;
import com.nexage.app.services.BidderConfigDTOService;
import com.nexage.app.services.BuyerService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/** {@inheritDoc} */
@Service
@PreAuthorize("@loginUserContext.isOcUserNexage()")
@RequiredArgsConstructor
public class BidderConfigDTOServiceImpl implements BidderConfigDTOService {

  private final BidderConfigDTOMapper bidderConfigDTOMapper;
  private final BuyerService buyerService;
  private final CompanyRepository companyRepository;
  private final BidderConfigRepository bidderConfigRepository;

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() OR @loginUserContext.isOcManagerBuyer()")
  public BidderConfigDTO create(Long dspPid, BidderConfigDTO bidderConfigDTO) {
    BidderConfig bidderConfig = bidderConfigDTOMapper.map(bidderConfigDTO);
    BidderConfig created = buyerService.createBidderConfig(dspPid, bidderConfig);
    return bidderConfigDTOMapper.map(created);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserBuyer()")
  public BidderConfigDTO get(Long dspPid, Long bidderConfigPid) {
    BidderConfig bidderConfig = buyerService.getBidderConfig(dspPid, bidderConfigPid);
    return bidderConfigDTOMapper.map(bidderConfig);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() OR @loginUserContext.isOcManagerBuyer()")
  public BidderConfigDTO update(
      Long dspPid, Long bidderConfigPid, BidderConfigDTO bidderConfigDTO) {
    BidderConfig bidderConfig = bidderConfigDTOMapper.map(bidderConfigDTO);
    BidderConfig updated = buyerService.updateBidderConfig(bidderConfigPid, bidderConfig, dspPid);
    return bidderConfigDTOMapper.map(updated);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() OR @loginUserContext.isOcUserBuyer()")
  public Page<BidderConfigDTOView> findAllBidderConfigs(
      final Long dspPid, final Set<String> qf, final String qt, final Pageable pageable) {

    if (!companyRepository.existsById(dspPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND);
    }
    return bidderConfigRepository
        .findAll(
            BidderConfigSpecification.withCompanyPidAndSearchCriteria(dspPid, qf, qt), pageable)
        .map(BidderConfigDTOViewMapper.MAPPER::map);
  }
}

package com.nexage.app.services.impl;

import com.nexage.admin.core.repository.DealBidderRepository;
import com.nexage.admin.core.sparta.jpa.model.DealBuyerView;
import com.nexage.admin.core.specification.GeneralSpecification;
import com.nexage.app.dto.deals.DealBidderDTO;
import com.nexage.app.mapper.DealBidderDTOMapper;
import com.nexage.app.services.DealBidderDTOService;
import com.nexage.app.util.validator.SearchRequestParamValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
public class DealBidderDTOServiceImpl implements DealBidderDTOService {

  private final DealBidderRepository dealBidderRepository;

  /** {@inheritDoc} */
  public Page<DealBidderDTO> findAll(Set<String> qf, String qt, Pageable pageable) {
    validateSearchParamRequest(qf, DealBuyerView.class);
    if (!CollectionUtils.isEmpty(qf)) {
      return dealBidderRepository
          .findAll(GeneralSpecification.withSearchCriteria(qf, qt), pageable)
          .map(DealBidderDTOMapper.MAPPER::map);
    }
    return dealBidderRepository.findAll(pageable).map(DealBidderDTOMapper.MAPPER::map);
  }

  private void validateSearchParamRequest(Set<String> qf, Class classType) {
    if (!SearchRequestParamValidator.isValid(qf, classType)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }
}

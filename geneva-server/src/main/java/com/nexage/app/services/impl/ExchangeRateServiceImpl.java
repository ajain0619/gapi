package com.nexage.app.services.impl;

import com.nexage.admin.core.model.ExchangeRate;
import com.nexage.admin.core.repository.ExchangeRateRepository;
import com.nexage.admin.core.specification.ExchangeRateSpecification;
import com.nexage.app.dto.ExchangeRateDTO;
import com.nexage.app.mapper.ExchangeRateDTOMapper;
import com.nexage.app.services.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

  private final ExchangeRateRepository exchangeRateRepository;
  private static final ExchangeRateDTOMapper MAPPER = ExchangeRateDTOMapper.MAPPER;

  /** {@inheritDoc} */
  public Page<ExchangeRateDTO> getAllExchangeRates(
      String qf, String qt, Pageable pageable, Boolean latest) {
    Specification<ExchangeRate> spec =
        ExchangeRateSpecification.withQueryFieldsAndSearchTermAndLatest(qf, qt, latest)
            .orElse(null);
    return exchangeRateRepository.findAll(spec, pageable).map(MAPPER::map);
  }
}

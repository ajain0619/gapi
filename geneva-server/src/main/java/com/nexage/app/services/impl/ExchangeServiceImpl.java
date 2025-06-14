package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BDRExchange;
import com.nexage.admin.core.repository.BdrExchangeRepository;
import com.nexage.app.services.ExchangeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service("exchangeService")
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatHolder()")
public class ExchangeServiceImpl implements ExchangeService {

  private final BdrExchangeRepository exchangeRepository;

  @Override
  public List<BDRExchange> findAll() {
    return exchangeRepository.findAll();
  }
}

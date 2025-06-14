package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.admin.core.repository.BdrExchangeCompanyRepository;
import com.nexage.app.dto.bdr.BdrExchangeCompanyDTO;
import com.nexage.app.mapper.BdrExchangeCompanyDTOMapper;
import com.nexage.app.services.ExchangeCompanyService;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service("exchangeCompanyService")
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatHolder()")
public class ExchangeCompanyServiceImpl implements ExchangeCompanyService {

  private static final BdrExchangeCompanyDTOMapper DTO_MAPPER = BdrExchangeCompanyDTOMapper.MAPPER;

  private final BdrExchangeCompanyRepository bdrExchangeCompanyRepository;
  private final EntityManager entityManager;

  @Override
  public BdrExchangeCompanyDTO create(BdrExchangeCompanyDTO exchangeCompanyDto) {
    BdrExchangeCompany exchangeCompany = DTO_MAPPER.map(exchangeCompanyDto);
    var created = bdrExchangeCompanyRepository.saveAndFlush(exchangeCompany);
    entityManager.refresh(created);
    return DTO_MAPPER.map(created);
  }

  @Override
  public List<BdrExchangeCompanyDTO> getAllForSeatholder(long sellerPid) {
    return bdrExchangeCompanyRepository.findByExchangeCompanyPk_Company_Pid(sellerPid).stream()
        .map(DTO_MAPPER::map)
        .collect(Collectors.toList());
  }

  @Override
  public BdrExchangeCompanyDTO update(BdrExchangeCompanyDTO exchangeCompanyDto) {
    BdrExchangeCompany exchangeCompany = DTO_MAPPER.map(exchangeCompanyDto);
    var updated = bdrExchangeCompanyRepository.saveAndFlush(exchangeCompany);
    entityManager.refresh(updated);
    return DTO_MAPPER.map(updated);
  }

  @Override
  public void delete(long seatholderPid, long exchangePid) {
    bdrExchangeCompanyRepository
        .deleteByExchangeCompanyPk_Company_PidAndExchangeCompanyPk_BidderExchange_Pid(
            seatholderPid, exchangePid);
  }
}

package com.nexage.app.services;

import com.nexage.app.dto.bdr.BdrExchangeCompanyDTO;
import java.util.List;

public interface ExchangeCompanyService {

  BdrExchangeCompanyDTO create(BdrExchangeCompanyDTO exchangeCompany);

  List<BdrExchangeCompanyDTO> getAllForSeatholder(long sellerPid);

  BdrExchangeCompanyDTO update(BdrExchangeCompanyDTO exchangeCompany);

  void delete(long seatholderPid, long exchangePid);
}

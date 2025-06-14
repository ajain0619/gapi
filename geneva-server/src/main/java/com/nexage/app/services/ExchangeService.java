package com.nexage.app.services;

import com.nexage.admin.core.bidder.model.BDRExchange;
import java.util.List;

public interface ExchangeService {

  List<BDRExchange> findAll();
}

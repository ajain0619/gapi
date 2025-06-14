package com.nexage.app.services;

import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBuyerDTO;
import java.util.List;

public interface DealService {

  DirectDealDTO createDeal(DirectDealDTO deal);

  DirectDealDTO updateDeal(long dealPid, DirectDealDTO deal);

  List<DealBuyerDTO> getAllBuyers();
}

package com.nexage.app.services;

import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import java.util.List;

public interface BidderConfigService {

  List<BidderSummaryDTO> getBidderSummaries();

  BidderConfig createDefaultBidderConfigForBuyer(Company company);

  BidderConfig saveFlushAndRefreshBidderConfig(BidderConfig bidderConfig);
}

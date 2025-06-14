package com.nexage.app.services;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import java.util.List;

public interface BDRAdvertiserService {

  List<BDRAdvertiser> getAdvertisersForCompany(long companyPid);
}

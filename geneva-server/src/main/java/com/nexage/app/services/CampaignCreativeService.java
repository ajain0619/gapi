package com.nexage.app.services;

import com.nexage.admin.core.model.Creative;
import java.util.Set;

public interface CampaignCreativeService {

  Set<Creative> getAllCreatives(long sellerPid, long campaignPid);
}

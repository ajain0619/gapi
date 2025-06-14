package com.nexage.app.services;

import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.publisher.PublisherSiteDTO;

public interface HbPartnerSiteService {

  void validateHbPartnerAssociations(Site siteDTO, PublisherSiteDTO publisherSite);
}

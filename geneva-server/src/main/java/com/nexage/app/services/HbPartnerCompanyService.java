package com.nexage.app.services;

import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.publisher.PublisherDTO;

public interface HbPartnerCompanyService {

  /**
   * Validate {@link HbPartnerCompanyService} incoming publisher hbpartner attributes.
   *
   * @param company Company.
   * @param publisher Publisher
   */
  void validateHbPartnerAssociations(Company company, PublisherDTO publisher);
}

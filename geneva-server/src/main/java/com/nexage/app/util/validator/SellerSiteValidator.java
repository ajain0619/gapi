package com.nexage.app.util.validator;

import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class SellerSiteValidator {

  private final SiteRepository siteRepository;

  public SellerSiteValidator(SiteRepository siteRepository) {
    this.siteRepository = siteRepository;
  }

  /** Validator contains validation logic if list of sites belong to a seller */
  public void validate(List<Long> sellerIds, List<Long> siteIds) {
    if (CollectionUtils.isNotEmpty(sellerIds) && CollectionUtils.isNotEmpty(siteIds)) {
      List<Long> sitePidsForSeller =
          new ArrayList<>(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(sellerIds));
      if (!sitePidsForSeller.containsAll(siteIds)) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_SITES_SELLERS_INVALID_COMBINATION);
      }
    }
  }
}

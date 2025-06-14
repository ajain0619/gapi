package com.nexage.app.services.impl;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.app.services.CompanyRtbProfileLibraryService;
import com.nexage.app.services.LimitService;
import com.nexage.app.services.RtbProfileLibrarySellerLimitService;
import com.ssp.geneva.common.base.annotation.Legacy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * This class was originally on {@link SellerLimitServiceImpl}. it has been decouple to deal with
 * certain circular dependency injection.
 */
@Legacy
@Service
@Qualifier("rtbProfileLibrarySellerLimitService")
public class RtbProfileLibrarySellerLimitServiceImpl
    implements RtbProfileLibrarySellerLimitService {

  private final LimitService limitService;
  private final SellerAttributesRepository sellerAttributesRepository;
  private final CompanyRtbProfileLibraryService companyRtbProfileLibraryService;

  @Autowired
  public RtbProfileLibrarySellerLimitServiceImpl(
      LimitService limitService,
      SellerAttributesRepository sellerAttributesRepository,
      CompanyRtbProfileLibraryService companyRtbProfileLibraryService) {
    this.limitService = limitService;
    this.sellerAttributesRepository = sellerAttributesRepository;
    this.companyRtbProfileLibraryService = companyRtbProfileLibraryService;
  }

  /** {@inheritDoc} */
  @Override
  public boolean canCreateBidderGroups(long publisher) {
    return !isLimitEnabled(publisher) || checkBidderLibrariesLimit(publisher) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean canCreateBlockGroups(long publisher) {
    return !isLimitEnabled(publisher) || checkBlockLibrariesLimit(publisher) > 0;
  }

  /** {@inheritDoc} */
  @Override
  public int checkBidderLibrariesLimit(long publisher) {
    int noOfBidderLibraries =
        companyRtbProfileLibraryService
            .getRTBProfileLibrariesForCompany(publisher, true, true, true)
            .size();

    Integer pubBidderLibrariesLimit = getPubBidderLibrariesLimit(publisher);
    if (pubBidderLibrariesLimit != null) {
      return pubBidderLibrariesLimit - noOfBidderLibraries;
    } else {
      return limitService.getGlobalBidderLibrariesLimit() - noOfBidderLibraries;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int checkBlockLibrariesLimit(long publisher) {
    int noOfBlockLibraries =
        companyRtbProfileLibraryService
            .getRTBProfileLibrariesForCompany(publisher, false, true, true)
            .size();

    Integer pubBlockLibrariesLimit = getPubBlockLibrariesLimit(publisher);
    if (pubBlockLibrariesLimit != null) {
      return pubBlockLibrariesLimit - noOfBlockLibraries;
    } else {
      return limitService.getGlobalBlockLibrariesLimit() - noOfBlockLibraries;
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isLimitEnabled(long sellerPid) {
    return sellerAttributesRepository
        .findById(sellerPid)
        .map(SellerAttributes::isLimitEnabled)
        .orElse(true);
  }

  private Integer getPubBidderLibrariesLimit(long sellerPid) {
    return sellerAttributesRepository
        .findById(sellerPid)
        .map(SellerAttributes::getBidderLibrariesLimit)
        .orElse(null);
  }

  private Integer getPubBlockLibrariesLimit(long sellerPid) {
    return sellerAttributesRepository
        .findById(sellerPid)
        .map(SellerAttributes::getBlockLibrariesLimit)
        .orElse(null);
  }
}

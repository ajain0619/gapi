package com.nexage.app.services.impl;

import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.specification.SiteSpecification;
import com.nexage.app.dto.SiteDealTermSummaryDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.tag.TagUpdateInfoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.HashUtils;
import com.nexage.app.util.PositionValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Methods used in reference to Seller's sites.
 *
 * <p>NOTE: Please do not add any new functionality to this class or its implementations. The
 * business concepts associated to the different operations handled by the following functions use
 * strategies, concepts, or frameworks considered deprecated. Be sure you reach to the core team
 * before considering designing or implementing new functionality under this class.
 */
@Log4j2
@Service
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()")
public class SellerSiteServiceImpl implements SellerSiteService {

  private static final int MAX_GROUP_NAMES = 5000;
  private static final int MAX_NAME_LENGTH = 40;
  private static final Pattern ILLEGAL_CHARS =
      Pattern.compile("[^0-9A-Za-z._-]"); // not a digit, lowercase letter,
  // period or dash

  private final UserContext userContext;
  private final SiteRepository siteRepository;
  private final SellerAttributesRepository sellerAttributesRepository;
  private final RTBProfileRepository rtbProfileRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final PositionValidator positionValidator;
  private final RTBProfileUtil rtbProfileUtil;
  private final EntityManager entityManager;

  public SellerSiteServiceImpl(
      UserContext userContext,
      CompanyRepository companyRepository,
      SiteRepository siteRepository,
      SellerAttributesRepository sellerAttributesRepository,
      UserRepository userRepository,
      RTBProfileRepository rtbProfileRepository,
      PositionValidator positionValidator,
      RTBProfileUtil rtbProfileUtil,
      EntityManager entityManager) {
    this.userContext = userContext;
    this.companyRepository = companyRepository;
    this.siteRepository = siteRepository;
    this.sellerAttributesRepository = sellerAttributesRepository;
    this.userRepository = userRepository;
    this.rtbProfileRepository = rtbProfileRepository;
    this.positionValidator = positionValidator;
    this.rtbProfileUtil = rtbProfileUtil;
    this.entityManager = entityManager;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.canAccessSite(#pid)")
  @PostAuthorize("@loginUserContext.doSameOrNexageAffiliation(returnObject.companyPid)")
  public Site getSite(Long pid) {
    Site site =
        siteRepository
            .findByPid(pid)
            .orElseThrow(
                () -> {
                  log.info("Site not found for Pid: [" + pid + "]");
                  return new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
                });
    rtbProfileUtil.populateRTBProfileLibraryPids(site);
    return site;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.canAccessSite(#sitePid)")
  @PostAuthorize("@loginUserContext.doSameOrNexageAffiliation(returnObject.companyPid)")
  public Site getValidatedSiteForPublisher(long sitePid, long publisherPid) {
    Site site = getSite(sitePid);

    if (publisherPid != site.getCompanyPid()) {
      log.error("Publisher PID for Site does not match the provided PID: [" + publisherPid + "]");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_PUBLISHER_ID);
    }

    return site;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()")
  public List<SiteSummaryDTO> getAllSitesSummary() {
    return siteRepository.findSummaryDtosWithStatusNotDeleted();
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())"
          + " and @loginUserContext.doSameOrNexageAffiliation(#companyPid)")
  public List<SiteSummaryDTO> getAllSitesSummaryByCompanyPid(Long companyPid) {
    if (!companyRepository.existsById(companyPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }
    return siteRepository.findSummaryDtosByCompanyPidWithStatusNotDeleted(companyPid);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public List<SiteSummaryDTO> getAllowedSitesForUser(Long userPid) {
    User user =
        userRepository
            .findById(userPid)
            .orElseThrow(
                () -> new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND));
    Set<Long> companyPids =
        user.getCompanies().stream()
            .mapToLong(Company::getPid)
            .collect(TreeSet::new, Set::add, Set::addAll);
    return siteRepository.findSummaryDtosByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
        userPid, companyPids);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#companyPid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()) )")
  public Site createSite(Long companyPid, Site site) {
    if (site.getCompanyPid() != null && !companyPid.equals(site.getCompanyPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    if (site.getDefaultRtbProfile() != null) {
      site.setDefaultRtbProfile(
          rtbProfileUtil.prepareDefaultRtbProfile(site.getDefaultRtbProfile()));
    }
    Company company =
        companyRepository
            .findById(companyPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_CREATE_SITE_COMPANY_MISSING));
    if (site.getCurrentDealTerm() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_TERM_REQUIRED_ON_CREATE);
    }
    site.setCompany(company);
    return siteRepository.save(site);
  }

  /** {@inheritDoc} */
  @Transactional
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()")
  @Override
  public List<SiteDealTermSummaryDTO> getAllSiteDealTerms(Long sellerPid) {
    if (sellerPid == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_EMPTY_SELLER_PID);
    }
    if (!userContext.doSameOrNexageAffiliation(sellerPid)) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
    List<Site> sites = getAllSitesByCompanyPid(sellerPid);
    List<SiteDealTermSummaryDTO> dealTerms = new LinkedList<>();
    for (Site s : sites) {
      SiteDealTerm dealTerm = s.getCurrentDealTerm();
      SiteDealTermSummaryDTO summary =
          new SiteDealTermSummaryDTO.Builder()
              .setRevShare(dealTerm.getNexageRevenueShare())
              .setRtbFee(dealTerm.getRtbFee())
              .setSitePid(dealTerm.getSitePid())
              .setSiteName(s.getName())
              .build();
      dealTerms.add(summary);
    }
    return dealTerms;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public List<Site> getAllSitesByCompanyPid(Long companyId) {
    return siteRepository.findAll(
        SiteSpecification.withSellerId(companyId)
            .and(Specification.not(SiteSpecification.withStatus(Status.DELETED))));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#toBeUpdated.companyPid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) )")
  public SiteUpdateInfoDTO processUpdateSiteRequest(Site toBeUpdated) {

    Set<TagUpdateInfoDTO> tagUpdateInfo = performUpdateSiteOperations(toBeUpdated);
    SiteUpdateInfoDTO.Builder builder = new SiteUpdateInfoDTO.Builder();
    builder.setTagUpdateInfo(tagUpdateInfo).setTxId(calcHashForSiteUpdate(tagUpdateInfo));
    return builder.build();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#toBeUpdated.companyPid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) )")
  public Set<TagUpdateInfoDTO> performUpdateSiteOperations(Site toBeUpdated) {
    // we first refresh the defaultReserve so it won't be null when following getSite(Long) call is
    // done
    if (toBeUpdated.getDefaultRtbProfile() != null) {
      toBeUpdated.setDefaultRtbProfile(
          rtbProfileUtil.prepareDefaultRtbProfile(toBeUpdated.getDefaultRtbProfile()));
    }
    Site site = getSite(toBeUpdated.getPid());
    Set<TagUpdateInfoDTO> tagUpdateInfo = new HashSet<>();

    if (toBeUpdated.getPositions() != null) {
      for (Position position : toBeUpdated.getPositions()) {
        positionValidator.validatePosition(position);
      }
    }

    // Attributes that gets never updated are replace with current db
    // values:
    // company, tags, rtb profiles
    toBeUpdated.setCompany(site.getCompany());
    toBeUpdated.setTags(site.getTags());
    toBeUpdated.setRtbProfiles(site.getRtbProfiles());
    toBeUpdated.setDealTerms(site.getDealTerms());

    SiteDealTerm dealTermUpdated = toBeUpdated.getCurrentDealTerm();
    toBeUpdated.setCurrentDealTerm(dealTermUpdated);
    if (dealTermUpdated != null && !dealTermUpdated.equals(site.getCurrentDealTerm())) {
      dealTermUpdated.setPid(null);
      dealTermUpdated.setSite(site);
      dealTermUpdated.setEffectiveDate(new Date());
      if (log.isDebugEnabled()) log.debug("There is a new deal term for site: " + dealTermUpdated);
      toBeUpdated.addToDealTerms(dealTermUpdated);
      tagUpdateInfo.addAll(adjustTagReservesForSiteUpdate(toBeUpdated));
    }

    toBeUpdated.setIncludePubName(site.getIncludePubName());
    toBeUpdated.setPubAliasId(site.getPubAliasId());
    toBeUpdated.setPubNameAlias(site.getPubNameAlias());
    toBeUpdated.setIncludeSiteName(site.getIncludeSiteName());
    toBeUpdated.setSiteAliasId(site.getSiteAliasId());
    toBeUpdated.setSiteNameAlias(site.getSiteNameAlias());

    validateImpressionGroup(toBeUpdated.getImpressionGroup());
    return tagUpdateInfo;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public String calcHashForSiteUpdate(Set<TagUpdateInfoDTO> tagUpdateInfo) {
    return new HashUtils().calculateHash(tagUpdateInfo.hashCode());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#site.companyPid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()) )")
  public Site updateSite(Site site) {
    processUpdateSiteRequest(site);
    alignSiteUpdateWithPositionAndTier(site);
    Site updated = siteRepository.saveAndFlush(site);
    updated.setCurrentDealTerm(null); // this forces to reload current
    // dealterm
    entityManager.refresh(updated);
    return updated;
  }

  @Override
  public void validateSiteNameUniqueness(Long sitePid, Long companyPid, String siteName) {
    if (siteRepository.existsByPidNotAndCompanyPidAndName(sitePid, companyPid, siteName)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_SITE_NAME);
    }
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) "
          + "or (@loginUserContext.canAccessSite(#sitePid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()) )")
  public void deleteSite(Long sitePid) {
    Site site = getSite(sitePid);
    siteRepository.delete(site);
  }

  /** {@inheritDoc} */
  @Transactional
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeller()")
  @Override
  public List<SiteDealTermSummaryDTO> updateSiteDealTermsToPubDefault(
      Long sellerPid, List<Long> sitePids) {
    return updateSiteDealTermsToPublisherDefault(sellerPid, sitePids);
  }

  /** {@inheritDoc} */
  @Transactional
  @PreAuthorize("@loginUserContext.isOcManagerYieldNexage()")
  @Override
  public List<SiteDealTermSummaryDTO> updateSiteDealTermsToPubDefaultByYieldManager(
      Long sellerPid, List<Long> sitePids) {
    return updateSiteDealTermsToPublisherDefault(sellerPid, sitePids);
  }

  /** {@inheritDoc} */
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public void assignRTBProfileToSite(Long sitePid, Long rtbProfilePid, Long ownerRTBProfilePid) {
    Site siteDTO = getSite(sitePid);
    RTBProfile rtbProfile = null;
    if (rtbProfilePid != null) {
      rtbProfile =
          rtbProfileRepository
              .findByDefaultRtbProfileOwnerCompanyPidAndPid(ownerRTBProfilePid, rtbProfilePid)
              .orElse(null);
    }
    siteDTO.setDefaultRtbProfile(rtbProfile);
    siteRepository.saveAndFlush(siteDTO);
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()")
  public Set<TagUpdateInfoDTO> adjustTagReservesForSiteUpdate(Site site) {
    Set<TagUpdateInfoDTO> tagUpdateInfo = new HashSet<>();
    if (site != null) {
      Set<Tag> tags = site.getTags();
      Set<RTBProfile> rtbProfiles = site.getRtbProfiles();
      Iterator<Tag> it = tags.iterator();
      while (it.hasNext()) {
        Tag tag = it.next();
        if (tag.isExchangeTag()
            && (tag.getRtbFeeOverride() == null || tag.getNexageRevenueShareOverride() == null)) {
          TagUpdateInfoDTO.Builder builder = new TagUpdateInfoDTO.Builder();

          buildTagUpdateInfo(tagUpdateInfo, builder, rtbProfiles, tag, site);
        }
      }
    }
    return tagUpdateInfo;
  }

  public void addUpdatedTierIfTagIsPresentProxy(Position position) {
    addUpdatedTierIfTagIsPresent(position);
  }

  private void sortTiers(List<Tier> inputTiers) {
    Collections.sort(
        inputTiers,
        (t1, t2) -> {
          if (t1.getLevel() < t2.getLevel()) return -1;
          if (t1.getLevel() > t2.getLevel()) return 1;
          return 0;
        });
  }

  private void addUpdatedTierIfTagIsPresent(Position position) {
    List<Tier> updatedTiers = new ArrayList<>();
    for (Tier tier : position.getTiers()) {
      if (!tier.getTags().isEmpty()) {
        tier.setPosition(position);
        updatedTiers.add(tier);
      }
    }
    sortTiers(updatedTiers);
    position.setTiers(updatedTiers);
    position.renumberTiers();
  }

  private void verifySitesAndSellerAttributes(Long sellerPid, List<Long> sitePids) {
    if (sellerPid == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_EMPTY_SELLER_PID);
    }
    if (!sellerAttributesRepository.existsBySellerPid(sellerPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_ATTRIBUTES_NOT_FOUND);
    }
    for (Long pid : sitePids) {
      if (pid == null) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
      }
      if (!userContext.canAccessSite(pid)) {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  private boolean shouldCreateNewDeal(SiteDealTerm current, SellerAttributes attribs) {
    BigDecimal sellerRevShare = attribs.getRevenueShare();
    BigDecimal sellerRtbFee = attribs.getRtbFee();
    return current == null
        || sellerRevShare == null
        || sellerRtbFee == null
        || !(sellerRevShare.equals(current.getNexageRevenueShare())
            && sellerRtbFee.equals(current.getRtbFee()));
  }

  private List<SiteDealTermSummaryDTO> updateSiteDealTermsToPublisherDefault(
      Long sellerPid, List<Long> sitePids) {
    List<SiteDealTermSummaryDTO> dealTerms = new LinkedList<>();
    verifySitesAndSellerAttributes(sellerPid, sitePids);
    List<Site> sites = getSites(sellerPid, sitePids);
    Date date = Calendar.getInstance().getTime();
    Company company =
        companyRepository
            .findById(sellerPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_CREATE_SITE_COMPANY_MISSING));
    SellerAttributes currentSellerAttributes = company.getSellerAttributes();

    for (Site site : sites) {
      SiteDealTerm currentSiteDealTerm = site.getCurrentDealTerm();
      site.setCurrentDealTerm(currentSiteDealTerm);
      // We only want to create new deal term if either the seller rev share or RTB fee is different
      // from those
      // of the current deal term
      if (shouldCreateNewDeal(currentSiteDealTerm, currentSellerAttributes)) {
        SiteDealTerm newDealTerm = new SiteDealTerm();
        newDealTerm.setRtbFee(currentSellerAttributes.getRtbFee());
        newDealTerm.setNexageRevenueShare(currentSellerAttributes.getRevenueShare());
        newDealTerm.setRevenueMode(SiteDealTerm.RevenueMode.REV_SHARE);
        newDealTerm.setSite(site);
        newDealTerm.setEffectiveDate(date);

        site.addToDealTerms(newDealTerm);

        siteRepository.save(site);

        SiteDealTermSummaryDTO summary =
            new SiteDealTermSummaryDTO.Builder()
                .setRevShare(newDealTerm.getNexageRevenueShare())
                .setRtbFee(newDealTerm.getRtbFee())
                .setSiteName(site.getName())
                .setSitePid(site.getPid())
                .build();
        dealTerms.add(summary);
      }
    }
    return dealTerms;
  }

  private List<Site> getSites(Long sellerPid, List<Long> sitePids) {
    List<Site> sites = new LinkedList<>();
    for (Long pid : sitePids) {
      Site site =
          siteRepository
              .findByPid(pid)
              .orElseThrow(
                  () -> new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND));
      if (!site.getCompanyPid().equals(sellerPid)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_PIDS_MISMATCH);
      }
      sites.add(site);
    }
    return sites;
  }

  private void validateImpressionGroup(Site.ImpressionGroup impGroup) {
    if (!impGroup.isEnabled() && !impGroup.getGroups().isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_IMP_GROUP_SHOULD_BE_ENABLED);
    }
    if (impGroup.getGroups().size() > MAX_GROUP_NAMES) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TOO_MANY_IMP_GROUPS);
    }
    for (String name : impGroup.getGroups()) {
      if (name.length() > MAX_NAME_LENGTH) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_IMP_GRP_TOO_LONG);
      }
      Matcher matcher = ILLEGAL_CHARS.matcher(name);
      if (matcher.find()) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_IMP_GRP_ILLEGAL_CHAR);
      }
    }
  }

  private void alignSiteUpdateWithPositionAndTier(Site site) {
    if (site.getPositions() != null) {
      for (Position position : site.getPositions()) {
        position.setSite(site);
        if (position.getTiers() != null) {
          addUpdatedTierIfTagIsPresentProxy(position);
        }
      }
    }
  }

  private void buildTagUpdateInfo(
      Set<TagUpdateInfoDTO> tagUpdateInfo,
      TagUpdateInfoDTO.Builder builder,
      Set<RTBProfile> rtbProfiles,
      Tag tag,
      Site site) {
    for (RTBProfile rtbProfile : rtbProfiles) {
      if (tag.getPrimaryId() != null
          && tag.getPrimaryId().equals(rtbProfile.getExchangeSiteTagId())) {
        TagUpdateInfoDTO tempInfo =
            builder
                .setTagName(tag.getName())
                .setPreviousGrossFloor(rtbProfile.getDefaultReserve())
                .setPreviousLowFloor(rtbProfile.getLowReserve())
                .build();
        rtbProfileUtil.adjustReservesWithDealTerm(site, tag, rtbProfile);

        if (tempInfo.getPreviousLowFloor() != null && rtbProfile.getLowReserve() != null) {
          comparePreviousGrossAndLowFloorToRTBProfileDefaultAndLowReserve(
              tempInfo, rtbProfile, builder, tagUpdateInfo);
        } else if ((tempInfo.getPreviousLowFloor() == null && rtbProfile.getLowReserve() != null)
            || (tempInfo.getPreviousLowFloor() != null && rtbProfile.getLowReserve() == null)
            || tempInfo.getPreviousGrossFloor().compareTo(rtbProfile.getDefaultReserve()) != 0) {

          builder
              .setNewGrossFloor(rtbProfile.getDefaultReserve())
              .setNewLowFloor(rtbProfile.getLowReserve());
          tagUpdateInfo.add(builder.build());
        }
        break;
      }
    }
  }

  private void comparePreviousGrossAndLowFloorToRTBProfileDefaultAndLowReserve(
      TagUpdateInfoDTO tempInfo,
      RTBProfile rtbProfile,
      TagUpdateInfoDTO.Builder builder,
      Set<TagUpdateInfoDTO> tagUpdateInfo) {
    if (tempInfo.getPreviousGrossFloor().compareTo(rtbProfile.getDefaultReserve()) != 0
        || tempInfo.getPreviousLowFloor().compareTo(rtbProfile.getLowReserve()) != 0) {
      builder
          .setNewGrossFloor(rtbProfile.getDefaultReserve())
          .setNewLowFloor(rtbProfile.getLowReserve());
      tagUpdateInfo.add(builder.build());
    }
  }
}

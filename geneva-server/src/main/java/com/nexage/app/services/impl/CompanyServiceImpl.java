package com.nexage.app.services.impl;

import static com.nexage.admin.core.specification.CompanySpecification.withNameLike;
import static com.nexage.admin.core.specification.CompanySpecification.withType;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.admin.core.dto.BuyerMetadataDTO;
import com.nexage.admin.core.dto.SeatHolderMetadataDTO;
import com.nexage.admin.core.dto.SellerMetadataDTO;
import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.enums.CrsReviewStatusBlock;
import com.nexage.admin.core.enums.CrsSecureStatusBlock;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Advertiser;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Company.EstimateTimeRemaining;
import com.nexage.admin.core.model.Company.EstimateTimeRemaining.ETR;
import com.nexage.admin.core.model.Creative;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.projections.AllSeatHolderMetaDataReturnProjection;
import com.nexage.admin.core.projections.BuyerMetaDataForCompanyProjection;
import com.nexage.admin.core.projections.SellerMetaDataForCompanyProjection;
import com.nexage.admin.core.repository.AdvertiserRepository;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import com.nexage.admin.core.repository.BdrExchangeCompanyRepository;
import com.nexage.admin.core.repository.BdrExchangeRepository;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.CampaignRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.CreativeRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.nexage.admin.core.specification.UserSpecification;
import com.nexage.admin.dw.dashboard.dao.DashboardDao;
import com.nexage.admin.dw.dashboard.model.BuyerKeyMetrics;
import com.nexage.admin.dw.util.DateUtil;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.queue.model.event.SyncEvent;
import com.nexage.app.queue.producer.CompanySyncProducer;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BidderConfigService;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.SeatHolderService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.validator.CompanyValidator;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service("companyService")
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or "
        + "@loginUserContext.isOcUserBuyer() or @loginUserContext.isOcUserSeatHolder()")
public class CompanyServiceImpl implements CompanyService {

  private static final String DEFAULT_CURRENCY_CODE = "USD";

  @Value("${video.human.prebid.samplingrate.default}")
  private Integer defaultHumanPrebidSampleRate;

  @Value("${video.human.postbid.samplingrate.default}")
  private Integer defaultHumanPostbidSampleRate;

  private final UserContext userContext;
  private final SellerSeatRepository sellerSeatRepository;
  private final BdrInsertionOrderRepository bdrInsertionOrderRepository;
  private final BDRAdvertiserRepository bdrAdvertiserRepository;
  private final BdrExchangeCompanyRepository bdrExchangeCompanyRepository;
  private final BdrExchangeRepository bdrExchangeRepository;
  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final UserRestrictedSiteRepository userRestrictedSiteRepository;
  private final SiteRepository siteRepository;
  private final CreativeRepository creativeRepository;
  private final CampaignRepository campaignRepository;
  private final BidderConfigRepository bidderConfigRepository;
  private final BidderConfigService bidderConfigService;
  private final SellerAttributesRepository sellerAttributesRepository;
  private final RTBProfileRepository rtbProfileRepository;
  private final SeatHolderService seatHolderService;
  private final DashboardDao dashboardDao;
  private final TransparencyService transparencyService;
  private final SellerSiteService sellerSiteService;
  private final RevenueShareUpdateValidator revenueShareUpdateValidator;
  private final RTBProfileUtil rtbProfileUtil;
  private final CompanySyncProducer producer;
  private final AdvertiserRepository advertiserRepository;
  private final EntityManager entityManager;
  private final CompanyValidator companyValidator;

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public Company createCompany(Company company) {
    if (company.getType() == CompanyType.BUYER) {
      if (company.getCpiConversionNoticeUrl() != null && !company.isCpiTrackingEnabled()) {
        company.setCpiConversionNoticeUrl(null);
      }

      if (company.getExternalAdVerificationEnabled() == null) {
        company.setExternalAdVerificationEnabled(true);
      }
    }

    companyValidator.validateCreateCompany(company, DEFAULT_CURRENCY_CODE);

    populateDefaultRtbProfileFields(company);

    assignSellerSeatIfApplicable(company);

    createCompanySellerAttributes(company);

    Company created = companyRepository.save(company);
    companyRepository.flush();
    entityManager.refresh(created);

    updateUserCompanyRelationForSellerSeat(created);

    // Add user contact and reporting api
    addContact(created, company.getContactUserPid());
    Company updated = companyRepository.save(created);
    setTransientFields(company, updated);

    if (CompanyType.SELLER == updated.getType() && updated.isAdServingEnabled()) {
      Advertiser houseAdvertiser = Advertiser.createHouseAdvertiser(updated.getPid());
      advertiserRepository.save(houseAdvertiser);
      if (log.isInfoEnabled()) {
        log.info("Automatically created HOUSE advertiser for company (pid): " + updated.getPid());
      }
    }

    if (CompanyType.BUYER == updated.getType() && updated.isRtbEnabled()) {
      bidderConfigRepository.save(bidderConfigService.createDefaultBidderConfigForBuyer(updated));
    }

    if (company.getType() == CompanyType.SEATHOLDER) {
      var exchangeCompany = createBdrExchangeCompany(created);
      bdrExchangeCompanyRepository.save(exchangeCompany);

      if (company.getCredit() != null) {
        seatHolderService.addCreditToSeatHolder(created, company.getCredit());
      }
    }
    setSellerSeatPid(updated);
    postProcessCompany(updated);
    producer.publishEvent(SyncEvent.createOf(updated));
    return updated;
  }

  private void createCompanySellerAttributes(Company company) {
    if (company.getSellerAttributes() != null) {
      validateSellerAttributesHbFields(company, null);
      SellerAttributes sellerAttributes = company.getSellerAttributes();
      // set default value as RealName for new companies
      if (sellerAttributes.getIncludePubName() == null) {
        sellerAttributes.setIncludePubName(TransparencyMode.RealName.asInt());
      }
      validateTransparencySettingsMgmtFlag(sellerAttributes);
      if (sellerAttributes.getPubAliasId() != null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_TRANSPARENCY_PUBLISHER_ID_ALIAS_ERROR);
      }
      transparencyService.regenerateAliasIdForBlindAndAlias(sellerAttributes);
      transparencyService.validateTransparencySettings(sellerAttributes);

      checkSmartQPSEnabledEditPermission(company.getSellerAttributes(), null);
      checkDynamicFloorEnabledEditPermission(
          company.getSellerAttributes().getDynamicFloorEnabled(), null);
      setDefaultHumanSampleRatesIfNull(sellerAttributes);

      sellerAttributes.setSeller(company);
    }
  }

  private void setDefaultHumanSampleRatesIfNull(SellerAttributes sellerAttributes) {
    if (sellerAttributes.getHumanPrebidSampleRate() == null) {
      sellerAttributes.setHumanPrebidSampleRate(defaultHumanPrebidSampleRate);
    }
    if (sellerAttributes.getHumanPostbidSampleRate() == null) {
      sellerAttributes.setHumanPostbidSampleRate(defaultHumanPostbidSampleRate);
    }
  }

  private void updateRevenueShareDetails(Company company, Company updated) {
    if (revenueShareUpdateValidator.isRevenueShareUpdated(company, updated)) {
      if (userContext.isOcManagerYieldNexage()) {
        company.getSellerAttributes().setRtbFee(updated.getSellerAttributes().getRtbFee());
        company
            .getSellerAttributes()
            .setRevenueShare(updated.getSellerAttributes().getRevenueShare());
      } else {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  private void updateSspDealRevenueShareDetails(Company company, Company updated) {
    var originalSellerAttributes = company.getSellerAttributes();
    var updatedSellerAttributes = updated.getSellerAttributes();
    if (revenueShareUpdateValidator.isRevenueShareUpdated(
        originalSellerAttributes.getSspDealRevShare(),
        updatedSellerAttributes.getSspDealRevShare(),
        null,
        null)) {
      if (userContext.isOcManagerYieldNexage()) {
        company
            .getSellerAttributes()
            .setSspDealRevShare(updated.getSellerAttributes().getSspDealRevShare());
      } else {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  private void updateJointDealRevenueShareDetails(Company company, Company updated) {
    var originalSellerAttributes = company.getSellerAttributes();
    var updatedSellerAttributes = updated.getSellerAttributes();
    if (revenueShareUpdateValidator.isRevenueShareUpdated(
        originalSellerAttributes.getJointDealRevShare(),
        updatedSellerAttributes.getJointDealRevShare(),
        null,
        null)) {
      if (userContext.isOcManagerYieldNexage()) {
        company
            .getSellerAttributes()
            .setJointDealRevShare(updated.getSellerAttributes().getJointDealRevShare());
      } else {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  private void updateSellerDealRevenueShareDetails(Company company, Company updated) {
    var originalSellerAttributes = company.getSellerAttributes();
    var updatedSellerAttributes = updated.getSellerAttributes();
    if (revenueShareUpdateValidator.isRevenueShareUpdated(
        originalSellerAttributes.getSellerDealRevShare(),
        updatedSellerAttributes.getSellerDealRevShare(),
        null,
        null)) {
      if (userContext.isOcManagerYieldNexage()) {
        company
            .getSellerAttributes()
            .setJointDealRevShare(updated.getSellerAttributes().getJointDealRevShare());
      } else {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcUserBuyer() or @loginUserContext.isOcAdminSeatHolder()")
  public List<Company> getAllCompaniesByType(
      CompanyType type, Set<String> queryFields, String queryTerm) {
    if (userContext.isNexageUser()) {
      Specification<Company> companySpecification = withNameLike(queryTerm).and(withType(type));
      if (isNotEmpty(queryFields)) {
        return findCompanies(companySpecification);
      }
      return findCompaniesByType(type);
    } else if (type == CompanyType.SELLER || type == CompanyType.BUYER) {
      return findCompaniesByPids(userContext.getCompanyPids());
    }
    return emptyList();
  }

  private List<Company> findCompaniesByType(CompanyType type) {
    List<Company> companiesByType = companyRepository.findByType(type);
    addCompanyDetailsForType(type, companiesByType);
    companiesByType.forEach(this::postProcessCompany);
    return companiesByType;
  }

  private List<Company> findCompaniesByPids(Set<Long> companyPids) {
    List<Company> companiesByPids = companyRepository.findByPidIn(companyPids);
    companiesByPids.forEach(this::addCompanyDetails);
    return companiesByPids;
  }

  private List<Company> findCompanies(Specification<Company> companySpecification) {
    List<Company> companiesFindAll = companyRepository.findAll(companySpecification);
    companiesFindAll.forEach(this::postProcessCompany);
    return companiesFindAll;
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcAdminNexage() or "
          + "@loginUserContext.isOcAdminBuyer() or @loginUserContext.isOcAdminSeatHolder()")
  public List<Company> getAllCompanies() {
    if (!userContext.isNexageUser()) {
      return findCompaniesByPids(userContext.getCompanyPids());
    }
    Predicate<CompanyType> skipNonNexageCompaniesForNexageAdmin =
        type -> type == CompanyType.NEXAGE || !userContext.isOcAdminNexage();

    return Arrays.stream(CompanyType.values())
        .filter(skipNonNexageCompaniesForNexageAdmin)
        .map(this::findCompaniesByType)
        .collect(ArrayList::new, List::addAll, List::addAll);
  }

  private void postProcessCompany(Company company) {
    Hibernate.initialize(company.getInventoryAttributeValues());
    Hibernate.initialize(company.getCredits());
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or "
          + "@loginUserContext.isOcUserBuyer() or @loginUserContext.isOcUserSeatHolder() or "
          + "@loginUserContext.isOcApiSeller() or @loginUserContext.isOcApiIIQ()")
  public Company getCompany(long companyPId) {
    if (userContext.doSameOrNexageAffiliation(companyPId)) {
      Company company = findByPid(companyPId);
      addCompanyDetails(company);
      setSellerSeatPid(company);
      return company;
    } else {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcAdminBuyer() or @loginUserContext.isOcAdminSeatHolder() or "
          + "@loginUserContext.isOcManagerSeller() or @loginUserContext.isOcManagerSmartexNexage()")
  public Company updateCompanyAndReload(Company company) {
    Company companyUpdated = updateCompany(company);
    Company reloaded = companyRepository.saveAndFlush(companyUpdated);
    entityManager.refresh(companyUpdated);
    setSellerSeatPid(reloaded);
    return reloaded;
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcAdminBuyer() or @loginUserContext.isOcAdminSeatHolder() or "
          + "@loginUserContext.isOcManagerSeller() or @loginUserContext.isOcManagerSmartexNexage()")
  public Company updateCompany(Company company) {
    // Read all the attributes and relations not passed to the client from database and add it to
    // the company before update
    Company companyInDb = findByPid(company.getPid());

    if (company.getType() == CompanyType.BUYER) {
      if (company.getCpiConversionNoticeUrl() != null && !company.isCpiTrackingEnabled()) {
        company.setCpiConversionNoticeUrl(null);
      }

      if (company.getExternalAdVerificationEnabled() == null) {
        company.setExternalAdVerificationEnabled(companyInDb.getExternalAdVerificationEnabled());
      }
    }

    if (!userContext.doSameOrNexageAffiliation(company.getPid())) {
      log.error("Cannot update company that a user is not affiliated to");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    if (!Objects.equals(company.getRegionId(), companyInDb.getRegionId())
        && !userContext.isNexageAdminOrManager()) {
      log.error(
          "Non-nexage user({}) try to change company({}) region.",
          userContext.getPid(),
          company.getPid());
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    companyValidator.validateUpdateCompany(company, companyInDb);

    updateCompanySellerAttributes(company, companyInDb);

    populateDefaultRtbProfileFields(company);
    addNotSerializedDataFromDB(companyInDb, company);

    for (SellerEligibleBidders bidders : company.getEligibleBidders()) {
      bidders.setPublisher(company);
    }

    // retrieve initial seller seat from db
    SellerSeat initialSellerSeat = getInitialSellerSeatFromDb(company);

    assignSellerSeatIfApplicable(company);

    if (company.getDhReportingId() == null) {
      company.setDhReportingId(companyInDb.getDhReportingId());
    }

    if (company.getFraudDetectionJavascriptEnabled() == null) {
      company.setFraudDetectionJavascriptEnabled(companyInDb.getFraudDetectionJavascriptEnabled());
    }

    Company updated = companyRepository.save(company);
    setTransientFields(company, updated);

    updateSellerSeat(updated, initialSellerSeat);

    checkUpdateCompanyHouseAdvertiser(updated, company);

    if (CompanyType.BUYER == updated.getType()
        && updated.isRtbEnabled()
        && bidderConfigRepository.findByCompanyPid(updated.getPid()).isEmpty()) {
      bidderConfigRepository.save(bidderConfigService.createDefaultBidderConfigForBuyer(updated));
    }
    addCompanyDetails(updated);

    setSellerSeatPid(updated);
    return updated;
  }

  private void updateSellerSeat(Company updated, SellerSeat initialSellerSeat) {
    if (initialSellerSeat != null) {
      updateUserCompanyRelationForSellerSeatWhenInitialSellerSeatExists(
          updated, initialSellerSeat.getPid());
    } else {
      updateUserCompanyRelationForSellerSeat(updated);
    }
  }

  private void updateCompanySellerAttributes(Company company, Company companyInDb) {
    boolean revShareUpdated = false;
    boolean updatePfo = false;

    if (company.getSellerAttributes() != null) {
      SellerAttributes attributes = company.getSellerAttributes();
      SellerAttributes dbAttributes = companyInDb.getSellerAttributes();

      transparencyService.validateTransparencyMgmtChangingByRole(attributes, dbAttributes);
      validateTransparencySettingsMgmtFlag(attributes);
      if (dbAttributes == null) {
        checkSmartQPSEnabledEditPermission(attributes, null);
        updateCompanySellerAttributesDBAttributesNull(attributes, companyInDb);

        if (attributes.getRevenueShare() != null || attributes.getRtbFee() != null) {
          revShareUpdated = true;
        }
      } else {
        // check if revenue share is updated
        if (ObjectUtils.compare(attributes.getRevenueShare(), dbAttributes.getRevenueShare()) != 0
            || ObjectUtils.compare(attributes.getRtbFee(), dbAttributes.getRtbFee()) != 0) {
          revShareUpdated = true;
        }
        validateSellerAttributesHbFields(companyInDb, company);
        checkSmartQPSEnabledEditPermission(dbAttributes, attributes);
        checkDynamicFloorEnabledEditPermission(
            dbAttributes.getDynamicFloorEnabled(), attributes.getDynamicFloorEnabled());
        updateRevenueShareDetails(companyInDb, company);

        setUpdateCompanyTransparencyMode(dbAttributes, attributes);

        updateSspDealRevenueShareDetails(companyInDb, company);
        updateJointDealRevenueShareDetails(companyInDb, company);
        updateSellerDealRevenueShareDetails(companyInDb, company);

        updateCompanyToNewAttributes(company, attributes, dbAttributes);

        if (!Objects.equals(
            attributes.isPfoEnabled(), company.getSellerAttributes().isPfoEnabled())) {
          company.getSellerAttributes().setPfoEnabled(attributes.isPfoEnabled());
          updatePfo = true;
        }
      }
    }

    checkUpdatedRevenueShareAndPFO(company, revShareUpdated, updatePfo);
  }

  private void checkUpdatedRevenueShareAndPFO(
      Company company, boolean revShareUpdated, boolean updatePfo) {
    if (revShareUpdated) {
      updateSitesRevenueShare(company.getPid());
    }

    if (updatePfo) {
      togglePfo(company.getPid(), company.getSellerAttributes().isPfoEnabled());
    }
  }

  private void setUpdateCompanyTransparencyMode(
      SellerAttributes dbAttributes, SellerAttributes attributes) {
    TransparencyMode mode = TransparencyMode.fromInt(attributes.getIncludePubName());
    if ((TransparencyMode.Aliases.equals(mode) || TransparencyMode.None.equals(mode))) {
      if (attributes.getPubAliasId() == null) {
        dbAttributes.setPubAliasId(transparencyService.generateIdAlias());
        attributes.setPubAliasId(dbAttributes.getPubAliasId());
      }
    } else {
      // clean old value
      dbAttributes.setPubAliasId(null);
    }
    transparencyService.validateTransparencySettings(attributes);
  }

  private void updateCompanySellerAttributesDBAttributesNull(
      SellerAttributes attributes, Company companyInDb) {
    transparencyService.regenerateAliasIdForBlindAndAlias(attributes);
    transparencyService.validateTransparencySettings(attributes);
    checkDynamicFloorEnabledEditPermission(attributes.getDynamicFloorEnabled(), null);
    setDefaultHumanSampleRatesIfNull(attributes);
    attributes.setSeller(companyInDb);
    // workaround for MX-9234 and Hibernate 3.6.10
    sellerAttributesRepository.save(attributes);
    companyRepository.flush();
    entityManager.refresh(companyInDb);
  }

  private void updateCompanyToNewAttributes(
      Company company, SellerAttributes attributes, SellerAttributes dbAttributes) {
    company.setSellerAttributes(dbAttributes);
    setCompanySellerAttributes(company, attributes);

    if (attributes.getPublisherDataProtectionRole() != null) {
      company
          .getSellerAttributes()
          .setPublisherDataProtectionRole(attributes.getPublisherDataProtectionRole());
    }

    company.getSellerAttributes().setSmartQPSEnabled(attributes.getSmartQPSEnabled());

    if (attributes.getSellerDomainVerificationAuthLevel() != null) {
      company
          .getSellerAttributes()
          .setSellerDomainVerificationAuthLevel(attributes.getSellerDomainVerificationAuthLevel());
    }

    Optional.ofNullable(attributes.getHumanPrebidSampleRate())
        .ifPresent(
            humanPrebidSampleRate ->
                company.getSellerAttributes().setHumanPrebidSampleRate(humanPrebidSampleRate));

    Optional.ofNullable(attributes.getHumanPostbidSampleRate())
        .ifPresent(
            humanPostbidSampleRate ->
                company.getSellerAttributes().setHumanPostbidSampleRate(humanPostbidSampleRate));
  }

  private void setCompanySellerAttributes(Company company, SellerAttributes attributes) {
    company.getSellerAttributes().setRevenueShare(attributes.getRevenueShare());
    company.getSellerAttributes().setRtbFee(attributes.getRtbFee());
    company.getSellerAttributes().setHbThrottleEnabled(attributes.isHbThrottleEnabled());
    company.getSellerAttributes().setHbThrottlePercentage(attributes.getHbThrottlePercentage());
    company.getSellerAttributes().setSiteLimit(attributes.getSiteLimit());
    company.getSellerAttributes().setPositionsPerSiteLimit(attributes.getPositionsPerSiteLimit());
    company.getSellerAttributes().setTagsPerPositionLimit(attributes.getTagsPerPositionLimit());
    company.getSellerAttributes().setCampaignsLimit(attributes.getCampaignsLimit());
    company
        .getSellerAttributes()
        .setCreativesPerCampaignLimit(attributes.getCreativesPerCampaignLimit());
    company.getSellerAttributes().setBidderLibrariesLimit(attributes.getBidderLibrariesLimit());
    company.getSellerAttributes().setBlockLibrariesLimit(attributes.getBlockLibrariesLimit());
    company.getSellerAttributes().setUserLimit(attributes.getUserLimit());
    company.getSellerAttributes().setLimitEnabled(attributes.isLimitEnabled());
    company.getSellerAttributes().setHbPricePreference(attributes.getHbPricePreference());
    company
        .getSellerAttributes()
        .setVideoUseInboundSiteOrApp(TRUE.equals(attributes.getVideoUseInboundSiteOrApp()));
    company
        .getSellerAttributes()
        .setTransparencyMgmtEnablement(attributes.getTransparencyMgmtEnablement());
    company.getSellerAttributes().setIncludePubName(attributes.getIncludePubName());
    company.getSellerAttributes().setPubNameAlias(attributes.getPubNameAlias());
    company.getSellerAttributes().setSuperAuctionEnabled(attributes.isSuperAuctionEnabled());
    company.getSellerAttributes().setAdFeedbackOptOut(attributes.getAdFeedbackOptOut());
    company.getSellerAttributes().setHumanOptOut(attributes.getHumanOptOut());
    company.getSellerAttributes().setSmartQPSEnabled(attributes.getSmartQPSEnabled());
    company
        .getSellerAttributes()
        .setBuyerTransparencyOptOut(attributes.getBuyerTransparencyOptOut());
    company.getSellerAttributes().setRevenueGroupPid(attributes.getRevenueGroupPid());
    company.getSellerAttributes().setSellerType(attributes.getSellerType());
    company
        .getSellerAttributes()
        .setDefaultBiddersAllowList(attributes.isDefaultBiddersAllowList());
    company
        .getSellerAttributes()
        .setReviewStatusBlock(
            attributes.getReviewStatusBlock() == null
                ? CrsReviewStatusBlock.ALLOW_ALL
                : attributes.getReviewStatusBlock());
    company
        .getSellerAttributes()
        .setSecureStatusBlock(
            attributes.getSecureStatusBlock() == null
                ? CrsSecureStatusBlock.ALLOW_ALL
                : attributes.getSecureStatusBlock());
    company.getSellerAttributes().setDynamicFloorEnabled(attributes.getDynamicFloorEnabled());
    company.getSellerAttributes().setSspDealRevShare(attributes.getSspDealRevShare());
    company.getSellerAttributes().setJointDealRevShare(attributes.getJointDealRevShare());
    company.getSellerAttributes().setSellerDealRevShare(attributes.getSellerDealRevShare());
    company.getSellerAttributes().setRawResponse(attributes.isRawResponse());
  }

  private void checkUpdateCompanyHouseAdvertiser(Company updated, Company company) {
    if (CompanyType.SELLER == updated.getType() && company.isAdServingEnabled()) {
      boolean hasHouseAdvertiser = false;
      for (Advertiser advertiser :
          advertiserRepository.findAllBySellerIdAndStatusNotDeleted(company.getPid())) {
        if (advertiser.isHouseAdvertiser()) {
          hasHouseAdvertiser = true;
          break;
        }
      }
      if (!hasHouseAdvertiser) {
        Advertiser houseAdvertiser = Advertiser.createHouseAdvertiser(company.getPid());
        advertiserRepository.save(houseAdvertiser);
        if (log.isInfoEnabled()) {
          log.info("Automatically created HOUSE advertiser for company (pid): " + company.getPid());
        }
      }
    }
  }

  private static void setSellerSeatPid(Company company) {
    if (CompanyType.SELLER == company.getType()) {
      SellerSeat sellerSeat = company.getSellerSeat();
      if (nonNull(sellerSeat)) {
        // we are setting the sellerSeatPid since it must be present in the response even if we are
        // not serializing SellerSeat entity
        company.setSellerSeatPid(sellerSeat.getPid());
      }
    }
  }

  private void assignSellerSeatIfApplicable(Company company) {
    if (!CompanyType.SELLER.equals(company.getType()) || isNull(company.getSellerSeatPid())) {
      // remove seller seat from company if 'None' seller seat was chosen
      if (company.getSellerSeat() != null) {
        company.getSellerSeat().removeSeller(company);
      }
      return;
    }
    Optional<SellerSeat> sellerSeat = sellerSeatRepository.findById(company.getSellerSeatPid());
    if (sellerSeat.isEmpty()) {
      log.error(
          "Seller seat with id {} NOT found. Association is not possible",
          company.getSellerSeatPid());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND);
    }
    if (sellerSeat.get().isDisabled()) {
      log.error(
          "Seller seat with id {} is DISABLED. Association is not possible",
          company.getSellerSeatPid());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_ENABLED);
    }
    sellerSeat.get().addSeller(company);
  }

  private SellerSeat getInitialSellerSeatFromDb(Company company) {
    if (!CompanyType.SELLER.equals(company.getType())
        || isNull(company.getSellerSeat())
        || isNull(company.getSellerSeat().getPid())) {
      return null;
    }

    return sellerSeatRepository.findById(company.getSellerSeat().getPid()).orElse(null);
  }

  /**
   * update user <-> company relations MX-12481
   *
   * @param company company
   */
  private void updateUserCompanyRelationForSellerSeat(Company company) {
    Long sellerSeatPid = company.getSellerSeatPid();
    if (isNull(sellerSeatPid) || !CompanyType.SELLER.equals(company.getType())) {
      return;
    }
    List<User> users = userRepository.findAllBySellerSeat_Pid(sellerSeatPid);
    if (nonNull(users)) {
      users.forEach(user -> user.addCompany(company));
    }
  }

  private void updateUserCompanyRelationForSellerSeatWhenInitialSellerSeatExists(
      Company company, Long initialSellerSeatPid) {
    if (!CompanyType.SELLER.equals(company.getType())) {
      return;
    }
    // seller seat is 'None' or different from the one previously set on the Company entity
    if (isNull(company.getSellerSeatPid())
        || !company.getSellerSeatPid().equals(initialSellerSeatPid)) {
      List<User> usersFromDb = userRepository.findAllBySellerSeat_Pid(initialSellerSeatPid);
      Set<Long> usersPids = getUsersPids(usersFromDb);

      if (!usersFromDb.isEmpty()) {
        // clear the company from all seller seat users
        userRepository.deleteCompanyAppUserByCompanyPidAndUserPid(company.getPid(), usersPids);
        usersFromDb.forEach(user -> user.removeCompany(company));
      }
    }

    // add the company to all users that are affiliated to the new seller seat
    updateUserCompanyRelationForSellerSeat(company);
  }

  private Set<Long> getUsersPids(List<User> usersFromDb) {
    return usersFromDb.stream().map(User::getPid).collect(TreeSet::new, Set::add, Set::addAll);
  }

  private void updateSitesRevenueShare(Long companyPid) {
    List<Long> siteIds = siteRepository.findPidsByCompanyPid(companyPid);
    sellerSiteService.updateSiteDealTermsToPubDefault(companyPid, siteIds);
  }

  private void populateDefaultRtbProfileFields(Company company) {
    if (company.getSellerAttributes() != null
        && company.getSellerAttributes().getDefaultRtbProfile() != null) {
      company
          .getSellerAttributes()
          .setDefaultRtbProfile(
              rtbProfileUtil.prepareDefaultRtbProfile(
                  company.getSellerAttributes().getDefaultRtbProfile()));
    }
  }

  private void addNotSerializedDataFromDB(Company companyInDb, Company tobeupdated) {
    User contact = companyInDb.getContact();
    if (contact != null && contact.getPid().equals(tobeupdated.getPid())) {
      tobeupdated.setContact(contact);
    } else {
      log.info(
          "Company{} contact has been updated to user{}",
          tobeupdated.getPid(),
          tobeupdated.getContactUserPid());
      addContact(tobeupdated, tobeupdated.getContactUserPid());
    }
    tobeupdated.getCredits().clear();
    tobeupdated.getCredits().addAll(companyInDb.getCredits());
  }

  private Company findByPid(long pid) {
    return companyRepository
        .findById(pid)
        .orElseThrow(
            () -> {
              log.info("Company not found in database: " + pid);
              return new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
            });
  }

  public int togglePfo(long companyPid, boolean isPfoEnabled) {
    Set<Long> sitePids =
        siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(Set.of(companyPid));
    List<RTBProfile> rtbProfiles = rtbProfileRepository.findBySitePidIn(sitePids);

    return updateAlterReserve(rtbProfiles, isPfoEnabled);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public void deleteCompany(long companyPID) {

    if (log.isInfoEnabled()) {
      log.info("Deleting company {} and all related objects...", companyPID);
    }

    Company company = findByPid(companyPID);

    if (CompanyType.BUYER.equals(company.getType())) {
      deleteCompanyTypeBuyer(company, companyPID);
    } else if (CompanyType.SELLER.equals(company.getType())) {
      deleteCompanyTypeSeller(company);
    } else if (CompanyType.SEATHOLDER.equals(company.getType())) {
      deleteCompanyTypeSeatholder(company);
    } else {
      log.error("Cannot delete NEXAGE company. NOTE: this is very suspicious behavior.");
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_NEXAGE_DELETE_NOT_ALLOWED);
    }

    deleteCompanyUsers(company);

    companyRepository.delete(company);
  }

  private void deleteCompanyTypeBuyer(Company company, long companyPID) {
    var calendar = Calendar.getInstance();
    calendar.set(0, Calendar.JANUARY, 0);
    var start = DateUtil.format(calendar.getTime());
    var stop = DateUtil.format(new Date());

    long bidRequests = 0L;
    try {
      BuyerKeyMetrics buyerKeyMetrics = dashboardDao.getBuyerMetrics(start, stop, company.getPid());
      bidRequests = buyerKeyMetrics.getBidsRequests();
    } catch (EmptyResultDataAccessException | BadSqlGrammarException e) {
      // these exceptions are ok, they occur when a company is so new
      // that dw doesn't have it in dim_company yet; thus, no bids yet
    }

    if (0L == bidRequests) {
      List<BidderConfig> bidderConfigs = bidderConfigRepository.findByCompanyPid(companyPID);
      for (BidderConfig bidderConfig : bidderConfigs) {
        logUsersDeletionMessage(bidderConfigs);
        bidderConfigRepository.delete(bidderConfig);
      }

    } else {
      log.error("Cannot delete active BUYER company because it previously made bids.");
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_BUYER_DELETE_NOT_ALLOWED);
    }
  }

  private void deleteCompanyTypeSeller(Company company) {
    if (!siteRepository.existsByCompanyPidAndStatusNot(company.getPid(), Status.DELETED)) {

      List<Campaign> campaigns = campaignRepository.findBySellerId(company.getPid());
      logUsersDeletionMessage(campaigns);
      campaignRepository.deleteAll(campaigns);

      List<Creative> creatives = creativeRepository.findAllBySellerId(company.getPid());
      logUsersDeletionMessage(creatives);
      creativeRepository.deleteAll(creatives);

      List<Advertiser> advertisers = advertiserRepository.findAllBySellerId(company.getPid());

      logUsersDeletionMessage(advertisers);
      advertiserRepository.deleteAll(advertisers);

    } else {
      log.error("Cannot delete active SELLER company because it has sites.");
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_SELLER_DELETE_NOT_ALLOWED);
    }
  }

  private void deleteCompanyTypeSeatholder(Company company) {
    if (bdrInsertionOrderRepository.findAllByAdvertiser_Company_Pid(company.getPid()).isEmpty()) {
      List<BDRAdvertiser> bdrAdvertisers =
          bdrAdvertiserRepository.findByCompanyPid(company.getPid());
      logUsersDeletionMessage(bdrAdvertisers);

      bdrAdvertiserRepository.deleteAll(bdrAdvertisers);

      // Delete the BdrExchangeCompany record before deleting company
      List<BdrExchangeCompany> bdrExchangeCompanies =
          bdrExchangeCompanyRepository.findByExchangeCompanyPk_Company_Pid(company.getPid());
      if (!bdrExchangeCompanies.isEmpty()) {
        logUsersDeletionMessage(bdrExchangeCompanies);
        bdrExchangeCompanyRepository.deleteAll(bdrExchangeCompanies);
      }
    } else {
      log.error("Cannot delete SeatHolder company because it has insertion Orders.");
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_SEATHOLDER_DELETE_NOT_ALLOWED);
    }
  }

  private void deleteCompanyUsers(Company company) {
    List<User> users = userRepository.findAll(UserSpecification.withCompany(company.getPid()));
    if (company.getSellerSeatPid() != null) {
      users =
          users.stream()
              .filter(user -> !company.getSellerSeatPid().equals(user.getSellerSeatPid()))
              .collect(Collectors.toList());
    }
    logUsersDeletionMessage(users);

    for (User user : users) {
      if (user.getUserName().equals("superadmin")) {
        log.error("<<<ALARM>>> Attempt to delete superadmin user");
        throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_CANNOT_DELETE_SUPERADMIN);
      }
      userRestrictedSiteRepository.deleteByPkUserId(user.getPid());
      userRepository.delete(user);
    }
  }

  private void logUsersDeletionMessage(List<?> users) {
    if (log.isInfoEnabled() && !CollectionUtils.isEmpty(users)) {
      log.info("Deleting all {} users that belong to company.", users.size());
    }
  }

  /**
   * Mark all related company resources as DELETED.
   *
   * @param companyPID company pid
   */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public void softDeleteCompany(long companyPID) {

    if (log.isInfoEnabled()) {
      log.info("Soft deleting company " + companyPID + " and all related objects...");
    }

    Company company = findByPid(companyPID);
    company.setStatus(Status.DELETED);
    companyRepository.save(company);
  }

  private void addCompanyDetails(Company company) {
    switch (company.getType()) {
      case SELLER:
        addCompanyDetailsSeller(company);
        break;
      case BUYER:
        addCompanyDetailsBuyer(company);
        break;
      case SEATHOLDER:
        addCompanyDetailsSeatHolder(company);
        break;
      default:
        break;
    }
    postProcessCompany(company);
  }

  private void addCompanyDetailsForType(CompanyType type, List<Company> companies) {
    switch (type) {
      case SELLER:
        Map<Long, SellerMetadataDTO> sellerMap = findSellerMetadataDtoMap();
        addCompanyDetailsForTypeSeller(companies, sellerMap);
        break;
      case BUYER:
        Map<Long, BuyerMetadataDTO> buyerMap = findBuyerMetadataDtoMap();
        addCompanyDetailsForTypeBuyer(companies, buyerMap);
        break;
      case SEATHOLDER:
        Map<Long, SeatHolderMetadataDTO> seatholderMap = findSeatHolderMetadataDtoMap();
        addCompanyDetailsForTypeSeatHolder(companies, seatholderMap);
        break;
      default:
        break;
    }
  }

  private Map<Long, SeatHolderMetadataDTO> findSeatHolderMetadataDtoMap() {
    List<AllSeatHolderMetaDataReturnProjection> metadataProjections =
        companyRepository.findAllSeatHolderMetaDataReturnProjections();
    Map<Long, SeatHolderMetadataDTO> metadataMap = new HashMap<>();
    for (AllSeatHolderMetaDataReturnProjection metadataProjection : metadataProjections) {
      long companyPid = metadataProjection.getCompany();
      metadataMap.computeIfAbsent(
          companyPid, k -> new SeatHolderMetadataDTO(metadataProjection.getIos()));
    }
    return metadataMap;
  }

  private Map<Long, BuyerMetadataDTO> findBuyerMetadataDtoMap() {
    List<BuyerMetaDataForCompanyProjection> buyerMetadataProjections =
        companyRepository.findAllBuyerMetaDataForCompanyProjections();
    Map<Long, BuyerMetadataDTO> metadataMap = new HashMap<>();
    for (BuyerMetaDataForCompanyProjection buyerMetadataProjection : buyerMetadataProjections) {
      long companyPid = buyerMetadataProjection.getCompany();
      metadataMap.computeIfAbsent(companyPid, k -> new BuyerMetadataDTO());
      BuyerMetadataDTO metadata = metadataMap.get(companyPid);
      mapBuyerMetadata(buyerMetadataProjection, metadata);
    }
    return metadataMap;
  }

  private void mapBuyerMetadata(
      BuyerMetaDataForCompanyProjection buyerMetadataProjection, BuyerMetadataDTO metadata) {
    if (buyerMetadataProjection.getDataProvider() != null) {
      metadata.addToExternalDataProviderNames(buyerMetadataProjection.getDataProvider());
    }
    if (buyerMetadataProjection.getAdsource() != null) {
      metadata.addToAdsourceNames(buyerMetadataProjection.getAdsource());
    }
    if (buyerMetadataProjection.getUser() != null) {
      metadata.addToUserNames(buyerMetadataProjection.getUser());
    }
  }

  private Map<Long, SellerMetadataDTO> findSellerMetadataDtoMap() {
    List<SellerMetaDataForCompanyProjection> sellerMetadataProjections =
        companyRepository.findAllSellerMetaDataForCompanyProjections();
    Map<Long, SellerMetadataDTO> metadataMap = new HashMap<>();
    for (SellerMetaDataForCompanyProjection sellerMetadataProjection : sellerMetadataProjections) {
      long companyPid = sellerMetadataProjection.getCompany();
      metadataMap.computeIfAbsent(companyPid, k -> new SellerMetadataDTO());
      SellerMetadataDTO metadata = metadataMap.get(companyPid);
      metadata.setNumberOfMediationSites(sellerMetadataProjection.getSites());
      metadata.setNumberOfRtbTags(sellerMetadataProjection.getTags());
      metadata.setNumberOfUsers(sellerMetadataProjection.getUsers());
      metadata.setNumberOfHbSites(sellerMetadataProjection.getHbsites());
    }
    return metadataMap;
  }

  @Override
  public void addContact(Company company, Long userPid) {
    if (userPid != null) {
      User contact =
          userRepository
              .findById(company.getContactUserPid())
              .orElseThrow(
                  () -> new GenevaValidationException(ServerErrorCodes.SERVER_USER_NOT_FOUND));
      company.setContact(contact);
    }
  }

  private BdrExchangeCompany createBdrExchangeCompany(Company company) {
    // Currently, we only have one exchange, and therefore it is being looked up by a hardCoded
    // value of pid = 1
    // pid 1 = Nexage Exchange in bdr_exchange table.
    var bdrExchange =
        bdrExchangeRepository
            .findById(1L)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_TARGET_INVALID_EXCHANGE_ID));
    var exchangeCompany = new BdrExchangeCompany(bdrExchange, company);
    exchangeCompany.setBiddingFee(null);
    return exchangeCompany;
  }

  private Company.EstimateTimeRemaining calculateEstimatedTimeRemaining(final Company company) {
    BigDecimal credit = company.getCredit();
    Company.EstimateTimeRemaining etr;

    if (credit == null || credit.compareTo(BigDecimal.ZERO) <= 0) {
      etr = new EstimateTimeRemaining(ETR.NO_CREDIT, BigInteger.ZERO);
    } else {
      etr = new EstimateTimeRemaining(ETR.NO_SPEND, BigInteger.ZERO);
    }
    return etr;
  }

  private void validateSellerAttributesHbFields(Company original, Company updated) {
    SellerAttributes originalSa = original.getSellerAttributes();
    SellerAttributes updatedSa = updated == null ? null : updated.getSellerAttributes();

    Integer percentToCheck =
        updatedSa == null
            ? originalSa.getHbThrottlePercentage()
            : updatedSa.getHbThrottlePercentage();

    // Only Nexage users are authorized to update the HB fields
    if (userContext.getType() != CompanyType.NEXAGE
        && (updatedSa != null && !(areHbFieldsIdentical(originalSa, updatedSa)))) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    if (percentToCheck != null && (percentToCheck < 0 || percentToCheck > 100)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_THROTTLE_PERCENTAGE_INVALID);
    }
  }

  private void checkSmartQPSEnabledEditPermission(
      SellerAttributes original, SellerAttributes updated) {
    Boolean originalValue = original.getSmartQPSEnabled();
    Boolean updatedValue = updated != null && updated.getSmartQPSEnabled();

    if (!originalValue.equals(updatedValue) && !userContext.canEditSmartExchange()) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  private void checkDynamicFloorEnabledEditPermission(Boolean originalValue, Boolean updated) {
    Boolean updatedValue = updated != null && updated;

    if (nonNull(originalValue)
        && !originalValue.equals(updatedValue)
        && !userContext.canEditSmartExchange()) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  private boolean areHbFieldsIdentical(SellerAttributes originalSa, SellerAttributes updatedSa) {
    if (originalSa.isHbThrottleEnabled() != updatedSa.isHbThrottleEnabled()) {
      return false;
    }
    if ((originalSa.getHbThrottlePercentage() == null
            && updatedSa.getHbThrottlePercentage() != null)
        || (originalSa.getHbThrottlePercentage() != null
            && updatedSa.getHbThrottlePercentage() == null)) {
      return false;
    }
    if (originalSa.getHbThrottlePercentage() == null
        && updatedSa.getHbThrottlePercentage() == null) {
      return true;
    }

    return originalSa.getHbThrottlePercentage().equals(updatedSa.getHbThrottlePercentage());
  }

  private void validateTransparencySettingsMgmtFlag(SellerAttributes sellerAttributes) {
    TransparencyMgmtEnablement mgmtEnablement =
        TransparencyMgmtEnablement.getById(sellerAttributes.getTransparencyMgmtEnablement());
    if (mgmtEnablement == null) {
      log.error(
          "Cannot update transparency settings when transparency mgmt flag is unexpected: {}",
          sellerAttributes.getTransparencyMgmtEnablement());
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_TRANSPARENCY_SETTING_MGMT_FLAG_UNKNOWN);
    }
  }

  @Override
  public String getDefaultCurrencyCode() {
    return DEFAULT_CURRENCY_CODE;
  }

  private boolean attributeChanged(String previousValue, String newValue) {
    if (previousValue == null) {
      return newValue != null;
    }

    return !previousValue.equals(newValue);
  }

  private void addCompanyDetailsForTypeSeller(
      List<Company> companies, Map<Long, SellerMetadataDTO> sellerMap) {
    for (Company company : companies) {
      SellerMetadataDTO metadata = sellerMap.get(company.getPid());
      if (metadata != null) {
        company.setNumberOfUsers(metadata.getNumberOfUsers());
        company.setNumberOfMediationSites(metadata.getNumberOfMediationSites());
        company.setNumberOfRtbTags(metadata.getNumberOfRtbTags());
        company.setHasHeaderBiddingSites(metadata.getNumberOfHbSites() > 0);
      }
    }
  }

  private void addCompanyDetailsForTypeBuyer(
      List<Company> companies, Map<Long, BuyerMetadataDTO> buyerMap) {
    for (Company company : companies) {
      BuyerMetadataDTO metadata = buyerMap.get(company.getPid());
      if (metadata != null) {
        company.setNumberOfUsers(metadata.getUserNames().size());
        company.setAdsourceNames(metadata.getAdsourceNames());
        company.setExternalDataProviderNames(metadata.getExternalDataProviderNames());
        company.setHasHeaderBiddingSites(null);
      }
    }
  }

  private void addCompanyDetailsForTypeSeatHolder(
      List<Company> companies, Map<Long, SeatHolderMetadataDTO> seatholderMap) {
    for (Company company : companies) {
      SeatHolderMetadataDTO metadata = seatholderMap.get(company.getPid());
      if (metadata != null) {
        company.setActiveIOs(metadata.getActiveIOs());
        company.setHasHeaderBiddingSites(null);
      }
    }
  }

  private void addCompanyDetailsSeller(Company company) {
    var metadata = findSellerMetadataDtoByCompanyPid(company.getPid());
    company.setNumberOfUsers(metadata.getNumberOfUsers());
    company.setNumberOfMediationSites(metadata.getNumberOfMediationSites());
    company.setNumberOfRtbTags(metadata.getNumberOfRtbTags());
    company.setHasHeaderBiddingSites(metadata.getNumberOfHbSites() > 0);
    // to avoid serialization of theses as they're irrelevant for seller
    company.setAdsourceNames(null);
    company.setExternalDataProviderNames(null);
  }

  private SellerMetadataDTO findSellerMetadataDtoByCompanyPid(long companyPid) {
    List<SellerMetaDataForCompanyProjection> sellerMetadataProjections =
        companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(companyPid);
    var metadata = new SellerMetadataDTO();
    for (SellerMetaDataForCompanyProjection sellerMetadataProjection : sellerMetadataProjections) {
      metadata.setNumberOfMediationSites(sellerMetadataProjection.getSites());
      metadata.setNumberOfRtbTags(sellerMetadataProjection.getTags());
      metadata.setNumberOfUsers(sellerMetadataProjection.getUsers());
      metadata.setNumberOfHbSites(sellerMetadataProjection.getHbsites());
    }
    return metadata;
  }

  private void addCompanyDetailsBuyer(Company company) {
    var metadata = findBuyerMetadataDtoByCompanyPid(company.getPid());
    company.setNumberOfUsers(metadata.getUserNames().size());
    company.setAdsourceNames(metadata.getAdsourceNames());
    company.setExternalDataProviderNames(metadata.getExternalDataProviderNames());
    company.setHasHeaderBiddingSites(null);
  }

  private BuyerMetadataDTO findBuyerMetadataDtoByCompanyPid(long companyPid) {
    List<BuyerMetaDataForCompanyProjection> buyerMetadataProjections =
        companyRepository.findBuyerMetaDataForCompanyProjectionsByCompanyPid(companyPid);
    var metadata = new BuyerMetadataDTO();
    for (BuyerMetaDataForCompanyProjection buyerMetadataProjection : buyerMetadataProjections) {
      mapBuyerMetadata(buyerMetadataProjection, metadata);
    }
    return metadata;
  }

  private void addCompanyDetailsSeatHolder(Company company) {
    var seatHolderMetadataDto =
        companyRepository.findSeatHolderMetadataByCompanyPid(company.getPid());
    if (seatHolderMetadataDto != null) {
      company.setActiveIOs(seatHolderMetadataDto.getActiveIOs());
      company.setEstimatedTimeRemaining(calculateEstimatedTimeRemaining(company));
    }
    // to avoid serialization of theses as they're irrelevant for seatholder
    company.setHasHeaderBiddingSites(null);
    company.setAdsourceNames(null);
    company.setExternalDataProviderNames(null);
  }

  private int updateAlterReserve(List<RTBProfile> rtbProfiles, boolean isPfoEnabled) {
    AlterReserve alterReserve = isPfoEnabled ? AlterReserve.ONLY_IF_HIGHER : AlterReserve.OFF;

    return rtbProfileRepository
        .saveAll(
            rtbProfiles.stream()
                .filter(rtbProfile -> !alterReserve.equals(rtbProfile.getAlterReserve()))
                .map(
                    rtbProfile -> {
                      rtbProfile.setAlterReserve(alterReserve);
                      return rtbProfile;
                    })
                .collect(Collectors.toList()))
        .size();
  }

  private void setTransientFields(Company originalCompany, Company savedCompany) {
    savedCompany.setNumberOfUsers(originalCompany.getNumberOfUsers());
    savedCompany.setNumberOfRtbTags(originalCompany.getNumberOfRtbTags());
    savedCompany.setNumberOfMediationSites(originalCompany.getNumberOfMediationSites());
    savedCompany.setAdsourceNames(originalCompany.getAdsourceNames());
    savedCompany.setExternalDataProviderNames(originalCompany.getExternalDataProviderNames());
    savedCompany.setActiveIOs(originalCompany.getActiveIOs());
    savedCompany.setCredit(originalCompany.getCredit());
    savedCompany.setSellerSeatPid(originalCompany.getSellerSeatPid());
  }
}

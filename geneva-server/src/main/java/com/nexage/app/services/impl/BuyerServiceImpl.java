package com.nexage.app.services.impl;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.ContentEncodingType;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.UserIdPreference;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.BidderConfig.SubscriptionInfo;
import com.nexage.admin.core.model.BidderDeviceType;
import com.nexage.admin.core.model.BidderSubscription;
import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.ExchangeProduction;
import com.nexage.admin.core.model.ExchangeRegional;
import com.nexage.admin.core.model.ExternalDataProvider;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.repository.AdSourceRepository;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.BuyerSeatRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DeviceTypeRepository;
import com.nexage.admin.core.repository.ExchangeProductionRepository;
import com.nexage.admin.core.repository.ExchangeRegionalRepository;
import com.nexage.admin.core.repository.ExternalDataProviderRepository;
import com.nexage.admin.core.sparta.jpa.model.AdSourceLogoFileReference;
import com.nexage.admin.core.sparta.jpa.model.NativeTypeAdsource;
import com.nexage.admin.core.specification.BuyerSeatSpecification;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.dto.buyer.BuyerSeatDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.BuyerGroupDTOMapper;
import com.nexage.app.services.AdSourceService;
import com.nexage.app.services.BidderConfigService;
import com.nexage.app.services.BuyerService;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.util.EnvironmentUtil;
import com.nexage.app.util.HttpUtil;
import com.nexage.app.util.assemblers.buyer.BuyerGroupAssembler;
import com.nexage.app.util.assemblers.buyer.BuyerSeatAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.config.BidUnit;
import com.ssp.geneva.common.model.config.FilterAuction;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ws.rs.InternalServerErrorException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"SpringElInspection"})
@Log4j2
@Transactional
@Service("buyerService")
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class BuyerServiceImpl implements BuyerService {

  private static final List<Long> EXCHANGE_IDS = new ArrayList<>();
  private static final List<Long> TEST_EXCHANGE_IDS = new ArrayList<>();

  private final AdSourceRepository adSourceRepository;
  private final AdSourceService adSourceService;
  private final CompanyRepository companyRepository;
  private final BidderConfigService bidderConfigService;
  private final BidderConfigRepository bidderConfigRepository;
  private final ExchangeRegionalRepository exchangeRegionalRepository;
  private final BuyerGroupRepository buyerGroupRepository;
  private final BuyerSeatRepository buyerSeatRepository;
  private final BuyerGroupAssembler buyerGroupAssembler;
  private final BuyerSeatAssembler buyerSeatAssembler;
  private final DeviceTypeRepository deviceTypeRepository;

  private final PhoneCastConfigService phoneCastConfigService;

  private final ExternalDataProviderRepository externalDataProviderRepository;
  private final ExchangeProductionRepository exchangeProductionRepository;
  private final FileSystemService fileSystemService;
  private final GlobalConfigService globalConfigService;
  private final EnvironmentUtil environmentUtil;

  @Value("${spp.geneva.static.base.url}")
  private String staticBaseUrl;

  private String logoBaseUrl;
  private String logoUploadDir;

  @Autowired
  public BuyerServiceImpl(
      AdSourceService adSourceService,
      AdSourceRepository adSourceRepository,
      BidderConfigService bidderConfigService,
      BidderConfigRepository bidderConfigRepository,
      ExchangeRegionalRepository exchangeRegionalRepository,
      ExchangeProductionRepository exchangeProductionRepository,
      FileSystemService fileSystemService,
      CompanyRepository companyRepository,
      BuyerGroupRepository buyerGroupRepository,
      BuyerSeatRepository buyerSeatRepository,
      BuyerGroupAssembler buyerGroupAssembler,
      ExternalDataProviderRepository externalDataProviderRepository,
      BuyerSeatAssembler buyerSeatAssembler,
      DeviceTypeRepository deviceTypeRepository,
      PhoneCastConfigService phoneCastConfigService,
      GlobalConfigService globalConfigService,
      EnvironmentUtil environmentUtil) {
    this.adSourceService = adSourceService;
    this.adSourceRepository = adSourceRepository;
    this.bidderConfigService = bidderConfigService;
    this.bidderConfigRepository = bidderConfigRepository;
    this.exchangeRegionalRepository = exchangeRegionalRepository;
    this.exchangeProductionRepository = exchangeProductionRepository;
    this.fileSystemService = fileSystemService;
    this.companyRepository = companyRepository;
    this.buyerGroupRepository = buyerGroupRepository;
    this.buyerSeatRepository = buyerSeatRepository;
    this.buyerGroupAssembler = buyerGroupAssembler;
    this.externalDataProviderRepository = externalDataProviderRepository;
    this.buyerSeatAssembler = buyerSeatAssembler;
    this.deviceTypeRepository = deviceTypeRepository;
    this.phoneCastConfigService = phoneCastConfigService;
    this.globalConfigService = globalConfigService;
    this.environmentUtil = environmentUtil;
  }

  @PostConstruct
  public void init() {
    EXCHANGE_IDS.addAll(transformExchangeIds(phoneCastConfigService.getExchangeIdsAsList()));
    TEST_EXCHANGE_IDS.addAll(
        transformExchangeIds(phoneCastConfigService.getTestExchangeIdsAsList()));
    logoBaseUrl =
        environmentUtil.isAwsEnvironment()
            ? staticBaseUrl + File.separator
            : globalConfigService.getStringValue(GlobalConfigProperty.BUYER_LOGO_BASE_URL);
    logoUploadDir = globalConfigService.getStringValue(GlobalConfigProperty.BUYER_LOGO_DIR);
    checkAndCreateLogoUploadDir();
    if (!logoUploadDir.endsWith(File.separator)) {
      logoUploadDir += File.separator;
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserNexage()")
  public List<AdSourceSummaryDTO> getAllAdSourceSummaries() {
    List<AdSourceSummaryDTO> summaries = adSourceService.getAdSourceSummariesForGeneva();
    for (AdSourceSummaryDTO summary : summaries) {
      summary.setExchange(isExchange(summary.getPid()));
      summary.setTestExchange(isTestExchange(summary.getPid()));
      addLogoUrlTo(summary);
    }
    return summaries;
  }

  @Override
  @PreAuthorize(value = "hasAnyRole('ROLE_USER_NEXAGE', 'ROLE_USER_SELLER')")
  public List<BidderSummaryDTO> getAllBidderSummaries() {
    return bidderConfigService.getBidderSummaries();
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserNexage()")
  public AdSource getAdSource(Long adSourcePid) {
    AdSource adSource;
    try {
      adSource =
          adSourceRepository
              .findById(adSourcePid)
              .orElseThrow(
                  () -> new GenevaValidationException(ServerErrorCodes.SERVER_ADSOURCE_NOT_FOUND));
    } catch (Exception e) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ADSOURCE_NOT_FOUND);
    }
    adSource.setExchange(isExchange(adSource.getPid()));
    addLogoUrlTo(adSource);
    return adSource;
  }

  @Override
  @Transactional(readOnly = true)
  public List<AdSource> getAllAdSourcesByCompanyPid(Long companyPid) {
    if (!companyRepository.existsById(companyPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }
    List<AdSource> adSources = adSourceRepository.findNonDeletedByCompanyPid(companyPid);
    for (AdSource adSource : adSources) {
      adSource.setExchange(isExchange(adSource.getPid()));
      addLogoUrlTo(adSource);
    }
    return adSources;
  }

  @Override
  @Transactional(readOnly = true)
  public List<AdSourceSummaryDTO> getAllAdSourceSummariesByCompanyPid(Long companyPid) {
    if (!companyRepository.existsById(companyPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }
    List<AdSourceSummaryDTO> adSources =
        adSourceService.getAdSourceSummariesByCompanyPid(companyPid);
    for (AdSourceSummaryDTO adSource : adSources) {
      adSource.setExchange(isExchange(adSource.getPid()));
      addLogoUrlTo(adSource);
    }
    return adSources;
  }

  private void addLogoUrlTo(com.nexage.admin.core.model.AdSourceSummary adSource) {
    if (StringUtils.isBlank(logoBaseUrl) || StringUtils.isBlank(adSource.getLogo())) {
      adSource.setLogoUrl(null);
      return;
    }

    adSource.setLogoUrl(logoBaseUrl + adSource.getLogo());
  }

  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#companyPid) == true "
          + "and @loginUserContext.isOcManagerNexage()")
  public AdSource createAdSource(Long companyPid, AdSource adSource) {
    Company company =
        companyRepository
            .findById(companyPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_CREATE_ADSOURCE_COMPANY_NOT_FOUND));

    adSource.setCompany(company);

    List<NativeTypeAdsource> ntaInJSON = adSource.getNativeTypeAdSource();
    adSource.setNativeTypeAdSource(Collections.emptyList());
    for (NativeTypeAdsource nta : ntaInJSON) {
      nta.setAdsource(adSource);
    }
    adSource.setNativeTypeAdSource(ntaInJSON);

    adSource.setCridHeaderField(cleanCreativeIdHeaderName(adSource.getCridHeaderField()));

    AdSource createdAdSource = adSourceRepository.save(adSource);

    boolean addedLogo = updateLogo(adSource);
    if (addedLogo) {
      createdAdSource = adSourceRepository.save(createdAdSource);
    }

    createdAdSource.setExchange(isExchange(createdAdSource.getPid()));
    addLogoUrlTo(createdAdSource);

    return createdAdSource;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#companyPid) == true "
          + "and @loginUserContext.isOcManagerNexage()")
  public AdSource updateAdSource(Long companyPid, AdSource adSource, Long adSourcePid) {
    if (!adSource.getPid().equals(adSourcePid)) {
      log.error(
          "Provided AdSource PID [{}] does not match with AdSource object PID [{}].",
          adSourcePid,
          adSource.getPid());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_AD_SOURCE_ID_INVALID);
    }

    if (adSource.getStatus() == Status.DELETED) {
      log.error(
          "Attempt to set AdSource status to DELETED for company Id [{}]",
          adSource.getCompany().getId());
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }

    Company company =
        companyRepository
            .findById(companyPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
    try {
      getAdSource(adSource.getPid());
    } catch (Exception e) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ADSOURCE_NOT_FOUND);
    }

    adSource.setCompany(company);
    for (NativeTypeAdsource nta : adSource.getNativeTypeAdSource()) {
      nta.setAdsource(adSource);
    }
    adSource.setCridHeaderField(cleanCreativeIdHeaderName(adSource.getCridHeaderField()));
    updateLogo(adSource);
    AdSource updatedAdSource = adSourceRepository.save(adSource);
    updatedAdSource.setExchange(isExchange(updatedAdSource.getPid()));
    addLogoUrlTo(updatedAdSource);
    return updatedAdSource;
  }

  void writeLogoToDisc(String logoPath, byte[] image) {
    fileSystemService.write(logoUploadDir, logoPath, image);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public void deleteAdSource(Long adSourcePid) {
    try {
      getAdSource(adSourcePid);
    } catch (Exception e) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ADSOURCE_NOT_FOUND);
    }
    adSourceService.softDelete(adSourcePid);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()")
  public List<BidderConfig> getAllBidderConfigsByCompanyPid(Long companyPid) {
    if (!companyRepository.existsById(companyPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND);
    }
    List<BidderConfig> configs = bidderConfigRepository.findByCompanyPid(companyPid);
    if (configs == null || configs.isEmpty()) {
      log.error("Bidder Configs do not exist for the buyer [{}]", companyPid);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND);
    }
    return configs;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()")
  public BidderConfig getBidderConfig(long buyerPid, long bidderConfigPid) {
    BidderConfig bidderConfig =
        bidderConfigRepository
            .findById(bidderConfigPid)
            .orElseThrow(
                () -> {
                  log.error(
                      "Bidder Config [{}] doesn't exist for pid [{}]", bidderConfigPid, buyerPid);
                  return new GenevaValidationException(
                      ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND);
                });
    if (!bidderConfig.getCompanyPid().equals(buyerPid)) {
      log.error(
          "Bidder Config's company pid passed does not belong to the requested bidder config");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_COMPANY);
    }
    return bidderConfig;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerBuyer()")
  public BidderConfig createBidderConfig(Long buyerPid, BidderConfig bidderConfig) {
    Company company =
        companyRepository
            .findById(buyerPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND));

    if (bidderConfig == null) {
      bidderConfig = bidderConfigService.createDefaultBidderConfigForBuyer(company);
    } else {
      bidderConfig.setCompany(company);
      bidderConfig.setCompanyPid(buyerPid);
      validateBidderConfig(company, bidderConfig);
    }
    userIdPreferenceCheck(bidderConfig);

    return bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerBuyer()")
  public BidderConfig updateBidderConfig(
      long bidderConfigPid, BidderConfig bidderConfig, Long buyerPid) {
    userIdPreferenceCheck(bidderConfig);
    validateBidderConfig(bidderConfigPid, bidderConfig);
    BidderConfig existingConfig =
        bidderConfigRepository
            .findById(bidderConfig.getPid())
            .orElseThrow(
                () ->
                    new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND));

    validateBidderAndExistingConfig(existingConfig, bidderConfig);

    Company company =
        companyRepository
            .findById(existingConfig.getCompanyPid())
            .orElseThrow(
                () -> {
                  log.error("Bidder Config's company pid passed is unknown");
                  return new GenevaValidationException(
                      ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_COMPANY);
                });
    validateBidderConfig(company, bidderConfig);
    bidderConfig.setCompany(company);

    // Handle Bidder Subscriptions
    if (!bidderConfig.getSubscriptions().isEmpty()) {
      Map<Long, ExternalDataProvider> dataProviders =
          externalDataProviderRepository.findAll().stream()
              .collect(Collectors.toMap(ExternalDataProvider::getPid, Function.identity()));
      for (SubscriptionInfo subscription : bidderConfig.getSubscriptions()) {
        if (!dataProviders.containsKey(subscription.getDataProviderPid())) {
          throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
        }

        // check for existing bidder subscriptions before adding
        BidderSubscription bs =
            getBidderSubscriptionByDataProvider(bidderConfig, subscription.getDataProviderPid());
        if (bs == null) {
          bs =
              new BidderSubscription(
                  dataProviders.get(subscription.getDataProviderPid()), bidderConfig);
          bidderConfig.getBidderSubscriptions().add(bs);
        }
        bs.setBidderAlias(subscription.getBidderAlias());
        bs.setRequiresDataToBid(subscription.isRequiresDataToBid());
      }
    }

    for (BidderDeviceType bidderDeviceType : bidderConfig.getAllowedDeviceTypes()) {
      if (!deviceTypeRepository.existsById(bidderDeviceType.getDeviceTypeId())) {
        log.error("Bidder Config contains invalid device types in its allowed device types list.");
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_ALLOWED_DEVICE_TYPE);
      }
    }

    // make sure bidder private attributes aren't updated/deleted (pr-1744)
    bidderConfig.setPrivateAttributes(existingConfig.getPrivateAttributes());

    // treat empty as "no change" to the exchange regionals mapping (pr-9005, pre-go-live behavior)
    if (bidderConfig.getExchangeRegionals() == null
        || bidderConfig.getExchangeRegionals().isEmpty()) {
      log.error("Bidder Config doesn't have any exchange regions set; this is an error.");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_NO_EXCHANGE_REGIONS);
    }
    return bidderConfigService.saveFlushAndRefreshBidderConfig(bidderConfig);
  }

  private void validateBidderAndExistingConfig(
      BidderConfig existingConfig, BidderConfig bidderConfig) {
    if (bidderConfig.getName() == null) {
      bidderConfig.setName(existingConfig.getName());
    }

    if (existingConfig.getCompanyPid().longValue() != bidderConfig.getCompanyPid().longValue()) {
      log.error(
          "Bidder Config's company pid passed does not belong to the bidder config being updated");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_COMPANY);
    }

    if (bidderConfig.getSmartQpsOutboundEnabled() == null) {
      bidderConfig.setSmartQpsOutboundEnabled(existingConfig.getSmartQpsOutboundEnabled());
    }

    if (bidderConfig.getThrottleRate() == null) {
      bidderConfig.setThrottleRate(existingConfig.getThrottleRate());
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerBuyer()")
  public void deleteBidderConfig(Long bidderConfigPid) {
    BidderConfig bidderConfig =
        bidderConfigRepository
            .findById(bidderConfigPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND));
    bidderConfigRepository.delete(bidderConfig);
  }

  @Override
  public List<ExchangeRegional> getAllExchangeRegions() {
    return exchangeRegionalRepository.findAll();
  }

  @Override
  public List<ExchangeProduction> getAllExchangeProductions() {
    return exchangeProductionRepository.findAll();
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public BuyerGroupDTO createBuyerGroup(Long companyPid, BuyerGroupDTO dto) {
    BuyerGroup entity = buyerGroupAssembler.transientEntity(companyPid, dto);
    return BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroupRepository.save(entity));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserNexage()")
  public List<BuyerGroupDTO> getAllBuyerGroupsForCompany(Long companyPid) {
    return buyerGroupRepository.findAllByCompanyPid(companyPid).stream()
        .map(BuyerGroupDTOMapper.MAPPER::manualMap)
        .collect(toList());
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserNexage()")
  public List<BuyerSeatDTO> getAllBuyerSeatsForCompanyAndName(
      Long companyPid, String name, Set<String> qf, String qt) {
    return buyerSeatRepository
        .findAll(
            BuyerSeatSpecification.withCompanyPidAndQueryFieldsAndSearchTerm(
                companyPid, name, qf, qt))
        .stream()
        .map(buyerSeatAssembler::make)
        .collect(toList());
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public BuyerGroupDTO updateBuyerGroup(Long companyPid, Long buyerGroupPid, BuyerGroupDTO dto) {
    BuyerGroup entity =
        buyerGroupRepository
            .findById(buyerGroupPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST));

    assertVersionMatch(dto.getVersion(), entity.getVersion());
    assertCompanyUnchanged(companyPid, entity.getCompany().getPid());

    entity = buyerGroupAssembler.apply(entity, dto);
    return BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroupRepository.saveAndFlush(entity));
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public BuyerSeatDTO updateBuyerSeat(Long companyPid, Long seatPid, BuyerSeatDTO dto) {
    BuyerSeat entity =
        buyerSeatRepository
            .findById(seatPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_ENTITY_NOT_EXIST));

    assertVersionMatch(dto.getVersion(), entity.getVersion());
    assertCompanyUnchanged(companyPid, entity.getCompany().getPid());

    entity = buyerSeatAssembler.apply(entity, dto);
    return buyerSeatAssembler.make(buyerSeatRepository.saveAndFlush(entity));
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public BuyerSeatDTO createBuyerSeat(Long companyPid, BuyerSeatDTO dto) {

    BuyerSeat entity = buyerSeatAssembler.transientEntity(companyPid, dto);
    return buyerSeatAssembler.make(buyerSeatRepository.save(entity));
  }

  void checkAndCreateLogoUploadDir() {
    File baseDir = new File(logoUploadDir);
    if (!baseDir.exists() && !baseDir.mkdirs()) {
      throw new InternalServerErrorException(
          String.format(
              "Logo directory path {%s} doesn't exist, creating one failed", logoUploadDir));
    }
  }

  private boolean updateLogo(AdSource adSource) {
    if (adSource.getLogoFileReference() == null) {
      return false;
    }

    AdSourceLogoFileReference logoFileReference = adSource.getLogoFileReference();

    if (StringUtils.isBlank(logoFileReference.getExtension())
        || logoFileReference.getData() == null
        || logoFileReference.getData().length == 0) {
      log.error("Invalid logo upload attempted for ad source [" + adSource.getPid() + "]");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_ADSOURCE_LOGO);
    }

    StringBuilder logoNameBuilder = new StringBuilder();
    if (environmentUtil.isAwsEnvironment()) {
      logoNameBuilder.append("logos/");
    }

    logoNameBuilder
        .append(adSource.getPid())
        .append("-")
        .append(System.currentTimeMillis())
        .append(".")
        .append(logoFileReference.getExtension());
    String logoName = logoNameBuilder.toString();

    writeLogoToDisc(logoName, logoFileReference.getData());

    adSource.setLogo(logoName);
    adSource.setLogoFileReference(null);

    return true;
  }

  private boolean isExchange(long adSourcePid) {
    return EXCHANGE_IDS.contains(adSourcePid);
  }

  private boolean isTestExchange(long adSourcePid) {
    return TEST_EXCHANGE_IDS.contains(adSourcePid);
  }

  private BidderSubscription getBidderSubscriptionByDataProvider(
      BidderConfig bidderConfig, Long dataProviderPid) {
    BidderSubscription bs = null;
    if (bidderConfig.getBidderSubscriptions() != null) {
      for (BidderSubscription subscription : bidderConfig.getBidderSubscriptions()) {
        if (subscription.getExternalDataProvider().getPid().equals(dataProviderPid)) {
          bs = subscription;
          break;
        }
      }
    }
    return bs;
  }

  private void validateBidderConfig(Company company, BidderConfig bidderConfig) {
    if (!company.getCurrency().equals(bidderConfig.getDefaultBidCurrency())) {
      log.error(
          "Bidder Config's bid currency should be {} which is set as Company pid={} currency",
          company.getCurrency(),
          company.getPid());
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_BID_CURRENCY);
    }
    validateBidderConfig(bidderConfig);
  }

  private void validateBidderConfig(long bidderConfigPid, BidderConfig bidderConfig) {
    if (bidderConfig.getPid() == null) {
      log.error("Bidder Config pid cannot be null when updating the bidder config");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    if (bidderConfig.getPid() != bidderConfigPid) {
      log.error("Pids don't match in the request URL and JSON");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    validateBidderConfig(bidderConfig);
  }

  private void validateAndSetAllowedContentEncodingTypes(BidderConfig bidderConfig) {
    if (StringUtils.isEmpty(bidderConfig.getAllowedContentEncoding())) {
      return;
    }

    Set<String> types =
        Arrays.asList(bidderConfig.getAllowedContentEncoding().split(",")).stream()
            .map(String::toUpperCase)
            .collect(Collectors.toSet());
    for (String type : types) {
      if (ContentEncodingType.fromString(type) == null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_CONTENT_ENCODING_TYPE);
      }
    }
    bidderConfig.setAllowedContentEncoding(Joiner.on(",").join(types));
  }

  private void validateBidderConfig(BidderConfig bidderConfig) {
    if (bidderConfig.getId() == null) {
      log.error("Bidder Config's id cannot be null");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    if (bidderConfig.getCompanyPid() == null) {
      log.error("Bidder Config's company pid cannot be null");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_COMPANY);
    }

    int allowedAuctionType = bidderConfig.getAuctionTypeFilter();
    if (!FilterAuction.isValidValue(allowedAuctionType)) {
      log.error("Bidder Config's Allowed auction Types can only be 0/1/2");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_AUCTIONTYPE);
    }

    if (!BidUnit.isValidValue(bidderConfig.getDefaultBidUnit())) {
      log.error("Bidder Config's unknown bid Price Unit");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_BID_PRICE_UNIT);
    }
    var bidderFormatOrdinal = bidderConfig.getFormatType().ordinal();
    if (bidderFormatOrdinal == BidderFormat.OpenRTBv2_3.ordinal()
        && bidderConfig.getDefaultBidUnit() != BidderFormat.OpenRTBv2.ordinal()) {
      log.error("Bidder Config's bid price units should be 0 (CPM) for Open RTB 2.1");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_BID_PRICE_UNIT_OPENRTBV2);
    }

    if (StringUtils.isNotEmpty(bidderConfig.getAllowedTraffic())) {
      String[] typeStr = bidderConfig.getAllowedTraffic().split(",");
      Set<Type> types = new LinkedHashSet<>();
      for (String typ : typeStr) {
        try {
          types.add(Type.valueOf(typ.toUpperCase()));
        } catch (IllegalArgumentException e) {
          throw new GenevaValidationException(
              ServerErrorCodes.SERVER_BIDDER_CONFIG_UNKNOWN_SITE_TYPE);
        }
      }
      bidderConfig.setAllowedTraffic(Joiner.on(",").join(types));
    }
    validateAndSetAllowedContentEncodingTypes(bidderConfig);

    validateTrafficFilterConfig(bidderConfig);
  }

  private List<Long> transformExchangeIds(List<String> exchangeIds) {
    return exchangeIds.stream().map(Long::valueOf).collect(toList());
  }

  private void assertCompanyUnchanged(Long requestCompanyPid, Long recordCompanyPid) {
    if (!Objects.equals(requestCompanyPid, recordCompanyPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_MUST_NOT_BE_CHANGED);
    }
  }

  private void assertVersionMatch(Integer dtoVersion, Integer recordVersion) {
    if (!Objects.equals(dtoVersion, recordVersion)) {
      throw new StaleStateException(
          "Data record has been updated since last loaded." + " Please reload it and try again");
    }
  }

  private void userIdPreferenceCheck(BidderConfig bidderConfig) {
    if (bidderConfig.getUserIdPreference() == null) {
      bidderConfig.setUserIdPreference(UserIdPreference.NO_ID_RESTRICTION);
    }
  }

  private String cleanCreativeIdHeaderName(String headerName) {
    if (StringUtils.isNotBlank(headerName)) {
      String trimmed = headerName.trim();
      if (trimmed.length() > AdSource.CRID_HEADER_FIELD_LENGTH_LIMIT) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_CRID_HEADER_FIELD_TOO_LONG);
      } else if (HttpUtil.httpHeaderNameContainsInvalidCharacters(trimmed)) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_CRID_HEADER_FIELD_INVALID_CHARACTERS);
      } else {
        return trimmed;
      }
    } else {
      return null;
    }
  }

  private void validateTrafficFilterConfig(BidderConfig bidderConfig) {
    Integer limit = bidderConfig.getRequestRateFilter();
    Double throttleRate = 0.0;
    boolean smartQpsOutboundEnabled = false;

    if (bidderConfig.getThrottleRate() != null) {
      throttleRate = bidderConfig.getThrottleRate();
    }

    if (bidderConfig.getSmartQpsOutboundEnabled() != null) {
      smartQpsOutboundEnabled = bidderConfig.getSmartQpsOutboundEnabled();
    }

    if (throttleRate < 0 || throttleRate > 1) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_THROTTLE_RATE);
    }

    if (smartQpsOutboundEnabled && limit == -1 && throttleRate == 0) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_INVALID_FILTER_CONFIG);
    }

    if (!smartQpsOutboundEnabled && throttleRate != 0) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_BIDDER_CONFIG_THROTTLE_RATE_NOT_ALLOWED);
    }
  }
}

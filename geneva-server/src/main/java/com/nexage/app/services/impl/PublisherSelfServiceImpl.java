package com.nexage.app.services.impl;

import static com.nexage.app.services.impl.PublisherSelfServiceUtil.getSitePositionByName;
import static com.nexage.app.services.impl.PublisherSelfServiceUtil.getSitePositionByPid;
import static com.nexage.app.services.impl.PublisherSelfServiceUtil.getTagPidsForPosition;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.MediaType;
import com.nexage.admin.core.enums.Owner;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.TagType;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.AdSource.DecisionMakerEnabled;
import com.nexage.admin.core.model.AdSource.SelfServeEnablement;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.pubselfserve.TagPubSelfServeView;
import com.nexage.admin.core.repository.AdSourceRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DealTermViewRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.repository.TagViewRepository;
import com.nexage.admin.core.sparta.jpa.model.DealTermView;
import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.TagPosition;
import com.nexage.admin.core.sparta.jpa.model.TagView;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.PositionArchiveTransactionDTO;
import com.nexage.app.dto.RtbProfileLibsAndTagsDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.Status;
import com.nexage.app.dto.publisher.PublisherAdSourceDefaultsDTO;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherHierarchyDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherSiteDealTermDTO;
import com.nexage.app.dto.publisher.PublisherTagControllerDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import com.nexage.app.dto.tag.TagArchiveTransactionDTO;
import com.nexage.app.dto.tag.TagDeploymentInfoDTO;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import com.nexage.app.dto.tag.TagUpdateInfoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.TagViewMapper;
import com.nexage.app.mapper.deal.DealTermViewMapper;
import com.nexage.app.mapper.site.SiteTypeMapper;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.BuyerService;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerSiteService;
import com.nexage.app.services.PlacementVideoService;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.services.SellerService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.SellerTagService;
import com.nexage.app.services.impl.limit.PositionLimitChecker;
import com.nexage.app.services.impl.limit.SiteLimitChecker;
import com.nexage.app.services.impl.limit.TagLimitChecker;
import com.nexage.app.services.publisher.ExternalPublisherCrudService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.PositionTrafficTypeValidator;
import com.nexage.app.util.PositionValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.RevenueUtils;
import com.nexage.app.util.assemblers.PublisherAdSourceDefaultsAssembler;
import com.nexage.app.util.assemblers.PublisherBuyerAssembler;
import com.nexage.app.util.assemblers.PublisherPositionAssembler;
import com.nexage.app.util.assemblers.PublisherSiteAssembler;
import com.nexage.app.util.assemblers.PublisherTagAssembler;
import com.nexage.app.util.assemblers.PublisherTierAssembler;
import com.nexage.app.util.assemblers.context.PublisherAdSourceDefaultsContext;
import com.nexage.app.util.assemblers.context.PublisherBuyerContext;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import com.nexage.app.util.assemblers.context.PublisherTagContext;
import com.nexage.app.util.assemblers.context.PublisherTierContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.server.report.performance.pss.facade.BiddersPerformanceFacadeImpl;
import com.ssp.geneva.server.report.performance.pss.facade.EstimatedRevenueFacadeImpl;
import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdNetworksForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdvertiserForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueForPubSelfServe;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleStateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service("publisherSelfService")
@Transactional
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() or "
        + "@loginUserContext.isOcUserSeller() or "
        + "@loginUserContext.isOcApiSeller()")
public class PublisherSelfServiceImpl implements PublisherSelfService {

  private static final int DCN_MAX_LENGTH = 32;

  // TODO: switch on validation on the core for the update operation (some tests will fail) and move
  // validation processing into it

  private static final String ADSOURCE_DEFAULTS_WARNING =
      "No AdSource defaults found for seller: {} and adsource: {}";

  private final LoginUserContext userContext;
  private final PublisherTierAssembler publisherTierAssembler;
  private final PublisherSiteAssembler publisherSiteAssembler;
  private final PublisherTagAssembler publisherTagAssembler;
  private final PublisherPositionAssembler publisherPositionAssembler;
  private final PublisherBuyerAssembler publisherBuyerAssembler;
  private final PublisherAdSourceDefaultsAssembler adsourceDefaultsAssembler;
  private final SiteRepository siteRepository;
  private final TagRepository tagRepository;
  private final TagViewRepository tagViewRepository;
  private final AdSourceRepository adSourceRepository;
  private final CompanyRepository companyRepository;
  private final PositionRepository positionRepository;
  private final DealTermViewRepository dealTermViewRepository;
  private final PositionValidator positionValidator;
  private final SellerService sellerService;
  private final SellerSiteService sellerSiteService;
  private final SellerPositionService sellerPositionService;
  private final SellerTagService sellerTagService;
  private final BuyerService buyerService;
  private final CompanyService companyService;
  private final ExternalPublisherCrudService externalPublisherCrudService;
  private final BiddersPerformanceFacadeImpl biddersPerformanceFacadeImpl;
  private final EstimatedRevenueFacadeImpl estimatedRevenueFacadeImpl;
  private final RTBProfileService rtbProfileService;
  private final HbPartnerSiteService hbPartnerSiteService;
  private final PlacementVideoService placementVideoService;
  private final BeanValidationService beanValidationService;
  private final RevenueShareUpdateValidator revenueShareUpdateValidator;
  private final PositionTrafficTypeValidator positionTrafficTypeValidator;
  private final TagLimitChecker tagLimitChecker;
  private final PositionLimitChecker positionLimitChecker;
  private final SiteLimitChecker siteLimitChecker;
  private final RTBProfileUtil rtbProfileUtil;

  public PublisherSelfServiceImpl(
      SiteRepository siteRepository,
      BuyerService buyerService,
      SellerService sellerService,
      SellerSiteService sellerSiteService,
      PublisherAdSourceDefaultsAssembler adsourceDefaultsAssembler,
      RTBProfileService rtbProfileService,
      HbPartnerSiteService hbPartnerSiteService,
      SellerPositionService sellerPositionService,
      CompanyService companyService,
      SellerTagService sellerTagService,
      CompanyRepository companyRepository,
      TagViewRepository tagViewRepository,
      @Qualifier("estimatedRevenueFacade") EstimatedRevenueFacadeImpl estimatedRevenueFacadeImpl,
      PlacementVideoService placementVideoService,
      PositionTrafficTypeValidator positionTrafficTypeValidator,
      DealTermViewRepository dealTermViewRepository,
      LoginUserContext userContext,
      PublisherTierAssembler publisherTierAssembler,
      PositionValidator positionValidator,
      PositionRepository positionRepository,
      PublisherSiteAssembler publisherSiteAssembler,
      AdSourceRepository adSourceRepository,
      PublisherTagAssembler publisherTagAssembler,
      PublisherPositionAssembler publisherPositionAssembler,
      RevenueShareUpdateValidator revenueShareUpdateValidator,
      ExternalPublisherCrudService externalPublisherCrudService,
      @Qualifier("biddersPerformanceFacade")
          BiddersPerformanceFacadeImpl biddersPerformanceFacadeImpl,
      PublisherBuyerAssembler publisherBuyerAssembler,
      BeanValidationService beanValidationService,
      TagRepository tagRepository,
      TagLimitChecker tagLimitChecker,
      PositionLimitChecker positionLimitChecker,
      SiteLimitChecker siteLimitChecker,
      RTBProfileUtil rtbProfileUtil) {
    this.siteRepository = siteRepository;
    this.buyerService = buyerService;
    this.sellerService = sellerService;
    this.sellerSiteService = sellerSiteService;
    this.adsourceDefaultsAssembler = adsourceDefaultsAssembler;
    this.rtbProfileService = rtbProfileService;
    this.hbPartnerSiteService = hbPartnerSiteService;
    this.sellerPositionService = sellerPositionService;
    this.companyService = companyService;
    this.sellerTagService = sellerTagService;
    this.companyRepository = companyRepository;
    this.tagViewRepository = tagViewRepository;
    this.estimatedRevenueFacadeImpl = estimatedRevenueFacadeImpl;
    this.placementVideoService = placementVideoService;
    this.positionTrafficTypeValidator = positionTrafficTypeValidator;
    this.dealTermViewRepository = dealTermViewRepository;
    this.userContext = userContext;
    this.publisherTierAssembler = publisherTierAssembler;
    this.positionValidator = positionValidator;
    this.positionRepository = positionRepository;
    this.publisherSiteAssembler = publisherSiteAssembler;
    this.adSourceRepository = adSourceRepository;
    this.publisherTagAssembler = publisherTagAssembler;
    this.publisherPositionAssembler = publisherPositionAssembler;
    this.revenueShareUpdateValidator = revenueShareUpdateValidator;
    this.externalPublisherCrudService = externalPublisherCrudService;
    this.biddersPerformanceFacadeImpl = biddersPerformanceFacadeImpl;
    this.publisherBuyerAssembler = publisherBuyerAssembler;
    this.beanValidationService = beanValidationService;
    this.tagRepository = tagRepository;
    this.tagLimitChecker = tagLimitChecker;
    this.positionLimitChecker = positionLimitChecker;
    this.siteLimitChecker = siteLimitChecker;
    this.rtbProfileUtil = rtbProfileUtil;
  }

  @Override
  @Transactional(readOnly = true)
  public PublisherSiteDTO getSite(long pid, boolean detail) {
    Site siteDTO = sellerSiteService.getSite(pid);
    validateSelfServeEnabled(siteDTO.getCompanyPid());
    return publisherSiteAssembler.make(siteDTO, detail);
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "@loginUserContext.isPublisherSelfServeEnabled(#publisherPid) and "
          + "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcApiSeller())")
  public List<PublisherSiteDTO> getSites(long publisherPid, boolean detail) {
    List<PublisherSiteDTO> publisherSites = new ArrayList<>();

    if (companyRepository.countByPid(publisherPid) == 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }

    List<Site> sites =
        siteRepository.findByCompanyPidWithStatusNotDeletedAndSiteNotRestricted(
            userContext.getPid(), publisherPid);

    for (Site site : sites) {
      rtbProfileUtil.populateRTBProfileLibraryPids(site);
      publisherSites.add(publisherSiteAssembler.make(site, detail));
    }

    return publisherSites;
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() or @loginUserContext.isOcAdminSeller() "
          + " or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller()")
  public PublisherSiteDTO createSite(
      long publisher, PublisherSiteDTO publisherSiteDto, boolean detail) {
    siteLimitChecker.checkLimitsSite(publisher);
    Company company = companyService.getCompany(publisher);
    checkRevenueShareDetailsEditPermission(company, publisherSiteDto.getCurrentDealTerm());
    Site site = publisherSiteAssembler.apply(publisher, new Site(), publisherSiteDto);
    site.setDefaultRtbProfile(
        rtbProfileService.processDefaultRtbProfile(
            site,
            company,
            publisherSiteDto.getDefaultRtbProfile(),
            site.getDefaultRtbProfile(),
            detail));

    String siteDcn = publisherSiteDto.getDcn();
    if (StringUtils.isNotBlank(siteDcn)) {
      setDCNOnSiteDTO(publisherSiteDto, site, siteDcn);
    }
    site = sellerSiteService.createSite(publisher, site);
    return publisherSiteAssembler.make(site, detail);
  }

  private void setDCNOnSiteDTO(PublisherSiteDTO publisherSite, Site siteDTO, String siteDcn) {
    if (userContext.isNexageAdminOrManager()) {
      validateDCN(siteDcn);
      siteDTO.setDcn(publisherSite.getDcn());
    } else if (!User.Role.ROLE_USER.equals(userContext.getCurrentUser().getRole())) {
      throw new GenevaSecurityException(ServerErrorCodes.SERVER_SITE_DCN_READONLY);
    }
  }

  private void validateDCN(String siteDcn) {
    if (siteDcn.length() > DCN_MAX_LENGTH) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_DCN_MAX_LENGTH);
    }
    if (siteRepository.existsByDcn(siteDcn)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_DCN_DUPLICATE);
    }
  }

  private void checkRevenueShareDetailsEditPermission(
      Company company, PublisherSiteDealTermDTO currentDealTerm) {
    if (currentDealTerm != null) {
      if (revenueShareUpdateValidator.isRevenueShareUpdate(
              company.getSellerAttributes(), currentDealTerm)
          && !userContext.isOcManagerYieldNexage()) {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  private void checkRevenueShareDetailsEditPermission(
      SiteDealTerm siteDealTerm, PublisherSiteDealTermDTO currentDealTerm) {
    if (currentDealTerm != null) {
      if (revenueShareUpdateValidator.isRevenueShareUpdate(siteDealTerm, currentDealTerm)
          && !userContext.isOcManagerYieldNexage()) {
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() or @loginUserContext.isOcAdminSeller() "
          + "or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcApiSeller() "
          + "or @loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
  public PublisherSiteDTO updateSite(
      long publisher, PublisherSiteDTO publisherSiteDto, boolean detail) {
    Site site = sellerSiteService.getSite(publisherSiteDto.getPid());
    sellerSiteService.validateSiteNameUniqueness(
        publisherSiteDto.getPid(), publisher, publisherSiteDto.getName());
    SiteDealTerm siteDealTerm = site.getCurrentDealTerm();
    checkRevenueShareDetailsEditPermission(siteDealTerm, publisherSiteDto.getCurrentDealTerm());
    site.setCurrentDealTerm(siteDealTerm);
    if (!site.getVersion().equals(publisherSiteDto.getVersion())) {
      // throw stale data exception
      throw new StaleStateException(
          "PublisherSite has a different version of the data siteVersion="
              + site.getVersion()
              + "; pubSiteVersion="
              + publisherSiteDto.getVersion());
    }
    RTBProfile rtbProfile =
        rtbProfileService.processDefaultRtbProfile(
            site,
            site.getCompany(),
            publisherSiteDto.getDefaultRtbProfile(),
            site.getDefaultRtbProfile(),
            detail);
    site.setDefaultRtbProfile(rtbProfile);
    hbPartnerSiteService.validateHbPartnerAssociations(site, publisherSiteDto);

    site = publisherSiteAssembler.apply(publisher, site, publisherSiteDto);

    sellerSiteService.adjustTagReservesForSiteUpdate(site);
    site = sellerSiteService.updateSite(site);
    return publisherSiteAssembler.make(site, detail);
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserNexage() "
          + "or @loginUserContext.isOcManagerYieldNexage() or @loginUserContext.isOcAdminSeller() "
          + " or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller()")
  public SiteUpdateInfoDTO siteUpdateInfo(
      long publisher, PublisherSiteDTO publisherSite, boolean detail) {
    Site siteDTO = updateSiteData(publisher, publisherSite, detail);
    Set<TagUpdateInfoDTO> tagUpdateInfos =
        new HashSet<>(sellerSiteService.adjustTagReservesForSiteUpdate(siteDTO));

    SiteUpdateInfoDTO.Builder builder = new SiteUpdateInfoDTO.Builder();
    builder
        .setTagUpdateInfo(tagUpdateInfos)
        .setTxId(sellerSiteService.calcHashForSiteUpdate(tagUpdateInfos));

    return builder.build();
  }

  private Site updateSiteData(long publisher, PublisherSiteDTO publisherSiteDto, boolean detail) {
    Site site = sellerSiteService.getSite(publisherSiteDto.getPid());

    if (!site.getVersion().equals(publisherSiteDto.getVersion())) {
      // throw stale data exception
      throw new StaleStateException(
          "PublisherSite has a different version of the data siteVersion="
              + site.getVersion()
              + "; publisherSiteDtoVersion="
              + publisherSiteDto.getVersion());
    }

    SiteDealTerm siteDealTerm = site.getCurrentDealTerm();
    checkRevenueShareDetailsEditPermission(siteDealTerm, publisherSiteDto.getCurrentDealTerm());
    site.setCurrentDealTerm(siteDealTerm);
    rtbProfileService.processDefaultRtbProfile(
        site,
        site.getCompany(),
        publisherSiteDto.getDefaultRtbProfile(),
        site.getDefaultRtbProfile(),
        detail);
    return publisherSiteAssembler.apply(publisher, site, publisherSiteDto);
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public void deleteSite(long publisher, long site) {
    sellerSiteService.deleteSite(site);
  }

  /** {@inheritDoc} */
  @Override
  public PublisherPositionDTO getPosition(
      long publisherPid, long sitePid, long positionPid, boolean detail) {
    Site site = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);
    validateSelfServeEnabled(site.getCompanyPid());
    PublisherPositionContext context =
        PublisherPositionContext.newBuilder().withSite(site).withDetail(detail).build();

    Position position = getSitePositionByPid(site, positionPid);

    PlacementVideoDTO placementVideoDTO = placementVideoService.getPlacementVideo(positionPid);

    position.setLongform(placementVideoDTO != null && placementVideoDTO.isLongform());

    return populatePlacementVideoForVideoPlacements(
        publisherPositionAssembler.make(context, position), placementVideoDTO);
  }

  /** {@inheritDoc} */
  @Override
  public List<PublisherPositionDTO> getPositions(long publisherPid, long sitePid, boolean detail) {
    List<PublisherPositionDTO> publisherPositions = new ArrayList<>();
    Site site = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);
    validateSelfServeEnabled(site.getCompanyPid());
    PublisherPositionContext context =
        PublisherPositionContext.newBuilder().withDetail(detail).withSite(site).build();

    Set<Position> positions = site.getPositions();
    for (Position position : positions) {
      PlacementVideoDTO placementVideoDTO =
          placementVideoService.getPlacementVideo(position.getPid());
      position.setLongform(placementVideoDTO != null && placementVideoDTO.isLongform());
      publisherPositions.add(
          populatePlacementVideoForVideoPlacements(
              publisherPositionAssembler.make(context, position), placementVideoDTO));
    }

    return publisherPositions;
  }

  /** {@inheritDoc} */
  @Override
  public PublisherPositionDTO createPosition(
      long site, PublisherPositionDTO publisherPosition, boolean detail) {
    Site siteDTO = sellerSiteService.getSite(site);
    if (publisherPosition.getSite() == null) {
      publisherPosition.setSite(new PublisherSiteDTO());
    }
    publisherPosition.getSite().setType(SiteTypeMapper.MAPPER.map(siteDTO.getType()));
    validateSelfServeEnabled(siteDTO.getCompanyPid());
    beanValidationService.validate(publisherPosition, CreateGroup.class, Default.class);

    PublisherPositionContext context =
        PublisherPositionContext.newBuilder().withSite(siteDTO).build();

    PlacementVideoDTO placementVideoDTO = publisherPosition.getPlacementVideo();

    placementVideoDTO =
        placementVideoService.populateVideoData(placementVideoDTO, publisherPosition);

    if (isLongformVideoPlacement(publisherPosition)) {
      placementVideoDTO.setLongform(publisherPosition.isLongform());
      placementVideoDTO.setMultiImpressionBid(false);
      placementVideoDTO.setCompetitiveSeparation(false);
      placementVideoDTO.setPlayerHeight(300);
      placementVideoDTO.setPlayerWidth(300);
      placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
      placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
      publisherPosition.setPlacementVideo(placementVideoDTO);
    }

    Position position =
        publisherPositionAssembler.apply(context, new Position(), publisherPosition);

    position.setDefaultRtbProfile(
        rtbProfileService.processDefaultRtbProfile(
            siteDTO,
            siteDTO.getCompany(),
            publisherPosition.getDefaultRtbProfile(),
            position.getDefaultRtbProfile(),
            detail));

    if (StringUtils.isBlank(position.getName())) {
      position.setName(new UUIDGenerator().generateUniqueId());
      publisherPosition.setName(position.getName());
    }

    if (StringUtils.isBlank(position.getPositionAliasName())) {
      position.setPositionAliasName(new UUIDGenerator().generateUniqueId());
      publisherPosition.setPositionAliasName(position.getPositionAliasName());
    }

    sellerPositionService.createPosition(site, position);
    // refresh the site to ensure we get the correct version
    siteDTO = sellerSiteService.getSite(site);
    context = PublisherPositionContext.newBuilder().withSite(siteDTO).withDetail(detail).build();

    Position savedPosition = getSitePositionByName(siteDTO, publisherPosition.getName());
    PublisherPositionDTO savedPublisherPositionDTO =
        publisherPositionAssembler.make(context, savedPosition);

    if (isLongformVideoPlacement(publisherPosition)) {
      PlacementVideoDTO savedPlacementVideoDTO =
          placementVideoService.save(placementVideoDTO, savedPosition.getPid());
      savedPublisherPositionDTO.setPlacementVideo(savedPlacementVideoDTO);
    }

    return savedPublisherPositionDTO;
  }

  private PublisherPositionDTO populatePlacementVideoForVideoPlacements(
      PublisherPositionDTO positionDTO, PlacementVideoDTO placementVideoDTO) {

    if (isLongformVideoPlacement(positionDTO)) {
      positionDTO.setPlacementVideo(placementVideoDTO);
    }

    return positionDTO;
  }

  private void isNameDuplicate(Site destSite, PublisherPositionDTO publisherPosition) {
    for (Position pos : destSite.getPositions()) {
      if (pos.getName().equals(publisherPosition.getName())) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_POSITION_NAME);
      }
    }
  }

  private HashMap<Long, PublisherTagDTO> getPubTagsMap(PublisherPositionDTO publisherPosition) {
    HashMap<Long, PublisherTagDTO> pubTags = new HashMap<>();
    HashSet<String> pubTagNames = new HashSet<>();
    if (publisherPosition.getTags() != null) {
      for (PublisherTagDTO pTag : publisherPosition.getTags()) {
        pubTags.put(pTag.getPid(), pTag);

        // pubTagNames is used to validate that every tag/buyerPid combination is unique

        pubTagNames.add(pTag.getName().concat(pTag.getBuyer().getPid().toString()));
      }
      if (pubTagNames.size() != pubTags.size()) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_TAG_NAME);
      }
      publisherPosition.removeTags();
    }
    log.debug("total number of tags to be copied: {}", pubTags.size());
    return pubTags;
  }

  private List<PublisherTierDTO> getPubTiers(PublisherPositionDTO publisherPosition) {
    List<PublisherTierDTO> pubTiers = new LinkedList<>();
    if ((publisherPosition.getTiers() != null && !publisherPosition.getTiers().isEmpty())) {
      pubTiers.addAll(publisherPosition.getTiers());
      publisherPosition.removeTiers();
    }
    log.debug("total number of tiers to be copied: {}", pubTiers.size());
    return pubTiers;
  }

  private PublisherTierDTO constructPubTier(PublisherTierDTO pTier) {
    return PublisherTierDTO.newBuilder()
        .withIsAutogenerated(pTier.isAutogenerated())
        .withLevel(pTier.getLevel())
        .withName(pTier.getName())
        .withOrderStrategy(pTier.getOrderStrategy())
        .withPid(null)
        .withVersion(0)
        .build();
  }

  private void cloneTier(Site destSite, Position newPosition, PublisherTierDTO publisherTier) {
    PublisherTierContext context =
        PublisherTierContext.newBuilder().withSite(destSite).withPosition(newPosition).build();

    Tier tier = publisherTierAssembler.apply(context, newPosition.newTier(), publisherTier);
    if (publisherTier.getLevel() != null) {
      tier.setLevel(publisherTier.getLevel());
    } else {
      tier.setLevel(0);
    }

    destSite = sellerPositionService.updatePosition(newPosition);
    // refresh the site to ensure we get the correct version
    destSite = sellerSiteService.getSite(destSite.getPid());
    newPosition = getSitePositionByPid(destSite, newPosition.getPid());

    context =
        PublisherTierContext.newBuilder().withSite(destSite).withPosition(newPosition).build();

    publisherTierAssembler.make(context, tier);
  }

  /*
   * This method creates a copy of position object graphs that contains - tags (rtb, mediation), tag rules, tiers and returns
   * PublisherPosition JSON.
   *
   * @param publisherPid       : pss company id
   * @param srcSitePid         : site pid that contains position to be copied
   * @param srcPositionPid     : position pid to be copied
   * @param targetSitePid      : Could be different OR same as srcSitePid, if the position is copied with the source site.
   * @param publisherPosition  : PublisherPosition JSON with user edited information to be persisted
   * @return PublisherPosition : PublisherPosition JSON returned to client
   * @see com.nexage.app.services.PublisherSelfService#copyPosition(long, long, long, java.lang.Long, com.nexage.app.dto.PublisherPosition)
   */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcAdminSeller() or  @loginUserContext.isOcManagerSeller()")
  public PublisherPositionDTO copyPosition(
      long publisherPid,
      long srcSitePid,
      long srcPositionPid,
      Long targetSitePid,
      PublisherPositionDTO publisherPosition) {

    positionLimitChecker.checkLimitsPositionsInSite(publisherPid, targetSitePid);

    Site srcSite = sellerSiteService.getSite(srcSitePid);
    Site destSite = srcSite;
    if (!targetSitePid.equals(srcSite.getPid())) {
      destSite = sellerSiteService.getSite(targetSitePid);
    }

    if (StringUtils.isBlank(publisherPosition.getName())) {
      publisherPosition.setName(new UUIDGenerator().generateUniqueId());
    }

    publisherPosition.setPositionAliasName(new UUIDGenerator().generateUniqueId());

    isNameDuplicate(destSite, publisherPosition);
    PublisherPositionContext context =
        PublisherPositionContext.newBuilder().withSite(destSite).build();
    HashMap<Long, PublisherTagDTO> pubTagsMap = getPubTagsMap(publisherPosition);
    List<PublisherTierDTO> pubTiers = getPubTiers(publisherPosition);

    pubTiers.sort(Comparator.comparing(PublisherTierDTO::getLevel));
    PlacementVideoDTO placementVideoDTO = publisherPosition.getPlacementVideo();

    if (publisherPosition.getPlacementCategory() != null
        && publisherPosition.getPlacementCategory().equals(PlacementCategory.INSTREAM_VIDEO)) {
      placementVideoDTO =
          placementVideoService.populateVideoData(placementVideoDTO, publisherPosition);
    }

    var copyPosition = new Position();
    BeanUtils.copyProperties(
        positionRepository
            .findById(srcPositionPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS)),
        copyPosition);
    copyPosition.setPid(null);
    copyPosition.setVersion(null);
    copyPosition.setTiers(new ArrayList<>());
    copyPosition.setHbPartnerPosition(new HashSet<>());

    var position = publisherPositionAssembler.apply(context, copyPosition, publisherPosition);

    position.setMraidAdvancedTracking(
        publisherPosition.getMraidAdvancedTracking() != null
            && publisherPosition.getMraidAdvancedTracking());

    sellerPositionService.createPosition(targetSitePid, position);
    // refresh the site to ensure we get the correct version
    destSite = sellerSiteService.getSite(targetSitePid);

    long newPositionPid = -1L;
    Position newPosition = new Position();
    for (Position p : destSite.getPositions()) {
      if (p.getName().equals(publisherPosition.getName())) {
        newPositionPid = p.getPid();
        newPosition = p;
        break;
      }
    }
    log.debug("Newly copied Position pid: {}", newPositionPid);

    // cycle through the tiers and for each tag in the tier , create it and then the tier and so on
    // so forth.
    for (PublisherTierDTO pTier : pubTiers) {
      PublisherTierDTO newPubTier = constructPubTier(pTier);
      boolean tagsInTier =
          tierContainsTags(
              publisherPid,
              srcSitePid,
              targetSitePid,
              pubTagsMap,
              newPositionPid,
              pTier,
              newPubTier);
      if (tagsInTier) {
        cloneTier(destSite, newPosition, newPubTier);
        log.debug("Clone tier to assign newly copied {} tags", newPubTier.getTags().size());
      }
    }

    // create unassigned tags
    log.debug("copy remaining {} unassigned tags", pubTagsMap.size());
    for (PublisherTagDTO pTag : pubTagsMap.values()) {
      long tagPidInTier = pTag.getPid();
      PublisherTagDTO pt = pTag.copy(pTag);
      cloneTag(
          publisherPid,
          srcSitePid,
          newPositionPid,
          tagPidInTier,
          pt,
          targetSitePid,
          newPositionPid);
    }

    context =
        PublisherPositionContext.newBuilder().withSite(destSite).withCopyOperation(true).build();

    Position savedPosition = getSitePositionByName(destSite, publisherPosition.getName());
    PublisherPositionDTO savedPublisherPositionDTO =
        publisherPositionAssembler.make(context, savedPosition, null);

    if (Objects.nonNull(placementVideoDTO)) {
      placementVideoDTO.setVersion(null);
      placementVideoDTO.setPid(null);
      PlacementVideoDTO savedPlacementVideoDTO =
          placementVideoService.save(placementVideoDTO, savedPosition.getPid());
      savedPublisherPositionDTO.setPlacementVideo(savedPlacementVideoDTO);
    }
    return savedPublisherPositionDTO;
  }

  private boolean tierContainsTags(
      long publisherPid,
      long srcSitePid,
      Long targetSitePid,
      HashMap<Long, PublisherTagDTO> pubTagsMap,
      long newPositionPid,
      PublisherTierDTO pTier,
      PublisherTierDTO newPubTier) {
    boolean tagsInTier = false;
    if (CollectionUtils.isNotEmpty(pTier.getTags())) {
      for (PublisherTagDTO pTag : pTier.getTags()) {
        if (pubTagsMap.containsKey(pTag.getPid())) {
          tagsInTier = true;
          long tagPidInTier = pTag.getPid();
          PublisherTagDTO pubTag = pubTagsMap.get(tagPidInTier);
          pubTagsMap.remove(tagPidInTier);

          pubTag = pubTag.copy(pubTag);
          PublisherTagDTO clonedPubTag =
              cloneTag(
                  publisherPid,
                  srcSitePid,
                  newPositionPid,
                  tagPidInTier,
                  pubTag,
                  targetSitePid,
                  newPositionPid);

          // create tier in which the newly cloned tag resides
          if (clonedPubTag != null) {
            newPubTier.addTag(clonedPubTag);
          }
        }
      }
    }
    return tagsInTier;
  }

  /** {@inheritDoc} */
  @Override
  public PublisherPositionDTO updatePosition(
      long publisherPid, long sitePid, PublisherPositionDTO publisherPosition, boolean detail) {
    Site site = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);
    validateSelfServeEnabled(site.getCompanyPid());
    if (publisherPosition.getSite() == null) {
      publisherPosition.setSite(new PublisherSiteDTO());
    }
    publisherPosition.getSite().setType(SiteTypeMapper.MAPPER.map(site.getType()));
    beanValidationService.validate(publisherPosition, UpdateGroup.class);
    PublisherPositionContext context =
        PublisherPositionContext.newBuilder()
            .withDetail(detail)
            .withSite(site)
            .build(); // heed to add for the context

    PlacementVideoDTO placementVideoDTO = publisherPosition.getPlacementVideo();
    boolean insertPlacementVideo = false;
    boolean deletePlacementVideo = false;

    Position position = getSitePositionByPid(site, publisherPosition.getPid());
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.getPlacementVideo(publisherPosition.getPid());
    boolean placementHasExistingSavedPlacementVideo = savedPlacementVideoDTO != null;

    if (needToDeletePlacementVideo(publisherPosition, placementHasExistingSavedPlacementVideo)) {
      position.setVideoLinearity(null);
      placementVideoDTO = null;
      deletePlacementVideo = true;
    } else if (needToCreateNewPlacementVideo(
        publisherPosition, placementHasExistingSavedPlacementVideo)) {
      insertPlacementVideo = true;
    }

    if (isLongformVideoPlacement(publisherPosition)) {
      publisherPosition.setPlacementVideo(savedPlacementVideoDTO);
      savedPlacementVideoDTO =
          placementVideoService.populateVideoData(savedPlacementVideoDTO, publisherPosition);
      savedPlacementVideoDTO.setLongform(publisherPosition.isLongform());
      savedPlacementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
      savedPlacementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    }

    if (isLongformVideoPlacement(publisherPosition)) {
      validateUpdatePlacementVideo(placementVideoDTO);
      placementVideoDTO =
          placementVideoService.populateVideoData(placementVideoDTO, publisherPosition);
    }

    positionValidator.validateVersion(position, publisherPosition.getVersion());
    positionValidator.validateName(publisherPosition);

    if (StringUtils.isBlank(publisherPosition.getPositionAliasName())) {
      publisherPosition.setPositionAliasName(new UUIDGenerator().generateUniqueId());
    }

    positionValidator.validatePublisherPositionAliasName(publisherPosition);

    positionTrafficTypeValidator.validatePositionTiers(
        publisherPosition.getTiers(), site, position);

    position.setDefaultRtbProfile(
        rtbProfileService.processDefaultRtbProfile(
            site,
            site.getCompany(),
            publisherPosition.getDefaultRtbProfile(),
            position.getDefaultRtbProfile(),
            detail));

    position =
        publisherPositionAssembler.apply(
            context,
            position,
            publisherPosition); // here set to the position assembler, details not used!

    sellerPositionService.updatePosition(position);
    // refresh the site to ensure we get the correct version
    site = sellerSiteService.getSite(sitePid);
    context = PublisherPositionContext.newBuilder().withSite(site).withDetail(detail).build();

    Position savedPosition = getSitePositionByPid(site, publisherPosition.getPid());

    PublisherPositionDTO savedPublisherPositionDTO =
        publisherPositionAssembler.make(context, savedPosition);

    boolean instream = false;
    if (isLongformVideoPlacement(publisherPosition)) {
      instream = true;
      PlacementVideoDTO newPlacementVideoDTO =
          placementVideoService.update(
              savedPlacementVideoDTO, savedPosition.getPid(), insertPlacementVideo);
      savedPublisherPositionDTO.setPlacementVideo(newPlacementVideoDTO);
    }

    if (Objects.nonNull(placementVideoDTO) && !instream) {
      savedPublisherPositionDTO.setPlacementVideo(
          placementVideoService.update(
              placementVideoDTO, savedPosition.getPid(), insertPlacementVideo));
    } else if (deletePlacementVideo) {
      placementVideoService.delete(savedPlacementVideoDTO);
    }
    return savedPublisherPositionDTO;
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#publisherPid) or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcUserSeller()")
  public PositionArchiveTransactionDTO getPositionPerformanceMetrics(
      long publisherPid, long sitePid, long positionPid, boolean withTransaction) {
    Site site = validateRequestParamsForPosition(publisherPid, sitePid, positionPid);

    Set<Long> tagPidSet = getTagPidsForPosition(site, positionPid, false);

    List<TagPerformanceMetricsDTO> performanceMetrics =
        sellerTagService.getTagPerformanceMetrics(sitePid, tagPidSet);
    String transactionId =
        withTransaction ? DigestUtils.md5Hex(String.valueOf(performanceMetrics.hashCode())) : null;

    PositionArchiveTransactionDTO.Builder builder =
        new PositionArchiveTransactionDTO.Builder()
            .withPerformanceMetrics(performanceMetrics)
            .withTransaction(transactionId);
    return builder.build();
  }

  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#publisherPid) or @loginUserContext.isOcManagerNexage() "
          + "or @loginUserContext.isOcUserSeller()")
  public PositionArchiveTransactionDTO getPositionPerformanceMetricsForArchive(
      long publisherPid, long sitePid, long positionPid, boolean withTransaction) {
    Site site = validateRequestParamsForPosition(publisherPid, sitePid, positionPid);
    Position position = PublisherSelfServiceUtil.getSitePositionByPid(site, positionPid);

    Set<Long> tagPidsAssigned = getTagPidsForPosition(site, positionPid, true);
    Set<Long> tagPidsAll = getTagPidsForPosition(site, positionPid, false);

    List<TagPerformanceMetricsDTO> performanceMetricAssignedTags =
        sellerTagService.getTagPerformanceMetrics(sitePid, tagPidsAssigned);
    List<TagPerformanceMetricsDTO> performanceMetricAllTags =
        sellerTagService.getTagPerformanceMetrics(sitePid, tagPidsAll);
    String transactionId =
        withTransaction
            ? DigestUtils.md5Hex(String.valueOf(performanceMetricAllTags.hashCode()))
            : null;

    PositionArchiveTransactionDTO.Builder builder =
        new PositionArchiveTransactionDTO.Builder()
            .withPerformanceMetrics(
                position.getTrafficType() == TrafficType.SMART_YIELD
                    ? performanceMetricAssignedTags
                    : performanceMetricAllTags)
            .withTransaction(transactionId);
    return builder.build();
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherSiteDTO archivePosition(
      long publisherPid, long sitePid, long positionPid, String transactionId) {
    if (userContext.getCurrentUser().getRole() != User.Role.ROLE_API) {
      validatePositionTransactionId(publisherPid, sitePid, positionPid, transactionId);
    }
    Site site = validateRequestParamsForPosition(publisherPid, sitePid, positionPid);

    Site siteUpdated = sellerPositionService.archivePosition(site, positionPid);
    return publisherSiteAssembler.make(siteUpdated, false);
  }

  @Override
  public List<PublisherTagDTO> getTags(long publisherPid, long sitePid, long positionPid) {
    List<PublisherTagDTO> publisherTags = new ArrayList<>();
    var simpleTags =
        tagViewRepository.findBySitePidAndPositionPidAndStatusNotIn(
            sitePid, positionPid, List.of(com.nexage.admin.core.enums.Status.DELETED));
    var tagPids = simpleTags.stream().map(TagView::getPid).collect(Collectors.toList());
    log.info("Tag PIDs: {}", tagPids);
    var dealTerms =
        dealTermViewRepository.findBySitePidAndTagPidInOrderByEffectiveDateDesc(sitePid, tagPids);
    log.info("Deal terms: {}", dealTerms);

    Site site = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);
    validateSelfServeEnabled(site.getCompanyPid());
    PublisherTagContext context = PublisherTagContext.newBuilder().withSite(site).build();

    simpleTags.forEach(
        st -> {
          var tag = TagViewMapper.MAPPER.map(st);
          tag.setSite(site);
          setDealTermForTag(dealTerms, tag, sitePid);
          if (tag.getPosition() != null && tag.getPosition().getPid().equals(positionPid)) {
            publisherTags.add(publisherTagAssembler.make(context, tag));
          }
        });

    return publisherTags;
  }

  @Override
  public PublisherTagDTO getTag(long publisherPid, long sitePid, long positionPid, long tagPid) {
    var simpleTag =
        tagViewRepository
            .findByPidAndSitePidAndPositionPidAndStatusNotIn(
                tagPid, sitePid, positionPid, List.of(com.nexage.admin.core.enums.Status.DELETED))
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND));
    var tag = TagViewMapper.MAPPER.map(simpleTag);
    var dealTerms =
        dealTermViewRepository.findBySitePidAndTagPidInOrderByEffectiveDateDesc(
            sitePid, List.of(tag.getPid()));
    log.info("Deal terms: {}", dealTerms);
    Site site = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);
    validateSelfServeEnabled(site.getCompanyPid());
    PublisherTagContext context = PublisherTagContext.newBuilder().withSite(site).build();
    tag.setSite(site);

    setDealTermForTag(dealTerms, tag, sitePid);

    return publisherTagAssembler.make(context, tag);
  }

  private void setDealTermForTag(List<DealTermView> dealTerms, Tag tag, Long sitePid) {
    var tagDealTerm =
        dealTerms.stream()
            .filter(dt -> dt.getSitePid().equals(sitePid) && dt.getTagPid().equals(tag.getPid()))
            .findFirst();
    if (tagDealTerm.isPresent()) {
      log.info("Setting deal term: {}", tagDealTerm.get());
      tag.setCurrentDealTerm(DealTermViewMapper.MAPPER.map(tagDealTerm.get()));
    }
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerYieldNexage() "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#publisher) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()))")
  public PublisherTagDTO createTag(
      long publisher, long site, long position, PublisherTagDTO publisherTag) {

    tagLimitChecker.checkLimitsTagsInPosition(publisher, site, position);

    Site siteDTO = sellerSiteService.getSite(site);
    SiteDealTerm siteDealTerm = siteDTO.getCurrentDealTerm();
    checkRevenueShareDetailsEditPermission(siteDealTerm, publisherTag.getCurrentDealTerm());
    siteDTO.setCurrentDealTerm(siteDealTerm);
    PublisherTagContext context = PublisherTagContext.newBuilder().withSite(siteDTO).build();

    Tag tag = publisherTagAssembler.apply(context, new Tag(), publisherTag);
    if (tag.getClickthroughDisable() == null) {
      tag.setClickthroughDisable(false);
    }
    if (tag.getReturnRawResponse() == null) {
      tag.setReturnRawResponse(false);
    }

    // ensure that the position exists
    Position pos = getSitePositionByPid(siteDTO, position);

    // pub tags are tied to a single position
    TagPosition tagPosition = new TagPosition();
    tagPosition.setPid(pos.getPid());
    tag.setPosition(tagPosition);

    if (publisherTag.getRtbProfile() != null) {
      RTBProfile rtbProfile = rtbProfileService.createTagRTBProfile(publisherTag, tag, siteDTO);
      sellerTagService.createExchangeTag(site, tag, rtbProfile, false);
    } else {
      sellerTagService.createTag(site, tag, false);
    }

    // refresh the site to ensure we get the correct version
    siteDTO = sellerSiteService.getSite(site);
    context = PublisherTagContext.newBuilder().withSite(siteDTO).build();

    return publisherTagAssembler.make(
        context, getPublisherTagById(siteDTO, position, tag.getIdentifier()));
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public PublisherTagDTO cloneTag(
      long publisher,
      long site,
      long position,
      long tag,
      PublisherTagDTO publisherTag,
      long targetSite,
      long targetPosition) {

    if (!userContext.canAccessSite(targetSite)) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    tagLimitChecker.checkLimitsTagsInPosition(publisher, site, position);

    Site originSite = sellerSiteService.getSite(site);
    Site destinationSite = sellerSiteService.getSite(targetSite);
    Tag originTag =
        tagRepository
            .findById(tag)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND));
    Position destinationPos = getSitePositionByPid(destinationSite, targetPosition);
    TagPosition destinationTagPosition = new TagPosition();
    destinationTagPosition.setPid(destinationPos.getPid());

    PublisherTagContext context =
        PublisherTagContext.newBuilder()
            .withSite(destinationSite)
            .withCopyOperation(true)
            .withCopyAcrossSite(site != targetSite)
            .build();
    // THis refreshes the current deal term on the original tag, which we'll
    // take over to the new tag
    SiteDealTerm siteDealTerm = originSite.getCurrentDealTerm();
    originSite.setCurrentDealTerm(siteDealTerm);
    Tag newTag = publisherTagAssembler.apply(context, getClonedTag(originTag), publisherTag);
    newTag.setSite(destinationSite);
    newTag.setPosition(destinationTagPosition);

    validateTagNamesAndIDs(newTag, destinationPos);

    // Have to re-calculate the eCPM manual, because when it was set earlier the tag didn't have any
    // deal-terms

    newTag.setEcpmManual(
        RevenueUtils.calculateNexageEcpm(destinationSite, newTag, publisherTag.getEcpmManual())
            .doubleValue());

    Site updated;

    if (!newTag.isExchangeTag()) {
      updated = sellerTagService.createTag(destinationSite, newTag, false);
    } else {
      RTBProfile rtbProfile =
          rtbProfileService.cloneTagRTBProfile(
              destinationSite, newTag, originSite, originTag, publisherTag);
      updated = sellerTagService.createExchangeTag(targetSite, newTag, rtbProfile, false);
    }

    return publisherTagAssembler.make(
        context, getPublisherTagById(updated, destinationPos.getPid(), newTag.getIdentifier()));
  }

  private void validateTagNamesAndIDs(Tag newTag, Position pos) {
    List<Tag> tagsForPos = tagRepository.findActiveByPositionPid(pos.getPid());
    if (StringUtils.isEmpty(newTag.getName())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_EMPTY_TAG_NAME);
    }
    for (Tag t : tagsForPos) {
      // For all existing tags in a position, make sure that the intended name,
      // is unique
      if ((t.getBuyerName().equals(newTag.getBuyerName())
          && t.getName().equals(newTag.getName()))) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_TAG_NAME);
      }
    }
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerYieldNexage() "
          + "or (@loginUserContext.doSameOrNexageAffiliation(#publisherPid) "
          + " and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()))")
  public PublisherTagDTO updateTag(
      long publisherPid, long sitePid, long positionPid, PublisherTagDTO publisherTag) {

    Site site = sellerSiteService.getSite(sitePid);
    // The following line is necessary to populate the transient dealterm fields of tag.
    SiteDealTerm dealTerm = site.getCurrentDealTerm();
    site.setCurrentDealTerm(dealTerm);

    PublisherTagContext context = PublisherTagContext.newBuilder().withSite(site).build();

    Tag tag = getPublisherTagByPid(site, positionPid, publisherTag.getPid());
    checkRevenueShareDetailsEditPermission(
        tag.getCurrentDealTerm(), publisherTag.getCurrentDealTerm());

    if (!tag.getVersion().equals(publisherTag.getVersion())) {
      // throw stale data exception
      throw new StaleStateException(
          "PublisherTag has a different version of the data tag.version = "
              + tag.getVersion()
              + " but publisherTag.version = "
              + publisherTag.getVersion());
    }

    if (tag.getPosition() == null && publisherTag.getPosition() != null) {
      // ensure that the position exists
      Position pos = getSitePositionByPid(site, publisherTag.getPosition().getPid());

      // pub tags are tied to a single position
      TagPosition tagPosition = new TagPosition();
      tagPosition.setPid(pos.getPid());
      tag.setPosition(tagPosition);
    }

    if (tag.isExchangeTag()) {
      if (publisherTag.getRtbProfile() == null) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_AND_PROFILE_NOT_MATCH);
      }

      RTBProfile rtbProfile = rtbProfileService.updateTagRTBProfile(site, tag, publisherTag);
      sellerTagService.updateExchangeTag(
          publisherTagAssembler.apply(context, tag, publisherTag), rtbProfile);
    } else {
      sellerTagService.updateTag(publisherTagAssembler.apply(context, tag, publisherTag));
    }

    // refresh the site to ensure we get the correct version
    site = sellerSiteService.getSite(sitePid);
    context = PublisherTagContext.newBuilder().withSite(site).build();

    return publisherTagAssembler.make(
        context, getPublisherTagByPid(site, positionPid, publisherTag.getPid()));
  }

  private void deleteTag(long site, long position, long tag) {
    Site siteDTO = sellerSiteService.getSite(site);
    @SuppressWarnings("unused")
    Tag siteTag = getPublisherTagByPid(siteDTO, position, tag);

    sellerTagService.deleteTag(site, tag);
  }

  private Tag getPublisherTagByPid(Site siteDTO, long position, long pid) {
    for (Tag tag : siteDTO.getTags()) {
      if (tag.getPid().equals(pid)
          && tag.getPosition() != null
          && tag.getPosition().getPid().equals(position)) {
        return tag;
      }
    }

    throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
  }

  private Tag getPublisherTagById(Site siteDTO, long position, String id) {
    for (Tag tag : siteDTO.getTags()) {
      if (tag.getIdentifier().equals(id)
          && tag.getPosition() != null
          && tag.getPosition().getPid().equals(position)) {
        return tag;
      }
    }

    throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
  }

  @Override
  public List<PublisherTierDTO> getTiers(long publisherPid, long sitePid, long positionPid) {
    List<PublisherTierDTO> publisherTiers = new ArrayList<>();
    Site site = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);
    validateSelfServeEnabled(site.getCompanyPid());
    Position pos = getSitePositionByPid(site, positionPid);
    PublisherTierContext context =
        PublisherTierContext.newBuilder().withSite(site).withPosition(pos).build();

    for (Tier tier : pos.getTiers()) {
      publisherTiers.add(publisherTierAssembler.make(context, tier));
    }

    return publisherTiers;
  }

  private void validateSelfServeEnabled(Long publisherPid) {
    if (!userContext.isPublisherSelfServeEnabled(publisherPid)) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  @Override
  public PublisherTierDTO getTier(long publisherPid, long sitePid, long positionPid, long tierPid) {
    Site site = sellerSiteService.getValidatedSiteForPublisher(sitePid, publisherPid);

    validateSelfServeEnabled(site.getCompanyPid());
    Position pos = getSitePositionByPid(site, positionPid);

    PublisherTierContext context =
        PublisherTierContext.newBuilder().withSite(site).withPosition(pos).build();

    return publisherTierAssembler.make(context, getPositionTierByPid(pos, tierPid));
  }

  private void renumberTiers(List<Tier> tiers) {
    int level = 0;
    for (Tier tier : tiers) {
      tier.setLevel(++level);
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public PublisherTierDTO createTier(
      Long publisher, long site, long position, PublisherTierDTO publisherTier) {
    Site siteDTO = sellerSiteService.getSite(site);
    Position pos = getSitePositionByPid(siteDTO, position);

    PublisherTierContext context =
        PublisherTierContext.newBuilder().withSite(siteDTO).withPosition(pos).build();

    if (BooleanUtils.isTrue(publisherTier.isAutogenerated())
        && publisherTier.getLevel() != null
        && publisherTier.getLevel() == 0) {
      Tier tierDB = pos.getTier(0);
      if (tierDB != null) {
        renumberTiers(pos.getTiers());
      }
    }

    positionTrafficTypeValidator.validatePositionTrafficType(siteDTO, pos, publisherTier, false);

    Tier tier = publisherTierAssembler.apply(context, pos.newTier(), publisherTier);
    if (publisherTier.getLevel() == null || publisherTier.getLevel() == 9999) {
      tier.setLevel(pos.getTiers().size() - 1);
    }

    sellerPositionService.updatePosition(pos);
    // refresh the site to ensure we get the correct version
    siteDTO = sellerSiteService.getSite(site);
    pos = getSitePositionByPid(siteDTO, position);

    context = PublisherTierContext.newBuilder().withSite(siteDTO).withPosition(pos).build();

    return publisherTierAssembler.make(context, getPositionTierByLevel(pos, tier.getLevel()));
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisherPid)")
  public void validateTag(
      long publisherPid,
      long adsourceId,
      String primaryId,
      String primaryName,
      String secondaryId,
      String secondaryName) {

    AdSource adSource =
        adSourceRepository
            .findById(adsourceId)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND));

    primaryId = StringUtils.defaultIfBlank(primaryId, null);
    primaryName = StringUtils.defaultIfBlank(primaryName, null);
    secondaryId = StringUtils.defaultIfBlank(secondaryId, null);
    secondaryName = StringUtils.defaultIfBlank(secondaryName, null);

    if ((adSource.isPrimaryIdRequired() && primaryId == null)
        || (adSource.isPrimaryNameRequired() && primaryName == null)
        || (adSource.isSecondaryIdRequired() && secondaryId == null)
        || (adSource.isSecondaryNameRequired() && secondaryName == null)
        || tagRepository.existsByBuyerPidAndPrimaryIdAndPrimaryNameAndSecondaryIdAndSecondaryName(
            adsourceId, primaryId, primaryName, secondaryId, secondaryName)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public PublisherTierDTO updateTier(
      long publisher, long site, long position, PublisherTierDTO publisherTier) {
    Site siteDTO = sellerSiteService.getSite(site);
    Position pos = getSitePositionByPid(siteDTO, position);
    Tier tier = getPositionTierByPid(pos, publisherTier.getPid());
    if (!tier.getVersion().equals(publisherTier.getVersion())) {
      // throw stale data exception
      throw new StaleStateException("PublisherTier has a different version of the data");
    }

    if (!tier.getTierType().equals(publisherTier.getTierType())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TIER_TYPE_UPDATE_NOT_SUPPORTED);
    }

    positionTrafficTypeValidator.validatePositionTrafficType(siteDTO, pos, publisherTier, false);

    PublisherTierContext context =
        PublisherTierContext.newBuilder().withSite(siteDTO).withPosition(pos).build();
    publisherTierAssembler.apply(context, tier, publisherTier);

    // order tiers by level and update level if duplicates found
    pos.getTiers().sort(Comparator.comparingInt(Tier::getLevel));

    int level = 0;
    for (Tier tr : pos.getTiers()) {
      if (tr.getLevel() != level) {
        tr.setLevel(level);
      }
      level++;
    }

    sellerPositionService.updatePosition(pos);

    // refresh the site to ensure we get the correct version
    siteDTO = sellerSiteService.getSite(site);
    pos = getSitePositionByPid(siteDTO, position);

    context = PublisherTierContext.newBuilder().withSite(siteDTO).withPosition(pos).build();

    Tier tr = getPositionTierByPidForTierUpdate(pos, publisherTier.getPid());

    if (tr != null) {
      return publisherTierAssembler.make(context, tr);
    } else {
      return null;
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#companyPid)")
  public void deleteTier(Long companyPid, long site, long position, long tier) {
    Site siteDTO = sellerSiteService.getSite(site);
    Position pos = getSitePositionByPid(siteDTO, position);
    Tier delTier = getPositionTierByPid(pos, tier);
    if (delTier.getTierType() == TierType.SY_DECISION_MAKER
        && pos.getTiers().stream().anyMatch(t -> t.getTierType() == TierType.SUPER_AUCTION)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_SUPER_AUCTION_TIER_CAN_NOT_EXISTS_WITHOUT_DECISION_MAKER_TIER);
    }
    pos.setTiers(
        pos.getTiers().stream()
            .filter(t -> !t.getPid().equals(delTier.getPid()))
            .collect(Collectors.toList()));
    sellerPositionService.updatePosition(pos);
  }

  private Tier getPositionTierByPidForTierUpdate(Position position, long pid) {
    try {
      return getPositionTierByPid(position, pid);
    } catch (GenevaValidationException | NullPointerException e) {
      return null;
    }
  }

  private Tier getPositionTierByPid(Position position, long pid) {
    for (Tier tier : position.getTiers()) {
      if (tier.getPid().equals(pid)) {
        return tier;
      }
    }

    throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
  }

  private Tier getPositionTierByLevel(Position position, int level) {
    Tier tier = position.getTier(level);
    if (tier != null) {
      return tier;
    }

    throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public List<PublisherBuyerDTO> getBuyers(long publisher, String search) {
    boolean isInternalUser = userContext.isNexageUser();

    List<AdSourceSummaryDTO> summaries = buyerService.getAllAdSourceSummaries();

    Set<Long> adSourceDefaults = new HashSet<>();
    for (PublisherAdSourceDefaultsDTO adSourceDefault :
        getAllAdsourceDefaultsForSeller(publisher)) {
      adSourceDefaults.add(adSourceDefault.getAdSourcePid());
    }

    List<PublisherBuyerDTO> publisherBuyers = new ArrayList<>();
    for (AdSourceSummaryDTO summary : summaries) {
      if ((isInternalUser
              || summary.getSelfServeEnablement() != SelfServeEnablement.NONE
              || adSourceDefaults.contains(summary.getPid()))
          && (StringUtils.isEmpty(search) || summary.getName().startsWith(search))) {
        PublisherBuyerContext context =
            PublisherBuyerContext.newBuilder()
                .enabledForPublisher(adSourceDefaults.contains(summary.getPid()))
                .build();
        publisherBuyers.add(publisherBuyerAssembler.make(context, summary));
      }
    }

    return publisherBuyers;
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisherPid)")
  public TagDeploymentInfoDTO getTagDeploymentInfo(long publisherPid, long sitePid, long tagPid) {
    return sellerTagService.getTagDeploymentInfo(sitePid, tagPid);
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisherPid)")
  public void undeployTag(long publisherPid, long sitePid, long positionPid, long tagPid) {
    sellerTagService.undeployTag(sitePid, positionPid, tagPid);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public TagArchiveTransactionDTO getTagPerformanceMetrics(
      long publisherPid, long sitePid, long positionPid, long tagPid, boolean withTransaction) {
    validateRequestParamsForTag(publisherPid, sitePid, positionPid, tagPid);
    TagPerformanceMetricsDTO.Builder builder =
        sellerTagService.getTagPerformanceMetrics(sitePid, tagPid);
    TagPerformanceMetricsDTO performanceMetrics = builder.build();

    TagArchiveTransactionDTO.Builder archiveBuilder = new TagArchiveTransactionDTO.Builder();
    archiveBuilder.withPerformanceMetrics(performanceMetrics);
    if (withTransaction) {
      String transactionID = sellerService.calcHashForTagArchive(performanceMetrics);
      archiveBuilder.withTransaction(transactionID);
    }
    return archiveBuilder.build();
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherPid)")
  public PublisherPositionDTO archiveTag(
      long publisherPid, long sitePid, long positionPid, long tag, String transactionId) {
    validateTagTransactionId(publisherPid, sitePid, positionPid, tag, transactionId);
    Site sitedbObject = validateRequestParamsForTag(publisherPid, sitePid, positionPid, tag);
    Site updatedSite = sellerTagService.archiveTag(sitedbObject, positionPid, tag);
    Set<Position> positions = updatedSite.getPositions();
    Position position = null;
    if (positions != null && positions.size() > 0) {
      for (Position pos : positions) {
        if (positionPid == pos.getPid()) {
          position = pos;
          break;
        }
      }
    }
    if (position != null) {
      PublisherPositionContext context =
          PublisherPositionContext.newBuilder().withSite(updatedSite).build();
      return publisherPositionAssembler.make(context, position);
    } else {
      return null;
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherId)")
  public List<PublisherTagDTO> getPubAdsourceTags(long publisherId, long adsourceId) {
    List<Tag> tags = sellerTagService.getPubAdsourceTags(publisherId, adsourceId);
    List<PublisherTagDTO> pubTags = new ArrayList<>();

    for (Tag tag : tags) {
      pubTags.add(publisherTagAssembler.make(null, tag));
    }

    return pubTags;
  }

  @Override
  @Transactional
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherId)")
  public List<PublisherTagDTO> updatePubAdsourceTags(
      long publisherId, long adsourceId, List<PublisherTagDTO> pubTags) {
    for (PublisherTagDTO pubTag : pubTags) {
      if (pubTag.getSite() == null || pubTag.getPosition() == null) {
        throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
      }

      updateTag(publisherId, pubTag.getSite().getPid(), pubTag.getPosition().getPid(), pubTag);
    }

    return getPubAdsourceTags(publisherId, adsourceId);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisherId)")
  public List<TagPerformanceMetricsDTO> getPubAdsourceTagPerformanceMetrics(
      long publisherId, long adsourceId) {
    return sellerTagService.getTagPerformanceMetrics(
        sellerTagService.getPubAdsourceTags(publisherId, adsourceId));
  }

  private Site validateRequestParamsForPosition(long publisherPid, long sitePid, long positionPid) {
    Site site = sellerSiteService.getSite(sitePid);

    if (site == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
    }

    Long companyPid = site.getCompanyPid();
    if (publisherPid != companyPid) {
      log.error("Site not found for publisher: [" + publisherPid + "]");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
    }

    boolean positionFound = false;
    Set<Position> positions = site.getPositions();
    if (positions != null && positions.size() > 0) {
      for (Position pos : positions) {
        if (pos.getPid().equals(positionPid)) {
          positionFound = true;
          break;
        }
      }
    }
    if (!positionFound) {
      log.error("Position not found for site: [" + sitePid + "]");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS);
    }

    return site;
  }

  private Site validateRequestParamsForTag(
      long publisherPid, long sitePid, long positionPid, long tagPid) {
    Site site = validateRequestParamsForPosition(publisherPid, sitePid, positionPid);
    Set<Tag> tags = site.getTags();
    if (tags != null && tags.size() > 0) {
      boolean tagFound = false;
      for (Tag tag : tags) {
        if (tag.getPid().equals(tagPid)) {
          tagFound = true;
          break;
        }
      }
      if (!tagFound) {
        log.error("Tag not found for site: [" + sitePid + "]");
        throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
      }
    } else {
      log.error("Tag not found for site: [" + sitePid + "]");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }
    return site;
  }

  private void validatePositionTransactionId(
      long publisherPid, long sitePid, long positionPid, String transactionId) {
    String generatedTxId =
        getPositionPerformanceMetrics(publisherPid, sitePid, positionPid, true).getTxId();
    if (transactionId == null || !transactionId.equals(generatedTxId)) {
      log.error(
          "Transaction Id is invalid: {}, currently generated: {}", transactionId, generatedTxId);
      throw new StaleStateException("Transaction Id is invalid");
    }
  }

  private void validateTagTransactionId(
      long publisherPid, long sitePid, long positionPid, long tagPid, String transactionId) {
    String generatedTxId =
        getTagPerformanceMetrics(publisherPid, sitePid, positionPid, tagPid, true).getTxId();
    if (transactionId == null || !transactionId.equals(generatedTxId)) {
      log.error(
          "Transaction Id is invalid: {}, currently generated: {}", transactionId, generatedTxId);
      throw new StaleStateException("Transaction Id is invalid");
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public List<PublisherAdSourceDefaultsDTO> getAllAdsourceDefaultsForSeller(long publisher) {
    List<PublisherAdSourceDefaultsDTO> result = new ArrayList<>();
    List<SellerAdSource> dbList = sellerService.getAllAdsourceDefaults(publisher);
    if (dbList != null && !dbList.isEmpty()) {
      PublisherAdSourceDefaultsContext context =
          PublisherAdSourceDefaultsContext.newBuilder().build();
      for (SellerAdSource eachDbItem : dbList) {
        result.add(adsourceDefaultsAssembler.make(context, eachDbItem));
      }
    }
    return result;
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public Collection<PublisherAdSourceDefaultsDTO> getAvailableAdsources(long publisher) {
    Set<PublisherAdSourceDefaultsDTO> result =
        new HashSet<>(getAllAdsourceDefaultsForSeller(publisher));
    addPublisherSelfServeDefaultAdsources(result);
    return result;
  }

  private void addPublisherSelfServeDefaultAdsources(Set<PublisherAdSourceDefaultsDTO> result) {
    List<AdSource> specificSellerAdsources = sellerService.getPublisherSelfServeDefaultAdsources();
    if (specificSellerAdsources != null && !specificSellerAdsources.isEmpty()) {
      PublisherAdSourceDefaultsContext context =
          PublisherAdSourceDefaultsContext.newBuilder().build();
      for (AdSource eachDbItem : specificSellerAdsources) {
        result.add(adsourceDefaultsAssembler.make(context, eachDbItem));
      }
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public PublisherAdSourceDefaultsDTO getAdsourceDefaultsForSeller(
      long publisher, long adsourceId) {
    SellerAdSource dbresult =
        sellerService
            .getSellerAdSourceBySellerPidAndAdSourcePid(publisher, adsourceId)
            .orElseThrow(
                () -> {
                  log.warn(ADSOURCE_DEFAULTS_WARNING, publisher, adsourceId);
                  return new GenevaValidationException(
                      ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_NOT_FOUND);
                });
    PublisherAdSourceDefaultsContext context =
        PublisherAdSourceDefaultsContext.newBuilder().build();
    return adsourceDefaultsAssembler.make(context, dbresult);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public PublisherAdSourceDefaultsDTO createAdsourceDefaultsForSeller(
      long publisher, long adsourceId, PublisherAdSourceDefaultsDTO defaults) {
    beanValidationService.validate(defaults, CreateGroup.class);
    AdSource adsource = buyerService.getAdSource(adsourceId);
    if (adsource == null) {
      log.error("Unknown adsource pid {}", adsourceId);
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    if (sellerService.existsSellerAdSource(publisher, adsourceId)) {
      log.error(
          "AdSource defaults already exist for seller: {} and adsource: {}", publisher, adsourceId);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_EXIST);
    }

    validateAdSourceDefaults(publisher, adsourceId, defaults);
    PublisherAdSourceDefaultsContext context =
        PublisherAdSourceDefaultsContext.newBuilder().build();
    SellerAdSource assembledDbObject =
        adsourceDefaultsAssembler.apply(context, new SellerAdSource(), defaults);
    SellerAdSource createdDefaults = sellerService.saveSellerAdSource(assembledDbObject);
    return adsourceDefaultsAssembler.make(context, createdDefaults);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public PublisherAdSourceDefaultsDTO updateAdsourceDefaultsForSeller(
      long publisher, long adsourceId, PublisherAdSourceDefaultsDTO defaults) {
    beanValidationService.validate(defaults, UpdateGroup.class);
    if (defaults == null) {
      log.error("JSON payload cannot be empty");
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    SellerAdSource dbresult =
        sellerService
            .getSellerAdSourceBySellerPidAndAdSourcePid(publisher, adsourceId)
            .orElseThrow(
                () -> {
                  log.error(ADSOURCE_DEFAULTS_WARNING, publisher, adsourceId);
                  return new GenevaValidationException(
                      ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_NOT_FOUND);
                });

    if (!Objects.equals(dbresult.getPid(), defaults.getPid())) {
      log.error(
          "Pid of the JSON payload {} does not match the db object's Pid {}",
          defaults.getPid(),
          dbresult.getPid());
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    if (dbresult.getVersion() != defaults.getVersion()) {
      throw new StaleStateException(
          "Database Adsource defaults has a different version of the data");
    }
    validateAdSourceDefaults(publisher, adsourceId, defaults);
    PublisherAdSourceDefaultsContext context =
        PublisherAdSourceDefaultsContext.newBuilder().build();
    SellerAdSource assembledDbObject = adsourceDefaultsAssembler.apply(context, dbresult, defaults);
    SellerAdSource createdDefaults = sellerService.saveSellerAdSource(assembledDbObject);
    return adsourceDefaultsAssembler.make(context, createdDefaults);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public void deleteAdsourceDefaultsForSeller(long publisher, long adsourceId) {
    if (!sellerService.existsSellerAdSource(publisher, adsourceId)) {
      log.error(ADSOURCE_DEFAULTS_WARNING, publisher, adsourceId);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_NOT_FOUND);
    }
    sellerService.deleteSellerAdSourceBySellerPidAndAdSourcePid(publisher, adsourceId);
  }

  private void validateAdSourceDefaults(
      long publisher, long adsourceId, PublisherAdSourceDefaultsDTO defaults) {
    if (defaults == null) {
      log.error("JSON payload cannot be empty");
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    if (defaults.getSellerPid() == null || !defaults.getSellerPid().equals(publisher)) {
      log.error("Publisher Pids don't match between URL and the JSON payload");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_PIDS_DONT_MATCH);
    }
    if (defaults.getAdSourcePid() == null || !defaults.getAdSourcePid().equals(adsourceId)) {
      log.error("Adsource Pids don't match between URL and the JSON payload");
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_PIDS_DONT_MATCH);
    }
    if (StringUtils.isBlank(defaults.getUsername())
        && StringUtils.isBlank(defaults.getPassword())
        && StringUtils.isBlank(defaults.getApiKey())
        && StringUtils.isBlank(defaults.getApiToken())) {
      log.error("All defaults cannot be null");
      throw new GenevaValidationException(ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_CANNOT_BE_NULL);
    }
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or "
          + "@loginUserContext.isOcManagerYieldNexage() or @loginUserContext.isOcUserNexage()) or "
          + "(@loginUserContext.doSameOrNexageAffiliation(#id) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller()))")
  public PublisherDTO getPublisher(long id) {
    return externalPublisherCrudService.read(id);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerYieldNexage()) or "
          + "(@loginUserContext.doSameOrNexageAffiliation(#publisherId) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()))")
  public PublisherDTO updatePublisher(long publisherId, PublisherDTO inPublisher) {
    return externalPublisherCrudService.update(inPublisher, publisherId);
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public PublisherPositionDTO detailedPosition(long publisher, long site, long position) {
    Site siteDTO = sellerSiteService.getSite(site);
    PublisherPositionContext context =
        PublisherPositionContext.newBuilder().withSite(siteDTO).withCopyOperation(true).build();

    PublisherPositionDTO detailedPublisherPositionDTO =
        publisherPositionAssembler.make(context, getSitePositionByPid(siteDTO, position));

    if (isLongformVideoPlacement(detailedPublisherPositionDTO)) {
      PlacementVideoDTO detailedPlacementVideoDTO =
          placementVideoService.getPlacementVideo(position);
      if (Objects.nonNull(detailedPlacementVideoDTO)) {
        detailedPublisherPositionDTO.setPlacementVideo(detailedPlacementVideoDTO);
      }
    }
    return detailedPublisherPositionDTO;
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public List<BiddersPerformanceForPubSelfServe> getBiddersPerformanceForPSS(
      long publisher, Date start, Date stop) {
    var loggedInUser = userContext.getCurrentUser();
    var companyDB = companyService.getCompany(publisher);
    if (companyDB == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }

    if (!userContext.isNexageUser() && !companyDB.isRtbRevenueReportEnabled()) {
      log.error(
          "No Bidder Information to Display: Restrict Advertiser Level RTB Reporting Is Enabled");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    Set<Long> siteIds =
        siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
            userContext.getPid(), Set.of(publisher));

    if (siteIds.isEmpty()) {
      log.info("No Sites found for Publisher {}, start {}, stop {}", publisher, start, stop);
      return new ArrayList<>();
    }
    return biddersPerformanceFacadeImpl.getBiddersPerformanceForPubSelfServe(
        siteIds, start, stop, loggedInUser.getUsername());
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public EstimatedRevenueForPubSelfServe getEstimatedRevenue(
      long publisher, String start, String stop) {
    SpringUserDetails loggedInUser = userContext.getCurrentUser();
    Company companyDB = companyService.getCompany(publisher);
    if (companyDB == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }

    StartStopDates dates = validateStartStop(start, stop);
    return estimatedRevenueFacadeImpl.getEstimatedRevenueForPubSelfServe(
        publisher, dates.getStart(), dates.getStop(), loggedInUser.getUsername());
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public EstimatedRevenueByAdNetworksForPubSelfServ getEstimatedRevenueByAdNetworks(
      long publisher, String start, String stop) {
    SpringUserDetails loggedInUser = userContext.getCurrentUser();
    Company companyDB = companyService.getCompany(publisher);
    if (companyDB == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }

    StartStopDates dates = validateStartStop(start, stop);
    return estimatedRevenueFacadeImpl.getEstimatedRevenueByAdNetworksForPubSelfServ(
        publisher, dates.getStart(), dates.getStop(), loggedInUser.getUsername());
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public EstimatedRevenueByAdvertiserForPubSelfServ getEstimatedRevenueByAdvertiser(
      long publisher, String start, String stop) {
    SpringUserDetails loggedInUser = userContext.getCurrentUser();
    Company companyDB = companyService.getCompany(publisher);
    if (companyDB == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND);
    }

    StartStopDates dates = validateStartStop(start, stop);
    return estimatedRevenueFacadeImpl.getEstimatedRevenueByAdvertiserForPubSelfServ(
        publisher, dates.getStart(), dates.getStop(), loggedInUser.getUsername());
  }

  private StartStopDates validateStartStop(String startDate, String endDate) {
    LocalDate start = null;
    LocalDate stop = null;

    if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DATES);
    }
    try {
      start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
      stop = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);

      if (start.isAfter(stop) || start.isEqual(stop) || stop.isAfter(LocalDate.now())) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DATES);
      }

    } catch (DateTimeParseException ex) {
      log.error("Invalid dates: {} {} {}", ex, start, stop);
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_DATES);
    }
    stop = stop.plusDays(1);
    log.debug("start date : {}, and stop date : {}", start, stop);
    return new StartStopDates(start, stop);
  }

  private static class StartStopDates {

    LocalDate start;
    LocalDate stop;

    StartStopDates(LocalDate s, LocalDate e) {
      this.start = s;
      this.stop = e;
    }

    public LocalDate getStart() {
      return this.start;
    }

    public LocalDate getStop() {
      return this.stop;
    }
  }

  @Override
  @PreAuthorize("@loginUserContext.isPublisherSelfServeEnabled(#publisher)")
  public PublisherTagDTO getDecisionMaker(long publisher, long site, long position) {
    Site siteDTO = sellerSiteService.getSite(site);
    Position posDTO = getSitePositionByPid(siteDTO, position);
    if (posDTO.getTrafficType() != TrafficType.SMART_YIELD) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION);
    }
    PublisherTagContext context = PublisherTagContext.newBuilder().withSite(siteDTO).build();

    List<Tier> tiers = posDTO.getTiers();
    Tier decMakerTier =
        tiers.stream()
            .filter(t -> t.getTierType().equals(TierType.SY_DECISION_MAKER))
            .findFirst()
            .orElse(null);
    if (decMakerTier == null) {
      return null;
    }

    Tag tag = null;
    if (!decMakerTier.getTags().isEmpty()) {
      tag = decMakerTier.getTags().get(0);
    }

    return publisherTagAssembler.make(context, tag);
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher))")
  public PublisherTagDTO createDecisionMaker(
      long publisher, long site, long position, PublisherTagDTO decisionMakerTag) {
    PublisherTagDTO response = null;
    Site siteDTO = sellerSiteService.getSite(site);
    Position posDTO = getSitePositionByPid(siteDTO, position);

    List<Tier> tiers = posDTO.getTiers();
    List<Tier> decMakerTiers =
        tiers.stream()
            .filter(t -> t.getTierType().equals(TierType.SY_DECISION_MAKER))
            .collect(Collectors.toList());
    if (!decMakerTiers.isEmpty()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_TIER_DECISION_MAKER_ALREADY_EXISTS);
    }

    decisionMakerTag = validateDecisionMakerTag(site, position, decisionMakerTag);
    decisionMakerTag = createTag(publisher, site, position, decisionMakerTag);

    // create dm at bottom
    PublisherTierDTO publisherTier =
        PublisherTierDTO.newBuilder()
            .withTierType(TierType.SY_DECISION_MAKER)
            .withLevel(9999)
            .withTag(decisionMakerTag)
            .withOrderStrategy(PublisherTierDTO.OrderStrategy.Dynamic)
            .build();
    PublisherTierDTO publisherDecMakerTier =
        createTier(siteDTO.getCompanyPid(), site, position, publisherTier);

    siteDTO = sellerSiteService.getSite(site);
    posDTO = getSitePositionByPid(siteDTO, position);
    PublisherPositionDTO publisherPosition =
        publisherPositionAssembler.make(
            PublisherPositionContext.newBuilder().withSite(siteDTO).build(), posDTO);
    if (publisherDecMakerTier != null) {
      // update position to make DM tier 0
      PublisherPositionDTO.PublisherPositionDTOBuilder posBuilder =
          PublisherPositionDTO.builder()
              .withDecisionMaker(decisionMakerTag)
              .withHeight(publisherPosition.getHeight())
              .withInterstitial(publisherPosition.getInterstitial())
              .withMemo(publisherPosition.getMemo())
              .withMraidSupport(publisherPosition.getMraidSupport())
              .withName(publisherPosition.getName())
              .withPid(publisherPosition.getPid())
              .withPlacementCategory(publisherPosition.getPlacementCategory())
              .withScreenLocation(publisherPosition.getScreenLocation())
              .withSite(publisherPosition.getSite())
              .withStatus(publisherPosition.getStatus())
              .withTrafficType(publisherPosition.getTrafficType())
              .withVersion(publisherPosition.getVersion())
              .withVideoLinearity(publisherPosition.getVideoLinearity())
              .withVideoSupport(publisherPosition.getVideoSupport())
              .withWidth(publisherPosition.getWidth())
              .withPositionAliasName(publisherPosition.getPositionAliasName())
              .withMraidAdvancedTracking(publisherPosition.getMraidAdvancedTracking())
              .withImpressionTypeHandling(publisherPosition.getImpressionTypeHandling());

      posBuilder.withTag(decisionMakerTag);
      if (publisherPosition.getTags() != null) {
        publisherPosition.getTags().forEach(posBuilder::withTag);
      }

      if (publisherPosition.getTiers() != null) {
        publisherPosition.getTiers().stream()
            .filter(t -> !t.getTierType().equals(TierType.SY_DECISION_MAKER))
            .forEach(
                pTier -> {
                  PublisherTierDTO.Builder tierBuilder =
                      PublisherTierDTO.newBuilder()
                          .withPid(pTier.getPid())
                          .withVersion(pTier.getVersion())
                          .withPosition(pTier.getPosition())
                          .withName(pTier.getName())
                          .withIsAutogenerated(pTier.isAutogenerated())
                          .withTierType(pTier.getTierType())
                          .withLevel(pTier.getLevel() + 1)
                          .withOrderStrategy(pTier.getOrderStrategy());

                  if (pTier.getTags() != null) {
                    pTier.getTags().forEach(tierBuilder::withTag);
                  }
                  posBuilder.withTier(tierBuilder.build());
                });
      }

      PublisherTierDTO.Builder tierBuilder =
          PublisherTierDTO.newBuilder()
              .withPid(publisherDecMakerTier.getPid())
              .withVersion(publisherDecMakerTier.getVersion())
              .withPosition(publisherDecMakerTier.getPosition())
              .withName(publisherDecMakerTier.getName())
              .withIsAutogenerated(publisherDecMakerTier.isAutogenerated())
              .withTierType(publisherDecMakerTier.getTierType())
              .withOrderStrategy(publisherDecMakerTier.getOrderStrategy())
              .withLevel(0);

      if (publisherDecMakerTier.getTags() != null) {
        publisherDecMakerTier.getTags().forEach(tierBuilder::withTag);
      }

      posBuilder.withTier(tierBuilder.build());
      posBuilder.withVersion(posDTO.getVersion());
      publisherPosition = updatePosition(publisher, site, posBuilder.build(), false);

      response = publisherPosition.getDecisionMaker();
    }
    return response;
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher))")
  public PublisherTagDTO updateDecisionMaker(
      long publisher, long site, long position, PublisherTagDTO decisionMakerTag) {
    decisionMakerTag = validateDecisionMakerTag(site, position, decisionMakerTag);

    Site siteDTO = sellerSiteService.getSite(site);
    Position posDTO = getSitePositionByPid(siteDTO, position);

    List<Tier> tiers = posDTO.getTiers();
    Tier decMakerTier =
        tiers.stream()
            .filter(t -> t.getTierType().equals(TierType.SY_DECISION_MAKER))
            .findFirst()
            .orElse(null);
    if (decMakerTier == null
        || decMakerTier.getTags() == null
        || decMakerTier.getTags().isEmpty()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_TIER_DECISION_MAKER_DOES_NOT_EXIST);
    }

    Tag oldDmTag = decMakerTier.getTags().get(0);
    PublisherTierDTO publisherDecMakerTier =
        publisherTierAssembler.make(
            PublisherTierContext.newBuilder().withSite(siteDTO).withPosition(posDTO).build(),
            decMakerTier);

    decisionMakerTag = createTag(publisher, site, position, decisionMakerTag);

    updateTier(
        publisher,
        site,
        position,
        PublisherTierDTO.newBuilder()
            .withTag(decisionMakerTag)
            .withIsAutogenerated(publisherDecMakerTier.isAutogenerated())
            .withOrderStrategy(publisherDecMakerTier.getOrderStrategy())
            .withLevel(0)
            .withTierType(TierType.SY_DECISION_MAKER)
            .withName(publisherDecMakerTier.getName())
            .withPosition(publisherDecMakerTier.getPosition())
            .withPid(publisherDecMakerTier.getPid())
            .withVersion(publisherDecMakerTier.getVersion())
            .build());

    deleteTag(site, position, oldDmTag.getPid());
    siteDTO = sellerSiteService.getSite(site);
    posDTO = getSitePositionByPid(siteDTO, position);
    return publisherPositionAssembler
        .make(PublisherPositionContext.newBuilder().withSite(siteDTO).build(), posDTO)
        .getDecisionMaker();
  }

  private void validateCreatePlacementVideo(PlacementVideoDTO placementVideoDTO) {
    if (!Objects.isNull(placementVideoDTO) && isYvapAndNotNull(placementVideoDTO)) {
      validatePlaylistInfoContentType(placementVideoDTO.getPlaylistInfo());
    }
  }

  private void validateUpdatePlacementVideo(PlacementVideoDTO placementVideoDTO) {
    if (!Objects.isNull(placementVideoDTO) && isYvapAndNotNull(placementVideoDTO)) {
      validatePlaylistInfoContentType(placementVideoDTO.getPlaylistInfo());
    }
  }

  private boolean isYvapAndNotNull(PlacementVideoDTO placementVideoDTO) {
    return (placementVideoDTO.getDapPlayerType() != null
        && placementVideoDTO.getDapPlayerType() == DapPlayerType.YVAP);
  }

  private void validatePlaylistInfoContentType(
      List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTOS) {
    for (PlacementVideoPlaylistDTO placementVideoPlaylistDTO : placementVideoPlaylistDTOS) {
      if (!placementVideoPlaylistDTO.getFallbackURL().endsWith(".mp4")
          || !placementVideoPlaylistDTO.getMediaType().equals(MediaType.VIDEO_MP4)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_MEDIA_TYPE);
      }
    }
  }

  private PublisherTagDTO validateDecisionMakerTag(
      long site, long position, PublisherTagDTO decisionMakerTag) {
    if (decisionMakerTag == null
        || decisionMakerTag.getBuyer() == null
        || decisionMakerTag.getBuyer().getPid() == null
        || decisionMakerTag.getSite() == null
        || decisionMakerTag.getSite().getPid() == null
        || decisionMakerTag.getSite().getPid() != site
        || decisionMakerTag.getPosition() == null
        || decisionMakerTag.getPosition().getPid() == null
        || position != decisionMakerTag.getPosition().getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    AdSource buyer =
        adSourceRepository
            .findById(decisionMakerTag.getBuyer().getPid())
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_BUYER_NOT_FOUND));
    if (buyer.getDecisionMakerEnabled() == DecisionMakerEnabled.NO) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED);
    }
    return getPublisherTagWithPopulatedValues(
        PublisherTagDTO.newBuilder()
            .withBuyer(decisionMakerTag.getBuyer())
            .withSite(decisionMakerTag.getSite())
            .withPosition(decisionMakerTag.getPosition()),
        decisionMakerTag,
        buyer);
  }

  protected PublisherTagDTO getPublisherTagWithPopulatedValues(
      PublisherTagDTO.Builder publisherTagBuilder,
      PublisherTagDTO publisherTag,
      AdSource adsource) {

    String primaryId = getPrimaryId(publisherTag, adsource);

    publisherTagBuilder
        .withTagType(
            publisherTag.getTagType() == null ? TagType.Mediation : publisherTag.getTagType())
        .withTagController(
            publisherTag.getTagController() == null
                ? PublisherTagControllerDTO.builder().autoExpand(false).build()
                : publisherTag.getTagController())
        .withStatus(publisherTag.getStatus() == null ? Status.ACTIVE : publisherTag.getStatus())
        .withOwner(publisherTag.getOwner() == null ? Owner.Publisher : publisherTag.getOwner())
        .withName(publisherTag.getName())
        .withEcpmProvision(
            publisherTag.getEcpmProvision() == null
                ? TagPubSelfServeView.EcpmProvision.Auto.name()
                : publisherTag.getEcpmProvision())
        .withPrimaryId(primaryId)
        .withPrimaryName(publisherTag.getPrimaryName())
        .withSecondaryId(publisherTag.getSecondaryId())
        .withSecondaryName(publisherTag.getSecondaryName())
        .withEcpmAuto(publisherTag.getEcpmAuto() == null ? 0d : publisherTag.getEcpmAuto())
        .withEcpmManual(publisherTag.getEcpmManual() == null ? 0d : publisherTag.getEcpmManual())
        .withMonetization(publisherTag.getMonetization() == null || publisherTag.getMonetization())
        .withIsAutoGenerated(
            publisherTag.isAutogenerated() != null && publisherTag.isAutogenerated())
        .withRtbProfile(publisherTag.getRtbProfile())
        .withAdNetReportApiKey(publisherTag.getAdNetReportApiKey())
        .withAdNetReportApiToken(publisherTag.getAdNetReportApiToken())
        .withAdNetReportPassword(publisherTag.getAdNetReportPassword())
        .withAdNetReportUserName(publisherTag.getAdNetReportUserName())
        .withAdSize(publisherTag.getAdSize())
        .withHeight(publisherTag.getHeight())
        .withImportRevenueFlag(publisherTag.getImportRevenueFlag())
        .withVideoAllowed(publisherTag.getVideoAllowed())
        .withVideoLinearity(publisherTag.getVideoLinearity())
        .withInsterstitial(publisherTag.getInsterstitial())
        .withScreenLocation(publisherTag.getScreenLocation())
        .withVideoSupport(publisherTag.getVideoSupport())
        .withWidth(publisherTag.getWidth())
        .withImportRevenueFlag(publisherTag.getImportRevenueFlag())
        .withPid(publisherTag.getPid())
        .withVersion(publisherTag.getVersion())
        .withCurrentDealTerm(publisherTag.getCurrentDealTerm());
    if (userContext.isNexageUser()) {
      publisherTagBuilder
          .withBuyerClass(publisherTag.getBuyerClass())
          .withUrlTemplate(publisherTag.getUrlTemplate())
          .withPostTemplate(publisherTag.getPostTemplate())
          .withGetTemplate(publisherTag.getGetTemplate())
          .withAdditionalPost(publisherTag.getAdditionalPost())
          .withAdditionalGet(publisherTag.getAdditionalGet())
          .withNoAdRegex(publisherTag.getNoAdRegex())
          .withClickthroughDisable(
              publisherTag.getClickthroughDisable() != null
                  && publisherTag.getClickthroughDisable())
          .withAdSpaceIdTemplate(publisherTag.getAdSpaceIdTemplate())
          .withAdSpaceNameTemplate(publisherTag.getAdSpaceNameTemplate())
          .withPostProcessTemplate(publisherTag.getPostProcessTemplate())
          .withHttpHeaderTemplate(publisherTag.getHttpHeaderTemplate())
          .withBuyerName(publisherTag.getBuyerName())
          .withBuyerLogo(publisherTag.getBuyerLogo())
          .withReturnRawResponse(publisherTag.getReturnRawResponse());
    }

    if (publisherTag.getRules() != null) {
      publisherTag.getRules().forEach(publisherTagBuilder::withTagRule);
    }

    return publisherTagBuilder.build();
  }

  private String getPrimaryId(PublisherTagDTO publisherTag, AdSource adsource) {
    String primaryId;
    if (publisherTag.getPrimaryId() != null) {
      primaryId = publisherTag.getPrimaryId();
    } else if (adsource != null && StringUtils.isNotBlank(adsource.getPrimaryIdDefault())) {
      primaryId = adsource.getPrimaryIdDefault();
    } else {
      primaryId = "PmId" + UUID.randomUUID();
    }
    return primaryId;
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher))")
  public List<PublisherTagDTO> generateSmartYieldDemandSourceTags(
      long publisher, long site, long position, List<PublisherTagDTO> publisherTags) {
    Site siteDTO = sellerSiteService.getSite(site);
    Position posDTO = getSitePositionByPid(siteDTO, position);

    List<Long> tagPids = siteDTO.getTags().stream().map(Tag::getPid).collect(Collectors.toList());

    List<PublisherTagDTO> newPublisherTags = new ArrayList<>();
    List<PublisherTagDTO> updatePublisherTags = new ArrayList<>();

    publisherTags.forEach(
        t -> {
          if (t.getPid() == null) {
            newPublisherTags.add(t);
          } else {
            updatePublisherTags.add(t);
          }
        });

    if (updatePublisherTags.stream().anyMatch(t -> !tagPids.contains(t.getPid()))) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }

    if (newPublisherTags.stream()
        .anyMatch(
            t ->
                ((t.getSite() != null && t.getSite().getPid() != site)
                    || (t.getPosition() != null && t.getPosition().getPid() != position)
                    || t.getBuyer() == null
                    || t.getBuyer().getPid() == null))) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }

    List<PublisherTagDTO> responsePublisherTags = new ArrayList<>();

    for (PublisherTagDTO inPublisherTag : newPublisherTags) {

      PublisherSiteDTO publisherSite = new PublisherSiteDTO.Builder().withPid(site).build();

      PublisherPositionContext context =
          PublisherPositionContext.newBuilder().withSite(siteDTO).build();
      PublisherPositionDTO pubPosition = publisherPositionAssembler.make(context, posDTO);

      PublisherBuyerDTO publisherBuyer =
          new PublisherBuyerDTO.Builder().withPid(inPublisherTag.getBuyer().getPid()).build();

      PublisherTagDTO publisherTag =
          getPublisherTagWithPopulatedValues(
              PublisherTagDTO.newBuilder()
                  .withBuyer(publisherBuyer)
                  .withSite(publisherSite)
                  .withPosition(pubPosition),
              inPublisherTag,
              null);

      try {
        PublisherTagDTO savedPublisherTag = createTag(publisher, site, position, publisherTag);
        responsePublisherTags.add(savedPublisherTag);
      } catch (Exception e) {
        log.error("Error in creating a tag for buyerId, " + inPublisherTag.getBuyer().getPid());
        throw e;
      }
    }

    for (PublisherTagDTO upPublisherTag : updatePublisherTags) {
      try {
        PublisherTagDTO updatedPublisherTag = updateTag(publisher, site, position, upPublisherTag);
        responsePublisherTags.add(updatedPublisherTag);
      } catch (Exception e) {
        log.error("Error in updating the tag, " + upPublisherTag.getPid());
        throw e;
      }
    }
    return responsePublisherTags;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserNexage()) or "
          + "((@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller())"
          + " and @loginUserContext.doSameOrNexageAffiliation(#publisher))")
  public Set<PublisherHierarchyDTO> getTagHierachy(long publisher, long rtbprofilegroup) {
    return rtbProfileService.getTagHierachy(publisher, rtbprofilegroup);
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage()) or "
          + "((@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) and @loginUserContext.doSameOrNexageAffiliation(#publisher))")
  public void updateRTBProfileLibToRTBProfilesMap(
      long publisher, RtbProfileLibsAndTagsDTO rtbprofileLibAndTagList) {
    rtbProfileService.updateRTBProfileLibToRTBProfilesMap(publisher, rtbprofileLibAndTagList);
  }

  private boolean isLongformVideoPlacement(PublisherPositionDTO publisherPositionDTO) {
    return publisherPositionDTO.getPlacementCategory() != null
        && publisherPositionDTO.getPlacementCategory().equals(PlacementCategory.INSTREAM_VIDEO)
        && publisherPositionDTO.isLongform();
  }

  private boolean needToCreateNewPlacementVideo(
      PublisherPositionDTO publisherPositionDTO, boolean placementHasExistingPlacementVideo) {
    return publisherPositionDTO.isLongform() && !placementHasExistingPlacementVideo;
  }

  private boolean needToDeletePlacementVideo(
      PublisherPositionDTO publisherPositionDTO, boolean placementHasExistingPlacementVideo) {
    return !publisherPositionDTO.isLongform() && placementHasExistingPlacementVideo;
  }

  private Tag getClonedTag(Tag original) {
    Tag copy;
    try {
      copy = original.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e); // this will never happen
    }
    copy.setPid(null);
    copy.setSite(null);
    copy.setDeployments(null);
    copy.setVersion(null);
    copy.setTagController(null);
    copy.getRules().clear();
    return copy;
  }
}

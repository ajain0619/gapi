package com.nexage.app.services.impl;

import com.google.common.collect.Sets;
import com.nexage.admin.core.dto.RtbProfileTagHierarchyDto;
import com.nexage.admin.core.dto.TagHierarchyDto;
import com.nexage.admin.core.enums.Mode;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.RTBProfileLibraryRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.app.dto.PubPositionHierarchy;
import com.nexage.app.dto.PubSiteHierarchyDTO;
import com.nexage.app.dto.PubTagHierarchyDTO;
import com.nexage.app.dto.RtbProfileLibsAndTagsDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileAssignmentsDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherHierarchyDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.SellerTagService;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.assemblers.PublisherDefaultRTBProfileAssembler;
import com.nexage.app.util.assemblers.PublisherRTBProfileAssembler;
import com.nexage.app.util.assemblers.context.PublisherDefaultRTBProfileContext;
import com.nexage.app.util.assemblers.context.PublisherRTBProfileContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.StaleStateException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RTBProfileServiceImpl implements RTBProfileService {

  private final LoginUserContext userContext;
  private final PublisherDefaultRTBProfileAssembler publisherDefaultRTBProfileAssembler;
  private final PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  private final RTBProfileRepository rtbProfileRepository;
  private final RTBProfileLibraryRepository rtbProfileLibraryRepository;
  private final SiteRepository siteRepository;
  private final CompanyService companyService;
  private final SellerSiteService sellerSiteService;
  private final SellerPositionService sellerPositionService;
  private final SellerTagService sellerTagService;
  private final PositionRepository positionRepository;
  private final RTBProfileUtil rtbProfileUtil;
  private final TagRepository tagRepository;
  private final EntityManager entityManager;

  public RTBProfileServiceImpl(
      LoginUserContext userContext,
      PublisherDefaultRTBProfileAssembler publisherDefaultRTBProfileAssembler,
      PublisherRTBProfileAssembler publisherRTBProfileAssembler,
      SiteRepository siteRepository,
      RTBProfileRepository rtbProfileRepository,
      RTBProfileLibraryRepository rtbProfileLibraryRepository,
      PositionRepository positionRepository,
      CompanyService companyService,
      SellerSiteService sellerSiteService,
      SellerPositionService sellerPositionService,
      SellerTagService sellerTagService,
      RTBProfileUtil rtbProfileUtil,
      TagRepository tagRepository,
      EntityManager entityManager) {
    this.userContext = userContext;
    this.publisherDefaultRTBProfileAssembler = publisherDefaultRTBProfileAssembler;
    this.publisherRTBProfileAssembler = publisherRTBProfileAssembler;
    this.positionRepository = positionRepository;
    this.rtbProfileRepository = rtbProfileRepository;
    this.rtbProfileLibraryRepository = rtbProfileLibraryRepository;
    this.siteRepository = siteRepository;
    this.companyService = companyService;
    this.sellerSiteService = sellerSiteService;
    this.sellerPositionService = sellerPositionService;
    this.sellerTagService = sellerTagService;
    this.rtbProfileUtil = rtbProfileUtil;
    this.tagRepository = tagRepository;
    this.entityManager = entityManager;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public PublisherDefaultRTBProfileDTO createDefaultRTBProfile(
      long publisherPid, PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile) {
    RTBProfile rtbProfile = createRTBProfile(publisherPid, publisherDefaultRTBProfile);
    processAssignments(publisherPid, publisherDefaultRTBProfile, rtbProfile);
    return readDefaultRTBProfile(publisherPid, rtbProfile.getPid());
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public PublisherDefaultRTBProfileDTO readDefaultRTBProfile(
      long publisherPid, long rtbProfilePid) {
    Company company = companyService.getCompany(publisherPid);
    validateDefaultRTBProfileEnabled(company);
    var rtbProfile = getActiveRTBProfile(publisherPid, rtbProfilePid);

    PublisherDefaultRTBProfileContext context =
        PublisherDefaultRTBProfileContext.newBuilder()
            .withSitesForRTBProfile(
                new ArrayList<>(
                    siteRepository.findByDefaultRtbProfile_PidAndStatusNot(
                        rtbProfile.getPid(), Status.DELETED)))
            .withPositionsForRTBProfile(
                new ArrayList<>(
                    positionRepository.findByDefaultRtbProfile_PidAndStatusNot(
                        rtbProfile.getPid(), Status.DELETED)))
            .build();

    return publisherDefaultRTBProfileAssembler.make(
        context, rtbProfile, PublisherDefaultRTBProfileAssembler.DRP_API_FIELDS);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public PublisherDefaultRTBProfileDTO updateDefaultRTBProfile(
      long publisherPid,
      PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile,
      long rtbProfileId) {
    validateRTBProfileRequest(publisherPid, publisherDefaultRTBProfile);
    if (rtbProfileId != publisherDefaultRTBProfile.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND);
    }
    Company company =
        companyService.getCompany(publisherDefaultRTBProfile.getDefaultRtbProfileOwnerCompanyPid());
    validateDefaultRTBProfileEnabled(company);
    validateRTBProfileNameUniqueness(publisherPid, publisherDefaultRTBProfile);

    RTBProfile dbRTBProfile =
        getActiveRTBProfile(publisherPid, publisherDefaultRTBProfile.getPid());
    if (!dbRTBProfile.getVersion().equals(publisherDefaultRTBProfile.getVersion())) {
      throw new StaleStateException("RTB Profile has a different version of the data");
    }
    // process assignments
    PublisherDefaultRTBProfileContext context =
        PublisherDefaultRTBProfileContext.newBuilder().withCompany(company).build();
    RTBProfile rtbProfile =
        publisherDefaultRTBProfileAssembler.apply(
            context, dbRTBProfile, publisherDefaultRTBProfile);
    Long rtbProfilePid = rtbProfile.getPid();
    List<Long> existedSitesPids =
        siteRepository.findPidsByDefaultRtbProfile_PidWithStatusNotDeleted(rtbProfileId);
    List<Long> existedPositionsPids =
        positionRepository.findPidsByDefaultRtbProfile_PidAndStatusNotDeleted(rtbProfileId);
    List<Long> incomeSitesPids = new ArrayList<>();
    List<Long> incomePositionsPids = new ArrayList<>();

    if (publisherDefaultRTBProfile.getAssignments() != null) {
      PublisherDefaultRTBProfileAssignmentsDTO assignments =
          publisherDefaultRTBProfile.getAssignments();
      if (assignments.getRTBProfileSites() != null) {
        incomeSitesPids =
            assignments.getRTBProfileSites().stream()
                .map(PublisherSiteDTO::getPid)
                .collect(Collectors.toList());
      }
      if (assignments.getRTBProfilePositions() != null) {
        incomePositionsPids =
            assignments.getRTBProfilePositions().stream()
                .map(PublisherPositionDTO::getPid)
                .collect(Collectors.toList());
      }
    }

    List<Long> sitesToAssign = new ArrayList<>(incomeSitesPids);
    sitesToAssign.removeAll(existedSitesPids);

    List<Long> sitesToRemove = new ArrayList<>(existedSitesPids);
    sitesToRemove.removeAll(incomeSitesPids);

    List<Long> positionsToAssign = new ArrayList<>(incomePositionsPids);
    positionsToAssign.removeAll(existedPositionsPids);

    List<Long> positionsToRemove = new ArrayList<>(existedPositionsPids);
    positionsToRemove.removeAll(incomePositionsPids);

    if (CollectionUtils.isNotEmpty(sitesToRemove)
        || CollectionUtils.isNotEmpty(positionsToRemove)
        || CollectionUtils.isNotEmpty(positionsToAssign)
        || CollectionUtils.isNotEmpty(sitesToAssign)) {
      rtbProfile.setVersion(
          rtbProfile.getVersion()
              + 1); // manually increment version by 1 because assignments are not audited
    }
    rtbProfileUtil.prepareDefaultRtbProfile(rtbProfile);
    rtbProfile = rtbProfileRepository.save(rtbProfile);

    // set rtb profile as null to the assigned items
    for (Long positionPid : positionsToRemove) {
      assignPosition(positionPid, null, publisherPid);
    }
    for (Long sitePid : sitesToRemove) {
      assignSite(sitePid, null, publisherPid);
    }

    // assign assigned to the updated default rtb profile
    for (Long positionPid : positionsToAssign) {
      assignPosition(positionPid, rtbProfilePid, publisherPid);
    }
    for (Long sitePid : sitesToAssign) {
      assignSite(sitePid, rtbProfilePid, publisherPid);
    }

    return readDefaultRTBProfile(publisherPid, rtbProfile.getPid());
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public void deleteDefaultRTBProfile(long publisherPid, long rtbProfilePid) {
    Company company = companyService.getCompany(publisherPid);
    validateDefaultRTBProfileEnabled(company);

    RTBProfile rtbProfile = getActiveRTBProfile(publisherPid, rtbProfilePid);

    if (company.getSellerAttributes() != null
        && company.getSellerAttributes().getDefaultRtbProfile() != null
        && company.getSellerAttributes().getDefaultRtbProfile().getPid() == rtbProfilePid) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_COMPANY_DEFAULT_RTB_PROFILE_CANT_BE_REMOVED);
    }

    List<Long> existedSitesPids =
        siteRepository.findPidsByDefaultRtbProfile_PidWithStatusNotDeleted(rtbProfile.getPid());
    List<Long> existedPositionsPids =
        positionRepository.findPidsByDefaultRtbProfile_PidAndStatusNotDeleted(rtbProfile.getPid());

    if (!CollectionUtils.isNotEmpty(existedSitesPids)
        || !CollectionUtils.isNotEmpty(existedPositionsPids)) {
      for (Long positionPid : existedPositionsPids) {
        assignPosition(positionPid, null, publisherPid);
      }
      for (Long sitePid : existedSitesPids) {
        assignSite(sitePid, null, publisherPid);
      }
    }

    rtbProfile.setStatus(Status.DELETED);
    rtbProfileRepository.save(rtbProfile);
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcManagerNexage()")
  public PublisherDefaultRTBProfileDTO cloneDefaultRTBProfile(
      long publisherPid, long sourceRTBProfilePid, PublisherDefaultRTBProfileDTO source) {
    getActiveRTBProfile(publisherPid, sourceRTBProfilePid);
    source.setPid(null);
    source.setId(null);
    source.setTag(null);
    RTBProfile clone = createRTBProfile(publisherPid, source);
    return readDefaultRTBProfile(publisherPid, clone.getPid());
  }

  @Override
  public RTBProfile processCompanyDefaultRtbProfile(
      Long publisherPid,
      PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile,
      RTBProfile dbRtbProfile,
      Company company) {

    if (publisherPid != null) {
      validateRTBProfileNameUniqueness(publisherPid, publisherDefaultRTBProfile);
    }
    if (publisherDefaultRTBProfile == null && dbRtbProfile != null) {
      if (userContext.isNexageAdminOrManager()) return null;
      throw new GenevaSecurityException(
          ServerErrorCodes.SERVER_NOT_AUTHORIZED_FOR_DEFAULT_RTB_PROFILE);
    }

    if (publisherDefaultRTBProfile == null) return null;

    if (!userContext.isNexageAdminOrManager()) {
      throw new GenevaSecurityException(
          ServerErrorCodes.SERVER_NOT_AUTHORIZED_FOR_DEFAULT_RTB_PROFILE);
    }

    if (publisherDefaultRTBProfile.getTag() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_TAG_NOT_AVAILABLE);
    }

    if (company.getPid() == null) {
      // for new company RTB Profile and Tag should be new
      if (publisherDefaultRTBProfile.getPid() != null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_EXISTING_DEFAULT_RTB_PROFILE_FOR_NEW_PUBLISHER_NOT_ALLOWED);
      }

      if (publisherDefaultRTBProfile.getTag().getPid() != null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_INVALID_TAG_FOR_DEFAULT_RTB_PROFILE);
      }
    }

    if (dbRtbProfile != null
        && !dbRtbProfile.getTag().getPid().equals(publisherDefaultRTBProfile.getTag().getPid())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_ALTER_TAG_FOR_DEFAULT_RTB_PROFILE_NOT_ALLOWED);
    }

    RTBProfile profile;

    if (publisherDefaultRTBProfile.getPid() != null) {
      profile =
          rtbProfileRepository
              .findById(publisherDefaultRTBProfile.getPid())
              .orElse(new RTBProfile());
    } else {
      profile = new RTBProfile();
    }

    PublisherDefaultRTBProfileContext context =
        PublisherDefaultRTBProfileContext.newBuilder().withCompany(company).build();
    profile =
        publisherDefaultRTBProfileAssembler.apply(context, profile, publisherDefaultRTBProfile);

    syncRTBProfileLibraries(profile, publisherDefaultRTBProfile);

    if (profile.getVersion() != null
        && !profile.getVersion().equals(publisherDefaultRTBProfile.getVersion())) {
      throw new StaleStateException("PublisherDefaultRTBProfile has a incorrect version number");
    }

    if (publisherDefaultRTBProfile.getPid() != null
        && profile.getOwnerCompany() != null
        && !profile.getOwnerCompany().getPid().equals(company.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_OWNER_MISMATCH);
    }

    return profile;
  }

  /** {@inheritDoc} */
  @Override
  public RTBProfile processDefaultRtbProfile(
      Site site,
      Company company,
      PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile,
      RTBProfile dbRtbProfile,
      boolean details) {
    if (!userContext.isNexageAdminOrManager()) return dbRtbProfile;

    if (publisherDefaultRTBProfile == null && dbRtbProfile != null) {
      if (userContext.isNexageAdminOrManager()) return null;
    }

    validateRTBProfileNameUniqueness(site.getCompanyPid(), publisherDefaultRTBProfile);
    RTBProfile profile = null;
    if (publisherDefaultRTBProfile != null) {
      if (publisherDefaultRTBProfile.getPid() != null) {
        profile =
            rtbProfileRepository
                .findById(publisherDefaultRTBProfile.getPid())
                .orElse(new RTBProfile());
      } else {
        profile = new RTBProfile();
      }

      if (company == null
          || company.getSellerAttributes() == null
          || company.getSellerAttributes().getDefaultRtbProfile() == null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_PUBLISHER_DEFAULT_RTB_PROFILE_NOT_AVAILABLE);
      }

      if (publisherDefaultRTBProfile.getTag() != null) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_MODIFYING_TAG_NOT_ALLOWED);
      }

      PublisherDefaultRTBProfileContext context =
          PublisherDefaultRTBProfileContext.newBuilder()
              .withSite(site)
              .withCompany(company)
              .build();
      profile =
          publisherDefaultRTBProfileAssembler.apply(
              context, profile, publisherDefaultRTBProfile, details);

      syncRTBProfileLibraries(profile, publisherDefaultRTBProfile);

      if (profile.getVersion() != null
          && !profile.getVersion().equals(publisherDefaultRTBProfile.getVersion())) {
        throw new StaleStateException(
            "PublisherDefaultRTBProfile has a incorrect version number "
                + " pubVersion="
                + publisherDefaultRTBProfile.getVersion()
                + "; profVerision="
                + profile.getVersion());
      }
    }
    return profile;
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or "
          + "@loginUserContext.isOcManagerNexage() or "
          + "@loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcManagerSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public void updateRTBProfileLibToRTBProfilesMap(
      long publisher, RtbProfileLibsAndTagsDTO rtbProfileLibAndTags) {
    List<Long> rtbProfileLibraryPids = rtbProfileLibAndTags.getRtbProfileLibPid();
    List<RtbProfileLibrary> rtbProfileLibraries =
        rtbProfileLibraryPids != null
            ? rtbProfileLibraryRepository.findAllById(rtbProfileLibraryPids)
            : new ArrayList<>();

    List<Long> tagPids = rtbProfileLibAndTags.getTagPid();
    List<String> tagPrimaryIds = tagRepository.getPrimaryIdForPidIn(tagPids);
    List<RTBProfile> addProfiles = rtbProfileRepository.findByExchangeSiteTagIdIn(tagPrimaryIds);

    tagPids = rtbProfileLibAndTags.getRemovedTagPid();
    tagPrimaryIds = tagRepository.getPrimaryIdForPidIn(tagPids);
    List<RTBProfile> removeProfiles = rtbProfileRepository.findByExchangeSiteTagIdIn(tagPrimaryIds);

    for (RtbProfileLibrary rtbProfileLibrary : rtbProfileLibraries) {
      for (var profile : addProfiles) {
        addRtbProfileLibraryAssociation(profile, rtbProfileLibrary);
      }
      for (var profile : removeProfiles) {
        removeRtbProfileLibraryAssociation(profile, rtbProfileLibrary);
      }
    }
  }

  @Override
  public RTBProfile createTagRTBProfile(PublisherTagDTO publisherTag, Tag tag, Site siteDTO) {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setSiteType(getSiteTypeCode(siteDTO.getType()));
    rtbProfile.setScreeningLevel(RTBProfile.ScreeningLevel.AllowAll);

    rtbProfile.setAlterReserve(publisherTag.getRtbProfile().getAlterReserve());

    PublisherRTBProfileContext profileContext =
        PublisherRTBProfileContext.newBuilder().withSite(siteDTO).withTag(tag).build();
    rtbProfile =
        publisherRTBProfileAssembler.apply(
            profileContext, rtbProfile, publisherTag.getRtbProfile());

    rtbProfile.setDescription(
        (userContext.isNexageUser() && publisherTag.getRtbProfile().getDescription() != null)
            ? publisherTag.getRtbProfile().getDescription()
            : "Self Service Created Ad Source");

    syncRTBProfileLibraries(rtbProfile, publisherTag.getRtbProfile());
    return rtbProfile;
  }

  @Override
  public RTBProfile updateTagRTBProfile(Site site, Tag tag, PublisherTagDTO publisherTagDto) {
    PublisherRTBProfileContext profileContext =
        PublisherRTBProfileContext.newBuilder().withSite(site).withTag(tag).build();

    RTBProfile rtbProfile = getRTBProfileByPid(site, publisherTagDto.getRtbProfile().getPid());
    if (!publisherTagDto.getRtbProfile().getVersion().equals(rtbProfile.getVersion())) {
      // throw stale data exception
      throw new StaleStateException("PublisherRTBProfile has a different version of the data");
    }
    rtbProfile =
        publisherRTBProfileAssembler.apply(
            profileContext, rtbProfile, publisherTagDto.getRtbProfile());
    syncRTBProfileLibraries(rtbProfile, publisherTagDto.getRtbProfile());
    return rtbProfile;
  }

  @Override
  public RTBProfile cloneTagRTBProfile(
      Site destinationSite,
      Tag newTag,
      Site originSite,
      Tag originTag,
      PublisherTagDTO publisherTag) {
    RTBProfile destinationProfile = null;

    var rtbProfilePid =
        originSite.getRtbProfiles().stream()
            .filter(
                rtbProfile -> rtbProfile.getExchangeSiteTagId().equals(originTag.getPrimaryId()))
            .map(RTBProfile::getPid)
            .findAny();

    if (rtbProfilePid.isPresent()) {
      var rtbProfile = rtbProfileRepository.findById(rtbProfilePid.get());
      if (rtbProfile.isPresent()) destinationProfile = getClonedRTBProfile(rtbProfile.get());
    }

    if (destinationProfile != null) {
      PublisherRTBProfileContext context =
          PublisherRTBProfileContext.newBuilder().withSite(destinationSite).withTag(newTag).build();
      // The profile library associations will be set in createExchangeTag
      destinationProfile.resetRtbProfileLibraries();

      PublisherRTBProfileDTO publisherTagRtbProfile = publisherTag.getRtbProfile();

      RTBProfile rtbProfile =
          publisherRTBProfileAssembler.apply(context, destinationProfile, publisherTagRtbProfile);
      rtbProfile.setSitePid(destinationSite.getPid());
      rtbProfile.setSiteType(getSiteTypeCode(destinationSite.getType()));
      rtbProfile.setLibraryPids(getLibraryPids(publisherTagRtbProfile.getLibraries()));
      return rtbProfile;
    } else {
      return null;
    }
  }

  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or "
          + "@loginUserContext.isOcManagerNexage() or "
          + "@loginUserContext.isOcUserNexage() or "
          + "@loginUserContext.isOcAdminSeller() or "
          + "@loginUserContext.isOcManagerSeller() or "
          + "@loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher)")
  public Set<PublisherHierarchyDTO> getTagHierachy(long publisher, long rtbprofilegroup) {
    var list = rtbProfileRepository.getAllTagHierarchy(publisher);
    return format(rtbprofilegroup, list);
  }

  private Set<PublisherHierarchyDTO> format(
      long rtbprofilegroup, List<RtbProfileTagHierarchyDto> resultSet) {
    Set<PublisherHierarchyDTO> returnList = new LinkedHashSet<>();
    Map<Long, PublisherHierarchyDTO> publisherSites = new LinkedHashMap<>();

    for (RtbProfileTagHierarchyDto hier : resultSet) {
      Long publisherPid = hier.getPublisherPid();
      String publisherName = hier.getPublisherName();
      Long sitePid = hier.getSitePid();
      Long siteAlias = hier.getSiteAlias();
      String siteNameAlias = hier.getSiteNameAlias();
      Character siteType = hier.getSiteType();
      Long positionPid = hier.getPositionPid();
      String positionName = hier.getPositionName();
      Byte placementType = hier.getPlacementType().byteValue();
      var trafficType = TrafficType.fromInt(hier.getTrafficType());
      String siteName = hier.getSiteName();
      var status = Status.fromInt(hier.getStatus());
      var mode = Mode.fromInt(Boolean.TRUE.equals(hier.getMode()) ? 1 : 0);

      PublisherHierarchyDTO publisherHier = publisherSites.get(publisherPid);

      if (publisherHier == null && !publisherSites.containsKey(publisherPid)) {
        PublisherHierarchyDTO.Builder builder = PublisherHierarchyDTO.newBuilder();
        publisherHier =
            builder.withPublisherPid(publisherPid).withPublisherName(publisherName).build();
      }
      PubSiteHierarchyDTO site = publisherHier.containsSite(sitePid);
      if (site == null && publisherHier != null) {
        createSiteHierarchy(
            rtbprofilegroup,
            returnList,
            publisherSites,
            publisherPid,
            sitePid,
            siteAlias,
            siteNameAlias,
            siteType,
            positionPid,
            positionName,
            placementType,
            trafficType,
            siteName,
            publisherHier,
            status,
            mode);
      } else {
        PubPositionHierarchy positionHier = null;
        if (site != null) {
          positionHier = site.containsPosition(positionPid);
        }
        if (positionHier == null) {
          createPositionHierarchy(
              rtbprofilegroup,
              positionPid,
              positionName,
              placementType,
              trafficType,
              site,
              publisherHier);
        }
      }
    }

    return populateTagRevenue(returnList);
  }

  private RTBProfile getActiveRTBProfile(long publisherPid, long rtbProfilePid) {
    var rtbProfile =
        rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndPid(
            publisherPid, rtbProfilePid);
    if (rtbProfile.isEmpty() || Status.DELETED.equals(rtbProfile.get().getStatus())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND);
    }
    return rtbProfile.get();
  }

  private void validateRTBProfileRequest(
      long publisherPid, PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile) {
    if (publisherDefaultRTBProfile.getTag() != null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_SETUP_IS_NOT_ALLOWED);
    }
    if (publisherPid != publisherDefaultRTBProfile.getDefaultRtbProfileOwnerCompanyPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND);
    }
  }

  private void validateDefaultRTBProfileEnabled(Company company) {
    if (!company.isDefaultRtbProfilesEnabled()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NOT_ENABLED_FOR_COMPANY);
    }
  }

  private void assignPosition(long positionPid, Long publisherDefaultRTBProfile, long publisherId) {
    var position =
        positionRepository.findByPidAndSite_CompanyPidAndStatus(
            positionPid, publisherId, Status.ACTIVE);
    if (position == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS);
    }
    sellerPositionService.assignRTBProfileToPosition(
        position.getPid(), publisherDefaultRTBProfile, publisherId);
  }

  private void assignSite(long sitePid, Long publisherDefaultRTBProfile, long publisherId) {
    if (!siteRepository.existsByPidAndCompanyPidAndStatus(sitePid, publisherId, Status.ACTIVE)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
    }
    sellerSiteService.assignRTBProfileToSite(sitePid, publisherDefaultRTBProfile, publisherId);
  }

  private void validateRTBProfileNameUniqueness(
      Long publisherPid, PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile) {
    if (publisherPid != null && publisherDefaultRTBProfile != null) {
      String name = publisherDefaultRTBProfile.getName();
      if (name == null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NAME_CANNOT_BE_EMPTY);
      }

      var rtbProfiles =
          rtbProfileRepository.findByDefaultRtbProfileOwnerCompanyPidAndName(publisherPid, name);
      if (CollectionUtils.isNotEmpty(rtbProfiles)
          && (rtbProfiles.size() > 1
              || !rtbProfiles.get(0).getPid().equals(publisherDefaultRTBProfile.getPid()))) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NAME_SHOULD_BE_UNIQUE);
      }
    }
  }

  private RTBProfile createRTBProfile(
      long publisherPid, PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile) {
    validateRTBProfileRequest(publisherPid, publisherDefaultRTBProfile);
    Company company = companyService.getCompany(publisherPid);
    validateDefaultRTBProfileEnabled(company);
    validateRTBProfileNameUniqueness(publisherPid, publisherDefaultRTBProfile);

    // creation
    PublisherDefaultRTBProfileContext context =
        PublisherDefaultRTBProfileContext.newBuilder().withCompany(company).build();
    RTBProfile rtbProfile =
        publisherDefaultRTBProfileAssembler.apply(
            context, new RTBProfile(), publisherDefaultRTBProfile);
    rtbProfile.setStatus(Status.ACTIVE);
    rtbProfileUtil.prepareDefaultRtbProfile(rtbProfile);
    rtbProfile = rtbProfileRepository.save(rtbProfile);
    entityManager.refresh(rtbProfile);
    return rtbProfile;
  }

  private void processAssignments(
      long publisherPid,
      PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile,
      RTBProfile rtbProfile) {
    PublisherDefaultRTBProfileAssignmentsDTO assignments =
        publisherDefaultRTBProfile.getAssignments();
    long publisherDefaultRTBProfilePid = rtbProfile.getPid();
    if (Objects.nonNull(assignments)) {
      if (CollectionUtils.isNotEmpty(assignments.getRTBProfilePositions())) {
        for (PublisherPositionDTO position : assignments.getRTBProfilePositions()) {
          assignPosition(position.getPid(), publisherDefaultRTBProfilePid, publisherPid);
        }
      }
      if (CollectionUtils.isNotEmpty(assignments.getRTBProfileSites())) {
        for (PublisherSiteDTO site : assignments.getRTBProfileSites()) {
          assignSite(site.getPid(), publisherDefaultRTBProfilePid, publisherPid);
        }
      }
    }
  }

  private void syncRTBProfileLibraries(
      RTBProfile rtbProfile, PublisherRTBProfileDTO publisherRTBProfile) {
    Set<RTBProfileLibraryAssociation> currentAssociations = rtbProfile.getLibraries();
    Set<PublisherRTBProfileLibraryDTO> targetLibraries = publisherRTBProfile.getLibraries();

    Set<Long> currentLibraryIds = new HashSet<>();
    Set<Long> targetLibraryIds = new HashSet<>();

    for (RTBProfileLibraryAssociation currentAssocation : currentAssociations) {
      currentLibraryIds.add(currentAssocation.getLibrary().getPid());
    }

    if (targetLibraries != null) {
      for (PublisherRTBProfileLibraryDTO targetLibrary : targetLibraries) {
        targetLibraryIds.add(targetLibrary.getPid());
      }
    }

    Set<Long> deleteLibraryIds = Sets.difference(currentLibraryIds, targetLibraryIds);
    Set<Long> addLibraryIds = Sets.difference(targetLibraryIds, currentLibraryIds);

    // Remove unwanted libraries
    for (Long deleteLibraryId : deleteLibraryIds) {
      currentAssociations.removeIf(
          association -> association.getLibrary().getPid().equals(deleteLibraryId));
    }

    // Add new libraries
    for (Long addLibraryId : addLibraryIds) {
      rtbProfileLibraryRepository
          .findById(addLibraryId)
          .ifPresent(
              coreLibrary -> {
                RTBProfileLibraryAssociation association = new RTBProfileLibraryAssociation();
                association.setLibrary(coreLibrary);
                association.setRtbprofile(rtbProfile);
                currentAssociations.add(association);
              });
    }
  }

  private void addRtbProfileLibraryAssociation(
      RTBProfile rtbProfile, RtbProfileLibrary rtbProfileLibrary) {
    if (rtbProfileLibrary != null) {
      if (rtbProfile.getLibraries().stream()
          .anyMatch(a -> a.getLibrary().getPid().equals(rtbProfileLibrary.getPid()))) {
        return; // assocation already exists
      }
      RTBProfileLibraryAssociation association = new RTBProfileLibraryAssociation();
      association.setLibrary(rtbProfileLibrary);
      association.setRtbprofile(rtbProfile);
      rtbProfile.getLibraries().add(association);
    }
  }

  private void removeRtbProfileLibraryAssociation(
      RTBProfile rtbProfile, RtbProfileLibrary rtbProfileLibrary) {
    if (rtbProfileLibrary != null) {
      rtbProfile
          .getLibraries()
          .removeIf(
              association -> association.getLibrary().getPid().equals(rtbProfileLibrary.getPid()));
    }
  }

  private RTBProfile getRTBProfileByPid(Site site, Long profilePid) {
    if (site.getRtbProfiles().isEmpty() || profilePid == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND);
    }

    return site.getRtbProfiles().stream()
        .filter(profile -> profile.getPid().equals(profilePid))
        .findFirst()
        .orElseThrow(
            () -> new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND));
  }

  // TODO - move utility methods into the utility class
  private char getSiteTypeCode(Type siteType) {
    Character result = null;
    if (siteType != null) {
      switch (siteType) {
        case APPLICATION:
          {
            result = 'A';
            break;
          }
        case MOBILE_WEB:
          {
            result = 'W';
            break;
          }
        case DESKTOP:
          {
            result = 'D';
            break;
          }
        case DOOH:
          {
            result = 'O';
            break;
          }
        case WEBSITE:
          {
            result = 'S';
            break;
          }
      }
    }
    if (result == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_UNSUPPORTED_SITE_TYPE);
    }
    return result;
  }

  private Set<Long> getLibraryPids(Set<PublisherRTBProfileLibraryDTO> libraries) {
    Set<Long> pids = new HashSet<>();
    for (PublisherRTBProfileLibraryDTO l : libraries) {
      pids.add(l.getPid());
    }
    return pids;
  }

  private Set<PublisherHierarchyDTO> populateTagRevenue(Set<PublisherHierarchyDTO> publishers) {

    for (PublisherHierarchyDTO publisher : publishers) {
      for (PubSiteHierarchyDTO site : publisher.getSite()) {
        for (PubPositionHierarchy pos : site.getPositions()) {
          for (PubTagHierarchyDTO tag : pos.getTags()) {
            BigDecimal reveneue =
                sellerTagService.getTagRevenue(tag.getTagPid(), publisher.getPublisherPid());
            tag.setRevenue(reveneue);
          }
        }
      }
    }
    return publishers;
  }

  private void createSiteHierarchy(
      long rtbprofilegroup,
      Set<PublisherHierarchyDTO> returnList,
      Map<Long, PublisherHierarchyDTO> publisherSites,
      Long publisherPid,
      Long sitePid,
      Long sitealias,
      String siteNameAlias,
      Character siteType,
      Long positionPid,
      String posName,
      Byte posType,
      TrafficType trafficType,
      String siteName,
      PublisherHierarchyDTO publisherHier,
      com.nexage.admin.core.enums.Status status,
      Mode mode) {

    PubSiteHierarchyDTO.Builder siteBuilder = PubSiteHierarchyDTO.newBuilder();
    PubSiteHierarchyDTO siteHier =
        siteBuilder
            .withSitePid(sitePid)
            .withSiteNameAlias(siteNameAlias)
            .withSitealias(sitealias)
            .withSiteType(siteType)
            .withSiteName(siteName)
            .withSiteStatus(status)
            .withSiteMode(mode)
            .build();
    publisherHier.addSite(siteHier);
    publisherSites.put(publisherPid, publisherHier);
    returnList.add(publisherHier);

    PubPositionHierarchy positionHier = siteHier.containsPosition(positionPid);
    if (positionHier == null) {
      createPositionHierarchy(
          rtbprofilegroup, positionPid, posName, posType, trafficType, siteHier, publisherHier);
    }
  }

  private void createPositionHierarchy(
      long rtbprofilegroup,
      Long positionPid,
      String posName,
      Byte posType,
      TrafficType trafficType,
      PubSiteHierarchyDTO siteHier,
      PublisherHierarchyDTO hier) {

    PubPositionHierarchy.Builder posBuilder = PubPositionHierarchy.newBuilder();
    PubPositionHierarchy posHier =
        posBuilder
            .withPid(positionPid)
            .withPlacementType(posType)
            .withTrafficType(trafficType)
            .withPositionName(posName)
            .build();
    siteHier.addPosition(posHier);

    Set<TagHierarchyDto> tags =
        tagRepository.getTagHierarchy(hier.getPublisherPid(), positionPid, rtbprofilegroup);

    SellerAttributes sellerAttributes =
        companyService.getCompany(hier.getPublisherPid()).getSellerAttributes();
    createTagHierarchy(tags, posHier, rtbprofilegroup, sellerAttributes);
  }

  private void createTagHierarchy(
      Set<TagHierarchyDto> tags,
      PubPositionHierarchy posHier,
      long rtbprofilegroup,
      SellerAttributes sellerAttributes) {

    Set<Long> defaultBlock = sellerAttributes != null ? sellerAttributes.getDefaultBlock() : null;
    Set<Long> defaultBidderGroup =
        sellerAttributes != null ? sellerAttributes.getDefaultBidderGroups() : null;
    boolean isBlockedAsDefault = (defaultBlock != null) && defaultBlock.contains(rtbprofilegroup);
    boolean isBidderBlockedAsDefault =
        (defaultBidderGroup != null) && defaultBidderGroup.contains(rtbprofilegroup);

    for (TagHierarchyDto tag : tags) {
      Boolean blockAsDefault = false;
      if (isBlockedAsDefault) {
        blockAsDefault = tag.getUseDefaultBlock();
      } else if (isBidderBlockedAsDefault) {
        blockAsDefault = tag.getUseDefaultBidders();
      }
      PubTagHierarchyDTO tHier =
          PubTagHierarchyDTO.newBuilder()
              .withTagPid(tag.getTagPid())
              .withTagname(tag.getTagName())
              .withFilterBiddersWhitelist(tag.getFilterBiddersWhitelist())
              .withFilterBiddersAllowlist(tag.getFilterBiddersAllowlist())
              .withTierPid(tag.getTierPid())
              .withTierLevel(tag.getTierLevel())
              .withTierType(
                  Optional.ofNullable(tag.getTierType())
                      .map(t -> TierType.fromInt((int) t))
                      .orElse(null))
              .withBelongsToRTBGroup(tag.getBelongsToRtbGroup().equals(1))
              .withBlockAsDefault(blockAsDefault)
              .build();
      posHier.addTag(tHier);
    }
  }

  private RTBProfile getClonedRTBProfile(RTBProfile original) {
    var originalSite = original.getSite();
    original.setSite(null);

    try {
      RTBProfile newProfile = original.clone();

      original.setSite(originalSite);
      if (newProfile != null) {
        newProfile.setPid(null);
        newProfile.setExchangeSiteTagId(null);
        newProfile.setVersion(null);
      }

      return newProfile;
    } catch (CloneNotSupportedException cnse) {
      return null;
    }
  }
}

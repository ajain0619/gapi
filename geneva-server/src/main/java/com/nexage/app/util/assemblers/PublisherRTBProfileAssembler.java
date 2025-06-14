package com.nexage.app.util.assemblers;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Stream.concat;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileBidder;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.app.dto.publisher.PublisherRTBProfileBidderDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileDTO.AuctionType;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileScreeningLevel;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import com.nexage.app.dto.transparency.TransparencySettingsEntity;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.PublisherRTBProfileBidderDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.util.assemblers.context.PublisherRTBProfileContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleStateException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublisherRTBProfileAssembler
    extends Assembler<PublisherRTBProfileDTO, RTBProfile, PublisherRTBProfileContext> {

  private final PublisherRTBProfileLibraryAssembler publisherRTBProfileLibraryAssembler;
  private final BidderConfigRepository bidderConfigRepository;
  private final TransparencyService transparencyService;
  private final UserContext userContext;

  protected static final Set<String> DEFAULT_FIELDS =
      Set.of(
          "pid",
          "version",
          "id",
          "blockedAdCategories",
          "blockedAdvertisers",
          "pubNetReserve",
          "libraries",
          "bidderFilters",
          "bidderFilterWhitelist",
          "bidderFilterAllowlist",
          "useDefaultBlock",
          "includeConsumerId",
          "includeSiteName",
          "includeDomainReferences",
          "includeConsumerProfile",
          "siteNameAlias",
          "pubNameAlias",
          "screeningLevel",
          "useDefaultBidders",
          "bidderSeatWhitelists",
          "auctionType",
          "lowReserve",
          "pubNetLowReserve",
          "alterReserve",
          "siteTransparencySettings",
          "publisherTransparencySettings");

  protected static final Set<String> ALL_FIELDS =
      concat(
              DEFAULT_FIELDS.stream(),
              Stream.of(
                  "description",
                  "siteType",
                  "blockedAdTypes",
                  "blockedAttributes",
                  "defaultReserve",
                  "creationDate",
                  "blockedExternalDataProviderMap",
                  "lastUpdate",
                  "bidderFilterMap",
                  "libraryPids",
                  "includeGeoData"))
          .collect(Collectors.toSet());

  protected static final Set<String> LIMITED_DEFAULT_RTB_PROFILE_FIELDS =
      Set.of("pid", "id", "description", "version", "defaultRtbProfileOwnerCompanyPid", "name");

  protected static final Set<String> ALL_DEFAULT_RTB_PROFILE_FIELDS =
      concat(Stream.of("tag", "defaultRtbProfileOwnerCompanyPid", "name"), ALL_FIELDS.stream())
          .collect(Collectors.toSet());

  private static final Joiner joiner = Joiner.on(",").skipNulls();
  private static final Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

  @Override
  public PublisherRTBProfileDTO make(PublisherRTBProfileContext context, RTBProfile rtbProfile) {
    return make(context, rtbProfile, userContext.isNexageUser() ? ALL_FIELDS : DEFAULT_FIELDS);
  }

  @Override
  public PublisherRTBProfileDTO make(
      PublisherRTBProfileContext context, RTBProfile rtbProfile, Set<String> fields) {
    PublisherRTBProfileDTO.Builder publisherRTBProfileBuilder =
        context.getProfileBuilder() != null
            ? context.getProfileBuilder()
            : PublisherRTBProfileDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          publisherRTBProfileBuilder.withPid(rtbProfile.getPid());
          break;
        case "version":
          publisherRTBProfileBuilder.withVersion(rtbProfile.getVersion());
          break;
        case "id":
          publisherRTBProfileBuilder.withId(rtbProfile.getExchangeSiteTagId());
          break;
        case "name":
          publisherRTBProfileBuilder.withName(rtbProfile.getName());
          break;
        case "blockedAdCategories":
          if (rtbProfile.getBlockedAdCategories() != null) {
            publisherRTBProfileBuilder.withBlockedAdCategories(
                Sets.newHashSet(splitter.split(rtbProfile.getBlockedAdCategories())));
          }
          break;
        case "blockedAdvertisers":
          if (rtbProfile.getBlockedAdvertisers() != null) {
            publisherRTBProfileBuilder.withBlockedAdvertisers(
                Sets.newHashSet(splitter.split(rtbProfile.getBlockedAdvertisers())));
          }
          break;
        case "pubNetReserve":
          publisherRTBProfileBuilder.withPubNetReserve(rtbProfile.getPubNetReserve());
          break;
        case "libraries":
          publisherRTBProfileBuilder.withLibraries(
              makeRTBProfileLibraries(rtbProfile.getLibraries()));
          break;
        case "bidderFilters":
          if (!StringUtils.isBlank(rtbProfile.getBidderFilterList())) {
            publisherRTBProfileBuilder.withBidderFilters(
                Sets.newHashSet(splitter.split(rtbProfile.getBidderFilterList())));
          }
          break;
        case "bidderFilterWhitelist":
          publisherRTBProfileBuilder.withBidderFilterWhitelist(
              rtbProfile.getBiddersFilterWhitelist());
          break;
        case "bidderFilterAllowlist":
          publisherRTBProfileBuilder.withBidderFilterAllowlist(
              rtbProfile.getBiddersFilterAllowlist());
          break;
        case "useDefaultBlock":
          publisherRTBProfileBuilder.withUseDefaultBlock(rtbProfile.isUseDefaultBlock());
          break;
        case "includeConsumerId":
          publisherRTBProfileBuilder.withIncludeConsumerId(rtbProfile.isIncludeConsumerId());
          break;
        case "includeDomainReferences":
          publisherRTBProfileBuilder.withIncludeDomainReferences(
              rtbProfile.isIncludeDomainReferences());
          break;
        case "includeConsumerProfile":
          publisherRTBProfileBuilder.withIncludeConsumerProfile(
              rtbProfile.isIncludeConsumerProfile());
          break;
        case "screeningLevel":
          if (rtbProfile.getScreeningLevel() != null) {
            PublisherRTBProfileScreeningLevel sl =
                PublisherRTBProfileScreeningLevel.valueOf(rtbProfile.getScreeningLevel().name());
            publisherRTBProfileBuilder.withScreeningLevel(sl);
          }
          break;
        case "useDefaultBidders":
          publisherRTBProfileBuilder.withUseDefaultBidders(rtbProfile.isUseDefaultBidders());
          break;
        case "bidderSeatWhitelists":
          if (!rtbProfile.getBidderSeatWhitelists().isEmpty()) {
            Set<RTBProfileBidder> rtbProfileBidders = rtbProfile.getBidderSeatWhitelists();
            publisherRTBProfileBuilder.withRtbProfileBidders(
                makeRTBProfileBidders(rtbProfileBidders));
          }
          break;
        case "auctionType":
          publisherRTBProfileBuilder.withAuctionType(
              AuctionType.values()[rtbProfile.getAuctionType()]);
          break;
        case "lowReserve":
          publisherRTBProfileBuilder.withLowReserve(rtbProfile.getLowReserve());
          break;
        case "pubNetLowReserve":
          publisherRTBProfileBuilder.withPubNetLowReserve(rtbProfile.getPubNetLowReserve());
          break;
        case "alterReserve":
          publisherRTBProfileBuilder.withAlterReserve(rtbProfile.getAlterReserve());
          break;
        case "siteTransparencySettings":
          if (rtbProfile.getIncludeSiteName() != null) {
            publisherRTBProfileBuilder.withSiteTransparencySettings(
                new TransparencySettingsDTO(
                    TransparencyMode.fromInt(rtbProfile.getIncludeSiteName()),
                    rtbProfile.getSiteAlias(),
                    rtbProfile.getSiteNameAlias()));
          }
          break;
        case "publisherTransparencySettings":
          if (rtbProfile.getIncludePubName() != null) {
            publisherRTBProfileBuilder.withPublisherTransparencySettings(
                new TransparencySettingsDTO(
                    TransparencyMode.fromInt(rtbProfile.getIncludePubName()),
                    rtbProfile.getPubAlias(),
                    rtbProfile.getPubNameAlias()));
          }
          break;
        case "description":
          publisherRTBProfileBuilder.withDescription(rtbProfile.getDescription());
          break;
        case "siteType":
          publisherRTBProfileBuilder.withSiteType(rtbProfile.getSiteType());
          break;
        case "blockedAdTypes":
          publisherRTBProfileBuilder.withBlockedAdTypes(rtbProfile.getBlockedAdTypes());
          break;
        case "blockedAttributes":
          publisherRTBProfileBuilder.withBlockedAttributes(rtbProfile.getBlockedAttributes());
          break;
        case "defaultReserve":
          publisherRTBProfileBuilder.withDefaultReserve(rtbProfile.getDefaultReserve());
          break;
        case "creationDate":
          publisherRTBProfileBuilder.withCreationDate(rtbProfile.getCreationDate());
          break;
        case "lastUpdate":
          publisherRTBProfileBuilder.withLastUpdate(rtbProfile.getLastUpdate());
          break;
        case "blockedExternalDataProviderMap":
          publisherRTBProfileBuilder.withBlockedExternalDataProviderMap(
              rtbProfile.getBlockedExternalDataProviderMap());
          break;
        case "bidderFilterMap":
          publisherRTBProfileBuilder.withBidderFilterMap(rtbProfile.getBidderFilterMap());
          break;
        case "libraryPids":
          publisherRTBProfileBuilder.withLibraryPids(rtbProfile.getLibraryPids());
          break;
        case "includeGeoData":
          publisherRTBProfileBuilder.withIncludeGeoData(rtbProfile.isIncludeGeoData());
          break;
        default:
      }
    }

    return publisherRTBProfileBuilder.build();
  }

  @Override
  public RTBProfile apply(
      PublisherRTBProfileContext context,
      RTBProfile rtbProfile,
      PublisherRTBProfileDTO publisherRTBProfile) {
    transparencyService.validateTransparencySettingsForRTBProfile(
        context.getCompany().getPid(),
        rtbProfile,
        publisherRTBProfile.getSiteTransparencySettings(),
        TransparencySettingsEntity.SITE);
    transparencyService.validateTransparencySettingsForRTBProfile(
        context.getCompany().getPid(),
        rtbProfile,
        publisherRTBProfile.getPublisherTransparencySettings(),
        TransparencySettingsEntity.PUBLISHER);

    fillSiteTransparencySettings(rtbProfile, publisherRTBProfile);
    fillPublisherTransparencySettings(rtbProfile, publisherRTBProfile);

    if (publisherRTBProfile.getBlockedAdCategories() != null) {
      rtbProfile.setBlockedAdCategories(joiner.join(publisherRTBProfile.getBlockedAdCategories()));
    }
    if (publisherRTBProfile.getBlockedAdvertisers() != null) {
      rtbProfile.setBlockedAdvertisers(joiner.join(publisherRTBProfile.getBlockedAdvertisers()));
    }
    if (publisherRTBProfile.getBidderFilters() != null) {
      rtbProfile.setBidderFilterList(joiner.join(publisherRTBProfile.getBidderFilters()));
    } else {
      rtbProfile.setBidderFilterList(null);
    }

    rtbProfile.setBiddersFilterWhitelist(publisherRTBProfile.getBidderFilterWhitelist());
    rtbProfile.setBiddersFilterAllowlist(publisherRTBProfile.getBidderFilterAllowlist());

    rtbProfile.setPubNetReserve(publisherRTBProfile.getPubNetReserve());
    if (publisherRTBProfile.getUseDefaultBlock() != null) {
      rtbProfile.setUseDefaultBlock(publisherRTBProfile.getUseDefaultBlock());
    }

    if (publisherRTBProfile.getIncludeConsumerId() != null) {
      rtbProfile.setIncludeConsumerId(publisherRTBProfile.getIncludeConsumerId());
    }

    if (publisherRTBProfile.getName() != null) {
      rtbProfile.setName(publisherRTBProfile.getName());
    }

    if (publisherRTBProfile.getIncludeDomainReferences() != null) {
      rtbProfile.setIncludeDomainReferences(publisherRTBProfile.getIncludeDomainReferences());
    }

    if (publisherRTBProfile.getIncludeConsumerProfile() != null) {
      rtbProfile.setIncludeConsumerProfile(publisherRTBProfile.getIncludeConsumerProfile());
    }

    if (publisherRTBProfile.getScreeningLevel() != null) {
      RTBProfile.ScreeningLevel sl =
          RTBProfile.ScreeningLevel.valueOf(publisherRTBProfile.getScreeningLevel().name());
      rtbProfile.setScreeningLevel(sl);
    }

    if (publisherRTBProfile.getUseDefaultBidders() != null) {
      rtbProfile.setUseDefaultBidders(publisherRTBProfile.getUseDefaultBidders());
    }

    rtbProfile.setLowReserve(publisherRTBProfile.getLowReserve());
    rtbProfile.setPubNetLowReserve(publisherRTBProfile.getPubNetLowReserve());

    if (publisherRTBProfile.getAuctionType() != null) {
      rtbProfile.setAuctionType(publisherRTBProfile.getAuctionType().ordinal());
    }

    if (publisherRTBProfile.getAlterReserve() != null) {
      rtbProfile.setAlterReserve(publisherRTBProfile.getAlterReserve());
    }

    syncProfileBidders(rtbProfile, publisherRTBProfile);

    if (userContext.isNexageUser()) {
      rtbProfile.setDescription(publisherRTBProfile.getDescription());
      rtbProfile.setBlockedAdTypes(publisherRTBProfile.getBlockedAdTypes());
      rtbProfile.setBlockedAttributes(publisherRTBProfile.getBlockedAttributes());
      rtbProfile.setIncludeGeoData(TRUE.equals(publisherRTBProfile.getIncludeGeoData()));
    }
    return rtbProfile;
  }

  private Set<PublisherRTBProfileBidderDTO> makeRTBProfileBidders(
      Set<RTBProfileBidder> rtbProfileBidders) {
    Set<PublisherRTBProfileBidderDTO> publisherRTBProfileBidders = new HashSet<>();

    for (RTBProfileBidder eachProfileBidder : rtbProfileBidders) {
      PublisherRTBProfileBidderDTO publisherBidderProfile =
          PublisherRTBProfileBidderDTOMapper.MAPPER.map(eachProfileBidder);
      publisherRTBProfileBidders.add(publisherBidderProfile);
    }

    return publisherRTBProfileBidders;
  }

  private Set<PublisherRTBProfileLibraryDTO> makeRTBProfileLibraries(
      Set<RTBProfileLibraryAssociation> associations) {
    Set<PublisherRTBProfileLibraryDTO> libraries = new HashSet<>();

    for (RTBProfileLibraryAssociation association : associations) {
      PublisherRTBProfileLibraryDTO library =
          publisherRTBProfileLibraryAssembler.make(association.getLibrary());
      libraries.add(library);
    }

    return libraries;
  }

  private void syncProfileBidders(
      RTBProfile rtbProfile, PublisherRTBProfileDTO publisherRTBProfile) {

    Set<PublisherRTBProfileBidderDTO> dtoProfileBidders =
        publisherRTBProfile.getRtbProfileBidders();
    Set<RTBProfileBidder> coreProfileBidders = rtbProfile.getBidderSeatWhitelists();

    if (dtoProfileBidders != null && !dtoProfileBidders.isEmpty()) {
      if (coreProfileBidders != null && !coreProfileBidders.isEmpty()) {
        // Update existing Profile Bidders by matching on Pids
        for (RTBProfileBidder coreProfileBidder : coreProfileBidders) {
          Long coreProfileBidderPid = coreProfileBidder.getPid();
          for (PublisherRTBProfileBidderDTO publisherProfileBidder : dtoProfileBidders) {
            validateData(publisherProfileBidder);
            if (publisherProfileBidder.getPid() != null) {
              if (!publisherProfileBidder.getPid().equals(coreProfileBidderPid)) {
                continue;
              } else {
                if (publisherProfileBidder.getVersion() != null
                    && !publisherProfileBidder
                        .getVersion()
                        .equals(coreProfileBidder.getVersion())) {
                  // throw stale data exception
                  throw new StaleStateException(
                      "PublisherRTBProfileBidder has a different version of the data");
                }
                coreProfileBidder =
                    PublisherRTBProfileBidderDTOMapper.MAPPER.map(
                        coreProfileBidder, publisherProfileBidder);
              }
            }
          }
        }

        Set<Long> currentPids = new HashSet<Long>();
        Set<Long> dtoPids = new HashSet<Long>();

        for (RTBProfileBidder currentProfileBidder : coreProfileBidders) {
          currentPids.add(currentProfileBidder.getPid());
        }

        if (dtoProfileBidders != null) {
          for (PublisherRTBProfileBidderDTO dtoProfileBidder : dtoProfileBidders) {
            dtoPids.add(dtoProfileBidder.getPid());
          }
        }

        Set<Long> deletedPids = Sets.difference(currentPids, dtoPids);

        // Remove unwanted core Profile Bidders
        for (Long deletedPid : deletedPids) {
          for (Iterator<RTBProfileBidder> it = coreProfileBidders.iterator(); it.hasNext(); ) {
            RTBProfileBidder profileBidder = it.next();
            if (profileBidder.getPid().equals(deletedPid)) {
              it.remove();
            }
          }
        }
      }

      // New Profile Bidders will not have pids. For all such items, create a new record of
      // RTBProfileBidder
      for (PublisherRTBProfileBidderDTO dtoProfileBidder : dtoProfileBidders) {
        if (dtoProfileBidder.getPid() == null) {
          Long bidderPid = dtoProfileBidder.getBidderPid();
          if (!bidderConfigRepository.existsById(bidderPid)) {
            throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND);
          }
          RTBProfileBidder newProfileBidder = new RTBProfileBidder();
          newProfileBidder.setRtbprofile(rtbProfile);
          newProfileBidder.setBidderPid(dtoProfileBidder.getBidderPid());
          newProfileBidder.setSeatWhitelist(joiner.join(dtoProfileBidder.getSeatWhitelist()));
          newProfileBidder.setVersion(0);
          coreProfileBidders.add(newProfileBidder);
        }
      }
    } else {
      if (coreProfileBidders != null && !coreProfileBidders.isEmpty()) {
        coreProfileBidders.clear();
      }
    }
  }

  private void validateData(PublisherRTBProfileBidderDTO publisherProfileBidder) {
    Long bidderPid = publisherProfileBidder.getBidderPid();
    if (!bidderConfigRepository.existsById(bidderPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_BIDDER_CONFIG_NOT_FOUND);
    }
    Set<String> whiteListItems = publisherProfileBidder.getSeatWhitelist();
    if (whiteListItems != null && !whiteListItems.isEmpty()) {
      for (String s : whiteListItems) {
        if (s.contains(",")) {
          throw new GenevaValidationException(
              ServerErrorCodes.SERVER_WHITELISTITEMS_CANNOT_CONTAIN_COMMAS);
        }
      }
    }
  }

  private void fillSiteTransparencySettings(
      RTBProfile rtbProfile, final PublisherRTBProfileDTO publisherRTBProfile) {
    TransparencySettingsDTO transparencySettings =
        publisherRTBProfile.getSiteTransparencySettings();
    if (transparencySettings != null) {
      TransparencyMode transparencyMode = transparencySettings.getTransparencyMode();
      if (transparencyMode != null) {
        rtbProfile.setIncludeSiteName(transparencyMode.asInt());
        switch (transparencyMode) {
          case Aliases:
            {
              rtbProfile.setSiteNameAlias(transparencySettings.getNameAlias());
              if (rtbProfile.getSiteAlias() == null
                  || BooleanUtils.isTrue(transparencySettings.getRegenerateIdAlias())) {
                rtbProfile.setSiteAlias(transparencyService.generateIdAlias());
              }
              break;
            }
          case None:
            {
              rtbProfile.setSiteNameAlias(null);
              if (rtbProfile.getSiteAlias() == null
                  || BooleanUtils.isTrue(transparencySettings.getRegenerateIdAlias())) {
                rtbProfile.setSiteAlias(transparencyService.generateIdAlias());
              }
              break;
            }
          case RealName:
            {
              rtbProfile.setSiteNameAlias(null);
              rtbProfile.setSiteAlias(null);
            }
        }
      }
    } else {
      rtbProfile.setIncludeSiteName(null);
      rtbProfile.setSiteNameAlias(null);
      rtbProfile.setSiteAlias(null);
    }
  }

  private void fillPublisherTransparencySettings(
      RTBProfile rtbProfile, final PublisherRTBProfileDTO publisherRTBProfile) {
    TransparencySettingsDTO transparencySettings =
        publisherRTBProfile.getPublisherTransparencySettings();
    if (transparencySettings != null) {
      TransparencyMode transparencyMode = transparencySettings.getTransparencyMode();
      if (transparencyMode != null) {
        rtbProfile.setIncludePubName(transparencyMode.asInt());
        switch (transparencyMode) {
          case Aliases:
            {
              rtbProfile.setPubNameAlias(transparencySettings.getNameAlias());
              if (rtbProfile.getPubAlias() == null
                  || BooleanUtils.isTrue(transparencySettings.getRegenerateIdAlias())) {
                rtbProfile.setPubAlias(transparencyService.generateIdAlias());
              }
              break;
            }
          case None:
            {
              rtbProfile.setPubNameAlias(null);
              if (rtbProfile.getPubAlias() == null
                  || BooleanUtils.isTrue(transparencySettings.getRegenerateIdAlias())) {
                rtbProfile.setPubAlias(transparencyService.generateIdAlias());
              }
              break;
            }
          case RealName:
            {
              rtbProfile.setPubNameAlias(null);
              rtbProfile.setPubAlias(null);
            }
        }
      }
    } else {
      rtbProfile.setIncludePubName(null);
      rtbProfile.setPubNameAlias(null);
      rtbProfile.setPubAlias(null);
    }
  }
}

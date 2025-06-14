package com.nexage.app.util.assemblers;

import static java.util.stream.Stream.concat;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileAssignmentsDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.context.PublisherDefaultRTBProfileContext;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import com.nexage.app.util.assemblers.context.PublisherTagContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublisherDefaultRTBProfileAssembler
    extends Assembler<
        PublisherDefaultRTBProfileDTO, RTBProfile, PublisherDefaultRTBProfileContext> {

  private static final Set<String> LOCAL_FIELDS =
      Set.of("tag", "defaultRtbProfileOwnerCompanyPid", "name");

  private static final Set<String> DEFAULT_DRP_FIELDS =
      concat(
              PublisherRTBProfileAssembler.DEFAULT_FIELDS.stream(),
              concat(LOCAL_FIELDS.stream(), Stream.of("rtbProfileAssignments")))
          .collect(Collectors.toSet());

  public static final Set<String> LIMITED_DRP_FIELDS =
      Set.of("pid", "id", "description", "version", "defaultRtbProfileOwnerCompanyPid", "name");

  protected static final Set<String> ALL_DRP_FIELDS =
      concat(LOCAL_FIELDS.stream(), PublisherRTBProfileAssembler.ALL_FIELDS.stream())
          .collect(Collectors.toSet());

  public static final Set<String> DRP_API_FIELDS =
      concat(
              LIMITED_DRP_FIELDS.stream(),
              Stream.of(
                  "pubNetReserve",
                  "includeConsumerId",
                  "includeSiteName",
                  "includeDomainReferences",
                  "includeConsumerProfile",
                  "siteNameAlias",
                  "pubNameAlias",
                  "screeningLevel",
                  "auctionType",
                  "lowReserve",
                  "pubNetLowReserve",
                  "alterReserve",
                  "siteTransparencySettings",
                  "publisherTransparencySettings",
                  "blockedAdTypes",
                  "blockedAttributes",
                  "defaultReserve",
                  "creationDate",
                  "lastUpdate",
                  "includeGeoData",
                  "tag",
                  "rtbProfileAssignments"))
          .collect(Collectors.toSet());

  private final PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  private final PublisherSiteAssembler publisherSiteAssembler;
  private final PublisherPositionAssembler publisherPositionAssembler;
  private final PublisherTagAssembler publisherTagAssembler;
  private final UserContext userContext;
  private final TagRepository tagRepository;

  @Override
  public PublisherDefaultRTBProfileDTO make(
      PublisherDefaultRTBProfileContext context, RTBProfile rtbProfile) {
    return make(context, rtbProfile, DEFAULT_DRP_FIELDS);
  }

  @Override
  public PublisherDefaultRTBProfileDTO make(
      PublisherDefaultRTBProfileContext context, RTBProfile rtbProfile, Set<String> fields) {

    PublisherDefaultRTBProfileDTO.Builder publisherDefaultRTBProfileBuilder =
        PublisherDefaultRTBProfileDTO.newBuilder();
    PublisherDefaultRTBProfileAssignmentsDTO.Builder publisherDefaultRtbProfileAssignmentsBuilder =
        PublisherDefaultRTBProfileAssignmentsDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_DRP_FIELDS;

    context.setProfileBuilder(publisherDefaultRTBProfileBuilder);
    publisherRTBProfileAssembler.make(context, rtbProfile, fields);

    for (String field : fieldsToMap) {

      switch (field) {
        case "tag":
          if (context.getCompany() != null
              && context.getCompany().getSellerAttributes() != null
              && context.getCompany().getSellerAttributes().getDefaultRtbProfile() != null) {
            PublisherTagContext tagContext = PublisherTagContext.newBuilder().build();
            Tag tag = context.getCompany().getSellerAttributes().getDefaultRtbProfile().getTag();
            publisherDefaultRTBProfileBuilder.withTag(
                publisherTagAssembler.make(
                    tagContext, tag, PublisherTagAssembler.DEFAULT_RTBPROFILE_FIELDS));
          }
          break;
        case "defaultRtbProfileOwnerCompanyPid":
          publisherDefaultRTBProfileBuilder.withDefaultRtbProfileOwnerCompanyPid(
              rtbProfile.getDefaultRtbProfileOwnerCompanyPid());
          break;

        case "rtbProfileAssignments":
          if (context.getRTBProfileSites() != null && !context.getRTBProfileSites().isEmpty()) {
            Set<PublisherSiteDTO> assignedSites = new HashSet<>();
            for (Site site : context.getRTBProfileSites()) {
              assignedSites.add(
                  publisherSiteAssembler.make(site, Set.of("pid", "name", "hbEnabled"), false));
            }
            publisherDefaultRtbProfileAssignmentsBuilder.withSitesForRTBProfile(assignedSites);
          }

          if (context.getRTBProfilePositions() != null
              && !context.getRTBProfilePositions().isEmpty()) {
            Set<PublisherPositionDTO> assignedPositions = new HashSet<>();
            for (Position position : context.getRTBProfilePositions()) {
              PublisherPositionContext positionContext =
                  PublisherPositionContext.newBuilder().build();
              assignedPositions.add(
                  publisherPositionAssembler.make(
                      positionContext, position, Set.of("pid", "memo")));
            }
            publisherDefaultRtbProfileAssignmentsBuilder.withPositionsForRTBProfile(
                assignedPositions);
          }

          publisherDefaultRtbProfileAssignmentsBuilder.withIsPublisherDefault(
              context.getIsPublisherDefault());
          publisherDefaultRTBProfileBuilder.withRtbProfileAssignments(
              publisherDefaultRtbProfileAssignmentsBuilder.build());
          break;

        default:
      }
    }

    return publisherDefaultRTBProfileBuilder.build();
  }

  public RTBProfile apply(
      PublisherDefaultRTBProfileContext context,
      RTBProfile model,
      PublisherDefaultRTBProfileDTO dto,
      boolean detail) {
    RTBProfile result;

    if (detail) {
      result = apply(context, model, dto);
    } else {
      if (dto.getPid() == null) {
        // new item - copy only name, description and defaultRtbProfileOwnerCompanyPid, all other
        // fields should be null for retrieving default values
        PublisherDefaultRTBProfileDTO filteredDTO = new PublisherDefaultRTBProfileDTO();
        filteredDTO.setName(dto.getName());
        filteredDTO.setDescription(dto.getDescription());
        filteredDTO.setDefaultRtbProfileOwnerCompanyPid(dto.getDefaultRtbProfileOwnerCompanyPid());
        result = apply(context, model, filteredDTO);
      } else {
        // already existed item - copy only name, description to the model
        validateRTBProfile(context, model, dto);
        if (StringUtils.isNotEmpty(dto.getName())) {
          model.setName(dto.getName());
        }
        initDescription(model, dto);
        result = model;
      }
    }
    return result;
  }

  @Override
  public RTBProfile apply(
      PublisherDefaultRTBProfileContext context,
      RTBProfile model,
      PublisherDefaultRTBProfileDTO dto) {
    validateRTBProfile(context, model, dto);
    if (dto.getTag() != null) {
      PublisherTagDTO publisherTag = dto.getTag();
      if (publisherTag.getRtbProfile() != null
          || publisherTag.getSite() != null
          || publisherTag.getPosition() != null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_INVALID_TAG_FOR_DEFAULT_RTB_PROFILE);
      }

      Tag tag =
          Optional.ofNullable(publisherTag.getPid())
              .flatMap(tagRepository::findById)
              .orElse(new Tag());

      PublisherTagContext tagContext = PublisherTagContext.newBuilder().forRTBProfile(true).build();
      tag = publisherTagAssembler.apply(tagContext, tag, publisherTag);

      if (tag.getClickthroughDisable() == null) {
        tag.setClickthroughDisable(false);
      }
      if (tag.getReturnRawResponse() == null) {
        tag.setReturnRawResponse(false);
      }

      tag.setRtbProfile(model);
      model.setTag(tag);
      context.setTag(tag);
    }

    model = publisherRTBProfileAssembler.apply(context, model, dto);
    initDescription(model, dto);
    if (dto.getScreeningLevel() != null) {
      model.setScreeningLevel(
          RTBProfile.ScreeningLevel.valueOf(dto.getScreeningLevel().toString()));
    }
    if (dto.getAlterReserve() != null) {
      model.setAlterReserve(dto.getAlterReserve());
    }
    model.setOwnerCompany(context.getCompany());

    return model;
  }

  private void validateRTBProfile(
      PublisherDefaultRTBProfileContext context,
      RTBProfile model,
      PublisherDefaultRTBProfileDTO dto) {
    if (!userContext.isNexageAdminOrManager()) {
      throw new GenevaSecurityException(
          ServerErrorCodes.SERVER_NOT_AUTHORIZED_FOR_DEFAULT_RTB_PROFILE);
    }

    if (dto.getPid() != null
        && !context.getCompany().getPid().equals(model.getDefaultRtbProfileOwnerCompanyPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILE_NOT_FOUND);
    }

    if (context.getCompany() != null && !context.getCompany().isDefaultRtbProfilesEnabled()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DEFAULT_RTB_PROFILES_NOT_ENABLED_FOR_COMPANY);
    }
  }

  private void initDescription(RTBProfile model, PublisherDefaultRTBProfileDTO dto) {
    model.setDescription(
        (StringUtils.isNotEmpty(dto.getDescription()))
            ? dto.getDescription()
            : "Self Service Created Default RTB Profile");
  }
}

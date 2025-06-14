package com.nexage.app.services.impl;

import static java.util.Objects.nonNull;

import com.google.common.base.Splitter;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.RtbProfileLibrary;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.projections.SiteWithInactiveTagProjection;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
import com.nexage.admin.core.util.UUIDGenerator;
import com.nexage.app.dto.tag.TagCleanupResultsDTO;
import com.nexage.app.dto.tag.TagDeploymentInfoDTO;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.SellerTagService;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.validator.RTBProfileValidator;
import com.nexage.app.util.validator.TagValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Methods used in reference to Seller's tags.
 *
 * <p>NOTE: Please do not add any new functionality to this class or its implementations. The
 * business concepts associated to the different operations handled by the following functions use
 * strategies, concepts, or frameworks considered deprecated. Be sure you reach to the core team
 * before considering designing or implementing new functionality under this class.
 */
@Log4j2
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcManagerSeller()")
public class SellerTagServiceImpl implements SellerTagService {
  private static final String ZONE_ID = "America/New_York";
  private static final String START_DATE = "startDate";
  private static final String STOP_DATE = "stopDate";
  private static final String TAG_PID = "tagPid";
  private static final String TAG_PERF_METRICS =
      "select d.id, d.name, coalesce(sum(ads_requested_adnet), 0) as requests, coalesce(sum(ads_served),0) as served, coalesce(sum(ads_delivered),0) as delivered "
          + ", coalesce(sum(ads_clicked),0) as clicked, coalesce(sum(revenue),0) as revenue "
          + ", coalesce(sum(revenue_net),0) as netRevenue "
          + " FROM dim_tag d "
          + " LEFT OUTER JOIN fact_revenue_adnet_vw_daily f "
          + " ON  f.tag_id = d.id "
          + " AND start >= :startDate "
          + " AND start < :stopDate "
          + " WHERE d.id IN (:tagPid) "
          + " GROUP BY d.id, d.name "
          + " ORDER BY d.id";
  private final UUIDGenerator uuidGen = new UUIDGenerator();

  private final UserContext userContext;
  private final SiteRepository siteRepository;
  private final PhoneCastConfigService phoneCastConfigService;
  private final RTBProfileRepository rtbProfileRepository;
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate coreNamedTemplate;
  private final NamedParameterJdbcTemplate dwNamedTemplate;
  private final SellerSiteService sellerSiteService;
  private final TagValidator tagValidator;
  private final RTBProfileValidator rtbProfileValidator;
  private final RTBProfileUtil rtbProfileUtil;
  private final GlobalConfigService globalConfigService;
  private final TagRepository tagRepository;
  private final EntityManager entityManager;

  private static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public SellerTagServiceImpl(
      SiteRepository siteRepository,
      UserContext userContext,
      RTBProfileUtil rtbProfileUtil,
      PhoneCastConfigService phoneCastConfigService,
      RTBProfileRepository rtbProfileRepository,
      @Qualifier("coreServicesJdbcTemplate") JdbcTemplate jdbcTemplate,
      GlobalConfigService globalConfigService,
      @Qualifier("coreNamedJdbcTemplate") NamedParameterJdbcTemplate coreNamedTemplate,
      @Qualifier("dwNamedJdbcTemplate") NamedParameterJdbcTemplate dwNamedTemplate,
      SellerSiteService sellerSiteService,
      TagValidator tagValidator,
      RTBProfileValidator rtbProfileValidator,
      TagRepository tagRepository,
      EntityManager entityManager) {
    this.siteRepository = siteRepository;
    this.userContext = userContext;
    this.rtbProfileUtil = rtbProfileUtil;
    this.phoneCastConfigService = phoneCastConfigService;
    this.rtbProfileRepository = rtbProfileRepository;
    this.jdbcTemplate = jdbcTemplate;
    this.globalConfigService = globalConfigService;
    this.coreNamedTemplate = coreNamedTemplate;
    this.dwNamedTemplate = dwNamedTemplate;
    this.sellerSiteService = sellerSiteService;
    this.tagValidator = tagValidator;
    this.rtbProfileValidator = rtbProfileValidator;
    this.tagRepository = tagRepository;
    this.entityManager = entityManager;
  }

  public static SiteDealTerm findAnyDealTermMatchingTagPid(Set<SiteDealTerm> terms, Long tagPid) {
    if (tagPid == null) {
      return null;
    }

    return terms.stream()
        .filter(Objects::nonNull)
        .filter(term -> tagPid.equals(term.getTagPid()))
        .findFirst()
        .orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.canAccessSite(#tag.getSitePid()) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) )")
  public Site updateTag(Tag tag) {
    tagValidator.validateTag(tag);

    Site site = sellerSiteService.getSite(tag.getSitePid());
    updateTagDealtermsIfRequired(site, tag);
    // remove the old tag
    removeOldTag(site, tag);

    // add the updated tag
    tag.setSite(site);
    site.getTags().add(tag);

    // See if position exists for Tag for PSS, then update its updatedOn
    updateUpdatedOnIfPositionExistsForTag(tag, site);

    if (tag.getRules() != null && !tag.getRules().isEmpty()) {
      for (TagRule rule : tag.getRules()) {
        rule.setTag(tag);
      }
    }

    Site updated = siteRepository.save(site);
    updated.getMetadata().setSavedMediationTagId(tag.getIdentifier());
    return updated;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.canAccessSite(#sitePid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) )")
  public Site createExchangeTag(
      Long sitePid, Tag tag, RTBProfile rtbProfile, boolean fromSellerAdmin) {
    if ((tag.getSitePid() != null && !sitePid.equals(tag.getSitePid()))
        && (rtbProfile.getSitePid() != null && !sitePid.equals(rtbProfile.getSitePid()))) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }

    tagValidator.validateTag(tag);
    rtbProfileValidator.validateRtbProfile(rtbProfile);
    Site site = sellerSiteService.getSite(sitePid);
    rtbProfileUtil.adjustReservesWithDealTerm(site, tag, rtbProfile);

    Set<RtbProfileLibrary> candidateLibraries;
    Site updated = new Site();
    String id = "";

    if (nonNull(rtbProfile)) {
      candidateLibraries = rtbProfileUtil.getRTBProfileLibraries(rtbProfile.getLibraryPids());

      // create RTB profile
      id = uuidGen.generateUniqueId();
      rtbProfile.setExchangeSiteTagId(id);
      rtbProfile.setSite(site);

      rtbProfile.setTag(tag);
      tag.setRtbProfile(rtbProfile);

      rtbProfileUtil.syncRTBProfileLibraries(rtbProfile, candidateLibraries, null);

      updated = siteRepository.save(site);
      updated.getRtbProfiles().add(rtbProfile);
    }

    // create tag
    tag.setPrimaryId(id);

    tag.setSite(site);
    Site finalSite = createTag(updated, tag, fromSellerAdmin);

    finalSite.getMetadata().setSavedRtbProfileId(id);

    rtbProfileUtil.populateRTBProfileLibraryPids(finalSite);
    return finalSite;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.canAccessSite(#tag.getSitePid()) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) )")
  public Site updateExchangeTag(Tag tag, RTBProfile rtbProfile) {
    tagValidator.validateTag(tag);
    rtbProfile.setTag(tag);
    tag.setRtbProfile(rtbProfile);

    rtbProfileValidator.validateRtbProfile(rtbProfile);
    Site site = sellerSiteService.getSite(tag.getSitePid());
    updateTagDealtermsIfRequired(site, tag);

    rtbProfileUtil.adjustReservesWithDealTerm(site, tag, rtbProfile);

    // We have to retrieve libraries way before we want to use them because
    // Hibernate will have a meltdown
    // if we try to bring back data after existing persisted data has been
    // changed
    Set<RtbProfileLibrary> candidateLibraries =
        rtbProfileUtil.getRTBProfileLibraries(rtbProfile.getLibraryPids());

    // remove the old tag
    removeExchangeTag(site, tag);

    // add the updated tag
    tag.setSite(site);
    site.getTags().add(tag);

    // remove the old rtb profile
    Set<RTBProfileLibraryAssociation> currentLibraryAssociations =
        removeExhangeOldRTBTag(site, rtbProfile);

    rtbProfileUtil.syncRTBProfileLibraries(
        rtbProfile, candidateLibraries, currentLibraryAssociations);

    // add the updated profile
    rtbProfile.setSite(site);
    site.getRtbProfiles().add(rtbProfile);

    // See if position exists for Tag for PSS, then update its updatedOn
    updateUpdatedOnIfPositionExistsForTag(tag, site);
    rtbProfile.onUpdate();
    tagRepository.save(tag);
    rtbProfileRepository.save(rtbProfile);

    Site updated = siteRepository.save(site);
    updated.getMetadata().setSavedMediationTagId(tag.getIdentifier());
    updated.getMetadata().setSavedRtbProfileId(rtbProfile.getExchangeSiteTagId());

    rtbProfileUtil.populateRTBProfileLibraryPids(updated);
    return updated;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or (@loginUserContext.canAccessSite(#sitePid) "
          + "and (@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller()) )")
  public Site createTag(Long sitePid, Tag tag, boolean fromSellerAdmin) {
    if (tag.getSitePid() != null && !sitePid.equals(tag.getSitePid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }

    Site site = sellerSiteService.getSite(sitePid);

    return createTag(site, tag, fromSellerAdmin);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or ((@loginUserContext.canAccessSite(#site.getPid()) and @loginUserContext.isOcAdminSeller()) "
          + "or (@loginUserContext.canAccessSite(#site.getPid()) and @loginUserContext.isOcManagerSeller()) )")
  public Site createTag(Site site, Tag tag, boolean fromSellerAdmin) {
    tagValidator.validateTag(tag);
    tag.setSite(site);

    Set<TagRule> rules = new HashSet<>(tag.getRules()); // cache TagRule
    SiteDealTerm tagDealTerm = tag.getCurrentDealTerm(); // cache Tag
    // override
    // dealterm
    String id = uuidGen.generateUniqueId();
    tag.setIdentifier(id);
    site.getTags().add(tag);

    // remove rules and add later
    tag.getRules().clear();

    // See if position exists for Tag for PSS, then update its updatedOn
    updateUpdatedOnIfPositionExistsForTag(tag, site);

    //      enable pfo for tag if company level switch is on and vice-versa (MX-1610)
    //      change value to default only if alterReserve not specified (MX-3177)
    rtbProfileUtil.setAlterReserveForTag(site.getCompany(), tag.getRtbProfile());

    Site updated = siteRepository.saveAndFlush(site);
    entityManager.refresh(updated);
    updated = determineUpdateRequiredForTag(tagDealTerm, fromSellerAdmin, updated, id, rules);
    updated.getMetadata().setSavedMediationTagId(id);
    return updated;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or ((@loginUserContext.canAccessSite(#sitePid) and @loginUserContext.isOcAdminSeller()) "
          + "or (@loginUserContext.canAccessSite(#sitePid) and @loginUserContext.isOcManagerSeller()) )")
  public Site deleteTag(Long sitePid, Long tagPid) {
    Site site = sellerSiteService.getSite(sitePid);
    boolean matchFound = false;
    Long positionPid = null;
    for (Iterator<Tag> it = site.getTags().iterator(); it.hasNext(); ) {
      Tag tag = it.next();
      if (tag.getPid().equals(tagPid)) {
        matchFound = true;
        // Check if this is a PSS position, if so, save its position Pid
        if (tag.getPosition() != null) {
          positionPid = tag.getPosition().getPid();
        }
        it.remove();
        break;
      }
    }
    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }

    if (positionPid != null) {
      setUpdatedOnForPositions(site, positionPid);
    }
    return siteRepository.save(site);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.canAccessSite(#sitePid)")
  public TagDeploymentInfoDTO getTagDeploymentInfo(Long sitePid, Long tagPid) {

    StringBuilder query =
        new StringBuilder(
                "select tt.tier_pid, tr.level, t.position_pid, p.name, p.pid from tier_tag tt ")
            .append("join tier tr on tr.pid = tt.tier_pid ")
            .append("join position p on tr.position_pid = p.pid ")
            .append("join tag t on t.pid = tt.tag_pid ")
            .append("where tt.tag_pid=")
            .append(tagPid);

    log.debug("deployment query: " + query);

    SqlRowSet rowset = jdbcTemplate.queryForRowSet(query.toString());
    TagDeploymentInfoDTO info = new TagDeploymentInfoDTO(tagPid);
    try {
      int count = 0;
      Map<Long, TagDeploymentInfoDTO.Position> posMap = new HashMap<>();
      while (rowset.next()) {
        info.setSelfServePosition(rowset.getLong("position_pid"));
        Long ppid = rowset.getLong("pid");
        String name = rowset.getString("name");
        TagDeploymentInfoDTO.Position position = posMap.get(ppid);
        if (null == position) {
          position = new TagDeploymentInfoDTO.Position(ppid, name);
          posMap.put(ppid, position);
          info.addPosition(position);
        }
        long tierPid = rowset.getLong("tier_pid");
        TagDeploymentInfoDTO.Tier tier = new TagDeploymentInfoDTO.Tier(tierPid);
        tier.setLevel(rowset.getInt("level"));
        position.addTier(tier);
        count++;
      }
      checkCount(count);
    } catch (InvalidResultSetAccessException e) {
      log.debug("exception accessing core data: " + e.getMessage());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }

    if (!userContext.isNexageUser()) {
      int positionCount = info.getNumPositions();
      // pub self serve user cannot undeploy tags that are deployed to
      // any position other than the self serve position
      info.setIsUndeployValid(positionCount <= 1);
    } else {
      info.setIsUndeployValid(true);
    }

    for (TagDeploymentInfoDTO.Position position : info.getPositions()) {

      for (TagDeploymentInfoDTO.Tier tier : position.getTiers()) {
        SqlRowSet tierSet =
            jdbcTemplate.queryForRowSet(
                "select count(*) from tier_tag where tier_pid = ?", tier.getPid());
        try {
          while (tierSet.next()) {
            int count = tierSet.getInt(1);
            tier.setNumTags(count);
          }
        } catch (InvalidResultSetAccessException e) {
          log.debug("exception accessing tier data: " + e.getMessage());
        }
      }
    }

    return info;
  }

  private void checkCount(int count) {
    if (0 == count) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.canAccessSite(#sitePid)")
  public Site undeployTag(Long sitePid, Long positionPid, Long tagPid) {
    return undeployTagWithStatus(sitePid, tagPid, Status.INACTIVE);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize("@loginUserContext.isOcAdminNexage()")
  public TagCleanupResultsDTO cleanupTagDeployments(Long sitePid) {
    long tagsRemoved = 0L;
    long tiersRemoved = 0L;

    List<Site> sitesWithInactiveTags = findSitesWithInactiveTagsBySitePid(sitePid);

    for (Site undeployedSite : sitesWithInactiveTags) {
      Site site = sellerSiteService.getSite(undeployedSite.getPid());

      for (Position undeployedPosition : undeployedSite.getPositions()) {
        Position matchedPosition =
            site.getPositions().stream()
                .filter(position -> position.getPid().equals(undeployedPosition.getPid()))
                .findFirst()
                .orElse(null);

        if (matchedPosition == null) {
          log.debug(
              "Couldn't find a real corresponding position with pid {}",
              undeployedPosition.getPid());
          continue;
        }

        for (Tier undeployedTier : undeployedPosition.getTiers()) {
          Tier matchedTier =
              matchedPosition.getTiers().stream()
                  .filter(tier -> tier.getPid().equals(undeployedTier.getPid()))
                  .findFirst()
                  .orElse(null);

          if (matchedTier == null) {
            log.debug(
                "Couldn't find a real corresponding tier with pid {}", undeployedTier.getPid());
            continue;
          }

          for (Tag undeployedTag : undeployedTier.getTags()) {
            Tag inactiveTag =
                matchedTier.getTags().stream()
                    .filter(
                        tag ->
                            tag.getPid().equals(undeployedTag.getPid())
                                && tag.getStatus() == Status.INACTIVE)
                    .findFirst()
                    .orElse(null);

            if (inactiveTag == null) {
              log.debug(
                  "Couldn't find a real corresponding tag with pid {}", undeployedTag.getPid());
              continue;
            }

            matchedTier.removeTag(inactiveTag);
            tagsRemoved++;
            log.info("Removed inactive tag with pid {} from the tier", inactiveTag.getPid());
          }

          // Now that we have removed all relevant tags from the tier
          // we need to check whether we need to remove the tier
          // itself
          if (matchedTier.getTags().isEmpty()) {
            matchedPosition.removeTier(matchedTier);
            tiersRemoved++;
            log.info("Removed tier without tags with pid {}", matchedTier.getPid());
          }
        }
      }
      siteRepository.save(site);
    }

    return new TagCleanupResultsDTO(tagsRemoved, tiersRemoved);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() "
          + "or ((@loginUserContext.canAccessSite(#sitePid) and @loginUserContext.isOcAdminSeller()) "
          + "or (@loginUserContext.canAccessSite(#sitePid) and @loginUserContext.isOcManagerSeller()) )")
  public Site deleteExchangeTag(Long sitePid, Long tagPid) {
    Site site = sellerSiteService.getSite(sitePid);
    boolean matchFound = false;
    String profileId = null;
    for (Iterator<Tag> it = site.getTags().iterator(); it.hasNext(); ) {
      Tag tag = it.next();
      if (tag.getPid().equals(tagPid)) {
        matchFound = true;
        profileId = tag.getPrimaryId();
        it.remove();
        break;
      }
    }
    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }

    matchFound = false;
    for (Iterator<RTBProfile> it = site.getRtbProfiles().iterator(); it.hasNext(); ) {
      RTBProfile rtbProfile = it.next();
      if (rtbProfile.getExchangeSiteTagId().equals(profileId)) {
        matchFound = true;
        it.remove();
        break;
      }
    }
    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND);
    }
    return siteRepository.save(site);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.canAccessSite(#sitePid)")
  public TagPerformanceMetricsDTO.Builder getTagPerformanceMetrics(
      final Long sitePid, final Long tagPid) {

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ZONE_ID));
    ZonedDateTime startDate;
    ZonedDateTime stopDate;
    startDate = now.truncatedTo(ChronoUnit.DAYS).minusDays(7);
    stopDate = now.truncatedTo(ChronoUnit.DAYS);

    SqlParameterSource namedParameters =
        new MapSqlParameterSource(START_DATE, startDate.format(dateTimeFormatter))
            .addValue(STOP_DATE, stopDate.format(dateTimeFormatter))
            .addValue(TAG_PID, tagPid);

    return dwNamedTemplate.queryForObject(
        TAG_PERF_METRICS,
        namedParameters,
        (rs, rowNum) -> {
          TagPerformanceMetricsDTO.Builder builder =
              new TagPerformanceMetricsDTO.Builder()
                  .withDefaults(
                      tagPid,
                      rs.getString("name"),
                      rs.getLong("requests"),
                      rs.getBigDecimal("netRevenue"));
          builder.withClicks(rs.getLong("clicked"));
          builder.withDelivered(rs.getLong("delivered"));
          builder.withServed(rs.getLong("served"));
          return builder;
        });
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.canAccessSite(#sitePid)")
  public List<TagPerformanceMetricsDTO> getTagPerformanceMetrics(
      final Long sitePid, final Collection<Long> tagPids) {
    if (CollectionUtils.isNotEmpty(tagPids)) {
      ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ZONE_ID));
      ZonedDateTime startDate = now.truncatedTo(ChronoUnit.DAYS).minusDays(7);
      ZonedDateTime stopDate = now.truncatedTo(ChronoUnit.DAYS);

      SqlParameterSource namedParameters =
          new MapSqlParameterSource(START_DATE, startDate.format(dateTimeFormatter))
              .addValue(STOP_DATE, stopDate.format(dateTimeFormatter))
              .addValue(TAG_PID, tagPids);

      return dwNamedTemplate.query(
          TAG_PERF_METRICS,
          namedParameters,
          (rs, rowNum) ->
              new TagPerformanceMetricsDTO.Builder()
                  .withDefaults(
                      rs.getLong("id"),
                      rs.getString("name"),
                      rs.getLong("requests"),
                      rs.getBigDecimal("netRevenue"))
                  .withClicks(rs.getLong("clicked"))
                  .withDelivered(rs.getLong("delivered"))
                  .withServed(rs.getLong("served"))
                  .build());
    }
    return new ArrayList<>();
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(@loginUserContext.isOcAdminNexage() or @loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserNexage()) "
          + "or ((@loginUserContext.isOcAdminSeller() or @loginUserContext.isOcManagerSeller() or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisher) )")
  public BigDecimal getTagRevenue(Long tagPid, Long publisher) {
    ZonedDateTime startDate = ZonedDateTime.now(ZoneId.of(ZONE_ID));
    ZonedDateTime stopDate = startDate.truncatedTo(ChronoUnit.DAYS).minusDays(30);

    String sql =
        "select sum(revenue) from fact_revenue_adnet_vw_daily where tag_id=:tagPid AND start >=:stopDate AND start <:startDate";
    SqlParameterSource paramMap =
        new MapSqlParameterSource(STOP_DATE, stopDate.format(dateFormatter))
            .addValue(START_DATE, startDate.format(dateFormatter))
            .addValue(TAG_PID, tagPid);
    return dwNamedTemplate.queryForObject(sql, paramMap, BigDecimal.class);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize("@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()")
  public List<TagPerformanceMetricsDTO> getTagPerformanceMetrics(final List<Tag> tags) {
    Map<Long, List<Long>> tagsPerSite = new HashMap<>();
    for (Tag tag : tags) {
      List<Long> ids = tagsPerSite.getOrDefault(tag.getSitePid(), new ArrayList<>());
      ids.add(tag.getPid());
      tagsPerSite.put(tag.getSitePid(), ids);
    }

    List<TagPerformanceMetricsDTO> metrics = new ArrayList<>();
    for (Map.Entry<Long, List<Long>> entry : tagsPerSite.entrySet()) {
      metrics.addAll(getTagPerformanceMetrics(entry.getKey(), entry.getValue()));
    }

    return metrics;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  @PreAuthorize(
      "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller()) "
          + "and @loginUserContext.doSameOrNexageAffiliation(#publisherId)")
  public List<Tag> getPubAdsourceTags(final Long publisherId, final Long adsourceId) {
    return tagRepository.findForAdSource(publisherId, adsourceId);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.canAccessSite(#site.pid) and "
          + "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcUserSeller())")
  public Site archiveTag(Site site, Long positionPid, Long tagPid) {
    Site result;
    if (isTagDeployed(tagPid)) {
      result = undeployTagWithStatus(site.getPid(), tagPid, Status.DELETED);
    } else {
      result = archiveTagOnSite(site, positionPid, tagPid);
    }
    return result;
  }

  private Site determineUpdateRequiredForTag(
      SiteDealTerm tagDealTerm,
      boolean fromSellerAdmin,
      Site updated,
      String id,
      Set<TagRule> rules) {
    boolean updateRequired = false;
    Tag updatedTag = null;
    if (tagDealTerm != null
        && (!fromSellerAdmin
            || null != tagDealTerm.getNexageRevenueShare()
            || null != tagDealTerm.getRtbFee())) { // add tag dealterm override
      updatedTag = getTagFromSite(updated, id);
      if (nonNull(updatedTag)) {
        updatedTag.setCurrentDealTerm(tagDealTerm);
        setNexageRevShare(updatedTag);
        updateRequired = updateTagDealtermsIfRequired(updated, updatedTag);
      }
    }
    if (!rules.isEmpty()) { // add rules to tag
      if (updatedTag == null) {
        updatedTag = getTagFromSite(updated, id);
      }
      for (TagRule rule : rules) {
        rule.setTag(updatedTag);
        if (nonNull(updatedTag)) {
          updatedTag.getRules().add(rule);
        }
      }
      updateRequired = true;
    }

    if (updateRequired) {
      updated = siteRepository.save(updated);
    }

    return updated;
  }

  /**
   * Sets the nexage revenue share.
   *
   * @param tag The {@link Tag} to set the nexage revenue share for.
   */
  private void setNexageRevShare(final Tag tag) {
    SiteDealTerm dealTerm = tag.getCurrentDealTerm();
    String adsourcePids =
        globalConfigService.getStringValue(
            GlobalConfigProperty.GENEVA_ADSOURCES_WITH_ZERO_NEXAGE_REV_SHARE);
    boolean hideNexageRevShare = false;

    if (!StringUtils.isBlank(adsourcePids)) {
      List<String> adsourcePidFromConfig =
          Splitter.on(",").omitEmptyStrings().trimResults().splitToList(adsourcePids);
      hideNexageRevShare = adsourcePidFromConfig.contains(tag.getBuyerPid().toString());
    }
    if (hideNexageRevShare) {
      dealTerm.setNexageRevenueShare(BigDecimal.ZERO);
      dealTerm.setRevenueMode(SiteDealTerm.RevenueMode.REV_SHARE);
    }
  }

  private Tag getTagFromSite(Site site, String tagId) {
    for (Tag tag : site.getTags()) {
      if (tagId.equals(tag.getIdentifier())) {
        return tag;
      }
    }
    return null;
  }

  private void setUpdatedOnForPositions(Site site, Long positionPid) {
    Set<Position> sitePositions = site.getPositions();
    if (sitePositions != null && !sitePositions.isEmpty()) {
      for (Position pos : sitePositions) {
        if (pos.getPid().equals(positionPid)) {
          pos.setUpdatedOn(Calendar.getInstance().getTime());
        }
      }
    }
  }

  private Site undeployTagWithStatus(Long sitePid, Long tagPid, Status status) {
    Site result;
    TagDeploymentInfoDTO info = getTagDeploymentInfo(sitePid, tagPid);

    if (!info.isUndeployTagValid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_SELF_SERVE_TAG_UNDEPLOY);
    }

    Site site = sellerSiteService.getSite(sitePid);
    // make sure the site and tag match, if site is given
    if (!sitePid.equals(site.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND);
    }

    Tag tag = checkTagPidAgainstSiteTags(tagPid, site);

    tag.setStatus(status);
    Set<Position> positions = site.getPositions();

    for (Position position : positions) {
      TagDeploymentInfoDTO.Position undeployPos = info.findPosition(position.getPid());
      removeTagFromTierForUndeployment(undeployPos, position, tag);
    }
    if (tag.isExchangeTag() && tagPid > 0L) {
      rtbProfileUtil.updateRTBProfileForTag(tagPid);
    }
    if (status == Status.DELETED) {
      site.setLastUpdate(Calendar.getInstance().getTime());
      result = siteRepository.saveAndFlush(site);
      return result;
    }
    return siteRepository.save(site);
  }

  private void removeTagFromTierForUndeployment(
      TagDeploymentInfoDTO.Position undeployPos, Position position, Tag tag) {
    if (null != undeployPos) {
      for (Iterator<Tier> it = position.getTiers().iterator(); it.hasNext(); ) {
        Tier tier = it.next();
        TagDeploymentInfoDTO.Tier undeployTier = undeployPos.findTier(tier.getPid());
        if (null != undeployTier) {
          tier.removeTag(tag);
          if (1 == undeployTier.getNumTags()) {
            // this was the only tag in this tier, so also
            // remove the tier
            it.remove();
            position.renumberTiers();
          }
        }
      }
      position.setUpdatedOn(Calendar.getInstance().getTime());
    }
  }

  private boolean updateTagDealtermsIfRequired(Site site, Tag tag) {
    SiteDealTerm currentDealTerm = tag.getCurrentDealTerm();
    // additional protection during client bugs
    if (dealTermsHaveBeenUpdated(currentDealTerm, site.getDealTerms(), tag.getPid())) {
      Date d = Calendar.getInstance().getTime();
      if (nonNull(currentDealTerm)) {
        currentDealTerm.setPid(null);
        currentDealTerm.setSite(site);
        currentDealTerm.setTagPid(tag.getPid());
        currentDealTerm.setEffectiveDate(d);
        if (log.isDebugEnabled()) log.debug("There is a new deal term for tag: " + currentDealTerm);
        site.addToDealTerms(currentDealTerm);
      }
      return true;
    }
    return false;
  }

  private Tag checkTagPidAgainstSiteTags(Long tagPid, Site site) {
    Set<Tag> tags = site.getTags();
    Tag tag = null;
    for (Tag siteTag : tags) {
      if (tagPid.equals(siteTag.getPid())) {
        tag = siteTag;
        break;
      }
    }
    if (null == tag) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }

    return tag;
  }

  private void removeExchangeTag(Site site, Tag tag) {
    boolean matchFound = false;
    for (Iterator<Tag> it = site.getTags().iterator(); it.hasNext(); ) {
      Tag oldTag = it.next();
      if (oldTag.getPid().equals(tag.getPid())) {
        matchFound = true;
        it.remove();
        break;
      }
    }
    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }

    if (tag.getRules() != null && !tag.getRules().isEmpty()) {
      for (TagRule rule : tag.getRules()) {
        rule.setTag(tag);
      }
    }
  }

  private Set<RTBProfileLibraryAssociation> removeExhangeOldRTBTag(
      Site site, RTBProfile rtbProfile) {
    boolean matchFound = false;
    Set<RTBProfileLibraryAssociation> currentLibraryAssociations = null;

    for (Iterator<RTBProfile> it = site.getRtbProfiles().iterator(); it.hasNext(); ) {
      RTBProfile oldProfile = it.next();
      if (oldProfile.getPid().equals(rtbProfile.getPid())) {
        matchFound = true;
        currentLibraryAssociations = oldProfile.getLibraries();
        it.remove();
        break;
      }
    }
    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND);
    }

    return currentLibraryAssociations;
  }

  private void updateUpdatedOnIfPositionExistsForTag(Tag tag, Site site) {
    if (tag.getPosition() != null) {
      Long positionPid = tag.getPosition().getPid();
      Set<Position> sitePositions = site.getPositions();
      if (sitePositions != null && !sitePositions.isEmpty()) {
        for (Position pos : sitePositions) {
          if (pos.getPid().equals(positionPid)) {
            pos.setUpdatedOn(Calendar.getInstance().getTime());
            break;
          }
        }
      }
    }
  }

  private void removeOldTag(Site site, Tag tag) {
    boolean matchFound = false;
    Set<Long> exchangeIds = phoneCastConfigService.getValidExchangeIds();
    for (Iterator<Tag> it = site.getTags().iterator(); it.hasNext(); ) {
      Tag oldTag = it.next();
      if (oldTag.getPid().equals(tag.getPid())) {
        if (exchangeIds.contains(tag.getBuyerPid())) {
          List<Tag> tagsWithTheSameRtbProfileAndActiveSite =
              tagRepository.findByPrimaryIdWithActiveSite(tag.getPrimaryId());
          boolean differentExchangeTagPointsToThisRtbProfile =
              tagsWithTheSameRtbProfileAndActiveSite.stream()
                  .anyMatch(
                      tagWithActiveSite ->
                          exchangeIds.contains(tagWithActiveSite.getBuyerPid())
                              && !tag.getPid().equals(tagWithActiveSite.getPid()));
          if (differentExchangeTagPointsToThisRtbProfile) {
            throw new GenevaValidationException(
                ServerErrorCodes.SERVER_DUPLICATE_TAGS_WITH_SAME_RTB_PROFILE);
          }
        }
        matchFound = true;
        it.remove();
        break;
      }
    }
    if (!matchFound) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TAG_NOT_FOUND);
    }
  }

  private boolean isTagDeployed(Long tagPid) {
    SqlParameterSource namedParameters = new MapSqlParameterSource(TAG_PID, tagPid);
    Long count =
        coreNamedTemplate.queryForObject(
            "select count(*) from tier_tag where tag_pid = :tagPid", namedParameters, Long.class);
    return count > 0;
  }

  private Site archiveTagOnSite(Site site, Long positionPid, Long tagPid) {
    Set<Position> positions = site.getPositions();
    Position position = null;
    for (Position sitePos : positions) {
      if (positionPid.equals(sitePos.getPid())) {
        position = sitePos;
        break;
      }
    }
    if (null == position) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE);
    }

    Tag tag = checkTagPidAgainstSiteTags(tagPid, site);

    if (tag.isExchangeTag() && tagPid > 0L) {
      rtbProfileUtil.updateRTBProfileForTag(tagPid);
    }

    tag.setStatus(Status.DELETED);
    Date now = Calendar.getInstance().getTime();
    position.setUpdatedOn(now);
    site.setLastUpdate(now);
    return siteRepository.saveAndFlush(site);
  }

  private boolean dealTermsHaveBeenUpdated(
      SiteDealTerm possiblyNewDealTerm, Set<SiteDealTerm> existingTerms, Long tagPid) {
    SiteDealTerm oldTerms = findAnyDealTermMatchingTagPid(existingTerms, tagPid);
    if (null == oldTerms && possiblyNewDealTerm != null) {
      return true;
    }
    if (oldTerms != null) {
      if (null == possiblyNewDealTerm) {
        return true;
      }

      if (!areEqual(
          oldTerms.getNexageRevenueShare(), possiblyNewDealTerm.getNexageRevenueShare())) {
        return true;
      }

      return !areEqual(oldTerms.getRtbFee(), possiblyNewDealTerm.getRtbFee());
    }

    return false;
  }

  private boolean areEqual(BigDecimal first, BigDecimal second) {
    if (null != first) {
      return null != second && 0 == first.compareTo(second);
    } else { // first is null
      return null == second;
    }
  }

  private List<Site> findSitesWithInactiveTagsBySitePid(Long sitePid) {
    List<SiteWithInactiveTagProjection> siteWithInactiveTagProjections;
    if (sitePid > 0) {
      siteWithInactiveTagProjections =
          siteRepository.findSiteWithInactiveTagProjectionsByPid(sitePid);
    } else {
      siteWithInactiveTagProjections = siteRepository.findAllSiteWithInactiveTagProjections();
    }
    List<Site> sitesWithInactiveTags = new ArrayList<>();
    Site currSite = null;
    Position currPosition = null;
    Tier currTier = null;

    for (SiteWithInactiveTagProjection siteWithInactiveTagProjection :
        siteWithInactiveTagProjections) {
      if (currSite == null
          || !currSite.getPid().equals(siteWithInactiveTagProjection.getSitePid())) {
        currSite = new Site();
        currSite.setPid(siteWithInactiveTagProjection.getSitePid());
        sitesWithInactiveTags.add(currSite);
      }

      if (currPosition == null
          || !currPosition.getPid().equals(siteWithInactiveTagProjection.getPositionPid())) {
        currPosition = new Position();
        currPosition.setPid(siteWithInactiveTagProjection.getPositionPid());
        currPosition.setName(siteWithInactiveTagProjection.getPositionName());
        currSite.addPosition(currPosition);
      }

      if (currTier == null
          || !currTier.getPid().equals(siteWithInactiveTagProjection.getTierPid())) {
        currTier = new Tier();
        currTier.setPid(siteWithInactiveTagProjection.getTierPid());
        currPosition.getTiers().add(currTier);
      }

      Tag tag = new Tag();
      tag.setPid(siteWithInactiveTagProjection.getTagPid());
      currTier.addTag(tag);
    }

    return sitesWithInactiveTags;
  }
}

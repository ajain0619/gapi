package com.nexage.app.web.publisher;

import static com.ssp.geneva.common.base.annotation.ExternalAPI.WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.dto.BidderSummaryDTO;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.LimitItem;
import com.nexage.app.dto.PositionArchiveTransactionDTO;
import com.nexage.app.dto.RTBProfileLibraryCloneDataDTO;
import com.nexage.app.dto.RtbProfileLibsAndTagsDTO;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.publisher.PublisherAdSourceDefaultsDTO;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherHierarchyDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileGroupDTO;
import com.nexage.app.dto.publisher.PublisherRTBProfileLibraryDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.tag.TagArchiveTransactionDTO;
import com.nexage.app.dto.tag.TagDeploymentInfoDTO;
import com.nexage.app.dto.tag.TagPerformanceMetricsDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PublisherBidderSelfService;
import com.nexage.app.services.PublisherRtbProfileLibrarySelfService;
import com.nexage.app.services.PublisherSelfService;
import com.nexage.app.services.RtbProfileLibrarySellerLimitService;
import com.nexage.app.services.SellerLimitService;
import com.nexage.dw.geneva.util.ISO8601Util;
import com.ssp.geneva.common.base.annotation.ExternalAPI;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdNetworksForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdvertiserForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueForPubSelfServe;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@Tag(name = "/pss")
@RestController
@RequestMapping(value = "/pss")
public class PublisherSelfServeController {

  private final UserContext userContext;
  private final PublisherSelfService publisherSelfService;
  private final PublisherBidderSelfService publisherSelfBidderService;
  private final PublisherRtbProfileLibrarySelfService publisherSelfRtbService;
  private final SellerLimitService sellerLimitService;
  private final RtbProfileLibrarySellerLimitService rtbProfileLibrarySellerLimitService;
  private final BeanValidationService beanValidationService;

  @Autowired
  public PublisherSelfServeController(
      UserContext userContext,
      PublisherSelfService publisherSelfService,
      PublisherBidderSelfService publisherSelfBidderService,
      PublisherRtbProfileLibrarySelfService publisherSelfRtbService,
      SellerLimitService sellerLimitService,
      RtbProfileLibrarySellerLimitService rtbProfileLibrarySellerLimitService,
      BeanValidationService beanValidationService) {
    this.userContext = userContext;
    this.publisherSelfService = publisherSelfService;
    this.publisherSelfBidderService = publisherSelfBidderService;
    this.publisherSelfRtbService = publisherSelfRtbService;
    this.sellerLimitService = sellerLimitService;
    this.rtbProfileLibrarySellerLimitService = rtbProfileLibrarySellerLimitService;
    this.beanValidationService = beanValidationService;
  }

  /*
   *  Site Management
   */
  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public List<PublisherSiteDTO> getSites(
      @PathVariable(value = "publisher") long publisher,
      @RequestParam(value = "detail", required = false) String detail) {

    checkPublisher(publisher);
    return publisherSelfService.getSites(publisher, Boolean.parseBoolean(detail));
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public PublisherSiteDTO getSite(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @RequestParam(value = "detail", required = false) String detail) {

    checkPublisher(publisher);
    return publisherSelfService.getSite(site, Boolean.parseBoolean(detail));
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<PublisherSiteDTO> createSite(
      @PathVariable(value = "publisher") long publisher,
      @RequestBody PublisherSiteDTO publisherSite,
      @RequestParam(value = "detail", required = false) String detail) {

    checkPublisher(publisher);
    beanValidationService.validate(publisherSite);
    return new ResponseEntity<>(
        publisherSelfService.createSite(publisher, publisherSite, Boolean.parseBoolean(detail)),
        HttpStatus.CREATED);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{publisher}/site/{site}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public PublisherSiteDTO updateSite(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @RequestBody PublisherSiteDTO publisherSite,
      @RequestParam(value = "detail", required = false) String detail,
      @RequestParam(value = "txIdSiteUpdate", required = false) String txIdSiteUpdate) {

    checkPublisher(publisher);

    if (publisherSite.getPid() == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_PID_IS_NULL);
    }

    if (site != publisherSite.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    if (!userContext.isApiUser()) {
      SiteUpdateInfoDTO siteUpdateInfo =
          publisherSelfService.siteUpdateInfo(
              publisher, publisherSite, Boolean.parseBoolean(detail));
      if (!txIdSiteUpdate.equals(siteUpdateInfo.getTxId())) {
        throw new StaleStateException("Site has a different txIdSiteUpdate");
      }
    }
    beanValidationService.validate(publisherSite);
    return publisherSelfService.updateSite(publisher, publisherSite, Boolean.parseBoolean(detail));
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site/{site}/siteUpdateInfo",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public SiteUpdateInfoDTO siteUpdateInfo(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @RequestParam(value = "detail", required = false) String detail,
      @RequestBody PublisherSiteDTO publisherSite) {

    if (site != publisherSite.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }

    return publisherSelfService.siteUpdateInfo(
        publisher, publisherSite, Boolean.parseBoolean(detail));
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping("/{publisher}/site/{site}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public void deleteSite(
      @PathVariable(value = "publisher") long publisher, @PathVariable(value = "site") long site) {

    checkPublisher(publisher);
    publisherSelfService.deleteSite(publisher, site);
  }

  /*
   *  Position Management
   */

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<List<PublisherPositionDTO>> getPositions(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @RequestParam(value = "detail", required = false) String detail) {

    List<PublisherPositionDTO> responseBody =
        publisherSelfService.getPositions(publisherPid, sitePid, Boolean.parseBoolean(detail));
    return ResponseEntity.ok(responseBody);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}")
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<PublisherPositionDTO> getPosition(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "position") long positionPid,
      @RequestParam(value = "detail", required = false) String detail) {
    PublisherPositionDTO responseBody =
        publisherSelfService.getPosition(
            publisherPid, sitePid, positionPid, Boolean.parseBoolean(detail));
    return ResponseEntity.ok(responseBody);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site/{site}/position",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<PublisherPositionDTO> createPosition(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @RequestBody PublisherPositionDTO publisherPosition,
      @RequestParam(value = "detail", required = false) String detail) {
    beanValidationService.validate(publisherPosition, CreateGroup.class, Default.class);
    if (!userContext.isNexageUser()
        && sellerLimitService.isLimitEnabled(publisher)
        && !sellerLimitService.canCreatePositionsInSite(publisher, site)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_LIMIT_REACHED);
    }
    PublisherPositionDTO responseBody =
        publisherSelfService.createPosition(site, publisherPosition, Boolean.parseBoolean(detail));
    return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{publisher}/site/{site}/position/{position}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<PublisherPositionDTO> updatePosition(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "position") long positionPid,
      @RequestBody PublisherPositionDTO publisherPosition,
      @RequestParam(value = "detail", required = false) String detail) {
    beanValidationService.validate(publisherPosition, UpdateGroup.class, Default.class);

    if (positionPid != publisherPosition.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }

    PublisherPositionDTO responseBody =
        publisherSelfService.updatePosition(
            publisherPid, sitePid, publisherPosition, Boolean.parseBoolean(detail));
    return ResponseEntity.ok(responseBody);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site/{site}/position/{position}",
      params = {"operation", "targetSite"},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public PublisherPositionDTO copyPosition(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @RequestParam(value = "operation") String action,
      @RequestParam(value = "targetSite") Long targetSitePid,
      @RequestBody PublisherPositionDTO publisherPosition) {
    if (action.equalsIgnoreCase("clone")) {
      return publisherSelfService.copyPosition(
          publisher, site, position, targetSitePid, publisherPosition);
    } else {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/detailedPosition")
  public PublisherPositionDTO detailedPosition(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position) {

    return publisherSelfService.detailedPosition(publisher, site, position);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      "/{publisher}/site/{site}/position/{position}/positionPerformanceMetrics/archiveTransaction")
  public PositionArchiveTransactionDTO getPositionArchiveTransaction(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position) {
    return publisherSelfService.getPositionPerformanceMetricsForArchive(
        publisher, site, position, true);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(
      value = "/{publisher}/site/{site}/position/{position}",
      params = {"txid"})
  @ExternalAPI(WARNING_EXTERNAL_API_NEEDS_TO_BE_CHECKED_WITH_PRODUCT_BEFORE_DEPLOYING)
  public ResponseEntity<PublisherSiteDTO> archivePosition(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @RequestParam(value = "txid", required = false) String transactionId) {
    PublisherSiteDTO responseBody =
        publisherSelfService.archivePosition(publisher, site, position, transactionId);
    return ResponseEntity.ok(responseBody);
  }

  /*
   *  Tag Management
   */

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/tag")
  public List<PublisherTagDTO> getTags(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "position") long positionPid) {

    return publisherSelfService.getTags(publisherPid, sitePid, positionPid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/tag/{tag}")
  public ResponseEntity<PublisherTagDTO> getTag(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "position") long positionPid,
      @PathVariable(value = "tag") long tagPid) {

    return ResponseEntity.ok(
        publisherSelfService.getTag(publisherPid, sitePid, positionPid, tagPid));
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site/{site}/position/{position}/tag",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PublisherTagDTO> createTag(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @RequestBody PublisherTagDTO publisherTag) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(publisherSelfService.createTag(publisher, site, position, publisherTag));
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site/{site}/position/{position}/tag/{tag}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherTagDTO cloneTag(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "tag") long tag,
      @RequestBody PublisherTagDTO publisherTag,
      @RequestParam(value = "method") String method,
      @RequestParam(value = "targetSite") long targetSite,
      @RequestParam(value = "targetPosition") long targetPosition) {

    if (method != null && method.equals("clone")) {
      return publisherSelfService.cloneTag(
          publisher, site, position, tag, publisherTag, targetSite, targetPosition);
    }
    return null;
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{publisher}/site/{site}/position/{position}/tag/{tag}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PublisherTagDTO> updateTag(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "tag") long tag,
      @RequestBody PublisherTagDTO publisherTag) {

    if (tag != publisherTag.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }

    return ResponseEntity.ok(
        publisherSelfService.updateTag(publisher, site, position, publisherTag));
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/tag/{tag}/tagPerformanceMetrics")
  public TagArchiveTransactionDTO getTagPerformanceMetrics(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "tag") long tag) {
    return publisherSelfService.getTagPerformanceMetrics(publisher, site, position, tag, false);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      "/{publisher}/site/{site}/position/{position}/tag/{tag}/tagPerformanceMetrics/archiveTransaction")
  public TagArchiveTransactionDTO getTagArchiveTransaction(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "tag") long tag) {
    return publisherSelfService.getTagPerformanceMetrics(publisher, site, position, tag, true);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(
      value = "/{publisher}/site/{site}/position/{position}/tag/{tag}",
      params = {"txid"})
  public PublisherPositionDTO archiveTag(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "tag") long tag,
      @RequestParam(value = "txid") String transactionId) {

    return publisherSelfService.archiveTag(publisher, site, position, tag, transactionId);
  }

  /*
   *  Tier Management
   */
  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/tier")
  public List<PublisherTierDTO> getTiers(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "position") long positionPid) {

    return publisherSelfService.getTiers(publisherPid, sitePid, positionPid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/tier/{tier}")
  public PublisherTierDTO getTier(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "position") long positionPid,
      @PathVariable(value = "tier") long tierPid) {

    return publisherSelfService.getTier(publisherPid, sitePid, positionPid, tierPid);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site/{site}/position/{position}/tier",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherTierDTO createTier(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @RequestBody PublisherTierDTO publisherTier) {

    if (publisherTier.getTierType() != null
        && TierType.SY_DECISION_MAKER.equals(publisherTier.getTierType())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DECISION_MAKER_TIER_NOT_SUPPORTED);
    }

    return publisherSelfService.createTier(publisher, site, position, publisherTier);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/{publisher}/site/{site}/position/{position}/tier/{tier}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherTierDTO updateTier(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "tier") long tier,
      @RequestBody PublisherTierDTO publisherTier) {

    if (publisherTier.getPid() == null || tier != publisherTier.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    } else if (publisherTier.getTierType() != null
        && TierType.SY_DECISION_MAKER.equals(publisherTier.getTierType())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DECISION_MAKER_TIER_NOT_SUPPORTED);
    }
    return publisherSelfService.updateTier(publisher, site, position, publisherTier);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping("/{publisher}/site/{site}/position/{position}/tier/{tier}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTier(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @PathVariable(value = "tier") long tier) {

    publisherSelfService.deleteTier(publisher, site, position, tier);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/buyer")
  public List<PublisherBuyerDTO> getBuyers(
      @PathVariable(value = "publisher") long publisher,
      @RequestParam(value = "search", required = false) String search) {

    return publisherSelfService.getBuyers(publisher, search);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/bidders")
  public List<BidderSummaryDTO> getBidders(@PathVariable(value = "publisher") long publisher) {
    return publisherSelfBidderService.getBidders(publisher);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/dsps")
  public List<RuleDSPBiddersDTO> getDSPsWithBidders(
      @PathVariable(value = "publisher") Long publisher) {
    return publisherSelfBidderService.getRuleDSPBidders(publisher);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/tag/{tag}/tagdeployment/")
  public TagDeploymentInfoDTO getTagDeploymentInfo(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "tag") long tagPid) {
    return publisherSelfService.getTagDeploymentInfo(publisherPid, sitePid, tagPid);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping("/{publisher}/site/{site}/position/{position}/tag/{tag}/tagdeployment/")
  public void undeployTag(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "site") long sitePid,
      @PathVariable(value = "position") long positionPid,
      @PathVariable(value = "tag") long tagPid) {
    publisherSelfService.undeployTag(publisherPid, sitePid, positionPid, tagPid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisherPid}/tag")
  public ResponseEntity<Void> validateTag(
      @PathVariable(value = "publisherPid") long publisherPid,
      @RequestParam(value = "adnet") Long adnetPid,
      @RequestParam(value = "pid") String primaryId,
      @RequestParam(value = "pname", required = false) String primaryName,
      @RequestParam(value = "sid", required = false) String secondaryId,
      @RequestParam(value = "sname", required = false) String secondaryName) {

    try {
      publisherSelfService.validateTag(
          publisherPid, adnetPid, primaryId, primaryName, secondaryId, secondaryName);
    } catch (GenevaValidationException genevaValidationException) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /*
   * Super Auction Decision Maker methods
   */

  @Timed
  @ExceptionMetered
  @GetMapping("/{publisher}/site/{site}/position/{position}/decisionMaker")
  public PublisherTagDTO getDecisionMaker(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position) {

    return publisherSelfService.getDecisionMaker(publisher, site, position);
  }

  @Timed
  @ExceptionMetered
  @PostMapping(
      value = "/{publisher}/site/{site}/position/{position}/decisionMaker",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherTagDTO createDecisionMaker(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @RequestBody PublisherTagDTO publisherTag) {

    if (publisherTag.getPosition() != null
        && publisherTag.getPosition().getPid() != null
        && position != publisherTag.getPosition().getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }

    return publisherSelfService.createDecisionMaker(publisher, site, position, publisherTag);
  }

  @PutMapping(
      value = "/{publisher}/site/{site}/position/{position}/decisionMaker",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherTagDTO updateDecisionMaker(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @RequestBody PublisherTagDTO publisherTag) {

    if (publisherTag.getPosition() != null
        && publisherTag.getPosition().getPid() != null
        && position != publisherTag.getPosition().getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }

    return publisherSelfService.updateDecisionMaker(publisher, site, position, publisherTag);
  }

  @PutMapping(
      value = "/{publisher}/site/{site}/position/{position}/multiTags",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public List<PublisherTagDTO> createSmartYieldDemandSourceTags(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "site") long site,
      @PathVariable(value = "position") long position,
      @RequestBody List<PublisherTagDTO> publisherTags) {

    return publisherSelfService.generateSmartYieldDemandSourceTags(
        publisher, site, position, publisherTags);
  }

  /*
   * RTB Profile Library methods
   */

  @PostMapping(
      value = "/{publisher}/rtbprofilegroup",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherRTBProfileGroupDTO createRTBProfileGroup(
      @PathVariable(value = "publisher") long publisher,
      @RequestBody PublisherRTBProfileGroupDTO group) {
    group.setPublisherPid(publisher);
    return publisherSelfRtbService.createRTBProfileGroup(publisher, group);
  }

  @GetMapping("/{publisher}/rtbprofilegroup/{group}")
  public PublisherRTBProfileGroupDTO getRTBProfileGroup(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "group") long groupPid) {
    return publisherSelfRtbService.getRTBProfileGroup(publisher, groupPid);
  }

  @PutMapping("/{publisher}/rtbprofilegroup/{group}")
  public PublisherRTBProfileGroupDTO updateRTBProfileGroup(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "group") long groupPid,
      @RequestBody PublisherRTBProfileGroupDTO group) {
    return publisherSelfRtbService.updateRTBProfileGroup(publisher, groupPid, group);
  }

  @DeleteMapping("/{publisher}/rtbprofilegroup/{group}")
  public void deleteRTBProfileGroup(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "group") long groupPid) {
    publisherSelfRtbService.deleteRTBProfileGroup(publisher, groupPid);
  }

  @PostMapping(
      value = "/{publisher}/rtbprofilelibrary",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherRTBProfileLibraryDTO createRTBProfileLibrary(
      @PathVariable(value = "publisher") long publisher,
      @RequestBody PublisherRTBProfileLibraryDTO library) {
    return publisherSelfRtbService.createRTBProfileLibrary(publisher, library);
  }

  @PostMapping("/{publisher}/rtbprofilelibrary/clone")
  public PublisherRTBProfileLibraryDTO cloneRTBProfileLibraries(
      @PathVariable(value = "publisher") long publisher,
      @RequestBody RTBProfileLibraryCloneDataDTO data) {
    return publisherSelfRtbService.cloneRTBProfileLibraries(publisher, data);
  }

  @GetMapping("/{publisher}/rtbprofilegroup/{rtbprofilegroup}/hierarchy")
  public Set<PublisherHierarchyDTO> getTagHierachy(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "rtbprofilegroup") long rtbprofilegroup) {
    return publisherSelfService.getTagHierachy(publisher, rtbprofilegroup);
  }

  @GetMapping("/{publisher}/rtbprofilelibrary/{library}")
  public PublisherRTBProfileLibraryDTO getRTBProfileLibrary(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "library") long libraryPid) {
    return publisherSelfRtbService.getRTBProfileLibrary(publisher, libraryPid);
  }

  @GetMapping("/{publisher}/rtbprofilelibrary")
  public List<PublisherRTBProfileLibraryDTO> getRTBProfileLibrariesForCompany(
      @PathVariable(value = "publisher") long publisher,
      @RequestParam(value = "ref", required = false) String ref) {
    if ("seller_admin".equals(ref)) {
      return publisherSelfRtbService.getRTBProfileLibrariesForCompany(publisher);
    } else {
      return publisherSelfRtbService.getEligibleRTBProfileLibrariesForCompany(publisher);
    }
  }

  @PutMapping("/{publisher}/rtbprofilelibrary/{library}")
  public PublisherRTBProfileLibraryDTO updateRTBProfileLibrary(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "library") long libraryPid,
      @RequestBody PublisherRTBProfileLibraryDTO library) {
    return publisherSelfRtbService.updateRTBProfileLibrary(publisherPid, libraryPid, library);
  }

  @DeleteMapping("/{publisher}/rtbprofilelibrary/{library}")
  public void deleteRTBProfileLibrary(
      @PathVariable(value = "publisher") long publisherPid,
      @PathVariable(value = "library") long libraryPid) {
    publisherSelfRtbService.deleteRTBProfileLibrary(publisherPid, libraryPid);
  }

  @GetMapping("/{publisher}/availableadsources")
  public Collection<PublisherAdSourceDefaultsDTO> getAvailableAdsources(
      @PathVariable(value = "publisher") long publisher) {
    return publisherSelfService.getAvailableAdsources(publisher);
  }

  @GetMapping("/{publisher}/adsourcedefaults")
  public List<PublisherAdSourceDefaultsDTO> getAllAdsourceDefaultsForAllAdSources(
      @PathVariable(value = "publisher") long publisher) {
    return publisherSelfService.getAllAdsourceDefaultsForSeller(publisher);
  }

  @GetMapping("/{publisher}/adsourcedefaults/adsource/{adsourceId}")
  public PublisherAdSourceDefaultsDTO getAdsourceDefaultsForAdSource(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "adsourceId") long adsource) {
    return publisherSelfService.getAdsourceDefaultsForSeller(publisher, adsource);
  }

  @DeleteMapping("/{publisher}/adsourcedefaults/adsource/{adsourceId}")
  public ResponseEntity<Void> deleteAdsourceDefaultsForAdSource(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "adsourceId") long adsource) {
    publisherSelfService.deleteAdsourceDefaultsForSeller(publisher, adsource);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping(
      value = "/{publisher}/adsourcedefaults/adsource/{adsourceId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherAdSourceDefaultsDTO createAdSourceDefaultsForAdSource(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "adsourceId") long adsource,
      @RequestBody PublisherAdSourceDefaultsDTO adsourceDefaults) {
    return publisherSelfService.createAdsourceDefaultsForSeller(
        publisher, adsource, adsourceDefaults);
  }

  @PutMapping(
      value = "/{publisher}/adsourcedefaults/adsource/{adsourceId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherAdSourceDefaultsDTO updateAdSourceDefaultsForAdSource(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "adsourceId") long adsource,
      @RequestBody PublisherAdSourceDefaultsDTO adsourceDefaults) {
    return publisherSelfService.updateAdsourceDefaultsForSeller(
        publisher, adsource, adsourceDefaults);
  }

  @GetMapping("/{publisher}/adsource/{adsourceId}/tag")
  public List<PublisherTagDTO> getAdsourceTags(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "adsourceId") long adsource) {
    return publisherSelfService.getPubAdsourceTags(publisher, adsource);
  }

  @GetMapping("/{publisher}/adsource/{adsourceId}/tag/metric")
  public List<TagPerformanceMetricsDTO> getAdsourceTagMetrics(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "adsourceId") long adsource) {
    return publisherSelfService.getPubAdsourceTagPerformanceMetrics(publisher, adsource);
  }

  @PutMapping("/{publisher}/adsource/{adsourceId}/tag")
  public List<PublisherTagDTO> updateAdsourceTags(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "adsourceId") long adsource,
      @RequestBody List<PublisherTagDTO> tags) {
    return publisherSelfService.updatePubAdsourceTags(publisher, adsource, tags);
  }

  /** Publisher/Seller Methods */
  @GetMapping("/{publisher}")
  public PublisherDTO getPublisher(@PathVariable(value = "publisher") long publisher) {
    return publisherSelfService.getPublisher(publisher);
  }

  @PutMapping(
      value = "{publisher}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public PublisherDTO updatePublisher(
      @PathVariable(value = "publisher") long publisherPid,
      @RequestBody PublisherDTO publisherDto) {
    if (publisherDto == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    return publisherSelfService.updatePublisher(publisherPid, publisherDto);
  }

  @GetMapping("/{publisher}/biddersPerformanceSummary")
  public List<BiddersPerformanceForPubSelfServe> getBiddersPerformanceSummary(
      @PathVariable(value = "publisher") long publisher,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE_TIME) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE_TIME) String endDate) {
    StartStopDates dates = new StartStopDates();
    dates.setStartStopDates(startDate, endDate);

    return publisherSelfService.getBiddersPerformanceForPSS(
        publisher, dates.getStartDate(), dates.getStopDate());
  }

  /*
   *  Estimated Revenue Report
   */
  @GetMapping("/{publisher}/estimatedRevenueReport")
  public EstimatedRevenueForPubSelfServe getEstimatedRevenue(
      @PathVariable(value = "publisher") long publisher,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE) String endDate) {
    return publisherSelfService.getEstimatedRevenue(publisher, startDate, endDate);
  }

  @GetMapping("/{publisher}/estimatedRevenueReport/drilldown=adnet")
  public EstimatedRevenueByAdNetworksForPubSelfServ getEstimatedRevenueByAdNetworks(
      @PathVariable(value = "publisher") long publisher,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE) String endDate) {
    return publisherSelfService.getEstimatedRevenueByAdNetworks(publisher, startDate, endDate);
  }

  @GetMapping("/{publisher}/estimatedRevenueReport/drilldown=advertiser")
  public EstimatedRevenueByAdvertiserForPubSelfServ getEstimatedRevenueByAdvertiser(
      @PathVariable(value = "publisher") long publisher,
      @RequestParam(value = "start") @DateTimeFormat(iso = ISO.DATE) String startDate,
      @RequestParam(value = "stop") @DateTimeFormat(iso = ISO.DATE) String endDate) {
    return publisherSelfService.getEstimatedRevenueByAdvertiser(publisher, startDate, endDate);
  }

  public static class StartStopDates {
    Date start;
    Date stop;

    StartStopDates() {
      this.start = null;
      this.stop = null;
    }

    public void setStartStopDates(String startDate, String endDate) {
      try {
        this.start = ISO8601Util.parse(startDate);
        this.stop = ISO8601Util.parse(endDate);
      } catch (ParseException e) {
        log.error("Error Parsing start/end dates :" + e.getMessage());
        throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
      }
    }

    public Date getStartDate() {
      return this.start;
    }

    public Date getStopDate() {
      return this.stop;
    }
  }

  @PostMapping(
      value = "/{publisher}/applyGroups",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseStatus(HttpStatus.OK)
  public void addRTBProfileLibsToRTBProfiles(
      @PathVariable(value = "publisher") long publisher,
      @RequestBody RtbProfileLibsAndTagsDTO rtbProfileLibAndTagList) {
    publisherSelfService.updateRTBProfileLibToRTBProfilesMap(publisher, rtbProfileLibAndTagList);
  }

  @GetMapping("/{publisher}/checkLimit/{item}")
  public ResponseEntity<Map<String, Integer>> checkLimit(
      @PathVariable(value = "publisher") long publisher,
      @PathVariable(value = "item") String item,
      @RequestParam(value = "site", required = false) Long site,
      @RequestParam(value = "position", required = false) Long position,
      @RequestParam(value = "campaign", required = false) Long campaign) {

    final String KEY = "remainingItems";
    Map<String, Integer> response = new HashMap<>();
    LimitItem limitItem = LimitItem.getEnum(item);

    if (limitItem == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }

    switch (limitItem) {
      case SITES:
        response.put(KEY, sellerLimitService.checkSitesLimit(publisher));
        break;
      case POSITIONS_PER_SITE:
        response.put(KEY, sellerLimitService.checkPositionsInSiteLimit(publisher, site));
        break;
      case TAGS_PER_POSITION:
        response.put(KEY, sellerLimitService.checkTagsInPositionLimit(publisher, site, position));
        break;
      case CAMPAIGNS:
        response.put(KEY, sellerLimitService.checkCampaignsLimit(publisher));
        break;
      case CREATIVES_PER_CAMPAIGN:
        response.put(KEY, sellerLimitService.checkCreativesInCampaignLimit(publisher, campaign));
        break;
      case BIDDER_LIBRARIES:
        response.put(KEY, rtbProfileLibrarySellerLimitService.checkBidderLibrariesLimit(publisher));
        break;
      case BLOCK_LIBRARIES:
        response.put(KEY, rtbProfileLibrarySellerLimitService.checkBlockLibrariesLimit(publisher));
        break;
      case USERS:
        response.put(KEY, sellerLimitService.checkUsersLimit(publisher));
        break;
      default:
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private void checkPublisher(long publisher) {
    if (userContext.isApiUser() && !userContext.canAccessPublisher(publisher)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_PUBLISHER_ID);
    }
  }
}

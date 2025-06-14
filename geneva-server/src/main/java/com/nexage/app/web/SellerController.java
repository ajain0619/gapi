package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.dto.SiteSummaryDTO;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.app.dto.SiteDealTermSummaryDTO;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.tag.TagCleanupResultsDTO;
import com.nexage.app.dto.tag.TagDeploymentInfoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SearchService;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.services.SellerService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.SellerTagService;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
public class SellerController {

  private final SellerService sellerService;

  private final SellerSiteService sellerSiteService;

  private final SellerPositionService sellerPositionService;

  private final SellerTagService sellerTagService;

  private final SearchService<?> searchService;

  public SellerController(
      SellerService sellerService,
      SellerSiteService sellerSiteService,
      SellerPositionService sellerPositionService,
      SellerTagService sellerTagService,
      SearchService<?> searchService) {
    this.sellerService = sellerService;
    this.sellerSiteService = sellerSiteService;
    this.sellerPositionService = sellerPositionService;
    this.sellerTagService = sellerTagService;
    this.searchService = searchService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sellers/sitesummaries")
  public List<SiteSummaryDTO> getAllSitesSummary() {
    return sellerSiteService.getAllSitesSummary();
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sellers/{sellerPID}/sitesummaries")
  public List<SiteSummaryDTO> getAllSitesSummaryByCompanyPid(
      @PathVariable(value = "sellerPID") long sellerPid) {
    return sellerSiteService.getAllSitesSummaryByCompanyPid(sellerPid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value = "/sellers/sitesummaries",
      params = {"userPID"})
  public List<SiteSummaryDTO> getAllowedSitesForUser(
      @RequestParam(value = "userPID") long userPid) {
    return sellerSiteService.getAllowedSitesForUser(userPid);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(value = "/sellers/sites/setdefaultdealterm")
  public List<SiteDealTermSummaryDTO> updateSiteDealTermsToPubDefaultByYieldManager(
      @RequestParam(value = "sellerPid") Long sellerPid, @RequestBody List<Long> sitePids) {
    return sellerSiteService.updateSiteDealTermsToPubDefaultByYieldManager(sellerPid, sitePids);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sellers/sites/sitedealterms")
  public List<SiteDealTermSummaryDTO> getSiteDealTerms(
      @RequestParam(value = "sellerPid", required = false) Long sellerPid) {
    return sellerSiteService.getAllSiteDealTerms(sellerPid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sellers/sites/{sitePID}")
  public Site getSite(@PathVariable(value = "sitePID") long sitePid) {
    return sellerSiteService.getSite(sitePid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(
      value = "/sellers",
      params = {"prefix"})
  public List<?> getSellersAndSitesByPrefix(
      @RequestParam(value = "prefix", required = true) String prefix) {
    return searchService.findSearchSummaryDtosContaining(prefix);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/sellers/sites/{sitePID}/tags",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Site createTag(@PathVariable(value = "sitePID") long sitePid, @RequestBody Tag tag) {
    return sellerTagService.createTag(sitePid, tag, true);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/sellers/sites/{sitePID}/positions",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Site createPosition(
      @PathVariable(value = "sitePID") long sitePid, @RequestBody Position position) {
    return sellerPositionService.createPosition(sitePid, position);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/sellers/sites/{sitePID}",
      params = {"txid"},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Site updateSite(
      @PathVariable(value = "sitePID") long sitePid,
      @RequestBody Site site,
      @RequestParam(value = "txid", required = true) String txid) {
    if (sitePid != site.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    sellerSiteService.validateSiteNameUniqueness(
        site.getPid(), site.getCompanyPid(), site.getName());
    return sellerSiteService.updateSite(verifyUpdateSite(site, txid));
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/sellers/sites/{sitePID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public SiteUpdateInfoDTO updateVerifySite(
      @PathVariable(value = "sitePID") long sitePid, @RequestBody Site site) {
    if (sitePid != site.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    return sellerSiteService.processUpdateSiteRequest(site);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/sellers/sites/positions/{positionPID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Site updatePosition(
      @PathVariable(value = "positionPID") long positionPid, @RequestBody Position position) {
    if (positionPid != position.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    return sellerPositionService.updatePosition(position);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/sellers/sites/{sitePID}")
  public void deleteSite(@PathVariable(value = "sitePID") long sitePid) {
    sellerSiteService.deleteSite(sitePid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sellers/sites/{sitePID}/tagdeployment/{tagPID}")
  public TagDeploymentInfoDTO getTagDeploymentInfo(
      @PathVariable(value = "tagPID") long tagPid, @PathVariable(value = "sitePID") long sitePid) {
    return sellerTagService.getTagDeploymentInfo(sitePid, tagPid);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/sellers/sites/{sitePID}/tagdeployment/{tagPID}")
  public Site undeployTag(
      @PathVariable(value = "tagPID") long tagPid, @PathVariable(value = "sitePID") Long sitePid) {
    Long positionPid = null;
    return sellerTagService.undeployTag(sitePid, positionPid, tagPid);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sellers/tagcleanup")
  public TagCleanupResultsDTO cleanupTagDeployments() {
    return sellerTagService.cleanupTagDeployments(-1L);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/sellers/sites/{sitePID}/tagcleanup")
  public TagCleanupResultsDTO cleanupTagDeployments(@PathVariable(value = "sitePID") Long sitePid) {
    return sellerTagService.cleanupTagDeployments(sitePid);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/sellers/sites/{sitePID}/tags/{tagPID}")
  public Site deleteTag(
      @PathVariable(value = "sitePID") long sitePid, @PathVariable(value = "tagPID") Long tagPid) {
    return sellerTagService.deleteTag(sitePid, tagPid);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/sellers/sites/{sitePID}/positions/{positionPID}")
  public Site deletePosition(
      @PathVariable(value = "sitePID") long sitePid,
      @PathVariable(value = "positionPID") long positionPid) {
    return sellerPositionService.deletePosition(sitePid, positionPid);
  }

  @Timed
  @ExceptionMetered
  @DeleteMapping(value = "/sellers/sites/{sitePID}/exchangetags/{tagPID}")
  public Site deleteExchangeTag(
      @PathVariable(value = "sitePID") long sitePid, @PathVariable(value = "tagPID") long tagPid) {
    return sellerTagService.deleteExchangeTag(sitePid, tagPid);
  }

  @Timed
  @ExceptionMetered
  @PutMapping(
      value = "/sellers/sites/rtbprofiles/{rtbProfilePID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Site updateRTBProfile(
      @PathVariable(value = "rtbProfilePID") long rtbProfilePid,
      @RequestBody RTBProfile rtbProfile) {
    if (rtbProfilePid != rtbProfile.getPid()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REQUEST_PARAM_BODY_NOT_MATCH);
    }
    return sellerService.updateRTBProfile(rtbProfile);
  }

  private Site verifyUpdateSite(Site site, String txid) {
    Site tempSite = site;
    String newTxid = sellerSiteService.processUpdateSiteRequest(tempSite).getTxId();
    if (txid == null || newTxid == null || !txid.equals(newTxid)) {
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_UPDATE_SITE_OBJECTS_MISMATCH);
    }
    return site;
  }
}

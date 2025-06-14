package com.nexage.app.web;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.nexage.admin.core.model.BdrExternalPublisher;
import com.nexage.admin.core.model.BdrExternalSite;
import com.nexage.app.services.ExternalPubSiteService;
import com.ssp.geneva.common.base.annotation.Legacy;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@RestController
public class ExternalPubSiteController {

  private final ExternalPubSiteService extPubSiteService;

  public ExternalPubSiteController(ExternalPubSiteService extPubSiteService) {
    this.extPubSiteService = extPubSiteService;
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/externalpubs")
  public List<BdrExternalPublisher> getAllExternalPubs(
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "iab", required = false) String iab,
      @RequestParam(value = "negateIab", defaultValue = "false") Boolean negateIab,
      @RequestParam(value = "tgPID", required = false) Long tgPID) {
    if (type == null && iab == null) {
      return extPubSiteService.getAllExternalPublishers(tgPID);
    }
    if (type != null && iab == null) {
      return extPubSiteService.getExternalPubsMatchingType(type, tgPID);
    }
    if (type == null && iab != null) {
      return extPubSiteService.getExternalPubsMatchingCategory(iab, negateIab, tgPID);
    }
    if (type != null && iab != null) {
      return extPubSiteService.getExternalPubsMatchingTypeAndCategory(type, iab, negateIab, tgPID);
    }

    // fallback
    return extPubSiteService.getAllExternalPublishers(tgPID);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/externalpubs/{pubPID}/externalsites")
  public List<BdrExternalSite> getAllExternalSitesForPub(
      @PathVariable String pubPID,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "iab", required = false) String iab,
      @RequestParam(value = "negateIab", defaultValue = "false") Boolean negateIab,
      @RequestParam(value = "tgPID", required = false) Long tgPID) {
    if (type == null && iab == null) {
      return extPubSiteService.getExternalSitesForPub(pubPID, tgPID);
    }
    if (type != null && iab == null) {
      return extPubSiteService.getExternalSitesForPubMatchingType(pubPID, type, tgPID);
    }
    if (type == null && iab != null) {
      return extPubSiteService.getExternalSitesForPubMatchingCategory(
          pubPID, iab, negateIab, tgPID);
    }
    if (type != null && iab != null) {
      return extPubSiteService.getExternalSitesForPubMatchingTypeAndCategory(
          pubPID, type, iab, negateIab, tgPID);
    }

    // fallback
    return extPubSiteService.getAllExternalSites(tgPID);
  }

  @Timed
  @ExceptionMetered
  @GetMapping(value = "/externalpubs/externalsites")
  public List<BdrExternalSite> getAlExternalSitesMatchingFilters(
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "pubPIDs", required = false) String pubPIDs,
      @RequestParam(value = "iab", required = false) String iab,
      @RequestParam(value = "negateIab", defaultValue = "false") Boolean negateIab,
      @RequestParam(value = "tgPID", required = false) Long tgPID) {
    if (type == null && iab == null) {
      if (pubPIDs != null) {
        return extPubSiteService.getExternalSitesForPub(pubPIDs, tgPID);
      } else {
        return extPubSiteService.getAllExternalSites(tgPID);
      }
    }
    if (type != null && iab == null) {
      if (pubPIDs != null) {
        return extPubSiteService.getExternalSitesForPubMatchingType(pubPIDs, type, tgPID);
      } else {
        return extPubSiteService.getExternalSitesMatchingType(type, tgPID);
      }
    }
    if (type == null && iab != null) {
      if (pubPIDs != null) {
        return extPubSiteService.getExternalSitesForPubMatchingCategory(
            pubPIDs, iab, negateIab, tgPID);
      } else {
        return extPubSiteService.getExternalSitesMatchingCategory(iab, negateIab, tgPID);
      }
    }
    if (type != null && iab != null) {
      if (pubPIDs != null) {
        return extPubSiteService.getExternalSitesForPubMatchingTypeAndCategory(
            pubPIDs, type, iab, negateIab, tgPID);
      } else {
        return extPubSiteService.getExternalSitesMatchingTypeAndCategory(
            type, iab, negateIab, tgPID);
      }
    }

    // fallback
    return extPubSiteService.getAllExternalSites(tgPID);
  }
}

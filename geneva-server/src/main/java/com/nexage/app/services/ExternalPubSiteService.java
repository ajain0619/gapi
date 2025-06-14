package com.nexage.app.services;

import com.nexage.admin.core.model.BdrExternalPublisher;
import com.nexage.admin.core.model.BdrExternalSite;
import java.util.List;

public interface ExternalPubSiteService {

  List<BdrExternalPublisher> getAllExternalPublishers(Long targetGroupPid);

  List<BdrExternalPublisher> getExternalPubsMatchingType(String type, Long targetGroupPid);

  List<BdrExternalPublisher> getExternalPubsMatchingCategory(
      String iab, boolean negateIab, Long targetGroupPid);

  List<BdrExternalPublisher> getExternalPubsMatchingTypeAndCategory(
      String type, String iab, boolean negateIab, Long targetGroupPid);

  List<BdrExternalSite> getAllExternalSites(Long targetGroupPid);

  List<BdrExternalSite> getExternalSitesForPub(String pubPid, Long targetGroupPid);

  List<BdrExternalSite> getExternalSitesMatchingType(String type, Long targetGroupPid);

  List<BdrExternalSite> getExternalSitesMatchingCategory(
      String iab, boolean negateIab, Long targetGroupPid);

  List<BdrExternalSite> getExternalSitesMatchingTypeAndCategory(
      String type, String iab, boolean negateIab, Long targetGroupPid);

  List<BdrExternalSite> getExternalSitesForPubMatchingTypeAndCategory(
      String pubPid, String type, String iab, boolean negateIab, Long targetGroupPid);

  List<BdrExternalSite> getExternalSitesForPubMatchingCategory(
      String pubPid, String iab, boolean negateIab, Long targetGroupPid);

  List<BdrExternalSite> getExternalSitesForPubMatchingType(
      String pubPid, String type, Long targetGroupPid);
}

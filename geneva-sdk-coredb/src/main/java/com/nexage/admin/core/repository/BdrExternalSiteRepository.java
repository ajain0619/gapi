package com.nexage.admin.core.repository;

import com.nexage.admin.core.model.BdrExternalSite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BdrExternalSiteRepository extends JpaRepository<BdrExternalSite, Long> {

  List<BdrExternalSite> findBySiteType(String type);

  List<BdrExternalSite> findByIabCategoriesContains(String categories);

  List<BdrExternalSite> findByIabCategoriesNotContains(String categories);

  List<BdrExternalSite> findBySiteTypeAndIabCategoriesContains(String type, String categories);

  List<BdrExternalSite> findBySiteTypeAndIabCategoriesNotContains(String type, String categories);

  List<BdrExternalSite> findByBdrPubInfoPidAndSiteType(Long pubPid, String type);

  List<BdrExternalSite> findByBdrPubInfoPidAndIabCategoriesContains(Long pubPid, String categories);

  List<BdrExternalSite> findByBdrPubInfoPidAndIabCategoriesNotContains(
      Long pubPid, String categories);

  List<BdrExternalSite> findByBdrPubInfoPidAndSiteTypeAndIabCategoriesContains(
      Long pubPid, String type, String categories);

  List<BdrExternalSite> findByBdrPubInfoPidAndSiteTypeAndIabCategoriesNotContains(
      Long pubPid, String type, String categories);

  List<BdrExternalSite> findByBdrPubInfoPid(Long pubPid);
}

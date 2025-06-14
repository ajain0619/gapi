package com.nexage.admin.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.BdrExternalSite;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/bdr-external-site-publisher-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class BdrExternalSiteRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired protected BdrExternalSiteRepository externalSiteRepository;

  @Test
  void shouldFindAllExternalSites() {
    assertEquals(5, externalSiteRepository.findAll().size());
  }

  @Test
  void shouldFindAllSitesOfType() {
    String type = "type2";

    List<BdrExternalSite> bdrExternalSites = externalSiteRepository.findBySiteType(type);

    assertEquals(2, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(type, bdrExternalSite.getSiteType());
    }
  }

  @Test
  void shouldFindAllSitesContainingCategory() {
    String category = "category1";

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByIabCategoriesContains(category);

    assertEquals(3, bdrExternalSites.size());
    assertTrue(
        bdrExternalSites.stream().allMatch(site -> site.getIabCategories().contains(category)));
  }

  @Test
  void shouldFindAllSitesOfTypeContainingCategory() {
    String category = "category1";
    String type = "type2";

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findBySiteTypeAndIabCategoriesContains(type, category);

    assertEquals(1, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(type, bdrExternalSite.getSiteType());
      assertTrue(bdrExternalSite.getIabCategories().contains(category));
    }
  }

  @Test
  void shouldFindAllSitesNotContainingCategory() {
    String category = "category1";

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByIabCategoriesNotContains(category);

    assertEquals(2, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertFalse(bdrExternalSite.getIabCategories().contains(category));
    }
  }

  @Test
  void shouldFindAllSitesOfTypeNotContainingCategory() {
    String category = "category1";
    String type = "type2";

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findBySiteTypeAndIabCategoriesNotContains(type, category);

    assertEquals(1, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(type, bdrExternalSite.getSiteType());
      assertFalse(bdrExternalSite.getIabCategories().contains(category));
    }
  }

  @Test
  void shouldFindAllSitesOfTypeForPublisher() {
    String type = "type3";
    Long publisherPid = 1L;

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByBdrPubInfoPidAndSiteType(publisherPid, type);

    assertEquals(1, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(publisherPid, bdrExternalSite.getBdrPubInfoPid());
      assertEquals(type, bdrExternalSite.getSiteType());
    }
  }

  @Test
  void shoudlFindAllSitesContainingCategoryForPublisher() {
    String category = "category1";
    Long publisherPid = 1L;

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByBdrPubInfoPidAndIabCategoriesContains(publisherPid, category);

    assertEquals(2, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(publisherPid, bdrExternalSite.getBdrPubInfoPid());
      assertTrue(bdrExternalSite.getIabCategories().contains(category));
    }
  }

  @Test
  void shouldFindAllSitesNotContainingCategoryForPublisher() {
    String category = "category1";
    Long publisherPid = 1L;

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByBdrPubInfoPidAndIabCategoriesNotContains(
            publisherPid, category);

    assertEquals(1, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(publisherPid, bdrExternalSite.getBdrPubInfoPid());
      assertFalse(bdrExternalSite.getIabCategories().contains(category));
    }
  }

  @Test
  void shouldFindAllSitesOfTypeContainingCategoryForPublisher() {
    String category = "category3";
    String type = "type1";
    Long publisherPid = 1L;

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByBdrPubInfoPidAndSiteTypeAndIabCategoriesContains(
            publisherPid, type, category);

    assertEquals(1, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(publisherPid, bdrExternalSite.getBdrPubInfoPid());
      assertEquals(type, bdrExternalSite.getSiteType());
      assertTrue(bdrExternalSite.getIabCategories().contains(category));
    }
  }

  @Test
  void shouldFindAllSitesOfTypeNotContainingCategoryForPublisher() {
    String category = "category3";
    String type = "type1";
    Long publisherPid = 1L;

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByBdrPubInfoPidAndSiteTypeAndIabCategoriesNotContains(
            publisherPid, type, category);

    assertEquals(1, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(publisherPid, bdrExternalSite.getBdrPubInfoPid());
      assertEquals(type, bdrExternalSite.getSiteType());
      assertFalse(bdrExternalSite.getIabCategories().contains(category));
    }
  }

  @Test
  void shouldFindAllSitesForPublisher() {
    Long publisherPid = 1L;

    List<BdrExternalSite> bdrExternalSites =
        externalSiteRepository.findByBdrPubInfoPid(publisherPid);

    assertEquals(3, bdrExternalSites.size());
    for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
      assertEquals(publisherPid, bdrExternalSite.getBdrPubInfoPid());
    }
  }
}

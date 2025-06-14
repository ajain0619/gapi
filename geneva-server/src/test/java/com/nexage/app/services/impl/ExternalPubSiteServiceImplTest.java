package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.bidder.model.BDRTarget;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.bidder.type.BDRTargetType;
import com.nexage.admin.core.model.BdrExternalPublisher;
import com.nexage.admin.core.model.BdrExternalSite;
import com.nexage.admin.core.repository.BdrExternalPublisherRepository;
import com.nexage.admin.core.repository.BdrExternalSiteRepository;
import com.nexage.admin.core.repository.BdrTargetGroupRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExternalPubSiteServiceImplTest {
  @InjectMocks ExternalPubSiteServiceImpl externalPubSiteService;

  @Mock BdrExternalPublisherRepository externalPublisherRepository;

  @Mock BdrExternalSiteRepository externalSiteRepository;

  @Mock BdrTargetGroupRepository targetGroupRepository;

  @Test
  void shouldReturnAllExternalPublishers() {
    List<BdrExternalPublisher> bdrExternalPublishers =
        List.of(getExternalPublisher(1L), getExternalPublisher(2L));
    when(externalPublisherRepository.findAll()).thenReturn(bdrExternalPublishers);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getAllExternalPublishers(1L);

    assertEquals(bdrExternalPublishers, resultPublishers);
  }

  @Test
  void shouldReturnAllExternalPublishersOfType() {
    List<BdrExternalPublisher> bdrExternalPublishers =
        List.of(getExternalPublisher(1L), getExternalPublisher(2L));
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findBySiteType(any())).thenReturn(bdrExternalSites);
    when(externalPublisherRepository.findByPidIn(any())).thenReturn(bdrExternalPublishers);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getExternalPubsMatchingType("type", 1L);

    assertEquals(bdrExternalPublishers, resultPublishers);
  }

  @Test
  void shouldReturnAllExternalPublishersInCategory() {
    List<BdrExternalPublisher> bdrExternalPublishers =
        List.of(getExternalPublisher(1L), getExternalPublisher(2L));
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByIabCategoriesContains(any())).thenReturn(bdrExternalSites);
    when(externalPublisherRepository.findByPidIn(any())).thenReturn(bdrExternalPublishers);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getExternalPubsMatchingCategory("category", false, 1L);

    assertEquals(bdrExternalPublishers, resultPublishers);
  }

  @Test
  void shouldReturnAllExternalPublishersInCategoryWhenNoTargetGroup() {
    List<BdrExternalPublisher> bdrExternalPublishers =
        List.of(getExternalPublisher(1L), getExternalPublisher(2L));
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByIabCategoriesContains(any())).thenReturn(bdrExternalSites);
    when(externalPublisherRepository.findByPidIn(any())).thenReturn(bdrExternalPublishers);

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getExternalPubsMatchingCategory("category", false, null);

    assertEquals(bdrExternalPublishers, resultPublishers);
  }

  @Test
  void shouldReturnAllExternalPublishersNotInCategory() {
    List<BdrExternalPublisher> bdrExternalPublishers =
        List.of(getExternalPublisher(1L), getExternalPublisher(2L));
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByIabCategoriesNotContains(any())).thenReturn(bdrExternalSites);
    when(externalPublisherRepository.findByPidIn(any())).thenReturn(bdrExternalPublishers);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getExternalPubsMatchingCategory("category", true, 1L);

    assertEquals(bdrExternalPublishers, resultPublishers);
  }

  @Test
  void shouldReturnAllExternalPublishersOfTypeInCategory() {
    List<BdrExternalPublisher> bdrExternalPublishers =
        List.of(getExternalPublisher(1L), getExternalPublisher(2L));
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findBySiteTypeAndIabCategoriesContains(any(), any()))
        .thenReturn(bdrExternalSites);
    when(externalPublisherRepository.findByPidIn(any())).thenReturn(bdrExternalPublishers);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getExternalPubsMatchingTypeAndCategory(
            "type", "category", false, 1L);

    assertEquals(bdrExternalPublishers, resultPublishers);
  }

  @Test
  void shouldReturnAllExternalPublishersOfTypeInCategoryWhenNoTargetGroup() {
    List<BdrExternalPublisher> bdrExternalPublishers =
        List.of(getExternalPublisher(1L), getExternalPublisher(2L));
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findBySiteTypeAndIabCategoriesContains(any(), any()))
        .thenReturn(bdrExternalSites);
    when(externalPublisherRepository.findByPidIn(any())).thenReturn(bdrExternalPublishers);

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getExternalPubsMatchingTypeAndCategory(
            "type", "category", false, null);

    assertEquals(bdrExternalPublishers, resultPublishers);
  }

  @Test
  void shouldReturnAllExternalSitesOfType() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findBySiteType(any())).thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesMatchingType("type", 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesInCategory() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByIabCategoriesContains(any())).thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesMatchingCategory("category", false, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesNotInCategory() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByIabCategoriesNotContains(any())).thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesMatchingCategory("category", true, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesOfTypeInCategory() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findBySiteTypeAndIabCategoriesContains(any(), any()))
        .thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesMatchingTypeAndCategory(
            "type", "category", false, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesOfTypeNotInCategory() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findBySiteTypeAndIabCategoriesNotContains(any(), any()))
        .thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesMatchingTypeAndCategory(
            "type", "category", true, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSites() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findAll()).thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites = externalPubSiteService.getAllExternalSites(1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesForPublisher() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByBdrPubInfoPid(any())).thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites = externalPubSiteService.getExternalSitesForPub("1", 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesOfTypeForPublisher() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByBdrPubInfoPidAndSiteType(any(), any()))
        .thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesForPubMatchingType("1", "type", 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesInCategoryForPublisher() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByBdrPubInfoPidAndIabCategoriesContains(any(), any()))
        .thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesForPubMatchingCategory("1", "category", false, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesNotInCategoryForPublisher() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByBdrPubInfoPidAndIabCategoriesNotContains(any(), any()))
        .thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesForPubMatchingCategory("1", "category", true, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesOfTypeInCategoryForPublisher() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByBdrPubInfoPidAndSiteTypeAndIabCategoriesContains(
            any(), any(), any()))
        .thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesForPubMatchingTypeAndCategory(
            "1", "type", "category", false, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldReturnAllExternalSitesOfTypeNotInCategoryForPublisher() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByBdrPubInfoPidAndSiteTypeAndIabCategoriesNotContains(
            any(), any(), any()))
        .thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L)).thenReturn(Optional.of(new BdrTargetGroup()));

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesForPubMatchingTypeAndCategory(
            "1", "type", "category", true, 1L);

    assertEquals(bdrExternalSites, resultSites);
  }

  @Test
  void shouldCheckIfSitesInTarget() {
    List<BdrExternalSite> bdrExternalSites = List.of(getExternalSite(1L, 1L));
    when(externalSiteRepository.findAll()).thenReturn(bdrExternalSites);
    when(targetGroupRepository.findById(1L))
        .thenReturn(Optional.of(getBdrTargetGroup("pubAlias/siteAlias")));

    List<BdrExternalSite> resultSites = externalPubSiteService.getAllExternalSites(1L);

    assertTrue(resultSites.get(0).getIsInTarget());
  }

  @Test
  void shouldCheckIfPublishersInTarget() {
    List<BdrExternalPublisher> bdrExternalPublishers = List.of(getExternalPublisher(1L));
    when(externalPublisherRepository.findAll()).thenReturn(bdrExternalPublishers);
    when(targetGroupRepository.findById(1L))
        .thenReturn(Optional.of(getBdrTargetGroup("pubAlias/siteAlias")));

    List<BdrExternalPublisher> resultPublishers =
        externalPubSiteService.getAllExternalPublishers(1L);

    assertTrue(resultPublishers.get(0).getIsInTarget());
  }

  @Test
  void shouldReturnNonRepeatingSitesForCategories() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findByIabCategoriesContains(any())).thenReturn(bdrExternalSites);

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesMatchingCategory("category1,category2", false, 1L);

    assertEquals(bdrExternalSites.size(), resultSites.size());
  }

  @Test
  void shouldReturnNonRepeatingSitesForCategoriesAndType() {
    List<BdrExternalSite> bdrExternalSites =
        List.of(getExternalSite(1L, 1L), getExternalSite(2L, 2L));
    when(externalSiteRepository.findBySiteTypeAndIabCategoriesContains(any(), any()))
        .thenReturn(bdrExternalSites);

    List<BdrExternalSite> resultSites =
        externalPubSiteService.getExternalSitesMatchingTypeAndCategory(
            "type", "category1,category2", false, 1L);

    assertEquals(bdrExternalSites.size(), resultSites.size());
  }

  private BdrExternalPublisher getExternalPublisher(Long pid) {
    BdrExternalPublisher bdrExternalPublisher = new BdrExternalPublisher();
    bdrExternalPublisher.setPid(pid);
    bdrExternalPublisher.setPubAlias("pubAlias");
    return bdrExternalPublisher;
  }

  private BdrExternalSite getExternalSite(Long pid, Long publisherPid) {
    BdrExternalSite bdrExternalSite = new BdrExternalSite();
    bdrExternalSite.setPid(pid);
    bdrExternalSite.setBdrPubInfoPid(publisherPid);
    bdrExternalSite.setSiteAlias("siteAlias");
    return bdrExternalSite;
  }

  private BdrTargetGroup getBdrTargetGroup(String data) {
    BdrTargetGroup bdrTargetGroup = new BdrTargetGroup();
    bdrTargetGroup.setTargets(Set.of(getTarget(data)));
    return bdrTargetGroup;
  }

  private BDRTarget getTarget(String data) {
    BDRTarget bdrTarget = new BDRTarget();
    bdrTarget.setData(data);
    bdrTarget.setTargetType(BDRTargetType.PUBLISHER);
    return bdrTarget;
  }
}

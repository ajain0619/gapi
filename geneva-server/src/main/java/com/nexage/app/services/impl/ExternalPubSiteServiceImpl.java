package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BDRTarget;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.model.BdrExternalPublisher;
import com.nexage.admin.core.model.BdrExternalSite;
import com.nexage.admin.core.repository.BdrExternalPublisherRepository;
import com.nexage.admin.core.repository.BdrExternalSiteRepository;
import com.nexage.admin.core.repository.BdrTargetGroupRepository;
import com.nexage.app.services.ExternalPubSiteService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Log4j2
@Service
@Transactional
@PreAuthorize("@loginUserContext.isOcUserNexage()")
public class ExternalPubSiteServiceImpl implements ExternalPubSiteService {

  private final BdrTargetGroupRepository targetGroupRepository;

  private final BdrExternalSiteRepository externalSiteRepository;

  private final BdrExternalPublisherRepository externalPublisherRepository;

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalPublisher> getAllExternalPublishers(Long targetgroupPid) {
    List<BdrExternalPublisher> fullList = externalPublisherRepository.findAll();
    if (targetgroupPid != null) {
      return getPublishersInTargets(fullList, targetgroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalPublisher> getExternalPubsMatchingType(String type, Long targetGroupPid) {
    List<Long> pidList =
        externalSiteRepository.findBySiteType(type).stream()
            .map(BdrExternalSite::getBdrPubInfoPid)
            .collect(Collectors.toList());
    List<BdrExternalPublisher> bdrExternalPublishers =
        externalPublisherRepository.findByPidIn(pidList);
    if (targetGroupPid != null) {
      return getPublishersInTargets(bdrExternalPublishers, targetGroupPid);
    }
    return bdrExternalPublishers;
  }

  /*
   * take a list of sites and a target group pid, and return a list of those sites which were present in Publisher/Site targets in the target group s
   */
  public List<BdrExternalSite> getSitesInTargets(
      List<BdrExternalSite> fullList, long targetGroupPid) {
    Map<String, List<BdrExternalSite>> siteMap = new HashMap<>();
    fullList.stream()
        .filter(site -> site.getSiteAlias() != null)
        .forEach(
            site -> {
              List<BdrExternalSite> sites =
                  Optional.ofNullable(siteMap.get(site.getSiteAlias())).orElse(new ArrayList<>());
              sites.add(site);
              siteMap.put(site.getSiteAlias(), sites);
            });
    BdrTargetGroup targetGroup = targetGroupRepository.findById(targetGroupPid).orElse(null);
    if (targetGroup != null) {
      for (BDRTarget target : targetGroup.getTargets()) {
        switch (target.getTargetType()) {
          case PUBLISHER:
            String[] pubSiteTargetList = target.getData().split(",");
            if (pubSiteTargetList != null) {
              for (String strTarget : pubSiteTargetList) {
                String[] pubSiteTarget = strTarget.split("/");
                if (pubSiteTarget != null) {

                  if (!pubSiteTarget[1].isEmpty() && !pubSiteTarget[1].equals("*")) {
                    String targetString = pubSiteTarget[1];
                    List<BdrExternalSite> bdrExternalSites = siteMap.get(targetString);
                    if (bdrExternalSites != null && !bdrExternalSites.isEmpty()) {
                      log.debug("adding target for string: " + targetString);
                      for (BdrExternalSite bdrExternalSite : bdrExternalSites) {
                        bdrExternalSite.setIsInTarget(true);
                      }
                    }
                  }
                }
              }
            }
          default:
            break;
        }
      }
    }

    return fullList;
  }

  /*
   * take a list of sites and a target group pid, and return a list of those sites which were present in Publisher/Site targets in the target group s
   */
  public List<BdrExternalPublisher> getPublishersInTargets(
      List<BdrExternalPublisher> fullList, long targetGroupPid) {
    Map<String, BdrExternalPublisher> pubMap = new HashMap<>();
    for (BdrExternalPublisher pub : fullList) {
      if (pub.getPubAlias() != null) {
        pubMap.put(pub.getPubAlias(), pub);
      }
    }
    BdrTargetGroup targetGroup = targetGroupRepository.findById(targetGroupPid).orElse(null);
    if (targetGroup != null) {
      for (BDRTarget target : targetGroup.getTargets()) {
        switch (target.getTargetType()) {
          case PUBLISHER:
            String[] pubSiteTargetList = target.getData().split(",");
            if (pubSiteTargetList != null) {
              for (String strTarget : pubSiteTargetList) {
                String[] pubSiteTarget = strTarget.split("/");
                if (pubSiteTarget != null) {
                  if (!pubSiteTarget[0].isEmpty()) {
                    String targetString = pubSiteTarget[0];
                    if (pubMap.get(targetString) != null) {
                      log.debug("adding publisher target for string: " + targetString);
                      pubMap.get(targetString).setIsInTarget(true);
                    }
                  }
                }
              }
            }
          default:
            break;
        }
      }
    }

    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalPublisher> getExternalPubsMatchingCategory(
      String iab, boolean negateIab, Long targetGroupPid) {

    List<Long> pidList =
        getSitesByCategory(iab, negateIab).stream()
            .map(BdrExternalSite::getBdrPubInfoPid)
            .collect(Collectors.toList());
    List<BdrExternalPublisher> bdrExternalPublishers =
        externalPublisherRepository.findByPidIn(pidList);
    if (targetGroupPid != null) {
      return getPublishersInTargets(bdrExternalPublishers, targetGroupPid);
    }
    return bdrExternalPublishers;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalPublisher> getExternalPubsMatchingTypeAndCategory(
      String type, String iab, boolean negateIab, Long targetGroupPid) {
    List<Long> pidList =
        getSitesByTypeAndCategories(type, iab, negateIab).stream()
            .map(BdrExternalSite::getBdrPubInfoPid)
            .collect(Collectors.toList());
    List<BdrExternalPublisher> bdrExternalPublishers =
        externalPublisherRepository.findByPidIn(pidList);
    if (targetGroupPid != null) {
      return getPublishersInTargets(bdrExternalPublishers, targetGroupPid);
    }
    return bdrExternalPublishers;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getAllExternalSites(Long targetGroupPid) {
    List<BdrExternalSite> fullList = externalSiteRepository.findAll();
    if (targetGroupPid != null) {
      return getSitesInTargets(fullList, targetGroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getExternalSitesForPub(String pubPid, Long targetGroupPid) {
    List<BdrExternalSite> fullList =
        externalSiteRepository.findByBdrPubInfoPid(Long.valueOf(pubPid));
    if (targetGroupPid != null) {
      return getSitesInTargets(fullList, targetGroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getExternalSitesMatchingType(String type, Long targetgroupPid) {
    List<BdrExternalSite> fullList = externalSiteRepository.findBySiteType(type);
    if (targetgroupPid != null) {
      return getSitesInTargets(fullList, targetgroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getExternalSitesMatchingCategory(
      String iab, boolean negateIab, Long targetGroupPid) {
    List<BdrExternalSite> fullList = getSitesByCategory(iab, negateIab);
    if (targetGroupPid != null) {
      return getSitesInTargets(fullList, targetGroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getExternalSitesMatchingTypeAndCategory(
      String type, String iab, boolean negateIab, Long targetGroupPid) {
    List<BdrExternalSite> fullList = getSitesByTypeAndCategories(type, iab, negateIab);
    if (targetGroupPid != null) {
      return getSitesInTargets(fullList, targetGroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getExternalSitesForPubMatchingTypeAndCategory(
      String pubPid, String type, String iab, boolean negateIab, Long targetGroupPid) {
    List<BdrExternalSite> fullList =
        negateIab
            ? externalSiteRepository.findByBdrPubInfoPidAndSiteTypeAndIabCategoriesNotContains(
                Long.valueOf(pubPid), type, iab)
            : externalSiteRepository.findByBdrPubInfoPidAndSiteTypeAndIabCategoriesContains(
                Long.valueOf(pubPid), type, iab);
    if (targetGroupPid != null) {
      return getSitesInTargets(fullList, targetGroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getExternalSitesForPubMatchingType(
      String pubPid, String type, Long targetGroupPid) {
    List<BdrExternalSite> fullList =
        externalSiteRepository.findByBdrPubInfoPidAndSiteType(Long.valueOf(pubPid), type);
    if (targetGroupPid != null) {
      return getSitesInTargets(fullList, targetGroupPid);
    }
    return fullList;
  }

  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatholder()")
  public List<BdrExternalSite> getExternalSitesForPubMatchingCategory(
      String pubPid, String iab, boolean negateIab, Long targetGroupPid) {
    List<BdrExternalSite> fullList =
        negateIab
            ? externalSiteRepository.findByBdrPubInfoPidAndIabCategoriesNotContains(
                Long.valueOf(pubPid), iab)
            : externalSiteRepository.findByBdrPubInfoPidAndIabCategoriesContains(
                Long.valueOf(pubPid), iab);
    if (targetGroupPid != null) {
      return getSitesInTargets(fullList, targetGroupPid);
    }
    return fullList;
  }

  private List<BdrExternalSite> getSitesByCategory(String iab, boolean negateIab) {
    Set<BdrExternalSite> bdrExternalSites = new HashSet<>();
    String[] categories = iab.split(",");
    if (negateIab) {
      for (String category : categories) {
        bdrExternalSites.addAll(externalSiteRepository.findByIabCategoriesNotContains(category));
      }
    } else {
      for (String category : categories) {
        bdrExternalSites.addAll(externalSiteRepository.findByIabCategoriesContains(category));
      }
    }
    return new ArrayList<>(bdrExternalSites);
  }

  private List<BdrExternalSite> getSitesByTypeAndCategories(
      String type, String iab, boolean negateIab) {
    Set<BdrExternalSite> bdrExternalSites = new HashSet<>();
    String[] categories = iab.split(",");
    if (negateIab) {
      for (String category : categories) {
        bdrExternalSites.addAll(
            externalSiteRepository.findBySiteTypeAndIabCategoriesNotContains(type, category));
      }
    } else {
      for (String category : categories) {
        bdrExternalSites.addAll(
            externalSiteRepository.findBySiteTypeAndIabCategoriesContains(type, category));
      }
    }
    return new ArrayList<>(bdrExternalSites);
  }
}

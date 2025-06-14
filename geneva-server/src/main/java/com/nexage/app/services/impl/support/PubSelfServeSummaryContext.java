package com.nexage.app.services.impl.support;

import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PubSelfServeSummaryContext {

  private final Map<Long, Site> sitesMap = new HashMap<>();
  private final Map<Long, Tag> tagsMap = new HashMap<>();
  private final String buyerLogoBaseUrl;

  public PubSelfServeSummaryContext(List<Site> sites, String buyerLogoBaseUrl) {
    for (Site site : sites) {
      sitesMap.put(site.getPid(), site);
      for (Tag tag : site.getTags()) {
        tagsMap.put(tag.getPid(), tag);
      }
    }

    this.buyerLogoBaseUrl = buyerLogoBaseUrl;
  }

  public PubSelfServeSummaryContext(Site site, String buyerLogoBaseUrl) {
    sitesMap.put(site.getPid(), site);
    for (Tag tag : site.getTags()) {
      tagsMap.put(tag.getPid(), tag);
    }

    this.buyerLogoBaseUrl = buyerLogoBaseUrl;
  }

  public Site getSiteFromPid(long pid) {
    return sitesMap.get(pid);
  }

  public Tag getTagFromPid(long pid) {
    return tagsMap.get(pid);
  }

  public String getBuyerLogoBaseUrl() {
    return buyerLogoBaseUrl;
  }
}

package com.nexage.app.dto.publisher;

import com.nexage.admin.core.model.Site;
import java.util.HashSet;
import java.util.Set;

/** Publisher API resresentation of the {@link Site.ImpressionGroup} entity. */
public class PublisherImpressionGroupDTO {

  private boolean enabled;
  private Set<String> groups = new HashSet();

  public PublisherImpressionGroupDTO() {}

  public PublisherImpressionGroupDTO(boolean enabled, Set<String> groups) {
    this.enabled = enabled;
    this.groups = groups;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Set<String> getGroups() {
    return groups;
  }

  public void setGroups(Set<String> groups) {
    this.groups = groups;
  }
}

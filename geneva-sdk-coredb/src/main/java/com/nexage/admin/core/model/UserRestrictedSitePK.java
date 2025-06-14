package com.nexage.admin.core.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/** Embeddable composite primary key class */
@Embeddable
public class UserRestrictedSitePK implements Serializable {

  private static final long serialVersionUID = 7125662437090989499L;

  @NotNull
  @Column(name = "user_id")
  private Long userId;

  @NotNull
  @Column(name = "site_id")
  private Long siteId;

  public UserRestrictedSitePK() {}

  public UserRestrictedSitePK(Long userId, Long siteId) {
    this.userId = userId;
    this.siteId = siteId;
  }

  /** @return the userId */
  public Long getUserId() {
    return userId;
  }

  /** @return the siteId */
  public Long getSiteId() {
    return siteId;
  }

  @Override
  public boolean equals(Object o) {
    return ((o instanceof UserRestrictedSitePK)
        && (userId.equals(((UserRestrictedSitePK) o).getUserId()))
        && (siteId.equals(((UserRestrictedSitePK) o).getSiteId())));
  }

  @Override
  public int hashCode() {
    return userId.hashCode() + siteId.hashCode();
  }
}

package com.nexage.admin.core.bidder.model.view;

import java.sql.Timestamp;

public class RevInfo {

  private final Long revision;

  private final String userName;

  private final Timestamp revisionDate;

  public RevInfo(Long revision, String userName, Timestamp revisionDate) {
    this.revision = revision;
    this.userName = userName;
    this.revisionDate = revisionDate;
  }

  public Long getRevision() {
    return revision;
  }

  public String getUserName() {
    return userName;
  }

  public Timestamp getRevisionDate() {
    return revisionDate;
  }
}

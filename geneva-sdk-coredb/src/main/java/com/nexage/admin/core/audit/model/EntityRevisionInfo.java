package com.nexage.admin.core.audit.model;

import java.util.Date;
import org.hibernate.envers.RevisionType;

public class EntityRevisionInfo extends RevisionInfo {

  private final long pid;
  private final RevisionType revisionType;

  public EntityRevisionInfo(
      Number revision, String userName, Date date, long pid, RevisionType revisionType) {
    super(revision, userName, date);
    this.pid = pid;
    this.revisionType = revisionType;
  }

  public long getPid() {
    return pid;
  }

  public RevisionType getRevisionType() {
    return revisionType;
  }
}

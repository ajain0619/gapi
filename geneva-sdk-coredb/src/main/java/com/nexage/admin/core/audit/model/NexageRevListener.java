package com.nexage.admin.core.audit.model;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class NexageRevListener implements RevisionListener {

  private static final String DEFAULT_USER_NAME = "SYSTEM_PROCESS";

  @Override
  public void newRevision(Object revisionEntity) {
    RevInfo revInfo = (RevInfo) revisionEntity;
    String userName = null;
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      userName = SecurityContextHolder.getContext().getAuthentication().getName();
    } else {
      userName = DEFAULT_USER_NAME;
    }
    revInfo.setUserName(userName);
  }
}

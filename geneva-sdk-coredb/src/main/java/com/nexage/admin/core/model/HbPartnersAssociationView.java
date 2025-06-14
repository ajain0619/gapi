package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.Status;
import org.springframework.beans.factory.annotation.Value;

public interface HbPartnersAssociationView {
  @Value("#{target.pid}")
  Long getPid();

  @Value("#{target.hbPartnerPid}")
  Long getHbPartnerPid();

  @Value("#{target.positionStatus}")
  Status getPositionStatus();

  @Value("#{target.type}")
  Integer getType();
}

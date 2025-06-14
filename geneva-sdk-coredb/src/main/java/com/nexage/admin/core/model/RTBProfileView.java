package com.nexage.admin.core.model;

import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.model.RTBProfile.ScreeningLevel;
import java.math.BigDecimal;
import java.util.Date;

public interface RTBProfileView {
  String getName();

  Long getPid();

  int getAuctionType();

  String getBlockedAdTypes();

  BigDecimal getPubNetLowReserve();

  BigDecimal getPubNetReserve();

  ScreeningLevel getScreeningLevel();

  Integer getVersion();

  AlterReserve getAlterReserve();

  BigDecimal getDefaultReserve();

  boolean getIncludeConsumerId();

  boolean getIncludeConsumerProfile();

  boolean getIncludeDomainReferences();

  boolean getIncludeGeoData();

  BigDecimal getLowReserve();

  Date getCreationDate();

  String getDescription();

  Date getLastUpdate();
}

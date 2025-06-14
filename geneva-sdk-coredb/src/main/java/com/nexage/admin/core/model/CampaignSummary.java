package com.nexage.admin.core.model;

import com.nexage.admin.core.model.Campaign.CampaignModel;
import com.nexage.admin.core.model.Campaign.CampaignStatus;
import com.nexage.admin.core.model.Campaign.CampaignType;
import com.nexage.admin.core.util.TimeZoneAdjustable;
import java.math.BigDecimal;
import java.util.Date;

public interface CampaignSummary extends TimeZoneAdjustable {

  Long getPid();

  void setPid(Long pid);

  long getSellerId();

  void setSellerId(long sellerId);

  long getAdvertiserId();

  void setAdvertiserId(long advertiserId);

  String getName();

  void setName(String name);

  CampaignType getType();

  void setType(CampaignType type);

  CampaignModel getModel();

  void setModel(CampaignModel model);

  Date getStart();

  void setStart(Date start);

  Date getStop();

  void setStop(Date stop);

  CampaignStatus getStatus();

  void setStatus(CampaignStatus status);

  long getImpressions();

  void setImpressions(long impressions);

  long getDelivered();

  void setDelivered(long delivered);

  long getClicks();

  void setClicks(long clicks);

  BigDecimal getClickThroughRate();

  void setClickThroughRate(BigDecimal clickThroughRate);

  boolean isInFlight();

  void setInFlight(boolean inFlight);

  boolean isDeployable();

  void setDeployable(boolean deployable);

  boolean isPurgeable();

  @Override
  void addTimeZoneOffset();

  @Override
  void removeTimeZoneOffset();

  String getAdvertiserName();

  void setAdvertiserName(String advertiserName);
}

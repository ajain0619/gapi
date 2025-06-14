package com.nexage.admin.core.sparta.jpa.model;

import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.DirectDeal.DealStatus;
import com.nexage.admin.core.model.Rule;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface DealView {

  Long getPid();

  String getDealId();

  String getDescription();

  Date getStart();

  Date getStop();

  String getCurrency();

  Integer getVersion();

  DealPriorityType getPriorityType();

  String getPlacementFormula();

  Boolean getVisibility();

  Long getCreatedBy();

  DealStatus getStatus();

  BigDecimal getFloor();

  List<Rule> getRules();

  Date getCreationDate();

  Date getUpdatedOn();

  Integer getAuctionType();

  Integer getDealCategory();

  PlacementFormulaStatus getPlacementFormulaStatus();
}

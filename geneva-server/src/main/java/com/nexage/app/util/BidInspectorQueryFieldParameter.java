package com.nexage.app.util;

import static com.nexage.app.dto.queryfield.QueryFieldValueType.INTEGER;
import static com.nexage.app.dto.queryfield.QueryFieldValueType.LONG;
import static com.nexage.app.dto.queryfield.QueryFieldValueType.STRING;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.nexage.app.dto.queryfield.QueryFieldValueType;
import lombok.Getter;

@Getter
public enum BidInspectorQueryFieldParameter implements QueryFieldKey {
  SELLER_ID("sellerId", LONG),
  PLACEMENT_ID("placementId", INTEGER),
  SITE_ID("siteId", LONG),
  APP_BUNDLE_ID("appBundleId", INTEGER),
  DEAL_ID("dealId", STRING),
  BIDDER_ID("bidderId", INTEGER);

  @Getter private final String name;

  private final QueryFieldValueType type;
  private final Object[] allowedValues;
  @Getter private final Object[] defaultValues;

  BidInspectorQueryFieldParameter(String name, QueryFieldValueType fieldType) {
    this(name, fieldType, new Object[0]);
  }

  BidInspectorQueryFieldParameter(
      String name, QueryFieldValueType fieldType, Object[] allowedValues) {
    this(name, fieldType, allowedValues, new Object[0]);
  }

  BidInspectorQueryFieldParameter(
      String name, QueryFieldValueType fieldType, Object[] allowedValues, Object[] defaultValues) {
    this.name = name;
    this.type = fieldType;
    this.allowedValues = allowedValues;
    this.defaultValues = defaultValues;
  }
  /** {@inheritDoc} */
  @Override
  public QueryFieldValueType getType() {
    return this.type;
  }

  /** {@inheritDoc} */
  @Override
  public Object[] getAllowedValues() {
    return this.allowedValues;
  }
}

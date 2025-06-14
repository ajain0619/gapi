package com.nexage.app.util;

import static com.nexage.app.dto.queryfield.QueryFieldValueType.LONG;
import static com.nexage.app.dto.queryfield.QueryFieldValueType.STRING;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.nexage.app.dto.queryfield.QueryFieldValueType;
import lombok.Getter;

/**
 * This class stores definition of fields required/used when searching for Seller,Seller seats,Site
 * using a <b>qf</b> request parameter.<br>
 * It stores the names of the fields, their type and allowed only values (if applicable).
 */
public enum ScreenedAdsQueryFieldParameter implements QueryFieldKey {
  BIDDER_ID("bidderId", LONG),
  DSP_ID("dspId", LONG),
  LAST_SEEN_TO("lastSeenTo", STRING),
  LAST_SEEN_FROM("lastSeenFrom", STRING),
  URL("url", STRING),
  BUYER_SEAT_ID("buyerSeatId", STRING),
  CREATIVE_ID("creativeId", STRING),
  SITE_ID("siteId", LONG),
  SELLER_ID("sellerId", LONG),
  STATUS(
      "status",
      STRING,
      new Object[] {"all", "blocked", "reviewed", "notReviewed", "allowed"},
      new Object[] {"all"});

  /** name of the field used in request query string - in <code>qf</code> parameter */
  @Getter private final String name;

  private final QueryFieldValueType type;
  private final Object[] allowedValues;
  @Getter private final Object[] defaultValues;

  ScreenedAdsQueryFieldParameter(String name, QueryFieldValueType fieldType) {
    this(name, fieldType, new Object[0]);
  }

  ScreenedAdsQueryFieldParameter(
      String name, QueryFieldValueType fieldType, Object[] allowedValues) {
    this(name, fieldType, allowedValues, new Object[0]);
  }

  ScreenedAdsQueryFieldParameter(
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

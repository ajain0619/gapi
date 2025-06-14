package com.nexage.app.util;

import static com.nexage.app.dto.queryfield.QueryFieldValueType.STRING;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.nexage.app.dto.queryfield.QueryFieldValueType;
import lombok.Getter;

/**
 * This class stores definition of fields required/used when searching for Seller,Seller seats,Site
 * using a <b>qf</b> request parameter.<br>
 * It stores the names of the fields, their type and allowed only values (if applicable).
 */
@Getter
public enum BuyerSellerSeatDealQueryFieldParameter implements QueryFieldKey {
  SELLERS("sellers", STRING),
  DSP_BUYER_SEATS("dspBuyerSeats", STRING),
  DEAL_ID("dealId", STRING);

  /** name of the field used in request query string - in <code>qf</code> parameter */
  private final String name;

  private final QueryFieldValueType type;
  private final Object[] allowedValues;
  private final Object[] defaultValues;

  BuyerSellerSeatDealQueryFieldParameter(String name, QueryFieldValueType fieldType) {
    this(name, fieldType, new Object[0]);
  }

  BuyerSellerSeatDealQueryFieldParameter(
      String name, QueryFieldValueType fieldType, Object[] allowedValues) {
    this(name, fieldType, allowedValues, new Object[0]);
  }

  BuyerSellerSeatDealQueryFieldParameter(
      String name, QueryFieldValueType fieldType, Object[] allowedValues, Object[] defaultValues) {
    this.name = name;
    this.type = fieldType;
    this.allowedValues = allowedValues;
    this.defaultValues = defaultValues;
  }
}

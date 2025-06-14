package com.nexage.app.services.sellingrule.impl;

import static com.nexage.app.dto.queryfield.QueryFieldValueType.BOOL;
import static com.nexage.app.dto.queryfield.QueryFieldValueType.LONG;
import static com.nexage.app.dto.queryfield.QueryFieldValueType.STRING;
import static com.nexage.app.dto.sellingrule.RuleType.BRAND_PROTECTION;
import static com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter.DF_PLACEMENT_FIELD_NAME;
import static com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter.DF_SELLER_FIELD_NAME;
import static com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter.DF_SITE_FIELD_NAME;
import static com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter.NAME_FIELD_NAME;
import static com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter.PID_FIELD_NAME;
import static com.nexage.app.services.sellingrule.impl.SellerRuleQueryFieldParameter.TYPE_FIELD_NAME;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.nexage.app.dto.queryfield.QueryFieldValueType;
import lombok.Getter;

/**
 * This class stores definition of fields required/used when searching for seller rules using a
 * <b>qf</b> request parameter. It is used for API users endpoint only.<br>
 * It stores the names of the fields, their type, default values and allowed values (if applicable).
 */
public enum SellerRuleAPIQueryField implements QueryFieldKey {
  TYPE(
      TYPE_FIELD_NAME,
      STRING,
      new Object[] {
        BRAND_PROTECTION.name(),
      },
      new Object[] {
        BRAND_PROTECTION.name(),
      }),
  PID(PID_FIELD_NAME, LONG),
  NAME(NAME_FIELD_NAME, STRING),
  DF_SITE(DF_SITE_FIELD_NAME, LONG),
  DF_PLACEMENT(DF_PLACEMENT_FIELD_NAME, LONG),
  DF_SELLER(DF_SELLER_FIELD_NAME, BOOL, new Object[0], new Object[] {Boolean.TRUE});

  /** name of the field used in request query string - in <code>qf</code> parameter */
  @Getter private final String name;

  private final QueryFieldValueType type;
  private final Object[] allowedValues;
  /** An array of default values for the particular field. */
  @Getter private final Object[] defaultValues;

  SellerRuleAPIQueryField(String name, QueryFieldValueType fieldType) {
    this(name, fieldType, new Object[0]);
  }

  SellerRuleAPIQueryField(String name, QueryFieldValueType fieldType, Object[] allowedValues) {
    this(name, fieldType, allowedValues, new Object[0]);
  }

  SellerRuleAPIQueryField(
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

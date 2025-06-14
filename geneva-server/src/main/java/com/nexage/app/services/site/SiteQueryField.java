package com.nexage.app.services.site;

import static com.nexage.app.dto.queryfield.QueryFieldValueType.INTEGER;
import static com.nexage.app.dto.queryfield.QueryFieldValueType.LONG;
import static com.nexage.app.dto.queryfield.QueryFieldValueType.STRING;
import static com.nexage.app.services.site.SiteQueryFieldParameter.COMPANY_NAME_FIELD_NAME;
import static com.nexage.app.services.site.SiteQueryFieldParameter.COMPANY_PID_FIELD_NAME;
import static com.nexage.app.services.site.SiteQueryFieldParameter.GLOBAL_ALIAS_NAME_FIELD_NAME;
import static com.nexage.app.services.site.SiteQueryFieldParameter.NAME_FIELD_NAME;
import static com.nexage.app.services.site.SiteQueryFieldParameter.PID_FIELD_NAME;
import static com.nexage.app.services.site.SiteQueryFieldParameter.STATUS_FIELD_NAME;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.nexage.app.dto.queryfield.QueryFieldValueType;
import lombok.Getter;

/**
 * This class stores definition of fields required/used when searching for sites using a <b>qf</b>
 * request parameter.<br>
 * It stores the names of the fields, their type and allowed only values (if applicable).
 */
public enum SiteQueryField implements QueryFieldKey {
  PID(PID_FIELD_NAME, LONG),
  NAME(NAME_FIELD_NAME, STRING),
  COMPANY_NAME(COMPANY_NAME_FIELD_NAME, STRING),
  GLOBAL_ALIAS_NAME(GLOBAL_ALIAS_NAME_FIELD_NAME, STRING),
  COMPANY_PID(COMPANY_PID_FIELD_NAME, LONG),
  STATUS(STATUS_FIELD_NAME, INTEGER);

  /** name of the field used in request query string - in <code>qf</code> parameter */
  @Getter private final String name;

  private final QueryFieldValueType type;

  SiteQueryField(String name, QueryFieldValueType fieldType) {
    this.name = name;
    this.type = fieldType;
  }

  /** {@inheritDoc} */
  @Override
  public QueryFieldValueType getType() {
    return this.type;
  }

  @Override
  public Object[] getAllowedValues() {
    return new Object[0];
  }

  @Override
  public Object[] getDefaultValues() {
    return new Object[0];
  }
}

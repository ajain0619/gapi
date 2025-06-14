package com.nexage.app.util;

import static com.nexage.app.dto.queryfield.QueryFieldValueType.LONG;

import com.nexage.app.dto.queryfield.QueryFieldKey;
import com.nexage.app.dto.queryfield.QueryFieldValueType;
import lombok.Getter;

/**
 * This class stores definition of fields required/used when searching for deal MDM IDs using a
 * <b>qf</b> request parameter.<br>
 * It stores the names of the fields, their type and allowed only values (if applicable).
 */
@Getter
public enum InventoryMdmIdQueryFieldParameter implements QueryFieldKey {
  SELLER_PID("sellerPid", LONG),
  DEAL_PID("dealPid", LONG);

  private final String name;
  private final QueryFieldValueType type;
  private final Object[] allowedValues;
  private final Object[] defaultValues;

  InventoryMdmIdQueryFieldParameter(String name, QueryFieldValueType fieldType) {
    this(name, fieldType, new Object[0]);
  }

  InventoryMdmIdQueryFieldParameter(
      String name, QueryFieldValueType fieldType, Object[] allowedValues) {
    this(name, fieldType, allowedValues, new Object[0]);
  }

  InventoryMdmIdQueryFieldParameter(
      String name, QueryFieldValueType fieldType, Object[] allowedValues, Object[] defaultValues) {
    this.name = name;
    this.type = fieldType;
    this.allowedValues = allowedValues;
    this.defaultValues = defaultValues;
  }
}

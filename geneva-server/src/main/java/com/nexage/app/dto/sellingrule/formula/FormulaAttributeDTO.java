package com.nexage.app.dto.sellingrule.formula;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.site.PublisherSiteType;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

@NoArgsConstructor
@AllArgsConstructor
public enum FormulaAttributeDTO {
  PUBLISHER_NAME(FormulaAttributeInfo.PUBLISHER_NAME),
  SITE_NAME(FormulaAttributeInfo.SITE_NAME),
  LONG_FORM(FormulaAttributeInfo.LONG_FORM),
  PLACEMENT_NAME(FormulaAttributeInfo.PLACEMENT_NAME),
  SITE_TYPE(FormulaAttributeInfo.SITE_TYPE) {
    @Override
    public Object getValueAsObject(String value) {
      return Type.APPLICATION.name().equals(value)
          ? Type.APPLICATION
          : PublisherSiteType.fromString(value);
    }
  },
  PLACEMENT_TYPE(FormulaAttributeInfo.PLACEMENT_TYPE) {
    @Override
    public PlacementCategory getValueAsObject(String value) {
      PlacementCategory result = null;
      if (EnumUtils.isValidEnum(PlacementCategory.class, value)) {
        result = PlacementCategory.valueOf(value);
      }

      return result;
    }
  },
  INVENTORY_ATTRIBUTE,
  SITE_IAB_CATEGORY(FormulaAttributeInfo.SITE_IAB_CATEGORY),
  DOMAIN,
  APP_BUNDLE,
  APP_ALIAS;

  @Getter private FormulaAttributeInfo attributeInfo;

  public Object getValueAsObject(String value) {
    return value;
  }
}

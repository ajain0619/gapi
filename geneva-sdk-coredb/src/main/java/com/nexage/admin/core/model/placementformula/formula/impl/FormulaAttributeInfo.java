package com.nexage.admin.core.model.placementformula.formula.impl;

import static com.google.common.base.Joiner.on;
import static com.nexage.admin.core.model.RuleFormulaPositionView_.MEMO;
import static com.nexage.admin.core.model.RuleFormulaPositionView_.PLACEMENT_VIDEO_VIEW;
import static com.nexage.admin.core.model.RuleFormulaPositionView_.SITE;
import static com.nexage.admin.core.model.RuleFormulaPositionView_.TYPE;
import static com.nexage.admin.core.model.RuleFormulaSiteView_.COMPANY;
import static com.nexage.admin.core.model.RuleFormulaSiteView_.IAB_CATEGORIES;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.RuleFormulaCompanyView_;
import com.nexage.admin.core.model.RuleFormulaSiteView_;
import com.nexage.admin.core.model.placementformula.formula.AttributeInfo;
import com.nexage.admin.core.model.placementformula.formula.RootWrapper;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoView_;
import java.util.Objects;
import javax.persistence.criteria.Path;

public enum FormulaAttributeInfo implements AttributeInfo {
  PUBLISHER_NAME(on('.').join(SITE, COMPANY, RuleFormulaCompanyView_.NAME)),
  SITE_NAME(on('.').join(SITE, RuleFormulaSiteView_.NAME)),
  LONG_FORM(on('.').join(PLACEMENT_VIDEO_VIEW, PlacementVideoView_.LONGFORM)) {
    @Override
    public Boolean getValueAsObject(String value) {
      return Objects.equals(value, "true");
    }
  },
  PLACEMENT_NAME(MEMO /* placement name presented by memo field in the DB */),
  SITE_TYPE(null) {
    @Override
    public <T> Path<T> getPath(RootWrapper root, String value) {
      String pathAsString =
          Type.fromString(value) == null
              ? SITE + "." + RuleFormulaSiteView_.PLATFORM
              : SITE + "." + RuleFormulaSiteView_.TYPE;
      return root.getPath(pathAsString);
    }

    @Override
    public Object getValueAsObject(String value) {
      return Type.fromString(value) == null ? Platform.valueOf(value) : Type.fromString(value);
    }
  },
  PLACEMENT_TYPE(TYPE) {
    @Override
    public PlacementCategory getValueAsObject(String value) {
      return value == null ? null : PlacementCategory.valueOf(value);
    }
  },
  SITE_IAB_CATEGORY(on('.').join(SITE, IAB_CATEGORIES));

  private final String path;

  FormulaAttributeInfo(String path) {
    this.path = path;
  }

  @Override
  public Object getValueAsObject(String value) {
    return value;
  }

  @Override
  public <T> Path<T> getPath(RootWrapper root, String value) {
    return root.getPath(path);
  }
}

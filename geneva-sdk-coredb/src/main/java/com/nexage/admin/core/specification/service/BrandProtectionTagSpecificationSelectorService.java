package com.nexage.admin.core.specification.service;

import com.nexage.admin.core.model.BrandProtectionTag;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

public interface BrandProtectionTagSpecificationSelectorService {
  String CATEGORY_ID = "categoryId";
  String PARENT_ID = "parentTagPid";
  String QF = "qf";
  String QT = "qt";
  String PARENT_TAG_PID = "parentTagPid";

  Specification<BrandProtectionTag> selector(Map<String, Object> values);

  default boolean isChildSpecification() {
    return true;
  }

  boolean isValid(Map<String, Object> values);
}

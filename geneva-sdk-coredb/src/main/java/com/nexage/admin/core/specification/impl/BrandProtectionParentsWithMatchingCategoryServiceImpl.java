package com.nexage.admin.core.specification.impl;

import static com.nexage.admin.core.specification.BrandProtectionTagSpecification.parentsWithMatchingCategory;

import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.specification.service.BrandProtectionTagSpecificationSelectorService;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BrandProtectionParentsWithMatchingCategoryServiceImpl
    implements BrandProtectionTagSpecificationSelectorService {
  @Override
  public Specification<BrandProtectionTag> selector(Map<String, Object> values) {
    return parentsWithMatchingCategory(String.valueOf(values.get(QT)));
  }

  @Override
  public boolean isChildSpecification() {
    return false;
  }

  @Override
  public boolean isValid(Map<String, Object> values) {
    var parentTagPid = values.get(PARENT_ID);
    return parentTagPid == null
        && values.entrySet().stream()
            .filter(keySet -> !keySet.getKey().equals(PARENT_ID))
            .map(Map.Entry::getValue)
            .allMatch(Objects::nonNull);
  }
}

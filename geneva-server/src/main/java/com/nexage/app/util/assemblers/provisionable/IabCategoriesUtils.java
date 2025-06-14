package com.nexage.app.util.assemblers.provisionable;

import static com.nexage.app.util.assemblers.provisionable.ProvisionableUtils.getStaticJsonFolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.nexage.app.dto.provisionable.ProvisionIABCategoryDTO;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IabCategoriesUtils {

  public static Map<String, ProvisionIABCategoryDTO> categoryMap;

  private static void loadData() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      ProvisionIABCategoryDTO[] categoryTree =
          mapper.readValue(
              Resources.getResource(
                  IabCategoriesUtils.class, getStaticJsonFolder() + "iab_categories.json"),
              ProvisionIABCategoryDTO[].class);
      categoryMap = new HashMap<>();
      initHierarchy(categoryTree, null, categoryMap);
    } catch (Exception ex) {
      log.error("Exception was thrown on loading iab categories data", ex);
    }
  }

  private static void initHierarchy(
      ProvisionIABCategoryDTO[] cats,
      ProvisionIABCategoryDTO parent,
      Map<String, ProvisionIABCategoryDTO> categoryMap) {
    for (ProvisionIABCategoryDTO category : cats) {
      category.setParent(parent);
      categoryMap.put(category.getId(), category);
      if (category.getChildren() != null) {
        initHierarchy(category.getChildren(), category, categoryMap);
      }
    }
  }

  private static Map<String, ProvisionIABCategoryDTO> getData() {
    if (categoryMap == null) {
      loadData();
    }
    return categoryMap;
  }

  public static ProvisionIABCategoryDTO getCategoryDescription(String categoryId) {
    return getData().get(categoryId);
  }

  public static boolean checkParent(
      ProvisionIABCategoryDTO target, Collection<String> iabCategories) {
    boolean result = true;
    ProvisionIABCategoryDTO parent = target.getParent();
    if (parent != null) {
      result = iabCategories.contains(parent.getId()) ? false : checkParent(parent, iabCategories);
    }
    return result;
  }
}

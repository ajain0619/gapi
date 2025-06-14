package com.nexage.app.util.validator.placement.nativeads.template;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class TemplateInfo {

  private Set<String> nonConditionalPlaceholders;
  private Map<String, List<String>> placeholdersInsideConditionMap;
  private Set<String> notAllowedMarks;
}

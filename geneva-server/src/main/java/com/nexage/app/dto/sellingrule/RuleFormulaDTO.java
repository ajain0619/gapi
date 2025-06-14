package com.nexage.app.dto.sellingrule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RuleFormulaDTO {
  @Null(groups = CreateGroup.class)
  private Long pid;

  @Null(groups = CreateGroup.class)
  private Integer version;

  private boolean autoUpdate;

  @NotNull @Valid private PlacementFormulaDTO placementFormula;
}

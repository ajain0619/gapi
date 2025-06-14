package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonInclude(Include.NON_EMPTY)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class FormulaAssignedInventoryDTO {

  @EqualsAndHashCode.Include @ToString.Include @NotNull private Boolean autoUpdate;
  @EqualsAndHashCode.Include @ToString.Include private Long dealPid;

  @EqualsAndHashCode.Include @ToString.Include @NotNull
  private PlacementFormulaDTO placementFormula;
}

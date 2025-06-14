package com.nexage.app.dto.sellingrule.formula;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PlacementFormulaDTO {

  @EqualsAndHashCode.Include @ToString.Include @NotNull private FormulaGroupingDTO groupedBy;
  @NotEmpty @Valid private List<FormulaGroupDTO> formulaGroups = new ArrayList<>();
}

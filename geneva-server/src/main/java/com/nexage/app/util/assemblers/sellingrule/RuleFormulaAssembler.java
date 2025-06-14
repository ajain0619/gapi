package com.nexage.app.util.assemblers.sellingrule;

import com.nexage.admin.core.model.RuleFormula;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.util.assemblers.Assembler;
import com.nexage.app.util.assemblers.context.NullableContext;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleFormulaAssembler extends Assembler<RuleFormulaDTO, RuleFormula, NullableContext> {

  private final PlacementFormulaAssembler placementFormulaAssembler;

  @Autowired
  public RuleFormulaAssembler(PlacementFormulaAssembler placementFormulaAssembler) {
    this.placementFormulaAssembler = placementFormulaAssembler;
  }

  public static final Set<String> DEFAULT_FIELDS =
      Set.of("pid", "version", "autoUpdate", "placementFormula");

  @Override
  public RuleFormulaDTO make(NullableContext context, RuleFormula model) {
    return make(context, model, DEFAULT_FIELDS);
  }

  @Override
  public RuleFormulaDTO make(NullableContext context, RuleFormula model, Set<String> fields) {
    RuleFormulaDTO.RuleFormulaDTOBuilder builder = RuleFormulaDTO.builder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          builder.pid(model.getPid());
          break;
        case "version":
          builder.version(model.getVersion());
          break;
        case "autoUpdate":
          builder.autoUpdate(model.isAutoUpdate());
          break;
        case "placementFormula":
          if (model.getFormula() != null) {
            builder.placementFormula(placementFormulaAssembler.make(model.getFormula()));
          }
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }

    return builder.build();
  }

  @Override
  public RuleFormula apply(NullableContext context, RuleFormula model, RuleFormulaDTO dto) {
    if (dto.getPlacementFormula() != null) {
      model.setFormula(placementFormulaAssembler.applyToString(dto.getPlacementFormula()));
      model.setAutoUpdate(dto.isAutoUpdate());
    }
    return model;
  }
}

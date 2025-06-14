package com.nexage.app.mapper.rule;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.RuleFormula;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface RuleFormulaMapper {

  ObjectMapper objectMapper = new ObjectMapper();

  @Mapping(source = "formula", target = "placementFormula")
  RuleFormulaDTO map(RuleFormula entity);

  @InheritInverseConfiguration
  RuleFormula map(RuleFormulaDTO dto);

  @InheritConfiguration
  void apply(@MappingTarget RuleFormula entity, RuleFormulaDTO dto);

  default String mapPlacementFormulaDTO(PlacementFormulaDTO dto) {
    if (isNull(dto)) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(dto);
    } catch (JsonProcessingException e) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR);
    }
  }

  default PlacementFormulaDTO mapPlacementFormulaDTO(String formula) {
    if (isNull(formula)) {
      return null;
    }
    try {
      return objectMapper.readValue(formula, PlacementFormulaDTO.class);
    } catch (IOException e) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR);
    }
  }
}

package com.nexage.app.util.assemblers.sellingrule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator;
import com.nexage.admin.core.model.placementformula.formula.impl.InventoryAttribute;
import com.nexage.admin.core.model.placementformula.formula.impl.PlacementFormulaPredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.SimpleAttribute;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.assemblers.NoContextAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PlacementFormulaAssembler extends NoContextAssembler {

  private final CustomObjectMapper objectMapper;

  @Autowired
  public PlacementFormulaAssembler(CustomObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Group<RuleFormulaPositionView> apply(PlacementFormulaDTO dto) {
    PlacementFormulaPredicateBuilder builder =
        PlacementFormulaPredicateBuilder.betweenGroups(dto.getGroupedBy().getGroupOperator())
            .betweenGroupItems(GroupOperator.AND);

    dto.getFormulaGroups()
        .forEach(
            fg -> {
              List<PredicateBuilder<RuleFormulaPositionView>> group = new ArrayList<>();
              fg.getFormulaRules()
                  .forEach(
                      r -> {
                        if (r.isInventoryAttribute()) {
                          group.add(
                              new InventoryAttribute(
                                  r.getAttributePid(),
                                  r.getOperator().getOperator(),
                                  r.getRuleData()));
                        } else if (r.isDomainAppAttribute()) {
                          return;
                        } else {
                          group.add(
                              new SimpleAttribute(
                                  r.getAttribute().getAttributeInfo(),
                                  r.getOperator().getOperator(),
                                  r.getRuleData()));
                        }
                      });
              builder.addGroup(group);
            });

    return builder.build();
  }

  public PlacementFormulaDTO make(String placementFormulaString) {
    if (placementFormulaString == null) {
      return null;
    }
    try {
      return objectMapper.readValue(placementFormulaString, PlacementFormulaDTO.class);
    } catch (IOException e) {
      log.error(
          "Failed to convert string data to placement formula dto : {}", placementFormulaString);
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR);
    }
  }

  public String applyToString(PlacementFormulaDTO placementFormulaDto) {
    if (placementFormulaDto == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(placementFormulaDto);
    } catch (JsonProcessingException e) {
      log.error("Failed to convert placement formula dto to string data : {}", placementFormulaDto);
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_FORMULA_DATA_CONVERSION_ERROR);
    }
  }
}

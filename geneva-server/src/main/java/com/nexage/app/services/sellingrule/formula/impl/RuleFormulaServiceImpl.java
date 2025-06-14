package com.nexage.app.services.sellingrule.formula.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.specification.RuleSpecification;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.services.sellingrule.formula.RuleFormulaService;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class RuleFormulaServiceImpl implements RuleFormulaService {

  private final PlacementFormulaAssembler placementFormulaAssembler;
  private final RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository;

  public RuleFormulaServiceImpl(
      PlacementFormulaAssembler placementFormulaAssembler,
      RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository) {
    this.placementFormulaAssembler = placementFormulaAssembler;
    this.ruleFormulaPositionViewRepository = ruleFormulaPositionViewRepository;
  }

  /** {@inheritDoc} */
  public InventoryAssignmentsDTO processFormula(
      @Nullable RuleFormulaDTO inputFormula, @Nonnull Long sellerPid) {
    InventoryAssignmentsDTO inventoryAssignments = null;

    if (inputFormula != null && inputFormula.getPlacementFormula() != null) {
      PlacementFormulaDTO formula = inputFormula.getPlacementFormula();
      List<RuleFormulaPositionView> rulePosition =
          ruleFormulaPositionViewRepository.findAll(
              RuleSpecification.withDefaultRtbProfiles(
                  Collections.singleton(sellerPid), placementFormulaAssembler.apply(formula)));
      Set<PositionAssignmentDTO> positions = new HashSet<>();

      if (isNotEmpty(rulePosition)) {
        rulePosition.forEach(
            pos -> {
              if (pos.getSite() != null
                  && pos.getSite().getCompany() != null
                  && sellerPid.equals(pos.getSite().getCompany().getPid())) {
                PublisherAssignmentDTO pubDto =
                    PublisherAssignmentDTO.builder()
                        .name(pos.getSite().getCompany().getName())
                        .pid(pos.getSite().getCompany().getPid())
                        .build();

                SiteAssignmentDTO siteDto =
                    SiteAssignmentDTO.builder()
                        .name(pos.getSite().getName())
                        .pid(pos.getSite().getPid())
                        .publisherAssignment(pubDto)
                        .build();

                PositionAssignmentDTO posDto =
                    PositionAssignmentDTO.builder()
                        .pid(pos.getPid())
                        .memo(pos.getMemo())
                        .name(pos.getName())
                        .siteAssignment(siteDto)
                        .build();

                positions.add(posDto);
              }
            });
      }
      inventoryAssignments = InventoryAssignmentsDTO.builder().positions(positions).build();
    }

    return inventoryAssignments;
  }
}

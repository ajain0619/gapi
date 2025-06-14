package com.nexage.app.util.assemblers.sellingrule;

import static java.util.stream.Collectors.toSet;

import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import com.nexage.app.util.assemblers.NoContextAssembler;
import com.nexage.app.util.assemblers.context.SellingRuleContext;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleAssignmentAssembler extends NoContextAssembler {
  private final SiteRuleAssignmentAssembler siteRuleAssignmentAssembler;
  private final PositionRuleAssignmentAssembler positionRuleAssignmentAssembler;
  private final PublisherRuleAssignmentAssembler publisherRuleAssignmentAssembler;

  @Autowired
  public RuleAssignmentAssembler(
      SiteRuleAssignmentAssembler siteRuleAssignmentAssembler,
      PositionRuleAssignmentAssembler positionRuleAssignmentAssembler,
      PublisherRuleAssignmentAssembler publisherRuleAssignmentAssembler) {
    this.siteRuleAssignmentAssembler = siteRuleAssignmentAssembler;
    this.positionRuleAssignmentAssembler = positionRuleAssignmentAssembler;
    this.publisherRuleAssignmentAssembler = publisherRuleAssignmentAssembler;
  }

  public static final Set<String> DEFAULT_FIELDS = Set.of("publishers", "sites", "positions");

  public InventoryAssignmentsDTO make(SellingRuleContext context) {
    return make(context, DEFAULT_FIELDS);
  }

  public InventoryAssignmentsDTO make(SellingRuleContext context, Set<String> fields) {
    InventoryAssignmentsDTO.InventoryAssignmentsDTOBuilder builder =
        InventoryAssignmentsDTO.builder();

    for (String field : fields) {
      switch (field) {
        case "publishers":
          Set<RuleDeployedCompany> companies = context.getPublishers();
          if (companies != null && companies.size() > 0) {
            Set<PublisherAssignmentDTO> publisherAssignmentDtos = new HashSet<>();

            for (RuleDeployedCompany company : companies) {
              publisherAssignmentDtos.add(publisherRuleAssignmentAssembler.make(company));
            }
            builder.publishers(publisherAssignmentDtos);
          }
          break;
        case "sites":
          Set<RuleDeployedSite> sites = context.getSites();
          if (sites != null && sites.size() > 0) {
            Set<SiteAssignmentDTO> siteAssignmentDtos = new HashSet<>();

            for (RuleDeployedSite site : sites) {
              siteAssignmentDtos.add(siteRuleAssignmentAssembler.make(site));
            }
            builder.sites(siteAssignmentDtos);
          }
          break;
        case "positions":
          Set<RuleDeployedPosition> positions = context.getPositions();
          if (positions != null && positions.size() > 0) {
            Set<PositionAssignmentDTO> positionAssignmentDtos = new HashSet<>();

            for (RuleDeployedPosition position : positions) {
              positionAssignmentDtos.add(positionRuleAssignmentAssembler.make(position));
            }
            builder.positions(positionAssignmentDtos);
          }
          break;
        default:
          throw new RuntimeException("Unknown field '" + field + "'.");
      }
    }
    return builder.build();
  }

  public CompanyRule apply(CompanyRule entity, InventoryAssignmentsDTO assignments) {
    processPublishers(entity, assignments);

    processSites(entity, assignments);

    processPositions(entity, assignments);

    return entity;
  }

  private void processPublishers(CompanyRule entity, InventoryAssignmentsDTO dto) {
    entity.getDeployedCompanies().clear();
    entity
        .getDeployedCompanies()
        .addAll(
            dto.getPublishers().stream()
                .map(publisherDto -> publisherRuleAssignmentAssembler.apply(publisherDto))
                .collect(toSet()));
  }

  private void processSites(CompanyRule entity, InventoryAssignmentsDTO dto) {
    entity.getDeployedSites().clear();
    entity
        .getDeployedSites()
        .addAll(
            dto.getSites().stream()
                .map(siteDto -> siteRuleAssignmentAssembler.apply(siteDto))
                .collect(toSet()));
  }

  private void processPositions(CompanyRule entity, InventoryAssignmentsDTO dto) {
    entity.getDeployedPositions().clear();
    entity
        .getDeployedPositions()
        .addAll(
            dto.getPositions().stream()
                .map(positionDto -> positionRuleAssignmentAssembler.apply(positionDto))
                .collect(toSet()));
  }
}

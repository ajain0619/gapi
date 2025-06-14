package com.nexage.app.util.assemblers.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import com.nexage.app.util.assemblers.context.SellingRuleContext;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleAssignmentAssemblerTest {

  @Mock private SiteRuleAssignmentAssembler siteRuleAssignmentAssembler;
  @Mock private PositionRuleAssignmentAssembler positionRuleAssignmentAssembler;
  @Mock private PublisherRuleAssignmentAssembler publisherRuleAssignmentAssembler;
  @InjectMocks private RuleAssignmentAssembler assembler;

  @Test
  void shouldReturnInventoryAssignmentDtoWhenSellingRuleContextHasData() {
    RuleDeployedSite site = TestObjectsFactory.createRuleDeployedSite();
    RuleDeployedPosition position = TestObjectsFactory.createRuleDeployedPosition();
    RuleDeployedCompany publisher = TestObjectsFactory.createRuleDeployedCompany();

    SiteAssignmentDTO expectedSite = TestObjectsFactory.createSiteAssignmentDto();
    PositionAssignmentDTO expectedPosition = TestObjectsFactory.createPositionAssignmentDto();
    PublisherAssignmentDTO expectedPublisher = TestObjectsFactory.createPublisherAssignmentDto();

    when(siteRuleAssignmentAssembler.make(site)).thenReturn(expectedSite);
    when(positionRuleAssignmentAssembler.make(position)).thenReturn(expectedPosition);
    when(publisherRuleAssignmentAssembler.make(publisher)).thenReturn(expectedPublisher);

    InventoryAssignmentsDTO actual =
        assembler.make(
            buildSellingRuleContext(
                buildSites(site), buildPositions(position), buildPublishers(publisher)));
    assertNotNull(actual);
    assertEquals(true, actual.getSites().contains(expectedSite));
    assertEquals(true, actual.getPositions().contains(expectedPosition));
    assertEquals(true, actual.getPublishers().contains(expectedPublisher));
  }

  private SellingRuleContext buildSellingRuleContext(
      Set<RuleDeployedSite> sites,
      Set<RuleDeployedPosition> positions,
      Set<RuleDeployedCompany> publishers) {
    SellingRuleContext.Builder sellingRuleContextBuilder =
        SellingRuleContext.newBuilder()
            .withPublishersForSellingRule(publishers)
            .withSitesForSellingRule(sites)
            .withPositionsForSellingRule(positions);

    return sellingRuleContextBuilder.build();
  }

  private Set<RuleDeployedSite> buildSites(RuleDeployedSite site) {
    Set<RuleDeployedSite> sites = new HashSet<>();
    sites.add(site);
    return sites;
  }

  private Set<RuleDeployedPosition> buildPositions(RuleDeployedPosition position) {
    Set<RuleDeployedPosition> positions = new HashSet<>();
    positions.add(position);
    return positions;
  }

  private Set<RuleDeployedCompany> buildPublishers(RuleDeployedCompany publisher) {
    Set<RuleDeployedCompany> publishers = new HashSet<>();
    publishers.add(publisher);
    return publishers;
  }
}

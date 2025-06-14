package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator;
import com.nexage.admin.core.model.placementformula.formula.impl.PlacementFormulaPredicateBuilder;
import com.nexage.admin.core.repository.AttributeCompanyVisibilityRepository;
import com.nexage.admin.core.repository.InventoryAttributeRepository;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.specification.RuleSpecification;
import com.nexage.app.dto.sellingrule.FormulaInventoryDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.mapper.PlacementFormulaDTOMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class FormulaInventoryServiceImplTest {
  @Mock private RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository;
  @Mock private InventoryAttributeRepository inventoryAttributeRepository;
  @Mock private AttributeCompanyVisibilityRepository attributeCompanyVisibilityRepository;

  @Mock private PlacementFormulaDTOMapper placementFormulaDTOMapper;

  @InjectMocks private FormulaInventoryServiceImpl formulaInventoryService;

  @Mock private PlacementFormulaDTO PlacementFormulaDTO;
  @Mock private FormulaGroupDTO formulaGroup1;
  @Mock private FormulaGroupDTO formulaGroup2;
  @Mock private FormulaRuleDTO formulaRule11;
  @Mock private FormulaRuleDTO formulaRule12;
  @Mock private FormulaRuleDTO formulaRule21;
  @Mock private FormulaRuleDTO formulaRule22;

  @BeforeEach
  void setup() {
    when(PlacementFormulaDTO.getGroupedBy()).thenReturn(FormulaGroupingDTO.OR);
  }

  @Test
  void shouldReturnNoPlacementsByFormula() {
    // given
    PlacementFormulaDTO placementFormulaDTO = getPlacementFormulaDTO();

    PlacementFormulaPredicateBuilder builder =
        PlacementFormulaPredicateBuilder.betweenGroups(
                placementFormulaDTO.getGroupedBy().getGroupOperator())
            .betweenGroupItems(GroupOperator.AND);

    given(placementFormulaDTOMapper.map(placementFormulaDTO)).willReturn(builder.build());
    given(inventoryAttributeRepository.countByGlobalVisibility(true, List.of(1L))).willReturn(0L);
    given(attributeCompanyVisibilityRepository.findCompaniesForAttributes(List.of(1L)))
        .willReturn(List.of(1L));

    // when
    Page<RuleFormulaPositionView> ruleFormulaPositionViews =
        formulaInventoryService.findPlacementsByFormula(placementFormulaDTO, Pageable.unpaged());

    // then
    assertNull(ruleFormulaPositionViews);
  }

  @Test
  void shouldReturnPlacementsByFormulaWhenAttributesFoundByGlobalVisibility() {
    // given
    PlacementFormulaDTO placementFormulaDTO = getPlacementFormulaDTO();

    PlacementFormulaPredicateBuilder builder =
        PlacementFormulaPredicateBuilder.betweenGroups(
                placementFormulaDTO.getGroupedBy().getGroupOperator())
            .betweenGroupItems(GroupOperator.AND);
    Group<RuleFormulaPositionView> group = builder.build();

    when(placementFormulaDTOMapper.map(placementFormulaDTO)).thenReturn(group);
    given(inventoryAttributeRepository.countByGlobalVisibility(true, List.of(1L))).willReturn(1L);
    given(
            ruleFormulaPositionViewRepository.findAll(
                RuleSpecification.withDefaultRtbProfiles(Collections.emptyList(), group)))
        .willReturn(List.of(new RuleFormulaPositionView()));

    // when
    Page<RuleFormulaPositionView> ruleFormulaPositionViews =
        formulaInventoryService.findPlacementsByFormula(placementFormulaDTO, Pageable.unpaged());

    // then
    assertNull(ruleFormulaPositionViews);
    verify(ruleFormulaPositionViewRepository)
        .findAll(any(Specification.class), any(Pageable.class));
  }

  private PlacementFormulaDTO getPlacementFormulaDTO() {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setRuleData("test1,test2");
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    formulaRuleDTO.setAttributePid(1L);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);
    return placementFormulaDTO;
  }

  private PlacementFormulaDTO getPlacementFormulaDTOwithDomainAppAttribute(
      FormulaAttributeDTO attribute) {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setRuleData("test1,test2");
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.NOT_MEMBER_OF);
    formulaRuleDTO.setAttributePid(1L);
    FormulaRuleDTO formulaRuleDTO2 = new FormulaRuleDTO();
    String ruleData =
        "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"" + attribute.toString() + "\"}";
    formulaRuleDTO2.setRuleData(ruleData);
    formulaRuleDTO2.setAttribute(attribute);
    formulaRuleDTO2.setOperator(FormulaOperatorDTO.EQUALS);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO, formulaRuleDTO2);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);
    return placementFormulaDTO;
  }

  @Test
  void shouldReturnPlacementsByFormulaForDeals() {
    // given
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setRuleData(PlacementCategory.BANNER.toString());
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.PLACEMENT_TYPE);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);
    PlacementFormulaPredicateBuilder builder =
        PlacementFormulaPredicateBuilder.betweenGroups(
                placementFormulaDTO.getGroupedBy().getGroupOperator())
            .betweenGroupItems(GroupOperator.AND);

    Integer count = 10;

    List<RuleFormulaPositionView> ruleFormulaPositionViews =
        TestObjectsFactory.gimme(count, RuleFormulaPositionView.class);
    Page<RuleFormulaPositionView> page = new PageImpl(ruleFormulaPositionViews);
    given(placementFormulaDTOMapper.map(placementFormulaDTO)).willReturn(builder.build());
    given(ruleFormulaPositionViewRepository.findAll(any(Specification.class), any(Pageable.class)))
        .willReturn(page);

    // when
    Page<FormulaInventoryDTO> ruleFormulaPositionDTOSResponse =
        formulaInventoryService.getPlacementsByFormulaForDeals(
            placementFormulaDTO, PageRequest.of(0, count));

    // then
    assertEquals(count.longValue(), ruleFormulaPositionDTOSResponse.getTotalElements());
  }

  @Test
  void shouldReturnListOfPlacementsByFormula() {
    // given
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setRuleData(PlacementCategory.BANNER.toString());
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.PLACEMENT_TYPE);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);
    PlacementFormulaPredicateBuilder builder =
        PlacementFormulaPredicateBuilder.betweenGroups(
                placementFormulaDTO.getGroupedBy().getGroupOperator())
            .betweenGroupItems(GroupOperator.AND);

    Integer count = 10;
    List<RuleFormulaPositionView> ruleFormulaPositionViews =
        TestObjectsFactory.gimme(count, RuleFormulaPositionView.class);
    Page<RuleFormulaPositionView> page = new PageImpl(ruleFormulaPositionViews);
    given(placementFormulaDTOMapper.map(placementFormulaDTO)).willReturn(builder.build());
    given(ruleFormulaPositionViewRepository.findAll(any(Specification.class), any(Pageable.class)))
        .willReturn(page);

    // when
    Page<RuleFormulaPositionView> ruleFormulaPositionDTOSResponse =
        formulaInventoryService.findPlacementsByFormula(placementFormulaDTO, Pageable.unpaged());

    // then
    assertEquals(count.longValue(), ruleFormulaPositionDTOSResponse.getTotalElements());
  }

  @Test
  void shouldReturnGetPlacementsByFormulaForPublisher() {
    // given
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setRuleData(PlacementCategory.BANNER.toString());
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.PLACEMENT_TYPE);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);
    PlacementFormulaPredicateBuilder builder =
        PlacementFormulaPredicateBuilder.betweenGroups(
                placementFormulaDTO.getGroupedBy().getGroupOperator())
            .betweenGroupItems(GroupOperator.AND);

    Integer count = 10;
    Long publisherPid = 1L;

    List<RuleFormulaPositionView> ruleFormulaPositionViews =
        TestObjectsFactory.gimme(count, RuleFormulaPositionView.class);
    Page<RuleFormulaPositionView> page = new PageImpl(ruleFormulaPositionViews);
    given(placementFormulaDTOMapper.map(placementFormulaDTO)).willReturn(builder.build());
    given(ruleFormulaPositionViewRepository.findAll(any(Specification.class), any(Pageable.class)))
        .willReturn(page);

    // when
    Page<FormulaInventoryDTO> ruleFormulaPositionDTOSResponse =
        formulaInventoryService.getPlacementsByFormulaForPublisher(
            publisherPid, placementFormulaDTO, PageRequest.of(0, count));

    // then
    assertEquals(count.longValue(), ruleFormulaPositionDTOSResponse.getTotalElements());
  }
}

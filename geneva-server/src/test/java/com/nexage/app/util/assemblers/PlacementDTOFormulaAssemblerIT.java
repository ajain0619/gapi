package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator;
import com.nexage.admin.core.model.placementformula.formula.impl.Operator;
import com.nexage.admin.core.model.placementformula.formula.impl.SimpleAttribute;
import com.nexage.app.config.GenevaServerJacksonBeanFactory;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
class PlacementDTOFormulaAssemblerIT {

  private static CustomObjectMapper mapper =
      GenevaServerJacksonBeanFactory.initCustomObjectMapper();
  private PlacementFormulaAssembler assembler = new PlacementFormulaAssembler(mapper);

  @Test
  void test() throws IOException {
    PlacementFormulaDTO dto;
    try (InputStream is = getClass().getResourceAsStream("/data/GridPFormulaInvAttr.json")) {
      dto = mapper.readValue(is, PlacementFormulaDTO.class);
    }
    Group<RuleFormulaPositionView> result = assembler.apply(dto);

    assertEquals(GroupOperator.OR, result.getOperator());
    assertEquals(2, result.getItems().size());
    List<? extends PredicateBuilder<RuleFormulaPositionView>> rootMembers = result.getItems();
    Group<RuleFormulaPositionView> formulaGroup1 =
        (Group<RuleFormulaPositionView>) rootMembers.get(0);
    assertEquals(GroupOperator.AND, formulaGroup1.getOperator());
    final List<? extends PredicateBuilder<RuleFormulaPositionView>> formulaGroup1Members =
        formulaGroup1.getItems();

    SimpleAttribute simpleAttribute1 = (SimpleAttribute) formulaGroup1Members.get(0);
    assertEquals(Operator.EQUALS, simpleAttribute1.getOperator());
    assertEquals("MOBILE_WEB", simpleAttribute1.getValue());
  }

  @Autowired PlacementFormulaAssembler placementFormulaAssembler;

  @Test
  void test_make() {
    String pf =
        "{"
            + "   \"groupedBy\":\"OR\","
            + "   \"formulaGroups\":["
            + "      {"
            + "         \"formulaRules\":["
            + "            {"
            + "               \"attribute\":\"PUBLISHER_NAME\","
            + "               \"operator\":\"EQUALS\","
            + "               \"ruleData\":\"Yahoo! NAR\""
            + "            }"
            + "         ]"
            + "      }"
            + "   ]"
            + "}";
    // method under test
    PlacementFormulaDTO placementFormulaDto = placementFormulaAssembler.make(pf);
    assertNotNull(placementFormulaDto);

    FormulaGroupingDTO formulaGroupingDto = placementFormulaDto.getGroupedBy();
    assertEquals(FormulaGroupingDTO.OR, formulaGroupingDto);

    FormulaRuleDTO formulaRuleDto =
        placementFormulaDto.getFormulaGroups().get(0).getFormulaRules().get(0);
    assertEquals(FormulaAttributeDTO.PUBLISHER_NAME, formulaRuleDto.getAttribute());
    assertEquals(FormulaOperatorDTO.EQUALS, formulaRuleDto.getOperator());
    assertEquals("Yahoo! NAR", formulaRuleDto.getRuleData());
  }

  @Test
  void test_make_Null() {
    assertNull(placementFormulaAssembler.make(null));
  }

  @Test
  void test_make_Exception() {
    assertThrows(
        GenevaValidationException.class,
        () -> placementFormulaAssembler.make("{\"invalidBy\":\"OR\",\"formulaGroups\":[]}"));
  }

  @Test
  void test_applyToString() {
    PlacementFormulaDTO placementFormulaDto = new PlacementFormulaDTO();
    placementFormulaDto.setGroupedBy(FormulaGroupingDTO.OR);

    FormulaRuleDTO formulaRuleDto = new FormulaRuleDTO();
    formulaRuleDto.setAttribute(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    formulaRuleDto.setOperator(FormulaOperatorDTO.NOT_EQUALS);
    formulaRuleDto.setRuleData("LREC");
    formulaRuleDto.setAttributePid(1L);
    FormulaGroupDTO formulaGroupDto = new FormulaGroupDTO();
    formulaGroupDto.setFormulaRules(Collections.singletonList(formulaRuleDto));
    placementFormulaDto.setFormulaGroups(Collections.singletonList(formulaGroupDto));

    // method under test
    String formula = placementFormulaAssembler.applyToString(placementFormulaDto);
    assertEquals(
        "{\"groupedBy\":\"OR\",\"formulaGroups\":[{\"formulaRules\":[{\"attribute\":\"INVENTORY_ATTRIBUTE\",\"operator\":\"NOT_EQUALS\",\"ruleData\":\"LREC\",\"attributePid\":1}]}]}",
        formula);
  }

  @Test
  void test_applyToString_Null() {
    assertNull(placementFormulaAssembler.applyToString(null));
  }

  @Test
  void apply_whenFormulaRuleHasDomainAttribute_thenShouldIgnoreIt() {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    String ruleData = "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"DOMAIN\"}";
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.DOMAIN);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);

    Group<RuleFormulaPositionView> result = assembler.apply(placementFormulaDTO);

    // No change in the incoming PlacementFormulaDTO
    assertEquals(1, placementFormulaDTO.getFormulaGroups().get(0).getFormulaRules().size());

    // Domain Rule formula should not be added to result
    assertEquals(0, ((Group) result.getItems().get(0)).getItems().size());
  }

  @Test
  void apply_whenFormulaRuleHasAppAliasAttribute_thenShouldIgnore() {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    String ruleData = "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"APP_ALIAS\"}";
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.APP_ALIAS);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);

    Group<RuleFormulaPositionView> result = assembler.apply(placementFormulaDTO);

    // No change in the incoming PlacementFormulaDTO
    assertEquals(1, placementFormulaDTO.getFormulaGroups().get(0).getFormulaRules().size());

    // App Alias Rule formula should not be added to result
    assertEquals(0, ((Group) result.getItems().get(0)).getItems().size());
  }

  @Test
  void apply_whenFormulaRuleHasAppBundleAttribute_thenShouldIgnore() {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    String ruleData = "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"APP_BUNDLE\"}";
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.APP_BUNDLE);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    List<FormulaRuleDTO> formulaRuleDTOS = List.of(formulaRuleDTO);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);

    Group<RuleFormulaPositionView> result = assembler.apply(placementFormulaDTO);

    // No change in the incoming PlacementFormulaDTO
    assertEquals(1, placementFormulaDTO.getFormulaGroups().get(0).getFormulaRules().size());

    // App Bundle Rule formula should not be added to result
    assertEquals(0, ((Group) result.getItems().get(0)).getItems().size());
  }

  @Test
  void
      apply_whenFormulaRuleHasDomainAttributeAndOthers_thenShouldIgnoreOnlyAppBundleAndOtherAttributeFormulaShouldBeAdded() {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();

    // Domain Rule formula dto
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    String ruleData = "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"DOMAIN\"}";
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.DOMAIN);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);

    // Inventory Rule formula dto
    FormulaRuleDTO formulaRuleDTO2 = new FormulaRuleDTO();
    formulaRuleDTO2.setRuleData("test1,test2");
    formulaRuleDTO2.setAttribute(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    formulaRuleDTO2.setOperator(FormulaOperatorDTO.EQUALS);
    formulaRuleDTO2.setAttributePid(1L);

    // Placement type Rule formula dto
    FormulaRuleDTO formulaRuleDTO3 = new FormulaRuleDTO();
    formulaRuleDTO3.setRuleData("BANNER,INSTREAM_VIDEO");
    formulaRuleDTO3.setAttribute(FormulaAttributeDTO.PLACEMENT_TYPE);
    formulaRuleDTO3.setOperator(FormulaOperatorDTO.EQUALS);

    List<FormulaRuleDTO> formulaRuleDTOS =
        List.of(formulaRuleDTO, formulaRuleDTO2, formulaRuleDTO3);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);

    Group<RuleFormulaPositionView> result = assembler.apply(placementFormulaDTO);

    // No change in the incoming PlacementFormulaDTO
    assertEquals(3, placementFormulaDTO.getFormulaGroups().get(0).getFormulaRules().size());

    // Domain Rule formula should not be added to result
    assertEquals(2, ((Group) result.getItems().get(0)).getItems().size());
  }

  @Test
  void
      apply_whenFormulaRuleHasAppAliasAttributeAndOthers_thenShouldIgnoreOnlyAppBundleAndOtherAttributeFormulaShouldBeAdded() {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();

    // APP ALIAS rule formula dto
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    String ruleData = "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"APP_ALIAS\"}";
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.APP_ALIAS);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);

    // Inventory Rule formula
    FormulaRuleDTO formulaRuleDTO2 = new FormulaRuleDTO();
    formulaRuleDTO2.setRuleData("test1,test2");
    formulaRuleDTO2.setAttribute(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    formulaRuleDTO2.setOperator(FormulaOperatorDTO.EQUALS);
    formulaRuleDTO2.setAttributePid(1L);

    // Placement type Rule formula dto
    FormulaRuleDTO formulaRuleDTO3 = new FormulaRuleDTO();
    formulaRuleDTO3.setRuleData("BANNER,INSTREAM_VIDEO");
    formulaRuleDTO3.setAttribute(FormulaAttributeDTO.PLACEMENT_TYPE);
    formulaRuleDTO3.setOperator(FormulaOperatorDTO.EQUALS);

    List<FormulaRuleDTO> formulaRuleDTOS =
        List.of(formulaRuleDTO, formulaRuleDTO2, formulaRuleDTO3);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);

    Group<RuleFormulaPositionView> result = assembler.apply(placementFormulaDTO);

    // No change in the incoming PlacementFormulaDTO
    assertEquals(3, placementFormulaDTO.getFormulaGroups().get(0).getFormulaRules().size());

    // App Alias Rule formula should not be added to result
    assertEquals(2, ((Group) result.getItems().get(0)).getItems().size());
  }

  @Test
  void
      apply_whenFormulaRuleHasAppBundleAttributeAndOthers_thenShouldIgnoreOnlyAppBundleAndOtherAttributeFormulaShouldBeAdded() {
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO();

    // App Bundle Rule Formula
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    String ruleData = "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"APP_BUNDLE\"}";
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(FormulaAttributeDTO.APP_BUNDLE);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);

    // Inventory Rule formula
    FormulaRuleDTO formulaRuleDTO2 = new FormulaRuleDTO();
    formulaRuleDTO2.setRuleData("test1,test2");
    formulaRuleDTO2.setAttribute(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    formulaRuleDTO2.setOperator(FormulaOperatorDTO.EQUALS);
    formulaRuleDTO2.setAttributePid(1L);

    // Placement type Rule formula dto
    FormulaRuleDTO formulaRuleDTO3 = new FormulaRuleDTO();
    formulaRuleDTO3.setRuleData("BANNER,INSTREAM_VIDEO");
    formulaRuleDTO3.setAttribute(FormulaAttributeDTO.PLACEMENT_TYPE);
    formulaRuleDTO3.setOperator(FormulaOperatorDTO.EQUALS);

    List<FormulaRuleDTO> formulaRuleDTOS =
        List.of(formulaRuleDTO, formulaRuleDTO2, formulaRuleDTO3);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(formulaRuleDTOS);
    List<FormulaGroupDTO> formulaGroupDTOList = List.of(formulaGroupDTO);
    placementFormulaDTO.setFormulaGroups(formulaGroupDTOList);
    placementFormulaDTO.setGroupedBy(FormulaGroupingDTO.OR);

    Group<RuleFormulaPositionView> result = assembler.apply(placementFormulaDTO);

    // No change in the incoming PlacementFormulaDTO
    assertEquals(3, placementFormulaDTO.getFormulaGroups().get(0).getFormulaRules().size());

    // App Bundle Rule formula should not be added to result
    assertEquals(2, ((Group) result.getItems().get(0)).getItems().size());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenMapperThrowsJsonProcessingException()
      throws JsonProcessingException {
    // given
    PlacementFormulaDTO dto = new PlacementFormulaDTO();
    CustomObjectMapper customObjectMapper = Mockito.mock(CustomObjectMapper.class);
    Mockito.when(customObjectMapper.writeValueAsString(dto))
        .thenThrow(JsonProcessingException.class);
    PlacementFormulaAssembler assembler = new PlacementFormulaAssembler(customObjectMapper);

    // when/then
    assertThrows(GenevaValidationException.class, () -> assembler.applyToString(dto));
  }
}

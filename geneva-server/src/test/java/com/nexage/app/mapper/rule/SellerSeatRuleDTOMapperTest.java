package com.nexage.app.mapper.rule;

import static com.nexage.app.web.support.TestObjectsFactory.createFilterRuleIntendedActionDto;
import static com.nexage.app.web.support.TestObjectsFactory.createFloorRuleIntendedActionDto;
import static com.nexage.app.web.support.TestObjectsFactory.createRuleTargetDto;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatRule;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatRuleDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.model.SellerSeatRule;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import com.nexage.app.util.CustomObjectMapper;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SellerSeatRuleDTOMapperTest {

  private SellerSeatRuleDTOMapper mapper = SellerSeatRuleDTOMapper.MAPPER;

  private BidderConfigRepository bidderConfigRepository =
      Mockito.mock(BidderConfigRepository.class);

  private RuleTargetDataConverter ruleTargetDataConverter =
      new RuleTargetDataConverter(new CustomObjectMapper(), bidderConfigRepository);

  @BeforeEach
  public void setUp() throws Exception {
    when(bidderConfigRepository.findCompanyPidByPid(1000L)).thenReturn(20L);
  }

  @Test
  void entityDtoMappingTest() {
    SellerSeatRule rule = createSellerSeatRule();
    SellerSeatRuleDTO mapped = mapper.map(rule, ruleTargetDataConverter);

    assertEquals(rule.getPid(), mapped.getPid());
    assertEquals(rule.getVersion(), mapped.getVersion());
    assertEquals(rule.getStatus(), mapped.getStatus());
    assertFalse(mapped.getIntendedActions().isEmpty());
    assertEquals(rule.getName(), mapped.getName());
    assertFalse(mapped.getTargets().isEmpty());
    assertEquals(rule.getRuleType().name(), mapped.getType().name());
    assertEquals(rule.getDescription(), mapped.getDescription());
    assertEquals(rule.getSellerSeatPid(), mapped.getSellerSeatPid());
  }

  @Test
  void dtoEntityMappingTest() {
    SellerSeatRuleDTO dto = createSellerSeatRuleDto();
    SellerSeatRule mapped = mapper.map(dto);

    dtoEntityMappingCommonAsserts(dto, mapped);
  }

  @Test
  void dtoEntityApplyTest() {
    SellerSeatRule rule = createSellerSeatRule();
    SellerSeatRuleDTO dto = createSellerSeatRuleDto();
    SellerSeatRule returnedSellerSeatRule = mapper.update(dto);

    dtoEntityApplyCommonAsserts(returnedSellerSeatRule, dto);
    assertEquals(dto.getPid(), returnedSellerSeatRule.getPid());
  }

  @Test
  void dtoEntityApplySellerSeatPidIsUnchangedTest() {
    SellerSeatRule rule = createSellerSeatRule();
    SellerSeatRuleDTO dto = createSellerSeatRuleDto();
    ReflectionTestUtils.setField(dto, "sellerSeatPid", 6L);
    SellerSeatRule returnedSellerSeatRule = mapper.update(dto);

    dtoEntityApplyCommonAsserts(returnedSellerSeatRule, dto);
    assertEquals(returnedSellerSeatRule.getSellerSeatPid(), dto.getSellerSeatPid());
  }

  @Test
  void dtoEntityApplyTargetsAndIntendedActionsReplaceTest() {
    SellerSeatRule rule = createSellerSeatRule();
    SellerSeatRuleDTO dto = createSellerSeatRuleDto();
    HashSet<RuleTargetDTO> targets =
        Sets.newHashSet(
            createRuleTargetDto(1L, MatchType.EXCLUDE_LIST),
            createRuleTargetDto(2L, MatchType.INCLUDE_LIST));
    HashSet<IntendedActionDTO> intendedActions =
        Sets.newHashSet(createFilterRuleIntendedActionDto(), createFloorRuleIntendedActionDto());
    ReflectionTestUtils.setField(dto, "targets", targets);
    ReflectionTestUtils.setField(dto, "intendedActions", intendedActions);

    assertEquals(1, rule.getRuleTargets().size());
    assertEquals(1, rule.getRuleIntendedActions().size());

    SellerSeatRule returnedSellerSeatRule = mapper.update(dto);

    dtoEntityApplyCommonAsserts(returnedSellerSeatRule, dto);
    assertEquals(2, returnedSellerSeatRule.getRuleTargets().size());
    assertEquals(2, returnedSellerSeatRule.getRuleIntendedActions().size());
  }

  private void dtoEntityMappingCommonAsserts(SellerSeatRuleDTO dto, SellerSeatRule mapped) {
    assertNull(mapped.getPid());
    assertNull(mapped.getVersion());
    assertTrue(mapped.getDeployedCompanies().isEmpty());
    assertTrue(mapped.getDeployedPositions().isEmpty());
    assertTrue(mapped.getDeployedSites().isEmpty());
    assertNull(mapped.getRuleFormula());
    assertFalse(mapped.getRuleIntendedActions().isEmpty());
    assertFalse(mapped.getRuleTargets().isEmpty());
    assertEquals(dto.getName(), mapped.getName());
    assertEquals(dto.getDescription(), mapped.getDescription());
    assertEquals(dto.getStatus(), mapped.getStatus());
    assertEquals(dto.getType().name(), mapped.getRuleType().name());
    assertEquals(dto.getSellerSeatPid(), mapped.getSellerSeatPid());
  }

  private void dtoEntityApplyCommonAsserts(SellerSeatRule rule, SellerSeatRuleDTO dto) {
    assertEquals(dto.getPid(), rule.getPid());
    assertEquals(dto.getName(), rule.getName());
    assertEquals(dto.getDescription(), rule.getDescription());
    assertEquals(dto.getStatus(), rule.getStatus());
  }
}

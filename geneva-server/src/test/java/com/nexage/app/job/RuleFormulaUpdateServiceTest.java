package com.nexage.app.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleFormula;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.repository.RuleDeployedPositionRepository;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class RuleFormulaUpdateServiceTest {
  private static final long UPDATE_DELAY = 50L; // MS

  @Mock private RuleRepository ruleRepository;
  @Mock private RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository;
  @Mock private PlacementFormulaAssembler placementFormulaAssembler;
  @Mock private RuleDeployedPositionRepository ruleDeployedPositionRepository;

  @InjectMocks private RuleFormulaUpdateService service;

  @BeforeEach
  void setUp() {
    lenient()
        .when(ruleRepository.save(any()))
        .then(
            (Answer<CompanyRule>)
                invocation -> {
                  sleep(UPDATE_DELAY + 10);
                  return null;
                });
  }

  @Test
  void whenFormulaReturnsDifferentPlacements_thenRuleShouldBeUpdated() {
    CompanyRule rule = mockRuleWithPlacementAssignments(8L, Arrays.asList(10L, 11L));
    mockPlacementFormulaReturningPlacements(rule, Arrays.asList(20L, 21L));

    // at this point, we should have two unequal sets of position pids in the method under test

    // method under test
    PlacementFormulaAutoUpdateMetrics jobMetricsData =
        new PlacementFormulaAutoUpdateMetrics(100L, 1);
    service.tryUpdate(8L, jobMetricsData);

    verify(ruleRepository).save(rule);
    assertEquals(1, jobMetricsData.getLoaded());
    assertEquals(1, jobMetricsData.getSearched());
    assertEquals(2, jobMetricsData.getTotalFoundPlacements());
    assertEquals(1, jobMetricsData.getChanged());
    assertEquals(0, jobMetricsData.getNotChanged());
    assertEquals(1, jobMetricsData.getUpdated());
    assertEquals(0, jobMetricsData.getWarnings());
    assertEquals(0, jobMetricsData.getErrors());
    assertTrue(jobMetricsData.getUpdateTime() > UPDATE_DELAY);

    ArgumentCaptor<Set> argumentCaptor = ArgumentCaptor.forClass(Set.class);
    verify(rule).setDeployedPositions(argumentCaptor.capture());
    Set<RuleDeployedPosition> deployedPositions = argumentCaptor.getValue();
    assertEquals(2, deployedPositions.size());
    Set<Long> deployedPositionPids =
        deployedPositions.stream().map(RuleDeployedPosition::getPid).collect(Collectors.toSet());
    assertEquals(new HashSet<>(Arrays.asList(20L, 21L)), deployedPositionPids);
  }

  @Test
  void shouldIncrementErrorMetricWhenCompanyRuleDoesNotExist() {
    when(ruleRepository.findActualByPid(anyLong())).thenReturn(Optional.empty());

    PlacementFormulaAutoUpdateMetrics jobMetricsData =
        new PlacementFormulaAutoUpdateMetrics(100L, 1);
    service.tryUpdate(8L, jobMetricsData);
    assertEquals(1, jobMetricsData.getErrors());
  }

  @Test
  void whenFormulaReturnsSamePlacements_thenRuleShouldNotBeUpdated() {
    CompanyRule rule = mockRuleWithPlacementAssignments(8L, Arrays.asList(10L, 11L));
    mockPlacementFormulaReturningPlacements(rule, Arrays.asList(10L, 11L));

    // at this point, we should have two equal sets of position pids in the method under test

    // method under test
    PlacementFormulaAutoUpdateMetrics jobMetricsData =
        new PlacementFormulaAutoUpdateMetrics(100L, 1);
    service.tryUpdate(8L, jobMetricsData);

    verify(ruleRepository, never()).save(rule);
    assertEquals(1, jobMetricsData.getLoaded());
    assertEquals(1, jobMetricsData.getSearched());
    assertEquals(2, jobMetricsData.getTotalFoundPlacements());
    assertEquals(0, jobMetricsData.getChanged());
    assertEquals(1, jobMetricsData.getNotChanged());
    assertEquals(0, jobMetricsData.getUpdated());
    assertEquals(0, jobMetricsData.getWarnings());
    assertEquals(0, jobMetricsData.getErrors());
    assertEquals(0L, jobMetricsData.getUpdateTime());
  }

  private CompanyRule mockRuleWithPlacementAssignments(Long rulePid, List<Long> positionPids) {
    CompanyRule rule = mock(CompanyRule.class);
    when(ruleRepository.findActualByPid(rulePid)).thenReturn(Optional.ofNullable(rule));

    when(rule.getOwnerCompanyPid()).thenReturn(6L);

    RuleFormula ruleFormula = mock(RuleFormula.class);
    when(rule.getRuleFormula()).thenReturn(ruleFormula);

    Set<RuleDeployedPosition> positions =
        positionPids.stream()
            .map(
                posPid -> {
                  RuleDeployedPosition position = mock(RuleDeployedPosition.class);
                  when(position.getPid()).thenReturn(posPid);
                  return position;
                })
            .collect(Collectors.toSet());
    when(rule.getDeployedPositions()).thenReturn(positions);

    return rule;
  }

  private void mockPlacementFormulaReturningPlacements(CompanyRule rule, List<Long> positionPids) {
    Long companyPid = rule.getOwnerCompanyPid();

    PlacementFormulaDTO placementFormulaDto = mock(PlacementFormulaDTO.class);
    when(placementFormulaAssembler.make(any())).thenReturn(placementFormulaDto);

    Group group = mock(Group.class);
    when(placementFormulaAssembler.apply(placementFormulaDto)).thenReturn(group);

    List<RuleFormulaPositionView> positionViews =
        positionPids.stream()
            .map(
                posPid -> {
                  RuleDeployedPosition position = mock(RuleDeployedPosition.class);
                  lenient().when(position.getPid()).thenReturn(posPid);
                  lenient()
                      .when(ruleDeployedPositionRepository.getOne(posPid))
                      .thenReturn(position);

                  RuleFormulaPositionView positionView = mock(RuleFormulaPositionView.class);
                  when(positionView.getPid()).thenReturn(posPid);
                  return positionView;
                })
            .collect(Collectors.toList());
    when(ruleFormulaPositionViewRepository.findAll(any(Specification.class)))
        .thenReturn(positionViews);
  }

  @Test
  void shouldFindAllToUpdate() {
    // given
    long pid = 1L;

    RuleFormula ruleFormula = new RuleFormula();
    ruleFormula.setPid(2L);
    ruleFormula.setAutoUpdate(true);

    CompanyRule companyRule = TestObjectsFactory.createCompanyRule(pid);
    companyRule.setPid(pid);
    companyRule.setStatus(Status.ACTIVE);
    companyRule.setRuleFormula(ruleFormula);

    List<Long> pids = List.of(1L, 2L, 3L);
    when(ruleRepository.findRulesUpdateableWithNewlyApplicablePlacements()).thenReturn(pids);

    // when
    List<Long> result = service.findAllToUpdate();

    // then
    assertEquals(pids, result);
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

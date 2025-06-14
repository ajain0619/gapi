package com.nexage.app.services.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
class DealServiceSupportTest {
  @Mock private PositionViewRepository positionViewRepository;
  @Mock private DirectDealRepository directDealRepository;
  @Mock private PlacementFormulaAssembler placementFormulaAssembler;

  @InjectMocks private DealServiceSupport dealServiceSupport;

  @BeforeEach
  void setUp() {
    lenient()
        .when(positionViewRepository.findAllById(anySet()))
        .then(
            invocation -> {
              final Set<Long> positionPids = (Set<Long>) invocation.getArguments()[0];
              List<PositionView> positionViews = new ArrayList<>();
              for (Long positionPid : positionPids) {
                PositionView position = new PositionView();
                position.setPid(positionPid);
                // when(position.getPid()).thenReturn(positionPid);
                positionViews.add(position);
              }
              return positionViews;
            });
  }

  @Test
  void shouldReturnEmptySetWhenExistingDealPositionsAreNotEmptyAndNewPositionPidsAreEmpty() {
    // given
    Set<Long> newPositionPids = Collections.emptySet();
    List<Long> existingPositionPids = Arrays.asList(1L, 2L, 3L);
    DirectDeal deal = mockDealWithExistingDealPositions(existingPositionPids);

    // when
    List<DealPosition> newDealPositions =
        dealServiceSupport.convertToCorePositions(deal, deal.getPositions(), newPositionPids);

    // then
    assertTrue(newDealPositions.isEmpty());
    verify(positionViewRepository, times(1)).findAllById(anySet());
  }

  @Test
  void
      shouldReturnAllNewDealPositionsWhenExistingDealPositionsAreEmptyAndNewPositionPidsAreNotEmpty() {
    // given
    Set<Long> newPositionPids = Sets.newHashSet(4L, 5L, 6L);
    List<Long> existingPositionPids = Collections.emptyList();
    DirectDeal deal = mockDealWithExistingDealPositions(existingPositionPids);

    // when
    List<DealPosition> newDealPositions =
        dealServiceSupport.convertToCorePositions(deal, deal.getPositions(), newPositionPids);

    // then
    assertEquals(3, newDealPositions.size());
    assertEquals(
        newPositionPids,
        newDealPositions.stream()
            .map(DealPosition::getPositionView)
            .map(PositionView::getPid)
            .collect(Collectors.toSet()));
    assertTrue(newDealPositions.stream().allMatch(dp -> dp.getPid() == null));
    verify(positionViewRepository, times(1)).findAllById(anySet());
  }

  @Test
  void shouldReturnAllNewDealPositionsWhenExistingDealPositionsAndNewPositionPidsDoNotOverlap() {
    // given
    Set<Long> newPositionPids = Sets.newHashSet(4L, 5L, 6L);
    List<Long> existingPositionPids = Arrays.asList(1L, 2L, 3L);
    DirectDeal deal = mockDealWithExistingDealPositions(existingPositionPids);

    // when
    List<DealPosition> newDealPositions =
        dealServiceSupport.convertToCorePositions(deal, deal.getPositions(), newPositionPids);

    assertEquals(3, newDealPositions.size());
    assertEquals(
        newPositionPids,
        newDealPositions.stream()
            .map(DealPosition::getPositionView)
            .map(PositionView::getPid)
            .collect(Collectors.toSet()));
    assertTrue(newDealPositions.stream().allMatch(dp -> dp.getPid() == null));
    verify(positionViewRepository, times(1)).findAllById(anySet());
  }

  @Test
  void
      shouldReturnAllNewDealPositionsAndOverlappedDealPositionsHavePidsWhenExistingDealPositionsAndNewPositionPidsOverlap() {
    // given
    Set<Long> newPositionPids = Sets.newHashSet(3L, 4L, 5L, 6L);
    List<Long> existingPositionPids = Arrays.asList(1L, 2L, 3L, 4L);
    DirectDeal deal = mockDealWithExistingDealPositions(existingPositionPids);
    Set<Long> expectedDealPositionPids =
        Sets.newHashSet(getDealPostionPidByPositionPid(3L), getDealPostionPidByPositionPid(4L));

    // when
    List<DealPosition> newDealPositions =
        dealServiceSupport.convertToCorePositions(deal, deal.getPositions(), newPositionPids);

    // then
    assertEquals(4, newDealPositions.size());
    assertEquals(
        newPositionPids,
        newDealPositions.stream()
            .map(DealPosition::getPositionView)
            .map(PositionView::getPid)
            .collect(Collectors.toSet()));
    assertEquals(
        Sets.newHashSet(5L, 6L),
        newDealPositions.stream()
            .filter(dp -> dp.getPid() == null)
            .map(DealPosition::getPositionView)
            .map(PositionView::getPid)
            .collect(Collectors.toSet()));
    assertEquals(
        expectedDealPositionPids,
        newDealPositions.stream()
            .map(DealPosition::getPid)
            .filter(pid -> pid != null)
            .collect(Collectors.toSet()));
    verify(positionViewRepository, times(1)).findAllById(anySet());
  }

  @ParameterizedTest
  @MethodSource("providePlacementFormulaStatusUpdateValues")
  void shouldReturnPlacementFormulaStatusWhenPlacementFormulaIsUpdated(
      PlacementFormulaStatus input, PlacementFormulaStatus output) {
    // given
    PlacementFormulaDTO placementFormula = new PlacementFormulaDTO();
    String formula = "formula";
    Long dealPid = 1L;

    given(directDealRepository.findPlacementFormulaStatusByPid(dealPid)).willReturn(input);
    given(placementFormulaAssembler.applyToString(placementFormula)).willReturn(formula + "x");

    // then
    assertEquals(
        output,
        dealServiceSupport.updateDealPlacementFormulaStatus(dealPid, formula, placementFormula));
  }

  @Test
  void shouldReturnSamePlacementFormulaStatusWhenPlacementFormulaIsNotUpdated() {
    // given
    PlacementFormulaDTO placementFormula = new PlacementFormulaDTO();
    String formula = "formula";
    Long dealPid = 1L;

    given(directDealRepository.findPlacementFormulaStatusByPid(dealPid))
        .willReturn(PlacementFormulaStatus.IN_PROGRESS);
    given(placementFormulaAssembler.applyToString(placementFormula)).willReturn(formula);

    // then
    assertEquals(
        PlacementFormulaStatus.IN_PROGRESS,
        dealServiceSupport.updateDealPlacementFormulaStatus(dealPid, formula, placementFormula));
  }

  private DirectDeal mockDealWithExistingDealPositions(List<Long> positionPids) {
    final List<DealPosition> dealPositions =
        positionPids.stream()
            .map(
                positionPid -> {
                  DealPosition dealPosition = mock(DealPosition.class);
                  lenient()
                      .when(dealPosition.getPid())
                      .thenReturn(getDealPostionPidByPositionPid(positionPid));
                  PositionView position = mock(PositionView.class);
                  when(position.getPid()).thenReturn(positionPid);
                  when(dealPosition.getPositionView()).thenReturn(position);
                  return dealPosition;
                })
            .collect(Collectors.toList());

    final DirectDeal deal = mock(DirectDeal.class);
    when(deal.getPositions()).thenReturn(dealPositions);
    return deal;
  }

  private static Long getDealPostionPidByPositionPid(Long positionPid) {
    return 10000L + positionPid;
  }

  private static Stream<Arguments> providePlacementFormulaStatusUpdateValues() {
    return Stream.of(
        Arguments.of(PlacementFormulaStatus.NEW, PlacementFormulaStatus.NEW),
        Arguments.of(PlacementFormulaStatus.UPDATE, PlacementFormulaStatus.UPDATE),
        Arguments.of(PlacementFormulaStatus.IN_QUEUE, PlacementFormulaStatus.IN_QUEUE),
        Arguments.of(PlacementFormulaStatus.IN_PROGRESS, PlacementFormulaStatus.UPDATE),
        Arguments.of(PlacementFormulaStatus.DONE, PlacementFormulaStatus.NEW),
        Arguments.of(PlacementFormulaStatus.ERROR, PlacementFormulaStatus.NEW));
  }
}

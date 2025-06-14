package com.nexage.app.services.support;

import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DealServiceSupport {

  private final PositionViewRepository positionViewRepository;
  private final PlacementFormulaAssembler placementFormulaAssembler;
  private final DirectDealRepository directDealRepository;

  public DealServiceSupport(
      PositionViewRepository positionViewRepository,
      PlacementFormulaAssembler placementFormulaAssembler,
      DirectDealRepository directDealRepository) {
    this.positionViewRepository = positionViewRepository;
    this.placementFormulaAssembler = placementFormulaAssembler;
    this.directDealRepository = directDealRepository;
  }

  public List<DealPosition> convertToCorePositions(
      final DirectDeal deal, final List<DealPosition> corePositions, final Set<Long> positionPids) {
    // Date is updated so that the Deal is reloaded by the cache
    deal.setUpdatedOn(new Date());

    final Set<Long> existingPositionPids = new HashSet<>();
    Iterator<DealPosition> iterator = corePositions.iterator();
    while (iterator.hasNext()) {
      var dealPosition = iterator.next();
      Long positionPid = dealPosition.getPositionView().getPid();
      if (positionPids.contains(positionPid)) {
        existingPositionPids.add(positionPid);
      } else {
        iterator.remove();
      }
    }

    if (positionPids != null) {
      List<PositionView> positionViews = positionViewRepository.findAllById(positionPids);
      for (PositionView positionView : positionViews) {
        if (!existingPositionPids.contains(positionView.getPid())) {
          var dealPosition = new DealPosition();
          dealPosition.setDeal(deal);
          dealPosition.setPositionView(positionView);
          dealPosition.setPositionPid(positionView.getPid());
          corePositions.add(dealPosition);
        }
      }
    }

    return corePositions;
  }

  public PlacementFormulaStatus updateDealPlacementFormulaStatus(
      Long dealPid, String dbPlacementFormula, PlacementFormulaDTO placementFormulaDTO) {
    var placementFormulaStatus = directDealRepository.findPlacementFormulaStatusByPid(dealPid);
    if (Objects.isNull(dbPlacementFormula)
        || !dbPlacementFormula.equals(
            placementFormulaAssembler.applyToString(placementFormulaDTO))) {

      if (placementFormulaStatus == PlacementFormulaStatus.IN_PROGRESS)
        placementFormulaStatus = PlacementFormulaStatus.UPDATE;
      else if (PlacementFormulaStatus.IN_QUEUE != placementFormulaStatus
          && PlacementFormulaStatus.UPDATE != placementFormulaStatus)
        placementFormulaStatus = PlacementFormulaStatus.NEW;
    }
    return placementFormulaStatus;
  }
}

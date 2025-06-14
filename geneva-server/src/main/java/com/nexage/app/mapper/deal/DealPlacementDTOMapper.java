package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.deals.DealPlacementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealPlacementDTOMapper {

  DealPlacementDTOMapper MAPPER = Mappers.getMapper(DealPlacementDTOMapper.class);

  default DealPosition map(
      DealPlacementDTO dealPlacementDTO, DirectDeal deal, PositionView positionView) {
    var dealPosition = new DealPosition();
    dealPosition.setDeal(deal);
    dealPosition.setPid(dealPlacementDTO.getPid());
    dealPosition.setPositionPid(dealPlacementDTO.getPlacementPid());
    dealPosition.setPositionView(positionView);
    return dealPosition;
  }

  default DealPlacementDTO map(DealPosition dealPosition) {
    var placement = new DealPlacementDTO();
    placement.setPid(dealPosition.getPid());
    placement.setPlacementPid(dealPosition.getPositionPid());
    placement.setPlacementName(dealPosition.getPositionView().getName());
    placement.setPlacementMemo(dealPosition.getPositionView().getMemo());
    return placement;
  }
}

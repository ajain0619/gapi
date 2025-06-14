package com.nexage.app.mapper.deal;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.sparta.jpa.model.DealView;
import com.nexage.app.dto.AssignedInventoryType;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.dto.deals.DealDTO.DealDTOBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DealDTOMapper {

  DealDTOMapper MAPPER = Mappers.getMapper(DealDTOMapper.class);

  DealDTO map(DirectDeal directDeal);

  default DealDTO map(DealView source) {
    DealDTOBuilder builder = DealDTO.builder();
    builder
        .pid(source.getPid())
        .dealId(source.getDealId())
        .description(source.getDescription())
        .start(source.getStart())
        .stop(source.getStop())
        .currency(source.getCurrency())
        .status(source.getStatus())
        .creationDate(source.getCreationDate())
        .priorityType(source.getPriorityType())
        .placementFormulaStatus(source.getPlacementFormulaStatus())
        .floor(source.getFloor())
        .visibility(source.getVisibility())
        .updatedOn(source.getUpdatedOn())
        .version(source.getVersion())
        .createdBy(source.getCreatedBy())
        .auctionType(source.getAuctionType())
        .dealCategory(source.getDealCategory());
    if (CollectionUtils.isNotEmpty(source.getRules())) {
      builder.rulePid(source.getRules().get(0).getPid());
    }
    if (StringUtils.isNotEmpty(source.getPlacementFormula())) {
      builder.assignedInventoryType(AssignedInventoryType.FORMULA);
    } else {
      builder.assignedInventoryType(AssignedInventoryType.SPECIFIC);
    }
    return builder.build();
  }
}

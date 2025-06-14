package com.nexage.app.mapper.feeadjustment;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentBuyer;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentDTO;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FeeAdjustmentDTOMapper {

  FeeAdjustmentDTOMapper MAPPER = Mappers.getMapper(FeeAdjustmentDTOMapper.class);

  default FeeAdjustmentDTO map(FeeAdjustment feeAdjustment, boolean isBasicView) {
    FeeAdjustmentDTO.FeeAdjustmentDTOBuilder builder =
        FeeAdjustmentDTO.builder()
            .pid(feeAdjustment.getPid())
            .name(feeAdjustment.getName())
            .inclusive(feeAdjustment.getInclusive())
            .demandFeeAdjustment(feeAdjustment.getDemandFeeAdjustment())
            .version(feeAdjustment.getVersion())
            .enabled(feeAdjustment.getEnabled())
            .description(feeAdjustment.getDescription());

    if (isBasicView) {
      builder =
          builder.entityName(
              feeAdjustment.getFeeAdjustmentBuyers().stream()
                  .map(FeeAdjustmentBuyer::getBuyer)
                  .map(FeeAdjustmentCompanyView::getName)
                  .sorted()
                  .collect(Collectors.joining(", ")));
    } else {
      builder =
          builder
              .feeAdjustmentSellers(
                  feeAdjustment.getFeeAdjustmentSellers().stream()
                      .map(FeeAdjustmentSellerDTOMapper.MAPPER::map)
                      .collect(Collectors.toList()))
              .feeAdjustmentBuyers(
                  feeAdjustment.getFeeAdjustmentBuyers().stream()
                      .map(FeeAdjustmentBuyerDTOMapper.MAPPER::map)
                      .collect(Collectors.toList()));
    }

    return builder.build();
  }

  default FeeAdjustment map(
      FeeAdjustmentDTO feeAdjustmentDTO,
      FeeAdjustment feeAdjustment,
      FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepository) {

    feeAdjustment.setPid(feeAdjustmentDTO.getPid());
    feeAdjustment.setName(feeAdjustmentDTO.getName());
    feeAdjustment.setInclusive(feeAdjustmentDTO.getInclusive());
    feeAdjustment.setDemandFeeAdjustment(feeAdjustmentDTO.getDemandFeeAdjustment());
    feeAdjustment.setVersion(feeAdjustmentDTO.getVersion());
    feeAdjustment.setEnabled(feeAdjustmentDTO.getEnabled());
    feeAdjustment.setDescription(feeAdjustmentDTO.getDescription());

    feeAdjustment.setFeeAdjustmentSellers(
        FeeAdjustmentSellerDTOMapper.MAPPER.map(
            feeAdjustmentDTO.getFeeAdjustmentSellers(),
            feeAdjustment,
            feeAdjustmentCompanyViewRepository));
    feeAdjustment.setFeeAdjustmentBuyers(
        FeeAdjustmentBuyerDTOMapper.MAPPER.map(
            feeAdjustmentDTO.getFeeAdjustmentBuyers(),
            feeAdjustment,
            feeAdjustmentCompanyViewRepository));

    return feeAdjustment;
  }
}

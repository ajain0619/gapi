package com.nexage.app.mapper.feeadjustment;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentBuyer;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentBuyerDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FeeAdjustmentBuyerDTOMapper {

  FeeAdjustmentBuyerDTOMapper MAPPER = Mappers.getMapper(FeeAdjustmentBuyerDTOMapper.class);

  default FeeAdjustmentBuyerDTO map(FeeAdjustmentBuyer feeAdjustmentBuyer) {
    return FeeAdjustmentBuyerDTO.builder()
        .buyerPid(feeAdjustmentBuyer.getBuyer().getPid())
        .buyerName(feeAdjustmentBuyer.getBuyer().getName())
        .build();
  }

  default Collection<FeeAdjustmentBuyer> map(
      List<FeeAdjustmentBuyerDTO> feeAdjustmentBuyerDTOs,
      FeeAdjustment feeAdjustment,
      FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepository) {

    Map<Long, FeeAdjustmentBuyer> buyerPidToExistingFeeAdjustmentBuyerMap =
        feeAdjustment.getFeeAdjustmentBuyers().stream()
            .collect(Collectors.toMap(x -> x.getBuyer().getPid(), Function.identity()));

    return feeAdjustmentBuyerDTOs.stream()
        .map(
            feeAdjustmentBuyerDTO -> {
              FeeAdjustmentBuyer feeAdjustmentBuyer =
                  buyerPidToExistingFeeAdjustmentBuyerMap.getOrDefault(
                      feeAdjustmentBuyerDTO.getBuyerPid(), new FeeAdjustmentBuyer());

              feeAdjustmentBuyer.setFeeAdjustment(feeAdjustment);

              FeeAdjustmentCompanyView feeAdjustmentCompanyView =
                  FeeAdjustmentCompanyViewMapper.MAPPER.map(
                      feeAdjustmentBuyerDTO.getBuyerPid(), feeAdjustmentCompanyViewRepository);

              feeAdjustmentBuyer.setBuyer(feeAdjustmentCompanyView);

              if (feeAdjustmentCompanyView.getType() != CompanyType.BUYER) {
                throw new GenevaValidationException(
                    ServerErrorCodes.SERVER_FEE_ADJUSTMENT_COMPANY_NOT_A_BUYER);
              }

              buyerPidToExistingFeeAdjustmentBuyerMap.put(
                  feeAdjustmentBuyerDTO.getBuyerPid(), feeAdjustmentBuyer);

              return feeAdjustmentBuyer;
            })
        .collect(Collectors.toList());
  }
}

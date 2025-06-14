package com.nexage.app.mapper.feeadjustment;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustment;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentSeller;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.app.dto.feeadjustment.FeeAdjustmentSellerDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FeeAdjustmentSellerDTOMapper {

  FeeAdjustmentSellerDTOMapper MAPPER = Mappers.getMapper(FeeAdjustmentSellerDTOMapper.class);

  default FeeAdjustmentSellerDTO map(FeeAdjustmentSeller feeAdjustmentSeller) {
    return FeeAdjustmentSellerDTO.builder()
        .sellerPid(feeAdjustmentSeller.getSeller().getPid())
        .sellerName(feeAdjustmentSeller.getSeller().getName())
        .build();
  }

  default Collection<FeeAdjustmentSeller> map(
      Collection<FeeAdjustmentSellerDTO> feeAdjustmentSellerDTOs,
      FeeAdjustment feeAdjustment,
      FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepository) {

    Map<Long, FeeAdjustmentSeller> sellerPidToExistingFeeAdjustmentSellerMap =
        feeAdjustment.getFeeAdjustmentSellers().stream()
            .collect(Collectors.toMap(x -> x.getSeller().getPid(), Function.identity()));

    return feeAdjustmentSellerDTOs.stream()
        .map(
            feeAdjustmentSellerDTO -> {
              FeeAdjustmentSeller feeAdjustmentSeller =
                  sellerPidToExistingFeeAdjustmentSellerMap.getOrDefault(
                      feeAdjustmentSellerDTO.getSellerPid(), new FeeAdjustmentSeller());

              feeAdjustmentSeller.setFeeAdjustment(feeAdjustment);

              FeeAdjustmentCompanyView feeAdjustmentCompanyView =
                  FeeAdjustmentCompanyViewMapper.MAPPER.map(
                      feeAdjustmentSellerDTO.getSellerPid(), feeAdjustmentCompanyViewRepository);

              if (feeAdjustmentCompanyView.getType() != CompanyType.SELLER) {
                throw new GenevaValidationException(
                    ServerErrorCodes.SERVER_FEE_ADJUSTMENT_COMPANY_NOT_A_SELLER);
              }

              feeAdjustmentSeller.setSeller(feeAdjustmentCompanyView);

              sellerPidToExistingFeeAdjustmentSellerMap.put(
                  feeAdjustmentSellerDTO.getSellerPid(), feeAdjustmentSeller);

              return feeAdjustmentSeller;
            })
        .collect(Collectors.toList());
  }
}

package com.nexage.app.mapper.feeadjustment;

import com.nexage.admin.core.model.feeadjustment.FeeAdjustmentCompanyView;
import com.nexage.admin.core.repository.FeeAdjustmentCompanyViewRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FeeAdjustmentCompanyViewMapper {

  FeeAdjustmentCompanyViewMapper MAPPER = Mappers.getMapper(FeeAdjustmentCompanyViewMapper.class);

  default FeeAdjustmentCompanyView map(
      Long companyPid, FeeAdjustmentCompanyViewRepository feeAdjustmentCompanyViewRepository) {
    return feeAdjustmentCompanyViewRepository
        .findById(companyPid)
        .orElseThrow(
            () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));
  }
}

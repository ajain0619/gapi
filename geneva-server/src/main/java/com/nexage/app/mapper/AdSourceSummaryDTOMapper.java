package com.nexage.app.mapper;

import static com.nexage.admin.core.util.XssSanitizerUtil.sanitize;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.model.AdSource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdSourceSummaryDTOMapper {

  AdSourceSummaryDTOMapper MAPPER = Mappers.getMapper(AdSourceSummaryDTOMapper.class);

  default AdSourceSummaryDTO map(AdSource adSource) {
    return new AdSourceSummaryDTO(
        adSource.getId(),
        adSource.getPid(),
        adSource.getName(),
        adSource.getDescription(),
        adSource.getStatus(),
        adSource.getParamMap(),
        adSource.getSelfServeEnablement(),
        adSource.getParamDefault(),
        adSource.getParamRequired(),
        adSource.getReportAuthType(),
        adSource.getParamMetadata(),
        adSource.getBidEnabled(),
        adSource.getDecisionMakerEnabled(),
        adSource.getLogo());
  }

  default AdSourceSummaryDTO mapForGeneva(AdSource adSource) {
    return AdSourceSummaryDTO.createAdSourceSummaryForGeneva(
        sanitize(adSource.getId()),
        adSource.getPid(),
        sanitize(adSource.getName()),
        sanitize(adSource.getDescription()),
        adSource.getStatus(),
        sanitize(adSource.getParamMap()),
        adSource.getSelfServeEnablement(),
        sanitize(adSource.getParamDefault()),
        sanitize(adSource.getParamRequired()),
        adSource.getReportAuthType(),
        adSource.getParamMetadata(),
        adSource.getBidEnabled(),
        adSource.getDecisionMakerEnabled(),
        sanitize(adSource.getLogo()));
  }
}

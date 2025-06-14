package com.nexage.app.util.assemblers;

import com.nexage.admin.core.model.HbPartner;
import com.nexage.app.dto.HbPartnerDTO;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class HbPartnerAssembler extends NoContextAssembler {

  private static final String FORMATTED_DEFAULT_TYPE_ENABLED = "formattedDefaultTypeEnabled";
  private static final String MULTI_IMPRESSION_BID = "multiImpressionBid";
  private static final String FILL_MAX_DURATION = "fillMaxDuration";
  private static final String MAX_ADS_PER_POD = "maxAdsPerPod";
  public static final Set<String> DEFAULT_FIELDS =
      Set.of(
          "pid",
          "id",
          "name",
          "partnerHandler",
          "status",
          "version",
          "description",
          "feeType",
          "fee",
          "responseConfig",
          FORMATTED_DEFAULT_TYPE_ENABLED,
          MULTI_IMPRESSION_BID,
          FILL_MAX_DURATION,
          MAX_ADS_PER_POD);

  public static final Set<String> SUMMARY_FIELDS =
      Set.of(
          "pid",
          "name",
          FORMATTED_DEFAULT_TYPE_ENABLED,
          MULTI_IMPRESSION_BID,
          FILL_MAX_DURATION,
          MAX_ADS_PER_POD);

  public HbPartnerDTO make(HbPartner entity) {
    return make(entity, DEFAULT_FIELDS);
  }

  public HbPartnerDTO make(HbPartner entity, Set<String> fields) {
    HbPartnerDTO.HbPartnerDTOBuilder builder = HbPartnerDTO.builder();

    for (String field : (fields != null) ? fields : DEFAULT_FIELDS) {
      switch (field) {
        case "pid":
          builder.pid(entity.getPid());
          break;
        case "id":
          builder.id(entity.getId());
          break;
        case "name":
          builder.name(entity.getName());
          break;
        case "partnerHandler":
          builder.partnerHandler(entity.getPartnerHandler());
          break;
        case "status":
          builder.status(entity.getStatus());
          break;
        case "version":
          builder.version(entity.getVersion());
          break;
        case "description":
          builder.description(entity.getDescription());
          break;
        case "feeType":
          builder.feeType(entity.getFeeType());
          break;
        case "fee":
          builder.fee(entity.getFee());
          break;
        case "responseConfig":
          builder.responseConfig(entity.getResponseConfig());
          break;
        case FORMATTED_DEFAULT_TYPE_ENABLED:
          builder.formattedDefaultTypeEnabled(entity.isFormattedDefaultTypeEnabled());
          break;
        case MULTI_IMPRESSION_BID:
          builder.multiImpressionBid(entity.isMultiImpressionBid());
          break;
        case FILL_MAX_DURATION:
          builder.fillMaxDuration(entity.isFillMaxDuration());
          break;
        case MAX_ADS_PER_POD:
          builder.maxAdsPerPod(entity.getMaxAdsPerPod());
          break;
        default:
      }
    }
    return builder.build();
  }

  public HbPartner apply(HbPartner entity, HbPartnerDTO dto) {
    entity.setPid(dto.getPid());
    entity.setId(dto.getId());
    entity.setName(dto.getName());
    entity.setStatus(dto.getStatus());
    entity.setVersion(dto.getVersion());
    entity.setDescription(dto.getDescription());
    entity.setFeeType(dto.getFeeType());
    entity.setFee(dto.getFee());
    entity.setPartnerHandler(dto.getPartnerHandler());
    entity.setResponseConfig(dto.getResponseConfig());
    entity.setFormattedDefaultTypeEnabled(dto.isFormattedDefaultTypeEnabled());
    entity.setMultiImpressionBid(dto.isMultiImpressionBid());
    entity.setFillMaxDuration(dto.isFillMaxDuration());
    entity.setMaxAdsPerPod(dto.getMaxAdsPerPod());
    return entity;
  }
}

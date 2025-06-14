package com.nexage.app.mapper;

import static org.apache.logging.log4j.core.util.Assert.isEmpty;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.app.dto.seller.SellerAttributesDTO;
import com.nexage.app.dto.seller.SellerAttributesDTO.SellerAttributesDTOBuilder;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencyMode;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerAttributesDTOMapper {

  SellerAttributesDTOMapper MAPPER = Mappers.getMapper(SellerAttributesDTOMapper.class);

  default SellerAttributesDTO map(SellerAttributes entity) {
    if (isEmpty(entity)) return new SellerAttributesDTO();

    SellerAttributesDTOBuilder builder = SellerAttributesDTO.builder();
    builder
        .sellerPid(entity.getSellerPid())
        .version(entity.getVersion())
        .humanOptOut(entity.getHumanOptOut())
        .smartQPSEnabled(entity.getSmartQPSEnabled())
        .defaultTransparencyMgmtEnablement(
            TransparencyMgmtEnablement.getById(entity.getTransparencyMgmtEnablement()))
        .transparencyMode(TransparencyMode.fromInt(entity.getIncludePubName()))
        .sellerNameAlias(entity.getPubNameAlias())
        .sellerIdAlias(entity.getPubAliasId())
        .revenueShare(entity.getRevenueShare())
        .rtbFee(entity.getRtbFee())
        .adStrictApproval(entity.getAdStrictApproval())
        .revenueGroupPid(entity.getRevenueGroupPid())
        .humanPrebidSampleRate(entity.getHumanPrebidSampleRate())
        .humanPostbidSampleRate(entity.getHumanPostbidSampleRate())
        .customDealFloorEnabled(entity.isCustomDealFloorEnabled());
    return builder.build();
  }
}

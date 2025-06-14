package com.nexage.app.mapper;

import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.app.dto.publisher.PublisherEligibleBiddersDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PublisherEligibleBiddersDTOMapper {

  PublisherEligibleBiddersDTOMapper MAPPER =
      Mappers.getMapper(PublisherEligibleBiddersDTOMapper.class);

  default PublisherEligibleBiddersDTO map(SellerEligibleBidders source) {
    if (source == null) {
      return null;
    }
    PublisherEligibleBiddersDTO.Builder builder = PublisherEligibleBiddersDTO.newBuilder();
    builder.withPid(source.getPid());
    builder.withVersion(source.getVersion());
    builder.withBidders(source.getEligibleBidderGroups());
    return builder.build();
  }
}

package com.nexage.app.mapper;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileBidder;
import com.nexage.app.dto.publisher.PublisherRTBProfileBidderDTO;
import java.util.Set;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PublisherRTBProfileBidderDTOMapper {

  PublisherRTBProfileBidderDTOMapper MAPPER =
      Mappers.getMapper(PublisherRTBProfileBidderDTOMapper.class);

  Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
  Joiner joiner = Joiner.on(",").skipNulls();

  @Mapping(target = "seatWhitelist", ignore = true)
  PublisherRTBProfileBidderDTO map(RTBProfileBidder rtbProfileBidder);

  default RTBProfileBidder map(
      RTBProfileBidder rtbProfileBidder,
      PublisherRTBProfileBidderDTO publisherRTBProfileBidderDTO) {
    if (rtbProfileBidder == null || publisherRTBProfileBidderDTO == null) {
      throw new IllegalArgumentException(
          "rtbProfileBidder and publisherRTBProfileBidderDTO cannot be null");
    }
    rtbProfileBidder.setVersion(publisherRTBProfileBidderDTO.getVersion());
    if (publisherRTBProfileBidderDTO.getSeatWhitelist() != null) {
      rtbProfileBidder.setSeatWhitelist(
          joiner.join(publisherRTBProfileBidderDTO.getSeatWhitelist()));
    }
    return rtbProfileBidder;
  }

  @AfterMapping
  default void mapSeatList(
      RTBProfileBidder rtbProfileBidder,
      @MappingTarget PublisherRTBProfileBidderDTO publisherRTBProfileBidderDTO) {
    String seatList = rtbProfileBidder.getSeatWhitelist();
    if (seatList != null) {
      Set<String> mappedSeatList = Sets.newHashSet(splitter.split(seatList));
      publisherRTBProfileBidderDTO.setSeatWhitelist(mappedSeatList);
      publisherRTBProfileBidderDTO.setSeatAllowlist(mappedSeatList);
    }
  }
}

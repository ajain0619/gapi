package com.nexage.app.mapper.postauctiondiscount;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostAuctionDiscountDspSeatDTOMapper {

  PostAuctionDiscountDspSeatDTOMapper MAPPER =
      Mappers.getMapper(PostAuctionDiscountDspSeatDTOMapper.class);

  @Mapping(target = "pid", source = "dsp.pid")
  @Mapping(target = "name", source = "dsp.name")
  PostAuctionDiscountDspSeatDTO map(PostAuctionDiscountDspSeat dspSeat);
}

package com.nexage.app.mapper.postauctiondiscount;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostAuctionDiscountTypeDTOMapper {

  PostAuctionDiscountTypeDTOMapper MAPPER =
      Mappers.getMapper(PostAuctionDiscountTypeDTOMapper.class);

  PostAuctionDiscountTypeDTO map(PostAuctionDiscountType padType);

  PostAuctionDiscountType map(PostAuctionDiscountTypeDTO padTypeDto);
}

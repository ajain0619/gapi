package com.nexage.app.mapper.postauctiondiscount;

import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountSeller;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = PostAuctionDiscountTypeDTOMapper.class)
public interface PostAuctionDiscountSellerDTOMapper {

  PostAuctionDiscountSellerDTOMapper MAPPER =
      Mappers.getMapper(PostAuctionDiscountSellerDTOMapper.class);

  @Mapping(target = "pid", source = "seller.pid")
  @Mapping(target = "name", source = "seller.name")
  @Mapping(target = "revenueGroupPid", source = "seller.sellerAttributes.revenueGroupPid")
  PostAuctionDiscountSellerDTO map(PostAuctionDiscountSeller seller);

  default List<PostAuctionDiscountSeller> map(
      List<PostAuctionDiscountSellerDTO> sellers,
      PostAuctionDiscount discount,
      EntityManager entityManager) {

    return sellers.stream()
        .map(sellerDto -> getSellerFromDiscountOrCreateFromDto(sellerDto, discount, entityManager))
        .toList();
  }

  private PostAuctionDiscountSeller getSellerFromDiscountOrCreateFromDto(
      PostAuctionDiscountSellerDTO sellerDto,
      PostAuctionDiscount discount,
      EntityManager entityManager) {
    PostAuctionDiscountSeller seller =
        discount.getSellers().stream()
            .filter(
                existingSeller -> sellerDto.getPid().equals(existingSeller.getSeller().getPid()))
            .findAny()
            .orElseGet(() -> createSellerFromDto(sellerDto, discount, entityManager));
    seller.setType(mapType(sellerDto.getType()));
    return seller;
  }

  private PostAuctionDiscountSeller createSellerFromDto(
      PostAuctionDiscountSellerDTO sellerDto,
      PostAuctionDiscount discount,
      EntityManager entityManager) {
    var seller = new PostAuctionDiscountSeller();
    seller.setPostAuctionDiscount(discount);
    seller.setSeller(entityManager.getReference(CompanyView.class, sellerDto.getPid()));
    return seller;
  }

  private PostAuctionDiscountType mapType(PostAuctionDiscountTypeDTO typeDTO) {
    return PostAuctionDiscountTypeDTOMapper.MAPPER.map(typeDTO);
  }
}

package com.nexage.app.mapper.postauctiondiscount;

import com.nexage.admin.core.model.RevenueGroup;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountRevenueGroup;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountRevenueGroupDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = PostAuctionDiscountTypeDTOMapper.class)
public interface PostAuctionDiscountRevenueGroupDTOMapper {

  PostAuctionDiscountRevenueGroupDTOMapper MAPPER =
      Mappers.getMapper(PostAuctionDiscountRevenueGroupDTOMapper.class);

  @Mapping(target = "pid", source = "revenueGroup.pid")
  PostAuctionDiscountRevenueGroupDTO map(PostAuctionDiscountRevenueGroup padRevenueGroup);

  default List<PostAuctionDiscountRevenueGroup> map(
      List<PostAuctionDiscountRevenueGroupDTO> revGroupDtos,
      PostAuctionDiscount discount,
      EntityManager entityManager) {

    return revGroupDtos.stream()
        .map(
            revGroupDto ->
                getRevGroupFromDiscountOrCreateFromDto(revGroupDto, discount, entityManager))
        .toList();
  }

  private PostAuctionDiscountRevenueGroup getRevGroupFromDiscountOrCreateFromDto(
      PostAuctionDiscountRevenueGroupDTO revenueGroupDTO,
      PostAuctionDiscount discount,
      EntityManager entityManager) {
    PostAuctionDiscountRevenueGroup revenueGroup =
        discount.getRevenueGroups().stream()
            .filter(
                existingRevGroup ->
                    revenueGroupDTO.getPid().equals(existingRevGroup.getRevenueGroup().getPid()))
            .findAny()
            .orElseGet(() -> createRevGroupFromDto(revenueGroupDTO, discount, entityManager));
    revenueGroup.setType(mapType(revenueGroupDTO.getType()));
    return revenueGroup;
  }

  private PostAuctionDiscountRevenueGroup createRevGroupFromDto(
      PostAuctionDiscountRevenueGroupDTO dto,
      PostAuctionDiscount discount,
      EntityManager entityManager) {
    var padRevenueGroup = new PostAuctionDiscountRevenueGroup();
    padRevenueGroup.setPostAuctionDiscount(discount);
    RevenueGroup revenueGroup = entityManager.getReference(RevenueGroup.class, dto.getPid());
    padRevenueGroup.setRevenueGroup(revenueGroup);
    return padRevenueGroup;
  }

  private PostAuctionDiscountType mapType(PostAuctionDiscountTypeDTO typeDTO) {
    return PostAuctionDiscountTypeDTOMapper.MAPPER.map(typeDTO);
  }
}

package com.nexage.app.mapper.postauctiondiscount;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeatView;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountIntermediaryDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    uses = PostAuctionDiscountDspSeatDTOMapper.class)
public interface PostAuctionDiscountDspDTOMapper {

  PostAuctionDiscountDspDTOMapper MAPPER = Mappers.getMapper(PostAuctionDiscountDspDTOMapper.class);

  PostAuctionDiscountDspDTO map(PostAuctionDiscountIntermediaryDTO dto);

  default List<PostAuctionDiscountDspSeat> map(
      List<PostAuctionDiscountDspDTO> dsps,
      PostAuctionDiscount discount,
      EntityManager entityManager) {

    return dsps.stream()
        .flatMap(dspDto -> dspDto.getDspSeats().stream())
        .map(
            dspSeatDto ->
                getDspSeatFromDiscountOrCreateFromDto(dspSeatDto, discount, entityManager))
        .toList();
  }

  private PostAuctionDiscountDspSeat getDspSeatFromDiscountOrCreateFromDto(
      PostAuctionDiscountDspSeatDTO dspSeatDto,
      PostAuctionDiscount discount,
      EntityManager entityManager) {
    return discount.getDsps().stream()
        .filter(dspSeat -> dspSeatDto.getPid().equals(dspSeat.getDsp().getPid()))
        .findAny()
        .orElseGet(() -> createDspSeatFromDto(dspSeatDto, discount, entityManager));
  }

  private PostAuctionDiscountDspSeat createDspSeatFromDto(
      PostAuctionDiscountDspSeatDTO dspSeatDto,
      PostAuctionDiscount discount,
      EntityManager entityManager) {
    var postAuctionDiscountDSPSeat = new PostAuctionDiscountDspSeat();
    postAuctionDiscountDSPSeat.setPostAuctionDiscount(discount);
    postAuctionDiscountDSPSeat.setDsp(
        entityManager.getReference(PostAuctionDiscountDspSeatView.class, dspSeatDto.getPid()));
    return postAuctionDiscountDSPSeat;
  }
}

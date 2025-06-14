package com.nexage.app.mapper.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.model.DirectDealView;
import com.nexage.admin.core.model.postauctiondiscount.DealPostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DirectDealViewDTOMapperTest {

  @Test
  void shouldMapToDto() {
    DirectDealView inputDeal = new DirectDealView(1L, "Test Deal 1", "ex1");
    DealPostAuctionDiscount input = new DealPostAuctionDiscount();
    input.setPid(2L);
    input.setDeal(inputDeal);
    input.setVersion(2);

    DirectDealViewDTO expected = new DirectDealViewDTO("ex1", 1L, "Test Deal 1");

    assertEquals(expected, DealPostAuctionDiscountDTOMapper.MAPPER.map(input));
  }

  @Test
  void shouldMapDtoToModelWhenAddingDeal() {
    List<DirectDealViewDTO> dealDTOList =
        List.of(new DirectDealViewDTO("1", 1L, "DES 1"), new DirectDealViewDTO("2", 2L, "DES 2"));

    PostAuctionDiscount discount =
        new PostAuctionDiscount(
            183L,
            "test discount",
            "test description",
            10.0,
            true,
            false,
            PostAuctionDealsSelected.ALL,
            1,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    Collection<DealPostAuctionDiscount> deals =
        List.of(
            new DealPostAuctionDiscount(
                5L,
                discount,
                new DirectDealView(1L, "Deal 1", "ex1"),
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1));

    discount.setDeals(deals);

    List<DealPostAuctionDiscount> expected =
        List.of(
            new DealPostAuctionDiscount(
                5L,
                discount,
                new DirectDealView(1L, "Deal 1", "ex1"),
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1),
            new DealPostAuctionDiscount(
                null, discount, new DirectDealView(2L, "Deal 2", "ex1"), null, null, 1));

    assertEquals(expected, DealPostAuctionDiscountDTOMapper.MAPPER.map(dealDTOList, discount));
  }

  @Test
  void shouldTestMapDtoToModelWhenRemovingDeal() {
    List<DirectDealViewDTO> dealDTOList = List.of(new DirectDealViewDTO("ex1", 2L, "Deal 2"));

    PostAuctionDiscount discount =
        new PostAuctionDiscount(
            183L,
            "test discount",
            "test description",
            10.0,
            true,
            false,
            PostAuctionDealsSelected.ALL,
            1,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    List<DealPostAuctionDiscount> deals =
        List.of(
            new DealPostAuctionDiscount(
                5L,
                discount,
                new DirectDealView(1L, "Deal 1", "ex1"),
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1),
            new DealPostAuctionDiscount(
                6L,
                discount,
                new DirectDealView(2L, "Deal 2", "ex1"),
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1));

    discount.setDeals(deals);

    Collection<DealPostAuctionDiscount> expected =
        List.of(
            new DealPostAuctionDiscount(
                6L,
                discount,
                new DirectDealView(2L, "Deal 2", "ex1"),
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1));

    assertEquals(expected, DealPostAuctionDiscountDTOMapper.MAPPER.map(dealDTOList, discount));
  }
}

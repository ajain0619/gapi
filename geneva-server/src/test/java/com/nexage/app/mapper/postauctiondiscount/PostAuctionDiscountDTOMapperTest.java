package com.nexage.app.mapper.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.DirectDealView;
import com.nexage.admin.core.model.postauctiondiscount.DealPostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeatView;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountSeller;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostAuctionDiscountDTOMapperTest {

  private static PostAuctionDiscountTypeDTO TYPE_DTO = new PostAuctionDiscountTypeDTO(1L, "pad v1");
  private static PostAuctionDiscountType TYPE =
      new PostAuctionDiscountType(1L, "pad v1", null, null);

  @Mock private EntityManager entityManager;

  @Test
  void shouldMapModelToDtoWithGetAll() {
    PostAuctionDiscount inputModel =
        new PostAuctionDiscount(
            1L,
            "Test Name",
            "Test Description",
            5.5,
            true,
            true,
            PostAuctionDealsSelected.ALL,
            1,
            null,
            null,
            null,
            null,
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    // Combining everything
    PostAuctionDiscountDTO expectedOutput =
        new PostAuctionDiscountDTO(
            1L,
            "Test Name",
            true,
            5.5,
            "Test Description",
            true,
            1,
            List.of(),
            List.of(),
            List.of(),
            PostAuctionDealsSelected.ALL,
            List.of());
    assertEquals(expectedOutput, PostAuctionDiscountDTOMapper.MAPPER.map(inputModel));
  }

  @Test
  void shouldMapModelToDto() {
    PostAuctionDiscountDspSeat dspSeat1 = new PostAuctionDiscountDspSeat();
    dspSeat1.setPid(1L);
    dspSeat1.setVersion(1);
    dspSeat1.setDsp(
        new PostAuctionDiscountDspSeatView(
            2L,
            "test buyer seat 1",
            new CompanyView(3L, "DSP Company 1", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat2 = new PostAuctionDiscountDspSeat();
    dspSeat2.setPid(2L);
    dspSeat2.setVersion(2);
    dspSeat2.setDsp(
        new PostAuctionDiscountDspSeatView(
            3L,
            "test buyer seat 2",
            new CompanyView(4L, "DSP Company 2", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat3 = new PostAuctionDiscountDspSeat();
    dspSeat3.setPid(4L);
    dspSeat3.setVersion(4);
    dspSeat3.setDsp(
        new PostAuctionDiscountDspSeatView(
            4L,
            "test buyer seat 3",
            new CompanyView(3L, "DSP Company 1", CompanyType.BUYER, false)));

    PostAuctionDiscountSeller seller1 = new PostAuctionDiscountSeller();
    seller1.setPid(1L);
    seller1.setVersion(2);
    seller1.setSeller(new CompanyView(2L, "test company 1", CompanyType.SELLER, true));
    seller1.setType(TYPE);
    PostAuctionDiscountSeller seller2 = new PostAuctionDiscountSeller();
    seller2.setPid(2L);
    seller2.setVersion(3);
    seller2.setSeller(new CompanyView(3L, "test company 2", CompanyType.SELLER, false));
    seller2.setType(TYPE);

    PostAuctionDiscount input =
        new PostAuctionDiscount(
            1L,
            "test discount",
            "test description",
            10.0,
            true,
            false,
            PostAuctionDealsSelected.NONE,
            2,
            Arrays.asList(seller1, seller2),
            new ArrayList<>(),
            Arrays.asList(dspSeat1, dspSeat2, dspSeat3),
            new ArrayList<>(),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    PostAuctionDiscountDTO expected =
        new PostAuctionDiscountDTO(
            1L,
            "test discount",
            true,
            10.0,
            "test description",
            false,
            2,
            Arrays.asList(
                new PostAuctionDiscountDspDTO(
                    3L,
                    "DSP Company 1",
                    Arrays.asList(
                        new PostAuctionDiscountDspSeatDTO(2L, "test buyer seat 1"),
                        new PostAuctionDiscountDspSeatDTO(4L, "test buyer seat 3"))),
                new PostAuctionDiscountDspDTO(
                    4L,
                    "DSP Company 2",
                    Arrays.asList(new PostAuctionDiscountDspSeatDTO(3L, "test buyer seat 2")))),
            Arrays.asList(
                new PostAuctionDiscountSellerDTO(2L, "test company 1", TYPE_DTO, null),
                new PostAuctionDiscountSellerDTO(3L, "test company 2", TYPE_DTO, null)),
            new ArrayList<>(),
            PostAuctionDealsSelected.NONE,
            new ArrayList<>());

    assertEquals(expected, PostAuctionDiscountDTOMapper.MAPPER.map(input));
  }

  @Test
  void shouldMapDtoToModel() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            1L,
            "test discount",
            true,
            10.0,
            "test description",
            false,
            3,
            Arrays.asList(
                new PostAuctionDiscountDspDTO(
                    3L,
                    "Test Company 3",
                    Arrays.asList(
                        new PostAuctionDiscountDspSeatDTO(2L, "test dsp seat 1"),
                        new PostAuctionDiscountDspSeatDTO(4L, "test dsp seat 3"))),
                new PostAuctionDiscountDspDTO(
                    4L,
                    "Test Company 4",
                    Arrays.asList(new PostAuctionDiscountDspSeatDTO(3L, "test dsp seat 2")))),
            Arrays.asList(
                new PostAuctionDiscountSellerDTO(2L, "test company 1", TYPE_DTO, null),
                new PostAuctionDiscountSellerDTO(3L, "test company 2", TYPE_DTO, null)),
            new ArrayList<>(),
            PostAuctionDealsSelected.SPECIFIC,
            Arrays.asList(
                new DirectDealViewDTO("ex2", 2L, "test deal 1"),
                new DirectDealViewDTO("ex3", 3L, "test deal 2")));

    PostAuctionDiscount expected =
        new PostAuctionDiscount(
            1L,
            "test discount",
            "test description",
            10.0,
            true,
            true,
            PostAuctionDealsSelected.SPECIFIC,
            3,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeat dspSeat1 = new PostAuctionDiscountDspSeat();
    dspSeat1.setPostAuctionDiscount(expected);
    dspSeat1.setDsp(
        new PostAuctionDiscountDspSeatView(
            2L, "test dsp seat 1", new CompanyView(3L, "Test 3", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat2 = new PostAuctionDiscountDspSeat();
    dspSeat2.setPostAuctionDiscount(expected);
    dspSeat2.setDsp(
        new PostAuctionDiscountDspSeatView(
            3L, "test dsp seat 2", new CompanyView(4L, "Test 4", CompanyType.BUYER, false)));

    PostAuctionDiscountDspSeat dspSeat3 = new PostAuctionDiscountDspSeat();
    dspSeat3.setPostAuctionDiscount(expected);
    dspSeat3.setDsp(
        new PostAuctionDiscountDspSeatView(
            4L, "test dsp seat 3", new CompanyView(3L, "Test 3", CompanyType.BUYER, true)));
    Collection<PostAuctionDiscountDspSeat> dspSeats = Arrays.asList(dspSeat1, dspSeat2, dspSeat3);

    PostAuctionDiscountSeller seller1 = new PostAuctionDiscountSeller();
    seller1.setPostAuctionDiscount(expected);
    seller1.setSeller(new CompanyView(2L, "test company 1", CompanyType.SELLER, true));
    PostAuctionDiscountSeller seller2 = new PostAuctionDiscountSeller();
    seller2.setPostAuctionDiscount(expected);
    seller2.setSeller(new CompanyView(3L, "test company 2", CompanyType.SELLER, false));
    Collection<PostAuctionDiscountSeller> sellers = Arrays.asList(seller1, seller2);

    DealPostAuctionDiscount deal1 = new DealPostAuctionDiscount();
    deal1.setPostAuctionDiscount(expected);
    deal1.setDeal(new DirectDealView(2L, "test deal 1", "ex1"));
    DealPostAuctionDiscount deal2 = new DealPostAuctionDiscount();
    deal2.setPostAuctionDiscount(expected);
    deal2.setDeal(new DirectDealView(3L, "test deal 2", "ex2"));
    Collection<DealPostAuctionDiscount> deals = Arrays.asList(deal1, deal2);

    expected.setDsps(dspSeats);
    expected.setSellers(sellers);
    expected.setDeals(deals);

    assertEquals(expected, PostAuctionDiscountDTOMapper.MAPPER.map(input, expected, entityManager));
  }

  @Test
  void shouldMapDtoToModelWhereRepositoryShouldBeCalled() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            1L,
            "test discount",
            true,
            10.0,
            "test description",
            false,
            3,
            Arrays.asList(
                new PostAuctionDiscountDspDTO(
                    3L,
                    "Test Company 3",
                    Arrays.asList(
                        new PostAuctionDiscountDspSeatDTO(2L, "test dsp seat 1"),
                        new PostAuctionDiscountDspSeatDTO(5L, "test dsp seat 5"))),
                new PostAuctionDiscountDspDTO(
                    4L,
                    "Test Company 4",
                    Arrays.asList(new PostAuctionDiscountDspSeatDTO(3L, "test dsp seat 2")))),
            Arrays.asList(
                new PostAuctionDiscountSellerDTO(2L, "test company 1", TYPE_DTO, null),
                new PostAuctionDiscountSellerDTO(8L, "test company 8", TYPE_DTO, null)),
            new ArrayList<>(),
            PostAuctionDealsSelected.SPECIFIC,
            Arrays.asList(
                new DirectDealViewDTO("ex1", 2L, "test deal 1"),
                new DirectDealViewDTO("ex2", 8L, "test deal 8")));

    PostAuctionDiscount originalDiscount =
        new PostAuctionDiscount(
            1L,
            "test discount",
            "test description",
            10.0,
            true,
            false,
            PostAuctionDealsSelected.SPECIFIC,
            3,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeat dspSeat1 = new PostAuctionDiscountDspSeat();
    dspSeat1.setPid(10L);
    dspSeat1.setPostAuctionDiscount(originalDiscount);
    dspSeat1.setDsp(
        new PostAuctionDiscountDspSeatView(
            2L, "test dsp seat 1", new CompanyView(3L, "Test 3", CompanyType.BUYER, true)));
    dspSeat1.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat1.setCreationDate(Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeat dspSeat2 = new PostAuctionDiscountDspSeat();
    dspSeat2.setPid(11L);
    dspSeat2.setPostAuctionDiscount(originalDiscount);
    dspSeat2.setDsp(
        new PostAuctionDiscountDspSeatView(
            3L, "test dsp seat 2", new CompanyView(4L, "Test 4", CompanyType.BUYER, false)));
    dspSeat2.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat2.setCreationDate(Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeat dspSeat3 = new PostAuctionDiscountDspSeat();
    dspSeat3.setPid(12L);
    dspSeat3.setPostAuctionDiscount(originalDiscount);
    dspSeat3.setDsp(
        new PostAuctionDiscountDspSeatView(
            4L, "test dsp seat 3", new CompanyView(3L, "Test 3", CompanyType.BUYER, true)));
    dspSeat3.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat3.setCreationDate(Date.from(Instant.EPOCH));

    PostAuctionDiscountSeller seller1 = new PostAuctionDiscountSeller();
    seller1.setPid(91L);
    seller1.setPostAuctionDiscount(originalDiscount);
    seller1.setSeller(new CompanyView(2L, "test company 1", CompanyType.SELLER, true));
    seller1.setLastUpdate(Date.from(Instant.EPOCH));
    seller1.setCreationDate(Date.from(Instant.EPOCH));

    PostAuctionDiscountSeller seller2 = new PostAuctionDiscountSeller();
    seller2.setPid(81L);
    seller2.setPostAuctionDiscount(originalDiscount);
    seller2.setSeller(new CompanyView(3L, "test company 2", CompanyType.SELLER, false));
    seller2.setLastUpdate(Date.from(Instant.EPOCH));
    seller2.setCreationDate(Date.from(Instant.EPOCH));

    DealPostAuctionDiscount deal1 = new DealPostAuctionDiscount();
    deal1.setPid(91L);
    deal1.setPostAuctionDiscount(originalDiscount);
    deal1.setDeal(new DirectDealView(2L, "test deal 1", "ex1"));
    deal1.setUpdatedOn(Date.from(Instant.EPOCH));
    deal1.setCreatedOn(Date.from(Instant.EPOCH));

    DealPostAuctionDiscount deal2 = new DealPostAuctionDiscount();
    deal2.setPid(81L);
    deal2.setPostAuctionDiscount(originalDiscount);
    deal2.setDeal(new DirectDealView(3L, "test deal 2", "ex2"));
    deal2.setUpdatedOn(Date.from(Instant.EPOCH));
    deal2.setCreatedOn(Date.from(Instant.EPOCH));

    originalDiscount.setDsps(Arrays.asList(dspSeat1, dspSeat2, dspSeat3));
    originalDiscount.setSellers(Arrays.asList(seller1, seller2));
    originalDiscount.setDeals(Arrays.asList(deal1, deal2));

    PostAuctionDiscount expected =
        new PostAuctionDiscount(
            1L,
            "test discount",
            "test description",
            10.0,
            true,
            true,
            PostAuctionDealsSelected.SPECIFIC,
            3,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeat dspSeat4 = new PostAuctionDiscountDspSeat();
    dspSeat4.setPid(10L);
    dspSeat4.setPostAuctionDiscount(expected);
    dspSeat4.setDsp(
        new PostAuctionDiscountDspSeatView(
            2L, "test dsp seat 1", new CompanyView(3L, "Test 3", CompanyType.BUYER, true)));
    dspSeat4.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat4.setCreationDate(Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeat dspSeat5 = new PostAuctionDiscountDspSeat();
    dspSeat5.setPostAuctionDiscount(expected);
    dspSeat5.setDsp(
        new PostAuctionDiscountDspSeatView(
            8L, "test dsp seat 8", new CompanyView(4L, "Test 4", CompanyType.BUYER, false)));

    PostAuctionDiscountDspSeat dspSeat6 = new PostAuctionDiscountDspSeat();
    dspSeat6.setPid(12L);
    dspSeat6.setPostAuctionDiscount(expected);
    dspSeat6.setDsp(
        new PostAuctionDiscountDspSeatView(
            4L, "test dsp seat 3", new CompanyView(3L, "Test 3", CompanyType.BUYER, true)));
    dspSeat6.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat6.setCreationDate(Date.from(Instant.EPOCH));

    PostAuctionDiscountSeller seller3 = new PostAuctionDiscountSeller();
    seller3.setPid(91L);
    seller3.setPostAuctionDiscount(expected);
    seller3.setSeller(new CompanyView(2L, "test company 1", CompanyType.SELLER, true));
    seller3.setLastUpdate(Date.from(Instant.EPOCH));
    seller3.setCreationDate(Date.from(Instant.EPOCH));

    PostAuctionDiscountSeller seller4 = new PostAuctionDiscountSeller();
    seller4.setPostAuctionDiscount(expected);
    seller4.setSeller(new CompanyView(8L, "test company 8", CompanyType.SELLER, false));

    DealPostAuctionDiscount deal3 = new DealPostAuctionDiscount();
    deal3.setPid(91L);
    deal3.setPostAuctionDiscount(expected);
    deal3.setDeal(new DirectDealView(2L, "test deal 1", "ex3"));
    deal3.setUpdatedOn(Date.from(Instant.EPOCH));
    deal3.setCreatedOn(Date.from(Instant.EPOCH));

    DealPostAuctionDiscount deal4 = new DealPostAuctionDiscount();
    deal4.setPostAuctionDiscount(expected);
    deal4.setDeal(new DirectDealView(8L, "test deal 8", "ex8"));

    expected.setDsps(Arrays.asList(dspSeat4, dspSeat5, dspSeat6));
    expected.setSellers(Arrays.asList(seller3, seller4));
    expected.setDeals(Arrays.asList(deal3, deal4));

    assertEquals(
        expected, PostAuctionDiscountDTOMapper.MAPPER.map(input, originalDiscount, entityManager));
  }
}

package com.nexage.app.mapper.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeatView;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountIntermediaryDTO;
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
class PostAuctionDiscountDspDTOMapperTest {

  @Mock private EntityManager entityManager;

  @Test
  void shouldMapModelToDTO() {
    PostAuctionDiscountDspSeat dspSeat1 = new PostAuctionDiscountDspSeat();
    dspSeat1.setPid(1L);
    dspSeat1.setVersion(1);
    dspSeat1.setDsp(
        new PostAuctionDiscountDspSeatView(
            2L,
            "test buyer seat 1",
            new CompanyView(3L, "Test Company 3", CompanyType.BUYER, false)));
    PostAuctionDiscountDspSeat dspSeat2 = new PostAuctionDiscountDspSeat();
    dspSeat2.setPid(2L);
    dspSeat2.setVersion(1);
    dspSeat2.setDsp(
        new PostAuctionDiscountDspSeatView(
            4L,
            "test buyer seat 2",
            new CompanyView(3L, "Test Company 3", CompanyType.BUYER, true)));

    List<PostAuctionDiscountDspSeat> input = List.of(dspSeat1, dspSeat2);

    PostAuctionDiscountDspDTO expected =
        new PostAuctionDiscountDspDTO(
            3L,
            "Test Company 3",
            Arrays.asList(
                new PostAuctionDiscountDspSeatDTO(2L, "test buyer seat 1"),
                new PostAuctionDiscountDspSeatDTO(4L, "test buyer seat 2")));

    assertEquals(
        expected,
        PostAuctionDiscountDspDTOMapper.MAPPER.map(new PostAuctionDiscountIntermediaryDTO(input)));
  }

  @Test
  void shouldMapDtoToModelAddingPostAuctionDiscountDSPSeat() {

    List<PostAuctionDiscountDspDTO> dspDTOList =
        Arrays.asList(
            new PostAuctionDiscountDspDTO(
                1L,
                "Test Company 1",
                Arrays.asList(
                    new PostAuctionDiscountDspSeatDTO(18L, "dsp seat 1"),
                    new PostAuctionDiscountDspSeatDTO(29L, "dsp seat 2"))),
            new PostAuctionDiscountDspDTO(
                2L,
                "Test Company 2",
                Arrays.asList(
                    new PostAuctionDiscountDspSeatDTO(31L, "dsp seat 3"),
                    new PostAuctionDiscountDspSeatDTO(42L, "dsp seat 4"))));

    PostAuctionDiscount discount =
        new PostAuctionDiscount(
            1L,
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

    PostAuctionDiscountDspSeat dspSeat1 = new PostAuctionDiscountDspSeat();
    dspSeat1.setPid(1L);
    dspSeat1.setVersion(1);
    dspSeat1.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat1.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat1.setPostAuctionDiscount(discount);
    dspSeat1.setDsp(
        new PostAuctionDiscountDspSeatView(
            18L, "dsp seat 1", new CompanyView(1L, "Test Company 1", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat2 = new PostAuctionDiscountDspSeat();
    dspSeat2.setPid(2L);
    dspSeat2.setVersion(1);
    dspSeat2.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat2.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat2.setPostAuctionDiscount(discount);
    dspSeat2.setDsp(
        new PostAuctionDiscountDspSeatView(
            29L, "dsp seat 2", new CompanyView(1L, "Test Company 1", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat3 = new PostAuctionDiscountDspSeat();
    dspSeat3.setPid(3L);
    dspSeat3.setVersion(1);
    dspSeat3.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat3.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat3.setPostAuctionDiscount(discount);
    dspSeat3.setDsp(
        new PostAuctionDiscountDspSeatView(
            31L, "dsp seat 3", new CompanyView(2L, "Test Company 2", CompanyType.BUYER, true)));

    discount.setDsps(Arrays.asList(dspSeat1, dspSeat2, dspSeat3));

    Collection<PostAuctionDiscountDspSeat> expected =
        Arrays.asList(
            new PostAuctionDiscountDspSeat(
                1L,
                discount,
                new PostAuctionDiscountDspSeatView(
                    18L,
                    "dsp seat 1",
                    new CompanyView(1L, "Test Company 1", CompanyType.BUYER, false)),
                1,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH)),
            new PostAuctionDiscountDspSeat(
                2L,
                discount,
                new PostAuctionDiscountDspSeatView(
                    29L,
                    "dsp seat 2",
                    new CompanyView(1L, "Test Company 1", CompanyType.BUYER, false)),
                1,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH)),
            new PostAuctionDiscountDspSeat(
                3L,
                discount,
                new PostAuctionDiscountDspSeatView(
                    31L,
                    "dsp seat 3",
                    new CompanyView(2L, "Test Company 2", CompanyType.BUYER, true)),
                1,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH)),
            new PostAuctionDiscountDspSeat(
                null,
                discount,
                new PostAuctionDiscountDspSeatView(
                    42L,
                    "dsp seat 4",
                    new CompanyView(2L, "Test Company 2", CompanyType.BUYER, true)),
                null,
                null,
                null));

    assertEquals(
        expected, PostAuctionDiscountDspDTOMapper.MAPPER.map(dspDTOList, discount, entityManager));
  }

  @Test
  void shouldMapDtoToModelRemovingPostAuctionDiscountDSPSeat() {
    List<PostAuctionDiscountDspDTO> dspDTOList =
        Arrays.asList(
            new PostAuctionDiscountDspDTO(
                1L,
                "Test Company 1",
                Arrays.asList(
                    new PostAuctionDiscountDspSeatDTO(51L, "dsp seat 1"),
                    new PostAuctionDiscountDspSeatDTO(122L, "dsp seat 2"))));

    PostAuctionDiscount discount =
        new PostAuctionDiscount(
            1L,
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

    PostAuctionDiscountDspSeat dspSeat1 = new PostAuctionDiscountDspSeat();
    dspSeat1.setPid(1L);
    dspSeat1.setVersion(1);
    dspSeat1.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat1.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat1.setPostAuctionDiscount(discount);
    dspSeat1.setDsp(
        new PostAuctionDiscountDspSeatView(
            51L, "dsp seat 1", new CompanyView(1L, "Test Company 1", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat2 = new PostAuctionDiscountDspSeat();
    dspSeat2.setPid(2L);
    dspSeat2.setVersion(1);
    dspSeat2.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat2.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat2.setPostAuctionDiscount(discount);
    dspSeat2.setDsp(
        new PostAuctionDiscountDspSeatView(
            122L, "dsp seat 2", new CompanyView(1L, "Test Company 1", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat3 = new PostAuctionDiscountDspSeat();
    dspSeat3.setPid(3L);
    dspSeat3.setVersion(1);
    dspSeat3.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat3.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat3.setPostAuctionDiscount(discount);
    dspSeat3.setDsp(
        new PostAuctionDiscountDspSeatView(
            3L, "dsp seat 3", new CompanyView(201L, "Test Company 2", CompanyType.BUYER, true)));

    discount.setDsps(Arrays.asList(dspSeat1, dspSeat2, dspSeat3));

    Collection<PostAuctionDiscountDspSeat> expected =
        Arrays.asList(
            new PostAuctionDiscountDspSeat(
                1L,
                discount,
                new PostAuctionDiscountDspSeatView(
                    1L,
                    "dsp seat 1",
                    new CompanyView(51L, "Test Company 1", CompanyType.BUYER, false)),
                1,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH)),
            new PostAuctionDiscountDspSeat(
                2L,
                discount,
                new PostAuctionDiscountDspSeatView(
                    122L,
                    "dsp seat 2",
                    new CompanyView(1L, "Test Company 1", CompanyType.BUYER, false)),
                1,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH)));

    assertEquals(
        expected, PostAuctionDiscountDspDTOMapper.MAPPER.map(dspDTOList, discount, entityManager));
  }
}

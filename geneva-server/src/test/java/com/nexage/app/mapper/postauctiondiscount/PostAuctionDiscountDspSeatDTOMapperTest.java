package com.nexage.app.mapper.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeatView;
import com.nexage.admin.core.repository.PostAuctionDiscountDspSeatViewRepository;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostAuctionDiscountDspSeatDTOMapperTest {

  @Mock PostAuctionDiscountDspSeatViewRepository mockRepository;

  @Test
  void shouldMapModelToDTO() {
    PostAuctionDiscountDspSeat input =
        new PostAuctionDiscountDspSeat(
            613L,
            null,
            new PostAuctionDiscountDspSeatView(
                1L, "test name", new CompanyView(2L, "Company 2", CompanyType.BUYER, false)),
            1,
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeatDTO expected = new PostAuctionDiscountDspSeatDTO(1L, "test name");

    assertEquals(expected, PostAuctionDiscountDspSeatDTOMapper.MAPPER.map(input));
  }
}

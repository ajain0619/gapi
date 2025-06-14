package com.nexage.app.mapper.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.SellerAttributesView;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountSeller;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostAuctionDiscountSellerDTOMapperTest {

  private static PostAuctionDiscountTypeDTO DISCOUNT_TYPE_DTO =
      new PostAuctionDiscountTypeDTO(1L, "pad v1");
  private static PostAuctionDiscountType DISCOUNT_TYPE =
      new PostAuctionDiscountType(1L, "pad v1", null, null);
  @Mock private EntityManager entityManager;

  @Test
  void shouldMapModelToDTO() {
    CompanyView inputPublisher =
        new CompanyView(
            1L, "test company", CompanyType.SELLER, false, new SellerAttributesView(1L, 2L));
    PostAuctionDiscountSeller input = new PostAuctionDiscountSeller();
    input.setPid(2L);
    input.setSeller(inputPublisher);
    input.setVersion(2);
    input.setType(DISCOUNT_TYPE);

    PostAuctionDiscountSellerDTO expected =
        new PostAuctionDiscountSellerDTO(1L, "test company", DISCOUNT_TYPE_DTO, 2L);

    assertEquals(expected, PostAuctionDiscountSellerDTOMapper.MAPPER.map(input));
  }

  @Test
  void shouldMapDtoToModelWhenAddingSeller() {
    List<PostAuctionDiscountSellerDTO> sellerDTOList =
        List.of(
            new PostAuctionDiscountSellerDTO(1L, "Company 1", DISCOUNT_TYPE_DTO, 3L),
            new PostAuctionDiscountSellerDTO(2L, "Company 2", DISCOUNT_TYPE_DTO, 3L));

    PostAuctionDiscount discount =
        new PostAuctionDiscount(
            183L,
            "test discount",
            "test description",
            10.0,
            true,
            false,
            PostAuctionDealsSelected.NONE,
            1,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            null,
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    Collection<PostAuctionDiscountSeller> sellers =
        List.of(
            new PostAuctionDiscountSeller(
                5L,
                discount,
                new CompanyView(
                    1L, "Company 1", CompanyType.SELLER, true, new SellerAttributesView(1L, 3L)),
                DISCOUNT_TYPE,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1));

    discount.setSellers(sellers);

    List<PostAuctionDiscountSeller> expected =
        List.of(
            new PostAuctionDiscountSeller(
                5L,
                discount,
                new CompanyView(
                    1L, "Company 1", CompanyType.SELLER, false, new SellerAttributesView(1L, 3L)),
                DISCOUNT_TYPE,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1),
            new PostAuctionDiscountSeller(
                null,
                discount,
                new CompanyView(
                    2L, "Company 2", CompanyType.SELLER, true, new SellerAttributesView(2L, 3L)),
                DISCOUNT_TYPE,
                null,
                null,
                1));

    assertEquals(
        expected,
        PostAuctionDiscountSellerDTOMapper.MAPPER.map(sellerDTOList, discount, entityManager));
  }

  @Test
  void shouldMapDtoToModelWhenRemovingSeller() {
    List<PostAuctionDiscountSellerDTO> sellerDTOList =
        List.of(new PostAuctionDiscountSellerDTO(2L, "Company 2", DISCOUNT_TYPE_DTO, 3L));

    PostAuctionDiscount discount =
        new PostAuctionDiscount(
            183L,
            "test discount",
            "test description",
            10.0,
            true,
            false,
            PostAuctionDealsSelected.NONE,
            1,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            null,
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    List<PostAuctionDiscountSeller> sellers =
        List.of(
            new PostAuctionDiscountSeller(
                5L,
                discount,
                new CompanyView(
                    1L, "Company 1", CompanyType.SELLER, true, new SellerAttributesView(1L, 3L)),
                DISCOUNT_TYPE,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1),
            new PostAuctionDiscountSeller(
                6L,
                discount,
                new CompanyView(
                    2L, "Company 2", CompanyType.SELLER, true, new SellerAttributesView(2L, 3L)),
                DISCOUNT_TYPE,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1));

    discount.setSellers(sellers);

    Collection<PostAuctionDiscountSeller> expected =
        List.of(
            new PostAuctionDiscountSeller(
                6L,
                discount,
                new CompanyView(
                    2L, "Company 2", CompanyType.SELLER, false, new SellerAttributesView(2L, 3L)),
                DISCOUNT_TYPE,
                Date.from(Instant.EPOCH),
                Date.from(Instant.EPOCH),
                1));

    assertEquals(
        expected,
        PostAuctionDiscountSellerDTOMapper.MAPPER.map(sellerDTOList, discount, entityManager));
  }
}

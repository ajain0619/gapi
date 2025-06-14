package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.DirectDealView;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.postauctiondiscount.DealPostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeatView;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountSeller;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount_;
import com.nexage.admin.core.repository.PostAuctionDiscountRepository;
import com.nexage.admin.core.repository.PostAuctionDiscountTypeRepository;
import com.nexage.admin.core.repository.RevenueGroupRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.specification.CustomSearchSpecification;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountRevenueGroupDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PostAuctionDiscountServiceImplTest {

  private static final PostAuctionDiscountTypeDTO DISCOUNT_TYPE_DTO_1 =
      new PostAuctionDiscountTypeDTO(1L, "pad v1");
  private static final PostAuctionDiscountType DISCOUNT_TYPE_1 =
      new PostAuctionDiscountType(1L, "pad v1", null, null);
  private static final PostAuctionDiscountTypeDTO DISCOUNT_TYPE_DTO_2 =
      new PostAuctionDiscountTypeDTO(2L, "pad v2");

  @Mock private PostAuctionDiscountRepository discountRepository;
  @Mock private RevenueGroupRepository revenueGroupRepository;
  @Mock private SellerAttributesRepository sellerAttributesRepository;
  @Mock private PostAuctionDiscountTypeRepository postAuctionDiscountTypeRepository;
  @Mock private EntityManager entityManager;
  @InjectMocks private PostAuctionDiscountServiceImpl postAuctionDiscountService;

  @Test
  void shouldGetAll() {
    Page<PostAuctionDiscount> postAuctionDiscountPage =
        new PageImpl<>(
            List.of(
                new PostAuctionDiscount(
                    1L,
                    "Test Name",
                    "Test Description",
                    25.0,
                    true,
                    false,
                    PostAuctionDealsSelected.ALL,
                    1,
                    null,
                    null,
                    null,
                    null,
                    Date.from(Instant.EPOCH),
                    Date.from(Instant.EPOCH)),
                new PostAuctionDiscount(
                    2L,
                    "Test Name 2",
                    "Test Description",
                    5.5,
                    true,
                    false,
                    PostAuctionDealsSelected.NONE,
                    1,
                    null,
                    null,
                    null,
                    null,
                    Date.from(Instant.EPOCH),
                    Date.from(Instant.EPOCH))));

    Page<PostAuctionDiscountDTO> expectedResult =
        new PageImpl<>(
            List.of(
                new PostAuctionDiscountDTO(
                    1L,
                    "Test Name",
                    true,
                    25.0,
                    "Test Description",
                    false,
                    1,
                    List.of(),
                    List.of(),
                    List.of(),
                    PostAuctionDealsSelected.ALL,
                    List.of()),
                new PostAuctionDiscountDTO(
                    2L,
                    "Test Name 2",
                    true,
                    5.5,
                    "Test Description",
                    false,
                    1,
                    List.of(),
                    List.of(),
                    List.of(),
                    PostAuctionDealsSelected.NONE,
                    List.of())));

    Specification<PostAuctionDiscount> specification =
        new CustomSearchSpecification.Builder<PostAuctionDiscount>()
            .with("discountName", "Name")
            .build()
            .and(
                (root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(PostAuctionDiscount_.DISCOUNT_STATUS), true));

    when(discountRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(postAuctionDiscountPage);

    assertEquals(
        expectedResult.get().collect(Collectors.toSet()),
        postAuctionDiscountService
            .getAll(Set.of("discountName"), "Name", true, Pageable.unpaged())
            .get()
            .collect(Collectors.toSet()));
  }

  @Test
  void shouldGetObjectById() {
    PostAuctionDiscountDspSeat buyer1 = new PostAuctionDiscountDspSeat();
    buyer1.setPid(1L);
    buyer1.setVersion(1);
    buyer1.setDsp(
        new PostAuctionDiscountDspSeatView(
            1L,
            "test buyer seat 1",
            new CompanyView(109L, "Real Company 109", CompanyType.BUYER, true)));
    PostAuctionDiscountDspSeat buyer2 = new PostAuctionDiscountDspSeat();
    buyer2.setPid(2L);
    buyer2.setVersion(1);
    buyer2.setDsp(
        new PostAuctionDiscountDspSeatView(
            2L,
            "test buyer seat 2",
            new CompanyView(109L, "Real Company 109", CompanyType.BUYER, false)));
    PostAuctionDiscountDspSeat buyer3 = new PostAuctionDiscountDspSeat();
    buyer3.setPid(3L);
    buyer3.setVersion(1);
    buyer3.setDsp(
        new PostAuctionDiscountDspSeatView(
            3L,
            "test buyer seat 3",
            new CompanyView(38L, "Test Company 12", CompanyType.BUYER, false)));

    PostAuctionDiscountSeller publisher1 = new PostAuctionDiscountSeller();
    publisher1.setPid(1L);
    publisher1.setVersion(1);
    publisher1.setSeller(new CompanyView(1L, "test company 1", CompanyType.SELLER, false));
    publisher1.setType(new PostAuctionDiscountType(1L, "pad v1", null, null));
    PostAuctionDiscountSeller publisher2 = new PostAuctionDiscountSeller();
    publisher2.setPid(2L);
    publisher2.setVersion(1);
    publisher2.setSeller(new CompanyView(2L, "test company 2", CompanyType.SELLER, true));
    publisher2.setType(new PostAuctionDiscountType(1L, "pad v1", null, null));

    DealPostAuctionDiscount deal1 = new DealPostAuctionDiscount();
    deal1.setPid(1L);
    deal1.setVersion(1);
    deal1.setDeal(new DirectDealView(1L, "test deal 1", "ex1"));
    DealPostAuctionDiscount deal2 = new DealPostAuctionDiscount();
    deal2.setPid(2L);
    deal2.setVersion(1);
    deal2.setDeal(new DirectDealView(2L, "test deal 2", "ex2"));

    PostAuctionDiscount foundDiscount =
        new PostAuctionDiscount(
            1L,
            "Regular Discount",
            "This is a description",
            16.7,
            false,
            false,
            PostAuctionDealsSelected.SPECIFIC,
            2,
            List.of(publisher1, publisher2),
            List.of(),
            List.of(buyer1, buyer2, buyer3),
            List.of(deal1, deal2),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    PostAuctionDiscountDTO expected =
        new PostAuctionDiscountDTO(
            1L,
            "Regular Discount",
            false,
            16.7,
            "This is a description",
            false,
            2,
            List.of(
                new PostAuctionDiscountDspDTO(
                    38L,
                    "Test Company 12",
                    List.of(new PostAuctionDiscountDspSeatDTO(3L, "test buyer seat 3"))),
                new PostAuctionDiscountDspDTO(
                    109L,
                    "Real Company 109",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(1L, "test buyer seat 1"),
                        new PostAuctionDiscountDspSeatDTO(2L, "test buyer seat 2")))),
            List.of(
                new PostAuctionDiscountSellerDTO(1L, "test company 1", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(2L, "test company 2", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(
                new DirectDealViewDTO("ex1", 1L, "test deal 1"),
                new DirectDealViewDTO("ex2", 2L, "test deal 2")));

    when(discountRepository.findById(1L)).thenReturn(Optional.of(foundDiscount));

    assertEquals(expected, postAuctionDiscountService.get(1L));
  }

  @Test
  void shouldThrowOnGetByIdWhenNotFound() {
    when(discountRepository.findById(9999L)).thenReturn(Optional.empty());

    var notFoundException =
        assertThrows(GenevaValidationException.class, () -> postAuctionDiscountService.get(9999L));

    assertEquals(
        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_NOT_FOUND, notFoundException.getErrorCode());
  }

  @Test
  void shouldCreatePostAuctionDiscount() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            187L,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            false,
            0,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L, null, List.of(new PostAuctionDiscountDspSeatDTO(54L, null)))),
            List.of(new PostAuctionDiscountSellerDTO(67L, null, DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(new DirectDealViewDTO("ex2", 2L, "test deal")));

    PostAuctionDiscountDTO expected =
        new PostAuctionDiscountDTO(
            187L,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            false,
            0,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L,
                    "Company Test Buyer",
                    List.of(new PostAuctionDiscountDspSeatDTO(54L, "Test Create Buyer Seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    67L, "Test Create Publisher", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(new DirectDealViewDTO("ex2", 2L, "test deal")));

    when(discountRepository.saveAndFlush(any(PostAuctionDiscount.class))).then(returnsFirstArg());
    when(postAuctionDiscountTypeRepository.countByPidIn(anyIterable())).thenReturn(1L);
    when(entityManager.getReference(PostAuctionDiscountDspSeatView.class, 54L))
        .thenReturn(
            new PostAuctionDiscountDspSeatView(
                54L, "Test Create Buyer Seat", new CompanyView(1887L, "Company Test Buyer")));
    when(entityManager.getReference(CompanyView.class, 67L))
        .thenReturn(new CompanyView(67L, "Test Create Publisher"));

    PostAuctionDiscountDTO result = postAuctionDiscountService.create(input);

    assertEquals(expected, result);
  }

  @Test
  void shouldThrowOnCreateWhenThereIsMatchingDiscountWithAllDealsSelected() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            null,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            false,
            null,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L,
                    "Company Test 2",
                    List.of(new PostAuctionDiscountDspSeatDTO(54L, "Test Create Buyer Seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    67L, "Test Create Publisher", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.ALL,
            null);

    PostAuctionDiscount foundDiscount = new PostAuctionDiscount();
    foundDiscount.setPid(127L);
    foundDiscount.setOpenAuctionEnabled(false);
    foundDiscount.setDealsSelected(PostAuctionDealsSelected.ALL);

    when(postAuctionDiscountTypeRepository.countByPidIn(Set.of(DISCOUNT_TYPE_DTO_1.getPid())))
        .thenReturn(1L);
    when(discountRepository.findByDspSellerPairIncludingRevenueGroups(
            List.of(54L), List.of(67L), null))
        .thenReturn(List.of(foundDiscount));

    GenevaValidationException genevaValidationException =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              postAuctionDiscountService.create(input);
            });

    assertEquals(
        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_ALREADY_EXISTS,
        genevaValidationException.getErrorCode());
  }

  @Test
  void shouldThrowOnCreateWhenThereIsMatchingDiscountWithSameSpecificDeal() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            null,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            false,
            null,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L,
                    "Company Test 2",
                    List.of(new PostAuctionDiscountDspSeatDTO(54L, "Test Create Buyer Seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    67L, "Test Create Publisher", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(DirectDealViewDTO.builder().pid(1l).build()));

    PostAuctionDiscount foundDiscount = new PostAuctionDiscount();
    foundDiscount.setPid(127L);
    foundDiscount.setOpenAuctionEnabled(false);
    foundDiscount.setDealsSelected(PostAuctionDealsSelected.SPECIFIC);
    foundDiscount.setDeals(
        List.of(
            new DealPostAuctionDiscount(
                1l, null, new DirectDealView(1L, "test", "1"), null, null, 1)));

    when(postAuctionDiscountTypeRepository.countByPidIn(Set.of(DISCOUNT_TYPE_DTO_1.getPid())))
        .thenReturn(1L);
    when(discountRepository.findByDspSellerPairIncludingRevenueGroups(
            List.of(54L), List.of(67L), null))
        .thenReturn(List.of(foundDiscount));

    GenevaValidationException genevaValidationException =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              postAuctionDiscountService.create(input);
            });

    assertEquals(
        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_ALREADY_EXISTS,
        genevaValidationException.getErrorCode());
  }

  @Test
  void shouldThrowOnCreateSpecificDealDiscountWhenThereIsMatchingAllDealsDiscount() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            null,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            false,
            null,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L,
                    "Company Test 2",
                    List.of(new PostAuctionDiscountDspSeatDTO(54L, "Test Create Buyer Seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    67L, "Test Create Publisher", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(DirectDealViewDTO.builder().pid(1l).build()));

    PostAuctionDiscount foundDiscount = new PostAuctionDiscount();
    foundDiscount.setPid(127L);
    foundDiscount.setOpenAuctionEnabled(false);
    foundDiscount.setDealsSelected(PostAuctionDealsSelected.ALL);

    when(postAuctionDiscountTypeRepository.countByPidIn(Set.of(DISCOUNT_TYPE_DTO_1.getPid())))
        .thenReturn(1L);
    when(discountRepository.findByDspSellerPairIncludingRevenueGroups(
            List.of(54L), List.of(67L), null))
        .thenReturn(List.of(foundDiscount));

    GenevaValidationException genevaValidationException =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              postAuctionDiscountService.create(input);
            });

    assertEquals(
        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_ALREADY_EXISTS,
        genevaValidationException.getErrorCode());
  }

  @Test
  void shouldThrowOnCreateWhenThereIsMatchingDiscountWithOpenAuction() {
    // given
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            null,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            true,
            null,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L,
                    "Company Test 2",
                    List.of(new PostAuctionDiscountDspSeatDTO(54L, "Test Create Buyer Seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    67L, "Test Create Publisher", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(DirectDealViewDTO.builder().pid(1l).build()));

    PostAuctionDiscount foundDiscount = new PostAuctionDiscount();
    foundDiscount.setPid(123L);
    foundDiscount.setOpenAuctionEnabled(true);
    foundDiscount.setDealsSelected(PostAuctionDealsSelected.NONE);

    when(postAuctionDiscountTypeRepository.countByPidIn(Set.of(DISCOUNT_TYPE_DTO_1.getPid())))
        .thenReturn(1L);
    when(discountRepository.findByDspSellerPairIncludingRevenueGroups(
            List.of(54L), List.of(67L), null))
        .thenReturn(List.of(foundDiscount));

    // when
    GenevaValidationException genevaValidationException =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              postAuctionDiscountService.create(input);
            });

    // then
    assertEquals(
        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_ALREADY_EXISTS,
        genevaValidationException.getErrorCode());
  }

  @Test
  void shouldUpdatePostAuctionDiscount() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            3L,
            "Update-Test",
            true,
            3.33,
            "test description",
            true,
            3,
            List.of(
                new PostAuctionDiscountDspDTO(
                    3L,
                    "Test Company Name",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(4L, "DSP-1"),
                        new PostAuctionDiscountDspSeatDTO(5L, "DSP-2")))),
            List.of(
                new PostAuctionDiscountSellerDTO(6L, "Seller-6", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(7L, "Seller-7", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.NONE,
            new ArrayList<>());

    PostAuctionDiscountDTO expected =
        new PostAuctionDiscountDTO(
            3L,
            "Update-Test",
            true,
            3.33,
            "test description",
            true,
            3,
            List.of(
                new PostAuctionDiscountDspDTO(
                    3L,
                    "Test Company Name",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(4L, "DSP-1"),
                        new PostAuctionDiscountDspSeatDTO(5L, "DSP-2")))),
            List.of(
                new PostAuctionDiscountSellerDTO(6L, "Seller-6", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(7L, "Seller-7", DISCOUNT_TYPE_DTO_1, null)),
            List.of(),
            PostAuctionDealsSelected.NONE,
            new ArrayList<>());

    PostAuctionDiscount foundDiscount =
        new PostAuctionDiscount(
            3L,
            "Update-Test",
            "test description",
            3.33,
            true,
            true,
            PostAuctionDealsSelected.NONE,
            3,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Date.from(Instant.EPOCH),
            Date.from(Instant.EPOCH));

    PostAuctionDiscountDspSeat dspSeat1 = new PostAuctionDiscountDspSeat();
    dspSeat1.setPid(4L);
    dspSeat1.setVersion(1);
    dspSeat1.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat1.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat1.setPostAuctionDiscount(foundDiscount);
    dspSeat1.setDsp(
        new PostAuctionDiscountDspSeatView(
            4L, "DSP-1", new CompanyView(3L, "Test Company Name", CompanyType.BUYER, true)));

    PostAuctionDiscountDspSeat dspSeat2 = new PostAuctionDiscountDspSeat();
    dspSeat2.setPid(5L);
    dspSeat2.setVersion(1);
    dspSeat2.setCreationDate(Date.from(Instant.EPOCH));
    dspSeat2.setLastUpdate(Date.from(Instant.EPOCH));
    dspSeat2.setPostAuctionDiscount(foundDiscount);
    dspSeat2.setDsp(
        new PostAuctionDiscountDspSeatView(
            5L, "DSP-2", new CompanyView(3L, "Test Company Name", CompanyType.BUYER, true)));
    foundDiscount.setDsps(List.of(dspSeat1, dspSeat2));

    PostAuctionDiscountSeller seller1 = new PostAuctionDiscountSeller();
    seller1.setPid(19L);
    seller1.setVersion(2);
    seller1.setCreationDate(Date.from(Instant.EPOCH));
    seller1.setLastUpdate(Date.from(Instant.EPOCH));
    seller1.setPostAuctionDiscount(foundDiscount);
    seller1.setSeller(new CompanyView(6L, "Seller-6", CompanyType.SELLER, true));

    PostAuctionDiscountSeller seller2 = new PostAuctionDiscountSeller();
    seller2.setPid(20L);
    seller2.setVersion(2);
    seller2.setCreationDate(Date.from(Instant.EPOCH));
    seller2.setLastUpdate(Date.from(Instant.EPOCH));
    seller2.setPostAuctionDiscount(foundDiscount);
    seller2.setSeller(new CompanyView(7L, "Seller-7", CompanyType.SELLER, false));
    foundDiscount.setSellers(List.of(seller1, seller2));

    when(discountRepository.findById(3L)).thenReturn(Optional.of(foundDiscount));
    when(postAuctionDiscountTypeRepository.countByPidIn(anyIterable())).thenReturn(1L);
    when(discountRepository.saveAndFlush(any(PostAuctionDiscount.class))).then(returnsFirstArg());

    assertEquals(expected, postAuctionDiscountService.update(input));
  }

  @Test
  void shouldThrowOnUpdateWhenDiscountNotFound() {
    PostAuctionDiscountDTO input = new PostAuctionDiscountDTO();
    input.setPid(5821L);

    when(discountRepository.findById(5821L)).thenReturn(Optional.empty());

    var notFoundException =
        assertThrows(
            GenevaValidationException.class, () -> postAuctionDiscountService.update(input));

    assertEquals(
        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_NOT_FOUND, notFoundException.getErrorCode());
  }

  @ParameterizedTest(name = "shouldThrow{0}")
  @MethodSource("validationArguments")
  void shouldThrowOnCreate(
      String exString, PostAuctionDiscountDTO input, ServerErrorCodes expectedErrorCode) {
    // given
    lenient()
        .when(revenueGroupRepository.countByPidIn(anyList()))
        .thenAnswer(i -> Long.valueOf(i.getArgument(0, List.class).size()));

    // when
    GenevaValidationException actualException =
        assertThrows(
            GenevaValidationException.class, () -> postAuctionDiscountService.create(input));

    // then
    assertEquals(expectedErrorCode, actualException.getErrorCode());
  }

  @ParameterizedTest(name = "shouldThrow{0}")
  @MethodSource("validationArguments")
  void shouldThrowOnUpdate(
      String exString, PostAuctionDiscountDTO input, ServerErrorCodes expectedErrorCode) {
    // given
    when(discountRepository.findById(input.getPid()))
        .thenReturn(Optional.of(new PostAuctionDiscount()));
    lenient()
        .when(revenueGroupRepository.countByPidIn(anyList()))
        .thenAnswer(i -> Long.valueOf(i.getArgument(0, List.class).size()));

    // when
    GenevaValidationException actualException =
        assertThrows(
            GenevaValidationException.class, () -> postAuctionDiscountService.update(input));

    // then
    assertEquals(expectedErrorCode, actualException.getErrorCode());
  }

  private static Stream<Arguments> validationArguments() {
    PostAuctionDiscountDTO duplicateSellerDto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(1L, "seller", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(1L, "seller", DISCOUNT_TYPE_DTO_1, null)),
            null,
            PostAuctionDealsSelected.NONE,
            null);
    PostAuctionDiscountDTO duplicateRevenueGroupDto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            null,
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_1),
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_1)),
            PostAuctionDealsSelected.NONE,
            null);
    PostAuctionDiscountDTO sameTypeInSellersAndRevenueGroupsDto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    12L, "seller", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null)),
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(
                    1L, new PostAuctionDiscountTypeDTO(1L, "pad v1")),
                new PostAuctionDiscountRevenueGroupDTO(
                    2L, new PostAuctionDiscountTypeDTO(2L, "pad v2"))),
            PostAuctionDealsSelected.NONE,
            null);

    return Stream.of(
        Arguments.of(
            "SellerRepeated", duplicateSellerDto, ServerErrorCodes.SERVER_DISCOUNT_SELLER_REPEATED),
        Arguments.of(
            "RevenueGroupRepeated",
            duplicateRevenueGroupDto,
            ServerErrorCodes.SERVER_DISCOUNT_REVENUE_GROUP_REPEATED),
        Arguments.of(
            "SellersAndRevenueGroupMixed",
            sameTypeInSellersAndRevenueGroupsDto,
            ServerErrorCodes.SERVER_DISCOUNT_SELLERS_AND_REVENUE_GROUPS_MIXED));
  }

  @Test
  void shouldThrowWhenNotAllRevenueGroupsExistOnCreate() {
    PostAuctionDiscountDTO dto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            null,
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_1),
                new PostAuctionDiscountRevenueGroupDTO(2L, DISCOUNT_TYPE_DTO_1),
                new PostAuctionDiscountRevenueGroupDTO(3L, DISCOUNT_TYPE_DTO_1)),
            PostAuctionDealsSelected.NONE,
            null);
    when(revenueGroupRepository.countByPidIn(List.of(1L, 2L, 3L))).thenReturn(2L);

    GenevaValidationException actualException =
        assertThrows(GenevaValidationException.class, () -> postAuctionDiscountService.create(dto));

    assertEquals(ServerErrorCodes.SERVER_REVENUE_GROUP_NOT_FOUND, actualException.getErrorCode());
  }

  @Test
  void shouldThrowWhenNotAllRevenueGroupsExistOnUpdate() {
    PostAuctionDiscountDTO dto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            null,
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_1),
                new PostAuctionDiscountRevenueGroupDTO(2L, DISCOUNT_TYPE_DTO_1),
                new PostAuctionDiscountRevenueGroupDTO(3L, DISCOUNT_TYPE_DTO_1)),
            PostAuctionDealsSelected.NONE,
            null);
    when(discountRepository.findById(123L)).thenReturn(Optional.of(new PostAuctionDiscount()));
    when(revenueGroupRepository.countByPidIn(List.of(1L, 2L, 3L))).thenReturn(2L);

    GenevaValidationException actualException =
        assertThrows(GenevaValidationException.class, () -> postAuctionDiscountService.update(dto));

    assertEquals(ServerErrorCodes.SERVER_REVENUE_GROUP_NOT_FOUND, actualException.getErrorCode());
  }

  @Test
  void shouldThrowWhenSomeSellerIsAssignedToSomeGivenRevenueGroupOnCreate() {
    PostAuctionDiscountDTO dto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(4L, "seller1", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(5L, "seller2", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(3L, "seller3", DISCOUNT_TYPE_DTO_1, null)),
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(2L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(3L, DISCOUNT_TYPE_DTO_2)),
            PostAuctionDealsSelected.NONE,
            null);
    when(revenueGroupRepository.countByPidIn(anyList()))
        .thenAnswer(i -> Long.valueOf(((List) i.getArgument(0)).size()));
    when(sellerAttributesRepository.findAllById(any()))
        .thenReturn(
            List.of(
                new SellerAttributes() {
                  {
                    this.setRevenueGroupPid(4L);
                  }
                },
                new SellerAttributes() {
                  {
                    this.setRevenueGroupPid(5L);
                  }
                },
                new SellerAttributes() {
                  {
                    this.setRevenueGroupPid(3L);
                  }
                }));

    GenevaValidationException actualException =
        assertThrows(GenevaValidationException.class, () -> postAuctionDiscountService.create(dto));

    assertEquals(
        ServerErrorCodes.SERVER_DISCOUNT_SELLER_REVENUE_GROUP_CONFLICT,
        actualException.getErrorCode());
  }

  @Test
  void shouldThrowWhenSomeSellerIsAssignedToSomeGivenRevenueGroupOnUpdate() {
    PostAuctionDiscountDTO dto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(4L, "seller1", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(5L, "seller2", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(3L, "seller3", DISCOUNT_TYPE_DTO_1, null)),
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(2L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(3L, DISCOUNT_TYPE_DTO_2)),
            PostAuctionDealsSelected.NONE,
            null);
    when(discountRepository.findById(123L)).thenReturn(Optional.of(new PostAuctionDiscount()));
    when(revenueGroupRepository.countByPidIn(anyList()))
        .thenAnswer(i -> Long.valueOf(((List) i.getArgument(0)).size()));
    when(sellerAttributesRepository.findAllById(any()))
        .thenReturn(
            List.of(
                new SellerAttributes() {
                  {
                    this.setRevenueGroupPid(4L);
                  }
                },
                new SellerAttributes() {
                  {
                    this.setRevenueGroupPid(5L);
                  }
                },
                new SellerAttributes() {
                  {
                    this.setRevenueGroupPid(3L);
                  }
                }));

    GenevaValidationException actualException =
        assertThrows(GenevaValidationException.class, () -> postAuctionDiscountService.update(dto));

    assertEquals(
        ServerErrorCodes.SERVER_DISCOUNT_SELLER_REVENUE_GROUP_CONFLICT,
        actualException.getErrorCode());
  }

  @Test
  void shouldThrowWhenNotAllDiscountTypesExistOnCreate() {
    PostAuctionDiscountDTO dto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(4L, "seller1", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(5L, "seller2", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(3L, "seller3", DISCOUNT_TYPE_DTO_1, null)),
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(2L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(
                    3L, new PostAuctionDiscountTypeDTO(3L, "pad v3"))),
            PostAuctionDealsSelected.NONE,
            null);
    when(revenueGroupRepository.countByPidIn(anyList()))
        .thenAnswer(i -> Long.valueOf(((List) i.getArgument(0)).size()));
    when(postAuctionDiscountTypeRepository.countByPidIn(Set.of(1L, 2L, 3L))).thenReturn(2L);

    GenevaValidationException actualException =
        assertThrows(GenevaValidationException.class, () -> postAuctionDiscountService.create(dto));

    assertEquals(ServerErrorCodes.SERVER_DISCOUNT_TYPE_NOT_FOUND, actualException.getErrorCode());
  }

  @Test
  void shouldThrowWhenNotAllDiscountTypesExistOnUpdate() {
    PostAuctionDiscountDTO dto =
        new PostAuctionDiscountDTO(
            123L,
            "discount",
            true,
            0.5,
            "discount description",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    234L, "dsp", List.of(new PostAuctionDiscountDspSeatDTO(456L, "dsp seat")))),
            List.of(
                new PostAuctionDiscountSellerDTO(4L, "seller1", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(5L, "seller2", DISCOUNT_TYPE_DTO_1, null),
                new PostAuctionDiscountSellerDTO(3L, "seller3", DISCOUNT_TYPE_DTO_1, null)),
            List.of(
                new PostAuctionDiscountRevenueGroupDTO(1L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(2L, DISCOUNT_TYPE_DTO_2),
                new PostAuctionDiscountRevenueGroupDTO(
                    3L, new PostAuctionDiscountTypeDTO(3L, "pad v3"))),
            PostAuctionDealsSelected.NONE,
            null);
    when(discountRepository.findById(123L)).thenReturn(Optional.of(new PostAuctionDiscount()));
    when(revenueGroupRepository.countByPidIn(anyList()))
        .thenAnswer(i -> Long.valueOf(((List) i.getArgument(0)).size()));
    when(postAuctionDiscountTypeRepository.countByPidIn(Set.of(1L, 2L, 3L))).thenReturn(2L);

    GenevaValidationException actualException =
        assertThrows(GenevaValidationException.class, () -> postAuctionDiscountService.update(dto));

    assertEquals(ServerErrorCodes.SERVER_DISCOUNT_TYPE_NOT_FOUND, actualException.getErrorCode());
  }

  @Test
  void shouldThrowOnCreatePostAuctionDiscountWithDefaultSellerTypeWhenNoneIsSupplied() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            187L,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            false,
            0,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L, null, List.of(new PostAuctionDiscountDspSeatDTO(54L, null)))),
            List.of(new PostAuctionDiscountSellerDTO(67L, null, null, null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(new DirectDealViewDTO("ex2", 2L, "test deal")));

    GenevaValidationException genevaValidationException =
        assertThrows(
            GenevaValidationException.class, () -> postAuctionDiscountService.create(input));

    assertEquals(
        ServerErrorCodes.SERVER_DISCOUNT_TYPE_NOT_FOUND, genevaValidationException.getErrorCode());
  }

  @Test
  void shouldThrowOnCreatePostAuctionDiscountWithDefaultRevenueGroupTypeWhenNoneIsSupplied() {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            187L,
            "Test Create Discount",
            true,
            75.0,
            "This is a description",
            false,
            0,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1887L, null, List.of(new PostAuctionDiscountDspSeatDTO(54L, null)))),
            List.of(),
            List.of(new PostAuctionDiscountRevenueGroupDTO(4L, null)),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(new DirectDealViewDTO("ex2", 2L, "test deal")));

    when(revenueGroupRepository.countByPidIn(anyIterable())).thenReturn(1L);

    GenevaValidationException genevaValidationException =
        assertThrows(
            GenevaValidationException.class, () -> postAuctionDiscountService.create(input));

    assertEquals(
        ServerErrorCodes.SERVER_DISCOUNT_TYPE_NOT_FOUND, genevaValidationException.getErrorCode());
  }
}

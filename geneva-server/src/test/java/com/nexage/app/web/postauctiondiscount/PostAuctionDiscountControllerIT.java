package com.nexage.app.web.postauctiondiscount;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.PostAuctionDiscountService;
import com.nexage.app.web.ControllerExceptionHandler;
import com.nexage.app.web.support.BaseControllerItTest;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

class PostAuctionDiscountControllerIT extends BaseControllerItTest {

  @Mock private PostAuctionDiscountService service;

  @InjectMocks private PostAuctionDiscountController controller;

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .setControllerAdvice(controllerExceptionHandler)
            .build();
  }

  @Test
  void shouldTestGetAllPostAuctionDiscountsNoSearchParams() throws Exception {
    Page<PostAuctionDiscountDTO> returnPage =
        new PageImpl<>(
            List.of(
                new PostAuctionDiscountDTO(1L, "Discount 1", true, .75, "Test Description"),
                new PostAuctionDiscountDTO(2L, "Discount 2", true, .15, "Test Description")));

    String expectedOutboundJson = objectMapper.writeValueAsString(returnPage);
    when(service.getAll(
            nullable(Set.class),
            nullable(String.class),
            nullable(Boolean.class),
            nullable(Pageable.class)))
        .thenReturn(returnPage);

    mockMvc
        .perform(get("/v1/post-auction-discounts"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void shouldTestGetAllPostAuctionDiscountsWithSearchParams() throws Throwable {
    Page<PostAuctionDiscountDTO> returnPage =
        new PageImpl<>(
            List.of(
                new PostAuctionDiscountDTO(1L, "Discount Test 1", true, .75, "Test Description")));

    String expectedOutboundJson = objectMapper.writeValueAsString(returnPage);

    when(service.getAll(
            nullable(Set.class),
            nullable(String.class),
            nullable(Boolean.class),
            nullable(Pageable.class)))
        .thenReturn(returnPage);

    mockMvc
        .perform(
            get(
                String.format(
                    "/v1/post-auction-discounts?page=%s&size=%s&qf=%s&qt=%s&enabled=%s",
                    "0", "10", "name", "test-1", "true")))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void shouldTestGetById() throws Exception {
    PostAuctionDiscountDTO expectedJson =
        new PostAuctionDiscountDTO(
            187L,
            "Discount Test 1",
            true,
            .75,
            "Test Description",
            true,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1L,
                    "Test 1",
                    List.of(new PostAuctionDiscountDspSeatDTO(1L, "Test Buyer Seat 1"))),
                new PostAuctionDiscountDspDTO(
                    56L,
                    "Test 56",
                    List.of(new PostAuctionDiscountDspSeatDTO(4L, "Test Buyer Seat 456")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    15L, "Test Company Name 1", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null),
                new PostAuctionDiscountSellerDTO(
                    29L,
                    "Test Company Name 2",
                    new PostAuctionDiscountTypeDTO(1L, "pad v1"),
                    null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(
                new DirectDealViewDTO("ex1", 1L, "Test Deal Name 1"),
                new DirectDealViewDTO("ex1", 2L, "Test Deal Name 2")));

    String expectedJsonToString = objectMapper.writeValueAsString(expectedJson);

    when(service.get(187L)).thenReturn(expectedJson);

    mockMvc
        .perform(get("/v1/post-auction-discounts/187"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJsonToString));
  }

  @Test
  void shouldTestCreatePostAuctionDiscount() throws Exception {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            null,
            "Create Discount Test 1",
            true,
            30.0,
            "Test Description",
            true,
            null,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1L, null, List.of(new PostAuctionDiscountDspSeatDTO(1L, null))),
                new PostAuctionDiscountDspDTO(
                    56L, null, List.of(new PostAuctionDiscountDspSeatDTO(4L, null)))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    15L, null, new PostAuctionDiscountTypeDTO(1L, "pad v1"), null),
                new PostAuctionDiscountSellerDTO(
                    29L, null, new PostAuctionDiscountTypeDTO(1L, "pad v1"), null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(
                new DirectDealViewDTO("ex1", 1L, null), new DirectDealViewDTO("ex2", 2L, null)));

    PostAuctionDiscountDTO expected =
        new PostAuctionDiscountDTO(
            521L,
            "Create Discount Test 1",
            true,
            30.0,
            "Test Description",
            true,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1L,
                    "Real Company 1",
                    List.of(new PostAuctionDiscountDspSeatDTO(1L, "Test Buyer Seat 1"))),
                new PostAuctionDiscountDspDTO(
                    56L,
                    "Buyer Company 12",
                    List.of(new PostAuctionDiscountDspSeatDTO(4L, "Test Buyer Seat 456")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    15L, "Test Company Name 1", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null),
                new PostAuctionDiscountSellerDTO(
                    29L,
                    "Test Company Name 2",
                    new PostAuctionDiscountTypeDTO(1L, "pad v1"),
                    null)),
            List.of(),
            PostAuctionDealsSelected.SPECIFIC,
            List.of(
                new DirectDealViewDTO("ex1", 1L, "Test Deal Name 1"),
                new DirectDealViewDTO("ex2", 2L, "Test Deal Name 2")));

    String expectedOutboundJson = objectMapper.writeValueAsString(expected);

    when(service.create(any(PostAuctionDiscountDTO.class))).thenReturn(expected);

    mockMvc
        .perform(
            post("/v1/post-auction-discounts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().json(expectedOutboundJson));
  }

  @Test
  void shouldTestUpdatePostAuctionDiscount() throws Exception {

    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            8271L,
            "Discount Test Name",
            false,
            18.5,
            "Discount Test Description",
            true,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    51L,
                    "Test Test",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(8L, "Test Name"),
                        new PostAuctionDiscountDspSeatDTO(9L, "Test Name 2"))),
                new PostAuctionDiscountDspDTO(
                    912L,
                    "Test Company 912",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(743L, "Test Name 3"),
                        new PostAuctionDiscountDspSeatDTO(1001L, "Test Name 4")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    21L, "Test Seller 1", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null),
                new PostAuctionDiscountSellerDTO(
                    312L, "Test Seller 2", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null)),
            List.of(),
            PostAuctionDealsSelected.ALL,
            null);

    PostAuctionDiscountDTO expected =
        new PostAuctionDiscountDTO(
            8271L,
            "Discount Test Name",
            false,
            18.5,
            "Discount Test Description",
            true,
            2,
            List.of(
                new PostAuctionDiscountDspDTO(
                    51L,
                    "Test Test",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(8L, "Test Name"),
                        new PostAuctionDiscountDspSeatDTO(9L, "Test Name 2"))),
                new PostAuctionDiscountDspDTO(
                    912L,
                    "Test Company 912",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(743L, "Test Name 3"),
                        new PostAuctionDiscountDspSeatDTO(1001L, "Test Name 4")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    21L, "Test Seller 1", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null),
                new PostAuctionDiscountSellerDTO(
                    312L, "Test Seller 2", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null)),
            List.of(),
            PostAuctionDealsSelected.ALL,
            null);

    String expectedJson = objectMapper.writeValueAsString(expected);

    when(service.update(any(PostAuctionDiscountDTO.class))).thenReturn(expected);

    mockMvc
        .perform(
            put("/v1/post-auction-discounts/8271")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(input)))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void shouldTestUpdatePostAuctionDiscountMismatchedPids() throws Exception {
    PostAuctionDiscountDTO input =
        new PostAuctionDiscountDTO(
            18L,
            "Test Update Discount",
            true,
            12.6,
            "Testing to see discount update fails",
            false,
            1,
            List.of(
                new PostAuctionDiscountDspDTO(
                    1L,
                    "Real Company",
                    List.of(new PostAuctionDiscountDspSeatDTO(2L, "Test Name"))),
                new PostAuctionDiscountDspDTO(
                    2L,
                    "Another Real Company",
                    List.of(
                        new PostAuctionDiscountDspSeatDTO(743L, "Test Name 2"),
                        new PostAuctionDiscountDspSeatDTO(1001L, "Test Name 3")))),
            List.of(
                new PostAuctionDiscountSellerDTO(
                    4L, "Test Seller 1", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null),
                new PostAuctionDiscountSellerDTO(
                    5L, "Test Seller 2", new PostAuctionDiscountTypeDTO(1L, "pad v1"), null)),
            List.of(),
            PostAuctionDealsSelected.ALL,
            null);

    String expectedJson = objectMapper.writeValueAsString(input);

    mockMvc
        .perform(
            put("/v1/post-auction-discounts/81")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(expectedJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        ServerErrorCodes.SERVER_PIDS_MISMATCH))));
  }
}

package com.nexage.app.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.app.dto.BdrCreativeDTO;
import com.nexage.app.dto.BidderCreativeDTO;
import com.nexage.app.dto.CreativeFileReferenceDTO;
import com.nexage.app.dto.support.BDRInsertionOrderDTO;
import com.nexage.app.dto.support.BDRLineItemDTO;
import com.nexage.app.dto.support.BDRTargetGroupDTO;
import com.nexage.app.services.CreativeService;
import com.nexage.app.services.InsertionOrderService;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class CreativeControllerIT {

  @Mock private CreativeService creativeService;

  @Mock private InsertionOrderService insertionOrderService;

  @InjectMocks private CreativeController creativeController;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  private static final long SEATHOLDER_PID = 1L;
  private static final long INSERTION_ORDER_PID = 1L;
  private static final long LINE_ITEM_PID = 1L;
  private static final long TARGET_GROUP_PID = 1L;

  private BdrInsertionOrder bdrInsertionOrder;
  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(creativeController)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
    bdrInsertionOrder = TestObjectsFactory.createBdrInsertionOrder(1, 1);
  }

  @Test
  void shouldGetCreativeUsagesAsList() throws Exception {
    Set<BDRInsertionOrderDTO> bdrInsertionOrderDtos = new HashSet<>();
    for (int i = 0; i < 2; i++) {
      Set<BDRLineItemDTO> bdrLineItemDtos = new HashSet<>();
      for (int j = 0; j < 2; j++) {
        Set<BDRTargetGroupDTO> bdrTargetGroupDtos = new HashSet<>();
        for (int k = 0; k < 3; k++) {
          BDRTargetGroupDTO bdrTargetGroupDto =
              (BDRTargetGroupDTO)
                  BDRTargetGroupDTO.newBuilder()
                      .withPid(RandomUtils.nextLong())
                      .withName(RandomStringUtils.randomAlphanumeric(32))
                      .build();
          bdrTargetGroupDtos.add(bdrTargetGroupDto);
        }
        BDRLineItemDTO bdrLineItemDto =
            (BDRLineItemDTO)
                BDRLineItemDTO.newBuilder()
                    .withTargetGroups(bdrTargetGroupDtos)
                    .withPid(RandomUtils.nextLong())
                    .withName(RandomStringUtils.randomAlphanumeric(32))
                    .build();
        bdrLineItemDtos.add(bdrLineItemDto);
      }
      BDRInsertionOrderDTO insertionOrderDto =
          (BDRInsertionOrderDTO)
              BDRInsertionOrderDTO.newBuilder()
                  .withLineItems(bdrLineItemDtos)
                  .withPid(RandomUtils.nextLong())
                  .withName(RandomStringUtils.randomAlphanumeric(32))
                  .build();
      bdrInsertionOrderDtos.add(insertionOrderDto);
    }

    BDRInsertionOrderDTO firstInsertionOrder = bdrInsertionOrderDtos.iterator().next();
    BDRLineItemDTO firstLineItem = firstInsertionOrder.getLineItems().iterator().next();
    BDRTargetGroupDTO firstTargetGroup = firstLineItem.getTargetGroups().iterator().next();

    when(creativeService.getCreativeUsages(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(bdrInsertionOrderDtos);
    when(insertionOrderService.getInsertionOrder(Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(TestObjectsFactory.createBdrInsertionOrder(1, 1));
    mockMvc
        .perform(
            get(
                "/seatholders/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/creatives/{creativePID}/usages",
                1,
                1,
                1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(bdrInsertionOrderDtos.size())))
        .andExpect(jsonPath("$[0].pid", is(firstInsertionOrder.getPid())))
        .andExpect(jsonPath("$[0].name", is(firstInsertionOrder.getName())))
        .andExpect(jsonPath("$[0].lineItems", hasSize(2)))
        .andExpect(jsonPath("$[0].lineItems[0].pid", is(firstLineItem.getPid())))
        .andExpect(jsonPath("$[0].lineItems[0].name", is(firstLineItem.getName())))
        .andExpect(jsonPath("$[0].lineItems[0].targetGroups", hasSize(3)))
        .andExpect(jsonPath("$[0].lineItems[0].targetGroups[0].pid", is(firstTargetGroup.getPid())))
        .andExpect(
            jsonPath("$[0].lineItems[0].targetGroups[0].name", is(firstTargetGroup.getName())));
  }

  @Test
  void shouldGetCreativesForInsertionOrder() throws Exception {
    // given
    when(insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID))
        .thenReturn(bdrInsertionOrder);
    when(creativeService.getCreativesForAdvertiser(
            SEATHOLDER_PID, bdrInsertionOrder.getAdvertiserPid()))
        .thenReturn(Set.of(new BdrCreative()));

    // when
    mockMvc
        .perform(
            get(
                "/seatholders/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/creatives",
                SEATHOLDER_PID,
                INSERTION_ORDER_PID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    // then
    verify(insertionOrderService).getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID);
    verify(creativeService)
        .getCreativesForAdvertiser(SEATHOLDER_PID, bdrInsertionOrder.getAdvertiserPid());
  }

  @Test
  void shouldCreateCreative() throws Exception {
    // given
    BdrCreativeDTO bdrCreativeDTO = new BdrCreativeDTO();
    BdrCreative bdrCreative = new BdrCreative();
    List<BidderCreativeDTO> creativesDTO = new ArrayList<>();
    creativesDTO.add(new BidderCreativeDTO(new CreativeFileReferenceDTO(), bdrCreativeDTO));

    when(insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID))
        .thenReturn(bdrInsertionOrder);
    when(creativeService.createBdrCreative(
            refEq(creativesDTO), eq(SEATHOLDER_PID), eq(INSERTION_ORDER_PID)))
        .thenReturn(List.of(bdrCreative));

    // when
    mockMvc
        .perform(
            put(
                    "/seatholders/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/creatives",
                    SEATHOLDER_PID,
                    INSERTION_ORDER_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creativesDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasSize(creativesDTO.size())));

    // then
    verify(insertionOrderService).getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID);
    verify(creativeService)
        .createBdrCreative(refEq(creativesDTO), eq(SEATHOLDER_PID), eq(INSERTION_ORDER_PID));
  }

  @Test
  void shouldCreateCreativeWithBdrCreativeDTOAndCustomMarkup() throws Exception {
    // given
    BdrCreative bdrCreative = new BdrCreative();
    ReflectionTestUtils.setField(bdrCreative, "pid", 100L);
    BdrCreativeDTO bdrCreativeDTO = new BdrCreativeDTO();
    CreativeFileReferenceDTO creativeFileReferenceDTO = new CreativeFileReferenceDTO();
    creativeFileReferenceDTO.setImage("IMAGE".getBytes());
    creativeFileReferenceDTO.setExtension("EXTENSION");
    BidderCreativeDTO bidderCreativeDTO =
        new BidderCreativeDTO(creativeFileReferenceDTO, bdrCreativeDTO);

    given(
            creativeService.createBdrCreative(
                refEq(bidderCreativeDTO, "creativeFileRef"),
                eq(SEATHOLDER_PID),
                eq(INSERTION_ORDER_PID)))
        .willReturn(bdrCreative);
    given(
            creativeService.addCreativeToTargetGroup(
                refEq(bdrCreative),
                eq(SEATHOLDER_PID),
                eq(TARGET_GROUP_PID),
                eq(INSERTION_ORDER_PID),
                eq(LINE_ITEM_PID)))
        .willReturn(bdrCreative);

    // when
    mockMvc
        .perform(
            put(
                    "/seatholders/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives",
                    SEATHOLDER_PID,
                    INSERTION_ORDER_PID,
                    LINE_ITEM_PID,
                    TARGET_GROUP_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidderCreativeDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid", is(100)));

    // then
    verify(creativeService)
        .createBdrCreative(
            refEq(bidderCreativeDTO, "creativeFileRef"),
            eq(SEATHOLDER_PID),
            eq(INSERTION_ORDER_PID));
    verify(creativeService)
        .addCreativeToTargetGroup(
            refEq(bdrCreative),
            eq(SEATHOLDER_PID),
            eq(TARGET_GROUP_PID),
            eq(INSERTION_ORDER_PID),
            eq(LINE_ITEM_PID));
  }

  @Test
  void shouldReturnBadRequestWhenCreatingBdrCreativeWithMissingDTO() throws Exception {
    // given
    BidderCreativeDTO bidderCreativeDTO = new BidderCreativeDTO(null, null);

    // when
    mockMvc
        .perform(
            put(
                    "/seatholders/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives",
                    SEATHOLDER_PID,
                    INSERTION_ORDER_PID,
                    LINE_ITEM_PID,
                    TARGET_GROUP_PID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bidderCreativeDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(result.getResolvedException() instanceof GenevaValidationException))
        .andExpect(
            result ->
                assertEquals(
                    CommonErrorCodes.COMMON_BAD_REQUEST,
                    ((GenevaValidationException)
                            Objects.requireNonNull(result.getResolvedException()))
                        .getErrorCode()));

    // then
    verifyNoInteractions(creativeService);
  }

  @Test
  void shouldUpdateCreative() throws Exception {
    // given
    BdrCreative creative = new BdrCreative();
    long creativePid = 100L;
    ReflectionTestUtils.setField(creative, "pid", creativePid);

    when(insertionOrderService.getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID))
        .thenReturn(bdrInsertionOrder);
    when(creativeService.updateBdrCreative(
            refEq(creative), eq(SEATHOLDER_PID), eq(INSERTION_ORDER_PID)))
        .thenReturn(creative);

    // when
    mockMvc
        .perform(
            put(
                    "/seatholders/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/creatives/{creativePID}",
                    SEATHOLDER_PID,
                    INSERTION_ORDER_PID,
                    creativePid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creative)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pid").value(creativePid));

    // then
    verify(insertionOrderService).getInsertionOrder(SEATHOLDER_PID, INSERTION_ORDER_PID);
    verify(creativeService)
        .updateBdrCreative(refEq(creative), eq(SEATHOLDER_PID), eq(INSERTION_ORDER_PID));
  }
}

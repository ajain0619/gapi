package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BDRTargetGroupCreative;
import com.nexage.admin.core.bidder.model.BdrConfig;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.bidder.type.BDRMraidCompliance;
import com.nexage.admin.core.bidder.type.BDRStatus;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import com.nexage.admin.core.repository.BdrConfigRepository;
import com.nexage.admin.core.repository.BdrCreativeRepository;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.admin.core.repository.BdrTargetGroupRepository;
import com.nexage.app.dto.BdrCreativeDTO;
import com.nexage.app.dto.BidderCreativeDTO;
import com.nexage.app.dto.CreativeFileReferenceDTO;
import com.nexage.app.dto.CreativeFileReferenceDTO.CreativeSizeType;
import com.nexage.app.dto.support.BDRInsertionOrderDTO;
import com.nexage.app.dto.support.BDRLineItemDTO;
import com.nexage.app.dto.support.BDRTargetGroupDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.BdrRelationshipValidationService;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CreativeServiceImplTest {

  private static final long CREATIVE_PID = 1L;
  private static final long SEAT_HOLDER_PID = 1L;
  private static final long INSERTION_ORDER_PID = 1L;
  private static final long LINE_ITEM_PID = 1L;
  private static final long TARGET_GROUP_PID = 1L;
  private static final long ADVERTISER_PID = 1L;
  private static final long COMPANY_PID = 1L;
  private static final long BDR_TARGET_PID = 1L;
  private static final byte[] DATA = "Test".getBytes();
  private static final String EXTENSION = "test-ext";

  @Mock private BdrCreativeRepository bdrCreativeRepository;
  @Mock private BDRAdvertiserRepository bdrAdvertiserRepository;
  @Mock private GlobalConfigService globalConfigService;
  @Mock private BdrConfigRepository bdrConfigRepository;
  @Mock private BdrInsertionOrderRepository bdrInsertionOrderRepository;
  @Mock private BdrRelationshipValidationService relationshipValidator;
  @Mock private BdrTargetGroupRepository targetGroupRepository;
  @Mock private FileSystemService fileSystemService;
  @InjectMocks private CreativeServiceImpl creativeServiceImpl;

  @BeforeEach
  void setUp() throws Exception {
    BdrConfig bdrConfig = new BdrConfig();
    bdrConfig.setValue("http://localhost");
    given(globalConfigService.getStringValue(GlobalConfigProperty.CREATIVE_CONFIG_DIR))
        .willReturn("baseDir");
    given(bdrConfigRepository.findByProperty("bidder.creative.host")).willReturn(bdrConfig);

    creativeServiceImpl.init();
  }

  @Test
  void shouldGetCreativeUsages() {
    // given
    int insertionOrderCount = 2;
    int lineItemCount = 3;
    int targetGroupCount = 4;
    BDRAdvertiser advertiser = TestObjectsFactory.createBdrAdvertiser(CompanyType.SEATHOLDER);
    BdrCreative creative = spy(TestObjectsFactory.createBdrCreative());

    Set<BdrTargetGroup> targetGroups = new HashSet<>();
    Collection<BdrInsertionOrder> insertionOrders =
        TestObjectsFactory.createBdrInsertionOrders(
            insertionOrderCount, lineItemCount, targetGroupCount);
    for (BdrInsertionOrder insertionOrder : insertionOrders) {
      for (BDRLineItem lineItem : insertionOrder.getLineItems()) {
        targetGroups.addAll(lineItem.getTargetGroups());
      }
    }

    Collection<BdrInsertionOrder> fakeInsertionOrders =
        TestObjectsFactory.createBdrInsertionOrders(
            insertionOrderCount, lineItemCount, targetGroupCount);
    for (BdrInsertionOrder fakeInsertionOrder : fakeInsertionOrders) {
      for (BDRLineItem fakeLineItem : fakeInsertionOrder.getLineItems()) {
        BdrTargetGroup firstFakeTargetGroup = fakeLineItem.getTargetGroups().iterator().next();
        targetGroups
            .iterator()
            .next()
            .getLineItem()
            .getInsertionOrder()
            .getLineItems()
            .add(fakeLineItem);
        targetGroups.iterator().next().getLineItem().getTargetGroups().add(firstFakeTargetGroup);
      }
    }
    given(creative.getTargetGroups()).willReturn(targetGroups);
    given(bdrCreativeRepository.findById(anyLong())).willReturn(Optional.of(creative));

    // when
    Set<BDRInsertionOrderDTO> insertionOrderDtos =
        creativeServiceImpl.getCreativeUsages(
            advertiser.getCompany().getPid(), advertiser.getPid(), creative.getPid());

    // then
    assertEquals(insertionOrderDtos.size(), insertionOrderCount);
    for (BDRInsertionOrderDTO insertionOrderDto : insertionOrderDtos) {
      Set<BDRLineItemDTO> lineItems = insertionOrderDto.getLineItems();
      assertEquals(lineItems.size(), lineItemCount);
      for (BDRLineItemDTO lineItem : lineItems) {
        Set<? extends BDRTargetGroupDTO> targetGroup = lineItem.getTargetGroups();
        assertEquals(targetGroup.size(), targetGroupCount);
      }
    }
  }

  @Test
  void shouldCreateBdrCreative() {
    // given
    CreativeFileReferenceDTO creativeFileRef = new CreativeFileReferenceDTO();
    BdrCreativeDTO bdrCreativeDto = new BdrCreativeDTO();
    BidderCreativeDTO bidderCreativeDTO = new BidderCreativeDTO(creativeFileRef, bdrCreativeDto);

    BdrInsertionOrder insertionOrder = new BdrInsertionOrder();
    insertionOrder.setAdvertiserPid(ADVERTISER_PID);

    BdrCreative inputCreative = new BdrCreative();
    inputCreative.setStatus(BDRStatus.ACTIVE);
    inputCreative.setMraidCompliance(BDRMraidCompliance.NONE);
    inputCreative.setCustomMarkup("custom_markup");
    inputCreative.setAdvertiser(new BDRAdvertiser());

    given(bdrCreativeRepository.save(any(BdrCreative.class))).willReturn(inputCreative);
    given(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .willReturn(Optional.of(insertionOrder));
    given(bdrAdvertiserRepository.findById(ADVERTISER_PID))
        .willReturn(Optional.of(new BDRAdvertiser()));

    // when
    BdrCreative result =
        creativeServiceImpl.createBdrCreative(
            bidderCreativeDTO, SEAT_HOLDER_PID, INSERTION_ORDER_PID);

    // then
    assertEquals(inputCreative, result);
  }

  @Test
  void shouldThrowExceptionOnDuplicateCreativeNameOnCreate() {
    // given
    BidderCreativeDTO bidderCreativeDTO =
        new BidderCreativeDTO(new CreativeFileReferenceDTO(), new BdrCreativeDTO());
    when(bdrCreativeRepository.existsByNameAndAdvertiserCompanyPid(any(), anyLong()))
        .thenReturn(true);

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                creativeServiceImpl.createBdrCreative(
                    bidderCreativeDTO, SEAT_HOLDER_PID, INSERTION_ORDER_PID));

    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_CREATIVE_NAME, exception.getErrorCode());
  }

  @Test
  void shouldGetAllBdrCreativesForAdvertiser() {
    // given
    BdrCreative inputCreative = new BdrCreative();
    given(bdrCreativeRepository.findActiveByAdvertiserPid(ADVERTISER_PID))
        .willReturn(Set.of(inputCreative));
    BDRAdvertiser advertiser = new BDRAdvertiser();
    Company company = new Company();
    company.setPid(COMPANY_PID);
    advertiser.setCompany(company);
    BdrInsertionOrder io = new BdrInsertionOrder();
    io.setAdvertiserPid(123L);

    // when
    Set<BdrCreative> result =
        creativeServiceImpl.getCreativesForAdvertiser(SEAT_HOLDER_PID, ADVERTISER_PID);

    // then
    assertEquals(inputCreative, result.iterator().next());
    verifyNoMoreInteractions(bdrAdvertiserRepository);
  }

  @Test
  void shouldGetBdrCreativesForAdvertiser() {
    // given
    BdrCreative inputCreative = new BdrCreative();
    given(
            bdrCreativeRepository.findActiveByAdvertiserPidAndPidInCreativePids(
                ADVERTISER_PID, Set.of(CREATIVE_PID)))
        .willReturn(Set.of(inputCreative));
    BDRAdvertiser advertiser = new BDRAdvertiser();
    Company company = new Company();
    company.setPid(CREATIVE_PID);
    advertiser.setCompany(company);

    // when
    Set<BdrCreative> result =
        creativeServiceImpl.getCreativesForAdvertiser(
            SEAT_HOLDER_PID, ADVERTISER_PID, Set.of(CREATIVE_PID));

    // then
    assertEquals(inputCreative, result.iterator().next());
  }

  @Test
  void shouldGetCreative() {
    // given
    BdrCreative inputCreative = new BdrCreative();
    given(bdrCreativeRepository.findById(CREATIVE_PID)).willReturn(Optional.of(inputCreative));

    // when
    BdrCreative result =
        creativeServiceImpl.getCreative(
            SEAT_HOLDER_PID, INSERTION_ORDER_PID, LINE_ITEM_PID, TARGET_GROUP_PID, CREATIVE_PID);

    // then
    assertEquals(inputCreative, result);
  }

  @Test
  void shouldThrowResourceNotFoundExceptionOnGetCreative() {
    // given
    given(bdrCreativeRepository.findById(CREATIVE_PID)).willReturn(Optional.empty());

    // when/then
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                creativeServiceImpl.getCreative(
                    SEAT_HOLDER_PID,
                    INSERTION_ORDER_PID,
                    LINE_ITEM_PID,
                    TARGET_GROUP_PID,
                    CREATIVE_PID));
    assertEquals(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowResourceNotFoundExceptionOnGetCreativeUsages() {
    // given
    given(bdrCreativeRepository.findById(anyLong())).willReturn(Optional.empty());

    // when/then
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                creativeServiceImpl.getCreativeUsages(
                    SEAT_HOLDER_PID, ADVERTISER_PID, CREATIVE_PID));
    assertEquals(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenBDRAdvertiserNotFound() {
    // given
    BidderCreativeDTO bidderCreative = new BidderCreativeDTO(null, new BdrCreativeDTO());

    BdrInsertionOrder io = new BdrInsertionOrder();
    io.setAdvertiserPid(123L);
    when(bdrInsertionOrderRepository.findById(any())).thenReturn(Optional.of(io));

    // when
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> creativeServiceImpl.createBdrCreative(bidderCreative, 100L, 200L));
    // then
    assertEquals(ServerErrorCodes.SERVER_ADVERTISER_NOT_FOUND, ex.getErrorCode());
    verify(bdrAdvertiserRepository, times(1)).findById(123L);
    verifyNoMoreInteractions(bdrAdvertiserRepository);
  }

  @Test
  void shouldUpdateBdrCreative() {
    // given
    Long creativePid = 456L;

    var input =
        new BdrCreative() {
          {
            ReflectionTestUtils.setField(this, "pid", creativePid);
            setIndicativeURL("Some things in life are bad, they can really make you mad");
            setLandingURL("Other things just make you swear and curse");
            setTrackingURL("When you're chewing on life's gristle, don't grumble, give a whistle");
            setName("And this'll help things turn out for the best");
            setCustomMarkup("Always look on the bright side of life");
            setHeight(12);
            setWidth(34);
            setMraidCompliance(BDRMraidCompliance.NONE);
          }
        };
    var original = new BdrCreative();

    given(
            bdrCreativeRepository.existsByNameAndAdvertiserCompanyPid(
                input.getName(), SEAT_HOLDER_PID))
        .willReturn(false);
    given(bdrCreativeRepository.findById(creativePid)).willReturn(Optional.of(original));
    given(bdrCreativeRepository.save(original)).willReturn(original);

    // when
    BdrCreative result = creativeServiceImpl.updateBdrCreative(input, SEAT_HOLDER_PID, 0);

    // then
    assertEquals(input.getIndicativeURL(), result.getIndicativeURL());
    assertEquals(input.getLandingURL(), result.getLandingURL());
    assertEquals(input.getTrackingURL(), result.getTrackingURL());
    assertEquals(input.getName(), result.getName());
    assertEquals(input.getCustomMarkup(), result.getCustomMarkup());
    assertEquals(input.getHeight(), result.getHeight());
    assertEquals(input.getWidth(), result.getWidth());
    assertEquals(input.getMraidCompliance(), result.getMraidCompliance());
  }

  @Test
  void shouldThrowExceptionOnDuplicateCreativeNameOnUpdate() {
    // given
    var input =
        new BdrCreative() {
          {
            ReflectionTestUtils.setField(this, "pid", 1L);
            setName("test");
          }
        };
    given(bdrCreativeRepository.findById(anyLong())).willReturn(Optional.of(new BdrCreative()));
    when(bdrCreativeRepository.existsByNameAndAdvertiserCompanyPid(any(), anyLong()))
        .thenReturn(true);

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                creativeServiceImpl.updateBdrCreative(input, SEAT_HOLDER_PID, INSERTION_ORDER_PID));

    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_CREATIVE_NAME, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadExceptionOnUpdateBdrCreativeWhenNoBrdCreative() {
    // given
    var input = new BdrCreative();

    // when/then
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () -> creativeServiceImpl.updateBdrCreative(input, SEAT_HOLDER_PID, 0));
    assertEquals(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldCreateBdrCreativeFromListOfDtos() {
    // given
    var inputCreatives =
        List.of(
            new BidderCreativeDTO(
                new CreativeFileReferenceDTO(new byte[0], "png", CreativeSizeType.MEDIUM_RECTANGLE),
                new BdrCreativeDTO()));
    long insertionOrderPid = 2L;
    long advertiserPid = 3L;
    BdrInsertionOrder insertionOrder = mock(BdrInsertionOrder.class);
    BDRAdvertiser advertiser = mock(BDRAdvertiser.class);
    var creative =
        new BdrCreative() {
          {
            ReflectionTestUtils.setField(this, "pid", 345L);
            setCustomMarkup("I'm not empty!");
            setWidth(300);
            setHeight(250);
            setAdvertiser(advertiser);
          }
        };

    given(insertionOrder.getAdvertiserPid()).willReturn(advertiserPid);
    given(advertiser.getPid()).willReturn(advertiserPid);

    given(bdrInsertionOrderRepository.findById(insertionOrderPid))
        .willReturn(Optional.of(insertionOrder));
    given(bdrAdvertiserRepository.findById(advertiserPid)).willReturn(Optional.of(advertiser));
    given(bdrCreativeRepository.save(any())).willReturn(creative);
    given(bdrCreativeRepository.save(creative)).willReturn(creative);

    // when
    List<BdrCreative> result =
        creativeServiceImpl.createBdrCreative(inputCreatives, SEAT_HOLDER_PID, insertionOrderPid);

    // then
    assertEquals(List.of(creative), result);
  }

  @Test
  void shouldAddCreativesToTargetGroup() {
    // given
    BdrTargetGroup bdrTargetGroup = new BdrTargetGroup();
    BDRLineItem bdrLineItem = new BDRLineItem();
    BdrInsertionOrder bdrInsertionOrder = new BdrInsertionOrder();
    bdrLineItem.setInsertionOrder(bdrInsertionOrder);
    bdrTargetGroup.setLineItem(bdrLineItem);

    BdrCreative bdrCreative = new BdrCreative();

    given(targetGroupRepository.findById(BDR_TARGET_PID)).willReturn(Optional.of(bdrTargetGroup));
    given(bdrCreativeRepository.findById(CREATIVE_PID)).willReturn(Optional.of(bdrCreative));

    // when
    creativeServiceImpl.addCreativesToTargetGroup(
        SEAT_HOLDER_PID,
        INSERTION_ORDER_PID,
        LINE_ITEM_PID,
        TARGET_GROUP_PID,
        List.of(CREATIVE_PID));

    // then
    assertEquals(1, bdrTargetGroup.getTargetGroupCreatives().size());
    assertEquals(
        bdrCreative, bdrTargetGroup.getTargetGroupCreatives().iterator().next().getCreative());
  }

  @Test
  void shouldThrowGenevaValidationExceptionOnAddCreativesToTargetGroup() {
    // given
    given(targetGroupRepository.findById(BDR_TARGET_PID))
        .willReturn(Optional.of(new BdrTargetGroup()));
    given(bdrCreativeRepository.findById(CREATIVE_PID)).willReturn(Optional.empty());

    // when/thenBdrCreativeRepositoryIT
    List<Long> creativePids = List.of(CREATIVE_PID);
    GenevaValidationException ex =
        assertThrows(
            GenevaValidationException.class,
            () ->
                creativeServiceImpl.addCreativesToTargetGroup(
                    SEAT_HOLDER_PID,
                    INSERTION_ORDER_PID,
                    LINE_ITEM_PID,
                    TARGET_GROUP_PID,
                    creativePids));
    assertEquals(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldSave3rdPartyImage() {
    // given
    long advertiserPid = 3L;
    BDRAdvertiser bdrAdvertiser = new BDRAdvertiser();
    ReflectionTestUtils.setField(bdrAdvertiser, "pid", advertiserPid);
    BdrInsertionOrder bdrInsertionOrder = new BdrInsertionOrder();
    bdrInsertionOrder.setAdvertiser(bdrAdvertiser);
    String regEx =
        "http://localhost/iurl/"
            + SEAT_HOLDER_PID
            + "/"
            + advertiserPid
            + "/\\d{24}\\."
            + EXTENSION;

    when(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID))
        .thenReturn(Optional.of(bdrInsertionOrder));

    // when
    String result =
        creativeServiceImpl.save3rdPartyImage(
            SEAT_HOLDER_PID, INSERTION_ORDER_PID, DATA, EXTENSION);

    // then
    assertTrue(result.matches(regEx));
    verify(bdrInsertionOrderRepository).findById(INSERTION_ORDER_PID);
  }

  @Test
  void shouldGetCreativesForTargetgroup() {
    // given
    BdrCreative creative = new BdrCreative();
    Set<BdrCreative> bdrCreativesSource = new HashSet<>();
    bdrCreativesSource.add(creative);
    BDRTargetGroupCreative targetGroupCreative = new BDRTargetGroupCreative();
    targetGroupCreative.setCreative(creative);
    Set<BDRTargetGroupCreative> targetGroupCreatives = new HashSet<>();
    targetGroupCreatives.add(targetGroupCreative);
    BdrTargetGroup bdrTargetGroup = new BdrTargetGroup();
    bdrTargetGroup.setTargetGroupCreatives(targetGroupCreatives);
    bdrTargetGroup.setCreatives(bdrCreativesSource);
    doNothing()
        .when(relationshipValidator)
        .validateRelationship(
            SEAT_HOLDER_PID, INSERTION_ORDER_PID, LINE_ITEM_PID, TARGET_GROUP_PID);
    given(targetGroupRepository.findById(TARGET_GROUP_PID)).willReturn(Optional.of(bdrTargetGroup));

    // when
    Set<BdrCreative> bdrCreatives =
        creativeServiceImpl.getCreativesForTargetgroup(
            SEAT_HOLDER_PID, INSERTION_ORDER_PID, LINE_ITEM_PID, TARGET_GROUP_PID);

    // then
    assertEquals(bdrCreativesSource, bdrCreatives);
  }

  @Test
  void shouldThrowNotFoundWhenBdrTargetGroupDoesNotExist() {
    // when
    when(targetGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                creativeServiceImpl.getCreativesForTargetgroup(
                    SEAT_HOLDER_PID, INSERTION_ORDER_PID, LINE_ITEM_PID, TARGET_GROUP_PID));
    assertEquals(ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenInsertionOrderNotFound() {
    // given
    when(bdrInsertionOrderRepository.findById(INSERTION_ORDER_PID)).thenReturn(Optional.empty());

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () ->
                creativeServiceImpl.save3rdPartyImage(
                    SEAT_HOLDER_PID, INSERTION_ORDER_PID, DATA, EXTENSION));

    // then
    assertEquals(ServerErrorCodes.SERVER_IO_NOT_FOUND, result.getErrorCode());
    verify(bdrInsertionOrderRepository).findById(INSERTION_ORDER_PID);
  }

  @Test
  void shouldRemoveCreativesFromTargetGroup() {
    // given
    var argument = ArgumentCaptor.forClass(BdrTargetGroup.class);
    var pids = List.of(1L, 2L, 3L);
    var targetGroupCreatives = new HashSet<BDRTargetGroupCreative>();
    for (var pid : pids) {
      var creative = new BdrCreative();
      ReflectionTestUtils.setField(creative, "pid", pid);
      var bdrTargetGroupCreative = new BDRTargetGroupCreative();
      bdrTargetGroupCreative.setCreative(creative);
      targetGroupCreatives.add(bdrTargetGroupCreative);
    }
    var bdrTargetGroup = createTargetGroup();
    bdrTargetGroup.setTargetGroupCreatives(targetGroupCreatives);
    given(targetGroupRepository.findById(1L)).willReturn(Optional.of(bdrTargetGroup));

    // when
    creativeServiceImpl.removeCreativesFromTargetGroup(1L, 1L, 1L, 1L, List.of(1L, 2L));

    // then
    verify(targetGroupRepository).save(argument.capture());
    assertEquals(1, argument.getValue().getTargetGroupCreatives().size());
  }

  @Test
  void shouldThrowNotFoundWhenBdrTargetGroupDoesNotExistOnRemove() {
    // when
    when(targetGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> creativeServiceImpl.removeCreativesFromTargetGroup(1L, 1L, 1L, 1L, null));
    assertEquals(ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldAddCreativeToTargetGroup() {
    // given
    BdrCreative creative = new BdrCreative();
    BdrInsertionOrder insertionOrder = new BdrInsertionOrder();
    BDRLineItem bdrLineItem = new BDRLineItem();
    BdrTargetGroup bdrTargetGroup = new BdrTargetGroup();
    bdrTargetGroup.setLineItem(bdrLineItem);
    bdrLineItem.setInsertionOrder(insertionOrder);
    ReflectionTestUtils.setField(bdrTargetGroup, "pid", 1L);
    bdrLineItem.setTargetGroups(Set.of(bdrTargetGroup));
    ReflectionTestUtils.setField(bdrLineItem, "pid", 1L);
    insertionOrder.setLineItems(Set.of(bdrLineItem));

    given(bdrInsertionOrderRepository.findById(1L)).willReturn(Optional.of(insertionOrder));

    // when
    creativeServiceImpl.addCreativeToTargetGroup(creative, 1L, 1L, 1L, 1L);

    // then
    assertEquals(creative, new ArrayList<>(bdrTargetGroup.getCreatives()).get(0));
  }

  private BdrTargetGroup createTargetGroup() {
    BdrTargetGroup targetGroup = new BdrTargetGroup();
    targetGroup.setLineItem(new BDRLineItem());
    targetGroup.getLineItem().setInsertionOrder(new BdrInsertionOrder());
    return targetGroup;
  }
}

package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anySetOf;
import static org.mockito.Mockito.lenient;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.BuyerSeatRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.validation.sellingrule.AbstractBidderValidator.BidderSeat;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuyerSeatTargetValidatorTest {

  private static final Map<Long, BidderSeat> EXISTING_SEATS = createExistingSeats();
  private static final Map<Long, List<Long>> BUYER_COMPANY_PIDS = createBuyerCompanyPids();

  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private GlobalConfigService globalConfigService;
  @Mock private BuyerSeatRepository buyerSeatRepository;
  @Mock private BuyerGroupRepository buyerGroupRepository;
  @InjectMocks private BuyerSeatTargetValidator validator;

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[{\"buyerCompany\":1}]",
        "[{\"buyerCompany\":1, \"bidders\":[11,21]}]",
        "[{\"buyerCompany\":1, \"seats\":[\"seat14\"]}]",
        "[{\"buyerCompany\":1, \"bidders\":[11,31], \"seats\":[\"seat14\", \"seat17\"]}]",
        "[{\"buyerCompany\":1, \"bidders\":[11,21,31], \"buyerGroups\":[11,13],\"seats\":[\"seat14\"]}]",
        "[{\"buyerCompany\":1}, {\"buyerCompany\":2}]",
        "[{\"buyerCompany\":1, \"bidders\":[11,21,31]}, {\"buyerCompany\":2, \"bidders\":[21,22]}]",
        "[{\"buyerCompany\":1, \"bidders\":[11,21,31]}, {\"buyerCompany\":2, \"bidders\":[21,22,23]}]",
        "[{\"buyerCompany\":1, \"buyerGroups\":[11,12]}, {\"buyerCompany\":2, \"buyerGroups\":[21]}]",
        "[{\"buyerCompany\":1, \"buyerGroups\":[11,12]}]",
        "[{\"buyerCompany\":1, \"seats\":[\"seat14\", \"seat15\"]}, {\"buyerCompany\":2, \"seats\":[\"seat24\"]}]",
        "[{\"buyerCompany\":1, \"bidders\":[11,31], \"seats\":[\"seat14\", \"seat15\"], \"buyerGroups\":[11,13]}]",
        "[{\"buyerCompany\":1, \"bidders\":[11,31], \"seats\":[\"seat14\", \"seat15\"], \"buyerGroups\":[11,13]}, {\"buyerCompany\":2, \"bidders\":[21,22], \"seats\":[\"seat24\"], \"buyerGroups\":[21,23]}]",
        "[{\"buyerCompany\":1, \"bidder\":101,\"buyerGroups\":[11,13],\"seats\":[\"seat14\"]}]",
        "[{\"bidder\":101,\"buyerGroups\":[11,13],\"seats\":[\"seat14\"]}]",
        "[{\"bidder\":101,\"buyerGroups\":[11,13]},{\"bidder\":102,\"seats\":[\"seat25\"]},{\"bidder\":103}]"
      })
  void shouldSuccessfullyValidateCorrectInput(String data) {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();

    // when
    assertDoesNotThrow(() -> validator.accept(createBuyerSeatTarget(data)));
  }

  @Test
  void shouldThrowWhenInvalidBuyerCompanyIsPresent() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"buyerCompany\":11111}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_NOT_FOUND, e.getErrorCode());
  }

  @Test
  void
      shouldThrowWhenBuyerCompanyIsPresentAndOnlyASubsetOfBiddersAreTargetedAndOneOfThemIsInvalid() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"buyerCompany\":1, \"bidders\":[11,311]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BIDDER_PID_DOES_NOT_BELONG_TO_BUYER_COMPANY, e.getErrorCode());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[{\"buyerCompany\":1, \"seats\":[\"seat14\", \"seat15\"]}, {\"buyerCompany\":1, \"seats\":[\"seat24\", \"seat25\"]}]",
        "[{\"buyerCompany\":1, \"seats\":[\"seat14\", \"seat15\"]}, {\"buyerCompany\":2, \"seats\":[\"seat2423\"]}]",
        "[{\"bidder\":101,\"seats\":[\"seat99\"]}]",
        "[{\"bidder\":101,\"seats\":[\"seat21\"]}]"
      })
  void shouldThrowWhenBuyerSeatNotFound(String data) {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_SEAT_NOT_FOUND, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenInvalidBuyerCompanyPidIsPresent() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data =
        "[{\"buyerCompany\":1000, \"bidder\":101,\"buyerGroups\":[11,13],\"seats\":[\"seat14\"]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_NOT_FOUND, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenValidBuyerCompanyHasInvalidBidders() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data =
        "[{\"buyerCompany\":1, \"bidders\":[111,211,311], \"bidder\":101,\"buyerGroups\":[11,13],\"seats\":[\"seat14\"]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BIDDER_PID_DOES_NOT_BELONG_TO_BUYER_COMPANY, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenBidderHasEmptyBuyerGroups() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"bidder\":101,\"buyerGroups\":[]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_GROUP_EMPTY_LIST, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenBidderHasEmptySeats() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"bidder\":101,\"seats\":[]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_SEAT_EMPTY_LIST, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenBidderHasEmptyBuyerGroupsAndSeatExistenceValidationIsDisabled() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationDisabled();
    final String data = "[{\"bidder\":101,\"buyerGroups\":[]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_GROUP_EMPTY_LIST, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenBidderHasEmptySeatsAndSeatExistenceValidationIsDisabled() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationDisabled();
    final String data = "[{\"bidder\":101,\"seats\":[]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_SEAT_EMPTY_LIST, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenDataIsNull() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = null;
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenDataIsBlank() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "   ";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenDataIsNotJson() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "1,2,3,4";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_JSON_FORMAT, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenDataIsJsonObjectInsteadOfArray() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "{\"bidder\":101}";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_JSON_FORMAT, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenDataIsEmptyArray() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_EMPTY_LIST, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenDataIsArrayWithEmptyObject() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_NULL_BIDDER, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenDataHasNoBidder() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"buyerGroups\":[11]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_NULL_BIDDER, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenBidderDoesNotExist() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"bidder\":109}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BIDDER_NOT_FOUND, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenBuyerGroupDoesNotExist() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"bidder\":101,\"buyerGroups\":[99]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_GROUP_NOT_FOUND, e.getErrorCode());
  }

  @Test
  void shouldThrowWhenBuyerGroupBelongsToAnotherCompany() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data = "[{\"bidder\":101,\"buyerGroups\":[22]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_BUYER_GROUP_NOT_FOUND, e.getErrorCode());
  }

  @Test
  void shouldSuccessfullyValidateWhenSeatDoesNotExistButSeatExistenceValidationIsDisabled() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationDisabled();
    final String data = "[{\"bidder\":101,\"seats\":[\"seat99\"]}]";

    // when
    assertDoesNotThrow(() -> validator.accept(createBuyerSeatTarget(data)));
  }

  @Test
  void
      shouldSuccessfullyValidateWhenSeatBelongsToAnotherCompanyButSeatExistenceValidationIsDisabled() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationDisabled();
    final String data = "[{\"bidder\":101,\"seats\":[\"seat21\"]}]";

    // when
    assertDoesNotThrow(() -> validator.accept(createBuyerSeatTarget(data)));
  }

  @Test
  void shouldThrowWhenBidderAppearsTwice() {
    // given
    prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled();
    final String data =
        "[{\"bidder\":101,\"buyerGroups\":[11]},{\"bidder\":101,\"seats\":[\"seat12\"]}]";
    RuleTargetDTO ruleTargetDTO = createBuyerSeatTarget(data);

    // when
    GenevaValidationException e =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_BUYER_SEAT_TARGET_DUPLICATED_BIDDER, e.getErrorCode());
  }

  private void prepareMocksAndInitValidatorWithSeatExistenceValidationEnabled() {
    prepareMocksAndInitValidator(true);
  }

  private void prepareMocksAndInitValidatorWithSeatExistenceValidationDisabled() {
    prepareMocksAndInitValidator(false);
  }

  private void prepareMocksAndInitValidator(boolean seatExistenceValidationEnabled) {
    mockGlobalConfigService(seatExistenceValidationEnabled);
    mockBidderConfigRepository();
    mockCompanyRepository();
    mockBuyerSeatRepository();
    mockBuyerGroupRepository();
    validator.init();
  }

  private void mockBuyerGroupRepository() {
    lenient()
        .when(buyerGroupRepository.countByCompanyPidAndPidIn(any(Long.class), anySetOf(Long.class)))
        .then(
            invocation -> {
              Long companyPid = (Long) invocation.getArguments()[0];
              Set<Long> inboundBuyerGroups = (Set<Long>) invocation.getArguments()[1];
              if (companyPid == 0) return 0;
              if (BUYER_COMPANY_PIDS.containsKey(companyPid)) {
                if (companyPid == 1) {
                  return inboundBuyerGroups.stream()
                      .filter(
                          inboundBuyerGroup ->
                              EXISTING_SEATS.get(104L).getBuyerGroups().contains(inboundBuyerGroup))
                      .count();
                }
                if (companyPid == 2) {
                  return inboundBuyerGroups.stream()
                      .filter(
                          inboundBuyerGroup ->
                              EXISTING_SEATS.get(105L).getBuyerGroups().contains(inboundBuyerGroup))
                      .count();
                }
              }
              return 0;
            });
  }

  private void mockBuyerSeatRepository() {
    lenient()
        .when(
            buyerSeatRepository.countByCompanyPidAndSeatIn(any(Long.class), anySetOf(String.class)))
        .then(
            invocation -> {
              Long companyPid = (Long) invocation.getArguments()[0];
              Set<String> inboundSeats = (Set<String>) invocation.getArguments()[1];
              if (companyPid == 0) return 0;
              if (BUYER_COMPANY_PIDS.containsKey(companyPid)) {
                if (companyPid == 1) {
                  return inboundSeats.stream()
                      .filter(
                          inboundSeat -> EXISTING_SEATS.get(104L).getSeats().contains(inboundSeat))
                      .count();
                }
                if (companyPid == 2) {
                  return inboundSeats.stream()
                      .filter(
                          inboundSeat -> EXISTING_SEATS.get(105L).getSeats().contains(inboundSeat))
                      .count();
                }
              }
              return 0;
            });
  }

  private void mockCompanyRepository() {
    lenient()
        .when(companyRepository.countByPids(anySetOf(Long.class)))
        .then(
            invocation -> {
              Set<Long> pids = (Set<Long>) invocation.getArguments()[0];
              return pids.stream().filter(BUYER_COMPANY_PIDS::containsKey).count();
            });
  }

  private void mockBidderConfigRepository() {
    lenient()
        .when(bidderConfigRepository.findPidsByCompanyPid(any()))
        .then(
            invocation -> {
              Long companyPid = (Long) invocation.getArguments()[0];
              if (companyPid == 1) {
                return Sets.newHashSet(11L, 21L, 31L);
              } else {
                return Sets.newHashSet(21L, 22L, 23L);
              }
            });

    lenient()
        .when(bidderConfigRepository.countByPidIn(anySet()))
        .then(
            invocation -> {
              Set<Long> pids = (Set<Long>) invocation.getArguments()[0];
              return pids.stream().filter(EXISTING_SEATS::containsKey).count();
            });

    lenient()
        .when(bidderConfigRepository.countByPidAndCompany_buyerGroups_pidIn(any(), anySet()))
        .then(
            invocation -> {
              Long bidder = (Long) invocation.getArguments()[0];
              BidderSeat bidderSeat = EXISTING_SEATS.get(bidder);
              if (bidderSeat == null) {
                return 0;
              }
              Set<Long> groups = (Set<Long>) invocation.getArguments()[1];
              return groups.stream().filter(bidderSeat.getBuyerGroups()::contains).count();
            });

    lenient()
        .when(bidderConfigRepository.countByPidAndCompany_buyerSeats_seatIn(any(), anySet()))
        .then(
            invocation -> {
              Long bidder = (Long) invocation.getArguments()[0];
              BidderSeat bidderSeat = EXISTING_SEATS.get(bidder);
              if (bidderSeat == null) {
                return 0;
              }
              Set<String> seats = (Set<String>) invocation.getArguments()[1];
              return seats.stream().filter(bidderSeat.getSeats()::contains).count();
            });
  }

  private void mockGlobalConfigService(boolean seatExistenceValidationEnabled) {
    lenient()
        .when(
            globalConfigService.getBooleanValue(
                GlobalConfigProperty.BUYER_SEAT_EXISTENCE_VALIDATION_ENABLED))
        .thenReturn(seatExistenceValidationEnabled);
  }

  private static BidderSeat buildBidderSeats(
      Long buyerCompanyPid, Set<Long> bidders, Long bidder, Set<Long> groups, Set<String> seats) {
    BidderSeat bidderSeat = new BidderSeat();
    bidderSeat.setBuyerCompany(buyerCompanyPid);
    bidderSeat.setBidders(bidders);
    bidderSeat.setBidder(bidder);
    bidderSeat.setBuyerGroups(groups);
    bidderSeat.setSeats(seats);
    return bidderSeat;
  }

  private RuleTargetDTO createBuyerSeatTarget(String data) {
    return RuleTargetDTO.builder()
        .pid(1L)
        .version(1)
        .status(Status.ACTIVE)
        .targetType(RuleTargetType.BUYER_SEATS)
        .matchType(MatchType.INCLUDE_LIST)
        .data(data)
        .build();
  }

  private static Map<Long, List<Long>> createBuyerCompanyPids() {
    return Map.of(1L, List.of(11L, 21L, 31L), 2L, List.of(21L, 22L, 23L));
  }

  private static Map<Long, BidderSeat> createExistingSeats() {
    Map<Long, BidderSeat> existingSeats = new HashMap<>();
    existingSeats.put(
        101L,
        buildBidderSeats(
            null, null, 101L, Sets.newHashSet(11L, 12L, 13L), Sets.newHashSet("seat14", "seat15")));
    existingSeats.put(
        102L,
        buildBidderSeats(
            null, null, 102L, Sets.newHashSet(21L, 22L, 23L), Sets.newHashSet("seat24", "seat25")));
    existingSeats.put(
        103L,
        buildBidderSeats(
            null, null, 103L, Sets.newHashSet(31L, 32L, 33L), Sets.newHashSet("seat34", "seat35")));
    existingSeats.put(
        104L,
        buildBidderSeats(
            1L,
            Sets.newHashSet(11L, 21L, 31L),
            null,
            Sets.newHashSet(11L, 12L, 13L),
            Sets.newHashSet("seat14", "seat15", "seat16", "seat17")));
    existingSeats.put(
        105L,
        buildBidderSeats(
            2L,
            Sets.newHashSet(21L, 22L, 23L),
            null,
            Sets.newHashSet(21L, 22L, 23L),
            Sets.newHashSet("seat24", "seat25")));
    return existingSeats;
  }
}

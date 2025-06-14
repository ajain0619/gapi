package com.nexage.app.util.assemblers.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.util.CustomObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleTargetAssemblerTest {

  @Mock private BidderConfigRepository bidderConfigRepository;
  @Spy private CustomObjectMapper objectMapper;
  @InjectMocks private RuleTargetAssembler assembler;

  @Test
  void shouldReturnRuleTargetWithNoDataWhenRuleTargetIsBuyerSeatTypeAndTargetDataIsEmpty() {
    // given
    String targetData = "";
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertNull(actual.getData());
  }

  @Test
  void shouldReturnRuleTargetWithNoDataWhenRuleTargetIsBuyerSeatTypeAndTargetDataIsNull() {
    // given
    String targetData = null;
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertNull(actual.getData());
  }

  @Test
  void shouldReturnRuleTargetIgnoringNewLogicWhenRuleTargetIsNotBuyerSeatType() {
    // given
    String targetData = "[{\"bidder\":1000}]";
    RuleTargetType targetType = RuleTargetType.BIDDER;

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertEquals("[{\"bidder\":1000}]", actual.getData());
  }

  @Test
  void shouldReturnRuleTargetWithNewFieldsWhenRuleTargetIsBuyerSeatTypeOldFormatWithBidderOnly() {
    // given
    String targetData = "[{\"bidder\":1000}]";
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;
    when(bidderConfigRepository.findCompanyPidByPid(1000L)).thenReturn(10L);

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertEquals("[{\"buyerCompany\":10,\"bidders\":[1000],\"bidder\":1000}]", actual.getData());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[{\"buyerCompany\":100}]",
        "[{\"buyerCompany\":100,\"bidders\":[1001,1002]}]",
        "[{\"buyerCompany\":100,\"seats\":[\"seat14\"]}]",
        "[{\"buyerCompany\":11,\"bidders\":[1001],\"bidder\":1001,\"buyerGroups\":[14,15]}]"
      })
  void shouldReturnRuleTargetWithNotChangedTargetData(String targetData) {
    // given
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertEquals(targetData, actual.getData());
  }

  @Test
  void shouldReturnSingleRuleTargetWithNewFieldsWhenRuleTargetIsOldFormat() {
    // given
    String targetData = "[{\"bidder\":1000,\"buyerGroups\":[11,13],\"seats\":[\"seat14\"]}]";
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;
    when(bidderConfigRepository.findCompanyPidByPid(1000L)).thenReturn(10L);

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertEquals(1000L, (long) actual.getPid());
    assertEquals("ACTIVE", actual.getStatus().toString());
    assertEquals(
        "[{\"buyerCompany\":10,\"bidders\":[1000],\"bidder\":1000,\"buyerGroups\":[11,13],\"seats\":[\"seat14\"]}]",
        actual.getData());
  }

  @Test
  void shouldReturnMultipleRuleTargetsWithNewFieldsWhenRuleTargetsAreOldFormat() {
    // given
    String targetData =
        "[{\"bidder\":1000,\"buyerGroups\":[11,13],\"seats\":[\"seat14\",\"seat15\"]}, {\"bidder\":10001,\"buyerGroups\":[14,15],\"seats\":[\"seat16\",\"seat17\"]}]";
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;
    when(bidderConfigRepository.findCompanyPidByPid(1000L)).thenReturn(10L);
    when(bidderConfigRepository.findCompanyPidByPid(10001L)).thenReturn(20L);

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertEquals(
        "[{\"buyerCompany\":10,\"bidders\":[1000],\"bidder\":1000,\"buyerGroups\":[11,13],\"seats\":[\"seat14\",\"seat15\"]},{\"buyerCompany\":20,\"bidders\":[10001],\"bidder\":10001,\"buyerGroups\":[14,15],\"seats\":[\"seat16\",\"seat17\"]}]",
        actual.getData());
  }

  @Test
  void shouldReturnMultipleRuleTargetsWithNewFieldsWhenRuleTargetsAreOldFormatWithBidderOnly() {
    // given
    String targetData = "[{\"bidder\":1000}, {\"bidder\":10001}]";
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;
    when(bidderConfigRepository.findCompanyPidByPid(1000L)).thenReturn(10L);
    when(bidderConfigRepository.findCompanyPidByPid(10001L)).thenReturn(20L);

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertEquals(
        "[{\"buyerCompany\":10,\"bidders\":[1000],\"bidder\":1000},{\"buyerCompany\":20,\"bidders\":[10001],\"bidder\":10001}]",
        actual.getData());
  }

  @Test
  void shouldReturnMultipleRuleTargetsWithNewFieldsWhenRuleTargetsAreBothOldAndNewFormat() {
    // given
    String targetData = "[{\"bidder\":1000}, {\"buyerCompany\":20}]";
    when(bidderConfigRepository.findCompanyPidByPid(1000L)).thenReturn(10L);
    RuleTargetType targetType = RuleTargetType.BUYER_SEATS;

    // when
    RuleTargetDTO actual = assembler.make(buildRuleTarget(targetType, targetData));

    // then
    assertNotNull(actual);
    assertEquals(
        "[{\"buyerCompany\":10,\"bidders\":[1000],\"bidder\":1000},{\"buyerCompany\":20}]",
        actual.getData());
  }

  private RuleTarget buildRuleTarget(RuleTargetType targetType, String targetData) {
    RuleTarget entity = new RuleTarget();
    entity.setPid(1000L);
    entity.setRuleTargetType(targetType);
    entity.setMatchType(MatchType.INCLUDE_LIST);
    entity.setStatus(Status.ACTIVE);
    entity.setData(targetData);
    entity.setRule(new CompanyRule());

    return entity;
  }
}

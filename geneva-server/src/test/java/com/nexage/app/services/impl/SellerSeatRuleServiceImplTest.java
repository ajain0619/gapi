package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeat;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatRule;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatRuleDto;
import static com.nexage.app.web.support.TestObjectsFactory.randomLong;
import static com.nexage.app.web.support.TestObjectsFactory.randomPid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.nexage.admin.core.model.SellerSeatRule;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.admin.core.repository.SellerSeatRuleRepository;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.rule.RuleTargetDataConverter;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.CustomObjectMapper;
import com.ssp.geneva.common.error.exception.GenevaException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Optional;
import java.util.Set;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SellerSeatRuleServiceImplTest {

  @Mock private UserContext userContext;
  @Mock private SellerSeatRuleRepository sellerSeatRuleRepository;
  @Mock private SellerSeatRepository sellerSeatRepository;
  private BidderConfigRepository bidderConfigRepository =
      Mockito.mock(BidderConfigRepository.class);

  @Spy
  private RuleTargetDataConverter ruleTargetDataConverter =
      new RuleTargetDataConverter(new CustomObjectMapper(), bidderConfigRepository);

  @InjectMocks private SellerSeatRuleServiceImpl service;

  private static final long DEFAULT_SELLER_SEAT_ID = 12345L;
  private static final long DEFAULT_RULE_ID = 23456L;

  @BeforeEach
  void setUp() {
    lenient().when(userContext.isOcAdminNexage()).thenReturn(true);
  }

  @Test
  void shouldFindRules() {
    // given
    Long sellerSeatPid = 123L;
    String ruleTypes = "BRAND_PROTECTION";
    String statuses = "ACTIVE,INACTIVE";
    Pageable pageable = PageRequest.of(0, 10);
    ImmutableList<SellerSeatRule> rules =
        ImmutableList.of(
            new SellerSeatRule() {
              {
                this.setPid(12L);
                this.setSellerSeatPid(sellerSeatPid);
              }
            },
            new SellerSeatRule() {
              {
                this.setPid(23L);
                this.setSellerSeatPid(sellerSeatPid);
              }
            });

    given(sellerSeatRuleRepository.findAll(any(Specification.class), eq(pageable)))
        .willReturn(new PageImpl<>(rules, pageable, rules.size()));

    // when
    Page<SellerSeatRuleDTO> result =
        service.findRulesInSellerSeat(sellerSeatPid, ruleTypes, statuses, null, null, pageable);

    // then
    assertEquals(2, result.getContent().size());
    assertEquals(rules.get(0).getPid(), result.getContent().get(0).getPid());
    assertEquals(rules.get(1).getPid(), result.getContent().get(1).getPid());
  }

  @Test
  void shouldThrowWhenSearchingForRulesAndSearchSpecificationCannotBeCreated() {
    Long sellerSeatPid = 123L;
    Pageable pageable = PageRequest.of(0, 10);
    var queryFields = Set.of("pid");
    var queryTerm = "foo";

    assertThrows(
        GenevaValidationException.class,
        () ->
            service.findRulesInSellerSeat(
                sellerSeatPid, null, null, queryFields, queryTerm, pageable));
  }

  @Test
  void shouldFindRule() {
    // given
    Long sellerSeatPid = 123L;
    SellerSeatRule rule = createRule(12L, sellerSeatPid);

    given(sellerSeatRuleRepository.findOne(any(Specification.class))).willReturn(Optional.of(rule));

    // when
    SellerSeatRuleDTO sellerSeatRuleDTO = service.findSellerSeatRule(sellerSeatPid, 12L);

    // then
    assertNotNull(sellerSeatRuleDTO);
    assertNotNull(sellerSeatRuleDTO.getPid());
    assertNotNull(sellerSeatRuleDTO.getSellerSeatPid());
    assertEquals(sellerSeatRuleDTO.getSellerSeatPid(), rule.getSellerSeatPid());
    assertEquals(sellerSeatRuleDTO.getPid(), rule.getPid());
  }

  @Test
  void shouldFailToFindNonexistentRule() {
    // given
    Long sellerSeatPid = 123L;
    SellerSeatRule rule = createRule(12L, sellerSeatPid);

    given(sellerSeatRuleRepository.findOne(any(Specification.class))).willReturn(Optional.empty());

    var exception =
        assertThrows(
            GenevaValidationException.class, () -> service.findSellerSeatRule(sellerSeatPid, 12L));

    assertEquals(ServerErrorCodes.SERVER_RULE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldSaveRules() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto(null, DEFAULT_SELLER_SEAT_ID, null);
    given(sellerSeatRuleRepository.save(any(SellerSeatRule.class)))
        .willAnswer(
            a -> {
              SellerSeatRule rule = (SellerSeatRule) a.getArguments()[0];
              rule.setPid(randomLong());
              rule.setVersion(0);
              return rule;
            });
    given(sellerSeatRepository.findById(anyLong())).willReturn(Optional.of(createSellerSeat()));
    given(sellerSeatRuleRepository.findOne(any(Specification.class))).willReturn(Optional.empty());
    SellerSeatRuleDTO savedRuleDto = service.save(seatRuleDto.getSellerSeatPid(), seatRuleDto);
    assertNotNull(savedRuleDto);
    assertNotNull(savedRuleDto.getPid());
    assertNotNull(savedRuleDto.getVersion());
    assertNotNull(savedRuleDto.getIntendedActions());
    assertFalse(savedRuleDto.getIntendedActions().isEmpty());
    assertNotNull(savedRuleDto.getTargets());
    assertFalse(savedRuleDto.getTargets().isEmpty());
    assertEquals(seatRuleDto.getStatus(), savedRuleDto.getStatus());
    assertEquals(seatRuleDto.getSellerSeatPid(), savedRuleDto.getSellerSeatPid());
    assertEquals(seatRuleDto.getDescription(), savedRuleDto.getDescription());
    assertEquals(seatRuleDto.getName(), savedRuleDto.getName());
  }

  @Test
  void shouldFailWhenSavingRuleWithDuplicateName() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto(null, DEFAULT_SELLER_SEAT_ID, null);
    SellerSeatRule sellerSeatRule = createSellerSeatRule();
    given(sellerSeatRuleRepository.findOne(any(Specification.class)))
        .willReturn(Optional.of(sellerSeatRule));
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.save(sellerSeatPid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_RULE_DUPLICATE_NAME, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenSavingRuleWithForbiddenType() {
    SellerSeatRuleDTO seatRuleDto =
        createSellerSeatRuleDto(null, DEFAULT_SELLER_SEAT_ID, null, RuleType.EXPERIMENTATION);
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.save(sellerSeatPid, seatRuleDto));

    assertEquals(
        ServerErrorCodes.SERVER_SELLER_SEAT_RULE_TYPE_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenSavingRuleWithPidMismatch() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto();
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.save(sellerSeatPid + 1, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_PIDS_MISMATCH, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenSavingRuleWithNonExistingSellerSeat() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto(null, DEFAULT_SELLER_SEAT_ID, null);
    given(sellerSeatRepository.findById(anyLong())).willReturn(Optional.empty());
    given(sellerSeatRuleRepository.findOne(any(Specification.class))).willReturn(Optional.empty());
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.save(sellerSeatPid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldUpdateRules() {
    SellerSeatRule sellerSeatRule = createSellerSeatRule();
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto(sellerSeatRule);
    given(sellerSeatRuleRepository.saveAndFlush(any(SellerSeatRule.class)))
        .willReturn(sellerSeatRule);
    given(sellerSeatRepository.existsById(anyLong())).willReturn(true);
    given(sellerSeatRuleRepository.findOne(any(Specification.class)))
        .willReturn(Optional.of(sellerSeatRule));

    SellerSeatRuleDTO savedRuleDto =
        service.update(seatRuleDto.getSellerSeatPid(), seatRuleDto.getPid(), seatRuleDto);
    assertNotNull(savedRuleDto);
    assertNotNull(savedRuleDto.getPid());
    assertNotNull(savedRuleDto.getVersion());
    assertNotNull(savedRuleDto.getIntendedActions());
    assertFalse(savedRuleDto.getIntendedActions().isEmpty());
    assertNotNull(savedRuleDto.getTargets());
    assertFalse(savedRuleDto.getTargets().isEmpty());
    assertEquals(seatRuleDto.getStatus(), savedRuleDto.getStatus());
    assertEquals(seatRuleDto.getSellerSeatPid(), savedRuleDto.getSellerSeatPid());
    assertEquals(seatRuleDto.getDescription(), savedRuleDto.getDescription());
    assertEquals(seatRuleDto.getName(), savedRuleDto.getName());
  }

  @Test
  void shouldFailWhenUpdatingRuleWithNullPid() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto();
    ReflectionTestUtils.setField(seatRuleDto, "pid", null);
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    Long pid = seatRuleDto.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.update(sellerSeatPid, pid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_RULE_PID_IS_NULL, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenUpdatingRuleWithWrongPidInPayload() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto();
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> service.update(sellerSeatPid, DEFAULT_RULE_ID, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_PIDS_MISMATCH, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenUpdatingRuleWithWrongSellerSeatPidInPayload() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto();
    Long pid = seatRuleDto.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> service.update(DEFAULT_SELLER_SEAT_ID, pid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_PIDS_MISMATCH, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenUpdatingRuleWithDuplicateName() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto();
    SellerSeatRule sellerSeatRule = createSellerSeatRule();
    given(sellerSeatRuleRepository.findOne(any(Specification.class)))
        .willReturn(Optional.of(sellerSeatRule));
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    Long pid = seatRuleDto.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.update(sellerSeatPid, pid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_RULE_DUPLICATE_NAME, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenUpdatingRuleAndSellerSeatNotFound() {
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto();
    given(sellerSeatRuleRepository.findOne(any(Specification.class))).willReturn(Optional.empty());
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    Long pid = seatRuleDto.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.update(sellerSeatPid, pid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenUpdatingRuleAndRuleNotFound() {

    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto();
    given(sellerSeatRepository.existsById(anyLong())).willReturn(true);
    given(sellerSeatRuleRepository.findOne(any(Specification.class))).willReturn(Optional.empty());
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    Long pid = seatRuleDto.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.update(sellerSeatPid, pid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_RULE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldFailWhenUpdatingRuleWithVersionMismatch() {
    SellerSeatRule sellerSeatRule = createSellerSeatRule();
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto(sellerSeatRule);
    sellerSeatRule.setVersion(sellerSeatRule.getVersion() + 1);

    given(sellerSeatRepository.existsById(anyLong())).willReturn(true);
    given(sellerSeatRuleRepository.findOne(any(Specification.class)))
        .willReturn(Optional.of(sellerSeatRule));
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    Long pid = seatRuleDto.getPid();
    assertThrows(StaleStateException.class, () -> service.update(sellerSeatPid, pid, seatRuleDto));
  }

  @Test
  void shouldFailWhenUpdatingNotOwnedRule() {
    SellerSeatRule sellerSeatRule = createSellerSeatRule();
    SellerSeatRuleDTO seatRuleDto = createSellerSeatRuleDto(sellerSeatRule);
    sellerSeatRule.setSellerSeatPid(sellerSeatRule.getSellerSeatPid() + 1);

    given(sellerSeatRepository.existsById(anyLong())).willReturn(true);
    given(sellerSeatRuleRepository.findOne(any(Specification.class)))
        .willReturn(Optional.of(sellerSeatRule));
    Long sellerSeatPid = seatRuleDto.getSellerSeatPid();
    Long pid = seatRuleDto.getPid();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> service.update(sellerSeatPid, pid, seatRuleDto));

    assertEquals(ServerErrorCodes.SERVER_FORBIDDEN_TO_EDIT, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenNoRuleWithPid() {

    final long sellerSeatId = randomPid();
    final long sellerSeatRulePid = randomPid();
    given(sellerSeatRuleRepository.findOne(any(Specification.class))).willReturn(Optional.empty());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> {
              service.delete(sellerSeatId, sellerSeatRulePid);
            });
    assertEquals(ServerErrorCodes.SERVER_RULE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenInvalidSearchField() {
    Set<String> queryFields = Sets.newHashSet("invalid");
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> service.findRulesInSellerSeat(null, null, null, queryFields, "whatever", null));

    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenQueryPassedWithInvalidRuleType() {
    // given
    String ruleType = "INVALID_RULE_TYPE";

    // when
    GenevaException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> service.findRulesInSellerSeat(1L, ruleType, null, null, null, null));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenQueryPassedWithInvalidStatus() {
    // given
    String invalidStatus = "INVALID_STATUS";

    // when
    GenevaException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> service.findRulesInSellerSeat(1L, null, invalidStatus, null, null, null));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenQueryPassedWithStatusDeleted() {
    // given
    String status = "DELETED";

    // when
    GenevaException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> service.findRulesInSellerSeat(1L, null, status, null, null, null));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldDeleteRuleWhenDeletingExistingRule() {
    service = spy(service);
    SellerSeatRule sellerSeatRule = createSellerSeatRule();
    final long sellerSeatId = sellerSeatRule.getSellerSeatPid();
    final long sellerSeatRulePid = sellerSeatRule.getPid();
    given(sellerSeatRuleRepository.findOne(any(Specification.class)))
        .willReturn(Optional.of(sellerSeatRule));

    SellerSeatRuleDTO deleted = service.delete(sellerSeatId, sellerSeatRulePid);

    assertEquals(sellerSeatRulePid, deleted.getPid().longValue());
    verify(sellerSeatRuleRepository).delete(sellerSeatRule);
    verify(service).validateWriteAccess();
  }

  private SellerSeatRule createRule(Long pid, Long sellerSeatPid) {
    return new SellerSeatRule() {
      {
        this.setPid(pid);
        this.setSellerSeatPid(sellerSeatPid);
      }
    };
  }
}

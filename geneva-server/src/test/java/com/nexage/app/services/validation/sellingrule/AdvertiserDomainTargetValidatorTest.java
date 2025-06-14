package com.nexage.app.services.validation.sellingrule;

import static com.nexage.app.services.validation.sellingrule.AdvertiserDomainTargetValidator.ADOMAIN_TAG_CATEGORY_PID;
import static com.nexage.app.services.validation.sellingrule.AdvertiserDomainTargetValidator.MAX_TAG_VALUES_LENGTH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvertiserDomainTargetValidatorTest {

  private static final Long INVALID_TAG_ID = 123L;
  private static final Long VALID_TAG_ID = 124L;

  @Spy private ObjectMapper objectMapper;
  @Mock private BrandProtectionTagRepository tagRepository;
  @InjectMocks private AdvertiserDomainTargetValidator advertiserDomainTargetValidator;

  @BeforeEach
  void setUp() {
    lenient()
        .when(tagRepository.findById(VALID_TAG_ID))
        .thenReturn(Optional.of(aBrandProtectionTag(VALID_TAG_ID, ADOMAIN_TAG_CATEGORY_PID)));
    lenient()
        .when(tagRepository.findById(INVALID_TAG_ID))
        .thenReturn(Optional.of(aBrandProtectionTag(INVALID_TAG_ID, 6L)));
  }

  @Test
  void shouldNotThrowExceptionWithValidExactMatchAdvertiserDomainTarget() {
    RuleTargetDTO validationTarget =
        ruleWithExactMatchAdvertiserDomainTargetData(
            "[{\"tag_id\":124,\"tag_values\":[\"a.com\",\"a-A-9.com\",\"ab.aa\",\"a.ac\"]}]");

    assertDoesNotThrow(() -> advertiserDomainTargetValidator.accept(validationTarget));
  }

  @Test
  void shouldNotThrowExceptionWithValidWildcardAdvertiserDomainTarget() {
    RuleTargetDTO validationTarget =
        ruleWithWildcardAdvertiserDomainTargetData(
            "[{\"tag_id\":124,\"tag_values\":[\"a.a\",\"abcde\",\"a.ac\"]}]");

    assertDoesNotThrow(() -> advertiserDomainTargetValidator.accept(validationTarget));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "[{\"tag_id\":124,\"tag_values\":[]}]", // Empty Tag Values
        "[{\"tag_id\":124,\"tag_values\":[\"a.aa\"]}, {\"tag_id\":124,\"tag_values\":[\"a.aa\"]}]", // More than one tag
        "[{\"tag_id\":null,\"tag_values\":[\"a.aa\"]}]", // Missing tag id
        "[{\"tag_id\":125,\"tag_values\":[\"a.aa\"]}]", // Tag not found
        "[{\"tag_id\":123,\"tag_values\":[\"a.aa\"]}]", // Tag with invalid category
        "[{\"tag_id\":124,\"tag_values\":[\"a.a\"]}]", // Advertiser Domain Target Does Not Match
        // The Pattern
      })
  void shouldThrowBadRequestWhenInvalidTagValues(String tag) {
    RuleTargetDTO validationTarget = ruleWithExactMatchAdvertiserDomainTargetData(tag);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> advertiserDomainTargetValidator.accept(validationTarget));

    assertEquals(
        ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenTagValuesTooLong() {
    String tooLongTagValue = RandomStringUtils.random(MAX_TAG_VALUES_LENGTH + 1, true, true);
    RuleTargetDTO validationTarget =
        ruleWithWildcardAdvertiserDomainTargetData(
            String.format("[{\"tag_id\":124,\"tag_values\":[\"%s\"]}]", tooLongTagValue));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> advertiserDomainTargetValidator.accept(validationTarget));

    assertEquals(
        ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenWildcardAdvertiserDomainTargetDoesNotMatchThePattern() {
    RuleTargetDTO validationTarget =
        ruleWithWildcardAdvertiserDomainTargetData("[{\"tag_id\":124,\"tag_values\":[\"a;;a\"]}]");

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> advertiserDomainTargetValidator.accept(validationTarget));

    assertEquals(
        ServerErrorCodes.SERVER_TARGET_INVALID_ADVERTISER_DOMAIN, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestOnNullTargetData() {
    RuleTargetDTO validationTarget = ruleWithExactMatchAdvertiserDomainTargetData(null);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> advertiserDomainTargetValidator.accept(validationTarget));

    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenInvalidJson() {
    RuleTargetDTO validationTarget = ruleWithExactMatchAdvertiserDomainTargetData("invalid json");

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> advertiserDomainTargetValidator.accept(validationTarget));

    assertEquals(
        ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_JSON_FORMAT, exception.getErrorCode());
  }

  private RuleTargetDTO ruleWithExactMatchAdvertiserDomainTargetData(String targetData) {
    return RuleTargetDTO.builder()
        .targetType(RuleTargetType.EXACT_MATCH_ADVERTISER_DOMAIN)
        .data(targetData)
        .build();
  }

  private RuleTargetDTO ruleWithWildcardAdvertiserDomainTargetData(String targetData) {
    return RuleTargetDTO.builder()
        .targetType(RuleTargetType.WILDCARD_ADVERTISER_DOMAIN)
        .data(targetData)
        .build();
  }

  private BrandProtectionTag aBrandProtectionTag(Long tagId, Long tagCategoryId) {
    BrandProtectionTag brandProtectionTag = new BrandProtectionTag();
    brandProtectionTag.setPid(tagId);
    BrandProtectionCategory category = new BrandProtectionCategory();
    category.setPid(tagCategoryId);
    brandProtectionTag.setCategory(category);
    return brandProtectionTag;
  }
}

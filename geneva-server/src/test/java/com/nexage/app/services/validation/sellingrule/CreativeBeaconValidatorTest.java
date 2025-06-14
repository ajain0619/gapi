package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.BrandProtectionCategory;
import com.nexage.admin.core.model.BrandProtectionTag;
import com.nexage.admin.core.model.BrandProtectionTagValues;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class CreativeBeaconValidatorTest {

  @Mock private TransactionTemplate transactionTemplate;
  @Mock private BrandProtectionTagRepository brandProtectionTagRepository;
  @Spy private ObjectMapper objectMapper;
  @InjectMocks private CreativeBeaconValidator brandProtectionValidator;

  @BeforeEach
  void setUp() {
    lenient()
        .when(transactionTemplate.execute(any()))
        .thenAnswer(
            a -> {
              final TransactionCallback argument = a.getArgument(0);
              argument.doInTransaction(null);
              return null;
            });
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "invalid json", // Invalid json format
        "[{\"tag_id\": 123, \"tag_values\": [\"A\", \"B\"]},{\"tag_id\": 456}]", // Json with more
        // than one entry
        "[{\"tag_id\": 123, \"tag_values\": []}]", // Json with no tag values
      })
  void shouldThrowBadRequestWithInvalidJson(String json) {
    RuleTargetDTO targetDTO = creativeBeaconRuleTarget(json);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionValidator.accept(targetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON_FORMAT,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenTagIdDoesNotExist() {
    RuleTargetDTO targetDTO =
        creativeBeaconRuleTarget("[{\"tag_id\": 123, \"tag_values\": [\"A\", \"B\"]}]");
    lenient().when(brandProtectionTagRepository.getOne(any())).thenReturn(null);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionValidator.accept(targetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenTargetTagIdIsNotRelatedToCreativeBeaconCategory() {
    RuleTargetDTO targetDTO =
        creativeBeaconRuleTarget("[{\"tag_id\": 123, \"tag_values\": [\"A\", \"B\"]}]");
    given(brandProtectionTagRepository.getOne(123L)).willReturn(notCreativeBeacon());

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionValidator.accept(targetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON_CATEGORY,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenIllegalValueInPayload() {

    RuleTargetDTO targetDTO =
        creativeBeaconRuleTarget("[{\"tag_id\": 123, \"tag_values\": [\"C\", \"B\"]}]");
    lenient()
        .when(brandProtectionTagRepository.getOne(123L))
        .thenReturn(aCreativeBeacon(123L, "A", "B"));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> brandProtectionValidator.accept(targetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_RULE_TARGET_DATA_INVALID_CREATIVE_BEACON, exception.getErrorCode());
  }

  @Test
  void shouldNotThrowExceptionWhenAllValidValues() {
    RuleTargetDTO targetDTO =
        creativeBeaconRuleTarget("[{\"tag_id\": 123, \"tag_values\": [\"A\", \"B\"]}]");

    lenient()
        .when(brandProtectionTagRepository.getOne(123L))
        .thenReturn((aCreativeBeacon(123L, "A", "B")));

    assertDoesNotThrow(() -> brandProtectionValidator.accept(targetDTO));
  }

  @Test
  void shouldNotThrowExceptionWhenValidSubsetOfValues() {
    RuleTargetDTO targetDTO =
        creativeBeaconRuleTarget("[{\"tag_id\": 123, \"tag_values\": [\"A\", \"B\"]}]");

    lenient()
        .when(brandProtectionTagRepository.getOne(123L))
        .thenReturn(aCreativeBeacon(123L, "A", "B", "C"));

    assertDoesNotThrow(() -> brandProtectionValidator.accept(targetDTO));
  }

  private RuleTargetDTO creativeBeaconRuleTarget(String jsonPayload) {
    return RuleTargetDTO.builder()
        .targetType(RuleTargetType.CREATIVE_BEACON)
        .data(jsonPayload)
        .build();
  }

  private BrandProtectionTag notCreativeBeacon() {
    BrandProtectionTag brandProtectionTag = new BrandProtectionTag();
    brandProtectionTag.setCategory(TestObjectsFactory.createBrandProtectionCategory());
    return brandProtectionTag;
  }

  private BrandProtectionTag aCreativeBeacon(Long tagId, String... values) {
    BrandProtectionTag brandProtectionTag = new BrandProtectionTag();
    brandProtectionTag.setPid(tagId);
    BrandProtectionCategory category = new BrandProtectionCategory();
    category.setPid(CreativeBeaconValidator.CREATIVE_BEACON_CATEGORY_PID);
    brandProtectionTag.setCategory(category);
    Set<BrandProtectionTagValues> tagValues = new HashSet<>();
    for (String value : values) {
      BrandProtectionTagValues element = new BrandProtectionTagValues();
      element.setValue(value);
      tagValues.add(element);
    }
    brandProtectionTag.setTagValues(tagValues);
    return brandProtectionTag;
  }
}

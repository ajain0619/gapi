package com.nexage.app.services.validation.sellingrule;

import static com.nexage.app.services.validation.sellingrule.CreativeLanguageValidator.CREATIVE_LANG_CATEGORY_ID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.repository.BrandProtectionTagRepository;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Set;
import java.util.StringJoiner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeLanguageValidatorTest {

  private static final String FIELD_NAME = "tag_id";

  @Mock private BrandProtectionTagRepository brandProtectionTagRepository;
  @Spy private ObjectMapper objectMapper;

  @InjectMocks private CreativeLanguageValidator validator;

  private String buildDataModel(Set<Long> testIds) {
    StringJoiner sj = new StringJoiner(",", "[", "]");
    testIds.forEach(id -> sj.add(String.format("{\"%s\":%d}", FIELD_NAME, id)));
    return sj.toString();
  }

  @Test
  void shouldNotThrowExceptionWhenJsonDataIsPassedWithCreativeLang() {
    Set<Long> tagIds = ImmutableSet.of(125L, 126L, 127L);
    String targetData = buildDataModel(tagIds);

    given(
            brandProtectionTagRepository.countByCategoryPidAndPidIn(
                CREATIVE_LANG_CATEGORY_ID, tagIds))
        .willReturn((long) tagIds.size());

    assertDoesNotThrow(() -> validator.accept(makeDTO(targetData)));
  }

  @Test
  void shouldNotThrowExceptionWhenJsonDataIsPassedWithSingleCreativeLang() {
    Set<Long> tagIds = ImmutableSet.of(31L);
    String targetData = buildDataModel(tagIds);

    given(
            brandProtectionTagRepository.countByCategoryPidAndPidIn(
                CREATIVE_LANG_CATEGORY_ID, tagIds))
        .willReturn((long) tagIds.size());

    assertDoesNotThrow(() -> validator.accept(makeDTO(targetData)));
  }

  @Test
  void shouldThrowBadRequestWhenJsonDataIsPassedWithIncorrectCreativeLang() {
    String targetData = "[{\"some_name\":140}]";
    RuleTargetDTO ruleTargetDTO = makeDTO(targetData);
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_TARGET_INVALID_CREATIVE_LANGUAGE, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenJsonDataIsPassedWithIncorrectCreativeLang2() {
    String targetData = "{ \"element\" : \"3\" }";
    RuleTargetDTO ruleTargetDTO = makeDTO(targetData);
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_TARGET_INVALID_CREATIVE_LANGUAGE, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenJsonDataIsPassedWithNoValue() {
    RuleTargetDTO ruleTargetDTO = makeDTO(null);
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    assertEquals(ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenJsonDataIsPassedWithInvalidData() {
    RuleTargetDTO ruleTargetDTO = makeDTO("this is not a json");
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_TARGET_INVALID_CREATIVE_LANGUAGE, exception.getErrorCode());
  }

  @Test
  void shouldThrowBadRequestWhenJsonDataIsPassedWithNotExistingData() {
    Long tagId = 31L;
    given(
            brandProtectionTagRepository.countByCategoryPidAndPidIn(
                CREATIVE_LANG_CATEGORY_ID, ImmutableSet.of(tagId)))
        .willReturn(0L);
    RuleTargetDTO ruleTargetDTO = makeDTO(String.format("[{\"%s\":%d}]", FIELD_NAME, tagId));
    GenevaValidationException exception =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));

    assertEquals(
        ServerErrorCodes.SERVER_TARGET_INVALID_CREATIVE_LANGUAGE, exception.getErrorCode());
  }

  private RuleTargetDTO makeDTO(String data) {
    return RuleTargetDTO.builder().data(data).targetType(RuleTargetType.CREATIVE_LANGUAGE).build();
  }
}

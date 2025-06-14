package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.AdSource;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.security.LoginUserContext;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherSelfServiceTagMappedValuesGenerationTest {

  protected static final String TEST_PRIMARY_ID = "testPrimaryId";
  protected static final String TEST_PRIMARY_NAME = "testPrimaryName";
  protected static final String TEST_SECONDARY_ID = "testSecondaryId";
  protected static final String TEST_SECONDARY_NAME = "testSecondaryName";
  protected static final String AD_SOURCE_PRIMARY_ID_DEFAULT = "adsourcePrimaryIdDefault";

  @Mock private LoginUserContext userContext;

  @InjectMocks private PublisherSelfServiceImpl publisherSelfService;

  @Test
  void whenNullMappedParamValuesInTag_thenPrimaryIdGetsGenerated() {
    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), null);

    assertPrimaryIdGeneration(publisherTag);
    assertOtherMappedParamValuesAreNull(publisherTag);
  }

  @Test
  void whenNullMappedParamValuesInTag_thenPrimaryIdGetsGeneratedForNexageUser() {
    Mockito.when(userContext.isNexageUser()).thenReturn(true);
    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), null);

    assertPrimaryIdGeneration(publisherTag);
    assertOtherMappedParamValuesAreNull(publisherTag);
  }

  protected void assertPrimaryIdGeneration(PublisherTagDTO publisherTag) {
    assertFalse(
        StringUtils.isBlank(publisherTag.getPrimaryId()), "primary id should have been generated");
  }

  protected void assertOtherMappedParamValuesAreNull(PublisherTagDTO publisherTag) {
    assertNull(publisherTag.getPrimaryName(), "primary name should not be generated");
    assertNull(publisherTag.getSecondaryId(), "secondary id should not be generated");
    assertNull(publisherTag.getSecondaryName(), "secondary name should not be generated");
  }

  @Test
  void whenNullMappedParamValuesInTagAndNoPrimaryIdDefault_thenPrimaryIdGetsGenerated() {
    AdSource adSource = new AdSource();

    // case 1
    adSource.setPrimaryIdDefault(null);

    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), adSource);

    assertPrimaryIdGeneration(publisherTag);
    assertOtherMappedParamValuesAreNull(publisherTag);

    // case 2
    adSource.setPrimaryIdDefault(" ");

    publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), adSource);

    assertPrimaryIdGeneration(publisherTag);
    assertOtherMappedParamValuesAreNull(publisherTag);
  }

  @Test
  void
      whenNullMappedParamValuesInTagAndNoPrimaryIdDefault_thenPrimaryIdGetsGeneratedForNexageUser() {
    Mockito.when(userContext.isNexageUser()).thenReturn(true);
    AdSource adSource = new AdSource();

    // case 1
    adSource.setPrimaryIdDefault(null);

    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), adSource);

    assertPrimaryIdGeneration(publisherTag);
    assertOtherMappedParamValuesAreNull(publisherTag);

    // case 2
    adSource.setPrimaryIdDefault(" ");

    publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), adSource);

    assertPrimaryIdGeneration(publisherTag);
    assertOtherMappedParamValuesAreNull(publisherTag);
  }

  @Test
  void whenNullMappedParamValuesInTagAndPrimaryIdHasDefaultValue_thenPrimaryIdGetsDefaultValue() {
    AdSource adSource = new AdSource();
    adSource.setPrimaryIdDefault(AD_SOURCE_PRIMARY_ID_DEFAULT);

    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), adSource);

    assertEquals(
        AD_SOURCE_PRIMARY_ID_DEFAULT,
        publisherTag.getPrimaryId(),
        "primary id should be set to default");
    assertOtherMappedParamValuesAreNull(publisherTag);
  }

  @Test
  void
      whenNullMappedParamValuesInTagAndPrimaryIdHasDefaultValue_thenPrimaryIdGetsDefaultValueForNexageUser() {
    Mockito.when(userContext.isNexageUser()).thenReturn(true);
    AdSource adSource = new AdSource();
    adSource.setPrimaryIdDefault(AD_SOURCE_PRIMARY_ID_DEFAULT);

    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), new PublisherTagDTO(), adSource);

    assertEquals(
        AD_SOURCE_PRIMARY_ID_DEFAULT,
        publisherTag.getPrimaryId(),
        "primary id should be set to default");
    assertOtherMappedParamValuesAreNull(publisherTag);
  }

  @Test
  void whenPopulatedMappedParamValues_thenNoGenerationOfPrimaryId() {
    PublisherTagDTO tag = new PublisherTagDTO();
    tag.setPrimaryId(TEST_PRIMARY_ID);
    tag.setPrimaryName(TEST_PRIMARY_NAME);
    tag.setSecondaryId(TEST_SECONDARY_ID);
    tag.setSecondaryName(TEST_SECONDARY_NAME);

    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), tag, null);

    assertEquals(
        TEST_PRIMARY_ID, publisherTag.getPrimaryId(), "primary id should be left untouched");
    assertEquals(
        TEST_PRIMARY_NAME, publisherTag.getPrimaryName(), "primary name should be left untouched");
    assertEquals(
        TEST_SECONDARY_ID, publisherTag.getSecondaryId(), "secondary id should be left untouched");
    assertEquals(
        TEST_SECONDARY_NAME,
        publisherTag.getSecondaryName(),
        "secondary name should be left untouched");
  }

  @Test
  void whenPopulatedMappedParamValues_thenNoGenerationOfPrimaryIdForNexageUser() {
    Mockito.when(userContext.isNexageUser()).thenReturn(true);
    PublisherTagDTO tag = new PublisherTagDTO();
    tag.setPrimaryId(TEST_PRIMARY_ID);
    tag.setPrimaryName(TEST_PRIMARY_NAME);
    tag.setSecondaryId(TEST_SECONDARY_ID);
    tag.setSecondaryName(TEST_SECONDARY_NAME);

    PublisherTagDTO publisherTag =
        publisherSelfService.getPublisherTagWithPopulatedValues(
            PublisherTagDTO.newBuilder(), tag, null);

    assertEquals(
        TEST_PRIMARY_ID, publisherTag.getPrimaryId(), "primary id should be left untouched");
    assertEquals(
        TEST_PRIMARY_NAME, publisherTag.getPrimaryName(), "primary name should be left untouched");
    assertEquals(
        TEST_SECONDARY_ID, publisherTag.getSecondaryId(), "secondary id should be left untouched");
    assertEquals(
        TEST_SECONDARY_NAME,
        publisherTag.getSecondaryName(),
        "secondary name should be left untouched");
  }
}

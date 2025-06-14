package com.nexage.app.services.validation;

import static com.nexage.admin.core.enums.PlacementCategory.INTERSTITIAL;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.PlacementCategory.MEDIUM_RECTANGLE;
import static com.nexage.admin.core.enums.PlacementCategory.NATIVE_V2;
import static com.nexage.admin.core.enums.VideoSupport.BANNER;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.AMAZON_TAM;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.GOOGLE_EB;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.PREBID_JS;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.WATCH_FANTOM;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.formattedAssociationTypeSupportedHBPartners;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.util.validator.placement.PlacementAssociationTypeValidator;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PlacementAssociationTypeValidatorTest {

  private final Long OTHER_HB = 3L;

  @Test
  void shouldReturnTrueWhenHbPartnerAttributesIsEmpty() {

    assertTrue(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            IN_ARTICLE,
            VIDEO,
            Collections.emptySet(),
            formattedAssociationTypeSupportedHBPartners));
  }

  @Test
  void shouldReturnFalseWhenInvalidAssociationTypeForBannerVideoSupport() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(GOOGLE_EB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT_BANNER);
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(GOOGLE_EB);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT_VIDEO);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(validHbPartnerAttribute, invalidHbPartnerAttribute);
    assertFalse(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            INTERSTITIAL,
            BANNER,
            hbPartnerAttributes,
            formattedAssociationTypeSupportedHBPartners));
  }

  @Test
  void shouldReturnFalseWhenInvalidAssociationTypeForInArticlePlacementCategory() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(AMAZON_TAM);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT_VIDEO);
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(AMAZON_TAM);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT_BANNER);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(validHbPartnerAttribute, invalidHbPartnerAttribute);
    assertFalse(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            IN_ARTICLE, VIDEO, hbPartnerAttributes, formattedAssociationTypeSupportedHBPartners));
  }

  @Test
  void shouldReturnFalseWhenInvalidAssociationTypeForPrebidJSIntegration() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(PREBID_JS);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT_BANNER);
    HbPartnerAssignmentDTO validHbPartnerAttribute2 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute2.setHbPartnerPid(PREBID_JS);
    validHbPartnerAttribute2.setType(AssociationType.DEFAULT_VIDEO);
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(PREBID_JS);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(
            validHbPartnerAttribute, validHbPartnerAttribute2, invalidHbPartnerAttribute);
    assertFalse(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            PlacementCategory.BANNER,
            VIDEO_AND_BANNER,
            hbPartnerAttributes,
            formattedAssociationTypeSupportedHBPartners));
  }

  @Test
  void shouldReturnFalseWhenInvalidAssociationTypeForOtherIntegration() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(GOOGLE_EB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT_VIDEO);
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(OTHER_HB);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT_VIDEO);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(validHbPartnerAttribute, invalidHbPartnerAttribute);
    assertFalse(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            IN_ARTICLE, VIDEO, hbPartnerAttributes, formattedAssociationTypeSupportedHBPartners));
  }

  @Test
  void shouldReturnTrueWhenAllHbPartnersHaveValidAssociationType() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(AMAZON_TAM);
    validHbPartnerAttribute.setType(AssociationType.NON_DEFAULT);
    HbPartnerAssignmentDTO validHbPartnerAttribute2 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute2.setHbPartnerPid(GOOGLE_EB);
    validHbPartnerAttribute2.setType(AssociationType.DEFAULT_VIDEO);
    HbPartnerAssignmentDTO validHbPartnerAttribute3 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute3.setHbPartnerPid(OTHER_HB);
    validHbPartnerAttribute3.setType(AssociationType.DEFAULT);
    HbPartnerAssignmentDTO validHbPartnerAttribute4 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute4.setHbPartnerPid(WATCH_FANTOM);
    validHbPartnerAttribute4.setType(AssociationType.DEFAULT_VIDEO);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(
            validHbPartnerAttribute,
            validHbPartnerAttribute2,
            validHbPartnerAttribute3,
            validHbPartnerAttribute4);
    assertTrue(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            MEDIUM_RECTANGLE,
            VIDEO,
            hbPartnerAttributes,
            formattedAssociationTypeSupportedHBPartners));
  }

  @Test
  void shouldReturnFalseWhenInvalidAssociationTypeForOtherIntegrationNativeV2() {
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(OTHER_HB);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT_BANNER);
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(GOOGLE_EB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT_BANNER);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(validHbPartnerAttribute, invalidHbPartnerAttribute);
    assertFalse(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            NATIVE_V2,
            VideoSupport.NATIVE,
            hbPartnerAttributes,
            formattedAssociationTypeSupportedHBPartners));
  }

  @Test
  void shouldReturnTrueWhenValidAssociationTypeForOtherHbPartnerNativeV2() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(OTHER_HB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT);
    HbPartnerAssignmentDTO validHbPartnerAttribute2 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute2.setHbPartnerPid(OTHER_HB);
    validHbPartnerAttribute2.setType(AssociationType.NON_DEFAULT);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(validHbPartnerAttribute, validHbPartnerAttribute2);
    assertTrue(
        PlacementAssociationTypeValidator.isAssociationTypeValid(
            NATIVE_V2,
            VideoSupport.NATIVE,
            hbPartnerAttributes,
            formattedAssociationTypeSupportedHBPartners));
  }
}

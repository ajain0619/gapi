package com.nexage.app.services.validation;

import static com.nexage.app.util.PlacementAssociationTypeTestUtil.AMAZON_TAM;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.GOOGLE_EB;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.PublisherAndSiteAssociationTypeConstraint;
import com.nexage.app.util.validator.PublisherAndSiteAssociationTypeValidator;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class PublisherAndSiteAssociationTypeValidatorTest extends BaseValidatorTest {
  private static final Long OTHER_HB = 3L;

  @Mock private PublisherAndSiteAssociationTypeConstraint annotation;
  @InjectMocks private PublisherAndSiteAssociationTypeValidator validator;

  @Test
  void shouldReturnTrueWhenHbPartnerAttributesIsEmpty() {
    assertTrue(validator.isValid(Collections.emptySet(), ctx));
  }

  @Test
  void shouldReturnFalseWhenHbPartnerHasInvalidAssociationType() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(OTHER_HB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT);
    HbPartnerAssignmentDTO validHbPartnerAttribute2 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute2.setHbPartnerPid(88L);
    validHbPartnerAttribute2.setType(AssociationType.DEFAULT);
    HbPartnerAssignmentDTO invalidHbPartnerAttribute = new HbPartnerAssignmentDTO();
    invalidHbPartnerAttribute.setHbPartnerPid(56L);
    invalidHbPartnerAttribute.setType(AssociationType.DEFAULT_VIDEO);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(
            validHbPartnerAttribute, validHbPartnerAttribute2, invalidHbPartnerAttribute);
    assertFalse(validator.isValid(hbPartnerAttributes, ctx));
    verify(annotation).message();
  }

  @Test
  void shouldReturnTrueWhenAllHbPartnersHaveValidAssociationType() {
    HbPartnerAssignmentDTO validHbPartnerAttribute = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute.setHbPartnerPid(OTHER_HB);
    validHbPartnerAttribute.setType(AssociationType.DEFAULT);
    HbPartnerAssignmentDTO validHbPartnerAttribute2 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute2.setHbPartnerPid(GOOGLE_EB);
    validHbPartnerAttribute2.setType(AssociationType.NON_DEFAULT);
    HbPartnerAssignmentDTO validHbPartnerAttribute3 = new HbPartnerAssignmentDTO();
    validHbPartnerAttribute3.setHbPartnerPid(AMAZON_TAM);
    validHbPartnerAttribute3.setType(AssociationType.DEFAULT);
    Set<HbPartnerAssignmentDTO> hbPartnerAttributes =
        Sets.newHashSet(
            validHbPartnerAttribute, validHbPartnerAttribute2, validHbPartnerAttribute3);
    assertTrue(validator.isValid(hbPartnerAttributes, ctx));
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(annotation.message()).thenReturn("Invalid hb partner association type");
  }
}

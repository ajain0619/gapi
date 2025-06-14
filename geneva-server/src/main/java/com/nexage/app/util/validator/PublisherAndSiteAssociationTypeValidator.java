package com.nexage.app.util.validator;

import static com.nexage.admin.core.enums.AssociationType.DEFAULT_BANNER;
import static com.nexage.admin.core.enums.AssociationType.DEFAULT_VIDEO;

import com.nexage.app.dto.HbPartnerAssignmentDTO;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;

public class PublisherAndSiteAssociationTypeValidator
    extends BaseValidator<PublisherAndSiteAssociationTypeConstraint, Set<HbPartnerAssignmentDTO>> {

  @Override
  public boolean isValid(
      Set<HbPartnerAssignmentDTO> hbPartnerAttributes, ConstraintValidatorContext context) {

    if (CollectionUtils.isEmpty(hbPartnerAttributes)) {
      return true;
    }
    boolean result = isAssociationTypeValid(hbPartnerAttributes);
    if (!result) {
      addConstraintMessage(context, "hbPartnerAttributes", getAnnotation().message());
    }
    return result;
  }

  public boolean isAssociationTypeValid(Set<HbPartnerAssignmentDTO> hbPartnerAttributes) {
    return hbPartnerAttributes.stream()
        .noneMatch(
            hbPartnerAssignmentDTO ->
                DEFAULT_BANNER.equals(hbPartnerAssignmentDTO.getType())
                    || DEFAULT_VIDEO.equals(hbPartnerAssignmentDTO.getType()));
  }
}

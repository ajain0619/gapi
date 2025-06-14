package com.nexage.app.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.AssociationType;
import lombok.Data;

@JsonInclude(NON_NULL)
@Data
public class HbPartnerAssignmentDTO {

  private String externalId;

  private Long hbPartnerPid;

  private AssociationType type;
}

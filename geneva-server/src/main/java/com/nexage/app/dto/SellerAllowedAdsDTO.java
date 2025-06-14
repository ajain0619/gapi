package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class SellerAllowedAdsDTO {
  @NotNull(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private String screenedAdId;

  @NotNull(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Long sellerPid;

  private Long allowedBy;

  private String comment;
}

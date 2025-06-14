package com.nexage.app.dto.feeadjustment;

import com.nexage.app.util.validator.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FeeAdjustmentSellerDTO {
  @Schema(title = "The pid of the associated seller")
  @NotNull
  private Long sellerPid;

  @Schema(
      title = "The name of the associated seller (Note: This value is ignored on create/update)")
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String sellerName;
}

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
public class FeeAdjustmentBuyerDTO {
  @Schema(title = "The pid of the associated buyer")
  @NotNull
  private Long buyerPid;

  @Schema(title = "The name of the associated buyer (Note: This value is ignored on create/update)")
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String buyerName;
}

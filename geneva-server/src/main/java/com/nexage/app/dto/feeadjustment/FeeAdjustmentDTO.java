package com.nexage.app.dto.feeadjustment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeAdjustmentDTO {
  @Schema(title = "A primary key for the table")
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Long pid;

  @Schema(title = "A unique name for the fee adjustment")
  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String name;

  @Schema(
      title =
          "A flag to indicate whether this fee adjustment is an include list (true) or an exclude list (false)")
  private Boolean inclusive;

  @Schema(title = "The multiplicative demand fee adjustment to apply to fee charges")
  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private Double demandFeeAdjustment;

  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private Integer version;

  @Schema(title = "A flag to enable/disable this fee adjustment")
  private Boolean enabled;

  @Schema(title = "A description of this fee adjustment")
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String description;

  @Schema(title = "the sellers to include/exclude from this fee adjustment")
  private List<@Valid FeeAdjustmentSellerDTO> feeAdjustmentSellers = new ArrayList<>();

  @Schema(title = "the buyers to which this fee adjustment applies")
  private List<@Valid FeeAdjustmentBuyerDTO> feeAdjustmentBuyers = new ArrayList<>();

  @Schema(
      title =
          "the entity name (derived from buyers) for this fee adjustment (Note: This value is ignored on create/update)")
  private String entityName;
}

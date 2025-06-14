package com.nexage.app.dto.postauctiondiscount;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class PostAuctionDiscountDspSeatDTO {

  @Schema(title = "A primary key from the buyer seat table")
  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private Long pid;

  @Schema(title = "A unique name for the buyer seat")
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  @Size(min = 1, max = 255, message = ValidationMessages.WRONG_STRING_LENGTH)
  private String name;
}

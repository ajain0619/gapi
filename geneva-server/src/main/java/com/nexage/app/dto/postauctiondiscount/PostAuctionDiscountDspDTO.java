package com.nexage.app.dto.postauctiondiscount;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostAuctionDiscountDspDTO {

  @Schema(title = "The company pid that all the buyer seats belong too")
  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private Long companyPid;

  @Schema(title = "The company name that all the buyer seats belong too")
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  private String companyName;

  @Schema(title = "A list of buyer seats that all have the same company pid")
  private List<@Valid PostAuctionDiscountDspSeatDTO> dspSeats;
}

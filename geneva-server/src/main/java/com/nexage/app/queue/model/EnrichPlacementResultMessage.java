package com.nexage.app.queue.model;

import com.nexage.app.util.validator.ValidationMessages;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnrichPlacementResultMessage implements SyncMessage {

  @NotBlank(message = ValidationMessages.WRONG_IS_EMPTY)
  @Digits(integer = Integer.MAX_VALUE, fraction = 0)
  private String placementPid;

  @NotBlank(message = ValidationMessages.WRONG_IS_EMPTY)
  @Digits(integer = Integer.MAX_VALUE, fraction = 0)
  private String companyPid;

  @NotBlank(message = ValidationMessages.WRONG_IS_EMPTY)
  @Digits(integer = Integer.MAX_VALUE, fraction = 0)
  private String sitePid;

  @NotBlank(message = ValidationMessages.WRONG_IS_EMPTY)
  private String sectionPid;
}

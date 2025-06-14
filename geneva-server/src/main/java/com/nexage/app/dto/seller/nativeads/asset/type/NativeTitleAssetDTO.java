package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;
import static com.nexage.app.util.validator.ValidationMessages.WRONG_NUMBER_MIN;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NativeTitleAssetDTO extends NativeAssetDTO {

  @Valid private Title title;

  public NativeTitleAssetDTO() {
    this.title = new Title();
  }

  @Data
  public class Title {
    @Min(value = 1, message = WRONG_NUMBER_MIN)
    @NotNull(message = WRONG_IS_EMPTY)
    private Integer maxLength;
  }
}

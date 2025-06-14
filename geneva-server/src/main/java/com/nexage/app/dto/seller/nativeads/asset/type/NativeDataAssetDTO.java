package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;
import static com.nexage.app.util.validator.ValidationMessages.WRONG_NUMBER_MIN;

import com.nexage.app.dto.seller.nativeads.enums.DataType;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

@lombok.Data
@EqualsAndHashCode(callSuper = true)
public class NativeDataAssetDTO extends NativeAssetDTO {

  @Valid private Data data;

  public NativeDataAssetDTO() {
    this.data = new NativeDataAssetDTO.Data();
  }

  @lombok.Data
  public class Data {
    @Min(value = 1, message = WRONG_NUMBER_MIN)
    private Integer maxLength;

    @NotNull(message = WRONG_IS_EMPTY)
    private DataType type;
  }
}

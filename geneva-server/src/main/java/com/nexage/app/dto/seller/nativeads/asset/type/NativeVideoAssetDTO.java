package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_ELEMENT_IS_EMPTY;
import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;
import static com.nexage.app.util.validator.ValidationMessages.WRONG_NUMBER_MIN;

import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = true)
public class NativeVideoAssetDTO extends NativeAssetDTO {

  @Valid private Video video;

  public NativeVideoAssetDTO() {
    this.video = new Video();
  }

  @Data
  public class Video {
    @NotNull(message = WRONG_IS_EMPTY)
    @Min(value = 1, message = WRONG_NUMBER_MIN)
    private Integer minDuration;

    @NotNull(message = WRONG_IS_EMPTY)
    @Min(value = 1, message = WRONG_NUMBER_MIN)
    private Integer maxDuration;

    @NotEmpty(message = WRONG_ELEMENT_IS_EMPTY)
    private Set<VideoProtocols> protocols;
  }
}

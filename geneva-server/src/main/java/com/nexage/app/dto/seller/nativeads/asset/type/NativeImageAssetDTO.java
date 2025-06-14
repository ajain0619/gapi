package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NativeImageAssetDTO extends NativeAssetDTO {

  private static final String SOURCE_URL = "-sourceUrl";
  private Image image;

  public NativeImageAssetDTO() {
    this.image = new Image();
  }

  @Override
  @JsonIgnore
  protected String getPlaceholderSuffix() {
    return SOURCE_URL;
  }

  @Data
  public class Image {
    @NotNull(message = WRONG_IS_EMPTY)
    private AssetImageType type;

    private Integer width;
    private Integer widthMinimum;
    private Integer height;
    private Integer heightMinimum;
  }
}

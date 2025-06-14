package com.nexage.app.dto.seller.nativeads.asset.type;

import static com.nexage.app.util.validator.ValidationMessages.WRONG_IS_EMPTY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nexage.admin.core.enums.nativeads.NativeAssetType;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    visible = true,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(value = NativeTitleAssetDTO.class, name = "TITLE"),
  @JsonSubTypes.Type(value = NativeVideoAssetDTO.class, name = "VIDEO"),
  @JsonSubTypes.Type(value = NativeImageAssetDTO.class, name = "IMAGE"),
  @JsonSubTypes.Type(value = NativeDataAssetDTO.class, name = "DATA")
})
public abstract class NativeAssetDTO {
  private static final String VALUE = "-value";
  private static final String LINK_URL = "-linkUrl";

  @NotNull(message = WRONG_IS_EMPTY)
  private NativeAssetType type;

  @NotNull(message = WRONG_IS_EMPTY)
  private String key;

  @JsonIgnore
  public String getMandatoryPlaceholder() {
    return this.getKey() + getPlaceholderSuffix();
  }

  @JsonIgnore
  public List<String> getPlaceholders() {
    return List.of(getMandatoryPlaceholder(), this.getKey() + getPlaceholderUrlSuffix());
  }

  protected String getPlaceholderSuffix() {
    return VALUE;
  }

  protected String getPlaceholderUrlSuffix() {
    return LINK_URL;
  }
}

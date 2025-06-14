package com.nexage.app.dto.transparency;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nexage.app.util.json.TransparencyModeJsonDeserializer;

@JsonInclude(Include.NON_NULL)
public class TransparencySettingsDTO {

  @JsonDeserialize(using = TransparencyModeJsonDeserializer.class)
  private TransparencyMode transparencyMode;

  @JsonSerialize(using = ToStringSerializer.class)
  private Long idAlias;

  private String nameAlias;
  private Boolean regenerateIdAlias;

  public TransparencySettingsDTO() {}

  public TransparencySettingsDTO(
      TransparencyMode transparencyMode, Long idAlias, String nameAlias) {
    this.transparencyMode = transparencyMode;
    this.idAlias = idAlias;
    this.nameAlias = nameAlias;
  }

  public TransparencyMode getTransparencyMode() {
    return transparencyMode;
  }

  public void setTransparencyMode(TransparencyMode transparencyMode) {
    this.transparencyMode = transparencyMode;
  }

  public Long getIdAlias() {
    return idAlias;
  }

  public void setIdAlias(Long idAlias) {
    this.idAlias = idAlias;
  }

  public String getNameAlias() {
    return nameAlias;
  }

  public void setNameAlias(String nameAlias) {
    this.nameAlias = nameAlias;
  }

  public Boolean getRegenerateIdAlias() {
    return regenerateIdAlias;
  }

  public void setRegenerateIdAlias(Boolean regenerateIdAlias) {
    this.regenerateIdAlias = regenerateIdAlias;
  }
}

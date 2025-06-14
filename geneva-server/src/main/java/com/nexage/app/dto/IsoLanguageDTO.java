package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IsoLanguageDTO {
  private static final long serialVersionUID = 1L;

  @NotNull private Long pid;

  @NotNull private String languageName;

  @NotNull private String languageCode;
}

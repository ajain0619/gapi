package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.app.util.validator.ValidationMessages;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Display {

  @NotNull
  @Min(value = 1, message = ValidationMessages.WRONG_NUMBER_MIN)
  private Integer width;

  @NotNull
  @Min(value = 1, message = ValidationMessages.WRONG_NUMBER_MIN)
  private Integer height;
}

package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(Include.NON_NULL)
public class PlacementDoohDTO implements Serializable {

  @Positive(
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MIN)
  @Max(
      value = 999999999,
      groups = {CreateGroup.class, UpdateGroup.class},
      message = ValidationMessages.WRONG_NUMBER_MAX)
  private BigDecimal defaultImpressionMultiplier;

  @NotNull(groups = UpdateGroup.class, message = ValidationMessages.WRONG_IS_EMPTY)
  @Min(groups = UpdateGroup.class, value = 0, message = ValidationMessages.WRONG_NUMBER_MIN)
  @Null(groups = CreateGroup.class, message = ValidationMessages.WRONG_IS_NOT_EMPTY)
  private Integer version;
}

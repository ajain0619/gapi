package com.nexage.app.dto.smartexchangeattributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.app.util.validator.smartexchangeattributes.SmartExchangeAttributesConstraint;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SmartExchangeAttributesConstraint
@Builder(builderClassName = "Builder", setterPrefix = "with", builderMethodName = "newBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class SmartExchangeAttributesDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer version;

  @Default private Boolean smartMarginEnabled = false;
}

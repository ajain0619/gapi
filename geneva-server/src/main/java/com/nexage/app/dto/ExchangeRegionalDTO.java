package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Data transfer representation of {@link com.nexage.admin.core.model.ExchangeRegional}. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRegionalDTO implements Serializable {

  private static final long serialVersionUID = -6809637346089655147L;

  @EqualsAndHashCode.Include @ToString.Include private Long pid;

  @EqualsAndHashCode.Include @ToString.Include private String id;
}

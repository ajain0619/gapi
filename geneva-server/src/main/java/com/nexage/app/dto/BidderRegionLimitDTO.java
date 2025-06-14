package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.bidder.support.validation.annotation.CountryLetterCodes;
import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Data transfer representation of {@link com.nexage.admin.core.model.BidderRegionLimit}. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidderRegionLimitDTO implements Serializable {

  private static final long serialVersionUID = -4187973123255508460L;

  @EqualsAndHashCode.Include @ToString.Include private Long pid;

  private Integer version;

  @EqualsAndHashCode.Include
  @ToString.Include
  @Size(min = 1, max = 255)
  private String name;

  @EqualsAndHashCode.Include
  @ToString.Include
  @NotNull
  @CountryLetterCodes
  @Size(min = 1, max = 1000)
  private String countriesFilter;

  @NotNull
  @Min(value = 1)
  @Max(value = 999_999)
  private Integer requestRate;
}

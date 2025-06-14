package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.app.dto.filter.FilterListDTO;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data transfer representation of {@link
 * com.nexage.admin.core.model.filter.BidderConfigDenyAllowFilterList}.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidderConfigDenyAllowFilterListDTO implements Serializable {

  private static final long serialVersionUID = -420080142888191062L;

  @EqualsAndHashCode.Include @ToString.Include private Integer pid;

  private Integer version;

  @EqualsAndHashCode.Include @ToString.Include private FilterListDTO filterList;
}

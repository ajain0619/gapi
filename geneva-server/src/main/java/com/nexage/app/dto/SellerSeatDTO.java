package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.user.CompanyViewDTO;
import java.util.Date;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class SellerSeatDTO {
  private Long pid;
  @NotNull private String name;
  private String description;
  @NotNull private Boolean status;
  @NotNull private Integer version;
  private Date updatedOn;

  @JsonInclude(Include.NON_EMPTY)
  private Set<CompanyViewDTO> sellers;
}

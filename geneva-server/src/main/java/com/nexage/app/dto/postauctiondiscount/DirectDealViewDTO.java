package com.nexage.app.dto.postauctiondiscount;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DirectDealViewDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  @Schema(title = "The deal id that will be eligible for discount")
  private String id;

  @Schema(title = "The deal pid that will be eligible for discount")
  private Long pid;

  @Schema(title = "The deal description that will be eligible for discount")
  private String description;
}

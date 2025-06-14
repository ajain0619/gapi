package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.model.DealInventoryType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealInventoriesDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long pid;
  private String fileName;
  private DealInventoryType fileType;
  private Long dealId;
}

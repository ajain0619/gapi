package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealInventoryDownloadResponseDTO {
  private String fileName;
  private ByteArrayResource inventoryFileByteResource;
}

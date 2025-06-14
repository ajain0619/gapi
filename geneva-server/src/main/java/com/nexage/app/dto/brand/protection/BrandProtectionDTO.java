package com.nexage.app.dto.brand.protection;

import com.nexage.app.dto.CrsTagMappingDTO;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandProtectionDTO {
  private Long pid;
  private Long categoryId;
  private String name;
  private String rtbId;
  private Collection<CrsTagMappingDTO> crsTags;
  private Long parentTagPid;
}

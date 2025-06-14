package com.nexage.app.dto.dsp;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DspSummaryDTO implements Serializable {

  private String id;
  private Long pid;
  private Integer version;
  private String name;
  private String type;
  private String website;
  private String description;
}

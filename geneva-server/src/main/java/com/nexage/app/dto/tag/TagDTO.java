package com.nexage.app.dto.tag;

import com.nexage.admin.core.enums.Status;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long pid;
  private String name;
  private String adsourceName;
  private Status status = Status.ACTIVE;
  private Long placementId;
  private Double floor;
  private Long siteId;
  private Long buyerId;
  private String ecpmProvision;
}

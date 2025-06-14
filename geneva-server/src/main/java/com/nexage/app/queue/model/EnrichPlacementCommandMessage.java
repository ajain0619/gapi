package com.nexage.app.queue.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnrichPlacementCommandMessage implements SyncMessage {

  private String placementPid;
  private String sitePid;
  private String name;
  private String status;
}

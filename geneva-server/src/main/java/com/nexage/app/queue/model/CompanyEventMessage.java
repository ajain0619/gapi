package com.nexage.app.queue.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyEventMessage implements SyncMessage {

  private String id;
  private String name;
  private String status;
}

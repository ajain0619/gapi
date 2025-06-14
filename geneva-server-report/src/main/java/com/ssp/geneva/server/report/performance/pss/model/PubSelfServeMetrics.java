package com.ssp.geneva.server.report.performance.pss.model;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class PubSelfServeMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  private long siteId;
  private long adnetId;
  private long tagId;
  private String position;
  private long currentInboundReqs;
  private long currentOutboundReqs;
  private long currentServed;
  private long currentDelivered;
  private long currentClicks;
  private double currentRevenue;
  private long prevInboundReqs;
  private long prevOutboundReqs;
  private long prevServed;
  private long prevDelivered;
  private long prevClicks;
  private double prevRevenue;
  private Date updated;
}

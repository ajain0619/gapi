package com.nexage.app.dto.pub.self.serve;

public class PubSelfServeMetricSummaryOverTimeDTO {

  protected double current;
  protected double past;
  protected double diff;
  protected double diffPercent;

  public double getCurrent() {
    return current;
  }

  public double getPast() {
    return past;
  }

  public double getDiff() {
    diff = current - past;
    return diff;
  }

  public double getDiffPercent() {
    if (past > 0) diffPercent = (diff / past) * 100;
    return diffPercent;
  }

  public void aggregate(double current, double past) {
    this.current += current;
    this.past += past;
  }

  public void setCurrent(double current) {
    this.current = current;
  }

  public void setPast(double past) {
    this.past = past;
  }

  public void setDiff(double diff) {
    this.diff = diff;
  }

  public void setDiffPercent(double diffPercent) {
    this.diffPercent = diffPercent;
  }
}

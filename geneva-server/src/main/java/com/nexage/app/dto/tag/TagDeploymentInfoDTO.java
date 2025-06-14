package com.nexage.app.dto.tag;

import java.util.ArrayList;
import java.util.List;

public class TagDeploymentInfoDTO {

  private long tagPid;
  private Long selfServePosition;
  private boolean isUndeployTagValid = false;

  private List<Position> positions = new ArrayList<Position>();

  public TagDeploymentInfoDTO(long tagPid) {
    this.tagPid = tagPid;
  }

  public long getTagPid() {
    return tagPid;
  }

  private void invalidateReturnData() {
    positions.clear();
  }

  public void setIsUndeployValid(boolean valid) {
    this.isUndeployTagValid = valid;
    if (false == isUndeployTagValid) {
      invalidateReturnData();
    }
  }

  public boolean isUndeployTagValid() {
    return isUndeployTagValid;
  }

  public Long getSelfServePosition() {
    return selfServePosition;
  }

  public void setSelfServePosition(Long position) {
    this.selfServePosition = position;
  }

  public boolean hasSelfServePosition() {
    return selfServePosition != null && selfServePosition != 0;
  }

  public List<Position> getPositions() {
    return positions;
  }

  public void addPosition(Position position) {
    positions.add(position);
  }

  public int getNumPositions() {
    int count = (null == positions ? 0 : positions.size());
    return count;
  }

  public Position findPosition(long pid) {
    for (Position p : positions) {
      if (p.getPid() == pid) {
        return p;
      }
    }
    return null;
  }

  public List<Tier> findTiersToUndeploy() {
    List<Tier> tiersToUndeploy = new ArrayList<>();
    for (Position position : positions) {
      List<Tier> tiers = position.getTiers();
      for (Tier tier : tiers) {
        if (1 == tier.getNumTags()) {
          tiersToUndeploy.add(tier);
        }
      }
    }

    return tiersToUndeploy;
  }

  public static class Tier {

    private long pid;
    private int numTags;
    private int level;

    public Tier(long pid) {
      this.pid = pid;
    }

    public long getPid() {
      return pid;
    }

    public void setNumTags(int tags) {
      this.numTags = tags;
    }

    public int getNumTags() {
      return numTags;
    }

    public void setLevel(int level) {
      this.level = level;
    }

    public int getLevel() {
      return level;
    }
  }

  public static class Position {

    private long pid;

    public Position(long pid, String name) {
      this.pid = pid;
      this.name = name;
    }

    private List<Tier> tiers = new ArrayList<Tier>();
    private String name;

    public long getPid() {
      return pid;
    }

    public String getName() {
      return name;
    }

    public void addTier(Tier tier) {
      tiers.add(tier);
    }

    public List<Tier> getTiers() {
      return tiers;
    }

    public int getNumTiers() {
      return tiers.size();
    }

    public Tier findTier(long pid) {
      for (Tier t : tiers) {
        if (t.getPid() == pid) {
          return t;
        }
      }
      return null;
    }
  }
}

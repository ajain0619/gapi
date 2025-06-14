package com.nexage.admin.core.bidder.type;

import com.nexage.admin.core.bidder.model.BDRLineItem;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public enum BDRLineItemStatus {
  DELETED(-1) {
    @Override
    public boolean prepareForTransition(BDRLineItem lineitem) {
      lineitem.setStatus(DELETED);
      return true;
    }
  },
  INACTIVE(0) {
    @Override
    public boolean prepareForTransition(BDRLineItem lineitem) {
      return false;
    }
  },
  ACTIVE(1) { // Running
    @Override
    public boolean prepareForTransition(BDRLineItem lineitem) {
      Calendar stopCal = Calendar.getInstance();
      if (lineitem.getStopDate() != null) stopCal.setTime(lineitem.getStopDate());
      Calendar now = Calendar.getInstance();
      if ((lineitem.getStatus() == INACTIVE
              || lineitem.getStatus() == SCHEDULED
              || lineitem.getStatus() == PAUSED)
          && (lineitem.getStopDate() == null || stopCal.compareTo(now) > 0)) {
        lineitem.setDeployable(true);
        if (INACTIVE.equals(lineitem.getStatus()) || SCHEDULED.equals(lineitem.getStatus())) {
          lineitem.setStartDate(now.getTime());
          lineitem.setResumeProgress(0d);
        }
        lineitem.setStatus(ACTIVE);
        lineitem.setResumeTime(now.getTime());
        return true;
      }
      return false;
    }
  },
  SCHEDULED(2) {
    @Override
    public boolean prepareForTransition(BDRLineItem lineitem) {
      Date now = Calendar.getInstance().getTime();
      if (lineitem.getStatus() == INACTIVE
          && (lineitem.getStartDate().after(now)
              && (lineitem.getStopDate() == null
                  || lineitem.getStopDate().after(lineitem.getStartDate())))) {
        lineitem.setDeployable(true);
        lineitem.setStatus(ACTIVE);
        lineitem.setResumeTime(lineitem.getStartDate());
        lineitem.setResumeProgress(0d);
        return true;
      }
      return false;
    }
  },
  PAUSED(3) {
    @Override
    public boolean prepareForTransition(BDRLineItem lineitem) {
      if (lineitem.getStatus() == SCHEDULED || lineitem.getStatus() == ACTIVE) {
        lineitem.setStatus(PAUSED);
        return true;
      }
      return false;
    }
  },
  COMPLETED(4) {
    @Override
    public boolean prepareForTransition(BDRLineItem lineitem) {
      if (lineitem.getStatus() == PAUSED
          || lineitem.getStatus() == ACTIVE
          || lineitem.getStatus() == SCHEDULED) {
        lineitem.setStatus(COMPLETED);
        return true;
      }
      return false;
    }
  },
  ARCHIVED(5) {
    @Override
    public boolean prepareForTransition(BDRLineItem lineitem) {
      if (lineitem.getStatus() == INACTIVE || lineitem.getStatus() == COMPLETED) {
        lineitem.setStatus(ARCHIVED);
        return true;
      }
      return false;
    }
  };

  private int value;

  BDRLineItemStatus(int value) {
    this.value = value;
  }

  public static BDRLineItemStatus fromInt(int value) {
    return fromIntMap.get(value);
  }

  public int asInt() {
    return value;
  }

  private static final HashMap<Integer, BDRLineItemStatus> fromIntMap = new HashMap<>();

  static {
    for (BDRLineItemStatus status : BDRLineItemStatus.values()) {
      fromIntMap.put(status.value, status);
    }
  }

  public abstract boolean prepareForTransition(BDRLineItem old);
}

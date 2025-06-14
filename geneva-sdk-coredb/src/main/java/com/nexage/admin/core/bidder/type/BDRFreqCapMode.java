package com.nexage.admin.core.bidder.type;

/**
 * Frequency Capping: - "Strict" will be stored as a 1 and should be display as "Always" - "Loose"
 * will be stored as a 2 and should be displayed as "When Possible"
 *
 * @author g.lira
 */
public enum BDRFreqCapMode {
  NONE(1),
  ALWAYS(1),
  WHEN_POSSIBLE(2);

  private final int code;

  BDRFreqCapMode(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static BDRFreqCapMode valueOf(int code) {
    for (BDRFreqCapMode bdrFreqCapMode : values()) {
      if (bdrFreqCapMode.getCode() == code) {
        return bdrFreqCapMode;
      }
    }
    throw new IllegalArgumentException("Can't find value for code " + code);
  }
}

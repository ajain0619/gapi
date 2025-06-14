package com.nexage.admin.core.enums;

/** Defines CRS filtering level for a publisher */
public enum CrsReviewStatusBlock {
  ALLOW_ALL(0),
  ALLOW_SCANNED_ONLY(1),
  ALLOW_SCANNED_OR_TRUSTED_ONLY(2);

  private final int status;

  CrsReviewStatusBlock(int status) {
    this.status = status;
  }

  public static CrsReviewStatusBlock of(int statusBlock) {
    for (CrsReviewStatusBlock crsReviewStatusBlock : CrsReviewStatusBlock.values()) {
      if (crsReviewStatusBlock.status == statusBlock) {
        return crsReviewStatusBlock;
      }
    }

    return ALLOW_ALL;
  }
}

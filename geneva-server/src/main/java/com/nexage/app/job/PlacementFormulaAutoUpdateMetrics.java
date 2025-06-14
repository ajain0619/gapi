package com.nexage.app.job;

class PlacementFormulaAutoUpdateMetrics {
  private final long findAllTime;
  private final int totalFound;

  private long findTime = 0L;
  private int loaded = 0;

  private long searchTime = 0L;
  private int searched = 0;
  private int totalFoundPlacements = 0;

  private int changed = 0;
  private int notChanged = 0;

  private long updateTime = 0L;
  private int updated = 0;

  private int warnings = 0;
  private int errors = 0;

  private long totalTime = 0L;

  PlacementFormulaAutoUpdateMetrics(long findAllTime, int totalFound) {
    this.findAllTime = findAllTime;
    this.totalFound = totalFound;
  }

  long getFindAllTime() {
    return findAllTime;
  }

  int getTotalFound() {
    return totalFound;
  }

  long getFindTime() {
    return findTime;
  }

  public int getLoaded() {
    return loaded;
  }

  long getSearchTime() {
    return searchTime;
  }

  int getSearched() {
    return searched;
  }

  int getTotalFoundPlacements() {
    return totalFoundPlacements;
  }

  int getChanged() {
    return changed;
  }

  int getNotChanged() {
    return notChanged;
  }

  long getUpdateTime() {
    return updateTime;
  }

  int getUpdated() {
    return updated;
  }

  int getWarnings() {
    return warnings;
  }

  int getErrors() {
    return errors;
  }

  long getTotalTime() {
    return totalTime;
  }

  void incrementChanged() {
    ++changed;
  }

  void incrementNotChanged() {
    ++notChanged;
  }

  void incrementWarnings() {
    ++warnings;
  }

  void incrementErrors() {
    ++errors;
  }

  void addFindTime(long millis) {
    findTime += millis;
    ++loaded;
  }

  void addSearchTimeAndFoundPlacements(long millis, int placements) {
    searchTime += millis;
    totalFoundPlacements += placements;
    ++searched;
  }

  void addUpdateTime(long millis) {
    updateTime += millis;
    ++updated;
  }

  void setTotalTime(long millis) {
    this.totalTime = millis;
  }
}

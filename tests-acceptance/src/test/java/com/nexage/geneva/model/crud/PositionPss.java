package com.nexage.geneva.model.crud;

/**
 * corresponding to following json scheme (item 2.3.1)
 * https://confluence.nexage.com/display/KS/Publisher+Self-Serve+REST+API#PublisherSelf-ServeRESTAPI-2.3PublisherPositionAPI
 */
public class PositionPss {
  private String pid;
  private String name;
  private String sitePid;
  private String version;
  private String mraidSupport;
  private String videoSupport;
  private String screenLocation;
  private String videoLinearity;
  private String videoMaxdur;
  private String height;
  private String width;
  private String interstitial;
  private String placementCategory;

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSitePid() {
    return sitePid;
  }

  public void setSitePid(String sitePid) {
    this.sitePid = sitePid;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getMraidSupport() {
    return mraidSupport;
  }

  public void setMraidSupport(String mraidSupport) {
    if (mraidSupport != null) {
      switch (mraidSupport) {
        case "NO":
        case "0":
          this.mraidSupport = "false";
          break;
        case "YES":
        case "1":
          this.mraidSupport = "true";
          break;
        default:
          this.mraidSupport = mraidSupport;
          break;
      }
    } else {
      this.mraidSupport = null;
    }
  }

  public String getVideoSupport() {
    return videoSupport;
  }

  public void setVideoSupport(String videoSupport) {
    if (videoSupport != null) {
      switch (videoSupport) {
        case "BANNER":
          this.videoSupport = "0";
          break;
        case "VIDEO":
          this.videoSupport = "1";
          break;
        case "VIDEO_AND_BANNER":
          this.videoSupport = "2";
          break;
        case "NATIVE":
          this.videoSupport = "3";
          break;
        default:
          this.videoSupport = videoSupport;
          break;
      }
    } else {
      this.videoSupport = null;
    }
  }

  public String getScreenLocation() {
    return screenLocation;
  }

  public void setScreenLocation(String screenLocation) {
    this.screenLocation = screenLocation;
  }

  public String getVideoLinearity() {
    return videoLinearity;
  }

  public void setVideoLinearity(String videoLinearity) {
    if (videoLinearity != null) {
      switch (videoLinearity) {
        case "LINEAR":
          this.videoLinearity = "1";
          break;
        case "NON_LINEAR":
          this.videoLinearity = "0";
          break;
        default:
          this.videoLinearity = videoLinearity;
          break;
      }
    } else {
      this.videoLinearity = null;
    }
  }

  public String getHeight() {
    return height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getWidth() {
    return width;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public String getInterstitial() {
    return interstitial;
  }

  public void setInterstitial(String interstitial) {
    if (interstitial != null) {
      switch (interstitial) {
        case "0":
        case "NO":
          this.interstitial = "false";
          break;
        case "1":
        case "YES":
          this.interstitial = "true";
          break;
        default:
          this.interstitial = interstitial;
          break;
      }
    } else {
      this.interstitial = null;
    }
  }

  public void setIsInterstitial(String isInterstitial) {
    setInterstitial(isInterstitial);
  }

  public String getPlacementCategory() {
    return placementCategory;
  }

  public void setPlacementCategory(String placementCategory) {
    if (placementCategory != null) {
      switch (placementCategory) {
        case "BANNER":
          this.placementCategory = "0";
          break;
        case "INTERSTITIAL":
          this.placementCategory = "1";
          break;
        case "MEDIUM_RECTANGLE":
          this.placementCategory = "2";
          break;
        case "NATIVE":
          this.placementCategory = "3";
          break;
        case "INSTREAM_VIDEO":
          this.placementCategory = "4";
          break;
        default:
          this.placementCategory = placementCategory;
          break;
      }
    } else {
      this.placementCategory = null;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PositionPss that = (PositionPss) o;

    if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (mraidSupport != null ? !mraidSupport.equals(that.mraidSupport) : that.mraidSupport != null)
      return false;
    if (videoSupport != null ? !videoSupport.equals(that.videoSupport) : that.videoSupport != null)
      return false;
    if (videoLinearity != null
        ? !videoLinearity.equals(that.videoLinearity)
        : that.videoLinearity != null) return false;
    if (height != null ? !height.equals(that.height) : that.height != null) return false;
    if (width != null ? !width.equals(that.width) : that.width != null) return false;
    if (placementCategory != null
        ? !placementCategory.equals(that.placementCategory)
        : that.placementCategory != null) return false;
    return !(interstitial != null
        ? !interstitial.equals(that.interstitial)
        : that.interstitial != null);
  }

  @Override
  public int hashCode() {
    int result = pid != null ? pid.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (mraidSupport != null ? mraidSupport.hashCode() : 0);
    result = 31 * result + (videoSupport != null ? videoSupport.hashCode() : 0);
    result = 31 * result + (videoLinearity != null ? videoLinearity.hashCode() : 0);
    result = 31 * result + (height != null ? height.hashCode() : 0);
    result = 31 * result + (width != null ? width.hashCode() : 0);
    result = 31 * result + (interstitial != null ? interstitial.hashCode() : 0);
    result = 31 * result + (placementCategory != null ? placementCategory.hashCode() : 0);
    return result;
  }
}

package com.nexage.geneva.model.crud;

/**
 * corresponding to following json scheme (item 'Site Model')
 * https://confluence.nexage.com/display/KS/Seller+Admin+-+Interface
 */
public class PositionNonPss {
  private String pid;
  private String version;
  private String sitePid;
  private String name;
  private String memo;
  private String isDefault;
  private String isInterstitial;
  private String mraidSupport;
  private String videoSupport;
  private String screenLocation;
  private String mraidAdvancedTracking;
  private String adSize;
  private String staticAdUnit;
  private String richMediaAdUnit;
  private String richMediaMRAIDVersion;
  private String videoMRAID2;
  private String videoProprietary;
  private String videoVast;
  private String videoResponseProtocol;
  private String videoLinearity;
  private String height;
  private String width;
  private String fullScreenTiming;
  private String positionAliasName;
  private String nativeVersion;
  private String nativeRequest;
  private String placementCategory;

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getSitePid() {
    return sitePid;
  }

  public void setSitePid(String sitePid) {
    this.sitePid = sitePid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(String isDefault) {
    if (isDefault != null) {
      if (isDefault.equals("0") || isDefault.equals("NO")) {
        this.isDefault = "false";
      } else if (isDefault.equals("1") || isDefault.equals("YES")) {
        this.isDefault = "true";
      } else {
        this.isDefault = isDefault;
      }
    } else {
      this.isDefault = null;
    }
  }

  public String getIsInterstitial() {
    return isInterstitial;
  }

  public void setIsInterstitial(String isInterstitial) {
    if (isInterstitial != null) {
      if (isInterstitial.equals("0") || isInterstitial.equals("NO")) {
        this.isInterstitial = "false";
      } else if (isInterstitial.equals("1") || isInterstitial.equals("YES")) {
        this.isInterstitial = "true";
      } else {
        this.isInterstitial = isInterstitial;
      }
    } else {
      this.isInterstitial = null;
    }
  }

  public String getMraidSupport() {
    return mraidSupport;
  }

  public void setMraidSupport(String mraidSupport) {
    if (mraidSupport != null) {
      if (mraidSupport.equals("NO") || mraidSupport.equals("0")) {
        this.mraidSupport = "false";
      } else if (mraidSupport.equals("YES") || mraidSupport.equals("1")) {
        this.mraidSupport = "true";
      } else {
        this.mraidSupport = mraidSupport;
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

  public String getMraidAdvancedTracking() {
    return mraidAdvancedTracking;
  }

  public void setMraidAdvancedTracking(String mraidAdvancedTracking) {
    this.mraidAdvancedTracking = mraidAdvancedTracking;
  }

  public String getAdSize() {
    return adSize;
  }

  public void setAdSize(String adSize) {
    this.adSize = adSize;
  }

  public String getStaticAdUnit() {
    return staticAdUnit;
  }

  public void setStaticAdUnit(String staticAdUnit) {
    this.staticAdUnit = staticAdUnit;
  }

  public String getRichMediaAdUnit() {
    return richMediaAdUnit;
  }

  public void setRichMediaAdUnit(String richMediaAdUnit) {
    this.richMediaAdUnit = richMediaAdUnit;
  }

  public String getRichMediaMRAIDVersion() {
    return richMediaMRAIDVersion;
  }

  public void setRichMediaMRAIDVersion(String richMediaMRAIDVersion) {
    this.richMediaMRAIDVersion = richMediaMRAIDVersion;
  }

  public String getVideoMRAID2() {
    return videoMRAID2;
  }

  public void setVideoMRAID2(String videoMRAID2) {
    this.videoMRAID2 = videoMRAID2;
  }

  public String getVideoProprietary() {
    return videoProprietary;
  }

  public void setVideoProprietary(String videoProprietary) {
    this.videoProprietary = videoProprietary;
  }

  public String getVideoVast() {
    return videoVast;
  }

  public void setVideoVast(String videoVast) {
    this.videoVast = videoVast;
  }

  public String getVideoResponseProtocol() {
    return videoResponseProtocol;
  }

  public void setVideoResponseProtocol(String videoResponseProtocol) {
    this.videoResponseProtocol = videoResponseProtocol;
  }

  public String getVideoLinearity() {
    return videoLinearity;
  }

  public void setVideoLinearity(String videoLinearity) {
    this.videoLinearity = videoLinearity;
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

  public String getFullScreenTiming() {
    return fullScreenTiming;
  }

  public void setFullScreenTiming(String fullScreenTiming) {
    this.fullScreenTiming = fullScreenTiming;
  }

  public String getPositionAliasName() {
    return positionAliasName;
  }

  public void setPositionAliasName(String positionAliasName) {
    this.positionAliasName = positionAliasName;
  }

  public String getNativeVersion() {
    return nativeVersion;
  }

  public void setNativeVersion(String nativeVersion) {
    this.nativeVersion = nativeVersion;
  }

  public String getNativeRequest() {
    return nativeRequest;
  }

  public void setNativeRequest(String nativeRequest) {
    this.nativeRequest = nativeRequest;
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

    PositionNonPss that = (PositionNonPss) o;

    if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
    if (sitePid != null ? !sitePid.equals(that.sitePid) : that.sitePid != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (memo != null ? !memo.equals(that.memo) : that.memo != null) return false;
    if (isDefault != null ? !isDefault.equals(that.isDefault) : that.isDefault != null)
      return false;
    if (isInterstitial != null
        ? !isInterstitial.equals(that.isInterstitial)
        : that.isInterstitial != null) return false;
    if (adSize != null ? !adSize.equals(that.adSize) : that.adSize != null) return false;
    if (staticAdUnit != null ? !staticAdUnit.equals(that.staticAdUnit) : that.staticAdUnit != null)
      return false;
    if (richMediaAdUnit != null
        ? !richMediaAdUnit.equals(that.richMediaAdUnit)
        : that.richMediaAdUnit != null) return false;
    if (richMediaMRAIDVersion != null
        ? !richMediaMRAIDVersion.equals(that.richMediaMRAIDVersion)
        : that.richMediaMRAIDVersion != null) return false;
    if (videoMRAID2 != null ? !videoMRAID2.equals(that.videoMRAID2) : that.videoMRAID2 != null)
      return false;
    if (videoProprietary != null
        ? !videoProprietary.equals(that.videoProprietary)
        : that.videoProprietary != null) return false;
    if (videoSupport != null ? !videoSupport.equals(that.videoSupport) : that.videoSupport != null)
      return false;
    if (videoVast != null ? !videoVast.equals(that.videoVast) : that.videoVast != null)
      return false;
    if (videoResponseProtocol != null
        ? !videoResponseProtocol.equals(that.videoResponseProtocol)
        : that.videoResponseProtocol != null) return false;
    if (height != null ? !height.equals(that.height) : that.height != null) return false;
    if (width != null ? !width.equals(that.width) : that.width != null) return false;
    if (fullScreenTiming != null
        ? !fullScreenTiming.equals(that.fullScreenTiming)
        : that.fullScreenTiming != null) return false;
    if (positionAliasName != null
        ? !positionAliasName.equals(that.positionAliasName)
        : that.positionAliasName != null) return false;
    if (placementCategory != null
        ? !placementCategory.equals(that.placementCategory)
        : that.placementCategory != null) return false;
    return !(nativeRequest != null
        ? !nativeRequest.equals(that.nativeRequest)
        : that.nativeRequest != null);
  }

  @Override
  public int hashCode() {
    int result = pid != null ? pid.hashCode() : 0;
    result = 31 * result + (sitePid != null ? sitePid.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (memo != null ? memo.hashCode() : 0);
    result = 31 * result + (isDefault != null ? isDefault.hashCode() : 0);
    result = 31 * result + (isInterstitial != null ? isInterstitial.hashCode() : 0);
    result = 31 * result + (adSize != null ? adSize.hashCode() : 0);
    result = 31 * result + (staticAdUnit != null ? staticAdUnit.hashCode() : 0);
    result = 31 * result + (richMediaAdUnit != null ? richMediaAdUnit.hashCode() : 0);
    result = 31 * result + (richMediaMRAIDVersion != null ? richMediaMRAIDVersion.hashCode() : 0);
    result = 31 * result + (videoSupport != null ? videoSupport.hashCode() : 0);
    result = 31 * result + (videoMRAID2 != null ? videoMRAID2.hashCode() : 0);
    result = 31 * result + (videoProprietary != null ? videoProprietary.hashCode() : 0);
    result = 31 * result + (videoVast != null ? videoVast.hashCode() : 0);
    result = 31 * result + (videoResponseProtocol != null ? videoResponseProtocol.hashCode() : 0);
    result = 31 * result + (videoLinearity != null ? videoLinearity.hashCode() : 0);
    result = 31 * result + (height != null ? height.hashCode() : 0);
    result = 31 * result + (width != null ? width.hashCode() : 0);
    result = 31 * result + (fullScreenTiming != null ? fullScreenTiming.hashCode() : 0);
    result = 31 * result + (positionAliasName != null ? positionAliasName.hashCode() : 0);
    result = 31 * result + (nativeRequest != null ? nativeRequest.hashCode() : 0);
    result = 31 * result + (placementCategory != null ? placementCategory.hashCode() : 0);
    return result;
  }

  // Compares objects created through the clone api
  public boolean compareClones(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PositionNonPss that = (PositionNonPss) o;
    // if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
    // if (sitePid != null ? !sitePid.equals(that.sitePid) : that.sitePid != null) return false;
    // if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (memo != null ? !memo.equals(that.memo) : that.memo != null) return false;
    if (isDefault != null ? !isDefault.equals(that.isDefault) : that.isDefault != null)
      return false;
    if (isInterstitial != null
        ? !isInterstitial.equals(that.isInterstitial)
        : that.isInterstitial != null) return false;
    if (adSize != null ? !adSize.equals(that.adSize) : that.adSize != null) return false;
    if (staticAdUnit != null ? !staticAdUnit.equals(that.staticAdUnit) : that.staticAdUnit != null)
      return false;
    if (richMediaAdUnit != null
        ? !richMediaAdUnit.equals(that.richMediaAdUnit)
        : that.richMediaAdUnit != null) return false;
    if (richMediaMRAIDVersion != null
        ? !richMediaMRAIDVersion.equals(that.richMediaMRAIDVersion)
        : that.richMediaMRAIDVersion != null) return false;
    if (videoMRAID2 != null ? !videoMRAID2.equals(that.videoMRAID2) : that.videoMRAID2 != null)
      return false;
    if (videoProprietary != null
        ? !videoProprietary.equals(that.videoProprietary)
        : that.videoProprietary != null) return false;
    if (videoSupport != null ? !videoSupport.equals(that.videoSupport) : that.videoSupport != null)
      return false;
    if (videoVast != null ? !videoVast.equals(that.videoVast) : that.videoVast != null)
      return false;
    if (videoResponseProtocol != null
        ? !videoResponseProtocol.equals(that.videoResponseProtocol)
        : that.videoResponseProtocol != null) return false;
    if (height != null ? !height.equals(that.height) : that.height != null) return false;
    if (width != null ? !width.equals(that.width) : that.width != null) return false;
    if (fullScreenTiming != null
        ? !fullScreenTiming.equals(that.fullScreenTiming)
        : that.fullScreenTiming != null) return false;
    if (placementCategory != null
        ? !placementCategory.equals(that.placementCategory)
        : that.placementCategory != null) return false;
    if (nativeRequest != null
        ? !nativeRequest.equals(that.nativeRequest)
        : that.nativeRequest != null) return false;

    return true;
  }
}

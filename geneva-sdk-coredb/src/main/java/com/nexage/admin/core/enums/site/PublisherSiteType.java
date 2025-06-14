package com.nexage.admin.core.enums.site;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public enum PublisherSiteType {
  MOBILE_WEB,
  ANDROID,
  IOS,
  DESKTOP,
  DOOH,
  WEBSITE,
  CTV_OTT;

  private static final HashMap<Map<Platform, Type>, PublisherSiteType> FROM_SITE_TYPE;

  private static final HashMap<String, PublisherSiteType> STRING_TO_PUBLISHER_SITE_TYPE =
      new HashMap<>();

  public static PublisherSiteType fromString(String asString) {
    return STRING_TO_PUBLISHER_SITE_TYPE.get(asString);
  }

  public static PublisherSiteType fromSiteType(Platform platform, Type type) {
    for (Map<Platform, Type> k : FROM_SITE_TYPE.keySet()) {
      if (k.containsKey(platform) && k.containsValue(type)) {
        return FROM_SITE_TYPE.get(k);
      }
    }
    return null;
  }

  public static Tuple2<Type, Set<Platform>> platformsFromSiteType(
      PublisherSiteType publisherSiteType) {
    Map<Platform, Type> platforms =
        FROM_SITE_TYPE.entrySet().stream()
            .filter(entry -> entry.getValue() == publisherSiteType)
            .map(Entry::getKey)
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format("No platforms configured for %s", publisherSiteType)));
    return Tuple.of(platforms.values().iterator().next(), platforms.keySet());
  }

  public static PublisherSiteType parse(String value) {
    for (PublisherSiteType type : PublisherSiteType.values()) {
      if (value.equalsIgnoreCase(type.name())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid Input value " + value);
  }

  static {
    for (PublisherSiteType publisherSiteType : values()) {
      STRING_TO_PUBLISHER_SITE_TYPE.put(publisherSiteType.name(), publisherSiteType);
    }

    FROM_SITE_TYPE = new HashMap<>();
    EnumMap<Platform, Type> androidMap = new EnumMap<>(Platform.class);
    EnumMap<Platform, Type> iosMap = new EnumMap<>(Platform.class);
    EnumMap<Platform, Type> mobileWeb = new EnumMap<>(Platform.class);
    EnumMap<Platform, Type> desktop = new EnumMap<>(Platform.class);
    EnumMap<Platform, Type> dooh = new EnumMap<>(Platform.class);
    EnumMap<Platform, Type> website = new EnumMap<>(Platform.class);
    EnumMap<Platform, Type> ctvOttMap = new EnumMap<>(Platform.class);
    androidMap.put(Platform.ANDROID, Type.APPLICATION);
    androidMap.put(Platform.ANDROID_TAB, Type.APPLICATION);
    androidMap.put(Platform.ANDROID_PHONE_TAB, Type.APPLICATION);
    iosMap.put(Platform.IPAD, Type.APPLICATION);
    iosMap.put(Platform.IPAD_IPHONE, Type.APPLICATION);
    iosMap.put(Platform.IPHONE, Type.APPLICATION);
    mobileWeb.put(Platform.OTHER, Type.MOBILE_WEB);
    desktop.put(Platform.OTHER, Type.DESKTOP);
    dooh.put(Platform.OTHER, Type.DOOH);
    website.put(Platform.OTHER, Type.WEBSITE);
    ctvOttMap.put(Platform.CTV_OTT, Type.APPLICATION);
    FROM_SITE_TYPE.put(androidMap, ANDROID);
    FROM_SITE_TYPE.put(iosMap, IOS);
    FROM_SITE_TYPE.put(mobileWeb, MOBILE_WEB);
    FROM_SITE_TYPE.put(desktop, DESKTOP);
    FROM_SITE_TYPE.put(dooh, DOOH);
    FROM_SITE_TYPE.put(website, WEBSITE);
    FROM_SITE_TYPE.put(ctvOttMap, CTV_OTT);
  }
}

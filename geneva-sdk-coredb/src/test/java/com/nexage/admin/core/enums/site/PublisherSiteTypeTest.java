package com.nexage.admin.core.enums.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublisherSiteTypeTest {

  private Tuple2<Type, Set<Platform>> mobileWeb, android, ios, desktop, website, ctv_ott;

  @BeforeEach
  void setup() {
    mobileWeb = Tuple.of(Type.MOBILE_WEB, HashSet.of(Platform.OTHER).toJavaSet());
    desktop = Tuple.of(Type.DESKTOP, HashSet.of(Platform.OTHER).toJavaSet());
    android =
        Tuple.of(
            Type.APPLICATION,
            HashSet.of(Platform.ANDROID, Platform.ANDROID_PHONE_TAB, Platform.ANDROID_TAB)
                .toJavaSet());
    ios =
        Tuple.of(
            Type.APPLICATION,
            HashSet.of(Platform.IPAD, Platform.IPAD_IPHONE, Platform.IPHONE).toJavaSet());
    website = Tuple.of(Type.WEBSITE, HashSet.of(Platform.OTHER).toJavaSet());
    ctv_ott = Tuple.of(Type.APPLICATION, HashSet.of(Platform.CTV_OTT).toJavaSet());
  }

  @Test
  void testPlatformsFromSiteType() {
    assertEquals(android, PublisherSiteType.platformsFromSiteType(PublisherSiteType.ANDROID));
    assertEquals(ios, PublisherSiteType.platformsFromSiteType(PublisherSiteType.IOS));
    assertEquals(desktop, PublisherSiteType.platformsFromSiteType(PublisherSiteType.DESKTOP));
    assertEquals(mobileWeb, PublisherSiteType.platformsFromSiteType(PublisherSiteType.MOBILE_WEB));
    assertEquals(website, PublisherSiteType.platformsFromSiteType(PublisherSiteType.WEBSITE));
    assertEquals(ctv_ott, PublisherSiteType.platformsFromSiteType(PublisherSiteType.CTV_OTT));
  }

  @Test
  void testSiteTypeFromPlatformAndSite() {
    assertEquals(
        PublisherSiteType.ANDROID,
        PublisherSiteType.fromSiteType(Platform.ANDROID, Type.APPLICATION));
    assertEquals(
        PublisherSiteType.ANDROID,
        PublisherSiteType.fromSiteType(Platform.ANDROID_PHONE_TAB, Type.APPLICATION));
    assertEquals(
        PublisherSiteType.CTV_OTT,
        PublisherSiteType.fromSiteType(Platform.CTV_OTT, Type.APPLICATION));
  }

  @Test
  void shouldReturnPublisherSiteTypeWithGivenName() {
    assertEquals(PublisherSiteType.ANDROID, PublisherSiteType.fromString("ANDROID"));
  }

  @Test
  void shouldReturnNullIfNoPublisherSiteTypeWithGivenName() {
    assertNull(PublisherSiteType.fromString("NOT_A_PUBLISHER_SITE_TYPE"));
  }
}

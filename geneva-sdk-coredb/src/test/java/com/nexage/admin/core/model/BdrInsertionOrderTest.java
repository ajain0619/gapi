package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import org.junit.jupiter.api.Test;

class BdrInsertionOrderTest {

  @Test
  void shouldReturnAdomainWhenAdvertiserIsNull() {
    // given
    String initialAdomain = "adomain";
    BdrInsertionOrder io = new BdrInsertionOrder();
    io.setAdomain(initialAdomain);

    // when
    String adomain = io.getAdomain();

    // then
    assertEquals(initialAdomain, adomain);
  }

  @Test
  void shouldReturnNullWhenInitialAdomainAndAdvertiserAreNull() {
    // given
    BdrInsertionOrder io = new BdrInsertionOrder();

    // when
    String adomain = io.getAdomain();

    // then
    assertNull(adomain);
  }

  @Test
  void shouldReturnAdvertisersDomainName() {
    // given
    String domainName = "domain-name";
    BdrInsertionOrder io = new BdrInsertionOrder();
    BDRAdvertiser ad = new BDRAdvertiser();
    ad.setDomainName(domainName);
    io.setAdvertiser(ad);

    // when
    String adomain = io.getAdomain();

    // then
    assertEquals(domainName, adomain);
  }

  @Test
  void shouldAddLineItemToTheList() {
    // given
    BdrInsertionOrder io = new BdrInsertionOrder();
    BDRLineItem lineItem = new BDRLineItem();
    lineItem.setName("line-item");

    // when
    io.addLineItem(lineItem);

    // then
    assertEquals(1, io.getLineItems().size());
    assertTrue(io.getLineItems().contains(lineItem));
    assertSame(io, io.getLineItems().iterator().next().getInsertionOrder());
  }

  @Test
  void shouldReturnIabCategoryWhenAdvertiserIsNull() {
    // given
    String initialIabCat = "iab-cat";
    BdrInsertionOrder io = new BdrInsertionOrder();
    io.setIabCategory(initialIabCat);

    // when
    String iabCategory = io.getIabCategory();

    // then
    assertEquals(initialIabCat, iabCategory);
  }

  @Test
  void shouldReturnNullWhenInitialIabCategoryAndAdvertiserAreNull() {
    // given
    BdrInsertionOrder io = new BdrInsertionOrder();

    // when
    String iabCategory = io.getIabCategory();

    // then
    assertNull(iabCategory);
  }

  @Test
  void shouldReturnAdvertisersIabCAtegory() {
    // given
    String adIabCategory = "iab-cat";
    BdrInsertionOrder io = new BdrInsertionOrder();
    BDRAdvertiser ad = new BDRAdvertiser();
    ad.setIabCategory(adIabCategory);
    io.setAdvertiser(ad);

    // when
    String iabCategory = io.getIabCategory();

    // then
    assertEquals(adIabCategory, iabCategory);
  }

  @Test
  void shouldReturnAdvertiserName() {
    // given
    String initialAdvertiserName = "ad-name";
    BdrInsertionOrder io = new BdrInsertionOrder();
    io.setAdvertiserName(initialAdvertiserName);

    // when
    String advertiserName = io.getAdvertiserName();

    // then
    assertEquals(initialAdvertiserName, advertiserName);
  }

  @Test
  void shouldReturnNullWhenAdvertiserNameAndAdvertiserPidAreNull() {
    // given
    BdrInsertionOrder io = new BdrInsertionOrder();

    // when
    String advertiserName = io.getAdvertiserName();

    // then
    assertNull(advertiserName);
  }

  @Test
  void shouldReturnNameFromAdvertiserWhenAdvertiserPidNotNull() {
    // given
    String name = "ad-name";
    BdrInsertionOrder io = new BdrInsertionOrder();
    BDRAdvertiser ad = new BDRAdvertiser();
    ad.setName(name);
    io.setAdvertiser(ad);
    io.setAdvertiserPid(123L);

    // when
    String advertiserName = io.getAdvertiserName();

    // then
    assertEquals(name, advertiserName);
  }
}

package com.nexage.admin.core.sparta.jpa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SellerAdSourceTest {

  @Test
  void testSetAndGetPassword() {
    var input = "some data";
    var sellerAdSource = new SellerAdSource();
    sellerAdSource.setPassword(input);

    assertEquals(input, sellerAdSource.getPassword());
  }
}

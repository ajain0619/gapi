package com.nexage.admin.core.sparta.jpa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.SellerAttributes;
import org.junit.jupiter.api.Test;

class SmartExchangeAttributesTest {

  @Test
  void shouldBeEqualAndHaveSameHashcodeWhenComparedToItself() {
    SmartExchangeAttributes original = createSmartExchangeAttributes();

    assertEquals(original, original);
    assertEquals(original.hashCode(), original.hashCode());
  }

  @Test
  void shouldNotBeEqualWhenOtherIsNull() {
    SmartExchangeAttributes original = new SmartExchangeAttributes();

    assertNotNull(original);
  }

  @Test
  void shouldNotBeEqualAndShouldHaveDifferentHashcodeWhenOtherHasDifferentType() {
    SmartExchangeAttributes original = new SmartExchangeAttributes();
    SellerAttributes other = new SellerAttributes();

    assertNotEquals(original, other);
    assertNotEquals(original.hashCode(), other.hashCode());
  }

  @Test
  void shouldBeEqualAndHaveSameHashcodeWhenBothDefault() {
    SmartExchangeAttributes original = new SmartExchangeAttributes();
    SmartExchangeAttributes other = new SmartExchangeAttributes();

    assertEquals(original, other);
    assertEquals(original.hashCode(), other.hashCode());
  }

  @Test
  void shouldNotBeEqualAndShouldHaveDifferentHashcodeWhenOtherIsNotDefault() {
    SmartExchangeAttributes original = new SmartExchangeAttributes();
    SmartExchangeAttributes other = new SmartExchangeAttributes();
    other.setSmartMarginOverride(true);

    assertNotEquals(original, other);
    assertNotEquals(original.hashCode(), other.hashCode());
  }

  @Test
  void shouldNotBeEqualAndShouldHaveDifferentHashcodeWhenOtherHasSmartMarginEnabled() {
    SmartExchangeAttributes original = new SmartExchangeAttributes();
    SmartExchangeAttributes other = new SmartExchangeAttributes();
    other.setSmartMarginOverride(true);

    assertNotEquals(original, other);
    assertNotEquals(original.hashCode(), other.hashCode());
  }

  @Test
  void shouldBeEqualAndHaveSameHashcodeWhenBothHaveSameValues() {
    SmartExchangeAttributes original = createSmartExchangeAttributes();
    SmartExchangeAttributes other = createSmartExchangeAttributes();

    assertEquals(original, other);
    assertEquals(original.hashCode(), other.hashCode());
  }

  private SmartExchangeAttributes createSmartExchangeAttributes() {
    SmartExchangeAttributes smartExchangeAttributes = new SmartExchangeAttributes();
    smartExchangeAttributes.setSmartMarginOverride(true);
    return smartExchangeAttributes;
  }
}

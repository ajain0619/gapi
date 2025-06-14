package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class BaseTargetTest {

  @Test
  void equals_hashCode_PositiveTest() {
    BaseTarget bt1 = new BaseTarget();
    bt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt1.setData("CHN/*,USA/MA");
    BaseTarget bt2 = new BaseTarget();
    bt2.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt2.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt2.setData("CHN/*,USA/MA");
    // equals() and hashCode() behavior must be consistent
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.DEVICE_MAKE_MODEL);
    bt1.setData("Alcatel/*,Huawei/*");
    bt2.setTargetType(BaseTarget.TargetType.DEVICE_MAKE_MODEL);
    bt2.setData("Alcatel/*,Huawei/*");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.DEVICE_OS_VERSION);
    bt1.setData("Android/*/2.1,ios/*/*");
    bt2.setTargetType(BaseTarget.TargetType.DEVICE_OS_VERSION);
    bt2.setData("Android/*/2.1,ios/*/*");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.GENDER_AGE);
    bt1.setData("male / 18/*, female/18/*");
    bt2.setTargetType(BaseTarget.TargetType.GENDER_AGE);
    bt2.setData("male / 18/*, female/18/*");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.SDK_VERSION);
    bt1.setData("sdk/6.3.0/6.3.1");
    bt2.setTargetType(BaseTarget.TargetType.SDK_VERSION);
    bt2.setData("sdk/6.3.0/6.3.1");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt1.setRuleType(BaseTarget.RuleType.NEGATIVE);
    bt1.setData("CHN/*,USA/MA");
    bt2.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt2.setRuleType(BaseTarget.RuleType.NEGATIVE);
    bt2.setData("CHN/*,USA/MA");
    // equals() and hashCode() behavior must be consistent
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.DEVICE_MAKE_MODEL);
    bt1.setData("Alcatel/*,Huawei/*");
    bt2.setTargetType(BaseTarget.TargetType.DEVICE_MAKE_MODEL);
    bt2.setData("Alcatel/*,Huawei/*");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.DEVICE_OS_VERSION);
    bt1.setData("Android/*/2.1,ios/*/*");
    bt2.setTargetType(BaseTarget.TargetType.DEVICE_OS_VERSION);
    bt2.setData("Android/*/2.1,ios/*/*");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.GENDER_AGE);
    bt1.setData("male / 18/*, female/18/*");
    bt2.setTargetType(BaseTarget.TargetType.GENDER_AGE);
    bt2.setData("male / 18/*, female/18/*");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());

    bt1.setTargetType(BaseTarget.TargetType.SDK_VERSION);
    bt1.setData("sdk/6.3.0/6.3.1");
    bt2.setTargetType(BaseTarget.TargetType.SDK_VERSION);
    bt2.setData("sdk/6.3.0/6.3.1");
    assertEquals(bt1, bt2);
    assertEquals(bt1.hashCode(), bt2.hashCode());
  }

  @Test
  void equals_hashCode_TargetType_NegativeTest() {
    BaseTarget bt1 = new BaseTarget();
    bt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt1.setData("CHN/*,USA/MA");

    BaseTarget bt2 = new BaseTarget();
    bt2.setTargetType(BaseTarget.TargetType.DEVICE_MAKE_MODEL);
    bt2.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt2.setData("CHN/*,USA/MA");

    // equals() and hashCode() behavior must be consistent
    assertNotEquals(bt1, bt2);
    assertNotEquals(bt1.hashCode(), bt2.hashCode());
  }

  @Test
  void equals_hashCode_RuleType_NegativeTest() {
    BaseTarget bt1 = new BaseTarget();
    bt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt1.setData("CHN/*,USA/MA");

    BaseTarget bt2 = new BaseTarget();
    bt2.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt2.setRuleType(BaseTarget.RuleType.NEGATIVE);
    bt2.setData("CHN/*,USA/MA");

    // equals() and hashCode() behavior must be consistent
    assertFalse(bt1.equals(bt2));
    assertFalse(bt1.hashCode() == bt2.hashCode());
  }

  @Test
  void equals_hashCode_Data_NegativeTest() {
    BaseTarget bt1 = new BaseTarget();
    bt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt1.setData("CHN/*,USA/MA");

    BaseTarget bt2 = new BaseTarget();
    bt2.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt2.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt2.setData("CHN/*,USA");

    // equals() and hashCode() behavior must be consistent
    assertNotEquals(bt1, bt2);
    assertNotEquals(bt1.hashCode(), bt2.hashCode());
  }

  @Test
  void equals_hashCode_notBaseTargetObject_NegativeTest() {
    BaseTarget bt1 = new BaseTarget();
    bt1.setTargetType(BaseTarget.TargetType.COUNTRY_STATE);
    bt1.setRuleType(BaseTarget.RuleType.POSITIVE);
    bt1.setData("CHN/*,USA/MA");

    Object object2 = new Object();

    // equals() and hashCode() behavior must be consistent
    assertNotEquals(bt1, object2);
    assertNotEquals(bt1.hashCode(), object2.hashCode());
  }
}

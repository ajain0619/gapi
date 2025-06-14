package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BDRTarget;
import com.nexage.admin.core.bidder.model.BDRTargetGroupCreative;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.bidder.type.BDRTargetType;
import com.nexage.admin.core.enums.Status;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BdrTargetGroupTest {

  @Test
  void shouldReturnEqualForSameObject() {
    BdrTargetGroup targetGroup = new BdrTargetGroup();
    assertEquals(targetGroup, targetGroup);
  }

  @Test
  void shouldReturnEqualsForObjectsWithSameValues() {
    List<BdrTargetGroup> bdrTargetGroupList = List.of(new BdrTargetGroup(), new BdrTargetGroup());
    BDRLineItem lineItem = new BDRLineItem();
    for (BdrTargetGroup targetGroup : bdrTargetGroupList) {
      targetGroup.setName("targetGroup");
      targetGroup.setLineItem(lineItem);
    }
    assertEquals(bdrTargetGroupList.get(0), bdrTargetGroupList.get(1));
    assertNull(bdrTargetGroupList.get(0).getPid());
    assertNull(bdrTargetGroupList.get(1).getPid());
  }

  @Test
  void shouldCloneTargetGroup() {
    // given
    var defaultValue = 100L;
    String defaultName = "targetGroup";
    Status defaultStatus = Status.ACTIVE;

    BdrTargetGroup bdrTargetGroup = new BdrTargetGroup();
    bdrTargetGroup.setDailyImpressionCap(defaultValue);
    bdrTargetGroup.setDailySpendCap(BigDecimal.valueOf(defaultValue));
    bdrTargetGroup.setMaxPrice(BigDecimal.valueOf(defaultValue));
    bdrTargetGroup.setName(defaultName);
    bdrTargetGroup.setStatus(defaultStatus);
    bdrTargetGroup.setLineItem(new BDRLineItem());
    bdrTargetGroup.setTargetGroupCreatives(
        Set.of(
            createTestTargetGroupCreative("creative1"),
            createTestTargetGroupCreative("creative2")));
    bdrTargetGroup.setTargets(
        Set.of(createTestTarget(BDRTargetType.PUBLISHER), createTestTarget(BDRTargetType.DEVICE)));
    BDRLineItem lineItem = new BDRLineItem();

    // when
    BdrTargetGroup clonedTargetGroup =
        new BdrTargetGroup.Cloner(bdrTargetGroup).lineItem(lineItem).copyCreatives(true).build();

    // then
    assertEquals(bdrTargetGroup.getDailyImpressionCap(), clonedTargetGroup.getDailyImpressionCap());
    assertEquals(bdrTargetGroup.getDailySpendCap(), clonedTargetGroup.getDailySpendCap());
    assertEquals(bdrTargetGroup.getMaxPrice(), clonedTargetGroup.getMaxPrice());
    assertEquals("Clone of " + bdrTargetGroup.getName(), clonedTargetGroup.getName());
    assertEquals(bdrTargetGroup.getStatus(), clonedTargetGroup.getStatus());
    assertEquals(lineItem, clonedTargetGroup.getLineItem());
    assertEquals(2, clonedTargetGroup.getTargets().size());
    assertEquals(2, clonedTargetGroup.getTargetGroupCreatives().size());
  }

  private BDRTarget createTestTarget(BDRTargetType targetType) {
    BDRTarget target = new BDRTarget();
    target.setTargetType(targetType);
    return target;
  }

  private BDRTargetGroupCreative createTestTargetGroupCreative(String name) {
    BDRTargetGroupCreative targetGroupCreative = new BDRTargetGroupCreative();
    BdrCreative creative = new BdrCreative();
    creative.setName(name);
    targetGroupCreative.setCreative(creative);
    return targetGroupCreative;
  }
}

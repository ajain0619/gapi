package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.TierType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TierTest {

  private static final long TIER_PID = 1L;
  private static final long POSITION_PID = 2L;
  private static final double INPUT_WEIGHT = 1.00;
  private static final long TAG_PID = 1L;
  private static final int INT = 1;
  private static final String INPUT_STRING = "_27";
  private static final long EXPECTED_PID = 27L;

  @Test
  void shouldCreateStringKeyFromPidTest() {
    Tier tier = new Tier();
    assertEquals(INPUT_STRING, tier.convertPidToWeightKey(EXPECTED_PID));
  }

  @Test
  void shouldCreatePidFromStringKeyTest() {
    Tier tier = new Tier();
    assertEquals(EXPECTED_PID, tier.convertWeightKeyToPid(INPUT_STRING));
  }

  @Test
  void shouldNotConvertPidWeightKeyToPidWhenKeyIsNull() {
    // given
    Tier tier = new Tier();
    Map<String, Double> weightsMap = new HashMap<>();
    weightsMap.put(null, INPUT_WEIGHT);

    // when
    tier.setWeightsMap(weightsMap);

    // then
    assertNull(tier.getWeightsMap().get(INPUT_STRING));
  }

  @Test
  void shouldReturnPositiveTestWhenAssertingEqualsAndHashCode() {
    // given
    Tier tier1 = new Tier();
    tier1.setPid(TIER_PID);

    Position position1 = new Position();
    position1.setPid(POSITION_PID);

    tier1.setPosition(position1);
    tier1.setLevel(0);
    tier1.setOrderStrategy(Tier.OrderStrategy.Dynamic);
    tier1.setTags(new ArrayList<>());
    tier1.setTierType(TierType.SUPER_AUCTION);

    Tier tier2 = new Tier();
    tier2.setPid(TIER_PID);

    Position position2 = new Position();
    position2.setPid(POSITION_PID);

    tier2.setPosition(position2);
    tier2.setLevel(0);
    tier2.setOrderStrategy(Tier.OrderStrategy.Dynamic);
    tier2.setTags(new ArrayList<>());
    tier2.setTierType(TierType.SUPER_AUCTION);

    // when & then
    assertEquals(tier1.getPosition(), tier2.getPosition());
    assertEquals(tier1.hashCode(), tier2.hashCode());
  }

  @Test
  void shouldReturnPositiveTestOnPositionFieldsWhenAssertingEqualsAndHashCode() {
    // given
    Tier tier1 = new Tier();
    tier1.setPid(TIER_PID);
    tier1.setLevel(0);
    tier1.setOrderStrategy(Tier.OrderStrategy.Dynamic);
    tier1.setTags(new ArrayList<>());
    tier1.setTierType(TierType.SUPER_AUCTION);

    Tier tier2 = new Tier();
    tier2.setPid(TIER_PID);

    tier2.setLevel(0);
    tier2.setOrderStrategy(Tier.OrderStrategy.Dynamic);
    tier2.setTags(new ArrayList<>());
    tier2.setTierType(TierType.SUPER_AUCTION);

    List<Tier> tierList = new ArrayList<>();
    tierList.add(tier1);
    tierList.add(tier2);

    Position position1 = new Position();
    position1.setTiers(tierList);
    position1.setAdSizeType(AdSizeType.DYNAMIC);

    Position position2 = new Position();
    position1.setTiers(tierList); // same list of tiers
    position2.setAdSizeType(AdSizeType.STANDARD);

    // when & then
    assertEquals(tier1, tier2);
    assertEquals(tier1.hashCode(), tier2.hashCode());
  }

  @Test
  void shouldReturnNegativeTestWhenAssertingEqualsAndHashCode() {
    // given
    Tier tier1 = new Tier();
    tier1.setPid(TIER_PID);
    tier1.setLevel(0);
    tier1.setOrderStrategy(Tier.OrderStrategy.Dynamic);
    tier1.setTags(new ArrayList<>());
    tier1.setTierType(TierType.SUPER_AUCTION);

    Tier tier2 = new Tier();
    tier2.setPid(TIER_PID);

    tier2.setLevel(0);
    tier2.setOrderStrategy(Tier.OrderStrategy.Dynamic);
    tier2.setTags(new ArrayList<>());
    tier2.setTierType(TierType.SUPER_AUCTION);

    List<Tier> tierList1 = new ArrayList<>();
    tierList1.add(tier1);

    List<Tier> tierList2 = new ArrayList<>();
    tierList2.add(tier2);

    Position position1 = new Position();
    position1.setTiers(tierList1);
    position1.setAdSizeType(AdSizeType.DYNAMIC);

    Position position2 = new Position();
    position1.setTiers(tierList2); // different list of tiers
    position2.setAdSizeType(AdSizeType.STANDARD);

    // when & then
    assertEquals(tier1, tier2);
    assertEquals(tier1.hashCode(), tier2.hashCode());
  }

  @Test
  void shouldReturnNegativeTestOnDifferentInstancesWhenAssertingEqualsAndHashCode() {
    // given
    Tier tier = new Tier();
    tier.setPid(TIER_PID);

    Position position = new Position();
    position.setPid(POSITION_PID);

    tier.setPosition(position);

    // when & then
    assertNotEquals(tier, position);
    assertNotEquals(tier.hashCode(), position.hashCode());
  }

  @Test
  void shouldAddTagWeight() {
    // given
    Tier tier = new Tier();

    // when
    tier.addTagWeight(TAG_PID, INPUT_WEIGHT);

    // then
    assertEquals(1, tier.getWeight().size());
  }

  @Test
  void shouldNotAddWeightWhenTagPidIsNull() {
    // given
    Tier tier = new Tier();

    // when
    tier.addTagWeight(null, INPUT_WEIGHT);

    // then
    assertEquals(0, tier.getWeight().size());
  }

  @Test
  void shouldReturnEmptyMapWhenWeightMapIsNull() {
    // given
    Tier tier = new Tier();
    tier.setWeight(null);

    // when
    tier.addTagWeight(TAG_PID, INPUT_WEIGHT);

    // then
    assertNotNull(tier.getWeight());
  }

  @Test
  void shouldSetWeightWhenValidInputWeights() {
    // given
    Tier tier = new Tier();
    Map<Long, Double> inputWeights = new HashMap<>();
    inputWeights.put(TAG_PID, INPUT_WEIGHT);

    // when
    tier.setWeight(inputWeights);

    // then
    assertEquals(1, tier.getWeight().size());
  }

  @Test
  void shouldClearWeights() {
    // given
    Tier tier = new Tier();

    // when
    tier.addTagWeight(TAG_PID, INPUT_WEIGHT);
    tier.clearWeights();

    // then
    assertEquals(0, tier.getWeight().size());
    assertEquals(new HashMap<>(), tier.getWeight());
  }

  @Test
  void shouldSetLevel() {
    // given / when / then
    assertEquals(INT, new Tier(INT).getLevel());
  }

  @Test
  void shouldAddTag() {
    // given
    Tier tier = new Tier();
    Tag tag = new Tag();

    // when
    tier.addTag(tag);

    // then
    assertEquals(1, tier.getTags().size());
  }

  @Test
  void shouldSetWeightMap() {
    // given
    Tier tier = new Tier();
    Map<String, Double> weightsMap = new HashMap<>();
    weightsMap.put(INPUT_STRING, INPUT_WEIGHT);

    // when
    tier.setWeightsMap(weightsMap);

    // then
    assertEquals(1, tier.getWeightsMap().size());
    assertEquals(INPUT_WEIGHT, tier.getWeightsMap().get(INPUT_STRING));
  }

  @Test
  void shouldRemoveTag() {
    // given
    Tier tier = new Tier();
    Tag tag = new Tag();
    tag.setPid(1L);
    tier.addTag(tag);

    assertEquals(1, tier.getTags().size());

    // when
    tier.removeTag(tag);

    // then
    assertEquals(0, tier.getTags().size());
  }
}

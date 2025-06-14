package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Tag.Deployment;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.TagPosition;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TagTest {

  @ParameterizedTest
  @CsvSource(value = {"someName,someName", ",buyerName", "'',buyerName"})
  void shouldCorrectlyResolveBuyerName(String name, String expected) {
    // given
    Tag tag = new Tag();
    tag.setBuyerName(name);

    // when & then
    assertEquals(expected, tag.getBuyerName());
  }

  @Test
  void shouldDetermineIfTagIsPss() {
    // given
    Tag pssTag = new Tag();
    pssTag.setPosition(new TagPosition());

    Tag nonPssTag = new Tag();

    // when & then
    assertTrue(pssTag.isPublisherSelfServeTag());
    assertFalse(nonPssTag.isPublisherSelfServeTag());
  }

  @Test
  void shouldTrimInputWhenSettingAdditionalGet() {
    // given
    Tag tag = new Tag();

    // when
    tag.setAdditionalGet("   get   ");

    // then
    assertEquals("get", tag.getAdditionalGet());
  }

  @Test
  void shouldTrimInputWhenSettingAdditionalPost() {
    // given
    Tag tag = new Tag();

    // when
    tag.setAdditionalPost("   post   ");

    // then
    assertEquals("post", tag.getAdditionalPost());
  }

  @Test
  void shouldCorrectlyDetermineNexageRevenueShareOverride() {
    // given
    SiteDealTerm dealTerm = new SiteDealTerm();
    dealTerm.setNexageRevenueShare(BigDecimal.ONE);

    Tag tagWithRso = new Tag();
    tagWithRso.setCurrentDealTerm(dealTerm);

    Tag tagWithoutRso = new Tag();

    // when & then
    assertNull(tagWithoutRso.getNexageRevenueShareOverride());
    assertEquals(BigDecimal.ONE, tagWithRso.getNexageRevenueShareOverride());
  }

  @Test
  void shouldAddDeployment() {
    // given
    Deployment deployment = new Deployment();
    Tag tag = new Tag();

    // when
    tag.addToDeployments(deployment);

    // then
    assertEquals(List.of(deployment), tag.getDeployments());
  }

  @Test
  void shouldCloneItself() throws Exception {
    // given
    Tag original = new Tag();
    original.setPid(123L);
    original.setIdentifier("abc");
    original.setName("def");
    original.setSite(new Site());
    original.setStatus(Status.ACTIVE);
    original.setDeployments(new ArrayList<>(List.of(new Deployment())));
    original.setRules(new HashSet<>(Set.of(new TagRule())));

    // when
    Tag copy = original.clone();

    // then
    assertNotSame(copy, original);
    assertEquals(copy, original);
    assertSame(copy.getSite(), original.getSite());
    assertSame(copy.getStatus(), original.getStatus());
    assertNotSame(copy.getDeployments(), original.getDeployments());
    assertEquals(copy.getDeployments(), original.getDeployments());
    assertNotSame(copy.getRules(), original.getRules());
    assertEquals(copy.getRules(), original.getRules());
  }

  @Test
  void shouldGetRtbFeeOverride() {
    // given
    BigDecimal fee = BigDecimal.TEN;

    SiteDealTerm dealTerm = new SiteDealTerm();
    dealTerm.setRtbFee(fee);

    Tag tag = new Tag();
    tag.setCurrentDealTerm(dealTerm);

    // when
    BigDecimal result = tag.getRtbFeeOverride();

    // then
    assertEquals(fee, result);
  }

  @Test
  void shouldGetNullAsRtbFeeOverrideWhenDealTermIsNotSet() {
    // given
    Tag tag = new Tag();

    // when
    BigDecimal result = tag.getRtbFeeOverride();

    // then
    assertNull(result);
  }
}

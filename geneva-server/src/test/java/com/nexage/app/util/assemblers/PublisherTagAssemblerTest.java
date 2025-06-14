package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.enums.Owner;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.TagController;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
import com.nexage.app.dto.Status;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherTagControllerDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTagRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.context.PublisherDefaultRTBProfileContext;
import com.nexage.app.util.assemblers.context.PublisherTagContext;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PublisherTagAssemblerTest {

  @Mock private UserContext userContext;
  @Mock private PublisherTagDTO publisherTag;
  @Mock private PublisherBuyerDTO publisherBuyer;

  @InjectMocks private PublisherTagAssembler publisherTagAssembler;

  private Tag inputTag;

  @BeforeEach
  void setup() {
    inputTag = new Tag();
  }

  @Test
  void when_for_default_rtb_profile_is_true_then_owner_is_nexage() {
    // given
    ReflectionTestUtils.setField(publisherTagAssembler, "userContext", userContext);

    var context =
        PublisherTagContext.newBuilder().forRTBProfile(true).withCopyOperation(false).build();

    given(publisherTag.getStatus()).willReturn(Status.ACTIVE);
    given(publisherTag.getBuyer()).willReturn(publisherBuyer);
    given(publisherTag.getRtbProfile()).willReturn(null);
    given(publisherTag.getTagController())
        .willReturn(PublisherTagControllerDTO.builder().pid(1L).autoExpand(true).build());
    given(publisherTag.getRules())
        .willReturn(Set.of(PublisherTagRuleDTO.builder().data("DATA").build()));

    // when
    var result = publisherTagAssembler.apply(context, inputTag, publisherTag);

    // then
    assertNotNull(result, "Assembled Tag cannot be null");
    assertSame(Tag.Owner.Nexage, result.getOwner(), "Owner of Tag should be Nexage");
    assertEquals(publisherTag.getTagController().getPid(), result.getTagController().getPid());
    assertEquals(
        publisherTag.getTagController().getAutoExpand(), result.getTagController().getAutoExpand());
    assertEquals(1, result.getRules().size());
    assertEquals("DATA", result.getRules().iterator().next().getTarget());
  }

  @Test
  void when_for_default_rtb_profile_is_false_then_owner_is_publisher() {
    var tagRule = new TagRule();
    var context =
        PublisherTagContext.newBuilder().forRTBProfile(false).withCopyOperation(false).build();

    ReflectionTestUtils.setField(publisherTagAssembler, "userContext", userContext);

    tagRule.setPid(1L);
    tagRule.setTarget("DATA");
    inputTag.setTagController(new TagController(1L, false));
    inputTag.getRules().add(tagRule);

    given(publisherTag.getStatus()).willReturn(Status.ACTIVE);
    given(publisherTag.getBuyer()).willReturn(publisherBuyer);
    given(publisherTag.getRtbProfile()).willReturn(null);
    given(publisherTag.getTagController())
        .willReturn(PublisherTagControllerDTO.builder().pid(1L).autoExpand(true).build());
    given(publisherTag.getRules())
        .willReturn(Set.of(PublisherTagRuleDTO.builder().pid(1L).data("DATA_UPDATED").build()));

    // when
    var result = publisherTagAssembler.apply(context, inputTag, publisherTag);

    // then
    assertNotNull(result, "Assembled Tag cannot be null");
    assertSame(Tag.Owner.Publisher, result.getOwner(), "Owner of Tag should be Publisher");
    assertEquals(publisherTag.getTagController().getPid(), result.getTagController().getPid());
    assertEquals(
        publisherTag.getTagController().getAutoExpand(), result.getTagController().getAutoExpand());
    assertEquals(1, result.getRules().size());
    assertEquals("DATA_UPDATED", result.getRules().iterator().next().getTarget());
  }

  @Test
  void when_copying_then_owner_is_from_the_input_for_publisher_owner() {
    // given
    var context = PublisherTagContext.newBuilder().withCopyOperation(true).build();

    ReflectionTestUtils.setField(publisherTagAssembler, "userContext", userContext);

    given(publisherTag.getStatus()).willReturn(Status.ACTIVE);
    given(publisherTag.getBuyer()).willReturn(publisherBuyer);
    given(publisherTag.getRtbProfile()).willReturn(null);
    given(publisherTag.getOwner()).willReturn(Owner.Publisher);

    // when
    var result = publisherTagAssembler.apply(context, inputTag, publisherTag);

    // then
    assertNotNull(result, "Assembled Tag cannot be null");
    assertSame(Tag.Owner.Publisher, result.getOwner(), "Owner of Tag should be Publisher");
  }

  @Test
  void when_copying_then_owner_is_from_the_input_for_nexage_owner() {
    // given
    var context = PublisherTagContext.newBuilder().withCopyOperation(true).build();

    ReflectionTestUtils.setField(publisherTagAssembler, "userContext", userContext);

    given(publisherTag.getStatus()).willReturn(Status.ACTIVE);
    given(publisherTag.getBuyer()).willReturn(publisherBuyer);
    given(publisherTag.getRtbProfile()).willReturn(null);
    given(publisherTag.getOwner()).willReturn(Owner.Nexage);

    // when
    var result = publisherTagAssembler.apply(context, inputTag, publisherTag);

    // then
    assertNotNull(result, "Assembled Tag cannot be null");
    assertSame(Tag.Owner.Nexage, result.getOwner(), "Owner of Tag should be Nexage");
  }

  @Test
  void shouldAddTagToDefaultRtbProfileDtoBuilderWhenPresent() {
    // given
    var tag = new Tag();
    tag.setPid(1L);
    tag.setStatus(com.nexage.admin.core.enums.Status.ACTIVE);
    tag.setOwner(Tag.Owner.Nexage);

    var defaultRtbProfile = new RTBProfile();
    defaultRtbProfile.setTag(tag);
    var sellerAttributes = new SellerAttributes();
    sellerAttributes.setDefaultRtbProfile(defaultRtbProfile);
    var company = new Company();
    company.setSellerAttributes(sellerAttributes);

    var builder = PublisherDefaultRTBProfileDTO.newBuilder();
    var context = PublisherDefaultRTBProfileContext.newBuilder().withCompany(company).build();
    var fields = Set.of("tag");

    // when
    publisherTagAssembler.addTagToPublisherDefaultRTBProfileDtoBuilder(builder, context, fields);

    // then
    assertEquals(tag.getPid(), builder.build().getTag().getPid());
  }

  @Test
  void shouldNotAddTagToDefaultRtbProfileDtoBuilderWhenNotPresentInFields() {
    // given
    var tag = new Tag();
    tag.setPid(1L);
    tag.setStatus(com.nexage.admin.core.enums.Status.ACTIVE);
    tag.setOwner(Tag.Owner.Nexage);

    var defaultRtbProfile = new RTBProfile();
    defaultRtbProfile.setTag(tag);
    var sellerAttributes = new SellerAttributes();
    sellerAttributes.setDefaultRtbProfile(defaultRtbProfile);
    var company = new Company();
    company.setSellerAttributes(sellerAttributes);

    var builder = PublisherDefaultRTBProfileDTO.newBuilder();
    var context = PublisherDefaultRTBProfileContext.newBuilder().withCompany(company).build();
    Set<String> fields = Set.of();

    // when
    publisherTagAssembler.addTagToPublisherDefaultRTBProfileDtoBuilder(builder, context, fields);

    // then
    assertNull(builder.build().getTag());
  }

  @Test
  void shouldThrowSecurityExceptionOnBuyerPidMisMatch() {
    // given
    var context = PublisherTagContext.newBuilder().withCopyOperation(true).build();
    var tag = new Tag();
    tag.setBuyerPid(1L);

    var publisherBuyerDTO = new PublisherBuyerDTO();
    var publisherTag = new PublisherTagDTO();
    publisherTag.setBuyer(publisherBuyerDTO);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherTagAssembler.apply(context, tag, publisherTag));
    assertEquals(ServerErrorCodes.SERVER_TAG_BUYER_READONLY, exception.getErrorCode());
  }
}

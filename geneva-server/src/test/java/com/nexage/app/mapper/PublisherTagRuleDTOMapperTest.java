package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.TagRule;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTagRuleDTO;
import org.junit.jupiter.api.Test;

class PublisherTagRuleDTOMapperTest {

  private static final PublisherTagRuleDTOMapper MAPPER = PublisherTagRuleDTOMapper.MAPPER;

  @Test
  void shouldReturnValidDtoWhenEntityIsValid() {
    // given
    TagRule entity = makeTagRule();
    // when
    PublisherTagRuleDTO dto = MAPPER.map(entity);
    // then
    assertEquals(entity.getPid(), dto.getPid());
    assertEquals(entity.getVersion(), dto.getVersion());
    assertEquals(entity.getTarget(), dto.getData());
    assertEquals(getDtoTargetType(entity.getTargetType()), dto.getTargetType());
    assertEquals(getDtoRuleType(entity.getRuleType()), dto.getRuleType());
    assertEquals(entity.getParamName(), dto.getParamName());
    assertNull(dto.getTag());
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    // given
    // when
    PublisherTagRuleDTO dto = PublisherTagRuleDTOMapper.MAPPER.map(null);
    // then
    assertNull(dto);
  }

  @Test
  void shouldReturnEmptyDtoWhenEntityIsEmpty() {
    // given
    TagRule entity = new TagRule();
    // when
    PublisherTagRuleDTO dto = MAPPER.map(entity);
    // then
    assertNull(dto.getPid());
    assertNull(dto.getVersion());
    assertNull(dto.getData());
    assertNull(dto.getTargetType());
    assertNull(dto.getRuleType());
    assertNull(dto.getParamName());
    assertNull(dto.getTag());
  }

  @Test
  void shouldReturnValidEntityWhenDtoIsValid() {
    // given
    PublisherTagRuleDTO dto = makePublisherTagRuleDTO();
    // when
    TagRule entity = MAPPER.map(new TagRule(), dto);
    // then
    assertEquals(dto.getParamName(), entity.getParamName());
    assertEquals(dto.getData(), entity.getTarget());
    assertEquals(getEntityTargetType(dto.getTargetType()), entity.getTargetType());
    assertEquals(getEntityRuleType(dto.getRuleType()), entity.getRuleType());
    assertNull(entity.getPid());
    assertNull(entity.getVersion());
    assertNull(entity.getTagPid());
    assertNull(entity.getTag());
  }

  @Test
  void shouldUpdateValidEntityWhenDtoIsValid() {
    // given
    PublisherTagRuleDTO dto = makePublisherTagRuleDTO();
    TagRule entity = makeTagRule();
    TagRule oldEntity = makeTagRule();
    // when
    entity = MAPPER.map(entity, dto);
    // then
    assertEquals(dto.getParamName(), entity.getParamName());
    assertEquals(dto.getData(), entity.getTarget());
    assertEquals(getEntityTargetType(dto.getTargetType()), entity.getTargetType());
    assertEquals(getEntityRuleType(dto.getRuleType()), entity.getRuleType());
    assertEquals(oldEntity.getPid(), entity.getPid());
    assertEquals(oldEntity.getVersion(), entity.getVersion());
    assertEquals(oldEntity.getTagPid(), entity.getTagPid());
    assertEquals(oldEntity.getTag(), entity.getTag());
  }

  @Test
  void shouldReturnEmptyEntityWhenDtoIsEmpty() {
    // given
    PublisherTagRuleDTO dto = PublisherTagRuleDTO.builder().build();
    // when
    TagRule entity = MAPPER.map(new TagRule(), dto);
    // then
    assertNull(entity.getPid());
    assertNull(entity.getVersion());
    assertNull(entity.getTagPid());
    assertNull(entity.getParamName());
    assertNull(entity.getTarget());
    assertNull(entity.getTag());
    assertNull(entity.getTargetType());
    assertNull(entity.getRuleType());
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenEntityIsNull() {
    // given
    PublisherTagRuleDTO dto = makePublisherTagRuleDTO();
    // when
    // then
    assertThrows(IllegalArgumentException.class, () -> MAPPER.map(null, dto));
  }

  @Test
  void shouldReturnEntityUnchangedWhenDtoIsNull() {
    // given
    TagRule entity = makeTagRule();
    // when
    TagRule mappedEntity = MAPPER.map(entity, null);
    // then
    assertEquals(entity, mappedEntity);
  }

  private TagRule makeTagRule() {
    TagRule tagRule = new TagRule();
    tagRule.setPid(775L);
    tagRule.setVersion(0);
    tagRule.setTag(new Tag());
    tagRule.setTarget("BREW,Hiptop,Linux,Motorola,Nokia OS");
    tagRule.setRuleType(TagRule.RuleType.OsVersion);
    tagRule.setTargetType(TagRule.TargetType.Keyword);
    tagRule.setParamName("paramName");
    return tagRule;
  }

  private PublisherTagRuleDTO makePublisherTagRuleDTO() {
    PublisherTagRuleDTO publisherTagRuleDTO = PublisherTagRuleDTO.builder().build();
    publisherTagRuleDTO.setPid(123L);
    publisherTagRuleDTO.setVersion(1);
    publisherTagRuleDTO.setParamName("paramNameDto");
    publisherTagRuleDTO.setData("Nucleusplus,PalmOS,REX,RIM,Symbian,VRTXmc,WebOS,Windows");
    publisherTagRuleDTO.setTargetType(PublisherTagRuleDTO.TargetType.NegKeyword);
    publisherTagRuleDTO.setRuleType(PublisherTagRuleDTO.RuleType.Country);
    publisherTagRuleDTO.setTag(new PublisherTagDTO());
    return publisherTagRuleDTO;
  }

  private PublisherTagRuleDTO.TargetType getDtoTargetType(TagRule.TargetType targetType) {
    return PublisherTagRuleDTO.TargetType.valueOf(targetType.name());
  }

  private PublisherTagRuleDTO.RuleType getDtoRuleType(TagRule.RuleType ruleType) {
    return PublisherTagRuleDTO.RuleType.valueOf(ruleType.name());
  }

  private TagRule.TargetType getEntityTargetType(PublisherTagRuleDTO.TargetType targetType) {
    return TagRule.TargetType.valueOf(targetType.name());
  }

  private TagRule.RuleType getEntityRuleType(PublisherTagRuleDTO.RuleType ruleType) {
    return TagRule.RuleType.valueOf(ruleType.name());
  }
}

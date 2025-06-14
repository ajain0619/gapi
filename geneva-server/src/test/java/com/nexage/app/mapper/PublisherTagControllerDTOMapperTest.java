package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.TagController;
import com.nexage.app.dto.publisher.PublisherTagControllerDTO;
import org.junit.jupiter.api.Test;

class PublisherTagControllerDTOMapperTest {

  private static final PublisherTagControllerDTOMapper MAPPER =
      PublisherTagControllerDTOMapper.MAPPER;

  @Test
  void shouldReturnValidDtoWhenEntityIsValid() {
    // given
    TagController entity = makeTagController();
    // when
    PublisherTagControllerDTO dto = MAPPER.map(entity);
    // then
    assertEquals(entity.getPid(), dto.getPid());
    assertEquals(entity.getAutoExpand(), dto.getAutoExpand());
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    // given
    // when
    PublisherTagControllerDTO dto = MAPPER.map(null);
    // then
    assertNull(dto);
  }

  @Test
  void shouldReturnEmptyDtoWhenEntityIsEmpty() {
    // given
    TagController entity = new TagController();
    // when
    PublisherTagControllerDTO dto = MAPPER.map(entity);
    // then
    assertNull(dto.getPid());
    assertNull(dto.getAutoExpand());
  }

  @Test
  void shouldReturnValidEntityWhenDtoIsValid() {
    // given
    PublisherTagControllerDTO dto = makePublisherTagControllerDTO();
    // when
    TagController entity = MAPPER.map(new TagController(), dto);
    // then
    assertEquals(dto.getPid(), entity.getPid());
    assertEquals(dto.getAutoExpand(), entity.getAutoExpand());
    assertNull(entity.getVersion());
    assertNull(entity.getTag());
  }

  @Test
  void shouldUpdateValidEntityWhenDtoIsValid() {
    // given
    PublisherTagControllerDTO dto = makePublisherTagControllerDTO();
    TagController entity = makeTagController();
    // when
    entity = MAPPER.map(entity, dto);
    // then
    assertEquals(dto.getPid(), entity.getPid());
    assertEquals(dto.getAutoExpand(), entity.getAutoExpand());
    assertNotNull(entity.getVersion());
    assertNotNull(entity.getTag());
  }

  @Test
  void shouldReturnEmptyEntityWhenDtoIsEmpty() {
    // given
    PublisherTagControllerDTO dto = PublisherTagControllerDTO.builder().build();
    // when
    TagController entity = MAPPER.map(new TagController(), dto);
    // then
    assertNull(entity.getVersion());
    assertNull(entity.getPid());
    assertNull(entity.getTag());
    assertNull(entity.getAutoExpand());
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenEntityIsNull() {
    // given
    PublisherTagControllerDTO dto = makePublisherTagControllerDTO();
    // when
    // then
    assertThrows(IllegalArgumentException.class, () -> MAPPER.map(null, dto));
  }

  @Test
  void shouldReturnEntityUnchangedWhenDtoIsNull() {
    // given
    TagController entity = makeTagController();
    // when
    TagController mappedEntity = MAPPER.map(entity, null);
    // then
    assertEquals(entity, mappedEntity);
  }

  private PublisherTagControllerDTO makePublisherTagControllerDTO() {
    return PublisherTagControllerDTO.builder().pid(1L).autoExpand(false).build();
  }

  private TagController makeTagController() {
    TagController tagController = new TagController();
    tagController.setPid(100L);
    tagController.setAutoExpand(true);
    tagController.setTag(new Tag());
    tagController.setVersion(1);
    return tagController;
  }
}

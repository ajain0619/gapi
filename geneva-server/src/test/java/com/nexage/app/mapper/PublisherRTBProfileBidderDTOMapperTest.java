package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

import com.google.common.collect.Sets;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileBidder;
import com.nexage.app.dto.publisher.PublisherRTBProfileBidderDTO;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PublisherRTBProfileBidderDTOMapperTest {

  @Test
  void shouldReturnValidDtoWhenEntityIsValid() {
    // given
    RTBProfileBidder entity = makeRTBProfileBidder();
    // when
    PublisherRTBProfileBidderDTO dto = PublisherRTBProfileBidderDTOMapper.MAPPER.map(entity);
    // then
    assertEquals(entity.getPid(), dto.getPid());
    assertEquals(entity.getBidderPid(), dto.getBidderPid());
    assertEquals(entity.getVersion(), dto.getVersion());
    assertEquals(getSeatList(entity.getSeatWhitelist()), dto.getSeatWhitelist());
    assertEquals(getSeatList(entity.getSeatWhitelist()), dto.getSeatAllowlist());
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    // given
    // when
    PublisherRTBProfileBidderDTO dto = PublisherRTBProfileBidderDTOMapper.MAPPER.map(null);
    // then
    assertNull(dto);
  }

  @Test
  void shouldReturnEmptyDtoWhenEntityIsEmpty() {
    // given
    RTBProfileBidder entity = new RTBProfileBidder();
    // when
    PublisherRTBProfileBidderDTO dto = PublisherRTBProfileBidderDTOMapper.MAPPER.map(entity);
    // then
    assertNull(dto.getPid());
    assertNull(dto.getBidderPid());
    assertNull(dto.getVersion());
    assertNull(dto.getSeatWhitelist());
    assertNull(dto.getSeatAllowlist());
  }

  @Test
  void shouldReturnValidEntityWhenDtoIsValid() {
    // given
    PublisherRTBProfileBidderDTO dto = makePublisherRTBProfileBidderDTO();
    // when
    RTBProfileBidder entity =
        PublisherRTBProfileBidderDTOMapper.MAPPER.map(new RTBProfileBidder(), dto);
    // then
    assertEquals(dto.getVersion(), entity.getVersion());
    assertNull(entity.getPid());
    assertNull(entity.getBidderPid());
    assertNull(entity.getRtbprofile());
    assertEquals(getSeatList(dto.getSeatAllowlist()), entity.getSeatWhitelist());
  }

  @Test
  void shouldUpdateValidEntityWhenDtoIsValid() {
    // given
    PublisherRTBProfileBidderDTO dto = makePublisherRTBProfileBidderDTO();
    RTBProfileBidder entity = makeRTBProfileBidder();
    Long pid = entity.getPid();
    Long bidderPid = entity.getBidderPid();
    // when
    entity = PublisherRTBProfileBidderDTOMapper.MAPPER.map(entity, dto);
    // then
    assertEquals(dto.getVersion(), entity.getVersion());
    assertEquals(pid, entity.getPid());
    assertEquals(bidderPid, entity.getBidderPid());
    assertNull(entity.getRtbprofile());
    assertEquals(getSeatList(dto.getSeatAllowlist()), entity.getSeatWhitelist());
  }

  @Test
  void shouldReturnEmptyEntityWhenDtoIsEmpty() {
    // given
    PublisherRTBProfileBidderDTO dto = new PublisherRTBProfileBidderDTO();
    // when
    RTBProfileBidder entity =
        PublisherRTBProfileBidderDTOMapper.MAPPER.map(new RTBProfileBidder(), dto);
    // then
    assertNull(entity.getVersion());
    assertNull(entity.getPid());
    assertNull(entity.getBidderPid());
    assertNull(entity.getRtbprofile());
    assertNull(entity.getSeatWhitelist());
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenEntityIsNull() {
    // given
    PublisherRTBProfileBidderDTO dto = makePublisherRTBProfileBidderDTO();
    // when
    // then
    assertThrows(
        IllegalArgumentException.class,
        () -> PublisherRTBProfileBidderDTOMapper.MAPPER.map(null, dto));
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDtoIsNull() {
    // given
    RTBProfileBidder entity = makeRTBProfileBidder();
    // when
    // then
    assertThrows(
        IllegalArgumentException.class,
        () -> PublisherRTBProfileBidderDTOMapper.MAPPER.map(entity, null));
  }

  @Test
  void shouldMapSeatListToDto() {
    // given
    var mapper = spy(PublisherRTBProfileBidderDTOMapper.class);
    var bidder = new RTBProfileBidder();
    bidder.setSeatWhitelist("ABC");
    var bidderDto = new PublisherRTBProfileBidderDTO();

    // when
    mapper.mapSeatList(bidder, bidderDto);

    // then
    assertEquals(Set.of("ABC"), bidderDto.getSeatAllowlist());
    assertEquals(Set.of("ABC"), bidderDto.getSeatWhitelist());
  }

  private PublisherRTBProfileBidderDTO makePublisherRTBProfileBidderDTO() {
    PublisherRTBProfileBidderDTO publisherRTBProfileBidderDTO = new PublisherRTBProfileBidderDTO();
    publisherRTBProfileBidderDTO.setPid(101L);
    publisherRTBProfileBidderDTO.setVersion(5);
    publisherRTBProfileBidderDTO.setBidderPid(65L);
    publisherRTBProfileBidderDTO.setSeatAllowlist(Set.of("1", "2"));
    return publisherRTBProfileBidderDTO;
  }

  private RTBProfileBidder makeRTBProfileBidder() {
    RTBProfileBidder rtbProfileBidder = new RTBProfileBidder();
    rtbProfileBidder.setPid(201L);
    rtbProfileBidder.setVersion(95);
    rtbProfileBidder.setBidderPid(678L);
    rtbProfileBidder.setSeatWhitelist("1, 2,3  ,4,   ,");
    return rtbProfileBidder;
  }

  private Set<String> getSeatList(String seatList) {
    return Sets.newHashSet(PublisherRTBProfileBidderDTOMapper.splitter.split(seatList));
  }

  private String getSeatList(Set<String> seatList) {
    return PublisherRTBProfileBidderDTOMapper.joiner.join(seatList);
  }
}

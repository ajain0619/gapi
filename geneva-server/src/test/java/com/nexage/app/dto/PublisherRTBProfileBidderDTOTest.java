package com.nexage.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.app.dto.publisher.PublisherRTBProfileBidderDTO;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

class PublisherRTBProfileBidderDTOTest {

  @Test
  void testSeatWhiteListNullvalue() {
    PublisherRTBProfileBidderDTO publisherRTBProfile = new PublisherRTBProfileBidderDTO();
    publisherRTBProfile.setPid(100L);
    publisherRTBProfile.setBidderPid(50L);
    publisherRTBProfile.setVersion(10);
    Set<String> seatWhiteList = null;
    Set<String> seatAllowList = null;

    assertEquals(100L, publisherRTBProfile.getPid().longValue());
    assertEquals(50L, publisherRTBProfile.getBidderPid().longValue());
    assertEquals(10, publisherRTBProfile.getVersion().intValue());
    assertNull(publisherRTBProfile.getSeatWhitelist());
    assertNull(publisherRTBProfile.getSeatAllowlist());
  }

  @Test
  void testPublisherRTBProfileBidderValue() {
    PublisherRTBProfileBidderDTO publisherRTBProfile =
        new PublisherRTBProfileBidderDTO(
            100L, 10, 50L, Sets.newSet("seat_1", "seat_2"), Sets.newSet("seat_1", "seat_2"));

    assertEquals(100L, publisherRTBProfile.getPid().longValue());
    assertEquals(50L, publisherRTBProfile.getBidderPid().longValue());
    assertEquals(10, publisherRTBProfile.getVersion().intValue());
    Set<String> expectedSeatWhiteList = Sets.newSet("seat_1", "seat_2");
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatWhitelist());
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatAllowlist());
  }

  @Test
  void testSeatWhiteListvalue() {
    PublisherRTBProfileBidderDTO publisherRTBProfile = new PublisherRTBProfileBidderDTO();

    Set<String> seatWhiteList = Sets.newSet("seat_1", "seat_2");
    publisherRTBProfile.setSeatWhitelist(seatWhiteList);

    Set<String> expectedSeatWhiteList = Sets.newSet("seat_1", "seat_2");

    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatWhitelist());
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatAllowlist());
  }

  @Test
  void testsetSeatWhiteListvalueWithSeatAllowListNull() {
    PublisherRTBProfileBidderDTO publisherRTBProfile = new PublisherRTBProfileBidderDTO();

    Set<String> seatAllowList = null;
    Set<String> seatWhiteList = Sets.newSet("seat_1", "seat_2");
    publisherRTBProfile.setSeatWhitelist(seatWhiteList);
    publisherRTBProfile.setSeatAllowlist(seatAllowList);

    Set<String> expectedSeatWhiteList = Sets.newSet("seat_1", "seat_2");

    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatWhitelist());
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatAllowlist());
  }

  @Test
  void testSetAllowListThenSeatWhiteListvalueWithSeatAllowListNull() {
    PublisherRTBProfileBidderDTO publisherRTBProfile = new PublisherRTBProfileBidderDTO();

    Set<String> seatAllowList = null;
    Set<String> seatWhiteList = Sets.newSet("seat_3", "seat_4");
    publisherRTBProfile.setSeatAllowlist(seatAllowList);
    publisherRTBProfile.setSeatWhitelist(seatWhiteList);

    Set<String> expectedSeatWhiteList = Sets.newSet("seat_3", "seat_4");

    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatWhitelist());
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatAllowlist());
  }

  @Test
  void testSeatAllowListvalue() {
    PublisherRTBProfileBidderDTO publisherRTBProfile = new PublisherRTBProfileBidderDTO();

    Set<String> seatAllowList = Sets.newSet("seat_1", "seat_2");
    publisherRTBProfile.setSeatAllowlist(seatAllowList);

    Set<String> expectedSeatWhiteList = Sets.newSet("seat_1", "seat_2");

    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatWhitelist());
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatAllowlist());
  }

  @Test
  void tesBothSeatWhiteAllowListvalueAndGiveHighPerfernceAllowValue() {
    PublisherRTBProfileBidderDTO publisherRTBProfile = new PublisherRTBProfileBidderDTO();

    Set<String> seatAllowList = Sets.newSet("seat_1", "seat_2");
    publisherRTBProfile.setSeatAllowlist(seatAllowList);
    Set<String> seatWhiteList = Sets.newSet("seat_3", "seat_4");
    publisherRTBProfile.setSeatWhitelist(seatWhiteList);

    Set<String> expectedSeatWhiteList = Sets.newSet("seat_1", "seat_2");
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatWhitelist());
    assertEquals(expectedSeatWhiteList, publisherRTBProfile.getSeatAllowlist());
  }
}

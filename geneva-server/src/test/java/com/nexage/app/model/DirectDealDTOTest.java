package com.nexage.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.util.CustomObjectMapper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DirectDealDTOTest {

  private final ObjectMapper mapper = new CustomObjectMapper();

  @Test
  void shouldHaveAllDefaultValues() {
    DirectDealDTO dto = DirectDealDTO.builder().build();

    assertEquals("USD", dto.getCurrency());
    assertEquals(List.of(), dto.getBidders());
    assertEquals(List.of(), dto.getPositions());
    assertEquals(List.of(), dto.getProfiles());
    assertEquals(List.of(), dto.getSellers());
    assertEquals(List.of(), dto.getSites());
    assertEquals(Set.of(), dto.getRules());
    assertEquals(Set.of(), dto.getTargets());
    assertFalse(dto.isAllBidders());
    assertFalse(dto.isAllSellers());
    assertFalse(dto.isVisibility());
    assertNull(dto.getPlacementFormulaStatus());
  }

  @Test
  void shouldSerializeWithPublishersField() throws Exception {
    DirectDealDTO dto =
        DirectDealDTO.builder().auctionType(DirectDealDTO.AuctionType.FIRST_PRICE).build();

    String output = mapper.writeValueAsString(dto);

    assertTrue(output.contains("\"publishers\":[]"));
  }

  @Test
  void shouldMarshalWithCorrectAuctionTypeValue() throws Exception {
    DirectDealDTO dto =
        DirectDealDTO.builder().auctionType(DirectDealDTO.AuctionType.FIRST_PRICE).build();

    String output = mapper.writeValueAsString(dto);

    assertTrue(output.contains("\"auctionType\":\"FirstPrice\""));
  }

  @Test
  void shouldUnmarshalWithCorrectAuctionTypeValue() throws Exception {
    DirectDealDTO dto =
        DirectDealDTO.builder().auctionType(DirectDealDTO.AuctionType.FIRST_PRICE).build();
    String marshaled = mapper.writeValueAsString(dto);

    DirectDealDTO unmarshaled = mapper.readValue(marshaled, DirectDealDTO.class);

    assertEquals(DirectDealDTO.AuctionType.FIRST_PRICE, unmarshaled.getAuctionType());
  }

  @Test
  void shouldMarshalWithCorrectPlacementFormulaStatusValue() throws Exception {
    DirectDealDTO dto =
        DirectDealDTO.builder().placementFormulaStatus(PlacementFormulaStatus.NEW).build();

    String output = mapper.writeValueAsString(dto);

    assertTrue(output.contains("\"placementFormulaStatus\":\"NEW\""));
  }
}

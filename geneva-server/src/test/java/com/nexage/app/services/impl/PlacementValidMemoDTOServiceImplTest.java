package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

import com.nexage.app.dto.seller.PlacementValidMemoDTO;
import com.nexage.app.security.LoginUserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlacementValidMemoDTOServiceImplTest {

  @Mock LoginUserContext userContext;

  @InjectMocks private PlacementValidMemoDTOServiceImpl placementValidMemoDTOService;

  private PlacementValidMemoDTO dto;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void testPlacementValidMemoValidParams() {
    dto = placementValidMemoDTOService.getValidPlacementMemo(123L, 123L, "memo");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo-s%d-t", 123L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(275L, 123L, "memo");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo-s%d-t", 275L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(275L, 123L, "memo4");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo4-s%d-t", 275L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(275L, 123L, "memo4foo");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo4foo-s%d-t", 275L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(275L, 123L, "memo-s10000186-t435946");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo-s%d-t", 275L)));
    assertTrue(dto.isUnique());

    dto =
        placementValidMemoDTOService.getValidPlacementMemo(
            275L, 123L, "memo-s10000186-t435946-s10000186-t435946");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo-s%d-t", 275L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(275L, 123L, "undefined");
    assertTrue(dto.getValidMemo().startsWith(String.format("pl-s%d-t", 275L)));
    assertTrue(dto.isUnique());
  }

  @Test
  void testPlacementValidMemoNullParams() {
    dto = placementValidMemoDTOService.getValidPlacementMemo(null, 123L, "memo");
    assertEquals("", dto.getValidMemo());
    assertFalse(dto.isUnique(), "");

    dto = placementValidMemoDTOService.getValidPlacementMemo(123L, null, "memo");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo-s%d-t", 123L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(123L, null, "memo4");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo4-s%d-t", 123L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(123L, null, "memo4foo");
    assertTrue(dto.getValidMemo().startsWith(String.format("memo4foo-s%d-t", 123L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(123L, 123L, null);
    assertTrue(dto.getValidMemo().startsWith(String.format("pl-s%d-t", 123L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(null, null, "memo");
    assertEquals("", dto.getValidMemo());
    assertFalse(dto.isUnique(), "");

    dto = placementValidMemoDTOService.getValidPlacementMemo(123L, null, null);
    assertTrue(dto.getValidMemo().startsWith(String.format("pl-s%d-t", 123L)));
    assertTrue(dto.isUnique());

    dto = placementValidMemoDTOService.getValidPlacementMemo(null, null, null);
    assertEquals("", dto.getValidMemo());
    assertFalse(dto.isUnique(), "");
  }
}

package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HbPartnerPositionMapperTest {

  final Position position = mock(Position.class);
  final HbPartnerRepository hbPartnerRepository = mock(HbPartnerRepository.class);

  Long PID = 1L;
  String EXTERNAL_ID = "Test_123";

  @Test
  void test_apply_HbPartnerPosition_NotFound_HbPartner() {
    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.empty());
    var hbPartnerAssignmentDTOSet =
        createHbPartnerDTOSet(PID, EXTERNAL_ID, AssociationType.DEFAULT);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                HbPartnerPositionMapper.MAPPER.map(
                    hbPartnerAssignmentDTOSet, position, hbPartnerRepository));

    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void test_apply_HbPartnerPosition_NullType() {
    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(createHbPartner(PID)));
    Set<HbPartnerPosition> hbPartnerPositions =
        HbPartnerPositionMapper.MAPPER.map(
            createHbPartnerDTOSet(PID, EXTERNAL_ID, null), position, hbPartnerRepository);
    HbPartnerPosition hbPartnerPosition = hbPartnerPositions.stream().findFirst().get();
    assertEquals(0, hbPartnerPosition.getType(), "Invalid hb partner  type");
  }

  @Test
  void test_apply_HbPartnerPosition() {

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(createHbPartner(PID)));
    Set<HbPartnerPosition> hbPartnerPositions =
        HbPartnerPositionMapper.MAPPER.map(
            createHbPartnerDTOSet(PID, EXTERNAL_ID, AssociationType.DEFAULT),
            position,
            hbPartnerRepository);
    Optional<HbPartnerPosition> hbPartnerSiteOptional = hbPartnerPositions.stream().findFirst();
    HbPartnerPosition hbPartnerPosition = hbPartnerSiteOptional.get();
    assertEquals(hbPartnerPosition.getHbPartner().getPid(), PID, "Invalid Hb Partner");
    assertEquals(hbPartnerPosition.getExternalPositionId(), EXTERNAL_ID, "Invalid External Pub Id");
    assertEquals(1, hbPartnerPosition.getType(), "Invalid hb partner  type");
    assertNotNull(hbPartnerPosition.getPosition(), "Company should not be null");
  }

  private HbPartner createHbPartner(Long pid) {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(pid);
    hbPartner.setName("Test_HbPartner");
    hbPartner.setId("Test_Id");
    return hbPartner;
  }

  private Set<HbPartnerAssignmentDTO> createHbPartnerDTOSet(
      Long pid, String externalId, AssociationType type) {
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(pid);
    hbPartnerAssignmentDTO.setExternalId(externalId);
    hbPartnerAssignmentDTO.setType(type);
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet = Sets.newHashSet();
    hbPartnerAssignmentDTOSet.add(hbPartnerAssignmentDTO);
    return hbPartnerAssignmentDTOSet;
  }
}

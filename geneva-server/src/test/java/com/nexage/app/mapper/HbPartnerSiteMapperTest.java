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
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HbPartnerSiteMapperTest {

  final Site site = mock(Site.class);
  final HbPartnerRepository hbPartnerRepository = mock(HbPartnerRepository.class);

  Long PID = 1L;
  String EXTERNAL_ID = "Test_123";

  @Test
  void test_map_HbPartnerSite_NotFound_HbPartner() {
    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.empty());
    var hbPartnerAssignmentDTOSet =
        createHbPartnerDTOSet(PID, EXTERNAL_ID, AssociationType.DEFAULT);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                HbPartnerSiteMapper.MAPPER.map(
                    hbPartnerAssignmentDTOSet, site, hbPartnerRepository));
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void test_map_HbPartnerSite() {

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(createHbPartner(PID)));
    Set<HbPartnerSite> hbPartnerSites =
        HbPartnerSiteMapper.MAPPER.map(
            createHbPartnerDTOSet(PID, EXTERNAL_ID, AssociationType.DEFAULT),
            site,
            hbPartnerRepository);
    Optional<HbPartnerSite> hbPartnerSiteOptional = hbPartnerSites.stream().findFirst();
    HbPartnerSite hbPartnerSite = hbPartnerSiteOptional.get();
    assertEquals(PID, hbPartnerSite.getHbPartner().getPid(), "Invalid Hb Partner");
    assertEquals(EXTERNAL_ID, hbPartnerSite.getExternalSiteId(), "Invalid External Pub Id");
    assertEquals(1, hbPartnerSite.getType(), "Invalid hb partner  type");
    assertNotNull(hbPartnerSite.getSite(), "Company should not be null");
  }

  @Test
  void test_map_HbPartnerSite_NullType() {

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(createHbPartner(PID)));
    Set<HbPartnerSite> hbPartnerSites =
        HbPartnerSiteMapper.MAPPER.map(
            createHbPartnerDTOSet(PID, EXTERNAL_ID, null), site, hbPartnerRepository);
    Optional<HbPartnerSite> hbPartnerSiteOptional = hbPartnerSites.stream().findFirst();
    HbPartnerSite hbPartnerSite = hbPartnerSiteOptional.get();
    assertEquals(PID, hbPartnerSite.getHbPartner().getPid(), "Invalid Hb Partner");
    assertEquals(EXTERNAL_ID, hbPartnerSite.getExternalSiteId(), "Invalid External Pub Id");
    assertEquals(0, hbPartnerSite.getType(), "Invalid hb partner  type");
    assertNotNull(hbPartnerSite.getSite(), "Company should not be null");
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

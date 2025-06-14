package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerCompany;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HbPartnerCompanyMapperTest {

  final Company company = mock(Company.class);

  final HbPartnerRepository hbPartnerRepository = mock(HbPartnerRepository.class);

  Long PID = 1L;
  String EXTERNAL_ID = "Test_123";

  @Test
  void test_map_HbPartnerCompany_NotFound_HbPartner() {
    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.empty());
    var hbPartnerAssignmentDTOSet = createHbPartnerDTOSet(PID, EXTERNAL_ID);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                HbPartnerCompanyMapper.MAPPER.map(
                    hbPartnerAssignmentDTOSet, company, hbPartnerRepository));

    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void test_map_HbPartnerCompany() {

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(createHbPartner(PID)));
    Set<HbPartnerCompany> hbPartnerCompanies =
        HbPartnerCompanyMapper.MAPPER.map(
            createHbPartnerDTOSet(PID, EXTERNAL_ID), company, hbPartnerRepository);
    Optional<HbPartnerCompany> hbPartnerCompanyOptional = hbPartnerCompanies.stream().findFirst();
    HbPartnerCompany hbPartnerCompany = hbPartnerCompanyOptional.get();
    assertEquals(hbPartnerCompany.getHbPartner().getPid(), PID, "Invalid Hb Partner");
    assertEquals(hbPartnerCompany.getExternalPubId(), EXTERNAL_ID, "Invalid External Pub Id");
    assertNotNull(hbPartnerCompany.getCompany(), "Company should not be null");
  }

  private HbPartner createHbPartner(Long pid) {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(pid);
    hbPartner.setName("Test_HbPartner");
    hbPartner.setId("Test_Id");
    return hbPartner;
  }

  private Set<HbPartnerAssignmentDTO> createHbPartnerDTOSet(Long pid, String externalId) {
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(pid);
    hbPartnerAssignmentDTO.setExternalId(externalId);
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet = Sets.newHashSet();
    hbPartnerAssignmentDTOSet.add(hbPartnerAssignmentDTO);
    return hbPartnerAssignmentDTOSet;
  }
}

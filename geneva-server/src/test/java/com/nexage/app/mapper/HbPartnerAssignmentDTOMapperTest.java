package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerCompany;
import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class HbPartnerAssignmentDTOMapperTest {

  @Mock private Company company;

  @Mock private Site site;

  @Mock private Position position;

  @Test
  void mapHbPartnerCompanyNull_test() {
    Set<HbPartnerCompany> hbPartnerCompanies = null;
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerCompany(hbPartnerCompanies);
    assertNull(hbPartnerAssignmentDTOSet);
  }

  @Test
  void mapHbPartnerSiteNull_test() {
    Set<HbPartnerSite> hbPartnerSites = null;
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerSite(hbPartnerSites);
    assertNull(hbPartnerAssignmentDTOSet);
  }

  @Test
  void mapHbPartnerPositionNull_test() {
    Set<HbPartnerPosition> hbPartnerPositions = null;
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerPosition(hbPartnerPositions);
    assertNull(hbPartnerAssignmentDTOSet);
  }

  @Test
  void mapHbPartnerCompany_test() {

    Set<HbPartnerCompany> hbPartnerCompanies = Sets.newHashSet();
    HbPartnerCompany hbPartnerCompany = new HbPartnerCompany();
    hbPartnerCompany.setPid(123L);
    hbPartnerCompany.setCompany(company);
    hbPartnerCompany.setExternalPubId("123Test");
    hbPartnerCompany.setHbPartner(createHbPartner(12L));
    hbPartnerCompanies.add(hbPartnerCompany);

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerCompany(hbPartnerCompanies);

    Optional<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOOptional =
        hbPartnerAssignmentDTOSet.stream().findFirst();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = hbPartnerAssignmentDTOOptional.get();
    assertEquals(
        hbPartnerAssignmentDTO.getHbPartnerPid(),
        hbPartnerCompany.getHbPartner().getPid(),
        "Invalid HbPartner PID");
    assertEquals(
        hbPartnerAssignmentDTO.getExternalId(),
        hbPartnerCompany.getExternalPubId(),
        "Invalid External ID");
  }

  @Test
  void makeFromHbPartnerSite_test() {

    Set<HbPartnerSite> hbPartnerSites = Sets.newHashSet();
    HbPartnerSite hbPartnerSite = new HbPartnerSite();
    hbPartnerSite.setPid(123L);
    hbPartnerSite.setSite(site);
    hbPartnerSite.setExternalSiteId("123Test");
    hbPartnerSite.setType(1);
    hbPartnerSite.setHbPartner(createHbPartner(12L));
    hbPartnerSites.add(hbPartnerSite);

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerSite(hbPartnerSites);

    Optional<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOOptional =
        hbPartnerAssignmentDTOSet.stream().findFirst();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = hbPartnerAssignmentDTOOptional.get();
    assertEquals(
        hbPartnerAssignmentDTO.getHbPartnerPid(),
        hbPartnerSite.getHbPartner().getPid(),
        "Invalid HbPartner PID");
    assertEquals(
        hbPartnerAssignmentDTO.getExternalId(),
        hbPartnerSite.getExternalSiteId(),
        "Invalid External ID");
    assertEquals(AssociationType.DEFAULT, hbPartnerAssignmentDTO.getType(), "Invalid External ID");
  }

  @Test
  void makeFromHbPartnerSite_testDefaultType() {

    Set<HbPartnerSite> hbPartnerSites = Sets.newHashSet();
    HbPartnerSite hbPartnerSite = new HbPartnerSite();
    hbPartnerSites.add(hbPartnerSite);

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerSite(hbPartnerSites);

    Optional<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOOptional =
        hbPartnerAssignmentDTOSet.stream().findFirst();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = hbPartnerAssignmentDTOOptional.get();
    assertEquals(
        AssociationType.NON_DEFAULT, hbPartnerAssignmentDTO.getType(), "Invalid External ID");
  }

  @Test
  void makeFromHbPartnerPosition_test() {

    Set<HbPartnerPosition> hbPartnerPositions = Sets.newHashSet();
    HbPartnerPosition hbPartnerPosition = new HbPartnerPosition();
    hbPartnerPosition.setPid(123L);
    hbPartnerPosition.setPosition(position);
    hbPartnerPosition.setExternalPositionId("123Test");
    hbPartnerPosition.setType(1);
    hbPartnerPosition.setHbPartner(createHbPartner(12L));
    hbPartnerPositions.add(hbPartnerPosition);

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerPosition(hbPartnerPositions);

    Optional<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOOptional =
        hbPartnerAssignmentDTOSet.stream().findFirst();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = hbPartnerAssignmentDTOOptional.get();
    assertEquals(
        hbPartnerAssignmentDTO.getHbPartnerPid(),
        hbPartnerPosition.getHbPartner().getPid(),
        "Invalid HbPartner PID");
    assertEquals(
        hbPartnerAssignmentDTO.getExternalId(),
        hbPartnerPosition.getExternalPositionId(),
        "Invalid External ID");
    assertEquals(AssociationType.DEFAULT, hbPartnerAssignmentDTO.getType(), "Invalid External ID");
  }

  @Test
  void makeFromHbPartnerPosition_testDefaultType() {

    Set<HbPartnerPosition> hbPartnerPositions = Sets.newHashSet();
    HbPartnerPosition hbPartnerPosition = new HbPartnerPosition();
    hbPartnerPositions.add(hbPartnerPosition);

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet =
        HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerPosition(hbPartnerPositions);

    Optional<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOOptional =
        hbPartnerAssignmentDTOSet.stream().findFirst();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = hbPartnerAssignmentDTOOptional.get();
    assertEquals(
        AssociationType.NON_DEFAULT, hbPartnerAssignmentDTO.getType(), "Invalid External ID");
  }

  private HbPartner createHbPartner(Long pid) {
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(pid);
    hbPartner.setName("Test_HbPartner");
    hbPartner.setId("Test_Id");
    return hbPartner;
  }
}

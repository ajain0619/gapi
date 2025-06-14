package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HbPartnerSiteServiceImplTest {

  @InjectMocks private HbPartnerSiteServiceImpl hbPartnerSiteService;

  HbPartner hbPartner1;
  HbPartner hbPartner2;

  @BeforeEach
  public void setup() {
    hbPartner1 = TestObjectsFactory.createHbPartner();
    hbPartner2 = TestObjectsFactory.createHbPartner();
  }

  @Test
  void validateHbPartnerAssociations_testDeletingHbPartnerAssociation() {
    PublisherSiteDTO publisherSite = new PublisherSiteDTO();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(hbPartner1.getPid());
    publisherSite.setHbPartnerAttributes(Sets.newHashSet(hbPartnerAssignmentDTO));
    Site siteDTO = TestObjectsFactory.createSiteDTO(1).get(0);
    Position position = getPosition("testPosition");
    siteDTO.setHbPartnerSite(
        Sets.newHashSet(
            TestObjectsFactory.createHbPartnerSite(siteDTO, hbPartner1, "test1"),
            TestObjectsFactory.createHbPartnerSite(siteDTO, hbPartner2, "test2")));
    HbPartnerPosition hbPartnerPosition1 = getHbPartnerPostion(1L, position, hbPartner1);
    HbPartnerPosition hbPartnerPosition2 = getHbPartnerPostion(2L, position, hbPartner2);
    position.setHbPartnerPosition(Sets.newHashSet(hbPartnerPosition1, hbPartnerPosition2));
    siteDTO.setPositions(Sets.newHashSet(position));

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> hbPartnerSiteService.validateHbPartnerAssociations(siteDTO, publisherSite));

    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_SITE_ASSOCIATION_DELETE_INVALID,
        exception.getErrorCode());
  }

  private HbPartnerPosition getHbPartnerPostion(long pid, Position position, HbPartner hbPartner) {
    HbPartnerPosition hbPartnerPosition = new HbPartnerPosition();
    hbPartnerPosition.setPid(pid);
    hbPartnerPosition.setPosition(position);
    hbPartnerPosition.setHbPartner(hbPartner);
    return hbPartnerPosition;
  }

  private Position getPosition(String name) {
    Position position = new Position();
    position.setPid(1L);
    position.setName(name);

    return position;
  }
}

package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.app.dto.BdrAdvertiserDTO;
import org.junit.jupiter.api.Test;

class BdrAdvertiserDTOMapperTest {

  public static final String DOMAIN_NAME = "DOMAIN NAME";
  public static final String NAME = "NAME";
  public static final String ACTIVE = "ACTIVE";
  public static final String IAB_CATEGORY = "IAB_CATEGORY";

  @Test
  void shouldMapToDTO() {
    // given
    BdrAdvertiserDTO bdrAdvertiserDTO = new BdrAdvertiserDTO();
    bdrAdvertiserDTO.setName(NAME);
    bdrAdvertiserDTO.setStatus(ACTIVE);
    bdrAdvertiserDTO.setDomainName(DOMAIN_NAME);
    bdrAdvertiserDTO.setIabCategory(IAB_CATEGORY);

    // when
    BDRAdvertiser bdrAdvertiser = BdrAdvertiserDTOMapper.MAPPER.map(bdrAdvertiserDTO);

    // then
    assertEquals(NAME, bdrAdvertiser.getName());
    assertEquals(ACTIVE, bdrAdvertiser.getStatus().toString());
    assertEquals(DOMAIN_NAME, bdrAdvertiser.getDomainName());
    assertEquals(IAB_CATEGORY, bdrAdvertiser.getIabCategory());
  }
}

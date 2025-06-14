package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.BillingSource;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.app.dto.BidderConfigDTOView;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

class BidderConfigDTOViewMapperTest {

  @Test
  void shouldMapEntityToSummaryDTO() {
    BidderConfig source = TestObjectsFactory.createBidderConfig();
    source.setId(RandomStringUtils.randomAlphanumeric(8));
    source.setCompanyPid(new Random().nextLong());
    source.setName("BidderConfig1");
    source.setVersion(1);
    source.setBillingSource(BillingSource.ONE_MOBILE);
    source.setDefaultBidCurrency("USD");
    source.setFormatType(BidderFormat.OpenRTBv2_4);

    BidderConfigDTOView resultDTO = BidderConfigDTOViewMapper.MAPPER.map(source);

    assertNotNull(resultDTO);
    assertEquals(resultDTO.getPid(), source.getPid());
    assertEquals(resultDTO.getCompanyPid(), source.getCompanyPid());
    assertEquals(resultDTO.getId(), source.getId());
    assertEquals(resultDTO.getName(), source.getName());
    assertEquals(resultDTO.getVersion(), source.getVersion());
    assertEquals(resultDTO.getBillingSource(), source.getBillingSource().toString());
    assertEquals(resultDTO.getDefaultBidCurrency(), source.getDefaultBidCurrency());
    assertEquals(resultDTO.getFormatType(), source.getFormatType().toString());
  }
}

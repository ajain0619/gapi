package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.admin.core.model.RuleDSPBiddersView;
import com.nexage.app.dto.RuleDSPBiddersDTO;
import com.nexage.app.dto.deals.DealBidderDTO;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RuleDSPBiddersDTOMapperTest {

  @Test
  void shouldMap() {
    final long dspPid = new Random().nextLong();
    final String dspName = UUID.randomUUID().toString();

    RuleDSPBiddersView source = new RuleDSPBiddersView();
    source.setName(dspName);
    source.setPid(dspPid);

    DealBidderConfigView sourceBidder = new DealBidderConfigView();
    final long bidderPid = new Random().nextLong();
    final String bidderName = UUID.randomUUID().toString();
    sourceBidder.setPid(bidderPid);
    sourceBidder.setName(bidderName);
    sourceBidder.setCompanyPid(dspPid);

    source.setBidders(Collections.singleton(sourceBidder));

    RuleDSPBiddersDTO result = RuleDSPBiddersDTOMapper.MAPPER.map(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
    assertNotNull(result.getBidders());
    assertEquals(1, result.getBidders().size());

    DealBidderDTO bidderTarget = result.getBidders().iterator().next();
    assertNotNull(bidderTarget);
    assertEquals(bidderTarget.getPid(), sourceBidder.getPid());
    assertEquals(bidderTarget.getName(), sourceBidder.getName());
    assertEquals(bidderTarget.getCompanyPid(), sourceBidder.getCompanyPid());
  }
}

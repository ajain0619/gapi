package com.nexage.app.mapper.deals;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.sparta.jpa.model.DealTermView;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm.RevenueMode;
import com.nexage.app.mapper.deal.DealTermViewMapper;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DealTermViewMapperTest {

  @Test
  void testMapping() {
    var in = new DealTermView();
    in.setPid(1L);
    in.setRevenueMode(RevenueMode.REV_SHARE);
    in.setRtbFee(new BigDecimal(0.1));
    in.setNexageRevenueShare(new BigDecimal(0.2));
    in.setEffectiveDate(new Date());

    var out = DealTermViewMapper.MAPPER.map(in);
    assertAll(
        "deal term view mapper",
        () -> assertEquals(in.getPid(), out.getPid()),
        () -> assertEquals(in.getRevenueMode(), out.getRevenueMode()),
        () -> assertEquals(in.getNexageRevenueShare(), out.getNexageRevenueShare()),
        () -> assertEquals(in.getRtbFee(), out.getRtbFee()),
        () -> assertEquals(in.getEffectiveDate(), out.getEffectiveDate()));
  }
}

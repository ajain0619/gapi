package com.nexage.app.util.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import org.junit.jupiter.api.Test;

class PublisherBuyerAssemblerTest {

  @Test
  void testMake_logoUrl() {
    String logoUrl = "http://geneva-crud.sbx/creative/buyer_logo/7061-1507318471319.jpg";

    AdSourceSummaryDTO adSourceSummary = TestObjectsFactory.createAdSourceSummary();
    adSourceSummary.setLogoUrl(logoUrl);

    PublisherBuyerAssembler assembler = new PublisherBuyerAssembler();
    PublisherBuyerDTO actualBuyer = assembler.make(null, adSourceSummary);

    assertEquals(logoUrl, actualBuyer.getLogoUrl());
  }
}

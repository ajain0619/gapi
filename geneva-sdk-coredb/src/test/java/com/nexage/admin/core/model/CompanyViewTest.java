package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ssp.geneva.common.model.inventory.CompanyType;
import org.junit.jupiter.api.Test;

class CompanyViewTest {

  @Test
  void shouldIntialiseCompanyView() {
    long pid = 1L;
    String name = "company 1";
    CompanyType type = CompanyType.SELLER;
    long revenueGroupPid = 2L;
    SellerAttributesView sellerAttributesView = new SellerAttributesView(pid, revenueGroupPid);

    CompanyView companyView = new CompanyView(pid, name, type, true, sellerAttributesView);

    assertEquals(pid, companyView.getPid());
    assertEquals(name, companyView.getName());
    assertEquals(type, companyView.getType());
    assertEquals(pid, companyView.getSellerAttributes().getSellerPid());
    assertEquals(revenueGroupPid, companyView.getSellerAttributes().getRevenueGroupPid());
  }
}

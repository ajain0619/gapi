package com.nexage.geneva.step;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.DealSupplier;
import com.nexage.geneva.model.crud.Site;
import com.nexage.geneva.request.DealSupplierRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.TestUtils;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONArray;

/** Created by seanryan on 24/02/2016. */
public class DealSupplierSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private DealSupplierRequests dealSupplierRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<String, DealSupplier> supplierMap;

  private String supplier;

  private static Site site;

  private static String result;

  @When("^the user searches for all suppliers$")
  public void the_user_searches_for_all_suppliers() throws Throwable {
    commonSteps.requestMap = new RequestParams().getRequestParams();
    commonSteps.request =
        dealSupplierRequests.getGetAllSuppliersRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);

    if (commonSteps.serverResponse != null) {
      JSONArray supplierArray = commonSteps.serverResponse.array();
      supplierMap = new HashMap<>();
      for (int i = 0; i < supplierArray.length(); i++) {
        DealSupplier dealSupplier =
            TestUtils.mapper.readValue(supplierArray.get(i).toString(), DealSupplier.class);
        supplierMap.put(dealSupplier.getPid(), dealSupplier);
      }
    }
  }

  @When("^the user searches for supplier by supplier id \"(.+?)\"$")
  public void the_user_searches_for_suppliers_by_suppliers_id(String profileId) throws Throwable {
    assertNotNull(supplierMap, "Suppliers are not found");
    String pid = supplierMap.get(profileId).getPid();
    commonSteps.requestMap = new RequestParams().setProfilePid(pid).getRequestParams();
    commonSteps.request =
        dealSupplierRequests.getGetSupplierRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for an \"(archived|inactive)\" supplier by id \"(.+?)\"$")
  public void the_user_searches_for_archived_supplier_by_id(String type, String rtbprofileId)
      throws Throwable {
    assertNotNull(supplierMap, "Suppliers are not found");
    supplier = null;
    for (Map.Entry<String, DealSupplier> entry : supplierMap.entrySet()) {
      if (entry.getKey().equals(rtbprofileId)) {
        supplier = entry.getKey();
      }
    }
  }

  @Then("^the archived supplier \"(.+?)\" should not be available$")
  public void the_archived_supplier_should_not_be_available(String rtbprofileId) throws Throwable {
    assertNull(supplier, "Archived tag is showing up in the supplier list");
  }

  @Then("^the inactive supplier \"(.+?)\" should be available$")
  public void the_inactive_supplier_should_be_available(String rtbprofileId) throws Throwable {
    assertNotNull(supplier, "Inactive tag is not showing up in the supplier list");
  }

  @When("^site \"(.+?)\" is retrieved from database$")
  public void site_is_retrieved_from_database(String sitePid) throws Throwable {
    site = databaseUtils.getSiteSummaryByPid(sitePid);
    assertNotNull(site, "site " + sitePid + " not exists in database.");
  }

  @When(
      "^column name \"(.+?)\" that contains tag name \"(.+?)\" with site name \"(.+?)\" is retrieved from database$")
  public void deal_supplier_attributes_is_retrieved_from_database(
      String columnName, String description, String siteNameAlias) throws Throwable {
    result =
        databaseUtils.getDealSupplierByColumnNameAndDescAndSiteNameAlias(
            columnName, description, siteNameAlias);
    assertNotNull(
        result, "Deal Supplier with site name alias " + siteNameAlias + " not exists in database.");
  }
}

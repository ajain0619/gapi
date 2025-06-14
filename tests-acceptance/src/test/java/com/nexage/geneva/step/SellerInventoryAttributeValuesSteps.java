package com.nexage.geneva.step;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.SellerInventoryAttributeValuesRequests;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class SellerInventoryAttributeValuesSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private SellerInventoryAttributeValuesRequests attributeValuesRequests;

  private int attributePid;
  private int sellerPid;
  private int attributeValuePid;
  private JSONObject expectedJsonObject;

  @When("^the user creates inventory attribute value from the json file \"(.+?)\"$")
  public void create_seller_inventory_attribute_value(String fileName) throws Throwable {
    expectedJsonObject = JsonHandler.getJsonObjectFromFile(fileName);
    var requestMap =
        new RequestParams()
            .setSellerPid(Integer.toString(sellerPid))
            .setAttributePid(Integer.toString(attributePid));
    commonSteps.request =
        attributeValuesRequests
            .createInventoryAtributeValueForSeller()
            .setRequestPayload(expectedJsonObject)
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user selects seller PID (\\d+) and inventory attribute PID (\\d+)$")
  public void select_inventory_attribute_pid(int sellerPid, int attributePid) {
    this.sellerPid = sellerPid;
    this.attributePid = attributePid;
  }

  @When(
      "^the user selects seller PID (\\d+) and inventory attribute PID (\\d+) and attribute value PID (\\d+)$")
  public void select_inventory_attribute_value_pid(
      int sellerPid, int attributePid, int attributeValuePid) {
    this.sellerPid = sellerPid;
    this.attributePid = attributePid;
    this.attributeValuePid = attributeValuePid;
  }

  @When("^user gets all inventory attribute values")
  public void get_all_inventory_attribute_values() throws Throwable {
    var requestMap =
        new RequestParams()
            .setSellerPid(Integer.toString(sellerPid))
            .setAttributePid(Integer.toString(attributePid));
    commonSteps.request =
        attributeValuesRequests
            .getAllInventoryAttributeValues()
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^user updates inventory attribute value from the json file \"(.+?)\"$")
  public void update_inventory_attribute_value(String fileName) throws Throwable {
    expectedJsonObject = JsonHandler.getJsonObjectFromFile(fileName);
    var requestMap =
        new RequestParams()
            .setSellerPid(Integer.toString(sellerPid))
            .setAttributePid(Integer.toString(attributePid))
            .setAttributeValuePid(Integer.toString(attributeValuePid));
    commonSteps.request =
        attributeValuesRequests
            .putInventoryAttributeValue()
            .setRequestPayload(expectedJsonObject)
            .setRequestParams(requestMap.getRequestParams());
    commonFunctions.executeRequest(commonSteps);
  }
}

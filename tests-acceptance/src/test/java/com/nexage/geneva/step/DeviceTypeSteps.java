package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.DeviceType;
import com.nexage.geneva.request.DeviceTypeRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceTypeSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private DeviceTypeRequests deviceTypeRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<Integer, DeviceType> deviceTypeMap;

  @When("^the user searches for all device types$")
  public void the_user_searches_for_all_device_types() throws Throwable {
    commonSteps.request = deviceTypeRequests.getGetAllDeviceTypesRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for device types with params \"(.*?)\" and search term \"(.*?)\"$")
  public void search_for_device_type(String qf, String qt) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(qt).setqf(qf).getRequestParams();

    commonSteps.request =
        deviceTypeRequests.searchDeviceType().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.DeviceOsRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceOsSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private DeviceOsRequests deviceOsRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<Integer, DeviceOsSteps> deviceTypeMap;

  @When("^the user searches for all device os$")
  public void the_user_searches_for_all_device_os() throws Throwable {
    commonSteps.request = deviceOsRequests.getGetAllDeviceOsRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for device os with query field \"(.*?)\" and search term \"(.*?)\"$")
  public void search_for_device_os(String qf, String qt) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(qt).setqf(qf).getRequestParams();

    commonSteps.request =
        deviceOsRequests.searchDeviceOs().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^insert device os data of ids \"(.*?)\" and names \"(.*?)\"$")
  public void insertData(String ids, String names) {
    AtomicInteger index = new AtomicInteger(0);
    final String[] strData = names.split(",");
    Map<Long, String> data =
        Arrays.stream(ids.split(","))
            .collect(Collectors.toMap(Long::valueOf, str -> strData[index.getAndAdd(1)]));

    databaseUtils.insertDeviceOs(data);
  }
}

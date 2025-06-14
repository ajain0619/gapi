package com.nexage.geneva.step;

import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.request.RevenueGroupsRequests;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class RevenueGroupSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private RevenueGroupsRequests revenueGroupsRequests;

  @When("^the user gets revenue groups$")
  public void the_user_gets_all_revenue_groups() throws Throwable {
    commonSteps.request = revenueGroupsRequests.getGetRevenueGroupsRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets revenue groups page \"(.+?)\" of size \"(.+?)\"$")
  public void the_user_gets_all_revenue_groups_with_paging(String page, String size)
      throws Throwable {
    commonSteps.requestMap = new RequestParams().setPage(page).setSize(size).getRequestParams();
    commonSteps.request =
        revenueGroupsRequests
            .getGetRevenueGroupsRequestWithPaging()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

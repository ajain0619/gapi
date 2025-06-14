package com.nexage.geneva.step;

import com.nexage.geneva.request.DspRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class DspSteps {

  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private DspRequests dspRequests;

  @When("^the user requests all DSPs$")
  public void the_user_requests_all_dsps() throws Throwable {
    executeDSPPageRequest("0", "1000");
  }

  @When("^the user requests a page of DSPs with page \"(.+?)\", size \"(.+?)\"$")
  public void the_user_requests_a_page_of_dsps(String page, String size) throws Throwable {
    executeDSPPageRequest(page, size);
  }

  @When("^the user requests all DSP summaries$")
  public void the_user_requests_all_dsp_summaries() throws Throwable {
    executeDSPSummaryPageRequest("0", "1000");
  }

  @When("^the user requests a page of DSP summaries with page \"(.+?)\", size \"(.+?)\"$")
  public void the_user_requests_a_page_of_dsp_summaries(String page, String size) throws Throwable {
    executeDSPSummaryPageRequest(page, size);
  }

  @When("^the user requests a DSP with name \"(.+?)\"$")
  public void the_user_requests_a_dsp_by_name(String name) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(name).getRequestParams();
    commonSteps.request = dspRequests.getDSPByName().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void executeDSPPageRequest(String page, String size) throws Throwable {
    commonSteps.requestMap = new RequestParams().setPage(page).setSize(size).getRequestParams();
    commonSteps.request = dspRequests.getPageOfDSPs().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  private void executeDSPSummaryPageRequest(String page, String size) throws Throwable {
    commonSteps.requestMap = new RequestParams().setPage(page).setSize(size).getRequestParams();
    commonSteps.request =
        dspRequests.getPageOfDSPSummaries().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

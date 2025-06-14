package com.nexage.geneva.step;

import com.nexage.geneva.request.PlaylistRenderingCapabilityRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;

public class PlaylistRenderingCapabilitySteps {

  private final CommonFunctions commonFunctions;
  private final CommonSteps commonSteps;
  private final PlaylistRenderingCapabilityRequests playlistRenderingCapabilityRequests;

  PlaylistRenderingCapabilitySteps(
      CommonFunctions commonFunctions,
      CommonSteps commonSteps,
      PlaylistRenderingCapabilityRequests playlistRenderingCapabilityRequests) {
    this.commonFunctions = commonFunctions;
    this.commonSteps = commonSteps;
    this.playlistRenderingCapabilityRequests = playlistRenderingCapabilityRequests;
  }

  @When("get playlist rendering capabilities")
  public void getManyWithDefaultPaging() throws Throwable {
    commonSteps.request = playlistRenderingCapabilityRequests.getSdkCapabilities();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^get playlist rendering capabilities page \"([0-9]*)\" when page size is \"([0-9]*)\"$")
  public void getManyWithCustomPaging(String page, String size) throws Throwable {
    commonSteps.requestMap = new RequestParams().setPage(page).setSize(size).getRequestParams();
    commonSteps.request =
        playlistRenderingCapabilityRequests
            .getSdkCapabilitiesWithCustomPaging()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

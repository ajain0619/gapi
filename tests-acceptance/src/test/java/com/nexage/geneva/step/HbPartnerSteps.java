package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.HbPartnerRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import com.nexage.geneva.util.JsonHandler;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import us.monoid.json.JSONObject;

public class HbPartnerSteps {

  @Autowired private CommonFunctions commonFunctions;
  @Autowired private DatabaseUtils databaseUtils;
  @Autowired private CommonSteps commonSteps;
  @Autowired private HbPartnerRequests hbPartnerRequests;

  @When("^the user gets hb partner with pid \"(.*?)\"$")
  public void the_user_gets_hb_partner_with_pid(String pid) throws Throwable {
    String hbPartnerPid = String.valueOf(pid);
    commonSteps.requestMap = new RequestParams().setHbPartnerPid(hbPartnerPid).getRequestParams();
    commonSteps.request =
        hbPartnerRequests.getGetOneHbPartnerRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets all hb partners")
  public void the_user_gets_all_hb_partners() throws Throwable {
    commonSteps.request =
        hbPartnerRequests.getGetAllHbPartnersRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user creates hb partner from the json file \"(.+?)\"$")
  public void the_user_creates_hb_partner_from_the_json_file(String filename) throws Throwable {
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request = hbPartnerRequests.getCreateHbPartnerRequest().setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user updates hb partner with pid \"(.*?)\" from the json file \"(.+?)\"$")
  public void the_user_updates_hb_partner_from_the_json_file(String pid, String filename)
      throws Throwable {
    String hbPartnerPid = String.valueOf(pid);
    commonSteps.requestMap = new RequestParams().setHbPartnerPid(hbPartnerPid).getRequestParams();
    JSONObject payload = JsonHandler.getJsonObjectFromFile(filename);
    commonSteps.request =
        hbPartnerRequests
            .getUpdateHbPartnerRequest()
            .setRequestParams(commonSteps.requestMap)
            .setRequestPayload(payload);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user deletes hb partner with pid \"(.*?)\"$")
  public void the_user_deletes_hb_partner_with_pid(String pid) throws Throwable {
    String hbPartnerPid = String.valueOf(pid);
    commonSteps.requestMap = new RequestParams().setHbPartnerPid(hbPartnerPid).getRequestParams();
    commonSteps.request =
        hbPartnerRequests.getDeactivateHbPArtnerRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user finds hb partners for publisher with detail \"(.*?)\"")
  public void the_user_finds_hb_partners_for_publisher(boolean detail) throws Throwable {
    String detailFlag = String.valueOf(detail);
    commonSteps.requestMap = new RequestParams().setDetail(detailFlag).getRequestParams();
    commonSteps.request =
        hbPartnerRequests.getGetHbPartnersRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets hb partners with publisher pid \"(.*?)\" and detail \"(.*?)\"")
  public void hb_partners_with_publisher_pid_and_detail_flag(String pid, String detail)
      throws Throwable {
    String publisherPid = String.valueOf(pid);
    String detailFlag = String.valueOf(detail);
    commonSteps.requestMap =
        new RequestParams().setPublisherPid(publisherPid).setDetail(detailFlag).getRequestParams();
    commonSteps.request =
        hbPartnerRequests
            .getGetHbPartnersForPublisherRequest()
            .setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user gets hb partners with site pid \"(.*?)\" and detail \"(.*?)\"")
  public void hb_partners_with_site_pid_and_detail_flag(String pid, String detail)
      throws Throwable {
    String sitePid = String.valueOf(pid);
    String detailFlag = String.valueOf(detail);
    commonSteps.requestMap =
        new RequestParams().setSitePid(sitePid).setDetail(detailFlag).getRequestParams();
    commonSteps.request =
        hbPartnerRequests.getGetHbPartnersForSiteRequest().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.request.IsoLanguageRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class IsoLanguageSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private IsoLanguageRequests isoLanguageRequests;
  @Autowired private DatabaseUtils databaseUtils;

  @When("^the user searches for all languages$")
  public void the_user_searches_for_all_languages() throws Throwable {
    commonSteps.request = isoLanguageRequests.getGetAllIsoLanguagesRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for language with params \"(.*?)\" and search term \"(.*?)\"$")
  public void search_for_language(String qf, String qt) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(qt).setqf(qf).getRequestParams();

    commonSteps.request =
        isoLanguageRequests.searchIsoLanguage().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

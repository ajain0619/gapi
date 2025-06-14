package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.ContentRating;
import com.nexage.geneva.request.ContentRatingRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentRatingSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private ContentRatingRequests contentRatingRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<Integer, ContentRating> contentRatingMap;

  @When("^the user searches for all ratings$")
  public void the_user_searches_for_all_ratings() throws Throwable {
    commonSteps.request = contentRatingRequests.getGetAllContentRatingRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for rating with params \"(.*?)\" and search term \"(.*?)\"$")
  public void search_for_rating(String qf, String qt) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(qt).setqf(qf).getRequestParams();

    commonSteps.request =
        contentRatingRequests.searchContentRating().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

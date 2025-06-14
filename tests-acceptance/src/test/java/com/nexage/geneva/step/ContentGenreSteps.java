package com.nexage.geneva.step;

import com.nexage.geneva.database.DatabaseUtils;
import com.nexage.geneva.model.crud.ContentGenre;
import com.nexage.geneva.request.ContentGenreRequests;
import com.nexage.geneva.request.RequestParams;
import com.nexage.geneva.util.CommonFunctions;
import io.cucumber.java.en.When;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentGenreSteps {
  @Autowired private CommonSteps commonSteps;
  @Autowired private CommonFunctions commonFunctions;
  @Autowired private ContentGenreRequests contentGenreRequests;
  @Autowired private DatabaseUtils databaseUtils;

  private Map<Integer, ContentGenre> contentGenreMap;

  @When("^the user searches for all genres$")
  public void the_user_searches_for_all_genres() throws Throwable {
    commonSteps.request = contentGenreRequests.getGetAllContentGenreRequest();
    commonFunctions.executeRequest(commonSteps);
  }

  @When("^the user searches for genre with params \"(.*?)\" and search term \"(.*?)\"$")
  public void search_for_genre(String qf, String qt) throws Throwable {
    commonSteps.requestMap = new RequestParams().setqt(qt).setqf(qf).getRequestParams();

    commonSteps.request =
        contentGenreRequests.searchContentGenre().setRequestParams(commonSteps.requestMap);
    commonFunctions.executeRequest(commonSteps);
  }
}

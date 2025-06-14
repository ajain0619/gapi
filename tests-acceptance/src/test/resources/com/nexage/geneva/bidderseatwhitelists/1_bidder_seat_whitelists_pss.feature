Feature: Seller - Sites - Position - Exchange Tag: create, get, update, delete bidder seat whitelists as pss user

  Background: log in as pss seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"

  Scenario: Create bidder seat whitelists by adding an rtbProfileBidders section
    When the user specifies the date range from "2016-03-03T00:00:00-04:00" to "2016-03-08T00:00:00-04:00"
    And the user selects the site "AS8A" and the position "custom1a" and the tag "Nexage Exchange - Nexage-Adserver - test8z"
    Then the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateExchangeTag_To_Add_Bidder_Seat_WhitelistPR9690_payload.json"
    And request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdateExchangeTag_To_Add_Bidder_Seat_WhitelistPR9690_ER.json"

  Scenario: Get existing bidder seat whitelists
    When the user specifies the date range from "2016-03-03T00:00:00-04:00" to "2016-03-08T00:00:00-04:00"
    And the user selects the site "AS8A" and the position "custom1a" and the tag "Nexage Exchange - Nexage-Adserver - test8z"
    Then publisher tag is retrieved
    And request passed successfully
    And returned "bidder seat whitelist" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/PublisherTagsWithRtbProfileBidders_ER.json"

  Scenario: Edit bidder seat whitelists by adding/removing bidder seat whitelists in rtbProfileBidders
    When the user specifies the date range from "2016-03-03T00:00:00-04:00" to "2016-03-08T00:00:00-04:00"
    And the user selects the site "AS8A" and the position "custom1a" and the tag "Nexage Exchange - Nexage-Adserver - test8z"
    Then the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateExchangeTag_To_Edit_Bidder_Seat_WhitelistPR9690_payload.json"
    And request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdateExchangeTag_To_Edit_Bidder_Seat_WhitelistPR9690_ER.json"

  Scenario: Edit bidder seat whitelists by adding duplicate seat
    When the user specifies the date range from "2016-03-03T00:00:00-04:00" to "2016-03-08T00:00:00-04:00"
    And the user selects the site "AS8A" and the position "custom1a" and the tag "Nexage Exchange - Nexage-Adserver - test8z"
    Then the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateExchangeTag_To_Add_Duplicate_SeatPR9690_payload.json"
    And request passed successfully
    And returned "bidder seat whitelist" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdatedExchangeTag_With_Duplicate_Seat_RemovedPR9690_ER.json"

  Scenario: Add bidder seat whitelists with unknown bidder pid
    When the user specifies the date range from "2016-03-03T00:00:00-04:00" to "2016-03-08T00:00:00-04:00"
    And the user selects the site "AS8A" and the position "custom1a" and the tag "Nexage Exchange - Nexage-Adserver - test8z"
    Then the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateExchangeTag_To_Add_Bidder_Seat_Whitelist_With_Unknown_Bidder_PidPR9690_payload.json"
    Then "tag update" failed with "404" response code

  Scenario: Edit bidder seat whitelists by removing entire rtbProfileBidders
    When the user specifies the date range from "2016-03-03T00:00:00-04:00" to "2016-03-08T00:00:00-04:00"
    And the user selects the site "AS8A" and the position "custom1a" and the tag "Nexage Exchange - Nexage-Adserver - test8z"
    Then the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateExchangeTag_To_Delete_RtbProfileBiddersPR9690_payload.json"
    And request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdateExchangeTag_To_Delete_RtbProfileBiddersPR9690_ER.json"

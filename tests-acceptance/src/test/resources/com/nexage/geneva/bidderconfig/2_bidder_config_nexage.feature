Feature: search, update bidder config for buyer as nexage admin; search publishers and publisher sites as nexage admin

  Background: log in as nexage admin and selects the buyer
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Buyer" company "asdf"

  @restoreCrudCoreDatabaseBefore

  Scenario: get all bidder configs
    When the user gets all bidder configs
    Then request passed successfully
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetAllBidderConfigs_ER.json"

  Scenario: get bidder config
    When the user gets first bidder config
    Then request passed successfully
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetFirstBidderConfig_ER.json"

  Scenario: create bidder config
    When the user creates bidder config from the json file "jsons/genevacrud/bidderconfig/payload/create/CreateBidderConfig_payload.json"
    Then request passed successfully with code "201"
    And returned "create bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/create/CreateBidderConfig_ER.json"

  Scenario: get created bidder config
    When the user gets bidder config with id "0001"
    Then request passed successfully
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/create/CreateBidderConfig_ER.json"

  Scenario Outline: update bidder config with different parameters
    When the user updates first bidder config from the json file "jsons/genevacrud/bidderconfig/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload                          | file_ER                          |
      | BidderConfiguration_payload           | BidderConfiguration_ER           |
      | AppBundleFilterListUpdate_payload     | AppBundleFilterListUpdate_ER     |
      | TrafficFilter_payload                 | TrafficFilter_ER                 |
      | CountriesFilter_payload               | CountriesFilter_ER               |
      | PublishersFilter_payload              | PublishersFilter_ER              |
      | SitesFilter_payload                   | SitesFilter_ER                   |
      | AdSizesFilter_payload                 | AdSizesFilter_ER                 |
      | NexageOnlyBidderConfiguration_payload | NexageOnlyBidderConfiguration_ER |

  Scenario: delete bidder config
    When the user deletes first bidder config
    Then request passed without errors
    And deleted bidder config cannot be searched out

  Scenario: get list of ad sizes
    When the user gets list of ad sizes
    Then request passed successfully
    And returned "ad sizes" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetAdSizes_ER.json"

Feature: search, update bidder config for buyer as buyer admin; search publishers and publisher sites as buyer admin

  Background: log in as nexage admin and select the buyer
    Given the user "asdfadmin" has logged in with role "AdminNexage"

  @restoreCrudCoreDatabaseBefore

  Scenario: get all bidder configs
    When the user gets all bidder configs
    Then request passed successfully
    And returned "Bidder Config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetAllBidderConfigs_ER.json"

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

  Scenario: create bidder config with global id
    When the user creates bidder config from the json file "jsons/genevacrud/bidderconfig/payload/create/CreateBidderConfigWithUUMPGlobalId_payload.json"
    Then request passed successfully with code "201"
    And returned "create bidder config with global id" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/create/CreateBidderConfigWithUUMPGlobalId_ER.json"

  Scenario: get bidder config with global id
    When the user gets bidder config with id "0002"
    Then request passed successfully
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/create/CreateBidderConfigWithUUMPGlobalId_ER.json"

  Scenario: create bidder config using null payload
    When the user creates bidder config using null payload
    Then request passed successfully with code "201"
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/create/CreateBidderConfigNullPayload_ER.json"

  Scenario Outline: update bidder config with different parameters
    When the user updates first bidder config from the json file "jsons/genevacrud/bidderconfig/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/update/<file_ER>.json"

    Examples:
      | file_payload                      | file_ER                      |
      | BidderConfiguration_payload       | BidderConfiguration_ER       |
      | AppBundleFilterListUpdate_payload | AppBundleFilterListUpdate_ER |
      | TrafficFilter_payload             | TrafficFilter_ER             |
      | CountriesFilter_payload           | CountriesFilter_ER           |
      | PublishersFilter_payload          | PublishersFilter_ER          |
      | SitesFilter_payload               | SitesFilter_ER               |
      | AdSizesFilter_payload             | AdSizesFilter_ER             |
      | UumpGlobalId_payload              | UumpGlobalId_ER              |
      | RTBv2_4_payload                   | RTBv2_4_ER                   |

  Scenario: get bidder config with bidderConfigDenyAllowFilterLists
    When the user gets first bidder config
    Then request passed successfully
    And returned "bidder config" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetBidderConfigWithDenyAllowFilterList_ER.json"

  Scenario: update bidder config with non-existing device_type foreign key will fail
    When the user updates first bidder config from the json file "jsons/genevacrud/bidderconfig/payload/update/UpdateBidderConfigNonExistentDeviceType_payload.json"
    Then "bidder config update" failed with "400" response code

  Scenario: create bidder config with invalid content encoding
    When the user creates bidder config from the json file "jsons/genevacrud/bidderconfig/payload/create/BidderConfig_invalid_allowedContentEncoding_payload.json"
    Then "create bidder config" failed with "400" response code and error message "Invalid Content Encoding Types"

  Scenario: update bidder config with invalid content encoding
    When the user updates first bidder config from the json file "jsons/genevacrud/bidderconfig/payload/update/UpdateBidderConfigInvalidContentEncoding_payload.json"
    Then "bidder config update" failed with "400" response code and error message "Invalid Content Encoding Types"

  Scenario: delete bidder config
    When the user deletes first bidder config
    Then request passed without errors
    And deleted bidder config cannot be searched out

  Scenario: get list of ad sizes
    When the user gets list of ad sizes
    Then request passed successfully
    And returned "ad sizes" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetAdSizes_ER.json"

  Scenario: getAll bidder config summaries
    When the user gets bidder config summaries with companyPid "10230" and qf "name" and qt "usa" and page "0" and size "5"
    Then request passed successfully
    And returned "bidder config summary" data matches the following json file "jsons/genevacrud/bidderconfig/expected_results/GetAllBidderConfigSummaries_ER.json"


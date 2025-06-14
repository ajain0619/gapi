Feature: create, update, search deals as nexage admin

  Background: log in
    Given the user "admin1c" has logged in with role "AdminNexage"

  @restoreCrudCoreDatabaseBefore

  Scenario: create desktop site, placement and rtb tag for deal
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateDesktopSiteAllFields_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteDesktopAllFields_ER.json"
    And set site "desktop_site1"
    When the PSS user creates position from the json file "jsons/genevacrud/position/pss/payload/create/CreateForDeal_payload.json"
    Then returned "position create" data matches the following json file "jsons/genevacrud/position/pss/expected_results/create/CreateForDeal_ER.json"
    When the user specifies the date range from "2018-02-13T00:00:00-04:00" to "2018-02-15T23:59:59-04:00"
    And the user selects the site "desktop_site1" and the position "ban1"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/TagForDeal_payload.json"
    Then request passed successfully with code "201"

  Scenario: Create rule without a publisher
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateRuleWithoutPublisher_payload.json"
    Then request passed successfully
    And returned "rule" data matches the following json file "jsons/genevacrud/sellingrule/expected_results/CreateRuleWithoutPublisher_ER.json"

  Scenario: Update rule attributes
    When the user finds rule pid for name "Rule without publisher"
    And the user updates one rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/UpdateRuleAttribute_payload.json"
    Then request passed successfully
    And returned "rule" data matches the following json file "jsons/genevacrud/sellingrule/expected_results/UpdateRuleAttribute_ER.json"

  Scenario: create deal with desktop site
    When the user create a deal from the json file "jsons/genevacrud/deal/payload/create/DealDesktopCreate_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/create/DealDesktopCreate_ER.json"

  Scenario: search deal with desktop site by id
    Given the user searches for all deals
    When the user searches for deal by deal id "13021645789"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/search/GetDealDesktopById_ER.json"

  Scenario: update deal with different set of fields
    Given the user searches for all deals
    When the user update a deal with Deal id "13021645789" from the json file "jsons/genevacrud/deal/payload/update/DealDesktopUpdate_payload.json"
    Then request passed successfully
    And returned "deal" data matches the following json file "jsons/genevacrud/deal/expected_results/update/DealDesktopUpdate_ER.json"

  Scenario Outline: create Rule with invalid Channel target values will fail
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/<filename>.json"
    Then "rule creation" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"

    Examples:
      | filename                                                                             |
      | CreateRuleWithoutPublisherWithInvalidContentChannelJson_payload                      |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentChannelLength_payload             |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentChannelMissingChannel_payload     |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentChannelWithComma_payload          |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentChannelMissingTrafficFlag_payload |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentChannelInvalidTrafficFlag_payload |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentChannelNotArray_payload           |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentChannelWithExtraParam_payload     |

  Scenario: Update rule attributes with invalid Content channel values
    When the user finds rule pid for name "Rule without publisher"
    And the user updates one rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/UpdateRuleAttributeWithInvalidContentChannel_payload.json"
    Then "rule updation" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"

  Scenario Outline:: Create rule without a publisher with invalid content Series values
    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/<file_name>.json"
    Then "rule creation" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    Examples:
      | file_name                                                                   |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentSeriesLength             |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentSeriesData               |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentSeriesMissingSeries      |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentSeriesWithComma          |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentSeriesMissingTrafficFlag |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentSeriesInvalidTrafficFlag |
      | CreateRuleWithoutPublisherWithInvalidTarget_ContentSeriesNotArray           |

  Scenario Outline: Update rule attributes with invalid content Series values
    When the user finds rule pid for name "Rule without publisher"
    And the user updates one rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/<file_name>.json"
    Then "rule updation" failed with "400" response code and error message "Bad Request. Check your request parameters (json format, type..)"
    Examples:
      | file_name                                                                   |
      | UpdateRuleWithoutPublisherWithInvalidTarget_ContentSeriesMissingTrafficFlag |
      | UpdateRuleWithoutPublisherWithInvalidTarget_ContentSeriesData               |

#  Scenario Outline: Create rule with invalid rating values
#    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/<filename>.json"
#    Then "rule creation" failed with "400" response code and error message "Rule Target CONTENT_RATING has invalid value"
#
#    Examples:
#      | filename                                                              |
#      | CreateRuleWithoutPublisherWithEmptyRatingWithoutTrafficFlag_payload   |
#      | CreateRuleWithoutPublisherWithInvalidField_payload                    |
#      | CreateRuleWithoutPublisherWithInvalidRating_payload                   |
#      | CreateRuleWithoutPublisherWithInvalidTrafficFlag_payload              |
#      | CreateRuleWithoutPublisherWithMissingRating_payload                   |
#      | CreateRuleWithoutPublisherWithMissingTrafficFlag_payload              |
#
#  Scenario Outline: Create rule with invalid genre values
#    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/<filename>.json"
#    Then "rule creation" failed with "400" response code and error message "Rule Target CONTENT_GENRE has invalid value"
#
#    Examples:
#      | filename                                                       |
#      | CreateRuleWithoutPublisherWithEmptyGenre_payload               |
#      | CreateRuleWithoutPublisherGenreWithInvalidField_payload        |
#      | CreateRuleWithoutPublisherWithInvalidGenre_payload             |
#      | CreateRuleWithoutPublisherGenreWithInvalidTrafficFlag_payload  |
#      | CreateRuleWithoutPublisherWithMissingGenre_payload             |
#      | CreateRuleWithoutPublisherGenreWithMissingTrafficFlag_payload  |
#
#  Scenario: Create rule without a publisher With Valid Content targets
#    When the user creates selling rule without a publisher from the json file "jsons/genevacrud/sellingrule/payload/CreateRuleWithoutPublisherWithValidContentTargets_payload.json"
#    Then request passed successfully
#    And returned "rule" data matches the following json file "jsons/genevacrud/sellingrule/expected_results/CreateRuleWithoutPublisherWithValidContentTargets_ER.json"

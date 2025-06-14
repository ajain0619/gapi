Feature: Seller - Sites - Tags: get, create, update as pss user

  Scenario: get all tag summaries
    #GET http://geneva.sbx:8080/geneva/publisher/313/tagsummary?start=2015-09-01T00:00:00-04:00&stop=2015-09-23T00:00:00-04:00
    Given the user "cpi_seller" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the tag summaries are retrieved
    Then request passed successfully
    And returned "tag summaries" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/TagSummaries_ER.json"

  Scenario: get all tag summaries (with detailed tags information)
    #GET http://geneva.sbx:8080/geneva/publisher/105/tagsummary?start=2016-02-24T00:00:00-05:00&stop=2016-03-02T00:00:00-05:00
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2016-02-24T00:00:00-05:00" to "2016-03-02T00:00:00-05:00"
    When the tag summaries are retrieved
    Then request passed successfully
    And returned "tag summaries" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/TagSummariesWithTags_ER.json"

  Scenario: get publisher tags
    #GET http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    Then publisher tags are retrieved
    And request passed successfully
    And returned "publisher tags" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/PublisherTags_ER.json"

  Scenario: get a publisher tag
    #GET http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/7296
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "Nexage Exchange - test8x"
    Then publisher tag is retrieved
    And request passed successfully
    And returned "publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/PublisherTag_ER.json"

  Scenario: get a publisher tag with autoExpand field
    #GET http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/7334
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "MediationAdsourceWithTArgeting"
    Then publisher tag is retrieved
    And request passed successfully
    And returned "publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/PublisherTag_autoExpand_GET.json"

  # scenario below fails due to environment issue
  # currently it returns 400 error instead of 404
  @unstable
  Scenario: getting a publisher tag that doesn`t exist will fail
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the non-existing tag "0000000000000"
    And the user tries to search the publisher tag
    Then "publisher tag search" failed with "404" response code

  Scenario Outline: create a publisher tag with different set of fields
    #POST http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<file_ER>.json"

    Examples:
      | type         | file_payload                                             | file_ER                                             |
      | exchange     | NewMillennialMediaExchangeAdSourceRequiredFields_payload | NewMillennialMediaExchangeAdSourceRequiredFields_ER |
      | exchange     | NewMillennialMediaExchangeAdSourceAllFields_payload      | NewMillennialMediaExchangeAdSourceAllFields_ER      |
      | non-exchange | NewMediationAdSourceRequiredFields_payload               | NewMediationAdSourceRequiredFields_ER               |
      | non-exchange | NewMediationAdSourceAllFields_payload                    | NewMediationAdSourceAllFields_ER                    |
      | exchange     | ExchangeTagWithAllTargeting_payload                      | ExchangeTagWithAllTargeting_ER                      |
      | exchange     | ExchangeTagWithWifiTargeting_payload                     | ExchangeTagWithWifiTargeting_ER                     |
      | exchange     | ExchangeTagWithNoTargeting_payload                       | ExchangeTagWithNoTargeting_ER                       |
      | exchange     | ExchangeTagWithUnifiedSdkTargetingFields_payload         | ExchangeTagWithUnifiedSdkTargetingFields_ER         |
      | non-exchange | MediationAdSourceWithAllTargeting_payload                | MediationAdSourceWithAllTargeting_ER                |
      | non-exchange | MediationAdSourceWithWifiTargeting_payload               | MediationAdSourceWithWifiTargeting_ER               |
      | non-exchange | MediationAdSourceWithNoTargeting_payload                 | MediationAdSourceWithNoTargeting_ER                 |
      | non-exchange | MediationAdSourceAutoExpand_payload                      | MediationAdSourceAutoExpand_ER                      |
      | non-exchange | MediationAdSourceWithUnifiedSdkTargetingFields_payload   | MediationAdSourceWithUnifiedSdkTargetingFields_ER   |

  Scenario: create a publisher client tag SDK1 withOUT smart yield ruleType
    Given the user "user" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "S12E" and the position "footer"
    And the user creates a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/MediationAdSourceWithAllTargeting_SDK1_NoSY_payload.json"
    Then request passed successfully with code "201"
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/MediationAdSourceWithAllTargeting_SDK1_NoSY_ER.json"

  Scenario: create a publisher non client tag (use_wrapped_sdk=0) with smart yield ruleType
    #returns success, but UI does not allow this scenario
    #POST http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/MediationAdSourceWithAllTargeting_SmartYield_payload.json"
    Then "tag creation" failed with "400" response code

  @unstable
  Scenario: create a publisher client tag SDK1 with smart yield ruleType
  #returns success, but UI does not allow this scenario
    Given the user "user" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "S12E" and the position "footer"
    And the user creates a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/MediationAdSourceWithAllTargeting_SmartYieldSDK1_payload.json"
    Then "tag creation" failed with "400" response code

  Scenario: Get/Edit a publisher client tag SDK1 with smart yield ruleType
    #http://geneva-crud.sbx:8080/geneva/pss/248/site/10000198/position/100317/tag/5566580?
    Given the user "user" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "SY_HB_EnabledSite" and the position "sy_enabled_position" and the tag "SY_Exchange_1 - SY_Exchange_1 - SY_enabled_Client_tag"
    Then publisher tag is retrieved
    And request passed successfully
    When the user updates the publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateSYClientTag_Payload.json"
    And request passed successfully
    Then returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/UpdateSYClientTag_ER.json"

  # returns 500 instead of 400
  @unstable
  Scenario: create invalid publisher tag will fail
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/CreateInvalidTag_payload.json"
    Then "tag creation" failed with "400" response code

  Scenario: update a publisher tag using invalid tag will fail
    #PUT http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/3381
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "Nexage-Adserver - test8c"
    And the user updates the publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateInvalidTag_payload.json"
    Then "tag update" failed with "400" response code

  Scenario Outline: update a publisher tag with different set of fields
    #PUT http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/<tag>
    #<tag> = [3381, 3394, 7296, 7297]
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/<file_ER>.json"

    Examples:
      | type         | tagName                        | file_payload                                  | file_ER                                  |
      | non-exchange | Nexage-Adserver - test8c       | UpdateMediationAdSourceRequiredFields_payload | UpdateMediationAdSourceRequiredFields_ER |
      | non-exchange | Nexage-Adserver - test8d       | UpdateMediationAdSourceAllFields_payload      | UpdateMediationAdSourceAllFields_ER      |
      | exchange     | Nexage Exchange - test8x       | UpdateMillennialMediaRequiredFields_payload   | UpdateMillennialMediaRequiredFields_ER   |
      | exchange     | Nexage Exchange - test8y       | UpdateMillennialMediaAllFields_payload        | UpdateMillennialMediaAllFields_ER        |
      | exchange     | ExchangeTagWithTargeting       | UpdateMillennialMediaWithTargeting_payload    | UpdateMillennialMediaWithTargeting_ER    |
      | non-exchange | MediationAdsourceWithTArgeting | UpdateMediationAdSourceWithTargeting_payload  | UpdateMediationAdSourceWithTargeting_ER  |

  @unstable
  Scenario: update a publisher tag SDK0 with SmartYield ruleType
  #return success, but UI does not allow this scenario
    #PUT http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/<tag>
    #<tag> = [3381, 3394, 7296, 7297]
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "MediationAdsourceWithTArgeting"
    And the user updates the publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateMediationAdSourceWithTargeting_SmartYield_payload.json"
    #Then "tag update" failed with "400" response code

  @unstable
  Scenario: update a publisher client tag SDK1 - add SmartYield ruleType
  #return success, but UI does not allow this scenario
    Given the user "user" has logged in with role "UserSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "S12C" and the position "gadmob" and the tag "AdMob - Direct SDK Only - T12C-1"
    And the user updates the publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateMediationAdSourceWithTargeting_SmartYieldSDK1_payload.json"
    #Then "tag update" failed with "400" response code

  Scenario Outline: update a publisher tag and check ownership
    #PUT http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/<tag>
    #<tag> = [3381, 3394, 7296, 7297, 3381, 7296]
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<filename>_payload.json"
    Then request passed successfully
    And returned "owner" field is "<owner>"

    Examples:
      | type         | tagName                  | filename                                            | owner     |
      | non-exchange | Nexage-Adserver - test8c | UpdateMediationAdSourceRequiredFields_withOwnership | Publisher |
      | non-exchange | Nexage-Adserver - test8d | UpdateMediationAdSourceAllFields_withOwnership      | Publisher |
      | exchange     | Nexage Exchange - test8x | UpdateMillennialMediaRequiredFields_withOwnership   | Nexage    |
      | exchange     | Nexage Exchange - test8y | UpdateMillennialMediaAllFields_withOwnership        | Nexage    |
      | non-exchange | Nexage-Adserver - test8c | UpdateMediationAdSourceRequiredFieldsOwnerIgnored   | Publisher |
      | exchange     | Nexage Exchange - test8x | UpdateMillennialMediaRequiredFieldsOwnerIgnored     | Nexage    |

  Scenario Outline: update a publisher tag, only the alter_reserve field
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<file_payload>.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/<file_ER>.json"
    And alter_reserve field in DB has correct value for tag "<tagName>"

    Examples:
      | type     | tagName                        | file_payload                             | file_ER                             |
      | exchange | Nexage Exchange-test8x-updated | UpdateTagRTBProfile_AlterReserve_payload | UpdateTagRTBProfile_AlterReserve_ER |

  Scenario: update a publisher tag with ecmpAuto value, which defaults back to zero
    #PUT http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/<tag>
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "Nexage-Adserver-test8c-updated"
    And the user updates the publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/update/UpdateMediationAdSourceRequiredFieldsEcmp_payload.json"
    Then request passed successfully
    And returned ecmp value should still be "0.0" and not "567.0" as sent

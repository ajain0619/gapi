Feature: Seller - Sites - Tags: get, create, update as pss user

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: Clone a publisher tag
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    #POST http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/<tag>?method=clone&targetSite=950&targetPosition=13599
    #<tag> = [3394, 7296, 3394]
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/<file_payload>.json"
    Then request passed successfully
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/<file_ER>.json"

    Examples:
      | type         | tagName                  | file_payload                                        | file_ER                                        |
      | non-exchange | Nexage-Adserver - test8d | NewMediationAdSourceAllFields_payload               | NewMediationAdSourceAllFields_ER               |
      | exchange     | Nexage Exchange - test8x | NewMillennialMediaExchangeAdSourceAllFields_payload | NewMillennialMediaExchangeAdSourceAllFields_ER |
      | non-exchange | Nexage-Adserver - test8d | NewMediationAdSourceAllFieldsWithNulls_payload      | NewMediationAdSourceAllFieldsWithNulls_ER      |

  Scenario: Clone a publisher tag SDK0 with Smart Yield ruleType
    #POST http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/<tag>?method=clone&targetSite=950&targetPosition=13599
    #<tag> = [3394, 7296, 3394]
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "Nexage-Adserver - test8d"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/NewMediationAdSourceAllFields_SmartYield_payload.json"
    Then "tag creation" failed with "400" response code

  Scenario: Clone a publisher tag SDK1 with Smart Yield ruleType
    Given the user "user" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "S12C" and the position "gadmob" and the tag "AdMob - Direct SDK Only - T12C-1"
    And the user selects the target site "456" and the target position "10206"
    And the user clones a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/NewMediationAdSourceAllFields_SmartYieldSDK1_payload.json"
    Then request passed successfully
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/NewMediationAdSourceAllFields_SmartYieldSDK1_ER.json"

  Scenario: Clone a publisher tag with duplicate name in destination position should fail
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "Nexage-Adserver - test8d"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/NewMediationAdSourceDuplicateName_payload.json"
    Then "tag creation" failed with "400" response code

  Scenario: Clone a publisher tag with a duplicate name BUT different adsource should work
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8A" and the position "position1" and the tag "Moolah Media - NewNonExchangeTagAllFields20151123161930049"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/NewMediationAdsourceDuplicateNameDifferentAdsource_payload.json"
    Then request passed successfully
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/NewMediationAdsourceDuplicateNameDifferentAdsource_ER.json"

  Scenario Outline: cloning publisher with invalid buyer will fail
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/<filename>_payload.json"
    Then "tag creation" failed with "401" response code

    Examples:
      | type         | tagName                  | filename                                  |
      | non-exchange | Nexage-Adserver - test8d | NewMediationAdSourceAllFieldsBuyerInvalid |
      | non-exchange | Nexage-Adserver - test8d | NewMediationAdSourceAllFieldsBuyerNull    |

  Scenario Outline: cloning invalid publisher site, position will fail
      #POST http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag/7296?method=clone&targetSite=950&targetPosition=xyz
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "Nexage Exchange - test8x"
    And the user selects the target site "<site>" and the target position "<position>"
    And the user clones a publisher "non-exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/NewMediationAdSourceAllFields_payload.json"
    Then "tag creation" failed with "400" response code

    Examples:
      | site | position |
      | 950  | xyz      |
      | xyz  | 13599    |
      | 950  | null     |
      | null | 13599    |

  Scenario: Create a publisher tag with tagController and clone it
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/create/ExchangeTagWithTagController_payload.json"
    Then the user selects the site "AS8B" and the position "position1" and the tag "ExchangeTagWithTagController"
    And the user selects the target site "950" and the target position "13599"
    And the user clones a publisher "exchange" tag from the json file "jsons/genevacrud/tag/pss/payload/clone/ExchangeTagWithTagController_payload.json"
    Then request passed successfully
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/clone/ExchangeTagWithTagController_ER.json"

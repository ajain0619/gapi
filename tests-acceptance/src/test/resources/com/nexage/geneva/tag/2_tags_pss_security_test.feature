@onemobilelogin
Feature: Seller - Sites - Tags: access/permission tests

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: All publisher/seller roles can GET tags
      #GET http://geneva.sbx:8080/geneva/pss/105/site/963/position/14730/tag
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user creates a duplicate user from the json file "jsons/genevacrud/tag/permission/create/<usr_file_payload>_payload.json"
    And the user logs out
    And the user "<username>" has logged in with role "<role>"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "Nexage Exchange - test8x"
    Then publisher tag is retrieved
    And request passed successfully
    And returned "publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/PublisherTag_ER.json"

    Examples:
      | usr_file_payload | username           | password | role          |
      | SellerUser       | 105_SELLER_USER    | 123      | UserSeller    |
      | SellerAdmin      | 105_SELLER_ADMIN   | 123      | AdminSeller   |
      | SellerManager    | 105_SELLER_MANAGER | 123      | ManagerSeller |

  @restoreCrudCoreDatabaseBefore
  Scenario Outline: Create Admin and Manager users and check that they can PUT tags
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user creates a duplicate user from the json file "jsons/genevacrud/tag/permission/create/<usr_file_payload>_payload.json"
    And the user logs out
    And the user "<username>" has logged in with role "<role>"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<tag_file_payload>.json"
    Then request passed successfully
    And returned "updated publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/update/<file_ER>.json"

    Examples:
      | usr_file_payload | username           | role          | password | type         | tagName                  | tag_file_payload                              | file_ER                                  |
      | SellerAdmin      | 105_SELLER_ADMIN   | AdminSeller   | 123      | non-exchange | Nexage-Adserver - test8c | UpdateMediationAdSourceRequiredFields_payload | UpdateMediationAdSourceRequiredFields_ER |
      | SellerAdmin      | 105_SELLER_ADMIN   | AdminSeller   | 123      | exchange     | Nexage Exchange - test8x | UpdateMillennialMediaRequiredFields_payload   | UpdateMillennialMediaRequiredFields_ER   |
      | SellerManager    | 105_SELLER_MANAGER | ManagerSeller | 123      | non-exchange | Nexage-Adserver - test8c | UpdateMediationAdSourceRequiredFields_payload | UpdateMediationAdSourceRequiredFields_ER |
      | SellerManager    | 105_SELLER_MANAGER | ManagerSeller | 123      | exchange     | Nexage Exchange - test8x | UpdateMillennialMediaRequiredFields_payload   | UpdateMillennialMediaRequiredFields_ER   |

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: Create a user with User role and check that he cannot PUT tags
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user creates a duplicate user from the json file "jsons/genevacrud/tag/permission/create/<usr_file_payload>_payload.json"
    And the user logs out
    And the user "<username>" has logged in with role "<role>"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1" and the tag "<tagName>"
    And the user updates the publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/update/<tag_file_payload>.json"
    Then "Tag Update" failed with "401" response code

    Examples:
      | usr_file_payload | username        | role       | password | type         | tagName                  | tag_file_payload                              | file_ER                                  |
      | SellerUser       | 105_SELLER_USER | UserSeller | 123      | non-exchange | Nexage-Adserver - test8c | UpdateMediationAdSourceRequiredFields_payload | UpdateMediationAdSourceRequiredFields_ER |
      | SellerUser       | 105_SELLER_USER | UserSeller | 123      | exchange     | Nexage Exchange - test8x | UpdateMillennialMediaRequiredFields_payload   | UpdateMillennialMediaRequiredFields_ER   |

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: Create Admin and Manager users and check that they can POST tags
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user creates a duplicate user from the json file "jsons/genevacrud/tag/permission/create/<usr_file_payload>_payload.json"
    And the user logs out
    And the user "<username>" has logged in with role "<role>"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<tag_file_payload>.json"
    Then request passed successfully with code "201"
    And returned "created publisher tag" data matches the following json file "jsons/genevacrud/tag/pss/expected_results/create/<file_ER>.json"

    Examples:
      | usr_file_payload | username           | role          | password | type         | tag_file_payload                                         | file_ER                                             |
      | SellerAdmin      | 105_SELLER_ADMIN   | AdminSeller   | 123      | exchange     | NewMillennialMediaExchangeAdSourceRequiredFields_payload | NewMillennialMediaExchangeAdSourceRequiredFields_ER |
      | SellerAdmin      | 105_SELLER_ADMIN   | AdminSeller   | 123      | non-exchange | NewMediationAdSourceRequiredFields_payload               | NewMediationAdSourceRequiredFields_ER               |
      | SellerManager    | 105_SELLER_MANAGER | ManagerSeller | 123      | exchange     | NewMillennialMediaExchangeAdSourceRequiredFields_payload | NewMillennialMediaExchangeAdSourceRequiredFields_ER |
      | SellerManager    | 105_SELLER_MANAGER | ManagerSeller | 123      | non-exchange | NewMediationAdSourceRequiredFields_payload               | NewMediationAdSourceRequiredFields_ER               |

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  Scenario Outline: Create a user with User role and check that he cannot POST tags
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the user creates a duplicate user from the json file "jsons/genevacrud/tag/permission/create/<usr_file_payload>_payload.json"
    And the user logs out
    And the user "<username>" has logged in with role "<role>"
    And the user specifies the date range from "2015-09-01T00:00:00-04:00" to "2015-09-23T00:00:00-04:00"
    When the user selects the site "AS8B" and the position "position1"
    And the user creates a publisher "<type>" tag from the json file "jsons/genevacrud/tag/pss/payload/create/<tag_file_payload>.json"
    Then "Tag Create" failed with "401" response code

    Examples:
      | usr_file_payload | username        | role       | password | type         | tag_file_payload                                         | file_ER                                             |
      | SellerUser       | 105_SELLER_USER | UserSeller | 123      | exchange     | NewMillennialMediaExchangeAdSourceRequiredFields_payload | NewMillennialMediaExchangeAdSourceRequiredFields_ER |
      | SellerUser       | 105_SELLER_USER | UserSeller | 123      | non-exchange | NewMediationAdSourceRequiredFields_payload               | NewMediationAdSourceRequiredFields_ER               |

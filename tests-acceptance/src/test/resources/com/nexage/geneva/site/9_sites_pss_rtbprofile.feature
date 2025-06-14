Feature: pss users should not be able to add/change/remove an rtb profile to a site or see its site's rtb profile

  Background:
    Given the user updates a company "CRUDPositionTest" wtih rtbProfile belongs to the tag "Nexage Exchange - medRectPosition1_exchange"
    Given the user updates a company "CRUDPositionTest" wtih rtbProfile belongs to the tag "Nexage Exchange - PositionArchiving_TestTag10"
    Given the user updates a company "CRUDPositionTest" wtih rtbProfile belongs to the tag "Nexage Exchange - interstitialPosition1_excahnge"

  Scenario Outline: create new site with new rtbprofile
    Given the user "<user>" has logged in with role "<role>"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "201"
    Then returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/<file_ER>.json"

    Examples:
      | user                | role          | file_payload                                               | file_ER                                               |
      | crudPositionAdmin   | AdminSeller   | CreatePssSiteAddNewDefaultRtbProfilePssRoleAdmin_payload   | CreatePssSiteAddNewDefaultRtbProfilePssRoleAdmin_ER   |
      | crudPositionManager | ManagerSeller | CreatePssSiteAddNewDefaultRtbProfilePssRoleManager_payload | CreatePssSiteAddNewDefaultRtbProfilePssRoleManager_ER |

  Scenario Outline: create new site add existing rtbprofile
    Given the user "<user>" has logged in with role "<role>"
    When the PSS user creates a site with detail from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "201"
    Then returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/<file_ER>.json"

    Examples:
      | user                | role          | file_payload                                                    | file_ER                                                    |
      | crudPositionAdmin   | AdminSeller   | CreatePssSiteAddExistingDefaultRtbProfilePssRoleAdmin_payload   | CreatePssSiteAddExistingDefaultRtbProfilePssRoleAdmin_ER   |
      | crudPositionManager | ManagerSeller | CreatePssSiteAddExistingDefaultRtbProfilePssRoleManager_payload | CreatePssSiteAddExistingDefaultRtbProfilePssRoleManager_ER |

  Scenario Outline: Update site by adding a new rtb profile
    Given the user "<user>" has logged in with role "<role>"
    And the PSS user selects the site "CRUDPosition_Site2"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    Then returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/<file_ER>.json"

    Examples:
      | user                | role          | file_payload                                               | file_ER                                               |
      | crudPositionAdmin   | AdminSeller   | UpdatePssSiteAddNewDefaultRtbProfilePssRoleAdmin_payload   | UpdatePssSiteAddNewDefaultRtbProfilePssRoleAdmin_ER   |
      | crudPositionManager | ManagerSeller | UpdatePssSiteAddNewDefaultRtbProfilePssRoleManager_payload | UpdatePssSiteAddNewDefaultRtbProfilePssRoleManager_ER |

  Scenario Outline: Update site adding an existing rtb profile
    Given the user "<user>" has logged in with role "<role>"
    And the PSS user selects the site "CRUDPosition_Site2"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    Then returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/<file_ER>.json"

    Examples:
      | user                | role          | file_payload                                                    | file_ER                                                    |
      | crudPositionAdmin   | AdminSeller   | UpdatePssSiteAddExistingDefaultRtbProfilePssRoleAdmin_payload   | UpdatePssSiteAddExistingDefaultRtbProfilePssRoleAdmin_ER   |
      | crudPositionManager | ManagerSeller | UpdatePssSiteAddExistingDefaultRtbProfilePssRoleManager_payload | UpdatePssSiteAddExistingDefaultRtbProfilePssRoleManager_ER |

  Scenario Outline: Try to change the rtbProfile field values of the site's assigned rtb profile
    Given the user updates a site "CRUDPosition_Site3" by adding a default rtbProfile from the tag "DealTagTest-PSS-TargetingAdsizeOverrides2-Banner"
    Given the user "<user>" has logged in with role "<role>"
    And the PSS user selects the site "CRUDPosition_Site3"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    Then returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/<file_ER>.json"

    Examples:
      | user                | role          | file_payload                                                          | file_ER                                                          |
      | crudPositionAdmin   | AdminSeller   | UpdatePssSiteChangeDefaultRtbProfileFieldValuesPssRoleAdmin_payload   | UpdatePssSiteChangeDefaultRtbProfileFieldValuesPssRoleAdmin_ER   |
      | crudPositionManager | ManagerSeller | UpdatePssSiteChangeDefaultRtbProfileFieldValuesPssRoleManager_payload | UpdatePssSiteChangeDefaultRtbProfileFieldValuesPssRoleManager_ER |


  Scenario Outline: Detach a site's rtb profile by removing it from payload
    Given the user updates a company "CRUDPositionTest" wtih rtbProfile belongs to the tag "DealTagTest-PSS-TargetingAdsizeOverrides2-Banner"
    Given the user updates a site "CRUDPosition_Site3" by adding a default rtbProfile from the tag "DealTagTest-PSS-TargetingAdsizeOverrides2-Banner"
    Given the user "<user>" has logged in with role "<role>"
    And the PSS user selects the site "CRUDPosition_Site3"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/<file_payload>.json"
    And request passed successfully with code "200"
    Then returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/<file_ER>.json"

    Examples:
      | user                | role          | file_payload                                                         | file_ER                                                         |
      | crudPositionAdmin   | AdminSeller   | UpdatePssSiteDetachDefaultRtbProfileByRemovingPssRoleAdmin_payload   | UpdatePssSiteDetachDefaultRtbProfileByRemovingPssRoleAdmin_ER   |
      | crudPositionManager | ManagerSeller | UpdatePssSiteDetachDefaultRtbProfileByRemovingPssRoleManager_payload | UpdatePssSiteDetachDefaultRtbProfileByRemovingPssRoleManager_ER |

  Scenario Outline: Get site with default rtbprofile
    Given the user updates a company "CRUDPositionTest" wtih rtbProfile belongs to the tag "DealTagTest-PSS-TargetingAdsizeOverrides2-Banner"
    Given the user updates a site "CRUDPosition_Site3" by adding a default rtbProfile from the tag "DealTagTest-PSS-TargetingAdsizeOverrides2-Banner"
    Given the user "<user>" has logged in with role "<role>"
    When the PSS user selects the site "CRUDPosition_Site3"
    And request passed successfully
    Then returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/GetPssSiteWithDefaultRtbProfilePssRole_ER.json"

    Examples:
      | user                | role          |
      | crudPositionAdmin   | AdminSeller   |
      | crudPositionManager | ManagerSeller |
      | crudPositionUser    | UserSeller    |

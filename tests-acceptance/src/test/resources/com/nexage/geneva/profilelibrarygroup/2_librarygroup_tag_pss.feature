Feature: Rtbprofile library group operations

  Background: log in as Seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"

  @restoreCrudCoreDatabaseBefore

  Scenario: get library group tag
    When the user gets a group pid "1" in seller "105"
    Then request passed successfully
    And returned "publisher hierarchy" data matches the following json file "jsons/genevacrud/profilelibrarygroup/expected_results/LibraryGroupTag2_Pss.json"

  Scenario: create more tag library group connections
    When the user creates RTB Library Group Connections for seller "105" from the json file "jsons/genevacrud/profilelibrarygroup/payload/TagLibraryGroup2_Nexage.json"
    Then request passed successfully
    Then there are now "3" rtb_profile_library_association records with library pid "3"
    Then there are now "3" rtb_profile_library_association records with library pid "4"

  Scenario: create tag library group connections for other seller company
    When the user creates RTB Library Group Connections for seller "10201" from the json file "jsons/genevacrud/profilelibrarygroup/payload/TagLibraryGroup1_Nexage.json"
    Then "create tag library group" failed with "401" response code

  Scenario: create more tag library group connections as a publisher user
    Given the user "crudPositionUser" has logged in with role "UserSeller"
    When the user creates RTB Library Group Connections for seller "10201" from the json file "jsons/genevacrud/profilelibrarygroup/payload/TagLibraryGroup1_Nexage.json"
    Then "create tag library group" failed with "401" response code

  Scenario: remove tag library group connections
    When the user creates RTB Library Group Connections for seller "105" from the json file "jsons/genevacrud/profilelibrarygroup/payload/TagLibraryGroup3_Nexage.json"
    Then request passed successfully
    And library pid "3" now associated with exchange tag pids
      | 7301 |
    And library pid "4" now associated with exchange tag pids
      | 7301 |
    And there are now "5" rtb_profile_library_association audit records with library pid "3"
    And there are now "5" rtb_profile_library_association audit records with library pid "4"

  Scenario: Remove all tag library group connections
    When the user creates RTB Library Group Connections for seller "105" from the json file "jsons/genevacrud/profilelibrarygroup/payload/TagLibraryGroup4_Nexage.json"
    Then request passed successfully
    And library pid "3" now associated with exchange tag pids
      | |
    And library pid "4" now associated with exchange tag pids
      | |
    And there are now "6" rtb_profile_library_association audit records with library pid "3"
    And there are now "6" rtb_profile_library_association audit records with library pid "4"

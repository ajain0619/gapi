Feature: Seller - Sites - Positions: create, read or update a placement with an RTB profile

  @restoreCrudCoreDatabaseBefore
  Scenario: restore db
    Then nothing else to be done

  #Create a placement with default company RTB profile, with new RTB for placement and with already assigned to a placement RTB profile.
  #Also with empty RTB profile
  Scenario Outline:1 create a placement with an RTB as nexage admin
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update2_tag_transparency_enabled"
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    And the PSS user creates position from the json file "jsons/genevacrud/position/rtb/payload/<file_payload>.json"
    Then request passed successfully with code "201"
    And returned "create placement" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/<file_ER>.json"
    And position pid is retrieved for name "<posname>"
    And there are "<rtb_count>" rtb profiles for the company "transparency_enabled"

    Examples:
      | file_payload                           | file_ER                           | posname  | rtb_count |
      | CreatePlacementEmptyRTB_payload        | CreatePlacementEmptyRTB_ER        | tracker1 | 2         |
      | CreatePlacementCompanyRTB_payload      | CreatePlacementCompanyRTB_ER      | tracker2 | 2         |
      | CreatePlacementNewRTB_payload          | CreatePlacementNewRTB_ER          | tracker3 | 3         |
      | CreateOtherPlacementCompanyRTB_payload | CreateOtherPlacementCompanyRTB_ER | tracker4 | 3         |

  Scenario: Test external user for creating a placement with an RTB
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000202" for the company with pid "10225"
    When the PSS user creates position from the json file "jsons/genevacrud/position/rtb/payload/CreatePlacementCompanyRTB_payload.json"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario Outline:2 create a placement with an RTB profile from other company or non-existing RTB
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    And the PSS user creates position from the json file "jsons/genevacrud/position/rtb/payload/<file_payload>.json"
    Then "create position" failed with "404" response code and error message "Default RTB Profile record not found"
    And there are "3" rtb profiles for the company "transparency_enabled"

    Examples:
      | file_payload                           |
      | CreatePlacementOtherCompanyRTB_payload |
      | CreatePlacementNonExistingRTB_payload  |

  Scenario:3 create a placement with an RTB profile for company without RTB
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "debseller_for_mraid_tracking_test"
    And the defaultRTBProfilesFlag for company "debseller_for_mraid_tracking_test" is set to "true"
    And set site "debmraidsitemobileweb"
    And the PSS user creates position from the json file "jsons/genevacrud/position/rtb/payload/CreatePlacementNonRTBCompany_payload.json"
    Then "create placement" failed with "400" response code and error message "Default RTB Profile must be available at Publisher level"
    And there are "0" rtb profiles for the company "debseller_for_mraid_tracking_tes"

  Scenario Outline:4 read a placement with an RTB profile as nexage user or nexage admin
    Given the user "<userlogin>" has logged in with role "<role>"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And the user selects the site "aleksis_site"
    Given position with name "tracker3" is selected
    When the PSS user gets data for position with detail "true"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/<file_payload>.json"

    Examples:
      | userlogin      | role        | file_payload                  |
      | crudnexageuser | UserNexage  | GetPlacementRTBNexageUser_ER  |
      | admin1c        | AdminNexage | GetPlacementRTBNexageAdmin_ER |

  Scenario: Test external user for reading a placement with an RTB profile
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000202" for the company with pid "10225"
    And position by pid "100331" is retrieved from database
    When the PSS user gets data for selected position
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario:5 update a placement with an RTB as nexage user
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And the user selects the site "aleksis_site"
    Given position with name "tracker2" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementCompanyRTB_payload.json" with detail "true"
    Then "update placement" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario Outline:6 update a placement with an RTB by NULL RTB, then update it with RTB profile
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    Given position with name "tracker3" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/<file_payload>.json" with detail "true"
    Then request passed successfully
    And returned "update position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/<file_ER>.json"
    And there are "<count>" rtb profiles for the company "transparency_enabled"

    Examples:
      | file_payload                   | file_ER                   | count |
      | UpdatePlacementNullRTB_payload | UpdatePlacementNullRTB_ER | 3     |
      | UpdatePlacementNewRTB_payload  | UpdatePlacementNewRTB_ER  | 3     |

  Scenario: Test external user for updating a placement with an RTB by NULL RTB
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000202" for the company with pid "10225"
    And position by pid "100331" is retrieved from database
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementNullRTB_payload.json" with detail "false"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario:7 update a placement with company RTB with disabled defaultRTBProfilesEnabled
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "false"
    And set site "aleksis_site"
    Given position with name "tracker2" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementCompanyRTB_payload.json" with detail "true"
    Then "update placement" failed with "400" response code and error message "Default RTB Profiles feature not enabled for the publisher"

  #Update a placement with detail=true and then get it with detail=false or detail=true
  Scenario:8 update a placement with company RTB
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    Given position with name "tracker2" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementCompanyRTB_payload.json" with detail "true"
    Then request passed successfully
    And returned "update position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/UpdatePlacementCompanyRTB_ER.json"
    And there are "3" rtb profiles for the company "transparency_enabled"
    When the PSS user gets data for position with detail "true"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/GetPlacementDetailsTrue_ER.json"
    When the PSS user gets data for position with detail "false"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/GetPlacementDetailsFalse_ER.json"

  #In these scenarios validating, that impossible to update placement with non-existing RTB profile, RTB profile belonging to the other
  #company or updating placement with tag as well
  Scenario Outline:9 update a placement with non-existing RTB, belonging to the other company or updating along with tag
    Given the user updates a company "transparency_disabled" wtih rtbProfile belongs to the tag "Update1_tag_transparency_disabled"
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    Given position with name "tracker2" is selected
    When the PSS user updates selected position from the json file "jsons/genevacrud/position/rtb/payload/<file_payload>.json"
    Then "update placement" failed with "<expected_http_status>" response code and error message "<error_message>"

    Examples:
      | file_payload                           | expected_http_status | error_message                                                                                |
      | UpdatePlacementNonExistingRTB_payload  | 404                  | Default RTB Profile record not found                                                         |
      | UpdatePlacementOtherCompanyRTB_payload | 404                  | Default RTB Profile record not found                                                         |
      | UpdatePlacementRTBWithTag_payload      | 400                  | Modifying default mediation tag setup at Site and Position level of inventory is not allowed |

  Scenario: Test external user for updating a placement with non-existing RTB
    Given the user "role-api-user-1c" logs in via B2B with role "Api"
    And the user selects site pid "10000202" for the company with pid "10225"
    And position by pid "100331" is retrieved from database
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementNullRTB_payload.json" with detail "false"
    Then response failed with "401" response code, error message "You're not authorized to perform this operation." and without field errors.

  Scenario:10 get a placement with an RTB profile as an external admin
    Given the user "pssTransEnabledMgr" has logged in with role "ManagerSeller"
    And the user selects the site "aleksis_site"
    Given position with name "tracker3" is selected
    When the PSS user gets data for position with detail "true"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/GetPlacementRTBExternalUser_ER.json"

  #In this case, update all parameters of the placement (with detail=true), then update it without detail and make sure, that values are not set as default ones
  Scenario:11 update a placement with details and then without details
    Given the user updates a company "transparency_disabled" wtih rtbProfile belongs to the tag "Update1_tag_transparency_disabled"
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    Given position with name "tracker3" is selected
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementRTBAllFields_payload.json" with detail "true"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/UpdatePlacementRTBAllFields_ER.json"
    When the PSS user updates position with detail from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementWithoutDetails_payload.json" with detail "false"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/UpdatePlacementWithoutDetails_ER.json"
    When the PSS user gets data for position with detail "true"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/GetPlacementAfterSeveralUpdatesTrue_ER.json"
    When the PSS user gets data for position with detail "false"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/GetPlacementAfterSeveralUpdatesFalse_ER.json"

  Scenario:12 update a placement with an RTB profile as an external admin
    Given the user "pssTransEnabledAdmin" has logged in with role "AdminSeller"
    And the user selects the site "aleksis_site"
    Given position with name "tracker2" is selected
    When the PSS user updates selected position from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementCompany2RTB_payload.json"
    Then request passed successfully with code "200"
    Then returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/UpdatePlacementCompanyRTBExternalAdmin_ER.json"

  Scenario:13 create a placement with an RTB as nexage user
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update2_tag_transparency_enabled"
    Given the user "crudnexageuser" has logged in with role "UserNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    And the PSS user creates position from the json file "jsons/genevacrud/position/rtb/payload/CreatePlacementCompanyRTB_payload.json"
    Then "create placement" failed with "401" response code and error message "You're not authorized to perform this operation"

  Scenario:14 create a placement with an RTB as external admin
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update2_tag_transparency_enabled"
    Given the user "pssTransEnabledAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    And the PSS user creates position from the json file "jsons/genevacrud/position/rtb/payload/CreatePlacementNewRTB_payload.json"
    Then request passed successfully with code "201"
    Then returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/CreatePlacementNewRTBExternalAdmin_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:15 create a placement with an RTB as nexage admin with limited response with disabled defaultRTBProfilesFlag
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update2_tag_transparency_enabled"
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "false"
    And set site "aleksis_site"
    And the user creates position on pss api with limited response from the json file "jsons/genevacrud/position/rtb/payload/CreatePlacementNewRTB_payload.json"
    Then "create placement" failed with "400" response code and error message "Default RTB Profiles feature not enabled for the publisher"

  Scenario:16 create a placement with an RTB as nexage admin with limited response
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update2_tag_transparency_enabled"
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    And set site "aleksis_site"
    And the user creates position on pss api with limited response from the json file "jsons/genevacrud/position/rtb/payload/CreatePlacementNewRTB_payload.json"
    Then request passed successfully with code "201"
    And returned "create placement" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/CreatePlacementRTBLimitedResponse_ER.json"
    And position pid is retrieved for name "tracker3"
    And there are "2" rtb profiles for the company "transparency_enabled"

  Scenario:17 read a placement with an RTB profile as nexage admin with limited response
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site"
    Given position with name "tracker3" is selected
    When the PSS user gets data for position with detail "false"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/CreatePlacementRTBLimitedResponse_ER.json"

  Scenario:18 update a placement without detail and get it without detail
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "transparency_enabled"
    And set site "aleksis_site"
    Given position with name "tracker3" is selected
    When the PSS user updates selected position from the json file "jsons/genevacrud/position/rtb/payload/UpdatePlacementWithoutParam_payload.json"
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/UpdatePlacementWithoutParam_ER.json"
    When the PSS user gets data for selected position
    Then request passed successfully
    And returned "position" data matches the following json file "jsons/genevacrud/position/rtb/expected_results/UpdatePlacementWithoutParam_ER.json"

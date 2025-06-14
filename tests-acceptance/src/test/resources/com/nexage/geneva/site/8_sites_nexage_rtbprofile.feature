Feature: add/change/remove/retrieve rtb profile for sites as a nexage admin/mgr

  Background:
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update2_tag_transparency_enabled"
    Given the user updates a company "transparency_disabled" wtih rtbProfile belongs to the tag "Update1_tag_transparency_disabled"
    Given the defaultRTBProfilesFlag for company "transparency_enabled" is set to "true"
    Given the defaultRTBProfilesFlag for company "transparency_disabled" is set to "true"
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"

  Scenario: create a site with new default rtbprofile as nexage admin
    And the user selects the "Seller" company "transparency_enabled"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAddNewDefaultRtbProfile_payload.json"
    And request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/CreatePssSiteAddNewDefaultRtbProfile_ER.json"
    And there are "2" rtb profiles for the company "transparency_enabled"

  Scenario: Create site by assigning existing rtb profile as a nexage mananger
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    And the user selects the "Seller" company "transparency_disabled"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAssignExistingRtbProfile_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/CreatePssSiteAssignExistingRtbProfile_ER.json"
    And there are "1" rtb profiles for the company "transparency_disabled"

  Scenario: Create site by assigning an rtb profile that is assigned to a different site
    And the user selects the "Seller" company "transparency_disabled"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAssignDefaultRtbProfileAssignedToOtherSite_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/CreatePssSiteAssignDefaultRtbProfileAssignedToOtherSite_ER.json"
    And there are "1" rtb profiles for the company "transparency_disabled"

  Scenario: Create site assigning an existing rtbprofile and changing its field values
    And the user selects the "Seller" company "transparency_disabled"
    Then the PSS user creates a site with detail from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAssignExistingRtbProfileWithModifiedFieldValues_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/CreatePssSiteAssignExistingRtbProfileWithModifiedFieldValues_ER.json"
    And there are "1" rtb profiles for the company "transparency_disabled"

  Scenario: Create site with new default rbprofile with detail=false - default rtbProfile in response should only contain 5 fields
    And the user selects the "Seller" company "transparency_enabled"
    Then the PSS user creates a site without detail from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAddDefaultRtbProfileNoDetail_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/CreatePssSiteAddDefaultRtbProfileNoDetail_ER.json"
    And there are "3" rtb profiles for the company "transparency_enabled"

  Scenario: Create site by assigning an rtb profile that is assigned to a different company should fail
    And the user selects the "Seller" company "transparency_enabled"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAssignDefaultRtbProfileFromOtherCompany_payload.json"
    And "create site by assigning rtb profile from other company" failed with "404" response code and error message "Default RTB Profile record not found"

  Scenario: Create site by assigning an rtb profile that has null value for company_pid should fail
    And the user selects the "Seller" company "transparency_enabled"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAssignDefaultRtbProfileWithNullCompanyPid_payload.json"
    And "create site by assigning rtb profile with null value for company pid" failed with "404" response code and error message "Default RTB Profile record not found"

  Scenario: Create site by assigning a default rtbprofile for a publisher with no default rtbprofile (no value for rtb_profile in seller_attributes table) should fail
    And the user selects the "Seller" company "SravaniTest"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteWithNewDefaultRtbProfileForCompanyWithNoDefaultRtbProfile_payload.json"
    And "create site by assigning a default rtb profile for a publisher with no default rtbprofile" failed with "400" response code and error message "Default RTB Profile must be available at Publisher level"

  Scenario: Update site by adding new rtb profile to a site
    And the user selects the "Seller" company "transparency_enabled"
    When the PSS user selects the site "aleksis_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddNewRtbProfile_payload.json"
    Then request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddNewRtbProfile_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteAddNewRtbProfile_ER.json"
    And there are "4" rtb profiles for the company "transparency_enabled"

  Scenario: Update site by adding a new rtb profile with detail=false - default rtbrofile in response should only have 5 fields
    And the user selects the "Seller" company "transparency_enabled"
    When the PSS user selects the site "aleksis_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddNewRtbProfileNoDetail_payload.json"
    Then request passed successfully
    And the PSS user updates site without detail and data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddNewRtbProfileNoDetail_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteAddNewRtbProfileNoDetail_ER.json"
    And there are "5" rtb profiles for the company "transparency_enabled"

  Scenario: Update site by adding existing rtb profile to a site
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    And the user selects the "Seller" company "transparency_disabled"
    And the PSS user selects the site "site_for_transparency_disabled"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddExistingRtbProfile_payload.json"
    Then request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddExistingRtbProfile_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteAddExistingRtbProfile_ER.json"
    And there are "1" rtb profiles for the company "transparency_disabled"

  Scenario Outline: Get site with an rtb profile assigned
    Given the user "<user>" has logged in with role "<role>"
    And the user selects the "Seller" company "transparency_enabled"
    When the PSS user selects the site "aleksis_site"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/<filename>.json"

    Examples:
      | user              | role          | filename                                   |
      | admin1c           | AdminNexage   | GetPssSiteRtbProfileShouldBeReturned_ER    |
      | crudnexagemanager | ManagerNexage | GetPssSiteRtbProfileShouldBeReturned_ER    |
      | crudnexageuser    | UserNexage    | GetPssSiteRtbProfileShouldNotBeReturned_ER |

  Scenario: Get site with an rtb profile with detail=true
    And the user selects the "Seller" company "transparency_enabled"
    When the PSS user select the site "aleksis_site" with detail "true"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/GetPssSiteRtbProfileWithDetail_ER.json"

  Scenario: Get site with an rtb profile with detail=false
    And the user selects the "Seller" company "transparency_enabled"
    When the PSS user select the site "aleksis_site" with detail "false"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/GetPssSiteRtbProfileShouldBeReturned_ER.json"

  Scenario: Update site by changing its default rtb profile
    And the user selects the "Seller" company "transparency_enabled"
    And the PSS user selects the site "aleksis_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteChangeRtbProfile_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteChangeRtbProfile_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteChangeRtbProfile_ER.json"
    And there are "5" rtb profiles for the company "transparency_enabled"

  Scenario: Update site by adding an rtb profile that is already assigned
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update2_tag_transparency_enabled"
    Given the user updates a company "transparency_enabled" wtih rtbProfile belongs to the tag "Update1_tag_transparency_enabled"
    And the user selects the "Seller" company "transparency_enabled"
    And the PSS user selects the site "aleksis_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAssignRtbProfileFromOtherSite_payload.json"
    Then request passed successfully
    Then the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAssignRtbProfileFromOtherSite_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteAssignRtbProfileFromOtherSite_ER.json"
    And there are "6" rtb profiles for the company "transparency_enabled"

  Scenario: Update site detaching its rtb profile by setting it to null
    And the user selects the "Seller" company "transparency_enabled"
    And the user selects the site "aleksis_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetachRtbProfileSettingToNull_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetachRtbProfileSettingToNull_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteDetachRtbProfileSettingToNull_ER.json"
    And there are "6" rtb profiles for the company "transparency_enabled"

  Scenario: Update site detaching its rtb profile by removing the rtbprofile field
    And the user selects the "Seller" company "transparency_disabled"
    And the user selects the site "site_for_transparency_disabled"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetachRtbProfileByRemoving_payload.json"
    Then request passed successfully
    When the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetachRtbProfileByRemoving_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteDetachRtbProfileByRemoving_ER.json"
    And there are "1" rtb profiles for the company "transparency_disabled"

  Scenario: Update site by assigning an rtb profile that is assigned to a different company should fail
    And the user selects the "Seller" company "transparency_disabled"
    And the PSS user selects the site "site_for_transparency_disabled"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAssignRtbProfileFromOtherCompany_payload.json"
    Then "update site by assigning rtb profile from other company" failed with "404" response code and error message "Default RTB Profile record not found"

  Scenario: create a site with new default rtbprofile but with disabled defaultRtbProfileEnabled
    And the user selects the "Seller" company "transparency_enabled"
    And updates a company defaultRtbProfilesEnabled to "false"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAddNewDefaultRtbProfileWithDisabledFlag_payload.json"
    Then "create site" failed with "400" response code and error message "Default RTB Profiles feature not enabled for the publisher"
    Then updates a company defaultRtbProfilesEnabled to "true"
    Then the PSS user creates a site from the json file "jsons/genevacrud/site/rtb/payload/CreatePssSiteAddNewDefaultRtbProfileWithDisabledFlag_payload.json"
    And request passed successfully with code "201"
    And there are "7" rtb profiles for the company "transparency_enabled"

  Scenario: Update site by adding new rtb profile to a site but with disabled defaultRtbProfileEnabled
    And the user selects the "Seller" company "transparency_enabled"
    And updates a company defaultRtbProfilesEnabled to "false"
    When the PSS user selects the site "aleksis_site"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddNewRtbProfileWithDisabledFlag_payload.json"
    Then "update site" failed with "400" response code and error message "Default RTB Profiles feature not enabled for the publisher"
    Then updates a company defaultRtbProfilesEnabled to "true"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddNewRtbProfileWithDisabledFlag_payload.json"
    And request passed successfully
    And the PSS user updates site with data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteAddNewRtbProfileWithDisabledFlag_payload.json"
    And request passed successfully
    And there are "8" rtb profiles for the company "transparency_enabled"

  @restoreCrudCoreDatabaseBefore

  #Update a site's rtb profile all fields with detail = true. Get the site with detail=true, detail=false or without detail
  Scenario: Update site by changing the field values in its rtb profile with detail=true
    And the user selects the "Seller" company "transparency_enabled"
    And the PSS user select the site "aleksis_site" with detail "true"
    And the PSS user get site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteChangeRtbProfileFieldValues_payload.json" with details "true"
    Then request passed successfully
    When the PSS user updates site with detail and data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteChangeRtbProfileFieldValues_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteChangeRtbProfileFieldValues_ER.json"
    When the PSS user select the site "aleksis_site" with detail "true"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/GetPssSiteRtbProfileAllDetailTrue_ER.json"
    When the PSS user select the site "aleksis_site" with detail "false"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/GetPssSiteRtbProfileAllDetailFalse_ER.json"
    When the PSS user selects the site "aleksis_site"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/GetPssSiteRtbProfileAllDetailFalse_ER.json"

  #Update a site's rtb profile some fields with detail = true. Update a site with detail = false and validate, that it is updated correctly
  Scenario: Update site by changing rtb profile fields with detail=true and only a few fields and then with detail=false
    And the user selects the "Seller" company "transparency_enabled"
    And the PSS user select the site "aleksis_site" with detail "true"
    When the PSS user get site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetailTrueFewFields_payload.json" with details "true"
    Then request passed successfully
    When the PSS user updates site with detail and data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetailTrueFewFields_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteDetailTrueFewFields_ER.json"
    When the PSS user get site update info from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetailFalse_payload.json" with details "false"
    Then request passed successfully
    When the PSS user updates site without detail and data from the json file "jsons/genevacrud/site/rtb/payload/UpdatePssSiteDetailFalse_payload.json"
    Then request passed successfully
    And returned "site" data matches the following json file "jsons/genevacrud/site/rtb/expected_results/UpdatePssSiteDetailFalse_ER.json"

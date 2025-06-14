Feature: create/update site with dcn as internal/external users

  Scenario: Create site with dcn for nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDcnInternalAdmin_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteDcnInternalAdmin_ER.json"
    And site name "dcn_internal_admin" is retrieved for dcn "ff808081015e5e616d087077d07e0az0"

  Scenario: Create site with dcn for nexage manager
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDcnInternalManager_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteDcnInternalManager_ER.json"
    And site name "dcn_internal_manager" is retrieved for dcn "ff808081015e5e616d087077d07e0ay1"

  Scenario: Create site with duplicate dcn for nexage admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDuplicateDcnInternal_payload.json"
    Then the request failed with http status "400" errorcode "1159" and message "Passed dcn already exists."
    And there is only "1" site with specified dcn "b4e02ff924554e5c9c6d4567d067d1bd"

  Scenario: Create site with duplicate dcn if it belongs to the same company
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDuplicateDcnSameCompanyInternal_payload.json"
    Then the request failed with http status "400" errorcode "1159" and message "Passed dcn already exists."
    And there is only "1" site with specified dcn "ff808081015e5e616d087077d07e0az0"

  Scenario: Create site with duplicate dcn for nexage manager
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDuplicateDcnInternal_payload.json"
    Then the request failed with http status "400" errorcode "1159" and message "Passed dcn already exists."
    And there is only "1" site with specified dcn "b4e02ff924554e5c9c6d4567d067d1bd"

  Scenario: Create site with null dcn for internal
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteNullDcnInternal_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteNullDcnInternal_ER.json"
    And site "dcn_internal_null" can be searched in the database

  Scenario: Create site with dcn length greater than allowed for internal
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteBigLengthDcnInternal_payload.json"
    Then the request failed with http status "400" errorcode "1157" and message "Site dcn should have no more than 32 characters."
    And site was not generated for dcn "1231241231241231f24123124123124123124123124123124f"

  Scenario: Create site with numeric dcn for internal
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteNumericDcnInternal_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteNumericDcnInternal_ER.json"
    And site name "dcn_internal_num" is retrieved for dcn "1234567890123456789012399"

  Scenario: Create site with upper and lower case dcn for internal
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteUpperLowerDcnInternal_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteUpperLowerDcnInternal_ER.json"
    And site name "dcn_internal_upper_lower" is retrieved for dcn "fK808081015e5F616S087077d07E0aC9"

  Scenario: Create site with upper case dcn that exists as lower case for internal
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteLowerDcnExistsInternal_payload.json"
    Then the request failed with http status "400" errorcode "1159" and message "Passed dcn already exists."
    And there is only "1" site with specified dcn "FF808081015E5E616D087077D07E0AY1"

  Scenario: Create site with single digit dcn for internal
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteOneDigitDcnInternal_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteOneDigitDcnInternal_ER.json"
    And site name "dcn_internal_one_digit" is retrieved for dcn "0"

  Scenario: Create site with special characters dcn for internal
    Given the user "admin1c" has logged in with role "AdminNexage"
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteSpecialCharDcnInternal_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteSpecialCharDcnInternal_ER.json"
    And site name "dcn_internal_special" is retrieved for dcn "§&<>¿¦§"

  Scenario: Create site with dcn for seller admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDcnExternalAdmin_payload.json"
    Then "Create site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "ff808081015e5e616d087077d07e0au3"

  Scenario: Create site with dcn for seller manager
    Given the user "pssTransEnabledMgr" has logged in with role "ManagerSeller"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteDcnExternalManager_payload.json"
    Then "Create site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "ff808081015e5e616d087077d07e0at4"

  Scenario: Create site with null dcn for external
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteNullDcnExternal_payload.json"
    Then request passed successfully with code "201"
    And returned "site" data matches the following json file "jsons/genevacrud/site/pss/expected_results/CreateSiteNullDcnExternal_ER.json"
    And site "dcn_external_null" can be searched in the database

  Scenario: Create site with dcn length greater than allowed for external
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteBigLengthDcnExternal_payload.json"
    Then "Create site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "1231241231241231f24123124123124123124123124123124k"

  Scenario: Create site with numeric dcn for external
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And "Seller" companies are retrieved
    When the PSS user creates a site from the json file "jsons/genevacrud/site/pss/payload/CreateSiteNumericDcnExternal_payload.json"
    Then "Create site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "ff808081015e5e616d087077d07e0aq7"

  Scenario: Update site with dcn for internal admin
    Given the user "admin1c" has logged in with role "AdminNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDcnInternalAdmin_payload.json"
    Then "Update site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "ff808081015e5e5805905b6311ba0499"

  Scenario: Update site with dcn for internal manager
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"
    And the user selects the "Seller" company "CRUDPositionTest"
    And the user selects the site "CRUDPosition_Site5_VersionTest"
    And the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDcnInternalManager_payload.json"
    Then "Update site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "ff808081015e5e616d087077d07e0aq7"

  Scenario: Update site with dcn for external admin
    Given the user "pssSellerAdmin" has logged in with role "AdminSeller"
    And the Seller sites for PSS user are retrieved
    And the PSS user selects the site "AS8B"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDcnExternalAdmin_payload.json"
    Then "Update site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "8a858a330137372da0982da3ffc50195"

  Scenario: Update site with dcn for external manager
    Given the user "pssTransEnabledMgr" has logged in with role "ManagerSeller"
    And the Seller sites for PSS user are retrieved
    And the PSS user selects the site "aleksis_site"
    When the PSS user gets site update info from the json file "jsons/genevacrud/site/pss/payload/UpdateSiteDcnExternalManager_payload.json"
    Then "Update site" failed with "401" response code and error message "DCN is read-only."
    And site was not generated for dcn "ff808081015d5df1ecc8f21eb7260999"

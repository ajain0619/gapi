Feature: update company for transparency as a seller user

  Background:  Log in as a pss admin and update transparency settings using pss api
    Given the user "pssTransEnabledAdmin" has logged in with role "AdminSeller"

  Scenario:  Update company and set transparency type to real
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateCompanySetTransparencyToReal_payload.json"
    Then request passed successfully
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/seller/UpdateCompanySetTransparencyToReal_ER.json"

  Scenario:  Update company and set transparency type to blind
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateCompanySetTransparencyToBlind_payload.json"
    Then request passed successfully
    And pubAliasId is retrieved for "transparency_enabled" publisher
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/seller/UpdateCompanySetTransparencyToBlind_ER.json"

  Scenario:  Update company and set transparency type to alias
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateCompanySetTransparencyToAlias_payload.json"
    Then request passed successfully
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/seller/UpdateCompanySetTransparencyToAlias_ER.json"

  Scenario:  Negative - attempt to set transparency to real when transparency is disabled
    Given the user "pssTransDisabledAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "transparency_disabled"
    Then the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateCompanySetTransparencyWhenTransparencyDisabled_payload.json"
    And "company update" failed with "401" response code

  Scenario:  Negative - attempt to enable transparency when transparency is disabled
    Given the user "pssTransDisabledAdmin" has logged in with role "AdminSeller"
    And the user selects the "Seller" company "transparency_disabled"
    Then the user updates a company from the json file "jsons/genevacrud/company/payload/seller/UpdateCompanyEnableTransparencyWhenTransparencyDisabled_payload.json"
    And "company update" failed with "401" response code

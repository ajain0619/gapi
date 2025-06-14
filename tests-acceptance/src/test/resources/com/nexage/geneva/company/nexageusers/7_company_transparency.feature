Feature: create, update company for transparency as nexage manager using the company api

  Background:  Log in as a nexage manager and create/update publishers with transparency settings
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"

  Scenario:  Create company without transparency settings and verify default setting is RealName
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithoutTransparencySettings_payload.json"
    Then request passed successfully
    Then returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/CreateWithoutTransparencySettings_ER.json"

  Scenario:  Create company with transparency enabled and set type to alias
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyEnabledAsAlias_payload.json"
    Then request passed successfully
    And pubAliasId was generated after create publisher
    Then returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/CreateWithTransparencyEnabledAsAlias_ER.json"

  Scenario:  Create company with transparency enabled and set type to real
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyEnabledAsReal_payload.json"
    Then request passed successfully
    And pubAliasId was not generated after create publisher
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/CreateWithTransparencyEnabledAsReal_ER.json"

  Scenario:  Create company with transparency enabled and set type to blind
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyEnabledAsBlind_payload.json"
    Then request passed successfully
    And pubAliasId was generated after create publisher
    Then returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/CreateWithTransparencyEnabledAsBlind_ER.json"

  Scenario:  Create company with transparency disabled and set type to alias
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyDisabledAsAlias_payload.json"
    Then request passed successfully
    And pubAliasId was generated after create publisher
    And returned "pss company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/CreateWithTransparencyDisabledAsAlias_ER.json"

  Scenario:  Create company with transparency disabled and set type to real
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyDisabledAsReal_payload.json"
    Then request passed successfully
    And pubAliasId was not generated after create publisher
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/CreateWithTransparencyDisabledAsReal_ER.json"

  Scenario:  Create company with transparency disabled and set type to blind
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyDisabledAsBlind_payload.json"
    Then request passed successfully
    And pubAliasId was generated after create publisher
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/CreateWithTransparencyDisabledAsBlind_ER.json"

  Scenario:  Update a company to enable transparency
    Given the user selects the "Seller" company "transparency_disabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanyEnableTransparency_payload.json"
    Then request passed successfully
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/UpdateCompanyEnableTransparency_ER.json"

  Scenario:  Update company and set transparency type to real
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanySetTransparencyToReal_payload.json"
    Then request passed successfully
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/UpdateCompanySetTransparencyToReal_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update company and set transparency type to blind
    Given the user selects the "Seller" company "transparency_enabled"
    And pubAliasId is retrieved for "transparency_enabled" publisher
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanySetTransparencyToBlind_payload.json"
    Then request passed successfully
    And pubAliasId was regenerated for "transparency_enabled" publisher
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/UpdateCompanySetTransparencyToBlind_ER.json"

  Scenario:  Update a company to disable transparency
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanyDisableTransparency_payload.json"
    Then request passed successfully
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/UpdateCompanyDisableTransparency_ER.json"

  Scenario:  Negative - Create company with transparency type as alias with alias name set to null
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyAsAliasAndAliasNameNull_payload.json"
    Then "create company" failed with "400" response code
    And "create company" failed with "Publisher name alias is incorrect" response message

  Scenario:   Negative - Update company with transparency type as alias with alias name set to null
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateSetTransparencyToAliasAndAliasNameNull_payload.json"
    Then "update company" failed with "400" response code
    And "update company" failed with "Publisher name alias is incorrect" response message

  Scenario:   Negative - Create company and set transparency type to real with alias name set
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyAsRealWithAliasName_payload.json"
    Then "create company" failed with "400" response code
    And "create company" failed with "Publisher name alias is incorrect" response message

  Scenario:   Negative - Update a company and set transparency type to real with alias name set
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateSetTransparencyToRealWithAliasName_payload.json"
    Then "update company" failed with "400" response code
    And "update company" failed with "Publisher name alias is incorrect" response message

  Scenario:  Negative - Create company and set transparency type to blind with alias name set
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyAsBlindWithAliasName_payload.json"
    Then "create company" failed with "400" response code
    And "create company" failed with "Publisher name alias is incorrect" response message

  Scenario:  Negative - Update company and set transparency type to blind with alias name set
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateSetTransparencyToBlindWithAliasName_payload.json"
    Then "company update" failed with "400" response code
    And "update company" failed with "Publisher name alias is incorrect" response message

  Scenario:  Negative - Create company and set transparency mode value to a string
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithTransparencyModeAsString_payload.json"
    Then "create company" failed with "400" response code

  Scenario:  Negative - Update company and set transparency mode value to a string
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateSetTransparencyModeAsString_payload.json"
    Then "company update" failed with "400" response code

  Scenario:  Negative - Create company and set transparency mode value to an invalid enum value
    Given the user creates a "Seller" company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithInvalidTransparencyModeEnum_payload.json"
    Then "company update" failed with "400" response code
    And "create company" failed with "Transparency flag is incorrect" response message

  Scenario:  Negative - Update company and set transparency mode value to an invalid enum vtealue
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/company/payload/nexageuser/CreateWithInvalidTransparencyModeEnum_payload.json"
    Then "company update" failed with "400" response code

  #Update a company to allow self-service and transparency settings. Add new alias name, regenerate id and make sure, that id is changed
  Scenario: update company with alias name and validate regenerate id
    Given the user "admin1c" has logged in with role "AdminNexage"
    And set company "traffic_type"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/pss/payload/UpdateCompanyAliasName_payload.json"
    Then request passed successfully
    And pubAliasId is retrieved for "traffic_type" publisher
    And pubAliasId was regenerated for "transparency_enabled" publisher
    And returned "company" data matches the following json file "jsons/genevacrud/company/pss/expected_results/UpdateCompanyAliasName_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update a company to create a custom pubAliasId when regenerateIdAlias is false and pubAliasId is manually changed
    And the user selects the "Seller" company "transparency_enabled"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanyWithCustomPubAliasIdAndRegenIdIsFalse_payload.json"
    Then request passed successfully
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/UpdateCompanyWithCustomPubAliasIdAndRegenIdIsFalse_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update a company to create a custom pubAliasId when regenerateIdAlias is true and pubAliasId is manually changed - pubAliasId should be regenerated
    And the user selects the "Seller" company "transparency_enabled"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanyWithCustomPubAliasIdAndRegenIdIsTrue1_payload.json"
    Then request passed successfully
    And pubAliasId is retrieved for "transparency_enabled" publisher
    And pubAliasId was regenerated for "transparency_enabled" publisher
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/UpdateCompanyWithCustomPubAliasIdAndRegenIdIsTrue_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update a company to create a custom pubAliasId after a pubAliasId has been generated
    And the user selects the "Seller" company "transparency_enabled"
    When the user updates a company using pss from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanyWithCustomPubAliasIdAndRegenIdIsTrue1_payload.json"
    Then request passed successfully
    And pubAliasId is retrieved for "transparency_enabled" publisher
    And pubAliasId was regenerated for "transparency_enabled" publisher
    Then the user updates a company using pss from the json file "jsons/genevacrud/company/payload/nexageuser/UpdateCompanyWithCustomPubAliasIdAndRegenIdIsTrue2_payload.json"
    And returned "company" data matches the following json file "jsons/genevacrud/company/expected_results/nexageuser/UpdateCompanyWithCustomPubAliasIdAndRegenIdIsTrue_ER.json"

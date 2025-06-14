Feature: Update publisher for transparency as nexage manager using pss api

  Background:  Log in as a nexage manager and update publisher transparency settings
    Given the user "crudnexagemanager" has logged in with role "ManagerNexage"

  Scenario:  Update a publisher to enable transparency
    Given the user selects the "Seller" company "transparency_disabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherEnableTransparency_payload.json"
    Then request passed successfully
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherEnableTransparency_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update a publisher and set transparency type to real
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyToReal_payload.json"
    Then request passed successfully
    And pubAliasId is not set for "transparency_enabled" publisher
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherSetTransparencyToReal_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher and set transparency type to blind
    Given the user selects the "Seller" company "transparency_enabled"
    And pubAliasId is retrieved for "transparency_enabled" publisher
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyToBlind_payload.json"
    Then request passed successfully
    And pubAliasId was regenerated for "transparency_enabled" publisher
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherSetTransparencyToBlind_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher and set transparency type to aliases
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyToAlias_payload.json"
    Then request passed successfully
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherSetTransparencyToAlias_ER.json"

  Scenario:  Update a publisher and set transparency type to real when disabled
    Given the user selects the "Seller" company "transparency_disabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherToRealWhenDisabled_payload.json"
    Then request passed successfully
    And pubAliasId is not set for "transparency_disabled" publisher
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherToRealWhenDisabled_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher and set transparency type to blind when disabled
    Given the user selects the "Seller" company "transparency_disabled"
    And pubAliasId is not set for "transparency_disabled" publisher
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherToBlindWhenDisabled_payload.json"
    Then request passed successfully
    And pubAliasId was regenerated for "transparency_disabled" publisher
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherToBlindWhenDisabled_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher and set transparency type to aliases when disabled
    Given the user selects the "Seller" company "transparency_disabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherToAliasWhenDisabled_payload.json"
    Then request passed successfully
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherToAliasWhenDisabled_ER.json"

  Scenario:  Update a publisher to disable transparency
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherDisableTransparency_payload.json"
    Then request passed successfully
    And returned "pss company" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherDisableTransparency_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update a publisher to regenerate alias id
    Given the user selects the "Seller" company "transparency_enabled"
    Then pubAliasId is retrieved for "transparency_enabled" publisher
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherRegenerateAliasId_payload.json"
    Then request passed successfully
    Then pubAliasId was regenerated for "transparency_enabled" publisher

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher - remove transparency settings when setting transparency mgmt to disabled and setting defaultTransparencySettings to null
    Given the user selects the "Seller" company "transparency_enabled"
    And transparency is disabled for publisher "transparency_enabled"
    Then the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetDefaultTransparencyToNullWhenDisabled_payload.json"
    Then request passed successfully
    And returned "pss company" data matches the following json file "jsons/genevacrud/publisher/expected_results/nexageuser/UpdatePublisherSetDefaultTransparencyToNullWhenDisabled_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:   Negative - Update publisher with transparency mode as aliases with alias name set to null
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyToAliasWithAliasNameNull_payload.json"
    Then "update company" failed with "400" response code
    And "update company" failed with "Publisher name alias is incorrect" response message

  Scenario:   Negative - Update a publisher and set transparency type to real with alias name set
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyToRealWithAliasName_payload.json"
    Then "create company" failed with "400" response code
    And "update company" failed with "Publisher name alias is incorrect" response message

  Scenario:  Negative - Update publisher and set transparency type to blind with alias name set
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a company from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyToBlindWithAliasName_payload.json"
    Then "company update" failed with "400" response code

  Scenario:  Negative - Update publisher and set transparency mode value to an invalid string value
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyModeInvalidString_payload.json"
    Then "company update" failed with "400" response code

  Scenario:  Negative - Update publisher and set transparency mode value to a number
    Given the user selects the "Seller" company "transparency_enabled"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/nexageuser/UpdatePublisherSetTransparencyModeAsNumber_payload.json"
    Then "company update" failed with "400" response code

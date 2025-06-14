Feature: Update publisher for transparency as pss manager

  Background:  Log in as a pss manager and update transparency settings
    Given the user "pssTransEnabledMgr" has logged in with role "ManagerSeller"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher set transparency type to alias
    Given the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/pssuser/UpdatePublisherSetTransparencyToAlias_payload.json"
    Then request passed successfully
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/pssuser/UpdatePublisherSetTransparencyToAlias_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher set transparency type to real
    Given pubAliasId is retrieved for "transparency_enabled" publisher
    Then the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/pssuser/UpdatePublisherSetTransparencyToReal_payload.json"
    Then request passed successfully
    Then pubAliasId is not set for "transparency_enabled" publisher
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/pssuser/UpdatePublisherSetTransparencyToReal_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update publisher set transparency type to blind
    Given the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/pssuser/UpdatePublisherSetTransparencyToBlind_payload.json"
    Then request passed successfully
    Then pubAliasId is retrieved for "transparency_enabled" publisher
    And returned "publisher" data matches the following json file "jsons/genevacrud/publisher/expected_results/pssuser/UpdatePublisherSetTransparencyToBlind_ER.json"

  @restoreCrudCoreDatabaseBefore

  Scenario:  Update a publisher to regenerate alias id
    Given pubAliasId is retrieved for "transparency_enabled" publisher
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/pssuser/UpdatePublisherRegenerateAliasId_payload.json"
    Then request passed successfully
    Then pubAliasId was regenerated for "transparency_enabled" publisher

  @restoreCrudCoreDatabaseBefore

  Scenario:  Negative - Update publisher - remove transparency settings when setting transparency mgmt disabled and setting defaultTransparencySettings to null
    Given transparency is disabled for publisher "transparency_enabled"
    Then the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/pssuser/UpdatePublisherSetDefaultTransparencyToNullWhenDisabled_payload.json"
    Then "update publisher" failed with "401" response code

  Scenario:  Negative - Attempt to update transparency settings when transparency is disabled
    Given the user "pssTransDisabledMgr" has logged in with role "ManagerSeller"
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/pssuser/UpdatePublisherSetTransparencyToBlindWhenDisabled_payload.json"
    Then "update publisher" failed with "401" response code

  Scenario: Negative - Attempt to regenerate publisher alias id when transparency is disabled
    Given pubAliasId is not set for "transparency_disabled" publisher
    And the user updates a publisher from the json file "jsons/genevacrud/publisher/payload/pssuser/UpdatePublisherRegenerateAliasIdWhenDisabled_payload.json"
    Then "regenerate publisher alias id" failed with "401" response code

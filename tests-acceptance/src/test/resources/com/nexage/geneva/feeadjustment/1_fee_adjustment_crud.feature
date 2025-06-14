Feature: Create, read, update and delete fee adjustments

  Scenario: Get all fee adjustments as "NexageUser1".
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all fee adjustments
    Then request passed successfully
    And returned "fee adjustment page" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_all_fee_adjustments_ER.json"

  Scenario: Get all fee adjustments as "NexageUser1" by name.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all fee adjustments matching qf "name" with qt "fee-adjustment-1"
    Then request passed successfully
    And returned "fee adjustment page" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_all_by_name_fee_adjustments_ER.json"

  Scenario: Get all fee adjustments as "NexageUser1" with pagination parameters.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all fee adjustments of page "0" with size "1"
    Then request passed successfully
    And returned "fee adjustment page" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_all_paged_fee_adjustments_ER.json"

  Scenario: Get all fee adjustments as "NexageUser1" by name with pagination parameters.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all fee adjustments both matching qf "name" with qt "fee-adjustment-1" and in page "0" with size "10"
    Then request passed successfully
    And returned "fee adjustment page" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_all_by_name_fee_adjustments_ER.json"

  Scenario: Get all fee adjustments as "NexageUser1" by enabled.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all fee adjustments matching enabled "true"
    Then request passed successfully
    And returned "fee adjustment page" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_all_by_enabled_ER.json"

  Scenario: Get all fee adjustments as "NexageUser1" by disabled.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all fee adjustments matching enabled "false"
    Then request passed successfully
    And returned "fee adjustment page" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_all_by_disabled_ER.json"

  Scenario: Get fee adjustment as "NexageAdmin1".
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user gets fee adjustment with pid "1"
    Then request passed successfully
    And returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_fee_adjustment_1_ER.json"

  Scenario: Get fee adjustment as "crudnexagemanageryield".
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    When the user gets fee adjustment with pid "2"
    Then request passed successfully
    And returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/get_fee_adjustment_2_ER.json"

  Scenario: Create a fee adjustment as "NexageAdmin1".
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates fee adjustment from the json file "jsons/genevacrud/feeadjustment/payload/create_fee_adjustment_1_payload.json"
    Then request passed successfully with code "201"
    And returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/create_fee_adjustment_1_ER.json"

  Scenario: Create a fee adjustment as "crudnexagemanageryield".
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    When the user creates fee adjustment from the json file "jsons/genevacrud/feeadjustment/payload/create_fee_adjustment_2_payload.json"
    Then request passed successfully with code "201"
    And returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/create_fee_adjustment_2_ER.json"

  Scenario: Create a fee adjustment with a duplicate name should fail.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates fee adjustment from the json file "jsons/genevacrud/feeadjustment/payload/create_fee_adjustment_1_payload.json"
    Then "created fee adjustment" failed with "409" response code and error message "Unable to create (or update) the fee adjustment because it's name is not unique."

  Scenario: Update fee adjustment as "NexageAdmin1".
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    Given the user updates fee adjustment with pid "1" from the json file "jsons/genevacrud/feeadjustment/payload/update_fee_adjustment_1_payload.json"
    And request passed successfully
    Then returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/update_fee_adjustment_1_ER.json"

  Scenario: Update fee adjustment as "crudnexagemanageryield".
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    Given the user updates fee adjustment with pid "2" from the json file "jsons/genevacrud/feeadjustment/payload/update_fee_adjustment_2_payload.json"
    And request passed successfully
    Then returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/update_fee_adjustment_2_ER.json"

  Scenario: Update a fee adjustment with a duplicate name should fail.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates fee adjustment from the json file "jsons/genevacrud/feeadjustment/payload/create_fee_adjustment_1_payload.json"
    Then "updated fee adjustment" failed with "409" response code and error message "Unable to create (or update) the fee adjustment because it's name is not unique."

  Scenario: Update fee adjustment as "NexageAdmin1" with unspecified "feeAdjustmentSeller" list and unspecified "feeAdjustmentBuyer" list.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    Given the user updates fee adjustment with pid "1" from the json file "jsons/genevacrud/feeadjustment/payload/update_fee_adjustment_3_payload.json"
    And request passed successfully
    Then returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/update_fee_adjustment_3_ER.json"

  Scenario: Delete a fee adjustment as "NexageUser1".
    When the user deletes fee adjustment with pid "1"
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    Then request passed successfully
    And returned "fee adjustment" data matches the following json file "jsons/genevacrud/feeadjustment/expected_results/delete_fee_adjustment_ER.json"
    When the user gets fee adjustment with pid "1"
    Then "delete fee adjustment" failed with "404" response code and error message "Unable to find the fee adjustment with the specified pid."

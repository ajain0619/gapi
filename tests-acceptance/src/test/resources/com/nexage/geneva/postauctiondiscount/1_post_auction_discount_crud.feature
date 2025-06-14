Feature: Create, read post auction discounts

  Scenario: Get all post auction discounts as "NexageUser1".
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all post auction discounts
    And insert deal publisher data of deal_pid "1" and pub_pid "131"
    Then request passed successfully
    And returned "post auction discount page" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/get_all_post_auction_discounts_ER.json"

  Scenario: Get all post auction discounts as "NexageUser1" by name.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all post auction discounts matching qf "discountName" with qt "discount-1"
    Then request passed successfully
    And returned "post auction discount page" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/get_all_by_name_post_auction_discounts_ER.json"

  Scenario: Get all post auction discounts as "NexageUser1" with pagination parameters.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all post auction discounts of page "0" with size "1"
    Then request passed successfully
    And returned "post auction discount page" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/get_all_paged_post_auction_discounts_ER.json"

  Scenario: Get all post auction discounts as "NexageUser1" by name with pagination parameters.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all post auction discounts both matching qf "discountName" with qt "discount-1" and in page "0" with size "10"
    Then request passed successfully
    And returned "post auction discount page" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/get_all_by_name_post_auction_discounts_ER.json"

  Scenario: Get all post auction discounts as "NexageUser1" by enabled.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all post auction discounts matching enabled "true"
    Then request passed successfully
    And returned "post auction discount page" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/get_all_by_enabled_post_auction_discounts_ER.json"

  Scenario: Get all post auction discounts as "NexageUser1" by disabled.
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets all post auction discounts matching enabled "false"
    Then request passed successfully
    And returned "post auction discount page" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/get_all_by_disabled_post_auction_discounts_ER.json"

  Scenario: Get post auction discount as "NexageAdmin1".
    Given the user "NexageUser1" has logged in with role "UserNexage"
    When the user gets post auction discount with pid "1"
    Then request passed successfully
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/get_post_auction_discount_1_ER.json"

  Scenario: Create a post auction discount as "NexageAdmin1".
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_1_payload.json"
    Then request passed successfully with code "201"
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/create_post_auction_discount_1_ER.json"

  Scenario: Create a post auction discount as "crudnexagemanageryield" with ALL deals against existing ALL deal to validate duplicate error
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_payload_duplicate_with_all_deals.json"
    Then "created post auction discount" failed with "400" response code and error message "Unable to create (or update) the post auction discount because a post auction discount with that seller/dsp seat combination (test-post-auction-discount-1) already exists."

  Scenario: Create a post auction discount as "crudnexagemanageryield" with open auction against existing open auction configuration to validate duplicate error
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_payload_duplicate_open_auction.json"
    Then "created post auction discount" failed with "400" response code and error message "Unable to create (or update) the post auction discount because a post auction discount with that seller/dsp seat combination (test-post-auction-discount-1) already exists."

  Scenario: Create a post auction discount for specific deal as "NexageAdmin1".
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    And valid deal data for company pid 10200 and deal pid 6543 exists
    And insert deal publisher data of deal_pid "6543" and pub_pid "118"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_1_specific_deal_payload.json"
    Then request passed successfully with code "201"
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/create_post_auction_discount_1_specific_ER.json"

  Scenario: Create a post auction discount as "crudnexagemanageryield" with ALL deals against existing specific deal to validate duplicate error
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_payload_duplicate_all_deal.json"
    Then "created post auction discount" failed with "400" response code and error message "Unable to create (or update) the post auction discount because a post auction discount with that seller/dsp seat combination (test-post-auction-discount-1_duplicate_specific_deals) already exists."

  Scenario: Create a post auction discount as "crudnexagemanageryield" with specific deals against existing deal to validate duplicate error
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_payload_with_specific_deal_existing_deal.json"
    Then "created post auction discount" failed with "400" response code and error message "Unable to create (or update) the post auction discount because a post auction discount with that seller/dsp seat combination (test-post-auction-discount-1_duplicate_specific_deals) already exists."

  Scenario: Create a post auction discount as "crudnexagemanageryield".
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_2_payload.json"
    Then request passed successfully with code "201"
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/create_post_auction_discount_2_ER.json"

  Scenario: Create a post auction discount as "crudnexagemanageryield" and specific deals validation fail.
    Given the user "crudnexagemanageryield" has logged in with role "ManagerYieldNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_4_payload.json"
    Then "updated post auction discount" failed with "400" response code and error message "Invalid Deal for given DSP/Buyer Seat combination"

  Scenario: Create a post auction discount as "NexageAdmin1" with openAuction disabled and deals disabled.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_3_payload.json"
    Then "create post auction discount" failed with "400" response code and error message "Either dealsEnabled or openAuctionEnabled should be true"

  Scenario: Update post auction discount as "NexageAdmin1".
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    Given the user updates post auction discount with pid "1" from the json file "jsons/genevacrud/postauctiondiscount/payload/update_post_auction_discount_4_payload.json"
    Then "update post auction discount" failed with "400" response code and error message "Either dealsEnabled or openAuctionEnabled should be true"

  Scenario: Update post auction discount as "NexageAdmin1".
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    Given the user updates post auction discount with pid "1" from the json file "jsons/genevacrud/postauctiondiscount/payload/update_post_auction_discount_1_payload.json"
    And request passed successfully
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/update_post_auction_discount_1_ER.json"
    When the user gets post auction discount with pid "1"
    Then request passed successfully
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/update_post_auction_discount_1_ER.json"
    When the user updates post auction discount with pid "1" from the json file "jsons/genevacrud/postauctiondiscount/payload/update_post_auction_discount_3_payload.json"
    And request passed successfully
    Then returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/update_post_auction_discount_2_ER.json"
    When the user gets post auction discount with pid "1"
    Then request passed successfully
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/update_post_auction_discount_2_ER.json"

  Scenario: Update a post auction discount with a duplicate name should fail.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user updates post auction discount with pid "1" from the json file "jsons/genevacrud/postauctiondiscount/payload/update_post_auction_discount_2_payload.json"
    Then "updated post auction discount" failed with "409" response code and error message "Unable to create (or update) the post auction discount because it's name is not unique."

  Scenario: Update post auction discount as "NexageAdmin1" and specific deals validation fail.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user updates post auction discount with pid "1" from the json file "jsons/genevacrud/postauctiondiscount/payload/update_post_auction_discount_5_payload.json"
    Then "updated post auction discount" failed with "400" response code and error message "Invalid Deal for given DSP/Buyer Seat combination"

  Scenario: Create a post auction discount with revenue groups.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_5_payload.json"
    Then request passed successfully with code "201"
    And returned "post auction discount" data matches the following json file "jsons/genevacrud/postauctiondiscount/expected_results/create_post_auction_discount_5_ER.json"

  Scenario: Create a post auction discount with non-existent types.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_6_payload.json"
    Then "create post auction discount" failed with "400" response code and error message "Unable to find the selected discount type."

  Scenario: Create a post auction discount with non-existent revenue groups.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_7_payload.json"
    Then "create post auction discount" failed with "400" response code and error message "Unable to find the selected revenue group."

  Scenario: Create a post auction discount with a revenue group that belong to a conflicting seller/dsp pair.
    Given the user "NexageAdmin1" has logged in with role "AdminNexage"
    When the user creates post auction discount from the json file "jsons/genevacrud/postauctiondiscount/payload/create_post_auction_discount_8_payload.json"
    Then "create post auction discount" failed with "400" response code and error message "Unable to create (or update) the post auction discount because a post auction discount with that seller/dsp seat combination (golden-data-discount-1-update) already exists."

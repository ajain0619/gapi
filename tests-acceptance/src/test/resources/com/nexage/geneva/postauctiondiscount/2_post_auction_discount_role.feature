Feature: Post auction discount role based access tests.

  Scenario Outline: Getting all post auction discounts with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user gets all post auction discounts
    Then "post auction discount get all" failed with "401" response code

    Examples:
      | user        | role       |
      | SellerUser1 | UserSeller |
      | BuyerUser1  | UserBuyer  |

  Scenario Outline: Getting a fee adjustment with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user gets fee adjustment with pid "1"
    Then "fee adjustment get" failed with "401" response code

    Examples:
      | user        | role       |
      | SellerUser1 | UserSeller |
      | BuyerUser1  | UserBuyer  |

  Scenario Outline: Creating a fee adjustment with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user creates fee adjustment from the json file "jsons/genevacrud/feeadjustment/payload/<file_payload>.json"
    Then "fee adjustment creation" failed with "401" response code

    Examples:
      | user        | role       | file_payload                    |
      | NexageUser1 | UserNexage | create_fee_adjustment_1_payload |
      | SellerUser1 | UserSeller | create_fee_adjustment_1_payload |
      | BuyerUser1  | UserBuyer  | create_fee_adjustment_1_payload |

  Scenario Outline: Updating a post auction discount with no permissions should fail.
    Given the user "<user>" has logged in with role "<role>"
    When the user updates post auction discount with pid "1" from the json file "jsons/genevacrud/postauctiondiscount/payload/<file_payload>.json"
    Then "post auction discount update" failed with "401" response code

    Examples:
      | user        | role       | file_payload                           |
      | NexageUser1 | UserNexage | update_post_auction_discount_1_payload |
      | SellerUser1 | UserSeller | update_post_auction_discount_1_payload |
      | BuyerUser1  | UserBuyer  | update_post_auction_discount_1_payload |

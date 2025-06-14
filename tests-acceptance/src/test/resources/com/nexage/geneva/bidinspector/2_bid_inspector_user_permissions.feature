Feature: Bid inspector - User permissions

  Scenario Outline: Search bids with a user having adequate authorization
    Given the user "<user>" has logged in with role "<role>"
    When the user searches for bids with page "Empty", size "Empty", qf "Empty", sort "Empty"
    Then request passed successfully

    Examples:
      | user              | role              |
      | crudnexageadmin   | AdminNexage       |
      | crudnexagemanager | ManagerNexage     |

  Scenario Outline: Search bids with a user having inadequate authorization
    Given the user "<user>" has logged in with role "<role>"
    When the user searches for bids with page "Empty", size "Empty", qf "Empty", sort "Empty"
    Then the request failed with http status "401" errorcode "1004" and message "You're not authorized to perform this operation."

    Examples:
      | user              | role              |
      | crudnexageuser    | UserNexage        |
      | adminathens1      | AdminSeller       |
      | SellerManager1    | ManagerSeller     |
      | cpi_seller        | UserSeller        |
      | svcsellerseat034  | AdminSellerSeat   |
      | svcsellerseat957  | ManagerSellerSeat |
      | svcsellerseat878  | AdminSellerSeat   |
      | svcsellerseat470  | ManagerSellerSeat |
      | svcsellerseat944  | UserSellerSeat    |
      | svcsellerseat444  | UserSellerSeat    |

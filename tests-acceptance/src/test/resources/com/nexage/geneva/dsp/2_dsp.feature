Feature: DSP - User Permissions for DSP

  Scenario Outline: DSP enabled for nexage, seller and seller seat (admin,mgr,user)
    Given the user "<user>" has logged in with role "<role>"
    When the user requests all DSPs
    Then request passed successfully

    Examples:
      | user              | role            |
      | crudnexageadmin   | AdminNexage     |
      | crudnexagemanager | ManagerNexage   |
      | crudnexageuser    | UserNexage      |
      | adminathens1      | AdminSeller     |
      | SellerManager1    | ManagerSeller   |
      | cpi_seller        | UserSeller      |
      | svcsellerseat034  | AdminSellerSeat |
      | svcsellerseat957  | AdminSellerSeat |
      | svcsellerseat878  | AdminSellerSeat |
      | svcsellerseat470  | AdminSellerSeat |
      | svcsellerseat944  | AdminSellerSeat |
      | svcsellerseat444  | AdminSellerSeat |

  Scenario Outline: DSP disabled for buyer users (admin,mgr,user)
    Given the user "<user>" has logged in with role "<role>"
    When the user requests all DSPs
    Then the request failed with http status "401" errorcode "1004" and message "You're not authorized to perform this operation."

    Examples:
      | user          | role         |
      | BuyerAdmin1   | AdminBuyer   |
      | BuyerManager1 | ManagerBuyer |
      | BuyerUser1    | UserBuyer    |

  Scenario Outline: DSP summaries enabled for nexage, seller and seller seat users (admin,mgr,user)
    Given the user "<user>" has logged in with role "<role>"
    When the user requests all DSP summaries
    Then request passed successfully

    Examples:
      | user              | role            |
      | crudnexageadmin   | AdminNexage     |
      | crudnexagemanager | AdminNexage     |
      | crudnexageuser    | AdminNexage     |
      | adminathens1      | AdminSeller     |
      | SellerManager1    | ManagerSeller   |
      | cpi_seller        | UserSeller      |
      | svcsellerseat034  | AdminSellerSeat |
      | svcsellerseat957  | AdminSellerSeat |
      | svcsellerseat878  | AdminSellerSeat |
      | svcsellerseat470  | AdminSellerSeat |
      | svcsellerseat944  | AdminSellerSeat |
      | svcsellerseat444  | AdminSellerSeat |

  Scenario Outline: DSP summaries disabled for buyer users (admin,mgr,user)
    Given the user "<user>" has logged in with role "<role>"
    When the user requests all DSP summaries
    Then the request failed with http status "401" errorcode "1004" and message "You're not authorized to perform this operation."

    Examples:
      | user          | role         |
      | BuyerAdmin1   | AdminBuyer   |
      | BuyerManager1 | ManagerBuyer |
      | BuyerUser1    | UserBuyer    |

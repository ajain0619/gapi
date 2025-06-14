package com.nexage.geneva.model.crud;

import java.util.Set;
import lombok.Data;

@Data
public class UserDTO {
  private String pid;
  private String userName;
  private String name;
  private String email;
  private String contactNumber;
  private String title;
  private String role;
  private Boolean enabled;
  private Boolean primaryContact;
  private Set<Company> companies;
  private SellerSeat sellerSeat;
  private String id;
  private Integer version;
  private String oneCentralUserName;
  private String firstName;
  private String lastName;
  private boolean global;
  private boolean dealAdmin;
}

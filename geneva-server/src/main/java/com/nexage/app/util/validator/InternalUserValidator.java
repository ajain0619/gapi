package com.nexage.app.util.validator;

import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalUserValidator {

  private static final List<String> INTERNAL_EMAILS = List.of("@yahooinc.com");

  /**
   * Verify if a given user is considered internal or not based on criteria.
   *
   * @param user {@link User}
   * @return true if internal, false otherwise.
   */
  public static boolean isInternal(User user) {
    var email = user.getEmail();
    var companyType = user.getCompanyType();
    if (email == null || email.equals("") || companyType == null) return false;

    for (String acceptedInternalEmails : INTERNAL_EMAILS) {
      if (StringUtils.contains(email, acceptedInternalEmails) && companyType == CompanyType.NEXAGE)
        return true;
    }
    return false;
  }
}

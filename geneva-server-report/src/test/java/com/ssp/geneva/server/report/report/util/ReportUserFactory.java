package com.ssp.geneva.server.report.report.util;

import com.ssp.geneva.server.report.report.ReportUser;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ReportUserFactory {

  private ReportUserFactory() {}

  public static Long noUserPid() {
    return null;
  }

  public static Set<Long> withCompanies(Long... pids) {
    return new HashSet<>(Arrays.asList(pids));
  }

  public static ReportUser aSeller(Set<Long> companies, Long userPid) {
    return anUser(companies, false, userPid);
  }

  public static ReportUser aSeller(Set<Long> companies) {
    return aSeller(companies, new Random().nextLong());
  }

  public static ReportUser aNexageUser() {
    return aNexageUser(withCompanies());
  }

  public static ReportUser aNexageUser(Set<Long> companies) {
    return anUser(companies, true, noUserPid());
  }

  public static ReportUser anUser(Set<Long> companies, boolean isNexageUser, Long userPid) {
    return new ReportUser() {
      @Override
      public Long getCompany() {
        return null;
      }

      @Override
      public Set<Long> getCompanies() {
        return companies;
      }

      @Override
      public String getUserName() {
        return null;
      }

      @Override
      public Long getUserPid() {
        return userPid;
      }

      @Override
      public boolean isNexageUser() {
        return isNexageUser;
      }
    };
  }
}

package com.nexage.app.dto;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceMetricDTO {
  private Integer userId;
  private String userRole;
  private String userCompanyName;
  private Integer userCompanyPID;
  private String userCompanyType;
  private @NotNull String route;
  private String originalRoute;
  private Date pageLoadStart;
  private Date pageLoadComplete;
  private @NotNull Integer pageLoadTime;
  private String browser;
}

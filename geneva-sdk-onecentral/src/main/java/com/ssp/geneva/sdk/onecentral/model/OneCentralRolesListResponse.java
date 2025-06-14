package com.ssp.geneva.sdk.onecentral.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class OneCentralRolesListResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<OneCentralUserRolesResponse> list;
  private int totalCount;
}

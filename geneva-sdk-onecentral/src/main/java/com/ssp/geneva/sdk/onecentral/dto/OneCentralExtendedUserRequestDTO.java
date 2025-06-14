package com.ssp.geneva.sdk.onecentral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(Include.NON_DEFAULT)
public class OneCentralExtendedUserRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private OneCentralUserRequestDTO userRequestDTO;
  private String roleName;
  private boolean sellerSeatEnabled;
}

package com.nexage.admin.core.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SeatHolderMetadataDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  private int activeIOs;
}

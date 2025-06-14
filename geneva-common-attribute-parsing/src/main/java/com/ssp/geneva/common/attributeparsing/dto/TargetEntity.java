package com.ssp.geneva.common.attributeparsing.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TargetEntity {

  private OperatorType operatorType;
  private String attributeType;
  private List<String> targetValues = new ArrayList<>();

  public TargetEntity() {}
}

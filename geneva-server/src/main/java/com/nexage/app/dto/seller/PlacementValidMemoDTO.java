package com.nexage.app.dto.seller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacementValidMemoDTO {

  private String validMemo;
  private boolean isUnique;

  public static PlacementValidMemoDTO buildDefault() {
    return new PlacementValidMemoDTO("", false);
  }
}

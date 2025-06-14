package com.nexage.app.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldError {

  private String fieldName;
  private Object fieldValue;
  private String errorMessage;

  public FieldError(String fieldName, String errorMessage) {
    this.fieldName = fieldName;
    this.errorMessage = errorMessage;
  }
}

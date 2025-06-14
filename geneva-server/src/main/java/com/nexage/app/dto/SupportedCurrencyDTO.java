package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SupportedCurrencyDTO {

  private final String symbol;
  private final String name;
  private final String nativeSymbol;
  private final String code;

  @JsonCreator
  public SupportedCurrencyDTO(
      @JsonProperty("symbol") String symbol,
      @JsonProperty("name") String name,
      @JsonProperty("symbol_native") String nativeSymbol,
      @JsonProperty("code") String code) {
    this.symbol = symbol;
    this.name = name;
    this.nativeSymbol = nativeSymbol;
    this.code = code;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getName() {
    return name;
  }

  public String getNativeSymbol() {
    return nativeSymbol;
  }

  public String getCode() {
    return code;
  }
}

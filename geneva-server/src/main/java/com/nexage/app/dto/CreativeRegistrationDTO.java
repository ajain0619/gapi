package com.nexage.app.dto;

import com.aol.crs.model.v1.ContentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.app.util.validator.CreativeRegistrationConstraint;
import com.nexage.app.util.validator.ValidationMessages;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@CreativeRegistrationConstraint(groups = {Default.class})
public class CreativeRegistrationDTO {
  @Size(max = 255, message = ValidationMessages.WRONG_STRING_LENGTH_TO_LARGE)
  private String seatId;

  private List<Long> sellerIds = new ArrayList<>();

  @NotNull(message = ValidationMessages.WRONG_REQUIRED_FIELD)
  private String contentMarkup;

  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private ContentType contentType;

  @NotNull(message = ValidationMessages.WRONG_REQUIRED_FIELD)
  private String adomain;

  private String iurl;

  @Valid
  @NotNull(message = ValidationMessages.WRONG_IS_EMPTY)
  private Display display;

  private String countryCode;

  private List<String> dealIds = new ArrayList<>();

  private boolean isDoohAd;
}

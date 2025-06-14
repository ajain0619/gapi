package com.nexage.admin.core.custom;

import com.nexage.admin.core.enums.UserIdBidRequestType;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserIdBidRequestTypeConverter
    implements AttributeConverter<UserIdBidRequestType, Integer> {

  @Override
  public Integer convertToDatabaseColumn(UserIdBidRequestType userIdBidRequestType) {
    return Objects.requireNonNullElse(userIdBidRequestType, UserIdBidRequestType.UNKNOWN)
        .getValue();
  }

  @Override
  public UserIdBidRequestType convertToEntityAttribute(Integer value) {
    return UserIdBidRequestType.valueOf(value);
  }
}

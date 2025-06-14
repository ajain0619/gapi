package com.nexage.admin.core.custom;

import com.nexage.admin.core.enums.MediaType;
import javax.persistence.AttributeConverter;

public class MediaTypeConverter implements AttributeConverter<MediaType, String> {

  @Override
  public String convertToDatabaseColumn(MediaType mediaType) {
    if (mediaType == null) {
      return null;
    }

    return mediaType.getValue();
  }

  @Override
  public MediaType convertToEntityAttribute(String str) {
    if (str == null) {
      return null;
    }

    return MediaType.of(str);
  }
}

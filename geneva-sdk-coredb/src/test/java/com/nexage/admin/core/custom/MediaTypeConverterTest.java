package com.nexage.admin.core.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.enums.MediaType;
import org.junit.jupiter.api.Test;

class MediaTypeConverterTest {

  private static final String TEST_INPUT = "video/mp4";
  private MediaTypeConverter converter = new MediaTypeConverter();

  @Test
  void shouldConvertToString() throws Exception {
    String out = converter.convertToDatabaseColumn(MediaType.VIDEO_MP4);
    assertEquals(TEST_INPUT, out);
  }

  @Test
  void shouldConvertToMediaType() throws Exception {
    MediaType out = converter.convertToEntityAttribute(TEST_INPUT);
    assertEquals(TEST_INPUT, out.getValue());
  }

  @Test
  void shouldReturnNullWhenMediaTypeIsInvalid() {
    MediaType mockObject = mock(MediaType.class);

    String out = converter.convertToDatabaseColumn(mockObject);
    assertNull(out);
  }

  @Test
  void shouldReturnNullWhenMediaTypeIsNull() {
    String out = converter.convertToDatabaseColumn(null);
    assertNull(out);
  }

  @Test
  void shouldReturnNullWhenStringIsNull() {
    MediaType out = converter.convertToEntityAttribute(null);
    assertNull(out);
  }
}

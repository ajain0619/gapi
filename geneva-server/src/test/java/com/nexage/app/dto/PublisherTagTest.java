package com.nexage.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.app.dto.publisher.PublisherTagDTO;
import org.junit.jupiter.api.Test;

class PublisherTagTest {

  @Test
  void testAdNetPassword() {
    var testValue = "my password";
    var tag = new PublisherTagDTO();
    tag.setAdNetReportPassword(testValue);

    assertEquals(testValue, tag.getAdNetReportPassword());
  }

  @Test
  void getAdNetPasswordNotSet() {
    var tag = new PublisherTagDTO();
    assertNull(tag.getAdNetReportPassword());
  }
}

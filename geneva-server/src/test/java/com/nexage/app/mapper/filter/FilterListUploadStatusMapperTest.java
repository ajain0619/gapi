package com.nexage.app.mapper.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.app.dto.filter.FilterListUploadStatusDTO;
import com.nexage.app.mapper.FilterListUploadStatusMapper;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FilterListUploadStatusMapperTest {
  private FilterListUploadStatusMapper filterListUploadStatusMapper =
      FilterListUploadStatusMapper.MAPPER;

  private static Stream<Arguments> statusesAndDtosProvider() {
    return Stream.of(
        Arguments.of(FilterListUploadStatus.READY, FilterListUploadStatusDTO.READY),
        Arguments.of(FilterListUploadStatus.PENDING, FilterListUploadStatusDTO.PENDING),
        Arguments.of(FilterListUploadStatus.ERROR, FilterListUploadStatusDTO.ERROR));
  }

  @ParameterizedTest
  @MethodSource("statusesAndDtosProvider")
  void shouldCorrectlyMapAllStatuses(
      FilterListUploadStatus status, FilterListUploadStatusDTO statusDto) {
    assertEquals(status, filterListUploadStatusMapper.map(statusDto));
    assertEquals(statusDto, filterListUploadStatusMapper.map(status));
  }
}

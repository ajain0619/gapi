package com.nexage.app.mapper;

import com.nexage.admin.core.model.filter.FilterListUploadStatus;
import com.nexage.app.dto.filter.FilterListUploadStatusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FilterListUploadStatusMapper {
  FilterListUploadStatusMapper MAPPER = Mappers.getMapper(FilterListUploadStatusMapper.class);

  FilterListUploadStatus map(FilterListUploadStatusDTO filterListUploadStatusDTO);

  FilterListUploadStatusDTO map(FilterListUploadStatus filterListUploadStatus);
}

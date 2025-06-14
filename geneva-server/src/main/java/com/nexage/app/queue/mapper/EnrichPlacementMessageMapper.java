package com.nexage.app.queue.mapper;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.PositionBuyer;
import com.nexage.app.queue.model.EnrichPlacementCommandMessage;
import com.nexage.app.queue.model.EnrichPlacementResultMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnrichPlacementMessageMapper {

  EnrichPlacementMessageMapper MAPPER = Mappers.getMapper(EnrichPlacementMessageMapper.class);

  @Mapping(source = "pid", target = "placementPid")
  @Mapping(source = "site.pid", target = "sitePid")
  @Mapping(source = "memo", target = "name")
  EnrichPlacementCommandMessage map(Position position);

  @Mapping(source = "placementPid", target = "positionPid")
  @Mapping(source = "sectionPid", target = "buyerPositionId")
  @Mapping(target = "version", constant = "1")
  PositionBuyer map(EnrichPlacementResultMessage message);
}

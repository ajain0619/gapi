package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.repository.PlacementVideoCompanionRepository;
import com.nexage.admin.core.repository.PlacementVideoPlaylistRepository;
import com.nexage.admin.core.repository.PlacementVideoRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoPlaylist;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.seller.PlacementCommonDTO;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.site.PlacementVideoDTOMapper;
import com.nexage.app.mapper.site.PlacementVideoPlaylistDTOMapper;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.PlacementVideoService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@Log4j2
@Transactional(readOnly = true)
public class PlacementVideoServiceImpl implements PlacementVideoService {

  private final PlacementVideoRepository placementVideoRepository;
  private final PlacementVideoCompanionRepository placementVideoCompanionRepository;
  private final PositionViewRepository positionViewRepository;
  private final PlacementVideoPlaylistRepository placementVideoPlaylistRepository;
  private final BeanValidationService beanValidationService;

  public PlacementVideoServiceImpl(
      PlacementVideoRepository placementVideoRepository,
      PlacementVideoCompanionRepository placementVideoCompanionRepository,
      PositionViewRepository positionViewRepository,
      PlacementVideoPlaylistRepository placementVideoPlaylistRepository,
      BeanValidationService beanValidationService) {
    this.placementVideoRepository = placementVideoRepository;
    this.placementVideoCompanionRepository = placementVideoCompanionRepository;
    this.positionViewRepository = positionViewRepository;
    this.placementVideoPlaylistRepository = placementVideoPlaylistRepository;
    this.beanValidationService = beanValidationService;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public PlacementVideoDTO save(PlacementVideoDTO placementVideoDTO, Long positionPid) {
    if (Objects.isNull(placementVideoDTO) || Objects.isNull(positionPid)) return null;

    beanValidationService.validate(placementVideoDTO, Default.class, CreateGroup.class);

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    placementVideo.setPosition(
        positionViewRepository.findById(positionPid).stream().findFirst().orElse(null));

    PlacementVideo savedPlacementVideo = placementVideoRepository.save(placementVideo);

    if (!Objects.isNull(placementVideoDTO.getDapPlayerType())) {
      List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTOS =
          savePlaylistInfo(placementVideoDTO, savedPlacementVideo);
      return PlacementVideoDTOMapper.MAPPER.map(savedPlacementVideo, placementVideoPlaylistDTOS);
    }

    return PlacementVideoDTOMapper.MAPPER.map(savedPlacementVideo);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public PlacementVideoDTO update(PlacementVideoDTO placementVideoDTO, Long positionPid) {
    if (Objects.isNull(placementVideoDTO) || Objects.isNull(positionPid)) return null;

    if (Objects.isNull(placementVideoDTO.getPid())
        || 0 != positionPid.compareTo(placementVideoDTO.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_PLACEMENT_VIDEO);
    }

    beanValidationService.validate(placementVideoDTO, Default.class, UpdateGroup.class);

    Optional<PositionView> positionView =
        positionViewRepository.findById(positionPid).stream().findFirst();
    if (positionView.isEmpty()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_EXISTS);
    }

    if (!placementVideoRepository.existsByPid(placementVideoDTO.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PLACEMENT_VIDEO_NOT_FOUND);
    }

    PlacementVideo placementVideo = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    placementVideo.setPosition(positionView.get());
    deleteExtraCompanions(placementVideoDTO);
    PlacementVideo savedPlacementVideo = placementVideoRepository.saveAndFlush(placementVideo);

    if (!Objects.isNull(placementVideoDTO.getDapPlayerType())) {
      List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTOS =
          updatePlaylistInfo(placementVideoDTO, savedPlacementVideo);
      return PlacementVideoDTOMapper.MAPPER.map(savedPlacementVideo, placementVideoPlaylistDTOS);
    }

    return PlacementVideoDTOMapper.MAPPER.map(savedPlacementVideo);
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public PlacementVideoDTO update(
      PlacementVideoDTO placementVideoDTO, Long positionPid, boolean insertPlacementVideo) {
    PlacementVideoDTO savedPlacementVideoDTO = null;
    Optional<PlacementVideo> placementVideo = placementVideoRepository.findById(positionPid);
    if (placementVideo.isPresent()) {
      if (Objects.isNull(placementVideoDTO.getPid())) {
        placementVideoDTO.setPid(placementVideo.get().getPid());
        placementVideoDTO.setVersion(placementVideo.get().getVersion());
      }
      savedPlacementVideoDTO = update(placementVideoDTO, positionPid);
    } else if (insertPlacementVideo) {
      savedPlacementVideoDTO = save(placementVideoDTO, positionPid);
    }
    return savedPlacementVideoDTO;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(PlacementVideoDTO placementVideoDTO) {
    if (Objects.isNull(placementVideoDTO)) return false;

    if (placementVideoDTO.getPlaylistInfo() != null) {
      for (PlacementVideoPlaylistDTO placementVideoPlaylistDTO :
          placementVideoDTO.getPlaylistInfo()) {
        if (placementVideoPlaylistDTO.getPid() != null
            && placementVideoPlaylistRepository.existsById(placementVideoPlaylistDTO.getPid())) {
          placementVideoPlaylistRepository.deleteById(placementVideoPlaylistDTO.getPid());
        }
      }
    }

    if (placementVideoRepository.existsByPid(placementVideoDTO.getPid())) {
      placementVideoRepository.deleteById(placementVideoDTO.getPid());
      return true;
    }

    return false;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public boolean delete(Long placementVideoPid) {
    if (Objects.isNull(placementVideoPid)) return false;

    if (placementVideoRepository.existsByPid(placementVideoPid)) {
      placementVideoRepository.deleteById(placementVideoPid);
      return true;
    }

    return false;
  }

  /** {@inheritDoc} */
  @Override
  public PlacementVideoDTO populateVideoData(
      PlacementVideoDTO placementVideoDTO, PlacementCommonDTO publisherPosition) {
    if (Objects.isNull(placementVideoDTO)) {
      placementVideoDTO =
          PlacementVideoDTOMapper.MAPPER.populatePlacementVideoDTOFromPosition(publisherPosition);
    } else {
      PlacementVideoDTOMapper.MAPPER.populatePositionVideoFieldsFromPlacementVideoDTO(
          publisherPosition);
    }
    return placementVideoDTO;
  }

  /** {@inheritDoc} */
  @Override
  public PlacementVideoDTO getPlacementVideo(Long pid) {
    return PlacementVideoDTOMapper.MAPPER.map(placementVideoRepository.findById(pid).orElse(null));
  }

  /** {@inheritDoc} */
  @Override
  public List<PlacementVideoPlaylistDTO> getPlaylistInfo(PlacementVideo placementVideo) {
    List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTOS = new ArrayList<>();

    for (PlacementVideoPlaylist placementVideoPlaylist :
        placementVideoPlaylistRepository.findAllByPlacementVideoPid(placementVideo)) {
      placementVideoPlaylistDTOS.add(
          PlacementVideoPlaylistDTOMapper.MAPPER.map(placementVideoPlaylist));
    }

    return placementVideoPlaylistDTOS;
  }

  private void deleteExtraCompanions(PlacementVideoDTO placementVideoDTO) {
    List<PlacementVideoCompanionDTO> placementVideoDTOCompanions =
        placementVideoDTO.getCompanions();
    Set<Long> placementVideoDTOCompanionsSet = new HashSet<>();
    if (Objects.nonNull(placementVideoDTOCompanions) && !placementVideoDTOCompanions.isEmpty()) {
      placementVideoDTOCompanionsSet =
          placementVideoDTOCompanions.stream()
              .map(PlacementVideoCompanionDTO::getPid)
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());
    }
    if (!placementVideoDTOCompanionsSet.isEmpty()) {
      placementVideoCompanionRepository.delete(
          placementVideoDTO.getPid(), placementVideoDTOCompanionsSet);
    } else {
      placementVideoCompanionRepository.delete(placementVideoDTO.getPid());
    }
  }

  private List<PlacementVideoPlaylistDTO> savePlaylistInfo(
      PlacementVideoDTO placementVideoDTO, PlacementVideo placementVideo) {
    List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTOS = new ArrayList<>();
    if (placementVideoDTO.getDapPlayerType().equals(DapPlayerType.YVAP)) {
      for (PlacementVideoPlaylistDTO placementVideoPlaylistDTO :
          placementVideoDTO.getPlaylistInfo()) {
        PlacementVideoPlaylist placementVideoPlaylist =
            PlacementVideoPlaylistDTOMapper.MAPPER.map(placementVideoPlaylistDTO, placementVideo);
        placementVideoPlaylistRepository.save(placementVideoPlaylist);
        placementVideoPlaylistDTOS.add(
            PlacementVideoPlaylistDTOMapper.MAPPER.map(placementVideoPlaylist));
      }
    }

    return placementVideoPlaylistDTOS;
  }

  private List<PlacementVideoPlaylistDTO> updatePlaylistInfo(
      PlacementVideoDTO placementVideoDTO, PlacementVideo placementVideo) {
    List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTOS = new ArrayList<>();

    List<PlacementVideoPlaylist> placementVideoPlaylists =
        placementVideoPlaylistRepository.findAllByPlacementVideoPid(placementVideo);

    if (placementVideoDTO.getDapPlayerType().equals(DapPlayerType.YVAP)) {
      if (placementVideoPlaylists.isEmpty()) {
        return savePlaylistInfo(placementVideoDTO, placementVideo);
      } else {
        placementVideoDTO
            .getPlaylistInfo()
            .forEach(
                placementVideoPlaylistDTO ->
                    placementVideoPlaylists.stream()
                        .filter(
                            placementVideoPlaylist ->
                                placementVideoPlaylist
                                    .getPid()
                                    .equals(placementVideoPlaylistDTO.getPid()))
                        .forEach(
                            placementVideoPlaylist -> {
                              placementVideoPlaylist.setFallbackURL(
                                  placementVideoPlaylistDTO.getFallbackURL());
                              placementVideoPlaylistRepository.save(placementVideoPlaylist);
                              placementVideoPlaylistDTOS.add(
                                  PlacementVideoPlaylistDTOMapper.MAPPER.map(
                                      placementVideoPlaylist));
                            }));
      }
    } else {
      for (PlacementVideoPlaylist placementVideoPlaylist : placementVideoPlaylists) {
        if (placementVideoPlaylist.getPid() != null
            && placementVideoPlaylistRepository.existsById(placementVideoPlaylist.getPid())) {
          placementVideoPlaylistRepository.deleteById(placementVideoPlaylist.getPid());
        }
      }
    }

    return placementVideoPlaylistDTOS;
  }
}

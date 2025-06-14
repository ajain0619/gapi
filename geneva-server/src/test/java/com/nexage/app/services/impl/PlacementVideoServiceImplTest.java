package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.ScreenLocation;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.PlacementVideoCompanionRepository;
import com.nexage.admin.core.repository.PlacementVideoPlaylistRepository;
import com.nexage.admin.core.repository.PlacementVideoRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideoPlaylist;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.seller.PlacementDTO;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.dto.seller.PlacementVideoPlaylistDTO;
import com.nexage.app.dto.seller.SiteDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.mapper.site.PlacementDTOMapper;
import com.nexage.app.mapper.site.PlacementVideoDTOMapper;
import com.nexage.app.mapper.site.PlacementVideoPlaylistDTOMapper;
import com.nexage.app.mapper.site.SiteDTOMapper;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.groups.Default;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlacementVideoServiceImplTest {

  private static final Long DEFAULT_PID = 123L;

  @Mock private PlacementVideoRepository placementVideoRepository;
  @Mock private PositionViewRepository positionViewRepository;
  @Mock private PlacementVideoCompanionRepository placementVideoCompanionRepository;
  @Mock private PlacementVideoPlaylistRepository placementVideoPlaylistRepository;
  @Mock private BeanValidationService beanValidationService;
  @InjectMocks private PlacementVideoServiceImpl placementVideoService;

  @Test
  void shouldSavePlacementVideoTest() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    List<PlacementVideoCompanionDTO> placementVideoCompanionDTOList = new ArrayList<>();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTOList.add(placementVideoCompanionDTO);
    // PlacementVideoDTO = null and position = null
    assertNull(placementVideoService.save(null, null));

    // PlacementVideoDTO = null and position = not null
    assertNull(placementVideoService.save(null, position.getPid()));

    // PlacementVideoDTO = not null and position =  null
    assertNull(placementVideoService.save(placementVideoDTO, null));

    // PlacementVideoDTO = not null and position = not null and no companion
    assertNotNull(placementVideoService.save(placementVideoDTO, position.getPid()));

    // with one companion
    placementVideoDTO.setCompanions(placementVideoCompanionDTOList);
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(1, savedPlacementVideoDTO.getCompanions().size());

    // with multiple companions
    PlacementVideoCompanionDTO placementVideoCompanionDTO2 =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTOList.add(placementVideoCompanionDTO2);
    placementVideoDTO.setCompanions(placementVideoCompanionDTOList);
    savedPlacementVideoDTO = placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(2, savedPlacementVideoDTO.getCompanions().size());
  }

  @Test
  void shouldSavePlacementVideoTestWiithYvapInfo() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultPlacementVideoDTOWithPlaylistInfo();
    List<PlacementVideoCompanionDTO> placementVideoCompanionDTOList = new ArrayList<>();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTOList.add(placementVideoCompanionDTO);
    // PlacementVideoDTO = null and position = null
    assertNull(placementVideoService.save(null, null));

    // PlacementVideoDTO = null and position = not null
    assertNull(placementVideoService.save(null, position.getPid()));

    // PlacementVideoDTO = not null and position =  null
    assertNull(placementVideoService.save(placementVideoDTO, null));

    // PlacementVideoDTO = not null and position = not null and no companion
    assertNotNull(placementVideoService.save(placementVideoDTO, position.getPid()));

    // with one companion
    placementVideoDTO.setCompanions(placementVideoCompanionDTOList);
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(1, savedPlacementVideoDTO.getCompanions().size());
    assertEquals(DapPlayerType.YVAP, savedPlacementVideoDTO.getDapPlayerType());
    assertEquals(1, savedPlacementVideoDTO.getPlaylistInfo().size());
  }

  @Test
  void shouldSavePlacementVideoWhenLinearityIsNull() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    PlacementVideoDTO savedPlacementVideo =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertEquals(PlacementVideoLinearity.NON_LINEAR, savedPlacementVideo.getLinearity());

    placementVideoDTO.setLinearity(null);
    savedPlacementVideo = placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertEquals(PlacementVideoLinearity.LINEAR, savedPlacementVideo.getLinearity());
  }

  @Test
  void shouldNotSaveInvalidPlacementVideo() {
    Position position = createPosition();

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(placementVideoDTO, Default.class, CreateGroup.class);

    // PlacementVideoDTO = not null and position = not null and no companion
    Long pid = position.getPid();
    assertNotNull(pid);
    assertThrows(
        EntityConstraintViolationException.class,
        () -> placementVideoService.save(placementVideoDTO, pid));
  }

  @Test
  void getPlacementVideoTest() {
    Position position = createPosition();
    when(placementVideoRepository.findById(position.getPid())).thenReturn(Optional.empty());
    assertNull(placementVideoService.getPlacementVideo(position.getPid()));
    assertNull(placementVideoService.getPlacementVideo(null));

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(1234L);
    PlacementVideo placementVideo1 = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    when(placementVideoRepository.findById(any())).thenReturn(Optional.of(placementVideo1));

    assertEquals(
        placementVideoDTO, placementVideoService.getPlacementVideo(placementVideo1.getPid()));

    PlacementVideoDTO placementVideoDTO2 = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO2.setPid(234L);
    PlacementVideo placementVideo2 = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO2);
    when(placementVideoRepository.findById(any())).thenReturn(Optional.empty());
    assertNull(placementVideoService.getPlacementVideo(placementVideo2.getPid()));
  }

  @Test
  void getPlaylistInfoTest() {
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();

    PlacementVideoPlaylist placementVideoPlaylist =
        TestObjectsFactory.createDefaultPlacementVideoPlaylist(placementVideo);
    PlacementVideoPlaylistDTO placementVideoPlaylistDTO =
        PlacementVideoPlaylistDTOMapper.MAPPER.map(placementVideoPlaylist);
    List<PlacementVideoPlaylistDTO> placementVideoPlaylistDTOS = new ArrayList<>();
    placementVideoPlaylistDTOS.add(placementVideoPlaylistDTO);

    when(placementVideoPlaylistRepository.findAllByPlacementVideoPid(placementVideo))
        .thenReturn(Arrays.asList(placementVideoPlaylist));

    List<PlacementVideoPlaylistDTO> actual = placementVideoService.getPlaylistInfo(placementVideo);

    assertNotNull(actual);
    assertEquals(actual.get(0).getPid(), placementVideoPlaylistDTOS.get(0).getPid());
  }

  @Test
  void shouldSavePlacementVideoWhenValidLongformFields() {
    Position position = createPosition();
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    placementVideoDTO.setPlayerBrand("test_player");
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_CLIENT_SIDE);
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideoDTO);
    assertTrue(savedPlacementVideoDTO.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, savedPlacementVideoDTO.getStreamType());
    assertEquals("test_player", savedPlacementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ALL_CLIENT_SIDE, savedPlacementVideoDTO.getSsai());
  }

  @Test
  void shouldSavePlacementVideoWhenLongformFalse() {
    Position position = createPosition();
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(false);
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideoDTO);
    assertFalse(savedPlacementVideoDTO.isLongform());
    assertNull(savedPlacementVideoDTO.getStreamType());
    assertNull(savedPlacementVideoDTO.getPlayerBrand());
    assertNull(savedPlacementVideoDTO.getSsai());
  }

  @Test
  void shouldNotUpdateInvalidPlacementVideo() {
    Position position = createPosition();

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(position.getPid());
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(placementVideoDTO, Default.class, UpdateGroup.class);

    Long pid = position.getPid();
    assertNotNull(pid);
    assertThrows(
        EntityConstraintViolationException.class,
        () -> placementVideoService.update(placementVideoDTO, pid));
  }

  @Test
  @SneakyThrows
  void shouldUpdateValidPlacementVideo() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    PlacementVideoDTO savedPlacementVideo =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertEquals(PlacementVideoLinearity.NON_LINEAR, savedPlacementVideo.getLinearity());

    savedPlacementVideo.setPid(position.getPid());
    savedPlacementVideo.setLinearity(PlacementVideoLinearity.LINEAR);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(position.getPid());
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertEquals(PlacementVideoLinearity.LINEAR, savedPlacementVideo.getLinearity());
  }

  @Test
  void shouldRemovePlaylistInfoWhenPlacementIsNotYVAP() {
    Long pid = 123L;
    Position position = createPosition();
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PlacementVideo placementVideoNotYVAP = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideoNotYVAP.setPid(pid);
    placementVideoNotYVAP.setVersion(1);
    PlacementVideoPlaylist placementVideoPlaylistNotYVAP =
        TestObjectsFactory.createDefaultPlacementVideoPlaylist(placementVideoNotYVAP);
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultPlacementVideoDTOWithPlaylistInfo();
    placementVideoDTO.setDapPlayerType(DapPlayerType.YAHOO);

    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    when(placementVideoRepository.findById(any())).thenReturn(Optional.of(placementVideoNotYVAP));
    when(placementVideoPlaylistRepository.findAllByPlacementVideoPid(any()))
        .thenReturn(Arrays.asList(placementVideoPlaylistNotYVAP));
    when(placementVideoPlaylistRepository.existsById(any())).thenReturn(true);

    placementVideoDTO.setPid(123L);
    placementVideoDTO.getPlaylistInfo().get(0).setFallbackURL("newurl.mp4");
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.update(placementVideoDTO, pid, true);

    assertNotNull(savedPlacementVideoDTO);
    assertEquals(123L, savedPlacementVideoDTO.getPid());
    assertEquals(null, savedPlacementVideoDTO.getPlaylistInfo());
    verify(placementVideoPlaylistRepository, atLeastOnce()).deleteById(any());
  }

  @Test
  void shouldUpdateVideoPlacementWithPlaylistInfo() {
    Position position = createPosition();
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultPlacementVideoDTOWithPlaylistInfo();
    placementVideoDTO.getPlaylistInfo().get(0).setFallbackURL("newurl.mp4");
    Long pid = 123L;
    when(placementVideoRepository.existsByPid(any())).thenReturn(false);

    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(pid);
    PlacementVideoPlaylist placementVideoPlaylist =
        TestObjectsFactory.createDefaultPlacementVideoPlaylist(placementVideo);
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    when(placementVideoRepository.findById(any())).thenReturn(Optional.of(placementVideo));
    when(placementVideoPlaylistRepository.findAllByPlacementVideoPid(any()))
        .thenReturn(Arrays.asList(placementVideoPlaylist));
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.update(placementVideoDTO, pid, true);
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(pid, savedPlacementVideoDTO.getPid());
    assertEquals("newurl.mp4", savedPlacementVideoDTO.getPlaylistInfo().get(0).getFallbackURL());
  }

  @Test
  void shouldSavePlaylistInfoWhenVideoPlacementHasNoPlaylistInfo() {
    Position position = createPosition();
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultPlacementVideoDTOWithPlaylistInfo();
    Long pid = 123L;

    PlacementVideo placementVideoNoPlaylist = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideoNoPlaylist.setPid(pid);
    placementVideoNoPlaylist.setVersion(1);
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    when(placementVideoRepository.findById(any()))
        .thenReturn(Optional.of(placementVideoNoPlaylist));
    when(placementVideoPlaylistRepository.findAllByPlacementVideoPid(any()))
        .thenReturn(Collections.emptyList());
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.update(placementVideoDTO, pid, true);
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(pid, savedPlacementVideoDTO.getPid());
    assertEquals("someurl.mp4", savedPlacementVideoDTO.getPlaylistInfo().get(0).getFallbackURL());
  }

  @Test
  void shouldNotUpdatePlacementVideoIfPositionPidIsDiff() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    Long positionPid = position.getPid();
    PlacementVideoDTO savedPlacementVideo =
        placementVideoService.save(placementVideoDTO, positionPid);
    assertNotNull(savedPlacementVideo);
    PlacementVideoLinearity linearity = savedPlacementVideo.getLinearity();
    assertEquals(PlacementVideoLinearity.NON_LINEAR, linearity);
    assertThrows(
        GenevaValidationException.class,
        () -> placementVideoService.update(savedPlacementVideo, positionPid));

    savedPlacementVideo.setPid(positionPid);
    assertThrows(
        GenevaValidationException.class,
        () -> placementVideoService.update(savedPlacementVideo, positionPid + 1));

    savedPlacementVideo.setPid(positionPid + 1);
    assertThrows(
        GenevaValidationException.class,
        () -> placementVideoService.update(savedPlacementVideo, positionPid));

    savedPlacementVideo.setPid(positionPid);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(positionPid);
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    assertNotNull(placementVideoService.update(savedPlacementVideo, positionPid));

    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.empty());
    assertThrows(
        GenevaValidationException.class,
        () -> placementVideoService.update(savedPlacementVideo, positionPid));

    when(placementVideoRepository.existsByPid(any())).thenReturn(false);
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    assertThrows(
        GenevaValidationException.class,
        () -> placementVideoService.update(savedPlacementVideo, positionPid));
  }

  @Test
  void shouldNotUpdatePlacementVideo() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    Long positionPid = position.getPid();
    PlacementVideoDTO savedPlacementVideo =
        placementVideoService.save(placementVideoDTO, positionPid);
    assertNotNull(savedPlacementVideo);
    PlacementVideoLinearity linearity = savedPlacementVideo.getLinearity();
    assertEquals(PlacementVideoLinearity.NON_LINEAR, linearity);

    savedPlacementVideo.setPid(positionPid);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(positionPid);
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);

    assertNull(placementVideoService.update(null, null));
    assertNull(placementVideoService.update(savedPlacementVideo, null));
    assertNull(placementVideoService.update(null, positionPid));
    assertNotNull(placementVideoService.update(savedPlacementVideo, positionPid));
  }

  @Test
  @SneakyThrows
  void shouldUpdateCompanionsWithPlacementVideo() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTO.setPid(10L);
    PlacementVideoCompanionDTO placementVideoCompanionDTO2 =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTO2.setPid(11L);
    PlacementVideoCompanionDTO placementVideoCompanionDTO3 =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoCompanionDTO3.setPid(12L);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    placementVideoDTO.addCompanion(placementVideoCompanionDTO2);
    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    PlacementVideoDTO savedPlacementVideo =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertEquals(PlacementVideoLinearity.NON_LINEAR, savedPlacementVideo.getLinearity());
    List<PlacementVideoCompanionDTO> savedCompanions = savedPlacementVideo.getCompanions();
    assertNotNull(savedCompanions);
    assertEquals(2, savedCompanions.size());

    savedPlacementVideo.setPid(position.getPid());
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(position.getPid());
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);

    // Add Companion
    savedPlacementVideo.addCompanion(placementVideoCompanionDTO3);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    savedCompanions = savedPlacementVideo.getCompanions();
    assertNotNull(savedCompanions);
    assertEquals(3, savedCompanions.size());

    // Remove Companion
    savedPlacementVideo.removeCompanion(placementVideoCompanionDTO2);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    savedCompanions = savedPlacementVideo.getCompanions();
    assertNotNull(savedCompanions);
    assertEquals(2, savedCompanions.size());

    // Update companion
    savedCompanions.get(0).setHeight(100);
    savedPlacementVideo.setCompanions(savedCompanions);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    savedCompanions = savedPlacementVideo.getCompanions();
    assertNotNull(savedCompanions);
    assertEquals(2, savedCompanions.size());
    assertEquals((Integer) 100, savedCompanions.get(0).getHeight());

    // remove one comapanion and add one another
    savedPlacementVideo.removeCompanion(placementVideoCompanionDTO3);
    placementVideoCompanionDTO2.setPid(null);
    savedPlacementVideo.addCompanion(placementVideoCompanionDTO2);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    savedCompanions = savedPlacementVideo.getCompanions();
    assertNotNull(savedCompanions);
    assertEquals(2, savedCompanions.size());

    // remove all companions
    savedPlacementVideo.setCompanions(null);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    savedCompanions = savedPlacementVideo.getCompanions();
    assertNull(savedCompanions);

    List<PlacementVideoCompanionDTO> companionsDTO = new ArrayList<>();
    savedPlacementVideo.setCompanions(companionsDTO);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    savedCompanions = savedPlacementVideo.getCompanions();
    assertNotNull(savedCompanions);
    assertEquals(0, savedCompanions.size());
  }

  @Test
  void shouldDeletePlacementVideo() {
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);

    assertFalse(placementVideoService.delete((PlacementVideoDTO) null));
    assertTrue(placementVideoService.delete(1L));

    when(placementVideoRepository.existsByPid(any())).thenReturn(false);
    assertFalse(placementVideoService.delete(1L));
  }

  @Test
  void shouldDeletePlacementVideoWithPlaylistInfo() {
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultPlacementVideoDTOWithPlaylistInfo();

    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    when(placementVideoPlaylistRepository.existsById(any())).thenReturn(true);

    assertFalse(placementVideoService.delete((PlacementVideoDTO) null));
    assertTrue(placementVideoService.delete(placementVideoDTO));

    assertFalse(placementVideoService.delete((PlacementVideoDTO) null));
    assertTrue(placementVideoService.delete(1L));

    when(placementVideoRepository.existsByPid(any())).thenReturn(false);
    assertFalse(placementVideoService.delete(1L));
  }

  @Test
  void shouldPopulateVideoData() {
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementDTO placementDTO = TestObjectsFactory.createPlacements(1).get(0);
    placementDTO.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    placementDTO.setVideoLinearity(VideoLinearity.LINEAR);

    PlacementVideoDTO returnedPlacementVideoDTO =
        placementVideoService.populateVideoData(null, placementDTO);
    assertNotNull(returnedPlacementVideoDTO);

    placementDTO.setPlacementVideo(placementVideoDTO);
    returnedPlacementVideoDTO =
        placementVideoService.populateVideoData(placementVideoDTO, placementDTO);
    assertFalse(returnedPlacementVideoDTO.isLongform());
    assertNull(returnedPlacementVideoDTO.getStreamType());
    assertNull(returnedPlacementVideoDTO.getPlayerBrand());
    assertNull(returnedPlacementVideoDTO.getSsai());
  }

  @Test
  void shouldUpdateVideoPlacement() {
    Position position = createPosition();
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    Long pid = 123L;
    when(placementVideoRepository.existsByPid(any())).thenReturn(false);
    PlacementVideoDTO savedPlacementVideoDTO =
        placementVideoService.update(placementVideoDTO, pid, false);
    assertNull(savedPlacementVideoDTO);

    savedPlacementVideoDTO = placementVideoService.update(placementVideoDTO, pid, true);
    assertNotNull(savedPlacementVideoDTO);

    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(pid);
    placementVideo.setVersion(1);
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    when(placementVideoRepository.findById(any())).thenReturn(Optional.of(placementVideo));
    savedPlacementVideoDTO = placementVideoService.update(placementVideoDTO, pid, true);
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(pid, savedPlacementVideoDTO.getPid());

    placementVideoDTO.setPid(123L);
    savedPlacementVideoDTO = placementVideoService.update(placementVideoDTO, pid, true);
    assertNotNull(savedPlacementVideoDTO);
    assertEquals(123L, savedPlacementVideoDTO.getPid());
    assertEquals(1, savedPlacementVideoDTO.getVersion());
  }

  @Test
  void shouldNotUpdateInvalidLongformPlacementVideo() {
    Position position = createPosition();

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setStreamType(null);

    Long pid = position.getPid();
    assertNotNull(pid);
    assertThrows(
        GenevaValidationException.class,
        () -> placementVideoService.update(placementVideoDTO, pid));
  }

  @Test
  @SneakyThrows
  void shouldUpdateValidLongformPlacementVideo() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    PlacementVideoDTO savedPlacementVideo =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertEquals(PlacementVideoSsai.ALL_SERVER_SIDE, savedPlacementVideo.getSsai());

    savedPlacementVideo.setPid(position.getPid());
    savedPlacementVideo.setSsai(PlacementVideoSsai.ALL_CLIENT_SIDE);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(position.getPid());
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertEquals(PlacementVideoSsai.ALL_CLIENT_SIDE, savedPlacementVideo.getSsai());
  }

  @Test
  void shouldGetPlacementVideoWithMultiBiddingFields() {
    Position position = createPosition();
    when(placementVideoRepository.findById(position.getPid())).thenReturn(Optional.empty());
    assertNull(placementVideoService.getPlacementVideo(position.getPid()));
    assertNull(placementVideoService.getPlacementVideo(null));

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setPid(1234L);
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(true);
    PlacementVideo placementVideo1 = PlacementVideoDTOMapper.MAPPER.map(placementVideoDTO);
    when(placementVideoRepository.findById(any())).thenReturn(Optional.of(placementVideo1));

    PlacementVideoDTO fetchedPlacementVideoDTO =
        placementVideoService.getPlacementVideo(placementVideo1.getPid());
    assertNotNull(fetchedPlacementVideoDTO);
    assertTrue(fetchedPlacementVideoDTO.isMultiImpressionBid());
    assertTrue(fetchedPlacementVideoDTO.isCompetitiveSeparation());
  }

  @Test
  void shouldSaveAndUpdatePlacementVideoWithMultiBiddingFields() {
    Position position = createPosition();
    when(placementVideoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(placementVideoRepository.saveAndFlush(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PositionView positionView =
        new PositionView(
            position.getPid(),
            position.getName(),
            position.getMemo(),
            position.getVersion(),
            position.getSitePid());
    when(positionViewRepository.findById(position.getPid())).thenReturn(Optional.of(positionView));
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(true);
    PlacementVideoDTO savedPlacementVideo =
        placementVideoService.save(placementVideoDTO, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertTrue(savedPlacementVideo.isMultiImpressionBid());
    assertTrue(savedPlacementVideo.isCompetitiveSeparation());

    savedPlacementVideo.setPid(position.getPid());
    savedPlacementVideo.setCompetitiveSeparation(false);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(position.getPid());
    when(placementVideoRepository.existsByPid(any())).thenReturn(true);
    savedPlacementVideo = placementVideoService.update(savedPlacementVideo, position.getPid());
    assertNotNull(savedPlacementVideo);
    assertTrue(savedPlacementVideo.isMultiImpressionBid());
    assertFalse(savedPlacementVideo.isCompetitiveSeparation());
  }

  private Site createSite() {
    Site site = new Site();
    site.setPid(DEFAULT_PID);
    site.setType(Type.DESKTOP);
    site.setStatus(Status.ACTIVE);
    site.setCompanyPid(DEFAULT_PID);
    return site;
  }

  private PlacementDTO createPlacementDTO() {
    SiteDTO siteDTO = new SiteDTO();
    siteDTO.setPid(DEFAULT_PID);
    siteDTO.setType(Type.DESKTOP);
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPid(DEFAULT_PID);
    placementDTO.setWidth(100);
    placementDTO.setHeight(100);
    placementDTO.setName("foo");
    placementDTO.setMemo("foo");
    placementDTO.setVideoSupport(VideoSupport.BANNER);
    placementDTO.setScreenLocation(ScreenLocation.ABOVE_VISIBLE);
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setSite(siteDTO);
    return placementDTO;
  }

  private Position createPosition() {
    Site site = createSite();

    PlacementDTO placementDTO = createPlacementDTO();
    placementDTO.setName(null);
    placementDTO.setSite(SiteDTOMapper.MAPPER.map(site));
    return PlacementDTOMapper.MAPPER.map(placementDTO);
  }
}

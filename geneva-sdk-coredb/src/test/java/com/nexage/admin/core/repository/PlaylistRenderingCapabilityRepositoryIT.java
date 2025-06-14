package com.nexage.admin.core.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.PlaylistRenderingCapability;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "/data/repository/playlist-rendering-capability-repository.sql")
@Transactional
class PlaylistRenderingCapabilityRepositoryIT extends CoreDbSdkIntegrationTestBase {

  @Autowired private PlaylistRenderingCapabilityRepository repository;

  @Test
  void shouldThrowExceptionWhenPersistAttempted() {
    var playlistRenderingCapability = new PlaylistRenderingCapability();
    playlistRenderingCapability.setPid(100L);

    var ex =
        assertThrows(
            UnsupportedOperationException.class,
            () -> repository.saveAndFlush(playlistRenderingCapability));

    assertThat(ex.getMessage())
        .endsWith("PlaylistRenderingCapability entity does not support the persist operation");
  }

  @Test
  void shouldThrowExceptionWhenUpdateAttempted() {
    var playlistRenderingCapability = repository.findById(1L).orElseThrow(RuntimeException::new);
    playlistRenderingCapability.setDisplayValue("sdk capability display value updated");

    var ex =
        assertThrows(
            UnsupportedOperationException.class,
            () -> repository.saveAndFlush(playlistRenderingCapability));

    assertThat(ex.getMessage())
        .endsWith("PlaylistRenderingCapability entity does not support the update operation");
  }

  @Test
  void shouldThrowExceptionWhenDeleteAttempted() {
    var playlistRenderingCapability = repository.findById(1L).orElseThrow(RuntimeException::new);

    var ex =
        assertThrows(
            UnsupportedOperationException.class,
            () -> repository.delete(playlistRenderingCapability));

    assertThat(ex.getMessage())
        .endsWith("PlaylistRenderingCapability entity does not support the remove operation");
  }

  @Test
  void shouldReturnCountOfRowsWithGivenStatusAndValueInSet() {
    var inactiveCapability = "sdkcapability2";
    Set<String> values =
        Set.of("sdkcapability1", inactiveCapability, "sdkcapability8", "not_an_sdk_capability");

    var count = repository.countByStatusAndValueIn(Status.ACTIVE, values);

    assertEquals(2, count);
  }

  @Test
  void shouldReturnPageOfRowsWithGivenStatus() {
    var sort = Sort.by(Order.asc("displayValue"));
    var pageable = PageRequest.of(1, 3, sort);

    var playlistRenderingCapabilities =
        repository.findAllByStatus(pageable, Status.ACTIVE).toList();

    assertEquals(3, playlistRenderingCapabilities.size());
    assertEquals("display value 5", playlistRenderingCapabilities.get(0).getDisplayValue());
    assertEquals("display value 7", playlistRenderingCapabilities.get(1).getDisplayValue());
    assertEquals("display value 8", playlistRenderingCapabilities.get(2).getDisplayValue());
  }
}

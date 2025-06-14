package com.nexage.app.util.validator;

import static com.nexage.admin.core.enums.RuleTargetType.PLAYLIST_RENDERING_CAPABILITY;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.repository.PlaylistRenderingCapabilityRepository;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Data validator for {@link RuleTargetType#PLAYLIST_RENDERING_CAPABILITY} targets. */
@Component
public class RulePlaylistRenderingCapabilityValidation implements RuleTargetValidation {

  private final PlaylistRenderingCapabilityRepository playlistRenderingCapabilityRepository;

  RulePlaylistRenderingCapabilityValidation(
      PlaylistRenderingCapabilityRepository playlistRenderingCapabilityRepository) {
    this.playlistRenderingCapabilityRepository = playlistRenderingCapabilityRepository;
  }

  /**
   * Determines if the data for a {@link RuleTargetType#PLAYLIST_RENDERING_CAPABILITY} target is
   * valid.
   *
   * <p>The data is valid when it is a comma-separated string of active sdk_capability#value.
   *
   * @param data the data to validate
   * @return a {@code boolean} flag indicating if the data is valid
   */
  @Override
  public boolean isValid(String data) {
    var values = data.split(",");

    var count =
        playlistRenderingCapabilityRepository.countByStatusAndValueIn(
            Status.ACTIVE, Set.of(values));

    return count == values.length;
  }

  /**
   * Identifies this component as a validator for the {@link
   * RuleTargetType#PLAYLIST_RENDERING_CAPABILITY} target type.
   *
   * @return the {@link RuleTargetType} this validator supports
   */
  @Override
  public RuleTargetType getRuleTarget() {
    return PLAYLIST_RENDERING_CAPABILITY;
  }
}

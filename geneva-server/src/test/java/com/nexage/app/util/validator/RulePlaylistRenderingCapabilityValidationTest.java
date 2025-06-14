package com.nexage.app.util.validator;

import static com.nexage.admin.core.enums.RuleTargetType.PLAYLIST_RENDERING_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.repository.PlaylistRenderingCapabilityRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RulePlaylistRenderingCapabilityValidationTest {

  @Mock private PlaylistRenderingCapabilityRepository repository;
  @InjectMocks private RulePlaylistRenderingCapabilityValidation validation;

  @Test
  void shouldBeValidatorForPlaylistRenderingCapabilityTarget() {
    assertEquals(PLAYLIST_RENDERING_CAPABILITY, validation.getRuleTarget());
  }

  @Test
  void shouldBeValidWhenDataIsCommaSeparatedStringOfActivePlaylistRenderingCapabilityValues() {
    when(repository.countByStatusAndValueIn(Status.ACTIVE, Set.of("cap1", "cap2", "cap3")))
        .thenReturn(3);

    assertTrue(validation.isValid("cap1,cap2,cap3"));
  }

  @Test
  void shouldBeInvalidWhenDataIsNotCommaSeparatedStringOfActivePlaylistRenderingCapabilityValues() {
    when(repository.countByStatusAndValueIn(Status.ACTIVE, Set.of("cap1", "cap2", "cap3")))
        .thenReturn(2);

    assertFalse(validation.isValid("cap1,cap2,cap3"));
  }
}

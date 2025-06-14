package com.nexage.app.dto.sellingrule;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(NON_NULL)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class InventoryAssignmentsDTO {
  @JsonInclude(Include.NON_EMPTY)
  @Builder.Default
  @Valid
  private Set<PublisherAssignmentDTO> publishers = new HashSet<>();

  @JsonInclude(Include.NON_EMPTY)
  @Builder.Default
  @Valid
  private Set<SiteAssignmentDTO> sites = new HashSet<>();

  @JsonInclude(Include.NON_EMPTY)
  @Builder.Default
  @Valid
  private Set<PositionAssignmentDTO> positions = new HashSet<>();
}

package com.nexage.app.dto.inventory.attributes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.SellerInventoryAttributeDTOConstraint;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SellerInventoryAttributeDTOConstraint(groups = {CreateGroup.class, UpdateGroup.class})
public class InventoryAttributeDTO implements Serializable {
  @NotNull private String name;
  private String description;
  private String prefix;

  @NotNull private Long createdBy;

  @Null(groups = {CreateGroup.class})
  @NotNull(groups = {UpdateGroup.class})
  private Long pid;

  @Deprecated private Long attributePid;

  @NotNull private Status status;

  @JsonInclude(Include.NON_EMPTY)
  private Set<InventoryAttributeValueDTO> attributeValues = new HashSet<>();

  private boolean isInternalOnly;
  private boolean hasGlobalVisibility;

  @JsonInclude(Include.NON_EMPTY)
  private Set<SpecificPublisherVisibility> specificPublisherVisibility = new HashSet<>();

  private Set<Integer> assignedLevel;
  private boolean isRequired;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date lastUpdated;

  @Null(groups = {CreateGroup.class})
  @NotNull(groups = {UpdateGroup.class})
  private Integer version;

  private Long inventoryAttributeValueCount;

  private Long inventoryAttributeValueCountActive;
  @JsonIgnore private Long sellerPid;

  @JsonInclude(Include.NON_NULL)
  public static class SpecificPublisherVisibility {
    private String name;
    private Long publisherPid;

    public SpecificPublisherVisibility() {}

    public SpecificPublisherVisibility(String name, Long publisherPid) {
      super();
      this.name = name;
      this.publisherPid = publisherPid;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Long getPublisherPid() {
      return publisherPid;
    }

    public void setPublisherPid(Long publisherPid) {
      this.publisherPid = publisherPid;
    }
  }
}

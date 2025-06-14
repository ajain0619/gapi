package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.NonPageable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonInclude(Include.NON_EMPTY)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class SpecificAssignedInventoryDTO implements NonPageable<DealSellerDTO> {

  @Valid private final List<DealSellerDTO> content;

  public SpecificAssignedInventoryDTO() {
    content = new ArrayList<>();
  }

  public void setContent(List<DealSellerDTO> sellers) {
    this.content.clear();
    this.content.addAll(sellers);
  }

  @Override
  public Collection<DealSellerDTO> getContent() {
    return content;
  }

  @Override
  @JsonGetter("size")
  @EqualsAndHashCode.Include
  @ToString.Include
  public int size() {
    return content.size();
  }
}

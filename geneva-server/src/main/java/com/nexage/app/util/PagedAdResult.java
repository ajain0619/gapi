package com.nexage.app.util;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PagedAdResult<T> extends PageImpl<T> {

  private Set<String> buyers;

  public PagedAdResult(List<T> content, Set<String> buyers, Pageable pageable, Long total) {
    super(content, pageable, total);
    this.buyers = buyers;
  }

  public Set<String> getBuyers() {
    return buyers;
  }

  public void setBuyers(Set<String> buyers) {
    this.buyers = buyers;
  }
}

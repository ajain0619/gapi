package com.nexage.app.dto.postauctiondiscount;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class PostAuctionDiscountRevenueGroupDTO implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include private Long pid;
  private PostAuctionDiscountTypeDTO type;
}

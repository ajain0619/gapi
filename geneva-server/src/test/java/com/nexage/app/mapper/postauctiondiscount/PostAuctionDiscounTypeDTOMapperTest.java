package com.nexage.app.mapper.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostAuctionDiscounTypeDTOMapperTest {

  @Test
  void shouldMapModelToDto() {
    // given
    var pid = 123L;
    var name = "pad v1";
    PostAuctionDiscountType type = new PostAuctionDiscountType(pid, name, null, null);

    // when
    PostAuctionDiscountTypeDTO output = PostAuctionDiscountTypeDTOMapper.MAPPER.map(type);

    // then
    assertEquals(pid, output.getPid());
    assertEquals(name, output.getName());
  }

  @Test
  void shouldMapDtoToModel() {
    // given
    var pid = 123L;
    var name = "pad v1";
    PostAuctionDiscountTypeDTO typeDTO = new PostAuctionDiscountTypeDTO(pid, name);

    // when
    PostAuctionDiscountType output = PostAuctionDiscountTypeDTOMapper.MAPPER.map(typeDTO);

    // then
    assertEquals(pid, output.getPid());
    assertEquals(name, output.getName());
    assertNull(output.getCreatedOn());
    assertNull(output.getUpdatedOn());
  }
}

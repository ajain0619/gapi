package com.nexage.app.mapper.postauctiondiscount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.RevenueGroup;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountRevenueGroup;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountType;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountRevenueGroupDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostAuctionDiscountRevenueGroupDTOMapperTest {

  @Mock private EntityManager entityManager;

  @Test
  void shouldMapModelToDto() {
    // given
    var revGroupPid = 234L;
    var typePid = 1L;
    var typeName = "pad v1";
    PostAuctionDiscountRevenueGroup padRevGroup = new PostAuctionDiscountRevenueGroup();
    padRevGroup.setPid(123L);
    RevenueGroup revenueGroup = new RevenueGroup();
    revenueGroup.setPid(revGroupPid);
    padRevGroup.setRevenueGroup(revenueGroup);
    padRevGroup.setType(new PostAuctionDiscountType(typePid, typeName, null, null));

    // when
    PostAuctionDiscountRevenueGroupDTO output =
        PostAuctionDiscountRevenueGroupDTOMapper.MAPPER.map(padRevGroup);

    // then
    assertEquals(revGroupPid, output.getPid());
    assertEquals(typePid, output.getType().getPid());
    assertEquals(typeName, output.getType().getName());
  }

  @Test
  void shouldMapDtoToModelWhenItDoesNotExistInDiscount() {
    // given
    var pid = 123L;
    var typePid = 1L;
    var typeName = "pad v1";
    PostAuctionDiscountRevenueGroupDTO dto =
        new PostAuctionDiscountRevenueGroupDTO(
            pid, new PostAuctionDiscountTypeDTO(typePid, typeName));
    var pad = new PostAuctionDiscount();
    when(entityManager.getReference(RevenueGroup.class, pid))
        .thenReturn(
            new RevenueGroup() {
              {
                setPid(pid);
              }
            });

    // when
    List<PostAuctionDiscountRevenueGroup> output =
        PostAuctionDiscountRevenueGroupDTOMapper.MAPPER.map(List.of(dto), pad, entityManager);

    // then
    assertEquals(1, output.size());
    PostAuctionDiscountRevenueGroup revGroup = output.get(0);
    assertEquals(pid, revGroup.getRevenueGroup().getPid());
    assertNull(revGroup.getPid());
    assertEquals(pad, revGroup.getPostAuctionDiscount());
    assertEquals(typePid, revGroup.getType().getPid());
    assertEquals(typeName, revGroup.getType().getName());
  }

  @Test
  void shouldMapDtoToModelWhenItExistsInDiscountWithSameType() {
    // given
    var pid = 123L;
    var typePid = 1L;
    var typeName = "pad v1";
    PostAuctionDiscountRevenueGroupDTO dto =
        new PostAuctionDiscountRevenueGroupDTO(
            pid, new PostAuctionDiscountTypeDTO(typePid, typeName));
    var pad = new PostAuctionDiscount();
    when(entityManager.getReference(RevenueGroup.class, pid))
        .thenReturn(
            new RevenueGroup() {
              {
                setPid(pid);
              }
            });

    // when
    List<PostAuctionDiscountRevenueGroup> output =
        PostAuctionDiscountRevenueGroupDTOMapper.MAPPER.map(List.of(dto), pad, entityManager);

    // then
    assertEquals(1, output.size());
    PostAuctionDiscountRevenueGroup revGroup = output.get(0);
    assertEquals(pid, revGroup.getRevenueGroup().getPid());
    assertNull(revGroup.getPid());
    assertEquals(pad, revGroup.getPostAuctionDiscount());
    assertEquals(typePid, revGroup.getType().getPid());
    assertEquals(typeName, revGroup.getType().getName());
  }
}

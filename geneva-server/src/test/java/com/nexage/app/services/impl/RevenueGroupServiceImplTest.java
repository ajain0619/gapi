package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createRevenueGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.RevenueGroup;
import com.nexage.admin.core.repository.RevenueGroupRepository;
import com.nexage.app.dto.RevenueGroupDTO;
import com.nexage.app.mapper.RevenueGroupDTOMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RevenueGroupServiceImplTest {

  @Mock private RevenueGroupRepository revenueGroupRepository;
  @InjectMocks private RevenueGroupServiceImpl revenueGroupService;

  @Test
  void shouldGetAllRevenueGroups() {
    // given
    Pageable pageable = Pageable.ofSize(10);
    Page<RevenueGroup> repositoryOutput =
        new PageImpl<>(List.of(createRevenueGroup(), createRevenueGroup(), createRevenueGroup()));
    given(revenueGroupRepository.findAllByStatus(Status.ACTIVE, pageable))
        .willReturn(repositoryOutput);

    // when
    Page<RevenueGroupDTO> output = revenueGroupService.getRevenueGroups(pageable);

    // then
    assertEquals(repositoryOutput.map(RevenueGroupDTOMapper.MAPPER::map), output);
  }
}

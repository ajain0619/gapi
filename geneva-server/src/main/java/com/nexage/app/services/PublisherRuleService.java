package com.nexage.app.services;

import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PublisherRuleService {
  SellerRuleDTO create(SellerRuleDTO sellerRuleDTO);

  SellerRuleDTO update(Long rulePid, SellerRuleDTO sellerRuleDTO);

  Page<SellerRuleDTO> findRulesByPidAndTypeAndStatusWithPagination(
      Long publisherPid,
      String types,
      String statuses,
      Pageable pageable,
      Set<String> qf,
      String qt);

  void delete(Long rulePid);

  SellerRuleDTO find(Long rulePid);
}

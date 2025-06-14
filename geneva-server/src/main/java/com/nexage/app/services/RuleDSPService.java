package com.nexage.app.services;

import com.nexage.app.dto.RuleDSPBiddersDTO;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Find all {@link RuleDSPBiddersDTO} that are available (without pagination). A service built
 * retrieve a list of DSPs
 */
@Component
public interface RuleDSPService {

  /**
   * @return {@link List} of {@link RuleDSPBiddersDTO} for all DSPs that have bidders associated
   *     with them. Does not retrieve a record if there are no bidders for that company
   */
  List<RuleDSPBiddersDTO> findAll();
}

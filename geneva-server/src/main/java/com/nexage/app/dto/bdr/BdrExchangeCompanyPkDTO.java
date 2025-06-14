package com.nexage.app.dto.bdr;

import com.nexage.app.dto.CompanyDTO;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BdrExchangeCompanyPkDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private BdrExchangeDTO bidderExchange;
  private CompanyDTO company;
}

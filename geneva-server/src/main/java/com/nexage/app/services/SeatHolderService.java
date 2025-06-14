package com.nexage.app.services;

import com.nexage.admin.core.model.Company;
import java.math.BigDecimal;

public interface SeatHolderService {

  public Company addCreditToSeatHolder(long pid, BigDecimal credit);

  public Company addCreditToSeatHolder(Company seatholder, BigDecimal credit);
}

package com.nexage.app.util.assemblers.buyer;

import com.nexage.admin.core.model.BuyerSeat;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.BuyerGroupRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.buyer.BuyerSeatDTO;
import com.nexage.app.util.assemblers.NoContextAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyerSeatAssembler extends NoContextAssembler {

  private final BuyerGroupRepository buyerGroupRepository;
  private final CompanyRepository companyRepository;

  @Autowired
  public BuyerSeatAssembler(
      BuyerGroupRepository buyerGroupRepository, CompanyRepository companyRepository) {
    this.buyerGroupRepository = buyerGroupRepository;
    this.companyRepository = companyRepository;
  }

  public BuyerSeat transientEntity(Long companyPid, BuyerSeatDTO dto) {
    BuyerSeat entity = new BuyerSeat();

    Company companyRef = companyRepository.getOne(companyPid);
    entity.setCompany(companyRef);

    var buyerGroupRef = buyerGroupRepository.getOne(dto.getBuyerGroupPid());
    entity.setBuyerGroup(buyerGroupRef);

    entity.setName(dto.getName());
    entity.setSeat(dto.getSeat());
    entity.setEnabled(dto.isEnabled());
    entity.setBuyerTransparencyDataFeedPid(dto.getBuyerTransparencyDataFeedPid());
    entity.setBuyerTransparencyFeedEnabled(dto.isBuyerTransparencyFeedEnabled());

    return entity;
  }

  public BuyerSeat apply(BuyerSeat entity, BuyerSeatDTO dto) {
    entity.setEnabled(dto.isEnabled());
    entity.setName(dto.getName());
    entity.setSeat(dto.getSeat());
    entity.setBuyerTransparencyDataFeedPid(dto.getBuyerTransparencyDataFeedPid());
    entity.setBuyerTransparencyFeedEnabled(dto.isBuyerTransparencyFeedEnabled());

    var buyerGroupRef = buyerGroupRepository.getOne(dto.getBuyerGroupPid());
    entity.setBuyerGroup(buyerGroupRef);

    return entity;
  }

  public BuyerSeatDTO make(BuyerSeat buyerSeat) {
    return new BuyerSeatDTO(buyerSeat);
  }
}

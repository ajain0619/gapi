package com.nexage.app.util.assemblers.buyer;

import com.nexage.admin.core.model.BuyerGroup;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.app.dto.buyer.BuyerGroupDTO;
import com.nexage.app.mapper.BuyerGroupDTOMapper;
import com.nexage.app.util.assemblers.NoContextAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyerGroupAssembler extends NoContextAssembler {

  private final CompanyRepository companyRepository;

  @Autowired
  public BuyerGroupAssembler(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public BuyerGroup transientEntity(Long companyPid, BuyerGroupDTO dto) {
    BuyerGroup entity = new BuyerGroup();

    Company companyRef = companyRepository.getOne(companyPid);
    entity.setCompany(companyRef);
    entity = this.apply(entity, dto);
    return entity;
  }

  public BuyerGroup apply(BuyerGroup entity, BuyerGroupDTO dto) {
    entity.setName(dto.getName());
    entity.setSfdcLineId(dto.getSfdcLineId());
    entity.setSfdcIoId(dto.getSfdcIoId());
    entity.setCurrency(dto.getCurrency());
    entity.setBillingCountry(dto.getBillingCountry());
    entity.setBillable(dto.getBillable());

    return entity;
  }

  /**
   * @param buyerGroup {@link BuyerGroup}
   * @return instance of type {@link BuyerGroupDTO}
   * @deprecated use {@link BuyerGroupDTOMapper#manualMap(BuyerGroup)} instead.
   */
  @Deprecated
  public BuyerGroupDTO make(BuyerGroup buyerGroup) {
    return BuyerGroupDTOMapper.MAPPER.manualMap(buyerGroup);
  }
}

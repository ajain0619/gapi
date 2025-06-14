package com.nexage.app.services.deal.impl;

import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.repository.CompanyViewRepository;
import com.nexage.admin.core.repository.DealAppAliasRepository;
import com.nexage.admin.core.repository.DealAppBundleDataRepository;
import com.nexage.admin.core.repository.DealDomainRepository;
import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DealSiteRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.deals.DealPlacementDTO;
import com.nexage.app.dto.deals.DealSellerDTO;
import com.nexage.app.dto.deals.DealSiteDTO;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DealPlacementDTOMapper;
import com.nexage.app.mapper.deal.DealSellerDTOMapper;
import com.nexage.app.mapper.deal.DealSiteDTOMapper;
import com.nexage.app.services.deal.DealSpecificAssignedInventoryService;
import com.nexage.app.services.validation.sellingrule.ZeroCostDealValidator;
import com.nexage.app.util.validator.deals.DealSpecificInventoriesFileParser;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@Transactional
public class SpecificAssignedInventoryDTOServiceImpl
    implements DealSpecificAssignedInventoryService {

  private final DealPublisherRepository dealPublisherRepository;
  private final DealSiteRepository dealSiteRepository;
  private final DealPositionRepository dealPositionRepository;
  private final DirectDealRepository dealRepository;
  private final PositionRepository positionRepository;
  private final SiteRepository siteRepository;
  private final EntityManager entityManager;
  private final ZeroCostDealValidator zeroCostDealValidator;
  private final DealDomainRepository dealDomainRepository;
  private final DealAppBundleDataRepository dealAppBundleDataRepository;
  private final DealAppAliasRepository dealAppAliasRepository;
  private final CompanyViewRepository companyViewRepository;
  private final DealSpecificInventoriesFileParser dealSpecificInventoriesFileParser;
  private final PositionViewRepository positionViewRepository;

  @Autowired
  public SpecificAssignedInventoryDTOServiceImpl(
      DealPublisherRepository dealPublisherRepository,
      DealSiteRepository dealSiteRepository,
      DealPositionRepository dealPositionRepository,
      DirectDealRepository dealRepository,
      PositionRepository positionRepository,
      SiteRepository siteRepository,
      EntityManager entityManager,
      ZeroCostDealValidator zeroCostDealValidator,
      DealDomainRepository dealDomainRepository,
      DealAppBundleDataRepository dealAppBundleDataRepository,
      DealAppAliasRepository dealAppAliasRepository,
      CompanyViewRepository companyViewRepository,
      DealSpecificInventoriesFileParser dealSpecificInventoriesFileParser,
      PositionViewRepository positionViewRepository) {
    this.dealPublisherRepository = dealPublisherRepository;
    this.dealSiteRepository = dealSiteRepository;
    this.dealPositionRepository = dealPositionRepository;
    this.dealRepository = dealRepository;
    this.positionRepository = positionRepository;
    this.siteRepository = siteRepository;
    this.entityManager = entityManager;
    this.zeroCostDealValidator = zeroCostDealValidator;
    this.dealDomainRepository = dealDomainRepository;
    this.dealAppBundleDataRepository = dealAppBundleDataRepository;
    this.dealAppAliasRepository = dealAppAliasRepository;
    this.companyViewRepository = companyViewRepository;
    this.dealSpecificInventoriesFileParser = dealSpecificInventoriesFileParser;
    this.positionViewRepository = positionViewRepository;
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or ((@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerYieldNexage()) "
          + "and @loginUserContext.isDealAdmin())")
  @Override
  public SpecificAssignedInventoryDTO createNewAssignedInventory(
      Long dealPid, SpecificAssignedInventoryDTO dealAssignedInventoryDTO) {

    var deal =
        dealRepository
            .findById(dealPid)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_NOT_FOUND));

    zeroCostDealValidator.validateZeroCostDeals(deal, dealAssignedInventoryDTO);

    dealPublisherRepository.deleteByDealPid(dealPid);
    dealSiteRepository.deleteByDealPid(dealPid);
    dealPositionRepository.deleteByDealPid(dealPid);

    // Delete bulk upload entries added via file
    dealDomainRepository.deleteByDealPid(dealPid);
    dealAppBundleDataRepository.deleteByDealPid(dealPid);
    dealAppAliasRepository.deleteByDealPid(dealPid);

    var sellersToSave = new HashSet<DealSellerDTO>();
    var placementsToSave = new HashSet<DealPlacementDTO>();
    var sitesToSave = new HashSet<DealSiteDTO>();

    dealAssignedInventoryDTO
        .getContent()
        .forEach(
            seller -> {
              seller
                  .getSites()
                  .forEach(
                      site -> {
                        if (site.getPlacements().isEmpty()) {
                          sitesToSave.add(site);
                        } else {
                          placementsToSave.addAll(site.getPlacements());
                        }
                      });
              if (seller.getSites().isEmpty()) {
                sellersToSave.add(seller);
              }
            });

    var updatedPositions =
        dealPositionRepository.saveAll(
            placementsToSave.stream()
                .map(
                    dto -> {
                      var position =
                          entityManager.getReference(PositionView.class, dto.getPlacementPid());
                      return DealPlacementDTOMapper.MAPPER.map(dto, deal, position);
                    })
                .collect(Collectors.toList()));

    var updatedSites =
        dealSiteRepository.saveAll(
            sitesToSave.stream()
                .map(dto -> DealSiteDTOMapper.MAPPER.map(dto, deal))
                .collect(Collectors.toList()));

    var updatedSellers =
        dealPublisherRepository.saveAll(
            sellersToSave.stream()
                .map(dto -> DealSellerDTOMapper.MAPPER.map(dto, deal))
                .collect(Collectors.toList()));

    if (null != deal.getPlacementFormula()) {
      deal.setPlacementFormula(null);
      deal.setPlacementFormulaStatus(PlacementFormulaStatus.DONE);
      dealRepository.save(deal);
    }
    if (!updatedSellers.isEmpty() || !updatedSites.isEmpty() || !updatedPositions.isEmpty()) {
      setPidsForSpecificAssignedInventory(
          updatedSellers, updatedSites, updatedPositions, dealAssignedInventoryDTO);
    }
    return dealAssignedInventoryDTO;
  }

  private void setPidsForSpecificAssignedInventory(
      Collection<DealPublisher> dealPublishers,
      Collection<DealSite> dealSites,
      Collection<DealPosition> dealPositions,
      SpecificAssignedInventoryDTO dealAssignedInventoryDTO) {
    dealAssignedInventoryDTO
        .getContent()
        .forEach(
            seller -> {
              var dealPublisher =
                  dealPublishers.stream()
                      .filter(
                          updatedSeller -> updatedSeller.getPubPid().equals(seller.getSellerPid()))
                      .findFirst()
                      .orElse(null);
              if (dealPublisher != null) seller.setPid(dealPublisher.getPid());

              seller
                  .getSites()
                  .forEach(
                      site -> {
                        var dealSite =
                            dealSites.stream()
                                .filter(
                                    updatedSite ->
                                        updatedSite.getSitePid().equals(site.getSitePid()))
                                .findFirst()
                                .orElse(null);
                        if (dealSite != null) site.setPid(dealSite.getPid());

                        site.getPlacements()
                            .forEach(
                                position -> {
                                  var dealPosition =
                                      dealPositions.stream()
                                          .filter(
                                              updatedPosition ->
                                                  updatedPosition
                                                      .getPositionPid()
                                                      .equals(position.getPlacementPid()))
                                          .findFirst()
                                          .orElse(null);
                                  if (dealPosition != null) position.setPid(dealPosition.getPid());
                                });
                      });
            });
  }

  @Transactional(readOnly = true)
  @Override
  public SpecificAssignedInventoryDTO getAssignedInventory(Long dealPid) {
    var dealPublishers = dealPublisherRepository.findByDealPid(dealPid);
    var dealSites = dealSiteRepository.findByDealPid(dealPid);
    var dealPositions = dealPositionRepository.findByDealPid(dealPid);
    if (!dealPositions.isEmpty()) {
      dealSites.addAll(getDealSitesByPositions(dealPositions));
    }
    if (!dealSites.isEmpty()) {
      dealPublishers.addAll(getDealPublishersBySite(dealSites));
    }
    setPositionDetails(dealPositions);
    return getSpecificAssignedInventoryDTO(dealPublishers, dealSites, dealPositions);
  }

  /** {@inheritDoc} */
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() "
          + "or (@loginUserContext.isOcManagerNexage() and @loginUserContext.isDealAdmin()) "
          + "or @loginUserContext.isOcManagerSeller() and @loginUserContext.doSameOrNexageAffiliation(#sellerId)")
  @Override
  public SpecificAssignedInventoryDTO createNewAssignedInventoryAssociatedWithSeller(
      Long sellerId, Long dealPid, SpecificAssignedInventoryDTO dealAssignedInventoryDTO) {
    return createNewAssignedInventory(dealPid, dealAssignedInventoryDTO);
  }

  private void setPositionDetails(List<DealPosition> dealPositions) {
    if (!dealPositions.isEmpty()) {
      var positionViewMap =
          positionRepository
              .findAllByPidIn(
                  dealPositions.stream()
                      .map(DealPosition::getPositionPid)
                      .collect(Collectors.toList()))
              .stream()
              .collect(Collectors.toMap(PositionView::getPid, Function.identity()));
      dealPositions.forEach(dp -> dp.setPositionView(positionViewMap.get(dp.getPositionPid())));
    }
  }

  private Map<Long, PositionView> getPositionViewByPositionIds(List<Long> pids) {
    if (!pids.isEmpty()) {
      return positionViewRepository.findAllByPidsIn(pids).stream()
          .collect(Collectors.toMap(PositionView::getPid, Function.identity()));
    }
    return null;
  }

  private Map<Long, SiteView> getSiteViewBySiteIds(List<Long> pids) {
    if (!pids.isEmpty()) {
      return siteRepository.findBySitePidIn(pids).stream()
          .collect(Collectors.toMap(SiteView::getPid, Function.identity()));
    }
    return null;
  }

  private Map<Long, CompanyView> getCompanyViewByCompanyIds(List<Long> pids) {
    if (!pids.isEmpty()) {
      return companyViewRepository.findCompaniesByIds(pids).stream()
          .collect(Collectors.toMap(CompanyView::getPid, Function.identity()));
    }
    return null;
  }

  private Set<DealSite> getDealSitesByPositions(List<DealPosition> dealPositions) {
    var positionViews =
        positionRepository.findAllByPidIn(
            dealPositions.stream().map(DealPosition::getPositionPid).collect(Collectors.toList()));
    var siteIdsList =
        positionViews.stream().map(PositionView::getSitePid).collect(Collectors.toList());
    var sites = siteRepository.findBySitePidIn(siteIdsList);
    return sites.stream().map(s -> new DealSite(s.getPid(), s)).collect(Collectors.toSet());
  }

  private Set<DealPublisher> getDealPublishersBySite(List<DealSite> dealSites) {
    return dealSites.stream().map(this::dealSiteToPublisher).collect(Collectors.toSet());
  }

  private DealPublisher dealSiteToPublisher(DealSite dealSite) {
    var company = dealSite.getSiteView().getCompany();
    var companyView = new CompanyView(company.getPid(), company.getName());
    return new DealPublisher(company.getPid(), companyView);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "@loginUserContext.isOcAdminNexage() OR @loginUserContext.isOcManagerNexage() "
          + "OR @loginUserContext.isOcManagerYieldNexage() "
          + "OR @loginUserContext.isOcManagerSmartexNexage() "
          + "OR @loginUserContext.isOcUserNexage()"
          + "OR @loginUserContext.isOcAdminSeller()")
  public SpecificAssignedInventoryDTO processBulkInventories(MultipartFile inventoriesFile) {

    List[] output =
        dealSpecificInventoriesFileParser.processSpecificInventoriesFile(inventoriesFile);

    List<CompanyView> companyViews = output[0];
    List<SiteView> siteViews = output[1];
    List<PositionView> positionViews = output[2];

    Map<Long, PositionView> positionViewMap =
        getPositionViewByPositionIds(
            positionViews.stream().map(PositionView::getPid).collect(Collectors.toList()));
    Map<Long, SiteView> siteViewMap =
        getSiteViewBySiteIds(siteViews.stream().map(SiteView::getPid).collect(Collectors.toList()));
    Map<Long, CompanyView> companyViewMap =
        getCompanyViewByCompanyIds(
            companyViews.stream().map(CompanyView::getPid).collect(Collectors.toList()));

    ArrayList<String> invalidEntries = new ArrayList<>();

    positionViews.forEach(
        positionView -> {
          var view = positionViewMap.get(positionView.getPid());
          if (view == null
              || !view.getSiteView().getPid().equals(positionView.getSiteView().getPid())
              || !view.getSiteView()
                  .getCompany()
                  .getPid()
                  .equals(positionView.getSiteView().getCompany().getPid())) {
            invalidEntries.add(
                String.format(
                    "\"%d,%d,%d\"",
                    positionView.getSiteView().getCompany().getPid(),
                    positionView.getSiteView().getPid(),
                    positionView.getPid()));
          }
        });

    siteViews.forEach(
        siteView -> {
          var view = siteViewMap.get(siteView.getPid());
          if (view == null || !view.getCompany().getPid().equals(siteView.getCompany().getPid())) {
            invalidEntries.add(
                String.format("\"%d,%d\"", siteView.getCompany().getPid(), siteView.getPid()));
          }
        });

    companyViews.forEach(
        companyView -> {
          if (companyViewMap.get(companyView.getPid()) == null) {
            invalidEntries.add(String.format("\"%d\"", companyView.getPid()));
          }
        });

    if (!invalidEntries.isEmpty()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVALID_ENTRIES,
          new Object[] {invalidEntries.size(), String.join(",", invalidEntries)});
    }

    Set<DealSite> dealSites = new HashSet<>();
    Set<DealPublisher> dealPublishers = new HashSet<>();
    Set<DealPosition> dealPositions = new HashSet<>();

    positionViews.forEach(
        p -> {
          var view = positionViewMap.get(p.getPid());
          var dp = new DealPosition();
          dp.setPositionPid(view.getPid());
          dp.setPositionView(view);
          dealPositions.add(dp);
          dealSites.add(new DealSite(view.getSiteView().getPid(), view.getSiteView()));
          dealPublishers.add(
              new DealPublisher(
                  view.getSiteView().getCompany().getPid(),
                  new CompanyView(
                      view.getSiteView().getCompany().getPid(),
                      view.getSiteView().getCompany().getName())));
        });

    siteViews.forEach(
        s -> {
          var view = siteViewMap.get(s.getPid());
          var ds = new DealSite(view.getPid(), view);
          dealSites.add(ds);
          dealPublishers.add(
              new DealPublisher(
                  view.getCompany().getPid(),
                  new CompanyView(view.getCompany().getPid(), view.getCompany().getName())));
        });

    companyViews.forEach(
        c -> {
          var view = companyViewMap.get(c.getPid());
          dealPublishers.add(new DealPublisher(view.getPid(), view));
        });

    return getSpecificAssignedInventoryDTO(dealPublishers, dealSites, dealPositions);
  }

  private SpecificAssignedInventoryDTO getSpecificAssignedInventoryDTO(
      Collection<DealPublisher> dealPublishers,
      Collection<DealSite> dealSites,
      Collection<DealPosition> dealPositions) {

    var pubSitesMap =
        dealSites.stream()
            .collect(
                Collectors.groupingBy(
                    dealSiteView -> dealSiteView.getSiteView().getCompany().getPid()));

    var sitePositionsMap =
        dealPositions.stream()
            .collect(Collectors.groupingBy(dp -> dp.getPositionView().getSitePid()));

    var sellers =
        dealPublishers.stream()
            .map(dp -> DealSellerDTOMapper.MAPPER.map(dp, pubSitesMap, sitePositionsMap))
            .collect(Collectors.toList());

    var specificInventoryDto = new SpecificAssignedInventoryDTO();
    specificInventoryDto.setContent(sellers);
    return specificInventoryDto;
  }
}

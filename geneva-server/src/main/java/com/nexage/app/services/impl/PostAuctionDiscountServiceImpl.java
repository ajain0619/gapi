package com.nexage.app.services.impl;

import static com.nexage.admin.core.specification.PostAuctionDiscountSpecification.withQueryFieldsAndSearchTermAndEnabled;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.repository.PostAuctionDiscountRepository;
import com.nexage.admin.core.repository.PostAuctionDiscountTypeRepository;
import com.nexage.admin.core.repository.RevenueGroupRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountRevenueGroupDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountTypeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.postauctiondiscount.PostAuctionDiscountDTOMapper;
import com.nexage.app.services.PostAuctionDiscountService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Log4j2
@Service("postAuctionDiscountService")
@PreAuthorize("@loginUserContext.isOcManagerYieldNexage() or @loginUserContext.isOcAdminNexage()")
@Transactional
public class PostAuctionDiscountServiceImpl implements PostAuctionDiscountService {

  private final PostAuctionDiscountRepository repository;
  private final RevenueGroupRepository revenueGroupRepository;
  private final SellerAttributesRepository sellerAttributesRepository;
  private final PostAuctionDiscountTypeRepository postAuctionDiscountTypeRepository;
  private final EntityManager entityManager;

  @Autowired
  public PostAuctionDiscountServiceImpl(
      PostAuctionDiscountRepository repository,
      RevenueGroupRepository revenueGroupRepository,
      SellerAttributesRepository sellerAttributesRepository,
      PostAuctionDiscountTypeRepository postAuctionDiscountTypeRepository,
      EntityManager entityManager) {
    this.repository = repository;
    this.revenueGroupRepository = revenueGroupRepository;
    this.sellerAttributesRepository = sellerAttributesRepository;
    this.postAuctionDiscountTypeRepository = postAuctionDiscountTypeRepository;
    this.entityManager = entityManager;
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public Page<PostAuctionDiscountDTO> getAll(
      Set<String> qf, String qt, Boolean discountStatus, Pageable pageable) {
    return repository
        .findAll(withQueryFieldsAndSearchTermAndEnabled(qf, qt, discountStatus), pageable)
        .map(PostAuctionDiscountDTOMapper.MAPPER::map);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public PostAuctionDiscountDTO get(Long postAuctionDiscountPid) {
    return PostAuctionDiscountDTOMapper.MAPPER.map(
        repository
            .findById(postAuctionDiscountPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_NOT_FOUND)));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerYieldNexage() or @loginUserContext.isOcAdminNexage()")
  public PostAuctionDiscountDTO create(PostAuctionDiscountDTO postAuctionDiscountDTO) {
    validateSellerSelectedOnce(postAuctionDiscountDTO);
    validateRevenueGroupSelectedOnce(postAuctionDiscountDTO);
    validateAllRevenueGroupsExist(postAuctionDiscountDTO);
    validateNoSellerAndRevenueGroupHaveSameDiscountType(postAuctionDiscountDTO);
    validateNoSellerIsAssignedToAnyOfSelectedRevenueGroups(postAuctionDiscountDTO);
    validateAllDiscountTypesExist(postAuctionDiscountDTO);
    validateBuyerPublisherOpenAuctionDealsCombinations(postAuctionDiscountDTO);
    return PostAuctionDiscountDTOMapper.MAPPER.map(
        repository.saveAndFlush(
            PostAuctionDiscountDTOMapper.MAPPER.map(
                postAuctionDiscountDTO, new PostAuctionDiscount(), entityManager)));
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("@loginUserContext.isOcManagerYieldNexage() or @loginUserContext.isOcAdminNexage()")
  public PostAuctionDiscountDTO update(PostAuctionDiscountDTO postAuctionDiscountDTO) {
    var originalPostAuctionDiscount =
        repository
            .findById(postAuctionDiscountDTO.getPid())
            .orElseThrow(
                () ->
                    new GenevaValidationException(
                        ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_NOT_FOUND));

    validateSellerSelectedOnce(postAuctionDiscountDTO);
    validateRevenueGroupSelectedOnce(postAuctionDiscountDTO);
    validateAllRevenueGroupsExist(postAuctionDiscountDTO);
    validateNoSellerAndRevenueGroupHaveSameDiscountType(postAuctionDiscountDTO);
    validateNoSellerIsAssignedToAnyOfSelectedRevenueGroups(postAuctionDiscountDTO);
    validateAllDiscountTypesExist(postAuctionDiscountDTO);
    validateBuyerPublisherOpenAuctionDealsCombinations(postAuctionDiscountDTO);
    return PostAuctionDiscountDTOMapper.MAPPER.map(
        repository.saveAndFlush(
            PostAuctionDiscountDTOMapper.MAPPER.map(
                postAuctionDiscountDTO, originalPostAuctionDiscount, entityManager)));
  }

  /**
   * Loops through the lists of dsps and sellers to validate if a seller/dsp with open auction and
   * deals combination already exists. If the combination already exists it checks to see if the
   * post auction discount has the same pid. If it does not the function will throw a
   * BadRequestException.
   *
   * @param postAuctionDiscountDTO The full post auction discount object to test all seller and dsp
   *     along with deals and open auction combinations.
   */
  private void validateBuyerPublisherOpenAuctionDealsCombinations(
      PostAuctionDiscountDTO postAuctionDiscountDTO) {
    var dealValidator = getDealsValidator(postAuctionDiscountDTO);
    List<PostAuctionDiscount> discountsWithMatchingSellerDspPair =
        repository.findByDspSellerPairIncludingRevenueGroups(
            postAuctionDiscountDTO.getDiscountDSPs().stream()
                .flatMap(dspdto -> dspdto.getDspSeats().stream())
                .map(PostAuctionDiscountDspSeatDTO::getPid)
                .toList(),
            CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountSellers())
                ? null
                : postAuctionDiscountDTO.getDiscountSellers().stream()
                    .map(PostAuctionDiscountSellerDTO::getPid)
                    .toList(),
            CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountRevenueGroups())
                ? null
                : postAuctionDiscountDTO.getDiscountRevenueGroups().stream()
                    .map(PostAuctionDiscountRevenueGroupDTO::getPid)
                    .toList());

    discountsWithMatchingSellerDspPair.stream()
        .filter(
            existingDiscount -> !existingDiscount.getPid().equals(postAuctionDiscountDTO.getPid()))
        .filter(
            existingDiscount ->
                (existingDiscount.getOpenAuctionEnabled()
                        && postAuctionDiscountDTO.getOpenAuctionEnabled())
                    || dealValidator.test(existingDiscount))
        .findFirst()
        .ifPresent(
            existingDiscount -> {
              throw new GenevaValidationException(
                  ServerErrorCodes.SERVER_POST_AUCTION_DISCOUNT_ALREADY_EXISTS,
                  new Object[] {existingDiscount.getDiscountName()});
            });
  }

  /**
   * This takes PostAuctionDiscountDTO input and validate the deal options against post auction
   * discount object
   *
   * @param postAuctionDiscountDTO
   * @return predicate functional interface to validate against postDiscountAuction model object
   */
  private Predicate<PostAuctionDiscount> getDealsValidator(
      PostAuctionDiscountDTO postAuctionDiscountDTO) {
    boolean specificDeal =
        postAuctionDiscountDTO.getDealsSelected().equals(PostAuctionDealsSelected.SPECIFIC);
    Set<Long> specificDealsIds;
    if (specificDeal) {
      specificDealsIds =
          postAuctionDiscountDTO.getDiscountDeals().stream()
              .map(DirectDealViewDTO::getPid)
              .collect(Collectors.toSet());
    } else {
      specificDealsIds = Set.of();
    }

    return postAuctionDiscount -> {
      if (specificDealsIds.isEmpty()) {
        return postAuctionDiscount
                .getDealsSelected()
                .equals(postAuctionDiscountDTO.getDealsSelected())
            || postAuctionDiscount.getDealsSelected().equals(PostAuctionDealsSelected.SPECIFIC);
      } else {
        var existingDeals =
            postAuctionDiscount.getDeals().stream()
                .map(dealPostAuctionDiscount -> dealPostAuctionDiscount.getDeal().getPid())
                .collect(Collectors.toSet());
        return (!existingDeals.isEmpty() && !Collections.disjoint(existingDeals, specificDealsIds)
            || postAuctionDiscount.getDealsSelected().equals(PostAuctionDealsSelected.ALL));
      }
    };
  }

  private void validateSellerSelectedOnce(PostAuctionDiscountDTO postAuctionDiscountDTO) {
    if (CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountSellers())) {
      return;
    }

    if (postAuctionDiscountDTO.getDiscountSellers().stream()
            .map(PostAuctionDiscountSellerDTO::getPid)
            .distinct()
            .count()
        != postAuctionDiscountDTO.getDiscountSellers().size()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DISCOUNT_SELLER_REPEATED);
    }
  }

  private void validateRevenueGroupSelectedOnce(PostAuctionDiscountDTO postAuctionDiscountDTO) {
    if (CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountRevenueGroups())) {
      return;
    }

    if (postAuctionDiscountDTO.getDiscountRevenueGroups().stream()
            .map(PostAuctionDiscountRevenueGroupDTO::getPid)
            .distinct()
            .count()
        != postAuctionDiscountDTO.getDiscountRevenueGroups().size()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DISCOUNT_REVENUE_GROUP_REPEATED);
    }
  }

  private void validateAllRevenueGroupsExist(PostAuctionDiscountDTO postAuctionDiscountDTO) {
    if (CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountRevenueGroups())) {
      return;
    }

    List<Long> revenueGroupPids =
        postAuctionDiscountDTO.getDiscountRevenueGroups().stream()
            .map(PostAuctionDiscountRevenueGroupDTO::getPid)
            .toList();
    if (revenueGroupRepository.countByPidIn(revenueGroupPids) < revenueGroupPids.size()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_REVENUE_GROUP_NOT_FOUND);
    }
  }

  private void validateNoSellerAndRevenueGroupHaveSameDiscountType(
      PostAuctionDiscountDTO postAuctionDiscountDTO) {
    if (CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountRevenueGroups())
        || CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountSellers())) {
      return;
    }

    Set<PostAuctionDiscountTypeDTO> revenueGroupDiscountTypes =
        postAuctionDiscountDTO.getDiscountRevenueGroups().stream()
            .map(PostAuctionDiscountRevenueGroupDTO::getType)
            .collect(Collectors.toSet());
    Set<PostAuctionDiscountTypeDTO> sellerDiscountTypes =
        postAuctionDiscountDTO.getDiscountSellers().stream()
            .map(PostAuctionDiscountSellerDTO::getType)
            .collect(Collectors.toSet());
    if (!Collections.disjoint(revenueGroupDiscountTypes, sellerDiscountTypes)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DISCOUNT_SELLERS_AND_REVENUE_GROUPS_MIXED);
    }
  }

  private void validateNoSellerIsAssignedToAnyOfSelectedRevenueGroups(
      PostAuctionDiscountDTO postAuctionDiscountDTO) {
    if (CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountRevenueGroups())
        || CollectionUtils.isEmpty(postAuctionDiscountDTO.getDiscountSellers())) {
      return;
    }

    List<SellerAttributes> selectedSellersAttributes =
        sellerAttributesRepository.findAllById(
            postAuctionDiscountDTO.getDiscountSellers().stream()
                .map(PostAuctionDiscountSellerDTO::getPid)
                .toList());

    Set<Long> selectedSellersRevenueGroupPids =
        selectedSellersAttributes.stream()
            .map(SellerAttributes::getRevenueGroupPid)
            .collect(Collectors.toSet());
    Set<Long> selectedRevenueGroupPids =
        postAuctionDiscountDTO.getDiscountRevenueGroups().stream()
            .map(PostAuctionDiscountRevenueGroupDTO::getPid)
            .collect(Collectors.toSet());
    if (!Collections.disjoint(selectedSellersRevenueGroupPids, selectedRevenueGroupPids)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_DISCOUNT_SELLER_REVENUE_GROUP_CONFLICT);
    }
  }

  private void validateAllDiscountTypesExist(PostAuctionDiscountDTO postAuctionDiscountDTO) {
    List<PostAuctionDiscountSellerDTO> discountSellerDtos =
        Optional.ofNullable(postAuctionDiscountDTO.getDiscountSellers()).orElse(List.of());
    List<PostAuctionDiscountRevenueGroupDTO> discountRevenueGroupDtos =
        Optional.ofNullable(postAuctionDiscountDTO.getDiscountRevenueGroups()).orElse(List.of());

    if (discountSellerDtos.stream()
            .map(PostAuctionDiscountSellerDTO::getType)
            .anyMatch(Objects::isNull)
        || discountRevenueGroupDtos.stream()
            .map(PostAuctionDiscountRevenueGroupDTO::getType)
            .anyMatch(Objects::isNull)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DISCOUNT_TYPE_NOT_FOUND);
    }

    Stream<Long> sellerDiscountTypePids =
        discountSellerDtos.stream()
            .map(PostAuctionDiscountSellerDTO::getType)
            .map(PostAuctionDiscountTypeDTO::getPid);
    Stream<Long> revenueGroupDiscountTypePids =
        discountRevenueGroupDtos.stream()
            .map(PostAuctionDiscountRevenueGroupDTO::getType)
            .map(PostAuctionDiscountTypeDTO::getPid);
    Set<Long> discountTypePids =
        Stream.concat(sellerDiscountTypePids, revenueGroupDiscountTypePids)
            .collect(Collectors.toSet());
    if (postAuctionDiscountTypeRepository.countByPidIn(discountTypePids)
        < discountTypePids.size()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DISCOUNT_TYPE_NOT_FOUND);
    }
  }
}

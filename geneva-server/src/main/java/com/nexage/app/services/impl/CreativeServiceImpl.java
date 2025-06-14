package com.nexage.app.services.impl;

import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BDRTargetGroupCreative;
import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import com.nexage.admin.core.repository.BdrConfigRepository;
import com.nexage.admin.core.repository.BdrCreativeRepository;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.admin.core.repository.BdrTargetGroupRepository;
import com.nexage.app.dto.BidderCreativeDTO;
import com.nexage.app.dto.CreativeFileReferenceDTO;
import com.nexage.app.dto.support.BDRInsertionOrderDTO;
import com.nexage.app.dto.support.BDRLineItemDTO;
import com.nexage.app.dto.support.BDRTargetGroupDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.BdrCreativeDTOMapper;
import com.nexage.app.services.BdrRelationshipValidationService;
import com.nexage.app.services.CreativeService;
import com.nexage.app.services.FileSystemService;
import com.nexage.app.services.impl.support.CreativeImageSaver;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service("creativeService")
@Transactional
public class CreativeServiceImpl implements CreativeService {

  private static final String IURL_SUFFIX = "iurl";
  private static final String SEPARATOR = "/";
  private static final String BIDDER_CREATIVE_HOST = "bidder.creative.host";
  private String creativeHost;
  private String creativeBaseDir;
  private String creativeIurlImageBaseDir;

  private final BdrCreativeRepository bdrCreativeRepository;
  private final BdrTargetGroupRepository targetGroupRepository;
  private final BDRAdvertiserRepository bdrAdvertiserRepository;
  private final GlobalConfigService globalConfigService;
  private final BdrConfigRepository bdrConfigRepository;
  private final BdrInsertionOrderRepository bdrInsertionOrderRepository;
  private final FileSystemService fileSystemService;
  private final BdrRelationshipValidationService relationshipValidator;

  @Autowired
  public CreativeServiceImpl(
      BdrTargetGroupRepository targetGroupRepository,
      BdrCreativeRepository bdrCreativeRepository,
      BDRAdvertiserRepository bdrAdvertiserRepository,
      GlobalConfigService globalConfigService,
      BdrConfigRepository bdrConfigRepository,
      BdrInsertionOrderRepository bdrInsertionOrderRepository,
      FileSystemService fileSystemService,
      BdrRelationshipValidationService relationshipValidator) {
    this.targetGroupRepository = targetGroupRepository;
    this.bdrCreativeRepository = bdrCreativeRepository;
    this.bdrAdvertiserRepository = bdrAdvertiserRepository;
    this.globalConfigService = globalConfigService;
    this.bdrConfigRepository = bdrConfigRepository;
    this.bdrInsertionOrderRepository = bdrInsertionOrderRepository;
    this.fileSystemService = fileSystemService;
    this.relationshipValidator = relationshipValidator;
  }

  @PostConstruct
  public void init() {
    creativeBaseDir = globalConfigService.getStringValue(GlobalConfigProperty.CREATIVE_CONFIG_DIR);
    creativeIurlImageBaseDir = creativeBaseDir + SEPARATOR + IURL_SUFFIX;
    var baseDir = new File(creativeBaseDir);
    if (!baseDir.exists()) {
      if (!baseDir.mkdirs()) {
        throw new RuntimeException(
            String.format(
                "Creative directory path {%s} doesn't exist, creating one failed",
                creativeBaseDir));
      }
    }
    var bdrCreativeHost = bdrConfigRepository.findByProperty(BIDDER_CREATIVE_HOST);
    if (bdrCreativeHost == null || StringUtils.isBlank(bdrCreativeHost.getValue())) {
      throw new RuntimeException("Creative host config is missing");
    }
    creativeHost = bdrCreativeHost.getValue();
    log.info("Creative host: {} Creative dir : {}", creativeHost, creativeBaseDir);
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true and "
          + "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeatHolder())")
  public List<BdrCreative> createBdrCreative(
      List<BidderCreativeDTO> bidderCreatives,
      final long seatholderPid,
      final long insertionorderPID) {
    return bidderCreatives.stream()
        .map(creative -> createBdrCreative(creative, seatholderPid, insertionorderPID))
        .collect(Collectors.toList());
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true and "
          + "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeatHolder())")
  public BdrCreative createBdrCreative(
      BidderCreativeDTO bidderCreative, long seatholderPid, long insertionorderPID) {
    BdrCreative creative = BdrCreativeDTOMapper.MAPPER.map(bidderCreative.getBdrCreativeDTO());
    if (isCreativeNamePresent(seatholderPid, null, creative)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_CREATIVE_NAME);
    }

    BdrInsertionOrder io = getIO(insertionorderPID);
    setAdvertiser(io, creative);

    var created = bdrCreativeRepository.save(creative);
    if (StringUtils.isEmpty(creative.getCustomMarkup())) {
      String bannerUrl =
          storeImagesToDisk(created, bidderCreative.getCreativeFileRef(), seatholderPid);
      creative.setBannerURL(bannerUrl);
    }
    var updated = bdrCreativeRepository.save(created);
    updated.setNexageBannerUrl(creativeHost); // should be bidder host
    return updated;
  }

  @Override
  public String save3rdPartyImage(
      long seatholderPid, long insertionOrderPid, byte[] data, String extension) {
    BdrInsertionOrder io =
        bdrInsertionOrderRepository
            .findById(insertionOrderPid)
            .orElseThrow(() -> new GenevaValidationException(ServerErrorCodes.SERVER_IO_NOT_FOUND));
    var saver = new CreativeImageSaver(creativeIurlImageBaseDir, fileSystemService);
    saver
        .add(seatholderPid)
        .insertSeparator()
        .add(io.getAdvertiser().getPid())
        .insertSeparator()
        .addRandomNumber(24)
        .addExtension(extension);
    saver.write(data);
    return creativeHost + SEPARATOR + IURL_SUFFIX + saver.getRelativePathAsUrl();
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true and "
          + "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeatHolder())")
  public BdrCreative updateBdrCreative(
      BdrCreative creative, long seatholderPid, long insertionorderPID) {
    BdrCreative original =
        bdrCreativeRepository
            .findById(creative.getPid())
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND));

    if (isCreativeNamePresent(seatholderPid, original, creative)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_DUPLICATE_CREATIVE_NAME);
    }

    original.setIndicativeURL(creative.getIndicativeURL());
    original.setLandingURL(creative.getLandingURL());
    original.setTrackingURL(creative.getTrackingURL());
    original.setName(creative.getName());

    if (creative.getCustomMarkup() != null) { // these are updatable for custom markups
      original.setCustomMarkup(creative.getCustomMarkup());
      original.setHeight(creative.getHeight());
      original.setWidth(creative.getWidth());
      original.setMraidCompliance(creative.getMraidCompliance());
    }
    var updated = bdrCreativeRepository.save(original);
    updated.setNexageBannerUrl(creativeHost);
    return updated;
  }

  @Override
  @PreAuthorize(
      "@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true and "
          + "(@loginUserContext.isOcManagerNexage() or @loginUserContext.isOcManagerSeatHolder())")
  public BdrCreative addCreativeToTargetGroup(
      BdrCreative creative,
      long seatholderPid,
      long targetGroupPid,
      long insertionorderPID,
      long lineitemPID) {
    BdrInsertionOrder io = getIO(insertionorderPID);
    BdrTargetGroup targetGroup = getTargetGroup(io, lineitemPID, targetGroupPid);
    targetGroup.addToCreatives(creative);
    setUpdateDate(targetGroup);
    targetGroupRepository.save(targetGroup);
    return creative;
  }

  private BdrInsertionOrder getIO(final long ioPid) {
    return bdrInsertionOrderRepository
        .findById(ioPid)
        .orElseThrow(() -> new GenevaValidationException(ServerErrorCodes.SERVER_IO_NOT_FOUND));
  }

  private BdrTargetGroup getTargetGroup(
      BdrInsertionOrder io, final Long lineItemPid, final Long targetgroupPid) {
    BDRLineItem lineItem =
        (BDRLineItem)
            CollectionUtils.find(
                io.getLineItems(),
                arg0 -> {
                  BDRLineItem li = (BDRLineItem) arg0;
                  return (lineItemPid.equals(li.getPid()));
                });

    if (lineItem == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_LINEITEM_NOT_FOUND);
    }
    BdrTargetGroup targetGroup =
        (BdrTargetGroup)
            CollectionUtils.find(
                lineItem.getTargetGroups(),
                arg0 -> {
                  BdrTargetGroup tg = (BdrTargetGroup) arg0;
                  return (targetgroupPid.equals(tg.getPid()));
                });
    if (targetGroup == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND);
    }
    return targetGroup;
  }

  private void setUpdateDate(BdrTargetGroup targetGroup) {
    Date now = Calendar.getInstance().getTime();
    targetGroup.setUpdatedOn(now);
    targetGroup.getLineItem().setUpdatedOn(now);
    targetGroup.getLineItem().getInsertionOrder().setUpdatedOn(now);
  }

  private void setAdvertiser(BdrInsertionOrder io, BdrCreative creative) {
    BDRAdvertiser advertiser =
        bdrAdvertiserRepository
            .findById(io.getAdvertiserPid())
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_ADVERTISER_NOT_FOUND));
    creative.setAdvertiser(advertiser);
  }

  private String storeImagesToDisk(
      BdrCreative creative, CreativeFileReferenceDTO creativeFileReference, Long seatPid) {
    String relativeUrl = getRelativeCreativePath(creative, creativeFileReference, seatPid);
    fileSystemService.write(creativeBaseDir, relativeUrl, creativeFileReference.getImage());
    return relativeUrl;
  }

  private String getRelativeCreativePath(
      BdrCreative creative, CreativeFileReferenceDTO fileReference, Long seatPid) {
    return SEPARATOR
        + seatPid
        + SEPARATOR
        + creative.getAdvertiser().getPid()
        + SEPARATOR
        + creative.getPid()
        + "_"
        + creative.getWidth()
        + "x"
        + creative.getHeight()
        + "."
        + fileReference.getExtension();
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public Set<BdrCreative> getCreativesForTargetgroup(
      long seatholderPid, long insertionOrderPid, long lineItemPid, long targetGroupPid) {
    relationshipValidator.validateRelationship(
        seatholderPid, insertionOrderPid, lineItemPid, targetGroupPid);
    BdrTargetGroup targetgroup =
        targetGroupRepository
            .findById(targetGroupPid)
            .orElseThrow(
                () -> {
                  log.info("TargetGroup not found in database: " + targetGroupPid);
                  return new GenevaValidationException(
                      ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND);
                });
    Set<BDRTargetGroupCreative> targetGroupCreatives = targetgroup.getTargetGroupCreatives();
    for (BDRTargetGroupCreative targetGroupCreative : targetGroupCreatives) {
      BdrCreative creative = targetGroupCreative.getCreative();
      creative.setNexageBannerUrl(creativeHost);
    }
    return targetgroup.getCreatives();
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public Set<BdrCreative> getCreativesForAdvertiser(long seatholderPid, long advertiserPid) {
    relationshipValidator.validateRelationship(seatholderPid, advertiserPid);
    Set<BdrCreative> creatives =
        new HashSet<>(bdrCreativeRepository.findActiveByAdvertiserPid(advertiserPid));
    for (BdrCreative creative : creatives) {
      creative.setNexageBannerUrl(creativeHost);
    }
    return creatives;
  }

  @Override
  public Set<BdrCreative> getCreativesForAdvertiser(
      long seatholderPid, long advertiserPid, Set<Long> creativePids) {
    relationshipValidator.validateRelationship(seatholderPid, advertiserPid);
    return bdrCreativeRepository.findActiveByAdvertiserPidAndPidInCreativePids(
        advertiserPid, creativePids);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public BdrCreative getCreative(
      long seatholderPid,
      long insertionOrderPid,
      long lineitemPid,
      long targetGroupPid,
      long creativePid) {
    relationshipValidator.validateRelationship(
        seatholderPid, insertionOrderPid, lineitemPid, targetGroupPid);

    BdrCreative creative =
        bdrCreativeRepository
            .findById(creativePid)
            .orElseThrow(
                () -> {
                  log.info("Creative not found in database: " + creativePid);
                  return new GenevaValidationException(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND);
                });
    creative.setNexageBannerUrl(creativeHost);
    return creative;
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public Set<BDRInsertionOrderDTO> getCreativeUsages(
      long seatholderPid, long advertiserPid, long creativePid) {
    relationshipValidator.validateRelationship(seatholderPid, advertiserPid);

    BdrCreative creative =
        bdrCreativeRepository
            .findById(creativePid)
            .orElseThrow(
                () -> {
                  log.info("Creative not found in database: " + creativePid);
                  return new GenevaValidationException(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND);
                });

    return getCreativeUsages(creative);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public void addCreativesToTargetGroup(
      long seatholderPid,
      long insertionOrderPid,
      long lineitemPid,
      long targetGroupPid,
      List<Long> creativePids) {
    relationshipValidator.validateRelationship(
        seatholderPid, insertionOrderPid, lineitemPid, targetGroupPid);
    BdrTargetGroup targetGroup =
        targetGroupRepository
            .findById(targetGroupPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND));
    for (Long creativePid : creativePids) {
      BdrCreative creative =
          bdrCreativeRepository
              .findById(creativePid)
              .orElseThrow(
                  () -> new GenevaValidationException(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND));
      BDRTargetGroupCreative targetGroupCreative = new BDRTargetGroupCreative();
      targetGroupCreative.setCreative(creative);
      targetGroupCreative.setTargetGroup(targetGroup);
      targetGroup.getTargetGroupCreatives().add(targetGroupCreative);
    }
    setUpdateDate(targetGroup);
    targetGroupRepository.save(targetGroup);
  }

  @Override
  @PreAuthorize("@loginUserContext.doSameOrNexageAffiliation(#seatholderPid) == true")
  public void removeCreativesFromTargetGroup(
      long seatholderPid,
      long insertionOrderPid,
      long lineitemPid,
      long targetGroupPid,
      List<Long> creativePids) {
    relationshipValidator.validateRelationship(
        seatholderPid, insertionOrderPid, lineitemPid, targetGroupPid);
    BdrTargetGroup targetGroup =
        targetGroupRepository
            .findById(targetGroupPid)
            .orElseThrow(
                () ->
                    new GenevaValidationException(ServerErrorCodes.SERVER_TARGET_GROUP_NOT_FOUND));
    targetGroup
        .getTargetGroupCreatives()
        .removeIf(
            targetGroupCreative ->
                creativePids.contains(targetGroupCreative.getCreative().getPid()));
    setUpdateDate(targetGroup);
    targetGroupRepository.save(targetGroup);
  }

  /**
   * Check whether a name of create is used or not.
   *
   * @param seatholderId a seatholder pid
   * @param existing - an existing creative
   * @param updated - a new creative
   * @return true if name of update
   */
  private boolean isCreativeNamePresent(
      long seatholderId, BdrCreative existing, BdrCreative updated) {

    String oldName = existing != null ? existing.getName() : StringUtils.EMPTY;
    String newName = updated.getName();

    if (StringUtils.isNotEmpty(newName)
        && StringUtils.equalsIgnoreCase(newName, oldName)) { // name wasn't changed
      return false;
    } else {
      return bdrCreativeRepository.existsByNameAndAdvertiserCompanyPid(newName, seatholderId);
    }
  }

  private Set<BDRInsertionOrderDTO> getCreativeUsages(BdrCreative creative) {
    Set<BdrInsertionOrder> insertionOrderUsages = new HashSet<>();
    Set<BDRLineItem> lineItemUsages = new HashSet<>();
    Set<BdrTargetGroup> targetGroupUsages = creative.getTargetGroups();

    for (BdrTargetGroup targetGroup : targetGroupUsages) {
      BDRLineItem lineItem = targetGroup.getLineItem();
      lineItemUsages.add(lineItem);
      insertionOrderUsages.add(lineItem.getInsertionOrder());
    }
    return getInsertionOrderUsages(insertionOrderUsages, lineItemUsages, targetGroupUsages);
  }

  private Set<BDRTargetGroupDTO> getTargetGroupUsagesForLineItem(
      BDRLineItem lineItem, Set<BdrTargetGroup> allTargetGroupUsages) {
    Set<BDRTargetGroupDTO> targetGroupDtoUsages = new HashSet<>();
    for (BdrTargetGroup targetGroup : lineItem.getTargetGroups()) {
      if (allTargetGroupUsages.contains(targetGroup)) {
        targetGroupDtoUsages.add(
            (BDRTargetGroupDTO)
                BDRTargetGroupDTO.newBuilder()
                    .withName(targetGroup.getName())
                    .withPid(targetGroup.getPid())
                    .build());
      }
    }
    return targetGroupDtoUsages;
  }

  private Set<BDRLineItemDTO> getLineItemUsagesForInsertionOrder(
      BdrInsertionOrder insertionOrder,
      Set<BDRLineItem> allLineItemUsages,
      Set<BdrTargetGroup> allTargetGroupUsages) {
    Set<BDRLineItemDTO> lineItemDtoUsages = new HashSet<>();
    for (BDRLineItem lineItem : insertionOrder.getLineItems()) {
      Set<BDRTargetGroupDTO> targetGroupDtoUsages =
          getTargetGroupUsagesForLineItem(lineItem, allTargetGroupUsages);
      if (allLineItemUsages.contains(lineItem) && !targetGroupDtoUsages.isEmpty()) {
        lineItemDtoUsages.add(
            (BDRLineItemDTO)
                BDRLineItemDTO.newBuilder()
                    .withTargetGroups(targetGroupDtoUsages)
                    .withName(lineItem.getName())
                    .withPid(lineItem.getPid())
                    .build());
      }
    }
    return lineItemDtoUsages;
  }

  private Set<BDRInsertionOrderDTO> getInsertionOrderUsages(
      Set<BdrInsertionOrder> allInsertionOrderUsages,
      Set<BDRLineItem> allLineItemUsages,
      Set<BdrTargetGroup> allTargetGroupUsages) {
    Set<BDRInsertionOrderDTO> insertionOrderDtoUsages = new HashSet<>();
    for (BdrInsertionOrder insertionOrder : allInsertionOrderUsages) {
      Set<BDRLineItemDTO> lineItemDtoUsages =
          getLineItemUsagesForInsertionOrder(
              insertionOrder, allLineItemUsages, allTargetGroupUsages);
      insertionOrderDtoUsages.add(
          (BDRInsertionOrderDTO)
              BDRInsertionOrderDTO.newBuilder()
                  .withLineItems(lineItemDtoUsages)
                  .withName(insertionOrder.getName())
                  .withPid(insertionOrder.getPid())
                  .build());
    }
    return insertionOrderDtoUsages;
  }
}

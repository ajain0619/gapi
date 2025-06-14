package com.nexage.app.web;

import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.app.dto.BidderCreativeDTO;
import com.nexage.app.dto.CreativeFileReferenceDTO;
import com.nexage.app.dto.support.BDRInsertionOrderDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.CreativeService;
import com.nexage.app.services.InsertionOrderService;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Tag(name = "/seatholders")
@RestController
@RequestMapping(value = "/seatholders")
@RequiredArgsConstructor
public class CreativeController {

  private final CreativeService creativeService;

  private final InsertionOrderService insertionOrderService;

  @Deprecated
  @PutMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseBody
  public BdrCreative createBdrCreative(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "insertionorderPID") long insertionorderPID,
      @PathVariable(value = "lineitemPID") long lineitemPID,
      @PathVariable(value = "targetgroupPID") long targetGroupPid,
      @RequestBody BidderCreativeDTO creative) {
    if (seatholderPid <= 0
        || insertionorderPID <= 0
        || lineitemPID <= 0
        || targetGroupPid <= 0
        || creative == null
        || creative.getBdrCreativeDTO() == null
        || (StringUtils.isEmpty(creative.getBdrCreativeDTO().getCustomMarkup())
            && (creative.getCreativeFileRef() == null
                || creative.getCreativeFileRef().getImage().length == 0
                || StringUtils.isEmpty(creative.getCreativeFileRef().getExtension())))
        || (StringUtils.isNotEmpty(creative.getBdrCreativeDTO().getCustomMarkup())
            && creative.getCreativeFileRef() != null)) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    var created = creativeService.createBdrCreative(creative, seatholderPid, insertionorderPID);
    creativeService.addCreativeToTargetGroup(
        created, seatholderPid, targetGroupPid, insertionorderPID, lineitemPID);
    return created;
  }

  @Deprecated
  @PutMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives/{creativePID}",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  @ResponseBody
  public BdrCreative updateBdrCreative(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "insertionorderPID") long insertionorderPID,
      @PathVariable(value = "lineitemPID") long lineitemPID,
      @PathVariable(value = "targetgroupPID") long targetGroupPid,
      @PathVariable(value = "creativePID") long creativePID,
      @RequestBody BdrCreative creative) {
    if (seatholderPid <= 0
        || insertionorderPID <= 0
        || lineitemPID <= 0
        || targetGroupPid <= 0
        || creativePID <= 0
        || creative == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    return creativeService.updateBdrCreative(creative, seatholderPid, insertionorderPID);
  }

  @GetMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives")
  @ResponseBody
  public Set<BdrCreative> getCreativesForTargetGroup(
      @PathVariable(value = "seatholderPID") long seatPid,
      @PathVariable(value = "insertionorderPID") long insertionorderPID,
      @PathVariable(value = "lineitemPID") long lineitemPID,
      @PathVariable(value = "targetgroupPID") long targetGroupPid) {
    if (seatPid <= 0 || insertionorderPID <= 0 || lineitemPID <= 0 || targetGroupPid <= 0)
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    return creativeService.getCreativesForTargetgroup(
        seatPid, insertionorderPID, lineitemPID, targetGroupPid);
  }

  @Deprecated
  @GetMapping(value = "/{seatholderPID}/advertisers/{advertiserPid}/creatives")
  @ResponseBody
  public Set<BdrCreative> getCreativesForAdvertiser(
      @PathVariable(value = "seatholderPID") long seatPid,
      @PathVariable(value = "advertiserPid") long advertiserPid) {
    if (seatPid <= 0 || advertiserPid <= 0) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);
    }
    return creativeService.getCreativesForAdvertiser(seatPid, advertiserPid);
  }

  @Deprecated
  @GetMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives/{creativePID}")
  @ResponseBody
  public BdrCreative getCreative(
      @PathVariable(value = "seatholderPID") long seatPid,
      @PathVariable(value = "insertionorderPID") long insertionorderPID,
      @PathVariable(value = "lineitemPID") long lineitemPID,
      @PathVariable(value = "targetgroupPID") long targetGroupPid,
      @PathVariable(value = "creativePID") long creativePID) {
    if (seatPid <= 0
        || insertionorderPID <= 0
        || lineitemPID <= 0
        || targetGroupPid <= 0
        || creativePID <= 0)
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_INPUT);

    return creativeService.getCreative(
        seatPid, insertionorderPID, lineitemPID, targetGroupPid, creativePID);
  }

  @Deprecated
  @PutMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives/{creativePIDs}/add")
  @ResponseBody
  public void addCreativesToTargetGroup(
      @PathVariable(value = "seatholderPID") long seatPid,
      @PathVariable(value = "insertionorderPID") long insertionorderPID,
      @PathVariable(value = "lineitemPID") long lineitemPID,
      @PathVariable(value = "targetgroupPID") long targetGroupPid,
      @PathVariable(value = "creativePIDs") List<Long> creativePids) {
    creativeService.addCreativesToTargetGroup(
        seatPid, insertionorderPID, lineitemPID, targetGroupPid, creativePids);
  }

  @PutMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/lineitems/{lineitemPID}/targetgroups/{targetgroupPID}/creatives/delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeCreativesFromTargetGroup(
      @PathVariable(value = "seatholderPID") long seatPid,
      @PathVariable(value = "insertionorderPID") long insertionorderPID,
      @PathVariable(value = "lineitemPID") long lineitemPID,
      @PathVariable(value = "targetgroupPID") long targetGroupPid,
      @RequestBody List<Long> creativePids) {
    creativeService.removeCreativesFromTargetGroup(
        seatPid, insertionorderPID, lineitemPID, targetGroupPid, creativePids);
  }

  /**
   * Returns all creatives which belong to given insertion order.
   *
   * @param seatholderPid a company pid
   * @param insertionorderPID an insertion order pid
   * @return a set of creatives.
   */
  @ResponseBody
  @GetMapping(value = "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/creatives")
  public Set<BdrCreative> getCreatives(
      @PathVariable("seatholderPID") Long seatholderPid,
      @PathVariable("insertionorderPID") Long insertionorderPID) {
    BdrInsertionOrder insertionOrder =
        insertionOrderService.getInsertionOrder(seatholderPid, insertionorderPID);
    if (insertionOrder == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_IO_NOT_FOUND);
    }
    return creativeService.getCreativesForAdvertiser(
        seatholderPid, insertionOrder.getAdvertiserPid());
  }

  /**
   * Returns tree structure which represents useges for given creative.
   *
   * @param seatholderPid a company pid
   * @param insertionorderPID an insertion order pid
   * @param creativePID a creative pid
   * @return a set of insertion orders and their children.
   */
  @ResponseBody
  @GetMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionorderPID}/creatives/{creativePID}/usages")
  public Set<BDRInsertionOrderDTO> getCreativeUsages(
      @PathVariable("seatholderPID") Long seatholderPid,
      @PathVariable("insertionorderPID") Long insertionorderPID,
      @PathVariable("creativePID") Long creativePID) {
    BdrInsertionOrder insertionOrder =
        insertionOrderService.getInsertionOrder(seatholderPid, insertionorderPID);
    if (insertionOrder == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_IO_NOT_FOUND);
    }
    return creativeService.getCreativeUsages(
        seatholderPid, insertionOrder.getAdvertiserPid(), creativePID);
  }

  /**
   * Creates new creatives and link them to given advertiser.
   *
   * @param seatholderPid a company pid
   * @param insertionOrderPid an insertion order pid
   * @param creatives a list of creatives.
   * @return a list of created creatives.
   */
  @PutMapping(value = "/{seatholderPID}/advertisers/insertionorders/{insertionOrderPID}/creatives")
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public List<BdrCreative> createCreative(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "insertionOrderPID") long insertionOrderPid,
      @RequestBody List<BidderCreativeDTO> creatives) {

    BdrInsertionOrder insertionOrder =
        insertionOrderService.getInsertionOrder(seatholderPid, insertionOrderPid);
    if (insertionOrder == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_IO_NOT_FOUND);
    }
    return creativeService.createBdrCreative(creatives, seatholderPid, insertionOrderPid);
  }

  /**
   * Creates an embeddable int 3rd party tag image.
   *
   * @param seatholderPid a company pid
   * @param insertionOrderPid an insertion order pid
   * @param creative a creative.
   * @return an absolute url.
   */
  @PutMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionOrderPID}/creatives/3rdpartyimage")
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public String create3rdPartyImage(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "insertionOrderPID") long insertionOrderPid,
      @RequestBody CreativeFileReferenceDTO creative) {
    return creativeService.save3rdPartyImage(
        seatholderPid, insertionOrderPid, creative.getImage(), creative.getExtension());
  }

  /**
   * Updates an existing creative.
   *
   * @param seatholderPid
   * @param insertionOrderPid
   * @param creativePID
   * @param creative
   * @return an update creative
   */
  @PutMapping(
      value =
          "/{seatholderPID}/advertisers/insertionorders/{insertionOrderPID}/creatives/{creativePID}")
  @ResponseBody
  public BdrCreative updateCreative(
      @PathVariable(value = "seatholderPID") long seatholderPid,
      @PathVariable(value = "insertionOrderPID") long insertionOrderPid,
      @PathVariable(value = "creativePID") long creativePID,
      @RequestBody BdrCreative creative) {

    BdrInsertionOrder insertionOrder =
        insertionOrderService.getInsertionOrder(seatholderPid, insertionOrderPid);
    if (insertionOrder == null) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_IO_NOT_FOUND);
    }

    if (creativePID != creative.getPid()) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
    }
    return creativeService.updateBdrCreative(creative, seatholderPid, insertionOrderPid);
  }
}

package com.nexage.app.services;

import com.nexage.admin.core.bidder.model.BdrCreative;
import com.nexage.app.dto.BidderCreativeDTO;
import com.nexage.app.dto.support.BDRInsertionOrderDTO;
import java.util.List;
import java.util.Set;

public interface CreativeService {

  List<BdrCreative> createBdrCreative(
      List<BidderCreativeDTO> bidderCreative, long seatholderPid, long insertionorderPID);

  BdrCreative createBdrCreative(
      BidderCreativeDTO bidderCreative, long seatholderPid, long insertionorderPID);

  /**
   * Creates save a image used in 3rd party tag markup.
   *
   * @param seatholderPid
   * @param insertionOrderPid
   * @param data
   * @param extension
   * @return an absolute url.
   */
  String save3rdPartyImage(
      long seatholderPid, long insertionOrderPid, byte[] data, String extension);

  BdrCreative updateBdrCreative(BdrCreative creative, long seatholderPid, long insertionorderPID);

  BdrCreative addCreativeToTargetGroup(
      BdrCreative creative,
      long seatholderPid,
      long targetGroupPid,
      long insertionorderPID,
      long lineitemPID);

  Set<BdrCreative> getCreativesForTargetgroup(
      long seatholderPid, long insertionOrderPid, long lineitemPid, long targetGroupPid);

  Set<BdrCreative> getCreativesForAdvertiser(long seatholderPid, long advertiserPid);

  Set<BdrCreative> getCreativesForAdvertiser(
      long seatholderPid, long advertiserPid, Set<Long> creativePids);

  BdrCreative getCreative(
      long seatholderPid,
      long insertionOrderPid,
      long lineitemPid,
      long targetGroupPid,
      long creativePid);

  /** Retrieve information about creative usages */
  Set<BDRInsertionOrderDTO> getCreativeUsages(
      long seatholderPid, long advertiserPid, long creativePid);

  void addCreativesToTargetGroup(
      long seatholderPid,
      long insertionOrderPid,
      long lineitemPid,
      long targetGroupPid,
      List<Long> creativePids);

  void removeCreativesFromTargetGroup(
      long seatholderPid,
      long insertionOrderPid,
      long lineitemPid,
      long targetGroupPid,
      List<Long> creativePids);
}

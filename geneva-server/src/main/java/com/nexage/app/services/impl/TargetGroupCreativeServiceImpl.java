package com.nexage.app.services.impl;

import com.google.common.collect.Maps;
import com.nexage.admin.core.bidder.model.BDRLineItem;
import com.nexage.admin.core.bidder.model.BDRTargetGroupCreative;
import com.nexage.admin.core.bidder.model.BdrTargetGroup;
import com.nexage.admin.core.bidder.model.HasCreativeWeight;
import com.nexage.admin.core.repository.BdrCreativeRepository;
import com.nexage.app.dto.support.AssociatedCreativeDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.TargetGroupCreativeService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Eugeny Yurko
 * @since 22.10.2014
 */
@RequiredArgsConstructor
@Transactional
@Service("targetGroupCreativeService")
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeatHolder()")
public class TargetGroupCreativeServiceImpl implements TargetGroupCreativeService {

  private final BdrCreativeRepository bdrCreativeRepository;

  @Override
  public void associateCreatives(
      long seatholderPid,
      BDRLineItem lineItem,
      final BdrTargetGroup targetGroup,
      Set<AssociatedCreativeDTO> associatedCreatives) {

    Map<Long, HasCreativeWeight> newAssociationMap = buildAssociationMap(associatedCreatives);
    Map<Long, HasCreativeWeight> existingAssociationMap = new HashMap<>();

    Iterator<BDRTargetGroupCreative> it = targetGroup.getTargetGroupCreatives().iterator();
    BDRTargetGroupCreative associatedCreative;

    // remove non existed or update weight
    while (it.hasNext()) {
      associatedCreative = it.next();
      HasCreativeWeight s = newAssociationMap.get(associatedCreative.getCreativePid());
      if (s == null) {
        it.remove();
      } else {
        if (associatedCreative.getWeight() != s.getWeight()) {
          associatedCreative.setWeight(s.getWeight());
        }
      }
      existingAssociationMap.put(
          associatedCreative.getCreativePid(), associatedCreative); // save for the next operation;
    }

    // new associated
    for (AssociatedCreativeDTO ac : associatedCreatives) {
      if (existingAssociationMap.get(ac.getCreativePid()) == null) {

        BDRTargetGroupCreative targetGroupCreative = new BDRTargetGroupCreative();
        targetGroupCreative.setCreative(
            bdrCreativeRepository
                .findByPidAndAdvertiser_Pid(
                    ac.getPid(), lineItem.getInsertionOrder().getAdvertiserPid())
                .orElseThrow(
                    () ->
                        new GenevaValidationException(ServerErrorCodes.SERVER_CREATIVE_NOT_FOUND)));
        targetGroupCreative.setWeight(ac.getWeight());
        targetGroupCreative.setTargetGroup(targetGroup);
        targetGroup.getTargetGroupCreatives().add(targetGroupCreative);
      }
    }
  }

  private Map<Long, HasCreativeWeight> buildAssociationMap(
      Set<? extends HasCreativeWeight> hasCreativeWeights) {
    Map<Long, HasCreativeWeight> result = Maps.newHashMap();
    for (HasCreativeWeight hasCreativeWeight : hasCreativeWeights) {
      result.put(hasCreativeWeight.getCreativePid(), hasCreativeWeight);
    }
    return result;
  }
}

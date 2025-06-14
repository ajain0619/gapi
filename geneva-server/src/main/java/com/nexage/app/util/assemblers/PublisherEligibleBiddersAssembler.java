package com.nexage.app.util.assemblers;

import com.nexage.admin.core.model.SellerEligibleBidders;
import com.nexage.admin.core.repository.RtbProfileGroupRepository;
import com.nexage.app.dto.publisher.PublisherBidderDTO;
import com.nexage.app.dto.publisher.PublisherEligibleBiddersDTO;
import com.nexage.app.util.assemblers.context.CompanyContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.hibernate.StaleStateException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublisherEligibleBiddersAssembler
    extends Assembler<
        Set<PublisherEligibleBiddersDTO>, Set<SellerEligibleBidders>, CompanyContext> {

  public static final Set<String> DEFAULT_FIELDS = Set.of("pid", "version", "siteType", "bidders");

  private final RtbProfileGroupRepository rtbProfileGroupRepository;

  @Override
  public Set<PublisherEligibleBiddersDTO> make(
      CompanyContext context, Set<SellerEligibleBidders> model) {
    return make(context, model, DEFAULT_FIELDS);
  }

  @Override
  public Set<PublisherEligibleBiddersDTO> make(
      CompanyContext context, Set<SellerEligibleBidders> model, Set<String> fields) {
    if (model == null) return null;

    if (model.isEmpty()) return Collections.emptySet();

    Set<PublisherEligibleBiddersDTO> eligibleBidders = new HashSet<>();
    for (SellerEligibleBidders entry : model) {
      PublisherEligibleBiddersDTO.Builder builder = PublisherEligibleBiddersDTO.newBuilder();

      for (String field : fields) {
        switch (field) {
          case "pid":
            builder.withPid(entry.getPid());
            break;
          case "version":
            builder.withVersion(entry.getVersion());
            break;
          case "bidders":
            builder.withBidders(entry.getEligibleBidderGroups());
            break;
        }
      }

      eligibleBidders.add(builder.build());
    }

    return eligibleBidders;
  }

  @Override
  public Set<SellerEligibleBidders> apply(
      CompanyContext context,
      Set<SellerEligibleBidders> model,
      Set<PublisherEligibleBiddersDTO> dto) {

    for (final PublisherEligibleBiddersDTO peb : dto) {
      Set<Long> pids = new HashSet<>();
      for (PublisherBidderDTO pubBidder : peb.getBidderGroups()) {
        if (rtbProfileGroupRepository.existsById(pubBidder.getPid())) {
          pids.add(pubBidder.getPid());
        } else {
          throw new IllegalArgumentException(
              String.format("Bidder Group PID %s not found.", pubBidder.getPid()));
        }
      }

      boolean existing = false;
      // Update
      for (SellerEligibleBidders seb : model) {
        if (Objects.equals(seb.getPid(), peb.getPid())) {
          if (!Objects.equals(seb.getVersion(), peb.getVersion()))
            throw new StaleStateException("Publisher Eligible Bidders have wrong version.");

          existing = true;
          seb.setEligibleBidderGroups(pids);
        }
      }

      // Create
      if (!existing) {
        SellerEligibleBidders seb = new SellerEligibleBidders();
        seb.setVersion(0);
        seb.setEligibleBidderGroups(pids);
        seb.setPublisher(context.getCompany());
        model.add(seb);
      }
    }

    // Delete
    if (model.size() != dto.size()) {
      Iterator<SellerEligibleBidders> it = model.iterator();
      while (it.hasNext()) {
        SellerEligibleBidders seb = it.next();
        boolean exists = false;
        for (PublisherEligibleBiddersDTO peb : dto) {
          exists = exists || Objects.equals(seb.getPid(), peb.getPid());
        }

        if (!exists) {
          it.remove();
        }
      }
    }

    return model;
  }
}

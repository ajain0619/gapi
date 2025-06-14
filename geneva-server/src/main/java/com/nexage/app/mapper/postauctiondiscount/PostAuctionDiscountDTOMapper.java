package com.nexage.app.mapper.postauctiondiscount;

import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscount;
import com.nexage.admin.core.model.postauctiondiscount.PostAuctionDiscountDspSeat;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountIntermediaryDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostAuctionDiscountDTOMapper {

  PostAuctionDiscountDTOMapper MAPPER = Mappers.getMapper(PostAuctionDiscountDTOMapper.class);

  default PostAuctionDiscountDTO map(PostAuctionDiscount postAuctionDiscount) {
    PostAuctionDiscountDTO.PostAuctionDiscountDTOBuilder builder =
        PostAuctionDiscountDTO.builder()
            .pid(postAuctionDiscount.getPid())
            .discountName(postAuctionDiscount.getDiscountName())
            .discountDescription(postAuctionDiscount.getDiscountDescription())
            .discountPercent(postAuctionDiscount.getDiscountPercent())
            .discountStatus(postAuctionDiscount.getDiscountStatus());

    Map<Long, List<PostAuctionDiscountDspSeat>> dspMap = new HashMap<>();
    postAuctionDiscount
        .getDsps()
        .forEach(
            dsp -> {
              boolean didNotFindPid = true;

              for (Map.Entry<Long, List<PostAuctionDiscountDspSeat>> b : dspMap.entrySet()) {
                if (dsp.getDsp().getCompany().getPid().equals(b.getKey())) {
                  List<PostAuctionDiscountDspSeat> updatedList = new ArrayList<>(b.getValue());
                  updatedList.add(dsp);
                  b.setValue(updatedList);
                  didNotFindPid = false;
                  break;
                }
              }
              if (didNotFindPid) {
                dspMap.put(dsp.getDsp().getCompany().getPid(), Arrays.asList(dsp));
              }
            });
    builder =
        builder
            .openAuctionEnabled(postAuctionDiscount.getOpenAuctionEnabled())
            .dealsSelected(
                postAuctionDiscount.getDealsSelected() == null
                    ? PostAuctionDealsSelected.ALL
                    : postAuctionDiscount.getDealsSelected())
            .version(postAuctionDiscount.getVersion())
            .discountSellers(
                postAuctionDiscount.getSellers().stream()
                    .map(PostAuctionDiscountSellerDTOMapper.MAPPER::map)
                    .toList())
            .discountRevenueGroups(
                postAuctionDiscount.getRevenueGroups().stream()
                    .map(PostAuctionDiscountRevenueGroupDTOMapper.MAPPER::map)
                    .toList())
            .discountDSPs(
                dspMap.entrySet().stream()
                    .map(Map.Entry::getValue)
                    .map(PostAuctionDiscountIntermediaryDTO::new)
                    .map(PostAuctionDiscountDspDTOMapper.MAPPER::map)
                    .toList())
            .discountDeals(
                postAuctionDiscount.getDeals() != null
                    ? postAuctionDiscount.getDeals().stream()
                        .map(DealPostAuctionDiscountDTOMapper.MAPPER::map)
                        .toList()
                    : null);
    return builder.build();
  }

  default PostAuctionDiscount map(
      PostAuctionDiscountDTO postAuctionDiscountDTO,
      PostAuctionDiscount discount,
      EntityManager entityManager) {

    discount.setPid(postAuctionDiscountDTO.getPid());
    discount.setDiscountName(postAuctionDiscountDTO.getDiscountName());
    discount.setDiscountStatus(postAuctionDiscountDTO.getDiscountStatus());
    discount.setDiscountPercent(postAuctionDiscountDTO.getDiscountPercent());
    discount.setDiscountDescription(postAuctionDiscountDTO.getDiscountDescription());
    discount.setOpenAuctionEnabled(postAuctionDiscountDTO.getOpenAuctionEnabled());
    discount.setDealsSelected(postAuctionDiscountDTO.getDealsSelected());
    discount.setVersion(postAuctionDiscountDTO.getVersion());

    discount.setDsps(
        PostAuctionDiscountDspDTOMapper.MAPPER.map(
            postAuctionDiscountDTO.getDiscountDSPs(), discount, entityManager));

    discount.setSellers(
        postAuctionDiscountDTO.getDiscountSellers() == null
            ? List.of()
            : PostAuctionDiscountSellerDTOMapper.MAPPER.map(
                postAuctionDiscountDTO.getDiscountSellers(), discount, entityManager));

    discount.setRevenueGroups(
        postAuctionDiscountDTO.getDiscountRevenueGroups() == null
            ? List.of()
            : PostAuctionDiscountRevenueGroupDTOMapper.MAPPER.map(
                postAuctionDiscountDTO.getDiscountRevenueGroups(), discount, entityManager));

    if (postAuctionDiscountDTO.getDiscountDeals() != null) {
      discount.setDeals(
          DealPostAuctionDiscountDTOMapper.MAPPER.map(
              postAuctionDiscountDTO.getDiscountDeals(), discount));
    }

    return discount;
  }
}

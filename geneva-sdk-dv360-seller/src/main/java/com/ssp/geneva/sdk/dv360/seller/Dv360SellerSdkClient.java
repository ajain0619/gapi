package com.ssp.geneva.sdk.dv360.seller;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.config.Dv360SellerSdkConfigProperties;
import com.ssp.geneva.sdk.dv360.seller.repository.AuctionPackageRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.OrderRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.ProductRepository;
import java.text.SimpleDateFormat;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Getter
@Builder
public class Dv360SellerSdkClient {
  @NonNull @NotNull private final Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties;
  @NonNull @NotNull private final RestTemplate dv360SellerRestTemplate;
  @NonNull @NotNull private final GoogleCredentials googleCredentials;
  @NonNull @NotNull private final DoubleClickBidManager doubleClickBidManager;
  @NonNull @NotNull private final AuctionPackageRepository auctionPackageRepository;
  @NonNull @NotNull private final OrderRepository orderRepository;
  @NonNull @NotNull private final ProductRepository productRepository;
  @NonNull @NotNull private final SimpleDateFormat dv360DateFormat;
}

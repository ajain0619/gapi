package com.nexage.app.web.mockwebcontext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.PostAuctionDealsSelected;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DealSiteRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PostAuctionDiscountRepository;
import com.nexage.admin.core.repository.PostAuctionDiscountTypeRepository;
import com.nexage.admin.core.repository.RevenueGroupRepository;
import com.nexage.admin.core.repository.RuleTargetRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SiteViewRepository;
import com.nexage.app.dto.postauctiondiscount.DirectDealViewDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountDspSeatDTO;
import com.nexage.app.dto.postauctiondiscount.PostAuctionDiscountSellerDTO;
import com.nexage.app.services.PostAuctionDiscountService;
import com.nexage.app.services.impl.PostAuctionDiscountServiceImpl;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.web.ControllerExceptionHandler;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"geneva.server.login=sso"})
@ContextConfiguration(
    locations = {
      "classpath:application-context-test.xml",
      "classpath:application-context-api-security.xml"
    })
@WebAppConfiguration
class PostAuctionDiscountControllerIT {

  private static final String URL_TEMPLATE = "/v1/post-auction-discounts";

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext wac;

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;

  @Autowired private FilterChainProxy springSecurityFilterChain;

  private ObjectMapper mapper = new CustomObjectMapper();
  private SpringUserDetails userDetails;

  @Autowired
  @Qualifier("dealRepository")
  private DirectDealRepository dealRepository;

  @Autowired
  @Qualifier("siteViewRepository")
  private SiteViewRepository siteViewRepository;

  @Autowired private RuleTargetRepository ruleTargetRepository;
  @Autowired private DealSiteRepository dealSiteRepository;
  @Autowired private DealPublisherRepository dealPublisherRepository;
  @Autowired private DealPositionRepository dealPositionRepository;

  @BeforeEach
  void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(wac)
            .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
            .build();

    userDetails =
        new SpringUserDetails(
            getDefaultUser(
                    "ROLE_USER_NEXAGE", "ROLE_MANAGER_YIELD_NEXAGE", "ROLE_MANAGER_SMARTEX_NEXAGE")
                .get());
  }

  @Test
  void shouldThrowErrorWhenCreatePostAuctionDiscountHasSpecificDealSelectionWithNoDiscountDeals()
      throws Exception {

    // given
    var postAuctionDiscountSellerDTO = PostAuctionDiscountSellerDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPSeatDTO = PostAuctionDiscountDspSeatDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPDTO =
        PostAuctionDiscountDspDTO.builder()
            .companyPid(1L)
            .dspSeats(List.of(postAuctionDiscountDSPSeatDTO))
            .build();
    var postAuctionDiscountDTO =
        PostAuctionDiscountDTO.builder()
            .discountDescription("Test discount")
            .discountName("Test discount")
            .discountPercent(5D)
            .discountStatus(true)
            .openAuctionEnabled(true)
            .discountSellers(List.of(postAuctionDiscountSellerDTO))
            .dealsSelected(PostAuctionDealsSelected.SPECIFIC)
            .discountDSPs(List.of(postAuctionDiscountDSPDTO))
            .build();
    String jsonRequest = mapper.writeValueAsString(postAuctionDiscountDTO);

    // when
    mockMvc
        .perform(
            post(URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(user(userDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))

        // then
        .andExpect(status().is(400))
        .andExpect(
            content()
                .string(
                    Matchers.containsString(
                        "Specific deals are selected but no deals found in discountDeals property")));
  }

  @Test
  void shouldThrowErrorWhenCreatePostAuctionDiscountWithOpenAuctionAndDealsDisabled()
      throws Exception {

    // given
    var postAuctionDiscountSellerDTO = PostAuctionDiscountSellerDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPSeatDTO = PostAuctionDiscountDspSeatDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPDTO =
        PostAuctionDiscountDspDTO.builder()
            .companyPid(1L)
            .dspSeats(List.of(postAuctionDiscountDSPSeatDTO))
            .build();
    var postAuctionDiscountDTO =
        PostAuctionDiscountDTO.builder()
            .discountDescription("Test discount")
            .discountName("Test discount")
            .discountPercent(5D)
            .discountStatus(true)
            .openAuctionEnabled(false)
            .discountSellers(List.of(postAuctionDiscountSellerDTO))
            .discountDSPs(List.of(postAuctionDiscountDSPDTO))
            .dealsSelected(PostAuctionDealsSelected.NONE)
            .build();
    String jsonRequest = mapper.writeValueAsString(postAuctionDiscountDTO);

    // when
    mockMvc
        .perform(
            post(URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(user(userDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))

        // then
        .andExpect(status().is(400))
        .andExpect(
            content()
                .string(
                    Matchers.containsString(
                        "Either dealsEnabled or openAuctionEnabled should be true")));
  }

  @Test
  void shouldThrowErrorWhenCreatePostAuctionDiscountWithInValidDealValidation() throws Exception {

    // given
    when(dealRepository.countByPidIn(anySet())).thenReturn(1);

    var postAuctionDiscountSellerDTO = PostAuctionDiscountSellerDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPSeatDTO = PostAuctionDiscountDspSeatDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPDTO =
        PostAuctionDiscountDspDTO.builder()
            .companyPid(1L)
            .dspSeats(List.of(postAuctionDiscountDSPSeatDTO))
            .build();
    var postAuctionDiscountDTO =
        PostAuctionDiscountDTO.builder()
            .discountDescription("Test discount")
            .discountName("Test discount")
            .discountPercent(5D)
            .discountStatus(true)
            .openAuctionEnabled(false)
            .discountSellers(List.of(postAuctionDiscountSellerDTO))
            .discountDSPs(List.of(postAuctionDiscountDSPDTO))
            .discountDeals(
                List.of(
                    DirectDealViewDTO.builder().pid(1L).build(),
                    DirectDealViewDTO.builder().pid(2L).build()))
            .dealsSelected(PostAuctionDealsSelected.ALL)
            .build();
    String jsonRequest = mapper.writeValueAsString(postAuctionDiscountDTO);

    // when
    mockMvc
        .perform(
            post(URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(user(userDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))

        // then
        .andExpect(status().is(400))
        .andExpect(
            content().string(Matchers.containsString("Invalid Deals selected for discount")));
  }

  @Test
  void shouldThrowErrorWhenCreatePostAuctionDiscountWithInValidDealPublisherValidation()
      throws Exception {

    // given
    var ruleTarget = new RuleTarget();
    ruleTarget.setData("[{\"buyerCompany\":6}]");
    ruleTarget.setMatchType(MatchType.EXCLUDE_LIST);
    ruleTarget.setRuleTargetType(RuleTargetType.BUYER_SEATS);
    when(dealRepository.countByPidIn(anySet())).thenReturn(2);
    when(ruleTargetRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(ruleTarget));

    var postAuctionDiscountSellerDTO = PostAuctionDiscountSellerDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPSeatDTO = PostAuctionDiscountDspSeatDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPDTO =
        PostAuctionDiscountDspDTO.builder()
            .companyPid(1L)
            .dspSeats(List.of(postAuctionDiscountDSPSeatDTO))
            .build();
    var postAuctionDiscountDTO =
        PostAuctionDiscountDTO.builder()
            .discountDescription("Test discount")
            .discountName("Test discount")
            .discountPercent(5D)
            .discountStatus(true)
            .openAuctionEnabled(false)
            .discountSellers(List.of(postAuctionDiscountSellerDTO))
            .discountDSPs(List.of(postAuctionDiscountDSPDTO))
            .discountDeals(
                List.of(
                    DirectDealViewDTO.builder().pid(1L).build(),
                    DirectDealViewDTO.builder().pid(2L).build()))
            .dealsSelected(PostAuctionDealsSelected.ALL)
            .build();
    String jsonRequest = mapper.writeValueAsString(postAuctionDiscountDTO);

    // when
    mockMvc
        .perform(
            post(URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(user(userDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))

        // then
        .andExpect(status().is(400))
        .andExpect(
            content()
                .string(Matchers.containsString("Invalid Deal - 1 for given sellers combination")));
  }

  @Test
  void shouldThrowErrorWhenCreatePostAuctionDiscountWithInValidBuyerAndBuyerSeatValidation()
      throws Exception {

    // given
    var ruleTarget = new RuleTarget();
    ruleTarget.setData("[{\"buyerCompany\":6}]");
    ruleTarget.setMatchType(MatchType.INCLUDE_LIST);
    ruleTarget.setRuleTargetType(RuleTargetType.BUYER_SEATS);
    when(dealRepository.countByPidIn(anySet())).thenReturn(2);
    when(dealPositionRepository.count(any(Specification.class))).thenReturn(1L);
    when(ruleTargetRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(ruleTarget));
    var postAuctionDiscountSellerDTO = PostAuctionDiscountSellerDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPSeatDTO = PostAuctionDiscountDspSeatDTO.builder().pid(1L).build();
    var postAuctionDiscountDSPDTO =
        PostAuctionDiscountDspDTO.builder()
            .companyPid(1L)
            .dspSeats(List.of(postAuctionDiscountDSPSeatDTO))
            .build();
    var postAuctionDiscountDTO =
        PostAuctionDiscountDTO.builder()
            .discountDescription("Test discount")
            .discountName("Test discount")
            .discountPercent(5D)
            .discountStatus(true)
            .openAuctionEnabled(false)
            .discountSellers(List.of(postAuctionDiscountSellerDTO))
            .discountDSPs(List.of(postAuctionDiscountDSPDTO))
            .discountDeals(
                List.of(
                    DirectDealViewDTO.builder().pid(1L).build(),
                    DirectDealViewDTO.builder().pid(2L).build()))
            .dealsSelected(PostAuctionDealsSelected.ALL)
            .build();
    String jsonRequest = mapper.writeValueAsString(postAuctionDiscountDTO);

    // when
    mockMvc
        .perform(
            post(URL_TEMPLATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .with(user(userDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))

        // then
        .andExpect(status().is(400))
        .andExpect(
            content()
                .string(
                    Matchers.containsString("Invalid Deal for given DSP/Buyer Seat combination")));
  }

  private Supplier<UserAuth> getDefaultUser(String... overrideAuthorities) {
    return () -> {
      User user = new User();
      user.setUserName("user");
      user.setRole(User.Role.ROLE_MANAGER);
      user.setPid(1L);
      user.setGlobal(false);
      user.setDealAdmin(false);
      User spyUser = Mockito.spy(user);

      doReturn(CompanyType.NEXAGE).when(spyUser).getCompanyType();
      doReturn(null).when(spyUser).getSellerSeat();
      if (overrideAuthorities == null || overrideAuthorities.length == 0) {
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_MANAGER_NEXAGE")))
            .when(spyUser)
            .getAuthorities();
      } else {
        List<GrantedAuthority> authorities =
            Arrays.stream(overrideAuthorities)
                .reduce(
                    new ArrayList<GrantedAuthority>(),
                    (acc, str) -> {
                      acc.add(new SimpleGrantedAuthority(str));
                      return acc;
                    },
                    (list, list1) -> list);
        doReturn(authorities).when(spyUser).getAuthorities();
      }

      var derivedEntitlements = new ArrayList<Entitlement>();
      Arrays.stream(
              Arrays.stream(
                      Optional.ofNullable(overrideAuthorities)
                          .orElse(new String[] {"MANAGER_NEXAGE"}))
                  .collect(Collectors.joining("_"))
                  .replace("ROLE_", "")
                  .split("_"))
          .forEach(
              str -> {
                var entitlement = new Entitlement();
                entitlement.setName(str);
                derivedEntitlements.add(entitlement);
              });

      doReturn(Collections.EMPTY_SET).when(spyUser).getCompanies();

      doReturn(Collections.EMPTY_SET).when(spyUser).getCompanies();
      doReturn(null).when(spyUser).getCompany();
      doReturn(1L).when(spyUser).getCompanyPid();
      return new UserAuth(spyUser, derivedEntitlements);
    };
  }

  @Configuration
  static class ContextConfiguration {
    @Mock private PostAuctionDiscountRepository repository;
    @Mock private RevenueGroupRepository revenueGroupRepository;
    @Mock private SellerAttributesRepository sellerAttributesRepository;
    @Mock private PostAuctionDiscountTypeRepository postAuctionDiscountTypeRepository;
    @Mock private EntityManager entityManager;

    @Autowired LocalValidatorFactoryBean beanValidator;

    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
      MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
      postProcessor.setValidator(beanValidator.getValidator());
      return postProcessor;
    }

    @Bean
    @Primary
    PostAuctionDiscountService postAuctionDiscountServiceImpl() {
      return new PostAuctionDiscountServiceImpl(
          repository,
          revenueGroupRepository,
          sellerAttributesRepository,
          postAuctionDiscountTypeRepository,
          entityManager);
    }
  }
}

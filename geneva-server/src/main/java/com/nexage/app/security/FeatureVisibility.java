package com.nexage.app.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class FeatureVisibility implements Serializable {

  private static final long serialVersionUID = -3645048286021851740L;

  private Menu menu;
  private Map<CompanyType, Boolean> loginAccess;
  private SelfRegistration selfRegistration;

  public Menu getMenu() {
    return menu;
  }

  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  public Map<CompanyType, Boolean> getLoginAccess() {
    return loginAccess;
  }

  public void setLoginAccess(Map<CompanyType, Boolean> loginAccess) {
    this.loginAccess = loginAccess;
  }

  public SelfRegistration getSelfRegistration() {
    return selfRegistration;
  }

  public void setSelfRegistration(SelfRegistration selfRegistration) {
    this.selfRegistration = selfRegistration;
  }

  private boolean dashboardSummaryCaching;

  public boolean hasDashboardSummaryCaching() {
    return dashboardSummaryCaching;
  }

  public void setDashboardSummaryCaching(boolean dashboardSummaryCaching) {
    this.dashboardSummaryCaching = dashboardSummaryCaching;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  public abstract static class MenuMixinForPublic {

    @JsonIgnore private boolean buyerLogin;
    @JsonIgnore private boolean sellerLogin;
    @JsonIgnore private boolean seatHolderLogin;
    @JsonIgnore private Map<CompanyType, Boolean> loginAccess;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class Menu {

    @JsonProperty("dashboard")
    private Visible dashboard;

    @JsonProperty("insertionOrders")
    private Visible insertionOrders;

    @JsonProperty("reports")
    private Visible reports;

    @JsonProperty("adQuality")
    private AdQuality adQuality;

    @JsonProperty("adScreeningTopLevel")
    private Visible adScreeningTopLevel;

    @JsonProperty("rtbConfiguration")
    private Visible rtbConfiguration;

    @JsonProperty("companies")
    private Companies companies;

    @JsonProperty("users")
    private Visible users;

    @JsonProperty("campaigns")
    private Visible campaigns;

    @JsonProperty("experiments")
    private Visible experiments;

    @JsonProperty("deals")
    private Visible deals;

    @JsonProperty("pssPayouts")
    private Visible pssPayouts;

    @JsonCreator
    public Menu(
        @JsonProperty("dashboard") Visible dashboard,
        @JsonProperty("insertionOrders") Visible insertionOrders,
        @JsonProperty("reports") Visible reports,
        @JsonProperty("adQuality") AdQuality adQuality,
        @JsonProperty("adScreeningTopLevel") Visible adScreeningTopLevel,
        @JsonProperty("rtbConfiguration") Visible rtbConfiguration,
        @JsonProperty("companies") Companies companies,
        @JsonProperty("users") Visible users,
        @JsonProperty("campaigns") Visible campaigns,
        @JsonProperty("experiments") Visible experiments,
        @JsonProperty("deals") Visible deals,
        @JsonProperty("pssPayouts") Visible pssPayouts) {
      this.dashboard = dashboard;
      this.insertionOrders = insertionOrders;
      this.reports = reports;
      this.adQuality = adQuality;
      this.adScreeningTopLevel = adScreeningTopLevel;
      this.rtbConfiguration = rtbConfiguration;
      this.companies = companies;
      this.users = users;
      this.campaigns = campaigns;
      this.experiments = experiments;
      this.deals = deals;
      this.pssPayouts = pssPayouts;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    private static class Visible {

      @JsonProperty("visible")
      private Boolean visible;

      @JsonCreator
      public Visible(@JsonProperty("visible") Boolean visible) {
        this.visible = visible;
      }

      @Override
      public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
      }
    }

    private static class Companies {

      @JsonProperty("visible")
      private boolean visible;

      @JsonProperty("buyers")
      private Visible buyers;

      @JsonProperty("sellers")
      private Visible sellers;

      @JsonProperty("seatHolders")
      private Visible seatHolders;

      @JsonProperty("partners")
      private Visible partners;

      @JsonProperty("hbPartners")
      private Visible hbPartners;

      @JsonCreator
      public Companies(
          @JsonProperty("visible") Boolean visible,
          @JsonProperty("buyers") Visible buyers,
          @JsonProperty("sellers") Visible sellers,
          @JsonProperty("seatHolders") Visible seatHolders,
          @JsonProperty("partners") Visible partners,
          @JsonProperty("hbPartners") Visible hbPartners) {
        this.visible = visible;
        this.buyers = buyers;
        this.sellers = sellers;
        this.seatHolders = seatHolders;
        this.partners = partners;
        this.hbPartners = hbPartners;
      }

      @Override
      public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
      }
    }

    private static class AdQuality {

      @JsonProperty("visible")
      private boolean visible;

      @JsonProperty("adScreening")
      private Visible adScreening;

      @JsonCreator
      public AdQuality(
          @JsonProperty("visible") Boolean visible,
          @JsonProperty("adScreening") Visible adScreening) {
        this.visible = visible;
        this.adScreening = adScreening;
      }

      @Override
      public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
      }
    }
  }

  public static class SelfRegistration {

    @JsonProperty("oneCentralUserCreation")
    private boolean oneCentralUserCreation;

    @JsonCreator
    public SelfRegistration(
        @JsonProperty("oneCentralUserCreation") boolean oneCentralUserCreation) {
      this.oneCentralUserCreation = oneCentralUserCreation;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
  }
}

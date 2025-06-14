package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.Copyable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PublisherTagRuleDTO implements Copyable<PublisherTagRuleDTO>, Serializable {

  private static final long serialVersionUID = 1L;

  public enum RuleType {
    UserAgent,
    Country,
    URL,
    KeywordParam,
    ReqParam,
    DeviceModel,
    DeviceMake,
    ISPCarrier,
    DMA,
    OsVersion,
    DeviceMakeModel,
    CarrierWifi,
    SmartYield,
    SdkPlugin,
    SdkCapability
  }

  public enum TargetType {
    Keyword,
    NegKeyword,
    Regex,
    NegRegex,
  }

  private PublisherTagDTO tag;
  private Long pid;
  private Integer version;
  private String data;
  private TargetType targetType;
  private RuleType ruleType;
  private String paramName;

  @Override
  public PublisherTagRuleDTO copy(PublisherTagRuleDTO original) {
    PublisherTagRuleDTO copy = original;
    copy.pid = null;
    copy.version = null;
    return copy;
  }
}

package com.nexage.admin.dw.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

public class DrillDowns {

  @XStreamImplicit(itemFieldName = "drilldown")
  private List<DrillDown> drillDown = new ArrayList<DrillDown>();

  public class DrillDown {

    @XStreamAsAttribute private String id;

    @XStreamAlias("displayname")
    private String displayName;

    private String description;

    @XStreamAlias("actualtype")
    private String actualType;

    @XStreamAlias("conversiontype")
    private String conversionType;

    private Sorting sorting;

    @XStreamAlias("enumvalues")
    private IntevalEnums intervalEnums;

    @XStreamAlias("mappinginfoid")
    private String mappingInfoId;

    class IntevalEnums {

      @XStreamImplicit private List<String> value = new ArrayList<String>();

      public List<String> getValue() {
        return value;
      }

      public void setValue(List<String> value) {
        this.value = value;
      }
    }

    @XStreamAlias("mappinginfo")
    private MappingInfo mappingInfo;

    @XStreamAlias("extramappinginfo")
    private ExtraMappingInfo extraMappingInfo;

    @XStreamAlias("conditionalVisibility")
    private ConditionalVisibility conditionalVisibility;

    class ConditionalVisibility {

      @XStreamImplicit List<String> value = new ArrayList<>();

      public List<String> getValue() {
        return value;
      }

      public void setValue(List<String> value) {
        this.value = value;
      }
    }

    public String getId() {
      return id;
    }

    public MappingInfo getMappingInfo() {
      return mappingInfo;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getActualType() {
      return actualType;
    }

    public void setActualType(String actualType) {
      this.actualType = actualType;
    }

    public String getConversionType() {
      return conversionType;
    }

    public void setConversionType(String conversionType) {
      this.conversionType = conversionType;
    }

    public Sorting getSorting() {
      return sorting;
    }

    public void setSorting(Sorting sorting) {
      this.sorting = sorting;
    }

    public IntevalEnums getIntervalEnums() {
      return intervalEnums;
    }

    public void setIntervalEnums(IntevalEnums intervalEnums) {
      this.intervalEnums = intervalEnums;
    }

    public void setId(String id) {
      this.id = id;
    }

    public void setMappingInfo(MappingInfo mappingInfo) {
      this.mappingInfo = mappingInfo;
    }

    public ExtraMappingInfo getExtraMappingInfo() {
      return extraMappingInfo;
    }

    public void setExtraMappingInfo(ExtraMappingInfo extraMappingInfo) {
      this.extraMappingInfo = extraMappingInfo;
    }

    public String getMappingInfoId() {
      return mappingInfoId;
    }

    public void setMappingInfoId(String mappingInfoId) {
      this.mappingInfoId = mappingInfoId;
    }

    public void setConditionalVisibility(ConditionalVisibility cv) {
      this.conditionalVisibility = cv;
    }

    public ConditionalVisibility getConditionalVisibility() {
      return conditionalVisibility;
    }
  }

  public List<DrillDown> getDrillDown() {
    return drillDown;
  }

  public void setDrillDown(List<DrillDown> drillDown) {
    this.drillDown = drillDown;
  }
}

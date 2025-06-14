package com.nexage.admin.dw.xstream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.List;

/** Root model for xml report definition */
@XStreamAlias("reportdefinition")
@JsonInclude(Include.NON_NULL)
public class XmlReportDefinition {

  @XStreamAsAttribute private String id;

  private String name;

  private String description;

  private String category;

  @XStreamAlias("dateranges")
  private DateRange dateRange;

  public class DateRange {

    @XStreamImplicit(itemFieldName = "supported")
    private List<String> supportedValues;

    @XStreamAsAttribute
    @XStreamAlias("default")
    private String defaultValue;

    public List<String> getSupportedValues() {
      return supportedValues;
    }

    public void setSupportedValues(List<String> supportedValues) {
      this.supportedValues = supportedValues;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
    }
  }

  @XStreamAlias("intervals")
  private Interval interval;

  public class Interval {

    @XStreamImplicit(itemFieldName = "supported")
    private List<String> supportedValues;

    @XStreamAsAttribute
    @XStreamAlias("default")
    private String defaultValue;

    public List<String> getSupportedValues() {
      return supportedValues;
    }

    public void setSupportedValues(List<String> supportedValues) {
      this.supportedValues = supportedValues;
    }

    public String getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
    }
  }

  @XStreamAlias("columndefinitions")
  private ColumnDefinitions columnDefs;

  @XStreamAlias("drilldowns")
  private DrillDowns drillDowns;

  public XmlReportDefinition(
      String id,
      String name,
      String description,
      String category,
      DateRange dateRange,
      Interval interval) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.category = category;
    this.dateRange = dateRange;
    this.interval = interval;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public DateRange getDateRange() {
    return dateRange;
  }

  public void setDateRange(DateRange dateRange) {
    this.dateRange = dateRange;
  }

  public Interval getInterval() {
    return interval;
  }

  public void setInterval(Interval interval) {
    this.interval = interval;
  }

  public ColumnDefinitions getColumnDefs() {
    return columnDefs;
  }

  public void setColumnDefs(ColumnDefinitions columnDefs) {
    this.columnDefs = columnDefs;
  }

  public DrillDowns getDrillDowns() {
    return drillDowns;
  }

  public void setDrillDowns(DrillDowns drillDowns) {
    this.drillDowns = drillDowns;
  }
}

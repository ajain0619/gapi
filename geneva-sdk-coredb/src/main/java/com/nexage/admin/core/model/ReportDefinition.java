package com.nexage.admin.core.model;

import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.model.report.ReportType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "report_def")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ReportDefinition extends BaseModel implements Serializable {

  private static final long serialVersionUID = -2311010828104758809L;

  @NotNull
  @Column(name = "report_def_xml")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String reportDefAsXml;

  /** True - System defined report def False - Custom report def */
  @EqualsAndHashCode.Include @ToString.Include @NotNull private boolean system;

  @Column(name = "include_limit")
  @EqualsAndHashCode.Include
  @ToString.Include
  private boolean includeLimit;

  @Column(name = "row_limit")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Integer rowLimit;

  @Enumerated(EnumType.STRING)
  @Column(name = "report_type", nullable = false, length = 50)
  @NotNull
  @EqualsAndHashCode.Include
  @ToString.Include
  private ReportType reportType;

  @Column(name = "display_order", nullable = false)
  private int displayOrder;

  @ElementCollection
  @CollectionTable(
      name = "report_def_company_type",
      joinColumns = @JoinColumn(name = "report_def_id", referencedColumnName = "pid"))
  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private List<CompanyType> companyTypes = new ArrayList<>();

  /** JPA requires default constructor */
  public ReportDefinition() {
    super();
  }

  public ReportDefinition(String reportDefXml, boolean system, ReportType type) {
    this(reportDefXml, system, false, null, type);
  }

  public ReportDefinition(String reportDefXml, ReportType type) {
    this(reportDefXml, true, false, null, type);
  }

  public ReportDefinition(
      String reportDefXml,
      boolean system,
      boolean includeLimit,
      Integer rowLimit,
      ReportType type) {
    super();
    this.reportDefAsXml = reportDefXml;
    this.system = system;
    this.includeLimit = includeLimit;
    this.rowLimit = rowLimit;
    this.reportType = type;
  }

  public List<CompanyType> getCompanyTypes() {
    return companyTypes;
  }

  public void setCompanyTypes(List<CompanyType> companyTypes) {
    this.companyTypes = companyTypes;
  }
}

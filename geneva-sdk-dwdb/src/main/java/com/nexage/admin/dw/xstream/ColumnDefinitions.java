package com.nexage.admin.dw.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

public class ColumnDefinitions {

  public class ColumnDefinition {

    @XStreamAsAttribute private String id;

    private Dimension dimension;

    public class Dimension {

      @XStreamAlias("requiresmapping")
      private boolean mappingRequired;

      @XStreamAlias("mappingcolumnid")
      private String mappingColumnId;

      @XStreamAlias("internalidentifier")
      private MappingInfo mappinginfo;

      public boolean isMappingRequired() {
        return mappingRequired;
      }

      public void setMappingRequired(boolean mappingRequired) {
        this.mappingRequired = mappingRequired;
      }

      public String getMappingColumnId() {
        return mappingColumnId;
      }

      public void setMappingColumnId(String mappingColumnId) {
        this.mappingColumnId = mappingColumnId;
      }

      public MappingInfo getMappinginfo() {
        return mappinginfo;
      }

      public void setMappinginfo(MappingInfo mappinginfo) {
        this.mappinginfo = mappinginfo;
      }
    }

    @XStreamAlias("displayname")
    private String displayName;

    private String description;

    @XStreamAlias("actualtype")
    private String actualType;

    @XStreamAlias("conversiontype")
    private String conversionType;

    private Sorting sorting;

    private Boolean markcolumn;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public Dimension getDimension() {
      return dimension;
    }

    public void setDimension(Dimension dimension) {
      this.dimension = dimension;
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

    public final Boolean getMarkcolumn() {
      return markcolumn;
    }

    public final void setMarkcolumn(Boolean markcolumn) {
      this.markcolumn = markcolumn;
    }
  }

  @XStreamImplicit(itemFieldName = "columndefinition")
  private List<ColumnDefinition> columnDef = new ArrayList<>();

  @XStreamAlias("defaultsort")
  @XStreamAsAttribute
  private String defaultSort;

  @XStreamAlias("sortorder")
  @XStreamAsAttribute
  private String sortOrder;

  public ColumnDefinitions(List<ColumnDefinition> columnDef, String defaultSort, String sortOrder) {
    this.columnDef = columnDef;
    this.defaultSort = defaultSort;
    this.sortOrder = sortOrder;
  }

  public List<ColumnDefinition> getColumnDef() {
    return columnDef;
  }

  public void setColumnDef(List<ColumnDefinition> columnDef) {
    this.columnDef = columnDef;
  }

  public String getDefaultSort() {
    return defaultSort;
  }

  public void setDefaultSort(String defaultSort) {
    this.defaultSort = defaultSort;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }
}

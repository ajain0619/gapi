package com.nexage.admin.dw.xstream;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

public class ExtraMappingInfo {

  @XStreamImplicit(itemFieldName = "mappingfield")
  private List<MappingField> mappingField = new ArrayList<>();

  @XStreamAlias("columndefinitions")
  private ColumnDefinitions columnDefinitions;

  @XStreamAlias("mappingfield")
  public class MappingField {

    private String id;

    private String type;

    public String getId() {
      return id;
    }

    public String getType() {
      return type;
    }
  }

  public List<MappingField> getMappingField() {
    return mappingField;
  }

  public ColumnDefinitions getColumnDefinitions() {
    return columnDefinitions;
  }
}

package com.nexage.app.dto;

import java.util.List;

public class SeatHolderReportRequestDTO extends BaseReportRequestDTO {

  private static final long serialVersionUID = 1L;

  private Long seatholder;
  private Long seatadvertiser;
  private String exchangesite;
  private Long insertionorder;
  private Long lineitem;
  private Long targetgroup;
  private Long bdrcreative;
  private Long insertionOrderType;
  private List<Long> insertionorders;

  public Long getSeatholder() {
    return seatholder;
  }

  public Long getSeatadvertiser() {
    return seatadvertiser;
  }

  public String getExchangesite() {
    return exchangesite;
  }

  public Long getInsertionorder() {
    return insertionorder;
  }

  public Long getLineitem() {
    return lineitem;
  }

  public Long getTargetgroup() {
    return targetgroup;
  }

  public Long getbdrCreative() {
    return bdrcreative;
  }

  public Long getInsertionOrderType() {
    return insertionOrderType;
  }

  public List<Long> getInsertionorders() {
    return insertionorders;
  }

  public void setSeatholder(Long seatholder) {
    this.seatholder = seatholder;
    addToFilterParams(FilterParam.seatholder.getDwAlias(), seatholder);
  }

  public void setSeatadvertiser(Long seatadvertiser) {
    this.seatadvertiser = seatadvertiser;
    addToFilterParams(FilterParam.seatadvertiser.getDwAlias(), seatadvertiser);
  }

  public void setExchangesite(String exchangesite) {
    this.exchangesite = exchangesite;
    addToFilterParams(FilterParam.exchangesite.getDwAlias(), exchangesite);
  }

  public void setInsertionorder(Long insertionorder) {
    this.insertionorder = insertionorder;
    addToFilterParams(FilterParam.insertionorder.getDwAlias(), insertionorder);
  }

  public void setLineitem(Long lineitem) {
    this.lineitem = lineitem;
    addToFilterParams(FilterParam.lineitem.getDwAlias(), lineitem);
  }

  public void setTargetgroup(Long targetgroup) {
    this.targetgroup = targetgroup;
    addToFilterParams(FilterParam.targetgroup.getDwAlias(), targetgroup);
  }

  public void setbdrCreative(Long bdrcreative) {
    this.bdrcreative = bdrcreative;
    addToFilterParams(FilterParam.creative.getDwAlias(), bdrcreative);
  }

  public void setInsertionOrderType(Long insertionOrderType) {
    this.insertionOrderType = insertionOrderType;
    addToFilterParams(FilterParam.insertionOrderType.getDwAlias(), insertionOrderType);
  }

  public void setInsertionorders(List<Long> insertionorders) {
    this.insertionorders = insertionorders;
    addToFilterParams(FilterParam.insertionorders.getDwAlias(), insertionorders);
  }
}

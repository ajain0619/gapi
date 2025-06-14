package com.nexage.admin.dw.reports.helper;

import com.nexage.admin.dw.reports.model.QueryParameters;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

@Log4j2
public class ReportQueryHelper {

  protected final Context context;
  protected final JdbcTemplate jdbcTemplate;

  public ReportQueryHelper(Context context, JdbcTemplate jdbcTemplate) {
    this.context = context;
    this.jdbcTemplate = jdbcTemplate;
  }

  public String addSQLComment(String sql, String loggedInUser) {
    String query = sql;
    String regex = "[sS][eE][lL][eE][cC][tT]";

    StringBuilder replacement =
        new StringBuilder("select /*")
            .append(loggedInUser)
            .append("*/ /*+label(")
            .append("geneva\\$rpt");
    if (context != null
        && context.queryParams != null
        && context.queryParams.getReportType() != null)
      replacement.append("\\$").append(context.queryParams.getReportType().name());
    if (context != null && context.queryParams != null && context.queryParams.getDimKey() != null)
      replacement.append("\\$").append(context.queryParams.getDimKey().name());

    replacement.append(")*/ ");

    if (!StringUtils.isBlank(sql)) {
      sql = query.replaceFirst(regex, replacement.toString());
    }
    return sql;
  }

  public static final class Context {

    // Required only for nested queries
    protected final String outerSelectList;
    protected final String innerSelectList;
    // End
    protected final String simpleSelectList;
    protected final String factTable;
    protected final String dimTableAlias;
    protected final String tempTableAlias;
    protected final String factTableAliasDot;
    protected final String dimTableAliasDot;
    protected final String tempTableAliasDot;
    protected final boolean limit;
    protected final String otherWhereClause;
    private final String extraDimJoin; // if more than one hop required for id-name resolution

    /* Mandatory for any reports to use the helper */
    protected final QueryParameters queryParams;
    protected final String factTableAlias;
    protected final List<Long> siteIds;
    protected final Long companyId;
    protected final boolean excludeSites;
    private final String queryForHistoricdimensions;
    private final String innerOrderbyClause;

    public Context(Builder builder) {
      this.outerSelectList = builder.outerSelectList;
      this.innerSelectList = builder.innerSelectList;
      this.simpleSelectList = builder.simpleSelectList;
      this.factTable = builder.factTable;
      this.siteIds = builder.siteIds;
      this.factTableAlias = builder.factTableAlias;
      this.dimTableAlias = builder.dimTableAlias;
      this.tempTableAlias = builder.tempTableAlias;
      this.factTableAliasDot = builder.factTableAliasDot;
      this.dimTableAliasDot = builder.dimTableAliasDot;
      this.tempTableAliasDot = builder.tempTableAliasDot;
      this.queryParams = builder.queryParams;
      this.limit = builder.limit;
      this.otherWhereClause = builder.otherWhereClause;
      this.companyId = builder.companyId;
      this.excludeSites = builder.excludeSites;
      this.extraDimJoin = builder.extraDimJoin;
      this.queryForHistoricdimensions = builder.queryForHistoricdimensions;
      this.innerOrderbyClause = builder.innerOrderbyClause;
    }

    public String getFactTable() {
      return factTable;
    }

    public List<Long> getSiteIds() {
      return siteIds;
    }

    public String getFactTableAlias() {
      return factTableAlias;
    }

    public String getDimTableAlias() {
      return dimTableAlias;
    }

    public QueryParameters getQueryParams() {
      return queryParams;
    }

    public String getFactTableAliasDot() {
      return factTableAliasDot;
    }

    public String getDimTableAliasDot() {
      return dimTableAliasDot;
    }

    public String getOuterSelectList() {
      return outerSelectList;
    }

    public String getInnerSelectList() {
      return innerSelectList;
    }

    public String getTempTableAlias() {
      return tempTableAlias;
    }

    public String getTempTableAliasDot() {
      return tempTableAliasDot;
    }

    public boolean isLimit() {
      return limit;
    }

    public String getSimpleSelectList() {
      return simpleSelectList;
    }

    public long getCompanyId() {
      return companyId;
    }

    public String getExtraDimJoin() {
      return extraDimJoin;
    }

    public boolean isExcludeSites() {
      return excludeSites;
    }

    public String getQueryForHistoricdimensions() {
      return queryForHistoricdimensions;
    }

    public String getInnerOrderbyClause() {
      return innerOrderbyClause;
    }

    public static final class Builder {
      private String outerSelectList;
      private String innerSelectList;
      private String simpleSelectList;
      private String factTable;
      private List<Long> siteIds = new ArrayList<>();
      private String factTableAlias;
      private String dimTableAlias;
      private String tempTableAlias;
      private String factTableAliasDot;
      private String dimTableAliasDot;
      private String tempTableAliasDot;
      private QueryParameters queryParams;
      private boolean limit = false;
      private Long companyId;
      public boolean excludeSites = false;
      private String queryForHistoricdimensions;

      // Holds any additional where clause to be added to query other than
      // ones in QueryParameters.
      // This should have only the condition (without any Where clause)
      // and more than one conditions should be
      // conjuncted with AND/OR
      private String otherWhereClause;
      private String extraDimJoin;

      private String innerOrderbyClause;

      public Context build() {
        return new Context(this);
      }

      public String getOuterSelectList() {
        return outerSelectList;
      }

      public Builder setOuterSelectList(String outerSelectList) {
        this.outerSelectList = outerSelectList;
        return this;
      }

      public String getInnerSelectList() {
        return innerSelectList;
      }

      public Builder setInnerSelectList(String innerSelectList) {
        this.innerSelectList = innerSelectList;
        return this;
      }

      public String getTempTableAlias() {
        return tempTableAlias;
      }

      public Builder setTempTableAlias(String tempTableAlias) {
        this.tempTableAlias = tempTableAlias;
        this.tempTableAliasDot = tempTableAlias.concat(".");
        return this;
      }

      public String getTempTableAliasDot() {
        return tempTableAliasDot;
      }

      public String getFactTable() {
        return factTable;
      }

      public Builder setFactTable(String factTable) {
        this.factTable = factTable;
        return this;
      }

      public List<Long> getSiteIds() {
        return siteIds;
      }

      public Builder setSiteIds(List<Long> siteIds) {
        this.siteIds = siteIds;
        return this;
      }

      public String getFactTableAlias() {
        return factTableAlias;
      }

      public Builder setFactTableAlias(String factTableAlias) {
        this.factTableAlias = factTableAlias;
        this.factTableAliasDot = factTableAlias.concat(".");
        return this;
      }

      public String getDimTableAlias() {
        return dimTableAlias;
      }

      public Builder setDimTableAlias(String dimTableAlias) {
        this.dimTableAlias = dimTableAlias;
        this.dimTableAliasDot = dimTableAlias.concat(".");
        return this;
      }

      public QueryParameters getQueryParams() {
        return queryParams;
      }

      public Builder setQueryParams(QueryParameters queryParams) {
        this.queryParams = queryParams;
        return this;
      }

      public String getFactTableAliasDot() {
        return factTableAliasDot;
      }

      public String getDimTableAliasDot() {
        return dimTableAliasDot;
      }

      public boolean isLimit() {
        return limit;
      }

      public Builder setLimit(boolean limit) {
        this.limit = limit;
        return this;
      }

      public String getOtherFullWhereClause() {
        return otherWhereClause;
      }

      public Builder setOtherFullWhereClause(String otherFullWhereClause) {
        this.otherWhereClause = otherFullWhereClause;
        return this;
      }

      public String getSimpleSelectList() {
        return simpleSelectList;
      }

      public Builder setSimpleSelectList(String simpleSelectList) {
        this.simpleSelectList = simpleSelectList;
        return this;
      }

      public Long getCompanyId() {
        return companyId;
      }

      public Builder setCompanyId(Long companyId) {
        this.companyId = companyId;
        return this;
      }

      public boolean isExcludeSites() {
        return excludeSites;
      }

      public Builder setExcludeSites(boolean excludeSites) {
        this.excludeSites = excludeSites;
        return this;
      }

      public Builder setExtraDimJoin(String extraDimJoin) {
        this.extraDimJoin = extraDimJoin;
        return this;
      }

      public String getExtraDimJoin() {
        return extraDimJoin;
      }

      public String getQueryForHistoricdimensions() {
        return queryForHistoricdimensions;
      }

      public void setQueryForHistoricdimensions(String queryForHistoricdimensions) {
        this.queryForHistoricdimensions = queryForHistoricdimensions;
      }

      public String getInnerOrderbyClause() {
        return innerOrderbyClause;
      }

      public void setInnerOrderbyClause(String innerOrderByClause) {
        this.innerOrderbyClause = innerOrderByClause;
      }
    }
  }
}

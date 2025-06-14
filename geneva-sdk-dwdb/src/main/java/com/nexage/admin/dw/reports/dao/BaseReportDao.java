package com.nexage.admin.dw.reports.dao;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

@Log4j2
public class BaseReportDao {

  @Autowired
  @Qualifier("dwJdbcTemplate")
  protected JdbcTemplate jdbcTemplate;
}

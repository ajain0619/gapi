/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nexage.app.services.impl.queries;

import com.nexage.admin.dw.util.ReportDefEnums;

/** @author Gamal Dawood <gamal.dawood@teamaol.com> */
public interface DWQueries {

  StringBuilder buildSQLforMetrics(ReportDefEnums.Interval interval);

  StringBuilder buildSQLforAdSourceMetrics(ReportDefEnums.Interval interval);
}

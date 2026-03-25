package com.abc.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatisticsVO {

    private Long productCount;

    private Long pendingAuditCount;

    private Long orderCount;

    private Long userCount;

    private Long todayProductCount;

    private Long todayOrderCount;

    private List<Map<String, Object>> productTrend;

    private List<Map<String, Object>> orderTrend;

    private List<Map<String, Object>> categoryDistribution;
}

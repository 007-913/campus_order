package org.example.campus_order.service;

import java.util.Map;

public interface StatService {

    /**
     * 获取管理员全局统计数据
     */
    Map<String, Object> getAdminStatData();

    /**
     * 获取单店铺经营统计数据
     */
    Map<String, Object> getShopStatData(Long shopId);

    /**
     * 获取店铺指定月份营业额趋势数据
     */
    Map<String, Object> getShopDailyRevenue(Long shopId, String month);

    /**
     * 获取管理员近7日完成订单趋势数据
     */
    Map<String, Object> getAdminDailyOrderTrend();

    /**
     * 获取店铺近15日完成订单趋势数据
     */
    Map<String, Object> getShopDailyOrderTrend(Long shopId);

    /**
     * 获取管理员营业额导出数据
     */
    Map<String, Object> getAdminRevenueData(String range);

    /**
     * 获取管理员指定月份营业额趋势数据
     */
    Map<String, Object> getAdminMonthlyRevenueData(String month);

    /**
     * 获取单店铺营业额导出数据
     */
    Map<String, Object> getShopRevenueData(Long shopId, String range);
}

package org.example.campus_order.service.Impl;

import org.example.campus_order.entity.Order;
import org.example.campus_order.entity.Food;
import org.example.campus_order.mapper.OrderMapper;
import org.example.campus_order.vo.DailyStatVO;

import org.example.campus_order.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatServiceImpl implements StatService {

    /** 订单已完成状态 */
    private static final int ORDER_FINISHED = 2;

    @Autowired
    private UserService userService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Map<String, Object> getAdminStatData() {
        Map<String, Object> map = new HashMap<>();
        map.put("userCount", userService.count());
        map.put("shopCount", shopService.count());
        map.put("orderCount", orderService.count());
        map.put("commentCount", commentService.count());
        map.put("foodCount", foodService.count());

        // 已完成订单总营业额
        BigDecimal totalRevenue = orderService.lambdaQuery()
                .eq(Order::getStatus, ORDER_FINISHED)
                .list()
                .stream()
                .map(Order::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("totalRevenue", totalRevenue);

        // 今日完成订单数
        Date todayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Order> todayFinishedOrders = orderService.lambdaQuery()
                .eq(Order::getStatus, ORDER_FINISHED)
                .list()
                .stream()
                .filter(order -> isInRange(getOrderStatTime(order), todayStart, null))
                .toList();
        long todayOrderCount = todayFinishedOrders.size();
        map.put("todayOrderCount", todayOrderCount);

        return map;
    }

    @Override
    public Map<String, Object> getShopStatData(Long shopId) {
        long orderCount = orderService.lambdaQuery()
                .eq(Order::getShopId, shopId)
                .count();

        long foodCount = foodService.lambdaQuery()
                .eq(Food::getShopId, shopId)
                .count();

        // 店铺总营业额（仅已完成订单）
        BigDecimal totalMoney = orderService.lambdaQuery()
                .eq(Order::getShopId, shopId)
                .eq(Order::getStatus, ORDER_FINISHED)
                .list()
                .stream()
                .map(Order::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Date todayStart = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        // 今日完成订单总数
        List<Order> todayFinishedOrders = orderService.lambdaQuery()
                .eq(Order::getShopId, shopId)
                .eq(Order::getStatus, ORDER_FINISHED)
                .list()
                .stream()
                .filter(order -> isInRange(getOrderStatTime(order), todayStart, null))
                .toList();
        long todayOrderCount = todayFinishedOrders.size();

        // 今日营业额（按完成时间）
        BigDecimal todayMoney = todayFinishedOrders.stream()
                .map(Order::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> map = new HashMap<>();
        map.put("orderCount", orderCount);
        map.put("foodCount", foodCount);
        map.put("totalMoney", totalMoney);
        map.put("todayOrderCount", todayOrderCount);
        map.put("todayMoney", todayMoney);
        return map;
    }

    @Override
    public Map<String, Object> getShopDailyRevenue(Long shopId, String month) {
        List<String> dateList = new ArrayList<>();
        List<BigDecimal> moneyList = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = parseMonth(month);

        LocalDate today = LocalDate.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.equals(YearMonth.now()) ? today : yearMonth.atEndOfMonth();
        Map<LocalDate, DailyStatVO> daily = loadDailyStat(shopId, startDate, endDate.plusDays(1));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dateList.add(date.format(dayFormatter));
            DailyStatVO vo = daily.get(date);
            moneyList.add(vo == null || vo.getMoney() == null ? BigDecimal.ZERO : vo.getMoney());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("month", yearMonth.format(monthFormatter));
        result.put("dateList", dateList);
        result.put("moneyList", moneyList);
        return result;
    }

    @Override
    public Map<String, Object> getAdminDailyOrderTrend() {
        return buildOrderTrendData(null, 7);
    }

    @Override
    public Map<String, Object> getShopDailyOrderTrend(Long shopId) {
        return buildOrderTrendData(shopId, 15);
    }

    @Override
    public Map<String, Object> getAdminRevenueData(String range) {
        return buildRevenueData(null, range);
    }

    @Override
    public Map<String, Object> getAdminMonthlyRevenueData(String month) {
        return buildMonthlyRevenueData(null, month);
    }

    @Override
    public Map<String, Object> getShopRevenueData(Long shopId, String range) {
        return buildRevenueData(shopId, range);
    }

    private Map<String, Object> buildRevenueData(Long shopId, String range) {
        boolean monthRange = "month".equals(range);
        LocalDate startDate = monthRange ? LocalDate.now().withDayOfMonth(1) : LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> dateList = new ArrayList<>();
        List<BigDecimal> moneyList = new ArrayList<>();
        List<Long> orderCountList = new ArrayList<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalOrderCount = 0;

        Map<LocalDate, DailyStatVO> daily = loadDailyStat(shopId, startDate, endDate);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            DailyStatVO vo = daily.get(date);
            BigDecimal dayRevenue = vo == null || vo.getMoney() == null ? BigDecimal.ZERO : vo.getMoney();
            long dayCount = vo == null || vo.getCnt() == null ? 0L : vo.getCnt();

            dateList.add(date.format(formatter));
            moneyList.add(dayRevenue);
            orderCountList.add(dayCount);
            totalRevenue = totalRevenue.add(dayRevenue);
            totalOrderCount += dayCount;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("range", monthRange ? "month" : "today");
        result.put("startDate", startDate.format(formatter));
        result.put("endDate", endDate.minusDays(1).format(formatter));
        result.put("dateList", dateList);
        result.put("moneyList", moneyList);
        result.put("orderCountList", orderCountList);
        result.put("totalRevenue", totalRevenue);
        result.put("totalOrderCount", totalOrderCount);
        return result;
    }

    private Map<String, Object> buildMonthlyRevenueData(Long shopId, String month) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = parseMonth(month);

        List<String> dateList = new ArrayList<>();
        List<BigDecimal> moneyList = new ArrayList<>();
        List<Long> orderCountList = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalOrderCount = 0;

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        Map<LocalDate, DailyStatVO> daily = loadDailyStat(shopId, startDate, endDate.plusDays(1));

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyStatVO vo = daily.get(date);
            BigDecimal dayRevenue = vo == null || vo.getMoney() == null ? BigDecimal.ZERO : vo.getMoney();
            long dayCount = vo == null || vo.getCnt() == null ? 0L : vo.getCnt();

            dateList.add(date.format(dayFormatter));
            moneyList.add(dayRevenue);
            orderCountList.add(dayCount);
            totalRevenue = totalRevenue.add(dayRevenue);
            totalOrderCount += dayCount;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("month", yearMonth.format(monthFormatter));
        result.put("dateList", dateList);
        result.put("moneyList", moneyList);
        result.put("orderCountList", orderCountList);
        result.put("totalRevenue", totalRevenue);
        result.put("totalOrderCount", totalOrderCount);
        return result;
    }

    private Map<String, Object> buildOrderTrendData(Long shopId, int days) {
        List<String> dateList = new ArrayList<>();
        List<Long> orderCountList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        LocalDate startDate = LocalDate.now().minusDays(days - 1L);
        Map<LocalDate, DailyStatVO> daily = loadDailyStat(shopId, startDate, LocalDate.now().plusDays(1));

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            DailyStatVO vo = daily.get(date);
            dateList.add(date.format(formatter));
            orderCountList.add(vo == null || vo.getCnt() == null ? 0L : vo.getCnt());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dateList", dateList);
        result.put("orderCountList", orderCountList);
        return result;
    }

    /** 解析月份参数，非法或为空时退回当前月 */
    private YearMonth parseMonth(String month) {
        try {
            return (month == null || month.isBlank())
                    ? YearMonth.now()
                    : YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            return YearMonth.now();
        }
    }

    /** 一次查询取回区间内按天聚合的订单数与营业额 */
    private Map<LocalDate, DailyStatVO> loadDailyStat(Long shopId, LocalDate startDate, LocalDate endExclusive) {
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endExclusive.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<LocalDate, DailyStatVO> daily = new HashMap<>();
        for (DailyStatVO vo : orderMapper.selectDailyStat(shopId, start, end)) {
            if (vo.getDay() != null) {
                daily.put(vo.getDay(), vo);
            }
        }
        return daily;
    }

    private Date getOrderStatTime(Order order) {
        return order.getFinishTime() != null ? order.getFinishTime() : order.getCreateTime();
    }

    private boolean isInRange(Date target, Date start, Date end) {
        if (target == null) {
            return false;
        }
        boolean afterStart = start == null || !target.before(start);
        boolean beforeEnd = end == null || target.before(end);
        return afterStart && beforeEnd;
    }
}

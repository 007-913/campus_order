package org.example.campus_order.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.entity.Order;
import org.example.campus_order.service.OrderService;
import org.example.campus_order.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/pay")
public class PayController {

    private static final Set<String> PAID_ORDER_IDS = ConcurrentHashMap.newKeySet();
    private static final int STATUS_PENDING_PAYMENT = 0;
    private static final int STATUS_PENDING_ACCEPT = 1;
    private static final int STATUS_CANCELLED = 3;

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody Map<String, Object> body) {
        Object orderId = body.get("orderId");
        if (orderId == null || orderId.toString().trim().isEmpty()) {
            return Result.error("订单号不能为空");
        }
        Order order = orderService.getById(orderId.toString());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != null && order.getStatus() == STATUS_CANCELLED) {
            return Result.error("订单已取消，无法支付");
        }
        markOrderPaid(order);
        return Result.success("PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 18));
    }

    @GetMapping("/status")
    public Result<Boolean> status(@RequestParam String orderId) {
        Order order = orderService.getById(orderId);
        boolean paid = PAID_ORDER_IDS.contains(orderId) ||
                (order != null && order.getStatus() != null &&
                        order.getStatus() != STATUS_PENDING_PAYMENT &&
                        order.getStatus() != STATUS_CANCELLED);
        return Result.success(paid);
    }

    @GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qrcode(@RequestParam String orderId,
                                         @RequestParam(required = false) Long shopId,
                                         HttpServletRequest request) throws Exception {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String scanUrl = baseUrl + "/pay/scan?orderId=" + orderId +
                "&shopId=" + (shopId == null ? "" : shopId);
        BitMatrix matrix = new MultiFormatWriter().encode(scanUrl, BarcodeFormat.QR_CODE, 360, 360);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.IMAGE_PNG)
                .body(output.toByteArray());
    }

    @GetMapping(value = "/scan", produces = MediaType.TEXT_HTML_VALUE)
    public String scan(@RequestParam String orderId,
                       @RequestParam(required = false) Long shopId) {
        Order order = orderService.getById(orderId);
        BigDecimal payAmount = BigDecimal.ZERO;
        if (order != null && order.getTotalPrice() != null) {
            payAmount = order.getTotalPrice();
        }
        String statusText = getStatusText(order);
        String amountText = payAmount == null ? "0.00" : payAmount.setScale(2, RoundingMode.HALF_UP).toString();
        String shopText = shopId == null ? "-" : shopId.toString();

        return "<!doctype html><html><head><meta charset='utf-8'>" +
                "<meta name='viewport' content='width=device-width,initial-scale=1,maximum-scale=1'>" +
                "<title>校园点餐支付</title>" +
                "<style>" +
                "body{margin:0;background:#f4f1ec;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;color:#1f2937;}" +
                ".card{margin:42px 18px;padding:24px 20px;background:#fff;border-radius:20px;box-shadow:0 8px 24px rgba(31,41,55,.08);}" +
                ".title{font-size:22px;font-weight:700;margin-bottom:8px}.sub{font-size:13px;color:#6b7280;margin-bottom:24px}" +
                ".row{display:flex;justify-content:space-between;padding:13px 0;border-bottom:1px solid #eef2f7;font-size:15px}" +
                ".amount{font-size:30px;font-weight:800;color:#ef4444;text-align:center;margin:28px 0 8px}" +
                ".demo{text-align:center;color:#5f7fa3;font-size:13px;margin-bottom:22px;font-weight:700}" +
                ".btn{display:block;text-align:center;text-decoration:none;background:#5f7fa3;color:#fff;border-radius:24px;padding:13px 0;font-size:16px;font-weight:700}" +
                ".tip{text-align:center;color:#9ca3af;font-size:12px;margin-top:14px;line-height:20px}" +
                "</style></head><body><div class='card'>" +
                "<div class='title'>校园点餐支付</div>" +
                "<div class='sub'>手机扫码后显示的订单付款页</div>" +
                "<div class='row'><span>订单号</span><span>" + escape(orderId) + "</span></div>" +
                "<div class='row'><span>店铺ID</span><span>" + escape(shopText) + "</span></div>" +
                "<div class='row'><span>状态</span><span>" + statusText + "</span></div>" +
                "<div class='amount'>¥" + amountText + "</div>" +
                "<div class='demo'>演示支付，不涉及真实扣款</div>" +
                "<a class='btn' href='/pay/scan/success?orderId=" + escape(orderId) + "'>确认支付</a>" +
                "<div class='tip'>这是校园点餐演示支付页<br>真实收款需接入微信或支付宝支付接口</div>" +
                "</div></body></html>";
    }

    @GetMapping(value = "/scan/success", produces = MediaType.TEXT_HTML_VALUE)
    public String scanSuccess(@RequestParam String orderId) {
        Order order = orderService.getById(orderId);
        if (order != null && (order.getStatus() == null || order.getStatus() != STATUS_CANCELLED)) {
            markOrderPaid(order);
        }
        return "<!doctype html><html><head><meta charset='utf-8'>" +
                "<meta name='viewport' content='width=device-width,initial-scale=1,maximum-scale=1'>" +
                "<title>支付成功</title>" +
                "<style>body{margin:0;background:#f4f1ec;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;color:#1f2937}" +
                ".card{margin:80px 18px;padding:30px 20px;background:#fff;border-radius:20px;text-align:center;box-shadow:0 8px 24px rgba(31,41,55,.08)}" +
                ".ok{font-size:44px;color:#5f7fa3}.title{font-size:22px;font-weight:700;margin:14px 0}.sub{font-size:13px;color:#6b7280;line-height:22px}</style>" +
                "</head><body><div class='card'><div class='ok'>✓</div><div class='title'>支付成功</div>" +
                "<div class='sub'>订单号：" + escape(orderId) + "<br>请回到点餐应用查看订单</div></div></body></html>";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String getStatusText(Order order) {
        if (order == null) {
            return "订单不存在，仅展示扫码效果";
        }
        if (order.getStatus() == null) {
            return "状态未知";
        }
        if (order.getStatus() == STATUS_PENDING_PAYMENT) {
            return "订单待支付";
        }
        if (order.getStatus() == STATUS_PENDING_ACCEPT) {
            return "订单待接单";
        }
        if (order.getStatus() == 2) {
            return "订单已完成";
        }
        if (order.getStatus() == STATUS_CANCELLED) {
            return "订单已取消";
        }
        return "状态未知";
    }

    private void markOrderPaid(Order order) {
        if (order.getStatus() != null && order.getStatus() == STATUS_CANCELLED) {
            return;
        }
        PAID_ORDER_IDS.add(order.getId());
        if (order.getStatus() != null && order.getStatus() == STATUS_PENDING_PAYMENT) {
            order.setStatus(STATUS_PENDING_ACCEPT);
            orderService.updateById(order);
        }
    }
}

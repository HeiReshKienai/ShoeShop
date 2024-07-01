package com.hutech.ShoeShop.controller;

import com.hutech.ShoeShop.DTO.PaymentResDTO;
import com.hutech.ShoeShop.config.Config;
import com.hutech.ShoeShop.model.OrderDetail;
import com.hutech.ShoeShop.service.OrderDetailService;
import com.hutech.ShoeShop.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/create_payment/{orderId}")
    public String createPayment(HttpServletRequest req, @PathVariable Long orderId) throws UnsupportedEncodingException {

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);

        long amount = 0;
        for (OrderDetail orderDetail : orderDetails) {
            amount += orderDetail.getProductPrice() * orderDetail.getQuantity();
        }
        amount *= 100;

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", Config.vnp_Version);
        vnp_Params.put("vnp_Command", Config.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_ReturnUrl", "http://localhost:8080/api/payment/payment_info?orderId=" + orderId);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        vnp_Params.put("vnp_IpAddr", "13.160.92.202");

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (!fieldName.equals(fieldNames.get(fieldNames.size() - 1))) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        return "redirect:" + paymentUrl;
    }

    @GetMapping("/payment_info")
    public String transaction(@RequestParam(value = "vnp_ResponseCode") String responseCode, @RequestParam(value = "orderId") Long orderId) {
        if (responseCode.equals("00")) {
            orderService.updateOrderStatus(orderId, "Đã thanh toán");
            return "redirect:/order/confirmation?orderId=" + orderId + "&paymentSuccess=true";
        } else {
            return "redirect:/profile/history?paymentSuccess=false";
        }
    }

}

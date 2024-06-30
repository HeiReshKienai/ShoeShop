package com.hutech.ShoeShop.controller;


import com.hutech.ShoeShop.DTO.PaymentResDTO;
import com.hutech.ShoeShop.config.Config;
import com.hutech.ShoeShop.model.OrderDetail;
import com.hutech.ShoeShop.service.OrderDetailService;
import com.hutech.ShoeShop.service.OrderService;
import groovy.lang.GString;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/create_payment/{orderId}")
    public String createPayment(HttpServletRequest req, @PathVariable Long orderId) throws UnsupportedEncodingException {


//        String orderType = "other";
//        long amount = Integer.parseInt(req.getParameter("amount")) * 100;
//        String bankCode = req.getParameter("bankCode");

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);

        long amount = 0;

        for (OrderDetail orderDetail : orderDetails) {
            amount += orderDetail.getProductPrice() * orderDetail.getQuantity();
        }

        amount*=100;

        String vnp_TxnRef = Config.getRandomNumber(8);

        //String vnp_IpAddr = Config.getIpAddress(req);
       // String vnp_IpAddr = "13.160.92.202";

        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", Config.vnp_Version);
        vnp_Params.put("vnp_Command", Config.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_CurrCode", "VND");


        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_ReturnUrl", "http://localhost:8080/api/payment/payment_info");
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
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
//        PaymentResDTO paymentResDTO  = new PaymentResDTO();
//        paymentResDTO.setStatus("Ok");
//        paymentResDTO.setMessage("Successfully");
//        paymentResDTO.setURL(paymentUrl);

        return "redirect:"+paymentUrl;
        //"url": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=1000000&vnp_BankCode=NCB&vnp_Command=pay&vnp_CreateDate=20240630230408&vnp_CurrCode=VND&vnp_ExpireDate=20240630231908&vnp_IpAddr=13.160.92.202&vnp_Locale=vn&vnp_OrderInfo=Thanh+toan+don+hang%3A41411152&vnp_OrderType=other&vnp_ReturnUrl=http%3A%2F%2Flocalhost%3A8080%2Fproducts&vnp_TmnCode=698PAAJG&vnp_TxnRef=41411152&vnp_Version=2.1.0&vnp_SecureHash=5abca1b034763f2cabeee5c0382212b6ca47a9c05cbdf113acba5b95de041ae076ea2aba7c95a3e5eda642d3c65070d7d7a973211deea51a52d49d4085f5f397"
    }
    @GetMapping("/payment_info")
    public String transaction(@RequestParam(value = "vnp_ResponseCode") String responseCode) {

        if (responseCode.equals("00")) {
            return "redirect:https://www.facebook.com/";

        } else {
            return "/api/payment";
        }

//        return "redirect:https://www.facebook.com/";
    }

}

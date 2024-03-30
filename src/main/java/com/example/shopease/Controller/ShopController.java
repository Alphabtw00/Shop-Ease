package com.example.shopease.Controller;


import com.example.shopease.Entity.Coupon;
import com.example.shopease.Entity.Order;
import com.example.shopease.Entity.Product;
import com.example.shopease.Entity.User;
import com.example.shopease.Repository.OrderRepository;
import com.example.shopease.Repository.ProductRepository;
import com.example.shopease.Repository.UserRepository;
import com.example.shopease.Service.CouponService;
import com.example.shopease.Service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping
@Slf4j
public class ShopController {
    private final CouponService couponService;
    private final ProductService productService;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    public ShopController(CouponService couponService, ProductService productService,
                          UserRepository userRepository, OrderRepository orderRepository,
                          ProductRepository productRepository) {
        this.couponService = couponService;
        this.productService = productService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> getInventory() {
        Product product = productService.getSingleProductFromRepo();

        Map<String, Object> response = new HashMap<>();
        response.put("ordered", product.getOrderedQuantity());
        response.put("price", product.getPrice());
        response.put("available", product.getAvailableQuantity());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetchCoupons")
    public ResponseEntity<Map<String, Float>> fetchCoupons() {
        List<Coupon> couponList = couponService.getAllCoupons();

        Map<String, Float> response = new HashMap<>();
        couponList.forEach(coupon -> {
            response.put(coupon.getCode(), coupon.getDiscountValue());
        });
        return ResponseEntity.ok(response);
    }


    @Transactional
    @PostMapping("/{userId}/order")
    public ResponseEntity<?> createOrder(@PathVariable Long userId,
                                         @RequestParam(name = "qty") int orderedQuantity,
                                         @RequestParam(name = "coupons", required = false) String couponName) {
        Product product = productService.getSingleProductFromRepo();
        log.info("product {}", product);
        User user = userRepository.findById(userId).get();
        log.info("user {}" , user);

        Coupon coupon = null;

        if(couponName!=null){
            coupon = couponService.getCoupon(couponName);

            if (coupon == null || user.getCouponList().contains(coupon)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("description", "Invalid coupon"));
            }
            log.info("coupon {}", coupon);
        }



        if (orderedQuantity < 1 || orderedQuantity > product.getAvailableQuantity()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("description", "Invalid quantity"));
        }



        double orderAmount = orderedQuantity * product.getPrice();

        if (coupon != null) {
            orderAmount = orderAmount * (1 - coupon.getDiscountValue() / 100.0);
        }




        product.setOrderedQuantity(product.getOrderedQuantity()+ orderedQuantity);
        product.setAvailableQuantity(product.getAvailableQuantity()- orderedQuantity);

        Order order = new Order();
        order.setUser(user);
        order.setCoupon(coupon);
        order.setAmount(orderAmount);
        order.setDate(new Date());
        order.getProductList().add(product);


        Order savedOrder = orderRepository.save(order); //cascading will save products aswell


        user.getOrderList().add(order);
        if(coupon!=null){
            user.getCouponList().add(coupon);

        }

        User savedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();

        response.put("orderId", savedOrder.getId());
        response.put("userId", savedUser.getId());
        response.put("quantity", orderedQuantity);
        response.put("amount", savedOrder.getAmount());
        if(coupon!=null){
            response.put("coupon", coupon.getCode());

        }
        return ResponseEntity.ok(response);
    }



    @PostMapping("/{userId}/{orderId}/pay")
    public ResponseEntity<?> processPayment(@PathVariable Long userId,
                                            @PathVariable Long orderId,
                                            @RequestParam(name = "amount") double amountToBePaid) {
        Order order = orderRepository.findById(orderId).get();
        Map<String, String> response = new HashMap<>();

        if(order==null){
            response.put("userId", userId.toString());
            response.put("orderId", orderId.toString());
            response.put("transactionId", "tran010100004");
            response.put("status", "failed");
            response.put("description", "Payment Failed due to invalid order id");
            return ResponseEntity.badRequest().body(response);
        }

        if(order.getAmount()!=amountToBePaid){
            response.put("userId", order.getUser().getId().toString());
            response.put("orderId", order.getId().toString());
            response.put("transactionId", "tran010100004");
            response.put("status", "failed");
            response.put("description",  "Payment Failed as amount is invalid");
            return ResponseEntity.badRequest().body(response);

        }

        if(order.getPaidFor()){
            response.put("userId", order.getUser().getId().toString());
            response.put("orderId", order.getId().toString());
            response.put("transactionId", order.getTransactionId());
            response.put("status", "failed");
            response.put("description", "Order is already paid for");
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(response);  //405 uses a response entity body builder
        }


        order.setTransactionId(UUID.randomUUID().toString());
        order.setPaidFor(true);

        Order savedOrder = orderRepository.save(order);

        response.put("userId", order.getUser().getId().toString());
        response.put("orderId", order.getId().toString());
        response.put("transactionId", order.getTransactionId());
        response.put("status", "successful");
        return ResponseEntity.ok(response);


    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<Map<String, String>>> getOrders(@PathVariable Long userId) {
        List<Order> orderList = userRepository.findById(userId).get().getOrderList();
        List<Map<String, String>> list = new ArrayList<>();
        for (Order order : orderList) {
            Map<String, String> orderMap = new HashMap<>();
            orderMap.put("orderId", String.valueOf(order.getId()));
            orderMap.put("amount", String.valueOf(order.getAmount()));
            orderMap.put("date", order.getDate().toString());
            if(order.getCoupon()!=null){
                orderMap.put("coupon", order.getCoupon().getCode());
            }
            else {
                orderMap.put("coupon", "No coupon applied");
            }
            orderMap.put("paid ?" , order.getPaidFor().toString());
            list.add(orderMap);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long userId,
                                      @PathVariable Long orderId){
        User user = userRepository.findById(userId).get();
        Order order = orderRepository.findById(orderId).get();

        Map<String, String> response = new HashMap<>();

        if(order==null){
            response.put("orderId", orderId.toString());
            response.put("description",  "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(response);
        }


        response.put("orderId", order.getId().toString());
        response.put("amount", order.getAmount().toString());
        if(order.getCoupon()!=null){
            response.put("coupon",  order.getCoupon().getCode());

        }
        else {
            response.put("coupon", "No coupon applied");
        }
        response.put("transactionId: ", order.getTransactionId());
        response.put("status", "successful");
        response.put("paid ?" , order.getPaidFor().toString());

        return ResponseEntity.ok(response);
    }
}

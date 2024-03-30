package com.example.shopease.Config;

import com.example.shopease.Entity.Coupon;
import com.example.shopease.Entity.Product;
import com.example.shopease.Entity.User;
import com.example.shopease.Repository.CouponRepository;
import com.example.shopease.Repository.ProductRepository;
import com.example.shopease.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
public class CommandLineConfig {

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, ProductRepository productRepository,
                                        CouponRepository couponRepository){
        return args -> {


            Product product = new Product(UUID.randomUUID().toString(), "product1", 100,
                    10, 0);

            productRepository.save(product);



            User user1 = new User(1L, "admin");
            User user2 = new User(2L, "user");
            List<User> userList = List.of(user1, user2);
            userRepository.saveAll(userList);



            Coupon coupon1 = new Coupon("OFF5", 5);
            Coupon coupon2 = new Coupon("OFF10", 10);
            Coupon coupon3 = new Coupon("OFF15", 15);
            Coupon coupon4 = new Coupon("OFF50", 50);

            List<Coupon> couponList = List.of(coupon1, coupon2, coupon3, coupon4);
            couponRepository.saveAll(couponList);
        };
    }
}

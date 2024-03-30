package com.example.shopease.Service;

import com.example.shopease.Entity.Coupon;
import com.example.shopease.Repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> getAllCoupons(){
        Iterable<Coupon> opitonalCoupon = couponRepository.findAll();
        List<Coupon> couponList = new ArrayList<>();
        opitonalCoupon.forEach(couponList::add);
        return couponList;
    }

    public Coupon getCoupon(String couponName){
        return couponRepository.findById(couponName).orElse(null);
    }
}

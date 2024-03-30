package com.example.shopease.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "Orders") //cause order is reserved as table name and gives error

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // should have done this, but as im not setting up spring security we dont need it, UUID.randomUUID().toString();
    private String transactionId;
    private Double amount;
    private Boolean paidFor = false;
    private Date date;


    @ManyToOne
    @JoinColumn(name = "orderList")
    @ToString.Exclude //so it doesnt go start reucrisve call due to bidirectional relationship
    private User user;

    @OneToOne
    private Coupon coupon;


    @ManyToMany
    @JoinTable(
            name = "order_product_ref",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> productList = new ArrayList<>();



}

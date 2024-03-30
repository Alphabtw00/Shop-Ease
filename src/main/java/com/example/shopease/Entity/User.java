package com.example.shopease.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Users") //cause user is reserved as table name and gives error
@Data
@NoArgsConstructor
public class User {

    @Id
    private Long  id;  // could have done this, but as im not setting up spring security we dont need it, UUID.randomUUID().toString();

    private String username;

    @ManyToMany
    @JoinTable(
            name = "user_coupon_ref",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_name"))
    private List<Coupon> couponList = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) //instead of creating table, it gives ownership to user field in Order class which adds its foreign key as a column instead
    private List<Order> orderList = new ArrayList<>();

    public User(Long id, String username){
        this.id= id;
        this.username = username;
    }
}

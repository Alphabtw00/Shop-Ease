package com.example.shopease.Service;

import com.example.shopease.Entity.Product;
import com.example.shopease.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(String productId) {
        return productRepository.findById(productId).orElse(null);
    }

    public List<Product> getAllProducts(){
        Iterable<Product> opitonalProduct= productRepository.findAll();
        List<Product> productList = new ArrayList<>();
        opitonalProduct.forEach(productList::add);
        return productList;
    }

    public Product getSingleProductFromRepo(){  //made just as theres 1 product in database
        Iterable<Product> opitonalProduct= productRepository.findAll();
        List<Product> productList = new ArrayList<>();
        opitonalProduct.forEach(productList::add);
        return productList.get(0);
    }
}

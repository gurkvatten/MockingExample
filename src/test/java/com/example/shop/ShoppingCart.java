package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private final List<Double> prices = new ArrayList<>();

    public void addItem(String name, double price) {
        prices.add(price);
    }

    public double totalPrice() {
        return prices.stream().mapToDouble(Double::doubleValue).sum();
    }
}

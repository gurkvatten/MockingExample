package com.example.shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingCart {

    private double discountPercent = 0.0;


    public void updateQuantity(String name, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Double price = null;
        for (Item item : items) {
            if (item.name.equals(name)) {
                price = item.price;
                break;
            }
        }

        if (price == null) {
            return;
        }

        items.removeIf(i -> i.name.equals(name));

        for (int i = 0; i < quantity; i++) {
            items.add(new Item(name, price));
        }
    }

    public void applyDiscount(double percent) {
        discountPercent = percent;
    }



    private static class Item {
        String name;
        double price;

        Item(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    private final List<Item> items = new ArrayList<>();

    public void addItem(String name, double price) {
        items.add(new Item(name, price));
    }

    public void removeItem(String name) {
        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().name.equals(name)) {
                iterator.remove();
                return;
            }
        }
    }

    public double totalPrice() {
        double sum = items.stream()
                .mapToDouble(item -> item.price)
                .sum();

        return sum * (1.0 - (discountPercent / 100.0));
    }

}

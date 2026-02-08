package com.example.shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingCart {

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
        return items.stream()
                .mapToDouble(item -> item.price)
                .sum();
    }
}

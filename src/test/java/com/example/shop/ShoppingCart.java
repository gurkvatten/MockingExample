package com.example.shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingCart {

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

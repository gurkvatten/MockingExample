package com.example;

import com.example.shop.ShoppingCart;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    @Test
    void totalPrice_shouldBeZero_forEmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        assertThat(cart.totalPrice()).isZero();
    }
    @Test
    void totalPrice_shouldBeSumOfAddedItemPrices() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Apple", 10.0);

        assertThat(cart.totalPrice()).isEqualTo(10.0);
    }

}

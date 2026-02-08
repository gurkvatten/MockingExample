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
}

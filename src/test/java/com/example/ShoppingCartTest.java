package com.example;

import com.example.shop.ShoppingCart;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @Test
    void totalPrice_shouldSumMultipleItems() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Apple", 10.0);
        cart.addItem("Banana", 5.5);

        assertThat(cart.totalPrice()).isEqualTo(15.5);
    }
    @Test
    void removeItem_shouldRemoveItemAndUpdateTotalPrice() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Apple", 10.0);
        cart.addItem("Banana", 5.0);

        cart.removeItem("Apple");

        assertThat(cart.totalPrice()).isEqualTo(5.0);
    }
    @Test
    void totalPrice_shouldHandleMultipleAddsOfSameItem() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Apple", 10.0);
        cart.addItem("Apple", 10.0);

        assertThat(cart.totalPrice()).isEqualTo(20.0);
    }
    @Test
    void updateQuantity_shouldChangeTotalPrice() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Apple", 10.0);
        cart.updateQuantity("Apple", 3);

        assertThat(cart.totalPrice()).isEqualTo(30.0);
    }
    @Test
    void totalPrice_shouldApplyDiscountPercentage() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem("Apple", 100.0);
        cart.applyDiscount(10.0);

        assertThat(cart.totalPrice()).isEqualTo(90.0);
    }
    @Test
    void updateQuantity_shouldThrow_whenQuantityIsNegative() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 10.0);

        assertThatThrownBy(() -> cart.updateQuantity("Apple", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }
    @Test
    void applyDiscount_shouldThrow_whenDiscountIsInvalid() {
        ShoppingCart cart = new ShoppingCart();

        assertThatThrownBy(() -> cart.applyDiscount(-1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> cart.applyDiscount(150))
                .isInstanceOf(IllegalArgumentException.class);
    }






}

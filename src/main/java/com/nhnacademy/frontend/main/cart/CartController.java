package com.nhnacademy.frontend.main.cart;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/08
 */
@Controller
public class CartController {
    // todo: delete example Class
    private class Item {
        public String id;
        public String title;
        public Long quantity;
        public Long price;
        public Long discount;

        public Item(String id, String title, Long quantity, Long price, Long discount) {
            this.id = id;
            this.title = title;
            this.quantity = quantity;
            this.price = price;
            this.discount = discount;
        }
    }

    /**
     * getCartPage
     * @return mav
     */
    @GetMapping("/cart")
    public ModelAndView getCartPage() {
        ModelAndView mav = new ModelAndView("index/main/cart/cart");

        // todo: delete example data.
        List<Item> cart = new ArrayList<>();
        cart.add(new Item("KNB001", "example item", 5L, 5000L, 1000L));
        cart.add(new Item("KNB002", "example item", 5L, 5000L, 1000L));
        cart.add(new Item("KNB003", "example item", 5L, 5000L, 1000L));
        cart.add(new Item("KNB004", "example item", 5L, 5000L, 1000L));

        mav.addObject("cart", cart);

        return mav;
    }
}

package com.nhnacademy.frontend.main.order;

import com.nhnacademy.frontend.main.cart.CartController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Order Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/08
 */
@Controller
@RequestMapping("/order")
public class OrderController {
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
     * getOrderPage
     * @return mav
     */
    @PostMapping
    public ModelAndView getOrderPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/main/order/order");

        // Item Id와 갯수 추출
        // todo: id -> Isbn으로 수정 필요
        for (Iterator<String> it = request.getParameterNames().asIterator(); it.hasNext(); ) {
            String field = it.next();

            if(field.contains("item-checkbox-")) {
                String itemId = field.split("-")[2];

                System.out.println("item id(" + itemId + ") : " + request.getParameter("quantity-" + itemId));
            }
        }

        // todo: delete example data.
        List<Item> cart = new ArrayList<>();
        cart.add(new Item("KNB001", "example item 1", 5L, 5000L, 1000L));
        cart.add(new Item("KNB002", "example item 2", 5L, 5000L, 1000L));
        cart.add(new Item("KNB003", "example item 3", 5L, 5000L, 1000L));
        cart.add(new Item("KNB004", "example item 4", 5L, 5000L, 1000L));

        mav.addObject("cart", cart);

        return mav;
    }

    @GetMapping("/input/wrap")
    public ModelAndView getWrapPopupPage() {
        ModelAndView mav = new ModelAndView("index/main/order/input_wrap");

        return mav;
    }
}

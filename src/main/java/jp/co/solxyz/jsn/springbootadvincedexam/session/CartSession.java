package jp.co.solxyz.jsn.springbootadvincedexam.session;

import jp.co.solxyz.jsn.springbootadvincedexam.session.dto.Cart;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@SessionScope
public class CartSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<Cart> cartList = new ArrayList<>();

    public void addCart(Cart cart) {
        boolean isExist = cartList.stream()
                .anyMatch(c -> c.getIsbn().equals(cart.getIsbn()));
        if (!isExist) {
            cartList.add(cart);
        }
    }

    public void removeCart(String isbn) {
        cartList.stream()
                .filter(cart -> cart.getIsbn().equals(isbn))
                .findFirst()
                .ifPresent(cartList::remove);
    }

    public void clearCart() {
        cartList.clear();
    }

}

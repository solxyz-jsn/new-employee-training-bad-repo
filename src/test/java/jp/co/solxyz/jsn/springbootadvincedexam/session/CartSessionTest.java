package jp.co.solxyz.jsn.springbootadvincedexam.session;

import jp.co.solxyz.jsn.springbootadvincedexam.session.dto.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class CartSessionTest {

    @InjectMocks
    private CartSession cartSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("同じISBNがリストにない場合、カートに追加する")
    void shouldAddCartWhenISBNIsNotInTheList() {
        String expected = "1234567890123";
        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        cartSession.addCart(cart);

        assertThat(cartSession.getCartList().size()).isEqualTo(1);
        assertThat(cartSession.getCartList().get(0).getIsbn()).isEqualTo(expected);
    }

    @Test
    @DisplayName("同じISBNが既にリストにある場合、カートに追加しない")
    void shouldNotAddCartWhenISBNIsAlreadyInTheList() {
        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        cartSession.addCart(cart);
        cartSession.addCart(cart);

        assertThat(cartSession.getCartList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("一致するISBNがリストにある場合、カートから削除する")
    void shouldRemoveCartWhenISBNIsInTheList() {
        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        cartSession.addCart(cart);
        cartSession.removeCart("1234567890123");

        assertThat(cartSession.getCartList()).isEmpty();
    }

    @Test
    @DisplayName("一致するISBNがリストにない場合、カートから削除しない")
    void shouldNotRemoveCartWhenISBNIsNotInTheList() {
        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        cartSession.addCart(cart);
        cartSession.removeCart("0987654321");

        assertThat(cartSession.getCartList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("カートリストをクリアする")
    void shouldClearCartList() {
        Cart cart = new Cart();
        cart.setIsbn("1234567890123");
        cartSession.addCart(cart);
        cartSession.clearCart();

        assertThat(cartSession.getCartList()).isEmpty();
    }
}

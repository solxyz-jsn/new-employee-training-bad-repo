package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.api;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.json.CartIsbn;
import jp.co.solxyz.jsn.springbootadvincedexam.session.CartSession;
import jp.co.solxyz.jsn.springbootadvincedexam.session.dto.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BookCartApiControllerTest {

    @InjectMocks
    private BookCartApiController bookCartApiController;

    @Mock
    private CartSession cartSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("カートへの追加時にISBNがnullでない場合、カートに書籍を追加する")
    void shouldAddBookToCartWhenIsbnIsNotNull() {
        CartIsbn cartIsbn = new CartIsbn();
        cartIsbn.setIsbn("1234567890123");

        Cart cart = new Cart();
        cart.setIsbn(cartIsbn.getIsbn());

        ResponseEntity<Void> response = bookCartApiController.add(cartIsbn);

        verify(cartSession, times(1)).addCart(cart);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("カートへの追加時にISBNがnullの場合、BadRequestが返される")
    void shouldReturnBadRequestWhenIsbnIsNullOnAdd() {
        CartIsbn cartIsbn = new CartIsbn();
        cartIsbn.setIsbn(null);

        Cart cart = new Cart();
        cart.setIsbn(cartIsbn.getIsbn());

        ResponseEntity<Void> response = bookCartApiController.add(cartIsbn);

        verify(cartSession, never()).addCart(cart);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("削除時にISBNがnullでない場合、カートから書籍を削除する")
    void shouldRemoveBookFromCartWhenIsbnIsNotNull() {
        CartIsbn cartIsbn = new CartIsbn();
        cartIsbn.setIsbn("1234567890123");

        ResponseEntity<Void> response = bookCartApiController.delete(cartIsbn);

        verify(cartSession, times(1)).removeCart(cartIsbn.getIsbn());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("削除時にISBNがnullの場合、BadRequestが返される")
    void shouldReturnBadRequestWhenIsbnIsNullOnDelete() {
        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        CartIsbn cartIsbn = new CartIsbn();
        cartIsbn.setIsbn(null);

        ResponseEntity<Void> response = bookCartApiController.delete(cartIsbn);

        verify(cartSession, never()).removeCart(anyString());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

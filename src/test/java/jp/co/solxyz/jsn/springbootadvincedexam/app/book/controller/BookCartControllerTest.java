package jp.co.solxyz.jsn.springbootadvincedexam.app.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller.BookCartController;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookCartService;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import jp.co.solxyz.jsn.springbootadvincedexam.session.CartSession;
import jp.co.solxyz.jsn.springbootadvincedexam.session.dto.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookCartControllerTest {

    @InjectMocks
    private BookCartController bookCartController;

    @Mock
    private CartSession cartSession;

    @Mock
    private BookCartService bookCartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("カートに書籍が1冊ある状態でindexが呼び出された場合、cartListに1件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalled() {
        Cart cart = new Cart();
        cart.setIsbn("1234567890");

        List<Cart> carts = List.of(cart);
        List<Cart> expectedCarts = List.of(cart);

        CartBookModel cartBook = new CartBookModel("1234567890", "Test Book", "Test Author", "Test Publisher");

        List<CartBookModel> cartBooks = List.of(cartBook);
        List<CartBookModel> expectedCartBooks = List.of(cartBook);

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartService.getCartList(carts)).thenReturn(cartBooks);

        ModelAndView response = bookCartController.index();

        verify(bookCartService, times(1)).getCartList(expectedCarts);
        assertThat(response.getViewName()).isEqualTo("general/bookcart");
        assertThat(response.getModel().get("cartList")).isEqualTo(expectedCartBooks);
    }

    @Test
    @DisplayName("カートに書籍が複数ある状態でindexが呼び出された場合、cartListに複数件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalledWithMultipleBooks() {
        Cart cart1 = new Cart();
        cart1.setIsbn("1234567890");
        Cart cart2 = new Cart();
        cart2.setIsbn("0987654321");

        List<Cart> carts = List.of(cart1, cart2);
        List<Cart> expectedCarts = List.of(cart1, cart2);

        CartBookModel cartBook1 = new CartBookModel("1234567890", "Test Book1", "Test Author1", "Test Publisher1");
        CartBookModel cartBook2 = new CartBookModel("0987654321", "Test Book2", "Test Author2", "Test Publisher2");

        List<CartBookModel> cartBooks = List.of(cartBook1, cartBook2);
        List<CartBookModel> expectedCartBooks = List.of(cartBook1, cartBook2);

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartService.getCartList(carts)).thenReturn(cartBooks);

        ModelAndView response = bookCartController.index();

        verify(bookCartService, times(1)).getCartList(expectedCarts);
        assertThat(response.getViewName()).isEqualTo("general/bookcart");
        assertThat(response.getModel().get("cartList")).isEqualTo(expectedCartBooks);
    }

    @Test
    @DisplayName("カートに何もない状態でindexが呼び出された場合、空のカートが返される")
    void shouldEmptyDisplayCartWhenIndexIsCalled() {
        when(cartSession.getCartList()).thenReturn(Collections.emptyList());
        when(bookCartService.getCartList(anyList())).thenReturn(Collections.emptyList());

        ModelAndView response = bookCartController.index();

        verify(bookCartService, times(1)).getCartList(Collections.emptyList());
        assertThat(response.getViewName()).isEqualTo("general/bookcart");
        assertThat(response.getModel().get("cartList")).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("有効なユーザー情報がある場合、カート内の書籍の貸し出し処理をする")
    void shouldCheckedOutBookToCartWhenValidUserDetails() {
        String expectedUserId = "user1";
        String userId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUserId()).thenReturn(userId);
        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartService.checkout(userId, List.of(cart))).thenReturn(List.of());

        ModelAndView response = bookCartController.checkout(userDetails);

        verify(bookCartService, times(1)).checkout(expectedUserId, List.of(cart));
        assertThat(response.getViewName()).isEqualTo("general/bookcart");
        assertThat(response.getModel().get("cartList")).isNull();
    }

    @Test
    @DisplayName("有効なユーザ情報がない場合、エラーが返される")
    void shouldReturnErrorWhenExceptionIsThrownOnCheckedOut() {
        String expectedUserId = "user1";
        String userId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        MyUserDetails userDetails = mock(MyUserDetails.class);
        when(userDetails.getUserId()).thenReturn("user1");
        when(cartSession.getCartList()).thenReturn(List.of(cart));
        doThrow(DataIntegrityViolationException.class).when(bookCartService).checkout(userId, List.of(cart));

        ModelAndView response = bookCartController.checkout(userDetails);

        verify(bookCartService, times(1)).checkout(expectedUserId, List.of(cart));
        assertThat(response.getViewName()).isEqualTo("general/bookcart");
        assertThat(response.getModel().get("errorMessage")).isEqualTo("処理中にエラーが発生しました。");
    }

    @Test
    @DisplayName("すでに借りている書籍を借りる場合、借りれなかった書籍として返される")
    void shouldReturnErrorWhenSomeBooksAreNotCheckedOut() {
        String expectedUserId = "user1";
        String userId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        BookModel expectedBook = new BookModel();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Book");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");

        MyUserDetails userDetails = mock(MyUserDetails.class);

        when(userDetails.getUserId()).thenReturn(userId);
        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartService.checkout(userId, List.of(cart))).thenReturn(List.of(book));

        ModelAndView response = bookCartController.checkout(userDetails);

        List<BookModel> result = (List<BookModel>) response.getModel().get("cartList");

        verify(bookCartService, times(1)).checkout(expectedUserId, List.of(cart));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(expectedBook);
        assertThat(response.getViewName()).isEqualTo("general/bookcart");
        assertThat(response.getModel().get("errorMessage")).isEqualTo("以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。");
    }

    @Test
    @DisplayName("すでに借りている書籍を複数件借りる場合、複数件の借りれなかった書籍が返される")
    void shouldReturnErrorWhenMultipleBooksAreNotCheckedOut() {
        String expectedUserId = "user1";
        String userId = "user1";

        Cart cart1 = new Cart();
        cart1.setIsbn("1234567890123");
        Cart cart2 = new Cart();
        cart2.setIsbn("0987654321098");

        BookModel expectedBook1 = new BookModel();
        expectedBook1.setIsbn("1234567890123");
        expectedBook1.setTitle("Test Book1");
        expectedBook1.setAuthor("Test Author1");
        expectedBook1.setPublisher("Test Publisher1");
        BookModel expectedBook2 = new BookModel();
        expectedBook2.setIsbn("0987654321098");
        expectedBook2.setTitle("Test Book2");
        expectedBook2.setAuthor("Test Author2");
        expectedBook2.setPublisher("Test Publisher2");

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Book1");
        book1.setAuthor("Test Author1");
        book1.setPublisher("Test Publisher1");
        Book book2 = new Book();
        book2.setIsbn("0987654321098");
        book2.setTitle("Test Book2");
        book2.setAuthor("Test Author2");
        book2.setPublisher("Test Publisher2");

        MyUserDetails userDetails = mock(MyUserDetails.class);

        when(userDetails.getUserId()).thenReturn(userId);
        when(cartSession.getCartList()).thenReturn(List.of(cart1, cart2));
        when(bookCartService.checkout(userId, List.of(cart1, cart2))).thenReturn(List.of(book1, book2));

        ModelAndView response = bookCartController.checkout(userDetails);

        List<BookModel> result = (List<BookModel>) response.getModel().get("cartList");

        verify(bookCartService, times(1)).checkout(expectedUserId, List.of(cart1, cart2));
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(expectedBook1);
        assertThat(result.get(1)).isEqualTo(expectedBook2);
        assertThat(response.getViewName()).isEqualTo("general/bookcart");
        assertThat(response.getModel().get("errorMessage")).isEqualTo("以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。");
    }
}

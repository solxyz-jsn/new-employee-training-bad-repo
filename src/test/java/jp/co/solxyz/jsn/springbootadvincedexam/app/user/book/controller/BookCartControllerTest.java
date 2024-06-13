package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BookCartControllerTest {

    @MockBean
    private CartSession cartSession;

    @MockBean
    private BookCartService bookCartService;

    @Mock
    private MyUserDetails userDetails;

    private MockMvc mockMvc;

    private final WebApplicationContext context;

    private final String USER_ID = "user1";

    BookCartControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/")
                        .with(user(userDetails)))
                .defaultRequest(MockMvcRequestBuilders.post("/")
                        .with(user(userDetails))
                        .with(csrf()))
                .build();

        when(userDetails.getUserId()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("カートに書籍が1冊ある状態でindexが呼び出された場合、cartListに1件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalled() throws Exception {
        Cart cart = new Cart();
        cart.setIsbn("1234567890");

        List<Cart> carts = List.of(cart);
        List<Cart> expectedCarts = List.of(cart);

        CartBookModel cartBook = new CartBookModel("1234567890", "Test Book", "Test Author", "Test Publisher");

        List<CartBookModel> cartBooks = List.of(cartBook);
        List<CartBookModel> expectedCartBooks = List.of(cartBook);

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartService.getCartList(carts)).thenReturn(cartBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks));
        verify(bookCartService, times(1)).getCartList(expectedCarts);
    }

    @Test
    @DisplayName("カートに書籍が複数ある状態でindexが呼び出された場合、cartListに複数件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalledWithMultipleBooks() throws Exception {
        Cart cart1 = new Cart();
        cart1.setIsbn("1234567890");
        Cart cart2 = new Cart();
        cart2.setIsbn("0987654321");

        List<Cart> carts = List.of(cart1, cart2);

        CartBookModel cartBook1 = new CartBookModel("1234567890", "Test Book1", "Test Author1", "Test Publisher1");
        CartBookModel cartBook2 = new CartBookModel("0987654321", "Test Book2", "Test Author2", "Test Publisher2");

        List<CartBookModel> cartBooks = List.of(cartBook1, cartBook2);
        List<CartBookModel> expectedCartBooks = List.of(cartBook1, cartBook2);

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartService.getCartList(carts)).thenReturn(cartBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks));
    }

    @Test
    @DisplayName("カートに何もない状態でindexが呼び出された場合、空のカートが返される")
    void shouldEmptyDisplayCartWhenIndexIsCalled() throws Exception {
        when(cartSession.getCartList()).thenReturn(Collections.emptyList());
        when(bookCartService.getCartList(anyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", Collections.emptyList()));

        verify(bookCartService, times(1)).getCartList(Collections.emptyList());
    }

    @Test
    @DisplayName("有効なユーザー情報がある場合、カート内の書籍の貸し出し処理をする")
    void shouldCheckedOutBookToCartWhenValidUserDetails() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartService.co(USER_ID, List.of(cart))).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("cartList"));

        verify(bookCartService, times(1)).co(expectedUserId, List.of(cart));
    }

    @Test
    @DisplayName("チェックアウト時にDataIntegrityViolationExceptionが発生した場合、エラーが返される")
    void shouldReturnErrorWhenExceptionIsThrownOnCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        doThrow(DataIntegrityViolationException.class).when(bookCartService).co(USER_ID, List.of(cart));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "処理中にエラーが発生しました。"));

        verify(bookCartService, times(1)).co(expectedUserId, List.of(cart));
    }

    @Test
    @DisplayName("すでに借りている書籍を借りる場合、借りれなかった書籍として返される")
    void shouldReturnErrorWhenSomeBooksAreNotCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        BookModel expectedBook = new BookModel();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Book");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        List<BookModel> expectedCartBooks = List.of(expectedBook);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartService.co(USER_ID, List.of(cart))).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。"));

        verify(bookCartService, times(1)).co(expectedUserId, List.of(cart));
    }

    @Test
    @DisplayName("すでに借りている書籍を複数件借りる場合、複数件の借りれなかった書籍が返される")
    void shouldReturnErrorWhenMultipleBooksAreNotCheckedOut() throws Exception {
        String expectedUserId = "user1";

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
        List<BookModel> expectedCartBooks = List.of(expectedBook1, expectedBook2);

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

        when(cartSession.getCartList()).thenReturn(List.of(cart1, cart2));
        when(bookCartService.co(USER_ID, List.of(cart1, cart2))).thenReturn(List.of(book1, book2));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。"));

        verify(bookCartService, times(1)).co(expectedUserId, List.of(cart1, cart2));
    }
}
